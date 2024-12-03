package gui;

import gameClient.ClientInterface;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.io.File;
import java.awt.MediaTracker;

import java.awt.Image;

@SuppressWarnings("serial")
public class GameLobby extends JPanel implements GameLobbyInter, PanelInterface {

	private UserListFrame userListFrame;
	
	/*
	 * Component of Gamer Panel
	 */
	private JLabel m_roomInfo, m_userInfo1, m_userInfo2;
	
	private JPanel m_gamerPanel = new JPanel() {
		public void paint(Graphics g) {
			this.paintComponents(g);
		}
	};

	private JLabel m_UserLabel1, m_UserLabel2;

	/*
	 * Component of User List Panel
	 */
	private JPanel m_userListPanel = new JPanel() {
		public void paint(Graphics g) {
			this.paintComponents(g);
		}
	};

	private JList m_userList = new JList();

	/*
	 * Component of Chat Window
	 */
	private JPanel m_logWinPanel = new JPanel() {
		public void paint(Graphics g) {
			this.paintComponents(g);
		}
	};

	private JTextArea m_logWindow = new JTextArea(5, 20);

	private JTextField m_textInput = new JTextField();

	private ImageButton m_sendButton = new ImageButton("image/gameLobbySendButton.png", "SEND", "image/gameLobbySendButtonOver.png");

	private JScrollPane m_scPaneLogWin;
	private JScrollBar m_vScroll;

	private final int iUserImageWidth = 100;
	private final int iUserImageHeight = 130;
	/*
	 * Component of Info Panel
	 */
	private JPanel m_infoPanel = new JPanel() {
		public void paint(Graphics g) {
			this.paintComponents(g);
		}
	};

	private ImageButton m_exitButton = new ImageButton("image/gameLobbyExitButton.png", "나가기", "image/gameLobbyExitButtonOver.png");
	
	protected AbstractButton m_startButton;
	
	private Vector<String> vc = new Vector<String>();
	
	
	private ClientInterface client;
	private EventExecute event;
	private String kingCharacterName, challengerCharacterName;
	
	private boolean isRoomKing = true;
	/*
	 *  Constructor
	 */
	public GameLobby(ClientInterface client, boolean isRoomKing) {
		
		this.client = client;
		this.event = new EventExecute(this, this.client);
		this.isRoomKing = isRoomKing;
		
		execute();
		
		System.out.println("E.GameLobby001");
	}
	
	/** 선택한 캐릭터 이름을 받는 생성자 */
	public GameLobby(ClientInterface client, boolean isRoomKing, String characterName) {
		
		this.client = client;
		this.event = new EventExecute(this, this.client);
		this.isRoomKing = isRoomKing;
		
	    m_UserLabel1 = new JLabel();
	    m_UserLabel2 = new JLabel();
	    
		setCharacter(characterName, isRoomKing);
		
		execute();
		
		System.out.println("E.GameLobby001");
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Network FIve Eyes Ver. 1.0");
		Container cp = frame.getContentPane();
		cp.add(new GameLobby(null, true));
		
		frame.setSize(340,440);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		System.out.println("Exit");
	}
	
	private void execute() {
		add(generatorGamerPanel());
		add(generatorUserListPanel());
		add(generatorChatWindowPanel());
		add(generatorInfoPanel());
		setLayout(null);
	}
	
	public void paint(Graphics g) {
		ImageIcon icon = new ImageIcon(
				URLGetter.getResource("src/image/GameLobbyBg.png"));
		g.drawImage(icon.getImage(),0,0,null,null);
		this.paintComponents(g);
	}
	
	private JPanel generatorGamerPanel() {
		m_roomInfo = new JLabel();
		m_userInfo1 = new JLabel();
		m_userInfo2 = new JLabel();
	    
	    setCharacterLabel(this.isRoomKing);

	    m_gamerPanel.setLayout(null);
	    m_gamerPanel.setBounds(5, 5, 300, 245);
	    
	    m_gamerPanel.revalidate();
	    m_gamerPanel.repaint();

	    return m_gamerPanel;
	}
	
	private JPanel generatorUserListPanel() {
		m_userList.setListData(vc);

		m_userListPanel.setLayout(null);
		m_userListPanel.setBounds(270, 5, 230, 130);

		return m_userListPanel;
	}

