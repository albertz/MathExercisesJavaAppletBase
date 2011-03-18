/**
 * 
 */
package applets.Termumformungen$in$der$Technik_07_elastischerStoss;

import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class OTPositionInfo {
	int absolutePos = 0;
	OTPositionInfo.BetweenEntities parent = null;
	
	abstract OTPositionInfo next();
	int prefixLen() { return 0; }
	int postfixLen() { return 0; }
	OTPositionInfo.BetweenEntities currentTreePos() { return parent; }
	
	OTPositionInfo nextInStack() {
		if(parent == null) return null;
		OTPositionInfo.BetweenEntities pos = new BetweenEntities();
		pos.parent = parent.parent;
		pos.ot = parent.ot;
		pos.index = parent.index + 1;
		pos.absolutePos = absolutePos + postfixLen();
		return pos;
	}
	
	static class StringPos extends OTPositionInfo {
    	OTRawString rs;
    	int pos;            	

    	@Override OTPositionInfo next() {
			if(pos >= rs.content.length()) return nextInStack();
			OTPositionInfo.StringPos pos = new StringPos();
			pos.absolutePos = absolutePos + 1;
			pos.parent = parent;
			pos.rs = rs;
			pos.pos = this.pos + 1;
			return pos;
		}
    }
    static class BetweenEntities extends OTPositionInfo {
    	OperatorTree ot;
    	int index; // of following index. 0 .. ot.entities.size()
    	boolean beforeOp = true;
    	@Override int prefixLen() { return 1; /* bracket '(' */ }
    	@Override int postfixLen() { return 1; /* bracket ')' */ }
    	@Override OTPositionInfo.BetweenEntities currentTreePos() { return this; }
    	@Override OTPositionInfo next() {
			if(index >= ot.entities.size()) return nextInStack();
			if(beforeOp) {
				OTPositionInfo.BetweenEntities pos = new BetweenEntities();
				pos.ot = ot;
				pos.index = index;
				pos.beforeOp = false;
				pos.parent = parent;
				pos.absolutePos = absolutePos + 1/*space*/ + ot.op.length() + 1/*space*/;
				return pos;
			}
			OTEntity nextEntity = ot.entities.get(index);
			OTPositionInfo pos = null;
			if(nextEntity instanceof OTRawString) {
				OTPositionInfo.StringPos spos = new StringPos();
				spos.rs = (OTRawString) nextEntity;
				spos.pos = 0;
				pos = spos;
			}
			else {
				OTPositionInfo.BetweenEntities spos = new BetweenEntities();
				spos.ot = ((OTSubtree) nextEntity).content;
				spos.index = 0;
				pos = spos;
			}
			pos.absolutePos = absolutePos + pos.prefixLen();
			pos.parent = this;
    		return pos;
    	}
    }
    
    static OTPositionInfo start(OperatorTree ot) {
    	OTPositionInfo.BetweenEntities pos = new BetweenEntities();
    	pos.ot = ot;
    	return pos;
    }
    
    static OTPositionInfo posAt(OperatorTree ot, int pos) {
    	OTPositionInfo p = start(ot);
    	while(p.absolutePos < pos) {
    		OTPositionInfo next = p.next();
    		if(next == null) return p; // guarantees that we return something != null
    		p = next;
    	}
    	return p;
    }
    
    static Iterable<OTPositionInfo> position(final OperatorTree ot) {
		return new Iterable<OTPositionInfo>() {
			public Iterator<OTPositionInfo> iterator() { return positionIterator(ot); }
		};
	}
    
	static Iterator<OTPositionInfo> positionIterator(final OperatorTree ot) {
		return new Iterator<OTPositionInfo>() {
			OTPositionInfo next = start(ot);        		
			public boolean hasNext() { return next != null; }
			public OTPositionInfo next() {
				if(!hasNext()) throw new NoSuchElementException();
				OTPositionInfo res = next;
				next = next.next();
				return res;
			}
			public void remove() { throw new UnsupportedOperationException(); }        	
		};
	}
    
}
