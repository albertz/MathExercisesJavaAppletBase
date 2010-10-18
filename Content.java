package applets.Termumformungen$in$der$Technik_01_URI;

public class Content {

	Applet applet;
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(518, 440);
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
		s.registerVariableSymbol("A", "!");
		s.registerVariableSymbol("B1", "!");
		s.registerVariableSymbol("B2", "!");
		s.registerVariableSymbol("C", "!");
		s.debugEquation(ot);
	}
	
	public void run() {
		Utils.debugUtilsParsingOpTree();
		
		graph = new PGraph(applet, 480, 400);
		ElectronicCircuit e = new ElectronicCircuit();
		e.registerOnPGraph(graph, e.randomSetup(4, 4));
				
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 20, 480, 400, graph),
				new VTText("math", 10, 10, 400, new Utils.Callback<VTText>() {
					public void run(VTText obj) {
						doSomeDebugStuffWithOpTree(((MathTextField) obj.getComponent()).getOperatorTree());						
					}
				}, MathTextField.class),
		});
	}
	
}
