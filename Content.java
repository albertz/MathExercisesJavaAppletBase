package applets.Abbildungen_I66_Zuweisung;

import java.awt.Point;
import java.util.Arrays;

public class Content {

	Applet applet;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(560, 454);
	}

	public void run() {
		PZuweisungExtended2 zuweisung = new PZuweisungExtended2(applet);
/*		zuweisung.oval1back.label = "X";
		zuweisung.oval1a.label = "U";
		zuweisung.oval1b.label = "V";
		zuweisung.oval2.label = "Y";
		//zuweisung.dotANames = Arrays.asList(new String[] {"A","B","C","D","E","F"});
		//zuweisung.dotsCountA = zuweisung.dotANames.size();
		//zuweisung.dotsCountB = zuweisung.dotsCountA + 5; */
		zuweisung.editable = false;
		zuweisung.reset();
		//zuweisung.addSurjectivConnections();
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(0), (Point)zuweisung.dotsB.get(0)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(1), (Point)zuweisung.dotsB.get(0)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(2), (Point)zuweisung.dotsB.get(2)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(3), (Point)zuweisung.dotsB.get(1)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(4), (Point)zuweisung.dotsB.get(2)));
		zuweisung.connections.add(new PZuweisungExtended2.Connection((Point)zuweisung.dotsA.get(5), (Point)zuweisung.dotsB.get(3)));
		
//		PGraph graph = new PGraph(applet, 600, 300);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("zuweisung", 10, 5, 600, 250, zuweisung)
		});
		
	}
}
