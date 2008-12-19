package applets.ganze$und$natuerliche$Zahlen_prim_2;

import java.awt.Point;
import java.util.Arrays;












public class Content {

	Applet applet;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(610, 300);
	}

	public void run() {
		PZuweisungExtended2 zuweisung = new PZuweisungExtended2(applet);

		zuweisung.editable = false;
		zuweisung.reset();
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(0), (Point)zuweisung.dotsB.get(0)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(1), (Point)zuweisung.dotsB.get(0)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(2), (Point)zuweisung.dotsB.get(2)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(3), (Point)zuweisung.dotsB.get(1)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(4), (Point)zuweisung.dotsB.get(2)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(5), (Point)zuweisung.dotsB.get(3)));
		
	
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("zuweisung", 10, 5, 600, 250, zuweisung)
		});
		
	}
}
