package applets.ganze$und$natuerliche$Zahlen_Primzahlen;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Iterator;






public class FileIterator implements Iterator, Iterable {
	private final Iterator files;
	private final FileFilter filter;
	private FileIterator child = null;
	private FileIterator parent = null;
	
	FileIterator(File file, FileFilter filter) {
		this.files = Arrays.asList(file.listFiles(filter)).iterator();
		this.filter = filter;
	}

	private FileIterator getLastChild() {
		if(child != null)
			return child.getLastChild();
		else
			return this;
	}
	
	private FileIterator getRoot() {
		if(parent != null)
			return parent.getRoot();
		else
			return this;
	}
	
	// returns false if empty
	private boolean gotoParentIfEmpty() {
		if(!this.files.hasNext()) {
			if(parent == null) return false;
			parent.child = null;
			return parent.gotoParentIfEmpty();
		} else
			return true;
	}
	
	private File next_lastChild() {
		File next = (File) this.files.next();

		if (next.isDirectory()) {
			child = new FileIterator(next, this.filter);
			child.parent = this;
		}
		
		return next;
	}
	
	private boolean hasNext_lastChild() {
		return gotoParentIfEmpty();
	}
	
	public void remove() {
	}

	public Object next() {
		if(!getLastChild().gotoParentIfEmpty()) return null;
		return getLastChild().next_lastChild();
	}

	public boolean hasNext() {
		return getLastChild().hasNext_lastChild();
	}

	public Iterator iterator() {
		return this;
	}

}
