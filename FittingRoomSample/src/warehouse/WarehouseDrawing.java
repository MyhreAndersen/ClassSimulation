package warehouse;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import animation.Animation;

public class WarehouseDrawing extends Animation {	
	final WarehouseSimulation sim;
	final int qLine=40, qPos=30;	// start of queue;
	final int drPos=15;				// driving to a dock;
	final int pPos=40;				// dock platform;
	final int uPos=120;				// unload position;
	final int wPos=200;				// car washery;
	final int dLine1=qLine+65;		// first dock;

	public WarehouseDrawing(WarehouseSimulation sim) {
		super("Warehouse Simulation",500,700);
		this.sim=sim;
	}
	  
	public void paintBackground(Graphics2D g) {
		int dLine;
		g.setColor(Color.gray);
		g.fill(new Rectangle2D.Double(qPos-8,qLine-8,4,16+Van.width));
		g.fill(new Rectangle2D.Double(qPos-4,qLine-8,100,4));
		g.fill(new Rectangle2D.Double(qPos-4,qLine+4+Van.width,100,4));
		
		for(int psl=1;psl<=sim.nDocks;psl++) {
			dLine= dLine1 + (psl-1)*20;
			g.fill(new Rectangle2D.Double(pPos-8,dLine-8,4,16+Van.width));
			g.fill(new Rectangle2D.Double(pPos-4,dLine-8,240,4));
			g.fill(new Rectangle2D.Double(pPos-4,dLine+4+Van.width,240,4));
		}
		g.setColor(Color.orange);
		g.setFont(g.getFont().deriveFont(Font.BOLD,12));
		g.drawString("Platform:",pPos,dLine1-15);
		g.drawString("Unload:",uPos,dLine1-15);
		g.drawString("Wash:",wPos,dLine1-15);  
	}

}
