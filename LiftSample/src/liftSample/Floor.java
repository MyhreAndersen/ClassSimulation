package liftSample;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;

import simulation.Process;
import simulation.Util;

public class Floor {
	int nr;
	public Deque<Process> queueGoingUp= new ArrayDeque<Process>(); // Wait queue of men going up
	public Deque<Process> queueGoingDown= new ArrayDeque<Process>(); // Wait queue of men going down
    Vector<Lift> upGoingLifts= new Vector<Lift>();
    Vector<Lift> downGoingLifts= new Vector<Lift>();
    
    public int baseLine;
    
    Floor(int nr){
    	this.nr=nr;
		System.out.println("NEW Floor: "+this);
    }
    
    private void CHECK(Lift lift) {
    	int cnt = 0;
    	for(Lift lft:upGoingLifts) if(lft==lift) cnt++;
    	for(Lift lft:downGoingLifts) if(lft==lift) cnt++;
    	if(cnt!=0) {
    		//Util.IERR("FAILED");
    		Util.exit();
    	}
    }
    
    void liftEnterFloor(Lift lift) {
    	CHECK(lift);
    	switch(lift.state) {
    		case wayup: upGoingLifts.add(lift); break;
    		case waydown: downGoingLifts.add(lift); break;
    		default: Util.IERR("Impossible");
    	}
    }
    
    void liftLeaveFloor(Lift lift) {
//    	switch(lift.state) {
//    		case wayup: upGoingLifts.remove(lift); break;
//    		case waydown: downGoingLifts.remove(lift); break;
//    		default: Util.IERR("Impossible");
//    	}
		upGoingLifts.remove(lift);
		downGoingLifts.remove(lift);
    	CHECK(lift);
    }

    boolean anyoneGoingUp()   {
//    	return(!queueGoingUp.isEmpty() && queueGoingUp.firstElement()!= null);
    	boolean res= !queueGoingUp.isEmpty();
//    	if(res) System.out.println("anyoneGoingUp("+nr+"): UP Queue="+queueGoingUp);
    	return(res);
    }
    
    boolean anyoneGoingDown() {
//    	return(!queueGoingDown.isEmpty() && queueGoingDown.firstElement()!= null);
    	boolean res= !queueGoingDown.isEmpty();
//    	if(res) System.out.println("anyoneGoingDown("+nr+"): DOWN Queue="+queueGoingDown);
    	return(res);
    }
    
//    void PushUp() {
//    	if(!Idly.empty) {
//    		Activate Idly.first after Current;
//    		Idly.first.out;
//    	}
//    }
    
//    void PushDown() {
//    	if(!Idly.empty) {
//    		Activate Idly.first after Current;
//    		Idly.first.out;
//    	}
//    }

    void list(String title) {
    	System.out.println(title);
    	System.out.println("UP   Queue="+queueGoingUp);
    	System.out.println("DOWN Queue="+queueGoingDown);
    	System.out.println("UP   Lifts="+upGoingLifts);
    	System.out.println("DOWN Lifts="+downGoingLifts);

    }
    
    String listLifts(Vector<Lift> lifts) {
    	StringBuilder s=new StringBuilder();
    	char sep='[';
    	for(Lift lift:lifts) { s.append(sep).append(lift.name); sep=','; }
    	s.append(']');
    	return(s.toString());
    }
    
    public String toString() {
    	StringBuilder s=new StringBuilder();
    	s.append("Floor#"+nr+": ");
    	if(anyoneGoingUp()) s.append(" Q-UP:").append(queueGoingUp);
    	if(anyoneGoingDown()) s.append(" Q-DOWN:").append(queueGoingDown);
    	if(!upGoingLifts.isEmpty()) s.append(" UP-Lifts:").append(listLifts(upGoingLifts));
    	if(!downGoingLifts.isEmpty()) s.append(" DOWN-Lifts:").append(listLifts(downGoingLifts));
    	return(s.toString());
    }
    
}
