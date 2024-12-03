package gameServer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import java.util.List;  // List를 사용하기 위해 필요
import java.util.ArrayList;  // ArrayList를 사용하기 위해 필요


import protocolData.*;
import protocolData.RequestData;

public class GameServer extends Thread {
	private boolean flag = true;
	private Protocol data;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	protected String name;
	private ServerInterface server;
	private LobbyInterface lobby;
	private GameRoomInterface room;
	private int userLocation, roomNumber;

	private boolean isReady = false;
	private boolean isLogin = false;
	private boolean isMyTurn = false;
	
	public GameServer(Socket socket, LobbyInterface lobby) {
		System.out.println("Game Server New!!");
		LogFrame.print("Game Server New!!");

		setUserLocation(ServerInterface.LOBBY);

		this.lobby = lobby;
		this.name  = socket.getInetAddress().toString();
		setLobbyInstance();

		this.socket = socket;
		try 
		{
			in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) 
		{
			//e.printStackTrace();
			System.out.println("-- ["+name+"]");
			lobby.subSocket(name);

            if( this.socket != null) 
            {
	            try 
	            {
	                this.socket.close();
	            } catch (IOException e1) 
	            {
	                e1.printStackTrace();
	            }
            }
			return;
        }

		/** 스레드는 실행 대기 상태에 들어갑니다. CPU 스케줄링에 의해 점유 시 실행 상태 run()에 진입합니다. */
		this.start();
	}

	/** 스레드가 실행할 코드입니다. */
	@Override
	public void run() {
		while (flag) {

			try {
				data = (Protocol) in.readObject();

				System.out.println("=====================================");
				LogFrame.print("=====================================");
				
				System.out.println("From Client : " + data);
				LogFrame.print("From Client : " + data);
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();

			} catch (EOFException e) {
				continue;

			} catch ( SocketException e ) {
				System.out.println("E.GameServer01");
				e.printStackTrace();
				flag = false;
				
				exitUser();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				break;

			} 

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
			}
		}

		System.out.println("Exit");

