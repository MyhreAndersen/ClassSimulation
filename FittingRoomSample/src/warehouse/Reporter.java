package warehouse;

import java.awt.Font;
import java.awt.Graphics2D;
import animation.Animable;

public class Reporter extends Animable {
	final WarehouseSimulation sim;

	public Reporter(WarehouseSimulation sim) {
		this.sim=sim;
	}

	public void paint(Graphics2D g) {
//    	g.setFont(new Font(Font.SERIF,Font.BOLD|Font.ITALIC,24));
    	g.setFont(new Font(Font.MONOSPACED,Font.BOLD,12));
//    	g.setFont(new Font(Font.SANS_SERIF,Font.BOLD|Font.ITALIC,24));
        
        int line=270;
        g.drawString("Simulated Time: "+sim.time(),50,(line=line+30));
        g.drawString("Docks: "+sim.nDocks,50,(line=line+30));
        g.drawString("nVans: "+sim.Totnr,50,(line=line+30));
        g.drawString("Waiting Time: "+sim.accWait,50,(line=line+30));
	}

}
