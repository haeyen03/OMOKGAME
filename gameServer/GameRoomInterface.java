package gameServer;

import java.util.List;

import protocolData.Protocol;

public interface GameRoomInterface extends ServerInterface {

	String getRoomName();
	String getRoomKingName();
	String getCharacterName(boolean bIsRoomKing);
	public void setCharacterName(String name, boolean bIsRoomKing);
	void subRoom();
    int getNumber();
	boolean isAllReady();
	void sendTo(int toGamer, Protocol data);
	void moveTurn(int userLocation);
	boolean addStone(int[] stoneLocation, boolean b);
	void newGame();
	void exitRoomMaster(Protocol data);
	void subLastStone(int n);
	boolean isStart();
	void setStart(boolean isStart);
	int getUserCounter();
	
	boolean isPlaying();  // 게임 진행 중 여부
    void setPlaying(boolean isPlaying);  // 게임 시작/종료 상태 설정
	public String getGameState();
	
    // 흑돌 위치 반환
    public List<int[]> getBlackStoneLocations();
    // 백돌 위치 반환
    public List<int[]> getWhiteStoneLocations();
    
	public void addViewer(GameServer viewer);
}
