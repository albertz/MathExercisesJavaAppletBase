package applets.Komplexe$Zahlen_Polarkoord_AuswahlAufg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Random;
import javax.swing.JLabel;


public class Content {

	Applet applet;
	PGraph.Point zParams = new PGraph.Point(Math.sqrt(2), Math.sqrt(2));
	PGraph.Point wantedPoint = new PGraph.Point(0,0);
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(440, 610);
	}

	protected String Round(double x) {
		return "" + (Math.round(x * 10) / 10.0);
	}
	
	protected PGraph.Point randomChoiceFrom(Collection<PGraph.Point> points) {
		PGraph.Point[] ar = points.toArray(new PGraph.Point[0]);
		int i = new Random().nextInt(ar.length);
		return ar[i];
	}
	
	public void run() {
		graph = new PGraph(applet, 400, 400);
		graph.x_l = -4;
		graph.x_r = 4;
		graph.y_o = 4;
		graph.y_u = -4;
		graph.showPolarcircles = true;
		
		graph.dragablePoints.add(new PGraph.GraphPoint(zParams, Color.BLUE, true, true));
		
		graph.OnDragablePointMoved =
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {						
						((JLabel) applet.getComponentByName("z"))
						.setText("z = " + Round(zParams.x) + " + " + Round(zParams.y) + "i");
						((JLabel) applet.getComponentByName("z")).setForeground(Color.BLUE);

						double abs = wantedPoint.abs();
						double angle = Math.atan2(wantedPoint.y, wantedPoint.x) / Math.PI;
						
						((JLabel) applet.getComponentByName("reqz"))
						.setText("z = " + Round(abs) + "∙( cos(" + Round(angle) + "π) + i∙sin(" + Round(angle) + "π) )");
						
						((JLabel) applet.getComponentByName("res1")).setText("");						
					}
				};

		Applet.CorrectCheck checker = new Applet.CorrectCheck() {

			public String getResultMsg() {
				return isCorrect() ? "das ist korrekt" : "das ist leider falsch";
			}

			public boolean isCorrect() {
				return wantedPoint.transform(graph).distance(zParams.transform(graph)) < 0.1;
			}
			
		};
		
		// very bad hack (to use CorrectCheck as action)
		Applet.CorrectCheck next = new Applet.CorrectCheck() {
			public String getResultMsg() { return ""; }

			public boolean isCorrect() {
				do {
					wantedPoint = randomChoiceFrom(graph.gridPolarPoints());
				} while(wantedPoint.abs() > 4);
				graph.OnDragablePointMoved.actionPerformed(null);
				((JLabel) applet.getComponentByName("res1")).setText("");				
				return true;
			}
			
		};

		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.getW(), graph.getH(), graph),
				new VTDummyObject("checker", 0, 0, checker),
				new VTDummyObject("next", 0, 0, next)				
		});	
	}
	
	void postinit() {
		graph.OnDragablePointMoved.actionPerformed(null);		
	}
}
