package applets.AnalytischeGeometrieundLA_07_EbeneGeradeSchnittpunkt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;









class PZuweisungExtended implements VTImage.PainterAndListener, Applet.CorrectCheck {
	
	private final Applet applet;

	class Oval {
		public Oval(Point p, int w, int h, Color c, String label) {
			this.p = p;
			this.w = w;
			this.h = h;
			this.c = c;
			this.label = label;
		}
		
		public Point p;
		public int w, h;
		public Color c;
		public String label;
		
		public void paint(Graphics g) {
			g.setColor(c);
			g.fillOval(p.x, p.y, w, h);
			g.setFont(g.getFont().deriveFont(Font.BOLD));
			g.drawString(label,
					p.x + w/2 - (int)Math.sqrt(w*w + h*h)/4 - 10,
					p.y + h/2 - (int)Math.sqrt(w*w + h*h)/4 - 5);
		}
		
		public Point getRandomPoint(Collection keepDistance, float d) {
			Point res = null;
			Random rnd = new Random();
			
			boolean distance = false;
			while(!distance) {
				double r = Math.sqrt(rnd.nextDouble()); // sqrt damit groessere r etwas wahrscheinlicher werden
				double a = rnd.nextDouble() * 2 * Math.PI;
				double x = Math.cos(a) * w * 0.5 * r; 
				double y = Math.sin(a) * h * 0.5 * r;
				x *= 0.8; y *= 0.8; // damit es auch echt drin ist
				res = new Point((int)x + p.x + w / 2, (int)y + p.y + h / 2);
				
				distance = true;
				for(Iterator i = keepDistance.iterator(); i.hasNext(); ) {
					Point p = (Point) i.next();
					if(p.distance(res) < d) {
						distance = false;
						break;
					}
				}
			}
			
			return res;
		}
		
		public boolean isInside(Point point) {
			// TODO: nicht ganz korrekt (für w != h)
			return point.distance(p.x + w/2, p.y + h/2) < w/2;
		}
	}
	
	class Connection {
		public Point src;
		public Point dst;
		
		public void paint(Graphics g) {
			g.drawLine(src.x, src.y, dst.x, dst.y);
			
			Point p1 = turn(new Point(src.x - dst.x, src.y - dst.y), 0.25 * Math.PI); 
			Point p2 = turn(new Point(src.x - dst.x, src.y - dst.y), -0.25 * Math.PI); 
			double r = Math.sqrt(p1.x * p1.x + p1.y * p1.y);
			double R = 10;
			p1.x *= R / r; p1.y *= R / r; p2.x *= R / r; p2.y *= R / r;
			g.drawLine(dst.x, dst.y, dst.x + p1.x, dst.y + p1.y);
			g.drawLine(dst.x, dst.y, dst.x + p2.x, dst.y + p2.y);

		}
	}
	
	public Oval oval1a = new Oval(new Point(100, 20), 100, 100, new Color(223, 123, 123), "U");
	public Oval oval1b = new Oval(new Point(50, 120), 100, 100, new Color(123, 223, 123), "V");
	public Oval oval1back = new Oval(new Point(20, 10), 220, 220, new Color(223, 223, 123), "U");
	public Oval oval2 = new Oval(new Point(300, 50), 150, 150, new Color(200, 100, 123), "B");
	public Collection dotsA = new LinkedList(), dotsB = new LinkedList();
	protected int dotsCountA1 = 3, dotsCountA2 = 3, dotsCountB = 6;
	public Collection connections = new LinkedList();
	public Point selectedDotA = null; 
	public Point overDotA = null, overDotB = null;
	public List dotANames = null;
	public List dotBNames = null;
	public boolean dotANames_showalways = true;
	public boolean dotBNames_showalways = true;
	
	public void paint(Graphics g) {
		oval1back.paint(g);
		oval1a.paint(g);
		oval1b.paint(g);
		oval2.paint(g);
		g.setColor(new Color(0, 200, 0));
		drawDots(g, dotsA, dotANames_showalways, dotANames);
		g.setColor(new Color(0, 200, 0));
		drawDots(g, dotsB, dotBNames_showalways, dotBNames);
		g.setColor(Color.BLACK);
		drawConnections(g, connections);
	}

