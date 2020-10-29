package example;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import animation.Animable;
import animation.Animation;

public class AnimationTest extends Animation {

	public static void main(String[] args) {
		new AnimationTest();
	}

	public AnimationTest() {
		super("Animation Test",500,700);
		
		Animable elt2=new Animable(Color.magenta) {
			public void paint(Graphics2D g) {
				g.fill(new Rectangle2D.Double(currentX,currentY,200,50));
			}
		};
		elt2.instantMoveTo(100,100);

		Animable elt3=new Animable(Color.blue) {
			public void paint(Graphics2D g) {
				g.fill(new RoundRectangle2D.Double(currentX,currentY,200,50,50,50));
			}
		};
		elt3.instantMoveTo(100,100);

		Animable elt1=new Animable(Color.magenta) {
			public void paint(Graphics2D g) {
				g.fill(new Ellipse2D.Double(currentX,currentY,200,50));
			}
		};
		elt1.instantMoveTo(10,10);
		
		Animable txt1=new Animable(Color.orange) {
			public void paint(Graphics2D g) {
				//g.setFont(new Font(Font.SERIF,Font.PLAIN,12));
		    	g.setFont(new Font(Font.SERIF,Font.BOLD|Font.ITALIC,24));
		        g.drawString("Abracadabra",(float)currentX,(float)currentY);
			}
		};
		elt2.instantMoveTo(200,100);

		while(true) {
			txt1.color=Color.orange;
			elt3.moveTo(800,800,5000);
			elt1.moveTo(800,800,5000);
			elt1.moveTo(80,80,5000);
			elt3.moveTo(80,80,3000);
			elt1.moveTo(800,800,5000);
			elt2.moveTo(800,800,7000);
			elt1.moveTo(80,800,5000);
			elt2.moveTo(600,10,7000);
			txt1.instantMoveTo(300,700);
			elt3.moveTo(200,10,4000);
			elt1.moveTo(10,10,5000);
			txt1.color=Color.red;
			txt1.moveTo(100,400,5000);
			elt2.moveTo(100,100,7000);
			txt1.moveTo(200,100,5000);
		}

//		while(true)
//		{
//			elt3.moveTo(800,800,5000); //sleep(3000);
//			elt2.moveTo(800,800,700);  //sleep(3000);
//			elt3.moveTo(80,80,3000);   //sleep(4000);
//			elt3.moveTo(200,10,4000);  //sleep(5000);
//			elt2.moveTo(100,100,700);
//		}
	}
	
	public void sleep(long dt) {
		try {Thread.sleep(dt);}catch(Exception e) {}
	}

	@Override
	public void paintBackground(Graphics2D g) {
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, drawing.getWidth(), drawing.getHeight());
	}

}
