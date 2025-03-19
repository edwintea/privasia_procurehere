/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierPerformanceFormCriteriaDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceFormCriteria;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Jayshree
 *
 */
@Repository
public class SupplierPerformanceFormCriteriaDaoImpl extends GenericDaoImpl<SupplierPerformanceFormCriteria, String> implements SupplierPerformanceFormCriteriaDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceFormCriteria> getSPFormCriteriaByFormId(String formId) {
		final Query query = getEntityManager().createQuery("select distinct a from SupplierPerformanceFormCriteria a where a.form.id =:formId order by a.level, a.order");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreByCriteriaAndFormId(Date startDate, Date endDate, String formId, String supplierId, String tenantId) {
		String sql = "";

		sql = "select NEW com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo(spfc.id, spfc.name, spfc.totalScore, spfc.weightage) from SupplierPerformanceFormCriteria spfc ";
		sql += " left outer join spfc.form spf";
		sql += " left outer join spf.awardedSupplier sup";
		sql += " where spfc.order = 0 and spf.buyer.id = :tenantId and sup.id = :supplierId and spf.formStatus = :formStatus";

//		if (startDate != null && endDate != null) {
//			sql += " and spf.concludeDate between :startDate and :endDate";
//		}

		if (StringUtils.checkString(formId).length() > 0) {
			sql += " and spf.id = :formId ";
		}

		sql += " order by spfc.level ";

		// LOG.info("Query >>>>>>>>>>> " + sql);
		final Query query = getEntityManager().createQuery(sql);

		query.setParameter("tenantId", tenantId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("formStatus", SupplierPerformanceFormStatus.CONCLUDED);
//		if (startDate != null && endDate != null) {
//			query.setParameter("startDate", startDate);
//			query.setParameter("endDate", endDate);
//		}
		if (StringUtils.checkString(formId).length() > 0) {
			query.setParameter("formId", formId);
		}
		List<SupplierPerformanceEvaluationPojo> dataList = query.getResultList();

		return dataList;
	}
}
