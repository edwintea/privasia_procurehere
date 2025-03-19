package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.pojo.OwnerDashboardPojo;
import com.privasia.procurehere.core.pojo.StatisticsPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.YearStatisticsPojo;
import com.privasia.procurehere.core.pojo.YearStatisticsValuePojo;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.OwnerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.OwnerDashboardService;

@Service
@Transactional(readOnly = true)
public class OwnerDashboardServiceImpl implements OwnerDashboardService {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	BuyerService buyerService;

	@Autowired(required = true)
	BuyerDao buyerDao;

	@Autowired(required = true)
	OwnerDao ownerDao;

	@Autowired(required = true)
	SupplierDao supplierDao;

	@Autowired
	CountryService countryService;

	@Override
	public OwnerDashboardPojo findAllBuyerAndSupplierStatus(Country country) {
		return ownerDao.findBuyerAndSupplierStatus(country);
	}

	@Override
	public int countAllCurrentBuyersOnTrial(Country country) {
		return ownerDao.countCurrentBuyersOnTrial(country);
	}

	@Override
	public int countAllTotalRegisteredBuyers(Country country) {
		return ownerDao.countTotalRegisteredBuyers(country);
	}

	@Override
	public int countAllRegisteredBuyersForDateRange(Date stime, Date etime, Country country) {
		return ownerDao.countRegisteredBuyersForDateRange(stime, etime, country);
	}

	@Override
	public List<Buyer> findListOfAllBuyerForDateRange(Date start, Date end, Country country, TableDataInput input) {
//		List<Buyer> buyer = buyerDao.findListOfBuyerForDateRange(start, end, country, input);
//		if (CollectionUtil.isNotEmpty(buyer)) {
//			for (Buyer b1 : buyer) {
//				List<Country> c1 = countryService.findAllActiveCountries();
//				for (Country country2 : c1) {
//					if (country2.getCountryName() != null) {
//						b1.setRegistrationCountryName(country2.getCountryName());
//					}
//				}
//			}
//		}

		return buyerDao.findListOfBuyerForDateRange(start, end, country, input);
	}

	@Override
	public List<SupplierPojo> findListOfAllSupplierForDateRange(Date start, Date end, Country country) {
		return supplierDao.findListOfSupplierForDateRange(start, end, country);
	}

	@Override
	public int countAllCurrentBuyersOnTrial(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findListOfBuyersOnTrial(sDate, eDate, regCountry);
	}

	@Override
	public int countAllBuyerConvesionRate(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findBuyerConvesionRate(sDate, eDate, regCountry);
	}

	@Override
	public int countAllCurrentBuyersSuspended(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findListOfBuyersSuspended(sDate, eDate, regCountry);
	}

	@Override
	public int countAllCurrentBuyersActive(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findListOfBuyersActive(sDate, eDate, regCountry);
	}

	@Override
	public int countAllNewBuyers(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findListOfNewBuyer(sDate, eDate, regCountry);
	}

	@Override
	public int countAllEventCancelled(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllCancelledEvents(sDate, eDate, regCountry);
	}

	@Override
	public int countAllEventFinished(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllTotalEvents(sDate, eDate, regCountry);
	}

	@Override
	public int countAllTotalPr(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllTotalPr(sDate, eDate, regCountry);
	}

	@Override
	public int countAllTotalPo(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllTotalPo(sDate, eDate, regCountry);
	}

	@Override
	public int countAllTotalSupplier(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllTotalSupplier(sDate, eDate, regCountry);
	}

	@Override
	public int countAllTotalBuyer(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllTotalBuyer(sDate, eDate, regCountry);
	}

	@Override
	public int countAllFailedPaymentsTransaction(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllFailedPayments(sDate, eDate, regCountry);
	}

	@Override
	public int countAllRevenueGenerated(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllRevenueGenerated(sDate, eDate, regCountry);
	}

	@Override
	public double countAllEventPerCategory(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findEventPerCategory(sDate, eDate, regCountry);
	}

	@Override
	public double countAllAverageTimeEvent(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAllAverageTimeEvent(sDate, eDate, regCountry);
	}

	@Override
	public double countRfaTotalAuctionSaving(Date sDate, Date eDate, Country regCountry) {
		LOG.info(" :count Total Auction Saving :");
		return ownerDao.countRfaTotalAuctionSaving(sDate, eDate, regCountry);
	}

	@Override
	public double countRfaAverageAuctionSaving(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.countRfaAverageAuctionSaving(sDate, eDate, regCountry);
	}

	@Override
	public Map<String, String> getAllNewBuyerBreakups(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAndCountNewBuyerBreakup(sDate, eDate, regCountry);
	}

	@Override
	public Map<String, String> getAllTotalBuyerBreakups(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAndCountTotalBuyerBreakup(sDate, eDate, regCountry);
	}

