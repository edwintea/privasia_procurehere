package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ErpAuditDao;
import com.privasia.procurehere.core.entity.ErpAudit;
import com.privasia.procurehere.core.enums.ErpAuditType;

/**
 * @author parveen
 */
@Repository
public class ErpAuditDaoImpl extends GenericDaoImpl<ErpAudit, String> implements ErpAuditDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<ErpAudit> getAllAuditByTenantIdAndActionType(String tenantId, ErpAuditType type) {
		StringBuilder hql = new StringBuilder("select distinct ea from ErpAudit ea where ea.tenantId = :tenantId and ea.action = :type order by actionDate desc");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("type", type);
		return query.getResultList();
	}

	@Override
	public boolean isExists(String prNo, String tenantId) {
		StringBuilder hql = new StringBuilder("select count(*) from ErpAudit ea where ea.tenantId = :tenantId and upper(ea.prNo) = :prNo and ea.action = :action");
		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("tenantId", tenantId);
		query.setParameter("prNo", prNo);
		query.setParameter("action", ErpAuditType.CREATED);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

}
