package main;

import gfx.Graphics;

public class Polygon2D {

	public int[] vertex,face;
	
	public Polygon2D(int num_vertices, int num_faces) {
		// Each vertex consists of 2 points
		vertex=new int[num_vertices*2];
		// Each face consists of 3 points and a colour
		face=new int[num_faces*4];
	}
	public void set_vertex(int point_id, int x, int y) {
		int off=point_id*2;
		vertex[off++]=x;
		vertex[off++]=y;
	}
	public void set_face(int face_id, int v1, int v2, int v3, int col) {
		int off=face_id*4;
		face[off++]=v1;
		face[off++]=v2;
		face[off++]=v3;
		face[off++]=col;
	}
	public void render_points(int col) {
		for (int i=0;i<vertex.length;) {
			final int x=vertex[i++];
			final int y=vertex[i++];
			Graphics.hline(x-5,y,10,col);
			Graphics.vline(x,y-5,10,col);
		}
	}
	public void render() {
		for (int i=0;i<face.length;) {
			final int p1=face[i++]<<1;
			final int p2=face[i++]<<1;
			final int p3=face[i++]<<1;
			final int col=face[i++];
			final int x1=vertex[p1];
			final int y1=vertex[p1+1];
			final int x2=vertex[p2];
			final int y2=vertex[p2+1];
			final int x3=vertex[p3];
			final int y3=vertex[p3+1];
			Graphics.tri(x1, y1, x2, y2, x3, y3, col);
		}
	}
	
}
