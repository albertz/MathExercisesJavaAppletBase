package applets.Komplexe$Zahlen_Polarkoord_Multiplikation;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JLabel;



public class Content {

	Applet applet;
	PGraph.Point zParams = new PGraph.Point(1, 1);
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(440, 530);
	}

	protected String Round(double x) {
		return "" + (Math.round(x * 10) / 10.0);
	}
	
	public void run() {
		graph = new PGraph(applet, 400, 400);
		graph.x_l = -4;
		graph.x_r = 4;
		graph.y_o = 4;
		graph.y_u = -4;
		graph.showPolarcircles = true;
		
		graph.dragablePoints.add(new PGraph.GraphPoint(zParams, Color.RED, true, true));
		
		graph.OnDragablePointMoved =
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						double abs = zParams.abs();
						double angle = Math.atan2(zParams.y, zParams.x) / Math.PI;
						
						((JLabel) applet.getComponentByName("z"))
						.setText("z = " + Round(zParams.x) + " + " + Round(zParams.y) + "i");
						((JLabel) applet.getComponentByName("polarz"))
						.setText("z = " + Round(abs) + "∙( cos(" + Round(angle) + "π) + i∙sin(" + Round(angle) + "π) )");

						((JLabel) applet.getComponentByName("z")).setForeground(Color.RED);
						((JLabel) applet.getComponentByName("polarz")).setForeground(Color.RED);
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
