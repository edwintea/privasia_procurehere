package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.RfqSupplierCqOption;

/**
 * @author ravi
 */
@Repository
public class RfqSupplierCqOptionDaoImpl extends GenericDaoImpl<RfqSupplierCqOption, String> implements RfqSupplierCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqSupplierCqOption> findSupplierCqOptionsListByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfqSupplierCqOption(a.id, a.value) from RfqSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqSupplierCqOption> findSupplierCqOptionsListWithCqByCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfqSupplierCqOption(a.id, a.value, a.cqItem.id, a.scoring) from RfqSupplierCqOption a  where a.cqItem.id = :cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

}
