package budgetapp.main.test;

import android.test.AndroidTestCase;
import budgetapp.util.money.Money;
import budgetapp.util.money.MoneyFactory;

public class MoneyTest extends AndroidTestCase{
	
	public void setUp()
	{
		Money.setExchangeRate(1);
	}
	
	public void testAddingMoney()
	{
		double value1 = 10;
		double value2 = 20;
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = m1.add(m2);
		
		double resultDouble = value1 + value2;
		Money expectedResult =  MoneyFactory.createMoneyFromNewDouble(resultDouble);
		
		assertEquals("Not correct result.", resultDouble, result.get());
		}
	
	public void testAddingMoneyHighExchangeRate()
	{
		double value1 = 10;
		double value2 = 10;
		double exchangeRate = 2;
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money.setExchangeRate(exchangeRate);
		Money result = m1.add(m2);
		
		
		double resultDouble = (value1 + value2);
		assertEquals("Not correct result.", resultDouble, result.get());
		}
	
	public void testAddingMoneyHighExchangeRate2()
	{
		double value1 = 10;
		double value2 = 10;
		double exchangeRate = 2;
		Money.setExchangeRate(exchangeRate);
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = m1.add(m2);
		
		
		double resultDouble = (value1 + value2) * exchangeRate;
		
		assertEquals("Not correct result.", resultDouble, result.get());
	}
	
	public void testAddingMoneyLowExchangeRate()
	{
		double value1 = 10;
		double value2 = 20;
		double exchangeRate = 0.1;
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money.setExchangeRate(exchangeRate);
		Money result = m1.add(m2);
		

		double resultDouble = (value1 + value2);
		assertEquals("Not correct result.", resultDouble, result.get());
		}
	
	public void testAddingMoneyLowExchangeRate2()
	{
		double value1 = 10;
		double value2 = 20;
		double exchangeRate = 0.1;
		Money.setExchangeRate(exchangeRate);
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = m1.add(m2);

		
		double resultDouble = (value1 + value2) * exchangeRate;
		assertEquals("Not correct result.", resultDouble, result.get());
	}
	
	public void testSubractMoney()
	{
		double value1 = 30;
		double value2 = 20;
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = m1.subtract(m2);
		
		
		double resultDouble = value1 - value2;
		assertEquals("Not correct result.", resultDouble, result.get());
	}
	
	public void testSubtractMoneyHighExchangeRate()
	{
		double value1 = 30;
		double value2 = 20;
		double exchangeRate = 2;
		Money.setExchangeRate(exchangeRate);
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = m1.subtract(m2);
		Money.setExchangeRate(1);
		
		
		double resultDouble = (value1 - value2) * exchangeRate;
		assertEquals("Not correct result.", resultDouble, result.get());
	}
	
	public void testDivideMoney()
	{
		double value1 = 100;
		double value2 = 20;
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = new Money(m1.divide(m2));
		
		
		double resultDouble = value1 / value2;
		assertEquals("Not correct result.", resultDouble, result.get());
	}
	
	public void testDivideMoneyHighExchangeRate()
	{
		double value1 = 100;
		double value2 = 20;
		double exchangeRate = 2;
		Money.setExchangeRate(exchangeRate);
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = new Money(m1.divide(m2));
		Money.setExchangeRate(1);
		
		
		double resultDouble = (value1 * exchangeRate) / (value2 * exchangeRate);
		assertEquals("Not correct result.", resultDouble, result.get());
	}
	
	public void testDivideMoneyLowExchangeRate()
	{
		double value1 = 100;
		double value2 = 20;
		double exchangeRate = 0.1;
		Money.setExchangeRate(exchangeRate);
		
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = new Money(m1.divide(m2));
		Money.setExchangeRate(1);
		
		
		double resultDouble = (value1 * exchangeRate) / (value2 * exchangeRate);
		assertEquals("Not correct result.", resultDouble, result.get());
	}
	
	public void testMultiplyMoneyHighExchangeRate()
	{
		double value1 = 1;
		double value2 = 2;
		double exchangeRate = 2;

		Money.setExchangeRate(exchangeRate);
		Money m1 = MoneyFactory.createMoneyFromNewDouble(value1);
		Money m2 = MoneyFactory.createMoneyFromNewDouble(value2);
		
		Money result = m1.multiply(m2);
		Money.setExchangeRate(1);
		
		
		double resultDouble = value1 * exchangeRate * value2 * exchangeRate;
		assertEquals("Not correct result.", resultDouble, result.get());
	}
	
	public void testMakePositive()
	{
		Money m1 = MoneyFactory.createMoneyFromNewDouble(-100);
		assertEquals("Changing negative not equal", 100.0, m1.makePositive().get());
		
		Money m2 = MoneyFactory.createMoneyFromNewDouble(100);
		assertEquals("Changing positive not equal", 100.0, m2.makePositive().get());
	}
	
	public void testMakeNegative()
	{
		Money m1 = MoneyFactory.createMoneyFromNewDouble(100);
		assertEquals("Changing positive not equal", -100.0, m1.makeNegative().get());
		
		Money m2 = MoneyFactory.createMoneyFromNewDouble(-100);
		assertEquals("Changing negative not equal", -100.0, m2.makeNegative().get());
	}
	public void testMoneyClone()
	{
		Money.setExchangeRate(2);
		Money m1 = MoneyFactory.createMoneyFromNewDouble(100);
		Money m2 = new Money(m1);
		
		assertEquals("Not equal",m1.get(),m2.get());
	}
	
	public void testEmptyMoney() {
		Money m = MoneyFactory.createMoney();
		assertEquals("Not zero",m.get(), 0.0);
	}

	public void tearDown()
	{
		Money.setExchangeRate(1);
	}

}
