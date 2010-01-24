package applets.AnalytischeGeometrieundLA_11_MatrixMultiplikation;

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
		applet.setSize(347, 265);
	}

	void postinit() { updateLabel(); /* updater.doFrameUpdate(null); */ }
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

	VisualThing[][] labels = null;
	JLabel mouseOverLabel = null;
	
	static private String subscript(int n) {
		if(n >= 10) return subscript(n/10) + subscript(n%10);
		return "₀₁₂₃₄₅₆₇₈₉".substring(n, n+1);
	}
	
	void updateLabel() {
		if(mouseOverLabel != null) {
			mouseOverLabel.setOpaque(false);
			mouseOverLabel.repaint();
			mouseOverLabel = null;
		}
			
		for(int i = 0; i < labels.length; ++i)
			for(int j = 0; j < labels[0].length; ++j)
				if(labels[i][j].getComponent().getMousePosition() != null) {
					mouseOverLabel = (JLabel) labels[i][j].getComponent();
					mouseOverLabel.setOpaque(true);
					mouseOverLabel.setBackground(Color.green);

					((JLabel) applet.getComponentByName("m")).setText("Zeile " + (i+1) + ", Spalte " + (j+1) + ",   A" + subscript(i+1) + "," + subscript(j+1));
					return;
				}
		
		((JLabel) applet.getComponentByName("m")).setText("Fahre mit der Maus über ein Element der Matrix A");
	}
	
	public void run() {
		/*
		graph = new PGraph3D(applet, 480, 400);		
		graph.addBaseAxes();
		
		final PGraph3D.MoveablePointDynamic pt1 = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(2,2,0), Color.blue);
		final PGraph3D.MoveablePointDynamic pt2 = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(0,-1,2), Color.blue);
		final PGraph3D.MoveablePointDynamic pt1v = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(3,2,0), Color.red);
		final PGraph3D.MoveablePointDynamic pt2v = graph.new MoveablePointDynamic(new PGraph3D.Vector3D(0,0,2), Color.red);		
		pt1.snapGrid = 1;
		pt2.snapGrid = 1;
		pt1v.snapGrid = 1;
		pt2v.snapGrid = 1;
		final PGraph3D.Vector3D plane1Normal = pt1v.point.diff(pt1.point).fixed();
		final PGraph3D.Vector3D plane2Normal = pt2v.point.diff(pt2.point).fixed();
		final PGraph3D.Plane plane1 = new PGraph3D.Plane(pt1.point, plane1Normal).setColor(Color.red);
		final PGraph3D.Plane plane2 = new PGraph3D.Plane(pt2.point, plane2Normal).setColor(Color.red);
		final PGraph3D.Line intersectionLine = plane1.intersectionLine(plane2).setColor(Color.blue);
		
		updater = new PGraph3D.FrameUpdate() {
			public void doFrameUpdate(PGraph3D.Vector3D diff) {
				((JLabel) applet.getComponentByName("plane1")).setText("Ebene1(x,y,z): " + plane1.toString());
				((JLabel) applet.getComponentByName("plane2")).setText("Ebene2(x,y,z): " + plane2.toString());
				
				if(intersectionLine.point.isValid())
					((JLabel) applet.getComponentByName("schnitt")).setText("Schnittgerade: " + intersectionLine.toString("g"));
				else if(plane1.isPlaneEqual(plane2))
					((JLabel) applet.getComponentByName("schnitt")).setText("Ebenen liegen aufeinander");
				else
					((JLabel) applet.getComponentByName("schnitt")).setText("keine Schnittgerade");
			}
		};
		pt1.updater.add(updater);
		pt2.updater.add(updater);
		pt1v.updater.add(updater);
		pt2v.updater.add(updater);
		pt1.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D)pt1v.point, pt1.point.sum(plane1Normal)));
		pt1v.updater.add(new PGraph3D.Vector3DUpdater(plane1Normal, pt1v.point.diff(pt1.point)).setAllowZero(false));
		pt2.updater.add(new PGraph3D.Vector3DUpdater((PGraph3D.Vector3D)pt2v.point, pt2.point.sum(plane2Normal)));
		pt2v.updater.add(new PGraph3D.Vector3DUpdater(plane2Normal, pt2v.point.diff(pt2.point)).setAllowZero(false));
		
		graph.objects.add(pt1);
		graph.objects.add(pt2);
		graph.objects.add(pt1v);
		graph.objects.add(pt2v);
		
		graph.objects.add(new PGraph3D.VectorArrow(pt1.point, plane1.normal, Color.red));
		graph.objects.add(new PGraph3D.VectorArrow(pt2.point, plane2.normal, Color.red));		
		graph.objects.add(plane1);
		graph.objects.add(plane2);		
		graph.objects.add(intersectionLine);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.W, graph.H, graph),
		});
		*/

		int lineCount = 10;
		int rowCount = 6;
		labels = new VisualThing[lineCount][];
		int c = 1;
		for(int i = 0; i < lineCount; ++i) {
			labels[i] = new VisualThing[rowCount];
			for(int j = 0; j < rowCount; ++j)
				labels[i][j] = new VTLabel("" + c++, 0, 0);
		}
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTMatrix("matrix", 0, 0, labels),
		});
		
		applet.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				updateLabel();
			}
			
			public void mouseDragged(MouseEvent e) {}
		});
	}
	
}
