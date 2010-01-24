package applets.AnalytischeGeometrieundLA_12_2DDrehung;

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
		applet.setSize(500, 420);
	}

	void postinit() { /*updater.doFrameUpdate(null);*/ }
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
	
	public void run() {
		graph = new PGraph3D(applet, 480, 400);
		graph.viewport.scaleFactor = 30;
		graph.setOnlyXY(true);
		graph.addBaseAxes();
		
		p = new PGraph3D.Point[4];
		p[0] = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(2,1,0), PGraph3D.Plane.zPlane, Color.blue);
		p[1] = new PGraph3D.Point(p[0].point.sum(new PGraph3D.Vector3D(2,0,0)), Color.black);
		p[2] = new PGraph3D.Point(p[1].point.sum(new PGraph3D.Vector3D(0,4,0)), Color.black);
		p[3] = new PGraph3D.Point(p[2].point.sum(new PGraph3D.Vector3D(-2,0,0)), Color.black);		
		
		for(int i = 0; i < 4; ++i) {
			graph.objects.add(p[i]);
			graph.objects.add(new PGraph3D.Line(p[i].point, p[(i+1)%4].point.diff(p[i].point), false, Color.blue));
		}
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 20, graph.W, graph.H, graph),
		});
	}
	
}
