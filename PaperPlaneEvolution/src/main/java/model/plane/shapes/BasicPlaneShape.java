package model.plane.shapes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import model.plane.BasicPlane;
import util.Triangle;
import util.Vector3D;

public class BasicPlaneShape extends PlaneShape{
	Vector3D points[];
	public BasicPlaneShape(Vector3D ...points) {
		if(points.length<3)System.err.println("Shapes need at least 3 points");
		this.points = points;
	}
	
	public static List<BasicPlaneShape>parseShapes(BasicPlane plane, String description){
		List<BasicPlaneShape> list = new ArrayList<BasicPlaneShape>();
		
		ShapeParser parser = new ShapeParser();
		JSONObject r = parser.parse(description);
		
		JSONArray shapes = r.getJSONArray("shapes");
		for(int i=0;i<shapes.length();i++) {
			JSONObject shape = shapes.getJSONObject(i);
			//PolygonShape
			JSONArray pts = shape.getJSONArray("points");
			int l = pts.length();
			Vector3D vs[] = new Vector3D[l];
			for(int j=0;j<l;j++) {
				vs[j] = findPoint(plane, pts.getJSONObject(j));
			}
			list.add(new BasicPlaneShape(vs));
		}
		
		return list;
	}
	private static Vector3D findPoint(BasicPlane plane, JSONObject query) {
		String type = query.getString("type");
		switch(type) {
		case "NaturalPoint":
			double x = query.getJSONObject("x").getDouble("value");
			double y = query.getJSONObject("y").getDouble("value");
			double z = query.getJSONObject("z").getDouble("value");
			return Vector3D.of(x, y, z);
		case "OperationPoint":
			String op = query.getString("op");
			Vector3D left = findPoint(plane, query.getJSONObject("left"));
			Vector3D right = findPoint(plane, query.getJSONObject("right"));
			if(op.equals("+"))return Vector3D.add(left, right);
			else return Vector3D.sub(left, right);
		case "BasePoint":
			try {
				Field f = BasicPlane.class.getDeclaredField(query.getString("value"));
				return (Vector3D)f.get(plane);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		case "GeneralPoint":
			try {
				Method m = BasicPlane.class.getDeclaredMethod(query.getString("value"), null);
				return (Vector3D)m.invoke(plane, null);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static void main(String args[]) {
		BasicPlane plane = BasicPlane.construct(40d, 5d, 2d, 5d);
		System.out.println(plane);
		List<BasicPlaneShape> shapes = parseShapes(plane, "plane(40,5,2,5);tri(upperMiddleLeft,+(upperMiddleLeft,middleBase),middleBase);");
		for(BasicPlaneShape s:shapes)System.out.println(s);
		
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
    @Override 
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	for(Vector3D v:this.points)sb.append(v).append(" ; ");
    	return sb.toString();
    }
    /*public static List<BasicPlaneShape>parseShapes(BasicPlane plane, String text){
	List<BasicPlaneShape> list = new ArrayList<BasicPlaneShape>();
	
	Pattern pattern = Pattern.compile("tri[^;]*;");
	Matcher matcher = pattern.matcher(text);
	while (matcher.find()) {
		String atts[] = text.substring(matcher.start()+4, matcher.end()-2).split(",");
		Vector3D vs[] = new Vector3D[3];
		for(int i=0;i<3;i++) {
			vs[i] = findPoint(plane, atts[i]);
		}
		list.add(new BasicPlaneShape(vs));			
	}
	pattern = Pattern.compile("cuad[^;]*;");
	matcher = pattern.matcher(text);
	while (matcher.find()) {
		String atts[] = text.substring(matcher.start()+5, matcher.end()-2).split(",");
		Vector3D vs[] = new Vector3D[4];
		for(int i=0;i<4;i++) {
			vs[i] = findPoint(plane, atts[i]);
		}
		list.add(new BasicPlaneShape(vs));
	}
	return list;
}
*/
}
