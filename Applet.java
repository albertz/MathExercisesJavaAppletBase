package applets.E_13;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JApplet;
import java.awt.Dimension;
import java.awt.CardLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JButton;

public class Applet extends JApplet {

	private JPanel jContentPane = null;
	private JLabel jQuestionLabel = null;
	private JTextField jResultText = null;
	private JButton jQueckButton = null;

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
		this.setSize(499, 36);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jQuestionLabel = new JLabel();
			jQuestionLabel.setText("( A / \u221a(A² + B²) )² + ( B / \u221a(A² + B²) )² = ");
			jQuestionLabel.setName("jLabel");
			jContentPane = new JPanel();
			jContentPane.setLayout(new FlowLayout());
			jContentPane.add(jQuestionLabel, null);
			jContentPane.add(getJResultText(), null);
			jContentPane.add(getJQueckButton(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jResultText	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJResultText() {
		if (jResultText == null) {
			jResultText = new JTextField();
			jResultText.setPreferredSize(new Dimension(100, 19));
			jResultText.setName("jTextField");
		}
		return jResultText;
	}

	/**
	 * This method initializes jQueckButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJQueckButton() {
		if (jQueckButton == null) {
			jQueckButton = new JButton();
			jQueckButton.setText("überprüfe");
			jQueckButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						if(Double.parseDouble(jResultText.getText().trim()) == 1)
							JOptionPane.showMessageDialog(Applet.this, "Das Ergebnis ist korrekt!");
						else
							JOptionPane.showMessageDialog(Applet.this, "Das Ergebnis ist leider falsch.");
					} catch(NumberFormatException nfe) {
						JOptionPane.showMessageDialog(Applet.this, "Das Ergebnis ist leider falsch.");
					}
				}
			});
		}
		return jQueckButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
