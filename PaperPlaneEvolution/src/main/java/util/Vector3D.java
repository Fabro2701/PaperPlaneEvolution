package util;

public class Vector3D {
	public float x,y,z,w=1f;
	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3D(Vector3D copy) {
		x=copy.x;
		y=copy.y;
		z=copy.z;
	}
	public static Vector3D of(float x, float y, float z) {
		return new Vector3D(x,y,z);
	}
	
	public static Vector3D Vector_IntersectPlane(Vector3D plane_p, Vector3D plane_n, Vector3D lineStart, Vector3D lineEnd)
	{
		plane_n = Vector3D.normal(plane_n);
		float plane_d = -Vector3D.dotProduct(plane_n, plane_p);
		float ad = Vector3D.dotProduct(lineStart, plane_n);
		float bd = Vector3D.dotProduct(lineEnd, plane_n);
		float t = (-plane_d - ad) / (bd - ad);
		Vector3D lineStartToEnd = Vector3D.sub(lineEnd, lineStart);
		Vector3D lineToIntersect = Vector3D.mul(lineStartToEnd, t);
		return Vector3D.add(lineStart, lineToIntersect);
	}
	public static Vector3D crossProduct(Vector3D l, Vector3D r) {
		return new Vector3D(l.y*r.z-l.z*r.y, l.z*r.x-l.x*r.z, l.x*r.y-l.y*r.x);
	}
	public static float dotProduct(Vector3D l, Vector3D r) {
		return l.x*r.x + l.y*r.y + l.z*r.z;
	}
	public static float length(Vector3D l) {
		return (float) Math.sqrt(Vector3D.dotProduct(l, l));
	}
	public static Vector3D normal(Vector3D l) {
		float aux = Vector3D.length(l);
		return new Vector3D(l.x/aux,l.y/aux,l.z/aux);
	}
	public static Vector3D add(Vector3D l, Vector3D r) {
		return new Vector3D(l.x+r.x,l.y+r.y,l.z+r.z);
	}
	public static Vector3D sub(Vector3D l, Vector3D r) {
		return new Vector3D(l.x-r.x,l.y-r.y,l.z-r.z);
	}
	public static Vector3D mul(Vector3D l, Vector3D r) {
		return new Vector3D(l.x*r.x,l.y*r.y,l.z*r.z);
	}
	public static Vector3D div(Vector3D l, float r) {
		return new Vector3D(l.x/r,l.y/r,l.z/r);
	}
	public static Vector3D mul(Vector3D l, float r) {
		return new Vector3D(l.x*r,l.y*r,l.z*r);
	}
	public static Vector3D sub(Vector3D l, float r) {
		return new Vector3D(l.x-r,l.y-r,l.z-r);
	}
	public static Vector3D add(Vector3D l, float r) {
		return new Vector3D(l.x+r,l.y+r,l.z+r);
	}
	public static Vector3D multiplyMatrix(Vector3D l, Matrix m) {
		Vector3D v = new Vector3D(0f,0f,0f);

		v.x = l.x * m.m[0][0] + l.y * m.m[1][0] + l.z * m.m[2][0] + l.w * m.m[3][0];
		v.y = l.x * m.m[0][1] + l.y * m.m[1][1] + l.z * m.m[2][1] + l.w * m.m[3][1];
		v.z = l.x * m.m[0][2] + l.y * m.m[1][2] + l.z * m.m[2][2] + l.w * m.m[3][2];
		v.w = l.x * m.m[0][3] + l.y * m.m[1][3] + l.z * m.m[2][3] + l.w * m.m[3][3];
		return v;
	}
	@Override
	public String toString() {
		return "("+x+", "+y+", "+z+")";
	}
}
