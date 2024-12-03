package work;

import work.Expression;
import work.Weather;
import work.ZipcodePopup;
import work.SignupPanel;
import work.EventListener;
import work.Observer;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MainLogin {
    JPanel cardPanel;
    MainLogin lp;
    CardLayout card;

    public static void main(String[] args) {
        MainLogin cLogin = new MainLogin();
        cLogin.setFrame(cLogin);
    }

    public void setFrame(MainLogin cLogin) {
        JFrame jf = new JFrame();

        // 로그인 창
        LoginPanel cLoginPanel = new LoginPanel(cLogin);
        // 회원가입 창
        SignupPanel cSignupPanel = new SignupPanel(cLogin, jf);

        card = new CardLayout();
        cardPanel = new JPanel(card);

        // 로그인창
        cardPanel.add(cLoginPanel.mainPanel, "Login");
        // 회원가입창
        cardPanel.add(cSignupPanel.mainPanel, "Signup");

        jf.add(cardPanel);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(800, 1000);
        jf.setVisible(true);
    }

    /** 데이터베이스 연결 - Connection 사용을 위해선 Web 프로젝트로 작동시킬 것 */
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver"); // Oracle 드라이버 로드
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Oracle JDBC Driver not found.");
        }
        String url = "jdbc:oracle:thin:@localhost:1521:orcl"; // Oracle 데이터베이스 연결 URL
        String user = "C##TestUser"; // Oracle DB 사용자 이름
        String password = "1234"; // Oracle DB 비밀번호
        conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
}

class LoginPanel extends JPanel implements ActionListener {
    JPanel mainPanel;
    JTextField idTextField;
    JPasswordField passTextField;
    MainLogin lp;
    Font font = new Font("회원가입", Font.BOLD, 40);

    public LoginPanel(MainLogin lp) {
        this.lp = lp;

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1));

        JPanel centerPanel = new JPanel();
        JLabel loginLabel = new JLabel("로그인 화면");
        loginLabel.setFont(font);
        centerPanel.add(loginLabel);

        JPanel gridBagidInfo = new JPanel(new GridBagLayout());
        gridBagidInfo.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints c = new GridBagConstraints();
        JLabel idLabel = new JLabel(" 아이디 : ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        gridBagidInfo.add(idLabel, c);
        
        idTextField = new JTextField(15);
        c.insets = new Insets(0, 5, 0, 0);
        c.gridx = 1;
        c.gridy = 0;
        gridBagidInfo.add(idTextField, c);

        JLabel passLabel = new JLabel(" 비밀번호 : ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(20, 0, 0, 0);
        gridBagidInfo.add(passLabel, c);
        
        passTextField = new JPasswordField(15);
        c.insets = new Insets(20, 5, 0, 0);
        c.gridx = 1;
        c.gridy = 1;
        gridBagidInfo.add(passTextField, c);

        JPanel loginPanel = new JPanel();
        JButton loginButton = new JButton("로그인");
        loginPanel.add(loginButton);

        JPanel signupPanel = new JPanel();
        JButton signupButton = new JButton("회원가입");
        loginPanel.add(signupButton);

        mainPanel.add(centerPanel);
        mainPanel.add(gridBagidInfo);
        mainPanel.add(loginPanel);
        mainPanel.add(signupPanel);

        loginButton.addActionListener(this);
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lp.card.next(lp.cardPanel);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton jb = (JButton) e.getSource();
        switch (e.getActionCommand()) {
            case "로그인":
                String strID = idTextField.getText();
                String strPassword = new String(passTextField.getPassword());
                try {
                    String strSqlPassword = "SELECT PASSWORD FROM USERS WHERE USERNAME = ?";
                    Connection conn = lp.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(strSqlPassword);
                    pstmt.setString(1, strID);

                    ResultSet rset = pstmt.executeQuery();
                    if (rset.next()) {
                        String dbPassword = rset.getString(1);
                        if (strPassword.equals(dbPassword)) {
                            JOptionPane.showMessageDialog(this, "로그인되셨습니다.", "로그인 성공", 1);
                            Expression	expression = new Expression(strID, lp);
                        } else {
                            JOptionPane.showMessageDialog(this, "잘못된 정보입니다.", "로그인 실패", 1);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "해당 아이디가 존재하지 않습니다.", "로그인 실패", 1);
                    }
                    rset.close();
                    pstmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Login Failed (Catch)", "로그인 실패", 1);
                    System.out.println("SQLException: " + ex.getMessage());
                }
                break;
        }
    }
}
