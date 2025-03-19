/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.privasia.procurehere.core.dao.RfaBqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfaBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfaBqEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RfaBqEvaluationCommentsImpl implements RfaBqEvaluationCommentsService {

	@Autowired
	RfaBqEvaluationCommentsDao evaluationCommentsDao;

	@Override
	public List<RfaBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		List<RfaBqEvaluationComments> returnList = new ArrayList<RfaBqEvaluationComments>();
		List<RfaBqEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaBqEvaluationComments comments : list) {
				comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
				comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
				returnList.add(comments.createShallowCopy());
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void SaveComment(RfaBqEvaluationComments comments) {
		evaluationCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(RfaBqEvaluationComments comments) {
		evaluationCommentsDao.delete(comments);
	}

	@Override
	public RfaBqEvaluationComments findComment(String commentId) {
		RfaBqEvaluationComments comment = evaluationCommentsDao.findById(commentId);
		comment.getCreatedBy().getId();
		comment.getComment();
		comment.getCreatedBy().getName();
		return comment;
	}

	@Override
	public List<RfaBqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logInUser) {
		List<RfaBqEvaluationComments> returnList = new ArrayList<RfaBqEvaluationComments>();
		List<RfaBqEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaBqEvaluationComments comments : list) {
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
