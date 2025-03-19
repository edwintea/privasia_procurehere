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

import com.privasia.procurehere.core.dao.RfqBqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfqBqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.RfqBqEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RfqBqEvaluationCommentsImpl implements RfqBqEvaluationCommentsService {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RfqBqEvaluationCommentsDao evaluationCommentsDao;

	@Override
	public List<RfqBqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		List<RfqBqEvaluationComments> returnList = new ArrayList<RfqBqEvaluationComments>();
		List<RfqBqEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqBqEvaluationComments comments : list) {
				comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
				comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
				returnList.add(comments.createShallowCopy());
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void SaveComment(RfqBqEvaluationComments comments) {
		evaluationCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(RfqBqEvaluationComments comments) {
		LOG.info("Deleting comments : " + comments.getId());
		evaluationCommentsDao.delete(comments);
	}

	@Override
	public RfqBqEvaluationComments findComment(String commentId) {
		RfqBqEvaluationComments comment = evaluationCommentsDao.findById(commentId);
		comment.getCreatedBy().getId();
		comment.getComment();
		comment.getCreatedBy().getName();
		return comment;

	}

	@Override
	public List<RfqBqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logInUser) {
		List<RfqBqEvaluationComments> returnList = new ArrayList<RfqBqEvaluationComments>();
		List<RfqBqEvaluationComments> list = evaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqBqEvaluationComments comments : list) {
				comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
				comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
				if (comments.getCreatedBy() != null) {
					if (logInUser.getId().equals(comments.getCreatedBy().getId())) {
						comments.setFlagForCommentOwner(true);
						LOG.info("....................."+comments.getFlagForCommentOwner());
					} else {
						comments.setFlagForCommentOwner(false);
						LOG.info("...........%%%%%%%.........."+comments.getFlagForCommentOwner());
					}
				}
				returnList.add(comments.createShallowCopy());
			}
		}
		return returnList;
	}
}
