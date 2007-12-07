package applets.Abbildungen_I57_SchnittZuweisung;

import java.util.Arrays;

public class Content {

	Applet applet;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(502, 366);
	}

	public void run() {
		PZuweisungExtended zuweisung = new PZuweisungExtended(applet);
		zuweisung.oval1back.label = "X";
		zuweisung.oval1a.label = "U";
		zuweisung.oval1b.label = "V";
		zuweisung.oval2.label = "Y";
		//zuweisung.dotANames = Arrays.asList(new String[] {"A","B","C","D","E","F"});
		//zuweisung.dotsCountA = zuweisung.dotANames.size();
		//zuweisung.dotsCountB = zuweisung.dotsCountA + 5;
		//zuweisung.editable = false;
		zuweisung.reset();
		//zuweisung.addSurjectivConnections();

		
//		PGraph graph = new PGraph(applet, 600, 300);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("zuweisung", 10, 5, 500, 250, zuweisung)
		});
		
	}
}
