package gui.Dainn;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.Dimension;

import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.Color;

import gui.LoginPanel;
import dataBase.DataBase;
import gui.Dainn.EventListener;
import gui.Dainn.ImageCrop;
import java.awt.CardLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
//회원가입창
/* 클래스는 다중 상속 X, 인터페이스는 다중 상속 가능 */
public class SignupPanel extends JFrame implements EventListener
{
   @Override
   public void onEvent(Observer obs)
   {
      if(null == obs)
         return;
      
      String fullAddress = ((ZipcodePopup)(obs)).Get_Address();
      String[] splitAddress = fullAddress.split(":");
      zipcodeTf.setText(splitAddress[0]);
      addressTf.setText(splitAddress[1]);
      int i = 0;
   }
   
   public JPanel mainPanel;
   JPanel subPanel;
   JButton SignupButton;
   JButton CancelButton;
   JButton AgainButton;
   Font font = new Font("회원가입", Font.BOLD, 40);

   String birth = "", year = "", month = "", day = "";
   String email = "", domain = "";
   String address;
   String id = "", password = "", passRe = "", name = "", sex = "", phone = "";
   boolean   bCheckDuplicate = false;
   
   LoginPanel lp;
   DataBase   mySql;

   JLabel imageLabel = new JLabel();
   JLabel nameLabel = new JLabel("이름 : ");
   JLabel idLabel = new JLabel("아이디 : ");
   JLabel aliasLabel = new JLabel("닉네임 : ");
   JLabel passLabel = new JLabel("패스워드 : ");
   JLabel passReLabel = new JLabel("패스워드 확인 : ");
   JLabel strengthLabel = new JLabel("안전도: ");
   JLabel birthLabel = new JLabel("나이 : ");
   JLabel phoneLabel = new JLabel("전화번호 : ");
   JLabel sexLabel = new JLabel("성별 : ");
   JLabel emailLabel = new JLabel("이메일 : ");
   JLabel zipcodeLabel = new JLabel("우편번호: ");
   JLabel addressLabel = new JLabel("주소: ");
   JLabel detailedAddressLabel = new JLabel("상세주소: ");
   
   
   
   

   JTextField nameTf = new JTextField(15);
   JTextField idTf = new JTextField(15);
   JTextField aliasTf = new JTextField(15);
   JPasswordField passTf = new JPasswordField(15);
   JPasswordField passReTf = new JPasswordField(15);
   JTextField zipcodeTf = new JTextField(100);
   JTextField addressTf = new JTextField(100);
   JTextField detailedAddressTf = new JTextField(100);
   JTextField emailTf = new JTextField(30);
   JTextField domainTf = new JTextField(30);
   JTextField phoneMiddleTf = new JTextField(4);
   JTextField phoneEndTf = new JTextField(4);
   
   final int PROFILE_SIZE = 120;
   
   private final ImageIcon Profile = new ImageIcon("src/image/DefaultProfile.png");
   byte[] imageData = imageToByteArray("src/image/DefaultProfile.png");
   
   ZipcodePopup  ZipcodePopup = null;
   
   // 생성자 //
   public SignupPanel(LoginPanel lp, DataBase mySql) 
   {
      UIManager.put("Button.background", new Color(137, 119, 173)); // 모든 버튼의 배경색
      UIManager.put("Button.foreground", Color.WHITE);            // 모든 버튼의 텍스트 색상

      this.lp = lp;
      this.mySql = mySql;
      
      setTitle("회원가입");
      setSize(800, 800);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setLocationRelativeTo(null);
      
      setGlobalFont("src/image/Galmuri11-Bold.ttf", 11);
      
      JLabel backgroundLabel = new JLabel(new ImageIcon("src/image/Signup.png"));
      backgroundLabel.setLayout(new BorderLayout()); // 컴포넌트를 배치할 수 있도록 레이아웃 설정
      
   // 폰트 로드 및 설정
      try {
           Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/image/Galmuri11-Bold.ttf"));
           customFont = customFont.deriveFont(Font.PLAIN, 16); // 원하는 크기와 스타일 설정

           // JLabel에 개별적으로 폰트를 적용하고 텍스트 색상을 흰색으로 설정
           imageLabel.setFont(customFont);
           imageLabel.setForeground(Color.WHITE);

           nameLabel.setFont(customFont);
           nameLabel.setForeground(Color.WHITE);

           idLabel.setFont(customFont);
           idLabel.setForeground(Color.WHITE);

           aliasLabel.setFont(customFont);
           aliasLabel.setForeground(Color.WHITE);

           passLabel.setFont(customFont);
           passLabel.setForeground(Color.WHITE);

           passReLabel.setFont(customFont);
           passReLabel.setForeground(Color.WHITE);

           strengthLabel.setFont(customFont);
           strengthLabel.setForeground(Color.WHITE);

           birthLabel.setFont(customFont);
           birthLabel.setForeground(Color.WHITE);

           phoneLabel.setFont(customFont);
           phoneLabel.setForeground(Color.WHITE);

           sexLabel.setFont(customFont);
           sexLabel.setForeground(Color.WHITE);

           emailLabel.setFont(customFont);
           emailLabel.setForeground(Color.WHITE);

           zipcodeLabel.setFont(customFont);
           zipcodeLabel.setForeground(Color.WHITE);

           addressLabel.setFont(customFont);
           addressLabel.setForeground(Color.WHITE);

           detailedAddressLabel.setFont(customFont);
           detailedAddressLabel.setForeground(Color.WHITE);

           System.out.println("폰트와 텍스트 색상이 성공적으로 적용되었습니다.");
       } catch (FontFormatException | IOException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "폰트를 로드할 수 없습니다. 기본 폰트를 사용합니다.");
       }


      

