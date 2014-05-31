package budgetapp.main.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import budgetapp.util.BudgetFunctions;
import budgetapp.util.entries.BudgetEntry;
import budgetapp.util.money.MoneyFactory;

public class BudgetFunctionsTest
    extends AndroidTestCase {

    public void setUp() {
        BudgetFunctions.TESTING = true;
    }

    public void tearDown() {
        BudgetFunctions.TESTING = false;
    }

    public void testMin() {
        assertEquals(1, BudgetFunctions.min((int) 1, (int) 2));
        assertEquals(1, BudgetFunctions.min((int) 2, (int) 1));

        assertEquals(1.0, BudgetFunctions.min(1.0, 2.0));
        assertEquals(1.0, BudgetFunctions.min(2.0, 1.0));

        assertEquals(MoneyFactory.createMoneyFromNewDouble(1).get(),
            BudgetFunctions
                .min(MoneyFactory.createMoneyFromNewDouble(1), MoneyFactory.createMoneyFromNewDouble(2))
                .get());
        assertEquals(MoneyFactory.createMoneyFromNewDouble(1).get(),
            BudgetFunctions
                .min(MoneyFactory.createMoneyFromNewDouble(2), MoneyFactory.createMoneyFromNewDouble(1))
                .get());
    }

    public void testMax() {
        assertEquals(2.0, BudgetFunctions.max(1.0, 2.0));
        assertEquals(2.0, BudgetFunctions.max(2.0, 1.0));

        assertEquals(MoneyFactory.createMoneyFromNewDouble(2).get(), BudgetFunctions.max(MoneyFactory
            .createMoneyFromNewDouble(1)
            .get(), MoneyFactory.createMoneyFromNewDouble(2).get()));
        assertEquals(MoneyFactory.createMoneyFromNewDouble(2).get(), BudgetFunctions.max(MoneyFactory
            .createMoneyFromNewDouble(2)
            .get(), MoneyFactory.createMoneyFromNewDouble(1).get()));
    }

    public void testAlmostEquals() {
        assertEquals(true, BudgetFunctions.almostEquals(0, 0.0000001));
        assertEquals(false, BudgetFunctions.almostEquals(0, 0.0001));
    }

    public void testGetDateAsIntegers() {
        BudgetFunctions.TESTING = true;
        //dateString = "2012/01/01 00:00"
        assertEquals(2012, BudgetFunctions.getYear());
        assertEquals(0, BudgetFunctions.getMonth());
        assertEquals(1, BudgetFunctions.getDay());
        assertEquals(0, BudgetFunctions.getHours());
        assertEquals(0, BudgetFunctions.getMinutes());
    }

    public void testGetMean() {
        List<BudgetEntry> entries = new ArrayList<BudgetEntry>();
        BudgetEntry entry1 = new BudgetEntry();
        entry1.setValue(MoneyFactory.createMoneyFromNewDouble(10));
        BudgetEntry entry2 = new BudgetEntry();
        entry2.setValue(MoneyFactory.createMoneyFromNewDouble(20));
        BudgetEntry entry3 = new BudgetEntry();
        entry3.setValue(MoneyFactory.createMoneyFromNewDouble(30));

        entries.add(entry1);
        assertEquals(MoneyFactory.createMoney().get(), BudgetFunctions.getMean(entries, 1).get());
        entries.add(entry2);
        entries.add(entry3);
        assertEquals(entry2.getValue().get(), BudgetFunctions.getMean(entries, 3).get());

    }
}
