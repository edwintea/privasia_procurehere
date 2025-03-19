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

import com.privasia.procurehere.core.dao.RfpSupplierCqDao;
import com.privasia.procurehere.core.entity.RfpSupplierCq;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author jayshree
 *
 */
@Repository
public class RfpSupplierCqDaoImpl extends GenericDaoImpl<RfpSupplierCq, String> implements RfpSupplierCqDao {

	private static final Logger LOG = LogManager.getLogger(RfpSupplierCqDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public RfpSupplierCq findCqByEventIdAndEventCqId(String eventId, String cqId, String supplierId) {
		LOG.info("Event id .... : "+eventId);
		Query query =getEntityManager().createQuery("from RfpSupplierCq pc inner join fetch pc.event as e where pc.supplier.id =:supplierId and e.id =:eventId and pc.cq.id =:cqId ");
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("cqId", cqId);
		List<RfpSupplierCq> cqList =query.getResultList();
		if (CollectionUtil.isNotEmpty(cqList)) {
			return cqList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public long findPendingCqsByEventIdAndEventCqId(String eventId, String supplierId) {
		LOG.info("Event id .... : "+eventId);
		Query query =getEntityManager().createQuery("select count (pc.id) from RfpSupplierCq pc inner join pc.event as e where pc.supplier.id =:supplierId and e.id =:eventId and pc.supplierCqStatus in (:supplierCqStatus) ");
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("supplierCqStatus", Arrays.asList(SupplierCqStatus.DRAFT, SupplierCqStatus.PENDING));
		return ((Number) query.getSingleResult()).longValue();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public RfpSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId) {
		Query query =getEntityManager().createQuery("from RfpSupplierCq a inner join fetch a.event as e inner join fetch a.cq as b where e.id =:eventId and a.supplier.id =:supplierId and a.cq.id =:cqId order by b.cqOrder ");
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		query.setParameter("cqId", cqId);
		List<RfpSupplierCq> cqList =query.getResultList();
		if (CollectionUtil.isNotEmpty(cqList)) {
			return cqList.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId) {
		Query query =getEntityManager().createQuery("from RfpSupplierCq a inner join fetch a.event as e inner join fetch a.cq as b where e.id =:eventId and a.supplier.id =:supplierId order by b.cqOrder ");
		query.setParameter("supplierId", supplierId);
		query.setParameter("eventId", eventId);
		List<RfpSupplierCq> cqList =query.getResultList();
		if (CollectionUtil.isNotEmpty(cqList)) {
			return cqList;
		} else {
			return null;
		}
	}

	@Override
	public Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId) {
		Query query =getEntityManager().createQuery("select count(a) from RfpSupplierCq a inner join a.event as e inner join a.cq as b where e.id =:eventId and a.supplier.id =:supplierId and a.cq.id =:cqId ");
		query.setParameter("eventId", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("cqId", cqId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		LOG.info("Cq Count  :   " + count);
		return count;
	}

	
	@Override
	public void deleteSupplierCqForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("delete from RfpSupplierCq a where a.event.id = :eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}
}
