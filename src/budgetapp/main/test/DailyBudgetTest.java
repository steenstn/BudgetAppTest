package budgetapp.main.test;


import budgetapp.models.BudgetModel;
import budgetapp.util.entries.*;
import budgetapp.util.BudgetFunctions;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;
import budgetapp.util.test.HelperFunctions;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class DailyBudgetTest extends AndroidTestCase{
	
	
	String prefix = "test";
	String startDate = "2012/01/01 00:00";
	RenamingDelegatingContext mockContext;
	BudgetModel model;
	
	public void setUp()
	{
		BudgetFunctions.TESTING = true;
		mockContext = new RenamingDelegatingContext(getContext(), getContext(), prefix);
		
		model = new BudgetModel(mockContext);
		
		BudgetFunctions.theDate = startDate;
		Money.setExchangeRate(1.0);
		model.setDailyBudget(MoneyFactory.createMoney());
		
		model.queueTransaction(new BudgetEntry(MoneyFactory.createMoney(),BudgetFunctions.getDateString(),"test"));
		model.processWholeQueue();
		assertEquals("Incorrect starting budget.", 0.0,model.getCurrentBudget().get());
		assertEquals("Incorrect startDate", startDate, BudgetFunctions.theDate);
	
	}
	
	public void setUpTestCase()
	{
	}
	
	public void tearDownTestCase()
	{
	}
		
	public void testDailyBudget()
	{
		setUpTestCase();
		
		int numberOfDays = 8;
		double dailyBudget = 100;
		model.setDailyBudget(MoneyFactory.createMoneyFromNewDouble(dailyBudget));
		
		addDays(numberOfDays);
		model.queueAddDailyBudget();
		model.processWholeQueue();
		assertEquals("Inncorrect budget after adding daily budget.",
				dailyBudget * numberOfDays, model.getCurrentBudget().get());
		tearDownTestCase();
	}
	
	public void testDailyBudgetHighExchangeRate()
	{
		setUpTestCase();
		
		int numberOfDays = 1;
		double dailyBudget = 100;
		double exchangeRate = 2.0;
		Money.setExchangeRate(exchangeRate);
		model.setDailyBudget(MoneyFactory.createMoneyFromNewDouble(dailyBudget));
		assertEquals("Daily budget not correct", dailyBudget * exchangeRate, model.getDailyBudget().get());
		addDays(numberOfDays);
		model.queueAddDailyBudget();
		model.processWholeQueue();
		assertEquals("Inncorrect budget after adding daily budget.",
				dailyBudget * numberOfDays * Money.getExchangeRate(), model.getCurrentBudget().get());
		tearDownTestCase();
	}
	
	public void testDailyBudgetLowExchangeRate()
	{
		setUpTestCase();
		
		int numberOfDays = 8;
		double dailyBudget = 100;
		Money.setExchangeRate(0.1);
		model.setDailyBudget(MoneyFactory.createMoneyFromNewDouble(dailyBudget));

		addDays(numberOfDays);
		model.queueAddDailyBudget();
		model.processWholeQueue();
		assertEquals("Inncorrect budget after adding daily budget.",
				dailyBudget * numberOfDays * Money.getExchangeRate(), model.getCurrentBudget().get());
		tearDownTestCase();
	}
	
	

	public void tearDown()
	{
		BudgetFunctions.TESTING = false;

		model.clearDatabaseInstance();
	}
	
	private void addDays(int n)
	{
		BudgetFunctions.theDate = HelperFunctions.addDays(BudgetFunctions.theDate, n);
	}

}
