package gameServer;

import java.util.ArrayList;
import java.util.Vector;

import protocolData.*;

public class Lobby implements LobbyInterface {

	private ClientManager userList;
	private RoomManager roomList;
	
	public Lobby() {
		userList = new ClientManager();
		roomList = new RoomManager();
	}

	public void broadcasting(Protocol data) {
		System.out.println("in broadcast : " + data);
		LogFrame.print("in broadcast : " + data);
		
		// 로비에 접속해있는 모든 userList에게 위치가 LOBBY이면 sendMessage(data)
		for (GameServer server : userList.getCollection())
			if(server.getUserLocation() == ServerInterface.LOBBY) {
				try 
				{
					server.sendMessage(data);
				} catch (Exception e) {
					userList.subUser( server );
					System.out.println(server.getUserName() + "E.Lobby01");
				}
			}
	}
	
	/** data로 받아온 이름 목록으로 멀티캐스팅 수행합니다. (리팩 필요)*/
	public void multicasting(Protocol data)
	{
		System.out.println("in multicast : " + data);
		
		if(data instanceof ChatData chatData)
		{
			Vector<String>	UserNameVec = chatData.getUserList();
			for(int i = 0 ; i < UserNameVec.size(); ++i)
			{
				GameServer server = userList.get(UserNameVec.get(i));
				try
				{
					server.sendMessage(data);
				} catch(Exception e) {
					System.out.println("Lobby" + e);
				}
			}
		}
	}
	
	public void subSocket(String name) {
		userList.subUser(name);
	}
	
	public void sendSlip(ChatData data) {
		System.out.println("in sendSlip : " + data.getReceiver());
		LogFrame.print("in sendSlip : " + data.getReceiver());
		try {
			userList.get(data.getReceiver()).sendMessage(data);
		} catch(Exception e) {
			System.out.println("[Throw] Send Slip Exception~!!!");
			LogFrame.print("[Throw] Send Slip Exception~!!!");
			
			userList.subUser( data.getReceiver() );
		}
		
	}
	

	public void addGamer(GameServer gameServer) {
		userList.addUser(gameServer);
	}
	
	public void addRoom(GameRoomInterface room) {
		roomList.addRoom(room);
	}

	protected ArrayList<GameServer> getClientList_Lobby() {
		return userList.getCollection();
	}
	

	protected ArrayList<GameRoomInterface> getRoomList() {
		return roomList.getCollection();
	}
	

	public ClientManager getUserList() {
		return userList;
	}

	public void subRoom(int roomNumber) {
		roomList.subRoom(roomNumber);
		System.out.println("SUB ROOM succeed!!");
	}

	public void printState() {
		System.out.println("=========== state ==============");
		System.out.println("user List : " + userList.getStringList());
		System.out.println("Client : " + userList.getCollection());
		System.out.println("Room : " + roomList.getCollection());
		
		LogFrame.print("=========== state ==============");
		LogFrame.print("user List : " + userList.getStringList());
		LogFrame.print("Client : " + userList.getCollection());
		LogFrame.print("Room : " + roomList.getCollection());
		
	}

	public GameRoomInterface getSelectedRoom(String roomName) {
		for(GameRoomInterface temp : roomList.getCollection())
			if(temp.getRoomName().equals(roomName))
				return temp;
		
		return null;
	}

	public Vector<String> getRoomListAsString() {
		return roomList.getStringList();
	}

	public Vector<String> getStringUser() {
		Vector<String> user = new Vector<String>();
		
		for (GameServer temp : userList.getCollection())
			if(temp.getUserLocation() == ServerInterface.LOBBY)
				user.add(temp.getUserName());
		
		return user;
	}
	
    // RoomManager를 반환하는 메소드 추가
    public RoomManager getRoomManager() {
        return roomList;
    }
	
}