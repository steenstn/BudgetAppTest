package budgetapp.main.test;

import android.test.AndroidTestCase;
import budgetapp.util.Currency;

public class CurrencyTest
    extends AndroidTestCase {

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

}
