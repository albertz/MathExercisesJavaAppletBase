/**
 * 
 */
package applets.ganze$und$natuerliche$Zahlen_prim_1;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;








public class VTLineCombiner extends VTContainer {

	public VTLineCombiner(String name, int stepX, int stepY, VisualThing[] things) {
		super(name, stepX, stepY, things);
	}
	
	protected void calcSize() {
		size = new Point(0, 5);
		for(int i = 0; i < things.length; i++) {
			size.y = Math.max(size.y, things[i].getHeight());
			size.x += things[i].getWidth() + things[i].getStepX();
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
		calcSize();
		int curX = 0, curY = 0;

		for (int i = 0; i < things.length; i++) {
			Component c = things[i].getComponent();
			curY = (size.y - things[i].getHeight()) / 2;
			curX += things[i].getStepX();
			if(c != null) {
				c.setBounds(new Rectangle(curX, curY, things[i].getWidth(), things[i].getHeight()));
				panel.add(c);
			}
			curX += things[i].getWidth();
		}
	}
	
	public int getHeight() {
		if(panel == null) calcSize();
		return size.y;
	}

	public int getWidth() {
		if(panel == null) calcSize();
		return size.x;
	}

}
