package applets.AnalytischeGeometrieundLA_01a_2DGeradenSchnittParallel;

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
	PGraph3D.Float xab2, yab2;
	PGraph3D.MoveablePoint a2, ab2, b2;
	PGraph3D.Line line2;
	PGraph3D.Point schnitt;
	
	void updateLabels() {
		String txt;
		PGraph3D.DynFloat d = new PGraph3D.Line(new PGraph3D.Vector3D(0,0,0), PGraph3D.Plane.yPlane.normal).intersectionPoint(line).point.dynGet(1);
		txt = "A1 = " + a.point.toString2D();
		txt += ",    B1 = " + b.point.toString2D();
		txt += ",    m1 = " + num(yab.x/xab.x);
		txt += ",    d1 = " + num( d );
		((JLabel) applet.getComponentByName("g1")).setText(txt);
		
		PGraph3D.DynFloat d2 = new PGraph3D.Line(new PGraph3D.Vector3D(0,0,0), PGraph3D.Plane.yPlane.normal).intersectionPoint(line2).point.dynGet(1);
		txt = "A2 = " + a2.point.toString2D();
		txt += ",    B2 = " + b2.point.toString2D();
		txt += ",    m2 = " + num(yab2.x/xab2.x);
		txt += ",    d2 = " + num( d2 );
		((JLabel) applet.getComponentByName("g2")).setText(txt);
		
		if(schnitt.point.isValid())
			((JLabel) applet.getComponentByName("schnitt")).setText("Schnittpunkt: " + schnitt.point.toString2D());
		else if(line.lineIsEqual(line2))
			((JLabel) applet.getComponentByName("schnitt")).setText("Geraden liegen übereinander");
		else
			((JLabel) applet.getComponentByName("schnitt")).setText("Geraden sind parallel ohne Berührungspunkt");
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
		graph.objects.add(new PGraph3D.Text(a.point, "A1", a.color));
		graph.objects.add(new PGraph3D.Text(b.point, "B1", b.color));
		
		// Hack to not accidently use these anymore
		//int xab,yab,line,a,ab,b;
		
		xab2 = new PGraph3D.Float(5);
		yab2 = new PGraph3D.Float(3);

		a2 = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(-2,5,0), PGraph3D.Plane.zPlane, Color.black);
		ab2 = graph.new MoveablePointOnLine(new PGraph3D.Vector3D(2+xab2.x,3,0), new PGraph3D.Line(a2.point, new PGraph3D.Vector3D(1, 0, 0)), Color.blue);
		b2 = graph.new MoveablePointOnLine(new PGraph3D.Vector3D(2+xab2.x,3+yab2.x,0), new PGraph3D.Line(ab2.point, new PGraph3D.Vector3D(0, 1, 0)), Color.red);
		line2 = new PGraph3D.Line(a2.point, b2.point.diff(a2.point), Color.black);
		
		a2.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D) ab2.point, a2.point.sum(new PGraph3D.Vector3D(1,0,0).product(xab2))));
		a2.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D) b2.point, ab2.point.sum(new PGraph3D.Vector3D(0,1,0).product(yab2))));
		ab2.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D) b2.point, ab2.point.sum(new PGraph3D.Vector3D(0,1,0).product(yab2))));
		ab2.updater.add(new PGraph3D.FloatUpdater(xab2, ab2.point.diff(a2.point).dynGet(0)));
		b2.updater.add(new PGraph3D.FloatUpdater(yab2, b2.point.diff(ab2.point).dynGet(1)));
		
		a2.updater.add(updater);
		ab2.updater.add(updater);
		b2.updater.add(updater);
		
		ab2.clickPriority = 1;
		b2.clickPriority = 2;
		
		graph.objects.add(a2);
		graph.objects.add(ab2);
		graph.objects.add(b2);
		graph.objects.add(new PGraph3D.VectorArrow(a2.point, ab2.point.diff(a2.point), Color.blue));
		graph.objects.add(new PGraph3D.VectorArrow(ab2.point, b2.point.diff(ab2.point), Color.red));
		graph.objects.add(line2);
		graph.objects.add(new PGraph3D.Text(a2.point, "A2", a2.color));
		graph.objects.add(new PGraph3D.Text(b2.point, "B2", b2.color));

		schnitt = line.intersectionPoint(line2);
		schnitt.color = Color.blue;
		graph.objects.add(schnitt);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 20, graph.W, graph.H, graph),
		});
	}
	
}
