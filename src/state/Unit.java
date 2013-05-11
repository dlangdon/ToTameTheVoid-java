package state;

import org.newdawn.slick.Image;

public class Unit implements Comparable<Unit>
{
// Internals ==========================================================================================================
	private String name_;
	private Image image_;
	private float hitPoints_;
	private float cost_;
	
// Public Methods =====================================================================================================
	public Unit(String name_, Image image_)
	{
		super();
		this.name_ = name_;
		this.image_ = image_;
		this.hitPoints_ = 1.0f;
		this.cost_ = 1e-4f;
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
		return name_.compareTo(other.name_);
	}
}
