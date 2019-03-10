package edu.ou.cs.cg.ck;

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import edu.ou.cs.cg.utilities.Utilities;

public final class MouseHandler extends MouseAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View		view;
	private final Model	model;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public MouseHandler(View view, Model model)
	{
		this.view = view;
		this.model = model;

		Component	component = view.getCanvas();

		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
	}

	//**********************************************************************
	// Override Methods (MouseListener)
	//**********************************************************************

	public void		mouseClicked(MouseEvent e)
	{
		
	}

	public void		mouseEntered(MouseEvent e)
	{

	}

	public void		mouseExited(MouseEvent e)
	{

	}

	public void		mousePressed(MouseEvent e)
	{

	}

	public void		mouseReleased(MouseEvent e)
	{

	}

	//**********************************************************************
	// Override Methods (MouseMotionListener)
	//**********************************************************************

	public void		mouseDragged(MouseEvent e)
	{

	}

	public void		mouseMoved(MouseEvent e)
	{

	}

	//**********************************************************************
	// Override Methods (MouseWheelListener)
	//**********************************************************************

	public void		mouseWheelMoved(MouseWheelEvent e)
	{
		
	}
}

