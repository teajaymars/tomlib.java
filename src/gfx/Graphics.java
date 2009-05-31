package gfx;


public class Graphics {
	public static int[] pixels;
	public static int width, height;

	public static void setSurface(Surface s) {
		pixels=s.getPixels();
		width=s.getWidth();
		height=s.getHeight();
	}
	
	public static void cls() {
		for (int i=0;i<pixels.length;i++) pixels[i]=0;
	}
	
	public static void point(int x, int y, int col) {
		if (x<0 || y<0 || x>=width || y>=height) return;
		pixels[x+y*width]=col;
	}
	
	public static void line(int x1, int y1, int x2, int y2, int col) {
		final int dx=Math.abs(x2-x1);
		final int dy=Math.abs(y2-y1);
		if (dx==0 && dy==0) return;
		if (dx==0) { vline(x1,y1,y2-y1,col); return; }
		if (dy==0) { hline(x1,y1,x2-x1,col); return; }
		
		final int MX=(int)((((long)y2-y1)<<16)/(x2-x1)); // Gradient (relative to y) 
		final int MY=(int)((((long)x2-x1)<<16)/(y2-y1)); // Gradient (relative to x)
		
		// Clip the entire line if possible
		if ((y1<0 && y2<0) || (x1<0 && x2<0) || (y1>=height && y2>=height) || (x1>=width && x2>=width)) return;
		// Clip against top/bottom boundaries
		if (y1>y2) 		{ 	int d=x1; x1=x2; x2=d; d=y1; y1=y2; y2=d;	}
		if (y1<0) 		{	x1-=(((long)MY*y1)>>16);	y1=0;			}
		if (y2>=height) {	x2-=(((long)MY*(y2-(height-1)))>>16);	y2=height-1;	}
		// Have we discovered that the line never appears?
		if ((y1<0 && y2<0) || (x1<0 && x2<0) || (y1>=height && y2>=height) || (x1>=width && x2>=width)) return;
		// Delta-Y might have been clipped down to nothing by now...
		if (y2==y1) { hline(x1,y1,x2-x1,col); return; }
		// Clip against the left/right boundaries
		if (x1>x2) 		{	int d=x1; x1=x2; x2=d; d=y1; y1=y2; y2=d;	}
		if (x1<0) 		{ 	y1-=(MX*x1>>16);	x1=0;					}
		if (x2>=width) 	{	y2-=(((long)MX*(x2-(width-1)))>>16);	x2=width-1;		}
		// Now the line is trimmed. Re-clip it in case we've discovered it really never appears
		if ((y1<0 && y2<0) || (x1<0 && x2<0) || (y1>=height && y2>=height) || (x1>=width && x2>=width)) return;
		// delta-X might have been clipped down to nothing by now
		if (x2==x1) { vline(x1,y1,y2-y1,col); return; }

		// OK, OK. We really do have to draw a line then, I guess. :(
		if (dx>dy) {
			final int MASK=(MX>>>31);
			// Rendering left to right
			int D=0;
			int off=x1+y1*width;
			for (int i=x1-x2;i<0;i++) {
				pixels[off++]=col;
				D+=MX;
				off+=(D>>16)*width;
				D=MASK|(D&65535);
			}
		}
		else {
			if (y1>y2) { int d=x1; x1=x2; x2=d; d=y1; y1=y2; y2=d;	}
			final int MASK=(MY>>>31);
			// Rendering top to bottom
			int D=0;
			int off=x1+y1*width;
			for (int i=y1-y2;i<0;i++) {
				pixels[off]=col;
				off+=width;
				D+=MY;
				off+=(D>>16);
				D=MASK|(D&65535);
			}
		}
	}
	
	public static void vline(int x, int y, int h, int col) {
		if (x<0 || x>=width) return;
		if (h<0) { y+=h; h=-h; }
		if (y<0) { h+=y; y=0; }
		if (y+h>=height) { h=height-y; }
		int off=x+y*width;
		for (int n=-h;n<0;n++) { pixels[off]=col; off+=width; }
	}
	
