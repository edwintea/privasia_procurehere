package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SourcingFormCqDao;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author sarang
 */
@Repository
public class SourcingFormCqDaoImpl extends GenericCqDaoImpl<SourcingTemplateCq, String> implements SourcingFormCqDao {
	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);
	@PersistenceContext(unitName = "entityManagerFactory")
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public boolean isCqExists(String id, String name) {
		final Query query = getEntityManager().createQuery("select count(sc) from SourcingTemplateCq sc where sc.sourcingForm.id=:id and sc.name=:name");
		query.setParameter("id", id);
		query.setParameter("name", name);
		LOG.info((Long) query.getSingleResult());
		return ((Long) query.getSingleResult() > 0);
	}

	@Override
	public SourcingTemplateCq getSourcingFormCq(String cqId) {

		final Query query = getEntityManager().createQuery("from SourcingTemplateCq s where s.id =:cqId");
		query.setParameter("cqId", cqId);

		return (SourcingTemplateCq) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingTemplateCqItem> findCqItembyCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select distinct c  from SourcingTemplateCqItem c  left outer  join  c.cq cq  where cq.id=:cqId and c.parent is null");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

	@Override
	public SourcingFormTemplate getSourcingForm(String cqId) {
		final Query query = getEntityManager().createQuery("select  (c.sourcingForm) from SourcingTemplateCq c left outer join c.sourcingForm where c.id= :cqId");
		query.setParameter("cqId", cqId);
		return (SourcingFormTemplate) query.getSingleResult();
	}

	@Override
	public boolean isCqItemExists(String cqId, String name) {
		final Query query = getEntityManager().createQuery("select count(sc) from SourcingTemplateCqItem sc where sc.cq.id=:id and sc.itemName=:name and parent is not null");
		query.setParameter("id", cqId);
		query.setParameter("name", name);

		return ((Long) query.getSingleResult() > 0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public SourcingTemplateCqItem getCqItembyCqItemId(String cqId) {
		LOG.info("CqId " + cqId);
		try {
			final Query query = getEntityManager().createQuery("from SourcingTemplateCqItem c left outer join fetch c.children cc where  c.id=:cqId");
			query.setParameter("cqId", cqId);
			List<SourcingTemplateCqItem> uList = query.getResultList();
			if (CollectionUtil.isNotEmpty(uList)) {
				return uList.get(0);
			}
		} catch (NoResultException nr) {
			LOG.info("Error while getting CQ Items : " + nr.getMessage(), nr);
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingTemplateCq> getSourcingFormCqByFormId(String templateId) {
		final Query query = getEntityManager().createQuery("select distinct(s) from SourcingTemplateCq s left outer join fetch s.cqItems eo where s.sourcingForm.id =:templateId order by s.createdDate");
		query.setParameter("templateId", templateId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingTemplateCqItem> findAllCqItembyCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select distinct c  from SourcingTemplateCqItem c  left outer  join  c.cq cq  where cq.id=:cqId");
		query.setParameter("cqId", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingTemplateCqItem> findLoadedCqItembyCqId(String cqId) {
		final Query query = getEntityManager().createQuery("select distinct a from SourcingTemplateCqItem a inner join  a.cq sp where sp.id =:id and a.parent is null order by a.level, a.order");
		query.setParameter("id", cqId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getNotSectionItemAddedCqIdsByFormId(String formId) {
		final Query query = getEntityManager().createQuery("select cq1.name from SourcingTemplateCqItem i inner join i.cq cq1 where i.sourcingForm.id = :formId and i.parent is null and  (select count(child.id) from  SourcingTemplateCqItem child where child.parent.id = i.id ) = 0");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingTemplateCq> getAllQuestionnarieByOrder(String formId) {
		final Query query = getEntityManager().createQuery("select distinct(s) from SourcingTemplateCq s left outer join fetch s.cqItems eo where s.sourcingForm.id =:formId order by s.cqOrder, s.createdDate");
		query.setParameter("formId", formId);
		return query.getResultList();
	}

}
