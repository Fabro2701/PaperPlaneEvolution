package model.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import model.plane.BasicPlane;
import model.plane.BasicPlane.TriangleMap;
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
		
		//double sumMass = tris.stream().mapToDouble((Triangle t)->{return t.mass;}).sum();
		
		//System.out.println(sumMass);
		for(Triangle tri:tris) {
			tri.force[0] = Vector3D.of(0d, 0d, 0d);
			tri.force[1] = Vector3D.of(0d, 0d, 0d);
			tri.force[2] = Vector3D.of(0d, 0d, 0d);
		}
		for(Triangle tri:tris) {
			
			
			TriangleMap neighbors = plane.getNeighMap();
			//System.out.println(neighbors.size());
			for(int i=0;i<3;i++) {
				tri.force[i].y += -GRAVITY*tri.mass;
			}
			//tri.velocity.add(Vector3D.mul(tri.force, dt));
			
			//Vector3D change = Vector3D.add(Vector3D.add(tri.force, dt), dt);
			spreadToNeighbors(tri, neighbors.get(tri));
			
			//dragNeighbors(tri, neighbors, change);
			for(int i=0; i<3; i++) {
				tri.points[i].add(Vector3D.add(Vector3D.add(tri.force[i], dt), dt));
            }
		}
	}
	
	private void spreadToNeighbors(Triangle tri, HashMap<Triangle, List<Integer>> neigh) {
		for(Triangle t:neigh.keySet()) {
			List<Integer>pos = neigh.get(t);
			for(Integer idx:pos) {
				t.force[idx].add(-GRAVITY*tri.mass*0.05);
			}
		}
	}
	private void dragNeighbors(Triangle tri, List<Triangle> neighbors, Vector3D change) {
		
	}
}
