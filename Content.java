package applets.Termumformungen$in$der$Technik_02_Kondensatoren;

import java.util.Random;
import javax.swing.JScrollPane;

public class Content {

	Applet applet;
	PGraph graph;
	ElectronicCircuit circuit;
	VTEquationInput equationInput;
	VTLabel questionLabel;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(600, 500);
		applet.scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

	void postinit() {}
	void next(int i) {}	
	boolean isCorrect(int i, String sel) { return false; }
	
	static void opTreeDebugOutput(String desc, Utils.OperatorTree ot) {
		System.out.println(desc + ": " + ot.toString() + " ; simplified: " + ot.simplify().toString());
	}
	static void doSomeDebugStuffWithOpTree(Utils.OperatorTree ot) {		
		//opTreeDebugOutput("minustoplus", ot.transformMinusToPlus());
		EquationSystem s = new EquationSystem();
		s.registerVariableSymbol("A");
		s.registerVariableSymbol("B1");
		s.registerVariableSymbol("B2");
		s.registerVariableSymbol("C");
		s.debugEquation(ot);
	}
	
	void initNewCircuit() {
		int w = 5;
		int h = 3;
		graph.setSize(w * 100 + 20, h * 100);
		graph.clear();
		circuit.registerOnPGraph(graph, circuit.randomSetup(w, h));
		ElectronicCircuit.EquationQuestion question = circuit.randomEquationQuestion();
		equationInput.setEquationSystem(circuit.getEquationSystem(), question.wantedExpr, question.allowedVars);
		equationInput.clear();
		questionLabel.setText(equationInput.wantedResult.toString());
	}
	
	public void run() {
		//Utils.debugUtilsParsingOpTree();
		//EquationSystem.debug();
		
		graph = new PGraph(applet, 400, 400);
		circuit = new ElectronicCircuit();
		equationInput = new VTEquationInput("equ", 10, 10, applet.getWidth() - 60);
		questionLabel = new VTLabel("question", "", 10, 10, applet.getWidth() - 60);
		initNewCircuit();
				
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTButton("new", "neue Aufgabe", 10, 10, new Runnable() {
					public void run() {
						initNewCircuit();
						applet.revalidateVisualThings();
						applet.repaint();						
					}
				}),
				new VTImage("graph", 10, 20, applet.getWidth() - 60, 400, graph),
				questionLabel,
				new VTText("math", 10, 10, 400, new Utils.Callback<VTText>() {
					public void run(VTText obj) {
						doSomeDebugStuffWithOpTree(((MathTextField) obj.getComponent()).getOperatorTree());						
					}
				}, MathTextField.class),
				equationInput,
		});
	}
	
}
