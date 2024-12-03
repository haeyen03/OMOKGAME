package work;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Expression extends JFrame {
    private JLabel weatherLabel;
    private Weather Weather;
    private JPanel chatPanel;
    private JTextField chatInput;
    private JLabel opponentFaceLabel;
    private JLabel myFaceLabel;
    private JButton drawButton;
    private JLabel winRateLabel; // 승률 표시 레이블

    private MainLogin lp;
    private String userId;

    public Expression(String userId, MainLogin lp) {
        Weather = new Weather(userId);
        this.userId = userId;
        this.lp = lp;

        setTitle("오목 게임");
        setSize(1000, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel();
        gamePanel.setPreferredSize(new Dimension(600, 900));
        gamePanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        drawButton = new JButton("무승부");
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDrawConfirmationPopup();
            }
        });
        gamePanel.add(drawButton, BorderLayout.SOUTH);

        JPanel playerPanel = new JPanel();
        playerPanel.setPreferredSize(new Dimension(400, 900));
        playerPanel.setLayout(new BorderLayout());

        JPanel playerFacePanel = new JPanel();
        playerFacePanel.setLayout(new GridLayout(1, 2));
        playerFacePanel.setBorder(BorderFactory.createTitledBorder("플레이어 얼굴"));

        opponentFaceLabel = new JLabel();
        opponentFaceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        opponentFaceLabel.setPreferredSize(new Dimension(100, 100));
        playerFacePanel.add(opponentFaceLabel);

        myFaceLabel = new JLabel();
        myFaceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        myFaceLabel.setPreferredSize(new Dimension(100, 100));
        playerFacePanel.add(myFaceLabel);

        setMyFace("C:\\angang\\java\\사람\\나르시스.png");

        playerPanel.add(playerFacePanel, BorderLayout.NORTH);

        // 채팅 패널 설정 (BoxLayout 사용)
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBorder(BorderFactory.createTitledBorder("채팅"));

        // 승률 레이블 생성 및 chatPanel 상단에 추가
        winRateLabel = new JLabel();
        updateWinRate(userId);
        chatPanel.add(winRateLabel); // BorderLayout 없이 chatPanel에 직접 추가

        JPanel chatAreaPanel = new JPanel();
        chatAreaPanel.setLayout(new BoxLayout(chatAreaPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chatAreaPanel);
        scrollPane.setPreferredSize(new Dimension(250, 600));
        chatPanel.add(scrollPane); // BorderLayout 없이 chatPanel에 직접 추가

        chatInput = new JTextField(20);
        chatInput.setPreferredSize(new Dimension(250, 30));
        chatPanel.add(chatInput);

        JButton sendButton = new JButton("전송");
        chatPanel.add(sendButton);

        sendButton.addActionListener(e -> {
            String message = chatInput.getText();
            if (!message.trim().isEmpty()) {
                JLabel messageLabel = new JLabel(message);
                chatAreaPanel.add(messageLabel);
                chatAreaPanel.revalidate();
                chatAreaPanel.repaint();

                saveMessageToDatabase(message);

                chatInput.setText("");
            }
        });

        JPanel emojiPanel = new JPanel();
        emojiPanel.setLayout(new FlowLayout());

        String[] emojiFiles = {
            "src/work/홍홍.png", "src/work/냉랭.png",
            "src/work/씨익.png", "src/work/홍홍.png",
            "src/work/와우.png", "src/work/이것뭐에요.png",
            "src/work/초진지.png", "src/work/하트쪽.png"
        };

        for (String fileName : emojiFiles) {
            ImageIcon icon = new ImageIcon(fileName);
            JButton emojiButton = new JButton();
            emojiButton.setPreferredSize(new Dimension(30, 30));

            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            emojiButton.setIcon(new ImageIcon(scaledImg));

            emojiButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int targetSize = 50;
                    int originalWidth = img.getWidth(null);
                    int originalHeight = img.getHeight(null);
                    double aspectRatio = (double) originalWidth / (double) originalHeight;

                    int newWidth, newHeight;
                    if (aspectRatio > 1) {
                        newWidth = targetSize;
                        newHeight = (int) (targetSize / aspectRatio);
                    } else {
                        newHeight = targetSize;
                        newWidth = (int) (targetSize * aspectRatio);
                    }

                    Image resizedImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    JLabel emojiLabel = new JLabel(new ImageIcon(resizedImg));
                    emojiLabel.setPreferredSize(new Dimension(50, 50));
                    emojiLabel.setMaximumSize(new Dimension(50, 50));
                    chatAreaPanel.add(emojiLabel);
                    chatAreaPanel.revalidate();
                    chatAreaPanel.repaint();
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                }
            });

            emojiPanel.add(emojiButton);
        }

        chatPanel.add(emojiPanel); // chatPanel에 직접 추가

        playerPanel.add(chatPanel, BorderLayout.CENTER);

        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new BorderLayout());
        weatherPanel.setBorder(BorderFactory.createTitledBorder("날씨 정보"));
        weatherLabel = new JLabel("날씨 정보: " + Weather.getCurrentWeatherInfo());
        weatherPanel.add(weatherLabel, BorderLayout.CENTER);

        playerPanel.add(weatherPanel, BorderLayout.SOUTH);

        mainPanel.add(playerPanel, BorderLayout.EAST);

        add(mainPanel);
        setVisible(true);
    }

    private void saveMessageToDatabase(String message) {
        try (Connection conn = lp.getConnection()) {

            String name = "";
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT NAME FROM USERS WHERE USERNAME = ?")) {
                pstmt.setString(1, this.userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        name = rs.getString("NAME");
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO CHATLOGTABLE (GAME_SESSION_ID, ALIAS, MESSAGE, TIMESTAMP) VALUES (?, ?, ?, SYSDATE)")) {
                pstmt.setInt(1, 1);
                pstmt.setString(2, name);
                pstmt.setString(3, message);
                pstmt.executeUpdate();
            }

        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
    }

    private void setMyFace(String filePath) {
        ImageIcon myFaceIcon = new ImageIcon(filePath);
        myFaceLabel.setIcon(myFaceIcon);
    }

    public void setPlayerFaces(ImageIcon opponentFace) {
        opponentFaceLabel.setIcon(opponentFace);
    }

    // 무승부 확인 팝업 표시
    private void showDrawConfirmationPopup() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "상대방이 무승부를 요청했습니다. 찬성하시겠습니까?",
                "무승부 요청",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "무승부처리되었습니다!");
            // 게임 종료 로직 추가 (예: 게임 종료 플래그 설정, 게임 화면 닫기)
            System.exit(0); // 임시로 게임 종료
        } else if (result == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(this, "상대방이 반대를 눌렀습니다. 게임을 재개합니다.");
            // 게임 재개 로직 추가 (필요한 경우)
        }
    }

    // 승률 업데이트 및 표시
    private void updateWinRate(String userId) {
        try (Connection conn = lp.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT NAME, WIN_COUNT, LOSE_COUNT FROM USERS WHERE USERNAME = ?")) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("NAME");
                    int winCount = rs.getInt("WIN_COUNT");
                    int loseCount = rs.getInt("LOSE_COUNT");
                    double winRate = (winCount + loseCount > 0) ? (double) winCount / (winCount + loseCount) * 100 : 0;
                    String winRateText = String.format("%s님 %d승/%d패/승률: %.2f%%", name, winCount, loseCount, winRate);
                    winRateLabel.setText(winRateText);

                    // UI 업데이트
                    winRateLabel.revalidate();
                    winRateLabel.repaint();
                }
            }
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
    }
   }
