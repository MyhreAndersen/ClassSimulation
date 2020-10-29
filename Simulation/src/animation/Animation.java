/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package animation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public abstract class Animation {
	private final JFrame frame;
	protected static Drawing drawing;
	final public static Vector<Animable> RENDERING_SET=new Vector<Animable>();
	
	public static void repaint()	{
		drawing.repaint();
	}

	public abstract void paintBackground(Graphics2D g);

	public class Drawing extends JComponent {
		static final long serialVersionUID=123;
		public void paint(Graphics graphics) {
			Graphics2D g=(Graphics2D)graphics;
//			g.setBackground(currentBackgroundColor);
			g.setBackground(Color.white);
			Rectangle bnd=this.getBounds();
			g.clearRect(bnd.x,bnd.y,bnd.width,bnd.height);
			// Render to graphics
			paintBackground(g);
			for(Animable x:RENDERING_SET) {
				g.setColor(x.color);
				x.paint(g);
			}
			graphics.dispose();
		}
	}
    
	// Constructor
	public Animation(final String title,final int width,final int height) {
    	frame = new JFrame(title);
    	drawing = new Drawing();
		drawing.setPreferredSize(new Dimension(width, height));
    	JScrollPane scrollPane = new JScrollPane(drawing);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    frame.add(scrollPane);
	    frame.pack();
	    frame.setVisible(true);  
//	    frame.setAlwaysOnTop(true);
//        frame.addWindowListener(new WindowAdapter () {
//			public void windowClosing(WindowEvent e) { System.exit(0);	}
//        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }

}
