package applets.Termumformungen$in$der$Technik_07_elastischerStoss;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

class JSimpleLabel extends JComponent {
	private static final long serialVersionUID = 1L;
	
	String text = "";
	Integer fixedWidth = null;
	
	JSimpleLabel() {}
	@SuppressWarnings({"UnusedDeclaration"})
	JSimpleLabel(String text) { this.text = text; setFont(Applet.defaultFont); }
	@SuppressWarnings({"UnusedDeclaration"})
	JSimpleLabel(String text, int fixedWidth) { this.text = text; this.fixedWidth = fixedWidth; setFont(Applet.defaultFont); }
	@SuppressWarnings({"UnusedDeclaration"})
	JSimpleLabel(String text, int fixedWidth, Font font) { this.text = text; this.fixedWidth = fixedWidth; setFont(font); }
	
	private abstract class WalkStringCallback {
		abstract void exec(String s, int x, int y);
	}	
	private Dimension walkString(WalkStringCallback callback) {
		Point cur = new Point();
		Dimension bounds = new Dimension();
		final int lineSpace = 5;
		FontMetrics metrics = getFontMetrics(getFont());
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
		g.setFont(getFont());
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
