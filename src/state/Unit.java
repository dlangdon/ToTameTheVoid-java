package state;

import java.util.HashMap;

import org.newdawn.slick.Image;

public abstract class Unit implements Comparable<Unit>
{
// Statics ==========================================================================================================
	private static HashMap<String, Unit> all = new HashMap<String, Unit>();
	public static Unit fetchByName(String name)
	{
		return all.get(name);
	}
	
// Internals ==========================================================================================================
	private String name_;
	private Image image_;
	private float hitPoints_;
	private float cost_;
	
// Public Methods =====================================================================================================
	public Unit(String name, Image image, float cost)
	{
		super();
		this.name_ = name;
		this.image_ = image;
		this.hitPoints_ = 1.0f;
		this.cost_ = cost;
		all.put(name, this);
	}

	public String name()
	{
		return name_;
	}

	public Image image()
	{
		return image_;
	}

	public float hitPoints()
	{
		return hitPoints_;
	}
	
	public float cost()
	{
		return cost_;
	}
	
	@Override
	public int compareTo(Unit other)
	{
		int aux = type().compareTo(other.type());
		return (aux != 0) ? aux : name_.compareTo(other.name_);
	}

	public abstract String type();
}
