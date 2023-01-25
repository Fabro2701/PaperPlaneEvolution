package model.plane.shapes;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	public static BasicPlane parsePlaneAndShapes(String description){
				
		ShapeParser parser = new ShapeParser();
		JSONObject r = parser.parse(description);
		
		JSONArray planeD = r.getJSONArray("plane");
		BasicPlane plane = BasicPlane.construct(planeD.getJSONObject(0).getDouble("value"),
												planeD.getJSONObject(1).getDouble("value"),
												planeD.getJSONObject(2).getDouble("value"),
												planeD.getJSONObject(3).getDouble("value"));
		
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
			plane.addShape(new BasicPlaneShape(vs));
		}
		return plane;
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
				return (Vector3D)((Vector3D)f.get(plane)).clone();
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		case "GeneralPoint":
			try {
				Method m = BasicPlane.class.getDeclaredMethod(query.getString("value"), null);
				return (Vector3D)((Vector3D)m.invoke(plane, null)).clone();
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static void main(String args[]) {
		
		BasicPlane plane = parsePlaneAndShapes("plane(40,5,2,5);tri(upperMiddleLeft,+(upperMiddleLeft,middleBase),middleBase);");
		System.out.println(plane);
		for(BasicPlaneShape s:plane.getShapes())System.out.println(s);
		
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
    public void sortVertices() {
    	 // Encuentra el vértice con la coordenada z más baja (en caso de empate, el vértice con la coordenada y más baja, y en caso de empate, el vértice con la coordenada x más a la izquierda)
        Vector3D reference = points[0];
        for (Vector3D vertex : points) {
            if (vertex.z < reference.z || (vertex.z == reference.z && vertex.y < reference.y) || (vertex.z == reference.z && vertex.y == reference.y && vertex.x < reference.x)) {
                reference = vertex;
            }
        }
        // Ordena los vértices de acuerdo al ángulo con el punto de referencia
        Set<Vector3D> visited = new HashSet<>();
        List<Vector3D> result = new ArrayList<>();
        Vector3D current = reference;
        result.add(current);
        visited.add(current);
        while (true) {
        	Vector3D next = null;
            for (Vector3D vertex : points) {
                if (visited.contains(vertex)) continue;
                if (next == null || isCounterClockwise(current, next, vertex)) {
                    next = vertex;
                }
            }
            if (next == reference) {
                break;
            }
            result.add(next);
            visited.add(next);
            current = next;
            if(result.size()==points.length)break;
        }
        
        for(int i=0;i<points.length;i++)points[i]=result.get(i);
	}
    private boolean isCounterClockwise(Vector3D a, Vector3D b, Vector3D c) {
        double crossProduct = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        return crossProduct > 0;
    }
}
