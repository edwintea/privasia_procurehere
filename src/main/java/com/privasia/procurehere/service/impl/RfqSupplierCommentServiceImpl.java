package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqSupplierCommentDao;
import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.RfqSupplierComment;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfqSupplierBqItemService;
import com.privasia.procurehere.service.RfqSupplierCommentService;

@Service
@Transactional(readOnly = true)
public class RfqSupplierCommentServiceImpl implements RfqSupplierCommentService {

	@Autowired
	RfqSupplierCommentDao rfqSupplierCommentDao;

	@Autowired
	RfqSupplierBqItemService rfqSupplierBqItemService;

	@Override
	@Transactional(readOnly = false)
	public List<RfqSupplierComment> saveSupplierBqComment(Comments supplierComment, String supplierId) {
		RfqSupplierComment suComment = new RfqSupplierComment();
		suComment.setCreatedDate(new Date());
		suComment.setCreatedBy(SecurityLibrary.getLoggedInUser());
		suComment.setComment(supplierComment.getComment());
		RfqSupplierBqItem persistObject = rfqSupplierBqItemService.getSupplierBqItemByBqItemAndSupplierId(supplierComment.getBqItemId(), SecurityLibrary.getLoggedInUserTenantId());
		suComment.setSupplierBqItem(persistObject);
		suComment = rfqSupplierCommentDao.saveOrUpdate(suComment);
		suComment.setBqItemId(suComment.getSupplierBqItem().getId());
		return getSupplierCommentsByBqId(suComment.getSupplierBqItem().getId(), supplierId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RfqSupplierComment> getSupplierCommentsByBqId(String bqItemId, String supplierId) {
		// return rfqSupplierCommentDao.findByBqId(bqItemId,supplierId);

		List<RfqSupplierComment> returnList = new ArrayList<RfqSupplierComment>();
		List<RfqSupplierComment> comments = rfqSupplierCommentDao.findSupplierCommentByBqIdAndSupplierId(bqItemId, supplierId);
		for (RfqSupplierComment comt : comments) {
			comt.setUserName(comt.getCreatedBy() != null ? comt.getCreatedBy().getName() : "");
			returnList.add(comt.createShallowCopy());
		}
		return returnList;

	}

	/*
	 * @Override
	 * @Transactional(readOnly = true) public RfqSupplierComment getSupplierBqCommentByRemarkId(String id ,String
	 * supplierId) { return rfqSupplierCommentDao.findByRemarkId(id,supplierId); }
	 */

	@Override
	@Transactional(readOnly = false)
	public List<RfqSupplierComment> deleteSupplierBqComment(String remarkId, String supplierId) {
		String bqItemId = null;
		RfqSupplierComment comments = rfqSupplierCommentDao.findById(remarkId);
		bqItemId = comments.getSupplierBqItem().getId();
		rfqSupplierCommentDao.delete(comments);
		return getSupplierCommentsByBqId(bqItemId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSupplierComments(String eventId) {
		rfqSupplierCommentDao.deleteSupplierComments(eventId);
	}
}
