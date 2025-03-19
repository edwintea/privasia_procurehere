/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaCqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfaCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfaCqEvaluationCommentsService;

/**
 * @author Priyanka Singh
 */

@Service
@Transactional(readOnly = true)
public class RfaCqEvaluationCommentsServiceImpl implements RfaCqEvaluationCommentsService {

	@Autowired
	RfaCqEvaluationCommentsDao cqEvaluationCommentsDao;

	@Override
	public List<RfaCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		List<RfaCqEvaluationComments> returnList = new ArrayList<RfaCqEvaluationComments>();
		List<RfaCqEvaluationComments> list = cqEvaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaCqEvaluationComments comments : list) {
				comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
				comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
				returnList.add(comments.createShallowCopy());
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void SaveComment(RfaCqEvaluationComments comments) {
		cqEvaluationCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(RfaCqEvaluationComments comments) {
		cqEvaluationCommentsDao.delete(comments);
	}

	@Override
	public RfaCqEvaluationComments findComment(String commentId) {
		return cqEvaluationCommentsDao.findById(commentId);
	}

	@Override
	public List<RfaCqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logedInUser) {
		List<RfaCqEvaluationComments> returnList = new ArrayList<RfaCqEvaluationComments>();
		List<RfaCqEvaluationComments> list = cqEvaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaCqEvaluationComments comments : list) {
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
