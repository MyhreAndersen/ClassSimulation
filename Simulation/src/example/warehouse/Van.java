package example.warehouse;

import animation.Animation;
import simulation.Process;
import simulation.Util;

public class Van extends Process {
	public static final	int width=5,height=5;
	double enterTime=10;		 // time used to enter the scene
	double leaveTime=10;		 // time used to leave the scene
	double washTime = 20;		 // time used to wash van
	double pTime = 5.0;			 // time used to move to platform
	double unloadSpeed = 100; // unloading speed;

	public final WarehouseSimulation warehouse;
	public final int load;
	
	static int nVan=1;
	int nr;
    double waitTime;
    Dock myDock;
    int dLine;
    int speed=200;
    WarehouseDrawing drawing;
	public VanPainter vanPainter;

	public Van(WarehouseSimulation warehouse,int load) {
		super(warehouse,"Van#"+(++nVan));
		this.warehouse=warehouse;
		this.load=load;
		this.nr=nVan;
		drawing=warehouse.drawing;
		vanPainter=new VanPainter(drawing,this);
	}

	@Override
	public void storyboard() {
		trace(" is entering the scene");
		vanPainter.vantoQueue(enterTime);
		hold(enterTime);

		
		myDock=grabFreeDock();
		if(myDock == null) {
			//*** all docks busy, wait in queue ***;
			waitTime= warehouse.time();
			Van.this.wait(warehouse.waiting);
			warehouse.accWait= warehouse.accWait+warehouse.time()-waitTime;     // accumulate waiting time;
			myDock=grabFreeDock();
			Util.ASSERT(myDock!=null,"Invariant");
		}
		Animation.repaint();

		trace(" maneuver to platform");
		vanPainter.vantoPlatform(myDock,pTime);				// drive to free dock;
		hold(pTime);											// maneuver to platform;
		
		trace(" is unloading");		
		double unloadTime= 1000.0*load/unloadSpeed;
		vanPainter.vantoUnload(pTime);
		hold(unloadTime);												// unload;
		
		trace(" is washing");		
		vanPainter.vantoWash(pTime);
		hold(washTime);                         // clean van;
		
		trace(" is leaving the scene");
		vanPainter.vanFinished(leaveTime);
		hold(leaveTime);												// unload;
		
		myDock.occupant=null;                    // this dock free again;
		if(!warehouse.waiting.isEmpty()) {         // signal waiting vans;
			Process van=warehouse.waiting.poll();
//			Util.BREAK("REMOVE FROM QUEUE: "+van+", this="+this);
			van.activateAfter(Van.this);
		}
		trace(" is terminating");

	}
	
	private Dock grabFreeDock() {
		Animation.repaint();
		warehouse.listDocks();
		for(Dock d:warehouse.platforms) {
			if(d.occupant==null) {
				d.occupant=this; // this dock busy now;
//				Util.BREAK("grabFreeDock: "+d);
				return(d);
			}
		}
		return(null);
	}
 

    public void trace(String msg) {
//    	if(verbose)
    	Animation.repaint();
        System.out.println(Van.this.name+msg);
    }


	public String toString() {
		return (this.name + "[State="+state+", evTime="+EVTIME+", Dock="+myDock+']');
	}

}
