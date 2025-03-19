package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfiSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RfiSorEvaluationCommentsDao extends GenericDao<RfiSorEvaluationComments, String> {
    List<RfiSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

    RfiSorEvaluationComments findComment(String commentId);
}