	@Override
	public Map<String, String> getSuspendedBuyerBreakups(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAndCountSuspendedBuyerBreakup(sDate, eDate, regCountry);
	}

	@Override
	public Map<String, String> getRevenuePlanBreakups(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAndCountRevenuePlanBreakup(sDate, eDate, regCountry);
	}

	@Override
	public Map<String, String> getConversionPlanBreakups(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAndCountConversionPlanBreakup(sDate, eDate, regCountry);
	}

	@Override
	public Map<String, String> getPerWeekEventBreakups(Date sDate, Date eDate, Country regCountry) {
		return ownerDao.findAndCountPerWeekEventPlanBreakup(sDate, eDate, regCountry);
	}

	@Override
	public Map<String, String> getTotalAuctionSavingBreakup(Date sDate, Date eDate, Country regCountry) {
		LOG.info(" AuctionSavingBreakup Service Impl");
		return ownerDao.findTotalAuctionSavingBreakup(sDate, eDate, regCountry);
	}

	@Override
	public Map<String, Double> getBuyerRevenueByWeek(Date effectiveStartDate, int weekCount, Country country, boolean excludeFees) {
		Map<String, Double> weekMap = new HashMap<String, Double>();
		SimpleDateFormat df = new SimpleDateFormat("'Week' w '(' dd/MM/YYYY ')' ");

		// Set the user requested effective current date
		Calendar c = Calendar.getInstance();
		c.setTime(effectiveStartDate);

		// take it back to start of Week if its in the middle/end of the week
		int weekday = c.get(Calendar.DAY_OF_WEEK);
		weekday--; // set it to zero if Sunday

		c.add(Calendar.DATE, -weekday);

		// Fetch the stats for the requested number of weeks
		for (int i = 0; i < weekCount; i++) {
			Date startDate = c.getTime();

			// Add 7 days to get the end of the week
			c.add(Calendar.DATE, 7);
			Date endDate = c.getTime();

			// Get the stats for the week

			double volume = ownerDao.findSumOfBuyerRevenueCurrentWeek(startDate, endDate, country, excludeFees);

			weekMap.put(df.format(startDate), volume);
			// go to previous week
			c.add(Calendar.DATE, -7);
			c.add(Calendar.WEEK_OF_YEAR, -1);
		}

		return weekMap;
	}

	@Override
	public Map<String, Double> getBuyerRevenueByMonth(Date effectiveStartDate, int monthCount, Country country, boolean excludeFees) {
		Map<String, Double> monthMap = new HashMap<String, Double>();
		SimpleDateFormat df = new SimpleDateFormat("MMM yyyy");

		// Set the user requested effective current date
		Calendar c = Calendar.getInstance();
		c.setTime(effectiveStartDate);

		// Set First Day Of Month
		c.set(Calendar.DATE, 1);

		// Fetch the stats for the requested number of month
		for (int i = 0; i < monthCount; i++) {
			Date startDate = c.getTime();

			c.add(Calendar.DATE, c.getActualMaximum(Calendar.DATE));

			c.set(Calendar.DAY_OF_MONTH, 1);
			c.add(Calendar.DATE, -1);

			Date endDate = c.getTime();

			// Get the stats for the month

			double volume = ownerDao.findSumOfBuyerRevenueForMonth(startDate, endDate, country, excludeFees);

			monthMap.put(df.format(startDate), volume);
			// go to previous month
			c.set(Calendar.DATE, 1);
			c.add(Calendar.MONTH, -1);
		}

		return monthMap;
	}

	@Override
	public List<StatisticsPojo> getBuyerRevenueForQuaterly(Date effectiveStartDate, int quarterCount, Country country, boolean excludeFees) {
		List<StatisticsPojo> quatStats = new ArrayList<StatisticsPojo>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		SimpleDateFormat mf = new SimpleDateFormat("MMM yyyy");

		// Set the user requested effective current date
		Calendar c = Calendar.getInstance();
		c.setTime(effectiveStartDate);

		// Fetch the stats for the requested number of Quarter
		for (int i = 0; i < quarterCount; i++) {

			StatisticsPojo q = new StatisticsPojo();

			int quarter = (c.get(Calendar.MONTH) / 3) + 1;

			c.set(Calendar.MONTH, ((quarter - 1) * 3));
			c.set(Calendar.DATE, 1);
			Date startDate = c.getTime();

			Calendar monthly = (Calendar) c.clone();
			LOG.info("  startDate  :" + startDate);

			// Go to end of quarter last day
			c.add(Calendar.MONTH, 2);
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = c.getTime();
			LOG.info("  endDate  " + endDate);

			// Get the stats for the quater
			double qVolume = ownerDao.findRevenueForDates(startDate, endDate, country, excludeFees);

			LOG.info("  qVolume   :" + qVolume);

			q.setLabel("Q" + quarter + "-" + df.format(startDate));
			q.setValue(qVolume);

			for (int month = 1; month <= 3; month++) {
				startDate = monthly.getTime();
				monthly.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				endDate = monthly.getTime();

				// get Volume for Month()
				double mVolume = ownerDao.findRevenueForDates(startDate, endDate, country, excludeFees);

				LOG.info("  mVolume   :" + mVolume);

				q.getbData().put(mf.format(startDate), mVolume);

				monthly.add(Calendar.DATE, 1);
			}
			c.set(Calendar.DATE, 1);
			c.add(Calendar.MONTH, -3);
			quatStats.add(q);
		}

		LOG.info(" quatStats :" + quatStats);
		return quatStats;
	}

