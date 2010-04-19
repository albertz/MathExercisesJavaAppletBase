package applets.AnalytischeGeometrieundLA_01a_2DGeradenSchnittParallel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

public class PGraph3D implements VTImage.PainterAndListener, Applet.CorrectCheck {

	static double EPS = 0.1;
	
	static public abstract class DynFloat {
		public double get() throws Exception { throw new Exception("DynFloat::get() not defined"); }

		public DynFloat product(final DynFloat f) {
			final DynFloat t = this; 
			return new DynFloat() {
				public double get() throws Exception {
					return t.get() * f.get();
				}
			};
		}
		
		public boolean isValid() {
			try {
				get();
				return true;
			} catch(Exception e) {
				return false;
			}
		}

		static private String num(double n) {
			if(Double.isInfinite(n)) return (n >= 0) ? "∞" : "-∞";
			if(n < 0) return "-" + num(-n);
			return "" + (int)(Math.floor(n)) + "." + ((int)(Math.floor(n*10)) % 10);
		}
		
		private String num() {
			try {
				return num(get());
			} catch (Exception e) {
				return "ungültig";
			}
		}

		public String toString() {
			return num();
		}
	}

	static public abstract class DynVector3D {
		public double get(int i) throws Exception { throw new Exception("DynVector3D::get() not defined"); }
		
		public boolean isValid() {
			try {
				for(int i = 0; i < 3; ++i) get(i);
				return true;
			} catch(Exception e) {
				return false;
			}
		}
		
		public DynFloat abs() {
			final DynVector3D t = this;
			return new DynFloat() {
				public double get() throws Exception {
					return Math.sqrt(get(0) * get(0) + get(1) * get(1) + get(2) * get(2));
				}

				private double get(int i) throws Exception {
					return t.get(i);
				}
			}; 
		}
		
