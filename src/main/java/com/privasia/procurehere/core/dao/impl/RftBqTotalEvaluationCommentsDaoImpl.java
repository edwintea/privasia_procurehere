package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftBqTotalEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RftBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
@Repository
public class RftBqTotalEvaluationCommentsDaoImpl extends GenericDaoImpl<RftBqTotalEvaluationComments, String> implements RftBqTotalEvaluationCommentsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser) {
		StringBuilder hsql = new StringBuilder("select e from RftBqTotalEvaluationComments e left outer join e.supplier ss inner join e.bq bq inner join e.event event inner join e.createdBy cb  where event.id =:eventId  and  ss.id =:supplier and bq.id =:bqId");
		if(logedInUser != null){
			hsql.append(" and e.createdBy =:createdBy");
		}
		final Query query = getEntityManager().createQuery(hsql.toString() );
		query.setParameter("eventId", eventId);
		query.setParameter("supplier", supplier);
		query.setParameter("bqId", bqId);
		if(logedInUser != null){
			query.setParameter("createdBy", logedInUser);
		}
		return query.getResultList();
	}

 
}
