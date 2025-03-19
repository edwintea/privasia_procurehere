package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.GoodsReceiptNoteAuditDao;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit;
import com.privasia.procurehere.core.enums.GrnAuditVisibilityType;

/**
 * @author ravi
 */

@Repository
public class GoodsReceiptNoteAuditDaoImpl extends GenericDaoImpl<GoodsReceiptNoteAudit, String> implements GoodsReceiptNoteAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsReceiptNoteAudit> getGrnAuditByGrnIdForSupplier(String grnId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit(da.id, u.name, da.actionDate, da.action, da.description) from GoodsReceiptNoteAudit da left outer join da.actionBy u where da.goodsReceiptNote.id = :grnId and da.visibilityType in(:visibilityType)  order by da.actionDate");
		query.setParameter("grnId", grnId);
		query.setParameter("visibilityType", Arrays.asList(GrnAuditVisibilityType.SUPPLIER, GrnAuditVisibilityType.BOTH));
		List<GoodsReceiptNoteAudit> list = query.getResultList();
		return list;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsReceiptNoteAudit> getGrnAuditByGrnIdForBuyer(String grnId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit(da.id, u.name, da.actionDate, da.action, da.description) from GoodsReceiptNoteAudit da left outer join da.actionBy u where da.goodsReceiptNote.id = :grnId and da.visibilityType in(:visibilityType)  order by da.actionDate");
		query.setParameter("grnId", grnId);
		query.setParameter("visibilityType", Arrays.asList(GrnAuditVisibilityType.BUYER, GrnAuditVisibilityType.BOTH));
		List<GoodsReceiptNoteAudit> list = query.getResultList();
		return list;
	}

}
