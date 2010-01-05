package applets.AnalytischeGeometrieundLA_Ebene_StuetzNormRichtung;

import java.awt.Color;
import java.util.Collection;
import java.util.Random;

import applets.AnalytischeGeometrieundLA_Ebene_StuetzNormRichtung.PGraph3D.Vector3D;

public class Content {

	Applet applet;
	PGraph3D graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(436, 581);
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
		
	public void run() {
		graph = new PGraph3D(applet, 400, 400);		
		graph.addBaseAxes();
	
		PGraph3D.Vector3D normalUnnorminated = new PGraph3D.Vector3D(2,2,2);
		double length = 0;
		try {
			length = normalUnnorminated.abs().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PGraph3D.DynVector3D normal = normalUnnorminated.norminated();

		PGraph3D.MoveablePointOnLine stuetzPt = graph.new MoveablePointOnLine(new PGraph3D.Vector3D(4,4,4), Color.black);
		final PGraph3D.MoveablePointOnPlane normalPt = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(), Color.blue);
		stuetzPt.updater.add( new PGraph3D.Vector3DUpdater((Vector3D) normalPt.point, stuetzPt.dynPoint().sum(normalUnnorminated)) );
		normalPt.updater.add( new PGraph3D.Vector3DUpdater(normalUnnorminated, normalPt.dynPoint().diff(stuetzPt.dynPoint()).norminated().product(new PGraph3D.Float(length)) ) );
		normalPt.updater.add( new PGraph3D.Vector3DUpdater((Vector3D) normalPt.point, stuetzPt.dynPoint().sum(normalUnnorminated)) );

		//PGraph3D.MoveablePointOnPlane richtungPt = graph.new MoveablePointOnPlane(new PGraph3D.Vector3D(0,5,0), Color.blue);
		graph.objects.add(stuetzPt);
		graph.objects.add(normalPt);
		graph.objects.add(new PGraph3D.VectorArrow(stuetzPt.dynPoint(), normalPt.dynPoint().diff(stuetzPt.dynPoint()), Color.blue));
		//graph.objects.add(richtungPt);
				
		PGraph3D.Plane plane = new PGraph3D.Plane( stuetzPt.dynPoint().dotProduct(normal), normal, Color.red );
		graph.objects.add(plane);
		
		normalPt.plane = new PGraph3D.Plane( normalPt.dynPoint().dotProduct(normal), normal );
		stuetzPt.line = new PGraph3D.Line(stuetzPt.dynPoint(), normal);
		//richtungPt.plane = plane;
		
		graph.objects.add(plane.intersectionLine(PGraph3D.Plane.zPlane).setColor(Color.darkGray));
		
		/*
		PGraph3D.Plane plane = new PGraph3D.Plane(new PGraph3D.Float(5f), new PGraph3D.Vector3D(1,1,1), Color.red);
		graph.objects.add(plane);
		PGraph3D.Point pt = graph.new MoveablePointOnPlane(plane.basePoint(), plane, Color.blue);
		graph.objects.add(pt);
		PGraph3D.Line line = new PGraph3D.Line(pt.dynPoint(), null, Color.green);
		PGraph3D.Point pt2 = graph.new MoveablePointOnLine(new PGraph3D.Vector3D(5, 0, 0), line, Color.blue);
		line.vector = pt.dynPoint().diff( pt2.dynPoint() );
		graph.objects.add(line);
		graph.objects.add(pt2);
		*/
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.W, graph.H, graph)
		});	
	}
	
}
