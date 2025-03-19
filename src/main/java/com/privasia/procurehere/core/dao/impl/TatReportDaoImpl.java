/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.privasia.procurehere.core.enums.RfxTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.TatReportDao;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.TatReport;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.OrderParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 */
@Repository("tatReportDao")
public class TatReportDaoImpl extends GenericDaoImpl<TatReport, String> implements TatReportDao {

	private static final Logger LOG = LogManager.getLogger(TatReportDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<TatReport> getRfsForTatReportListByRfsFormIdAndTenantId(String formId, String tenantId) {
		final Query query = getEntityManager().createQuery("from TatReport tr where upper(tr.formId) =:formId and tr.tenantId =:tenantId ");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("formId", formId);
		query.setParameter("tenantId", tenantId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TatReport> getEventsDataForTatReportListByEventIdAndTenantId(String eventId, String tenantId) {
		final Query query = getEntityManager().createQuery("from TatReport tr where upper(tr.eventId) =:eventId and tr.tenantId =:tenantId ");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", tenantId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isRfsExistsByFormIdAndTenantId(String formId, String tenantId) {

		StringBuilder hsql = new StringBuilder("from TatReport as sf where upper(sf.formId) = upper(:formId) and sf.tenantId =:tenantId ");

		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("formId", formId);
		List<SourcingFormRequest> stList = query.getResultList();
		return CollectionUtil.isNotEmpty(stList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReport getEventsDataForTatReportListByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId) {
		final Query query = getEntityManager().createQuery("from TatReport tr where upper(tr.eventId) =:eventId and tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventId", eventId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReport getRfsForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("from TatReport tr where tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId ");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TatReport> getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("from TatReport tr where tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId ");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReport getEventsDataForTatReportByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("from TatReport tr where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TatReport> getTatReportDataForListByTenantId(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructQueryForTatReportList(input, tenantId, startDate, endDate, false);

		query.setFirstResult(input.getStart());
		query.setMaxResults(input.getLength());

		return query.getResultList();
	}

	@Override
	public long getTatReportsCountForListByTenantId(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		final Query query = constructQueryForTatReportList(input, tenantId, startDate, endDate, true);

		try {
			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			return 0;
		}
	}

	private Query constructQueryForTatReportList(TableDataInput tableParams, String tenantId, Date startDate, Date endDate, boolean isCount) {

		String hql = "";

		if (isCount) {
			hql += "select count(distinct tr)";
		} else {

			hql += "select new com.privasia.procurehere.core.entity.TatReport(tr.id, tr.formId, tr.requestStatus, tr.createdDate, tr.lastApprovedDate, tr.eventId, tr.status, tr.eventCreatedDate, tr.eventLastApprovedDate, tr.eventStart, tr.eventEnd, tr.eventFirstMeetingDate, tr.evaluationCompletedDate, tr.unmskingDate, tr.eventConcludeDate, tr.eventAwardDate, tr.sapPocreatedDate, tr.paperApprovalDaysCount, tr.overallTat, tr.requestGeneratedId, tr.eventGeneratedId)";

			// hql += "select distinct tr ";
		}
		hql += " from TatReport tr";

		hql += " where tr.tenantId=:tenantId ";

		// search with Date range
		if (startDate != null && endDate != null) {
			hql += " and tr.createdDate between :startDate and :endDate ";
		}

		// Add on search filter conditions
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {
					if (cp.getData().equals("requestStatus")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							hql += " and tr.requestStatus in (:requestStatus) ";
						} else {
							hql += " and tr.requestStatus =:requestStatus ";
						}
					} else if (cp.getData().equals("status")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							hql += " and tr.status in (:status) ";
						}
					} else {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							if (cp.getData().equalsIgnoreCase("formId")) {
								hql += " and upper(tr.formId) like (:" + cp.getData().replace(".", "") + ") ";
							} else if (cp.getData().equalsIgnoreCase("eventId")) {
								hql += " and upper(tr.eventId) like (:" + cp.getData().replace(".", "") + ") ";
							} else {
								hql += " and upper(tr." + cp.getData() + ") like (:" + cp.getData().replace(".", "") + ") ";
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
					if (orderColumn.equalsIgnoreCase("createdDate")) {
						hql += " tr.createdDate " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("lastApprovedDate")) {
						hql += " tr.lastApprovedDate " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("formId")) {
						hql += " tr.formId " + dir + ",";
					} else if (orderColumn.equalsIgnoreCase("eventId")) {
						hql += " tr.eventId " + dir + ",";
					} else {
						hql += " tr." + orderColumn + " " + dir + ",";
					}
					hql += " tr.id desc";
				}
				if (hql.lastIndexOf(",") == hql.length() - 1) {
					hql = hql.substring(0, hql.length() - 1);
				}
			} else {
				hql += " order by tr.createdDate desc , tr.id desc";
			}
		}

		// LOG.info("HQL ...... : " + hql);

		final Query query = getEntityManager().createQuery(hql.toString());

		// Apply search filter values
		if (tableParams != null) {
			for (ColumnParameter cp : tableParams.getColumns()) {
				if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null) {

					if (cp.getData().equals("requestStatus")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							if (StringUtils.checkString(cp.getSearch().getValue()).equals("ALL")) {
								query.setParameter("requestStatus", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.FINISHED, SourcingFormStatus.APPROVED, SourcingFormStatus.PENDING, SourcingFormStatus.CANCELED, SourcingFormStatus.CONCLUDED));
							} else {
								query.setParameter("requestStatus", SourcingFormStatus.fromString(cp.getSearch().getValue()));
							}
						} else {
							List<SourcingFormStatus> requestStatus = Arrays.asList(SourcingFormStatus.FINISHED);
							query.setParameter("requestStatus", requestStatus);
						}
					} else if (cp.getData().equals("status")) {
						if (StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
							if (StringUtils.checkString(cp.getSearch().getValue()).equals("ALL")) {
								query.setParameter("status", Arrays.asList(EventStatus.DRAFT, EventStatus.APPROVED, EventStatus.ACTIVE, EventStatus.PENDING, EventStatus.CLOSED, EventStatus.COMPLETE, EventStatus.FINISHED, EventStatus.CANCELED, EventStatus.SUSPENDED));
							} else {
								query.setParameter("status", EventStatus.fromString(cp.getSearch().getValue()));
							}
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
	public long findTotalTatReportsForListByTenantId(String tenantId) {
		String sql = "";
		sql += " select count(distinct tr) from TatReport tr where tr.tenantId =:tenantId ";

		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("tenantId", tenantId);
		// query.setParameter("reqStatus", SourcingFormStatus.FINISHED);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TatReport> findAllTatReportForTenantIdForCsv(int pageSize, int pageNo, TatReportPojo tatReport) {

		String sql = "";
		sql += " from TatReport tr where tr.tenantId =:tenantId";

		// search with Date range

		if (tatReport.getEventStart() != null && tatReport.getEventEnd() != null) {
			sql += " and tr.createdDate between :startDate and :endDate";
		}
		if (StringUtils.checkString(tatReport.getSourcingrequestid()).length() > 0) {
			sql += " and upper(tr.formId) like :formId";
		}
		if (StringUtils.checkString(tatReport.getEventid()).length() > 0) {
			sql += " and upper(tr.eventId) like :eventId";
		}

		if (tatReport.getRequeststatus() != null && tatReport.getRequeststatus().toString().equals("ALL")) {
			sql += " and upper(tr.requestStatus) in (:reqStatus)";
		} else {
			sql += " and upper(tr.requestStatus) like :reqStatus";
		}

		if (SourcingFormStatus.FINISHED == tatReport.getRequeststatus()) {
			if (tatReport.getEventstatus() != null) {
				sql += " and upper(tr.status) like :status";
			} else {
				sql += " and upper(tr.status) in (:status)";
			}
		}

		LOG.info("Query ===========> " + sql);

		final Query query = getEntityManager().createQuery(sql);

		if (StringUtils.checkString(tatReport.getSourcingrequestid()).length() > 0) {
			query.setParameter("formId", "%" + tatReport.getSourcingrequestid().toUpperCase() + "%");
		}
		if (StringUtils.checkString(tatReport.getEventid()).length() > 0) {
			query.setParameter("eventId", "%" + tatReport.getEventid().toUpperCase() + "%");
		}
		if (tatReport.getRequeststatus() != null && tatReport.getRequeststatus().toString().equals("ALL")) {
			query.setParameter("reqStatus", Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.FINISHED, SourcingFormStatus.APPROVED, SourcingFormStatus.PENDING, SourcingFormStatus.CANCELED, SourcingFormStatus.CONCLUDED));
		} else {
			query.setParameter("reqStatus", tatReport.getRequeststatus());
		}

		if (SourcingFormStatus.FINISHED == tatReport.getRequeststatus()) {
			if (tatReport.getEventstatus() != null) {
				query.setParameter("status", Arrays.asList(tatReport.getEventstatus()));
			} else {
				query.setParameter("status", Arrays.asList(EventStatus.DRAFT, EventStatus.APPROVED, EventStatus.ACTIVE, EventStatus.PENDING, EventStatus.CLOSED, EventStatus.COMPLETE, EventStatus.FINISHED, EventStatus.CANCELED, EventStatus.SUSPENDED));
			}
		}
		// set parameter Date range
		if (tatReport.getEventStart() != null && tatReport.getEventEnd() != null) {
			query.setParameter("startDate", tatReport.getEventStart());
			query.setParameter("endDate", tatReport.getEventEnd());
		}
		query.setParameter("tenantId", tatReport.getTenantId());
		query.setFirstResult((pageSize * pageNo));
		query.setMaxResults(pageSize);

		List<TatReport> eventList = query.getResultList();

		return eventList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReport getRfsForTatReportByRfsIDAndFormIdAndTenantId(String formId, String tenantId, String requestGeneratedId) {
		final Query query = getEntityManager().createQuery("from TatReport tr where upper(tr.formId) =:formId and tr.tenantId =:tenantId and tr.requestGeneratedId =:requestGeneratedId");
		LOG.info("Query ======>>>>>" + query);
		query.setParameter("formId", formId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("requestGeneratedId", requestGeneratedId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void updateTatReportEventsStartDate(String eventId, EventStatus status) {
		LOG.info("Event id : " + eventId + " Status : " + status);
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.status =:status where tr.eventGeneratedId =:eventId ");
		query.setParameter("status", status);
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReport getTatReportDataForTatReportByEventIdAndTenantId(String eventId, String tenantId) {
		final Query query = getEntityManager().createQuery("from TatReport tr where upper(tr.eventId) =:eventId and tr.tenantId =:tenantId ");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", tenantId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void updateTatReportOnEventEnd(String id, BigDecimal eventOpenDuration, Integer invitedSupplierCount, Integer acceptedSupplierCount, Integer submitedSupplierCount) {
		LOG.info("id : " + id + " EventOpenDuration : " + eventOpenDuration + " InvitedSupplierCount : " + invitedSupplierCount + " AcceptedSupplierCount : " + acceptedSupplierCount + " SubmitedSupplierCount : " + submitedSupplierCount);
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.eventOpenDuration =:eventOpenDuration, tr.invitedSupplierCount = :invitedSupplierCount, tr.acceptedSupplierCount =:acceptedSupplierCount, tr.submitedSupplierCount = :submitedSupplierCount  where tr.id =:id ");
		query.setParameter("eventOpenDuration", eventOpenDuration);
		query.setParameter("invitedSupplierCount", invitedSupplierCount);
		query.setParameter("acceptedSupplierCount", acceptedSupplierCount);
		query.setParameter("submitedSupplierCount", submitedSupplierCount);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportOnSapResponse(String eventId, EventStatus status, String tenantId) {
		LOG.info("Event id : " + eventId + " Status : " + status);

		StringBuffer sql = new StringBuffer("update TatReport tr set ");
		sql.append(" tr.status = :status, tr.eventConcludeDate = null, tr.eventFinishedDate = null, tr.paperApprovalDaysCount = null, ");
		sql.append(" tr.overallTat = null,tr.awardedSupplier = null, tr.awardValue = null, tr.eventAwardDate = null ");
		sql.append(" where tr.eventGeneratedId = :eventId and tr.tenantId = :tenantId ");

		Query query = getEntityManager().createQuery(sql.toString());
		query.setParameter("status", status);
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportSapPrId(String requestGeneratedId, String sapPrId, String tenantId) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.sapPrId =:sapPrId where tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("sapPrId", sapPrId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportSapoDetails(String id, String sapPoId, Date sapPocreatedDate, BigDecimal overallTat) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.sapPoId =:sapPoId, tr.sapPocreatedDate =:sapPocreatedDate, tr.overallTat =:overallTat where tr.id =:id ");
		query.setParameter("sapPoId", sapPoId);
		query.setParameter("sapPocreatedDate", sapPocreatedDate);
		query.setParameter("overallTat", overallTat);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportApproved(String id, Date eventFirstApprovedDate, Date eventLastApprovedDate, BigDecimal eventApprovalDaysCount, EventStatus status) {
		StringBuilder hql = new StringBuilder("update TatReport tr set ");
		if (eventFirstApprovedDate == null) {
			hql.append(" tr.eventFirstApprovedDate = :eventFirstApprovedDate, ");
		}
		if (eventLastApprovedDate == null) {
			hql.append(" tr.eventLastApprovedDate = :eventLastApprovedDate, ");
		}
		if (eventApprovalDaysCount != null) {
			hql.append(" tr.eventApprovalDaysCount = :eventApprovalDaysCount,");
		}
		hql.append(" tr.status = :status  where tr.id =:id ");

		Query query = getEntityManager().createQuery(hql.toString());
		if (eventFirstApprovedDate == null) {
			query.setParameter("eventFirstApprovedDate", new Date());
		}
		if (eventLastApprovedDate == null) {
			query.setParameter("eventLastApprovedDate", new Date());
		}
		if (eventApprovalDaysCount != null) {
			query.setParameter("eventApprovalDaysCount", eventApprovalDaysCount);
		}
		query.setParameter("status", status);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportEventFirstApprovedDate(String requestGeneratedId, String eventId, String tenantId, Date eventFirstApprovedDate) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.eventFirstApprovedDate =:eventFirstApprovedDate where tr.eventGeneratedId =:eventId and tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId  ");
		query.setParameter("eventFirstApprovedDate", eventFirstApprovedDate);
		query.setParameter("eventId", eventId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);

		query.executeUpdate();
	}

	@Override
	public void updateTatReportEventRejection(String requestGeneratedId, String eventId, String tenantId, Date eventRejectedDate, EventStatus status) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.eventRejectedDate =:eventRejectedDate, tr.status = :status  where upper(tr.eventId) =:eventId and tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventRejectedDate", eventRejectedDate);
		query.setParameter("status", status);
		query.setParameter("eventId", eventId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();

	}

	@Override
	public void updateTatReportApprovedAndApprovalDaysCountAndLastApprovedDate(String eventGeneratedId, String tenantId, Date eventLastApprovedDate, BigDecimal eventApprovalDaysCount, EventStatus status) {
		// tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId
		StringBuilder hql = new StringBuilder("update TatReport tr set ");
		hql.append(" tr.eventLastApprovedDate = :eventLastApprovedDate, ");
		hql.append(" tr.eventApprovalDaysCount = :eventApprovalDaysCount,");
		hql.append(" tr.status = :status  where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId  ");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventLastApprovedDate", eventLastApprovedDate);
		query.setParameter("eventApprovalDaysCount", eventApprovalDaysCount);
		query.setParameter("status", status);
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportReqApprovedAndApprovalDaysCountAndLastApprovedDate(String id, Date lastApprovedDate, BigDecimal reqApprovalDaysCount, SourcingFormStatus requestStatus) {
		StringBuilder hql = new StringBuilder("update TatReport tr set ");
		hql.append(" tr.lastApprovedDate = :lastApprovedDate, ");
		hql.append(" tr.reqApprovalDaysCount = :reqApprovalDaysCount,");
		hql.append(" tr.requestStatus = :requestStatus  where tr.id =:id ");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("lastApprovedDate", lastApprovedDate);
		query.setParameter("reqApprovalDaysCount", reqApprovalDaysCount);
		query.setParameter("requestStatus", requestStatus);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportReqRejection(String id, Date reqRejectedDate, SourcingFormStatus requestStatus) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.reqRejectedDate =:reqRejectedDate, tr.requestStatus = :requestStatus  where tr.id =:id ");
		query.setParameter("reqRejectedDate", reqRejectedDate);
		query.setParameter("requestStatus", requestStatus);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportReqFirstApprovedDate(String requestGeneratedId, String tenantId, Date firstApprovedDate) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.firstApprovedDate =:firstApprovedDate where tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId");
		query.setParameter("firstApprovedDate", firstApprovedDate);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportReqApprovedAndApprovalDaysCountAndFirstAndLastApprovedDate(String id, Date lastApprovedDate, BigDecimal reqApprovalDaysCount, Date firstApprovedDate) {
		StringBuilder hql = new StringBuilder("update TatReport tr set ");
		hql.append(" tr.lastApprovedDate = :lastApprovedDate, ");
		hql.append(" tr.reqApprovalDaysCount = :reqApprovalDaysCount,");
		hql.append(" tr.firstApprovedDate =:firstApprovedDate  where tr.id =:id ");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("lastApprovedDate", lastApprovedDate);
		query.setParameter("reqApprovalDaysCount", reqApprovalDaysCount);
		query.setParameter("firstApprovedDate", firstApprovedDate);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportAwardDetails(String id, String awardedSupplier, String awardValue, Date eventAwardDate, EventStatus status, Date eventConcludeDate, Date eventFinishedDate, BigDecimal paperApprovalDaysCount) {
		StringBuilder hql = new StringBuilder("update TatReport tr set ");
		hql.append(" tr.awardedSupplier = :awardedSupplier, ");
		hql.append(" tr.awardValue = :awardValue,");
		hql.append(" tr.eventAwardDate = :eventAwardDate,");
		hql.append(" tr.status = :status,");
		hql.append(" tr.eventConcludeDate = :eventConcludeDate,");
		hql.append(" tr.eventFinishedDate = :eventFinishedDate");
		if (paperApprovalDaysCount != null) {
			hql.append(", tr.paperApprovalDaysCount = :paperApprovalDaysCount ");
		}
		hql.append(" where tr.id =:id ");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("awardedSupplier", awardedSupplier);
		query.setParameter("awardValue", awardValue);
		query.setParameter("eventAwardDate", eventAwardDate);
		query.setParameter("status", status);
		query.setParameter("eventConcludeDate", eventConcludeDate);
		query.setParameter("eventFinishedDate", eventFinishedDate);
		if (paperApprovalDaysCount != null) {
			query.setParameter("paperApprovalDaysCount", paperApprovalDaysCount);
		}

		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportEvaluationCompleted(String eventGeneratedId, String tenantId, Date evaluationCompleted, EventStatus status) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.evaluationCompletedDate =:evaluationCompleted, tr.status = :status  where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("evaluationCompleted", evaluationCompleted);
		query.setParameter("status", status);
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	// @Override
	// public void updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(String id, EventStatus status,
	// Date eventConcludeDate, Date eventFinishedDate, BigDecimal paperApprovalDaysCount) {
	//
	// StringBuilder hql = new StringBuilder("update TatReport tr set ");
	// hql.append(" tr.status = :status,");
	// hql.append(" tr.eventConcludeDate = :eventConcludeDate,");
	// hql.append(" tr.eventFinishedDate = :eventFinishedDate");
	// if (paperApprovalDaysCount != null) {
	// hql.append(", tr.paperApprovalDaysCount = :paperApprovalDaysCount ");
	// }
	// hql.append(" where tr.id =:id ");
	//
	// Query query = getEntityManager().createQuery(hql.toString());
	// query.setParameter("status", status);
	// query.setParameter("eventConcludeDate", eventConcludeDate);
	// query.setParameter("eventFinishedDate", eventFinishedDate);
	// if (paperApprovalDaysCount != null) {
	// query.setParameter("paperApprovalDaysCount", paperApprovalDaysCount);
	// }
	//
	// query.setParameter("id", id);
	// query.executeUpdate();
	// }

	@Override
	public void updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(String eventId, EventStatus status) {
		Date now = new Date();
		StringBuilder hql = new StringBuilder("update TatReport tr set ");
		hql.append(" tr.status = :status,");
		hql.append(" tr.eventConcludeDate = :eventConcludeDate,");
		hql.append(" tr.eventFinishedDate = :eventFinishedDate,");
		hql.append(" tr.paperApprovalDaysCount = cast((:currentdate - cast(tr.evaluationCompletedDate as date)) as integer) ");
		hql.append(" where tr.eventGeneratedId = :eventId ");

		System.out.println(">>>>> " + hql.toString());
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("status", status);
		query.setParameter("eventConcludeDate", now);
		query.setParameter("eventFinishedDate", now);
		query.setParameter("currentdate", now);

		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportEventFirstMeetingDate(String id, Date eventFirstMeetingDate) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.eventFirstMeetingDate =:eventFirstMeetingDate where tr.id =:id ");
		query.setParameter("eventFirstMeetingDate", eventFirstMeetingDate);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public long updateTatReportReqStatus(String formId, String tenantId, String requestGeneratedId, SourcingFormStatus requestStatus) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.requestStatus = :requestStatus  where upper(tr.formId) =:formId and tr.tenantId =:tenantId and tr.requestGeneratedId =:requestGeneratedId");
		query.setParameter("requestStatus", requestStatus);
		query.setParameter("formId", formId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		return query.executeUpdate();
	}

	@Override
	public void updateTatReportEventStatus(String eventId, String requestGeneratedId, String tenantId, EventStatus status) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.status =:status where upper(tr.eventId) =:eventId and tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId  ");
		query.setParameter("status", status);
		query.setParameter("eventId", eventId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportEventDetails(String id, String eventGeneratedId, Date eventStart, Date eventEnd, Date eventPublishDate, String eventName, String referanceNumber) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.eventGeneratedId = :eventGeneratedId, tr.eventStart = :eventStart, tr.eventEnd = :eventEnd, tr.eventPublishDate = :eventPublishDate, tr.eventName = :eventName,tr.referanceNumber = :referanceNumber  where tr.id =:id ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("eventStart", eventStart);
		query.setParameter("eventEnd", eventEnd);
		query.setParameter("eventPublishDate", eventPublishDate);
		query.setParameter("eventName", eventName);
		query.setParameter("referanceNumber", referanceNumber);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportFirstEnvelopOpenDate(String eventGeneratedId, String tenantId, EventStatus status) {
		StringBuilder hql = new StringBuilder(" update TatReport tr set ");
		hql.append(" tr.firstEnvelopOpenDate =:firstEnvelopOpenDate, ");
		hql.append(" tr.status = :status  where  tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId and tr.firstEnvelopOpenDate is null ");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("firstEnvelopOpenDate", new Date());
		query.setParameter("status", status);
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportUnmskingDate(String id, Date unmskingDate) {
		StringBuilder hql = new StringBuilder(" update TatReport tr set tr.unmskingDate = :unmskingDate  where tr.id =:id ");
		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("unmskingDate", unmskingDate);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportFinishDateAndDecimalAndStatus(String id, Date eventFinishDate, String eventDecimal, EventStatus status) {

		StringBuilder hql = new StringBuilder("update TatReport tr set tr.eventFinishDate = :eventFinishDate, tr.eventDecimal = :eventDecimal, tr.status = :status");
		hql.append(" where tr.id =:id ");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("eventFinishDate", eventFinishDate);
		query.setParameter("eventDecimal", eventDecimal);
		query.setParameter("status", status);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportReqFinishDateAndStatus(String id, Date finishDate, SourcingFormStatus requestStatus) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.finishDate =:finishDate, tr.requestStatus = :requestStatus  where tr.id =:id ");
		query.setParameter("finishDate", finishDate);
		query.setParameter("requestStatus", requestStatus);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportReqDetails(String id, String requestGeneratedId, String formId, String sourcingFormName, String formDescription, String businessUnit, String costCenter, //
			String requestOwner, String groupCode, BigDecimal availableBudget, BigDecimal estimatedBudget, Date createdDate, String procurementMethod, //
			String procurementCategories, String baseCurrency, SourcingFormStatus requestStatus, String reqDecimal, String tenantId) {
		StringBuilder hql = new StringBuilder("update TatReport tr set tr.requestGeneratedId = :requestGeneratedId, tr.formId = :formId, tr.sourcingFormName = :sourcingFormName ");
		hql.append(", tr.formDescription = :formDescription , tr.businessUnit = :businessUnit, tr.costCenter = :costCenter ");
		hql.append(", tr.requestOwner = :requestOwner, tr.groupCode = :groupCode, tr.availableBudget = :availableBudget, tr.estimatedBudget = :estimatedBudget, tr.createdDate = :createdDate, tr.procurementMethod = :procurementMethod ");
		hql.append(", tr.procurementCategories = :procurementCategories, tr.baseCurrency = :baseCurrency, tr.requestStatus = :requestStatus, tr.reqDecimal = :reqDecimal, tr.tenantId = :tenantId where tr.id = :id ");

		Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("formId", formId);
		query.setParameter("sourcingFormName", sourcingFormName);
		query.setParameter("formDescription", formDescription);
		query.setParameter("businessUnit", businessUnit);
		query.setParameter("costCenter", costCenter);
		query.setParameter("requestOwner", requestOwner);
		query.setParameter("groupCode", groupCode);
		query.setParameter("availableBudget", availableBudget);
		query.setParameter("estimatedBudget", estimatedBudget);
		query.setParameter("createdDate", createdDate);
		query.setParameter("procurementMethod", procurementMethod);
		query.setParameter("procurementCategories", procurementCategories);
		query.setParameter("baseCurrency", baseCurrency);
		query.setParameter("requestStatus", requestStatus);
		query.setParameter("reqDecimal", reqDecimal);
		query.setParameter("tenantId", tenantId);
		query.setParameter("id", id);
		query.executeUpdate();

	}

	@Override
	public TatReport getEventDetailsForTatReportByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.TatReport(tr.id, tr.eventStart, tr.eventEnd) from TatReport tr where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReportPojo geTatReportByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.TatReportPojo(tr.id, tr.formId, tr.eventId, tr.tenantId, tr.requestGeneratedId, tr.awardedSupplier, tr.sapPoId, tr.lastApprovedDate, tr.paperApprovalDaysCount, tr.eventFirstApprovedDate, tr.eventLastApprovedDate,tr.eventFinishDate) from TatReport tr where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		List<TatReportPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReportPojo getTatReportListByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.TatReportPojo(tr.id, tr.formId, tr.eventId, tr.tenantId, tr.requestGeneratedId, tr.awardedSupplier, tr.sapPoId, tr.lastApprovedDate, tr.paperApprovalDaysCount, tr.eventFirstApprovedDate, tr.eventLastApprovedDate,tr.eventFinishDate) from TatReport tr where upper(tr.eventId) =:eventId and tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventId", eventId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);

		List<TatReportPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Date getEventFinishDateByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select tr.eventFinishDate from TatReport tr where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		List<Date> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public long getRfsCountForTatReportListByRfsFormIdAndIdAndTenantId(String requestGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select count(*) from TatReport tr where tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long getEventCountByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select count(*) from TatReport tr where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long getRfsCountForTatReportByRfsIDAndFormIdAndTenantId(String formId, String tenantId, String requestGeneratedId) {
		final Query query = getEntityManager().createQuery("select count(*)  from TatReport tr where upper(tr.formId) =:formId and tr.tenantId =:tenantId and tr.requestGeneratedId =:requestGeneratedId");
		query.setParameter("formId", formId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReportPojo geTatReportEvaluationCompletedAndFirstMeetingDateByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.TatReportPojo(tr.id, tr.evaluationCompletedDate,tr.eventFirstMeetingDate) from TatReport tr where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		List<TatReportPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void updateTatReportEventStatus(String eventId, String tenantId, EventStatus status) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.status =:status where upper(tr.eventId) =:eventId  and tr.tenantId =:tenantId  ");
		query.setParameter("status", status);
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@Override
	public void updateTatReportEventStatusById(String eventGeneratedId, String tenantId, EventStatus status) {
		Query query = getEntityManager().createQuery(" update TatReport tr set tr.status =:status where tr.eventGeneratedId =:eventGeneratedId  and tr.tenantId =:tenantId  ");
		query.setParameter("status", status);
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReportPojo getTatReportByEventIdAndFormIdAndTenantId(String requestGeneratedId, String eventId, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.TatReportPojo(tr.id) from TatReport tr where upper(tr.eventId) =:eventId and tr.requestGeneratedId =:requestGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventId", eventId);
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);

		List<TatReportPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReportPojo geTatReportFirstEnvelopOpenDateByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.TatReportPojo(tr.id, tr.firstEnvelopOpenDate) from TatReport tr where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		List<TatReportPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReportPojo getRfsFinishDatetByRfsIdAndIdAndTenantId(String requestGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.TatReportPojo(tr.id, tr.finishDate, tr.requestStatus) from TatReport tr where tr.requestGeneratedId = :requestGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("requestGeneratedId", requestGeneratedId);
		query.setParameter("tenantId", tenantId);

		List<TatReportPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public TatReportPojo getEventStartAndEndDatasForTatReportByEventIdAndTenantId(String eventGeneratedId, String tenantId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.pojo.TatReportPojo(tr.id, tr.eventStart,tr.eventEnd, tr.status) from TatReport tr where tr.eventGeneratedId =:eventGeneratedId and tr.tenantId =:tenantId ");
		query.setParameter("eventGeneratedId", eventGeneratedId);
		query.setParameter("tenantId", tenantId);
		List<TatReportPojo> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@Override
	public TatReport getTatReportyEventIdAndTenantIdAndEventTypeAndPrNo(String eventId, String tenantId, RfxTypes eventType) {
		final Query query = getEntityManager().createQuery("select tr from TatReport tr where " +
				"tr.eventId =:eventId and tr.tenantId =:tenantId and tr.eventType =:eventType");
		query.setParameter("eventId", eventId);
		query.setParameter("tenantId", tenantId);
		query.setParameter("eventType", eventType);

		List<TatReport> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
