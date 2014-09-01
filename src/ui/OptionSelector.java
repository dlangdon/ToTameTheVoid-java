package ui;

import graphic.Images;
import graphic.Render;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import ui.widget.Widget;
import ui.widget.EventListener;

import java.util.Arrays;

public class OptionSelector<T> extends Widget
{
	String[] options;
	T[] values;
	int current;
	EventListener<T> listener = (value) -> {};

	OptionSelector(Widget parent)
	{
		super(parent);
	}

	public void setOptions(String[] options, T[] values) throws RuntimeException
	{
		if(options.length != values.length)
			throw new RuntimeException("Options need corresponding values.");

		this.options = options;
		this.values = values;
		select(0);
	}

	public void select(int optionIndex)
	{
		current = optionIndex;
		listener.onEvent(values[current]);
	}

    public void selectValue(T value)
    {
        current = Arrays.asList(values).indexOf(value);
        listener.onEvent(values[current]);
    }


    public void setListener(EventListener<T> listener)
	{
		this.listener = listener;
	}

	@Override
	public void render(GameContainer gc, Graphics g)
	{
		// Render the option
		float textWidth = Render.dialogText.getWidth(options[current]);

		int iconWidth = Images.LEFT_ARROW.get().getWidth();
		Images.LEFT_ARROW.get().draw(x(), y()+3, Render.highlightColor);
		Render.dialogText.drawString(x() + iconWidth + 4, y(), options[current], Render.highlightColor);
		Images.RIGHT_ARROW.get().draw(x() + iconWidth + textWidth + 4, y() + 3, Render.highlightColor);

		// Remember the size, in case someone clicks.
		this.setSize(textWidth + 2*iconWidth + 4, Render.dialogText.getLineHeight());
	}

	@Override
	public void mouseDown(int button, int delta)
	{
		if(delta != 0)
			return;

		float y = Mouse.getY();
		float x = Mouse.getX() - x();
		if(options == null || x < 0 || x > width() || y < y() && y > y() + height())
			return;

		// We move the option right unless we click left arrow or in the center with the right mouse button.
		int iconWidth = Images.LEFT_ARROW.get().getWidth();
		if( x <= iconWidth || (x <= width() - iconWidth && button == 1))
			current = (current + options.length -1) % options.length;
		else
			current = (current +1) % options.length;

		listener.onEvent(values[current]);
		return;
	}
}
