package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PoAuditDao;
import com.privasia.procurehere.core.entity.PoAudit;
import com.privasia.procurehere.core.enums.PoAuditVisibilityType;

/**
 * @author ravi
 */
@Repository
public class PoAuditDaoImpl extends GenericDaoImpl<PoAudit, String> implements PoAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<PoAudit> getPoAuditByPrId(String poId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.PoAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description) from PoAudit pa left outer join pa.actionBy u where pa.po.id = :poId order by pa.actionDate");
		query.setParameter("poId", poId);
		List<PoAudit> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoAudit> getPoAuditByPoIdForBuyer(String poId) {
		Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.PoAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description, pa.snapshot) from PoAudit pa left outer join pa.actionBy u where pa.po.id = :poId and pa.visibilityType in(:visibilityType)  order by pa.actionDate");
		query.setParameter("poId", poId);
		query.setParameter("visibilityType", Arrays.asList(PoAuditVisibilityType.BUYER, PoAuditVisibilityType.BOTH));
		List<PoAudit> list = query.getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoAudit> getPoAuditByPoIdForSupplier(String poId) {
		Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.PoAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description, pa.snapshot) from PoAudit pa left outer join pa.actionBy u where pa.po.id = :poId and pa.visibilityType in(:visibilityType)  order by pa.actionDate");
		query.setParameter("poId", poId);
		query.setParameter("visibilityType", Arrays.asList(PoAuditVisibilityType.SUPPLIER, PoAuditVisibilityType.BOTH));
		List<PoAudit> list = query.getResultList();
		return list;
	}
}
