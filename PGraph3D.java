package applets.AnalytischeGeometrieundLA_Ebene_StuetzNormRichtung;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PGraph3D implements VTImage.PainterAndListener, Applet.CorrectCheck {

	static float EPS = 0.001f;
	
	static public abstract class DynFloat {
		public float get() throws Exception { throw new Exception("DynFloat::get() not defined"); }

		public boolean isValid() {
			try {
				get();
				return true;
			} catch(Exception e) {
				return false;
			}
		}
	}

	static public abstract class DynVector3D {
		public float get(int i) throws Exception { throw new Exception("DynVector3D::get() not defined"); }
		
		public boolean isValid() {
			try {
				for(int i = 0; i < 3; ++i) get(i);
				return true;
			} catch(Exception e) {
				return false;
			}
		}
		
		public float abs() throws Exception { return (float) Math.sqrt(get(0) * get(0) + get(1) * get(1) + get(2) * get(2)); }
		
		public Point3D fixed() {
			try {
				return new Point3D(this);
			} catch(Exception e) {
				return null;
			}
		}
		
		public DynVector3D crossProduct(final DynVector3D v) {
			final DynVector3D t = this;
			return new DynVector3D() {
				public float get(int i) throws Exception {
					return t.get(i + 1) * v.get(i + 2) - t.get(i + 2) * v.get(i + 1);
				}
			};
		}
		
		public DynFloat dotProduct(final DynVector3D v) {
			final DynVector3D t = this;
			return new DynFloat() {
				public float get() throws Exception {
					return t.get(0) * v.get(0) + t.get(1) * v.get(1) + t.get(2) * v.get(2);
				}
			};
		}

		public DynVector3D product(final DynFloat f) {
			final DynVector3D t = this;
			return new DynVector3D() {
				public float get(int i) throws Exception { return t.get(i) * f.get(); }
			};
		}

		public DynVector3D sum(final DynVector3D v) {
			final DynVector3D t = this;
			return new DynVector3D() {
				public float get(int i) throws Exception { return t.get(i) + v.get(i); }
			};
		}

		public DynVector3D diff(final DynVector3D v) {
			final DynVector3D t = this;
			return new DynVector3D() {
				public float get(int i) throws Exception { return t.get(i) - v.get(i); }
			};
		}
	}
	
	static public class pair < First > {
		public First x;
		public First y;
		public pair() {}
		public pair(First _x, First _y) { x = _x; y = _y; }
	}
	
	static public class Vector3D extends DynVector3D {
		public float x[] = new float[3];
		public Vector3D() {}
		public Vector3D(float a, float b, float c) { x[0] = a; x[1] = b; x[2] = c; }
		public float get(int i) { return x[i % 3]; }
	}
	
	static public class Point3D extends Vector3D {
		public Point3D() {}
		public Point3D(DynVector3D v) throws Exception { for(int i = 0; i < 3; ++i) x[i] = v.get(i); }
	}
	
	static public class Float extends DynFloat {
		public float x;
		public Float() {}
		public Float(float x_) { x = x_; }
		public float get() { return x; }
	}
	
	static public class Viewport {
		Graphics g;
		Float eyeHeight = new Float(1.0f);
		Vector3D eyeDir = new Vector3D(1,0,0);
		Plane eyePlane = new Plane(eyeHeight, eyeDir);
		DynVector3D eyePoint = eyeDir.product(eyeHeight).product(new Float(2.0f));
		float scaleFactor = 1;
		
		public void setGraphics(Graphics g) { this.g = g; setColor(Color.black); }
		public void setColor(Color c) { g.setColor(c); }
		
		protected java.awt.Point translate(Point3D p) {
			Line eyeLine = new Line();
			eyeLine.point = eyePoint;
			eyeLine.vector = p.diff(eyeLine.point);
			
			Point eyePtAbs = eyePlane.intersectionPoint(eyeLine);
			Line xAxeAbs = eyePlane.intersectionLine(Plane.zPlane);
			
			Point3D eyePt = eyePtAbs.point.diff(eyePlane.basePoint()).fixed();
			Vector3D xAxe = xAxeAbs.vector.fixed();
			Vector3D yAxe = eyePlane.normal.crossProduct(xAxe).fixed();
			
			java.awt.Point tp = new java.awt.Point();
			try {
				tp.x = (int) xAxe.dotProduct( eyePt ).get();
				tp.y = (int) yAxe.dotProduct( eyePt ).get();
			} catch (Exception e) {
				// should not happen
				e.printStackTrace();
			}
			return tp;
		}
		
		public void drawPoint(Point3D p) {
			java.awt.Point tp = translate(p);
			g.fillOval(tp.x, tp.y, 2, 2);
		}
		
		public void drawArrow(Point3D p, Vector3D v) {
			// TODO:...
			drawPolygon( new Point3D[] {p, p.sum(v).fixed()} );
		}
		
		public void drawPolygon(Point3D[] p) {
			Polygon tpoly = new Polygon();
			for(int i = 0; i < p.length; ++i) {
				java.awt.Point tp = translate(p[i]);
				tpoly.addPoint(tp.x, tp.y);
			}
			g.fillPolygon(tpoly);
		}
		
	}
	
	Viewport viewport;
	
	static public interface Object3D {
		public void draw(Viewport v);
	}
	
	static public class Line implements Object3D {
		DynVector3D point;
		DynVector3D vector;
		Color color;
		
		public void draw(Viewport v) {
			v.setColor(color);
			try {
				v.drawPolygon( new Point3D[] { new Point3D(point), new Point3D(point.sum(vector)) } );
			} catch (Exception e) {}
		};
		
		public boolean isValid() {
			try {
				return point.isValid() && vector.abs() > EPS;
			} catch (Exception e) {
				return false;
			}
		}
		
		public Point intersectionPoint(final Line l) {
			// TODO: this way doesn't cover all cases!
			
			final Line t = this;
			// [ v1, -v2 ] * (t1 t2) = p2 - p1  ->  p1 + t1 v1 ( = p2 + t2 v2 ) is intersection point
			final DynMatrix2D m = new DynMatrix2D() {
				public float get(int i, int j) throws Exception {
					j %= 2;
					if(j == 0) return t.vector.get(i);
					else return -l.vector.get(i);
				}
			};
			final DynVector3D pointDiff = l.point.diff( point );
			
			Point p = new Point();
			p.point = new DynVector3D() {
				public float get(int i) throws Exception {
					final pair<DynFloat> ts = m.solve(new pair<DynFloat>(new Float(pointDiff.get(0)), new Float(pointDiff.get(1))));

					// we must check now
					float check = vector.get(2) * ts.x.get() - l.vector.get(2) * ts.y.get() - (l.point.get(2) - point.get(2));
					if(Math.abs(check) > EPS) throw new Exception("no solution");
					
					return t.point.sum( t.vector.product(ts.x) ).get(i);
				}
			};
			return p;
		}
	}
	
	static public class VectorArrow extends Line {
		public void draw(Viewport v) {
			v.setColor(color);
			try {
				v.drawArrow( new Point3D(point), new Point3D(point.sum(vector)) );
			} catch (Exception e) {}
		}
	}
	
	static public class Point implements Object3D {
		DynVector3D point;
		Color color;
		
		public void draw(Viewport v) {
			if(point.isValid()) { 
				v.setColor(color);
				v.drawPoint(point.fixed());
			}
		}
	}
	
	static public abstract class DynMatrix2D {
		public float get(int i, int j) throws Exception { throw new Exception("DynMatrix2D::get not defined"); }
		
		public DynFloat det() {
			final DynMatrix2D t = this;			
			return new DynFloat() {
				public float get() throws Exception { return t.get(0,0) * t.get(1,1) - t.get(0,1) * t.get(1,0); }
			};
		}
		
		public pair<DynFloat> solve(final pair<DynFloat> v) {
			// using Cramer's rule
			final DynMatrix2D t = this;
			pair<DynFloat> p = new pair<DynFloat>();
			p.x = new DynFloat() {
				public float get() throws Exception {
					float top = v.x.get() * t.get(1,1) - t.get(0,1) * v.y.get();
					float bottom = t.det().get();
					if(Math.abs(bottom) > EPS) return top / bottom;
					throw new Exception("Matrix::solve: no solution for x");
				}
			};
			p.y = new DynFloat() {
				public float get() throws Exception {
					float top = t.get(0,0) * v.x.get() - v.y.get() * t.get(1,0);
					float bottom = t.det().get();
					if(Math.abs(bottom) > EPS) return top / bottom;
					throw new Exception("Matrix::solve: no solution for y");
				}
			};
			return p;
		}
	}
	
	static public class Plane implements Object3D {
		DynFloat height;
		DynVector3D normal;
		Color color;
		
		public Plane() {}
		public Plane(DynFloat height, DynVector3D normal) { this.height = height; this.normal = normal; }

		DynVector3D basePoint() {
			Line normalLine = new Line();
			normalLine.point = new Point3D();
			normalLine.vector = normal;
			return intersectionPoint(normalLine).point;
		}
		
		static public Plane xPlane = new Plane(new Float(0), new Vector3D(1,0,0));
		static public Plane yPlane = new Plane(new Float(0), new Vector3D(0,1,0));
		static public Plane zPlane = new Plane(new Float(0), new Vector3D(0,0,1));
		static public Plane[] basePlanes = new Plane[] { xPlane, yPlane, zPlane };
		
		public void draw(Viewport v) {
			v.setColor(color);
			
			Line[] lines = new Line[3];
			for(int i = 0; i < 3; ++i) lines[i] = basePlanes[i].intersectionLine(this);
			
			Point3D[] points = new Point3D[3];
			for(int i = 0; i < 3; ++i) points[i] = lines[i].intersectionPoint(lines[ (i+1) % 3 ]).point.fixed();

			List<Point3D> pointList = new LinkedList<Point3D>();
			for(int i = 0; i < 3; ++i) {
				Point3D p = points[i];
				if(p != null) pointList.add(p);
				else {
					Point3D[] otherps = new Point3D[2];
					for(int j = 0; j < 2; ++j) otherps[j] = points[ (i+j) % 3 ];
					for(int j = 0; j < 2; ++j) if(otherps[j] == null) return; // the whole plane seems invalid
					// somewhat pulled out of thin air
					for(int j = 0; j < 2; ++j) pointList.add( otherps[j].sum( basePlanes[(i+j) % 3].normal.product(new Float(5.0f)) ).fixed() );
				}
			}
			
			v.drawPolygon( pointList.toArray((Point3D[])null) );
		}
		
		public Line intersectionLine(Plane other) {
			Line l = new Line();
			l.vector = normal.crossProduct(other.normal);
			final DynVector3D normals[] = { normal, other.normal };
			// [ ( (n1 n1) (n1 n2) ) ( (n2 n1) (n2 n2) ) ] * (c1 c2) = (h1 h2)  ->  n1 c1 + n2 c2  is point on line
			DynMatrix2D nn = new DynMatrix2D() {
				public float get(int i, int j) throws Exception {
					i %= 2; j %= 2;
					return normals[i].dotProduct( normals[j] ).get();
				}
			};
			pair<DynFloat> heights = new pair<DynFloat>(height, other.height);
			final pair<DynFloat> c = nn.solve(heights); 
			l.point = normal.product(c.x).sum( other.normal.product(c.y) );
			return l;
		}
		
		public Point intersectionPoint(final Line line) {
			Point p = new Point();
			p.point = new DynVector3D() {
				public float get(int i) throws Exception {
					float nv = normal.dotProduct(line.vector).get();
					float hnp = height.get() - normal.dotProduct(line.point).get();
					if(Math.abs(nv) < EPS) {
						if(Math.abs(hnp) < EPS) throw new Exception("Plane::intersectionPoint of line: line is on plane");
						else throw new Exception("Plane::intersectionPoint of line: there is no intersection point");
					}
					float t = nv / hnp;
					return line.point.sum( line.vector.product(new Float(t)) ).get(i);
				}
			};
			return p;
		}
	}
		
	public int W;
	public int H;
	
	public PGraph3D(Applet applet, int w, int h) {
		W = w;
		H = h;
	}

	public Set<Object3D> objects = new HashSet<Object3D>();
	
	public void paint(Graphics g) {
		viewport.setGraphics(g);
		for(Object3D o : objects) {
			o.draw(viewport);
		}
	}
	
	public void mouseClicked(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
	public void mouseDragged(MouseEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
	}
	
	public String getResultMsg() {
		return "";
	}
	
	public boolean isCorrect() {
		return false;
	}

}
