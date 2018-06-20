package alarmtest;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alarm.Car;

public class testCar {

	private Car c;
	
	@Before
	public void SetUp() throws Exception {
		this.c = new Car(System.out, 1234);
	}
	
	@After
	public void tearDown() throws Exception {
		c = null;
	}
	
	@Test
	public void setPin() {
		c.setPinCode(1234, 2345);
		assertEquals(2345, c.pin);
	}
	
	@Test
	public void openIndividualDoors() {
		c.close(1);
		assertEquals(true, c.open);
		c.close(2);
		assertEquals(true, c.open);
		c.close(3);
		assertEquals(true, c.open);
		
		c.close(0);
		assertEquals(false, c.open);
		
		c.open(3);
		assertEquals(true, c.open);
		
		c.close(3);
		assertEquals(false, c.open);
	}
		
	@Test
	public void unlockTrunkInArmed() {
		c.close(0);
		c.close(1);
		c.close(2);
		c.close(3);
		assertEquals(false, c.open);
		
		c.lock();
		assertEquals(true, c.locked);
		assertEquals(true, c.lockedTrunk);
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(true, c.armed);
		
		c.unlock(1234, 1);
		assertEquals(false, c.lockedTrunk);
		assertEquals(true, c.armed);
	}
	
	@Test
	public void testAlarm() {
		c.close(0);
		c.close(1);
		c.close(2);
		c.close(3);
		assertEquals(false, c.open);
		
		c.lock();
		assertEquals(true, c.locked);
		assertEquals(true, c.lockedTrunk);
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(true, c.armed);
		
		c.open(1);
		assertEquals(true, c.open);
		
		try {
			Thread.sleep(90);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(true, c.silentAlarm);
		
		c.close(1);
		assertEquals(false, c.open);
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(true, c.armed);
	}
	
	@Test
	public void testUnlockAlarm() {
		c.close(0);
		c.close(1);
		c.close(2);
		c.close(3);
		assertEquals(false, c.open);
		
		c.lock();
		assertEquals(true, c.locked);
		assertEquals(true, c.lockedTrunk);
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(true, c.armed);
		
		c.unlock(1234, 0);
		
		assertEquals(false, c.armed);
		assertEquals(false, c.locked);
		
		c.lock();
		assertEquals(true, c.locked);
		assertEquals(true, c.lockedTrunk);
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(true, c.armed);
		
		c.unlock(2345, 0);
		assertEquals(true, c.locked);
		assertEquals(true, c.armed);
		assertEquals(2, c.tries);
		
		c.unlock(2345, 0);
		assertEquals(true, c.locked);
		assertEquals(true, c.armed);
		assertEquals(1, c.tries);
		
		c.unlock(2345, 0);
		
		try {
			Thread.sleep(90);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(true, c.silentAlarm);	
	}
	
	@Test
	public void testUnlockUnderAlarm() {
		c.close(0);
		c.close(1);
		c.close(2);
		c.close(3);
		assertEquals(false, c.open);
		
		c.lock();
		assertEquals(true, c.locked);
		assertEquals(true, c.lockedTrunk);
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(true, c.armed);
		
		c.open(1);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// ignore
		}
		c.unlock(1234, 0);
		
		assertEquals(false, c.alarm);
		assertEquals("OpenAndUnlocked", c.state);
	}
	
	@Test
	public void testInterruptArming() {
		c.close(0);
		c.close(1);
		c.close(2);
		c.close(3);
		assertEquals(false, c.open);
		
		c.lock();
		assertEquals(true, c.locked);
		assertEquals(true, c.lockedTrunk);
		
		c.unlock(1234, 0);
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			// ignore
		}
		
		assertEquals(false, c.armed);
	}
	
}
