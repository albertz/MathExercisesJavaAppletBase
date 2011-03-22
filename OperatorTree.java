/**
 * 
 */
package applets.Termumformungen$in$der$Technik_03_Logistik;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


class OperatorTree implements Comparable<OperatorTree> {
	String op = "";
	List<OTEntity> entities = new LinkedList<OTEntity>();
	final static String knownOps = "^/∙+-="; // they also must be in order
	
	OperatorTree() {}
	OperatorTree(String op) { this.op = op; } 
	OperatorTree(String op, OTEntity e) { this.op = op; entities.add(e); } 
	OperatorTree(String op, List<OTEntity> entities) { this.op = op; this.entities = entities; }
	OperatorTree copy() { return new OperatorTree(op, new LinkedList<OTEntity>(entities)); }
	static OperatorTree MergedEquation(OperatorTree left, OperatorTree right) {
		return new OperatorTree("+", Utils.listFromArgs(left.asEntity(), right.minusOne().asEntity()));
	}    	
	static OperatorTree Sum(List<OTEntity> entities) { return new OperatorTree("+", entities); }
	static OperatorTree Product(List<OTEntity> entities) { return new OperatorTree("∙", entities); }
	
    static OperatorTree Zero() { return Number(0); }        
    boolean isZero() {
    	if(op.equals("∙") && entities.isEmpty()) return false;
    	if(entities.isEmpty()) return true;
    	if(entities.size() > 1) return false;
    	OTEntity e = entities.get(0);
    	if(e instanceof OTRawString)
    		return ((OTRawString) e).content.equals("0");
    	return ((OTSubtree) e).content.isZero();
    }

    static OperatorTree One() { return Number(1); }        
    boolean isOne() {
    	if(op.equals("∙") && entities.isEmpty()) return true;
    	if(entities.isEmpty()) return false;
    	if(entities.size() > 1) return false;
    	OTEntity e = entities.get(0);
    	if(e instanceof OTRawString)
    		return ((OTRawString) e).content.equals("1");
    	return ((OTSubtree) e).content.isOne();
    }
    