      setLayout(new BorderLayout());
      setVisible(true);
      
      BatchGui();
      
      add(mainPanel, BorderLayout.CENTER);
      
      
      setContentPane(backgroundLabel); // 배경 이미지를 프레임의 컨텐츠로 설정
      backgroundLabel.add(mainPanel); // 메인 패널을 배경 위에 추가

      setVisible(true);
   }
   
   private void BatchGui()
   {
      /* 콤보박스 */
         JComboBox<String> yearComboBox = new JComboBox<String>(
               new String[] { "1990", "1991", "1992", "1993", "1994", "1995", "1996", "1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006" });
         JComboBox<String> monthComboBox = new JComboBox<String>(
               new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" });
         JComboBox<String> dayComboBox = new JComboBox<String>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
               "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
               "28", "29", "30", "31" });
         JComboBox<String> domainComboBox = new JComboBox<String>(new String[] { "직접입력", "naver.com", "hanmail.net", "gmail.co.kr", "nate.com", "anyang.ac.kr"});
         JComboBox<String> phoneNumberComboBox = new JComboBox<String>(new String[] { "010", "02", "031"});
         
         /* 라디오버튼 */
         JRadioButton menButton = new JRadioButton("남자");
         JRadioButton girlButton = new JRadioButton("여자");
         ButtonGroup sexGroup = new ButtonGroup();
         sexGroup.add(menButton);
         sexGroup.add(girlButton);
         
         /* 버튼 */
         JButton idCheckButton = new JButton("중복확인");
         JButton aliasCheckButton = new JButton("중복확인");
         JButton imageButton = new JButton("사진등록");
         JButton zipcodeButton = new JButton("우편번호 검색");
         
         GridBagConstraints MainGridCons = new GridBagConstraints();
         MainGridCons.fill = GridBagConstraints.BOTH; 
         
         /* 레이아웃 패널 */
         mainPanel = new JPanel(new BorderLayout());
         mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
         mainPanel.setOpaque(false); // 배경 투명하게 설정
         
         
         
         
         subPanel = new JPanel();   
         subPanel.setLayout(new GridBagLayout());
         subPanel.setOpaque(false); // 배경 투명하게 설정

         subPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
         
         /* 이미지 */
         // 1. 원본 이미지 불러와서 조정 기능하기!
         // 2. 라디오버튼 클릭하면 그냥 이미지에 맞춰서 사이즈 고정시켜버리기!
         Image DumpImage = Profile.getImage().getScaledInstance(PROFILE_SIZE,  PROFILE_SIZE, Image.SCALE_SMOOTH); // 크기 조정
         imageLabel = new JLabel(new ImageIcon(DumpImage)); // 크기 조정된 이미지로 JLabel 생성         
         
          JPanel imagePanel = new JPanel();
          imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS)); 
          imagePanel.setPreferredSize(new Dimension(PROFILE_SIZE, PROFILE_SIZE)); // 이미지 패널 크기 설정
          imagePanel.setOpaque(false); // 배경 투명하게 설정
          
          JLabel Choose = new JLabel("이미지 선택");
          Choose.setAlignmentX(Component.CENTER_ALIGNMENT);
          imagePanel.add(Choose);
          imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
          imagePanel.add(imageLabel);
          imageButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬
          imagePanel.add(imageButton);
          
         MainGridCons.anchor = GridBagConstraints.NORTH; // 상단에 고정
         MainGridCons.insets = new Insets(20, 20, 10, 10); // 오른쪽에 여백 추가
         
          MainGridCons.gridx = 0;
          MainGridCons.gridy = 0;
          imagePanel.setPreferredSize(new Dimension(200, PROFILE_SIZE)); // (가로폭, 세로폭)
          mainPanel.add(imagePanel, BorderLayout.WEST);

         //++c.gridy;
         //subPanel.add(imageButton, c);
          
          GridBagConstraints SubGridCons = new GridBagConstraints();
          SubGridCons.fill = GridBagConstraints.HORIZONTAL; // 가로로 꽉 채우기 (꽉 채우지 않으면 최소값만 표시해서 JTextField가 쪼그라듬)
          SubGridCons.anchor = GridBagConstraints.WEST; // 왼쪽 정렬
          SubGridCons.insets = new Insets(20, 20, 10, 10); // 각 요소 사이 간격 설정
         
          
         /* 이름 */
          SubGridCons.gridy = 1;
          SubGridCons.gridx = 1;
          SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(nameLabel, SubGridCons);

         JPanel namePanel = new JPanel(); // 버튼을 담을 패널
         namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS)); 
         
         namePanel.add(nameTf, SubGridCons); 

         SubGridCons.gridx = 2;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(namePanel, SubGridCons);
         
         /* 아이디 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(idLabel, SubGridCons);
         
         JPanel idPanel = new JPanel(); // 버튼을 담을 패널
         idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.X_AXIS)); 
         
         idPanel.add(idTf, SubGridCons); 
         idPanel.add(idCheckButton, SubGridCons); 

         SubGridCons.weightx = 1.0; // 폭을 넓힘
         SubGridCons.gridx = 2;
         subPanel.add(idPanel, SubGridCons);
         
         /* 닉네임 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(aliasLabel, SubGridCons);

         JPanel aliasPanel = new JPanel(); // 버튼을 담을 패널
         aliasPanel.setLayout(new BoxLayout(aliasPanel, BoxLayout.X_AXIS)); 
         
         aliasPanel.add(aliasTf, SubGridCons); 
         aliasPanel.add(aliasCheckButton, SubGridCons); 

         SubGridCons.gridx = 2;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(aliasPanel, SubGridCons);
         
         /* 비밀번호 */
         SubGridCons.gridx = 1;
            ++SubGridCons.gridy;
            SubGridCons.weightx = 0.0; // 폭을 넓힘
            subPanel.add(passLabel, SubGridCons);
     
            // 비밀번호 입력 패널 생성
            JPanel passWithTogglePanel = new JPanel();
            passWithTogglePanel.setLayout(new BoxLayout(passWithTogglePanel, BoxLayout.X_AXIS));

            // 패널에 색상을 적용하기 위한 빈 패널 (안전도 표시)
            JPanel strengthPanel = new JPanel();
            strengthPanel.setPreferredSize(new Dimension(100, 30));
            strengthPanel.setBackground(Color.LIGHT_GRAY); // 패널에 기본 색상 설정

            /** @feature - 비밀번호 일시적으로 보이기 기능 */
            JButton toggleVisibilityButton = new JButton("👁");
            // 버튼의 마우스 리스너 추가
            toggleVisibilityButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    // 버튼을 누르고 있을 때 비밀번호를 평문으로 보이게 설정
                    passTf.setEchoChar((char) 0); // 평문 노출
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    // 버튼에서 손을 떼면 다시 비밀번호를 가려진 상태로 복원
                    passTf.setEchoChar('*'); // 비밀번호 숨김
                }
            });

            // 패널에 각 컴포넌트 추가
            passWithTogglePanel.add(passTf);               // 비밀번호 필드
            passWithTogglePanel.add(strengthPanel);        // 안전도 색상 패널
            passWithTogglePanel.add(toggleVisibilityButton); // 보기 버튼

            // 기존 패널에 passWithTogglePanel 추가
            SubGridCons.gridx = 2;
            SubGridCons.weightx = 1.0; // 폭을 넓힘
            subPanel.add(passWithTogglePanel, SubGridCons);

            /** @feature - 비밀번호 안전 정책 */
            JLabel passwordConditionLabel = new JLabel("비밀번호는 8~20자, 문자, 숫자, 특수문자를 포함해야 합니다.");
            passwordConditionLabel.setForeground(Color.GRAY); // 기본 텍스트 색상 설정
            passwordConditionLabel.setPreferredSize(new Dimension(300, 20));

            // 기존 코드: 비밀번호 패널 생성 직후
            SubGridCons.gridx = 1;
            ++SubGridCons.gridy;
            SubGridCons.gridwidth = 2; // 라벨이 두 칸에 걸쳐 표시되도록 설정
            subPanel.add(passwordConditionLabel, SubGridCons);
            
            // 기존 비밀번호 DocumentListener 수정
            passTf.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validatePassword();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    validatePassword();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    validatePassword();
                }

                private void validatePassword() {
                    String password = new String(passTf.getPassword());
                    StringBuilder warnings = new StringBuilder();

                    boolean isValid = true;

                    // 조건 검사
                    if (password.length() < 8 || password.length() > 20) {
                        warnings.append(" - 8~20자 길이 필요\n");
                        isValid = false;
                    }
                    if (!password.matches(".*[a-zA-Z].*")) {
                        warnings.append(" - 최소 1개의 문자 포함\n");
                        isValid = false;
                    }
                    if (!password.matches(".*\\d.*")) {
                        warnings.append(" - 최소 1개의 숫자 포함\n");
                        isValid = false;
                    }
                    if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                        warnings.append(" - 최소 1개의 특수문자 포함\n");
                        isValid = false;
                    }

                    // 조건에 따라 라벨 업데이트
                    if (isValid) {
                        passwordConditionLabel.setText("✅ 비밀번호 조건을 충족합니다.");
                        passwordConditionLabel.setForeground(Color.GREEN);
                    } else {
                        passwordConditionLabel.setText("<html⚠️ 비밀번호 조건 미충족:<br>" + warnings.toString() + "</html>");
                        passwordConditionLabel.setForeground(Color.RED);
                    }
                }
            });     
            
            //++SubGridCons.gridx;
            //subPanel.add(new JLabel("특수문자 + 8자"), SubGridCons); //보안설정
            
            //c.gridx = 0;
            ///++c.gridy;
            //subPanel.add(checkPassSaftey(passTf), c); // 안전도 검사
         /* 비밀번호 확인 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(passReLabel, SubGridCons);

         ++SubGridCons.gridx;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(passReTf, SubGridCons); // password 재확인

         /* 나이 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(birthLabel, SubGridCons);

         JPanel birthPanel = new JPanel(); // 버튼을 담을 패널
         birthPanel.setLayout(new BoxLayout(birthPanel, BoxLayout.X_AXIS)); 
         
         birthPanel.add(yearComboBox, SubGridCons);
         birthPanel.add(monthComboBox, SubGridCons);
         birthPanel.add(dayComboBox, SubGridCons);
         
         SubGridCons.gridx = 2;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(birthPanel, SubGridCons);

         /* 전화번호 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(phoneLabel, SubGridCons);

         JPanel phonePanel = new JPanel(); // 버튼을 담을 패널
         phonePanel.setLayout(new BoxLayout(phonePanel, BoxLayout.X_AXIS)); 
         
         phoneNumberComboBox.setPreferredSize(new Dimension(200, 30)); // 원하는 크기로 설정
         phonePanel.add(phoneNumberComboBox); 
         phoneMiddleTf.setPreferredSize(new Dimension(200, 30)); // 원하는 크기로 설정
         phonePanel.add(phoneMiddleTf);
         phoneEndTf.setPreferredSize(new Dimension(200, 30)); // 원하는 크기로 설정
         phonePanel.add(phoneEndTf);
         
         SubGridCons.gridx = 2;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(phonePanel, SubGridCons);
         
         /* 성별 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(sexLabel, SubGridCons);

         JPanel sexPanel = new JPanel(); // 버튼을 담을 패널
         sexPanel.setLayout(new BoxLayout(sexPanel, BoxLayout.X_AXIS)); 
         
         sexPanel.add(menButton); 
         sexPanel.add(girlButton);
         
         SubGridCons.gridx = 2;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(sexPanel, SubGridCons);

         /* 이메일 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(emailLabel, SubGridCons);
               
         // BoxLayout은 X_AXIX 방향으로 채워지면서 창 크기 상관없이 setPreferredSize한 크기로 고정됨 (FlowLayout은 창 위치에 따라 배치 변경됨)
         JPanel emailPanel = new JPanel(); // 버튼을 담을 패널
         emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.X_AXIS)); 
         
         emailPanel.add(emailTf);
         emailPanel.add(new JLabel("@"));
         //domainTf.setPreferredSize(new Dimension(100, 30)); // 고정 크기 설정
         emailPanel.add(domainTf);
         emailPanel.add(domainComboBox);

         SubGridCons.gridx = 2;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(emailPanel, SubGridCons);
         
         /* 우편번호 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(zipcodeLabel, SubGridCons);

         JPanel zipcodePanel = new JPanel(); // 버튼을 담을 패널
         zipcodePanel.setLayout(new BoxLayout(zipcodePanel, BoxLayout.X_AXIS)); 
         
         zipcodePanel.add(zipcodeTf); 
         zipcodePanel.add(zipcodeButton);

         
         SubGridCons.gridx = 2;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(zipcodePanel, SubGridCons);
         
         /* 주소 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(addressLabel, SubGridCons);

         ++SubGridCons.gridx;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(addressTf, SubGridCons);
         
         /* 상세주소 */
         SubGridCons.gridx = 1;
         ++SubGridCons.gridy;
         SubGridCons.weightx = 0.0; // 폭을 넓힘
         subPanel.add(detailedAddressLabel, SubGridCons);

         ++SubGridCons.gridx;
         SubGridCons.weightx = 1.0; // 폭을 넓힘
         subPanel.add(detailedAddressTf, SubGridCons);
         
         /* 회원가입창 */

         //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         //JLabel signupLabel = new JLabel("회원가입 화면 ");
         //signupLabel.setFont(font);
         //signupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

             // gridPanel을 이미지 패널의 오른쪽 (1, 0)에 배치
         MainGridCons.gridx = 1;
         MainGridCons.gridy = 0;
         mainPanel.add(subPanel, BorderLayout.CENTER);
           //mainPanel.add(subPanel, MainGridCons);
         
         /* 버튼들 */
         SignupButton = new JButton("확인");
         SignupButton.setAlignmentX(Component.CENTER_ALIGNMENT);

         CancelButton = new JButton("취소");
         CancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
         AgainButton = new JButton("다시 입력");
         AgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);

         // BorderLayout으로 해버리면 중앙 정렬이 안됨
         JPanel buttonPanel = new JPanel(new FlowLayout()); // 버튼을 담을 패널
         buttonPanel.setOpaque(false); // 패널 배경 투명화
         buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 0)); // (정렬, 수평 간격, 수직 간격)
         
         buttonPanel.add(SignupButton);
         buttonPanel.add(CancelButton);
         buttonPanel.add(AgainButton);
         
         mainPanel.add(buttonPanel, BorderLayout.SOUTH);
         
         // 실시간 비밀번호 안전도 검사
         passTf.getDocument().addDocumentListener(new DocumentListener() {
             @Override
             public void insertUpdate(DocumentEvent e) {
                 updateStrength();
             }

             @Override
             public void removeUpdate(DocumentEvent e) {
                 updateStrength();
             }

             @Override
             public void changedUpdate(DocumentEvent e) {
                 updateStrength();
             }

             // 비밀번호 안전도 검사 함수
             private void updateStrength() {
                 String password = new String(passTf.getPassword());
                 Color strengthColor = getStrengthColor(password);
                 strengthPanel.setBackground(strengthColor); // 안전도 상태 업데이트
             }
             
             // 비밀번호 안전도에 따른 색상 반환
             private Color getStrengthColor(String password) {
                 if (password.length() < 6) {
                     return Color.RED; // 위험
                 } else if (password.length() < 10 || !password.matches(".*\\d.*")) {
                     return Color.ORANGE; // 중간
                 } else {
                     return Color.GREEN; // 안전
                 }
             }
         });
         
         monthComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               if (e.getSource() == monthComboBox) {
                  JComboBox monthBox = (JComboBox) e.getSource();
                  month = (String) monthBox.getSelectedItem();
                  System.out.println(month);
               }

            }
         });
         
         dayComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               // TODO Auto-generated method stub
               if (e.getSource() == dayComboBox) {
                  JComboBox dayBox = (JComboBox) e.getSource();
                  day = (String) dayBox.getSelectedItem();
                  System.out.println(month);
               }
            }
         });

         menButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               // TODO Auto-generated method stub
               sex = e.getActionCommand();
            }
         });

         girlButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               // TODO Auto-generated method stub
               sex = e.getActionCommand();
            }
         });
         
         /* 사진등록 버튼 - 이미지 폴더 불러오기 */
           imageButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  JFrame FolderFrame = new JFrame();
                   FolderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 창을 닫을 때만 해당 창을 닫도록 설정
                   FolderFrame.setSize(400, 300);
                   
                   
                   JFileChooser fileChooser = new JFileChooser();
                   fileChooser.setDialogTitle("Select an Image");
                   // 이미지 파일 필터 설정 (선택 사항)
                   fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
                   
                   int result = fileChooser.showOpenDialog(FolderFrame);
                   // 마우스 클릭 들어오는 시점
                   if (result == JFileChooser.APPROVE_OPTION) 
                   {
                       File selectedFile = fileChooser.getSelectedFile();
                       // 선택한 이미지를 JLabel에 표시
                       String imagePath = selectedFile.getAbsolutePath();
                       ImageIcon profileIcon = new ImageIcon(imagePath);
                       
                       // 이미지 리사이즈 창 표시
                       JFrame imageResizeFrame = new JFrame();
                       ImageCrop cropPanel = new ImageCrop(imagePath);
                       
                       JPanel buttonPanel = new JPanel();
                       buttonPanel.setLayout(new FlowLayout());
                       
                       JButton cropButton = new JButton("크기 리사이즈");
                       cropButton.addActionListener(new ActionListener() {
                          public void actionPerformed(ActionEvent e)
                          {
                             BufferedImage croppedImage = cropPanel.cropImage();
                              if (null != croppedImage) 
                              {
                                 ImageIcon croppedIcon = new ImageIcon(croppedImage);
                                 imageLabel.setIcon(croppedIcon);
                                  imageResizeFrame.dispose(); // JFrame::dipose() 창 종료 함수
                              }
                          }
                      });

                       JButton fixButton = new JButton("프사 크기로 고정");
                       fixButton.addActionListener(new ActionListener() {
                          public void actionPerformed(ActionEvent e)
                          {
                              /* 이미지 크기 조정하여 셋팅 */
                              Image DumpImage = profileIcon.getImage().getScaledInstance(PROFILE_SIZE,  PROFILE_SIZE, Image.SCALE_SMOOTH); // 크기 조정
                              ImageIcon scaledImage = new ImageIcon(DumpImage);
                              imageLabel.setIcon(scaledImage); // new JLabel로 하면 Label 자체가 바껴서 두번째부터 교체가 안됨
                              imageResizeFrame.dispose(); // JFrame::dipose() 창 종료 함수

                          }
                      });
                       
                       imageResizeFrame.add(cropPanel, BorderLayout.CENTER);
                       buttonPanel.add(cropButton);
                       buttonPanel.add(fixButton);
                       imageResizeFrame.add(buttonPanel, BorderLayout.SOUTH);
                       imageResizeFrame.setSize(500, 500);
                       imageResizeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                       imageResizeFrame.setVisible(true);                   
                       
                     /* 이미지 크기 조정하여 셋팅 */
                     //Image DumpImage = profileIcon.getImage().getScaledInstance(PROFILE_SIZE,  PROFILE_SIZE, Image.SCALE_SMOOTH); // 크기 조정
                     //ImageIcon scaledImage = new ImageIcon(DumpImage);
                     //imageLabel.setIcon(scaledImage); // new JLabel로 하면 Label 자체가 바껴서 두번째부터 교체가 안됨
                       
                     imageData = imageToByteArray(imagePath);
                   }
               }
           });
           
           /* 우편번호 버튼 */
           EventListener el = (EventListener)this;
           zipcodeButton.addActionListener(new ActionListener() 
           {
               @Override
               public void actionPerformed(ActionEvent e) 
               {       
                  ZipcodePopup zp = new ZipcodePopup();
                  zp.addListener(el);
               }
           });
           
           
           
           
               
         /* 중복 확인 - PRIMARY 키 속성을 이용하여 확인 */
           idCheckButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               try
               {
                  id = idTf.getText();
                  if(0 == id.length())
                  {
                     JOptionPane.showMessageDialog(null, "아이디를 입력해주세요.", "미입력", 1);
                     return;
                  }
                  
                  Connection conn = mySql.getConnection();
      
                  // 쿼리문 준비
                  String Query = "select id from UserTable where id=(?)";
                  PreparedStatement pstmt = conn.prepareStatement(Query);      
                  
                  pstmt.setString(1,  id);
                  ResultSet Result = pstmt.executeQuery(); // select문은 executeQuery로 실행 (반환타입: ResultSet)
                  
                  if(!Result.next())
                  {
                     JOptionPane.showMessageDialog(null, "사용가능한 아이디입니다.", "통과", 1);
                  }
                  else
                  {
                     JOptionPane.showMessageDialog(null, "해당 아이디가 이미 존재합니다.", "아이디 중복 오류", 1);
                  }
                  
                  bCheckDuplicate = true;
                  
                  pstmt.close();
                  conn.close();
               
               } 
               catch (SQLException e1) 
               {
                  System.out.println(e1.getMessage()); // 오류 출력문
                  if (e1.getMessage().contains("PRIMARY")) 
                     JOptionPane.showMessageDialog(null, "해당 아이디가 이미 존재합니다.", "아이디 중복 오류", 1);
                  else
                     System.out.println("Exception 오류");
                  
                  bCheckDuplicate = false;
               }
            }
         });
         
         aliasCheckButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               try
               {
                  id = idTf.getText();
                  if(0 == id.length())
                  {
                     JOptionPane.showMessageDialog(null, "닉네임을 입력해주세요.", "미입력", 1);
                     return;
                  }
                  
                  Connection conn = mySql.getConnection();
      
                  // 쿼리문 준비
                  String Query = "select id from UserTable where alias=(?)";
                  PreparedStatement pstmt = conn.prepareStatement(Query);      
                  
                  pstmt.setString(1,  id);
                  ResultSet Result = pstmt.executeQuery(); // select문은 executeQuery로 실행 (반환타입: ResultSet)
                  
                  if(!Result.next())
                  {
                     JOptionPane.showMessageDialog(null, "사용가능한 닉네임입니다.", "통과", 1);
                  }
                  else
                  {
                     JOptionPane.showMessageDialog(null, "해당 닉네임이 이미 존재합니다.", "닉네임 중복 오류", 1);
                  }
                  
                  bCheckDuplicate = true;
                  
                  pstmt.close();
                  conn.close();
               } 
               
               catch (SQLException e1) 
               {
                  System.out.println(e1.getMessage()); // 오류 출력문
                  if (e1.getMessage().contains("PRIMARY")) 
                     JOptionPane.showMessageDialog(null, "해당 아이디가 이미 존재합니다.", "아이디 중복 오류", 1);
                  else
                     System.out.println("Exception 오류");
                  
                  bCheckDuplicate = false;
               }
            }
         });
         
         domainComboBox.addActionListener(new ActionListener() 
         {
            @Override
            public void actionPerformed(ActionEvent e) {
               // TODO Auto-generated method stub
               if (e.getSource() == domainComboBox) {
                  JComboBox domainBox = (JComboBox) e.getSource();
                  domain = (String) domainBox.getSelectedItem();
                  
                  if("직접입력" != domain)
                  {
                     domainTf.setText(domain);
                  }
                  
                  System.out.println(domain);
               }
            }
         });
         
         CancelButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
               dispose();
            }
         });
         
         AgainButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
                nameTf.setText("");
                idTf.setText("");
                aliasTf.setText("");
                passTf.setText("");
                passReTf.setText("");
                zipcodeTf.setText("");
                addressTf.setText("");
                detailedAddressTf.setText("");
                emailTf.setText("");
                domainTf.setText("");
                phoneMiddleTf.setText("");
                phoneEndTf.setText("");
            }
         });
         
         // 회원가입 버튼 클릭
         SignupButton.addActionListener(new ActionListener() 
         { 

            @Override
            public void actionPerformed(ActionEvent e) {
               
               if(!bCheckDuplicate)
               {
                  JOptionPane.showMessageDialog(null, "아이디 중복확인을 해주세요.", "오류", 1);
                  return;
               }
               
               // TODO Auto-generated method stub
               id = idTf.getText();
               password = new String(passTf.getPassword());
               passRe = new String(passReTf.getPassword());
               name = nameTf.getText();
               birth = (String)yearComboBox.getSelectedItem() + (String)monthComboBox.getSelectedItem() + (String)dayComboBox.getSelectedItem();
               address = addressTf.getText() + detailedAddressTf.getText(); // zipcodeTf.getText()
               email = emailTf.getText() + "@" + domainTf.getText();
               phone = phoneNumberComboBox.getSelectedItem() + phoneMiddleTf.getText() + phoneEndTf.getText();
               
               if(" " == domainTf.getText())
                  JOptionPane.showMessageDialog(null, "도메인을 선택해주세요", "아이디 중복 오류", 1);

               String Query = "insert into UserTable(image, name, id, alias, password, birth, phone, sex, email, address) values (?,?,?,?,?,?,?,?,?,?)";

               Pattern passPattern1 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$"); //8자 영문+특문+숫자
               Matcher passMatcher = passPattern1.matcher(password);

               if (!passMatcher.find()) 
               {
                  JOptionPane.showMessageDialog(null, "비밀번호는 영문+특수문자+숫자 8자로 구성되어야 합니다", "비밀번호 오류", 1);
               } 
               else if (!password.equals(passRe)) 
               {
                  JOptionPane.showMessageDialog(null, "비밀번호가 서로 맞지 않습니다", "비밀번호 오류", 1);

               } 
               else 
               {
                  try 
                  {
                     // 데이터베이스 연결
                     Connection conn = mySql.getConnection();

                     // 쿼리문 준비
                     PreparedStatement pstmt = conn.prepareStatement(Query);

                     //String date = yearTf.getText() + "-" + month + "-" + day;
                     
                     // 데이터베이스에 저장되는 순서
                     int iIndex = 0;
                     pstmt.setBytes(++iIndex, imageData);
                     pstmt.setString(++iIndex, nameTf.getText());
                     pstmt.setString(++iIndex, id);
                     pstmt.setString(++iIndex, aliasTf.getText());
                     pstmt.setString(++iIndex, password);
                     pstmt.setString(++iIndex, birth);
                     pstmt.setString(++iIndex, phone);
                     pstmt.setString(++iIndex, sex);
                     pstmt.setString(++iIndex, email);
                     pstmt.setString(++iIndex, address);

                     int Result = pstmt.executeUpdate();
                     System.out.println("변경된 row " + Result);
                     JOptionPane.showMessageDialog(null, "회원 가입 완료!", "회원가입", 1);
                     
                     pstmt.close();
                     conn.close();
                     
                     // 완료되면 이전 cardPanel(로그인창)으로 전환
                     dispose();
                     
                  } catch (SQLException e1) {
                     System.out.println("SQL error" + e1.getMessage());
                     if (e1.getMessage().contains("PRIMARY")) 
                        JOptionPane.showMessageDialog(null, "아이디 중복!", "아이디 중복 오류", 1);
                     else
                        JOptionPane.showMessageDialog(null, "정보를 제대로 입력해주세요!", "오류", 1);
                  } // try ,catch
               }
            }
         });
   }
   private void setGlobalFont(String fontPath, float fontSize) {
       try {
           Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
           customFont = customFont.deriveFont(Font.PLAIN, fontSize);

           // Swing 컴포넌트에 적용
           UIManager.put("Label.font", customFont);
           UIManager.put("Button.font", customFont);
           UIManager.put("TextField.font", customFont);
           UIManager.put("TextArea.font", customFont);
           UIManager.put("ComboBox.font", customFont);
           UIManager.put("CheckBox.font", customFont);
           UIManager.put("RadioButton.font", customFont);
           UIManager.put("TabbedPane.font", customFont);
           UIManager.put("Table.font", customFont);
           UIManager.put("Menu.font", customFont);
           UIManager.put("MenuItem.font", customFont);
           UIManager.put("ToolTip.font", customFont);

           System.out.println("폰트 적용 완료: " + fontPath);
       } catch (FontFormatException | IOException e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(null, "폰트를 로드하지 못했습니다. 기본 폰트를 사용합니다.");
       }
   }
   
   public JLabel checkPassSaftey(JPasswordField passwordTF)
   {
      /* 검사 기준 */
      // 1. 문자열 길이 2. 연속된 숫자
      // 0개 충족 - 안전, 1개 충족 - 보통 2개 충족 - 위험
      
      JLabel safteyLabel = null;
      password = new String(passwordTF.getPassword());
      if(8 >= password.length())
         safteyLabel = new JLabel("안전도: 위험");
      else if(9 >= password.length())
         safteyLabel = new JLabel("안전도: 보통");
      else
         safteyLabel = new JLabel("안전도: 안전");
      
      return safteyLabel;
   }
   
   /* ImageToByteArray: 이미지를 byte로 변환하는 함수 */
   // mySql은 BLOP(Binary Large Object)를 사용하여 이미지 저장
   public byte[] imageToByteArray(String filepath)
   {
      byte[] returnValue= null;
      
      ByteArrayOutputStream baos = null;
      FileInputStream fis = null;
      
      try
      {
         baos = new ByteArrayOutputStream();
         fis = new FileInputStream(filepath);
         
         byte[] buffer = new byte[1024];
         int read = 0;
         
         while(-1 != (read = fis.read(buffer, 0, buffer.length)))
         {
            baos.write(buffer, 0, read);
         }
         
         returnValue = baos.toByteArray();

         baos.close();
         fis.close();
      }
      catch(Exception e)
      {
         System.out.println(e.getMessage());
         e.printStackTrace();
      }

      return returnValue;
   }

}