	public PZuweisungExtended(Applet applet) {
		this.applet = applet;
		reset();
	}
	
	protected void fillWithDots(Collection col, Oval o, int n) {
		for(int i = 0; i < n; i++) {
			col.add(o.getRandomPoint(col, 30));
		}
	}
	
	protected void drawDots(Graphics g, Collection col, boolean showalways, List names) {
		Color c = g.getColor();
		int k = 0;
		for(Iterator i = col.iterator(); i.hasNext(); k++) {
			Point p = (Point) i.next();
			if(p == selectedDotA)
				g.setColor(Color.BLUE);
			else if(p == overDotA || p == overDotB)
				g.setColor(Color.CYAN);
			else
				g.setColor(c);
			g.fillOval(p.x - 4, p.y - 4, 8, 8);
			if(p == overDotA || p == overDotB || showalways)
				g.drawString(getDotName(names, k), p.x + 4, p.y - 4);
		}
	}
	
	protected String getDotName(List names, int index) {
		if(names != null)
			return (String) names.get(index);
		else
			return String.valueOf(index + 1);
	}
				
	protected void drawConnections(Graphics g, Collection cons) {
		if(selectedDotA != null && overDotB != null) {
			Color c = g.getColor();
			g.setColor(Color.GRAY);
			Connection tmpCon = new Connection();
			tmpCon.src = selectedDotA;
			tmpCon.dst = overDotB;
			tmpCon.paint(g);
			g.setColor(c);
		}

		for(Iterator i = cons.iterator(); i.hasNext(); ) {
			Connection con = (Connection) i.next();
			if(selectedDotA != null && overDotB != null
					&& con.src.distance(selectedDotA) == 0
					&& con.dst.distance(overDotB) != 0) {
				// ignore
			} else if(
					(overDotA != null && con.src.distance(overDotA) == 0) ||
					(overDotB != null && con.dst.distance(overDotB) == 0)
					) {
				Color c = g.getColor();
				g.setColor(Color.CYAN);
				con.paint(g);
				g.setColor(c);
			} else
				con.paint(g);
		}
	}
	
	public void addSurjectivConnections() {
		Iterator j = dotsB.iterator();
		for(Iterator i = dotsA.iterator(); i.hasNext(); ) {
			Connection con = new Connection();
			con.src = (Point) i.next();
			if(!j.hasNext())
				j = dotsB.iterator();
			con.dst = (Point) j.next();
			connections.add(con);
		}
	}
		
	protected int getPointIndex(Collection col, Point pos) {
		int k = 0;
		for(Iterator i = col.iterator(); i.hasNext(); k++) {
			Point p = (Point) i.next();
			if(p.distance(pos) == 0) return k;
		}
		return -1;
	}
	
	protected Point getPointByIndex(Collection col, int index) {
		int k = 0;
		for(Iterator i = col.iterator(); i.hasNext(); k++) {
			Point p = (Point) i.next();
			if(index == k) return p;
		}
		return null;
	}
	
	public boolean existsConnection(Point a, Point b) {
		for(Iterator i = connections.iterator(); i.hasNext(); ) {
			Connection p = (Connection) i.next();
			if(p.src.distance(a) == 0 && p.dst.distance(b) == 0)
				return true;
		}
		return false;
	}
	
	public boolean isCorrect() {
		List dotsAcopy = new LinkedList(dotsA);
		List dotsB1 = new LinkedList();
		List dotsB2 = new LinkedList();
		boolean correct = false;
		for(Iterator i = connections.iterator(); i.hasNext(); ) {
			Connection con = (Connection) i.next();
			dotsAcopy.remove(getPointIndex(dotsAcopy, con.src));
			
			if(!correct) {
				boolean inDotsB1 = getPointIndex(dotsB1, con.dst) >= 0;
				boolean inDotsB2 = getPointIndex(dotsB2, con.dst) >= 0;
				if(oval1a.isInside(con.src)) {
					if(inDotsB2) correct = true;
					else dotsB1.add(con.dst);
				}
				else { // in oval1b
					if(inDotsB1) correct = true;
					else dotsB2.add(con.dst);
				}
			}
		}
		
		return dotsAcopy.isEmpty() && correct;
	}
	
