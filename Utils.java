package applets.Termumformungen$in$der$Technik_01_URI;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

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
    
    static <X,Y> List<Y> map(Iterable<? extends X> coll, Function<X,Y> func) {
    	List<Y> l = new LinkedList<Y>();
    	for(X obj : coll) l.add(func.eval(obj));
    	return l;
    }
    
}
