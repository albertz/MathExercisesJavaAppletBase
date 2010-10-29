/**
 * 
 */
package applets.Termumformungen$in$der$Technik_01_URI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;



public class VTLabel extends VisualThing {

	public VTLabel(String text, int stepX, int stepY, String fontName) {
		this.text = text;
		this.stepX = stepX;
		this.stepY = stepY;
		setFontName(fontName);
	}

	public VTLabel(String text, int stepX, int stepY) {
		this.text = text;
		this.stepX = stepX;
		this.stepY = stepY;
		setFontName("default");
	}

	public VTLabel(String name, String text, int stepX, int stepY) {
		this.text = text;
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		setFontName("default");
	}

	public VTLabel(String name, String text, int stepX, int stepY, int fixedWidth) {
		this(name, text, stepX, stepY);
		this.fixedWidth = fixedWidth;
	}

	private int stepX, stepY;
	private String name = null;
	private String text;
	private Font font = null;
	private Color color = Color.black;
	private Label label = null;
	private Integer fixedWidth = null;

	public String getName() { return name; }
	
	class Label extends JComponent {
		private static final long serialVersionUID = 1L;
		abstract class WalkStringCallback {
			abstract void exec(String s, int x, int y);
		}
		
		Dimension walkString(WalkStringCallback callback) {
			Point cur = new Point();
			Dimension bounds = new Dimension();
			final int lineSpace = 5;
			FontMetrics metrics = getFontMetrics(font);
			for(String s : Utils.tokenizeString(text)) {
				if(s.equals("\n")) {
					cur.x = 0;
					cur.y += metrics.getHeight() + lineSpace;
					continue;
				}
				Rectangle2D rect = metrics.getStringBounds(s, getGraphics());
				if(fixedWidth != null) {
					if(cur.x > 0 && cur.x + rect.getWidth() > fixedWidth) {
						cur.x = 0;
						cur.y += metrics.getHeight() + lineSpace;				
					}
				}
				if(callback != null)
					callback.exec(s, cur.x, cur.y + metrics.getLeading() + metrics.getMaxAscent());
				cur.x += rect.getWidth();
				bounds.width = Math.max(bounds.width, cur.x);
			}
			bounds.height = cur.y + metrics.getHeight();
			return bounds;
		}
		
		@Override public void paint(final Graphics g) {
			super.paint(g);
			g.setFont(font);
			walkString(new WalkStringCallback() {
				@Override void exec(String s, int x, int y) {
					g.drawString(s, x, y);
				}
			});
		}
		
		@Override public Dimension getPreferredSize() {
			return walkString(null);
		}
	}
	
	public void setText(String text) {
		this.text = text;
		if(label != null)
			label.repaint(); // to recalculate the size
	}
	
	public String getText() {
		return text;
	}
	
	public void setFontName(String fontName) {
		if(fontName.compareToIgnoreCase("default") == 0)
			font = Applet.defaultFont;
		else if(fontName.compareToIgnoreCase("monospace") == 0)
			font = Applet.monospaceFont;
		else
			font = Applet.defaultFont;
			
		if(label != null) {
			label.setFont(font);
			label.repaint(); // to recalculate the size
		}
	}
	
	public String getFontName() {
		if(font == Applet.defaultFont) return "default";
		if(font == Applet.monospaceFont) return "monospace";
		return "default";
	}

	public Color getColor() {
		if(label != null)
			return label.getForeground();
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		if(label != null) {
			label.setForeground(color);
			label.repaint();
		}
	}
	
	public Component getComponent() {
		if (label == null) {
			label = new Label();
			if (name != null)
				label.setName(name);
			label.setFont(font);
			label.setForeground(color);
			label.setOpaque(false);
			//label.setBackground(Color.cyan);
		}
		return label;
	}

	public int getHeight() {
		getComponent();
		return label.getPreferredSize().height;
	}

	public int getWidth() {
		if(fixedWidth != null) return fixedWidth;
		getComponent();
		return label.getPreferredSize().width;
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

	public String getDebugStringExtra() {
		return ",\"" + text + "\"";
	}

	@Override public String toString() {
		return super.toString() + " \"" + text + "\"";
	}
}
