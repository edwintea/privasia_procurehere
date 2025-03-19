package com.privasia.procurehere.core.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.MutableDateTime;

public class DateUtil {

	private static final Logger LOG = LogManager.getLogger(DateUtil.class);

	public static Calendar setTime(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static Date getStartDateOfWeek(Date date) {
		Calendar calendarStartDate = DateUtil.setTime(date);
		Integer noOfDayOfWeek = calendarStartDate.get(Calendar.DAY_OF_WEEK) - 1;
		calendarStartDate.add(Calendar.DATE, -noOfDayOfWeek);
		// J.printPositif(noOfDayOfWeek+"..."+calendarStartDate.getTime());

		return calendarStartDate.getTime();
	}

	public static Date getStartTimeOfADate(Date date) {
		MutableDateTime d = new MutableDateTime(date);
		if (d.getHourOfDay() < 8) {
			d.setHourOfDay(8);
			d.setMinuteOfHour(0);
			d.setSecondOfMinute(0);
			return d.toDate();
		} else {
			return d.toDate();
		}
	}

	/**
	 * add time to date.
	 * 
	 * @param date
	 * @param time
	 * @return
	 */
	public static Date addTime(Date date, Time time) {
		Calendar calendar = setTime(date);
		calendar.add(Calendar.SECOND, time.getSecond());
		calendar.add(Calendar.MINUTE, time.getMinute());
		calendar.add(Calendar.HOUR_OF_DAY, time.getHour());
		return calendar.getTime();
	}

	/**
	 * add Hour to date.
	 * 
	 * @param date
	 * @param hour
	 * @return
	 */
	public static Date addHour(Date date, int hour) {
		Calendar calendar = setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		return calendar.getTime();
	}

	/**
	 * add Sec to date.
	 * 
	 * @param date
	 * @param min
	 * @return
	 */
	public static Date addMin(Date date, int min) {
		Calendar calendar = setTime(date);
		calendar.add(Calendar.MINUTE, min);
		return calendar.getTime();
	}

	/**
	 * add Sec to date.
	 * 
	 * @param date
	 * @param sec
	 * @return
	 */
	public static Date addSec(Date date, int sec) {
		Calendar calendar = setTime(date);
		calendar.add(Calendar.SECOND, sec);
		return calendar.getTime();
	}

	/**
	 * add millis to date.
	 * 
	 * @param date
	 * @param millis
	 * @return
	 */
	public static Date addMillis(Date date, long millis) {
		Calendar timeCal = Calendar.getInstance();
		long newDate = date.getTime() + millis;
		timeCal.setTimeInMillis(newDate);

		return timeCal.getTime();
	}

	public static Date addTime(Date date, Date time) {
		Calendar timeCal = setTime(time);

		int hour = timeCal.get(Calendar.HOUR_OF_DAY);
		int mins = timeCal.get(Calendar.MINUTE);
		int secs = timeCal.get(Calendar.SECOND);

		Calendar cal = setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, mins);
		cal.set(Calendar.SECOND, secs);

		return cal.getTime();
	}

	//
	// public static Date getExpDate(Date date, int month) {
	//
	// Calendar calendar = setTime(date);
	// calendar.add(Calendar.MONTH, month + 1);
	// calendar.set(Calendar.DAY_OF_MONTH, 1);
	// calendar.add(Calendar.DAY_OF_MONTH, -1);
	// return DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH);
	// }

	public static Date formatDateByTime(Date date, int hour, int minute, int second, int milisecond) {
		Calendar calendar = setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, milisecond);

