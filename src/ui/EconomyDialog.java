package ui;

import graphic.Render;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import state.Economy;
import state.Empire;

public class EconomyDialog
{
// Internals ==========================================================================================================	
	private boolean visible;
	
// Public Methods =====================================================================================================
	public EconomyDialog()
	{
		visible = false;
	}
	
	public void render(GameContainer gc, Graphics g)
	{
		if(!visible)
			return;
		
		Empire e = Empire.getPlayerEmpire();
		Economy ec = e.getEconomy();

		// Background.
		g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.8f));
		g.fillRect(50, 50, 500, 500);

		// Policy
		int y = 50;
		Render.titles.drawString(100, (y+=20), "Policy:");
		Render.normal.drawString(120, (y+=20), String.format("Limit growth to %d%% of production.", (int)(ec.growthPolicy()*100)));
		Render.normal.drawString(120, (y+=15), String.format("Only spend on infrastructure if costs are recovered before %d turns.", (ec.returnOfInvestmentLimit())));
		Render.normal.drawString(120, (y+=15), (ec.isOnlyLocal() ? "Allow" : "Prohibit") + " spending of reserve to boost growth (at 50% extra cost)");

		// Income
		y += 20;
		Render.titles.drawString(100, y, "Last turn income:");
		y += 20;
		float totalIncome = 0.0f;
		for(int i=0; i<Economy.causes().size(); i++)
		{
			if(ec.movements()[i] > 0)
			{
				Render.normal.drawString(120, y, Economy.causes().get(i));
				Render.normal.drawString(320, y, String.format("$ %10d %s", (int)(ec.movements()[i]*10000.0f), ec.rejections()[i] ? "!" : ""));
				y += 15;
				totalIncome += ec.movements()[i];
			}
		}
		
		// Expenses
		y += 20;
		Render.titles.drawString(100, y, "Last turn expenses:");
		y += 20;
		float totalExpenses = 0.0f;
		for(int i=0; i<Economy.causes().size(); i++)
		{
			if(ec.movements()[i] <= 0)
			{
				Render.normal.drawString(120, y, Economy.causes().get(i));
				Render.normal.drawString(320, y, String.format("$ %10d %s", (int)(-ec.movements()[i]*10000.0f), ec.rejections()[i] ? "!" : ""));
				y += 15;
				totalExpenses += ec.movements()[i];
			}
		}
		
		// Totals
		Render.titles.drawString(100, (y+=20), "Totals:");
		Render.normal.drawString(120, (y+=20), "Income");
		Render.normal.drawString(320, y, String.format("$ %10d", (int)(totalIncome*10000.0f) ));
		Render.normal.drawString(120, (y+=15), "Expenses");
		Render.normal.drawString(320, y, String.format("$ %10d", (int)(totalExpenses*10000.0f)));
		Render.normal.drawString(120, (y+=15), "To Imperial Reserve");
		Render.normal.drawString(320, y, String.format("$ %10d", (int)((totalIncome + totalExpenses)*10000.0f)));
		Render.normal.drawString(120, (y+=15), "Current Reserve");
		Render.normal.drawString(320, y, String.format("$ %10d", (int)(ec.reserve()*10000.0f)));
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
