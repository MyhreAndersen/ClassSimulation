package example.lift;

import simulation.SimulationLog;

import example.lift.graphics.LiftDrawing;
import example.lift.graphics.PersonPainter;
import simulation.Process;
import simulation.Util;

public class Person extends Process {
	private static int SEQU;
	private static final double enterTime=0.1;		 // time used to enter the scene
	private static final double leaveTime=0.1;		 // time used to leave the scene
	private static final double getinTime=0.2;//0.002; //  Time for man to get in elevator (average)
	private static final double getoutTime=0.2;//0.002; // Time for man to get out of elevator
	private final SimulationLog console;

	public LiftSimulation sim;
	private LiftDrawing drawing;
	public PersonPainter personPainter;
	private enum State { neutral, queuedUp, wayUp, working, queuedDown, wayDown, lunch, goingHome };
	private State state;

	private int officeFloor; // Office Floor
    private Lift lift;
    private double arrival; // Arrival time
    private double lunch;   // Lunch time
    private double Leave;   // End of workday
    
	int requestedDestination;
    Floor currentFloor;
    double waiting; // This Persons total waiting time in queue for lift.

	public Person(LiftSimulation simulation,int officeFloor) {
		super(simulation,"Person#"+(++SEQU));
		state=State.neutral;
		this.sim=simulation;
		this.officeFloor=officeFloor;
		console = (SEQU>6)?null : new SimulationLog(this);
		drawing=sim.drawing;
		personPainter=new PersonPainter(drawing,this);
	}

	@Override
	protected void storyboard() {
		arrival= sim.time();
		Leave= arrival + 8.0 + Util.uniform(-1.0, 1.0);
		lunch= arrival + 4.0 + Util.uniform(-0.5, 1.0);
		
		trace("storyboard: BEGIN");
		currentFloor= sim.floors[LiftSimulation.groundFloor];
		requestLiftGoingUp();
		personEntersLift(State.wayUp);
		requestRideTo(officeFloor);
		personLeavesLift(State.working);
		lift.activateAfter(sim.current());
		sim.current().reactivateAt(lunch);
		
		trace("TIME FOR LUNCH");
		requestLiftGoingDown();
		personEntersLift(State.wayDown);
		requestRideTo(LiftSimulation.lunchFloor);
		personLeavesLift(State.lunch);
		lift.activateAfter(sim.current());
		sim.current().reactivateAt(lunch+0.5);
		
		trace("END OF LUNCH");
		requestLiftGoingUp();
		personEntersLift(State.wayUp);
		requestRideTo(officeFloor);
		personLeavesLift(State.working);
		lift.activateAfter(sim.current());
		sim.current().reactivateAt(Leave);
		
		trace("END OF WORK-DAY");
		requestLiftGoingDown();
		personEntersLift(State.wayDown);
		requestRideTo(LiftSimulation.groundFloor);
		personLeavesLift(State.goingHome);
		lift.activateAfter(sim.current());
		sim.totalWaitingTime= sim.totalWaitingTime + waiting;
		
		trace("GOING HOME");
	}

	private void requestLiftGoingUp() {
    	trace("arrives and request Lift going Up at "+currentFloor);
    	personPainter.personEnterUpQueue(currentFloor,enterTime);
//    	hold(enterTime);
		lift= getLiftGoingUp();
//		trace("getLiftGoingUp: "+lift);
		while(lift == null || !lift.anySpaceLeft() ) {
			pushUpButton();
//			trace("Enter queue going up at currentFloor: "+currentFloor);
			state=State.queuedUp;
			double time=sim.time();
			wait(currentFloor.queueGoingUp);   // Enter queue going up;
			waiting= waiting+sim.time()-time;
			lift= getLiftGoingUp();
//			trace("Retry LiftGoingUp: "+lift);
		}
    	trace("got Lift going Up at "+currentFloor);
	}

    private Lift getLiftGoingUp() {
    	for(Lift lift:currentFloor.upGoingLifts) { if(lift.anySpaceLeft()) return(lift); }
    	return(null);
    }
    
    private void requestLiftGoingDown() {
    	trace("arrives and request Lift going Down at "+currentFloor);
    	personPainter.personEnterDownQueue(currentFloor,enterTime);
    	lift= getLiftGoingDown();
    	while(lift == null || !lift.anySpaceLeft() ) {
    		pushDownButton();
    		state=State.queuedDown;
			double time=sim.time();
    		wait(currentFloor.queueGoingDown); //! Enter queue going down;
			waiting= waiting+sim.time()-time;
    		lift= getLiftGoingDown();
    	}
    	trace("got Lift going Down at "+currentFloor);
    }

    private Lift getLiftGoingDown() {
    	for(Lift lift:currentFloor.downGoingLifts) { if(lift.anySpaceLeft()) return(lift);	}
    	return(null);
    };
    
    private void pushUpButton() {
    	trace("Push UpButten at "+currentFloor);
    	if(!sim.idlyLifts.isEmpty()) {
    		sim.idlyLifts.poll().activateAfter(sim.current());
    	}
    }

    private void pushDownButton() {
    	trace("Push DownButten at "+currentFloor);
    	if(!sim.idlyLifts.isEmpty()) {
    		sim.idlyLifts.poll().activateAfter(sim.current());
    	}
    }

    private void personEntersLift(State newState) {
    	trace("enters "+ lift.name +" at "+currentFloor);  
    	personPainter.personEntersLift(lift.liftPainter,getinTime);
		lift.load.add(this);
		state=newState;
		hold(getinTime);
    }
    
    private void requestRideTo(int dest) {
    	requestedDestination=dest;
    	lift.pushButton(dest);
    	trace("request ride with "+ lift.name +" to "+sim.floors[dest]);    	
    	lift.activateAfter(sim.current());
    	sim.passivate();
    }
    
    private void personLeavesLift(State newState) {
    	trace("leaves "+ lift.name +" at "+currentFloor);   
    	personPainter.personLeavesLift(lift,getoutTime+leaveTime);
		hold(getoutTime);
    	lift.load.remove(this);
		state=newState;
//    	personPainter.personLeavesLift(lift,getoutTime);
    }
    
    private void trace(String msg) {
		String line="Time=" + sim.formatTime(sim.time()) + ":  " +this.name+": "+ msg +", SQS="+simulation.edSQS();
		System.out.println(line);
		if(console!=null) console.write(line+"\n");
    }

	@Override
	public String toString() {
    	StringBuilder s=new StringBuilder();
    	s.append(this.name+"[").append(state);
    	s.append(", requestedDestination=").append(requestedDestination);
    	if(currentFloor!=null) s.append(", currentFloor=").append(currentFloor.nr);
    	if(lift!=null) s.append(", Lift=").append(lift.nr);
//        double GiveUp, Leave, lunch, waiting, arrival;
    	s.append(']');
    	return(s.toString());
    }

}
