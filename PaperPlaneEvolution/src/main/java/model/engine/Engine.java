package model.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import model.plane.BasicPlane;
import model.plane.BasicPlane.PointMap;
import util.Triangle;
import util.Vector3D;

public class Engine {
	BasicPlane plane;
	final double GRAVITY = 0.001d;
	
	public Engine(BasicPlane plane) {
		this.plane = plane;
	}
	public void step(double dt) {
		List<Triangle> tris = this.plane.getTriangles();
		
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
			//System.out.println(neighbors.size());
			for(int i=0;i<3;i++) {
				tri.force[i].y += -GRAVITY*tri.mass;
			}
		}
		for(Triangle tri:tris) {
			//System.out.println(neighbors.size());
			for(int i=0;i<3;i++) {
				tri.force[i].y += -GRAVITY*tri.mass;
			}
		}
		System.out.println("before");
		for(Triangle tri:tris) {
			for(int i=0;i<3;i++) {
				System.out.print(tri.force[i]+"  ");
			}
			System.out.println("");
		}
			
		for(Triangle tri:tris) {
			for(int i=0;i<3;i++) {
				if(!neighbors.containsKey(tri.points[i]))continue;
				spreadToNeighbors(tri.points[i], tri.force[i], neighbors.get(tri.points[i]));
			}
		}
		for(Triangle tri:tris) {
			
			for(int i=0; i<3; i++) {
				tri.force[i].add(tri.tmpforce[i]);
	        }
		}
		for(Triangle tri:tris) {
			for(int i=0; i<3; i++) {
				tri.points[i].add(Vector3D.add(Vector3D.add(tri.force[i], dt), dt));
	        }
		}
		
		System.out.println("after");
		for(Triangle tri:tris) {
			for(int i=0;i<3;i++) {
				System.out.print(tri.force[i]+"  ");
			}
			System.out.println("");
		}
	}
	
	private void spreadToNeighbors(Vector3D point, Vector3D force, List<Vector3D> list) {
		for(Vector3D neigh:list) {
			neigh.add(force);
		}
				//t.force[idx].add(-GRAVITY*tri.mass*0.05);
			
	}
	private void dragNeighbors(Triangle tri, List<Triangle> neighbors, Vector3D change) {
		
	}
}
