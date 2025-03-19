package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.InvoiceAuditDao;
import com.privasia.procurehere.core.entity.InvoiceAudit;
import com.privasia.procurehere.core.enums.InvoiceAuditVisibilityType;

/**
 * @author pooja
 */
@Repository
public class InvoiceAuditDaoImpl extends GenericDaoImpl<InvoiceAudit, String> implements InvoiceAuditDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<InvoiceAudit> getInvoiceAuditByInvoiceIdForSupplier(String invoiceId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.InvoiceAudit(da.id, u.name, da.actionDate, da.action, da.description) from InvoiceAudit da left outer join da.actionBy u where da.invoice.id = :invoiceId and da.visibilityType in(:visibilityType)  order by da.actionDate");
		query.setParameter("invoiceId", invoiceId);
		query.setParameter("visibilityType", Arrays.asList(InvoiceAuditVisibilityType.SUPPLIER, InvoiceAuditVisibilityType.BOTH));
		List<InvoiceAudit> list = query.getResultList();
		return list;

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<InvoiceAudit> getInvoiceAuditByInvoiceIdForBuyer(String invoiceId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.InvoiceAudit(da.id, u.name, da.actionDate, da.action, da.description) from InvoiceAudit da left outer join da.actionBy u where da.invoice.id = :invoiceId and da.visibilityType in(:visibilityType)  order by da.actionDate");
		query.setParameter("invoiceId", invoiceId);
		query.setParameter("visibilityType", Arrays.asList(InvoiceAuditVisibilityType.BUYER, InvoiceAuditVisibilityType.BOTH));
		List<InvoiceAudit> list = query.getResultList();
		return list;
	}

}
