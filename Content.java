package applets.Trigonometrie_SinCos_variable;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JLabel;

import applets.Trigonometrie_SinCos_variable.PGraph.Function2D;





public class Content {

	Applet applet;
	PGraph.Point sinParams = new PGraph.Point(Math.PI/2, 1);
	PGraph.Point cosParams = new PGraph.Point(0, 1);
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(700, 454);
	}

	protected String Round(double x) {
		return "" + (Math.round(x * 10) / 10.0);
	}
	
	public void run() {
		graph = new PGraph(applet, 660, 300);
		graph.axeXStep = Math.PI/2;
		graph.axeXTextStep = 4;
		graph.axeXMult = 0.25 * 2;
		graph.axeXPostText = "π";
		
		graph.functions.add(
				new Function2D() {
					public double get(double x) {
						return sinParams.y * Math.sin(x - sinParams.x + Math.PI/2);
					}});
		graph.functionColors.add(Color.BLUE);
		
		graph.functions.add(
				new Function2D() {
					public double get(double x) {
						return cosParams.y * Math.cos(x - cosParams.x);
					}});
		graph.functionColors.add(Color.GREEN);
		
		graph.dragablePoints.add(sinParams);
		graph.dragablePoints.add(cosParams);
		graph.OnDragablePointMoved =
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {						   
						((JLabel) applet.getComponentByName("f1"))
						.setText("f(x) = " + Round(sinParams.y) + "∙sin(x + " + Round(- (sinParams.x - Math.PI/2) / Math.PI) + "π)");
						((JLabel) applet.getComponentByName("f2"))
						.setText("g(x) = " + Round(cosParams.y) + "∙cos(x + " + Round(- (cosParams.x - Math.PI/2) / Math.PI) + "π)");
						((JLabel) applet.getComponentByName("f1")).setForeground(Color.BLUE);
						((JLabel) applet.getComponentByName("f2")).setForeground(Color.GREEN);
					}
				};
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.getW(), graph.getH(), graph)
		});	
	}
	
	void postinit() {
		graph.OnDragablePointMoved.actionPerformed(null);		
	}
}
