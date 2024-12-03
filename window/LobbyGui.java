package gui;

/*
 * Version 1.01 梨쀭떚李� �씪�씤蹂댄샇.
 */

import dataBase.DataBase;


import gameClient.ClientInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import gui.KakaoTalk.*;
import java.awt.Image;
import java.awt.Font;
import java.awt.FontFormatException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionListener;


@SuppressWarnings("serial")
public class LobbyGui extends JPanel implements LobbyGuiInter, PanelInterface {

    private ClientInterface client;
    private EventExecute event;
    private UserListFrame userListFrame;
    private DataBase mySql;
    private JButton adminButton;
    private UserInfoFrame UserInfoFrame;
    private JButton replayButton; 
    

    private JLabel m_profilePicLabel; // 사용자 프로필 사진
    private JLabel m_usernameLabel; // 사용자 이름

    private JPanel m_roomListPanel = new JPanel() {
        public void paint(Graphics g) {
            this.paintComponents(g);
        }
    };

    private JList m_roomList = new JList();
    private JTable m_roomListTable;
    private JScrollPane m_scPaneRoomList;
    private JButton m_createRoomButton, m_enterRoomButton;

    private JPanel m_userListPanel = new JPanel() {
        public void paint(Graphics g) {
            this.paintComponents(g);
        }
    };

    private JList m_userList = new JList();
    private JPopupMenu popup;
    private JScrollPane m_scPaneUserList;
    private ImageButton m_totalUserButton;

    private JPanel m_logWinPanel = new JPanel() {
        public void paint(Graphics g) {
            this.paintComponents(g);
        }
    };

    private JTextArea m_logWindow = new JTextArea(5, 20);
    protected JTextField m_textInput = new JTextField();
    private JButton m_sendButton = new JButton("SEND");
    private JScrollPane m_scPaneLogWin;
    private JScrollBar m_vScroll;

    private JPanel m_infoPanel = new JPanel() {
        public void paint(Graphics g) {
            this.paintComponents(g);
        }
    };

    private JLabel m_infoLabel = new JLabel();
    private JButton m_exitButton = new JButton("EXIT");
    
    

    private Vector<String> vc = new Vector<String>(1);

	/** @error - ClientLobby의 main을 호출하는데 불구하고 LobbyGui의 main이 호출되는 현상이 발생함. 의문 .. */
	public LobbyGui(ClientInterface client) 
	{
		this.client = client;
		
		
		event = new EventExecute(this, client);
		
		// @function - 항목 선택 시 회원 정보 창을 띄웁니다. (이벤트 리스터는 생성자 혹은 메소드 내부에 있어야 함.)
		m_userList.addListSelectionListener(new ListSelectionListener() 
		{
		    @Override
		    public void valueChanged(ListSelectionEvent e) 
		    {
		        if (!e.getValueIsAdjusting()) 
		        { // 클릭이 완료된 상태인지 확인
		            String selectedUser = (String)m_userList.getSelectedValue(); // 선택된 항목 가져오기
		            
	            	// 서버 Lobby로부터 get(선택한 목록 이름)해서 GameServer를 넘겨야 함.
	            	String myName = client.getName();
	            	UserInfoFrame = new UserInfoFrame(client, myName, selectedUser, mySql);	 //@error - 내 이름 가져와서 넘겨야 함
		        }
		    }
		});	
		
		vc.add("");
		excute();
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public LobbyGui(ClientInterface client, DataBase mySql) 
	{
		this.client = client;
		this.mySql = mySql;
		
		setLayout(null); // 명시적 위치 지정
	    
		event = new EventExecute(this, client);
		
		 UIManager.put("Button.background", new Color(137, 119, 173)); // 모든 버튼의 배경색
	     UIManager.put("Button.foreground", Color.black);            // 모든 버튼의 텍스트 색상

		
	    // 사용자 이름과 프로필 사진 초기화
        m_usernameLabel = new JLabel();//닉네임
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/image/Galmuri11-Bold.ttf")).deriveFont(16f); // 24px 크기로 설정
            m_usernameLabel.setFont(customFont); // 폰트 적용
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            m_usernameLabel.setText("폰트를 로드할 수 없습니다.");
        }

        m_usernameLabel.setBounds(39, 85, 96, 20);
        String alias = mySql.getUserAlias(client.getName()); // client의 이름으로 alias 조회
        m_usernameLabel.setText(alias);
        add(m_usernameLabel);
        	            
        // Weather 클래스를 사용해 날씨 정보를 가져옵니다.
        Weather weather = new Weather(client.getName()); // loggedInUserId는 현재 로그인된 사용자 ID를 전달
        String weatherInfo = weather.getCurrentWeatherInfo(); // 날씨 정보를 가져옵니다.

        // 새 JLabel 생성
        JLabel weatherLabel = new JLabel("<html>" + weatherInfo.replace("\n", "<br>") + "</html>"); // 줄바꿈을 HTML 형식으로 변환
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/image/Galmuri11-Bold.ttf")).deriveFont(16f); // 24px 크기로 설정
            weatherLabel.setFont(customFont); // 폰트 적용
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            weatherLabel.setText("폰트를 로드할 수 없습니다.");
        }
        weatherLabel.setBounds(29, 106, 300, 60); // 위치와 크기 설정 (x, y, width, height)

