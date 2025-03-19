/**
 * 
 */
package com.privasia.procurehere.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.privasia.procurehere.service.impl.BuyerSubscriptionServiceImpl;

/**
 * @author parveen
 */
public class SubscriptionTest {

	public static void main(String[] args) {
		Calendar start = Calendar.getInstance();
		System.out.println("Start : " + start.getTime());
		Calendar end = Calendar.getInstance();
		start.set(Calendar.MONTH, Calendar.SEPTEMBER);
		// start.set(Calendar.YEAR, 2016);
		// start.setTime(new Date());
		// start.add(Calendar.DATE, -1);
		// start.set(Calendar.HOUR, 11);
		// start.set(Calendar.MINUTE, 59);
		// start.set(Calendar.SECOND, 59);
		// start.set(Calendar.MILLISECOND, 59);

		// start.add(Calendar.MONTH, 1);

		end.set(Calendar.MONTH, Calendar.OCTOBER);
		end.set(Calendar.DATE, 12);
		// end.set(Calendar.HOUR, 11);
		// end.set(Calendar.MINUTE, 59);
		// end.set(Calendar.SECOND, 59);
		// end.set(Calendar.MILLISECOND, 59);
		//
		// end.set(Calendar.YEAR, 2018);

		System.out.println("Start : " + start.getTime());
		System.out.println("End : " + end.getTime());
		System.out.println("Chargable months : " + new BuyerSubscriptionServiceImpl().calculateChargeableMonths(start.getTime(), end.getTime()));
		// testTimeZone();
		try {
			// zodaTimeZoneTest();
			ZonedTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// not working ufor time Zone
	public static void testTimeZone() {
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a z");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a z");
		String timeZone = "GMT+08:00";
		// Calendar start1 = Calendar.getInstance();
		Calendar start1 = Calendar.getInstance();
		// start1.set(Calendar.HOUR, 0);
		// start1.set(Calendar.MINUTE, 0);
		// start1.set(Calendar.SECOND, 0);
		// start1.set(Calendar.MILLISECOND, 0);
		System.out.println("1 : " + start1.getTime().getTime());
		// System.out.println("0 "+df.format(start1.getTime()) + " - " + start1.getTimeZone());
		// start1.setTimeZone(TimeZone.getTimeZone(timeZone));
		System.out.println("2 : " + start1.getTime().getTime());
		/*
		 * start1.setTime(new Date());
		 * //change the timezone
		 * start1.set(Calendar.HOUR, 0);
		 * start1.set(Calendar.MINUTE, 0);
		 * start1.set(Calendar.SECOND, 0);
		 * start1.set(Calendar.MILLISECOND, 0);
		 * start1.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
		 * // Default time zone for new Buyer
		 * // start.setTimeZone(TimeZone.getTimeZone(timeZone));
		 */
		df1.setTimeZone(TimeZone.getTimeZone(timeZone));

		String userTime = df1.format(start1.getTime());

		Calendar end = Calendar.getInstance();
		end.set(Calendar.HOUR, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);
		// end.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));

		try {
			start1.setTime(df1.parse(userTime));
			System.out.println(start1.compareTo(end));
			System.out.println("1 " + df1.format(start1.getTime()));
			System.out.println("2 " + df2.format(end.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Working fine
	public static void zodaTimeZoneTest() throws ParseException {

		String DATE_FORMAT = "dd-M-yyyy hh:mm:ss a";

		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

		String dateInString = "22-01-2015 10:15:55 AM";
		Date date = formatter.parse(dateInString);
		TimeZone tz = TimeZone.getDefault();

		// From TimeZone Asia/Singapore
		System.out.println("TimeZone : " + tz.getID() + " - " + tz.getDisplayName());
		System.out.println("TimeZone : " + tz);
		System.out.println("Date (Singapore) : " + formatter.format(date));

		// To TimeZone America/New_York
		SimpleDateFormat sdfMalysia = new SimpleDateFormat(DATE_FORMAT);
		DateTime dt = new DateTime(date);
		DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		// .forID("Asia/Kuala_Lumpur");
		DateTime dtus = dt.withZone(dtZone);
		TimeZone tzInMalaysia = dtZone.toTimeZone();
		Date dateInMalaysia = dtus.toLocalDateTime().toDate(); // Convert to LocalDateTime first

		sdfMalysia.setTimeZone(tzInMalaysia);

		System.out.println("\nTimeZone : " + tzInMalaysia.getID() + " - " + tzInMalaysia.getDisplayName());
		System.out.println("TimeZone : " + tzInMalaysia);
		System.out.println("DateTimeZone : " + dtZone);
		System.out.println("DateTime : " + dtus);

		System.out.println("dateInAmerica (Formatter) : " + formatter.format(dateInMalaysia));
		System.out.println("dateInAmerica (Object) : " + dateInMalaysia);

	}

	public static void ZonedTime() {
		final String DATE_FORMAT = "dd-M-yyyy hh:mm:ss a";
		DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);
	
		String dateInString = "22-1-2015 10:15:55 AM";
		LocalDateTime ldt = LocalDateTime.now();// LocalDateTime.parse(dateInString,
												// DateTimeFormatter.ofPattern(DATE_FORMAT));
	
		ZoneId utc = ZoneId.of("UTC");
		System.out.println("TimeZone : " + utc + " Date : " + ldt.format(format));
		ZonedDateTime asiaZonedDateTime = ldt.atZone(utc);
	
		SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a z");
		df1.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
	
		System.out.println("NOW >>>>>>>>>>> : " + df1.format(new Date()));
		
		//
		// //LocalDateTime + ZoneId = ZonedDateTime
		// ZonedDateTime asiaZonedDateTime = ldt.atZone(singaporeZoneId);
		// System.out.println("Date (Singapore) : " + asiaZonedDateTime);
	
		ZoneId buyerTimeZone = ZoneId.of("GMT+05:30");
		ZonedDateTime buyerZoneDateTime = asiaZonedDateTime.withZoneSameInstant(buyerTimeZone);
		System.out.println("Date (MAL) : " + buyerZoneDateTime.toInstant());
		System.out.println("In Date obj (MAL) : " + Date.from(buyerZoneDateTime.toInstant()));
	
		System.out.println("\n---DateTimeFormatter---");
		System.out.println("Date (Singapore) : " + format.format(buyerZoneDateTime));
		System.out.println("Date (MAL) : " + format.format(buyerZoneDateTime));
	
	}
	
	

}
