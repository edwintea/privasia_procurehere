/**
 * 
 */
package com.privasia.procurehere.core.supplier.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.pojo.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.impl.GenericDaoImpl;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Arc
 */
@Repository
public class BuyerDaoImpl extends GenericDaoImpl<Buyer, String> implements BuyerDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllBuyers() {
		final Query query = getEntityManager().createQuery("from Buyer b left outer join fetch b.registrationOfCountry rc left outer join fetch b.state st order by b.companyName asc");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllActiveBuyers() {
		final Query query = getEntityManager().createQuery("from Buyer b where b.status = :status  order by b.companyName asc");
		query.setParameter("status", BuyerStatus.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllActiveBuyersWithActiveSubscription() {
		final Query query = getEntityManager().createQuery("from Buyer b where b.status = :buyerStatus and b.currentSubscription.subscriptionStatus in (:status) order by b.companyName asc");
		query.setParameter("buyerStatus", BuyerStatus.ACTIVE);
		List<SubscriptionStatus> status = new ArrayList<SubscriptionStatus>();
		status.add(SubscriptionStatus.ACTIVE);
		status.add(SubscriptionStatus.IN_TRIAL);
		query.setParameter("status", status);
		return query.getResultList();
	}

	@Override
	public boolean isExists(Buyer buyer) {
		StringBuilder hsql = new StringBuilder("from Buyer a where upper(a.companyRegistrationNumber) = :regNumber and a.registrationOfCountry = :registrationOfCountry");
		if (StringUtils.checkString(buyer.getId()).length() > 0) {
			LOG.info("===========buyer id>" + buyer.getId());
			hsql.append(" and a.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("regNumber", buyer.getCompanyRegistrationNumber().toUpperCase());
		query.setParameter("registrationOfCountry", buyer.getRegistrationOfCountry());
		if (StringUtils.checkString(buyer.getId()).length() > 0) {
			query.setParameter("id", buyer.getId());
		}
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public boolean isExistsLoginEmail(String loginEmail) {
		Query query = getEntityManager().createQuery("from User a where  upper(a.loginId) = :loginEmail ");
		query.setParameter("loginEmail", loginEmail.toUpperCase());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public Buyer findPlainBuyerById(String id) {
		return super.findById(id);
	}

	@Override
	public Buyer findById(String id) {
		final Query query = getEntityManager().createQuery("from Buyer b left outer join fetch b.registrationOfCountry rc left outer join fetch b.buyerPackage bp left outer join fetch b.currentSubscription bs left outer join fetch bs.plan splan left outer join fetch bp.plan plan left outer join fetch b.state st left outer join fetch b.companyStatus cs where b.id =:id");
		query.setParameter("id", id);
		return (Buyer) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> searchBuyer(String status, String order, String globalSearch) {
		LOG.info("Buyer Search enter....." + status);
		StringBuilder hql = new StringBuilder(" from Buyer a  left outer join fetch  a.registrationOfCountry rc  where 1 = 1 ");
		if (StringUtils.checkString(status).length() > 0 && BuyerStatus.ALL != BuyerStatus.valueOf(StringUtils.checkString(status).toUpperCase())) {
			hql.append("and a.status =:status ");
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			hql.append("and ( upper(a.companyName) like :companyName or upper(a.fullName) like :fullName or upper(a.companyRegistrationNumber) like :companyRegistrationNumber)");
		}

		if (StringUtils.checkString(order).equals("Newest")) {
			hql.append("order by a.registrationCompleteDate desc, a.subscriptionDate desc");
		} else {
			hql.append("order by a.registrationCompleteDate, a.subscriptionDate ");
		}
		LOG.info(hql.toString());
		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(status).length() > 0 && BuyerStatus.ALL != BuyerStatus.valueOf(StringUtils.checkString(status).toUpperCase())) {
			query.setParameter("status", BuyerStatus.valueOf(StringUtils.checkString(status).toUpperCase()));
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			query.setParameter("companyName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("fullName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("companyRegistrationNumber", "%" + globalSearch.toUpperCase() + "%");

		}
		return query.getResultList();
	}

	@Override
	public Buyer update(Buyer e) {
		return getEntityManager().merge(e);
	}

	@Override
	public boolean isExistsRegistrationNumber(Buyer buyer) {
		final Query query = getEntityManager().createQuery("from Buyer a where a.companyRegistrationNumber = :companyRegistrationNumber and a.registrationOfCountry = :registrationOfCountry ");
		query.setParameter("companyRegistrationNumber", buyer.getCompanyRegistrationNumber().toUpperCase());
		query.setParameter("registrationOfCountry", buyer.getRegistrationOfCountry());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public boolean isExistsCompanyName(Buyer buyer) {
		String hql = "from Buyer a where upper(a.companyName) = :companyName and a.registrationOfCountry = :registrationOfCountry ";

		if (StringUtils.checkString(buyer.getId()).length() > 0) {
			hql += " and a.id <> :id";
		}

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("companyName", buyer.getCompanyName().toUpperCase());
		query.setParameter("registrationOfCountry", buyer.getRegistrationOfCountry());
		if (StringUtils.checkString(buyer.getId()).length() > 0) {
			query.setParameter("id", buyer.getId());
		}
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public List<Buyer> findAll1() {
		final Query query = getEntityManager().createQuery("from Buyer");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findActiveBuyers() {
		final Query query = getEntityManager().createQuery("from Buyer b left outer join fetch b.registrationOfCountry rc where b.status =:status order by b.companyName asc");
		query.setParameter("status", BuyerStatus.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findPendingBuyers() {
		final Query query = getEntityManager().createQuery("from Buyer b left outer join fetch b.registrationOfCountry rc where b.status =:status order by b.companyName asc");
		query.setParameter("status", BuyerStatus.PENDING);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findListOfBuyerForDateRange(Date start, Date end, Country country, TableDataInput input) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.Buyer(b.companyName,b.registrationCompleteDate, rc.countryName, b.companyRegistrationNumber) from Buyer b left outer join b.registrationOfCountry rc where b.registrationCompleteDate between :start and :end");
		if (country != null) {
			hsql.append(" and rc = :country ");
		}
		hsql.append(" order by b.registrationCompleteDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		if (country != null) {
			query.setParameter("country", country);
		}
		query.setParameter("start", start, TemporalType.TIMESTAMP);
		query.setParameter("end", end, TemporalType.TIMESTAMP);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> getAllBuyerFromGlobalSearch(String searchVal) {
		final Query query = getEntityManager().createQuery("select  b from Buyer b left outer join fetch b.registrationOfCountry rc where (upper(b.companyRegistrationNumber) like :searchVal) or (upper(b.companyName) like :searchVal)");
		query.setParameter("searchVal", "%" + searchVal.toUpperCase() + "%");
		List<Buyer> list = query.getResultList();
		LOG.info("  Buyer  GlobalSearch " + list.size());
		return list;
	}

	@Override
	public void decreaseEventLimitCountByBuyerId(String buyerId) {
		final Query query = getEntityManager().createQuery("update BuyerPackage bp set bp.noOfEvents = (bp.noOfEvents - 1) where bp.buyer.id = :buyerId and bp.noOfEvents > 0");
		query.setParameter("buyerId", buyerId);
		query.executeUpdate();
	}

	@Override
	public Buyer findBuyerGeneralDetailsById(String tenantId) {
		final Query query = getEntityManager().createQuery("select NEW Buyer(b.companyName, b.fullName, b.communicationEmail, b.companyContactNumber, b.mobileNumber, b.faxNumber, b.allowSupplierUpload) from Buyer b where b.id =:id");
		query.setParameter("id", tenantId);
		return (Buyer) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllBuyersWithActiveSubscription() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.currentSubscription cs left outer join fetch cs.nextSubscription where b.status = :status and cs.subscriptionStatus != :subscriptionStatus");
		query.setParameter("status", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.FUTURE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore30Days() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.buyerPackage bp left outer join fetch bp.plan p where b.status = :buyerStatus and bp.subscriptionStatus = :subscriptionStatus and bp.remBefore30Day = :reminderSent ");
		query.setParameter("buyerStatus", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.ACTIVE);
		query.setParameter("reminderSent", Boolean.FALSE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore15Days() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.buyerPackage bp left outer join fetch bp.plan p where b.status = :buyerStatus and bp.subscriptionStatus = :subscriptionStatus and bp.remBefore15Day = :reminderSent ");
		query.setParameter("buyerStatus", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.ACTIVE);
		query.setParameter("reminderSent", Boolean.FALSE);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore7Days() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.buyerPackage bp left outer join fetch bp.plan p where b.status = :buyerStatus and bp.subscriptionStatus = :subscriptionStatus and bp.remBefore7Day = :reminderSent ");
		query.setParameter("buyerStatus", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.ACTIVE);
		query.setParameter("reminderSent", Boolean.FALSE);

		return query.getResultList();
	}

	@Override
	public Buyer findBuyerByIdWithBuyerPackage(String buyerId) {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.currentSubscription cs left outer join fetch b.buyerPackage bp left outer join fetch bp.plan p where b.id = :buyerId ");
		query.setParameter("buyerId", buyerId);
		return (Buyer) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllBuyersFor2DaysBeforeSubscriptionExpire() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.currentSubscription cs left outer join fetch cs.nextSubscription ns left outer join fetch b.buyerPackage bp where b.status = :status and bp.subscriptionStatus != :subscriptionStatus and bp.userDaRemBefore2Day = :reminderSent");
		query.setParameter("status", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.EXPIRED);
		query.setParameter("reminderSent", Boolean.FALSE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore6Months() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.buyerPackage bp left outer join fetch bp.plan p where b.status = :buyerStatus and bp.subscriptionStatus = :subscriptionStatus and bp.remBefore6Month = :reminderSent and b.currentSubscription.planType = :subscriptionType ");
		query.setParameter("buyerStatus", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.ACTIVE);
		query.setParameter("subscriptionType", PlanType.PER_EVENT);
		query.setParameter("reminderSent", Boolean.FALSE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findBuyersForExpiryNotificationReminderBefore3Months() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.buyerPackage bp left outer join fetch bp.plan p where b.status = :buyerStatus and bp.subscriptionStatus = :subscriptionStatus and bp.remBefore3Month = :reminderSent and b.currentSubscription.planType = :subscriptionType ");
		query.setParameter("buyerStatus", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.ACTIVE);
		query.setParameter("subscriptionType", PlanType.PER_EVENT);
		query.setParameter("reminderSent", Boolean.FALSE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllBuyersFor7DaysBeforeSubscriptionExpire() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.currentSubscription cs left outer join fetch cs.nextSubscription ns left outer join fetch b.buyerPackage bp where b.status = :status and bp.subscriptionStatus != :subscriptionStatus and bp.userDaRemBefore7Day = :reminderSent");
		query.setParameter("status", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.EXPIRED);
		query.setParameter("reminderSent", Boolean.FALSE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllBuyersFor15DaysBeforeSubscriptionExpire() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.currentSubscription cs left outer join fetch cs.nextSubscription ns left outer join fetch b.buyerPackage bp where b.status = :status and bp.subscriptionStatus != :subscriptionStatus and bp.userDaRemBefore15Day = :reminderSent");
		query.setParameter("status", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.EXPIRED);
		query.setParameter("reminderSent", Boolean.FALSE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllBuyersFor30DaysBeforeSubscriptionExpire() {
		final Query query = getEntityManager().createQuery("select b from Buyer b left outer join fetch b.currentSubscription cs left outer join fetch cs.nextSubscription ns left outer join fetch b.buyerPackage bp where b.status = :status and bp.subscriptionStatus != :subscriptionStatus and bp.userDaRemBefore30Day = :reminderSent");
		query.setParameter("status", BuyerStatus.ACTIVE);
		query.setParameter("subscriptionStatus", SubscriptionStatus.EXPIRED);
		query.setParameter("reminderSent", Boolean.FALSE);
		return query.getResultList();
	}

	@Override
	public void deleteIndustryCategoryByTanent(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from IndustryCategory where buyer.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteFavouriteSupplierByTanent(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from FavouriteSupplier where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();
	}

	@Override
	public void deleteAuditTrail(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from BuyerAuditTrail where tenantId =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();
	}

	@Override
	public void deleteUOM(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from Uom where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();
	}

	@Override
	public void deleteBuyerSettings(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from BuyerSettings where tenantId =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();
	}

	@Override
	public void deleteCostCenter(String tenantId) {

		StringBuilder hsql = new StringBuilder("delete from CostCenter where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteBusinessUnit(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from BusinessUnit where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteProductItem(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from ProductItem where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteProductCategory(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from ProductCategory where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteBuyerAddress(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from BuyerAddress where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteIdSettings(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from IdSettings where tenantId =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteBuyerById(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from Buyer where id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteBuyerPackage(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from BuyerPackage where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void setNullBuyerPackageBuyerSubscription(String tenantId) {
		StringBuilder hsql = new StringBuilder("update Buyer b set b.buyerPackage = null , b.currentSubscription = null , b.plan = null where id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteBuyerSubscription(String tenantId, boolean isNextsub) {

		String hsql = "delete from BuyerSubscription where buyer.id =:id ";
		if (isNextsub) {
			hsql += " and nextSubscription is null";
		} else {
			hsql += " and nextSubscription is not null";
		}

		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void deleteBuyerPaymentTransaction(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from PaymentTransaction where buyer.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	public void setUserNullInBuyer(String tenantId) {

		StringBuilder hsql = new StringBuilder("update Buyer b set b.actionBy = null where b.id =:id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> searchBuyerForPagination(String status, String order, String globalSearch, String pageNo) {
		LOG.info("Buyer Search enter....." + status);
		StringBuilder hql = new StringBuilder("select new com.privasia.procurehere.core.entity.Buyer(a.id, a.companyName, rc.countryName, a.status, a.companyRegistrationNumber,a.registrationCompleteDate,a.fullName, a.companyContactNumber, a.line1, a.line2, a.city ) from Buyer a  left outer join  a.registrationOfCountry rc  where 1 = 1 ");
		if (StringUtils.checkString(status).length() > 0 && BuyerStatus.ALL != BuyerStatus.valueOf(StringUtils.checkString(status).toUpperCase())) {
			hql.append("and a.status =:status ");
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			hql.append("and ( upper(a.companyName) like :companyName or upper(a.fullName) like :fullName or upper(a.companyRegistrationNumber) like :companyRegistrationNumber)");
		}

		if (StringUtils.checkString(order).equals("Newest")) {
			hql.append("order by a.registrationCompleteDate desc, a.subscriptionDate desc");
		} else {
			hql.append("order by a.registrationCompleteDate, a.subscriptionDate ");
		}
		LOG.info(hql.toString());
		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(status).length() > 0 && BuyerStatus.ALL != BuyerStatus.valueOf(StringUtils.checkString(status).toUpperCase())) {
			query.setParameter("status", BuyerStatus.valueOf(StringUtils.checkString(status).toUpperCase()));
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			query.setParameter("companyName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("fullName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("companyRegistrationNumber", "%" + globalSearch.toUpperCase() + "%");

		}
		query.setFirstResult(Integer.parseInt(pageNo) * 10);
		query.setMaxResults(10);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> findAllActiveMailBoxBuyers() {
		final Query query = getEntityManager().createQuery("from Buyer b where b.status = :status and b.enableMailbox = :enableMailbox  order by b.companyName asc");
		query.setParameter("status", BuyerStatus.ACTIVE);
		query.setParameter("enableMailbox", Boolean.TRUE);
		return query.getResultList();
	}

	@Override
	public Integer isExistPublicContextPathForBuyer(String publicContextPath, String buyerId) {
		final Query query = getEntityManager().createQuery("select count(b) from Buyer b where lower(b.publicContextPath) = :publicContextPath and b.id <> :buyerId");
		query.setParameter("publicContextPath", publicContextPath.toLowerCase());
		query.setParameter("buyerId", buyerId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getTenantIdByPublicContextPath(String buyerId) {
		final Query query = getEntityManager().createQuery("select b.id from Buyer b where lower(b.publicContextPath) = :publicContextPath");
		query.setParameter("publicContextPath", buyerId.toLowerCase());
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Buyer getContextPathForRegistration(String buyerId) {
		final Query query = getEntityManager().createQuery("select b from Buyer b where lower(b.publicContextPath) = :publicContextPath");
		query.setParameter("publicContextPath", buyerId.toLowerCase());
		List<Buyer> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getTenantId(String buyerId) {
		final Query query = getEntityManager().createQuery("select b.id from Buyer b where b.id = :buyerId");
		query.setParameter("buyerId", buyerId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getContextPathByBuyerId(String buyerId) {
		final Query query = getEntityManager().createQuery("select b.publicContextPath from Buyer b where b.id = :buyerId");
		query.setParameter("buyerId", buyerId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);

	}

	@Override
	@SuppressWarnings("unchecked")
	public RequestedAssociatedBuyerPojo getPublishedBuyerDetailsById(String buyerId) {
		String hql = "select distinct NEW com.privasia.procurehere.core.pojo.RequestedAssociatedBuyerPojo(b.id,b.companyName,b.publishedProfileCommunicationEmail, b.publishedProfileContactNumber,b.publishedProfileContactPerson,b.publishedProfileWebsite,b.publishedProfileInfoToSuppliers,b.publishedProfileMinimumCategories, b.publishedProfileMaximumCategories,b.publishedProfileIsAllowIndustryCat) from Buyer b where b.id=:buyerId";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("buyerId", buyerId);
		List<RequestedAssociatedBuyerPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerReportPojo> findAllSearchFilterBuyerReportList(TableDataInput input, Date startDate, Date endDate) {
		Query query = constructAllBuyersForOwnerQuery(input, false, startDate, endDate);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<Object[]> objects = query.getResultList();
		List<BuyerReportPojo> buyerReportPojoList = new ArrayList<>();
		objects.forEach(list -> {
			BuyerReportPojo buyerReportPojo = new BuyerReportPojo();
			buyerReportPojo.setId(list[0].toString());
			buyerReportPojo.setCompanyName(list[1].toString()!= null ? list[1].toString() : "");
			buyerReportPojo.setCompanyRegistrationNumber(list[2] != null ? list[2].toString() : "");
			buyerReportPojo.setCountry(list[3] != null ? list[3].toString() : "");
			buyerReportPojo.setCompanyType(list[4] != null ? list[4].toString() : "");
			buyerReportPojo.setPlanType(list[5] != null ? PlanType.valueOf(list[5].toString()) : null);
			buyerReportPojo.setStatus(list[6] != null ? BuyerStatus.fromString(list[6].toString()) : null);
			try {
				buyerReportPojo.setCreatedDate(list[7] != null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(list[7].toString()) : null);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			buyerReportPojoList.add(buyerReportPojo);
		});
		return buyerReportPojoList;

	}

	private Query constructAllBuyersForOwnerQuery(TableDataInput tableParams, boolean isCount, Date startDate, Date endDate) {

		String hql = "";
		String orderSql= "";
		String querySql= "";
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("planType")) {
						orderSql += " b.plan.planType " + dir + ",";
						querySql += " b.plan.planType " + ",";
					} else if (orderColumn.equalsIgnoreCase("country")) {
						orderSql += " b.registrationOfCountry.countryName " + dir + ",";
						querySql += " b.registrationOfCountry.countryName " + ",";
					} else if (orderColumn.equalsIgnoreCase("companyType")) {
						orderSql += " b.companyStatus.companystatus " + dir + ",";
						querySql += " b.companyStatus.companystatus " + ",";
					} else {
						orderSql += " b." + orderColumn + " " + dir + ",";
						querySql += " b." + orderColumn + " ,";
					}
				}
				if (orderSql.lastIndexOf(",") == orderSql.length() - 1) {
					orderSql = orderSql.substring(0, orderSql.length() - 1);
					querySql = querySql.substring(0, querySql.length() - 1);
				}
			} else {
				orderSql += " b.createdDate desc ";
				querySql += " b.createdDate ";
			}
		}

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct b) ";
		} else {
			hql += "select distinct b.id, b.companyName, b.companyRegistrationNumber, rc.countryName, cs.companystatus, p.planType, b.status, b.createdDate, " + querySql;
		}

		hql += " from Buyer b ";

		// If this is not a count query, only then add the join fetch. Count
		// query does not require its
		if (!isCount) {
			hql += " left outer join b.registrationOfCountry as rc left outer join b.companyStatus as cs left outer join b.currentSubscription crs left outer join crs.plan as p  ";
		}

		hql += " where 1=1";

		if (startDate != null && endDate != null) {
			hql += " and b.createdDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and b.status in (:status) ";
				} else if (cp.getData().equals("planType")) {
					hql += " and b.plan.planType = (:planType)";
				} else if (cp.getData().equalsIgnoreCase("country")) {
					hql += " and upper(b.registrationOfCountry.countryName ) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equalsIgnoreCase("companyType")) {
					hql += " and upper(b.companyStatus.companystatus ) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					hql += " and upper(b." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
//			List<OrderParameter> orderList = tableParams.getOrder();
//			if (CollectionUtil.isNotEmpty(orderList)) {
//				hql += " order by ";
//				for (OrderParameter order : orderList) {
//					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
//					String dir = order.getDir();
//					if (orderColumn.equalsIgnoreCase("planType")) {
//						hql += " b.plan.planType " + dir + ",";
//					} else if (orderColumn.equalsIgnoreCase("country")) {
//						hql += " b.registrationOfCountry.countryName " + dir + ",";
//					} else if (orderColumn.equalsIgnoreCase("companyType")) {
//						hql += " b.companyStatus.companystatus " + dir + ",";
//					} else {
//						hql += " b." + orderColumn + " " + dir + ",";
//					}
//				}
//				if (hql.lastIndexOf(",") == hql.length() - 1) {
//					hql = hql.substring(0, hql.length() - 1);
//				}
//			} else {
//				hql += " order by b.createdDate desc ";
//			}
			hql += " order by " + orderSql;
		}

		LOG.info("HQl >>>>> " + hql.toString());
		final Query query = getEntityManager().createQuery(hql.toString());
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

		}
		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					if (cp.getSearch().getValue().equalsIgnoreCase("ALL")) {
						List<BuyerStatus> statuss = Arrays.asList(BuyerStatus.values());
						query.setParameter("status", statuss);
					} else {
						query.setParameter("status", BuyerStatus.fromString(cp.getSearch().getValue().toUpperCase()));
					}
				} else if (cp.getData().equals("planType")) {
					query.setParameter("planType", PlanType.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		return query;

	}

	@Override
	public long findTotalSearchFilterBuyerReportCount(TableDataInput input, Date startDate, Date endDate) {
		Query query = constructAllBuyersForOwnerQuery(input, true, startDate, endDate);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalBuyerReportCount(Date startDate, Date endDate) {
		String hql = "select count(distinct b) from Buyer b where 1=1 ";
		if(startDate != null && endDate != null) {
			hql += " and b.createdDate between :startDate and :endDate ";
		}
		Query query = getEntityManager().createQuery(hql);
		if(startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		return ((Number) query.getSingleResult()).longValue();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BuyerReportPojo> getAllBuyerListForCsvReport(String[] buyrIds, BuyerSearchFilterPojo buyerReportPojo, boolean select_all, int pageSize, int pageNo, Date startDate, Date endDate) {
		String hql = "";

		hql = constructQueryForBuyerReport(buyrIds, buyerReportPojo, select_all, startDate, endDate);
		LOG.info("hql  : " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());

		if (!(select_all)) {
			if (buyrIds != null && buyrIds.length > 0) {
				query.setParameter("buyerIds", Arrays.asList(buyrIds));
			}
		}

		if(startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		
		if (buyerReportPojo != null) {
			if (StringUtils.checkString(buyerReportPojo.getCompanyname()).length() > 0) {
				query.setParameter("companyName", "%" + buyerReportPojo.getCompanyname().toUpperCase() + "%");
			}
			if (StringUtils.checkString(buyerReportPojo.getRegistrationnumber()).length() > 0) {
				query.setParameter("registrationNumber", "%" + buyerReportPojo.getRegistrationnumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(buyerReportPojo.getCompanytype()).length() > 0) {
				query.setParameter("companyType", "%" + buyerReportPojo.getCompanytype().toUpperCase() + "%");
			}
			if (StringUtils.checkString(buyerReportPojo.getCountry()).length() > 0) {
				query.setParameter("country", "%" + buyerReportPojo.getCountry().toUpperCase() + "%");
			}
			if (buyerReportPojo.getPlantype() != null) {
				query.setParameter("planType", Arrays.asList(buyerReportPojo.getPlantype()));
			}

			if (buyerReportPojo.getAccountstatus() != null) {
				query.setParameter("status", Arrays.asList(buyerReportPojo.getAccountstatus()));
			}
		}
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		List<BuyerReportPojo> finalList = query.getResultList();

		return finalList;
	}

	private String constructQueryForBuyerReport(String[] buyrIds, BuyerSearchFilterPojo buyerReportPojo, boolean select_all, Date startDate, Date endDate) {

		String hql = "";

		hql += "select distinct new com.privasia.procurehere.core.pojo.BuyerReportPojo(b.id, b.companyName, b.companyRegistrationNumber, ct.companystatus, b.yearOfEstablished, b.registrationCompleteDate,";

		hql += " b.status , b.line1 , b.line2 , b.city, b.postcode, c.countryName , s.stateName, b.companyContactNumber, b.faxNumber, b.companyWebsite, b.loginEmail, b.communicationEmail, b.mobileNumber, b.publicContextPath,";

		hql += " p.planName, cs.endDate , cs.userQuantity , cs.eventQuantity , b.createdDate, b.fullName, bp.noOfUsers, bp.noOfEvents, p.planType)";

		hql += " from Buyer b ";

		hql += " left outer join b.registrationOfCountry as c";

		hql += " left outer join b.companyStatus ct";

		hql += " left outer join b.state s";

		hql += " left outer join b.currentSubscription cs left outer join cs.plan p left outer join b.buyerPackage bp left outer join cs.plan as p ";

		hql += " where 1=1";

		if (!(select_all)) {
			if (buyrIds != null && buyrIds.length > 0) {
				hql += " and b.id in (:buyerIds)";
			}
		}

		if(startDate != null && endDate != null) {
			hql += " and b.createdDate between :startDate and :endDate ";
		}
		if (buyerReportPojo != null) {

			if (StringUtils.checkString(buyerReportPojo.getCompanyname()).length() > 0) {
				hql += " and upper(b.companyName) like :companyName";
			}
			if (StringUtils.checkString(buyerReportPojo.getRegistrationnumber()).length() > 0) {
				hql += " and upper(b.companyRegistrationNumber) like :registrationNumber";
			}
			if (StringUtils.checkString(buyerReportPojo.getCompanytype()).length() > 0) {
				hql += " and upper(ct.companystatus) like :companyType";
			}
			if (StringUtils.checkString(buyerReportPojo.getCountry()).length() > 0) {
				hql += " and upper(c.countryName) like :country";
			}
			if (buyerReportPojo.getPlantype() != null) {
				hql += " and upper(b.plan.planType) like :planType";
			}

			if (buyerReportPojo.getAccountstatus() != null) {
				hql += " and upper(b.status) like :status";
			}
		}

		hql += " order by b.createdDate desc ";

		return hql;

	}
	
	@Override
	public Buyer findBuyerById(String buyerId) {
		final Query query = getEntityManager().createQuery("select NEW Buyer(b.id, b.companyName, b.fullName) from Buyer b where b.id =:id");
		query.setParameter("id", buyerId);
		return (Buyer) query.getSingleResult();
	}

}
