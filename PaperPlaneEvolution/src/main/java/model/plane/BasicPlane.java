package model.plane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import model.plane.BasicPlane.PointMap.TriPoint;
import model.plane.shapes.BasicPlaneShape;
import util.Triangle;
import util.Vector3D;

public class BasicPlane extends AbstractPlane{
	public Vector3D startBase, middleBase, endBase;
	public Vector3D upperRightCorner, upperMiddleRight;
	public Vector3D upperLeftCorner, upperMiddleLeft;
	
	
	List<Triangle>tris;
	List<BasicPlaneShape>shapes;
	
	PointMap neighMap;
	
	private BasicPlane() {
		this.tris = new ArrayList<>();
		this.shapes = new ArrayList<>();
	}
	public static BasicPlane construct(double baseLength, double backUpperCornerShift, double bodyAngle, double bodyHeight) {
		BasicPlane plane = new BasicPlane();
		
		plane.startBase = Vector3D.of(0d, 0d, 0d);
		plane.endBase = Vector3D.of(plane.startBase.x + baseLength, plane.startBase.y, plane.startBase.z);
		plane.middleBase = Vector3D.of((plane.endBase.x - plane.startBase.x)/2f, plane.startBase.y, plane.startBase.z);
		
		double radAngle = bodyAngle*Math.PI/180d;
		double cosAngle = Math.cos(radAngle);
		double sinAngle = Math.sin(radAngle);
		double opp = sinAngle*bodyHeight/cosAngle;
		
		plane.upperRightCorner = Vector3D.of(plane.startBase.x - backUpperCornerShift, 
											 plane.startBase.y + bodyHeight, 
											 plane.startBase.z + opp);
		plane.upperMiddleRight = Vector3D.mul(Vector3D.add(plane.endBase, plane.upperRightCorner), 0.5d);
		
		plane.upperLeftCorner = Vector3D.of(plane.startBase.x - backUpperCornerShift, 
											plane.startBase.y + bodyHeight, 
											plane.startBase.z - opp);
		plane.upperMiddleLeft = Vector3D.mul(Vector3D.add(plane.endBase, plane.upperLeftCorner), 0.5d);
		
		
		
		BasicPlaneShape base1 = new BasicPlaneShape((Vector3D) plane.startBase.clone(), (Vector3D)plane.upperRightCorner.clone(), (Vector3D)plane.endBase.clone());
		BasicPlaneShape base2 = new BasicPlaneShape((Vector3D) plane.startBase.clone(), (Vector3D)plane.upperLeftCorner.clone(), (Vector3D)plane.endBase.clone());
		/*plane.shapes.add(base1);
		plane.shapes.add(base2);
		plane.tris.addAll(base1.getTriangles());
		plane.tris.addAll(base2.getTriangles());*/
		plane.addShape(base1);
		plane.addShape(base2);
		return plane;
	}
	
