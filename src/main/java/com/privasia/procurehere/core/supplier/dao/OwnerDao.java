/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao;

import java.util.Date;
import java.util.Map;

import com.privasia.procurehere.core.dao.GenericDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.pojo.OwnerDashboardPojo;

/**
 * @author Vipul
 */
public interface OwnerDao extends GenericDao<Owner, String> {

	/**
	 * @param country
	 * @return
	 */
	OwnerDashboardPojo findBuyerAndSupplierStatus(Country country);

	/**
	 * @param start
	 * @param end
	 * @param country
	 * @return
	 */
	int countRegisteredBuyersForDateRange(Date start, Date end, Country country);

	// int countCurrentBuyersOnTrial(Country country);
	/**
	 * @param country
	 * @return
	 */
	int countTotalRegisteredBuyers(Country country);

	/**
	 * @param countryId
	 * @return
	 */
	int countCurrentBuyersOnTrial(Country countryId);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findListOfBuyersOnTrial(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findListOfBuyersSuspended(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findListOfBuyersActive(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findListOfNewBuyer(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findAllCancelledEvents(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findAllTotalEvents(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findAllTotalPr(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findAllTotalPo(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findAllTotalSupplier(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findAllTotalBuyer(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findAllFailedPayments(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findAllRevenueGenerated(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	double findEventPerCategory(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	double findAllAverageTimeEvent(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> findAndCountNewBuyerBreakup(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> findAndCountTotalBuyerBreakup(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> findAndCountSuspendedBuyerBreakup(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> findAndCountRevenuePlanBreakup(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> findAndCountConversionPlanBreakup(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> findAndCountPerWeekEventPlanBreakup(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param startDate
	 * @param endDate
	 * @param regCountry
	 * @param excludeFees
	 * @return
	 */
	double findSumOfBuyerRevenueCurrentWeek(Date startDate, Date endDate, Country regCountry, boolean excludeFees);

	/**
	 * @param startDate
	 * @param endDate
	 * @param country
	 * @param excludeFees
	 * @return
	 */
	double findSumOfBuyerRevenueForMonth(Date startDate, Date endDate, Country country, boolean excludeFees);

	/**
	 * @param startDate
	 * @param endDate
	 * @param regCountry
	 * @param excludeFees
	 * @return
	 */
	double findRevenueForDates(Date startDate, Date endDate, Country regCountry, boolean excludeFees);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	int findBuyerConvesionRate(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param startDate
	 * @param endDate
	 * @param country
	 * @param excludeFees
	 * @return
	 */
	double findCountOfBuyerSubscriptionCurrentWeek(Date startDate, Date endDate, Country country, boolean excludeFees);

	/**
	 * @param startDate
	 * @param endDate
	 * @param country
	 * @param excludeFees
	 * @return
	 */
	double findCountOfBuyerSubscriptionForMonth(Date startDate, Date endDate, Country country, boolean excludeFees);

	/**
	 * @param startDate
	 * @param endDate
	 * @param regCountry
	 * @param excludeFees
	 * @return
	 */
	double findSubscriptionForDates(Date startDate, Date endDate, Country regCountry, boolean excludeFees);

	/**
	 * @return
	 */
	Owner getDefaultOwner();

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	Map<String, String> findTotalAuctionSavingBreakup(Date sDate, Date eDate, Country regCountry);

	/**
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	double countRfaTotalAuctionSaving(Date sDate, Date eDate, Country regCountry);

	/**
	 * 
	 * @param sDate
	 * @param eDate
	 * @param regCountry
	 * @return
	 */
	double countRfaAverageAuctionSaving(Date sDate, Date eDate, Country regCountry);
}
