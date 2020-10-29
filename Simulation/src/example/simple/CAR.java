package example.simple;

import simulation.Simulation;
import simulation.Process;

public class CAR extends Process {
	private static int SEQU;

	public CAR(Simulation simulation) {
		super(simulation,"Car#"+(++SEQU));
	}

	@Override
	public void storyboard() {
		SIM_TRACE(name+" is entering the scene");
		//	vanShape:-new ShapeElement;
		hold(0.2);
		SIM_TRACE(name+" secod phase");
		hold(0.3);
		SIM_TRACE(name+" third phase");
		hold(0.1);
		SIM_TRACE(name+" is terminating");
	}
	

    public void trace(String msg) {
//    	if(verbose)
        System.out.println(name+msg);
    }

}
