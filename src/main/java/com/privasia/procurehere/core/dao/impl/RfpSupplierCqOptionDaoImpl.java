package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.RfpSupplierCqOption;

/**
 * @author ravi
 */
@Repository
public class RfpSupplierCqOptionDaoImpl extends GenericDaoImpl<RfpSupplierCqOption, String> implements RfpSupplierCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfpSupplierCqOption(a.id, a.value) from RfpSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfpSupplierCqOption(a.id, a.value, a.cqItem.id, a.scoring) from RfpSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

}
