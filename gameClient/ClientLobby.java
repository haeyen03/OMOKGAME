package gameClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import gui.GameLobby;

import gui.GameLobbyInter;
import gui.GameRoomGui;
import gui.GuiInterface;
import gui.InfoFrame;
import gui.LobbyGui;
import gui.LobbyGuiInter;
import gui.LoginPanel;
import gui.MainFrame;
import gui.PanelInterface;
import gui.RoomGuiInter;
import gui.Dainn.CharacterSelection;
import dataBase.DataBase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import protocolData.*;
import gui.KakaoTalk.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import gui.AdminFrame;
import protocolData.RequestData;
import protocolData.RequestListener;

public class ClientLobby implements ClientInterface {
	
	public static final double SERVER_VERSION = 1.01;
	private final String serverIP = "127.0.0.1";
	
	private String name;

	private	DataBase	m_mySql;
	
	private GuiInterface m_Frame;

	private LobbyGuiInter m_lobby; 

	private GameLobbyInter m_gameLobby;

	private RoomGuiInter m_gameRoom;
	private InfoFrame infoFrame;
	private Socket socket;

	private ObjectOutputStream out;

	private ObjectInputStream netIn;

	private Protocol data;

	private String receiver;

	private	boolean	isPlaying;

	private boolean isInRoom = false;
	private boolean isRoomKing;
	private boolean isLogin = false;

    private ChattingListener 	chattingListener ;  // 시스템 메시지 리스너
    private RequestListener 	requestListener ;  // 시스템 메시지 리스너

    // 채팅 메시지 리스너 설정
    public void setChattingListener(ChattingListener listener) {
        this.chattingListener = listener;
    }
    
    public void setRequestListener(RequestListener listener) {
        this.requestListener = listener;
    }
    
	public ClientLobby(String id) {
		System.out.println("start! serverIP " + serverIP);
		
		// GUI 초기화
		m_mySql = new DataBase();
		m_Frame = new MainFrame(this);
		m_Frame.setPanel(new LoginPanel(this, m_mySql));

		
		// 로그인 확인 루프
		try {
			while(!isLogin) Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// 소켓 초기화
		try {
			socket = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(serverIP, 9999);
			socket.connect(socketAddress, 3_000);
			out = new ObjectOutputStream(socket.getOutputStream());
			netIn = new ObjectInputStream(socket.getInputStream());

			m_lobby = new LobbyGui(this, m_mySql);
			m_Frame.setPanel(m_lobby);
			
	        /** @add - 관리자 모드 버튼 추가 */
            JButton adminButton = new JButton("관리자 모드");
            adminButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new AdminFrame();  // 관리자 모드 창 열기
                }
            });

            // m_lobby를 LobbyGui로 캐스팅하여 버튼 추가
            ((LobbyGui) m_lobby).add(adminButton, BorderLayout.NORTH);
            ((LobbyGui) m_lobby).revalidate();
            ((LobbyGui) m_lobby).repaint();
			
			sendMessage(""+SERVER_VERSION, ChatData.ENTER);

			working();

		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showConfirmDialog(null, 
					"서버가 연결 되어 있지 않습니다.", "Notice!", JOptionPane.DEFAULT_OPTION);
			System.exit(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

	}

