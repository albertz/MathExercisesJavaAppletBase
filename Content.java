package applets.Trigonometrie_SinCos_variable;

import java.awt.Point;
import java.util.Arrays;

import applets.Trigonometrie_SinCos_variable.PGraph.Function2D;





public class Content {

	Applet applet;
	PGraph.Point sinParams = new PGraph.Point(0, 1);
	PGraph.Point cosParams = new PGraph.Point(0, 1);
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(700, 454);
	}

	public void run() {
		PGraph graph = new PGraph(applet, 660, 300);
		graph.functions.add(
			new Function2D() {
				public double get(double x) {
					return sinParams.y * Math.sin(x + sinParams.x);
				}});

		graph.functions.add(
				new Function2D() {
					public double get(double x) {
						return cosParams.y * Math.cos(x + cosParams.x);
					}});
		
		graph.dragablePoints.add(sinParams);
		graph.dragablePoints.add(cosParams);
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.getW(), graph.getH(), graph)
		});
		
	}
}
