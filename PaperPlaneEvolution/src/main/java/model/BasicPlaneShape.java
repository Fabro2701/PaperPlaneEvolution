package model;

import java.util.ArrayList;
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
		if(points.length==3)return List.of(Triangle.of(points[0], points[1], points[2]));
		else return triangulate(List.of(points));
	}
	public static List<Triangle> triangulate(List<Vector3D> originalPolygon) {
        List<Triangle> triangles = new ArrayList<>();
        List<Vector3D> polygon = new ArrayList<>(originalPolygon);
        Vector3D current = polygon.get(0);
        while (polygon.size() > 3) {
        	Vector3D previous = polygon.get((polygon.indexOf(current) + polygon.size() - 1) % polygon.size());
        	Vector3D next = polygon.get((polygon.indexOf(current) + 1) % polygon.size());

            if (isEar(previous, current, next, polygon)) {
                triangles.add(new Triangle(previous, current, next));
                polygon.remove(current);
            }
            current = next;
        }
        triangles.add(new Triangle(polygon.get(0), polygon.get(1), polygon.get(2)));
        return triangles;
    }

    private static boolean isEar(Vector3D a, Vector3D b, Vector3D c, List<Vector3D> polygon) {
        // Check if the triangle is convex
    	Vector3D ab = Vector3D.sub(b, a);
    	Vector3D ac = Vector3D.sub(c, a);
    	Vector3D normal = Vector3D.crossProduct(ab, ac);
        if (Vector3D.dotProduct(normal, normal) < 0.000001f) return false; // degenerate triangle

        // Check if any other vertex is inside the triangle
        for (Vector3D v : polygon) {
            if (v == a || v == b || v == c) continue;
            if (isPointInTriangle(v, a, b, c)) return false;
        }
        return true;
    }
    private static boolean isPointInTriangle(Vector3D p, Vector3D a, Vector3D b, Vector3D c) {
        // Compute vectors
        Vector3D v0 = Vector3D.sub(c, a);
        Vector3D v1 = Vector3D.sub(b, a);
        Vector3D v2 = Vector3D.sub(p, a);

        // Compute dot products
        double dot00 = Vector3D.dotProduct(v0, v0);
        double dot01 = Vector3D.dotProduct(v0, v1);
        double dot02 = Vector3D.dotProduct(v0, v2);
        double dot11 = Vector3D.dotProduct(v1, v1);
        double dot12 = Vector3D.dotProduct(v1, v2);

        // Compute barycentric coordinates
        double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // Check if point is in triangle
        return (u >= 0) && (v >= 0) && (u + v < 1);
    }
}
