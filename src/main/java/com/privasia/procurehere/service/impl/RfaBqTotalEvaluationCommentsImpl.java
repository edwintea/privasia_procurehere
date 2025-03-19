/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaBqTotalEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfaBqTotalEvaluationComments;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfaBqTotalEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RfaBqTotalEvaluationCommentsImpl implements RfaBqTotalEvaluationCommentsService {

	@Autowired
	RfaBqTotalEvaluationCommentsDao totalCommentsDao;

	@Override
	public List<RfaBqTotalEvaluationComments> getComments(String supplier, String eventId, String bqId, User logedInUser) {
		List<RfaBqTotalEvaluationComments> returnList = new ArrayList<RfaBqTotalEvaluationComments>();
		List<RfaBqTotalEvaluationComments> list = totalCommentsDao.getComments(supplier, eventId, bqId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaBqTotalEvaluationComments comments : list) {
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
	public void SaveComment(RfaBqTotalEvaluationComments comments) {
		totalCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(String commentId) {
		RfaBqTotalEvaluationComments comment = new RfaBqTotalEvaluationComments();
		comment.setId(commentId);
		totalCommentsDao.delete(comment);
	}

	@Override
	public RfaBqTotalEvaluationComments findComment(String commentId) {
		return totalCommentsDao.findById(commentId);
	}

	@Override
	public List<RfaBqTotalEvaluationComments> getCommentsNew(String supplier, String eventId, String bqId, User user, User logedInUser) {
		List<RfaBqTotalEvaluationComments> returnList = new ArrayList<RfaBqTotalEvaluationComments>();
		List<RfaBqTotalEvaluationComments> list = totalCommentsDao.getComments(supplier, eventId, bqId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaBqTotalEvaluationComments comments : list) {
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
