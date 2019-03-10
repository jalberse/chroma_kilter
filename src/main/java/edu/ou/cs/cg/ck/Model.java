package edu.ou.cs.cg.ck;

//import java.lang.*;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;
import com.jogamp.opengl.*;
import edu.ou.cs.cg.utilities.Utilities;

//******************************************************************************

/**
 * The <CODE>Model</CODE> class.
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
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
	private ArrayList<Point2D.Double>	points;	// Drawn polyline points
	private ArrayList<Point2D.Double> pointStars;
	private boolean					colorful;	// Show rainbow version?
	private int[][]					horizonColors;
	private int horizonColorIndex;
	private Point moonPos;
	private boolean flip;
	private int roofSplits;
	private int ballStart;
	private boolean drawBall;
	private int k;
	private int starStart;
	private boolean starDown;
	private boolean animatingStar;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public Model(View view)
	{
		this.view = view;

		pointStars = new ArrayList<Point2D.Double>();
		moonPos = new Point(94,720-94);
		flip = false;
		roofSplits = 3;
		ballStart = 0;
		drawBall = false;
		k = 0;
		starStart = 0;
		starDown = false;
		animatingStar = false;

		// Initialize user-adjustable variables (with reasonable default values
		origin = new Point2D.Double(0.0, 0.0);
		cursor = null;
		points = new ArrayList<Point2D.Double>();
		colorful = false;

		horizonColors = new int[5][3];
		horizonColors[0][0] = 128;
		horizonColors[0][1] = 112;
		horizonColors[0][2] = 80;
		horizonColors[1][0] = 256;
		horizonColors[1][1] = 256;
		horizonColors[1][2] = 256;
		horizonColors[2][0] = 247;
		horizonColors[2][1] = 146;
		horizonColors[2][2] = 126;
		horizonColors[3][0] = 118;
		horizonColors[3][1] = 62;
		horizonColors[3][2] = 183;
		horizonColors[4][0] = 224;
		horizonColors[4][1] = 114;
		horizonColors[4][2] = 185;

		horizonColorIndex = 0;
	}

	//**********************************************************************
	// Public Methods (Access Variables)
	//**********************************************************************

	public boolean getAnimatingStar(){
		return animatingStar;
	}

	public void setAnimatingStar(boolean v){
		animatingStar = v;
	}

	public int getStarStart(){
		return starStart;
	}
	public void setStarStart(int v){
		starStart = v;
	}

	public void setStarDown(boolean v){
		starDown = v;
	}
	public boolean getStarDown(){
		return starDown;
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

	public List<Point2D.Double> getStars(){
		return Collections.unmodifiableList(pointStars);
	}

	public List<Point2D.Double>	getPolyline()
	{
		return Collections.unmodifiableList(points);
	}

	public boolean	getColorful()
	{
		return colorful;
	}

	public Point getMoonPos(){
		return moonPos;
	}

	public boolean getFlip(){
		return flip;
	}

	public int[] getHorizonColor(){
		return horizonColors[horizonColorIndex];
	}

	public int getRoofSplits(){
		return roofSplits;
	}

	public boolean getDrawBall(){
		return drawBall;
	}

	public int getBallStart(){
		return ballStart;
	}

	public int getK(){
		return k;
	}

	//**********************************************************************
	// Public Methods (Modify Variables)
	//**********************************************************************

	public void setK(int v){
		k = v;
	}

	public void setDrawBall(boolean x){
		drawBall = x;
	}

	public void setBallStart(int v){
		ballStart = v;
	}

	public void setRoofSplits(int s){
		roofSplits = s;
	}

	public void flip(){
		flip = !flip;
	}

	public void setMoonPos(int x, int y){
		moonPos.move(x,y);
	}

	public void setHorizonColor(int i){
		horizonColorIndex = i;
	}

	public void	addPointStarInViewCoordinates(Point q)
	{
		view.getCanvas().invoke(false, new ViewPointUpdater(q) {
			public void	update(double[] p) {
				pointStars.add(new Point2D.Double(p[0], p[1]));
			}
		});
	}

	public void deleteOldestStar(){
		if (!pointStars.isEmpty()){
			pointStars.remove(0);
		}
	}

	public void deleteNewestStar(){
		if (!pointStars.isEmpty()){
			pointStars.remove(pointStars.size() - 1);
		}
	}

	public void	setOriginInSceneCoordinates(Point2D.Double q)
	{
		view.getCanvas().invoke(false, new BasicUpdater() {
			public void	update(GL2 gl) {
				origin = new Point2D.Double(q.x, q.y);
			}
		});;
	}

	public void	setOriginInViewCoordinates(Point q)
	{
		view.getCanvas().invoke(false, new ViewPointUpdater(q) {
			public void	update(double[] p) {
				origin = new Point2D.Double(p[0], p[1]);
			}
		});;
	}

	public void	setCursorInViewCoordinates(Point q)
	{
		view.getCanvas().invoke(false, new ViewPointUpdater(q) {
			public void	update(double[] p) {
				cursor = new Point2D.Double(p[0], p[1]);
			}
		});;
	}

	public void	turnCursorOff()
	{
		view.getCanvas().invoke(false, new BasicUpdater() {
			public void	update(GL2 gl) {
				cursor = null;
			}
		});;
	}

	public void	addPolylinePointInViewCoordinates(Point q)
	{
		view.getCanvas().invoke(false, new ViewPointUpdater(q) {
			public void	update(double[] p) {
				points.add(new Point2D.Double(p[0], p[1]));
			}
		});;
	}

	public void	clearPolyline()
	{
		view.getCanvas().invoke(false, new BasicUpdater() {
			public void	update(GL2 gl) {
				points.clear();
			}
		});;
	}

	public void	toggleColorful()
	{
		view.getCanvas().invoke(false, new BasicUpdater() {
			public void	update(GL2 gl) {
				colorful = !colorful;
			}
		});;
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
}

//******************************************************************************