		return calendar.getTime();
	}

	/**
	 * Max the time part for the specified date to 23:59:59
	 * 
	 * @param date the date part whose time has to be max'ed
	 * @return The max'ed date
	 */
	public static Date formatDateToEndTime(Date date) {
		return formatDateByTime(date, 23, 59, 59, 999);
	}

	/**
	 * @param date
	 * @return
	 */
	public static Date formatDateToStartTime(Date date) {
		return formatDateByTime(date, 0, 0, 0, 0);
	}

	/**
	 * Erase the time component from the specified date. Set all time fields to 00:00:00.
	 * 
	 * @param date the date from which the time component should be removed.
	 * @return the truncated date with its time component set to 00:00:00
	 */
	public static Date truncateTime(Date date) {
		return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
	}

	/**
	 * Return those date in dates which is between fromDate and toDate.
	 * 
	 * @return
	 */
	public static List<Date> getDateBetween(List<Date> dates, Date fromDate, Date toDate) {
		List<Date> retDateList = new ArrayList<Date>();

		for (Date date : dates) {
			if (fromDate.equals(date) || toDate.equals(date) || date.after(fromDate) && date.before(toDate)) {
				retDateList.add(date);
			}
		}

		if (retDateList.size() > 1) {
			Collections.sort(retDateList);
		}
		return retDateList;
	}

	/**
	 * Calculate Age base on date of birth until onDate.
	 * 
	 * @param dob
	 * @return
	 */
	public static int countAge(Date dob, Date onDate) {

		// Create a calendar object with today's date
		Calendar today = Calendar.getInstance();
		today.setTime(DateUtil.truncateTime(onDate));
		Calendar calendarDob = Calendar.getInstance();
		calendarDob.setTime(DateUtil.truncateTime(dob));

		// Get age based on year
		int age = today.get(Calendar.YEAR) - calendarDob.get(Calendar.YEAR);

		// Add the tentative age to the date of birth to get this year's
		// birthday
		calendarDob.add(Calendar.YEAR, age);

		// If this year's birthday has not happened yet, subtract one from age
		if (today.before(calendarDob)) {
			age--;
		}
		return age;
	}

	/**
	 * Not Consider time. Just compare date.
	 * 
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isBetweenDate(Date date, Date startDate, Date endDate) {
		Date clonedDate = (Date) date.clone();
		clonedDate = formatDateByTime(clonedDate, 0, 0, 0, 0);

		Date clonedStartDate = (Date) startDate.clone();
		Date clonedEndDate = (Date) endDate.clone();

		clonedStartDate = formatDateByTime(clonedStartDate, 0, 0, 0, 0);
		clonedEndDate = formatDateByTime(clonedEndDate, 23, 59, 59, 999);

		return (clonedDate.equals(clonedStartDate) || clonedDate.after(clonedStartDate)) && (clonedDate.equals(clonedEndDate) || clonedDate.before(clonedEndDate));
	}

	/**
	 * consider time and date.
	 * 
	 * @param date
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static boolean isBetweenDateTime(Date date, Date startDate, Date endDate) {
		return (date.equals(startDate) || date.after(startDate)) && (date.equals(endDate) || date.before(endDate));
	}

	public static boolean isSaturday(Date date) {
		Calendar cal = setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
	}

	public static boolean isSunday(Date date) {
		Calendar cal = setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public static Date addDate(Date date, int noOfDays) {
		Calendar cal = setTime(date);
		cal.add(Calendar.DATE, noOfDays);
		return cal.getTime();
	}

	public static long getDaysBetween2Dates(Date dateFrom, Date DateTo) {
		long msDateFrom = DateUtil.truncateTime(dateFrom).getTime();
		long msDateTo = DateUtil.truncateTime(DateTo).getTime();

		return (msDateTo - msDateFrom) / (24 * 3600 * 1000) + 1;
	}

	/**
	 * @param year
	 * @param month 0-11
	 * @return
	 */
	public static List<String> getDayInWeekForMonthIn2Characters(int year, int month) {
		SimpleDateFormat df = new SimpleDateFormat("EE");
		List<String> list = new ArrayList<String>();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, 1);

		while (cal.get(Calendar.MONTH) == month) {
			list.add(df.format(cal.getTime()));

			cal.add(Calendar.DATE, 1);
		}

		return list;
	}

	public static List<String> listMonthIn3Characters() {
		SimpleDateFormat df = new SimpleDateFormat("MMM");
		List<String> list = new ArrayList<String>();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, 0);

		while (cal.get(Calendar.MONTH) < 12) {
			list.add(df.format(cal.getTime()));

			if (cal.get(Calendar.MONTH) == 11)
				break;
			cal.add(Calendar.MONTH, 1);
		}

		return list;
	}

	/**
	 * @param later to set later date
	 * @param earlier to set earlier date
	 * @return later - earlier in hours.
	 */
	public static int getDiffHoursInInteger(Date later, Date earlier) {
		long dateInLong = later.getTime();
		long withInLong = earlier.getTime();
		return (int) (((double) dateInLong - (double) withInLong) / (double) (60 * 60 * 1000));
	}

	/**
	 * @param later to set later date
	 * @param earlier to set earlier date
	 * @return later - earlier in hours.
	 */
	public static double getDiffHoursInDouble(Date later, Date earlier) {
		long dateInLong = later.getTime();
		long withInLong = earlier.getTime();
		return (((double) dateInLong - (double) withInLong) / (double) (60 * 60 * 1000));
	}

	/**
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static double differenceInMilliseconds(Date date1, Date date2) {
		return Math.abs(getTimeInMilliseconds(date1) - getTimeInMilliseconds(date2));
	}

	/**
	 * @param date
	 * @return
	 */
	public static long getTimeInMilliseconds(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());
	}

	/**
	 * Converts time in milliseconds to a <code>String</code> in the format HH:mm:ss.SSS.
	 * 
	 * @param time the time in milliseconds.
	 * @return a <code>String</code> representing the time in the format HH:mm:ss.SSS.
	 */
	public static String millisecondsToString(final long time) {
		try {
			// int milliseconds = (int) (time % 1000);
			int seconds = (int) ((time / 1000) % 60);
			int minutes = (int) ((time / 60000) % 60);
			int hours = (int) ((time / 3600000) % 24);
			int days = (int) ((time / 3600000) / 24);
			hours += (days * 24);

			// String millisecondsStr = (milliseconds < 10 ? "00" : (milliseconds < 100 ? "0" : "")) + milliseconds;
			String secondsStr = ((seconds < 10) ? "0" : "") + seconds;
			String minutesStr = ((minutes < 10) ? "0" : "") + minutes;
			String hoursStr = ((hours < 10) ? "0" : "") + hours;

			return new String(hoursStr + ":" + minutesStr + ":" + secondsStr);// + ":" + millisecondsStr);
		} catch (Exception e) {
			// e.printStackTrace();
		}

		return "";
	}

	/**
	 * Construct a year list.
	 * 
	 * @return List of {@link String}
	 */
	public static List<String> getYearList() {
		int total = 10;
		List<String> yearList = new ArrayList<String>();

		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);
		for (int i = 10; i >= 1; i--) {
			String value = Integer.toString(currentYear - i);
			yearList.add(value);
		}
		for (int i = 0; i <= total; i++) {
			String value = Integer.toString(currentYear + i);
			yearList.add(value);
		}

		return yearList;
	}

	/**
	 * @return
	 */
	public static Date cosmosDateTime(String datetime) {
		Date date = null;
		try {
			if (StringUtils.checkString(datetime).length() == 0)
				return null;

			String datetimes[] = datetime.split(" ");
			String dateStr = datetimes[0];
			String time = datetimes[1];

			// return null if date is 0
			if (Integer.parseInt(dateStr.trim()) == 0)
				return null;

			time = paddingString(time, 6, '0', true);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");
			date = df.parse(dateStr + " " + time);

		} catch (Exception e) {
		}
		return date;
	}

	/**
	 * Pads a String <code>s</code> to take up <code>n</code> characters, padding with char <code>c</code> on the left (
	 * <code>true</code>) or on the right (<code>false</code>). Returns <code>null</code> if passed a <code>null</code>
	 * String.
	 **/
	public static String paddingString(String s, int n, char c, boolean paddingLeft) {
		if (s == null) {
			return s;
		}
		int add = n - s.length(); // may overflow int size... should not be a
		// problem in real life
		if (add <= 0) {
			return s;
		}
		StringBuffer str = new StringBuffer(s);
		char[] ch = new char[add];
		Arrays.fill(ch, c);
		if (paddingLeft) {
			str.insert(0, ch);
		} else {
			str.append(ch);
		}
		return str.toString();
	}

	/**
	 * 
	 */
	public static int getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.get(Calendar.YEAR);
	}

	public static boolean sameDate(Date src, Date dst) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
		return simpleDateFormat.format(src).equalsIgnoreCase(simpleDateFormat.format(dst));
	}

	/**
	 * From Date - to Date In Minutes
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static double difference(Date fromDate, Date toDate) {
		if (fromDate == null || toDate == null)
			return 0.0;
		double diff = (double) (fromDate.getTime() - toDate.getTime());
		double diffMinutes = diff / (60 * 60 * 1000);
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(diffMinutes));
	}

	/**
	 * From Date - to Date In Days
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static double differenceInDays(Date fromDate, Date toDate) {
		if (fromDate == null || toDate == null)
			return 0.0;
		double diff = (double) (fromDate.getTime() - toDate.getTime());
		double diffMinutes = diff / (24 * 60 * 60 * 1000);
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.valueOf(df.format(diffMinutes));
	}
	
	/**
	 * @param date
	 */
	public static String getDateAsString(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		return df.format(date);
	}

	public static XMLGregorianCalendar getXMLGregorianCalendar(Date d) {
		if (d == null)
			return null;

		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(d);
		XMLGregorianCalendar xmlGregorianCalendar = null;
		try {
			xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return xmlGregorianCalendar;
	}

	public static Date getDateFromXMLGregorianCalendar(XMLGregorianCalendar xmlGregorianCalendar) {

		if (xmlGregorianCalendar == null) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			Date d1 = simpleDateFormat.parse(xmlGregorianCalendar.getDay() + "/" + xmlGregorianCalendar.getMonth() + "/" + xmlGregorianCalendar.getYear() + " " + xmlGregorianCalendar.getHour() + ":" + xmlGregorianCalendar.getMinute() + ":" + xmlGregorianCalendar.getSecond());
			return d1;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Date[] getFromAndToDate(int month) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.set(Calendar.MONTH, month);
		calendar1.set(Calendar.DAY_OF_MONTH, 1);
		calendar1.setTime(DateUtil.formatDateToStartTime(calendar1.getTime()));
		Date from = calendar1.getTime();
		Calendar calendar2 = (Calendar) calendar1.clone();
		calendar2.add(Calendar.MONTH, 1);
		calendar2.add(Calendar.DATE, -1);
		calendar2.setTime(DateUtil.formatDateToEndTime(calendar2.getTime()));
		Date to = calendar2.getTime();

		return new Date[] { from, to };
	}

	public static List<String> getRoasterPlanYearList() {
		int total = 10;
		List<String> yearList = new ArrayList<String>();

		Calendar cal = Calendar.getInstance();
		int currentYear = cal.get(Calendar.YEAR);
		for (int i = 4; i >= 1; i--) {
			String value = Integer.toString(currentYear - i);
			yearList.add(value);
		}
		for (int i = 0; i <= total; i++) {
			String value = Integer.toString(currentYear + i);
			yearList.add(value);
		}

		return yearList;
	}

	public static String getDateTimeAsString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		return format.format(date);
	}

	public static Date combineDateTime(Date appointmentDateTime, Date appointmentTime, TimeZone timeZone) {

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		df.setTimeZone(timeZone);
		SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
		tf.setTimeZone(timeZone);
		SimpleDateFormat ff = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		ff.setTimeZone(timeZone);
		Date finalDate = null;
		try {
			finalDate = ff.parse(df.format(appointmentDateTime) + " " + tf.format(appointmentTime));
		} catch (ParseException e) {
			LOG.error("Error combining Date Time - Values " + appointmentDateTime + "," + appointmentTime + " - Error : " + e.getMessage(), e);
		}
		return finalDate;
	}

	public static long getDiffHoursInLong(Date approvalDate, Date submittedDate) {
		long approvalTime = approvalDate.getTime();
		long submittedTime = submittedDate.getTime();
		return ((approvalTime - submittedTime) / (60 * 60 * 1000));
	}

	public static int getDiffMinInInteger(Date later, Date earlier) {
		return (int) ((later.getTime() - earlier.getTime()) / (60 * 1000));
	}

	
	public static void main(String[] args) {
		
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Date startDate = df.parse("2022-05-28 09:30:00");
			Date endDate = df.parse("2023-06-02 09:30:00");
			
			Calendar start = Calendar.getInstance();
			start.setTime(startDate);
			
			Calendar end = Calendar.getInstance();
			end.setTime(endDate);
			System.out.println(getDiffMinInInteger(end.getTime(), start.getTime()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
