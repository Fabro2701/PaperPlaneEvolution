import javax.swing.JFrame;

import util.Vector3D;


/**
 * 
 * @author Fabrizio Ortega
 *
 */
public class Main  {
	public static void main(String args[]) {
		System.out.println(intersectionArea(Vector3D.of(0, 1, 0),
									Vector3D.of(5, 5, 5),
									Vector3D.of(1, 0, 0),
								Vector3D.of(2, 1, 0.5)));
	}
	public static boolean intersectionArea(Vector3D p0, Vector3D p1, Vector3D p2, Vector3D vector) {

	    Vector3D v0 = Vector3D.sub(p1, p0);
		Vector3D v1 = Vector3D.sub(p2, p1);
	    Vector3D v2 = Vector3D.sub(p0, p2);
	    Vector3D normal = Vector3D.crossProduct(v0, v1);
	    normal = Vector3D.normal(normal);
	    
	    Vector3D O = Vector3D.of(0, 0, 0);
	    Vector3D R = Vector3D.normal(vector);

	    double D = -Vector3D.dotProduct(normal, p1);
	    double t = -(D + Vector3D.dotProduct(normal, O))/(Vector3D.dotProduct(normal, R));
	    
	    //double t = -(normal.x * vector.x + normal.y * vector.y + normal.z * vector.z + d) / Vector3D.dotProduct(normal, vector);

	    Vector3D phit = Vector3D.add(O, Vector3D.mul(R, t));
	    if (t < 0d) {
	      return false;
	    }

	    Vector3D c0 = Vector3D.sub(phit, p0);
	    Vector3D c1 = Vector3D.sub(phit, p1);
	    Vector3D c2 = Vector3D.sub(phit, p2);
	    
	    boolean b = false;
	    if(Vector3D.dotProduct(normal, Vector3D.crossProduct(v0, c0))>0d &&
	       Vector3D.dotProduct(normal, Vector3D.crossProduct(v1, c1))>0d &&
	       Vector3D.dotProduct(normal, Vector3D.crossProduct(v2, c2))>0d)b =  true;

	    
	    
	    return b;
//	    double dot00 = Vector3D.dotProduct(v0,v0);
//	    double dot01 = Vector3D.dotProduct(v0,v1);
//	    double dot02 = Vector3D.dotProduct(v0,v2);
//	    double dot11 = Vector3D.dotProduct(v1,v1);
//	    double dot12 = Vector3D.dotProduct(v1,v2);
//	    double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
//	    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
//	    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
//
//	    if ((u >= 0) && (v >= 0) && (u + v <= 1)) {
//	      return Vector3D.length(v0) * Vector3D.length(normal) * invDenom * 0.5d;
//	    } else {
//	      return 0;
//	    }
	  }
}
