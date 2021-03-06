/*
 *  MathExercisesJavaAppletBase
 * 
 * by Albert Zeyer / developed for Lehrstuhl A für Mathematik at the RWTH Aachen University
 * code under GPLv3+
 */
package applets.Termumformungen$in$der$Technik_08_Ethanolloesungen;


abstract class OTEntity implements Comparable<OTEntity> {
	OperatorTree asTree() { return new OperatorTree("", this); }
	OperatorTree prefixed(final String op) {
		// empty subtree at the beginning is the mark for a prefix op
		OperatorTree ot = new OperatorTree(op, new OTSubtree(new OperatorTree()));
		ot.entities.add(this);
		return ot;
	}
    String toString(String parentOp) { return toString(); }
	abstract Object getContent();
	@Override public int hashCode() { return 31 + getContent().hashCode(); }
	@Override public boolean equals(Object obj) {
		if(!(obj instanceof OTEntity)) return false;
		return compareTo((OTEntity) obj) == 0;
	}
}
