package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.RfqSorEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfaSorEvaluationComments;
import com.privasia.procurehere.core.entity.RfqSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class RfqSorEvaluationCommentsDaoImpl extends GenericDaoImpl<RfqSorEvaluationComments, String> implements RfqSorEvaluationCommentsDao {


    @Override
    public List<RfqSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {

        StringBuilder hsql = new StringBuilder("select e from RfqSorEvaluationComments e left outer join e.supplier ss inner join e.bqItem item inner join e.event event left outer join fetch e.createdBy cb  where event.id =:eventId  and  ss =:supplier and item.id =:itemId");
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
    public RfqSorEvaluationComments findComment(String commentId) {
        final Query query = getEntityManager().createQuery("select s from RfqSorEvaluationComments s left outer join fetch s.createdBy sb where s.id=:commentId");
        query.setParameter("commentId", commentId);
        return (RfqSorEvaluationComments) query.getSingleResult();
    }

}
