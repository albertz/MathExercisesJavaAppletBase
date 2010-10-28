package applets.Termumformungen$in$der$Technik_01_URI;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	
	static class VariableSymbol implements Comparable<VariableSymbol> {
		Unit unit;
		String name;
		VariableSymbol(String unit) { this.unit = new Unit(unit); }
		VariableSymbol(String name, String unit) { this.name = name; this.unit = new Unit(unit); }
		public int compareTo(VariableSymbol o) { if(this == o) return 0; return name.compareTo(o.name); }
		@Override public String toString() { return name; }
	}
	
	Map<String, VariableSymbol> variableSymbols = new HashMap<String, VariableSymbol>();	
	void registerVariableSymbol(VariableSymbol var) { variableSymbols.put(var.name, var); }	
	void registerVariableSymbol(String name, String unit) { registerVariableSymbol(new VariableSymbol(name, unit)); }
		
	static abstract class Expression {
		<T> T castTo(Class<T> clazz) {
			if(clazz.isAssignableFrom(getClass())) return clazz.cast(this);
			if(clazz.isAssignableFrom(VariableSymbol.class)) return clazz.cast(asVariable());
			if(clazz.isAssignableFrom(Integer.class)) return clazz.cast(asNumber());
			try {
				return clazz.getConstructor(getClass()).newInstance(this);
			} catch (Exception e) {}
			throw new AssertionError("unknown type: " + clazz);
		}
		VariableSymbol asVariable() { return castTo(Equation.FracSum.Frac.Sum.Prod.Pot.class).asVariable(); } 
		Integer asNumber() { return castTo(Equation.FracSum.Frac.Sum.Prod.class).asNumber(); }

		boolean equalsToNum(int num) {
			Integer myNum = asNumber();
			if(myNum != null) return myNum == num;
			return false;
		}
		
		abstract Iterable<? extends Expression> childs();
		Iterable<VariableSymbol> vars() {
			Iterable<Iterable<VariableSymbol>> varIters = Utils.map(childs(), new Utils.Function<Expression, Iterable<VariableSymbol>>() {
				public Iterable<VariableSymbol> eval(Expression obj) {
					return obj.vars();
				}
			});
			return Utils.concatCollectionView(varIters);
		}
	}
	
	static class Equation extends Expression implements Comparable<Equation> {
		static class FracSum extends Expression implements Comparable<FracSum> {
			static class Frac extends Expression implements Comparable<Frac> {
				static class Sum extends Expression implements Comparable<Sum> {
					static class Prod extends Expression implements Comparable<Prod> {
						int fac = 0;
						static class Pot extends Expression implements Comparable<Pot> {
							VariableSymbol sym;
							int pot = 1;
							@Override public String toString() { return sym.name + ((pot != 1) ? ("^" + pot) : ""); }		
							Pot(VariableSymbol s) { sym = s; }
							Pot(VariableSymbol s, int p) { sym = s; pot = p; }
							public int compareTo(Pot o) {
								if(sym != o.sym) return sym.compareTo(o.sym);
								if(pot < o.pot) return -1;
								if(pot > o.pot) return 1;
								return 0;
							}
							@Override public int hashCode() {
								int result = 1;
								result = 31 * result + pot;
								result = 31 * result + ((sym == null) ? 0 : sym.hashCode());
								return result;
							}
							@Override public boolean equals(Object obj) {
								if(!(obj instanceof Pot)) return false;
								return compareTo((Pot) obj) == 0;
							}
							@Override Integer asNumber() {
								if(pot == 0) return 1;
								return null;
							}
							@Override VariableSymbol asVariable() {
								if(pot == 1) return sym;
								return null;
							}
							@Override Iterable<VariableSymbol> vars() { return Utils.listFromArgs(sym); }
							@Override Iterable<? extends Expression> childs() { return Utils.listFromArgs(); }
						}						
						List<Pot> facs = new LinkedList<Pot>();
						public int compareTo(Prod o) {
							int r = Utils.<Pot>orderOnCollection().compare(facs, o.facs);
							if(r != 0) return r;
							if(fac < o.fac) return -1;
							if(fac > o.fac) return 1;
							return 0;
						}
						@Override public boolean equals(Object obj) {
							if(!(obj instanceof Prod)) return false;
							return compareTo((Prod) obj) == 0;
						}
						@Override Integer asNumber() {
							if(fac == 0) return 0;
							if(facs.isEmpty()) return fac;
							return null;
						}
						@Override <T> T castTo(Class<T> clazz) {
							if(clazz.isAssignableFrom(Pot.class)) {
								if(facs.size() == 1) return clazz.cast(facs.get(0));
								return null;
							}
							return super.castTo(clazz);
						}
						boolean isZero() { return fac == 0; }
						boolean isOne() { return fac == 1 && facs.isEmpty(); }
						@Override public String toString() {
							if(facs.isEmpty()) return "" + fac;
							if(fac == 1) return Utils.concat(facs, " ∙ ");
							if(fac == -1) return "-" + Utils.concat(facs, " ∙ ");
							return fac + " ∙ " + Utils.concat(facs, " ∙ ");
						}
						Map<VariableSymbol,Integer> varMap() {
							Map<VariableSymbol,Integer> vars = new TreeMap<VariableSymbol,Integer>();
							for(Pot p : facs) {
								if(!vars.containsKey(p.sym)) vars.put(p.sym, 0);
								vars.put(p.sym, vars.get(p.sym) + p.pot);
							}
							return vars;
						}
						Prod normalize() {
							Prod prod = new Prod();
							prod.fac = fac;
							Map<VariableSymbol,Integer> vars = varMap();
							for(VariableSymbol sym : vars.keySet()) {
								if(vars.get(sym) != 0)
									prod.facs.add(new Pot(sym, vars.get(sym)));
							}
							return prod;
						}
						Prod minusOne() { return new Prod(-fac, facs); }
						Prod divideAndRemove(VariableSymbol var) {
							Prod prod = normalize();
							for(Iterator<Pot> pit = prod.facs.iterator(); pit.hasNext(); ) {
								Pot p = pit.next();
								if(p.sym == var) {
									if(p.pot == 1) {
										pit.remove();
										return prod;
									}
									break;
								}
							}
							return null; // var not included or not pot=1
						}
						Prod mult(Prod other) {
							Prod res = new Prod();
							res.fac = fac * other.fac;
							res.facs.addAll(facs);
							res.facs.addAll(other.facs);
							return res.normalize();
						}
						Prod divide(Prod other) {
							if(!(other.fac != 0)) throw new AssertionError("other.fac != 0 failed");
							if(!(fac % other.fac == 0)) throw new AssertionError("fac % other.fac == 0 failed");
							Prod res = new Prod();
							res.fac = fac / other.fac;
							res.facs.addAll(facs);
							for(Pot p : other.facs)
								res.facs.add(new Pot(p.sym, -p.pot));
							return res.normalize();
						}
						Prod commonBase(Prod other) {
							Prod base = new Prod();
							base.fac = BigInteger.valueOf(fac).gcd(BigInteger.valueOf(other.fac)).intValue();
							Map<VariableSymbol,Integer> varMap1 = varMap();
							Map<VariableSymbol,Integer> varMap2 = other.varMap();							
							Set<VariableSymbol> commonVars = new HashSet<VariableSymbol>();
							commonVars.addAll(varMap1.keySet());
							commonVars.retainAll(varMap2.keySet());
							for(VariableSymbol var : commonVars) {
								Pot pot = new Pot(var);
								pot.pot = Math.min(varMap1.get(var), varMap2.get(var));
								if(pot.pot != 0)
									base.facs.add(pot);
							}
							return base;
						}
						@Override Iterable<? extends Expression> childs() { return facs; }
						Prod() {}
						Prod(int num) { this.fac = num; }
						Prod(VariableSymbol var) { fac = 1; facs.add(new Pot(var)); }
						Prod(int fac, List<Pot> facs) { this.fac = fac; this.facs = facs; }
						Prod(Iterable<VariableSymbol> vars) {
							fac = 1;
							for(VariableSymbol v : vars)
								facs.add(new Pot(v));
						}
						Prod(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError { fac = 1; parse(ot, vars); }
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
												throw new ParseError("Variable '" + s + "' is unknown.", "Die Variable '" + s + "' ist unbekannt.");
										}
									}
									else
										parse( ((Utils.OperatorTree.Subtree) e).content, vars );
								}
							}
							else
								throw new ParseError("'" + ot + "' must be a product.", "'" + ot + "' muss ein Produkt sein.");
							
							if(facs.isEmpty())
								throw new ParseError("'" + ot + "' must contain at least one variable.", "'" + ot + "' muss mindestens eine Variable enthalten.");
						}
					}
					List<Prod> entries = new LinkedList<Prod>();
					@Override public String toString() { return Utils.concat(entries, " + "); }
					public int compareTo(Sum o) { return Utils.<Prod>orderOnCollection().compare(entries, o.entries); }
					@Override public boolean equals(Object obj) {
						if(!(obj instanceof Sum)) return false;
						return compareTo((Sum) obj) == 0;
					}
					@Override <T> T castTo(Class<T> clazz) {
						// TODO...
						return super.castTo(clazz);
					}
					boolean isEmpty() { return entries.isEmpty(); }
					boolean isOne() { return entries.size() == 1 && entries.get(0).isOne(); }
					Sum normalize() {
						Sum sum = new Sum();
						List<Prod> newEntries = new LinkedList<Prod>();
						for(Prod prod : entries)
							newEntries.add(prod.normalize());
						Collections.sort(newEntries);
						Prod lastProd = null;
						for(Prod prod : newEntries) {
							if(lastProd != null && lastProd.facs.equals(prod.facs))
								lastProd.fac += prod.fac;
							else {
								lastProd = prod;
								sum.entries.add(lastProd);
							}
						}
						for(Iterator<Prod> pit = sum.entries.iterator(); pit.hasNext();) {
							if(pit.next().isZero())
								pit.remove();
						}
						return sum;
					}
					Sum minusOne() {
						Sum sum = new Sum();
						for(Prod prod : entries) sum.entries.add(prod.minusOne());
						return sum;
					}
					Sum mult(Prod other) {
						Sum sum = new Sum();
						if(other.isZero()) return sum;
						for(Prod p : entries)
							sum.entries.add(p.mult(other));
						return sum;
					}
					@Override Iterable<? extends Expression> childs() { return entries; }
					static class ExtractedVar {
						VariableSymbol var;
						Sum varMult = new Sum();
						Sum independentPart = new Sum();
					}
					ExtractedVar extractVar(VariableSymbol var) {
						ExtractedVar extracted = new ExtractedVar();
						extracted.var = var;
						for(Prod p : entries) {
							if(p.isZero()) continue;
							Prod newP = p.divideAndRemove(var);
							if(newP != null)
								extracted.varMult.entries.add(newP);
							else
								extracted.independentPart.entries.add(p);
						}
						if(!extracted.varMult.entries.isEmpty())
							return extracted;
						return null;
					}
					Sum() {}
					Sum(VariableSymbol var) { entries.add(new Prod(var)); }
					Sum(Prod prod) { entries.add(prod); }
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
				@Override public String toString() { return "(" + numerator.toString() + ")" + ((denominator != null) ? " / (" + denominator.toString() + ")" : ""); }
				boolean denominatorEqualTo(Sum fdenominator) {
					if(denominator == null && fdenominator == null) return true;
					if(denominator != null && fdenominator == null) return false;
					if(denominator == null && fdenominator != null) return false;
					return denominator.equals(fdenominator);
				}
				boolean hasEqualDenominatorAs(Frac f) { return denominatorEqualTo(f.denominator); }
				boolean isZero() { return numerator.isEmpty(); }
				boolean isOne() { return numerator.isOne() && denominator == null; }
				public int compareTo(Frac o) {
					if(denominator != null && o.denominator == null) return 1;
					if(denominator == null && o.denominator != null) return -1;
					if(denominator != null && o.denominator != null) {
						int r = denominator.compareTo(o.denominator);
						if(r != 0) return r;
					}
					return numerator.compareTo(o.numerator);					
				}
				@Override public boolean equals(Object obj) {
					if(!(obj instanceof Frac)) return false;
					return compareTo((Frac) obj) == 0;
				}
				Frac normalize() { return new Frac(numerator.normalize(), (denominator != null) ? denominator.normalize() : null); }
				Frac minusOne() { return new Frac(numerator.minusOne(), denominator); }
				Frac mult(Sum.Prod prod) { return new Frac(numerator.mult(prod), denominator); }
				@Override Iterable<? extends Expression> childs() {
					if(denominator != null)
						return Utils.listFromArgs(numerator, denominator);
					else
						return Utils.listFromArgs(numerator);
				}
				Frac() {}
				Frac(VariableSymbol var) { this(new Sum(var), null); }
				Frac(Sum.Prod prod) { this(new Sum(prod), null); }
				Frac(Sum numerator, Sum denominator) { this.numerator = numerator; this.denominator = denominator; }
				Frac(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError {
					if(!ot.op.equals("/"))
						numerator = new Sum(ot, vars);
					else {
						if(ot.entities.size() != 2) throw new ParseError("The fraction '" + ot + "' must be of the form 'a / b'.", "Der Bruch '" + ot + "' muss in der Form 'a / b' sein.");
						numerator = new Sum(ot.entities.get(0).asTree(), vars);
						denominator = new Sum(ot.entities.get(1).asTree(), vars);
					}
				}
			}
			List<Frac> entries = new LinkedList<Frac>();
			@Override public String toString() { return entries.isEmpty() ? "0" : Utils.concat(entries, " + "); }
			public int compareTo(FracSum o) { return Utils.<Frac>orderOnCollection().compare(entries, o.entries); }
			@Override public boolean equals(Object obj) {
				if(!(obj instanceof FracSum)) return false;
				return compareTo((FracSum) obj) == 0;
			}
			boolean isZero() { return entries.isEmpty(); }
			boolean isOne() { return entries.size() == 1 && entries.get(0).isOne(); }
			FracSum normalize() {
				List<Frac> newEntries = new LinkedList<Frac>();
				for(Frac f : entries) newEntries.add(f.normalize());
				Collections.sort(newEntries);
				FracSum sum = new FracSum();
				Frac lastFrac = null;
				for(Frac f : newEntries) {
					if(lastFrac != null && lastFrac.hasEqualDenominatorAs(f)) {
						lastFrac.numerator.entries.addAll(f.numerator.entries);
						lastFrac.numerator = lastFrac.numerator.normalize();
					}
					else {
						lastFrac = f;
						sum.entries.add(lastFrac);
					}
				}
				for(Iterator<Frac> fit = sum.entries.iterator(); fit.hasNext();) {
					if(fit.next().numerator.isEmpty())
						fit.remove();
				}
				return sum;
			}
			FracSum minusOne() {
				FracSum sum = new FracSum();
				for(Frac f : entries) sum.entries.add(f.minusOne());
				return sum;
			}
			FracSum sum(FracSum otherSum) {
				FracSum sum = new FracSum();
				for(Frac f : entries) sum.entries.add(f);
				for(Frac f : otherSum.entries) sum.entries.add(f);
				return sum;
			}
			Frac fracWithDenom(Frac.Sum denominator) {
				for(Frac f : entries)
					if(f.denominatorEqualTo(denominator))
						return f;
				return null;
			}
			Frac fracWithDenom1() { return fracWithDenom(null); }
			Frac.Sum numeratorWithDenom1() { return fracWithDenom1().numerator; }
			FracSum mult(Frac.Sum.Prod prod) {
				FracSum sum = new FracSum();
				if(prod.isZero()) return sum;
				for(Frac f : entries)
					sum.entries.add(f.mult(prod));
				return sum;
			}
			@Override Iterable<? extends Expression> childs() { return entries; }
			FracSum() {}
			FracSum(VariableSymbol var) { entries.add(new Frac(var)); }
			FracSum(Frac.Sum.Prod prod) { entries.add(new Frac(prod)); }
			FracSum(Frac.Sum sum) { entries.add(new Frac(sum, null)); }
			FracSum(Frac frac) { entries.add(frac); }
			FracSum(int num) { entries.add(new Frac(new Frac.Sum.Prod(num))); }
			FracSum(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError {
				if(ot.entities.size() == 1
						&& ot.entities.get(0) instanceof Utils.OperatorTree.RawString
						&& ((Utils.OperatorTree.RawString) ot.entities.get(0)).content.equals("0"))
					// empty entries list -> 0
					return;
				
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
		public int compareTo(Equation o) {
			int r = left.compareTo(o.left); if(r != 0) return r;
			return right.compareTo(o.right);
		}
		@Override public boolean equals(Object obj) {
			if(!(obj instanceof Equation)) return false;
			return compareTo((Equation) obj) == 0;
		}
		Equation normalize() {
			Equation eq = new Equation();
			eq.left.entries.addAll(left.entries);
			eq.left.entries.addAll(right.minusOne().entries);
			eq.left = eq.left.normalize();
			return eq;
		}
		Equation minusOne() { return new Equation(left.minusOne(), right.minusOne()); }
		boolean isTautology() {
			Equation norm = normalize();
			return norm.left.isZero() && norm.right.isZero();
		}
		boolean equalNorm(Equation other) {
			other = other.normalize();
			Equation normed = normalize();
			if(other.equals(normed)) return true;
			if(other.equals(normed.minusOne())) return true;
			return false;
		}
		@Override Iterable<? extends Expression> childs() { return Utils.listFromArgs(left, right); }
		Equation() {}
		Equation(FracSum left, FracSum right) { this.left = left; this.right = right; }
		Equation(Utils.OperatorTree ot, Map<String,VariableSymbol> vars) throws ParseError {
			if(ot.entities.size() == 0) throw new ParseError("Please give me an equation.", "Bitte Gleichung eingeben.");
			if(!ot.op.equals("=")) throw new ParseError("'=' at top level required.", "'=' benötigt.");
			if(ot.entities.size() == 1) throw new ParseError("An equation with '=' needs two sides.", "'=' muss genau 2 Seiten haben.");
			if(ot.entities.size() > 2) throw new ParseError("Sorry, only two parts for '=' allowed.", "'=' darf nicht mehr als 2 Seiten haben.");
			left = new FracSum(ot.entities.get(0).asTree(), vars);
			right = new FracSum(ot.entities.get(1).asTree(), vars);			
		}
		Equation(String str, Map<String,VariableSymbol> vars) throws ParseError {
			this(Utils.OperatorTree.parse(str), vars);
		}
		void swap(Equation eq) {
			Equation tmp = new Equation(left, right);
			left = eq.left; right = eq.right;
			eq.left = tmp.left; eq.right = tmp.right;
		}

		static class ParseError extends Exception {
			private static final long serialVersionUID = 1L;
			String english, german;
			public ParseError(String english, String german) { super(english); this.english = english; this.german = german; }			
		}
		
	}

	void debugEquation(Utils.OperatorTree ot) {
		ot = ot.simplify().transformMinusToPlus();
		System.out.print("normalised input: " + ot + ", ");
		try {
			Equation eq = new Equation(ot, variableSymbols);
			System.out.println("parsed: " + eq + ", normalised eq: " + eq.normalize());
		} catch (Equation.ParseError e) {
			System.out.println("error while parsing " + ot + ": " + e.getMessage());
		}
	}
	
	Collection<Equation> equations = new LinkedList<Equation>();
	
	EquationSystem() {}
	EquationSystem(Map<String, VariableSymbol> variableSymbols) { this.variableSymbols = variableSymbols; }
	EquationSystem(Collection<Equation> equations, Map<String, VariableSymbol> variableSymbols) {
		this.equations = equations;
		this.variableSymbols = variableSymbols;
	}
	EquationSystem extendedSystem(Collection<Equation> additionalEquations) {
		return new EquationSystem(
				Utils.extendedCollectionView(equations, additionalEquations),
				variableSymbols);
	}
	EquationSystem extendedSystem() { return extendedSystem(new LinkedList<Equation>()); }
	EquationSystem normalized() {
		return new EquationSystem(
				Utils.map(equations, new Utils.Function<Equation,Equation>() {
					public Equation eval(Equation obj) { return obj.normalize(); }
				}),
				variableSymbols);
	}
	EquationSystem linearIndependent() {
		EquationSystem eqSys = new EquationSystem(variableSymbols);
		for(Equation eq : equations)
			if(!eqSys.canConcludeTo(eq))
				eqSys.equations.add(eq);
		return eqSys;
	}
	
	boolean contains(Equation eq) {
		eq = eq.normalize();
		Equation minusEq = eq.minusOne();		
		for(Equation e : normalized().equations) {
			if(eq.equals(e) || minusEq.equals(e)) {
				//System.out.println("contains match: " + eq + " equal to " + e);
				return true;
			}
			//System.out.println("contains: " + eq + " unequal to " + e);
		}
		return false;
	}
	
	private boolean _canConcludeTo(Equation eq, Set<Equation> usedEquationList) {
		//System.out.println("to? " + eq);
		//dump();
		Set<Equation> equations = new TreeSet<Equation>(this.equations);
		if(equations.contains(eq))
			return true;
		for(Equation myEq : this.equations) {
			if(usedEquationList.contains(myEq)) continue;
			Collection<Equation> allConclusions = calcAllConclusions(eq, myEq);
			if(!allConclusions.isEmpty()) {
				usedEquationList.add(myEq);
				equations.remove(myEq);
				Set<Equation> results = new TreeSet<Equation>();
				for(Equation resultingEq : allConclusions) {
					if(resultingEq.isTautology())
						return true;
					if(results.contains(resultingEq)) continue;
					results.add(resultingEq);
					if(new EquationSystem(equations, variableSymbols)._canConcludeTo(resultingEq, usedEquationList))
						return true;
				}
				equations.add(myEq);
			}
		}
		return false;		
	}
	
	boolean canConcludeTo(Equation eq) {
		return normalized()._canConcludeTo(eq.normalize(), new TreeSet<Equation>());
	}

	EquationSystem allConclusions() {
		return calcAllConclusions(null);
	}
	
	private Set<VariableSymbol> commonVars(Equation eq1, Equation eq2) {
		Set<VariableSymbol> commonVars = new HashSet<VariableSymbol>(Utils.collFromIter(eq1.vars()));
		commonVars.retainAll(new HashSet<VariableSymbol>(Utils.collFromIter(eq2.vars())));
		return commonVars;
	}

	private List<Equation> calcAllConclusions(Equation eq1, Equation eq2) {
		List<Equation> results = new LinkedList<Equation>();
		
		if(eq1.isTautology()) return results;
		if(eq2.isTautology()) return results;
		if(!(eq1.left.fracWithDenom1() != null)) throw new AssertionError("pair.first.left.fracWithDenom1() != null failed");
		if(!(eq2.left.fracWithDenom1() != null)) throw new AssertionError("pair.second.left.fracWithDenom1() != null failed");

		//System.out.println("vars in first equ " + pair.first + ": " + Utils.collFromIter(pair.first.vars()));
		//System.out.println("vars in second equ " + pair.second + ": " + Utils.collFromIter(pair.second.vars()));
		Set<VariableSymbol> commonVars = commonVars(eq1, eq2);
		//System.out.println("common vars in " + pair.first + " and " + pair.second + ": " + commonVars);
		
		for(VariableSymbol var : commonVars) {					
			Equation.FracSum.Frac.Sum.ExtractedVar extract1 = eq1.left.numeratorWithDenom1().extractVar(var);
			Equation.FracSum.Frac.Sum.ExtractedVar extract2 = eq2.left.numeratorWithDenom1().extractVar(var);
			if(extract1 == null) continue; // can happen if we have higher order polynoms
			if(extract2 == null) continue; // can happen if we have higher order polynoms
			if(extract1.varMult.entries.size() != 1) continue; // otherwise not supported yet
			if(extract2.varMult.entries.size() != 1) continue; // otherwise not supported yet
			Equation.FracSum.Frac.Sum.Prod varMult1 = extract1.varMult.entries.get(0);
			Equation.FracSum.Frac.Sum.Prod varMult2 = extract2.varMult.entries.get(0);
			Equation.FracSum.Frac.Sum.Prod commonBase = varMult1.commonBase(varMult2);
			varMult1 = varMult1.divide(commonBase);
			varMult2 = varMult2.divide(commonBase);
			if(!(!varMult1.varMap().containsKey(var))) throw new AssertionError("!varMult1.varMap().containsKey(var) failed"); // we tried to remove that
			if(!(!varMult2.varMap().containsKey(var))) throw new AssertionError("!varMult2.varMap().containsKey(var) failed"); // we tried to remove that
			Equation.FracSum newSum1 = eq1.left.mult(varMult2);
			Equation.FracSum newSum2 = eq2.left.mult(varMult1.minusOne());
			Equation.FracSum bothSums = newSum1.sum(newSum2);
			Equation resultingEquation = new Equation(bothSums, new Equation.FracSum(0));
			resultingEquation = resultingEquation.normalize();
			results.add(resultingEquation);			
		}
		
		return results;
	}
	
	private EquationSystem calcAllConclusions(Equation breakIfThisEquIsFound) {
		if(breakIfThisEquIsFound != null) {
			breakIfThisEquIsFound = breakIfThisEquIsFound.normalize();
			if(contains(breakIfThisEquIsFound)) return null;
		}
		
		// map values are the set of used based equations
		Map<Equation, Set<Equation>> resultingEquations = new TreeMap<Equation, Set<Equation>>();
		Set<Equation> normalizedEquations = new TreeSet<Equation>(normalized().equations);
		Iterable<Equation> nextEquations = normalizedEquations;
		
		int iteration = 0;
		while(true) {
			iteration++;
			List<Equation> newResults = new LinkedList<Equation>();
			for(Utils.Pair<Equation,Equation> pair : Utils.allPairs(normalizedEquations, nextEquations) ) {
				// if we have used the same base equation in this resulted equation already, skip this one
				if(resultingEquations.containsKey(pair.second) && resultingEquations.get(pair.second).contains(pair.first)) continue;

				for(Equation resultingEquation : calcAllConclusions(pair.first, pair.second)) {
					if(!resultingEquation.isTautology() && !resultingEquations.containsKey(resultingEquation)) {
						//System.out.println("in iteration " + iteration + ": " + pair.first + " and " + pair.second + " -> " + resultingEquation);
						resultingEquations.put(resultingEquation, new TreeSet<Equation>(Utils.listFromArgs(pair.first)));
						if(normalizedEquations.contains(pair.second)) resultingEquations.get(resultingEquation).add(pair.second);
						else resultingEquations.get(resultingEquation).addAll(resultingEquations.get(pair.second));
						newResults.add(resultingEquation);
						if(breakIfThisEquIsFound != null) {
							if(breakIfThisEquIsFound.equals(resultingEquation)) return null;
							if(breakIfThisEquIsFound.equals(resultingEquation.minusOne())) return null;
						}
					}					
				}
			}
			nextEquations = newResults;
			if(newResults.isEmpty()) break;
			if(iteration >= normalizedEquations.size()) break;
		}
		
		/*
		System.out.println("{");
		for(Equation e : normalizedEquations)
			System.out.println("   " + e);
		for(Equation e : resultingEquations)
			System.out.println("-> " + e);
		System.out.println("}");
		*/
		
		return new EquationSystem(new LinkedList<Equation>(Utils.concatCollectionView(normalizedEquations, resultingEquations.keySet())), variableSymbols);
	}
		
	void dump() {
		System.out.println("equations: [");
		for(Equation e : equations)
			System.out.println("  " + e + " ,");
		System.out.println(" ]");
	}

	static void debug() {
		EquationSystem sys = new EquationSystem();
		for(int i = 1; i <= 5; ++i) sys.registerVariableSymbol("I" + i, "");		
		try {
			sys.equations.add(new Equation("(I3 + -I4 + -I5) = 0", sys.variableSymbols));
			sys.equations.add(new Equation("(-I2 + I5) = 0", sys.variableSymbols));
			sys.equations.add(new Equation("(-I1 + I2) = 0", sys.variableSymbols));
			Equation eq = new Equation("(I1 + -I3 + I4) = 0", sys.variableSymbols);
			if(!sys.canConcludeTo(eq)) throw new AssertionError("must follow: " + eq);
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		}		
	}
}
