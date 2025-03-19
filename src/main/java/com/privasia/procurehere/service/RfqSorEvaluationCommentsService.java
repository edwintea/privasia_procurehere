package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfqSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RfqSorEvaluationCommentsService {
    List<RfqSorEvaluationComments> getCommentsForSupplierNew(Supplier findSuppById, String eventId, String cqItemId, User user, User logInUser);

    void saveComment(RfqSorEvaluationComments comments);

    List<RfqSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

    RfqSorEvaluationComments findComment(String commentId);

    public void deleteComment(RfqSorEvaluationComments comments);
}
