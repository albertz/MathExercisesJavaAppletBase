package applets.Termumformungen$in$der$Technik_01_URI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EquationSystem {

	Set<String> baseUnits = new HashSet<String>();
	
	static class Unit {
		static class Pot {
			String baseUnit; int pot;
			Pot(String u) { baseUnit = u; pot = 1; }
			Pot(String u, int p) { baseUnit = u; pot = p; }
			@Override public String toString() { return baseUnit + ((pot != 1) ? ("^" + pot) : ""); }
		}
		List<Pot> facs = new LinkedList<Pot>();
		void init(String str) {
			int state = 0;
			String curPot = "";
			String curBaseUnit = "";
			for(int i = 0; i <= str.length(); ++i) {
				int c = (i < str.length()) ? str.charAt(i) : 0;
				boolean finishCurrent = false;
				if(c == '^') {
					if(state != 0) throw new AssertionError("bad ^ at " + i);
					if(curBaseUnit.isEmpty()) throw new AssertionError("missing unit, bad ^ at " + i);
					state = 1;
				}
				else if(c >= '0' && c <= '9' || c == '-') {
					if(state == 0) curBaseUnit += (char)c;
					else curPot += (char)c;
				}
				else if(c == ' ' || c == 0) {
					if(state == 0 && !curBaseUnit.isEmpty()) finishCurrent = true;
					if(state == 1 && !curPot.isEmpty()) finishCurrent = true;
				}
				else {
					if(state == 1) throw new AssertionError("invalid char " + (char)c + " at " + i);
					curBaseUnit += (char)c;
				}
				if(finishCurrent) {
					Pot p = new Pot(curBaseUnit);
					if(state > 0) p.pot = Integer.parseInt(curPot);
					facs.add(p);
					state = 0; curBaseUnit = ""; curPot = "";
				}
			}			
		}
		void simplify() {
			Map<String,Integer> pot = new HashMap<String, Integer>();
			for(Pot p : this.facs) {
				int oldPot = pot.containsKey(p.baseUnit) ? pot.get(p.baseUnit) : 0;
				pot.put(p.baseUnit, oldPot + p.pot);
			}
			facs.clear();
			for(String u : pot.keySet()) {
				if(pot.get(u) != 0)
					facs.add(new Pot(u, pot.get(u)));
			}			
		}
		Unit(String str) { init(str); simplify(); }
		Unit(String str, Set<String> allowedBaseUnits) {
			init(str); simplify();
			for(Pot p : facs)
				if(!allowedBaseUnits.contains(p.baseUnit))
					throw new AssertionError("unit " + p.baseUnit + " not allowed");
		}
		@Override public String toString() { return Utils.concat(facs, " "); }
	}
	
	static class VariableSymbol {
		Unit unit;
		String name;
		VariableSymbol(String unit) { this.unit = new Unit(unit); }
		VariableSymbol(String name, String unit) { this.name = name; this.unit = new Unit(unit); }
	}
	
	Map<String, VariableSymbol> variableSymbols = new HashMap<String, VariableSymbol>();	
	void registerVariableSymbol(VariableSymbol var) { variableSymbols.put(var.name, var); }	
	void registerVariableSymbol(String name, String unit) { registerVariableSymbol(new VariableSymbol(name, unit)); }
	
	static class Equation {
		static class FracSum {
			static class Frac {
				static class Sum {		
					static class Prod {
						int fac = 1;
						static class Pot {
							VariableSymbol sym;
							int pot = 1;
							@Override public String toString() { return sym.name + ((pot != 1) ? ("^" + pot) : ""); }		
							Pot(VariableSymbol s) { sym = s; }
						}						
						List<Pot> facs = new LinkedList<Pot>();
						@Override public String toString() { return ((fac != 1) ? "" + fac + " ∙ " : "") + Utils.concat(facs, " ∙ "); }						
						Prod() {}
						Prod(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError { parse(ot, vars); }
						void parse(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError {
							if(ot.canBeInterpretedAsUnaryPrefixed() && ot.op.equals("-")) {
								fac = -fac;
								parse(ot.unaryPrefixedContent().asTree(), vars);
							}
							else if(ot.op.equals("∙") || ot.entities.size() <= 1) {
								for(Utils.OperatorTree.Entity e : ot.entities) {
									if(e instanceof Utils.OperatorTree.RawString) {
										String s = ((Utils.OperatorTree.RawString) e).content;
										try {
											fac *= Integer.parseInt(s);
										}
										catch(NumberFormatException ex) {
											VariableSymbol var = vars.get(s);
											if(var != null)
												facs.add(new Pot(var));
											else
												throw new ParseError("Variable '" + s + "' is unknown.");
										}
									}
									else
										parse( ((Utils.OperatorTree.Subtree) e).content, vars );
								}
							}
							else
								throw new ParseError("'" + ot + "' must be a product.");
							
							if(facs.isEmpty())
								throw new ParseError("'" + ot + "' must contain at least one variable.");
						}
					}
					List<Prod> entries = new LinkedList<Prod>();
					@Override public String toString() { return ((entries.size() > 1) ? "(" : "") + Utils.concat(entries, " + ") + ((entries.size() > 1) ? ")" : ""); }
					boolean isEmpty() { return entries.isEmpty(); }
					Sum() {}
					Sum(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError {
						if(ot.op.equals("+") || ot.entities.size() <= 1) {
							for(Utils.OperatorTree.Entity e : ot.entities)
								entries.add( new Prod(e.asTree(), vars) );
						}
						else
							entries.add( new Prod(ot, vars) );
					}
				}
				Sum numerator = new Sum(), denominator = null;
				@Override public String toString() { return numerator.toString() + ((denominator != null) ? " / " + denominator.toString() : ""); }
				Frac() {}
				Frac(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError {
					if(!ot.op.equals("/"))
						numerator = new Sum(ot, vars);
					else {
						if(ot.entities.size() != 2) throw new ParseError("The fraction '" + ot + "' must be of the form 'a / b'.");
						numerator = new Sum(ot.entities.get(0).asTree(), vars);
						denominator = new Sum(ot.entities.get(1).asTree(), vars);
					}
				}
			}
			List<Frac> entries = new LinkedList<Frac>();
			@Override public String toString() { return Utils.concat(entries, " + "); }
			FracSum() {}
			FracSum(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError {
				if(ot.op.equals("+") || ot.entities.size() <= 1) {
					for(Utils.OperatorTree.Entity e : ot.entities)
						entries.add( new Frac(e.asTree(), vars) );
				}
				else
					entries.add( new Frac(ot, vars) );
			}
		}
		FracSum left = new FracSum(), right = new FracSum();		
		@Override public String toString() { return left.toString() + " = " + right.toString(); }
		Equation() {}
		Equation(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError {
			if(ot.entities.size() == 0) throw new ParseError("Please give me an equation.");
			if(!ot.op.equals("=")) throw new ParseError("'=' at top level required.");
			if(ot.entities.size() == 1) throw new ParseError("An equation with '=' needs two sides.");
			if(ot.entities.size() > 2) throw new ParseError("Sorry, only two parts for '=' allowed.");
			left = new FracSum(ot.entities.get(0).asTree(), vars);
			right = new FracSum(ot.entities.get(1).asTree(), vars);			
		}		

		static class ParseError extends Exception {
			private static final long serialVersionUID = 1L;
			public ParseError(String msg) { super(msg); }			
		}
		
	}

	void debugEquation(Utils.OperatorTree ot) {
		ot = ot.simplify().transformMinusToPlus();
		System.out.print("normalised: " + ot + ", ");
		try {
			System.out.println("parsed: " + new Equation(ot, variableSymbols));
		} catch (Equation.ParseError e) {
			System.out.println("error while parsing " + ot + ": " + e.getMessage());
		}
	}
	
	static void debugEquationParsing() {
		
	}

	List<Equation> equations = new LinkedList<Equation>();
	
}
