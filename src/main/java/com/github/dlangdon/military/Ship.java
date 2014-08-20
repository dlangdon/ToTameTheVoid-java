/**
 *
 */
package com.github.dlangdon.military;

import org.newdawn.slick.Image;

import com.github.dlangdon.state.Unit;

/**
 * @author Daniel Langdon
 */
public class Ship extends Unit
{
	/**
	 * @param name_
	 * @param image_
	 */
	public Ship(String name, Image image, float cost)
	{
		super(name, image, cost);
	}

	/* (non-Javadoc)
	 * @see com.github.dlangdon.com.github.dlangdon.state.Unit#type()
	 */
	@Override
	public String type()
	{
		return "SHIP";
	}
}
