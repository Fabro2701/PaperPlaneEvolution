import javax.swing.JFrame;

import util.Vector3D;


/**
 * 
 * @author Fabrizio Ortega
 *
 */
public class Main  {
	public static void main(String args[]) {
		System.out.println(intersectionArea(Vector3D.of(0, 0, 0),
											Vector3D.of(0, 10, 3.6),
											Vector3D.of(40, 0, 0),
								Vector3D.of(1, 0, 0.1),
								Vector3D.of(0, 1, 0)));
	}
	public static boolean intersectionArea(Vector3D p0, Vector3D p1, Vector3D p2, Vector3D origin, Vector3D vector) {

	    Vector3D v0 = Vector3D.sub(p1, p0);
		Vector3D v1 = Vector3D.sub(p2, p1);
	    Vector3D v2 = Vector3D.sub(p0, p2);
	    Vector3D normal = Vector3D.crossProduct(v0, v1);
	    normal = Vector3D.normal(normal);
	    
	    Vector3D O = origin;
	    Vector3D R = Vector3D.normal(vector);

	    double D = -Vector3D.dotProduct(normal, p1);
	    double t = -(D + Vector3D.dotProduct(normal, O))/(Vector3D.dotProduct(normal, R));
	    
	    Vector3D phit = Vector3D.add(O, Vector3D.mul(R, t));
	    if (Math.abs(t) < 0.0001d) {
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

	  }
}
