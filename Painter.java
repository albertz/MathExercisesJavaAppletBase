package applets.Abbildungen_I63_Part1_UrbildX2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

class Painter implements VTImage.PainterAndListener {
	// Funktionsplotter / Graphzeichner
	
	private final Applet applet;
	private int W, H;
	
	public Function2D function = new Function2D() {
		public double get(double x) {
			return Math.cos(x);
		}
	};
	
	public double x_l = -4 * Math.PI, x_r = 4 * Math.PI;
	public double y_u = -1.2, y_o = 1.2;
	public int xspace_l = 30, xspace_r = 30;
	public int yspace_u = 20, yspace_o = 70;
	
	public double axeXStep = Math.PI / 2;
	public double axeXMult = 0.5;
	public int axeXTextStep = 2;
	public String axeXPostText = "π";
	public double axeYStep = 0.1;
	public double axeYMult = 0.1;
	public int axeYTextStep = 10;
	public String axeYPostText = "";
	
	public int state = 0;
	public String[] stateMsgs = null;
	public int stateMsgX = 25, stateMsgY = 25;
	
	public double selectedX1 = 0, selectedX2 = 0;
	public double selectedX = 0;
	public double selectedY = -100;

	protected int simulationX2Ypos = 0;
	protected int simulationX2Ydir = -1; // 1 = pos; -1 = neg
	protected Timer simulationX2Ytimer = null;
	
	public void setXYValuesInversFrom(Painter src) {
		x_l = src.y_u;
		x_r = src.y_o;
		y_u = src.x_l;
		y_o = src.x_r;
		axeXStep = src.axeYStep;
		axeXMult = src.axeYMult;
		axeXTextStep = src.axeYTextStep;
		axeXPostText = src.axeYPostText;
		axeYStep = src.axeXStep;
		axeYMult = src.axeXMult;
		axeYTextStep = src.axeXTextStep;
		axeYPostText = src.axeXPostText;
						
		// keep this, perhaps looks better
		xspace_l = src.xspace_l;
		xspace_r = src.xspace_r;
		yspace_o = src.yspace_o;
		yspace_u = src.yspace_u;
	}
	
	public void paint(Graphics g) {
		// Hintergrund
		g.setColor(new Color(250, 250, 250));
		g.fillRect(0, 0, W, H);
		
		drawAchsen(g);
		drawAchsentext(g);
		drawSelectionXPos(g);
		drawSelectionYPos(g);
		drawSelectionXRange(g);
		//drawSimulationX2Y(g);
		drawStateMsg(g);
		
		// Funktion
		g.setColor(Color.BLUE);
		drawFunction(g);

	}
	
	protected void stopSimulationX2Y() {
		simulationX2Ypos = 0;
		try {
			if(simulationX2Ytimer != null) simulationX2Ytimer.cancel();
		} catch(IllegalArgumentException e) {}
		applet.repaint();
	}
	
	protected void resetSimulationX2Y() {
		stopSimulationX2Y();
		
		simulationX2Ytimer = new Timer();
		simulationX2Ytimer.schedule(new TimerTask() {
			public void run() {
				simulationX2Ypos += 10;
				applet.repaint();
			}
		}, 0, 50);
	}
	
	protected void drawSimulationX2Y(Graphics g) {
		int s = (int)Math.signum(function.get(selectedX));
		int f_x = transformY(function.get(selectedX));
		int x = transformX(selectedX);
		int len = Math.abs(x - transformX(0)) + Math.abs(transformY(0) - f_x); 
		int pos = (int)(((double)len / 200) * simulationX2Ypos);
		if(pos != 0) pos %= len + 20;
		if(simulationX2Ydir < 0) pos = 20 + len - pos;
		int y = transformY(0) - s * pos;
		if(s * y < s * f_x) {
			x -= Math.signum(selectedX) * Math.abs(f_x - y);
			if(Math.signum(selectedX) * x < Math.signum(selectedX) * transformX(0)) x = transformX(0);
			y = f_x;
		}
		g.setColor(new Color(255, 123, 50, 200));
		g.fillOval(x - 3, y - 3, 6, 6);
	}
	
