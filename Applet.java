package applets.Abbildungen_Surjektiv_WarumNicht;

//S.11 in Vorlage

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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
		this.setSize(863, 622);
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
		public abstract void setStepX(int v);

		/**
		 * wie getStepX, nur für Y; wenn >0, wird außerdem wieder ganz links
		 * begonnen
		 */
		public abstract int getStepY();
		public abstract void setStepY(int v);

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

		public void setText(String text) {
			this.text = text;
			if(label == null)
				getComponent();
			else
				label.setText(text);
		}
		
		public String getText() {
			return text;
		}
		
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

		public int getWidth() {
			getComponent();
			return label.getFontMetrics(label.getFont()).stringWidth(text);
		}
		
		public int getStepX() {
			return stepX;
		}
		
		public int getStepY() {
			return stepY;
		}

		public void setStepX(int v) {
			stepX = v;
		}

		public void setStepY(int v) {
			stepY = v;
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

		public void setStepX(int v) {
			stepX = v;
		}

		public void setStepY(int v) {
			stepY = v;
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

		public void setStepX(int v) {
			stepX = v;
		}

		public void setStepY(int v) {
			stepY = v;
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

		public void setStepX(int v) {
			stepX = v;
		}

		public void setStepY(int v) {
			stepY = v;
		}
		
		public int getWidth() {
			return 40;
		}

		public int getHeight() {
			return 23;
		}

	}

	public static class VTContainer extends VisualThing {

		public VTContainer(int stepX, int stepY,
				VisualThing[] things) {
			this(null, stepX, stepY, things);
		}

		public VTContainer(String name, int stepX, int stepY,
				VisualThing[] things) {
			this.name = name;
			this.stepX = stepX;
			this.stepY = stepY;
			this.things = things;
		}

		private int stepX, stepY;
		private String name;
		protected JPanel panel = null;
		protected VisualThing[] things;
		protected Point size;

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

		public void setStepX(int v) {
			stepX = v;
		}

		public void setStepY(int v) {
			stepY = v;
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

	public static class VTEmptySpace extends VisualThing {

		public VTEmptySpace(int stepX, int stepY, int width, int height) {
			this.stepX = stepX;
			this.stepY = stepY;
			this.width = width;
			this.height = height;
		}

		private int stepX, stepY;
		private int width, height;

		public Component getComponent() {
			return null;
		}

		public int getStepY() {
			return stepY;
		}

		public int getStepX() {
			return stepX;
		}

		public void setStepX(int v) {
			stepX = v;
		}

		public void setStepY(int v) {
			stepY = v;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

	}

	public static class VTLine extends VTLabel {

		public VTLine(int stepX, int stepY, int width) {
			super("", stepX, stepY, "Courier");
			while(getWidth() < width) {
				this.setText(getText() + "—");
			}
		}
		
	}
	
	public static class VTFrac extends VTContainer {

		public VTFrac(int stepX, int stepY, String top, String down) {
			this(stepX, stepY, top, down, true);
		}
		
		public VTFrac(int stepX, int stepY, String top, String down, boolean withLine) {
			this(stepX, stepY,
					new VTLabel(top, 0, 0, "Courier"),
					new VTLabel(down, 0, 0, "Courier"),
					withLine);
		}
		
		public VTFrac(int stepX, int stepY, VisualThing top, VisualThing down, boolean withLine) {
			super(null, stepX, stepY, null);
			
			int width = Math.max(top.getWidth(), down.getWidth());
			if(withLine) width += 20;
			
			if(withLine)
				this.things = new VisualThing[] {
					new VTEmptySpace(0, 0, (width - top.getWidth()) / 2, 5),
					top,
					new VTLine(0, -6, width),
					new VTEmptySpace(0, -6, (width - down.getWidth()) / 2, 5),
					down,
				};
			else
				this.things = new VisualThing[] {
					new VTEmptySpace(0, 0, (width - top.getWidth()) / 2, 5),
					top,
					new VTEmptySpace(0, -6, (width - down.getWidth()) / 2, 5),
					down,
				};
		}

		public VTFrac(int stepX, int stepY, VisualThing top, VisualThing down) {
			this(stepX, stepY, top, down, true);
		}

	}
	
	public static VisualThing newVTLimes(int stepX, int stepY, String var, String c) {
		return new VTFrac(stepX, stepY,
				new VTLabel("lim", 0, 0, "Courier"),
				new VTLabel(var + " → " + c, 0, 0, "Courier"),
				false);
	}
	
	public static class VTLineCombiner extends VTContainer {

		public VTLineCombiner(int stepX, int stepY, VisualThing[] things) {
			super(null, stepX, stepY, things);
			
			size = new Point(0, 5);
			for(int i = 0; i < things.length; i++) {
				size.y = Math.max(size.y, things[i].getHeight());
				size.x += things[i].getWidth() + things[i].getStepX();
			}
			
			for(int i = 0; i < things.length; i++) {
				things[i].setStepY((size.y - things[i].getHeight()) / 2);
			}
		}
		
		public Component getComponent() {
			if (panel == null) {
				panel = new JPanel();
				panel.setLayout(null);
				addThings();
			}
			return panel;
		}

		private void addThings() {
			int curX = 0, curY = 0;

			for (int i = 0; i < things.length; i++) {
				Component c = things[i].getComponent();
				curY = things[i].getStepY();
				curX += things[i].getStepX();
				if(c != null) {
					c.setBounds(new Rectangle(curX, curY, things[i].getWidth(), things[i].getHeight()));
					panel.add(c);
				}
				curX += things[i].getWidth();
			}
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

		public void setStepX(int v) {
			stepX = v;
		}

		public void setStepY(int v) {
			stepY = v;
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
			if(c != null) {
				c.setBounds(new Rectangle(curX, curY, things[i].getWidth(),
						things[i].getHeight()));
				panel.add(c);
			}
			max.x = Math.max(max.x, curX + things[i].getWidth());
			max.y = Math.max(max.y, curY + things[i].getHeight());

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
	
	private void updateDefaultVisualThings() {
		removeAllVisualThings(jContentPane);

		/* Copy&Paste Bereich für häufig genutzte Zeichen:
		 * → ∞ ∈ ℝ π ℤ ℕ
		 * ≤ ⇒ ∉ ∅ ⊆ ∩ ∪
		 * ∙ × ÷ ± —
		 */
		String[] choices1 = new String[] {
				"f(x)/g(x)", "f(x+h)/g(x+h)", "g(x)g(x+h)", "f(x+h)g(x+h)",
				"f(x)g(x+h)", "f(x+h)g(x)", "f(x)", 
				"g(x+h)", "f(x+h)", "g²(x)", "f'(x)g(x)", "f(x)g'(x)",
				"g(x+h)-g(x)", "-g(x+h)+g(x)", "1/h" };
		Runnable updater = new Runnable() {
			public void run() {
				resetSelectorColors();
				resetResultLabels();
			}};
		addVisualThings(jContentPane, new VisualThing[] {
/*			new VTButton("neue Aufgabe", 10, 5, new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					aufgabeNr++;
					aufgabeNr %= 4;
					new Timer().schedule(new TimerTask() {
						public void run() {
							updateDefaultVisualThings();
						}
					}, 100);
				}
			}), */


			// Input-Feld
			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("(", 0, 0, "Courier"),
						new VTFrac(0, 0, "f", "g"),
						new VTLabel(")'(x) =", 0, 0, "Courier"),
						newVTLimes(10, 0, "h", "0"),
						new VTFrac(10, 0,
								new VTContainer(0, 0, new VisualThing[] {
										new VTSelector("s1", choices1, 0, 0, updater),
										new VTLabel("-", 10, 0, "Courier"),
										new VTSelector("s2", choices1, 10, 0, updater),
								}),
								new VTLabel("h", 0, 0, "Courier")),
			}),

			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("           =", 0, 10, "Courier"),
						newVTLimes(10, 0, "h", "0"),
						new VTSelector("s3", choices1, 10, 0, updater),
						new VTLabel("(", 10, 0, "Courier"),
						new VTFrac(10, 0, "f(x+h)", "g(x+h)"),
						new VTLabel("-", 10, 0, "Courier"),
						new VTFrac(10, 0, "f(x)", "g(x)"),
						new VTLabel(")", 10, 0, "Courier"),
			}),
			
			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("           =", 0, 10, "Courier"),
						newVTLimes(10, 0, "h", "0"),
						new VTFrac(10, 0, "1", "h"),
						new VTLabel("(", 10, 0, "Courier"),
						new VTFrac(10, 0,
								new VTContainer(0, 0, new VisualThing[] {
										new VTSelector("s4", choices1, 0, 0, updater),
										new VTLabel("-", 10, 0, "Courier"),
										new VTSelector("s5", choices1, 10, 0, updater),
								}),
								new VTLabel("g(x)g(x+h)", 0, 0, "Courier")
						),
						new VTLabel(")", 10, 0, "Courier"),
			}),
			
			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("           =", 0, 10, "Courier"),
						newVTLimes(10, 0, "h", "0"),
						new VTFrac(10, 0,
								new VTLabel("1", 0, 0, "Courier"),
								new VTSelector("s6", choices1, 0, 0, updater)
						),
						new VTFrac(10, 0, "f(x+h)g(x) - f(x)g(x+h)", "h"),
			}),

			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("           =", 0, 10, "Courier"),
						newVTLimes(10, 0, "h", "0"),
						new VTFrac(10, 0, "1", "g(x)g(x+h)"),
						new VTFrac(10, 0,
								new VTContainer(0, 0, new VisualThing[] {
										new VTLabel("f(x+h)g(x) - f(x+h)g(x+h) +", 0, 0, "Courier"),
										new VTSelector("s7", choices1, 10, 0, updater),
										new VTLabel("- f(x)g(x+h)", 10, 0, "Courier"),
								}),
								new VTLabel("h", 0, 0, "Courier")
						),
			}),

			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("           =", 0, 10, "Courier"),
						newVTLimes(10, 0, "h", "0"),
						new VTFrac(10, 0, "1", "g(x)g(x+h)"),
						new VTLabel("(", 10, 0, "Courier"),
						new VTFrac(10, 0,
								new VTContainer(0, 0, new VisualThing[] {
										new VTLabel("(g(x) - g(x+h))", 0, 0, "Courier"),
										new VTSelector("s8", choices1, 10, 0, updater),
								}),
								new VTLabel("h", 0, 0, "Courier")
						),
						new VTLabel("+", 10, 0, "Courier"),
						new VTFrac(10, 0,
								new VTContainer(0, 0, new VisualThing[] {
										new VTSelector("s9", choices1, 0, 0, updater),
										new VTLabel("(f(x+h) - f(x))", 10, 0, "Courier"),
								}),
								new VTLabel("h", 0, 0, "Courier")
						),
						new VTLabel(")", 10, 0, "Courier"),
			}),
			
			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("           =", 0, 10, "Courier"),
						newVTLimes(10, 0, "h", "0"),
						new VTFrac(10, 0, "1", "g(x)g(x+h)"),
						new VTLabel("( -", 10, 0, "Courier"),
						new VTFrac(10, 0,
								new VTContainer(0, 0, new VisualThing[] {
										new VTSelector("s10", choices1, 0, 0, updater),
										new VTLabel("f(x+h)", 10, 0, "Courier"),
								}),
								new VTLabel("h", 0, 0, "Courier")
						),
						new VTLabel("+", 10, 0, "Courier"),
						new VTFrac(10, 0,
								new VTContainer(0, 0, new VisualThing[] {
										new VTSelector("s11", choices1, 0, 0, updater),
										new VTLabel("(f(x+h) - f(x))", 10, 0, "Courier"),
								}),
								new VTLabel("h", 0, 0, "Courier")
						),
						new VTLabel(")", 10, 0, "Courier"),
			}),

			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("           =", 0, 10, "Courier"),
						newVTLimes(10, 0, "h", "0"),
						new VTFrac(10, 0, "1", "g(x)g(x+h)"),
						new VTFrac(10, 0, "f(x+h) - f(x)", "h"),
						new VTSelector("s12", choices1, 10, 0, updater),
						new VTLabel("-", 10, 0, "Courier"),
						new VTSelector("s13", choices1, 10, 0, updater),
						new VTFrac(10, 0, "g(x+h) - g(x)", "h"),
			}),

			new VTLineCombiner(10, 10,
					new VisualThing[] {
						new VTLabel("           =", 0, 10, "Courier"),
						new VTFrac(10, 0,
								new VTLabel("1", 0, 0, "Courier"),
								new VTSelector("s14", choices1, 0, 0, updater)
						),
						new VTLabel("(", 10, 0, "Courier"),
						new VTSelector("s15", choices1, 10, 0, updater),
						new VTLabel("-", 10, 0, "Courier"),
						new VTSelector("s16", choices1, 10, 0, updater),
						new VTLabel(")", 10, 0, "Courier"),
			}),
			

			// Bedienung
			new VTButton("überprüfen", 10, 40, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean correct = true;
					for(int i = 1; ; i++) {
						JComponent comp = (JComponent) getComponentByName("s" + i);
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
						JComponent comp = (JComponent) getComponentByName("s" + i);
						if(comp == null) break;
						String selected = comp instanceof JComboBox
							? (String) ((JComboBox)comp).getSelectedItem() : ((JTextField)comp).getText();
						boolean correct = isCorrect(i, selected);
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
	
	public String getSelected(int selId) {
		Component comp = getComponentByName("s" + selId);
		if(comp == null) return null;
		if(comp instanceof JComboBox)
			return (String) ((JComboBox)comp).getSelectedItem();
		if(comp instanceof JTextField)
			return ((JTextField)comp).getText();
		return null;
	}	
	
	public boolean isCorrect(int selId, String selected) {
		switch(selId) {
		case 1: return selected == "f(x+h)/g(x+h)";
		case 2: return selected == "f(x)/g(x)";
		case 3: return selected == "1/h";
		case 4: return selected == "f(x+h)g(x)";
		case 5: return selected == "f(x)g(x+h)";
		case 6: return selected == "g(x)g(x+h)";
		case 7: return selected == "f(x+h)g(x+h)";
		case 8: return selected == "f(x+h)";
		case 9: return selected == "g(x+h)";
		case 10: return selected == "g(x+h)-g(x)";
		case 11: return selected == "g(x+h)";
		case 12: return selected == "g(x+h)";
		case 13: return selected == "f(x)";
		case 14: return selected == "g²(x)";
		case 15: return selected == "f'(x)g(x)";
		case 16: return selected == "f(x)g'(x)";
		default: return false;
		}
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
