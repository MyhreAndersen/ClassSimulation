package liftSample;

import animation.Animation;
import simulation.Process;
//import simulation.SimulationLog;
//import simulation.Util;

public class Ticker extends Process {
//	public final SimulationLog console;

	public Ticker(LiftSimulation simulation) {
		super(simulation,"Ticker");
//		console = new SimulationLog(this);
	}

	@Override
	public void storyboard() {
		while(true) {
//			double tt=0.01; // TickTime
			double tt=(1.0/60);///60;///10; // TickTime
			hold(tt);
//			Util.BREAK("GENERATOR - AFTER HOLD");
//			console.write("TICK: "+simulation.formatTime(simulation.time())+"\n");
//			System.out.println("TICK: "+simulation.formatTime(simulation.time()));
			Animation.repaint();
			if(simulation.time() > LiftSimulation.startTime )
			try { Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

}
