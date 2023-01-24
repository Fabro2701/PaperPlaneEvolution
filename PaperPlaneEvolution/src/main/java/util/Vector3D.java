package util;


public class Vector3D {
	public float x,y,z;
	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public static Vector3D of(float x, float y, float z) {
		return new Vector3D(x,y,z);
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
	@Override
	public String toString() {
		return "("+x+", "+y+", "+z+")";
	}
}
