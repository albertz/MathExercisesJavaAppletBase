/**
 * 
 */
package applets.Termumformungen$in$der$Technik_05_Fadenstrahlrohr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPanel;







public class VTContainer extends VisualThing {

	public VTContainer(int stepX, int stepY,
			VisualThing[] things) {
		this(null, stepX, stepY, things);
	}

	public VTContainer(String name, int stepX, int stepY,
			VisualThing[] things) {
		if(name == null || name.isEmpty()) name = "__" + this.getClass().getName() + "@" + this.hashCode();
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.things = things;
	}

	protected int stepX, stepY;
	protected String name;
	protected JPanel panel = null;
	protected VisualThing[] things;
	protected Point size = null;

	public Component getComponent() {
		if (panel == null) {
			panel = new JPanel() {
				private static final long serialVersionUID = 1L;
				@Override public void doLayout() {
					VTContainer.this.size = Applet.addVisualThings(this, VTContainer.this.things);
					setPreferredSize(new Dimension(VTContainer.this.size.x, VTContainer.this.size.y));					
				}				
			};
			panel.setLayout(null);
			panel.setName(name);
			panel.revalidate();
		}
		return panel;
	}

	public VisualThing[] getThings() {
		return things;
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
		// when we have not generated the panel yet,
		// then calculate always a new size (because we are perhaps
		// changing things until we realy create the component)
		if(panel == null || size == null)
			size = Applet.addVisualThings(panel, things, true);
		return size.x;
	}

	public int getHeight() {
		if(panel == null || size == null)
			size = Applet.addVisualThings(panel, things, true);
		return size.y;
	}

	public String getDebugStringExtra() {
		String list = "";
		for(int i = 0; i < things.length; i++) {
			if(i != 0) list += ", ";
			list += things[i].getDebugString();
		}
		return ", {" + list + "} ";
	}
}
