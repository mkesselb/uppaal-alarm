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
		System.out.println("\t4: unlock");
		
		Car c = new Car(System.out);
		
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
				c.unlock();
				break;
			default:
				System.out.println("provide correct input!");
				System.out.println("\t0: exit");
				System.out.println("\t1: open");
				System.out.println("\t2: close");
				System.out.println("\t3: lock");
				System.out.println("\t4: unlock");
			}
					
		} while(true);
		
		sc.close();
	}
}
