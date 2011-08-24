package app;

public class Maths {

	/** Floating point matrix muliplication */
	public static float[][] matrixMultiply(float[][] a, float[][] b) {
		// Width of a = Height of b
		assert (a[0].length==b.length);
		float[][] c=new float[a.length][b[0].length];
		// For each row in a, and each column in b
		for (int i=0;i<a.length;i++) {
			for (int j=0;j<b[0].length;j++) {
				// Sum the row*column
				for (int k=0;k<a[i].length;k++) {
					float aa=a[i][k];
	                float bb=b[k][j];
					c[i][j]+=aa*bb;
                }
			}
		}
		return c;
	}
	
	/** A useful debug mechanism for printing out matrices */
	public static void printMatrix(float[][] m) {
		for (int i=0;i<m.length;i++) {
			System.out.print("[  ");
			for (int j=0;j<m[i].length;j++) {
				System.out.print(twoDP(m[i][j]) + "  ");
			}
			System.out.println("]");
		}
		
	}
	
	/** Represent a floating point value as a string with two decimal points */
	public static String twoDP(float f) {
		StringBuilder s=new StringBuilder();
		if (f<0) { s.append('-'); f=-f; }
		s.append(Integer.toString((int)f));
		s.append('.');
		int n=((int)(f*100))%100;
		if (n<10) s.append("0");
		s.append(n);
		return s.toString();
	}
	
	/** Represent a fixed point value as a string with two decimal points */
	public static String fromFP(int n) {
		return twoDP(((float)n)/65536);
	}
	
}
