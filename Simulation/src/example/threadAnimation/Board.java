package example.threadAnimation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Board extends JPanel implements Runnable {
    private final int WIDTH = 350;
    private final int HEIGHT = 350;
    private final int DELAY = 25;
    private int x, y;

    public Board() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        init();
        new Thread(this).start();        
    }
    
    private void init() { x = -40; y= -40; }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    	g.setColor(Color.yellow);
        g.drawRect(x, y, 30, 60);
        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
    	x++; y++;
//        if (y > HEIGHT) init();
        if (y > this.getHeight()) init();
    }

	public void run() {
        long beforeTime = System.currentTimeMillis();
        while (true) {
            update();
            repaint();
            long timeDiff = System.currentTimeMillis() - beforeTime;
            long sleep = DELAY - timeDiff;
            if(sleep < 0) sleep = 2;
            try { Thread.sleep(sleep); } catch (InterruptedException e) {}
            beforeTime = System.currentTimeMillis();
        }
	}
}