	protected String getStateMsg() {
		if(stateMsgs != null && state < stateMsgs.length) {
			return stateMsgs[state];
		}
		return "";
	}
	
	protected void drawStateMsg(Graphics g) {
		String msg = getStateMsg();
		msg = msg.replace("%x%", "" + ((double)Math.round(selectedX * 10) / 10));
		msg = msg.replace("%x1/pi%", "" + ((double)Math.round(selectedX1 * 10 / Math.PI) / 10));
		msg = msg.replace("%x2/pi%", "" + ((double)Math.round(selectedX2 * 10 / Math.PI) / 10));
		msg = msg.replace("%x1%", "" + ((double)Math.round(selectedX1 * 10) / 10));
		msg = msg.replace("%x2%", "" + ((double)Math.round(selectedX2 * 10) / 10));
		msg = msg.replace("%y%", "" + ((double)Math.round(selectedY * 10) / 10));
		String[] lines = msg.split("\n");
		for(int i = 0; i < lines.length; i++) {
			if(lines[i].length() > 0) {
				double w = g.getFontMetrics().getStringBounds(lines[i], g).getWidth();
				g.setColor(Color.white);
				g.fillRect(stateMsgX - 4, stateMsgY - 18 + i*25, (int)w + 8, 25);
				g.setColor(new Color(122, 123, 50));
				g.drawString(lines[i], stateMsgX, stateMsgY + i*25);
			}
		}
	}
	
	protected void drawSelectionXRange(Graphics g) {
		g.setColor(new Color(123, 255, 50, 80));
		g.fillRect(transformX(selectedX1), 0, transformX(selectedX2) - transformX(selectedX1), H);
		
		g.setColor(new Color(123, 255, 50, 200));
		g.drawLine(transformX(selectedX1), 0, transformX(selectedX1), H);
		g.drawLine(transformX(selectedX2), 0, transformX(selectedX2), H);
	}
	
	protected void drawSelectionXPos(Graphics g) {
		g.setColor(new Color(255, 255, 50, 200));
		g.drawLine(transformX(selectedX), 0, transformX(selectedX), H);
	}

	protected void drawSelectionYPos(Graphics g) {
		g.setColor(new Color(255, 255, 50, 200));
		g.drawLine(0, transformY(selectedY), W, transformY(selectedY));
	}

	protected void drawAchsen(Graphics g) {
		g.setColor(Color.GRAY);
		int x = transformX(0), y = transformY(0);
		if(x < xspace_l) x = xspace_l;
		if(x > W - xspace_r) x = W - xspace_r;
		if(y < yspace_o) y = yspace_o;
		if(y > H - yspace_u) y = H - yspace_u;
		g.drawLine(x, 0, x, H);
		g.drawLine(0, y, W, y);
	}

	protected void drawAchsentext(Graphics g) {
		int x = transformX(0), y = transformY(0);
		if(x < xspace_l) x = xspace_l;
		if(x > W - xspace_r) x = W - xspace_r;
		if(y < yspace_o) y = yspace_o;
		if(y > H - yspace_u) y = H - yspace_u;
		double px = 0;
		for(; px > x_l; px -= axeXStep) {}
		for(; px <= x_r; px += axeXStep) {
			g.drawLine(transformX(px), y, transformX(px), y + 5);
			long n = Math.round(px / axeXStep);
			if(n % axeXTextStep == 0) {
				g.drawString("" + Math.round(axeXMult * px / axeXStep) + axeXPostText, transformX(px) - 10, y + 20);
			}
		}

		double py = 0;
		for(; py > y_u; py -= axeYStep) {}
		for(; py <= y_o; py += axeYStep) {
			g.drawLine(x, transformY(py), x - 5, transformY(py));
			long n = Math.round(py / axeYStep);
			if(n % axeYTextStep == 0) {
				g.drawString("" + Math.round(axeYMult * py / axeYStep) + axeYPostText, x - 25, transformY(py) + 10);
			}
		}
	}
	
	protected void drawFunction(Graphics g) {
		double step = (x_r - x_l) / 100;
		int last_y = transformY(function.get(x_l));
		int y;
		for(double x = x_l + step; x <= x_r; x += step) {
			y = transformY(function.get(x));
			g.drawLine(transformX(x - step), last_y, transformX(x), y);
			last_y = y;
		}
	}
	
