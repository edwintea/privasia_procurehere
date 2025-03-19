/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.dao.PrTemplateDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RfxTemplateDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PurgeDataService;

/**
 * @author yogesh
 */
@Service
@Transactional(readOnly = true)
public class PurgeDataServiceImpl implements PurgeDataService {

	private static Logger LOG = LogManager.getLogger(PurgeDataServiceImpl.class);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	PrDao prDao;

	@Autowired
	PrTemplateDao prTemplateDao;

	@Autowired
	RfxTemplateDao rfxTemplateDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	UserDao userDao;

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Override
	@Transactional(readOnly = false)
	public void deleteBuyerData(String buyerId) {

		LOG.info("-----------deleting------for tenantId--------" + buyerId);
		LOG.info("-----------deleting done now time to PR--------------");
		deletePrData(buyerId);
		LOG.info("-----------deleting done now time to RFA--------------");
		deleteRfaEventData(buyerId);
		LOG.info("-----------deleting done now time to RFT--------------");
		deleteRftEventData(buyerId);
		LOG.info("-----------deleting done now time to RFI--------------");
		deleteRfiEventData(buyerId);
		LOG.info("-----------deleting done now time to RFP--------------");
		deleteRfpEventData(buyerId);
		LOG.info("-----------deleting done now time to RFQ--------------");
		deleteRfqEventData(buyerId);
		LOG.info("-----------deleting done now time to Template--------------");

		List<String> userIdList = userDao.getUserIdList(buyerId);
		for (String uid : userIdList) {
			LOG.info("-----------deleting user found--------------" + uid + "--------------" + buyerId);
			deletePrTemplate(uid);
			deleteEventTemplate(uid);
		}

		deletePrTemplate(buyerId);
		deleteEventTemplate(buyerId);

		LOG.info("-----------deleting done now time master data--------------");
		buyerDao.deleteAuditTrail(buyerId);
		buyerDao.deleteBuyerSettings(buyerId);
		buyerDao.deleteCostCenter(buyerId);
		buyerDao.deleteBusinessUnit(buyerId);
		buyerDao.deleteProductItem(buyerId);
		buyerDao.deleteProductCategory(buyerId);
		LOG.info("-----------deleting done now time to Fev Supplier--------------");
		deleteFavoriteSupplier(buyerId);

		buyerDao.deleteUOM(buyerId);
		buyerDao.deleteBuyerAddress(buyerId);
		buyerDao.deleteIdSettings(buyerId);

		LOG.info("-----------deleting done now time to buyer--------------");

		buyerDao.setNullBuyerPackageBuyerSubscription(buyerId);

		LOG.info("-----------deleting done now time to buyer Package--------------");

		buyerDao.deleteBuyerPackage(buyerId);
		buyerDao.deleteBuyerPaymentTransaction(buyerId);
		buyerDao.deleteBuyerSubscription(buyerId, false);
		buyerDao.deleteBuyerSubscription(buyerId, true);
		buyerDao.setUserNullInBuyer(buyerId);
		deleteUser(buyerId);
		buyerDao.deleteBuyerById(buyerId);

	}

	private void deleteUser(String buyerId) {
		userDao.deleteNotifactions(buyerId);
		userDao.deleteBuyerNotifactions(buyerId);
		List<String> userIdList = userDao.getUserIdList(buyerId);
		for (String uid : userIdList) {
			userDao.deletePasswordHistory(uid);
			userDao.deleteErpAudit(uid);
			userDao.deleteNote(uid);
			userDao.deleteSecurityToken(uid);
			userDao.setModifyNull(uid);
			userDao.deleteBuyerUsers(uid);
		}
		userDao.deleteSecurityToken(buyerId);
		userDao.setModifyNull(buyerId);
		userDao.deleteBuyerUsers(buyerId);

		//userDao.deleteSecurityToken(buyerId);
		userDao.deleteUsers(buyerId);

	}

	private void deleteEventTemplate(String buyerId) {

		rfaEventDao.deleteRfxrecord(buyerId);
		rfaEventDao.deleteEventTemplateFieldByTanent(buyerId);
		LOG.info("-----------deleting start event template--------------");
		List<String> eventIdList = rfaEventDao.getEventTemplateIdList(buyerId);
		for (String rfxTemplateId : eventIdList) {
			List<String> eventApprovalsIdList = rfaEventDao.getEventTemplateApprovalIdList(rfxTemplateId);
			for (String apid : eventApprovalsIdList) {
				rfaEventDao.deleteEventTemplateApprovalUserByAproval(apid);
				rfaEventDao.deleteEventTemplateApprovalById(apid);
			}
			rfaEventDao.deleteEventTemplateByID(rfxTemplateId);
			/*
			 * RfxTemplate template = new RfxTemplate(); template.setId(rfxTemplateId); rfxTemplateDao.delete(template);
			 */
		}
		rfaEventDao.deleteEventTemplateByTanent(buyerId);
		LOG.info("-----------deleting done event template--------------");
	}

	private void deletePrTemplate(String buyerId) {
		prDao.deletePrTemplateFieldByTanent(buyerId);
		LOG.info("-----------deleting start pr template--------------");
		List<String> prIdList = prDao.getPrTemplateIdList(buyerId);
		for (String prTemplateId : prIdList) {
			prDao.deletePrrecord(prTemplateId);
			List<String> prApprovalsIdList = prDao.getPrTemplateApprovalIdList(prTemplateId);
			for (String apid : prApprovalsIdList) {
				prDao.deletePrTemplateApprovalUserByAproval(apid);
				prDao.deletePrTemplateApprovalById(apid);
			}
			prDao.deletePrTemplateById(prTemplateId);
			/*
			 * PrTemplate t = new PrTemplate(); t.setId(prTemplateId); prTemplateDao.delete(t);
			 */
		}
		prDao.deletePrTemplateByTanent(buyerId);
		LOG.info("-----------deleting done pr template--------------");
	}

