/**
 * 
 */
package applets.Komplexe$Zahlen_Polarkoord_PolarZuEbene;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;








public class VTSelector extends VisualThing {

	public VTSelector(String name, String[] items, int stepX, int stepY,
			Runnable changeListener) {
		this.name = name;
		this.items = items;
		this.stepX = stepX;
		this.stepY = stepY;
		this.changeListener = changeListener;
	}

	private int stepX, stepY;
	private String name;
	private String[] items;
	private JComboBox selector = null;
	private Runnable changeListener = null;

	public Component getComponent() {
		if (selector == null) {
			selector = new JComboBox();
			selector.setName(name);
			selector.setFont(Applet.defaultFont);
			if (changeListener != null)
				selector.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						changeListener.run();
					}
				});
			for (int i = 0; i < items.length; i++)
				selector.addItem(items[i]);
		}
		return selector;
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
		int max = 0;
		for (int i = 0; i < items.length; i++)
			max = Math.max(max, selector.getFontMetrics(selector.getFont())
					.stringWidth(items[i]));

		return max + 50;
	}

	public int getHeight() {
		return 18;
	}

}
