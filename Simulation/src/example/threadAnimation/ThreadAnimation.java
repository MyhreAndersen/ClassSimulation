package example.threadAnimation;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ThreadAnimation extends JFrame {

	    public ThreadAnimation() {
	        add(new Board());
//	        setResizable(false);
	        pack();
	        setTitle("ThreadAnimation");    
	        setLocationRelativeTo(null);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
	    }

	    public static void main(String[] args) {
            JFrame ex = new ThreadAnimation();
            ex.setVisible(true);
	    }
	}
