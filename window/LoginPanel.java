package gui;

import dataBase.DataBase;

import gameClient.ClientLobby;
import java.sql.*;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ImageIcon;  // 이미지 아이콘 사용
import javax.swing.JButton;    // JButton 사용
import javax.swing.JTextField; // JTextField 사용
import javax.swing.JPasswordField; // JPasswordField 사용
import javax.swing.JOptionPane; // JOptionPane 사용 (메시지 박스)
import javax.swing.border.LineBorder; // 버튼 테두리 설정용
import java.awt.Color; // 색상 설정
import java.awt.event.MouseAdapter; // 마우스 이벤트 처리
import java.awt.event.MouseEvent;   // 마우스 이벤트 처리
import java.awt.Font; // 글꼴 설정
import java.io.File; // File 클래스를 import
import java.io.IOException; // IOException을 처리하기 위해 import
import java.awt.Font; // Font 클래스를 import
import java.awt.FontFormatException;
import java.awt.Image; // Image 클래스 import
import javax.swing.ImageIcon;
import javax.swing.SwingConstants; // ImageIcon 클래스 import

@SuppressWarnings("serial")
public class LoginPanel extends JPanel implements PanelInterface {

    private ClientLobby client;

    private JTextField inputId;
    private JPasswordField inputPass;
    private JButton loginButton;
    private JButton signupButton;
    
    private JButton passwordfind;
    private JButton idfind;

    private DataBase mySql;
    
   
   @SuppressWarnings("serial")
   public LoginPanel(final ClientLobby client, final DataBase mySql) {
       this.client = client;
       this.mySql = mySql;

       // Create text fields for input
       inputId = new JTextField(7);
       inputPass = new JPasswordField(7);

       // Create buttons instead of ImageButton
       loginButton = new JButton("LOGIN");
       signupButton = new JButton("SIGNUP");
       
       passwordfind = new JButton("FINDPASSWORD");
       idfind = new JButton("FINDID");

      add(inputId);
      add(inputPass);
      add(loginButton);
      add(signupButton);
      add(passwordfind);
      add(idfind);
     
      inputId.setBounds(209, 198, 355, 25);
      signupButton.setBounds(430, 314, 134, 25);
      inputPass.setBounds(209, 233, 355, 25);
      loginButton.setBounds(209, 268, 355, 35); // Set bounds for the login button
      idfind.setBounds(343, 313, 112, 25);
      passwordfind.setBounds(185, 313, 182, 25);
      
      JButton btnNewButton = new JButton("");
      btnNewButton.setVerticalAlignment(SwingConstants.BOTTOM);
      btnNewButton.setEnabled(false);
      btnNewButton.setBounds(185, 168, 417, 187);
      add(btnNewButton);
      
      btnNewButton.setContentAreaFilled(true); // 내용 영역을 채움
      btnNewButton.setBackground(new Color(50, 50, 50, 200)); // 배경색을 진한 회색(투명도 200)
      btnNewButton.setOpaque(true); // 버튼을 불투명하게 설정
      
      JButton LABEL = new JButton("OMOKGAME");
      LABEL.setForeground(new Color(255, 105, 180)); // 텍스트 색을 네온 하늘색으로 설정
      LABEL.setEnabled(true);  // 버튼을 활성화 상태로 설정
      LABEL.setOpaque(false);  // 배경을 투명하게 설정
      LABEL.setContentAreaFilled(false);  // 버튼의 콘텐츠 영역을 투명하게 설정
      LABEL.setBorderPainted(false);  // 테두리 없애기
      LABEL.setFocusPainted(false);  // 버튼에 포커스 시 생기는 외곽선 없애기
      LABEL.setBounds(130, 53, 504, 105);
      add(LABEL);
      
      JButton LABEL2 = new JButton("OMOKGAME");
      LABEL2.setForeground(new Color(135, 206, 235)); // 텍스트 색을 네온 하늘색으로 설정
      LABEL2.setEnabled(true);  // 버튼을 활성화 상태로 설정
      LABEL2.setOpaque(false);  // 배경을 투명하게 설정
      LABEL2.setContentAreaFilled(false);  // 버튼의 콘텐츠 영역을 투명하게 설정
      LABEL2.setBorderPainted(false);  // 테두리 없애기
      LABEL2.setFocusPainted(false);  // 버튼에 포커스 시 생기는 외곽선 없애기
      LABEL2.setBounds(138, 57, 504, 105);
      add(LABEL2);



      
   // 로그인 버튼 스타일 설정
      try {
    	    // TTF 파일 경로 설정
    	    File fontFile = new File("C:\\Users\\dlgod\\workspace\\OMg3\\src\\image\\Ailerons-TrialVersion.otf");

    	    // TTF 폰트 로드
    	    Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile); // 기본 폰트 로드
    	    

    	    // 로그인 버튼에는 30f 크기
    	    Font loginButtonFont = customFont.deriveFont(30f); 
    	    loginButton.setFont(loginButtonFont);

    	    // 다른 컴포넌트에는 16f 크기
    	    Font inputFont = customFont.deriveFont(24f);
    	    idfind.setFont(inputFont);
    	    passwordfind.setFont(inputFont);
    	    signupButton.setFont(inputFont);
    	    
    	    Font LABELFONT = customFont.deriveFont(100f);
    	    LABEL.setFont(LABELFONT);
    	    LABEL2.setFont(LABELFONT);

    	} catch (FontFormatException | IOException e) {
    	    e.printStackTrace();
    	    JOptionPane.showMessageDialog(null, "폰트를 로드하는 데 실패했습니다.");
    	}
      
