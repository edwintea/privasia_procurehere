/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftBqTotalEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RftBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RftBqTotalEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RftBqTotalEvaluationCommentsImpl implements RftBqTotalEvaluationCommentsService {

	@Autowired
	RftBqTotalEvaluationCommentsDao totalCommentsDao;

	@Override
	public List<RftBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser) {
		List<RftBqTotalEvaluationComments> returnList = new ArrayList<RftBqTotalEvaluationComments>();
		List<RftBqTotalEvaluationComments> list = totalCommentsDao.getComments(supplier, eventId, bqId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftBqTotalEvaluationComments comments : list) {
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
	public void SaveComment(RftBqTotalEvaluationComments comments) {
		totalCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(String comments) {
		RftBqTotalEvaluationComments comment = new RftBqTotalEvaluationComments();
		comment.setId(comments);
		totalCommentsDao.delete(comment);
	}

	@Override
	public RftBqTotalEvaluationComments findComment(String commentId) {
		return totalCommentsDao.findById(commentId);
	}

	@Override
	public List<RftBqTotalEvaluationComments> getCommentsNew(String supplier, String eventId, String bqId, User user, User logedInUser) {
		List<RftBqTotalEvaluationComments> returnList = new ArrayList<RftBqTotalEvaluationComments>();
		List<RftBqTotalEvaluationComments> list = totalCommentsDao.getComments(supplier, eventId, bqId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftBqTotalEvaluationComments comments : list) {
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
