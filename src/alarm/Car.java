package alarm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Car {
	
	public boolean armed;

	public boolean locked;
	public boolean lockedTrunk;
	
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
		lockedTrunk = false;
		armed = false;
		alarm = false;
		silentAlarm = false;
		flash = false;
		sound = false;
		armedThread = null;
		alarmThread = null;
		tries = 3;
		triesNewPin = 3;
		doors = new int[] {1,1,1,1,1}; //doors 0-3, trunk 4. value 1 means open, 0 means closed
		out = out_;
		changeState("OpenAndUnlocked");
	}
	
	//try to open door
	public void open(int door){
		out.println("- open " + door);
		if(door < 0 || door > 4) {
			System.err.println("door number wrong: " + door);
			return;
		}
		if(open){
			setDoor(door, 1);
			return;
		}
		if(!armed){
			if(!alarm && !silentAlarm){
				if(!open && locked){
					setDoor(door, 1);
					if(!this.doorsClosed()) {
						open = true;
						changeState("OpenAndLocked");
						this.interruptArmedThread();
						return;
					}
				}
				if(!open && !locked){
					setDoor(door, 1);
					if(!this.doorsClosed()) {
						open = true;	
						changeState("OpenAndUnlocked");
						return;
					}
				}
			} else{
				if(!open && locked){
					setDoor(door, 1);
					if(!this.doorsClosed()) {
						open = true;
						changeState("OpenAndLockedUnderAlarm");
						this.interruptArmedThread();
						return;
					}
				}
				if(!open && !locked){
					setDoor(door, 1);
					if(!this.doorsClosed()) {
						open = true;
						changeState("OpenAndUnlockedUnderAlarm");
						return;
					}
				}
			}
		}
		if(armed){
			if(!open){
				setDoor(door, 1);
				if(door == 4) {
					//trunk
					if(lockedTrunk) {
						this.alarmThread();
						changeState("Alarm");
						return;
					} else {
						//fine
						return;
					}
				} else {
					if(!this.doorsClosed()) {
						open = true;
						this.alarmThread();
						changeState("Alarm");
						return;
					}
				}
			}
		}
	}
	
	//try to close door
	public void close(int door){
		out.println("- close " + door);
		if(door < 0 || door > 4) {
			System.err.println("door number wrong: " + door);
			return;
		}
		if(silentAlarm && open && locked || silentAlarm && locked && (door == 4)){
			setDoor(door, 0);
			if(this.doorsClosed()) {
				this.interruptAlarmThread();
				flash = false;
				sound = false;
				alarm = false;
				silentAlarm = false;
				
				open = false;
				changeState("Armed");
			}
			return;
		}
		if(!open){
			//do nothing
			setDoor(door, 0);
			return;
		}
		if(!alarm && !silentAlarm){
			if(open && locked){
				setDoor(door, 0);
				if(this.doorsClosed()) {
					open = false;
					changeState("ClosedAndLocked");
					this.armThread();
					return;
				}
			}
			if(open && !locked){
				setDoor(door, 0);
				if(this.doorsClosed()) {
					open = false;
					changeState("ClosedAndUnlocked");
					return;
				}
			}
		} else{
			if(open && locked){
				setDoor(door, 0);
				if(this.doorsClosed()) {
					open = false;
					changeState("ClosedAndLockedUnderAlarm");
					this.armThread();
					return;
				}
			}
			if(open && !locked){
				setDoor(door, 0);
				if(this.doorsClosed()) {
					open = false;
					changeState("ClosedAndUnlockedUnderAlarm");
					return;
				}
			}
		}
	}
	
	//try to lock door
	public void lock(){
		out.println("- lock");
		if(locked){
			//do nothing
			locked = true;
			lockedTrunk = true;
			out.println("all locked");
			return;
		}
		//locking only possible if no alarm (e.g. from wrong pin setting)
		if(!alarm && !silentAlarm){
			if(!locked && open){
				locked = true;
				lockedTrunk = true;
				changeState("OpenAndLocked");
				return;
			}
			if(!locked && !open){
				locked = true;
				lockedTrunk = true;
				changeState("ClosedAndLocked");
				this.armThread();
				return;
			}
		}
	}
	
	//try to unlock door
	public void unlock(int pin, int trunk){
		out.println("- unlock: " + pin + ", trunk " + trunk);
		if(1000 > pin || 9999 < pin){
			System.err.println("pin not 4 digits!");
			return;
		}
		
		//check pin
		if(pin == this.pin){
			tries = 3;
			if(trunk != 0) {
				//unlock trunk only
				if(!lockedTrunk){
					//do nothing
					out.println("trunk not locked!");
					return;
				}
				if(lockedTrunk){
					lockedTrunk = false;
					out.println("trunk unlocked");
					return;
				}
			} else {
				if(!locked){
					this.interruptAlarmThread();
					flash = false;
					sound = false;
					alarm = false;
					silentAlarm = false;
					locked = false;
					lockedTrunk = false;
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
					lockedTrunk = false;
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
					lockedTrunk = false;
					changeState("ClosedAndUnlocked");
					return;
				}
				if(!armed){
					if(locked && open){
						locked = false;
						lockedTrunk = false;
						changeState("OpenAndUnlocked");
						return;
					}
					if(locked && !open){
						locked = false;
						lockedTrunk = false;
						changeState("ClosedAndUnlocked");
						this.interruptArmedThread();
						return;
					}
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
			armed = true;
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
		
		if(!locked && !lockedTrunk){
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
	
	private void setDoor(int door, int status) {
		doors[door] = status;
		out.println("# door " + door + " set to " + status + " /doors: " + Arrays.toString(doors));
	}
	
	private boolean doorsClosed(){
		if (doors[0] == 0 && doors[1] == 0 
				&& doors[2] == 0 && doors[3] == 0){
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
	
	public void status() {
		out.println("^^");
		out.println("^ armed: " + armed);
		out.println("^ locked: " + locked);
		out.println("^ lockedTrunk: " + lockedTrunk);
		out.println("^ doors: " + Arrays.toString(doors));
		out.println("^ open: " + open);
		out.println("^ alarm: " + alarm);
		out.println("^ silentAlarm: " + silentAlarm);
		out.println("^ flash: " + flash);
		out.println("^ sound: " + sound);
		out.println("^ pin: " + pin);
		out.println("^ tries: " + tries);
		out.println("^ triesNewPin: " + triesNewPin);
		out.println("^ state: " + state);
		out.println("^^");
	}
}
