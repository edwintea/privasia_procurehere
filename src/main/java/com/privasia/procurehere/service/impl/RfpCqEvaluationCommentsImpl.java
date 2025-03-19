/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpCqEvaluationCommentsDao;
import com.privasia.procurehere.core.entity.RfpCqEvaluationComments;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.RfpCqEvaluationCommentsService;

/**
 * @author Arc
 */

@Service
@Transactional(readOnly = true)
public class RfpCqEvaluationCommentsImpl implements RfpCqEvaluationCommentsService {

	@Autowired
	RfpCqEvaluationCommentsDao cqEvaluationCommentsDao;

	@Override
	public List<RfpCqEvaluationComments> getCommentsForSupplier(Supplier supplier, String eventId, String cqItemId, User logedInUser) {
		List<RfpCqEvaluationComments> returnList = new ArrayList<RfpCqEvaluationComments>();
		List<RfpCqEvaluationComments> list = cqEvaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, logedInUser);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpCqEvaluationComments comments : list) {
				comments.setUserName(comments.getCreatedBy() != null ? comments.getCreatedBy().getName() : "");
				comments.setLoginName(comments.getCreatedBy() != null ? comments.getCreatedBy().getLoginId() : "");
				returnList.add(comments.createShallowCopy());
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void SaveComment(RfpCqEvaluationComments comments) {
		cqEvaluationCommentsDao.saveOrUpdate(comments);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteComment(RfpCqEvaluationComments comments) {
		cqEvaluationCommentsDao.delete(comments);
	}

	
	@Override
	public RfpCqEvaluationComments findComment(String commentId){
		return cqEvaluationCommentsDao.findById(commentId);
	}

	@Override
	public List<RfpCqEvaluationComments> getCommentsForSupplierNew(Supplier supplier, String eventId, String cqItemId, User user, User logedInUser) {
		List<RfpCqEvaluationComments> returnList = new ArrayList<RfpCqEvaluationComments>();
		List<RfpCqEvaluationComments> list = cqEvaluationCommentsDao.getCommentsForSupplier(supplier, eventId, cqItemId, user);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpCqEvaluationComments comments : list) {
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
