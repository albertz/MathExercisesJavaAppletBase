package applets.Termumformungen$in$der$Technik_01_URI;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import applets.Termumformungen$in$der$Technik_01_URI.EquationSystem.Equation;

public class VTEquationInput extends VisualThing {
	
	abstract static class EquationPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		MathTextField textField = new MathTextField();
		JLabel infoLabel = new JLabel();
		JButton removeButton = new JButton();
		EquationSystem.Equation eq = new EquationSystem.Equation();
		EquationSystem baseEqSystem = null;
		static final int height = 20;
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
			this.add(textField);
			this.add(infoLabel);
			this.add(removeButton);
			posComponents();
		}

		void posComponents() {
			textField.setBounds(0, 0, this.getWidth() - infoLabel.getWidth() - 5 - removeButton.getWidth() - 5, height);
			infoLabel.setBounds(textField.getWidth() + 5, 0, infoLabel.getFontMetrics(infoLabel.getFont()).stringWidth(infoLabel.getText()), infoLabel.getFontMetrics(infoLabel.getFont()).getHeight());
			removeButton.setLocation(infoLabel.getX() + infoLabel.getWidth() + 5, 0);
			this.validate();
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
			addNewEquationButton.setText("+");
			addNewEquationButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { addEquation().requestFocus(); }
			});
			this.add(addNewEquationButton);
			for(int i = 0; i < startSize; ++i)
				addEquation();
			if(startSize <= 0) posComponents();
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
			this.add(eqp, equations.size());
			equations.add(eqp);
			posComponents();
			return eqp;
		}
		
		void removeEquation(EquationPanel eqp) {
			for(Iterator<EquationPanel> i = equations.iterator(); i.hasNext();) {
				if(i.next() == eqp) {
					i.remove();
					this.remove(eqp);
					posComponents();
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
			int y = 0;
			for(EquationPanel eqp : equations) {
				eqp.setBounds(0, y, this.getWidth(), EquationPanel.height);
				y += eqp.getHeight() + 5;
			}
			addNewEquationButton.setLocation(0, y);
			height = y + addNewEquationButton.getHeight();
			this.validate();
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
					eqp.setInputWrong("Das kann nicht direkt aus der Schaltung abgelesen werden.");
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
									public Equation eval(EquationPanel obj) { return obj.eq; }
								}));
			}
			
			@Override boolean recheck(EquationPanel eqp) {
				if(eqp.baseEqSystem.canConcludeTo(eqp.eq)) {
					eqp.setInputRight("ok");
					return true;
				} else {
					eqp.setInputWrong("Das kann nicht direkt aus den bisherigen Gleichungen hergeleitet werden.");
					return false;
				}
			}
		}

		BasicEquationsPanel basicEquationsPanel = new BasicEquationsPanel(1);
		FollowingEquationsPanel followingEquationsPanel = new FollowingEquationsPanel();
		
		void posComponents() {
			basicEquationsPanel.setBounds(0, 0, width, basicEquationsPanel.height);
			followingEquationsPanel.setBounds(0, basicEquationsPanel.height, width, height - basicEquationsPanel.height);
			this.validate();
			this.repaint();
		}
		
		MainPanel() {
			super();
			this.setLayout(null);
			this.setName(name);
			this.setAutoscrolls(true);
			this.add(basicEquationsPanel);
			this.add(followingEquationsPanel);
			posComponents();
		}
	}
	
	MainPanel mainPanel = null;
	String name;
	int stepX;
	int stepY;
	int width;
	int height;
	EquationSystem eqSys;
	
	VTEquationInput(String name, int stepX, int stepY, int width, int height, EquationSystem eqSys) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.width = width;
		this.height = height;
		this.eqSys = eqSys;
	}
	
	void posComponents() { if(mainPanel != null) mainPanel.posComponents(); }
	
	@Override
	public Component getComponent() {
		if(mainPanel == null) {
			mainPanel = new MainPanel();
		}
		return mainPanel;
	}

	@Override public int getWidth() { return width; }
	@Override public int getHeight() { return height; }
	@Override public int getStepX() { return stepX; }
	@Override public int getStepY() { return stepY; }
	@Override public void setStepX(int v) { stepX = v; }
	@Override public void setStepY(int v) { stepY = v; }

}
