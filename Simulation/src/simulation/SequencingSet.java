package simulation;

import java.util.Comparator;
import java.util.TreeSet;

public class SequencingSet {
	private final TreeSet<Process> sequencingSet;

	private Comparator<Process> compareEvents=new Comparator<Process>() {
		public int compare(Process first, Process second) {
			if(first.KEY < second.KEY) return(-1);
			if(first.KEY > second.KEY) return(+1);
			return(0);
		}
	};

	public SequencingSet() {
		sequencingSet=new TreeSet<Process>(compareEvents);
	}
	
	public boolean isEmpty() { 
		return(sequencingSet.isEmpty());
	}
	
	public void add(Process ev,boolean prior) {
//		Util.BREAK("SQS:ADD prior="+prior+", "+ev);
		ev.KEY=ev.EVTIME;
		boolean done=sequencingSet.add(ev);
		while(!done) {
			ev.KEY=(prior)?Math.nextDown(ev.KEY):Math.nextUp(ev.KEY);
			done=sequencingSet.add(ev);
		}
//		Util.BREAK("CURRENT SQS="+this);
	}
	
	public void remove(Process ev) {
		boolean done=sequencingSet.remove(ev);
		Util.ASSERT(done,"SequencingSet.remove");
	}
	
	public Process pollFirst() {
		return(sequencingSet.pollFirst());
	}
	
	public Process first() {
		//System.out.println("sequencingSet="+this);
		if(sequencingSet.isEmpty()) return(null);
		return(sequencingSet.first());
	}
	
	public Process nextAfter(Process ev) {
		Process nxt=sequencingSet.higher(ev);
		return(nxt);
	}

//	private void checkSQS() {
//		for(EVENT_NOTICE ev:sequencingSet) {
//			if(ev.PROC.STATE$==OperationalState.terminated)
//				throw new RuntimeException("SQS CONTAIN TERMINATED ELEMENT: "+ev.PROC);
//		}
//	}

	public String toString() {
		//checkSQS();
		StringBuilder s = new StringBuilder();
		for(Process e:sequencingSet) {
			String procid=e.name;
			s.append(' ').append(procid).append('[').append(e.EVTIME).append(']');			
		}
		return (s.toString());
	}


}
