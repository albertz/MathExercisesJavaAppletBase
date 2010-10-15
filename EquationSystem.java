package applets.Termumformungen$in$der$Technik_01_URI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import applets.Termumformungen$in$der$Technik_01_URI.EquationSystem.Equation.ParseError;

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
	}

	static class Equation {
		static class FracSum {
			static class Frac {
				static class Sum {		
					static class Prod {
						int fac;
						static class Pot {
							VariableSymbol sym;
							int pot;
							@Override public String toString() { return sym.name + ((pot != 1) ? ("^" + pot) : ""); }					
						}
						List<Pot> facs = new LinkedList<Pot>();
						@Override public String toString() { return ((fac != 1) ? "" + fac + " " : "") + Utils.concat(facs, " "); }
					}
					List<Prod> entries = new LinkedList<Prod>();
					@Override public String toString() { return ((entries.size() > 1) ? "(" : "") + Utils.concat(entries, " + ") + ((entries.size() > 1) ? ")" : ""); }
				}
				Sum numerator = new Sum(), denominator = new Sum();
				@Override public String toString() { return numerator.toString() + " / " + denominator.toString(); }
				Frac() {}
				Frac(Utils.ParseTree trimmedTree) throws ParseError { parse(trimmedTree); }
				void parse(Utils.ParseTree trimmedTree) throws ParseError {}
			}
			List<Frac> entries = new LinkedList<Frac>();
			@Override public String toString() { return Utils.concat(entries, " + "); }
			void parse(Utils.ParseTree trimmedTree) throws ParseError {
				List<Utils.ParseTree> l = trimmedTree.split("+");
				entries.clear();
				for(Utils.ParseTree t : l)
					entries.add(new Frac(t));
			}
		}
		FracSum left = new FracSum(), right = new FracSum();
		@Override public String toString() { return left.toString() + " = " + right.toString(); }
		
		void clear() { left = new FracSum(); right = new FracSum(); }
		class ParseError extends Exception {
			public ParseError(String msg) { super(msg); }			
		}
		void parse(String s) throws ParseError {
			List<Utils.ParseTree> l = new Utils.ParseTree(s).trim().split("=");
			if(l.size() != 2) throw new ParseError("bad number of '=' found. exactly one expected");
			left.parse(l.get(0));
			right.parse(l.get(1));
		}
	}
	
	List<Equation> equations = new LinkedList<Equation>();
	
}
