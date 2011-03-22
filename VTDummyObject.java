/*
 *  MathExercisesJavaAppletBase
 * 
 * by Albert Zeyer / developed for Lehrstuhl A für Mathematik at the RWTH Aachen University
 * code under GPLv3+
 */
package applets.Termumformungen$in$der$Technik_08_Ethanolloesungen;

import java.awt.Component;

public class VTDummyObject extends VisualThing implements Applet.CorrectCheck {

	public VTDummyObject(String name, int stepX, int stepY, Applet.CorrectCheck checker) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.checker = checker;
	}

	private int stepX, stepY;
	private String name;
	private Component panel = null;
	private Applet.CorrectCheck checker;

	public Component getComponent() {
		if (panel == null) {
			panel = new Component() { private static final long serialVersionUID = 1L; };
			panel.setName(name);
			panel.setSize(0, 0);
		}
		return panel;
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
		return 0;
	}

	public int getHeight() {
		return 0;
	}

	public boolean isCorrect() {
		return checker.isCorrect();
	}
	
	public String getResultMsg() {
		return checker.getResultMsg();
	}
	
	
}
