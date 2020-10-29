package animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayDeque;
import java.util.Deque;

public abstract class Animable {
	public double currentX;
	public double currentY;
	public Color color;
	
	public Animable(Color color) {
		this.color=color;
		Animation.RENDERING_SET.add(this);
	}

	public Animable() {
		this(Color.black);
	}
	
	public void remove() {
		Animation.RENDERING_SET.remove(this);
	}

	public synchronized void instantMoveTo(final double x,final double y) {
		currentX=x; currentY=y;
		Animation.repaint();
	}
    
    // speed = pixels per milli-second ???
	public synchronized void moveTo1(final double x,final double y,final double speed) {
		final double dx=(x-currentX)/500;
		final double dy=(y-currentY)/500;
		int wait=(int)speed/100;
		if(wait<1) wait=1;
		if(wait>50) wait=50;
		final int waiting=wait;
//		new Thread() {
//			public void run() {
				for(int i=0;i<500;i++) {
					instantMoveTo(currentX+dx,currentY+dy);
					try {Thread.sleep(waiting);}catch(Exception e) {}
				}
				instantMoveTo(x,y);				
//			}
//		}.start();
	}
    
    // speed = pixels per milli-second ???
	boolean running=false;
	public synchronized void moveTo2(final double x,final double y,final double speed) {
		final double dx=(x-currentX)/500;
		final double dy=(y-currentY)/500;
		int wait=(int)speed/100;
		if(wait<1) wait=1;
		if(wait>50) wait=50;
		final int waiting=wait;
//		while(running) try {Thread.sleep(1);}catch(Exception e) {}
		running=true;
		new Thread() {
			public void run() {
				for(int i=0;i<500;i++) {
					instantMoveTo(currentX+dx,currentY+dy);
					try {Thread.sleep(waiting);}catch(Exception e) {}
				}
				instantMoveTo(x,y);
				running=false;
			}
		}.start();
	}


	private static final int stepTime=2;
	public void moveTo3(final double x,final double y,final long duration) {
//		while(running) Thread.yield();
		final long nStep=duration/stepTime;
		final double dx=(x-currentX)/nStep;
		final double dy=(y-currentY)/nStep;
//		running=true;
//		new Thread() {
//			public void run() {
				for(long n=nStep;n>0;n--) {
					instantMoveTo(currentX+dx,currentY+dy);
					try {Thread.sleep(stepTime);}catch(Exception e) {}
				}
				instantMoveTo(x,y);
//				running=false;
//			}
//		}.start();
	}

	


	public void moveTo(final double x,final double y,final long duration) {
		while(running) Thread.yield();
		final long nStep=duration/stepTime;
		final double dx=(x-currentX)/nStep;
		final double dy=(y-currentY)/nStep;
		running=true;
		new Thread() {
			public void run() {
				for(long n=nStep;n>0;n--) {
					instantMoveTo(currentX+dx,currentY+dy);
					try {Thread.sleep(stepTime);}catch(Exception e) {}
				}
				instantMoveTo(x,y);
				running=false;
			}
		}.start();
	}

	
	public abstract void paint(Graphics2D g);

}
