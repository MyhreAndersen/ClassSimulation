/*
 * (CC) This work is licensed under a Creative Commons
 * Attribution 4.0 International License.
 *
 * You find a copy of the License on the following
 * page: https://creativecommons.org/licenses/by/4.0/
 */
package simulation;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class SimulationLog extends JPanel {
	private static final long serialVersionUID = 1L;
    private JTextArea textArea;
    private static JTextArea currentTextArea;
    
	private static JFrame frame;
	private static JTabbedPane tabbedLog;
	private static Simulation sim;
	
	public void write(final String s) { textArea.append(s); }

	public SimulationLog(Process process) {
    	super(new BorderLayout());
    	
    	if(tabbedLog==null) {
    		sim=process.simulation;
    		tabbedLog=new JTabbedPane();
    		tabbedLog.setBackground(Color.white);
    		tabbedLog.addChangeListener(new ChangeListener() {
    			public void stateChanged(ChangeEvent e) {
    				JScrollPane scrollPane=(JScrollPane)tabbedLog.getSelectedComponent();
    				if(scrollPane!=null) {
    					JViewport viewport=scrollPane.getViewport();
    					Component view=viewport.getView();
    					if(view instanceof JTextArea) {
    						currentTextArea=(JTextArea) view;
    					}
    				} else currentTextArea=null;
    			}
    		});
    		
    		frame=new JFrame("Simulation Log");
    		frame.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {System.exit(0);}  });
    		tabbedLog.setPreferredSize(new Dimension(1000,950));
    		frame.getContentPane().add("Center",tabbedLog);
    		
    		JMenuBar menuBar=new JMenuBar();
    		frame.setJMenuBar(menuBar);
    		menuBar.add(makeMenu());
    		menuBar.add(new JSeparator(SwingConstants.VERTICAL));

    		frame.pack();
    		frame.setVisible(true);
    		frame.setLocationRelativeTo(null);
    	}
    	
    	JScrollPane scrollPane;
    	textArea = new JTextArea();
    	Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    	textArea.setFont(font);
    	scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);//_AS_NEEDED);
        
		String name=process.name;
		tabbedLog.addTab(name,null,scrollPane,"Tip for '"+name+"'tab");
		tabbedLog.setSelectedComponent(scrollPane);

		currentTextArea=textArea;
    }

	private JMenu makeMenu() { 
		JMenu fileMenu=new JMenu("Menu");
		addItem(fileMenu,"Clear Console",clearCmnd);
		addItem(fileMenu,"Copy to Clipboard",copyCmnd);
		fileMenu.addSeparator();
		addItem(fileMenu,"SnapShot",snapshotCmnd);
		addItem(fileMenu,"Exit",exitCmnd);
		return(fileMenu);
	}


	private void addItem(JMenu menu,String ident,ActionListener action) {
		addItem(menu,null,ident,action); }

	private void addItem(JMenu menu,ButtonGroup group,String ident,ActionListener action) {
		JMenuItem itm=new JMenuItem(ident);
		if(action==null) itm.setEnabled(false);
		else itm.addActionListener(action);
		if(group!=null) group.add(itm);
		//    Log.ASSERT(menu!=null,"");
		menu.add(itm);
	}

	private ActionListener exitCmnd=new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{ System.exit(0); }
	};

	private ActionListener snapshotCmnd=new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{ sim.snapshot(""); }
	};

	private ActionListener clearCmnd=new ActionListener() {
		public void actionPerformed(ActionEvent e) {
//			clear();
			currentTextArea.setText(null);
		}
	};

	private ActionListener copyCmnd=new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String text=textArea.getSelectedText();
			if(text==null) text=textArea.getText();
			StringSelection stringSelection = new StringSelection(text);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}
	};

}