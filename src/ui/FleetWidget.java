package ui;

import graphic.Camera;
import graphic.Render;

import java.util.HashMap;
import java.util.Map.Entry;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import state.Fleet;
import state.Star;
import state.Unit;
import state.UnitStack;

public class FleetWidget extends IndexedDialog
{
	/**
	 * An internal class in order to keep count of unit selections inside a fleet.
	 */
	private class StackSelection
	{
		float selected;
		int max;
		Unit design;
	}
	
// Internals ==========================================================================================================	
	private Fleet fleet;
	private Image[] backgrounds;
	private int[][] bckDeltas;
	private StackSelection[] cache;
	private int numSteps;

// Public Methods =====================================================================================================
	public FleetWidget() throws SlickException
	{
		this.fleet = null;
		this.numSteps = 6;
		backgrounds = new Image[] 
			{
				new Image("resources/fleetBase.png"),
				new Image("resources/fleetExt1.png"),
				new Image("resources/fleetExt2.png"),
				new Image("resources/fleetExt3.png"),
				new Image("resources/fleetExt4.png")
			};
		
		bckDeltas = new int[][]
			{
				{	-74,	-60,	-121, -168,	-108 },
				{	-119,	-121,	-105,	-129,	-169 }
			};
	}

	/**
	 * Sets the task fleet to be displayed by this
	 * @param fleet
	 */
	public void showFleet(Fleet fleet)
	{
		this.fleet = fleet;
		
		// Reset the selected values of the ships for this fleet to their maximum value.
		if(fleet != null)
		{
			cache = new StackSelection[fleet.stacks().size()];
			int i=0;
			for(Entry<Unit, UnitStack> entry : fleet.stacks().entrySet())
			{
				cache[i] = new StackSelection();
				cache[i].design = entry.getKey();
				cache[i].max = entry.getValue().quantity();
				cache[i].selected = cache[i].max;
				i++;
			}
		}
	}
	
	public Fleet selectedfleet()
	{
		return fleet;
	}

	public void render(GameContainer gc, Graphics g)
	{
		// If no star is being displayed, do nothing.
		if(fleet == null)
			return;
		
		// Make it so drawing stars is always done in local coordinates.
		Camera.instance().pushLocalTransformation(g, fleet.position());

		// Decide how many segments to show.
		int numStacks = fleet.stacks().size();
		for(int i=0; i<=(numStacks-1)/4 && i<5; i++)
			backgrounds[i].draw(bckDeltas[0][i], bckDeltas[1][i]);

		// Paint the icons and numbers.
		for(int i=0; i<cache.length; i++)
		{
			// Draw the icon.
			Vector2f pos = indexToCoord(i);
			cache[i].design.image().draw(pos.x-15, pos.y-15);
			
			// Calculate location and draw the count for the stack.
			g.setColor(Color.orange);
			float length = pos.length();
			String number = Integer.toString((int)cache[i].selected);
			pos.normalise().scale(length + 10.0f);
			g.fillRect(
						pos.x - Render.normal.getWidth(number)/2,
						pos.y - Render.normal.getHeight()/2,
						Render.normal.getWidth(number),
						Render.normal.getHeight());
			Render.normal.drawString(
						pos.x - Render.normal.getWidth(number)/2,
						pos.y - Render.normal.getHeight()/2,
						number, Color.black);
			
			// Check if we also display the local information.
			if(hoverIndex == i)
			{
				Render.titles.drawString(120, -100, cache[i].design.name());
			}
		}
		
		g.popTransform();
	}

	/* (non-Javadoc)
	 * @see ui.IndexedDialog#indexToCoord(int)
	 */
	@Override
	protected Vector2f indexToCoord(int index)
	{
		// Determine first 12 segments.
		if(index < 12)
		{
			float angle = index*15.0f;
			if(index%2 == 0)
				angle = -angle - 15.0f;
			
			return new Vector2f(angle).scale(98.5f);
		}
		else
		{
			float angle = -(index-12)*10.0f - 180.0f;
			if(index%2 == 1)
				angle = -angle + 10.0f;

			return new Vector2f(angle).scale(145.5f);
		}
	}
	
	/* (non-Javadoc)
	 * @see ui.IndexedDialog#location()
	 */
	@Override
	public Vector2f location()
	{
		return fleet == null ? null : fleet.position();
	}

