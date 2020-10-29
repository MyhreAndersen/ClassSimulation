package example.lift.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import animation.Animation;
import example.lift.Floor;
import example.lift.LiftSimulation;

public class LiftDrawing extends Animation {

	public static final int floorHeight=50;
	static final int liftWidth=16;
	static final int liftHeight=32;
	
	LiftSimulation sim;
	public final int baseLine;

	public LiftDrawing(LiftSimulation sim) {
		super("Lift Simulation",500,700);
		this.sim=sim;
		this.baseLine=floorHeight*sim.nFloor;
//		paintBackground();
	}

	public void paintBackground(Graphics2D g) {
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		int dLine = 16;
//		this.setFillColor(Color.black);
		for(int i=0;i<=sim.nLift;i++) {
			int pPos=100+40*i;
			int h=floorHeight*sim.nFloor;
			//fillRectangle(pPos,dLine-8,4,h);
			//public ShapeElement fillRectangle(final double x,final double y,final double width,final double height)
			//{ ShapeElement elt=new ShapeElement(this); elt.fillRectangle(x,y,width,height); return(elt); }
			Shape shape=new Rectangle2D.Double(pPos,dLine-8,4,h);
			g.setColor(Color.black); g.fill(shape);
			
		}

		for(int i=0;i<sim.nFloor;i++) {
			Floor floor=sim.floors[i];
//			System.out.println("Floor "+i+"   "+floor);
			floor.baseLine=floorHeight*(sim.nFloor-i);
			int tPos=20;
			g.drawString(""+(i+1)+":",tPos,floor.baseLine);
		}
		g.drawString(sim.formatTime(sim.time()),400,50);
	}


}
