package applets.Abbildungen_I37_BijektivBeweis;

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
		this.setSize(523, 900);
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
				label.setOpaque(false);
			}
			return label;
		}

		public int getHeight() {
			getComponent();
			return label.getFontMetrics(label.getFont()).getHeight();
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

		public int getHeight() {
			return 10;
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

	public static VisualThing newVTLimes(int stepX, int stepY, VisualThing sub) {
		return new VTFrac(stepX, stepY,
				new VTLabel("lim", 0, 0, "Courier"), sub, false);
	}
	
	public static VisualThing newVTLimes(int stepX, int stepY, VisualThing var, VisualThing c) {
		return new VTFrac(stepX, stepY,
				new VTLabel("lim", 0, 0, "Courier"),
				new VTContainer(0, 0, new VisualThing[] {
						var, new VTLabel("→", 0, 0, "Courier"), c
				}),
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

	public static class VTMeta extends VTContainer  {
				
		public VTMeta(String name, int stepX, int stepY, String content) {
			super(name, stepX, stepY, getThingsByContentStr(content));
		}

		public VTMeta(int stepX, int stepY, String content) {
			this(null, stepX, stepY, content);
		}
		
		private static class Number {
			public int number = 0;
		}
		
		public static VisualThing createSimpleContainer(List thing_list) {
			return new VTContainer(0, 0, getArrayByThingList(thing_list));
		}
		
		public static VisualThing[] getArrayByThingList(List thing_list) {
			VisualThing[] things = new VisualThing[thing_list.size()];
			for(int i = 0; i < things.length; i++)
				things[i] = (VisualThing) thing_list.get(i);
			return things;
		}
		
		public static VisualThing[] getThingsByContentStr(String content) {
			Number endpos = new Number();
			List things = getThingsByContentStr(content, 0, endpos);
			if(endpos.number <= content.length())
				System.err.println("getThingsByContentStr: not parsed until end");
			return getArrayByThingList(things);
		}
		
		protected static String getTextOutOfVisualThing(VisualThing thing) {
			if(thing instanceof VTLabel) {
				return ((VTLabel)thing).getText();
			} else if(thing instanceof VTContainer) {
				VTContainer cont = (VTContainer) thing;
				if(cont.things.length == 0)
					return "";
				else
					return getTextOutOfVisualThing(cont.things[0]);
			} else {
				System.err.println("getTextOutOfVisualThing: cannot handle thing");
				return "";
			}
		}
		
		/*
		 * expect extparam as "param1=value1,param2=value2,..."
		 */
		protected static String getExtParamVar(String extparam, String param, boolean matchIfNoParams) {
			int state = 0;
			String curparam = ""; 
			String curvar = "";
			int pos = 0;
			int parcount = 0;
			
			while(state >= 0) {
				int c = pos < extparam.length() ? extparam.charAt(pos) : -1;
				
				switch(state) {
				case 0: // paramname
					switch(c) {
					case -1: state = -1; parcount++; break;
					case ',': 
						if(param.compareToIgnoreCase(curparam) == 0) return "";
						curparam = "";
						parcount++;
						break;
					case '=': state = 5; break;
					default: curparam += (char)c;
					}
					break;
					
				case 5: // var
					switch(c) {
					case -1:
					case ',':
						if(param.compareToIgnoreCase(curparam) == 0) return curvar;
						curparam = ""; curvar = "";
						parcount++;
						state = 0; break;
					default: curvar += (char)c;
					}
					break;
					
				}
				
				pos++;
			}
			
			if(matchIfNoParams && parcount <= 1) return extparam;
			return null;
		}
		
		protected static String getExtParamVar(String extparam, String param) {
			return getExtParamVar(extparam, param, false);
		}
		
		protected static VisualThing handleTag(String tagname, VisualThing baseparam, String extparam, VisualThing lowerparam, VisualThing upperparam) {
			if(tagname.compareToIgnoreCase("frac") == 0) {
				return new VTFrac(0, 0, upperparam, lowerparam);
			}
			else if(tagname.compareToIgnoreCase("lim") == 0) {
				return newVTLimes(0, 0, lowerparam);
			}
			else if(tagname.compareToIgnoreCase("text") == 0) {
				Runnable action = null; // TODO: depending on extparam.onchange
				String name = getExtParamVar(extparam, "name");
				return new VTText(name, 0, 0, action);
			}
			else if(tagname.compareToIgnoreCase("button") == 0) {
				ActionListener action = null; // TODO: depending on extparam.action
				String name = getExtParamVar(extparam, "name");
				return new VTButton(name, getTextOutOfVisualThing(baseparam), 0, 0, action);
			}
			else if(tagname.compareToIgnoreCase("label") == 0) {
				String name = getExtParamVar(extparam, "name", true);
				return new VTLabel(name, getTextOutOfVisualThing(baseparam), 0, 0);
			}
			else if(tagname.compareToIgnoreCase("selector") == 0) {
				Runnable action = null; // TODO: depending on extparam.action
				String[] items = getTextOutOfVisualThing(baseparam).split(",");
				String name = getExtParamVar(extparam, "name");
				return new VTSelector(name, items, 0, 0, action);
			}
			else
				System.out.println("handleTag: don't know tag " + tagname);
			
			return null;
		}

		protected static VisualThing handleTag(String tag, VisualThing baseparam) {
			return handleTag(tag, baseparam, "", null, null);
		}

		protected static VisualThing handleTag(String tag) {
			return handleTag(tag, null);
		}
		
		protected static void addNewVT(List things, String curstr, VisualThing newVT) {
			if(curstr.length() > 0) things.add(new VTLabel(curstr, 5, 0));
			if(newVT != null) things.add(newVT);
		}
		
		protected static class Tag {
			/***
			 * @param tag			Tagname
			 * @param baseparam		all in {...}
			 * @param extparam		all in [...]
			 * @param lowerparam	all in _...
			 * @param upperparam	all in ^...
			 * @return	VisualThing
			 */
			public String name = "";
			public VisualThing baseparam = null;
			public String extparam = "";
			public VisualThing lowerparam = null;
			public VisualThing upperparam = null;
			
			public boolean isSet() {
				return name.length() != 0;
			}
			
			public void reset() {
				name = ""; baseparam = null; extparam = ""; lowerparam = null; upperparam = null;
			}
			
			public VisualThing handle() {
				return VTMeta.handleTag(name, baseparam, extparam, lowerparam, upperparam);
			}
		}
		
		public static List getThingsByContentStr(String content, int startpos, Number endpos) {
			int state = 0;
			int pos = startpos;
			List lastlines = new LinkedList(); // VTLineCombiners
			List things = new LinkedList(); // current things which are filled
			String curstr = "";
			Tag curtag = new Tag();
			Number newpos = new Number(); // if recursive calls will be done, this is for getting the new pos
			String curtagtmpstr = ""; // used by lowerparam and upperparam in simple mode
			
			while(state >= 0) {
				int c = (pos >= content.length()) ? -1 : content.charAt(pos);
				
				switch(state) {
				case 0: // default + clean up
					curstr = ""; curtag.reset(); state = 1;
				case 1: // default
					switch(c) {
					case '\\': curtag.reset(); state = 10; break;
					case -1: case '}': case ']': // these marks the end at all
						state = -1;
					case '\n': // new line
						addNewVT(things, curstr, null); curstr = "";
						lastlines.add(new VTLineCombiner(0, 5, getArrayByThingList(things)));
						things.clear();
						break;
					default: curstr += (char)c;
					}
					break;
					
				case 10: // we got a '\', tagmode
					if(!curtag.isSet()) switch(c) { // check first for special chars if curtag is not set yet
					case '\\': case '{':
					case '[': case '_':
					case '^': case -1:
						state = 1; pos--; // handle char as normal text 
					}
					if(state == 1) break;

					if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9')
						curtag.name += (char)c;
					else switch(c) {
					case '\\': addNewVT(things, curstr, curtag.handle()); curtag.reset(); break;
					case '{': state = 11; break;
					case '[': state = 12; break;
					case '_': state = 13; curtagtmpstr = ""; break;
					case '^': state = 14; curtagtmpstr = ""; break;
					default: // nothing special, so tag ended here
						addNewVT(things, curstr, curtag.handle());
						state = 0; pos--; // handle char as normal text 
					}
					break;
				case 11: // tagmode, baseparam starting
					curtag.baseparam = createSimpleContainer(getThingsByContentStr(content, pos, newpos));
					pos = newpos.number - 1; state = 10;
					break;
				case 12: // tagmode, extparam starting
					switch(c) {
					case -1: state = 10; pos--; break;
					case ']': state = 10; break;
					default: curtag.extparam += (char)c;
					}
					break;
				case 13: // tagmode, lowerparam simple (directly after '_')
					switch(c) {					
					case -1: pos--;
					case ' ': case 8: case '\n':
						curtag.lowerparam = new VTLabel(curtagtmpstr, 0, 0);
						state = 10;
						break;
					case '^': state = 14; break;
					case '{': state = 15; break;
					default: curtagtmpstr += (char)c;
					}
					break;
				case 14: // tagmode, upperparam simple (directly after '^')
					switch(c) {					
					case -1: pos--;
					case ' ': case 8: case '\n':
						curtag.upperparam = new VTLabel(curtagtmpstr, 0, 0);
						state = 10;
						break;
					case '_': state = 13; break;
					case '{': state = 16; break;
					default: curtagtmpstr += (char)c;
					}
					break;
				case 15: // tagmode, lowerparam normal (in {...})
					curtag.lowerparam = createSimpleContainer(getThingsByContentStr(content, pos, newpos));
					pos = newpos.number - 1; state = 10;
					break;
				case 16: // tagmode, upperparam normal (in {...})
					curtag.upperparam = createSimpleContainer(getThingsByContentStr(content, pos, newpos));
					pos = newpos.number - 1; state = 10;
					break;
					
				
				
				default:
					System.err.println("getThingsByContentStr: unknown state " + state);
					state = 0;
				}
				
				pos++;
			}
			endpos.number = pos;
			
			// we fill the last things in the automat automatically in lastlines at the end
			if(lastlines.size() == 0)
				return null;
			else if(lastlines.size() == 1)
				return Arrays.asList(((VTLineCombiner) lastlines.get(0)).things);
			else
				return lastlines;
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
			
		String[] choices1 = new String[] { "1", "0", "n", "2n", "-n", "-2n", "2n + 1", "n/2" };
		addVisualThings(jContentPane, VTMeta.getThingsByContentStr(
				"Hallo Welt\n" +
				"und noch mal Hallo.\n" +
				"\\lim_{n → ∞} \\frac^{1}_{n} = 0\n" +
				"\\button{click me} \\selector{a,b,c,d} \\text \\label[einlabel]{hallo}"
				));
/*		{
			new VTLabel("Man betrachte die Abbildung h : ℤ² → ℤ², (n,m) → (3n-4m,4n-5m).", 10, 10),
			
				
				new VTLabel("Es gibt viele Bijektionen von ℕ auf ℤ.", 10, 10),			
			new VTLabel("Eine könnte etwa so aussehen:", 10, -2),
			new VTLabel("1 ↦ 0", 20, -2),
			new VTLabel("2 ↦ 1", 20, -2),
			new VTLabel("3 ↦ -1", 20, -2),
			new VTLabel("4 ↦ 2", 20, -2),
			new VTLabel("5 ↦ -2", 20, -2),
			new VTLabel("...", 20, -2),
			
			new VTLabel("Man sieht, dass die Zuordnung für gerade natürliche Zahlen auf positive Zahlen", 10, 5),
			new VTLabel("geht, während sie für ungerade Zahlen negative Werte annimmt.", 10, -2),
			new VTLabel("Machen Sie sich bitte klar, dass jede positive natürliche Zahl k von der Form", 10, -2),
			new VTLabel("k = 2n ist, während jede ungerade positive Zahl k von der Form k = 2n + 1 ist (n ∈ ℕ).", 10, -2),
			
			new VTLabel("Die allgemeine Formel für die oben angedeutete Zuordnung lautet:", 10, 5),
			new VTLabel("f : ℕ → ℤ", 10, -2),
			new VTLabel("f(0) :=", 20, -2),
			new VTSelector("s1", choices1, 10, 0, updater),
			new VTLabel("f(2n) :=", 20, -2),
			new VTSelector("s2", choices1, 10, 0, updater),
			new VTLabel("f(2n + 1) :=", 20, -2),
			new VTSelector("s3", choices1, 10, 0, updater),
			
			new VTButton("überprüfen", 10, 20, createCheckButtonListener(1, new Runnable() {
				public void run() {
					getComponentByName("con1").setVisible(true);
				}
			}, null)),
			new VTLabel("res1", "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", 10, 0),
			new VTButton("Hilfe", 10, 0, createHelpButtonListener(1)),
			
			new VTContainer("con1", 0, 10, new VisualThing[] {
					new VTLabel("Behauptung:", 10, 0),
					new VTLabel("f ist bijektiv.", 10, -2),
					
					new VTLabel("Beweis:", 10, 5),
					
					new VTLabel("a) f ist injektiv:", 10, 2),
					new VTLabel("Um dies zu zeigen, verwenden wir die Version: f(k) = f(m) ⇒ k = m", 10, -2),

					new VTLabel("Fall 1: f(k) > 0:", 10, 2),
					new VTLabel("Nach Definition der Abbildung f gilt dann:", 10, -2),
					new VTLabel("k =", 10, -2),
					new VTSelector("s5", choices1, 10, 0, updater),
					new VTLabel(",   m =", 10, 0),
					new VTSelector("s6", choices1, 10, 0, updater),
					new VTLabel("Es gilt also k = m.", 10, -2),
					
					new VTLabel("Fall 2: f(k) < 0:", 10, 2),
					new VTLabel("Nach Definition der Abbildung f gilt dann:", 10, -2),
					new VTLabel("k =", 10, -2),
					new VTSelector("s7", choices1, 10, 0, updater),
					new VTLabel(",   m =", 10, 0),
					new VTSelector("s8", choices1, 10, 0, updater),
					new VTLabel("Es gilt also k = m.", 10, -2),
					
					new VTLabel("Fall 3: f(k) = 0: klar", 10, 2),
					
					new VTLabel("b) f ist surjektiv:", 10, 2),
					new VTLabel("Wir musse also zeigen, dass jede positive ganze Zahl, jede negative ganze Zahl", 10, -2),
					new VTLabel("und die Zahl 0 als Werte von f auftreten. Wegen f(0) = 0 brauchen wir nur", 10, -2),
					new VTLabel("positive und negative ganze Zahlen zu betrachten. Sei n ∈ ℕ, dann gilt:", 10, -2),
					new VTLabel("f(", 10, -2),
					new VTSelector("s9", choices1, 10, 0, updater),
					new VTLabel(") = n", 10, 0),
					new VTLabel("f(", 10, -2),
					new VTSelector("s10", choices1, 10, 0, updater),
					new VTLabel(") = -n", 10, 0),
					new VTLabel("Somit wird ganz ℤ erreicht, also ist f surjektiv.", 10, -2),
					
					new VTButton("überprüfen", 10, 20, createCheckButtonListener(5)),
					new VTLabel("res5", "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww", 10, 0),
					new VTButton("Hilfe", 10, 0, createHelpButtonListener(5)),
			}),
			
		});*/

		resetResultLabels();
		resetSelectorColors();
//		getComponentByName("con1").setVisible(false);
		
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
		case 1: return selected == "0";
		case 2: return selected == "n";
		case 3: return selected == "-n";
		case 5: return selected == "2n";
		case 6: return selected == "2n";
		case 7: return selected == "2n + 1";
		case 8: return selected == "2n + 1";
		case 9: return selected == "2n";
		case 10: return selected == "2n + 1";
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
