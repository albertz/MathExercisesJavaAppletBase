package applets.AnalytischeGeometrieundLA_01_2DGeradeSteigungsdreieck;

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
	
	final Vector<PGraph3D.MoveablePoint> pts = new Vector<PGraph3D.MoveablePoint>(); 
	PGraph3D.FrameUpdate updater = null;

	PGraph3D.Point[] p;
	PGraph3D.Point[] movedP;
	PGraph3D.MoveablePointOnPlane a;
	PGraph3D.Point anglePt;
	PGraph3D.Vector3D relAnglePt = new PGraph3D.Vector3D();
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
		
		for(int i = 0; i < 2; ++i)
			for(int j = 0; j < 2; ++j)
				try {
					((JLabel) applet.getComponentByName("R"+i+j)).setText("" + num(matrix.get(i, j)));
				} catch (Exception e) {
					((JLabel) applet.getComponentByName("R"+i+j)).setText("?");					
				}

		for (int i = 0; i < 2; ++i)
			try {
				((JLabel) applet.getComponentByName("A" + i)).setText("" + num(a.point.get(i)));
			} catch (Exception e) {
				((JLabel) applet.getComponentByName("A" + i)).setText("?");
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
		
		p = new PGraph3D.Point[4];
		p[0] = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(4,1,0), PGraph3D.Plane.zPlane, Color.blue);
		p[1] = new PGraph3D.Point(p[0].point.sum(new PGraph3D.Vector3D(2,0,0)), Color.black);
		p[2] = new PGraph3D.Point(p[1].point.sum(new PGraph3D.Vector3D(0,4,0)), Color.black);
		p[3] = new PGraph3D.Point(p[2].point.sum(new PGraph3D.Vector3D(-2,0,0)), Color.black);		
		a = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(1,1,0), PGraph3D.Plane.zPlane, Color.red);
		
		class MoveableAngle extends PGraph3D.MoveablePointOnPlane {			
			MoveableAngle() {
				graph.super(a.point.sum(new PGraph3D.Vector3D(2,0,0)).fixed(), PGraph3D.Plane.zPlane, Color.cyan);
			}

			public PGraph3D.Point3D pointForPos(Point p) {
				PGraph3D.DynVector3D p3d = super.pointForPos(p);
				try {
					return p3d.diff(a.point).norminated().product(new PGraph3D.Float(2)).sum(a.point).fixed();
				}
				catch(NullPointerException e) { return null; }
			}

		}
		
		anglePt = new MoveableAngle();
		((MoveableAngle) anglePt).updater.add(new PGraph3D.Vector3DUpdater(relAnglePt, anglePt.point.diff(a.point)));
		((MoveableAngle) anglePt).updater.add(updater);
		a.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D) anglePt.point, a.point.sum(relAnglePt)));
		a.updater.add(updater);
		((PGraph3D.MoveablePoint) p[0]).updater.add(updater);
		
		angle = new PGraph3D.DynFloat() {
			public double get() throws Exception {
				PGraph3D.DynVector3D v = relAnglePt;
				return Math.atan2(v.get(1), v.get(0));
			}
		};
		
		movedP = new PGraph3D.Point[4];
		for(int i = 0; i < 4; ++i)
			movedP[i] = new PGraph3D.Point(rotated(p[i].point), p[i].color);	
		
		for(int i = 0; i < 4; ++i) {
			graph.objects.add(p[i]);
			graph.objects.add(movedP[i]);
			graph.objects.add(new PGraph3D.Line(p[i].point, p[(i+1)%4].point.diff(p[i].point), false, Color.blue));
			graph.objects.add(new PGraph3D.Line(movedP[i].point, movedP[(i+1)%4].point.diff(movedP[i].point), false, Color.blue));
		}
		graph.objects.add(a);		
		graph.objects.add(new PGraph3D.Text(a.point, "A", a.color));		
		graph.objects.add(anglePt);
		graph.objects.add(new PGraph3D.VectorArrow(a.point, relAnglePt, Color.cyan));
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 20, graph.W, graph.H, graph),
				new VTMatrix("m1", 0, 0, new VisualThing[][] {
						new VisualThing[] { new VTLabel("cos(α)", 0, 0), new VTLabel("-sin(α)", 0, 0) },
						new VisualThing[] { new VTLabel("sin(α)", 0, 0), new VTLabel("cos(α)", 0, 0) }
				}),
				new VTMatrix("m2", 0, 0, new VisualThing[][] {
						new VisualThing[] { new VTLabel("R00", "www", 0, 0), new VTLabel("R01", "www", 0, 0) },
						new VisualThing[] { new VTLabel("R10", "www", 0, 0), new VTLabel("R11", "www", 0, 0) }
				}),
				new VTMatrix("A", 0, 0, new VisualThing[][] {
						new VisualThing[] { new VTLabel("A0", "www", 0, 0) },
						new VisualThing[] { new VTLabel("A1", "www", 0, 0) }
				})
		});
	}
	
}
