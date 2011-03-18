package applets.Termumformungen$in$der$Technik_07_elastischerStoss;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;






public class VTImage extends VisualThing implements Applet.CorrectCheck {

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
				private static final long serialVersionUID = 1L;
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
		if(painter instanceof Utils.Size) return ((Utils.Size) painter).getWidth();
		return width;
	}

	public int getHeight() {
		if(painter instanceof Utils.Size) return ((Utils.Size) painter).getHeight();
		return height;
	}

	public boolean isCorrect() {
		return ((Applet.CorrectCheck)painter).isCorrect();
	}
	
	public String getResultMsg() {
		return ((Applet.CorrectCheck)painter).getResultMsg();
	}
	
	
}
