/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpBqTotalEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfpBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfpBqTotalEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RfpBqTotalEvaluationCommentsImpl implements RfpBqTotalEvaluationCommentsService {

	@Autowired
	RfpBqTotalEvaluationCommentsDao totalCommentsDao;

	@Override
	public List<RfpBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser) {
		List<RfpBqTotalEvaluationComments> returnList = new ArrayList<RfpBqTotalEvaluationComments>();
		List<RfpBqTotalEvaluationComments> list = totalCommentsDao.getComments(supplier, eventId, bqId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpBqTotalEvaluationComments comments : list) {
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
	public void SaveComment(RfpBqTotalEvaluationComments comments) {
		totalCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(String commentId) {
		RfpBqTotalEvaluationComments comment = new RfpBqTotalEvaluationComments();
		comment.setId(commentId);
		totalCommentsDao.delete(comment);
	}

	@Override
	public RfpBqTotalEvaluationComments findComment(String commentId) {
		return totalCommentsDao.findById(commentId);
	}

	@Override
	public List<RfpBqTotalEvaluationComments> getCommentsNew(String supplier, String eventId, String bqId, User user, User logedInUser) {
		List<RfpBqTotalEvaluationComments> returnList = new ArrayList<RfpBqTotalEvaluationComments>();
		List<RfpBqTotalEvaluationComments> list = totalCommentsDao.getComments(supplier, eventId, bqId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpBqTotalEvaluationComments comments : list) {
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
