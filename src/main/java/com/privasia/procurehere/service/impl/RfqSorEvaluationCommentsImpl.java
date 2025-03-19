package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfaSorEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfqSorEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfaSorEvaluationComments;
import com.privasia.procurehere.core.entity.RfqSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfaSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfqSorEvaluationCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RfqSorEvaluationCommentsImpl implements RfqSorEvaluationCommentsService {
    @Autowired
    RfqSorEvaluationCommentsDao evaluationCommentsDao;

    @Override
    public List<RfqSorEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logInUser) {
        List<RfqSorEvaluationComments> returnList = new ArrayList<RfqSorEvaluationComments>();
        List<RfqSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfqSorEvaluationComments comments : list) {
                comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
                comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
                if (comments.getCreatedBy() != null) {
                    if (logInUser.getId().equals(comments.getCreatedBy().getId())) {
                        comments.setFlagForCommentOwner(true);
                    } else {
                        comments.setFlagForCommentOwner(false);
                    }
                }
                returnList.add(comments.createShallowCopy());
            }
        }
        return returnList;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveComment(RfqSorEvaluationComments comments) {
        evaluationCommentsDao.saveOrUpdate(comments);
    }

    @Override
    public List<RfqSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
        List<RfqSorEvaluationComments> returnList = new ArrayList<RfqSorEvaluationComments>();
        List<RfqSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfqSorEvaluationComments comments : list) {
                comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
                comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
                returnList.add(comments.createShallowCopy());
            }
        }
        return returnList;
    }

    @Override
    public RfqSorEvaluationComments findComment(String commentId) {
        RfqSorEvaluationComments comment = evaluationCommentsDao.findComment(commentId);
        comment.getCreatedBy().getId();
        comment.getComment();
        comment.getCreatedBy().getName();
        return comment;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteComment(RfqSorEvaluationComments comments) {
        evaluationCommentsDao.delete(comments);
    }
}