	public String getResultMsg() {
		List dotsAcopy = new LinkedList(dotsA);
		List dotsB1 = new LinkedList();
		List dotsB2 = new LinkedList();
		boolean correct = false;
		for(Iterator i = connections.iterator(); i.hasNext(); ) {
			Connection con = (Connection) i.next();
			dotsAcopy.remove(getPointIndex(dotsAcopy, con.src));
			
			if(!correct) {
				boolean inDotsB1 = getPointIndex(dotsB1, con.dst) >= 0;
				boolean inDotsB2 = getPointIndex(dotsB2, con.dst) >= 0;
				if(oval1a.isInside(con.src)) {
					if(inDotsB2) correct = true;
					else dotsB1.add(con.dst);
				}
				else { // in oval1b
					if(inDotsB1) correct = true;
					else dotsB2.add(con.dst);
				}
			}
		}

		if(!dotsAcopy.isEmpty())
			return "alle Punkte in X müssen zugewiesen werden";
		else if(correct)
			return "das ist korrekt!";
		else
			return "leider ist das nicht korrekt";
	}
	
	protected Point turn(Point p, double a) {
		double x = Math.cos(a);
		double y = Math.sin(a);
		return new Point((int)(x * p.x + y * p.y), (int)(-y * p.x + x * p.y));
	}
	
	public void reset() {
		dotsA.clear();
		dotsB.clear();
		fillWithDots(dotsA, oval1a, dotsCountA1);
		fillWithDots(dotsA, oval1b, dotsCountA2);
		fillWithDots(dotsB, oval2, dotsCountB);
		resetConnections();
	}
	
	public void resetConnections() {
		connections.clear();
		selectedDotA = null;
		overDotA = null;
		overDotB = null;
		
		this.applet.repaint();
	}
	
	
	protected void makeCopyOfPoints(Collection col) {
		Collection col_copy = new LinkedList(col);
		col.clear();
		for(Iterator i = col_copy.iterator(); i.hasNext(); ) {
			Point p = (Point) i.next();
			col.add(p.clone());
		}
	}
	
	protected void fixXPos(Collection col, int d) {
		for(Iterator i = col.iterator(); i.hasNext(); ) {
			Point p = (Point) i.next();
			p.x += d;
		}
	}
	
	protected Point getPointByPos(Collection col, Point pos) {
		for(Iterator i = col.iterator(); i.hasNext(); ) {
			Point p = (Point) i.next();
			if(p.distance(pos) < 10) return p;
		}
		return null;
	}
	
	protected Connection getConnectionBySrc(Point src, Collection cons) {
		for(Iterator i = cons.iterator(); i.hasNext(); ) {
			Connection con = (Connection) i.next();
			if(con.src.distance(src) == 0) return con;
		}
		return null;
	}
	
	public boolean editable = true;
	
	public void mouseClicked(MouseEvent e) {
		if(!editable) return;
		
		mouseMoved(e); // just a HACK to get sure that vars are correct
		Point p = getPointByPos(dotsA, e.getPoint());
		if(p != null) {
			selectedDotA = p;
		} else if(selectedDotA != null) {
			p = getPointByPos(dotsB, e.getPoint());
			if(p != null) {
				Connection con = getConnectionBySrc(selectedDotA, connections);
				if(con != null) connections.remove(con);
				con = new Connection();
				con.src = selectedDotA;
				con.dst = p;
				connections.add(con);
				
				onChange();
			}
		}
		
		this.applet.repaint();
	}

	protected void onChange() {
		// TODO: do something here
	}
	
	public void mouseMoved(MouseEvent e) {
		overDotA = getPointByPos(dotsA, e.getPoint());
		overDotB = getPointByPos(dotsB, e.getPoint());

		e.getComponent().repaint();
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}

}
