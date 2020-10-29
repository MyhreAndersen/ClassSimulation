package example.simple;

import java.lang.String;
import java.lang.System;
import java.util.ArrayDeque;
import java.util.Deque;

import simulation.Simulation;
import simulation.Process;

public class Example2 {
	static final Simulation simulation=new Simulation();
    static Deque<Process> queue=new ArrayDeque<Process>();
	
    public static void main(String[] args) {
//    	example1();
    	example3();
    }

	// *********************************************************************
	// *** EXAMPLE 3: Using SequencingSet ( ala' Simulation)
	// *********************************************************************

    public static void example3() {
    	
    	CAR car1=new CAR(simulation); car1.activate();
    	CAR car2=new CAR(simulation); car2.activateAt(8.0);
    	CAR car3=new CAR(simulation); car3.activateAt(6.0);
    	CAR car4=new CAR(simulation); car4.activateAfter(car2);
    	new CAR(simulation).activateAt(14.0);
    	
    	car1.cancel();
    	car4.wait(queue);
    	
    	new GENERATOR(simulation).activate();
    	
//    	simulation.SQS.add(new CAR(simulation), false);
//    	simulation.SQS.add(new CAR(simulation), false);
//    	simulation.wait(queue);
//    	simulation.SQS.add(new CAR(simulation), false);
//    	simulation.wait(queue);
		
		
//    	simulation.resumeCurrent(null); // Will start Producer:Part 1.
    	simulation.runUntil(50); // Will start Producer:Part 1.
		System.out.println("End of program");
	}

}