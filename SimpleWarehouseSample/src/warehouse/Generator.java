package warehouse;

import simulation.Util;
import simulation.Process;

public class Generator extends Process {
	WarehouseSimulation simulation;
//	int arrInterval;
//	float arrivals[]= { 59, 19, 13, 4, 1, 1, 1, 1, 1};
//	float AvrgLoad[]= { 9, 20, 22, 14, 9, 3, 2, 9, 4};

	static int loopCnt;

	public Generator(WarehouseSimulation scope) {
		super(scope,"Generator");
		this.simulation=scope;
//		Util.ASSERT(this.sequ==1,"Invariant: Generator sequ="+sequ);
	}

	@Override
	public void storyboard() {
		while(true)
//		for(int i=0;i<6;i++)
		{
//			Util.BREAK(edObjectIdent()+" LOOP:"+(loopCnt++));
			
//			arrInterval= Util.histd(arrivals);       // arrivals gir fordeling;
//			arrInterval=2; // TEMP
			
			double arrivalsPerTimeUnit=0.07;//0.2;
			double arrInterval=Util.negexp(arrivalsPerTimeUnit);
			
//			Util.BREAK("GENERATOR - BEFORE HOLD");
			hold(arrInterval);//*10-5);
//			Util.BREAK("GENERATOR - AFTER HOLD");
			simulation.Totnr= simulation.Totnr + 1;
			int load=4;//Util.histd(AvrgLoad);
			Van van=new Van(simulation,load);
//			Util.BREAK("GENERATOR - NEW Van:"+van);
			van.activate();    // Medbragt mengde;
//			Util.BREAK("GENERATOR - END LOOP");
		}
	}

}
