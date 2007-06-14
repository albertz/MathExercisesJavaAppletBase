package applets.E_14;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;

public class Applet extends JApplet {

	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JLabel jLabel7 = null;
	private JButton jCheckButton = null;
	private JButton jHelpButton = null;
	private JLabel jResultLabel = null;

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
		this.setSize(487, 104);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jResultLabel = new JLabel();
			jResultLabel.setBounds(new Rectangle(205, 68, 140, 24));
			jResultLabel.setText("");
			jLabel7 = new JLabel();
			jLabel7.setBounds(new Rectangle(290, 43, 24, 21));
			jLabel7.setText(")");
			jLabel6 = new JLabel();
			jLabel6.setBounds(new Rectangle(218, 43, 13, 21));
			jLabel6.setText("+");
			jLabel5 = new JLabel();
			jLabel5.setBounds(new Rectangle(30, 43, 133, 21));
			jLabel5.setText("= √(A² + B²) ∙ cos(");
			jLabel4 = new JLabel();
			jLabel4.setBounds(new Rectangle(418, 14, 63, 21));
			jLabel4.setText(")sin(x))");
			jLabel3 = new JLabel();
			jLabel3.setBounds(new Rectangle(345, 14, 13, 21));
			jLabel3.setText("(");
			jLabel2 = new JLabel();
			jLabel2.setBounds(new Rectangle(229, 14, 52, 21));
			jLabel2.setText(")cos(x) -");
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(30, 14, 133, 21));
			jLabel1.setText("= √(A² + B²) ∙ (cos(");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(13, 14, 16, 21));
			jLabel.setText("A");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(jLabel2, null);
			jContentPane.add(jLabel3, null);
			jContentPane.add(jLabel4, null);
			jContentPane.add(jLabel5, null);
			jContentPane.add(jLabel6, null);
			jContentPane.add(jLabel7, null);
			jContentPane.add(getJCheckButton(), null);
			jContentPane.add(getJHelpButton(), null);
			jContentPane.add(jResultLabel, null);

			jContentPane.add(getNewSelector(162,14), null);
			jContentPane.add(getNewSelector(285,14), null);
			jContentPane.add(getNewSelector(355,14), null);
			jContentPane.add(getNewSelector(157,43), null);
			jContentPane.add(getNewSelector(230,43), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jCheckButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCheckButton() {
		if (jCheckButton == null) {
			jCheckButton = new JButton();
			jCheckButton.setBounds(new Rectangle(79, 68, 121, 23));
			jCheckButton.setText("überprüfen");
			jCheckButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean correct = true;
					for(int i = 0; i < getJContentPane().getComponents().length; i++) {
						if(getJContentPane().getComponents()[i] instanceof JComboBox) {
							String selected = (String) ((JComboBox)getJContentPane().getComponents()[i]).getSelectedItem();
							switch(Integer.parseInt(getJContentPane().getComponents()[i].getName())) {
							case 0:
								correct = selected == "ξ";
								break;
							case 1:
								correct = selected == "sin";
								break;
							case 2:
								correct = selected == "ξ";
								break;
							case 3:
								correct = selected == "ξ";
								break;
							case 4:
								correct = selected == "x";
								break;
							}
							if(!correct) break;
						}
					}
					if(correct) {
						jResultLabel.setForeground(Color.MAGENTA);
						jResultLabel.setText("alles ist richtig!");
					} else {
						jResultLabel.setForeground(Color.RED);
						jResultLabel.setText("leider ist etwas falsch");
					}
				}});
		}
		return jCheckButton;
	}

	/**
	 * This method initializes jHelpButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJHelpButton() {
		if (jHelpButton == null) {
			jHelpButton = new JButton();
			jHelpButton.setBounds(new Rectangle(358, 68, 121, 23));
			jHelpButton.setText("hilf mir");
			jHelpButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					for(int i = 0; i < getJContentPane().getComponents().length; i++) {
						if(getJContentPane().getComponents()[i] instanceof JComboBox) {
							String selected = (String) ((JComboBox)getJContentPane().getComponents()[i]).getSelectedItem();
							switch(Integer.parseInt(getJContentPane().getComponents()[i].getName())) {
							case 0:
								if(selected == "ξ")
									getJContentPane().getComponents()[i].setForeground(Color.MAGENTA);
								else
									getJContentPane().getComponents()[i].setForeground(Color.RED);
								break;
							case 1:
								if(selected == "sin")
									getJContentPane().getComponents()[i].setForeground(Color.MAGENTA);
								else
									getJContentPane().getComponents()[i].setForeground(Color.RED);
								break;
							case 2:
								if(selected == "ξ")
									getJContentPane().getComponents()[i].setForeground(Color.MAGENTA);
								else
									getJContentPane().getComponents()[i].setForeground(Color.RED);
								break;
							case 3:
								if(selected == "ξ")
									getJContentPane().getComponents()[i].setForeground(Color.MAGENTA);
								else
									getJContentPane().getComponents()[i].setForeground(Color.RED);
								break;
							case 4:
								if(selected == "x")
									getJContentPane().getComponents()[i].setForeground(Color.MAGENTA);
								else
									getJContentPane().getComponents()[i].setForeground(Color.RED);
								break;
							}
						}
					}
				}});
		}
		return jHelpButton;
	}

	public void resetResultLabel() {
		jResultLabel.setText("");
	}
	
	public void resetSelectorColors() {
		for(int i = 0; i < getJContentPane().getComponents().length; i++) {
			if(getJContentPane().getComponents()[i] instanceof JComboBox) {
				getJContentPane().getComponents()[i].setForeground(Color.BLACK);
			}
		}
	}
	
	/**
	 * Gibt neues Auswahlfeld zurück
	 */
	int selectorId = 0;
	private JComboBox getNewSelector(int x, int y) {
		JComboBox selector = new JComboBox();
		selector.setName(""+selectorId++);
		selector.setBounds(new Rectangle(x,y,58,18));
		selector.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				resetSelectorColors();
				resetResultLabel();
			}});
		selector.addItem("A");
		selector.addItem("B");
		selector.addItem("ξ");
		selector.addItem("0");
		selector.addItem("1");
		selector.addItem("x");
		selector.addItem("π");
		selector.addItem("exp");
		selector.addItem("sin");
		selector.addItem("cos");
		return selector;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
