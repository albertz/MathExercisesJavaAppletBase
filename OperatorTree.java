/**
 * 
 */
package applets.Termumformungen$in$der$Technik_03_Logistik;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;


class OperatorTree implements Comparable<OperatorTree> {
	String op = "";
	abstract static class Entity implements Comparable<OperatorTree.Entity> {
		OperatorTree asTree() { return new OperatorTree("", this); }
		OperatorTree prefixed(final String op) {
			// empty subtree at the beginning is the mark for a prefix op
			OperatorTree ot = new OperatorTree(op, new Subtree(new OperatorTree()));
			ot.entities.add(this);
			return ot;
		}
		boolean isEnclosedImplicitely() { return true; }
		String toString(String parentOp) { return toString(); }
		abstract Object getContent();
		@Override public int hashCode() { return 31 + getContent().hashCode(); }
		@Override public boolean equals(Object obj) {
			if(!(obj instanceof OperatorTree.Entity)) return false;
			return compareTo((OperatorTree.Entity) obj) == 0;
		}
	}
	static class RawString extends OperatorTree.Entity {
		String content = "";
		RawString() {}
		RawString(String s) { if(s == null) throw new AssertionError("string must be non-null"); content = s; }
		@Override public String toString() { return debugOperatorTreeDump ? ("{" + content + "}") : content; }
		public int compareTo(OperatorTree.Entity o) {
			if(o instanceof OperatorTree.RawString) return content.compareTo(((OperatorTree.RawString) o).content);
			return -1;
		}
		Object getContent() { return content; }
		static Utils.Function<OperatorTree.RawString,String> toStringConverter() {
			return new Utils.Function<OperatorTree.RawString,String>() {
				public String eval(OperatorTree.RawString obj) {
					return obj.content;
				}
			};
		}
	}
	static class Subtree extends OperatorTree.Entity {
		OperatorTree content;
		boolean explicitEnclosing = false;
		Subtree(OperatorTree t) { content = t; }
		Subtree(OperatorTree t, boolean explicitEnclosing) { content = t; this.explicitEnclosing = explicitEnclosing; }
		@Override boolean isEnclosedImplicitely() { return explicitEnclosing; }
		@Override public String toString() {
			if(debugOperatorTreeDump || explicitEnclosing)
				return "(" + content.toString() + ")";
			if(content.entities.size() == 1)
				return content.toString();
			if(content.canBeInterpretedAsUnaryPrefixed())
				return content.toString();
			return "(" + content.toString() + ")";
		}
		@Override String toString(String parentOp) {
			if(debugOperatorTreeDump || explicitEnclosing)
				return "(" + content.toString() + ")";
			if(content.entities.size() == 1)
				return content.toString();
			if(content.canBeInterpretedAsUnaryPrefixed())
				return content.toString();
			final String ops = "/∙+-=";
			int parentOpIdx = ops.indexOf(parentOp);
			int childOpIdx = ops.indexOf(content.op);
			if(parentOpIdx < 0 || childOpIdx < 0) return "(" + content.toString() + ")";
			if(parentOpIdx == 3) parentOpIdx--; if(childOpIdx == 3) childOpIdx--; // take +- as equal
			if(childOpIdx <= parentOpIdx) return content.toString();
			return "(" + content.toString() + ")";
		}
		@Override OperatorTree asTree() { return content; }
		Object getContent() { return content; }
		public int compareTo(OperatorTree.Entity o) {
			if(o instanceof OperatorTree.Subtree) return content.compareTo(((OperatorTree.Subtree) o).content);
			return 1; // not subtree, i.e. rawstring, i.e. greater
		}
	}
	List<OperatorTree.Entity> entities = new LinkedList<OperatorTree.Entity>();
	
	OperatorTree() {}
	OperatorTree(String op) { this.op = op; } 
	OperatorTree(String op, OperatorTree.Entity e) { this.op = op; entities.add(e); } 
	OperatorTree(String op, List<OperatorTree.Entity> entities) { this.op = op; this.entities = entities; }
	OperatorTree copy() { return new OperatorTree(op, new LinkedList<OperatorTree.Entity>(entities)); }
	static OperatorTree MergedEquation(OperatorTree left, OperatorTree right) {
		return new OperatorTree("+", Utils.listFromArgs(left.asEntity(), right.minusOne().asEntity()));
	}    	
	static OperatorTree Sum(List<OperatorTree.Entity> entities) { return new OperatorTree("+", entities); }
	static OperatorTree Product(List<OperatorTree.Entity> entities) { return new OperatorTree("∙", entities); }
	
    static OperatorTree Zero() { return Number(0); }        
    boolean isZero() {
    	if(op.equals("∙") && entities.isEmpty()) return false;
    	if(entities.isEmpty()) return true;
    	if(entities.size() > 1) return false;
    	OperatorTree.Entity e = entities.get(0);
    	if(e instanceof OperatorTree.RawString)
    		return ((OperatorTree.RawString) e).content.equals("0");
    	return ((OperatorTree.Subtree) e).content.isZero();
    }

    static OperatorTree One() { return Number(1); }        
    boolean isOne() {
    	if(op.equals("∙") && entities.isEmpty()) return true;
    	if(entities.isEmpty()) return false;
    	if(entities.size() > 1) return false;
    	OperatorTree.Entity e = entities.get(0);
    	if(e instanceof OperatorTree.RawString)
    		return ((OperatorTree.RawString) e).content.equals("1");
    	return ((OperatorTree.Subtree) e).content.isOne();
    }
    
    static OperatorTree Variable(String var) {
    	try {
    		Integer.parseInt(var);
    		throw new AssertionError("Variable '" + var + "' must not be interpretable as a number.");
    	}
    	catch(NumberFormatException e) {
        	return new RawString(var).asTree();        		
    	}
    }
	static OperatorTree Number(int num) { return new RawString("" + num).asTree(); }
	Integer asNumber() {
		if(isZero()) return 0;
		if(isOne()) return 1;
		if(entities.size() == 1) {
			OperatorTree.Entity e = entities.get(0);
			if(e instanceof OperatorTree.Subtree)
				return ((OperatorTree.Subtree)e).content.asNumber();
			String s = ((OperatorTree.RawString)e).content;
			try { return Integer.parseInt(s); }
			catch(NumberFormatException exc) { return null; }
		}
		Integer x;
		if(op.equals("+") || op.equals("-")) x = 0;
		else if(op.equals("∙") || op.equals("/")) x = 1;
		else return null;
		for(OperatorTree.Entity e : entities) {
			Integer y = e.asTree().asNumber();
			if(y == null) return null;
			try {
    			if(op.equals("+")) x += y;
    			else if(op.equals("-")) x -= y;
    			else if(op.equals("∙")) x *= y;
    			else if(op.equals("/")) x /= y;
			} catch(ArithmeticException exc) { // e.g. div by zero 
				return null;
			}
		}
		return x;
	}
    boolean isNumber(int num) {
    	Integer x = asNumber();
    	if(x == null) return false;
    	return x.intValue() == num;
    }
	
