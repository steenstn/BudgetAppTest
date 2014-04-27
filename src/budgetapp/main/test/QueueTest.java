package budgetapp.main.test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import budgetapp.models.BudgetModel;
import budgetapp.util.BudgetFunctions;
import budgetapp.util.entries.BudgetEntry;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;

public class QueueTest extends AndroidTestCase {
	
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
	
	public void tearDown()
	{
		BudgetFunctions.TESTING = false;
		model.clearDatabaseInstance();
	}
	public void testQueue() {
		model.queueTransaction(new BudgetEntry(MoneyFactory.createMoney(),BudgetFunctions.getDateString(),"test"));
		model.queueTransaction(new BudgetEntry(MoneyFactory.createMoney(),BudgetFunctions.getDateString(),"test"));
		model.queueTransaction(new BudgetEntry(MoneyFactory.createMoney(),BudgetFunctions.getDateString(),"test"));
		int remainingItems = model.getRemainingItemsInQueue();
		assertEquals("Incorrect size", 5, model.getQueueSize());
		assertEquals("Incorrect remaining items", 3, remainingItems);
		model.processQueueItem();
		remainingItems = model.getRemainingItemsInQueue();
		assertEquals("Incorrect remaining items", 2, remainingItems);
		model.processQueueItem();
		remainingItems = model.getRemainingItemsInQueue();
		assertEquals("Incorrect remaining items", 1, remainingItems);
		model.processQueueItem();
		remainingItems = model.getRemainingItemsInQueue();
		assertEquals("Incorrect remaining items", 0, remainingItems);
		model.processQueueItem();
		remainingItems = model.getRemainingItemsInQueue();
		assertEquals("Incorrect remaining items", 0, remainingItems);
		model.processQueueItem();
		assertEquals("Incorrect size", 5, model.getQueueSize());
		
		
	}
}
