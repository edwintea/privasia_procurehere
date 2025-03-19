package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.pojo.OwnerDashboardPojo;
import com.privasia.procurehere.core.pojo.StatisticsPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.YearStatisticsPojo;

/**
 * @author VIPUL
 */

public interface OwnerDashboardService {

	/**
	 * @param country
	 * @return
	 */
	OwnerDashboardPojo findAllBuyerAndSupplierStatus(Country country);

	/**
	 * @param country
	 * @return
	 */
	int countAllCurrentBuyersOnTrial(Country country);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllBuyerConvesionRate(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param country
	 * @return
	 */
	int countAllTotalRegisteredBuyers(Country country);

	/**
	 * @param stime
	 * @param etime
	 * @param country
	 * @return
	 */
	int countAllRegisteredBuyersForDateRange(Date stime, Date etime, Country country);

	/**
	 * @param start
	 * @param end
	 * @param country
	 * @param input
	 * @return
	 */
	List<Buyer> findListOfAllBuyerForDateRange(Date start, Date end, Country country, TableDataInput input);

	/**
	 * @param start
	 * @param end
	 * @param country
	 * @return
	 */
	List<SupplierPojo> findListOfAllSupplierForDateRange(Date start, Date end, Country country);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllCurrentBuyersOnTrial(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllCurrentBuyersSuspended(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllCurrentBuyersActive(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllNewBuyers(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllEventCancelled(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllEventFinished(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllTotalPr(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllTotalPo(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllTotalSupplier(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllTotalBuyer(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllFailedPaymentsTransaction(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int countAllRevenueGenerated(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	double countAllEventPerCategory(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	double countAllAverageTimeEvent(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> getAllNewBuyerBreakups(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> getAllTotalBuyerBreakups(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> getSuspendedBuyerBreakups(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> getRevenuePlanBreakups(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> getConversionPlanBreakups(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> getPerWeekEventBreakups(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param date
	 * @param i
	 * @param regCountry
	 * @param b
	 * @return
	 */
	Map<String, Double> getBuyerRevenueByWeek(Date date, int i, Country regCountry, boolean b);

	/**
	 * @param date
	 * @param i
	 * @param country
	 * @param b
	 * @return
	 */
	Map<String, Double> getBuyerRevenueByMonth(Date date, int i, Country country, boolean b);

	/**
	 * @param date
	 * @param i
	 * @param regCountry
	 * @param b
	 * @return
	 */
	List<StatisticsPojo> getBuyerRevenueForQuaterly(Date date, int i, Country regCountry, boolean b);

	/**
	 * @param date
	 * @param i
	 * @param regCountry
	 * @param b
	 * @return
	 */
	List<YearStatisticsPojo> getBuyerRevenueHalfYearly(Date date, int i, Country regCountry, boolean b);

	/**
	 * @param date
	 * @param i
	 * @param regCountry
	 * @param b
	 * @return
	 */
	Map<String, Double> getBuyerSubscriptionByWeek(Date date, int i, Country regCountry, boolean b);

	/**
	 * @param date
	 * @param i
	 * @param regCountry
	 * @param b
	 * @return
	 */
	Map<String, Double> getBuyerSubscriptionByMonth(Date date, int i, Country regCountry, boolean b);

	/**
	 * @param date
	 * @param i
	 * @param regCountry
	 * @param b
	 * @return
	 */
	List<StatisticsPojo> getBuyerSubscriptionForQuaterly(Date date, int i, Country regCountry, boolean b);

	/**
	 * @param date
	 * @param i
	 * @param regCountry
	 * @param b
	 * @return
	 */
	List<YearStatisticsPojo> getBuyerSubscriptionHalfYearly(Date date, int i, Country regCountry, boolean b);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> getTotalAuctionSavingBreakup(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	double countRfaTotalAuctionSaving(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	double countRfaAverageAuctionSaving(Date sDate, Date eDate, Country regCountry);
}