	OperatorTree sublist(int from, int to) { return new OperatorTree(op, entities.subList(from, to)); }
	
	static class RawStringIterator implements Iterator<OperatorTree.RawString> {
		static class State {
			int entityIndex = 0;
			OperatorTree ot;
			State(int i, OperatorTree t) { entityIndex = i; ot = t; }
			State(OperatorTree t) { ot = t; }
			State(RawStringIterator.State s) { entityIndex = s.entityIndex; ot = s.ot; }
			boolean hasNext() { return entityIndex < ot.entities.size(); }
			OperatorTree.Entity current() { return ot.entities.get(entityIndex); }
			void remove() { ot.entities.remove(entityIndex); }
			void replace(OperatorTree.Entity e) { ot.entities.set(entityIndex, e); }
			boolean isAtEnd() { return entityIndex == ot.entities.size() - 1; }
		}
		Stack<RawStringIterator.State> stateStack = new Stack<RawStringIterator.State>();
		RawStringIterator.State lastState;
		RawStringIterator() {}    		
		RawStringIterator(OperatorTree t) { stateStack.push(new State(t)); walkSubtrees(); }    		
		@SuppressWarnings("unchecked" /* because of the clone() */) RawStringIterator(OperatorTree.RawStringIterator i) { 
			stateStack = (Stack<RawStringIterator.State>) i.stateStack.clone();
			lastState = i.lastState; // this is safe because we never manipulate lastState
		}
		OperatorTree.RawStringIterator copy() { return new RawStringIterator(this); }
		static Iterable<OperatorTree.RawString> iterable(final OperatorTree t) {
			return new Iterable<OperatorTree.RawString>() {
				public Iterator<OperatorTree.RawString> iterator() {
					return new RawStringIterator(t);
				}
			};
		}

		public boolean hasNext() { return !stateStack.empty() && stateStack.peek().hasNext(); }

		void walkdown() {
			while(!stateStack.empty() && !stateStack.peek().hasNext()) {
				stateStack.pop();
				if(!stateStack.empty())
					stateStack.peek().entityIndex++;						
			}
		}
		
		void walkSubtrees() {
			walkdown();
			while(!stateStack.empty() && stateStack.peek().current() instanceof OperatorTree.Subtree) {
				stateStack.push(new State( ((OperatorTree.Subtree)stateStack.peek().current()).content ));
				walkdown();
			}
		}
		
		public OperatorTree.RawString peek() {
			if(!hasNext()) throw new NoSuchElementException();
			return (OperatorTree.RawString) stateStack.peek().current();
		}
		    		
		public OperatorTree.RawString next() {
			if(!hasNext()) throw new NoSuchElementException();
			
			lastState = new State(stateStack.peek());
			
			stateStack.peek().entityIndex++;
			walkSubtrees();
			
			return (OperatorTree.RawString) lastState.current();
		}

		public void remove() {
			if(lastState == null) throw new IllegalStateException();
			lastState.remove();
			for(RawStringIterator.State s : stateStack) {
				if(s.ot == lastState.ot) {
					if(s.entityIndex == lastState.entityIndex) throw new AssertionError("should not happen that we delete currently pointed-to entry");
					if(s.entityIndex > lastState.entityIndex)
						s.entityIndex--;
				}
			}
			lastState = null;
		}
		
		// replaces last element returned by next() with e in underlying container
		public void replace(OperatorTree.Entity e) {
			if(lastState == null) throw new IllegalStateException();
			lastState.replace(e);
		}
		
		public boolean wasEndOfTree() {
			if(lastState == null) throw new IllegalStateException();
			return lastState.isAtEnd();
		}
		
		public OperatorTree lastTree() {
			if(lastState == null) throw new IllegalStateException();
			return lastState.ot;
		}
	}
	    	
	/* Resulting tree contains subtrees which each parts and rawstrings
	 * with the specific op.
	 */
	private static OperatorTree splitByOps(OperatorTree ot, Set<String> equalPriorityOps) {
		OperatorTree res = new OperatorTree();
		OperatorTree lastSubtree = new OperatorTree();
		
		for(OperatorTree.Entity e : ot.entities) {
			if(e instanceof OperatorTree.RawString) {
	    		String lastStr = "";
				for(Character c : Utils.iterableString(((OperatorTree.RawString) e).content)) {
					if(equalPriorityOps.contains("" + c)) {
						if(!lastStr.isEmpty()) {
							lastSubtree.entities.add(new RawString(lastStr));
							lastStr = "";
						}
						
						if(!lastSubtree.entities.isEmpty()) {
				    		res.entities.add(new Subtree(lastSubtree));
				    		lastSubtree = new OperatorTree();
						}
						
						res.entities.add(new RawString("" + c));
					}
					else if(c != ' ')							
						lastStr += c;
					else {
						if(!lastStr.isEmpty()) {
							lastSubtree.entities.add(new RawString(lastStr));
							lastStr = "";
						}							
					}
				}
				if(!lastStr.isEmpty())
					lastSubtree.entities.add(new RawString(lastStr));
			}
			else { // e instanceof Subtree
				lastSubtree.entities.add(e);
			}
		}
		if(!lastSubtree.entities.isEmpty())
    		res.entities.add(new Subtree(lastSubtree));			
		
		return res;
	}
	
	static OperatorTree.Entity parseOpsInEntity(final OperatorTree.Entity source, final Set<String> binOps, List<Set<String>> binOpList) {
		if(source instanceof OperatorTree.RawString) {
			OperatorTree ot = parseOpsInTree(new OperatorTree("", source), binOps, binOpList);
    		if(ot.entities.size() == 0) throw new AssertionError("something is wrong");
    		else if(ot.entities.size() == 1) return ot.entities.get(0);
    		return new Subtree(ot);        		
		}
		else
			return new Subtree(parseOpsInTree(((OperatorTree.Subtree)source).content, binOps, binOpList), ((OperatorTree.Subtree) source).explicitEnclosing);
	}

