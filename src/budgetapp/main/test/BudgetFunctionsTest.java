package budgetapp.main.test;

import android.test.AndroidTestCase;
import budgetapp.util.BudgetFunctions;
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
        assertEquals(1.0, BudgetFunctions.min(1.0, 2.0));
        assertEquals(MoneyFactory.createMoneyFromNewDouble(1).get(), BudgetFunctions.min(MoneyFactory
            .createMoneyFromNewDouble(1)
            .get(), MoneyFactory.createMoneyFromNewDouble(2).get()));
    }

    public void testMax() {
        assertEquals(2.0, BudgetFunctions.max(1.0, 2.0));
        assertEquals(MoneyFactory.createMoneyFromNewDouble(2),
            BudgetFunctions.max(MoneyFactory.createMoneyFromNewDouble(1), MoneyFactory.createMoneyFromNewDouble(2)));
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
}