      loginButton.setBackground(new Color(50, 50, 50)); // 배경 색상 (진한 회색)
      loginButton.setForeground(Color.white); // 텍스트 색상 (검은색)
      loginButton.setFocusPainted(false); // 버튼에 포커스 시 생기는 외곽선 없애기
      
      
      idfind.setForeground(Color.white); // 텍스트 색상 (검은색)
      passwordfind.setForeground(Color.white); // 텍스트 색상 (검은색)
      signupButton.setForeground(Color.white); // 텍스트 색상 (검은색)
      
      idfind.setContentAreaFilled(false);  // 버튼 콘텐츠 영역을 투명하게 설정
      passwordfind.setContentAreaFilled(false);  // 버튼 콘텐츠 영역을 투명하게 설정
      signupButton.setContentAreaFilled(false);  // 버튼 콘텐츠 영역을 투명하게 설정

      idfind.setOpaque(false);  // 버튼을 투명하게 설정
      passwordfind.setOpaque(false);  // 버튼을 투명하게 설정
      signupButton.setOpaque(false);  // 버튼을 투명하게 설정

      idfind.setBorderPainted(false);  // 외곽선 제거
      passwordfind.setBorderPainted(false);  // 외곽선 제거
      signupButton.setBorderPainted(false);  // 외곽선 제거

      idfind.setFocusPainted(false);  // 포커스 시 외곽선 제거
      passwordfind.setFocusPainted(false);  // 포커스 시 외곽선 제거
      signupButton.setFocusPainted(false);  // 포커스 시 외곽선 제거
     

      // 마우스 오버 시 이벤트 처리
      loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
    	    @Override
    	    public void mouseEntered(java.awt.event.MouseEvent evt) {
    	        // 마우스를 버튼 위에 올렸을 때 텍스트 색을 네온 하늘색으로 변경
    	        loginButton.setForeground(new Color(135, 206, 235)); // 네온 하늘색 텍스트
    	    }

    	    @Override
    	    public void mouseExited(java.awt.event.MouseEvent evt) {
    	        // 마우스를 떼었을 때 텍스트 색을 검은색으로 복원
    	        loginButton.setForeground(Color.white); // 원래 텍스트 색 (검은색)
    	    }

