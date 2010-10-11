package applets.Termumformungen$in$der$Technik_01_URI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.JLabel;

public class Content {

	Applet applet;
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(518, 498);
	}

	void postinit() {}
	void next(int i) {}	
	boolean isCorrect(int i, String sel) { return false; }
		
	public void run() {
		graph = new PGraph(applet, 480, 400);
		ElectronicCircuit e = new ElectronicCircuit();
		e.randomSetup(4, 4);
		e.registerOnPGraph(graph);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 20, 480, 400, graph),
		});
	}
	
}
