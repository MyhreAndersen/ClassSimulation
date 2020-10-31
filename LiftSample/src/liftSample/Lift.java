package liftSample;

import java.util.Vector;

import liftSample.graphics.LiftDrawing;
import liftSample.graphics.LiftPainter;
import simulation.SimulationLog;
import simulation.Process;
import simulation.Util;

public class Lift extends Process {
	private static int SEQU;
	private static final int maxload=4;//10;				  // Max load in one lift
	private static final double doorSlamTime=0.01;//0.001;    // Door slam time
	private static final double floorToFloorTime=0.3;//0.003; // Floor to floor time for elevator
	private static final double accelerationTime=0.1;//0.001; // Acceleration time for elevator
	private static final double decelerationTime=0.1;//0.001; // Deceleration time for elevator

	private final SimulationLog console;
	public final LiftSimulation sim;
	private final LiftDrawing drawing;
    private final boolean[] button;

    private int currentFloor;
	private Floor floor() { return(sim.floors[currentFloor]); }
	private int goal;
	private boolean opened;
    public Vector<Person> load=new Vector<Person>();
	public final int nr;
	final LiftPainter liftPainter;
	enum State {neutral, wayup, waydown };
	State state;


	public Lift(LiftSimulation simulation) {
		super(simulation,"Lift#"+(++SEQU));
		this.nr=SEQU;
		this.sim=simulation;
		this.state=State.neutral;
	    button=new boolean[sim.nFloor];
		console = new SimulationLog(this);
		drawing=sim.drawing;
		liftPainter=new LiftPainter(this,drawing);
	}
	
	boolean anySpaceLeft() { return(load.size() < maxload); }
    
	void pushButton(int sn) {
    	button[sn]= true;
    	trace(" button "+sn+" pushed");
    }

	@Override
	protected void storyboard() {
		int repeatCount=0;
		state= State.neutral;
		trace("storyboard: BEGIN");
		while(true) {
			sim.snapshot(name+" MAIN LOOP("+(repeatCount++)+")");
			
			// Elevator is empty at this stage, at least the first time;
			paintLiftAtPosition();

			// Any Request for me ?
			while(state == State.neutral) {
				goal= anyCallsUpFromFloor(currentFloor);
				if(goal>=0) state= State.wayup; else {
					goal= anyCallsDownFromFloor(currentFloor);
					if(goal>=0) state= State.waydown; else {
						goal= anyCallsUpFromFloor(0);
						if(goal>=0) state= State.waydown; else {
							goal= anyCallsDownFromFloor(sim.nFloor-1);
							if(goal>=0) state= State.wayup;
						}
					}
				}
				if(state == State.neutral) {
					trace("enter idle-queue: "+sim.idlyLifts);
					wait(sim.idlyLifts);
					trace("leave idle-queue: "+sim.idlyLifts);
				}
			}
			trace("is requested for floor "+goal+", currentFloor="+currentFloor);

			floor().liftLeaveFloor(this);

			if(goal != currentFloor) {
				int fDiff=Math.abs(goal-currentFloor);
//				liftPainter.moveTo(currentFloor,doorSlamTime+accelerationTime+fDiff*floorToFloorTime+decelerationTime);
				// floor().liftLeaveFloor(this);

				// Move Lift to goal
				if(opened) {
					//paintCloseDoor();
					opened= false;
					hold(doorSlamTime);
				} 
				hold(accelerationTime);
				if(state == State.waydown) {
					while(currentFloor > goal) {
						//paintLiftBelow(currentFloor);
						liftPainter.moveTo(currentFloor+1,accelerationTime+fDiff*floorToFloorTime+decelerationTime);
						hold(floorToFloorTime);
						currentFloor= currentFloor-1;
						//paintLiftAbove(currentfloor);
						if(! sim.floors[currentFloor].queueGoingDown.isEmpty()) goal= currentFloor;
					}
				} else
					if(state == State.wayup) {
						while(currentFloor < goal) {
							//paintLiftAbove(currentFloor);
							liftPainter.moveTo(currentFloor+1,accelerationTime+fDiff*floorToFloorTime+decelerationTime);
							hold(floorToFloorTime);
							currentFloor= currentFloor+1;
							//paintLiftBelow(currentFloor);
							if(! sim.floors[currentFloor].queueGoingUp.isEmpty()) goal= currentFloor;
						}
					}
				hold(decelerationTime);
			}


			trace("AtNewFloor: "+currentFloor);

			sim.snapshot("liftEnterFloor");
//			int fDiff=Math.abs(goal-currentFloor);
//			liftPainter.moveTo(currentFloor,doorSlamTime+accelerationTime+fDiff*floorToFloorTime+decelerationTime);
			floor().liftEnterFloor(this);
			
			paintLiftAtPosition();

			trace("arrived at Floor#"+currentFloor);
			if(! opened) {
				paintOpenDoor();
				hold(doorSlamTime);
				opened= true;
			}

			// Person(s) leaving here exit lift;
			button[currentFloor]= false;
//			listLoad(this.name+": anyone about to leave ? - load="+load);
			Vector<Person> leaving=new Vector<Person>();
			for(Person person:load) {
				if(person.requestedDestination==currentFloor) leaving.add(person);
			}
			for(Person person:leaving) {
				trace(person.name+" is leaving");
				load.remove(person);
				person.currentFloor=sim.floors[currentFloor];
				person.activateAfter(sim.current());
				sim.snapshot("");
				//    	         Util.exit();
				sim.passivate();	        	 
			}

			// Person entering elevator here;
//			Util.BREAK("Person entering elevator here: state="+state);
			//	         Util.exit();
			switch(state) {
			case wayup:
				while (! floor().queueGoingUp.isEmpty() && anySpaceLeft()) {
					//	               inspect floor().queueGoingUp do {
					//	            	if(floor().queueGoingUp != null) {
					//	                  Activate first after Current;
					Process p=floor().queueGoingUp.poll();
//					Util.BREAK("Person going up: "+p);
					p.activateAfter(sim.current());
					sim.passivate();
					//	            	}
				} break;
			case waydown:
				while (! floor().queueGoingDown.isEmpty() && anySpaceLeft()) {
					//	               inspect floor().queueGoingDown do {
					//	            	if(floor().queueGoingDown != null) {
					//	                  Activate first after Current;
					Process p=floor().queueGoingDown.poll();
//					Util.BREAK("Person going down: "+p);
					p.activateAfter(sim.current());
					sim.passivate();
					//	            	}
				} break;
			default: Util.IERR("Impossible");
			}
			
			
			if(! load.isEmpty()) {
				trace("is empty. Should it turn");
				switch(state) {
				case wayup:
					goal= sim.nFloor+1;
					for(int i=sim.nFloor-1;i>currentFloor;i--) if(button[i]) goal= i;
					if(goal > sim.nFloor) {
						for(int i=0;i<currentFloor;i++)	if(button[i]) goal= i;
						state= State.waydown;
					}
					break;
				case waydown:
					goal= -1;
					for(int i=0;i<currentFloor;i++)	if(button[i]) goal= i;
					if(goal < 0) {
						for(int i=sim.nFloor-1;i>currentFloor;i--) if(button[i]) goal= i;
						state= State.wayup; 
					}
					break;
				default: Util.IERR("Impossible");
				}
			} else {
				sim.snapshot(" TIME TO TURN ? ");
				state= State.neutral;
			}
		}
	}


