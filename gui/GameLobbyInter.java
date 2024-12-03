package gui;

public interface GameLobbyInter extends GuiInterface {

	void setRoomKing(String name);
	void setCharacter(String chracterName, boolean IsRoomKing);
	String getCharacter(boolean IsRoomKing);
	void setChallenger(String string);
	void setGameRoomInf(String info);
	void setStartButton(boolean isRoomKing);
	void setButtonEnable(boolean clickable);
	String getGameInfo();
	int[] getFrameSize();
	
	
}
