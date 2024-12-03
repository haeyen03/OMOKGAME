package gameServer;

import java.util.ArrayList;
import java.util.Vector;

import protocolData.*;

/** 채팅방 서버를 관리하는 클래스입니다. */
public class ChattingRoomServer 
{
	private ClientManager clientManager;
	
	ChattingRoomServer()
	{
		clientManager = new ClientManager();
	}
	
	public void broadcasting(Protocol data) 
	{	
		for (GameServer temp : clientManager.getCollection())
			// 서버가... 채팅방이 열려잇다면... sendMEssage
			if(temp.getUserLocation() == ServerInterface.LOBBY) 
			{
				try 
				{
					temp.sendMessage(data);
					
				} catch (Exception e) {
					System.out.println(temp.getUserName() + "ChattingRoomServer 오류");
				}
			}
	}
}