	/* (non-Javadoc)
	 * @see ui.IndexedDialog#coordToIndex(org.newdawn.slick.geom.Vector2f)
	 */
	@Override
	protected int coordToIndex(Vector2f vector)
	{
		double angle = vector.getTheta();
		double radius = vector.length();
		
		int index = NO_INDEX;
		if(54 < radius && radius < 74 )
		{
			// Buttons
			if(angle >= 310)
				index = -1 - (int)((angle - 310.0) / 20.0);
			
			if(angle <= 50)
				index = -1 - (int)((angle + 50) / 20.0);
			
			// Invalid index for decoration
			if(angle >= 130 && angle <= 230)
				index = -6;
		}
		if(76 < radius && radius < 121 )
		{
			// First circle, all of it works
			int aux = (int)(360 - angle) / 30;
			if(aux < 6)
				index = aux*2;
			else
				index = 23 - aux*2; 
		}
		else if(123 < radius && radius < 168 )
		{
			// Second circle
			int aux = (int)(angle - 10) / 20;
			if(aux > 2)
			{
				if(aux < 9)
					index = 28 - aux*2;
				else if(aux < 14)
					index = aux*2 - 5;
			}
		}
		
		// Check that the index is actually being shown.
		int numStacks = (int) (Math.ceil((fleet.stacks().size())/4.0)) *4;
		return (index < numStacks) ? index : NO_INDEX;
	}

	/*
	 * (non-Javadoc)
	 * @see ui.IndexedDialog#mouseClick(int, int)
	 */
	@Override 
	public void mouseClick(int button, int delta)
	{
		// Check if visible.
		if(fleet == null || hoverIndex <= NO_INDEX || delta != 0 || hoverIndex >= cache.length)
			return;
		
		// Process if it's a button.
		if(hoverIndex == -1)
			clearSelection();
		else if(hoverIndex == -2)
			invertSelection();
		else if(hoverIndex == -3)
			disbandSelection();
		else if(hoverIndex == -4)
			leaveInOrbit();
		else if(hoverIndex == -5)
			splitSelection(false);
		
		// Process if its a stack.
		else if(hoverIndex >= 0)
		{
			// This calculation may seem rather convoluted and the % operator may sound like a better idea, but this behavior is rather rare. 
			// If we are close to the maximum, we want to go to 12 before going pass 12. Example to avoid: 0, 3, 6, 9, 12, 2, 5, 8, 11...
			float step = Math.max(1.0f * cache[hoverIndex].max / numSteps, 1.0f);
			if(button == 0)
			{
				if(cache[hoverIndex].selected == cache[hoverIndex].max)
					cache[hoverIndex].selected = 0;
				else
					cache[hoverIndex].selected = Math.min(cache[hoverIndex].selected + step, cache[hoverIndex].max);
			}
			else if(button == 1)
			{
				if(cache[hoverIndex].selected < 1.0f)
					cache[hoverIndex].selected = cache[hoverIndex].max;
				else
					cache[hoverIndex].selected = Math.max(cache[hoverIndex].selected - step, 0.0f);
			}
			System.out.println(cache[hoverIndex].selected);
		}
		
		return;
	}

	/**
	 * Called when a star is clicked, potentially signaling that the route for a fleet needs to change.
	 */
	public void starClick(int button, Star s)
	{
		// Case 1: a click while in orbit and with a selection in place need to be split.
		if(fleet.orbiting() != null)
		{
			Fleet newFleet = splitSelection(true);
			if(newFleet != null)
				showFleet(newFleet);
		}
		
		// Now in every case, try to append or remove from the fleet's route.
		if(button == 0)
			fleet.addToRoute(s);
		else
			fleet.removeFromRoute(s);
	}
	
	/**
	 * Sets the selection to be empty, fo all unit types in the fleet.
	 */
	private void clearSelection()
	{
		for(StackSelection ss : cache)
			ss.selected = 0;
	}
	
	/**
	 * Sets the selection to be the complement of the current selection. For instance, if 2 ships were selected out of 5, the new selection is 3 ships.
	 */
	private void invertSelection()
	{
		for(StackSelection ss : cache)
			ss.selected = ss.max - ss.selected;
	}
	
	/**
	 * Scraps the ships currently selected, so they stop requiring maintenance.
	 */
	private void disbandSelection()
	{
		for(StackSelection ss : cache)
			fleet.addUnits(ss.design, (int)-ss.selected);
		clearSelection();
	}

	/**
	 * Leaves the selection in orbit.
	 */
	private void leaveInOrbit()
	{
		// Find the fleet where the ships would be deposited.
		Fleet toJoin = null;
		for(Fleet f: fleet.orbiting().getFleetsInOrbit())
			if(f != fleet && f.owner() == fleet.owner() && !f.isAutoMerge())
			{
				toJoin = f;
				break;
			}
			
		// Split this fleet and merge to that one.
		Fleet aux = this.splitSelection(true);
		if(toJoin != null && aux != null)
			toJoin.mergeIn(aux);
	}
	
	private Fleet splitSelection(boolean autoMerge)
	{
		// Check if a split can be done.
		if(fleet.orbiting() == null)
			return null;	// Can't split in transit.
		
		// Collect a map of selections.
		HashMap<Unit, Integer> split = new HashMap<Unit, Integer>();
		boolean everything = true;
		for(StackSelection s : cache)
		{
			if(s.selected > 0)
				split.put(s.design, (int)s.selected);
			
			if(s.selected != s.max)
				everything = false;
		}
		
		// A full split can't be made.
		return everything ? null : fleet.split(split);
	}
}
