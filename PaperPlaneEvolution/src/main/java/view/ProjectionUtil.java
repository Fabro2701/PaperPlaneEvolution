package view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import util.Matrix;
import util.Triangle;
import util.Vector3D;

public class ProjectionUtil {
	Matrix projectionMatrix;
	int width=1000,height=900;
	double zfar=10000d,znear=0.1d;
	double a=(double)width/(double)height,f=90d,q=zfar/(zfar-znear);
	double frad = (double) (1d/ (double)Math.tan(f*0.5d/180d*3.14159d));
	double offset = 28d;
	double time = 0.0d;
	Vector3D camera;
	Vector3D lookDir;
	double fYaw,fXaw;
	
	public ProjectionUtil() {
		this.projectionMatrix = Matrix.Matrix_MakeProjection(f, a, znear, zfar);
		camera = new Vector3D(0d,0d,0d);
		lookDir = new Vector3D(0d,0d,1d);
	}
	public List<Triangle> triangles(List<Triangle>tris) {
		Matrix zrotation = Matrix.Matrix_MakeRotationZ(time);
		Matrix xrotation = Matrix.Matrix_MakeRotationX(time);
		
		Matrix matTrans = Matrix.translate(0d, 0d, offset);
		
		Matrix matWorld = Matrix.identity();
		matWorld = zrotation.multiply(xrotation);
		matWorld = matWorld.multiply(matTrans);
		
		Vector3D vUp = new Vector3D(0d,1d,0d);
		//Vector3D vTarget = camera.add(lookDir);
		Vector3D vTarget = new Vector3D(0d,0d,1d);
		Matrix matCameraRot = Matrix.Matrix_MakeRotationY(fYaw);
		matCameraRot = matCameraRot.multiply(Matrix.Matrix_MakeRotationX(fXaw));
		lookDir = Vector3D.multiplyMatrix(vTarget, matCameraRot);
		//lookDir = new Vector3D(256.0f, 179.99997f, 0.9875988f);
		vTarget = Vector3D.add(camera, lookDir);
		
		Matrix matCamera = Matrix.Matrix_PointAt(camera, vTarget, vUp);
		Matrix matView = Matrix.Matrix_QuickInverse(matCamera);
		
		List<Triangle>trianglesToRaster = new ArrayList<Triangle>();
		for(Triangle tri:tris) {
			//System.out.println("printingini: "+tri.toString());
			Triangle triTransformed = new Triangle(Vector3D.multiplyMatrix(tri.points[0], matWorld),
												   Vector3D.multiplyMatrix(tri.points[1], matWorld),
												   Vector3D.multiplyMatrix(tri.points[2], matWorld));
			
			triTransformed.col = tri.col;
			
			Vector3D l1 = Vector3D.sub(triTransformed.points[1], triTransformed.points[0]);
			Vector3D l2 = Vector3D.sub(triTransformed.points[2], triTransformed.points[0]);

			Vector3D normal = Vector3D.crossProduct(l1, l2);
			normal = Vector3D.normal(normal);
			
			Vector3D cameraRay = Vector3D.sub(triTransformed.points[0], camera);

			//if(Vector3D.dotProduct(normal, cameraRay) < 0f) {
			if(true) {
				Triangle triViewed = new Triangle(Vector3D.multiplyMatrix(triTransformed.points[0], matView),
						  Vector3D.multiplyMatrix(triTransformed.points[1], matView),
						  Vector3D.multiplyMatrix(triTransformed.points[2], matView));
				triViewed.col = triTransformed.col;
				
				
				int nClippedTriangles = 0;
				Triangle clipped[] = new Triangle[] {new Triangle(),new Triangle()};
				nClippedTriangles = Triangle.Triangle_ClipAgainstPlane(new Vector3D(0.0d, 0.0d, 0.1d), 
																	   new Vector3D(0.0d, 0.0d, 1.0d), 
																	   triViewed, 
																	   clipped);

				for (int n = 0; n < nClippedTriangles; n++){
					
					Triangle triProjected = new Triangle(Vector3D.multiplyMatrix(clipped[n].points[0], projectionMatrix),
														 Vector3D.multiplyMatrix(clipped[n].points[1], projectionMatrix),
														 Vector3D.multiplyMatrix(clipped[n].points[2], projectionMatrix));
					triProjected.col = clipped[n].col;
					
					triProjected.points[0] = Vector3D.div(triProjected.points[0], triProjected.points[0].w);
					triProjected.points[1] = Vector3D.div(triProjected.points[1], triProjected.points[1].w);
					triProjected.points[2] = Vector3D.div(triProjected.points[2], triProjected.points[2].w);
					
					triProjected.points[0].x *= -1.0d;
					triProjected.points[1].x *= -1.0d;
					triProjected.points[2].x *= -1.0d;
					triProjected.points[0].y *= -1.0d;
					triProjected.points[1].y *= -1.0d;
					triProjected.points[2].y *= -1.0d;
					
					Vector3D offsetView = new Vector3D(1d,1d,0d);
					triProjected.points[0] = Vector3D.add(triProjected.points[0], offsetView);
					triProjected.points[1] = Vector3D.add(triProjected.points[1], offsetView);
					triProjected.points[2] = Vector3D.add(triProjected.points[2], offsetView);
					
					triProjected.points[0].x *= 0.5d * (double)this.width;
					triProjected.points[0].y *= 0.5d * (double)this.height;
					triProjected.points[1].x *= 0.5d * (double)this.width;
					triProjected.points[1].y *= 0.5d * (double)this.height;
					triProjected.points[2].x *= 0.5d * (double)this.width;
					triProjected.points[2].y *= 0.5d * (double)this.height;
					
					trianglesToRaster.add(triProjected);
				}
			}

		}
		Collections.sort(trianglesToRaster, new Comparator<Triangle>(){
			@Override
			public int compare(Triangle t1, Triangle t2) {
				double z1 = (t1.points[0].z + t1.points[1].z + t1.points[2].z) / 3.0d;
				double z2 = (t2.points[0].z + t2.points[1].z + t2.points[2].z) / 3.0d;
				return z1<z2?1:z1>z2?-1:0;
			}
		});
		return trianglesToRaster;
	}
	public List<Vector3D> vectors(List<Vector3D> vs) {
		Matrix zrotation = Matrix.Matrix_MakeRotationZ(time);
		Matrix xrotation = Matrix.Matrix_MakeRotationX(time);
		
		Matrix matTrans = Matrix.translate(0d, 0d, offset);
		
		Matrix matWorld = Matrix.identity();
		matWorld = zrotation.multiply(xrotation);
		matWorld = matWorld.multiply(matTrans);
		
		Vector3D vUp = new Vector3D(0d,1d,0d);
		//Vector3D vTarget = camera.add(lookDir);
		Vector3D vTarget = new Vector3D(0d,0d,1d);
		Matrix matCameraRot = Matrix.Matrix_MakeRotationY(fYaw);
		matCameraRot = matCameraRot.multiply(Matrix.Matrix_MakeRotationX(fXaw));
		lookDir = Vector3D.multiplyMatrix(vTarget, matCameraRot);
		//lookDir = new Vector3D(256.0f, 179.99997f, 0.9875988f);
		vTarget = Vector3D.add(camera, lookDir);
		
		Matrix matCamera = Matrix.Matrix_PointAt(camera, vTarget, vUp);
		Matrix matView = Matrix.Matrix_QuickInverse(matCamera);
		
		List<Vector3D>trianglesToRaster = new ArrayList<>();
		for(Vector3D v:vs) {
			//System.out.println("printingini: "+tri.toString());
			Vector3D triTransformed = Vector3D.multiplyMatrix(v, matWorld);
			Vector3D triViewed = Vector3D.multiplyMatrix(triTransformed, matView);
			Vector3D triProjected = Vector3D.multiplyMatrix(triViewed, projectionMatrix);
			triProjected = Vector3D.div(triProjected, triProjected.w);


			triProjected.x *= -1.0d;
			triProjected.y *= -1.0d;
			Vector3D offsetView = new Vector3D(1d,1d,0d);
			triProjected = Vector3D.add(triProjected, offsetView);
			triProjected.x *= 0.5d * this.width;
			triProjected.y *= 0.5d * this.height;
			
			trianglesToRaster.add(triProjected);

		}
		return trianglesToRaster;
	}
}
