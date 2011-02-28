package applets.Termumformungen$in$der$Technik_03_Logistik;

class OTSubtree extends OTEntity {
	OperatorTree content;
	boolean explicitEnclosing = false;
	OTSubtree(OperatorTree t) { content = t; }
	OTSubtree(OperatorTree t, boolean explicitEnclosing) { content = t; this.explicitEnclosing = explicitEnclosing; }
	@Override boolean isEnclosedImplicitely() { return explicitEnclosing; }
	@Override public String toString() {
		if(OperatorTree.debugOperatorTreeDump || explicitEnclosing)
			return "(" + content.toString() + ")";
		if(content.entities.size() == 1)
			return content.toString();
		if(content.canBeInterpretedAsUnaryPrefixed())
			return content.toString();
		return "(" + content.toString() + ")";
	}
	@Override String toString(String parentOp) {
		if(OperatorTree.debugOperatorTreeDump || explicitEnclosing)
			return "(" + content.toString() + ")";
		if(content.entities.size() == 1)
			return content.toString();
		if(content.canBeInterpretedAsUnaryPrefixed())
			return content.toString();
		final String ops = "/∙+-=";
		int parentOpIdx = ops.indexOf(parentOp);
		int childOpIdx = ops.indexOf(content.op);
		if(parentOpIdx < 0 || childOpIdx < 0) return "(" + content.toString() + ")";
		if(parentOpIdx == 3) parentOpIdx--; if(childOpIdx == 3) childOpIdx--; // take +- as equal
		if(childOpIdx <= parentOpIdx) return content.toString();
		return "(" + content.toString() + ")";
	}
	@Override OperatorTree asTree() { return content; }
	Object getContent() { return content; }
	public int compareTo(OTEntity o) {
		if(o instanceof OTSubtree) return content.compareTo(((OTSubtree) o).content);
		return 1; // not subtree, i.e. rawstring, i.e. greater
	}
}