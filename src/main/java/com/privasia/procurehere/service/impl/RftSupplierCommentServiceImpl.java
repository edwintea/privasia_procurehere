package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftSupplierCommentDao;
import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.RftSupplierComment;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.RftSupplierBqItemService;
import com.privasia.procurehere.service.RftSupplierCommentService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class RftSupplierCommentServiceImpl implements RftSupplierCommentService {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);
	@Autowired
	RftSupplierCommentDao rftSupplierCommentDao;

	@Autowired
	RftSupplierBqItemService rftSupplierBqItemService;

	@Override
	@Transactional(readOnly = false)
	public List<RftSupplierComment> saveSupplierBqComment(Comments supplierComment, String supplierId) {

		RftSupplierComment suComment = new RftSupplierComment();
		suComment.setCreatedDate(new Date());
		suComment.setCreatedBy(SecurityLibrary.getLoggedInUser());
		suComment.setComment(supplierComment.getComment());
		RftSupplierBqItem item = rftSupplierBqItemService.getSupplierBqItemByBqItemAndSupplierId(supplierComment.getBqItemId(), SecurityLibrary.getLoggedInUserTenantId());
		suComment.setRftSupplierBqItem(item);
		suComment = rftSupplierCommentDao.save(suComment);
		return getSupplierBqCommentByBqId(suComment.getRftSupplierBqItem().getId(), supplierId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RftSupplierComment> getSupplierBqCommentByBqId(String bqItemId, String supplierId) {
		List<RftSupplierComment> returnList = new ArrayList<RftSupplierComment>();
		List<RftSupplierComment> comments = rftSupplierCommentDao.findSupplierCommentByBqIdAndSupplierId(bqItemId, supplierId);
		for (RftSupplierComment comt : comments) {
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
	public List<RftSupplierComment> deleteSupplierBqComment(String remarkId, String supplierId) {
		String bqItemId = null;
		RftSupplierComment comments = rftSupplierCommentDao.findById(remarkId);
		bqItemId = comments.getRftSupplierBqItem().getId();
		rftSupplierCommentDao.delete(comments);
		return getSupplierBqCommentByBqId(bqItemId, supplierId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSupplierComments(String eventId) {
		rftSupplierCommentDao.deleteSupplierComments(eventId);
	}

}
