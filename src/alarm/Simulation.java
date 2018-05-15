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
		System.out.println("\t1: open");
		System.out.println("\t2: close");
		System.out.println("\t3: lock");
		System.out.println("\t4: unlock +pin");
		System.out.println("\t5: setPincode +oldpin +newpin");
		
		Car c = new Car(System.out, 1234);
		
		do{
			int input = sc.nextInt();
			if(input == 0){
				break;
			}
			
			switch(input){
			case 1:
				c.open();
				break;
			case 2:
				c.close();
				break;
			case 3:
				c.lock();
				break;
			case 4:
				System.out.println("input pin:");
				int pin = sc.nextInt();
				c.unlock(pin);
				break;
			case 5:
				System.out.println("input old pin:");
				int opin = sc.nextInt();
				System.out.println("input new pin:");
				int npin = sc.nextInt();
				c.setPinCode(opin, npin);
				break;
			default:
				System.out.println("provide correct input!");
				System.out.println("\t0: exit");
				System.out.println("\t1: open");
				System.out.println("\t2: close");
				System.out.println("\t3: lock");
				System.out.println("\t4: unlock +pin");
				System.out.println("\t5: setPincode +oldpin +newpin");
			}
					
		} while(true);
		
		sc.close();
	}
}
