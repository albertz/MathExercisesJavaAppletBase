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
		applet.setSize(620, 650);
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
		graph = new PGraph3D(applet, 600, 600);		
		graph.addBaseAxes();
			
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.W, graph.H, graph),
				new VTButton("newpt", "neuer Punkt", 10, 5, new Runnable() {
					public void run() {
						if(pts.size() >= 3) return;
						final PGraph3D.MoveablePoint pt = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(
								(pts.size() == 0) ? 4 : 1,
								(pts.size() == 1) ? 4 : 1,
								(pts.size() == 2) ? 4 : 1),
								Color.blue);
						pts.add(pt);
						graph.objects.add(pt);
						
						if(pts.size() == 1) {
							graph.objects.add(new PGraph3D.Plane(
									pt.point,
									new PGraph3D.DynVector3D() {
										public double get(int i) throws Exception {
											if(pts.size() >= 2) return pts.get(1).point.get(i) - pt.point.get(i);
											i %= 3;
											if(i == 0) return 1;
											if(i == 1) return ((Calendar.getInstance().getTimeInMillis() % 1000) - 500) * 0.001 * 20;
											return ((Calendar.getInstance().getTimeInMillis() % 1000) - 500) * 0.001 * 20;
										}
									},
									new PGraph3D.DynVector3D() {
										public double get(int i) throws Exception {
											if(pts.size() >= 3) return pts.get(2).point.get(i) - pt.point.get(i);
											i %= 3;
											if(i == 0) return 1;
											if(i == 1) return (((Calendar.getInstance().getTimeInMillis() + 300) % 2000) - 1000) * 0.001 * 20;
											return (((Calendar.getInstance().getTimeInMillis() + 1000) % 2000) - 1000) * 0.001 * 20;
										}
									}
									).setColor(Color.green)
							);							
						}
						
						newScheduledRedraw();
						applet.repaint();
					}
				})
		});	
	}
	
}
