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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericBqDao;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericBqDaoImpl<T extends Bq, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericBqDao<T, PK> {

	private static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Override
	public boolean isExists(final T bq, String eventId) {
		String hql = "select count(*) from " + bq.getClass().getName() + " b where b.rfxEvent.id = :eventId and upper(b.name) = :name";
		if (StringUtils.checkString(bq.getId()).length() > 0) {
			hql += " and b.id <> :id";
		}
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("name", StringUtils.checkString(bq.getName()).toUpperCase());
		query.setParameter("eventId", eventId);
		if (StringUtils.checkString(bq.getId()).length() > 0) {
			query.setParameter("id", bq.getId());
		}
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBqsByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.rfxEvent sp where sp.id = :id");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBqsByEventIdForEnvelop(String eventId, List<String> bqIds) {
		StringBuilder hql = new StringBuilder("from " + entityClass.getName() + " a inner join fetch a.rfxEvent sp where sp.id = :id ");
		if (CollectionUtil.isNotEmpty(bqIds)) {
			hql.append(" and a.id not in (:bqIds)");
		}

		final Query query = getEntityManager().createQuery(hql.toString());
		query.setParameter("id", eventId);
		if (CollectionUtil.isNotEmpty(bqIds)) {
			query.setParameter("bqIds", bqIds);
		}

		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAllBqsByIds(String[] ids) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r where r.id in (:id)");
		query.setParameter("id", Arrays.asList(ids));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	public void deleteBQ(String id) {
		Query query = getEntityManager().createQuery("from " + entityClass.getName() + " bq where bq.id = :id");
		query.setParameter("id", id);
		List<T> result = query.getResultList();
		for (T t : result) {
			getEntityManager().remove(t);
		}

	}

	@Override
	public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
		Query query = null;
		switch (eventType) {
		case RFA:
			query = getEntityManager().createQuery("select count(distinct bq) from RfaEnvelop  ev left outer join ev.bqList as bq where bq.id =:id");
			break;
		case RFI:
			break;
		case RFP:
			query = getEntityManager().createQuery("select count(distinct bq) from RfpEnvelop  ev left outer join ev.bqList as bq where bq.id =:id");
			break;
		case RFQ:
			query = getEntityManager().createQuery("select count(distinct bq) from RfqEnvelop  ev left outer join ev.bqList as bq where bq.id =:id");
			break;
		case RFT:
			query = getEntityManager().createQuery("select count(distinct bq) from RftEnvelop  ev left outer join ev.bqList as bq where bq.id =:id");
			break;
		}
		query.setParameter("id", id);
		if (((Number) query.getSingleResult()).longValue() > 0) {
			LOG.error(messageSource.getMessage("rfx.bq.info.envelope", new Object[] {}, Global.LOCALE));
			throw new NotAllowedException(messageSource.getMessage("rfx.bq.info.envelope", new Object[] {}, Global.LOCALE));
		}
	}

	@Override
	public T findBqItemById(String id) {
		try {
			final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " r where r.id= :id");
			query.setParameter("id", id);
			@SuppressWarnings("unchecked")
			List<T> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting BQ ITEM : " + nr.getMessage(), nr);
			return null;
		}
	}

	@Override
	public void deletefieldInBq(String bqId, String label) {
		try {
			LOG.info("BQ ID :: " + bqId + " label :: " + label);
			StringBuilder hql = new StringBuilder("update " + entityClass.getName() + " b set b." + label + "Label = null, b." + label + "FilledBy = null, b." + label + "ToShowSupplier = false, b." + label + "Required = false  where b.id = :bqId");
			Query query = getEntityManager().createQuery(hql.toString());
			query.setParameter("bqId", bqId);
			query.executeUpdate();
			LOG.info("hql : " + hql);
		} catch (NoResultException nr) {
			LOG.info("Error while deleting new Fields: " + nr.getMessage(), nr);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBQForEventId(String id) {
		Query query = getEntityManager().createQuery("delete from " + entityClass.getName() + " bq where bq.rfxEvent.id = :id ");
		query.setParameter("id", id);
		query.executeUpdate();

	}

	@Override
	public RftEventBq getRftEventBqByBqId(String id) {
		final Query query = getEntityManager().createQuery("from RftEventBq r where r.id= :id");
		query.setParameter("id", id);
		RftEventBq bq = (RftEventBq) query.getSingleResult();
		return bq;
	}

	@Override
	public RfaEventBq getRfaEventBqByBqId(String id) {
		final Query query = getEntityManager().createQuery("from RfaEventBq r where r.id= :id");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		RfaEventBq bq = (RfaEventBq) query.getSingleResult();
		return bq;
	}

	@Override
	public RfpEventBq getRfpEventBqByBqId(String id) {
		final Query query = getEntityManager().createQuery("from RfpEventBq r where r.id= :id");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		RfpEventBq bq = (RfpEventBq) query.getSingleResult();
		return bq;
	}

	@Override
	public RfqEventBq getRfqEventBqByBqId(String id) {
		final Query query = getEntityManager().createQuery("from RfqEventBq r where r.id= :id");
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		RfqEventBq bq = (RfqEventBq) query.getSingleResult();
		return bq;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBqsByEventIdByOrder(String eventId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.rfxEvent sp where sp.id = :id order by a.bqOrder");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findBqAndBqItemsByEventIdByOrder(String eventId) {
		final Query query = getEntityManager().createQuery("from " + entityClass.getName() + " a inner join fetch a.rfxEvent sp left outer join fetch a.bqItems items where sp.id = :id order by a.bqOrder");
		query.setParameter("id", eventId);
		return query.getResultList();
	}
}
