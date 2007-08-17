package applets.Bruch_Bruchterme_Gleichung;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
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
		this.setSize(617, 293);
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
		 * Reihe interpretiert, also z.B. -1 bezeichnet die X-Position des 1.
		 * Items in der vorherigen Reihe
		 */
		public abstract int getStepX();

		/**
		 * wie getStepX, nur für Y; wenn >0, wird außerdem wieder ganz links
		 * begonnen
		 */
		public abstract int getStepY();

		/**
		 * und die eigentliche Komponente
		 */
		public abstract Component getComponent();
	}

	public static class VTLabel extends VisualThing {

		public VTLabel(String text, int stepX, int stepY, String fontName) {
			this.text = text;
			this.stepX = stepX;
			this.stepY = stepY;
			this.fontName = fontName;
		}

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
		private String fontName = "";
		private JLabel label = null;

		public Component getComponent() {
			if (label == null) {
				label = new JLabel();
				if (name != null)
					label.setName(name);
				label.setText(text);
				label.setFont(new Font(fontName, 0, 12));
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

		public VTSelector(String name, String[] items, int stepX, int stepY,
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
			if (selector == null) {
				selector = new JComboBox();
				selector.setName(name);
				if (changeListener != null)
					selector.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							changeListener.run();
						}
					});
				for (int i = 0; i < items.length; i++)
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
			for (int i = 0; i < items.length; i++)
				max = Math.max(max, selector.getFontMetrics(selector.getFont())
						.stringWidth(items[i]));

			return max + 40;
		}

		public int getHeight() {
			return 18;
		}

	}

	public static class VTButton extends VisualThing {

		public VTButton(String name, String text, int stepX, int stepY,
				ActionListener actionListener) {
			this.name = name;
			this.text = text;
			this.stepX = stepX;
			this.stepY = stepY;
			this.actionListener = actionListener;
		}

		public VTButton(String text, int stepX, int stepY,
				ActionListener actionListener) {
			this.text = text;
			this.stepX = stepX;
			this.stepY = stepY;
			this.actionListener = actionListener;
		}

		private int stepX, stepY;

		private String name = null;

		private String text;

		private JButton button = null;

		private ActionListener actionListener;

		public Component getComponent() {
			if (button == null) {
				button = new JButton();
				button.setText(text);
				if (name != null)
					button.setName(name);
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
			if (text == null) {
				text = new JTextField();
				text.setName(name);
				if (changeListener != null)
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

	public static class VTContainer extends VisualThing {

		public VTContainer(String name, int stepX, int stepY,
				VisualThing[] things) {
			this.name = name;
			this.stepX = stepX;
			this.stepY = stepY;
			this.things = things;
		}

		private int stepX, stepY;

		private String name;

		private JPanel panel = null;

		private VisualThing[] things;

		private Point size;

		public Component getComponent() {
			if (panel == null) {
				panel = new JPanel();
				panel.setLayout(null);
				panel.setName(name);
				size = addVisualThings(panel, things);
			}
			return panel;
		}

		public int getStepY() {
			return stepY;
		}

		public int getStepX() {
			return stepX;
		}

		public int getWidth() {
			getComponent();
			return size.x;
		}

		public int getHeight() {
			getComponent();
			return size.y;
		}

	}

	public static class VTImage extends VisualThing {

		public static interface Painter {
			public void paint(Graphics g);
		}

		public static interface PainterAndListener extends Painter,
				MouseListener, MouseMotionListener {
		}

		public VTImage(String name, int stepX, int stepY, int width,
				int height, PainterAndListener painter) {
			this.name = name;
			this.stepX = stepX;
			this.stepY = stepY;
			this.width = width;
			this.height = height;
			this.painter = painter;
		}

		private int stepX, stepY;

		private int width, height;

		private String name;

		private Component panel = null;

		private PainterAndListener painter;

		public Component getComponent() {
			if (panel == null) {
				panel = new Component() {
					public void paint(Graphics g) {
						painter.paint(g);
					}
				};
				panel.setName(name);
				panel.setSize(width, height);
				panel.addMouseListener(painter);
				panel.addMouseMotionListener(painter);
			}
			return panel;
		}

		public int getStepY() {
			return stepY;
		}

		public int getStepX() {
			return stepX;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

	}

	/**
	 * fügt alle Dinge zum panel hinzu; siehe VisualThing für weitere Details
	 */
	public static Point addVisualThings(JPanel panel, VisualThing[] things) {
		int curX = 0, curY = 0;
		List xs_old = null;
		List xs = new LinkedList();
		Point max = new Point(0, 0);

		for (int i = 0; i < things.length; i++) {
			if (things[i].getStepY() != 0) {
				curY = max.y + things[i].getStepY();
				xs_old = xs;
				xs = new LinkedList();
				curX = 0;
			}
			if (things[i].getStepX() < 0) {
				curX = ((Integer) (xs_old.get(-things[i].getStepX() - 1)))
						.intValue();
			} else
				curX += things[i].getStepX();
			xs.add(new Integer(curX));

			Component c = things[i].getComponent();
			c.setBounds(new Rectangle(curX, curY, things[i].getWidth(),
					things[i].getHeight()));
			max.x = Math.max(max.x, curX + things[i].getWidth());
			max.y = Math.max(max.y, curY + things[i].getHeight());
			panel.add(c);

			curX += things[i].getWidth();
		}

		return max;
	}

	public static interface ComponentWalker {
		// false is a signal to break
		public boolean meet(Component comp);
	}

	public static boolean ForEachComponent(JPanel panel, ComponentWalker walker) {
		for (int i = 0; i < panel.getComponents().length; i++) {
			walker.meet(panel.getComponents()[i]);
			if (panel.getComponents()[i] instanceof JPanel)
				if (!ForEachComponent((JPanel) panel.getComponents()[i], walker))
					return false;
		}
		return true;
	}

	public boolean ForEachComponent(ComponentWalker walker) {
		return ForEachComponent(getJContentPane(), walker);
	}

	/**
	 * durchsucht alle Komponenten und gibt die erste zurück, deren Namen passt;
	 * ansonsten null
	 */
	public Component getComponentByName(final String name) {
		class CWalker implements ComponentWalker {
			public Component comp;

			public boolean meet(Component comp) {
				String cname = comp.getName();
				if (cname != null && cname.compareTo(name) == 0) {
					this.comp = comp;
					return false;
				}
				return true;
			}
		}
		CWalker walker = new CWalker();
		ForEachComponent(walker);
		return walker.comp;
	}

	private ActionListener createCheckButtonListener(final int startIndex,
			final Runnable correctAction, final Runnable wrongAction) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean correct = true;
				for (int i = startIndex;; i++) {
					JComponent comp = (JComponent) getComponentByName("s" + i);
					if (comp == null)
						break;
					String selected = comp instanceof JComboBox ? (String) ((JComboBox) comp)
							.getSelectedItem()
							: ((JTextField) comp).getText();
					correct = isCorrect(i, selected);
					if (!correct)
						break;
				}
				setResultLabel(startIndex, correct);
				if (correct) {
					if (correctAction != null)
						correctAction.run();
				} else {
					if (wrongAction != null)
						wrongAction.run();
				}
			}
		};
	}

	private void setResultLabel(int index, boolean correct) {
		if (correct) {
			((JLabel) getComponentByName("res" + index))
					.setForeground(Color.MAGENTA);
			((JLabel) getComponentByName("res" + index))
					.setText("alles ist richtig!");
		} else {
			((JLabel) getComponentByName("res" + index))
					.setForeground(Color.RED);
			((JLabel) getComponentByName("res" + index))
					.setText("leider ist etwas falsch");
		}
	}

	private ActionListener createCheckButtonListener(int startIndex) {
		return createCheckButtonListener(startIndex, null, null);
	}

	private ActionListener createHelpButtonListener(final int startIndex) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = startIndex;; i++) {
					JComponent comp = (JComponent) getComponentByName("s" + i);
					if (comp == null)
						break;
					String selected = comp instanceof JComboBox ? (String) ((JComboBox) comp)
							.getSelectedItem()
							: ((JTextField) comp).getText();
					boolean correct = isCorrect(i, selected);
					comp.setBackground(correct ? Color.MAGENTA : Color.RED);
				}
			}
		};
	}

	private Runnable createVisibler(final String name) {
		return new Runnable() {
			public void run() {
				JComponent comp = (JComponent) getComponentByName(name);
				if (comp == null)
					return;
				comp.setVisible(true);
			}
		};
	}

	public static String getMultipliedString(String str, int n) {
		String res = "";
		for (int i = 0; i < n; i++)
			res += str;
		return res;
	}

	public void removeAllVisualThings(JPanel panel) {
		panel.removeAll();
	}

	private int aufgabeNr = 0;
	private String[] solutionsDef = new String[] {};
	private String solutionSet = "∅";
	
	private void updateDefaultVisualThings() {
		removeAllVisualThings(jContentPane);

		String tTerm1Over = "", tTerm1Under = "", tTerm2Over = "", tTerm2Under = "";
		switch(aufgabeNr) {
		case 0:
			tTerm1Over = "-3";
			tTerm1Under = "x";
			tTerm2Over = "2";
			tTerm2Under = "x²";
			solutionsDef = new String[] {"{0}"};
			solutionSet = "{-2/3}";
			break;
		case 1:
			tTerm1Over = "x";
			tTerm1Under = "sin(π∙x) ∙ x";
			tTerm2Over = "1";
			tTerm2Under = "1";
			solutionsDef = new String[] {"{0}", "∅", "∅", "∅", "2ℤ"};
			solutionSet = "2ℤ + 1";
			break;
		case 2:
			tTerm1Over = "4";
			tTerm1Under = "x - 3";
			tTerm2Over = "5";
			tTerm2Under = "x + 1";
			solutionsDef = new String[] {"∅", "{-1}", "∅", "{3}"};
			solutionSet = "{19}";
			break;
		case 3:
			tTerm1Over = "2";
			tTerm1Under = "x² - 1";
			tTerm2Over = "2";
			tTerm2Under = "x² + 4";
			solutionsDef = new String[] {"∅", "{1, -1}"};
			solutionSet = "∅";
			break;
		case 4:
			tTerm1Over = "0.5";
			tTerm1Under = "x ∙ exp(x)";
			tTerm2Over = "exp(x)";
			tTerm2Under = "2x";
			solutionsDef = new String[] {"{0}"};
			solutionSet = "∅";
			break;
		case 5:
			tTerm1Over = "5";
			tTerm1Under = "(x - 2) ∙ (x - 3)";
			tTerm2Over = "1";
			tTerm2Under = "(x - 2) ∙ x";
			solutionsDef = new String[] {"{0}", "∅", "{2}", "{3}"};
			solutionSet = "{-3/4}";
			break;
		}
		int term1size = Math.max(tTerm1Over.length(), tTerm1Under.length());
		int term2size = Math.max(tTerm2Over.length(), tTerm2Under.length());
		
		/* Copy&Paste Bereich für häufig genutzte Zeichen:
		 * → ∞ ∈ ℝ π ℤ ℕ
		 * ≤ ⇒ ∉ ∅ ⊆ ∩ ∪
		 * ∙ × ÷ ±
		 */
		Runnable updater = new Runnable() {
			public void run() {
				resetSelectorColors();
				resetResultLabels();
			}};
		addVisualThings(jContentPane, new VisualThing[] {
			new VTButton("neue Aufgabe", 10, 5, new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					aufgabeNr++;
					aufgabeNr %= 6;
					new Timer().schedule(new TimerTask() {
						public void run() {
							updateDefaultVisualThings();
						}
					}, 100);
				}
			}),
			
			// Input-Feld
			new VTLabel("Gib die Definitionsmenge folgendes Terms an:", 10, 10),
			new VTLabel(
					"  " + tTerm1Over
					+ getMultipliedString(" ", term1size - tTerm1Over.length() + 9)
					+ tTerm2Over,
					30, 5, "Courier"),
			new VTLabel(
					getMultipliedString("_", term1size + 4)
					+ "  =  "
					+ getMultipliedString("_", term2size + 4),
					-1, -13, "Courier"),
			new VTLabel(
					"  " + tTerm1Under
					+ getMultipliedString(" ", term1size - tTerm1Under.length() + 9)
					+ tTerm2Under,
					-1, -5, "Courier"),
			
			new VTLabel("Defintionsmenge:", 10, 10),
			new VTLabel("ℝ \\ (", 30, 5),
			new VTSelector("s1", new String[] {"∅", "{0}"}, 5, 0, updater),
			new VTLabel("∪", 5, 0),
			new VTSelector("s2", new String[] {"∅", "{1}", "{-1}", "{1, -1}"}, 5, 0, updater),
			new VTLabel("∪", 5, 0),
			new VTSelector("s3", new String[] {"∅", "{2}", "{-2}", "{2, -2}"}, 5, 0, updater),
			new VTLabel("∪", 5, 0),
			new VTSelector("s4", new String[] {"∅", "{3}", "{-3}", "{3, -3}"}, 5, 0, updater),
			new VTLabel("∪", 5, 0),
			new VTSelector("s5", new String[] {"∅", "ℕ", "2ℕ", "ℤ", "2ℤ", "2ℤ + 1"}, 5, 0, updater),
			new VTLabel("∪", 5, 0),
			new VTSelector("s6", new String[] {"∅", "ℝ"}, 5, 0, updater),
			new VTLabel(")", 5, 0),

			new VTLabel("Lösungsmenge:", 10, 10),
			new VTSelector("s7", new String[] {"∅", "ℝ", "ℕ", "2ℕ", "ℤ", "2ℤ", "2ℤ + 1", "{0}", "{1}", "{1/3}", "{-2/3}", "{4/3}", "{-3/4}", "{13}", "{19}", "{27}", "{31}"}, 30, 5, updater),
			
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

		resetResultLabels();
		resetSelectorColors();
		
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

	public static int parseNum(String txt) {
		try {
			return Integer.parseInt(txt);
		} catch (NumberFormatException e) {
			return -666;
		}
	}

	public boolean isCorrect(int selId, String selected) {
		if(selId == 7)
			return selected == solutionSet;
		
		if(selId > solutionsDef.length)
			return selected == "∅";
		else
			return selected == solutionsDef[selId - 1];
	}

	public void resetResultLabels() {
		ForEachComponent(new ComponentWalker() {
			public boolean meet(Component comp) {
				if (comp.getName() != null && comp.getName().startsWith("res")) {
					((JLabel) comp).setText("");
				}
				return true;
			}
		});
	}

	public void resetSelectorColors() {
		ForEachComponent(new ComponentWalker() {
			public boolean meet(Component comp) {
				if (comp instanceof JComboBox || comp instanceof JTextField) {
					comp.setBackground(Color.WHITE);
				}
				return true;
			}
		});
	}

} // @jve:decl-index=0:visual-constraint="10,10"
