package util;

import java.awt.Color;



public class Triangle {
	public Vector3D points[];
	public Color col = Color.gray;
	
	public double mass;
	public Vector3D velocity = Vector3D.of(0d, 0d, 0d);
	public Vector3D force[] = new Vector3D[] {Vector3D.of(0d, 0d, 0d),Vector3D.of(0d, 0d, 0d),Vector3D.of(0d, 0d, 0d)};
	
	public Triangle(Vector3D p1, Vector3D p2, Vector3D p3) {
		points = new Vector3D[]{p1,p2,p3};
		
	    double area = Vector3D.length(Vector3D.crossProduct(Vector3D.sub(p2, p1), Vector3D.sub(p3, p1)))/2d;
	    mass = area*0.1d;
	}
	public static Triangle of(Vector3D p1, Vector3D p2, Vector3D p3) {
		return new Triangle(p1,p2,p3);
	}
	public Triangle() {
		points = new Vector3D[3];
	}
	public Triangle(Triangle copy) {
		points = new Vector3D[3];
		points[0] = new Vector3D(copy.points[0]);
		points[1] = new Vector3D(copy.points[1]);
		points[2] = new Vector3D(copy.points[2]);
	}
	public static double dist(Vector3D pt, Vector3D plane_n, Vector3D plane_p) {
		//Vector3D p = Vector3D.normal(pt);
		Vector3D p = pt;
		double e1 = (plane_n.x * p.x) + (plane_n.y * p.y) + (plane_n.z * p.z);
		double e2 = Vector3D.dotProduct(plane_n, plane_p);
		return e1 - e2;
		//return (plane_n.x * p.x + plane_n.y * p.y + plane_n.z * p.z - Vector3D.dotProduct(plane_n, plane_p));

	}
	public static int Triangle_ClipAgainstPlane(Vector3D plane_p, Vector3D plane_n, Triangle in_tri, Triangle out_tris[])
	{
		// Make sure plane normal is indeed normal
		plane_n = Vector3D.normal(plane_n);

		// Return signed shortest distance from point to plane, plane normal must be normalised
		
		
		// Create two temporary storage arrays to classify points either side of plane
		// If distance sign is positive, point lies on "inside" of plane
		Vector3D inside_points[] = new Vector3D[3];  int nInsidePointCount = 0;
		Vector3D outside_points[] = new Vector3D[3];; int nOutsidePointCount = 0;

		// Get signed distance of each point in triangle to plane
		double d0 = dist(in_tri.points[0],plane_n,plane_p);
		double d1 = dist(in_tri.points[1],plane_n,plane_p);
		double d2 = dist(in_tri.points[2],plane_n,plane_p);

		if (d0 >= 0) { inside_points[nInsidePointCount++] = in_tri.points[0]; }
		else { outside_points[nOutsidePointCount++] = in_tri.points[0]; }
		if (d1 >= 0) { inside_points[nInsidePointCount++] = in_tri.points[1]; }
		else { outside_points[nOutsidePointCount++] = in_tri.points[1]; }
		if (d2 >= 0) { inside_points[nInsidePointCount++] = in_tri.points[2]; }
		else { outside_points[nOutsidePointCount++] = in_tri.points[2]; }

		// Now classify triangle points, and break the input triangle into 
		// smaller output triangles if required. There are four possible
		// outcomes...

		if (nInsidePointCount == 0)
		{
			// All points lie on the outside of plane, so clip whole triangle
			// It ceases to exist

			return 0; // No returned triangles are valid
		}

		if (nInsidePointCount == 3)
		{
			// All points lie on the inside of plane, so do nothing
			// and allow the triangle to simply pass through
			out_tris[0] = in_tri;

			return 1; // Just the one returned original triangle is valid
		}

		if (nInsidePointCount == 1 && nOutsidePointCount == 2)
		{
			// Triangle should be clipped. As two points lie outside
			// the plane, the triangle simply becomes a smaller triangle

			// Copy appearance info to new triangle
			out_tris[0].col =  in_tri.col;
			//out_tris[0].col =  Color.BLUE;
			//out_tri1.sym = in_tri.sym;

			// The inside point is valid, so keep that...
			out_tris[0].points[0] = inside_points[0];

			// but the two new points are at the locations where the 
			// original sides of the triangle (lines) intersect with the plane
			out_tris[0].points[1] = Vector3D.Vector_IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0]);
			out_tris[0].points[2] = Vector3D.Vector_IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[1]);

			return 1; // Return the newly formed single triangle
		}

		if (nInsidePointCount == 2 && nOutsidePointCount == 1)
		{
			// Triangle should be clipped. As two points lie inside the plane,
			// the clipped triangle becomes a "quad". Fortunately, we can
			// represent a quad with two new triangles

			// Copy appearance info to new triangles
			out_tris[0].col =  in_tri.col;
			//out_tris[0].col =  Color.GREEN;
			//out_tri1.sym = in_tri.sym;

			out_tris[1].col =  in_tri.col;
			//out_tris[1].col =  Color.RED;
			//out_tri2.sym = in_tri.sym;

			// The first triangle consists of the two inside points and a new
			// point determined by the location where one side of the triangle
			// intersects with the plane
			out_tris[0].points[0] = inside_points[0];
			out_tris[0].points[1] = inside_points[1];
			out_tris[0].points[2] = Vector3D.Vector_IntersectPlane(plane_p, plane_n, inside_points[0], outside_points[0]);

			// The second triangle is composed of one of he inside points, a
			// new point determined by the intersection of the other side of the 
			// triangle and the plane, and the newly created point above
			out_tris[1].points[0] = inside_points[1];
			out_tris[1].points[1] = out_tris[0].points[2];
			out_tris[1].points[2] = Vector3D.Vector_IntersectPlane(plane_p, plane_n, inside_points[1], outside_points[0]);

			return 2; // Return two newly formed triangles which form a quad
		}
		
		return 0;
	}
	@Override
	public String toString() {
		return points[0]+" , "+points[1]+" , "+points[2];
	}
}
