package liftSample.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import animation.Animable;
import liftSample.Lift;
import liftSample.Person;

public class LiftPainter extends Animable {
	public static final	int width=25,height=45;
	final Lift lift;
	double baseX;
	double baseY;

	public LiftPainter(Lift lift,LiftDrawing drawing) {
		this.lift=lift;
		baseX=70+(40*lift.nr);
		baseY=drawing.baseLine-height;//-40;
		this.instantMoveTo(baseX,baseY);
	}
	
	public void moveTo(int floor,double duration) {
		long millies=(long)duration*lift.sim.getSyncTimeFactor();
		double y=baseY-(LiftDrawing.floorHeight*floor);
		moveTo(baseX,y,millies);
		int dx=0;
		for(Person p:lift.load) {
			p.personPainter.moveTo(baseX+dx+1,y+height-PersonPainter.height,millies);
			dx=dx+6;
		}
	}

	public void paint(Graphics2D g) {
		g.setColor(Color.darkGray);
		g.draw(new Rectangle2D.Double(currentX,currentY,width,height));
//		int dx=0;
//		for(Person p:lift.load) {
//			p.personPainter.instantMoveTo(currentX+dx+1, currentY+height-PersonPainter.height-1);
//			dx=dx+6;
//		}
	}

}