	static OperatorTree grabUnaryPrefixedOpFromSplitted(final String op, final Iterator<OperatorTree.Entity> rest) {
		OperatorTree.Entity obj = null;
		
		if(rest.hasNext()) {
			OperatorTree.Entity e = rest.next();
			if(e instanceof OperatorTree.Subtree) {
				OperatorTree eSubtree = ((OperatorTree.Subtree) e).content;
				if(eSubtree.entities.size() == 1)
					obj = eSubtree.entities.get(0);
				else
					obj = e;
			} else // e instanceof RawString -> another unary prefix
				obj = new Subtree(grabUnaryPrefixedOpFromSplitted(((OperatorTree.RawString) e).content, rest));    			
		}
		else
			// we have expected another entry but there is none anymore.
			// add a dummy empty subtree so that it looks like an unary prefixed op.
			obj = new Subtree(new OperatorTree());
		
		return obj.prefixed(op);
	}
	
	static OperatorTree grabUnaryPrefixedOpFromSplitted(OperatorTree splitted) {
		OperatorTree ot = new OperatorTree();
		boolean nextOpWanted = false;
		for(Iterator<OperatorTree.Entity> eit = splitted.entities.iterator(); eit.hasNext();) {
			OperatorTree.Entity e = eit.next();
			if(e instanceof OperatorTree.RawString) {
				String op = ((OperatorTree.RawString) e).content;
				if(!nextOpWanted) {
					//System.out.println("prefix: " + op + " in " + splitted);
					ot.entities.add(new Subtree(grabUnaryPrefixedOpFromSplitted(op, eit)));
					//System.out.println("last prefixed: " + ((Subtree) ot.entities.get(ot.entities.size() - 1)).content);
					nextOpWanted = true;
				} else {
					ot.entities.add(e);
					nextOpWanted = false;
				}
			}
			else { // e instanceof Subtree
				if(nextOpWanted) throw new AssertionError("should not happen by the way splitByOps works");
				ot.entities.add(e);
				nextOpWanted = true;
			}
		}
		return ot;
	}
	
	static OperatorTree splitSplittedByOps(final OperatorTree splitted, Set<String> ops) {
		OperatorTree ot = new OperatorTree();
		OperatorTree lastSubtree = new OperatorTree();
		
		for(OperatorTree.Entity e : splitted.entities) {
			if(e instanceof OperatorTree.RawString) {
				String op = ((OperatorTree.RawString) e).content;
				if(ops.contains(op)) {
					if(!lastSubtree.entities.isEmpty())
			    		ot.entities.add(new Subtree(lastSubtree));
					ot.entities.add(e);
					lastSubtree = new OperatorTree();
					continue;
				}
			}
			lastSubtree.entities.add(e);
		}
		if(!lastSubtree.entities.isEmpty())
    		ot.entities.add(new Subtree(lastSubtree));
		return ot;
	}
	
	static OperatorTree handleOpsInSplitted(OperatorTree splitted, List<Set<String>> binOps) {
		if(binOps.isEmpty()) {
			if(splitted.entities.size() == 1 && splitted.entities.get(0) instanceof OperatorTree.Subtree)
				return ((OperatorTree.Subtree) splitted.entities.get(0)).content;
			return splitted;
		}
		
		Set<String> equallyPriorityOps = binOps.get(0);
		List<Set<String>> restBinOps = binOps.subList(1, binOps.size());
		    		
		splitted = splitSplittedByOps(splitted, equallyPriorityOps);
		if(splitted.entities.size() == 1)
			return handleOpsInSplitted( ((OperatorTree.Subtree) splitted.entities.get(0)).content, restBinOps );
		
		OperatorTree ot = new OperatorTree();
		boolean nextOpWanted = false;
		for(OperatorTree.Entity e : splitted.entities) {
			if(e instanceof OperatorTree.RawString) {
				String op = ((OperatorTree.RawString) e).content;
				if(!nextOpWanted) throw new AssertionError("we should have handled that already in grabUnaryPrefixedOpFromSplitted");
				if(ot.op.isEmpty())
					ot.op = op;
				else
					// it must be an op in equallyPriorityOps because of splitSplittedByOps
					ot = new OperatorTree(op, new Subtree(ot));
				nextOpWanted = false;
			}
			else { // e instanceof Subtree
				if(nextOpWanted) throw new AssertionError("should not happen by the way splitByOps works; ops = " + binOps + ", splitted = " + splitted + ", current = " + ot + ", next = " + ((OperatorTree.Subtree) e).content);
				OperatorTree subtree = ((OperatorTree.Subtree) e).content;
				//if(subtree.entities.size() == 1 && subtree.entities.get(0) instanceof Subtree)
				//	subtree = ((Subtree) subtree.entities.get(0)).content;
				subtree = handleOpsInSplitted( subtree, restBinOps );
				if(subtree.entities.size() == 1)
					ot.entities.add(subtree.entities.get(0));
				else
					ot.entities.add(new Subtree(subtree));
				nextOpWanted = true;
			}				
		}
		
		return ot;
	}
	
	static OperatorTree parseOpsInTree(final OperatorTree source, final Set<String> binOps, List<Set<String>> binOpList) {
		if(source.entities.isEmpty()) return source;
		
		OperatorTree ot = new OperatorTree();
		if(!source.op.isEmpty()) {
			ot.op = source.op;
			for(OperatorTree.Entity e : source.entities)
				ot.entities.add(parseOpsInEntity(e, binOps, binOpList));
			return ot;
		}
		
		OperatorTree splitted = splitByOps(source, binOps);
		if(splitted.entities.size() == 1 && splitted.entities.get(0) instanceof OperatorTree.Subtree) {
			// no op found -> we put the source just into one single subtree
			for(OperatorTree.Entity e : ((OperatorTree.Subtree) splitted.entities.get(0)).content.entities) {
				if(e instanceof OperatorTree.RawString)
					ot.entities.add(e); // we already parsed that in splitByOps
				else
					ot.entities.add(new Subtree(parseOpsInTree( ((OperatorTree.Subtree) e).content, binOps, binOpList ), ((OperatorTree.Subtree) e).explicitEnclosing));
			}
			return ot;
		}
		
		splitted = grabUnaryPrefixedOpFromSplitted(splitted);
		//System.out.println("after unary prefix grab: " + splitted);
		splitted = handleOpsInSplitted(splitted, binOpList);
		//System.out.println("after op handling: " + splitted);
		return parseOpsInTree( splitted, binOps, binOpList );
	}
	    	
