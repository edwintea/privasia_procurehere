/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierPerformanceAnalyticsDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.pojo.SpAnalyticsPojo;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author jayshree
 */
@Repository
public class SupplierPerformanceAnalyticsDaoImpl extends GenericDaoImpl<SupplierPerformanceForm, String> implements SupplierPerformanceAnalyticsDao {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceAnalyticsDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<SpAnalyticsPojo> getTopHighPerformanceSuppier(Date startDate, Date endDate, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT a.*, ";
			sql += " (SELECT sr.RATING  FROM PROC_SCORE_RATING sr WHERE a.avg BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.STATUS = 'ACTIVE' AND sr.TENANT_ID = :tenantId LIMIT 1) AS rating";
			sql += " FROM (SELECT ps.COMPANY_NAME,  AVG(p.OVERALL_SCORE) AS avg FROM PROC_SUPPLIER_PERFORMANCE_FORM p ";
			sql += " LEFT OUTER JOIN PROC_SUPPLIER ps ON p.AWARDED_SUPPLIER_ID = ps.SUPPLIER_ID WHERE p.TENANT_ID = :tenantId AND p.FORM_STATUS = 'CONCLUDED' AND p.OVERALL_SCORE > 0";

			if (startDate != null && endDate != null) {
				sql += " AND p.CONCLUDE_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY ps.COMPANY_NAME ORDER BY avg DESC , ps.COMPANY_NAME )a ";

//			 LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			query.setMaxResults(5);
			List<SpAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SpAnalyticsPojo data = new SpAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue((new BigDecimal(result[1].toString())).setScale(0, RoundingMode.HALF_UP));
				data.setRating(result[2] != null ? result[2].toString() : "");
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpAnalyticsPojo> getTopHighPerformanceSuppierByBUnit(Date startDate, Date endDate, String unitId, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT a.*, ";
			sql += " (SELECT sr.RATING  FROM PROC_SCORE_RATING sr WHERE a.avg BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.STATUS = 'ACTIVE' AND sr.TENANT_ID = :tenantId LIMIT 1 ) AS rating";
			sql += " FROM (SELECT ps.COMPANY_NAME,  AVG(p.OVERALL_SCORE) AS avg FROM PROC_SUPPLIER_PERFORMANCE_FORM p ";
			sql += " LEFT OUTER JOIN PROC_SUPPLIER ps ON p.AWARDED_SUPPLIER_ID = ps.SUPPLIER_ID";
			sql += " JOIN PROC_BUSINESS_UNIT pbu ON p.BUSINESS_UNIT_ID  = pbu.ID";
			sql += " WHERE p.TENANT_ID =:tenantId AND p.FORM_STATUS = 'CONCLUDED' AND p.OVERALL_SCORE > 0 ";

			if (startDate != null && endDate != null) {
				sql += " AND p.CONCLUDE_DATE between :startDate and :endDate ";
			}

			if (StringUtils.checkString(unitId).length() > 0) {
				sql += "AND p.BUSINESS_UNIT_ID = :unitId ";
			}

			sql += " GROUP BY ps.COMPANY_NAME ORDER BY avg DESC , ps.COMPANY_NAME )a ";

//			 LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			if (StringUtils.checkString(unitId).length() > 0) {
				query.setParameter("unitId", unitId);
			}
			query.setMaxResults(5);
			List<SpAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SpAnalyticsPojo data = new SpAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue((new BigDecimal(result[1].toString())).setScale(0, RoundingMode.HALF_UP));
				data.setRating(result[2] != null ? result[2].toString() : "");
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpAnalyticsPojo> getTopHighPerformanceSuppierByProcCat(Date startDate, Date endDate, String procCatId, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT a.*, ";
			sql += " (SELECT sr.RATING  FROM PROC_SCORE_RATING sr WHERE a.avg BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.STATUS = 'ACTIVE' AND sr.TENANT_ID = :tenantId LIMIT 1) AS rating";
			sql += " FROM (SELECT ps.COMPANY_NAME,  AVG(p.OVERALL_SCORE) AS avg FROM PROC_SUPPLIER_PERFORMANCE_FORM p ";
			sql += " LEFT OUTER JOIN PROC_SUPPLIER ps ON p.AWARDED_SUPPLIER_ID = ps.SUPPLIER_ID";
			sql += " LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES pc ON p.PROCUREMENT_CATEGORY_ID = pc.ID";
			sql += " WHERE p.TENANT_ID =:tenantId AND p.FORM_STATUS = 'CONCLUDED' AND p.OVERALL_SCORE > 0 ";

			if (startDate != null && endDate != null) {
				sql += " AND p.CONCLUDE_DATE between :startDate and :endDate ";
			}

			if (StringUtils.checkString(procCatId).length() > 0) {
				sql += "AND p.PROCUREMENT_CATEGORY_ID = :procCatId";
			}

			sql += " GROUP BY ps.COMPANY_NAME ORDER BY avg DESC , ps.COMPANY_NAME )a";

//			 LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			if (StringUtils.checkString(procCatId).length() > 0) {
				query.setParameter("procCatId", procCatId);
			}
			query.setMaxResults(5);
			List<SpAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SpAnalyticsPojo data = new SpAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue((new BigDecimal(result[1].toString())).setScale(0, RoundingMode.HALF_UP));
				data.setRating(result[2] != null ? result[2].toString() : "");
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpAnalyticsPojo> getTopLowPerformanceSuppier(Date startDate, Date endDate, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT a.*, ";
			sql += " (SELECT sr.RATING  FROM PROC_SCORE_RATING sr WHERE a.avg BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.STATUS = 'ACTIVE' AND sr.TENANT_ID = :tenantId LIMIT 1 ) AS rating";
			sql += " FROM (SELECT ps.COMPANY_NAME,  AVG(p.OVERALL_SCORE) AS avg FROM PROC_SUPPLIER_PERFORMANCE_FORM p ";
			sql += " LEFT OUTER JOIN PROC_SUPPLIER ps ON p.AWARDED_SUPPLIER_ID = ps.SUPPLIER_ID WHERE p.TENANT_ID =:tenantId AND p.FORM_STATUS = 'CONCLUDED' AND p.OVERALL_SCORE > 0 ";

			if (startDate != null && endDate != null) {
				sql += " AND p.CONCLUDE_DATE between :startDate and :endDate";
			}
			sql += " GROUP BY ps.COMPANY_NAME ORDER BY avg , ps.COMPANY_NAME )a ";

//			 LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			query.setMaxResults(5);
			List<SpAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SpAnalyticsPojo data = new SpAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue((new BigDecimal(result[1].toString())).setScale(0, RoundingMode.HALF_UP));
				data.setRating(result[2] != null ? result[2].toString() : "");
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpAnalyticsPojo> getTopLowPerformanceSuppByBU(Date startDate, Date endDate, String unitId, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT a.*, ";
			sql += " (SELECT sr.RATING  FROM PROC_SCORE_RATING sr WHERE a.avg BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.STATUS = 'ACTIVE' AND sr.TENANT_ID = :tenantId LIMIT 1 ) AS rating";
			sql += " FROM (SELECT ps.COMPANY_NAME,  AVG(p.OVERALL_SCORE) AS avg FROM PROC_SUPPLIER_PERFORMANCE_FORM p ";
			sql += " LEFT OUTER JOIN PROC_SUPPLIER ps ON p.AWARDED_SUPPLIER_ID = ps.SUPPLIER_ID";
			sql += " JOIN PROC_BUSINESS_UNIT pbu ON p.BUSINESS_UNIT_ID  = pbu.ID";
			sql += " WHERE p.TENANT_ID =:tenantId AND p.FORM_STATUS = 'CONCLUDED' AND p.OVERALL_SCORE > 0 ";

			if (startDate != null && endDate != null) {
				sql += " AND p.CONCLUDE_DATE between :startDate and :endDate ";
			}

			if (StringUtils.checkString(unitId).length() > 0) {
				sql += "AND p.BUSINESS_UNIT_ID = :unitId ";
			}

			sql += " GROUP BY ps.COMPANY_NAME ORDER BY avg , ps.COMPANY_NAME )a ";

			// LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			if (StringUtils.checkString(unitId).length() > 0) {
				query.setParameter("unitId", unitId);
			}
			query.setMaxResults(5);
			List<SpAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SpAnalyticsPojo data = new SpAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue((new BigDecimal(result[1].toString())).setScale(0, RoundingMode.HALF_UP));
				data.setRating(result[2] != null ? result[2].toString() : "");
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpAnalyticsPojo> getTopLowPerformanceSuppByProcCat(Date startDate, Date endDate, String procCatId, String tenantId) {
		try {
			String sql = "";
			sql += " SELECT a.*, ";
			sql += " (SELECT sr.RATING  FROM PROC_SCORE_RATING sr WHERE a.avg BETWEEN sr.MINIMUM_SCORE AND sr.MAXIMUM_SCORE AND sr.STATUS = 'ACTIVE' AND sr.TENANT_ID = :tenantId LIMIT 1 ) AS rating";
			sql += " FROM (SELECT ps.COMPANY_NAME,  AVG(p.OVERALL_SCORE) AS avg FROM PROC_SUPPLIER_PERFORMANCE_FORM p ";
			sql += " LEFT OUTER JOIN PROC_SUPPLIER ps ON p.AWARDED_SUPPLIER_ID = ps.SUPPLIER_ID";
			sql += " LEFT OUTER JOIN PROC_PROCUREMENT_CATEGORIES pc ON p.PROCUREMENT_CATEGORY_ID = pc.ID";
			sql += " WHERE p.TENANT_ID =:tenantId AND p.FORM_STATUS = 'CONCLUDED' AND p.OVERALL_SCORE > 0 ";

			if (startDate != null && endDate != null) {
				sql += " AND p.CONCLUDE_DATE between :startDate and :endDate ";
			}

			if (StringUtils.checkString(procCatId).length() > 0) {
				sql += "AND p.PROCUREMENT_CATEGORY_ID = :procCatId";
			}

			sql += " GROUP BY ps.COMPANY_NAME ORDER BY avg , ps.COMPANY_NAME )a";

//			 LOG.info("Query >>>>>>>>>>> " + sql);
			final Query query = getEntityManager().createNativeQuery(sql);

			query.setParameter("tenantId", tenantId);
			if (startDate != null && endDate != null) {
				query.setParameter("startDate", startDate);
				query.setParameter("endDate", endDate);
			}
			if (StringUtils.checkString(procCatId).length() > 0) {
				query.setParameter("procCatId", procCatId);
			}
			query.setMaxResults(5);
			List<SpAnalyticsPojo> dataList = new ArrayList<>();

			List<Object[]> records = query.getResultList();

			for (Object[] result : records) {
				SpAnalyticsPojo data = new SpAnalyticsPojo();
				data.setName((String) result[0]);
				data.setValue((new BigDecimal(result[1].toString())).setScale(0, RoundingMode.HALF_UP));
				data.setRating(result[2] != null ? result[2].toString(): "");
				dataList.add(data);
			}
			return dataList;
		} catch (Exception e) {
			LOG.info("Error " + e.getMessage(), e);
			return null;
		}
	}

}
