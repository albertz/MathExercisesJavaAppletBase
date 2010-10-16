/**
 * 
 */
package applets.Termumformungen$in$der$Technik_01_URI;

import java.awt.Component;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class VTMathInput extends VisualThing {

	public VTMathInput(String name, int stepX, int stepY, Runnable changeListener) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.changeListener = changeListener;
	}

	public VTMathInput(String name, int stepX, int stepY, int width, Runnable changeListener) {
		this.name = name;
		this.stepX = stepX;
		this.stepY = stepY;
		this.width = width;
		this.changeListener = changeListener;
	}
	
	private int stepX, stepY;
	private int width = 40;
	private String name = null;
	private JTextField text = null;
	private Runnable changeListener = null;
	private Utils.OperatorTree operatorTree = new Utils.OperatorTree();

	public Component getComponent() {
		if (text == null) {
			text = new JTextField();
			text.setName(name);
			PlainDocument d = (PlainDocument) text.getDocument();
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
					String s = operatorTree.toString();
					setNewString(fb, s, s.substring(0, offset) + s.substring(offset + length));
				}
				@Override
				public void replace(FilterBypass fb, int offset, int length,
						String string, AttributeSet attrs)
						throws BadLocationException {
					String s = operatorTree.toString();
					boolean insertedDummyChar = false;
					boolean insertedBrackets = false;
					if(length == 0 && string.matches("\\+|-|\\*|/|=")) {
						string = string + "_";
						insertedDummyChar = true;
					}
					else if(string.matches("\\(")) {
						string = "(" + s.substring(offset, offset + length) + ")";
						insertedBrackets = true;
					}
					else if(string.matches("\\)"))
						return; // ignore that
					setNewString(fb, s, s.substring(0, offset) + string + s.substring(offset + length));
					
					if(insertedDummyChar) {
						int p = text.getText().indexOf('_');
						if(p >= 0) {
							text.setCaretPosition(p);
							text.moveCaretPosition(p + 1);						
						}
					}
					else if(insertedBrackets) {
						// move just before the ')'
						if(text.getCaretPosition() > 0) text.setCaretPosition(text.getCaretPosition() - 1);
					}
				}

				synchronized void setNewString(FilterBypass fb, String oldStr, String tempStr) throws BadLocationException {
					operatorTree = Utils.OperatorTree.parse(tempStr).simplify();
					String newStr = operatorTree.toString();
					int commonStart = Utils.equalStartLen(oldStr, newStr);
					int commonEnd = Utils.equalEndLen(oldStr, newStr);
					if(commonEnd + commonStart >= Math.min(oldStr.length(), newStr.length()))
						commonEnd = Math.min(oldStr.length(), newStr.length()) - commonStart;
					//System.out.println("setNewString: " + oldStr + " -> " + tempStr + " -> " + newStr + " ; s = " + commonStart + ", e = " + commonEnd);
					super.replace(fb, commonStart, oldStr.length() - commonEnd - commonStart, newStr.substring(commonStart, newStr.length() - commonEnd), null);
				}
			});
			
			if (changeListener != null)
				text.getDocument().addDocumentListener(new DocumentListener() {
					public void removeUpdate(DocumentEvent e) {
						changeListener.run();
					}				
					public void insertUpdate(DocumentEvent e) {
						changeListener.run();
					}				
					public void changedUpdate(DocumentEvent e) {
						changeListener.run();
					}
				});
		}
		return text;
	}

	public int getStepY() {
		return stepY;
	}

	public int getStepX() {
		return stepX;
	}

	public void setStepX(int v) {
		stepX = v;
	}

	public void setStepY(int v) {
		stepY = v;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return 23;
	}

}
