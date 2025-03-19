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

import com.privasia.procurehere.core.dao.RfsDaoForRfxAnalytics;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.pojo.RfxAnalyticsPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 */
@Repository
public class RfsDaoForRfxAnalyticsImpl extends GenericDaoImpl<SourcingFormRequest, String> implements RfsDaoForRfxAnalytics {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfxAnalyticsPojo> getTopRfsVolumeByProcurementCategoryForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT PC.PROCUREMENT_CATEGORIES , COUNT(*) FROM PROC_SOURCING_FORM_REQ SR LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES PC ON SR.PROCUREMENT_CATEGORIES = PC.ID";
			sql += " WHERE 1=1";

			if (startDate != null && endDate != null) {
				sql += " AND SR.CREATED_DATE between :startDate and :endDate";
			}
			if (StringUtils.checkString(rfsStatus).length() > 0) {
				sql += " AND SR.STATUS =:rfsStatus";
			} else {
				sql += " AND SR.STATUS IN('APPROVED', 'FINISHED', 'PENDING', 'CANCELED', 'DRAFT', 'REJECTED', 'CONCLUDED')";
			}
			sql += " AND SR.TENANT_ID =:tenantId AND SR.PROCUREMENT_CATEGORIES IS NOT NULL GROUP BY PC.PROCUREMENT_CATEGORIES ORDER BY COUNT(*) DESC";

//			LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			if (StringUtils.checkString(rfsStatus).length() > 0) {
				query.setParameter("rfsStatus", Arrays.asList(rfsStatus));
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

	@Override
	public long getCountOfTopRfsByProcurementCategoryForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT COUNT(*) FROM PROC_SOURCING_FORM_REQ SR ";
			sql += " WHERE 1=1";

			if (startDate != null && endDate != null) {
				sql += " AND SR.CREATED_DATE between :startDate and :endDate";
			}
			if (StringUtils.checkString(rfsStatus).length() > 0) {
				sql += " AND SR.STATUS =:rfsStatus";
			} else {
				sql += " AND SR.STATUS IN('APPROVED', 'FINISHED', 'PENDING', 'CANCELED', 'DRAFT', 'REJECTED', 'CONCLUDED')";
			}
			sql += " AND SR.TENANT_ID =:tenantId AND SR.PROCUREMENT_CATEGORIES  IS NOT NULL";

//			 LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			if (StringUtils.checkString(rfsStatus).length() > 0) {
				query.setParameter("rfsStatus", Arrays.asList(rfsStatus));
			}

			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RfxAnalyticsPojo> getTopRfsVolumeByBusinessUnitForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT BU.DISPLAY_NAME , COUNT(*) FROM PROC_SOURCING_FORM_REQ SR LEFT OUTER JOIN PROC_BUSINESS_UNIT BU ON SR.BUSINESS_UNIT_ID = BU.ID";
			sql += " WHERE 1=1";
			
			if (startDate != null && endDate != null) {
				sql += " AND SR.CREATED_DATE between :startDate and :endDate";
			}
			if (StringUtils.checkString(rfsStatus).length() > 0) {
				sql += " AND SR.STATUS =:rfsStatus";
			} else {
				sql += " AND SR.STATUS IN('APPROVED', 'FINISHED', 'PENDING', 'CANCELED', 'DRAFT', 'REJECTED', 'CONCLUDED')";
			}
			sql += " AND SR.TENANT_ID =:tenantId AND BU.DISPLAY_NAME IS NOT NULL GROUP BY BU.DISPLAY_NAME ORDER BY COUNT(*) DESC";
			
//			LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);
			
			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			if (StringUtils.checkString(rfsStatus).length() > 0) {
				query.setParameter("rfsStatus", Arrays.asList(rfsStatus));
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

	@Override
	public long getCountOfTopRfsByBusinessUnitForCurrentYear(Date startDate, Date endDate, String rfsStatus, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT COUNT(*) FROM PROC_SOURCING_FORM_REQ SR LEFT OUTER JOIN PROC_BUSINESS_UNIT BU ON SR.BUSINESS_UNIT_ID = BU.ID";
			sql += " WHERE 1=1";

			if (startDate != null && endDate != null) {
				sql += " AND SR.CREATED_DATE between :startDate and :endDate";
			}
			if (StringUtils.checkString(rfsStatus).length() > 0) {
				sql += " AND SR.STATUS =:rfsStatus";
			} else {
				sql += " AND SR.STATUS IN('APPROVED', 'FINISHED', 'PENDING', 'CANCELED', 'DRAFT', 'REJECTED', 'CONCLUDED')";
			}
			sql += " AND SR.TENANT_ID =:tenantId AND BU.DISPLAY_NAME IS NOT NULL ";

//			LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			if (StringUtils.checkString(rfsStatus).length() > 0) {
				query.setParameter("rfsStatus", Arrays.asList(rfsStatus));
			}

			return ((Number) query.getSingleResult()).longValue();
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return 0;
		}

	}

}
