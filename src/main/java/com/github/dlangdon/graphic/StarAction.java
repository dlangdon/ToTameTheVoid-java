/**
 *
 */
package com.github.dlangdon.graphic;

import org.newdawn.slick.Image;

/**
 * @author Daniel Langdon
 */
public interface StarAction
{
	/**
	 * @return The icon to use if this action is going to be added to an interface.
	 */
	public Image icon();

	/**
	 * @return An index that determines where this icon is put on the interface. The idea is that actions appear on the same location, independently of what is currently available.
	 */
	public int slot();

	/**
	 * Performs whatever action is necessary.
	 */
	public void runAction();
}
