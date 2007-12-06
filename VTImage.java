package applets.Abbildungen_I63_Part2_UrbildX3m;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class VTImage extends VisualThing {

	public static interface Painter {
		public void paint(Graphics g);
	}

	public static interface PainterAndListener extends Painter,
			MouseListener, MouseMotionListener {
	}

	public VTImage(String name, int stepX, int stepY, int width,
			int height, PainterAndListener painter) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.width = width;
		this.height = height;
		this.painter = painter;
	}

	private int stepX, stepY;
	private int width, height;
	private String name;
	private Component panel = null;
	private PainterAndListener painter;

	public Component getComponent() {
		if (panel == null) {
			panel = new Component() {
				public void paint(Graphics g) {
					painter.paint(g);
				}
			};
			panel.setName(name);
			panel.setSize(width, height);
			panel.addMouseListener(painter);
			panel.addMouseMotionListener(painter);
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
		return width;
	}

	public int getHeight() {
		return height;
	}

}