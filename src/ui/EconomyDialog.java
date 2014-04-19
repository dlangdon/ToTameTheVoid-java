package ui;

import empire.Economy;
import empire.Empire;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import ui.widget.Widget;

public class EconomyDialog extends MainDialog
{
	private OptionSelector<Float> invest;
	private OptionSelector<Integer> roi;
	private OptionSelector<Integer> boost;

// Public Methods =====================================================================================================
	public EconomyDialog(Widget parent) throws SlickException
	{
		super(parent);
		setSize(600, 650);

		invest = new OptionSelector<>(this);
		String[] options = {"0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"};
		Float[] values = {0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
		invest.setOptions(options, values);
		invest.setListener((value) -> Empire.getPlayerEmpire().getEconomy().setGrowthPolicy(value));

		roi = new OptionSelector<>(this);
		String[] options2 = {"1 turn", "2 turns", "3 turns", "5 turns", "8 turns", "15 turns", "the end"};
		Integer[] values2 = {1, 2, 3, 5, 8, 15, 10000};
		roi.setOptions(options2, values2);
		roi.setListener((value) -> Empire.getPlayerEmpire().getEconomy().setReturnOfInvestmentLimit(value));

		boost = new OptionSelector<>(this);
		String[] options3 = {"Boost", "Avoid"};
		Integer[] values3 = {0, 1};
		boost.setOptions(options3, values3);
		boost.setListener((value) -> Empire.getPlayerEmpire().getEconomy().setOnlyLocal(value == 1));
	}

	public void render(GameContainer gc, Graphics g)
	{
		// Draw background
		super.render(gc, g);

		Empire e = Empire.getPlayerEmpire();
		Economy ec = e.getEconomy();

		float leftX = x() + 40;
		float leftY = super.drawTitle("Economy");
		float numberGap = 260;

		// Policy
		leftY = super.drawSubtitle(leftX, leftY, "Policy");

		float auxX = super.drawText(leftX, leftY, "Spend up to ", false, true);
		invest.setPosition(auxX, leftY);
		invest.render(gc, g);
		leftY = super.drawText(auxX + invest.width(), leftY, " of production on growth");

		auxX = super.drawText(leftX, leftY, "If the investment pays for itself before ", false, true);
		roi.setPosition(auxX, leftY);
		roi.render(gc, g);
		leftY = super.drawText(auxX + roi.width(), leftY, "");

		boost.setPosition(leftX, leftY);
		boost.render(gc, g);
		leftY = super.drawText(leftX + boost.width(), leftY, " growth with reserve (at 50% extra cost)");

		float rightY = leftY;

		leftY = super.drawSubtitle(leftX, leftY, "Income");
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
		leftY = super.drawSubtitle(leftX, leftY, "Expenses");
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
		float rightX = leftX + 280;
		numberGap -= 30;
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
		super.drawText(rightX + numberGap, rightY, String.format("%d", (int)(ec.reserve()*10000.0f)), true, false);
	}
}
