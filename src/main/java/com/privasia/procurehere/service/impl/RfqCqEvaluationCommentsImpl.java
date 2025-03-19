/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqCqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfqCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfqCqEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RfqCqEvaluationCommentsImpl implements RfqCqEvaluationCommentsService {

	@Autowired
	RfqCqEvaluationCommentsDao cqEvaluationCommentsDao;

	@Override
	public List<RfqCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		List<RfqCqEvaluationComments> returnList = new ArrayList<RfqCqEvaluationComments>();
		List<RfqCqEvaluationComments> list = cqEvaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqCqEvaluationComments comments : list) {
				comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
				comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
				returnList.add(comments.createShallowCopy());
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void SaveComment(RfqCqEvaluationComments comments) {
		cqEvaluationCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(RfqCqEvaluationComments comments) {
		cqEvaluationCommentsDao.delete(comments);
	}

	@Override
	public RfqCqEvaluationComments findComment(String commentId) {
		return cqEvaluationCommentsDao.findById(commentId);
	}

	@Override
	public List<RfqCqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logedInUser) {
		List<RfqCqEvaluationComments> returnList = new ArrayList<RfqCqEvaluationComments>();
		List<RfqCqEvaluationComments> list = cqEvaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqCqEvaluationComments comments : list) {
				comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
				comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
				if (comments.getCreatedBy() != null) {
					if (logedInUser.getId().equals(comments.getCreatedBy().getId())) {
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
