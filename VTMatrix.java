/**
 * 
 */
package applets.AnalytischeGeometrieundLA_01_2DGeradeSteigungsdreieck;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;


public class VTMatrix extends VTContainer {
	VisualThing[][] content;
	
	int lineCount() {
		return content.length;
	}
	
	int rowCount() {
		return content[0].length;
	}
	
	private int sepWidth = 2;
	
	private int rowWidth(int j) {
		int w = 0;
		for(int i = 0; i < lineCount(); ++i)
			w = Math.max(w, content[i][j].getWidth());
		return w;	
	}
	
	private int lineWidth() {
		int w = 0;
		for(int j = 0; j < rowCount(); ++j) {
			if(j > 0) w += sepWidth;
			w += rowWidth(j);
		}
		return w;
	}
		
	private int rowX(int J) {
		int w = 0;
		for(int j = 0; j < J; ++j) {
			if(j > 0) w += sepWidth;
			w += rowWidth(j);
		}
		return w;		
	}
	
	private int spaceWidthBefore(int i, int j) {
		int w = (rowWidth(j) - content[i][j].getWidth()) / 2;
		if(j > 0) w += (rowWidth(j-1) - content[i][j-1].getWidth()) / 2;
		if(j > 0) w += sepWidth;
		return w;
	}
	
	public int afterLastSpaceWidth() {
		int w = 0;
		for(int i = 0; i < lineCount(); ++i)
			w = Math.max(w, (rowWidth(rowCount()-1) + content[i][rowCount()-1].getWidth()) / 2);
		return rowWidth(rowCount()-1) - w; 
	}
	
	static private VisualThing[][] fromStrings(String[][] content) {
		VisualThing[][] c = new VisualThing[content.length][content[0].length];
		for(int i = 0; i < content.length; ++i)
			for(int j = 0; j < content[0].length; ++j)
				c[i][j] = new VTLabel(content[i][j], 0, 0);
		return c;
	}
			
	public VTMatrix(String name, int stepX, int stepY, VisualThing[][] content) {
		super(name, stepX, stepY, null);
		this.content = content;
		defineMyself();
	}

	public VTMatrix(int stepX, int stepY, VisualThing[][] content) {
		this(null, stepX, stepY, content);
	}

	public VTMatrix(int stepX, int stepY, String[][] content) {
		this(stepX, stepY, fromStrings(content));
	}

	static String[][] parseContent(String content) {
		String[] seperated = content.split(";");
		String[][] lines = new String[seperated.length][];
		for(int i = 0; i < seperated.length; ++i)
			lines[i] = seperated[i].split(",");
		return lines;
	}
	
	public VTMatrix(int stepX, int stepY, String content) {
		this(stepX, stepY, parseContent(content));
	}

	static private VisualThing[] stripThingsArray(VisualThing[] ts) {
		List<VisualThing> things = new LinkedList<VisualThing>();
		for(int i = 0; i < ts.length; ++i) {
			if(ts[i] instanceof VTLabel) {
				VTLabel l = (VTLabel) ts[i];
				if(l.getName() != null || l.getText().trim().length() > 0)
					things.add(l);
			}
			else
				things.add(ts[i]);
		}
		return things.toArray(new VisualThing[] {});
	}
	
	static private VisualThing[] splitThing(VisualThing c) {
		if(c instanceof VTContainer) {
			// we expect that it contains one single element and that is a line combiner
			// this is because of how VTMeta behaves in getThingsByContentStr right now
			// WARNING: if the behavior of getThingsByContentStr changes,
			// this perhaps need to be updated!
			VTContainer con = (VTContainer) c;
			if(con.things.length == 1)
				return splitThing(con.things[0]);
		}
		if(c instanceof VTLineCombiner) {
			VTLineCombiner l = (VTLineCombiner) c;
			return stripThingsArray(l.things);			
		}
		return null;
	}
	
	static private VisualThing[][] emptyMatrix() { return new VisualThing[][] { new VisualThing[] {} }; }
	
