/**
 * 
 */
package applets.Abbildungen_I63_Part1_UrbildX2;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

public class VTText extends VisualThing {

	public VTText(String name, int stepX, int stepY, Runnable changeListener) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.changeListener = changeListener;
	}

	private int stepX, stepY;
	private String name = null;
	private JTextField text = null;
	private Runnable changeListener = null;

	public Component getComponent() {
		if (text == null) {
			text = new JTextField();
			text.setName(name);
			if (changeListener != null)
				text.addKeyListener(new KeyListener() {
					public void keyPressed(KeyEvent e) {}
					public void keyReleased(KeyEvent e) {}

					public void keyTyped(KeyEvent e) {
						changeListener.run();
					}
				});
		}
		return text;
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
		return 40;
	}

	public int getHeight() {
		return 23;
	}

}