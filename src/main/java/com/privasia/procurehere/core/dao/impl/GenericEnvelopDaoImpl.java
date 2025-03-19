/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Ravi
 *
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Query;

import com.privasia.procurehere.core.pojo.SorPojo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericEnvelopDao;
import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@SuppressWarnings("unchecked")
@Transactional(propagation = Propagation.REQUIRED)
public class GenericEnvelopDaoImpl<T extends Envelop, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericEnvelopDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Override
	public List<T> getAllEnvelopByEventId(String eventId, RfxTypes eventType) {
		Query query = getEntityManager().createQuery("select a from " + entityClass.getSimpleName() + " a inner join fetch a.rfxEvent sp left outer join fetch a.cqList cq where sp.id =:id order by cq.cqOrder");
		query.setParameter("id", eventId);
		query.getResultList();
		if (eventType != RfxTypes.RFI) {
			query = getEntityManager().createQuery("select a from " + entityClass.getSimpleName() + " a inner join fetch a.rfxEvent sp left outer join fetch a.bqList bq where sp.id =:id order by bq.bqOrder");
			query.setParameter("id", eventId);
		}
		List<T> list=query.getResultList();
		//to remove duplicate record
		LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (T item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<T>(map.values());
	}

	@Override
	public List<T> getAllEnvelopSorByEventId(String eventId, RfxTypes eventType) {
		LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
//		if (eventType != RfxTypes.RFI) {
		Query query = getEntityManager().createQuery("select a from " + entityClass.getSimpleName() + " a inner join fetch a.rfxEvent sp left outer join fetch a.sorList bq where sp.id =:id order by bq.sorOrder");
		query.setParameter("id", eventId);
		List<T> list = query.getResultList();
		//to remove duplicate record
		if (CollectionUtil.isNotEmpty(list)) {
			for (T item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<T>(map.values());
	}

	@Override
	public Integer getAllEnvelopCountByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(a) from " + entityClass.getSimpleName() + " a inner join a.rfxEvent sp where sp.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public boolean isExists(T rftEnvelop, String eventId) {
		StringBuilder hsql = new StringBuilder("from " + entityClass.getSimpleName() + " re inner join re.rfxEvent ev where upper(re.envelopTitle) = :envelopTitle and ev.id =:eventId");
		if (StringUtils.checkString(rftEnvelop.getId()).length() > 0) {
			hsql.append(" and re.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("envelopTitle", rftEnvelop.getEnvelopTitle().toUpperCase());
		query.setParameter("eventId", eventId);
		if (StringUtils.checkString(rftEnvelop.getId()).length() > 0) {
			query.setParameter("id", rftEnvelop.getId());
		}
		List<RftEnvelop> envelopeList = query.getResultList();
		return CollectionUtil.isNotEmpty(envelopeList);
	}

	@Override
	public List<String> getBqsByEnvelopId(List<String> envelopId) {
		final Query query = getEntityManager().createQuery("select distinct bq.id from " + entityClass.getSimpleName() + " a inner join a.bqList bq  where a.id  in (:id)");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public List<String> getCqsByEnvelopId(List<String> envelopId) {
		final Query query = getEntityManager().createQuery("select distinct cq.id from " + entityClass.getSimpleName() + " e inner join e.cqList cq  where e.id  in (:id)");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public List<BqPojo> getBqNameAndIdsByEnvelopId(List<String> envelopId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.pojo.BqPojo(bq.id, bq.name)  from " + entityClass.getSimpleName() + " a inner join a.bqList bq  where a.id  in (:id)");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public List<T> getAllPlainEnvelopByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct a from " + entityClass.getSimpleName() + " a inner join fetch a.rfxEvent sp where sp.id =:id");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public List<Envelop> getPlainEnvelopByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.Envelop(a.id, a.envelopTitle, a.leadEvaluater, a.envelopType, a.isOpen) from " + entityClass.getSimpleName() + " a inner join a.rfxEvent sp where sp.id =:id");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public List<CqPojo> getCqsIdListByEnvelopIdByOrder(List<String> envelopId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.pojo.CqPojo(cq.id, cq.name, cq.cqOrder)  from " + entityClass.getSimpleName() + " a inner join a.cqList cq  where a.id in (:id) order by cq.cqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public List<BqPojo> getBqsIdListByEnvelopIdByOrder(List<String> envelopId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.pojo.BqPojo(bq.id, bq.name, bq.bqOrder)  from " + entityClass.getSimpleName() + " a inner join a.bqList bq  where a.id in (:id) order by bq.bqOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}

	@Override
	public List<SorPojo> getSorsIdListByEnvelopIdByOrder(List<String> envelopId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.pojo.SorPojo(bq.id, bq.name, bq.sorOrder)  from " + entityClass.getSimpleName() + " a inner join a.sorList bq  where a.id in (:id) order by bq.sorOrder");
		query.setParameter("id", envelopId);
		return query.getResultList();
	}
}