	static private VisualThing[][] splitContent(VisualThing c) {
		VisualThing[] lines = splitThing(c);
		if(lines == null) {
			System.err.println("splitContent: " + c.getDebugString() + " is invalid");			
			return emptyMatrix();
		}

		VisualThing[][] ret = new VisualThing[lines.length][]; 
		int rows = 0;
		for(int i = 0; i < lines.length; ++i) {
			VisualThing[] splittedLine = splitThing(lines[i]);
			if(splittedLine == null) {
				System.err.println("VTMatrix.splitContent: line " + (i+1) + " of " + c.getDebugString() + " is invalid");
				return emptyMatrix(); 
			}
			if(i == 0) rows = splittedLine.length;
			else if(rows != splittedLine.length) {
				System.err.println("VTMatrix.splitContent: first line has " + rows + " rows but line " + (i+1) + " has " + splittedLine.length + " rows: -> " + c.getDebugString() + " is invalid");
				return emptyMatrix();				
			}
			ret[i] = splittedLine;
		}
		
		System.out.println("Matrix with " + lines.length + " lines and " + rows + " rows: " + c.getDebugString());		
		return ret;
	}
	
	public VTMatrix(int stepX, int stepY, VisualThing content) {
		this(stepX, stepY, splitContent(content));
	}

	int width, height;

	static class VTArc extends VisualThing {
		int width, height;
		boolean left;
		Color color = Color.black;
		
		public VTArc(int w, int h, boolean l) { width = w; height = h; left = l; }
				
		Component c = new Container() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				g.setColor(VTArc.this.color);
				//g.drawArc(0,0,VTArc.this.width,VTArc.this.height,0,360);
				
				double w = VTArc.this.width;
				double h = VTArc.this.height;
				double r = (h*h/4 + w*w) / (2*w);
				double arcAngle = Math.asin(h*0.5/r) * 180 / Math.PI;
				
				double arcStart = VTArc.this.left ? (180 - arcAngle) : (-arcAngle);
				double x = VTArc.this.left ? 1 : -(2*r - w);
				double y = -(r - h/2);
				g.drawArc((int)x-1, (int)y, (int)(r*2), (int)(r*2), (int)arcStart, (int)(arcAngle*2));
			}
		};
		
		public Component getComponent() {
			return c;
		}

		public int getHeight() {
			return height;
		}

		public int getStepX() {
			return 0;
		}

		public int getStepY() {
			return 0;
		}

		public int getWidth() {
			return width;
		}

		public void setStepX(int v) {
		}

		public void setStepY(int v) {
		}
		
	}
	
	private VisualThing[] lineContent(int i) {
		VisualThing[] ret = new VisualThing[rowCount() * 2]; // every first entry for space
		for(int j = 0; j < rowCount(); ++j) {
			ret[j*2] = new VTEmptySpace(0, 0, spaceWidthBefore(i,j), 5);
			ret[j*2+1] = content[i][j];
		}
		return ret;
	}
	
	private int lineSepHeight = 5;

	private VisualThing[] matrixInner() {
		VisualThing[] ret = new VisualThing[lineCount()];
		for(int i = 0; i < lineCount(); ++i)
			ret[i] = new VTLineCombiner(0, lineSepHeight, lineContent(i));
		return ret;
	}
		
	private int lineHeight(int i) {
		int h = 0;
		for(int j = 0; j < rowCount(); ++j)
			h = Math.max(h, content[i][j].getHeight());
		return h;
	}
	
	private int rowHeight() {
		int h = 0;
		for(int i = 0; i < lineCount(); ++i) {
			if(i > 0) h += lineSepHeight;
			h += lineHeight(i);
		}
		return h + 2*lineSepHeight;
	}
	
	private void defineMyself() {
		width = lineWidth() + afterLastSpaceWidth() + 10;
		height = rowHeight();
				
		this.things = new VisualThing[] {
			new VTLineCombiner(0, 0, new VisualThing[] {
					new VTArc(5, height, true),
					new VTContainer(0, 0, matrixInner()),
					new VTEmptySpace(0, 0, afterLastSpaceWidth(), 1),
					new VTArc(5, height, false),
			})
		};
	}

	public Component getComponent() {
		if(panel == null)
			defineMyself();
		return super.getComponent();
	}
	
	public int getWidth() {
		// like in VTContainer, calc the size always again if not created yet
		if(panel == null)
			defineMyself();
		return width;
	}

	public int getHeight() {
		if(panel == null)
			defineMyself();
		return height;
	}
	
}
