package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfiSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RfiSorEvaluationCommentsService {
    List<RfiSorEvaluationComments> getCommentsForSupplierNew(Supplier findSuppById, String eventId, String cqItemId, User user, User logInUser);


    List<RfiSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);


    void saveComment(RfiSorEvaluationComments comments);


    RfiSorEvaluationComments findComment(String commentId);

    public void deleteComment(RfiSorEvaluationComments comments);
}
