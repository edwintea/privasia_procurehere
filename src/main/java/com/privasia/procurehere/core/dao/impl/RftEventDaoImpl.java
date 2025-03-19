package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RftApprovalUser;
import com.privasia.procurehere.core.entity.RftAwardApprovalUser;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RftEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventApproval;
import com.privasia.procurehere.core.entity.RftEventAwardApproval;
import com.privasia.procurehere.core.entity.RftEventAwardAudit;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.RftSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.RftUnMaskedUser;
import com.privasia.procurehere.core.entity.RfxView;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.FilterTypes;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.ActiveEventPojo;
import com.privasia.procurehere.core.pojo.ApprovedRejectEventPojo;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.pojo.PublicEventPojo;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SearchSortFilterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RftEventDaoImpl extends GenericEventDaoImpl<RftEvent, String> implements RftEventDao {

	private static final Logger LOG = LogManager.getLogger(RftEventDaoImpl.class);

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Autowired
	UserDao userDao;

	@Autowired
	private Environment env;

	@SuppressWarnings("unchecked")
	@Override
	public RftEvent findByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r inner join fetch r.eventOwner eo left outer join fetch r.template t left outer join fetch r.deliveryAddress left outer join fetch r.participationFeeCurrency pfc left outer join fetch r.depositCurrency left outer join fetch r.baseCurrency bc left outer join fetch r.costCenter cc left outer join fetch r.industryCategory ec left outer join fetch r.suppliers sup where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RftEvent> uList = query.getResultList();
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
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("from RftTeamMember tm left outer join fetch tm.user u where tm.event.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEventApproval> getApprovalsForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select a from RftEvent r join r.approvals a left outer join fetch a.approvalUsers u where r.id =:eventId");
		query.setParameter("eventId", eventId);
		List<RftEventApproval> list = query.getResultList();
		return list;
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
	public boolean isExists(RftEvent rftEvent) {
		LOG.info("Referance No : " + rftEvent.getReferanceNumber() + " Tenant Id : " + rftEvent.getTenantId());
		StringBuilder hsql = new StringBuilder("from RftEvent re where re.referanceNumber= :referanceNumber and re.id <> :id and re.tenantId= :tenantId ");

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("referanceNumber", rftEvent.getReferanceNumber());
		query.setParameter("tenantId", rftEvent.getTenantId());
		query.setParameter("id", rftEvent.getId());

		List<RftEvent> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllDraftEventsForBuyer(String tenantId, TableDataInput input, String userId) {
		// All Drafts events for Buyer
		String sql = "SELECT * FROM ( ";
		// String sql = "";
		if (userId == null) {
			// sql += getNativeRfxAdminQuery();
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			// sql += getNativeRfxQuery();
			sql += getSimpleNativeRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.type = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like :" + cp.getData().replace(".", "") + " ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.type";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC, createdDate DESC ";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.DRAFT.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllSuspendedEventsForBuyer(String tenantId, TableDataInput input, String userId) {

		// All Suspended events for Buyer
		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getNativeRfxAdminQuery();
			// sql += getRfxEventDetails();
		} else {
			sql += getNativeRfxQueryForSuspendedEvents();
			// sql += getRfxEventDetails();
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.type = (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventName")) {
					sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("sysEventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventStart")) {
					sql += " and upper(a.eventStart) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventEnd")) {
					sql += " and upper(a.eventEnd) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventUser")) {
					sql += " and upper(a.createdBy) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("unitName")) {
					sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("referenceNumber")) {
					sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ") ";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				} // eventName referenceNumber sysEventId eventStart eventEnd type eventUser
					// unitName
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.SUSPENDED.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSuspendedEventForBuyer(String tenantId, String userId, TableDataInput input) {
		/*
		 * if (userId == null) { sql = getNativeRfxCountAdminQuery(); } else { sql = getNativeRfxDraftCountQuery();//
		 * getNativeRfxCountQuery(); } sql += " where 1=1 ";
		 */

		// All Suspended events for Buyer
		String sql = "SELECT count(a.id) FROM (";
		if (userId == null) {
			// sql += getNativeRfxAdminQuery();
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeRfxQueryForSuspendedEvents();
			// sql += getRfxEventDetails();
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = (:" + cp.getData().replace(".", "") + ") ";
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("sysEventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("referenceNumber")) {
						sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventUser")) {
						sql += " and upper(a.createdBy) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.SUSPENDED.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllClosedEventsForBuyer(String tenantId, TableDataInput input, String userId) {

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getNativeRfxAdminQuery();
			// sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeClosedRfxQuery();
			// sql += getSimpleNativeRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.type = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}
		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.CLOSED.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllCompletedEventsForBuyer(String tenantId, TableDataInput input, String userId) {

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getNativeRfxAdminQuery();
			// sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeCompletedRfxQuery();
			// sql += getSimpleNativeRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.type = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.COMPLETE.toString()));
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllCancelledEventsForBuyer(String tenantId, TableDataInput input, String userId) {

		// All Suspended events for Buyer
		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getNativeRfxAdminQuery();
			// sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeCanceldRfxQuery();
			// sql += getSimpleNativeRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
				} else if (cp.getData().equals("formOwner")) {
					sql += " and upper(a.createdBy) = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
//			if (sql.lastIndexOf(",") == sql.length() - 1) {
//				sql = sql.substring(0, sql.length() - 1);
//			}
			sql += " id DESC ";
		} else {
			sql += " order by eventEnd DESC, id DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.CANCELED.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublicEventPojo> getActivePublicEvents(Country country, Buyer buyer) {
		String hql = "SELECT distinct NEW com.privasia.procurehere.core.pojo.PublicEventPojo(e.id, e.eventName, e.referanceNumber, e.eventEnd, e.type, i, o, b, c,e.eventDescription) from RfxView e inner join e.industryCategory i inner join e.eventOwner o, Buyer b inner join b.registrationOfCountry c where b.id = e.tenantId and e.status = :status and e.eventVisibility = :eventVisibility";
		if (country != null) {
			LOG.info("searching by Country....");
			hql += " and c = :country";
		}
		if (buyer != null) {
			LOG.info("searching by Buyer....");
			hql += " and b = :buyer";
		}
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", EventStatus.ACTIVE);
		query.setParameter("eventVisibility", EventVisibilityType.PUBLIC);
		if (country != null) {
			query.setParameter("country", country);
		}
		if (buyer != null) {
			query.setParameter("buyer", buyer);
		}
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Buyer> getAllActivePublicEventBuyers() {
		String hql = "select distinct b from RfxView e , Buyer b where b.id = e.tenantId and e.status = :status and e.eventVisibility = :eventVisibility";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", EventStatus.ACTIVE);
		query.setParameter("eventVisibility", EventVisibilityType.PUBLIC);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OngoingEventPojo> getAllOngoingEventsForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId) {
		// All Ongoing events for Buyer

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getNativeRfxAdminQuery();
		} else {
			sql += getNativeOngoingRfxQuery();
		}

		sql += " AND a.EVENT_START < :eventStart ";

		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.type = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		Query query = getEntityManager().createNativeQuery(sql, "ongoingEventResult");

		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStart", new Date());
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<OngoingEventPojo> list = query.getResultList();

		if (CollectionUtil.isEmpty(list)) {
			return null;
		}
		List<String> ids = new ArrayList<String>();
		for (OngoingEventPojo obj : list) {
			// LOG.info("Got Event ID : " + obj.getId());
			ids.add(obj.getId());
			if (obj.getType() != null) {
				try {
					String className = null;
					// Find total unread messages count for each event
					switch (obj.getType()) {
					case RFA:
						className = "RfaEventMessage";
						break;
					case RFI:
						className = "RfiEventMessage";
						break;
					case RFP:
						className = "RfpEventMessage";
						break;
					case RFQ:
						className = "RfqEventMessage";
						break;
					case RFT:
						className = "RftEventMessage";
						break;
					}
					query = getEntityManager().createQuery("SELECT count(*) from " + className + " m where m.event.id = :eventId  and m.createdDate > :lastLoginTime and m.sentBySupplier = true");
					query.setParameter("eventId", obj.getId());
					query.setParameter("lastLoginTime", lastLoginTime);
					int unreadMessageCount = ((Number) query.getSingleResult()).intValue();
					obj.setUnreadMessageCount(unreadMessageCount);
				} catch (Exception e) {
					LOG.info("Error while fetching unread message Count :" + e.getMessage(), e);
				}
			}
		}

		// Find total bids received for each event along with unread bids
		query = getEntityManager().createQuery("SELECT e.id, e.supplierSubmittedTime from RfxView e where e.id in (:ids) and e.submitted = true");
		query.setParameter("ids", ids);
		List<Object[]> rows = query.getResultList();
		for (Object[] row : rows) {
			String id = ((String) row[0]);
			Date submittedTime = ((Date) row[1]);
			// Search original Obj in the list
			if (StringUtils.checkString(id).length() > 0) {
				OngoingEventPojo dummy = new OngoingEventPojo();
				dummy.setId(id);
				dummy = list.get(list.indexOf(dummy));
				dummy.setTotalBidCount(dummy.getTotalBidCount() + 1);
				if (submittedTime != null && lastLoginTime != null && submittedTime.after(lastLoginTime)) {
					dummy.setUnreadBidCount(dummy.getUnreadBidCount() + 1);
				}
			}
		}

		// Find total bids received for each event
		query = getEntityManager().createQuery("SELECT e.id, e.supplierEventReadTime, e.supplier.id from RfxView e where e.id in (:ids) ");
		query.setParameter("ids", ids);
		rows = query.getResultList();
		for (Object[] row : rows) {
			String id = ((String) row[0]);
			Date readTime = ((Date) row[1]);
			String supId = ((String) row[2]);
			// int count = ((Number) row[1]).intValue();
			// LOG.info("id : " + id + " - " + readTime);
			// Search original Obj in the list
			if (StringUtils.checkString(id).length() > 0) {
				OngoingEventPojo dummy = new OngoingEventPojo();
				dummy.setId(id);
				dummy = list.get(list.indexOf(dummy));
				if (StringUtils.checkString(supId).length() > 0) {
					dummy.setTotalSupplierCount(dummy.getTotalSupplierCount() + 1);
					if (readTime != null) {
						dummy.setReadSupplierCount(dummy.getReadSupplierCount() + 1);
					}
				}
			}
		}
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FinishedEventPojo> getAllFinishedEventsForBuyer(String tenantId, Date lastLoginTime, int days, TableDataInput input, String userId) {
		// regarding PH 69 related to finished events
		// Calendar today = Calendar.getInstance();
		// Date rangeEnd = today.getTime();
		// today.add(Calendar.DATE, -days);
		// Date rangeStart = today.getTime();

		// All Ongoing events for Buyer
		String sql = "";
		if (userId == null) {
			sql += "SELECT * FROM get_native_rfx_admin_query";
			// sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += "SELECT DISTINCT a.id,a.eventname, a.createddate,a.createdby,a.modifieddate,a.type,a.eventstart,a.eventend,a.referencenumber, a.unitname,a.syseventid,a.eventuser FROM get_native_finish_rfx_query";
			// sql += getSimpleNativeRfxQuery();
		}

		sql += " a where a.tenantid = :tenantId ";

		if(userId != null){
			sql += " and (a.envelopopener = :userId or a.leadevaluator = :userId or a.tmuserid = :userId or a.evlid = :userId or a.eventowner = :userId or a.usrid = :userId or a.cuuserid =:userId )";
		}


		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				 LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.type";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		LOG.info(">>>> SQL FINISHED DASHBOARD REPORT >>>>>>>>>> "+sql);
		Query query = getEntityManager().createNativeQuery(sql, "finishedEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
//		List<String> status = new ArrayList<String>();
//		status.add(EventStatus.FINISHED.toString());

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				 LOG.info("  >>>> INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					LOG.info("is this type?  "+cp.getData().replace(".", "")+"what type??  "+ RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
				} else {
					LOG.info("is this  NOT type?  "+cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>USER ID >>>>>>>>>>>>>>    "+userId);
		LOG.info(">>>>>>>>>>>>>>>>>>>>TENANT ID >>>>>>>>>>>>>>>> "+tenantId);
		query.setParameter("tenantId", tenantId);
//		query.setParameter("status", status);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<FinishedEventPojo> list = query.getResultList();

		if (CollectionUtil.isEmpty(list)) {
			return null;
		}

		return list;
	}

	//4105

	@Override
	@SuppressWarnings("unchecked")
	public List<DraftEventPojo> getAllFinishedEventsForBizUnit(String tenantId, Date lastLoginTime, int days, TableDataInput input, String userId,List<String> businessUnitIds) {
		LOG.info(">>>>>>>>>>>>> loaded QUERY  getAllFinishedEventsForBizUnit>>>>>>>>>>>>> ");
		// regarding PH 69 related to finished events
		// Calendar today = Calendar.getInstance();
		// Date rangeEnd = today.getTime();
		// today.add(Calendar.DATE, -days);
		// Date rangeStart = today.getTime();
		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getRfxAdminBizUnitQuery();
		} else {

			sql += getFinishedAndClosedRfxBizUnitQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.type = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}
		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}
		LOG.info("DASHBOARD NEW SQL FINISHED STATUS >>>>>>>>> "+sql);
		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.FINISHED.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}


	/*
	 * @SuppressWarnings("unchecked")
	 * @Override
	 * @Deprecated public FavouriteSupplier getFavouriteSupplierBySupplierId(String supId) { StringBuffer sb = new
	 * StringBuffer("select distinct fs from FavouriteSupplier fs left outer join fetch fs.buyer as b left outer join fetch fs.supplier as s where s.id=:sid and b.id=:bid"
	 * ); try { final Query query = getEntityManager().createQuery(sb.toString()); query.setParameter("sid", supId);
	 * query.setParameter("bid", SecurityLibrary.getLoggedInUser().getBuyer().getId()); LOG.debug("supId : " + supId +
	 * "Buyer Id :" + SecurityLibrary.getLoggedInUser().getBuyer().getId()); List<FavouriteSupplier> favList =
	 * query.getResultList(); if (CollectionUtil.isNotEmpty(favList)) { return favList.get(0); } else { return null; } }
	 * catch (NoResultException nr) { LOG.info("Error while getting user : " + nr.getMessage(), nr); return null; } }
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> findAllEventForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id : " + supplierId);
		String sql = "select e from RfxView as e left outer join fetch e.participationFeeCurrency as cu inner join fetch e.eventOwner eo inner join fetch eo.buyer as by ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		LOG.info(" SQL FOR AC : " + sql);
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.SUSPENDED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return query.getResultList();

		/*
		 * if (CollectionUtil.isNotEmpty(resultList)) { LinkedHashSet<RfxView> result = new LinkedHashSet<RfxView>();
		 * for (RfxView rfxView : resultList) { result.add(rfxView); } return new ArrayList<RfxView>(result); } else {
		 * return null; }
		 */
	}

	@Override
	@SuppressWarnings("unchecked")
	public RftEvent findEventForCqByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r left outer join fetch r.cqs eo where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RftEvent> uList = query.getResultList();
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
	public RftEvent findEventForBqByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r left outer join fetch r.eventBqs eo where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<RftEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting Bq : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public RftEvent findBySupplierEventId(String id) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r inner join fetch r.eventOwner as eo left outer join fetch r.deliveryAddress as da  left outer join fetch da.state as s left outer join fetch s.country left outer join fetch r.eventBqs as bq where r.id =:eventId order by bq.bqOrder");
			query.setParameter("eventId", id);
			@SuppressWarnings("unchecked")
			List<RftEvent> uList = query.getResultList();
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
	public Integer getCountOfEnvelopByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(ev) from RftEvent e inner join e.rftEnvelop ev  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		LOG.debug("Envelop Count  :   " + count);
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllRftEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat) {
		StringBuffer sb = new StringBuffer("SELECT e.ID as id,e.EVENT_NAME as eventName,null as auctionType,e.STATUS as eventStatus,e.EVENT_DESCRIPTION as descripiton, CASE WHEN template.TEMPLATE_STATUS='INACTIVE' THEN  1  ELSE 0 END as templateStatus, e.REFERANCE_NUMBER as referenceNumber,e.EVENT_ID as eventId,e.EVENT_START as startDate,e.EVENT_END as endDate, (SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_RFT_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories FROM PROC_RFT_EVENTS e  ");
		sb.append("LEFT OUTER JOIN PROC_USER  eventOwner on e.EVENT_OWNER = eventOwner.ID  LEFT OUTER JOIN PROC_RFX_TEMPLATE   template on e.TEMPLATE_ID = template.ID ");
		if (StringUtils.checkString(indCat).length() > 0) {
			sb.append(" INNER JOIN  PROC_RFT_EVENT_INDUS_CAT cat ON cat.EVENT_ID = e.ID ");
		}
		sb.append(" WHERE e.TENANT_ID =:tenantId ");
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			sb.append(" AND e.EVENT_OWNER= :eventOwner ");
		}
		if (StringUtils.checkString(serchVal).length() > 0) {
			sb.append(" AND (UPPER(e.EVENT_NAME ) like :searchValue or upper(e.REFERANCE_NUMBER) like :searchValue or upper(e.EVENT_ID) like :searchValue)  ");
		}
		if (StringUtils.checkString(indCat).length() > 0) {
			sb.append(" AND cat.IND_CAT_ID = :indCat ");
		}

		sb.append("order by e.CREATED_DATE desc ");
		LOG.info(sb.toString());
		final Query query = getEntityManager().createNativeQuery(sb.toString(), "copyFromPreviousEvent");
		query.setParameter("tenantId", tenantId);
		query.setFirstResult(Integer.parseInt(pageNo) * 10);
		query.setMaxResults(10);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("eventOwner", loggedInUser);
		}
		if (StringUtils.checkString(serchVal).length() > 0) {
			query.setParameter("searchValue", "%" + serchVal.toUpperCase() + "%");
		}

		if (StringUtils.checkString(indCat).length() > 0) {
			query.setParameter("indCat", indCat);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		final Query query = getEntityManager().createQuery("select ss from RftEvent e left outer join e.suppliers s inner join s.supplier ss where e.id =:eventId order by ss.companyName");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getAwardedSuppliers(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct s from RftEvent e left outer join e.awardedSuppliers s where e.id =:eventId order by s.companyName");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEvent> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String loggedInUser) {
		StringBuffer sb = new StringBuffer("select distinct e from RftEvent e left outer join fetch e.industryCategories i where e.tenantId = :tenantId ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(e.eventName) like :searchValue or upper(e.referanceNumber) like :searchValue or upper(e.eventId) like :searchValue )");
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
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			query.setParameter("industryCategory", industryCategory);
		}
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("loggedInUser", loggedInUser);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftTeamMember getRftTeamMemberByUserIdAndEventId(String eventId, String userId) {
		final Query query = getEntityManager().createQuery("from RftTeamMember r where r.event.id =:eventId and r.user.id= :userId");
		query.setParameter("eventId", eventId);
		query.setParameter("userId", userId);
		List<RftTeamMember> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public long findTotalDraftEventForBuyer(String tenantId, String userId, TableDataInput input) {
		// All Drafts events for Buyer
		String sql = "SELECT count(a.id) FROM ( ";
		// String sql = "";
		if (userId == null) {
			// sql += getNativeRfxAdminQuery();
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			// sql += getNativeRfxQuery();
			sql += getSimpleNativeRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like :" + cp.getData().replace(".", "") + " ";
					}
				}
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		// query.setParameter("eventStart", new Date());
		query.setParameter("status", Arrays.asList(EventStatus.DRAFT.toString()));

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalOngoingEventForBuyer(String tenantId, String userId, TableDataInput input) {

		// All Ongoing events for Buyer
		String sql = "SELECT count(a.id) FROM ( ";
		if (userId == null) {
			sql += getNativeRfxAdminCountQuery();
		} else {
			// sql += getNativeOngoingRfxQuery();
			sql += getNativeOngoingRfxCountQuery();
		}
		sql += " ) a where a.eventStart < :eventStart ";

		// Add on search filter conditions

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("sysEventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("referenceNumber")) {
						sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		Query query = getEntityManager().createNativeQuery(sql);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStart", new Date());
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString()));
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalFinishedEventForBuyer(String tenantId, String userId, int days, TableDataInput input) {
		// All Ongoing events for Buyer
		// regarding PH 69 related to finished events
		// Calendar today = Calendar.getInstance();
		// Date rangeEnd = today.getTime();
		// today.add(Calendar.DATE, -days);
		// Date rangeStart = today.getTime();

		String sql = "";
		if (userId == null) {
			// sql += getNativeRfxAdminQuery();
			sql += "SELECT count(*) FROM get_simple_admin_native_rfx_query";
		} else {
			sql += "select count(a.id) from ( SELECT DISTINCT a.id,a.eventname, a.createddate,a.createdby,a.modifieddate,a.type,a.eventstart,a.eventend,a.referencenumber, a.unitname,a.syseventid,a.eventuser FROM get_native_finish_rfx_query";
			// sql += getSimpleNativeRfxQuery();
		}
		sql += " a where a.tenantid = :tenantId ";

		if(userId != null){
			sql += " and (a.envelopopener = :userId or a.leadevaluator = :userId or a.tmuserid = :userId or a.evlid = :userId or a.eventowner = :userId or a.usrid = :userId or a.cuuserid =:userId )";
		}

		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = (:" + cp.getData().replace(".", "") + ") ";
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("sysEventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("referenceNumber")) {
						sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventUser")) {
						sql += " and upper(a.createdBy) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		if(userId != null){
			sql += " ) a";
		}
		final Query query = getEntityManager().createNativeQuery(sql);

		if (userId != null) {
			query.setParameter("userId", userId);
		}

//		List<String> status = new ArrayList<String>();
//		status.add(EventStatus.FINISHED.toString());
		query.setParameter("tenantId", tenantId);
//		query.setParameter("status", status);

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}

		// if (days != -1) {
		// query.setParameter("rangeStart", rangeStart);
		// query.setParameter("rangeEnd", rangeEnd);
		// }
		return ((Number) query.getSingleResult()).longValue();
	}

	//4105

	@Override
	public long findTotalFinishedEventForBizUnit(String tenantId, String userId, int days, TableDataInput input,List<String> businessUnitIds) {

		LOG.info("loaded NEW TOTAL FINISHED EVENT COUNT");

		String sql = "SELECT count(a.id) FROM (";
		if (userId == null) {
			sql += getSimpleAdminBizUnitRfxQuery();
		} else {

			sql += getFinishedAndClosedRfxBizUnitQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";


		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("formOwner")) {
						sql += " and upper(a.createdBy) = :" + cp.getData().replace(".", "");
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.FINISHED.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
							cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}

		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActiveEventPojo> getAllActiveEventsForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId) {

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeRfxSecduleQuery();
		}
		sql += " ) a where 1 = 1 ";
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and upper(a.type) = (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventName")) {
					sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("sysEventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("referenceNumber")) {
					sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("unitName")) {
					sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("eventUser")) {
					sql += " and upper(a.eventUser) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		sql += " AND a.eventStart > :eventStart   ";

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.type";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by a.eventStart ";
		}

		// LOG.info("SQL :: " + sql);
		final Query query = getEntityManager().createNativeQuery(sql, "activeEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString(), EventStatus.APPROVED.toString()));
		query.setParameter("eventStart", new Date());
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		return query.getResultList();

	}

	@Override
	public long findTotalActiveEventForBuyer(String tenantId, String userId, TableDataInput input) {

		String sql = "SELECT COUNT(*) FROM (";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeRfxSecduleQuery();
		}
		sql += " ) a where 1 = 1 ";
		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = (:" + cp.getData().replace(".", "") + ") ";
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("sysEventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("referenceNumber")) {
						sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventUser")) {
						sql += " and upper(a.eventUser) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		sql += " and  a.eventStart > :eventStart ";
		// LOG.info("SQL :: " + sql);

		final Query query = getEntityManager().createNativeQuery(sql);

		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString(), EventStatus.APPROVED.toString()));
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStart", new Date());
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PendingEventPojo> getAllPendingEventsForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId) {

		// All Drafts events for Buyer
		String sql = "SELECT * FROM ( ";
		// String sql = "";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativePendingRfxQuery();
		}
		sql += " ) a where 1 = 1";

		// sql += " order by a.EVENT_START";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.TYPE = :" + cp.getData().replace(".", "");
				} else if (cp.getData().equals("eventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "type";
				} else if (orderColumn.equals("eventId")) {
					orderColumn = "sysEventId";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalPendingEventForBuyer(String tenantId, String userId, TableDataInput input) {

		// All Drafts events for Buyer
		String sql = "SELECT count(a.id) FROM ( ";
		// String sql = "";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativePendingRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		// sql += " order by a.EVENT_START";

		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}

		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEventApproval> getAllApprovalsForEvent(String id) {
		final Query query = getEntityManager().createQuery("select a from RftEvent e join e.approvals a where e.id =:id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {
		// LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();
		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		RftEvent event = getPlainEventById(eventId);

		/*
		 * if (event.getUnMaskedUser() != null && event.getUnMaskedUser().getId().equals(userId) && EventStatus.COMPLETE
		 * == event.getStatus() && Boolean.FALSE == event.getDisableMasking()) { permissions.setUnMaskUser(true); }
		 */

		for (RftUnMaskedUser rfiUnMaskedUser : event.getUnMaskedUsers()) {
			if (rfiUnMaskedUser.getUser() != null && rfiUnMaskedUser.getUser().getId().equals(userId) && EventStatus.COMPLETE == event.getStatus() && Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
				permissions.setUnMaskUser(true);
			}
		}

		for (RftEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId) && EventStatus.CLOSED == event.getStatus() && Boolean.FALSE == user.getConcluded()) {
				permissions.setEvaluationConclusionUser(true);
			}
		}

		boolean isAllUserConcludedDone = false;
		if (event.getEnableEvaluationConclusionUsers()) {
			isAllUserConcludedDone = true;
		}
		for (RftEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId)) {
				permissions.setConclusionUser(true);
			}
			if (user.getConcluded() == null || Boolean.FALSE == user.getConcluded()) {
				isAllUserConcludedDone = false;
			}
		}
		permissions.setAllUserConcludedPermatury(isAllUserConcludedDone);

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
		List<RftEventApproval> approvals = event.getApprovals(); // getApprovalsForEvent(eventId);
		for (RftEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RftApprovalUser> users = approval.getApprovalUsers();
				for (RftApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive() && ApprovalStatus.PENDING == user.getApprovalStatus()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}

		// Suspension Approver
		List<RftEventSuspensionApproval> suspApprovals = event.getSuspensionApprovals();
		for (RftEventSuspensionApproval approval : suspApprovals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RftSuspensionApprovalUser> users = approval.getApprovalUsers();
				for (RftSuspensionApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive() && ApprovalStatus.PENDING == user.getApprovalStatus()) {
							permissions.setActiveSuspensionApproval(true);
							break;
						}
					}
				}
			}
		}

		// Award Approver
		List<RftEventAwardApproval> awardApprovals = event.getAwardApprovals();
		for (RftEventAwardApproval approval : awardApprovals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RftAwardApprovalUser> users = approval.getApprovalUsers();
				for (RftAwardApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						permissions.setAwardApprover(true);
						if (approval.isActive()) {
							permissions.setActiveAwardApproval(true);
							break;
						}
					}
				}
			}
		}

		// Envelop Opener and Evaluator
		List<RftEnvelop> envelopes = rftEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RftEnvelop envelop : envelopes) {
				List<RftEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RftEvaluatorUser user : evaluators) {
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
				List<RftEnvelopeOpenerUser> openerUser = envelop.getOpenerUsers();
				for (RftEnvelopeOpenerUser user : openerUser) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setOpener(true);
					}
				}
			}
		}
		return permissions;
	}

	@Override
	public EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId) {
		// LOG.info("userId :" + userId + " eventId: " + eventId);
		EventPermissions permissions = new EventPermissions();
		User loggedInUser = userDao.findById(userId);
		if (UserType.APPROVER_USER == loggedInUser.getUserType()) {
			LOG.info("*************Checking userType");
			permissions.setApproverUser(true);
		}

		// Event Owner
		RftEvent event = getPlainEventById(eventId);
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
		List<RftEventApproval> approvals = event.getApprovals(); // getApprovalsForEvent(eventId);
		for (RftEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RftApprovalUser> users = approval.getApprovalUsers();
				for (RftApprovalUser user : users) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setApprover(true);
						if (approval.isActive() && ApprovalStatus.PENDING == user.getApprovalStatus()) {
							permissions.setActiveApproval(true);
							break;
						}
					}
				}
			}
		}

		// Envelop Opener and Evaluator
		List<RftEnvelop> envelopes = rftEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RftEnvelop envelop : envelopes) {
				if (!envelop.getId().equals(envelopeId)) {
					continue;
				}
				List<RftEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RftEvaluatorUser user : evaluators) {
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
				List<RftEnvelopeOpenerUser> openerUser = envelop.getOpenerUsers();
				for (RftEnvelopeOpenerUser user : openerUser) {
					if (user.getUser().getId().equals(userId)) {
						permissions.setOpener(true);
					}
				}
			}
		}
		return permissions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> findAllEventForSearchvalue(String tenantId, String searchValue, String status, String type, Date startDate, Date endDate, String userId) {
		LOG.info("Supplier Id : " + tenantId + " status : " + status + " Type : " + type + " Daterange : " + startDate + " EndDate : " + endDate + " Search Value : " + searchValue);

		List<EventStatus> statusList = new ArrayList<EventStatus>();
		if (StringUtils.checkString(status).length() == 0) {
			statusList.add(EventStatus.ACTIVE);
			statusList.add(EventStatus.SUSPENDED);
			statusList.add(EventStatus.CLOSED);
		} else {
			String[] statusArray = status.split(",");
			if (statusArray != null && statusArray.length > 0) {
				for (String ss : statusArray) {
					statusList.add(EventStatus.valueOf(ss));
				}
			}
		}

		// If user only selected REJECTED then add ACTIVE/SUSPEND/CLOSED by default
		if (statusList.contains(EventStatus.REJECTED) && statusList.size() == 1) {
			statusList.add(EventStatus.ACTIVE);
			statusList.add(EventStatus.SUSPENDED);
			statusList.add(EventStatus.CLOSED);
		}

		if (statusList.contains(EventStatus.CLOSED)) {
			statusList.add(EventStatus.COMPLETE);
			statusList.add(EventStatus.FINISHED);
		}

		StringBuffer hql = new StringBuffer("from RfxView as e left outer join fetch e.participationFeeCurrency as cu inner join fetch e.eventOwner eo inner join fetch eo.buyer as by  where e.status in (:eventStatus) and e.supplier.id = :id and ((upper(e.eventId) like :search) or (upper(e.referanceNumber) like :search) or (upper(e.eventName) like :search) or (upper(e.eventDescription) like :search))");

		List<RfxTypes> rfxTypes = new ArrayList<RfxTypes>();
		if (StringUtils.checkString(type).length() > 0) {
			hql.append(" and e.type in (:type) ");
			String[] types = type.split(",");
			if (types != null && types.length > 0) {
				for (String ty : types) {
					rfxTypes.add(RfxTypes.valueOf(ty));
				}
			}
		}
		if (startDate != null && endDate != null) {
			hql.append("and e.eventPublishDate between :startDate and :endDate");
		}

		if (statusList.contains(EventStatus.REJECTED)) {
			hql.append(" and e.submissionStatus = :rej");
		}

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventStatus", statusList);
		query.setParameter("id", tenantId);
		query.setParameter("search", "%" + searchValue.toUpperCase() + "%");
		if (statusList.contains(EventStatus.REJECTED)) {
			query.setParameter("rej", SubmissionStatusType.REJECTED);
		}
		if (StringUtils.checkString(type).length() > 0) {
			query.setParameter("type", rfxTypes);
		}
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		LOG.info(" Query " + hql);

		// // ===================================
		// String sql = null;
		// if (userId == null) {
		// sql = getNativeRfxQueryForSupplierAdminGlobalSearch();
		// } else {
		// sql = getNativeRfxQueryForSupplierGlobalSearch();
		// }
		// Query query = null;
		// if (startDate != null && endDate != null) {
		// query = getEntityManager().createNativeQuery(sql + " AND (a.EVENT_START >= :eventStart AND e.EVENT_END <=
		// :eventEnd)", "globalSearchResult");
		// } else {
		// query = getEntityManager().createNativeQuery(sql, "globalSearchResult");
		// }
		//
		// if (userId != null) {
		// query.setParameter("userId", userId);
		// }
		// query.setParameter("tenantId", tenantId);
		// query.setParameter("status", statusList);
		// query.setParameter("searchVal", "%" + searchValue.toUpperCase() + "%");
		// if (startDate != null && endDate != null) {
		// query.setParameter("eventStart", startDate);
		// query.setParameter("eventEnd", endDate);
		// }
		// List<GlobalSearchPojo> list = query.getResultList();
		// LOG.info("List : Size : " + list.size());

		return query.getResultList();
	}

	@Override
	public RftEvent getPlainEventById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RftEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@Override
	public RftEvent getPlainEventWithOwnerById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r left outer join fetch r.eventOwner ew where r.id =:eventId");
			query.setParameter("eventId", eventId);
			return (RftEvent) query.getSingleResult();
		} catch (NoResultException nr) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEvent> getAllRftEventByLoginId(String loginId) {

		String sql = "select re from RftEvent re inner join re.createdBy cr  where cr.id =:loginId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("loginId", loginId);
		return query.getResultList();
	}

	@Override
	public long findTotalCancelledEventForBuyer(String tenantId, String userId, TableDataInput input) {
		// All Suspended events for Buyer
		String sql = "SELECT Count(a.id) FROM (";
		if (userId == null) {
			// sql += getNativeRfxAdminQuery();
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeCanceldRfxQuery();
			// sql += getSimpleNativeRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.CANCELED.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalCompletedEventForBuyer(String tenantId, String userId, TableDataInput input) {
		String sql = "SELECT count(a.id) FROM (";
		if (userId == null) {
			// sql += getNativeRfxAdminQuery();
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeCompletedRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.COMPLETE.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalClosedEventForBuyer(String tenantId, String userId, TableDataInput input) {

		String sql = "SELECT count(a.id) FROM (";
		if (userId == null) {
			// sql += getNativeRfxAdminQuery();
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getNativeClosedRfxQuery();
			// sql += getSimpleNativeRfxQuery();
		}
		sql += " ) a where 1 = 1 ";

		if (input != null) {

			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("formOwner")) {
						sql += " and upper(a.createdBy) = :" + cp.getData().replace(".", "");
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.CLOSED.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalMyPendingApprovals(String tenantId, String userId) {
		String sql = getMyPendingCountQuery();
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList("PENDING", "SUBMITTED", "COMPLETE")); // Need to ask nitin
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalEventMyPendingApprovals(String tenantId, String userId) {
		String sql = getMyEventPendingCountQuery();
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalPrMyPendingApprovals(String tenantId, String userId) {
		String sql = getMyPrPendingCountQuery();
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalRfsMyPendingApprovals(String tenantId, String userId) {
		String sql = getMyRfsPendingCountQuery();
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalSupplierFormMyPendingApprovals(String tenantId, String userId) {
		String sql = getMySupplierFormPendingCountQuery();
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		query.setParameter("userId", userId);
		query.setParameter("status", SupplierFormSubmitionStatus.SUBMITTED.toString());
		long count = ((Number) query.getSingleResult()).longValue();
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PendingEventPojo> findMyPendingRfxApprovals(String tenantId, String userId, TableDataInput input) {

		String sql = "SELECT * FROM (";
		sql += getMyPendingRfxQuery();
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and \"type\"= :" + cp.getData().replace(".", "");
				} else if (cp.getData().equals("eventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "type";
				} else if (orderColumn.equals("eventId")) {
					orderColumn = "sysEventId";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input.getLength());
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		List<PendingEventPojo> list = query.getResultList();
		return list;
	}

	@Override
	public long findTotalMyPendingRfxApprovals(String tenantId, String userId, TableDataInput input) {
		String sql = getMyTotalPendingRfxQuery();
		sql += " ) a where 1 = 1 ";
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						sql += " and \"type\"= :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}

		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PendingEventPojo> findMyPendingPrApprovals(String tenantId, String userId, TableDataInput input) {

		String sql = "SELECT * FROM ( ";
		sql += getMyPendingPrQuery();
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("eventName")) {
					sql += " and a.eventName like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("eventId")) {
					sql += " and a.eventId like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("createdBy.name")) {
					sql += " and a.createdBy like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("prUserName")) {
					sql += " and a.createdBy like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("grandTotal")) {
					sql += " and a.grandTotal like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("unitName")) {
					sql += " and a.unitName like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and a." + cp.getData().replace(".", "") + " like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				} else if (orderColumn.equals("createdBy.name")) {
					orderColumn = "a.createdBy";
				} else if (orderColumn.equals("eventName")) {
					orderColumn = "a.eventName";
				} else if (orderColumn.equals("eventId")) {
					orderColumn = "a.eventId";
				} else if (orderColumn.equals("prUserName")) {
					orderColumn = "a.prUserName";
				} else if (orderColumn.equals("grandTotal")) {
					orderColumn = "a.grandTotal";
				} else if (orderColumn.equals("unitName")) {
					orderColumn = "a.unitName";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by a.urgentPr DESC, a.createdDate DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingPrResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("erptransferrd", 0);
		query.setParameter("status", Arrays.asList(PrStatus.PENDING.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<PendingEventPojo> list = query.getResultList();
		return list;
	}

	@Override
	public long findTotalMyPendingPrApprovals(String tenantId, String userId, TableDataInput input) {

		// String sql = getMyPendingPrCountQuery();

		// String sql = "";
		String sql = getMyPendingPrCountQuery();
		sql += " ) a where 1 = 1 ";
		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("eventName")) {
						sql += " and a.eventName like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventId")) {
						sql += " and a.eventId like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("createdBy.name")) {
						sql += " and a.createdBy like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("prUserName")) {
						sql += " and a.createdBy like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("grandTotal")) {
						sql += " and a.grandTotal like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and a.unitName like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and a." + cp.getData().replace(".", "") + " like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("erptransferrd", 0);
		query.setParameter("status", Arrays.asList(PrStatus.PENDING.toString()));

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalMyPendingEvaluation(String tenantId, String userId, TableDataInput input) {

		// String sql = getMyPendingEvaluationCountQuery();//

		String sql = "select count(a.id) from (";
//		sql += getMyPendingEvaluationRfxQuery();
//		sql += " ) a where 1=1 ";
		sql += "SELECT DISTINCT a.id,\n" +
				"    a.eventname,\n" +
				"    a.createddate,\n" +
				"    a.createdby,\n" +
				"    a.modifieddate,\n" +
				"    a.type,\n" +
				"    a.eventstart,\n" +
				"    a.eventend,\n" +
				"    a.referencenumber,\n" +
				"    a.unitname,\n" +
				"    a.syseventid,\n" +
				"    a.eventuser FROM closed_event_list a where a.tenantid = :tenantId and ((a.openuserid = :userId and a.openstatus = 0 and a.envelopstatus = 0 and a.enveloptype = 'Closed') or (a.envelopeuserid = :userId and a.enevaluationstatus = 'PENDING' and (a.envelopstatus = 1 or a.enveloptype = 'Open')) or (a.evaluatoruserid = :userId and a.evevaluationstatus = 'PENDING' and (a.envelopstatus = 1 or a.enveloptype = 'Open')) or (a.conuserid = :userId and a.isconcluded = 0)) ";
		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and \"type\"= :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		sql += " ) a";
		LOG.info(" COUNT EVALUATION PENDING");
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
//		query.setParameter("status", Arrays.asList(EventStatus.CLOSED.toString()));
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalMyPendingEvaluationBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds) {

		// String sql = getMyPendingEvaluationCountQuery();//

		String sql = "select count(a.id) from (";
//		sql += getMyPendingEvaluationRfxQuery();
//		sql += " ) a where 1=1 ";
		sql += "SELECT DISTINCT a.id,\n" +
				"    a.eventname,\n" +
				"    a.createddate,\n" +
				"    a.createdby,\n" +
				"    a.modifieddate,\n" +
				"    a.type,\n" +
				"    a.eventstart,\n" +
				"    a.eventend,\n" +
				"    a.referencenumber,\n" +
				"    a.unitname,\n" +
				"    a.syseventid,\n" +
				"    a.eventuser FROM closed_event_list a where a.tenantid = :tenantId and ((a.openuserid = :userId and a.openstatus = 0 and a.envelopstatus = 0 and a.enveloptype = 'Closed') or (a.envelopeuserid = :userId and a.enevaluationstatus = 'PENDING' and (a.envelopstatus = 1 or a.enveloptype = 'Open')) or (a.evaluatoruserid = :userId and a.evevaluationstatus = 'PENDING' and (a.envelopstatus = 1 or a.enveloptype = 'Open')) or (a.conuserid = :userId and a.isconcluded = 0)) ";
		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and \"type\"= :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		sql += " ) a";
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
//		query.setParameter("status", Arrays.asList(EventStatus.CLOSED.toString()));
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PendingEventPojo> findMyPendingEvaluation(String tenantId, String userId, TableDataInput input) {
//		System.out.println(tenantId + "-" + userId);
//		String sql = "SELECT * FROM (";
//		sql += getMyPendingEvaluationRfxQuery();
//		sql += " ) a where 1 = 1 ";
        String sql = "SELECT DISTINCT a.id,\n" +
				"    a.eventname,\n" +
				"    a.createddate,\n" +
				"    a.createdby,\n" +
				"    a.modifieddate,\n" +
				"    a.type,\n" +
				"    a.eventstart,\n" +
				"    a.eventend,\n" +
				"    a.referencenumber,\n" +
				"    a.unitname,\n" +
				"    a.syseventid,\n" +
				"    a.eventuser FROM closed_event_list a where a.tenantid = :tenantId and ((a.openuserid = :userId and a.openstatus = 0 and a.envelopstatus = 0 and a.enveloptype = 'Closed') or (a.envelopeuserid = :userId and a.enevaluationstatus = 'PENDING' and (a.envelopstatus = 1 or a.enveloptype = 'Open')) or (a.evaluatoruserid = :userId and a.evevaluationstatus = 'PENDING' and (a.envelopstatus = 1 or a.enveloptype = 'Open')) or (a.conuserid = :userId and a.isconcluded = 0)) ";
		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and \"type\"= :" + cp.getData().replace(".", "");
				} else if (cp.getData().equals("eventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "type";
				} else if (orderColumn.equals("eventId")) {
					orderColumn = "sysEventId";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}
		final Query query = getEntityManager().createNativeQuery(sql, "pendingEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
//		query.setParameter("status", Arrays.asList(EventStatus.CLOSED.toString()));
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<PendingEventPojo> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getEventSuppliersId(String id) {
		Query query = getEntityManager().createQuery("select s.supplier.id from RftEvent e left outer join e.suppliers s where e.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EventSupplierPojo> getEventSuppliersAndTimeZone(String id) {
		Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.EventSupplierPojo(s.supplier.id,s.supplier.companyName, ss.timeZone.timeZone) from RftEventSupplier s inner join s.rfxEvent e, SupplierSettings ss where ss.supplier.id = s.supplier.id and  e.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftTeamMember> getBuyerTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("from RftTeamMember tm where tm.event.id = :eventId").setParameter("eventId", eventId);
		return (List<RftTeamMember>) query.getResultList();
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(u.id, u.name, u.communicationEmail, u.emailNotifications, u.tenantId) from RftEvent e inner join e.eventOwner u where e.id = :eventId");
		query.setParameter("eventId", eventId);
		return (User) query.getSingleResult();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlag(String eventId) {
		Query query = getEntityManager().createQuery("update RftEvent e set e.startMessageSent = true where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUserBuyerTeamMemberByEventId(String eventId) {
		Query query = getEntityManager().createQuery("select new User(tm.user.id, tm.user.name, tm.user.communicationEmail, tm.user.emailNotifications, tm.user.tenantId) from RftTeamMember tm where tm.event.id = :eventId").setParameter("eventId", eventId);
		return (List<User>) query.getResultList();
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		StringBuilder hql = new StringBuilder("select count(e) from RftEvent e where e.template.id =:templateId");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("templateId", templateId);
		return ((Number) query.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.EventTeamMember(tm.id, tm.teamMemberType, u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId, u.deleted)from RftTeamMember tm left outer join tm.user u where tm.event.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventTeamMember> list = query.getResultList();
		return list;
	}

	@Override
	public boolean checkTemplateStatusForEvent(String eventId, String eventType) {
		Query query = null;
		switch (RfxTypes.valueOf(eventType)) {
		case RFA:
			query = getEntityManager().createQuery("select count(e) from RfaEvent e left outer join e.template et where et is not null and et.status= :status  and e.id =:eventId");
			break;
		case RFI:
			query = getEntityManager().createQuery("select count(e) from RfiEvent e left outer join e.template et where et is not null and et.status= :status  and e.id =:eventId");
			break;
		case RFP:
			query = getEntityManager().createQuery("select count(e) from RfpEvent e left outer join e.template et where et is not null and et.status= :status  and e.id =:eventId");
			break;
		case RFQ:
			query = getEntityManager().createQuery("select count(e) from RfqEvent e left outer join e.template et where et is not null and et.status= :status  and e.id =:eventId");
			break;
		case RFT:
			query = getEntityManager().createQuery("select count(e) from RftEvent e left outer join e.template et where et is not null and et.status= :status  and e.id =:eventId");
			break;
		default:
			break;
		}
		query.setParameter("status", Status.INACTIVE);
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovedRejectEventPojo> findMyAprrovedRejectList(String tenantId, String userId, SearchSortFilterPojo search) {
		String sql = getMyApprovedRejectRfxQuery();
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			sql += " and ( (UPPER(a.referenceNumber) like :search) or (UPPER(a.eventName) like :search) or (UPPER(puser.USER_NAME) like :search)) ";
		}
		if (search != null && StringUtils.checkString(search.getCreator()).length() > 0) {
			sql += " and UPPER(puser.USER_NAME) like :creator ";
		}
		if (search != null && StringUtils.checkString(search.getSupplierName()).length() > 0) {
			sql += " and (UPPER(a.openSupplier) like :supplierName or UPPER(a.mySupplierName) like :supplierName) ";
		}
		if (search != null && StringUtils.checkString(search.getUnitName()).length() > 0) {
			sql += " and UPPER(a.unitName) like :unitName ";
		}
		if (search != null && StringUtils.checkString(search.getStatus()).length() > 0) {
			sql += " and UPPER(a.status) = :status ";
		}

		if (search != null && search.getType() != null) {
			LOG.info(" Type : " + search.getType().toString());
			if (search.getType() == FilterTypes.ALL_RFX) {
				sql += " and a.EVENT_TYPE in (:type) ";
			} else if (search.getType() == FilterTypes.RFA) {
				sql += " and a.EVENT_TYPE = :type ";
			} else {
				sql += " and a.AUCTION_TYPE = :type ";
			}
		}

		if (search != null && search.getActionType() != null) {
			LOG.info("Action Type : " + search.getActionType().toString());
			sql += " and a.actionType = :actionType ";
		}

		if (search != null && StringUtils.checkString(search.getSortValue()).length() > 0 && StringUtils.checkString(search.getOrder()).length() > 0) {
			sql += " order by a." + search.getSortValue() + " " + search.getOrder();
		} else {
			sql += " order by a.actionDate DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "approvedRejectResult");
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			query.setParameter("search", "%" + search.getSearchValue().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getCreator()).length() > 0) {
			query.setParameter("creator", "%" + search.getCreator().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getSupplierName()).length() > 0) {
			query.setParameter("supplierName", "%" + search.getSupplierName().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getUnitName()).length() > 0) {
			query.setParameter("unitName", "%" + search.getUnitName().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getStatus()).length() > 0) {
			query.setParameter("status", search.getStatus().toUpperCase());
		}

		if (search != null && search.getType() != null) {
			if (search.getType() == FilterTypes.ALL_RFX) {
				query.setParameter("type", Arrays.asList(FilterTypes.RFT.name(), FilterTypes.RFP.name(), FilterTypes.RFQ.name(), FilterTypes.RFI.name()));
			} else {
				query.setParameter("type", search.getType().toString());
			}
		}

		if (search != null && search.getActionType() != null) {
			LOG.info("Action Type : " + search.getActionType().toString());
			query.setParameter("actionType", search.getActionType().toString());
		}

		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		if (search != null && search.getStart() != null && search.getLength() != null) {
			query.setFirstResult(search.getStart());
			query.setMaxResults(search.getLength());
		}
		// LOG.info("sql :" + sql);
		List<ApprovedRejectEventPojo> list = query.getResultList();
		LOG.info("Sixe of List " + list.size());
		for (ApprovedRejectEventPojo approvedRejectEventPojo : list) {
			LOG.info(approvedRejectEventPojo.getIsApproved());
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PendingEventPojo> findMyToDoList(String tenantId, String userId, SearchSortFilterPojo search) {
		String sql = getMyToDoList();
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			sql += "where ( (UPPER(a.eventName) like :search) or (UPPER(a.referenceNumber) like :search) or (UPPER(a.creatorName) like :search)) ";
		}
		sql += " order by a.urgentEvent DESC, a.edate DESC ";

		// LOG.info("My Pending Pr sql : " + sql);
		final Query query = getEntityManager().createNativeQuery(sql, "myToDoResult");
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			query.setParameter("search", "%" + search.getSearchValue().toUpperCase() + "%");
		}
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(PrStatus.PENDING.toString(), PrStatus.CLOSED.toString()));
		if (search != null && search.getStart() != null && search.getLength() != null) {
			query.setFirstResult(search.getStart());
			query.setMaxResults(search.getLength());
		}
		List<PendingEventPojo> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PendingEventPojo> findMyToDoListForSupplier(String tenantId, String userId, SearchSortFilterPojo search) {
		final Query query = constructSupplierMobileToDoList(tenantId, userId, search);
		return query.getResultList();
	}

	private Query constructSupplierMobileToDoList(String tenantId, String userId, SearchSortFilterPojo search) {
		String hql = "";

		hql += "select distinct NEW com.privasia.procurehere.core.pojo.PendingEventPojo(e.id , e.eventId , e.eventName, e.createdDate, e.eventStart, e.eventEnd, e.referanceNumber , e.status , e.type, e.submissionStatus, e.eventOwner, buy.companyName )";
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join eo.buyer as buy";
		hql += " where e.status in (:status) and e.supplier.id = :id and e.submissionStatus in (:submissionStatus)";
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			hql += " and ( (UPPER(e.eventName) like :search) or (UPPER(e.referanceNumber) like :search) or (UPPER(eo.name) like :search)) ";
		}
		// hql += " order by DECODE(e.urgentEvent, null , 0, e.urgentEvent) DESC, e.edate DESC";

		LOG.info("==================>" + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			query.setParameter("search", "%" + search.getSearchValue().toUpperCase() + "%");
		}
		if (search != null && search.getStart() != null && search.getLength() != null) {
			query.setFirstResult(search.getStart());
			query.setMaxResults(search.getLength());
		}
		query.setParameter("id", tenantId);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		// list.add(EventStatus.CLOSED);
		// list.add(EventStatus.FINISHED);
		// list.add(EventStatus.COMPLETE);
		// list.add(EventStatus.SUSPENDED);

		List<SubmissionStatusType> submissionStatus = new ArrayList<SubmissionStatusType>();
		submissionStatus.add(SubmissionStatusType.ACCEPTED);
		submissionStatus.add(SubmissionStatusType.INVITED);
		submissionStatus.add(SubmissionStatusType.COMPLETED);
		submissionStatus.add(SubmissionStatusType.REJECTED);
		query.setParameter("submissionStatus", submissionStatus);
		query.setParameter("status", list);

		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEvent getMobileEventDetails(String id) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r inner join fetch r.eventOwner eo left outer join fetch r.baseCurrency bc left outer join fetch r.eventBqs where r.id =:eventId");
			query.setParameter("eventId", id);
			List<RftEvent> uList = query.getResultList();
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

	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovedRejectEventPojo> findMyEventList(String tenantId, String userId, SearchSortFilterPojo search) {
		String sql = getMyEvent();
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			sql += " and ( (UPPER(a.referenceNumber) like :search) or (UPPER(a.eventName) like :search) or (UPPER(puser.USER_NAME) like :search)) ";
		}
		if (search != null && StringUtils.checkString(search.getCreator()).length() > 0) {
			sql += " and UPPER(puser.USER_NAME) like :creator ";
		}
		if (search != null && StringUtils.checkString(search.getSupplierName()).length() > 0) {
			sql += " and (UPPER(a.openSupplier) like :supplierName or UPPER(a.mySupplierName) like :supplierName) ";
		}
		if (search != null && StringUtils.checkString(search.getUnitName()).length() > 0) {
			sql += " and UPPER(a.unitName) like :unitName ";
		}
		if (search != null && StringUtils.checkString(search.getStatus()).length() > 0) {
			sql += " and UPPER(a.status) = :status ";
		}

		if (search != null && search.getType() != null) {
			LOG.info(" Type : " + search.getType().toString());
			if (search.getType() == FilterTypes.ALL_RFX) {
				sql += " and a.EVENT_TYPE in (:type) ";
			} else if (search.getType() == FilterTypes.RFA) {
				sql += " and a.EVENT_TYPE = :type ";
			} else {
				sql += " and a.AUCTION_TYPE = :type ";
			}
		}
		if (search != null && StringUtils.checkString(search.getSortValue()).length() > 0 && StringUtils.checkString(search.getOrder()).length() > 0) {
			sql += " order by a." + search.getSortValue() + " " + search.getOrder();
		} else {
			sql += " order by a.createdDate DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "myEventResult");
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			query.setParameter("search", "%" + search.getSearchValue().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getCreator()).length() > 0) {
			query.setParameter("creator", "%" + search.getCreator().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getSupplierName()).length() > 0) {
			query.setParameter("supplierName", "%" + search.getSupplierName().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getUnitName()).length() > 0) {
			query.setParameter("unitName", "%" + search.getUnitName().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getStatus()).length() > 0) {
			query.setParameter("status", search.getStatus().toUpperCase());
		}

		if (search != null && search.getType() != null) {
			if (search.getType() == FilterTypes.ALL_RFX) {
				query.setParameter("type", Arrays.asList(FilterTypes.RFT.name(), FilterTypes.RFP.name(), FilterTypes.RFQ.name(), FilterTypes.RFI.name()));
			} else {
				query.setParameter("type", search.getType().toString());
			}
		}
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		if (search != null && search.getStart() != null && search.getLength() != null) {
			query.setFirstResult(search.getStart());
			query.setMaxResults(search.getLength());
		}
		List<ApprovedRejectEventPojo> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ApprovedRejectEventPojo> findMyAllEventListForSupplier(String tenantId, String userId, SearchSortFilterPojo search) {
		Query query = constructSupplierMobileAllEventList(tenantId, userId, search);
		return query.getResultList();
	}

	private Query constructSupplierMobileAllEventList(String tenantId, String userId, SearchSortFilterPojo search) {

		boolean isRfaSubTypeFilter = false;
		if (search.getType() == FilterTypes.FORWARD_ENGISH || search.getType() == FilterTypes.REVERSE_ENGISH || search.getType() == FilterTypes.FORWARD_SEALED_BID || search.getType() == FilterTypes.REVERSE_SEALED_BID || search.getType() == FilterTypes.FORWARD_DUTCH || search.getType() == FilterTypes.REVERSE_DUTCH) {
			isRfaSubTypeFilter = true;
		}

		String hql = "";
		hql += "select distinct NEW com.privasia.procurehere.core.pojo.ApprovedRejectEventPojo(e.id , e.eventId , e.eventName, e.eventStart, e.eventEnd, e.referanceNumber , e.status , e.type, e.submissionStatus, e.eventOwner, buy.companyName ,e.createdDate)";
		hql += " from RfxView e ";
		if (isRfaSubTypeFilter) {
			hql += " , RfaEvent rfa ";
		}
		hql += " inner join e.eventOwner eo inner join eo.buyer as buy ";
		hql += " where e.status in (:status) and e.supplier.id = :id ";
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			hql += " and ( (UPPER(e.referanceNumber) like :search) or (UPPER(e.eventName) like :search) or (UPPER(eo.name) like :search)) ";
		}
		if (search != null && StringUtils.checkString(search.getCreator()).length() > 0) {
			hql += " and UPPER(eo.name) like :creator ";
		}
		if (search != null && StringUtils.checkString(search.getBuyerName()).length() > 0) {
			hql += " and UPPER(buy.companyName) like :buyerName ";
		}
		if (search != null && StringUtils.checkString(search.getUnitName()).length() > 0) {
			hql += " and UPPER(e.unitName) like :unitName ";
		}
		if (search != null && StringUtils.checkString(search.getStatus()).length() > 0) {
			hql += " and UPPER(e.submissionStatus) = :submissionStatus ";
		}
		if (search != null && StringUtils.checkString(search.getEventOwner()).length() > 0) {
			hql += " and UPPER(eo.name) like :eventOwner ";
		}
		if (search != null && search.getType() != null) {
			LOG.info(" Type : " + search.getType().toString());
			if (search.getType() == FilterTypes.ALL_RFX) {
				// do nothing
			} else if (isRfaSubTypeFilter) {
				hql += " and rfa.id = e.id and rfa.auctionType = :rfaType and e.type = :type";
			} else {
				hql += " and e.type = :type ";
			}
		}
		if (search != null && StringUtils.checkString(search.getSortValue()).length() > 0 && StringUtils.checkString(search.getOrder()).length() > 0) {
			hql += " order by e." + search.getSortValue() + " " + search.getOrder();
		} else {
			hql += " order by e.createdDate DESC";
		}
		final Query query = getEntityManager().createQuery(hql.toString());
		if (search != null && StringUtils.checkString(search.getSearchValue()).length() > 0) {
			query.setParameter("search", "%" + search.getSearchValue().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getCreator()).length() > 0) {
			query.setParameter("creator", "%" + search.getCreator().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getBuyerName()).length() > 0) {
			query.setParameter("buyerName", "%" + search.getBuyerName().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getUnitName()).length() > 0) {
			query.setParameter("unitName", "%" + search.getUnitName().toUpperCase() + "%");
		}
		if (search != null && StringUtils.checkString(search.getStatus()).length() > 0) {
			query.setParameter("submissionStatus", SubmissionStatusType.valueOf(search.getStatus()));
		}
		if (search != null && StringUtils.checkString(search.getEventOwner()).length() > 0) {
			query.setParameter("eventOwner", "%" + search.getEventOwner().toUpperCase() + "%");
		}
		if (search != null && search.getType() != null) {
			if (search.getType() == FilterTypes.ALL_RFX) {
				// do nothing
			} else if (isRfaSubTypeFilter) {
				LOG.info("AyctionType......" + search.getType());
				LOG.info("getType.name " + search.getType().name());
				query.setParameter("type", RfxTypes.RFA);
				query.setParameter("rfaType", AuctionType.valueOf(search.getType().name()));
				LOG.info("AyctionType......" + RfxTypes.valueOf(RfxTypes.RFA.name()));
			} else {
				query.setParameter("type", RfxTypes.valueOf(search.getType().toString()));
			}
		}
		query.setParameter("id", tenantId);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.CLOSED);
		list.add(EventStatus.FINISHED);
		list.add(EventStatus.COMPLETE);
		list.add(EventStatus.SUSPENDED);
		query.setParameter("status", list);
		if (search != null && search.getStart() != null && search.getLength() != null) {
			query.setFirstResult(search.getStart());
			query.setMaxResults(search.getLength());
		}
		return query;
	}

	@Override
	public long findAggregateEventCountForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception {
		String sql = findAggregateEventCountForTenant();

		if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
			sql += " and upper(a.CURRENCY_CODE) = :currencyCode ";
		}
		if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
			sql += " and a.EVENT_PUBLISH_DATE between :startDate and :endDate ";
		}
		if (status != null) {
			sql += " and a.STATUS in (:status) ";
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
			query.setParameter("currencyCode", filter.getCurrencyCode().toUpperCase());
		}
		if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			LOG.info("Start Date :" + df.parse(filter.getStartDate()));
			Date endDate = df.parse(filter.getEndDate());
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(endDate);
			endCal.set(Calendar.HOUR, 23);
			endCal.set(Calendar.MINUTE, 59);
			endCal.set(Calendar.SECOND, 59);
			endDate = endCal.getTime();
			LOG.info("end Date :" + endDate);

			query.setParameter("startDate", df.parse(filter.getStartDate()));
			query.setParameter("endDate", endDate);
		}
		if (status != null) {
			query.setParameter("status", status);
		}
		// LOG.info("sql : "+ sql);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public BigDecimal findAggregateEventBudgetAmountValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception {
		String sql = findAggregateEventBudgetAmountValueForTenant();

		if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
			sql += " and upper(a.CURRENCY_CODE) = :currencyCode ";
		}
		if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
			sql += " and a.EVENT_PUBLISH_DATE between :startDate and :endDate ";
		}
		if (status != null) {
			sql += " and a.STATUS in (:status) ";
		}
		Log.info("Mobile Budget Amount :" + sql);
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
			query.setParameter("currencyCode", filter.getCurrencyCode().toUpperCase());
		}
		if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			LOG.info("Start Date :" + df.parse(filter.getStartDate()));
			Date endDate = df.parse(filter.getEndDate());
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(endDate);
			endCal.set(Calendar.HOUR, 23);
			endCal.set(Calendar.MINUTE, 59);
			endCal.set(Calendar.SECOND, 59);
			endDate = endCal.getTime();
			LOG.info("end Date :" + endDate);

			query.setParameter("startDate", df.parse(filter.getStartDate()));
			query.setParameter("endDate", endDate);
		}
		if (status != null) {
			query.setParameter("status", status);
		}
		LOG.info("sql : " + sql);
		BigDecimal value = (BigDecimal) query.getSingleResult();
		return value != null ? value : new BigDecimal(0);
	}

	@Override
	public BigDecimal findAggregateEventAwardedPriceValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception {
		String sql = findAggregateEventAwardedPriceValueForTenant();

		if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
			sql += " and upper(a.CURRENCY_CODE) = :currencyCode ";
		}
		if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
			sql += " and a.EVENT_PUBLISH_DATE between :startDate and :endDate ";
		}
		if (status != null) {
			sql += " and a.STATUS in (:status) ";
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
			query.setParameter("currencyCode", filter.getCurrencyCode().toUpperCase());
		}
		if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			LOG.info("Start Date :" + df.parse(filter.getStartDate()));
			Date endDate = df.parse(filter.getEndDate());
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(endDate);
			endCal.set(Calendar.HOUR, 23);
			endCal.set(Calendar.MINUTE, 59);
			endCal.set(Calendar.SECOND, 59);
			endDate = endCal.getTime();
			LOG.info("end Date :" + endDate);

			query.setParameter("startDate", df.parse(filter.getStartDate()));
			query.setParameter("endDate", endDate);
		}
		if (status != null) {
			query.setParameter("status", status);
		}
		BigDecimal value = (BigDecimal) query.getSingleResult();
		return value != null ? value : new BigDecimal(0);
	}

	@Override
	public BigDecimal findAggregateClosedCompletedEventValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception {
		String sql = findAggregateClosedCompletedEventValueForTenant();

		if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
			sql += " and upper(s.currency) = :currencyCode ";
		}
		if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
			sql += " and s.publish between :startDate and :endDate ";
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		if (filter != null && StringUtils.checkString(filter.getCurrencyCode()).length() > 0) {
			query.setParameter("currencyCode", filter.getCurrencyCode().toUpperCase());
		}

		if (filter != null && StringUtils.checkString(filter.getStartDate()).length() > 0 && StringUtils.checkString(filter.getEndDate()).length() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			LOG.info("Start Date :" + df.parse(filter.getStartDate()));
			Date endDate = df.parse(filter.getEndDate());
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(endDate);
			endCal.set(Calendar.HOUR, 23);
			endCal.set(Calendar.MINUTE, 59);
			endCal.set(Calendar.SECOND, 59);
			endDate = endCal.getTime();
			LOG.info("end Date :" + endDate);
			query.setParameter("startDate", df.parse(filter.getStartDate()));
			query.setParameter("endDate", endDate);
		}

		query.setParameter("status", Arrays.asList(EventStatus.CLOSED.toString(), EventStatus.COMPLETE.toString()));
		LOG.info("findAggregateClosedComp Sql :" + sql);
		BigDecimal value = (BigDecimal) query.getSingleResult();
		return value != null ? value : new BigDecimal(0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RftSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId) {
		final Query query = getEntityManager().createQuery("from RftSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.grandTotal = (select min(r.grandTotal) from RftSupplierBq r where r.rfxEvent.id =:eventId and r.bq.id =:bqId and r.supplier.id in (select es.supplier.id from RftEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = false and es.submissionStatus=:submissionStatus )) and sb.rfxEvent.id =:eventId and sb.bq.id =:bqId and s.id in (select ess.supplier.id from RftEventSupplier ess where ess.rfxEvent.id = :eventId and ess.disqualify = false and ess.submissionStatus=:submissionStatus)");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("submissionStatus", SubmissionStatusType.COMPLETED);
		List<RftSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {

			// if more suppliers have same lowest price then sending the first submit supplier.
			if (uList.size() > 0) {
				List<String> suppIds = new ArrayList<>();
				for (RftSupplierBq rftSupplierBq : uList) {
					LOG.info("suuplier name: " + rftSupplierBq.getSupplier().getCompanyName() + "======= rfaSupplierBq:  " + rftSupplierBq.getGrandTotal());
					suppIds.add(rftSupplierBq.getSupplier().getId());
				}
				Query bidHistoryQuery = getEntityManager().createQuery("select distinct ab from RftEventSupplier ab left outer join fetch ab.supplier sup  where sup.id in (:suppIds) and ab.rfxEvent.id = :eventId and ab.supplierSubmittedTime in (select max(abs.supplierSubmittedTime) from RftEventSupplier abs where abs.supplier.id in (:suppIds) and abs.rfxEvent.id = :eventId  group by abs.supplier.id) ");
				bidHistoryQuery.setParameter("eventId", eventId);
				bidHistoryQuery.setParameter("suppIds", suppIds);
				List<RftEventSupplier> auctionBidsList = bidHistoryQuery.getResultList();
				/*
				 * for (RftEventSupplier rftEventSupplier : auctionBidsList) {
				 * LOG.info("-------------------"+rftEventSupplier.getSupplierSubmittedTime()+"------------"+
				 * rftEventSupplier.getSupplier().getCompanyName()); }
				 */
				if (CollectionUtil.isNotEmpty(auctionBidsList)) {
					Collections.sort(auctionBidsList, new Comparator<RftEventSupplier>() {
						public int compare(RftEventSupplier o1, RftEventSupplier o2) {
							return o1.getSupplierSubmittedTime().compareTo(o2.getSupplierSubmittedTime());
						}
					});

					if (auctionBidsList.get(0) != null) {
						for (RftSupplierBq rftSupplierBq : uList) {
							if (rftSupplierBq.getSupplier().getId().equals(auctionBidsList.get(0).getSupplier().getId())) {
								return rftSupplierBq;
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
	public RftSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId) {
		final Query query = getEntityManager().createQuery("from RftSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.rfxEvent.id =:eventId and sb.bq.id =:bqId and s.id =:supplierId");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		query.setParameter("supplierId", supplierId);
		List<RftSupplierBq> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftSupplierBq> getSupplierBQOfLowestItemisedPrize(String eventId, String bqId) {
		final Query query = getEntityManager().createQuery("select distinct sb from RftSupplierBq sb left outer join fetch sb.supplier s left outer join fetch sb.bq b left outer join fetch sb.supplierBqItems sbi where sb.rfxEvent.id =:eventId and sb.bq.id =:bqId");
		query.setParameter("eventId", eventId);
		query.setParameter("bqId", bqId);
		List<RftSupplierBq> uList = query.getResultList();
		return uList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RftSupplierBqItem getMinItemisePrice(String id, String eventId) {
		final Query query = getEntityManager().createQuery("select distinct sb from RftSupplierBqItem sb left outer join fetch sb.supplier s left outer join fetch sb.event e  left outer join fetch sb.bqItem bi where sb.totalAmount = (select min(r.totalAmount) from RftSupplierBqItem r where r.bqItem.id = :bqItemId  and r.supplier.id in (select es.supplier.id from RftEventSupplier es where es.rfxEvent.id = :eventId and es.disqualify = false)) and bi.id = :bqItemId and s.id in (select ess.supplier.id from RftEventSupplier ess where ess.rfxEvent.id = :eventId and ess.disqualify = false )");
		query.setParameter("bqItemId", id);
		query.setParameter("eventId", eventId);
		List<RftSupplierBqItem> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {

			if (uList.size() > 0) {
				List<String> suppIds = new ArrayList<>();
				for (RftSupplierBqItem rftSupplierBq : uList) {
					LOG.info("suuplier name: " + rftSupplierBq.getSupplier().getCompanyName() + "======= rftSupplierBq:  " + rftSupplierBq.getUnitPrice());
					suppIds.add(rftSupplierBq.getSupplier().getId());
				}
				Query bidHistoryQuery = getEntityManager().createQuery("select distinct ab from RftEventSupplier ab left outer join fetch ab.supplier sup  where sup.id in (:suppIds) and ab.rfxEvent.id = :eventId and ab.supplierSubmittedTime in (select max(abs.supplierSubmittedTime) from RftEventSupplier abs where abs.supplier.id in (:suppIds) and abs.rfxEvent.id = :eventId  group by abs.supplier.id) ");
				bidHistoryQuery.setParameter("eventId", eventId);
				bidHistoryQuery.setParameter("suppIds", suppIds);
				List<RftEventSupplier> auctionBidsList = bidHistoryQuery.getResultList();
				if (CollectionUtil.isNotEmpty(auctionBidsList)) {
					for (RftEventSupplier rftEventSupplier : auctionBidsList) {
						LOG.info("-------------------" + rftEventSupplier.getSupplierSubmittedTime() + "------------" + rftEventSupplier.getSupplier().getCompanyName());
					}
					Collections.sort(auctionBidsList, new Comparator<RftEventSupplier>() {
						public int compare(RftEventSupplier o1, RftEventSupplier o2) {
							return o1.getSupplierSubmittedTime().compareTo(o2.getSupplierSubmittedTime());
						}
					});

					if (auctionBidsList.get(0) != null) {
						for (RftSupplierBqItem rftSupplierBq : uList) {
							if (rftSupplierBq.getSupplier().getId().equals(auctionBidsList.get(0).getSupplier().getId())) {
								return rftSupplierBq;
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
	public long findCountOfAllActiveEventForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Active: " + supplierId);
		Calendar currentDate = Calendar.getInstance();
		String sql = "select count(e) from RfxView as e ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		// sql += " where e.status in (:status) and e.supplier.id = :id and e.supplierSubmittedTime is not null ";
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		sql += " and e.submissionStatus in (:accepted) and e.eventStart < :currentDate ";
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.APPROVED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		query.setParameter("currentDate", currentDate.getTime());
		List<SubmissionStatusType> submissionStatus = new ArrayList<SubmissionStatusType>();
		submissionStatus.add(SubmissionStatusType.ACCEPTED);
		submissionStatus.add(SubmissionStatusType.REJECTED);
		submissionStatus.add(SubmissionStatusType.COMPLETED);
		query.setParameter("accepted", submissionStatus);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();

	}

	@Override
	public long findCountOfAllSuspendedEventForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Suspended: " + supplierId);
		String sql = "select count(e) from RfxView as e ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.SUSPENDED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();

	}

	@Override
	public long findCountOfAllClosedEventForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Closed: " + supplierId);
		String sql = "select count(e) from RfxView as e ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		sql += " and e.submissionStatus = :complete ";
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.CLOSED);
		list.add(EventStatus.COMPLETE);
		list.add(EventStatus.FINISHED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("complete", SubmissionStatusType.COMPLETED);
		return ((Number) query.getSingleResult()).longValue();

	}

	@Override
	public long findCountOfAllRejectedEventForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Rejected: " + supplierId);
		String sql = "select count(e) from RfxView as e ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		sql += " and e.submissionStatus = :rej ";
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.SUSPENDED);
		list.add(EventStatus.CLOSED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		query.setParameter("rej", SubmissionStatusType.REJECTED);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();

	}

	@Override
	public long findCountOfAllPendingEventForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Pending : " + supplierId);
		String sql = "select count(e) from RfxView as e ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		sql += " and e.submissionStatus = :invited ";
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		// list.add(EventStatus.SUSPENDED);
		// list.add(EventStatus.CLOSED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		query.setParameter("invited", SubmissionStatusType.INVITED);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findCountOfAllCompletedEventForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Completed : " + supplierId);
		String sql = "select count(e) from RfxView as e ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		sql += " and e.submissionStatus = :complete ";
		LOG.info("SQl :" + sql);
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.SUSPENDED);
		list.add(EventStatus.CLOSED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		query.setParameter("complete", SubmissionStatusType.COMPLETED);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getOnlyAllSuspendedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllSuspendedEventsForSupplier(supplierId, input, userId, false);
		/*
		 * query.setFirstResult(input.getStart()); query.setMaxResults(input.getLength());
		 */

		return query.getResultList();

		/*
		 * if (CollectionUtil.isNotEmpty(resultList)) { LinkedHashSet<RfxView> result = new LinkedHashSet<RfxView>();
		 * for (RfxView rfxView : resultList) { result.add(rfxView); } return new ArrayList<RfxView>(result); } else {
		 * return null; }
		 */

	}

	@Override
	public long findTotalOnlyAllSuspendedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllSuspendedEventsForSupplier(supplierId, input, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructOnlyAllSuspendedEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type = :type ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventStart desc ";
			}
		}

		// LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.SUSPENDED);
		query.setParameter("status", list);

		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllActiveEventsForSupplier(supplierId, input, userId, false);
		/*
		 * query.setFirstResult(input.getStart()); query.setMaxResults(input.getLength());
		 */

		return query.getResultList();
		/*
		 * if (CollectionUtil.isNotEmpty(resultList)) { LinkedHashSet<RfxView> result = new LinkedHashSet<RfxView>();
		 * for (RfxView rfxView : resultList) { result.add(rfxView); } return new ArrayList<RfxView>(result); } else {
		 * return null; }
		 */
	}

	@Override
	public long findTotalOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllActiveEventsForSupplier(supplierId, input, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";
		Calendar currentDate = Calendar.getInstance();
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		// hql += " and e.supplierSubmittedTime is not null";
		hql += " and e.submissionStatus in (:accepted) and e.eventStart < :currentDate ";
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type = :type ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventEnd asc ";
			}
		}

		// LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.APPROVED);
		List<SubmissionStatusType> submissionStatus = new ArrayList<SubmissionStatusType>();
		submissionStatus.add(SubmissionStatusType.ACCEPTED);
		submissionStatus.add(SubmissionStatusType.REJECTED);
		submissionStatus.add(SubmissionStatusType.COMPLETED);
		query.setParameter("accepted", submissionStatus);
		query.setParameter("currentDate", currentDate.getTime());
		query.setParameter("status", list);

		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllClosedEventsForSupplier(supplierId, input, userId, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		return query.getResultList();

		/*
		 * if (CollectionUtil.isNotEmpty(resultList)) { LinkedHashSet<RfxView> result = new LinkedHashSet<RfxView>();
		 * for (RfxView rfxView : resultList) { result.add(rfxView); } return new ArrayList<RfxView>(result); } else {
		 * return null; }
		 */ }

	@Override
	public long findTotalOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllClosedEventsForSupplier(supplierId, input, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch().getValue() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type = :type ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

				}
			}
		}
		hql += " and e.submissionStatus = :complete ";

		// If it is not a count query then add order by clause
		if (!isCount) {
			List<OrderParameter> orderList = tableParams.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				hql += " order by ";
				for (OrderParameter order : orderList) {
					String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventEnd  ";
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.CLOSED);
		list.add(EventStatus.COMPLETE);
		list.add(EventStatus.FINISHED);
		query.setParameter("status", list);
		query.setParameter("complete", SubmissionStatusType.COMPLETED);
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllCompletedEventsForSupplier(supplierId, input, userId, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		return query.getResultList();
		// List<RfxView> resultList = query.getResultList();

		/*
		 * if (CollectionUtil.isNotEmpty(resultList)) { LinkedHashSet<RfxView> result = new LinkedHashSet<RfxView>();
		 * for (RfxView rfxView : resultList) { result.add(rfxView); } return new ArrayList<RfxView>(result); } else {
		 * return null; }
		 */
	}

	@Override
	public long findTotalOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllCompletedEventsForSupplier(supplierId, input, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status) and e.supplier.id = :id ";
		hql += " and e.submissionStatus = :complete ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type = :type ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventStart desc ";
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.SUSPENDED);
		list.add(EventStatus.CLOSED);
		query.setParameter("status", list);
		query.setParameter("complete", SubmissionStatusType.COMPLETED);
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getOnlyAllRejectedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllRejectedEventsForSupplier(supplierId, input, userId, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		return query.getResultList();

		/*
		 * if (CollectionUtil.isNotEmpty(resultList)) { LinkedHashSet<RfxView> result = new LinkedHashSet<RfxView>();
		 * for (RfxView rfxView : resultList) { result.add(rfxView); } return new ArrayList<RfxView>(result); } else {
		 * return null; }
		 */
	}

	@Override
	public long findTotalOnlyAllRejectedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllRejectedEventsForSupplier(supplierId, input, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructOnlyAllRejectedEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status) and e.supplier.id = :id ";
		hql += " and e.submissionStatus = :rejected ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type = :type ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventEnd asc ";
			}
		}

		// LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.SUSPENDED);
		list.add(EventStatus.CLOSED);
		query.setParameter("status", list);
		query.setParameter("rejected", SubmissionStatusType.REJECTED);
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getOnlyAllPendingEventsForSupplier(String supplierId, TableDataInput input, String userId) {

		/*
		 * final Query query = constructQuery(supplierId, input, userId, false); query.setFirstResult(input.getStart());
		 * query.setMaxResults(input.getLength()); return query.getResultList();
		 */

		final Query query = constructOnlyAllPendingEventsForSupplier(supplierId, input, userId, false);
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();

	}

	@Override
	public long findTotalPendingEventForForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllPendingEventsForSupplier(supplierId, input, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unused")
	private Query constructQuery(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String sql = "";

		if (isCount) {
			sql += " SELECT count(*) FROM ( ";
		}
		sql += " SELECT DISTINCT * FROM ( ";
		int count = RfxTypes.values().length;
		for (RfxTypes rfxType : RfxTypes.values()) {
			sql += " SELECT DISTINCT e.ID, EVENT_NAME, REFERANCE_NUMBER, EVENT_START, EVENT_END, STATUS, '" + rfxType.name() + "' AS EVENT_TYPE ";
			sql += " FROM PROC_" + rfxType.name() + "_EVENTS e LEFT OUTER JOIN PROC_FAVOURITE_SUPPLIER fs ON e.TENANT_ID = fs.BUYER_ID LEFT OUTER join PROC_" + rfxType.name() + "_EVENT_SUPPLIERS s ON e.ID = s.EVENT_ID ";
			if (StringUtils.checkString(userId).length() > 0) {
				sql += " LEFT OUTER JOIN PROC_" + rfxType.name() + "_SUPPLIER_TEAM est on e.ID = est.EVENT_ID ";
			}
			sql += " WHERE STATUS = 'ACTIVE' ";
			if (StringUtils.checkString(userId).length() > 0) {
				sql += " AND (est.USER_ID = :userId) ";
			}
			sql += " AND ( ";
			sql += " ( fs.SUPPLIER_ID = :supplierId	AND e.EVENT_VISIBILITY <> 'PRIVATE' AND (SELECT count(ess.ID) FROM PROC_" + rfxType.name() + "_EVENT_SUPPLIERS ess WHERE ess.EVENT_ID =e.ID AND ess.SUPPLIER_ID =:supplierId) = 0) " + " OR ";
			sql += " (s.SUPPLIER_ID=:supplierId AND s.SUBMISSION_STATUS = 'INVITED') ";
			sql += " ) ";

			if (count > 1) {
				sql += " UNION ";
			}
			count--;
		}
		sql += " ) WHERE 1 = 1 ";

		LOG.info("=================>" + sql);

		// Add on search filter conditions
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						sql += " AND EVENT_TYPE in (:types) ";
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(EVENT_NAME) like (:" + cp.getData().replace(".", "") + ") ";
					} else if (cp.getData().equals("referanceNumber")) {
						sql += " and upper(REFERANCE_NUMBER) like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		if (isCount) {
			sql += " ) ";
		} else {
			// If it is not a count query then add order by clause
			if (tableParams != null) {
				List<OrderParameter> orderList = tableParams.getOrder();
				if (CollectionUtil.isNotEmpty(orderList)) {
					sql += " order by ";
					for (OrderParameter order : orderList) {
						String orderColumn = tableParams.getColumns().get(order.getColumn()).getData();
						String dir = order.getDir();
						if (orderColumn.equals("eventName")) {
							sql += " EVENT_NAME " + dir + ",";
						} else if (orderColumn.equals("eventStart")) {
							sql += " EVENT_START " + dir + ",";
						} else if (orderColumn.equals("eventEnd")) {
							sql += " EVENT_END " + dir + ",";
						} else if (orderColumn.equals("refrenceNumber")) {
							sql += " REFERANCE_NUMBER " + dir + ",";
						}
					}
					if (sql.lastIndexOf(",") == sql.length() - 1) {
						sql = sql.substring(0, sql.length() - 1);
					}
				} else {
					sql += " order by EVENT_START desc ";
				}
			} else {
				sql += " order by EVENT_START desc ";
			}
		}

		Query query = null;
		if (isCount) {
			query = getEntityManager().createNativeQuery(sql);
		} else {
			query = getEntityManager().createNativeQuery(sql, "supplierInvitedEventResult");
		}
		query.setParameter("supplierId", supplierId);

		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						query.setParameter("types", RfxTypes.fromString(cp.getSearch().getValue().toUpperCase()));
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return query;
	}

	private Query constructOnlyAllPendingEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status)";
		hql += " and e.supplier.id = :id ";
		hql += " and e.submissionStatus = :invited ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type = :type ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventStart desc ";
			}
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		// list.add(EventStatus.SUSPENDED);
		// list.add(EventStatus.CLOSED);
		query.setParameter("status", list);
		query.setParameter("invited", SubmissionStatusType.INVITED);
		return query;
	}

	@Override
	public boolean isExistsRftEventId(String tenantId, String eventId) {
		StringBuilder hsql = new StringBuilder("select count(p) from RftEvent p  where p.eventId= :eventId and p.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventId", eventId);
		return (((Number) query.getSingleResult()).longValue() > 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllEventsForBuyer(String tenantId, EventStatus status) {
		String sql = "";
		sql = getNativeRfxZipQuery();

		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");

		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(status.toString()));
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getAllEventsForSupplier(String tenantId, SubmissionStatusType status) {
		final Query query = constructAllEventsForSupplier(tenantId, status);

		return query.getResultList();

		/*
		 * if (CollectionUtil.isNotEmpty(resultList)) { LinkedHashSet<RfxView> result = new LinkedHashSet<RfxView>();
		 * for (RfxView rfxView : resultList) { result.add(rfxView); } return new ArrayList<RfxView>(result); } else {
		 * return null; }
		 */

	}

	private Query constructAllEventsForSupplier(String supplierId, SubmissionStatusType status) {

		String hql = "";

		hql += "select distinct NEW RfxView(e.id, e.eventId ,e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";

		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		hql += " where e.supplier.id = :id ";

		hql += " and e.submissionStatus = :status ";

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);

		query.setParameter("status", status);
		return query;
	}

	/*********************************************** delete data *****************************************************/
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventIdList(String tenantId) {
		StringBuilder hsql = new StringBuilder("select id from RftEvent p  where p.tenantId = :tenantId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllComments(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftComment pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAllDocument(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventDocument pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getenvelopIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RftEnvelop p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvaluatorUser(String envelopID) {
		StringBuilder hsql = new StringBuilder("delete from RftEvaluatorUser pc where pc.envelope.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", envelopID);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEnvelop(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEnvelop pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAddress(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventCorrespondenceAddress pc where pc.rfxEvent.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getApprovalIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RftEventApproval p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteApprovalUser(String aprovalID) {
		StringBuilder hsql = new StringBuilder("delete from RftApprovalUser pc where pc.approval.id= :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", aprovalID);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAproval(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventApproval p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAudit(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventAudit p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardAudit(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventAwardAudit p  where p.buyer.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAward(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventAward p  where p.rfxEvent.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqEvaluationComments(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftBqEvaluationComments p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RftEventBq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetail(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventAwardDetails p  where p.bqItem.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventBq p  where p.rfxEvent.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierComments(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RftSupplierComment p  where p.rftSupplierBqItem.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqSupplierItem(String bqId, boolean isParent) {

		String hsql = "delete from RftSupplierBqItem p  where p.id = :id ";
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
		StringBuilder hsql = new StringBuilder("select id from RftSupplierBq p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierBq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftSupplierBq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventContact(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventContact p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RftCq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvalutionCqComts(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftCqEvaluationComments p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getCqItemIdList(String cqId) {
		StringBuilder hsql = new StringBuilder("select id from RftCqItem p  where p.cq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deletCqOption(String cqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RftCqOption p  where p.rftCqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deletSuppCqOption(String cqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RftSupplierCqOption p  where p.cqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCq(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from  RftCq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierCqIdList(String eventId) {
		StringBuilder hsql = new StringBuilder("select id from RftSupplierCqItem p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSupplierCqItemIdList(String cqId) {
		StringBuilder hsql = new StringBuilder("select id from RftSupplierCqItem p  where p.cq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCqItem(String eventId) {

		StringBuilder hsql = new StringBuilder("delete from   RftCqItem p  where p.rfxEvent.id = :id and p.parent is not null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteCqParentItem(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RftCqItem p  where p.rfxEvent.id = :id and p.parent is null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqItem(String cqId) {
		StringBuilder hsql = new StringBuilder("delete from   RftSupplierCqItem p  where p.event.id = :id ");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierCqParentItem(String cqId) {
		StringBuilder hsql = new StringBuilder("delete from   RftSupplierCqItem p  where p.cq.id = :id and p.parent is null");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqItem(String bqId, boolean isParent) {
		String hsql = "delete from   RftBqItem p  where p.id = :id ";
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
		StringBuilder hsql = new StringBuilder("select id from RftEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingContact(String mid) {
		StringBuilder hsql = new StringBuilder("delete from   RftEventMeetingContact p  where p.rfxEventMeeting.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", mid);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingDoc(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from   RftEventMeetingDocument p  where p.tenantId = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeetingReminder(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RftEventMeetingReminder p  where p.rftEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierMeetingAtt(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RftSupplierMeetingAttendance p  where p.rftEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteSupplierTeam(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RftSupplierTeamMember p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventSupplier(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RftEventSupplier p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteTimeLine(String tenantId) {
		StringBuilder hsql = new StringBuilder("delete from   RftEventTimeLine p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", tenantId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventReminder(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from   RftReminder p  where p.rftEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getBqItemList(String bqId) {
		StringBuilder hsql = new StringBuilder("select id from RftBqItem p  where p.bq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSppBqItemList(String bqId) {

		StringBuilder hsql = new StringBuilder("select id from RftSupplierBqItem p  where p.supplierBq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteBqItems(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventBq p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteeventBqItems(String eventId, boolean isParent) {

		String hsql = "delete from RftBqItem p  where p.rfxEvent.id = :id ";
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
		String hsql = "delete from RftSupplierBqItem p  where p.event.id = :id ";
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
		String hsql = "delete from RftSupplierCqOption p  where p.cqItem.id = :id ";

		Query query = getEntityManager().createQuery(hsql);
		query.setParameter("id", cqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetails(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventAward p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventAwardByBq(String bqId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventAward p  where p.bq.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteAwardDetailbyItem(String bqItemId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventAwardDetails p  where p.bqItem.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", bqItemId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEvent(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEvent p  where p.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMeeting(String eventId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteMettingContact(String meetingId) {
		StringBuilder hsql = new StringBuilder("delete from RftEventMeetingContact p  where p.rfxEventMeeting.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", meetingId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getEventMeettingIds(String eventId) {

		StringBuilder hsql = new StringBuilder("select id from RftEventMeeting p  where p.rfxEvent.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		return query.getResultList();

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteEventMessage(String eventId, boolean isParent) {

		String hsql = "delete from RftEventMessage p  where p.event.id = :id ";
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
		StringBuilder hsql = new StringBuilder("delete from RftTeamMember p  where p.event.id = :id");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("id", eventId);
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllEventsForBuyer(String tenantId, TableDataInput input, String userId, Date startDate, Date endDate) {
		String sql = null;

		if (startDate != null && endDate != null) {
			sql = getNativeRfxReportQueryDate();
		} else {

			sql = getNativeRfxReportQuery();
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			LOG.info(" orderList Not Empty");
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				switch (orderColumn) {
				case "eventName":
					orderColumn = "a.EVENT_NAME";
					break;
				case "createdBy.name":
					orderColumn = "puser.LOGIN_ID";
					break;
				case "createdDate":
					orderColumn = "a.CREATED_DATE";
					break;
				case "modifiedDate":
					orderColumn = "a.MODIFIED_DATE";
					break;
				case "type":
					orderColumn = "a.EVENT_TYPE";
					break;
				default:
					break;
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			LOG.info(" orderList Empty");
			sql += " order by a.EVENT_START desc";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "eventReportResult");
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.APPROVED.toString(), EventStatus.CANCELED.toString(), EventStatus.REJECTED.toString(), EventStatus.SUSPENDED.toString(), EventStatus.COMPLETE.toString(), EventStatus.FINISHED.toString(), EventStatus.PENDING.toString(), EventStatus.CLOSED.toString(), EventStatus.ACTIVE.toString()));
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllExcelEventReportForBuyer(String tenantId, String[] eventArr) {
		String sql = getNativeRfxReportQueryForReportExport();
		sql += " order by a.EVENT_START desc";
		final Query query = getEntityManager().createNativeQuery(sql, "eventReportResult");
		query.setParameter("id", Arrays.asList(eventArr));
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.APPROVED.toString(), EventStatus.CANCELED.toString(), EventStatus.REJECTED.toString(), EventStatus.SUSPENDED.toString(), EventStatus.COMPLETE.toString(), EventStatus.FINISHED.toString(), EventStatus.PENDING.toString(), EventStatus.CLOSED.toString(), EventStatus.ACTIVE.toString()));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEvent> getEventsByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		final Query query = searchFilterEventReport(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	private Query searchFilterEventReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		LOG.info("eventIds" + eventIds);
		String hql = "";

		hql = "select distinct e from RftEvent e join fetch e.suppliers as sup join e.eventOwner as user join e.businessUnit as unit where e.tenantId = :tenantId ";

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
	public List<RfxView> getOnlyAllAcceptedEventsForSupplier(String supplierId, TableDataInput input, String userId) {

		final Query query = constructOnlyAllAcceptedEventsForSupplier(supplierId, input, userId, false);
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input.getLength());

		return query.getResultList();

		/*
		 * if (CollectionUtil.isNotEmpty(resultList)) { LinkedHashSet<RfxView> result = new LinkedHashSet<RfxView>();
		 * for (RfxView rfxView : resultList) { result.add(rfxView); } return new ArrayList<RfxView>(result); } else {
		 * return null; }
		 */
	}

	@Override
	public long findTotalAcceptedEventForForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructOnlyAllAcceptedEventsForSupplier(supplierId, input, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructOnlyAllAcceptedEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";
		Calendar currentDate = Calendar.getInstance();

		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status) and e.eventStart > :currentDate";
		hql += " and e.supplier.id = :id ";
		hql += " and e.submissionStatus = :invited  ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type in (:types) ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventStart desc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {

					query.setParameter("types", RfxTypes.fromString(cp.getSearch().getValue().toUpperCase()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		// list.add(EventStatus.SUSPENDED);
		// list.add(EventStatus.CLOSED);
		query.setParameter("status", list);
		query.setParameter("currentDate", currentDate.getTime());

		query.setParameter("invited", SubmissionStatusType.ACCEPTED);
		return query;
	}

	@Override
	public long findCountOfAllAcceptedEventForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Pending : " + supplierId);
		String sql = "select count(e) from RfxView as e ";
		Calendar currentDate = Calendar.getInstance();
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		sql += " and e.submissionStatus = :invited and e.eventStart > :currentDate ";
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		// list.add(EventStatus.SUSPENDED);
		// list.add(EventStatus.CLOSED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		query.setParameter("currentDate", currentDate.getTime());
		query.setParameter("invited", SubmissionStatusType.ACCEPTED);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEvent findRftEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId) {
		final Query query = getEntityManager().createQuery("from RftEvent e where e.erpAwardRefId like:erpAwardRefId and e.tenantId=:tenantId");
		query.setParameter("erpAwardRefId", "%" + erpAwardRefId + "%");
		query.setParameter("tenantId", tenantId);
		List<RftEvent> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public List<RftEvent> getSearchEventReportssByIds(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {
		return searchFilterEventReportList(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate);
	}

	@SuppressWarnings("unchecked")
	private List<RftEvent> searchFilterEventReportList(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {
		LOG.info("eventIds" + eventIds);
		String hql = "";

		hql = "select distinct e from RftEvent e join fetch e.suppliers as sup join e.eventOwner as user join e.businessUnit as unit where e.tenantId = :tenantId ";

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

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEvent> getAllRftEventByTenantId(String tenantId) {
		String hql = "from RftEvent re where re.tenantId=:tenantId";
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
		final Query query = getEntityManager().createQuery("from RftEvent r where r.eventId =:eventRefranceNo and r.tenantId =:tenantId and r.status in (:status)");
		query.setParameter("eventRefranceNo", eventRefranceNo);
		query.setParameter("tenantId", tenantId);
		List<EventStatus> status = new ArrayList<EventStatus>();
		status.add(EventStatus.APPROVED);
		status.add(EventStatus.ACTIVE);
		query.setParameter("status", status);
		List<RftEvent> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public User getUnMaskedUserNameAndMailByEventId(String eventId) {
		String hql = "select new User(u.id, u.loginId, u.name, u.communicationEmail, u.emailNotifications, u.tenantId)from RftEvent e left outer join e.unMaskedUser u where e.id=:id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		try {
			return (User) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public EventPojo findEventPojoById(String id) {
		try {
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id, r.eventId, ro.name, rob.line1, rob.line2, rob.city, robs.stateName, robc.countryName, ro.communicationEmail,ro.phoneNumber,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate, r.submissionValidityDays, r.participationFees, rpc.currencyCode, r.tenantId, r.status, r.eventDescription, rbc.currencyCode, r.decimal,  r.paymentTerm,r.documentReq,r.meetingReq, r.questionnaires,r.billOfQuantity, r.scheduleOfRate,r.erpEnable,rob.companyName,r.enableSupplierDeclaration, r.disableTotalAmount,r.suspensionType) from RftEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join rob.state as robs left outer join robs.country as robc left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as rpc where r.id =:eventId");
			query.setParameter("eventId", id);
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
	public EventPojo loadSupplierEventPojoForSummeryById(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id, r.eventId, ro.name, rob.line1, rob.line2, rob.city, robs.stateName, robc.countryName, ro.communicationEmail,ro.phoneNumber,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate, r.submissionValidityDays, r.participationFees, rpc.currencyCode, r.tenantId, r.status, r.eventDescription, rbc.currencyName, r.decimal,  r.paymentTerm,r.documentReq,r.meetingReq, r.questionnaires,r.billOfQuantity, r.scheduleOfRate,r.erpEnable,rob.companyName,da,rbc.currencyCode,bu.displayName, das.stateName, dac.countryName,r.deposit,rdc.currencyCode,r.suspensionType) from RftEvent r inner join r.eventOwner as ro inner join ro.buyer as rob left outer join rob.state as robs left outer join robs.country as robc left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as rpc left outer join r.depositCurrency as rdc left outer join r.deliveryAddress da left outer join r.businessUnit bu left outer join da.state das left outer join das.country dac where r.id =:eventId");
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
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventMeetingFlagsToFalse(String eventId) {
		Query query = getEntityManager().createQuery("update RftEvent e set e.meetingCompleted = false, e.meetingReq = false where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllEventsForBuyerEventReport(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = eventsForReport(input, tenantId, startDate, endDate, false);

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		LOG.info("================>" + input.getStart());
		LOG.info("================>" + input.getLength());

		LOG.info("" + query.getResultList().size());

		return query.getResultList();
	}

	@Override
	public long findTotalAllEventsForBuyerEventReport(String loggedInUserTenantId) {
		final Query query = eventsForReport(null, loggedInUserTenantId, null, null, true);
		int unreadMessageCount = ((Number) query.getSingleResult()).intValue();
		return unreadMessageCount;
	}

	private Query constructAllEventsForReport(TableDataInput tableParams, String tenantId, Date startDate, Date endDate, boolean isCount) {

		String hql = "";

		if (isCount) {
			hql += "select count(distinct e) ";
		} else {

			hql += "select distinct NEW com.privasia.procurehere.core.pojo.DraftEventPojo(e.id, e.eventName, e.eventStart, e.eventEnd, e.type, e.status, e.businessUnit.unitName, e.referanceNumber, e.eventId,e.eventOwner.name)";
		}
		hql += " from RfxView e ";

		hql += " where e.tenantId=:tenantId ";

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and e.createdDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
					if (cp.getData().equals("type")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							hql += " and e.type =:types ";
						} else {
							hql += " and e.type in (:types) ";
						}
					} else if (cp.getData().equals("status")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							hql += " and e.status =:status ";
						} else {
							hql += " and e.status in (:status) ";
						}
					} else {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							if (cp.getData().equalsIgnoreCase("referenceNumber")) {
								hql += " and upper(e.referanceNumber) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("sysEventId")) {
								hql += " and upper(e.eventId) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("eventUser")) {
								hql += " and upper(e.eventOwner.name) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("unitName")) {
								hql += " and upper(e.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ") ";
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
					if (orderColumn.equalsIgnoreCase("referenceNumber")) {
						hql += " e.referanceNumber " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("sysEventId")) {
						hql += " e.eventId " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("eventUser")) {
						hql += " e.eventOwner.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("unitName")) {
						hql += " e.businessUnit.unitName " + dir + ",";
					}

					else {
						hql += " e." + orderColumn + " " + dir + ",";
					}

				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.createdDate desc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// Apply search filter values
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

					if (cp.getData().equals("type")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							query.setParameter("types", RfxTypes.fromStringToRfxType(cp.getSearch().getValue()));
						} else {
							List<RfxTypes> types = Arrays.asList(RfxTypes.RFT, RfxTypes.RFP, RfxTypes.RFA, RfxTypes.RFQ, RfxTypes.RFI);
							query.setParameter("types", types);
						}
					} else if (cp.getData().equals("status")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							query.setParameter("status", EventStatus.fromString(cp.getSearch().getValue()));
						} else {
							List<EventStatus> types = Arrays.asList(EventStatus.ACTIVE, EventStatus.DRAFT, EventStatus.PENDING, EventStatus.APPROVED, EventStatus.REJECTED, EventStatus.CANCELED, EventStatus.SUSPENDED, EventStatus.ACTIVE, EventStatus.CLOSED, EventStatus.FINISHED, EventStatus.COMPLETE);
							query.setParameter("status", types);
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
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		return query;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IndustryCategory> getIndustryCategoryListForRfaById(String eventId) {
		StringBuffer sb = new StringBuffer("select distinct NEW com.privasia.procurehere.core.entity.IndustryCategory(ind.id, ind.code, ind.name) from RftEvent re left outer join re.industryCategories ind where re.id =:eventId ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStatus(EventStatus status, String eventId) {
		StringBuffer sb = new StringBuffer("update RftEvent e set e.status=:status where e.id=:eventId");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("status", status);
		query.executeUpdate();
	}

	@Override
	public RftEvent getRftEventForShortSummary(String eventId) {
		String hql = "select e from RftEvent e left outer join fetch e.baseCurrency  where e.id=:id";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", eventId);
		try {
			return (RftEvent) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		String hql = "select count(*) from RftEventSupplier sup  where  sup.rfxEvent.id=:eventId";

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
	public long getParticepatedSupplierCount(String eventId) {
		String hql = "select count(*) from RftEventSupplier sup  where  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType";
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
		String hql = "select count(*) from RftEventSupplier sup  where  sup.rfxEvent.id=:eventId and sup.submissionStatus=:submissionStatusType";
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
	public List<RftSupplierBq> getLowestSubmissionPrice(String eventId) {
		String hql = "select  bq from RftSupplierBq  bq  left outer join fetch bq.supplier where  bq.rfxEvent.id=:eventId and  bq.grandTotal=(select MIN(bq.grandTotal) FROM bq  where  bq.rfxEvent.id=:id)";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("eventId", eventId);
		query.setParameter("id", eventId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSupplierPojo> getSuppliersByStatus(String eventId) {
		final Query query = getEntityManager().createQuery("select NEW com.privasia.procurehere.core.pojo.EventSupplierPojo(sup.id, s.id, s.companyName, sum(bq.totalAfterTax)) from RftEventSupplier sup left outer join sup.supplier s, RftSupplierBq bq where bq.supplier.id = sup.supplier.id and sup.rfxEvent.id = :eventId and sup.submissionStatus = :submissionStatusType and sup.disqualify = :disqualify and bq.rfxEvent.id = :eventId group by sup.id, s.id, s.companyName order by SUM(bq.totalAfterTax)");
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

	/*
	 * @Override public RftSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId) { final Query query =
	 * getEntityManager().
	 * createQuery("select bq from RftSupplierBq bq where bq.supplier.id=:id and bq.rfxEvent.id=:eventId");
	 * query.setParameter("id", supplierId); query.setParameter("eventId", eventId); try { return (RftSupplierBq)
	 * query.getSingleResult(); } catch (Exception e) { return null; } }
	 */
	@Override
	public RftEnvelop getBqForEnvelope(String envelopeId) {
		final Query query = getEntityManager().createQuery("select re from RftEnvelop re left outer join fetch re.bqList bq where re.id =:envelopeId ");
		query.setParameter("envelopeId", envelopeId);
		try {
			return (RftEnvelop) query.getSingleResult();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublicEventPojo> getActivePublicEventsByTenantId(String buyerId) {
		String hql = "SELECT distinct NEW com.privasia.procurehere.core.pojo.PublicEventPojo(e.id, e.eventName, e.referanceNumber,e.eventStart, e.eventEnd, e.type, i, o, b, c,e.eventDescription,bu) from RfxView e inner join e.businessUnit bu inner join e.industryCategory i inner join e.eventOwner o, Buyer b inner join b.registrationOfCountry c where b.id = :buyerId and e.status = :status and e.eventVisibility = :eventVisibility";

		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", EventStatus.ACTIVE);
		query.setParameter("eventVisibility", EventVisibilityType.PUBLIC);
		query.setParameter("buyerId", buyerId);
		LOG.info(hql + "HQL  getActivePublicEventsByTenantId(buyerId) " + buyerId);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublicEventPojo> findActivePublicEventsByTenantId(String buyerId, TableDataInput input) {

		final Query query = constructPublicActiveEventsQuery(buyerId, input, false);
		LOG.info(" findActivePublicEventsByTenantId(buyerId, input) called");
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input..getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalFilteredActivePublicEventsList(String buyerId, TableDataInput input) {
		LOG.info("findTotalFilteredActivePublicEventsList( buyerId , input) called");
		final Query query = constructPublicActiveEventsQuery(buyerId, input, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalActivePublicEventsList(String buyerId) {
		LOG.info("findTotalActivePublicEventsList( buyerId, input) called");
		StringBuilder hql = new StringBuilder("select count (e) from RfxView e where e.status = :status AND e.tenantId = :tenantId ");
		final Query query = getEntityManager().createQuery(hql.toString());
		LOG.info(" query :" + query);
		query.setParameter("status", EventStatus.ACTIVE);
		query.setParameter("tenantId", buyerId);
		return ((Number) query.getSingleResult()).longValue();

	}

	private Query constructPublicActiveEventsQuery(String buyerId, TableDataInput tableParams, boolean isCount) {

		String hql = "";

		if (!isCount) {
			hql += "SELECT distinct NEW com.privasia.procurehere.core.pojo.PublicEventPojo(e.id, e.eventName, e.referanceNumber,e.eventStart, e.eventEnd, e.type,o, b, c,e.eventDescription,bu.unitName,i.code,i.name) ";
		} else { // If count query is enabled, then add the select count(*) clause
			hql += "select count(*) ";
		}
		hql += " from RfxView e ";

		// If this is not a count query, only then add the join fetch. Count query does not require its
		if (!isCount) {
			hql += " left outer join e.businessUnit bu left outer join e.industryCategory i inner join e.eventOwner o ";
		}
		hql += " , Buyer b left outer join b.registrationOfCountry c";
		hql += " where b.id = :buyerId and e.status = :status and e.eventVisibility = :eventVisibility ";

		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					hql += " and e.status = (:" + cp.getData() + ")";
				} else if (cp.getData().equals("industryCategory")) {
					LOG.info("Industry :");
					// String[] inArray =
					hql += " and ( upper(e.industryCategory.code) like (:" + cp.getData().replace(".", "") + ") or upper(e.industryCategory.name) like (:" + cp.getData().replace(".", "") + ") ) ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ")";
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
					if (orderColumn.equalsIgnoreCase("eventEndDate")) {
						hql += " e.eventEnd " + dir + ",";
					} else if (orderColumn.equals("industryCategory")) {
						hql += "  e.industryCategory.code " + dir + ",";
					} else {
						hql += " e." + orderColumn + " " + dir + ",";
					}
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			}
		}
		LOG.info("HQL : " + hql);
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", EventStatus.ACTIVE);
		query.setParameter("eventVisibility", EventVisibilityType.PUBLIC);
		query.setParameter("buyerId", buyerId);
		// LOG.info(""+hql.);
		// query.setParameter("suppId", suppId);
		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("status")) {
					query.setParameter("status", Status.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublicEventPojo> findActivePublicEventsListByTenantId(String tenantId, TableDataInput input) {
		Query query = getNativePublicEventsActiveQuery(tenantId, input, false);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", EventStatus.ACTIVE.toString());
		query.setParameter("eventVisibility", EventVisibilityType.PRIVATE.toString());
		return query.getResultList();
	}

	private Query getNativePublicEventsActiveQuery(String tenantId, TableDataInput input, boolean isCount) {
		String sql = "";
		if (isCount) {
			sql += "SELECT COUNT(DISTINCT r.ID) as total FROM (";
		} else {
			sql += "SELECT DISTINCT r.ID AS id, r.EVENT_NAME AS eventName, r.REFERANCE_NUMBER AS referenceNumber,r.EVENT_START AS eventStart,r.EVENT_END AS eventEnd,r.BUSINESS_UNIT_NAME AS unitName,r.CATEGORY_NAME as name,r.CATEGORY_CODE as code,r.APPOINTMENT_DATE_TIME AS siteVisitDate,r.CONTACT_NAME AS contactName ,r.MEETING_TYPE as meetingType,r.EVENT_TYPE as \"type\" ,r.CONTACT_NUMBER as contactNumber, r.TENANT_ID as buyerId, r.PARTICIPATION_FEES as participationFees, r.CURRENCY_CODE as currencyCode from (";
		}

		sql += "SELECT DISTINCT e.ID , e.EVENT_NAME , e.REFERANCE_NUMBER ,e.EVENT_START ,e.EVENT_END ,bu.BUSINESS_UNIT_NAME ,c.CATEGORY_NAME ,c.CATEGORY_CODE ,m.APPOINTMENT_DATE_TIME ,cm.CONTACT_NAME  ,m.MEETING_TYPE,'" + RfxTypes.RFT.name() + "' as EVENT_TYPE ,cm.CONTACT_NUMBER, e.TENANT_ID, e.PARTICIPATION_FEES, pcy.CURRENCY_CODE";
		sql += " FROM PROC_RFT_EVENTS e";
		sql += " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID LEFT OUTER JOIN PROC_RFT_EVENT_MEETINGS m ON m.EVENT_ID = e.ID LEFT OUTER JOIN PROC_RFT_MEETING_CONTACT cm ON cm.EVENT_MEETING_ID=m.ID  JOIN PROC_RFT_EVENT_INDUS_CAT ic  ON ic.EVENT_ID=e.ID JOIN PROC_INDUSTRY_CATEGORY c ON c.ID=ic.IND_CAT_ID";
		sql += " LEFT OUTER JOIN PROC_CURRENCY pcy ON e.PARTICIPATION_FEE_CURRENCY = pcy.ID";
		sql += " WHERE e.TENANT_ID = :tenantId AND e.EVENT_VISIBILITY <> :eventVisibility AND  e.STATUS = :status";
		sql += " UNION";
		sql += " SELECT DISTINCT e.ID , e.EVENT_NAME , e.REFERANCE_NUMBER ,e.EVENT_START ,e.EVENT_END ,bu.BUSINESS_UNIT_NAME ,c.CATEGORY_NAME ,c.CATEGORY_CODE ,m.APPOINTMENT_DATE_TIME ,cm.CONTACT_NAME  ,m.MEETING_TYPE ,'" + RfxTypes.RFP.name() + "' as EVENT_TYPE ,cm.CONTACT_NUMBER, e.TENANT_ID, e.PARTICIPATION_FEES, pcy.CURRENCY_CODE ";
		sql += " FROM PROC_RFP_EVENTS e";
		sql += " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID LEFT OUTER JOIN PROC_RFP_MEETINGS m ON m.EVENT_ID = e.ID LEFT OUTER JOIN PROC_RFP_MEETING_CONTACT cm ON cm.EVENT_MEETING_ID=m.ID  JOIN PROC_RFP_EVENT_INDUS_CAT ic  ON ic.EVENT_ID=e.ID JOIN PROC_INDUSTRY_CATEGORY c ON c.ID=ic.IND_CAT_ID";
		sql += " LEFT OUTER JOIN PROC_CURRENCY pcy ON e.PARTICIPATION_FEE_CURRENCY = pcy.ID";
		sql += " WHERE e.TENANT_ID = :tenantId AND e.EVENT_VISIBILITY <> :eventVisibility  AND  e.STATUS =:status";
		sql += " UNION";
		sql += " SELECT DISTINCT e.ID , e.EVENT_NAME , e.REFERANCE_NUMBER ,e.EVENT_START ,e.EVENT_END ,bu.BUSINESS_UNIT_NAME ,c.CATEGORY_NAME ,c.CATEGORY_CODE ,m.APPOINTMENT_DATE_TIME ,cm.CONTACT_NAME  ,m.MEETING_TYPE ,'" + RfxTypes.RFI.name() + "' as EVENT_TYPE ,cm.CONTACT_NUMBER, e.TENANT_ID, e.PARTICIPATION_FEES, pcy.CURRENCY_CODE";
		sql += " FROM PROC_RFI_EVENTS e";
		sql += " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID LEFT OUTER JOIN PROC_RFI_MEETINGS m ON m.EVENT_ID = e.ID LEFT OUTER JOIN PROC_RFI_MEETING_CONTACT cm ON cm.EVENT_MEETING_ID=m.ID   JOIN PROC_RFI_EVENT_INDUS_CAT ic  ON ic.EVENT_ID=e.ID JOIN PROC_INDUSTRY_CATEGORY c ON c.ID=ic.IND_CAT_ID";
		sql += " LEFT OUTER JOIN PROC_CURRENCY pcy ON e.PARTICIPATION_FEE_CURRENCY = pcy.ID";
		sql += " WHERE e.TENANT_ID = :tenantId AND e.EVENT_VISIBILITY <> :eventVisibility  AND  e.STATUS =:status";
		sql += " UNION";
		sql += " SELECT DISTINCT e.ID , e.EVENT_NAME , e.REFERANCE_NUMBER ,e.EVENT_START ,e.EVENT_END ,bu.BUSINESS_UNIT_NAME ,c.CATEGORY_NAME ,c.CATEGORY_CODE ,m.APPOINTMENT_DATE_TIME ,cm.CONTACT_NAME  ,m.MEETING_TYPE ,'" + RfxTypes.RFQ.name() + "' as EVENT_TYPE ,cm.CONTACT_NUMBER, e.TENANT_ID, e.PARTICIPATION_FEES, pcy.CURRENCY_CODE";
		sql += " FROM PROC_RFQ_EVENTS e";
		sql += " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID LEFT OUTER JOIN PROC_RFQ_MEETINGS m ON m.EVENT_ID = e.ID LEFT OUTER JOIN PROC_RFQ_MEETING_CONTACT cm ON cm.EVENT_MEETING_ID=m.ID  JOIN PROC_RFQ_EVENT_INDUS_CAT ic  ON ic.EVENT_ID=e.ID JOIN PROC_INDUSTRY_CATEGORY c ON c.ID=ic.IND_CAT_ID";
		sql += " LEFT OUTER JOIN PROC_CURRENCY pcy ON e.PARTICIPATION_FEE_CURRENCY = pcy.ID";
		sql += " WHERE e.TENANT_ID = :tenantId AND e.EVENT_VISIBILITY <> :eventVisibility AND  e.STATUS =:status";
		sql += " UNION";
		sql += " SELECT DISTINCT e.ID , e.EVENT_NAME , e.REFERANCE_NUMBER ,e.EVENT_START ,e.EVENT_END ,bu.BUSINESS_UNIT_NAME ,c.CATEGORY_NAME ,c.CATEGORY_CODE ,m.APPOINTMENT_DATE_TIME ,cm.CONTACT_NAME  ,m.MEETING_TYPE ,'" + RfxTypes.RFA.name() + "' as EVENT_TYPE ,cm.CONTACT_NUMBER, e.TENANT_ID, e.PARTICIPATION_FEES, pcy.CURRENCY_CODE";
		sql += " FROM PROC_RFA_EVENTS e";
		sql += " LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID LEFT OUTER JOIN PROC_RFA_EVENT_MEETINGS m ON m.EVENT_ID = e.ID LEFT OUTER JOIN PROC_RFA_MEETING_CONTACT cm ON cm.RFA_MEETING_ID=m.ID  JOIN PROC_RFA_EVENT_INDUS_CAT ic  ON ic.EVENT_ID=e.ID JOIN PROC_INDUSTRY_CATEGORY c ON c.ID=ic.IND_CAT_ID";
		sql += " LEFT OUTER JOIN PROC_CURRENCY pcy ON e.PARTICIPATION_FEE_CURRENCY = pcy.ID";
		sql += " WHERE e.TENANT_ID = :tenantId AND e.EVENT_VISIBILITY <> :eventVisibility AND  e.STATUS = :status";
		sql += "  ) r  where r.TENANT_ID = :tenantId";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("referanceNumber")) {
					sql += " and upper(r.REFERANCE_NUMBER) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("eventName")) {
					sql += " and upper(r.EVENT_NAME) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("type")) {
					sql += " and r.EVENT_TYPE = :type";
				} else if (cp.getData().equals("unitName")) {
					sql += " and upper(r.BUSINESS_UNIT_NAME) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("siteVisitMeetingDetails")) {
					sql += " and upper(r.CONTACT_NAME) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("industryCategoriesNames")) {
					sql += " and (upper(r.CATEGORY_NAME) like (:" + cp.getData().replace(".", "") + ") ) ";
				}
			}
		}

		if (!isCount) {
			List<OrderParameter> orderList = input.getOrder();
			if (CollectionUtil.isNotEmpty(orderList)) {
				sql += " order  by ";
				for (OrderParameter order : orderList) {
					String orderColumn = input.getColumns().get(order.getColumn()).getData();
					String dir = order.getDir();
					switch (orderColumn) {
					case "referanceNumber":
						orderColumn = "REFERANCE_NUMBER";
					case "eventName":
						orderColumn = "EVENT_NAME";
						break;
					case "eventStart":
						orderColumn = "EVENT_START";
						break;
					case "eventEndDate":
						orderColumn = "EVENT_END";
						break;
					case "unitName":
						orderColumn = "BUSINESS_UNIT_NAME";
						break;
					case "type":
						orderColumn = "EVENT_TYPE";
						break;
					case "industryCategoriesNames":
						orderColumn = "CATEGORY_NAME";
						break;
					case "siteVisitMeetingDetails":
						orderColumn = "APPOINTMENT_DATE_TIME";
						break;
					case "participationFees":
						orderColumn = "PARTICIPATION_FEES";
					default:
						break;
					}
					sql += " r." + orderColumn + " " + dir + ",";
				}
				if (sql.lastIndexOf(",") == sql.length() - 1) {
					sql = sql.substring(0, sql.length() - 1);
				}
			} else {
				sql += " order by r.EVENT_START desc";
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql, "publicEventsResult");
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter("type", cp.getSearch().getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String findTenantIdBasedOnEventIdAndEventType(String eventId, RfxTypes eventType) {
		final Query query = getEntityManager().createQuery("select r.tenantId from R" + eventType.name().substring(1, 3).toLowerCase() + "Event r where r.id = :eventId");
		query.setParameter("eventId", eventId);
		List<String> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);

	}

	@SuppressWarnings("unchecked")
	@Override
	public EventPojo getRftForPublicEventByeventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.id,r.eventId,r.referanceNumber, r.eventName, r.eventVisibility, r.eventStart, r.eventEnd, r.eventPublishDate,r.deliveryDate, r.tenantId, r.status, r.paymentTerm, rbc.currencyName,rbc.currencyCode,rob.companyName, r.participationFees, pfc.currencyCode) from RftEvent r inner join r.eventOwner as ro inner join ro.buyer as rob  left outer join r.baseCurrency as rbc left outer join r.participationFeeCurrency as pfc where r.id =:eventId");
			query.setParameter("eventId", eventId);
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

	@SuppressWarnings("unchecked")
	@Override
	public List<IndustryCategory> getIndustryCategoriesForRftById(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct ic from RftEvent as r  join r.industryCategories as ic  where r.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public EventPojo findMinMaxRatingsByEventId(String eventId, RfxTypes eventType) {
		final Query query = getEntityManager().createQuery("select distinct NEW com.privasia.procurehere.core.pojo.EventPojo(r.minimumSupplierRating,r.maximumSupplierRating) from R" + eventType.name().substring(1, 3).toLowerCase() + "Event r where r.id =:eventId");
		query.setParameter("eventId", eventId);
		List<EventPojo> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}

	}

	@Override
	public boolean isMinMaxSupplierRatingAvaliableInEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r) from RftEvent as r where r.id = :eventId and (r.minimumSupplierRating is not null or r.maximumSupplierRating is not null)");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	public boolean isIndustryCategoryMandatoryInEvent(String eventId, RfxTypes eventType) {
		final Query query = getEntityManager().createQuery("select count(r)  from R" + eventType.name().substring(1, 3).toLowerCase() + "Event r inner join r.template t where r.id = :eventId and t.supplierBasedOnCategory = true");
		query.setParameter("eventId", eventId);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Supplier findWinnerSupplier(String id) {
		Query query = getEntityManager().createNativeQuery("SELECT DISTINCT sb.SUPPLIER_ID AS id, sum(TOTAL_AFTER_TAX) AS totalAfterTax, es.SUPPLIER_BID_SUBMITTED_TIME AS createdDate FROM PROC_RFT_SUPPLIER_BQ sb JOIN PROC_RFT_EVENT_SUPPLIERS es ON sb.SUPPLIER_ID = es.SUPPLIER_ID WHERE sb.EVENT_ID =:eventId	AND es.IS_DISQUALIFY = 0 AND es.SUBMISSION_STATUS = 'COMPLETED'	AND es.EVENT_ID = sb.EVENT_ID GROUP BY sb.SUPPLIER_ID, es.SUPPLIER_BID_SUBMITTED_TIME	ORDER BY totalAfterTax ASC,es.SUPPLIER_BID_SUBMITTED_TIME", "winnerSupplierResult");
		query.setParameter("eventId", id);
		List<Supplier> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DraftEventPojo> getAllEventwithSearchFilter(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {

		List<EventStatus> list = new ArrayList<EventStatus>();
		String sql = "";

		sql += getSqlForAllEventForReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFA);

		sql += " UNION ";

		sql += getSqlForAllEventForReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFP);

		sql += " UNION ";

		sql += getSqlForAllEventForReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFQ);

		sql += " UNION ";

		sql += getSqlForAllEventForReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFT);

		sql += " UNION ";

		sql += getSqlForAllEventForReport(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFI);

		sql += " ";

		// LOG.info("====>" + sql);

		final Query query = getEntityManager().createNativeQuery(sql, "eventExcelReportData");

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

	private String getSqlForAllEventForReport(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, List<EventStatus> list, RfxTypes eventType) {
		String sql = "";

		if (eventType == RfxTypes.RFI) {
			sql += "SELECT e.ID AS id, ";
			sql += " null, " //
					+ "null,"//
					+ "aown.USER_NAME as teamMember, " //
					+ "address.TITLE as addressTitle, " //
					+ "address.ADRESS_LINE1 as line1, " //
					+ "t.MEMBER_TYPE as memberType, " //
					+ "e.EVENT_ID AS sysEventId, " //
					+ "e.EVENT_NAME AS eventName, " //
					+ "e.BUYER_SET_DECIMAL AS eventDecimal, " //
					+ "e.VIEW_SUPPLIER_NAME AS viewUnmaskSupplerName, " //
					+ "e.REFERANCE_NUMBER AS referenceNumber, " //
					+ "e.EVENT_DESCRIPTION AS eventDescription, " //
					+ "owner.USER_NAME AS ownerName, " //
					+ "e.EVENT_PUBLISH_DATE AS publishDate, " //
					+ "e.EVENT_START AS eventStart, " //
					+ "e.EVENT_END AS eventEnd, " //
					+ "e.DELIVERY_DATE AS deliveryDate, " //
					+ "e.EVENT_VISIBILITY AS visibility, "; //
			if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
				sql += "CAST(e.SUBMISSION_VALIDITY_DAYS as varchar) AS validityDays, "; //
			} else {
				sql += "CONVERT(varchar,e.SUBMISSION_VALIDITY_DAYS) AS validityDays, "; //
			}
			sql += "'" + eventType.name() + "' AS eventType, " //
					+ "currency.CURRENCY_NAME as currencyName, "//
					+ "pm.PROCUREMENT_METHOD AS procurementMethod, "//
					+ "pc.PROCUREMENT_CATEGORIES AS procurementCategories, "//
					+ "(gc.k || '-' || gc.DESCRIPTION) AS groupCode, "//
					+ "bu.DISPLAY_NAME AS unitName, " //
					+ "cc.COST_CENTER AS costCenter,  " //
					+ "e.BUDGET_AMOUNT as budgetAmount, " //
					+ "e.ESTIMATED_BUDGET as estimatedBudget, " + "e.HISTORICAL_AMOUNT as historicAmount, " //
					+ "e.PARTICIPATION_FEES as participationFees, " //
					+ "e.DEPOSIT as deposit, " //
					+ "e.AWARD_DATE as awardDate, " //
					+ "e.EVENT_PUSH_DATE as eventPushDate, " //
					+ "e.CONCLUDE_DATE as concludeDate, " //
					// + "unMaskedUser.USER_NAME as unMaskedUser, " //
					+ "template.TEMPLATE_NAME as templateName, " //
					+ "e.STATUS AS status, " //
					+ "null AS leadingSupplier, " //
					+ "null AS leadingAmount, "; //
			if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
				sql += "CAST(COUNT( es.id ) as varchar) AS invitedSupplierCount, "; //
				sql += "CAST(SUM( CASE WHEN es.SUBMISSION_STATUS = 'COMPLETED' THEN 1 ELSE 0 END ) as varchar) AS submittedSupplierCount, "; //
				sql += "CAST(SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END ) as varchar) AS acceptedSupplierCount, ";//
				sql += "(SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT eic ";
				sql += "JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, " + "CAST(SUM( CASE WHEN es.PREVIEW_TIME IS NOT NULL  THEN 1 ELSE 0 END ) as varchar) AS preViewSupplierCount,  "; //
				sql += "CAST(SUM( CASE WHEN es.SUBMISSION_STATUS = 'REJECTED' THEN 1 ELSE 0 END ) as varchar) AS rejectedSupplierCount, "; //
				sql += "CAST(SUM( CASE WHEN es.IS_DISQUALIFY = 1 THEN 1 ELSE 0 END ) as varchar) AS disqualifedSuppliers, ";//
				sql += "(SELECT string_agg(usr.USER_NAME,', ' ORDER BY usr.USER_NAME) cv FROM PROC_" + eventType.name() + "_UNMASKED_USER umu ";//
				sql += "JOIN PROC_USER usr ON umu.USER_ID = usr.ID WHERE umu.EVENT_ID = e.ID) AS unMaskedUser, "; //
			} else {
				sql += "CONVERT(varchar,COUNT( es.id )) AS invitedSupplierCount, "; //
				sql += "CONVERT(varchar,SUM( CASE WHEN es.SUBMISSION_STATUS = 'COMPLETED' THEN 1 ELSE 0 END )) AS submittedSupplierCount, "; //
				sql += "CONVERT(varchar,SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) AS acceptedSupplierCount, ";//
				sql += "(SELECT STRING_AGG(ic.CATEGORY_NAME,', ') WITHIN GROUP(ORDER BY ic.CATEGORY_NAME) csv FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT eic ";
				sql += "JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, " + "CONVERT(varchar,SUM( CASE WHEN es.PREVIEW_TIME IS NOT NULL  THEN 1 ELSE 0 END )) AS preViewSupplierCount,  "; //
				sql += "CONVERT(varchar,SUM( CASE WHEN es.SUBMISSION_STATUS = 'REJECTED' THEN 1 ELSE 0 END )) AS rejectedSupplierCount, "; //
				sql += "CONVERT(varchar,SUM( CASE WHEN es.IS_DISQUALIFY = 1 THEN 1 ELSE 0 END )) AS disqualifedSuppliers, ";//
				sql += "(SELECT STRING_AGG(usr.USER_NAME,', ') WITHIN GROUP(ORDER BY usr.USER_NAME) cv FROM PROC_" + eventType.name() + "_UNMASKED_USER umu ";//
				sql += "JOIN PROC_USER usr ON umu.USER_ID = usr.ID WHERE umu.EVENT_ID = e.ID) AS unMaskedUser, "; //
			}

			sql += "null AS auctionType " //
					+ "FROM PROC_" + eventType.name() + "_EVENTS e " //
					+ " LEFT OUTER JOIN PROC_CURRENCY currency on e.CURRENCY_ID = currency.ID "//
					+ "LEFT OUTER JOIN PROC_" + eventType.name() + "_TEAM t on e.ID = t.EVENT_ID " //
					+ "LEFT OUTER JOIN PROC_USER aown ON t.USER_ID = aown.id " //
					+ "LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID  " //
					+ "LEFT OUTER JOIN PROC_USER unMaskedUser ON e.UNMASKED_BY= unMaskedUser.id " //
					+ "LEFT OUTER JOIN PROC_RFX_TEMPLATE template ON e.TEMPLATE_ID = template.id " //
					+ "LEFT OUTER JOIN PROC_BUYER_ADDRESS address on address.ID=e.DELIVERY_ADDRESS " //
					+ "LEFT OUTER JOIN PROC_COST_CENTER cc ON e.COST_CENTER = cc.ID " //
					+ "LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES pc ON e.PROCUREMENT_CATEGORIES = pc.ID "//
					+ "LEFT OUTER JOIN PROC_PROCUREMENT_METHOD pm ON e.PROCUREMENT_METHOD = pm.ID "//
					+ "LEFT OUTER JOIN PROC_GROUP_CODE gc ON e.GROUP_CODE = gc.ID "//
					+ "JOIN PROC_USER owner ON e.EVENT_OWNER = owner.ID " //
					+ "LEFT OUTER JOIN PROC_" + eventType.name() + "_EVENT_SUPPLIERS es ON e.id = es.EVENT_ID " //
					// + "LEFT OUTER JOIN PROC_SUPPLIER ws ON e.WINNING_SUPPLIER = ws.SUPPLIER_ID " //
					+ "WHERE e.TENANT_ID = :tenantId ";

		} else {
			sql += "SELECT e.ID AS id, "//
					+ "AVG(supBq.GRAND_TOTAL) as grandTotal, "//
					+ "e.PR_PUSH_DATE as prPushDate," + "aown.USER_NAME as teamMember,"//
					+ "address.TITLE as addressTitle, "//
					+ "address.ADRESS_LINE1 as line1,"//
					+ "t.MEMBER_TYPE as memberType, "//
					+ "e.EVENT_ID AS sysEventId, "//
					+ "e.EVENT_NAME AS eventName, "//
					+ "e.BUYER_SET_DECIMAL AS eventDecimal, " //
					+ "e.VIEW_SUPPLIER_NAME AS viewUnmaskSupplerName, " //
					+ "e.REFERANCE_NUMBER AS referenceNumber, "//
					+ "e.EVENT_DESCRIPTION AS eventDescription, "//
					+ "owner.USER_NAME AS ownerName, "//
					+ "e.EVENT_PUBLISH_DATE AS publishDate, "//
					+ "e.EVENT_START AS eventStart, "//
					+ "e.EVENT_END AS eventEnd, "//
					+ "e.DELIVERY_DATE AS deliveryDate,"//
					+ "e.EVENT_VISIBILITY AS visibility, ";//
			if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
				sql += "CAST(e.SUBMISSION_VALIDITY_DAYS as varchar) AS validityDays ,'" + eventType.name() + "' AS eventType ,";//
			} else {
				sql += "CONVERT(varchar,e.SUBMISSION_VALIDITY_DAYS) AS validityDays ,'" + eventType.name() + "' AS eventType ,";//
			}
			sql += "currency.CURRENCY_NAME as currencyName ,	"//
					+ " pm.PROCUREMENT_METHOD AS procurementMethod, "//
					+ " pc.PROCUREMENT_CATEGORIES AS procurementCategories, "//
					+ " (gc.GROUP_CODE || '-' || gc.DESCRIPTION)AS groupCode, "//
					+ " bu.DISPLAY_NAME AS unitName, " //
					+ "cc.COST_CENTER AS costCenter,  " //
					+ "e.BUDGET_AMOUNT as budgetAmount, " //
					+ "e.ESTIMATED_BUDGET as estimatedBudget, "//
					+ "e.HISTORICAL_AMOUNT as historicAmount, " //
					+ "e.PARTICIPATION_FEES as participationFees ," //
					+ "e.DEPOSIT as deposit,e.AWARD_DATE as awardDate," //
					+ " e.EVENT_PUSH_DATE as eventPushDate," //
					+ "e.CONCLUDE_DATE as concludeDate," //
					// + "unMaskedUser.USER_NAME as unMaskedUser, " //
					+ "template.TEMPLATE_NAME as templateName, " //
					+ "e.STATUS AS status," //
					+ "ws.COMPANY_NAME  AS leadingSupplier, " //
					+ "e.WINNING_PRICE  AS leadingAmount, "; //
			if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
				sql += "CAST(COUNT( es.id ) as varchar) AS invitedSupplierCount ," //
						+ "CAST(SUM( CASE WHEN es.SUBMISSION_STATUS = 'COMPLETED' THEN 1 ELSE 0 END ) as varchar) AS submittedSupplierCount,	" //
						+ "CAST(SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END ) as varchar) AS acceptedSupplierCount , " //
						+ "(SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories,  "//
						+ "CAST(SUM( CASE WHEN es.PREVIEW_TIME IS NOT NULL  THEN 1 ELSE 0 END ) as varchar) AS preViewSupplierCount,  "//
						+ "CAST(SUM( CASE WHEN es.SUBMISSION_STATUS = 'REJECTED' THEN 1 ELSE 0 END ) as varchar) AS  rejectedSupplierCount,"//
						+ "CAST(SUM( CASE WHEN es.IS_DISQUALIFY = 1 THEN 1 ELSE 0 END ) as varchar) AS disqualifedSuppliers, "//
						+ "(SELECT string_agg(usr.USER_NAME,', ' ORDER BY usr.USER_NAME) cv FROM PROC_" + eventType.name() + "_UNMASKED_USER umu JOIN PROC_USER usr ON umu.USER_ID = usr.ID WHERE umu.EVENT_ID = e.ID) AS unMaskedUser, "; //

			} else {
				sql += "CONVERT(varchar,COUNT( es.id )) AS invitedSupplierCount ," //
						+ "CONVERT(varchar,SUM( CASE WHEN es.SUBMISSION_STATUS = 'COMPLETED' THEN 1 ELSE 0 END )) AS submittedSupplierCount,	" //
						+ "CONVERT(varchar,SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) AS acceptedSupplierCount , " //
						+ "(SELECT	STRING_AGG(ic.CATEGORY_NAME,', ') WITHIN GROUP(ORDER BY ic.CATEGORY_NAME) csv FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT eic " //
						+ " JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories,  "//
						+ "CONVERT(varchar,SUM( CASE WHEN es.PREVIEW_TIME IS NOT NULL  THEN 1 ELSE 0 END )) AS preViewSupplierCount,  "//
						+ "CONVERT(varchar,SUM( CASE WHEN es.SUBMISSION_STATUS = 'REJECTED' THEN 1 ELSE 0 END )) AS  rejectedSupplierCount,"//
						+ "CONVERT(varchar,SUM( CASE WHEN es.IS_DISQUALIFY = 1 THEN 1 ELSE 0 END )) AS disqualifedSuppliers," + "(SELECT STRING_AGG(usr.USER_NAME,', ') WITHIN GROUP(ORDER BY usr.USER_NAME) cv FROM PROC_" + eventType.name() + "_UNMASKED_USER umu JOIN PROC_USER usr ON umu.USER_ID = usr.ID WHERE umu.EVENT_ID = e.ID) AS unMaskedUser, "; //
			}
			if (eventType == RfxTypes.RFA) {
				sql += " e.AUCTION_TYPE AS auctionType ";
			} else {
				sql += " null AS auctionType ";
			}
			sql += "FROM PROC_" + eventType.name() + "_EVENTS e  " //
					+ " LEFT OUTER JOIN PROC_CURRENCY currency on e.CURRENCY_ID = currency.ID "//
					+ " LEFT OUTER JOIN PROC_" + eventType.name() + "_TEAM t on e.ID = t.EVENT_ID " //

					+ " LEFT OUTER JOIN PROC_USER aown ON t.USER_ID = aown.id " //
					+ "LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID  "//
					+ "LEFT OUTER JOIN PROC_USER unMaskedUser ON e.UNMASKED_BY= unMaskedUser.id   " //
					+ "LEFT OUTER JOIN PROC_RFX_TEMPLATE template ON e.TEMPLATE_ID = template.id" //
					+ " LEFT OUTER JOIN PROC_BUYER_ADDRESS address on address.ID=e.DELIVERY_ADDRESS  " //
					+ "LEFT OUTER JOIN PROC_COST_CENTER cc ON e.COST_CENTER = cc.ID JOIN PROC_USER owner ON e.EVENT_OWNER = owner.ID " //
					+ "LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES pc ON e.PROCUREMENT_CATEGORIES = pc.ID "//
					+ "LEFT OUTER JOIN PROC_PROCUREMENT_METHOD pm ON e.PROCUREMENT_METHOD = pm.ID "//
					+ "LEFT OUTER JOIN PROC_GROUP_CODE gc ON e.GROUP_CODE = gc.ID "//
					+ "LEFT OUTER JOIN PROC_" + eventType.name() + "_EVENT_SUPPLIERS es ON e.id = es.EVENT_ID  " //
					+ " LEFT OUTER JOIN PROC_" + eventType.name() + "_SUPPLIER_BQ supBq ON e.ID = supBq.EVENT_ID AND es.SUPPLIER_ID = supBq.SUPPLIER_ID AND es.SUBMISSION_STATUS = 'COMPLETED' " //
					+ "LEFT OUTER JOIN PROC_SUPPLIER ws ON e.WINNING_SUPPLIER = ws.SUPPLIER_ID  WHERE e.TENANT_ID = :tenantId ";

		}

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
				sql += " and upper(owner.USER_NAME) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				sql += " and upper(bu.DISPLAY_NAME) like :businessUnit";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				sql += " and upper(e.STATUS) like :status";
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
			sql += " and e.CREATED_DATE between :startDate and :endDate ";
		}
		if (eventType == RfxTypes.RFI) {
			sql += " GROUP BY e.ID ,e.EVENT_ID,e.EVENT_NAME,e.BUYER_SET_DECIMAL, e.VIEW_SUPPLIER_NAME, currency.CURRENCY_NAME,e.REFERANCE_NUMBER,address.TITLE ,address.ADRESS_LINE1,aown.USER_NAME ,t.MEMBER_TYPE,e.EVENT_DESCRIPTION, owner.USER_NAME, e.EVENT_PUBLISH_DATE,	e.EVENT_START, e.EVENT_END,	e.DELIVERY_DATE, e.EVENT_VISIBILITY, e.SUBMISSION_VALIDITY_DAYS, pm.PROCUREMENT_METHOD, pc.PROCUREMENT_CATEGORIES, bu.DISPLAY_NAME, cc.COST_CENTER, e.BUDGET_AMOUNT, e.ESTIMATED_BUDGET, e.HISTORICAL_AMOUNT, e.PARTICIPATION_FEES, e.DEPOSIT,e.AWARD_DATE,e.EVENT_PUSH_DATE , e.CONCLUDE_DATE,template.TEMPLATE_NAME ,e.STATUS ";
		} else {
			sql += " GROUP BY e.ID ,e.PR_PUSH_DATE,e.EVENT_ID,e.EVENT_NAME, e.BUYER_SET_DECIMAL, e.VIEW_SUPPLIER_NAME, currency.CURRENCY_NAME,e.REFERANCE_NUMBER,address.TITLE ,address.ADRESS_LINE1,aown.USER_NAME ,t.MEMBER_TYPE,e.EVENT_DESCRIPTION, owner.USER_NAME, e.EVENT_PUBLISH_DATE,	e.EVENT_START, e.EVENT_END,	e.DELIVERY_DATE, e.EVENT_VISIBILITY, e.SUBMISSION_VALIDITY_DAYS, pm.PROCUREMENT_METHOD, pc.PROCUREMENT_CATEGORIES, bu.DISPLAY_NAME, cc.COST_CENTER, e.BUDGET_AMOUNT, e.ESTIMATED_BUDGET, e.HISTORICAL_AMOUNT, e.PARTICIPATION_FEES, e.DEPOSIT,e.AWARD_DATE,e.EVENT_PUSH_DATE , e.CONCLUDE_DATE,template.TEMPLATE_NAME ,e.STATUS, ws.COMPANY_NAME , e.WINNING_PRICE " + (eventType == RfxTypes.RFA ? ", e.AUCTION_TYPE" : "");
		}
		return sql;

	}

	@Override
	public long getAllEventsCountForBuyerEventReport(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = eventsForReport(input, tenantId, startDate, endDate, true);

		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { NotAllowedException.class, Exception.class })
	public int updateEventPushedDate(String eventId) {
		String hql = "update RftEvent re set re.eventPushDate =:eventPushDate where re.id=:eventId and re.eventPushDate is null";
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
		String hql = "select AVG(bq.grandTotal) from RftSupplierBq bq where bq.rfxEvent.id = :id";
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
		String sql = "SELECT u.USER_NAME  FROM PROC_USER u RIGHT OUTER JOIN PROC_RFT_TEAM  team ON  team.USER_ID = u.ID WHERE team.MEMBER_TYPE =:memberType AND team.EVENT_ID =:eventId";
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
		String hql = "update RftEvent re set re.pushToPr =:pushToPr where re.id=:eventId and re.pushToPr is null";
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
		String hql = "update RftEvent re set re.awardDate =:awardDate where re.id=:eventId and re.awardDate is null";
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
	public int updateImmediately(String eventId, EventStatus status) {
		String hql = "update RftEvent re set re.status = :status where re.id = :eventId ";
		Query query = getEntityManager().createQuery(hql);
		query.setParameter("status", status);
		query.setParameter("eventId", eventId);
		try {
			return query.executeUpdate();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public long findTotalEventForBuyer(String tenantId, String userId) {
		String sql = "";
		if (userId == null) {
			sql = getNativeRfxCountAdminQuery();
		} else {
			sql = getNativeRfxDraftCountQuery();
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString(), EventStatus.DRAFT.toString(), EventStatus.PENDING.toString(), EventStatus.APPROVED.toString(), EventStatus.REJECTED.toString(), EventStatus.CANCELED.toString(), EventStatus.SUSPENDED.toString(), EventStatus.CLOSED.toString(), EventStatus.FINISHED.toString(), EventStatus.COMPLETE.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalAdminEventByTenantId(String tenantId) {

		String sql = " SELECT SUM(CNT) FROM ( ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFQ_EVENTS  WHERE TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFP_EVENTS  WHERE TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFI_EVENTS  WHERE TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFA_EVENTS  WHERE TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFT_EVENTS  WHERE TENANT_ID = :tenantId  ) a";

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEvent> getAllRftEventbyTenantidInitial(String tenantId, String loggedInUser) throws SubscriptionException {
		StringBuffer sb = new StringBuffer("from RftEvent re left outer join fetch re.industryCategory where re.tenantId =:tenantId");
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			sb.append(" and re.eventOwner.id = :loggedInUser");
		}
		sb.append("  order by re.createdDate desc ");
		final Query query = getEntityManager().createQuery(sb.toString());
		query.setParameter("tenantId", tenantId);
		query.setFirstResult(0);
		query.setMaxResults(10);
		if (StringUtils.checkString(loggedInUser).length() > 0) {
			query.setParameter("loggedInUser", loggedInUser);
		}
		return query.getResultList();
	}

	@Override
	public Integer getCountByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(e) from RftEvent e where e.id =:id");
		query.setParameter("id", eventId);
		try {
			Integer count = ((Number) query.getSingleResult()).intValue();
			return count;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEvent> getAllActiveEvents() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RftEvent(v.id, v.eventPublishDate, v.eventName, v.eventEnd, v.referanceNumber) from RftEvent v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.ACTIVE);
		List<RftEvent> list = query.getResultList();
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEvent> getAllApprovedEventsforJob() {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.RftEvent(v.id, v.eventPublishDate, v.eventName, v.eventEnd, v.referanceNumber) from RftEvent v where v.status = :eventStatus");
		query.setParameter("eventStatus", EventStatus.APPROVED);
		List<RftEvent> list = query.getResultList();
		return list;
	}

	@Override
	public void updateRfaForAwardPrice(String eventId, BigDecimal sumAwardPrice) {
		try {
			RftEvent rftEvent = findById(eventId);
			rftEvent.setAwardedPrice(sumAwardPrice);
			saveOrUpdate(rftEvent);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private Query eventsForReport(TableDataInput tableParams, String tenantId, Date startDate, Date endDate, boolean isCount) {

		String hql = "";

		if (isCount) {
			hql += "select count(distinct e) ";
		} else {

			hql += "select distinct NEW com.privasia.procurehere.core.pojo.DraftEventPojo(e.id, e.eventName, e.eventStart, e.eventEnd, e.type, e.status, b.unitName, e.referanceNumber, e.eventId,e.eventOwner.name, e.createdDate)";
		}
		hql += " from RfxSimpleView e  left outer join e.businessUnit b ";

		hql += " where e.tenantId=:tenantId ";

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and e.createdDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
					if (cp.getData().equals("type")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							hql += " and e.type =:types ";
						} else {
							hql += " and e.type in (:types) ";
						}
					} else if (cp.getData().equals("status")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							hql += " and e.status =:status ";
						} else {
							hql += " and e.status in (:status) ";
						}
					} else {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							if (cp.getData().equalsIgnoreCase("referenceNumber")) {
								hql += " and upper(e.referanceNumber) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("sysEventId")) {
								hql += " and upper(e.eventId) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("eventUser")) {
								hql += " and upper(e.eventOwner.name) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("unitName")) {
								hql += " and upper(e.businessUnit.unitName) like (:" + cp.getData().replace(".", "") + ") ";
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
					if (orderColumn.equalsIgnoreCase("referenceNumber")) {
						hql += " e.referanceNumber " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("sysEventId")) {
						hql += " e.eventId " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("eventUser")) {
						hql += " e.eventOwner.name " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("unitName")) {
						hql += " e.businessUnit.unitName " + dir + ",";
					}

					else {
						hql += " e." + orderColumn + " " + dir + ",";
					}

				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.createdDate desc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// Apply search filter values
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

					if (cp.getData().equals("type")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							query.setParameter("types", RfxTypes.fromStringToRfxType(cp.getSearch().getValue()));
						} else {
							List<RfxTypes> types = Arrays.asList(RfxTypes.RFT, RfxTypes.RFP, RfxTypes.RFA, RfxTypes.RFQ, RfxTypes.RFI);
							query.setParameter("types", types);
						}
					} else if (cp.getData().equals("status")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							query.setParameter("status", EventStatus.fromString(cp.getSearch().getValue()));
						} else {
							List<EventStatus> types = Arrays.asList(EventStatus.ACTIVE, EventStatus.DRAFT, EventStatus.PENDING, EventStatus.APPROVED, EventStatus.CANCELED, EventStatus.SUSPENDED, EventStatus.ACTIVE, EventStatus.CLOSED, EventStatus.FINISHED, EventStatus.COMPLETE);
							query.setParameter("status", types);
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
		// set parameter Date range
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getEventOwnerId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("select eo.id from RftEvent r inner join r.eventOwner eo where r.id =:eventId");
			query.setParameter("eventId", eventId);
			List<String> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting event owner user : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public boolean doValidateOwnerUserEnvelope(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r.id) from RftEnvelop r inner join r.rfxEvent  e inner join e.eventOwner eo  left outer join r.evaluators ee where e.id =:eventId and ( eo.id = r.leadEvaluater.id or eo.id = r.opener.id or  eo.id = ee.user.id)");
		query.setParameter("eventId", eventId);
		return (0 < ((Number) query.getSingleResult()).longValue());

	}

	@Override
	public boolean doValidateOwnerUserTeamMember(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r.id) from RftTeamMember r inner join r.event  e inner join e.eventOwner eo  left outer join r.user ee where e.id =:eventId and ( ee.id = eo.id )");
		query.setParameter("eventId", eventId);
		return (0 < ((Number) query.getSingleResult()).longValue());
	}

	@Override
	public boolean doValidateOwnerUserUnmaskUser(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r.id) from RftEvent r inner join r.eventOwner eo  left outer join r.unMaskedUser ee where r.id =:eventId and  ee.id = eo.id");
		query.setParameter("eventId", eventId);
		return (0 < ((Number) query.getSingleResult()).longValue());
	}

	@Override
	public boolean doValidateOwnerUserApprover(String eventId) {
		final Query query = getEntityManager().createQuery("select count(r.id) from RftEventApproval r inner join r.event  e inner join e.eventOwner eo  left outer join r.approvalUsers ee where e.id =:eventId and ee.user.id = eo.id");
		query.setParameter("eventId", eventId);
		return (0 < ((Number) query.getSingleResult()).longValue());
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r where r.eventId = :eventId and r.tenantId = :tenantId");
			query.setParameter("eventId", eventId);
			query.setParameter("tenantId", tenantId);
			List<RftEvent> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (Exception nr) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RftEventAwardAudit> getRftEventAwardByEventId(String eventId) {
		String hql = " from RftEventAwardAudit award where award.event.id=:eventId order by award.actionDate  desc";
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
		final Query query = getEntityManager().createQuery("select t.rfxEnvelopeReadOnly from RftEvent  event left outer join event.template t  where event.id=:eventId ");
		query.setParameter("eventId", eventId);
		return (Boolean) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEvent> getAllRftEventWhereUnMaskingUserIsNotNull() {
		String sql = "select e from RftEvent e where e.unMaskedUser is not null";
		final Query query = getEntityManager().createQuery(sql);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventUnMaskUser(String eventId) {
		Query query = getEntityManager().createQuery("update RftEvent e set e.unMaskedUser = null where e.id = :eventId").setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getIndustryCategoriesIdForRftById(String eventId, RfxTypes eventType) {
		final Query query = getEntityManager().createQuery("select distinct ic.id from " + StringUtils.capitalize(eventType.name().toLowerCase()) + "Event as r  join r.industryCategories as ic  where r.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Declaration getDeclarationForSupplierByEventId(String eventId, RfxTypes eventType) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.Declaration(d.id,d.title,d.content) from " + StringUtils.capitalize(eventType.name().toLowerCase()) + "Event as r left outer join r.supplierAcceptanceDeclaration as d where r.id =:eventId");
		query.setParameter("eventId", eventId);
		List<Declaration> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RftEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		final Query query = getEntityManager().createQuery("from RftEvaluationConclusionUser u where u.id = :evalConUserId and u.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.setParameter("evalConUserId", evalConUserId);
		List<RftEvaluationConclusionUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEvaluationConclusionUser> findEvaluationConclusionUsersByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from RftEvaluationConclusionUser u where  u.event.id = :eventId");
		query.setParameter("eventId", eventId);
		List<RftEvaluationConclusionUser> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList;
		} else {
			return null;
		}
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
		RftEvent event = getPlainEventById(eventId);

		// ph-773
		for (RftUnMaskedUser rfiUnMaskedUser : event.getUnMaskedUsers()) {
			if (rfiUnMaskedUser.getUser() != null && rfiUnMaskedUser.getUser().getId().equals(userId) && EventStatus.COMPLETE == event.getStatus() && Boolean.FALSE == rfiUnMaskedUser.getUserUnmasked()) {
				permissions.setUnMaskUser(true);
			}
		}

		for (RftEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId) && EventStatus.CLOSED == event.getStatus() && Boolean.FALSE == user.getConcluded()) {
				permissions.setEvaluationConclusionUser(true);
			}
		}

		for (RftEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
			if (user.getUser() != null && user.getUser().getId().equals(userId)) {
				permissions.setConclusionUser(true);
			}
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
		List<RftEventApproval> approvals = event.getApprovals(); // getAllApprovalsForEvent(eventId);
		for (RftEventApproval approval : approvals) {
			if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
				List<RftApprovalUser> users = approval.getApprovalUsers();
				for (RftApprovalUser user : users) {
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
		List<RftEnvelop> envelopes = rftEnvelopDao.getAllPlainEnvelopByEventId(eventId);
		if (CollectionUtil.isNotEmpty(envelopes)) {
			for (RftEnvelop envelop : envelopes) {
				List<RftEvaluatorUser> evaluators = envelop.getEvaluators();
				for (RftEvaluatorUser user : evaluators) {
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
					for (RftEnvelopeOpenerUser op : envelop.getOpenerUsers()) {
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

	@SuppressWarnings("unchecked")
	@Override
	public List<EventTeamMember> findAssociateOwnerOfRft(String id, TeamMemberType associateOwner) {
		String hql = " from RftTeamMember ptm where ptm.event.id = :id and ptm.teamMemberType =:associateOwner";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		query.setParameter("associateOwner", associateOwner);

		List<EventTeamMember> prTeamList = query.getResultList();
		return prTeamList;
	}

	@Override
	public RftEvent getEventDetailsForFeePayment(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.RftEvent(e.id, e.eventId, e.eventName, e.referanceNumber, e.participationFees, c, cb) from RftEvent e left outer join e.participationFeeCurrency c left outer join e.createdBy cb where e.id = :eventId");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (RftEvent) query.getSingleResult();
	}

	@Override
	public long findCountOfAllActivePendingEventCountForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Active: " + supplierId);
		Calendar currentDate = Calendar.getInstance();
		String sql = "select count(e) from RfxView as e ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		sql += " and e.submissionStatus in (:accepted) and e.eventStart < :currentDate ";
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.APPROVED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		query.setParameter("currentDate", currentDate.getTime());
		List<SubmissionStatusType> submissionStatus = new ArrayList<SubmissionStatusType>();
		submissionStatus.add(SubmissionStatusType.ACCEPTED);
		query.setParameter("accepted", submissionStatus);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findCountOfAllActiveSubmittedEventCountForSupplier(String supplierId, String userId) {
		LOG.info("Supplier Id Active: " + supplierId);
		Calendar currentDate = Calendar.getInstance();
		String sql = "select count(e) from RfxView as e ";
		if (userId != null) {
			sql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		sql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			sql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		sql += " and e.submissionStatus in (:accepted) and e.eventStart < :currentDate ";
		final Query query = getEntityManager().createQuery(sql);
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.APPROVED);
		query.setParameter("status", list);
		query.setParameter("id", supplierId);
		query.setParameter("currentDate", currentDate.getTime());
		List<SubmissionStatusType> submissionStatus = new ArrayList<SubmissionStatusType>();
		submissionStatus.add(SubmissionStatusType.COMPLETED);
		query.setParameter("accepted", submissionStatus);
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getOnlyActivePendingEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructQueryForActivePendingEventsForSupplier(supplierId, input, userId, false);
		return query.getResultList();
	}

	@Override
	public long findTotalCountOfActivePendingEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		final Query query = constructQueryForActivePendingEventsForSupplier(supplierId, input, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructQueryForActivePendingEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";
		Calendar currentDate = Calendar.getInstance();
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		hql += " and e.submissionStatus in (:accepted) and e.eventStart < :currentDate ";
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type = :type ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventEnd asc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.APPROVED);
		List<SubmissionStatusType> submissionStatus = new ArrayList<SubmissionStatusType>();
		submissionStatus.add(SubmissionStatusType.ACCEPTED);
		query.setParameter("accepted", submissionStatus);
		query.setParameter("currentDate", currentDate.getTime());
		query.setParameter("status", list);

		return query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxView> getOnlyActiveSubmittedEventsForSupplier(String supplierId, TableDataInput tableParams, String userId) {
		final Query query = constructQueryForActiveSubmittedEventsForSupplier(supplierId, tableParams, userId, false);
		return query.getResultList();
	}

	@Override
	public long findTotalCountOfActiveSubmittedEventsForSupplier(String supplierId, TableDataInput tableParams, String userId) {
		final Query query = constructQueryForActiveSubmittedEventsForSupplier(supplierId, tableParams, userId, true);
		return ((Number) query.getSingleResult()).longValue();
	}

	private Query constructQueryForActiveSubmittedEventsForSupplier(String supplierId, TableDataInput tableParams, String userId, boolean isCount) {

		String hql = "";
		Calendar currentDate = Calendar.getInstance();
		// If count query is enabled, then add the select count(*) clause
		if (isCount) {
			hql += "select count(distinct e) ";
		} else {
			hql += "select distinct NEW RfxView(e.id, e.eventStart, e.eventEnd, e.eventName, e.referanceNumber , e.status ,e.type )";
		}
		hql += " from RfxView e ";
		hql += " left outer join e.participationFeeCurrency as cu inner join e.eventOwner eo inner join  eo.buyer as by  ";

		if (userId != null) {
			hql += " left outer join RftSupplierTeamMember tm with e.id = tm.event.id left outer join RfpSupplierTeamMember pm with e.id = pm.event.id left outer join RfqSupplierTeamMember qm with e.id = qm.event.id left outer join RfiSupplierTeamMember im with e.id = im.event.id left outer join RfaSupplierTeamMember am with e.id = am.event.id ";
		}
		hql += " where e.status in (:status) and e.supplier.id = :id ";
		if (userId != null) {
			hql += " and (tm.user.id = :userId or pm.user.id = :userId or qm.user.id = :userId or im.user.id = :userId or am.user.id = :userId ) ";
		}
		hql += " and e.submissionStatus in (:accepted) and e.eventStart < :currentDate ";
		// Add on search filter conditions
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					hql += " and e.type = :type ";
				} else {
					hql += " and upper(e." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";

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
					hql += " e." + orderColumn + " " + dir + ",";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by e.eventEnd asc ";
			}
		}

		LOG.info("HQL : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", supplierId);
		if (userId != null) {
			query.setParameter("userId", userId);
		}

		// Apply search filter values
		for (ColumnParameter cp : tableParams.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()));
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		List<EventStatus> list = new ArrayList<EventStatus>();
		list.add(EventStatus.ACTIVE);
		list.add(EventStatus.APPROVED);
		List<SubmissionStatusType> submissionStatus = new ArrayList<SubmissionStatusType>();
		submissionStatus.add(SubmissionStatusType.COMPLETED);
		query.setParameter("accepted", submissionStatus);
		query.setParameter("currentDate", currentDate.getTime());
		query.setParameter("status", list);

		return query;
	}

	@Override
	public long findTotalEventsCountForCsv(String tenantId) {
		String sql = " SELECT SUM(CNT) FROM ( ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFQ_EVENTS  WHERE TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFP_EVENTS  WHERE TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFI_EVENTS  WHERE TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFA_EVENTS  WHERE TENANT_ID = :tenantId ";
		sql += " UNION ";
		sql += " SELECT count(*) AS CNT FROM PROC_RFT_EVENTS  WHERE TENANT_ID = :tenantId  ) a";

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> findAllActiveEventsForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {

		String sql = "";
		List<EventStatus> list = new ArrayList<EventStatus>();
		sql += getSqlForAllEventForCsv(tenantId, pageSize, pageNo, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFA);

		sql += " UNION ";

		sql += getSqlForAllEventForCsv(tenantId, pageSize, pageNo, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFP);

		sql += " UNION ";

		sql += getSqlForAllEventForCsv(tenantId, pageSize, pageNo, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFQ);

		sql += " UNION ";

		sql += getSqlForAllEventForCsv(tenantId, pageSize, pageNo, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFT);

		sql += " UNION ";

		sql += getSqlForAllEventForCsv(tenantId, pageSize, pageNo, eventIds, searchFilterEventPojo, select_all, startDate, endDate, list, RfxTypes.RFI);

		sql += " ";

		 LOG.info("====>" + sql);

		final Query query = getEntityManager().createNativeQuery(sql, "eventCsvReportData");

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

	private String getSqlForAllEventForCsv(String tenantId, int pageSize, int pageNo, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, List<EventStatus> list, RfxTypes eventType) {
		String sql = "";

		if (eventType == RfxTypes.RFI) {
			sql += "SELECT e.ID AS id, ";
			sql += " null, " //
					+ "null,"//
					+ "aown.USER_NAME as teamMember, " //
					+ "address.TITLE as addressTitle, " //
					+ "address.ADRESS_LINE1 as line1, " //
					+ "t.MEMBER_TYPE as memberType, " //
					+ "e.EVENT_ID AS sysEventId, " //
					+ "e.EVENT_NAME AS eventName, " //
					+ "e.BUYER_SET_DECIMAL AS eventDecimal, " //
					+ "e.VIEW_SUPPLIER_NAME AS viewUnmaskSupplerName, " //
					+ "e.REFERANCE_NUMBER AS referenceNumber, " //
					+ "e.EVENT_DESCRIPTION AS eventDescription, " //
					+ "owner.USER_NAME AS ownerName, " //
					+ "e.EVENT_PUBLISH_DATE AS publishDate, " //
					+ "e.EVENT_START AS eventStart, " //
					+ "e.EVENT_END AS eventEnd, " //
					+ "e.DELIVERY_DATE AS deliveryDate, " //
					+ "e.EVENT_VISIBILITY AS visibility, " //
					+ "CAST(e.SUBMISSION_VALIDITY_DAYS as varchar) AS validityDays, " //
					+ "'" + eventType.name() + "' AS eventType, " //
					+ "currency.CURRENCY_NAME as currencyName, "//
					+ "pm.PROCUREMENT_METHOD AS procurementMethod, "//
					+ "pc.PROCUREMENT_CATEGORIES AS procurementCategories, "//
					+ " (gc.GROUP_CODE || '-' || gc.DESCRIPTION ) AS groupCode , "//
					+ "bu.DISPLAY_NAME AS unitName, " //
					+ "cc.COST_CENTER AS costCenter,  " //
					+ "e.BUDGET_AMOUNT as budgetAmount, " //
					+ "e.ESTIMATED_BUDGET as estimatedBudget, " + "e.HISTORICAL_AMOUNT as historicAmount, " //
					+ "e.PARTICIPATION_FEES as participationFees, " //
					+ "e.DEPOSIT as deposit, " //
					+ "e.AWARD_DATE as awardDate, " //
					+ "e.EVENT_PUSH_DATE as eventPushDate, " //
					+ "e.CONCLUDE_DATE as concludeDate, " //
					// + "unMaskedUser.USER_NAME as unMaskedUser, " //
					+ "template.TEMPLATE_NAME as templateName, " //
					+ "e.STATUS AS status, " //
					+ "null AS leadingSupplier, " //
					+ "null AS leadingAmount, "; //
			if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
				sql += "CAST(COUNT( es.id ) as varchar) AS invitedSupplierCount, "; //
				sql += "CAST(SUM( CASE WHEN es.SUBMISSION_STATUS = 'COMPLETED' THEN 1 ELSE 0 END ) as varchar) AS submittedSupplierCount, "; //
				sql += "CAST(SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END ) as varchar) AS acceptedSupplierCount, ";//
				sql += "(SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT eic ";
				sql += "JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, " + "CAST(SUM( CASE WHEN es.PREVIEW_TIME IS NOT NULL  THEN 1 ELSE 0 END ) as varchar) AS preViewSupplierCount,  "; //
				sql += "CAST(SUM( CASE WHEN es.SUBMISSION_STATUS = 'REJECTED' THEN 1 ELSE 0 END ) as varchar) AS rejectedSupplierCount, "; //
				sql += "CAST(SUM( CASE WHEN es.IS_DISQUALIFY = 1 THEN 1 ELSE 0 END ) as varchar) AS disqualifedSuppliers, ";//
				sql += "(SELECT string_agg(usr.USER_NAME,', ' ORDER BY usr.USER_NAME) cv FROM PROC_" + eventType.name() + "_UNMASKED_USER umu ";//
				sql += "JOIN PROC_USER usr ON umu.USER_ID = usr.ID WHERE umu.EVENT_ID = e.ID) AS unMaskedUser, "; //
			} else {
				sql += "CONVERT(varchar,COUNT( es.id )) AS invitedSupplierCount, "; //
				sql += "CONVERT(varchar,SUM( CASE WHEN es.SUBMISSION_STATUS = 'COMPLETED' THEN 1 ELSE 0 END )) AS submittedSupplierCount, "; //
				sql += "CONVERT(varchar,SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) AS acceptedSupplierCount, ";//
				sql += "(SELECT STRING_AGG(ic.CATEGORY_NAME,', ') WITHIN GROUP(ORDER BY ic.CATEGORY_NAME) csv FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT eic ";
				sql += "JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories, " + "CONVERT(varchar,SUM( CASE WHEN es.PREVIEW_TIME IS NOT NULL  THEN 1 ELSE 0 END )) AS preViewSupplierCount,  "; //
				sql += "CONVERT(varchar,SUM( CASE WHEN es.SUBMISSION_STATUS = 'REJECTED' THEN 1 ELSE 0 END )) AS rejectedSupplierCount, "; //
				sql += "CONVERT(varchar,SUM( CASE WHEN es.IS_DISQUALIFY = 1 THEN 1 ELSE 0 END )) AS disqualifedSuppliers, ";//
				sql += "(SELECT STRING_AGG(usr.USER_NAME,', ') WITHIN GROUP(ORDER BY usr.USER_NAME) cv FROM PROC_" + eventType.name() + "_UNMASKED_USER umu ";//
				sql += "JOIN PROC_USER usr ON umu.USER_ID = usr.ID WHERE umu.EVENT_ID = e.ID) AS unMaskedUser, "; //
			}

			sql += "null AS auctionType " //
					+ "FROM PROC_" + eventType.name() + "_EVENTS e " //
					+ " LEFT OUTER JOIN PROC_CURRENCY currency on e.CURRENCY_ID = currency.ID "//
					+ "LEFT OUTER JOIN PROC_" + eventType.name() + "_TEAM t on e.ID = t.EVENT_ID " //
					+ "LEFT OUTER JOIN PROC_USER aown ON t.USER_ID = aown.id " //
					+ "LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID  " //
					+ "LEFT OUTER JOIN PROC_USER unMaskedUser ON e.UNMASKED_BY= unMaskedUser.id " //
					+ "LEFT OUTER JOIN PROC_RFX_TEMPLATE template ON e.TEMPLATE_ID = template.id " //
					+ "LEFT OUTER JOIN PROC_BUYER_ADDRESS address on address.ID=e.DELIVERY_ADDRESS " //
					+ "LEFT OUTER JOIN PROC_COST_CENTER cc ON e.COST_CENTER = cc.ID " //
					+ "LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES pc ON e.PROCUREMENT_CATEGORIES = pc.ID "//
					+ "LEFT OUTER JOIN PROC_PROCUREMENT_METHOD pm ON e.PROCUREMENT_METHOD = pm.ID "//
					+ "LEFT OUTER JOIN PROC_GROUP_CODE gc ON e.GROUP_CODE = gc.ID "//
					+ "JOIN PROC_USER owner ON e.EVENT_OWNER = owner.ID " //
					+ "LEFT OUTER JOIN PROC_" + eventType.name() + "_EVENT_SUPPLIERS es ON e.id = es.EVENT_ID " //
					// + "LEFT OUTER JOIN PROC_SUPPLIER ws ON e.WINNING_SUPPLIER = ws.SUPPLIER_ID " //
					+ "WHERE e.TENANT_ID = :tenantId ";

		} else {
			sql += "SELECT e.ID AS id, "//
					+ "AVG(supBq.GRAND_TOTAL) as grandTotal, "//
					+ "e.PR_PUSH_DATE as prPushDate," + "aown.USER_NAME as teamMember,"//
					+ "address.TITLE as addressTitle, "//
					+ "address.ADRESS_LINE1 as line1,"//
					+ "t.MEMBER_TYPE as memberType, "//
					+ "e.EVENT_ID AS sysEventId, "//
					+ "e.EVENT_NAME AS eventName, "//
					+ "e.BUYER_SET_DECIMAL AS eventDecimal, " //
					+ "e.VIEW_SUPPLIER_NAME AS viewUnmaskSupplerName, " //
					+ "e.REFERANCE_NUMBER AS referenceNumber, "//
					+ "e.EVENT_DESCRIPTION AS eventDescription, "//
					+ "owner.USER_NAME AS ownerName, "//
					+ "e.EVENT_PUBLISH_DATE AS publishDate, "//
					+ "e.EVENT_START AS eventStart, "//
					+ "e.EVENT_END AS eventEnd, "//
					+ "e.DELIVERY_DATE AS deliveryDate,"//
					+ "e.EVENT_VISIBILITY AS visibility, ";//
			if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
				sql += "CAST(e.SUBMISSION_VALIDITY_DAYS as varchar) AS validityDays ,'" + eventType.name() + "' AS eventType ,";//
			} else {
				sql += "CONVERT(varchar,e.SUBMISSION_VALIDITY_DAYS) AS validityDays ,'" + eventType.name() + "' AS eventType ,";//
			}
			sql += "currency.CURRENCY_NAME as currencyName ,	"//
					+ " pm.PROCUREMENT_METHOD AS procurementMethod, "//
					+ " pc.PROCUREMENT_CATEGORIES AS procurementCategories, "//
					+ " (gc.GROUP_CODE || '-' || gc.DESCRIPTION ) AS groupCode , "//
					+ " bu.DISPLAY_NAME AS unitName, " //
					+ "cc.COST_CENTER AS costCenter,  " //
					+ "e.BUDGET_AMOUNT as budgetAmount," //
					+ "e.ESTIMATED_BUDGET as estimatedBudget, " + "e.HISTORICAL_AMOUNT as historicAmount, " //
					+ "e.PARTICIPATION_FEES as participationFees ," //
					+ "e.DEPOSIT as deposit,e.AWARD_DATE as awardDate," //
					+ " e.EVENT_PUSH_DATE as eventPushDate," //
					+ "e.CONCLUDE_DATE as concludeDate," //
					// + "unMaskedUser.USER_NAME as unMaskedUser, " //
					+ "template.TEMPLATE_NAME as templateName, " //
					+ "e.STATUS AS status," //
					+ "ws.COMPANY_NAME  AS leadingSupplier, " //
					+ "e.WINNING_PRICE  AS leadingAmount, "; //
			if ("POSTGRESQL".equals(env.getRequiredProperty("db.jpa.type"))) {
				sql += "CAST(COUNT( es.id ) as varchar) AS invitedSupplierCount ," //
						+ "CAST(SUM( CASE WHEN es.SUBMISSION_STATUS = 'COMPLETED' THEN 1 ELSE 0 END ) as varchar) AS submittedSupplierCount,	" //
						+ "CAST(SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END ) as varchar) AS acceptedSupplierCount , " //
						+ "(SELECT string_agg(ic.CATEGORY_NAME,', ' ORDER BY ic.CATEGORY_NAME) csv FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT eic JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories,  "//
						+ "CAST(SUM( CASE WHEN es.PREVIEW_TIME IS NOT NULL  THEN 1 ELSE 0 END ) as varchar) AS preViewSupplierCount,  "//
						+ "CAST(SUM( CASE WHEN es.SUBMISSION_STATUS = 'REJECTED' THEN 1 ELSE 0 END ) as varchar) AS  rejectedSupplierCount,"//
						+ "CAST(SUM( CASE WHEN es.IS_DISQUALIFY = 1 THEN 1 ELSE 0 END ) as varchar) AS disqualifedSuppliers, "//
						+ "(SELECT string_agg(usr.USER_NAME,', ' ORDER BY usr.USER_NAME) cv FROM PROC_" + eventType.name() + "_UNMASKED_USER umu JOIN PROC_USER usr ON umu.USER_ID = usr.ID WHERE umu.EVENT_ID = e.ID) AS unMaskedUser, "; //

			} else {
				sql += "CONVERT(varchar,COUNT( es.id )) AS invitedSupplierCount ," //
						+ "CONVERT(varchar,SUM( CASE WHEN es.SUBMISSION_STATUS = 'COMPLETED' THEN 1 ELSE 0 END )) AS submittedSupplierCount,	" //
						+ "CONVERT(varchar,SUM( CASE WHEN ( es.SUBMISSION_STATUS = 'ACCEPTED' OR es.SUBMISSION_STATUS = 'COMPLETED' ) THEN 1 ELSE 0 END )) AS acceptedSupplierCount , " //
						+ "(SELECT	STRING_AGG(ic.CATEGORY_NAME,', ') WITHIN GROUP(ORDER BY ic.CATEGORY_NAME) csv FROM PROC_" + eventType.name() + "_EVENT_INDUS_CAT eic " //
						+ " JOIN PROC_INDUSTRY_CATEGORY ic ON eic.IND_CAT_ID = ic.ID WHERE eic.EVENT_ID = e.ID) AS eventCategories,  "//
						+ "CONVERT(varchar,SUM( CASE WHEN es.PREVIEW_TIME IS NOT NULL  THEN 1 ELSE 0 END )) AS preViewSupplierCount,  "//
						+ "CONVERT(varchar,SUM( CASE WHEN es.SUBMISSION_STATUS = 'REJECTED' THEN 1 ELSE 0 END )) AS  rejectedSupplierCount,"//
						+ "CONVERT(varchar,SUM( CASE WHEN es.IS_DISQUALIFY = 1 THEN 1 ELSE 0 END )) AS disqualifedSuppliers," + "(SELECT STRING_AGG(usr.USER_NAME,', ') WITHIN GROUP(ORDER BY usr.USER_NAME) cv FROM PROC_" + eventType.name() + "_UNMASKED_USER umu JOIN PROC_USER usr ON umu.USER_ID = usr.ID WHERE umu.EVENT_ID = e.ID) AS unMaskedUser, "; //
			}
			if (eventType == RfxTypes.RFA) {
				sql += " e.AUCTION_TYPE AS auctionType ";
			} else {
				sql += " null AS auctionType ";
			}
			sql += "FROM PROC_" + eventType.name() + "_EVENTS e  " //
					+ " LEFT OUTER JOIN PROC_CURRENCY currency on e.CURRENCY_ID = currency.ID "//
					+ " LEFT OUTER JOIN PROC_" + eventType.name() + "_TEAM t on e.ID = t.EVENT_ID " //

					+ " LEFT OUTER JOIN PROC_USER aown ON t.USER_ID = aown.id " //
					+ "LEFT OUTER JOIN PROC_BUSINESS_UNIT bu ON e.BUSINESS_UNIT_ID = bu.ID  "//
					+ "LEFT OUTER JOIN PROC_USER unMaskedUser ON e.UNMASKED_BY= unMaskedUser.id   " //
					+ "LEFT OUTER JOIN PROC_RFX_TEMPLATE template ON e.TEMPLATE_ID = template.id" //
					+ " LEFT OUTER JOIN PROC_BUYER_ADDRESS address on address.ID=e.DELIVERY_ADDRESS  " //
					+ "LEFT OUTER JOIN  PROC_COST_CENTER cc ON e.COST_CENTER = cc.ID JOIN PROC_USER owner ON e.EVENT_OWNER = owner.ID " //
					+ "LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES pc ON e.PROCUREMENT_CATEGORIES = pc.ID "//
					+ "LEFT OUTER JOIN PROC_PROCUREMENT_METHOD pm ON e.PROCUREMENT_METHOD = pm.ID "//
					+ "LEFT OUTER JOIN PROC_GROUP_CODE gc ON e.GROUP_CODE = gc.ID "//
					+ "LEFT OUTER JOIN PROC_" + eventType.name() + "_EVENT_SUPPLIERS es ON e.id = es.EVENT_ID  " //
					+ " LEFT OUTER JOIN PROC_" + eventType.name() + "_SUPPLIER_BQ supBq ON e.ID = supBq.EVENT_ID AND es.SUPPLIER_ID = supBq.SUPPLIER_ID AND es.SUBMISSION_STATUS = 'COMPLETED' " //
					+ "LEFT OUTER JOIN PROC_SUPPLIER ws ON e.WINNING_SUPPLIER = ws.SUPPLIER_ID  WHERE e.TENANT_ID = :tenantId ";

		}

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
				sql += " and upper(owner.USER_NAME) like :eventOwner";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getBusinessunit()).length() > 0) {
				sql += " and upper(bu.DISPLAY_NAME) like :businessUnit";
			}
			if (StringUtils.checkString(searchFilterEventPojo.getStatus()).length() > 0) {
				sql += " and upper(e.STATUS) like :status";
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
			sql += " and e.CREATED_DATE between :startDate and :endDate ";
		}
		if (eventType == RfxTypes.RFI) {
			sql += " GROUP BY e.ID ,e.EVENT_ID,e.EVENT_NAME,e.BUYER_SET_DECIMAL, e.VIEW_SUPPLIER_NAME, currency.CURRENCY_NAME,e.REFERANCE_NUMBER,address.TITLE ,address.ADRESS_LINE1,aown.USER_NAME ,t.MEMBER_TYPE,e.EVENT_DESCRIPTION, owner.USER_NAME, e.EVENT_PUBLISH_DATE,	e.EVENT_START, e.EVENT_END,	e.DELIVERY_DATE, e.EVENT_VISIBILITY, e.SUBMISSION_VALIDITY_DAYS , bu.DISPLAY_NAME, cc.COST_CENTER, e.BUDGET_AMOUNT,e.ESTIMATED_BUDGET,e.HISTORICAL_AMOUNT,e.PARTICIPATION_FEES, e.DEPOSIT,e.AWARD_DATE,e.EVENT_PUSH_DATE , e.CONCLUDE_DATE,template.TEMPLATE_NAME ,e.STATUS, pm.PROCUREMENT_METHOD, pc.PROCUREMENT_CATEGORIES, gc.GROUP_CODE, gc.DESCRIPTION";
		} else {
			sql += " GROUP BY e.ID ,e.PR_PUSH_DATE,e.EVENT_ID,e.EVENT_NAME, e.BUYER_SET_DECIMAL, e.VIEW_SUPPLIER_NAME, currency.CURRENCY_NAME,e.REFERANCE_NUMBER,address.TITLE ,address.ADRESS_LINE1,aown.USER_NAME ,t.MEMBER_TYPE,e.EVENT_DESCRIPTION, owner.USER_NAME, e.EVENT_PUBLISH_DATE,	e.EVENT_START, e.EVENT_END,	e.DELIVERY_DATE, e.EVENT_VISIBILITY, e.SUBMISSION_VALIDITY_DAYS , bu.DISPLAY_NAME, cc.COST_CENTER, e.BUDGET_AMOUNT,e.ESTIMATED_BUDGET, e.HISTORICAL_AMOUNT,e.PARTICIPATION_FEES, e.DEPOSIT,e.AWARD_DATE,e.EVENT_PUSH_DATE , e.CONCLUDE_DATE,template.TEMPLATE_NAME ,e.STATUS, ws.COMPANY_NAME , e.WINNING_PRICE, pm.PROCUREMENT_METHOD, pc.PROCUREMENT_CATEGORIES, gc.GROUP_CODE, gc.DESCRIPTION" + (eventType == RfxTypes.RFA ? ", e.AUCTION_TYPE" : "");
		}
		return sql;
	}

	@Override
	public long getRftEventCountByTenantId(String searchValue, String tenantId, String userId, String industryCategory) {
		StringBuffer sb = new StringBuffer("select count(distinct re.id) from RftEvent re left outer join re.industryCategories i left outer join re.industryCategory where re.tenantId =:tenantId ");
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
	public List<RftEvent> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String industryCategory, RfxTypes eventType, String tenantId, String userId) {
		StringBuffer sb = new StringBuffer("select distinct e from RftEvent e left outer join fetch e.industryCategories i left outer join fetch e.industryCategory where e.tenantId = :tenantId ");

		if (StringUtils.checkString(searchValue).length() > 0) {
			sb.append(" and (upper(e.eventName) like :searchValue or upper(e.referanceNumber) like :searchValue or upper(e.eventId) like :searchValue) ");
		}
		if (StringUtils.checkString(industryCategory).length() > 0) {
			sb.append(" and i.id = :industryCategory  ");
		}
		if (StringUtils.checkString(userId).length() > 0) {
			sb.append(" and e.eventOwner.id = :loggedInUser ");
		}
		sb.append(" order by e.createdDate desc ");

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
	public List<PendingEventPojo> findSuspendedEventsPendingApprovals(String tenantId, String userId, TableDataInput input) {

		String sql = "SELECT * FROM (";
		sql += getSuspendedEventsPendingApprovalQuery();
		sql += " ) a where 1 = 1 ";

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and \"type\"= :" + cp.getData().replace(".", "");
				} else if (cp.getData().equals("eventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "type";
				} else if (orderColumn.equals("eventId")) {
					orderColumn = "sysEventId";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.SUSPENDED.toString()));
		// query.setFirstResult(input.getStart());
		// query.setMaxResults(input.getLength());
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		List<PendingEventPojo> list = query.getResultList();
		return list;
	}

	@Override
	public long findCountOfSuspendedEventsPendingApprovals(String tenantId, String userId, TableDataInput input) {
		String sql = findCountOfSuspendedEventsPendingApprovals();
		sql += " ) a where 1 = 1 ";
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						sql += " and \"type\"= :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.SUSPENDED.toString()));
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}

		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findCountOfSuspendedEventPendingApprovals(String tenantId, String userId) {
		String sql = findCountOfSuspendedEventPendingApprovals();
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.SUSPENDED.toString()));
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAssociateOwnersOfEventsByEventId(String eventId, TeamMemberType memberType) {
		Query query = getEntityManager().createQuery("select new User(tm.user.id, tm.user.name, tm.user.communicationEmail, tm.user.emailNotifications, tm.user.loginId, tm.user.tenantId, tm.user.deviceId) from RftTeamMember tm where tm.event.id = :eventId and tm.teamMemberType =:memberType");
		query.setParameter("eventId", eventId);
		query.setParameter("memberType", memberType);
		return (List<User>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PendingEventPojo> findMyPendingPoApprovals(String tenantId, String userId, TableDataInput input) {

		String sql = "SELECT * FROM ( ";
		sql += getPendingRevisePoQuery();
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("eventName")) {
					sql += " and a.eventName like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("eventId")) {
					sql += " and a.eventId like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("creatorName")) {
					sql += " and a.createdBy like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("currency")) {
					sql += " and a.currency like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("unitName")) {
					sql += " and a.unitName like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and a." + cp.getData().replace(".", "") + " like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equals("creatorName")) {
					orderColumn = "a.createdBy";
				} else if (orderColumn.equals("eventName")) {
					orderColumn = "a.eventName";
				} else if (orderColumn.equals("eventId")) {
					orderColumn = "a.eventId";
				} else if (orderColumn.equals("currency")) {
					orderColumn = "a.currency";
				} else if (orderColumn.equals("grandTotal")) {
					orderColumn = "a.grandTotal";
				} else if (orderColumn.equals("createdDate")) {
					orderColumn = "a.createdDate";
				} else if (orderColumn.equals("unitName")) {
					orderColumn = "a.unitName";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by a.createdDate DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingRevisePoResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("erptransferrd", 0);
		query.setParameter("status", Arrays.asList(PoStatus.PENDING.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<PendingEventPojo> list = query.getResultList();
		return list;
	}

	@Override
	public long findTotalRevisePendingPoApprovals(String tenantId, String userId) {
		String sql = getRevisePendingPoCountQuery();
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalRevisePendingPoApprovals(String tenantId, String userId, TableDataInput input) {

		String sql = getMyRevisePendingPoCountQuery();
		sql += " ) a where 1 = 1 ";
		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("eventName")) {
						sql += " and a.eventName like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventId")) {
						sql += " and a.eventId like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("creatorName")) {
						sql += " and a.createdBy like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("currency")) {
						sql += " and a.currency like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and a.unitName like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and a." + cp.getData().replace(".", "") + " like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("erptransferrd", 0);
		query.setParameter("status", Arrays.asList(PrStatus.PENDING.toString()));

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void revertEventAwardStatus(String eventId) {
//		Query query = getEntityManager().createQuery("update RftEvent e set e.status = :status where e.id = :eventId").setParameter("status", EventStatus.COMPLETE).setParameter("eventId", eventId);
//		query.executeUpdate();


		Query query = getEntityManager().createQuery("update RftEvent e set e.status = :status, e.awardStatus = :awardStatus, e.awarded = false where e.id = :eventId")//
				.setParameter("status", EventStatus.COMPLETE)//
				.setParameter("awardStatus", AwardStatus.DRAFT)//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RftEventAward e set e.transferred = false where e.rfxEvent.id = :eventId")//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RftEventAwardApproval e set e.done = false, e.active = false where e.event.id = :eventId")//
				.setParameter("eventId", eventId);
		query.executeUpdate();

		query = getEntityManager().createQuery("update RftAwardApprovalUser e set e.actionDate = null, e.approvalStatus = :approvalStatus, e.remarks = null where e.approval.id in (select a.id from RftEventAwardApproval a where a.event.id = :eventId ) ")//
				.setParameter("approvalStatus", ApprovalStatus.PENDING)//
				.setParameter("eventId", eventId);
		query.executeUpdate();

	}

	@Override
	public List<PendingEventPojo> findMyPendingRfxAwardApprovals(String tenantId, String userId, TableDataInput input) {
		String sql = "SELECT * FROM (";
		sql += getAwardEventPendingApprovalQuery();
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and \"type\"= :" + cp.getData().replace(".", "");
				} else if (cp.getData().equals("eventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "type";
				} else if (orderColumn.equals("eventId")) {
					orderColumn = "sysEventId";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.COMPLETE.toString()));
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		return query.getResultList();
	}

	@Override
	public long findTotalMyPendingRfxAwardApprovals(String tenantId, String userId, TableDataInput input) {
		String sql = getMyTotalPendingRfxQuery();
		sql += " ) a where 1 = 1 ";
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						sql += " and \"type\"= :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}

		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long findTotalMyPendingAwardApprovals(String tenantId, String userId) {
		String sql = getMyPendingAwardCountQuery();
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.COMPLETE.toString()));
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getAllRftEventIdsByLoginId(String loginId) {
		System.out.println("Login Id : " + loginId);
		String sql = "select distinct new com.privasia.procurehere.core.entity.Event(re.id,  re.eventId, re.status) from RftEvent re inner join re.createdBy cr  where cr.id =:loginId";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("loginId", loginId);
		List<Event> list = query.getResultList();
		System.out.println(">>>>>>>>>>>>>>>   " + (list != null ? list.size() : 0));
		return list;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateTransferOwnerForEvent(String fromUserId, String toUserId) {

		User sourceUser = getEntityManager().find(User.class, fromUserId);
		User targetUser = getEntityManager().find(User.class, toUserId);

		Query query = getEntityManager().createNativeQuery("UPDATE PROC_RFT_EVENTS SET CREATED_BY = :toUserId, EVENT_OWNER = :toUserId  where CREATED_BY =:fromUserId");
		query.setParameter("toUserId", toUserId);
		query.setParameter("fromUserId", fromUserId);
		int recordsUpdated = query.executeUpdate();
		LOG.info("Creators transferred: {}", recordsUpdated);

		//transfer the user to team members too
		Query query2 = getEntityManager().createQuery(
				"UPDATE RftTeamMember team " +
						"SET team.user = :targetUser " +
						"WHERE team.user = :sourceUser "
		);
		query2.setParameter("sourceUser", sourceUser);
		query2.setParameter("targetUser", targetUser);
		recordsUpdated = query2.executeUpdate();
		LOG.info("Form Owners transferred: {}", recordsUpdated);

		//transfer ownership of approvals only for RFS with status of DRAFT and PENDING
		Query query5 = getEntityManager().createQuery(
				"UPDATE RftApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query5.setParameter("sourceUser", sourceUser);
		query5.setParameter("targetUser", targetUser);
		recordsUpdated = query5.executeUpdate();
		LOG.info("Approval user transferred: {}", recordsUpdated);

		Query query6 = getEntityManager().createQuery(
				"UPDATE RftUnMaskedUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query6.setParameter("sourceUser", sourceUser);
		query6.setParameter("targetUser", targetUser);
		recordsUpdated = query6.executeUpdate();
		LOG.info("Unmasked user transferred: {}", recordsUpdated);

		Query query7 = getEntityManager().createQuery(
				"UPDATE RftEvaluationConclusionUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query7.setParameter("sourceUser", sourceUser);
		query7.setParameter("targetUser", targetUser);
		recordsUpdated = query7.executeUpdate();
		LOG.info("Evaluation Conclusion user transferred: {}", recordsUpdated);

		Query query8 = getEntityManager().createQuery(
				"UPDATE RftSuspensionApprovalUser sfaur " +
						"SET sfaur.user = :targetUser " +
						"WHERE sfaur.user = :sourceUser "
		);
		query8.setParameter("sourceUser", sourceUser);
		query8.setParameter("targetUser", targetUser);
		recordsUpdated = query8.executeUpdate();
		LOG.info("Suspension Approval user transferred: {}", recordsUpdated);

		//envelope openers
		Query query9 = getEntityManager().createNativeQuery("UPDATE PROC_RFT_ENV_OPEN_USERS SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query9.setParameter("toUserId", toUserId);
		query9.setParameter("fromUserId", fromUserId);
		recordsUpdated = query9.executeUpdate();
		LOG.info("Envelope Openers transferred: {}", recordsUpdated);

		Query query10 = getEntityManager().createNativeQuery("UPDATE PROC_RFT_EVALUATOR_USER SET USER_ID = :toUserId WHERE USER_ID = :fromUserId");
		query10.setParameter("toUserId", toUserId);
		query10.setParameter("fromUserId", fromUserId);
		recordsUpdated = query10.executeUpdate();
		LOG.info("Evaluator users transferred: {}", recordsUpdated);
	}

	@Override
	public RftEvent findEventForSapByEventId(String eventId) {
		try {
			final Query query = getEntityManager().createQuery("from RftEvent r where r.eventId =:eventId");
			query.setParameter("eventId", eventId);
			List<RftEvent> uList = query.getResultList();
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
	public RftEvent findEventSorByEventId(String id) {
		try {
			// final Query query = getEntityManager().createQuery("from RfqEvent r inner join fetch r.eventOwner as eo
			// left outer join fetch r.eventBqs as bq where r.id =:eventId");
			final Query query = getEntityManager().createQuery("from RftEvent r  left outer join fetch r.eventSors as sor where r.id =:eventId order by sor.sorOrder");
			query.setParameter("eventId", id);
			@SuppressWarnings("unchecked")
			List<RftEvent> uList = query.getResultList();
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
	public List<DraftEventPojo> getAllClosedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds) {

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getRfxAdminBizUnitQuery();
		} else {

			sql += getFinishedAndClosedRfxBizUnitQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				 LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.type = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}
		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}
		LOG.info("DASHBOARD NEW SQL CLOSED STATUS >>>>>>>>> "+sql);
		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.CLOSED.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				 LOG.info("INPUT :: " + cp.getData() + "VALUE :: " + cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalClosedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {

		String sql = "SELECT count(a.id) FROM (";
		if (userId == null) {
			sql += getSimpleAdminBizUnitRfxQuery();
		} else {

			sql += getFinishedAndClosedRfxBizUnitQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";


		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("formOwner")) {
						sql += " and upper(a.createdBy) = :" + cp.getData().replace(".", "");
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.CLOSED.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					 LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					 cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		LOG.info("DASHBOARD SQL CLOSED STATUS COUNT >>>>>>>>> "+ ((Number) query.getSingleResult()).longValue());
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<DraftEventPojo> getAllSuspendedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds) {
		LOG.info("new LIST for suspended");

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getRfxAdminBizUnitQuery();
		} else {
			sql += getBizUnitRfxQueryForSuspendedEvents(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

				if (cp.getData().equals("type")) {
					sql += " and a.type = (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventName")) {
					sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("sysEventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventStart")) {
					sql += " and upper(a.eventStart) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventEnd")) {
					sql += " and upper(a.eventEnd) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventUser")) {
					sql += " and upper(a.eventUser) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("unitName")) {
					sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("referenceNumber")) {
					sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ") ";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.SUSPENDED.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalSuspendedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
		LOG.info("new count for suspended");

		// All Suspended events for Buyer
		String sql = "SELECT count(a.id) FROM (";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getBizUnitRfxQueryForSuspendedEvents(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = (:" + cp.getData().replace(".", "") + ") ";
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("sysEventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("referenceNumber")) {
						sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventUser")) {
						sql += " and upper(a.eventUser) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.SUSPENDED.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<DraftEventPojo> getAllCompletedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds) {

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getRfxAdminBizUnitQuery();
		} else {
			sql += geBizUnitCompletedRfxQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					sql += " and a.type = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}
		LOG.info("findTotalCompletedEventForBizUnit list "+sql);
		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");

		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.COMPLETE.toString()));
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}
	@Override
	public long findTotalCompletedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
		String sql = "SELECT count(a.id) FROM (";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += geBizUnitCompletedRfxQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";

		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		LOG.info("findTotalCompletedEventForBizUnit count "+sql);
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.COMPLETE.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DraftEventPojo> getAllCancelledEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds) {

		// All Suspended events for Buyer
		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getRfxAdminBizUnitQuery();
		} else {
			sql += getBizUnitCanceldRfxQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";


		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

				if (cp.getData().equals("type")) {
					sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
				} else if (cp.getData().equals("formOwner")) {
					sql += " and upper(a.createdBy) = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
//			if (sql.lastIndexOf(",") == sql.length() - 1) {
//				sql = sql.substring(0, sql.length() - 1);
//			}
			sql += " id DESC ";
		} else {
			sql += " order by eventEnd DESC, id DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "draftEventResult");

		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.CANCELED.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalCancelledEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
		// All Suspended events for Buyer
		String sql = "SELECT Count(a.id) FROM (";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getBizUnitCanceldRfxQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";


		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}

		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.CANCELED.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<PendingEventPojo> getAllPendingEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds) {

		// All Drafts events for Buyer
		String sql = "SELECT * FROM ( ";
		// String sql = "";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			//sql += getNativePendingRfxQuery();
			sql += getBizUnitRfxQueryForPendingEvents(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1";



		// sql += " order by a.EVENT_START";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					sql += " and a.TYPE = :" + cp.getData().replace(".", "");
				} else if (cp.getData().equals("eventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		// Implement order by
		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "type";
				} else if (orderColumn.equals("eventId")) {
					orderColumn = "sysEventId";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		final Query query = getEntityManager().createNativeQuery(sql, "pendingEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
				// cp.getSearch().getValue());
				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		return query.getResultList();
	}

	@Override
	public long findTotalPendingEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds) {

		// All Drafts events for Buyer
		String sql = "SELECT count(a.id) FROM ( ";
		// String sql = "";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			//sql += getNativePendingRfxQuery();
			sql += getBizUnitRfxQueryForPendingEvents(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";



		// sql += " order by a.EVENT_START";

		if (input != null) {
			// Add on search filter conditions
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		final Query query = getEntityManager().createNativeQuery(sql);
		query.setParameter("status", Arrays.asList(EventStatus.PENDING.toString()));
		query.setParameter("tenantId", tenantId);
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData(), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}

		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<ActiveEventPojo> getAllActiveEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds) {

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getRfxAdminBizUnitQuery();
		} else {
			sql += getBizUnitActiveRfxQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";


		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

				if (cp.getData().equals("type")) {
					sql += " and upper(a.type) = (:" + cp.getData().replace(".", "") + ") ";
				} else if (cp.getData().equals("eventName")) {
					sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("sysEventId")) {
					sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("referenceNumber")) {
					sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("unitName")) {
					sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
				} else if (cp.getData().equals("eventUser")) {
					sql += " and upper(a.eventUser) like (:" + cp.getData().replace(".", "") + ")";
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		sql += " AND a.eventStart > :eventStart   ";

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.type";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by a.eventStart ";
		}

		// LOG.info("SQL :: " + sql);
		final Query query = getEntityManager().createNativeQuery(sql, "activeEventResult");
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString(), EventStatus.APPROVED.toString()));
		query.setParameter("eventStart", new Date());
		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		return query.getResultList();

	}

	@Override
	public long findTotalActiveEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds) {

		String sql = "SELECT COUNT(*) FROM (";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getBizUnitActiveRfxQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";


		// Add on search filter conditions
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = (:" + cp.getData().replace(".", "") + ") ";
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("sysEventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("referenceNumber")) {
						sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("eventUser")) {
						sql += " and upper(a.eventUser) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		sql += " and  a.eventStart > :eventStart ";
		// LOG.info("SQL :: " + sql);

		final Query query = getEntityManager().createNativeQuery(sql);

		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString(), EventStatus.APPROVED.toString()));
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStart", new Date());
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<OngoingEventPojo> getAllOngoingEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds) {
		// All Ongoing events for Buyer

		String sql = "SELECT * FROM (";
		if (userId == null) {
			sql += getNativeRfxAdminQuery();
		} else {
			sql += getBizUnitOngoingdRfxQuery(userId,businessUnitIds);
		}
		sql += " ) a where 1 = 1 ";
		sql += " AND eventStart < :eventStart ";

		// Add on search filter conditions
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

				if (cp.getData().equals("type")) {
					sql += " and a.type = :" + cp.getData().replace(".", "");
				} else {
					sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
				}
			}
		}

		List<OrderParameter> orderList = input.getOrder();
		if (CollectionUtil.isNotEmpty(orderList)) {
			sql += " order by ";
			for (OrderParameter order : orderList) {
				String orderColumn = input.getColumns().get(order.getColumn()).getData();
				String dir = order.getDir();
				if (orderColumn.equalsIgnoreCase("timeLeft")) {
					orderColumn = "eventEnd";
				} else if (orderColumn.equals("type")) {
					orderColumn = "a.EVENT_TYPE";
				}
				sql += " " + orderColumn + " " + dir + ",";
			}
			if (sql.lastIndexOf(",") == sql.length() - 1) {
				sql = sql.substring(0, sql.length() - 1);
			}
		} else {
			sql += " order by eventEnd DESC";
		}

		Query query = getEntityManager().createNativeQuery(sql, "ongoingEventResult");

		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStart", new Date());
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString()));

		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {

				if (cp.getData().equals("type")) {
					query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue());
				} else {
					query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
				}
			}
		}

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());
		List<OngoingEventPojo> list = query.getResultList();

		if (CollectionUtil.isEmpty(list)) {
			return null;
		}
		List<String> ids = new ArrayList<String>();
		for (OngoingEventPojo obj : list) {
			ids.add(obj.getId());
			if (obj.getType() != null) {
				try {
					String className = null;
					// Find total unread messages count for each event
					switch (obj.getType()) {
						case RFA:
							className = "RfaEventMessage";
							break;
						case RFI:
							className = "RfiEventMessage";
							break;
						case RFP:
							className = "RfpEventMessage";
							break;
						case RFQ:
							className = "RfqEventMessage";
							break;
						case RFT:
							className = "RftEventMessage";
							break;
					}
					query = getEntityManager().createQuery("SELECT count(*) from " + className + " m where m.event.id = :eventId  and m.createdDate > :lastLoginTime and m.sentBySupplier = true");
					query.setParameter("eventId", obj.getId());
					query.setParameter("lastLoginTime", lastLoginTime);
					int unreadMessageCount = ((Number) query.getSingleResult()).intValue();
					obj.setUnreadMessageCount(unreadMessageCount);
				} catch (Exception e) {
					LOG.info("Error while fetching unread message Count :" + e.getMessage(), e);
				}
			}
		}

		// Find total bids received for each event along with unread bids
		query = getEntityManager().createQuery("SELECT e.id, e.supplierSubmittedTime from RfxView e where e.id in (:ids) and e.submitted = true");
		query.setParameter("ids", ids);
		List<Object[]> rows = query.getResultList();
		for (Object[] row : rows) {
			String id = ((String) row[0]);
			Date submittedTime = ((Date) row[1]);
			// Search original Obj in the list
			if (StringUtils.checkString(id).length() > 0) {
				OngoingEventPojo dummy = new OngoingEventPojo();
				dummy.setId(id);
				dummy = list.get(list.indexOf(dummy));
				dummy.setTotalBidCount(dummy.getTotalBidCount() + 1);
				if (submittedTime != null && lastLoginTime != null && submittedTime.after(lastLoginTime)) {
					dummy.setUnreadBidCount(dummy.getUnreadBidCount() + 1);
				}
			}
		}

		// Find total bids received for each event
		query = getEntityManager().createQuery("SELECT e.id, e.supplierEventReadTime, e.supplier.id from RfxView e where e.id in (:ids) ");
		query.setParameter("ids", ids);
		rows = query.getResultList();
		for (Object[] row : rows) {
			String id = ((String) row[0]);
			Date readTime = ((Date) row[1]);
			String supId = ((String) row[2]);
			// int count = ((Number) row[1]).intValue();
			// LOG.info("id : " + id + " - " + readTime);
			// Search original Obj in the list
			if (StringUtils.checkString(id).length() > 0) {
				OngoingEventPojo dummy = new OngoingEventPojo();
				dummy.setId(id);
				dummy = list.get(list.indexOf(dummy));
				if (StringUtils.checkString(supId).length() > 0) {
					dummy.setTotalSupplierCount(dummy.getTotalSupplierCount() + 1);
					if (readTime != null) {
						dummy.setReadSupplierCount(dummy.getReadSupplierCount() + 1);
					}
				}
			}
		}
		return list;
	}

	@Override
	public long findTotalOngoingEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds) {

		// All Ongoing events for Buyer
		String sql = "SELECT count(a.id) FROM ( ";
		if (userId == null) {
			sql += getSimpleAdminNativeRfxQuery();
		} else {
			sql += getBizUnitOngoingdRfxQuery(userId,businessUnitIds);
		}
		sql += " ) a where a.eventStart < :eventStart ";



		// Add on search filter conditions

		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						sql += " and upper(a.type) = :" + cp.getData().replace(".", "");
					} else if (cp.getData().equals("eventName")) {
						sql += " and upper(a.eventName) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("sysEventId")) {
						sql += " and upper(a.sysEventId) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("referenceNumber")) {
						sql += " and upper(a.referenceNumber) like (:" + cp.getData().replace(".", "") + ")";
					} else if (cp.getData().equals("unitName")) {
						sql += " and upper(a.unitName) like (:" + cp.getData().replace(".", "") + ")";
					} else {
						sql += " and upper(a." + cp.getData().replace(".", "") + ") like (:" + cp.getData().replace(".", "") + ") ";
					}
				}
			}
		}
		Query query = getEntityManager().createNativeQuery(sql);
		if (userId != null) {
			query.setParameter("userId", userId);
			if (businessUnitIds != null && !businessUnitIds.isEmpty()) {
				query.setParameter("businessUnitIds", businessUnitIds);
			}
		}
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventStart", new Date());
		query.setParameter("status", Arrays.asList(EventStatus.ACTIVE.toString()));
		if (input != null) {
			for (ColumnParameter cp : input.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
					// LOG.info("INPUT :: " + cp.getData() + "VALUE :: " +
					// cp.getSearch().getValue());
					if (cp.getData().equals("type")) {
						query.setParameter(cp.getData().replace(".", ""), RfxTypes.valueOf(cp.getSearch().getValue()).getValue().toUpperCase());
					} else {
						query.setParameter(cp.getData().replace(".", ""), "%" + cp.getSearch().getValue().toUpperCase() + "%");
					}
				}
			}
		}
		return ((Number) query.getSingleResult()).longValue();
	}

}
