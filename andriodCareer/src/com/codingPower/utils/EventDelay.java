package com.codingPower.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * a timeline control what set event happens after init time; 
 * 
 * @author pengf
 *
 */
public class EventDelay {
	private int mDelayTime = 0;
	private DelayEvent mDelayEvent;
	private boolean timeup;
	
	public static EventDelay createBy(int time){
		EventDelay delay = new EventDelay();
		delay.mDelayTime = time;
		return delay;
	}
	
	public void setDelayEvent(DelayEvent event){
		if(timeup)
			event.doNext();
		else
			mDelayEvent = event;
	}
	/**
	 * set timeup
	 * 
	 * @param cancelEvent cancel set event or not 
	 */
	public void timeup(boolean cancelEvent){
		timeup = true;
		if(cancelEvent)
			mDelayEvent = null;
		else if(mDelayEvent != null)
			mDelayEvent.doNext();
	}
	
	public static interface DelayEvent{
		void doNext();
	}
	
	public void start(){
		(new Timer()).schedule(new TimerTask() {
			@Override
			public void run() {
				if(mDelayEvent != null)
					mDelayEvent.doNext();
				else
					timeup = true;
			}
		}, mDelayTime);
	}
}
