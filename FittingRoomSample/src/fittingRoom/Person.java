package fittingRoom;

import animation.Animation;
import simulation.Process;
import simulation.Util;

//Process Class Person (pname); Text pname; Begin
//	While True Do Begin
//	   Hold (Normal (12, 4, u));
//	   report  (pname & " is requesting the fitting room");
//	   fittingroom1.request;
//	   report (pname & " has entered the fitting room");
//	   Hold (Normal (3, 1, u));
//	   fittingroom1.leave;
//	   report (pname & " has left the fitting room");
//	End;
//End;

public class Person extends Process {
	FittingRoomSimulation scope;
	
	public Person(FittingRoomSimulation scope,String name) {
		super(scope,name);
		this.scope=scope;
	}
	
	FittingRoomSimulation fittingRoom1() {
		return(((FittingRoomSimulation)simulation).fittingRoom1);
	}

	@Override
	public void storyboard() {
		while(true) {
		hold(Util.normal(12,4));
		report(name + " is requesting the fitting room");
		fittingRoom1().request(this);
		report(name + " has entered the fitting room");
		hold(Util.normal (3, 1));
		fittingRoom1().leave();
		report(name + " has left the fitting room");
		}

	}
 

    public void report(String msg) {
        System.out.println("Person "+this+msg);
    }


	public String toString() {
		return (this.name + "[State="+state+", evTime="+EVTIME+']');
	}

}