	static OperatorTree parseDefaultAnonBinOpInTree(final OperatorTree source, String defaultOp) {
		OperatorTree ot = new OperatorTree();
		ot.op = source.op;
		if(source.op.isEmpty() && source.entities.size() > 1)
			ot.op = defaultOp;
		
		for(OperatorTree.Entity e : source.entities) {
			if(e instanceof OperatorTree.RawString)
				ot.entities.add(e);
			else
				ot.entities.add( new Subtree( parseDefaultAnonBinOpInTree( ((OperatorTree.Subtree) e).content, defaultOp ), ((OperatorTree.Subtree) e).explicitEnclosing ) );
		}
		return ot;
	}
	    	
	static OperatorTree parse(OperatorTree t, List<Set<String>> binOpList, String defaultAnonBinOp) {
		Set<String> binOps = new HashSet<String>();
		for(Set<String> ops : binOpList)
			binOps.addAll(ops);
			
		t = parseOpsInTree(t, binOps, binOpList);
		//System.out.println("before defaultop: " + t);
		if(!defaultAnonBinOp.isEmpty()) t = parseDefaultAnonBinOpInTree(t, defaultAnonBinOp);
		return t;
	}
	
	static OperatorTree undefinedOpTreeFromParseTree(ParseTree t) {
		OperatorTree ot = new OperatorTree();
		for(ParseTree.Entity e : t.entities) {
			if(e instanceof ParseTree.RawString)
				ot.entities.add( new RawString(((ParseTree.RawString)e).content) );
			else
				ot.entities.add( new Subtree( undefinedOpTreeFromParseTree(((ParseTree.Subtree)e).content), true ) );
		}
		return ot;
	}
	    	
	static List<Set<String>> parseOpList(String ops) {
		List<Set<String>> opl = new LinkedList<Set<String>>();
		Set<String> curEquOps = new HashSet<String>();
		opl.add(curEquOps);
		for(Character c : Utils.iterableString(ops)) {
			if(c == ' ') {
				if(curEquOps.isEmpty()) throw new AssertionError("bad oplist str");
				curEquOps = new HashSet<String>();
				opl.add(curEquOps);
			}
			else
				curEquOps.add("" + c);
		}
		if(curEquOps.isEmpty()) throw new AssertionError("bad oplist str");
		if(opl.isEmpty()) throw new AssertionError("bad oplist str - no operators");
		return opl;
	}
	
	static Set<String> simpleParseOps(String ops) {
		Set<String> opSet = new HashSet<String>();
		for(Character c : Utils.iterableString(ops)) {
			if(c == ' ')
				throw new AssertionError("bad oplist str");
			else
				opSet.add("" + c);
		}
		if(opSet.isEmpty()) throw new AssertionError("bad oplist str");
		return opSet;
	}
	    	
	static OperatorTree parse(String str, String binOps, String defaultAnonBinOp) {
		return parse( undefinedOpTreeFromParseTree(new ParseTree(str)), parseOpList(binOps), defaultAnonBinOp );
	}
	static OperatorTree parse(String str, String defaultAnonBinOp) { return parse(str.replace('*', '∙').replace(',', '.'), "= +- ∙/", defaultAnonBinOp); }
	static OperatorTree parse(String str) { return parse(str, "∙"); }
	
	boolean canBeInterpretedAsUnaryPrefixed() {
		return entities.size() == 2 && entities.get(0) instanceof OperatorTree.Subtree && ((OperatorTree.Subtree)entities.get(0)).content.entities.isEmpty();	
	}
	
	OperatorTree.Entity unaryPrefixedContent() {
		if(!canBeInterpretedAsUnaryPrefixed()) throw new AssertionError("we expect the OT to be unary prefixed");
		return entities.get(1);
	}
	
	OperatorTree prefixed(String prefixOp) {
		return new OperatorTree(prefixOp, Utils.listFromArgs(new Subtree(new OperatorTree()), asEntity()));
	}
	
	OperatorTree replaceVar(String var, OperatorTree replacement) {
		OperatorTree ot = new OperatorTree(op);
		for(OperatorTree.Entity e : entities) {
			if(e instanceof OperatorTree.RawString) {
				if(((OperatorTree.RawString)e).content.equals(var))
					ot.entities.add(replacement.asEntity());
				else
					ot.entities.add(e);
			}
			else {
				OperatorTree.Subtree subtree = (OperatorTree.Subtree) e;
				ot.entities.add(new Subtree(subtree.content.replaceVar(var, replacement), subtree.explicitEnclosing));
			}
		}
		return ot;
	}
	
	boolean isNegative() {
		if(entities.size() == 1) {
			OperatorTree.Entity e = entities.get(0);
			if(e instanceof OperatorTree.RawString) return false;
			return ((OperatorTree.Subtree) e).content.isNegative();
		}
		
		if(op.equals("∙") || op.equals("/")) {
			boolean neg = false;
			for(OperatorTree.Entity e : entities)
				neg ^= e.asTree().isNegative();
			return neg;
		}
		
		return canBeInterpretedAsUnaryPrefixed() && op.equals("-") && !unaryPrefixedContent().asTree().isNegative();
	}
	
	@Override public String toString() {
		if(debugOperatorTreeDump)
    		return "[" + op + "] " + Utils.concat(entities, ", ");    			
		if(canBeInterpretedAsUnaryPrefixed())
			// this is a special case used for unary ops (or ops which look like those)
			return op + unaryPrefixedContent().toString(); // always put brackets if it is a subtree
		/*if(entities.isEmpty()) {
			if(isZero()) return "0";
			if(isOne()) return "1";
		}*/
		Iterable<String> entitiesStr = Utils.map(entities, new Utils.Function<OperatorTree.Entity,String>() {
			public String eval(OperatorTree.Entity obj) {
				return obj.toString(op);
			}
		});
		return Utils.concat(entitiesStr, " " + op + " ");
	}
    static boolean debugOperatorTreeDump = false;

    String toString(boolean debug) {
    	boolean oldDebugState = debugOperatorTreeDump;
    	debugOperatorTreeDump = debug;
    	String s = toString();
    	debugOperatorTreeDump = oldDebugState;
    	return s;
    }
    
