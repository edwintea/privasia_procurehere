package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.RfaBqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfaSorEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfaBqEvaluationComments;
import com.privasia.procurehere.core.entity.RfaSorEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfaSorEvaluationCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RfaSorEvaluationCommentsImpl implements RfaSorEvaluationCommentsService {
    @Autowired
    RfaSorEvaluationCommentsDao evaluationCommentsDao;

    @Override
    public List<RfaSorEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logInUser) {
        List<RfaSorEvaluationComments> returnList = new ArrayList<RfaSorEvaluationComments>();
        List<RfaSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfaSorEvaluationComments comments : list) {
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
    public List<RfaSorEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
        List<RfaSorEvaluationComments> returnList = new ArrayList<RfaSorEvaluationComments>();
        List<RfaSorEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
        if (CollectionUtil.isNotEmpty(list)) {
            for (RfaSorEvaluationComments comments : list) {
                comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
                comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
                returnList.add(comments.createShallowCopy());
            }
        }
        return returnList;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveComment(RfaSorEvaluationComments comments) {
        evaluationCommentsDao.saveOrUpdate(comments);
    }

    @Override
    public RfaSorEvaluationComments findComment(String commentId) {
        RfaSorEvaluationComments comment = evaluationCommentsDao.findComment(commentId);
        return comment;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteComment(RfaSorEvaluationComments comments) {
        evaluationCommentsDao.delete(comments);
    }
}
