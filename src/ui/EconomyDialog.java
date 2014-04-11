package ui;

import graphic.Render;

import org.newdawn.slick.*;

import empire.Economy;
import empire.Empire;

public class EconomyDialog extends MainDialog
{
// Public Methods =====================================================================================================
	public EconomyDialog() throws SlickException
	{
		super();
		setSize(750, 700);
	}

	public void render(GameContainer gc, Graphics g)
	{
		// Draw background
		super.render(gc, g);

		Empire e = Empire.getPlayerEmpire();
		Economy ec = e.getEconomy();

		float x = x() + 40;
		float y = super.drawTitle("Economy");

		y = super.drawSubtitle(0, y, "Policy");
		y = super.drawText(x, y, String.format("Limit growth to %d%% of production.", (int)(ec.growthPolicy()*100)));
		y = super.drawText(x, y, String.format("For investments recovered before %d turns.", (ec.returnOfInvestmentLimit())));
		y = super.drawText(x, y, (ec.isOnlyLocal() ? "Allow" : "Prohibit") + " spending of reserve to boost growth (at 50% extra cost)");

		y = super.drawSubtitle(0, y, "Income");
		y = super.drawSubtitle(0, y+71, "Expenses");


//		Render.dialogText.drawString(x + 70, (y+=20), String.format("Limit growth to %d%% of production.", (int)(ec.growthPolicy()*100)), textColor);
//		Render.normal.drawString(x + 70, (y+=15), String.format("For investments recovered before %d turns.", (ec.returnOfInvestmentLimit())));
//		Render.normal.drawString(x + 70, (y+=15), (ec.isOnlyLocal() ? "Allow" : "Prohibit") + " spending of reserve to boost growth (at 50% extra cost)");

		y+=100;

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
}
