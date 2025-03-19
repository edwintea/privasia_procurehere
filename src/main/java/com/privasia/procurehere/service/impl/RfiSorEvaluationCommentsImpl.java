package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfiSorEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfiSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfiSorEvaluationCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RfiSorEvaluationCommentsImpl implements RfiSorEvaluationCommentsService {
    @Autowired
    RfiSorEvaluationCommentsDao evaluationCommentsDao;

    @Override
    public List<RfiSorEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logInUser) {
        List<RfiSorEvaluationComments> returnList = new ArrayList<RfiSorEvaluationComments>();
        List<RfiSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfiSorEvaluationComments comments : list) {
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
    public List<RfiSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
        List<RfiSorEvaluationComments> returnList = new ArrayList<RfiSorEvaluationComments>();
        List<RfiSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfiSorEvaluationComments comments : list) {
                comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
                comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
                returnList.add(comments.createShallowCopy());
            }
        }
        return returnList;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveComment(RfiSorEvaluationComments comments) {
        evaluationCommentsDao.saveOrUpdate(comments);
    }

    @Override
    public RfiSorEvaluationComments findComment(String commentId) {
        RfiSorEvaluationComments comment = evaluationCommentsDao.findComment(commentId);
        return comment;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteComment(RfiSorEvaluationComments comments) {
        evaluationCommentsDao.delete(comments);
    }
}
