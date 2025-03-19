package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfaSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RfaSorEvaluationCommentsDao extends GenericDao<RfaSorEvaluationComments, String> {
    List<RfaSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

    RfaSorEvaluationComments findComment(String commentId);
}
