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

	PGraph3D.Float xab, yab;
	PGraph3D.MoveablePoint a, ab, b;
	PGraph3D.Line line;
	
	void updateLabels() {
		String txt = "";
		PGraph3D.DynFloat d = new PGraph3D.Line(new PGraph3D.Vector3D(0,0,0), PGraph3D.Plane.yPlane.normal).intersectionPoint(line).point.dynGet(1);
		txt += "x_B - x_A = " + num(xab.x);
		txt += ",    y_B - y_A = " + num(yab.x);
		txt += ",    m = " + num(yab.x/xab.x);
		txt += ",    d = " + num( d );
		((JLabel) applet.getComponentByName("rest")).setText(txt);
		
		try {
			((JLabel) applet.getComponentByName("g")).setText("Gerade g : y = " + num(yab.x/xab.x) + "∙x + " + num(d.get()));
		} catch (Exception e1) {
			((JLabel) applet.getComponentByName("g")).setText("Gerade g lässt sich nicht als Funktion darstellen");
		}

		for (int i = 0; i < 2; ++i)
			try {
				((JLabel) applet.getComponentByName("A" + i)).setText("" + num(a.point.get(i)));
			} catch (Exception e) {
				((JLabel) applet.getComponentByName("A" + i)).setText("?");
			}

		for (int i = 0; i < 2; ++i)
			try {
				((JLabel) applet.getComponentByName("B" + i)).setText("" + num(b.point.get(i)));
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

		xab = new PGraph3D.Float(5);
		yab = new PGraph3D.Float(3);

		a = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(2,3,0), PGraph3D.Plane.zPlane, Color.black);
		ab = graph.new MoveablePointOnLine(new PGraph3D.Vector3D(2+xab.x,3,0), new PGraph3D.Line(a.point, new PGraph3D.Vector3D(1, 0, 0)), Color.blue);
		b = graph.new MoveablePointOnLine(new PGraph3D.Vector3D(2+xab.x,3+yab.x,0), new PGraph3D.Line(ab.point, new PGraph3D.Vector3D(0, 1, 0)), Color.red);
		line = new PGraph3D.Line(a.point, b.point.diff(a.point), Color.black);
		
		a.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D) ab.point, a.point.sum(new PGraph3D.Vector3D(1,0,0).product(xab))));
		a.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D) b.point, ab.point.sum(new PGraph3D.Vector3D(0,1,0).product(yab))));
		ab.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D) b.point, ab.point.sum(new PGraph3D.Vector3D(0,1,0).product(yab))));
		ab.updater.add(new PGraph3D.FloatUpdater(xab, ab.point.diff(a.point).dynGet(0)));
		b.updater.add(new PGraph3D.FloatUpdater(yab, b.point.diff(ab.point).dynGet(1)));
		
		a.updater.add(updater);
		ab.updater.add(updater);
		b.updater.add(updater);
		
		ab.clickPriority = 1;
		b.clickPriority = 2;
		
		graph.objects.add(a);
		graph.objects.add(ab);
		graph.objects.add(b);
		graph.objects.add(new PGraph3D.VectorArrow(a.point, ab.point.diff(a.point), Color.blue));
		graph.objects.add(new PGraph3D.VectorArrow(ab.point, b.point.diff(ab.point), Color.red));
		graph.objects.add(line);
		graph.objects.add(new PGraph3D.Text(a.point, "A", a.color));
		graph.objects.add(new PGraph3D.Text(b.point, "B", b.color));
		
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
