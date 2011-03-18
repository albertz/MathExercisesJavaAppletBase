package applets.Termumformungen$in$der$Technik_07_elastischerStoss;

import java.awt.Component;

/**
 * z.B. nen Label oder ein Selector
 */
public abstract class VisualThing {
	/**
	 * @return Breite vom Ding
	 */
	public abstract int getWidth();
	
	/**
	 * @return Höhe vom Ding
	 */
	public abstract int getHeight();

	/**
	 * @return wie weit nach rechts vom letzten Ding aus, also wo soll ich beginnen;
	 * ist der Wert negativ, so wirt er absolut als Index von der vorherigen
	 * Reihe interpretiert, also z.B. -1 bezeichnet die X-Position des 1.
	 * Items in der vorherigen Reihe
	 */
	public abstract int getStepX();
	public abstract void setStepX(int v);

	/**
	 * @return wie getStepX, nur für Y; wenn >0, wird außerdem wieder ganz links
	 * begonnen
	 */
	public abstract int getStepY();
	public abstract void setStepY(int v);

	/**
	 * @return und die eigentliche Komponente
	 */
	public abstract Component getComponent();
	
	/**
	 * @return Debug-string
	 */
	public String getDebugString() {
		return this.getClass().getName() + "("
			+ getStepX() + "," + getStepY() + ","
			+ getWidth() + "," + getHeight()
			+ getDebugStringExtra() + ")";
	}
	
	/**
	 * @return Extra-info (like Label.txt)
	 */
	public String getDebugStringExtra() {
		return "";
	}
	
	@Override public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
	}
}
