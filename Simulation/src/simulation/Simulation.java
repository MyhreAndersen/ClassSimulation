package simulation;

import java.util.Deque;
import simulation.Process.State;


public class Simulation extends ContinuationScope {
//	public final SequencingSet SQS;
	public final Ranking sqs;
//	public MAIN_PROGRAM main;
	public boolean DEBUG;
	
	public Simulation() {
//		SQS=new SequencingSet();
		sqs=new Ranking("MAIN");
        sqs.bl = sqs;
        sqs.ll = sqs;
        sqs.rl = sqs;

		
//		main = new MAIN_PROGRAM(this);
//		main.EVENT = new EVENT_NOTICE(0, main);
//		SQS.add(main.EVENT,false);
	}
	
	public Ranking SQS_FIRST() { return(this.sqs.bl); }
	public Ranking SQS_LAST() { return(this.sqs.ll); }



	public Process current() {
		Ranking cur=SQS_FIRST();
//		Process cur2=SQS.first();
//		if(Process.DEBUG) {
//			if(cur!=null && cur!=SQS_FIRST()) {
//				Util.BREAK("Invariant FAILED !!!  Current: cur="+cur+", SQS_FIRST()="+SQS_FIRST());
//				while(true);
//			}
//		}
//		if(!Process.USE_RANKING) return(cur2);
		return((cur instanceof Process)?(Process)cur:null);			
	}

	public double time() {
		Process first=current();
		return ((first==null)?0:first.EVTIME);			
	}

	public String formatTime(double time) {
		return(""+time);
	}

	public void passivate() {
		Process cur = current();
		State prevState=cur.state;
		cur.state=State.suspended;
		SIM_TRACE("Passivate " + cur.name);
		cur.EVTIME = Process.NULLTIME;
		if (cur != null) Process.RANK_OUT(cur);
		if (Ranking.RANK_EMPTY(sqs)) throw new RuntimeException("Cancel,Passivate or Wait empties SQS");
		Process nxtcur = current();
		SIM_TRACE("END Passivate Resume[prevState="+prevState+", " + nxtcur.name + ']');
		if(prevState==State.active) Continuation.yield(this);
		SIM_TRACE("END Passivate AFTER Resume[" + nxtcur.name + ']');
	}

	public void wait(final Deque<Process> S) {
		current().wait(S);
	}
	
	private boolean running;
	public void runUntil(final double T) {
		running=true;
		Process MAIN_PROGRAM=new Process(this,"MAIN_PROGRAM:") {
			@Override
			protected void storyboard() {
				SIM_TRACE("Simulation.runUntil: terminates");
				snapshot("Simulation.runUntil terminates");
				running=false;
			}
		};
		MAIN_PROGRAM.activateAt(T);
//		snapshot("MAIN-1");
			
//		while(time() < T) {
		while(running) {
//			Util.BREAK("EXECUTE: BEFORE RUN: "+current()+", State="+current().state);
			if(current()==null) return;
			if(syncTimeFactor>0) syncTime();
			current().runNextEvent();
			if(current()==null) return;
//			Util.BREAK("EXECUTE: AFTER RUN: "+current()+", State="+current().state);			
		}
	}
	
	private long time1=System.currentTimeMillis();
	private long syncTimeFactor=300;
	public void setSyncTimeFactor(long syncTimeFactor) { this.syncTimeFactor=syncTimeFactor; }
	public long getSyncTimeFactor() { return(syncTimeFactor); }
	private void syncTime() {
		
		while(true) {
			double elapsed=System.currentTimeMillis()-time1;
			double lim=elapsed/syncTimeFactor;
			if(time() < lim) {
//				System.out.println("Time="+time()+"  Limit="+lim+ "   EXIT");
				break;
			}
//			System.out.println("Time="+time()+"  Limit="+lim+ "   Still waiting");
			try { Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//System.exit(0);
	}
	

	public void SIM_TRACE(final String msg) {
		if(DEBUG) {
			double time=time()+0.0005;
			int tt=(int)(time*1000);
			time=((double)tt)/1000;
			System.out.println("Time=" + formatTime(time()) + "  " + msg +", sqs="+ edSQS());
		}
	}

	public String edSQS() {
		StringBuilder s=new StringBuilder("[");
		boolean first=true;
		for(Ranking r=Ranking.RANK_FIRST(sqs);r!=null;r=Ranking.RANK_SUC(r)) {
			if(!first) s.append(','); first=false;
			s.append(r.name);
			Process p=(Process)r;
			s.append("at").append(formatTime(p.evtime()));
//			Util.BREAK("edSQS: r="+r);
		}
		s.append(']');
//		Util.BREAK("edSQS: "+s.toString());
		return(s.toString());
	}
	
    public void snapshot(String title) {
    	System.out.println("****************** SNAPSHOT: "+title+" ******************");
    	System.out.println("SQS="+edSQS());
    	System.out.println("****************** SNAPSHOT: END ******************");
	    Util.BREAK("SNAPSHOT: continue ?");
    }
	
	void listQueue(final String title,final Deque<Process> queue) {
		System.out.println("================ LIST QUEUE "+title+" ("+queue.size()+") ================");
		for(Process p:queue) System.out.println(p);
	}
}
