package edu.ou.cs.cg.ck;

//import java.lang.*;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;
import com.jogamp.opengl.*;
import edu.ou.cs.cg.utilities.Utilities;

public final class Model
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View					view;

	// Model variables
	private Point2D.Double				origin;	// Current origin coords
	private Point2D.Double				cursor;	// Current cursor coords
	private int k; // frame counter
	private float rotationSpeed;
	private boolean isRotating;
	private float distance; // distance object is from camera
	private int geomID; // the geometry we are rending
		// 0 - cube
		// 1 - square pyramid
		// ... TODO add more
	private float chromMagnitude; // the strength (translation) of chromatic abberation effect
	private float[] clearColor; 
	private int currClearColor = 0;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Model(View view)
	{
		this.view = view;

		// Initialize user-adjustable variables (with reasonable default values
		origin = new Point2D.Double(0.0, 0.0);
		cursor = null;

		k = 0;

		rotationSpeed = .15f;
		isRotating = true;
		distance = -5.0f;
		chromMagnitude = .1f;

		clearColor = CLEAR_COLORS[0];


		geomID = 0;
	}

	//**********************************************************************
	// Public Methods (Access Variables)
	//**********************************************************************

	public float[] getClearColor(){
		return clearColor;
	}

	public float getChromMagnitude(){
		return chromMagnitude;
	}

	public int getGeomID(){
		return geomID;
	}

	public float getDistance(){
		return distance;
	}

	public boolean isRotating(){
		return isRotating;
	}

	public float getRotationSpeed(){
		return rotationSpeed;
	}

	public int getK(){
		return k;
	}

	public Point2D.Double	getOrigin()
	{
		return new Point2D.Double(origin.x, origin.y);
	}

	public Point2D.Double	getCursor()
	{
		if (cursor == null)
			return null;
		else
			return new Point2D.Double(cursor.x, cursor.y);
	}

	//**********************************************************************
	// Public Methods (Modify Variables)
	//**********************************************************************
	
	public void setClearColorPrevious(){
		if (currClearColor > 0){
			currClearColor--;
			clearColor = CLEAR_COLORS[currClearColor];
		}
	}

	public void setClearColorNext(){
		if (currClearColor < CLEAR_COLORS.length - 1){
			currClearColor++;
			clearColor = CLEAR_COLORS[currClearColor];
		}
	}

	public void setClearColor (float r, float g, float b){
		clearColor[0] = r;
		clearColor[1] = g;
		clearColor[2] = b;
	}

	public void setChromMagnitude(float x){
		chromMagnitude = x;
	}

	public void setGeomID(int x){
		geomID = x;
	}

	public void setDistance(float f){
		distance = f;
	}

	public void setIsRotating(boolean x){
		isRotating = x;
	}

	public void setRotationSpeed(float r){
		rotationSpeed = r;
	}

	public void setK(int k){
		this.k = k;
	}

	public void	setOriginInSceneCoordinates(Point2D.Double q)
	{
		view.getCanvas().invoke(false, new BasicUpdater() {
			public void	update(GL2 gl) {
				origin = new Point2D.Double(q.x, q.y);
			}
		});
	}

	public void	setOriginInViewCoordinates(Point q)
	{
		view.getCanvas().invoke(false, new ViewPointUpdater(q) {
			public void	update(double[] p) {
				origin = new Point2D.Double(p[0], p[1]);
			}
		});
	}

	public void	setCursorInViewCoordinates(Point q)
	{
		view.getCanvas().invoke(false, new ViewPointUpdater(q) {
			public void	update(double[] p) {
				cursor = new Point2D.Double(p[0], p[1]);
			}
		});
	}

	public void	turnCursorOff()
	{
		view.getCanvas().invoke(false, new BasicUpdater() {
			public void	update(GL2 gl) {
				cursor = null;
			}
		});
	}

	//**********************************************************************
	// Inner Classes
	//**********************************************************************

	// Convenience class to simplify the implementation of most updaters.
	private abstract class BasicUpdater implements GLRunnable
	{
		public final boolean	run(GLAutoDrawable drawable)
		{
			GL2	gl = drawable.getGL().getGL2();

			update(gl);

			return true;	// Let animator take care of updating the display
		}

		public abstract void	update(GL2 gl);
	}

	// Convenience class to simplify updates in cases in which the input is a
	// single point in view coordinates (integers/pixels).
	private abstract class ViewPointUpdater extends BasicUpdater
	{
		private final Point	q;

		public ViewPointUpdater(Point q)
		{
			this.q = q;
		}

		public final void	update(GL2 gl)
		{
			int		h = view.getHeight();
			double[]	p = Utilities.mapViewToScene(gl, q.x, h - q.y, 0.0);

			update(p);
		}

		public abstract void	update(double[] p);
	}

	public static final float[][] CLEAR_COLORS = {
		{0.0f,0.0f,0.0f}, // black
		{1.0f,1.0f,1.0f},  // white
		{0f,0f,1f}, // blue
		{0f,1f,1f}, // yellow
		{1f,0f,0f}, // red
		{1f,1f,0f} // yellow
	};
}

//******************************************************************************
