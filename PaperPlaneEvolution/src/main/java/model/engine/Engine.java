package model.engine;

import java.util.List;
import java.util.stream.Collectors;

import model.plane.BasicPlane;
import util.Triangle;
import util.Vector3D;

public class Engine {
	BasicPlane plane;
	final double GRAVITY = 0.1d;
	
	public Engine(BasicPlane plane) {
		this.plane = plane;
	}
	public void step(double dt) {
		List<Triangle> tris = this.plane.getTriangles();
		
		//double sumMass = tris.stream().mapToDouble((Triangle t)->{return t.mass;}).sum();
		
		//System.out.println(sumMass);
		for(Triangle tri:tris) {
			Vector3D force = Vector3D.of(0d, 0d, 0d);
			
			force.y += -GRAVITY*tri.mass;
			tri.velocity.add(Vector3D.mul(force, dt));
			
			List<Triangle>neighbors = searchNeighbors(tri, tris);
			//System.out.println(neighbors.size());
			
			Vector3D change = Vector3D.of(0f, 0f, 0f);
			for(int i=0; i<3; i++) {
				change.x = tri.velocity.x * dt;
				change.y = tri.velocity.y * dt;
				change.z = tri.velocity.z * dt;
            }
			//dragNeighbors(tri, neighbors, change);
		}
	}
	private List<Triangle> searchNeighbors(Triangle tri, List<Triangle> tris) {
		
		List<Triangle> list = tris.stream().filter((Triangle t)->t!=tri&&(equalPoints(t.points[0], tri.points[0])||
																		  equalPoints(t.points[0], tri.points[1])||
																		  equalPoints(t.points[0], tri.points[2])||
																		  equalPoints(t.points[1], tri.points[0])||
																		  equalPoints(t.points[1], tri.points[1])||
																		  equalPoints(t.points[1], tri.points[2])||
																		  equalPoints(t.points[2], tri.points[0])||
																		  equalPoints(t.points[2], tri.points[1])||
																		  equalPoints(t.points[2], tri.points[2]))
							).collect(Collectors.toList());
		return list;
	}
	private boolean equalPoints(Vector3D l, Vector3D r) {
		double ep = 0.0001d;
		return Math.abs(l.x-r.x)<ep&&Math.abs(l.y-r.y)<ep&&Math.abs(l.z-r.z)<ep;
	}
	private void dragNeighbors(Triangle tri, List<Triangle> neighbors, Vector3D change) {
		for(Triangle neigh:neighbors) {
			List<Vector3D>commonVertices = getCommonVertices(tri,neigh);
			
		}
	}
	private List<Vector3D> getCommonVertices(Triangle tri, Triangle neigh) {
		// TODO Auto-generated method stub
		return null;
	}
}
