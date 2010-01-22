package applets.AnalytischeGeometrieundLA_05_EbenenGleichung;

import java.awt.Color;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Content {

	Applet applet;
	PGraph3D graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(520, 530);
	}

	void postinit() {}
	void next(int i) {}
	
	protected String Round(double x) {
		return "" + (Math.round(x * 10) / 10.0);
	}
	
	protected PGraph.Point randomChoiceFrom(Collection<PGraph.Point> points) {
		PGraph.Point[] ar = points.toArray(new PGraph.Point[0]);
		int i = new Random().nextInt(ar.length);
		return ar[i];
	}
	
	final Vector<PGraph3D.MoveablePoint> pts = new Vector<PGraph3D.MoveablePoint>(); 

	void newScheduledRedraw() {
		TimerTask t = new TimerTask() {
			public void run() {
				applet.repaint();
				if(pts.size() < 3)
					newScheduledRedraw();
			}
		};
		new Timer().schedule(t, 50);
	}
	
	public void run() {
		graph = new PGraph3D(applet, 480, 400);		
		graph.addBaseAxes();
			
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.W, graph.H, graph),
				new VTButton("neueebene", "andere Ebene", 10, 5, new Runnable() {
					public void run() {
						applet.repaint();
					}
				})
		});	
	}
	
}
