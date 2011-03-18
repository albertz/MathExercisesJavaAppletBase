/**
 * 
 */
package applets.Termumformungen$in$der$Technik_04_Plattenkondensator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


class ParseTree {
	static abstract class Entity { abstract ParseTree.Entity trim(); }
	static class Subtree extends ParseTree.Entity {
		String prefix, postfix;
		ParseTree content = new ParseTree();
		ParseTree.Entity trim() {
			ParseTree.Subtree t = new Subtree();
			t.prefix = prefix; t.postfix = postfix;
			t.content = content.trim();
			return t;
		}
	}
	static class RawString extends ParseTree.Entity {
		String content = "";
		RawString() {}
		RawString(String s) { content = s; }
		ParseTree.Entity trim() { return new RawString(content.trim()); }
	}
	List<ParseTree.Entity> entities = new LinkedList<ParseTree.Entity>();
	
	void parse(Iterator<Character> i, Map<String,String> bracketTypes, String stoppingBracket) {
		for(String s : bracketTypes.keySet()) if(s.length() != 1) throw new AssertionError("bracketsize != 1 currently not supported");
		for(String s : bracketTypes.values()) if(s.length() != 1) throw new AssertionError("bracketsize != 1 currently not supported");

		ParseTree.RawString lastRaw = null;
		while(i.hasNext()) {
			char c = i.next();
			if(stoppingBracket.equals("" + c)) return;
			if(bracketTypes.containsKey("" + c)) {
				lastRaw = null;
				ParseTree.Subtree subtree = new Subtree();
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
	void parse(String str, Map<String,String> bracketTypes) { parse(new Utils.StringIterator(str), bracketTypes); }
	
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
		for(ParseTree.Entity e : entities) t.entities.add(e.trim());
		return t;
	}
	void removeEmptyRawStrings() {
		for(Iterator<ParseTree.Entity> e = entities.iterator(); e.hasNext();) {
			ParseTree.RawString s = Utils.castOrNull(e.next(), ParseTree.RawString.class);
			if(s != null && s.content.isEmpty()) e.remove();
		}
	}
	List<ParseTree> split(Set<String> ss) {
		for(String s : ss) if(s.length() != 1) throw new AssertionError("str len != 1 not supported currently");
		List<ParseTree> l = new LinkedList<ParseTree>();
		{
    		ParseTree t = new ParseTree();
    		l.add(t);
    		for(ParseTree.Entity e : entities) {
    			if(e instanceof ParseTree.RawString) {
    				String rs = ((ParseTree.RawString)e).content;
    				ParseTree.RawString lastStr = new RawString();
    				t.entities.add(lastStr);
    				for(Character c : Utils.iterableString(rs)) {
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
	@SuppressWarnings({"UnusedDeclaration"})
	List<ParseTree> split(String s) { Set<String> ss = new HashSet<String>(); ss.add(s); return split(ss); }
    
	@SuppressWarnings({"UnusedDeclaration"})
	static ParseTree merge(List<ParseTree> tl) {
		ParseTree finalt = new ParseTree();
		for(ParseTree t : tl) finalt.entities.addAll(t.entities);
		return finalt;
	}
}
