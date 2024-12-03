package gameServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import protocolData.ChatData;
import protocolData.Protocol;
import java.util.List;  // List를 사용하기 위해 필요
import java.util.ArrayList;  // ArrayList를 사용하기 위해 필요

public class GameRoom implements GameRoomInterface , Serializable {
	
	private static final long serialVersionUID = 1L;
	private static int roomNO = 1;
	
	private ClientManager userList;
	private int number;
	private String roomName;
	private String kingCharacterName;
	private String challengerCharacterName;
	
	private StoneAlgol analysisStone;
	private boolean isStart = false;
    private boolean isPlaying;  // 게임 진행 중 여부
    
	public GameRoom(String roomName, String characterName, boolean bIsRoomKing, ClientManager userList) {
		this.roomName = roomName;
		this.userList = userList;
		this.analysisStone = new StoneAlgol();
		number = roomNO++;
		
		if(bIsRoomKing)
			this.kingCharacterName = characterName;
		else
			this.challengerCharacterName = characterName;
	}
	
	public void broadcasting(Protocol data) {
		System.out.println("in broadcast : " + data);
		LogFrame.print("in broadcast : " + data);
		
		for (GameServer temp : userList.getCollection(number)) {
			/** @error -  로비가 아닐 경우여서 관전자도 LOBBY 말고 교체해줘야함. */
			//if (!(temp.getUserLocation() == ServerInterface.LOBBY)) { 
				try {
					temp.sendMessage(data);
				} catch (Exception e) {
					userList.subUser( temp );
					System.out.println( temp.getName() + "E.GameRoom01" );
				}
			//}
		}
	}

	public void multicasting(Protocol data) {}
	
	public void sendSlip(ChatData data) {
		try {
			userList.get(data.getReceiver()).sendMessage(data);
		} catch (Exception e) {
			userList.subUser( data.getReceiver() );
			System.out.println( "E.GameRoom02" );
		}		
	}

	public void addViewer(GameServer viewer) {
	    // 유저 목록에 관전자 추가
	    userList.addUser(viewer);
	    System.out.println("관전자 추가됨: " + viewer.getUserName());
	}

	/** @feature - 관전자 기능을 위해 게임 중인지 확인하기 위함 */
    // 게임 진행 상태 확인
    public boolean isPlaying() {
        return isPlaying;
    }

    // 게임 진행 상태 설정
    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
    
    // 게임 시작
    public void startGame() {
        setPlaying(true);  // 게임 시작 시 isPlaying을 true로 설정
    }

    // 게임 종료
    public void endGame() {
        setPlaying(false); // 게임 종료 시 isPlaying을 false로 설정
    }
    
	public int getNumber() {
		return number;
	}

	public String getRoomName() {
		return roomName;
	}
	
	public ClientManager getUserList() {
		return userList;
	}

	public String getRoomKingName() {
		return null; 
	}

	public void setCharacterName(String name, boolean bIsRoomKing) {
		if(bIsRoomKing)
			kingCharacterName = name;
		else
			challengerCharacterName = name;
	}
	
	public String getCharacterName(boolean bIsRoomKing) {
		if(bIsRoomKing)
			return kingCharacterName;
		else
			return challengerCharacterName;
	}
	
	public Vector<String> getStringUser() {
		Vector<String> stringList = new Vector<String>();
		ArrayList<GameServer> gamerList = userList.getCollection(number);
		
		try {
			stringList.add(find(gamerList, ServerInterface.IN_GAME_ROOMKING).getUserName());
		} catch (NullPointerException e) {
			stringList.add("empty!");
		}

		try {
			stringList.add(find(gamerList, ServerInterface.IN_GAME_CRHARANGER).getUserName());
		} catch (NullPointerException e) {
			stringList.add("empty!");
		}
		

		for(GameServer temp : gamerList)
			if (temp.getUserLocation() == ServerInterface.IN_GAME_VIWER)
				stringList.add(temp.getUserName());
	
		return stringList;
	}

	private GameServer find(ArrayList<GameServer> userList, int location) {
		for(GameServer temp : userList)
			if (temp.getUserLocation() == location)
				return temp;
			
		return null;		
		
	}

	public String toString() {
		return roomName;
	}

	public void subSocket(String name) {
		userList.subUser(name);
	}

	public void subRoom() {
		// TODO Auto-generated method stub
		
	}

	public boolean isAllReady() {
		for(GameServer temp : userList.getCollection(number))
			if(!temp.isReady()) return false;
		
		return true;
	}

	public void sendTo(int toGamer, Protocol data) {
		GameServer reciver = find(userList.getCollection(number), toGamer); 
		try {
			reciver.sendMessage(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void moveTurn(int userLocation) {
		if(userLocation == IN_GAME_ROOMKING) userLocation = IN_GAME_CRHARANGER;
		else userLocation = IN_GAME_ROOMKING;
		
		for(GameServer temp : userList.getCollection(number))
			if(temp.getUserLocation() == userLocation) temp.setMyTurn(!temp.isMyTurn());
	}

	public boolean addStone(int[] stoneLocation, boolean isBlack) {
		int result = analysisStone.addStone(stoneLocation, isBlack);
		
		if (result == 1) return true;
		else return false;

	}
	
	public void subLastStone(int n) {
		analysisStone.subLastStone();
		if (n==2) subLastStone(1);
	}

	public void newGame() {
		analysisStone = new StoneAlgol();
		
	}

	public void exitRoomMaster(Protocol data) {
		for(GameServer temp : userList.getCollection(this.number)) 
			if (!(temp.getUserLocation() == ServerInterface.LOBBY)) 
				temp.toGameServer(data);
	}

	public boolean isStart() {
		return isStart;
	}
	

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public int getUserCounter() {
		return userList.getCollection(this.number).size();
	}
	
	public String getGameState() {
	    // 보드 상태와 차례를 문자열로 변환하여 반환
	    StringBuilder gameState = new StringBuilder();

	    // 게임 보드 상태 추가 (보드의 상태와 놓인 돌을 표시)
	    gameState.append("Game Board State: \n");

	    // 흑돌과 백돌 위치를 추가
	    gameState.append("Black Stones: ");
	    for (int[] blackStone : analysisStone.getBlackStoneLocations()) {
	        gameState.append("(").append(blackStone[0]).append(", ").append(blackStone[1]).append(") ");
	    }
	    gameState.append("\n");

	    gameState.append("White Stones: ");
	    for (int[] whiteStone : analysisStone.getWhiteStoneLocations()) {
	        gameState.append("(").append(whiteStone[0]).append(", ").append(whiteStone[1]).append(") ");
	    }
	    gameState.append("\n");

	    // 게임 진행 상태 (Playing or Waiting)
	    gameState.append("Current Turn: ").append(isPlaying ? "Playing" : "Waiting").append("\n");

	    // 방장 및 도전자 정보
	    gameState.append("Room King: ").append(kingCharacterName).append("\n");
	    gameState.append("Challenger: ").append(challengerCharacterName).append("\n");

	    return gameState.toString();
	}
	
    // 흑돌 위치 반환
    public List<int[]> getBlackStoneLocations() {
        return analysisStone.getBlackStoneLocations();
    }

    // 백돌 위치 반환
    public List<int[]> getWhiteStoneLocations() {
        return analysisStone.getWhiteStoneLocations();
    }
    
}