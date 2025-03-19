package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpSupplierCommentDao;
import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.RfpSupplierComment;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfpSupplierBqItemService;
import com.privasia.procurehere.service.RfpSupplierCommentService;

/**
 * @author Vipul
 */
@Service
@Transactional(readOnly = true)
public class RfpSupplierCommentServiceImpl implements RfpSupplierCommentService {

	@Autowired
	RfpSupplierCommentDao rfpSupplierCommentDao;

	@Autowired
	RfpSupplierBqItemService rfpSupplierBqItemService;

	@Override
	@Transactional(readOnly = false)
	public List<RfpSupplierComment> saveSupplierBqComment(Comments supplierComment, String supplierId) {
		RfpSupplierComment suComment = new RfpSupplierComment();
		suComment.setCreatedDate(new Date());
		suComment.setCreatedBy(SecurityLibrary.getLoggedInUser());
		suComment.setComment(supplierComment.getComment());
		RfpSupplierBqItem persistObject = rfpSupplierBqItemService.getSupplierBqItemByBqItemAndSupplierId(supplierComment.getBqItemId(), SecurityLibrary.getLoggedInUserTenantId());
		suComment.setSupplierBqItem(persistObject);
		suComment = rfpSupplierCommentDao.saveOrUpdate(suComment);
		suComment.setBqItemId(suComment.getSupplierBqItem().getId());
		return getSupplierCommentsByBqId(suComment.getSupplierBqItem().getId(), supplierId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RfpSupplierComment> getSupplierCommentsByBqId(String bqItemId, String supplierId) {
		List<RfpSupplierComment> returnList = new ArrayList<RfpSupplierComment>();
		List<RfpSupplierComment> comments = rfpSupplierCommentDao.findSupplierCommentByBqIdAndSupplierId(bqItemId, supplierId);
		for (RfpSupplierComment comt : comments) {
			comt.setUserName(comt.getCreatedBy() != null ? comt.getCreatedBy().getName() : "");
			returnList.add(comt.createShallowCopy());
		}
		return returnList;

	}

	/*
	 * @Override
	 * @Transactional(readOnly = true) public RfpSupplierComment getSupplierBqCommentByRemarkId(String id,String
	 * supplierId) { return rfpSupplierCommentDao.findByRemarkId(id, supplierId); }
	 */

	@Override
	@Transactional(readOnly = false)
	public List<RfpSupplierComment> deleteSupplierBqComment(String remarkId, String supplierId) {
		String bqItemId = null;
		RfpSupplierComment comments = rfpSupplierCommentDao.findById(remarkId);
		bqItemId = comments.getSupplierBqItem().getId();
		rfpSupplierCommentDao.delete(comments);
		return getSupplierCommentsByBqId(bqItemId, supplierId);
	}

	@Transactional(readOnly = false)
	@Override
	public void deleteSupplierComments(String eventId) {
		rfpSupplierCommentDao.deleteSupplierComments(eventId);		
	}

}
