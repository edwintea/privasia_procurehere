package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.RfaSupplierCqOption;

/**
 * @author ravi
 */
@Repository
public class RfaSupplierCqOptionDaoImpl extends GenericDaoImpl<RfaSupplierCqOption, String> implements RfaSupplierCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfaSupplierCqOption(a.id, a.value) from RfaSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfaSupplierCqOption(a.id, a.value, a.cqItem.id, a.scoring) from RfaSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

}
