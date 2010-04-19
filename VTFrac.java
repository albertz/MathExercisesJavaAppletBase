/**
 * 
 */
package applets.AnalytischeGeometrieundLA_13_Kavalierprojektion;

import java.awt.Component;













public class VTFrac extends VTContainer {

	public VTFrac(int stepX, int stepY, String top, String down) {
		this(stepX, stepY, top, down, true);
	}
	
	public VTFrac(int stepX, int stepY, String top, String down, boolean withLine) {
		this(stepX, stepY,
				new VTLabel(top, 0, 0, "monospace"),
				new VTLabel(down, 0, 0, "monospace"),
				withLine);
	}
	
	public VTFrac(int stepX, int stepY, VisualThing top, VisualThing down) {
		this(stepX, stepY, top, down, true);
	}

	boolean withLine;
	int width, height;
	VisualThing top, down;
	
	public VTFrac(int stepX, int stepY, VisualThing top, VisualThing down, boolean withLine) {
		super(null, stepX, stepY, null);
		
		this.withLine = withLine; 
		this.top = top;
		this.down = down;
		
		defineMyself();
	}
	
	private void defineMyself() {
		width = Math.max(top.getWidth(), down.getWidth());
		if(withLine) width += 20;

		if(withLine) {
			this.things = new VisualThing[] {
				new VTEmptySpace(0, 0, (width - top.getWidth()) / 2, 5),
				top,
				new VTLine(0, -6, width),
				new VTEmptySpace(0, -2, (width - down.getWidth()) / 2, 5),
				down,
			};
			height = Math.max(5, top.getHeight()) + Math.max(5, down.getHeight()) + VTLine.height - 8;
		}
		else {
			this.things = new VisualThing[] {
				new VTEmptySpace(0, 0, (width - top.getWidth()) / 2, 5),
				top,
				new VTEmptySpace(0, -2, (width - down.getWidth()) / 2, 5),
				down,
			};
			height = Math.max(5, top.getHeight()) + Math.max(5, down.getHeight()) - 2;
		}
	}

	public Component getComponent() {
		if(panel == null)
			defineMyself();
		return super.getComponent();
	}
	
	public int getWidth() {
		// like in VTContainer, calc the size always again if not created yet
		if(panel == null)
			defineMyself();
		return width;
	}

	public int getHeight() {
		if(panel == null)
			defineMyself();
		return height;
	}
	
}
