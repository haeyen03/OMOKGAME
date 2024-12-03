package dataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.sql.*;
import java.awt.Image;
import javax.swing.*;

import gui.ImageButton;

import javax.swing.*; // JButton, ImageIcon 등을 포함
import java.awt.*; // Image 및 기타 그래픽 관련 클래스


public class DataBase 
{
	/// 데이터베이스 정보를 모두 가지고 있을 것. get,set도 구현해둘 것 */
	String userId = "tkfkdekdls";
	   
	/** 데이터베이스 연결 - Connection 사용을 위해선 Web 프로젝트로 작동시킬 것*/
	public synchronized Connection getConnection() throws SQLException {
		Connection conn = null;

		/** JDBC 연동을 먼저 해주세요. (프로젝트 > Properties > Java Build Path > Libraries > jar 추가 */
		// (~ 호스트/데이터베이스명?serverTimezone=UTC", 관리자, 비번)
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/LoginDB?serverTimezone=UTC"
				, "root", "1234");

		return conn;
	}
	
    public synchronized void   saveMessageToDatabase(String message)
    {
       try
       {
           Connection conn = getConnection();
           String Query = String.format("select alias from UserTable where id = '%s'", this.userId);
           
            // PreparedStatement 생성
           PreparedStatement pstmt = conn.prepareStatement(Query);
            
            // 쿼리 실행
            ResultSet rs = pstmt.executeQuery();
            String alias = "";
            if(rs.next())
            {
               alias = rs.getString("alias");
            }
            
            Query = "insert into ChatLogTable(game_session_id, alias, message, timestamp) values (?,?,?,?)";

            pstmt = conn.prepareStatement(Query);
            
            // 현재 시간 가져오기
            LocalDateTime now = LocalDateTime.now();

            // 원하는 포맷으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = now.format(formatter);
            
            // 데이터베이스에 저장되는 순서
            int iIndex = 0;
            pstmt.setString(++iIndex, "1"); // 플레이 중인 게임 세션 번호
            pstmt.setString(++iIndex, alias);
            pstmt.setString(++iIndex, message);
            pstmt.setString(++iIndex, formattedTime);
  
            int Result = pstmt.executeUpdate();
                  
            pstmt.close();
            conn.close();
                  
      } catch (SQLException ex) {
      
         System.out.println("SQLException" + ex);
      }

    }
    
    // 순위 가져오기 메서드
    public String getUserRank(String userId) {
        String rank = "언랭"; // 기본값
        String query = 
                "SELECT ranking, win_count FROM (" +
                "    SELECT id, win_count, RANK() OVER (ORDER BY win_count DESC) AS ranking " +
                "    FROM usertable" +
                ") ranked_users WHERE id = ?";
        try (Connection conn = getConnection(); // DB 연결
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // 사용자 ID 설정
            stmt.setString(1, userId);

            // 쿼리 실행
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int winCount = rs.getInt("win_count");
                    if (winCount > 0) {
                        rank = rs.getInt("ranking") + "위"; // 순위를 문자열로 반환
                    } else {
                        rank = "언랭"; // win_count가 0일 경우
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "오류 발생: " + e.getMessage(); // 오류 메시지 반환
        }

        return rank;
    }


    
    public String getWinLoseRate(String id) {
        try (Connection conn = getConnection()) { // try-with-resources로 연결 자동 닫기
            String query = "SELECT win_count, lose_count FROM usertable WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int winCount = rs.getInt("win_count");
                int loseCount = rs.getInt("lose_count");
                int totalGames = winCount + loseCount;
                String winRate = (totalGames > 0)
                        ? String.format("%.2f%%", ((double) winCount / totalGames) * 100)
                        : "--%";
                return String.format("승: %d | 패: %d | 승률: %s", winCount, loseCount, winRate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "승: 0 | 패: 0 | 승률: --%";
    }
 // 사용자 alias 가져오기 메서드
    public String getUserAlias(String id) {
        String alias = null;
        try (Connection conn = getConnection()) { // 데이터베이스 연결
            String query = "SELECT alias FROM UserTable WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                alias = rs.getString("alias"); // alias 값 가져오기
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alias != null ? alias : "Unknown"; // alias가 없으면 기본값 반환
    }
    
    /** @feature - DB로부터 이미지 로드 */
    public JLabel getImageIconByName(String id) {
        final int buttonWidth = 80;
        final int buttonHeight = 80;

        String query = "SELECT image FROM UserTable WHERE id = ?";
        byte[] imageData = null;

        try (Connection connection = getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, id); // name 값을 설정
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // 데이터베이스에서 이미지 데이터를 가져옴
                imageData = rs.getBytes("image");
                if (imageData == null) {
                    System.out.println("No image found for the given name.");
                }
            } else {
                System.out.println("No user found with the given name.");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 이미지가 null이 아닌 경우에만 이미지 생성
        if (imageData != null) {
            ImageIcon imageIcon = new ImageIcon(imageData);
            JLabel imageLabel = new JLabel(imageIcon);
            return imageLabel; // JLabel에 이미지 설정 후 반환
        } else {
            // 이미지가 없을 경우 텍스트를 반환
            JLabel noImageLabel = new JLabel("No Image");
            return noImageLabel;
        }
    }
}
