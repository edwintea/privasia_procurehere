/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfxDaoForRfxAnalytics;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.pojo.RfxAnalyticsPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 *
 */
@Repository
public class RfxDaoForRfxAnalyticsImpl extends GenericDaoImpl<RfaEvent, String> implements RfxDaoForRfxAnalytics {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RfxAnalyticsPojo> getTopRfxVolumeByCategoryForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		try {
			String sql = "";
			sql = constructQueryForTopRfxVolumeByCategory(startDate, endDate, eventType, eventStatus, tenantId);

//			LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			
			if (StringUtils.checkString(eventStatus).length() > 0) {
				query.setParameter("eventStatus", Arrays.asList(eventStatus));
			}else {
				query.setParameter("eventStatus", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.CANCELED.toString(), EventStatus.PENDING.toString(), EventStatus.APPROVED.toString(), EventStatus.ACTIVE.toString(), EventStatus.CLOSED.toString(), EventStatus.COMPLETE.toString(), EventStatus.FINISHED.toString(), EventStatus.REJECTED.toString(), EventStatus.SUSPENDED.toString()));
			}
			
			query.setMaxResults(10);
			List<RfxAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				RfxAnalyticsPojo data = new RfxAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue(new BigDecimal(result[1].toString()));
				dataList.add(data);
			}

			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}

	}

	private String constructQueryForTopRfxVolumeByCategory(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		String sql = "";
		
		if (StringUtils.checkString(eventType).length() > 0) {
			
			sql += " SELECT PC.PROCUREMENT_CATEGORIES , count(*) FROM PROC_" + eventType + "_EVENTS RFA LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON RFA.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE RFA.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFA.STATUS =:eventStatus";
			} else {
				sql += " AND RFA.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFA.CREATED_DATE between :startDate and :endDate";
			}
			sql += " AND RFA.PROCUREMENT_CATEGORIES IS NOT NULL GROUP BY PC.PROCUREMENT_CATEGORIES ORDER BY COUNT(*) DESC";
			
		} else {
			sql += " SELECT A.PROCUREMENT_CATEGORIES, count(*) FROM (";
			sql += "SELECT PC.PROCUREMENT_CATEGORIES , RFA.ID AS ID FROM PROC_RFA_EVENTS RFA LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON RFA.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE RFA.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFA.STATUS =:eventStatus";
			} else {
				sql += " AND RFA.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFA.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";
			
			sql += " SELECT PC.PROCUREMENT_CATEGORIES , RFT.ID AS ID FROM PROC_RFT_EVENTS RFT LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON RFT.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE RFT.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFT.STATUS =:eventStatus";
			} else {
				sql += " AND RFT.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFT.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";
			
			sql += " SELECT PC.PROCUREMENT_CATEGORIES , RFP.ID AS ID FROM PROC_RFP_EVENTS RFP LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON RFP.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE RFP.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFP.STATUS =:eventStatus";
			} else {
				sql += " AND RFP.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFP.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";
			
			sql += " SELECT PC.PROCUREMENT_CATEGORIES , RFQ.ID AS ID FROM PROC_RFQ_EVENTS RFQ LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON RFQ.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE RFQ.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFQ.STATUS =:eventStatus";
			} else {
				sql += " AND RFQ.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFQ.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";
			
			sql += " SELECT PC.PROCUREMENT_CATEGORIES , RFI.ID AS ID FROM PROC_RFI_EVENTS RFI LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON RFI.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE RFI.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFI.STATUS =:eventStatus";
			} else {
				sql += " AND RFI.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFI.CREATED_DATE between :startDate and :endDate";
			}
			sql += ")A WHERE A.PROCUREMENT_CATEGORIES IS NOT NULL GROUP BY A.PROCUREMENT_CATEGORIES ORDER BY 2 DESC";
			
		}
		return sql;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxAnalyticsPojo> getTopRfxVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {

		try {
			String sql = "";
			sql = constructQueryForTopRfxVolumeByBusinessUnit(startDate, endDate, eventType, eventStatus, tenantId);

//			LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			
			if (StringUtils.checkString(eventStatus).length() > 0) {
				query.setParameter("eventStatus", Arrays.asList(eventStatus));
			}else {
				query.setParameter("eventStatus", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.CANCELED.toString(), EventStatus.PENDING.toString(), EventStatus.APPROVED.toString(), EventStatus.ACTIVE.toString(), EventStatus.CLOSED.toString(), EventStatus.COMPLETE.toString(), EventStatus.FINISHED.toString(), EventStatus.REJECTED.toString(), EventStatus.SUSPENDED.toString()));
			}
			
			query.setMaxResults(10);
			List<RfxAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				RfxAnalyticsPojo data = new RfxAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue(new BigDecimal(result[1].toString()));
				dataList.add(data);
			}

			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}

	
	}

	private String constructQueryForTopRfxVolumeByBusinessUnit(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		String sql = "";
		
		if (StringUtils.checkString(eventType).length() > 0) {
			
			sql += " SELECT PC.DISPLAY_NAME , count(*) FROM PROC_" + eventType + "_EVENTS RFA LEFT OUTER JOIN PROC_BUSINESS_UNIT PC ON RFA.BUSINESS_UNIT_ID = PC.ID";
			sql += " WHERE RFA.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFA.STATUS =:eventStatus";
			} else {
				sql += " AND RFA.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFA.CREATED_DATE between :startDate and :endDate";
			}
			sql += " AND PC.DISPLAY_NAME IS NOT NULL GROUP BY PC.DISPLAY_NAME ORDER BY COUNT(*) DESC";
			
		} else {
			sql += " SELECT A.DISPLAY_NAME, count(*) FROM (";
			sql += "SELECT PC.DISPLAY_NAME , RFA.ID AS ID FROM PROC_RFA_EVENTS RFA LEFT OUTER JOIN PROC_BUSINESS_UNIT PC ON RFA.BUSINESS_UNIT_ID = PC.ID";
			sql += " WHERE RFA.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFA.STATUS =:eventStatus";
			} else {
				sql += " AND RFA.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFA.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";
			
			sql += " SELECT PC.DISPLAY_NAME , RFT.ID AS ID FROM PROC_RFT_EVENTS RFT LEFT OUTER JOIN PROC_BUSINESS_UNIT PC ON RFT.BUSINESS_UNIT_ID = PC.ID";
			sql += " WHERE RFT.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFT.STATUS =:eventStatus";
			} else {
				sql += " AND RFT.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFT.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";
			
			sql += " SELECT PC.DISPLAY_NAME , RFP.ID AS ID FROM PROC_RFP_EVENTS RFP LEFT OUTER JOIN PROC_BUSINESS_UNIT PC ON RFP.BUSINESS_UNIT_ID = PC.ID";
			sql += " WHERE RFP.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFP.STATUS =:eventStatus";
			} else {
				sql += " AND RFP.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFP.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";
			
			sql += " SELECT PC.DISPLAY_NAME , RFQ.ID AS ID FROM PROC_RFQ_EVENTS RFQ LEFT OUTER JOIN PROC_BUSINESS_UNIT PC ON RFQ.BUSINESS_UNIT_ID = PC.ID";
			sql += " WHERE RFQ.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFQ.STATUS =:eventStatus";
			} else {
				sql += " AND RFQ.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFQ.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";
			
			sql += " SELECT PC.DISPLAY_NAME , RFI.ID AS ID FROM PROC_RFI_EVENTS RFI LEFT OUTER JOIN PROC_BUSINESS_UNIT PC ON RFI.BUSINESS_UNIT_ID = PC.ID";
			sql += " WHERE RFI.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFI.STATUS =:eventStatus";
			} else {
				sql += " AND RFI.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFI.CREATED_DATE between :startDate and :endDate";
			}
			sql += ")A WHERE A.DISPLAY_NAME IS NOT NULL GROUP BY A.DISPLAY_NAME ORDER BY 2 DESC";
			
		}
		return sql;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxAnalyticsPojo> getTopRfxAwardValueByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String tenantId) {

		try {
			String sql = "";
			sql = constructQueryForTopRfxAwardValueByBusinessUnit(startDate, endDate, eventType, tenantId);

//			LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			
			query.setMaxResults(10);
			List<RfxAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				RfxAnalyticsPojo data = new RfxAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue(new BigDecimal(result[1].toString()));
				dataList.add(data);
			}

			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	private String constructQueryForTopRfxAwardValueByBusinessUnit(Date startDate, Date endDate, String eventType, String tenantId) {
		String sql = "";

		if (StringUtils.checkString(eventType).length() > 0) {

			sql += " SELECT BU.DISPLAY_NAME , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_" + eventType + "_EVENT_AWARD A JOIN PROC_" + eventType + "_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_BUSINESS_UNIT BU ON E.BUSINESS_UNIT_ID = BU.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " AND BU.DISPLAY_NAME IS NOT NULL GROUP BY BU.DISPLAY_NAME ORDER BY sum(A.GRAND_TOTAL_PRICE) DESC";

		} else {

			sql += " SELECT B.DISPLAY_NAME, SUM(B.GRAND_TOTAL_PRICE) FROM (";
			sql += "SELECT BU.DISPLAY_NAME , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_RFA_EVENT_AWARD A JOIN PROC_RFA_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_BUSINESS_UNIT BU ON E.BUSINESS_UNIT_ID = BU.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY BU.DISPLAY_NAME";
			sql += " UNION";

			sql += " SELECT BU.DISPLAY_NAME , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_RFT_EVENT_AWARD A JOIN PROC_RFT_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_BUSINESS_UNIT BU ON E.BUSINESS_UNIT_ID = BU.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY BU.DISPLAY_NAME";
			sql += " UNION";

			sql += " SELECT BU.DISPLAY_NAME , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_RFP_EVENT_AWARD A JOIN PROC_RFP_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_BUSINESS_UNIT BU ON E.BUSINESS_UNIT_ID = BU.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY BU.DISPLAY_NAME";
			sql += " UNION";

			sql += " SELECT BU.DISPLAY_NAME , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_RFQ_EVENT_AWARD A JOIN PROC_RFQ_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_BUSINESS_UNIT BU ON E.BUSINESS_UNIT_ID = BU.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY BU.DISPLAY_NAME";

			sql += ") B WHERE B.DISPLAY_NAME  IS NOT NULL GROUP BY B.DISPLAY_NAME ORDER BY 2 DESC";

		}
		return sql;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxAnalyticsPojo> getTopRfxAwardValueByCategoryForForCurrentYear(Date startDate, Date endDate, String eventType, String tenantId) {

		try {
			String sql = "";
			sql = constructQueryForTopRfxAwardValueByProcCategory(startDate, endDate, eventType, tenantId);

//			LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			
			query.setMaxResults(10);
			List<RfxAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				RfxAnalyticsPojo data = new RfxAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue(new BigDecimal(result[1].toString()));
				dataList.add(data);
			}

			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	private String constructQueryForTopRfxAwardValueByProcCategory(Date startDate, Date endDate, String eventType, String tenantId) {
		String sql = "";

		if (StringUtils.checkString(eventType).length() > 0) {

			sql += " SELECT PC.PROCUREMENT_CATEGORIES , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_" + eventType + "_EVENT_AWARD A JOIN PROC_" + eventType + "_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON E.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " AND PC.PROCUREMENT_CATEGORIES IS NOT NULL GROUP BY PC.PROCUREMENT_CATEGORIES ORDER BY sum(A.GRAND_TOTAL_PRICE) DESC";

		} else {

			sql += " SELECT B.PROCUREMENT_CATEGORIES, SUM(B.GRAND_TOTAL_PRICE) FROM (";
			sql += "SELECT PC.PROCUREMENT_CATEGORIES , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_RFA_EVENT_AWARD A JOIN PROC_RFA_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON E.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY PC.PROCUREMENT_CATEGORIES";
			sql += " UNION";

			sql += " SELECT PC.PROCUREMENT_CATEGORIES , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_RFT_EVENT_AWARD A JOIN PROC_RFT_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON E.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY PC.PROCUREMENT_CATEGORIES";
			sql += " UNION";

			sql += " SELECT PC.PROCUREMENT_CATEGORIES , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_RFP_EVENT_AWARD A JOIN PROC_RFP_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON E.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY PC.PROCUREMENT_CATEGORIES";
			sql += " UNION";

			sql += " SELECT PC.PROCUREMENT_CATEGORIES , sum(A.GRAND_TOTAL_PRICE) AS GRAND_TOTAL_PRICE FROM PROC_RFQ_EVENT_AWARD A JOIN PROC_RFQ_EVENTS E ON E.ID = A.EVENT_ID LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON E.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE E.TENANT_ID =:tenantId";
			if (startDate != null && endDate != null) {
				sql += " AND A.CREATED_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY PC.PROCUREMENT_CATEGORIES";

			sql += ") B WHERE B.PROCUREMENT_CATEGORIES IS NOT NULL GROUP BY B.PROCUREMENT_CATEGORIES ORDER BY 2 DESC";

		}
		return sql;
	}

	@Override
	public long getCountOfRfxVolumeByCategoryForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		String sql = "";

		if (StringUtils.checkString(eventType).length() > 0) {

			sql += " SELECT count(*) FROM PROC_" + eventType + "_EVENTS RFA";
			sql += " WHERE RFA.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFA.STATUS =:eventStatus";
			} else {
				sql += " AND RFA.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFA.CREATED_DATE between :startDate and :endDate";
			}
			sql += " AND RFA.PROCUREMENT_CATEGORIES IS NOT NULL";

		} else {
			sql += " SELECT count(*) FROM (";
			sql += "SELECT  RFA.ID AS ID FROM PROC_RFA_EVENTS RFA";
			sql += " WHERE RFA.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFA.STATUS =:eventStatus";
			} else {
				sql += " AND RFA.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFA.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";

			sql += " SELECT RFT.ID AS ID FROM PROC_RFT_EVENTS RFT";
			sql += " WHERE RFT.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFT.STATUS =:eventStatus";
			} else {
				sql += " AND RFT.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFT.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";

			sql += " SELECT RFP.ID AS ID FROM PROC_RFP_EVENTS RFP";
			sql += " WHERE RFP.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFP.STATUS =:eventStatus";
			} else {
				sql += " AND RFP.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFP.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";

			sql += " SELECT RFQ.ID AS ID FROM PROC_RFQ_EVENTS RFQ";
			sql += " WHERE RFQ.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFQ.STATUS =:eventStatus";
			} else {
				sql += " AND RFQ.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFQ.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";

			sql += " SELECT RFI.ID AS ID FROM PROC_RFI_EVENTS RFI";
			sql += " WHERE RFI.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFI.STATUS =:eventStatus";
			} else {
				sql += " AND RFI.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFI.CREATED_DATE between :startDate and :endDate";
			}
			sql += ")A ";

		}
		
//		LOG.info("Query >>>>>>>>>>> " + sql);
		final Query query = getEntityManager().createNativeQuery(sql);

		query.setParameter("tenantId", tenantId);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		if (StringUtils.checkString(eventStatus).length() > 0) {
			query.setParameter("eventStatus", Arrays.asList(eventStatus));
		} else {
			query.setParameter("eventStatus", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.CANCELED.toString(), EventStatus.PENDING.toString(), EventStatus.APPROVED.toString(), EventStatus.ACTIVE.toString(), EventStatus.CLOSED.toString(), EventStatus.COMPLETE.toString(), EventStatus.FINISHED.toString(), EventStatus.REJECTED.toString(), EventStatus.SUSPENDED.toString()));
		}

		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long getCountOfRfxVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String eventType, String eventStatus, String tenantId) {
		String sql = "";

		if (StringUtils.checkString(eventType).length() > 0) {

			sql += " SELECT count(*) FROM PROC_" + eventType + "_EVENTS RFA";
			sql += " WHERE RFA.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFA.STATUS =:eventStatus";
			} else {
				sql += " AND RFA.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFA.CREATED_DATE between :startDate and :endDate";
			}

		} else {
			sql += " SELECT count(*) FROM (";
			sql += "SELECT  RFA.ID AS ID FROM PROC_RFA_EVENTS RFA";
			sql += " WHERE RFA.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFA.STATUS =:eventStatus";
			} else {
				sql += " AND RFA.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFA.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";

			sql += " SELECT RFT.ID AS ID FROM PROC_RFT_EVENTS RFT";
			sql += " WHERE RFT.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFT.STATUS =:eventStatus";
			} else {
				sql += " AND RFT.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFT.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";

			sql += " SELECT RFP.ID AS ID FROM PROC_RFP_EVENTS RFP";
			sql += " WHERE RFP.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFP.STATUS =:eventStatus";
			} else {
				sql += " AND RFP.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFP.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";

			sql += " SELECT RFQ.ID AS ID FROM PROC_RFQ_EVENTS RFQ";
			sql += " WHERE RFQ.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFQ.STATUS =:eventStatus";
			} else {
				sql += " AND RFQ.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFQ.CREATED_DATE between :startDate and :endDate";
			}
			sql += " UNION";

			sql += " SELECT RFI.ID AS ID FROM PROC_RFI_EVENTS RFI";
			sql += " WHERE RFI.TENANT_ID =:tenantId";
			if (StringUtils.checkString(eventStatus).length() > 0) {
				sql += " AND RFI.STATUS =:eventStatus";
			} else {
				sql += " AND RFI.STATUS IN(:eventStatus)";
			}
			if (startDate != null && endDate != null) {
				sql += " AND RFI.CREATED_DATE between :startDate and :endDate";
			}
			sql += ")A ";

		}
		
//		LOG.info("Query >>>>>>>>>>> " + sql);
		final Query query = getEntityManager().createNativeQuery(sql);

		query.setParameter("tenantId", tenantId);
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
		}

		if (StringUtils.checkString(eventStatus).length() > 0) {
			query.setParameter("eventStatus", Arrays.asList(eventStatus));
		} else {
			query.setParameter("eventStatus", Arrays.asList(EventStatus.DRAFT.toString(), EventStatus.CANCELED.toString(), EventStatus.PENDING.toString(), EventStatus.APPROVED.toString(), EventStatus.ACTIVE.toString(), EventStatus.CLOSED.toString(), EventStatus.COMPLETE.toString(), EventStatus.FINISHED.toString(), EventStatus.REJECTED.toString(), EventStatus.SUSPENDED.toString()));
		}

		return ((Number) query.getSingleResult()).longValue();
	}

}
