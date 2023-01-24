package model.engine;

import java.util.List;
import java.util.stream.Collectors;

import model.BasicPlane;
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
		
		double sumMass = tris.stream().mapToDouble((Triangle t)->{return t.mass;}).sum();
		
		//System.out.println(sumMass);
		for(Triangle tri:tris) {
			tri.velocity.y -= GRAVITY*sumMass*dt;
			
			for(int i=0; i<3; i++) {
                tri.points[i].x += tri.velocity.x * dt;
                tri.points[i].y += tri.velocity.y * dt;
                tri.points[i].z += tri.velocity.z * dt;
            }
		}
	}
}
