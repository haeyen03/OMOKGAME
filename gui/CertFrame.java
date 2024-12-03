package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.DriverManager;

/** 관리자 이메일 인증 클래스입니다. */
public class CertFrame extends JFrame {
    private String userId; // 로그인한 사용자 ID를 저장

    public CertFrame(String userId) {
        this.userId = userId; // 전달받은 ID를 저장
        setTitle("인증 화면");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI 구성
        JLabel label = new JLabel("관리자 인증");
        JTextField verificationField = new JTextField(10);
        JButton verifyButton = new JButton("인증");

        JPanel panel = new JPanel();
        panel.add(new JLabel("인증번호:"));
        panel.add(verificationField);
        panel.add(verifyButton);

        add(label, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // 예제: 로그인 ID로 이메일 주소 가져오기
        String userEmail = fetchUserEmail(userId); // 사용자 이메일 가져오는 메서드 필요
        String verificationCode = generateVerificationCode();
        sendEmail(userEmail, "[오목게임] 인증번호", "인증번호는 다음과 같습니다: " + verificationCode + "\n10분 동안 유효합니다.");

        // 인증 버튼 이벤트
        verifyButton.addActionListener(e -> {
            String inputCode = verificationField.getText();
            if (inputCode.equals(verificationCode)) {
                JOptionPane.showMessageDialog(this, "인증 성공!");
                dispose();
                SwingUtilities.invokeLater(AdminFrame::new); // AdminFrame 실행
            } else {
                JOptionPane.showMessageDialog(this, "인증 실패!", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String fetchUserEmail(String userId) {
        String userEmail = null; // 이메일 값을 저장할 변수
        String query = "SELECT email FROM UserTable WHERE id = ?"; // 쿼리 작성

        try (Connection conn = getConnection(); // 데이터베이스 연결
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId); // 쿼리에 userId 바인딩
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userEmail = rs.getString("email"); // 이메일 값 가져오기
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // 오류 로그 출력
            JOptionPane.showMessageDialog(this, "사용자 이메일을 가져오는 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
        }

        if (userEmail == null) {
            JOptionPane.showMessageDialog(this, "사용자 이메일을 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }

        return userEmail; // 이메일 반환 (없으면 null)
    }
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/LoginDB?serverTimezone=UTC";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }


    private String generateVerificationCode() {
        // 인증번호 생성 로직
        return String.format("%06d", (int) (Math.random() * 1000000)); // 6자리 랜덤 숫자
    }

    private void sendEmail(String email, String subject, String message) {
        final String username = "omkgame.java@gmail.com"; // 발송자 이메일
        final String password = "fwtm cpik hjqw obyz";       // 발송자 이메일 비밀번호

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);

            System.out.println("이메일 발송 성공!");
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "이메일 발송 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

}
