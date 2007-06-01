package applets.M3_01_04;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JApplet;
import java.awt.Dimension;

public class Applet extends JApplet {

	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="31,41"

	/**
	 * This is the xxx default constructor
	 */
	public Applet() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		this.setSize(377, 311);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="88,10"
