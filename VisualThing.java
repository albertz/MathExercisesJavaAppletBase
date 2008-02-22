package applets.Abbildungen_I08_KompositionBereiche;

import java.awt.Component;

/**
 * z.B. nen Label oder ein Selector
 */
public abstract class VisualThing {
	/**
	 * Breite vom Ding
	 */
	public abstract int getWidth();
	
	/**
	 * Höhe vom Ding
	 */
	public abstract int getHeight();

	/**
	 * wie weit nach rechts vom letzten Ding aus, also wo soll ich beginnen;
	 * ist der Wert negativ, so wirt er absolut als Index von der vorherigen
	 * Reihe interpretiert, also z.B. -1 bezeichnet die X-Position des 1.
	 * Items in der vorherigen Reihe
	 */
	public abstract int getStepX();
	public abstract void setStepX(int v);

	/**
	 * wie getStepX, nur für Y; wenn >0, wird außerdem wieder ganz links
	 * begonnen
	 */
	public abstract int getStepY();
	public abstract void setStepY(int v);

	/**
	 * und die eigentliche Komponente
	 */
	public abstract Component getComponent();
	
	/**
	 * Debug-string
	 */
	public String getDebugString() {
		return this.getClass().getSimpleName() + "("
			+ getStepX() + "," + getStepY() + ","
			+ getWidth() + "," + getHeight()
			+ getDebugStringExtra() + ")";
	}
	
	/**
	 * Extra-info (like Label.txt) 
	 */
	public String getDebugStringExtra() {
		return "";
	}
}