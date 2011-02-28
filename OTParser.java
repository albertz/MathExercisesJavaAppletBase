package applets.Termumformungen$in$der$Technik_03_Logistik;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class OTParser {

	static OperatorTree grabUnaryPrefixedOpFromSplitted(OperatorTree splitted) {
		OperatorTree ot = new OperatorTree();
		boolean nextOpWanted = false;
		for(Iterator<OperatorTree.Entity> eit = splitted.entities.iterator(); eit.hasNext();) {
			OperatorTree.Entity e = eit.next();
			if(e instanceof OperatorTree.RawString) {
				String op = ((OperatorTree.RawString) e).content;
				if(!nextOpWanted) {
					//System.out.println("prefix: " + op + " in " + splitted);
					ot.entities.add(new OperatorTree.Subtree(grabUnaryPrefixedOpFromSplitted(op, eit)));
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
				obj = new OperatorTree.Subtree(grabUnaryPrefixedOpFromSplitted(((OperatorTree.RawString) e).content, rest));    			
		}
		else
			// we have expected another entry but there is none anymore.
			// add a dummy empty subtree so that it looks like an unary prefixed op.
			obj = new OperatorTree.Subtree(new OperatorTree());
		
		return obj.prefixed(op);
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
					ot = new OperatorTree(op, new OperatorTree.Subtree(ot));
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
					ot.entities.add(new OperatorTree.Subtree(subtree));
				nextOpWanted = true;
			}				
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

	static OperatorTree parse(String str) { return parse(str, "∙"); }

	static OperatorTree parse(String str, String defaultAnonBinOp) { return parse(str.replace('*', '∙').replace(',', '.'), "= +- ∙/", defaultAnonBinOp); }

	static OperatorTree parse(String str, String binOps, String defaultAnonBinOp) {
		return parse( undefinedOpTreeFromParseTree(new ParseTree(str)), parseOpList(binOps), defaultAnonBinOp );
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
				ot.entities.add( new OperatorTree.Subtree( parseDefaultAnonBinOpInTree( ((OperatorTree.Subtree) e).content, defaultOp ), ((OperatorTree.Subtree) e).explicitEnclosing ) );
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

	static OperatorTree.Entity parseOpsInEntity(final OperatorTree.Entity source, final Set<String> binOps, List<Set<String>> binOpList) {
		if(source instanceof OperatorTree.RawString) {
			OperatorTree ot = parseOpsInTree(new OperatorTree("", source), binOps, binOpList);
			if(ot.entities.size() == 0) throw new AssertionError("something is wrong");
			else if(ot.entities.size() == 1) return ot.entities.get(0);
			return new OperatorTree.Subtree(ot);        		
		}
		else
			return new OperatorTree.Subtree(parseOpsInTree(((OperatorTree.Subtree)source).content, binOps, binOpList), ((OperatorTree.Subtree) source).explicitEnclosing);
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
					ot.entities.add(new OperatorTree.Subtree(parseOpsInTree( ((OperatorTree.Subtree) e).content, binOps, binOpList ), ((OperatorTree.Subtree) e).explicitEnclosing));
			}
			return ot;
		}
		
		splitted = grabUnaryPrefixedOpFromSplitted(splitted);
		//System.out.println("after unary prefix grab: " + splitted);
		splitted = handleOpsInSplitted(splitted, binOpList);
		//System.out.println("after op handling: " + splitted);
		return parseOpsInTree( splitted, binOps, binOpList );
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
							lastSubtree.entities.add(new OperatorTree.RawString(lastStr));
							lastStr = "";
						}
						
						if(!lastSubtree.entities.isEmpty()) {
				    		res.entities.add(new OperatorTree.Subtree(lastSubtree));
				    		lastSubtree = new OperatorTree();
						}
						
						res.entities.add(new OperatorTree.RawString("" + c));
					}
					else if(c != ' ')							
						lastStr += c;
					else {
						if(!lastStr.isEmpty()) {
							lastSubtree.entities.add(new OperatorTree.RawString(lastStr));
							lastStr = "";
						}							
					}
				}
				if(!lastStr.isEmpty())
					lastSubtree.entities.add(new OperatorTree.RawString(lastStr));
			}
			else { // e instanceof Subtree
				lastSubtree.entities.add(e);
			}
		}
		if(!lastSubtree.entities.isEmpty())
			res.entities.add(new OperatorTree.Subtree(lastSubtree));			
		
		return res;
	}

	static OperatorTree splitSplittedByOps(final OperatorTree splitted, Set<String> ops) {
		OperatorTree ot = new OperatorTree();
		OperatorTree lastSubtree = new OperatorTree();
		
		for(OperatorTree.Entity e : splitted.entities) {
			if(e instanceof OperatorTree.RawString) {
				String op = ((OperatorTree.RawString) e).content;
				if(ops.contains(op)) {
					if(!lastSubtree.entities.isEmpty())
			    		ot.entities.add(new OperatorTree.Subtree(lastSubtree));
					ot.entities.add(e);
					lastSubtree = new OperatorTree();
					continue;
				}
			}
			lastSubtree.entities.add(e);
		}
		if(!lastSubtree.entities.isEmpty())
			ot.entities.add(new OperatorTree.Subtree(lastSubtree));
		return ot;
	}

	static OperatorTree undefinedOpTreeFromParseTree(ParseTree t) {
		OperatorTree ot = new OperatorTree();
		for(ParseTree.Entity e : t.entities) {
			if(e instanceof ParseTree.RawString)
				ot.entities.add( new OperatorTree.RawString(((ParseTree.RawString)e).content) );
			else
				ot.entities.add( new OperatorTree.Subtree( undefinedOpTreeFromParseTree(((ParseTree.Subtree)e).content), true ) );
		}
		return ot;
	}

}
