package applets.Bruch_Anwendung_WasWas;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JComboBox;

public class Applet extends JApplet {

	private JPanel jContentPane = null;

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
		this.setSize(717, 130);
		this.setContentPane(getJContentPane());
	}

	/**
	 * z.B. nen Label oder ein Selector 
	 */
	public abstract static class VisualThing {
		/**
		 * Breite vom Ding 
		 */
		public abstract int getWidth();

		/**
		 * Höhe vom Ding
		 */
		public abstract int getHeight();
		
		/**
		 * wie weit nach rechts vom letzten Ding aus, also wo soll ich beginnen;
		 * ist der Wert negativ, so wirt er absolut als Index von der vorherigen
		 * Reihe interpretiert, also z.B. -1 bezeichnet die X-Position des
		 * 1. Items in der vorherigen Reihe
		 */
		public abstract int getStepX();
		
		/**
		 * wie getStepX, nur für Y; wenn >0, wird außerdem wieder ganz links begonnen 
		 */
		public abstract int getStepY();

		/**
		 * und die eigentliche Komponente
		 */
		public abstract Component getComponent();
	}

	public static class VTLabel extends VisualThing {

		public VTLabel(String text, int stepX, int stepY) {
			this.text = text;
			this.stepX = stepX;
			this.stepY = stepY;
		}

		public VTLabel(String name, String text, int stepX, int stepY) {
			this.text = text;
			this.name = name;
			this.stepX = stepX;
			this.stepY = stepY;
		}
		
		private int stepX, stepY;
		private String name = null;
		private String text;
		private JLabel label = null;
		
		public Component getComponent() {
			if(label == null) {
				label = new JLabel();
				if(name != null) label.setName(name);
				label.setText(text);
			}
			return label;
		}

		public int getHeight() {
			return 21;
		}

		public int getStepY() {
			return stepY;
		}

		public int getStepX() {
			return stepX;
		}

		public int getWidth() {
			getComponent();
			return label.getFontMetrics(label.getFont()).stringWidth(text);
		}
		
	}

	public static class VTSelector extends VisualThing {

		public VTSelector(
				String name, String[] items, int stepX, int stepY,
				Runnable changeListener) {
			this.name = name;
			this.items = items;
			this.stepX = stepX;
			this.stepY = stepY;
			this.changeListener = changeListener;
		}
		
		private int stepX, stepY;
		private String name;
		private String[] items;
		private JComboBox selector = null;
		private Runnable changeListener = null;
		
		public Component getComponent() {
			if(selector == null) {
				selector = new JComboBox();
				selector.setName(name);
				if(changeListener != null)
					selector.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							changeListener.run();
						}
					});
				for(int i = 0; i < items.length; i++)
					selector.addItem(items[i]);
			}
			return selector;
		}

		public int getStepY() {
			return stepY;
		}

		public int getStepX() {
			return stepX;
		}

		public int getWidth() {
			getComponent();
			int max = 0;
			for(int i = 0; i < items.length; i++)
				max = Math.max(max, selector.getFontMetrics(selector.getFont()).stringWidth(items[i]));
			
			return max + 40;
		}

		public int getHeight() {
			return 18;
		}
		
	}

	public static class VTButton extends VisualThing {

		public VTButton(
				String text, int stepX, int stepY,
				ActionListener actionListener) {
			this.text = text;
			this.stepX = stepX;
			this.stepY = stepY;
			this.actionListener = actionListener;
		}
		
		private int stepX, stepY;
		private String text;
		private JButton button = null;
		private ActionListener actionListener;
		
		public Component getComponent() {
			if(button == null) {
				button = new JButton();
				button.setText(text);
				button.addActionListener(actionListener);
			}
			return button;
		}

		public int getStepY() {
			return stepY;
		}

		public int getStepX() {
			return stepX;
		}

		public int getWidth() {
			getComponent();
			return button.getFontMetrics(button.getFont()).stringWidth(text) + 40;
		}

		public int getHeight() {
			return 23;
		}
		
	}
	
	public static class VTText extends VisualThing {

		public VTText(String name, int stepX, int stepY, Runnable changeListener) {
			this.name = name;
			this.stepX = stepX;
			this.stepY = stepY;
			this.changeListener = changeListener;
		}
		
		private int stepX, stepY;
		private String name = null;
		private JTextField text = null;
		private Runnable changeListener = null;
		
		public Component getComponent() {
			if(text == null) {
				text = new JTextField();
				text.setName(name);
				if(changeListener != null)
					text.addKeyListener(new KeyListener() {
						public void keyPressed(KeyEvent e) {}
						public void keyReleased(KeyEvent e) {}
						public void keyTyped(KeyEvent e) {
							changeListener.run();
						}
					});
			}
			return text;
		}

		public int getStepY() {
			return stepY;
		}

		public int getStepX() {
			return stepX;
		}

		public int getWidth() {
			return 40;
		}

		public int getHeight() {
			return 23;
		}
		
	}
	
	/**
	 * fügt alle Dinge zum panel hinzu;
	 * siehe VisualThing für weitere Details
	 */
	public void addVisualThings(JPanel panel, VisualThing[] things) {
		int curX = 0, curY = 0;
		List xs_old = null;
		List xs = new LinkedList();
		
		for(int i = 0; i < things.length; i++) {
			if(things[i].getStepY() > 0) {
				curY += things[i].getStepY();
				xs_old = xs;
				xs = new LinkedList();
				curX = 0;
			}
			if(things[i].getStepX() < 0) {
				curX = ((Integer) (xs_old.get(-things[i].getStepX() - 1))).intValue();
			} else
				curX += things[i].getStepX();
			xs.add(new Integer(curX));
			
			Component c = things[i].getComponent();
			c.setBounds(new Rectangle(curX, curY, things[i].getWidth(), things[i].getHeight()));
			panel.add(c);

			curX += things[i].getWidth();
		}
	}
	
	public void removeAllVisualThings(JPanel panel) {
		panel.removeAll();
	}
	
	/**
	 * durchsucht alle Komponenten und gibt die erste zurück, deren Namen passt; ansonsten null
	 */
	public Component getComponentByName(String name) {
		for(int i = 0; i < getJContentPane().getComponents().length; i++) {
			String cname = getJContentPane().getComponents()[i].getName();
			if(cname != null && cname.compareTo(name) == 0)
				return getJContentPane().getComponents()[i];
		}
		return null;
	}
	
	private String[] answers = new String[] { "Grundwert", "Prozentsatz", "Prozentwert" };
	
	private void makeRandomText() {
		Random rnd = new Random();
		
		int grundwert = rnd.nextInt(201);
		String tGrundwert = "" + grundwert;
		String tEinheit = "";
		int r = rnd.nextInt(5);
		switch(r) {
		case 0: tEinheit = "€"; break;
		case 1: tEinheit = "$"; break;
		case 2: tEinheit = " Bananen"; break;
		case 3: tEinheit = " Grapefruits"; break;
		case 4: tEinheit = grundwert == 1 ? " Mensch" : " Menschen"; break;
		}
		tGrundwert += tEinheit;
		
		int prozentsatz = rnd.nextInt(201);
		String tProzentsatz = prozentsatz + "%";
		
		float prozentwert = (float)grundwert * prozentsatz / 100;
		String tProzentwert = "";
		if(Math.round(prozentwert * 10) != prozentwert * 10) {
			tProzentwert = "ungefähr ";
		}
		tProzentwert += (float)Math.round(prozentwert * 10) / 10;
		tProzentwert += tEinheit;
		
		r = rnd.nextInt(6);
		switch(r) {
		case 0: // G Ps Pw
			setText(2, "Nimmt man von " + tGrundwert);
			setText(3, tProzentsatz + ",");
			setText(4, "erhält man " + tProzentwert + ".");
			answers = new String[] { "Grundwert", "Prozentsatz", "Prozentwert" };
			break;
		case 1: // G Pw Ps
			setText(2, "Nimmt man von " + tGrundwert + ",");
			setText(3, "so dass man " + tProzentwert + " erhält,");
			setText(4, "dann sind das " + tProzentsatz + ".");
			answers = new String[] { "Grundwert", "Prozentwert", "Prozentsatz" };
			break;
		case 2: // Ps G Pw
			setText(2, tProzentsatz);
			setText(3, "von " + tGrundwert);
			setText(4, "sind " + tProzentwert);
			answers = new String[] { "Prozentsatz", "Grundwert", "Prozentwert" };
			break;
		case 3: // Ps Pw G
			setText(2, "Hat man bei " + tProzentsatz);
			setText(3, tProzentwert + ",");
			setText(4, "so hat man insg. " + tGrundwert + ".");
			answers = new String[] { "Prozentsatz", "Prozentwert", "Grundwert" };
			break;
		case 4: // Pw G Ps
			setText(2, tProzentwert);
			setText(3, "sind von " + tGrundwert);
			setText(4, tProzentsatz + ".");
			answers = new String[] { "Prozentwert", "Grundwert", "Prozentsatz" };
			break;
		case 5: // Pw Ps G
			setText(2, tProzentwert);
			setText(3, "sind " + tProzentsatz);
			setText(4, "von " + tGrundwert + ".");
			answers = new String[] { "Prozentwert", "Prozentsatz", "Grundwert" };
			break;
		}
		
		resetResultLabel();
		resetSelectorColors();
	}
	
	private void setText(int selId, String txt) {
		((JLabel) getComponentByName("t" + selId)).setText(txt);
	}
	
	private void updateDefaultVisualThings() {
		removeAllVisualThings(jContentPane);
		
		Runnable updater = new Runnable() {
			public void run() {
				resetSelectorColors();
				resetResultLabel();
			}};
			String[] choices = new String[] { "Grundwert", "Prozentsatz", "Prozentwert" };
			addVisualThings(jContentPane, new VisualThing[] {
				new VTButton("neue Aufgabe", 10, 5, new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						makeRandomText();
					}
				}),
				
				// Input-Feld
				new VTLabel("t2", "-----------------------------", 10, 30),
				new VTLabel("t3", "-----------------------------", 10, 0),
				new VTLabel("t4", "-----------------------------", 10, 0),
				new VTSelector("s1", choices, -1, 20, updater),
				new VTSelector("s2", choices, -2, 0, updater),
				new VTSelector("s3", choices, -3, 0, updater),
				
				// Bedienung
				new VTButton("überprüfen", 10, 40, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						boolean correct = true;
						for(int i = 1; ; i++) {
							JComponent comp = (JComponent) getComponentByName("s"+i);
							if(comp == null) break;
							String selected = comp instanceof JComboBox
								? (String) ((JComboBox)comp).getSelectedItem() : ((JTextField)comp).getText();
							correct = isCorrect(i, selected);
							if(!correct) break;
						}
						if(correct) {
							((JLabel)getComponentByName("result")).setForeground(Color.MAGENTA);
							((JLabel)getComponentByName("result")).setText("alles ist richtig!");
						} else {
							((JLabel)getComponentByName("result")).setForeground(Color.RED);
							((JLabel)getComponentByName("result")).setText("leider ist etwas falsch");
						}
					}}),
				new VTLabel("result", "leider ist etwas falsch", 10, 0),
				new VTButton("hilf mir", 10, 0, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						for(int i = 1; ; i++) {
							JComponent comp = (JComponent) getComponentByName("s"+i);
							if(comp == null) break;
							String selected = comp instanceof JComboBox
								? (String) ((JComboBox)comp).getSelectedItem() : ((JTextField)comp).getText();
							boolean correct = isCorrect(i, selected);
							Color col = correct ? Color.MAGENTA : Color.RED;
							comp.setBackground(correct ? Color.MAGENTA : Color.RED);
						}
					}}),
		});

		resetResultLabel();
		resetSelectorColors();
		makeRandomText();
		
		jContentPane.repaint();
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			updateDefaultVisualThings();
		}
		return jContentPane;
	}

	public static int GGT(int n1, int n2) {
		if(n1 < 0) return GGT(-n1, n2);
		if(n2 < 0) return GGT(n1, -n2);
		if(n1 == 0 && n2 == 0) return 1;
		if(n1 == 0) return n2;
		if(n2 == 0) return n1;
		if(n1 == n2) return n1;
		if(n1 > n2) return GGT(n1 % n2, n2);
		else return GGT(n1, n2 % n1);
	}
	
	public static int Signum(int n) {
		if(n < 0) return -1;
		if(n > 0) return 1;
		return 0;
	}
	
	public static int getBruchNenner(int z, int n) {
		return Math.abs(n) / GGT(z, n);
	}
	
	public static int getBruchZaehler(int z, int n) {
		return Signum(n * z) * Math.abs(z) / GGT(z, n);
	}
	
	public static int parseNum(String txt) {
		try {
			return Integer.parseInt(txt);
		} catch(NumberFormatException e) {
			return -666;
		}
	}
	
	public boolean isCorrect(int selId, String selected) {
		return selected == answers[selId - 1];
	}
	
	public void resetResultLabel() {
		if(getComponentByName("result") != null)
			((JLabel)getComponentByName("result")).setText("");
	}
	
	public void resetSelectorColors() {
		for(int i = 0; i < getJContentPane().getComponents().length; i++) {
			if(getJContentPane().getComponents()[i] instanceof JComboBox
			|| getJContentPane().getComponents()[i] instanceof JTextField) {
				getJContentPane().getComponents()[i].setBackground(Color.WHITE);
			}
		}
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
