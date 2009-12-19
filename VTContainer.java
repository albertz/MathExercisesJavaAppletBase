/**
 * 
 */
package applets.AnalytischeGeometrieundLA_Ebene_StuetzNormRichtung;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JPanel;







public class VTContainer extends VisualThing {

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
	protected Point size = null;

	public Component getComponent() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(null);
			panel.setName(name);
			size = Applet.addVisualThings(panel, things);
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
		if(panel == null)
			size = Applet.addVisualThings(panel, things, true);
		return size.x;
	}

	public int getHeight() {
		if(panel == null)
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
