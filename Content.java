package applets.AnalytischeGeometrieundLA_06_GeradenSchnittpunktWindschief;

import java.awt.Color;
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
		applet.setSize(520, 490);
	}

	void postinit() { updater.doFrameUpdate(null); }
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
	
	public void run() {
		graph = new PGraph3D(applet, 480, 400);		
		graph.addBaseAxes();
		
		final PGraph3D.MoveablePointDynamic pt1 = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(0,2,0), Color.blue);
		final PGraph3D.MoveablePointDynamic pt2 = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(2,0,0), Color.blue);
		final PGraph3D.MoveablePointDynamic pt1v = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(4,2,4), Color.red);
		final PGraph3D.MoveablePointDynamic pt2v = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(2,4,4), Color.red);		
		pt1.snapGrid = 1;
		pt2.snapGrid = 1;
		pt1v.snapGrid = 1;
		pt2v.snapGrid = 1;
		final PGraph3D.Line line1 = new PGraph3D.Line(pt1.point, pt1v.point.diff(pt1.point), Color.green);  
		final PGraph3D.Line line2 = new PGraph3D.Line(pt2.point, pt2v.point.diff(pt2.point), Color.green);
		final PGraph3D.Point intersectionPoint = line1.intersectionPoint(line2);
		
		updater = new PGraph3D.FrameUpdate() {
			public void doFrameUpdate(PGraph3D.Vector3D diff) {
				((JLabel) applet.getComponentByName("line1")).setText("g1(t) = " + pt1.point.toString() + " + " + line1.vector.toString() + " ∙ t");
				((JLabel) applet.getComponentByName("line2")).setText("g2(t) = " + pt2.point.toString() + " + " + line2.vector.toString() + " ∙ t");
				
				if(intersectionPoint.point.isValid())
					((JLabel) applet.getComponentByName("schnitt")).setText("Schnittpunkt: " + intersectionPoint.point.toString());
				else if(line1.lineIsEqual(line2))
					((JLabel) applet.getComponentByName("schnitt")).setText("Gerade liegen übereinander");
				else
					((JLabel) applet.getComponentByName("schnitt")).setText("kein Schnittpunkt, Geraden liegen windschief");
			}
		};
		pt1.updater.add(updater);
		pt2.updater.add(updater);
		pt1v.updater.add(updater);
		pt2v.updater.add(updater);
		
		graph.objects.add(pt1);
		graph.objects.add(pt2);
		graph.objects.add(pt1v);
		graph.objects.add(pt2v);
		
		graph.objects.add(new PGraph3D.VectorArrow(line1.point, line1.vector, Color.red));
		graph.objects.add(new PGraph3D.VectorArrow(line2.point, line2.vector, Color.red));		
		graph.objects.add(line1);
		graph.objects.add(line2);		
		graph.objects.add(intersectionPoint);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.W, graph.H, graph),
		});
	}
	
}