        // JPanel 또는 JFrame에 추가
        add(weatherLabel); // contentPane 또는 사용 중인 부모 컴포넌트에 추가
        
        m_profilePicLabel = new JLabel();//프로필
        m_profilePicLabel.setBounds(29, 24, 70, 70);
        add(m_profilePicLabel);
        
        
        String winLoseRate = mySql.getWinLoseRate(client.getName());
        JLabel m_winLoseLabel = new JLabel(winLoseRate); // 승패 정보 표시
        m_winLoseLabel.setBounds(81, 24, 140, 20);
        add(m_winLoseLabel);

        String rank = mySql.getUserRank(client.getName());
        JLabel m_rankLabel = new JLabel(rank); // 순위 표시	                        
        m_rankLabel.setBounds(81, 54, 120, 20);
        add(m_rankLabel);
        int replayButtonWidth = 163; // replayButton의 너비
        int replayButtonHeight = 30; // replayButton의 높이
        
        replayButton = new JButton("최근 플레이 복기");
        replayButton.setBounds(336, 7, 119, 30); // X 위치를 왼쪽으로 조정
        m_roomListPanel.add(replayButton);

        // 복기 버튼 클릭 이벤트
        replayButton.addActionListener(e -> new GameRoomGui(client, true)); // 복기 모드로 게임 창 열기
        
       
        
        replayButton.addActionListener(e -> new GameRoomGui(client, true));

        
        setGlobalFont("src/image/Galmuri11-Bold.ttf", 11, Color.black);
        
        JLabel backgroundLabel = new JLabel(new ImageIcon("src/image/Signup.png"));
        backgroundLabel.setLayout(new BorderLayout()); // 컴포넌트를 배치할 수 있도록 레이아웃 설정

			 

	    checkAdminPrivilege(client.getName());
	    
		// @function - 항목 선택 시 회원 정보 창을 띄웁니다. (이벤트 리스터는 생성자 혹은 메소드 내부에 있어야 함.)
		m_userList.addListSelectionListener(new ListSelectionListener() 
		{
		    @Override
		    public void valueChanged(ListSelectionEvent e) 
		    {
		    	// 클릭이 완료된 상태인지 확인
		        if (!e.getValueIsAdjusting()) 
		        { 
		            String selectedUser = (String)m_userList.getSelectedValue(); // 선택된 항목 가져오기
		            String myName = client.getName();
		            UserInfoFrame = new UserInfoFrame(client, myName, selectedUser, mySql);
		        }
		    }
		});	
				
