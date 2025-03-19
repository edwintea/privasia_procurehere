package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.RfpSorEvaluationComments;
import com.privasia.procurehere.core.entity.RftSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

import java.util.List;

public interface RftSorEvaluationCommentsDao extends GenericDao<RftSorEvaluationComments, String> {
    List<RftSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser);

    RftSorEvaluationComments findComment(String commentId);
}
