package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfpSorEvaluationComments;
import com.privasia.procurehere.core.entity.RfqSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RfpSorEvaluationCommentsDao extends GenericDao<RfpSorEvaluationComments, String> {
    List<RfpSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

    RfpSorEvaluationComments findComment(String commentId);
}
