package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.dao.RfqSorDao;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.*;
import com.privasia.procurehere.integration.PublishEventService;
import com.privasia.procurehere.service.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RfqEventServiceImpl implements RfqEventService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfqMeetingService meetingService;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	RfqBqDao rfqEventBqDao;

	@Autowired
	RfqEventContactDao rfqEventContactDao;

	@Autowired
	RfqEventCorrespondenceAddressDao rfqEventCorrespondenceAddressDao;

	@Autowired
	NaicsCodesDao naicsCodesDao;

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	RfqEventMeetingDao rfqEventMeetingDao;

	@Autowired
	RfqDocumentDao rfqDocumentDao;

	@Autowired
	RfqCqEvaluationCommentsDao cqEvaluationCommentsDao;
	@Autowired
	RfqBqItemDao rfqBqItemDao;

	@Autowired
	RfqCqDao rfqCqDao;

	@Autowired
	RfqCqItemDao rfqCqItemDao;

	@Autowired
	RfqEventMeetingDocumentDao rfqEventMeetingDocumentDao;

	@Autowired
	RfqEnvelopDao rfqEnvelopDao;

	@Autowired
	RfqEvaluatorUserDao rfqEvaluatorUserDao;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	RfqSupplierMeetingAttendanceDao rfqSupplierMeetingAttendanceDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	RfqBqTotalEvaluationCommentsService rfqBqTotalEvaluationCommentsService;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	CostCenterDao costCenterDao;

	@Autowired
	RfqReminderDao rfqReminderDao;

	@Autowired
	UserService userService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	PrService prService;

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	RfqEventTimeLineDao rfqEventTimeLineDao;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfqCqService rfqCqService;

	@Autowired
	RfqEventSupplierDao rfqEventSupplierDao;

	@Autowired
	RfqSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfqSupplierCqDao rfqSupplierCqDao;

	@Autowired
	RfqCqEvaluationCommentsService cqEvaluationCommentsService;

	@Autowired
	RfqSupplierBqItemService rfqSupplierBqItemService;

	@Autowired
	RfqSupplierSorItemService rfqSupplierSorItemService;

	@Autowired
	RfqBqEvaluationCommentsService rfqBqEvaluationCommentsService;

	@Autowired
	RfqSorEvaluationCommentsService rfqSorEvaluationCommentsService;

	@Autowired
	RfqEnvelopService rfqEnvelopService;

	@Autowired
	ServletContext context;

	@Autowired
	RfqEventAuditDao rfqEventAuditDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfpBqDao rfpBqDao;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaBqDao rfaBqDao;

	@Autowired
	RfaBqItemDao rfaBqItemDao;

	@Autowired
	RfqBqDao rfqBqDao;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RftEventSupplierDao rftEventSupplierDao;

	@Autowired
	RftBqDao rftBqDao;

	@Autowired
	RftBqItemDao rftBqItemDao;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfiEventSupplierDao rfiEventSupplierDao;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

	@Autowired
	RfpEventSupplierDao rfpEventSupplierDao;

	@Autowired
	RfpBqItemDao rfpBqItemDao;

	@Autowired
	RfqSupplierBqDao rfqSupplierBqDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	RfqSupplierCommentService rfqSupplierCommentService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	UomService uomService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	UserDao userDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	RfqDocumentService rfqDocumentService;

	@Autowired
	PublishEventService publishEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfqSupplierBqService rfqSupplierBqService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;
	@Autowired
	RfpSupplierBqService rfpSupplierBqService;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

	@Autowired
	RftSupplierBqService rftSupplierBqService;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	RfqEnvelopService envelopService;

	@Autowired
	DeclarationDao declarationDao;

	@Autowired
	RfqEvaluatorDeclarationDao rfqEvaluatorDeclarationDao;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	RfqSupplierCqOptionDao rfqSupplierCqOptionDao;

	@Autowired
	RfqCqOptionDao rfqCqOptionDao;

	@Autowired
	ProcurementMethodDao procurementMethodDao;

	@Autowired
	ProcurementCategoriesDao procurementCategoriesDao;

	@Autowired
	GroupCodeDao groupCodeDao;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	ProductContractDao productContractDao;

	@Autowired
	ProductContractItemsDao productContractItemsDao;

	@Autowired
	EventIdSettingsDao eventIdSettingDao;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	GroupCodeService groupCodeService;

	@Autowired
	ProductContractReminderService productContractReminderService;

	@Autowired
	ProductContractNotifyUsersDao productContractNotifyUsersDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	SupplierPerformanceTemplateService spTemplateService;

	@Autowired
	SupplierPerformanceFormService spFormService;

	@Autowired
	SupplierPerformanceAuditService formAuditService;

	@Autowired
	RfqEventAwardDetailsDao rfqEventAwardDetailsDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	ContractAuditDao contractAuditDao;

	@Autowired
	RfqEventAwardDao rfqEventAwardDao;

	@Autowired
	RfqSorDao rfqEventSorDao;

	@Autowired
	RfqSorItemDao rfqSorItemDao;

	@Autowired
	RfqSupplierSorDao rfqSupplierSorDao;

	@Autowired
	RfqSupplierSorItemDao rfqSupplierSorItemDao;

	@Override
	@Transactional(readOnly = false)
	public RfqEvent saveEvent(RfqEvent rfqEvent, User loggedInUser) throws SubscriptionException {
		// Check subscription limit
		Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(loggedInUser.getTenantId());

		if (buyer != null && buyer.getBuyerPackage() != null && buyer.getBuyerPackage().getPlan() != null) {
			BuyerPackage bp = buyer.getBuyerPackage();
			if (bp.getPlan().getPlanType() == PlanType.PER_USER) {
				if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
					throw new SubscriptionException("Your Subscription has Expired.");
				}
			} else {
				if (bp.getEventLimit() != null) {
					if (bp.getNoOfEvents() == null) {
						bp.setNoOfEvents(0);
					}
					if (bp.getNoOfEvents() >= bp.getEventLimit()) {
						throw new SubscriptionException("Event limit of " + bp.getEventLimit() + " reached.");
					} else if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
						throw new SubscriptionException("Your Subscription has Expired.");
					}
				}
			}
		}
		rfqEvent.setCreatedDate(new Date());
		rfqEvent.setEventOwner(loggedInUser);
		rfqEvent.setTenantId(loggedInUser.getTenantId());
		rfqEvent.setCreatedBy(loggedInUser);
		LOG.info("Save Event Name :" + rfqEvent.getEventId());
		RfqEvent dbObj = rfqEventDao.saveOrUpdate(rfqEvent);

		// If there are unsaved envelopes attached to the event, save them as well
		try {
			if (CollectionUtil.isNotEmpty(rfqEvent.getRfxEnvelop())) {
				for (RfqEnvelop env : rfqEvent.getRfxEnvelop()) {
					if (StringUtils.checkString(env.getId()).length() == 0 && env.getEnvelopTitle() != null) {
						env.setRfxEvent(dbObj);
						rfqEnvelopDao.save(env);
					}
				}
			} else {
				LOG.info("Kahan gaye envelopes bhai..... Kha gaya kya???");
			}
		} catch (Exception e) {
			LOG.error("Error saving envelope. : " + e.getMessage(), e);
		}

		// touch event owner
		LOG.info("id event when save : " + dbObj.getId());
		dbObj.getEventOwner().getLoginId();
		// documents required to upload for copied event if previous event has Cqtype.DOCUMENT_DOWNLOAD_LINK
		if (rfqEvent.isUploadDocuments()) {
			dbObj.setUploadDocuments(true);
		}
		return dbObj;
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent updateEvent(RfqEvent rfpEvent) {
		RfqEvent event = rfqEventDao.update(rfpEvent);
		event.getCreatedBy().getLoginId();
		return event;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteEvent(RfqEvent rfpEvent) {
		rfqEventDao.delete(rfpEvent);

	}

	@Override
	public RfqEvent getEventById(String id) {
		RfqEvent rfq = rfqEventDao.findById(id);
		if(rfq.getTemplate() != null){
			rfq.getTemplate().getId();
		}
		if(CollectionUtil.isNotEmpty(rfq.getSuppliers())){
			for(RfqEventSupplier supplier: rfq.getSuppliers()){
				supplier.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getDocuments())){
			for(RfqEventDocument doc : rfq.getDocuments()){
				if (doc.getUploadBy() != null) {
					doc.getUploadBy().getLoginId();
				}
				doc.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getMeetings())){
			for(RfqEventMeeting meeting: rfq.getMeetings()){
				meeting.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getEventContacts())){
			for(RfqEventContact contact : rfq.getEventContacts()){
				contact.getContactName();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getEventCorrespondenceAddress())){
			for(EventCorrespondenceAddress address: rfq.getEventCorrespondenceAddress()){
				address.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getEventBqs())){
			for(RfqEventBq bq: rfq.getEventBqs()){
				bq.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getCqs())){
			for(RfqCq cq: rfq.getCqs()) {
				cq.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getRftReminder())){
			for(RfqReminder reminder : rfq.getRftReminder()){
				reminder.getId();
			}
		}

		if(CollectionUtil.isNotEmpty(rfq.getEventViewers())){
			for(User viewr: rfq.getEventViewers()){
				viewr.getLoginId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getEventEditors())){
			for(User editor: rfq.getEventEditors()){
				editor.getLoginId();
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getTeamMembers())) {
			for (RfqTeamMember team : rfq.getTeamMembers()) {
				team.getTeamMemberType();
				if (team.getUser() != null) {
					team.getUser().getLoginId();
				}
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getAwardedSuppliers())){
			for(Supplier sup : rfq.getAwardedSuppliers()){
				sup.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getComment())){
			for(RfqComment comment: rfq.getComment()){
				comment.getComment();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getIndustryCategories())){
			for(IndustryCategory industryCategory: rfq.getIndustryCategories()){
				industryCategory.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getAwardApprovals())){
			for(RfqEventAwardApproval awardApproval: rfq.getAwardApprovals()){
				awardApproval.getLevel();
			}
		}
		if(CollectionUtil.isNotEmpty(rfq.getAwardComment())){
			for(RfqAwardComment awardComment: rfq.getAwardComment()){
				awardComment.getComment();
			}
		}
		if(rfq.getParticipationFeeCurrency() != null){
			rfq.getParticipationFeeCurrency().getId();
		}
		if(rfq.getCostCenter() != null){
			rfq.getCostCenter().getStatus();
		}
		for (RfqEventApproval approval : rfq.getApprovals()) {
			for (RfqApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}
		if(rfq.getBusinessUnit() != null){
			rfq.getBusinessUnit().getId();
		}
		if (CollectionUtil.isNotEmpty(rfq.getSuspensionApprovals())) {
			for (RfqEventSuspensionApproval approval : rfq.getSuspensionApprovals()) {
				for (RfqSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}

		if (rfq.getBaseCurrency() != null) {
			rfq.getBaseCurrency().getCurrencyCode();
		}

		for (RfqEnvelop rf : rfq.getRfxEnvelop()) {
			rf.getEnvelopTitle();
			if (CollectionUtil.isNotEmpty(rf.getOpenerUsers())) {
				for (RfqEnvelopeOpenerUser item : rf.getOpenerUsers()) {
					item.getUser();
				}
			}
		}

		if (rfq.getEventOwner() != null) {
			rfq.getEventOwner().getName();
			rfq.getEventOwner().getCommunicationEmail();
			rfq.getEventOwner().getPhoneNumber();
			if (rfq.getEventOwner().getOwner() != null) {
				Owner usr = rfq.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getUnMaskedUsers())) {
			for (RfqUnMaskedUser usr : rfq.getUnMaskedUsers()) {
				usr.getUserUnmasked();
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getEvaluationConclusionUsers())) {
			for (RfqEvaluationConclusionUser usr : rfq.getEvaluationConclusionUsers()) {
				usr.getConcluded();
				usr.getUser().getId();
			}
		}

		if (rfq.getEvaluationProcessDeclaration() != null) {
			rfq.getEvaluationProcessDeclaration().getContent();
		}

		if (rfq.getProcurementMethod() != null) {
			rfq.getProcurementMethod().getProcurementMethod();
		}
		if (rfq.getProcurementCategories() != null) {
			rfq.getProcurementCategories().getProcurementCategories();
		}
		if (rfq.getGroupCode() != null) {
			rfq.getGroupCode().getId();
			rfq.getGroupCode().getGroupCode();
		}
		return rfq;
	}

	@Override
	public RfqEvent getRfqEventByeventId(String eventId) {
		RfqEvent rfq = rfqEventDao.findByEventId(eventId);
		if (rfq.getEventOwner().getBuyer() != null) {
			rfq.getEventOwner().getBuyer().getLine1();
			rfq.getEventOwner().getBuyer().getLine2();
			rfq.getEventOwner().getBuyer().getCity();
			if (rfq.getEventOwner().getBuyer().getState() != null) {
				rfq.getEventOwner().getBuyer().getState().getStateName();
				if (rfq.getEventOwner().getBuyer().getState().getCountry() != null) {
					rfq.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (rfq.getTemplate() != null) {
			rfq.getTemplate().getTemplateName();
		}

		if (CollectionUtil.isNotEmpty(rfq.getUnMaskedUsers())) {
			for (RfqUnMaskedUser item : rfq.getUnMaskedUsers()) {
				item.getUser();
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getEvaluationConclusionUsers())) {
			for (RfqEvaluationConclusionUser item : rfq.getEvaluationConclusionUsers()) {
				item.getUser().getName();
			}
		}
		if(rfq.getDepositCurrency() != null){
			rfq.getDepositCurrency().getId();
		}

		if (CollectionUtil.isNotEmpty(rfq.getSuppliers())) {
			for (RfqEventSupplier item : rfq.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}
		if (rfq.getEventOwner().getBuyer() != null) {
			Buyer buyer = rfq.getEventOwner().getBuyer();
			buyer.getLine1();
			buyer.getLine2();
			buyer.getCity();
			if (buyer.getState() != null) {
				buyer.getState().getStateName();
				if (buyer.getState().getCountry() != null) {
					buyer.getState().getCountry().getCountryName();
				}
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getComment())) {
			for (RfqComment comment : rfq.getComment()) {
				comment.getComment();
				comment.getCreatedBy();
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getName();
				}
				comment.getLoginName();
				comment.getUserName();
				comment.getRfxEvent();
			}
		}

		if (rfq.getDeliveryAddress() != null) {
			rfq.getDeliveryAddress().getLine1();
			rfq.getDeliveryAddress().getLine2();
			rfq.getDeliveryAddress().getCity();
			if (rfq.getDeliveryAddress().getState() != null) {
				rfq.getDeliveryAddress().getState().getStateName();
				rfq.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getRftReminder())) {
			for (RfqReminder reminder : rfq.getRftReminder()) {
				reminder.getReminderDate();
				reminder.getStartReminder();
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getMeetings())) {
			for (RfqEventMeeting item : rfq.getMeetings()) {
				item.getStatus().name();
				item.getRemarks();
				item.getVenue();
				item.getAppointmentDateTime();
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingContacts())) {
					for (RfqEventMeetingContact contact : item.getRfxEventMeetingContacts()) {
						contact.getContactEmail();
						contact.getContactName();
						contact.getContactNumber();
					}
				}
				if (CollectionUtil.isNotEmpty(item.getInviteSuppliers())) {
					for (Supplier suppliers : item.getInviteSuppliers()) {
						suppliers.getCompanyName();
						suppliers.getCommunicationEmail();
						suppliers.getCompanyContactNumber();
					}
				}
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingDocument())) {
					for (RfqEventMeetingDocument docs : item.getRfxEventMeetingDocument()) {
						docs.getId();
						docs.getFileName();
						docs.getFileSizeInKb();
						docs.getCredContentType();
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getIndustryCategories())) {
			for (IndustryCategory indCat : rfq.getIndustryCategories()) {
				indCat.getCode();
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getSuspensionApprovals())) {
			for (RfqEventSuspensionApproval approver : rfq.getSuspensionApprovals()) {
				if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
					for (RfqSuspensionApprovalUser user : approver.getApprovalUsers()) {
						user.getRemarks();
						user.getUser().getCommunicationEmail();
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getAwardApprovals())) {
			for (RfqEventAwardApproval approval : rfq.getAwardApprovals()) {
				for (RfqAwardApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
					approvalUser.getUser().getCommunicationEmail();
					approvalUser.getUser();
				}
			}
		}

		if (rfq.getGroupCode() != null) {
			rfq.getGroupCode().getId();
			rfq.getGroupCode().getGroupCode();
			rfq.getGroupCode().getStatus();
		}

		if (rfq.getBusinessUnit() != null) {
			rfq.getBusinessUnit().getUnitName();
		}

		if (rfq.getCostCenter() != null) {
			rfq.getCostCenter().getCostCenter();
		}

		if (CollectionUtil.isNotEmpty(rfq.getAwardComment())) {
			for (RfqAwardComment comment : rfq.getAwardComment()) {
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getLoginId();
				}
			}
		}

		return rfq;
	}

	@Override
	public RfqEvent getRfqEventWithBuByEventId(String eventId) {
		RfqEvent rfq = rfqEventDao.findByEventId(eventId);
		if (rfq.getEventOwner().getBuyer() != null) {
			rfq.getEventOwner().getBuyer().getLine1();
			rfq.getEventOwner().getBuyer().getLine2();
			rfq.getEventOwner().getBuyer().getCity();
			if (rfq.getEventOwner().getBuyer().getState() != null) {
				rfq.getEventOwner().getBuyer().getState().getStateName();
				if (rfq.getEventOwner().getBuyer().getState().getCountry() != null) {
					rfq.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getSuppliers())) {
			for (RfqEventSupplier item : rfq.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}
		if (rfq.getEventOwner().getBuyer() != null) {
			Buyer buyer = rfq.getEventOwner().getBuyer();
			buyer.getLine1();
			buyer.getLine2();
			buyer.getCity();
			if (buyer.getState() != null) {
				buyer.getState().getStateName();
				if (buyer.getState().getCountry() != null) {
					buyer.getState().getCountry().getCountryName();
				}
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getComment())) {
			for (RfqComment comment : rfq.getComment()) {
				comment.getComment();
				comment.getCreatedBy();
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getName();
				}
				comment.getLoginName();
				comment.getUserName();
				comment.getRfxEvent();
			}
		}

		if (rfq.getDeliveryAddress() != null) {
			rfq.getDeliveryAddress().getLine1();
			rfq.getDeliveryAddress().getLine2();
			rfq.getDeliveryAddress().getCity();
			if (rfq.getDeliveryAddress().getState() != null) {
				rfq.getDeliveryAddress().getState().getStateName();
				rfq.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getRftReminder())) {
			for (RfqReminder reminder : rfq.getRftReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getMeetings())) {
			for (RfqEventMeeting item : rfq.getMeetings()) {
				item.getStatus().name();
				item.getRemarks();
				item.getVenue();
				item.getAppointmentDateTime();
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingContacts())) {
					for (RfqEventMeetingContact contact : item.getRfxEventMeetingContacts()) {
						contact.getContactEmail();
						contact.getContactName();
						contact.getContactNumber();
					}
				}
				if (CollectionUtil.isNotEmpty(item.getInviteSuppliers())) {
					for (Supplier suppliers : item.getInviteSuppliers()) {
						suppliers.getCompanyName();
						suppliers.getCommunicationEmail();
						suppliers.getCompanyContactNumber();
					}
				}
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingDocument())) {
					for (RfqEventMeetingDocument docs : item.getRfxEventMeetingDocument()) {
						docs.getId();
						docs.getFileName();
						docs.getFileSizeInKb();
						docs.getCredContentType();
					}
				}
			}
		}

		if (rfq.getBusinessUnit() != null) {
			rfq.getBusinessUnit().getUnitName();
		}
		if (CollectionUtil.isNotEmpty(rfq.getIndustryCategories())) {
			for (IndustryCategory indCat : rfq.getIndustryCategories()) {
				indCat.getCode();
			}
		}
		return rfq;
	}

	@Override
	public List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue) {
		return naicsCodesDao.findLeafIndustryCategoryByName(searchValue);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveEventContact(RfqEventContact rfpEventContact) {
		rfqEventContactDao.saveOrUpdate(rfpEventContact);

	}

	@Override
	@Transactional(readOnly = false)
	public void saveEventCorrespondenceAddress(RfqEventCorrespondenceAddress rfpEventCorrespondenceAddress) {
		rfqEventCorrespondenceAddressDao.save(rfpEventCorrespondenceAddress);
	}

	@Override
	public IndustryCategory getIndustryCategoryForRftById(String id) {
		return industryCategoryDao.getIndustryCategoryForRftById(id);
	}

	@Override
	public List<RfqEventContact> getAllContactForEvent(String eventId) {
		return rfqEventContactDao.findAllEventContactById(eventId);
	}

	@Override
	public List<RfqEventCorrespondenceAddress> getAllCAddressForEvent(String eventId) {
		List<RfqEventCorrespondenceAddress> list = rfqEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqEventCorrespondenceAddress address : list) {
				if (address != null && address.getState() != null && address.getState().getCountry() != null) {
					address.getState().getCountry().getCountryName();
				}
			}
		}
		return list;
	}

	@Override
	public List<EventCAddressPojo> getAllCorrespondenceAddressPojo(String eventId) {
		List<EventCAddressPojo> returnList = new ArrayList<EventCAddressPojo>();

		List<RfqEventCorrespondenceAddress> list = rfqEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqEventCorrespondenceAddress rfpEventCorrespondenceAddress : list) {
				if (rfpEventCorrespondenceAddress.getState() != null)
					rfpEventCorrespondenceAddress.getState().getCountry();
				if (rfpEventCorrespondenceAddress.getState() != null)
					rfpEventCorrespondenceAddress.getState().getStateName();

				EventCAddressPojo rep = new EventCAddressPojo(rfpEventCorrespondenceAddress);
				rep.setCountry(rfpEventCorrespondenceAddress.getState().getCountry().getCountryName());
				returnList.add(rep);
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addEditorUser(String eventId, String userId) {
		RfqEvent rfpEvent = getRfqEventByeventId(eventId);
		List<User> editors = rfpEvent.getEventEditors();
		if (editors == null) {
			editors = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		editors.add(user);
		updateEvent(rfpEvent);
		return editors;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEditorUser(String eventId, String userId) {
		RfqEvent rfpEvent = getRfqEventByeventId(eventId);
		User user = userService.getUsersById(userId);
		if (rfpEvent.getEventEditors() != null) {
			rfpEvent.getEventEditors().remove(user);
		}
		updateEvent(rfpEvent);
		return rfpEvent.getEventEditors();
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addViwersToList(String eventId, String userId) {
		RfqEvent rfpEvent = getRfqEventByeventId(eventId);
		List<User> viewers = rfpEvent.getEventViewers();
		if (viewers == null) {
			viewers = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		viewers.add(user);
		updateEvent(rfpEvent);
		return viewers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeViewersfromList(String eventId, String userId) {
		RfqEvent rfqEvent = getRfqEventByeventId(eventId);
		User user = userService.getUsersById(userId);
		if (rfqEvent.getEventViewers() != null) {
			rfqEvent.getEventViewers().remove(user);
		}
		updateEvent(rfqEvent);
		return rfqEvent.getEventViewers();
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfqTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType) {
		RfqEvent rfqEvent = rfqEventDao.findById(eventId);
		List<RfqTeamMember> teamMembers = rfqEvent.getTeamMembers();
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfqTeamMember>();
		}
		User user = new User(); // userService.findTeamUserById(userId);
		user.setId(userId);
		// LOG.info("TeamMember : " + user.getLoginId());
		RfqTeamMember teamMember = new RfqTeamMember();
		teamMember.setEvent(rfqEvent);
		teamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (RfqTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				teamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;
			}
		}
		teamMember.setTeamMemberType(memberType);
		User users = userService.findTeamUserById(userId);
		if (!exists) {
			teamMembers.add(teamMember);
		}
		rfqEvent.setTeamMembers(teamMembers);
		rfqEventDao.update(rfqEvent);
		try {
			if (!exists) {
				RfqEventAudit audit = new RfqEventAudit(null, rfqEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.added", new Object[] { users.getName(), memberType.getValue() }, Global.LOCALE), null);
				eventAuditService.save(audit);
				try {
					RfqEvent event = rfqEventService.getPlainEventById(eventId);
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + users.getName() + "' has been added for '" + event.getEventId() + "' as '" + memberType.getValue() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				LOG.info("************* RFQ Team Member event audit added successfully *************");
			} else {
				if (!previousMemberType.equalsIgnoreCase(memberType.getValue())) {
					RfqEventAudit audit = new RfqEventAudit(null, rfqEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.changed", new Object[] { users.getName(), previousMemberType, memberType.getValue() }, Global.LOCALE), null);
					eventAuditService.save(audit);
					try {
						RfqEvent event = rfqEventService.getPlainEventById(eventId);
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + users.getName() + "' has been changed from '" + previousMemberType + " to '" + memberType.getValue() + "' for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					LOG.info("************* RFQ Team Member event audit changed successfully *************");
				}
			}
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}

		/*
		 * List<User> userList = new ArrayList<>(); LOG.info("rfiEvent.getEventApprovers(): " +
		 * rfqEvent.getTeamMembers()); for (RfqTeamMember rfpApp : rfqEvent.getTeamMembers()) { try {
		 * userList.add((User) rfpApp.getUser().clone()); } catch (CloneNotSupportedException e) { LOG.error(
		 * "Error constructing list of users after adding TeamMembers operation : " + e.getMessage(), e); } }
		 * LOG.info(userList.size());
		 */
		return teamMembers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeTeamMemberfromList(String eventId, String userId, RfqTeamMember dbTeamMember) {
		RfqEvent event = rfqEventDao.findById(eventId); // getRfqEventByeventId(eventId);
		List<RfqTeamMember> teamMembers = event.getTeamMembers();
		if (teamMembers == null) {
			teamMembers = new ArrayList<>();
		}

		teamMembers.remove(dbTeamMember);
		dbTeamMember.setEvent(null);
		event.setTeamMembers(teamMembers);
		rfqEventDao.update(event);

		try {
			RfqEventAudit audit = new RfqEventAudit(null, event, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.removed", new Object[] { dbTeamMember.getUser().getName(), dbTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), null);
			eventAuditService.save(audit);
			try {
				RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + dbTeamMember.getUser().getName() + "' has been removed from '" + dbTeamMember.getTeamMemberType().getValue() + "' for Event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUser().getTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			LOG.info("************* RFQ Team Member removed successfully *************");
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}
		List<User> userList = new ArrayList<User>();
		for (RfqTeamMember rftApp : event.getTeamMembers()) {
			try {
				userList.add((User) rftApp.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error constructing list of users after removing TeamMember operation : " + e.getMessage(), e);
			}
		}
		return userList;

	}

	/*
	 * @Override
	 * @Transactional(readOnly = false) public List<User> reorderApprovers(String eventId, String[] approverList) {
	 * RfqEvent event = getRfqEventByeventId(eventId); List<RfqTeamMember> approvers = event.getEventApprovers(); int
	 * index = 1; for (String id : approverList) { for (RfqTeamMember app : approvers) { if
	 * (app.getUser().getId().equals(id)) { app.setApproverPosition(index); break; } } index++; }
	 * event.setEventApprovers(approvers); event = rfqEventDao.update(event); LOG.info("Approver reordered." + "	" +
	 * approvers.size()); List<User> userList = new ArrayList<User>(); // Collections.sort(event.getEventApprovers());
	 * for (RfqTeamMember rfpApp : event.getEventApprovers()) { try { userList.add((User) rfpApp.getUser().clone()); }
	 * catch (Exception e) { LOG.error("Error constructing list of users after reorder operation : " + e.getMessage(),
	 * e); } } LOG.info(userList.size() + " Event ID :" + eventId); return userList; }
	 */

	@Override
	public RfqEvent loadRfqEventById(String id) {
		RfqEvent event = rfqEventDao.findByEventId(id);
		if (event != null) {
			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
			}
			if (event.getParticipationFeeCurrency() != null) {
				event.getParticipationFeeCurrency().getCurrencyName();
			}
			if (event.getDepositCurrency() != null) {
				event.getDepositCurrency().getCurrencyName();
			}
			if (event.getIndustryCategory() != null) {
				event.getIndustryCategory().getCode();
			}
			if (event.getCostCenter() != null) {
				event.getCostCenter().getCostCenter();
			}
			if (event.getBusinessUnit() != null) {
				event.getBusinessUnit().getUnitName();
			}
			if (event.getUnMaskedUser() != null) {
				event.getUnMaskedUser().getName();
			}

			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyCode();
			}

			if (event.getProcurementMethod() != null) {
				event.getProcurementMethod().getProcurementMethod();
			}
			if (event.getProcurementCategories() != null) {
				event.getProcurementCategories().getProcurementCategories();
			}

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfqEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}

			if (event.getEventOwner().getBuyer() != null) {
				event.getEventOwner().getBuyer().getFullName();
				if (event.getEventOwner().getBuyer().getState() != null) {
					event.getEventOwner().getBuyer().getState().getStateName();
					if (event.getEventOwner().getBuyer().getState().getCountry() != null) {
						event.getEventOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfqEventMeeting meeting : event.getMeetings()) {
					meeting.getAppointmentDateTime();
					meeting.getAppointmentTime();
					meeting.getInviteSuppliers();
					meeting.getTitle();
					meeting.getRfxEventMeetingContacts();
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RfqEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
							meetingContact.getContactName();
							meetingContact.getContactNumber();
							meetingContact.getContactEmail();
							meetingContact.getId();

						}
					}
					meeting.getStatus();
					meeting.getVenue();
					// meeting.getRfxEventMeetingDocument();
					// if
					// (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument()))
					// {
					// for (RfqEventMeetingDocument meetingDocument :
					// meeting.getRfxEventMeetingDocument()) {
					// meetingDocument.getCredContentType();
					// meetingDocument.getFileData();
					// meetingDocument.getFileName();
					// }
					// }
					meeting.getRemarks();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEventEditors())) {
				for (User user : event.getEventEditors()) {
					user.getLoginId();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getEventViewers())) {
				for (User user : event.getEventViewers()) {
					user.getLoginId();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RfqTeamMember approver : event.getTeamMembers()) {
					approver.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfqEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfqApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfqEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfqSuspensionApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				for (IndustryCategory indCat : event.getIndustryCategories()) {
					indCat.getCode();
				}
			}

			if (event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getDescription();
			}

			if (event.getTemplate() != null) {
				event.getTemplate().getVisibleSuspendApproval();
				event.getTemplate().getReadOnlySuspendApproval();
				event.getTemplate().getOptionalSuspendApproval();
			}
		}
		return event;
	}

	@Override
	public boolean isExists(RfqEvent rfpEvent) {
		return rfqEventDao.isExists(rfpEvent);
	}

	@Override
	public RfqEvent loadEventForSummeryById(String id) {
		RfqEvent event = rfqEventDao.findBySupplierEventId(id);
		if (event != null) {

			if (event.getTemplate() != null) {
				event.getTemplate().getTemplateName();
			}

			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
			}
			if (event.getCostCenter() != null) {
				event.getCostCenter().getCostCenter();
			}
			if (event.getBusinessUnit() != null) {
				event.getBusinessUnit().getUnitName();
			}
			if (event.getIndustryCategory() != null) {
				event.getIndustryCategory().getName();
			}

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfqEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RfqTeamMember teamMembers : event.getTeamMembers()) {
					teamMembers.getTeamMemberType();
					teamMembers.getUser().getName();
				}
			}
			// BAD LOGIC HERE
			// if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
			// for (RfqEventSupplier supplier : event.getSuppliers()) {
			// LOG.info("Company Name : " + supplier.getSupplier().getCompanyName());
			// supplier.getSupplier().getCompanyName();
			// }
			// }
			if (event.getEventOwner().getBuyer() != null) {
				event.getEventOwner().getBuyer().getFullName();
				event.getEventOwner().getBuyer().getLine1();
				event.getEventOwner().getBuyer().getLine2();
				event.getEventOwner().getBuyer().getCity();
				if (event.getEventOwner().getBuyer().getState() != null) {
					event.getEventOwner().getBuyer().getState().getStateName();
					if (event.getEventOwner().getBuyer().getState().getCountry() != null) {
						event.getEventOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				for (IndustryCategory industryCategory : event.getIndustryCategories()) {
					industryCategory.getName();
					industryCategory.getCreatedBy();
					if (industryCategory.getCreatedBy() != null) {
						industryCategory.getCreatedBy().getName();
					}
					industryCategory.getCode();
					industryCategory.getBuyer();
				}
			}

			// Touch Event reminders...
			if (CollectionUtils.isNotEmpty(event.getRftReminder())) {
				for (RfqReminder reminder : event.getRftReminder()) {
					reminder.getInterval();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfqEventMeeting meeting : event.getMeetings()) {
					meeting.getAppointmentDateTime();
					meeting.getAppointmentTime();
					meeting.getInviteSuppliers();
					meeting.getTitle();
					meeting.getRfxEventMeetingContacts();
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RfqEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
							meetingContact.getContactName();
							meetingContact.getContactNumber();
							meetingContact.getContactEmail();
							meetingContact.getId();

						}
					}
					meeting.getStatus();
					meeting.getVenue();
					// meeting.getRfxEventMeetingDocument();
					// if
					// (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument()))
					// {
					// for (RfqEventMeetingDocument meetingDocument :
					// meeting.getRfxEventMeetingDocument()) {
					// meetingDocument.getCredContentType();
					// meetingDocument.getFileData();
					// meetingDocument.getFileName();
					// }
					// }
					meeting.getRemarks();
				}
			}

			// if (CollectionUtil.isNotEmpty(event.getDocuments())) {
			// for (RfqEventDocument document : event.getDocuments()) {
			// document.getDescription();
			// }
			// }
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfqEventMeeting meeting : event.getMeetings()) {
					meeting.getRemarks();
					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier supplier : meeting.getInviteSuppliers()) {
							supplier.getCompanyName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RfqEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
							contact.getContactName();
						}
					}
					// if
					// (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument()))
					// {
					// for (RfqEventMeetingDocument document :
					// meeting.getRfxEventMeetingDocument()) {
					// document.getFileName();
					// }
					// }
				}
			}
			if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
				for (RfqEventBq bq : event.getEventBqs()) {
					if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
						for (RfqBqItem item : bq.getBqItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				for (RfqComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfqEventApproval approvalLevel : event.getApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfqApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RfqEventSupplier eventSupplier : event.getSuppliers()) {
					if (eventSupplier.getSupplier() != null) {
						eventSupplier.getSupplier().getCompanyName();
					}
					if (CollectionUtil.isNotEmpty(eventSupplier.getTeamMembers())) {
						for (RfqSupplierTeamMember supplierTeamMember : eventSupplier.getTeamMembers())
							supplierTeamMember.getUser().getName();
					}
				}
			}
		}
		if (event.getConcludeBy() != null) {
			event.getConcludeBy().getName();
		}
		return event;
	}

	@Override
	public Integer getCountOfEnvelopByEventId(String eventId) {
		return rfqEventDao.getCountOfEnvelopByEventId(eventId);
	}

	@Override
	public Integer getCountOfDocumentByEventId(String eventId) {
		return rfqDocumentDao.getCountOfDocumentByEventId(eventId);
	}

	@Override
	public List<DraftEventPojo> getAllEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchVal, String indCat) throws SubscriptionException {
		Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		if (buyer != null && buyer.getBuyerPackage() != null) {
			BuyerPackage bp = buyer.getBuyerPackage();
			if (bp.getEventLimit() != null) {
				if (bp.getNoOfEvents() == null) {
					bp.setNoOfEvents(0);
				}
				if (bp.getNoOfEvents() >= bp.getEventLimit()) {
					throw new SubscriptionException("You have reached your subscription limitEvent. limit of " + bp.getEventLimit() + " reached.");
				} else if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
					throw new SubscriptionException("Your Subscription has Expired.");
				}
			}
		}
		List<DraftEventPojo> list = rfqEventDao.getAllRfqEventByTenantId(tenantId, loggedInUser, pageNo, searchVal, indCat);
		return list;
	}

	@Override
	public boolean isExists(RfqSupplierMeetingAttendance rfpSupplierMeetingAttendance) {
		return rfqSupplierMeetingAttendanceDao.isExists(rfpSupplierMeetingAttendance);
	}

	@Override
	public RfqSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId) {
		return rfqSupplierMeetingAttendanceDao.attendenceByEventId(meetingId, eventId, tenantId);
	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rfqEventDao.getEventSuppliers(eventId);

	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent copyFrom(String eventId, User createdBy, BusinessUnit buyerbusinessUnit) throws SubscriptionException, ApplicationException {
		RfqEvent rfqEvent = this.getEventById(eventId);

		if (rfqEvent.getGroupCode() != null && Status.INACTIVE == rfqEvent.getGroupCode().getStatus()) {
			LOG.error("The group code " + rfqEvent.getGroupCode().getGroupCode() + " used in Previous Event is inactive");
			throw new ApplicationException("The group code '" + rfqEvent.getGroupCode().getGroupCode() + "' used in Event is inactive");
		}

		if (rfqEvent.getDeliveryAddress() != null && rfqEvent.getDeliveryAddress().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Delivery address found ....");
			throw new ApplicationException("Delivery address is Inactive for event:" + rfqEvent.getEventId());
		} else {
			LOG.info("active Delivery address found ........");
		}
		if (rfqEvent.getTemplate() != null && Status.INACTIVE == rfqEvent.getTemplate().getStatus()) {
			LOG.info("inactive Template found for Id .... " + rfqEvent.getTemplate().getId());
			throw new ApplicationException("Template [" + rfqEvent.getTemplate().getTemplateName() + "] used by the event [" + rfqEvent.getEventId() + "] is Inactive");
		}

		RfqEvent newEvent = rfqEvent.copyFrom(rfqEvent);
		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());
		newEvent.setEnableAwardApproval(rfqEvent.getEnableAwardApproval());
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFQ")) {
			if (buyerbusinessUnit != null) {
				LOG.info("business unit selected by user choice selected");
				newEvent.setBusinessUnit(buyerbusinessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (newEvent.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select its own business unit");
					throw new ApplicationException("BUSINESS_UNIT_EMPTY");
				}
			}
		}

		ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(createdBy.getTenantId());
		if (erpSetup != null) {
			LOG.info("--------erp flag set for event-----------");
			newEvent.setErpEnable(erpSetup.getIsErpEnable());

		} else {
			newEvent.setErpEnable(false);
		}

		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFQ", newEvent.getBusinessUnit()));
		newEvent.setStatus(EventStatus.DRAFT);

		// Save teamMember
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RfqTeamMember> tm = new ArrayList<RfqTeamMember>();
			for (RfqTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
			}
		}

		// save Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getApprovals())) {
			for (RfqEventApproval app : newEvent.getApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfqApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);

					}
				}
			}
		}

		// save suspension Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getSuspensionApprovals())) {
			for (RfqEventSuspensionApproval app : newEvent.getSuspensionApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfqSuspensionApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);
					}
				}
			}
		}

		// save Doc
		if(CollectionUtil.isNotEmpty(newEvent.getDocuments())){
			for(RfqEventDocument rfqDocument: newEvent.getDocuments()){
				rfqDocument.copyFrom(newEvent);
				LOG.info("Saving document...");
				rfqDocumentService.saveDocuments(rfqDocument);
			}
		}

		// save Award Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getAwardApprovals())) {
			for (RfqEventAwardApproval app : newEvent.getAwardApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfqAwardApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(newEvent.getUnMaskedUsers())) {
			List<RfqUnMaskedUser> tm = new ArrayList<RfqUnMaskedUser>();
			for (RfqUnMaskedUser unmaskUser : newEvent.getUnMaskedUsers()) {
				unmaskUser.setEvent(newEvent);
				tm.add(unmaskUser);
			}
		}

		List<RfqEnvelop> envelops = newEvent.getRfxEnvelop();
		newEvent.setRfxEnvelop(null);
		RfqEvent newDbEvent = this.saveEvent(newEvent, createdBy);
		// Save teamMember
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RfqTeamMember> tm = new ArrayList<RfqTeamMember>();
			for (RfqTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
				rfaEventService.sendTeamMemberEmailNotificationEmail(teamMember.getUser(), teamMember.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFQ, newDbEvent.getId());
			}
		}
		// Save contacts
		if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
			for (RfqEventContact contact : newEvent.getEventContacts()) {
				contact.setRfxEvent(newDbEvent);
				rfqEventContactDao.saveOrUpdate(contact);
			}
		}

		// save suppliers
		if (CollectionUtil.isNotEmpty(newEvent.getSuppliers())) {
			for (RfqEventSupplier supp : newEvent.getSuppliers()) {
				supp.setRfxEvent(newDbEvent);
				rfqEventSupplierDao.save(supp);
			}
		}

		// save BQ
		List<RfqEventBq> eventBq = new ArrayList<RfqEventBq>();
		if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {
			for (RfqEventBq bq : newEvent.getEventBqs()) {
				bq.setRfxEvent(newDbEvent);
				RfqEventBq dbBq = rfqEventBqDao.saveOrUpdate(bq);
				eventBq.add(dbBq);
				// save BQ Items
				if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
					RfqBqItem parent = null;
					for (RfqBqItem item : bq.getBqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setBq(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rfqBqItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}

			}

		}

		// save SOR
		List<RfqEventSor> eventSor = new ArrayList<RfqEventSor>();
		if (CollectionUtil.isNotEmpty(newEvent.getEventSors())) {
			for (RfqEventSor bq : newEvent.getEventSors()) {
				bq.setRfxEvent(newDbEvent);
				RfqEventSor dbBq = rfqEventSorDao.saveOrUpdate(bq);
				eventSor.add(dbBq);
				if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
					RfqSorItem parent = null;
					for (RfqSorItem item : bq.getSorItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setSor(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rfqSorItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
			}
		}

		List<RfqCq> eventCq = new ArrayList<RfqCq>();
		// save CQ
		if (CollectionUtil.isNotEmpty(newEvent.getCqs())) {
			for (RfqCq cq : newEvent.getCqs()) {
				cq.setRfxEvent(newDbEvent);
				RfqCqItem parent = null;
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfqCqItem item : cq.getCqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setRfxEvent(newDbEvent);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
				RfqCq dbCq = rfqCqDao.saveOrUpdate(cq);
				eventCq.add(dbCq);
			}
		}

		// save envelop
		if (CollectionUtil.isNotEmpty(envelops)) {
			for (RfqEnvelop envelop : envelops) {
				envelop.setRfxEvent(newDbEvent);
				List<RfqEventBq> bqsOfEnvelop = new ArrayList<RfqEventBq>();

				List<RfqEventBq> envelopBqs = new ArrayList<RfqEventBq>();
				if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
					for (RfqEventBq bq : envelop.getBqList()) {
						envelopBqs.add(bq);
					}
				}

				for (RfqEventBq evntbq : eventBq) {
					for (RfqEventBq envbq : envelopBqs) {
						if (evntbq.getName().equals(envbq.getName())) {
							bqsOfEnvelop.add(evntbq);
							break;
						}
					}
				}

				List<RfqEventSor> sorsOfEnvelop = new ArrayList<RfqEventSor>();

				List<RfqEventSor> envelopSors = new ArrayList<RfqEventSor>();
				if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
					for (RfqEventSor sor : envelop.getSorList()) {
						envelopSors.add(sor);
					}
				}

				for (RfqEventSor evntsor : eventSor) {
					for (RfqEventSor envsor : envelopSors) {
						if (evntsor.getName().equals(envsor.getName())) {
							sorsOfEnvelop.add(evntsor);
							break;
						}
					}
				}

				List<RfqCq> cqsOfEnvelop = new ArrayList<RfqCq>();
				List<RfqCq> envelopCqs = new ArrayList<RfqCq>();
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfqCq bq : envelop.getCqList()) {
						envelopCqs.add(bq);
					}
				}

				for (RfqCq evntCq : eventCq) {
					for (RfqCq envcq : envelopCqs) {
						if (evntCq.getName().equals(envcq.getName())) {
							cqsOfEnvelop.add(evntCq);
							break;
						}
					}
				}
				List<RfqEvaluatorUser> evalUser = new ArrayList<RfqEvaluatorUser>();
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RfqEvaluatorUser eval : envelop.getEvaluators()) {
						if (eval.getUser().isActive()) {
							LOG.info("EV USER : " + eval.getUser().getId() + "  Active " + eval.getUser().isActive() + " envelop : " + envelop.getEnvelopTitle());
							eval.setEnvelope(envelop);
							evalUser.add(eval);
						}
					}
				}

				List<RfqEnvelopeOpenerUser> openerUserList = new ArrayList<RfqEnvelopeOpenerUser>();
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfqEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
						if (opener.getUser().isActive()) {
							// we should create a copy to before saving it.
							opener = opener.copyFrom();
							opener.setEnvelope(envelop);
							opener.setEvent(newDbEvent);
							openerUserList.add(opener);
						}
					}
				}

				envelop.setOpenerUsers(openerUserList);
				envelop.setEvaluators(evalUser);
				envelop.setBqList(bqsOfEnvelop);
				envelop.setCqList(cqsOfEnvelop);
				envelop.setSorList(sorsOfEnvelop);
				envelop = rfqEnvelopDao.save(envelop);

			}
		}

		return newDbEvent;
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit buyerbusinessUnit) throws SubscriptionException, ApplicationException {
		RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(templateId);

		if (rfxTemplate != null && Status.INACTIVE == rfxTemplate.getStatus()) {
			LOG.info("inactive Template [" + rfxTemplate.getTemplateName() + "] found for Id .... " + rfxTemplate.getId());
			throw new ApplicationException("Template [" + rfxTemplate.getTemplateName() + "] is Inactive");
		}

		RfqEvent newEvent = new RfqEvent();
		newEvent.setTemplate(rfxTemplate);
		newEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newEvent.setAddSupplier(rfxTemplate.getAddSupplier());
		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());
		newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());
		newEvent.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
		newEvent.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newEvent.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newEvent.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		newEvent.setEnableSuspensionApproval(rfxTemplate.getEnableSuspendApproval());
		newEvent.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());
		newEvent.setEnableAwardApproval(rfxTemplate.getEnableAwardApproval());

		// copy unmasking User

		List<RfqUnMaskedUser> unmaskingUser = new ArrayList<RfqUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RfqUnMaskedUser newRfiUnMaskedUser = new RfqUnMaskedUser();
				newRfiUnMaskedUser.setUser(team.getUser());
				newRfiUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRfiUnMaskedUser);
			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}

		// Copy Evaluation Conclusion Users from template
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RfqEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RfqEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RfqEvaluationConclusionUser conclusionUser = new RfqEvaluationConclusionUser();
				conclusionUser.setUser(user);
				conclusionUser.setEvent(newEvent);
				evaluationConclusionUsers.add(conclusionUser);
			}
			newEvent.setEvaluationConclusionUsers(evaluationConclusionUsers);
			LOG.info("Added Evaluation Conclusion user to Event  : " + (newEvent.getEvaluationConclusionUsers() != null ? newEvent.getEvaluationConclusionUsers().size() : 0));
		}

		/**
		 * commmented visibility for event publish type CR PH-352
		 */
		// newEvent.setEventVisibility(EventVisibilityType.PRIVATE);

		newEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
		// copy approval from template
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RfqEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfqEventApproval newRfApproval = new RfqEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfqApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfqApprovalUser approvalUser = new RfqApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(newRfApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfApprovalList.add(approvalUser);
					}
					newRfApproval.setApprovalUsers(rfApprovalList);
				}
				approvalList.add(newRfApproval);
			}
			newEvent.setApprovals(approvalList);
		}

		if (CollectionUtil.isNotEmpty(rfxTemplate.getSuspensionApprovals())) {
			List<RfqEventSuspensionApproval> approvalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Suspension Approval Level :" + templateApproval.getLevel());
				RfqEventSuspensionApproval newRfApproval = new RfqEventSuspensionApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfqSuspensionApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfqSuspensionApprovalUser approvalUser = new RfqSuspensionApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(newRfApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfApprovalList.add(approvalUser);
					}
					newRfApproval.setApprovalUsers(rfApprovalList);
				}
				approvalList.add(newRfApproval);
			}
			newEvent.setSuspensionApprovals(approvalList);
		}

		if (CollectionUtil.isNotEmpty(rfxTemplate.getAwardApprovals())) {
			List<RfqEventAwardApproval> approvalList = new ArrayList<>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfqEventAwardApproval newRfApproval = new RfqEventAwardApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfqAwardApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfqAwardApprovalUser approvalUser = new RfqAwardApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(newRfApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfApprovalList.add(approvalUser);
					}
					newRfApproval.setApprovalUsers(rfApprovalList);
				}
				approvalList.add(newRfApproval);
			}
			newEvent.setAwardApprovals(approvalList);
		}

		if (CollectionUtil.isNotEmpty(rfxTemplate.getFields())) {

			for (TemplateField field : rfxTemplate.getFields()) {
				switch (field.getFieldName()) {
				case BASE_CURRENCY:
					if (field.getDefaultValue() != null) {
						Currency currency = currencyDao.findById(field.getDefaultValue());
						newEvent.setBaseCurrency(currency);
						LOG.info("Base Currency : " + currency + "Default value :  " + field.getDefaultValue());
					}
					break;
				case BUDGET_AMOUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setBudgetAmount(new BigDecimal(field.getDefaultValue()));
						LOG.info("budget amount Default value :  " + field.getDefaultValue());
					}
					break;
				case COST_CENTER:
					if (field.getDefaultValue() != null) {
						CostCenter costCenter = costCenterDao.findById(field.getDefaultValue());
						newEvent.setCostCenter(costCenter);
						LOG.info("costCenter : " + costCenter + "Default value :  " + field.getDefaultValue());
					}
					break;
				case BUSINESS_UNIT:
					if (field.getDefaultValue() != null) {
						BusinessUnit businessUnit = businessUnitService.getPlainBusinessUnitById(field.getDefaultValue());
						newEvent.setBusinessUnit(businessUnit);
						// LOG.info("businessUnit : " + businessUnit + "Default value : " + field.getDefaultValue());
					}
					break;
				case DECIMAL:
					if (field.getDefaultValue() != null) {
						newEvent.setDecimal(field.getDefaultValue());
						LOG.info("Decimal Default value :  " + field.getDefaultValue());
					}
					break;
				case EVENT_CATEGORY:
					if (field.getDefaultValue() != null) {
						String[] icArr = field.getDefaultValue().split(",");
						List<IndustryCategory> icList = new ArrayList<IndustryCategory>();
						if (icArr != null) {
							for (String icId : icArr) {
								// IndustryCategory ic = industryCategoryService.getIndustryCategoryById(icId);
								IndustryCategory ic = new IndustryCategory();
								ic.setId(icId);
								icList.add(ic);
							}
							newEvent.setIndustryCategories(icList);
						}
						// newEvent.setIndustryCategory(industryCategory);
						// LOG.info("industryCategory : " + industryCategory + "Default value : " +
						// field.getDefaultValue());
					}
					break;
				case EVENT_NAME:
					if (field.getDefaultValue() != null) {
						newEvent.setEventName(field.getDefaultValue());
					}
					break;
				case HISTORIC_AMOUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setHistoricaAmount(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case PAYMENT_TERM:
					if (field.getDefaultValue() != null) {
						newEvent.setPaymentTerm(field.getDefaultValue());
					}
					break;
				case PARTICIPATION_FEES:
					if (field.getDefaultValue() != null) {
						newEvent.setParticipationFees(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case DEPOSIT:
					if (field.getDefaultValue() != null) {
						newEvent.setDeposit(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case PARTICIPATION_FEE_CURRENCY:
					if (field.getDefaultValue() != null) {
						Currency currency = currencyDao.findById(field.getDefaultValue());
						newEvent.setParticipationFeeCurrency(currency);
						LOG.info(" Currency : " + currency + "Default value :  " + field.getDefaultValue());
					}
					break;
				case DEPOSIT_CURRENCY:
					if (field.getDefaultValue() != null) {
						Currency currency = currencyDao.findById(field.getDefaultValue());
						newEvent.setDepositCurrency(currency);
						LOG.info(" Currency : " + currency + "Default value :  " + field.getDefaultValue());
					}
					break;
				case MINIMUM_SUPPLIER_RATING:
					if (field.getDefaultValue() != null) {
						newEvent.setMinimumSupplierRating(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case MAXIMUM_SUPPLIER_RATING:
					if (field.getDefaultValue() != null) {
						newEvent.setMaximumSupplierRating(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case EVALUATION_PROCESS_DECLARATION:
					if (field.getDefaultValue() != null) {
						Declaration declaration = declarationDao.findById(field.getDefaultValue());
						newEvent.setEvaluationProcessDeclaration(declaration);
						LOG.info("Declaration : " + declaration + "Default value :  " + field.getDefaultValue());
					}
					break;
				case SUPPLIER_ACCEPTANCE_DECLARATION:
					if (field.getDefaultValue() != null) {
						Declaration declaration = declarationDao.findById(field.getDefaultValue());
						newEvent.setSupplierAcceptanceDeclaration(declaration);
						LOG.info("Declaration : " + declaration + "Default value :  " + field.getDefaultValue());
					}
					break;
				case SUB_VALIDITY_DAYS:
					if (field.getDefaultValue() != null) {
						newEvent.setSubmissionValidityDays(Integer.parseInt(field.getDefaultValue()));
					}
					break;
				case DISABLE_TOTAL_AMOUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setDisableTotalAmount(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case _UNKNOWN:
					if (field.getDefaultValue() != null) {
					}
					break;
				case ESTIMATED_BUDGET:
					if (field.getDefaultValue() != null) {
						newEvent.setEstimatedBudget(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case PROCUREMENT_METHOD:
					if (field.getDefaultValue() != null) {
						ProcurementMethod procurementMethod = procurementMethodDao.findById(field.getDefaultValue());
						newEvent.setProcurementMethod(procurementMethod);
						LOG.info("Procurement Method : " + procurementMethod + "Default value :  " + field.getDefaultValue());
					}
					break;
				case PROCUREMENT_CATEGORY:
					if (field.getDefaultValue() != null) {
						ProcurementCategories procurementCategory = procurementCategoriesDao.findById(field.getDefaultValue());
						newEvent.setProcurementCategories(procurementCategory);
						LOG.info("ProcurementCategory : " + procurementCategory + "Default value :  " + field.getDefaultValue());
					}
					break;
				case GROUP_CODE:
					if (field.getDefaultValue() != null) {
						GroupCode groupCode = groupCodeDao.findById(field.getDefaultValue());
						if (Status.INACTIVE == groupCode.getStatus()) {
							throw new ApplicationException("The group code '" + groupCode.getGroupCode() + "' defaulted in template is inactive");
						}
						newEvent.setGroupCode(groupCode);
						LOG.info("group Code : " + groupCode + "Default value :  " + field.getDefaultValue());
					}
					break;
				default:
					break;

				}
			}

		}
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFQ")) {
			if (buyerbusinessUnit != null) {
				LOG.info("business unit selected by user choice selected");
				newEvent.setBusinessUnit(buyerbusinessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (newEvent.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select its own business unit");
					throw new ApplicationException("BUSINESS_UNIT_EMPTY");
				}

			}
		}

		ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(createdBy.getTenantId());
		if (erpSetup != null) {
			LOG.info("--------erp flag set for event-----------");
			newEvent.setErpEnable(erpSetup.getIsErpEnable());

		} else {
			newEvent.setErpEnable(false);
		}
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFQ", newEvent.getBusinessUnit()));
		newEvent = this.saveEvent(newEvent, createdBy);
		// copy envlope from rxf template

		List<RfxEnvelopPojo> envlopeList = new ArrayList<RfxEnvelopPojo>();

		if (rfxTemplate.getRfxEnvelope1() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope1());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence1());
			envlopeList.add(pojo);
		}

		if (rfxTemplate.getRfxEnvelope2() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope2());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence2());
			envlopeList.add(pojo);
		}
		if (rfxTemplate.getRfxEnvelope3() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope3());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence3());
			envlopeList.add(pojo);
		}

		if (rfxTemplate.getRfxEnvelope4() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope4());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence4());
			envlopeList.add(pojo);
		}

		if (rfxTemplate.getRfxEnvelope5() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope5());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence5());
			envlopeList.add(pojo);
		}

		if (rfxTemplate.getRfxEnvelope6() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope6());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence6());
			envlopeList.add(pojo);
		}

		if (CollectionUtil.isNotEmpty(envlopeList)) {
			for (RfxEnvelopPojo pojo : envlopeList) {

				RfqEnvelop envelop = new RfqEnvelop();
				envelop.setEnvelopTitle(pojo.getRfxEnvelope());
				envelop.setEnvelopSequence(pojo.getRfxSequence());
				envelop.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				envelop.setRfxEvent(newEvent);
				envelop.setEnvelopType(EnvelopType.CLOSED);
				envelopService.saveEnvelop(envelop, null, null, null);
			}
		}

		List<RfqTeamMember> teamMembers = new ArrayList<RfqTeamMember>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RfqTeamMember newTeamMembers = new RfqTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setEvent(newEvent);
				teamMembers.add(newTeamMembers);
				rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", rfxTemplate.getType(), newEvent.getId());
			}
			newEvent.setTeamMembers(teamMembers);
		}
		setDefaultEventContactDetail(createdBy.getId(), newEvent.getId());
		LOG.info("All new event :  " + newEvent.getIndustryCategory() + "   :evet Id :   " + newEvent.getId());
		return newEvent;
	}

	@Override
	public RfqEventContact getEventContactById(String id) {
		return rfqEventContactDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteContact(RfqEventContact eventContact) {
		rfqEventContactDao.delete(eventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventContact(RfqEventContact rfpEventContact) {
		rfqEventContactDao.update(rfpEventContact);

	}

	@Override
	@Transactional(readOnly = false)
	public void saveEventReminder(RfqReminder rfpReminder) {
		rfqReminderDao.saveOrUpdate(rfpReminder);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventReminder(RfqReminder rfpReminder) {
		rfqReminderDao.update(rfpReminder);
	}

	@Override
	public List<RfqReminder> getAllRfqEventReminderForEvent(String eventId) {
		return rfqReminderDao.getAllEventReminderForEvent(eventId);

	}

	@Override
	public List<RfqReminder> getAllEventReminderForEvent(String eventId) {
		return rfqReminderDao.getAllEventReminderForEvent(eventId);

	}

	@Override
	public RfqReminder getRfqEventReminderById(String id) {
		return rfqReminderDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfqReminder(RfqReminder rfqReminder) {
		rfqReminderDao.delete(rfqReminder);
	}

	@Override
	@Transactional(readOnly = true)
	public RfqEvent loadBqsForSupplierByEventId(String id) {
		return rfqEventDao.findBySupplierEventId(id);
	}

	@Override
	public Integer getCountOfRfqCqByEventId(String eventId) {
		return rfqCqDao.getCountOfCqByEventId(eventId);
	}

	@Override
	public boolean isExists(RfqEventContact rfqEventContact) {
		return rfqEventContactDao.isExists(rfqEventContact);
	}

	@Override
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		return rfqEventDao.getTeamMembersForEvent(eventId);
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {
		return rfqEventDao.getUserPemissionsForEvent(userId, eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId) {
		return rfqEventDao.getUserPemissionsForEnvelope(userId, eventId, envelopeId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent updateEventApproval(RfqEvent event, User loggedInUser) {

		RfqEvent persistObj = rfqEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfqEventApproval approvalRequest : persistObj.getApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfqApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			int level = 1;
			for (RfqEventApproval app : event.getApprovals()) {
				app.setEvent(event);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfqApprovalUser approvalUser : app.getApprovalUsers()) {
						approvalUser.setApproval(app);
						approvalUser.setId(null);

						// If db existing user list is empty, then it means this is a new level which is not existing in
						// database yet.
						// therefore we need to add all user list coming from frontend for current level as new users
						if (CollectionUtil.isEmpty(existingUserList)) {
							auditMessages.add("Approval Level " + app.getLevel() + " User " + approvalUser.getUser().getName() + " has been added as Approver");
						} else {
							// If db existing user list does not contain the user coming from frontend, then it has been
							// added as new user for current level
							if (!existingUserList.contains(approvalUser.getUser().getName())) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + approvalUser.getUser().getName() + " has been added as Approver");
							}
						}

						levelUsers.add(approvalUser.getUser().getName());
					}
					/*
					 * Loop through the db existing user list for the current level and check if they exist in the
					 * userlist coming from frontend.
					 */
					if (CollectionUtil.isNotEmpty(existingUserList)) {
						for (String existing : existingUserList) {
							if (!levelUsers.contains(existing)) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + existing + " has been removed as Approver");
							}
						}
					}
				}
				// to check if approval type is changed
				if (existingType != null && existingType != app.getApprovalType()) {
					auditMessages.add("Approval Level " + app.getLevel() + " Type changed from " + (existingType == ApprovalType.OR ? "Any" : "All") + " to " + (app.getApprovalType() == ApprovalType.OR ? "Any" : "All"));
				}
			}
		} else {
			LOG.warn("Approval levels is empty.");
		}

		while (CollectionUtil.isNotEmpty(existingUsers.get(newLevel))) {
			for (String existing : existingUsers.get(newLevel)) {
				auditMessages.add("Approval Level " + newLevel + " User " + existing + " has been removed as Approver");
			}
			newLevel++;
		}

		persistObj.setApprovals(event.getApprovals());
		persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
		persistObj.setModifiedDate(new Date());
		persistObj = rfqEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfqEventAudit audit = new RfqEventAudit(persistObj, loggedInUser, new Date(), AuditActionType.Update, auditMessage);
				rfqEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" + persistObj.getEventId() + "' ." + auditMessage, loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			} catch (Exception e) {
				LOG.error("Error saving Sourcing Request Audit for change of approvers : " + e.getMessage(), e);
			}
		}
		return persistObj;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void insertTimeLine(String id) throws ApplicationException {
		try {

			rfqEventTimeLineDao.deleteTimeline(id);

			RfqEvent rfqEvent = rfqEventDao.findById(id);
			// Publish Date
			RfqEventTimeLine timeline = new RfqEventTimeLine();
			timeline.setActivityDate(rfqEvent.getEventPublishDate());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfqEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.publish.date", new Object[] { rfqEvent.getEventName() }, Global.LOCALE));
			rfqEventTimeLineDao.save(timeline);

			// Event Start
			timeline = new RfqEventTimeLine();
			timeline.setActivityDate(rfqEvent.getEventStart());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfqEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.start.date", new Object[] { rfqEvent.getEventName() }, Global.LOCALE));
			rfqEventTimeLineDao.save(timeline);

			// End Date
			timeline = new RfqEventTimeLine();
			timeline.setActivityDate(rfqEvent.getEventEnd());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfqEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.end.date", new Object[] { rfqEvent.getEventName() }, Global.LOCALE));
			rfqEventTimeLineDao.save(timeline);

			// Event Reminders
			if (CollectionUtil.isNotEmpty(rfqEvent.getRftReminder())) {
				for (RfqReminder reminder : rfqEvent.getRftReminder()) {
					// Meeting Reminders
					timeline = new RfqEventTimeLine();
					timeline.setActivityDate(reminder.getReminderDate());
					timeline.setActivity(EventTimelineType.REMINDER);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfqEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.reminder.date", new Object[] { rfqEvent.getEventName() }, Global.LOCALE));
					rfqEventTimeLineDao.save(timeline);
				}
			}

			// Meetings
			if (CollectionUtil.isNotEmpty(rfqEvent.getMeetings())) {
				for (RfqEventMeeting meeting : rfqEvent.getMeetings()) {
					// Meeting
					timeline = new RfqEventTimeLine();
					timeline.setActivityDate(meeting.getAppointmentDateTime());
					timeline.setActivity(EventTimelineType.MEETING);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfqEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.meeting.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
					rfqEventTimeLineDao.save(timeline);

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
						// Meeting Reminders
						for (RfqEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
							timeline = new RfqEventTimeLine();
							timeline.setActivityDate(reminder.getReminderDate());
							timeline.setActivity(EventTimelineType.REMINDER);
							timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
							timeline.setEvent(rfqEvent);
							timeline.setDescription(messageSource.getMessage("timeline.event.meeting.reminder.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
							rfqEventTimeLineDao.save(timeline);
						}
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ApplicationException("Error generating event timeline : " + e.getMessage(), e);
		}

	}

	@Override
	public List<RfqEventTimeLine> getRfqEventTimeline(String id) {
		return rfqEventTimeLineDao.getRfqEventTimeline(id);
	}

	@Override
	public JasperPrint generatePdfforEvaluationSummary(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rfqSummary = new ArrayList<EvaluationPojo>();
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		generatedSdf.setTimeZone(timeZone);
		parameters.put("generatedOn", generatedSdf.format(new Date()));
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RfqEvent event = getRfqEventByeventId(eventId);
			boolean isMasked = event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking();

            if (event != null) {

				DecimalFormat df = null;
				if (event.getDecimal() != null) {
					if (event.getDecimal().equals("1")) {
						df = new DecimalFormat("#,###,###,##0.0");
					} else if (event.getDecimal().equals("2")) {
						df = new DecimalFormat("#,###,###,##0.00");
					} else if (event.getDecimal().equals("3")) {
						df = new DecimalFormat("#,###,###,##0.000");
					} else if (event.getDecimal().equals("4")) {
						df = new DecimalFormat("#,###,###,##0.0000");
					} else if (event.getDecimal().equals("5")) {
						df = new DecimalFormat("#,###,###,##0.00000");
					} else if (event.getDecimal().equals("6")) {
						df = new DecimalFormat("#,###,###,##0.000000");
					}
				}
				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				String type = "RFQ";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName(event.getEventOwner().getBuyer().getCompanyName());
				summary.setType(type);

				// Below code for display all Suppliers List
				List<EventSupplier> submittedSupplierList = null;
				List<EventSupplier> SupplierList = rfqEventSupplierService.getAllSuppliersByEventId(eventId);
				List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
				if (event.getViewSupplerName()) {
					summary.setSupplierMaskingList(new ArrayList<SupplierMaskingPojo>());
				} else {
					summary.setSupplierMaskingList(buildSupplierMaskingData(SupplierList, eventId));
				}
				List<EvaluationBqPojo> bqGranTotalList = new ArrayList<EvaluationBqPojo>();
				if (CollectionUtils.isNotEmpty(SupplierList)) {
					submittedSupplierList = new ArrayList<EventSupplier>();
					String SubmissionStatus = "";
					List<EvaluationSuppliersPojo> suppliersForLeadEvaluators = new ArrayList<EvaluationSuppliersPojo>();
					for (EventSupplier supplier : SupplierList) {
						EvaluationSuppliersPojo suppliers = new EvaluationSuppliersPojo();
						suppliers.setContactName(supplier.getSupplier().getFullName());
						if (isMasked) {
							suppliers.setSupplierName(MaskUtils.maskName("SUPPLIER", supplier.getSupplier().getId(), event.getId()));
							summary.setIsMask(true);
						} else {
							summary.setIsMask(false);
							suppliers.setSupplierName(supplier.getSupplierCompanyName());
						}
						suppliers.setDesignation(supplier.getSupplier().getDesignation());
						suppliers.setEmail(supplier.getSupplier().getCommunicationEmail());

						if (SubmissionStatusType.ACCEPTED.equals(supplier.getSubmissionStatus())) {
							SubmissionStatus = "ACCEPTED";
						} else if (SubmissionStatusType.COMPLETED.equals(supplier.getSubmissionStatus())) {
							SubmissionStatus = "COMPLETED";
						} else if (SubmissionStatusType.INVITED.equals(supplier.getSubmissionStatus())) {
							SubmissionStatus = "INVITED";
						} else if (SubmissionStatusType.REJECTED.equals(supplier.getSubmissionStatus())) {
							SubmissionStatus = "REJECTED";
						}
						suppliers.setStatus(SubmissionStatus);
						suppliers.setContactNo(supplier.getSupplier().getMobileNumber());
						suppliers.setIsQualify(supplier.getDisqualify());

						if (supplier.getDisqualify() == Boolean.TRUE) {
							suppliers.setReason(supplier.getDisqualifyRemarks());
						}
						if (Boolean.TRUE == supplier.getSubmitted()) {
							submittedSupplierList.add(supplier);
						}
						if (SubmissionStatusType.COMPLETED == supplier.getSubmissionStatus()) {
							suppliersForLeadEvaluators.add(suppliers);
						}
						allSuppliers.add(suppliers);
					}
					if (suppliersForLeadEvaluators.size() > 0) {
						summary.setSuppliersForLeadEvaluators(suppliersForLeadEvaluators);
					}
				}

				// Below Code to show all CQ details
				List<EvaluationCqPojo> allCqs = getAllSupplierCqforEvaluationSummary(eventId, isMasked);

				// Below functionality to get all BQ Items along with suppliers
				// response of each BQ Items
				List<EvaluationSuppliersBqPojo> supplierBq = getAllSupplierBqforEvaluationSummary(submittedSupplierList, eventId, isMasked);

				// GRAND TOTAL BQ LIST
				List<Bq> bqs = rfqEventBqDao.findBqbyEventId(eventId);
				if (CollectionUtil.isNotEmpty(bqs)) {
					for (Bq bq : bqs) {
						EvaluationBqPojo supBqs = new EvaluationBqPojo();
						supBqs.setName(bq.getName());
						List<EvaluationSuppliersPojo> suppBqComments = new ArrayList<EvaluationSuppliersPojo>();
						if (CollectionUtil.isNotEmpty(submittedSupplierList)) {
							for (EventSupplier supplier : submittedSupplierList) {
								EvaluationSuppliersPojo supList = new EvaluationSuppliersPojo();
								RfqSupplierBq supBq = rfqSupplierBqDao.findBqByBqId(bq.getId(), supplier.getSupplier().getId());
								if (isMasked) {
									supList.setSupplierName(MaskUtils.maskName("SUPPLIER", supplier.getSupplier().getId(), eventId));
								} else {
									supList.setSupplierName(supplier.getSupplier() != null ? supplier.getSupplier().getCompanyName() : "");
								}
								if (supBq != null) {
									supList.setGrandTotal(df.format(supBq.getGrandTotal()));
									supList.setTotalItemTaxAmt(supBq.getTotalAfterTax() != null ? df.format(supBq.getTotalAfterTax()) : "");
									supList.setTotalAmt(df.format(supBq.getAdditionalTax() != null ? supBq.getAdditionalTax() : BigDecimal.ZERO));
								}
								String comments = "";
								List<RfqBqTotalEvaluationComments> comment = rfqBqTotalEvaluationCommentsService.getComments(supplier.getSupplier().getId(), eventId, bq.getId(), SecurityLibrary.getLoggedInUser());
								if (CollectionUtil.isNotEmpty(comment)) {
									for (RfqBqTotalEvaluationComments leadComments : comment) {
										comments += "[" + leadComments.getLoginName() + " ] " + leadComments.getComment() + "\n";
									}
								}
								supList.setRemark(comments);
								suppBqComments.add(supList);
							}
						}
						supBqs.setLeadComments(suppBqComments);
						bqGranTotalList.add(supBqs);
					}
				}

				List<EvaluationSuppliersSorPojo> supplierSor = getAllSupplierSorforEvaluationSummary(submittedSupplierList, eventId, isMasked);

				// LOG.info(supplierBq.toString());
				summary.setBqLeadCommentsList(bqGranTotalList);
				summary.setSuppliers(allSuppliers);
				summary.setCqs(allCqs);
				summary.setBqSuppliers(supplierBq);
				summary.setSorSuppliers(supplierSor);

				rfqSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rfqSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfqSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationSuppliersSorPojo> getAllSupplierSorforEvaluationSummary(List<EventSupplier> supplierList, String eventId, boolean isMasked) {
		List<EvaluationSuppliersSorPojo> supplierBq = new ArrayList<EvaluationSuppliersSorPojo>();

		if (CollectionUtils.isNotEmpty(supplierList)) {
			for (EventSupplier supItem : supplierList) {
				if (supItem.getDisqualify() == Boolean.FALSE) {
					EvaluationSuppliersSorPojo bqSupplierPojo = new EvaluationSuppliersSorPojo();
					if (isMasked) {
						bqSupplierPojo.setSupplierName(MaskUtils.maskName("SUPPLIER", supItem.getSupplier().getId(), eventId));
					} else {
						bqSupplierPojo.setSupplierName(supItem.getSupplier() != null ? supItem.getSupplier().getCompanyName() : "");
					}

					List<Sor> bqs = rfqEventSorDao.findSorbyEventId(eventId);

					List<EvaluationSorPojo> allBqs = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Sor bq : bqs) {
							EvaluationSorPojo bqItem = new EvaluationSorPojo();
							bqItem.setName(bq.getName());
							List<RfqSupplierSorItem> suppBqItems = rfqSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());

							List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RfqSupplierSorItem suppBqItem : suppBqItems) {

									EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
									evlBqItem.setDescription(suppBqItem.getItemName());
									evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
									evlBqItem.setAmount(null);
									evlBqItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
									evlBqItems.add(evlBqItem);
									if (suppBqItem.getChildren() != null) {
										for (RfqSupplierSorItem childBqItem : suppBqItem.getChildren()) {
											EvaluationSorItemPojo evlBqChilItem = new EvaluationSorItemPojo();
											evlBqChilItem.setDescription(childBqItem.getItemName());
											evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
											evlBqChilItem.setUom(childBqItem.getUom().getUom());
											evlBqChilItem.setAmount(childBqItem.getTotalAmount());
											evlBqChilItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");

											// Review Comments
											List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
//											List<RfaSorEvaluationComments> bqItemComments = rfaSorEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getSorItem().getId(), null);
//											if (CollectionUtil.isNotEmpty(bqItemComments)) {
//												String reviews = "";
//												for (RfaSorEvaluationComments review : bqItemComments) {
//													EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
//													bqComment.setCommentBy(review.getUserName());
//													bqComment.setComments(review.getComment());
//													comments.add(bqComment);
//													reviews += "[ " + review.getUserName() + " ] " + review.getComment() + "\n";
//												}
//												evlBqChilItem.setReviews(reviews);
//												evlBqChilItem.setReview(comments);
//											}

											LOG.info("BQ COMMENTS :: " + comments.toString());
											evlBqItems.add(evlBqChilItem);
										}
									}
								}
							}
							bqItem.setBqItems(evlBqItems);

							allBqs.add(bqItem);
						}
					}
					bqSupplierPojo.setSors(allBqs);

					supplierBq.add(bqSupplierPojo);
				}
			}
		}
		return supplierBq;
	}

	private List<EvaluationSuppliersBqPojo> getAllSupplierBqforEvaluationSummary(List<EventSupplier> supplierList, String eventId, boolean isMasked) {

		List<EvaluationSuppliersBqPojo> supplierBq = new ArrayList<EvaluationSuppliersBqPojo>();

		if (CollectionUtils.isNotEmpty(supplierList)) {
			for (EventSupplier supItem : supplierList) {
				if (supItem.getDisqualify() == Boolean.FALSE) {
					EvaluationSuppliersBqPojo bqSupplierPojo = new EvaluationSuppliersBqPojo();

					if (isMasked) {
						bqSupplierPojo.setSupplierName(MaskUtils.maskName("SUPPLIER", supItem.getSupplier().getId(), eventId));
					} else {
						bqSupplierPojo.setSupplierName(supItem.getSupplier() != null ? supItem.getSupplier().getCompanyName() : "");
					}
					List<Bq> bqs = rfqEventBqDao.findBqbyEventId(eventId);

					List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Bq bq : bqs) {
							EvaluationBqPojo bqItem = new EvaluationBqPojo();
							bqItem.setName(bq.getName());
							List<RfqSupplierBqItem> suppBqItems = rfqSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());
							for (RfqSupplierBqItem rfqSupplierBqItem : suppBqItems) {
								BigDecimal grandTotal = rfqSupplierBqItem.getSupplierBq().getGrandTotal();
								bqSupplierPojo.setGrandTotal(grandTotal);
								LOG.info("GRANDTOTAL " + rfqSupplierBqItem.getSupplierBq().getGrandTotal());
							}

							List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RfqSupplierBqItem suppBqItem : suppBqItems) {
									EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
									evlBqItem.setDescription(suppBqItem.getItemName());
									evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
									evlBqItem.setQuantity(suppBqItem.getQuantity());
									// evlBqItem.setUom(suppBqItem.getUom().getUom());
									evlBqItem.setTaxAmt(suppBqItem.getTax());
									evlBqItem.setUnitPrice(suppBqItem.getUnitPrice());
									evlBqItem.setTotalAmt(suppBqItem.getTotalAmountWithTax());
									// evlBqItem.setAmount(suppBqItem.getTotalAmount());
									evlBqItem.setAmount(null);
									evlBqItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
									evlBqItems.add(evlBqItem);

									if (suppBqItem.getChildren() != null) {
										for (RfqSupplierBqItem childBqItem : suppBqItem.getChildren()) {
											EvaluationBqItemPojo evlBqChilItem = new EvaluationBqItemPojo();
											evlBqChilItem.setDescription(childBqItem.getItemName());
											evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
											evlBqChilItem.setQuantity(childBqItem.getQuantity());
											evlBqChilItem.setUom(childBqItem.getUom().getUom());
											evlBqChilItem.setTaxAmt(childBqItem.getTax());
											evlBqChilItem.setUnitPrice(childBqItem.getUnitPrice());
											evlBqChilItem.setTotalAmt(childBqItem.getTotalAmountWithTax());
											evlBqChilItem.setAmount(childBqItem.getTotalAmount());
											evlBqChilItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
											evlBqChilItem.setSubtotal(childBqItem.getSupplierBq().getGrandTotal());
											evlBqChilItem.setTotalAmtTax(childBqItem.getSupplierBq().getTotalAfterTax());
											evlBqChilItem.setAdditionalTax(childBqItem.getSupplierBq().getAdditionalTax());

											// Review Comments
											List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
											List<RfqBqEvaluationComments> bqItemComments = rfqBqEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getBqItem().getId(), null);
											if (CollectionUtil.isNotEmpty(bqItemComments)) {
												String reviews = "";
												for (RfqBqEvaluationComments review : bqItemComments) {
													EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
													bqComment.setCommentBy(review.getLoginName());
													bqComment.setComments(review.getComment());
													comments.add(bqComment);
													reviews += "[ " + review.getLoginName() + " ] " + review.getComment() + "\n";
												}
												evlBqChilItem.setReviews(reviews);
												evlBqChilItem.setReview(comments);
											}

											evlBqItems.add(evlBqChilItem);
										}
									}
								}
							}
							bqItem.setBqItems(evlBqItems);

							allBqs.add(bqItem);
						}
					}
					bqSupplierPojo.setBqs(allBqs);
					supplierBq.add(bqSupplierPojo);
				}
			}
		}
		return supplierBq;

	}

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, boolean isMasked) {

		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		List<RfqCq> cqList = rfqCqService.findCqForEvent(eventId);
		for (RfqCq cq : cqList) {
			EvaluationCqPojo cqPojo = new EvaluationCqPojo();
			cqPojo.setName(cq.getName());
			cqPojo.setDescription(cq.getDescription());

			List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
			if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
				for (RfqCqItem cqItem : cq.getCqItems()) {
					String itemName = "";
					EvaluationCqItemPojo cqItemPojo = new EvaluationCqItemPojo();
					if (cqItem.getAttachment() == Boolean.TRUE) {
						itemName = cqItem.getItemName() + (Boolean.TRUE == cqItem.getIsSupplierAttachRequired() ? " (Attachment is Required) " : " (Attachment is Optional) ");

					} else {
						itemName = cqItem.getItemName();
					}
					cqItemPojo.setItemName(itemName);
					cqItemPojo.setItemDescription(cqItem.getItemDescription());
					cqItemPojo.setLevel(cqItem.getLevel() + "." + cqItem.getOrder());

					List<EvaluationCqItemSupplierPojo> cqItemSuppliers = new ArrayList<EvaluationCqItemSupplierPojo>();
					List<Supplier> suppList = rfqEventSupplierDao.getEventSuppliersForEvaluation(eventId);
					for (Supplier supplier : suppList) {
						if (isMasked) {
							supplier.setCompanyName(MaskUtils.maskName("SUPPLIER", supplier.getId(), eventId));
						}
					}
					// Below code to get Suppliers Answers of each CQ Items
					if (CollectionUtil.isNotEmpty(suppList)) {
						// List<RfqSupplierCqItem> responseList =
						// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppList);
						List<RfqSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
						if (CollectionUtil.isNotEmpty(responseList)) {
							for (RfqSupplierCqItem suppCqItem : responseList) {
								List<RfqSupplierCqOption> listAnswers = rfqSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
								EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
								if (isMasked) {
									cqItemSupplierPojo.setSupplierName(MaskUtils.maskName("SUPPLIER", suppCqItem.getSupplier().getId(), eventId));
								} else {
									cqItemSupplierPojo.setSupplierName(suppCqItem.getSupplier().getCompanyName());
								}

								if (suppCqItem.getCqItem().getCqType() == CqType.TEXT || suppCqItem.getCqItem().getCqType() == CqType.DATE || suppCqItem.getCqItem().getCqType() == CqType.NUMBER || suppCqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
									cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
								}

								else if (CqType.DOCUMENT_DOWNLOAD_LINK == suppCqItem.getCqItem().getCqType()) {
									List<String> docIds = new ArrayList<String>();
									// List<RfqCqOption> rfqSupplierCqOptions = suppCqItem.getCqItem().getCqOptions();
									List<RfqCqOption> rfqSupplierCqOptions = rfqCqOptionDao.findOptionsByCqItem(suppCqItem.getCqItem().getId());// suppCqItem.getCqItem().getCqOptions();
									if (CollectionUtil.isNotEmpty(rfqSupplierCqOptions)) {
										for (RfqCqOption rfqSupplierCqOption : rfqSupplierCqOptions) {
											docIds.add(StringUtils.checkString(rfqSupplierCqOption.getValue()));
										}
									}
									List<EventDocument> eventDocuments = rfqDocumentService.findAllRfqEventDocsNamesByEventIdAndDocIds(docIds);
									if (eventDocuments != null) {
										String str = "";
										for (EventDocument docName : eventDocuments) {
											str = str + docName.getFileName() + "\n";
										}
										cqItemSupplierPojo.setAnswer(str);
									}
								} else if (CollectionUtil.isNotEmpty(listAnswers) && (suppCqItem.getCqItem().getCqType() == CqType.LIST || suppCqItem.getCqItem().getCqType() == CqType.CHECKBOX)) {
									String str = "";
									// List<RfqSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfqSupplierCqOption op : listAnswers) {
										str += op.getValue() + "\n";
									}
									cqItemSupplierPojo.setAnswer(str);
								} else if (CollectionUtil.isNotEmpty(listAnswers) && suppCqItem.getCqItem().getCqType() != CqType.DOCUMENT_DOWNLOAD_LINK) {
									String str = "";
									// List<RfqSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfqSupplierCqOption op : listAnswers) {
										// str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() :
										// "");
										int cqAnsListSize = (listAnswers).size();
										int index = (listAnswers).indexOf(op);
										str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));
									}
									cqItemSupplierPojo.setAnswer(str);
								}
								cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
								// Review Comments
								List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
								List<RfqCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
								if (CollectionUtil.isNotEmpty(comments)) {
									String evalComment = "";
									for (RfqCqEvaluationComments item : comments) {
										EvaluationCqItemSupplierCommentsPojo supCmnts = new EvaluationCqItemSupplierCommentsPojo();
										supCmnts.setComment(item.getComment());
										supCmnts.setCommentBy(item.getUserName());
										evalComments.add(supCmnts);
										evalComment += "[ " + item.getUserName() + " ] " + item.getComment() + "\n";
									}
									cqItemSupplierPojo.setEvalComment(evalComment);
									cqItemSupplierPojo.setComments(evalComments);
								}
								// Attachments
								if (StringUtils.checkString(suppCqItem.getFileName()).length() > 0) {
									cqItemSupplierPojo.setAttachments(suppCqItem.getFileName());
								}

								cqItemSuppliers.add(cqItemSupplierPojo);
							}
						}
					}
					cqItemPojo.setSuppliers(cqItemSuppliers);

					allCqItems.add(cqItemPojo);
				}
			}
			cqPojo.setCqItem(allCqItems);
			allCqs.add(cqPojo);
		}
		return allCqs;
	}

	@SuppressWarnings("deprecation")
	@Override
	public JasperPrint getEvaluationSummaryPdf(RfqEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rfqEventDao.findByEventId(event.getId());
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> summary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		/*
		 * String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY); if (strTimeZone != null) {
		 * timeZone = TimeZone.getTimeZone(strTimeZone); }
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		timeFormat.setTimeZone(timeZone);
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/GenerateEvaluationSummary.jasper");

			String imgPath = context.getRealPath("resources/images");

			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			eventDetails.setReferenceId(event.getEventId());
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(event.getEventName());
			eventDetails.setEventOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
			String owner = "";
			if (event.getEventOwner() != null) {
				owner += event.getEventOwner().getName() + "\r\n" + event.getEventOwner().getCommunicationEmail() + "\r\n" + StringUtils.checkString(event.getEventOwner().getPhoneNumber());
			}
			eventDetails.setOwner(owner);
			eventDetails.setEventStart(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			eventDetails.setEmail(event.getEventOwner().getCommunicationEmail());
			eventDetails.setType("RFQ");
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			eventDetails.setTeanantType("Buyer");
			eventDetails.setMinimumSupplierRating(event.getMinimumSupplierRating() != null ? event.getMinimumSupplierRating() : null);
			eventDetails.setMaximumSupplierRating(event.getMaximumSupplierRating() != null ? event.getMaximumSupplierRating() : null);
			if (Boolean.TRUE == event.getEnableEvaluationDeclaration() && event.getEvaluationProcessDeclaration() != null) {
				eventDetails.setEvalutionDeclaration(event.getEvaluationProcessDeclaration().getTitle());
			}
			if (Boolean.TRUE == event.getEnableSupplierDeclaration() && event.getSupplierAcceptanceDeclaration() != null) {
				eventDetails.setSupplierDeclaration(event.getSupplierAcceptanceDeclaration().getTitle());
			}
			eventDetails.setUrgentEvent(event.getUrgentEvent());
			eventDetails.setAllowToSuspendEvent(event.getAllowToSuspendEvent());
			eventDetails.setCloseEnvelope(event.getCloseEnvelope());
			eventDetails.setAddSupplier(event.getAddSupplier());
			eventDetails.setAllowEvaluationForDisqualifySupplier(event.getAllowDisqualifiedSupplierDownload());

			eventDetails.setEnableApprovalReminder(event.getEnableApprovalReminder());
			if (Boolean.TRUE == event.getEnableApprovalReminder()) {
				eventDetails.setReminderAfterHour(event.getReminderAfterHour());
				eventDetails.setReminderCount(event.getReminderCount());
			}
			eventDetails.setNotifyEventOwner(event.getNotifyEventOwner());
			if (!event.getViewSupplerName() && CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
					String unMaskedOwner = "";
					for (RfqUnMaskedUser rfaUnmaskedUser : event.getUnMaskedUsers()) {
						if (rfaUnmaskedUser.getUser() != null) {
							unMaskedOwner += rfaUnmaskedUser.getUser().getName() + "\r\n" + rfaUnmaskedUser.getUser().getLoginId() + "," + "\r\n";
						}
					}
					if (StringUtils.checkString(unMaskedOwner).length() > 0) {
						unMaskedOwner = StringUtils.checkString(unMaskedOwner).substring(0, StringUtils.checkString(unMaskedOwner).length() - 1);
						eventDetails.setUnmaskingOwners(unMaskedOwner);
					}
				}
			}
			if (event.getViewSupplerName()) {
				eventDetails.setUnmaskingOwners("N/A");
			}
			if (event.getRfxEnvelopeOpening() != null && Boolean.TRUE == event.getRfxEnvelopeOpening()) {
				eventDetails.setRfxEnvOpeningAfter(event.getRfxEnvOpeningAfter().equals("OPENING") ? "After each Opening" : "After each Evaluation");
			} else {
				eventDetails.setRfxEnvOpeningAfter("N/A");
			}
			if (event.getEnableEvaluationConclusionUsers() && CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				String evaluationUsers = "";
				for (RfqEvaluationConclusionUser rfaEvaluationUsers : event.getEvaluationConclusionUsers()) {
					if (rfaEvaluationUsers.getUser() != null) {
						evaluationUsers += rfaEvaluationUsers.getUser().getName() + "\r\n" + rfaEvaluationUsers.getUser().getLoginId() + "," + "\r\n";
					}
				}
				if (StringUtils.checkString(evaluationUsers).length() > 0) {
					evaluationUsers = StringUtils.checkString(evaluationUsers).substring(0, StringUtils.checkString(evaluationUsers).length() - 1);
					eventDetails.setEvalConclusionOwners(evaluationUsers);
				}
			}
			List<EvaluationPojo> eventRemider = new ArrayList<EvaluationPojo>();
			List<EvaluationPojo> eventStartRemider = new ArrayList<EvaluationPojo>();
			if (CollectionUtil.isNotEmpty(event.getRftReminder())) {
				for (RfqReminder item : event.getRftReminder()) {
					EvaluationPojo remider = new EvaluationPojo();
					if (item.getStartReminder() == Boolean.TRUE) {
						remider.setEventStart(item.getReminderDate() != null ? sdf.format(item.getReminderDate()) : "");
						eventStartRemider.add(remider);
					} else {
						remider.setEventEnd(item.getReminderDate() != null ? sdf.format(item.getReminderDate()) : "");
						eventRemider.add(remider);
					}
				}
			}
			eventDetails.setReminderStartDate(eventStartRemider);
			eventDetails.setReminderDate(eventRemider);

			eventDetails.setVisibility(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			SimpleDateFormat ddsdf = new SimpleDateFormat("dd/MM/yyyy");
			ddsdf.setTimeZone(sdf.getTimeZone());
			eventDetails.setDiliveryDate(event.getDeliveryDate() != null ? ddsdf.format(event.getDeliveryDate()) : "N/A");
			eventDetails.setPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			eventDetails.setValidityDays(event.getSubmissionValidityDays());
			String participationFeeCurrency = event.getParticipationFeeCurrency() != null ? event.getParticipationFeeCurrency().getCurrencyCode() : "";
			eventDetails.setParticipationFeeAndCurrency(participationFeeCurrency + " " + (event.getParticipationFees() != null ? formatedDecimalNumber("2", event.getParticipationFees()) : "-"));
			String depositCurrency = event.getDepositCurrency() != null ? event.getDepositCurrency().getCurrencyCode() : "";
			eventDetails.setDepositAndCurrency(depositCurrency + " " + (event.getDeposit() != null ? formatedDecimalNumber("2", event.getDeposit()) : "-"));

			// Solving issue PH-2916
			List<IndustryCategoryPojo> industryCategories = new ArrayList<IndustryCategoryPojo>();
			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				LOG.info(" Industry Categories " + event.getIndustryCategories().size());
				for (IndustryCategory category : event.getIndustryCategories()) {
					LOG.info(" 1 ");
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(category.getName());
					industryCategories.add(ic);
				}
			}
			eventDetails.setCategory(industryCategories);

			// Correspond Address
			String correspondAddress = "";

			if (event.getEventOwner().getBuyer() != null) {
				correspondAddress += event.getEventOwner().getBuyer().getLine1() + ", \r\n";
				if (event.getEventOwner().getBuyer().getLine2() != null) {
					correspondAddress += event.getEventOwner().getBuyer().getLine2() + ", \r\n";
				}
				correspondAddress += event.getEventOwner().getBuyer().getCity() + ", \r\n";
				if (event.getEventOwner().getBuyer().getState() != null) {
					correspondAddress += event.getEventOwner().getBuyer().getState().getStateName() + ", \r\n";
					correspondAddress += event.getEventOwner().getBuyer().getState().getCountry().getCountryName() + "\r\n";
				}
			}
			eventDetails.setCorrespondAddress(correspondAddress);

			// Delivery Address
			String deliveryAddress = "";
			if (event.getDeliveryAddress() != null) {
				deliveryAddress += event.getDeliveryAddress().getTitle() + ", \r\n" + event.getDeliveryAddress().getLine1() + ", \r\n";
				if (event.getDeliveryAddress().getLine2() != null) {
					deliveryAddress += event.getDeliveryAddress().getLine2() + ", \r\n";
				}
				deliveryAddress += event.getDeliveryAddress().getCity() + ", \r\n";
				if (event.getDeliveryAddress().getState() != null) {
					deliveryAddress += event.getDeliveryAddress().getState().getStateName() + ", \r\n";
					deliveryAddress += event.getDeliveryAddress().getState().getCountry().getCountryName() + "\r\n";
				}
			}
			eventDetails.setDeliveryAddress(deliveryAddress);

			// Event Contact Details.
			List<RfqEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfqEventContact contact : eventContacts) {
					EvaluationContactsPojo contactPojo = new EvaluationContactsPojo();
					contactPojo.setTitle(contact.getTitle());
					contactPojo.setContactName(contact.getContactName());
					contactPojo.setDesignation(contact.getDesignation());
					contactPojo.setContactNumber(contact.getContactNumber());
					contactPojo.setMobileNumber(contact.getMobileNumber());
					contactPojo.setComunicationEmail(contact.getComunicationEmail());
					contactList.add(contactPojo);
				}
			}
			eventDetails.setContacts(contactList);

			// Procurement Information
			eventDetails.setProcurementMethod(event.getProcurementMethod() != null ? event.getProcurementMethod().getProcurementMethod() : "");
			eventDetails.setProcurementCategories(event.getProcurementCategories() != null ? event.getProcurementCategories().getProcurementCategories() : "");

			// Commercial Information.
			eventDetails.setBaseCurrency(event.getBaseCurrency() != null ? (event.getBaseCurrency().getCurrencyCode() + " - " + event.getBaseCurrency().getCurrencyName()) : "");
			eventDetails.setPaymentTerm(event.getPaymentTerm());
			eventDetails.setCostCenter((event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "") + " - " + ((event.getCostCenter() != null && event.getCostCenter().getDescription() != null) ? (event.getCostCenter().getDescription()) : ""));
			eventDetails.setBusinesUnit(event.getBusinessUnit() != null ? event.getBusinessUnit().getUnitName() : "");
			eventDetails.setGroupCode((event.getGroupCode() != null ? event.getGroupCode().getGroupCode() : "") + " - " + (event.getGroupCode() != null ? event.getGroupCode().getDescription() : ""));

			eventDetails.setHistoricAmt(event.getHistoricaAmount());
			eventDetails.setDecimal(event.getDecimal());
			eventDetails.setDescription(event.getEventDescription());
			eventDetails.setBudgetAmt(event.getBudgetAmount());
			eventDetails.setInternalRemarks(event.getInternalRemarks() != null ? event.getInternalRemarks() : " - ");
			eventDetails.setEstimatedBudget(event.getEstimatedBudget());

			// Suppliers List
			List<Supplier> suppliersList = getEventSuppliers(event.getId());
			List<EvaluationSuppliersPojo> suppliers = new ArrayList<EvaluationSuppliersPojo>();
			if (CollectionUtil.isNotEmpty(suppliersList)) {
				for (Supplier evntSupplier : suppliersList) {
					EvaluationSuppliersPojo es = new EvaluationSuppliersPojo();
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						es.setSupplierName(MaskUtils.maskName("SUPPLIER", evntSupplier.getId(), event.getId()));
						eventDetails.setIsMask(true);
					} else {
						eventDetails.setIsMask(false);
						es.setSupplierName(evntSupplier.getCompanyName() != null ? evntSupplier.getCompanyName() : "");
					}
					es.setStatus(evntSupplier.getStatus() != null ? evntSupplier.getStatus().name() : "");
					es.setContactName(evntSupplier.getFullName());
					es.setEmail(evntSupplier.getCommunicationEmail() != null ? evntSupplier.getCommunicationEmail() : "");
					es.setContactNo(evntSupplier.getCompanyContactNumber() != null ? evntSupplier.getCompanyContactNumber() : "");
					suppliers.add(es);
				}
			}

			eventDetails.setSuppliers(suppliers);

			// Meeting Details.
			List<EvaluationMeetingPojo> meetings = summaryMeetingDetails(event, imgPath, sdf);

			// CQ Items
			List<EvaluationCqPojo> allCqData = new ArrayList<EvaluationCqPojo>();
			List<RfqCq> cqList = rfqCqService.findCqForEvent(event.getId());
			if (CollectionUtil.isNotEmpty(cqList)) {
				for (RfqCq item : cqList) {
					EvaluationCqPojo cqs = new EvaluationCqPojo();
					cqs.setName(item.getName());
					cqs.setDescription(item.getDescription());

					List<EvaluationCqItemPojo> cqItemList = new ArrayList<EvaluationCqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getCqItems())) {
						for (RfqCqItem cqItem : item.getCqItems()) {
							String itemName = "";
							EvaluationCqItemPojo cqItems = new EvaluationCqItemPojo();
							cqItems.setLevel(cqItem.getLevel() + "." + cqItem.getOrder());
							if (cqItem.getAttachment() == Boolean.TRUE) {
								itemName = cqItem.getItemName() + (Boolean.TRUE == cqItem.getIsSupplierAttachRequired() ? " (Attachment is Required) " : " (Attachment is Optional) ");

							} else {
								itemName = cqItem.getItemName();
							}
							cqItems.setItemName(itemName);
							cqItems.setItemDescription(cqItem.getItemDescription());
							if (cqItem.getOrder() == 0) {
								cqItems.setIsSection(Boolean.TRUE);
							} else {
								cqItems.setIsSection(Boolean.FALSE);
							}

							String answer = "";
							if (CollectionUtil.isNotEmpty(cqItem.getCqOptions())) {
								for (RfqCqOption cqOption : cqItem.getCqOptions()) {
									if (cqItem.getCqType() != null) {
										cqItems.setOptionType(cqItem.getCqType() != null ? cqItem.getCqType().getValue() : "");
										if (cqItem.getCqType() == CqType.DATE || cqItem.getCqType() == CqType.CHOICE || cqItem.getCqType() == CqType.LIST || cqItem.getCqType() == CqType.CHECKBOX || cqItem.getCqType() == CqType.TEXT) {
											answer += "\u2022 " + cqOption.getValue() + "     ";
										} else if (cqItem.getCqType() == CqType.CHOICE_WITH_SCORE) {
											answer += "\u2022 " + cqOption.getValue() + "/" + cqOption.getScoring() + "     ";
										}
									}
								}
							}
							if (cqItem.getCqType() == CqType.DATE) {
								cqItems.setOptionType("Date");
							}
							cqItems.setAnswer(answer);

							cqItemList.add(cqItems);
						}
					}
					cqs.setCqItem(cqItemList);
					allCqData.add(cqs);
				}
			}

			// Fetch List of suppliers BQ

			List<Bq> bqs = rfqEventBqDao.findBqbyEventId(event.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
			if (CollectionUtil.isNotEmpty(bqs)) {
				for (Bq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<RfqBqItem> bqItems = rfqBqItemDao.findBqItemsForBq(item.getId());
					List<EvaluationBqItemPojo> evaluationBqItem = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfqBqItem bqItem : bqItems) {
							EvaluationBqItemPojo bi = new EvaluationBqItemPojo();
							String priceType = "";
							if (bqItem.getPriceType() != null && bqItem.getPriceType() == PricingTypes.TRADE_IN_PRICE) {
								priceType = "TRADE IN";
							} else if (bqItem.getPriceType() != null && (bqItem.getPriceType() == PricingTypes.BUYER_FIXED_PRICE)) {
								priceType = "BUYER FIX";
							}

							bi.setLevel(bqItem.getLevel() + "." + bqItem.getOrder());
							bi.setItemName(bqItem.getItemName());
							bi.setDescription(bqItem.getItemDescription());
							bi.setUom(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
							bi.setQuantity(bqItem.getQuantity());
							bi.setImgPath(imgPath);
							bi.setPriceType(priceType);
							bi.setUnitPrice(bqItem.getUnitPrice());
							bi.setDecimal(event.getDecimal());
							evaluationBqItem.add(bi);
						}
					}
					bqPojo.setBqItems(evaluationBqItem);
					allBqs.add(bqPojo);
				}
			}

			// Fetch all list of SORS
			List<Sor> sors = rfqEventSorDao.findSorbyEventId(event.getId());
			List<EvaluationSorPojo> allSors = new ArrayList<EvaluationSorPojo>();
			if (CollectionUtil.isNotEmpty(sors)) {
				for (Sor item : sors) {
					EvaluationSorPojo bqPojo = new EvaluationSorPojo();
					bqPojo.setName(item.getName());

					List<RfqSorItem> bqItems = rfqSorItemDao.findSorItemsForSor(item.getId());
					List<EvaluationSorItemPojo> evaluationBqItem = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfqSorItem bqItem : bqItems) {
							EvaluationSorItemPojo bi = new EvaluationSorItemPojo();
//							String priceType = "";
//							if (bqItem.getPriceType() != null && bqItem.getPriceType() == PricingTypes.TRADE_IN_PRICE) {
//								priceType = "TRADE IN";
//							} else if (bqItem.getPriceType() != null && (bqItem.getPriceType() == PricingTypes.BUYER_FIXED_PRICE)) {
//								priceType = "BUYER FIX";
//							}

							bi.setLevel(bqItem.getLevel() + "." + bqItem.getOrder());
							bi.setItemName(bqItem.getItemName());
							bi.setDescription(bqItem.getItemDescription());
							bi.setUom(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
//							bi.setQuantity(bqItem.getQuantity());
							bi.setImgPath(imgPath);
//							bi.setPriceType(priceType);
//							bi.setUnitPrice(bqItem.getUnitPrice());
							bi.setDecimal(event.getDecimal());
							evaluationBqItem.add(bi);
						}
					}
					bqPojo.setBqItems(evaluationBqItem);
					allSors.add(bqPojo);
				}
			}

			// Fetch List of suppliers Envelops
			List<RfqEnvelop> envelops = rfqEnvelopService.getAllEnvelopByEventId(event.getId(), loggedInUser);
			List<EvaluationEnvelopPojo> envlopList = new ArrayList<EvaluationEnvelopPojo>();
			if (CollectionUtil.isNotEmpty(envelops)) {
				for (RfqEnvelop envelop : envelops) {
					EvaluationEnvelopPojo env = new EvaluationEnvelopPojo();
					env.setEnvlopName(envelop.getEnvelopTitle());
					env.setDescription(envelop.getDescription());
					env.setType(envelop.getEnvelopType().getValue());
					env.setOpener(envelop.getOpener() != null ? envelop.getOpener().getName() : "");
					env.setOwner(envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "");
					env.setSequence(envelop.getEnvelopSequence());

					List<EvaluationBqPojo> envlopBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
						for (RfqEventBq item : envelop.getBqList()) {
							EvaluationBqPojo bq = new EvaluationBqPojo();
							bq.setName(item.getName());
							envlopBqs.add(bq);
						}
					}
					List<EvaluationSorPojo> envlopSors = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
						for (RfqEventSor item : envelop.getSorList()) {
							EvaluationSorPojo bq = new EvaluationSorPojo();
							bq.setName(item.getName());
							envlopSors.add(bq);
						}
					}
					List<EvaluationCqPojo> envlopCqs = new ArrayList<EvaluationCqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
						for (RfqCq item : envelop.getCqList()) {
							EvaluationCqPojo cq = new EvaluationCqPojo();
							cq.setName(item.getName());
							envlopCqs.add(cq);
						}
					}

					// List of openers
					List<EvaluationEnvelopPojo> openersList = new ArrayList<EvaluationEnvelopPojo>();
					List<RfqEnvelopeOpenerUser> openers = envelop.getOpenerUsers();
					if (CollectionUtil.isNotEmpty(openers)) {
						for (RfqEnvelopeOpenerUser usr : openers) {
							EvaluationEnvelopPojo el = new EvaluationEnvelopPojo();
							el.setOwner(usr.getUser().getName());
							openersList.add(el);
						}
					}

					//List of Evaluators
					List<EvaluationEnvelopPojo> evaluatorUserList = new ArrayList<EvaluationEnvelopPojo>();
					List<RfqEvaluatorUser> evaluatorList = envelop.getEvaluators();
					if(CollectionUtil.isNotEmpty(evaluatorList)){
						for(RfqEvaluatorUser evaluator : evaluatorList ){
							EvaluationEnvelopPojo el = new EvaluationEnvelopPojo();
							el.setOwner(evaluator.getUser().getName());
							evaluatorUserList.add(el);
						}
					}

					env.setEvaluator(evaluatorUserList);
					env.setOpenerUsers(openersList);
					env.setImagePath(imgPath);
					env.setBqs(envlopBqs);
					env.setCqs(envlopCqs);
					env.setSors(envlopSors);
					envlopList.add(env);
				}
			}
			// Evaluation Conclusion Users
			boolean haveUserConcluded = false;
			if (event.getEnableEvaluationConclusionUsers() && event.getEvaluationConclusionEnvelopeNonEvaluatedCount() != null) {
				eventDetails.setEnvelopeEvaluatedCount(event.getEvaluationConclusionEnvelopeEvaluatedCount());
				eventDetails.setEnvelopeNonEvaluatedCount(event.getEvaluationConclusionEnvelopeNonEvaluatedCount());
				eventDetails.setDisqualifiedSupplierCount(event.getEvaluationConclusionDisqualifiedSupplierCount());
				eventDetails.setRemainingSupplierCount(event.getEvaluationConclusionRemainingSupplierCount());

				List<RfqEvaluationConclusionUser> evaluationConclusionUserList = rfqEventDao.findEvaluationConclusionUsersByEventId(event.getId());
				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					Log.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RfqEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
						LOG.info("Conclusion User");
						EvaluationConclusionUsersPojo user = new EvaluationConclusionUsersPojo();
						user.setUserIndex(userIndex++);
						if (evaluationConclusionUser.getUser() != null) {
							user.setUserName(evaluationConclusionUser.getUser().getName());
						}
						if (Boolean.TRUE == evaluationConclusionUser.getConcluded()) {
							haveUserConcluded = true;
						}
						user.setConcludedDate(timeFormat.format(evaluationConclusionUser.getConcludedTime()));
						user.setRemark(StringUtils.checkString(evaluationConclusionUser.getRemarks()).length() > 0 ? evaluationConclusionUser.getRemarks() : "N/A");
						user.setFileName(StringUtils.checkString(evaluationConclusionUser.getFileName()).length() > 0 ? evaluationConclusionUser.getFileName() : "N/A");
						if (StringUtils.checkString(evaluationConclusionUser.getFileDesc()).length() > 0) {
							user.setFileDescription(StringUtils.checkString(evaluationConclusionUser.getFileDesc()).length() > 0 ? evaluationConclusionUser.getFileDesc() : "N/A");
						}
						evaluationConclusionList.add(user);
					}
				}
				eventDetails.setEvaluationConclusionUsersList(evaluationConclusionList);
			}
			eventDetails.setHaveUserConclusionPermaturly(haveUserConcluded);

			// Event Team Members
			List<EventTeamMember> teamMembers = getTeamMembersForEvent(event.getId());
			List<EvaluationTeamsPojo> membersList = new ArrayList<EvaluationTeamsPojo>();
			if (CollectionUtil.isNotEmpty(teamMembers)) {
				for (EventTeamMember item : teamMembers) {
					EvaluationTeamsPojo members = new EvaluationTeamsPojo();
					members.setOwner(item.getUser().getName());
					members.setEmail(item.getUser().getLoginId());
					members.setTeamMemberType(item.getTeamMemberType().name());
					membersList.add(members);
				}
			}

			// Event TimeLine

			List<RfqEventTimeLine> timeline = getRfqEventTimeline(event.getId());
			List<EvaluationTimelinePojo> timelineList = new ArrayList<EvaluationTimelinePojo>();
			if (CollectionUtil.isNotEmpty(timeline)) {
				for (RfqEventTimeLine item : timeline) {
					EvaluationTimelinePojo et = new EvaluationTimelinePojo();
					et.setEventDate(item.getActivityDate() != null ? sdf.format(item.getActivityDate()) : "");
					et.setDescription(item.getDescription());
					et.setType(item.getActivity().name());
					timelineList.add(et);
				}
			}

			// Event Approvals
			RfqEvent rfqEvent = getEventById(event.getId());
			List<RfqEventApproval> approvals = rfqEvent.getApprovals();
			List<EvaluationApprovalsPojo> approvalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (RfqEventApproval item : approvals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(item.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						if (item.getApprovalUsers() != null) {
							for (RfqApprovalUser usr : item.getApprovalUsers()) {
								EvaluationAprovalUsersPojo usrs = new EvaluationAprovalUsersPojo();
								Integer cnt = item.getApprovalUsers().size();

								if (usr.getApprovalStatus() == ApprovalStatus.APPROVED && usr.getApproval().getApprovalType() == ApprovalType.OR) {
									statusOrFlag = true;
								}
								if (usr.getApprovalStatus() == ApprovalStatus.PENDING && usr.getApproval().getApprovalType() == ApprovalType.AND) {
									statusAndFlag = true;
								}

								userName += usr.getUser().getName() + "  ";
								if (cnt > index) {
									userName += usr.getApproval().getApprovalType() != null ? usr.getApproval().getApprovalType().name() : "";
									userName += "  ";
								}
								if (cnt == index) {
									usrs.setName(userName);
									if (usr.getApproval() != null && usr.getApproval().getApprovalType() != null) {
										usrs.setType(usr.getApproval().getApprovalType().name());
									}
									if (statusOrFlag) {
										usrs.setStatus("APPROVED");
									} else if (statusAndFlag) {
										usrs.setStatus("PENDING");
									} else {
										usrs.setStatus(usr.getApprovalStatus().name());
									}
									usrs.setImgPath(imgPath);
									approvUserList.add(usrs);
								}
								index++;
							}
						}
					}
					approve.setApprovalUsers(approvUserList);
					approvalList.add(approve);
				}
			}

			// Suspension Approvals
			List<RfqEventSuspensionApproval> suspensionApprovals = rfqEvent.getSuspensionApprovals();
			List<EvaluationApprovalsPojo> susppensionApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionApprovals)) {
				for (RfqEventSuspensionApproval approval : suspensionApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfqSuspensionApprovalUser usr : approval.getApprovalUsers()) {
							EvaluationAprovalUsersPojo usrs = new EvaluationAprovalUsersPojo();
							Integer cnt = approval.getApprovalUsers().size();

							if (usr.getApprovalStatus() == ApprovalStatus.APPROVED && usr.getApproval().getApprovalType() == ApprovalType.OR) {
								statusOrFlag = true;
							}
							if (usr.getApprovalStatus() == ApprovalStatus.PENDING && usr.getApproval().getApprovalType() == ApprovalType.AND) {
								statusAndFlag = true;
							}

							userName += usr.getUser().getName() + "  ";
							if (cnt > index) {
								userName += usr.getApproval().getApprovalType() != null ? usr.getApproval().getApprovalType().name() : "";
								userName += "  ";
							}
							if (cnt == index) {
								usrs.setName(userName);
								usrs.setType(usr.getApproval().getApprovalType().name());
								if (statusOrFlag) {
									usrs.setStatus("APPROVED");
								} else if (statusAndFlag) {
									usrs.setStatus("PENDING");
								} else {
									usrs.setStatus(usr.getApprovalStatus().name());
								}
								usrs.setImgPath(imgPath);
								approvUserList.add(usrs);
							}
							index++;
						}
					}
					approve.setApprovalUsers(approvUserList);
					susppensionApprovalList.add(approve);
				}
			}

			// Award Approvals
			List<RfqEventAwardApproval> awardApprovals = rfqEvent.getAwardApprovals();
			List<EvaluationApprovalsPojo> awardApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(awardApprovals)) {
				for (RfqEventAwardApproval approval : awardApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfqAwardApprovalUser usr : approval.getApprovalUsers()) {
							EvaluationAprovalUsersPojo usrs = new EvaluationAprovalUsersPojo();
							Integer cnt = approval.getApprovalUsers().size();

							if (usr.getApprovalStatus() == ApprovalStatus.APPROVED && usr.getApproval().getApprovalType() == ApprovalType.OR) {
								statusOrFlag = true;
							}
							if (usr.getApprovalStatus() == ApprovalStatus.PENDING && usr.getApproval().getApprovalType() == ApprovalType.AND) {
								statusAndFlag = true;
							}

							userName += usr.getUser().getName() + "  ";
							if (cnt > index) {
								userName += usr.getApproval().getApprovalType() != null ? usr.getApproval().getApprovalType().name() : "";
								userName += "  ";
							}
							if (cnt == index) {
								usrs.setName(userName);
								usrs.setType(usr.getApproval().getApprovalType().name());
								if (statusOrFlag) {
									usrs.setStatus("APPROVED");
								} else if (statusAndFlag) {
									usrs.setStatus("PENDING");
								} else {
									usrs.setStatus(usr.getApprovalStatus().name());
								}
								usrs.setImgPath(imgPath);
								approvUserList.add(usrs);
							}
							index++;
						}
					}
					approve.setApprovalUsers(approvUserList);
					awardApprovalList.add(approve);
				}
			}

			// Event Documents
			List<EvaluationDocumentPojo> documentList = new ArrayList<EvaluationDocumentPojo>();
			if (CollectionUtil.isNotEmpty(event.getDocuments())) {
				List<RfqEventDocument> document = rfqDocumentDao.findAllEventdocsbyEventId(event.getId());// event.getDocuments();
				for (RfqEventDocument docs : document) {
					EvaluationDocumentPojo item = new EvaluationDocumentPojo();
					item.setDescription(docs.getDescription());
					item.setFileName(docs.getFileName());
					item.setUploadDate(new Date(sdf.format(docs.getUploadDate())));
					item.setSize(docs.getFileSizeInKb() != null ? (double) docs.getFileSizeInKb() : 0);
					if (docs.getInternal() == Boolean.TRUE) {
						item.setInternal("Internal");
					} else {
						item.setInternal("External");
					}
					documentList.add(item);
				}
			}

			// Comments
			List<RfqComment> comments = event.getComment();
			List<EvaluationCommentsPojo> commentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(comments)) {
				for (RfqComment item : comments) {
					EvaluationCommentsPojo ec = new EvaluationCommentsPojo();
					// if (item.isApproved() == Boolean.TRUE) {
					ec.setComment(item.getComment());
					ec.setCreatedBy(item.getCreatedBy().getName());
					if (item.getCreatedDate() != null) {
						ec.setCreatedDate(new Date(sdf.format(item.getCreatedDate())));
					}
					ec.setImgPath(imgPath);
					commentDetails.add(ec);
					// }
				}
			}

			// Suspension Approval Comments
			List<RfqSuspensionComment> suspensionComments = event.getSuspensionComment();
			List<EvaluationCommentsPojo> suspensionCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionComments)) {
				for (RfqSuspensionComment comment : suspensionComments) {
					EvaluationCommentsPojo ec = new EvaluationCommentsPojo();
					// if (comment.isApproved() == Boolean.TRUE) {
					ec.setComment(comment.getComment());
					ec.setCreatedBy(comment.getCreatedBy().getName());
					if (comment.getCreatedDate() != null) {
						ec.setCreatedDate(new Date(sdf.format(comment.getCreatedDate())));
					}
					ec.setImgPath(imgPath);
					suspensionCommentDetails.add(ec);
					// }
				}
			}

			// Award Approval Comments
			List<RfqAwardComment> awardComments = event.getAwardComment();
			List<EvaluationCommentsPojo> awardCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(awardComments)) {
				for (RfqAwardComment comment : awardComments) {
					EvaluationCommentsPojo ec = new EvaluationCommentsPojo();
					// if (comment.isApproved() == Boolean.TRUE) {
					ec.setComment(comment.getComment());
					ec.setCreatedBy(comment.getCreatedBy().getName());
					if (comment.getCreatedDate() != null) {
						ec.setCreatedDate(new Date(sdf.format(comment.getCreatedDate())));
					}
					ec.setImgPath(imgPath);
					awardCommentDetails.add(ec);
					// }
				}
			}

			// Event Audit Details
			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<RfqEventAudit> eventAudit = rfqEventAuditDao.getRfqEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RfqEventAudit ra : eventAudit) {
					EvaluationAuditPojo audit = new EvaluationAuditPojo();
					audit.setAuctionDate(ra.getActionDate() != null ? sdf.format(ra.getActionDate()) : "");
					audit.setAuctionBy(ra.getActionBy() != null ? ra.getActionBy().getName() : "");
					audit.setAuction(ra.getAction().getValue());
					audit.setDescription(ra.getDescription());
					auditList.add(audit);
				}
			}

			eventDetails.setComments(commentDetails);
			eventDetails.setMeetings(meetings);
			eventDetails.setCqs(allCqData);
			eventDetails.setBqs(allBqs);
			eventDetails.setSors(allSors);
			eventDetails.setEnvelops(envlopList);
			eventDetails.setEvaluationTeam(membersList);
			eventDetails.setTimelines(timelineList);
			eventDetails.setApprovals(approvalList);
			eventDetails.setDocuments(documentList);
			eventDetails.setAuditDetails(auditList);
			eventDetails.setSuspensionApprovals(susppensionApprovalList);
			eventDetails.setSuspensionComments(suspensionCommentDetails);
			eventDetails.setAwardApprovals(awardApprovalList);
			eventDetails.setAwardComments(awardCommentDetails);
			summary.add(eventDetails);

			parameters.put("EVALUATION_SUMMARY", summary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	/**
	 * @param event
	 * @param imgPath
	 * @param sdf
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RfqEvent event, String imgPath, SimpleDateFormat sdf) {
		List<RfqEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfqEventMeeting meeting : meetingList) {
				EvaluationMeetingPojo em = new EvaluationMeetingPojo();
				em.setAppointmentDateTime(new Date(sdf.format(meeting.getAppointmentDateTime())));
				em.setRemarks(meeting.getRemarks());
				em.setStatus(meeting.getStatus().name());
				em.setVenue(meeting.getVenue());
				em.setTitle(meeting.getTitle());
				em.setMandatoryMeeting(meeting.getMeetingAttendMandatory() ? "Yes" : "No");

				// Fetch Contact Details -Start
				List<EvaluationMeetingContactsPojo> contacts = new ArrayList<EvaluationMeetingContactsPojo>();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
					for (RfqEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
						EvaluationMeetingContactsPojo contact = new EvaluationMeetingContactsPojo();
						contact.setContactEmail(mc.getContactEmail());
						contact.setContactName(mc.getContactName());
						contact.setContactNumber(mc.getContactNumber());
						contact.setImagePath(imgPath);
						contacts.add(contact);
					}
				}
				em.setMeetingContacts(contacts);

				// Fetch Contact Details - End
				List<EvaluationSuppliersPojo> invitedSuppliers = new ArrayList<EvaluationSuppliersPojo>();
				if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
					for (Supplier supplier : meeting.getInviteSuppliers()) {
						EvaluationSuppliersPojo is = new EvaluationSuppliersPojo();
						is.setSupplierName(supplier.getCompanyName());
						invitedSuppliers.add(is);
					}
				}
				em.setMeeingInviteSupplier(invitedSuppliers);

				// Documents
				List<EvaluationDocumentPojo> meetingDocs = new ArrayList<EvaluationDocumentPojo>();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
					for (RfqEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
						EvaluationDocumentPojo item = new EvaluationDocumentPojo();
						item.setFileName(docs.getFileName());
						item.setSize((double) ((docs.getFileData().length) / 1024));
						meetingDocs.add(item);
					}
				}
				em.setMeetingDocuments(meetingDocs);
				meetings.add(em);
			}
		}
		return meetings;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public RfqEvent cancelEvent(RfqEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer, User loggedInUser) throws Exception {
		boolean isCancelAfterSuspend = false;
		RfqEvent persistObj = rfqEventDao.findById(event.getId());
		if (EventStatus.SUSPENDED == persistObj.getStatus()) {
			isCancelAfterSuspend = true;
		}
		persistObj.setStatus(EventStatus.CANCELED);
		persistObj.setCancelReason(event.getCancelReason());
		persistObj = rfqEventDao.update(persistObj);

		// Decrease event count on cancel
		buyerDao.decreaseEventLimitCountByBuyerId(loggedInUser.getBuyer().getId());

		if (persistObj != null) {

			byte[] summarySnapshot = null;
			try {
				JasperPrint eventSummary = getEvaluationSummaryPdf(event, loggedInUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			} catch (Exception e) {
				LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
			}
			if (isCancelAfterSuspend) {
				try {
					LOG.info("publishing rfq cancel event to epportal");
					publishEventService.pushRfqEvent(persistObj.getId(), persistObj.getTenantId(), false);
				} catch (Exception e) {
					LOG.error("Error while publishing RFQ event to EPortal:" + e.getMessage(), e);
				}
			}
			try {
				RfqEventAudit audit = new RfqEventAudit(loggedInUser.getBuyer(), persistObj, loggedInUser, new java.util.Date(), AuditActionType.Cancel, messageSource.getMessage("event.audit.canceled", new Object[] { persistObj.getEventName() }, Global.LOCALE), summarySnapshot);
				rfqEventAuditDao.save(audit);
			} catch (Exception e) {
				LOG.error("Error creating cancel rfq audit : " + e.getMessage(), e);
			}

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Event '" + persistObj.getEventId() + "' Cancelled", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			// Check if budget checking ERP interface is enabled
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush() && (persistObj.getBusinessUnit() != null && Boolean.TRUE == persistObj.getBusinessUnit().getBudgetCheck())) {
				erpIntegrationService.transferRejectRfsToErp(persistObj, erpSetup, loggedInUser);
			}

		}
		return persistObj;
	}

	@SuppressWarnings("deprecation")
	@Override
	public JasperPrint getMeetingAttendanceReport(String eventId, String meetingId, HttpSession session) {
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> summary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String imgPath = context.getRealPath("resources/images");
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/MettingAttendanceReport.jasper");
			// String imgPath = context.getRealPath("resources/images");
			File jasperfile = resource.getFile();
			RfqEvent event = getRfqEventByeventId(eventId);
			if (event != null) {
				EvaluationPojo eventDetails = new EvaluationPojo();

				// Event Details
				eventDetails.setBuyerName(event.getEventOwner() != null ? event.getEventOwner().getBuyer().getCompanyName() : "");
				eventDetails.setEventName(event.getEventName());
				eventDetails.setEventId(event.getEventId());
				eventDetails.setReferenceNo(event.getReferanceNumber());
				String owner = "";
				if (event.getEventOwner() != null) {
					owner += event.getEventOwner().getName() + "\r\n" + event.getEventOwner().getCommunicationEmail() + "\r\n";
					owner += "Tel: ";
					if (event.getEventOwner().getOwner() != null) {
						owner += event.getEventOwner().getOwner().getCompanyContactNumber();
					}
					owner += "\t HP:";
					if (event.getEventOwner().getPhoneNumber() != null) {
						owner += event.getEventOwner().getPhoneNumber();
					}

				}
				eventDetails.setOwner(owner);
				eventDetails.setEventStart(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
				eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");

				// Meeting Details
				// List<EvaluationMeetingPojo> meetings =
				// summaryMeetingDetails(event, imgPath);

				RfqEventMeeting meetingItem = rfqEventMeetingDao.findById(meetingId);
				EvaluationMeetingPojo meeting = new EvaluationMeetingPojo();
				meeting.setTitle(meetingItem.getTitle());
				if (meetingItem.getAppointmentDateTime() != null) {
					meeting.setAppointmentDateTime(new Date(sdf.format(meetingItem.getAppointmentDateTime())));
				}
				meeting.setRemarks(meetingItem.getRemarks());
				meeting.setVenue(meetingItem.getVenue());

				List<EvaluationMeetingContactsPojo> contacts = new ArrayList<EvaluationMeetingContactsPojo>();
				if (CollectionUtil.isNotEmpty(meetingItem.getRfxEventMeetingContacts())) {
					for (RfqEventMeetingContact mc : meetingItem.getRfxEventMeetingContacts()) {
						EvaluationMeetingContactsPojo contact = new EvaluationMeetingContactsPojo();
						contact.setContactEmail(mc.getContactEmail());
						contact.setContactName(mc.getContactName());
						contact.setContactNumber(mc.getContactNumber());
						contact.setImagePath(imgPath);
						contacts.add(contact);
					}
				}
				meeting.setMeetingContacts(contacts);
				List<EvaluationMeetingPojo> meetings = Collections.singletonList(meeting);

				// Supplier Meeting Attendance
				List<EvaluationSuppliersPojo> meetingSuppliers = new ArrayList<EvaluationSuppliersPojo>();

				if (CollectionUtil.isNotEmpty(meetingItem.getInviteSuppliers())) {
					for (Supplier sup : meetingItem.getInviteSuppliers()) {
						EvaluationSuppliersPojo invitedSupplier = new EvaluationSuppliersPojo();
						RfqSupplierMeetingAttendance attendance = rfqSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meetingId, sup.getId());

						// Primary Contact
						invitedSupplier.setSupplierName(sup.getCompanyName());
						invitedSupplier.setContactName(sup.getFullName());
						invitedSupplier.setDesignation(sup.getDesignation());
						invitedSupplier.setEmail(sup.getCommunicationEmail());
						invitedSupplier.setContactNo(sup.getCompanyContactNumber());

						// Attend Contact
						if (attendance != null) {
							invitedSupplier.setAttendName(attendance.getName());
							invitedSupplier.setAttendDesignation(attendance.getDesignation());
							invitedSupplier.setAttendEmail(attendance.getEmail() != null ? attendance.getEmail() : "");
							invitedSupplier.setAttendContact(attendance.getMobileNumber());
							invitedSupplier.setStatus(attendance.getMeetingAttendanceStatus() != null ? attendance.getMeetingAttendanceStatus().name() : "");
							if (attendance.getMeetingAttendanceStatus() == MeetingAttendanceStatus.Accepted) {
								invitedSupplier.setRemark(attendance.getRemarks());
							}
							if (attendance.getMeetingAttendanceStatus() == MeetingAttendanceStatus.Rejected) {
								invitedSupplier.setRemark(attendance.getRejectReason());
							}

						}

						meetingSuppliers.add(invitedSupplier);

					}
				}
				eventDetails.setSuppliers(meetingSuppliers);
				eventDetails.setMeetings(meetings);
				summary.add(eventDetails);

			}
			parameters.put("MEETING_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Meeting Attendace PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent concludeRfqEvent(RfqEvent event, User loggedInUser) {
		// Clear Award details if present.
		int deleteCount = rfqEventAwardDao.removeAwardDetails(event.getId());

		RfqEvent dbevent = getRfqEventByeventId(event.getId());

		if (deleteCount > 0) {
			try {
				RfqEventAudit audit = new RfqEventAudit();
				audit.setActionDate(new Date());
				audit.setActionBy(loggedInUser);
				audit.setDescription(messageSource.getMessage("event.award.discarded", new Object[] { dbevent.getEventName() }, Global.LOCALE));
				audit.setEvent(dbevent);
				audit.setAction(AuditActionType.Discard);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISCARD, messageSource.getMessage("event.award.discarded", new Object[] { dbevent.getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error saving RFT Event Award Discard Audit " + e.getMessage());
			}
		}

		dbevent.setStatus(EventStatus.FINISHED);
		dbevent.setAwardStatus(null);
		dbevent.setAwardedSuppliers(event.getAwardedSuppliers());
		dbevent.setAwardedPrice(event.getAwardedPrice());
		dbevent.setConcludeRemarks(event.getConcludeRemarks());
		dbevent.setConcludeBy(loggedInUser);
		dbevent.setConcludeDate(new Date());
		return rfqEventDao.update(dbevent);
	}

	@Override
	@Transactional(readOnly = false)
	public String createNextEvent(RfqEvent rfqEvent, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String concludeRemarks, String[] invitedSupp) throws Exception {
		RfqEvent oldEvent = getRfqEventByeventId(rfqEvent.getId());

		// Clear Award details if present.
		int deleteCount = rfqEventAwardDao.removeAwardDetails(rfqEvent.getId());
		if (deleteCount > 0) {
			try {
				RfqEventAudit audit = new RfqEventAudit();
				audit.setActionDate(new Date());
				audit.setActionBy(loggedInUser);
				audit.setDescription(messageSource.getMessage("event.award.discarded", new Object[] { oldEvent.getEventName() }, Global.LOCALE));
				audit.setEvent(rfqEvent);
				audit.setAction(AuditActionType.Discard);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISCARD, messageSource.getMessage("event.award.discarded", new Object[] { oldEvent.getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error saving RFQ Event Award Discard Audit " + e.getMessage());
			}
		}

		String oldEventIdDesc = "This Event is created from " + (oldEvent != null ? oldEvent.getEventId() : "") + " ";
		if (oldEvent.getDeliveryAddress() != null && oldEvent.getDeliveryAddress().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Delivery address found ....");
			throw new ApplicationException("Delivery address is Inactive for event:" + oldEvent.getEventId());
		} else {
			LOG.info("active Delivery address found ........");
		}
		if (oldEvent.getGroupCode() != null && Status.INACTIVE == oldEvent.getGroupCode().getStatus()) {
			LOG.info("inactive Group Code found ....");
			throw new ApplicationException("The group code '" + oldEvent.getGroupCode().getGroupCode() + "' used in Event is inactive");
		} else {
			LOG.info("active Group Code found ........");
		}

		String newEventId = null;
		RfxTemplate template = null;
		List<RfqSupplierBq> rftSupplierBqs = rfqSupplierBqService.findRfqSummarySupplierBqbyEventId(rfqEvent.getId());

		BusinessUnit selectedbusinessUnit = null;
		if (StringUtils.checkString(businessUnitId).length() > 0)
			selectedbusinessUnit = businessUnitService.getBusinessUnitById(businessUnitId);

		if (StringUtils.checkString(idRfxTemplate).length() > 0) {
			template = rfxTemplateService.getRfxTemplateById(idRfxTemplate);
		}
		if (template != null && Status.INACTIVE == template.getStatus()) {
			LOG.info("inactive Template [" + template.getTemplateName() + "] found for Id .... " + template.getId());
			throw new ApplicationException("Template [" + template.getTemplateName() + "] is Inactive");
		}

		if (selectedRfxType != null) {
			switch (selectedRfxType) {
			case RFA: {

				List<RfqSupplierBq> rfqSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfqSupplierBqList = new ArrayList<RfqSupplierBq>();
					for (RfqSupplierBq bq : rftSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfqSupplierBqList.add(bq);
						}
					}
				}

				RfaEvent newEvent = rfqEvent.createNextRfaEvent(oldEvent, auctionType, bqId, loggedInUser, invitedSupp, rfqSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				List<RfaSupplierBq> supplierBqs = newEvent.getRfaSupplierBqs();
				AuctionRules auctionRules = new AuctionRules();
				if (template != null) {
					newEvent.setTemplate(template);

					rfaEventService.createRfaFromTemplate(newEvent, template, auctionRules, selectedbusinessUnit, loggedInUser, false);

				} else {

					if (selectedbusinessUnit != null) {
						LOG.info("setting business unit while temlate is null ......");
						newEvent.setBusinessUnit(selectedbusinessUnit);
					}
				}
				ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(loggedInUser.getTenantId());
				if (erpSetup != null) {
					LOG.info("--------erp flag set for event-----------");
					newEvent.setErpEnable(erpSetup.getIsErpEnable());

				} else {
					newEvent.setErpEnable(false);
				}
				newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFA", newEvent.getBusinessUnit()));

				// Auction Rules
				if (newEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || newEvent.getAuctionType() == AuctionType.REVERSE_ENGISH || newEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID || newEvent.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					newEvent.setBillOfQuantity(Boolean.TRUE);
				}

				RfaEvent newdbEvent = rfaEventService.saveRfaEvent(newEvent, loggedInUser);
				newEvent.setId(newdbEvent.getId());
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfaTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}

				/*
				 * newdbEvent = rfaEventService.saveRfaEvent(newdbEvent, loggedInUser);
				 */
				// save Auction Rule
				auctionRules.setEvent(newdbEvent);
				auctionRules.setAuctionType(newdbEvent.getAuctionType());
				if (auctionRules.getAuctionType() == AuctionType.FORWARD_DUTCH || auctionRules.getAuctionType() == AuctionType.FORWARD_ENGISH || auctionRules.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
					auctionRules.setFowardAuction(Boolean.TRUE);
				} else {
					auctionRules.setFowardAuction(Boolean.FALSE);
				}

				rfaEventService.saveAuctionRules(auctionRules);

				// save Contacts
				if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
					for (RfaEventContact contact : newEvent.getEventContacts()) {
						contact.setRfaEvent(newdbEvent);
						rfaEventService.saveRfaEventContact(contact);
					}
				}
				// save Supplier
				for (RfaEventSupplier supp : newEvent.getSuppliers()) {
					supp.setRfxEvent(newdbEvent);
					rfaEventSupplierDao.save(supp);
				}

				// save BQ
				for (RfaEventBq bq : newEvent.getEventBqs()) {
					bq.setRfxEvent(newdbEvent);
					RfaEventBq dbBq = rfaBqDao.saveOrUpdate(bq);
					bq.setId(dbBq.getId());
					// save BQ Items
					if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
						RfaBqItem parent = null;
						for (RfaBqItem item : bq.getBqItems()) {
							if (item.getOrder() != 0) {
								item.setParent(parent);
							}
							item.setBq(dbBq);
							item.setRfxEvent(newdbEvent);
							RfaBqItem itemDb = rfaBqItemDao.saveOrUpdate(item);
							item.setId(itemDb.getId());
							if (item.getOrder() == 0) {
								parent = item;
							}
						}
					}
				}
				if (invitedSupp != null && invitedSupp.length > 0) {
					if (CollectionUtil.isNotEmpty(supplierBqs)) {
						for (RfaSupplierBq sbq : supplierBqs) {
							RfaSupplierBq sbqDb = rfaSupplierBqService.updateSupplierBq(sbq);
							RfaSupplierBqItem parent = null;
							for (RfaSupplierBqItem sbqi : sbq.getSupplierBqItems()) {
								sbqi.setSupplierBq(sbqDb);
								if (sbqi.getOrder() != 0) {
									sbqi.setParent(parent);
								}
								RfaSupplierBqItem sbqiDb = rfaSupplierBqItemDao.saveOrUpdate(sbqi);
								if (sbqi.getOrder() == 0) {
									parent = sbqiDb;
								}
							}
						}
					}
				}
				newEventId = newdbEvent.getId();
				if (StringUtils.checkString(newEventId).length() > 0) {
					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setAction(AuditActionType.Create);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						oldEventIdDesc += (StringUtils.checkString(concludeRemarks).length() > 0 ? concludeRemarks : "");
						audit.setDescription(oldEventIdDesc);
						audit.setEvent(newdbEvent);
						eventAuditService.save(audit);

						RfqEventAudit rfqAudit = new RfqEventAudit();
						rfqAudit.setAction(AuditActionType.Create);
						rfqAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfqAudit.setActionDate(new Date());
						rfqAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfqAudit.setEvent(oldEvent);
						eventAuditService.save(rfqAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}

				break;
			}
			case RFI: {
				RfiEvent newEvent = rfqEvent.createNextRfiEvent(oldEvent, loggedInUser, invitedSupp);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				if (template != null) {
					newEvent.setTemplate(template);
					rfiEventService.createRfiFromTemplate(newEvent, template, selectedbusinessUnit, loggedInUser, false);
				} else {

					if (selectedbusinessUnit != null) {
						LOG.info("setting business unit while temlate is null ......");
						newEvent.setBusinessUnit(selectedbusinessUnit);
					}
				}
				if (loggedInUser.getBuyer().getErpEnable()) {
					ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(loggedInUser.getTenantId());
					if (erpSetup != null) {
						LOG.info("--------erp flag set for event-----------");
						newEvent.setErpEnable(erpSetup.getIsErpEnable());
					}

				}
				newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFI", newEvent.getBusinessUnit()));
				RfiEvent newdbEvent = rfiEventService.saveRfiEvent(newEvent);

				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfiTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}

				// save Contacts
				if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
					for (RfiEventContact contact : newEvent.getEventContacts()) {
						contact.setRfxEvent(newdbEvent);
						rfiEventService.saveRfiEventContact(contact);
					}
				}
				// save Supplier
				for (RfiEventSupplier supp : newEvent.getSuppliers()) {
					supp.setRfxEvent(newdbEvent);
					rfiEventSupplierDao.save(supp);
				}
				newEventId = newdbEvent.getId();
				if (StringUtils.checkString(newEventId).length() > 0) {
					try {
						RfiEventAudit audit = new RfiEventAudit();
						audit.setAction(AuditActionType.Create);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						oldEventIdDesc += (StringUtils.checkString(concludeRemarks).length() > 0 ? concludeRemarks : "");
						audit.setDescription(oldEventIdDesc);
						audit.setEvent(newdbEvent);
						eventAuditService.save(audit);

						RfqEventAudit rfqAudit = new RfqEventAudit();
						rfqAudit.setAction(AuditActionType.Create);
						rfqAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfqAudit.setActionDate(new Date());
						rfqAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfqAudit.setEvent(oldEvent);
						eventAuditService.save(rfqAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFP: {
				List<RfqSupplierBq> rfqSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfqSupplierBqList = new ArrayList<RfqSupplierBq>();
					for (RfqSupplierBq bq : rftSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfqSupplierBqList.add(bq);
						}
					}
				}

				RfpEvent newEvent = rfqEvent.createNextRfpEvent(oldEvent, loggedInUser, invitedSupp, rfqSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				if (template != null) {
					newEvent.setTemplate(template);
					rfpEventService.createRfpFromTemplate(newEvent, template, null, selectedbusinessUnit, loggedInUser, false);
				} else {

					if (selectedbusinessUnit != null) {
						LOG.info("setting business unit while temlate is null ......");
						newEvent.setBusinessUnit(selectedbusinessUnit);
					}
				}
				if (loggedInUser.getBuyer().getErpEnable()) {
					ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(loggedInUser.getTenantId());
					if (erpSetup != null) {
						LOG.info("--------erp flag set for event-----------");
						newEvent.setErpEnable(erpSetup.getIsErpEnable());
					}

				}
				newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFP", newEvent.getBusinessUnit()));
				RfpEvent newdbEvent = rfpEventService.saveEvent(newEvent, loggedInUser);
				newEvent.setId(newdbEvent.getId());

				// List<RfpSupplierBq> supplierBqs = newEvent.getRfpSupplierBqs();
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfpTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}

				// save Contacts
				if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
					for (RfpEventContact contact : newEvent.getEventContacts()) {
						contact.setRfxEvent(newdbEvent);
						rfpEventService.saveEventContact(contact);
					}
				}
				// save Supplier
				for (RfpEventSupplier supp : newEvent.getSuppliers()) {
					supp.setRfxEvent(newdbEvent);
					rfpEventSupplierDao.save(supp);
				}

				// save BQ
				if (CollectionUtil.isNotEmpty(newEvent.getEventBqs()))
					for (RfpEventBq bq : newEvent.getEventBqs()) {
						bq.setRfxEvent(newdbEvent);
						RfpEventBq dbBq = rfpBqDao.saveOrUpdate(bq);
						bq.setId(dbBq.getId());
						// save BQ Items
						if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
							RfpBqItem parent = null;
							for (RfpBqItem item : bq.getBqItems()) {
								if (item.getOrder() != 0) {
									item.setParent(parent);
								}
								item.setBq(dbBq);
								item.setRfxEvent(newdbEvent);
								RfpBqItem itemDb = rfpBqItemDao.saveOrUpdate(item);
								item.setId(itemDb.getId());
								if (item.getOrder() == 0) {
									parent = item;
								}
							}
						}
					}

				newEventId = newdbEvent.getId();
				if (StringUtils.checkString(newEventId).length() > 0) {
					try {
						RfpEventAudit audit = new RfpEventAudit();
						audit.setAction(AuditActionType.Create);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						oldEventIdDesc += (StringUtils.checkString(concludeRemarks).length() > 0 ? concludeRemarks : "");
						audit.setDescription(oldEventIdDesc);
						audit.setEvent(newdbEvent);
						eventAuditService.save(audit);

						RfqEventAudit rfqAudit = new RfqEventAudit();
						rfqAudit.setAction(AuditActionType.Create);
						rfqAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfqAudit.setActionDate(new Date());
						rfqAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfqAudit.setEvent(oldEvent);
						eventAuditService.save(rfqAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFQ: {

				List<RfqSupplierBq> rfqSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfqSupplierBqList = new ArrayList<RfqSupplierBq>();
					for (RfqSupplierBq bq : rftSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfqSupplierBqList.add(bq);
						}
					}
				}
				RfqEvent newEvent = rfqEvent.createNextRfqEvent(oldEvent, loggedInUser, invitedSupp, rfqSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());

				// List<RfqSupplierBq> supplierBqs = newEvent.getRfqSupplierBqs();
				if (template != null) {
					newEvent.setTemplate(template);
					createRfqFromTemplate(newEvent, template, selectedbusinessUnit, loggedInUser, false);
				} else {

					if (selectedbusinessUnit != null) {
						LOG.info("setting business unit while temlate is null ......");
						newEvent.setBusinessUnit(selectedbusinessUnit);
					}
				}
				if (loggedInUser.getBuyer().getErpEnable()) {
					ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(loggedInUser.getTenantId());
					if (erpSetup != null) {
						LOG.info("--------erp flag set for event-----------");
						newEvent.setErpEnable(erpSetup.getIsErpEnable());
					}

				}
				newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFQ", newEvent.getBusinessUnit()));
				RfqEvent newdbEvent = saveEvent(newEvent, loggedInUser);
				newEvent.setId(newdbEvent.getId());

				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfqTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}

				// save Contacts
				if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
					for (RfqEventContact contact : newEvent.getEventContacts()) {
						contact.setRfxEvent(newdbEvent);
						rfqEventContactDao.saveOrUpdate(contact);
					}
				}
				// save Supplier
				for (RfqEventSupplier supp : newEvent.getSuppliers()) {
					supp.setRfxEvent(newdbEvent);
					rfqEventSupplierDao.save(supp);
				}
				// save BQ
				if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {
					for (RfqEventBq bq : newEvent.getEventBqs()) {
						bq.setRfxEvent(newdbEvent);
						RfqEventBq dbBq = rfqEventBqDao.saveOrUpdate(bq);
						bq.setId(dbBq.getId());
						// save BQ Items
						if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
							RfqBqItem parent = null;
							for (RfqBqItem item : bq.getBqItems()) {
								if (item.getOrder() != 0) {
									item.setParent(parent);
								}
								item.setBq(dbBq);
								item.setRfxEvent(newdbEvent);
								RfqBqItem itemDb = rfqBqItemDao.saveOrUpdate(item);
								item.setId(itemDb.getId());
								if (item.getOrder() == 0) {
									parent = item;
								}
							}
						}

					}

				}

				newEventId = newdbEvent.getId();
				if (StringUtils.checkString(newEventId).length() > 0) {
					try {
						RfqEventAudit audit = new RfqEventAudit();
						audit.setAction(AuditActionType.Create);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						oldEventIdDesc += (StringUtils.checkString(concludeRemarks).length() > 0 ? concludeRemarks : "");
						audit.setDescription(oldEventIdDesc);
						audit.setEvent(newdbEvent);
						eventAuditService.save(audit);

						RfqEventAudit rfqAudit = new RfqEventAudit();
						rfqAudit.setAction(AuditActionType.Create);
						rfqAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfqAudit.setActionDate(new Date());
						rfqAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfqAudit.setEvent(oldEvent);
						eventAuditService.save(rfqAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + "'", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFT: {

				List<RfqSupplierBq> rfqSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfqSupplierBqList = new ArrayList<RfqSupplierBq>();
					for (RfqSupplierBq bq : rftSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfqSupplierBqList.add(bq);
						}
					}
				}
				RftEvent newEvent = rfqEvent.createNextRftEvent(oldEvent, loggedInUser, invitedSupp, rfqSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				// List<RftSupplierBq> supplierBqs = newEvent.getRftSupplierBqs();
				if (template != null) {

					newEvent.setTemplate(template);
					rftEventService.createRftFromTemplate(newEvent, template, selectedbusinessUnit, loggedInUser, false);
				} else {

					if (selectedbusinessUnit != null) {
						LOG.info("setting business unit while temlate is null ......");
						newEvent.setBusinessUnit(selectedbusinessUnit);
					}
				}
				if (loggedInUser.getBuyer().getErpEnable()) {
					ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(loggedInUser.getTenantId());
					if (erpSetup != null) {
						LOG.info("--------erp flag set for event-----------");
						newEvent.setErpEnable(erpSetup.getIsErpEnable());
					}

				}
				newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFT", newEvent.getBusinessUnit()));
				RftEvent newDbEvent = rftEventService.saveRftEvent(newEvent, loggedInUser);
				newEvent.setId(newDbEvent.getId());
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RftTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newDbEvent.getId());

					}
				}

				// Save contacts
				if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
					for (RftEventContact contact : newEvent.getEventContacts()) {
						contact.setRfxEvent(newDbEvent);
						rftEventService.saveRftEventContact(contact);
					}
				}
				// save suppliers
				for (RftEventSupplier supp : newEvent.getSuppliers()) {
					supp.setRfxEvent(newDbEvent);
					rftEventSupplierDao.save(supp);
				}
				// save BQ
				if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {
					for (RftEventBq bq : newEvent.getEventBqs()) {
						bq.setRfxEvent(newDbEvent);
						RftEventBq dbBq = rftBqDao.saveOrUpdate(bq);
						bq.setId(dbBq.getId());
						// save BQ Items
						if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
							RftBqItem parent = null;
							for (RftBqItem item : bq.getBqItems()) {
								if (item.getOrder() != 0) {
									item.setParent(parent);
								}
								item.setBq(dbBq);
								item.setRfxEvent(newDbEvent);
								RftBqItem itemDb = rftBqItemDao.saveOrUpdate(item);
								item.setId(itemDb.getId());
								if (item.getOrder() == 0) {
									parent = item;
								}
							}
						}
					}
				}

				newEventId = newDbEvent.getId();
				if (StringUtils.checkString(newEventId).length() > 0) {
					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setAction(AuditActionType.Create);
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						oldEventIdDesc += (StringUtils.checkString(concludeRemarks).length() > 0 ? concludeRemarks : "");
						audit.setDescription(oldEventIdDesc);
						audit.setEvent(newDbEvent);
						eventAuditService.save(audit);

						RfqEventAudit rfqAudit = new RfqEventAudit();
						rfqAudit.setAction(AuditActionType.Create);
						rfqAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfqAudit.setActionDate(new Date());
						rfqAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newDbEvent.getEventId() + " '");
						rfqAudit.setEvent(oldEvent);
						eventAuditService.save(rfqAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + "' is pushed to '" + newDbEvent.getEventId() + "'", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			default:
				break;
			}
		}
		oldEvent.setNextEventType(selectedRfxType);
		oldEvent.setNextEventId(newEventId);
		oldEvent.setConcludeRemarks(rfqEvent.getConcludeRemarks());
		oldEvent.setStatus(EventStatus.FINISHED);
		oldEvent.setConcludeBy(loggedInUser);
		oldEvent.setConcludeDate(new Date());
		oldEvent.setAwardStatus(null);
		rfqEventDao.update(oldEvent);

		try {
			tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(oldEvent.getId(), EventStatus.FINISHED);
		} catch (Exception e) {
			LOG.error("Error updating Tat Report " + e.getMessage(), e);
		}
		
		return newEventId;
	}

	@Override
	public void createRfqFromTemplate(RfqEvent newEvent, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRequest) throws ApplicationException {
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			LOG.info("----------------------create Rfq From Template call----------------------------");

			List<RfqEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfqEventApproval newRfApproval = new RfqEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfqApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfqApprovalUser approvalUser = new RfqApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(newRfApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfApprovalList.add(approvalUser);
					}
					newRfApproval.setApprovalUsers(rfApprovalList);
				}
				approvalList.add(newRfApproval);
			}
			newEvent.setApprovals(approvalList);
		}

		if (CollectionUtil.isNotEmpty(rfxTemplate.getAwardApprovals())) {
			List<RfqEventAwardApproval> approvalList = new ArrayList<>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfqEventAwardApproval newRfApproval = new RfqEventAwardApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfqAwardApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfqAwardApprovalUser approvalUser = new RfqAwardApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(newRfApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfApprovalList.add(approvalUser);
					}
					newRfApproval.setApprovalUsers(rfApprovalList);
				}
				approvalList.add(newRfApproval);
			}
			newEvent.setAwardApprovals(approvalList);
		}

		if (CollectionUtil.isNotEmpty(rfxTemplate.getSuspensionApprovals())) {
			List<RfqEventSuspensionApproval> suspApprovalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateSuspApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Susp Approval Level :" + templateSuspApproval.getLevel());
				RfqEventSuspensionApproval suspesionApproval = new RfqEventSuspensionApproval();
				suspesionApproval.setApprovalType(templateSuspApproval.getApprovalType());
				suspesionApproval.setLevel(templateSuspApproval.getLevel());
				suspesionApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateSuspApproval.getApprovalUsers())) {
					List<RfqSuspensionApprovalUser> rfqSuspApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateSuspApproval.getApprovalUsers()) {
						RfqSuspensionApprovalUser approvalUser = new RfqSuspensionApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(suspesionApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfqSuspApprovalList.add(approvalUser);
					}
					suspesionApproval.setApprovalUsers(rfqSuspApprovalList);
				}
				suspApprovalList.add(suspesionApproval);
			}
			newEvent.setSuspensionApprovals(suspApprovalList);
		}

		newEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newEvent.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newEvent.setAddSupplier(rfxTemplate.getAddSupplier());
		newEvent.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newEvent.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		newEvent.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());
		newEvent.setEnableSuspensionApproval(rfxTemplate.getEnableSuspendApproval());
		newEvent.setEnableAwardApproval(rfxTemplate.getEnableAwardApproval());

		List<RfxEnvelopPojo> envlopeList = new ArrayList<RfxEnvelopPojo>();

		if (rfxTemplate.getRfxEnvelope1() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope1());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence1());
			envlopeList.add(pojo);
		}

		if (rfxTemplate.getRfxEnvelope2() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope2());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence2());
			envlopeList.add(pojo);
		}
		if (rfxTemplate.getRfxEnvelope3() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope3());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence3());
			envlopeList.add(pojo);
		}

		if (rfxTemplate.getRfxEnvelope4() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope4());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence4());
			envlopeList.add(pojo);
		}

		if (rfxTemplate.getRfxEnvelope5() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope5());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence5());
			envlopeList.add(pojo);
		}

		if (rfxTemplate.getRfxEnvelope6() != null) {
			RfxEnvelopPojo pojo = new RfxEnvelopPojo();
			pojo.setRfxEnvelope(rfxTemplate.getRfxEnvelope6());
			pojo.setRfxSequence(rfxTemplate.getRfxSequence6());
			envlopeList.add(pojo);
		}

		if (CollectionUtil.isNotEmpty(envlopeList)) {
			newEvent.setRfxEnvelop(new ArrayList<RfqEnvelop>());

			for (RfxEnvelopPojo en : envlopeList) {
				LOG.info("envlop name form rfx   :  " + en);

				RfqEnvelop rfienvlope = new RfqEnvelop();
				rfienvlope.setEnvelopTitle(en.getRfxEnvelope());
				rfienvlope.setEnvelopSequence(en.getRfxSequence());
				rfienvlope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rfienvlope.setRfxEvent(newEvent);
				rfienvlope.setEnvelopType(EnvelopType.CLOSED);
				newEvent.getRfxEnvelop().add(rfienvlope);
			}
		}

		if (rfxTemplate.getAddBillOfQuantity() != null) {
			newEvent.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
		}
		if (rfxTemplate.getVisibleViewSupplierName()) {
			newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		}
		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());

		List<RfqUnMaskedUser> unmaskingUser = new ArrayList<RfqUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RfqUnMaskedUser newRftUnMaskedUser = new RfqUnMaskedUser();
				newRftUnMaskedUser.setUser(team.getUser());
				newRftUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRftUnMaskedUser);
			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}

		List<RfqTeamMember> teamMembers = new ArrayList<RfqTeamMember>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RfqTeamMember newTeamMembers = new RfqTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setEvent(newEvent);
				teamMembers.add(newTeamMembers);
			}
			newEvent.setTeamMembers(teamMembers);
		}
		// Copy Evaluation Conclusion Users from template
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RfqEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RfqEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RfqEvaluationConclusionUser conclusionUser = new RfqEvaluationConclusionUser();
				conclusionUser.setUser(user);
				conclusionUser.setEvent(newEvent);
				evaluationConclusionUsers.add(conclusionUser);
			}
			newEvent.setEvaluationConclusionUsers(evaluationConclusionUsers);
			LOG.info("Added Evaluation Conclusion user to Event  : " + (newEvent.getEvaluationConclusionUsers() != null ? newEvent.getEvaluationConclusionUsers().size() : 0));
		}

		if (CollectionUtil.isNotEmpty(rfxTemplate.getFields())) {

			for (TemplateField field : rfxTemplate.getFields()) {
				switch (field.getFieldName()) {
				case BASE_CURRENCY:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								Currency currency = currencyDao.findById(field.getDefaultValue());
								newEvent.setBaseCurrency(currency);
								LOG.info("Base Currency : " + currency + "Default value :  " + field.getDefaultValue());
							}
						} else {
							Currency currency = currencyDao.findById(field.getDefaultValue());
							newEvent.setBaseCurrency(currency);
							LOG.info("Base Currency : " + currency + "Default value :  " + field.getDefaultValue());
						}
					}
					break;
				case BUDGET_AMOUNT:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								newEvent.setBudgetAmount(new BigDecimal(field.getDefaultValue()));
								LOG.info("budget amount Default value :  " + field.getDefaultValue());
							}
						} else {
							newEvent.setBudgetAmount(new BigDecimal(field.getDefaultValue()));
							LOG.info("budget amount Default value :  " + field.getDefaultValue());
						}
					}
					break;
				case COST_CENTER:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								CostCenter costCenter = costCenterDao.findById(field.getDefaultValue());
								newEvent.setCostCenter(costCenter);
								LOG.info("costCenter : " + costCenter + "Default value :  " + field.getDefaultValue());
							}
						} else {
							CostCenter costCenter = costCenterDao.findById(field.getDefaultValue());
							newEvent.setCostCenter(costCenter);
							LOG.info("costCenter : " + costCenter + "Default value :  " + field.getDefaultValue());
						}
					}
					break;
				case BUSINESS_UNIT:
					if (field.getDefaultValue() != null) {
						BusinessUnit businessUnit = businessUnitService.getPlainBusinessUnitById(field.getDefaultValue());
						newEvent.setBusinessUnit(businessUnit);
						// LOG.info("businessUnit : " + businessUnit + "Default value : " + field.getDefaultValue());
					}
					break;
				case DECIMAL:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								newEvent.setDecimal(field.getDefaultValue());
								LOG.info("Decimal Default value :  " + field.getDefaultValue());
							}
						} else {
							newEvent.setDecimal(field.getDefaultValue());
							LOG.info("Decimal Default value :  " + field.getDefaultValue());

						}
					}
					break;
				case EVENT_CATEGORY:
					if (field.getDefaultValue() != null) {
						String[] icArr = field.getDefaultValue().split(",");
						List<IndustryCategory> icList = new ArrayList<IndustryCategory>();
						if (icArr != null) {
							for (String icId : icArr) {
								// IndustryCategory ic = industryCategoryService.getIndustryCategoryById(icId);
								IndustryCategory ic = new IndustryCategory();
								ic.setId(icId);
								icList.add(ic);
							}
							newEvent.setIndustryCategories(icList);
						}
						// newEvent.setIndustryCategory(industryCategory);
						// LOG.info("industryCategory : " + industryCategory + "Default value : " +
						// field.getDefaultValue());
					}
					break;
				case EVENT_NAME:
					if (field.getDefaultValue() != null) {
						newEvent.setEventName(field.getDefaultValue());
					}
					break;
				case HISTORIC_AMOUNT:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								newEvent.setHistoricaAmount(new BigDecimal(field.getDefaultValue()));
							}
						} else {
							newEvent.setHistoricaAmount(new BigDecimal(field.getDefaultValue()));
						}
					}
					break;
				case PAYMENT_TERM:
					if (field.getDefaultValue() != null) {
						newEvent.setPaymentTerm(field.getDefaultValue());
					}
					break;
				case PARTICIPATION_FEES:
					if (field.getDefaultValue() != null) {
						newEvent.setParticipationFees(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case DEPOSIT:
					if (field.getDefaultValue() != null) {
						newEvent.setDeposit(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case PARTICIPATION_FEE_CURRENCY:
					if (field.getDefaultValue() != null) {
						Currency currency = currencyDao.findById(field.getDefaultValue());
						newEvent.setParticipationFeeCurrency(currency);
						LOG.info(" Currency : " + currency + "Default value :  " + field.getDefaultValue());
					}
					break;
				case DEPOSIT_CURRENCY:
					if (field.getDefaultValue() != null) {
						Currency currency = currencyDao.findById(field.getDefaultValue());
						newEvent.setDepositCurrency(currency);
						LOG.info(" Currency : " + currency + "Default value :  " + field.getDefaultValue());
					}
					break;
				case MINIMUM_SUPPLIER_RATING:
					if (field.getDefaultValue() != null) {
						newEvent.setMinimumSupplierRating(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case MAXIMUM_SUPPLIER_RATING:
					if (field.getDefaultValue() != null) {
						newEvent.setMaximumSupplierRating(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case EVALUATION_PROCESS_DECLARATION:
					if (field.getDefaultValue() != null) {
						Declaration declaration = declarationDao.findById(field.getDefaultValue());
						newEvent.setEvaluationProcessDeclaration(declaration);
						LOG.info("Evaluation Declaration : " + declaration + " Default value :  " + field.getDefaultValue());
					}
					break;
				case SUPPLIER_ACCEPTANCE_DECLARATION:
					if (field.getDefaultValue() != null) {
						Declaration declaration = declarationDao.findById(field.getDefaultValue());
						newEvent.setSupplierAcceptanceDeclaration(declaration);
						LOG.info("Supplier Declaration : " + declaration + " Default value :  " + field.getDefaultValue());
					}
					break;
				case SUB_VALIDITY_DAYS:
					if (field.getDefaultValue() != null) {
						newEvent.setSubmissionValidityDays(Integer.parseInt(field.getDefaultValue()));
					}
					break;
				case _UNKNOWN:
					if (field.getDefaultValue() != null) {
					}
					break;
				case DISABLE_TOTAL_AMOUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setDisableTotalAmount(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case ESTIMATED_BUDGET:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								newEvent.setEstimatedBudget(new BigDecimal(field.getDefaultValue()));
							}
						} else {
							newEvent.setEstimatedBudget(new BigDecimal(field.getDefaultValue()));
						}
					}
					break;
				case PROCUREMENT_METHOD:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								ProcurementMethod procurementMethod = procurementMethodDao.findById(field.getDefaultValue());
								newEvent.setProcurementMethod(procurementMethod);
								LOG.info("ProcurementMethod : " + procurementMethod + "Default value :  " + field.getDefaultValue());
							}
						} else {
							ProcurementMethod procurementMethod = procurementMethodDao.findById(field.getDefaultValue());
							newEvent.setProcurementMethod(procurementMethod);
							LOG.info("ProcurementMethod : " + procurementMethod + "Default value :  " + field.getDefaultValue());
						}
					}
					break;
				case PROCUREMENT_CATEGORY:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								ProcurementCategories procurementCategory = procurementCategoriesDao.findById(field.getDefaultValue());
								newEvent.setProcurementCategories(procurementCategory);
								LOG.info("ProcurementCategories : " + procurementCategory + "Default value :  " + field.getDefaultValue());
							}
						} else {
							ProcurementCategories procurementCategory = procurementCategoriesDao.findById(field.getDefaultValue());
							newEvent.setProcurementCategories(procurementCategory);
							LOG.info("ProcurementCategories : " + procurementCategory + "Default value :  " + field.getDefaultValue());
						}
					}
					break;
				case GROUP_CODE:
					if (field.getDefaultValue() != null) {
						if (isFromRequest) {
							if (field.getReadOnly()) {
								GroupCode groupCode = groupCodeDao.findById(field.getDefaultValue());
								if (Status.INACTIVE == groupCode.getStatus()) {
									throw new ApplicationException("The group code '" + groupCode.getGroupCode() + "' defaulted in template is inactive");
								}
								newEvent.setGroupCode(groupCode);
							} else {
								if (newEvent.getGroupCode() == null) {
									GroupCode groupCode = groupCodeDao.findById(field.getDefaultValue());
									if (Status.ACTIVE == groupCode.getStatus()) {
										newEvent.setGroupCode(groupCode);
									}
								} else {
									if (Status.INACTIVE == newEvent.getGroupCode().getStatus()) {
										throw new ApplicationException("The group code '" + newEvent.getGroupCode().getGroupCode() + "' used in Sourcing Request is inactive");
									}
								}
							}
						} else {
							GroupCode groupCode = groupCodeDao.findById(field.getDefaultValue());
							if (Status.INACTIVE == groupCode.getStatus()) {
								throw new ApplicationException("The group code '" + groupCode.getGroupCode() + "' defaulted in template is inactive");
							}
							newEvent.setGroupCode(groupCode);
							LOG.info("groupCode : " + groupCode + "Default value :  " + field.getDefaultValue());
						}
					}
					break;
				default:
					break;

				}
			}

		}
		if (eventIdSettingsDao.isBusinessSettingEnable(loggedInUser.getTenantId(), "RFQ")) {
			if (selectedbusinessUnit != null) {
				LOG.info("business unit selected by user choice selected");
				newEvent.setBusinessUnit(selectedbusinessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (newEvent.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select its own business unit");
					throw new ApplicationException("BUSINESS_UNIT_EMPTY");
				}

			}
		}

	}

	@Override
	public List<Supplier> getAwardedSuppliers(String eventId) {
		return rfqEventDao.getAwardedSuppliers(eventId);
	}

	@Override
	public RfqEvent getPlainEventById(String eventId) {
		RfqEvent rfaEvent = rfqEventDao.getPlainEventById(eventId);
		for (RfqUnMaskedUser unmask : rfaEvent.getUnMaskedUsers()) {
			unmask.getUser();
		}
		return rfaEvent;
	}

	@Override
	@Transactional(readOnly = false)
	public void suspendEvent(RfqEvent event) {
		if (event.getSuspensionType() == SuspensionType.DELETE_NO_NOTIFY || event.getSuspensionType() == SuspensionType.DELETE_NOTIFY) {
			supplierCqItemDao.deleteSupplierCqItemsForEvent(event.getId());
			try {
				rfqSupplierCqDao.deleteSupplierCqForEvent(event.getId());
			} catch (Exception e) {
				LOG.error("Error while deleting Supplier Cq : " + e.getMessage(), e);
			}

			rfqSupplierCommentService.deleteSupplierComments(event.getId());
			rfqSupplierBqItemDao.deleteSupplierBqItemsForEvent(event.getId());
			rfqSupplierBqDao.deleteSupplierBqsForEvent(event.getId());
			rfqSupplierSorItemDao.deleteSupplierSorItemsForEvent(event.getId());
			rfqSupplierSorDao.deleteSupplierSorsForEvent(event.getId());
			rfqEventSupplierDao.updateSubmiTimeOnEventSuspend(event.getId());
		}
		rfqEventDao.suspendEvent(event.getId(), event.getSuspensionType(), event.getSuspendRemarks());
	}

	@Override
	@Transactional(readOnly = false)
	public void resumeEvent(RfqEvent event) {
		rfqEventDao.resumeEvent(event.getId());
	}

	@Override
	public long getEventSuppliersCount(String eventId) {
		return rfqEventSupplierDao.getEventSuppliersCount(eventId);
	}

	@Override
	public List<Date> getAllMeetingDateByEventId(String eventId) {
		return rfqEventMeetingDao.getAllMeetingDateByEventId(eventId);
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		return rfqEventDao.findAssignedTemplateCount(templateId);
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		return rfqEventDao.getPlainEventOwnerByEventId(eventId);
	}

	@Override
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		return rfqEventDao.getPlainTeamMembersForEvent(eventId);
	}

	@Override
	public MobileEventPojo getMobileEventDetails(String id, String userId) throws ApplicationException {
		RfqEvent event = rfqEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(event.getTenantId(), timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			List<Bq> bqlist = new ArrayList<>();
			for (RfqEventBq bq : event.getEventBqs()) {
				bqlist.add(bq.createMobileShallowCopy());
			}
			eventPojo.setBqs(bqlist);
		}
		if (CollectionUtil.isNotEmpty(event.getSuppliers())) {

			// Supplier Masking
			// <c:if test="${(!event.viewSupplerName) and (eventPermissions.owner or eventPermissions.editor or
			// eventPermissions.approverUser or ( eventPermissions.viewer and !eventPermissions.leadEvaluator and
			// !eventPermissions.evaluator and !eventPermissions.opener))}">

			List<EventSupplier> supplierList = new ArrayList<>();
			for (EventSupplier eventSupplier : event.getSuppliers()) {
				supplierList.add(eventSupplier.createMobileShallowCopy());
			}
			eventPojo.setSuppliers(supplierList);
		}

		List<EventTimelinePojo> timeLineList = new ArrayList<>();
		for (EventTimeline timeLine : rfqEventTimeLineDao.getPlainRfqEventTimeline(event.getId())) {
			EventTimelinePojo time = new EventTimelinePojo();
			Date ActivityDatedate = timeLine.getActivityDate();

			String activityDate = df.format(ActivityDatedate);
			LOG.info("Date in String " + activityDate);
			try {
				// Date date = df.parse(activityDate);
				time.setId(timeLine.getId());
				time.setActivityDate(activityDate);
				time.setBuyer(timeLine.getBuyer());
				time.setDescription(timeLine.getDescription());
				time.setActivity(timeLine.getActivity());
				timeLineList.add(time);
				LOG.info("java.util.date " + time.getActivityDate());
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage());
			}

		}
		eventPojo.setTimeLine(timeLineList);

		// eventPojo.setTimeLine(rfqEventTimeLineDao.getPlainRfqEventTimeline(event.getId()));
		eventPojo.setDocuments(rfqDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRfxEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RfqEnvelop envelope : event.getRfxEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RfqEventApproval eventApproval : event.getApprovals()) {
				if (CollectionUtil.isNotEmpty(eventApproval.getApprovalUsers())) {
					for (ApprovalUser approvalUser : eventApproval.getApprovalUsers()) {

						if (approvalUser.getActionDate() != null)
							approvalUser.setActionDateApk(df.format(approvalUser.getActionDate()));
						else
							approvalUser.setActionDateApk("N/A");

						eventApproval.addUsers(approvalUser.createMobileShallowCopy());
					}
				}
				approvalList.add(eventApproval);
			}
			eventPojo.setApprovers(approvalList);
		}

		if (CollectionUtil.isNotEmpty(event.getComment())) {
			List<Comments> commentList = new ArrayList<>();
			for (RfqComment comment : event.getComment()) {
				comment.setTransientIsApproved(comment.isApproved());
				String actionDateApk = df.format(comment.getCreatedDate());
				comment.setCreatedDateApk(actionDateApk);
				comment.setTransientIsApproved(comment.isApproved());
				commentList.add(comment.createMobileShallowCopy());
			}
			eventPojo.setComments(commentList);
		}
		if (event.getTemplate() != null) {
			eventPojo.setTemplateName(event.getTemplate().getTemplateName());
		}
		if (event.getBusinessUnit() != null) {
			if (StringUtils.checkString(event.getBusinessUnit().getUnitName()).length() > 0) {
				eventPojo.setBusinessUnit(event.getBusinessUnit().getUnitName());
			} else {
				eventPojo.setBusinessUnit("N/A");
			}
		}
		if (event.getCostCenter() != null)
			eventPojo.setCostCenter(event.getCostCenter().getCostCenter());
		else {
			eventPojo.setCostCenter("N/A");
		}
		return eventPojo;
	}

	@Override
	public MobileEventPojo getMobileEventDetailsForSupplier(String id, String userId, String tenantId) throws ApplicationException {
		RfqEvent event = rfqEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(tenantId, timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			List<Bq> bqlist = new ArrayList<>();
			for (RfqEventBq bq : event.getEventBqs()) {
				bqlist.add(bq.createMobileShallowCopy());
			}
			eventPojo.setBqs(bqlist);
		}
		if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
			List<EventSupplier> supplierList = new ArrayList<>();
			for (EventSupplier eventSupplier : event.getSuppliers()) {
				supplierList.add(eventSupplier.createMobileShallowCopy());
			}
			eventPojo.setSuppliers(supplierList);
		}

		List<EventTimelinePojo> timeLineList = new ArrayList<>();
		for (EventTimeline timeLine : rfqEventTimeLineDao.getPlainRfqEventTimeline(event.getId())) {
			EventTimelinePojo time = new EventTimelinePojo();
			Date ActivityDatedate = timeLine.getActivityDate();

			String activityDate = df.format(ActivityDatedate);
			LOG.info("Date in String " + activityDate);
			try {
				// Date date = df.parse(activityDate);
				time.setId(timeLine.getId());
				time.setActivityDate(activityDate);
				time.setBuyer(timeLine.getBuyer());
				time.setDescription(timeLine.getDescription());
				time.setActivity(timeLine.getActivity());
				timeLineList.add(time);
				LOG.info("java.util.date " + time.getActivityDate());
			} catch (Exception e) {
				throw new ApplicationException(e.getMessage());
			}

		}
		eventPojo.setTimeLine(timeLineList);

		// eventPojo.setTimeLine(rfqEventTimeLineDao.getPlainRfqEventTimeline(event.getId()));
		eventPojo.setDocuments(rfqDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRfxEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RfqEnvelop envelope : event.getRfxEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RfqEventApproval eventApproval : event.getApprovals()) {
				if (CollectionUtil.isNotEmpty(eventApproval.getApprovalUsers())) {
					for (ApprovalUser approvalUser : eventApproval.getApprovalUsers()) {

						if (approvalUser.getActionDate() != null)
							approvalUser.setActionDateApk(df.format(approvalUser.getActionDate()));
						else
							approvalUser.setActionDateApk("N/A");

						eventApproval.addUsers(approvalUser.createMobileShallowCopy());
					}
				}
				approvalList.add(eventApproval);
			}
			eventPojo.setApprovers(approvalList);
		}

		if (CollectionUtil.isNotEmpty(event.getComment())) {
			List<Comments> commentList = new ArrayList<>();
			for (RfqComment comment : event.getComment()) {
				comment.setTransientIsApproved(comment.isApproved());
				String actionDateApk = df.format(comment.getCreatedDate());
				comment.setCreatedDateApk(actionDateApk);
				comment.setTransientIsApproved(comment.isApproved());
				commentList.add(comment.createMobileShallowCopy());
			}
			eventPojo.setComments(commentList);
		}
		if (event.getTemplate() != null) {
			eventPojo.setTemplateName(event.getTemplate().getTemplateName());
		}
		if (event.getBusinessUnit() != null) {
			if (StringUtils.checkString(event.getBusinessUnit().getUnitName()).length() > 0) {
				eventPojo.setBusinessUnit(event.getBusinessUnit().getUnitName());
			} else {
				eventPojo.setBusinessUnit("N/A");
			}
		}
		if (event.getCostCenter() != null)
			eventPojo.setCostCenter(event.getCostCenter().getCostCenter());
		else {
			eventPojo.setCostCenter("N/A");
		}
		return eventPojo;
	}

	@Override
	public void downloadEventDocument(String docId, HttpServletResponse response) throws Exception {
		RfqEventDocument docs = rfqDocumentDao.findDocsById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadEventSummary(String eventId, HttpServletResponse response, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer) {
		try {
			String filename = "unknowEventSummary.pdf";
			RfqEvent event = getRfqEventByeventId(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " summary.pdf";
			}
			JasperPrint jasperPrint = getEvaluationSummaryPdf(event, loggedInUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@Override
	public RfqEvent getRfqEventByeventIdForSummaryReport(String eventId) {
		RfqEvent rfq = rfqEventDao.findByEventId(eventId);
		if (rfq.getEventOwner().getBuyer() != null) {
			rfq.getEventOwner().getBuyer().getLine1();
			rfq.getEventOwner().getBuyer().getLine2();
			rfq.getEventOwner().getBuyer().getCity();
			if (rfq.getEventOwner().getBuyer().getState() != null) {
				rfq.getEventOwner().getBuyer().getState().getStateName();
				if (rfq.getEventOwner().getBuyer().getState().getCountry() != null) {
					rfq.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getSuppliers())) {
			for (RfqEventSupplier item : rfq.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}
		if (rfq.getBusinessUnit() != null) {
			rfq.getBusinessUnit().getUnitName();
		}
		if (rfq.getEventOwner().getBuyer() != null) {
			Buyer buyer = rfq.getEventOwner().getBuyer();
			buyer.getLine1();
			buyer.getLine2();
			buyer.getCity();
			if (buyer.getState() != null) {
				buyer.getState().getStateName();
				if (buyer.getState().getCountry() != null) {
					buyer.getState().getCountry().getCountryName();
				}
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getComment())) {
			for (RfqComment comment : rfq.getComment()) {
				comment.getComment();
				comment.getCreatedBy();
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getName();
				}
				comment.getLoginName();
				comment.getUserName();
				comment.getRfxEvent();
			}
		}

		if (rfq.getDeliveryAddress() != null) {
			rfq.getDeliveryAddress().getLine1();
			rfq.getDeliveryAddress().getLine2();
			rfq.getDeliveryAddress().getCity();
			if (rfq.getDeliveryAddress().getState() != null) {
				rfq.getDeliveryAddress().getState().getStateName();
				rfq.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getRftReminder())) {
			for (RfqReminder reminder : rfq.getRftReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfq.getMeetings())) {
			for (RfqEventMeeting item : rfq.getMeetings()) {
				item.getStatus().name();
				item.getRemarks();
				item.getVenue();
				item.getAppointmentDateTime();
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingContacts())) {
					for (RfqEventMeetingContact contact : item.getRfxEventMeetingContacts()) {
						contact.getContactEmail();
						contact.getContactName();
						contact.getContactNumber();
					}
				}
				if (CollectionUtil.isNotEmpty(item.getInviteSuppliers())) {
					for (Supplier suppliers : item.getInviteSuppliers()) {
						suppliers.getCompanyName();
						suppliers.getCommunicationEmail();
						suppliers.getCompanyContactNumber();
					}
				}
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingDocument())) {
					for (RfqEventMeetingDocument docs : item.getRfxEventMeetingDocument()) {
						docs.getId();
						docs.getFileName();
						docs.getFileSizeInKb();
						docs.getCredContentType();
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(rfq.getIndustryCategories())) {
			for (IndustryCategory industryCategory : rfq.getIndustryCategories()) {
				industryCategory.getName();
				industryCategory.getCreatedBy();
				if (industryCategory.getCreatedBy() != null) {
					industryCategory.getCreatedBy().getName();
				}
				industryCategory.getCode();
				industryCategory.getBuyer();
			}
		}

		return rfq;
	}

	@Override
	public RfqSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId, String tenantId) {
		RfqSupplierBq rfqSupplierBq = rfqEventDao.getSupplierBQOfLeastTotalPrice(eventId, bqId);
		if (rfqSupplierBq != null) {

			if (rfqSupplierBq.getSupplierBqItems() != null) {
				for (RfqSupplierBqItem items : rfqSupplierBq.getSupplierBqItems()) {
					items.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, items.getSupplier().getId()));
					if (items.getBqItem() != null) {
						items.getBqItem().getId();
						items.getBqItem().getLevel();
						if (items.getBqItem().getUom() != null) {
							LOG.info("items.getBqItem().getUom()  : " + items.getBqItem().getUom().getUom());
							items.getBqItem().getUom().getUom();
						}
					}

					// LOG.info(items.getBqItem().getItemName());
				}
			}
		}
		return rfqSupplierBq;
	}

	@Override
	public RfqSupplierBq getSupplierBQOfLowestItemisedPrice(String eventId, String bqId, String tenantId) {
		RfqSupplierBq rfqSupplierBq = new RfqSupplierBq();
		List<RfqSupplierBqItem> bqItemList = new ArrayList<RfqSupplierBqItem>();
		List<RfqSupplierBq> suppBqList = rfqEventDao.getSupplierBQOfLowestItemisedPrize(eventId, bqId);
		if (CollectionUtil.isNotEmpty(suppBqList)) {
			int bqItemCount = 1;
			for (RfqSupplierBq bq : suppBqList) {
				if (CollectionUtil.isNotEmpty(bq.getSupplierBqItems())) {
					for (RfqSupplierBqItem item : bq.getSupplierBqItems()) {
						item.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, item.getSupplier().getId()));
						// LOG.info(item.getBqItem().getId());
						if (item.getBqItem() != null) {

							if (item.getBqItem().getOrder() == 0) {
								if (1 == bqItemCount) {
									bqItemList.add(item);
								}

							} else {
								LOG.info(item.getBqItem().getId() + "---------------" + item.getSupplier().getId());
								RfqSupplierBqItem bqItem = rfqEventDao.getMinItemisePrice(item.getBqItem().getId(), eventId);
								// LOG.info(bqItem);
								if (bqItem.getBqItem() != null) {

									LOG.info(bqItem.getId());
									LOG.info(bqItem.getItemName());
									LOG.info(bqItem.getLevel() + "......." + bqItem.getOrder());
									if (bqItem.getSupplier() != null) {

										LOG.info(bqItem.getSupplier().getId());
										LOG.info(bqItem.getSupplier().getFullName());
									}
								}
								if (!bqItemList.contains(bqItem)) {
									bqItemList.add(bqItem);
									rfqSupplierBq.setBq(bq.getBq());
								}
							}

						}

						// LOG.info(bqItem);

					}
					bqItemCount++;
				}
				LOG.info(bq.getSupplierBqItems().size() + "..........." + bqItemList.size());
				rfqSupplierBq.setSupplierBqItems(bqItemList);

			}

		}

		return rfqSupplierBq;
	}

	@Override
	public RfqSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId, String tenantId, String awardId) {
		RfqSupplierBq rfqSupplierBq = rfqEventDao.getSupplierBQwithSupplierId(eventId, bqId, supplierId);
		if (rfqSupplierBq != null) {

			if (rfqSupplierBq.getSupplierBqItems() != null) {
				for (RfqSupplierBqItem items : rfqSupplierBq.getSupplierBqItems()) {
					RfqEventAwardDetails rfqEventAwardDetails = null;
					if (StringUtils.checkString(awardId).length() > 0) {
						rfqEventAwardDetails = rfqEventAwardDetailsDao.rfqEventAwardByEventIdandBqId(awardId, items.getBqItem().getId());
					}
					items.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, (rfqEventAwardDetails != null && rfqEventAwardDetails.getSupplier() != null) ? rfqEventAwardDetails.getSupplier().getId() : items.getSupplier().getId()));
					if (items.getBqItem() != null) {
						items.getBqItem().getId();
						items.getBqItem().getLevel();
					}
				}
			}
		}
		return rfqSupplierBq;
	}

	@Override
	public RfqEvent getRfqEventWithIndustryCategoriesByEventId(String eventId) {
		RfqEvent rfq = rfqEventDao.findByEventId(eventId);
		if (CollectionUtil.isNotEmpty(rfq.getIndustryCategories())) {
			for (IndustryCategory indCat : rfq.getIndustryCategories()) {
				indCat.getCode();
			}
		}
		return rfq;
	}

	@Override
	public boolean isExistsRfqEventId(String tenantId, String eventId) {
		return rfqEventDao.isExistsRfqEventId(tenantId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfqTeamMember> addAssociateOwners(User createdBy, RfqEvent newEvent) {
		List<User> adminUser = userDao.fetchAllActivePlainAdminUsersForTenant(createdBy.getTenantId());
		List<RfqTeamMember> teamMemberList = new ArrayList<RfqTeamMember>();
		if (CollectionUtil.isNotEmpty(adminUser)) {
			for (User user : adminUser) {
				RfqTeamMember teamMember = new RfqTeamMember();
				teamMember.setUser(user);
				teamMember.setEvent(newEvent);
				teamMember.setTeamMemberType(TeamMemberType.Associate_Owner);
				teamMemberList.add(teamMember);
			}
			newEvent.setTeamMembers(teamMemberList);
		}
		rfqEventDao.saveOrUpdate(newEvent);
		return teamMemberList;
	}

	@Override
	@Transactional(readOnly = false)
	public void setDefaultEventContactDetail(String loggedInUserId, String eventId) {
		RfqEventContact eventContact = new RfqEventContact();
		User user = userDao.findById(loggedInUserId);
		eventContact.setContactName(user.getName());
		eventContact.setDesignation(user.getDesignation());
		eventContact.setContactNumber(user.getPhoneNumber());
		eventContact.setComunicationEmail(user.getCommunicationEmail());
		if (user.getBuyer() != null) {
			eventContact.setMobileNumber(user.getBuyer().getMobileNumber());
			eventContact.setFaxNumber(user.getBuyer().getFaxNumber());
		}
		RfqEvent rfpEvent = new RfqEvent();
		rfpEvent.setId(eventId);
		eventContact.setRfxEvent(rfpEvent);
		saveEventContact(eventContact);
	}

	@Override
	public RfqTeamMember getRfqTeamMemberByUserIdAndEventId(String eventId, String userId) {
		return rfqEventDao.getTeamMemberByUserIdAndEventId(eventId, userId);
	}

	@Override
	public RfqEvent findRfqEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId) {
		return rfqEventDao.findRfqEventByErpAwardRefNoAndTenantId(erpAwardRefId, tenantId);
	}

	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private String getTimeZoneBySupplierSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	@Override
	@Transactional(readOnly = false)
	public String createPrFromAward(RfqEventAward rfqEventAward, String templateId, String userId, User loggedInUser) throws ApplicationException {
		User createdBy = userDao.findById(userId);
		List<String> supplierList = new ArrayList<String>();
		// List<ProductCategory> ctegoryList =
		// productCategoryMaintenanceService.getAllProductCategoryByTenantId(loggedInUser.getTenantId());
		// Supplier -> List of Sections -> List of Items
		Map<String, Map<String, List<PrItem>>> data = new HashMap<String, Map<String, List<PrItem>>>();
		String value = "";
		try {
			LOG.info("rfqEventAward.getRfxAwardDetails()--------" + rfqEventAward.getRfxAwardDetails().size());
			if (CollectionUtil.isNotEmpty(rfqEventAward.getRfxAwardDetails())) {

				BqItem section = null;

				for (RfqEventAwardDetails rfxAward : rfqEventAward.getRfxAwardDetails()) {
					LOG.info("order--------" + rfxAward.getBqItem().getLevel() + "--" + rfxAward.getBqItem().getOrder());
					LOG.info("---------------bqitemId--------------------" + rfxAward.getBqItem().getBqId());

					BqItem dbBqItem = rfqBqItemDao.getBqItemByBqIdAndBqItemId(rfxAward.getBqItem().getId());
					if (dbBqItem.getOrder() == 0) {
						section = dbBqItem;
					} else {
						String sid = rfxAward.getSupplier().getId();
						RfqSupplierBqItem supplierBqItem = rfqSupplierBqItemDao.getSupplierBqItemByBqItemId(rfxAward.getBqItem().getId(), sid);
						LOG.info("rfqEventAward.getRfxAwardDetails() new--------------" + rfxAward.getSupplier().getId());
						if (!supplierList.contains(sid)) {
							supplierList.add(sid);
						}

						Map<String, List<PrItem>> sections = data.get(sid);

						if (sections == null) {
							sections = new HashMap<String, List<PrItem>>();
							data.put(sid, sections);
						}

						List<PrItem> itemList = sections.get(dbBqItem.getLevel().toString());

						if (itemList == null) {
							itemList = new ArrayList<PrItem>();
							sections.put(dbBqItem.getLevel().toString(), itemList);
						}

						// Create the section if its not already created yet.
						if (section != null && itemList.size() == 0) {
							PrItem item = new PrItem();
							item.setItemName(section.getItemName() == null? " " : prService.replaceSmartQuotes(section.getItemName()));
							item.setItemDescription(section.getItemDescription() == null ? " " : prService.replaceSmartQuotes(section.getItemDescription()));
							item.setBuyer(loggedInUser.getBuyer());
							item.setOrder(0);
							item.setTaxAmount(BigDecimal.ZERO);
							item.setTotalAmount(BigDecimal.ZERO);
							item.setTotalAmountWithTax(BigDecimal.ZERO);
							// item.setLevel();
							itemList.add(item);
						}
						PrItem item = new PrItem();
						ProductItem productItem = productCategoryMaintenanceService.checkProductItemExistOrNot(dbBqItem.getItemName(), sid, loggedInUser.getTenantId());
						if (productItem == null) {

//							if (StringUtils.checkString(rfxAward.getProductCategory()).length() <= 0) {
//								throw new ApplicationException("Please select a category for non-catalog item : " + dbBqItem.getItemName());
//							}

							ProductCategory category = productCategoryMaintenanceService.getProductCategoryById(rfxAward.getProductCategory());
							if (category != null) {
								item.setProductCategory(category);
							}

							item.setFreeTextItemEntered(Boolean.TRUE);
							item.setItemName(section.getItemName() == null? " " : prService.replaceSmartQuotes(dbBqItem.getItemName()));
							item.setItemDescription(section.getItemDescription() == null ? " " : prService.replaceSmartQuotes(dbBqItem.getItemDescription()));
							item.setUnit(dbBqItem.getUom());
							BigDecimal ap = rfxAward.getAwardedPrice();
							item.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, RoundingMode.HALF_UP));

							/*
							 * productItem = new ProductItem(); productItem.setProductName(dbBqItem.getItemName());
							 * productItem.setProductCode(productItem.getProductName()); if (dbBqItem.getUom() != null)
							 * { productItem.setUom(dbBqItem.getUom()); }
							 * productItem.setProductCategory(ctegoryList.get(0));
							 * productItem.setTax(rfxAward.getTax()); productItem.setCreatedBy(loggedInUser);
							 * productItem.setCreatedDate(new Date()); BigDecimal ap = rfxAward.getAwardedPrice();
							 * productItem.setUnitPrice(ap.divide(new BigDecimal(supplierBqItem.getQuantity()), 6,
							 * BigDecimal.ROUND_HALF_UP)); productItem.setBuyer(loggedInUser.getBuyer());
							 * FavouriteSupplier favouriteSupplier =
							 * favoriteSupplierDao.getFavouriteSupplierBySupplierId(sid, loggedInUser.getTenantId());
							 * productItem.setFavoriteSupplier(favouriteSupplier);
							 * productCategoryMaintenanceService.saveNewProductItem(productItem);
							 */
						} else {
							item.setProduct(productItem);
							item.setProductCategory(productItem.getProductCategory());
							item.setItemName(section.getItemName() == null? " " : prService.replaceSmartQuotes(dbBqItem.getItemName()));
							item.setItemDescription(section.getItemDescription() == null ? " " : prService.replaceSmartQuotes(dbBqItem.getItemDescription()));
							item.setUnit(productItem.getUom());
							BigDecimal ap = rfxAward.getAwardedPrice();
							item.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, RoundingMode.HALF_UP));
						}

						item.setBuyer(loggedInUser.getBuyer());

						if (supplierBqItem.getTaxType() == TaxType.Amount) {
							item.setItemTax(supplierBqItem.getTax() != null ? supplierBqItem.getTax().divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString() : null);
						} else {
							item.setItemTax(supplierBqItem.getTax() != null ? supplierBqItem.getTax().toString() : null);
						}

						item.setOrder(itemList.size() + 1);
						// item.setLevel(itemList.size());

						item.setQuantity(supplierBqItem.getQuantity());

						// item.setParent(rfxAward.getBqItem().getParent());
						item.setField1(dbBqItem.getField1());
						item.setField2(dbBqItem.getField2());
						item.setField3(dbBqItem.getField3());
						item.setField4(dbBqItem.getField4());

						itemList.add(item);

					}
				}

				int i = 0;
				// Now save the PRs
				for (String sid : data.keySet()) {
					FavouriteSupplier favouriteSupplier = favoriteSupplierDao.getFavouriteSupplierBySupplierId(sid, loggedInUser.getTenantId());
					Map<String, List<PrItem>> sections = data.get(sid);
					RfqEvent rfxEvent = rfqEventService.getEventById(rfqEventAward.getRfxEvent().getId());
					Pr pr = prService.copyFromTemplateWithAward(templateId, createdBy, loggedInUser, loggedInUser.getTenantId(), null, favouriteSupplier, sections, rfxEvent);
					if (i == 0) {
						value = (StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : "") + ": " + pr.getPrId();
					} else {
						value += ", " + pr.getPrId();
					}
					i++;
					LOG.info("*******" + pr.getPrId());
					/*
					 * int level = 1; for (String sectionLevel : sections.keySet()) { List<PrItem> items =
					 * sections.get(sectionLevel); int order = 0; PrItem sectionItem = null; for (PrItem item : items) {
					 * item.setPr(pr); item.setLevel(level); if (order == 0) { sectionItem =
					 * prService.savePrItemBare(item); } else { item.setParent(sectionItem); try {
					 * item.setTotalAmount((item.getUnitPrice() != null && item.getQuantity() != null) ?
					 * item.getUnitPrice().multiply(item.getQuantity()) : new BigDecimal(0));
					 * item.setTaxAmount(item.getItemTax() != null ? item.getTotalAmount().multiply(new
					 * BigDecimal(item.getItemTax()).divide(new BigDecimal(100))) : new BigDecimal(0));
					 * item.setTotalAmountWithTax(item.getTotalAmount() != null ?
					 * item.getTotalAmount().setScale(Integer.parseInt(pr.getDecimal()),
					 * BigDecimal.ROUND_HALF_UP).add(item.getTaxAmount().setScale(Integer.parseInt(pr.getDecimal()),
					 * BigDecimal.ROUND_HALF_UP)) : new BigDecimal(0)); } catch (Exception e) { LOG.error("Error : " +
					 * e.getMessage(), e); throw new
					 * NotAllowedException(messageSource.getMessage("common.number.format.error", new Object[] {},
					 * Global.LOCALE)); } item = prService.savePrItemBare(item); pr.setTotal(pr.getTotal() != null &&
					 * item.getTotalAmountWithTax() != null ? pr.getTotal().add(item.getTotalAmountWithTax()) : new
					 * BigDecimal(0)); pr.setGrandTotal(pr.getTotal() != null && pr.getAdditionalTax() != null ?
					 * pr.getTotal().add(pr.getAdditionalTax()) : new BigDecimal(0)); prService.updatePr(pr); } order++;
					 * } level++; }
					 */

				}

			}
		} catch (Exception e) {
			LOG.error("Error generating Auto PRs during Event Award : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
		return value;
		//
		// LOG.info("supplierList after--------------------" + supplierList.size());
		// for (String supplierId : supplierList) {
		// LOG.info("Supplier ID--------" + supplierId);
		// LOG.info("Tenant id---------" + loggedInUser.getTenantId());
		// FavouriteSupplier favouriteSupplier = favoriteSupplierDao.getFavouriteSupplierBySupplierId(supplierId,
		// loggedInUser.getTenantId());
		// Pr pr = prService.copyFromTemplateWithAward(templateId, createdBy, loggedInUser, loggedInUser.getTenantId(),
		// null, favouriteSupplier);
		// for (RftEventAwardDetails rfxAward : rftEventAward.getRfxAwardDetails()) {
		// LOG.info("UOM----" + rfxAward.getBqItem().getUom() + "----" + rfxAward.getAwardedPrice());
		// setProductData(rfxAward, pr, loggedInUser, supplierId);
		// }
		// }

	}

	@Override
	@Transactional(readOnly = false)
	public String createContractFromAward(RfqEventAward rfqEventAward, String eventId, String contractStartDate, String contractEndDate, String groupCodeHid, String referenceNumberHid, User loggedInUser, HttpSession session, String contractCreatorHid) throws ApplicationException {
		List<String> supplierList = new ArrayList<String>();

		Map<String, List<ProductContractItems>> map = new HashMap<>();
		String value = "";
		try {
			RfqEvent event = rfqEventDao.findByEventId(eventId);

			LOG.info("rfpEventAward.getRfxAwardDetails()--------" + rfqEventAward.getRfxAwardDetails().size());
			if (CollectionUtil.isNotEmpty(rfqEventAward.getRfxAwardDetails())) {

				for (RfqEventAwardDetails rfxAward : rfqEventAward.getRfxAwardDetails()) {
					LOG.info("order--------" + rfxAward.getBqItem().getLevel() + "--" + rfxAward.getBqItem().getOrder());
					LOG.info("---------------bqitemId--------------------" + rfxAward.getBqItem().getBqId());

					BqItem dbBqItem = rfqBqItemDao.getBqItemByBqIdAndBqItemId(rfxAward.getBqItem().getId());
					if (dbBqItem.getOrder() != 0) {
						String sid = rfxAward.getSupplier().getId();
						RfqSupplierBqItem supplierBqItem = rfqSupplierBqItemDao.getSupplierBqItemByBqItemId(rfxAward.getBqItem().getId(), sid);
						LOG.info("rfpEventAward.getRfxAwardDetails() new--------------" + rfxAward.getSupplier().getId());
						if (!supplierList.contains(sid)) {
							supplierList.add(sid);
						}

						List<ProductContractItems> supplierAwardItemList = map.get(sid);

						if (CollectionUtil.isEmpty(supplierAwardItemList)) {
							supplierAwardItemList = new ArrayList<>();
							map.put(sid, supplierAwardItemList);
						}

						ProductContractItems item = new ProductContractItems();
						ProductItem productItem = productCategoryMaintenanceService.checkProductItemExistOrNot(dbBqItem.getItemName(), sid, loggedInUser.getTenantId());
						ProductCategory category = productCategoryMaintenanceService.getProductCategoryById(rfxAward.getProductCategory());
						CostCenter costCenter = null;
						if (StringUtils.checkString(rfxAward.getCostCenter()).length() > 0) {
							costCenter = costCenterService.getCostCenterBycostId(rfxAward.getCostCenter());
						}
						BusinessUnit bu = null;
						if (StringUtils.checkString(rfxAward.getBusinessUnit()).length() > 0) {
							bu = businessUnitService.getBusinessUnitById(rfxAward.getBusinessUnit());
						}
						if (productItem == null) {

							if (category != null) {
								item.setProductCategory(category);
							}

							item.setFreeTextItemEntered(Boolean.TRUE);
							item.setItemName(dbBqItem.getItemName());
							item.setItemCode(rfxAward.getProductCode()); // UI List
							item.setItemDescription(dbBqItem.getItemDescription());
							item.setProductItemType(rfxAward.getProductType());
							item.setUom(dbBqItem.getUom());
							item.setProductItemType(rfxAward.getProductType()); // UI List
							BigDecimal ap = rfxAward.getAwardedPrice();
							item.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, RoundingMode.HALF_UP));
						} else {
							item.setProductItem(productItem);
							item.setProductItemType(rfxAward.getProductType());
							item.setProductCategory(productItem.getProductCategory());
							item.setFreeTextItemEntered(Boolean.FALSE);
							item.setItemName(productItem.getProductName());
							item.setItemCode(productItem.getProductCode());
							item.setItemDescription("");
							item.setUom(productItem.getUom());
							BigDecimal ap = rfxAward.getAwardedPrice();
							item.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, RoundingMode.HALF_UP));
						}
						item.setQuantity(supplierBqItem.getQuantity());
						item.setBalanceQuantity(item.getQuantity());
						item.setTotalAmount(item.getUnitPrice().multiply(item.getQuantity()));

						if (supplierBqItem.getTaxType() == TaxType.Amount) {
							item.setTax(supplierBqItem.getTax() != null ? supplierBqItem.getTax().multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
							item.setTax(item.getTax().divide(item.getTotalAmount(), 2, RoundingMode.HALF_UP));
							item.setTaxAmount(supplierBqItem.getTax());
						} else {
							item.setTax(supplierBqItem.getTax() != null ? supplierBqItem.getTax() : BigDecimal.ZERO);
							BigDecimal taxAmount = BigDecimal.ZERO.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP);
							if (item.getTax() != null) {
								taxAmount = item.getTax().setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP);
								taxAmount = item.getTotalAmount().multiply(taxAmount).divide(new BigDecimal(100), (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP);
								taxAmount = taxAmount.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP);
							}
							item.setTaxAmount(taxAmount);
						}
						if (item.getTax() == null) {
							item.setTax(BigDecimal.ZERO);
						}
						if (item.getTaxAmount() == null) {
							item.setTaxAmount(BigDecimal.ZERO);
						}

						item.setTotalAmountWithTax(item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0).setScale(Integer.parseInt("2"), RoundingMode.DOWN));
						item.setBrand(rfxAward.getBrand()); // UI List
						item.setBusinessUnit(bu); // UI List
						item.setCostCenter(costCenter); // UI List
						supplierAwardItemList.add(item);
					}
				}

				int j = 0;
				// Now save the PRs
				for (String sid : map.keySet()) {
					FavouriteSupplier favouriteSupplier = favoriteSupplierDao.getFavouriteSupplierBySupplierId(sid, loggedInUser.getTenantId());
					GroupCode groupCode = groupCodeService.getGroupCodeById(groupCodeHid);
					User user = userService.getUsersById(contractCreatorHid);
					TimeZone timeZone = TimeZone.getDefault();
					String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
					if (strTimeZone != null) {
						timeZone = TimeZone.getTimeZone(strTimeZone);
					}
					SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
					formatter.setTimeZone(timeZone);

					Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(contractStartDate);
					Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(contractEndDate);

					List<ProductContractReminder> productRemindersList = new ArrayList<ProductContractReminder>();
					List<ProductContractReminder> productReminders = createDefaultReminderList(productRemindersList, startDate, endDate);
					ProductContract contract = new ProductContract();
					contract.setContractId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "CTR", event.getBusinessUnit())); // generate
					contract.setEventId(event.getEventId());// ID
					if (eventIdSettingDao.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), "CTR")) {
						contract.setIdBasedOnBusinessUnit(Boolean.TRUE);
					}
					contract.setContractName(event.getEventName());
					contract.setSupplier(favouriteSupplier);
					contract.setCreatedDate(new Date());
					contract.setBusinessUnit(event.getBusinessUnit());
					contract.setProcurementCategory(event.getProcurementCategories());
					if (groupCode != null) {
						contract.setGroupCode(groupCode); // ?? // Dialog - optional
						contract.setGroupCodeStr(groupCode.getGroupCode());// ??
					}
					contract.setContractReferenceNumber(referenceNumberHid);
					contract.setContractValue(BigDecimal.ZERO);
					contract.setCurrency(event.getBaseCurrency());
					contract.setBuyer(loggedInUser.getBuyer());
					contract.setContractDocument(null);
					contract.setContractStartDate(startDate); // Dialog - required
					contract.setContractEndDate(endDate); // Dialog - required
					contract.setCreatedBy(loggedInUser);
					contract.setContractCreator(user);
					contract.setDecimal(event.getDecimal());
					List<ProductContractNotifyUsers> notifyUserList = new ArrayList<ProductContractNotifyUsers>();
					ProductContractNotifyUsers users = new ProductContractNotifyUsers();
					users.setUser(user);
					users.setProductContract(contract);
					notifyUserList.add(users);
					contract.setNotifyUsers(notifyUserList); // logged in user - copy from contract create controller
					contract.setStatus(ContractStatus.DRAFT);

					List<ProductContractItems> supplierAwardItemList = map.get(sid);
					if (CollectionUtil.isNotEmpty(supplierAwardItemList)) {
						int i = 1;
						for (ProductContractItems pci : supplierAwardItemList) {
							contract.setContractValue(contract.getContractValue().add(pci.getTotalAmountWithTax()));
							if (event.getDecimal() != null) {
								contract.setContractValue(contract.getContractValue().setScale(Integer.valueOf(event.getDecimal()), RoundingMode.HALF_UP));
							}
							pci.setContractItemNumber(String.valueOf(i));
							i++;
						}
						contract.setProductContractItem(supplierAwardItemList);
					}

					// save contract
					contract = productContractDao.save(contract);

					// Contract Audit
					try {
						ContractAudit contractAudit = new ContractAudit(contract, loggedInUser, new Date(), AuditActionType.Create, "Contract \"" + StringUtils.checkString(contract.getContractId()) + "\" created");
						contractAuditDao.save(contractAudit);
					} catch (Exception e) {
						LOG.error("Error while saving Contract Audit : " + e.getMessage(), e);
					}

					// Send Notification
					try {
						String url = APP_URL + "/buyer/productContractList";
						sendContractCreatedNotification(contract.getContractCreator(), url, contract);
					} catch (Exception e) {
						LOG.error("Error Sending Notification : " + e.getMessage(), e);
					}

					if (productReminders != null) {
						for (ProductContractReminder productReminder : productReminders) {
							ProductContractReminder contractReminder = new ProductContractReminder();
							contractReminder.setProductContract(contract);
							contractReminder.setInterval(productReminder.getInterval());
							Calendar cal = Calendar.getInstance(timeZone);
							cal.setTime(contract.getContractEndDate());
							cal.add(Calendar.DATE, -(productReminder.getInterval()));
							contractReminder.setReminderDate(cal.getTime());
							productContractReminderService.saveProductContractReminder(contractReminder);
						}
					}

					for (ProductContractItems pci : supplierAwardItemList) {
						pci.setProductContract(contract);
						productContractItemsDao.save(pci);
					}
					if (j == 0) {
						value = (StringUtils.checkString(contract.getContractId()).length() > 0 ? contract.getContractId() : "");
					} else {
						value += ", " + contract.getContractId();
					}
					j++;

				}

			}
		} catch (Exception e) {
			LOG.error("Error generating Auto PRs during Event Award : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
		return value;
	}

	@Override
	public List<DraftEventPojo> getAllExcelEventReportForBuyer(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {

		List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		getRfqEventsForExport(tenantId, eventArr, resultList, searchFilterEventPojo, select_all, input, startDate, endDate);
		return resultList;
	}

	private void getRfqEventsForExport(String tenantId, String[] eventArr, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		List<RfqEvent> rftList = rfqEventDao.getEventsByIds(tenantId, eventArr, searchFilterEventPojo, select_all, input, startDate, endDate);
		if (CollectionUtil.isNotEmpty(rftList)) {
			for (RfqEvent event : rftList) {
				DraftEventPojo eventPojo = new DraftEventPojo();
				if (event.getEventDescription() != null) {
					eventPojo.setEventDescription(event.getEventDescription());
				}
				eventPojo.setId(event.getId());
				eventPojo.setCostCenter(event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "");
				eventPojo.setInvitedSupplierCount(event.getSuppliers().size());
				eventPojo.setEventName(event.getEventName());
				eventPojo.setReferenceNumber(event.getReferanceNumber());
				eventPojo.setSysEventId(event.getEventId());
				eventPojo.setEventStart(event.getEventStart());
				eventPojo.setEventEnd(event.getEventEnd());
				eventPojo.setType(RfxTypes.RFQ);
				eventPojo.setDeliveryDate(event.getDeliveryDate());
				eventPojo.setVisibility(event.getEventVisibility());
				eventPojo.setPublishDate(event.getEventPublishDate());
				eventPojo.setValidityDays(event.getSubmissionValidityDays());
				eventPojo.setEventUser(event.getEventOwner().getName());
				if (event.getBusinessUnit() != null) {
					eventPojo.setUnitName(event.getBusinessUnit().getUnitName());
				}
				eventPojo.setStatus(event.getStatus());
				String eventCategories = "";
				for (IndustryCategory ic : event.getIndustryCategories()) {
					eventCategories += ic.getName() + ",";
				}
				if (eventCategories.length() > 0) {
					eventCategories = eventCategories.substring(0, eventCategories.length() - 1);
				}
				eventPojo.setEventCategories(eventCategories);

				RfqEventSupplier leadingSupplier = null;
				int submittedCount = 0;
				int acceptedCount = 0;
				BigDecimal leadingAmount = BigDecimal.ZERO;
				for (RfqEventSupplier es : event.getSuppliers()) {
					if (SubmissionStatusType.COMPLETED == es.getSubmissionStatus()) {
						submittedCount++;
					}
					if (es.getAcceptedBy() != null) {
						acceptedCount++;
					}
					if (Boolean.TRUE == es.getDisqualify() || Boolean.FALSE == es.getSubmitted() || SubmissionStatusType.COMPLETED != es.getSubmissionStatus())
						continue;
					boolean allOk = true;

					BigDecimal bqTotal = BigDecimal.ZERO;
					for (RfqEventBq eventBq : event.getEventBqs()) {
						// Check if any items is zero
						// int count = rfqSupplierBqItemDao.countIncompleteBqItemByBqIdsForSupplier(eventBq.getId(),
						// es.getSupplier().getId());
						// if (count > 0) {
						// allOk = false;
						// break;
						// }
						RfqSupplierBq supBq = rfqSupplierBqDao.findQualifiedSupplierBqByEventIdAndSupplierId(event.getId(), eventBq.getId(), es.getSupplier().getId());

						if (supBq == null) {
							allOk = false;
							break;
						}
						bqTotal = bqTotal.add(supBq.getTotalAfterTax());
					}
					if (!allOk)
						continue;
					if (leadingAmount.compareTo(new BigDecimal("0.0")) == 0) {
						leadingSupplier = es;
						leadingAmount = bqTotal;
						LOG.info("leading amount if " + leadingAmount);
					} else if (bqTotal.doubleValue() > leadingAmount.doubleValue()) {
						leadingSupplier = es;
						leadingAmount = bqTotal;
					}
				}

				eventPojo.setSubmittedSupplierCount(submittedCount);
				eventPojo.setLeadingAmount(leadingAmount);
				eventPojo.setAcceptedSupplierCount(acceptedCount);
				if (leadingSupplier != null && leadingSupplier.getSupplier() != null) {
					eventPojo.setLeadingSupplier(leadingSupplier.getSupplier().getCompanyName());
				}
				resultList.add(eventPojo);
			}
		}
	}

	@Override
	public JasperPrint getEvaluationReport(String eventId, String evenvelopId, String strTimeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;

		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		sdf.setTimeZone(timeZone);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		generatedSdf.setTimeZone(timeZone);
		parameters.put("generatedOn", generatedSdf.format(new Date()));
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RFXEvaluationBook.jasper");
			File jasperfile = resource.getFile();

			List<EvaluationAuctionBiddingPojo> auctionSummary = buildEvalutionReportData(eventId, sdf, parameters, evenvelopId);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auctionSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Bidding English PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private List<EvaluationAuctionBiddingPojo> buildEvalutionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			RfqEvent event = rfqEventDao.findByEventId(eventId);

			List<EventSupplier> supplierList = rfqEventSupplierDao.getAllSuppliersByEventId(eventId);
			if (event != null) {
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				buildSupplierCountData(supplierList, auction);

				RfqEnvelop envelop = rfqEnvelopService.getRfqEnvelopById(evenvelopId);
				boolean isMasked = false;
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					for (EventSupplier eventSupplier : supplierList) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
					}
					isMasked = true;
					/*
					 * Collections.sort(supplierList, new Comparator<EventSupplier>() { public int compare(EventSupplier
					 * o1, EventSupplier o2) { if (o1.getSupplier().getCompanyName() == null ||
					 * o2.getSupplier().getCompanyName() == null) { return 0; } return
					 * o1.getSupplier().getCompanyName().compareTo(o2.getSupplier().getCompanyName()); } });
					 */
				}
				auction.setIsMask(isMasked);
				buildHeadingReport(event, supplierList, auction, sdf, envelop, isMasked, true);
				auction.setEnvelopTitle(envelop != null ? envelop.getEnvelopTitle() : "NA");
				auction.setLeadEvaluater(envelop != null ? (envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "NA") : "NA");
				auction.setLeadEvaluatorSummary(envelop != null ? ((envelop.getEvaluatorSummaryDate() != null ? (sdf.format(envelop.getEvaluatorSummaryDate()) + ":") : "") + (envelop.getLeadEvaluatorSummary() != null ? envelop.getLeadEvaluatorSummary() : "")) : "N/A");
				auction.setFileName(envelop != null ? envelop.getFileName() : "N/A");
				if (envelop != null) {
					buildEvaluatorsSummary(envelop.getEvaluators(), auction);
					if (event.getEnableEvaluationDeclaration()) {
						buildEvaluatorDeclarationAcceptData(sdf, envelop.getId(), eventId, auction);
					}
				}
				buildEventDetailData(sdf, event, auction);
				buildBuyerAttachementData(event.getId(), auction, sdf);
				buildMatchingIpAddressData(eventId, auction, supplierList, envelop);
				buildSupplierLineChartAndData(sdf, event, supplierList, auction, envelop, isMasked);
				List<String> sortedSupplierList = new ArrayList<>();
				List<String> top3Supplier = new ArrayList<>();

				buildEnvoleBQData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);
				buildEnvoleSORData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);
				buildEnvelopeCQData(event, auction, sdf, envelop, isMasked);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);

		} catch (Exception e) {
			LOG.error("Could not get build EvalutionReport Data : " + e.getMessage(), e);
		}
		return auctionSummary;
	}

	private void buildEvaluatorDeclarationAcceptData(SimpleDateFormat sdf, String envelopId, String eventId, EvaluationAuctionBiddingPojo auction) {
		List<RfqEvaluatorDeclaration> evalutorDeclarationList = rfqEvaluatorDeclarationDao.getAllEvaluatorDeclarationByEnvelopAndEventId(envelopId, eventId);
		List<EvaluationDeclarationAcceptancePojo> evaluationDeclarationList = new ArrayList<EvaluationDeclarationAcceptancePojo>();
		if (CollectionUtil.isNotEmpty(evalutorDeclarationList)) {
			for (RfqEvaluatorDeclaration rfqEvaluatorDeclaration : evalutorDeclarationList) {
				EvaluationDeclarationAcceptancePojo evaluationDeclarationPojo = new EvaluationDeclarationAcceptancePojo();
				evaluationDeclarationPojo.setEvaluationCommittee(Boolean.TRUE == rfqEvaluatorDeclaration.getIsLeadEvaluator() ? "Evaluation Owner" : "Evaluation Team");
				evaluationDeclarationPojo.setAcceptedDate(sdf.format(rfqEvaluatorDeclaration.getAcceptedDate()));
				if (rfqEvaluatorDeclaration.getUser() != null) {
					evaluationDeclarationPojo.setUsername(rfqEvaluatorDeclaration.getUser().getName());
					evaluationDeclarationPojo.setUserLoginEmail(rfqEvaluatorDeclaration.getUser().getLoginId());
				}
				evaluationDeclarationList.add(evaluationDeclarationPojo);
			}
		}
		auction.setEvaluationDeclarationAcceptList(evaluationDeclarationList);
	}

	private List<EventSupplier> buildSupplierCountData(List<EventSupplier> supplierList, EvaluationAuctionBiddingPojo auction) {
		List<EventSupplier> participatedSupplier = new ArrayList<EventSupplier>();
		try {
			int supplierCount = 0, submittedCnt = 0, totalBids = 0;

			if (CollectionUtil.isNotEmpty(supplierList)) {
				for (EventSupplier suppItem : supplierList) {
					if (suppItem.getSubmitted() != null && suppItem.getSubmitted()) {
						submittedCnt++;
						participatedSupplier.add(suppItem);
					}
					if (suppItem.getNumberOfBids() != null) {
						totalBids += suppItem.getNumberOfBids();
					}
					supplierCount++;
				}
			}
			auction.setSupplierInvited(supplierCount);
			auction.setSupplierParticipated(submittedCnt);
			auction.setTotalBilds(totalBids);

		} catch (Exception e) {
			LOG.error("Could not get build Supplier Count Data : " + e.getMessage(), e);
		}
		return participatedSupplier;
	}

	private void buildHeadingReport(RfqEvent event, List<EventSupplier> supplierList, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfqEnvelop envelop, boolean isMasked, boolean isEvaluation) {
		try {
			List<EvaluationAuctionBiddingPojo> headerBqList = new ArrayList<EvaluationAuctionBiddingPojo>();
			LOG.info("envelop" + envelop.getId());

			List<RfqEventBq> bq = rfqEventBqDao.findBqbyyEventIdAndEnvelopeId(event.getId(), envelop.getId());
			EvaluationAuctionBiddingPojo headerPojo = new EvaluationAuctionBiddingPojo();
			if (CollectionUtil.isNotEmpty(bq)) {
				for (RfqEventBq rfaEventBq : bq) {
					headerPojo = new EvaluationAuctionBiddingPojo();
					List<RfaSupplierBqPojo> rfaEventSupplierIds = rfqSupplierBqDao.getAllRfqTopCompletedEventSuppliersIdByEventId(event.getId(), 5, rfaEventBq.getId());
					headerPojo.setHeaderBqName(rfaEventBq.getName());
					RfaSupplierBqPojo leadSupplier = null;
					for (RfaSupplierBqPojo rfaSupplierBqPojo : rfaEventSupplierIds) {
						if (rfaSupplierBqPojo.getCompleteness() == rfaSupplierBqPojo.getTotalItem()) {
							leadSupplier = rfaSupplierBqPojo;
							break;
						}
					}

					buildLeadSupplierData(event, headerPojo, leadSupplier, rfaEventBq, isMasked, envelop);
					buildTopCompletedBarChartData(event, headerPojo, rfaEventSupplierIds, envelop, isMasked);
					headerPojo.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
					buildDisqualificationSuppliersData(event.getId(), headerPojo, isMasked, envelop, sdf, isEvaluation);
					buildMatchingIpAddressData(event.getId(), headerPojo, supplierList, envelop);
					headerBqList.add(headerPojo);
				}
			} else {

				headerPojo.setHeaderBqName("");
				buildDisqualificationSuppliersData(event.getId(), headerPojo, isMasked, envelop, sdf, isEvaluation);
				buildMatchingIpAddressData(event.getId(), headerPojo, supplierList, envelop);
				headerBqList.add(headerPojo);
			}
			auction.setHeader(headerBqList);
		} catch (Exception e) {
			LOG.error("Could not get build Heading Report : " + e.getMessage(), e);
		}
	}

	private void buildLeadSupplierData(RfqEvent event, EvaluationAuctionBiddingPojo auction, RfaSupplierBqPojo leadingbid, RfqEventBq rfaEventBq, boolean isMasked, RfqEnvelop envelop) {
		try {
			if (leadingbid != null) {
				BigDecimal savingVsBudget = BigDecimal.ZERO, percentageVsBudget = BigDecimal.ZERO;
				BigDecimal savingVsHistoric = BigDecimal.ZERO, percentageVsHistoric = BigDecimal.ZERO;
				if (event.getBudgetAmount() != null && event.getBudgetAmount().compareTo(BigDecimal.ZERO) != 0) {
					savingVsBudget = event.getBudgetAmount().subtract(leadingbid.getTotalAfterTax() != null ? leadingbid.getTotalAfterTax() : BigDecimal.ZERO);
					percentageVsBudget = (savingVsBudget.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(event.getBudgetAmount(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
					auction.setRemarkComparerBudgetPrice((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + formatedDecimalNumber(event.getDecimal(), savingVsBudget) + " or " + formatedDecimalNumber(event.getDecimal(), percentageVsBudget) + "% vs budgeted price");
				}
				if (event.getHistoricaAmount() != null && event.getHistoricaAmount().compareTo(BigDecimal.ZERO) != 0) {
					savingVsHistoric = event.getHistoricaAmount().subtract(leadingbid.getTotalAfterTax() != null ? leadingbid.getTotalAfterTax() : BigDecimal.ZERO);
					percentageVsHistoric = (savingVsHistoric.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(event.getHistoricaAmount(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
					auction.setRemarkCompareHistoricPrice((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + formatedDecimalNumber(event.getDecimal(), savingVsHistoric) + " or " + formatedDecimalNumber(event.getDecimal(), percentageVsHistoric) + "% vs historic price");
				}
				auction.setSupplierCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), leadingbid.getSupplierId(), envelop.getId())) : leadingbid.getSupplierCompanyName());
				auction.setLeadSuppliergrandTotal((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + formatedDecimalNumber(event.getDecimal(), leadingbid.getTotalAfterTax()));
				auction.setRevisedGrandTotal(leadingbid.getTotalAfterTax());
				auction.setBqName(leadingbid.getBqName());
			}

			auction.setSupplier(leadingbid);
		} catch (Exception e) {
			LOG.error("Could not get Supplier leading bid Details" + e.getMessage(), e);
		}

	}

	private void buildTopCompletedBarChartData(RfqEvent event, EvaluationAuctionBiddingPojo auction, List<RfaSupplierBqPojo> eventBq, RfqEnvelop envelop, boolean isMasked) {
		List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
		try {
			if (CollectionUtil.isNotEmpty(eventBq)) {
				for (RfaSupplierBqPojo item : eventBq) {
					EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
					suppBidprice.setPostAuctionprice(item.getTotalAfterTax() != null ? item.getTotalAfterTax() : item.getInitialPrice());
					suppBidprice.setBidderName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId())) : item.getSupplierCompanyName() + "(" + item.getCompleteness() + "/" + item.getTotalItem() + ")");
					suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
					suppBidprice.setDecimal(event.getDecimal());
					suppBidprice.setPostAuctionprice(item.getTotalAfterTax());
					suppBidprice.setPostAuctionStrPrice(coolFormat(item.getTotalAfterTax().doubleValue(), 0));
					bidPriceList.add(suppBidprice);
				}
			}
		} catch (Exception e) {
			LOG.error("Could not get Supplier Bidding Price Details" + e.getMessage(), e);
		}

		auction.setBiddingPrice(bidPriceList);
	}

	private final char[] c = new char[] { 'K', 'M', 'B', 'T' };

	private String coolFormat(double n, int iteration) {
		if ((long) n >= 1000) {
			double d = ((long) n / 100) / 10.0;
			boolean isRound = (d * 10) % 10 == 0;// true if the decimal part is
													// equal to 0 (then it's
													// trimmed anyway)
			return (d < 1000 ? // this determines the class, i.e. 'k', 'm' etc
					((d > 99.9 || isRound || (!isRound && d > 9.99) ? // this
																		// decides
																		// whether
																		// to
																		// trim
																		// the
																		// decimals
							(int) d * 10 / 10 : d + "" // (int) d * 10 / 10
														// drops the decimal
					) + "" + c[iteration]) : coolFormat(d, iteration + 1));

		} else {
			return String.valueOf(n);
		}
	}

	private void buildDisqualificationSuppliersData(String eventId, EvaluationAuctionBiddingPojo auction, boolean isMasked, RfqEnvelop envelop, SimpleDateFormat sdf, boolean isEvaluation) {
		try {
			List<RfqEventSupplier> SupplierList = null;
			if (isEvaluation) {
				SupplierList = rfqEventSupplierService.findDisqualifySupplierForEvaluationReportByEventId(eventId);
			} else {
				SupplierList = rfqEventSupplierService.findDisqualifySupplierByEventId(eventId);
			}
			List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
			if (CollectionUtils.isNotEmpty(SupplierList)) {
				if (SupplierList.size() == 1) {
					for (RfqEventSupplier supplier : SupplierList) {
						EvaluationSuppliersPojo suppliers = new EvaluationSuppliersPojo();
						suppliers.setIsQualify(supplier.getDisqualify());
						if (StringUtils.checkString(supplier.getRejectionRemarks()).length() > 0 && !isEvaluation) {
							suppliers.setRemark(supplier.getRejectionRemarks());
						} else {
							suppliers.setRemark(StringUtils.checkString(supplier.getDisqualifyRemarks()));
						}
						suppliers.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), supplier.getSupplier().getId(), envelop.getId())) : supplier.getSupplierCompanyName());
						suppliers.setDisqualifiedTime(supplier.getDisqualifiedTime() != null ? sdf.format(supplier.getDisqualifiedTime()) : null);
						suppliers.setDisqualifiedBy(supplier.getDisqualifiedBy() != null ? supplier.getDisqualifiedBy().getName() : null);
						suppliers.setDisqualifiedEnvelope(supplier.getDisqualifiedEnvelope() != null && StringUtils.checkString(supplier.getDisqualifiedEnvelope().getEnvelopTitle()).length() > 0 ? supplier.getDisqualifiedEnvelope().getEnvelopTitle() : null);
						allSuppliers.add(suppliers);
					}
					auction.setShowSingleDisQaulify("true");
				} else {
					for (int j = 0; j < SupplierList.size(); j++) {
						/*
						 * if (SupplierList.size() == 1) { EvaluationSuppliersPojo e = new EvaluationSuppliersPojo();
						 * e.setSupplierName(SupplierList.get(j).getSupplierCompanyName());
						 * e.setRemark(SupplierList.get(j).getDisqualifyRemarks()); allSuppliers.add(e); break; }
						 */
						EvaluationSuppliersPojo e = new EvaluationSuppliersPojo();
						e.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), SupplierList.get(j).getSupplier().getId(), envelop.getId())) : SupplierList.get(j).getSupplierCompanyName());
						if (StringUtils.checkString(SupplierList.get(j).getRejectionRemarks()).length() > 0 && !isEvaluation) {
							e.setRemark(SupplierList.get(j).getRejectionRemarks());
						} else {
							e.setRemark(StringUtils.checkString(SupplierList.get(j).getDisqualifyRemarks()));
						}
						e.setDisqualifiedTime(SupplierList.get(j).getDisqualifiedTime() != null ? sdf.format(SupplierList.get(j).getDisqualifiedTime()) : null);
						e.setDisqualifiedBy(SupplierList.get(j).getDisqualifiedBy() != null ? SupplierList.get(j).getDisqualifiedBy().getName() : null);
						e.setDisqualifiedEnvelope(SupplierList.get(j).getDisqualifiedEnvelope() != null && StringUtils.checkString(SupplierList.get(j).getDisqualifiedEnvelope().getEnvelopTitle()).length() > 0 ? SupplierList.get(j).getDisqualifiedEnvelope().getEnvelopTitle() : null);
						e.setIsQualify(SupplierList.get(j).getDisqualify());
						if (j % 2 == 0) {
							if (SupplierList.size() > j + 1) {
								e.setSupplierName2(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), SupplierList.get(j + 1).getSupplier().getId(), envelop.getId())) : SupplierList.get(j + 1).getSupplierCompanyName());
								if (StringUtils.checkString(SupplierList.get(j + 1).getRejectionRemarks()).length() > 0 && !isEvaluation) {
									e.setRemark2(SupplierList.get(j + 1).getRejectionRemarks());
								} else {
									e.setRemark2(StringUtils.checkString(SupplierList.get(j + 1).getDisqualifyRemarks()));
								}
								e.setDisqualifiedTime2(SupplierList.get(j + 1).getDisqualifiedTime() != null ? sdf.format(SupplierList.get(j + 1).getDisqualifiedTime()) : null);
								e.setDisqualifiedBy2(SupplierList.get(j + 1).getDisqualifiedBy() != null ? SupplierList.get(j + 1).getDisqualifiedBy().getName() : null);
								e.setDisqualifiedEnvelope2(SupplierList.get(j + 1).getDisqualifiedEnvelope() != null && StringUtils.checkString(SupplierList.get(j + 1).getDisqualifiedEnvelope().getEnvelopTitle()).length() > 0 ? SupplierList.get(j + 1).getDisqualifiedEnvelope().getEnvelopTitle() : null);
								e.setIsQualify2(SupplierList.get(j + 1).getDisqualify());
							}
						} else {
							continue;
						}
						allSuppliers.add(e);
					}
					auction.setShowSingleDisQaulify("false");
				}
			}
			auction.setDisQualifiedSuppliers(allSuppliers);
		} catch (Exception e) {
			LOG.error("Could not get build Disqualification Suppliers Data : " + e.getMessage(), e);
		}
	}

	private void buildMatchingIpAddressData(String eventId, EvaluationAuctionBiddingPojo auction, List<EventSupplier> participatedSupplier, RfqEnvelop envelop) {
		try {
			List<EvaluationBiddingIpAddressPojo> ipAddressList = new ArrayList<EvaluationBiddingIpAddressPojo>();
			Map<String, String> hm = new HashMap<String, String>();
			int i = 0;
			if (CollectionUtil.isNotEmpty(participatedSupplier)) {
				for (EventSupplier rfqSupplier : participatedSupplier) {
					if (StringUtils.checkString(rfqSupplier.getIpAddress()).length() > 0) {
						if (hm.containsKey(rfqSupplier.getIpAddress())) {
							if (StringUtils.checkString(hm.get(rfqSupplier.getIpAddress())).length() > 0) {
								hm.put(rfqSupplier.getIpAddress(), hm.get(rfqSupplier.getIpAddress()) + " & " + rfqSupplier.getSupplierCompanyName());
							} else {
								hm.put(rfqSupplier.getIpAddress(), rfqSupplier.getSupplierCompanyName());
							}
							i++;
						} else {
							int flag = 0;
							for (EventSupplier rfqSupplierIp : participatedSupplier) {
								if (StringUtils.checkString(rfqSupplierIp.getIpAddress()).length() > 0) {
									if (rfqSupplierIp.getIpAddress().equals(rfqSupplier.getIpAddress())) {
										flag++;
									}
								}
							}
							if (flag > 1) {
								hm.put(rfqSupplier.getIpAddress(), rfqSupplier.getSupplierCompanyName());
							}

						}
					}
				}
			}
			if (i >= 1) {
				for (Map.Entry<String, String> entry : hm.entrySet()) {
					EvaluationBiddingIpAddressPojo ipNumber = new EvaluationBiddingIpAddressPojo();
					ipNumber.setIpAddress(entry.getKey());
					ipNumber.setSupplierCompanyName(entry.getValue());
					ipAddressList.add(ipNumber);
				}
			}
			List<EvaluationBiddingIpAddressPojo> EvaluationBiddingIpAddressPojo1 = new ArrayList<EvaluationBiddingIpAddressPojo>();
			if (CollectionUtils.isNotEmpty(ipAddressList)) {
				if (ipAddressList.size() == 1) {
					for (EvaluationBiddingIpAddressPojo evaluationBiddingIpAddressPojo : ipAddressList) {
						EvaluationBiddingIpAddressPojo suppliers = new EvaluationBiddingIpAddressPojo();
						suppliers.setIpAddress(evaluationBiddingIpAddressPojo.getIpAddress());
						suppliers.setSupplierCompanyName(evaluationBiddingIpAddressPojo.getSupplierCompanyName());
						EvaluationBiddingIpAddressPojo1.add(suppliers);
						auction.setShowSingleIpAdd("true");
					}

				} else {
					for (int j = 0; j < ipAddressList.size(); j++) {
						EvaluationBiddingIpAddressPojo e = new EvaluationBiddingIpAddressPojo();
						e.setSupplierCompanyName(ipAddressList.get(j).getSupplierCompanyName());
						e.setIpAddress(ipAddressList.get(j).getIpAddress());
						if (j % 2 == 0) {
							if (ipAddressList.size() > j + 1) {
								e.setSupplierCompanyName1(ipAddressList.get(j + 1).getSupplierCompanyName());
								e.setIpAddress1(ipAddressList.get(j + 1).getIpAddress());
							}
						} else {
							continue;
						}
						EvaluationBiddingIpAddressPojo1.add(e);

					}
					auction.setShowSingleIpAdd("false");
					// }

				}
			}
			auction.setIpAddressList(EvaluationBiddingIpAddressPojo1);
		} catch (Exception e) {
			LOG.error("Could not get build Matching IpAddress Data : " + e.getMessage(), e);
		}

	}

	private void buildEvaluatorsSummary(List<RfqEvaluatorUser> evaluators, EvaluationAuctionBiddingPojo auction) {
		try {
			List<EvaluationBqItemComments> evaluationSummary = new ArrayList<EvaluationBqItemComments>();
			for (RfqEvaluatorUser rfaEvaluatorUser : evaluators) {
				EvaluationBqItemComments comments = new EvaluationBqItemComments();
				comments.setCommentBy(rfaEvaluatorUser.getUser() != null ? rfaEvaluatorUser.getUser().getName() : "N/A");
				comments.setComments(StringUtils.checkString(rfaEvaluatorUser.getEvaluatorSummary()));
				comments.setFileName(rfaEvaluatorUser.getFileName() != null ? rfaEvaluatorUser.getFileName() : "N/A");
				evaluationSummary.add(comments);
			}
			auction.setEvaluationSummary(evaluationSummary);
		} catch (Exception e) {
			LOG.error("Could not get build Evaluators Summary : " + e.getMessage(), e);
		}
	}

	private void buildEventDetailData(SimpleDateFormat sdf, RfqEvent event, EvaluationAuctionBiddingPojo auction) {
		try {
			String auctionDate = event.getEventStart() != null ? sdf.format(event.getEventStart()) : "-" + (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			String auctionTitle = null;
			if (event.getBaseCurrency() != null && event.getBaseCurrency().getCurrencyCode() != null) {
				auctionTitle = "PRE & POST AUCTION PRICE (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + ")";

			}
			String netSavingTitle = "Saving based on Budged(%)";
			auction.setAuctionId(event.getEventId());
			auction.setReferenceNo(event.getReferanceNumber());
			auction.setEventDescription(StringUtils.checkString(event.getEventDescription()));
			auction.setAuctionName(event.getEventName());
			auction.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
			auction.setDateTime(auctionDate);
			auction.setAuctionTitle(auctionTitle);
			auction.setNetSavingTitle(netSavingTitle);
			auction.setEventType(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			auction.setOwnerWithContact(event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + " Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");

			// here for RFX submission & evaluation report used
			auction.setAuctionType(RfxTypes.RFQ.getValue());
			auction.setBuyerName(event.getEventOwner().getBuyer().getCompanyName());
			auction.setAuctionCreatorDate(event.getCreatedDate() != null ? sdf.format(event.getCreatedDate()) : "");
			auction.setAuctionPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			auction.setAuctionStartDate(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			auction.setAuctionEndDate(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			auction.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
			auction.setAuctionStatus(event.getStatus().name());
			auction.setIsBuyer(Boolean.TRUE);
			auction.setDecimal(event.getDecimal());
		} catch (Exception e) {
			LOG.error("Could not get build Event Detail Data : " + e.getMessage(), e);
		}
	}

	private void buildBuyerAttachementData(String eventId, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf) {
		try {
			List<AuctionEvaluationDocumentPojo> documentList = new ArrayList<AuctionEvaluationDocumentPojo>();
			List<RfqEventDocument> documents = rfqDocumentDao.findAllRfqEventdocsbyEventId(eventId);
			if (CollectionUtil.isNotEmpty(documents)) {
				for (RfqEventDocument docs : documents) {
					AuctionEvaluationDocumentPojo item = new AuctionEvaluationDocumentPojo();
					item.setDescription(docs.getDescription());
					item.setFileName(docs.getFileName());
					item.setUploadDate(sdf.format(docs.getUploadDate()));
					item.setSize(docs.getFileSizeInKb() != null ? (double) docs.getFileSizeInKb() : 0);
					documentList.add(item);
				}
			}
			auction.setAuctionEvaluationDocument(documentList);
		} catch (Exception e) {
			LOG.error("Could not get build Buyer Attachement Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierLineChartAndData(SimpleDateFormat sdf, RfqEvent event, List<EventSupplier> participatedSupplier, EvaluationAuctionBiddingPojo auction, RfqEnvelop envelop, boolean isMasked) {
		try {
			List<EvaluationSupplierBidsPojo> supplierBidHistory = new ArrayList<EvaluationSupplierBidsPojo>();
			List<EvaluationBidderContactPojo> bidderContactList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderAcceptedList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderRejectedList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderInvidedList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> supplierActivitySummaryList = new ArrayList<EvaluationBidderContactPojo>();
			if (CollectionUtil.isNotEmpty(participatedSupplier)) {
				for (EventSupplier eventSupp : participatedSupplier) {
					if (!isMasked) {
						buildSupplierContactListData(bidderContactList, eventSupp);
					} else {
						eventSupp.setSupplierCompanyName((MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())));
					}
					if (eventSupp.getAcceptedBy() != null) {
						buildSupplierAcceptedListData(event, eventSupp, bidderAcceptedList, sdf, isMasked, envelop);
					}
					if (SubmissionStatusType.REJECTED == eventSupp.getSubmissionStatus()) {
						buildSupplierRejectedListData(event, eventSupp, bidderRejectedList, sdf, isMasked, envelop);
					}
					if (SubmissionStatusType.INVITED == eventSupp.getSubmissionStatus()) {
						buildSupplierInvidedListData(event, eventSupp, bidderInvidedList, sdf, isMasked, envelop);
					}

				}
			}
			List<RfaSupplierBqPojo> eventBq = rfqSupplierBqDao.findRfqSupplierParticipation(event.getId());
			for (RfaSupplierBqPojo eventSupp : eventBq) {
				if (isMasked) {
					eventSupp.setSupplierCompanyName((MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplierId(), envelop.getId())));
				}
				buildSupplierActivitySummaryData(event, eventSupp, supplierActivitySummaryList, sdf);
			}
			buildSupplierTotallyCompleteBidsData(auction, event, sdf, isMasked, envelop);
			buildSupplierPartiallyCompleteBidsData(auction, event, isMasked, envelop);
			auction.setSupplierBidsList(supplierBidHistory);
			auction.setBidContacts(bidderContactList);
			auction.setSupplierActivitySummary(supplierActivitySummaryList);
			auction.setSupplierAcceptedBids(bidderAcceptedList);
			auction.setSupplierRejectedBids(bidderRejectedList);
			auction.setSupplierInvitedBids(bidderInvidedList);
		} catch (

		Exception e) {
			LOG.error("Could not get build Supplier Line Chart And Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierContactListData(List<EvaluationBidderContactPojo> bidderContact, EventSupplier eventSupp) {
		try {
			EvaluationBidderContactPojo supplierContact = new EvaluationBidderContactPojo();
			supplierContact.setCompanyName(eventSupp.getSupplierCompanyName());
			if (eventSupp.getSupplier() != null) {
				supplierContact.setContactName(eventSupp.getSupplier().getFullName());
				supplierContact.setPhno(eventSupp.getSupplier().getCompanyContactNumber());
				supplierContact.setMobileNo(eventSupp.getSupplier().getMobileNumber());
				supplierContact.setEmail(eventSupp.getSupplier().getCommunicationEmail());
				supplierContact.setDesignation(eventSupp.getSupplier().getDesignation());
				supplierContact.setStatus(eventSupp.getSubmissionStatus() != null ? eventSupp.getSubmissionStatus().toString() : "N/A");
			}
			bidderContact.add(supplierContact);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Contact List Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierActivitySummaryData(RfqEvent event, RfaSupplierBqPojo eventSupp, List<EvaluationBidderContactPojo> supplierActivitySummaryList, SimpleDateFormat sdf) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(eventSupp.getSupplierCompanyName());
			String submissionRemark = "";

			if (SubmissionStatusType.REJECTED == eventSupp.getSubmissionStatus()) {
				submissionRemark = "Rejected \n " + (eventSupp.getRejectedTime() != null ? sdf.format(eventSupp.getRejectedTime()) : "N/A");
			} else {
				submissionRemark = "Accepted \n " + (eventSupp.getSupplierEventReadTime() != null ? sdf.format(eventSupp.getSupplierEventReadTime()) : "N/A");
			}

			if (eventSupp.getIsDisqualify()) {
				supplierActivitySummary.setStatus("Disqualify");
			} else {
				supplierActivitySummary.setStatus("Qualify");
			}

			String completeness = Long.toString(eventSupp.getCompleteness());
			String totalItem = Long.toString(eventSupp.getTotalItem());
			supplierActivitySummary.setTotalAfterTax(eventSupp.getTotalAfterTax());
			supplierActivitySummary.setCompleAndTotalItem(completeness + " / " + totalItem);
			supplierActivitySummary.setNumberOfBids(eventSupp.getNumberOfBids());
			supplierActivitySummary.setActionDate((eventSupp.getSubmissionTime() != null ? sdf.format(eventSupp.getSubmissionTime()) : "N/A"));
			supplierActivitySummary.setActionStatus(submissionRemark);
			supplierActivitySummary.setIpnumber(eventSupp.getIpAddress());

			supplierActivitySummaryList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Activity Summary Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierAcceptedListData(RfqEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderAcceptedList, SimpleDateFormat sdf, boolean isMasked, RfqEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			supplierActivitySummary.setActionDate(eventSupp.getSupplierEventReadTime() != null ? sdf.format(eventSupp.getSupplierEventReadTime()) : "N/A");
			bidderAcceptedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Accepted List Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierRejectedListData(RfqEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderRejectedList, SimpleDateFormat sdf, boolean isMasked, RfqEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			supplierActivitySummary.setActionDate(eventSupp.getRejectedTime() != null ? sdf.format(eventSupp.getRejectedTime()) : "N/A");
			supplierActivitySummary.setRemark(StringUtils.checkString(eventSupp.getDisqualifyRemarks()).length() > 0 ? eventSupp.getDisqualifyRemarks() : "N/A");
			bidderRejectedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Rejected List Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierInvidedListData(RfqEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderInvidedList, SimpleDateFormat sdf, boolean isMasked, RfqEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			bidderInvidedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Invided List Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierTotallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfqEvent event, SimpleDateFormat sdf, boolean isMasked, RfqEnvelop envelop) {

		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfqSupplierBqDao.findRfqSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<String> bqids = rfqEnvelopDao.getBqsByEnvelopId(envelopId);

			if (CollectionUtil.isNotEmpty(bqids)) {

				if (CollectionUtil.isNotEmpty(eventBq)) {
					for (RfaSupplierBqPojo item : eventBq) {
						if (item.getCompleteness() == item.getTotalItem()) {
							EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
							suppBidprice.setPostAuctionprice(item.getTotalAfterTax());
							suppBidprice.setBidderName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId())) : item.getSupplierCompanyName());
							suppBidprice.setSubmitedDate(sdf.format(item.getSubmissionTime()));
							bidPriceList.add(suppBidprice);
						}
					}
				}
			}
			auction.setBidderTotallyCompleteBidsList(bidPriceList);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Totally Complete Bids Data : " + e.getMessage(), e);
		}

	}

	private void buildSupplierPartiallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfqEvent event, boolean isMasked, RfqEnvelop envelop) {
		LOG.info("isMasked" + isMasked);
		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfqSupplierBqDao.findRfqSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<String> bqids = rfqEnvelopDao.getBqsByEnvelopId(envelopId);

			if (CollectionUtil.isNotEmpty(bqids)) {
				if (CollectionUtil.isNotEmpty(eventBq)) {
					for (RfaSupplierBqPojo item : eventBq) {
						if (item.getCompleteness() != item.getTotalItem()) {
							EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();

							suppBidprice.setBidderName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId())) : item.getSupplierCompanyName());
							suppBidprice.setPostAuctionprice(item.getTotalAfterTax());

							String completeness = Long.toString(item.getCompleteness());
							String totalItem = Long.toString(item.getTotalItem());

							suppBidprice.setCompleAndTotalItem(completeness + " / " + totalItem);

							bidPriceList.add(suppBidprice);
						}
					}
				}
			}

			auction.setBidderPartiallyCompleteBidsList(bidPriceList);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Partially Complete Bids Data : " + e.getMessage(), e);
		}

	}

	private void buildEnvoleBQData(RfqEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfqEnvelop envelop, boolean isMasked,
								   List<String> sortedSupplierList, List<String> top3Supplier) {
		try {
			List<EnvelopeBqPojo> envopleBqPojos = new ArrayList<EnvelopeBqPojo>();
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			// List<String> bqids = rfqEnvelopDao.getBqsByEnvelopId(envelopId);
			List<BqPojo> bqids = rfqEnvelopDao.getBqsIdListByEnvelopIdByOrder(envelopId);
			for (BqPojo bqId : bqids) {
				// for (EvaluationSuppliersBqPojo bq : bqs) {

				EnvelopeBqPojo bqPojo = new EnvelopeBqPojo();
				buildEnvlopeTopSupplierComparisionforEvaluationSummary(bqId.getId(), event, bqPojo, isMasked, envelop, top3Supplier);

				buildEnvlopeSupplierBqforEvaluationSummary(bqId.getId(), event, bqPojo, envelop, isMasked, sortedSupplierList);
				envopleBqPojos.add(bqPojo);
			}
			auction.setEnvelopeBq(envopleBqPojos);
		} catch (Exception e) {
			LOG.error("Could not get build EnvoleBQ Data : " + e.getMessage(), e);
		}
	}


	private void buildEnvoleSORData(RfqEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfqEnvelop envelop, boolean isMasked,
									List<String> sortedSupplierList, List<String> top3Supplier) {
		try {
			List<EnvelopeSorPojo> envopleSorPojos = new ArrayList<EnvelopeSorPojo>();
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<SorPojo> sorIds = rfqEnvelopDao.getSorsIdListByEnvelopIdByOrder(envelopId);

			for (SorPojo bqId : sorIds) {
				EnvelopeSorPojo bqPojo = new EnvelopeSorPojo();
				buildEnvlopeSORTopSupplierComparisionforEvaluationSummary(bqId.getId(), event, bqPojo, isMasked, envelop, top3Supplier);

				buildEnvlopeSupplierSORorEvaluationSummary(bqId.getId(), event, bqPojo, envelop, isMasked, sortedSupplierList);
				envopleSorPojos.add(bqPojo);
			}
			auction.setEnvelopeSor(envopleSorPojos);
		} catch (Exception e) {
			LOG.error("Could not get build EnvoleSOR Data : " + e.getMessage(), e);
		}
	}

	private void buildEnvlopeSupplierBqforEvaluationSummary(String bqId, RfqEvent event, EnvelopeBqPojo bqPojo, RfqEnvelop envelop, boolean isMasked, List<String> sortedSupplierList) {
		try {
			List<EvaluationSuppliersBqPojo> bqs = rfqSupplierBqDao.getAllBqsByBqIdsAndEventId(bqId, event.getId());
			List<EvaluationSuppliersBqPojo> supplierBq = new ArrayList<EvaluationSuppliersBqPojo>();
			if (CollectionUtils.isNotEmpty(bqs)) {
				for (EvaluationSuppliersBqPojo bq : bqs) {
					EvaluationSuppliersBqPojo bqSupplierPojo = new EvaluationSuppliersBqPojo();
					sortedSupplierList.add(bq.getSupplierName());
					bqPojo.setBqName(bq.getBqName());
					LOG.info("Bq name in evaluation:" + bq.getBqName());
					bqSupplierPojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), bq.getId(), envelop.getId())) : bq.getSupplierName());

					List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
					if (bq != null) {
						EvaluationBqPojo bqItem = new EvaluationBqPojo();
						bqSupplierPojo.setGrandTotal(bq.getGrandTotal());
						bqSupplierPojo.setTotalAfterTax(bq.getTotalAfterTax());
						bqSupplierPojo.setAdditionalTax(bq.getAdditionalTax());
						bqSupplierPojo.setRemark(bq.getRemark());
						bqSupplierPojo.setBqName(bq.getBqName());

						bqItem.setName(bq.getBqName());
						List<RfqSupplierBqItem> suppBqItems = rfqSupplierBqItemService.getAllSupplierBqItemForReportByBqIdAndSupplierId(bq.getBqId(), bq.getId());
						List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
						if (CollectionUtil.isNotEmpty(suppBqItems)) {
							for (RfqSupplierBqItem suppBqItem : suppBqItems) {
								EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setQuantity(suppBqItem.getQuantity());
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									for (RfqSupplierBqItem childBqItem : suppBqItem.getChildren()) {
										EvaluationBqItemPojo evlBqChilItem = new EvaluationBqItemPojo();
										evlBqChilItem.setDescription(childBqItem.getItemName());
										evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
										evlBqChilItem.setQuantity(childBqItem.getQuantity());
										evlBqChilItem.setUom(childBqItem.getUom().getUom());
										evlBqChilItem.setTaxAmt(childBqItem.getTax() != null ? childBqItem.getTax() : BigDecimal.ZERO);
										evlBqChilItem.setUnitPrice(childBqItem.getUnitPrice());
										evlBqChilItem.setTotalAmt(childBqItem.getTotalAmountWithTax());
										evlBqChilItem.setAmount(childBqItem.getTotalAmount());
										evlBqChilItem.setDecimal(event != null ? event.getDecimal() : "");
										// Review Comments
										List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
										List<RfqBqEvaluationComments> bqItemComments = rfqBqEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getBqItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RfqBqEvaluationComments review : bqItemComments) {
												EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
												bqComment.setCommentBy(review.getUserName());
												bqComment.setComments(review.getComment());
												comments.add(bqComment);
												reviews += "[ " + review.getUserName() + " ] " + review.getComment() + "\n";
											}
											evlBqChilItem.setReviews(reviews);
											evlBqChilItem.setReview(comments);
										}

										evlBqItems.add(evlBqChilItem);
									}
								}
							}
						}
						bqItem.setBqItems(evlBqItems);

						allBqs.add(bqItem);
					}
					bqSupplierPojo.setBqs(allBqs);
					supplierBq.add(bqSupplierPojo);
				}
			}
			bqPojo.setSupplierBq(supplierBq);
		} catch (Exception e) {
			LOG.error("Could not get build Envlope SupplierBq for EvaluationSummary : " + e.getMessage(), e);
		}
	}


	private void buildEnvlopeSupplierSORorEvaluationSummary(String bqId, RfqEvent event, EnvelopeSorPojo bqPojo, RfqEnvelop envelop, boolean isMasked, List<String> sortedSupplierList) {
		LOG.info("Build envelope data for SOR ");
		try {

			// Same order as BQ
			List<EvaluationSuppliersSorPojo> sors = rfqSupplierSorDao.getAllSorsBySorIdsAndEventId(bqId, event.getId());
			List<EvaluationSuppliersSorPojo> supplierBq = new ArrayList<EvaluationSuppliersSorPojo>();
			if (CollectionUtils.isNotEmpty(sors)) {
				for (EvaluationSuppliersSorPojo sor : sors) {
					sortedSupplierList.add(sor.getSupplierName());
					EvaluationSuppliersSorPojo bqSupplierPojo = new EvaluationSuppliersSorPojo();
					bqPojo.setSorName(sor.getSorName());
					LOG.info("Sor name in evaluation:" + sor.getSorName());
					bqSupplierPojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), sor.getId(), envelop.getId())) : sor.getSupplierName());

					List<EvaluationSorPojo> allBqs = new ArrayList<EvaluationSorPojo>();
					if (sor != null) {
						EvaluationSorPojo bqItem = new EvaluationSorPojo();
						bqSupplierPojo.setRemark(sor.getRemark());
						bqSupplierPojo.setSorName(sor.getSorName());

						bqItem.setName(sor.getSorName());
						List<RfqSupplierSorItem> suppSorItems = rfqSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierId(sor.getSorId(), sor.getId());
						List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
						if (CollectionUtil.isNotEmpty(suppSorItems)) {
							for (RfqSupplierSorItem suppBqItem : suppSorItems) {
								EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									for (RfqSupplierSorItem childBqItem : suppBqItem.getChildren()) {
										EvaluationSorItemPojo evlBqChilItem = new EvaluationSorItemPojo();
										evlBqChilItem.setDescription(childBqItem.getItemName());
										evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
										evlBqChilItem.setUom(childBqItem.getUom().getUom());
										//Rate
										evlBqChilItem.setAmount(childBqItem.getTotalAmount());
										evlBqChilItem.setDecimal(event != null ? event.getDecimal() : "");
										evlBqItems.add(evlBqChilItem);

										List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
										List<RfqSorEvaluationComments> bqItemComments = rfqSorEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getSorItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RfqSorEvaluationComments review : bqItemComments) {
												EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
												bqComment.setCommentBy(review.getUserName());
												bqComment.setComments(review.getComment());
												comments.add(bqComment);
												reviews += "[ " + review.getUserName() + " ] " + review.getComment() + "\n";
											}
											evlBqChilItem.setReviews(reviews);
											evlBqChilItem.setReview(comments);
										}

									}
								}
							}
						}



						bqItem.setBqItems(evlBqItems);

						allBqs.add(bqItem);
					}
					bqSupplierPojo.setSors(allBqs);
					supplierBq.add(bqSupplierPojo);
				}
			}
			bqPojo.setSupplierSor(supplierBq);
		} catch (Exception e) {
			LOG.error("Could not get build Envlope SupplierBq for EvaluationSummary : " + e.getMessage(), e);
		}
	}


	private void buildEnvlopeSORTopSupplierComparisionforEvaluationSummary(String sorId, RfqEvent event, EnvelopeSorPojo bqPojo, boolean isMasked, RfqEnvelop envelop, List<String> top3Supplier) {
		try {
			List<RfaSupplierSorPojo> participatedSupplier = rfqSupplierSorDao.getAllRfqTopEventSuppliersIdByEventId(event.getId(), 3, sorId);
			Map<String, EvaluationSorItemPojo> itemsMap = new LinkedHashMap<String, EvaluationSorItemPojo>();
			List<EvaluationSuppliersSorPojo> pojoList = new ArrayList<EvaluationSuppliersSorPojo>();
			EvaluationSuppliersSorPojo pojo = new EvaluationSuppliersSorPojo();
			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierSorPojo rfaSupplierBqPojo = participatedSupplier.get(i);
				top3Supplier.add(rfaSupplierBqPojo.getSupplierCompanyName());
				bqPojo.setSorName(rfaSupplierBqPojo.getSorName());
				List<RfqSupplierSorItem> suppBqItems = rfqSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierIdParentIdNotNull(sorId, rfaSupplierBqPojo.getSupplierId());
				if (i == 0) {
					for (RfqSupplierSorItem suppBqItem : suppBqItems) {
							EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
							evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
							evlBqItem.setItemName(suppBqItem.getItemName());
							evlBqItem.setUom(suppBqItem.getUom() != null ? suppBqItem.getUom().getUom() : "");
						    evlBqItem.setAmount(suppBqItem.getTotalAmount());
							pojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
							itemsMap.put(suppBqItem.getSorItem().getId(), evlBqItem);
					}
				} else {
					for (RfqSupplierSorItem suppBqItem : suppBqItems) {
							EvaluationSorItemPojo evlBqItem = itemsMap.get(suppBqItem.getSorItem().getId());
							if (evlBqItem != null) {
								if (i == 1) {
									evlBqItem.setSupplier1TotalAmt(suppBqItem.getTotalAmount());
									pojo.setSupplierName1(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
								}
								if (i == 2) {
									evlBqItem.setSupplier2TotalAmt(suppBqItem.getTotalAmount());
									pojo.setSupplierName2(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
								}
							}
					}
				}
			}
			pojo.setTopSupplierItemList(new ArrayList<EvaluationSorItemPojo>(itemsMap.values()));
			pojoList.add(pojo);
			bqPojo.setTopSupplierSor(pojoList);
		} catch (Exception e) {
			LOG.error("Could not get build Envlope Top Supplier Comparision for EvaluationSummary : " + e.getMessage(), e);
		}
	}

	private void buildEnvlopeTopSupplierComparisionforEvaluationSummary(String bqId, RfqEvent event, EnvelopeBqPojo bqPojo, boolean isMasked, RfqEnvelop envelop, List<String> top3Supplier) {
		try {
			List<RfaSupplierBqPojo> participatedSupplier = rfqSupplierBqDao.getAllRfqTopEventSuppliersIdByEventId(event.getId(), 3, bqId);
			Map<String, EvaluationBqItemPojo> itemsMap = new LinkedHashMap<String, EvaluationBqItemPojo>();
			List<EvaluationSuppliersBqPojo> pojoList = new ArrayList<EvaluationSuppliersBqPojo>();
			EvaluationSuppliersBqPojo pojo = new EvaluationSuppliersBqPojo();
			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierBqPojo rfaSupplierBqPojo = participatedSupplier.get(i);
				top3Supplier.add(rfaSupplierBqPojo.getSupplierCompanyName());
				bqPojo.setBqName(rfaSupplierBqPojo.getBqName());
				List<RfqSupplierBqItem> suppBqItems = rfqSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierIds(bqId, rfaSupplierBqPojo.getSupplierId());
				if (i == 0) {
					for (RfqSupplierBqItem suppBqItem : suppBqItems) {
						EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
						evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
						evlBqItem.setQuantity(suppBqItem.getQuantity());
						evlBqItem.setItemName(suppBqItem.getItemName());
						evlBqItem.setUom(suppBqItem.getUom() != null ? suppBqItem.getUom().getUom() : "");
						if (suppBqItem.getOrder() != 0) {
							evlBqItem.setUnitPrice(suppBqItem.getUnitPrice());
						}
						evlBqItem.setAmount(suppBqItem.getTotalAmountWithTax());
						pojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
						pojo.setGrandTotal(rfaSupplierBqPojo.getGrandTotal());
						pojo.setAdditionalTax(rfaSupplierBqPojo.getAdditionalTax());
						pojo.setTotalAfterTax(rfaSupplierBqPojo.getTotalAfterTax());
						itemsMap.put(suppBqItem.getBqItem().getId(), evlBqItem);
					}
				} else {
					for (RfqSupplierBqItem suppBqItem : suppBqItems) {
						EvaluationBqItemPojo evlBqItem = itemsMap.get(suppBqItem.getBqItem().getId());
						if (evlBqItem != null) {
							if (i == 1) {
								if (suppBqItem.getOrder() != 0) {
									evlBqItem.setSupplier1UnitPrice(suppBqItem.getUnitPrice());
								}
								evlBqItem.setSupplier1TotalAmt(suppBqItem.getTotalAmountWithTax());
								pojo.setSupplierName1(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
								pojo.setGrandTotal1(rfaSupplierBqPojo.getGrandTotal());
								pojo.setAdditionalTax1(rfaSupplierBqPojo.getAdditionalTax());
								pojo.setTotalAfterTax1(rfaSupplierBqPojo.getTotalAfterTax());
							}
							if (i == 2) {
								if (suppBqItem.getOrder() != 0) {
									evlBqItem.setSupplier2UnitPrice(suppBqItem.getUnitPrice());
								}
								evlBqItem.setSupplier2TotalAmt(suppBqItem.getTotalAmountWithTax());
								pojo.setSupplierName2(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
								pojo.setGrandTotal2(rfaSupplierBqPojo.getGrandTotal());
								pojo.setAdditionalTax2(rfaSupplierBqPojo.getAdditionalTax());
								pojo.setTotalAfterTax2(rfaSupplierBqPojo.getTotalAfterTax());
							}
						}
					}
				}
			}
			pojo.setTopSupplierItemList(new ArrayList<EvaluationBqItemPojo>(itemsMap.values()));
			if (CollectionUtil.isNotEmpty(pojo.getTopSupplierItemList())) {
				EvaluationBqItemPojo footr = new EvaluationBqItemPojo();

				footr.setItemName("Grand Total Before Additional Charges");
				footr.setAmount(pojo.getGrandTotal());
				footr.setSupplier1TotalAmt(pojo.getGrandTotal1());
				footr.setSupplier2TotalAmt(pojo.getGrandTotal2());

				pojo.getTopSupplierItemList().add(footr);

				footr = new EvaluationBqItemPojo();
				footr.setItemName("Additional Charges");
				footr.setAmount(pojo.getAdditionalTax());
				footr.setSupplier1TotalAmt(pojo.getAdditionalTax1());
				footr.setSupplier2TotalAmt(pojo.getAdditionalTax2());

				pojo.getTopSupplierItemList().add(footr);

				footr = new EvaluationBqItemPojo();
				footr.setItemName("Grand Total After Additional Charges");
				footr.setAmount(pojo.getTotalAfterTax());
				footr.setSupplier1TotalAmt(pojo.getTotalAfterTax1());
				footr.setSupplier2TotalAmt(pojo.getTotalAfterTax2());

				pojo.getTopSupplierItemList().add(footr);
			}
			pojoList.add(pojo);
			bqPojo.setTopSupplierBq(pojoList);
		} catch (Exception e) {
			LOG.error("Could not get build Envlope Top Supplier Comparision for EvaluationSummary : " + e.getMessage(), e);
		}
	}

	private void buildEnvelopeCQData(RfqEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfqEnvelop envelop, boolean isMasked) {

		try {
			List<EnvelopeCqPojo> allCqs = new ArrayList<EnvelopeCqPojo>();
			List<String> cqid = rfqEnvelopDao.getCqIdlistByEnvelopId(envelop.getId());
			if (CollectionUtil.isNotEmpty(cqid)) {
				List<RfqCq> cqList = rfqCqService.findRfaCqForEventByEnvelopeId(cqid, event.getId());
				for (RfqCq cq : cqList) {
					EnvelopeCqPojo cqPojo = new EnvelopeCqPojo();
					cqPojo.setName(cq.getName());
					buildSupplierCqforEvaluationSummary(isMasked, cq, event, cqPojo, envelop);
					allCqs.add(cqPojo);
				}
			}

			auction.setEnvelopeCq(allCqs);
		} catch (Exception e) {
			LOG.error("Could not get build Envelope CQ Data : " + e.getMessage(), e);
		}

	}

	private void buildSupplierCqforEvaluationSummary(boolean isMasked, RfqCq cq, RfqEvent event, EnvelopeCqPojo pojo, RfqEnvelop envelop) {

		try {
			List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();

			if (cq != null) {
				EvaluationCqPojo cqPojo = new EvaluationCqPojo();
				cqPojo.setName(cq.getName());
				cqPojo.setDescription(cq.getDescription());

				List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
				if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
					for (RfqCqItem cqItem : cq.getCqItems()) {

						String itemName = "";
						EvaluationCqItemPojo cqItemPojo = new EvaluationCqItemPojo();
						cqItemPojo.setItemName(cqItem.getItemName());
						cqItemPojo.setItemDescription(cqItem.getItemDescription());
						cqItemPojo.setLevel(cqItem.getLevel() + "." + cqItem.getOrder());
						if (cqItem.getOrder() == 0) {
							cqItemPojo.setIsSection(Boolean.TRUE);
						} else {
							cqItemPojo.setIsSection(Boolean.FALSE);
						}
						if (cqItem.getParent() != null) {
							if (cqItem.getAttachment() == Boolean.TRUE) {
								itemName = cqItem.getItemName() + (Boolean.TRUE == cqItem.getIsSupplierAttachRequired() ? " (Attachment is Required) " : " (Attachment is Optional) ");

							} else {
								itemName = cqItem.getItemName();
							}
							cqItemPojo.setItemName(itemName);
							cqItemPojo.setOptionType(cqItem.getCqType() != null ? cqItem.getCqType().getValue() : "");

							List<EvaluationCqItemSupplierPojo> cqItemSuppliers = new ArrayList<EvaluationCqItemSupplierPojo>();
							List<Supplier> suppList = rfqEventSupplierDao.getEventSuppliersForSummary(event.getId());
							// Below code to get Suppliers Answers of each CQ Items

							if (CollectionUtil.isNotEmpty(suppList)) {
								// List<RfqSupplierCqItem> responseList =
								// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(),
								// event.getId(),
								// suppList);
								List<RfqSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), event.getId(), suppList);
								if (CollectionUtil.isNotEmpty(responseList)) {
									for (RfqSupplierCqItem suppCqItem : responseList) {
										EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
										cqItemSupplierPojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), suppCqItem.getSupplier().getId(), envelop.getId())) : suppCqItem.getSupplier().getCompanyName());
										List<RfqSupplierCqOption> listAnswers = rfqSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());

										if (suppCqItem.getCqItem().getCqType() == CqType.TEXT || suppCqItem.getCqItem().getCqType() == CqType.DATE || suppCqItem.getCqItem().getCqType() == CqType.NUMBER || suppCqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
											cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
										} else if (CqType.DOCUMENT_DOWNLOAD_LINK == suppCqItem.getCqItem().getCqType()) {
											List<String> docIds = new ArrayList<String>();
											List<RfqCqOption> rfqSupplierCqOptions = suppCqItem.getCqItem().getCqOptions();
											if (CollectionUtil.isNotEmpty(rfqSupplierCqOptions)) {
												for (RfqCqOption rfqSupplierCqOption : rfqSupplierCqOptions) {
													docIds.add(StringUtils.checkString(rfqSupplierCqOption.getValue()));
												}
												List<EventDocument> eventDocuments = rfqDocumentService.findAllRfqEventDocsNamesByEventIdAndDocIds(event.getId(), docIds);
												if (eventDocuments != null) {
													String str = "";
													for (EventDocument docName : eventDocuments) {
														str = str + docName.getFileName() + "\n";
													}
													cqItemSupplierPojo.setAnswer(str);
												}
											}
										} else if (CollectionUtil.isNotEmpty(listAnswers) && suppCqItem.getCqItem().getCqType() != CqType.DOCUMENT_DOWNLOAD_LINK) {
											String str = "";
											// List<RfqSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
											for (RfqSupplierCqOption op : listAnswers) {
												cqItemSupplierPojo.setScores(op.getScoring());
												int cqAnsListSize = (listAnswers).size();
												int index = (listAnswers).indexOf(op);
												str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));
											}
											cqItemSupplierPojo.setAnswer(str);
										}
										cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
										// Review Comments
										List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
										List<RfqCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), event.getId(), cqItem.getId(), null);
										if (CollectionUtil.isNotEmpty(comments)) {
											String evalComment = "";
											for (RfqCqEvaluationComments item : comments) {
												EvaluationCqItemSupplierCommentsPojo supCmnts = new EvaluationCqItemSupplierCommentsPojo();
												supCmnts.setComment(item.getComment());
												supCmnts.setCommentBy(item.getUserName());
												evalComments.add(supCmnts);
												evalComment += "[ " + item.getUserName() + " ] " + item.getComment() + "\n";
											}
											cqItemSupplierPojo.setEvalComment(evalComment);
											cqItemSupplierPojo.setComments(evalComments);
										}
										// Attachments
										if (StringUtils.checkString(suppCqItem.getFileName()).length() > 0) {
											cqItemSupplierPojo.setAttachments(suppCqItem.getFileName());
										}
										cqItemSuppliers.add(cqItemSupplierPojo);
									}
								}
							}
							cqItemPojo.setSuppliers(cqItemSuppliers);
						}

						allCqItems.add(cqItemPojo);
					}
					cqPojo.setCqItem(allCqItems);
				}
				allCqs.add(cqPojo);
			}
			pojo.setAllCqs(allCqs);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Cq for EvaluationSummary : " + e.getMessage(), e);
		}

	}

	@Override
	public JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rfqSummary = new ArrayList<EvaluationPojo>();
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RfqEvent event = getRfqEventByeventId(eventId);
			if (event != null) {

				DecimalFormat df = null;
				if (event.getDecimal() != null) {
					if (event.getDecimal().equals("1")) {
						df = new DecimalFormat("#,###,###,##0.0");
					} else if (event.getDecimal().equals("2")) {
						df = new DecimalFormat("#,###,###,##0.00");
					} else if (event.getDecimal().equals("3")) {
						df = new DecimalFormat("#,###,###,##0.000");
					} else if (event.getDecimal().equals("4")) {
						df = new DecimalFormat("#,###,###,##0.0000");
					} else if (event.getDecimal().equals("5")) {
						df = new DecimalFormat("#,###,###,##0.00000");
					} else if (event.getDecimal().equals("6")) {
						df = new DecimalFormat("#,###,###,##0.000000");
					}
				}
				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				String type = "RFQ";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName("");
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> SupplierList = rfqEventSupplierService.getAllSuppliersByEventId(eventId);
				List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
				List<EvaluationBqPojo> bqGranTotalList = new ArrayList<EvaluationBqPojo>();
				if (CollectionUtils.isNotEmpty(SupplierList)) {
					String SubmissionStatus = "";
					for (EventSupplier supplier : SupplierList) {
						EvaluationSuppliersPojo suppliers = new EvaluationSuppliersPojo();
						suppliers.setContactName(supplier.getSupplier().getFullName());
						suppliers.setSupplierName(supplier.getSupplierCompanyName());
						suppliers.setDesignation(supplier.getSupplier().getDesignation());
						suppliers.setEmail(supplier.getSupplier().getCommunicationEmail());

						if (SubmissionStatusType.ACCEPTED.equals(supplier.getSubmissionStatus())) {
							SubmissionStatus = "ACCEPTED";
						} else if (SubmissionStatusType.COMPLETED.equals(supplier.getSubmissionStatus())) {
							SubmissionStatus = "COMPLETED";
						} else if (SubmissionStatusType.INVITED.equals(supplier.getSubmissionStatus())) {
							SubmissionStatus = "INVITED";
						} else if (SubmissionStatusType.REJECTED.equals(supplier.getSubmissionStatus())) {
							SubmissionStatus = "REJECTED";
						}
						suppliers.setStatus(SubmissionStatus);
						suppliers.setContactNo(supplier.getSupplier().getMobileNumber());
						suppliers.setIsQualify(supplier.getDisqualify());

						if (supplier.getDisqualify() == Boolean.TRUE) {
							suppliers.setReason(supplier.getDisqualifyRemarks());
						}

						allSuppliers.add(suppliers);
					}
				}

				// Below Code to show all CQ details
				List<EvaluationCqPojo> allCqs = getAllSupplierCqforEvaluationSummary(eventId, envelopeId);

				// Below functionality to get all BQ Items along with suppliers
				// response of each BQ Items
				List<EvaluationSuppliersBqPojo> supplierBq = getAllSupplierBqforEvaluationSummary(SupplierList, eventId, envelopeId);

				// GRAND TOTAL BQ LIST
				List<Bq> bqs = rfqEventBqDao.findBqbyEventId(eventId);
				if (CollectionUtil.isNotEmpty(bqs)) {
					for (Bq bq : bqs) {
						EvaluationBqPojo supBqs = new EvaluationBqPojo();
						supBqs.setName(bq.getName());
						List<EvaluationSuppliersPojo> suppBqComments = new ArrayList<EvaluationSuppliersPojo>();
						if (CollectionUtil.isNotEmpty(SupplierList)) {
							for (EventSupplier supplier : SupplierList) {
								EvaluationSuppliersPojo supList = new EvaluationSuppliersPojo();
								RfqSupplierBq supBq = rfqSupplierBqDao.findBqByBqId(bq.getId(), supplier.getSupplier().getId());
								supList.setSupplierName(supplier.getSupplier() != null ? supplier.getSupplier().getCompanyName() : "");
								if (supBq != null) {
									supList.setGrandTotal(df.format(supBq.getGrandTotal()));
									supList.setTotalItemTaxAmt(supBq.getTaxDescription() != null ? df.format(supBq.getTotalAfterTax()) : "");
								}
								String comments = "";
								List<RfqBqTotalEvaluationComments> comment = rfqBqTotalEvaluationCommentsService.getComments(supplier.getSupplier().getId(), eventId, bq.getId(), SecurityLibrary.getLoggedInUser());
								if (CollectionUtil.isNotEmpty(comment)) {
									for (RfqBqTotalEvaluationComments leadComments : comment) {
										comments += "[" + leadComments.getLoginName() + " ] " + leadComments.getComment() + "\n";
									}
								}
								supList.setRemark(comments);
								suppBqComments.add(supList);
							}
						}
						supBqs.setLeadComments(suppBqComments);
						bqGranTotalList.add(supBqs);
					}
				}

				// LOG.info(supplierBq.toString());
				summary.setBqLeadCommentsList(bqGranTotalList);
				summary.setSuppliers(allSuppliers);
				summary.setCqs(allCqs);
				summary.setBqSuppliers(supplierBq);

				rfqSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rfqSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfqSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report : " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, String envelopeId) {
		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		try {

			List<RfqCq> cqList = rfqCqService.findCqForEvent(eventId);
			for (RfqCq cq : cqList) {
				EvaluationCqPojo cqPojo = new EvaluationCqPojo();
				cqPojo.setName(cq.getName());
				cqPojo.setDescription(cq.getDescription());

				List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
				if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
					for (RfqCqItem cqItem : cq.getCqItems()) {
						String itemName = "";
						EvaluationCqItemPojo cqItemPojo = new EvaluationCqItemPojo();
						if (cqItem.getAttachment() == Boolean.TRUE) {
							itemName = cqItem.getItemName() + (Boolean.TRUE == cqItem.getIsSupplierAttachRequired() ? " (Attachment is Required) " : " (Attachment is Optional) ");

						} else {
							itemName = cqItem.getItemName();
						}
						cqItemPojo.setItemName(itemName);
						cqItemPojo.setItemDescription(cqItem.getItemDescription());
						cqItemPojo.setLevel(cqItem.getLevel() + "." + cqItem.getOrder());

						List<EvaluationCqItemSupplierPojo> cqItemSuppliers = new ArrayList<EvaluationCqItemSupplierPojo>();
						List<Supplier> suppList = rfqEventSupplierDao.getEventSuppliersForEvaluation(eventId);

						// Below code to get Suppliers Answers of each CQ Items
						if (CollectionUtil.isNotEmpty(suppList)) {
							// List<RfqSupplierCqItem> responseList =
							// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId,
							// suppList);
							List<RfqSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
							if (CollectionUtil.isNotEmpty(responseList)) {

								for (RfqSupplierCqItem suppCqItem : responseList) {
									EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
									cqItemSupplierPojo.setSupplierName(suppCqItem.getSupplier().getCompanyName());
									List<RfqSupplierCqOption> listAnswers = rfqSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
									if (suppCqItem.getCqItem().getCqType() == CqType.TEXT) {
										cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
									} else if (CollectionUtil.isNotEmpty(listAnswers)) {
										String str = "";
										// List<RfqSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
										for (RfqSupplierCqOption op : listAnswers) {
											// str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() :
											// "");
											int cqAnsListSize = (listAnswers).size();
											int index = (listAnswers).indexOf(op);
											str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));
											cqItemSupplierPojo.setAnswer(str);
										}
									}
									cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
									// Review Comments
									List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
									List<RfqCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
									if (CollectionUtil.isNotEmpty(comments)) {
										String evalComment = "";
										for (RfqCqEvaluationComments item : comments) {
											EvaluationCqItemSupplierCommentsPojo supCmnts = new EvaluationCqItemSupplierCommentsPojo();
											supCmnts.setComment(item.getComment());
											supCmnts.setCommentBy(item.getUserName());
											evalComments.add(supCmnts);
											evalComment += "[ " + item.getUserName() + " ] " + item.getComment() + "\n";
										}
										cqItemSupplierPojo.setEvalComment(evalComment);
										cqItemSupplierPojo.setComments(evalComments);
									}
									// Attachments
									if (StringUtils.checkString(suppCqItem.getFileName()).length() > 0) {
										cqItemSupplierPojo.setAttachments(suppCqItem.getFileName());
									}

									cqItemSuppliers.add(cqItemSupplierPojo);
								}
							}
						}
						cqItemPojo.setSuppliers(cqItemSuppliers);

						allCqItems.add(cqItemPojo);
					}
				}
				cqPojo.setCqItem(allCqItems);
				allCqs.add(cqPojo);
			}

		} catch (Exception e) {
			LOG.error("Could not get All Supplier Cq for Evaluation Summary :" + e.getMessage(), e);
		}
		return allCqs;
	}

	private List<EvaluationSuppliersBqPojo> getAllSupplierBqforEvaluationSummary(List<EventSupplier> supplierList, String eventId, String envelopeId) {
		List<EvaluationSuppliersBqPojo> supplierBq = new ArrayList<EvaluationSuppliersBqPojo>();
		try {

			if (CollectionUtils.isNotEmpty(supplierList)) {
				for (EventSupplier supItem : supplierList) {
					if (supItem.getDisqualify() == Boolean.FALSE) {
						EvaluationSuppliersBqPojo bqSupplierPojo = new EvaluationSuppliersBqPojo();

						bqSupplierPojo.setSupplierName(supItem.getSupplierCompanyName());

						List<Bq> bqs = rfqEventBqDao.findBqbyEventIdAndEnvelopeId(eventId, envelopeId);

						List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
						if (CollectionUtil.isNotEmpty(bqs)) {
							for (Bq bq : bqs) {
								EvaluationBqPojo bqItem = new EvaluationBqPojo();
								bqItem.setName(bq.getName());
								List<RfqSupplierBqItem> suppBqItems = rfqSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());
								List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
								if (CollectionUtil.isNotEmpty(suppBqItems)) {
									for (RfqSupplierBqItem suppBqItem : suppBqItems) {
										EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
										evlBqItem.setDescription(suppBqItem.getItemName());
										evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
										evlBqItem.setQuantity(suppBqItem.getQuantity());
										// evlBqItem.setUom(suppBqItem.getUom().getUom());
										evlBqItem.setTaxAmt(suppBqItem.getTax());
										evlBqItem.setUnitPrice(suppBqItem.getUnitPrice());
										evlBqItem.setTotalAmt(suppBqItem.getTotalAmountWithTax());
										evlBqItem.setAmount(suppBqItem.getTotalAmount());
										evlBqItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
										evlBqItems.add(evlBqItem);

										if (suppBqItem.getChildren() != null) {
											for (RfqSupplierBqItem childBqItem : suppBqItem.getChildren()) {
												EvaluationBqItemPojo evlBqChilItem = new EvaluationBqItemPojo();
												evlBqChilItem.setDescription(childBqItem.getItemName());
												evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
												evlBqChilItem.setQuantity(childBqItem.getQuantity());
												evlBqChilItem.setUom(childBqItem.getUom().getUom());
												evlBqChilItem.setTaxAmt(childBqItem.getTax());
												evlBqChilItem.setUnitPrice(childBqItem.getUnitPrice());
												evlBqChilItem.setTotalAmt(childBqItem.getTotalAmountWithTax());
												evlBqChilItem.setAmount(childBqItem.getTotalAmount());
												evlBqChilItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
												// Review Comments
												List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
												List<RfqBqEvaluationComments> bqItemComments = rfqBqEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getBqItem().getId(), null);
												if (CollectionUtil.isNotEmpty(bqItemComments)) {
													String reviews = "";
													for (RfqBqEvaluationComments review : bqItemComments) {
														EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
														bqComment.setCommentBy(review.getLoginName());
														bqComment.setComments(review.getComment());
														comments.add(bqComment);
														reviews += "[ " + review.getLoginName() + " ] " + review.getComment() + "\n";
													}
													evlBqChilItem.setReviews(reviews);
													evlBqChilItem.setReview(comments);
												}
												LOG.info("BQ COMMENTS :: " + comments);

												evlBqItems.add(evlBqChilItem);
											}
										}
									}
								}
								bqItem.setBqItems(evlBqItems);

								allBqs.add(bqItem);
							}
						}
						bqSupplierPojo.setBqs(allBqs);
						supplierBq.add(bqSupplierPojo);
					}
				}
			}

		} catch (Exception e) {
			LOG.error("Could not get get All SupplierCq for EvaluationSummary : " + e.getMessage(), e);
		}
		return supplierBq;
	}

	@Override
	@Transactional(readOnly = false)
	public JasperPrint generateSubmissionReport(String evenvelopId, String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;

		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		sdf.setTimeZone(timeZone);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		generatedSdf.setTimeZone(timeZone);
		parameters.put("generatedOn", generatedSdf.format(new Date()));
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RFXSubmissionBook.jasper");
			File jasperfile = resource.getFile();

			List<EvaluationAuctionBiddingPojo> auctionSummary = buildSubmissionReportData(eventId, sdf, parameters, evenvelopId);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auctionSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Submission Report PDF : " + e.getMessage(), e);
		}

		return jasperPrint;

	}

	private List<EvaluationAuctionBiddingPojo> buildSubmissionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			RfqEvent event = rfqEventDao.findByEventId(eventId);

			List<EventSupplier> supplierList = rfqEventSupplierDao.getAllSuppliersByEventId(eventId);
			if (event != null) {
				RfqEnvelop envelop = rfqEnvelopService.getRfqEnvelopById(evenvelopId);
				boolean isMasked = false;
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					for (EventSupplier eventSupplier : supplierList) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
					}
					isMasked = true;
				}
				LOG.info("isMasked" + isMasked);
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				auction.setIsMask(isMasked);
				buildSupplierCountData(supplierList, auction);

				buildHeadingReport(event, supplierList, auction, sdf, envelop, isMasked, false);
				auction.setEnvelopTitle(envelop != null ? envelop.getEnvelopTitle() : "NA");
				auction.setLeadEvaluater(envelop != null ? (envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "NA") : "NA");
				auction.setLeadEvaluatorSummary(envelop != null ? ((envelop.getEvaluatorSummaryDate() != null ? (sdf.format(envelop.getEvaluatorSummaryDate()) + ":") : "") + (envelop.getLeadEvaluatorSummary() != null ? envelop.getLeadEvaluatorSummary() : "")) : "N/A");
				auction.setFileName(envelop != null ? envelop.getFileName() : "N/A");
				if (envelop != null) {
					buildEvaluatorsSummary(envelop.getEvaluators(), auction);
				}
				buildEventDetailData(sdf, event, auction);
				buildBuyerAttachementData(event.getId(), auction, sdf);
				buildMatchingIpAddressData(eventId, auction, supplierList, envelop);
				LOG.info("isMasked *******1****: " + isMasked);
				buildSupplierLineChartAndData(sdf, event, supplierList, auction, envelop, isMasked);

				List<String> sortedSupplierList = new ArrayList<>();
				List<String> top3Supplier = new ArrayList<>();

				buildEnvoleBQData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);
				buildEnvoleSORData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);
				buildEnvelopeCQData(event, auction, sdf, envelop, isMasked);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);

		} catch (Exception e) {
			LOG.error("Could not get build Submission Report Data :" + e.getMessage(), e);
		}
		return auctionSummary;
	}

	private String formatedDecimalNumber(String decimalPoint, BigDecimal value) {
		String decimal = StringUtils.checkString(decimalPoint).length() > 0 ? decimalPoint : "1";
		if (value != null) {
			DecimalFormat df = new DecimalFormat(decimal.equals("1") ? "#,###,###,##0.0" : decimal.equals("2") ? "#,###,###,##0.00" : decimal.equals("3") ? "#,###,###,##0.000" : decimal.equals("4") ? "#,###,###,##0.0000" : decimal.equals("5") ? "#,###,###,##0.00000" : decimal.equals("6") ? "#,###,###,##0.000000" : "#,###,###,##0.00");
			return df.format(value);
		}
		return "";
	}

	@Override
	@Transactional(readOnly = false)
	public void autoSaveRfqDetails(RfqEvent rfqEvent, String[] industryCategory, BindingResult result, String strTimeZone) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (rfqEvent.getEventVisibilityDates() != null) {
				String[] visibilityDates = rfqEvent.getEventVisibilityDates().split("-");
				formatter.setTimeZone(timeZone);
				Date startDate = formatter.parse(visibilityDates[0]);
				Date endDate = formatter.parse(visibilityDates[1]);
				rfqEvent.setEventEnd(endDate);
				rfqEvent.setEventStart(startDate);
			}

			if (industryCategory != null) {
				List<IndustryCategory> icList = new ArrayList<>();
				for (String industryCatId : industryCategory) {
					IndustryCategory ic = new IndustryCategory();
					ic.setId(industryCatId);
					icList.add(ic);
				}
				rfqEvent.setIndustryCategories(icList);
			}

			if (result.hasErrors()) {
				List<String> errMessages = new ArrayList<String>();
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.info("ERROR : " + err.getDefaultMessage());
				}
			} else {
				if (CollectionUtil.isNotEmpty(rfqEvent.getApprovals())) {
					int level = 1;
					for (RfqEventApproval app : rfqEvent.getApprovals()) {
						app.setEvent(rfqEvent);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
							for (RfqApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}

			}
			if (StringUtils.checkString(rfqEvent.getId()).length() > 0) {
				RfqEvent persistObj = getRfqEventByeventId(rfqEvent.getId());
				Date notificationDateTime = null;
				if (rfqEvent.getEventPublishDate() != null && rfqEvent.getEventPublishTime() != null) {
					notificationDateTime = DateUtil.combineDateTime(rfqEvent.getEventPublishDate(), rfqEvent.getEventPublishTime(), timeZone);
				}
				rfqEvent.setEventPublishDate(notificationDateTime);
				setAndUpdateRfpEvent(rfqEvent, persistObj);
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}

	}

	private void setAndUpdateRfpEvent(RfqEvent rfqEvent, RfqEvent persistObj) throws ParseException {

		LOG.info("Rfa Event : Bq : " + rfqEvent.getBillOfQuantity() + " : cq : " + rfqEvent.getQuestionnaires() + " : Doc : " + rfqEvent.getDocumentReq() + " : Meet : " + rfqEvent.getMeetingReq());
		persistObj.setModifiedDate(new Date());
		LOG.info(rfqEvent.getIndustryCategory());
		persistObj.setIndustryCategory(rfqEvent.getIndustryCategory());
		// persistObj.setStatus(EventStatus.DRAFT); -- DONT SET THE STATUS AS
		// DRAFT AS THIS COULD BE A SUSPENDED EVENT
		// EDIT - @Nitin Otageri

		persistObj.setEventStart(rfqEvent.getEventStart());
		persistObj.setEventEnd(rfqEvent.getEventEnd());
		LOG.info("StartDate:" + rfqEvent.getEventStart());
		LOG.info("EndDate:" + rfqEvent.getEventEnd());
		persistObj.setEventVisibility(rfqEvent.getEventVisibility());
		persistObj.setEventName(rfqEvent.getEventName());
		persistObj.setReferanceNumber(rfqEvent.getReferanceNumber());
		persistObj.setUrgentEvent(rfqEvent.getUrgentEvent());
		persistObj.setEventVisibilityDates(rfqEvent.getEventVisibilityDates());
		persistObj.setDeliveryAddress(rfqEvent.getDeliveryAddress());
		persistObj.setParticipationFeeCurrency(rfqEvent.getParticipationFeeCurrency());
		persistObj.setParticipationFees(rfqEvent.getParticipationFees());
		persistObj.setDepositCurrency(rfqEvent.getDepositCurrency());
		persistObj.setDeposit(rfqEvent.getDeposit());
		// Should not assign this

		if (persistObj.getTemplate() != null && persistObj.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(persistObj.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (!rfxTemplate.getVisibleAddSupplier()) {
				rfqEvent.setAddSupplier(rfxTemplate.getAddSupplier());
			}
			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				rfqEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				rfqEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}
			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				rfqEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}
		}
		persistObj.setEventPublishDate(rfqEvent.getEventPublishDate());
		persistObj.setSubmissionValidityDays(rfqEvent.getSubmissionValidityDays());
		persistObj.setCloseEnvelope(rfqEvent.getCloseEnvelope());
		persistObj.setAddSupplier(rfqEvent.getAddSupplier());
		// Event Timeline
		// Date notificationDateTime =
		// DateUtil.combineDateTime(rfaEvent.getSupplierNotificationDate(),
		// rfaEvent.getSupplierNotificationTime());
		// persistObj.setSupplierNotificationDate(notificationDateTime);
		persistObj.setEventStart(rfqEvent.getEventStart());
		persistObj.setApprovals(rfqEvent.getApprovals());

		// Event Req
		persistObj.setEnableEvaluationDeclaration(rfqEvent.getEnableEvaluationDeclaration());
		persistObj.setEnableSupplierDeclaration(rfqEvent.getEnableSupplierDeclaration());
		persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == rfqEvent.getEnableEvaluationDeclaration() && rfqEvent.getEvaluationProcessDeclaration() != null ? rfqEvent.getEvaluationProcessDeclaration() : null);
		persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == rfqEvent.getEnableSupplierDeclaration() && rfqEvent.getSupplierAcceptanceDeclaration() != null ? rfqEvent.getSupplierAcceptanceDeclaration() : null);
		persistObj.setBillOfQuantity(rfqEvent.getBillOfQuantity());
		persistObj.setQuestionnaires(rfqEvent.getQuestionnaires());
		persistObj.setDocumentReq(rfqEvent.getDocumentReq());
		persistObj.setMeetingReq(rfqEvent.getMeetingReq());
		persistObj.setEventDetailCompleted(Boolean.TRUE);
		persistObj.setIndustryCategories(rfqEvent.getIndustryCategories());
		persistObj.setDeliveryDate(rfqEvent.getDeliveryDate());
		persistObj.setViewSupplerName(rfqEvent.getViewSupplerName());
		persistObj.setUnMaskedUser(rfqEvent.getUnMaskedUser());
		persistObj.setAllowToSuspendEvent(rfqEvent.getAllowToSuspendEvent());
		updateEvent(persistObj);

	}

	@Override
	public List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {

		List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		getRfqSearchEventsForExport(tenantId, eventIds, resultList, searchFilterEventPojo, select_all, startDate, endDate, sdf);
		return resultList;
	}

	private void getRfqSearchEventsForExport(String tenantId, String[] eventIds, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		List<RfqEvent> rftList = rfqEventDao.getSearchEventsByIds(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate);
		if (CollectionUtil.isNotEmpty(rftList)) {
			for (RfqEvent event : rftList) {
				DraftEventPojo eventPojo = new DraftEventPojo();
				if (event.getEventDescription() != null) {
					eventPojo.setEventDescription(event.getEventDescription());
				}
				eventPojo.setId(event.getId());
				eventPojo.setCostCenter(event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "");
				eventPojo.setInvitedSupplierCount(event.getSuppliers().size());
				eventPojo.setEventName(event.getEventName());
				eventPojo.setReferenceNumber(event.getReferanceNumber());
				eventPojo.setSysEventId(event.getEventId());
				eventPojo.setEventStart(event.getEventStart());
				eventPojo.setEventEnd(event.getEventEnd());
				eventPojo.setType(RfxTypes.RFQ);
				eventPojo.setDeliveryDate(event.getDeliveryDate());
				eventPojo.setVisibility(event.getEventVisibility());
				eventPojo.setPublishDate(event.getEventPublishDate());
				eventPojo.setValidityDays(event.getSubmissionValidityDays());
				eventPojo.setEventUser(event.getEventOwner().getName());
				eventPojo.setHistricAmount(event.getHistoricaAmount() != null ? (event.getHistoricaAmount()) : BigDecimal.ZERO);
				eventPojo.setDeposite(event.getDeposit() != null ? (event.getDeposit()) : BigDecimal.ZERO);
				eventPojo.setEstimatedBudget(event.getBudgetAmount() != null ? (event.getBudgetAmount()) : BigDecimal.ZERO);
				eventPojo.setEventFees(event.getParticipationFees() != null ? (event.getParticipationFees()) : BigDecimal.ZERO);
				eventPojo.setTemplateName(event.getTemplate() != null ? (event.getTemplate().getTemplateName()) : "");
				eventPojo.setConcludedaDate(event.getConcludeDate() != null ? (event.getConcludeDate().toString()) : "");
				eventPojo.setAwardedDate(event.getAwardDate() != null ? event.getAwardDate().toString() : "");
				eventPojo.setPushDate(event.getEventPushDate() != null ? event.getEventPushDate().toString() : "");
				Double avarageBidValue = rfqEventService.getAvarageBidPriceSubmitted(event.getId());
				eventPojo.setAvarageBidSubmited(avarageBidValue != null ? avarageBidValue.toString() : "");

				if (event.getTeamMembers() != null) {
					String names = "";
					for (RfqTeamMember teamMember : event.getTeamMembers()) {
						if (teamMember.getTeamMemberType() == TeamMemberType.Associate_Owner) {
							names += teamMember.getUser().getName() + ",";
						}
					}
					eventPojo.setAssoiciateOwner(names);
				}
				if (event.getUnMaskedUser() != null) {
					eventPojo.setUnmaskOwner(event.getUnMaskedUser().getName());
				}

				if (event.getDeliveryAddress() != null) {
					String address = "";
					address += event.getDeliveryAddress().getLine1() != null ? (event.getDeliveryAddress().getLine1()) : "";
					address += event.getDeliveryAddress().getLine2() != null ? (event.getDeliveryAddress().getLine2()) : "";
					eventPojo.setDeliveryAddress(address);
				}

				List<EventSupplier> supplierList = rfqEventSupplierService.getAllSuppliersByEventId(event.getId());
				String previewSupplier = "";
				String rejectedSupplier = "";
				String disqualifiedSupp = "";
				// String awardedSuppliers = "";

				for (EventSupplier sup : supplierList) {
					if (sup.getSupplierEventReadTime() != null && sup.getSubmissionStatus() == SubmissionStatusType.REJECTED) {
						rejectedSupplier += sup.getSupplierCompanyName() + ",";
					}
					if (sup.getPreviewTime() != null) {
						previewSupplier += sup.getSupplierCompanyName() + ",";
					}
					if (sup.getDisqualify() != null && sup.getDisqualify()) {
						disqualifiedSupp += sup.getSupplierCompanyName() + ",";
					}
				}
				if (StringUtils.checkString(previewSupplier).length() > 0) {
					previewSupplier = previewSupplier.substring(0, previewSupplier.length() - 1);
				}

				if (StringUtils.checkString(rejectedSupplier).length() > 0) {
					rejectedSupplier = rejectedSupplier.substring(0, rejectedSupplier.length() - 1);
				}

				if (StringUtils.checkString(disqualifiedSupp).length() > 0) {
					disqualifiedSupp = disqualifiedSupp.substring(0, disqualifiedSupp.length() - 1);
				}
				// eventPojo.setPreviewedSupplier(previewSupplier);
				// eventPojo.setRejectedSupplier(rejectedSupplier);
				// eventPojo.setDisqualifedSuppliers(disqualifiedSupp);
				// List<Supplier> awardedSuppliersList = event.getAwardedSuppliers();
				// for (Supplier awardedSupplier : awardedSuppliersList) {
				// awardedSuppliers += awardedSupplier.getCompanyName() + ",";
				//
				// }
				// eventPojo.setAwardedSupplier(awardedSuppliers);
				eventPojo.setConcluded(event.getConcludeDate() != null ? "YES" : "NO");
				eventPojo.setConcludedaDate(event.getConcludeDate() != null ? sdf.format(event.getConcludeDate()) : "");
				eventPojo.setPushToEvent(event.getNextEventId() != null ? "YES" : "NO");

				if (event.getBusinessUnit() != null) {
					eventPojo.setUnitName(event.getBusinessUnit().getUnitName());
				}
				eventPojo.setStatus(event.getStatus());
				String eventCategories = "";
				for (IndustryCategory ic : event.getIndustryCategories()) {
					eventCategories += ic.getName() + ",";
				}
				if (eventCategories.length() > 0) {
					eventCategories = eventCategories.substring(0, eventCategories.length() - 1);
				}
				eventPojo.setEventCategories(eventCategories);

				RfqEventSupplier leadingSupplier = null;
				int submittedCount = 0;
				int acceptedCount = 0;
				BigDecimal leadingAmount = BigDecimal.ZERO;
				for (RfqEventSupplier es : event.getSuppliers()) {
					if (SubmissionStatusType.COMPLETED == es.getSubmissionStatus()) {
						submittedCount++;
					}
					if (es.getAcceptedBy() != null) {
						acceptedCount++;
					}
					if (Boolean.TRUE == es.getDisqualify() || Boolean.FALSE == es.getSubmitted() || SubmissionStatusType.COMPLETED != es.getSubmissionStatus())
						continue;
					boolean allOk = true;

					BigDecimal bqTotal = BigDecimal.ZERO;
					for (RfqEventBq eventBq : event.getEventBqs()) {
						// Check if any items is zero
						int count = rfqSupplierBqItemDao.countIncompleteBqItemByBqIdsForSupplier(eventBq.getId(), es.getSupplier().getId());
						LOG.info("Check Number of Item Which is Zero " + count);
						// if (count > 0) {
						// allOk = false;
						// break;
						// }
						RfqSupplierBq supBq = rfqSupplierBqDao.findQualifiedSupplierBqByEventIdAndSupplierId(event.getId(), eventBq.getId(), es.getSupplier().getId());
						LOG.info("No of Bq For " + supBq);

						if (supBq == null) {
							allOk = false;
							break;
						}
						bqTotal = bqTotal.add(supBq.getTotalAfterTax());
						LOG.info("supBq.getTotalAfterTax " + supBq.getTotalAfterTax());
						LOG.info("bqTotal " + bqTotal);
						LOG.info(leadingAmount.doubleValue());
					}
					if (!allOk)
						continue;
					if (leadingAmount.compareTo(new BigDecimal("0.0")) == 0) {
						leadingSupplier = es;
						leadingAmount = bqTotal;
						LOG.info("leading amount if " + leadingAmount);
					} else if (bqTotal.doubleValue() > leadingAmount.doubleValue()) {
						leadingSupplier = es;
						leadingAmount = bqTotal;
						LOG.info("leading amount else if " + leadingAmount);
					}
				}

				eventPojo.setSubmittedSupplierCount(submittedCount);
				eventPojo.setLeadingAmount(leadingAmount);
				eventPojo.setAcceptedSupplierCount(acceptedCount);
				if (leadingSupplier != null && leadingSupplier.getSupplier() != null) {
					eventPojo.setLeadingSupplier(leadingSupplier.getSupplier().getCompanyName());
				}
				resultList.add(eventPojo);
			}
		}
	}

	@Override
	public EventTimerPojo getTimeEventByeventId(String eventId) {
		return rfqEventDao.getTimeEventByeventId(eventId);
	}

	@Override
	public RfqEvent loadEventForSummeryPageById(String id) {
		RfqEvent event = rfqEventDao.findBySupplierEventId(id);

		// Here on the above method there is some issue to retreive sorlist as already
		// it is retreving BqList . that's why two method.
		RfqEvent event2 = rfqEventDao.findEventSorByEventId(id);
		if (event != null) {

			if (event.getTemplate() != null) {
				event.getTemplate().getTemplateName();
			}

			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
			}
			if (event.getCostCenter() != null) {
				event.getCostCenter().getCostCenter();
			}
			if (event.getBusinessUnit() != null) {
				event.getBusinessUnit().getUnitName();
			}
			if (event.getIndustryCategory() != null) {
				event.getIndustryCategory().getName();
			}
			if (event.getParticipationFeeCurrency() != null) {
				event.getParticipationFeeCurrency().getCurrencyCode();
			}
			if (event.getDepositCurrency() != null) {
				event.getDepositCurrency().getCurrencyCode();
			}

			if (event.getProcurementMethod() != null) {
				event.getProcurementMethod().getProcurementMethod();
			}
			if (event.getProcurementCategories() != null) {
				event.getProcurementCategories().getProcurementCategories();
			}
			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfqUnMaskedUser unmaskuser : event.getUnMaskedUsers()) {
					unmaskuser.getUser().getCommunicationEmail();
					unmaskuser.getUser().getName();
					unmaskuser.getUser().getPhoneNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RfqEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getCommunicationEmail();
					user.getUser().getName();
					user.getUser().getPhoneNumber();
				}
			}

			// User unMaskedUser = rfqEventDao.getUnMaskedUserNameAndMailByEventId(event.getId());
			// if (unMaskedUser != null) {
			// event.setUnMaskedUser(unMaskedUser);
			// }
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfqEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RfqTeamMember teamMembers : event.getTeamMembers()) {
					teamMembers.getTeamMemberType();
					teamMembers.getUser().getName();
				}
			}

			if (event.getEventOwner().getBuyer() != null) {
				event.getEventOwner().getBuyer().getFullName();
				event.getEventOwner().getBuyer().getLine1();
				event.getEventOwner().getBuyer().getLine2();
				event.getEventOwner().getBuyer().getCity();
				if (event.getEventOwner().getBuyer().getState() != null) {
					event.getEventOwner().getBuyer().getState().getStateName();
					if (event.getEventOwner().getBuyer().getState().getCountry() != null) {
						event.getEventOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				for (IndustryCategory industryCategory : event.getIndustryCategories()) {
					industryCategory.getName();
					industryCategory.getCreatedBy();
					if (industryCategory.getCreatedBy() != null) {
						industryCategory.getCreatedBy().getName();
					}
					industryCategory.getCode();
					industryCategory.getBuyer();
				}
			}

			// Touch Event reminders...
			if (CollectionUtils.isNotEmpty(event.getRftReminder())) {
				for (RfqReminder reminder : event.getRftReminder()) {
					reminder.getInterval();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfqEventMeeting meeting : event.getMeetings()) {
					meeting.getAppointmentDateTime();
					meeting.getAppointmentTime();
					meeting.getInviteSuppliers();
					meeting.getTitle();
					meeting.getRfxEventMeetingContacts();
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RfqEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
							meetingContact.getContactName();
							meetingContact.getContactNumber();
							meetingContact.getContactEmail();
							meetingContact.getId();

						}
					}
					meeting.getStatus();
					meeting.getVenue();
					meeting.getRemarks();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfqEventMeeting meeting : event.getMeetings()) {
					meeting.getRemarks();
					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier supplier : meeting.getInviteSuppliers()) {
							supplier.getCompanyName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RfqEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
							contact.getContactName();
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
				for (RfqEventBq bq : event.getEventBqs()) {
					if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
						for (RfqBqItem item : bq.getBqItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event2.getEventSors())) {
				for (RfqEventSor bq : event2.getEventSors()) {
					if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
						for (RfqSorItem item : bq.getSorItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
				event.setEventSors(event2.getEventSors());
			}
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				for (RfqComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfqEventApproval approvalLevel : event.getApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfqApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfqEventSuspensionApproval approvalLevel : event.getSuspensionApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfqSuspensionApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionComment())) {
				for (RfqSuspensionComment comment : event.getSuspensionComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
				for (RfqEventAwardApproval approvalLevel : event.getAwardApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfqAwardApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardComment())) {
				LOG.info(" Comments  >>>  " + event.getAwardComment());
				for (RfqAwardComment comment : event.getAwardComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}

			if (event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getDescription();
			}

			if (event.getSupplierAcceptanceDeclaration() != null) {
				event.getSupplierAcceptanceDeclaration().getTitle();
			}
			if (event.getEvaluationProcessDeclaration() != null) {
				event.getEvaluationProcessDeclaration().getTitle();
			}
		}
		if (event.getConcludeBy() != null) {
			event.getConcludeBy().getName();
		}

		return event;
	}

	@Override
	public RfqEvent loadEventForSummeryPageForSupplierById(String id) {
		RfqEvent event = rfqEventDao.findBySupplierEventId(id);
		if (event != null) {

			if (event.getTemplate() != null) {
				event.getTemplate().getTemplateName();
			}

			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
			}
			if (event.getCostCenter() != null) {
				event.getCostCenter().getCostCenter();
			}
			if (event.getBusinessUnit() != null) {
				event.getBusinessUnit().getUnitName();
			}
			if (event.getIndustryCategory() != null) {
				event.getIndustryCategory().getName();
			}

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfqEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}

			if (event.getEventOwner().getBuyer() != null) {
				event.getEventOwner().getBuyer().getFullName();
				event.getEventOwner().getBuyer().getLine1();
				event.getEventOwner().getBuyer().getLine2();
				event.getEventOwner().getBuyer().getCity();
				if (event.getEventOwner().getBuyer().getState() != null) {
					event.getEventOwner().getBuyer().getState().getStateName();
					if (event.getEventOwner().getBuyer().getState().getCountry() != null) {
						event.getEventOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}
		}
		return event;
	}

	private List<SupplierMaskingPojo> buildSupplierMaskingData(List<EventSupplier> supplierList, String eventId) {
		List<SupplierMaskingPojo> supplierMaskingList = new ArrayList<SupplierMaskingPojo>();
		RfqEvent event = getRfqEventByeventId(eventId);
		List<RfqEnvelop> env = rfqEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFQ);
		for (EventSupplier eventSupplier : supplierList) {
			if (eventSupplier.getSupplier() != null) {
				SupplierMaskingPojo pojo = new SupplierMaskingPojo();
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					pojo.setSupplierName(MaskUtils.maskName("SUPPLIER", eventSupplier.getSupplier().getId(), eventId));
				} else {
					pojo.setSupplierName(eventSupplier.getSupplier() != null ? eventSupplier.getSupplier().getCompanyName() : "");
				}
				List<SupplierMaskingCodePojo> supplierMaskingCodes = new ArrayList<SupplierMaskingCodePojo>();
				for (RfqEnvelop rfqEnvelop : env) {
					SupplierMaskingCodePojo codePojo = new SupplierMaskingCodePojo();
					codePojo.setEnevelopeName(rfqEnvelop.getEnvelopTitle());
					codePojo.setMakedCode(MaskUtils.maskName(rfqEnvelop.getPreFix(), eventSupplier.getSupplier().getId(), rfqEnvelop.getId()));
					supplierMaskingCodes.add(codePojo);
				}
				pojo.setSupplierMaskingCodes(supplierMaskingCodes);
				if (SubmissionStatusType.COMPLETED == eventSupplier.getSubmissionStatus()) {
					supplierMaskingList.add(pojo);
				}

			}
		}
		/*
		 * Collections.sort(supplierMaskingList, new Comparator<SupplierMaskingPojo>() { public int
		 * compare(SupplierMaskingPojo o1, SupplierMaskingPojo o2) { if (o1.getSupplierName() == null ||
		 * o2.getSupplierName() == null) { return 0; } return o1.getSupplierName().compareTo(o2.getSupplierName()); }
		 * });
		 */
		return supplierMaskingList;
	}

	@Override
	public List<RfqEvent> getAllRfqEventByTenantId(String tenantId) {
		return rfqEventDao.getAllRfqEventByTenantId(tenantId);
	}

	@Override
	public Event getEventByEventRefranceNo(String eventRefranceNo, String tenantId) {
		return rfqEventDao.getEventByEventRefranceNo(eventRefranceNo, tenantId);
	}

	@Override
	public EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId) {
		return rfqEventDao.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
	}

	@Override
	public EventPojo loadSupplierEventPojoForSummeryById(String eventId) {
		return rfqEventDao.loadSupplierEventPojoForSummeryById(eventId);
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		return rfqEventDao.getInvitedSupplierCount(eventId);
	}

	@Override
	public long getParticepatedSupplierCount(String eventId) {
		return rfqEventDao.getParticepatedSupplierCount(eventId);
	}

	@Override
	public long getSubmitedSupplierCount(String eventId) {
		return rfqEventDao.getSubmitedSupplierCount(eventId);
	}

	@Override
	public List<RfqSupplierBq> getLowestSubmissionPrice(String eventId) {
		return rfqEventDao.getLowestSubmissionPrice(eventId);
	}

	@Override
	public List<EventSupplierPojo> getSuppliersByStatus(String eventId) {
		return rfqEventDao.getSuppliersByStatus(eventId);

	}

	/*
	 * @Override public RfqSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId) { return
	 * rfqEventDao.getFinalBidsForSuppliers(eventId, supplierId); }
	 */
	@Override
	public RfqEnvelop getBqForEnvelope(String envelopeId) {
		return rfqEventDao.getBqForEnvelope(envelopeId);
	}

	@Override
	public EventPojo getRfqForPublicEventByeventId(String eventId) {
		return rfqEventDao.getRfqForPublicEventByeventId(eventId);
	}

	@Override
	public List<IndustryCategory> getIndustryCategoriesForRfqById(String eventId) {
		return rfqEventDao.getIndustryCategoriesForRfqById(eventId);
	}

	@Override
	public JasperPrint getPublicEventSummaryPdf(EventPojo event) {
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> summary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = buyerSettingsService.getBuyerTimeZoneByTenantId(event.getTenantId());
		if (StringUtils.checkString(strTimeZone).length() > 0) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		SimpleDateFormat ddsdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(timeZone);
		ddsdf.setTimeZone(timeZone);
		// Virtualizar - To increase the performance
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			// Resource resource = applicationContext.getResource("classpath:reports/GenerateEvaluationSummary.jasper");
			Resource resource = applicationContext.getResource("classpath:reports/PublicEventsSummary.jasper");

			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(StringUtils.checkString(event.getEventName()));
			eventDetails.setEventStart(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			eventDetails.setType("RFQ");
			eventDetails.setCompanyName(event.getCompanyName());
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			eventDetails.setVisibility(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			eventDetails.setPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			eventDetails.setDiliveryDate(event.getEventDeliveryDate() != null ? ddsdf.format(event.getEventDeliveryDate()) : "N/A");

			List<IndustryCategory> industryCategories = rfqEventDao.getIndustryCategoriesForRfqById(event.getId());
			if (CollectionUtil.isNotEmpty(industryCategories)) {
				List<IndustryCategoryPojo> categoryNames = new ArrayList<IndustryCategoryPojo>();
				for (IndustryCategory industryCategory : industryCategories) {
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(industryCategory.getName());
					categoryNames.add(ic);
				}
				eventDetails.setIndustryCategoryNames(categoryNames);
			}

			boolean siteVisit = rfqEventMeetingDao.isSiteVisitExist(event.getId());
			eventDetails.setSiteVisit(siteVisit);
			// Event Contact Details.
			List<RfqEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfqEventContact contact : eventContacts) {
					EvaluationContactsPojo contactPojo = new EvaluationContactsPojo();
					contactPojo.setContactName(contact.getContactName());
					contactPojo.setContactNumber(contact.getContactNumber());
					contactPojo.setMobileNumber(contact.getMobileNumber());
					contactPojo.setComunicationEmail(contact.getComunicationEmail());
					contactList.add(contactPojo);
				}
			}
			eventDetails.setContacts(contactList);

			// Commercial Information.
			if (StringUtils.checkString(event.getBaseCurrency()).length() > 0 && StringUtils.checkString(event.getCurrencyCode()).length() > 0) {
				eventDetails.setBaseCurrency(event.getCurrencyCode() + " - " + event.getBaseCurrency());
			}
			eventDetails.setPaymentTerm(event.getPaymentTerm());

			// Fetch List of suppliers BQ

			List<Bq> bqs = rfqEventBqDao.findBqbyEventId(event.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
			if (CollectionUtil.isNotEmpty(bqs)) {
				for (Bq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<RfqBqItem> bqItems = rfqBqItemDao.findBqItemsForBq(item.getId());
					List<EvaluationBqItemPojo> evaluationBqItem = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfqBqItem bqItem : bqItems) {
							EvaluationBqItemPojo bi = new EvaluationBqItemPojo();
							bi.setLevel(bqItem.getLevel() + "." + bqItem.getOrder());
							bi.setItemName(bqItem.getItemName());
							bi.setDescription(bqItem.getItemDescription());
							bi.setUom(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
							bi.setQuantity(bqItem.getQuantity());
							evaluationBqItem.add(bi);
						}
					}
					bqPojo.setBqItems(evaluationBqItem);
					allBqs.add(bqPojo);

				}
			}
			eventDetails.setBqs(allBqs);
			summary.add(eventDetails);

			parameters.put("PUBIC_EVENT_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Public RFQ Event Summary PDF Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				// virtualizer.cleanup();
			}
		}
		return jasperPrint;
	}

	@Override
	public int updateEventPushedDate(String eventId) {
		return rfqEventDao.updateEventPushedDate(eventId);
	}

	@Override
	public Double getAvarageBidPriceSubmitted(String id) {
		return rfqEventDao.getAvarageBidPriceSubmitted(id);
	}

	@Override
	public List<String> getEventTeamMember(String eventId) {

		return rfqEventDao.getEventTeamMember(eventId);
	}

	@Override
	public int updatePrPushDate(String eventId) {
		return rfqEventDao.updatePrPushDate(eventId);
	}

	@Override
	public int updateEventAward(String eventId) {
		return rfqEventDao.updateEventAward(eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String eventId, EventStatus status) {
		rfqEventDao.updateImmediately(eventId, status);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlagImmediately(String eventId) {
		rfqEventDao.updateEventStartMessageFlag(eventId);
	}

	@Override
	public List<RfqEvent> getAllEventByTenantIdInitial(String loggedInUserTenantId, String loggedInUser) throws SubscriptionException {
		Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		if (buyer != null && buyer.getBuyerPackage() != null) {
			BuyerPackage bp = buyer.getBuyerPackage();
			if (bp.getEventLimit() != null) {
				if (bp.getNoOfEvents() == null) {
					bp.setNoOfEvents(0);
				}
				if (bp.getNoOfEvents() >= bp.getEventLimit()) {
					throw new SubscriptionException("You have reached your subscription limitEvent. limit of " + bp.getEventLimit() + " reached.");
				} else if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
					throw new SubscriptionException("Your Subscription has Expired.");
				}
			}
		}
		List<RfqEvent> list = rfqEventDao.getAllRfqEventByTenantIdInitial(loggedInUserTenantId, loggedInUser);
		for (RfqEvent rfqEvent : list) {
			if (rfqEvent.getTemplate() != null) {
                rfqEvent.setTemplateActive(rfqEvent.getTemplate().getStatus() == Status.INACTIVE);
			}

			if (CollectionUtil.isNotEmpty(rfqEvent.getIndustryCategories()))
				rfqEvent.setIndustryCategory(rfqEvent.getIndustryCategories().get(0));
		}
		return list;

	}

	@Override
	public List<RfqEvent> getAllEventByTenantIdAndRfqTemplateId(String loggedInUserTenantId, String loggedInUser,String rfqTemplateId) throws SubscriptionException {
		Buyer buyer = buyerService.findBuyerById(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		if (buyer != null && buyer.getBuyerPackage() != null) {
			BuyerPackage bp = buyer.getBuyerPackage();
			if (bp.getEventLimit() != null) {
				if (bp.getNoOfEvents() == null) {
					bp.setNoOfEvents(0);
				}
				if (bp.getNoOfEvents() >= bp.getEventLimit()) {
					throw new SubscriptionException("You have reached your subscription limitEvent. limit of " + bp.getEventLimit() + " reached.");
				} else if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
					throw new SubscriptionException("Your Subscription has Expired.");
				}
			}
		}
		List<RfqEvent> list = rfqEventDao.getAllEventByTenantIdAndRfqTemplateId(loggedInUserTenantId, loggedInUser,rfqTemplateId);
		for (RfqEvent rfqEvent : list) {
			if (rfqEvent.getTemplate() != null) {
				rfqEvent.setTemplateActive(rfqEvent.getTemplate().getStatus() == Status.INACTIVE);
			}

			if (CollectionUtil.isNotEmpty(rfqEvent.getIndustryCategories()))
				rfqEvent.setIndustryCategory(rfqEvent.getIndustryCategories().get(0));
		}
		return list;

	}

	@Override
	public Integer getCountByEventId(String eventId) {
		return rfqEventDao.getCountByEventId(eventId);
	}

	@Override
	public Event loadRfqEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUser, List<User> suspApprovalUsers) throws CloneNotSupportedException {
		RfqEvent event = rfqEventDao.findByEventId(eventId);
		if (event != null) {
			if (event.getParticipationFeeCurrency() != null) {
				event.getParticipationFeeCurrency().getCurrencyName();
			}
			if (event.getDepositCurrency() != null) {
				event.getDepositCurrency().getCurrencyName();
			}
			if (event.getIndustryCategory() != null) {
				event.getIndustryCategory().getCode();
			}
			if (event.getUnMaskedUser() != null) {
				event.getUnMaskedUser().getName();
				model.addAttribute("unMaskUser", event.getUnMaskedUser());
			}

			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfqUnMaskedUser teamMembers : event.getUnMaskedUsers()) {
					teamMembers.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RfqEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfqEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}

			if (event.getEventOwner().getBuyer() != null) {
				event.getEventOwner().getBuyer().getFullName();
				if (event.getEventOwner().getBuyer().getState() != null) {
					event.getEventOwner().getBuyer().getState().getStateName();
					if (event.getEventOwner().getBuyer().getState().getCountry() != null) {
						event.getEventOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEventEditors())) {
				for (User user : event.getEventEditors()) {
					user.getLoginId();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getEventViewers())) {
				for (User user : event.getEventViewers()) {
					user.getLoginId();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RfqTeamMember approver : event.getTeamMembers()) {
					approver.getUser().getName();
					assignedTeamMembers.add((User) approver.getUser().clone());
				}
			}
			List<User> userList = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfqEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfqApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();

							User us = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());

							if (!userList.contains(us)) {
								userList.add(us);
								approvalUser.add(us);
							}
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfqEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfqSuspensionApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();

							User us = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());

							if (!suspApprovalUsers.contains(us)) {
								suspApprovalUsers.add(us);
							}
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				for (IndustryCategory indCat : event.getIndustryCategories()) {
					indCat.getCode();
				}
			}
			if (event.getEvaluationProcessDeclaration() != null) {
				event.getEvaluationProcessDeclaration().getTitle();

			}
			if (event.getSupplierAcceptanceDeclaration() != null) {
				event.getSupplierAcceptanceDeclaration().getTitle();
			}
			if (event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getDescription();
			}
			buildRfqEventModal(model, eventId, event, userList);
		}
		return event;
	}

	private void buildRfqEventModal(Model model, String eventId, Event event, List<User> userList) {
		event.setEventPublishTime(event.getEventPublishDate());
		if (event.getMinimumSupplierRating() != null) {
			event.setMinimumSupplierRating(event.getMinimumSupplierRating());
		}
		if (event.getMaximumSupplierRating() != null) {
			event.setMaximumSupplierRating(event.getMaximumSupplierRating());
		}
		RfqEventContact eventPContact1 = new RfqEventContact();
		eventPContact1.setEventId(event.getId());
		LOG.info("Approval List : " + (userList != null ? userList.size() : 0));
		// model.addAttribute("userList1", userList);
		model.addAttribute("eventContact", eventPContact1);
		model.addAttribute("eventContactsList", ((RfqEvent) event).getEventContacts());
		model.addAttribute("reminderList", rfqEventService.getAllRfqEventReminderForEvent(eventId));
	}

	@Override
	public Event getSimpleEventDetailsById(String eventId) {
		return rfqEventDao.getSimpleEventDetailsById(eventId);
	}

	@Override
	public RfqEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId) {
		return rfqEventDao.getPlainEventByFormattedEventIdAndTenantId(eventId, tenantId);
	}

	@Override
	public List<RfqEventAwardAudit> getRfqEventAwardByEventId(String eventId) {
		return rfqEventDao.getRfqEventAwardByEventId(eventId);
	}

	@Override
	public JasperPrint getEventAuditPdf(RfqEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rfqEventDao.findByEventId(event.getId());
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> summary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		/*
		 * String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY); if (strTimeZone != null) {
		 * timeZone = TimeZone.getTimeZone(strTimeZone); }
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/GenerateEventAuditPdf.jasper");
			/* String imgPath = context.getRealPath("resources/images"); */
			File jasperfile = resource.getFile();
			EvaluationPojo eventDetails = new EvaluationPojo();
			// Event Audit Details

			eventDetails.setReferenceId(event.getEventId());
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(event.getEventName());
			eventDetails.setEventOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
			String owner = "";
			if (event.getEventOwner() != null) {
				owner += event.getEventOwner().getName() + "\r\n" + event.getEventOwner().getCommunicationEmail() + "\r\n" + StringUtils.checkString(event.getEventOwner().getPhoneNumber());
			}
			eventDetails.setOwner(owner);
			eventDetails.setEventStart(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			eventDetails.setEmail(event.getEventOwner().getCommunicationEmail());
			eventDetails.setType("RFQ");
			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<RfqEventAudit> eventAudit = rfqEventAuditDao.getRfqEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RfqEventAudit ra : eventAudit) {
					EvaluationAuditPojo audit = new EvaluationAuditPojo();
					audit.setAuctionDate(ra.getActionDate() != null ? sdf.format(ra.getActionDate()) : "");
					audit.setAuctionBy(ra.getActionBy() != null ? ra.getActionBy().getName() : "");
					audit.setAuction(ra.getAction().name());
					audit.setDescription(ra.getDescription());
					auditList.add(audit);
				}
			}
			eventDetails.setAuditDetails(auditList);
			eventDetails.setIsMask(false);
			summary.add(eventDetails);
			parameters.put("EVALUATION_SUMMARY", summary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (

		Exception e) {
			LOG.error("Could not generate Evaluation Summary PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public Boolean isDefaultPreSetEnvlope(String eventId) {
		return rfqEventDao.isDefaultPreSetEnvlope(eventId);
	}

	@Override
	public void sendEvaluationCompletedPrematurelyNotification(User actionBy, Event event) {
		String url = APP_URL + "/buyer/" + RfxTypes.RFQ.name() + "/eventSummary/" + event.getId();
		String msg = "\"" + actionBy.getName() + "\" has concluded evaluation prematurely for \"" + event.getEventName() + "\"";
		String timeZone = "GMT+8:00";
		String subject = "Evaluation Concluded Prematurely";

		User eventOwner = rfqEventDao.getPlainEventOwnerByEventId(event.getId());
		timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);

		// Send to event owner
		sendNotification(event, url, msg, eventOwner, timeZone, subject);

		// send to Associate Owners
		List<EventTeamMember> teamMembers = rfqEventDao.getPlainTeamMembersForEvent(event.getId());
		if (CollectionUtil.isNotEmpty(teamMembers)) {
			for (EventTeamMember member : teamMembers) {
				if (TeamMemberType.Associate_Owner == member.getTeamMemberType()) {
					sendNotification(event, url, msg, member.getUser(), timeZone, subject);
				}
			}
		}

	}

	/**
	 * @param event
	 * @param url
	 * @param msg
	 * @param mailTo
	 * @param timeZone
	 * @param subject
	 */
	private void sendNotification(Event event, String url, String msg, User mailTo, String timeZone, String subject) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("msg", msg);
			map.put("date", df.format(new Date()));
			map.put("userName", mailTo.getName());
			map.put("eventType", RfxTypes.RFQ.getValue());
			map.put("businessUnit", StringUtils.checkString(rfqEventDao.findBusinessUnitName(event.getId())));
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			if(mailTo.getEmailNotifications())
			notificationService.sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.ENVELOPE_COMPLETED_TEMPLATE);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for premature evaluation completion action : " + e.getMessage(), e);
		}

		try {
			sendDashboardNotification(mailTo, url, subject, msg);
		} catch (Exception e) {
			LOG.error("Error While Sending notification for premature evaluation completion action : " + e.getMessage(), e);
		}
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	@Override
	public RfqEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		RfqEvaluationConclusionUser usr = rfqEventDao.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
		if (usr != null && usr.getFileData() != null) {
			@SuppressWarnings("unused")
			long len = usr.getFileData().length;
		}
		return usr;
	}

	@Override
	public JasperPrint generatePdfforEvaluationConclusion(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationConclusionPojo> rfqSummary = new ArrayList<EvaluationConclusionPojo>();
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		parameters.put("generatedOn", sdf.format(new Date()));

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationConclusionReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationConclusionPojo summary = new EvaluationConclusionPojo();
			RfqEvent event = rfqEventDao.getPlainEventWithOwnerById(eventId);

			if (event != null) {

				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				summary.setType("Request For Quotation");
				summary.setCreatedDate(sdf.format(event.getCreatedDate()));
				summary.setEventStart(sdf.format(event.getEventStart()));
				summary.setEventEnd(sdf.format(event.getEventEnd()));
				summary.setPublishDate(sdf.format(event.getEventPublishDate()));
				summary.setEnvelopeEvaluatedCount(event.getEvaluationConclusionEnvelopeEvaluatedCount());
				summary.setEnvelopeNonEvaluatedCount(event.getEvaluationConclusionEnvelopeNonEvaluatedCount());
				summary.setDisqualifiedSupplierCount(event.getEvaluationConclusionDisqualifiedSupplierCount());
				summary.setRemainingSupplierCount(event.getEvaluationConclusionRemainingSupplierCount());

				if (event.getEventOwner() != null) {
					summary.setEventOwner(event.getEventOwner().getLoginId() + " (" + (StringUtils.checkString(event.getEventOwner().getCommunicationEmail()).length() > 0 ? event.getEventOwner().getCommunicationEmail() + "," : "") + "Tel:" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : "N/A") + ")");
				}

				List<RfqEnvelop> envelopList = rfqEnvelopDao.getEnvelopListByEventId(eventId);
				String evalutedEnvelop = "";
				String nonEvalutedEnvelop = "";
				if (CollectionUtil.isNotEmpty(envelopList)) {
					for (RfqEnvelop envelop : envelopList) {
						if (Boolean.TRUE == envelop.getEvaluationCompletedPrematurely()) {
							nonEvalutedEnvelop += envelop.getEnvelopTitle() + ",";
						} else {
							evalutedEnvelop += envelop.getEnvelopTitle() + ",";
						}
					}
				}
				if (StringUtils.checkString(evalutedEnvelop).length() > 0) {
					evalutedEnvelop = StringUtils.checkString(evalutedEnvelop).substring(0, StringUtils.checkString(evalutedEnvelop).length() - 1);
				}
				if (StringUtils.checkString(nonEvalutedEnvelop).length() > 0) {
					nonEvalutedEnvelop = StringUtils.checkString(nonEvalutedEnvelop).substring(0, StringUtils.checkString(nonEvalutedEnvelop).length() - 1);
				}
				summary.setEnvelopEvaluted(StringUtils.checkString(evalutedEnvelop).length() > 0 ? " (" + evalutedEnvelop + ")" : "");
				summary.setEnvelopNonEvaluted(StringUtils.checkString(nonEvalutedEnvelop).length() > 0 ? " (" + nonEvalutedEnvelop + ")" : "");

				List<RfqEvaluationConclusionUser> evaluationConclusionUserList = rfqEventDao.findEvaluationConclusionUsersByEventId(eventId);
				String conclusionUser = "";

				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					Log.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RfqEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
						LOG.info("Conclusion User");
						if (evaluationConclusionUser.getUser() != null) {
							conclusionUser += evaluationConclusionUser.getUser().getName() + " (" + evaluationConclusionUser.getUser().getLoginId() + ")\r\n";
						}
						EvaluationConclusionUsersPojo user = new EvaluationConclusionUsersPojo();
						user.setUserIndex(userIndex++);
						if (evaluationConclusionUser.getUser() != null) {
							user.setUserName(evaluationConclusionUser.getUser().getName());
						}
						user.setConcludedDate(sdf.format(evaluationConclusionUser.getConcludedTime()));
						user.setRemark(StringUtils.checkString(evaluationConclusionUser.getRemarks()).length() > 0 ? evaluationConclusionUser.getRemarks() : "N/A");
						user.setFileName(StringUtils.checkString(evaluationConclusionUser.getFileName()).length() > 0 ? evaluationConclusionUser.getFileName() : "N/A");
						if (StringUtils.checkString(evaluationConclusionUser.getFileDesc()).length() > 0) {
							user.setFileDescription(StringUtils.checkString(evaluationConclusionUser.getFileDesc()).length() > 0 ? evaluationConclusionUser.getFileDesc() : "N/A");
						}
						evaluationConclusionList.add(user);
					}
				}
				summary.setEvaluationConclusionUsersList(evaluationConclusionList);
				summary.setEvaluationConclusionOwners(StringUtils.checkString(conclusionUser).length() > 0 ? conclusionUser : "");
				rfqSummary.add(summary);
			}
			parameters.put("EVALUATION_CONCLUSION", rfqSummary);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfqSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation conclusion  Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RfqEvent event, String name, String feeReference, String timezone) {
		try {
			LOG.info("Send Email to: " + recipientEmail + " for subject: " + subject + " with description:" + description + " and name: " + name + " and timezone: " + timezone);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", name);
			map.put("description", description);
			map.put("eventName", event.getEventName());
			map.put("eventFeeReference", feeReference);
			map.put("eventID", event.getEventId());
			map.put("eventParticipationAmount", (event.getParticipationFeeCurrency().getCurrencyCode() + " " + event.getParticipationFees().setScale(2, RoundingMode.HALF_UP)));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			if (StringUtils.checkString(timezone).length() == 0) {
				timezone = "GMT+8:00";
			}
			df.setTimeZone(TimeZone.getTimeZone(timezone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", APP_URL + "/login");
			notificationService.sendEmail(recipientEmail, subject, map, Global.RFX_EVENTPARTICIPATION_FEE_RECEIPT_TEMPLATE);
		} catch (Exception e) {
			LOG.error("Error while sending email: " + e.getMessage(), e);
		}
	}

	@Override
	public long getRfqEventCountByTenantId(String searchVal, String tenantId, String userId) {
		return rfqEventDao.getRfqEventCountByTenantId(searchVal, tenantId, userId, null);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEvent updateEventSuspensionApproval(RfqEvent event, User user) {

		RfqEvent persistObj = rfqEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfqEventSuspensionApproval approvalRequest : persistObj.getSuspensionApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfqSuspensionApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			int level = 1;
			for (RfqEventSuspensionApproval app : event.getSuspensionApprovals()) {
				app.setEvent(event);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfqSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
						approvalUser.setApproval(app);
						approvalUser.setId(null);

						// If db existing user list is empty, then it means this is a new level which is not existing in
						// database yet.
						// therefore we need to add all user list coming from frontend for current level as new users
						if (CollectionUtil.isEmpty(existingUserList)) {
							auditMessages.add("Approval Level " + app.getLevel() + " User " + approvalUser.getUser().getName() + " has been added as Approver");
						} else {
							// If db existing user list does not contain the user coming from frontend, then it has been
							// added as new user for current level
							if (!existingUserList.contains(approvalUser.getUser().getName())) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + approvalUser.getUser().getName() + " has been added as Approver");
							}
						}

						levelUsers.add(approvalUser.getUser().getName());
					}
					/*
					 * Loop through the db existing user list for the current level and check if they exist in the
					 * userlist coming from frontend.
					 */
					if (CollectionUtil.isNotEmpty(existingUserList)) {
						for (String existing : existingUserList) {
							if (!levelUsers.contains(existing)) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + existing + " has been removed as Approver");
							}
						}
					}
				}
				// to check if approval type is changed
				if (existingType != null && existingType != app.getApprovalType()) {
					auditMessages.add("Approval Level " + app.getLevel() + " Type changed from " + (existingType == ApprovalType.OR ? "Any" : "All") + " to " + (app.getApprovalType() == ApprovalType.OR ? "Any" : "All"));
				}
			}
		} else {
			LOG.warn("Approval levels is empty.");
		}

		while (CollectionUtil.isNotEmpty(existingUsers.get(newLevel))) {
			for (String existing : existingUsers.get(newLevel)) {
				auditMessages.add("Approval Level " + newLevel + " User " + existing + " has been removed as Approver");
			}
			newLevel++;
		}

		persistObj.setSuspensionApprovals(event.getSuspensionApprovals());
		persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
		persistObj.setModifiedDate(new Date());
		persistObj = rfqEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfqEventAudit audit = new RfqEventAudit(persistObj, user, new Date(), AuditActionType.Update, auditMessage);
				rfqEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" + persistObj.getEventId() + "' ." + auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			} catch (Exception e) {
				LOG.error("Error saving Sourcing Request Audit for change of approvers : " + e.getMessage(), e);
			}
		}
		return persistObj;
	}

	@Override
	public void downloadRfqEvaluatorDocument(String id, HttpServletResponse response) {
		RfqEvaluatorUser docs = rfqEvaluatorUserDao.getEvaluationDocument(id);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		try {
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			response.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadRfqLeadEvaluatorDocument(String envelopId, HttpServletResponse response) {
		RfqEnvelop docs = rfqEnvelopDao.getEvaluationDocument(envelopId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		try {
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			response.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private List<ProductContractReminder> createDefaultReminderList(List<ProductContractReminder> productRemindersList, Date contractStartDate, Date contractEndDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(contractEndDate);
		cal.add(Calendar.DATE, -180);
		Date reminderDate = cal.getTime();
		if (reminderDate.after(contractStartDate)) {
			LOG.info("Six month default reminder");
			ProductContractReminder newReminder = new ProductContractReminder();
			newReminder.setReminderDate(reminderDate);
			newReminder.setInterval(180);
			productRemindersList.add(newReminder);
		}
		cal = Calendar.getInstance();
		cal.setTime(contractEndDate);
		cal.add(Calendar.DATE, -90);
		reminderDate = cal.getTime();
		if (reminderDate.after(contractStartDate)) {
			LOG.info("three month default reminder");
			ProductContractReminder newReminder = new ProductContractReminder();
			newReminder.setInterval(90);
			newReminder.setReminderDate(reminderDate);
			productRemindersList.add(newReminder);
		}
		cal = Calendar.getInstance();
		cal.setTime(contractEndDate);
		cal.add(Calendar.DATE, -30);
		reminderDate = cal.getTime();
		if (reminderDate.after(contractStartDate)) {
			LOG.info("one month default reminder");
			ProductContractReminder newReminder = new ProductContractReminder();
			newReminder.setReminderDate(reminderDate);
			newReminder.setInterval(30);
			productRemindersList.add(newReminder);
		}
		return productRemindersList;
	}

	@Override
	@Transactional(readOnly = false)
	public void revertEventAwardStatus(String eventId) {
		rfqEventDao.revertEventAwardStatus(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> createSpFormFromAward(RfqEventAward rfqEventAward, String templateId, String eventId, String userId, User loggedInUser) throws ApplicationException {
		RfqEvent rfqEvent = rfqEventService.getRfqEventByeventId(eventId);
		SupplierPerformanceTemplate spTemplate = spTemplateService.getSupplierPerformanceTemplatebyId(templateId);
		User formOwner = userService.getUsersById(userId);
		List<String> formIds = new ArrayList<String>();
		try {

			List<String> supplierIds = new ArrayList<String>();
			for (RfqEventAwardDetails rfxAward : rfqEventAward.getRfxAwardDetails()) {
				if (rfxAward.getSupplier() != null && StringUtils.checkString(rfxAward.getSupplier().getId()).length() > 0) {
					if (!supplierIds.contains(rfxAward.getSupplier().getId())) {
						supplierIds.add(rfxAward.getSupplier().getId());
					}
				}
			}
			LOG.info("Total Suppliers : " + supplierIds.size());
			for (String supplierId : supplierIds) {
				Supplier sup = new Supplier();
				sup.setId(supplierId);
				SupplierPerformanceForm form = new SupplierPerformanceForm();
				String formId = eventIdSettingsDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "SP", rfqEvent.getBusinessUnit());
				form.setFormId(formId);
				form.setFormName(spTemplate.getTemplateName());
				form.setFormOwner(formOwner);
				form.setCreatedBy(SecurityLibrary.getLoggedInUser());
				form.setCreatedDate(new Date());
				form.setEventId(eventId);
				form.setEventType(RfxTypes.RFQ);
				form.setAwardedSupplier(sup);
				if (Boolean.TRUE == spTemplate.getProcurementCategoryVisible() && Boolean.TRUE == spTemplate.getProcurementCategoryDisabled()) {
					form.setProcurementCategory(spTemplate.getProcurementCategory());
				} else {
					if (rfqEvent.getProcurementCategories() != null) {
						form.setProcurementCategory(rfqEvent.getProcurementCategories());
					} else {
						// if it is available in template then use it.
						form.setProcurementCategory(spTemplate.getProcurementCategory());
					}
				}
				form.setReferenceNumber(rfqEvent.getEventId());
				form.setReferenceName(rfqEvent.getEventName());
				form.setTemplate(spTemplateService.getSupplierPerformanceTemplatebyId(templateId));
				form.setBusinessUnit(rfqEvent.getBusinessUnit());
				form.setFormStatus(SupplierPerformanceFormStatus.DRAFT);
				form.setBuyer(loggedInUser.getBuyer());
				form.copyCriteriaDetails(form, spTemplate);

				if (CollectionUtil.isNotEmpty(form.getCriteria())) {
					SupplierPerformanceFormCriteria parent = null;
					for (SupplierPerformanceFormCriteria criteria : form.getCriteria()) {
						criteria.setForm(form);
						if (criteria.getOrder() == 0) {
							parent = criteria;
						}
						if (criteria.getOrder() != 0) {
							criteria.setParent(parent);
						}
					}
				}

				form = spFormService.saveSupplierPerformanceForm(form);
				formIds.add(formId);

				// Send Notification
				try {
					String url = APP_URL + "/buyer/editSupplierPerformanceEvaluation/" + form.getId();
					Supplier supp = supplierDao.findPlainSupplierUsingConstructorById(supplierId);
					sendFormCreatedNotification(formOwner, url, form, supp);
				} catch (Exception e) {
					LOG.error("Error Sending Notification : " + e.getMessage(), e);
				}

				try {
					SupplierPerformanceAudit audit = new SupplierPerformanceAudit();
					audit.setAction(SupplierPerformanceAuditActionType.Create);
					audit.setActionBy(loggedInUser);
					audit.setActionDate(new Date());
					audit.setForm(form);
					audit.setDescription(messageSource.getMessage("sp.form.audit.create", new Object[] { form.getFormId() }, Global.LOCALE));
					audit.setOwner(Boolean.TRUE);
					audit.setEvaluator(Boolean.TRUE);
					audit.setApprover(Boolean.TRUE);

					formAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while saving audit trail: " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
		return formIds;
	}

	private void sendFormCreatedNotification(User user, String url, SupplierPerformanceForm form, Supplier supplier) {

		String mailTo = user.getCommunicationEmail();
		String subject = "You have been assigned as Form Owner for Supplier Performance Evaluation Form";
		HashMap<String, Object> map = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();

		String timeZoneStr = "GMT+8:00";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		try {
			BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(user.getTenantId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZoneStr = settings.getTimeZone().getTimeZone();
			} else {
				timeZoneStr = "GMT+8:00";
			}
			timeZone = TimeZone.getTimeZone(timeZoneStr);
			sdf.setTimeZone(timeZone);
			df.setTimeZone(timeZone);
		} catch (Exception e) {
		} finally {
			sdf.setTimeZone(timeZone);
			df.setTimeZone(timeZone);
		}
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("formId", form.getFormId());
		map.put("supplier", supplier.getCompanyName());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(user.getEmailNotifications())
		notificationService.sendEmail(mailTo, subject, map, Global.ASSIGNED_FORM_OWNER_FOR_SUPPLIER_PERFORMANCE_FORM);

	}

	private void sendContractCreatedNotification(User user, String url, ProductContract pc) {

		String mailTo = user.getCommunicationEmail();
		String subject = "You have been assigned as Contract Creator";
		HashMap<String, Object> map = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();

		String timeZoneStr = "GMT+8:00";
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		try {
			BuyerSettings settings = buyerSettingsDao.getBuyerSettingsByTenantId(user.getTenantId());
			if (settings != null && settings.getTimeZone() != null) {
				timeZoneStr = settings.getTimeZone().getTimeZone();
			} else {
				timeZoneStr = "GMT+8:00";
			}
			timeZone = TimeZone.getTimeZone(timeZoneStr);
			sdf.setTimeZone(timeZone);
		} catch (Exception e) {
		} finally {
			sdf.setTimeZone(timeZone);
		}
		map.put("date", sdf.format(new Date()));
		map.put("userName", user.getName());
		map.put("contractId", pc.getContractId());
		map.put("referenceNumber", pc.getContractReferenceNumber());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(user.getEmailNotifications()) {
			notificationService.sendEmail(mailTo, subject, map, Global.CONTRACT_CREATION_MAIL);
		}
	}
}
