package model.engine;

import java.util.List;

import model.plane.BasicPlane;
import model.plane.BasicPlane.PointMap;
import model.plane.BasicPlane.PointMap.TriPoint;
import util.Triangle;
import util.Vector3D;

public class Engine {
	BasicPlane plane;
	final double GRAVITY = 0.01d;
	
	public Engine(BasicPlane plane) {
		this.plane = plane;
	}
	public void step(double dt) {
		List<Triangle> tris = this.plane.getTriangles();
		Vector3D origin = Vector3D.of(2, 5, -5);
		Vector3D wind = Vector3D.mul(Vector3D.of(0, 0.5, 0.5), 20d);
		
		//double sumMass = tris.stream().mapToDouble((Triangle t)->{return t.mass;}).sum();
		PointMap neighbors = plane.getNeighMap();
		//System.out.println(sumMass);
		for(Triangle tri:tris) {
			for(int i=0;i<3;i++) {
				tri.force[i].reset();
				tri.tmpforce[i].reset();
			}
		}
		
		for(Triangle tri:tris) {
			Vector3D phit = intersectionTriangle(tri.points[0],tri.points[1],tri.points[2],
												 origin,wind);
			if(phit!=null) {
				double d[] = new double[3];
				double sum = 0d;
				for(int i=0;i<3;i++) {
					d[i] = Math.abs(Vector3D.length(Vector3D.sub(phit, tri.points[i])));
					sum += d[i];
					
				}
				for(int i=0;i<3;i++) {
					d[i] = 1d - (d[i]/sum);
					tri.force[i].add(Vector3D.mul(wind, d[i]*1d));
				}
			}
			
		}
		for(Triangle tri:tris) {
			//System.out.println(neighbors.size());
			for(int i=0;i<3;i++) {
				//tri.force[i].y += -GRAVITY*tri.mass;
			}
		}
//		System.out.println("before");
//		for(Triangle tri:tris) {
//			for(int i=0;i<3;i++) {
//				System.out.print(tri.force[i]+"  ");
//			}
//			System.out.println("");
//		}
		for(int d=0;d<2;d++) {
			for(Triangle tri:tris) {
				for(int i=0; i<3; i++) {

					tri.tmpforce[i].div(Math.pow(3, d));
					
				}
			}
			for(Triangle tri:tris) {
				for(int i=0;i<3;i++) {
					if(!neighbors.containsKey(tri.points[i]))continue;
					spreadToNeighbors(tri, i, neighbors.get(tri.points[i]));
				}
			}
			for(Triangle tri:tris) {
				for(int i=0; i<3; i++) {
					tri.force[i].add(tri.tmpforce[i]);
					tri.tmpforce[i].reset();
		        }
			}
		}
		
		for(Triangle tri:tris) {
			for(int i=0; i<3; i++) {
				
				tri.velocity[i].add(Vector3D.mul(tri.force[i], dt/tri.mass));
				tri.points[i].add(Vector3D.mul(tri.velocity[i], dt));
	        }
		}
		
//		System.out.println("after");
//		for(Triangle tri:tris) {
//			for(int i=0;i<3;i++) {
//				System.out.print(tri.force[i]+"  ");
//			}
//			System.out.println("");
//		}
	}
	
	private void spreadToNeighbors(Triangle tri, int idx, List<TriPoint> list) {
		for(TriPoint neigh:list) {
			double mm = neigh.tri.mass/(tri.mass+neigh.tri.mass);
			tri.force[idx].mul(1.0-mm);
			neigh.tri.tmpforce[neigh.idx].add(tri.force[idx]);
			
		}
				//t.force[idx].add(-GRAVITY*tri.mass*0.05);
			
	}

	public static Vector3D intersectionTriangle(Vector3D p0, Vector3D p1, Vector3D p2, Vector3D origin, Vector3D vector) {

	    Vector3D v0 = Vector3D.sub(p1, p0);
		Vector3D v1 = Vector3D.sub(p2, p1);
	    Vector3D v2 = Vector3D.sub(p0, p2);
	    Vector3D normal = Vector3D.crossProduct(v0, v1);
	    normal = Vector3D.normal(normal);
	    
	    Vector3D O = origin;
	    Vector3D R = Vector3D.normal(vector);

	    double D = -Vector3D.dotProduct(normal, p1);
	    double t = -(D + Vector3D.dotProduct(normal, O))/(Vector3D.dotProduct(normal, R));
	    
	    Vector3D phit = Vector3D.add(O, Vector3D.mul(R, t));
	    if (Math.abs(t) < 0.0001d) {
	      return null;
	    }

	    Vector3D c0 = Vector3D.sub(phit, p0);
	    Vector3D c1 = Vector3D.sub(phit, p1);
	    Vector3D c2 = Vector3D.sub(phit, p2);
	    
	    
	    if(!(Vector3D.dotProduct(normal, Vector3D.crossProduct(v0, c0))>0d &&
	       Vector3D.dotProduct(normal, Vector3D.crossProduct(v1, c1))>0d &&
	       Vector3D.dotProduct(normal, Vector3D.crossProduct(v2, c2))>0d))return null;

	    return phit;

	  }
}
