package budgetapp.main.test;

import java.util.List;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import budgetapp.models.BudgetModel;
import budgetapp.util.BudgetFunctions;
import budgetapp.util.Event;
import budgetapp.util.entries.BudgetEntry;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;

public class EventTest extends AndroidTestCase {

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
		
		assertEquals("Incorrect starting budget.", 0.0,model.getCurrentBudget().get());
		assertEquals("Incorrect startDate", startDate, BudgetFunctions.theDate);
	
	}
	
	public void testAddEvent() {
		Event startEvent = new Event(1, "before", "100", "200", "beforecomment", 1);
		model.addEvent(startEvent);
		List<Event> databaseEvents = model.getEvents();
		Event dbEvent = databaseEvents.get(0);
		assertTrue("Incorrect name", startEvent.getName().equalsIgnoreCase(dbEvent.getName()));
		assertTrue("Incorrect id", startEvent.getId() == dbEvent.getId());
		assertTrue("Incorrect startDate", startEvent.getStartDate().equalsIgnoreCase(dbEvent.getStartDate()));
		assertTrue("Incorrect endDate", startEvent.getEndDate().equalsIgnoreCase(dbEvent.getEndDate()));
		assertTrue("Incorrect comment", startEvent.getComment().equalsIgnoreCase(dbEvent.getComment()));
		assertTrue("Incorrect flags", startEvent.getFlags() == dbEvent.getFlags());
	}
	
	public void testEditEvent() {
		Event startEvent = new Event(1, "before", "100", "200", "beforecomment", 1);
		model.addEvent(startEvent);
		List<Event> databaseEvents = model.getEvents();
		long id = databaseEvents.get(0).getId();
		Event newEvent = new Event(2, "after", "300", "400", "aftercomment", 2);
		model.editEvent(id, newEvent);
		
		databaseEvents = model.getEvents();
		Event dbEvent = databaseEvents.get(0);
		assertTrue("Incorrect name", newEvent.getName().equalsIgnoreCase(dbEvent.getName()));
		assertTrue("Incorrect startDate", newEvent.getStartDate().equalsIgnoreCase(dbEvent.getStartDate()));
		assertTrue("Incorrect endDate", newEvent.getEndDate().equalsIgnoreCase(dbEvent.getEndDate()));
		assertTrue("Incorrect comment", newEvent.getComment().equalsIgnoreCase(dbEvent.getComment()));
		assertTrue("Incorrect flags", newEvent.getFlags() == dbEvent.getFlags());
	}
}