	int anyCallsUpFromFloor(int n) {
		for(int j=n;j<sim.nFloor;j++)
			if(sim.floors[j].anyoneGoingUp()) return(j);
    	return(-1);	  
    }

    int anyCallsDownFromFloor(int n) {
		for(int j=n;j>=0;j--)
			if(sim.floors[j].anyoneGoingDown()) return(j);
		return(-1);
    }

	private void paintLiftAtPosition() {
		trace("AtPosition:");
	}

	private void paintOpenDoor() {
		trace("OpenDoor:");
	}
    
	private String edButtons() {
    	StringBuilder s=new StringBuilder();
    	s.append('[');
    	boolean first=true;
    	for(int i=0;i<button.length;i++) {
    		if(button[i]) {
    			if(!first) s.append('|'); first=false;
    			s.append(i);
    		}
    	}
    	s.append(']');
    	return(s.toString());
    }
	
	private void trace(String msg) {
		StringBuilder s=new StringBuilder();
		s.append("Time=").append(sim.formatTime(sim.time())).append(":  ");
		s.append(this.name).append(": "); 
		s.append(msg).append(", state=").append(state);
		s.append(", currentFloor=").append(currentFloor);
		if(goal>=0) s.append(", goal=").append(goal);
		if(opened) s.append(", door opened"); else s.append(", door closed");
		s.append(", buttons="+edButtons());
		if(!load.isEmpty()) s.append(", load=").append(load);
		String line=s.toString();
		System.out.println(line);
		console.write(line+"\n");
	}
	
	public String toString() {
		StringBuilder s=new StringBuilder();
		s.append(this.name).append(": ").append(" ").append(state);
		s.append(", currentFloor=").append(currentFloor);
		if(goal>=0) s.append(", goal=").append(goal);
		if(opened) s.append(", door opened"); else s.append(", door closed");
		s.append(", buttons="+edButtons());
		if(!load.isEmpty()) s.append(", load=").append(load);
		return(s.toString());
	}

}