    static OperatorTree Variable(String var) {
    	try {
    		Integer.parseInt(var);
    		throw new AssertionError("Variable '" + var + "' must not be interpretable as a number.");
    	}
    	catch(NumberFormatException e) {
        	return new OTRawString(var).asTree();        		
    	}
    }
	static OperatorTree Number(int num) { return new OTRawString("" + num).asTree(); }
	Integer asNumber() {
		if(isZero()) return 0;
		if(isOne()) return 1;
		if(entities.size() == 1) {
			OTEntity e = entities.get(0);
			if(e instanceof OTSubtree)
				return ((OTSubtree)e).content.asNumber();
			String s = ((OTRawString)e).content;
			try { return Integer.parseInt(s); }
			catch(NumberFormatException exc) { return null; }
		}
		Integer x;
		if(op.equals("+") || op.equals("-")) x = 0;
		else if(op.equals("∙") || op.equals("/")) x = 1;
		else return null;
		for(OTEntity e : entities) {
			Integer y = e.asTree().asNumber();
			if(y == null) return null;
			try {
    			if(op.equals("+")) x += y;
    			else if(op.equals("-")) x -= y;
    			else if(op.equals("∙")) x *= y;
    			else if(op.equals("/")) {
    				if(x % y == 0)
    					x /= y;
    				else
    					return null; // this is not an integer, it's a rational number
    			}
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
	
    boolean isFunctionCall() {
    	// right now, everything which is not empty and not a known operation is a function
    	if(op.length() == 0) return false;
    	if(op.length() == 1) return knownOps.indexOf(op) < 0;
    	return true;
    }
    
	boolean canBeInterpretedAsUnaryPrefixed() {
		return entities.size() == 2 && entities.get(0) instanceof OTSubtree && ((OTSubtree)entities.get(0)).content.entities.isEmpty();	
	}
	
	OTEntity unaryPrefixedContent() {
		if(!canBeInterpretedAsUnaryPrefixed()) throw new AssertionError("we expect the OT to be unary prefixed");
		return entities.get(1);
	}
	
	OperatorTree prefixed(String prefixOp) {
		return new OperatorTree(prefixOp, Utils.listFromArgs(new OTSubtree(new OperatorTree()), asEntity()));
	}
	
	OperatorTree replaceVar(String var, OperatorTree replacement) {
		OperatorTree ot = new OperatorTree(op);
		for(OTEntity e : entities) {
			if(e instanceof OTRawString) {
				if(((OTRawString)e).content.equals(var))
					ot.entities.add(replacement.asEntity());
				else
					ot.entities.add(e);
			}
			else {
				OTSubtree subtree = (OTSubtree) e;
				ot.entities.add(new OTSubtree(subtree.content.replaceVar(var, replacement), subtree.explicitEnclosing));
			}
		}
		return ot;
	}
	
	boolean isNegative() {
		if(entities.size() == 1) {
			OTEntity e = entities.get(0);
			if(e instanceof OTRawString) return false;
			return ((OTSubtree) e).content.isNegative();
		}
		
		if(op.equals("∙") || op.equals("/")) {
			boolean neg = false;
			for(OTEntity e : entities)
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
		Iterable<String> entitiesStr = Utils.map(entities, new Utils.Function<OTEntity,String>() {
			public String eval(OTEntity obj) {
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
		Comparator<Collection<OTEntity>> comp = Utils.orderOnCollection();
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

	Iterable<OTRawString> leafs() { return OTRawStringIterator.iterable(this); }
	Iterable<String> leafsAsString() { return Utils.map(leafs(), OTRawString.toStringConverter()); }

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
							Iterable<OTSubtree> subtrees = Utils.filterType(OperatorTree.this.entities, OTSubtree.class);
							Iterable<Iterable<String>> iterables = Utils.map(subtrees, new Utils.Function<OTSubtree,Iterable<String>>() {
								public Iterable<String> eval(OTSubtree obj) {
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
			if(entities.get(0) instanceof OTSubtree)
				return ((OTSubtree) entities.get(0)).content.removeObsolete();
			return this;
		}
			
		OperatorTree ot = new OperatorTree(op);

		if(op.equals("+") || op.equals("-")) {
			boolean first = true;
			for(OTEntity e : entities) {
				if(e instanceof OTSubtree)
					e = ((OTSubtree) e).content.removeObsolete().asEntity();
				if(!e.asTree().isZero() || (op.equals("-") && first))
					ot.entities.add(e);
				first = false;
			}
		}
		else if(op.equals("∙") || op.equals("/")) {
			boolean first = true;
			for(OTEntity e : entities) {
				if(e instanceof OTSubtree)
					e = ((OTSubtree) e).content.removeObsolete().asEntity();
				if(!e.asTree().isOne() || (op.equals("/") && first))
					ot.entities.add(e);
				first = false;
			}
		}
		else {
			for(OTEntity e : entities) {
				if(e instanceof OTSubtree)
					e = ((OTSubtree) e).content.removeObsolete().asEntity();
				ot.entities.add(e);
			}
		}
		
		return ot;
	}
	
    OperatorTree mergeOps(Set<String> ops) {
    	if(entities.size() == 1 && entities.get(0) instanceof OTSubtree)
    		return ((OTSubtree) entities.get(0)).content.mergeOps(ops);
    	
    	OperatorTree ot = new OperatorTree(op);
    	for(OTEntity e : entities) {
    		if(e instanceof OTRawString)
    			ot.entities.add(e);
    		else {
    			OperatorTree subtree = ((OTSubtree) e).content.mergeOps(ops);
    			if(subtree.op.equals(ot.op) && ops.contains(op) && !subtree.canBeInterpretedAsUnaryPrefixed())
    				ot.entities.addAll(subtree.entities);
    			else
    				ot.entities.add(new OTSubtree(subtree));
    		}
    	}
    	return ot;
    }
    
    OperatorTree mergeOpsFromRight(Set<String> ops) {
    	if(entities.size() == 1 && entities.get(0) instanceof OTSubtree)
    		return ((OTSubtree) entities.get(0)).content.mergeOpsFromRight(ops);
    	
    	OperatorTree ot = new OperatorTree();
    	boolean first = true;
    	ot.op = op;
    	for(OTEntity e : entities) {
    		if(e instanceof OTRawString)
    			ot.entities.add(e);
    		else {
    			OperatorTree subtree = ((OTSubtree) e).content.mergeOpsFromRight(ops);
    			if(first && subtree.op.equals(ot.op) && ops.contains(op) && !subtree.canBeInterpretedAsUnaryPrefixed())
    				ot = subtree;
    			else
    				ot.entities.add(new OTSubtree(subtree));
    		}
    		first = false;
    	}
    	return ot;
    }        
    
    OperatorTree mergeOps(String ops) { return mergeOps(OTParser.simpleParseOps(ops)); }        
    OperatorTree mergeOpsFromRight(String ops) { return mergeOpsFromRight(OTParser.simpleParseOps(ops)); }
    OperatorTree simplify() { return mergeOps("=+∙").mergeOpsFromRight("-/").removeObsolete(); }
    
    OperatorTree transformOp(String oldOp, String newOp, Utils.Function<OTEntity,OTEntity> leftTransform, Utils.Function<OTEntity,OTEntity> rightTransform) {
    	OperatorTree ot = new OperatorTree();
    	if(!op.equals(oldOp) || canBeInterpretedAsUnaryPrefixed()) {
    		ot.op = op;
        	for(OTEntity e : entities) {
        		if(e instanceof OTSubtree)
        			ot.entities.add( new OTSubtree( ((OTSubtree) e).content.transformOp(oldOp, newOp, leftTransform, rightTransform), ((OTSubtree) e).explicitEnclosing ) );
        		else // e instanceof RawString
        			ot.entities.add(e);
        	}
        	return ot;
    	}
    	
    	// op == oldOp here 
    	ot.op = newOp;
    	boolean first = true;
    	for(OTEntity e : entities) {
    		if(e instanceof OTSubtree)
    			e = new OTSubtree( ((OTSubtree) e).content.transformOp(oldOp, newOp, leftTransform, rightTransform), ((OTSubtree) e).explicitEnclosing );
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
    
    OperatorTree transformMinusToPlus() {
		return transformOp("-", "+", null,
				new Utils.Function<OTEntity,OTEntity>() {
					public OTEntity eval(OTEntity obj) {
						return new OTSubtree(obj.prefixed("-"));
					}
				});
    }
    
    OperatorTree transformMinusPushedDown() {
    	OTEntity e = transformMinusPushedDown(false);
    	if(e instanceof OTSubtree)
    		return ((OTSubtree) e).content;
    	return new OperatorTree("", e);
    }
    OTEntity transformMinusPushedDown(boolean negate) {
    	if(canBeInterpretedAsUnaryPrefixed() && op.equals("-")) {        		
    		OTEntity e = unaryPrefixedContent();
    		negate = !negate;
    		if(e instanceof OTSubtree)
    			return ((OTSubtree) e).content.transformMinusPushedDown(negate);
    		if(negate)
    			return new OTSubtree(e.prefixed("-"));
    		return e;
    	}
    	else {
    		OperatorTree ot = new OperatorTree();
    		ot.op = op;
    		for(OTEntity e : entities) {
    			if(e instanceof OTSubtree) {
    				OTSubtree origSubtree = (OTSubtree) e;
    				e = origSubtree.content.transformMinusPushedDown(negate);
    				if(e instanceof OTSubtree)
    					((OTSubtree) e).explicitEnclosing = origSubtree.explicitEnclosing;
    			}
    			else {
    				if(negate)
    					e = new OTSubtree(e.prefixed("-"));
    			}
    			ot.entities.add(e);
    			// don't negate further entries if this is a multiplication/division
    			if(op.equals("∙") || op.equals("/")) negate = false;
    		}
    		return new OTSubtree(ot);
    	}
    }
    
	OperatorTree sum(OperatorTree other) {
		if(this.isZero()) return other;
		if(other.isZero()) return this;

		if((this.op.equals("+") || this.entities.size() == 1) && (other.op.equals("+") || other.entities.size() == 1))
			return Sum(new LinkedList<OTEntity>(Utils.concatCollectionView(entities, other.entities)));
					
		if(this.op.equals("+"))
			return Sum(new LinkedList<OTEntity>(Utils.concatCollectionView(this.entities, Utils.listFromArgs(other.asEntity()))));

		if(other.op.equals("+"))
			return Sum(new LinkedList<OTEntity>(Utils.concatCollectionView(Utils.listFromArgs(this.asEntity()), other.entities)));

		return Sum(Utils.listFromArgs(this.asEntity(), other.asEntity()));
	}

    OperatorTree minusOne() {
    	if(canBeInterpretedAsUnaryPrefixed() && op.equals("-"))
    		return unaryPrefixedContent().asTree();
    	
    	OTEntity e = transformMinusPushedDown(true);
    	return e.asTree();
	}

	OTEntity asEntity() {
		if(entities.size() == 1)
			return entities.get(0);
		return new OTSubtree(this);
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
			for(OTEntity e : entities) ot.entities.add(e);
			ot.entities.add(other.asEntity());
			return ot;
		}
		return new OperatorTree("/", Utils.listFromArgs(asEntity(), other.asEntity()));
	}
	
	OperatorTree mergeDivisions() {
		if(entities.size() == 1) {
			OTEntity e = entities.get(0);
			if(e instanceof OTSubtree)
				return ((OTSubtree)e).content.mergeDivisions();
			return this;
		}
		
		if(op.equals("∙")) {
			OperatorTree nom = One(), denom = One();
			for(OTEntity e : entities) {
				OperatorTree ot = e.asTree().mergeDivisions();
				if(ot.op.equals("/")) {
					int i = 0;
					for(OTEntity e2 : ot.entities) {
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
			for(OTEntity e : entities) {
				OperatorTree ot = e.asTree().mergeDivisions();
				if(ot.op.equals("/")) {
					int j = 0;
					for(OTEntity e2 : ot.entities) {
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
			for(OTEntity e : entities)
				ot.entities.add(e.asTree().mergeDivisions().asEntity());
			return ot;
		}
	}
	
	OperatorTree canRemoveFactor(OperatorTree fac) {
		if(equals(fac)) return One();
		if(minusOne().equals(fac)) return Number(-1);
		if(entities.size() == 1) {
			if(entities.get(0) instanceof OTSubtree)
				return ((OTSubtree) entities.get(0)).content.canRemoveFactor(fac);
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
		for(OTEntity e : entities) {
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
	
	static Utils.Pair<Integer,Integer> simplifyDivisionFactor(List<OTEntity> nomProd, List<OTEntity> denomProd) {
		Utils.Pair<Integer,Integer> nomDenomFacs = new Utils.Pair<Integer,Integer>(1,1);
		int fac = 1;
		for(int i = 0; i < nomProd.size(); ++i) {
			OTEntity e = nomProd.get(i);
			if(e.asTree().isNegative()) {
				nomProd.set(i, e.asTree().minusOne().asEntity());
				fac *= -1;
				nomDenomFacs.first *= -1;
				e = nomProd.get(i);
			}
			nomProd.set(i, e.asTree().simplify().asEntity());
		}
		for(int i = 0; i < denomProd.size(); ++i) {
			OTEntity e = denomProd.get(i);
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
			for(OTEntity e : new ArrayList<OTEntity>(Utils.concatCollectionView(denomProd.entities, Utils.listFromArgs(denom.asEntity())))) {
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
		for(OTEntity e : entities) {
			if(e instanceof OTSubtree) {
				OperatorTree next = ((OTSubtree) e).content.nextDivision();
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
    	for(OTEntity e : entities) {
    		if(e instanceof OTSubtree && e.asTree().haveDenominatorInSubtree(denom))
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
    		OTEntity e = unaryPrefixedContent();
    		if(e instanceof OTSubtree)
    			return ((OTSubtree) e).content.multiply(other).prefixed(op);
    		ot.op = "∙";
    		ot.entities.add(asEntity());
    		ot.entities.add(other.asEntity());
    		return ot;
    	}
    	
    	if(entities.size() == 1) {
    		OTEntity e = entities.get(0);
    		if(e instanceof OTSubtree)
    			return ((OTSubtree) e).content.multiply(other);
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
            	for(OTEntity e : entities) {
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
    	for(OTEntity e : entities)
    		ot.entities.add(e.asTree().multiply(other).asEntity());
    	return ot;
    }
    
    OperatorTree pushdownMultiplication(OperatorTree other) {
    	if(this.isOne()) return other;
		if((op.equals("+") || op.equals("-")) && !canBeInterpretedAsUnaryPrefixed()) {
			OperatorTree newOt = new OperatorTree(op);
			for(OTEntity e : entities) {
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
			for(OTEntity e : other.entities) {
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
    		for(OTEntity e : entities)
    			ot = ot.pushdownMultiplication(e.asTree().pushdownAllMultiplications());
    		return ot;
    	}
    	else {
        	OperatorTree ot = new OperatorTree(op);
        	for(OTEntity e : entities) {
        		if(e instanceof OTSubtree) {
        			OperatorTree subtree = ((OTSubtree) e).content.pushdownAllMultiplications();
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
