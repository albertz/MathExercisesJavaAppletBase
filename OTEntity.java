package applets.Termumformungen$in$der$Technik_03_Logistik;


abstract class OTEntity implements Comparable<OTEntity> {
	OperatorTree asTree() { return new OperatorTree("", this); }
	OperatorTree prefixed(final String op) {
		// empty subtree at the beginning is the mark for a prefix op
		OperatorTree ot = new OperatorTree(op, new OTSubtree(new OperatorTree()));
		ot.entities.add(this);
		return ot;
	}
	boolean isEnclosedImplicitely() { return true; }
	String toString(String parentOp) { return toString(); }
	abstract Object getContent();
	@Override public int hashCode() { return 31 + getContent().hashCode(); }
	@Override public boolean equals(Object obj) {
		if(!(obj instanceof OTEntity)) return false;
		return compareTo((OTEntity) obj) == 0;
	}
}