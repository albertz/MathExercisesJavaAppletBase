package applets.Abbildungen_I47_GraphCos;

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
		this.setSize(622, 364);
		this.setContentPane(getJContentPane());
	}

	public interface Function2D {
		double get(double x);
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
			final Runnable correctAction, final Runnable wrongAction,
			final String correctStr, final String wrongStr) {
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
				setResultLabel(startIndex, correct, correctStr, wrongStr);
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

	private ActionListener createCheckButtonListener(int startIndex,
			Runnable correctAction, Runnable wrongAction) {
		return createCheckButtonListener(startIndex, correctAction, wrongAction, null, null);
	}
	
	private ActionListener createCheckButtonListener(int startIndex) {
		return createCheckButtonListener(startIndex, null, null, null, null);
	}

	private ActionListener createCheckButtonListener(int startIndex, String correctStr, String wrongStr) {
		return createCheckButtonListener(startIndex, null, null, correctStr, wrongStr);
	}

	private void setResultLabel(int index, boolean correct) {
		setResultLabel(index, correct, null, null);
	}
	
	private void setResultLabel(int index, boolean correct, String correctStr, String wrongStr) {
		if(correctStr == null)
			correctStr = "alles ist richtig!";
		if(wrongStr == null)
			wrongStr = "leider ist etwas falsch";
		if (correct) {
			((JLabel) getComponentByName("res" + index))
					.setForeground(Color.MAGENTA);
			((JLabel) getComponentByName("res" + index))
					.setText(correctStr);
		} else {
			((JLabel) getComponentByName("res" + index))
					.setForeground(Color.RED);
			((JLabel) getComponentByName("res" + index))
					.setText(wrongStr);
		}
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

		final Runnable updater = new Runnable() {
			public void run() {
				resetSelectorColors();
				resetResultLabels();
			}
		};
		final int W = 600, H = 300;
		class Painter implements VTImage.PainterAndListener {
			// Funktionsplotter / Graphzeichner
			
			public Function2D function = new Function2D() {
				public double get(double x) {
					return Math.cos(x);
				}
			};
			public double x_l = -4 * Math.PI, x_r = 4 * Math.PI;
			public double y_u = -1.2, y_o = 1.2;
			public int xspace_l = 30, xspace_r = 30;
			public int yspace_u = 20, yspace_o = 70;
			
			public double axeXStep = Math.PI / 2;
			public double axeXMult = 0.5;
			public int axeXTextStep = 2;
			public String axeXPostText = "π";
			public double axeYStep = 0.1;
			public double axeYMult = 0.1;
			public int axeYTextStep = 10;
			public String axeYPostText = "";
			
			public int state = 0;
			public String[] stateMsgs = null;
			public int stateMsgX = 25, stateMsgY = 25;
			
			public double selectedX1 = 0, selectedX2 = 0;
			public double selectedX = 0;
			public double selectedY = -100;

			protected int simulationX2Ypos = 0;
			protected int simulationX2Ydir = -1; // 1 = pos; -1 = neg
			protected Timer simulationX2Ytimer = null;
			
			public void setXYValuesInversFrom(Painter src) {
				x_l = src.y_u;
				x_r = src.y_o;
				y_u = src.x_l;
				y_o = src.x_r;
				axeXStep = src.axeYStep;
				axeXMult = src.axeYMult;
				axeXTextStep = src.axeYTextStep;
				axeXPostText = src.axeYPostText;
				axeYStep = src.axeXStep;
				axeYMult = src.axeXMult;
				axeYTextStep = src.axeXTextStep;
				axeYPostText = src.axeXPostText;
								
				// keep this, perhaps looks better
				xspace_l = src.xspace_l;
				xspace_r = src.xspace_r;
				yspace_o = src.yspace_o;
				yspace_u = src.yspace_u;
			}
			
			public void paint(Graphics g) {
				// Hintergrund
				g.setColor(new Color(250, 250, 250));
				g.fillRect(0, 0, W, H);
				
				drawAchsen(g);
				drawAchsentext(g);
				drawSelectionXPos(g);
				drawSelectionYPos(g);
				drawSelectionXRange(g);
				drawSimulationX2Y(g);
				drawStateMsg(g);
				
				// Funktion
				g.setColor(Color.BLUE);
				drawFunction(g);

			}
			
			protected void stopSimulationX2Y() {
				simulationX2Ypos = 0;
				try {
					if(simulationX2Ytimer != null) simulationX2Ytimer.cancel();
				} catch(IllegalArgumentException e) {}
				repaint();
			}
			
			protected void resetSimulationX2Y() {
				stopSimulationX2Y();
				
				simulationX2Ytimer = new Timer();
				simulationX2Ytimer.schedule(new TimerTask() {
					public void run() {
						simulationX2Ypos += 10;
						repaint();
					}
				}, 0, 50);
 			}
			
			protected void drawSimulationX2Y(Graphics g) {
				int s = (int)Math.signum(function.get(selectedX));
				int f_x = transformY(function.get(selectedX));
				int x = transformX(selectedX);
				int len = Math.abs(x - transformX(0)) + Math.abs(transformY(0) - f_x); 
				int pos = (int)(((double)len / 200) * simulationX2Ypos);
				if(pos != 0) pos %= len + 20;
				if(simulationX2Ydir < 0) pos = 20 + len - pos;
				int y = transformY(0) - s * pos;
				if(s * y < s * f_x) {
					x -= Math.signum(selectedX) * Math.abs(f_x - y);
					if(Math.signum(selectedX) * x < Math.signum(selectedX) * transformX(0)) x = transformX(0);
					y = f_x;
				}
				g.setColor(new Color(255, 123, 50, 200));
				g.fillOval(x - 3, y - 3, 6, 6);
			}
			
			protected String getStateMsg() {
				if(stateMsgs != null && state < stateMsgs.length) {
					return stateMsgs[state];
				}
				return "";
			}
			
			protected void drawStateMsg(Graphics g) {
				String msg = getStateMsg();
				msg = msg.replace("%x%", "" + ((double)Math.round(selectedX * 10) / 10));
				msg = msg.replace("%x1/pi%", "" + ((double)Math.round(selectedX1 * 10 / Math.PI) / 10));
				msg = msg.replace("%x2/pi%", "" + ((double)Math.round(selectedX2 * 10 / Math.PI) / 10));
				msg = msg.replace("%x1%", "" + ((double)Math.round(selectedX1 * 10) / 10));
				msg = msg.replace("%x2%", "" + ((double)Math.round(selectedX2 * 10) / 10));
				msg = msg.replace("%y%", "" + ((double)Math.round(selectedY * 10) / 10));
				String[] lines = msg.split("\n");
				for(int i = 0; i < lines.length; i++) {
					if(lines[i].length() > 0) {
						double w = g.getFontMetrics().getStringBounds(lines[i], g).getWidth();
						g.setColor(Color.white);
						g.fillRect(stateMsgX - 4, stateMsgY - 18 + i*25, (int)w + 8, 25);
						g.setColor(new Color(122, 123, 50));
						g.drawString(lines[i], stateMsgX, stateMsgY + i*25);
					}
				}
			}
			
			protected void drawSelectionXRange(Graphics g) {
				g.setColor(new Color(123, 255, 50, 80));
				g.fillRect(transformX(selectedX1), 0, transformX(selectedX2) - transformX(selectedX1), H);
				
				g.setColor(new Color(123, 255, 50, 200));
				g.drawLine(transformX(selectedX1), 0, transformX(selectedX1), H);
				g.drawLine(transformX(selectedX2), 0, transformX(selectedX2), H);
			}
			
			protected void drawSelectionXPos(Graphics g) {
				g.setColor(new Color(255, 255, 50, 200));
				g.drawLine(transformX(selectedX), 0, transformX(selectedX), H);
			}

			protected void drawSelectionYPos(Graphics g) {
				g.setColor(new Color(255, 255, 50, 200));
				g.drawLine(0, transformY(selectedY), W, transformY(selectedY));
			}

			protected void drawAchsen(Graphics g) {
				g.setColor(Color.GRAY);
				int x = transformX(0), y = transformY(0);
				if(x < xspace_l) x = xspace_l;
				if(x > W - xspace_r) x = W - xspace_r;
				if(y < yspace_o) y = yspace_o;
				if(y > H - yspace_u) y = H - yspace_u;
				g.drawLine(x, 0, x, H);
				g.drawLine(0, y, W, y);
			}

			protected void drawAchsentext(Graphics g) {
				int x = transformX(0), y = transformY(0);
				if(x < xspace_l) x = xspace_l;
				if(x > W - xspace_r) x = W - xspace_r;
				if(y < yspace_o) y = yspace_o;
				if(y > H - yspace_u) y = H - yspace_u;
				double px = 0;
				for(; px > x_l; px -= axeXStep) {}
				for(; px <= x_r; px += axeXStep) {
					g.drawLine(transformX(px), y, transformX(px), y + 5);
					long n = Math.round(px / axeXStep);
					if(n % axeXTextStep == 0) {
						g.drawString("" + Math.round(axeXMult * px / axeXStep) + axeXPostText, transformX(px) - 10, y + 20);
					}
				}

				double py = 0;
				for(; py > y_u; py -= axeYStep) {}
				for(; py <= y_o; py += axeYStep) {
					g.drawLine(x, transformY(py), x - 5, transformY(py));
					long n = Math.round(py / axeYStep);
					if(n % axeYTextStep == 0) {
						g.drawString("" + Math.round(axeYMult * py / axeYStep) + axeYPostText, x - 25, transformY(py) + 10);
					}
				}
			}
			
			protected void drawFunction(Graphics g) {
				double step = (x_r - x_l) / 100;
				int last_y = transformY(function.get(x_l));
				int y;
				for(double x = x_l + step; x <= x_r; x += step) {
					y = transformY(function.get(x));
					g.drawLine(transformX(x - step), last_y, transformX(x), y);
					last_y = y;
				}
			}
			
			public int transformX(double x) {
				return xspace_l + (int) ((x - x_l) * (W - xspace_r - xspace_l) / (x_r - x_l));
			}
			
			public int transformY(double y) {
				return H - yspace_u - (int) ((y - y_u) * (H - yspace_o - yspace_u) / (y_o - y_u));
			}
			
			public double retransformX(int x) {
				return x_l + (double)(x - xspace_l) * (x_r - x_l) / (W - xspace_r - xspace_l);
			}
			
			public double retransformY(int y) {
				return y_u + (double)(H - yspace_u - y) * (y_o - y_u) / (H - yspace_o - yspace_u);
			}
			
			public Painter() {
				reset();
			}
			
			
			public boolean isCorrect() {
				return exs[excNr].isCorrect(selectedX1 / Math.PI, selectedX2 / Math.PI);
			}
			
			public String getResultText() {
				if(isCorrect())
					return "das ist korrekt";
				else
					return "leider ist das nicht korrekt";
			}
			
			class ExcSet {
				public double x; // x (/pi)
				
				public ExcSet(double x) {
					this.x = x;
				}
				
				// x1,x2 results (/pi)
				public boolean isCorrect(double x1, double x2) {
					if(x2 != x1 + 1) return false;
					if(x % 1 == 0) return x == x1 || x == x2;
					return Math.floor(x) == x1;
				}
			}
			
			public int excNr = 0;
			public ExcSet[] exs = new ExcSet[] {
					new ExcSet(0.25), new ExcSet(-1), new ExcSet(3.5),new ExcSet(0),
					new ExcSet(2.25), new ExcSet(-0.5), new ExcSet(-3.25), 
			};
			
			public void reset() {
				ExcSet e = exs[excNr];
				String baseStateMsg =
					"Geben Sie ein maximales Intervall an, " +
					"in dem " + e.x + "π enthalten ist und " +
					"für das Cosinus injektiv ist.";
				stateMsgs = new String[] {
					baseStateMsg + "\n(*x1 = %x1/pi%π, x2 = %x2/pi%π)",
					baseStateMsg + "\n(x1 = %x1/pi%π, *x2 = %x2/pi%π)",
					baseStateMsg + "\n(x1 = %x1/pi%π, x2 = %x2/pi%π)",
				};
				selectedX = e.x * Math.PI;
			}
			
			public void next() {
				excNr++; excNr %= exs.length;
				updater.run();
				reset();
				repaint();
			}
			
			protected void doSelectionXRange(int state, int mouse_x) {
				double step = axeXStep / 2;
				double x = retransformX(mouse_x);
				x = step * Math.round(x / step);
				
				switch(state) {
				case 0: selectedX1 = x; break;
				case 1: selectedX2 = x; break;
				}
			}
			
			protected void doSelectionXPos(int mouse_x, boolean stepWise) {
				double step = axeXStep / 2;
				double x = retransformX(mouse_x);
				if(stepWise)
					x = step * Math.round(x / step);
				if(x < x_l) x = x_l;
				if(x > x_r) x = x_r;
				selectedX = x;
			}

			protected void doSelectionYPos(int mouse_y, boolean stepWise) {
				double step = axeYStep / 2;
				double y = retransformY(mouse_y);
				if(stepWise)
					y = step * Math.round(y / step);
				if(y < y_u) y = y_u;
				if(y > y_o) y = y_o;
				selectedY = y;
			}
			
			public void mouseClicked(MouseEvent e) {
				mouseMoved(e);
				state++; state %= 3;
			}
			public void mouseMoved(MouseEvent e) {
				if(state < 2) {
					doSelectionXRange(state, e.getX());
					updater.run();
					repaint();
				}
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
			new VTImage("bla", 10, 10, W, H, painter),
			
			// Bedienung
			new VTButton("überprüfen", 10, 20, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(painter.isCorrect()) {
						((JLabel)getComponentByName("res1")).setForeground(Color.MAGENTA);
					} else {
						((JLabel)getComponentByName("res1")).setForeground(Color.RED);
					}
					((JLabel)getComponentByName("res1")).setText(painter.getResultText());
				}}),
			new VTLabel("res1", "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", 10, 0),
			new VTButton("weiter", 10, 0, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					painter.next();
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
		case 1: return selected == "nein";
		case 3: return selected == "ja";
		case 5: return selected == "ja";
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
