package applets.E_12;

import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
		this.setSize(390, 80);
		this.setContentPane(getJContentPane());
	}

	/**
	 * z.B. nen Label oder ein Selector 
	 */
	public abstract class VisualThing {
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

	public class VTLabel extends VisualThing {

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

	public class VTSelector extends VisualThing {

		public VTSelector(
				String name, String[] items, int stepX, int stepY,
				ItemListener itemListener) {
			this.name = name;
			this.items = items;
			this.stepX = stepX;
			this.stepY = stepY;
			this.itemListener = itemListener;
		}
		
		private int stepX, stepY;
		private String name;
		private String[] items;
		private JComboBox selector = null;
		private ItemListener itemListener;
		
		public Component getComponent() {
			if(selector == null) {
				selector = new JComboBox();
				selector.setName(name);
				selector.addItemListener(itemListener);
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
			return 65;
		}

		public int getHeight() {
			return 18;
		}
		
	}

	public class VTButton extends VisualThing {

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
			
			String[] choices = new String[] {"0", "½", "1", "2", "x", "sin x", "cos x", "sin 2x", "cos 2x"};
			ItemListener updater = new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					resetSelectorColors();
					resetResultLabel();
				}};
			addVisualThings(jContentPane, new VisualThing[] {
					// Input-Feld
					new VTLabel("sin²( x ) =", 10, 10),
					new VTSelector("s1", choices, 10, 0, updater),
					new VTLabel("∙ (", 10, 0),
					new VTSelector("s2", choices, 10, 0, updater),
					new VTLabel("-", 10, 0),
					new VTSelector("s3", choices, 10, 0, updater),
					new VTLabel(")", 10, 0),
					
					// Bedienung
					new VTButton("überprüfen", 10, 35, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							boolean correct = true;
							for(int i = 1; i <= 3; i++) {
								Component c = getComponentByName("s"+i);
								String selected = (String) ((JComboBox)c).getSelectedItem();
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
							for(int i = 1; i <= 3; i++) {
								JComboBox combo = (JComboBox) getComponentByName("s"+i);
								String selected = (String) combo.getSelectedItem();
								boolean correct = isCorrect(i, selected);
								combo.setForeground(correct ? Color.MAGENTA : Color.RED);
							}
						}}),
			});
			resetResultLabel();
			
		}
		return jContentPane;
	}

	public boolean isCorrect(int selId, String selected) {
		switch(selId) {
		case 1:
			return selected == "½";
		case 2:
			return selected == "1";
		case 3:
			return selected == "cos 2x";
		}
		return false;
	}
	
	public void resetResultLabel() {
		if(getComponentByName("result") != null)
			((JLabel)getComponentByName("result")).setText("");
	}
	
	public void resetSelectorColors() {
		for(int i = 0; i < getJContentPane().getComponents().length; i++) {
			if(getJContentPane().getComponents()[i] instanceof JComboBox) {
				getJContentPane().getComponents()[i].setForeground(Color.BLACK);
			}
		}
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
