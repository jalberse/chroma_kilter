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
		double			a = (Utilities.isShiftDown(e) ? 0.01 : 0.1);
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
			// Increase/decrease strength of chromatic effect
			case KeyEvent.VK_UP:
				model.setChromMagnitude(model.getChromMagnitude() + .01f);
				break;
			case KeyEvent.VK_DOWN:
				model.setChromMagnitude(model.getChromMagnitude() - .01f);
				break;
			// Set clear color (background color)
			case KeyEvent.VK_LEFT:
				model.setClearColorPrevious();
				break;
			case KeyEvent.VK_RIGHT:
				model.setClearColorNext();
				break;
		}
	}
}

//******************************************************************************
