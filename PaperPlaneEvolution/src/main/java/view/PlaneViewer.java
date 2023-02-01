package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.engine.Engine;
import model.grammar.AbstractGrammar.Symbol;
import model.plane.BasicPlane;
import model.plane.shapes.BasicPlaneShape;
import model.grammar.Chromosome;
import model.grammar.StandardGrammar;
import util.Matrix;
import util.RandomSingleton;
import util.Triangle;
import util.Vector3D;



public class PlaneViewer extends JPanel{
	ProjectionUtil renderer;
	
	BasicPlane plane;
	List<Triangle> tris;
	
	public PlaneViewer(BasicPlane plane){
		this.plane = plane;
		this.renderer = new ProjectionUtil();
		
		Dimension size = new Dimension(renderer.width, renderer.height);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setPreferredSize(size);
		
		this.init();
		

		MouseAdapter mouseA = new MouseAdapter() {
			
			boolean pressed = false;
    		Point current = null;
    		@Override
			public void mousePressed(MouseEvent e) {
    			//advance(0.0005f);
    			if(!pressed) {
    				pressed = true;
        			current = e.getPoint();
    			}
			}
    		@Override
			public void mouseReleased(MouseEvent e) {pressed = false;}
			@Override
			public void mouseDragged(MouseEvent e) {
				if(pressed) {
					Point dir = e.getPoint();
					if(dir.equals(current))return;
					int dx = dir.x-current.x;
					int dy = dir.y-current.y;
					
					double decreaseFactor = 10000.0d;
					if(Math.abs(dx) >10)renderer.fYaw +=(double)dx/decreaseFactor;
					if(Math.abs(dy) >10)renderer.fXaw +=(double)dy/decreaseFactor;
							
					repaint();
				}
			}
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Vector3D vForward = Vector3D.mul(renderer.lookDir, -((double)e.getWheelRotation())*0.3d);
				renderer.camera = Vector3D.add(renderer.camera, vForward);
				repaint();
			}
		};
		this.addMouseListener(mouseA);
		this.addMouseMotionListener(mouseA);;
		this.addMouseWheelListener(mouseA);
		
		
		KeyListener keyListener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent ke) {
				int c = ke.getKeyCode();
				switch(c) {
				  case KeyEvent.VK_S:
					  renderer.camera.y-=0.8d;
				    break;
				  case KeyEvent.VK_W:
					  renderer.camera.y+=0.8d;
				    break;
				  case KeyEvent.VK_D:
					  renderer.camera.x-=0.8d;
				    break;
				  case KeyEvent.VK_A:
					  renderer.camera.x+=0.8d;
				    break;
				  case KeyEvent.VK_R:
					  renderer.camera.z-=0.8d;
				    break;
				  case KeyEvent.VK_F:
					  renderer.camera.z+=0.8d;
				    break;

				  default:				    
				}
				repaint();
			}

			@Override
			public void keyReleased(KeyEvent ke) {}
			@Override
			public void keyTyped(KeyEvent ke) {}
			
		};
		this.addKeyListener(keyListener);
		this.setFocusable(true);
        this.requestFocusInWindow();
	}

	private void init() {
		
		this.repaint();
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2.setColor(Color.white);
		//g2.fillOval(500, 450, 10, 10);
		//g2.fillOval(651, 368, 10, 10);
		//g2.drawString("HOLA", this.time, 15f);
		
		this.tris = this.plane.getTriangles();

		g2.setStroke(new BasicStroke(3));
		
		List<Triangle>trianglesToRaster = this.renderer.triangles(this.tris);
		
		for(Triangle t:trianglesToRaster) {
			//g2.setColor(t.col==null ? new Color(0,0,0,100) : t.col);
			g2.setColor(new Color(0,255,0,150));
			g2.fillPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);
			
			g2.setColor(new Color(0,0,0,150));
			g2.drawPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);
			
		}


		g2.setStroke(new BasicStroke(5));
		
		Vector3D origin = Vector3D.of(2, 5, -5);
		Vector3D wind = Vector3D.mul(Vector3D.of(0, 0.5, 0.5), 20d);
		
		List<Vector3D>phs = new ArrayList<>();
		for(Triangle t:this.tris) {
			Vector3D tmp = Engine.intersectionTriangle(t.points[0], t.points[1], t.points[2], origin, wind);
			if(tmp!=null) {
				phs.add(tmp);
			}
		}
		

		List<Vector3D>vs = new ArrayList<>();
		vs.add(origin);
		vs.add(Vector3D.add(wind, origin));
		vs.addAll(phs);
		List<Vector3D>vectorsToRaster = this.renderer.vectors(vs);
		//for(Vector3D v:vectorsToRaster) {
			g2.setColor(new Color(0,255,255,150));
			g2.drawLine((int)vectorsToRaster.get(0).x, (int)vectorsToRaster.get(0).y, 
						(int)vectorsToRaster.get(1).x, (int)vectorsToRaster.get(1).y);
		//}

		for(int i=2;i<vectorsToRaster.size();i++) {
			Vector3D v = vectorsToRaster.get(i);
			g2.setColor(new Color(0,0,0,200));
			g2.drawOval((int)v.x, (int)v.y, 3, 3);
		}
			
		
	}
	public static void main(String args[]) {
		RandomSingleton.setSeed(11L);
		Chromosome<Chromosome.Codon> c = new Chromosome<>(500, Chromosome.Codon::new);
		StandardGrammar grammar = new StandardGrammar();
		grammar.parseBNF("resources/grammar/default.bnf");
		LinkedList<Symbol>ss = (LinkedList<Symbol>) grammar.mapChromosome(c);
		StringBuilder sb = new StringBuilder();
		for(Symbol symb:ss)sb.append(symb.getName());
		
		System.out.println(sb.toString());

		
		
		//String test = "plane(40,0,45,10);cuad(upperRightCorner,+(upperRightCorner,N(0,0,5)),+(upperRightCorner,N(5,-3,7)),endBase);";
		/*String test = "plane(40,0,20,10);tri(upperRightCorner,+(upperRightCorner,N(5,-2,7)),endBase);tri(upperRightCorner,+(upperRightCorner,N(0,0,5)),+(upperRightCorner,N(5,-2,7)));"+
						"tri(upperLeftCorner,+(upperLeftCorner,N(5,-2,-7)),endBase);tri(upperLeftCorner,+(upperLeftCorner,N(0,0,-5)),+(upperLeftCorner,N(5,-2,-7)));"+
						"tri(+(N(-1,0,0),endBase),+(N(5,0,-10),minZ),+(N(0,0,0),minZ));"+
						"tri(+(N(-1,0,0),endBase),+(N(5,0,10),maxZ),+(N(0,0,0),maxZ));";
		*/
		/*String test = "plane(40,0,20,10,3);"+ 
				"tri(upperRightCorner,+(upperRightCorner,N(5,-2,7)),endRightCorner);tri(upperRightCorner,+(upperRightCorner,N(0,0,5)),+(upperRightCorner,N(5,-2,7)));"+
				"tri(upperLeftCorner,+(upperLeftCorner,N(5,-2,-7)),endLeftCorner);tri(upperLeftCorner,+(upperLeftCorner,N(0,0,-5)),+(upperLeftCorner,N(5,-2,-7)));"+
				"tri(endLeftCorner,+(N(5,0,-10),minZ),+(N(0,0,0),minZ));"+
				"tri(endRightCorner,+(N(5,0,10),maxZ),+(N(0,0,0),maxZ));"
				;
		 */
		String test = "plane(40,0,20,10,3);";
		BasicPlane plane = null;

		int op = 0;
		if(op==0) {
			plane = BasicPlaneShape.parsePlaneAndShapes(test);
			plane.breakTriangles(3);
			plane.consolidate();
			plane.getTriangles().stream().forEach(s -> System.out.println(s));
			
		}
		else {
			plane = BasicPlaneShape.parsePlaneAndShapes(sb.toString());
			plane.consolidate();
		}
		
		
		System.out.println(plane);
		

		
		PlaneViewer panel = new PlaneViewer(plane);
		SwingUtilities.invokeLater(()->{
			JFrame frame = new JFrame();
			Dimension size = new Dimension(600, 600);
			//frame.setMinimumSize(size);
			//frame.setMaximumSize(new Dimension(1000, 1000));
			//frame.setPreferredSize(size);
			frame.setContentPane(panel);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocation(800, 100);
			frame.setVisible(true);
		});
		
		Engine engine = new Engine(plane);
		while(true) {
			try {
				Thread.sleep(300L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			engine.step(0.05d);
			panel.repaint();
		}
	}
}
