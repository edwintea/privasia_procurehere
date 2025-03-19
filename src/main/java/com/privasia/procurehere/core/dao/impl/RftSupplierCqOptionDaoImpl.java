package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.RftSupplierCqOption;

/**
 * @author ravi
 */
@Repository
public class RftSupplierCqOptionDaoImpl extends GenericDaoImpl<RftSupplierCqOption, String> implements RftSupplierCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RftSupplierCqOption(a.id, a.value) from RftSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RftSupplierCqOption(a.id, a.value, a.cqItem.id, a.scoring) from RftSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

}
