package graphic;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import state.Economy;
import state.Empire;
import state.Universe;

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
		
		Empire e = Universe.instance().getPlayerEmpire();
		Economy ec = e.getEconomy_();

		// Background.
		g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.8f));
		g.fillRect(50, 50, 500, 500);

		// Policy
		int y = 50;
		Render.titles.drawString(100, (y+=20), "Policy:");
		Render.normal.drawString(120, (y+=20), String.format("Limit growth to %d of production.", (int)(ec.growthPolicy()*100)));
		Render.normal.drawString(120, (y+=15), String.format("Only spend on infrastructure if costs are recovered before %d turns.", (ec.returnOfInvestmentLimit())));
		Render.normal.drawString(120, (y+=15), (ec.isOnlyLocal() ? "Allow" : "Prohibit") + " spending of reserve to boost growth (at 50% extra cost)");

		// Income
		y += 20;
		Render.titles.drawString(100, y, "Last turn income:");
		y += 20;
		float totalIncome = 0.0f;
		for(Economy.Movement mov : ec.movements())
		{
			if(mov.amount > 0)
			{
				Render.normal.drawString(120, y, mov.cause);
				Render.normal.drawString(320, y, String.format("$ %10d %s", (int)(mov.amount*100), mov.rejections ? "!" : ""));
				y += 15;
				totalIncome += mov.amount;
			}
		}
		
		// Expenses
		y += 20;
		Render.titles.drawString(100, y, "Last turn expenses:");
		y += 20;
		float totalExpenses = 0.0f;
		for(Economy.Movement mov : ec.movements())
		{
			if(mov.amount < 0)
			{
				Render.normal.drawString(120, y, mov.cause);
				Render.normal.drawString(320, y, String.format("$ %10d %s", (int)(-mov.amount*100), mov.rejections ? "!" : ""));
				y += 15;
				totalExpenses += mov.amount;
			}
		}
		
		// Totals
		Render.titles.drawString(100, (y+=20), "Last turn expenses:");
		Render.normal.drawString(120, (y+=20), "Total Income");
		Render.normal.drawString(320, y, String.format("$ %10d", (int)(totalIncome) ));
		Render.normal.drawString(120, (y+=15), "Total Expenses");
		Render.normal.drawString(320, y, String.format("$ %10d", (int)(totalExpenses)));
		Render.normal.drawString(120, (y+=15), "To Imperial Reserve");
		Render.normal.drawString(320, y, String.format("$ %10d", (int)(totalIncome - totalExpenses)));
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
