package applets.Komplexe$Zahlen_Polarkoord_PolarZuEbene;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JTextField;


public class Content {

	Applet applet;
	PGraph.Point zParams = new PGraph.Point(Math.sqrt(2), Math.sqrt(2));
	PGraph.Point wantedPoint = new PGraph.Point(0,0);
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(297, 164);
	}

	void postinit() {
		next(0);
	}
	
	public void next(int index) {
		wantedPoint = randomChoiceFrom(graph.gridPolarPoints());
		showText();
		((JLabel) applet.getComponentByName("res1")).setText("");		
	}
	
	protected void showText() {						
		double abs = wantedPoint.abs();
		double angle = Math.atan2(wantedPoint.y, wantedPoint.x) * 180.0 / Math.PI;
		
		((JLabel) applet.getComponentByName("reqz"))
		.setText("z = " + Round(abs) + "∙( cos(" + Round(angle) + "°) + i∙sin(" + Round(angle) + "°) )");
		
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
	
	protected void updateZ() {
		try {
			zParams.x = new Double(((JTextField) applet.getComponentByName("s1")).getText()).doubleValue();
			zParams.y = new Double(((JTextField) applet.getComponentByName("s2")).getText()).doubleValue();
		}
		catch(Exception e) {}
	}
	
	public void run() {
		// brauchen wir nur für next() für graph.gridPolarPoints()
		graph = new PGraph(applet, 400, 400);
		graph.x_l = -4;
		graph.x_r = 4;
		graph.y_o = 4;
		graph.y_u = -4;
		
		Applet.CorrectCheck checker = new Applet.CorrectCheck() {

			public String getResultMsg() {
				return isCorrect() ? "das ist korrekt" : "das ist leider falsch";
			}

			public boolean isCorrect() {
				updateZ();
				return wantedPoint.distance(zParams) < 0.1;
			}
			
		};
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTDummyObject("checker", 0, 0, checker),
		});	
	}
	
}
