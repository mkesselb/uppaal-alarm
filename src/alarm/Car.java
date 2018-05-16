package alarm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Car {
	
	public boolean armed;

	public boolean locked;
	
	public int[] doors;
	public boolean open;
	
	public boolean alarm;
	public boolean silentAlarm;
	public boolean flash;
	public boolean sound;
	
	public int pin;
	public int tries;
	public int triesNewPin;
	
	public Thread armedThread;
	public Thread alarmThread;
	
	public String state = "";
	
	private PrintStream out;
	
	public Car(PrintStream out_, int pin){
		this.pin = pin;
		open = true;
		locked = false;
		armed = false;
		alarm = false;
		silentAlarm = false;
		flash = false;
		sound = false;
		armedThread = null;
		alarmThread = null;
		tries = 3;
		triesNewPin = 3;
		doors = new int[] {1,1,1,1,1}; //doors 0-3, trunk 4
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
			if(!alarm && !silentAlarm){
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
			} else{
				if(!open && locked){
					open = true;
					changeState("OpenAndLockedUnderAlarm");
					this.interruptArmedThread();
					return;
				}
				if(!open && !locked){
					open = true;
					changeState("OpenAndUnlockedUnderAlarm");
					return;
				}
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
		if(silentAlarm && open && locked){
			this.interruptAlarmThread();
			flash = false;
			sound = false;
			alarm = false;
			silentAlarm = false;
			
			open = false;
			changeState("Armed");
			return;
		}
		if(!open){
			//do nothing
			out.println("already closed!");
			return;
		}
		if(!alarm && !silentAlarm){
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
		} else{
			if(open && locked){
				open = false;
				changeState("ClosedAndLockedUnderAlarm");
				this.armThread();
				return;
			}
			if(open && !locked){
				open = false;
				changeState("ClosedAndUnlockedUnderAlarm");
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
		//locking only possible if no alarm (e.g. from wrong pin setting)
		if(!alarm && !silentAlarm){
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
	}
	
	public void unlock(int pin){
		out.println("- unlock: " + pin);
		if(1000 > pin || 9999 < pin){
			System.err.println("pin not 4 digits!");
			return;
		}
		
		//check pin
		if(pin == this.pin){
			tries = 3;
			if(!locked){
				this.interruptAlarmThread();
				flash = false;
				sound = false;
				alarm = false;
				silentAlarm = false;
				locked = false;
				armed = false;
				if(open){
					changeState("OpenAndUnlocked");
				} else{
					changeState("ClosedAndUnlocked");
				}
				//System.out.println("already unlocked!");
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
				if(open){
					changeState("OpenAndUnlocked");
				} else{
					changeState("ClosedAndUnlocked");
				}
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
		} else{
			tries -= 1;
			System.err.println("pin incorrect! remaining tries: " + tries);
			if(tries < 1 && armed){
				this.alarmThread();
			}
			return;
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
	
	public void setPinCode(int oldPin, int newPin){
		out.println("- set pincode: " + oldPin + " -> " + newPin);
		if(1000 > oldPin || 9999 < oldPin){
			System.err.println("oldPin not 4 digits!");
			return;
		}
		if(1000 > newPin || 9999 < newPin){
			System.err.println("newPin not 4 digits!");
			return;
		}
		
		if(!locked){
			if(oldPin == this.pin){
				this.pin = newPin;
				triesNewPin = 3;
				System.out.println("newPinSet");
				
			} else{
				triesNewPin -= 1;
				System.err.println("pin incorrect! remaining tries: " + triesNewPin);
				if(triesNewPin < 1){
					this.alarmThread();
				}
				return;
			}
		} else{
			System.err.println("set pin not possible if locked!");
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
		if(open){
			if(locked){
				changeState("SilentAndOpenLocked");
			} else{
				changeState("SilentAndOpenUnlocked");
			}
		} else{
			if(locked){
				changeState("SilentAndClosedLocked");
			} else{
				changeState("SilentAndClosedUnlocked");
			}
		}
	}
	
	private boolean doorsOpen(){
		if (doors[0] == 1 && doors[1] == 1 
				&& doors[2] == 1 && doors[3] == 1){
			return true;
		}
		return false;
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
