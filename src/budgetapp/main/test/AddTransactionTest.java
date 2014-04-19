package budgetapp.main.test;

import java.util.List;

import budgetapp.models.BudgetModel;
import budgetapp.util.entries.*;
import budgetapp.util.BudgetFunctions;
import budgetapp.util.database.BudgetDataSource;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;
import budgetapp.util.test.HelperFunctions;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class AddTransactionTest extends AndroidTestCase{
	
	BudgetModel model;
	RenamingDelegatingContext mockContext;
	String prefix = "test";
	String startDate = "2012/01/01 00:00";
	
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
	
	public void testAddTransaction()
	{
		double value = 100;
		BudgetEntry entry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(value), BudgetFunctions.getDateString(), "test","comment",2);
		assertEquals("Value of entry not correct", value, entry.getValue().get());
		assertTrue("Category not correct.", entry.getCategory().equalsIgnoreCase("test"));
		assertTrue("Comment not correct.", entry.getComment().equalsIgnoreCase("comment"));
		assertEquals("Flags not correct", 2, entry.getFlags());
		model.queueTransaction(entry);
		model.processWholeQueue();
		List<BudgetEntry> transactions = model.getSomeTransactions(1, BudgetDataSource.DESCENDING);
		entry = transactions.get(0);
		
		assertEquals("Value of entry not correct after adding transaction", value, entry.getValue().get());
		assertEquals("Value of current budget not correct", value, model.getCurrentBudget().get());
		assertTrue("Category not correct after adding transaction.", entry.getCategory().equalsIgnoreCase("test"));
		assertTrue("Comment not correct after adding transaction.", entry.getComment().equalsIgnoreCase("comment"));
		assertEquals("Flags not correct after adding transaction", 2, entry.getFlags());
		
	}
	
	public void testAddTransactionHighExchangeRate()
	{
		double value = 100;
		double exchangeRate = 2;
		Money.setExchangeRate(exchangeRate);
		
		BudgetEntry startEntry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(value), BudgetFunctions.getDateString(), "test");
		assertEquals("Value of entry incorrect", value * exchangeRate, startEntry.getValue().get());
		model.queueTransaction(startEntry);
		model.processWholeQueue();
		List<BudgetEntry> transactions = model.getSomeTransactions(1, BudgetDataSource.DESCENDING);
		BudgetEntry entry = transactions.get(0);
		
		assertEquals("Value of entry not correct after adding transaction", value * exchangeRate, entry.getValue().get());
		assertEquals("Value of current budget not correct", value, model.getCurrentBudget().get() / Money.getExchangeRate());
	}
	
	public void testAddTransactionLowExchangeRate()
	{
		double value = 100;
		double exchangeRate = .2;
		Money.setExchangeRate(exchangeRate);
		
		model.queueTransaction(new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(value), BudgetFunctions.getDateString(), "test"));
		model.processWholeQueue();
		List<BudgetEntry> transactions = model.getSomeTransactions(1, BudgetDataSource.DESCENDING);
		BudgetEntry entry = transactions.get(0);
		
		assertEquals("Value of entry not correct after adding transaction", value * exchangeRate, entry.getValue().get());
		assertEquals("Value of current budget not correct", value * exchangeRate, model.getCurrentBudget().get());
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
