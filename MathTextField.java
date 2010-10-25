/**
 * 
 */
package applets.Termumformungen$in$der$Technik_01_URI;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class MathTextField extends JTextField {

	private static final long serialVersionUID = 3991754180692357067L;

	private Utils.OperatorTree operatorTree = new Utils.OperatorTree();
	public Utils.OperatorTree getOperatorTree() { return operatorTree; }

	public void updateByOpTree() {
		setText(operatorTree.toString());
	}
	
	protected void updateByOpTree(javax.swing.text.DocumentFilter.FilterBypass fb) {
		String oldStr = MathTextField.this.getText();
		String newStr = operatorTree.toString();
		int commonStart = Utils.equalStartLen(oldStr, newStr);
		int commonEnd = Utils.equalEndLen(oldStr, newStr);
		if(commonEnd + commonStart >= Math.min(oldStr.length(), newStr.length()))
			commonEnd = Math.min(oldStr.length(), newStr.length()) - commonStart;

		try {
			fb.replace(commonStart, oldStr.length() - commonEnd - commonStart, newStr.substring(commonStart, newStr.length() - commonEnd), null);
		} catch (BadLocationException e) {
			// this should not happen. we should have checked this. this whole function should be safe
			throw new AssertionError(e);
		}	
	}
	
	@Override
	protected Document createDefaultModel() {
		PlainDocument d = (PlainDocument) super.createDefaultModel();

		d.setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset,
					String string, AttributeSet attr)
					throws BadLocationException {					
				replace(fb, offset, 0, string, attr);
			}
			@Override
			public void remove(FilterBypass fb, int offset, int length)
					throws BadLocationException {
				String s = MathTextField.this.getText();
				setNewString(fb, s.substring(0, offset) + s.substring(offset + length));
			}
			@Override
			public void replace(FilterBypass fb, int offset, int length,
					String string, AttributeSet attrs)
					throws BadLocationException {
				String s = MathTextField.this.getText();
				boolean insertedDummyChar = false;
				boolean insertedBrackets = false;
				if(string.equals(" ")) string = "*";
				if(length == 0 && string.matches("\\+|-|\\*|/|=")) {
					if(s.substring(offset).matches(" *(((\\+|-|∙|/|=|\\)).*)|)")) {
						string = string + "_";
						insertedDummyChar = true;
					} else if(s.substring(offset).matches(" .*")) {
						string = "_" + string;
						insertedDummyChar = true;						
					}
				}
				else if(string.matches("\\(")) {
					/*if(length == 0) {
						string = "(_)";
						insertedDummyChar = true;
					}
					else*/ {
						string = "(" + s.substring(offset, offset + length) + ")";
						insertedBrackets = true;
					}
				}
				else if(string.matches("\\)"))
					return; // ignore that
				setNewString(fb, s.substring(0, offset) + string + s.substring(offset + length));
				
				if(insertedDummyChar) {
					int p = MathTextField.this.getText().indexOf('_');
					if(p >= 0) {
						MathTextField.this.setCaretPosition(p);
						MathTextField.this.moveCaretPosition(p + 1);						
					}
				}
				else if(insertedBrackets) {
					// move just before the ')'
					if(MathTextField.this.getCaretPosition() > 0)
						MathTextField.this.setCaretPosition(MathTextField.this.getCaretPosition() - 1);
				}
			}

			synchronized void setNewString(FilterBypass fb, String tempStr) throws BadLocationException {
				operatorTree = Utils.OperatorTree.parse(tempStr).simplify();
				MathTextField.this.updateByOpTree(fb);
			}
		});
		
		return d;
	}
	
}
