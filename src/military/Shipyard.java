/**
 * 
 */
package military;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import state.BaseIHQSlot;
import state.BaseIHQSlot.IHQSlotFactory;
import state.IHQSlot;
import state.Unit;

/**
 * A slot that builds military units.
 * @author Daniel Langdon
 */
public class Shipyard extends IHQSlot
{
	private static Image icon_;
	private static List<Unit> availableUnits_;
	
	public static void init()
	{
		availableUnits_ = new ArrayList<Unit>();
		
		try
		{
			// Add units.
			// TODO For now, just a hardcoded list of units.
			availableUnits_.add(new Ship("Figther", new Image("resources/ship2.png")));
			availableUnits_.add(new Ship("Colony Ship", new Image("resources/ship1.png")));
			
			// Load icon.
			icon_ = new Image("resources/ironFist.png");
		}
		catch (SlickException e)
		{
			System.out.println("Problem initializing resources.");
			e.printStackTrace();
		}

		// Register a factory for this sort of slots.
		BaseIHQSlot.registerFactory(new IHQSlotFactory()
		{
			@Override
			public Image getIcon()
			{
				return icon_;
			}
			
			@Override
			public IHQSlot create()
			{
				return new Shipyard();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see state.IHQSlot#getType()
	 */
	@Override
	public String getType()
	{
		return "Shipyard";
	}

	/* (non-Javadoc)
	 * @see state.IHQSlot#availableUnits()
	 */
	@Override
	public List<Unit> availableUnits()
	{
		return availableUnits_;
	}


	/* (non-Javadoc)
	 * @see state.IHQSlot#icon()
	 */
	@Override
	public Image icon()
	{
		return icon_;
	}
}
