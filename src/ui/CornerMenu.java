package ui;

import graphic.Images;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import ui.widget.Widget;

/**
 * Corner menu who encapsulates the 3 main game dialogs.
 * It is not supposed to be a general menu widget, but a very specific one.
 */
public class CornerMenu extends Widget
{
	private ToggleButton[] buttons;
	private EconomyDialog econDialog;
	private MainDialog techDialog;
	private MainDialog politicalDialog;

	public CornerMenu(Widget parent) throws SlickException
	{
		super(parent);
		this.setPosition(0, 0);

		buttons = new ToggleButton[3];
		buttons[0] = new ToggleButton(Images.ECONOMY_ICON, this);
		buttons[0].setPosition(57, 6);
		buttons[1] = new ToggleButton(Images.SCIENCE_ICON, this);
		buttons[1].setPosition(40, 38);
		buttons[2] = new ToggleButton(Images.POLITICAL_ICON, this);
		buttons[2].setPosition(6, 55);

		econDialog = new EconomyDialog(this);
		techDialog = new MainDialog(this);
		politicalDialog = new MainDialog(this);

		buttons[0].setListener((on) -> BaseDialog.setCurrent(econDialog, on));
		buttons[1].setListener((toggle) -> BaseDialog.setCurrent(techDialog, toggle));
		buttons[2].setListener((toggle) -> BaseDialog.setCurrent(politicalDialog, toggle));

		BaseDialog.registerForSelectionChanges(dialog ->
		{
			buttons[0].set(dialog == econDialog);
			buttons[1].set(dialog == techDialog);
			buttons[2].set(dialog == politicalDialog);
		});
	}

	public boolean keyPressed(int key)
	{
		switch(key)
		{
			case Input.KEY_Q: BaseDialog.setCurrent(politicalDialog, true); return true;
			case Input.KEY_W: BaseDialog.setCurrent(techDialog, true); return true;
			case Input.KEY_E: BaseDialog.setCurrent(econDialog, true); return true;
			default: return false;
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g)
	{
		Images.CORNER_MENU.get().draw(1, 1);

		for(ToggleButton b: buttons)
			b.render(gc, g);
	}
}