	public static void hline(int x, int y, int w, int col) {
		if (y<0 || y>=height) return;
		if (w<0) { x+=w; w=-w; }
		if (x<0) { w+=x; x=0; }
		if (x+w>width) { w=width-x; }
		int off=x+y*width;
		for (;w>0;w--) pixels[off++]=col; 
	}
	
	public static void marker(int x, int y, int size, int col) {
		hline(x-size,y,(size<<1),col);
		vline(x,y-size,(size<<1),col);
	}
	
	public static void tri(int x1, int y1, int x2, int y2, int x3, int y3, int col) {
		if (y1==y2 && y2==y3) return;
		// Very cheap bubble sort to put the points in y-order
		if (y2>y3) { int d=y2; y2=y3; y3=d; d=x2; x2=x3; x3=d; }
		if (y1>y2) { 
			int d=y1; y1=y2; y2=d; d=x1; x1=x2; x2=d; 
			if (y2>y3) { d=y2; y2=y3; y3=d; d=x2; x2=x3; x3=d; }
		}
		if (y1>height || y3<0) return;
//		marker(x1,y1,10, 0xff00ff);
//		marker(x2,y2,10, 0xff00ff);
//		marker(x3,y3,10, 0xff00ff);

		// Special case: Icosoles
		if (y1==y2) {
			final int H=y3-y1;
			final int xl_inc=((x3-x1)<<16)/H;
			final int xr_inc=((x3-x2)<<16)/H;
			int xl=x1<<16;
			int xr=x2<<16;
			if (y1<0) {
				xl-=y1*xl_inc;
				xr-=y1*xr_inc;
				y1=0;
			}
			if (y3>height) y3=height;
			fill_area(pixels,y1,y3-y1,xl,xr,xl_inc,xr_inc,col);
		}
		else {
			if (y1<0) {
				if (y2<0) {
					// Just fill the lower portion
					final int xl_inc=((x3-x1)<<16)/(y3-y1);
					final int xr_inc=((x3-x2)<<16)/(y3-y2);
					final int xl=(x1<<16)-(y1*xl_inc);
					final int xr=(x2<<16)-(y2*xr_inc);
					if (y3>height) y3=height;
					fill_area(pixels,0,y3,xl,xr,xl_inc,xr_inc,col);
				}
				else {
					// Two-section triangle with top clipping
					final int xl_inc=((x3-x1)<<16)/(y3-y1);
					final int xr_inc=((x2-x1)<<16)/(y2-y1);
					final int xl=(x1<<16)-(y1*xl_inc);
					final int xr=(x1<<16)-(y1*xr_inc);
					fill_area(pixels,0,y2>height?height:y2,xl,xr,xl_inc,xr_inc,col);
					if (y3>y2 && y2<height) {
						final int xl_inc2=((x3-x1)<<16)/(y3-y1);
						final int xr_inc2=((x3-x2)<<16)/(y3-y2);
						final int xl2=(x1<<16)+((y2-y1)*xl_inc2);
						final int xr2=(x2<<16);
						if (y3>height) y3=height;
						fill_area(pixels,y2,y3-y2,xl2,xr2,xl_inc2,xr_inc2,col);						
					}
				}
			}
			else {
				final int xl_inc=((x2-x1)<<16)/(y2-y1);
				final int xr_inc=((x3-x1)<<16)/(y3-y1);
				final int xl=(x1<<16);
				final int xr=xl;
				fill_area(pixels,y1,(y2>height?height:y2)-y1,xl,xr,xl_inc,xr_inc,col);
				if (y3>y2 && y2<height) {
					final int xl_inc2=((x3-x1)<<16)/(y3-y1);
					final int xr_inc2=((x3-x2)<<16)/(y3-y2);
					final int xl2=(x1<<16)+((y2-y1)*xl_inc2);
					final int xr2=(x2<<16);
					if (y3>height) y3=height;
					fill_area(pixels,y2,y3-y2,xl2,xr2,xl_inc2,xr_inc2,col);						
				}
			}
		}
	}
	
