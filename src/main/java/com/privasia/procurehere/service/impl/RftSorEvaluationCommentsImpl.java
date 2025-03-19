package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RftSorEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RftSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RftSorEvaluationCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RftSorEvaluationCommentsImpl implements RftSorEvaluationCommentsService {
    @Autowired
    RftSorEvaluationCommentsDao evaluationCommentsDao;

    @Override
    public List<RftSorEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logInUser) {
        List<RftSorEvaluationComments> returnList = new ArrayList<RftSorEvaluationComments>();
        List<RftSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RftSorEvaluationComments comments : list) {
                comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
                comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
                if (comments.getCreatedBy() != null) {
                    comments.setFlagForCommentOwner(logInUser.getId().equals(comments.getCreatedBy().getId()));
                }
                returnList.add(comments.createShallowCopy());
            }
        }
        return returnList;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveComment(RftSorEvaluationComments comments) {
        evaluationCommentsDao.saveOrUpdate(comments);
    }

    @Override
    public List<RftSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
        List<RftSorEvaluationComments> returnList = new ArrayList<RftSorEvaluationComments>();
        List<RftSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RftSorEvaluationComments comments : list) {
                comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
                comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
                returnList.add(comments.createShallowCopy());
            }
        }
        return returnList;
    }

    @Override
    public RftSorEvaluationComments findComment(String commentId) {
        RftSorEvaluationComments comment = evaluationCommentsDao.findComment(commentId);
        comment.getCreatedBy().getId();
        comment.getComment();
        comment.getCreatedBy().getName();
        return comment;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteComment(RftSorEvaluationComments comments) {
        evaluationCommentsDao.delete(comments);
    }
}
