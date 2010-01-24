package applets.AnalytischeGeometrieundLA_02_2DGeradeStuetzRichtung;

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
	PGraph3D graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(518, 498);
	}

	void postinit() { updateLabels(); }
	void next(int i) {}	
	boolean isCorrect(int i, String sel) { return false; }
	
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
	
	PGraph3D.FrameUpdate updater = null;
	
	static private String num(double n) {
		if(Double.isInfinite(n)) return (n >= 0) ? "∞" : "-∞";
		if(n < 0) return "-" + num(-n);
		return "" + (int)(Math.floor(n)) + "." + ((int)(Math.floor(n*10)) % 10);
	}
	
	static private String num(PGraph3D.DynFloat n) {
		try {
			return num(n.get());
		} catch (Exception e) {
			return "ungültig";
		}
	}

	PGraph3D.Vector3D pu;
	PGraph3D.MoveablePoint p, u;
	PGraph3D.Line line;
	
	void updateLabels() {		
		try {
			((JLabel) applet.getComponentByName("g")).setText("g : (x,y) = " + p.point.toString() + " + " + pu.toString() + "∙t");
		} catch (Exception e1) {
			((JLabel) applet.getComponentByName("g")).setText("Gerade g lässt sich nicht als Funktion darstellen");
		}

		for (int i = 0; i < 2; ++i)
			try {
				((JLabel) applet.getComponentByName("A" + i)).setText("" + num(p.point.get(i)));
			} catch (Exception e) {
				((JLabel) applet.getComponentByName("A" + i)).setText("?");
			}

		for (int i = 0; i < 2; ++i)
			try {
				((JLabel) applet.getComponentByName("B" + i)).setText("" + num(pu.get(i)));
			} catch (Exception e) {
				((JLabel) applet.getComponentByName("B" + i)).setText("?");
			}
	}
	
	public void run() {
		graph = new PGraph3D(applet, 480, 400);
		graph.viewport.scaleFactor = 30;
		graph.setOnlyXY(true);
		graph.addBaseAxes();
		
		PGraph3D.FrameUpdate updater = new PGraph3D.FrameUpdate() {
			public void doFrameUpdate(PGraph3D.Vector3D diff) {
				updateLabels();
			}
		};

		pu = new PGraph3D.Vector3D(5,4,0);

		p = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(2,3,0), PGraph3D.Plane.zPlane, Color.black);
		u = graph.new MoveablePointOnPlane(p.point.sum(pu).fixed(), PGraph3D.Plane.zPlane, Color.red);
		line = new PGraph3D.Line(p.point, pu, Color.blue);
		
		p.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D) u.point, p.point.sum(pu)));
		u.updater.add(new PGraph3D.Vector3DUpdater(pu, u.point.diff(p.point)));
		
		p.updater.add(updater);
		u.updater.add(updater);
		
		u.clickPriority = 2;
		
		graph.objects.add(p);
		graph.objects.add(u);
		graph.objects.add(new PGraph3D.VectorArrow(p.point, pu, Color.red));
		graph.objects.add(line);
		graph.objects.add(new PGraph3D.Text(p.point.sum(new PGraph3D.Vector3D(0,0.2,0)), "p", p.color));
		graph.objects.add(new PGraph3D.Text(u.point.sum(new PGraph3D.Vector3D(0,0.2,0)), "u", u.color));
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 20, graph.W, graph.H, graph),
				new VTMatrix("A", 0, 0, new VisualThing[][] {
						new VisualThing[] { new VTLabel("A0", "www", 0, 0) },
						new VisualThing[] { new VTLabel("A1", "www", 0, 0) }
				}),
				new VTMatrix("B", 0, 0, new VisualThing[][] {
						new VisualThing[] { new VTLabel("B0", "www", 0, 0) },
						new VisualThing[] { new VTLabel("B1", "www", 0, 0) }
				})
		});
	}
	
}