		vc.add("");
		excute();
	}
	
	
	
	private void checkAdminPrivilege(String userId) {
        String query = "SELECT is_admin FROM UserTable WHERE id = ?";
        try (Connection conn = mySql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getBoolean("is_admin")) {
                // 관리자라면 버튼 보이기
                adminButton.setVisible(true);
                adminButton.setEnabled(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "관리자 확인 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
	
	
	public void paint(Graphics g) 
	{
		ImageIcon icon = new ImageIcon(
	            URLGetter.getResource("src/image/lobbyForm.jpg"));
	      g.drawImage(icon.getImage(),0,0,null,null);
	      this.paintComponents(g);
	}
	
	public static void setGlobalFont(String fontPath, int fontSize, Color fontColor) {
        try {
            // TTF 폰트 로드
            Font globalFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont((float) fontSize);

            // UIManager를 사용해 기본 폰트와 색상 설정
            UIManager.put("Label.font", globalFont);
            UIManager.put("Button.font", globalFont);
            UIManager.put("TextField.font", globalFont);
            UIManager.put("TextArea.font", globalFont);
            UIManager.put("CheckBox.font", globalFont);
            UIManager.put("ComboBox.font", globalFont);
            // 더 필요한 컴포넌트 추가 가능

            // 폰트 색상 변경
            UIManager.put("Label.foreground", fontColor);
            UIManager.put("Button.foreground", fontColor);
            UIManager.put("TextField.foreground", fontColor);
            UIManager.put("TextArea.foreground", fontColor);
            // 더 필요한 색상 속성 추가 가능
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "글로벌 폰트 설정에 실패했습니다.");
        }
    }

	   
	

	
	
	public void addChatArea(String chat) 
	{
		m_logWindow.append(chat);
		mySql.saveMessageToDatabase(chat);
	}

	public static void main(String[] args) 
	{
		JFrame frame = new JFrame("Network FIve Eyes Ver. 1.0");
		Container cp = frame.getContentPane();
		cp.add(new LobbyGui(null));
		
		frame.setSize(600, 450);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		System.out.println("Exit");
	}

	private void excute() 
	{
		add(generatorRoomListPanel());	
		

		adminButton = new JButton("관리자 모드 실행");
		adminButton.setBounds(465, 7, 150, 30);
		m_roomListPanel.add(adminButton);
		adminButton.setVisible(false); // 기본적으로 숨김
		adminButton.setEnabled(false); // 클릭 불가
		adminButton.addActionListener(e -> new CertFrame(client.getName()));
		
		 int adminButtonX = adminButton.getX(); // adminButton의 X 위치
		 int adminButtonWidth = adminButton.getWidth(); // adminButton의 너비
		add(generatorUserListPanel());
		add(generatorChatWindowPanel());
		add(generatorInfoPanel());
		setLayout(null);
	}
	
	private JPanel generatorRoomListPanel() 
	{
	    // Room List Panel 레이아웃 설정
	    m_roomListPanel.setLayout(null);
        m_roomListPanel.setBounds(227, 5, 615, 295); // 패널 크기 및 위치 수정

	    // Create Room 버튼 설정
	    m_createRoomButton = new JButton("Create Game Room");
	    m_createRoomButton.setBounds(12, 7, 150, 30); // 크기 및 위치 조정
	    m_roomListPanel.add(m_createRoomButton);

	    // Enter Room 버튼 설정
	    m_enterRoomButton = new JButton("Enter Game Room");
	    m_enterRoomButton.setBounds(174, 7, 150, 30); // 크기 및 위치 조정
	    m_roomListPanel.add(m_enterRoomButton);

	    // 관리자 버튼 설정
	    adminButton = new JButton("관리자 모드 실행");
	    adminButton.setBounds(464, 7, 139, 30); // 크기 및 위치 조정
	    adminButton.setVisible(false); // 기본적으로 숨김
	    adminButton.setEnabled(false);
	    m_roomListPanel.add(adminButton);

	    // Room List ScrollPane 설정
	    m_scPaneRoomList = new JScrollPane(m_roomList,
	            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        m_scPaneRoomList.setBounds(5, 47, 598, 238); // 방 리스트 크기 및 위치 수정
	    m_roomListPanel.add(m_scPaneRoomList);

	    // Room List 데이터 설정
	    m_roomList.setListData(vc);

	    // 버튼 이벤트 리스너 추가
	    m_createRoomButton.addActionListener(event);
	    m_enterRoomButton.addActionListener(event);

	    return m_roomListPanel;
	}



	private JPanel generatorUserListPanel() 
	{
	    // User List Panel 레이아웃 설정
	    m_userListPanel.setLayout(null);
	    m_userListPanel.setBounds(12, 176, 200, 294); // 새 크기 및 위치 설정

	    // 프로필 사진 아이콘 설정
	    ImageIcon profileIcon = new ImageIcon("src/image/character/Kerby.png"); // 경로에 맞게 수정 필요
	    Image profileImage = profileIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // 크기 조정
	    m_profilePicLabel.setIcon(new ImageIcon(profileImage));

	    // 사용자 이름과 프로필 사진 배치

	    // User List ScrollPane 설정
	    m_scPaneUserList = new JScrollPane(m_userList,
	            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        m_scPaneUserList.setBounds(5, 10, 190, 274); // 유저 리스트 스크롤 위치 및 크기 수정
	    m_userListPanel.add(m_scPaneUserList);

	    // User List 데이터 설정
	    m_userList.setListData(vc);

	    return m_userListPanel;
	}
    
	private JPanel generatorChatWindowPanel() 
	{
	    // 로그 창 JScrollPane 설정
	    m_scPaneLogWin = new JScrollPane(m_logWindow,
	            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    m_logWindow.setEditable(false);
	    m_logWindow.setLineWrap(true);
	    m_scPaneLogWin.setBounds(12, 10, 594, 104); // 새 크기 설정
	    m_vScroll = m_scPaneLogWin.getVerticalScrollBar();
	    m_vScroll.addAdjustmentListener(new AdjustmentListener() {
	        public void adjustmentValueChanged(AdjustmentEvent e) {
	            if (!e.getValueIsAdjusting()) m_vScroll.setValue(m_vScroll.getMaximum());
	        }
	    });

	    // 로그 창 패널에 추가
	    m_logWinPanel.add(m_scPaneLogWin);

	    // 텍스트 입력 필드 위치 및 크기 조정
	    m_textInput.setBounds(12, 124, 448, 30); // 새 크기 설정
	    m_logWinPanel.add(m_textInput);

	    // 전송 버튼 위치 및 크기 조정
	    m_sendButton.setBounds(472, 124, 65, 30); // 새 크기 설정
	    m_logWinPanel.add(m_sendButton);

	    // 종료 버튼 위치 및 크기 조정
	    m_exitButton.setBounds(541, 124, 65, 30); // 새 크기 설정
	    m_logWinPanel.add(m_exitButton);

	    // 로그 창 패널 레이아웃 및 크기 설정
	    m_logWinPanel.setLayout(null);
	    m_logWinPanel.setBounds(224, 310, 618, 160); // 새 크기 설정

	    // 텍스트 입력 필드 이벤트 설정
	    m_textInput.setName("input");
	    m_textInput.addActionListener(event);

	    // 전송 버튼 이벤트 설정
	    m_sendButton.addActionListener(event);

	    return m_logWinPanel;
	    
	    
	}
	private JPanel generatorInfoPanel() 
	{
	    m_infoPanel.add(m_infoLabel);
	    m_infoLabel.setBounds(10, 10, 180, 140); // 크기와 위치 조정

	    m_infoPanel.setLayout(null);
	    m_infoPanel.setBounds(15, 5, 200, 161); // 패널 크기와 위치 조정

	    m_exitButton.addActionListener(event);

	    return m_infoPanel;
	}

	
	/** PanelInterface로부터 상속받고 있는 public 메소드입니다. (구현부) */
	public void setTextToLogWindow(String str) {
		m_logWindow.append(str);
	}

	public void setUserList(Vector<String> UserList) {
		m_userList.setListData(UserList);
	}

	public JList getUserList() {
		return m_userList;
	}

	public JList getJList() {
		return m_userList;
	}

	public void unShow() {
		this.setVisible(false);
	}


	public String getInputText() {
		return m_textInput.getText();
	}


	public void setTextToInput(String string) {
		m_textInput.setText(string);
	}


	public void setRoomList(Vector<String> roomList) {
		m_roomList.setListData(roomList);
	}


	public String getSelectRoom() {
		String temp = (String)m_roomList.getSelectedValue();
		return temp;
	}

	public void setTotalUser(Vector<String> userList) {
		userListFrame.setUserList(userList);
	}


	public void setUserListFrame(UserListFrame userListFrame) {
		this.userListFrame = userListFrame;
	}


	public void setPanel(PanelInterface panel) {
	
	}


	public int[] getFrameSize() {
		int[] size = {854,520};
		return size;
	}

	public void setPanel(GameLobbyInter panel) {
	}
	public void setPanel(LobbyGuiInter panel) {
	}
	public void setPanel(RoomGuiInter panel) {
	}
}

class RoomTableModel implements TableModel {
	

	public int getRowCount() {
		return 3;
	}

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getColumnName(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<?> getColumnClass(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCellEditable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getValueAt(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValueAt(Object arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}
	
}