package model;

import java.util.ArrayList;
import java.util.List;

import util.Triangle;
import util.Vector3D;

public class BasicPlane extends AbstractPlane{
	Vector3D startBase, middleBase, endBase;
	Vector3D upperRightCorner, upperMiddleRight;
	Vector3D upperLeftCorner, upperMiddleLeft;
	
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

		return plane;
	}
	@Override
	public List<Triangle>getTriangles(){
		List<Triangle>tris = new ArrayList<Triangle>();
		
		tris.add(Triangle.of(startBase, endBase, upperRightCorner));
		tris.add(Triangle.of(startBase, endBase, upperLeftCorner));
		
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
	}
	
}
