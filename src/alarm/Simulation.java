package alarm;

import java.util.Scanner;

public class Simulation {

	public static void main(String[] args){
	
		Scanner sc = new Scanner(System.in);
		
		System.out.println("\n-----");
		System.out.println("alarm system simulation");
		System.out.println("-----\n");
		
		System.out.println("please provide input");
		System.out.println("\t0: exit");
		System.out.println("\t1: open +door");
		System.out.println("\t2: close +door");
		System.out.println("\t3: lock");
		System.out.println("\t4: unlock +pin +trunk");
		System.out.println("\t5: setPincode +oldpin +newpin");
		System.out.println("\t6: status report");
		
		Car c = new Car(System.out, 1234);
		
		do{
			int input = sc.nextInt();
			if(input == 0){
				break;
			}
			
			switch(input){
			case 1:
				System.out.println("input door num (0-3: doors, 4: trunk)");
				int doorOpen = sc.nextInt();
				c.open(doorOpen);
				break;
			case 2:
				System.out.println("input door num (0-3: doors, 4: trunk)");
				int doorClose = sc.nextInt();
				c.close(doorClose);
				break;
			case 3:
				c.lock();
				break;
			case 4:
				System.out.println("input pin:");
				int pin = sc.nextInt();
				System.out.println("input if trunk:");
				int unlockTrunk = sc.nextInt();
				c.unlock(pin, unlockTrunk);
				break;
			case 5:
				System.out.println("input old pin:");
				int opin = sc.nextInt();
				System.out.println("input new pin:");
				int npin = sc.nextInt();
				c.setPinCode(opin, npin);
				break;
			case 6:
				c.status();
				break;
			default:
				System.out.println("provide correct input!");
				System.out.println("\t0: exit");
				System.out.println("\t1: open +door");
				System.out.println("\t2: close +door");
				System.out.println("\t3: lock");
				System.out.println("\t4: unlock +pin +trunk");
				System.out.println("\t5: setPincode +oldpin +newpin");
				System.out.println("\t6: status report");
			}
					
		} while(true);
		
		sc.close();
	}
}
