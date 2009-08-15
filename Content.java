package applets.Komplexe$Zahlen_Polarkoordinaten;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JLabel;



public class Content {

	Applet applet;
	PGraph.Point zParams = new PGraph.Point(1, 1);
	PGraph.Point conjzParams = new PGraph.Point(1, -1);
	PGraph.Point abszParams = new PGraph.Point(Math.sqrt(2), 0);	
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(440, 550);
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
		
		graph.dragablePoints.add(new PGraph.GraphPoint(zParams, Color.RED, true, true));
		graph.dragablePoints.add(new PGraph.GraphPoint(conjzParams, Color.BLUE, true, false));		
		graph.dragablePoints.add(new PGraph.GraphPoint(abszParams, Color.BLACK, true, false));
		
		graph.OnDragablePointMoved =
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						conjzParams.x = zParams.x;
						conjzParams.y = -zParams.y;
						abszParams.x = zParams.abs();
						
						((JLabel) applet.getComponentByName("z"))
						.setText("z = " + Round(zParams.x) + " + " + Round(zParams.y) + "i");
						((JLabel) applet.getComponentByName("conjz"))
						.setText("conj z = " + Round(conjzParams.x) + " + " + Round(conjzParams.y) + "i");
						((JLabel) applet.getComponentByName("absz"))
						.setText("|z| = " + Round(abszParams.x));

						((JLabel) applet.getComponentByName("z")).setForeground(Color.RED);
						((JLabel) applet.getComponentByName("conjz")).setForeground(Color.BLUE);
						((JLabel) applet.getComponentByName("absz")).setForeground(Color.BLACK);
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
