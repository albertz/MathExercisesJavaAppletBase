package applets.Komplexe$Zahlen_Polarkoord_Potenzen;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JTextField;


public class Content {

	Applet applet;
	PGraph.Point z1Params = new PGraph.Point(Math.sqrt(2)/2, Math.sqrt(2)/2);
	//PGraph.Point z2Params = new PGraph.Point(0, 1);
	PGraph.Point multParams = new PGraph.Point(-Math.sqrt(2), Math.sqrt(2));
	int n = 2;
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
	
	static protected void mult(PGraph.Point z1Params, PGraph.Point z2Params) {
		PGraph.Point multParams = new PGraph.Point();
		multParams.x = z1Params.x * z2Params.x - z1Params.y * z2Params.y;
		multParams.y = z1Params.x * z2Params.y + z1Params.y * z2Params.x;
		z1Params.x = multParams.x;
		z1Params.y = multParams.y;
	}
	
	protected void updateMult() {
		multParams.x = 1; multParams.y = 0;
		for(int i = 0; i < n; ++i)
			mult(multParams, z1Params);
	}
	
	public void run() {
		graph = new PGraph(applet, 400, 400);
		graph.x_l = -4;
		graph.x_r = 4;
		graph.y_o = 4;
		graph.y_u = -4;
		graph.showPolarcircles = true;
		
		graph.dragablePoints.add(new PGraph.GraphPoint(z1Params, Color.RED, true, true));
		//graph.dragablePoints.add(new PGraph.GraphPoint(z2Params, Color.RED, true, true));
		graph.dragablePoints.add(new PGraph.GraphPoint(multParams, Color.BLUE, true, false));
		
		graph.OnDragablePointMoved =
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						updateMult();
						
						double abs1 = z1Params.abs();
						double angle1 = Math.atan2(z1Params.y, z1Params.x) / Math.PI;
						double absM = multParams.abs();
						double angleM = Math.atan2(multParams.y, multParams.x) / Math.PI;
						
						((JLabel) applet.getComponentByName("z"))
						.setText(
								"z = " + Round(z1Params.x) + " + " + Round(z1Params.y) + "i" +
								" = " + Round(abs1) + "∙( cos(" + Round(angle1) + "π) + i∙sin(" + Round(angle1) + "π) )");

						/*((JLabel) applet.getComponentByName("z2"))
						.setText(
								"z2 = " + Round(z2Params.x) + " + " + Round(z2Params.y) + "i" +
								" = " + Round(abs2) + "∙( cos(" + Round(angle2) + "π) + i∙sin(" + Round(angle2) + "π) )");*/

						((JLabel) applet.getComponentByName("potz"))
						.setText(
								"z ^ " + n + " = " + Round(multParams.x) + " + " + Round(multParams.y) + "i" +
								" = " + Round(absM) + "∙( cos(" + Round(angleM) + "π) + i∙sin(" + Round(angleM) + "π) )");

						((JLabel) applet.getComponentByName("z")).setForeground(Color.RED);
						//((JLabel) applet.getComponentByName("z2")).setForeground(Color.RED);
						((JLabel) applet.getComponentByName("potz")).setForeground(Color.BLUE);
					}
				};
		
		// very bad hack (to use CorrectCheck as action)
		Applet.CorrectCheck next = new Applet.CorrectCheck() {
			public String getResultMsg() { return ""; }

			public boolean isCorrect() {
				graph.OnDragablePointMoved.actionPerformed(null);
				String ns = ((JTextField) applet.getComponentByName("s1")).getText();
				try {
					n = new Integer(ns).intValue();
				}
				catch(Exception e) {}
				((JTextField) applet.getComponentByName("s1")).setText("" + n);
				updateMult();
				applet.repaint();
				return true;
			}
			
		};

		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 5, graph.getW(), graph.getH(), graph),
				new VTDummyObject("next", 0, 0, next)				
		});	
	}
	
	void postinit() {
		((JTextField) applet.getComponentByName("s1")).setText("" + n);
		graph.OnDragablePointMoved.actionPerformed(null);		
	}
}
