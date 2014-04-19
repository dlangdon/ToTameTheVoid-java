package ui;

import graphic.Images;
import graphic.Render;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import ui.widget.EventListener;
import ui.widget.Widget;

public class ToggleButton extends Widget
{
	private boolean on;
	private Images icon;
	private EventListener<Boolean> listener;

	public ToggleButton(Images icon, Widget parent)
	{
		super(parent);
		this.icon = icon;
		this.setSize(icon.get().getWidth(), icon.get().getHeight());
	}

	public boolean isOn()
	{
		return on;
	}

	public void setListener(EventListener<Boolean> listener)
	{
		this.listener = listener;
	}

	@Override
	public void render(GameContainer gc, Graphics g)
	{
		Color color = Render.baseColor;
		if(on)
			color = Render.selectColor;
		if(underMouse() == this)
			color = Render.highlightColor;

		icon.get().draw(x(), y(), color);
	}

	@Override
	public void mouseDown(int button, int delta)
	{
		if(delta == 0)
			listener.onEvent(on);
	}

	public void set(boolean on)
	{
		this.on = on;
	}
}
