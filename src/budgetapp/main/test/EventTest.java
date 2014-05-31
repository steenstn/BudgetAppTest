package budgetapp.main.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import budgetapp.models.BudgetModel;
import budgetapp.util.BudgetFunctions;
import budgetapp.util.Event;
import budgetapp.util.commands.TransactionCommand;
import budgetapp.util.entries.BudgetEntry;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;

public class EventTest
    extends AndroidTestCase {

    BudgetModel model;
    RenamingDelegatingContext mockContext;
    String prefix = "test";
    String startDate = "2012/01/01 00:00";

    public void setUp() {
        BudgetFunctions.TESTING = true;
        mockContext = new RenamingDelegatingContext(getContext(), getContext(), prefix);

        model = new BudgetModel(mockContext);

        BudgetFunctions.theDate = startDate;
        Money.setExchangeRate(1.0);
        model.setDailyBudget(MoneyFactory.createMoney());

        model.queueTransaction(new BudgetEntry(MoneyFactory.createMoney(), BudgetFunctions.getDateString(), "test"));
        model.processWholeQueue();
        assertEquals("Incorrect starting budget.", 0.0, model.getCurrentBudget().get());
        assertEquals("Incorrect startDate", startDate, BudgetFunctions.theDate);

    }

    public void testAddEvent() {
        Event startEvent = new Event(1, "before", "100", "200", "beforecomment", 1);
        model.addEvent(startEvent);
        List<Event> databaseEvents = model.getEvents();
        Event dbEvent = databaseEvents.get(0);
        assertEquals("Incorrect name", startEvent.getName(), dbEvent.getName());
        assertEquals("Incorrect id", startEvent.getId(), dbEvent.getId());
        assertEquals("Incorrect startDate", startEvent.getStartDate(), dbEvent.getStartDate());
        assertEquals("Incorrect endDate", startEvent.getEndDate(), dbEvent.getEndDate());
        assertEquals("Incorrect comment", startEvent.getComment(), dbEvent.getComment());
        assertEquals("Incorrect flags", startEvent.getFlags(), dbEvent.getFlags());
    }

    public void testActive() {
        Event startEvent = new Event(1, "before", "100", "200", "beforecomment", 1);
        startEvent.setActive(true);
        assertEquals(true, startEvent.isActive());
        startEvent.setActive(false);
        assertEquals(true, startEvent.isActive());

    }

    public void testRemoveEvent() {
        Event event = new Event(1, "before", "100", "200", "beforecomment", 1);
        model.addEvent(event);
        model.removeEvent(model.getEvents().get(0).getId());
        assertEquals("There are still events in the database", 0, model.getEvents().size());
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
        Event event = new Event(0, "testEvent", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "",
            Event.EVENT_ACTIVE);
        assertTrue("Could not add event", model.addEvent(event));

        event = model.getEvent(model.getIdFromEventName("testEvent"));

        assertEquals("Incorrect event", event.getName(), "testEvent");

        BudgetEntry entry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100),
            BudgetFunctions.getDateString(), "test");
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(event.getId());
        model.queueTransaction(entry, ids);
        model.processWholeQueue();
        event = model.getEvent(model.getIdFromEventName("testEvent"));
        assertEquals("Transaction was not linked with event", event.getTotalCost().get(), entry.getValue().get());
    }

    public void testChangeTransactionLink() {
        Event event1 = new Event(0, "event1", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);
        Event event2 = new Event(0, "event2", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);

        model.addEvent(event1);
        model.addEvent(event2);

        event1 = model.getEvents().get(0);
        event2 = model.getEvents().get(1);

        BudgetEntry entry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100),
            BudgetFunctions.getDateString(), "test");

        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(event1.getId());

        model.queueTransaction(entry, ids);
        model.processWholeQueue();
        event1 = model.getEvent(event1.getId());
        event2 = model.getEvent(event2.getId());

        assertEquals("Incorrect total cost in first event", 100.0, event1.getTotalCost().get());
        assertEquals("Incorrect total cost in second event", 0.0, event2.getTotalCost().get());

        long transactionId = event1.getEntries().get(0).getId();

        model.linkTransactionToEvent(transactionId, event2.getId());

        event1 = model.getEvent(event1.getId());
        event2 = model.getEvent(event2.getId());

        assertEquals("Incorrect number of linked events", 2, model
            .getLinkedEventsFromTransactionId(transactionId)
            .size());
        assertEquals("Incorrect total cost in first event", 100.0, event1.getTotalCost().get());
        assertEquals("Incorrect total cost in second event", 100.0, event2.getTotalCost().get());

    }

    public void testGetTransactionsFromEvent() {
        Event event1 = new Event(0, "event1", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);
        model.addEvent(event1);
        event1 = model.getEvents().get(0);

        BudgetEntry entry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100),
            BudgetFunctions.getDateString(), "test");
        model.queueTransaction(entry);
        model.queueTransaction(entry);
        model.queueTransaction(entry);
        model.processWholeQueue();

        BudgetEntry eventEntry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100),
            BudgetFunctions.getDateString(), "eventEntry");
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(event1.getId());
        model.queueTransaction(eventEntry, ids);
        model.queueTransaction(eventEntry, ids);
        model.queueTransaction(eventEntry, ids);
        model.processWholeQueue();

        List<BudgetEntry> eventEntries = model.getTransactionsFromEvent(event1.getId());
        assertEquals("Incorrect number of entries", 3, eventEntries.size());
    }

    public void testAddTransactionToMultipleEvents() {
        Event event1 = new Event(0, "event1", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);
        Event event2 = new Event(0, "event2", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);
        Event event3 = new Event(0, "event3", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);

        model.addEvent(event1);
        model.addEvent(event2);
        model.addEvent(event3);
        event1 = model.getEvents().get(0);
        event2 = model.getEvents().get(1);
        event3 = model.getEvents().get(2);

        BudgetEntry eventEntry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100),
            BudgetFunctions.getDateString(), "eventEntry");

        BudgetEntry eventEntry2 = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100),
            BudgetFunctions.getDateString(), "eventEntry2");
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(event1.getId());
        model.queueTransaction(eventEntry, ids);

        model.processWholeQueue();
        ids.add(event2.getId());
        model.queueTransaction(eventEntry2, ids);

        model.processWholeQueue();
        assertEquals("Transaction linked on wrong event", 0, model.getEvent(event3.getId()).getEntries().size());

        List<BudgetEntry> eventEntries = model.getTransactionsFromEvent(event1.getId());
        assertEquals("Incorrect number of entries", 2, eventEntries.size());

        eventEntries = model.getTransactionsFromEvent(event2.getId());
        assertEquals("Incorrect number of entries", 1, eventEntries.size());

        eventEntries = model.getTransactionsFromEvent(event3.getId());
        assertEquals("Incorrect number of entries", 0, eventEntries.size());
    }

    public void testRemoveTransactionFromMultipleEvents() {
        Event event1 = new Event(0, "event1", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);
        Event event2 = new Event(0, "event2", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);
        Event event3 = new Event(0, "event3", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);

        model.addEvent(event1);
        model.addEvent(event2);
        model.addEvent(event3);
        event1 = model.getEvents().get(0);
        event2 = model.getEvents().get(1);
        event3 = model.getEvents().get(2);

        BudgetEntry eventEntry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100),
            BudgetFunctions.getDateString(), "eventEntry");
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(event1.getId());
        ids.add(event2.getId());
        model.queueTransaction(eventEntry, ids);
        model.processWholeQueue();
        BudgetEntry entry = model.getTransactionsFromEvent(event1.getId()).get(0);
        model.removeTransaction(entry);

        assertEquals("Wrong value", 0.0, model.getEvents().get(0).getTotalCost().get());
        assertEquals("Wrong value", 0.0, model.getEvents().get(1).getTotalCost().get());

        event1 = model.getEvents().get(0);
        assertEquals("Event has transactions in database", 0, event1.getEntries().size());

    }

    public void testGetTransactionsEvent() {
        Event event1 = new Event(0, "event1", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);
        Event event2 = new Event(0, "event2", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);
        Event event3 = new Event(0, "event3", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);

        model.addEvent(event1);
        model.addEvent(event2);
        model.addEvent(event3);
        event1 = model.getEvents().get(0);
        event2 = model.getEvents().get(1);
        event3 = model.getEvents().get(2);

        BudgetEntry eventEntry = new BudgetEntry(MoneyFactory.createMoneyFromNewDouble(100),
            BudgetFunctions.getDateString(), "eventEntry");
        ArrayList<Long> ids = new ArrayList<Long>();
        ids.add(event1.getId());
        ids.add(event2.getId());
        model.queueTransaction(eventEntry, ids);
        TransactionCommand command = (TransactionCommand) model.processQueueItem();
        System.out.println("id : " + command.getEntry().getId());
        List<Event> linkedEvents = model.getLinkedEventsFromTransactionId(command.getEntry().getId());
        assertEquals("Incorrect number of linked events", 2, linkedEvents.size());
        assertEquals("event1 does not exist", event1.getName(), linkedEvents.get(0).getName());
        assertEquals("event2 does not exist", event2.getName(), linkedEvents.get(1).getName());
    }

    public void testGetIdsOfActiveEvents() {
        Event event1 = new Event(0, "event1", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "",
            Event.EVENT_ACTIVE);
        Event event2 = new Event(0, "event2", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "",
            Event.EVENT_ACTIVE);
        Event event3 = new Event(0, "event3", BudgetFunctions.getDateString(), BudgetFunctions.getDateString(), "", 0);

        model.addEvent(event1);
        model.addEvent(event2);
        model.addEvent(event3);
        event1 = model.getEvents().get(0);
        event2 = model.getEvents().get(1);
        event3 = model.getEvents().get(2);

        List<Event> dbEvents = model.getActiveEvents();
        assertEquals("Incorrect event", event1.getName(), dbEvents.get(0).getName());
        assertEquals("Incorrect event", event2.getName(), dbEvents.get(1).getName());

    }

    public void tearDown() {
        BudgetFunctions.TESTING = false;
        model.clearDatabaseInstance();
    }
}
