package gui;

import gui.Dainn.CharacterSelection;
// Mouse Click Event in Game Canvas execute in GameRoomGui.class/
import gameClient.ClientInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import protocolData.ChatData;
import protocolData.GameLobbyData;
import protocolData.GameData;
import protocolData.LobbyData;
import protocolData.RequestData;

class EventExecute extends WindowAdapter implements ActionListener{
	
	PanelInterface gui;
	ClientInterface client;
	
	protected EventExecute(PanelInterface gui, ClientInterface client) {
		this.gui = gui;
		this.client = client;
	}
	
	public void windowClosing(WindowEvent e) {
		client.sendMessage("E.EventExecute01",  ChatData.EXIT);
		System.exit(0);
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		
		if(e.getActionCommand().equals("Create Game Room")) 
		{
			String roomName;
			try {
				// when user input empty String, reinput!
				while( (roomName = getRoomName()).equals("") );	
				
				
			} catch(NullPointerException e1) {
				// when user input null, this method exit!
				return;
			}
			
			new CharacterSelection(client, roomName, true);
			
		} 
		else if(e.getActionCommand().equals("Enter Game Room")) {
			String roomName = ((LobbyGuiInter)gui).getSelectRoom();
			if (roomName == null) {
				JOptionPane.showConfirmDialog(null, 
						"Select Game Room!!.", "Notice!", JOptionPane.DEFAULT_OPTION);
				return;
			}
			
			//client.sendMessage(null, GameLobbyData.GAME_START);
			
			client.sendMessage(roomName, RequestData.CHECK_PLAY);		
		} 
		else if(e.getSource().getClass().getName().equals("input"))
		{
			
		}
		else if(e.getActionCommand().equals("SEND")) {
			/*
	        // 채팅 전송 이벤트 리스너
            String message = chatInput.getText();
            if (!message.trim().isEmpty()) {
                // 채팅 메시지를 채팅 영역에 추가
                JLabel messageLabel = new JLabel(message);
                chatAreaPanel.add(messageLabel);
                chatAreaPanel.revalidate(); // 패널 업데이트
                chatAreaPanel.repaint(); // 패널 다시 그리기

                // 데이터베이스에 메시지 저장 (여기에 실제 DB 저장 로직을 구현)
                saveMessageToDatabase(message);

                // 입력 필드 비우기
                chatInput.setText("");
            }
            */
        
			System.out.println("-------------------SEND CHAT MESSAGE!!");
			sendChatMessage();
			
		} else if(e.getActionCommand().equals("EXIT")) {
			this.windowClosing(null);
			
		} else if (e.getActionCommand().equals("나가기")) {
			client.sendMessage(null, GameLobbyData.EXIT_ROOM);
			
		} else if (e.getActionCommand().equals("EXIT GAME")) {
			int bool = JOptionPane.showConfirmDialog(null, 
					"Really Exit Game? You lost this game.", "Notice!", JOptionPane.YES_NO_OPTION); 
			if(bool == 0) client.sendMessage(null, GameData.EXIT_THEGAME);
			else return;
			
		}else if (e.getActionCommand().equals("START")) {
			//String roomName = ((GameLobbyInter)gui).getSelectRoom();
			client.sendMessage(null, GameLobbyData.GAME_START);
		
		} else if (e.getActionCommand().equals("READY")) {
			((GameLobby)gui).m_startButton.setText("CANCEL");
			client.sendMessage(null, GameLobbyData.GAME_READY);
			
		} else if (e.getActionCommand().equals("CANCEL")) {
			((GameLobby)gui).m_startButton.setText("READY");
			client.sendMessage(null, GameLobbyData.CANCEL_READY);
			
		} else {
			System.out.println("&&&&&&&&&&&&&&&&&&&&&" + e.getActionCommand());
			sendChatMessage();
		}
	}
	
	private String getRoomName() {
		return JOptionPane.showInputDialog("방 제목을 입력해 주세요.");
		//방만들기 이벤트
	}
	
	  //send Event
	 
	private void sendChatMessage() {
		String chat = gui.getInputText();
		if(!chat.equals("")) 
		{
			/** @error gui는 PanelInterface이므로 자식인 
			 * LobbyGui에 선언된 함수를 사용하기 위해선 형변환이 필요합니다.
			 */
			if(null == client)
				System.out.println("EventExecute - sendChatMessage - client is NULL");			}
		
			// ClientLobby(client)의 sendMessage 호출함. 
			client.sendMessage(chat,  ChatData.MESSAGE);
			gui.setTextToInput("");
		
	}
	

	public void setGui(PanelInterface gui) {
		this.gui = gui;
	}
}