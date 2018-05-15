package alarm;

public class AlarmRunnable implements Runnable{

	Car c;
	
	public AlarmRunnable(Car c){
		this.c = c;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("* alarm started");
			c.activateFullAlarm();
			Thread.sleep(3 * 1000);
			c.activateFlashAlarm();
			Thread.sleep(3 * 1000);
			c.endAlarm();
		} catch (InterruptedException e) {
			System.out.println("* alarm interrupted");
			return;
		}
		
	}
	
	

}
