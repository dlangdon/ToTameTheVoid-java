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
		setSize(600, 750);
	}

	public void render(GameContainer gc, Graphics g)
	{
		// Draw background
		super.render(gc, g);

		Empire e = Empire.getPlayerEmpire();
		Economy ec = e.getEconomy();

		float leftX = 0;
		float leftY = super.drawTitle("Economy");
		float numberGap = 280;

		leftY = super.drawSubtitle(0, leftY, "Policy");
		leftY = super.drawText(leftX, leftY, String.format("Limit growth to %d%% of production.", (int)(ec.growthPolicy()*100)));
		leftY = super.drawText(leftX, leftY, String.format("For investments recovered before %d turns.", (ec.returnOfInvestmentLimit())));
		leftY = super.drawText(leftX, leftY, (ec.isOnlyLocal() ? "Allow" : "Prohibit") + " spending of reserve to boost growth (at 50% extra cost)");
		float rightY = leftY;

		leftY = super.drawSubtitle(0, leftY, "Income");
		float totalIncome = 0.0f;
		for(int i=0; i<Economy.causes().size(); i++)
		{
			if(ec.movements()[i] > 0)
			{
				super.drawText(leftX, leftY, Economy.causes().get(i) + (ec.rejections()[i] ? " (!)" : ""));
				leftY = super.drawText(leftX+numberGap, leftY, String.format("%d", (int)(ec.movements()[i]*10000.0f)), true, false);
				totalIncome += ec.movements()[i];
			}
		}

		// Expenses
		leftY = super.drawSubtitle(0, leftY, "Expenses");
		float totalExpenses = 0.0f;
		for(int i=0; i<Economy.causes().size(); i++)
		{
			if(ec.movements()[i] <= 0)
			{
				super.drawText(leftX, leftY, Economy.causes().get(i) + (ec.rejections()[i] ? " (!)" : ""));
				leftY = super.drawText(leftX+numberGap, leftY, String.format("%d", (int)(-ec.movements()[i]*10000.0f)), true, false);
				totalExpenses += ec.movements()[i];
			}
		}

		// Resources
		float rightX = leftX + 300;
		rightY = super.drawSubtitle(rightX, rightY, "Resources");
		rightY = super.drawText(rightX, rightY, "You control no");
		rightY = super.drawText(rightX, rightY, "strategic resources.");

		// Totals
		rightY = super.drawSubtitle(rightX, rightY, "Totals");
		super.drawText(rightX, rightY, "Income");
		rightY = super.drawText(rightX + numberGap, rightY, String.format("%d", (int)(totalIncome*10000.0f)), true, false);
		super.drawText(rightX, rightY, "Expenses");
		rightY = super.drawText(rightX + numberGap, rightY, String.format("%d", (int)(totalExpenses*10000.0f)), true, false);
		super.drawText(rightX, rightY, "Net");
		rightY = super.drawText(rightX + numberGap, rightY, String.format("%d", (int)((totalIncome + totalExpenses)*10000.0f)), true, false);
		super.drawText(rightX, rightY, "Reserve");
		rightY = super.drawText(rightX + numberGap, rightY, String.format("%d", (int)(ec.reserve()*10000.0f)), true, false);
	}
}
