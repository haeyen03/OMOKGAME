package work;

import java.util.ArrayList;

public class Observer
{
	public Observer()
	{
		ListenerList = new ArrayList<>(); // 초기화
	}
	
	public void addListener(EventListener Listener)
	{
		ListenerList.add(Listener);
	}
	
	public void onEvent(Observer obs)
	{
		for(EventListener Listener : ListenerList)
		{
			Listener.onEvent(obs);
		}
	}
	
	private ArrayList<EventListener> ListenerList;
}

