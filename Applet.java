package applets.E_31;

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
		this.setSize(412, 457);
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
				String name, String text, int stepX, int stepY,
				ActionListener actionListener) {
			this.name = name;
			this.text = text;
			this.stepX = stepX;
			this.stepY = stepY;
			this.actionListener = actionListener;
		}

		public VTButton(
				String text, int stepX, int stepY,
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
			if(button == null) {
				button = new JButton();
				button.setText(text);
				if(name != null) button.setName(name);
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
	
	public static class VTContainer extends VisualThing {

		public VTContainer(String name, int stepX, int stepY, VisualThing[] things) {
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
			if(panel == null) {
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

		public static interface PainterAndListener extends Painter, MouseListener, MouseMotionListener {}
		
		public VTImage(
				String name, int stepX, int stepY, int width, int height,
				PainterAndListener painter) {
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
			if(panel == null) {
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
	 * fügt alle Dinge zum panel hinzu;
	 * siehe VisualThing für weitere Details
	 */
	public static Point addVisualThings(JPanel panel, VisualThing[] things) {
		int curX = 0, curY = 0;
		List xs_old = null;
		List xs = new LinkedList();
		Point max = new Point(0, 0);
		
		for(int i = 0; i < things.length; i++) {
			if(things[i].getStepY() > 0) {
				curY = max.y + things[i].getStepY();
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
		for(int i = 0; i < panel.getComponents().length; i++) {
			walker.meet(panel.getComponents()[i]);
			if(panel.getComponents()[i] instanceof JPanel)
				if(!ForEachComponent((JPanel) panel.getComponents()[i], walker))
					return false;
		}
		return true;
	}
	
	public boolean ForEachComponent(ComponentWalker walker) {
		return ForEachComponent(getJContentPane(), walker);
	}
	
	/**
	 * durchsucht alle Komponenten und gibt die erste zurück, deren Namen passt; ansonsten null
	 */
	public Component getComponentByName(final String name) {
		class CWalker implements ComponentWalker {
			public Component comp;
			
			public boolean meet(Component comp) {
				String cname = comp.getName();
				if(cname != null && cname.compareTo(name) == 0) {
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
	
	private ActionListener createCheckButtonListener(final int startIndex, final Runnable correctAction, final Runnable wrongAction) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean correct = true;
				for(int i = startIndex; ; i++) {
					JComponent comp = (JComponent) getComponentByName("s" + i);
					if(comp == null) break;
					String selected = comp instanceof JComboBox
						? (String) ((JComboBox)comp).getSelectedItem() : ((JTextField)comp).getText();
					correct = isCorrect(i, selected);
					if(!correct) break;
				}
				setResultLabel(startIndex, correct);
				if(correct) {
					if(correctAction != null) correctAction.run();
				} else {
					if(wrongAction != null) wrongAction.run();
				}
			}};		
	}
	
	private void setResultLabel(int index, boolean correct) {
		if(correct) {
			((JLabel)getComponentByName("res" + index)).setForeground(Color.MAGENTA);
			((JLabel)getComponentByName("res" + index)).setText("alles ist richtig!");
		} else {
			((JLabel)getComponentByName("res" + index)).setForeground(Color.RED);
			((JLabel)getComponentByName("res" + index)).setText("leider ist etwas falsch");
		}
	}
	
	private ActionListener createCheckButtonListener(int startIndex) {
		return createCheckButtonListener(startIndex, null, null);
	}
	
	private ActionListener createHelpButtonListener(final int startIndex) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i = startIndex; ; i++) {
					JComponent comp = (JComponent) getComponentByName("s" + i);
					if(comp == null) break;
					String selected = comp instanceof JComboBox
						? (String) ((JComboBox)comp).getSelectedItem() : ((JTextField)comp).getText();
					boolean correct = isCorrect(i, selected);
					comp.setBackground(correct ? Color.MAGENTA : Color.RED);
				}
			}};
	}
	
	private Runnable createVisibler(final String name) {
		return new Runnable() {
			public void run() {
				JComponent comp = (JComponent) getComponentByName(name);
				if(comp == null) return;
				comp.setVisible(true);
			}
		};
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
			
			/* Copy&Paste Bereich für häufig genutzte Zeichen:
			 * → ∞ ∈ ℝ π ℤ
			 * ≤ ⇒ ∉ ∅ ⊆ ∩ ∪
			 * ∙ × ÷ ±
			 */
			Runnable updater = new Runnable() {
				public void run() {
					resetSelectorColors();
					resetResultLabels();
				}};
			String[] choices2 = new String[] { "ein", "kein" };
			final int W = 400, H = 400;
			class Painter implements VTImage.PainterAndListener {
				
				private double alpha = 0;
				private int n = 2 + (new Random()).nextInt(4);
				private int state = 0;
				private int m = 0;
				
				public void paint(Graphics g) {
					// Kreis
					g.setColor(Color.LIGHT_GRAY);
					g.fillOval(W / 4, W / 4, W / 2, W / 2);
					g.setColor(Color.GRAY);
					g.drawOval(W / 4, W / 4, W / 2, W / 2);

					// Koord.
					g.setColor(Color.BLACK);
					g.drawLine(W / 2, 30, W / 2, W - 30);
					g.drawLine(30, W / 2, W - 30, W / 2);
					
					// Auswahl
					g.setColor(Color.BLUE);
					g.fillOval(
							(int)(W / 2 + Math.cos(alpha) * W / 4) - 10,
							(int)(W / 2 + Math.sin(alpha) * W / 4) - 10, 20, 20);

					
					// Text
					g.setColor(Color.BLUE);
					g.setFont(new Font("Sans", 0, 18));
					g.drawString("Wähle e^(2πi * " + m + "/" + n + ") aus.", 30, 20);

					// Beschriftungen
					g.setColor(Color.BLUE);
					g.setFont(new Font("Sans", 0, 12));
					g.drawString((int)(-alpha * 180 / Math.PI) + "°", W * 3/4, W * 3/4);
				}

				public void reset() {
					alpha = 0;
					state = 0;
					m = 0;
					n = 2 + (new Random()).nextInt(4);
					repaint();
				}
				
				public boolean gotoNextStage() {
					setResultLabel(1, isSelectionCorrect());
					
					if(!isSelectionCorrect()) {
						state = 0;
						return false;
					}
					
					m += 1 + (new Random()).nextInt(2 * n - 1);
					m %= 2 * n;
					state = 0;
					getComponentByName("next").setVisible(false);
					repaint();
					return true;
				}
				
				public boolean isSelectionCorrect() {
					long sel_state_d = - Math.round(2 * n * alpha / (2 * Math.PI));
					if(sel_state_d < 0) sel_state_d += 2 * n;
					return 2 * (m % n) == sel_state_d; 
				}
				
				public void mouseClicked(MouseEvent e) {
					mouseMoved(e); // just a HACK to get sure that vars are correct
					state++; state %= 2;
					if(state == 1)
						getComponentByName("next").setVisible(true);
					repaint();
				}

				public void mouseMoved(MouseEvent e) {
					if(state == 1) return;
					
					alpha = Math.atan2(e.getY() - W / 2, e.getX() - W / 2);
					
					// fix alpha a bit
					long m_doubled = Math.round(2 * n * alpha / (2 * Math.PI));
					alpha = ((double)m_doubled / (2 * n)) * 2 * Math.PI;
					
					e.getComponent().repaint();
				}

				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseDragged(MouseEvent e) {}

			};
			final Painter painter = new Painter();
			
			addVisualThings(jContentPane, new VisualThing[] {
					// Input-Feld 1
					new VTImage("bla", 10, 10, 400, 400, painter),
					
					new VTButton("next", "weiter", 10, 10, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							painter.gotoNextStage();
						}
					}),
					new VTLabel("res1", "leider ist etwas falsch", 10, 0),
					new VTButton("breset", "reset", 50, 0, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							resetResultLabels();
							painter.reset();
							getComponentByName("next").setVisible(false);
						}
					}),
			});
			resetResultLabels();
			resetSelectorColors();
			getComponentByName("next").setVisible(false);
			
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
		case 5: return selected == "ein";
		case 6: return selected == "kein";
		}
		return false;
	}
	
	public void resetResultLabels() {
		ForEachComponent(new ComponentWalker() {
			public boolean meet(Component comp) {
				if(comp.getName() != null
				&& comp.getName().startsWith("res")) {
					((JLabel)comp).setText("");
				}
				return true;
			}
		});
	}
	
	public void resetSelectorColors() {
		ForEachComponent(new ComponentWalker() {
			public boolean meet(Component comp) {
				if(comp instanceof JComboBox
				|| comp instanceof JTextField) {
					comp.setBackground(Color.WHITE);
				}
				return true;
			}
		});
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
