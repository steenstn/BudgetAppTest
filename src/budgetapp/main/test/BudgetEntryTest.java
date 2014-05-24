package budgetapp.main.test;

import android.os.Parcel;
import android.os.Parcelable;
import android.test.AndroidTestCase;
import budgetapp.util.entries.BudgetEntry;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;

public class BudgetEntryTest extends AndroidTestCase {

	public void testCopyConstructor() {
		Money money = MoneyFactory.createMoneyFromNewDouble(100);
		int flags = 10;
		String date = "2012/01/01";
		String category = "category";
		String comment = "comment";
		BudgetEntry entry1 = new BudgetEntry(1, money, date, category, flags, comment);
		BudgetEntry entry2 = new BudgetEntry(entry1);
		
		assertEquals(1, entry2.getId(),1);
		assertEquals(money, entry2.getValue());
		assertEquals(date, entry2.getDate());
		assertEquals(category, entry2.getCategory());
		assertEquals(flags, entry2.getFlags());
		assertEquals(comment, entry2.getComment());
		
	}
	
	public void testDescribeContents() {
		BudgetEntry entry = new BudgetEntry();
		assertEquals(0,entry.describeContents());
	}
	
}
