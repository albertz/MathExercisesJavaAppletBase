/**
 * 
 */
package applets.Termumformungen$in$der$Technik_01_URI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;








public class VTLineCombiner extends VTContainer {

	public VTLineCombiner(String name, int stepX, int stepY, VisualThing[] things) {
		super((name == null || name.isEmpty()) ? "__VTLineCombiner" : name, stepX, stepY, things);
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
			panel = new JPanel() {
				private static final long serialVersionUID = 1L;
				@Override public void doLayout() {
					revalidateThings();
					setPreferredSize(new Dimension(VTLineCombiner.this.size.x, VTLineCombiner.this.size.y));					
				}
			};
			panel.setName(name);
			panel.setLayout(null);
			panel.doLayout();
		}
		return panel;
	}

	public void revalidateThings() {
		for (int i = 0; i < things.length; i++) {
			Component c = things[i].getComponent();
			if(c == null) continue;
			
			if(c.getParent() != panel)
				panel.add(c);
			c.setBounds(0, 0, things[i].getWidth(), things[i].getHeight());
			c.doLayout();
		}
		
		calcSize();
		
		int curX = 0, curY = 0;
		for (int i = 0; i < things.length; i++) {
			Component c = things[i].getComponent();
			if(c == null) continue;

			curY = (size.y - things[i].getHeight()) / 2;
			curX += things[i].getStepX() + things[i].getWidth();
			c.setBounds(curX, curY, things[i].getWidth(), things[i].getHeight());
			System.out.println("  " + things[i] + ".bounds = " + c.getBounds());
		}
	}
	
	public int getHeight() {
		if(panel == null || size == null) calcSize();
		return size.y;
	}

	public int getWidth() {
		if(panel == null || size == null) calcSize();
		return size.x;
	}

	@Override public String toString() {
		return super.toString() + " {" + Utils.concat(Utils.iterableFromArray(things), ", ") + "}";
	}
}
