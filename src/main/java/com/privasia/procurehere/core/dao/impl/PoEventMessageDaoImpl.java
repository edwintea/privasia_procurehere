package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.PoEventMessageDao;
import com.privasia.procurehere.core.dao.RfqEventMessageDao;
import com.privasia.procurehere.core.entity.PoEventMessage;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.utils.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Repository
public class PoEventMessageDaoImpl extends GenericDaoImpl<PoEventMessage, String> implements PoEventMessageDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<PoEventMessage> getEventMessages(String eventId, int page, int size, String search) {
		String sql = "select rm from PoEventMessage rm join fetch rm.event e left outer join fetch rm.suppliers s join fetch rm.createdBy cb where e.id = :id and rm.parent is null order by rm.createdDate desc";
		if (StringUtils.checkString(search).length() > 0) {
			sql += " and upper(rm.message) like :search or upper(rm.subject) like :sub ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
			query.setParameter("sub", "%" + search.toUpperCase() + "%");
		}
		query.setFirstResult(page * size);
		query.setMaxResults(size);
		return query.getResultList();
	}

	@Override
	public long getTotalFilteredEventMessageCount(String eventId, String search) {
		String sql = "select count(rm) from PoEventMessage rm join rm.event e where e.id = :id ";
		if (StringUtils.checkString(search).length() > 0) {
			sql += " and upper(rm.message) like :search or upper(rm.subject) like :sub ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
			query.setParameter("sub", "%" + search.toUpperCase() + "%");
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long getTotalEventMessageCount(String eventId) {
		String sql = "select count(rm) from PoEventMessage rm join rm.event e where e.id = :id ";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoEventMessage> getEventMessagesForSupplier(String eventId, String supplierId, int page, int size, String search) {
		String sql = "select rm from PoEventMessage rm join fetch rm.event e left outer join fetch rm.replies r join fetch rm.createdBy cb left outer join fetch rm.buyer buy join rm.suppliers s where e.id = :id and s.id = :supplierId and rm.parent is null order by rm.createdDate desc";
		if (StringUtils.checkString(search).length() > 0) {
			sql += " and upper(rm.message) like :search or upper(rm.subject) like :sub ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
			query.setParameter("sub", "%" + search.toUpperCase() + "%");
		}
		query.setFirstResult(page * size);
		query.setMaxResults(size);
		return query.getResultList();
	}

	@Override
	public long getTotalFilteredEventMessageCountForSupplier(String eventId, String supplierId, String search) {
		String sql = "select count(distinct rm) from PoEventMessage rm join rm.event e join rm.suppliers s where e.id = :id and (s.id = :supplierId or rm.createdBy.supplier.id = :supplierIds ) ";
		if (StringUtils.checkString(search).length() > 0) {
			sql += " and upper(rm.message) like :search or upper(rm.subject) like :sub ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("supplierIds", supplierId);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
			query.setParameter("sub", "%" + search.toUpperCase() + "%");
		}
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public long getTotalEventMessageCountForSupplier(String eventId, String supplierId) {
		String sql = "select count(distinct rm) from PoEventMessage rm join rm.event e join rm.suppliers s where e.id = :id and (s.id = :supplierId or rm.createdBy.supplier.id = :supplierIds ) ";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		query.setParameter("supplierIds", supplierId);
		return ((Number) query.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoEventMessage> getPoEventMessagesByEventId(String eventId, int page, int size, String search) {
		String sql = "select new com.privasia.procurehere.core.entity.PoEventMessage(rm.id, rm.subject, rm.message, rm.createdDate, rm.fileName, e, cb, p, b, rm.tenantId, rm.sentByBuyer, rm.sentBySupplier) from PoEventMessage rm join rm.event e left outer join rm.createdBy cb left outer join rm.buyer b left outer join rm.parent p where e.id = :id and rm.parent is null order by rm.createdDate desc";
		if (StringUtils.checkString(search).length() > 0) {
			sql += " and upper(rm.message) like :search or upper(rm.subject) like :sub ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
			query.setParameter("sub", "%" + search.toUpperCase() + "%");
		}
		query.setFirstResult(page * size);
		query.setMaxResults(size);
		List<PoEventMessage> messages = query.getResultList();
		LinkedHashSet<PoEventMessage> resultList = new LinkedHashSet<PoEventMessage>();
		for (PoEventMessage rep : messages) {
			resultList.add(rep);
		}
		return new ArrayList<PoEventMessage>(resultList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Supplier> getPoEventMessagesSupplierById(String messageId) {
		String sql = "select new com.privasia.procurehere.core.entity.Supplier(s.id, s.companyName, s.companyContactNumber, s.loginEmail, s.fullName) from PoEventMessage m join m.event e left outer join m.suppliers s where m.id = :id and m.parent is null order by m.createdDate desc";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", messageId);
		List<Supplier> supplierList = query.getResultList();
		LinkedHashSet<Supplier> resultList = new LinkedHashSet<Supplier>();
		for (Supplier supplier : supplierList) {
			resultList.add(supplier);
		}
		return new ArrayList<Supplier>(resultList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoEventMessage> getPoEventMessagesRepliesByEventId(String eventId) {
		String sql = "select new com.privasia.procurehere.core.entity.PoEventMessage(m.id, m.subject, m.message, m.createdDate, m.fileName, e, cb, p, b, m.tenantId, m.sentByBuyer, m.sentBySupplier) from PoEventMessage m join m.event e left outer join m.createdBy cb left outer join m.buyer b left outer join m.parent p where e.id = :id and m.parent is not null order by m.createdDate desc";
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		List<PoEventMessage> replies = query.getResultList();
		LinkedHashSet<PoEventMessage> resultList = new LinkedHashSet<PoEventMessage>();
		for (PoEventMessage rep : replies) {
			resultList.add(rep);
		}
		return new ArrayList<PoEventMessage>(resultList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PoEventMessage> getEventMessagesForSupplierByEventId(String eventId, String supplierId, int page, int size, String search) {
		String sql = "select new com.privasia.procurehere.core.entity.PoEventMessage(rm.id, rm.subject, rm.message, rm.createdDate, rm.fileName, e, cb, p, buy, rm.tenantId, rm.sentByBuyer, rm.sentBySupplier) from PoEventMessage rm join rm.event e join rm.createdBy cb left outer join rm.buyer buy left outer join rm.parent p join rm.suppliers s where e.id = :id and s.id = :supplierId and rm.parent is null order by rm.createdDate desc";
		if (StringUtils.checkString(search).length() > 0) {
			sql += " and upper(rm.message) like :search or upper(rm.subject) like :sub ";
		}
		final Query query = getEntityManager().createQuery(sql);
		query.setParameter("id", eventId);
		query.setParameter("supplierId", supplierId);
		if (StringUtils.checkString(search).length() > 0) {
			query.setParameter("search", "%" + search.toUpperCase() + "%");
			query.setParameter("sub", "%" + search.toUpperCase() + "%");
		}
		query.setFirstResult(page * size);
		query.setMaxResults(size);
		return query.getResultList();
	}

}