	public void working() {
		try {
			while (true) {
				data = (Protocol) netIn.readObject(); // in.readObject는 항상 대기 상태로 유지되어 데이터가 도착하면 받아옵니다.
				System.out.println("From Server : " + data);

				if (data instanceof ChatData)
					analysisChatData((ChatData) data);

				else if (data instanceof LobbyData)
					analysisLobbyData((LobbyData) data);

				else if (data instanceof GameLobbyData)
					analysisGameLobbyData((GameLobbyData) data);

				else if (data instanceof GameData)
					analysisGameData((GameData) data);

				else if (data instanceof RequestData)
					analysisRequestData((RequestData) data);
				
				else {
					System.out.println("E.ClientLobby01");
				}

			}

		} catch (ClassNotFoundException e) {
			System.out.println("Class not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("data read fail");
			e.printStackTrace();
		}
	}
    
	/** @add - 로그인(set_Name에서 사용함) */
	public boolean validateLogin(String id, String password) {
	    String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
	    try (Connection conn = m_mySql.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setString(1, id);
	        stmt.setString(2, password);
	        ResultSet rs = stmt.executeQuery();
	        return rs.next() && rs.getInt(1) > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	private void analysisChatData(ChatData data) {
		switch (data.getProtocol()) {

		case ChatData.ENTER:
			data.setMessage("님이 접속 하였습니다.");
			setTextToLog(data);
			break;
			
		case ChatData.LOGIN_CHECK:
			JOptionPane.showConfirmDialog(null, 
					"E.ClientLobby02 \n http://26.192.38.68/test/b.html E.ClientLobby03", "Notice!", JOptionPane.DEFAULT_OPTION);
			System.exit(0);
			break;
			
		case ChatData.EXIT:
			setTextToLog(data);
			break;

		case ChatData.MESSAGE:
			setTextToLog(data);
			break;
			
		case ChatData.SEND_USER_LIST:
			System.out.println("in send user list");
			m_Frame.setUserList(data.getUserList());
			break;
			
		case ChatData.SEND_MULTICAST:
		case ChatData.SEND_EMOJI:
		case ChatData.SEND_FILE:
			// ClientLobby에서 in.readObject로 받은 데이터를 리스너를 통해 다른 객체에 넘겨줍니다.
			if(null != chattingListener)
				chattingListener.onChatDataReceived(data);
			break;

		default:
			setTextToLog(data);
			break;
		}
	}

	private void analysisLobbyData(LobbyData data) {
		switch (data.getProtocol()) {

		case LobbyData.CREATE_ROOM:
			/*
			 * create Room...
			 */
			setRoomKing(true);
			
			changePanel(new GameLobby(this, true, data.getCharacterName(true)));
			setGameLobbyInstance();
			m_gameLobby.setRoomKing(name);
			m_gameLobby.setStartButton(true);

			m_gameLobby.setGameRoomInf("[" + data.getRoomNumber() + "번 방] "
					+ data.getRoomName());

			break;

		case LobbyData.ENTER_TO_ROOM: // 방 입장 시 챌린저&방장 모두에게 알람이 옴 (Server의 sendTo 덕분)
			
			// 혹시 방장이면 기존 로비에서 챌린저 셋팅
			if(isRoomKing())
			{
				m_gameLobby.setChallenger(name);
				m_gameLobby.setCharacter(data.getCharacterName(false), false); // 챌린저 셋팅
				// 챌린저의 캐릭터도 설정 (오른쪽에 배치하기 위함)
			}
			// 챌린저라면 로비 생성
			else
			{
				changePanel(new GameLobby(this,false, data.getCharacterName(false)));
				setGameLobbyInstance();
				m_gameLobby.setChallenger(name);
				m_gameLobby.setStartButton(false);
				m_gameLobby.setCharacter(data.getCharacterName(true), true);
			}
	
			Vector<String> temp = new Vector<String>();

			for (String user : data.getUserList())
				temp.add(user);

			m_gameLobby.setUserList(temp);

			m_gameLobby.setGameRoomInf("[" + data.getRoomNumber() + "번 방] "
					+ data.getRoomName());
			
			showEnterMessage(data);
			
			break;

		case LobbyData.SEND_ROOMLIST:
			/*
			 * send Room LIst...
			 */
			((RoomGuiInter) m_Frame).setRoomList(data.getRoomList());

			break;

		case LobbyData.EXIT_GAME:
			/*
			 * when Room king exit this room.
			 */
			break;

		default:
			break;
		}

	}

	private void showEnterMessage(Protocol data) {
		m_Frame.setTextToLogWindow("[ " + data.getName() + " ] 님이 입장 하셨습니다.\n");
	}

	private void showExitMessage(Protocol data) {
		m_Frame.setTextToLogWindow("[ " + data.getName() + " ] E.ClientLobby08 \n");
	}

	private void analysisGameLobbyData(GameLobbyData data) {
		switch (data.getProtocol()) {

		case GameLobbyData.CANCEL_READY:
			/*
			 * ready is canceled...
			 */
			m_gameLobby.setButtonEnable(false);

			break;

		case GameLobbyData.EXIT_ROOM:
			/*
			 * exit the room...
			 */
			JOptionPane.showConfirmDialog(null, "Room Master exit this room!",
					"Notice!", JOptionPane.DEFAULT_OPTION);

			isInRoom = false;
			changePanel(new LobbyGui(this));
			setLobbyInstance();

			break;

		case GameLobbyData.GAME_READY:
			/*
			 * All gamer is ready... (only roomking execute...)
			 */
			m_gameLobby.setButtonEnable(true);

			break;

		case GameLobbyData.GAME_START:
			/*
			 * game start!!
			 */
			
			String GameInfo = m_gameLobby.getGameInfo();
			// 
			StringTokenizer token = new StringTokenizer(GameInfo, "|");
			
			token.nextToken();
			token.nextToken();
			token.nextToken();
			token.nextToken(); // 분명 5개 저장했는데 왜 6개인지 모르겠음.
			
			
			String kingCharacterName = token.nextToken();
			String challengerCharacterName = token.nextToken();
		    
			changePanel(new GameRoomGui(this, kingCharacterName, challengerCharacterName));
			setGameRoomGui();

			setTextToLog(data);
			
			m_gameRoom.setGameRoomInfo(GameInfo);

			break;

		default:
			break;
		}

	}

	private void analysisGameData(GameData data) {

		switch (data.getProtocol()) {

		case GameData.EXIT_THEGAME:
			/*
			 * exit the game
			 */
			setRoomKing(false);
			System.out.println("##### gameData in");
			
			if (data.isBlack()) {
				changePanel(new GameLobby(this, true));
				setGameLobbyInstance();
				m_gameLobby.setStartButton(false);
				
				JOptionPane.showConfirmDialog(null, "Room Master exit this Game!",
						"Notice!", JOptionPane.DEFAULT_OPTION);
			
			} else {
				showExitMessage(data);
			}
			System.out.println("##### gameData out");
			break;

		case GameData.SEND_STONE_LOCATION:
			/*
			 * send stone location..
			 */
			m_gameRoom.drawStone(data.getStoneLocation(), data.isBlack());
			break;

		case GameData.SEND_RESULT:
			/*
			 * send Victory...
			 */
			if (data.isBlack()) 
			{
				JOptionPane.showConfirmDialog(null, "흑돌의 승리!!", "Notice!",
						JOptionPane.DEFAULT_OPTION);
				
				if(isRoomKing())
				{
					updateGameCount(getName(), true);
				}
				else
				{
					updateGameCount(getName(), false);
				}
				
			}
				
			else
			{
				JOptionPane.showConfirmDialog(null, "백돌의 승리!", "Notice!",
						JOptionPane.DEFAULT_OPTION);
				
				if(isRoomKing())
				{
					updateGameCount(getName(), false);
				}
				else
				{
					updateGameCount(getName(), true);
				}
			}
			
			if(isRoomKing())
				sendMessage("", GameData.SEND_RESULT);				
			
			newGame();
			
			break;
			
		case GameData.REQUEST_RETURN:
			
			// inner switch
			switch (JOptionPane.showConfirmDialog(null, 
					"무르기를 해줍니까?", "Notice!", JOptionPane.YES_NO_OPTION)) {
			
			case 0:
				sendMessage("YES", GameData.RESPONSE_RETURN);
				break;

			case 1:
				sendMessage("NO", GameData.RESPONSE_RETURN);
				break;

			default:
				break;
			}
			// inner switch
			
			break;
			
			
		case GameData.RESPONSE_RETURN:
			m_gameRoom.backOneStep(data.isBlack() ? 2 : 1);
			setTextToLog("E.ClientLobby12");
			break;

		case GameData.SEND_GAME_MESSAGE:
			infoFrame = new InfoFrame(data.getMessage());
			infoFrame.setVisible(true);
			
			break;

			
		default:
			break;
		}

	}

	private void analysisRequestData(RequestData data) 
	{
		switch (data.getProtocol()) 
		{			
		case RequestData.CHECK_PLAY:
		    if (data.isPlaying()) 
		    {
		        changePanel(new GameRoomGui(this, data.getRoomName())); // 관전 모드 시작
		        setGameRoomGui();
		        setTextToLog(data);
		    }
			else
			{
				new CharacterSelection(this, data.getRoomName(), false);
			}	
		    break;
		    
		case RequestData.SEND_GAMEINFO:
			// 현재 입장한 방이 play 시 응답을 요청한 관전자에게만 응답이 오므로 상태 구분 안하고 그냥 데이터 보냄.
			if(null != requestListener)
				requestListener.onRequestDataReceived(data);
			
			break;
		}

		
	}
	
	private void updateGameCount(String userId, boolean isWin) {
	    String query = isWin ? 
	            "UPDATE usertable SET win_count = win_count + 1 WHERE id = ?" :  // win_count 증가
	            "UPDATE usertable SET lose_count = lose_count + 1 WHERE id = ?";  // lose_count 증가
	    
	    try (Connection connection = m_mySql.getConnection();  // 데이터베이스 연결
	         PreparedStatement pstmt = connection.prepareStatement(query)) {

	        // id 값을 쿼리에 바인딩
	        pstmt.setString(1, userId);
	        
	        // 쿼리 실행
	        int rowsUpdated = pstmt.executeUpdate();
	        
	        if (rowsUpdated > 0) {
	            if (isWin) {
	                System.out.println("Win count for user " + userId + " has been incremented.");
	            } else {
	                System.out.println("Lose count for user " + userId + " has been incremented.");
	            }
	        } else {
	            System.out.println("No user found with id: " + userId);
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Error occurred while updating game count.");
	    }
	}


	
	private void newGame() {
		m_gameRoom.newGame();
	}

	private void setTextToLog(Protocol data) {
		m_Frame.setTextToLogWindow("[ " + data.getName() + " ] " + data.getMessage()
				+ "\n");
	}
	
	private void setTextToLog(String str) {
		m_Frame.setTextToLogWindow("[ " + "Error" + " ] " + str
				+ "\n");
	}

	private void setGameRoomGui() {
		this.m_gameRoom = (RoomGuiInter) this.m_Frame;
	}

	private void setGameLobbyInstance() {
		this.m_gameLobby = (GameLobbyInter) this.m_Frame;
	}

	private void setLobbyInstance() {
		this.m_lobby = (LobbyGuiInter) this.m_Frame;
	}

	/*
	 * User send when All Messsage call
	 */
	public void sendMessage(String message, short state) 
	{
		try 
		{
			if (state == ChatData.MESSAGE_SLIP) 
			{
				data = new ChatData(receiver, name, message,
						ChatData.MESSAGE_SLIP);
				out.writeObject(data);

			} 
			else if (state == LobbyData.CREATE_ROOM) 
			{
				// message is Room name.
				isInRoom = true;

				data = new LobbyData(name, message, state);
				out.writeObject(data);

			} 
			else if (state == LobbyData.ENTER_TO_ROOM) {
				// message is Room name.
				isInRoom = true;

				data = new LobbyData(name, message, state);
				out.writeObject(data);

			} 
			else if (state == GameLobbyData.EXIT_ROOM) {
				isInRoom = false;

				data = new GameLobbyData(name, message, state);
				data.setName(name);

				changePanel(new LobbyGui(this));
				setLobbyInstance();
				out.writeObject(data);

			} 
			else if (state == GameLobbyData.GAME_START) {
				data = new GameLobbyData(name, message, state);
				out.writeObject(data);

			} 
			else if (state == GameLobbyData.GAME_READY) {
				data = new GameLobbyData(name, message, state);
				out.writeObject(data);

			} 
			else if (state == GameLobbyData.CANCEL_READY) {
				data = new GameLobbyData(name, message, state);
				out.writeObject(data);

			} 
			else if (state == GameData.EXIT_THEGAME) {
				changePanel(new GameLobby(this, true));
				setGameLobbyInstance();
				m_gameLobby.setStartButton(true);

				data = new GameData("", state);
				out.writeObject(data);

			} 
			else if (state == ChatData.SEND_TOTAL_USER) {
				data = new ChatData(name, null, state);
				out.writeObject(data);
				
			} 
			else if (state == GameData.REQUEST_RETURN) {
				data = new GameData("", state);
				out.writeObject(data);
				
			} 
			else if (state == GameData.RESPONSE_RETURN) {
				data = new GameData(message, state);
				out.writeObject(data);
				
			} 
			else if (state == GameData.SEND_RESULT) {
				data = new GameData(message,state);
				out.writeObject(data);
				
			} 
			else if(state == RequestData.CHECK_PLAY)
			{
				data = new RequestData(message, state);
				out.writeObject(data);
			}
			else if(state == RequestData.SEND_GAMEINFO)
			{
				data = new RequestData(message, state);
				out.writeObject(data);
			}
			else {
				data = new ChatData(name, message, state);
				out.writeObject(data);
			}

		} catch (IOException e) {
			System.out.println("data write fail!");
			e.printStackTrace();
		}
	}

	/** 프로토콜을 매개변수로 받아 outputstream에 데이터를 작성하는 오버로딩된 함수입니다. */
	public void sendMessage(Protocol protocol) 
	{
		try
		{
			if(protocol instanceof ChatData chatData)
			{
				if(ChatData.SEND_MULTICAST == chatData.getState())
				{
					out.writeObject(chatData);
				}
				else if(ChatData.SEND_EMOJI == chatData.getState())
				{
					out.writeObject(chatData);
				}
				else if(ChatData.SEND_FILE == chatData.getState())
				{
					out.writeObject(chatData);
				}
			}
			
		}catch (IOException e) {
			System.out.println("data write fail!");
			e.printStackTrace();
		}

	}
	
	public void sendMessage(int[] location) {
		this.data = new GameData(location, GameData.SEND_STONE_LOCATION);

		try {
			out.writeObject(this.data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return this.name;
	}
	
	/*
	 * When Enter the Game Lobby, this method execute.
	 */
	public void sendSlip(String to, String message, short state) {
		receiver = to;
		sendMessage(message, state);
	}

	public GuiInterface getGui() {
		return m_Frame;
	}

	public void changeGui(GuiInterface gui) {
		m_Frame.unShow();
		m_Frame = gui;
	}
	
	public void changePanel(GuiInterface panel) {
		if (panel instanceof LoginPanel)
			m_Frame.setPanel((PanelInterface)panel);
		else if (panel instanceof LobbyGui)
			m_Frame.setPanel((LobbyGuiInter)panel);
		else if (panel instanceof GameLobby)
			m_Frame.setPanel((GameLobbyInter)panel);
		else if (panel instanceof GameRoomGui)
			m_Frame.setPanel((RoomGuiInter)panel);
		else {
			System.out.println("E.ClientLobby15");
		}
		
	}

	public static void main(String[] args) {
		try {
			new ClientLobby("test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSlipTo(String slipTo) {
		this.receiver = slipTo;
	}

	public boolean isRoomKing() {
		return isRoomKing;
	}
	

	public void setRoomKing(boolean isRoomKing) {
		this.isRoomKing = isRoomKing;
	}

	public void setName(String text) {
		this.name = text;
		isLogin = true;
	}
	
	public void setName(String id, String password) {
        if (validateLogin(id, password)) {
            this.name = id;
            isLogin = true;
        }
    }
}
