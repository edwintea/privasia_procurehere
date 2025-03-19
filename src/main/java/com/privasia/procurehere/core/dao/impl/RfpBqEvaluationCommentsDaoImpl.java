package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpBqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfpBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
@Repository
public class RfpBqEvaluationCommentsDaoImpl extends GenericDaoImpl<RfpBqEvaluationComments, String> implements RfpBqEvaluationCommentsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		StringBuilder hsql = new StringBuilder("select e from RfpBqEvaluationComments e left outer join e.supplier ss inner join e.bqItem item inner join e.event event inner join e.createdBy cb  where event.id =:eventId  and  ss =:supplier and item.id =:itemId");
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
	public void delete(RfpBqEvaluationComments t) {
		final Query query = getEntityManager().createQuery("delete from RfpBqEvaluationComments e where e.id =:id");
		query.setParameter("id", t.getId());
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpBqEvaluationComments> getCommentsByBqIdAndEventId(String bqId, String eventId, User logedUser) {
		final Query query = getEntityManager().createQuery("select re from RfpBqEvaluationComments re left outer join re.supplier ss inner join re.bqItem item inner join re.event event inner join re.createdBy cb where re.bqItem.id =:bqId and re.event.id=:eventId and re.createdBy =:logedUser");
		query.setParameter("bqId", bqId);
		query.setParameter("eventId", eventId);
		query.setParameter("logedUser", logedUser);
		return query.getResultList();
	}
}
