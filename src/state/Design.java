package state;

import org.newdawn.slick.Image;

public class Design implements Comparable<Design>
{
// Internals ==========================================================================================================
	private String name_;
	private Image image_;
	private Empire owner;
	
// Public Methods =====================================================================================================
	public Design(String name_, Image image_)
	{
		super();
		this.name_ = name_;
		this.image_ = image_;
	}

	public String name()
	{
		return name_;
	}

	public Image image()
	{
		return image_;
	}

	@Override
	public int compareTo(Design other)
	{
		return name_.compareTo(other.name_);
	}
}
