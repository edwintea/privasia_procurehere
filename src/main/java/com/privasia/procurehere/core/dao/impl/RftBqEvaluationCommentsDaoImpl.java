package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftBqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RftBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
@Repository
public class RftBqEvaluationCommentsDaoImpl extends GenericDaoImpl<RftBqEvaluationComments, String> implements RftBqEvaluationCommentsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		StringBuilder hsql = new StringBuilder("select e from RftBqEvaluationComments e left outer join e.supplier ss inner join e.bqItem item inner join e.event event inner join e.createdBy cb  where event.id =:eventId  and  ss =:supplier and item.id =:itemId");
		if (logedInUser != null) {
			hsql.append(" and e.createdBy =:createdBy");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplier", supplier);
		query.setParameter("itemId", cqItemId);
		if(logedInUser != null){
			query.setParameter("createdBy", logedInUser);
		}
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(RftBqEvaluationComments t) {
		final Query query = getEntityManager().createQuery("delete from RftBqEvaluationComments e where e.id =:id");
		query.setParameter("id", t.getId());
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftBqEvaluationComments> getCommentsByBqIdAndEventId(String bqId, String eventId, User logedUser) {
		final Query query = getEntityManager().createQuery("select re from RftBqEvaluationComments re left outer join re.supplier ss inner join re.bqItem item inner join re.event event inner join re.createdBy cb where re.bqItem.id =:bqId and re.event.id=:eventId and re.createdBy =:logedUser");
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("logedUser", logedUser);
		return query.getResultList();
	}
}
