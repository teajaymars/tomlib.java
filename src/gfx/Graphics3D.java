package gfx;

import app.Maths;

public class Graphics3D {

	public static final float PERSPECTIVE=600;
	
	public static int 	ORIGIN_X, 
						ORIGIN_Y;
	
	public static void set_origin(int x, int y) {
		ORIGIN_X=x;
		ORIGIN_Y=y;
	}
	
	public static void point(Cam3D cam, int x, int y, int z, int col) {
		// I need these values as a column matrix
		float[][] f=new float[][] {  {x}, {y}, {z}, {1} };
		f=Maths.matrixMultiply(cam.toRotationMatrix(), Maths.matrixMultiply(cam.toTranslationMatrix(), f));
		// Extract camera-relative x, y, z values
		float fx=f[0][0]; 
		float fy=f[1][0]; 
		float fz=f[2][0];
		if (fz<=0) return;  // its behind the camera
		x=ORIGIN_X + (int)(PERSPECTIVE*fx/fz);
		y=ORIGIN_Y- (int)(PERSPECTIVE*fy/fz);
		Graphics.point(x,y,col);
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

}
