package main;
import gfx.*;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import app.AppShell;
import app.Maths;

/**
 * Simple application builds a 3D wireframe holodeck environment to explore.
 */
public class Holodeck extends AppShell {

	private static final int 	WALK_SPEED=20;
	private static final float 	MOUSE_SENSITIVITY=(float)(Math.PI/1000);
	
	private Cam3D cam;
	
	private boolean gotMouse=false;
	private int mousex, mousey;
	private Robot mouseController;
	
	private Polygon3D poly;
	
	public static void main(String[] args) {
		new Holodeck().run();
	}
	public Holodeck() {
		super("Holodeck", 640, 480);
		Graphics3D.set_origin(320, 240);
		cam=new Cam3D();
		// Offset from centre
		cam.z=-300;
				
		// Head height above the floor
		cam.y=100;
		try {
			mouseController=new Robot();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// Blue and yellow crystal
		poly=new Polygon3D(4, 3);
		poly.set_vertex(0, -100, 100, 0);
		poly.set_vertex(1, 60, 100,  60);
		poly.set_vertex(2, 60, 100, -60);
		poly.set_vertex(3, 0, 200, 0);
		poly.set_face(0, 0, 1, 2, 0xff0000);
		poly.set_face(1, 0, 1, 3, 0xcc0000);
		poly.set_face(2, 0, 2, 3, 0xffcc00);
		poly.set_face(2, 1, 2, 3, 0xffff00);
		poly.translate(600, 0, 0);
	}
	
	/** Steal or reveal the mouse from the user */
	private void takemouse() 	{gotMouse=true; hidemouse(true); }
	private void releasemouse() {gotMouse=false;hidemouse(false);}
	
	/** 
	 * Part of our API is to deal with the mouse state.
	 * Here we use it to control camera orientation.
	 */
    public void processMouseState(int x, int y) {
	    if (gotMouse) {
	    	int dx=x-mousex;
	    	int dy=y-mousey;
	    	if (dx!=0 || dy!=0) {
	    		cam.yAngle+=MOUSE_SENSITIVITY*dx;
	    		cam.xAngle-=MOUSE_SENSITIVITY*dy;
	    		java.awt.Point p=MouseInfo.getPointerInfo().getLocation();
	    		mouseController.mouseMove(p.x-dx, p.y-dy);
	    	}
	    }
    }
    
    public void mouseclick(int x, int y, int b) {
    	if (gotMouse) {
    		releasemouse();
    	}
    	else {
    		takemouse();
    		mousex=x;
    		mousey=y;
    	}
    }
    
    public void keypress(int code, char c) {
    	if (code==KeyEvent.VK_ESCAPE) releasemouse();
    	if (code==KeyEvent.VK_SPACE) debugVisible=!debugVisible; 
    }
	
	public void processKeyState(boolean[] held) {
		boolean forward=		held[KeyEvent.VK_W] || held[KeyEvent.VK_UP];
		boolean backward=		held[KeyEvent.VK_S] || held[KeyEvent.VK_DOWN];
		boolean left=			held[KeyEvent.VK_A] || held[KeyEvent.VK_LEFT];
		boolean right=			held[KeyEvent.VK_D] || held[KeyEvent.VK_RIGHT];
		boolean yup=			held[KeyEvent.VK_X];
		boolean ydown=			held[KeyEvent.VK_Z];
		if (forward || backward || left || right || yup || ydown) 		{
			float[][] f=cam.toRotationMatrix();
			// Move on the plane like in a normal FPS
			int speed=(forward?WALK_SPEED:0) - (backward?WALK_SPEED:0);
			int strafe=(right?WALK_SPEED:0) - (left?WALK_SPEED:0);
			cam.x+=speed*f[2][0] + strafe*f[0][0] ;
			cam.z+=speed*f[2][2] + strafe*f[0][2];
			// Use x and z to change elevation regardless of orientation
			int raise=(yup?WALK_SPEED:0) - (ydown?WALK_SPEED:0);
			cam.y+=raise;
		}
		// Handle perspective warping
		boolean pup=			held[KeyEvent.VK_2];
		boolean pdown=			held[KeyEvent.VK_1];
		if (pup || pdown) {
			Graphics3D.PERSPECTIVE+=20*((pup?1:0)-(pdown?1:0));
		}
	}
	
	public void update() {
		debugText= new String[] {
				" [[ space :: debug ]]",
				"Location=",
				"  w/a/s/d :: ("+Maths.twoDP(cam.x)+","+Maths.twoDP(cam.y)+")",
				"      z/x :: "+Maths.twoDP(cam.y),
				"Perspective=",
				"      1/2 :: "+((int)Graphics3D.PERSPECTIVE),
				"Orientation=",
				"    mouse :: ("+Maths.twoDP(cam.xAngle)+"¹, "+Maths.twoDP(cam.yAngle)+"¹)"
				
		};
	}
	
	public void render() {
		Graphics.cls();
		
		final int FLOOR=0;
		final int TOP=400;
		// Draw the grid on the floor and ceiling
		final int W=120, N=W*10;
		final int FLOORCOL=0x336633;
		final int TOPCOL=0x003300;
		final int BOXCOL=0xaaffaa;
		
		for (int z=-N;z<N;z+=W) {
			for (int x=-N;x<N;x+=W) {
				Graphics3D.line(cam,x,FLOOR,z,x+W,FLOOR,z,FLOORCOL);
				Graphics3D.line(cam,x,FLOOR,z,x,FLOOR,z+W,FLOORCOL);
				Graphics3D.line(cam,x,TOP,z,x+W,TOP,z,TOPCOL);
				Graphics3D.line(cam,x,TOP,z,x,TOP,z+W,TOPCOL);
			}
		}
		// Draw the outermost bounding box with crosses and things
		for (int i=0;i<8;i++) {
			final boolean a=(i&1)==0, b=(i&2)==0, c=(i&4)==0;
			int px=a?N:-N, pz=b?N:-N;
			int f1=c?TOP:FLOOR;
			Graphics3D.line(cam,px,FLOOR,pz,px,TOP,pz,	BOXCOL);
			Graphics3D.line(cam,px,f1,pz,0,FLOOR+TOP>>1,pz,BOXCOL);
			Graphics3D.line(cam,px,f1,pz,px,FLOOR+TOP>>1,0,BOXCOL);
			Graphics3D.line(cam,px,f1,pz,px,f1,0,	BOXCOL);
			Graphics3D.line(cam,px,f1,pz,0,	f1,pz,	BOXCOL);
		}
		final int H=100, S=50;
		final int[] col=new int[] {
				0xff00ff,
				0x00ffff,
				0x0000ff,
				0xffff00,
				0x770077,
				0x007777,
				0x000077,
				0x777700,
		};
		int x1, x2, z1, z2, y;
		for (int i=0;i<4;i++) {
			boolean a=(i&1)==0, b=(i&2)==0;
			x1=a?S:-S; 
			x2=(a^b)?-S : S;
			z1=x2;
			z2=-x1;
			Graphics3D.tri(cam,0,0,0, x1, H, z1, x2, H, z2, col[i]);
		}
		for (int i=0;i<4;i++) {
			boolean a=(i&1)==0, b=(i&2)==0;
			x1=a?S:-S; 
			x2=(a^b)?-S : S;
			z1=-x2;
			z2=x1;
			Graphics3D.tri(cam,0,2*H,0, x1, H, z1, x2, H, z2, col[i+4]);
		}
		poly.render_markers(cam,0xff00ff, 3);
		poly.render_edges(cam,0xff00ff);
		poly.render(cam);
		
		// Awt overlays
		
	}
}
