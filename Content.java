package applets.Komplexe$Zahlen_Polarkoord_MultiplikationAufg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Content {

	Applet applet;
	PGraph.Point z1Params = new PGraph.Point(Math.sqrt(2), Math.sqrt(2));
	PGraph.Point z2Params = new PGraph.Point(0, 1);
	PGraph.Point multParams = new PGraph.Point(-Math.sqrt(2), Math.sqrt(2));	
	PGraph.Point selection = new PGraph.Point(Math.sqrt(2), Math.sqrt(2));
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(436, 581);
	}

	void postinit() {
		next(0);
	}
	
	public void next(int index) {
		do {
			z1Params = randomChoiceFrom(graph.gridPolarPoints());
			z2Params = randomChoiceFrom(graph.gridPolarPoints());
		} while(z1Params.abs() + z2Params.abs() > 5);
		updateMult();
		showText();
		((JLabel) applet.getComponentByName("res1")).setText("");		
	}
	
	protected void updateMult() {
		multParams.x = z1Params.x * z2Params.x - z1Params.y * z2Params.y;
		multParams.y = z1Params.x * z2Params.y + z1Params.y * z2Params.x;	
	}
	
	protected void showText() {		
		double abs1 = z1Params.abs();
		double angle1 = Math.atan2(z1Params.y, z1Params.x) / Math.PI;
		double abs2 = z2Params.abs();
		double angle2 = Math.atan2(z2Params.y, z2Params.x) / Math.PI;		
		double absM = multParams.abs();
		double angleM = Math.atan2(multParams.y, multParams.x) / Math.PI;
		
		((JLabel) applet.getComponentByName("z1"))
		.setText(
				"z1 = " + Round(z1Params.x) + " + " + Round(z1Params.y) + "i" +
				" = " + Round(abs1) + "∙( cos(" + Round(angle1) + "π) + i∙sin(" + Round(angle1) + "π) )");

		((JLabel) applet.getComponentByName("z2"))
		.setText(
				"z2 = " + Round(z2Params.x) + " + " + Round(z2Params.y) + "i" +
				" = " + Round(abs2) + "∙( cos(" + Round(angle2) + "π) + i∙sin(" + Round(angle2) + "π) )");

		/*((JLabel) applet.getComponentByName("multz"))
		.setText(
				"z1 * z2 = " + Round(multParams.x) + " + " + Round(multParams.y) + "i" +
				" = " + Round(absM) + "∙( cos(" + Round(angleM) + "π) + i∙sin(" + Round(angleM) + "π) )");*/

		((JLabel) applet.getComponentByName("z1")).setForeground(Color.RED);
		((JLabel) applet.getComponentByName("z2")).setForeground(Color.RED);
		//((JLabel) applet.getComponentByName("multz")).setForeground(Color.BLUE);
		
		((JLabel) applet.getComponentByName("res1")).setText("");						
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
		// brauchen wir nur für next() für graph.gridPolarPoints()
		graph = new PGraph(applet, 400, 400);
		graph.x_l = -4;
		graph.x_r = 4;
		graph.y_o = 4;
		graph.y_u = -4;
		graph.showPolarcircles = true;
		
		//graph.dragablePoints.add(new PGraph.GraphPoint(z1Params, Color.RED, true, true));
		//graph.dragablePoints.add(new PGraph.GraphPoint(z2Params, Color.RED, true, true));
		graph.dragablePoints.add(new PGraph.GraphPoint(selection, Color.BLUE, true, true));
		
		graph.OnDragablePointMoved =
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						((JLabel) applet.getComponentByName("res1")).setText("");					
					}
				};
				
		Applet.CorrectCheck checker = new Applet.CorrectCheck() {

			public String getResultMsg() {
				return isCorrect() ? "das ist korrekt" : "das ist leider falsch";
			}

			public boolean isCorrect() {
				return selection.distance(multParams) < 0.1;
			}
			
		};
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTDummyObject("checker", 0, 0, checker),
				new VTImage("graph", 10, 5, graph.getW(), graph.getH(), graph)
		});	
	}
	
}