	private Vector3D max(Function<Vector3D, Double>f) {
		Triangle t = this.tris.stream().max((Triangle t1, Triangle t2)->{double m1=Math.max(f.apply(t1.points[0]), Math.max(f.apply(t1.points[1]), f.apply(t1.points[2])));
																		 double m2=Math.max(f.apply(t2.points[0]), Math.max(f.apply(t2.points[1]), f.apply(t2.points[2])));
																		 return Double.compare(m1,m2);}
											).get();
		return Arrays.stream(t.points).max((Vector3D v1, Vector3D v2)->{return Double.compare(f.apply(v1), f.apply(v2));}).get();
	}
	private Vector3D min(Function<Vector3D, Double>f) {
		Triangle t = this.tris.stream().min((Triangle t1, Triangle t2)->{double m1=Math.min(f.apply(t1.points[0]), Math.min(f.apply(t1.points[1]), f.apply(t1.points[2])));
																		 double m2=Math.min(f.apply(t2.points[0]), Math.min(f.apply(t2.points[1]), f.apply(t2.points[2])));
																		 return Double.compare(m1,m2);}
											).get();
		return Arrays.stream(t.points).min((Vector3D v1, Vector3D v2)->{return Double.compare(f.apply(v1), f.apply(v2));}).get();
	}
	public Vector3D maxX() {
		return max((Vector3D v)->v.x);
	}
	public Vector3D maxY() {
		return max((Vector3D v)->v.y);
	}
	public Vector3D maxZ() {
		return max((Vector3D v)->v.z);
	}
	public Vector3D minX() {
		return min((Vector3D v)->v.x);
	}
	public Vector3D minY() {
		return min((Vector3D v)->v.y);
	}
	public Vector3D minZ() {
		return min((Vector3D v)->v.z);
	}
	public void addShape(BasicPlaneShape shape) {
		shape.sortVertices();
		shapes.add(shape);
		tris.addAll(shape.getTriangles());
	}
	public void addShapes(Collection<BasicPlaneShape> shapes) {
		for(BasicPlaneShape shape:shapes) {
			this.addShape(shape);
		}
	}
	@Override
	public List<Triangle>getTriangles(){
		return tris;
	}
	public void consolidate() {
		neighMap = PointMap.init(this.tris);
		System.out.println(neighMap);
	}
	public static class PointMap extends HashMap<Vector3D, List<TriPoint>>{
		public static class TriPoint{
			public Triangle tri;
			public int idx;
			public static TriPoint of(Triangle tri, int idx) {
				TriPoint p = new TriPoint();
				p.tri=tri;
				p.idx=idx;
				return p;
			}
		}
		public static PointMap init(List<Triangle>tris) {
			
			PointMap map = new PointMap();
			for(Triangle t1:tris) {
				for(Triangle t2:tris) {
					HashMap<Vector3D, ArrayList<TriPoint>> pos = null;
					if(t1!=t2 && !((pos=isTriangleNeighbor(t1, t2)).isEmpty())) {
						for(Vector3D k:pos.keySet()) {
							map.computeIfAbsent(k, (v)->new ArrayList<>()).addAll(pos.get(k));
						}
					}
					
					
				}
				
			}
			
//			Triangle test = tris.get(tris.size()-1);
//			test.col = new Color(255,0,0,255);
//			for(Triangle t1:tris) {
//				if(t1!=test) {
//					for(int i=0;i<3;i++) {
//						for(int j=0;j<3;j++) {
//							if(map.containsKey(test.points[i])) {
//								if(map.get(test.points[i]).contains(t1.points[j])) {
//									t1.col=test.col;
//									break;
//								}
//							}
//							
//						}
//					}
//				}
//			}
//			test.col = new Color(0,0,0,255);
			
			return map;
		}
		private static HashMap<Vector3D, ArrayList<TriPoint>> isTriangleNeighbor(Triangle triangle1, Triangle triangle2) {
			HashMap<Vector3D, ArrayList<TriPoint>> pos = new HashMap<Vector3D, ArrayList<TriPoint>>();
			for (int i = 0; i < 3; i++) {
				Vector3D p1 = triangle1.points[i];
	            for (int j = 0; j < 3; j++) {
					Vector3D p21 = triangle2.points[j];
					Vector3D p22 = triangle2.points[(j+1)% 3];
	                double x = 0;
	                double y = 0, z = 0;
	                double a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
	                
	                x = p1.x;
	                y = p1.y;
	                z = p1.z;
	                 
	                
	                a = p21.x;
                    b = p21.y;
                    c = p21.z;
                    d = p22.x;
                    e = p22.y;
                    f = p22.z;
	               
	                if (x == a && y == b && z == c) {
	                	//pos.computeIfAbsent(p1, (v)-> new ArrayList<>()).add(p21);
	                	pos.computeIfAbsent(p1, (v)-> new ArrayList<>()).add(TriPoint.of(triangle2, j));
	                }
	                else if ((x - a) * (x - d) + (y - b) * (y - e) + (z - c) * (z - f) == 0d) {
	                	//pos.computeIfAbsent(p1, (v)-> new ArrayList<>()).add(p21);
	                	//pos.computeIfAbsent(p1, (v)->new ArrayList<>()).add(p22);
	                	pos.computeIfAbsent(p1, (v)-> new ArrayList<>()).add(TriPoint.of(triangle2, j));
	                	pos.computeIfAbsent(p1, (v)->new ArrayList<>()).add(TriPoint.of(triangle2, (j+1)%3));
	                }
	            }
	        }
	        return pos;
	    }
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
//			for(Triangle t:this.keySet()) {
//				StringJoiner sj = new StringJoiner("\n\t","["+t.toString()+"]-->\n\t","\n");
//				//for(Triangle t2:this.get(t))sj.add(t2.toString());
//				sb.append(sj.toString());
//			}
			
			return sb.toString();
		}                 
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("startBase ").append(startBase).append("\n");
		sb.append("middleBase ").append(middleBase).append("\n");
		sb.append("endBase ").append(endBase).append("\n");
		sb.append("upperRightCorner ").append(upperRightCorner).append("\n");
		sb.append("upperMiddleRight ").append(upperMiddleRight).append("\n");
		sb.append("upperLeftCorner ").append(upperLeftCorner).append("\n");
		sb.append("upperMiddleLeft ").append(upperMiddleLeft).append("\n");
		
		return sb.toString();
	}
	public List<BasicPlaneShape> getShapes() {
		return shapes;
	}
	public PointMap getNeighMap() {
		return neighMap;
	}
	public static void main(String args[]) {
		BasicPlane plane = BasicPlane.construct(50d, 10d, 10d, 15d);
		System.out.println(plane);
		System.out.println(plane.minX());
	}
	
}
