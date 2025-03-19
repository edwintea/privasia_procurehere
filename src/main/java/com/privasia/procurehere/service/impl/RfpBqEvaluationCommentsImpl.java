/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpBqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfpBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfpBqEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RfpBqEvaluationCommentsImpl implements RfpBqEvaluationCommentsService {

	@Autowired
	RfpBqEvaluationCommentsDao evaluationCommentsDao;

	@Override
	public List<RfpBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		List<RfpBqEvaluationComments> returnList = new ArrayList<RfpBqEvaluationComments>();
		List<RfpBqEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpBqEvaluationComments comments : list) {
				comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
				comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
				returnList.add(comments.createShallowCopy());
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void SaveComment(RfpBqEvaluationComments comments) {
		evaluationCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(RfpBqEvaluationComments comments) {
		evaluationCommentsDao.delete(comments);
	}

	@Override
	public RfpBqEvaluationComments findComment(String commentId) {
		RfpBqEvaluationComments comment = evaluationCommentsDao.findById(commentId);
		comment.getCreatedBy().getId();
		comment.getComment();
		comment.getCreatedBy().getName();
		return comment;

	}

	@Override
	public List<RfpBqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logInUser) {
		List<RfpBqEvaluationComments> returnList = new ArrayList<RfpBqEvaluationComments>();
		List<RfpBqEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpBqEvaluationComments comments : list) {
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

}
