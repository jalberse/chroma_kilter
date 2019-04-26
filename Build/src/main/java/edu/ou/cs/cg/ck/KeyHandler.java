package edu.ou.cs.cg.ck;

//import java.lang.*;
import java.awt.Component;
import java.awt.event.*;
import edu.ou.cs.cg.utilities.Utilities;

//******************************************************************************

/**
 * The <CODE>KeyHandler</CODE> class.<P>
 * 
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class KeyHandler extends KeyAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View		view;
	private final Model	    model;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public KeyHandler(View view, Model model)
	{
		this.view = view;
		this.model = model;

		Component	component = view.getCanvas();

		component.addKeyListener(this);
	}

	//**********************************************************************
	// Override Methods (KeyListener)
	//**********************************************************************

	public void		keyPressed(KeyEvent e)
	{
		boolean	b = Utilities.isShiftDown(e);

		switch (e.getKeyCode())
		{
			// pause rotation
			case KeyEvent.VK_P:
				model.setIsRotating(!model.isRotating());
				break;
			// Change rotation speed
			case KeyEvent.VK_I:
				model.setRotationSpeed(model.getRotationSpeed() - .05f);
				break;
			case KeyEvent.VK_O:
				model.setRotationSpeed(model.getRotationSpeed() + .05f);
				break;
			// Increase/decrease distance of object
			case KeyEvent.VK_L:
				if (model.getDistance() > -20.0f){
					model.setDistance(model.getDistance() - .1f);
				}
				break;
			case KeyEvent.VK_K:
				if (model.getDistance() < -5.0f){
					model.setDistance(model.getDistance() + .1f);
				}
				break;
			case KeyEvent.VK_NUMPAD0:
			case KeyEvent.VK_0:
				model.setGeomID(0);
				break;
			case KeyEvent.VK_NUMPAD1:
			case KeyEvent.VK_1:
				model.setGeomID(1);
				break;
			case KeyEvent.VK_NUMPAD2:
			case KeyEvent.VK_2:
				model.setGeomID(2);
				break;
			case KeyEvent.VK_NUMPAD3:
			case KeyEvent.VK_3:
				model.setGeomID(3);
				break;
			case KeyEvent.VK_NUMPAD4:
			case KeyEvent.VK_4:
				model.setGeomID(4);
				break;
			case KeyEvent.VK_NUMPAD5:
			case KeyEvent.VK_5:
				model.setGeomID(5);
				break;
			case KeyEvent.VK_NUMPAD6:
			case KeyEvent.VK_6:
				model.setGeomID(6);
				break;
			case KeyEvent.VK_NUMPAD7:
			case KeyEvent.VK_7:
				model.setGeomID(7);
				break;

			// Increase/decrease strength of chromatic effect
			case KeyEvent.VK_UP:
				if (b)
				{
					model.setChromAlpha(model.getChromAlpha() + 0.02f);
				}
				else model.setChromMagnitude(model.getChromMagnitude() + .01f);
				break;
			case KeyEvent.VK_DOWN:
				if (b)
				{
					model.setChromAlpha(model.getChromAlpha() - 0.02f);
				}
				else model.setChromMagnitude(model.getChromMagnitude() - .01f);
				break;
				
			// Rotate axis of aberration
			case KeyEvent.VK_LEFT:
				model.setChromAngle(model.getChromAngle() + 0.1f);
				break;
			case KeyEvent.VK_RIGHT:
				model.setChromAngle(model.getChromAngle() - 0.1f);
				break;
				
			// Change distance of focal plane from the screen.
			case KeyEvent.VK_SLASH:
				model.setZFocalPlane(model.getZFocalPlane() + 0.1f);
				break;
			case KeyEvent.VK_PERIOD:
				model.setZFocalPlane(model.getZFocalPlane() - 0.1f);
				break;
				
			// Set clear color (background color)
			case KeyEvent.VK_MINUS:
				if (b)
				{
					model.setObjectColorPrevious();
				}
				else model.setClearColorPrevious();
				break;
			case KeyEvent.VK_EQUALS:
				if (b)
				{
					model.setObjectColorNext();
				}
				else model.setClearColorNext();
				break;
			// Toggle render modes
			case KeyEvent.VK_Z:
				// Use a stencil on original object - keep local colors
				model.setRenderMode("nondestructive");
				break;
			case KeyEvent.VK_X:
				// Simply draw with offset
				model.setRenderMode("basic");
				break;
			case KeyEvent.VK_C:
				// Do not check depth on chromAb
				model.setRenderMode("nodepth");
				break;
			// Toggle wireframe
			case KeyEvent.VK_W:
				// Do not check depth on chromAb
				model.setWireframe(!model.isWireframe());
				break;
		}
	}
}

//******************************************************************************
