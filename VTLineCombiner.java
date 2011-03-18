/**
 * 
 */
package applets.Termumformungen$in$der$Technik_03_Logistik;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;



public class VTLineCombiner extends VTContainer {

	public VTLineCombiner(String name, int stepX, int stepY, VisualThing[] things) {
		super((name == null || name.isEmpty()) ? "__VTLineCombiner" : name, stepX, stepY, things);
	}
	
	protected void calcSize() {
		size = new Point(0, 5);
		for(VisualThing thing : things) {
			size.y = Math.max(size.y, thing.getHeight());
			size.x += thing.getWidth() + thing.getStepX();
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
		}
		return panel;
	}

	public void revalidateThings() {
		for(VisualThing thing : things) {
			Component c = thing.getComponent();
			if(c != null) {
				if(c.getParent() != panel)
					panel.add(c);
				c.setSize(thing.getWidth(), thing.getHeight());
				c.doLayout();
			}
		}		
		
		calcSize();
		int curX = 0;

		for(VisualThing thing : things) {
			Component c = thing.getComponent();
			int curY = (size.y - thing.getHeight()) / 2;
			curX += thing.getStepX();
			if(c != null)
				c.setBounds(curX, curY, thing.getWidth(), thing.getHeight());
			curX += thing.getWidth();
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