	private void deleteFavoriteSupplier(String buyerId) {

		buyerDao.deleteFavouriteSupplierByTanent(buyerId);
		buyerDao.deleteIndustryCategoryByTanent(buyerId);

		//	List<String> eventIdList = buyerDao.getIndsCatIdList(buyerId);

	}

	private void deleteRfqEventData(String buyerId) {
		List<String> eventIdList = rfqEventDao.getEventIdList(buyerId);
		LOG.info("-----------deleting--------------" + eventIdList.size());

		rfqEventDao.deleteMeetingDoc(buyerId);

		rfqEventDao.deleteAwardAudit(buyerId);
		for (String eventId : eventIdList) {

			rfqEventDao.deleteAudit(eventId);
			rfqEventDao.deleteTimeLine(eventId);
			LOG.info("-----------deleting-------------eventId-" + eventId);
			rfqEventDao.deleteAllDocument(eventId);
			rfqEventDao.deleteAllComments(eventId);

			List<String> mettingId = rfqEventDao.getEventMeettingIds(eventId);
			for (String meetingId : mettingId) {
				LOG.info("-----------deleting-------------metting contact-" + meetingId);
				rfqEventDao.deleteMettingContact(meetingId);
			}
			rfqEventDao.deleteSupplierMeetingAtt(eventId);
			rfqEventDao.deleteMeetingReminder(eventId);
			rfqEventDao.deleteEventMessage(eventId, false);
			rfqEventDao.deleteEventMessage(eventId, true);
			rfqEventDao.deleteBuyerTeam(eventId);
			rfqEventDao.deleteSupplierTeam(eventId);
			rfqEventDao.deleteEventSupplier(eventId);
			rfqEventDao.deleteEventReminder(eventId);
			rfqEventDao.deleteMeeting(eventId);

			List<String> envelopList = rfqEventDao.getenvelopIdList(eventId);
			for (String envelopID : envelopList) {
				LOG.info("-----------deleting-------------envelopID-" + envelopID);
				rfqEventDao.deleteEvaluatorUser(envelopID);
			}
			if (CollectionUtil.isNotEmpty(envelopList)) {
				LOG.info("-----------deleting-------------envelop");
				rfqEventDao.deleteEnvelop(eventId);
			}

			rfqEventDao.deleteEventAddress(eventId);

			List<String> aprovalList = rfqEventDao.getApprovalIdList(eventId);
			for (String aprovalID : aprovalList) {
				LOG.info("-----------deleting-------------aprovalID-" + aprovalID);
				rfqEventDao.deleteApprovalUser(aprovalID);
			}
			if (CollectionUtil.isNotEmpty(aprovalList)) {
				LOG.info("-----------deleting-------------aproval");
				rfqEventDao.deleteAproval(eventId);
			}

			List<String> bqList = rfqEventDao.getBqIdList(eventId);
			for (String bqId : bqList) {

				List<String> bqItemList = rfqEventDao.getBqItemList(bqId);
				for (String bqItemId : bqItemList) {
					LOG.info("-----------deleting-------------bqItemId-" + bqItemId);
					rfqEventDao.deleteSupplierComments(bqItemId);

					LOG.info("-----------deleting-------------Child  deleting");
					rfqEventDao.deleteBqSupplierItem(bqItemId, false);
					LOG.info("-----------deleting-------------parent deleting");
					rfqEventDao.deleteBqSupplierItem(bqItemId, true);
					rfqEventDao.deleteAwardDetailbyItem(bqItemId);

				}

				List<String> supbqItemList = rfqEventDao.getSppBqItemList(bqId);
				for (String bqItemId : supbqItemList) {
					LOG.info("-----------deleting-------------bqItemId comments-" + bqItemId);
					rfqEventDao.deleteSupplierComments(bqItemId);

					LOG.info("-----------deleting-------------bq Child  deleting");
					rfqEventDao.deleteBqItem(bqItemId, false);
					LOG.info("-----------deleting-------------bq parent  deleting");
					rfqEventDao.deleteBqItem(bqItemId, true);

					rfqEventDao.deleteAwardDetailbyItem(bqItemId);
				}

				LOG.info("-----------deletingdetails-------------bqId-" + bqId);
				LOG.info("-----------deleting-------------bqId-" + bqId);
				rfqEventDao.deleteEventAwardByBq(bqId);

			}

			/*
			 * List<String> supbqList = rfqEventDao.getSupplierBqIdList(eventId); for (String bqId : supbqList) {
			 * LOG.info("-----------deleting-------------supbqId-"+bqId); rfqEventDao.deleteAwardDetail(bqId);
			 * LOG.info("-----------deleting-------------bq Child  deleting"); rfqEventDao.deleteBqItem(bqId, false);
			 * LOG.info("-----------deleting-------------bq parent  deleting"); rfqEventDao.deleteBqItem(bqId, true); }
			 */

			/*
			 * if (CollectionUtil.isNotEmpty(supbqList)) { LOG.info("-----------deleting-------------supbq"); }
			 */

			LOG.info("-----------deleting award -------------");
			rfqEventDao.deleteAwardDetails(eventId);
			LOG.info("-----------deleting award -------------");
			rfqEventDao.deleteEventAward(eventId);

			if (CollectionUtil.isNotEmpty(bqList)) {
				LOG.info("-----------deleting-------suplier------bqList");
				rfqEventDao.deleteSupplierBqItems(eventId, false);
				rfqEventDao.deleteSupplierBqItems(eventId, true);
				rfqEventDao.deleteSupplierBq(eventId);
				LOG.info("-----------deleting-------buyer------bqList");
				rfqEventDao.deleteeventBqItems(eventId, false);
				rfqEventDao.deleteeventBqItems(eventId, true);
				rfqEventDao.deleteBqItems(eventId);
				rfqEventDao.deleteBq(eventId);
			}

			rfqEventDao.deleteBqEvaluationComments(eventId);
			rfqEventDao.deleteEventContact(eventId);

			rfqEventDao.deleteEvalutionCqComts(eventId);

			List<String> cqList = rfqEventDao.getCqIdList(eventId);
			for (String cqId : cqList) {
				LOG.info("-----------deleting-------------cqId" + cqId);
				List<String> cqItem = rfqEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting-------------cqItemId" + cqItemId);
					rfqEventDao.deletSuppCqOption(cqItemId);
					rfqEventDao.deletCqOption(cqItemId);

				}

			}
			List<String> supCqItem = rfqEventDao.getSupplierCqIdList(eventId);
			for (String cqId : supCqItem) {

				LOG.info("-----------deleting------sup-------cqId" + cqId);
				List<String> cqItem = rfqEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting------sup-------cqItemId" + cqItemId);
					rfqEventDao.deletSuppCqOption(cqItemId);
					rfqEventDao.deletCqOption(cqItemId);

				}
				LOG.info("-----------deleting------sup---all----cqId" + cqId);
				rfqEventDao.deleteSupplierCqOption(cqId);
			}

