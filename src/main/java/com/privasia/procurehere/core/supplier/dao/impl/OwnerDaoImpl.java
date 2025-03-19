/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao.impl;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.pojo.OwnerDashboardPojo;
import com.privasia.procurehere.core.supplier.dao.OwnerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author VIPUL
 */
@Repository
public class OwnerDaoImpl extends GenericDaoImpl<Owner, String> implements OwnerDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public OwnerDashboardPojo findBuyerAndSupplierStatus(Country country) {
		StringBuilder hsql = new StringBuilder("select b.status as status, count(b) as tcount from Buyer as b ");
		if (country != null) {
			hsql.append(" where b.registrationOfCountry = :country");
		}
		hsql.append(" group by b.status ");
		Query query = getEntityManager().createQuery(hsql.toString());
		if (country != null) {
			query.setParameter("country", country);
		}

		List<Object[]> returnList = query.getResultList();
		hsql = new StringBuilder("select s.status as status, count(s) as tcount from Supplier as s ");
		if (country != null) {
			hsql.append(" where s.registrationOfCountry = :country");
		}
		hsql.append(" group by s.status ");
		query = getEntityManager().createQuery(hsql.toString());
		if (country != null) {
			query.setParameter("country", country);
		}

		returnList.addAll(query.getResultList());
		return new OwnerDashboardPojo(returnList);
	}

	@Override
	public Owner getDefaultOwner() {
		List<Owner> ownerList = findAll(Owner.class);
		if (CollectionUtil.isNotEmpty(ownerList)) {
			return ownerList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public int countRegisteredBuyersForDateRange(Date start, Date end, Country country) {

		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Buyer b where b.registrationCompleteDate between :start and :end ");
		if (country != null) {
			hsql.append(" and b.registrationOfCountry = :country");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		if (country != null) {
			query.setParameter("country", country);
		}
		query.setParameter("start", start, TemporalType.TIMESTAMP);
		query.setParameter("end", end, TemporalType.TIMESTAMP);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int countTotalRegisteredBuyers(Country country) {
		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Buyer b where b.registrationCompleteDate is not null ");
		if (country != null) {
			hsql.append(" and b.registrationOfCountry = :country");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		if (country != null) {
			query.setParameter("country", country);
		}
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int countCurrentBuyersOnTrial(Country country) {
		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Buyer b inner join b.currentSubscription s where :today between s.trialStartDate and s.trialEndDate ");
		if (country != null) {
			hsql.append(" and b.registrationOfCountry = :country");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("today", new java.util.Date());
		if (country != null) {
			query.setParameter("country", country);
		}
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findListOfBuyersOnTrial(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Subscription s inner join s.buyer b where :start between s.trialStartDate and s.trialEndDate or :end between s.trialStartDate and s.trialEndDate");

		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findBuyerConvesionRate(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Subscription s inner join s.buyer b where :start between s.trialStartDate and s.trialEndDate or :end between s.trialStartDate and s.trialEndDate");

		hsql.append(" and s.convertedToPaid = :flag");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("flag", true);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findListOfBuyersSuspended(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Buyer b where b.registrationCompleteDate between :start and :end");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry ");
		}
		hsql.append(" and b.status = :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", BuyerStatus.SUSPENDED);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findListOfBuyersActive(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Buyer b where b.registrationCompleteDate between :start and :end");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry ");
		}
		hsql.append(" and b.status = :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", BuyerStatus.ACTIVE);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findListOfNewBuyer(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Buyer b where b.registrationCompleteDate between :start and :end ");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findAllCancelledEvents(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select count(e) as tcount from RfxView e ");
		hsql.append(", Buyer b left outer join b.registrationOfCountry c  where b.id = e.tenantId and  e.actionDate between :start and :end and e.status = :status ");
		if (regCountry != null) {
			hsql.append("  and c = :regCountry ");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", EventStatus.CANCELED);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findAllTotalEvents(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select count(e) as tcount from RfxView e ");
		hsql.append(", Buyer b left outer join b.registrationOfCountry c  where b.id = e.tenantId and  e.actionDate between :start and :end and e.status = :status ");
		if (regCountry != null) {
			hsql.append("  and c = :regCountry ");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", EventStatus.FINISHED);

		return ((Number) query.getSingleResult()).intValue();
	}

	public int findAllTotalPr(Date sDate, Date eDate, Country regCountry) {
		StringBuilder hsql = new StringBuilder("select count(p) as tcount from Pr p inner join p.buyer as b left outer join b.registrationOfCountry c");
		hsql.append(" where p.prCreatedDate between :start and :end and p.status = :status ");
		if (regCountry != null) {
			hsql.append(" and c = :regCountry ");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", PrStatus.APPROVED);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}

		return ((Number) query.getSingleResult()).intValue();
	}

	public int findAllTotalPo(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select count(p.poNumber) as tcount from Pr p inner join p.buyer as b left outer join b.registrationOfCountry c");
		hsql.append(" where p.poCreatedDate between :start and :end and p.status = :status ");
		if (regCountry != null) {
			hsql.append(" and c = :regCountry ");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", PrStatus.APPROVED);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findAllTotalSupplier(Date sDate, Date eDate, Country regCountry) {
		StringBuilder hsql = new StringBuilder("select count(s) as tcount from Supplier s where s.registrationCompleteDate between :start and :end");
		if (regCountry != null) {
			hsql.append(" and s.registrationOfCountry = :regCountry ");
		}
		hsql.append(" and s.status = :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", SupplierStatus.APPROVED);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findAllTotalBuyer(Date sDate, Date eDate, Country regCountry) {
		StringBuilder hsql = new StringBuilder("select count(b) as tcount from Buyer b where b.registrationCompleteDate between :start and :end");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry ");
		}
		hsql.append(" and b.status = :status");
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", BuyerStatus.ACTIVE);

		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public int findAllFailedPayments(Date sDate, Date eDate, Country regCountry) {
		StringBuilder hsql = new StringBuilder("select sum(p.amount) as tsum from PaymentTransaction p inner join p.buyer as b left outer join b.registrationOfCountry c");
		hsql.append(" where p.createdDate between :start and :end and p.status != :status");
		if (regCountry != null) {
			hsql.append(" and c = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", TransactionStatus.SUCCESS);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Number number = (Number) query.getSingleResult();
		return number != null ? number.intValue() : 0;
	}

	@Override
	public int findAllRevenueGenerated(Date sDate, Date eDate, Country regCountry) {
		StringBuilder hsql = new StringBuilder("select sum(p.amount) as tsum from PaymentTransaction p inner join p.buyer as b left outer join b.registrationOfCountry c");
		hsql.append(" where p.createdDate between :start and :end and p.status = :status");
		if (regCountry != null) {
			hsql.append(" and c = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", TransactionStatus.SUCCESS);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Number number = (Number) query.getSingleResult();
		return number != null ? number.intValue() : 0;
	}

	public double findEventPerCategory(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select avg(a) from (select count(*) as a from PROC_RFX_EVENTS e  ");
		hsql.append("group by e.EVENT_TYPE ) b ");

		Query query = getEntityManager().createNativeQuery(hsql.toString());
		if (regCountry != null) {
			// query.setParameter("regCountry", regCountry);
		}
		Number number = (Number) query.getSingleResult();
		return  number != null ? number.doubleValue() : 0;            
	}

	@Override
	public double findAllAverageTimeEvent(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select avg( hour(e.eventEnd-e.eventStart)) as tavg from RfxView e where e.type = :status");

		hsql.append(" group by e.type");
		Query query = getEntityManager().createQuery(hsql.toString());
		for (RfxTypes rtype : RfxTypes.values()) {
			query.setParameter("status", RfxTypes.getRfxTypes(rtype.getValue()));

		}
		DecimalFormat df = new DecimalFormat("##.##");
		Number value = (Number) query.getSingleResult();
		if (value != null) {
			return Double.valueOf(df.format(value.doubleValue()));
		} else {
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> findAndCountNewBuyerBreakup(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select p.planName, count(b) as tcount from Buyer b left outer join b.buyerPackage.plan p ");
		hsql.append(" where b.registrationCompleteDate between :start and :end");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		hsql.append(" group by p.planName");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Map<String, String> data = new HashMap<>();

		List<Object[]> list = query.getResultList();

		for (Object[] values : list) {
			data.put((String.valueOf(values[0]) != null ? String.valueOf(values[0]) : ""), (String.valueOf(values[1]) != null ? String.valueOf(values[1]) : ""));
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> findAndCountTotalBuyerBreakup(Date sDate, Date eDate, Country regCountry) {
		StringBuilder hsql = new StringBuilder("select p.planName, count(b) as tcount from Buyer b left outer join b.buyerPackage.plan p");
		hsql.append(" where b.registrationCompleteDate between :start and :end and b.status = :status");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		hsql.append(" group by p.planName");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", BuyerStatus.ACTIVE);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Map<String, String> data = new HashMap<>();

		List<Object[]> list = query.getResultList();

		for (Object[] values : list) {
			data.put((String.valueOf(values[0]) != null ? String.valueOf(values[0]) : "No Plan Selected"), (String.valueOf(values[1]) != null ? String.valueOf(values[1]) : ""));
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> findAndCountSuspendedBuyerBreakup(Date sDate, Date eDate, Country regCountry) {
		StringBuilder hsql = new StringBuilder("select p.planName, count(b) as tcount from Buyer b left outer join b.buyerPackage.plan p where b.registrationCompleteDate between :start and :end");
		hsql.append(" and b.status = :status");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		hsql.append(" group by p.planName");

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", BuyerStatus.SUSPENDED);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Map<String, String> data = new HashMap<>();

		List<Object[]> list = query.getResultList();

		for (Object[] values : list) {
			data.put((String.valueOf(values[0]) != null ? String.valueOf(values[0]) : ""), (String.valueOf(values[1]) != null ? String.valueOf(values[1]) : ""));
		}
		return data;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> findAndCountRevenuePlanBreakup(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select pl.planName,sum(p.amount) as tsum from PaymentTransaction p left outer join p.buyerPlan pl left outer join p.buyer as b ");
		hsql.append(" where p.createdDate between :start and :end and p.status = :status");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		hsql.append(" group by pl.planName");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("status", TransactionStatus.SUCCESS);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Map<String, String> data = new HashMap<>();

		List<Object[]> list = query.getResultList();

		for (Object[] values : list) {
			data.put((String.valueOf(values[0]) != null ? String.valueOf(values[0]) : ""), (String.valueOf(values[1]) != null ? String.valueOf(values[1]) : ""));
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> findAndCountConversionPlanBreakup(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select p.planName,count(b) as tcount from Subscription s left outer join s.buyer b left outer join s.plan p where :start between s.trialStartDate and s.trialEndDate or :end between s.trialStartDate and s.trialEndDate");
		hsql.append(" and s.convertedToPaid = :flag");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		hsql.append(" group by p.planName");
		Query query = getEntityManager().createQuery(hsql.toString());
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		query.setParameter("flag", true);
		Map<String, String> data = new HashMap<>();
		List<Object[]> list = query.getResultList();
		for (Object[] values : list) {
			data.put((String.valueOf(values[0]) != null ? String.valueOf(values[0]) : ""), (String.valueOf(values[1]) != null ? String.valueOf(values[1]) : ""));
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> findAndCountPerWeekEventPlanBreakup(Date sDate, Date eDate, Country regCountry) {

		StringBuilder hsql = new StringBuilder("select e.eventName,count(e) as tcount from RfxView e ");
		hsql.append(", Buyer b left outer join b.registrationOfCountry c where b.id = e.tenantId and e.eventPublishDate between :start and :end and e.eventEnd between :start and :end and e.type = :status");
		if (regCountry != null) {
			hsql.append(" and c = :regCountry ");
		}
		hsql.append(" group by e.eventName");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("start", sDate, TemporalType.TIMESTAMP);
		query.setParameter("end", eDate, TemporalType.TIMESTAMP);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		for (RfxTypes rtype : RfxTypes.values()) {
			query.setParameter("status", RfxTypes.getRfxTypes(rtype.getValue()));
		}
		Map<String, String> data = new HashMap<>();

		List<Object[]> list = query.getResultList();

		for (Object[] values : list) {
			data.put((String.valueOf(values[0]) != null ? String.valueOf(values[0]) : ""), (String.valueOf(values[1]) != null ? String.valueOf(values[1]) : ""));
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> findTotalAuctionSavingBreakup(Date sDate, Date eDate, Country regCountry) {
		
		String sql = " SELECT DISTINCT(amount),auction FROM PROC_RFA_SUPPLIER_BQ  a, ";
		sql += " ( SELECT  ";
		sql += "(MAX(b.TOTAL_AFTER_TAX)-MAX(b.INITIAL_PRICE)) amount, e.AUCTION_TYPE  auction FROM PROC_RFA_SUPPLIER_BQ  b ";
		sql += "LEFT OUTER JOIN PROC_RFA_EVENTS  e ON e.ID = b.EVENT_ID WHERE e.AUCTION_TYPE IN ('Forward English Auction' , 'Forward Sealed Bid' ,'Forward Dutch Auction') ";
		sql += "group by  e.AUCTION_TYPE ";
		sql += "UNION SELECT  ";
		sql += " (MIN(b.INITIAL_PRICE)-MIN(b.TOTAL_AFTER_TAX))  amount, e.AUCTION_TYPE  auction FROM PROC_RFA_SUPPLIER_BQ  b ";
		sql += " LEFT OUTER JOIN PROC_RFA_EVENTS  e ON e.ID = b.EVENT_ID WHERE e.AUCTION_TYPE IN ('Reverse English Auction' , 'Reverse Sealed Bid' ,'Reverse Dutch Auction') ";
		sql += " group by  e.AUCTION_TYPE )c ";

		final Query query = getEntityManager().createNativeQuery(sql);

		Map<String, String> data = new HashMap<>();
		List<Object[]> list = query.getResultList();
		LOG.info(" LIST SIZE " + list.size());

		for (Object[] values : list) {
			data.put((String.valueOf(values[0]) != null ? String.valueOf(values[0]) : ""), (String.valueOf(values[1]) != null ? String.valueOf(values[1]) : ""));
		}
		return data;

	}

	@Override
	public double findSumOfBuyerRevenueCurrentWeek(Date startDate, Date endDate, Country regCountry, boolean excludeFees) {

		StringBuilder hsql = new StringBuilder("");
		if (excludeFees) {
			hsql.append("select sum(p.amount - p.paymentFee) ");
		} else {
			hsql.append("select sum(p.amount) ");
		}

		hsql.append("as tsum from PaymentTransaction p inner join p.buyer as b left outer join b.registrationOfCountry c where p.confirmationDate between :startDate and :endDate and p.status = :status");
		if (regCountry != null) {
			hsql.append(" and c = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
		query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
		query.setParameter("status", TransactionStatus.SUCCESS);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Number number = (Number) query.getSingleResult();
		return number != null ? number.doubleValue() : 0.0;
	}

	@Override
	public double findSumOfBuyerRevenueForMonth(Date startDate, Date endDate, Country regCountry, boolean excludeFees) {

		StringBuilder hsql = new StringBuilder("");
		if (excludeFees) {
			hsql.append("select sum(p.amount - p.paymentFee) ");
		} else {
			hsql.append("select sum(p.amount) ");
		}

		hsql.append("as tsum from PaymentTransaction p inner join p.buyer as b left outer join b.registrationOfCountry c where p.confirmationDate between :startDate and :endDate and p.status = :status");
		if (regCountry != null) {
			hsql.append(" and c = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
		query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
		query.setParameter("status", TransactionStatus.SUCCESS);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Number number = (Number) query.getSingleResult();
		return number != null ? number.doubleValue() : 0.0;
	}

	@Override
	public double findRevenueForDates(Date startDate, Date endDate, Country regCountry, boolean excludeFees) {
		LOG.info("  startDate  :" + startDate + "  endDate  :" + endDate);
		StringBuilder hsql = new StringBuilder("");
		if (excludeFees) {
			hsql.append("select sum(p.amount - p.paymentFee) ");
		} else {
			hsql.append("select sum(p.amount) ");
		}

		hsql.append("as tsum from PaymentTransaction p inner join p.buyer as b where p.confirmationDate between :startDate and :endDate and p.status = :status");
		if (regCountry != null) {
			hsql.append(" and b.registrationOfCountry = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
		query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
		query.setParameter("status", TransactionStatus.SUCCESS);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Number number = (Number) query.getSingleResult();
		LOG.info("  Number  " + number);
		return number != null ? number.doubleValue() : 0.0;
	}

	@Override
	public double findCountOfBuyerSubscriptionCurrentWeek(Date startDate, Date endDate, Country country, boolean excludeFees) {
		StringBuilder hsql = new StringBuilder("select count(s) as tcount from BuyerSubscription s  inner join s.buyer as b left outer join b.registrationOfCountry c where s.activatedDate between :startDate and :endDate and s.subscriptionStatus = :status");
		if (country != null) {
			hsql.append(" and c = :regCountry ");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
		query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
		query.setParameter("status", SubscriptionStatus.ACTIVE);
		if (country != null) {
			query.setParameter("regCountry", country);
		}
		Number number = (Number) query.getSingleResult();
		return number != null ? number.doubleValue() : 0.0;
	}

	@Override
	public double findCountOfBuyerSubscriptionForMonth(Date startDate, Date endDate, Country country, boolean excludeFees) {
		StringBuilder hsql = new StringBuilder("select count(s) as tcount from BuyerSubscription s  inner join s.buyer as b left outer join b.registrationOfCountry c where s.activatedDate between :startDate and :endDate and s.subscriptionStatus = :status");
		if (country != null) {
			hsql.append(" and c = :regCountry");
		}
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
		query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
		query.setParameter("status", SubscriptionStatus.ACTIVE);
		if (country != null) {
			query.setParameter("regCountry", country);
		}
		Number number = (Number) query.getSingleResult();
		return number != null ? number.doubleValue() : 0.0;
	}

	@Override
	public double findSubscriptionForDates(Date startDate, Date endDate, Country regCountry, boolean excludeFees) {

		StringBuilder hsql = new StringBuilder("select count(s) as tcount from BuyerSubscription s  inner join s.buyer as b left outer join b.registrationOfCountry c where s.activatedDate between :startDate and :endDate and s.subscriptionStatus = :status");
		if (regCountry != null) {
			hsql.append(" and c = :regCountry");
		}

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
		query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
		query.setParameter("status", SubscriptionStatus.ACTIVE);
		if (regCountry != null) {
			query.setParameter("regCountry", regCountry);
		}
		Number number = (Number) query.getSingleResult();
		return number != null ? number.doubleValue() : 0.0;
	}

	@Override
	public double countRfaTotalAuctionSaving(Date sDate, Date eDate, Country regCountry) {
		LOG.info(" countRfaTotalAuctionSaving Dao");

		String sql = "SELECT SUM(DISTINCT(total))  saving from PROC_RFA_SUPPLIER_BQ a, ";
		sql += "( SELECT DISTINCT (MAX(winning)-MAX(initialPrice))  total from PROC_RFA_SUPPLIER_BQ a, ";
		sql += "( SELECT ";
		sql += "MAX(b.TOTAL_AFTER_TAX)  winning,MAX(b.INITIAL_PRICE)  initialPrice, e.AUCTION_TYPE FROM PROC_RFA_SUPPLIER_BQ  b ";
		sql += "LEFT OUTER JOIN PROC_RFA_EVENTS e ON e.ID = b.EVENT_ID WHERE e.AUCTION_TYPE IN ('Forward English Auction' , 'Forward Sealed Bid' ,'Forward Dutch Auction') ";
		sql += " group by  e.AUCTION_TYPE ";
		sql += ") b ";
		sql += "UNION ";
		sql += "SELECT DISTINCT (MIN(initialPrice)-MIN(winning))  total from PROC_RFA_SUPPLIER_BQ a, ";
		sql += "( SELECT ";
		sql += "MIN(b.TOTAL_AFTER_TAX)  winning,MIN(b.INITIAL_PRICE) initialPrice, e.AUCTION_TYPE FROM PROC_RFA_SUPPLIER_BQ  b ";
		sql += " LEFT OUTER JOIN PROC_RFA_EVENTS  e ON e.ID = b.EVENT_ID WHERE e.AUCTION_TYPE IN ('Reverse English Auction' , 'Reverse Sealed Bid' ,'Reverse Dutch Auction') ";
		sql += "group by  e.AUCTION_TYPE ";
		sql += ") b )t  ";

//		LOG.info(" Sql :" + sql);
		final Query query = getEntityManager().createNativeQuery(sql);
		Number number = (Number) query.getSingleResult();
		LOG.info(" Number " + number);
		return number != null ? number.doubleValue() : 0.0;
	}

	@Override
	public double countRfaAverageAuctionSaving(Date sDate, Date eDate, Country regCountry) {
		LOG.info(" countRfaAverageAuctionSaving Dao");

		String sql = "SELECT AVG(DISTINCT(total))  saving from PROC_RFA_SUPPLIER_BQ a, ";
		sql += "( SELECT DISTINCT (MAX(winning)-MAX(initialPrice))  total from PROC_RFA_SUPPLIER_BQ a, ";
		sql += "( SELECT ";
		sql += "MAX(b.TOTAL_AFTER_TAX)  winning,MAX(b.INITIAL_PRICE)  initialPrice, e.AUCTION_TYPE FROM PROC_RFA_SUPPLIER_BQ  b ";
		sql += "LEFT OUTER JOIN PROC_RFA_EVENTS e ON e.ID = b.EVENT_ID WHERE e.AUCTION_TYPE IN ('Forward English Auction' , 'Forward Sealed Bid' ,'Forward Dutch Auction') ";
		sql += " group by  e.AUCTION_TYPE ";
		sql += ") b ";
		sql += "UNION ";
		sql += "SELECT DISTINCT (MIN(initialPrice)-MIN(winning))  total from PROC_RFA_SUPPLIER_BQ a, ";
		sql += "( SELECT ";
		sql += "MIN(b.TOTAL_AFTER_TAX)  winning,MIN(b.INITIAL_PRICE)  initialPrice, e.AUCTION_TYPE FROM PROC_RFA_SUPPLIER_BQ  b ";
		sql += " LEFT OUTER JOIN PROC_RFA_EVENTS  e ON e.ID = b.EVENT_ID WHERE e.AUCTION_TYPE IN ('Reverse English Auction' , 'Reverse Sealed Bid' ,'Reverse Dutch Auction') ";
		sql += "group by  e.AUCTION_TYPE ";
		sql += ") b )t  ";

		final Query query = getEntityManager().createNativeQuery(sql);

		Number number = (Number) query.getSingleResult();
		LOG.info(" Number " + number);
		return number != null ? number.doubleValue() : 0.0;
	}

}
