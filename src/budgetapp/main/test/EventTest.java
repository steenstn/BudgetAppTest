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
		assertEquals("Incorrect name", startEvent.getName(),dbEvent.getName());
		assertEquals("Incorrect id", startEvent.getId(), dbEvent.getId());
		assertEquals("Incorrect startDate", startEvent.getStartDate(),dbEvent.getStartDate());
		assertEquals("Incorrect endDate", startEvent.getEndDate(),dbEvent.getEndDate());
		assertEquals("Incorrect comment", startEvent.getComment(),dbEvent.getComment());
		assertEquals("Incorrect flags", startEvent.getFlags(), dbEvent.getFlags());
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
	
	public void testLinkedTransaction() {
		Event event = new Event(0, "testEvent", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", Event.EVENT_ACTIVE);
		assertTrue("Could not add event", model.addEvent(event));

		event = model.getEvent(model.getIdFromEventName("testEvent"));

		assertEquals("Incorrect event", event.getName(),"testEvent");
		
		BudgetEntry entry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100), BudgetFunctions.getDateString(),"test");
		model.queueTransaction(entry, event.getId());
		model.processWholeQueue();
		event =  model.getEvent(model.getIdFromEventName("testEvent"));
		assertEquals("Transaction was not linked with event", event.getTotalCost().get(),entry.getValue().get());
	}
}
