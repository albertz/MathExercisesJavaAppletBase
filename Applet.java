package applets.Abbildungen_I62_UrbildTeilmengen;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComboBox;

public class Applet extends JApplet {

	private JPanel jContentPane = null;

	/**
	 * This is the xxx default constructor
	 */
	public Applet() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		this.setSize(477, 568);
		this.setContentPane(getJContentPane());
	}

	public static VisualThing newVTLimes(int stepX, int stepY, String var, String c) {
		return new VTFrac(stepX, stepY,
				new VTLabel("lim", 0, 0, "Courier"),
				new VTLabel(var + " → " + c, 0, 0, "Courier"),
				false);
	}

	public static VisualThing newVTLimes(int stepX, int stepY, VisualThing sub) {
		return new VTFrac(stepX, stepY,
				new VTLabel("lim", 0, 0, "Courier"), sub, false);
	}
	
	public static VisualThing newVTLimes(int stepX, int stepY, VisualThing var, VisualThing c) {
		return new VTFrac(stepX, stepY,
				new VTLabel("lim", 0, 0, "Courier"),
				new VTContainer(0, 0, new VisualThing[] {
						var, new VTLabel("→", 0, 0, "Courier"), c
				}),
				false);
	}

	/**
	 * fügt alle Dinge zum panel hinzu; siehe VisualThing für weitere Details
	 */
	public static Point addVisualThings(JPanel panel, VisualThing[] things) {
		return addVisualThings(panel, things, false);
	}

	public static Point addVisualThings(JPanel panel, VisualThing[] things, boolean onlyCalcSize) {
		int curX = 0, curY = 0;
		List xs_old = null;
		List xs = new LinkedList();
		Point max = new Point(0, 0);

		for (int i = 0; i < things.length; i++) {
			if (things[i].getStepY() != 0) {
				curY = max.y + things[i].getStepY();
				xs_old = xs;
				xs = new LinkedList();
				curX = 0;
			}
			if (things[i].getStepX() < 0) {
				curX = ((Integer) (xs_old.get(-things[i].getStepX() - 1)))
						.intValue();
			} else
				curX += things[i].getStepX();
			xs.add(new Integer(curX));

			if(!onlyCalcSize) {
				Component c = things[i].getComponent();
				if(c != null) {
					c.setBounds(new Rectangle(curX, curY, things[i].getWidth(),
							things[i].getHeight()));
					panel.add(c);
				}
			}
			max.x = Math.max(max.x, curX + things[i].getWidth());
			max.y = Math.max(max.y, curY + things[i].getHeight());

			curX += things[i].getWidth();
		}

		return max;
	}

	public static interface ComponentWalker {
		// false is a signal to break
		public boolean meet(Component comp);
	}

	public static boolean ForEachComponent(JPanel panel, ComponentWalker walker) {
		for (int i = 0; i < panel.getComponents().length; i++) {
			walker.meet(panel.getComponents()[i]);
			if (panel.getComponents()[i] instanceof JPanel)
				if (!ForEachComponent((JPanel) panel.getComponents()[i], walker))
					return false;
		}
		return true;
	}

	public boolean ForEachComponent(ComponentWalker walker) {
		return ForEachComponent(getJContentPane(), walker);
	}

	/**
	 * durchsucht alle Komponenten und gibt die erste zurück, deren Namen passt;
	 * ansonsten null
	 */
	public Component getComponentByName(final String name) {
		class CWalker implements ComponentWalker {
			public Component comp;

			public boolean meet(Component comp) {
				String cname = comp.getName();
				if (cname != null && cname.compareTo(name) == 0) {
					this.comp = comp;
					return false;
				}
				return true;
			}
		}
		CWalker walker = new CWalker();
		ForEachComponent(walker);
		return walker.comp;
	}

	private Runnable createCheckButtonListener(final int startIndex,
			final Runnable correctAction, final Runnable wrongAction) {
		return new Runnable() {
			public void run() {
				boolean correct = true;
				for (int i = startIndex;; i++) {
					JComponent comp = (JComponent) getComponentByName("s" + i);
					if (comp == null)
						break;
					String selected = comp instanceof JComboBox ? (String) ((JComboBox) comp)
							.getSelectedItem()
							: ((JTextField) comp).getText();
					correct = isCorrect(i, selected);
					if (!correct)
						break;
				}
				setResultLabel(startIndex, correct);
				if (correct) {
					if (correctAction != null)
						correctAction.run();
				} else {
					if (wrongAction != null)
						wrongAction.run();
				}
			}
		};
	}

	private void setResultLabel(int index, boolean correct) {
		if (correct) {
			((JLabel) getComponentByName("res" + index))
					.setForeground(Color.MAGENTA);
			((JLabel) getComponentByName("res" + index))
					.setText("alles ist richtig!");
		} else {
			((JLabel) getComponentByName("res" + index))
					.setForeground(Color.RED);
			((JLabel) getComponentByName("res" + index))
					.setText("leider ist etwas falsch");
		}
	}

	Runnable createCheckButtonListener(int startIndex) {
		return createCheckButtonListener(startIndex, null, null);
	}

	Runnable createHelpButtonListener(final int startIndex) {
		return new Runnable() {
			public void run() {
				for (int i = startIndex;; i++) {
					JComponent comp = (JComponent) getComponentByName("s" + i);
					if (comp == null)
						break;
					String selected = comp instanceof JComboBox ? (String) ((JComboBox) comp)
							.getSelectedItem()
							: ((JTextField) comp).getText();
					boolean correct = isCorrect(i, selected);
					comp.setBackground(correct ? Color.MAGENTA : Color.RED);
				}
			}
		};
	}

	private Runnable createVisibler(final String name) {
		return new Runnable() {
			public void run() {
				JComponent comp = (JComponent) getComponentByName(name);
				if (comp == null)
					return;
				comp.setVisible(true);
			}
		};
	}

	public static String getMultipliedString(String str, int n) {
		String res = "";
		for (int i = 0; i < n; i++)
			res += str;
		return res;
	}

	public void removeAllVisualThings(JPanel panel) {
		panel.removeAll();
	}

	private int aufgabeNr = 0;
	VTMeta vtmeta = new VTMeta(this, 0, 0, "", null, new Runnable() {
				public void run() {
					resetSelectorColors();
					resetResultLabels();
				}
			});
	
	public String getPackageName() {
		return this.getClass().getPackage().getName();
	}

	public String getPackageNameBase() {
		String name = getPackageName().substring("applets.".length());
		int i = name.indexOf("_");
		return name.substring(0, i);
	}

	public String getPackageNameShort() {
		String name = getPackageName();
		int i = name.indexOf("_");
		return name.substring(i + 1);
	}
	
	public String getPackageAsPath() {
		return getPackageName().replace(".", "/");
	}
	
	private void updateDefaultVisualThings() {
		removeAllVisualThings(jContentPane);

		Runnable updater = new Runnable() {
			public void run() {
				resetSelectorColors();
				resetResultLabels();
			}
		};
			
		/* Copy&Paste Bereich für häufig genutzte Zeichen:
		 * → ↦ ∞ ∈ ℝ π ℤ ℕ
		 * ≤ ⇒ ∉ ∅ ⊆ ∩ ∪
		 * ∙ × ÷ ± — ≠
		 * “ ” θ
		 */
			
		String content = "";
		InputStream res = getClass().getClassLoader().getResourceAsStream("content.vtmeta");
		InputStreamReader file = null;
		if(res != null) file = new InputStreamReader(res);
		if(file == null) {
			try {
				file = new FileReader("content.vtmeta");
			} catch (FileNotFoundException e) {
			}
		}		
		if(file == null) {
			try {
				String path = "../Lehreinheiten/" + getPackageNameBase() + "/Code/" + getPackageAsPath(); 
				file = new FileReader(path + "/content.vtmeta");
			} catch (FileNotFoundException e) {
				System.err.println("ERROR: FileNotFound: $workspace/content.vtmeta");
			}
		}		
		if(file == null)
			System.err.println("ERROR: FileNotFound: content.vtmeta");
		else {
			try {
				int c;
				while(-1 != (c = file.read())) {
					content += (char)c;
				}
			}
			catch (IOException e) {
				System.err.println("ERROR: IO: content.vtmeta");
			}
			finally {
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		addVisualThings(jContentPane, vtmeta.getThingsByContentStr(content));

		resetResultLabels();
		resetSelectorColors();
		
		jContentPane.repaint();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			updateDefaultVisualThings();
		}
		return jContentPane;
	}

	public static double parseNum(String txt) {
		try {
			txt = txt.replace(",", ".");
			txt = txt.replace(" ", "");
			return Double.parseDouble(txt);
		} catch (NumberFormatException e) {
			return -666;
		}
	}
	
	public String getSelected(int selId) {
		Component comp = getComponentByName("s" + selId);
		if(comp == null) return null;
		if(comp instanceof JComboBox)
			return (String) ((JComboBox)comp).getSelectedItem();
		if(comp instanceof JTextField)
			return ((JTextField)comp).getText();
		return null;
	}	
	
	public boolean isCorrect(int selId, String selected) {
		Var var = vtmeta.getVar("s" + selId);
		if(var != null) return var.value.compareToIgnoreCase(selected) == 0;

		switch(selId) {
		default: return false;
		}
	}

	public void resetResultLabels() {
		ForEachComponent(new ComponentWalker() {
			public boolean meet(Component comp) {
				if (comp.getName() != null && comp.getName().startsWith("res")) {
					((JLabel) comp).setText("");
				}
				return true;
			}
		});
	}

	public void resetSelectorColors() {
		ForEachComponent(new ComponentWalker() {
			public boolean meet(Component comp) {
				if (comp instanceof JComboBox || comp instanceof JTextField) {
					comp.setBackground(Color.WHITE);
				}
				return true;
			}
		});
	}

} // @jve:decl-index=0:visual-constraint="10,10"
