package example.warehouse;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import animation.Animable;
import simulation.Process;

public class VanPainter extends Animable {
	private WarehouseDrawing drawing;
	private static final int width=5,height=5;
	private static final long timeFactor=300;
	private Van van;
    
    private static Color colors[]= {Color.red, Color.blue, Color.cyan, Color.green, Color.magenta, Color.orange, Color.pink };
    private static int curColor=0;
    private static Color pickColor() {
    	Color c=colors[curColor];
    	curColor++; if(curColor>=colors.length) curColor=0;
    	return(c);
    }

	public VanPainter(WarehouseDrawing drawing,Van van) {
		super(pickColor());
		this.drawing=drawing;
		this.van=van;
		this.instantMoveTo(500,drawing.qLine);
	}
	
	public void vantoQueue(double duration) {
		long millies=(long)duration*timeFactor;
//		Util.ASSERT(qLength==drawing.sim.waiting.size(),"qLength="+qLength+", size="+drawing.sim.waiting.size());
		int qLength=drawing.sim.waiting.size();
		
		updateQueue();
		moveTo(drawing.qPos+qLength*Van.height*2,drawing.qLine,millies);
		updateQueue();
	}

	 
	private void updateQueue() {
//		Util.BREAK("AnvanceQueue: START");
		int n=0;
		for(Process p:drawing.sim.waiting) {
			Van v=(Van)p;
//			v.vanPainter.instantMoveTo(drawing.qPos+n*Van.height*2,drawing.qLine);
			v.vanPainter.moveTo(drawing.qPos+n*Van.height*2,drawing.qLine,500);
			n=n+1;
		} 
//		Util.BREAK("AnvanceQueue: END");
	}
	 
	public void vantoPlatform(Dock dock,double duration) {
		long millies=(long)duration*timeFactor;
//		Util.BREAK("VantoPlatform: drPos="+drPos+", qLine="+qLine+", duration="+duration);
		int s1=1;
		int s2=7;
		int s3=4;
		long dur=millies/(s1+s2+s3);
		moveTo(drawing.drPos,drawing.qLine,dur*s1);
		
		updateQueue();

		van.dLine= drawing.dLine1 + (van.myDock.nr-1)*20 - 5;
		moveTo(drawing.drPos,van.dLine+5,dur*s2);

		van.dLine= drawing.dLine1 + (van.myDock.nr-1)*20;
		moveTo(drawing.pPos,van.dLine,dur*s3);
		van.trace(" is moved to plattform "+dock.nr);
	}
	

	public void vantoUnload(double duration) {
		long millies=(long)duration*timeFactor;
		moveTo(drawing.uPos,van.dLine,millies);
	}

	public void vantoWash(double duration) {
		long millies=(long)duration*timeFactor;
		moveTo(drawing.wPos,van.dLine,millies);
	}

	public void vanFinished(double duration) {
		long millies=(long)duration*timeFactor;
		moveTo(drawing.wPos+500,van.dLine,millies);
		//this.remove();
	}

	public void paint(Graphics2D g) {
		g.setColor(color);
		g.fill(new Rectangle2D.Double(currentX,currentY,width,height));
//		Util.IERR("Paint Van: X="+currentX+", Y="+currentY);
	}

}
