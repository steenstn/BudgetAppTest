package budgetapp.main.test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import budgetapp.models.BudgetModel;
import budgetapp.util.BudgetFunctions;
import budgetapp.util.Currency;
import budgetapp.util.entries.BudgetEntry;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;

public class CurrencyTest
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

    public void tearDown() {
        BudgetFunctions.TESTING = false;
    }

    public void testActiveFlag() {
        Currency c = new Currency(0, "kr", 2, 0);
        assertEquals(false, c.isActive());

        c.setActive(true);
        assertEquals(true, c.isActive());

        c.setActive(false);
        assertEquals(false, c.isActive());
    }

    public void testShowSymbolAfterFlag() {
        Currency c = new Currency(0, "kr", 2, 0);
        assertEquals(false, c.showSymbolAfter());

        c.setShowSymbolAfter(true);
        assertEquals(true, c.showSymbolAfter());

        c.setShowSymbolAfter(false);
        assertEquals(false, c.showSymbolAfter());
    }

    public void testCreateAndRetrieveCurrency() {
        Currency c = new Currency(0, "kr", 2, Currency.CURRENCY_ACTIVE);
        model.addCurrency(c);

        Currency dbCurrency = model.getCurrencies().get(0);

        assertEquals("kr", dbCurrency.getSymbol());
        assertEquals(2.0, dbCurrency.getExchangeRate());
        assertEquals(Currency.CURRENCY_ACTIVE, dbCurrency.getFlags());
    }
}