    String debugStringDouble() { return toString(false) + " // " + toString(true); }
    
	public int compareTo(OperatorTree o) {
		if(entities.size() != 1) {
			int c = op.compareTo(o.op);
			if(c != 0) return c;
		}
		Comparator<Collection<OperatorTree.Entity>> comp = Utils.orderOnCollection();
		return comp.compare(entities, o.entities);
	}
	@Override public int hashCode() {
		int result = 1;
		result = 31 * result + op.hashCode();
		result = 31 * result + entities.hashCode();
		return result;
	}
	@Override public boolean equals(Object obj) {
		if(!(obj instanceof OperatorTree)) return false;
		return compareTo((OperatorTree) obj) == 0;
	}

	Iterable<OperatorTree.RawString> leafs() { return RawStringIterator.iterable(this); }
	Iterable<String> leafsAsString() { return Utils.map(leafs(), RawString.toStringConverter()); }

    Iterable<String> vars() {
    	Iterable<String> leafs = leafsAsString();
    	return Utils.filter(leafs, new Utils.Predicate<String>() {
			public boolean apply(String s) {
				try {
					Integer.parseInt(s);
					return false; // it's an integer -> it's not a var
				}
				catch(NumberFormatException ex) {
					return true; // it's not an integer -> it's a var
				}
			}
    	});
	}
	
