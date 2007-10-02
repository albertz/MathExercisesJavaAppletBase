package applets.Abbildungen_Bijektiv_X2;

// S.16 in Vorlage

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
		this.setSize(523, 448);
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

		public VTLineCombiner(String name, int stepX, int stepY, VisualThing[] things) {
			super(name, stepX, stepY, things);
			
			size = new Point(0, 5);
			for(int i = 0; i < things.length; i++) {
				size.y = Math.max(size.y, things[i].getHeight());
				size.x += things[i].getWidth() + things[i].getStepX();
			}
			
			for(int i = 0; i < things.length; i++) {
				things[i].setStepY((size.y - things[i].getHeight()) / 2);
			}
		}
		
		public VTLineCombiner(int stepX, int stepY, VisualThing[] things) {
			this(null, stepX, stepY, things);
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

		Runnable updater = new Runnable() {
			public void run() {
				resetSelectorColors();
				resetResultLabels();
			}
		};
		final int W = 500, H = 180;
		class Painter implements VTImage.PainterAndListener {
			
			class Oval {
				public Oval(Point p, int w, int h, Color c, String label) {
					this.p = p;
					this.w = w;
					this.h = h;
					this.c = c;
					this.label = label;
				}
				
				Point p;
				int w, h;
				Color c;
				String label;
				
				public void paint(Graphics g) {
					g.setColor(c);
					g.fillOval(p.x, p.y, w, h);
					g.drawString(label, p.x, p.y);
				}
				
				public Point getRandomPoint(Collection keepDistance, float d) {
					Point res = null;
					Random rnd = new Random();
					
					boolean distance = false;
					while(!distance) {
						double r = Math.sqrt(rnd.nextDouble()); // sqrt damit groessere r etwas wahrscheinlicher werden
						double a = rnd.nextDouble() * 2 * Math.PI;
						double x = Math.cos(a) * w * 0.5 * r; 
						double y = Math.sin(a) * h * 0.5 * r;
						x *= 0.8; y *= 0.8; // damit es auch echt drin ist
						res = new Point((int)x + p.x + w / 2, (int)y + p.y + h / 2);
						
						distance = true;
						for(Iterator i = keepDistance.iterator(); i.hasNext(); ) {
							Point p = (Point) i.next();
							if(p.distance(res) < d) {
								distance = false;
								break;
							}
						}
					}
					
					return res;
				}
			}
			
			class Connection {
				public Point src;
				public Point dst;
				
				public void paint(Graphics g) {
					g.drawLine(src.x, src.y, dst.x, dst.y);
					
					Point p1 = turn(new Point(src.x - dst.x, src.y - dst.y), 0.25 * Math.PI); 
					Point p2 = turn(new Point(src.x - dst.x, src.y - dst.y), -0.25 * Math.PI); 
					double r = Math.sqrt(p1.x * p1.x + p1.y * p1.y);
					double R = 10;
					p1.x *= R / r; p1.y *= R / r; p2.x *= R / r; p2.y *= R / r;
					g.drawLine(dst.x, dst.y, dst.x + p1.x, dst.y + p1.y);
					g.drawLine(dst.x, dst.y, dst.x + p2.x, dst.y + p2.y);

				}
			}
			
			Oval oval1 = new Oval(new Point(20, 20), 200, 150, new Color(123, 123, 123), "A");
			Oval oval2 = new Oval(new Point(300, 20), 200, 150, new Color(200, 100, 123), "B");
			private Collection dotsA = new LinkedList(), dotsB = new LinkedList();
			private int dotsCountA = 5, dotsCountB = 6;
			private Collection connections = new LinkedList();
			private Point selectedDotA = null; 
			private Point overDotA = null, overDotB = null;
			
			public void paint(Graphics g) {
				oval1.paint(g);
				oval2.paint(g);
				g.setColor(new Color(0, 200, 0));
				drawDots(g, dotsA);
				g.setColor(new Color(0, 200, 0));
				drawDots(g, dotsB);
				g.setColor(Color.BLACK);
				drawConnections(g, connections);
			}

			public Painter() {
				reset();
			}
			
			private void fillWithDots(Collection col, Oval o, int n) {
				for(int i = 0; i < n; i++) {
					col.add(o.getRandomPoint(col, 30));
				}
			}
			
			private void drawDots(Graphics g, Collection col) {
				Color c = g.getColor();
				for(Iterator i = col.iterator(); i.hasNext(); ) {
					Point p = (Point) i.next();
					if(p == selectedDotA)
						g.setColor(Color.BLUE);
					else if(p == overDotA || p == overDotB)
						g.setColor(Color.CYAN);
					else
						g.setColor(c);
					g.fillOval(p.x - 4, p.y - 4, 8, 8);
				}
			}
			
			private void drawConnections(Graphics g, Collection cons) {
				if(selectedDotA != null && overDotB != null) {
					Color c = g.getColor();
					g.setColor(Color.GRAY);
					Connection tmpCon = new Connection();
					tmpCon.src = selectedDotA;
					tmpCon.dst = overDotB;
					tmpCon.paint(g);
					g.setColor(c);
				}

				for(Iterator i = cons.iterator(); i.hasNext(); ) {
					Connection con = (Connection) i.next();
					if(selectedDotA != null && overDotB != null
							&& con.src.distance(selectedDotA) == 0
							&& con.dst.distance(overDotB) != 0) {
						// ignore
					} else
						con.paint(g);
				}
			}
			
			public boolean isCorrect() {
				Collection dotsA_copy = new LinkedList(dotsA);
				Collection dotsB_copy = new LinkedList(dotsB);
				for(Iterator i = connections.iterator(); i.hasNext(); ) {
					Connection p = (Connection) i.next();
					dotsA_copy.remove(p.src);
					if(dotsB_copy.contains(p.dst))
						dotsB_copy.remove(p.dst);
					else
						return false;
				}
				return dotsA_copy.isEmpty();
			}
			
			public String getResultText() {
				Collection dotsA_copy = new LinkedList(dotsA);
				Collection dotsB_copy = new LinkedList(dotsB);
				for(Iterator i = connections.iterator(); i.hasNext(); ) {
					Connection p = (Connection) i.next();
					dotsA_copy.remove(p.src);
					if(dotsB_copy.contains(p.dst))
						dotsB_copy.remove(p.dst);
					else
						return "leider ist das nicht korrekt";
				}
				if(dotsA_copy.isEmpty())
					return "das ist richtig!";
				else
					return "alle Punkte in A müssen zugewiesen werden";
			}
			
			private Point turn(Point p, double a) {
				double x = Math.cos(a);
				double y = Math.sin(a);
				return new Point((int)(x * p.x + y * p.y), (int)(-y * p.x + x * p.y));
			}
			
			public void reset() {
				dotsA.clear();
				dotsB.clear();
				connections.clear();
				fillWithDots(dotsA, oval1, dotsCountA);
				fillWithDots(dotsB, oval2, dotsCountB);
				selectedDotA = null;
				overDotA = null;
				overDotB = null;
				repaint();
			}
						
			private Point getPointByPos(Collection col, Point pos) {
				for(Iterator i = col.iterator(); i.hasNext(); ) {
					Point p = (Point) i.next();
					if(p.distance(pos) < 10) return p;
				}
				return null;
			}
			
			private Connection getConnectionBySrc(Point src, Collection cons) {
				for(Iterator i = cons.iterator(); i.hasNext(); ) {
					Connection con = (Connection) i.next();
					if(con.src.distance(src) == 0) return con;
				}
				return null;
			}
			
			public void mouseClicked(MouseEvent e) {
				mouseMoved(e); // just a HACK to get sure that vars are correct
				Point p = getPointByPos(dotsA, e.getPoint());
				if(p != null) {
					selectedDotA = p;
				} else if(selectedDotA != null) {
					p = getPointByPos(dotsB, e.getPoint());
					if(p != null) {
						Connection con = getConnectionBySrc(selectedDotA, connections);
						if(con != null) connections.remove(con);
						con = new Connection();
						con.src = selectedDotA;
						con.dst = p;
						connections.add(con);
						
						onChange();
					}
				}
				
				repaint();
			}

			private void onChange() {
				((JLabel)getComponentByName("res1")).setText("");
			}
			
			public void mouseMoved(MouseEvent e) {
				overDotA = getPointByPos(dotsA, e.getPoint());
				overDotB = getPointByPos(dotsB, e.getPoint());

				e.getComponent().repaint();
			}

			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseDragged(MouseEvent e) {}

		};
		final Painter painter = new Painter();
			
		/* Copy&Paste Bereich für häufig genutzte Zeichen:
		 * → ↦ ∞ ∈ ℝ π ℤ ℕ
		 * ≤ ⇒ ∉ ∅ ⊆ ∩ ∪
		 * ∙ × ÷ ± — ≠
		 */
			
		String[] choices1 = new String[] { "ja", "nein" };
		addVisualThings(jContentPane, new VisualThing[] {
			new VTLabel("Bitte definieren Sie eine injektive Abbildung.", 10, 10),
			new VTImage("bild", 10, 10, W, H, painter),

			// Bedienung
			new VTButton("überprüfen", 10, 20, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(painter.isCorrect()) {
						((JLabel)getComponentByName("res1")).setForeground(Color.MAGENTA);
						getComponentByName("con1").setVisible(true);
					} else {
						((JLabel)getComponentByName("res1")).setForeground(Color.RED);
					}
					((JLabel)getComponentByName("res1")).setText(painter.getResultText());
				}}),
			new VTLabel("res1", "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", 10, 0),
			new VTButton("reset", 10, 0, new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					painter.reset();
					((JLabel)getComponentByName("res1")).setText("");
				}
			}),

			new VTContainer("con1", 0, 10, new VisualThing[] {
					// Input-Feld
					new VTLineCombiner(10, 10,
							new VisualThing[] {
								new VTLabel("Ist die Abbildung", 0, 0),
								new VTLabel("f : ℝ → ℝ, x ↦ x²", 10, 0, "Courier"),
								new VTLabel("injektiv?", 10, 0),
								new VTSelector("s3", choices1, 15, 0, new Runnable() {
									public void run() {
										if(getComponentByName("res3") != null)
											((JLabel)getComponentByName("res3")).setText("");
									}
								})
					}),

					// Bedienung
					new VTButton("überprüfen", 10, 10,
							createCheckButtonListener(3, new Runnable() {
								public void run() { // correct
									((JLabel)getComponentByName("res3")).setText("Richtig!");
									getComponentByName("con2").setVisible(true);
								}
							}, new Runnable() {
								public void run() { // wrong
									((JLabel)getComponentByName("res3")).setText(
											"Falsch: Sei etwa x=2 und y=-2, dann gilt x≠y aber f(x)=4=f(y).");
								}
							})),
					new VTLabel("res3", "Falsch: Sei etwa x=2 und y=-2, dann gilt x≠y aber f(x)=4=f(y).", 10, 0),
			}),

			new VTContainer("con2", 0, 10, new VisualThing[] {
					// Input-Feld
					new VTLabel("Geben Sie zwei reelle Zahlen x und y an mit f(x)=f(y).", 10, 0),
					new VTLabel("x =", 10, 10),
					new VTText("s5", 10, 0, new Runnable() {
						public void run() {
							if(getComponentByName("res5") != null)
								((JLabel)getComponentByName("res5")).setText("");
						}
					}),
					new VTLabel("y =", 40, 0),
					new VTText("s6", 10, 0, new Runnable() {
						public void run() {
							if(getComponentByName("res5") != null)
								((JLabel)getComponentByName("res5")).setText("");
						}
					}),

					// Bedienung
					new VTButton("überprüfen", 10, 10, createCheckButtonListener(5)),
					new VTLabel("res5", "leider ist etwas falsch", 10, 0),
			}),
		});

		resetResultLabels();
		resetSelectorColors();
		getComponentByName("con1").setVisible(false);
		getComponentByName("con2").setVisible(false);
		
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

	public static double parseNum(String txt) {
		try {
			return Double.parseDouble(txt);
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
		case 3: return selected == "nein";
		case 5: return parseNum(selected) != -666;
		case 6: return Math.pow(parseNum(getSelected(5)), 2) == Math.pow(parseNum(selected), 2);
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
