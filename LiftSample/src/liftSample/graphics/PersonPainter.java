package liftSample.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import animation.Animable;
import liftSample.Floor;
import liftSample.Lift;
import liftSample.Person;

public class PersonPainter extends Animable {
	public static final	int width=5,height=15;
//	private static final long timeFactor=300;
	Person person;
	Color color;
    
    private static Color colors[]= {Color.red, Color.blue, Color.cyan, Color.green, Color.magenta, Color.orange, Color.pink };
    private static int curColor=0;
    private static Color pickColor() {
    	Color c=colors[curColor];
    	curColor++; if(curColor>=colors.length) curColor=0;
    	return(c);
    }

	public PersonPainter(LiftDrawing drawing,Person person) {
		this.person=person;
		color=pickColor();
		instantMoveTo(500,drawing.baseLine);
	}
	
	public void personEnterUpQueue(Floor floor,double duration) {
		long millies=(long)duration*person.sim.getSyncTimeFactor();
		int qLength=floor.queueGoingUp.size();
//		int speed=2000;
		int qLine=floor.baseLine-10;
		int qPos=300; // start of queue;
		moveTo(qPos+qLength*height*2,qLine,millies);
	}

	public void personEnterDownQueue(Floor floor,double duration) {
		long millies=(long)duration*person.sim.getSyncTimeFactor();
		int qLength=floor.queueGoingDown.size();
//		int speed=2000;
		int qLine=floor.baseLine;// 10;
		int qPos=300; // start of queue;
		moveTo(qPos+qLength*height*2,qLine,millies);
	}

    public void personEntersLift(LiftPainter lift,double duration) {
		long millies=(long)duration*person.sim.getSyncTimeFactor();
    	double x=lift.currentX+LiftPainter.width;
    	double y=lift.currentY+LiftPainter.height-10;
    	moveTo(x, y, millies);
    }

    public void personLeavesLift(Lift lift,double duration) {
		long millies=(long)duration*person.sim.getSyncTimeFactor();
    	moveTo(currentX+400, currentY, millies);
    }

	public void paint(Graphics2D g) {
		g.setColor(color);
		g.fill(new Rectangle2D.Double(currentX,currentY,width,height));
	}

}
