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
	
	boolean isCorrect(int i, String sel) {
		double eps = 0.1;
		switch(i) {
		case 1:
			if(height.x != 0) {
				if(!Applet.equalParseNum(sel, 0, eps))
					return true;
				return false;
			}
			return Applet.equalParseNum(sel, 0, eps);
		case 2:
		case 3:
		case 4:
			double f;
			if(height.x != 0)
				f = Applet.parseNum(applet.getSelected(1)) / height.x;
			else if(normal.x[0] != 0)
				f = Applet.parseNum(applet.getSelected(2)) / normal.x[0];
			else if(normal.x[1] != 0)
				f = Applet.parseNum(applet.getSelected(3)) / normal.x[1];
			else if(normal.x[2] != 0)
				f = Applet.parseNum(applet.getSelected(4)) / normal.x[2];
			else
				f = 0;
			
			return Applet.equalParseNum(sel, normal.x[i - 2] * f, eps);
		}
		return false;
	}
	
	protected String Round(double x) {
		return "" + (Math.round(x * 10) / 10.0);
	}
	
	protected int randomChoiceFrom(int[] ar) {
		int i = new Random().nextInt(ar.length);
		return ar[i];
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
	
	final PGraph3D.Float height = new PGraph3D.Float();
	final PGraph3D.Vector3D normal = new PGraph3D.Vector3D();

	void neueEbene() {
		height.x = randomChoiceFrom(new int[]{0,1,2,3});
		normal.x[0] = randomChoiceFrom(new int[]{0,1,2,3});
		normal.x[1] = randomChoiceFrom(new int[]{0,1,2,3});
		normal.x[2] = randomChoiceFrom(new int[]{0,1,2,3});
		System.out.println("neueEbene: " + height.x + " = " + normal.x[0] + "*x + " + normal.x[1] + "*y + " + normal.x[2] + "*z");
	}
	
	public void run() {
		graph = new PGraph3D(applet, 480, 400);		
		graph.addBaseAxes();
		
		PGraph3D.Plane ebene = new PGraph3D.Plane(height, normal, Color.red);
		graph.objects.add(ebene);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.W, graph.H, graph),
				new VTButton("neueebene", "andere Ebene", 10, 5, new Runnable() {
					public void run() {
						neueEbene();
						applet.repaint();
					}
				})
				
		});
		
		neueEbene();
	}
	
}