	public int transformX(double x) {
		return xspace_l + (int) ((x - x_l) * (W - xspace_r - xspace_l) / (x_r - x_l));
	}
	
	public int transformY(double y) {
		return H - yspace_u - (int) ((y - y_u) * (H - yspace_o - yspace_u) / (y_o - y_u));
	}
	
	public double retransformX(int x) {
		return x_l + (double)(x - xspace_l) * (x_r - x_l) / (W - xspace_r - xspace_l);
	}
	
	public double retransformY(int y) {
		return y_u + (double)(H - yspace_u - y) * (y_o - y_u) / (H - yspace_o - yspace_u);
	}
	
	public Painter(Applet applet, int W, int H) {
		this.applet = applet;
		this.W = W; this.H = H;
		reset();
	}
	
	
	public boolean isCorrect() {
		return exs[excNr].isCorrect(selectedX1 / Math.PI, selectedX2 / Math.PI);
	}
	
	public String getResultText() {
		if(isCorrect())
			return "das ist korrekt";
		else
			return "leider ist das nicht korrekt";
	}
	
	class ExcSet {
		public double x; // x (/pi)
		
		public ExcSet(double x) {
			this.x = x;
		}
		
		// x1,x2 results (/pi)
		public boolean isCorrect(double x1, double x2) {
			if(x2 != x1 + 1) return false;
			if(x % 1 == 0) return x == x1 || x == x2;
			return Math.floor(x) == x1;
		}
	}
	
	public static interface Function2D {
		double get(double x);
	}

	public int excNr = 0;
	public ExcSet[] exs = new ExcSet[] {
			new ExcSet(0.25), new ExcSet(-1), new ExcSet(3.5), new ExcSet(0),
			new ExcSet(2.25), new ExcSet(-0.5), new ExcSet(-3.25), 
	};
	
	public void reset() {
		ExcSet e = exs[excNr];
		String baseStateMsg =
			"Geben Sie ein maximales Intervall an, " +
			"in dem " + e.x + "π enthalten ist und " +
			"für das Cosinus injektiv ist.";
		stateMsgs = new String[] {
			baseStateMsg + "\n(*x1 = %x1/pi%π, x2 = %x2/pi%π)",
			baseStateMsg + "\n(x1 = %x1/pi%π, *x2 = %x2/pi%π)",
			baseStateMsg + "\n(x1 = %x1/pi%π, x2 = %x2/pi%π)",
		};
		selectedX = e.x * Math.PI;
	}
	
	public void next() {
		excNr++; excNr %= exs.length;
		applet.updater.run();
		reset();
		applet.repaint();
	}
	
	protected void doSelectionXRange(int state, int mouse_x) {
		double step = axeXStep / 2;
		double x = retransformX(mouse_x);
		x = step * Math.round(x / step);
		
		switch(state) {
		case 0: selectedX1 = x; break;
		case 1: selectedX2 = x; break;
		}
	}
	
	protected void doSelectionXPos(int mouse_x, boolean stepWise) {
		double step = axeXStep / 2;
		double x = retransformX(mouse_x);
		if(stepWise)
			x = step * Math.round(x / step);
		if(x < x_l) x = x_l;
		if(x > x_r) x = x_r;
		selectedX = x;
	}

	protected void doSelectionYPos(int mouse_y, boolean stepWise) {
		double step = axeYStep / 2;
		double y = retransformY(mouse_y);
		if(stepWise)
			y = step * Math.round(y / step);
		if(y < y_u) y = y_u;
		if(y > y_o) y = y_o;
		selectedY = y;
	}
	
	public void mouseClicked(MouseEvent e) {
		mouseMoved(e);
		state++; state %= 3;
	}
	public void mouseMoved(MouseEvent e) {
		if(state < 2) {
			doSelectionXRange(state, e.getX());
			applet.updater.run();
			applet.repaint();
		}
		//doSelectionYPos(transformY(function.get(retransformX(e.getX()))), false);
	}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}

}
