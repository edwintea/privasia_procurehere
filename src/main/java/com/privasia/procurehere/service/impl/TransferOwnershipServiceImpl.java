package com.privasia.procurehere.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BudgetDao;
import com.privasia.procurehere.core.dao.GoodsReceiptNoteDao;
import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.dao.ProductContractDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.dao.SupplierFormDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceFormDao;
import com.privasia.procurehere.core.pojo.OwnerTransferPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.TransferOwnershipService;

@Service
@Transactional(readOnly = true)
public class TransferOwnershipServiceImpl implements TransferOwnershipService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	PrDao prDao;

	@Autowired
	PoDao poDao;

	@Autowired
	GoodsReceiptNoteDao goodsReceiptNoteDao;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	SourcingFormRequestDao sourcingFormRequestDao;

	@Autowired
	ProductContractDao productContractDao;

	@Autowired
	SupplierPerformanceFormDao supplierPerformanceFormDao;

	@Autowired
	BudgetDao budgetDao;

	@Autowired
	SupplierFormDao supplierFormDao;

	@Override
	@Transactional(readOnly = false)
	public void saveTransferOwnership(String fromUser, String toUser, HttpSession session, String loggedInUser) {
		LOG.info("Owner transferred from " + fromUser + " To " + toUser);

		try {
			rftEventDao.updateTransferOwnerForEvent(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while RFT owner transfer " + e1.getMessage(), e1);
		}

		try {
			rfqEventDao.updateTransferOwnerForEvent(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while RFQ owner transfer " + e1.getMessage(), e1);
		}

		try {
			rfpEventDao.updateTransferOwnerForEvent(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while RFP owner transfer " + e1.getMessage(), e1);
		}

		try {
			rfiEventDao.updateTransferOwnerForEvent(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while RFI owner transfer " + e1.getMessage(), e1);
		}

		try {
			rfaEventDao.updateTransferOwnerForEvent(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while RFA owner transfer " + e1.getMessage(), e1);
		}

		try {
			prDao.transferOwnership(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while PR owner transfer " + e1.getMessage(), e1);
		}

		try {
			poDao.updateTransferOwnerForPo(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while PO owner transfer " + e1.getMessage(), e1);
		}

		try {
			goodsReceiptNoteDao.transferOwnership(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while GRN owner transfer " + e1.getMessage(), e1);
		}

		try {
			sourcingFormRequestDao.transferOwnership(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while RFS owner transfer " + e1.getMessage(), e1);
		}

		try {
			productContractDao.transferOwnership(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while Contract owner transfer " + e1.getMessage(), e1);
		}

		try {
			supplierPerformanceFormDao.transferOwnership(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while Supplier Performance Form transfer " + e1.getMessage(), e1);
		}

		try {
			budgetDao.transferOwnership(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while Budget transfer " + e1.getMessage(), e1);
		}

		try {
			supplierFormDao.transferOwnership(fromUser, toUser);
		} catch (Exception e1) {
			LOG.error("Error while Supplier Form transfer " + e1.getMessage(), e1);
		}




		OwnerTransferPojo pojo = new OwnerTransferPojo(fromUser, toUser, loggedInUser);
		try {
			String strMessage = objectMapper.writeValueAsString(pojo);
			jmsTemplate.send("QUEUE.EVENT.OWNER.TRANSFER", new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					TextMessage objectMessage = session.createTextMessage();
					objectMessage.setText(strMessage);
					return objectMessage;
				}
			});
		} catch (Exception e) {
			LOG.error("Error sending message to queue : " + e.getMessage(), e);
		}

	}

}
