package edu.ou.cs.cg.ck;

//import java.lang.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.FPSAnimator;
import java.util.*;

// will probably use...
import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import java.lang.Math;
import com.jogamp.opengl.util.awt.TextRenderer;
import edu.ou.cs.cg.utilities.Utilities;
import java.awt.event.*;
import java.awt.geom.*;


public final class View
	implements GLEventListener
{
	//**********************************************************************
	// Private Class Members
	//**********************************************************************

	private static final int			DEFAULT_FRAMES_PER_SECOND = 60;
	public static final Random	RANDOM = new Random();
	private GLU glu = new GLU();

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final GLJPanel				canvas;
	private int						w;			// Canvas width
	private int						h;			// Canvas height

	private final FPSAnimator			animator;
	private int						k;	// Frame counter

	private final Model				model;

	private final KeyHandler			keyHandler;
	private final MouseHandler			mouseHandler;
	
	private float r;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public View(GLJPanel canvas)
	{
		this.canvas = canvas;

		r = 0.0f;

		// Initialize rendering
		k = 0;
		canvas.addGLEventListener(this);

		// Initialize model (scene data and parameter manager)
		model = new Model(this);

		// Initialize controller (interaction handlers)
		keyHandler = new KeyHandler(this, model);
		mouseHandler = new MouseHandler(this, model);

		// Initialize animation
		animator = new FPSAnimator(canvas, DEFAULT_FRAMES_PER_SECOND);
		animator.start();
	}

	//**********************************************************************
	// Getters and Setters
	//**********************************************************************

	public GLJPanel	getCanvas()
	{
		return canvas;
	}

	public int	getWidth()
	{
		return w;
	}

	public int	getHeight()
	{
		return h;
	}
	
	
	private void update(GLAutoDrawable drawable){
		k++; // increment frame ctr
		model.setK(k);
	}

	// *************************
	// Override methods (GLEventListener)
	// *************************

	@Override
	public void display( GLAutoDrawable drawable ) {

		// Update the pipeline here (clear buffer etc)
		final GL2 gl = drawable.getGL().getGL2();

		// set clear color (background color)
		float[] tmpClearCol = model.getClearColor();
		gl.glClearColor(tmpClearCol[0],tmpClearCol[1],tmpClearCol[2],1.0f);

	   	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );
	   	gl.glLoadIdentity();
	   	gl.glTranslatef( 0f, 0f, model.getDistance()); // translates back 5 units
 
		update(drawable);

		// Rotate The Cube On X, Y & Z
		// TODO: Let user specify axis of rotation
	   	gl.glRotatef(r, 1.0f, 1.0f, 1.0f); 
		
		// Draw the form
		// TODO: Switch case for different geometry (cube,cone,pyramid,teapot...), specified by model/user

		// TODO: Mess around for cool effects here
		switch (model.getGeomID())
		{
			case 0:
				drawCube(gl,1.0f);
				break;
			case 1:
				drawSquarePyramid(gl, 1.0f);
				break;
		}

	   	gl.glFlush();
		
		// TODO: Store rotation speed in model, change with keys
		if (model.isRotating()){
			r -= model.getRotationSpeed(); // change rotation angle iff spinning enabled
		}
		
	}
	
	@Override
	public void dispose( GLAutoDrawable drawable ) {
	}
	
	@Override
	public void init( GLAutoDrawable drawable ) {
		// Called when OpenGL context is initialized

		// Initialize pipeline
	    final GL2 gl = drawable.getGL().getGL2();
	    gl.glShadeModel( GL2.GL_SMOOTH );
	    gl.glClearColor( 0f, 0f, 0f, 0f );
	    gl.glClearDepth( 1.0f );
		gl.glEnable( GL2.GL_DEPTH_TEST );
	    gl.glDepthFunc( GL2.GL_LEQUAL );
		gl.glHint( GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST );

		// Disable rending backs of faces
		gl.glCullFace(GL2.GL_BACK);
		gl.glEnable(GL2.GL_CULL_FACE);

		// enable transparency
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	}
	   
	@Override
	public void reshape( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		// Handles when window is resized.
		// Keeps perspective consistent etc
		
		final GL2 gl = drawable.getGL().getGL2();
	    if( height < 0 )
		   height = 1;
			 
	    final float h = ( float ) width / ( float ) height;
	    gl.glViewport( 0, 0, width, height );
	    gl.glMatrixMode( GL2.GL_PROJECTION );
	    gl.glLoadIdentity();
		 
	    glu.gluPerspective( 45.0f, h, 1.0, 20.0 );
	    gl.glMatrixMode( GL2.GL_MODELVIEW );
	    gl.glLoadIdentity();
	}

	// ***********************************
	// Display helper methods
	// ***********************************

	private void drawCube(GL2 gl, float alpha){
		// NOTE: We may want to use tris?
		float chromMagnitude = model.getChromMagnitude();
		float distance = model.getDistance();

		gl.glBegin(GL2.GL_QUADS); // Start Drawing The Cube 

		for (int i = 0; i < CUBE_GEOMETRY.length; ++i){
			// Specify color of each face
			if (i == 0) gl.glColor4f( 1f,0f,0f,alpha ); // red color
			if (i == 4) gl.glColor4f( 0f,1f,0f,alpha  ); // green color
			if (i == 8) gl.glColor4f( 0f,0f,1f,alpha  ); // blue color
			if (i == 12) gl.glColor4f( 1f,1f,0f,alpha  ); // yellow
			if (i == 16) gl.glColor4f( 1f,0f,1f,alpha  ); // purple
			if (i == 20) gl.glColor4f( 0f,1f,1f,alpha  ); // sky blue
			// Draw the vertex
			gl.glVertex3f(CUBE_GEOMETRY[i].getX(),
						  CUBE_GEOMETRY[i].getY(),
						  CUBE_GEOMETRY[i].getZ());
		}

		for (int i = 0; i < CUBE_GEOMETRY.length; ++i){
			// Specify color of each face
			if (i == 0) gl.glColor4f( 1f,0f,0f,alpha ); // red color
			if (i == 4) gl.glColor4f( 0f,1f,0f,alpha  ); // green color
			if (i == 8) gl.glColor4f( 0f,0f,1f,alpha  ); // blue color
			if (i == 12) gl.glColor4f( 1f,1f,0f,alpha  ); // yellow
			if (i == 16) gl.glColor4f( 1f,0f,1f,alpha  ); // purple
			if (i == 20) gl.glColor4f( 0f,1f,1f,alpha  ); // sky blue
			// Draw the vertex
			gl.glVertex3f(CUBE_GEOMETRY[i].getX(),
						  CUBE_GEOMETRY[i].getY(),
						  CUBE_GEOMETRY[i].getZ());
		}

		gl.glColor4f( 0f,0f,1f,.3f  ); // blue color
		for (int i = 0; i < CUBE_GEOMETRY.length; ++i){
			// Draw the vertex
			gl.glVertex3f(CUBE_GEOMETRY[i].getX() - chromMagnitude * (-distance - CUBE_GEOMETRY[i].getZ()),
						  CUBE_GEOMETRY[i].getY() - chromMagnitude * (-distance - CUBE_GEOMETRY[i].getZ()),
						  CUBE_GEOMETRY[i].getZ() - chromMagnitude * (-distance - CUBE_GEOMETRY[i].getZ()));
		}
		
		gl.glColor4f( 1f,0f,0f,.3f); // red color
		for (int i = 0; i < CUBE_GEOMETRY.length; ++i){
			// Draw the vertex
			gl.glVertex3f(CUBE_GEOMETRY[i].getX() + chromMagnitude * (-distance - CUBE_GEOMETRY[i].getZ()),
						  CUBE_GEOMETRY[i].getY() + chromMagnitude * (-distance - CUBE_GEOMETRY[i].getZ()),
						  CUBE_GEOMETRY[i].getZ() + chromMagnitude * (-distance - CUBE_GEOMETRY[i].getZ()));
		}

		gl.glEnd(); // Done Drawing The Quad
	}

	private void drawSquarePyramid(GL2 gl, float alpha){
		float chromMagnitude = model.getChromMagnitude();
		float distance = model.getDistance();

		gl.glBegin(GL2.GL_TRIANGLES);

		for (int i = 0; i < PYRAMID_4.length; ++i){
			if (i == 0) gl.glColor4f( 1f,0f,0f,alpha ); // red color
			if (i == 3) gl.glColor4f( 0f,1f,0f,alpha  ); // green color
			if (i == 6) gl.glColor4f( 0f,0f,1f,alpha  ); // blue color
			if (i == 9) gl.glColor4f( 1f,1f,0f,alpha  ); // yellow
			gl.glVertex3f(PYRAMID_4[i].getX(),
						  PYRAMID_4[i].getY(),
						  PYRAMID_4[i].getZ());
		}

		gl.glColor4f( 0f,0f,1f,.3f  ); // blue color
		for (int i = 0; i < PYRAMID_4.length; ++i){
			gl.glVertex3f(PYRAMID_4[i].getX() - chromMagnitude * (-distance - PYRAMID_4[i].getZ()),
						  PYRAMID_4[i].getY() - chromMagnitude * (-distance - PYRAMID_4[i].getZ()),
						  PYRAMID_4[i].getZ() - chromMagnitude * (-distance - PYRAMID_4[i].getZ()));
		}

		gl.glColor4f( 1f,0f,0f,.3f); // red color
		for (int i = 0; i < PYRAMID_4.length; ++i){
			gl.glVertex3f(PYRAMID_4[i].getX() + chromMagnitude * (-distance - PYRAMID_4[i].getZ()),
						  PYRAMID_4[i].getY() + chromMagnitude * (-distance - PYRAMID_4[i].getZ()),
						  PYRAMID_4[i].getZ() + chromMagnitude * (-distance - PYRAMID_4[i].getZ()));
		}

		gl.glEnd();
	}

	// ***********************************
	// Define some geometric forms to draw
	// ***********************************

	// Cube defined with quads
	private static final Point3D[] CUBE_GEOMETRY = new Point3D[]
	{
		// TODO: Move the points in here rolling a custom 3d point class
		new Point3D(1.0f,1.0f,-1.0f),
		new Point3D(-1.0f, 1.0f, -1.0f),
		new Point3D( -1.0f, 1.0f, 1.0f ),
		new Point3D( 1.0f, 1.0f, 1.0f ),

		new Point3D( 1.0f, -1.0f, 1.0f ),
		new Point3D( -1.0f, -1.0f, 1.0f ),
		new Point3D( -1.0f, -1.0f, -1.0f ),
		new Point3D(1.0f, -1.0f, -1.0f ),

		new Point3D( 1.0f, 1.0f, 1.0f ),
		new Point3D( -1.0f, 1.0f, 1.0f ),
		new Point3D( -1.0f, -1.0f, 1.0f),
		new Point3D(1.0f, -1.0f, 1.0f),

		new Point3D(1.0f, -1.0f, -1.0f),
		new Point3D( -1.0f, -1.0f, -1.0f ),
		new Point3D( -1.0f, 1.0f, -1.0f ),
		new Point3D(1.0f, 1.0f, -1.0f ),

		new Point3D( -1.0f, 1.0f, 1.0f ),
		new Point3D( -1.0f, 1.0f, -1.0f ),
		new Point3D( -1.0f, -1.0f, -1.0f ),
		new Point3D( -1.0f, -1.0f, 1.0f ),

		new Point3D( 1.0f, 1.0f, -1.0f ),
		new Point3D( 1.0f, 1.0f, 1.0f),
		new Point3D(1.0f, -1.0f, 1.0f ),
		new Point3D( 1.0f, -1.0f, -1.0f ),
	};

	// A pyramid with 4 triangles and an open base
	private static final Point3D[] PYRAMID_4 = new Point3D[]
	{
      new Point3D( 0.0f, 1.0f, 0.0f),
      new Point3D(-1.0f, -1.0f, 1.0f),
      new Point3D(1.0f, -1.0f, 1.0f),
 
      new Point3D(0.0f, 1.0f, 0.0f),
      new Point3D(1.0f, -1.0f, 1.0f),
      new Point3D(1.0f, -1.0f, -1.0f),
 
      new Point3D(0.0f, 1.0f, 0.0f),
      new Point3D(1.0f, -1.0f, -1.0f),
      new Point3D(-1.0f, -1.0f, -1.0f),
 
      new Point3D( 0.0f, 1.0f, 0.0f),
      new Point3D(-1.0f,-1.0f,-1.0f),
      new Point3D(-1.0f,-1.0f, 1.0f),
	};

}

