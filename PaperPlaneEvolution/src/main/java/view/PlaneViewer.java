package view;

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
	Matrix projectionMatrix;
	int width=1000,height=900;
	double zfar=1000d,znear=0.1d;
	double a=(double)width/(double)height,f=90d,q=zfar/(zfar-znear);
	double frad = (double) (1d/ (double)Math.tan(f*0.5d/180d*3.14159d));
	double offset = 28d;
	double time = 0.0d;
	Vector3D camera;
	Vector3D lookDir;
	double fYaw,fXaw;
	
	BasicPlane plane;
	List<Triangle> tris;
	
	public PlaneViewer(BasicPlane plane){
		this.plane = plane;
		
		Dimension size = new Dimension(width, height);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setPreferredSize(size);
		
		this.init();
		
		this.projectionMatrix = Matrix.Matrix_MakeProjection(f, a, znear, zfar);
		
		camera = new Vector3D(0d,0d,0d);
		lookDir = new Vector3D(0d,0d,1d);
		
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
					if(Math.abs(dx) >10)PlaneViewer.this.fYaw +=(double)dx/decreaseFactor;
					if(Math.abs(dy) >10)PlaneViewer.this.fXaw +=(double)dy/decreaseFactor;
							
					repaint();
				}
			}
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Vector3D vForward = Vector3D.mul(PlaneViewer.this.lookDir, -((double)e.getWheelRotation())*0.3d);
				PlaneViewer.this.camera = Vector3D.add(PlaneViewer.this.camera, vForward);
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
					  PlaneViewer.this.camera.y-=0.8d;
				    break;
				  case KeyEvent.VK_W:
					  PlaneViewer.this.camera.y+=0.8d;
				    break;
				  case KeyEvent.VK_D:
					  PlaneViewer.this.camera.x-=0.8d;
				    break;
				  case KeyEvent.VK_A:
					  PlaneViewer.this.camera.x+=0.8d;
				    break;
				  case KeyEvent.VK_R:
					  PlaneViewer.this.camera.z-=0.8d;
				    break;
				  case KeyEvent.VK_F:
					  PlaneViewer.this.camera.z+=0.8d;
				    break;

				  default:				    
				}
				repaint();
			}

			@Override
			public void keyReleased(KeyEvent ke) {
				
			}

			@Override
			public void keyTyped(KeyEvent ke) {
			}
			
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
		
		Matrix zrotation = Matrix.Matrix_MakeRotationZ(time);
		Matrix xrotation = Matrix.Matrix_MakeRotationX(time);
		
		Matrix matTrans = Matrix.translate(0d, 0d, offset);
		
		Matrix matWorld = Matrix.identity();
		matWorld = zrotation.multiply(xrotation);
		matWorld = matWorld.multiply(matTrans);
		
		Vector3D vUp = new Vector3D(0d,1d,0d);
		//Vector3D vTarget = camera.add(lookDir);
		Vector3D vTarget = new Vector3D(0d,0d,1d);
		Matrix matCameraRot = Matrix.Matrix_MakeRotationY(fYaw);
		matCameraRot = matCameraRot.multiply(Matrix.Matrix_MakeRotationX(fXaw));
		lookDir = Vector3D.multiplyMatrix(vTarget, matCameraRot);
		//lookDir = new Vector3D(256.0f, 179.99997f, 0.9875988f);
		vTarget = Vector3D.add(camera, lookDir);
		
		Matrix matCamera = Matrix.Matrix_PointAt(camera, vTarget, vUp);
		Matrix matView = Matrix.Matrix_QuickInverse(matCamera);

		List<Triangle>trianglesToRaster = new ArrayList<Triangle>();
		this.tris = this.plane.getTriangles();
		for(Triangle tri:this.tris) {
			//System.out.println("printingini: "+tri.toString());
			Triangle triTransformed = new Triangle(Vector3D.multiplyMatrix(tri.points[0], matWorld),
												   Vector3D.multiplyMatrix(tri.points[1], matWorld),
												   Vector3D.multiplyMatrix(tri.points[2], matWorld));
			
			triTransformed.col = tri.col;
			
			Vector3D l1 = Vector3D.sub(triTransformed.points[1], triTransformed.points[0]);
			Vector3D l2 = Vector3D.sub(triTransformed.points[2], triTransformed.points[0]);

			Vector3D normal = Vector3D.crossProduct(l1, l2);
			normal = Vector3D.normal(normal);
			
			Vector3D cameraRay = Vector3D.sub(triTransformed.points[0], camera);

			//if(Vector3D.dotProduct(normal, cameraRay) < 0f) {
			if(true) {
				Triangle triViewed = new Triangle(Vector3D.multiplyMatrix(triTransformed.points[0], matView),
						  Vector3D.multiplyMatrix(triTransformed.points[1], matView),
						  Vector3D.multiplyMatrix(triTransformed.points[2], matView));
				triViewed.col = triTransformed.col;
				
				
				int nClippedTriangles = 0;
				Triangle clipped[] = new Triangle[] {new Triangle(),new Triangle()};
				nClippedTriangles = Triangle.Triangle_ClipAgainstPlane(new Vector3D(0.0d, 0.0d, 0.1d), 
																	   new Vector3D(0.0d, 0.0d, 1.0d), 
																	   triViewed, 
																	   clipped);

				for (int n = 0; n < nClippedTriangles; n++){
					
					Triangle triProjected = new Triangle(Vector3D.multiplyMatrix(clipped[n].points[0], projectionMatrix),
														 Vector3D.multiplyMatrix(clipped[n].points[1], projectionMatrix),
														 Vector3D.multiplyMatrix(clipped[n].points[2], projectionMatrix));
					triProjected.col = clipped[n].col;
					
					triProjected.points[0] = Vector3D.div(triProjected.points[0], triProjected.points[0].w);
					triProjected.points[1] = Vector3D.div(triProjected.points[1], triProjected.points[1].w);
					triProjected.points[2] = Vector3D.div(triProjected.points[2], triProjected.points[2].w);
					
					triProjected.points[0].x *= -1.0d;
					triProjected.points[1].x *= -1.0d;
					triProjected.points[2].x *= -1.0d;
					triProjected.points[0].y *= -1.0d;
					triProjected.points[1].y *= -1.0d;
					triProjected.points[2].y *= -1.0d;
					
					Vector3D offsetView = new Vector3D(1d,1d,0d);
					triProjected.points[0] = Vector3D.add(triProjected.points[0], offsetView);
					triProjected.points[1] = Vector3D.add(triProjected.points[1], offsetView);
					triProjected.points[2] = Vector3D.add(triProjected.points[2], offsetView);
					
					triProjected.points[0].x *= 0.5d * (double)this.getWidth();
					triProjected.points[0].y *= 0.5d * (double)this.getHeight();
					triProjected.points[1].x *= 0.5d * (double)this.getWidth();
					triProjected.points[1].y *= 0.5d * (double)this.getHeight();
					triProjected.points[2].x *= 0.5d * (double)this.getWidth();
					triProjected.points[2].y *= 0.5d * (double)this.getHeight();
					
					
					
					trianglesToRaster.add(triProjected);
					/*System.out.println("printingf: "+tri.toString());
					g2.setColor(Color.black);
					g2.fillPolygon(new int[] {(int) triProjected.points[0].x,(int)triProjected.points[1].x,(int)triProjected.points[2].x}, new int[] {(int)triProjected.points[0].y,(int)triProjected.points[1].y,(int)triProjected.points[2].y}, 3);
					
					g2.setColor(Color.green);
					g2.drawPolygon(new int[] {(int) triProjected.points[0].x,(int)triProjected.points[1].x,(int)triProjected.points[2].x}, new int[] {(int)triProjected.points[0].y,(int)triProjected.points[1].y,(int)triProjected.points[2].y}, 3);
					 */
				}
			}

		}
		Collections.sort(trianglesToRaster, new Comparator<Triangle>(){
			@Override
			public int compare(Triangle t1, Triangle t2) {
				double z1 = (t1.points[0].z + t1.points[1].z + t1.points[2].z) / 3.0d;
				double z2 = (t2.points[0].z + t2.points[1].z + t2.points[2].z) / 3.0d;
				return z1<z2?1:z1>z2?-1:0;
			}
		});
		for(Triangle t:trianglesToRaster) {
			g2.setColor(t.col==null ? new Color(0,0,0,100) : t.col);
			g2.fillPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);
			
			g2.setColor(new Color(0,255,0,100));
			g2.drawPolygon(new int[] {(int) t.points[0].x,(int)t.points[1].x,(int)t.points[2].x}, new int[] {(int)t.points[0].y,(int)t.points[1].y,(int)t.points[2].y}, 3);
			
		}
	}
	public static void main(String args[]) {
		RandomSingleton.setSeed(130L);
		Chromosome<Chromosome.Codon> c = new Chromosome<>(500, Chromosome.Codon::new);
		StandardGrammar grammar = new StandardGrammar();
		grammar.parseBNF("resources/grammar/default.bnf");
		LinkedList<Symbol>ss = (LinkedList<Symbol>) grammar.mapChromosome(c);
		StringBuilder sb = new StringBuilder();
		for(Symbol symb:ss)sb.append(symb.getName());
		
		System.out.println(sb.toString());

		
		
		//String test = "plane(40,0,45,10);cuad(upperRightCorner,+(upperRightCorner,N(0,0,5)),+(upperRightCorner,N(5,-3,7)),endBase);";
		String test = "plane(40,0,20,10);tri(upperRightCorner,+(upperRightCorner,N(5,-2,7)),endBase);tri(upperRightCorner,+(upperRightCorner,N(0,0,5)),+(upperRightCorner,N(5,-2,7)));"+
						"tri(upperLeftCorner,+(upperLeftCorner,N(5,-2,-7)),endBase);tri(upperLeftCorner,+(upperLeftCorner,N(0,0,-5)),+(upperLeftCorner,N(5,-2,-7)));";
		int op = 0;
		BasicPlane plane = null;
		
		if(op==0) {
			plane = BasicPlaneShape.parsePlaneAndShapes(test);
			plane.consolidate();
			plane.getTriangles().stream().forEach(s -> System.out.println(s));
			
		}
		else {
			plane = BasicPlaneShape.parsePlaneAndShapes(sb.toString());
			plane.consolidate();
		}
		
		
		System.out.println(plane);
		
		/*plane.addShape(new BasicPlaneShape(plane.upperRightCorner,
				   plane.endBase,
				   plane.upperLeftCorner)
				   );*/
		/*plane.addShape(new BasicPlaneShape(plane.upperRightCorner,
										   plane.endBase,
										   Vector3D.add(plane.upperRightCorner, Vector3D.of(0f, 0f, 5f))
										   ));
		plane.addShape(new BasicPlaneShape(plane.upperLeftCorner,
				   						   plane.endBase,
				   						   Vector3D.add(plane.upperLeftCorner, Vector3D.of(0f, 0f, -5f))
				   ));*/
				   
		/*plane.addShape(new BasicPlaneShape(plane.upperRightCorner,
										   Vector3D.add(plane.upperRightCorner, Vector3D.of(0f, 2f, 5f)),
										   plane.endBase
				   ));
		plane.addShape(new BasicPlaneShape(plane.upperRightCorner,
				   Vector3D.add(plane.upperRightCorner, Vector3D.of(0f, -2f, 5f)),
				   plane.endBase
				));
		
		plane.addShape(new BasicPlaneShape(plane.upperLeftCorner,
				   Vector3D.add(plane.upperLeftCorner, Vector3D.of(0f, 2f, -5f)),
				   plane.endBase
				));
		plane.addShape(new BasicPlaneShape(plane.upperLeftCorner,
				Vector3D.add(plane.upperLeftCorner, Vector3D.of(0f, -2f, -5f)),
				plane.endBase
				));*/
		
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
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			engine.step(0.1d);
			panel.repaint();
		}
	}
}