    	    @Override
    	    public void mousePressed(java.awt.event.MouseEvent evt) {
    	        // 마우스를 클릭했을 때 텍스트 색을 네온 핑크로 변경
    	        loginButton.setForeground(new Color(255, 105, 180)); // 네온 핑크 텍스트
    	    }

    	    @Override
    	    public void mouseReleased(java.awt.event.MouseEvent evt) {
    	        // 마우스를 떼었을 때 텍스트 색을 네온 하늘색으로 복원
    	        loginButton.setForeground(new Color(135, 206, 235)); // 네온 하늘색 텍스트
    	    }
    	});




      
      // 로그인 검사 코드
      loginButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            
            // 로그인 가능한지 확인
            if(checkIsCorrectLoginInfo())
            {
               client.setName(inputId.getText());
               client.changePanel(new LobbyGui(client, mySql));
            }
            else 
            {
               JOptionPane.showMessageDialog(null, "회원 정보가 일치하지 않습니다.");
            }
         }
      });
      
      idfind.addActionListener(e -> {
          IdFindPanel.showInFrame(mySql);
      });

      passwordfind.addActionListener(e -> {
          PasswordFindPanel.showInFrame(mySql);
      });


      
      setLayout(null);
      

   }
   
   
   /** 로그인 정보가 맞는지 확인합니다. */
   public boolean checkIsCorrectLoginInfo() {
	    String strID = inputId.getText();
	    String strPassword = inputPass.getText();
	    
	    // UserTable로부터 id와 password가 동일한 password 속성의 값을 가져와라.
	    String strSqlPassword = String.format("SELECT password, account_status FROM UserTable WHERE id = '%s' AND password ='%s'",
	            strID, strPassword);

	    try {
	        Connection conn = mySql.getConnection();
	        Statement stmt = conn.createStatement();

	        ResultSet rset = stmt.executeQuery(strSqlPassword);
	        
	        if (rset.next()) {
	            String password = rset.getString("password");
	            boolean accountStatus = rset.getBoolean("account_status"); // account_status 컬럼값 가져오기
	            
	            // 비밀번호가 맞고 계정이 활성화된 경우
	            if (strPassword.equals(password)) {
	                if (!accountStatus) { // account_status가 false (비활성화)일 경우
	                    JOptionPane.showMessageDialog(this, "비활성화된 계정입니다.", "계정 비활성화", JOptionPane.ERROR_MESSAGE);
	                    return false;
	                }
	                // 계정 활성화됨
	                return true;
	            }
	        }
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(this, "LoginPanel - checkIsCorrectLoginInfo() 오류", "로그인 실패", 1);
	        System.out.println("SQLException" + ex);
	        return false;
	    }

	    return false; // 비밀번호가 맞지 않으면 false 반환
	}

   
   public void paint(Graphics g) {
      ImageIcon icon = new ImageIcon(
            URLGetter.getResource("src/image/loginForm.png"));
      g.drawImage(icon.getImage(),0,0,null,null);
      this.paintComponents(g);
   }
   
   public int[] getFrameSize() {
      int size[] = new int[2];
      size[0] = 800;
      size[1] = 450;
      return size;
   }
   
   public static void main(String[] args) {
      new LoginForm();
   }
   
   public String getInputText() {
      return null;
   }

   public JList getJList() {
      return null;
   }

   public void setTextToInput(String string) {
   }
   public void setTextToLogWindow(String str) {
   }
   public void setTotalUser(Vector<String> userList) {
   }
   public void setUserList(Vector<String> userList) {
   }
   public void setUserListFrame(UserListFrame userListFrame) {
   }
   public void showMessageBox(String sender, String message, boolean option) {
   }
   public void unShow() {
   }
   public void setPanel(PanelInterface panel) {
   }
   public void setPanel(GameLobbyInter panel) {
   }
   public void setPanel(LobbyGuiInter panel) {
   }
   public void setPanel(RoomGuiInter panel) {
   }
}
