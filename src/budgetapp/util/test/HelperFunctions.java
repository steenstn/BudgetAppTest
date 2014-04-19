package budgetapp.util.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HelperFunctions {
	
	
	/**
	 * Adds a number of days to a date string
	 * @param dateString - The date string to add to
	 * @param numOfDays - Number of days to add
	 * @return - The resulting date string
	 */
	public static String addDays(String dateString, int numOfDays)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.set(getYearOfDateString(dateString), getMonthOfDateString(dateString),
				getDayOfDateString(dateString), 0, 0);
		
		cal.add(Calendar.DATE, numOfDays);
		String newDate = dateFormat.format(cal.getTime());
		
		return newDate;
	}
	
	public static int getYearOfDateString(String dateString)
	{
		return Integer.parseInt(dateString.substring(0, 4));
	}
	
	public static int getMonthOfDateString(String dateString)
	{
		return Integer.parseInt(dateString.substring(5, 7))-1; // January = 0 in calendar
	}
	
	public static int getDayOfDateString(String dateString)
	{
		return Integer.parseInt(dateString.substring(8,10));
	}

}
