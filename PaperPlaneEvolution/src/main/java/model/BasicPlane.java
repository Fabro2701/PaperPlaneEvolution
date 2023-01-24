package model;

import java.util.ArrayList;
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
	public static BasicPlane construct(float baseLength, float backUpperCornerShift, float bodyAngle ,float bodyHeight) {
		BasicPlane plane = new BasicPlane();
		
		plane.startBase = Vector3D.of(0f, 0f, 0f);
		plane.endBase = Vector3D.of(plane.startBase.x + baseLength, plane.startBase.y, plane.startBase.z);
		plane.middleBase = Vector3D.of((plane.endBase.x - plane.startBase.x)/2f, plane.startBase.y, plane.startBase.z);
		
		plane.upperRightCorner = Vector3D.of(plane.startBase.x - backUpperCornerShift, 
											 plane.startBase.y + bodyHeight, 
											 plane.startBase.z + bodyAngle);
		plane.upperMiddleRight = Vector3D.mul(Vector3D.add(plane.endBase, plane.upperRightCorner), 0.5f);
		
		plane.upperLeftCorner = Vector3D.of(plane.startBase.x - backUpperCornerShift, 
											plane.startBase.y + bodyHeight, 
											plane.startBase.z - bodyAngle);
		plane.upperMiddleLeft = Vector3D.mul(Vector3D.add(plane.endBase, plane.upperLeftCorner), 0.5f);
		
		
		
		PlaneShape base1 = new BasicPlaneShape(plane.startBase, plane.upperRightCorner, plane.endBase);
		PlaneShape base2 = new BasicPlaneShape(plane.startBase, plane.upperLeftCorner, plane.endBase);
		plane.shapes.add(base1);
		plane.shapes.add(base2);
		plane.tris.addAll(base1.getTriangles());
		plane.tris.addAll(base2.getTriangles());
		return plane;
	}
	
	private float max(Function<Vector3D, Float>f) {
		Triangle t = this.tris.stream().max((Triangle t1, Triangle t2)->{float m1=Math.max(f.apply(t1.points[0]), Math.max(f.apply(t1.points[1]), f.apply(t1.points[2])));
																		 float m2=Math.max(f.apply(t2.points[0]), Math.max(f.apply(t2.points[1]), f.apply(t2.points[2])));
																		 return Float.compare(m1,m2);}
											).get();
		return Math.max(f.apply(t.points[0]), Math.max(f.apply(t.points[1]), f.apply(t.points[2])));
	}
	private float min(Function<Vector3D, Float>f) {
		Triangle t = this.tris.stream().min((Triangle t1, Triangle t2)->{float m1=Math.min(f.apply(t1.points[0]), Math.min(f.apply(t1.points[1]), f.apply(t1.points[2])));
																		 float m2=Math.min(f.apply(t2.points[0]), Math.min(f.apply(t2.points[1]), f.apply(t2.points[2])));
																		 return Float.compare(m1,m2);}
											).get();
		return Math.min(f.apply(t.points[0]), Math.min(f.apply(t.points[1]), f.apply(t.points[2])));
	}
	public float maxX() {
		return max((Vector3D v)->v.x);
	}
	public float maxY() {
		return max((Vector3D v)->v.y);
	}
	public float maxZ() {
		return max((Vector3D v)->v.z);
	}
	public float minX() {
		return min((Vector3D v)->v.x);
	}
	public float minY() {
		return min((Vector3D v)->v.y);
	}
	public float minZ() {
		return min((Vector3D v)->v.z);
	}
	public void addShape(PlaneShape shape) {
		shapes.add(shape);
		tris.addAll(shape.getTriangles());
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
		BasicPlane plane = BasicPlane.construct(50f, 10f, 10f, 15f);
		System.out.println(plane);
		System.out.println(plane.maxX());
	}
	
}
