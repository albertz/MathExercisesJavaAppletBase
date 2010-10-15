package applets.Termumformungen$in$der$Technik_01_URI;

public class Content {

	Applet applet;
	PGraph graph;
	
	public Content(Applet applet) {
		this.applet = applet;		
	}
	
	public void init() {
		applet.setSize(518, 420);
	}

	void postinit() {}
	void next(int i) {}	
	boolean isCorrect(int i, String sel) { return false; }
	
	void debugUtilsParsingOpTree(String s) {
		System.out.println("parsed " + s + " -> " + Utils.OperatorTree.parse(s).toString());
	}
	
	void debugUtilsParsingOpTree() {
		debugUtilsParsingOpTree("a * b = c");
		debugUtilsParsingOpTree("a + b * d");
		debugUtilsParsingOpTree("1 + 2 - 3 - 4 + 5");
		debugUtilsParsingOpTree("(1 + 2) + (3 + 4) - 5");
		debugUtilsParsingOpTree("(1 + 2) - (3) - 4");
		debugUtilsParsingOpTree("1 + -2 + 3");
		debugUtilsParsingOpTree("1 + -(2 + 3) + 4");
		debugUtilsParsingOpTree("(1 + 2) + (3 + 4) (5 + 6)");
		debugUtilsParsingOpTree("1 * 2 (3)");
		debugUtilsParsingOpTree("1 * 2 3 / 4 * 5");
	}
	
	public void run() {
		debugUtilsParsingOpTree();
		
		graph = new PGraph(applet, 480, 400);
		ElectronicCircuit e = new ElectronicCircuit();
		e.registerOnPGraph(graph, e.randomSetup(4, 4));
		
		applet.vtmeta.setExtern(new VisualThing[] {
				new VTImage("graph", 10, 20, 480, 400, graph),
		});
	}
	
}
