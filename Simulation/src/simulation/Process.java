package simulation;

import java.util.Deque;

public abstract class Process extends Ranking {
	public static final boolean DEBUG=true;

	public static final int NULLTIME= -1;
	protected Simulation simulation;
	Continuation continuation;
	public double KEY;
	public double EVTIME= NULLTIME;

	enum State { initial, active, suspended, terminated }
	protected State state;

	public Process(Simulation simulation,String name) {
		super(name);
		this.simulation=simulation;
		this.state=State.initial;
	}
	
	private boolean scheduled() { return(EVTIME >= 0); }

	private class Runner extends Continuation{
		private ContinuationScope simulation;
		
		public Runner(ContinuationScope simulation, Runnable target) {
			super(simulation, target);
			this.simulation=simulation;
		}
		
		public String toString() {
			return("Runner["+name+" simulation="+simulation.getClass().getSimpleName()+']');
		}
	}
	
	protected abstract void storyboard();  // Norwegian 'dreiebok'
//	protected abstract void screenplay();
	
	public void runNextEvent() {
		switch(state) {
			default: case active: case terminated:
					System.out.println("ERROR: Illegal state "+state);
					System.exit(0);
			case initial:
					Util.ASSERT(continuation==null,"Invariant");
//					continuation=new Runner(simulation,activePhases());
					Runnable target=new Runnable() { public void run() { storyboard(); }};
					continuation=new Runner(simulation,target);
					// NOTE: No break - Fallthrough
			case suspended: // OK
					Util.ASSERT(continuation!=null,"Invariant");
		}
		state=State.active;
		continuation.run();
		state=State.suspended;
		if(continuation.isDone()) {
			state=State.terminated;
//			Util.BREAK(this.name+": ALL PHASES DONE !!");
//			Util.printStackTrace();
			continuation=null;
//			Process frst = simulation.SQS.pollFirst();
			Ranking first=RANK_FIRST(simulation.sqs);
			RANK_OUT(first);
//			Util.ASSERT(first==frst,"Invariant");
//			Util.BREAK("runActivePhases: first="+first+", SQS="+simulation.SQS);
			Util.ASSERT(first==this,"Invariant");
		}
	}

	public void hold(final double time) {
		Process first = simulation.current();
		RANK_OUT(this);
		if (time > 0) this.EVTIME = this.EVTIME + time;
		RANK_INTO(first,simulation.sqs,this.EVTIME);
		SIM_TRACE(name+":Hold " + time);
		if(this == first) {
			state=State.suspended;
			Continuation.yield(simulation);
		}
	}

	public void cancel() {
		SIM_TRACE("Cancel " + this);
		if (this == simulation.current()) simulation.passivate();
		else if (this.scheduled()) {
//			simulation.SQS.remove(this);
			RANK_OUT(this);
			
			this.EVTIME = NULLTIME;
			SIM_TRACE("Cancel " + this);
			if(this.state==State.active) {
				this.state=State.suspended;
				Continuation.yield(simulation);
			}
		}
	}
	
	public void wait(final Deque<Process> queue) {
		SIM_TRACE(name+":Wait in Queue " + queue);
		queue.add(this);
//		simulation.listQueue("Before WAIT: "+this,queue);
		cancel();
		
//		queue.remove(this); // TODO: Check dette
		
//		simulation.listQueue("After WAIT: "+this,queue);
        SIM_TRACE(name+":END WAITING: ");
	}

	public boolean idle() {
		return (!scheduled());			
	}

	public double evtime() {
		if (idle())
			throw new RuntimeException("Process.Evtime:  The process is idle.");
		return (EVTIME);			
	}

	public Process nextev() {
		if (idle())	return (null);
//		Process next1=simulation.SQS.nextAfter(this);
		Process next=(Process) RANK_SUC(this);
//		Util.ASSERT(next1==next,"Invariant");
		return(next);
	}

	

	public void activate()			 { activateDirect(false); }
	public void activateDelay		 (final double time) { activateAt(false,simulation.time() + time,false); }
	public void activateDelayPrior	 (final double time) { activateAt(false,simulation.time() + time,true); }
	public void activateAt			 (final double time) { activateAt(false,time,false); }
	public void activateAtPrior		 (final double time) { activateAt(false,time,true); }
	public void activateBefore		 (final Process Y) { ACTIVATE3(false, true, Y); }
	public void activateAfter		 (final Process Y) { ACTIVATE3(false, false, Y); }

	public void reactivate()		 { activateDirect(true); }
	public void reactivateDelay      (final double time) { activateAt(true,simulation.time() + time,false); }
	public void reactivateDelayPrior (final double time) { activateAt(true,simulation.time() + time,true); }
	public void reactivateAt		 (final double time) { activateAt(true,time,false); }
	public void reactivateAtPrior	 (final double time) { activateAt(true,time,true); }
	public void reactivateBefore	 (final Process Y) { ACTIVATE3(true, true, Y); }
	public void reactivateAfter		 (final Process Y) { ACTIVATE3(true, false, Y); }

