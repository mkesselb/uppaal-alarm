package alarm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Car {
	
	public boolean armed;

	public boolean locked;
	
	public boolean open;
	
	public boolean alarm;
	public boolean silentAlarm;
	public boolean flash;
	public boolean sound;
	
	public Thread armedThread;
	public Thread alarmThread;
	
	public String state = "";
	
	private PrintStream out;
	
	public Car(PrintStream out_){
		open = true;
		locked = false;
		armed = false;
		alarm = false;
		silentAlarm = false;
		flash = false;
		sound = false;
		armedThread = null;
		out = out_;
		changeState("OpenAndUnlocked");
	}
	
	//try to open door
	public void open(){
		out.println("- open");
		if(open){
			//do nothing
			out.println("already open!");
			return;
		}
		if(!armed){
			if(!open && locked){
				open = true;
				changeState("OpenAndLocked");
				this.interruptArmedThread();
				return;
			}
			if(!open && !locked){
				open = true;
				changeState("OpenAndUnlocked");
				return;
			}
		}
		if(armed){ 
			if(!open){
				open = true;
				this.alarmThread();
				changeState("Alarm");
			}
		}
	}
	
	//try to close door
	public void close(){
		out.println("- close");
		if(silentAlarm){
			open = false;
			silentAlarm = false;
			changeState("Armed");
			return;
		}
		if(!alarm){
			if(!open){
				//do nothing
				out.println("already closed!");
				return;
			}
			if(open && locked){
				open = false;
				changeState("ClosedAndLocked");
				this.armThread();
				return;
			}
			if(open && !locked){
				open = false;
				changeState("ClosedAndUnlocked");
				return;
			}
		}
	}
	
	public void lock(){
		out.println("- lock");
		if(locked){
			//do nothing
			out.println("already locked!");
			return;
		}
		if(!locked && open){
			locked = true;
			changeState("OpenAndLocked");
			return;
		}
		if(!locked && !open){
			locked = true;
			changeState("ClosedAndLocked");
			this.armThread();
			return;
		}
	}
	
	public void unlock(){
		out.println("- unlock");
		if(!locked){
			//do nothing
			out.println("already unlocked!");
			return;
		}
		if(alarm || silentAlarm){
			this.interruptAlarmThread();
			flash = false;
			sound = false;
			alarm = false;
			silentAlarm = false;
			locked = false;
			armed = false;
			changeState("OpenAndUnlocked");
			return;
		}
		if(armed){
			armed = false;
			locked = false;
			changeState("ClosedAndUnlocked");
			return;
		}
		if(!armed){
			if(locked && open){
				locked = false;
				changeState("OpenAndUnlocked");
				return;
			}
			if(locked && !open){
				locked = false;
				changeState("ClosedAndUnlocked");
				this.interruptArmedThread();
				return;
			}
		}
	}
	
	public void arm(){
		out.println("- arm");
		if(armed){
			//do nothing, should not happen!
			out.println("already armed!");
			return;
		}
		if(!armed){
			//should imply !open && locked
			armed = !armed;
			changeState("Armed");
			return;
		}
	}
	
	public void activateFullAlarm(){
		out.println("- alarm full");
		alarm = true;
		flash = true;
		sound = true;
		changeState("Alarm-FlashAndSound");
	}
	
	public void activateFlashAlarm(){
		out.println("- alarm flash");
		sound = false;
		changeState("Alarm-Flash");
	}
	
	public void endAlarm(){
		out.println("- alarm end");
		alarm = false;
		flash = false;
		silentAlarm = true;
		changeState("SilentAndOpen");
	}
	
	private void changeState(String newState){
		out.println("---");
		out.println("state: " + this.state + "\nnew state: " + newState);
		state = newState;
		out.println("---");
	}
	
	private void armThread(){
		Thread a1 = new Thread(new ArmedRunnable(this));
		armedThread = a1;
		a1.start();
	}
	
	private void interruptArmedThread(){
		if(armedThread != null){
			armedThread.interrupt();
			armedThread = null;
		}
	}
	
	private void alarmThread(){
		Thread a1 = new Thread(new AlarmRunnable(this));
		alarmThread = a1;
		a1.start();
	}
	
	private void interruptAlarmThread(){
		if(alarmThread != null){
			alarmThread.interrupt();
			alarmThread = null;
		}
	}
}
