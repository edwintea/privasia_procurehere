package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaSupplierCommentDao;
import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.RfaSupplierComment;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfaSupplierCommentService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class RfaSupplierCommentServiceImpl implements RfaSupplierCommentService {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);
	@Autowired
	RfaSupplierCommentDao rfaSupplierCommentDao;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Override
	@Transactional(readOnly = false)
	public List<RfaSupplierComment> saveSupplierBqComment(Comments supplierComment, String supplierId) {

		RfaSupplierComment suComment = new RfaSupplierComment();
		suComment.setCreatedDate(new Date());
		suComment.setCreatedBy(SecurityLibrary.getLoggedInUser());
		suComment.setComment(supplierComment.getComment());
		RfaSupplierBqItem item = rfaSupplierBqItemService.getSupplierBqItemByBqItemAndSupplierId(supplierComment.getBqItemId(), SecurityLibrary.getLoggedInUserTenantId());
		suComment.setRfaSupplierBqItem(item);
		suComment = rfaSupplierCommentDao.save(suComment);
		return getSupplierBqCommentByBqId(suComment.getRfaSupplierBqItem().getId(), supplierId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RfaSupplierComment> getSupplierBqCommentByBqId(String bqItemId, String supplierId) {
		List<RfaSupplierComment> returnList = new ArrayList<RfaSupplierComment>();
		List<RfaSupplierComment> comments = rfaSupplierCommentDao.findSupplierCommentByBqIdAndSupplierId(bqItemId, supplierId);
		for (RfaSupplierComment comt : comments) {
			comt.setUserName(comt.getCreatedBy() != null ? comt.getCreatedBy().getName() : "");
			returnList.add(comt.createShallowCopy());
		}
		return returnList;

	}

	/*
	 * @Override
	 * @Transactional(readOnly = true) public RftSupplierComment getSupplierBqCommentByRemarkId(String id, String
	 * supplierId) { return rftSupplierCommentDao.findByRemarkId(id, supplierId); }
	 */

	@Override
	@Transactional(readOnly = false)
	public List<RfaSupplierComment> deleteSupplierBqComment(String remarkId, String supplierId) {
		String bqItemId = null;
		RfaSupplierComment comments = rfaSupplierCommentDao.findById(remarkId);
		bqItemId = comments.getRfaSupplierBqItem().getId();
		rfaSupplierCommentDao.delete(comments);
		return getSupplierBqCommentByBqId(bqItemId, supplierId);
	}

}
