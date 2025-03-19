package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftCqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RftCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
@Repository
public class RftCqEvaluationCommentsDaoImpl extends GenericDaoImpl<RftCqEvaluationComments, String> implements RftCqEvaluationCommentsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		
		StringBuffer hsql = new StringBuffer("select e from RftCqEvaluationComments e left outer join e.supplier ss inner join e.cqItem item inner join e.event event inner join e.createdBy cb  where event.id =:eventId  and  ss =:supplier and item.id =:itemId ");
		if(logedInUser != null){
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

}
