package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import util.Triangle;
import util.Vector3D;

public class BasicPlane extends AbstractPlane{
	public Vector3D startBase, middleBase, endBase;
	public Vector3D upperRightCorner, upperMiddleRight;
	public Vector3D upperLeftCorner, upperMiddleLeft;
	
	List<Triangle>tris;
	List<PlaneShape>shapes;
	
	private BasicPlane() {
		this.tris = new ArrayList<>();
		this.shapes = new ArrayList<>();
	}
	public static BasicPlane construct(double baseLength, double backUpperCornerShift, double bodyAngle ,double bodyHeight) {
		BasicPlane plane = new BasicPlane();
		
		plane.startBase = Vector3D.of(0d, 0d, 0d);
		plane.endBase = Vector3D.of(plane.startBase.x + baseLength, plane.startBase.y, plane.startBase.z);
		plane.middleBase = Vector3D.of((plane.endBase.x - plane.startBase.x)/2f, plane.startBase.y, plane.startBase.z);
		
		plane.upperRightCorner = Vector3D.of(plane.startBase.x - backUpperCornerShift, 
											 plane.startBase.y + bodyHeight, 
											 plane.startBase.z + bodyAngle);
		plane.upperMiddleRight = Vector3D.mul(Vector3D.add(plane.endBase, plane.upperRightCorner), 0.5d);
		
		plane.upperLeftCorner = Vector3D.of(plane.startBase.x - backUpperCornerShift, 
											plane.startBase.y + bodyHeight, 
											plane.startBase.z - bodyAngle);
		plane.upperMiddleLeft = Vector3D.mul(Vector3D.add(plane.endBase, plane.upperLeftCorner), 0.5d);
		
		
		
		PlaneShape base1 = new BasicPlaneShape((Vector3D) plane.startBase.clone(), (Vector3D)plane.upperRightCorner.clone(), (Vector3D)plane.endBase.clone());
		PlaneShape base2 = new BasicPlaneShape((Vector3D) plane.startBase.clone(), (Vector3D)plane.upperLeftCorner.clone(), (Vector3D)plane.endBase.clone());
		plane.shapes.add(base1);
		plane.shapes.add(base2);
		plane.tris.addAll(base1.getTriangles());
		plane.tris.addAll(base2.getTriangles());
		return plane;
	}
	
	private Vector3D max(Function<Vector3D, Double>f) {
		Triangle t = this.tris.stream().max((Triangle t1, Triangle t2)->{double m1=Math.max(f.apply(t1.points[0]), Math.max(f.apply(t1.points[1]), f.apply(t1.points[2])));
																		 double m2=Math.max(f.apply(t2.points[0]), Math.max(f.apply(t2.points[1]), f.apply(t2.points[2])));
																		 return Double.compare(m1,m2);}
											).get();
		return Arrays.stream(t.points).max((Vector3D v1, Vector3D v2)->{return Double.compare(f.apply(v1), f.apply(v2));}).get();
	}
	private Vector3D min(Function<Vector3D, Double>f) {
		Triangle t = this.tris.stream().min((Triangle t1, Triangle t2)->{double m1=Math.min(f.apply(t1.points[0]), Math.min(f.apply(t1.points[1]), f.apply(t1.points[2])));
																		 double m2=Math.min(f.apply(t2.points[0]), Math.min(f.apply(t2.points[1]), f.apply(t2.points[2])));
																		 return Double.compare(m1,m2);}
											).get();
		return Arrays.stream(t.points).min((Vector3D v1, Vector3D v2)->{return Double.compare(f.apply(v1), f.apply(v2));}).get();
	}
	public Vector3D maxX() {
		return max((Vector3D v)->v.x);
	}
	public Vector3D maxY() {
		return max((Vector3D v)->v.y);
	}
	public Vector3D maxZ() {
		return max((Vector3D v)->v.z);
	}
	public Vector3D minX() {
		return min((Vector3D v)->v.x);
	}
	public Vector3D minY() {
		return min((Vector3D v)->v.y);
	}
	public Vector3D minZ() {
		return min((Vector3D v)->v.z);
	}
	public void addShape(PlaneShape shape) {
		shapes.add(shape);
		tris.addAll(shape.getTriangles());
	}
	public void addShapes(Collection<BasicPlaneShape> shapes) {
		for(PlaneShape shape:shapes) {
			this.addShape(shape);
		}
	}
	@Override
	public List<Triangle>getTriangles(){
		return tris;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("startBase ").append(startBase).append("\n");
		sb.append("middleBase ").append(middleBase).append("\n");
		sb.append("endBase ").append(endBase).append("\n");
		sb.append("upperRightCorner ").append(upperRightCorner).append("\n");
		sb.append("upperMiddleRight ").append(upperMiddleRight).append("\n");
		sb.append("upperLeftCorner ").append(upperLeftCorner).append("\n");
		sb.append("upperMiddleLeft ").append(upperMiddleLeft).append("\n");
		
		return sb.toString();
	}
	public static void main(String args[]) {
		BasicPlane plane = BasicPlane.construct(50d, 10d, 10d, 15d);
		System.out.println(plane);
		System.out.println(plane.minX());
	}
	
}