	Iterable<String> ops() {
		return new Iterable<String>() {
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					Iterator<String> childs = null;
					public boolean hasNext() { return childs == null || childs.hasNext(); }
					public String next() {
						if(childs == null) {
							Iterable<OperatorTree.Subtree> subtrees = Utils.filterType(OperatorTree.this.entities, OperatorTree.Subtree.class);
							Iterable<Iterable<String>> iterables = Utils.map(subtrees, new Utils.Function<OperatorTree.Subtree,Iterable<String>>() {
								public Iterable<String> eval(OperatorTree.Subtree obj) {
									return obj.content.ops();
								}
							});
							childs = Utils.concatCollectionView(iterables).iterator();
							return OperatorTree.this.op;
						}
						return childs.next();
					}
					public void remove() { throw new UnsupportedOperationException(); }
				};
			}
		};
	}
	
	OperatorTree removeObsolete() {
		if(entities.size() == 1) {
			if(entities.get(0) instanceof OperatorTree.Subtree)
				return ((OperatorTree.Subtree) entities.get(0)).content.removeObsolete();
			return this;
		}
			
		OperatorTree ot = new OperatorTree(op);

		if(op.equals("+") || op.equals("-")) {
			boolean first = true;
			for(OperatorTree.Entity e : entities) {
				if(e instanceof OperatorTree.Subtree)
					e = ((OperatorTree.Subtree) e).content.removeObsolete().asEntity();
				if(!e.asTree().isZero() || (op.equals("-") && first))
					ot.entities.add(e);
				first = false;
			}
		}
		else if(op.equals("∙") || op.equals("/")) {
			boolean first = true;
			for(OperatorTree.Entity e : entities) {
				if(e instanceof OperatorTree.Subtree)
					e = ((OperatorTree.Subtree) e).content.removeObsolete().asEntity();
				if(!e.asTree().isOne() || (op.equals("/") && first))
					ot.entities.add(e);
				first = false;
			}
		}
		else {
			for(OperatorTree.Entity e : entities) {
				if(e instanceof OperatorTree.Subtree)
					e = ((OperatorTree.Subtree) e).content.removeObsolete().asEntity();
				ot.entities.add(e);
			}
		}
		
		return ot;
	}
	
    OperatorTree mergeOps(Set<String> ops) {
    	if(entities.size() == 1 && entities.get(0) instanceof OperatorTree.Subtree)
    		return ((OperatorTree.Subtree) entities.get(0)).content.mergeOps(ops);
    	
    	OperatorTree ot = new OperatorTree(op);
    	for(OperatorTree.Entity e : entities) {
    		if(e instanceof OperatorTree.RawString)
    			ot.entities.add(e);
    		else {
    			OperatorTree subtree = ((OperatorTree.Subtree) e).content.mergeOps(ops);
    			if(subtree.op.equals(ot.op) && ops.contains(op) && !subtree.canBeInterpretedAsUnaryPrefixed())
    				ot.entities.addAll(subtree.entities);
    			else
    				ot.entities.add(new Subtree(subtree));
    		}
    	}
    	return ot;
    }
    
    OperatorTree mergeOpsFromRight(Set<String> ops) {
    	if(entities.size() == 1 && entities.get(0) instanceof OperatorTree.Subtree)
    		return ((OperatorTree.Subtree) entities.get(0)).content.mergeOpsFromRight(ops);
    	
    	OperatorTree ot = new OperatorTree();
    	boolean first = true;
    	ot.op = op;
    	for(OperatorTree.Entity e : entities) {
    		if(e instanceof OperatorTree.RawString)
    			ot.entities.add(e);
    		else {
    			OperatorTree subtree = ((OperatorTree.Subtree) e).content.mergeOpsFromRight(ops);
    			if(first && subtree.op.equals(ot.op) && ops.contains(op) && !subtree.canBeInterpretedAsUnaryPrefixed())
    				ot = subtree;
    			else
    				ot.entities.add(new Subtree(subtree));
    		}
    		first = false;
    	}
    	return ot;
    }        
    
    OperatorTree mergeOps(String ops) { return mergeOps(simpleParseOps(ops)); }        
    OperatorTree mergeOpsFromRight(String ops) { return mergeOpsFromRight(simpleParseOps(ops)); }
    OperatorTree simplify() { return mergeOps("=+∙").mergeOpsFromRight("-/").removeObsolete(); }
    
    OperatorTree transformOp(String oldOp, String newOp, Utils.Function<OperatorTree.Entity,OperatorTree.Entity> leftTransform, Utils.Function<OperatorTree.Entity,OperatorTree.Entity> rightTransform) {
    	OperatorTree ot = new OperatorTree();
    	if(!op.equals(oldOp) || canBeInterpretedAsUnaryPrefixed()) {
    		ot.op = op;
        	for(OperatorTree.Entity e : entities) {
        		if(e instanceof OperatorTree.Subtree)
        			ot.entities.add( new Subtree( ((OperatorTree.Subtree) e).content.transformOp(oldOp, newOp, leftTransform, rightTransform), ((OperatorTree.Subtree) e).explicitEnclosing ) );
        		else // e instanceof RawString
        			ot.entities.add(e);
        	}
        	return ot;
    	}
    	
    	// op == oldOp here 
    	ot.op = newOp;
    	boolean first = true;
    	for(OperatorTree.Entity e : entities) {
    		if(e instanceof OperatorTree.Subtree)
    			e = new Subtree( ((OperatorTree.Subtree) e).content.transformOp(oldOp, newOp, leftTransform, rightTransform), ((OperatorTree.Subtree) e).explicitEnclosing );
    		if(first) {
    			if(leftTransform != null) e = leftTransform.eval(e);
    		} else {
    			if(rightTransform != null) e = rightTransform.eval(e);
    		}
    		ot.entities.add(e);
    		first = false;
    	}
    	return ot;
    }
    
    static Utils.Function<OperatorTree.Entity,OperatorTree.Entity> DoPrefixByOp(final String op) {
    	return new Utils.Function<OperatorTree.Entity,OperatorTree.Entity>() {
    		public OperatorTree.Entity eval(OperatorTree.Entity obj) { return new Subtree(obj.prefixed(op)); }
    	};
    }
    
    OperatorTree transformMinusToPlus() {
		return transformOp("-", "+", null, DoPrefixByOp("-"));
    }
    
    OperatorTree transformMinusPushedDown() {
    	OperatorTree.Entity e = transformMinusPushedDown(false);
    	if(e instanceof OperatorTree.Subtree)
    		return ((OperatorTree.Subtree) e).content;
    	return new OperatorTree("", e);
    }
    OperatorTree.Entity transformMinusPushedDown(boolean negate) {
    	if(canBeInterpretedAsUnaryPrefixed() && op.equals("-")) {        		
    		OperatorTree.Entity e = unaryPrefixedContent();
    		negate = !negate;
    		if(e instanceof OperatorTree.Subtree)
    			return ((OperatorTree.Subtree) e).content.transformMinusPushedDown(negate);
    		if(negate)
    			return new Subtree(e.prefixed("-"));
    		return e;
    	}
    	else {
    		OperatorTree ot = new OperatorTree();
    		ot.op = op;
    		for(OperatorTree.Entity e : entities) {
    			if(e instanceof OperatorTree.Subtree) {
    				OperatorTree.Subtree origSubtree = (OperatorTree.Subtree) e;
    				e = origSubtree.content.transformMinusPushedDown(negate);
    				if(e instanceof OperatorTree.Subtree)
    					((OperatorTree.Subtree) e).explicitEnclosing = origSubtree.explicitEnclosing;
    			}
    			else {
    				if(negate)
    					e = new Subtree(e.prefixed("-"));
    			}
    			ot.entities.add(e);
    			// don't negate further entries if this is a multiplication/division
    			if(op.equals("∙") || op.equals("/")) negate = false;
    		}
    		return new Subtree(ot);
    	}
    }
    
	OperatorTree sum(OperatorTree other) {
		if(this.isZero()) return other;
		if(other.isZero()) return this;

		if((this.op.equals("+") || this.entities.size() == 1) && (other.op.equals("+") || other.entities.size() == 1))
			return Sum(new LinkedList<OperatorTree.Entity>(Utils.concatCollectionView(entities, other.entities)));
					
		if(this.op.equals("+"))
			return Sum(new LinkedList<OperatorTree.Entity>(Utils.concatCollectionView(this.entities, Utils.listFromArgs(other.asEntity()))));

		if(other.op.equals("+"))
			return Sum(new LinkedList<OperatorTree.Entity>(Utils.concatCollectionView(Utils.listFromArgs(this.asEntity()), other.entities)));

		return Sum(Utils.listFromArgs(this.asEntity(), other.asEntity()));
	}

    OperatorTree minusOne() {
    	if(canBeInterpretedAsUnaryPrefixed() && op.equals("-"))
    		return unaryPrefixedContent().asTree();
    	
    	OperatorTree.Entity e = transformMinusPushedDown(true);
    	return e.asTree();
	}

	OperatorTree.Entity asEntity() {
		if(entities.size() == 1)
			return entities.get(0);
		return new Subtree(this);
	}
    
	OperatorTree divide(OperatorTree other) {
		if(isZero()) return this;
		if(other.isOne()) return this;
		if(other.isNumber(-1)) return this.minusOne();
		{
			Integer thisNum = this.asNumber(), otherNum = other.asNumber();
			if(thisNum != null && otherNum != null && thisNum % otherNum != 0)
				return Number(thisNum / otherNum);
		}
		if(op.equals("/")) {
			if(entities.size() == 2)
				return new OperatorTree("/", Utils.listFromArgs(entities.get(0), entities.get(1).asTree().multiply(other).asEntity()));
			OperatorTree ot = new OperatorTree(op);
			for(OperatorTree.Entity e : entities) ot.entities.add(e);
			ot.entities.add(other.asEntity());
			return ot;
		}
		return new OperatorTree("/", Utils.listFromArgs(asEntity(), other.asEntity()));
	}
	
	OperatorTree mergeDivisions() {
		if(entities.size() == 1) {
			OperatorTree.Entity e = entities.get(0);
			if(e instanceof OperatorTree.Subtree)
				return ((OperatorTree.Subtree)e).content.mergeDivisions();
			return this;
		}
		
		if(op.equals("∙")) {
			OperatorTree nom = One(), denom = One();
			for(OperatorTree.Entity e : entities) {
				OperatorTree ot = e.asTree().mergeDivisions();
				if(ot.op.equals("/")) {
					int i = 0;
					for(OperatorTree.Entity e2 : ot.entities) {
						if(i == 0) nom = nom.multiply(e2.asTree());
						else denom = denom.multiply(e2.asTree());
						i++;
					}
				}
				else nom = nom.multiply(ot);
			}
			return nom.divide(denom);
		}
		else if(op.equals("/")) {
			OperatorTree nom = One(), denom = One();
			int i = 0;
			for(OperatorTree.Entity e : entities) {
				OperatorTree ot = e.asTree().mergeDivisions();
				if(ot.op.equals("/")) {
					int j = 0;
					for(OperatorTree.Entity e2 : ot.entities) {
						if(j == 0 && i == 0 || j > 0 && i > 0) nom = nom.multiply(e2.asTree());
						else denom = denom.multiply(e2.asTree());
						j++;
					}
				}
				else {
					if(i == 0) nom = nom.multiply(e.asTree());
					else denom = denom.multiply(e.asTree());
				}
				i++;
			}
			return nom.divide(denom);
		}
		else {
			OperatorTree ot = new OperatorTree(op);
			for(OperatorTree.Entity e : entities)
				ot.entities.add(e.asTree().mergeDivisions().asEntity());
			return ot;
		}
	}
	
	OperatorTree canRemoveFactor(OperatorTree fac) {
		if(equals(fac)) return One();
		if(minusOne().equals(fac)) return Number(-1);
		if(entities.size() == 1) {
			if(entities.get(0) instanceof OperatorTree.Subtree)
				return ((OperatorTree.Subtree) entities.get(0)).content.canRemoveFactor(fac);
			return null;
		}
		
		OperatorTree ot = new OperatorTree(op);
		boolean needOne;
		if(op.equals("∙")) needOne = true;
		else if(op.equals("/")) {
			if(entities.isEmpty()) return null;
			OperatorTree sub = entities.get(0).asTree().canRemoveFactor(fac);
			if(sub == null) return null;
			ot.entities.add(sub.asEntity());
			for(int i = 1; i < entities.size(); ++i) ot.entities.add(entities.get(i));
			return ot;
		}
		else needOne = false;
		boolean haveOne = false;
		int numfac = 1;
		for(OperatorTree.Entity e : entities) {
			if(needOne && haveOne) {
				ot.entities.add(e);
				continue;
			}
			OperatorTree sub = e.asTree().canRemoveFactor(fac);
			if(sub == null) {
				if(!needOne) return null; // we need all but this one does not have the factor
				ot.entities.add(e);
				continue;
			}
			haveOne = true;
			if(op.equals("∙") && sub.asNumber() != null)
				numfac *= sub.asNumber();
			else
				ot.entities.add(sub.asEntity());
		}
		if(needOne && !haveOne && !entities.isEmpty()) return null;
		if(op.equals("∙") && numfac != 1) {
			if(!ot.entities.isEmpty()) ot.entities.set(0, ot.entities.get(0).asTree().multiply(Number(numfac)).asEntity());
			else ot = Number(numfac);
		}
		return ot;
	}
	
	static Utils.Pair<Integer,Integer> simplifyDivisionFactor(List<OperatorTree.Entity> nomProd, List<OperatorTree.Entity> denomProd) {
		Utils.Pair<Integer,Integer> nomDenomFacs = new Utils.Pair<Integer,Integer>(1,1);
		int fac = 1;
		for(int i = 0; i < nomProd.size(); ++i) {
			OperatorTree.Entity e = nomProd.get(i);
			if(e.asTree().isNegative()) {
				nomProd.set(i, e.asTree().minusOne().asEntity());
				fac *= -1;
				nomDenomFacs.first *= -1;
				e = nomProd.get(i);
			}
			nomProd.set(i, e.asTree().simplify().asEntity());
		}
		for(int i = 0; i < denomProd.size(); ++i) {
			OperatorTree.Entity e = denomProd.get(i);
			if(e.asTree().isNegative()) {
				denomProd.set(i, e.asTree().minusOne().asEntity());
				fac *= -1;
				nomDenomFacs.second *= -1;
				e = denomProd.get(i);
			}
			denomProd.set(i, e.asTree().simplify().asEntity());
		}
		if(fac == -1) {
			if(nomProd.isEmpty()) nomProd.add(Number(-1).asEntity());
			else nomProd.set(0, nomProd.get(0).asTree().minusOne().asEntity());
			nomDenomFacs.first *= -1;
		}
		return nomDenomFacs;
	}
	
	OperatorTree asSum() {
		if((op.equals("+") || op.equals("-")) && !canBeInterpretedAsUnaryPrefixed()) return this;
		return Sum(Utils.listFromArgs(asEntity()));
	}
	
	OperatorTree asProduct() {
		if(op.equals("∙")) return this;
		return Product(Utils.listFromArgs(asEntity()));
	}
	
	OperatorTree firstProductInSum() { // expecting that we are a sum
		if(entities.isEmpty()) return null;
		if(op.equals("+") || op.equals("-")) return entities.get(0).asTree().asProduct();
		return null;
	}
	
	OperatorTree simplifyDivision() {
		if(op.equals("/") && entities.size() == 2) {
			OperatorTree nom = entities.get(0).asTree().asSum().copy(), denom = entities.get(1).asTree().asSum().copy();
			OperatorTree nomProd = nom.firstProductInSum();
			if(nomProd == null) return this; // somehow illformed -> cannot simplify
			OperatorTree denomProd = denom.firstProductInSum();
			if(denomProd == null) return this; // cannot simplify because it is undefined (division by zero)				
			Utils.Pair<Integer,Integer> nomDenomFac = simplifyDivisionFactor(nomProd.entities, denomProd.entities);
			nom.entities.set(0, nomProd.asEntity());
			denom.entities.set(0, denomProd.asEntity());
			if(nomDenomFac.first != 1) {
				for(int i = 1; i < nom.entities.size(); ++i)
					nom.entities.set(i, nom.entities.get(i).asTree().multiply(Number(nomDenomFac.first)).asEntity());
			}
			if(nomDenomFac.second != 1) {
				for(int i = 1; i < denom.entities.size(); ++i)
					denom.entities.set(i, denom.entities.get(i).asTree().multiply(Number(nomDenomFac.second)).asEntity());
			}
			
			//System.out.println("div: " + nomProd + " / " + denomProd);
			for(OperatorTree.Entity e : new ArrayList<OperatorTree.Entity>(Utils.concatCollectionView(denomProd.entities, Utils.listFromArgs(denom.asEntity())))) {
				OperatorTree newNom = nom.canRemoveFactor(e.asTree());
				OperatorTree newDenom = denom.canRemoveFactor(e.asTree());
				//System.out.println("removing " + e.asTree().debugStringDouble() + " from " + nom.debugStringDouble() + ": " + newNom);
				//System.out.println("removing " + e.asTree().debugStringDouble() + " from " + denom.debugStringDouble() + ": " + newDenom);
				
				if(newNom != null && newDenom != null) {
					nom = newNom.asSum(); denom = newDenom.asSum();
					nomProd = nom.firstProductInSum();
					denomProd = denom.firstProductInSum();
					if(nomProd == null || denomProd == null) break;
				}
			}
			
			return nom.divide(denom);
		}
		return this;
	}
	
	OperatorTree nextDivision() {
		if(entities.isEmpty()) return null;
		if(op.equals("/")) return entities.get(entities.size()-1).asTree();
		for(OperatorTree.Entity e : entities) {
			if(e instanceof OperatorTree.Subtree) {
				OperatorTree next = ((OperatorTree.Subtree) e).content.nextDivision();
				if(next != null) return next;
			}
		}
		return null;
	}
	
    OperatorTree multiplyAllDivisions() {
    	OperatorTree ot = this;
    	OperatorTree nextDiv = null;
    	while((nextDiv = ot.nextDivision()) != null)
    		ot = ot.multiply(nextDiv);
    	return ot;
    }
    
    boolean matchDenominatorInDiv(OperatorTree denom) {
    	return op.equals("/") && entities.size() > 1 && entities.get(entities.size()-1).asTree().equals(denom);
    }
    
    boolean haveDenominatorInSubtree(OperatorTree denom) {
    	if(matchDenominatorInDiv(denom)) return true;
    	for(OperatorTree.Entity e : entities) {
    		if(e instanceof OperatorTree.Subtree && e.asTree().haveDenominatorInSubtree(denom))
    			return true;
    	}
    	return false;
    }
    
    OperatorTree multiply(OperatorTree other) {
    	if(isZero()) return this;
    	if(isOne()) return other;
    	if(isNumber(-1)) return other.minusOne();
    	if(other.isOne()) return this;
    	if(other.isNumber(-1)) return this.minusOne();

    	OperatorTree ot = new OperatorTree(op);

    	if(op.equals("∙")) {
    		for(int i = 0; i < entities.size(); ++i) {
    			if(entities.get(i).asTree().haveDenominatorInSubtree(other)) {
    				for(int j = 0; j < entities.size(); ++j) {
    					if(i != j)
    						ot.entities.add(entities.get(j));
    					else
    						ot.entities.add(entities.get(j).asTree().multiply(other).asEntity());
    				}
    				return ot;
    			}
    		}
    		ot.entities.addAll(entities);
    		if(other.op.equals(op))
    			ot.entities.addAll(other.entities);
    		else
    			ot.entities.add(other.asEntity());
    		return ot;
    	}
    	
    	if(canBeInterpretedAsUnaryPrefixed() && !op.equals("/")) {
    		OperatorTree.Entity e = unaryPrefixedContent();
    		if(e instanceof OperatorTree.Subtree)
    			return ((OperatorTree.Subtree) e).content.multiply(other).prefixed(op);
    		ot.op = "∙";
    		ot.entities.add(asEntity());
    		ot.entities.add(other.asEntity());
    		return ot;
    	}
    	
    	if(entities.size() == 1) {
    		OperatorTree.Entity e = entities.get(0);
    		if(e instanceof OperatorTree.Subtree)
    			return ((OperatorTree.Subtree) e).content.multiply(other);
    		ot.op = "∙";
    		ot.entities.add(e);
    		ot.entities.add(other.asEntity());
    		return ot;
    	}

    	if(op.equals("/")) {
    		if(entities.isEmpty())
    			ot.entities.add(other.asEntity());
    		else if(matchDenominatorInDiv(other)) {
    			ot.entities.addAll(entities);
    			ot.entities.remove(entities.size()-1);
    			if(ot.entities.size() == 1)
    				ot = ot.entities.get(0).asTree();
    		}
    		else {
    			boolean first = true;
            	for(OperatorTree.Entity e : entities) {
            		if(first)
            			ot.entities.add(e.asTree().multiply(other).asEntity());
            		else
            			ot.entities.add(e);
            		first = false;
            	}
    		}
    		return ot;
    	}
    	
    	// +, - or whatever else -> mult each entry
    	for(OperatorTree.Entity e : entities)
    		ot.entities.add(e.asTree().multiply(other).asEntity());
    	return ot;
    }
    
    OperatorTree pushdownMultiplication(OperatorTree other) {
    	if(this.isOne()) return other;
		if((op.equals("+") || op.equals("-")) && !canBeInterpretedAsUnaryPrefixed()) {
			OperatorTree newOt = new OperatorTree(op);
			for(OperatorTree.Entity e : entities) {
				OperatorTree ot = e.asTree().pushdownMultiplication(other);
				if(ot.op.equals("+"))
					newOt.entities.addAll(ot.entities);
				else
					newOt.entities.add(ot.asEntity());
			}
			return newOt;
		}
		if(other.op.equals("+") || other.op.equals("-") && !other.canBeInterpretedAsUnaryPrefixed()) {
			OperatorTree newOt = new OperatorTree(other.op);
			for(OperatorTree.Entity e : other.entities) {
				OperatorTree ot = pushdownMultiplication(e.asTree());
				if(ot.op.equals("+"))
					newOt.entities.addAll(ot.entities);
				else
					newOt.entities.add(ot.asEntity());
			}
			return newOt;
		}
		if(canBeInterpretedAsUnaryPrefixed() && op.equals("-") && other.canBeInterpretedAsUnaryPrefixed() && other.op.equals("-")) {
			return unaryPrefixedContent().asTree().pushdownMultiplication(other.unaryPrefixedContent().asTree());
		}
		if(op.equals("∙")) {
			OperatorTree newOt = new OperatorTree(op);
			newOt.entities.addAll(entities);
			if(other.op.equals("∙"))
				newOt.entities.addAll(other.entities);
			else
				newOt.entities.add(other.asEntity());
			return newOt;
		}
    	return multiply(other);
    }
    
    OperatorTree pushdownAllMultiplications() {
    	if(op.equals("∙")) {
    		OperatorTree ot = One();
    		for(OperatorTree.Entity e : entities)
    			ot = ot.pushdownMultiplication(e.asTree().pushdownAllMultiplications());
    		return ot;
    	}
    	else {
        	OperatorTree ot = new OperatorTree(op);
        	for(OperatorTree.Entity e : entities) {
        		if(e instanceof OperatorTree.Subtree) {
        			OperatorTree subtree = ((OperatorTree.Subtree) e).content.pushdownAllMultiplications();
        			if(op.equals("+") && subtree.op.equals("+"))
        				ot.entities.addAll(subtree.entities);
        			else
        				ot.entities.add(subtree.asEntity());
        		}
        		else
        			ot.entities.add(e);
        	}
        	return ot;
    	}
    }

}