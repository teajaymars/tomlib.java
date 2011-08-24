package main;

import java.util.Arrays;

import app.Maths;
import gfx.Cam3D;
import gfx.Graphics3D;

public class Polygon3D {

	public int[] vertex,face;
	// Post-computation arrays
	private int[] vertex_transformed;
	private int[] face_sorted;
	
	public Polygon3D(int num_vertices, int num_faces) {
		// Each vertex requires 3 points;
		vertex=new int[num_vertices*3];
		// Each face requires 3 vertices and a colour
		face=new int[num_faces*4];
		
		// These internal arrays are used for correct rendering
		vertex_transformed=new int[vertex.length];
		face_sorted=new int[face.length];
	}
	
	public void set_vertex(int point_id, int x, int y, int z) {
		int off=point_id*3;
		vertex[off++]=x;
		vertex[off++]=y;
		vertex[off++]=z;
	}
	public void set_face(int face_id, int v1, int v2, int v3, int col) {
		int off=face_id*4;
		face[off++]=v1;
		face[off++]=v2;
		face[off++]=v3;
		face[off++]=col;
	}
	
	public void translate(int xo, int yo, int zo) {
		for (int i=0;i<vertex.length;i+=3) {
			vertex[i  ]+=xo;
			vertex[i+1]+=yo;
			vertex[i+2]+=zo;
		}
	}
	public void render_markers(Cam3D camera, int col, int size) {
		for (int i=0;i<vertex.length;i+=3) {
			Graphics3D.marker(camera, vertex[i], vertex[i+1], vertex[i+2], col, size);
		}
	}
	public void render_edges(Cam3D camera, int col) {
		for (int i=0;i<face.length;i+=4) {
			final int p1=face[i  ]*3, x1=vertex[p1], y1=vertex[p1+1], z1=vertex[p1+2];
			final int p2=face[i+1]*3, x2=vertex[p2], y2=vertex[p2+1], z2=vertex[p2+2];
			final int p3=face[i+2]*3, x3=vertex[p3], y3=vertex[p3+1], z3=vertex[p3+2];
			Graphics3D.line(camera, x1, y1, z1, x2, y2, z2, col);
			Graphics3D.line(camera, x1, y1, z1, x3, y3, z3, col);
			Graphics3D.line(camera, x3, y3, z3, x2, y2, z2, col);
		}
	}
	public void render(Cam3D camera) {
		float[][] temp=new float[4][1]; // Column vector 
		temp[3][0]=1;
		float[][] camera_matrix=Maths.matrixMultiply(camera.toRotationMatrix(), camera.toTranslationMatrix());
		// Transform all vertices into camera-space
		for (int i=0;i<vertex.length;i+=3) {
			temp[0][0]=vertex[i];
			temp[1][0]=vertex[i+1];
			temp[2][0]=vertex[i+2];
			float[][] camspace=Maths.matrixMultiply(camera_matrix, temp);
			vertex_transformed[i]=(int)camspace[0][0];
			vertex_transformed[i]=(int)camspace[1][0];
			vertex_transformed[i]=(int)camspace[2][0];
		}
		// Figure out the distance to each face
		for (int i=0;i<face.length;i+=4) {
			// Point IDs
			face_sorted[i]=face[i];
			face_sorted[i+1]=face[i+1];
			face_sorted[i+2]=face[i+2];
			// Distance heuristic is sum of Z values
			final int z1=(vertex_transformed[face[i]*3+2]);
			final int z2=(vertex_transformed[face[i+1]*3+2]);
			final int z3=(vertex_transformed[face[i+2]*3+2]);
			face_sorted[i+3]=z1+z2+z3;
		}
		// Depthsort the faces
		// TODO
			
	}
}
