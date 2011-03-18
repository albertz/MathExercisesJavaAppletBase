package applets.Termumformungen$in$der$Technik_08_Ethanolloesungen;

import java.util.*;


public class OTParser {

	static OperatorTree grabUnaryPrefixedOpFromSplitted(OperatorTree splitted) {
		OperatorTree ot = new OperatorTree();
		boolean nextOpWanted = false;
		for(Iterator<OTEntity> eit = splitted.entities.iterator(); eit.hasNext();) {
			OTEntity e = eit.next();
			if(e instanceof OTRawString) {
				String op = ((OTRawString) e).content;
				if(!nextOpWanted) {
					//System.out.println("prefix: " + op + " in " + splitted);
					ot.entities.add(new OTSubtree(grabUnaryPrefixedOpFromSplitted(op, eit)));
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

	static OperatorTree grabUnaryPrefixedOpFromSplitted(final String op, final Iterator<OTEntity> rest) {
		OTEntity obj;
		
		if(rest.hasNext()) {
			OTEntity e = rest.next();
			if(e instanceof OTSubtree) {
				OperatorTree eSubtree = ((OTSubtree) e).content;
				if(eSubtree.entities.size() == 1)
					obj = eSubtree.entities.get(0);
				else
					obj = e;
			} else // e instanceof RawString -> another unary prefix
				obj = new OTSubtree(grabUnaryPrefixedOpFromSplitted(((OTRawString) e).content, rest));    			
		}
		else
			// we have expected another entry but there is none anymore.
			// add a dummy empty subtree so that it looks like an unary prefixed op.
			obj = new OTSubtree(new OperatorTree());
		
		return obj.prefixed(op);
	}

	static OperatorTree handleOpsInSplitted(OperatorTree splitted, List<Set<String>> binOps) {
		if(binOps.isEmpty()) {
			if(splitted.entities.size() == 1 && splitted.entities.get(0) instanceof OTSubtree)
				return ((OTSubtree) splitted.entities.get(0)).content;
			return splitted;
		}
		
		Set<String> equallyPriorityOps = binOps.get(0);
		List<Set<String>> restBinOps = binOps.subList(1, binOps.size());
		    		
		splitted = splitSplittedByOps(splitted, equallyPriorityOps);
		if(splitted.entities.size() == 1)
			return handleOpsInSplitted( ((OTSubtree) splitted.entities.get(0)).content, restBinOps );
		
		OperatorTree ot = new OperatorTree();
		boolean nextOpWanted = false;
		for(OTEntity e : splitted.entities) {
			if(e instanceof OTRawString) {
				String op = ((OTRawString) e).content;
				if(!nextOpWanted) throw new AssertionError("we should have handled that already in grabUnaryPrefixedOpFromSplitted");
				if(ot.op.isEmpty())
					ot.op = op;
				else
					// it must be an op in equallyPriorityOps because of splitSplittedByOps
					ot = new OperatorTree(op, new OTSubtree(ot));
				nextOpWanted = false;
			}
			else { // e instanceof Subtree
				if(nextOpWanted) throw new AssertionError("should not happen by the way splitByOps works; ops = " + binOps + ", splitted = " + splitted + ", current = " + ot + ", next = " + ((OTSubtree) e).content);
				OperatorTree subtree = ((OTSubtree) e).content;
				//if(subtree.entities.size() == 1 && subtree.entities.get(0) instanceof Subtree)
				//	subtree = ((Subtree) subtree.entities.get(0)).content;
				subtree = handleOpsInSplitted( subtree, restBinOps );
				if(subtree.entities.size() == 1)
					ot.entities.add(subtree.entities.get(0));
				else
					ot.entities.add(new OTSubtree(subtree));
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
		if(defaultAnonBinOp != null) t = parseDefaultAnonBinOpInTree(t, defaultAnonBinOp);
		else t = parseFunctionCalls(t);
		return t;
	}

	static OperatorTree parse(String str) { return parse(str, null); }

	static OperatorTree parse(String str, String defaultAnonBinOp) {
		return parse(str.replace('*', '∙').replace(',', '.').replace('−','-'),
				OperatorTree.knownOps, defaultAnonBinOp); }

	static OperatorTree parse(String str, String binOps, String defaultAnonBinOp) {
		return parse( undefinedOpTreeFromParseTree(new ParseTree(str)), parseOpList(binOps), defaultAnonBinOp );
	}

	static OperatorTree parseDefaultAnonBinOpInTree(final OperatorTree source, String defaultOp) {
		OperatorTree ot = new OperatorTree();
		ot.op = source.op;
		if(source.op.isEmpty() && source.entities.size() > 1)
			ot.op = defaultOp;
		
		for(OTEntity e : source.entities) {
			if(e instanceof OTRawString)
				ot.entities.add(e);
			else
				ot.entities.add( new OTSubtree( parseDefaultAnonBinOpInTree( ((OTSubtree) e).content, defaultOp ), ((OTSubtree) e).explicitEnclosing ) );
		}
		return ot;
	}

	static OperatorTree parseFunctionCalls(final OperatorTree source) {
		if(source.op.isEmpty() && source.entities.size() > 1 && source.entities.get(0) instanceof OTRawString) {
			// set the first entity as the function
			OperatorTree ot = new OperatorTree();
			ot.op = ((OTRawString) source.entities.get(0)).content;
			ot.entities.addAll(source.entities.subList(1, source.entities.size()));
			if(ot.isFunctionCall())
				return parseFunctionCalls(ot);
			// something is wrong. this can't be used as a function call. fallback here
		}
		
		OperatorTree ot = new OperatorTree();
		ot.op = source.op;
		
		for(OTEntity e : source.entities) {
			if(e instanceof OTRawString)
				ot.entities.add(e);
			else
				ot.entities.add( new OTSubtree( parseFunctionCalls( ((OTSubtree) e).content ), ((OTSubtree) e).explicitEnclosing ) );
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

	static OTEntity parseOpsInEntity(final OTEntity source, final Set<String> binOps, List<Set<String>> binOpList) {
		if(source instanceof OTRawString) {
			OperatorTree ot = parseOpsInTree(new OperatorTree("", source), binOps, binOpList);
			if(ot.entities.size() == 0) throw new AssertionError("something is wrong");
			else if(ot.entities.size() == 1) return ot.entities.get(0);
			return new OTSubtree(ot);        		
		}
		else
			return new OTSubtree(parseOpsInTree(((OTSubtree)source).content, binOps, binOpList), ((OTSubtree) source).explicitEnclosing);
	}

	static OperatorTree parseOpsInTree(final OperatorTree source, final Set<String> binOps, List<Set<String>> binOpList) {
		if(source.entities.isEmpty()) return source;

		OperatorTree ot = new OperatorTree();
		if(!source.op.isEmpty()) {
			ot.op = source.op;
			for(OTEntity e : source.entities)
				ot.entities.add(parseOpsInEntity(e, binOps, binOpList));
			return ot;
		}
		
		OperatorTree splitted = splitByOps(source, binOps);
		if(splitted.entities.size() == 1 && splitted.entities.get(0) instanceof OTSubtree) {
			// no op found -> we put the source just into one single subtree
			for(OTEntity e : ((OTSubtree) splitted.entities.get(0)).content.entities) {
				if(e instanceof OTRawString)
					ot.entities.add(e); // we already parsed that in splitByOps
				else
					ot.entities.add(new OTSubtree(parseOpsInTree( ((OTSubtree) e).content, binOps, binOpList ), ((OTSubtree) e).explicitEnclosing));
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
		
		for(OTEntity e : ot.entities) {
			if(e instanceof OTRawString) {
	    		String lastStr = "";
				for(Character c : Utils.iterableString(((OTRawString) e).content)) {
					if(equalPriorityOps.contains("" + c)) {
						if(!lastStr.isEmpty()) {
							lastSubtree.entities.add(new OTRawString(lastStr));
							lastStr = "";
						}
						
						if(!lastSubtree.entities.isEmpty()) {
				    		res.entities.add(new OTSubtree(lastSubtree));
				    		lastSubtree = new OperatorTree();
						}
						
						res.entities.add(new OTRawString("" + c));
					}
					else if(c != ' ')							
						lastStr += c;
					else {
						if(!lastStr.isEmpty()) {
							lastSubtree.entities.add(new OTRawString(lastStr));
							lastStr = "";
						}							
					}
				}
				if(!lastStr.isEmpty())
					lastSubtree.entities.add(new OTRawString(lastStr));
			}
			else { // e instanceof Subtree
				lastSubtree.entities.add(e);
			}
		}
		if(!lastSubtree.entities.isEmpty())
			res.entities.add(new OTSubtree(lastSubtree));			
		
		return res;
	}

	static OperatorTree splitSplittedByOps(final OperatorTree splitted, Set<String> ops) {
		OperatorTree ot = new OperatorTree();
		OperatorTree lastSubtree = new OperatorTree();
		
		for(OTEntity e : splitted.entities) {
			if(e instanceof OTRawString) {
				String op = ((OTRawString) e).content;
				if(ops.contains(op)) {
					if(!lastSubtree.entities.isEmpty())
			    		ot.entities.add(new OTSubtree(lastSubtree));
					ot.entities.add(e);
					lastSubtree = new OperatorTree();
					continue;
				}
			}
			lastSubtree.entities.add(e);
		}
		if(!lastSubtree.entities.isEmpty())
			ot.entities.add(new OTSubtree(lastSubtree));
		return ot;
	}

	static OperatorTree undefinedOpTreeFromParseTree(ParseTree t) {
		OperatorTree ot = new OperatorTree();
		for(ParseTree.Entity e : t.entities) {
			if(e instanceof ParseTree.RawString)
				ot.entities.add( new OTRawString(((ParseTree.RawString)e).content) );
			else
				ot.entities.add( new OTSubtree( undefinedOpTreeFromParseTree(((ParseTree.Subtree)e).content), true ) );
		}
		return ot;
	}

}