			LOG.info("-----------deleting cq option ---------");
			rfqEventDao.deleteSupplierCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem1-------------");
			rfqEventDao.deleteCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem2-------------");
			rfqEventDao.deleteCqParentItem(eventId);
			LOG.info("-----------deleteSupplierCqItem3-------------");
			if (CollectionUtil.isNotEmpty(cqList)) {
				LOG.info("-----------deleting-------------cq");
				rfqEventDao.deleteCq(eventId);
			}

			List<String> meetingList = rfqEventDao.getMeetingIdList(eventId);
			for (String mid : meetingList) {
				LOG.info("-----------deleting-------------mid" + mid);
				rfqEventDao.deleteMeetingContact(mid);

			}

			LOG.info("-----------deleting-------------event id:" + eventId);
			rfqEventDao.deleteEvent(eventId);
		}

		LOG.info("--------------------------------------");
		LOG.info("-----------deleting done -------------");
		LOG.info("--------------------------------------");

	}

	private void deleteRfpEventData(String buyerId) {
		List<String> eventIdList = rfpEventDao.getEventIdList(buyerId);
		LOG.info("-----------deleting--------------" + eventIdList.size());

		rfpEventDao.deleteMeetingDoc(buyerId);

		rfpEventDao.deleteAwardAudit(buyerId);
		for (String eventId : eventIdList) {

			rfpEventDao.deleteAudit(eventId);
			rfpEventDao.deleteTimeLine(eventId);
			LOG.info("-----------deleting-------------eventId-" + eventId);
			rfpEventDao.deleteAllDocument(eventId);
			rfpEventDao.deleteAllComments(eventId);

			List<String> mettingId = rfpEventDao.getEventMeettingIds(eventId);
			for (String meetingId : mettingId) {
				LOG.info("-----------deleting-------------metting contact-" + meetingId);
				rfpEventDao.deleteMettingContact(meetingId);
			}
			rfpEventDao.deleteSupplierMeetingAtt(eventId);
			rfpEventDao.deleteMeetingReminder(eventId);
			rfpEventDao.deleteEventMessage(eventId, false);
			rfpEventDao.deleteEventMessage(eventId, true);
			rfpEventDao.deleteBuyerTeam(eventId);
			rfpEventDao.deleteSupplierTeam(eventId);
			rfpEventDao.deleteEventSupplier(eventId);
			rfpEventDao.deleteEventReminder(eventId);
			rfpEventDao.deleteMeeting(eventId);

			List<String> envelopList = rfpEventDao.getenvelopIdList(eventId);
			for (String envelopID : envelopList) {
				LOG.info("-----------deleting-------------envelopID-" + envelopID);
				rfpEventDao.deleteEvaluatorUser(envelopID);
			}
			if (CollectionUtil.isNotEmpty(envelopList)) {
				LOG.info("-----------deleting-------------envelop");
				rfpEventDao.deleteEnvelop(eventId);
			}

			rfpEventDao.deleteEventAddress(eventId);

			List<String> aprovalList = rfpEventDao.getApprovalIdList(eventId);
			for (String aprovalID : aprovalList) {
				LOG.info("-----------deleting-------------aprovalID-" + aprovalID);
				rfpEventDao.deleteApprovalUser(aprovalID);
			}
			if (CollectionUtil.isNotEmpty(aprovalList)) {
				LOG.info("-----------deleting-------------aproval");
				rfpEventDao.deleteAproval(eventId);
			}

			List<String> bqList = rfpEventDao.getBqIdList(eventId);
			for (String bqId : bqList) {

				List<String> bqItemList = rfpEventDao.getBqItemList(bqId);
				for (String bqItemId : bqItemList) {
					LOG.info("-----------deleting-------------bqItemId-" + bqItemId);
					rfpEventDao.deleteSupplierComments(bqItemId);

					LOG.info("-----------deleting-------------Child  deleting");
					rfpEventDao.deleteBqSupplierItem(bqItemId, false);
					LOG.info("-----------deleting-------------parent deleting");
					rfpEventDao.deleteBqSupplierItem(bqItemId, true);
					rfpEventDao.deleteAwardDetailbyItem(bqItemId);

				}

				List<String> supbqItemList = rfpEventDao.getSppBqItemList(bqId);
				for (String bqItemId : supbqItemList) {
					LOG.info("-----------deleting-------------bqItemId comments-" + bqItemId);
					rfpEventDao.deleteSupplierComments(bqItemId);

					LOG.info("-----------deleting-------------bq Child  deleting");
					rfpEventDao.deleteBqItem(bqItemId, false);
					LOG.info("-----------deleting-------------bq parent  deleting");
					rfpEventDao.deleteBqItem(bqItemId, true);

					rfpEventDao.deleteAwardDetailbyItem(bqItemId);
				}

				LOG.info("-----------deletingdetails-------------bqId-" + bqId);
				LOG.info("-----------deleting-------------bqId-" + bqId);
				rfpEventDao.deleteEventAwardByBq(bqId);

			}

			/*
			 * List<String> supbqList = rfpEventDao.getSupplierBqIdList(eventId); for (String bqId : supbqList) {
			 * LOG.info("-----------deleting-------------supbqId-"+bqId); rfpEventDao.deleteAwardDetail(bqId);
			 * LOG.info("-----------deleting-------------bq Child  deleting"); rfpEventDao.deleteBqItem(bqId, false);
			 * LOG.info("-----------deleting-------------bq parent  deleting"); rfpEventDao.deleteBqItem(bqId, true); }
			 */

			/*
			 * if (CollectionUtil.isNotEmpty(supbqList)) { LOG.info("-----------deleting-------------supbq"); }
			 */

			LOG.info("-----------deleting award -------------");
			rfpEventDao.deleteAwardDetails(eventId);
			LOG.info("-----------deleting award -------------");
			rfpEventDao.deleteEventAward(eventId);

			if (CollectionUtil.isNotEmpty(bqList)) {
				LOG.info("-----------deleting-------suplier------bqList");
				rfpEventDao.deleteSupplierBqItems(eventId, false);
				rfpEventDao.deleteSupplierBqItems(eventId, true);
				rfpEventDao.deleteSupplierBq(eventId);
				LOG.info("-----------deleting-------buyer------bqList");
				rfpEventDao.deleteeventBqItems(eventId, false);
				rfpEventDao.deleteeventBqItems(eventId, true);
				rfpEventDao.deleteBqItems(eventId);
				rfpEventDao.deleteBq(eventId);
			}

			rfpEventDao.deleteBqEvaluationComments(eventId);
			rfpEventDao.deleteEventContact(eventId);

			rfpEventDao.deleteEvalutionCqComts(eventId);

			List<String> cqList = rfpEventDao.getCqIdList(eventId);
			for (String cqId : cqList) {
				LOG.info("-----------deleting-------------cqId" + cqId);
				List<String> cqItem = rfpEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting-------------cqItemId" + cqItemId);
					rfpEventDao.deletSuppCqOption(cqItemId);
					rfpEventDao.deletCqOption(cqItemId);

				}

			}
			List<String> supCqItem = rfpEventDao.getSupplierCqIdList(eventId);
			for (String cqId : supCqItem) {

				LOG.info("-----------deleting------sup-------cqId" + cqId);
				List<String> cqItem = rfpEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting------sup-------cqItemId" + cqItemId);
					rfpEventDao.deletSuppCqOption(cqItemId);
					rfpEventDao.deletCqOption(cqItemId);

				}
				LOG.info("-----------deleting------sup---all----cqId" + cqId);
				rfpEventDao.deleteSupplierCqOption(cqId);
			}

			LOG.info("-----------deleting cq option ---------");
			rfpEventDao.deleteSupplierCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem1-------------");
			rfpEventDao.deleteCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem2-------------");
			rfpEventDao.deleteCqParentItem(eventId);
			LOG.info("-----------deleteSupplierCqItem3-------------");
			if (CollectionUtil.isNotEmpty(cqList)) {
				LOG.info("-----------deleting-------------cq");
				rfpEventDao.deleteCq(eventId);
			}

			List<String> meetingList = rfpEventDao.getMeetingIdList(eventId);
			for (String mid : meetingList) {
				LOG.info("-----------deleting-------------mid" + mid);
				rfpEventDao.deleteMeetingContact(mid);

			}

			LOG.info("-----------deleting-------------event id:" + eventId);
			rfpEventDao.deleteEvent(eventId);
		}

		LOG.info("--------------------------------------");
		LOG.info("-----------deleting done -------------");
		LOG.info("--------------------------------------");

	}

	private void deleteRfiEventData(String buyerId) {

		List<String> eventIdList = rfiEventDao.getEventIdList(buyerId);
		LOG.info("-----------deleting--------------" + eventIdList.size());

		rfiEventDao.deleteMeetingDoc(buyerId);

		for (String eventId : eventIdList) {

			rfiEventDao.deleteAudit(eventId);
			rfiEventDao.deleteTimeLine(eventId);
			LOG.info("-----------deleting-------------eventId-" + eventId);
			rfiEventDao.deleteAllDocument(eventId);
			rfiEventDao.deleteAllComments(eventId);

			List<String> mettingId = rfiEventDao.getEventMeettingIds(eventId);
			for (String meetingId : mettingId) {
				LOG.info("-----------deleting-------------metting contact-" + meetingId);
				rfiEventDao.deleteMettingContact(meetingId);
			}
			rfiEventDao.deleteSupplierMeetingAtt(eventId);
			rfiEventDao.deleteMeetingReminder(eventId);
			rfiEventDao.deleteEventMessage(eventId, false);
			rfiEventDao.deleteEventMessage(eventId, true);
			rfiEventDao.deleteSupplierTeam(eventId);
			rfiEventDao.deleteBuyerTeam(eventId);
			rfiEventDao.deleteEventSupplier(eventId);
			rfiEventDao.deleteEventReminder(eventId);
			rfiEventDao.deleteMeeting(eventId);

			List<String> envelopList = rfiEventDao.getenvelopIdList(eventId);
			for (String envelopID : envelopList) {
				LOG.info("-----------deleting-------------envelopID-" + envelopID);
				rfiEventDao.deleteEvaluatorUser(envelopID);
			}
			if (CollectionUtil.isNotEmpty(envelopList)) {
				LOG.info("-----------deleting-------------envelop");
				rfiEventDao.deleteEnvelop(eventId);
			}

			rfiEventDao.deleteEventAddress(eventId);

			List<String> aprovalList = rfiEventDao.getApprovalIdList(eventId);
			for (String aprovalID : aprovalList) {
				LOG.info("-----------deleting-------------aprovalID-" + aprovalID);
				rfiEventDao.deleteApprovalUser(aprovalID);
			}
			if (CollectionUtil.isNotEmpty(aprovalList)) {
				LOG.info("-----------deleting-------------aproval");
				rfiEventDao.deleteAproval(eventId);
			}

			rfiEventDao.deleteEventContact(eventId);

			rfiEventDao.deleteEvalutionCqComts(eventId);

			List<String> cqList = rfiEventDao.getCqIdList(eventId);
			for (String cqId : cqList) {
				LOG.info("-----------deleting-------------cqId" + cqId);
				List<String> cqItem = rfiEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting-------------cqItemId" + cqItemId);
					rfiEventDao.deletSuppCqOption(cqItemId);
					rfiEventDao.deletCqOption(cqItemId);

				}

			}
			List<String> supCqItem = rfiEventDao.getSupplierCqIdList(eventId);
			for (String cqId : supCqItem) {

				LOG.info("-----------deleting------sup-------cqId" + cqId);
				List<String> cqItem = rfiEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting------sup-------cqItemId" + cqItemId);
					rfiEventDao.deletSuppCqOption(cqItemId);
					rfiEventDao.deletCqOption(cqItemId);

				}
				LOG.info("-----------deleting------sup---all----cqId" + cqId);
				rfiEventDao.deleteSupplierCqOption(cqId);
			}

			LOG.info("-----------deleting cq option ---------");
			rfiEventDao.deleteSupplierCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem1-------------");
			rfiEventDao.deleteCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem2-------------");
			rfiEventDao.deleteCqParentItem(eventId);
			LOG.info("-----------deleteSupplierCqItem3-------------");
			if (CollectionUtil.isNotEmpty(cqList)) {
				LOG.info("-----------deleting-------------cq");
				rfiEventDao.deleteCq(eventId);
			}

			List<String> meetingList = rfiEventDao.getMeetingIdList(eventId);
			for (String mid : meetingList) {
				LOG.info("-----------deleting-------------mid" + mid);
				rfiEventDao.deleteMeetingContact(mid);

			}

			LOG.info("-----------deleting-------------event id:" + eventId);
			rfiEventDao.deleteEvent(eventId);
		}

		LOG.info("--------------------------------------");
		LOG.info("-----------deleting done -------------");
		LOG.info("--------------------------------------");

	}

	private void deleteRftEventData(String buyerId) {
		List<String> eventIdList = rftEventDao.getEventIdList(buyerId);
		LOG.info("-----------deleting--------------" + eventIdList.size());

		rftEventDao.deleteMeetingDoc(buyerId);

		rftEventDao.deleteAwardAudit(buyerId);
		for (String eventId : eventIdList) {

			rftEventDao.deleteAudit(eventId);
			rftEventDao.deleteTimeLine(eventId);
			LOG.info("-----------deleting-------------eventId-" + eventId);
			rftEventDao.deleteAllDocument(eventId);
			rftEventDao.deleteAllComments(eventId);

			List<String> mettingId = rftEventDao.getEventMeettingIds(eventId);
			for (String meetingId : mettingId) {
				LOG.info("-----------deleting-------------metting contact-" + meetingId);
				rftEventDao.deleteMettingContact(meetingId);
			}
			rftEventDao.deleteSupplierMeetingAtt(eventId);
			rftEventDao.deleteMeetingReminder(eventId);
			rftEventDao.deleteEventMessage(eventId, false);
			rftEventDao.deleteEventMessage(eventId, true);
			rftEventDao.deleteBuyerTeam(eventId);
			rftEventDao.deleteSupplierTeam(eventId);
			rftEventDao.deleteEventSupplier(eventId);
			rftEventDao.deleteEventReminder(eventId);
			rftEventDao.deleteMeeting(eventId);

			List<String> envelopList = rftEventDao.getenvelopIdList(eventId);
			for (String envelopID : envelopList) {
				LOG.info("-----------deleting-------------envelopID-" + envelopID);
				rftEventDao.deleteEvaluatorUser(envelopID);
			}
			if (CollectionUtil.isNotEmpty(envelopList)) {
				LOG.info("-----------deleting-------------envelop");
				rftEventDao.deleteEnvelop(eventId);
			}

			rftEventDao.deleteEventAddress(eventId);

			List<String> aprovalList = rftEventDao.getApprovalIdList(eventId);
			for (String aprovalID : aprovalList) {
				LOG.info("-----------deleting-------------aprovalID-" + aprovalID);
				rftEventDao.deleteApprovalUser(aprovalID);
			}
			if (CollectionUtil.isNotEmpty(aprovalList)) {
				LOG.info("-----------deleting-------------aproval");
				rftEventDao.deleteAproval(eventId);
			}

			List<String> bqList = rftEventDao.getBqIdList(eventId);
			for (String bqId : bqList) {

				List<String> bqItemList = rftEventDao.getBqItemList(bqId);
				for (String bqItemId : bqItemList) {
					LOG.info("-----------deleting-------------bqItemId-" + bqItemId);
					rftEventDao.deleteSupplierComments(bqItemId);

					LOG.info("-----------deleting-------------Child  deleting");
					rftEventDao.deleteBqSupplierItem(bqItemId, false);
					LOG.info("-----------deleting-------------parent deleting");
					rftEventDao.deleteBqSupplierItem(bqItemId, true);
					rftEventDao.deleteAwardDetailbyItem(bqItemId);

				}

				List<String> supbqItemList = rftEventDao.getSppBqItemList(bqId);
				for (String bqItemId : supbqItemList) {
					LOG.info("-----------deleting-------------bqItemId comments-" + bqItemId);
					rftEventDao.deleteSupplierComments(bqItemId);

					LOG.info("-----------deleting-------------bq Child  deleting");
					rftEventDao.deleteBqItem(bqItemId, false);
					LOG.info("-----------deleting-------------bq parent  deleting");
					rftEventDao.deleteBqItem(bqItemId, true);

					rftEventDao.deleteAwardDetailbyItem(bqItemId);
				}

				LOG.info("-----------deletingdetails-------------bqId-" + bqId);
				LOG.info("-----------deleting-------------bqId-" + bqId);
				rftEventDao.deleteEventAwardByBq(bqId);

			}

			/*
			 * List<String> supbqList = rftEventDao.getSupplierBqIdList(eventId); for (String bqId : supbqList) {
			 * LOG.info("-----------deleting-------------supbqId-"+bqId); rftEventDao.deleteAwardDetail(bqId);
			 * LOG.info("-----------deleting-------------bq Child  deleting"); rftEventDao.deleteBqItem(bqId, false);
			 * LOG.info("-----------deleting-------------bq parent  deleting"); rftEventDao.deleteBqItem(bqId, true); }
			 */

			/*
			 * if (CollectionUtil.isNotEmpty(supbqList)) { LOG.info("-----------deleting-------------supbq"); }
			 */

			LOG.info("-----------deleting award -------------");
			rftEventDao.deleteAwardDetails(eventId);
			LOG.info("-----------deleting award -------------");
			rftEventDao.deleteEventAward(eventId);

			if (CollectionUtil.isNotEmpty(bqList)) {
				LOG.info("-----------deleting-------suplier------bqList");
				rftEventDao.deleteSupplierBqItems(eventId, false);
				rftEventDao.deleteSupplierBqItems(eventId, true);
				rftEventDao.deleteSupplierBq(eventId);
				LOG.info("-----------deleting-------buyer------bqList");
				rftEventDao.deleteeventBqItems(eventId, false);
				rftEventDao.deleteeventBqItems(eventId, true);
				rftEventDao.deleteBqItems(eventId);
				rftEventDao.deleteBq(eventId);
			}

			rftEventDao.deleteBqEvaluationComments(eventId);
			rftEventDao.deleteEventContact(eventId);

			rftEventDao.deleteEvalutionCqComts(eventId);

			List<String> cqList = rftEventDao.getCqIdList(eventId);
			for (String cqId : cqList) {
				LOG.info("-----------deleting-------------cqId" + cqId);
				List<String> cqItem = rftEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting-------------cqItemId" + cqItemId);
					rftEventDao.deletSuppCqOption(cqItemId);
					rftEventDao.deletCqOption(cqItemId);

				}

			}
			List<String> supCqItem = rftEventDao.getSupplierCqIdList(eventId);
			for (String cqId : supCqItem) {

				LOG.info("-----------deleting------sup-------cqId" + cqId);
				List<String> cqItem = rftEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting------sup-------cqItemId" + cqItemId);
					rftEventDao.deletSuppCqOption(cqItemId);
					rftEventDao.deletCqOption(cqItemId);

				}
				LOG.info("-----------deleting------sup---all----cqId" + cqId);
				rftEventDao.deleteSupplierCqOption(cqId);
			}

			LOG.info("-----------deleting cq option ---------");
			rftEventDao.deleteSupplierCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem1-------------");
			rftEventDao.deleteCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem2-------------");
			rftEventDao.deleteCqParentItem(eventId);
			LOG.info("-----------deleteSupplierCqItem3-------------");
			if (CollectionUtil.isNotEmpty(cqList)) {
				LOG.info("-----------deleting-------------cq");
				rftEventDao.deleteCq(eventId);
			}

			List<String> meetingList = rftEventDao.getMeetingIdList(eventId);
			for (String mid : meetingList) {
				LOG.info("-----------deleting-------------mid" + mid);
				rftEventDao.deleteMeetingContact(mid);

			}

			LOG.info("-----------deleting-------------event id:" + eventId);
			rftEventDao.deleteEvent(eventId);
		}

		LOG.info("--------------------------------------");
		LOG.info("-----------deleting done -------------");
		LOG.info("--------------------------------------");

	}

	private void deleteRfaEventData(String buyerId) {
		List<String> eventIdList = rfaEventDao.getEventIdList(buyerId);
		LOG.info("-----------deleting--------------");

		rfaEventDao.deleteMeetingDoc(buyerId);

		rfaEventDao.deleteAwardAudit(buyerId);
		for (String eventId : eventIdList) {

			rfaEventDao.deleteAudit(eventId);
			rfaEventDao.deleteTimeLine(eventId);
			LOG.info("-----------deleting-------------eventId-" + eventId);
			rfaEventDao.deleteAuctionBids(eventId);
			rfaEventDao.deleteAuctionRules(eventId);
			rfaEventDao.deleteAllDocument(eventId);
			rfaEventDao.deleteAllComments(eventId);

			List<String> mettingId = rfaEventDao.getEventMeettingIds(eventId);
			for (String meetingId : mettingId) {
				LOG.info("-----------deleting-------------metting contact-" + meetingId);
				rfaEventDao.deleteMettingContact(meetingId);
			}
			rfaEventDao.deleteSupplierMeetingAtt(eventId);
			rfaEventDao.deleteMeetingReminder(eventId);
			rfaEventDao.deleteEventMessage(eventId, false);
			rfaEventDao.deleteEventMessage(eventId, true);
			rfaEventDao.deleteBuyerTeam(eventId);
			rfaEventDao.deleteSupplierTeam(eventId);
			rfaEventDao.deleteEventSupplier(eventId);
			rfaEventDao.deleteEventReminder(eventId);
			rfaEventDao.deleteMeeting(eventId);

			List<String> envelopList = rfaEventDao.getenvelopIdList(eventId);
			for (String envelopID : envelopList) {
				LOG.info("-----------deleting-------------envelopID-" + envelopID);
				rfaEventDao.deleteEvaluatorUser(envelopID);
			}
			if (CollectionUtil.isNotEmpty(envelopList)) {
				LOG.info("-----------deleting-------------envelop");
				rfaEventDao.deleteEnvelop(eventId);
			}

			rfaEventDao.deleteEventAddress(eventId);

			List<String> aprovalList = rfaEventDao.getApprovalIdList(eventId);
			for (String aprovalID : aprovalList) {
				LOG.info("-----------deleting-------------aprovalID-" + aprovalID);
				rfaEventDao.deleteApprovalUser(aprovalID);
			}
			if (CollectionUtil.isNotEmpty(aprovalList)) {
				LOG.info("-----------deleting-------------aproval");
				rfaEventDao.deleteAproval(eventId);
			}

			List<String> bqList = rfaEventDao.getBqIdList(eventId);
			for (String bqId : bqList) {

				List<String> bqItemList = rfaEventDao.getBqItemList(bqId);
				for (String bqItemId : bqItemList) {
					LOG.info("-----------deleting-------------bqItemId-" + bqItemId);
					rfaEventDao.deleteSupplierComments(bqItemId);

					LOG.info("-----------deleting-------------Child  deleting");
					rfaEventDao.deleteBqSupplierItem(bqItemId, false);
					LOG.info("-----------deleting-------------parent deleting");
					rfaEventDao.deleteBqSupplierItem(bqItemId, true);
					rfaEventDao.deleteAwardDetailbyItem(bqItemId);

				}

				List<String> supbqItemList = rfaEventDao.getSppBqItemList(bqId);
				for (String bqItemId : supbqItemList) {
					LOG.info("-----------deleting-------------bqItemId comments-" + bqItemId);
					rfaEventDao.deleteSupplierComments(bqItemId);

					LOG.info("-----------deleting-------------bq Child  deleting");
					rfaEventDao.deleteBqItem(bqItemId, false);
					LOG.info("-----------deleting-------------bq parent  deleting");
					rfaEventDao.deleteBqItem(bqItemId, true);

					rfaEventDao.deleteAwardDetailbyItem(bqItemId);
				}

				LOG.info("-----------deletingdetails-------------bqId-" + bqId);
				LOG.info("-----------deleting-------------bqId-" + bqId);
				rfaEventDao.deleteEventAwardByBq(bqId);

			}

			/*
			 * List<String> supbqList = rfaEventDao.getSupplierBqIdList(eventId); for (String bqId : supbqList) {
			 * LOG.info("-----------deleting-------------supbqId-"+bqId); rfaEventDao.deleteAwardDetail(bqId);
			 * LOG.info("-----------deleting-------------bq Child  deleting"); rfaEventDao.deleteBqItem(bqId, false);
			 * LOG.info("-----------deleting-------------bq parent  deleting"); rfaEventDao.deleteBqItem(bqId, true); }
			 */

			/*
			 * if (CollectionUtil.isNotEmpty(supbqList)) { LOG.info("-----------deleting-------------supbq"); }
			 */

			LOG.info("-----------deleting award -------------");
			rfaEventDao.deleteAwardDetails(eventId);
			LOG.info("-----------deleting award -------------");
			rfaEventDao.deleteEventAward(eventId);

			if (CollectionUtil.isNotEmpty(bqList)) {
				LOG.info("-----------deleting-------suplier------bqList");
				rfaEventDao.deleteSupplierBqItems(eventId, false);
				rfaEventDao.deleteSupplierBqItems(eventId, true);
				rfaEventDao.deleteSupplierBq(eventId);
				LOG.info("-----------deleting-------buyer------bqList");
				rfaEventDao.deleteeventBqItems(eventId, false);
				rfaEventDao.deleteeventBqItems(eventId, true);
				rfaEventDao.deleteBqItems(eventId);
				rfaEventDao.deleteBq(eventId);
			}

			rfaEventDao.deleteBqEvaluationComments(eventId);
			rfaEventDao.deleteEventContact(eventId);

			rfaEventDao.deleteEvalutionCqComts(eventId);

			List<String> cqList = rfaEventDao.getCqIdList(eventId);
			for (String cqId : cqList) {
				LOG.info("-----------deleting-------------cqId" + cqId);
				List<String> cqItem = rfaEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting-------------cqItemId" + cqItemId);
					rfaEventDao.deletSuppCqOption(cqItemId);
					rfaEventDao.deletCqOption(cqItemId);

				}

			}
			List<String> supCqItem = rfaEventDao.getSupplierCqIdList(eventId);
			for (String cqId : supCqItem) {

				LOG.info("-----------deleting------sup-------cqId" + cqId);
				List<String> cqItem = rfaEventDao.getCqItemIdList(cqId);
				for (String cqItemId : cqItem) {
					LOG.info("-----------deleting------sup-------cqItemId" + cqItemId);
					rfaEventDao.deletSuppCqOption(cqItemId);
					rfaEventDao.deletCqOption(cqItemId);

				}
				LOG.info("-----------deleting------sup---all----cqId" + cqId);
				rfaEventDao.deleteSupplierCqOption(cqId);
			}

			LOG.info("-----------deleting cq option ---------");
			rfaEventDao.deleteSupplierCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem1-------------");
			rfaEventDao.deleteCqItem(eventId);
			LOG.info("-----------deleteSupplierCqItem2-------------");
			rfaEventDao.deleteCqParentItem(eventId);
			LOG.info("-----------deleteSupplierCqItem3-------------");
			if (CollectionUtil.isNotEmpty(cqList)) {
				LOG.info("-----------deleting-------------cq");
				rfaEventDao.deleteCq(eventId);
			}

			List<String> meetingList = rfaEventDao.getMeetingIdList(eventId);
			for (String mid : meetingList) {
				LOG.info("-----------deleting-------------mid" + mid);
				rfaEventDao.deleteMeetingContact(mid);

			}

			LOG.info("-----------deleting-------------event id:" + eventId);
			rfaEventDao.deleteEvent(eventId);
		}

		LOG.info("--------------------------------------");
		LOG.info("-----------deleting done -------------");
		LOG.info("--------------------------------------");
	}

	private void deletePrData(String buyerId) {

		LOG.info("-----------delete--------------");
		prDao.deleteErpSettings(buyerId);
		List<String> prIdlist = prDao.getPrIdList(buyerId);

		prDao.deletePrItem(buyerId);
		prDao.deletePrItemParent(buyerId);
		prDao.deletePrAudit(buyerId);
		LOG.info("-----------delete PR--------------");
		for (String prId : prIdlist) {

			LOG.info("-----------delete PRID:-" + prId);
			prDao.deletePrDoc(prId);
			prDao.deletePoDoc(prId);
			List<String> prAprrove = prDao.getPrAprovalIdList(prId);
			for (String prApproveId : prAprrove) {
				LOG.info("-----------delete prApproveId:-" + prApproveId);
				prDao.removePrApprovalUserByPrApprove(prApproveId);
			}
			if (CollectionUtil.isNotEmpty(prAprrove)) {
				LOG.info("-----------delete prAprrove");
				prDao.removePrApproval(prId);
			}
			prDao.deletePrComments(prId);
			prDao.deletePrContacts(prId);
			prDao.deletePrTeam(prId);
			//TODO: pr is not deleting pls check for finance data 
			prDao.deletePr(prId);
		}

		LOG.info("-----------delete done");
	}

	/*
	 * @Override public BuyerSettings getBuyerSettings(String buyerId) { return
	 * buyerSettingsDao.getBuyerSettingsByTenantId(buyerId); }
	 */

	public void sendPurgeErrorEmailAdmin(String buyerId) {
		Buyer buyer = buyerDao.findById(buyerId);
		User mailTo = userDao.getAdminUser();
		LOG.info("Sending acount closing request email to Admin (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

		String subject = "Error while purge Data for buyer";
		String url = APP_URL;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());

		if (buyer != null) {
			map.put("message", "Error while purging Data for buyer : " + buyer.getCompanyName());
		} else {
			LOG.info("buyer not found");
			map.put("message", "Error while purging Data for buyerId : " + buyerId);
		}
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("appUrl", url);
		map.put("loginUrl", APP_URL + "/login");
		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			notificationService.sendEmail(mailTo.getCommunicationEmail(), subject, map, "purgeAccountError.ftl");

		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
		}

	}

}
