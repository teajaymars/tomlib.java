package gfx;

import app.Maths;

public class Cam3D {

	public static final float TURNSPEED=(float)(Math.PI/50);
	public float x, y, z;
	public float xAngle, yAngle;
	
	public float[][] toTranslationMatrix() {
		return new float[][] {
				{ 1, 0, 0, -x },
				{ 0, 1, 0, -y },
				{ 0, 0, 1, -z },
				{ 0, 0, 0,  1 },
		};
	}

	
	public float[][] toRotationMatrix() {
		final float sin_x=(float)Math.sin(xAngle);
		final float sin_y=(float)Math.sin(yAngle);
		final float cos_x=(float)Math.cos(xAngle);
		final float cos_y=(float)Math.cos(yAngle);
		
		return new float[][] {
				{  cos_y, 		0, 		-sin_y, 		0 },
				{ -sin_x*sin_y, cos_x, 	-sin_x*cos_y, 	0 },
				{  cos_x*sin_y, sin_x, 	 cos_x*cos_y,	0 },
				{  0, 			0, 		 0, 			1 },
		};
	}
	
	
	
	public int[] toScreen(int xx, int yy, int zz) {
		return toScreen(new float[][] { {xx},{yy},{zz},{1}});
	}
	public int[] toScreen(float[][] t) {
		// Step 1: Translate
		t=Maths.matrixMultiply(toTranslationMatrix(), t);
		// Step 2: Rotate
		t=Maths.matrixMultiply(toRotationMatrix(), t);
		// Step 3: Project
		
		final int ORIGIN_X=160, ORIGIN_Y=120;
		final int DEPTH_SCALE=10;
		int xx=ORIGIN_X+(int)(DEPTH_SCALE*t[0][0]/t[2][0]);
		int yy=ORIGIN_Y-(int)(DEPTH_SCALE*t[1][0]/t[2][0]);
		
		return new int[] { xx,yy };
	}
	
}
