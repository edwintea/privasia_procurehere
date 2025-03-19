package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SupplierFormSubmitionAuditDao;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit;
import com.privasia.procurehere.core.enums.SupplierFormSubAuditVisibilityType;

/**
 * @author pooja
 */
@Repository
public class SupplierFormSubmitionAuditDaoImpl extends GenericDaoImpl<SupplierFormSubmitionAudit, String> implements SupplierFormSubmitionAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormSubmitionAudit> getFormAuditByFormId(String formId) {
		Query query = getEntityManager().createQuery("select pa from SupplierFormSubmitionAudit pa where pa.supplierFormSubmition.id = :formId order by pa.actionDate");
		query.setParameter("formId", formId);
		List<SupplierFormSubmitionAudit> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormSubmitionAudit> getFormAuditByFormIdForBuyer(String formId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description) from SupplierFormSubmitionAudit pa left outer join pa.actionBy u where pa.supplierFormSubmition.id = :formId and pa.visibilityType in(:visibilityType)  order by pa.actionDate");
		query.setParameter("formId", formId);
		query.setParameter("visibilityType", Arrays.asList(SupplierFormSubAuditVisibilityType.BUYER, SupplierFormSubAuditVisibilityType.BOTH));
		List<SupplierFormSubmitionAudit> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SupplierFormSubmitionAudit> getFormAuditByFormIdForSupplier(String formId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SupplierFormSubmitionAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description) from SupplierFormSubmitionAudit pa left outer join pa.actionBy u where pa.supplierFormSubmition.id = :formId and pa.visibilityType in(:visibilityType)  order by pa.actionDate");
		query.setParameter("formId", formId);
		query.setParameter("visibilityType", Arrays.asList(SupplierFormSubAuditVisibilityType.SUPPLIER, SupplierFormSubAuditVisibilityType.BOTH));
		List<SupplierFormSubmitionAudit> list = query.getResultList();
		return list;
	}
}
