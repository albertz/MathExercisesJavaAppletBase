/**
 * 
 */
package applets.AnalytischeGeometrieundLA_04_Ebene_StuetzNormRichtung;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;







public class VTLabel extends VisualThing {

	public VTLabel(String text, int stepX, int stepY, String fontName) {
		this.text = text;
		this.stepX = stepX;
		this.stepY = stepY;
		setFontName(fontName);
	}

	public VTLabel(String text, int stepX, int stepY) {
		this.text = text;
		this.stepX = stepX;
		this.stepY = stepY;
		setFontName("default");
	}

	public VTLabel(String name, String text, int stepX, int stepY) {
		this.text = text;
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		setFontName("default");
	}

	private int stepX, stepY;
	private String name = null;
	private String text;
	private Font font = null;
	private Color color = Color.black;
	private JLabel label = null;

	public void setText(String text) {
		this.text = text;
		if(label != null)
			label.setText(text);
	}
	
	public String getText() {
		return text;
	}
	
	public void setFontName(String fontName) {
		if(fontName.compareToIgnoreCase("default") == 0)
			font = Applet.defaultFont;
		else if(fontName.compareToIgnoreCase("monospace") == 0)
			font = Applet.monospaceFont;
		else
			font = Applet.defaultFont;
			
		if(label != null) {
			label.setFont(font);
			label.setText(text); // to recalculate the size
		}
	}
	
	public String getFontName() {
		if(font == Applet.defaultFont) return "default";
		if(font == Applet.monospaceFont) return "monospace";
		return "default";
	}

	public Color getColor() {
		if(label != null)
			return label.getForeground();
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		if(label != null)
			label.setForeground(color);
	}
	
	public Component getComponent() {
		if (label == null) {
			label = new JLabel();
			if (name != null)
				label.setName(name);
			label.setFont(font);
			label.setText(text);
			label.setForeground(color);
			label.setOpaque(false);
			//label.setBackground(Color.cyan);
		}
		return label;
	}

	public int getHeight() {
		getComponent();
		return label.getFontMetrics(label.getFont()).getHeight();
	}

	public int getWidth() {
		getComponent();
		return label.getFontMetrics(label.getFont()).stringWidth(text);
	}
	
	public int getStepX() {
		return stepX;
	}
	
	public int getStepY() {
		return stepY;
	}

	public void setStepX(int v) {
		stepX = v;
	}

	public void setStepY(int v) {
		stepY = v;
	}

	public String getDebugStringExtra() {
		return ",\"" + text + "\"";
	}

}
