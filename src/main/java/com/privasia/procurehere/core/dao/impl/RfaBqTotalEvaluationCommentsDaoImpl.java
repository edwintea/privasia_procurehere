package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaBqTotalEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfaBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;

/**
 * @author Ravi
 */
@Repository
public class RfaBqTotalEvaluationCommentsDaoImpl extends GenericDaoImpl<RfaBqTotalEvaluationComments, String> implements RfaBqTotalEvaluationCommentsDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser) {
		StringBuilder hsql = new StringBuilder("select e from RfaBqTotalEvaluationComments e left outer join e.supplier ss inner join e.bq bq inner join e.event event inner join e.createdBy cb  where event.id =:eventId  and  ss.id =:supplier and bq.id =:bqId");
		if (logedInUser != null) {
			hsql.append(" and e.createdBy =:createdBy");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		query.setParameter("supplier", supplier);
		query.setParameter("bqId", bqId);
		if(logedInUser != null){
			query.setParameter("createdBy", logedInUser);
		}
		return query.getResultList();
	}

 
}
