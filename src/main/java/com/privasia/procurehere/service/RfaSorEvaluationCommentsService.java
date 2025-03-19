package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.RfaBqEvaluationComments;
import com.privasia.procurehere.core.entity.RfaSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RfaSorEvaluationCommentsService {
    List<RfaSorEvaluationComments> getCommentsForSupplierNew(Supplier findSuppById, String eventId, String cqItemId, User user, User logInUser);


    List<RfaSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);


    void saveComment(RfaSorEvaluationComments comments);


    RfaSorEvaluationComments findComment(String commentId);

    public void deleteComment(RfaSorEvaluationComments comments);
}
