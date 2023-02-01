import javax.swing.JFrame;

import util.Vector3D;


/**
 * 
 * @author Fabrizio Ortega
 *
 */
public class Main  {
	public static void main(String args[]) {
		System.out.println(getAngle(Vector3D.of(0, 0, 0),
											Vector3D.of(0, 0, 5),
											Vector3D.of(40, 0, 0),
								Vector3D.of(5, 10, 1),
								Vector3D.of(-1, 0-1, 0)));
	}

	public static double getAngle(Vector3D p0, Vector3D p1, Vector3D p2, Vector3D origin, Vector3D vector) {

	    Vector3D v0 = Vector3D.sub(p1, p0);
		Vector3D v1 = Vector3D.sub(p2, p1);
	    Vector3D normal = Vector3D.crossProduct(v0, v1);
	    normal = Vector3D.normal(normal);
	    
	    Vector3D R = Vector3D.normal(vector);

	  
	    System.out.println(normal);
	    System.out.println(R);
	    return Vector3D.dotProduct(normal, R);
	}
}
