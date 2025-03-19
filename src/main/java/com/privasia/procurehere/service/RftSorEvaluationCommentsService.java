package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RftSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RftSorEvaluationCommentsService {
    List<RftSorEvaluationComments> getCommentsForSupplierNew(Supplier findSuppById, String eventId, String cqItemId, User user, User logInUser);

    void saveComment(RftSorEvaluationComments comments);


    List<RftSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

    RftSorEvaluationComments findComment(String commentId);

    public void deleteComment(RftSorEvaluationComments comments);
}