	private static void fill_area(int[] px, int off, int h, int xl, int xr, int xl_inc, int xr_inc, final int col) {
		// Juggle variables to ensure we're always running right to left
		if (xl>xr || (xl==xr && xl_inc>xr_inc)) {
			int d=xl; xl=xr; xr=d;
			d=xl_inc; xl_inc=xr_inc; xr_inc=d;
		}
		final int width16=width<<16;

		int _xl=(xl<0) ? 0 : (xl>>16);
		int _xr=(xr>width16) ? width : (xr>>16);
		off=(off*width);
		for (;h>0;h--) {
			if (xr>0 && xl<width16) {
				// Clipped non-FP versions of xl,xr
				_xl=(xl<0) ? 0 : (xl>>16);
				_xr=(xr>width16) ? width : (xr>>16);
				off+=_xl;
				final int len=_xr-_xl;
				for (int i=-len;i<0;i++) px[off++]=col;
				off-=_xr;
			}
			off+=width;
			xl+=xl_inc;
			xr+=xr_inc;
		}
	}
	
	
	public static void triGouraud(int x1, int y1, int x2, int y2, int x3, int y3, int col1, int col2, int col3) {
		// Very cheap bubble sort to put the points in y-order
		if (y2>y3) { int d=y2; y2=y3; y3=d; d=x2; x2=x3; x3=d; d=col2; col2=col3; col3=d; }
		if (y1>y2) { 
			int d=y1; y1=y2; y2=d; d=x1; x1=x2; x2=d; d=col1; col1=col2; col2=d; 
			if (y2>y3) { d=y2; y2=y3; y3=d; d=x2; x2=x3; x3=d; d=col2; col2=col3; col3=d; }
		}
		
		int xl=x1<<16;
		int xr=xl;
		int xl_inc, xr_inc;

		int RB=0xff00ff;
		int G=0x00ff00;
		int coll_rb=col1&RB;
		int coll_g=col1&G;
		int colr_rb=coll_rb;
		int colr_g=coll_g;
		int coll_rb_inc, 
		    coll_g_inc,
		    colr_rb_inc,
		    colr_g_inc;
		if (y2>y1) {
			xl_inc=((x2-x1)<<16)/(y2-y1);
			xr_inc=((x3-x1)<<16)/(y3-y1);
			coll_rb_inc=(col2&RB)-(col1&RB);
			coll_g_inc=(col2&G)-(col1&G);
			colr_rb_inc=(col3&RB)-(col1&RB);
			colr_g_inc=(col3&G)-(col1&G);
			if (xl_inc>xr_inc) { int d=xr_inc; xr_inc=xl_inc; xl_inc=d; }
			for (;y1<y2;y1++) {
				int _xl=(xl>>16);
				int _xr=(xr>>16);
				if (_xr>0 && _xl<width) {
					if (_xl<0) _xl=0;
					if (_xr>width) _xr=width;
					int len=_xr-_xl;
					int off=_xl+(y1*width);
					int arb=coll_rb;
					int ag=coll_g;
					int rb_inc=colr_rb-coll_rb;
					int g_inc=colr_g-coll_g;
					for (int n=-len; n<0; n++) {
						pixels[off++]=(arb&RB)|(ag&G);
						arb+=rb_inc;
						ag+=g_inc;
					}
				}
				xl+=xl_inc;
				xr+=xr_inc;
				coll_rb+=coll_rb_inc;
				colr_rb+=colr_rb_inc;
				coll_g+=coll_g_inc;
				colr_g+=colr_g_inc;
			}
		}
		else {
			xr=x2;
		}
		if (y3>y1) {
			xl_inc=((x3<<16)-xl)/(y3-y1);
			xr_inc=((x3<<16)-xr)/(y3-y1);
			for (;y1<y3;y1++) {
				int _xl=(xl>>16);
				int _xr=(xr>>16);
				if (_xr>0 && _xl<width) {
					if (_xl<0) _xl=0;
					if (_xr>width) _xr=width;
					int off=_xl+(y1*width);
					for (int n=_xl; n<_xr; n++) pixels[off++]=0;
				}
				xl+=xl_inc;
				xr+=xr_inc;
			}
		}
	}
	
	
}
