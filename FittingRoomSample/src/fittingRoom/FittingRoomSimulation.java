package fittingRoom;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;

import animation.Animation;
import simulation.Process;
import simulation.Simulation;
import warehouse.Dock;

//Class FittingRoom; Begin
//	Ref (Head) door;
//	Boolean inUse;
//	Procedure request; Begin
//	   If inUse Then Begin
//	       Wait (door);
//	       door.First.Out;
//	   End;
//	   inUse:= True;
//	End;
//	Procedure leave; Begin
//	   inUse:= False;
//	   Activate door.First;
//	End;
//	door:- New Head;
//
//  Ref (FittingRoom) fittingRoom1; 
//
//  fittingRoom1:- New FittingRoom; 
//  Activate New Person ("Sam"); 
//  Activate New Person ("Sally"); 
//  Activate New Person ("Andy"); 
//  Hold (100); 
//
//End;


public class FittingRoomSimulation extends Simulation {
	FittingRoomSimulation fittingRoom1;
	
	boolean inUse;
	Deque<Process> door=new ArrayDeque<Process>();
	
	class FittingRoom {
		Deque<Process> door=new ArrayDeque<Process>();
		boolean inUse;

		public void request(Person p) {
			if(inUse) {
		       wait (door);
				listDoor();
		       if(!door.isEmpty())
		    	   door.removeFirst();
			}
		   inUse=true;
			
		}

		public void leave() {
			inUse=false;
//			listDoor();
			Person next=(Person) door.poll();
			if(next!=null) next.activate();
		}


		public FittingRoom() {
			DEBUG=true;
			fittingRoom1=this;
			new Person(this,"Sam").activate();
			new Person(this,"Sally").activate();
			new Person(this,"Andy").activate();
			runUntil(100);
		}
	}

	public FittingRoomSimulation() {
		DEBUG=true;
		fittingRoom1=this;
		new Person(this,"Sam").activate();
		new Person(this,"Sally").activate();
		new Person(this,"Andy").activate();
		runUntil(100);
	}

	public void request(Person p) {
		if(inUse) {
	       wait (door);
			listDoor();
	       if(!door.isEmpty())
	    	   door.removeFirst();
		}
	   inUse=true;
		
	}

	public void leave() {
		inUse=false;
//		listDoor();
		Person next=(Person) door.poll();
		if(next!=null) next.activate();
	}


	void listDoor() {
		System.out.println("================ LIST DOCKS ("+door.size()+") ================");
		for(Process d:door) System.out.println(d);
	}
	
	public static void main(String[] args) {
		new FittingRoomSimulation();
	}

}
