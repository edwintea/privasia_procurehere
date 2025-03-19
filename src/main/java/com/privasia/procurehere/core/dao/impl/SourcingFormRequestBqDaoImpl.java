package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SourcingFormRequestBqDao;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
@Repository
public class SourcingFormRequestBqDaoImpl extends GenericDaoImpl<SourcingFormRequestBq, String> implements SourcingFormRequestBqDao {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Override
	public boolean isBqExists(String formId, String bqId, String name) {
		String hql = "select count(*) from SourcingFormRequestBq b where b.sourcingFormRequest.id= :formId and upper (b.name)= :name ";
		if (StringUtils.checkString(bqId).length() > 0) {
			hql += " and b.id <> :id";
		}
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("name", StringUtils.checkString(name.toUpperCase()));
		query.setParameter("formId", formId);
		if (StringUtils.checkString(bqId).length() > 0) {
			query.setParameter("id", bqId);
		}
		return ((Number) query.getSingleResult()).intValue() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestBq> findBqByFormId(String formId) {
		final Query query = getEntityManager().createQuery("select distinct b from SourcingFormRequestBq b inner join fetch b.sourcingFormRequest s left outer join fetch b.bqItems it where s.id=:formId order by b.createdDate");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteBq(String bqId) {
		Query query = getEntityManager().createQuery("from SourcingFormRequestBq bq where bq.id=:bqId");
		query.setParameter("bqId", bqId);
		List<SourcingFormRequestBq> result = query.getResultList();
		for (SourcingFormRequestBq bq : result) {
			getEntityManager().remove(bq);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestBq> findBqsByFormId(String formId) {
		final Query query = getEntityManager().createQuery("select bq from SourcingFormRequestBq bq inner join fetch bq.sourcingFormRequest sf where sf.id=:formId order by bq.bqOrder");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SourcingFormRequestBq findBqItemById(String bqId) {
		try {
			final Query query = getEntityManager().createQuery("from SourcingFormRequestBq bq where bq.id=:bqId");
			query.setParameter("bqId", bqId);
			List<SourcingFormRequestBq> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			} else {
				return null;
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting Sourcing BQ ITEM : " + nr.getMessage(), nr);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestBq> findBqByFormIdByOrder(String formId) {
		final Query query = getEntityManager().createQuery("select distinct b from SourcingFormRequestBq b inner join fetch b.sourcingFormRequest s left outer join fetch b.bqItems it where s.id=:formId order by b.bqOrder, b.createdDate");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestBq> findRequestBqByFormIdByOrder(String formId) {
		final Query query = getEntityManager().createQuery("select distinct new com.privasia.procurehere.core.entity.SourcingFormRequestBq(bq.id, bq.name, bq.description, bq.bqOrder,bq.createdDate) from SourcingFormRequestBq bq left outer join bq.sourcingFormRequest s where s.id=:formId order by bq.bqOrder, bq.createdDate");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

}