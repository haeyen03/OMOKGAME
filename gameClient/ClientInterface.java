package gameClient;

import gui.GuiInterface;
import protocolData.Protocol;
import protocolData.RequestData;

public interface ClientInterface {
	void sendMessage(String message, short state);
	void sendMessage(Protocol protocol);
	void sendSlip(String to, String text, short message_slip);
	GuiInterface getGui();
	public void changeGui(GuiInterface gui);
	public void changePanel(GuiInterface panel);
	void sendMessage(int[] is);
	void setSlipTo(String slipTo);
	boolean isRoomKing();
	String	getName();
}
