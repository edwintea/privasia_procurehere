package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.privasia.procurehere.core.exceptions.NotAllowedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfaApprovalUser;
import com.privasia.procurehere.core.entity.RfaAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfaEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventApproval;
import com.privasia.procurehere.core.entity.RfaEventAwardApproval;
import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfaSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfaUnMaskedUser;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.pojo.BidHistoryPojo;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RfaEventDaoImpl extends GenericDaoImpl<RfaEvent, String> implements RfaEventDao {
	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	UserDao userDao;

	@Autowired
	private Environment env;

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvent findByEventId(String eventId) {
		try {
			// LOG.info("EVNET ID : " + eventId);
			final Query query = getEntityManager().createQuery("select r from RfaEvent r inner join fetch r.eventOwner eo left outer join fetch r.template t left outer join fetch r.depositCurrency left outer join fetch r.deliveryAddress  left outer join fetch r.revertBidUser  left outer join fetch r.previousAuction left outer join fetch r.participationFeeCurrency pfc left outer join fetch r.baseCurrency bc left outer join fetch r.costCenter cc left outer join fetch r.industryCategory ec left outer join fetch r.suppliers sup  left outer join fetch r.winningSupplier where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfaEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);

			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam) {
		LOG.info("Search Params : " + searchParam.toString());
		StringBuffer sb = new StringBuffer("select distinct fs from FavouriteSupplier fs inner join fetch fs.supplier as s where upper(s.companyName) like :companyName or upper(s.companyRegistrationNumber) like :companyRegistrationNumber order by s.companyName ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("companyName", "%" + searchParam.toUpperCase() + "%");
		query.setParameter("companyRegistrationNumber", "%" + searchParam.toUpperCase() + "%");

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfaEvent rfaEvent) {
		LOG.info("Referance No : " + rfaEvent.getReferanceNumber() + " Tenant Id : " + rfaEvent.getTenantId());
		StringBuilder hsql = new StringBuilder("select re from RfaEvent re where re.referanceNumber= :referanceNumber and re.id <> :id and re.tenantId= :tenantId ");

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("referanceNumber", rfaEvent.getReferanceNumber());
		query.setParameter("tenantId", rfaEvent.getTenantId());
		query.setParameter("id", rfaEvent.getId());

		List<RfaEvent> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllDraftEventsForBuyer(String tenantId) {
		final Query query = getEntityManager().createQuery("SELECT NEW com.privasia.procurehere.core.pojo.DraftEventPojo(e.id, e.eventName, e.createdBy, e.createdDate, e.modifiedDate,e.type) from RfxView e where e.tenantId = :tenantId and e.status = :status ");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", EventStatus.DRAFT);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OngoingEventPojo> getAllOngoingEventsForBuyer(String tenantId, Date lastLoginTime) {
		// All Ongoing events for Buyer
		Query query = getEntityManager().createQuery("SELECT NEW com.privasia.procurehere.core.pojo.OngoingEventPojo(e.id, e.eventName,e.type) from RfxView e where e.tenantId = :tenantId  and e.status = :status");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", EventStatus.ACTIVE);
		List<OngoingEventPojo> list = query.getResultList();
		// LOG.info("EventPojoList---------------------------" + list);

		if (CollectionUtil.isEmpty(list)) {
			return null;
		}

		List<String> ids = new ArrayList<String>();
		for (OngoingEventPojo obj : list) {
			// LOG.info("Got Event ID : " + obj.getId());
			ids.add(obj.getId());
		}

		// Find total bids received for each event along with unread bids
		query = getEntityManager().createQuery("SELECT e.id, e.supplierSubmittedTime from RfxView e where e.id in (:ids) and e.submitted = true");
		query.setParameter("ids", ids);
		List<Object[]> rows = query.getResultList();
		for (Object[] row : rows) {
			String id = ((String) row[0]);
			Date submittedTime = ((Date) row[1]);
			// int count = ((Number) row[1]).intValue();
			LOG.info("id : " + id + " - " + submittedTime);
			// Search original Obj in the list
			if (StringUtils.checkString(id).length() > 0) {
				OngoingEventPojo dummy = new OngoingEventPojo();
				dummy.setId(id);
				dummy = list.get(list.indexOf(dummy));
				dummy.setTotalBidCount(dummy.getTotalBidCount() + 1);
				if (submittedTime != null && submittedTime.after(lastLoginTime)) {
					dummy.setUnreadBidCount(dummy.getUnreadBidCount() + 1);
				}
			}
		}

		// Find total bids received for each event
		query = getEntityManager().createQuery("SELECT e.id, e.supplierEventReadTime from RfxView e where e.id in (:ids) ");
		query.setParameter("ids", ids);
		rows = query.getResultList();
		for (Object[] row : rows) {
			String id = ((String) row[0]);
			Date readTime = ((Date) row[1]);
			// int count = ((Number) row[1]).intValue();
			LOG.info("id : " + id + " - " + readTime);
			// Search original Obj in the list
			if (StringUtils.checkString(id).length() > 0) {
				OngoingEventPojo dummy = new OngoingEventPojo();
				dummy.setId(id);
				dummy = list.get(list.indexOf(dummy));
				dummy.setTotalSupplierCount(dummy.getTotalSupplierCount() + 1);
				if (readTime != null) {
					dummy.setReadSupplierCount(dummy.getReadSupplierCount() + 1);
				}
			}
		}
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FinishedEventPojo> getAllFinishedEventsForBuyer(String tenantId, Date lastLoginTime, int days) {
		// All Ongoing events for Buyer
		Calendar today = Calendar.getInstance();
		Date rangeEnd = today.getTime();
		today.add(Calendar.DATE, -days);
		Date rangeStart = today.getTime();

		Query query = getEntityManager().createQuery("SELECT NEW com.privasia.procurehere.core.pojo.FinishedEventPojo(e.id, e.eventName,e.eventStart,e.eventEnd,e.type) from RfxView e where e.tenantId = :tenantId and status = :status and e.eventEnd between :rangeStart and :rangeEnd");
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", EventStatus.FINISHED);
		query.setParameter("rangeStart", rangeStart);
		query.setParameter("rangeEnd", rangeEnd);
		List<FinishedEventPojo> list = query.getResultList();
		LOG.info("EventPojoList---------------------------" + list);

		if (CollectionUtil.isEmpty(list)) {
			return null;
		}

		List<String> ids = new ArrayList<String>();
		for (FinishedEventPojo obj : list) {
			// LOG.info("Got Event ID : " + obj.getId());
			ids.add(obj.getId());
		}
		return list;
	}

	/*
	 * @SuppressWarnings("unchecked")
	 * @Override public FavouriteSupplier getFavouriteSupplierBySupplierId(String supId) { StringBuffer sb = new
	 * StringBuffer(
	 * "select distinct fs from FavouriteSupplier fs left outer join fetch fs.buyer as b left outer join fetch fs.supplier as s where s.id=:sid and b.id=:bid"
	 * ); LOG.info("SQL : " + sb.toString()); try { final Query query = getEntityManager().createQuery(sb.toString());
	 * query.setParameter("sid", supId); query.setParameter("bid",
	 * SecurityLibrary.getLoggedInUser().getBuyer().getId()); LOG.info("supId : " + supId + "Buyer Id :" +
	 * SecurityLibrary.getLoggedInUser().getBuyer().getId()); List<FavouriteSupplier> favList = query.getResultList();
	 * if (CollectionUtil.isNotEmpty(favList)) { return favList.get(0); } else { return null; } } catch
	 * (NoResultException nr) { LOG.info("Error while getting user : " + nr.getMessage(), nr); return null; } }
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> findAllEventForSupplier(String supplierId) {
		LOG.info("Supplier Id : " + supplierId);
		final Query query = getEntityManager().createQuery("from RfxView as e inner join fetch e.participationFeeCurrency as cu inner join fetch e.eventOwner eo inner join fetch eo.buyer as by where e.status = :status and e.supplier.id = :id");
		query.setParameter("status", EventStatus.ACTIVE);
		query.setParameter("id", supplierId);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfaEvent findEventForCqByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r left outer join fetch r.cqs eo where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfaEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public RfaEvent findBySupplierEventId(String id) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r inner join fetch r.eventOwner as eo left outer join fetch r.eventBqs as bq where r.id =:eventId");
			query.setParameter("eventId", id);
			@SuppressWarnings("unchecked")
			List<RfaEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateDuctionAuctionWinningSupplier(String eventId, Supplier winningSupplier, BigDecimal winningPrice, EventStatus status) {
		StringBuilder hsql = new StringBuilder("update RfaEvent e set e.status = :status, e.auctionComplitationTime = :auctionComplitationTime, e.winningPrice = :winningPrice, e.winningSupplier = :winningSupplier where e.id = :eventId ");
		LOG.info("event Id : " + eventId + " : " + winningSupplier + " : " + winningPrice);
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("auctionComplitationTime", new Date());
		query.setParameter("eventId", eventId);
		query.setParameter("status", status);
		query.setParameter("winningPrice", winningPrice);
		query.setParameter("winningSupplier", winningSupplier);
		int ret = query.executeUpdate();
		LOG.info("Ret : " + ret);
	}

	@Override
	public Integer getCountOfEnvelopByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(ev) from RfaEvent e inner join e.rfaEnvelop ev  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		LOG.info("Envelop Count  :   " + count);
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllRfaEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchVal, String indCat) {
		StringBuffer sb = new StringBuffer("SELECT e.ID as id,e.AUCTION_TYPE as auctionType,e.EVENT_NAME as eventName,e.STATUS as eventStatus,e.EVENT_DESCRIPTION as descripiton, CASE WHEN template.TEMPLATE_STATUS='INACTIVE' THEN  1  ELSE 0 END as templateStatus, e.REFERANCE_NUMBER as referenceNumber,e.EVENT_ID as eventId,e.EVENT_START as startDate,e.EVENT_END as endDate, (SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories FROM PROC_RFA_EVENTS e ");
		sb.append(" LEFT OUTER JOIN PROC_USER  eventOwner on e.EVENT_OWNER = eventOwner.ID  LEFT OUTER JOIN PROC_RFX_TEMPLATE   template on e.TEMPLATE_ID = template.ID ");
		if (StringUtils.checkString(indCat).length() > 0) {
			sb.append(" INNER JOIN  PROC_RFA_EVENT_INDUS_CAT cat ON cat.EVENT_ID = e.ID ");
		}
		sb.append(" WHERE e.TENANT_ID =:tenantId ");

		if (StringUtils.checkString(loggedInUser).length() > 0) {
			sb.append(" AND e.EVENT_OWNER = :eventOwner ");
		}
		if (StringUtils.checkString(searchVal).length() > 0) {
			sb.append(" AND (UPPER(e.EVENT_NAME ) like :searchValue or upper(e.REFERANCE_NUMBER) like :searchValue or upper(e.EVENT_ID) like :searchValue)  ");
		}
		if (StringUtils.checkString(indCat).length() > 0) {
			sb.append(" AND cat.IND_CAT_ID = :indCat ");
		}
		sb.append("order by e.CREATED_DATE desc ");
		final Query query = getEntityManager().createNativeQuery(sb.toString(), "copyFromPreviousEvent");
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(searchVal).length() > 0) {
			query.setParameter("searchValue", "%" + searchVal.toUpperCase() + "%");
		}

		if (StringUtils.checkString(indCat).length() > 0) {
			query.setParameter("indCat", indCat);
		}
		LOG.info("teanant id " + tenantId + " Ind cat Id " + indCat + "searchVal " + searchVal.toUpperCase());
		LOG.info(sb.toString());

		query.setFirstResult(Integer.parseInt(pageNo) * 10);
		query.setMaxResults(10);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("eventOwner", loggedInUser);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		final Query query = getEntityManager().createQuery("select ss from RfaEvent e left outer join e.suppliers s inner join s.supplier ss where e.id =:eventId order by ss.companyName");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser) {
		StringBuffer sb = new StringBuffer("select distinct e from RfaEvent e left outer join fetch e.industryCategories i where e.tenantId = :tenantId ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(e.eventName) like :searchValue or upper(e.referanceNumber) like :searchValue or upper(e.eventId) like :searchValue) ");
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			sb.append(" and i.id = :industryCategory  ");
		}
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			sb.append(" and e.eventOwner.id = :loggedInUser ");
		}
		sb.append(" order by e.eventName");

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);

		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
			LOG.info("searchValue" + searchValue.toUpperCase());
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			query.setParameter("industryCategory", industryCategory);
		}
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("loggedInUser", loggedInUser);
		}

		query.setFirstResult(0);
		query.setMaxResults(10);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> getAllFinishAndApprovedRfaEventByTenantId(String tenantId) {
		final Query query = getEntityManager().createQuery("from RfaEvent re left outer join fetch re.industryCategory where re.tenantId =:tenantId and re.status in (:status)");
		query.setParameter("tenantId", tenantId);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.APPROVED);
		list.add(EventStatus.FINISHED);
		list.add(EventStatus.COMPLETE);
		query.setParameter("status", list);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> findRfaEventByNameAndTenantId(String searchValue, String tenantId) {
		LOG.info("search value : " + searchValue);
		// StringBuilder hsql = new StringBuilder("select distinct new
		// com.privasia.procurehere.core.entity.RfaEvent(e.eventName, e.id) from RfaEvent e where e.status in (:status)
		// and e.tenantId = :tenantId and e.eventDetailCompleted = :eventDetailCompleted");
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaEvent(e.id, e.eventName, e.status) from RfaEvent e where e.status in (:status) and e.tenantId = :tenantId and e.eventDetailCompleted = :eventDetailCompleted ");
		// StringBuilder hsql = new StringBuilder("from RfaEvent ra left outer join fetch ra.createdBy as cb left outer
		// join fetch ra.modifiedBy as mb where ra.status in (:status) and ra.tenantId = :tenantId and
		// ra.eventDetailCompleted = :eventDetailCompleted");
		if (StringUtils.checkString(searchValue).length() > 0) {
			hsql.append(" AND upper(e.eventName) like :eventName");
		}
		hsql.append(" order by e.eventName");
		final Query query = getEntityManager().createQuery(hsql.toString());
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.DRAFT);
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.PENDING);
		list.add(EventStatus.APPROVED);
		query.setParameter("status", list);
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventDetailCompleted", Boolean.TRUE);
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("eventName", "%" + StringUtils.checkString(searchValue).toUpperCase() + "%");
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaTeamMember getRfaTeamMemberByUserIdAndEventId(String eventId, String userId) {
		final Query query = getEntityManager().createQuery("from RfaTeamMember r where r.event.id =:eventId and r.user.id= :userId");
		query.setParameter("eventId", eventId);
		query.setParameter("userId", userId);
		List<RfaTeamMember> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("from RfaTeamMember tm left outer join fetch tm.user u where tm.event.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfaEvent findEventForBqByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r left outer join fetch r.eventBqs eo where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RfaEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaEventApproval> getAllApprovalsForEvent(String id) {
		final Query query = getEntityManager().createQuery("select a from RfaEvent e join e.approvals a where e.id =:id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {
		LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		RfaEvent event = getPlainEventById(eventId);

		// if (event.getUnMaskedUser() != null && event.getUnMaskedUser().getId().equals(userId) && EventStatus.COMPLETE
		// == event.getStatus() && Boolean.FALSE == event.getDisableMasking()) {
		// permissions.setUnMaskUser(true);
		// }

		// ph-773
		for (RfaUnMaskedUser rfiUnMaskedUser : event.getUnMaskedUsers()) {
			if (rfiUnMaskedUser.getUser() != null && rfiUnMaskedUser.getUser().getId().equals(userId) && EventStatus.COMPLETE == event.getStatus() && Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
				permissions.setUnMaskUser(true);
			}
		}

		for (RfaEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId) && EventStatus.CLOSED == event.getStatus() && Boolean.FALSE == user.getConcluded()) {
				permissions.setEvaluationConclusionUser(true);
			}
		}

		boolean isAllUserConcludedDone = false;
		if (event.getEnableEvaluationConclusionUsers()) {
			isAllUserConcludedDone = true;
		}
		for (RfaEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId)) {
				permissions.setConclusionUser(true);
			}
			if (user.getConcluded() == null || Boolean.FALSE == user.getConcluded()) {
				isAllUserConcludedDone = false;
			}
		}
		permissions.setAllUserConcludedPermatury(isAllUserConcludedDone);

		if ((AuctionType.FORWARD_ENGISH == event.getAuctionType() || AuctionType.REVERSE_ENGISH == event.getAuctionType()) && EventStatus.SUSPENDED == event.getStatus() && Boolean.TRUE == event.getRevertLastBid() && event.getRevertBidUser() != null && event.getRevertBidUser().getId().equals(userId)) {
			permissions.setRevertBidUser(true);
		}

		if (event.getEventOwner().getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<EventTeamMember> teamMembers = getTeamMembersForEvent(eventId);
			for (EventTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
					}
					if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
						permissions.setEditor(false);
						permissions.setViewer(false);
						permissions.setOwner(true);
						break;
					}
				}
			}
		}

		// Approver
		List<RfaEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfaEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfaApprovalUser> users = approval.getApprovalUsers();
				for (RfaApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}

		// Suspension Approver
		List<RfaEventSuspensionApproval> suspApprovals = event.getSuspensionApprovals();
		for (RfaEventSuspensionApproval approval : suspApprovals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfaSuspensionApprovalUser> users = approval.getApprovalUsers();
				for (RfaSuspensionApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveSuspensionApproval(true);
							break;
						}
					}
				}
			}
		}

		// Award Approver
		List<RfaEventAwardApproval> awardApprovals = event.getAwardApprovals();
		for (RfaEventAwardApproval approval : awardApprovals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfaAwardApprovalUser> users = approval.getApprovalUsers();
				for (RfaAwardApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setAwardApprover(true);
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveAwardApproval(true);
							break;
						}
					}
				}
			}
		}

		// Envelop Opener and Evaluator
		List<RfaEnvelop> envelopes = rfaEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfaEnvelop envelop : envelopes) {
				List<RfaEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfaEvaluatorUser user : evaluators) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setEvaluator(true);
					}
				}

				if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(userId)) {
					permissions.setLeadEvaluator(true);
					if (!permissions.isOwner()) {
						permissions.setViewer(true);
					}
				}
				// if (envelop.getOpener() != null && envelop.getOpener().getId().equals(userId)) {
				// permissions.setOpener(true);
				// }
				List<RfaEnvelopeOpenerUser> openerUser = envelop.getOpenerUsers();
				for (RfaEnvelopeOpenerUser user : openerUser) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setOpener(true);
					}
				}
			}
		}

		return permissions;

	}

	@Override
	public EventPermissions getUserEventPemissions(String userId, String eventId) {
		LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			permissions.setApproverUser(true);
		}

		// Event Owner
		RfaEvent event = getPlainEventById(eventId);

		// ph-773
		for (RfaUnMaskedUser rfiUnMaskedUser : event.getUnMaskedUsers()) {
			if (rfiUnMaskedUser.getUser() != null && rfiUnMaskedUser.getUser().getId().equals(userId) && EventStatus.COMPLETE == event.getStatus() && Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
				permissions.setUnMaskUser(true);
			}
		}

		for (RfaEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId) && EventStatus.CLOSED == event.getStatus() && Boolean.FALSE == user.getConcluded()) {
				permissions.setEvaluationConclusionUser(true);
			}
		}

		for (RfaEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId)) {
				permissions.setConclusionUser(true);
			}
		}

		if ((AuctionType.FORWARD_ENGISH == event.getAuctionType() || AuctionType.REVERSE_ENGISH == event.getAuctionType()) && EventStatus.SUSPENDED == event.getStatus() && Boolean.TRUE == event.getRevertLastBid() && event.getRevertBidUser() != null && event.getRevertBidUser().getId().equals(userId)) {
			permissions.setRevertBidUser(true);
		}

		if (event.getEventOwner().getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<EventTeamMember> teamMembers = getTeamMembersForEvent(eventId);
			for (EventTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
					}
					if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
						permissions.setEditor(false);
						permissions.setViewer(false);
						permissions.setOwner(true);
						break;
					}
				}
			}
		}

		// Approver
		List<RfaEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfaEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfaApprovalUser> users = approval.getApprovalUsers();
				for (RfaApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}

		// Envelop Opener and Evaluator
		List<RfaEnvelop> envelopes = rfaEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfaEnvelop envelop : envelopes) {
				List<RfaEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfaEvaluatorUser user : evaluators) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setEvaluator(true);
					}
				}

				if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(userId)) {
					permissions.setLeadEvaluator(true);
				}
				if (envelop.getOpener() != null && envelop.getOpener().getId().equals(userId)) {
					permissions.setOpener(true);
				}

				if (envelop.getOpenerUsers() != null) {
					for (RfaEnvelopeOpenerUser op : envelop.getOpenerUsers()) {
						if (op.getUser().getId().equals(userId)) {
							permissions.setOpener(true);
							break;
						}

					}
				}

			}
		}

		return permissions;

	}

	@Override
	public EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId) {
		LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();

		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		RfaEvent event = getPlainEventById(eventId);
		if (event.getEventOwner().getId().equals(userId)) {
			permissions.setOwner(true);
		} else {
			// Viewer Editor
			List<EventTeamMember> teamMembers = getTeamMembersForEvent(eventId);
			for (EventTeamMember member : teamMembers) {
				if (member.getUser().getId().equals(userId)) {
					if (member.getTeamMemberType() == TeamMemberType.Viewer) {
						permissions.setViewer(true);
					}
					if (member.getTeamMemberType() == TeamMemberType.Editor) {
						permissions.setEditor(true);
						permissions.setViewer(false);
						// break;
					}
					if (member.getTeamMemberType() == TeamMemberType.Associate_Owner) {
						permissions.setEditor(false);
						permissions.setViewer(false);
						permissions.setOwner(true);
						break;
					}
				}
			}
		}

		// Approver
		List<RfaEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RfaEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RfaApprovalUser> users = approval.getApprovalUsers();
				for (RfaApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}

		// Envelop Opener and Evaluator
		List<RfaEnvelop> envelopes = rfaEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RfaEnvelop envelop : envelopes) {
				if (!envelop.getId().equals(envelopeId)) {
					continue;
				}
				List<RfaEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RfaEvaluatorUser user : evaluators) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setEvaluator(true);
					}
				}

				if (envelop.getLeadEvaluater() != null && envelop.getLeadEvaluater().getId().equals(userId)) {
					permissions.setLeadEvaluator(true);
					if (!permissions.isOwner()) {
						permissions.setViewer(true);
					}
				}

				List<RfaEnvelopeOpenerUser> openerUser = envelop.getOpenerUsers();
				for (RfaEnvelopeOpenerUser user : openerUser) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setOpener(true);
					}
				}
			}
		}
		return permissions;
	}

	@Override
	public BidHistoryPojo getMinMaxBidPriceForEvent(String eventId) {
		BidHistoryPojo ret = new BidHistoryPojo();

		StringBuilder hsql = new StringBuilder("select min(ab.amount),max(ab.amount) from AuctionBids as ab join ab.event e where e.id = :eventId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		Object[] data = (Object[]) query.getSingleResult();

		if (data != null) {
			BigDecimal min = new BigDecimal(data[0] != null ? ((Number) data[0]).doubleValue() : 0);
			BigDecimal max = new BigDecimal(data[1] != null ? ((Number) data[1]).doubleValue() : 0);
			ret.setMinPrice(min);
			ret.setMaxPrice(max);
		}
		return ret;
	}

	@Override
	public RfaEvent getLeanEventbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from RfaEvent e inner join fetch e.eventOwner eo inner join fetch e.baseCurrency bc where e.id =:eventId");
		query.setParameter("eventId", eventId);
		return (RfaEvent) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getAwardedSuppliers(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct s from RfaEvent e left outer join e.awardedSuppliers s where e.id =:eventId order by s.companyName");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	public RfaEvent getPlainEventById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RfaEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> getAllRfaEventByLoginId(String loginId) {

		String sql = "select re from RfaEvent re inner join re.createdBy cr where cr.id =:loginId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("loginId", loginId);
		return query.getResultList();
	}

	@Override
	public RfaEvent getRfaEventForBidHistory(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaEvent(e.status, e.eventStart, e.eventEnd) from RfaEvent e where e.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (RfaEvent) query.getSingleResult();
	}

	@Override
	public EventStatus checkRelativeEventStatus(String eventId) {
		LOG.info("event id for statuc : " + eventId);
		StringBuilder hsql = new StringBuilder("select pa.status from RfaEvent e inner join e.previousAuction pa where e.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);

		return (EventStatus) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> getAllAssosiateAuction(String eventId) {
		StringBuilder hsql = new StringBuilder("select re from RfaEvent re inner join re.previousAuction pa where pa.id =:eventId and re.status = :status");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", EventStatus.ACTIVE);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> getAllAssosiateAuctionForReschdule(String eventId) {
		StringBuilder hsql = new StringBuilder("select re from RfaEvent re inner join re.previousAuction pa where pa.id =:eventId and re.status in (:status)");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.DRAFT);
		list.add(EventStatus.SUSPENDED);
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.PENDING);
		list.add(EventStatus.APPROVED);
		query.setParameter("status", list);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getEventSuppliersId(String id) {
		Query query = getEntityManager().createQuery("select s.supplier.id from RfaEvent e left outer join e.suppliers s where e.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EventSupplierPojo> getEventSuppliersAndTimeZone(String id) {
		Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.EventSupplierPojo(s.supplier.id,s.supplier.companyName, ss.timeZone.timeZone) from RfaEventSupplier s inner join s.rfxEvent e, SupplierSettings ss where ss.supplier.id = s.supplier.id and  e.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaTeamMember> getBuyerTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("from RfaTeamMember tm where tm.event.id = :eventId").setParameter("eventId", eventId);
		return (List<RfaTeamMember>) query.getResultList();
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(u.id, u.name, u.communicationEmail, u.emailNotifications, u.tenantId) from RfaEvent e inner join e.eventOwner u where e.id = :eventId");
		query.setParameter("eventId", eventId);
		return (User) query.getSingleResult();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlag(String eventId) {
		Query query = getEntityManager().createQuery("update RfaEvent e set e.startMessageSent = true where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUserBuyerTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(tm.user.id, tm.user.name, tm.user.communicationEmail, tm.user.emailNotifications, tm.user.tenantId) from RfaTeamMember tm where tm.event.id = :eventId").setParameter("eventId", eventId);
		return (List<User>) query.getResultList();
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		StringBuilder hql = new StringBuilder("select count(e) from RfaEvent e where e.template.id =:templateId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("templateId", templateId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@Override
	public RfaEvent getRfaEventForTimeExtensionAndBidSubmission(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaEvent(e.id, e.tenantId,  e.eventStart,e.eventEnd,e.status,e.auctionType,e.auctionDuration,e.auctionStartRelative,e.timeExtensionType,e.timeExtensionDurationType,e.timeExtensionDuration,e.timeExtensionLeadingBidType,e.timeExtensionLeadingBidValue,e.extensionCount,e.bidderDisqualify,e.autoDisqualify,c.id,b.id,e.decimal,e.totalExtensions) from RfaEvent e join e.createdBy c join c.buyer b where e.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (RfaEvent) query.getSingleResult();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateTimeExtension(String eventId, Integer totalExtensions, Date eventEnd) {
		LOG.info("Total Extension Count : " + totalExtensions);
		StringBuilder hsql = new StringBuilder("update RfaEvent e set e.totalExtensions = (e.totalExtensions + 1), e.eventEnd = :eventEnd where e.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("eventEnd", eventEnd);
		// query.setParameter("totalExtensions", totalExtensions);
		query.executeUpdate();
	}

	@Override
	public RfaEvent getEventNameAndReferenceNumberById(String id) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaEvent(e.id,e.eventName,e.referanceNumber) from RfaEvent e where e.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", id);
		return (RfaEvent) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from RfaTeamMember tm left outer join tm.user u where tm.event.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String findBusinessUnitName(String eventId) {
		StringBuilder hsql = new StringBuilder("select bu.displayName from RfaEvent as a  left outer join a.businessUnit as bu where a.id =:eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		List<String> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvent getMobileEventDetails(String id) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r inner join fetch r.eventOwner eo left outer join fetch r.baseCurrency bc left outer join fetch r.eventBqs where r.id =:eventId");
			query.setParameter("eventId", id);
			List<RfaEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting Event : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfaSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId) {
		final Query rfaSupplierBqQuery = getEntityManager().createQuery("select distinct sb from RfaSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.grandTotal = (select min(r.grandTotal) from RfaSupplierBq r where r.event.id =:eventId and r.bq.id =:bqId and r.supplier.id in (select es.supplier.id from RfaEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = false and es.submissionStatus=:submissionStatus )) and sb.event.id =:eventId and sb.bq.id =:bqId and s.id in (select ess.supplier.id from RfaEventSupplier ess where ess.rfxEvent.id = :eventId and ess.disqualify = false  and ess.submissionStatus=:submissionStatus )");
		rfaSupplierBqQuery.setParameter("eventId", eventId);
		rfaSupplierBqQuery.setParameter("bqId", bqId);
		rfaSupplierBqQuery.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfaSupplierBq> rfaSupplierBqList = rfaSupplierBqQuery.getResultList();
		if (CollectionUtil.isNotEmpty(rfaSupplierBqList)) {
			// if more suppliers have same lowest price then sending the first submit supplier.
			if (rfaSupplierBqList.size() > 0) {
				List<String> suppIds = new ArrayList<>();
				for (RfaSupplierBq rfaSupplierBq : rfaSupplierBqList) {
					LOG.info("suuplier name: " + rfaSupplierBq.getSupplier().getCompanyName() + "======= rfaSupplierBq:  " + rfaSupplierBq.getGrandTotal());
					suppIds.add(rfaSupplierBq.getSupplier().getId());
				}
				Query bidHistoryQuery = getEntityManager().createQuery("select distinct ab from AuctionBids ab left outer join fetch ab.bidBySupplier sup  where sup.id in (:suppIds) and ab.event.id = :eventId and ab.bidSubmissionDate in (select max(abs.bidSubmissionDate) from AuctionBids abs where abs.bidBySupplier.id in (:suppIds) and abs.event.id = :eventId  group by abs.bidBySupplier.id) ");
				bidHistoryQuery.setParameter("eventId", eventId);
				bidHistoryQuery.setParameter("suppIds", suppIds);
				List<AuctionBids> auctionBidsList = bidHistoryQuery.getResultList();
				if (CollectionUtil.isNotEmpty(auctionBidsList)) {
					Collections.sort(auctionBidsList, new Comparator<AuctionBids>() {
						public int compare(AuctionBids o1, AuctionBids o2) {
							return o1.getBidSubmissionDate().compareTo(o2.getBidSubmissionDate());
						}
					});

					if (auctionBidsList.get(0) != null) {
						for (RfaSupplierBq rfaSupplierBq : rfaSupplierBqList) {
							if (rfaSupplierBq.getSupplier().getId().equals(auctionBidsList.get(0).getBidBySupplier().getId())) {
								return rfaSupplierBq;
							}
						}
					}

				} else {

					bidHistoryQuery = getEntityManager().createQuery("select distinct ab from RfaEventSupplier ab left outer join fetch ab.supplier sup  where sup.id in (:suppIds) and ab.rfxEvent.id = :eventId and ab.supplierSubmittedTime in (select max(abs.supplierSubmittedTime) from RfaEventSupplier abs where abs.supplier.id in (:suppIds) and abs.rfxEvent.id = :eventId  group by abs.supplier.id) ");
					bidHistoryQuery.setParameter("eventId", eventId);
					bidHistoryQuery.setParameter("suppIds", suppIds);
					List<RfaEventSupplier> supplierEventList = bidHistoryQuery.getResultList();

					/*
					 * for (RfaEventSupplier rftEventSupplier : supplierEventList) { LOG.info("-------------------" +
					 * rftEventSupplier.getSupplierSubmittedTime() + "------------" +
					 * rftEventSupplier.getSupplier().getCompanyName()); }
					 */

					if (CollectionUtil.isNotEmpty(supplierEventList)) {
						Collections.sort(supplierEventList, new Comparator<RfaEventSupplier>() {

							public int compare(RfaEventSupplier o1, RfaEventSupplier o2) {
								return o1.getSupplierSubmittedTime().compareTo(o2.getSupplierSubmittedTime());
							}
						});

						if (supplierEventList.get(0) != null) {
							for (

							RfaSupplierBq rfaSupplierBq : rfaSupplierBqList) {
								if (rfaSupplierBq.getSupplier().getId().equals(supplierEventList.get(0).getSupplier().getId())) {
									return rfaSupplierBq;
								}
							}
						}

					}

				}
			}
			return rfaSupplierBqList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaSupplierBq> getSupplierBQOfLowestItemisedPrize(String eventId, String bqId) {
		final Query query = getEntityManager().createQuery("select distinct sb from RfaSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.event.id =:eventId and sb.bq.id =:bqId ");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		List<RfaSupplierBq> uList = query.getResultList();
		return uList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfaSupplierBqItem getMinItemisePrice(String id, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct sb from RfaSupplierBqItem sb left outer join fetch sb.supplier s left outer join fetch sb.event e  left outer join fetch sb.bqItem bi where sb.totalAmount = (select min(r.totalAmount) from RfaSupplierBqItem r where r.bqItem.id = :bqItemId  and r.supplier.id in (select es.supplier.id from RfaEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = false)) and bi.id = :bqItemId and s.id in (select ess.supplier.id from RfaEventSupplier ess where ess.rfxEvent.id = :eventId and ess.disqualify = false )");
		query.setParameter("bqItemId", id);
		query.setParameter("eventId", eventId);
		List<RfaSupplierBqItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {

			if (uList.size() > 0) {
				List<String> suppIds = new ArrayList<>();
				for (RfaSupplierBqItem rfaSupplierBq : uList) {
					LOG.info("suuplier name: " + rfaSupplierBq.getSupplier().getCompanyName() + "======= rfaSupplierBq:  " + rfaSupplierBq.getUnitPrice());
					suppIds.add(rfaSupplierBq.getSupplier().getId());
				}
				Query bidHistoryQuery = getEntityManager().createQuery("select distinct ab from AuctionBids ab left outer join fetch ab.bidBySupplier sup  where sup.id in (:suppIds) and ab.event.id = :eventId and ab.bidSubmissionDate in (select max(abs.bidSubmissionDate) from AuctionBids abs where abs.bidBySupplier.id in (:suppIds) and abs.event.id = :eventId  group by abs.bidBySupplier.id) ");
				bidHistoryQuery.setParameter("eventId", eventId);
				bidHistoryQuery.setParameter("suppIds", suppIds);
				List<AuctionBids> auctionBidsList = bidHistoryQuery.getResultList();

				if (CollectionUtil.isNotEmpty(auctionBidsList)) {
					/*
					 * for (AuctionBids auctionBids : auctionBidsList) { LOG.info("suuplier name: " +
					 * auctionBids.getBidBySupplier().getCompanyName() + "<---------->" +
					 * auctionBids.getBidSubmissionDate()); }
					 */
					Collections.sort(auctionBidsList, new Comparator<AuctionBids>() {
						public int compare(AuctionBids o1, AuctionBids o2) {
							return o1.getBidSubmissionDate().compareTo(o2.getBidSubmissionDate());
						}
					});

					if (auctionBidsList.get(0) != null) {
						for (RfaSupplierBqItem rfaSupplierBq : uList) {
							if (rfaSupplierBq.getSupplier().getId().equals(auctionBidsList.get(0).getBidBySupplier().getId())) {
								return rfaSupplierBq;
							}
						}
					}

					else {

						bidHistoryQuery = getEntityManager().createQuery("select distinct ab from RfaEventSupplier ab left outer join fetch ab.supplier sup  where sup.id in (:suppIds) and ab.rfxEvent.id = :eventId and ab.supplierSubmittedTime in (select max(abs.supplierSubmittedTime) from RfaEventSupplier abs where abs.supplier.id in (:suppIds) and abs.rfxEvent.id = :eventId  group by abs.supplier.id)  ");
						bidHistoryQuery.setParameter("eventId", eventId);
						bidHistoryQuery.setParameter("suppIds", suppIds);
						List<RfaEventSupplier> rfaAuctionBidsList = bidHistoryQuery.getResultList();
						if (CollectionUtil.isNotEmpty(rfaAuctionBidsList)) {
							/*
							 * for (RfaEventSupplier rftEventSupplier : rfaAuctionBidsList) {
							 * LOG.info("-------------------" + rftEventSupplier.getSupplierSubmittedTime() +
							 * "------------" + rftEventSupplier.getSupplier().getCompanyName()); }
							 */
							Collections.sort(rfaAuctionBidsList, new Comparator<RfaEventSupplier>() {
								public int compare(RfaEventSupplier o1, RfaEventSupplier o2) {
									return o1.getSupplierSubmittedTime().compareTo(o2.getSupplierSubmittedTime());
								}
							});

							if (rfaAuctionBidsList.get(0) != null) {
								for (RfaSupplierBqItem rftSupplierBq : uList) {
									if (rftSupplierBq.getSupplier().getId().equals(rfaAuctionBidsList.get(0).getSupplier().getId())) {
										return rftSupplierBq;
									}
								}
							}

						}
					}

				}
			}

			return uList.get(0);

		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfaSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RfaSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.event.id =:eventId and sb.bq.id =:bqId and s.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public boolean isExistsRfaEventId(String tenantId, String eventId) {
		StringBuilder hsql = new StringBuilder("select count(p) from RfaEvent p  where p.eventId = :eventId and p.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventId", eventId);
		return (((Number) query.getSingleResult()).longValue() > 0);
	}

	/*********************************************** delete data *****************************************************/
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventIdList(String tenantId) {
		StringBuilder hsql = new StringBuilder("select id from RfaEvent p  where p.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAuctionBids(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from AuctionBids pc where pc.event.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAuctionRules(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from AuctionRules pc where pc.event.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllComments(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaComment pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllDocument(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventDocument pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getenvelopIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfaEnvelop p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvaluatorUser(String envelopID) {
		StringBuilder hsql = new StringBuilder("delete from RfaEvaluatorUser pc where pc.envelope.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopID);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEnvelop(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEnvelop pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAddress(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventCorrespondenceAddress pc where pc.rfaEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getApprovalIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfaEventApproval p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteApprovalUser(String aprovalID) {
		StringBuilder hsql = new StringBuilder("delete from RfaApprovalUser pc where pc.approval.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", aprovalID);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAproval(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventApproval p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAudit(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventAudit p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCreatedByRecordUserID(String userId) {

		Query query = getEntityManager().createQuery("delete from RfiEventAudit p  where p.actionBy.id = :id ").setParameter("id", userId);
		query.executeUpdate();

		Query query1 = getEntityManager().createQuery("delete from RfaEventAudit p  where p.actionBy.id = :id ").setParameter("id", userId);
		query1.executeUpdate();

		Query query2 = getEntityManager().createQuery("delete from RfpEventAudit p  where p.actionBy.id = :id ").setParameter("id", userId);
		query2.executeUpdate();

		Query query3 = getEntityManager().createQuery("delete from RfqEventAudit p  where p.actionBy.id = :id ").setParameter("id", userId);
		query3.executeUpdate();

		Query query4 = getEntityManager().createQuery("delete from RftEventAudit p  where p.actionBy.id = :id ").setParameter("id", userId);
		query4.executeUpdate();

		Query query5 = getEntityManager().createQuery("update Supplier s set s.createdBy = null  where s.createdBy.id = :id ").setParameter("id", userId);
		query5.executeUpdate();

		Query query6 = getEntityManager().createQuery("update SupplierSettings s set s.modifiedBy = null  where s.modifiedBy.id = :id ").setParameter("id", userId);
		query6.executeUpdate();

		Query query7 = getEntityManager().createQuery("update UserRole s set s.createdBy = null  where s.createdBy.id = :id ").setParameter("id", userId);
		query7.executeUpdate();

		Query query8 = getEntityManager().createQuery("update UserRole s set s.modifiedBy = null  where s.modifiedBy.id = :id ").setParameter("id", userId);
		query8.executeUpdate();

		Query query10 = getEntityManager().createQuery("update User s set s.createdBy = null  where s.createdBy.id = :id ").setParameter("id", userId);
		query10.executeUpdate();

		Query query11 = getEntityManager().createQuery("update User s set s.modifiedBy = null  where s.modifiedBy.id = :id ").setParameter("id", userId);
		query11.executeUpdate();

		Query query9 = getEntityManager().createQuery("delete from FavouriteSupplierStatusAudit p  where p.actionBy.id = :id ").setParameter("id", userId);
		query9.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardAudit(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventAwardAudit p  where p.buyer.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

		Query query1 = getEntityManager().createQuery("delete from RfaEventAudit p  where p.buyer.id = :id ");
		query1.setParameter("id", tenantId);
		query1.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAward(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventAward p  where p.rfxEvent.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqEvaluationComments(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaBqEvaluationComments p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfaEventBq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetail(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventAwardDetails p  where p.bqItem.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventBq p  where p.rfxEvent.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierComments(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RfaSupplierComment p  where p.rfaSupplierBqItem.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqSupplierItem(String bqId, boolean isParent) {

		String hsql = "delete from RfaSupplierBqItem p  where p.id = :id ";
		if (isParent) {
			hsql += " and p.parent is null";
		} else {
			hsql += "and p.parent is not null";
		}
		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierBqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfaSupplierBq p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierBq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaSupplierBq p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventContact(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventContact p  where p.rfaEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfaCq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvalutionCqComts(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaCqEvaluationComments p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqItemIdList(String cqId) {
		StringBuilder hsql = new StringBuilder("select id from RfaCqItem p  where p.cq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deletCqOption(String cqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RfaCqOption p  where p.rfaCqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deletSuppCqOption(String cqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RfaSupplierCqOption p  where p.cqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from  RfaCq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierCqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfaSupplierCqItem p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierCqItemIdList(String cqId) {
		StringBuilder hsql = new StringBuilder("select id from RfaSupplierCqItem p  where p.cq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCqItem(String eventId) {

		StringBuilder hsql = new StringBuilder("delete from   RfaCqItem p  where p.rfxEvent.id = :id and p.parent is not null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCqParentItem(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaCqItem p  where p.rfxEvent.id = :id and p.parent is null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqItem(String cqId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaSupplierCqItem p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqParentItem(String cqId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaSupplierCqItem p  where p.cq.id = :id and p.parent is null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqItem(String bqId, boolean isParent) {
		String hsql = "delete from   RfaBqItem p  where p.id = :id ";
		if (isParent) {
			hsql += "and p.parent is null";
		} else {
			hsql += "and p.parent is not null";
		}

		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMeetingIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RfaEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingContact(String mid) {
		StringBuilder hsql = new StringBuilder("delete from   RfaEventMeetingContact p  where p.rfaEventMeeting.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", mid);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingDoc(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaEventMeetingDocument p  where p.tenantId = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingReminder(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaEventMeetingReminder p  where p.rfaEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierMeetingAtt(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaSupplierMeetingAttendance p  where p.rfaEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierTeam(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaSupplierTeamMember p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventSupplier(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaEventSupplier p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteTimeLine(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaEventTimeLine p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventReminder(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RfaReminder p  where p.rfaEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqItemList(String bqId) {
		StringBuilder hsql = new StringBuilder("select id from RfaBqItem p  where p.bq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSppBqItemList(String bqId) {

		StringBuilder hsql = new StringBuilder("select id from RfaSupplierBqItem p  where p.supplierBq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqItems(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventBq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteeventBqItems(String eventId, boolean isParent) {

		String hsql = "delete from RfaBqItem p  where p.rfxEvent.id = :id ";
		if (isParent) {
			hsql += " and p.parent is null";
		} else {
			hsql += "and p.parent is not null";
		}
		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierBqItems(String eventId, boolean isParent) {
		String hsql = "delete from RfaSupplierBqItem p  where p.event.id = :id ";
		if (isParent) {
			hsql += " and p.parent is null";
		} else {
			hsql += "and p.parent is not null";
		}
		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqOption(String cqId) {
		String hsql = "delete from RfaSupplierCqOption p  where p.cqItem.id = :id ";

		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetails(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventAward p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAwardByBq(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventAward p  where p.bq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetailbyItem(String bqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventAwardDetails p  where p.bqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvent(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEvent p  where p.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeeting(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMettingContact(String meetingId) {
		StringBuilder hsql = new StringBuilder("delete from RfaEventMeetingContact p  where p.rfaEventMeeting.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventMeettingIds(String eventId) {

		StringBuilder hsql = new StringBuilder("select id from RfaEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventMessage(String eventId, boolean isParent) {

		String hsql = "delete from RfaEventMessage p  where p.event.id = :id ";
		if (isParent) {
			hsql += " and p.parent is null";
		} else {
			hsql += "and p.parent is not null";
		}
		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBuyerTeam(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RfaTeamMember p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventTemplateFieldByTanent(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from TemplateField p  where p.buyer.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventTemplateIdList(String tenantId) {
		StringBuilder hsql = new StringBuilder("select id from RfxTemplate p  where p.buyer.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventTemplateApprovalIdList(String templateId) {
		StringBuilder hsql = new StringBuilder("select id from TemplateEventApproval p  where p.rfxTemplate.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", templateId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventTemplateApprovalUserByAproval(String apid) {
		StringBuilder hsql = new StringBuilder("delete from TemplateApprovalUser p  where p.approval.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", apid);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventTemplateApprovalById(String templateId) {
		StringBuilder hsql = new StringBuilder("delete from TemplateEventApproval p  where p.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", templateId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventTemplateByTanent(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from RfxTemplate p  where p.buyer.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventTemplateByID(String prTemplateId) {
		StringBuilder hsql = new StringBuilder("delete from RfxTemplate p  where p.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", prTemplateId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteRfxrecord(String id) {
		StringBuilder hsql = new StringBuilder("delete from PROC_USER_TEMPLATE_MAPPING  where TEMPLATE_ID = '" + id + "'");
		Query query = getEntityManager().createNativeQuery(hsql.toString());
		query.executeUpdate();

		StringBuilder hsql1 = new StringBuilder("delete from PROC_TEMPLATE_UNMASK_USER  where RFX_TEMPLATE_ID = '" + id + "'");
		Query query1 = getEntityManager().createNativeQuery(hsql1.toString());
		query1.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteTeamMemberByTemplateId(String rfxTemplateId) {
		StringBuilder hsql = new StringBuilder("delete from TemplateEventTeamMembers tpt where tpt.rfxTemplate.id= :rfxTemplateId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("rfxTemplateId", rfxTemplateId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvent findRfaEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId) {
		final Query query = getEntityManager().createQuery("from RfaEvent e where e.erpAwardRefId like :erpAwardRefId and e.tenantId = :tenantId");
		query.setParameter("erpAwardRefId", "%" + erpAwardRefId + "%");
		query.setParameter("tenantId", tenantId);
		List<RfaEvent> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> getEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		final Query query = searchFilterEventReport(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	private Query searchFilterEventReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		String hql = "select distinct e from RfaEvent e join fetch e.suppliers as sup join e.eventOwner as user join e.businessUnit as unit where e.tenantId = :tenantId ";

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				hql += " and e.id in (:eventIds)";
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();

		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				hql += " and upper(e.eventName) like :nameOfEvent";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(e.referanceNumber) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				hql += " and upper(e.eventId) like :eventId";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				hql += " and upper(user.name) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(unit.unitName) like :businessUnit";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				hql += " and upper(e.status) in (:status)";
				String[] types = searchFilterEventPojo.getStatus().split(",");
				if (types != null && types.length > 0) {
					for (String ty : types) {
						list.add(EventStatus.valueOf(ty));
					}
				}
			}

		}

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("eventName")) {
					hql += " and upper(e.eventName" + ") like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("referenceNumber")) {
					hql += " and upper(e.referanceNumber" + ")like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("sysEventId")) {
					hql += " and upper(e.eventId" + ") like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("eventUser")) {
					hql += " and upper(user.name" + ") like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("unitName")) {
					hql += " and upper(unit.unitName" + ")like (:" + cp.getData() + ")";
				}
				if (cp.getData().equals("status")) {
					hql += " and (e.status" + ")like (:" + cp.getData() + ")";
				}
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and e.createdDate between :startDate and :endDate ";
		}

		// If it is not a count query then add order by clause
		if (!select_all) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					LOG.info("orderColumn" + orderColumn);
					String dir = order.getDir();

					if (orderColumn.equalsIgnoreCase("eventName")) {
						hql += " order by e.eventName " + dir + ",";
					} else {
						if (orderColumn.equals("referenceNumber")) {
							hql += " order by e.referanceNumber " + dir + ",";
						} else if (orderColumn.equals("sysEventId")) {
							hql += " order by e.eventId " + dir + ",";
						} else if (orderColumn.equals("eventStart")) {
							hql += " order by e.eventStart " + dir + ",";
						} else if (orderColumn.equals("eventEnd")) {
							hql += " order by e.eventEnd " + dir + ",";
						} else if (orderColumn.equals("status")) {
							hql += " order by e.status " + dir + ",";
						} else if (orderColumn.equals("eventUser")) {
							hql += " order by e.eventOwner " + dir + ",";
						} else if (orderColumn.equals("unitName")) {
							hql += " order by e.businessUnit " + dir + ",";
						}
					}
				}

				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				query.setParameter("eventIds", Arrays.asList(eventIds));
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				query.setParameter("nameOfEvent", "%" + searchFilterEventPojo.getNameofevent().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + searchFilterEventPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				query.setParameter("eventId", "%" + searchFilterEventPojo.getEventid().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				query.setParameter("eventOwner", "%" + searchFilterEventPojo.getEventowner().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessUnit", "%" + searchFilterEventPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				query.setParameter("status", list);
			}

		}

		// Apply search filter values
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("eventName")) {
					query.setParameter("eventName", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("referenceNumber")) {
					query.setParameter("referenceNumber", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("sysEventId")) {
					query.setParameter("sysEventId", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("eventUser")) {
					query.setParameter("eventUser", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("unitName")) {
					query.setParameter("unitName", "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
				if (cp.getData().equals("status")) {
					query.setParameter("status", EventStatus.valueOf(cp.getSearch().getValue()));
				}
			}
		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BigDecimal> getEventsGrandTotalByIds(String tenantId, String[] eventIds) {
		String hql = "";
		if (eventIds != null && eventIds.length > 0) {
			hql = "select distinct e from RfaEvent e join fetch e.suppliers where e.tenantId = :tenantId and e.id in (:eventIds) and status in (:status)";
		} else {
			hql = "select distinct e from RfaEvent e join fetch e.suppliers where e.tenantId = :tenantId and status in (:status)";
		}

		final Query query = getEntityManager().createQuery(hql);
		if (eventIds != null && eventIds.length > 0) {
			query.setParameter("eventIds", Arrays.asList(eventIds));
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.APPROVED.toString(), EventStatus.CANCELED.toString(), EventStatus.REJECTED.toString(), EventStatus.SUSPENDED.toString(), EventStatus.COMPLETE.toString(), EventStatus.FINISHED.toString(), EventStatus.PENDING.toString(), EventStatus.CLOSED.toString(), EventStatus.ACTIVE.toString()));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategoryPojo> getTopFiveCategory(String tanentId) {
		String sql = getTopEventCategoryNativeQuery();
		final Query query = getEntityManager().createNativeQuery(sql, "industryCat");
		query.setParameter("tenantId", tanentId);
		return query.getResultList();

	}

	@Override
	public List<RfaEvent> getSearchEventByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date endDate, Date startDate) {
		return searchFilterEventReportList(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate);
	}

	@SuppressWarnings("unchecked")
	private List<RfaEvent> searchFilterEventReportList(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {
		String hql = "select distinct e from RfaEvent e join fetch e.suppliers as sup join e.eventOwner as user join e.businessUnit as unit where e.tenantId = :tenantId";

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				hql += " and e.id in (:eventIds)";
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();

		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				hql += " and upper(e.eventName) like :nameOfEvent";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				hql += " and upper(e.referanceNumber) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				hql += " and upper(e.eventId) like :eventId";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				hql += " and upper(user.name) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				hql += " and upper(unit.unitName) like :businessUnit";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				hql += " and upper(e.status) in (:status)";
				String[] types = searchFilterEventPojo.getStatus().split(",");
				if (types != null && types.length > 0) {
					for (String ty : types) {
						list.add(EventStatus.valueOf(ty));
					}
				}
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and e.createdDate between :startDate and :endDate ";
		}

		final Query query = getEntityManager().createQuery(hql);

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				query.setParameter("eventIds", Arrays.asList(eventIds));
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				query.setParameter("nameOfEvent", "%" + searchFilterEventPojo.getNameofevent().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + searchFilterEventPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				query.setParameter("eventId", "%" + searchFilterEventPojo.getEventid().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				query.setParameter("eventOwner", "%" + searchFilterEventPojo.getEventowner().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessUnit", "%" + searchFilterEventPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				query.setParameter("status", list);
			}

		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public EventTimerPojo getTimeRfaEventByeventId(String eventId) {
		final Query query = getEntityManager().createQuery("select NEW com.privasia.procurehere.core.pojo.EventTimerPojo(r.id, r.eventStart,r.eventEnd,r.auctionResumeDateTime,r.status) from RfaEvent r where r.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTimerPojo> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaEvent> getAllRfaEventByTenantId(String tenantId) {

		String hql = "from RfaEvent re where re.tenantId=:tenantId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("tenantId", tenantId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Event getEventByEventRefranceNo(String eventRefranceNo, String tenantId) {
		final Query query = getEntityManager().createQuery("from RfaEvent r where r.eventId =:eventRefranceNo and r.tenantId =:tenantId and r.status in (:status)");
		query.setParameter("eventRefranceNo", eventRefranceNo);
		query.setParameter("tenantId", tenantId);
		List<EventStatus> status = new ArrayList<EventStatus>();
		status.add(EventStatus.APPROVED);
		status.add(EventStatus.ACTIVE);
		query.setParameter("status", status);
		List<RfaEvent> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public User getUnMaskedUserNameAndMailByEventId(String eventId) {
		String hql = "select new User(u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications,  u.tenantId)from RfaEvent e left outer join e.unMaskedUser u where e.id=:id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id, r.eventId, ro.name, rob.line1, rob.line2, rob.city, robs.stateName, robc.countryName, ro.communicationEmail,ro.phoneNumber,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate, r.submissionValidityDays, r.participationFees, rpc.currencyCode, r.tenantId, r.status, r.eventDescription, rbc.currencyCode, r.decimal,  r.paymentTerm,r.auctionType,r.documentReq,r.meetingReq, r.questionnaires,r.billOfQuantity, r.scheduleOfRate,r.auctionResumeDateTime,r.erpEnable,rob.companyName,r.enableSupplierDeclaration, r.disableTotalAmount, r.suspensionType) from RfaEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join rob.state as robs left outer join robs.country as robc left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as rpc where r.id =:eventId");
			query.setParameter("eventId", eventId);
			@SuppressWarnings("unchecked")
			List<EventPojo> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public EventPojo loadSupplierEventPojoForSummeryById(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id, r.eventId, ro.name, rob.line1, rob.line2, rob.city, robs.stateName, robc.countryName, ro.communicationEmail,ro.phoneNumber,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate, r.submissionValidityDays, r.participationFees, rpc.currencyCode, r.tenantId, r.status, r.eventDescription, rbc.currencyName, r.decimal,  r.paymentTerm,r.auctionType,r.documentReq,r.meetingReq, r.questionnaires,r.billOfQuantity, r.scheduleOfRate,r.auctionResumeDateTime,r.erpEnable,rob.companyName,da,rbc.currencyCode,bu.displayName, das.stateName, dac.countryName ,r.auctionDurationType, r.auctionStartDelayType, r.auctionDuration, r.auctionStartDelay,r.auctionStartRelative, r.timeExtensionType, r.timeExtensionDurationType, r.timeExtensionDuration, r.timeExtensionLeadingBidType, r.timeExtensionLeadingBidValue,r.extensionCount,r.bidderDisqualify, r.autoDisqualify,r.deposit,rdc.currencyCode) from RfaEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join rob.state as robs left outer join robs.country as robc left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as rpc left outer join r.depositCurrency as rdc left outer join r.deliveryAddress da left outer join r.businessUnit bu left outer join da.state das left outer join das.country dac where r.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventPojo> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IndustryCategory> getIndustryCategoryListForRfaById(String eventId) {
		StringBuffer sb = new StringBuffer("select distinct NEW com.privasia.procurehere.core.entity.IndustryCategory(ind.id, ind.code, ind.name) from RfaEvent re left outer join re.industryCategories ind where re.id =:eventId ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStatus(EventStatus status, String eventId) {
		StringBuffer sb = new StringBuffer("update RfaEvent e set e.status=:status where e.id=:eventId");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", status);
		query.executeUpdate();
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		String hql = "select count(*) from RfaEventSupplier sup  where  sup.rfxEvent.id=:eventId";

		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventId", eventId);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public long getParticipatedSupplierCount(String eventId) {
		String hql = "select count(*) from RfaEventSupplier sup  where  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("submissionStatusType", SubmissionStatusType.ACCEPTED);
		query.setParameter("eventId", eventId);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public long getSubmitedSupplierCount(String eventId) {
		String hql = "select count(*) from RfaEventSupplier sup  where  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("submissionStatusType", SubmissionStatusType.COMPLETED);
		query.setParameter("eventId", eventId);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBq> getLowestSubmissionPrice(String eventId, Boolean prebidFlag) {
		String hql = "select bq from RfaSupplierBq bq left outer join fetch bq.supplier s, RfaEventSupplier es where bq.event.id = :eventId and es.rfxEvent.id = bq.event.id ";
		if (prebidFlag == Boolean.TRUE) {
			hql += "  and es.supplier.id = s.id and bq.totalAfterTax=(select MIN(bq.totalAfterTax) FROM bq where bq.event.id = :eventId and bq.supplier.id in(select es.supplier.id from RfaEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = :disqualify and es.submissionStatus= :submissionStatusType)) order by es.revisedBidDateAndTime asc ";
		} else {
			hql += "  and es.supplier.id = s.id and bq.totalAfterTax=(select MIN(bq.totalAfterTax) FROM bq where bq.event.id = :eventId and bq.supplier.id in(select es.supplier.id from RfaEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = :disqualify and es.submissionStatus= :submissionStatusType and es.numberOfBids > 0)) order by es.revisedBidDateAndTime asc ";
		}
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("submissionStatusType", SubmissionStatusType.COMPLETED);
		query.setParameter("eventId", eventId);
		query.setParameter("disqualify", Boolean.FALSE);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierBq> getHighestSubmissionPrice(String eventId, Boolean prebidFlag) {
		String hql = "select bq from RfaSupplierBq bq left outer join fetch bq.supplier s, RfaEventSupplier es where bq.event.id = :eventId and es.rfxEvent.id = bq.event.id ";
		if (prebidFlag == Boolean.TRUE) {
			hql += " and es.supplier.id = s.id and bq.totalAfterTax=(select MAX(bq.totalAfterTax) FROM bq where bq.event.id = :eventId and bq.supplier.id in(select es.supplier.id from RfaEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = :disqualify and es.submissionStatus= :submissionStatusType)) order by es.revisedBidDateAndTime desc ";
		} else {
			hql += " and es.supplier.id = s.id and bq.totalAfterTax=(select MAX(bq.totalAfterTax) FROM bq where bq.event.id = :eventId and bq.supplier.id in(select es.supplier.id from RfaEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = :disqualify and es.submissionStatus= :submissionStatusType and es.numberOfBids > 0)) order by es.revisedBidDateAndTime desc ";
		}
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("submissionStatusType", SubmissionStatusType.COMPLETED);
		query.setParameter("eventId", eventId);
		query.setParameter("disqualify", Boolean.FALSE);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	public RfaEvent getRfaEventForShortSummary(String eventId) {
		String hql = "select e from RfaEvent e left outer join fetch e.baseCurrency  where e.id=:id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		try {
			return (RfaEvent) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventSupplier> getSuppliersByStatus(String eventId) {
		final Query query = getEntityManager().createQuery("select sup from RfaEventSupplier sup,RfaSupplierBq bq where bq.supplier.id = sup.supplier.id and  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType and sup.disqualify =:disqualify and bq.event.id=:eventId order by bq.totalAfterTax");
		query.setParameter("submissionStatusType", SubmissionStatusType.COMPLETED);
		query.setParameter("eventId", eventId);
		query.setParameter("disqualify", false);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public RfaSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId) {
		final Query query = getEntityManager().createQuery("select bq from RfaSupplierBq bq where bq.supplier.id=:id and bq.event.id=:eventId");
		query.setParameter("id", supplierId);
		query.setParameter("eventId", eventId);
		try {
			return (RfaSupplierBq) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public long getNumberOfBidForRfa(String eventId) {
		String hql = "select sum(es.numberOfBids) from RfaEventSupplier es where es.rfxEvent.id = :eventId";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventId", eventId);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@Override
	public RfaEnvelop getBqForEnvelope(String envelopeId) {
		final Query query = getEntityManager().createQuery("select re from RfaEnvelop re left outer join fetch re.bqList bq where re.id =:envelopeId ");
		query.setParameter("envelopeId", envelopeId);
		try {
			return (RfaEnvelop) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	/*
	 * @Override public String getSuppliersByRevisedBidTime(String supplierId) { String hql =
	 * "select esup FROM RfaEventSupplier esup where esup.supplier.id =:supplierId and esup.revisedBidDateAndTime =:(select min(esup.revisedBidDateAndTime) from esup )"
	 * ; Query query = getEntityManager().createQuery(hql); query.setParameter("supplierId", supplierId); try { return
	 * (String) query.getSingleResult(); } catch (Exception e) { LOG.error(e.getMessage()); return null; } }
	 */

	@SuppressWarnings("unchecked")
	@Override
	public EventPojo getRfaForPublicEventByeventId(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id,r.eventId,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate,r.deliveryDate, r.tenantId, r.status, r.paymentTerm, rbc.currencyName,rbc.currencyCode,rob.companyName,r.auctionType,r.timeExtensionType) from RfaEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join r.baseCurrency as rbc where r.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventPojo> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getIndustryCategoriesForRfaById(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ic from RfaEvent as r  join r.industryCategories as ic  where r.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	public boolean isMinMaxSupplierRatingAvaliableInEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RfaEvent as r where r.id = :eventId and (r.minimumSupplierRating is not null or r.maximumSupplierRating is not null)");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Supplier findWinnerSupplier(String id) {
		Query query = getEntityManager().createNativeQuery("SELECT DISTINCT sb.SUPPLIER_ID AS id, sum(TOTAL_AFTER_TAX) AS totalAfterTax, es.SUPPLIER_BID_SUBMITTED_TIME AS createdDate,es.AUCTION_RANKING FROM PROC_RFA_SUPPLIER_BQ sb JOIN PROC_RFA_EVENT_SUPPLIERS es ON sb.SUPPLIER_ID = es.SUPPLIER_ID WHERE sb.EVENT_ID =:eventId	AND es.IS_DISQUALIFY = 0 AND es.SUBMISSION_STATUS = 'COMPLETED'	AND es.EVENT_ID = sb.EVENT_ID GROUP BY sb.SUPPLIER_ID, es.SUPPLIER_BID_SUBMITTED_TIME,es.AUCTION_RANKING ORDER BY es.AUCTION_RANKING", "winnerSupplierResult");
		query.setParameter("eventId", id);
		List<Supplier> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public int updateEventPushedDate(String eventId) {
		String hql = "update RfaEvent re set re.eventPushDate =:eventPushDate where re.id=:eventId and re.eventPushDate is null";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventPushDate", new Date());
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public Double getAvarageBidPriceSubmitted(String id) {
		String hql = "select AVG(bq.grandTotal) from RfaSupplierBq bq where bq.event.id = :id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		try {
			return (Double) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventTeamMember(String eventId) {
		String sql = "SELECT u.USER_NAME  FROM PROC_USER u RIGHT OUTER JOIN PROC_RFA_TEAM  team ON  team.USER_ID = u.ID WHERE team.MEMBER_TYPE =:memberType AND team.EVENT_ID =:eventId";
		Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("eventId", eventId);
		query.setParameter("memberType", TeamMemberType.Associate_Owner);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public int updatePrPushDate(String eventId) {
		String hql = "update RfaEvent re set re.pushToPr =:pushToPr where re.id=:eventId and re.pushToPr is null";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("pushToPr", new Date());
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public int updateEventAward(String eventId) {
		String hql = "update RfaEvent re set re.awardDate =:awardDate where re.id=:eventId and re.awardDate is null";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("awardDate", new Date());
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateImmediately(String eventId, EventStatus status, Date auctionComplitationTime) {
		String hql = "";
		if (auctionComplitationTime == null) {
			hql = "update RfaEvent re set re.status = :status where re.id = :eventId ";
		} else {
			hql = "update RfaEvent re set re.status = :status, re.auctionComplitationTime = :auctionComplitationTime where re.id = :eventId ";
		}
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", status);
		if (auctionComplitationTime != null) {
			query.setParameter("auctionComplitationTime", auctionComplitationTime);
		}
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> getAllRfaEventByTenantIdInitial(String loggedInUserTenantId, String loggedInUser) {
		LOG.info("Tenant Id : " + loggedInUserTenantId + " : user Id :  " + loggedInUser);
		StringBuffer sb = new StringBuffer("from RfaEvent re left outer join fetch re.industryCategory where re.tenantId =:tenantId ");
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			sb.append(" and re.eventOwner.id = :loggedInUser");
		}
		sb.append("  order by re.createdDate desc ");

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", loggedInUserTenantId);
		query.setFirstResult(0);
		query.setMaxResults(10);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("loggedInUser", loggedInUser);
		}
		return query.getResultList();
	}

	@Override
	public Integer getCountByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(e) from RfaEvent e where e.id =:id");
		query.setParameter("id", eventId);
		try {
			Integer count = ((Number) query.getSingleResult()).intValue();
			return count;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> getAllActiveEvents() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfaEvent(v.id, v.eventPublishDate, v.eventName, v.eventEnd, v.referanceNumber, v.auctionComplitationTime, v.billOfQuantity, v.questionnaires) from RfaEvent v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.ACTIVE);
		List<RfaEvent> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RfaEvent> getAllApprovedEventsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RfaEvent(v.id, v.eventPublishDate, v.eventName, v.eventEnd, v.referanceNumber, v.auctionComplitationTime,v.billOfQuantity, v.questionnaires) from RfaEvent v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.APPROVED);
		List<RfaEvent> list = query.getResultList();
		return list;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateRfaForAwardPrice(String eventId, BigDecimal sumOfAwardedPriceForEvent) {
		try {
			RfaEvent rfaEvent = findById(eventId);
			rfaEvent.setAwardedPrice(sumOfAwardedPriceForEvent);
			saveOrUpdate(rfaEvent);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public Event getSimpleEventDetailsById(String eventId) {
		StringBuilder hsql = new StringBuilder("select NEW com.privasia.procurehere.core.entity.Event(a.id, a.eventId, a.eventPublishDate, a.eventName, a.eventEnd, a.eventStart, a.status, a.referanceNumber, cb.id, cb.name,cb.communicationEmail, cb.emailNotifications ) from RfaEvent a  left outer join a.createdBy cb where a.id =:eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (Event) query.getSingleResult();
	}

	@Override
	public User getRevertLastBidUserNameAndMailByEventId(String eventId) {
		String hql = "select new User(u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId)from RfaEvent e left outer join e.revertBidUser u where e.id=:id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r where r.eventId = :eventId and r.tenantId = :tenantId");
			query.setParameter("eventId", eventId);
			query.setParameter("tenantId", tenantId);
			List<RfaEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (Exception nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventAwardAudit> getRfaEventAwardByEventId(String eventId) {
		String hql = " from RfaEventAwardAudit award where award.event.id=:eventId order by award.actionDate  desc";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventId", eventId);
		query.setFirstResult(0);
		query.setMaxResults(1);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public Boolean isDefaultPreSetEnvlope(String eventId) {
		final Query query = getEntityManager().createQuery("select t.rfxEnvelopeReadOnly from RfaEvent  event left outer join event.template t  where event.id=:eventId ");
		query.setParameter("eventId", eventId);
		return (Boolean) query.getSingleResult();
	}

	@Override
	public RfaSupplierBq getSupplierBQOfHighestTotalPrice(String eventId, String bqId) {
		final Query rfaSupplierBqQuery = getEntityManager().createQuery("select distinct sb from RfaSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.grandTotal = (select max(r.grandTotal) from RfaSupplierBq r where r.event.id =:eventId and r.bq.id =:bqId and r.supplier.id in (select es.supplier.id from RfaEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = false and es.submissionStatus=:submissionStatus )) and sb.event.id =:eventId and sb.bq.id =:bqId and s.id in (select ess.supplier.id from RfaEventSupplier ess where ess.rfxEvent.id = :eventId and ess.disqualify = false  and ess.submissionStatus=:submissionStatus )");
		rfaSupplierBqQuery.setParameter("eventId", eventId);
		rfaSupplierBqQuery.setParameter("bqId", bqId);
		rfaSupplierBqQuery.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RfaSupplierBq> rfaSupplierBqList = rfaSupplierBqQuery.getResultList();
		if (CollectionUtil.isNotEmpty(rfaSupplierBqList)) {
			// if more suppliers have same lowest price then sending the first submit supplier.
			if (rfaSupplierBqList.size() > 0) {
				List<String> suppIds = new ArrayList<>();
				for (RfaSupplierBq rfaSupplierBq : rfaSupplierBqList) {
					LOG.info("suuplier name: " + rfaSupplierBq.getSupplier().getCompanyName() + "======= rfaSupplierBq:  " + rfaSupplierBq.getGrandTotal());
					suppIds.add(rfaSupplierBq.getSupplier().getId());
				}
				Query bidHistoryQuery = getEntityManager().createQuery("select distinct ab from AuctionBids ab left outer join fetch ab.bidBySupplier sup  where sup.id in (:suppIds) and ab.event.id = :eventId and ab.bidSubmissionDate in (select max(abs.bidSubmissionDate) from AuctionBids abs where abs.bidBySupplier.id in (:suppIds) and abs.event.id = :eventId  group by abs.bidBySupplier.id) ");
				bidHistoryQuery.setParameter("eventId", eventId);
				bidHistoryQuery.setParameter("suppIds", suppIds);
				List<AuctionBids> auctionBidsList = bidHistoryQuery.getResultList();
				if (CollectionUtil.isNotEmpty(auctionBidsList)) {
					Collections.sort(auctionBidsList, new Comparator<AuctionBids>() {
						public int compare(AuctionBids o1, AuctionBids o2) {
							return o1.getBidSubmissionDate().compareTo(o2.getBidSubmissionDate());
						}
					});

					if (auctionBidsList.get(0) != null) {
						for (RfaSupplierBq rfaSupplierBq : rfaSupplierBqList) {
							if (rfaSupplierBq.getSupplier().getId().equals(auctionBidsList.get(0).getBidBySupplier().getId())) {
								return rfaSupplierBq;
							}
						}
					}

				} else {

					bidHistoryQuery = getEntityManager().createQuery("select distinct ab from RfaEventSupplier ab left outer join fetch ab.supplier sup  where sup.id in (:suppIds) and ab.rfxEvent.id = :eventId and ab.supplierSubmittedTime in (select max(abs.supplierSubmittedTime) from RfaEventSupplier abs where abs.supplier.id in (:suppIds) and abs.rfxEvent.id = :eventId  group by abs.supplier.id) ");
					bidHistoryQuery.setParameter("eventId", eventId);
					bidHistoryQuery.setParameter("suppIds", suppIds);
					List<RfaEventSupplier> supplierEventList = bidHistoryQuery.getResultList();

					/*
					 * for (RfaEventSupplier rftEventSupplier : supplierEventList) { LOG.info("-------------------" +
					 * rftEventSupplier.getSupplierSubmittedTime() + "------------" +
					 * rftEventSupplier.getSupplier().getCompanyName()); }
					 */

					if (CollectionUtil.isNotEmpty(supplierEventList)) {
						Collections.sort(supplierEventList, new Comparator<RfaEventSupplier>() {

							public int compare(RfaEventSupplier o1, RfaEventSupplier o2) {
								return o1.getSupplierSubmittedTime().compareTo(o2.getSupplierSubmittedTime());
							}
						});

						if (supplierEventList.get(0) != null) {
							for (

							RfaSupplierBq rfaSupplierBq : rfaSupplierBqList) {
								if (rfaSupplierBq.getSupplier().getId().equals(supplierEventList.get(0).getSupplier().getId())) {
									return rfaSupplierBq;
								}
							}
						}

					}

				}
			}
			return rfaSupplierBqList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<RfaSupplierBq> getSupplierBQOfHighestItemisedPrize(String eventId, String bqId) {
		final Query query = getEntityManager().createQuery("select distinct sb from RfaSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.event.id =:eventId and sb.bq.id =:bqId ");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		List<RfaSupplierBq> uList = query.getResultList();
		return uList;
	}

	@Override
	public RfaSupplierBqItem getMaxItemisePrice(String id, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct sb from RfaSupplierBqItem sb left outer join fetch sb.supplier s left outer join fetch sb.event e  left outer join fetch sb.bqItem bi where sb.totalAmount = (select max(r.totalAmount) from RfaSupplierBqItem r where r.bqItem.id = :bqItemId  and r.supplier.id in (select es.supplier.id from RfaEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = false)) and bi.id = :bqItemId and s.id in (select ess.supplier.id from RfaEventSupplier ess where ess.rfxEvent.id = :eventId and ess.disqualify = false )");
		query.setParameter("bqItemId", id);
		query.setParameter("eventId", eventId);
		List<RfaSupplierBqItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {

			if (uList.size() > 0) {
				List<String> suppIds = new ArrayList<>();
				for (RfaSupplierBqItem rfaSupplierBq : uList) {
					LOG.info("suuplier name: " + rfaSupplierBq.getSupplier().getCompanyName() + "======= rfaSupplierBq:  " + rfaSupplierBq.getUnitPrice());
					suppIds.add(rfaSupplierBq.getSupplier().getId());
				}
				Query bidHistoryQuery = getEntityManager().createQuery("select distinct ab from AuctionBids ab left outer join fetch ab.bidBySupplier sup  where sup.id in (:suppIds) and ab.event.id = :eventId and ab.bidSubmissionDate in (select max(abs.bidSubmissionDate) from AuctionBids abs where abs.bidBySupplier.id in (:suppIds) and abs.event.id = :eventId  group by abs.bidBySupplier.id) ");
				bidHistoryQuery.setParameter("eventId", eventId);
				bidHistoryQuery.setParameter("suppIds", suppIds);
				List<AuctionBids> auctionBidsList = bidHistoryQuery.getResultList();

				if (CollectionUtil.isNotEmpty(auctionBidsList)) {
					/*
					 * for (AuctionBids auctionBids : auctionBidsList) { LOG.info("suuplier name: " +
					 * auctionBids.getBidBySupplier().getCompanyName() + "<---------->" +
					 * auctionBids.getBidSubmissionDate()); }
					 */
					Collections.sort(auctionBidsList, new Comparator<AuctionBids>() {
						public int compare(AuctionBids o1, AuctionBids o2) {
							return o1.getBidSubmissionDate().compareTo(o2.getBidSubmissionDate());
						}
					});

					if (auctionBidsList.get(0) != null) {
						for (RfaSupplierBqItem rfaSupplierBq : uList) {
							if (rfaSupplierBq.getSupplier().getId().equals(auctionBidsList.get(0).getBidBySupplier().getId())) {
								return rfaSupplierBq;
							}
						}
					}

					else {

						bidHistoryQuery = getEntityManager().createQuery("select distinct ab from RfaEventSupplier ab left outer join fetch ab.supplier sup  where sup.id in (:suppIds) and ab.rfxEvent.id = :eventId and ab.supplierSubmittedTime in (select max(abs.supplierSubmittedTime) from RfaEventSupplier abs where abs.supplier.id in (:suppIds) and abs.rfxEvent.id = :eventId  group by abs.supplier.id)  ");
						bidHistoryQuery.setParameter("eventId", eventId);
						bidHistoryQuery.setParameter("suppIds", suppIds);
						List<RfaEventSupplier> rfaAuctionBidsList = bidHistoryQuery.getResultList();
						if (CollectionUtil.isNotEmpty(rfaAuctionBidsList)) {
							/*
							 * for (RfaEventSupplier rftEventSupplier : rfaAuctionBidsList) {
							 * LOG.info("-------------------" + rftEventSupplier.getSupplierSubmittedTime() +
							 * "------------" + rftEventSupplier.getSupplier().getCompanyName()); }
							 */
							Collections.sort(rfaAuctionBidsList, new Comparator<RfaEventSupplier>() {
								public int compare(RfaEventSupplier o1, RfaEventSupplier o2) {
									return o1.getSupplierSubmittedTime().compareTo(o2.getSupplierSubmittedTime());
								}
							});

							if (rfaAuctionBidsList.get(0) != null) {
								for (RfaSupplierBqItem rftSupplierBq : uList) {
									if (rftSupplierBq.getSupplier().getId().equals(rfaAuctionBidsList.get(0).getSupplier().getId())) {
										return rftSupplierBq;
									}
								}
							}

						}
					}

				}
			}

			return uList.get(0);

		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllAuctionEventsForAuctionSummaryReport(String tenantId, TableDataInput input, Date startDate, Date endDate, List<AuctionType> auctionTypeList, String auctionTypeS) {
		final Query query = constructAllAuctionEventsForReport(input, tenantId, startDate, endDate, false, auctionTypeList, auctionTypeS);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long getAuctionEventsCountForBuyerEventReport(String tenantId, TableDataInput input, Date startDate, Date endDate, List<AuctionType> auctionTypeList, String auctionTypeS) {
		final Query query = constructAllAuctionEventsForReport(input, tenantId, startDate, endDate, true, auctionTypeList, auctionTypeS);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public long findTotalEventForBuyer(String tenantId, Object userId, List<AuctionType> auctionTypeList, String auctionTypeS) {
		// List<EventStatus> eventStatus = Arrays.asList(EventStatus.FINISHED);
		String hql = "select count(re.id) from RfaEvent re  where re.tenantId = :tenantId and re.status=:eventStatus and re.auctionType in (:auctionTypeList)";
		Query query = getEntityManager().createQuery(hql);

		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStatus", EventStatus.FINISHED);
		query.setParameter("auctionTypeList", auctionTypeList);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	private Query constructAllAuctionEventsForReport(TableDataInput tableParams, String tenantId, Date startDate, Date endDate, boolean isCount, List<AuctionType> auctionTypeList, String auctionTypeS) {

		String hql = "";

		if (isCount) {
			hql += "select count(distinct e.id) ";
		} else {
			hql += "select distinct NEW com.privasia.procurehere.core.pojo.DraftEventPojo(e.id, e.eventName, e.referanceNumber, e.eventVisibility, e.eventStart, e.eventEnd, bu.unitName, t.templateName, e.eventId, o.name, e.createdDate) ";
		}

		hql += " from RfaEvent e left outer join e.template t left outer join e.businessUnit bu left outer join e.eventOwner o ";
		hql += " where e.tenantId = :tenantId and e.status in (:eventStatus) and e.auctionType in (:auctionTypeList)";
		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and e.createdDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
					if (cp.getData().equals("eventVisibility")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							hql += " and e.eventVisibility =:eventVisibilities ";
						} else {
							hql += " and e.eventVisibility in (:eventVisibilities) ";
						}
					} else {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							if (cp.getData().equalsIgnoreCase("referanceNumber")) {
								hql += " and upper(e.referanceNumber) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("sysEventId")) {
								hql += " and upper(e.eventId) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("eventUser")) {
								hql += " and upper(o.name) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("unitName")) {
								hql += " and upper(bu.unitName) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("templateName")) {
								hql += " and upper(t.templateName) like (:" + cp.getData().replace(".", "") + ") ";
							} else {
								hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
							}

						}

					}
				}
			}
		}

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					if (orderColumn.equalsIgnoreCase("referanceNumber")) {
						hql += " e.referanceNumber " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("sysEventId")) {
						hql += " e.eventId " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("eventUser")) {
						hql += " o.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("unitName")) {
						hql += " bu.unitName " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("templateName")) {
						hql += " t.templateName " + dir + ",";
					} else {
						hql += " e." + orderColumn + " " + dir + ",";
					}

				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				if (!isCount) {
					hql += " order by e.createdDate desc ";
				}
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// Apply search filter values
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

					if (cp.getData().equals("eventVisibility")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							query.setParameter("eventVisibilities", EventVisibilityType.fromStringToEventVisibilityType(cp.getSearch().getValue()));
						} else {
							List<EventVisibilityType> eventVisibilities = Arrays.asList(EventVisibilityType.PRIVATE, EventVisibilityType.PUBLIC, EventVisibilityType.PARTIAL);
							query.setParameter("eventVisibilities", eventVisibilities);
						}
					} else {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
						}
					}
				}
			}
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStatus", EventStatus.FINISHED);
		query.setParameter("auctionTypeList", auctionTypeList);

		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllAuctionEventwithSearchFilter(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, String auctionTypeS) {
		List<EventStatus> list = new ArrayList<EventStatus>();
		String sql = "";

		// sql += getSqlForAllAuctionEventForReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate,
		// endDate, list, RfxTypes.RFA);

		if (auctionTypeS.equals("FORWARD")) {
			sql += getSqlForAllForwardAuctionEventForReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFA);
		} else {
			sql += getSqlForAllReverseAuctionEventForReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFA);
		}

		sql += " ";

		// LOG.info("====>" + sql);

		final Query query = getEntityManager().createNativeQuery(sql, "auctionExcelReportData");

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				query.setParameter("eventIds", Arrays.asList(eventIds));
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				query.setParameter("nameOfEvent", "%" + searchFilterEventPojo.getNameofevent().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + searchFilterEventPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				query.setParameter("eventId", "%" + searchFilterEventPojo.getEventid().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				query.setParameter("eventOwner", "%" + searchFilterEventPojo.getEventowner().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessUnit", "%" + searchFilterEventPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				query.setParameter("status", "%" + searchFilterEventPojo.getBusinessunit().toUpperCase() + "%");
			}

		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		List<DraftEventPojo> eventList = query.getResultList();

		List<DraftEventPojo> finalList = new ArrayList<DraftEventPojo>();

		for (DraftEventPojo evt : eventList) {
			if (finalList.contains(evt)) {

				DraftEventPojo levt = finalList.get(finalList.indexOf(evt));
				if (StringUtils.checkString(levt.getAssoiciateOwner()).length() == 0) {
					if (StringUtils.checkString(evt.getMemberType()).equals(TeamMemberType.Associate_Owner.name())) {
						levt.setAssoiciateOwner(StringUtils.checkString(evt.getAssoiciateOwner()));
					}
				} else if (StringUtils.checkString(evt.getAssoiciateOwner()).length() > 0) {
					if (StringUtils.checkString(evt.getMemberType()).equals(TeamMemberType.Associate_Owner.name())) {
						levt.setAssoiciateOwner(StringUtils.checkString(levt.getAssoiciateOwner()) + "," + StringUtils.checkString(evt.getAssoiciateOwner()));
					}
				}
			} else {
				if (!StringUtils.checkString(evt.getMemberType()).equals(TeamMemberType.Associate_Owner.name())) {
					evt.setAssoiciateOwner("");
				}
				finalList.add(evt);
			}
		}

		return finalList;

	}

	private String getSqlForAllForwardAuctionEventForReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, List<EventStatus> list, RfxTypes eventType) {
		String sql = "";

		sql += " SELECT id, eventName, decimalValue, currencyName, referanceNumber, sysEventId, auctiontype, ownerName, eventStart, eventEnd, eventVisibility, unitName, templateName, leadingSuppierBid, leadingSuppier, awardedSupplier, awardedPrice, winningSupplier, selfInvitedWinner, leadingAmount, invitedSupplierCount, participatedSupplierCount, selfInvitedSupplierCount, submittedSupplierCount, eventCategories, budgetAmount,historicAmount, sumAwardedPrice, supplierTags, finalBid, noOfBids, total_invited_suppliers, round(((submittedSupplierCount / CASE WHEN total_invited_suppliers = 0 THEN 1 ELSE total_invited_suppliers END)  * 100), 2) AS ratio FROM (" //
				+ " SELECT e.ID AS id, " //
				+ "e.EVENT_NAME AS eventName, "//
				+ "e.BUYER_SET_DECIMAL AS decimalValue, " //
				+ "currency.CURRENCY_CODE as currencyName, " //
				+ "e.REFERANCE_NUMBER AS referanceNumber, " //
				+ "e.EVENT_ID AS sysEventId, " //
				+ "e.AUCTION_TYPE AS auctiontype, " //
				+ "u.USER_NAME AS ownerName, " //
				+ "e.EVENT_START AS eventStart, " //
				+ "e.EVENT_END AS eventEnd, " //
				+ "e.EVENT_VISIBILITY AS eventVisibility, " //
				+ "bu.BUSINESS_UNIT_NAME AS unitName, " //
				+ "t.TEMPLATE_NAME AS templateName, " //
				+ "(SELECT prsb.TOTAL_AFTER_TAX FROM  PROC_RFA_EVENT_SUPPLIERS rfas, PROC_RFA_SUPPLIER_BQ prsb  WHERE rfas.EVENT_ID=e.id "//
				+ " AND prsb.EVENT_ID = e.id AND rfas.IS_DISQUALIFY=0 AND prsb.SUPPLIER_ID = rfas.SUPPLIER_ID " //
				+ " AND rfas.AUCTION_RANKING IS NOT null ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS leadingSuppierBid ," //
				+ "(SELECT ps.COMPANY_NAME FROM  PROC_RFA_EVENT_SUPPLIERS rfas join PROC_SUPPLIER ps ON rfas.SUPPLIER_ID = ps.SUPPLIER_ID  WHERE rfas.EVENT_ID=e.id AND rfas.IS_DISQUALIFY=0 ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS leadingSuppier ,"; //
		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += "(SELECT string_agg(ps.COMPANY_NAME,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS awardedSupplier, " //
					+ "(SELECT string_agg(pread.AWARDED_PRICE,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS awardedPrice," //
					+ "(SELECT string_agg(ps.COMPANY_NAME,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS winningSupplier ," //
					+ "(SELECT string_agg(CASE WHEN es.IS_SELF_INVITED = 1 THEN 'YES' ELSE 'NO' END,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID  JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID JOIN PROC_RFA_EVENT_SUPPLIERS es ON es.SUPPLIER_ID=pread.SUPPLIER_ID AND es.EVENT_ID=prea.EVENT_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS selfInvitedWinner ,"; //
		} else {
			sql += "(SELECT STRING_AGG(ps.COMPANY_NAME,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS awardedSupplier, " //
					+ "(SELECT STRING_AGG(pread.AWARDED_PRICE,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS awardedPrice," //
					+ "(SELECT STRING_AGG(ps.COMPANY_NAME,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS winningSupplier ," //
					+ "(SELECT STRING_AGG(CASE WHEN es.IS_SELF_INVITED = 1 THEN 'YES' ELSE 'NO' END,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID  JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID JOIN PROC_RFA_EVENT_SUPPLIERS es ON es.SUPPLIER_ID=pread.SUPPLIER_ID AND es.EVENT_ID=prea.EVENT_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS selfInvitedWinner ,"; //
		}

		sql += "e.WINNING_PRICE  AS leadingAmount, "; //
		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += "CAST(SUM( CASE WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END) as varchar) AS invitedSupplierCount, " //
					+ "CAST(SUM( CASE WHEN (es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' )THEN 1 ELSE 0 END ) as varchar) AS participatedSupplierCount," //
					+ "CAST(SUM( CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0 END) as varchar) AS selfInvitedSupplierCount ," //
					+ "CAST(SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END ) as varchar) AS submittedSupplierCount ," //
					+ "(SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, "; //
		} else {
			sql += "CONVERT(varchar,SUM( CASE WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END)) AS invitedSupplierCount, " //
					+ "CONVERT(varchar,SUM( CASE WHEN (es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' )THEN 1 ELSE 0 END )) AS participatedSupplierCount," //
					+ "CONVERT(varchar,SUM( CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0 END)) AS selfInvitedSupplierCount ," //
					+ "CONVERT(varchar,SUM(CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) AS submittedSupplierCount ," //
					+ "(SELECT STRING_AGG(ic.CATEGORY_NAME,', ') WITHIN GROUP(ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, "; //
		}

		sql += "e.BUDGET_AMOUNT AS budgetAmount ," //
				+ "e.HISTORICAL_AMOUNT AS historicAmount ," //
				+ "(SELECT sum(pread.AWARDED_PRICE) FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID AND prea.EVENT_ID=e.ID WHERE pread.SUPPLIER_ID IS NOT NULL) AS sumAwardedPrice, "; //

		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += " (SELECT string_agg(st.SUPPLIER_TAGS,', ' ORDER BY st.SUPPLIER_TAGS) cst FROM PROC_RFA_AWARD_SUP ast, PROC_FAV_SUPPLIER_TAGS est JOIN PROC_FAVOURITE_SUPPLIER fs ON est.FAV_SUPP_ID = fs.FAV_SUPPLIER_ID JOIN PROC_SUPPLIER_TAGS st ON est.SUPP_TAG_ID= st.ID WHERE ast.SUPPLIER_ID = fs.SUPPLIER_ID AND ast.EVENT_ID = e.ID) AS supplierTags ," //
					+ "(SELECT string_agg(bq.TOTAL_AFTER_TAX,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID JOIN PROC_RFA_SUPPLIER_BQ bq ON bq.EVENT_ID = pas.EVENT_ID AND bq.SUPPLIER_ID = pas.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS finalBid ," //
					+ "CAST(SUM( es.NUMBER_OF_BIDS) as varchar) AS noOfBids ,"; //
		} else {
			sql += " (SELECT STRING_AGG(st.SUPPLIER_TAGS,', ') WITHIN GROUP(ORDER BY st.SUPPLIER_TAGS) cst FROM PROC_RFA_AWARD_SUP ast, PROC_FAV_SUPPLIER_TAGS est JOIN PROC_FAVOURITE_SUPPLIER fs ON est.FAV_SUPP_ID = fs.FAV_SUPPLIER_ID JOIN PROC_SUPPLIER_TAGS st ON est.SUPP_TAG_ID= st.ID WHERE ast.SUPPLIER_ID = fs.SUPPLIER_ID AND ast.EVENT_ID = e.ID) AS supplierTags ," //
					+ "(SELECT STRING_AGG(bq.TOTAL_AFTER_TAX,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID JOIN PROC_RFA_SUPPLIER_BQ bq ON bq.EVENT_ID = pas.EVENT_ID AND bq.SUPPLIER_ID = pas.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS finalBid ," //
					+ "CONVERT(varchar,SUM( es.NUMBER_OF_BIDS)) AS noOfBids ,"; //
		}

		sql += "count(es.ID) AS total_invited_suppliers " //
				+ " FROM PROC_RFA_EVENTS e" //
				+ " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID" //
				+ " LEFT OUTER JOIN PROC_RFX_TEMPLATE t ON e.TEMPLATE_ID = t.ID" //
				+ " INNER JOIN PROC_USER u ON e.EVENT_OWNER = u.ID" //
				+ " LEFT OUTER JOIN PROC_RFA_EVENT_SUPPLIERS es ON e.id = es.EVENT_ID" //
				+ " LEFT OUTER JOIN PROC_RFA_SUPPLIER_BQ  supBq ON e.ID = supBq.EVENT_ID AND es.SUPPLIER_ID = supBq.SUPPLIER_ID " //
				+ " LEFT OUTER JOIN PROC_CURRENCY currency on e.CURRENCY_ID = currency.ID " //
				+ " WHERE e.STATUS='FINISHED' AND e.AUCTION_TYPE IN('Forward English Auction', 'Forward Sealed Bid', 'Forward Dutch Auction') AND e.TENANT_ID = :tenantId";

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				sql += " and e.ID in (:eventIds)";
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				sql += " and upper(e.EVENT_NAME) like :nameOfEvent";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				sql += " and upper(e.REFERANCE_NUMBER) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				sql += " and upper(e.EVENT_ID) like :eventId";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				sql += " and upper(u.USER_NAME) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				sql += " and upper(bu.BUSINESS_UNIT_NAME) like :businessUnit";
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			sql += " and e.CREATED_DATE between :startDate and :endDate";
		}

		sql += " GROUP BY e.id, bu.BUSINESS_UNIT_NAME, e.BUYER_SET_DECIMAL, e.REFERANCE_NUMBER, e.EVENT_START,e.EVENT_END,e.EVENT_NAME,t.TEMPLATE_NAME,e.EVENT_VISIBILITY,u.USER_NAME,e.EVENT_ID,e.BUDGET_AMOUNT,e.HISTORICAL_AMOUNT, e.AUCTION_TYPE, e.WINNING_PRICE,currency.CURRENCY_CODE";
		// sql += " ORDER BY e.ID ";
		sql += " ) au";
		LOG.info(">>>>>>>>>>>>" + sql);
		return sql;
	}

	private String getSqlForAllReverseAuctionEventForReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, List<EventStatus> list, RfxTypes rfa) {
		String sql = "";

		sql += " SELECT id, eventName, decimalValue, currencyName, referanceNumber, sysEventId, auctiontype, ownerName, eventStart, eventEnd, eventVisibility, unitName, templateName, leadingSuppierBid, leadingSuppier, awardedSupplier, winningSupplier, selfInvitedWinner, leadingAmount, invitedSupplierCount, participatedSupplierCount, selfInvitedSupplierCount, submittedSupplierCount, eventCategories, budgetAmount, historicAmount, sumAwardedPrice, supplierTags, noOfBids, total_invited_suppliers, round(((submittedSupplierCount / CASE WHEN total_invited_suppliers = 0 THEN 1 ELSE total_invited_suppliers END)  * 100), 2) AS ratio FROM (" + "SELECT e.ID AS id, " + "e.EVENT_NAME AS eventName, " + "e.BUYER_SET_DECIMAL AS decimalValue, " + "currency.CURRENCY_CODE as currencyName, " + "e.REFERANCE_NUMBER AS referanceNumber, " + "e.EVENT_ID AS sysEventId, " + "e.AUCTION_TYPE AS auctiontype, " + "u.USER_NAME AS ownerName, " + "e.EVENT_START AS eventStart, " + "e.EVENT_END AS eventEnd, " + "e.EVENT_VISIBILITY AS eventVisibility, " + "bu.BUSINESS_UNIT_NAME AS unitName, " + "t.TEMPLATE_NAME AS templateName, " //
				+ "(SELECT prsb.TOTAL_AFTER_TAX FROM  PROC_RFA_EVENT_SUPPLIERS rfas, PROC_RFA_SUPPLIER_BQ prsb  WHERE rfas.EVENT_ID=e.id AND prsb.EVENT_ID = e.id AND rfas.IS_DISQUALIFY=0 AND prsb.SUPPLIER_ID = rfas.SUPPLIER_ID AND rfas.AUCTION_RANKING IS NOT null ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS leadingSuppierBid ," + "(SELECT ps.COMPANY_NAME FROM  PROC_RFA_EVENT_SUPPLIERS rfas join PROC_SUPPLIER ps ON rfas.SUPPLIER_ID = ps.SUPPLIER_ID  WHERE rfas.EVENT_ID=e.id AND rfas.IS_DISQUALIFY=0 AND rfas.AUCTION_RANKING IS NOT NULL ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS leadingSuppier , 'AW' AS awardedSupplier, ";//
		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += "(SELECT string_agg(ps.COMPANY_NAME,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS winningSupplier ," //
					+ "(SELECT (CASE WHEN rfas.IS_SELF_INVITED = 1 THEN 'YES' ELSE 'NO' END) FROM  PROC_RFA_EVENT_SUPPLIERS rfas join PROC_SUPPLIER ps ON rfas.SUPPLIER_ID = ps.SUPPLIER_ID  WHERE rfas.EVENT_ID=e.id AND rfas.IS_DISQUALIFY=0 AND rfas.AUCTION_RANKING IS NOT null ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS selfInvitedWinner ," //
					+ "e.WINNING_PRICE  AS leadingAmount, CAST(SUM( CASE WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END) as varchar) AS invitedSupplierCount, " //
					+ "CAST(SUM( CASE WHEN (es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' )THEN 1 ELSE 0 END ) as varchar) AS participatedSupplierCount," //
					+ "CAST(SUM( CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0 END) as varchar) AS selfInvitedSupplierCount ," //
					+ "CAST(SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END ) as varchar) AS submittedSupplierCount ," //
					+ "(SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, " //
					+ "e.BUDGET_AMOUNT AS budgetAmount , e.HISTORICAL_AMOUNT AS historicAmount ," //
					+ "(SELECT sum(pread.AWARDED_PRICE) FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID AND prea.EVENT_ID=e.ID WHERE pread.SUPPLIER_ID IS NOT NULL) AS sumAwardedPrice, " //
					+ "(SELECT string_agg(st.SUPPLIER_TAGS,', ' ORDER BY st.SUPPLIER_TAGS) cst FROM PROC_RFA_EVENT_SUPPLIERS rfas, PROC_FAV_SUPPLIER_TAGS est LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER fs ON est.FAV_SUPP_ID = fs.FAV_SUPPLIER_ID LEFT OUTER JOIN PROC_SUPPLIER_TAGS st ON est.SUPP_TAG_ID= st.ID WHERE rfas.SUPPLIER_ID = fs.SUPPLIER_ID AND rfas.EVENT_ID = e.ID AND rfas.IS_DISQUALIFY=0 AND rfas.SUBMISSION_STATUS = 'COMPLETED'  AND rfas.AUCTION_RANKING IS NOT NULL GROUP BY rfas.AUCTION_RANKING ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY ) AS supplierTags ," //
					+ "CAST(SUM( es.NUMBER_OF_BIDS) as varchar) AS noOfBids , count(es.ID) AS total_invited_suppliers ";
		} else {
			sql += "(SELECT STRING_AGG(ps.COMPANY_NAME,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS winningSupplier ," //
					+ "(SELECT (CASE WHEN rfas.IS_SELF_INVITED = 1 THEN 'YES' ELSE 'NO' END) FROM  PROC_RFA_EVENT_SUPPLIERS rfas join PROC_SUPPLIER ps ON rfas.SUPPLIER_ID = ps.SUPPLIER_ID  WHERE rfas.EVENT_ID=e.id AND rfas.IS_DISQUALIFY=0 AND rfas.AUCTION_RANKING IS NOT null ORDER BY rfas.AUCTION_RANKING OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY) AS selfInvitedWinner ," //
					+ "e.WINNING_PRICE  AS leadingAmount, CONVERT(varchar,SUM( CASE WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END)) AS invitedSupplierCount, " //
					+ "CONVERT(varchar,SUM(CASE WHEN (es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' )THEN 1 ELSE 0 END )) AS participatedSupplierCount," //
					+ "CONVERT(varchar,SUM(CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0 END)) AS selfInvitedSupplierCount ," //
					+ "CONVERT(varchar,SUM(CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) AS submittedSupplierCount ," //
					+ "(SELECT STRING_AGG(ic.CATEGORY_NAME,', ') WITHIN GROUP(ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, " //
					+ "e.BUDGET_AMOUNT AS budgetAmount , e.HISTORICAL_AMOUNT AS historicAmount ," //
					+ "(SELECT sum(pread.AWARDED_PRICE) FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID AND prea.EVENT_ID=e.ID WHERE pread.SUPPLIER_ID IS NOT NULL) AS sumAwardedPrice, " //
					+ "(SELECT STRING_AGG(st.SUPPLIER_TAGS,', ') WITHIN GROUP(ORDER BY st.SUPPLIER_TAGS) cst FROM PROC_RFA_EVENT_SUPPLIERS rfas, PROC_FAV_SUPPLIER_TAGS est LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER fs ON est.FAV_SUPP_ID = fs.FAV_SUPPLIER_ID LEFT OUTER JOIN PROC_SUPPLIER_TAGS st ON est.SUPP_TAG_ID= st.ID WHERE rfas.SUPPLIER_ID = fs.SUPPLIER_ID AND rfas.EVENT_ID = e.ID AND rfas.IS_DISQUALIFY=0 AND rfas.SUBMISSION_STATUS = 'COMPLETED'  AND rfas.AUCTION_RANKING IS NOT NULL GROUP BY rfas.AUCTION_RANKING ORDER BY rfas.AUCTION_RANKING OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ) AS supplierTags ," //
					+ "CONVERT(varchar,SUM( es.NUMBER_OF_BIDS)) AS noOfBids ,count(es.ID) AS total_invited_suppliers ";
		}

		// + "ROUND((((SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) / ( SUM( CASE
		// WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END) + SUM( CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0
		// END))) * 100),2) AS ratio "
		sql += " FROM PROC_RFA_EVENTS e" //
				+ " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID" //
				+ " LEFT OUTER JOIN PROC_RFX_TEMPLATE t ON e.TEMPLATE_ID = t.ID" //
				+ " INNER JOIN PROC_USER u ON e.EVENT_OWNER = u.ID" //
				+ " LEFT OUTER JOIN PROC_RFA_EVENT_SUPPLIERS es ON e.id = es.EVENT_ID" //
				+ " LEFT OUTER JOIN PROC_RFA_SUPPLIER_BQ  supBq ON e.ID = supBq.EVENT_ID AND es.SUPPLIER_ID = supBq.SUPPLIER_ID " //
				+ " LEFT OUTER JOIN PROC_CURRENCY currency on e.CURRENCY_ID = currency.ID " //
				+ " WHERE e.STATUS='FINISHED' AND e.AUCTION_TYPE IN('Reverse English Auction', 'Reverse Sealed Bid', 'Reverse Dutch Auction') AND e.TENANT_ID = :tenantId";

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				sql += " and e.ID in (:eventIds)";
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				sql += " and upper(e.EVENT_NAME) like :nameOfEvent";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				sql += " and upper(e.REFERANCE_NUMBER) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				sql += " and upper(e.EVENT_ID) like :eventId";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				sql += " and upper(u.USER_NAME) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				sql += " and upper(bu.BUSINESS_UNIT_NAME) like :businessUnit";
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			sql += " and e.CREATED_DATE between :startDate and :endDate";
		}

		sql += " GROUP BY e.id, bu.BUSINESS_UNIT_NAME, e.BUYER_SET_DECIMAL, e.REFERANCE_NUMBER, e.EVENT_START,e.EVENT_END,e.EVENT_NAME,t.TEMPLATE_NAME,e.EVENT_VISIBILITY,u.USER_NAME,e.EVENT_ID,e.BUDGET_AMOUNT,e.HISTORICAL_AMOUNT, e.AUCTION_TYPE, e.WINNING_PRICE,currency.CURRENCY_CODE";
		// sql += " ORDER BY e.ID";
		sql += " ) au";
		LOG.info(">>>>>>>>>>>>" + sql);
		return sql;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> getAllRfaEventWhereUnMaskingUserIsNotNull() {
		String sql = "select e from RfaEvent e where e.unMaskedUser is not null";
		final Query query = getEntityManager().createQuery(sql);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventUnMaskUser(String eventId) {
		Query query = getEntityManager().createQuery("update RfaEvent e set e.unMaskedUser = null where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllUnmaskedUsers(String eventId, String eventType) {

		Query query = getEntityManager().createNativeQuery("DELETE FROM PROC_" + eventType + "_UNMASKED_USER E WHERE E.EVENT_ID = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqItemComments(String eventId, String eventType) {

		Query query = getEntityManager().createNativeQuery("DELETE FROM PROC_" + eventType + "_EVALUATION_BQ_COMTS E WHERE E.EVENT_ID = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();

		Query bqTotalquery = getEntityManager().createNativeQuery("DELETE FROM PROC_" + eventType + "_EVAL_BQ_TOTAL_COMTS E WHERE E.EVENT_ID = :eventId").setParameter("eventId", eventId);
		bqTotalquery.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public RfaEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		final Query query = getEntityManager().createQuery("from RfaEvaluationConclusionUser u where u.id = :evalConUserId and u.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.setParameter("evalConUserId", evalConUserId);
		List<RfaEvaluationConclusionUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public RfaEvent getPlainEventWithOwnerById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r left outer join fetch r.eventOwner ew where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RfaEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from RfaEvaluationConclusionUser u where  u.event.id = :eventId");
		query.setParameter("eventId", eventId);
		List<RfaEvaluationConclusionUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> findAssociateOwnerOfRfa(String id, TeamMemberType associateOwner) {
		String hql = " from RfaTeamMember ptm where ptm.event.id = :id and ptm.teamMemberType =:associateOwner";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		query.setParameter("associateOwner", associateOwner);

		List<EventTeamMember> prTeamList = query.getResultList();
		return prTeamList;
	}

	@Override
	public RfaEvent getEventDetailsForFeePayment(String id) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RfaEvent(e.id, e.eventId, e.eventName, e.referanceNumber, e.participationFees, c, cb) from RfaEvent e left outer join e.participationFeeCurrency c left outer join e.createdBy cb where e.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", id);
		return (RfaEvent) query.getSingleResult();
	}

	@Override
	public long findTotalEventsCountForCsv(String tenantId, List<AuctionType> auctionTypeList) {
		String hql = "select count(re.id) from RfaEvent re  where re.tenantId = :tenantId and re.status=:eventStatus and re.auctionType in (:auctionTypeList)";
		Query query = getEntityManager().createQuery(hql);

		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStatus", EventStatus.FINISHED);
		query.setParameter("auctionTypeList", auctionTypeList);
		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> findAllActiveEventsForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, String auctionTypeS) {

		List<EventStatus> list = new ArrayList<EventStatus>();
		String sql = "";

		if (auctionTypeS.equals("FORWARD")) {
			sql += getSqlForAllForwardAuctionEventForCsvReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list);
		} else {
			sql += getSqlForAllReverseAuctionEventForCsvReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list);
		}

		sql += " ";
		// LOG.info("====>" + sql);

		final Query query = getEntityManager().createNativeQuery(sql, "auctionExcelReportData");

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				query.setParameter("eventIds", Arrays.asList(eventIds));
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				query.setParameter("nameOfEvent", "%" + searchFilterEventPojo.getNameofevent().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				query.setParameter("referenceNumber", "%" + searchFilterEventPojo.getReferencenumber().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				query.setParameter("eventId", "%" + searchFilterEventPojo.getEventid().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				query.setParameter("eventOwner", "%" + searchFilterEventPojo.getEventowner().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				query.setParameter("businessUnit", "%" + searchFilterEventPojo.getBusinessunit().toUpperCase() + "%");
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				query.setParameter("status", "%" + searchFilterEventPojo.getBusinessunit().toUpperCase() + "%");
			}

		}
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);
		List<DraftEventPojo> eventList = null;
		try {
			eventList = query.getResultList();
		} catch (Exception e) {
			eventList = new ArrayList<>();
		}
		List<DraftEventPojo> finalList = new ArrayList<DraftEventPojo>();

		for (DraftEventPojo evt : eventList) {
			if (finalList.contains(evt)) {

				DraftEventPojo levt = finalList.get(finalList.indexOf(evt));
				if (StringUtils.checkString(levt.getAssoiciateOwner()).length() == 0) {
					if (StringUtils.checkString(evt.getMemberType()).equals(TeamMemberType.Associate_Owner.name())) {
						levt.setAssoiciateOwner(StringUtils.checkString(evt.getAssoiciateOwner()));
					}
				} else if (StringUtils.checkString(evt.getAssoiciateOwner()).length() > 0) {
					if (StringUtils.checkString(evt.getMemberType()).equals(TeamMemberType.Associate_Owner.name())) {
						levt.setAssoiciateOwner(StringUtils.checkString(levt.getAssoiciateOwner()) + "," + StringUtils.checkString(evt.getAssoiciateOwner()));
					}
				}
			} else {
				if (!StringUtils.checkString(evt.getMemberType()).equals(TeamMemberType.Associate_Owner.name())) {
					evt.setAssoiciateOwner("");
				}
				finalList.add(evt);
			}
		}

		return finalList;

	}

	private String getSqlForAllReverseAuctionEventForCsvReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, List<EventStatus> list) {
		String sql = "";

		sql += " SELECT id, eventName, decimalValue, currencyName, referanceNumber, sysEventId, auctiontype, ownerName, eventStart, eventEnd, eventVisibility, unitName, templateName, leadingSuppierBid, leadingSuppier, awardedSupplier, winningSupplier, selfInvitedWinner, leadingAmount, invitedSupplierCount, participatedSupplierCount, selfInvitedSupplierCount, submittedSupplierCount, eventCategories, budgetAmount, historicAmount, sumAwardedPrice, supplierTags, noOfBids, total_invited_suppliers, round(((submittedSupplierCount / CASE WHEN total_invited_suppliers = 0 THEN 1 ELSE total_invited_suppliers END)  * 100), 2) AS ratio FROM (" + "SELECT e.ID AS id, " + "e.EVENT_NAME AS eventName, " + "e.BUYER_SET_DECIMAL AS decimalValue, " + "currency.CURRENCY_CODE as currencyName, " + "e.REFERANCE_NUMBER AS referanceNumber, " + "e.EVENT_ID AS sysEventId, " + "e.AUCTION_TYPE AS auctiontype, " + "u.USER_NAME AS ownerName, " + "e.EVENT_START AS eventStart, " + "e.EVENT_END AS eventEnd, " + "e.EVENT_VISIBILITY AS eventVisibility, " + "bu.BUSINESS_UNIT_NAME AS unitName, " + "t.TEMPLATE_NAME AS templateName, " //
				+ "(SELECT prsb.TOTAL_AFTER_TAX FROM  PROC_RFA_EVENT_SUPPLIERS rfas, PROC_RFA_SUPPLIER_BQ prsb  WHERE rfas.EVENT_ID=e.id AND prsb.EVENT_ID = e.id AND rfas.IS_DISQUALIFY=0 AND prsb.SUPPLIER_ID = rfas.SUPPLIER_ID AND rfas.AUCTION_RANKING IS NOT null ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS leadingSuppierBid ," + "(SELECT ps.COMPANY_NAME FROM  PROC_RFA_EVENT_SUPPLIERS rfas join PROC_SUPPLIER ps ON rfas.SUPPLIER_ID = ps.SUPPLIER_ID  WHERE rfas.EVENT_ID=e.id AND rfas.IS_DISQUALIFY=0 AND rfas.AUCTION_RANKING IS NOT NULL ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS leadingSuppier , 'AW' AS awardedSupplier, ";//
		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += "(SELECT string_agg(ps.COMPANY_NAME,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS winningSupplier ," //
					+ "(SELECT (CASE WHEN rfas.IS_SELF_INVITED = 1 THEN 'YES' ELSE 'NO' END) FROM  PROC_RFA_EVENT_SUPPLIERS rfas join PROC_SUPPLIER ps ON rfas.SUPPLIER_ID = ps.SUPPLIER_ID  WHERE rfas.EVENT_ID=e.id AND rfas.IS_DISQUALIFY=0 AND rfas.AUCTION_RANKING IS NOT null ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS selfInvitedWinner ," //
					+ "e.WINNING_PRICE  AS leadingAmount, CAST(SUM( CASE WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END) as varchar) AS invitedSupplierCount, " //
					+ "CAST(SUM( CASE WHEN (es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' )THEN 1 ELSE 0 END ) as varchar) AS participatedSupplierCount," //
					+ "CAST(SUM( CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0 END) as varchar) AS selfInvitedSupplierCount ," //
					+ "SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )  AS submittedSupplierCount ," //
					+ "(SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, " //
					+ "e.BUDGET_AMOUNT AS budgetAmount , e.HISTORICAL_AMOUNT AS historicAmount ," //
					+ "(SELECT sum(pread.AWARDED_PRICE) FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID AND prea.EVENT_ID=e.ID WHERE pread.SUPPLIER_ID IS NOT NULL) AS sumAwardedPrice, " //
					+ "(SELECT string_agg(st.SUPPLIER_TAGS,', ' ORDER BY st.SUPPLIER_TAGS) cst FROM PROC_RFA_EVENT_SUPPLIERS rfas, PROC_FAV_SUPPLIER_TAGS est LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER fs ON est.FAV_SUPP_ID = fs.FAV_SUPPLIER_ID LEFT OUTER JOIN PROC_SUPPLIER_TAGS st ON est.SUPP_TAG_ID= st.ID WHERE rfas.SUPPLIER_ID = fs.SUPPLIER_ID AND rfas.EVENT_ID = e.ID AND rfas.IS_DISQUALIFY=0 AND rfas.SUBMISSION_STATUS = 'COMPLETED'  AND rfas.AUCTION_RANKING IS NOT NULL GROUP BY rfas.AUCTION_RANKING ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY ) AS supplierTags ," //
					+ "CAST(SUM( es.NUMBER_OF_BIDS) as varchar) AS noOfBids , count(es.ID) AS total_invited_suppliers ";
		} else {
			sql += "(SELECT STRING_AGG(ps.COMPANY_NAME,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS winningSupplier ," //
					+ "(SELECT (CASE WHEN rfas.IS_SELF_INVITED = 1 THEN 'YES' ELSE 'NO' END) FROM  PROC_RFA_EVENT_SUPPLIERS rfas join PROC_SUPPLIER ps ON rfas.SUPPLIER_ID = ps.SUPPLIER_ID  WHERE rfas.EVENT_ID=e.id AND rfas.IS_DISQUALIFY=0 AND rfas.AUCTION_RANKING IS NOT null ORDER BY rfas.AUCTION_RANKING OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY) AS selfInvitedWinner ," //
					+ "e.WINNING_PRICE  AS leadingAmount, CONVERT(varchar,SUM( CASE WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END)) AS invitedSupplierCount, " //
					+ "CONVERT(varchar,SUM(CASE WHEN (es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' )THEN 1 ELSE 0 END )) AS participatedSupplierCount," //
					+ "CONVERT(varchar,SUM(CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0 END)) AS selfInvitedSupplierCount ," //
					+ "CONVERT(varchar,SUM(CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) AS submittedSupplierCount ," //
					+ "(SELECT STRING_AGG(ic.CATEGORY_NAME,', ') WITHIN GROUP(ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, " //
					+ "e.BUDGET_AMOUNT AS budgetAmount , e.HISTORICAL_AMOUNT AS historicAmount ," //
					+ "(SELECT sum(pread.AWARDED_PRICE) FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID AND prea.EVENT_ID=e.ID WHERE pread.SUPPLIER_ID IS NOT NULL) AS sumAwardedPrice, " //
					+ "(SELECT STRING_AGG(st.SUPPLIER_TAGS,', ') WITHIN GROUP(ORDER BY st.SUPPLIER_TAGS) cst FROM PROC_RFA_EVENT_SUPPLIERS rfas, PROC_FAV_SUPPLIER_TAGS est LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER fs ON est.FAV_SUPP_ID = fs.FAV_SUPPLIER_ID LEFT OUTER JOIN PROC_SUPPLIER_TAGS st ON est.SUPP_TAG_ID= st.ID WHERE rfas.SUPPLIER_ID = fs.SUPPLIER_ID AND rfas.EVENT_ID = e.ID AND rfas.IS_DISQUALIFY=0 AND rfas.SUBMISSION_STATUS = 'COMPLETED'  AND rfas.AUCTION_RANKING IS NOT NULL GROUP BY rfas.AUCTION_RANKING ORDER BY rfas.AUCTION_RANKING OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ) AS supplierTags ," //
					+ "CONVERT(varchar,SUM( es.NUMBER_OF_BIDS)) AS noOfBids ,count(es.ID) AS total_invited_suppliers ";
		}

		// + "ROUND((((SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) / ( SUM( CASE
		// WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END) + SUM( CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0
		// END))) * 100),2) AS ratio "
		sql += " FROM PROC_RFA_EVENTS e" //
				+ " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID" //
				+ " LEFT OUTER JOIN PROC_RFX_TEMPLATE t ON e.TEMPLATE_ID = t.ID" //
				+ " INNER JOIN PROC_USER u ON e.EVENT_OWNER = u.ID" //
				+ " LEFT OUTER JOIN PROC_RFA_EVENT_SUPPLIERS es ON e.id = es.EVENT_ID" //
				+ " LEFT OUTER JOIN PROC_RFA_SUPPLIER_BQ  supBq ON e.ID = supBq.EVENT_ID AND es.SUPPLIER_ID = supBq.SUPPLIER_ID " //
				+ " LEFT OUTER JOIN PROC_CURRENCY currency on e.CURRENCY_ID = currency.ID " //
				+ " WHERE e.STATUS='FINISHED' AND e.AUCTION_TYPE IN('Reverse English Auction', 'Reverse Sealed Bid', 'Reverse Dutch Auction') AND e.TENANT_ID = :tenantId";

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				sql += " and e.ID in (:eventIds)";
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				sql += " and upper(e.EVENT_NAME) like :nameOfEvent";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				sql += " and upper(e.REFERANCE_NUMBER) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				sql += " and upper(e.EVENT_ID) like :eventId";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				sql += " and upper(u.USER_NAME) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				sql += " and upper(bu.BUSINESS_UNIT_NAME) like :businessUnit";
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			sql += " and e.CREATED_DATE between :startDate and :endDate";
		}

		sql += " GROUP BY e.id, bu.BUSINESS_UNIT_NAME, e.BUYER_SET_DECIMAL, e.REFERANCE_NUMBER, e.EVENT_START,e.EVENT_END,e.EVENT_NAME,t.TEMPLATE_NAME,e.EVENT_VISIBILITY,u.USER_NAME,e.EVENT_ID,e.BUDGET_AMOUNT,e.HISTORICAL_AMOUNT, e.AUCTION_TYPE, e.WINNING_PRICE,currency.CURRENCY_CODE";
		// sql += " ORDER BY e.ID";
		sql += " ) au";
		LOG.info(">>>>>>>>>>>>" + sql);
		return sql;
	}

	private String getSqlForAllForwardAuctionEventForCsvReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, List<EventStatus> list) {
		LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> tenantId :" + tenantId);
		String sql = "";

		sql += " SELECT id, eventName, decimalValue, currencyName, referanceNumber, sysEventId, auctiontype, ownerName, eventStart, eventEnd, eventVisibility, unitName, templateName, leadingSuppierBid, leadingSuppier, awardedSupplier, awardedPrice, winningSupplier, selfInvitedWinner, leadingAmount, invitedSupplierCount, participatedSupplierCount, selfInvitedSupplierCount, submittedSupplierCount, eventCategories, budgetAmount,historicAmount, sumAwardedPrice, supplierTags, finalBid, noOfBids, round(((submittedSupplierCount / CASE WHEN total_invited_suppliers = 0 THEN 1 ELSE total_invited_suppliers END)  * 100), 2) AS ratio FROM (" //
				+ " SELECT e.ID AS id, " //
				+ "e.EVENT_NAME AS eventName, "//
				+ "e.BUYER_SET_DECIMAL AS decimalValue, " //
				+ "currency.CURRENCY_CODE as currencyName, " //
				+ "e.REFERANCE_NUMBER AS referanceNumber, " //
				+ "e.EVENT_ID AS sysEventId, " //
				+ "e.AUCTION_TYPE AS auctiontype, " //
				+ "u.USER_NAME AS ownerName, " //
				+ "e.EVENT_START AS eventStart, " //
				+ "e.EVENT_END AS eventEnd, " //
				+ "e.EVENT_VISIBILITY AS eventVisibility, " //
				+ "bu.BUSINESS_UNIT_NAME AS unitName, " //
				+ "t.TEMPLATE_NAME AS templateName, " //
				+ "(SELECT prsb.TOTAL_AFTER_TAX FROM  PROC_RFA_EVENT_SUPPLIERS rfas, PROC_RFA_SUPPLIER_BQ prsb  WHERE rfas.EVENT_ID=e.id "//
				+ " AND prsb.EVENT_ID = e.id AND rfas.IS_DISQUALIFY=0 AND prsb.SUPPLIER_ID = rfas.SUPPLIER_ID " //
				+ " AND rfas.AUCTION_RANKING IS NOT null ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS leadingSuppierBid ," //
				+ "(SELECT ps.COMPANY_NAME FROM  PROC_RFA_EVENT_SUPPLIERS rfas join PROC_SUPPLIER ps ON rfas.SUPPLIER_ID = ps.SUPPLIER_ID  WHERE rfas.EVENT_ID=e.id AND rfas.IS_DISQUALIFY=0 ORDER BY rfas.AUCTION_RANKING FETCH NEXT 1 ROWS ONLY) AS leadingSuppier ,"; //
		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += "(SELECT string_agg(ps.COMPANY_NAME,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS awardedSupplier, " //
					+ "(SELECT string_agg(CAST(pread.AWARDED_PRICE as varchar),', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS awardedPrice," //
					+ "(SELECT string_agg(ps.COMPANY_NAME,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS winningSupplier ," //
					+ "(SELECT string_agg(CASE WHEN es.IS_SELF_INVITED = 1 THEN 'YES' ELSE 'NO' END,', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID  JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID JOIN PROC_RFA_EVENT_SUPPLIERS es ON es.SUPPLIER_ID=pread.SUPPLIER_ID AND es.EVENT_ID=prea.EVENT_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS selfInvitedWinner ,"; //
		} else {
			sql += "(SELECT STRING_AGG(ps.COMPANY_NAME,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS awardedSupplier, " //
					+ "(SELECT STRING_AGG(pread.AWARDED_PRICE,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS awardedPrice," //
					+ "(SELECT STRING_AGG(ps.COMPANY_NAME,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS winningSupplier ," //
					+ "(SELECT STRING_AGG(CASE WHEN es.IS_SELF_INVITED = 1 THEN 'YES' ELSE 'NO' END,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID INNER JOIN PROC_RFA_EVENTS pre ON PREA.EVENT_ID=pre.ID  JOIN PROC_SUPPLIER ps ON pread.SUPPLIER_ID=ps.SUPPLIER_ID JOIN PROC_RFA_EVENT_SUPPLIERS es ON es.SUPPLIER_ID=pread.SUPPLIER_ID AND es.EVENT_ID=prea.EVENT_ID WHERE pre.ID=e.id AND pread.SUPPLIER_ID IS NOT NULL) AS selfInvitedWinner ,"; //
		}

		sql += "e.WINNING_PRICE  AS leadingAmount, "; //
		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += "CAST(SUM( CASE WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END) as varchar) AS invitedSupplierCount, " //
					+ "CAST(SUM( CASE WHEN (es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' )THEN 1 ELSE 0 END ) as varchar) AS participatedSupplierCount," //
					+ "CAST(SUM( CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0 END) as varchar) AS selfInvitedSupplierCount ," //
					+ "SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END ) AS submittedSupplierCount ," //
					+ "(SELECT STRING_AGG(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, "; //
		} else {
			sql += "CONVERT(varchar,SUM( CASE WHEN (es.IS_SELF_INVITED=0)THEN 1 ELSE 0 END)) AS invitedSupplierCount, " //
					+ "CONVERT(varchar,SUM( CASE WHEN (es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' )THEN 1 ELSE 0 END )) AS participatedSupplierCount," //
					+ "CONVERT(varchar,SUM( CASE WHEN (es.IS_SELF_INVITED=1)THEN 1 ELSE 0 END)) AS selfInvitedSupplierCount ," //
					+ "CONVERT(varchar,SUM(CASE WHEN ( es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) AS submittedSupplierCount ," //
					+ "(SELECT STRING_AGG(ic.CATEGORY_NAME,', ') WITHIN GROUP(ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFA_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, "; //
		}

		sql += "e.BUDGET_AMOUNT AS budgetAmount ," //
				+ "e.HISTORICAL_AMOUNT AS historicAmount ," //
				+ "(SELECT sum(pread.AWARDED_PRICE) FROM PROC_RFA_EVENT_AWARD_DETAILS pread INNER JOIN PROC_RFA_EVENT_AWARD prea ON pread.EVENT_AWARD=prea.ID AND prea.EVENT_ID=e.ID WHERE pread.SUPPLIER_ID IS NOT NULL) AS sumAwardedPrice, "; //

		if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
			sql += " (SELECT STRING_AGG(st.SUPPLIER_TAGS,', ' ORDER BY st.SUPPLIER_TAGS) cst FROM PROC_RFA_AWARD_SUP ast, PROC_FAV_SUPPLIER_TAGS est JOIN PROC_FAVOURITE_SUPPLIER fs ON est.FAV_SUPP_ID = fs.FAV_SUPPLIER_ID JOIN PROC_SUPPLIER_TAGS st ON est.SUPP_TAG_ID= st.ID WHERE ast.SUPPLIER_ID = fs.SUPPLIER_ID AND ast.EVENT_ID = e.ID) AS supplierTags ," //
					+ "(SELECT string_agg(CAST(bq.TOTAL_AFTER_TAX as varchar),', ' ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID JOIN PROC_RFA_SUPPLIER_BQ bq ON bq.EVENT_ID = pas.EVENT_ID AND bq.SUPPLIER_ID = pas.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS finalBid ," //
					+ "CAST(SUM( es.NUMBER_OF_BIDS) as varchar) AS noOfBids ,"; //
		} else {
			sql += " (SELECT STRING_AGG(st.SUPPLIER_TAGS,', ') WITHIN GROUP(ORDER BY st.SUPPLIER_TAGS) cst FROM PROC_RFA_AWARD_SUP ast, PROC_FAV_SUPPLIER_TAGS est JOIN PROC_FAVOURITE_SUPPLIER fs ON est.FAV_SUPP_ID = fs.FAV_SUPPLIER_ID JOIN PROC_SUPPLIER_TAGS st ON est.SUPP_TAG_ID= st.ID WHERE ast.SUPPLIER_ID = fs.SUPPLIER_ID AND ast.EVENT_ID = e.ID) AS supplierTags ," //
					+ "(SELECT STRING_AGG(bq.TOTAL_AFTER_TAX,', ') WITHIN GROUP(ORDER BY ps.COMPANY_NAME) asl FROM PROC_RFA_AWARD_SUP pas JOIN PROC_SUPPLIER ps ON pas.SUPPLIER_ID =ps.SUPPLIER_ID JOIN PROC_RFA_SUPPLIER_BQ bq ON bq.EVENT_ID = pas.EVENT_ID AND bq.SUPPLIER_ID = pas.SUPPLIER_ID WHERE pas.EVENT_ID=e.ID) AS finalBid ," //
					+ "CONVERT(varchar,SUM( es.NUMBER_OF_BIDS)) AS noOfBids ,"; //
		}

		sql += "count(es.ID) AS total_invited_suppliers " //
				+ " FROM PROC_RFA_EVENTS e" //
				+ " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID" //
				+ " LEFT OUTER JOIN PROC_RFX_TEMPLATE t ON e.TEMPLATE_ID = t.ID" //
				+ " INNER JOIN PROC_USER u ON e.EVENT_OWNER = u.ID" //
				+ " LEFT OUTER JOIN PROC_RFA_EVENT_SUPPLIERS es ON e.id = es.EVENT_ID" //
				+ " LEFT OUTER JOIN PROC_RFA_SUPPLIER_BQ  supBq ON e.ID = supBq.EVENT_ID AND es.SUPPLIER_ID = supBq.SUPPLIER_ID " //
				+ " LEFT OUTER JOIN PROC_CURRENCY currency on e.CURRENCY_ID = currency.ID " //
				+ " WHERE e.STATUS='FINISHED' AND e.AUCTION_TYPE IN('Forward English Auction', 'Forward Sealed Bid', 'Forward Dutch Auction') AND e.TENANT_ID = :tenantId";

		if (!(select_all)) {
			if (eventIds != null && eventIds.length > 0) {
				sql += " and e.ID in (:eventIds)";
			}
		}
		if (searchFilterEventPojo != null) {
			if (StringUtils.checkString(searchFilterEventPojo.getNameofevent()).length() > 0) {
				sql += " and upper(e.EVENT_NAME) like :nameOfEvent";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getReferencenumber()).length() > 0) {
				sql += " and upper(e.REFERANCE_NUMBER) like :referenceNumber";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventid()).length() > 0) {
				sql += " and upper(e.EVENT_ID) like :eventId";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getEventowner()).length() > 0) {
				sql += " and upper(u.USER_NAME) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				sql += " and upper(bu.BUSINESS_UNIT_NAME) like :businessUnit";
			}
		}
		// search with Date range
		if (startDate != null && endDate != null) {
			sql += " and e.CREATED_DATE between :startDate and :endDate";
		}

		sql += " GROUP BY e.id, bu.BUSINESS_UNIT_NAME, e.BUYER_SET_DECIMAL, e.REFERANCE_NUMBER, e.EVENT_START,e.EVENT_END,e.EVENT_NAME,t.TEMPLATE_NAME,e.EVENT_VISIBILITY,u.USER_NAME,e.EVENT_ID,e.BUDGET_AMOUNT,e.HISTORICAL_AMOUNT, e.AUCTION_TYPE, e.WINNING_PRICE,currency.CURRENCY_CODE";
		// sql += " ORDER BY e.ID ";
		sql += " ) au";
		LOG.info(">>>>>>>>>>>>" + sql);
		return sql;
	}

	@Override
	public long getRfaEventCountByTenantId(String searchValue, String tenantId, String userId, String industryCategory) {
		StringBuffer sb = new StringBuffer("select count(distinct re.id) from RfaEvent re left outer join re.industryCategories i left outer join re.industryCategory where re.tenantId =:tenantId ");
		if (StringUtils.checkString(userId).length() > 0) {
			sb.append(" and re.eventOwner.id = :userId");
		}

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(re.eventName) like :searchValue or upper(re.referanceNumber) like :searchValue or upper(re.eventId) like :searchValue) ");
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			sb.append(" and i.id = :industryCategory  ");
		}

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		if (StringUtils.checkString(userId).length() > 0) {
			query.setParameter("userId", userId);
		}
		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
			LOG.info("searchValue" + searchValue.toUpperCase());
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			query.setParameter("industryCategory", industryCategory);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String userId) {
		StringBuffer sb = new StringBuffer("select distinct e from RfaEvent e left outer join fetch e.industryCategories i left outer join fetch e.industryCategory where e.tenantId = :tenantId ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append("and (upper(e.eventName) like :searchValue or upper(e.referanceNumber) like :searchValue or upper(e.eventId) like :searchValue) ");
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			sb.append("and i.id = :industryCategory ");
		}
		if (StringUtils.checkString(userId).length() > 0) {
			sb.append("and e.eventOwner.id = :loggedInUser ");
		}
		sb.append("order by e.createdDate desc");

		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);

		if (StringUtils.checkString(searchValue).length() > 0) {
			query.setParameter("searchValue", "%" + searchValue.toUpperCase() + "%");
			LOG.info("searchValue" + searchValue.toUpperCase());
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			query.setParameter("industryCategory", industryCategory);
		}
		if (StringUtils.checkString(userId).length() > 0) {
			query.setParameter("loggedInUser", userId);
		}

		if (start != null && pageLength != null) {
			query.setFirstResult(start);
			query.setMaxResults(pageLength);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAssociateOwnersOfEventsByEventId(String eventId, TeamMemberType memberType) {
		Query query = getEntityManager().createQuery("select new User(tm.user.id, tm.user.name, tm.user.communicationEmail, tm.user.emailNotifications,  tm.user.loginId, tm.user.tenantId, tm.user.deviceId) from RfaTeamMember tm where tm.event.id = :eventId and tm.teamMemberType =:memberType");
		query.setParameter("eventId", eventId);
		query.setParameter("memberType", memberType);
		return (List<User>) query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void revertEventAwardStatus(String eventId) {
		// Query query = getEntityManager().createQuery("update RfaEvent e set e.status = :status where e.id =
		// :eventId").setParameter("status", EventStatus.COMPLETE).setParameter("eventId", eventId);
		// query.executeUpdate();

		Query query = getEntityManager().createQuery("update RfaEvent e set e.status = :status, e.awardStatus = :awardStatus, e.awarded = false where e.id = :eventId")//
				.setParameter("status", EventStatus.COMPLETE)//
				.setParameter("awardStatus", AwardStatus.DRAFT)//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RfaEventAward e set e.transferred = false where e.rfxEvent.id = :eventId")//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RfaEventAwardApproval e set e.done = false, e.active = false where e.event.id = :eventId")//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RfaAwardApprovalUser e set e.actionDate = null, e.approvalStatus = :approvalStatus, e.remarks = null where e.approval.id in (select a.id from RfaEventAwardApproval a where a.event.id = :eventId ) ")//
				.setParameter("approvalStatus", ApprovalStatus.PENDING)//
				.setParameter("eventId", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getAllRfaEventIdsByLoginId(String loginId) {
		String sql = "select distinct new com.privasia.procurehere.core.entity.Event(re.id,  re.eventId, re.status) from RfaEvent re inner join re.createdBy cr  where cr.id =:loginId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("loginId", loginId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateTransferOwnerForEvent(String fromUserId, String toUserId) {


		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_RFA_EVENTS SET CREATED_BY = :toUserId, EVENT_OWNER = :toUserId  where CREATED_BY =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators transferred: {}", recordsUpdated);

		//transfer the user to team members too
		Query query2 = getEntityManager().createQuery(
				"UPDATE RfaTeamMember team " +
						"SET team.user = :targetUser " +
						"WHERE team.user = :sourceUser "
		);
		query2.setParameter("sourceUser", sourceUser);
		query2.setParameter("targetUser", targetUser);
		recordsUpdated = query2.executeUpdate();
		LOG.info("Form Owners transferred: {}", recordsUpdated);

		//event viewers
		Query query3 = getEntityManager().createNativeQuery("UPDATE PROC_RFA_EVENT_VIEWERS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query3.setParameter("toUserId", toUserId);
		query3.setParameter("fromUserId", fromUserId);
		recordsUpdated = query3.executeUpdate();
		LOG.info("Event Viewers transferred: {}", recordsUpdated);

		//event editors
		Query query4 = getEntityManager().createNativeQuery("UPDATE PROC_RFA_EVENT_EDITORS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query4.setParameter("toUserId", toUserId);
		query4.setParameter("fromUserId", fromUserId);
		recordsUpdated = query4.executeUpdate();
		LOG.info("Event Editors transferred: {}", recordsUpdated);

		//transfer ownership of approvals only for RFS with status of DRAFT and PENDING
		Query query5 = getEntityManager().createQuery(
				"UPDATE RfaApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		recordsUpdated = query5.executeUpdate();
		LOG.info("Approval user transferred: {}", recordsUpdated);

		Query query6 = getEntityManager().createQuery(
				"UPDATE RfaUnMaskedUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query6.setParameter("sourceUser", sourceUser);
		query6.setParameter("targetUser", targetUser);
		recordsUpdated = query6.executeUpdate();
		LOG.info("Unmasked user transferred: {}", recordsUpdated);

		Query query7 = getEntityManager().createQuery(
				"UPDATE RfaEvaluationConclusionUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query7.setParameter("sourceUser", sourceUser);
		query7.setParameter("targetUser", targetUser);
		recordsUpdated = query7.executeUpdate();
		LOG.info("Evaluation Conclusion user transferred: {}", recordsUpdated);

		Query query8 = getEntityManager().createQuery(
				"UPDATE RfaSuspensionApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query8.setParameter("sourceUser", sourceUser);
		query8.setParameter("targetUser", targetUser);
		recordsUpdated = query8.executeUpdate();
		LOG.info("Suspension Approval user transferred: {}", recordsUpdated);

		//envelope openers
		Query query9 = getEntityManager().createNativeQuery("UPDATE PROC_RFA_ENV_OPEN_USERS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query9.setParameter("toUserId", toUserId);
		query9.setParameter("fromUserId", fromUserId);
		recordsUpdated = query9.executeUpdate();
		LOG.info("Envelope Openers transferred: {}", recordsUpdated);

		Query query10 = getEntityManager().createNativeQuery("UPDATE PROC_RFA_EVALUATOR_USER SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query10.setParameter("toUserId", toUserId);
		query10.setParameter("fromUserId", fromUserId);
		recordsUpdated = query10.executeUpdate();
		LOG.info("Evaluator users transferred: {}", recordsUpdated);
	}

	@Override
	public RfaEvent findEventForSapByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RfaEvent r where r.eventId =:eventId");
			query.setParameter("eventId", eventId);
			List<RfaEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting user : " + nr.getMessage(), nr);
			return null;
		}
	}

}
