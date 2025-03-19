/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaSupplierCqDao;
import com.privasia.procurehere.core.entity.RfaSupplierCq;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author jayshree
 *
 */
@Repository
public class RfaSupplierCqDaoImpl extends GenericDaoImpl<RfaSupplierCq, String> implements RfaSupplierCqDao {

	private static final Logger LOG = LogManager.getLogger(RfaSupplierCqDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public RfaSupplierCq findCqByEventIdAndCqId(String eventId, String cqId, String supplierId) {
		LOG.info("......");
		Query query =getEntityManager().createQuery("from RfaSupplierCq a inner join fetch a.event as e where e.id =:eventId and a.cq.id =:cqId and a.supplier.id =:supplierId ");
		query.setParameter("eventId", eventId);
		query.setParameter("cqId", cqId);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierCq> cqList =query.getResultList();
		if (CollectionUtil.isNotEmpty(cqList)) {
			return cqList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public RfaSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId) {
		Query query =getEntityManager().createQuery("from RfaSupplierCq a inner join fetch a.event as e inner join fetch a.cq as b where e.id =:eventId and a.supplier.id =:supplierId and a.cq.id =:cqId order by b.cqOrder");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("cqId", cqId);
		List<RfaSupplierCq> cqList = query.getResultList();
		if (CollectionUtil.isNotEmpty(cqList)) {
			return cqList.get(0);
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public long findPendingCqsByEventIdAndEventCqId(String eventId, String supplierId) {
		Query query =getEntityManager().createQuery("select count (pc.id) from RfaSupplierCq pc inner join pc.event as e where pc.supplier.id =:supplierId and e.id =:eventId and pc.supplierCqStatus in (:supplierCqStatus) ");
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("supplierCqStatus", Arrays.asList(SupplierCqStatus.DRAFT, SupplierCqStatus.PENDING));
		return ((Number) query.getSingleResult()).longValue();
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<RfaSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId) {
		Query query =getEntityManager().createQuery("from RfaSupplierCq a inner join fetch a.event as e inner join fetch a.cq as b where e.id =:eventId and a.supplier.id =:supplierId order by b.cqOrder");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		List<RfaSupplierCq> cqList = query.getResultList();
		if (CollectionUtil.isNotEmpty(cqList)) {
			return cqList;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SupplierCqStatus findSupplierCqStatusByCqAndEventId(String supplierId, String eventId, String cqId) {
		Query query =getEntityManager().createQuery("select a.supplierCqStatus from RfaSupplierCq a inner join a.event as e inner join a.cq as b where e.id =:eventId and a.supplier.id =:supplierId and a.cq.id =:cqId ");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("cqId", cqId);
		List<SupplierCqStatus> cqStList = query.getResultList();
		if (CollectionUtil.isNotEmpty(cqStList)) {
			return cqStList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId) {
		Query query =getEntityManager().createQuery("select count(a) from RfaSupplierCq a inner join a.event as e inner join a.cq as b where e.id =:eventId and a.supplier.id =:supplierId and b.id =:cqId ");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("cqId", cqId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		LOG.info("Cq Count  :   " + count);
		return count;
	}
	
}
