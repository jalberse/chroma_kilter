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
		   
		// All scenes are translated towards/away from camera like this
	   	gl.glTranslatef( 0f, 0f, model.getDistance()); // translates back 5 units

		// Draw the scene
		switch (model.getGeomID())
		{
			case 0:
				gl.glRotatef(r, 1.0f, 1.0f, 1.0f); // Rotate The Scene On X, Y & Z
				drawObject(gl,1.0f,CUBE_GEOMETRY);
				break;
			case 1:
				gl.glRotatef(r, 1.0f, 1.0f, 1.0f); // Rotate The Scene On X, Y & Z
				drawObject(gl, 1.0f,PYRAMID_4);
				break;
			case 2:
				gl.glTranslatef( 0f, -1f, -2f); // Center it
				gl.glRotatef(r, 0.3f * (float)Math.cos(r / 15), 1.0f, 0.3f * (float)Math.sin(r / 30)); // Rotate The Scene on Y axis
				drawObject(gl,1.0f,teapotVerts);
				break;
			case 3:
				gl.glRotatef(r, 0.0f, 1.0f, 0.0f); // Rotate The Scene On Y
				drawObject(gl,1.0f,suzanneVerts);
				break;
			case 4:
				gl.glTranslatef(0f, -1f, 0f); // center model
				// perspective rotation (look down at it)
				gl.glRotatef(20f, 1f, -.1f, 0f); 
				gl.glRotatef(r, 0.0f, 1.0f, 0.0f); // Rotate The Scene On Y
				drawObject(gl,1.0f,houseVerts);
				break;
			case 5:
				gl.glTranslatef(0f, -1f, 0f); // center model
				// perspective rotation (look down at it)
				gl.glRotatef(20f, 1f, -.1f, 0f);
				gl.glRotatef(r, 0.0f, 1.0f, 0.0f); // Rotate The Scene On Y
				gl.glScalef(.8f,.8f,.8f); // scale down
				drawObject(gl,1.0f,bankVerts);
				break;
			case 6:
				gl.glTranslatef(0f, -1.5f, -.5f); // center model
				// perspective rotation (look down at it)
				gl.glRotatef(20f, 1f, -.1f, 0f); 
				gl.glRotatef(r, 0.0f, 1.0f, 0.0f); // Rotate The Scene On Y
				drawObject(gl,1.0f,flatVerts);
				break;
			case 7:

				// TODO: For composite scenes like this,
				//		 the way we calc distance to camera is BROKEN
				//		 Should work to fix

				// Rotate the street and push it back in space
				gl.glTranslatef(0f,0f,-20f);
				// perspective rotation (look down at it)
				gl.glRotatef(20f, 1f, -.1f, 0f); 
				gl.glRotatef(r, 0f,1f,0f); // Spin around!
				// Draw a big circle that is the "ground"
				// We have to rotate it flat first
				drawCircle(gl,0f,0f,0f,10f); // x y z r

				// Draw buildings in a row
				gl.glTranslatef(-8f,0f,-4f);
				// bank
				gl.glTranslatef(3.5f,0f,0f);
				drawObject(gl,1.0f,bankVerts);
				// Two flats
				gl.glTranslatef(3.5f,0f,0f);
				drawObject(gl,1.0f,flatVerts);
				gl.glTranslatef(2.5f,0f,0f);
				drawObject(gl,1.0f,flatVerts);
				// Houses
				gl.glTranslatef(2.0f,0f,0f);
				drawObject(gl,1.0f,houseVerts);
				gl.glTranslatef(1.50f,0f,0f);
				drawObject(gl,1.0f,houseVerts);
				gl.glTranslatef(1.50f,0f,0f);
				drawObject(gl,1.0f,houseVerts);
				gl.glTranslatef(1.50f,0f,0f);
				drawObject(gl,1.0f,houseVerts);

				// Neighborhood on other side
				gl.glTranslatef(0f,0f,8f);
				gl.glRotatef(180f,0f,1f,0f); // flip the houses round
				gl.glTranslatef(3.5f,0f,0f);
				drawObject(gl,1.0f,houseVerts);
				gl.glTranslatef(1.5f,0f,0f);
				drawObject(gl,1.0f,houseVerts);
				gl.glTranslatef(1.5f,0f,0f);
				drawObject(gl,1.0f,houseVerts);
				gl.glTranslatef(1.5f,0f,0f);
				drawObject(gl,1.0f,houseVerts);
				gl.glTranslatef(1.5f,0f,0f);
				drawObject(gl,1.0f,houseVerts);

				break;
			case 8:
				// TODO: 8 objects in a cube
				break;
			case 9:
				// TODO: Objects in a line
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
		 
	    glu.gluPerspective( 45.0f, h, 1.0, 60.0 );
	    gl.glMatrixMode( GL2.GL_MODELVIEW );
	    gl.glLoadIdentity();
	}

	// ***********************************
	// Display helper methods
	// ***********************************

	private void prepareEffect(GL2 gl)
	{
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
	}

	private void midEffect(GL2 gl)
	{
		// Set up for aberrations
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
	}

	private void postEffect(GL2 gl)
	{
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

	private void drawObject(GL2 gl, float alpha, Point3D[] obj)
	{
		prepareEffect(gl);
		switch (model.getGeomID())
		{
			case 0:
				drawCubeBaseObject(gl,alpha);
				break;
			case 1:
				drawSquarePyramidBaseObject(gl, alpha);
				break;
		}
		midEffect(gl);
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
				
				break;
		}
		postEffect(gl);
	}

	// Draws an object with aberrations
	private void drawObject(GL2 gl, float alpha, Face[] obj){
		// Preprocess for the base object
		prepareEffect(gl);
		
		// Draw the base object
		drawBaseObject(gl, alpha, obj);

		// Preprocess for aberrations
		midEffect(gl);

		// Draw the aberrations
		drawObjectAbTri(gl, alpha, obj);

		// Clean up
		postEffect(gl);
	}

	/*
		Draws an object

		TODO draw wireframe too since we don't have shading
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
		if (model.isWireframe()){
			// draw a white wireframe
			gl.glColor4f(0f,0f,0f,alpha);
			for (int i = 0; i < obj.length; ++i){
				gl.glBegin(GL2.GL_LINE_LOOP); 
				for (int j = 0; j < obj[i].getSize(); ++j){
					gl.glVertex3f(obj[i].get(j).getX(),
									obj[i].get(j).getY(),
									obj[i].get(j).getZ());
				}
				gl.glEnd();
			}
		}
	}

	/*
		Draw chromatic aberrations of an object
	*/
	private void drawObjectAbTri(GL2 gl, float alpha, Face[] obj){
		
		float[][] aberrationVectors = getAberrationUnitVectors(gl);

		gl.glColor4f( 0f,0f,1f,.3f  ); // blue color
		for (int i = 0; i < obj.length; ++i){
			
			float[] chromMagnitudes = getVertexChromMagnitudes(gl, obj[i]);
			//System.out.println(Arrays.toString(chromMagnitudes));
			
			gl.glBegin(GL2.GL_POLYGON); 
			for (int j = 0; j < obj[i].getSize(); ++j){
				gl.glVertex3f(obj[i].get(j).getX() + chromMagnitudes[j] * aberrationVectors[0][0],
							  obj[i].get(j).getY() + chromMagnitudes[j] * aberrationVectors[0][1],
							  obj[i].get(j).getZ() + chromMagnitudes[j] * aberrationVectors[0][2]);
			}
			
			gl.glEnd();
		}
		
		gl.glColor4f( 1f,0f,0f,.3f); // red color
		for (int i = 0; i < obj.length; ++i){
			
			float[] chromMagnitudes = getVertexChromMagnitudes(gl, obj[i]);
			
			gl.glBegin(GL2.GL_POLYGON); 
			for (int j = 0; j < obj[i].getSize(); ++j){
				gl.glVertex3f(obj[i].get(j).getX() + chromMagnitudes[j] * aberrationVectors[1][0],
							  obj[i].get(j).getY() + chromMagnitudes[j] * aberrationVectors[1][1],
							  obj[i].get(j).getZ() + chromMagnitudes[j] * aberrationVectors[1][2]);
			}
			
			gl.glEnd();
		}
	}

	/**
	 * Get the vectors for perturbing the aberrations in object coordinates.
	 * @param gl
	 * @return
	 */
	private float[][] getAberrationUnitVectors(GL2 gl) {
		
		// Get the modelview matrix, projection matrix, and viewport.
		float[] modelview = new float[16];
		float[] projection = new float[16];
		int[] viewport = new int[4];
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection, 0);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		
		// Define arrays to store the coordinates of each point.
		float[] worldPt0 = new float[3];
		float[] worldPtB = new float[3];
		float[] worldPtR = new float[3];
		//float[] worldPtG = new float[3];
		
		// Calculate the components of the perturbation vectors in screen coordinates.
		float chromAngle = model.getChromAngle();
		float xComp = (float) Math.cos(chromAngle);
		float yComp = (float) Math.sin(chromAngle);
		
		// Calculate the components in world coordinates.
		glu.gluUnProject(0, 0, 0, modelview, 0, projection, 0, viewport, 0, worldPt0, 0);
		glu.gluUnProject( xComp,  yComp, 0, modelview, 0, projection, 0, viewport, 0, worldPtB, 0);
		glu.gluUnProject(-xComp, -yComp, 0, modelview, 0, projection, 0, viewport, 0, worldPtR, 0);
		
		// Calculate the aberration vectors.
		float[][] aberrationVecs = new float[2][3];
		for (int i = 0; i < 3; ++i) {  // red vector
			aberrationVecs[0][i] = worldPtB[i] - worldPt0[i];
		}
		for (int i = 0; i < 3; ++i) {  // blue vector
			aberrationVecs[1][i] = worldPtR[i] - worldPt0[i];
		}
		
		// Normalize and return the vectors.
		float bMag = (float) Math.sqrt(Math.pow(aberrationVecs[0][0], 2)
							         + Math.pow(aberrationVecs[0][1], 2)
							         + Math.pow(aberrationVecs[0][2], 2));
		float rMag = (float) Math.sqrt(Math.pow(aberrationVecs[1][0], 2)
									 + Math.pow(aberrationVecs[1][1], 2)
									 + Math.pow(aberrationVecs[1][2], 2));
		for (int i = 0; i < 3; ++i) {  // blue vector
			aberrationVecs[0][i] /= bMag;
		}
		for (int i = 0; i < 3; ++i) {  // red vector
			aberrationVecs[1][i] /= rMag;
		}
		
		return aberrationVecs;
	}
	
	/**
	 * Return a list of chroma magnitudes for each vertex in a given array of points. The chroma 
	 * magnitudes must be calculated outside of any glBegin call in order for gluProject to 
	 * calculate the correct distance of each vertex from the screen.
	 * @param gl
	 * @param vertices
	 * @return
	 */
	private float[] getVertexChromMagnitudes(GL2 gl, Point3D[] vertices) {
		
		// Get the modelview matrix, projection matrix, and viewport.
		float[] modelview = new float[16];
		float[] projection = new float[16];
		int[] viewport = new int[4];
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
		gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, projection, 0);
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		
		// Construct an array to hold the chroma magnitude for each vertex.
		float[] chromMags = new float[vertices.length];
		
		// Calculate the magnitude of the aberration for every vertex.
		float[] screenPt = new float[3];  // array to store the screen point
		float magnitude = model.getChromMagnitude();
		float zFocalPlane = model.getZFocalPlane();
		for (int idx = 0; idx < chromMags.length; ++idx) {
			
			// Get the vertex in screen coordinates.
			glu.gluProject(vertices[idx].getX(), vertices[idx].getY(), vertices[idx].getZ(), 
					modelview, 0, projection, 0, viewport, 0, screenPt, 0);
			
			// Extract the distance from the screen.
			float screenDistance = screenPt[2];
			
			// Calculate the chroma magnitude.
			chromMags[idx] = magnitude * (zFocalPlane - screenDistance);
		}
		
		return chromMags;
	}
	
	private float[] getVertexChromMagnitudes(GL2 gl, Face obj) {
		return getVertexChromMagnitudes(gl, obj.toArray());
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

		float[][] aberrationVectors = getAberrationUnitVectors(gl);
		float[] chromMagnitudes = getVertexChromMagnitudes(gl, CUBE_GEOMETRY);
		//System.out.println(Arrays.toString(chromMagnitudes));
		
		gl.glBegin(GL2.GL_QUADS); // Start Drawing The Cube
		
		gl.glColor4f( 0f,0f,1f,.3f  ); // blue color
		for (int i = 0; i < CUBE_GEOMETRY.length; ++i){
			// Draw the vertex
			gl.glVertex3f(CUBE_GEOMETRY[i].getX() + chromMagnitudes[i] * aberrationVectors[0][0],
						  CUBE_GEOMETRY[i].getY() + chromMagnitudes[i] * aberrationVectors[0][1],
						  CUBE_GEOMETRY[i].getZ() + chromMagnitudes[i] * aberrationVectors[0][2]);
		}
		
		gl.glColor4f( 1f,0f,0f,.3f); // red color
		for (int i = 0; i < CUBE_GEOMETRY.length; ++i){
			//float magnitude = getChromMagnitude(gl, CUBE_GEOMETRY[i]);
			// Draw the vertex
			gl.glVertex3f(CUBE_GEOMETRY[i].getX() + chromMagnitudes[i] * aberrationVectors[1][0],
						  CUBE_GEOMETRY[i].getY() + chromMagnitudes[i] * aberrationVectors[1][1],
						  CUBE_GEOMETRY[i].getZ() + chromMagnitudes[i] * aberrationVectors[1][2]);
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
		
		float[][] aberrationVectors = getAberrationUnitVectors(gl);
		float[] chromMagnitudes = getVertexChromMagnitudes(gl, PYRAMID_4);

		gl.glBegin(GL2.GL_TRIANGLES);

		gl.glColor4f( 0f,0f,1f,.3f  ); // blue color
		for (int i = 0; i < PYRAMID_4.length; ++i){
			gl.glVertex3f(PYRAMID_4[i].getX() + chromMagnitudes[i] * aberrationVectors[0][0],
						  PYRAMID_4[i].getY() + chromMagnitudes[i] * aberrationVectors[0][1],
						  PYRAMID_4[i].getZ() + chromMagnitudes[i] * aberrationVectors[0][2]);
		}

		gl.glColor4f( 1f,0f,0f,.3f); // red color
		for (int i = 0; i < PYRAMID_4.length; ++i){
			gl.glVertex3f(PYRAMID_4[i].getX() + chromMagnitudes[i] * aberrationVectors[1][0],
						  PYRAMID_4[i].getY() + chromMagnitudes[i] * aberrationVectors[1][1],
						  PYRAMID_4[i].getZ() + chromMagnitudes[i] * aberrationVectors[1][2]);
		}

		gl.glEnd();
	}

	// Draws a circle in the x-z plane with the specified center and radius
	private void drawCircle(GL2 gl, float x, float y, float z, float r)
	{
		gl.glColor3f(.515f,.75f,.56f); // greenish
		gl.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < 32; ++i){
			double a = i * 2.0 * Math.PI / 32;
			float xi = x + r * (float)Math.sin(a);
			float zi = z + r * (float)Math.cos(a);
			gl.glVertex3f(xi,y,zi);
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