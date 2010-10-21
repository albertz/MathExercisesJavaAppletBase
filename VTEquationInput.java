package applets.Termumformungen$in$der$Technik_01_URI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class VTEquationInput extends VisualThing {
	
	abstract static class EquationPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		MathTextField textField = new MathTextField();
		JLabel infoLabel = new JLabel();
		JButton removeButton = new JButton();
		EquationSystem.Equation eq = new EquationSystem.Equation();
		EquationSystem baseEqSystem = null;
		static final int height = 30;
		boolean correct = false;
		
		abstract EquationSystem getBaseEqSystem();
		
		EquationPanel() {
			super();
			this.setLayout(null);
			baseEqSystem = getBaseEqSystem();
			textField.getDocument().addDocumentListener(new DocumentListener() {
				public void removeUpdate(DocumentEvent e) { updateEq(); }
				public void insertUpdate(DocumentEvent e) { updateEq(); }
				public void changedUpdate(DocumentEvent e) { updateEq(); }
			});
			removeButton.setText("-");
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { onRemoveClick(); }
			});
			posComponents();
			this.add(textField);
			this.add(infoLabel);
			this.add(removeButton);
		}

		void posComponents() {
			int infoLabelWidth = infoLabel.getFontMetrics(infoLabel.getFont()).stringWidth(infoLabel.getText());
			int infoLabelHeight = infoLabel.getFontMetrics(infoLabel.getFont()).getHeight();
			int removeButtonWidth = 30;
			textField.setBounds(0, 0, this.getWidth() - infoLabelWidth - 5 - removeButtonWidth - 5, height);
			infoLabel.setBounds(textField.getWidth() + 5, (height - infoLabelHeight) / 2, infoLabelWidth, infoLabelHeight);
			removeButton.setBounds(infoLabel.getX() + infoLabel.getWidth() + 5, 0, removeButtonWidth, removeButtonWidth);
			repaint();
		}
		
		void setInputError(String s) {
			correct = false;
			infoLabel.setForeground(Color.red.brighter());
			infoLabel.setText(s);
			posComponents();
		}
		
		void setInputWrong(String s) {
			correct = false;
			infoLabel.setForeground(Color.red);
			infoLabel.setText(s);
			posComponents();			
		}

		void setInputRight(String s) {
			correct = true;
			infoLabel.setForeground(Color.blue);
			infoLabel.setText(s);
			posComponents();			
		}

		void resetInput() { setInputWrong(""); }
		
		void updateEq() {
			try {
				eq = new EquationSystem.Equation(textField.getOperatorTree(), baseEqSystem.variableSymbols);
			} catch (EquationSystem.Equation.ParseError e) {
				setInputError("Eingabe: " + e.german);
				return;
			}
			onEquationUpdate();
		}
		
		abstract void onRemoveClick();
		abstract void onEquationUpdate();
	}
	
	abstract class EquationsPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		List<EquationPanel> equations = new LinkedList<EquationPanel>();
		JButton addNewEquationButton = new JButton();
		
		public EquationsPanel() { this(0); }
		public EquationsPanel(int startSize) {
			super();
			this.setLayout(null);
			this.setBorder(new LineBorder(Color.black, 1));
			addNewEquationButton.setText("+");
			addNewEquationButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { addEquation().requestFocus(); }
			});
			posComponents();
			this.add(addNewEquationButton);
			for(int i = 0; i < startSize; ++i)
				addEquation();
		}
		
		EquationSystem baseEqSysForNewEquation(EquationPanel eqp) { return eqSys; }
		
		EquationPanel addEquation() {
			EquationPanel eqp = new EquationPanel() {
				private static final long serialVersionUID = 1L;
				@Override void onEquationUpdate() { EquationsPanel.this.onEquationUpdate(this); }
				@Override void onRemoveClick() { removeEquation(this); }
				@Override EquationSystem getBaseEqSystem() { return baseEqSysForNewEquation(this); }
			};
			eqp.baseEqSystem = baseEqSysForNewEquation(eqp);
			equations.add(eqp);
			this.add(eqp, equations.size());
			VTEquationInput.this.posComponents();
			return eqp;
		}
		
		void removeEquation(EquationPanel eqp) {
			for(Iterator<EquationPanel> i = equations.iterator(); i.hasNext();) {
				if(i.next() == eqp) {
					i.remove();
					this.remove(eqp);
					VTEquationInput.this.posComponents();
					return;
				}
			}
			throw new AssertionError("equation panel not found");
		}
		
		abstract boolean recheck(EquationPanel eqp);
		
		Iterator<EquationPanel> getNextAfter(EquationPanel eqp) {			
			for(Iterator<EquationPanel> eqpIter = equations.iterator(); eqpIter.hasNext();)
				if(eqpIter.next() == eqp) return eqpIter;
			throw new AssertionError("equation panel not found");				
		}
		boolean recheckAllFrom(EquationPanel eqp) {
			Iterator<EquationPanel> eqpIter = getNextAfter(eqp);
			if(!recheck(eqp)) { resetRest(eqpIter); return false; }
			return recheckAll(eqpIter);
		}
		boolean recheckAll(Iterator<EquationPanel> eqpIter) { while(eqpIter.hasNext()) if(!recheck(eqpIter.next())) { resetRest(eqpIter); return false; } return true; }
		boolean recheckAll() { return recheckAll(equations.iterator()); }
		void resetRest(Iterator<EquationPanel> eqpIter) { while(eqpIter.hasNext()) eqpIter.next().resetInput(); }
		void resetAll() { resetRest(equations.iterator()); }
		
		void onEquationUpdate(EquationPanel eqp) { recheckAllFrom(eqp); }
		
		int height = 0;
		
		void posComponents() {
			int y = 1;
			for(EquationPanel eqp : equations) {
				eqp.setBounds(1, y, this.getWidth() - 2, EquationPanel.height);
				eqp.posComponents();
				y += eqp.getHeight() + 5;
			}
			addNewEquationButton.setBounds(1, y, 30, 30);
			height = y + addNewEquationButton.getHeight();
		}
		
		void clear() {
			for(Iterator<EquationPanel> i = equations.iterator(); i.hasNext();) {
				EquationPanel eqp = i.next();
				i.remove();
				this.remove(eqp);
			}			
			VTEquationInput.this.posComponents();
		}
	}
	
	class MainPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		class BasicEquationsPanel extends EquationsPanel {
			private static final long serialVersionUID = 1L;

			public BasicEquationsPanel(int i) { super(i); }

			@Override void onEquationUpdate(EquationPanel eqp) {
				if(!recheckAllFrom(eqp))
					followingEquationsPanel.resetAll();
				else
					followingEquationsPanel.recheckAll();
			}
			
			@Override boolean recheck(EquationPanel eqp) {
				if(eqp.baseEqSystem.contains(eqp.eq)) {
					eqp.setInputRight("ok");
					return true;
				} else {
					eqp.setInputWrong("kann nicht aus der Schaltung abgelesen werden");
					return false;
				}
			}
		}
		
		class FollowingEquationsPanel extends EquationsPanel {
			private static final long serialVersionUID = 1L;
			
			@Override EquationSystem baseEqSysForNewEquation(final EquationPanel eqp) {
				return eqSys.extendedSystem(
						Utils.map(
								Utils.cuttedFromRight(equations, new Utils.Predicate<EquationPanel>() {
									public boolean apply(EquationPanel obj) {
										return eqp == obj;
									}
								}),
								new Utils.Function<EquationPanel,EquationSystem.Equation>() {
									public EquationSystem.Equation eval(EquationPanel obj) { return obj.eq; }
								}));
			}
			
			@Override boolean recheck(EquationPanel eqp) {
				if(eqp.baseEqSystem.canConcludeTo(eqp.eq)) {
					eqp.setInputRight("ok");
					return true;
				} else {
					eqp.setInputWrong("kann nicht hergeleitet werden");
					return false;
				}
			}
		}

		BasicEquationsPanel basicEquationsPanel = new BasicEquationsPanel(1);
		FollowingEquationsPanel followingEquationsPanel = new FollowingEquationsPanel();
		
		void posComponents() {
			basicEquationsPanel.setSize(getWidth(), 0);
			basicEquationsPanel.posComponents();
			followingEquationsPanel.setSize(getWidth(), 0);
			followingEquationsPanel.posComponents();
			basicEquationsPanel.setBounds(0, 0, getWidth(), basicEquationsPanel.height);
			followingEquationsPanel.setBounds(0, basicEquationsPanel.height, width, followingEquationsPanel.height);
			setPreferredSize(new Dimension(getWidth(), basicEquationsPanel.height + followingEquationsPanel.height));
		}
				
		@Override public void setBounds(int x, int y, int width, int height) {
			super.setBounds(x, y, width, height);
			posComponents();
		}
		
		MainPanel() {
			super();
			this.setLayout(null);
			posComponents();
			this.add(basicEquationsPanel);
			this.add(followingEquationsPanel);
		}

		void clear() {
			basicEquationsPanel.clear();
			basicEquationsPanel.addEquation();
			followingEquationsPanel.clear();
		}
	}
	
	MainPanel mainPanel = null;
	String name;
	int stepX;
	int stepY;
	int width;
	int height;
	EquationSystem eqSys = null;
	
	VTEquationInput(String name, int stepX, int stepY, int width, int height) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.width = width;
		this.height = height;
	}
	
	void posComponents() {
		if(mainPanel != null) {
			mainPanel.posComponents();
			mainPanel.revalidate();
			height = mainPanel.getPreferredSize().height;
			mainPanel.setSize(width, height);
			Container c = mainPanel.getParent();
			while(c != null) {
				if(c instanceof Applet.ContentPane) {
					((Applet.ContentPane) c).revalidateVisualThings();
					break;
				}
				c = c.getParent();
			}
		}
	}
	
	@Override
	public Component getComponent() {
		if(mainPanel == null)
			mainPanel = new MainPanel();		
		mainPanel.setName(name);
		return mainPanel;
	}

	@Override public int getWidth() { return width; }
	@Override public int getHeight() { return height; }
	@Override public int getStepX() { return stepX; }
	@Override public int getStepY() { return stepY; }
	@Override public void setStepX(int v) { stepX = v; }
	@Override public void setStepY(int v) { stepY = v; }

	public void clear() {
		if(mainPanel != null)
			mainPanel.clear();
	}

}
