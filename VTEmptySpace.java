/*
 *  MathExercisesJavaAppletBase
 * 
 * by Albert Zeyer / developed for Lehrstuhl A für Mathematik at the RWTH Aachen University
 * code under GPLv3+
 */
package applets.Termumformungen$in$der$Technik_08_Ethanolloesungen;

import java.awt.Component;







public class VTEmptySpace extends VisualThing {

	public VTEmptySpace(int stepX, int stepY, int width, int height) {
		this.stepX = stepX;
		this.stepY = stepY;
		this.width = width;
		this.height = height;
	}

	private int stepX, stepY;
	private int width, height;

	public Component getComponent() {
		return null;
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
		return width;
	}

	public int getHeight() {
		return height;
	}

}
