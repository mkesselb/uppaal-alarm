package alarm;

public class ArmedRunnable implements Runnable{

	Car c;
	
	public ArmedRunnable(Car c){
		this.c = c;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("* arming started");
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {
			System.out.println("* arming interrupted");
			return;
		}
		c.arm();
	}
	
	

}
