package applets.Termumformungen$in$der$Technik_04_Plattenkondensator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;


class OTRawStringIterator implements Iterator<OTRawString> {
	static class State {
		int entityIndex = 0;
		OperatorTree ot;
		State(int i, OperatorTree t) { entityIndex = i; ot = t; }
		State(OperatorTree t) { ot = t; }
		State(OTRawStringIterator.State s) { entityIndex = s.entityIndex; ot = s.ot; }
		boolean hasNext() { return entityIndex < ot.entities.size(); }
		OTEntity current() { return ot.entities.get(entityIndex); }
		void remove() { ot.entities.remove(entityIndex); }
		void replace(OTEntity e) { ot.entities.set(entityIndex, e); }
		boolean isAtEnd() { return entityIndex == ot.entities.size() - 1; }
	}
	Stack<OTRawStringIterator.State> stateStack = new Stack<OTRawStringIterator.State>();
	OTRawStringIterator.State lastState;
	OTRawStringIterator() {}    		
	OTRawStringIterator(OperatorTree t) { stateStack.push(new State(t)); walkSubtrees(); }    		
	@SuppressWarnings("unchecked" /* because of the clone() */) OTRawStringIterator(OTRawStringIterator i) { 
		stateStack = (Stack<OTRawStringIterator.State>) i.stateStack.clone();
		lastState = i.lastState; // this is safe because we never manipulate lastState
	}
	OTRawStringIterator copy() { return new OTRawStringIterator(this); }
	static Iterable<OTRawString> iterable(final OperatorTree t) {
		return new Iterable<OTRawString>() {
			public Iterator<OTRawString> iterator() {
				return new OTRawStringIterator(t);
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
		while(!stateStack.empty() && stateStack.peek().current() instanceof OTSubtree) {
			stateStack.push(new State( ((OTSubtree)stateStack.peek().current()).content ));
			walkdown();
		}
	}
	
	public OTRawString peek() {
		if(!hasNext()) throw new NoSuchElementException();
		return (OTRawString) stateStack.peek().current();
	}
	    		
	public OTRawString next() {
		if(!hasNext()) throw new NoSuchElementException();
		
		lastState = new State(stateStack.peek());
		
		stateStack.peek().entityIndex++;
		walkSubtrees();
		
		return (OTRawString) lastState.current();
	}

	public void remove() {
		if(lastState == null) throw new IllegalStateException();
		lastState.remove();
		for(OTRawStringIterator.State s : stateStack) {
			if(s.ot == lastState.ot) {
				if(s.entityIndex == lastState.entityIndex) throw new AssertionError("should not happen that we delete currently pointed-to entry");
				if(s.entityIndex > lastState.entityIndex)
					s.entityIndex--;
			}
		}
		lastState = null;
	}
	
	// replaces last element returned by next() with e in underlying container
	public void replace(OTEntity e) {
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
