package com.github.dlangdon.ui;

import com.github.dlangdon.graphic.Render;

import org.newdawn.slick.*;

import com.github.dlangdon.empire.Economy;
import com.github.dlangdon.empire.Empire;

public class EconomyDialog
{
// Internals ==========================================================================================================
	private boolean visible;
	private Image background;

// Public Methods =====================================================================================================
	public EconomyDialog() throws SlickException
	{
		visible = false;
		background = new Image("dialog_base_complete.png");
	}

	public void render(GameContainer gc, Graphics g)
	{
		if(!visible)
			return;

		Empire e = Empire.getPlayerEmpire();
		Economy ec = e.getEconomy();

		// Background.
		background.draw(3, 3);
		//g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.8f));
		//g.fillRect(50, 50, 500, 500);


		// Policy
		int y = 20;
		int x = 50;
		Render.dialogTitle.drawString(x+350, (y+=220), "Policy:");
		Render.normal.drawString(x + 70, (y+=20), String.format("Limit growth to %d%% of production.", (int)(ec.growthPolicy()*100)));
		Render.normal.drawString(x + 70, (y+=15), String.format("For investments recovered before %d turns.", (ec.returnOfInvestmentLimit())));
		Render.normal.drawString(x + 70, (y+=15), (ec.isOnlyLocal() ? "Allow" : "Prohibit") + " spending of reserve to boost growth (at 50% extra cost)");

		// Income
		y += 50;
		Render.titles.drawString(x, y, "Last turn income:");
		y += 20;
		float totalIncome = 0.0f;
		for(int i=0; i<Economy.causes().size(); i++)
		{
			if(ec.movements()[i] > 0)
			{
				Render.normal.drawString(x + 20, y, Economy.causes().get(i));
				Render.normal.drawString(x + 220, y, String.format("$ %10d %s", (int)(ec.movements()[i]*10000.0f), ec.rejections()[i] ? "!" : ""));
				y += 15;
				totalIncome += ec.movements()[i];
			}
		}

		// Expenses
		y += 20;
		Render.titles.drawString(x, y, "Last turn expenses:");
		y += 20;
		float totalExpenses = 0.0f;
		for(int i=0; i<Economy.causes().size(); i++)
		{
			if(ec.movements()[i] <= 0)
			{
				Render.normal.drawString(x + 20, y, Economy.causes().get(i));
				Render.normal.drawString(x + 220, y, String.format("$ %10d %s", (int)(-ec.movements()[i]*10000.0f), ec.rejections()[i] ? "!" : ""));
				y += 15;
				totalExpenses += ec.movements()[i];
			}
		}

		// Totals
		Render.titles.drawString(x, (y+=20), "Totals:");
		Render.normal.drawString(x + 20, (y+=20), "Income");
		Render.normal.drawString(x + 220, y, String.format("$ %10d", (int)(totalIncome*10000.0f) ));
		Render.normal.drawString(x + 20, (y+=15), "Expenses");
		Render.normal.drawString(x + 220, y, String.format("$ %10d", (int)(totalExpenses*10000.0f)));
		Render.normal.drawString(x + 20, (y+=15), "To Imperial Reserve");
		Render.normal.drawString(x + 220, y, String.format("$ %10d", (int)((totalIncome + totalExpenses)*10000.0f)));
		Render.normal.drawString(x + 20, (y+=15), "Current Reserve");
		Render.normal.drawString(x + 220, y, String.format("$ %10d", (int)(ec.reserve()*10000.0f)));
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
}
