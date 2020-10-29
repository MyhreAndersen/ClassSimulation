package example.simple;

import simulation.Simulation;
import simulation.Process;
import simulation.Util;

public class GENERATOR extends Process {

	public GENERATOR(Simulation simulation) {
		super(simulation,"GENERATOR");
	}

	@Override
	public void storyboard() {
		while(true) {
//		Util.BREAK(edObjectIdent()+" LOOP:"+(loopCnt++));
			hold(0.5);//*10-5);
//			Util.BREAK("GENERATOR - AFTER HOLD");
			CAR van=new CAR(simulation);
//			Util.BREAK("GENERATOR - NEW Van:"+van);
			van.activate();    // Medbragt mengde;
//			Util.BREAK("GENERATOR - END LOOP");
		}
	}

}
