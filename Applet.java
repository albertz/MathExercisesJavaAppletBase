package applets.M3_01_04;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import net.mumie.mathletfactory.action.handler.Affine2DMouseGridTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine2DMouseTranslateHandler;
import net.mumie.mathletfactory.appletskeleton.g2d.SingleG2DCanvasApplet;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.algebra.MMRelation;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.mmobject.set.MMSet;
import net.mumie.mathletfactory.mmobject.set.MMSetDefByRel;
import net.mumie.mathletfactory.mmobject.util.MMString;

public class Applet extends SingleG2DCanvasApplet {

	private void initDispProp(MMSet set, Color c, double trans) {
		set.getDisplayProperties().setBorderWidth(8);
		set.getDisplayProperties().setBorderColor(c);
		set.getDisplayProperties().setTransparency(trans);
	}
	
	public void init() {
		super.init();
		
		// 3 Kreise hinzufügen
		
		MMRelation rel1 = new MMRelation(MDouble.class, "0.2 > x*x+y*y", 0);
		MMRelation rel2 = new MMRelation(MDouble.class, "0.1 > x*x-x+0.25+y*y", 0);
		MMRelation rel3 = new MMRelation(MDouble.class, "0.2 > x*x-x+0.25+y*y+y+0.25", 0);
		
		MMSetDefByRel set1 = new MMSetDefByRel(rel1);
		initDispProp(set1, Color.BLUE, 0.6);
		getCanvas().addObject(set1);
		
		MMSetDefByRel set2 = new MMSetDefByRel(rel2);
		initDispProp(set2, Color.RED, 0.6);
		getCanvas().addObject(set2);

		MMSetDefByRel set3 = new MMSetDefByRel(rel3);
		initDispProp(set3, Color.GREEN, 0.6);
		getCanvas().addObject(set3);

		
		// Vereinigungen hinzufügen
		
		MMRelation tmprel;
		MMSetDefByRel tmpset;
		
		// set1 / set2
		tmprel = new MMRelation(rel1);
		tmprel.addAndComposite(new MMRelation(rel2).negated());
		tmpset = new MMSetDefByRel(tmprel);

		getCanvas().addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				System.out.print("mouse: ");
				System.out.print(e.getX());
				System.out.print(",");
				System.out.println(e.getY());
			}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				System.out.print("mouse: ");
				System.out.print(e.getX());
				System.out.print(",");
				System.out.println(e.getY());
			}
			public void mouseReleased(MouseEvent e) {}
		});
		
		
		initDispProp(tmpset, Color.ORANGE, 0.5);
		getCanvas().addObject(tmpset);
		
		getCanvas().addObject(new MMCoordinateSystem());
		
		set1.setLabel("M_1");
		getControlPanel().add(set1.getAsContainerContent());
		getControlPanel().insertLineBreak();
		set2.setLabel("M_2");
		getControlPanel().add(set2.getAsContainerContent());
		getControlPanel().insertLineBreak();
		set3.setLabel("M_3");
		getControlPanel().add(set3.getAsContainerContent());
		
	}
	
	
}
