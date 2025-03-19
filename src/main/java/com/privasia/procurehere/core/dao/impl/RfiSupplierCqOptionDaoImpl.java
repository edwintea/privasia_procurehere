package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.RfiSupplierCqOption;

/**
 * @author ravi
 */
@Repository
public class RfiSupplierCqOptionDaoImpl extends GenericDaoImpl<RfiSupplierCqOption, String> implements RfiSupplierCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfiSupplierCqOption(a.id, a.value) from RfiSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfiSupplierCqOption(a.id, a.value, a.cqItem.id, a.scoring) from RfiSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

}
