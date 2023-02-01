package util;

public class Vector3D implements Cloneable{
	public double x,y,z,w=1d;
	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3D(Vector3D copy) {
		x=copy.x;
		y=copy.y;
		z=copy.z;
	}
	public static Vector3D of(double x, double y, double z) {
		return new Vector3D(x,y,z);
	}
	public void reset() {
		x=0d;
		y=0d;
		z=0d;
	}
	@Override
	public Object clone() {
		return new Vector3D(x,y,z);
	}
	
	public static Vector3D Vector_IntersectPlane(Vector3D plane_p, Vector3D plane_n, Vector3D lineStart, Vector3D lineEnd)
	{
		plane_n = Vector3D.normal(plane_n);
		double plane_d = -Vector3D.dotProduct(plane_n, plane_p);
		double ad = Vector3D.dotProduct(lineStart, plane_n);
		double bd = Vector3D.dotProduct(lineEnd, plane_n);
		double t = (-plane_d - ad) / (bd - ad);
		Vector3D lineStartToEnd = Vector3D.sub(lineEnd, lineStart);
		Vector3D lineToIntersect = Vector3D.mul(lineStartToEnd, t);
		return Vector3D.add(lineStart, lineToIntersect);
	}
	public static Vector3D crossProduct(Vector3D l, Vector3D r) {
		return new Vector3D(l.y*r.z-l.z*r.y, l.z*r.x-l.x*r.z, l.x*r.y-l.y*r.x);
	}
	public static double dotProduct(Vector3D l, Vector3D r) {
		return l.x*r.x + l.y*r.y + l.z*r.z;
	}
	public static double length(Vector3D l) {
		return Math.sqrt(Vector3D.dotProduct(l, l));
	}
	public static Vector3D normal(Vector3D l) {
		double aux = Vector3D.length(l);
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
	public static Vector3D div(Vector3D l, double r) {
		return new Vector3D(l.x/r,l.y/r,l.z/r);
	}
	public static Vector3D mul(Vector3D l, double r) {
		return new Vector3D(l.x*r,l.y*r,l.z*r);
	}
	public static Vector3D sub(Vector3D l, double r) {
		return new Vector3D(l.x-r,l.y-r,l.z-r);
	}
	public static Vector3D add(Vector3D l, double r) {
		return new Vector3D(l.x+r,l.y+r,l.z+r);
	}
	public void add(Vector3D l) {
		this.x+=l.x;
		this.y+=l.y;
		this.z+=l.z;
	}
	public void add(double l) {
		this.x+=l;
		this.y+=l;
		this.z+=l;
	}
	public void div(double l) {
		this.x/=l;
		this.y/=l;
		this.z/=l;
	}
	public void mul(double l) {
		this.x*=l;
		this.y*=l;
		this.z*=l;
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
