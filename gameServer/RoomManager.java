package gameServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class RoomManager {
	private List<GameRoomInterface> roomList;

	public RoomManager() {
		roomList = Collections
				.synchronizedList(new ArrayList<GameRoomInterface>());
	}

	public synchronized void addRoom(GameRoomInterface room) {
		roomList.add(room);
	}

	public synchronized void subRoom(int roomNumber) {
		for (GameRoomInterface temp : roomList) {
			if (temp.getNumber() == roomNumber) {
				roomList.remove(temp);
				this.subRoom(temp.getRoomName());
				
				break;
			}
		}
	}

	public synchronized void subRoom(String name) {
		for (GameRoomInterface temp : roomList)
			if (temp.getRoomName().equals(name)) {
				roomList.remove(temp);
			}
	}

	public GameRoomInterface get(int index) {
		return roomList.get(index);
	}
	
	public GameRoomInterface getRoomByName(String roomName) {
	    for (GameRoomInterface room : roomList) {
	        if (room.getRoomName().equals(roomName)) {
	            return room;
	        }
	    }
	    return null;  // 방 이름에 해당하는 방을 찾지 못한 경우 null 반환
	}

	public Vector<String> getStringList() {
		Vector<String> userList = new Vector<String>();
		
		
		
		for (GameRoomInterface temp : roomList) {
			if (temp.getUserCounter() == 0)
				userList.remove(temp);
			else
				userList.add(temp.getRoomName());
		}

		return userList;
	}

	public int size() {
		return roomList.size();
	}

	public ArrayList<GameRoomInterface> getCollection() {
		ArrayList<GameRoomInterface> temp = new ArrayList<GameRoomInterface>();

		for (GameRoomInterface list : roomList)
			temp.add(list);

		return temp;
	}
	
	/** @feature - 관전자 모드 구현을 위해 게임 진행 중인지 확인 및 현재 게임/종료 셋팅 */
    // 방 상태 확인
    public boolean isRoomPlaying(String roomName) {
        for (GameRoomInterface room : roomList) {
            if (room.getRoomName().equals(roomName)) {
                return room.isPlaying();
            }
        }
        return false; // 방이 존재하지 않거나 게임이 진행되지 않음
    }

    // 게임 시작
    public void startGame(String roomName) {
        for (GameRoomInterface room : roomList) {
            if (room.getRoomName().equals(roomName)) {
                ((GameRoom) room).startGame();  // 게임 시작
            }
        }
    }

    // 게임 종료
    public void endGame(String roomName) {
        for (GameRoomInterface room : roomList) {
            if (room.getRoomName().equals(roomName)) {
                ((GameRoom) room).endGame();  // 게임 종료
            }
        }
    }
}