		lobby.printState();

	}

	protected void sendMessage(Protocol data) throws Exception {
		try {
			out.writeObject(data);
		} catch ( NullPointerException e ) {
			System.out.println("E.GameServer02");
			throw new Exception("E.GameServer03. NullPointerException!! ");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("E.GameServer04");
			throw new Exception("E.GameServer05" + e );
		}
	}

	protected void stopThisThread() {
		this.flag = false;
	}

	/*
	 * when First enter Game or exit GameRoom call!
	 */
	private void setRoomUserList() {
		sendUserList();
		sendRoomList();
	}

	private void analysisChatData(ChatData data) 
	{

		switch (data.getProtocol()) 
		{

		case ChatData.ENTER:
			String version = data.getMessage(); // 데이터에 저장된 버전 정보를 가져옴.
			this.name = data.getName();
			
			// 만약, 버전이 일치하지 않으면 새로운 ChatData를 생성하여 다시 실행합니다.
			if (!version.equals(""+MainServer.VERSION)) 
			{
				analysisChatData(new ChatData(data.getName(), null, ChatData.LOGIN_CHECK));
				System.out.println("GameServer - 버전이 일치하지 않습니다.");
			
			} 
			// 버전 일치 시 로그인 성공 및 서버에 브로드캐스팅으로 알립니다.
			else 
			{
				setLogin(true);
				server.broadcasting(data);
				setRoomUserList();
				System.out.println("GameServer - 버전이 일치합니다. 입장합니다.");
			}
			
			break;

		case ChatData.LOGIN_CHECK:
			try {
				sendMessage(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

		case ChatData.EXIT:
		
			exitUser( this.data );
			break;

		case ChatData.MESSAGE:
			/*
			 * send message
			 */
			server.broadcasting(data);
			break;

		case ChatData.MESSAGE_SLIP:
			/*
			 * send Slip message
			 */
			server.sendSlip(data);
			break;

		case ChatData.SEND_TOTAL_USER:
			// client request userlist.
			Vector<String> list = new Vector<String>();

			for (GameServer temp : server.getUserList().getCollection())
				list.add(temp.getUserName());

			data.setUserList(list);
			try {
				sendMessage(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		/** 채팅 유저에게 멀티캐스팅으로 데이터를 전송합니다. (사실 1대1채팅, 유니캐스트임)*/
		case ChatData.SEND_MULTICAST:
		case ChatData.SEND_EMOJI:
		case ChatData.SEND_FILE:
			server.multicasting(data);
			break;

		default:
			break;
		}

	}

	private void exitUser() {
		ChatData data = new ChatData(name, "exit", ChatData.EXIT );
		exitUser( data );
	}
	
	private void exitUser( Protocol data ) {
		if(isLogin) {
			server.broadcasting(data);
		}
			server.subSocket(data.getName());
			sendUserList();

		if (getUserLocation() != ServerInterface.LOBBY)
			analysisGameLobbyData(new GameLobbyData(data.getName(), null,
					GameLobbyData.EXIT_ROOM));

		stopThisThread();
	}
	
	
	/*
	 * When client is First Connetion...
	 */
	protected void sendUserList() {
		System.out.println("In Method[sendUserList()]");
		LogFrame.print("In Method[sendUserList()]");
		
		ChatData data = new ChatData(null, null, ChatData.SEND_USER_LIST);
		data.setUserList(server.getStringUser());
		server.broadcasting(data);
	}

	protected void sendRoomList() {
		server.broadcasting(new LobbyData(lobby.getRoomListAsString(),
				LobbyData.SEND_ROOMLIST));
	}

	private void analysisLobbyData(LobbyData data) {
		switch (data.getProtocol()) {

		case LobbyData.CREATE_ROOM:
			setUserLocation(ServerInterface.IN_GAME_ROOMKING);

			String[] roomData = data.getRoomName().split("\\|"); // "|" 기준으로 데이터 분리
		    String roomName = roomData[0]; // 방 이름
		    String roomKingCharacter = roomData[1]; // 선택된 캐릭터 이름
		    
			this.room = new GameRoom(roomName, roomKingCharacter, true, lobby.getUserList());
			room.setCharacterName(roomKingCharacter, true);
			
			lobby.addRoom(room);
			roomNumber = room.getNumber();
			setReady(true); // roomKing is always ready...

			setRoomUserList();

			setRoomInstance(room);

			data.setRoomNumber(roomNumber);
			data.setCharacterName(roomKingCharacter, true);
			
			try {
				sendMessage(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		case LobbyData.ENTER_TO_ROOM:
			/*
			 * enter to room....
			 */
			String[] Data = data.getRoomName().split("\\|"); // "|" 기준으로 데이터 분리
		    String ChallengerCharacter = Data[1]; // 선택된 캐릭터 이름
		    
		    room = lobby.getSelectedRoom(Data[0]); // Data[0]은 roomName
			room.setCharacterName(ChallengerCharacter, false);
			
			int userCount = room.getUserCounter();

			if (userCount < 2) {
				setUserLocation(ServerInterface.IN_GAME_CRHARANGER);

				sendUserList();

				roomNumber = room.getNumber();
				setRoomInstance(room);

				data.setUserList(room.getStringUser());
				data.setRoomNumber(roomNumber);
				
				/** CREATE_ROOM 시 방장은 서버 GameRoom에 자신의 캐릭터명을 저장해둠.
				 * ENTER_TO_ROOM으로 양쪽 다 데이터를 전달할 때 서로의 정보를 알아야함으로 CRATE 시 저장해둔 room 정보로 데이터 셋팅
				 */
				data.setCharacterName(room.getCharacterName(true), true);
				data.setCharacterName(ChallengerCharacter, false);
				
				try {
					sendMessage(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				room.sendTo(ServerInterface.IN_GAME_ROOMKING, data);
				sendUserList();

			} else {
				room = null;

				try {
					sendMessage(new ChatData("Error", "방 생성 오류!.", ChatData.MESSAGE));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;

		case LobbyData.SEND_ROOMLIST:
			/*
			 * send Room LIst...
			 */
			break;

		case LobbyData.EXIT_GAME:
			/*
			 * exit Game;;;
			 */
			break;

		default:
			break;
		}

	}

	private void setRoomInstance(GameRoomInterface room) {
		this.room = room;
		this.server = room;
	}

	private void setLobbyInstance() {
		this.room = null;
		this.server = lobby;
	}

	private void analysisGameLobbyData(GameLobbyData data) {
		switch (data.getProtocol()) {

		case GameLobbyData.CANCEL_READY:
			/*
			 * ready is canceled...
			 */
			setReady(false);
			room.sendTo(ServerInterface.IN_GAME_ROOMKING, data);

			break;

		case GameLobbyData.EXIT_ROOM:
			/*
			 * exit the room...
			 */

			if (userLocation == ServerInterface.IN_GAME_ROOMKING) {
				lobby.subRoom(roomNumber);
				setUserLocation(ServerInterface.LOBBY);
				server.broadcasting(data);
				room.exitRoomMaster(data);

			} else {
				setUserLocation(ServerInterface.LOBBY);
			}

			setLobbyInstance();

			sendUserList();
			setRoomUserList();

			break;

		case GameLobbyData.GAME_READY:
			/*
			 * game is ready...
			 */
			setReady(true);

			if (room.isAllReady())
				room.sendTo(ServerInterface.IN_GAME_ROOMKING, data);

			break;

		case GameLobbyData.GAME_START:
			/*
			 * game start!!
			 */

			setMyTurn(true);

			data.setMessage("Game Start!");
			data.setName("NOTICE");

			room.broadcasting(data);
			sendUserList();
			room.setStart(true);
			room.setPlaying(true);

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
			if (this.getUserLocation() == ServerInterface.IN_GAME_ROOMKING) {
				room.newGame();

				server.broadcasting(new GameData(name, true,
						GameData.EXIT_THEGAME));

				sendUserList();

			} else {
				server.broadcasting(new GameData(name, false,
						GameData.EXIT_THEGAME));
				sendUserList();
			}
			setUserLocation(ServerInterface.LOBBY);

			break;

		case GameData.SEND_STONE_LOCATION:
			/*
			 * send stone location..
			 */
			if (!room.isStart())
				room.setStart(true);

			if (isMyTurn()) {
				// Select Stone Color.
				if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING)
					data.setBlack(true);
				else
					data.setBlack(false);

				boolean isWin = room.addStone(data.getStoneLocation(), data
						.isBlack());

				moveTurn();

				server.broadcasting(data);

				// when anyone win the game, this send victory message.
				if (isWin) {
					sendVictory(data.isBlack());
					room.setStart(false);
				}
			} else { // not my turn.
				// send Warring!!
				System.out.println("============= no TURN!!!  =============");
				LogFrame.print("============= no TURN!!!  =============");
				if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING)
					room.sendTo(ServerInterface.IN_GAME_ROOMKING, new ChatData(
							"Error", "내 턴이 아닙니다!", ChatData.MESSAGE));
				else
					room.sendTo(ServerInterface.IN_GAME_CRHARANGER, new ChatData(
							"Error", "내 턴이 아닙니다!.", ChatData.MESSAGE));
			}

			break;

		case GameData.SEND_RESULT:
			/*
			 * After Victory, then restart!! install(?)...
			 */
			room.newGame();

			break;

		case GameData.REQUEST_RETURN:
			if(isMyTurn()) {
				if (room.isStart()) {

					if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING) {
						room.sendTo(ServerInterface.IN_GAME_CRHARANGER, data);
						room.sendTo(ServerInterface.IN_GAME_ROOMKING, new ChatData(
							"E.GameServer12", "E.GameServer13", ChatData.MESSAGE));


					} else {
						room.sendTo(ServerInterface.IN_GAME_ROOMKING, data);
						room.sendTo(ServerInterface.IN_GAME_CRHARANGER,
								new ChatData("E.GameServer13", "E.GameServer14",
										ChatData.MESSAGE));

					}
				}
			} else {
				System.out.println("E.GameServer15");
				if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING)
					room.sendTo(ServerInterface.IN_GAME_ROOMKING, new ChatData(
						"E.GameServer16", "E.GameServer17", ChatData.MESSAGE));
				else
					room.sendTo(ServerInterface.IN_GAME_CRHARANGER, new ChatData(
						"E.GameServer18", "E.GameServer19", ChatData.MESSAGE));
			}

			break;

		case GameData.RESPONSE_RETURN:
			if (data.isReturn()) {
				int flag = isMyTurn() ? 1 : 2;
				room.subLastStone(flag);

				if (flag == 2)
					data = new GameData(name, true, GameData.RESPONSE_RETURN);
				else
					moveTurn();
				
				room.broadcasting(data);

			} else if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING)
				room.sendTo(ServerInterface.IN_GAME_CRHARANGER, new ChatData(
						"E.GameServer20", "E.GameServer21", ChatData.MESSAGE));
			else
				room.sendTo(ServerInterface.IN_GAME_ROOMKING, new ChatData(
						"E.GameServer22", "E.GameServer23", ChatData.MESSAGE));
			;

			break;

		case GameData.SEND_GAME_MESSAGE:

			break;

		default:
			System.out.println("Error!!!!! : switch loop out!!!");
			break;
		}

	}

	/** 응답 */
	private void analysisRequestData(RequestData data) 
	{
		String roomName = data.getRoomName();
        GameRoomInterface room = lobby.getRoomManager().getRoomByName(roomName);
        boolean isPlaying = room.isPlaying();
        RequestData responseData = null;
        
		switch (data.getProtocol()) 
		{
		case RequestData.CHECK_PLAY:
			responseData = new RequestData(roomName, RequestData.CHECK_PLAY);
			responseData.setPlaying(isPlaying);

	        try {
	            out.writeObject(responseData);  // 응답 객체를 클라이언트에게 전송
	            out.flush();  // 전송한 데이터를 강제로 보내기
	            
	            //sendMessage(responseData);  // 클라이언트로 응답 전송
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
			break;
			
		case RequestData.SEND_GAMEINFO:
	        List<int[]> blackStoneLocations = room.getBlackStoneLocations();  // 이미 놓인 흑돌 좌표들
	        List<int[]> whiteStoneLocations = room.getWhiteStoneLocations();  // 이미 놓인 백돌 좌표들       
	        
	        // RequestData 응답 준비
	        responseData = new RequestData(roomName, RequestData.SEND_GAMEINFO);
	        responseData.setPlaying(isPlaying);
	        responseData.setBlackStoneLocations(blackStoneLocations);
	        responseData.setWhiteStoneLocations(whiteStoneLocations);
	        responseData.setCharacterName(room.getCharacterName(true), true); // king
	        responseData.setCharacterName(room.getCharacterName(false), false); // challenger

	        setRoomInstance(room);
	        roomNumber = room.getNumber(); // 돌 두면 같은 방 번호의 클라이언트에게 알림을 주기 때문에 설정해야함.
	        
	        try {
	            out.writeObject(responseData);  // 응답 객체를 클라이언트에게 전송
	            out.flush();  // 전송한 데이터를 강제로 보내기
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			break;
		}
	}
	
	
	private void sendVictory(boolean isBlack) {
		room.broadcasting(new GameData(name, isBlack, GameData.SEND_RESULT));
	}

	private void moveTurn() {
		setMyTurn(!isMyTurn());
		room.moveTurn(userLocation);
	}

	public String toString() {
		return name;
	}

	public int getUserLocation() {
		return userLocation;
	}

	public String getUserName() {
		return name;
	}

	public void setUserLocation(int location) {
		this.userLocation = location;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	private void setReady(boolean bool) {
		isReady = bool;
	}

	public boolean isReady() {
		return isReady;
	}

	public boolean isMyTurn() {
		return isMyTurn;
	}

	public void toGameServer(Protocol data) {
		analysisGameLobbyData((GameLobbyData) data);
	}

	public void setMyTurn(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
	}

	public boolean isLogin() {
		return isLogin;
	}
	

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	
	public String getGameState(String roomName) {
	    GameRoomInterface room = lobby.getSelectedRoom(roomName); // 방 이름으로 게임 방 객체 가져오기
	    if (room != null && room.isPlaying()) {
	        // 방이 플레이 중이면 게임 상태를 반환
	        return room.getGameState(); // 게임 상태 문자열 반환
	    }
	    return null; // 게임이 진행 중이 아니면 null 반환
	}
	
}