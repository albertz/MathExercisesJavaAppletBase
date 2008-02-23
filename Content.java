package applets.Abbildungen_I03_Abbildungen;

import java.awt.Color;
import java.awt.List;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JTextField;


public class Content {

	Applet applet;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(509, 434);		
	}

	public static boolean isSchonDrin(Collection col, String abb) {
		for(Iterator it = col.iterator(); it.hasNext(); ) {
			String a = (String) it.next();
			if(a.compareTo(abb) == 0) return true;
		}
		return false;
	}
	
	public Collection alleAbbildungen() {
		JTextField txt = (JTextField) applet.getComponentByName("s1");
		return alleAbbildungen(txt.getText());
	}
	
	public Collection alleAbbildungen(String txt) {
		JLabel parseMsg = (JLabel) applet.getComponentByName("res_parse");
		parseMsg.setText("");
		parseMsg.setForeground(Color.gray);
		
		LinkedList abbs = new LinkedList();		
		String[] items = txt.split(",");
		
		for(int i = 0; i < items.length; i++) {
			String item = items[i].trim().toLowerCase();
			if(item.length() == 3) {
				boolean ok = true;
				for(int j = 0; j < 3; j++)
					if(item.charAt(j) != 'x' && item.charAt(j) != 'y') {
						parseMsg.setText(item + " ist ungültig, an " + (j+1) + ". Stelle ist ein " + item.charAt(j));
						ok = false; break;
					}
				if(!ok) continue;
				if(isSchonDrin(abbs, item)) {
					parseMsg.setText(item + " ist doppelt angegeben");
				} else
					abbs.add(item);		
			}
			else if(item.length() != 0) {
				parseMsg.setText(item + " ist nicht 3 Zeichen lang");
			}
		}
		
		return abbs;
	}
	
	public void run() {
/*		PZuweisung zuw1 = new PZuweisung(applet);
		zuw1.dotsCountA = 4;
		zuw1.dotsCountB = 3;
		//zuw1.dotBNames = Arrays.asList(new String[] {"a","b","c","d","e","f"});
		zuw1.reset();
		
		PZuweisung zuw2 = new PZuweisung(applet) {

			public boolean isCorrect() {
				if(!existsConnectionForAllDotsA() || !copySrc.existsConnectionForAllDotsA())
					return false;
				
				return !connectionsAreEqual(copySrc);
			}
			
			public String getResultMsg() {
				if(!copySrc.existsConnectionForAllDotsA())
					return "f ist nicht vollständig definiert";
				if(!existsConnectionForAllDotsA())
					return "g ist nicht vollständig definiert";
				
				if(connectionsAreEqual(copySrc))
					return "f ist gleich g";

				return "das ist korrekt, f ist ungleich g";
			}
		};
		zuw2.copyFrom(zuw1, false);
*/		

		applet.vtmeta.updater = new Runnable() {
			public void run() {
				applet.updater.run(); // run old method
				JLabel labelNum = (JLabel) applet.getComponentByName("num");
				labelNum.setText( "" + alleAbbildungen().size() );
			}		
		};
		
		Applet.CorrectCheck checker = new Applet.CorrectCheck() {

			public String getResultMsg() {
				return isCorrect() ? "das ist korrekt" : "das sind alle möglichen Abbildungen";
			}

			public boolean isCorrect() {
				// richtige Lösung z.B.: xxx, xxy, xyx, xyy, yxx, yxy, yyx, yyy
				return alleAbbildungen().size() == 8;
			}
			
		};
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTDummyObject("checker", 0, 0, checker)
//				new VTImage("zuw1", 10, 5, 600, 200, zuw1),
//				new VTImage("zuw2", 10, 5, 600, 200, zuw2)
		});
		
		
		
	}
}
