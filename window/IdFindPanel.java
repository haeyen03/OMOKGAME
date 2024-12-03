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

import java.awt.Font; // Font 클래스를 import
import java.awt.FontFormatException;
import java.awt.Image; // Image 클래스 import
import javax.swing.ImageIcon;
import javax.swing.SwingConstants; // ImageIcon 클래스 import
import java.awt.Font; // 글꼴 설정
import java.io.File; // File 클래스를 import
import java.io.IOException; // IOException을 처리하기 위해 import

@SuppressWarnings("serial")
public class IdFindPanel extends JPanel {

    private JTextField inputName;
    private JTextField inputEmail;
    private JButton sendButton;
    private DataBase mySql;
    
    private static final String FONT_PATH = "C:\\Users\\dlgod\\workspace\\OMg3\\src\\image\\Galmuri11-Bold.ttf";


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

        // 레이아웃 설정 (절대 위치 지정)
        setLayout(null);

        // JLabel 생성 및 폰트 적용
        JLabel nameLabel = new JLabel("이름을 입력하세요:");
        JLabel emailLabel = new JLabel("이메일을 입력하세요:");
        applyFontToLabel(nameLabel);
        applyFontToLabel(emailLabel);

        // JTextField 및 JButton 폰트 설정
        applyFontToTextField(inputName);
        applyFontToTextField(inputEmail);
        applyFontToButton(sendButton);

        // 컴포넌트 배치 (x, y 좌표 지정)
        nameLabel.setBounds(38, 49, 162, 30);  // x=50, y=50, 너비=150, 높이=30
        inputName.setBounds(250, 50, 200, 30);  // x=200, y=50, 너비=200, 높이=30

        emailLabel.setBounds(38, 89, 192, 30);  // x=50, y=100, 너비=150, 높이=30
        inputEmail.setBounds(250, 90, 200, 30);  // x=200, y=100, 너비=200, 높이=30

        sendButton.setBounds(137, 215, 150, 40);  // x=150, y=150, 너비=150, 높이=40

        // 레이아웃에 컴포넌트 추가
        add(nameLabel);
        add(inputName);
        add(emailLabel);
        add(inputEmail);
        add(sendButton);

        // 버튼 클릭 이벤트 처리
        sendButton.addActionListener(e -> verifyAndSendEmail());
    }
       
    private void applyFontToLabel(JLabel label) {
        try {
            File fontFile = new File(FONT_PATH);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            Font labelFont = customFont.deriveFont(18f);
            label.setFont(labelFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "폰트를 로드하는 데 실패했습니다.");
        }
    }

    private void applyFontToTextField(JTextField textField) {
        try {
            File fontFile = new File(FONT_PATH);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            Font textFieldFont = customFont.deriveFont(16f);
            textField.setFont(textFieldFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "폰트를 로드하는 데 실패했습니다.");
        }
    }

    private void applyFontToButton(JButton button) {
        try {
            File fontFile = new File(FONT_PATH);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            Font buttonFont = customFont.deriveFont(18f);
            button.setFont(buttonFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "폰트를 로드하는 데 실패했습니다.");
        }
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
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null); // 화면 중앙에 위치
        frame.setVisible(true);
    }
}