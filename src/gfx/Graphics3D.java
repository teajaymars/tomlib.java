package gfx;

import app.Maths;

public class Graphics3D {

	public static float PERSPECTIVE=600;
	
	public static int 	ORIGIN_X, 
						ORIGIN_Y;
	
	public static void set_origin(int x, int y) {
		ORIGIN_X=x;
		ORIGIN_Y=y;
	}
	
	public static void point(Cam3D cam, int x, int y, int z, int col) {
		marker(cam,x,y,z,col,1);
	}
	
	public static void marker(Cam3D cam, int x, int y, int z, int col, int size) {
		// I need these values as a column matrix
		float[][] f=new float[][] {  {x}, {y}, {z}, {1} };
		f=Maths.matrixMultiply(cam.toRotationMatrix(), Maths.matrixMultiply(cam.toTranslationMatrix(), f));
		// Extract camera-relative x, y, z values
		float fx=f[0][0]; 
		float fy=f[1][0]; 
		float fz=f[2][0];
		if (fz<=1) return;  // its behind the camera
		x=ORIGIN_X + (int)(PERSPECTIVE*fx/fz);
		y=ORIGIN_Y- (int)(PERSPECTIVE*fy/fz);
		Graphics.marker(x,y,size,col);
	}
	
	public static void line(Cam3D cam, int x1, int y1, int z1, int x2, int y2, int z2, int col) {
		float[][] p1=new float[][] {  {x1}, {y1}, {z1}, {1} };
		float[][] p2=new float[][] {  {x2}, {y2}, {z2}, {1} };
		p1=Maths.matrixMultiply(cam.toRotationMatrix(), Maths.matrixMultiply(cam.toTranslationMatrix(), p1));
		p2=Maths.matrixMultiply(cam.toRotationMatrix(), Maths.matrixMultiply(cam.toTranslationMatrix(), p2));
		float fx1=p1[0][0]; 
		float fy1=p1[1][0]; 
		float fz1=p1[2][0];
		float fx2=p2[0][0]; 
		float fy2=p2[1][0]; 
		float fz2=p2[2][0];
		if (fz1<=1 || fz2<=1) return;  // its behind the camera
		x1=ORIGIN_X + 	(int)(PERSPECTIVE*fx1/fz1);
		y1=ORIGIN_Y- 	(int)(PERSPECTIVE*fy1/fz1);
		x2=ORIGIN_X + 	(int)(PERSPECTIVE*fx2/fz2);
		y2=ORIGIN_Y- 	(int)(PERSPECTIVE*fy2/fz2);
		Graphics.line(x1,y1,x2,y2,col);
	}
	
	public static void tri(Cam3D cam, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, int col) {
		float[][] p1=new float[][] {  {x1}, {y1}, {z1}, {1} };
		float[][] p2=new float[][] {  {x2}, {y2}, {z2}, {1} };
		float[][] p3=new float[][] {  {x3}, {y3}, {z3}, {1} };
		float[][] camRotation=cam.toRotationMatrix();
		float[][] camTranslation=cam.toTranslationMatrix();
		p1=Maths.matrixMultiply(camRotation, Maths.matrixMultiply(camTranslation, p1));
		p2=Maths.matrixMultiply(camRotation, Maths.matrixMultiply(camTranslation, p2));
		p3=Maths.matrixMultiply(camRotation, Maths.matrixMultiply(camTranslation, p3));
		
		float fx1=p1[0][0]; 
		float fy1=p1[1][0]; 
		float fz1=p1[2][0];
		float fx2=p2[0][0]; 
		float fy2=p2[1][0]; 
		float fz2=p2[2][0];
		float fx3=p3[0][0]; 
		float fy3=p3[1][0]; 
		float fz3=p3[2][0];
		if (fz1<=1 || fz2<=1 || fz3<=1) return;  // its behind the camera
		
		// Backface culling
		// Work out the cross product vector
//		final float ax=fx2-fx1, ay=fy2-fy1, az=fz2-fz1;
//		final float bx=fx3-fx1, by=fy3-fy1, bz=fz3-fz1;
//		float[] cross=new float[] {
//				ay*bz - az*by,	
//				az*bx - ax*bz,	
//				ax*by - ay*bx,	
//		};
//		final float dot=cross[0]*fx2 + cross[1]*fy2 + cross[2]*fz2;
//		if (dot<0) return; // Triangle does not face the camera
		
		x1=ORIGIN_X + 	(int)(PERSPECTIVE*fx1/fz1);
		y1=ORIGIN_Y- 	(int)(PERSPECTIVE*fy1/fz1);
		x2=ORIGIN_X + 	(int)(PERSPECTIVE*fx2/fz2);
		y2=ORIGIN_Y- 	(int)(PERSPECTIVE*fy2/fz2);
		x3=ORIGIN_X + 	(int)(PERSPECTIVE*fx3/fz3);
		y3=ORIGIN_Y- 	(int)(PERSPECTIVE*fy3/fz3);
		Graphics.tri(x1,y1,x2,y2,x3,y3,col);
	}

}
