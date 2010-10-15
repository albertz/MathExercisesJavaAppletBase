package applets.Termumformungen$in$der$Technik_01_URI;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import com.sun.corba.se.spi.copyobject.CopierManager;

public class Utils {

	static interface Predicate<T> {
		boolean apply(T obj);
	}
	
	static <T> Iterable<T> filter(final Iterable<T> i, final Predicate<? super T> pred) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					Iterator<T> last = i.iterator(), curr = i.iterator();
					int lastDistToCurr = 0;
					T nextObj = null;
					
					public boolean hasNext() { return advance(); }
					public void remove() { last.remove(); }
					
					private boolean advance() {
						if(nextObj != null) return true;
						while(curr.hasNext()) {
							lastDistToCurr++;
							nextObj = curr.next();
							if(pred.apply(nextObj))
								return true;
						}
						nextObj = null;
						return false;
					}
					
					public T next() {
						if(advance()) {
							T obj = nextObj;
							nextObj = null;
							while(lastDistToCurr > 0) { last.next(); --lastDistToCurr; }
							return obj;
						}
						throw new NoSuchElementException();
					}
				};
			}
		};
	}
	
	static <T> List<T> list(Iterable<T> i) {
		List<T> l = new LinkedList<T>();
		for(T o : i) l.add(o);
		return l;
	}
	
	static class Pair <T1,T2> {
		T1 first;
		T2 second;
		public Pair(T1 first, T2 second) {
			this.first = first;
			this.second = second;
		}
	}
	
	static <T1,T2> Iterable<Pair<T1,T2>> zip(final Iterable<T1> i1, final Iterable<T2> i2) {
		return new Iterable<Pair<T1,T2>>() {
			public Iterator<Pair<T1, T2>> iterator() {
				return new Iterator<Pair<T1,T2>>() {
					Iterator<T1> it1 = i1.iterator();
					Iterator<T2> it2 = i2.iterator();
					
					public boolean hasNext() { return it1.hasNext() && it2.hasNext(); }
					public Pair<T1, T2> next() { return new Pair<T1,T2>(it1.next(), it2.next()); }
					public void remove() { it1.remove(); it2.remove(); }
				};
			}
		};
	}
	
    static <T> Set<T> minSet(Iterable<? extends T> it, Comparator<? super T> comp) {
    	Iterator<? extends T> i = it.iterator();
    	Set<T> candidates = new HashSet<T>();

    	while (i.hasNext()) {
    		T next = i.next();
    		if(candidates.isEmpty())
    			candidates.add(next);
    		else {
    			int c = comp.compare(next, candidates.iterator().next());
    			if(c < 0) {
    				candidates.clear();
    				candidates.add(next);
    			}
    			else if(c == 0)
    				candidates.add(next);
    		}
    	}
    	
    	return candidates;
    }

    static <T> int indexInArray(T[] a, T obj) {
    	for(int i = 0; i < a.length; ++i)
    		if(a[i] == obj) return i;
    	return -1;
    }
    
	static interface Function<X,Y> {
		Y eval(X obj);
	}
    
	static interface CopyableIterator<X> extends Iterator<X>, Cloneable {}
	
    static <X,Y> List<Y> map(Iterable<? extends X> coll, Function<X,Y> func) {
    	List<Y> l = new LinkedList<Y>();
    	for(X obj : coll) l.add(func.eval(obj));
    	return l;
    }
    
    static <X> String concat(Iterable<X> coll, String seperator) {
    	String s = ""; Iterator<X> i = coll.iterator();
    	if(i.hasNext()) s += i.next().toString();
    	while(i.hasNext()) {
    		s += seperator;
    		s += i.next().toString();
    	}
    	return s;
    }

	static class StringIterator implements Iterator<Character> {
		String str; int pos = 0;
		StringIterator(String str) { this.str = str; }
		public boolean hasNext() { return pos < str.length(); }
		public Character next() { return str.charAt(pos++); }
		public void remove() { throw new AssertionError("removing in string iterator not supported"); }
	}
    
	static Iterable<Character> iterableString(final String s) {
		return new Iterable<Character>() {
			public Iterator<Character> iterator() { return new StringIterator(s); }
		};
	}
	
    static <T> T castOrNull(Object obj, Class<T> clazz) {
        if(clazz.isInstance(obj))
            return clazz.cast(obj);
        return null;
    }
	
    static class ParseTree {
    	static abstract class Entity { abstract Entity trim(); }
    	static class Subtree extends Entity {
    		String prefix, postfix;
    		ParseTree content = new ParseTree();
    		Entity trim() {
    			Subtree t = new Subtree();
    			t.prefix = prefix; t.postfix = postfix;
    			t.content = content.trim();
    			return t;
    		}
    	}
    	static class RawString extends Entity {
    		String content = "";
    		RawString() {}
    		RawString(String s) { content = s; }
    		Entity trim() { return new RawString(content.trim()); }
    	}
    	List<Entity> entities = new LinkedList<Entity>();
    	
    	void parse(Iterator<Character> i, Map<String,String> bracketTypes, String stoppingBracket) {
    		for(String s : bracketTypes.keySet()) if(s.length() != 1) throw new AssertionError("bracketsize != 1 currently not supported");
    		for(String s : bracketTypes.values()) if(s.length() != 1) throw new AssertionError("bracketsize != 1 currently not supported");

    		RawString lastRaw = null;
    		while(i.hasNext()) {
    			char c = i.next();
    			if(stoppingBracket.equals("" + c)) return;
    			if(bracketTypes.containsKey("" + c)) {
    				lastRaw = null;
    				Subtree subtree = new Subtree();
    				subtree.prefix = "" + c;
    				subtree.postfix = bracketTypes.get("" + c);
    				entities.add(subtree);
    				subtree.content.parse(i, bracketTypes, subtree.postfix);
    			}
    			else {
    				if(lastRaw == null) {
    					lastRaw = new RawString();
    					entities.add(lastRaw);
    				}
    				lastRaw.content += c;
    			}
    		}
    	}

    	void parse(Iterator<Character> i, Map<String,String> bracketTypes) { parse(i, bracketTypes, ""); }
    	void parse(String str, Map<String,String> bracketTypes) { parse(new StringIterator(str), bracketTypes); }
    	
    	void parse(String str, String bracketTypes) {
    		if(bracketTypes.length() % 2 == 1) throw new AssertionError("bracketTypes string len must be a multiple of 2");
    		Map<String,String> bracketTypesMap = new HashMap<String,String>();
    		for(int i = 0; i < bracketTypes.length(); i += 2)
    			bracketTypesMap.put("" + bracketTypes.charAt(i), "" + bracketTypes.charAt(i+1));
    		parse(str, bracketTypesMap);
		}
    	
    	void parse(String str) { parse(str, "()[]{}\"\""); }
    	
    	ParseTree() {}
    	ParseTree(String str) { parse(str); }
    	
    	ParseTree trim() {
    		ParseTree t = new ParseTree();
    		for(Entity e : entities) t.entities.add(e.trim());
    		return t;
    	}
    	void removeEmptyRawStrings() {
    		for(Iterator<Entity> e = entities.iterator(); e.hasNext();) {
    			RawString s = castOrNull(e.next(), RawString.class);
    			if(s != null && s.content.isEmpty()) e.remove();
    		}
    	}
    	List<ParseTree> split(Set<String> ss) {
    		for(String s : ss) if(s.length() != 1) throw new AssertionError("str len != 1 not supported currently");
    		List<ParseTree> l = new LinkedList<ParseTree>();
    		{
	    		ParseTree t = new ParseTree();
	    		l.add(t);
	    		for(Entity e : entities) {
	    			if(e instanceof RawString) {
	    				String rs = ((RawString)e).content;
	    				RawString lastStr = new RawString();
	    				t.entities.add(lastStr);
	    				for(Character c : iterableString(rs)) {
	    					if(ss.contains("" + c)) {
	    						t.removeEmptyRawStrings();
	            				t = new ParseTree();
	            				l.add(t);
	            				lastStr = new RawString();
	            				t.entities.add(lastStr);            				
	    					}
	    					else
	    						lastStr.content += c;
	    				}
	    			}
	    			else
	    				t.entities.add(e);
	    		}
	    		t.removeEmptyRawStrings();
    		}
    		// remove empty trees
    		for(Iterator<ParseTree> i = l.iterator(); i.hasNext();) {
    			ParseTree t = i.next();
    			if(t.entities.isEmpty()) i.remove();
    		}	
    		return l;
    	}
    	List<ParseTree> split(String s) { Set<String> ss = new HashSet<String>(); ss.add(s); return split(ss); }
        
    	static ParseTree merge(List<ParseTree> tl) {
    		ParseTree finalt = new ParseTree();
    		for(ParseTree t : tl) finalt.entities.addAll(t.entities);
    		return finalt;
    	}
    }
    
    static class OperatorTree {
    	String op;
    	abstract static class Entity {}
    	static class RawString extends Entity {
    		String content = "";
    		RawString() {}
    		RawString(String s) { content = s; }
    		@Override public String toString() { return content; }
    	}
    	static class Subtree extends Entity {
    		OperatorTree content;
    		Subtree(OperatorTree t) { content = t; }
    		@Override public String toString() { return "(" + content.toString() + ")"; }
    	}
    	List<Entity> entities = new LinkedList<Entity>();
    	OperatorTree() {}
    	OperatorTree(String op, Entity e) { this.op = op; entities.add(e); } 
    	
    	static OperatorTree parseNonNestedString(OperatorTree ot, String s, Set<String> equalPriorityOps) {
			boolean havePreviousContent = !ot.entities.isEmpty();
    		RawString lastStr = new RawString();
			ot.entities.add(lastStr);
			for(Character c : iterableString(s)) {
				if(havePreviousContent && equalPriorityOps.contains("" + c)) {
					lastStr.content = lastStr.content.trim();
					lastStr = new RawString();
					havePreviousContent = false;
					
					final String op = "" + c;
					if(!ot.op.equals(op))
						ot = new OperatorTree(op, new Subtree(ot));
					ot.entities.add(lastStr);
				}
				else if(!lastStr.content.isEmpty() || c != ' ') {
					lastStr.content += c;
					havePreviousContent = true;	
				}
			}
			lastStr.content = lastStr.content.trim();
			return ot;
    	}
    	
    	static Entity parseNonNestedString(String s, List<Set<String>> ops) {
    		OperatorTree ot = parseNonNestedString(new OperatorTree(), s, ops);
    		if(ot.entities.size() == 0) throw new AssertionError("something is wrong");
    		else if(ot.entities.size() == 1) return ot.entities.get(0);
    		return new Subtree(ot);
    	}
    	    	
    	static OperatorTree parseNonNestedString(final OperatorTree origOt, String s, List<Set<String>> ops) {
    		OperatorTree ot = origOt;
    		int startIndex = ot.entities.size();
    		int opIndex = 0;
	    	ot = parseNonNestedString(ot, s, ops.get(opIndex++));
    		if(ot != origOt) startIndex = 0;
    		if(ot.entities.size() - startIndex == 0) throw new AssertionError("something is wrong, no addition for " + ops.get(opIndex-1));	    	

    		
    		
    		for(; opIndex < ops.size(); ++opIndex) {
	    		for(int i = startIndex; i < ot.entities.size(); ++i) {
	    			RawString rs = castOrNull(ot.entities.get(i), RawString.class);
	    			if(rs != null) {
	    				//if(i > 0)
	    				//	ot.entities.set(i, parseNonNestedString(rs.content, ops.get(opIndex)));
	    				//else {
	    				//	ot = parseNonNestedString();
	    				//}
	    			}
	    		}
    		}
    		return ot;
    	}
    	    	
    	static OperatorTree parse(OperatorTree t, List<Set<String>> ops) {
    		OperatorTree ot = new OperatorTree();
    		ot.op = t.op;
    		for(Entity e : t.entities) {
    			if(e instanceof RawString) {
					String rs = ((RawString)e).content;
    				if(ot.op.isEmpty())
    					ot = parseNonNestedString(ot, rs, ops);
    				else
    					ot.entities.add(parseNonNestedString(rs, ops));
    			}
    			else {
    				OperatorTree subtree = ((Subtree)e).content;
    				if(!ot.entities.isEmpty()) {
    					RawString lastRs = castOrNull(ot.entities.get(ot.entities.size() - 1), RawString.class);
    					if(lastRs == null) throw new AssertionError("'(..)(..)' not allowed, there must be something in between");
	    				if(!lastRs.content.isEmpty())
	    					// for example, there was a "-" just before, e.g. "- ( ..."
	    					subtree = new OperatorTree(lastRs.content, new Subtree(subtree));
    					ot.entities.remove(ot.entities.size() - 1);
    				}
					ot.entities.add( new Subtree( parse(subtree, ops) ) );
    			}
    		}
    		return ot;
    	}
    	
    	static OperatorTree undefinedOpTreeFromParseTree(ParseTree t) {
    		OperatorTree ot = new OperatorTree();
    		for(ParseTree.Entity e : t.entities) {
    			if(e instanceof ParseTree.RawString)
    				ot.entities.add( new RawString(((ParseTree.RawString)e).content) );
    			else
    				ot.entities.add( new Subtree( undefinedOpTreeFromParseTree(((ParseTree.Subtree)e).content) ) );
    		}
    		return ot;
    	}
    	    	
    	static List<Set<String>> parseOpList(String ops) {
    		List<Set<String>> opl = new LinkedList<Set<String>>();
    		Set<String> curEquOps = new HashSet<String>();
			opl.add(curEquOps);
    		for(Character c : iterableString(ops)) {
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
    	
    	static OperatorTree parse(String str, String ops) { return parse( undefinedOpTreeFromParseTree(new ParseTree(str)), parseOpList(ops) ); }
    	static OperatorTree parse(String str) { return parse(str, "= +- */"); }
    	
    	@Override public String toString() { return (entities.size() == 1) ? (op + entities.get(0).toString()) : concat(entities, " " + op + " "); }
    }
}
