package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfpSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RfpSorEvaluationCommentsService {
    List<RfpSorEvaluationComments> getCommentsForSupplierNew(Supplier findSuppById, String eventId, String cqItemId, User user, User logInUser);

    void saveComment(RfpSorEvaluationComments comments);

    List<RfpSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

    RfpSorEvaluationComments findComment(String commentId);

    public void deleteComment(RfpSorEvaluationComments comments);
}
