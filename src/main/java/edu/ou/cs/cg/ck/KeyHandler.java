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
			case KeyEvent.VK_P:
				model.setIsRotating(!model.isRotating());
				break;
			case KeyEvent.VK_I:
				model.setRotationSpeed(model.getRotationSpeed() - .05f);
				break;
			case KeyEvent.VK_O:
				model.setRotationSpeed(model.getRotationSpeed() + .05f);
				break;
		}
	}
}

//******************************************************************************