	@Override
	public List<YearStatisticsPojo> getBuyerRevenueHalfYearly(Date effectiveStartDate, int yearCount, Country regCountry, boolean excludeFees) {
		List<YearStatisticsPojo> yearStats = new ArrayList<YearStatisticsPojo>();

		SimpleDateFormat df = new SimpleDateFormat("yyyy");

		// Set the user requested effective current date
		Calendar c = Calendar.getInstance();
		c.setTime(effectiveStartDate);

		// Fetch the stats for the requested number of Year
		for (int i = 0; i < yearCount; i++) {

			YearStatisticsPojo q = new YearStatisticsPojo();

			c.set(Calendar.MONTH, Calendar.JANUARY);
			c.set(Calendar.DATE, 1);

			Date startDate = c.getTime();

			Calendar monthly = (Calendar) c.clone();

			LOG.info("Start Date" + startDate);

			c.set(Calendar.MONTH, Calendar.DECEMBER);
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = c.getTime();
			LOG.info(" End date" + c.getTime());

			q.setLabel(df.format(startDate));

			List<YearStatisticsValuePojo> yearval = new ArrayList<YearStatisticsValuePojo>();

			for (int halfYear = 1; halfYear <= 2; halfYear++) {
				YearStatisticsValuePojo valpojo = new YearStatisticsValuePojo();
				startDate = monthly.getTime();
				c.add(Calendar.MONTH, 5);
				monthly.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				endDate = monthly.getTime();

				// get Volume for Month()
				double mVolume = ownerDao.findRevenueForDates(startDate, endDate, regCountry, excludeFees);

				LOG.info("  mVolume   :" + mVolume);

				valpojo.setValue1(mVolume);
				valpojo.setValue2(mVolume);

				yearval.add(valpojo);

				q.setValuePojo(valpojo);

				monthly.add(Calendar.DATE, 1);

				c.set(Calendar.DATE, 1);
			}

			c.add(Calendar.YEAR, -2);
			yearStats.add(q);

		}
		return yearStats;
	}

	@Override
	public Map<String, Double> getBuyerSubscriptionByWeek(Date effectiveStartDate, int weekCount, Country country, boolean excludeFees) {
		Map<String, Double> weekMap = new HashMap<String, Double>();
		SimpleDateFormat df = new SimpleDateFormat("'Week' w '(' dd/MM/YYYY ')' ");

		// Set the user requested effective current date
		Calendar c = Calendar.getInstance();
		c.setTime(effectiveStartDate);

		// take it back to start of Week if its in the middle/end of the week
		int weekday = c.get(Calendar.DAY_OF_WEEK);
		weekday--; // set it to zero if Sunday

		c.add(Calendar.DATE, -weekday);

		// Fetch the stats for the requested number of weeks
		for (int i = 0; i < weekCount; i++) {
			Date startDate = c.getTime();

			// Add 7 days to get the end of the week
			c.add(Calendar.DATE, 7);
			Date endDate = c.getTime();

			// Get the stats for the week

			double volume = ownerDao.findCountOfBuyerSubscriptionCurrentWeek(startDate, endDate, country, excludeFees);

			weekMap.put(df.format(startDate), volume);
			// go to previous week
			c.add(Calendar.DATE, -7);
			c.add(Calendar.WEEK_OF_YEAR, -1);
		}

		return weekMap;
	}

