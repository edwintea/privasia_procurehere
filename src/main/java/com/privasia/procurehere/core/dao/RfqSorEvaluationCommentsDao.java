package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfqSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RfqSorEvaluationCommentsDao extends GenericDao<RfqSorEvaluationComments, String> {
    List<RfqSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

    RfqSorEvaluationComments findComment(String commentId);
}