	private JPanel generatorChatWindowPanel() {
		m_scPaneLogWin = new JScrollPane(m_logWindow,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		m_logWindow.setEditable(false);
		m_scPaneLogWin.setBounds(5,5,250,100);
		m_logWindow.setLineWrap(true);
		
		m_vScroll = m_scPaneLogWin.getVerticalScrollBar();
		m_vScroll.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if(!e.getValueIsAdjusting()) m_vScroll.setValue(m_vScroll.getMaximum());
				
			}
		});
		
		m_logWinPanel.add(m_textInput);
		m_textInput.setBounds(5, 110, 180, 30);
		m_logWinPanel.add(m_sendButton);
		m_sendButton.setBounds(195, 110, 60, 30);
		

		m_logWinPanel.add(m_scPaneLogWin);
		m_logWinPanel.setLayout(null);
		m_logWinPanel.setBounds(5, 250, 260, 200);
		
		m_textInput.addActionListener(event);
		m_sendButton.addActionListener(event);
		
		return m_logWinPanel;
	}

	private ImageIcon getChangeCharacterIcon(String characterName) { 
		String path = "src/image/character/Dedede.png";
	    
	    if ("Kawasaki".equals(characterName)) {
	    	path = "src/image/character/Kawasaki.png";
	    } else if ("Dedede".equals(characterName)) {
	    	path = "src/image/character/Dedede.png";
	    } else if ("BlueKerby".equals(characterName)) {
	    	path = "src/image/character/BlueKerby.png";
	    } else if ("Kerby".equals(characterName)) {
	    	path = "src/image/character/Kerby.png";
	    } else if ("MetaKnight".equals(characterName)) {
	    	path = "src/image/character/MetaKnight.png";
	    } else if ("Mike".equals(characterName)) {
	    	path = "src/image/character/Mike.png";
	    } else if ("WaddleD".equals(characterName)) {
	    	path = "src/image/character/WaddleD.png";
	    } else if ("YelloKerby".equals(characterName)) {
	    	path = "src/image/character/YelloKerby.png";
	    }
	    
		// 이미지 경로가 제대로 됐는지 확인
	    File file = new File(path);
	    if (!file.exists()) 
	    {
	        System.out.println("Image file not found: " + file.getAbsolutePath());
	    }
	    
	    return new ImageIcon(path);
	}

	
	/** Resizes an image to fit specified dimensions */
	private ImageIcon resizeImageIcon(ImageIcon icon, int width, int height) {	    
	    Image img = icon.getImage();
	    Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	    return new ImageIcon(resizedImg);
	}

	/** 캐릭터 라벨을 배치합니다. */
	private void setCharacterLabel(boolean isRoomKing)
	{
	    ImageIcon icon = null;
	    if(isRoomKing)
	    	icon = getChangeCharacterIcon(kingCharacterName);
	    else
	    	icon = getChangeCharacterIcon(challengerCharacterName);
	    
	    if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) 
	    {
	        System.out.println("Failed to load image: src/image/character/Dedede.png");
	    } else {
	        System.out.println("Image loaded successfully!");
	    }
	    icon = resizeImageIcon(icon, iUserImageWidth, iUserImageHeight);
	    
	    if(isRoomKing)
	    {
	    	m_UserLabel1.setIcon(icon);
	    	m_UserLabel1.setBounds(40, 50, 100, 130);
	    	m_UserLabel1.setOpaque(false); // 배경 투명 설정
	    	m_gamerPanel.add(m_UserLabel1);
	    }
	    else
	    {
	    	m_UserLabel2.setIcon(icon);
	    	m_UserLabel2.setOpaque(false); // 배경 투명 설정
	    	m_UserLabel2.setBounds(170, 50, 100, 130);
	    	m_gamerPanel.add(m_UserLabel2);
	    }
	}
	
	private JPanel generatorInfoPanel() {
		m_startButton = new ImageButton("image/startButton.png", "START!", "src/image/startButtonOver.png");
		m_infoPanel.add(m_startButton);
		m_startButton.setBounds(5,50,60,40);
		m_infoPanel.add(m_exitButton);
		m_exitButton.setBounds(5,95,60,40);
		
		m_infoPanel.setLayout(null);
		m_infoPanel.setBounds(260, 250, 230,200);
				
		m_startButton.addActionListener(event);
		m_exitButton.addActionListener(event);
				
		return m_infoPanel;
	}

	public void setClickable(boolean b) {
		m_startButton.setEnabled(b);
	}

	public void setTextToLogWindow(String str) {
		m_logWindow.append(str);
	}

	public void setUserList(Vector<String> userList) {
		Vector<String> temp = new Vector<String>();
		
		setRoomKing(userList.get(0));

		if(userList.size() < 2)
			setChallenger("Pleas wait....");
		else
			setChallenger(userList.get(1));
		
		int i = 0;
		for (String user : userList)
			if (i++ > 1) temp.add(user);
		
		m_userList.setListData(temp);
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

	public void setRoomKing(String name) {
		m_userInfo1.setText(name);
		this.repaint();
	}

	/** 캐릭터 이름을 지정합니다. */
	public void setCharacter(String characterName, boolean isRoomKing)
	{
		if(isRoomKing)
			this.kingCharacterName = characterName; // 선택된 캐릭터 이름 저장
		else
			this.challengerCharacterName = characterName;
		
		setCharacterLabel(isRoomKing);
	    // UI 업데이트
	    repaint();
	}
	
	public String getCharacter(boolean isRoomKing)
	{
		if(isRoomKing)
			return this.kingCharacterName; // 선택된 캐릭터 이름 저장
		else
			return this.challengerCharacterName;
	}
	
	public void setChallenger(String name) {
		m_userInfo2.setText(name);
		this.repaint();
	}

	public void setGameRoomInf(String info) {
		m_roomInfo.setText(info);
	}

	public void setStartButton(boolean isRoomKing) {
		if (isRoomKing) {
			m_startButton.setText("START");
			
			this.setButtonEnable(true);
			
		} else {
			m_startButton.setText("START");
		}
		
	}

	public void setButtonEnable(boolean clickable) {
		m_startButton.setEnabled(true);
		System.out.println("Clikable******************");
	}

	public void setTotalUser(Vector<String> userList) {
		userListFrame.setUserList(userList);
	}

	public void setUserListFrame(UserListFrame userListFrame) {
		this.userListFrame = userListFrame;
	}
	
	public String getGameInfo() {
		return m_roomInfo.getText() + "|" + m_userInfo1.getText() + "|" + m_userInfo2.getText() 
				+ "|" + this.kingCharacterName + "|" + this.challengerCharacterName;
	}

	public void setPanel(PanelInterface panel) {
		
	}

	public int[] getFrameSize() {
		int size[] = {340,440};
		return size;
	}

	public void setPanel(GameLobbyInter panel) {
	}
	public void setPanel(LobbyGuiInter panel) {
	}
	public void setPanel(RoomGuiInter panel) {
	}
}