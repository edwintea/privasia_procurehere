package com.privasia.procurehere.core.dao.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.SourcingFormRequestCqItemDao;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqOption;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author pooja
 */
@Repository
public class SourcingFormRequestCqItemDaoImpl extends GenericDaoImpl<SourcingFormRequestCqItem, String> implements SourcingFormRequestCqItemDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingTemplateCqItem> getCqItemsByCqIdAndTempId(String cqId, String TemplateId) {
		final Query query = getEntityManager().createQuery("select distinct s from SourcingTemplateCqItem s inner join fetch s.cq cq inner join fetch s.sourcingForm sf left outer join fetch s.children c where sf.id=:TemplateId and cq.id =:cqId  order by s.level, s.order");
		query.setParameter("cqId", cqId);
		query.setParameter("TemplateId", TemplateId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SourcingFormRequestCqItem findCqByFormIdAndCqItemId(String formId, String cqItemId) {
		final Query query = getEntityManager().createQuery("from SourcingFormRequestCqItem s inner join fetch s.cq cq left outer join fetch s.sourcingFormRequest sf left outer join fetch s.sourcingForm f where sf.id=:formId and  s.id=:cqItemId");
		query.setParameter("formId", formId);
		query.setParameter("cqItemId", cqItemId);
		List<SourcingFormRequestCqItem> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestCqItem> findAllCqItemByCqId(String cqId, String formId) {
		final Query query = getEntityManager().createQuery("select s from SourcingFormRequestCqItem s inner join  s.sourcingFormRequest r inner join fetch s.cqItem ci left outer join fetch ci.cqOptions cqOp inner join fetch ci.cq cq inner join fetch s.sourcingForm re where cq.id = :cqId and r.id = :requestFormId order by ci.level, ci.order");
		query.setParameter("requestFormId", formId);
		query.setParameter("cqId", cqId);
		List<SourcingFormRequestCqItem> list = query.getResultList();
		// Remove duplicates as you cannot use Distinct on a table with LOB column in oracle.
		// see -
		// http://stackoverflow.com/questions/9357974/error-ora-00932-inconsistent-datatypes-expected-got-blob-in-join-statement
		LinkedHashMap<String, SourcingFormRequestCqItem> map = new LinkedHashMap<String, SourcingFormRequestCqItem>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (SourcingFormRequestCqItem item : list) {
				map.put(item.getId(), item);
			}
		}
		return new ArrayList<SourcingFormRequestCqItem>(map.values());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestCqItem> findRequestCqItemByFromCqItemId(String cqItemId, String requestFormId) {
		final Query query = getEntityManager().createQuery("select s from SourcingFormRequestCqItem s inner join  s.sourcingFormRequest r inner join fetch s.cqItem ci left outer join fetch ci.cqOptions cqOp inner join fetch ci.cq cq inner join fetch s.sourcingForm re where ci.id = :cqItemId and r.id = :requestFormId order by ci.level, ci.order");
		query.setParameter("requestFormId", requestFormId);
		query.setParameter("cqItemId", cqItemId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestCqItem> getAllSourcingCqItemByRequestId(String requestId) {

		final Query query = getEntityManager().createQuery("select s from SourcingFormRequestCqItem s inner join  s.sourcingFormRequest r inner join fetch s.cqItem ci left outer join fetch ci.cqOptions cqOp inner join fetch ci.cq cq inner join fetch s.sourcingForm re where cq.id = :cqId and r.id =:requestId order by ci.level, ci.order");
		query.setParameter("requestId", requestId);
		return query.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestCqOption> getListAnswers(String id) {
		String hql = "select s.listAnswers from SourcingFormRequestCqItem s inner join  s.listAnswers where s.id=:id and s.cqItem.parent != null or s.textAnswers is not null";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getListTextAnswers(String id) {
		String hql = "select s.textAnswers from SourcingFormRequestCqItem s  where s.sourcingFormRequest.id=:id ";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public boolean resetAttachement(String itemId, String formId) {
		final Query query = getEntityManager().createQuery("update SourcingFormRequestCqItem rc set  fileName =null, fileData = null, credContentType = null where rc.sourcingFormRequest.id = :formId and rc.id = :itemId");
		query.setParameter("formId", formId);
		query.setParameter("itemId", itemId);
		int returnValue = query.executeUpdate();
		return returnValue != 0 ? true : false;
	}

	@Override
	public long getListTextAnswersByItemId(String id) {
		String hql = "select count(textAnswers) from SourcingFormRequestCqItem s  where s.cqItem.id=:id ";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		try {
			return ((Number) query.getResultList()).longValue();
		} catch (Exception e) {
			return 0;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SourcingFormRequestCqItem> findAllCqItemByCqId(String cqId) {
		String hql = "from SourcingFormRequestCqItem cqItem where cqItem.cq.id = :cqId";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("cqId", cqId);
		try {
			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public SourcingFormRequestCqOption getCqOptionsByCqItemId(String id) {
		String hql = "select cqOpt from SourcingFormRequestCqOption cqOpt where cqOpt.formCqItemRequest.id = :id";
		final Query query = getEntityManager().createQuery(hql);
		query.setParameter("id", id);
		
		List<SourcingFormRequestCqOption> list = query.getResultList();
		if (CollectionUtil.isNotEmpty(list)) {
			return list.get(0);
		} else {
			return null;
		}
		
	}
}
