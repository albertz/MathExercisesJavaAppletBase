/**
 * 
 */
package applets.Termumformungen$in$der$Technik_01_URI;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class VTText extends VisualThing {

	public VTText(String name, int stepX, int stepY, Runnable changeListener) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.changeListener = changeListener;
	}

	public VTText(String name, int stepX, int stepY, int width, Runnable changeListener) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.width = width;
		this.changeListener = changeListener;
	}
	
	private int stepX, stepY;
	private int width = 40;
	private String name = null;
	private JTextField text = null;
	private Runnable changeListener = null;

	public Component getComponent() {
		if (text == null) {
			text = new JTextField();
			text.setName(name);
			if (changeListener != null)
				text.getDocument().addDocumentListener(new DocumentListener() {
					public void removeUpdate(DocumentEvent e) {
						changeListener.run();
					}				
					public void insertUpdate(DocumentEvent e) {
						changeListener.run();
					}				
					public void changedUpdate(DocumentEvent e) {
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
		return width;
	}

	public int getHeight() {
		return 23;
	}

}
