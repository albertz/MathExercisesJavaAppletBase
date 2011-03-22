/*
 *  MathExercisesJavaAppletBase
 * 
 * by Albert Zeyer / developed for Lehrstuhl A für Mathematik at the RWTH Aachen University
 * code under GPLv3+
 */
package applets.Termumformungen$in$der$Technik_08_Ethanolloesungen;







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