	private void activateDirect(final boolean REAC) {
		if (this.state==State.terminated) TRACE_ACTIVATE(REAC, "terminated process");
		else if (this.scheduled() && !REAC)  TRACE_ACTIVATE(REAC, "scheduled process");
		else {
//			TRACE_ACTIVATE(REAC, this.name);
			if (!REAC && this.scheduled()) {
				TRACE_ACTIVATE(REAC,"WITH NO EFFECT "+this.name);
				return;
			}
			Process z = simulation.current();
			this.EVTIME=simulation.time();
			
			if(simulation.current()==null)
				 RANK_INTO(this,simulation.sqs,this.EVTIME);
			else RANK_PRECEDE(this,simulation.current());
//			simulation.SQS.add(this,true);
			
//			if (REAC && this.scheduled()) {
////				simulation.SQS.remove(this);
//				RANK_OUT(this);
////				if (simulation.SQS.isEmpty()) throw new RuntimeException("(Re)Activate empties SQS.");
//				if (RANK_EMPTY(simulation.sqs)) throw new RuntimeException("(Re)Activate empties SQS.");
//			}
			TRACE_ACTIVATE(REAC,"DIRECT "+this.name);
			if (z != simulation.current()) {
//				Util.BREAK("ACTIVATE: "+this);
				if(z!=null && z.state!=State.initial) {
//					SIM_TRACE("ACTIVATE: Suspend "+this);
					z.state=State.suspended;
					Continuation.yield(simulation);
				}
			}
		}
	}

	private void activateAt(final boolean REAC,double time,final boolean PRIO) {
//		Util.BREAK("activateAt: X="+this+", TIME="+time+", PRIO="+PRIO);
		if (this.state==State.terminated) TRACE_ACTIVATE(REAC, "terminated process");
		else if (this.scheduled() && !REAC)  TRACE_ACTIVATE(REAC, "scheduled process");
		else {
//			TRACE_ACTIVATE2(REAC, this.name + " at " + time + ((PRIO) ? "prior" : ""));
			if (!REAC && this.scheduled()) return;
			Process z = simulation.current();
//			Util.BREAK("activateAt: time="+time+", TIME="+simulation.time());
			if (time < simulation.time())	time = simulation.time();
			this.EVTIME=time;
//			simulation.SQS.add(this,PRIO);
            if(PRIO) RANK_PRIOR(this,simulation.sqs,this.EVTIME);
            else RANK_INTO(this,simulation.sqs,this.EVTIME);
			
			TRACE_ACTIVATE(REAC, this.name + " at " + simulation.formatTime(time) + ((PRIO) ? "prior" : ""));
//			if (REAC && this.scheduled()) {
////				simulation.SQS.remove(this);
//				RANK_OUT(this);
////				if (simulation.SQS.isEmpty()) throw new RuntimeException("(Re)Activate empties SQS.");
//				if (RANK_EMPTY(simulation.sqs)) throw new RuntimeException("(Re)Activate empties SQS.");
//			}
			if (z != simulation.current()) {
//				Util.BREAK("ACTIVATE: this="+this);
				if(z!=null && z.state!=State.initial) {
					SIM_TRACE("(RE)ACTIVATE: Suspend "+z);
					z.state=State.suspended;
					Continuation.yield(simulation);
				}
			}
		}
	}

	private void ACTIVATE3(final boolean REAC,final boolean BEFORE,final Process Y) {
//		Util.BREAK("ACTIVATE3: X="+this+", BEFORE="+BEFORE+", Y="+Y);
		if (this.state==State.terminated)   TRACE_ACTIVATE(REAC, " terminated process: "+this);
		else if (this.scheduled() && !REAC) TRACE_ACTIVATE(REAC, " scheduled process: "+this);
		else if (this == Y)				    TRACE_ACTIVATE(REAC, " before/after itself");
		else {
//			Util.BREAK("ACTIVATE3: X="+this);
//			Util.BREAK("ACTIVATE3: Y="+Y);
//			TRACE_ACTIVATE3(REAC, this.name + ((BEFORE) ? " BEFORE " : " AFTER ") + Y.name);
			if (!REAC && this.scheduled()) return;
			Process z = simulation.current();
			if (Y != null) {
				if (this == Y)	return; // reactivate X before/after X;
				this.EVTIME=Y.EVTIME;
				
//				simulation.SQS.add(this,BEFORE);
				if(BEFORE) RANK_PRECEDE(this,Y); else RANK_FOLLOW(this,Y);
			}
			TRACE_ACTIVATE(REAC, this.name + ((BEFORE) ? " before " : " after ") + Y.name);
//			if (REAC && this.scheduled()) {
////				simulation.SQS.remove(this);
//				RANK_OUT(this);
////				if (simulation.SQS.isEmpty()) throw new RuntimeException("(Re)activate empties SQS.");
//				if (RANK_EMPTY(simulation.sqs)) throw new RuntimeException("(Re)activate empties SQS.");
//			}
			if (z != simulation.current()) {
				Util.BREAK("ACTIVATE: this="+this);
				if(z!=null && z.state!=State.initial) {
					SIM_TRACE("ACTIVATE: Suspend "+this);
					z.state=State.suspended;
					Continuation.yield(simulation);
				}
			}
		}
	}


	// *********************************************************************
	// *** TRACING AND DEBUGGING UTILITIES
	// *********************************************************************

	private void TRACE_ACTIVATE(final boolean REAC,final String msg) {
		String act = (REAC) ? "REACTIVATE " : "ACTIVATE ";
		SIM_TRACE(act + msg);
	}

	public void SIM_TRACE(final String msg) {
		if(simulation.DEBUG) {
			System.out.println("Time=" + simulation.formatTime(simulation.time()) + ":  " + msg +", SQS="+simulation.edSQS());
		}
	}
	


	public String toString() {
//		return ("Process["+this.name + ": STARTED=" + started()+ ", TERMINATED=" + terminated()+']');
		return ("Process["+this.name +']');
	}


}
