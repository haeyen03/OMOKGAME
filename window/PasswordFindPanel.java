package gui;

import dataBase.DataBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.awt.Font;
import java.awt.FontFormatException;

@SuppressWarnings("serial")
public class PasswordFindPanel extends JPanel {

    private JTextField inputName;
    private JTextField inputUsername;
    private JTextField inputBirthDate;
    private JTextField inputPhone;
    private JTextField inputEmail;
    private JButton findButton;
    private DataBase mySql;

    // SMTP 설정
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "omkgame.java@gmail.com"; // 발신자 이메일
    private static final String SMTP_PASSWORD = "fwtm cpik hjqw obyz";   // 발신자 앱 비밀번호

    public PasswordFindPanel(DataBase mySql) {
        this.mySql = mySql;
        this.setBackground(new Color(44, 44, 44)); // 어두운 회색

        // GUI 컴포넌트 초기화
        inputName = new JTextField(20);
        inputUsername = new JTextField(20);
        inputBirthDate = new JTextField(20); // "YYYY-MM-DD" 형식
        inputPhone = new JTextField(20);
        inputEmail = new JTextField(20);
        findButton = new JButton("비밀번호 찾기");
        findButton.setEnabled(false);
        
        Font customFont = loadCustomFont("C:\\Users\\dlgod\\workspace\\OMg3\\src\\image\\Galmuri11-Bold.ttf", 16f);

        
        inputName.setBackground(new Color(44, 44, 44)); // 텍스트 필드 배경 어두운 색
        inputUsername.setBackground(new Color(44, 44, 44)); 
        inputBirthDate.setBackground(new Color(44, 44, 44)); 
        inputPhone.setBackground(new Color(44, 44, 44)); 
        inputEmail.setBackground(new Color(44, 44, 44)); 
        
     // 버튼 스타일: 네온 핑크 배경에 검은색 텍스트
        findButton.setBackground(new Color(255, 20, 147)); // 네온 핑크
        findButton.setForeground(Color.BLACK);
        findButton.setBorderPainted(false); // 버튼 외곽선 없애기
        
        findButton.setFont(customFont);
        inputName.setFont(customFont);
        inputUsername.setFont(customFont);
        inputBirthDate.setFont(customFont);
        inputPhone.setFont(customFont);
        inputEmail.setFont(customFont);


        // 레이아웃 설정 및 컴포넌트 추가
        setLayout(new GridLayout(6, 2, 5, 5));
        JLabel labelName = new JLabel("이름:");
        labelName.setForeground(new Color(135, 206, 235)); // 분홍색 텍스트
        add(labelName);
        add(inputName);

        JLabel labelUsername = new JLabel("아이디:");
        labelUsername.setForeground(new Color(135, 206, 235)); // 분홍색 텍스트
        add(labelUsername);
        add(inputUsername);

        JLabel labelBirthDate = new JLabel("생년월일 (YYYY-MM-DD):");
        labelBirthDate.setForeground(new Color(135, 206, 235)); // 분홍색 텍스트
        add(labelBirthDate);
        add(inputBirthDate);

        JLabel labelPhone = new JLabel("전화번호:");
        labelPhone.setForeground(new Color(135, 206, 235)); // 분홍색 텍스트
        add(labelPhone);
        add(inputPhone);

        JLabel labelEmail = new JLabel("이메일:");
        labelEmail.setForeground(new Color(135, 206, 235)); // 분홍색 텍스트
        add(labelEmail);
        add(inputEmail);

        add(findButton);
        
        findButton.setFont(customFont);
        labelName.setFont(customFont);
        labelUsername.setFont(customFont);
        labelBirthDate.setFont(customFont);
        labelPhone.setFont(customFont);
        labelEmail.setFont(customFont);

        // 버튼 클릭 이벤트 리스너 추가
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findPassword();
            }
        });
    }
    
    private Font loadCustomFont(String fontPath, float size) {
        try {
            // 폰트 파일 경로 설정
            File fontFile = new File(fontPath);

            // TTF 폰트 로드
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile); // 기본 폰트 로드

            // 폰트 크기 설정
            return customFont.deriveFont(size); 

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "폰트를 로드하는 데 실패했습니다.");
            return new Font("Arial", Font.PLAIN, 16); // 기본 폰트 반환
        }
    }

    /**
     * 이름, 아이디, 생년월일, 전화번호, 이메일을 확인 후 비밀번호를 이메일로 전송
     */
    private void findPassword() {
        String name = inputName.getText().trim();
        String username = inputUsername.getText().trim();
        String birthDate = inputBirthDate.getText().trim();
        String phone = inputPhone.getText().trim();
        String email = inputEmail.getText().trim();

        if (name.isEmpty() || username.isEmpty() || birthDate.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 정보를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT password FROM UserTable WHERE name = ? AND id = ? AND birth = ? AND phone = ? AND email = ?";

        try (Connection conn = mySql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, username);
            pstmt.setString(3, birthDate);
            pstmt.setString(4, phone);
            pstmt.setString(5, email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String password = rs.getString("password");
                sendEmail(email, password);
                JOptionPane.showMessageDialog(this, "비밀번호가 이메일로 전송되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "입력한 정보와 일치하는 사용자를 찾을 수 없습니다.", "확인 실패", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 이메일로 비밀번호를 전송하는 메서드
     */
    private void sendEmail(String toEmail, String password) {
        String subject = "[오목게임]비밀번호 찾기 결과";
        String content = "안녕하세요,\n\n요청하신 비밀번호는 다음과 같습니다: " + password + "\n\n항상 이용해 주셔서 감사합니다.";

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
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "이메일 전송 오류: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * 새 창으로 열리는 메서드
     */
    public static void showInFrame(DataBase mySql) {
    	JFrame frame = new JFrame("비밀번호 찾기");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new PasswordFindPanel(mySql));
        
        frame.setSize(450, 300);
        
        // 화면의 좌측 상단 (100, 100) 좌표로 배치
        frame.setLocation(100, 100); // x, y 좌표를 원하는 위치로 설정
        
        frame.setVisible(true);
        frame.setUndecorated(false); 
    }
}
