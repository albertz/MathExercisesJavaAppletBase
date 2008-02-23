/**
 * 
 */
package applets.Abbildungen_I03_Abbildungen;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class VTButton extends VisualThing {

	public VTButton(String name, String text, int stepX, int stepY,
			Runnable actionListener) {
		this.name = name;
		this.text = text;
		this.stepX = stepX;
		this.stepY = stepY;
		this.actionListener = actionListener;
	}

	public VTButton(String text, int stepX, int stepY,
			Runnable actionListener) {
		this.text = text;
		this.stepX = stepX;
		this.stepY = stepY;
		this.actionListener = actionListener;
	}

	private int stepX, stepY;
	private String name = null;
	private String text;
	private JButton button = null;
	private Runnable actionListener;

	public Component getComponent() {
		if (button == null) {
			button = new JButton();
			button.setText(text);
			if (name != null)
				button.setName(name);
			if(actionListener != null)
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						actionListener.run();
					}
				});
		}
		return button;
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
		getComponent();
		return button.getFontMetrics(button.getFont()).stringWidth(text) + 40;
	}

	public int getHeight() {
		return 23;
	}

}