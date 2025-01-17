package gameServer;

import java.util.Vector;

public interface LobbyInterface extends ServerInterface {

	Vector<String> getRoomListAsString();
	void addRoom(GameRoomInterface room);
	void addGamer(GameServer server);
	void subRoom(int roomNumber);
	void printState();
	public void subSocket(String name);
	GameRoomInterface getSelectedRoom(String roomName);
    // RoomManager를 반환하는 메소드 추가
    public RoomManager getRoomManager();
}
