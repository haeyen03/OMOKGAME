package gui;

import dataBase.DataBase;

import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Properties;

@SuppressWarnings("serial")
public class IdFindPanel extends JPanel {

    private JTextField inputName;
    private JTextField inputEmail;
    private JButton sendButton;
    private DataBase mySql;

    // SMTP 설정
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "omkgame.java@gmail.com"; // 발신자 이메일
    private static final String SMTP_PASSWORD = "fwtm cpik hjqw obyz";   // 발신자 앱 비밀번호

    public IdFindPanel(DataBase mySql) {
        this.mySql = mySql;

        // GUI 컴포넌트 초기화
        inputName = new JTextField(20);
        inputEmail = new JTextField(20);
        sendButton = new JButton("인증하기");

        // 레이아웃 설정 및 컴포넌트 추가
        setLayout(new FlowLayout());
        add(new JLabel("이름을 입력하세요:"));
        add(inputName);
        add(new JLabel("이메일을 입력하세요:"));
        add(inputEmail);
        add(sendButton);

        // 버튼 클릭 이벤트 리스너 추가
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyAndSendEmail();
            }
        });
    }

    /**
     * 이름과 이메일로 데이터베이스를 조회한 후, 아이디를 이메일로 전송
     */
    private void verifyAndSendEmail() {
        String name = inputName.getText().trim();
        String email = inputEmail.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 정보를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT id, account_status FROM UserTable WHERE name = ? AND email = ?";

        try (Connection conn = mySql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String id = rs.getString("id");
                boolean accountStatus = rs.getBoolean("account_status"); // account_status 컬럼 확인

                if (!accountStatus) {
                    // account_status가 FALSE일 경우
                    JOptionPane.showMessageDialog(this, "귀하의 이메일은 비활성화되었습니다.", "계정 비활성화", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // account_status가 TRUE인 경우 이메일 전송
                sendEmail(email, id);
            } else {
                JOptionPane.showMessageDialog(this, "입력한 정보와 일치하는 사용자를 찾을 수 없습니다.", "확인 실패", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * 이메일로 아이디 전송
     */
    private void sendEmail(String toEmail, String userId) {
        String subject = "아이디 찾기 결과";
        String content = "안녕하세요, 요청하신 아이디는 다음과 같습니다: " + userId;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            JOptionPane.showMessageDialog(this, "아이디가 이메일로 전송되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "이메일 전송 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 새 창으로 열리는 메서드
     */
    public static void showInFrame(DataBase mySql) {
        JFrame frame = new JFrame("아이디 찾기");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new IdFindPanel(mySql));
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null); // 화면 중앙에 위치
        frame.setVisible(true);
    }
}
