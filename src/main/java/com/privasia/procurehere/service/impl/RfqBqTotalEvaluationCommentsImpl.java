/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqBqTotalEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfqBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfqBqTotalEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RfqBqTotalEvaluationCommentsImpl implements RfqBqTotalEvaluationCommentsService {

	@Autowired
	RfqBqTotalEvaluationCommentsDao totalCommentsDao;

	@Override
	public List<RfqBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser) {
		List<RfqBqTotalEvaluationComments> returnList = new ArrayList<RfqBqTotalEvaluationComments>();
		List<RfqBqTotalEvaluationComments> list = totalCommentsDao.getComments(supplier, eventId, bqId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqBqTotalEvaluationComments comments : list) {
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

	@Override
	@Transactional(readOnly = false)
	public void SaveComment(RfqBqTotalEvaluationComments comments) {
		totalCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(String commentId) {
		RfqBqTotalEvaluationComments comment = new RfqBqTotalEvaluationComments();
		comment.setId(commentId);
		totalCommentsDao.delete(comment);
	}

	@Override
	public RfqBqTotalEvaluationComments findComment(String commentId) {
		return totalCommentsDao.findById(commentId);
	}

	@Override
	public List<RfqBqTotalEvaluationComments> getCommentsNew(String supplier, String eventId, String bqId, User user, User logedInUser) {
		List<RfqBqTotalEvaluationComments> returnList = new ArrayList<RfqBqTotalEvaluationComments>();
		List<RfqBqTotalEvaluationComments> list = totalCommentsDao.getComments(supplier, eventId, bqId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqBqTotalEvaluationComments comments : list) {
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
