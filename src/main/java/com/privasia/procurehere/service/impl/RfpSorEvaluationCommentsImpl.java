package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfaSorEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfpSorEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfpSorEvaluationComments;
import com.privasia.procurehere.core.entity.RftSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfaSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfpSorEvaluationCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RfpSorEvaluationCommentsImpl implements RfpSorEvaluationCommentsService {
    @Autowired
    RfpSorEvaluationCommentsDao evaluationCommentsDao;

    @Override
    public List<RfpSorEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logInUser) {
        List<RfpSorEvaluationComments> returnList = new ArrayList<RfpSorEvaluationComments>();
        List<RfpSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfpSorEvaluationComments comments : list) {
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
    public void saveComment(RfpSorEvaluationComments comments) {
        evaluationCommentsDao.saveOrUpdate(comments);
    }


    @Override
    public List<RfpSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
        List<RfpSorEvaluationComments> returnList = new ArrayList<RfpSorEvaluationComments>();
        List<RfpSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfpSorEvaluationComments comments : list) {
                comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
                comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
                returnList.add(comments.createShallowCopy());
            }
        }
        return returnList;
    }

    @Override
    public RfpSorEvaluationComments findComment(String commentId) {
        RfpSorEvaluationComments comment = evaluationCommentsDao.findComment(commentId);
        comment.getCreatedBy().getId();
        comment.getComment();
        comment.getCreatedBy().getName();
        return comment;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteComment(RfpSorEvaluationComments comments) {
        evaluationCommentsDao.delete(comments);
    }
}
