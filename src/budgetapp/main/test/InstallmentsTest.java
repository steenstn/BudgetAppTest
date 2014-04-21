package budgetapp.main.test;

import java.util.List;

import budgetapp.models.BudgetModel;
import budgetapp.util.BudgetFunctions;
import budgetapp.util.entries.*;
import budgetapp.util.Installment;
import budgetapp.util.database.BudgetDataSource;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;
import budgetapp.util.test.HelperFunctions;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class InstallmentsTest extends AndroidTestCase{
	
	
	String prefix = "test";
	String startDate = "2012/01/01 00:00";
	RenamingDelegatingContext mockContext;
	BudgetModel model;
	
	public void setUp()
	{
		BudgetFunctions.TESTING = true;

		BudgetFunctions.theDate = startDate;
		mockContext = new RenamingDelegatingContext(getContext(), getContext(), prefix);
		
		model = new BudgetModel(mockContext);
		
		Money.setExchangeRate(1.0);
		model.setDailyBudget(MoneyFactory.createMoney());
		
		assertTrue("Incorrect startDate", startDate.equalsIgnoreCase(BudgetFunctions.getDateString()));
	
	}
	
	public void testAddInstallment()
	{
		assertEquals("Incorrect starting budget.", 0.0,model.getCurrentBudget().get());
		double totalValue = -100;
		double dailyPayment = -10;
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(totalValue), 
				MoneyFactory.createMoneyFromNewDouble(dailyPayment), BudgetFunctions.getDateString(), MoneyFactory.createMoney(), "test", "testComment");

		assertEquals("Could not add installment.", model.addInstallment(installment), true);
		List<Installment> installments = model.getInstallments();
		Installment result = installments.get(0);
		assertEquals("Incorrect start value", totalValue - dailyPayment, result.getRemainingValue().get());
	}
	
	public void testInstallment()
	{
		double installmentTotalValue = -100;
		double installmentDailyPayment = -10;
		double installmentAmountPaid = 0;

		int numberOfDays = 8;
		
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(installmentTotalValue), MoneyFactory.createMoneyFromNewDouble(installmentDailyPayment),
				BudgetFunctions.getDateString(), MoneyFactory.createMoneyFromNewDouble(installmentAmountPaid), "test", "testComment");
		
		assertEquals("Could not add installment.", model.addInstallment(installment), true);
		
		
		addDays(numberOfDays);
		assertEquals("Wrong number of days added.", model.addDailyBudget(), numberOfDays);
		model.payOffInstallments();
		model.processWholeQueue();
		List<Installment> resultingInstallments = model.getInstallments();
		double amountPaid = resultingInstallments.get(0).getAmountPaid().get();
		assertEquals("Incorrect amount paid", installmentDailyPayment * numberOfDays + installmentDailyPayment, amountPaid);
		
		assertEquals("Incorrect current budget after paying of installments",
				installmentDailyPayment * numberOfDays + installmentDailyPayment, model.getCurrentBudget().get());
		
		List<DayEntry> dayFlow = model.getSomeDays(0, BudgetDataSource.ASCENDING);
		
		for(int i = 0; i < dayFlow.size(); i++)
		{
			assertEquals("Daily flow not correct for day " + dayFlow.get(i).getDate(), 
					installmentDailyPayment, dayFlow.get(i).getValue().get());
		}
		
	}
	
	public void testInstallmentFlags()
	{
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(100), MoneyFactory.createMoneyFromNewDouble(10),
				BudgetFunctions.getDateString(), MoneyFactory.createMoney(), "test", "testComment");
		
		installment.setPaidOff(true);
		installment.setPaidOff(true);
		assertEquals("Installment paused", installment.isPaused(), false);
		assertEquals("Installment not paid off", installment.isPaidOff(), true);
		
		installment.setPaused(true);
		assertEquals("Installment not paused", installment.isPaused(), true);
		assertEquals("Installment not paid off", installment.isPaidOff(), true);
		
		installment.setPaused(false);
		installment.setPaused(false);
		assertEquals("Installment paused", installment.isPaused(), false);
		assertEquals("Installment not paid off", installment.isPaidOff(), true);
		
	}
	
	public void testPauseInstallment() {
		double installmentTotalValue = -100;
		double installmentDailyPayment = -10;
		double installmentAmountPaid = 0;

		int numberOfDays = 8;
		
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(installmentTotalValue), MoneyFactory.createMoneyFromNewDouble(installmentDailyPayment),
				BudgetFunctions.getDateString(), MoneyFactory.createMoneyFromNewDouble(installmentAmountPaid), "test", "testComment");
		installment.setPaused(true);
		assertEquals("Could not add installment.", model.addInstallment(installment), true);
		
		
		addDays(numberOfDays);
		assertEquals("Wrong number of days added.", model.addDailyBudget(), numberOfDays);
		Money temp = model.payOffInstallments();
		assertEquals("Incorrect amount paid", 0.0, temp.get());
		
		assertEquals("Incorrect current budget after paying of installments",
				installmentDailyPayment, model.getCurrentBudget().get());
		
		List<DayEntry> dayFlow = model.getSomeDays(0, BudgetDataSource.ASCENDING);
		
		for(int i = 1; i < dayFlow.size(); i++)
		{
			assertEquals("Daily flow not correct for day " + dayFlow.get(i).getDate(), 
					0.0, dayFlow.get(i).getValue().get());
		}
		
	}
	
	public void testResumeInstallment() {
		double installmentTotalValue = -100;
		double installmentDailyPayment = -10;
		double installmentAmountPaid = 0;

		int numberOfDays = 2;
		
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(installmentTotalValue), MoneyFactory.createMoneyFromNewDouble(installmentDailyPayment),
				BudgetFunctions.getDateString(), MoneyFactory.createMoneyFromNewDouble(installmentAmountPaid), "test", "testComment");
		installment.setPaused(true);
		assertEquals("Could not add installment.", model.addInstallment(installment), true);
		addDays(numberOfDays);
		Money temp = model.payOffInstallments();
		assertEquals("Installment incorrectly paid off", 0.0, temp.get());
		List<Installment> installments = model.getInstallments();

		installment = installments.get(0);
		installment.setPaused(false);
		model.editInstallment(installment.getId(), installment);
		installment = model.getInstallment(installment.getId());
		addDays(3);
		Money temp2 = model.payOffInstallments();
		assertEquals("Incorrect amount paid", installmentDailyPayment*3, temp2.get());
	}
	
	public void testEditInstallment() {
		Installment installment = new Installment(1, 1, MoneyFactory.createMoneyFromNewDouble(1), MoneyFactory.createMoneyFromNewDouble(1),
			"2012/01/01 00:00", MoneyFactory.createMoneyFromNewDouble(1), "FirstCategory", "FirstComment", 1);
		model.addInstallment(installment);
		
		Installment newInstallment = new Installment(1, 1, MoneyFactory.createMoneyFromNewDouble(2), MoneyFactory.createMoneyFromNewDouble(2),
				"2013/01/01 00:00", MoneyFactory.createMoneyFromNewDouble(2), "SecondCategory", "SecondComment", 0);
		newInstallment.setPaused(true);
		List<Installment> installments = model.getInstallments();
		model.editInstallment(installments.get(0).getId(), newInstallment);
		//t(long id, long transactionId, Money totalValue, Money dailyPayment,
			//	String dateLastPaid, Money amountPaid, String category, String comment, int flags)
		Installment resultingInstallment = model.getInstallment(installments.get(0).getId());
		assertEquals("Incorrect totalValue",newInstallment.getTotalValue().get(),resultingInstallment.getTotalValue().get());
		assertEquals("Incorrect dailyPayment",newInstallment.getDailyPayment().get(),resultingInstallment.getDailyPayment().get());
		assertEquals("Incorrect dateLastPadid",newInstallment.getDateLastPaid(),resultingInstallment.getDateLastPaid());
		assertEquals("Incorrect amountPaid",installment.getAmountPaid().get(),resultingInstallment.getAmountPaid().get());
		assertEquals("Incorrect category",installment.getCategory().equalsIgnoreCase(resultingInstallment.getCategory()),true);
		assertEquals("Incorrect comment",installment.getComment().equalsIgnoreCase(resultingInstallment.getComment()),true);
		assertEquals("Incorrect flags",newInstallment.getFlags(),resultingInstallment.getFlags());
		
		
	}
	
	public void testEditAndPayOffInstallment()
	{
		double exchangeRate = 2;
		double installmentTotalValue = -100;
		double installmentDailyPayment = -10;
		double installmentAmountPaid = 0;
		
		double newInstallmentTotalValue = -200;
		double newInstallmentDailyPayment = -15;

		int numberOfDays = 2;
		
		Money.setExchangeRate(exchangeRate);
		
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(installmentTotalValue), MoneyFactory.createMoneyFromNewDouble(installmentDailyPayment),
				BudgetFunctions.getDateString(), MoneyFactory.createMoneyFromNewDouble(installmentAmountPaid), "test", "testComment");
		
		assertEquals("Could not add installment.", model.addInstallment(installment), true);
		
		
		addDays(numberOfDays);
		assertEquals("Wrong number of days added.", model.addDailyBudget(), numberOfDays);
		Money temp = model.payOffInstallments();
		model.processWholeQueue();
		
		assertEquals("Incorrect current budget after paying of installments",
				installmentDailyPayment * exchangeRate * numberOfDays + installmentDailyPayment * exchangeRate, model.getCurrentBudget().get());
		
		List<DayEntry> dayFlow = model.getSomeDays(0, BudgetDataSource.ASCENDING);
		
		for(int i = 0; i < dayFlow.size(); i++)
		{
			assertEquals("Daily flow not correct for day " + dayFlow.get(i).getDate(), 
					installmentDailyPayment * exchangeRate, dayFlow.get(i).getValue().get());
		}
		
		List<Installment> installments = model.getInstallments();
		
		Installment newInstallment = new Installment(MoneyFactory.createMoneyFromNewDouble(newInstallmentTotalValue), MoneyFactory.createMoneyFromNewDouble(newInstallmentDailyPayment), installments.get(0).getDateLastPaid(),
				MoneyFactory.createMoney(), "", "");
		model.editInstallment(installments.get(0).getId(), newInstallment);
		
		int numberOfDays2 = 5;
		addDays(numberOfDays2);
		model.payOffInstallments();
		model.processWholeQueue();
		//assertEquals("Incorrect amount paid off after editing",
		//		newInstallmentDailyPayment * exchangeRate * numberOfDays2, );
	}
	
	
	public void testInstallmentPayOffExtraDays()
	{
		//assertEquals("Incorrect starting budget.", 0.0,model.getCurrentBudget().get());
		double installmentTotalValue = -5;
		double installmentDailyPayment = -1;
		double installmentAmountPaid = 0;

		int numberOfDays = 8;
		
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(installmentTotalValue), MoneyFactory.createMoneyFromNewDouble(installmentDailyPayment),
				BudgetFunctions.getDateString(), MoneyFactory.createMoneyFromNewDouble(installmentAmountPaid), "test", "testComment");
		
		assertEquals("Could not add installment.", model.addInstallment(installment), true);
		
		
		addDays(numberOfDays);
		assertEquals("Wrong number of days added.", model.addDailyBudget(), numberOfDays);
		model.payOffInstallments();
		model.processWholeQueue();
		
		assertEquals("Incorrect current budget after paying of installments",
				installmentTotalValue, model.getCurrentBudget().get());
		
		List<DayEntry> dayFlow = model.getSomeDays(0, BudgetDataSource.ASCENDING);
		
		int numDaysToPayOff = (int) Math.ceil(installmentTotalValue / installmentDailyPayment);
		double expectedValue = installmentDailyPayment;
		
		for(int i = 0; i < dayFlow.size(); i++)
		{
			if(i < numDaysToPayOff)
			{
				expectedValue = installmentDailyPayment;
			}
			else
			{
				expectedValue = 0;
			}
			assertEquals("Daily flow not correct for day " + dayFlow.get(i).getDate(), 
					expectedValue, dayFlow.get(i).getValue().get());
		}
		
		List<Installment> installments = model.getInstallments();
		assertEquals("Installment not set as paid off", installments.get(0).isPaidOff(),
				true);
		
	}
	

	public void testMultipleInstallment()
	{
		assertEquals("Incorrect starting budget.", 0.0,model.getCurrentBudget().get());
		double installmentTotalValue = -100;
		double installmentDailyPayment = -10;
		double installmentAmountPaid = 0;

		int numberOfDays = 8;
		int numInstallments = 3;
		for(int i = 0; i < numInstallments; i++)
		{
			Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(installmentTotalValue), MoneyFactory.createMoneyFromNewDouble(installmentDailyPayment),
					BudgetFunctions.getDateString(), MoneyFactory.createMoneyFromNewDouble(installmentAmountPaid), "test", "testComment");
			
			assertEquals("Could not add installment.", model.addInstallment(installment), true);
		}
		
		assertEquals("Incorrect number of installments", numInstallments, model.getInstallments().size());
		
		addDays(numberOfDays);
		assertEquals("Wrong number of days added.", model.addDailyBudget(), numberOfDays);
		model.payOffInstallments();
		model.processWholeQueue();
		assertEquals("Incorrect current budget after paying of installments",
				numInstallments * (installmentDailyPayment * numberOfDays + installmentDailyPayment),
				model.getCurrentBudget().get());
		
		List<DayEntry> dayFlow = model.getSomeDays(0, BudgetDataSource.ASCENDING);
		
		for(int i = 0; i < dayFlow.size(); i++)
		{
			assertEquals("Daily flow not correct for day " + dayFlow.get(i).getDate(), 
					numInstallments * installmentDailyPayment, dayFlow.get(i).getValue().get());
		}
		
	}
	
	
	public void testInstallmentsHighExchangeRate()
	{
		assertEquals("Wrong start budget.", 0.0,model.getCurrentBudget().get());
		double installmentTotalValue = -100;
		double installmentDailyPayment = -10;
		double installmentAmountPaid = 0;
		double exchangeRate = 2;
		int numberOfDays = 1;
		Money.setExchangeRate(exchangeRate);
		
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(installmentTotalValue), MoneyFactory.createMoneyFromNewDouble(installmentDailyPayment),
				BudgetFunctions.getDateString(), MoneyFactory.createMoneyFromNewDouble(installmentAmountPaid), "test", "testComment");
		
		model.setDailyBudget(MoneyFactory.createMoneyFromNewDouble(100));
		
		assertEquals("Could not add installment.", model.addInstallment(installment), true);
		
		addDays(numberOfDays);
		int daysAdded = model.addDailyBudget();
		double dailyBudgetMoney = model.getDailyBudget().multiply(daysAdded).get();
		model.payOffInstallments();
		model.processWholeQueue();
		double totalOutcome = installmentDailyPayment * exchangeRate * numberOfDays + installmentDailyPayment * exchangeRate;
		assertEquals("Incorrect current budget after paying of installments",
				totalOutcome + dailyBudgetMoney, model.getCurrentBudget().get());
		
	}
	
	public void testInstallmentsLowExchangeRate()
	{
		assertEquals("Wrong start budget.", 0.0,model.getCurrentBudget().get());
		double installmentTotalValue = -100;
		double installmentDailyPayment = -10;
		double installmentAmountPaid = 0;
		double exchangeRate = 0.1;
		Money.setExchangeRate(exchangeRate);
		
		Installment installment = new Installment(MoneyFactory.createMoneyFromNewDouble(installmentTotalValue), MoneyFactory.createMoneyFromNewDouble(installmentDailyPayment),
				BudgetFunctions.getDateString(), MoneyFactory.createMoneyFromNewDouble(installmentAmountPaid), "test", "testComment");
		
		model.setDailyBudget(MoneyFactory.createMoneyFromNewDouble(100));
		System.out.println("Daily budget: " + model.getDailyBudget());
		
		System.out.println("Daily budget: " + model.getDailyBudget());
		int numberOfDays = 1;
		
		assertEquals("Could not add installment.", model.addInstallment(installment), true);
		
		List<Installment> installments = model.getInstallments();
		addDays(numberOfDays);
		int daysAdded = model.addDailyBudget();
		model.processWholeQueue();
		double dailyBudgetMoney = model.getDailyBudget().multiply(daysAdded).get();
		model.payOffInstallments();
		model.processWholeQueue();
		installments = model.getInstallments();
		
		System.out.println("Current budget after installments: " + model.getCurrentBudget());
		double totalOutcome = installmentDailyPayment * exchangeRate * numberOfDays + installmentDailyPayment * exchangeRate;
		assertEquals("Incorrect current budget after paying of installments",
				totalOutcome + dailyBudgetMoney, model.getCurrentBudget().get());
		
	}

	public void tearDown()
	{
		model.clearDatabaseInstance();
		BudgetFunctions.TESTING = false;
	}
	
	private void addDays(int n)
	{
		BudgetFunctions.theDate = HelperFunctions.addDays(BudgetFunctions.theDate, n);
	}

}
