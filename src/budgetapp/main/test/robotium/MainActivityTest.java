package budgetapp.main.test.robotium;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import budgetapp.activities.MainActivity;
import budgetapp.util.money.MoneyFactory;

import com.robotium.solo.Solo;

public class MainActivityTest
    extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private MainView view;
    private MainActivity activity;

    public MainActivityTest() {
        super(MainActivity.class);

    }

    public void setUp() {
        activity = getActivity();
        solo = new Solo(getInstrumentation(), getActivity());
        view = new MainView(getActivity(), solo);
    }

    @Suppress
    public void testAddTransaction() throws InterruptedException {
        solo = new Solo(getInstrumentation(), getActivity());
        view = new MainView(getActivity(), solo);
        String transactionValue = "100";
        solo.enterText(view.getSubtractionEditText(), transactionValue);
        solo.clickOnView(view.getCategorySpinner());
        solo.clickInList(2);

        Thread.sleep(1000);
        assertEquals(view.getCurrentBudgetTextView().getText(), MoneyFactory.createMoneyFromNewDouble(-100).toString());

    }

    @Override
    public void tearDown() {
        solo.finishOpenedActivities();
        activity.deleteDatabase("budget.db");
        activity.deleteFile("current_budget");

    }

}
