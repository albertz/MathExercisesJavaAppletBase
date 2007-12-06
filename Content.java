package applets.Abbildungen_I63_Part1_UrbildX2;

import java.util.Arrays;

public class Content {

	Applet applet;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(665, 404);
	}

	public void run() {
/*		PZuweisungGfx zuweisung = new PZuweisungGfx(applet);
		zuweisung.oval1.label = "X";
		zuweisung.oval2.label = "Y";
		zuweisung.dotANames = Arrays.asList(new String[] {"A","B","C","D","E","F"});
		zuweisung.dotsCountA = zuweisung.dotANames.size();
		zuweisung.dotsCountB = zuweisung.dotsCountA + 5;
		zuweisung.editable = false;
		zuweisung.reset();
		zuweisung.addSurjectivConnections();
*/
		
		PGraph graph = new PGraph(applet, 300, 300);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.getW(), graph.getH(), graph)
		});
		
	}
}
