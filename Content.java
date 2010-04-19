package applets.AnalytischeGeometrieundLA_13_Kavalierprojektion;

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

	PGraph3D.MoveablePointOnPlane a;
	PGraph3D.DynFloat angle;
	PGraph3D.DynMatrix2D matrix = new PGraph3D.DynMatrix2D() {
		public double get(int i, int j) throws Exception {
			i %= 2; j %= 2;
			if(i == 0 && j == 0) return Math.cos(angle.get());
			if(i == 1 && j == 0) return Math.sin(angle.get());
			if(i == 0 && j == 1) return -Math.sin(angle.get());
			if(i == 1 && j == 1) return Math.cos(angle.get());
			throw new Exception("matrix get: invalid indexes " + i + "," + j);
		}
	};
	
	PGraph3D.DynVector3D rotated(PGraph3D.DynVector3D p) {
		return matrix.mult( p.diff(a.point) ).sum( a.point );
	}
	
	static private String num(double n) {
		if(n < 0) return "-" + num(-n);
		return "" + (int)(Math.floor(n)) + "." + ((int)(Math.floor(n*10)) % 10);
	}
	
	void updateLabels() {
		try {
			((JLabel) applet.getComponentByName("alpha")).setText("" + (int)(angle.get()*180/Math.PI) + "°");
		} catch (Exception e1) {
			((JLabel) applet.getComponentByName("alpha")).setText("?");
		}

		try {
			((JLabel) applet.getComponentByName("a")).setText(a.point.abs().toString());
		} catch (Exception e1) {
			((JLabel) applet.getComponentByName("a")).setText("?");
		}
	}
	
	final PGraph3D.Vector3D[] pts = new PGraph3D.Vector3D[] {
			new PGraph3D.Vector3D(0, 0, 0),
			new PGraph3D.Vector3D(0, 10, 0),
			new PGraph3D.Vector3D(7, 10, 0),
			new PGraph3D.Vector3D(7, 0, 0),
			new PGraph3D.Vector3D(0, 0, 3),
			new PGraph3D.Vector3D(0, 10, 3),
			new PGraph3D.Vector3D(7, 10, 3),
			new PGraph3D.Vector3D(7, 0, 3),
			new PGraph3D.Vector3D(3.5, 0, 7),
			new PGraph3D.Vector3D(3.5, 10, 7),			
	};
	
	static float sumDiffComponents(PGraph3D.Vector3D v1, PGraph3D.Vector3D v2) {
		float sum = 0;
		for(int i = 0; i < 3; ++i)
			sum += Math.abs(v1.get(i) - v2.get(i));
		return sum;
	}

	static int numDiffComponents(PGraph3D.Vector3D v1, PGraph3D.Vector3D v2) {
		int sum = 0;
		for(int i = 0; i < 3; ++i)
			if(Math.abs(v1.get(i) - v2.get(i)) > 0.01) sum++;
		return sum;
	}

	PGraph3D.DynVector3D kavalierProjection(final PGraph3D.DynVector3D v) {
		return new PGraph3D.DynVector3D() {
			public double get(int i) throws Exception {
				i %= 3;
				switch(i) {
				case 0: return v.get(0) * -Math.cos(angle.get()) * a.point.abs().get() + v.get(1);
				case 1: return v.get(0) * -Math.sin(angle.get()) * a.point.abs().get() + v.get(2);
				}
				return 0;
			}
		};
	}
	
	public void run() {
		graph = new PGraph3D(applet, 480, 430);
		graph.viewport.scaleFactor = 30;
		graph.setOnlyXY(true);
		graph.addBaseAxes();
		
		PGraph3D.FrameUpdate updater = new PGraph3D.FrameUpdate() {
			public void doFrameUpdate(PGraph3D.Vector3D diff) {
				updateLabels();
			}
		};
		
		for(PGraph3D.Vector3D p : pts) {
			graph.objects.add(new PGraph3D.Point(kavalierProjection(p), Color.blue));
			for(PGraph3D.Vector3D q : pts) {
				if(p == q) continue;
				if(sumDiffComponents(p, q) < 8 || numDiffComponents(p, q) == 1)
					graph.objects.add(new PGraph3D.Line(kavalierProjection(p), kavalierProjection(q.diff(p)), false, Color.black));
			}
		}
		
		a = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(-1,-1,0), PGraph3D.Plane.zPlane, Color.red);
		a.updater.add(updater);
		
		angle = new PGraph3D.DynFloat() {
			public double get() throws Exception {
				PGraph3D.DynVector3D v = a.point;
				return Math.atan2(-v.get(1), -v.get(0));
			}
		};
		
		graph.objects.add(a);		
		graph.objects.add(new PGraph3D.VectorArrow(new PGraph3D.Vector3D(), a.point, Color.red));
		graph.objects.add(new PGraph3D.Text(a.point, "a", a.color));		
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 20, graph.W, graph.H, graph),
		});
	}
	
}
