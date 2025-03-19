/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Ravi
 *
 */
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericCqDao;
import com.privasia.procurehere.core.entity.Cq;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericCqDaoImpl<T extends Cq, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericCqDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Override
	public boolean isExists(final T cq, String eventId) {
		String hql = "select count(*) from " + entityClass.getName() + " c where c.rfxEvent.id = :eventId and upper(c.name) = :name";
		if (cq.getId() != null) {
			hql += " and c.id <> :id";
		}
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("name", cq.getName().toUpperCase());
		query.setParameter("eventId", eventId);
		if (cq.getId() != null) {
			query.setParameter("id", cq.getId());
		}
		LOG.info("hql isExists :" + hql);
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findCqsByIds(String[] ids) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r where r.id in (:id)");
		query.setParameter("id", Arrays.asList(ids));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getCqForId(String id) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r inner join fetch r.rfxEvent re where r.id = :id");
		query.setParameter("id", id);
		List<T> list = query.getResultList();
		return (CollectionUtil.isNotEmpty(list) ? list.get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findCqsForEvent(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct r from " + entityClass.getName() + " r left outer join fetch r.cqItems ci where r.rfxEvent.id =:eventId order by r.cqOrder, r.createdDate");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findCqsByEventIdAndCqIds(String eventId, List<String> cqIds) {
		StringBuilder hql = new StringBuilder("from " + entityClass.getName() + " cq inner join fetch cq.rfxEvent e where e.id = :id ");
		if (CollectionUtil.isNotEmpty(cqIds)) {
			hql.append(" and cq.id not in (:cqIds)");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", eventId);
		if (CollectionUtil.isNotEmpty(cqIds)) {
			query.setParameter("cqIds", cqIds);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	public void deleteCqByEventId(String id) {
		Query query = getEntityManager().createQuery("from " + entityClass.getName() + " cq where cq.rfxEvent.id = :id");
		query.setParameter("id", id);
		List<T> result = query.getResultList();
		for (T t : result) {
			getEntityManager().remove(t);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	public void deleteCqById(String id) {
		Query query = getEntityManager().createQuery("from " + entityClass.getName() + " cq where cq.id = :id");
		query.setParameter("id", id);
		List<T> result = query.getResultList();
		for (T t : result) {
			getEntityManager().remove(t);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findCqsForEventByOrder(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct r from " + entityClass.getName() + " r left outer join fetch r.cqItems ci where r.rfxEvent.id =:eventId order by r.cqOrder, r.createdDate");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}
	
}
