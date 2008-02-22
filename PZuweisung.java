package applets.Abbildungen_I08_KompositionBereiche;

import java.awt.Color;
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

class PZuweisung implements VTImage.PainterAndListener, Applet.CorrectCheck {
		
	static class Oval {
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
			g.drawString(label, p.x, p.y);
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
	}
	
	static class Connection {
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
	
	// generelle Eigenschaften
	// in Content können diese gesetzt werden
	// danach reset() aufrufen zum aktuallisieren
	public Oval oval1 = new Oval(new Point(20, 20), 200, 150, new Color(123, 123, 123), "A");
	public Oval oval2 = new Oval(new Point(300, 20), 200, 150, new Color(200, 100, 123), "B");
	public Collection dotsA = new LinkedList(), dotsB = new LinkedList();
	protected int dotsCountA = 3, dotsCountB = 3;
	public Collection connections = new LinkedList();
	public Point selectedDotA = null; 
	public Point overDotA = null, overDotB = null;
	public List dotANames = null;
	public List dotBNames = null;
	public boolean dotANames_showalways = true;
	public boolean dotBNames_showalways = true;

	// only needed for repaint-call
	protected final Applet applet;
	// used for isCorrect and getResultText
	protected PZuweisung copySrc = null;
	
	public void paint(Graphics g) {
		oval1.paint(g);
		oval2.paint(g);
		g.setColor(new Color(0, 200, 0));
		drawDots(g, dotsA, dotANames_showalways, dotANames);
		g.setColor(new Color(0, 200, 0));
		drawDots(g, dotsB, dotBNames_showalways, dotBNames);
		g.setColor(Color.BLACK);
		drawConnections(g, connections);
	}

	public PZuweisung(Applet applet) {
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

	public boolean existsConnection(Point a) {
		for(Iterator i = connections.iterator(); i.hasNext(); ) {
			Connection p = (Connection) i.next();
			if(p.src.distance(a) == 0)
				return true;
		}
		return false;
	}

	public boolean existsConnectionForAllDotsA() {
		for(Iterator i = dotsA.iterator(); i.hasNext(); ) {
			Point p = (Point) i.next();
			if(!existsConnection(p))
				return false;
		}
		return true;
	}
	
	public boolean connectionsAreEqual(PZuweisung other) {
		if(other.connections.size() != connections.size())
			return false;
		
		for(Iterator i = connections.iterator(); i.hasNext(); ) {
			Connection p = (Connection) i.next();
			if(!other.existsConnection(p.src, p.dst))
				return false;
		}
		
		return true;
	}
	
	public boolean isCorrect() {
		Collection dotsA_copy = new LinkedList(dotsA);
		for(Iterator i = connections.iterator(); i.hasNext(); ) {
			Connection p = (Connection) i.next();
			Point a = (Point) p.dst.clone();
			Point b = (Point) p.src.clone();
			a.x += oval1.p.x - oval2.p.x;
			b.x += oval2.p.x - oval1.p.x;
			if(!copySrc.existsConnection(a, b))
				return false;
			dotsA_copy.remove(p.src);
		}
		return dotsA_copy.isEmpty();
	}
	
	public String getResultMsg() {
		Collection dotsA_copy = new LinkedList(dotsA);
		for(Iterator i = connections.iterator(); i.hasNext(); ) {
			Connection p = (Connection) i.next();
			Point a = (Point) p.dst.clone();
			Point b = (Point) p.src.clone();
			a.x += oval1.p.x - oval2.p.x;
			b.x += oval2.p.x - oval1.p.x;
			if(!copySrc.existsConnection(a, b))
				return "das ist leider falsch; überprüfen Sie mal " + getDotName(dotANames, getPointIndex(dotsA, p.src));
			dotsA_copy.remove(p.src);
		}
		if(!dotsA_copy.isEmpty())
			return "alle Punkte in B müssen zugewiesen werden";
		else
			return "das ist richtig!";
	}
	
	// helper function, turns point p by angle a
	protected static Point turn(Point p, double a) {
		double x = Math.cos(a);
		double y = Math.sin(a);
		return new Point((int)(x * p.x + y * p.y), (int)(-y * p.x + x * p.y));
	}
	
	public void reset() {
		copySrc = null;
		dotsA.clear();
		dotsB.clear();
		fillWithDots(dotsA, oval1, dotsCountA);
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
		
	public void copyReversedFrom(PZuweisung src, boolean withConn) {
		copySrc = src;
		oval1.label = src.oval2.label;
		oval2.label = src.oval1.label;
		dotANames = src.dotBNames;
		dotBNames = src.dotANames;
		dotsA = new LinkedList(src.dotsB);
		dotsB = new LinkedList(src.dotsA);
		makeCopyOfPoints(dotsA);
		makeCopyOfPoints(dotsB);
		fixXPos(dotsA, oval1.p.x - oval2.p.x);
		fixXPos(dotsB, oval2.p.x - oval1.p.x);
		dotsCountA = dotsA.size();
		dotsCountB = dotsB.size();
		selectedDotA = null;
		overDotA = null;
		overDotB = null;
		connections.clear();
		
		if(withConn) {
			for(Iterator i = src.connections.iterator(); i.hasNext(); ) {
				Connection con = (Connection) i.next();
				int dst_i = getPointIndex(src.dotsA, con.src); 
				int src_i = getPointIndex(src.dotsB, con.dst);
				Connection new_con = new Connection();
				new_con.src = getPointByIndex(dotsA, src_i);
				new_con.dst = getPointByIndex(dotsB, dst_i);
				connections.add(new_con);
			}
		}
		
		this.applet.repaint();
	}			
	
	public void copyFrom(PZuweisung src, boolean withConn) {
		copySrc = src;
		oval1.label = src.oval1.label;
		oval2.label = src.oval2.label;
		dotANames = src.dotANames;
		dotBNames = src.dotBNames;
		dotsA = new LinkedList(src.dotsA);
		dotsB = new LinkedList(src.dotsB);
		makeCopyOfPoints(dotsA);
		makeCopyOfPoints(dotsB);
		dotsCountA = dotsA.size();
		dotsCountB = dotsB.size();
		selectedDotA = null;
		overDotA = null;
		overDotB = null;
		connections.clear();
		
		if(withConn) {
			for(Iterator i = src.connections.iterator(); i.hasNext(); ) {
				Connection con = (Connection) i.next();
				int src_i = getPointIndex(src.dotsA, con.src);
				int dst_i = getPointIndex(src.dotsB, con.dst); 
				Connection new_con = new Connection();
				new_con.src = getPointByIndex(dotsA, src_i);
				new_con.dst = getPointByIndex(dotsB, dst_i);
				connections.add(new_con);
			}
		}
		
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