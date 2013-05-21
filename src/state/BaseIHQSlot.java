/**
 * 
 */
package state;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * A very particular class of IHQSlot used to build other IHWSlots (present their icons, descriptions, etc).
 * Modules can register special factories for that. 
 * @author Daniel Langdon
 */
public class BaseIHQSlot extends IHQSlot
{
	public interface IHQSlotFactory
	{
		public Image getIcon();
		public IHQSlot create();
	}

	protected static List<IHQSlotFactory> factories;
	protected static Image icon_;

	/**
	 * Register a type of slot factory. The IHQ can hold any kind of slot that can create units, but needs to know what slots can be created.
	 * @param factory
	 */
	public static void registerFactory(IHQSlotFactory factory)
	{
		factories.add(factory);
	}
	
	/**
	 * Global initialization phase to produce module constants, registration with other modules, resource loading, etc.
	 */
	public static void init()
	{
		factories = new ArrayList<IHQSlotFactory>();
		try
		{
			icon_ = new Image("resources/construction.png");
		}
		catch (SlickException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see state.IHQSlot#getType()
	 */
	@Override
	public String getType()
	{
		return "Base";
	}

	/* (non-Javadoc)
	 * @see state.IHQSlot#availableUnits()
	 */
	@Override
	public List<Unit> availableUnits()
	{
		return null;
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