		public DynFloat dynGet(final int i) {
			return new DynFloat() {
				public double get() throws Exception {
					return DynVector3D.this.get(i);
				}
			};
		}
		
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
				public double get(int i) throws Exception {
					return t.get(i + 1) * v.get(i + 2) - t.get(i + 2) * v.get(i + 1);
				}
			};
		}
		
		public DynFloat dotProduct(final DynVector3D v) {
			final DynVector3D t = this;
			return new DynFloat() {
				public double get() throws Exception {
					return t.get(0) * v.get(0) + t.get(1) * v.get(1) + t.get(2) * v.get(2);
				}
			};
		}

		public DynVector3D product(final DynFloat f) {
			final DynVector3D t = this;
			return new DynVector3D() {
				public double get(int i) throws Exception { return t.get(i) * f.get(); }
			};
		}

		public DynVector3D sum(final DynVector3D v) {
			final DynVector3D t = this;
			return new DynVector3D() {
				public double get(int i) throws Exception { return t.get(i) + v.get(i); }
			};
		}

		public DynVector3D diff(final DynVector3D v) {
			final DynVector3D t = this;
			return new DynVector3D() {
				public double get(int i) throws Exception { return t.get(i) - v.get(i); }
			};
		}
		
		public DynVector3D norminated() {
			final DynVector3D t = this;
			return new DynVector3D() {
				public double get(int i) throws Exception {
					double a = t.abs().get();
					if(a > EPS)
						return t.get(i) / a;
					throw new Exception("DynVector3D.norminated: vector is 0");
				}
			};
		}
		
		public DynVector3D someOrthogonal() {
			final DynVector3D t = this;
			return new DynVector3D() {
				public double get(int i) throws Exception {
					for(int j = 0; j < 3; ++j) {
						try {
							Vector3D v = t.crossProduct(Plane.basePlanes[j].normal).fixed();
							if(v.abs().get() > EPS) return v.get(i);
						}
						catch(Exception e) {}
					}
					throw new Exception("DynVector3D.someOrthogonal: no solution");
				}
			};
			
			/*
			DynVector3D otherVec = new DynVector3D() {
				public double get(int i) throws Exception {
					return t.get((i + 1) % 3);
				}
			};
			return t.crossProduct(otherVec);
			*/
		}
		
		public DynFloat divide(final DynVector3D v) {
			return new DynFloat() {
				public double get() throws Exception {
					double fac = 0;
					for(int i = 0; i < 4; ++i) {
						if(i == 3) throw new Exception("V3d.divide: v is zero");
						if(Math.abs(v.get(i)) > EPS) {
							fac = DynVector3D.this.get(i) / v.get(i);
							break;
						}
					}
					for(int i = 0; i < 3; ++i)
						if(Math.abs( DynVector3D.this.get(i) -  v.get(i) * fac ) > EPS)
							throw new Exception("V3d.divide: not parallel");
					return fac;
				}
			};			
		}
		
		public String toString() {
			return "(" + dynGet(0).toString() + "," + dynGet(1).toString() + "," + dynGet(2).toString() + ")";
		}

		public String toString2D() {
			return "(" + dynGet(0).toString() + "," + dynGet(1).toString() + ")";
		}
	}
	
	static public class pair < First > {
		public First x;
		public First y;
		public pair() {}
		public pair(First _x, First _y) { x = _x; y = _y; }

		public String toString() {
			return "<" + x.toString() + "," + y.toString() + ">";
		}
	}
	
	static public class Vector3D extends DynVector3D {
		public double x[] = new double[3];
		public Vector3D() {}
		public Vector3D(double a, double b, double c) { x[0] = a; x[1] = b; x[2] = c; }
		public Vector3D(double[] ds) { for(int i = 0; i < 3; ++i) x[i] = (double) ds[i]; }

		public double get(int i) { return x[i % 3]; }
		public void set(Vector3D v) { for(int i = 0; i < 3; ++i) x[i] = v.x[i]; }
	}
		
	static public class Point3D extends Vector3D {
		public Point3D() {}
		public Point3D(DynVector3D v) throws Exception { for(int i = 0; i < 3; ++i) x[i] = v.get(i); }
	}
	
	static public class Float extends DynFloat {
		public double x;
		public Float() {}
		public Float(double x_) { x = x_; }
		public double get() { return x; }
	}
	
	public class Viewport {
		Graphics g;
		Float eyeDistanceFactor = new Float(2.0f);
		Float eyeHeight = new Float(-30.0f);
		Vector3D eyeDir = new Vector3D(0,1,0);
		Vector3D xAxeDir = new Vector3D(1,0,0);
		Plane eyePlane = new Plane(eyeHeight, eyeDir);
		DynVector3D eyePoint = eyeDir.product(eyeHeight).product(eyeDistanceFactor);
		double scaleFactor = 60;
		
		public void rotate(Matrix3D rotateMatrix) {
			if(Math.abs(1 - rotateMatrix.det()) > EPS) return;
			eyeDir.set( rotateMatrix.product( eyeDir ).norminated().fixed() );
			xAxeDir.set( rotateMatrix.product( xAxeDir ).norminated().fixed() );
		}
		
		public void setGraphics(Graphics g) {
			this.g = g;
			g.setFont(Applet.defaultFont.deriveFont(14.0f));
			setColor(Color.black);
		}
		public void setColor(Color c) { g.setColor(c); }
		
		public double baseDistance() {
			return getEyePlane_PointDistance(new Point3D());			
		}
		
		public double maxSize() {
			return (double) ((Math.max(W, H) * 0.5 / viewport.scaleFactor) / viewport.eyeDistanceFactor.x);
		}
		
		public PGraph3D base() {
			return PGraph3D.this;
		}
		
		class Primitive {
			double dist;
			Color color;
			public Primitive() {}
			void draw() {}
			Point3D middle() { return null; }
		}
		
		class PrimitivePoint extends Primitive {
			Point3D p;
			PrimitivePoint(Point3D p_) { p = p_; }
			void draw() {
				try {
					java.awt.Point tp = translate(p);
					g.fillOval(tp.x - 3, tp.y - 3, 6, 6);					
				} catch (Exception e) {
					System.out.println("p = " + p);
					e.printStackTrace();
				}
			}
			Point3D middle() { return p; }
		}
		
		class PrimitiveText extends PrimitivePoint {
			String text;
			PrimitiveText(Point3D p, String t) {
				super(p);
				text = t;
			}
			void draw() {
				try {
					java.awt.Point tp = translate(p);
					g.drawString(text, tp.x - 3, tp.y - 3);
				} catch (Exception e) {
					System.out.println("p = " + p + ", t = " + text);
					e.printStackTrace();
				}				
			}
		}
		
		class PrimitivePolygon extends Primitive {
			Point3D[] p;
			PrimitivePolygon(Point3D[] p_) { p = p_; }
			void draw() {
				try {
					Polygon tpoly = new Polygon();
					for(int i = 0; i < p.length; ++i) {
						java.awt.Point tp = translate(p[i]);
						tpoly.addPoint(tp.x, tp.y);
					}
					g.fillPolygon(tpoly);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			Plane plane() {
				return new Plane(p[0], p[0].diff(p[1]), p[0].diff(p[2]));
			}
			Point3D middle() {
				if(p.length == 0) return new Point3D();
				DynVector3D sum = new Vector3D();
				for(int i = 0; i < p.length; ++i)
					sum = sum.sum(p[i]);
				return sum.product(new Float(1.0 / p.length)).fixed();
			}
		}
		
		class PrimitiveLine extends Primitive {
			Point3D p1, p2;
			PrimitiveLine(Point3D p1_, Point3D p2_) { p1 = p1_; p2 = p2_; }
			void draw() {
				try {
					java.awt.Point tp1 = translate(p1);
					java.awt.Point tp2 = translate(p2);
					//if(g instanceof Graphics2D) ((Graphics2D)g).setStroke(new BasicStroke(4));
					g.drawLine(tp1.x, tp1.y, tp2.x, tp2.y);
				} catch(Exception e) {
					System.out.println("p1 = " + p1);
					System.out.println("p2 = " + p2);
					e.printStackTrace();
				}
			}
			Point3D middle() {
				return p1.sum(p2).product(new Float(0.5)).fixed();
			}
		}
		
		SortedSet<Primitive> primitives = new TreeSet<Primitive>(new Comparator<Primitive>() {
			
			public int comparePoints(Point3D p1, Point3D p2) {
				double d = getEyePlane_PointDistance(p2) - getEyePlane_PointDistance(p1);
				if(d < -EPS) return -1;
				if(d > EPS) return 1;
				return 0;
			}
			
			public int compareLineLine(PrimitiveLine l1, PrimitiveLine l2) {
				return comparePoints(l1.middle(), l2.middle());
			}
			
			public int compareLinePolygon(PrimitiveLine l, PrimitivePolygon p) {
				Point3D lp = l.middle();
				Point3D pp = p.plane().intersectionPoint(new Line(lp, lp.diff(eyePoint))).point.fixed();
				
				return comparePoints(lp, pp);
			}
			
			public int compareLinePoint(PrimitiveLine l, PrimitivePoint p) {
				return comparePoints(l.middle(), p.p);
			}
			
			public int compareLine_(PrimitiveLine l, Primitive o) {
				if(o instanceof PrimitiveLine)
					return compareLineLine(l, (PrimitiveLine) o);
				if(o instanceof PrimitivePolygon)
					return compareLinePolygon(l, (PrimitivePolygon) o);
				return compareLinePoint(l, (PrimitivePoint) o);
			}
			
			public int comparePolygonPolygon(PrimitivePolygon p1, PrimitivePolygon p2) {
				return comparePoints(p1.middle(), p2.middle());
			}
			
			public int comparePolygonPoint(PrimitivePolygon pol, PrimitivePoint poi) {
				Point3D pp = pol.plane().intersectionPoint(new Line(poi.p, poi.p.diff(eyePoint))).point.fixed();
				return comparePoints(pp, poi.p);
			}
			
			public int comparePolygon_(PrimitivePolygon p, Primitive o) {
				if(o instanceof PrimitiveLine)
					return -compareLinePolygon((PrimitiveLine) o, p);
				if(o instanceof PrimitivePolygon)
					return comparePolygonPolygon(p, (PrimitivePolygon) o);
				return comparePolygonPoint(p, (PrimitivePoint) o);				
			}
			
			public int comparePointPoint(PrimitivePoint p1, PrimitivePoint p2) {
				return comparePoints(p1.p, p2.p);
			}
			
			public int comparePoint_(PrimitivePoint p, Primitive o) {
				if(o instanceof PrimitiveLine)
					return -compareLinePoint((PrimitiveLine) o, p);
				if(o instanceof PrimitivePolygon)
					return -comparePolygonPoint((PrimitivePolygon) o, p);
				return comparePointPoint(p, (PrimitivePoint) o);				
			}

			public int compare_(Primitive o1, Primitive o2) {
				if(o1 instanceof PrimitiveLine)
					return compareLine_((PrimitiveLine) o1, o2);
				if(o1 instanceof PrimitivePolygon)
					return comparePolygon_((PrimitivePolygon) o1, o2);
				return comparePoint_((PrimitivePoint) o1, o2);				
			}
			
			public int compare(Primitive o1, Primitive o2) {
				// complex compare doesnt work right now, some bug somewhere
				//int d = compare_(o1, o2);
				int d = comparePoints(o1.middle(), o2.middle());
				if(d != 0) return d;
				return o2.hashCode() - o1.hashCode();
			}
		});
		
		public void doFrame() {
			for(Primitive p : primitives) {
				setColorAtDepth( p.color, (float) p.dist );
				p.draw();
			}
			primitives.clear();
		}
		
		private void addPrimitive(double dist, Color color, Primitive p) {
			//if(p.dist < 0) return;
			p.dist = dist;
			p.color = color;
			primitives.add(p);
		}
		
		private double getEyePlane_PointDistance(Point3D p) {
			Point3D eyePlanePoint = getEyePlanePoint(p);
			Point3D relativeP = p.diff(eyePlanePoint).fixed();
			//Point3D relativeEye = eyePoint.diff(eyePlanePoint).fixed();
			
			try {
				double linP = eyePlane.normal.dotProduct(relativeP).get();
				//double linEye = eyePlane.normal.dotProduct(relativeEye).get();
				return linP;
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			return 10000000.0;
		}
		
		public Point3D getEyePlanePoint(Point3D p) {
			Line eyeLine = new Line();
			eyeLine.point = eyePoint;
			eyeLine.vector = p.diff(eyeLine.point);
			return eyePlane.intersectionPoint(eyeLine).point.fixed();
		}

		public Vector3D getRelAxeY() {
			Vector3D yAxe = xAxeDir.crossProduct(eyePlane.normal).norminated().fixed();
			return yAxe;
		}
		
		public Vector3D getRelAxeX() {
			Vector3D yAxe = getRelAxeY();
			Vector3D xAxe = eyePlane.normal.crossProduct(yAxe).norminated().fixed();
			return xAxe;
		}
		
		public java.awt.Point translate(Point3D p) throws Exception {
			Point3D eyePtAbs = getEyePlanePoint(p);
			if(eyePtAbs == null) throw new Exception("translate: plane point bad");
			
			Point3D eyePt = eyePtAbs.diff(eyePlane.basePoint()).fixed();
			Vector3D yAxe = getRelAxeY();
			Vector3D xAxe = getRelAxeX();
			
			java.awt.Point tp = new java.awt.Point();
			try {
				tp.x = (int) ( xAxe.dotProduct( eyePt ).get() * scaleFactor );
				tp.y = - (int) ( yAxe.dotProduct( eyePt ).get() * scaleFactor );
			} catch (Exception e) {
				// should not happen
				e.printStackTrace();
			}
			
			tp.x += W/2;
			tp.y += H/2;
			return tp;
		}
		
		private float CLAMP(float x, float min, float max) {
			return Math.min(max, Math.max(x, min));
		}
		
		private void setColorAtDepth(Color c, float d) {
			//this.g.setColor(c); if(0==0)return;
			
			// this is good but because ordering is wrong, we leave it out and want all transparent
			/*d /= (1.0f * baseDistance());
			d -= 0.9;*/
			
			d /= (1.5f * baseDistance());
			d = CLAMP(d, 0, 1);
			
			float r = (float) c.getRed() / 255;
			float g = (float) c.getGreen() / 255;
			float b = (float) c.getBlue() / 255;
			float a = (float) c.getAlpha() / 255;
			
			/*
			r = d + (1-d)*r;
			g = d + (1-d)*g;
			b = d + (1-d)*b;
			*/
			a = a * (1 - d);
			
			r = CLAMP(r, 0, 1);
			g = CLAMP(g, 0, 1);
			b = CLAMP(b, 0, 1);
			a = CLAMP(a, 0, 1);
			
			this.g.setColor(new Color(r, g, b, a));
		}
				
		private double getPointsDistance(Point3D[] ps) {
			double nearest = 10000000000.0;
			for(int i = 0; i < ps.length; ++i) {
				double dist = getEyePlane_PointDistance(ps[i]);
				nearest = Math.min(nearest, dist);
			}
			return nearest;
		}
		
		private void drawLine_LowLevel(final Point3D p1, final Point3D p2) throws Exception {
			double d = getPointsDistance(new Point3D[] {p1, p2});
			addPrimitive(d, g.getColor(), new PrimitiveLine(p1, p2));
		}
		
		public void drawPoint(final Point3D p) {
			double d = getEyePlane_PointDistance(p) - /* because of the radius */ 1;
			addPrimitive(d, g.getColor(), new PrimitivePoint(p));
		}

		public void drawText(final Point3D p, String text) {
			double d = getEyePlane_PointDistance(p);
			addPrimitive(d, g.getColor(), new PrimitiveText(p, text));
		}

		private void drawArrowItself(Point3D p, Vector3D v, Vector3D orth) {
			try {
				double arrowSize = 20.0 / scaleFactor;
				Point3D endp = p.sum(v).fixed();
				Point3D mp = endp.diff( v.norminated().product(new Float(arrowSize)) ).fixed();
				Point3D p1 = mp.sum( orth.product(new Float(arrowSize * 0.5)) ).fixed();
				Point3D p2 = mp.sum( orth.product(new Float(-arrowSize * 0.5)) ).fixed();
	
				drawLine(endp, p1.diff(endp).fixed(), false);
				drawLine(endp, p2.diff(endp).fixed(), false);
			}
			catch(NullPointerException e) {}			
		}
		
		public void drawArrow(Point3D p, Vector3D v) {
			drawLine(p, v, false);
			drawArrowItself(p, v, v.someOrthogonal().norminated().fixed());
			drawArrowItself(p, v, v.someOrthogonal().crossProduct(v).norminated().fixed());			
		}
		
		public void drawLine(Point3D p, Vector3D v, boolean infinite) {
			try {
				int splitNum = 1 + (int) (20.0f * Math.min(v.abs().get(), baseDistance()) / baseDistance());
				int start = 0;
				int end = splitNum;
				if(infinite) {
					// TODO: better
					start = -20;
					end = 20;
				}
				for(int i = start; i < end; ++i)
					drawLine_LowLevel(
							p.sum(v.product(new Float((double)(i) / splitNum))).fixed(),
							p.sum(v.product(new Float((double)(i+1) / splitNum))).fixed()
							);
				//drawLine_LowLevel(p, p.sum(v).fixed());
			} catch (Exception e) {
				// should not happen
				e.printStackTrace();
			}
		}
		
		public void drawPolygon(final Point3D[] p) {
			double d = getPointsDistance(p);
			addPrimitive(d, g.getColor(), new PrimitivePolygon(p));
		}
		
	}
	
	Viewport viewport = new Viewport();
	
	static public interface Object3D {
		public void draw(Viewport v);
	}
	
	static public class Line implements Object3D {
		DynVector3D point;
		DynVector3D vector;
		Color color = Color.black;
		boolean infinite = true;
		
		public Line() {}
		public Line(DynVector3D p, DynVector3D v) { point = p; vector = v; }  
		public Line(DynVector3D p, DynVector3D v, Color c) { point = p; vector = v; color = c; }  
		public Line(DynVector3D p, DynVector3D v, boolean inf, Color c) { point = p; vector = v; color = c; infinite = inf; }  
		public Line setColor(Color c) { color = c; return this; }
		
		public void draw(Viewport v) {
			if(point.isValid() && vector.isValid()) {
				v.setColor(color);
				v.drawLine( point.fixed(), vector.fixed(), infinite );
			}
		};
		
		public DynVector3D dynPoint() {
			return new DynVector3D() {
				public double get(int i) throws Exception {
					if(point == null) throw new Exception("Line.point == null");
					return point.get(i);
				}				
			};
		}
		
		public DynVector3D dynVector() {
			return new DynVector3D() {
				public double get(int i) throws Exception {
					if(vector == null) throw new Exception("vector == null");
					return vector.get(i);
				}				
			};
		}

		public boolean isValid() {
			try {
				return point.isValid() && vector.abs().get() > EPS;
			} catch (Exception e) {
				return false;
			}
		}
		
		public Point intersectionPoint(final Line l) {
			final Line t = this;
			
			final DynMatrix2D ms[] = new DynMatrix2D[3];
			
			for(int k = 0; k < 3; ++k) {
				final int K = k;
				// [ v1, -v2 ] * (t1 t2) = p2 - p1  ->  p1 + t1 v1 ( = p2 + t2 v2 ) is intersection point			
				ms[k] = new DynMatrix2D() {
					public double get(int i, int j) throws Exception {
						j %= 2;
						if(j == 0) return t.vector.get(i + K);
						else return -l.vector.get(i + K);
					}
				};
			}
			final DynVector3D pointDiff = l.point.diff( point );
			
			Point p = new Point();
			p.point = new DynVector3D() {
				public double get(int i) throws Exception {
					Exception exp = null;
					for(int k = 0; k < 3; ++k) {
						try {
							final pair<DynFloat> ts = ms[k].solve(new pair<DynFloat>(new Float(pointDiff.get(k)), new Float(pointDiff.get(k + 1))));
							double tx = ts.x.get();
							double ty = ts.y.get();
							
							// we must check now
							double check = vector.get(k + 2) * tx - l.vector.get(k + 2) * ty - (l.point.get(k + 2) - point.get(k + 2));
							if(Math.abs(check) > EPS) throw new Exception("no solution, check = " + check);
						
							return t.point.sum( t.vector.product(ts.x) ).get(i);
						}
						catch(Exception elocal) {
							if(exp == null) exp = elocal;
						}
					}
					String expStr = "null exception";
					if(exp != null) {
						expStr = exp.getMessage();
						for(int j = 0; j < exp.getStackTrace().length; ++j) {
							expStr = expStr + "\n  " + exp.getStackTrace()[j];
						}
					}
					throw new Exception("intersectionPoint: one param of one of the lines has thrown an exception:\n" + expStr);
				}
			};
			return p;
		}
		
		public DynFloat factorForPointOnLine(final DynVector3D p) {
			return p.diff(dynPoint()).divide(dynVector());
		}
		
		public boolean isPointOnLine(DynVector3D p) {
			return factorForPointOnLine(p).isValid();
		}
		
		public boolean lineIsEqual(Line l) {
			return isPointOnLine(l.point) && isPointOnLine(l.point.sum(l.vector));
		}
		
		public String toString(String name) {
			return name + "(t) = " + point.toString() + " + " + vector.toString() + "∙t";
		}
	}
	
	static public class VectorArrow extends Line {
		public VectorArrow() { super();	}
		public VectorArrow(DynVector3D p, DynVector3D v, Color c) { super(p, v, false, c); }

		public void draw(Viewport v) {
			if(point.isValid() && vector.isValid()) {
				v.setColor(color);
				v.drawArrow( point.fixed(), vector.fixed() );
			}
		}
	}
	
	static public class Point implements Object3D {
		DynVector3D point;
		Color color = Color.black;
		
		public Point() {}
		public Point(DynVector3D p, Color c) { point = p; color = c; }
		
		public void draw(Viewport v) {
			if(point.isValid()) {
				v.setColor(color);
				v.drawPoint(point.fixed());
			}
		}
		
		public DynVector3D dynPoint() {
			return new DynVector3D() {
				public double get(int i) throws Exception {
					if(point == null) throw new Exception("Point.point == null");
					return point.get(i);
				}
			};
		}
	}

	static public class Text extends Point {
		String text;
		public Text() { super(); }

		public Text(DynVector3D p, String t, Color c) {
			super(p, c);
			text = t;
		}

		public void draw(Viewport v) {
			if(point.isValid() && text != null) {
				v.setColor(color);
				v.drawText(point.fixed(), text);
			}
		}

	}
	
	static public abstract class DynMatrix2D {
		public double get(int i, int j) throws Exception { throw new Exception("DynMatrix2D::get not defined"); }
		
		public DynFloat det() {
			final DynMatrix2D t = this;			
			return new DynFloat() {
				public double get() throws Exception { return t.get(0,0) * t.get(1,1) - t.get(0,1) * t.get(1,0); }
			};
		}
		
		public pair<DynFloat> solve(final pair<DynFloat> v) {
			// using Cramer's rule
			final DynMatrix2D t = this;
			pair<DynFloat> p = new pair<DynFloat>();
			p.x = new DynFloat() {
				public double get() throws Exception {
					double top = v.x.get() * t.get(1,1) - t.get(0,1) * v.y.get();
					double bottom = t.det().get();
					if(Math.abs(bottom) > EPS) return top / bottom;
					throw new Exception("Matrix::solve: no solution for x");
				}
			};
			p.y = new DynFloat() {
				public double get() throws Exception {
					double top = t.get(0,0) * v.y.get() - v.x.get() * t.get(1,0);
					double bottom = t.det().get();
					if(Math.abs(bottom) > EPS) return top / bottom;
					throw new Exception("Matrix::solve: no solution for y");
				}
			};
			return p;
		}
		
		public DynVector3D mult(final DynVector3D v) {
			return new DynVector3D() {
				public double get(int i) throws Exception {
					i %= 3;
					if(i == 2) return v.get(i);
					return DynMatrix2D.this.get(i, 0) * v.get(0) + DynMatrix2D.this.get(i, 1) * v.get(1); 
				}
			};
		}
		
		private String get_toString(int i, int j) {
			try {
				return "" + get(i,j);
			} catch (Exception e) {
				return e.getMessage();
			}
		}
		
		public String toString() {
			return "[(" + get_toString(0,0) + "," + get_toString(0,1) + ") (" + get_toString(1,0) + "," + get_toString(1,1) + ")]";
		}
	}
	
	static public class Plane implements Object3D {
		DynFloat height;
		DynVector3D normal;
		Color color = Color.black;
		
		DynVector3D point; // obsolete but used for drawing as base point
		
		public Plane() {}
		public Plane(DynFloat height, DynVector3D normal) { this.height = height; this.normal = normal; point = basePoint(); }
		public Plane(DynFloat height, DynVector3D normal, Color c) { this.height = height; this.normal = normal; point = basePoint(); color = c; }

		public Plane(DynVector3D p, DynVector3D v1, DynVector3D v2) {
			normal = v1.crossProduct(v2);
			height = p.dotProduct(normal);
			point = p;
		}
		
		public Plane(DynVector3D p, DynVector3D normal) {
			this.normal = normal;
			height = p.dotProduct(normal);
			point = p;
		}
		
		DynVector3D basePoint() {
			return new DynVector3D() {
				public double get(int i) throws Exception {
					Line normalLine = new Line();
					normalLine.point = new Point3D();
					normalLine.vector = normal;
					return intersectionPoint(normalLine).point.get(i);
				}
			};
		}
		
		static public Plane xPlane = new Plane(new Float(0), new Vector3D(1,0,0));
		static public Plane yPlane = new Plane(new Float(0), new Vector3D(0,1,0));
		static public Plane zPlane = new Plane(new Float(0), new Vector3D(0,0,1));
		static public Plane[] basePlanes = new Plane[] { xPlane, yPlane, zPlane };
		
		Plane setColor(Color c) {
			color = c;
			return this;
		}
		
		public void draw(Viewport v) {
			v.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120));
			
			Vector3D v1 = normal.someOrthogonal().norminated().fixed(); if(v1 == null) return;
			Vector3D v2 = normal.crossProduct(v1).norminated().fixed(); if(v2 == null) return;

			for(int i = 4; i < 20; i += 6) {
				double s = 20 / v.scaleFactor;
				Float f = new Float(i * s);
				Point3D p1 = point.diff(v1.product(f)).fixed();
				Point3D p2 = point.diff(v2.product(f)).fixed();
				Point3D p3 = point.sum(v1.product(f)).fixed();
				Point3D p4 = point.sum(v2.product(f)).fixed();
							
				v.drawPolygon( new Point3D[] {p1, p2, p3, p4} );
			}
			
			/*
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
					for(int j = 0; j < 2; ++j) otherps[j] = points[ (i+j+1) % 3 ];
					for(int j = 0; j < 2; ++j) if(otherps[j] == null) return; // the whole plane seems invalid
					// somewhat pulled out of thin air
					for(int j = 0; j < 2; ++j) pointList.add( otherps[j].sum( basePlanes[(i+j) % 3].normal.product(new Float(5.0f)) ).fixed() );
				}
			}
			
			v.drawPolygon( pointList.toArray(new Point3D[] {}) );
			*/
		}
		
		public Line intersectionLine(Plane other) {
			Line l = new Line();
			l.vector = normal.crossProduct(other.normal);
			final DynVector3D normals[] = { normal, other.normal };
			// [ ( (n1 n1) (n1 n2) ) ( (n2 n1) (n2 n2) ) ] * (c1 c2) = (h1 h2)  ->  n1 c1 + n2 c2  is point on line
			DynMatrix2D nn = new DynMatrix2D() {
				public double get(int i, int j) throws Exception {
					i %= 2; j %= 2;
					return normals[i].dotProduct( normals[j] ).get();
				}
			};
			pair<DynFloat> heights = new pair<DynFloat>(height, other.height);
			final pair<DynFloat> c = nn.solve(heights);
			l.point = normal.product(c.x).sum( other.normal.product(c.y) );
			return l;
		}
		
		public DynFloat intersectionPoint_LineFactor(final Line line) {
			return new DynFloat() {
				public double get() throws Exception {
					double nv = normal.dotProduct(line.vector).get();
					double hnp = height.get() - normal.dotProduct(line.point).get();
					if(Math.abs(nv) < EPS) {
						if(Math.abs(hnp) < EPS) throw new Exception("Plane::intersectionPoint of line: line is on plane");
						else throw new Exception("Plane::intersectionPoint of line: there is no intersection point");
					}
					double t = hnp / nv; //nv / hnp;
					return t;
				}	
			};
		}
		
		public Point intersectionPoint(final Line line) {
			final DynFloat t = intersectionPoint_LineFactor(line);
			Point p = new Point();
			p.point = line.point.sum( line.vector.product(t) );
			return p;
		}
		
		public boolean isPointOnPlane(DynVector3D v) {
			try {
				return Math.abs(normal.dotProduct(v).get() - height.get()) < EPS;
			} catch (Exception e) {
				return false;
			} 
		}
		
		public boolean isLineOnPlane(Line l) {
			return isPointOnPlane(l.point) && isPointOnPlane(l.point.sum(l.vector));
		}
		
		public boolean isPlaneEqual(Plane p) {
			return isPointOnPlane(p.basePoint()) && normal.divide(p.normal).isValid();
		}
		
		public String toString() {
			try {
				return height.toString() + " = " + Math.round(normal.get(0)*10)*0.1 + "∙x + " + Math.round(normal.get(1)*10)*0.1 + "∙y + " + Math.round(normal.get(2)*10)*0.1 + "∙z";
			} catch (Exception e) {
				return "undefined";
			}
		}
	}
	
	static public class Matrix3D {
		Vector3D[] v = new Vector3D[] { new Vector3D(), new Vector3D(), new Vector3D() };
		public Matrix3D() {}
		public Matrix3D(double f) { for(int i = 0; i < 3; ++i) v[i].x[i] = f; }
		public Matrix3D(double[] m) {
			for(int i = 0; i < 3; ++i)
				for(int j = 0; j < 3; ++j)
					v[i].x[j] = m[j*3 + i];
		}
		
		public Matrix3D sum(Matrix3D m) {
			Matrix3D res = new Matrix3D();
			for(int i = 0; i < 3; ++i)
				for(int j = 0; j < 3; ++j)
					res.v[i].x[j] = v[i].x[j] + m.v[i].x[j];
			return res;
		}
		
		public Matrix3D product(Matrix3D m) {
			Matrix3D res = new Matrix3D();
			for(int i = 0; i < 3; ++i)
				for(int j = 0; j < 3; ++j)
					for(int k = 0; k < 3; ++k)
						res.v[i].x[j] += v[k].x[j] * m.v[i].x[k];
			return res;
		}

		public Matrix3D product(double f) {
			Matrix3D res = new Matrix3D();
			for(int i = 0; i < 3; ++i)
				for(int j = 0; j < 3; ++j)
					res.v[i].x[j] = v[i].x[j] * f;
			return res;
		}
		
		public Vector3D product(Vector3D v) {
			Vector3D res = new Vector3D();
			for(int i = 0; i < 3; ++i)
				for(int j = 0; j < 3; ++j)
					res.x[i] += this.v[j].x[i] * v.x[j];
			return res;
		}

		private double diag(int i, boolean pos) {
			int f = pos ? 1 : -1;
			return v[i%3].x[0] * v[(i+1*f+3)%3].x[1] * v[(i+2*f+3)%3].x[2];
		}
		
		public double det() {
			return diag(0,true) + diag(1,true) + diag(2,true) - diag(0,false) - diag(1,false) - diag(2,false);
		}

	}

	static interface FrameUpdate {
		void doFrameUpdate(Vector3D diff);
	}
	
	static public class Vector3DUpdater implements FrameUpdate {
		Vector3D point;
		DynVector3D updater;
		boolean allowZero = true;
		public Vector3DUpdater(Vector3D p, DynVector3D u) { point = p; updater = u; p.set(u.fixed()); }
		public Vector3DUpdater(Vector3D p, DynVector3D u, boolean immediateSet) { point = p; updater = u; if(immediateSet) p.set(u.fixed()); }
		
		public void doFrameUpdate(Vector3D diff) {
			Point3D updated = updater.fixed();
			if(updated != null) {
				if(!allowZero)
					try {
						if(Math.abs(updated.abs().get()) < EPS) return;
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				point.set(updated);
			}
		}
		
		Vector3DUpdater setAllowZero(boolean v) { allowZero = v; return this; }
	}

	static public class FloatUpdater implements FrameUpdate {
		Float point;
		DynFloat updater;
		public FloatUpdater(Float p, DynFloat u) {
			point = p; updater = u; 
			try { p.x = u.get(); } catch (Exception e) {} 
		}
		public FloatUpdater(Float p, DynFloat u, boolean immediateSet) {
			point = p; updater = u;
			if(immediateSet)
				try { p.x = u.get(); } catch (Exception e) {}
		}
		
		public void doFrameUpdate(Vector3D diff) {
			try {
				point.x = updater.get();
			} catch (Exception e) {}
		}
	}

	static public class MoveablePoint extends Point {
		List<FrameUpdate> updater = new LinkedList<FrameUpdate>();
		double snapGrid = 0;
		int clickPriority = 0; 
		public MoveablePoint(Vector3D p, Color c) { super(p, c); }
		public Point3D pointForPos(java.awt.Point p) { return null; }

		public void moveTo(java.awt.Point p) {
			Point3D pt = pointForPos(p);
			if(pt != null) {
				if(snapGrid > 0) {
					for(int i = 0; i < 3; ++i)
						pt.x[i] = Math.round(pt.x[i] / snapGrid) * snapGrid;
				}
				Vector3D diff = pt.diff(this.point).fixed();
				((Vector3D) this.point).set(pt);
				for(FrameUpdate u : updater) u.doFrameUpdate(diff);
			}
		}
		
		public void draw(Viewport v) {
			if(point.isValid()) {
				if(v.base().mouseOverPt == this)
					v.setColor(new Color(0,0,0,200));
				else
					v.setColor(color);
				v.drawPoint(point.fixed());
			}
		}
		
		public void mouseClicked() {}
		
		MoveablePoint setSnapGrid(double sg) { snapGrid = sg; return this; }
	}

	public class MoveablePointOnPlane extends MoveablePoint {
		Plane plane;
		public MoveablePointOnPlane(Vector3D p, Color c) { super(p, c); }
		public MoveablePointOnPlane(Vector3D p, Plane pl, Color c) { this(p, c); plane = pl; }
		
		public Point3D pointForPos(java.awt.Point p) {
			// if plane is almost orthogonal to viewport, point is very inaccurate -> dont do that movement
			try {
				if(Math.abs(plane.normal.dotProduct(viewport.eyeDir).get()) < 0.1) return null;
			} catch (Exception e) {
				return null;
			}

			// intersection from line(eye to ptOnEyePlane) to plane
			Line l = new Line();
			l.point = viewport.eyePoint;
			l.vector = pointOnEyePlane(p) .diff( l.point );			
			return plane.intersectionPoint(l).point.fixed();
		}
		
	}

	public class MoveablePointOnLine extends MoveablePointOnPlane {
		Line line;
		
		public MoveablePointOnLine(Vector3D p, Color c) {
			super(p, c);
			plane = new Plane(dynLinePoint(), dynLineVector(), viewport.eyeDir.crossProduct(dynLineVector()));
		}
		
		public MoveablePointOnLine(Vector3D p, Line l, Color c) {
			this(p, c);
			line = l;
		}
		
		protected DynVector3D dynLinePoint() {
			return new DynVector3D() {
				public double get(int i) throws Exception {
					if(line == null) throw new Exception("MoveablePointOnLine.line == null");
					return line.point.get(i);
				}
			};
		}
		
		protected DynVector3D dynLineVector() {
			return new DynVector3D() {
				public double get(int i) throws Exception {
					if(line == null) throw new Exception("MoveablePointOnLine.line == null");
					return line.vector.get(i);
				}
			};
		}

		public Point3D pointForPos(java.awt.Point p) {
			Point3D ptOnPlane = super.pointForPos(p);
			if(ptOnPlane == null) return null;
			
			// get normal line from ptOnPlane to line
			Line l = new Line();
			l.point = ptOnPlane;
			l.vector = line.vector.crossProduct( plane.normal );
			
			return l.intersectionPoint(line).point.fixed();
		}
		
	}
	
	public class MoveablePointDynamic extends MoveablePointOnLine {
		int baseLineIndex = 0;
		
		public MoveablePointDynamic(Vector3D p, Color c) {
			super(p, c);
			line = new Line();
			line.point = p;
			updateLine();
		}

		void updateLine() {
			line.vector = Plane.basePlanes[baseLineIndex].normal;
		}
				
		public void draw(Viewport v) {
			if(point.isValid()) {
				if(mouseOverPt == this && Calendar.getInstance().getTimeInMillis() % 1000 > 500)
					v.setColor(new Color(255,255,255,200));
				else
					v.setColor(color);
				
				v.drawPoint(point.fixed());
				
				if(mouseOverPt == this) {
					v.setColor(new Color(
							(baseLineIndex == 0) ? 255 : 0,
							(baseLineIndex == 1) ? 255 : 0,
							(baseLineIndex == 2) ? 255 : 0
							));
					v.drawLine(point.fixed(), line.vector.fixed(), true);
				}
				
				if(mouseOverPt == this) {
					new Timer() .schedule(new TimerTask() {
						public void run() {
							applet.repaint();
						}
					}, 500);
				}
			}
		}
		
		public void mouseClicked() {
			baseLineIndex++;
			baseLineIndex %= 3;
			updateLine();
		}

	}
	
	
	
	
	// ------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------

	boolean onlyXY = false;
	
	void setOnlyXY(boolean v) {
		onlyXY = v;
		if(v) {
			viewport.eyeDir.x[0] = 0;
			viewport.eyeDir.x[1] = 0;
			viewport.eyeDir.x[2] = -1;
			viewport.xAxeDir.x[0] = 1;
			viewport.xAxeDir.x[1] = 0;
			viewport.xAxeDir.x[2] = 0;			
		}
	}
	
	private Applet applet;
	public int W;
	public int H;
	
	public PGraph3D(Applet applet, int w, int h) {
		this.applet = applet;
		W = w;
		H = h;
	}

	public Set<Object3D> objects = new HashSet<Object3D>();
	
	public void paint(Graphics g) {		
		viewport.setGraphics(g);
		for(Object3D o : objects) {
			o.draw(viewport);
		}
		viewport.doFrame();

		// means we are rotating right now
		if(oldMousePoint != null) {
			g.setColor(Color.orange);
			String dist = "bad";
			dist = "" + Math.round(viewport.getEyePlane_PointDistance(new Point3D())*10.0)/10.0;
			try {
				dist += ";" + Math.round(viewport.getEyePlane_PointDistance(new Point3D(new Vector3D(10,10,10)))*10.0)/10.0;
				dist += ";" + Math.round(viewport.getEyePlane_PointDistance(new Point3D(new Vector3D(-10,-10,0)))*10.0)/10.0;
			} catch (Exception e) {
				dist += ";failed";
			}
			g.drawString(viewport.eyeDir.toString() + viewport.eyePoint.toString() + dist, 0, 10);
		}
		
		// means we are moving a point right now
		Point p = movedPoint;
		if(p == null) p = mouseOverPt;
		if(p != null) {
			g.setColor(p.color);
			g.drawString(p.point.toString(), 0, 10); 
		}
	}
	
	public void addBaseAxes() {
		double x = 400.0 / viewport.scaleFactor;
		if(onlyXY) {
			x *= 0.8;
			objects.add(new PGraph3D.VectorArrow(new PGraph3D.Vector3D(-x,0,0), new PGraph3D.Vector3D(x*2,0,0), Color.DARK_GRAY));
			objects.add(new PGraph3D.VectorArrow(new PGraph3D.Vector3D(0,-x,0), new PGraph3D.Vector3D(0,x*2,0), Color.DARK_GRAY));
		
			objects.add(new Text(new Vector3D(x, 0, 0), "x", Color.DARK_GRAY));
			objects.add(new Text(new Vector3D(0, x, 0), "y", Color.DARK_GRAY));
		}
		else {
			objects.add(new PGraph3D.VectorArrow(new PGraph3D.Vector3D(-x/2,0,0), new PGraph3D.Vector3D(x*1.75,0,0), Color.DARK_GRAY));
			objects.add(new PGraph3D.VectorArrow(new PGraph3D.Vector3D(0,-x/2,0), new PGraph3D.Vector3D(0,x*1.75,0), Color.DARK_GRAY));
			objects.add(new PGraph3D.VectorArrow(new PGraph3D.Vector3D(0,0,-1), new PGraph3D.Vector3D(0,0,x+1), Color.DARK_GRAY));
			
			objects.add(new Text(new Vector3D(x*1.25, 0, 0), "x", Color.DARK_GRAY));
			objects.add(new Text(new Vector3D(0, x*1.25, 0), "y", Color.DARK_GRAY));
			objects.add(new Text(new Vector3D(0, 0, x), "z", Color.DARK_GRAY));
		}
		
		for(int i = -(int)(x / 4); i <= (int)(x * 0.75); ++i) {
			if(i == 0) continue;

			double s = 0.01 * x;
			if(onlyXY) s *= 2;
			objects.add(new PGraph3D.Line(new PGraph3D.Vector3D(i,-s,0), new PGraph3D.Vector3D(0,s*2,0), false, Color.DARK_GRAY));
			objects.add(new PGraph3D.Line(new PGraph3D.Vector3D(i,0,-s), new PGraph3D.Vector3D(0,0,s*2), false, Color.DARK_GRAY));
			
			objects.add(new PGraph3D.Line(new PGraph3D.Vector3D(-s,i,0), new PGraph3D.Vector3D(s*2,0,0), false, Color.DARK_GRAY));
			objects.add(new PGraph3D.Line(new PGraph3D.Vector3D(0,i,-s), new PGraph3D.Vector3D(0,0,s*2), false, Color.DARK_GRAY));

			if(!onlyXY && i > 0) {
				objects.add(new PGraph3D.Line(new PGraph3D.Vector3D(0,-s,i), new PGraph3D.Vector3D(0,s*2,0), false, Color.DARK_GRAY));
				objects.add(new PGraph3D.Line(new PGraph3D.Vector3D(-s,0,i), new PGraph3D.Vector3D(s*2,0,0), false, Color.DARK_GRAY));
			}
		}
		
		for(int i = -15; i <= 15; ++i) {
			if(i == 0) continue;
			objects.add(new PGraph3D.Line(new PGraph3D.Vector3D(-20,i,0), new PGraph3D.Vector3D(40,0,0), false, Color.orange));
			objects.add(new PGraph3D.Line(new PGraph3D.Vector3D(i,-20,0), new PGraph3D.Vector3D(0,40,0), false, Color.orange));
						
			/*
			for(int l = -15; l <= 15; ++l) {
				final int j = i;
				final int k = l;
				objects.add(new Object3D() {
					public void draw(Viewport v) {
						v.setColor(new Color(40, 40, 40, 40));
						v.drawPolygon(new Point3D[] {
								new Vector3D(-k,-j,0).fixed(),
								new Vector3D(-k,j,0).fixed(),
								new Vector3D(k,j,0).fixed(),
								new Vector3D(k,-j,0).fixed(),						
						});
					}
				});
			}
			*/
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if(mouseOverPt != null) {
			mouseOverPt.mouseClicked();
			applet.repaint();
		}
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	static protected java.awt.Point pointFromEvent(MouseEvent e) {
		return new java.awt.Point(e.getX(), e.getY());
	}
	
	public Point3D pointOnEyePlane(java.awt.Point p) {
		/*
		Float eyeDistanceFactor = new Float(2.0f);
		Float eyeHeight = new Float(1.0f);
		Vector3D eyeDir = new Vector3D(-1,0,0);
		Plane eyePlane = new Plane(eyeHeight, eyeDir);
		DynVector3D eyePoint = eyeDir.product(eyeHeight).product(eyeDistanceFactor);
		double scaleFactor = 5;
		 */
		
		double x = (p.x - W/2) / viewport.scaleFactor;
		double y = -(p.y - H/2) / viewport.scaleFactor;
		
		//x /= viewport.eyeDistanceFactor.x;
		//y /= viewport.eyeDistanceFactor.x;
		
		return viewport.eyePlane.basePoint()
		.sum( viewport.getRelAxeX().product(new Float(x)) )
		.sum( viewport.getRelAxeY().product(new Float(y)) )
		.fixed();
	}
	
	public Point3D pointOnEyeGlobe(java.awt.Point p) {
		/*
		Float eyeDistanceFactor = new Float(2.0f);
		Float eyeHeight = new Float(1.0f);
		Vector3D eyeDir = new Vector3D(-1,0,0);
		Plane eyePlane = new Plane(eyeHeight, eyeDir);
		DynVector3D eyePoint = eyeDir.product(eyeHeight).product(eyeDistanceFactor);
		double scaleFactor = 5;
		 */
		
		double x = (p.x - W/2) / viewport.scaleFactor;
		double z = -(p.y - H/2) / viewport.scaleFactor;
		
		x /= viewport.eyeDistanceFactor.x;
		z /= viewport.eyeDistanceFactor.x;
		
		double maxsize = viewport.maxSize();

		double a = Math.sqrt(x*x + z*z); 
		if(a > maxsize) {
			x *= maxsize / a;
			z *= maxsize / a;
		}
		
		double y = maxsize*maxsize - x*x - z*z;
		if(y < 0) y = 0; else y = (double) Math.sqrt(y);
		
		Point3D globePos = new Point3D();
		globePos.x[0] = x;
		globePos.x[1] = -y;
		globePos.x[2] = z;
	
		//objects.add(new Point(globePos.sum(new Vector3D(0,y,0))));
		
		return globePos.norminated().fixed();
	}
	
	java.awt.Point oldMousePoint = null;
	MoveablePoint movedPoint = null;
	
	public MoveablePoint moveablePointAt(final java.awt.Point p) {
		SortedSet<MoveablePoint> pts = new TreeSet<MoveablePoint>(new Comparator<MoveablePoint>() {
			public int compare(MoveablePoint o1, MoveablePoint o2) {
				if(o1.clickPriority != o2.clickPriority) return o2.clickPriority - o1.clickPriority;
				try {
					double d1 = p.distance( viewport.translate(o1.point.fixed()) );
					double d2 = p.distance( viewport.translate(o2.point.fixed()) );
					if(d2 < d1) return -1;
					if(d2 > d1) return 1;
				} catch(Exception e) {}
				return 0;
			}
		});
		for(Object3D o : objects) {
			if(o instanceof MoveablePoint) {
				MoveablePoint mp = (MoveablePoint) o;
				try {
					if( p.distance( viewport.translate(mp.point.fixed()) ) < 6 )
						pts.add(mp);
				} catch (Exception e) {}
			}
		}
		if(pts.size() > 0)
			return pts.first();
		return null;
	}
	
	public void mousePressed(MouseEvent e) {
		java.awt.Point pressedp = pointFromEvent(e);
		movedPoint = moveablePointAt( pressedp );
		if(movedPoint == null && !onlyXY)
			oldMousePoint = pressedp;
	}
	
	public void mouseReleased(MouseEvent e) {
		oldMousePoint = null;
		movedPoint = null;
	}
	
	public void mouseDragged(MouseEvent e) {
		if(oldMousePoint != null) {
			Point3D globePosOld = pointOnEyeGlobe( oldMousePoint );
			Point3D globePosNew = pointOnEyeGlobe( pointFromEvent(e) );
			
			Matrix3D rotateM = getRotateMatrixForPoint(globePosNew, false).product( getRotateMatrixForPoint(globePosOld, true) );
					
			viewport.rotate(rotateM);
			
			oldMousePoint = pointFromEvent(e);
		}
		else if(movedPoint != null) {
			movedPoint.moveTo( pointFromEvent(e) );
		}
		else
			return;
		
		applet.repaint();
	}
	
	static Matrix3D getRotateMatrix(Vector3D rotateAxe, double cos_a, double sin_a) {
		double[] v = rotateAxe.x;
		Matrix3D rotateM = new Matrix3D(new double[] {
				cos_a + v[0]*v[0]*(1 - cos_a),		v[0]*v[1]*(1 - cos_a) - v[2]*sin_a,	v[0]*v[2]*(1 - cos_a) + v[1]*sin_a,
				v[1]*v[0]*(1 - cos_a) + v[2]*sin_a,	cos_a + v[1]*v[1]*(1 - cos_a),		v[1]*v[2]*(1 - cos_a) - v[0]*sin_a,
				v[2]*v[0]*(1 - cos_a) - v[1]*sin_a,	v[2]*v[1]*(1 - cos_a) + v[0]*sin_a,	cos_a + v[2]*v[2]*(1 - cos_a)
		});
		return rotateM;
	}
	
	static Matrix3D getRotateMatrixForPoint(Point3D globePos, boolean swapRotate) {
		// calculate rotation matrix which rotates (0,-1,0) <- globePos

		double x = globePos.x[0];
		double z = globePos.x[2];

		final Vector3D rotateAxe = globePos.crossProduct( new Vector3D(0,-1,0) ).norminated().fixed();
		final double[] v = rotateAxe.x;
				
		double sin_a = 0, cos_a = 0;
		if(Math.abs(v[1]) > EPS) try {
			// TODO: case v0 == 0 || v1 == 0
			
			DynMatrix2D m = new DynMatrix2D() {
				double[] m = new double[] {
						v[0] * v[1], - v[2],
						v[1] * v[2], v[0]
				};
				public double get(int i, int j) throws Exception {
					return m[i * 2 + j];
				}
			};
			
			pair<DynFloat> cossin_a = m.solve( new pair<DynFloat>(new Float(-x), new Float(-z)) );

			sin_a = cossin_a.y.get();
			cos_a = 1.0f - cossin_a.x.get();
		} catch (Exception e1) {
			// should not happen
			e1.printStackTrace();
		}
		else { // v[1] == 0, we always have that case right now ...
			if(Math.abs(v[0]) > Math.abs(v[2]) && Math.abs(v[0]) > EPS) {
				sin_a = - z / v[0];
			}
			else if(Math.abs(v[2]) > EPS) {
				sin_a = x / v[2];
			}
			else {
				sin_a = 1f;				
			}
			cos_a = (double) Math.sqrt(1 - sin_a*sin_a);
		}
		
		if(swapRotate)
			// rotate in the other direction (g -> 0,-1,0) than what we calculated (0,-1,0 -> g)
			sin_a = - sin_a;

		return getRotateMatrix(rotateAxe, cos_a, sin_a);
	}
	
	public MoveablePoint mouseOverPt = null;
	
	public void mouseMoved(MouseEvent e) {
		MoveablePoint p = moveablePointAt( pointFromEvent(e) );
		if(p != null)
			applet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else
			applet.setCursor(Cursor.getDefaultCursor());

		if(mouseOverPt != p) {
			mouseOverPt = p;			
			applet.repaint();
		}
	}
	
	public String getResultMsg() {
		return "";
	}
	
	public boolean isCorrect() {
		return false;
	}

	
}
