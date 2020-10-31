package liftSample;

import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;

import liftSample.graphics.LiftDrawing;
import simulation.Process;
import simulation.Simulation;
import simulation.Util;

/**
 * Lift simulation
 * 
 * The program is a simulation model of an lift, or a number of
 * lifts in an office buiding.
 * 
 * The file “lift90.ini“ contain the control parameters for the simulation. You
 * can set the parameters in order to try out any number of lifts in a
 * planned building. This gives the possibility to test capacity of lifts in
 * different load environments.
 * 
 * Typical questions could be:
 * 
 * How many lifts do we need in this building to avoid congestion, taking
 * into consideration the expected arrival intervals of employees. Given number
 * of lifts, what is the average and maximum waiting times for people in the
 * building.
 * 
 * Speed and capacity of the lifts is also vital for the flow of the system.
 * A more sofisticated model would have to try to simulate the possibility of
 * different lifts running with different strategies.
 * 
 * 
 * 
 * @author Øystein Myhre Andersen
 *
 */
public class LiftSimulation extends Simulation {
//	double meanGiveupTime=0.30; //   Give up time (average)
//	double meanArrivalTime=30.0; //   Mean arrival time
	private static final double standardDeviation=0.5; //    Standard deviation
	static final double startTime=7.0; // Starting time in the morning
	private static final double endTime=19.0;  // Ending time in the evening

	static final int groundFloor=0;
	static final int lunchFloor=0;  // Lunch room floor
	
	int nPerson=1;//500; //    Number of persons in Simulation
	public int nFloor=10;//25; //     Number of floors
	public int nLift=3;//8;   //   Number of lifts
	
	double totalWaitingTime;
	
	Vector<Person> persons=new Vector<Person>();
    public Floor[] floors=new Floor[nFloor];
    Lift[] lifts=new Lift[nLift];
    Deque<Process> idlyLifts=new ArrayDeque<Process>();
    
    final LiftDrawing drawing;
 	
    
	public static void main(String[] args) {
		new LiftSimulation();
	}
	
    public LiftSimulation() {
    	runUntil(startTime);
    	this.setSyncTimeFactor(300);
//    	new Ticker(this).activate();
    	for(int j=0;j<nFloor;j++) floors[j]= new Floor(j);
        drawing=new LiftDrawing(this);
    	for(int j=0;j<nLift;j++) {
    		lifts[j]= new Lift(this);
    		lifts[j].activate();
    	}
//    	for(int j=0;j<nFloor;j++) floors[j]= new Floor(j);
//        drawing=new LiftDrawing(this);
    	for(int j=0;j<nPerson;j++) {
    		double Rhlp= Util.normal(startTime+1.0, standardDeviation);
    		Person p=new Person(this, Util.randint(1,nFloor-1));
    		persons.add(p);
    		p.activateAt(Rhlp);
    	}
    	snapshot("SIMULATION BEGIN");
    	runUntil(endTime);
		System.out.println("End of Simulation");
    	System.out.println("Total waiting time was: "+formatTime(totalWaitingTime));
    }

	@Override
	public String formatTime(double time) { // Using timeunit: hour
		while(time>24) time=time-24;
		String tm=LocalTime.ofSecondOfDay((long) (time*60*60)).toString();
		if(tm.length()<8) return(tm+":00");
		return(tm);
	}
	
    public synchronized void snapshot(String title) {
    	System.out.println("****************** SNAPSHOT: "+title+" at "+this.formatTime(time())+" ******************");
    	System.out.println("PERSONS: ");   for(Person p:persons) System.out.println("  "+p);
    	System.out.println("LIFTS: ");	   for(Lift lift:lifts) System.out.println("  "+lift);
    	System.out.println("FLOORS: ");	   for(Floor floor:floors) System.out.println("  "+floor);
    	System.out.println("IDLE-QUEUE:"); for(Process lift:idlyLifts) System.out.println("  "+lift);
    	System.out.println("SQS="+edSQS());
    	System.out.println("************************************ SNAPSHOT: END ************************************");
    }



}
