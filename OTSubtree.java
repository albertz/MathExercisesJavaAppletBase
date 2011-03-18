package applets.Termumformungen$in$der$Technik_08_Ethanolloesungen;

class OTSubtree extends OTEntity {
	OperatorTree content;
	boolean explicitEnclosing = false;
	OTSubtree(OperatorTree t) { content = t; }
	OTSubtree(OperatorTree t, boolean explicitEnclosing) { content = t; this.explicitEnclosing = explicitEnclosing; }
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

		int parentOpIdx = Utils.getIndex(OTParser.parseOpList(OperatorTree.knownOps), parentOp);
		int childOpIdx = Utils.getIndex(OTParser.parseOpList(OperatorTree.knownOps), content.op);
		if(parentOpIdx < 0 || childOpIdx < 0) return "(" + content.toString() + ")";

		if(childOpIdx >= parentOpIdx) return content.toString();
		return "(" + content.toString() + ")";
	}
	@Override OperatorTree asTree() { return content; }
	Object getContent() { return content; }
	public int compareTo(OTEntity o) {
		if(o instanceof OTSubtree) return content.compareTo(((OTSubtree) o).content);
		return 1; // not subtree, i.e. rawstring, i.e. greater
	}
}
