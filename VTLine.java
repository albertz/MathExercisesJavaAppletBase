/**
 * 
 */
package applets.Termumformungen$in$der$Technik_06_Reflexionspunkt;







public class VTLine extends VTLabel {

	public VTLine(int stepX, int stepY, int width) {
		super("", stepX, stepY, "monospace");
		while(getWidth() < width) {
			this.setText(getText() + "―");
		}
	}

	public static int height = 10;
	
	public int getHeight() {
		return height;
	}
	
}
