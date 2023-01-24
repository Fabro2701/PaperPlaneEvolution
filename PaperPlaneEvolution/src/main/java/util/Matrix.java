package util;

import static util.Util.tanf;
import static util.Util.cosf;
import static util.Util.sinf;
public class Matrix {
	float m[][];
	int columns, rows;
	public Matrix(int columns, int rows) {
		this.columns=columns;
		this.rows=rows;
		m = new float[rows][columns];
	}
	public void setElement(float e, int x, int y) {
		m[y][x]=e;
	}
	public Matrix multiply(Matrix m2) {

		Matrix matrix = new Matrix(4,4);
		for (int c = 0; c < 4; c++)
			for (int r = 0; r < 4; r++)
				matrix.m[r][c] = this.m[r][0] * m2.m[0][c] + this.m[r][1] * m2.m[1][c] +this.m[r][2] * m2.m[2][c] + this.m[r][3] * m2.m[3][c];
		return matrix;
	}
	public static Matrix Matrix_MakeProjection(float fFovDegrees, float fAspectRatio, float fNear, float fFar)
	{
		float fFovRad = 1.0f / tanf(fFovDegrees * 0.5f / 180.0f * 3.14159f);
		Matrix matrix = new Matrix(4,4);
		matrix.m[0][0] = fAspectRatio * fFovRad;
		matrix.m[1][1] = fFovRad;
		matrix.m[2][2] = fFar / (fFar - fNear);
		matrix.m[3][2] = (-fFar * fNear) / (fFar - fNear);
		matrix.m[2][3] = 1.0f;
		matrix.m[3][3] = 0.0f;
		return matrix;
	}
	public static Matrix translate(float x, float y, float z) {
		Matrix matrix = new Matrix(4,4);
		matrix.m[0][0] = 1.0f;
		matrix.m[1][1] = 1.0f;
		matrix.m[2][2] = 1.0f;
		matrix.m[3][3] = 1.0f;
		matrix.m[3][0] = x;
		matrix.m[3][1] = y;
		matrix.m[3][2] = z;
		return matrix;
	}
	public static Matrix identity() {
		Matrix matrix = new Matrix(4,4);
		matrix.m[0][0] = 1.0f;
		matrix.m[1][1] = 1.0f;
		matrix.m[2][2] = 1.0f;
		matrix.m[3][3] = 1.0f;
		return matrix;
	}
	public static Matrix Matrix_MakeRotationX(float fAngleRad)
	{
		Matrix matrix = new Matrix(4,4);
		matrix.m[0][0] = 1.0f;
		matrix.m[1][1] = cosf(fAngleRad);
		matrix.m[1][2] = sinf(fAngleRad);
		matrix.m[2][1] = -sinf(fAngleRad);
		matrix.m[2][2] = cosf(fAngleRad);
		matrix.m[3][3] = 1.0f;
		return matrix;
	}

	public static Matrix Matrix_MakeRotationY(float fAngleRad)
	{
		Matrix matrix = new Matrix(4,4);
		matrix.m[0][0] = cosf(fAngleRad);
		matrix.m[0][2] = sinf(fAngleRad);
		matrix.m[2][0] = -sinf(fAngleRad);
		matrix.m[1][1] = 1.0f;
		matrix.m[2][2] = cosf(fAngleRad);
		matrix.m[3][3] = 1.0f;
		return matrix;
	}

	public static Matrix Matrix_MakeRotationZ(float fAngleRad)
	{
		Matrix matrix = new Matrix(4,4);
		matrix.m[0][0] = cosf(fAngleRad);
		matrix.m[0][1] = sinf(fAngleRad);
		matrix.m[1][0] = -sinf(fAngleRad);
		matrix.m[1][1] = cosf(fAngleRad);
		matrix.m[2][2] = 1.0f;
		matrix.m[3][3] = 1.0f;
		return matrix;
	}
	public static Matrix Matrix_PointAt(Vector3D pos, Vector3D target, Vector3D up)
	{
		// Calculate new forward direction
		Vector3D newForward = Vector3D.sub(target, pos);
		newForward = Vector3D.normal(newForward);
		
		// Calculate new Up direction
		Vector3D a = Vector3D.mul(newForward, Vector3D.dotProduct(up, newForward));
		Vector3D newUp = Vector3D.sub(up, a);
		newUp = Vector3D.normal(newUp);

		// New Right direction is easy, its just cross product
		Vector3D newRight = Vector3D.crossProduct(newUp, newForward);

		// Construct Dimensioning and Translation Matrix	
		Matrix matrix = new Matrix(4,4);
		matrix.m[0][0] = newRight.x;	matrix.m[0][1] = newRight.y;	matrix.m[0][2] = newRight.z;	matrix.m[0][3] = 0.0f;
		matrix.m[1][0] = newUp.x;		matrix.m[1][1] = newUp.y;		matrix.m[1][2] = newUp.z;		matrix.m[1][3] = 0.0f;
		matrix.m[2][0] = newForward.x;	matrix.m[2][1] = newForward.y;	matrix.m[2][2] = newForward.z;	matrix.m[2][3] = 0.0f;
		matrix.m[3][0] = pos.x;			matrix.m[3][1] = pos.y;			matrix.m[3][2] = pos.z;			matrix.m[3][3] = 1.0f;
		return matrix;

	}

	public static Matrix Matrix_QuickInverse(Matrix m) // Only for Rotation/Translation Matrices
	{
		Matrix matrix = new Matrix(4,4);
		matrix.m[0][0] = m.m[0][0]; matrix.m[0][1] = m.m[1][0]; matrix.m[0][2] = m.m[2][0]; matrix.m[0][3] = 0.0f;
		matrix.m[1][0] = m.m[0][1]; matrix.m[1][1] = m.m[1][1]; matrix.m[1][2] = m.m[2][1]; matrix.m[1][3] = 0.0f;
		matrix.m[2][0] = m.m[0][2]; matrix.m[2][1] = m.m[1][2]; matrix.m[2][2] = m.m[2][2]; matrix.m[2][3] = 0.0f;
		matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);
		matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);
		matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);
		matrix.m[3][3] = 1.0f;
		return matrix;
	}
//	public Vector3D multiply(Vector3D l) {
//		Vector3D r = new Vector3D();
//		r.x = l.x * m[0][0] + l.y * m[1][0] + l.z * m[2][0] + m[3][0];
//		r.y = l.x * m[0][1] + l.y * m[1][1] + l.z * m[2][1] + m[3][1];
//		r.z = l.x * m[0][2] + l.y * m[1][2] + l.z * m[2][2] + m[3][2];
//		float w = l.x * m[0][3] + l.y * m[1][3] + l.z * m[2][3] + m[3][3];
//
//		if (w != 0.0f)
//		{
//			r.x /= w; r.y /= w; r.z /= w;
//		}
//		return r;
//	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		for(int y=0;y<this.m.length;y++) {
			for(int x=0;x<this.m[y].length;x++) {
				s.append(m[y][x]+" ");
			}s.append("\n");
		}
		
		return s.toString();
	}
	public static void main2(String args[]) {
		
	}
}