	@Override
	public Map<String, Double> getBuyerSubscriptionByMonth(Date effectiveStartDate, int monthCount, Country country, boolean excludeFees) {
		Map<String, Double> monthMap = new HashMap<String, Double>();
		SimpleDateFormat df = new SimpleDateFormat("MMM yyyy");

		// Set the user requested effective current date
		Calendar c = Calendar.getInstance();
		c.setTime(effectiveStartDate);

		// Set First Day Of Month
		c.set(Calendar.DATE, 1);

		// Fetch the stats for the requested number of month
		for (int i = 0; i < monthCount; i++) {
			Date startDate = c.getTime();

			c.add(Calendar.DATE, c.getActualMaximum(Calendar.DATE));

			c.set(Calendar.DAY_OF_MONTH, 1);
			c.add(Calendar.DATE, -1);

			Date endDate = c.getTime();

			// Get the stats for the month

			double volume = ownerDao.findCountOfBuyerSubscriptionForMonth(startDate, endDate, country, excludeFees);

			monthMap.put(df.format(startDate), volume);
			// go to previous month
			c.set(Calendar.DATE, 1);
			c.add(Calendar.MONTH, -1);
		}

		return monthMap;
	}

	@Override
	public List<StatisticsPojo> getBuyerSubscriptionForQuaterly(Date effectiveStartDate, int quarterCount, Country country, boolean excludeFees) {
		List<StatisticsPojo> quatStats = new ArrayList<StatisticsPojo>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		SimpleDateFormat mf = new SimpleDateFormat("MMM yyyy");

		// Set the user requested effective current date
		Calendar c = Calendar.getInstance();
		c.setTime(effectiveStartDate);

		// Fetch the stats for the requested number of Quarter
		for (int i = 0; i < quarterCount; i++) {

			StatisticsPojo q = new StatisticsPojo();

			int quarter = (c.get(Calendar.MONTH) / 3) + 1;

			c.set(Calendar.MONTH, ((quarter - 1) * 3));
			c.set(Calendar.DATE, 1);
			Date startDate = c.getTime();

			Calendar monthly = (Calendar) c.clone();
			LOG.info("  startDate  :" + startDate);

			// Go to end of quarter last day
			c.add(Calendar.MONTH, 2);
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = c.getTime();
			LOG.info("  endDate  " + endDate);

			// Get the stats for the quater
			double qVolume = ownerDao.findSubscriptionForDates(startDate, endDate, country, excludeFees);

			LOG.info("  qVolume   :" + qVolume);

			q.setLabel("Q" + quarter + "-" + df.format(startDate));
			q.setValue(qVolume);

			for (int month = 1; month <= 3; month++) {
				startDate = monthly.getTime();
				monthly.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				endDate = monthly.getTime();

				// get Volume for Month()
				double mVolume = ownerDao.findRevenueForDates(startDate, endDate, country, excludeFees);

				LOG.info("  mVolume   :" + mVolume);

				q.getbData().put(mf.format(startDate), mVolume);

				monthly.add(Calendar.DATE, 1);
			}
			c.set(Calendar.DATE, 1);
			c.add(Calendar.MONTH, -3);
			quatStats.add(q);
		}

		LOG.info(" quatStats :" + quatStats);
		return quatStats;
	}

	@Override
	public List<YearStatisticsPojo> getBuyerSubscriptionHalfYearly(Date effectiveStartDate, int yearCount, Country regCountry, boolean excludeFees) {
		List<YearStatisticsPojo> yearStats = new ArrayList<YearStatisticsPojo>();

		SimpleDateFormat df = new SimpleDateFormat("yyyy");

		// Set the user requested effective current date
		Calendar c = Calendar.getInstance();
		c.setTime(effectiveStartDate);

		// Fetch the stats for the requested number of Year
		for (int i = 0; i < yearCount; i++) {

			YearStatisticsPojo q = new YearStatisticsPojo();

			c.set(Calendar.MONTH, Calendar.JANUARY);
			c.set(Calendar.DATE, 1);

			Date startDate = c.getTime();

			Calendar monthly = (Calendar) c.clone();

			LOG.info("Start Date" + startDate);

			c.set(Calendar.MONTH, Calendar.DECEMBER);
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = c.getTime();
			LOG.info(" End date" + c.getTime());

			q.setLabel(df.format(startDate));

			List<YearStatisticsValuePojo> yearval = new ArrayList<YearStatisticsValuePojo>();

			for (int halfYear = 1; halfYear <= 2; halfYear++) {

				YearStatisticsValuePojo valpojo = new YearStatisticsValuePojo();
				startDate = monthly.getTime();
				c.add(Calendar.MONTH, 5);
				monthly.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				endDate = monthly.getTime();

				// get Volume for Month()
				double mVolume = ownerDao.findRevenueForDates(startDate, endDate, regCountry, excludeFees);

				LOG.info("  mVolume   :" + mVolume);

				valpojo.setValue1(mVolume);
				valpojo.setValue2(mVolume);

				yearval.add(valpojo);

				q.setValuePojo(valpojo);

				monthly.add(Calendar.DATE, 1);

				c.set(Calendar.DATE, 1);
			}

			c.add(Calendar.YEAR, -2);
			yearStats.add(q);

		}
		return yearStats;
	}

}