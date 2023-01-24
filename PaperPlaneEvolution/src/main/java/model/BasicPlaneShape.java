package model;

import java.util.List;

import util.Triangle;
import util.Vector3D;

public class BasicPlaneShape extends PlaneShape{
	Vector3D points[];
	public BasicPlaneShape(Vector3D ...points) {
		this.points = points;
	}
	@Override
	public List<Triangle> getTriangles() {
		return List.of(Triangle.of(points[0], points[1], points[2]));
	}

}
