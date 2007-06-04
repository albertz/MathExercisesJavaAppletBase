package applets.M3_01_04;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JApplet;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JButton;

public class Applet extends JApplet {

	private JPanel jContentPane = null;
	private JCheckBox checkA = null;
	private JCheckBox checkAB = null;
	private JCheckBox checkAC = null;
	private JCheckBox checkBC = null;
	private JCheckBox checkB = null;
	private JCheckBox checkC = null;
	private JCheckBox checkABC = null;
	private JTextPane labelSelected = null;
	private JLabel jLabel = null;
	private JLabel labelSet = null;
	private JButton buttonNextSet = null;
	private JButton buttonCheck = null;
	private JLabel labelResult = null;
	
	private class SetRepresentation {
		public String Text;
		public int Selection;
	}
	
	SetRepresentation _set(String t, int s) {
		SetRepresentation res = new SetRepresentation();
		res.Text = t; res.Selection = s;
		return res;
	}
		
	SetRepresentation sets[] = {
			_set("A \\ B", 17),
			_set("B \\ C", 10),
			_set("B \\ A", 34),
			_set("(A \\ B) \\ C", 1),
			_set("A \\ (B \\ C)", 17),
			_set("A \\ (B \u222A C)", 1), // \u222A = vereinigt
			_set("A \u222A (B \\ C)", 91),
			_set("(A \u2229 B) \\ C", 8), // \u2229 = schnitt
			_set("(A \\ B) \u222A (B \\ C)", 27),
			_set("(A \u222A B) \\ C", 11)
			}; 
	
	private int curSetIndex = 0;
	
	private void showSet() {
		labelSet.setText(sets[curSetIndex].Text);		
	}
	
	private void checkSelection() {
		if(getSelection() == sets[curSetIndex].Selection) {
			labelResult.setForeground(Color.BLUE);
			labelResult.setText("das ist korrekt!");
		} else {
			labelResult.setForeground(Color.RED);
			labelResult.setText("leider falsch");			
		}		
	}
	
