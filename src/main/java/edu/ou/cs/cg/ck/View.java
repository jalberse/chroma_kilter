package edu.ou.cs.cg.ck;

//import java.lang.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.FPSAnimator;
import java.util.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;


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

	private Face[] teapotVerts;
	private Face[] suzanneVerts;
	private Face[] houseVerts;
	private Face[] bankVerts;
	private Face[] flatVerts;

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

		teapotVerts = loadObj("resources/teapot.obj");
		suzanneVerts = loadObj("resources/suzanne.obj");
		houseVerts = loadObj("resources/House.obj");
		bankVerts = loadObj("resources/Bank.obj");
		flatVerts = loadObj("resources/Flat.obj");

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

		if (model.isRotating()){
			r -= model.getRotationSpeed(); // change rotation angle iff spinning enabled
		}
	}

	// *************************
	// Override methods (GLEventListener)
	// *************************

	@Override
	public void display( GLAutoDrawable drawable ) {

		// Update the pipeline here (clear buffer etc)
		final GL2 gl = drawable.getGL().getGL2();
		update(drawable);

		// set clear color (background color)
		float[] tmpClearCol = model.getClearColor();
		gl.glClearColor(tmpClearCol[0],tmpClearCol[1],tmpClearCol[2],1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);
		   
		// Transform the whole scene
	   	gl.glLoadIdentity();
	   	gl.glTranslatef( 0f, 0f, model.getDistance()); // translates back 5 units
		// TODO: Let user specify axis of rotation
		gl.glRotatef(r, 1.0f, 1.0f, 1.0f); // Rotate The Scene On X, Y & Z

		// TODO: 8 objects in a cube scene
		// TODO: Objects in a (rough) line to view overlaps scene
		// Draw the scene
		switch (model.getGeomID())
		{
			case 0:
				//drawObject(gl,1.0f,CUBE_GEOMETRY);
				break;
			case 1:
				//drawObject(gl, 1.0f,PYRAMID_4);
				break;
			case 2:
				drawObject(gl,1.0f,teapotVerts);
				break;
			case 3:
				drawObject(gl,1.0f,suzanneVerts);
				break;
			case 4:
				drawObject(gl,1.0f,houseVerts);
				break;
			case 5:
				drawObject(gl,1.0f,bankVerts);
				break;
			case 6:
				drawObject(gl,1.0f,flatVerts);
				break;
		}

	   	gl.glFlush();
		
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

	private void drawObject(GL2 gl, float alpha, Face[] obj){
		// Preprocess for the base object
		switch (model.getRenderMode())
		{
			case "nondestructive":
				gl.glEnable(GL.GL_STENCIL_TEST);
				gl.glClearStencil(0);
				gl.glClear(GL.GL_STENCIL_BUFFER_BIT);

				// Write 1's into stencil buffer to make a "hole" on base object
				gl.glDepthMask(false);
				gl.glStencilFunc(GL.GL_ALWAYS,1,~0);
				gl.glStencilOp(GL.GL_KEEP,GL.GL_KEEP,GL.GL_REPLACE);
				break;
		}
		

		// Draw the base object

		// Primitive objects are exceptions - they might be defined with Quads (cube)
		// Or they might have unique colors per face (eg Pyramid)
		// So they have their own drawing methods
		switch (model.getGeomID())
		{
			case 0:
				drawCubeBaseObject(gl,alpha);
				break;
			case 1:
				drawSquarePyramidBaseObject(gl, alpha);
				break;
			// Done with primitive exceptions
			// Draw complex forms with Tris and monocolor 
			default:
				drawBaseObject(gl, alpha, obj);
				break;
		}

		// Set up for aberations
		switch (model.getRenderMode())
		{
			case "nondestructive":
				// Only draw where base object isn't
				gl.glDepthMask(true);
				gl.glStencilFunc(GL.GL_NOTEQUAL,1,~0);
				gl.glStencilOp(GL.GL_KEEP,GL.GL_KEEP,GL.GL_KEEP);
				break;
			case "nodepth":
				gl.glDisable(GL2.GL_DEPTH_TEST);
				break;
		}
		
		// Draw the aberations
		switch (model.getGeomID())
		{
			case 0:
				drawCubeAb(gl,alpha);
				break;
			case 1:
				drawSquarePyramidAb(gl,alpha);
				break;
			// Done with primitive exceptions
			// Draw complex forms with Tris
			default:
				drawObjectAbTri(gl, alpha, obj);
				break;
		}

		// Undo changes necessary for each render mode
		switch (model.getRenderMode())
		{
			case "nondestructive":
				// Disable stencil for next thing drawn in scene
				gl.glDisable(GL.GL_STENCIL_TEST);
				break;
			case "nodepth":
				gl.glEnable(GL2.GL_DEPTH_TEST);
				break;
		}
		
	}

	/*
		Draws an object
	*/
	private void drawBaseObject(GL2 gl, float alpha, Face[] obj){
		
		gl.glColor4f( 1f,0.59f,0.518f,alpha ); // pinkish - TODO let user toggle
		for (int i = 0; i < obj.length; ++i){
			gl.glBegin(GL2.GL_POLYGON); 
			for (int j = 0; j < obj[i].getSize(); ++j){
				gl.glVertex3f(obj[i].get(j).getX(),
								obj[i].get(j).getY(),
								obj[i].get(j).getZ());
			}
			gl.glEnd();
		}
		
	}

	/*
		Draw chromatic abberations of an object
	*/
	private void drawObjectAbTri(GL2 gl, float alpha, Face[] obj){
		float chromMagnitude = model.getChromMagnitude();
		float distance = model.getDistance(); 

		gl.glColor4f( 0f,0f,1f,.3f  ); // blue color
		for (int i = 0; i < obj.length; ++i){
			// Draw the vertex, displaced based on distance to camera
			// TODO Make a function (switch case - user chooses from function list)
			//		That determines strength of effect from chromMagnitude and distance
			//		Don't forget to call it from other drawObject methods!

			// TODO Affix and choose angle of abberation effect
			
			gl.glBegin(GL2.GL_POLYGON); 
			for (int j = 0; j < obj[i].getSize(); ++j){
				gl.glVertex3f(obj[i].get(j).getX()  - chromMagnitude * (-distance - obj[i].get(j).getZ()),
								obj[i].get(j).getY()  - chromMagnitude * (-distance - obj[i].get(j).getZ()),
								obj[i].get(j).getZ()  - chromMagnitude * (-distance - obj[i].get(j).getZ()));
			}
			
			gl.glEnd();
		}
		
		gl.glColor4f( 1f,0f,0f,.3f); // red color
		for (int i = 0; i < obj.length; ++i){
			gl.glBegin(GL2.GL_POLYGON); 
			for (int j = 0; j < obj[i].getSize(); ++j){
				gl.glVertex3f(obj[i].get(j).getX()  + chromMagnitude * (-distance - obj[i].get(j).getZ()),
								obj[i].get(j).getY()  + chromMagnitude * (-distance - obj[i].get(j).getZ()),
								obj[i].get(j).getZ()  + chromMagnitude * (-distance - obj[i].get(j).getZ()));
			}
			
			gl.glEnd();
		}
	}

	// ****************
	// Special drawing methods for primitives, which have some exceptions
	// (Quads, colors) that means we can't lump them with complicated geometries
	// *****************

	private void drawCubeBaseObject(GL2 gl, float alpha){
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
		gl.glEnd(); // Done Drawing The Quad
	}

	private void drawCubeAb(GL2 gl, float alpha){
		float chromMagnitude = model.getChromMagnitude();
		float distance = model.getDistance();
		gl.glBegin(GL2.GL_QUADS); // Start Drawing The Cube 

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

	private void drawSquarePyramidBaseObject(GL2 gl, float alpha){
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

		gl.glEnd();
	}

	private void drawSquarePyramidAb(GL2 gl, float alpha){
		float chromMagnitude = model.getChromMagnitude();
		float distance = model.getDistance();

		gl.glBegin(GL2.GL_TRIANGLES);

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

	private Face[] loadObj(String filename)
	{
		InputStream in  = this.getClass().getResourceAsStream(filename);
		Vector<Point3D> vertices = new Vector<Point3D>(); // vertex table
		Vector<Face> object = new Vector<Face>(); // Faces
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))){
			String line;
			while ((line = br.readLine()) != null){
				String[] tokens = line.split(" ");
				if (tokens[0].equals("v")){
					// Line represents a vertex - add to the list
					float x = Float.parseFloat(tokens[1]);
					float y = Float.parseFloat(tokens[2]);
					float z = Float.parseFloat(tokens[3]);
					vertices.add(new Point3D(x,y,z));
				}
				if (tokens[0].equals("f")){
					Face f = new Face();
					for (int i = 1; i < tokens.length; ++i){
						f.addVert(vertices.get(Integer.parseInt(tokens[i].split("/")[0]) - 1));
					}
					object.add(f);
				}
			}
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}

		Object[] a = object.toArray();
		Face[] o = Arrays.copyOf(a,a.length,Face[].class); 

		return o;
	}
}