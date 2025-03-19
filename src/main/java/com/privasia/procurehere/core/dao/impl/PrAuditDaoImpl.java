package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.PrAuditDao;
import com.privasia.procurehere.core.entity.PrAudit;

/**
 * @author parveen
 */
@Repository
public class PrAuditDaoImpl extends GenericDaoImpl<PrAudit, String> implements PrAuditDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<PrAudit> getPrAuditByPrId(String prId) {
		Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.PrAudit(pa.id, u.name, pa.actionDate, pa.action, pa.description) from PrAudit pa left outer join pa.actionBy u where pa.pr.id = :prId order by pa.actionDate");
		query.setParameter("prId", prId);
		List<PrAudit> list = query.getResultList();
		return list;
	}

}