	private void nextSet() {
		curSetIndex++; curSetIndex %= sets.length;
		showSet();
		labelResult.setText("");
	}
	
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
		this.setSize(324, 321);
		this.setContentPane(getJContentPane());
		showSet();
	}
	
	private void refreshSelectionLabel() {
		String txt = "";
		
		if(checkA.isSelected()) {
			txt += "A \\ (B \u222A C)\n";
		}
		if(checkB.isSelected()) {
			txt += "B \\ (A \u222A C)\n";
		}
		if(checkC.isSelected()) {
			txt += "C \\ (A \u222A B)\n";
		}
		if(checkAB.isSelected()) {
			txt += "(A \u2229 B) \\ C\n";
		}
		if(checkAC.isSelected()) {
			txt += "(A \u2229 C) \\ B\n";
		}
		if(checkBC.isSelected()) {
			txt += "(B \u2229 C) \\ A\n";
		}
		if(checkABC.isSelected()) {
			txt += "A \u2229 B \u2229 C\n";
		}
		
		labelSelected.setText(txt /* + " " + getSelection() */);
		labelResult.setText("");
	}

	// numerische Repräsentation der Auswahl
	private int getSelection() {
		int res = 0;
		
		if(checkA.isSelected()) res += 1;
		if(checkB.isSelected()) res += 2;
		if(checkC.isSelected()) res += 4;
		if(checkAB.isSelected()) res += 8;
		if(checkAC.isSelected()) res += 16;
		if(checkBC.isSelected()) res += 32;
		if(checkABC.isSelected()) res += 64;
		
		return res;
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			labelResult = new JLabel();
			labelResult.setBounds(new Rectangle(135, 282, 175, 23));
			labelResult.setText("");
			labelSet = new JLabel();
			labelSet.setBounds(new Rectangle(14, 238, 169, 22));
			labelSet.setBackground(Color.white);
			labelSet.setText("");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(15, 209, 256, 23));
			jLabel.setText("Wähle bitte die folgende Menge aus:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getCheckA(), null);
			jContentPane.add(getCheckAB(), null);
			jContentPane.add(getCheckAC(), null);
			jContentPane.add(getCheckBC(), null);
			jContentPane.add(getCheckB(), null);
			jContentPane.add(getCheckC(), null);
			jContentPane.add(getCheckABC(), null);
			jContentPane.add(getLabelSelected(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(labelSet, null);
			jContentPane.add(getButtonNextSet(), null);
			jContentPane.add(getButtonCheck(), null);
			jContentPane.add(labelResult, null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes checkA	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCheckA() {
		if (checkA == null) {
			checkA = new JCheckBox();
			checkA.setBounds(new Rectangle(22, 48, 21, 21));
			checkA.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					refreshSelectionLabel();
				}
			});
		}
		return checkA;
	}

	/**
	 * This method initializes checkAB	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCheckAB() {
		if (checkAB == null) {
			checkAB = new JCheckBox();
			checkAB.setBounds(new Rectangle(89, 42, 21, 21));
			checkAB.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					refreshSelectionLabel();
				}
			});
		}
		return checkAB;
	}

	/**
	 * This method initializes checkAC	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCheckAC() {
		if (checkAC == null) {
			checkAC = new JCheckBox();
			checkAC.setBounds(new Rectangle(60, 101, 21, 21));
			checkAC.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					refreshSelectionLabel();
				}
			});
		}
		return checkAC;
	}

	/**
	 * This method initializes checkBC	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCheckBC() {
		if (checkBC == null) {
			checkBC = new JCheckBox();
			checkBC.setBounds(new Rectangle(125, 96, 21, 21));
			checkBC.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					refreshSelectionLabel();
				}
			});
		}
		return checkBC;
	}

	/**
	 * This method initializes checkB	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCheckB() {
		if (checkB == null) {
			checkB = new JCheckBox();
			checkB.setBounds(new Rectangle(156, 46, 21, 21));
			checkB.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					refreshSelectionLabel();
				}
			});
		}
		return checkB;
	}

	/**
	 * This method initializes checkC	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCheckC() {
		if (checkC == null) {
			checkC = new JCheckBox();
			checkC.setBounds(new Rectangle(104, 150, 21, 21));
			checkC.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					refreshSelectionLabel();
				}
			});
		}
		return checkC;
	}

	/**
	 * This method initializes checkABC	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCheckABC() {
		if (checkABC == null) {
			checkABC = new JCheckBox();
			checkABC.setBounds(new Rectangle(90, 79, 21, 21));
			checkABC.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					refreshSelectionLabel();
				}
			});
		}
		return checkABC;
	}

	private Point addPoints(Point p1, Point p2) {
		return new Point(p1.x + p2.x, p1.y + p2.y);
	}
	
	private Point multPoint(Point p, double c) {
		return new Point((int)(p.x * c), (int)(p.y * c));
	}
	
	private Point midPoint(Point p1, Point p2) {
		return multPoint(addPoints(p1, p2), 0.5);
	}
	
	private Point pointByDim(Dimension d) {
		return new Point(d.width, d.height);
	}
	
	private Point getMidPos(JComponent c) {
		return addPoints(c.getLocation(), multPoint(pointByDim(c.getSize()), 0.5));
	}
		
	public void paint(Graphics g) {
		super.paint(g);
		
		Point k1 =
			addPoints(
				multPoint(midPoint(getMidPos(checkAB), getMidPos(checkAC)), 2),
				multPoint(getMidPos(checkABC), -1));
		double r1 = midPoint(getMidPos(checkABC), getMidPos(checkBC)).distance(k1);

		Point k2 =
			addPoints(
				multPoint(midPoint(getMidPos(checkAB), getMidPos(checkBC)), 2),
				multPoint(getMidPos(checkABC), -1));
		double r2 = midPoint(getMidPos(checkABC), getMidPos(checkAC)).distance(k2);

		Point k3 =
			addPoints(
				multPoint(midPoint(getMidPos(checkAC), getMidPos(checkBC)), 2),
				multPoint(getMidPos(checkABC), -1));
		double r3 = midPoint(getMidPos(checkABC), getMidPos(checkAB)).distance(k3);
		
		g.setColor(Color.BLACK);
		g.drawOval(k1.x - (int)(r1), k1.y - (int)(r1), (int)(r1*2), (int)(r1*2));
		g.drawOval(k2.x - (int)(r2), k2.y - (int)(r2), (int)(r2*2), (int)(r2*2));
		g.drawOval(k3.x - (int)(r3), k3.y - (int)(r3), (int)(r3*2), (int)(r3*2));
		
	}

	/**
	 * This method initializes labelSelected	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getLabelSelected() {
		if (labelSelected == null) {
			labelSelected = new JTextPane();
			labelSelected.setBounds(new Rectangle(198, 15, 113, 170));
			labelSelected.setEditable(false);
		}
		return labelSelected;
	}

	/**
	 * This method initializes buttonNextSet	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonNextSet() {
		if (buttonNextSet == null) {
			buttonNextSet = new JButton();
			buttonNextSet.setBounds(new Rectangle(192, 238, 92, 21));
			buttonNextSet.setText("nächste");
			buttonNextSet.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					nextSet();
				}
			});
		}
		return buttonNextSet;
	}

	/**
	 * This method initializes buttonCheck	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonCheck() {
		if (buttonCheck == null) {
			buttonCheck = new JButton();
			buttonCheck.setBounds(new Rectangle(15, 283, 114, 21));
			buttonCheck.setText("überprüfen");
			buttonCheck.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					checkSelection();
				}
			});
		}
		return buttonCheck;
	}

}  //  @jve:decl-index=0:visual-constraint="88,10"
