package applets.Bruch_Anwendung_Dreisatz;

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
		this.setSize(370, 280);
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
			return 58;
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
		
		resetSelectorColors();
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
	
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			
			Runnable updater = new Runnable() {
				public void run() {
					resetSelectorColors();
					resetResultLabel();
				}};
			addVisualThings(jContentPane, new VisualThing[] {
					// Input-Feld
					new VTLabel("3/4 + 4/5 =", 10, 10),
					new VTText("s1", 10, 0, updater),
					new VTLabel("/", 5, 0),
					new VTText("s2", 5, 0, updater),
					
					new VTLabel("2/3 - 9/10 =", -1, 30),
					new VTText("s3", 10, 0, updater),
					new VTLabel("/", 5, 0),
					new VTText("s4", 5, 0, updater),
					
					new VTLabel("1/6 + 2/3 =", -1, 30),
					new VTText("s5", 10, 0, updater),
					new VTLabel("/", 5, 0),
					new VTText("s6", 5, 0, updater),
					
					new VTLabel("-5/6 + 6 =", -1, 30),
					new VTText("s7", 10, 0, updater),
					new VTLabel("/", 5, 0),
					new VTText("s8", 5, 0, updater),
					
					new VTLabel("3/10 + 5/2 - 4 =", -1, 30),
					new VTText("s9", 10, 0, updater),
					new VTLabel("/", 5, 0),
					new VTText("s10", 5, 0, updater),
					
					new VTLabel("(Bitte Brüche in gekürzter Form angeben.)", 5, 40),
					new VTLabel("(Der Nenner muss immer positiv sein.)", -1, 20),
					
					// Bedienung
					new VTButton("überprüfen", 10, 50, new ActionListener() {
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
			
		}
		return jContentPane;
	}

	public static int parseNum(String txt) {
		try {
			return Integer.parseInt(txt);
		} catch(NumberFormatException e) {
			return -666;
		}
	}
	
	public boolean isCorrect(int selId, String selected) {
		switch(selId) {
		case 1:
			return parseNum(selected) == 31;
		case 2:
			return parseNum(selected) == 20;
		case 3:
			return parseNum(selected) == -7;
		case 4:
			return parseNum(selected) == 30;
		case 5:
			return parseNum(selected) == 5;
		case 6:
			return parseNum(selected) == 6;
		case 7:
			return parseNum(selected) == 31;
		case 8:
			return parseNum(selected) == 6;
		case 9:
			return parseNum(selected) == -6;
		case 10:
			return parseNum(selected) == 5;
		}
		return false;
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
