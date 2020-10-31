package warehouse;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Vector;

import animation.Animation;
import simulation.Process;
import simulation.Simulation;

public class WarehouseSimulation extends Simulation {
	public final int nDocks;
    final WarehouseDrawing drawing;

	Vector<Dock> platforms=new Vector<Dock>();
	Deque<Process> waiting=new ArrayDeque<Process>();

	double accWait;              // accumulate waiting time;
	int Totnr;                   // total number of Vans;

	public WarehouseSimulation(int nDocks) {
		this.nDocks=nDocks;
        drawing=new WarehouseDrawing(this);
		for(int i=1; i<=nDocks; i++) platforms.add(new Dock(i));
		new Reporter(this);
		new Generator(this).activate();

//		inner;

		Animation.repaint();
	}

	void listDocks() {
		System.out.println("================ LIST DOCKS ("+platforms.size()+") ================");
		for(Dock d:platforms) System.out.println(d);
	}

	
	public static void main(String[] args) {
		int minDocks=4;
		int maxDocks=8;

		for(int cnt= minDocks;cnt<maxDocks;cnt++) {
			WarehouseSimulation warehouseSimulation=new WarehouseSimulation(cnt);
			warehouseSimulation.runUntil(1000.0);//(1960.0);
		}
		System.out.println("End of Simulation");
	}

}
