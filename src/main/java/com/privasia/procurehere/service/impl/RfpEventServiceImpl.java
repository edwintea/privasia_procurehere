package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.RfpSupplierSorDao;
import com.privasia.procurehere.core.dao.RfpSupplierSorItemDao;
import com.privasia.procurehere.core.pojo.EnvelopeSorPojo;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RfpSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfpSupplierSorItemService;
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

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.ContractAuditDao;
import com.privasia.procurehere.core.dao.CostCenterDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.DeclarationDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.GroupCodeDao;
import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.dao.NaicsCodesDao;
import com.privasia.procurehere.core.dao.ProcurementCategoriesDao;
import com.privasia.procurehere.core.dao.ProcurementMethodDao;
import com.privasia.procurehere.core.dao.ProductContractDao;
import com.privasia.procurehere.core.dao.ProductContractItemsDao;
import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.dao.RfpSorDao;
import com.privasia.procurehere.core.dao.RfpSorItemDao;
import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.dao.RfpCqDao;
import com.privasia.procurehere.core.dao.RfpCqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfpCqItemDao;
import com.privasia.procurehere.core.dao.RfpCqOptionDao;
import com.privasia.procurehere.core.dao.RfpDocumentDao;
import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RfpEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RfpEventAuditDao;
import com.privasia.procurehere.core.dao.RfpEventAwardDao;
import com.privasia.procurehere.core.dao.RfpEventAwardDetailsDao;
import com.privasia.procurehere.core.dao.RfpEventContactDao;
import com.privasia.procurehere.core.dao.RfpEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpEventMeetingDao;
import com.privasia.procurehere.core.dao.RfpEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpEventTimeLineDao;
import com.privasia.procurehere.core.dao.RfpReminderDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.RfpSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventTimelineType;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceAuditActionType;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.AuctionEvaluationDocumentPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EnvelopeBqPojo;
import com.privasia.procurehere.core.pojo.EnvelopeCqPojo;
import com.privasia.procurehere.core.pojo.EvaluationApprovalsPojo;
import com.privasia.procurehere.core.pojo.EvaluationAprovalUsersPojo;
import com.privasia.procurehere.core.pojo.EvaluationAuctionBiddingPojo;
import com.privasia.procurehere.core.pojo.EvaluationAuditPojo;
import com.privasia.procurehere.core.pojo.EvaluationBidderContactPojo;
import com.privasia.procurehere.core.pojo.EvaluationBiddingIpAddressPojo;
import com.privasia.procurehere.core.pojo.EvaluationBiddingPricePojo;
import com.privasia.procurehere.core.pojo.EvaluationBqItemComments;
import com.privasia.procurehere.core.pojo.EvaluationBqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationBqPojo;
import com.privasia.procurehere.core.pojo.EvaluationCommentsPojo;
import com.privasia.procurehere.core.pojo.EvaluationConclusionPojo;
import com.privasia.procurehere.core.pojo.EvaluationConclusionUsersPojo;
import com.privasia.procurehere.core.pojo.EvaluationContactsPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqItemSupplierCommentsPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqItemSupplierPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqPojo;
import com.privasia.procurehere.core.pojo.EvaluationDeclarationAcceptancePojo;
import com.privasia.procurehere.core.pojo.EvaluationDocumentPojo;
import com.privasia.procurehere.core.pojo.EvaluationEnvelopPojo;
import com.privasia.procurehere.core.pojo.EvaluationMeetingContactsPojo;
import com.privasia.procurehere.core.pojo.EvaluationMeetingPojo;
import com.privasia.procurehere.core.pojo.EvaluationPojo;
import com.privasia.procurehere.core.pojo.EvaluationSupplierBidsPojo;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersPojo;
import com.privasia.procurehere.core.pojo.EvaluationTeamsPojo;
import com.privasia.procurehere.core.pojo.EvaluationTimelinePojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.EventTimelinePojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.RfpEventCAddressPojo;
import com.privasia.procurehere.core.pojo.RfxEnvelopPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SupplierMaskingCodePojo;
import com.privasia.procurehere.core.pojo.SupplierMaskingPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.MaskUtils;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.ErpIntegrationService;
import com.privasia.procurehere.service.ErpSetupService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.service.ProductContractReminderService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpBqEvaluationCommentsService;
import com.privasia.procurehere.service.RfpBqTotalEvaluationCommentsService;
import com.privasia.procurehere.service.RfpCqEvaluationCommentsService;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfpDocumentService;
import com.privasia.procurehere.service.RfpEnvelopService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.service.RfpSupplierBqItemService;
import com.privasia.procurehere.service.RfpSupplierBqService;
import com.privasia.procurehere.service.RfpSupplierCommentService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqSupplierBqService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftSupplierBqService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

@Service
@Transactional(readOnly = true)
public class RfpEventServiceImpl implements RfpEventService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfpBqDao rfpEventBqDao;

	@Autowired
	RfpEventContactDao rfpEventContactDao;

	@Autowired
	RfpEventCorrespondenceAddressDao rfpEventCorrespondenceAddressDao;

	@Autowired
	NaicsCodesDao naicsCodesDao;

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	RfpEventMeetingDao rfpEventMeetingDao;

	@Autowired
	RfpDocumentDao rfpDocumentDao;

	@Autowired
	RfpCqEvaluationCommentsDao cqEvaluationCommentsDao;

	@Autowired
	RfpBqItemDao rfpBqItemDao;

	@Autowired
	RfpCqDao rfpCqDao;

	@Autowired
	RfpCqItemDao rfpCqItemDao;

	@Autowired
	RfpEventMeetingDocumentDao rfpEventMeetingDocumentDao;

	@Autowired
	RfpEnvelopDao rfpEnvelopDao;

	@Autowired
	RfpEvaluatorUserDao rfpEvaluatorUserDao;

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	RfpSorEvaluationCommentsService rfpSorEvaluationCommentsService;

	@Autowired
	RfpSupplierMeetingAttendanceDao rfpSupplierMeetingAttendanceDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	RfpCqEvaluationCommentsService cqEvaluationCommentsService;

	@Autowired
	RfpBqTotalEvaluationCommentsService rfpBqTotalEvaluationCommentsService;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	CostCenterDao costCenterDao;

	@Autowired
	RfpReminderDao rfpReminderDao;

	@Autowired
	UserService userService;

	@Autowired
	BuyerService buyerService;

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	RfpEventTimeLineDao rfpEventTimeLineDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	ServletContext context;

	@Autowired
	RfpEnvelopService rfpEnvelopService;

	@Autowired
	RfpSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfpSupplierCqDao rfpSupplierCqDao;

	@Autowired
	RfpEventSupplierDao rfpEventSupplierDao;

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfpSupplierBqItemService rfpSupplierBqItemService;

	@Autowired
	RfpBqEvaluationCommentsService rfpBqEvaluationCommentsService;

	@Autowired
	RfpEventAuditDao rfpEventAuditDao;

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
	RfqEventService rfqEventService;

	@Autowired
	RfqEventSupplierDao rfqEventSupplierDao;

	@Autowired
	RfqBqDao rfqBqDao;

	@Autowired
	RfqBqItemDao rfqBqItemDao;

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
	RfpSupplierBqDao rfpSupplierBqDao;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

	@Autowired
	RfpSupplierSorItemDao rfpSupplierSorItemDao;

	@Autowired
	RfpSupplierCommentService rfpSupplierCommentService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	UomService uomService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	UserDao userDao;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	PrService prService;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfpSupplierBqService rfpSupplierBqService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;

	@Autowired
	RftSupplierBqService rftSupplierBqService;
	@Autowired
	RfqSupplierBqService rfqSupplierBqService;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	RfpEnvelopService envelopService;

	@Autowired
	DeclarationDao declarationDao;

	@Autowired
	RfpEvaluatorDeclarationDao rfpEvaluatorDeclarationDao;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	RfpSupplierCqOptionDao rfpSupplierCqOptionDao;

	@Autowired
	RfpCqOptionDao rfpCqOptionDao;

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
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfpEventAwardDetailsDao rfpEventAwardDetailsDao;

	@Autowired
	SupplierPerformanceTemplateService spTemplateService;

	@Autowired
	SupplierPerformanceFormService spFormService;

	@Autowired
	SupplierPerformanceAuditService formAuditService;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	ContractAuditDao contractAuditDao;

	@Autowired
	RfpEventAwardDao rfpEventAwardDao;

	@Autowired
	RfpSorDao rfpEventSorDao;

	@Autowired
	RfpSorItemDao rfpSorItemDao;

	@Autowired
	RfpSupplierSorItemService rfpSupplierSorItemService;

	@Autowired
	RfpSupplierSorDao rfpSupplierSorDao;

	@Override
	@Transactional(readOnly = false)
	public RfpEvent saveEvent(RfpEvent rfpEvent, User loggedInUser) throws SubscriptionException {
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
		rfpEvent.setCreatedDate(new Date());
		rfpEvent.setEventOwner(loggedInUser);
		rfpEvent.setTenantId(loggedInUser.getTenantId());
		rfpEvent.setCreatedBy(loggedInUser);
		LOG.info("Save Event Name :" + rfpEvent.getEventId());
		RfpEvent dbObj = rfpEventDao.saveOrUpdate(rfpEvent);

		// If there are unsaved envelopes attached to the event, save them as well
		try {
			if (CollectionUtil.isNotEmpty(rfpEvent.getRfxEnvelop())) {
				for (RfpEnvelop env : rfpEvent.getRfxEnvelop()) {
					if (StringUtils.checkString(env.getId()).length() == 0 && env.getEnvelopTitle() != null) {
						env.setRfxEvent(dbObj);
						rfpEnvelopDao.save(env);
					}
				}
			} else {
				LOG.info("Kahan gaye envelopes bhai..... Kha gaya kya???");
			}
		} catch (Exception e) {
			LOG.error("Error saving envelope. : " + e.getMessage(), e);
		}

		// touch event owner
		dbObj.getEventOwner().getLoginId();
		// documents required to upload for copied event if previous event has Cqtype.DOCUMENT_DOWNLOAD_LINK
		if (rfpEvent.isUploadDocuments()) {
			dbObj.setUploadDocuments(true);
		}
		return dbObj;
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent updateEvent(RfpEvent rfpEvent) {
		RfpEvent event = rfpEventDao.update(rfpEvent);
		event.getCreatedBy().getLoginId();
		return event;

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteEvent(RfpEvent rfpEvent) {
		rfpEventDao.delete(rfpEvent);

	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfpEvent(RfpEvent rfpEvent) {
		rfpEventDao.update(rfpEvent);
	}

	@Override
	public RfpEvent getEventById(String id) {
		RfpEvent rfp = rfpEventDao.findById(id);
		if(rfp.getTemplate() != null){
			rfp.getTemplate().getId();
		}
		if(CollectionUtil.isNotEmpty(rfp.getSuppliers())){
			for(RfpEventSupplier supplier: rfp.getSuppliers()){
				supplier.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getDocuments())){
			for(RfpEventDocument doc : rfp.getDocuments()){
				if (doc.getUploadBy() != null) {
					doc.getUploadBy().getLoginId();
				}
				doc.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getMeetings())){
			for(RfpEventMeeting meeting: rfp.getMeetings()){
				meeting.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getEventContacts())){
			for(RfpEventContact contact : rfp.getEventContacts()){
				contact.getContactName();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getEventCorrespondenceAddress())){
			for(EventCorrespondenceAddress address: rfp.getEventCorrespondenceAddress()){
				address.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getEventBqs())){
			for(RfpEventBq bq: rfp.getEventBqs()){
				bq.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getCqs())){
			for(RfpCq cq: rfp.getCqs()) {
				cq.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getRftReminder())){
			for(RfpReminder reminder : rfp.getRftReminder()){
				reminder.getId();
			}
		}

		if(CollectionUtil.isNotEmpty(rfp.getEventViewers())){
			for(User viewr: rfp.getEventViewers()){
				viewr.getLoginId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getEventEditors())){
			for(User editor: rfp.getEventEditors()){
				editor.getLoginId();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getTeamMembers())) {
			for (RfpTeamMember team : rfp.getTeamMembers()) {
				team.getTeamMemberType();
				if (team.getUser() != null) {
					team.getUser().getLoginId();
				}
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getAwardedSuppliers())){
			for(Supplier sup : rfp.getAwardedSuppliers()){
				sup.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getComment())){
			for(RfpComment comment: rfp.getComment()){
				comment.getComment();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getIndustryCategories())){
			for(IndustryCategory industryCategory: rfp.getIndustryCategories()){
				industryCategory.getId();
			}
		}
		for (RfpEventApproval approval : rfp.getApprovals()) {
			for (RfpApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}

		if (CollectionUtil.isNotEmpty(rfp.getSuspensionApprovals())) {
			for (RfpEventSuspensionApproval approval : rfp.getSuspensionApprovals()) {
				for (RfpSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getAwardApprovals())){
			for(RfpEventAwardApproval awardApproval: rfp.getAwardApprovals()){
				awardApproval.getLevel();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getAwardComment())){
			for(RfpAwardComment awardComment: rfp.getAwardComment()){
				awardComment.getComment();
			}
		}
		if(CollectionUtil.isNotEmpty(rfp.getSuspensionComment())){
			for(RfpSuspensionComment suspensionComment : rfp.getSuspensionComment()){
				suspensionComment.getComment();
			}
		}
		if(rfp.getParticipationFeeCurrency() != null){
			rfp.getParticipationFeeCurrency().getId();
		}
		if (rfp.getBaseCurrency() != null) {
			rfp.getBaseCurrency().getCurrencyCode();
		}
		if(rfp.getBusinessUnit() != null){
			rfp.getBusinessUnit().getId();
		}
		if(rfp.getCostCenter() != null){
			rfp.getCostCenter().getStatus();
		}
		for (RfpEnvelop rf : rfp.getRfxEnvelop()) {
			rf.getEnvelopTitle();
			if (CollectionUtil.isNotEmpty(rf.getOpenerUsers())) {
				for (RfpEnvelopeOpenerUser item : rf.getOpenerUsers()) {
					item.getUser();
				}
			}
		}

		if (rfp.getEventOwner() != null) {
			rfp.getEventOwner().getName();
			rfp.getEventOwner().getCommunicationEmail();
			rfp.getEventOwner().getPhoneNumber();
			if (rfp.getEventOwner().getOwner() != null) {
				Owner usr = rfp.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getUnMaskedUsers())) {
			for (RfpUnMaskedUser usr : rfp.getUnMaskedUsers()) {
				usr.getUserUnmasked();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getEvaluationConclusionUsers())) {
			for (RfpEvaluationConclusionUser usr : rfp.getEvaluationConclusionUsers()) {
				usr.getConcluded();
				usr.getUser().getId();
			}
		}

		if (rfp.getEvaluationProcessDeclaration() != null) {
			rfp.getEvaluationProcessDeclaration().getContent();
		}
		if (rfp.getProcurementMethod() != null) {
			rfp.getProcurementMethod().getProcurementMethod();
		}
		if (rfp.getProcurementCategories() != null) {
			rfp.getProcurementCategories().getProcurementCategories();
		}
		if (rfp.getGroupCode() != null) {
			rfp.getGroupCode().getId();
			rfp.getGroupCode().getGroupCode();
		}
		return rfp;
	}

	@Override
	public RfpEvent getRfpEventByeventId(String eventId) {
		RfpEvent rfp = rfpEventDao.findByEventId(eventId);
		if (rfp.getEventOwner().getBuyer() != null) {
			rfp.getEventOwner().getBuyer().getLine1();
			rfp.getEventOwner().getBuyer().getLine2();
			rfp.getEventOwner().getBuyer().getCity();
			if (rfp.getEventOwner().getBuyer().getState() != null) {
				rfp.getEventOwner().getBuyer().getState().getStateName();
				if (rfp.getEventOwner().getBuyer().getState().getCountry() != null) {
					rfp.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (rfp.getBaseCurrency() != null) {
			rfp.getBaseCurrency().getCurrencyCode();
		}

		rfp.getProcurementCategories();
		rfp.getProcurementMethod();

		if (CollectionUtil.isNotEmpty(rfp.getUnMaskedUsers())) {
			for (RfpUnMaskedUser item : rfp.getUnMaskedUsers()) {
				item.getUser();
			}
		}

		if (rfp.getTemplate() != null) {
			rfp.getTemplate().getTemplateName();
		}

		if (CollectionUtil.isNotEmpty(rfp.getEvaluationConclusionUsers())) {
			for (RfpEvaluationConclusionUser item : rfp.getEvaluationConclusionUsers()) {
				item.getUser().getName();
			}
		}

		if (CollectionUtil.isNotEmpty(rfp.getAwardApprovals())) {
			for (RfpEventAwardApproval approval : rfp.getAwardApprovals()) {
				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					for (RfpAwardApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getUser().getName();
						approvalUser.getUser().getLoginId();
						approvalUser.getApproval();
						approvalUser.getRemarks();
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(rfp.getAwardComment())) {
			LOG.info(" Comments  >>>  " + rfp.getAwardComment());
			for (RfpAwardComment comment : rfp.getAwardComment()) {
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getLoginId();
				}
				LOG.info(" Comments  >>>  " + comment.getComment());
			}
		}
		if (rfp.getDeliveryAddress() != null) {
			rfp.getDeliveryAddress().getLine1();
			rfp.getDeliveryAddress().getLine2();
			rfp.getDeliveryAddress().getCity();
			if (rfp.getDeliveryAddress().getState() != null) {
				rfp.getDeliveryAddress().getState().getStateName();
				rfp.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getRftReminder())) {
			for (RfpReminder reminder : rfp.getRftReminder()) {
				reminder.getReminderDate();
				reminder.getStartReminder();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getSuppliers())) {
			for (RfpEventSupplier item : rfp.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}
		if (rfp.getEventOwner().getBuyer() != null) {
			Buyer buyer = rfp.getEventOwner().getBuyer();
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
		if (CollectionUtil.isNotEmpty(rfp.getMeetings())) {
			for (RfpEventMeeting item : rfp.getMeetings()) {
				item.getStatus().name();
				item.getRemarks();
				item.getVenue();
				item.getAppointmentDateTime();
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingContacts())) {
					for (RfpEventMeetingContact contact : item.getRfxEventMeetingContacts()) {
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
					for (RfpEventMeetingDocument docs : item.getRfxEventMeetingDocument()) {
						docs.getId();
						docs.getFileName();
						docs.getFileSizeInKb();
						docs.getCredContentType();
					}
				}
			}
		}
		// if (CollectionUtil.isNotEmpty(rfp.getDocuments())) {
		// for (RfpEventDocument item : rfp.getDocuments()) {
		// item.getDescription();
		// item.getFileName();
		// item.getUploadDate();
		// item.getFileData();
		// }
		// }
		if (CollectionUtil.isNotEmpty(rfp.getComment())) {
			for (RfpComment comment : rfp.getComment()) {
				comment.getComment();
				comment.getCreatedBy();
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getName();
				}
				comment.getLoginName();
				comment.getUserName();
				comment.getRfpEvent();
			}
		}

		if (CollectionUtil.isNotEmpty(rfp.getIndustryCategories())) {
			for (IndustryCategory indCat : rfp.getIndustryCategories()) {
				indCat.getCode();
			}
		}

		if (CollectionUtil.isNotEmpty(rfp.getSuspensionApprovals())) {
			for (RfpEventSuspensionApproval approver : rfp.getSuspensionApprovals()) {
				if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
					for (RfpSuspensionApprovalUser user : approver.getApprovalUsers()) {
						user.getRemarks();
						user.getUser().getCommunicationEmail();
					}
				}
			}
		}

		if (CollectionUtil.isNotEmpty(rfp.getAwardApprovals())) {
			for (RfpEventAwardApproval approvalLevel : rfp.getAwardApprovals()) {
				approvalLevel.getLevel();
				if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
					for (RfpAwardApprovalUser user : approvalLevel.getApprovalUsers()) {
						user.getApproval();
						user.getUser().getLoginId();
					}
				}
			}
		}

		if (rfp.getGroupCode() != null) {
			rfp.getGroupCode().getId();
			rfp.getGroupCode().getGroupCode();
			rfp.getGroupCode().getStatus();
		}

		if (rfp.getBusinessUnit() != null) {
			rfp.getBusinessUnit().getUnitName();
		}

		if (rfp.getCostCenter() != null) {
			rfp.getCostCenter().getCostCenter();
		}

		if (CollectionUtil.isNotEmpty(rfp.getAwardComment())) {
			for (RfpAwardComment comment : rfp.getAwardComment()) {
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getLoginId();
				}
			}
		}

		return rfp;
	}

	@Override
	public List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue) {
		return naicsCodesDao.findLeafIndustryCategoryByName(searchValue);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveEventContact(RfpEventContact rfpEventContact) {
		rfpEventContactDao.saveOrUpdate(rfpEventContact);

	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfpEventContact(RfpEventContact rfpEventContact) {
		rfpEventContactDao.update(rfpEventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveEventCorrespondenceAddress(RfpEventCorrespondenceAddress rfpEventCorrespondenceAddress) {
		rfpEventCorrespondenceAddressDao.save(rfpEventCorrespondenceAddress);
	}

	@Override
	public IndustryCategory getIndustryCategoryForRftById(String id) {
		return industryCategoryDao.getIndustryCategoryForRftById(id);
	}

	@Override
	public List<RfpEventContact> getAllContactForEvent(String eventId) {
		return rfpEventContactDao.findAllEventContactById(eventId);
	}

	@Override
	public List<RfpEventCorrespondenceAddress> getAllCAddressForEvent(String eventId) {
		List<RfpEventCorrespondenceAddress> list = rfpEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpEventCorrespondenceAddress address : list) {
				if (address != null && address.getState() != null && address.getState().getCountry() != null) {
					address.getState().getCountry().getCountryName();
				}
			}
		}
		return list;
	}

	@Override
	public List<RfpEventCAddressPojo> getAllCorrespondenceAddressPojo(String eventId) {
		List<RfpEventCAddressPojo> returnList = new ArrayList<RfpEventCAddressPojo>();

		List<RfpEventCorrespondenceAddress> list = rfpEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpEventCorrespondenceAddress rfpEventCorrespondenceAddress : list) {
				if (rfpEventCorrespondenceAddress.getState() != null)
					rfpEventCorrespondenceAddress.getState().getCountry();
				if (rfpEventCorrespondenceAddress.getState() != null)
					rfpEventCorrespondenceAddress.getState().getStateName();

				RfpEventCAddressPojo rep = new RfpEventCAddressPojo(rfpEventCorrespondenceAddress);
				rep.setCountry(rfpEventCorrespondenceAddress.getState().getCountry().getCountryName());
				returnList.add(rep);
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addEditorUser(String eventId, String userId) {
		RfpEvent rfpEvent = getRfpEventByeventId(eventId);
		List<User> editors = rfpEvent.getEventEditors();
		if (editors == null) {
			editors = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		editors.add(user);
		updateRfpEvent(rfpEvent);
		return editors;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEditorUser(String eventId, String userId) {
		RfpEvent rfpEvent = getRfpEventByeventId(eventId);
		User user = userService.getUsersById(userId);
		if (rfpEvent.getEventEditors() != null) {
			rfpEvent.getEventEditors().remove(user);
		}
		updateRfpEvent(rfpEvent);
		return rfpEvent.getEventEditors();
	}

	@Override
	public RfpEvent loadRfpEventById(String id) {
		RfpEvent event = rfpEventDao.findByEventId(id);
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
				for (RfpEventContact contact : event.getEventContacts()) {
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
				for (RfpTeamMember approver : event.getTeamMembers()) {
					approver.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfpEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfpApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfpEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfpSuspensionApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getComment())) {
				LOG.info(" Comments  >>>  " + event.getComment());
				for (RfpComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
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
				event.getTemplate().getOptionalSuspendApproval();
				event.getTemplate().getVisibleSuspendApproval();
				event.getTemplate().getReadOnlySuspendApproval();
			}
		}
		return event;
	}

	@Override
	public boolean isExists(RfpEvent rfpEvent) {
		return rfpEventDao.isExists(rfpEvent);
	}

	@Override
	public RfpEvent loadRfpEventForSummeryById(String id) {
		RfpEvent event = rfpEventDao.findBySupplierEventId(id);
		RfpEvent event2 = rfpEventDao.findEventSorByEventId(id);
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
			// User unMaskeduser = rfpEventDao.getUnMaskedUserNameAndMailByEventId(event.getId());
			// if (unMaskeduser != null) {
			// event.setUnMaskedUser(unMaskeduser);
			// }
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

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfpEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RfpEventSupplier supplier : event.getSuppliers()) {
					LOG.info("Company Name : " + supplier.getSupplier().getCompanyName());
					supplier.getSupplier().getCompanyName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RfpTeamMember teamMembers : event.getTeamMembers()) {
					teamMembers.getTeamMemberType();
					teamMembers.getUser().getName();
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
			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfpUnMaskedUser unmaskuser : event.getUnMaskedUsers()) {
					unmaskuser.getUser().getCommunicationEmail();
					unmaskuser.getUser().getName();
					unmaskuser.getUser().getPhoneNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RfpEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getCommunicationEmail();
					user.getUser().getName();
					user.getUser().getPhoneNumber();
				}
			}

			// Touch Event reminders...
			if (CollectionUtils.isNotEmpty(event.getReminder())) {
				for (RfpReminder reminder : event.getReminder()) {
					reminder.getInterval();
				}
			}

			// if (CollectionUtil.isNotEmpty(event.getDocuments())) {
			// for (RfpEventDocument document : event.getDocuments()) {
			// document.getDescription();
			// }
			// }
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfpEventMeeting meeting : event.getMeetings()) {
					meeting.getRemarks();
					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier supplier : meeting.getInviteSuppliers()) {
							supplier.getCompanyName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RfpEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
							contact.getContactName();
						}
					}
					// if
					// (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument()))
					// {
					// for (RfpEventMeetingDocument document :
					// meeting.getRfxEventMeetingDocument()) {
					// document.getFileName();
					// }
					// }
				}
			}
			if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
				for (RfpEventBq bq : event.getEventBqs()) {
					if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
						for (RfpBqItem item : bq.getBqItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event2.getEventSors())) {
				for (RfpEventSor bq : event2.getEventSors()) {
					if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
						for (RfpSorItem item : bq.getSorItems()) {
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
				LOG.info(" Comments  >>>  " + event.getComment());
				for (RfpComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionComment())) {
				LOG.info(" Comments  >>>  " + event.getSuspensionComment());
				for (RfpSuspensionComment comment : event.getSuspensionComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}

			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfpEventApproval approvalLevel : event.getApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfpApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfpEventSuspensionApproval approvalLevel : event.getSuspensionApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfpSuspensionApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
				for (RfpEventAwardApproval approvalLevel : event.getAwardApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfpAwardApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardComment())) {
				LOG.info(" Comments  >>>  " + event.getAwardComment());
				for (RfpAwardComment comment : event.getAwardComment()) {
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

			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RfpEventSupplier eventSupplier : event.getSuppliers()) {
					if (eventSupplier.getSupplier() != null)
						eventSupplier.getSupplier().getFullName();
					if (CollectionUtil.isNotEmpty(eventSupplier.getTeamMembers())) {
						for (RfpSupplierTeamMember supplierTeamMember : eventSupplier.getTeamMembers())
							supplierTeamMember.getUser().getName();
					}
				}
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
	public Integer getCountOfEnvelopByEventId(String eventId) {
		return rfpEventDao.getCountOfEnvelopByEventId(eventId);
	}

	@Override
	public Integer getCountOfRftDocumentByEventId(String eventId) {
		return rfpDocumentDao.getCountOfDocumentByEventId(eventId);
	}

	@Override
	public Integer getCountOfRfpCqByEventId(String eventId) {
		return rfpCqDao.getCountOfCqByEventId(eventId);
	}

	@Override
	public List<DraftEventPojo> getAllEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat) throws SubscriptionException {
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

		return rfpEventDao.getAllRfpEventByTenantId(tenantId, loggedInUser, pageNo, serchVal, indCat);

	}

	@Override
	public boolean isExists(RfpSupplierMeetingAttendance rfpSupplierMeetingAttendance) {
		return rfpSupplierMeetingAttendanceDao.isExists(rfpSupplierMeetingAttendance);
	}

	@Override
	public RfpSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId) {
		return rfpSupplierMeetingAttendanceDao.attendenceByEventId(meetingId, eventId, tenantId);
	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rfpEventDao.getEventSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent copyFrom(String eventId, User createdBy, BusinessUnit buyerbusinessUnit) throws SubscriptionException, ApplicationException {
		RfpEvent event = this.getEventById(eventId);

		if (event.getGroupCode() != null && Status.INACTIVE == event.getGroupCode().getStatus()) {
			LOG.error("The group code " + event.getGroupCode().getGroupCode() + " used in Previous Event is inactive");
			throw new ApplicationException("The group code '" + event.getGroupCode().getGroupCode() + "' used in Event is inactive");
		}

		if (event.getDeliveryAddress() != null && event.getDeliveryAddress().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Delivery address found ....");
			throw new ApplicationException("Delivery address is Inactive for event:" + event.getEventId());
		} else {
			LOG.info("active Delivery address found ........");
		}

		if (event.getTemplate() != null && Status.INACTIVE == event.getTemplate().getStatus()) {
			LOG.info("inactive Template found for Id .... " + event.getTemplate().getId());
			throw new ApplicationException("Template [" + event.getTemplate().getTemplateName() + "] used by the event [" + event.getEventId() + "] is Inactive");
		}

		RfpEvent newEvent = event.copyFrom(event);
		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());

		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFP")) {
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
			newEvent.setErpEnable(erpSetup.getIsErpEnable());

		} else {
			newEvent.setErpEnable(false);
		}
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFP", newEvent.getBusinessUnit()));
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setEnableAwardApproval(event.getEnableAwardApproval());

		// Save teamMember
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RfpTeamMember> tm = new ArrayList<RfpTeamMember>();
			for (RfpTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
				// rfaEventService.sendTeamMemberEmailNotificationEmail(teamMember.getUser(),
				// teamMember.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length()
				// > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(),
				// StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() :
				// " ", RfxTypes.RFP, newDbEvent.getId());
			}
		}

		if (CollectionUtil.isNotEmpty(newEvent.getUnMaskedUsers())) {
			List<RfpUnMaskedUser> tm = new ArrayList<RfpUnMaskedUser>();
			for (RfpUnMaskedUser unmaskUser : newEvent.getUnMaskedUsers()) {
				unmaskUser.setEvent(newEvent);
				tm.add(unmaskUser);
			}
		}

		// save Doc
		if(CollectionUtil.isNotEmpty(newEvent.getDocuments())){
			for(RfpEventDocument rfpDocument: newEvent.getDocuments()){
				rfpDocument.copyFrom(newEvent);
				LOG.info("Saving document...");
				rfpDocumentService.saveDocuments(rfpDocument);
			}
		}

		// save Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getApprovals())) {
			for (RfpEventApproval app : newEvent.getApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfpApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);

					}
				}
			}
		}

		// save Suspension Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getSuspensionApprovals())) {
			for (RfpEventSuspensionApproval app : newEvent.getSuspensionApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfpSuspensionApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);
					}
				}
			}
		}

		// save Award Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getAwardApprovals())) {
			for (RfpEventAwardApproval app : newEvent.getAwardApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfpAwardApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);
					}
				}
			}
		}
		List<RfpEnvelop> envelops = newEvent.getRfxEnvelop();
		newEvent.setRfxEnvelop(null);
		RfpEvent newDbEvent = this.saveEvent(newEvent, createdBy);

		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RfpTeamMember> tm = new ArrayList<RfpTeamMember>();
			for (RfpTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
				rfaEventService.sendTeamMemberEmailNotificationEmail(teamMember.getUser(), teamMember.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFP, newDbEvent.getId());
			}
		}
		// Save contacts
		if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
			for (RfpEventContact contact : newEvent.getEventContacts()) {
				contact.setRfxEvent(newDbEvent);
				saveEventContact(contact);
			}
		}

		// save suppliers
		if (CollectionUtil.isNotEmpty(newEvent.getSuppliers())) {
			for (RfpEventSupplier supp : newEvent.getSuppliers()) {
				supp.setRfxEvent(newDbEvent);
				rfpEventSupplierDao.save(supp);
			}
		}
		List<RfpEventBq> eventBq = new ArrayList<RfpEventBq>();
		// save BQ
		if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {
			for (RfpEventBq bq : newEvent.getEventBqs()) {
				bq.setRfxEvent(newDbEvent);
				RfpEventBq dbBq = rfpEventBqDao.saveOrUpdate(bq);
				eventBq.add(dbBq);
				// save BQ Items
				if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
					RfpBqItem parent = null;
					for (RfpBqItem item : bq.getBqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setBq(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rfpBqItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}

			}

		}

		// save SOR
		List<RfpEventSor> eventSor = new ArrayList<RfpEventSor>();
		if (CollectionUtil.isNotEmpty(newEvent.getEventSors())) {
			for (RfpEventSor bq : newEvent.getEventSors()) {
				bq.setRfxEvent(newDbEvent);
				RfpEventSor dbBq = rfpEventSorDao.saveOrUpdate(bq);
				eventSor.add(dbBq);
				if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
					RfpSorItem parent = null;
					for (RfpSorItem item : bq.getSorItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setSor(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rfpSorItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
			}
		}



		List<RfpCq> eventCq = new ArrayList<RfpCq>();
		// save CQ
		if (CollectionUtil.isNotEmpty(newEvent.getCqs())) {
			for (RfpCq cq : newEvent.getCqs()) {
				cq.setRfxEvent(newDbEvent);
				RfpCqItem parent = null;
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfpCqItem item : cq.getCqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setRfxEvent(newDbEvent);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
				RfpCq dbCq = rfpCqDao.saveOrUpdate(cq);
				eventCq.add(dbCq);
			}
		}

		// save envelop
		if (CollectionUtil.isNotEmpty(envelops)) {
			for (RfpEnvelop envelop : envelops) {
				envelop.setRfxEvent(newDbEvent);
				List<RfpEventBq> bqsOfEnvelop = new ArrayList<RfpEventBq>();

				List<RfpEventBq> envelopBqs = new ArrayList<RfpEventBq>();
				if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
					for (RfpEventBq bq : envelop.getBqList()) {
						envelopBqs.add(bq);
					}
				}

				for (RfpEventBq evntbq : eventBq) {
					for (RfpEventBq envbq : envelopBqs) {
						if (evntbq.getName().equals(envbq.getName())) {
							bqsOfEnvelop.add(evntbq);
							break;
						}
					}
				}

				List<RfpEventSor> sorsOfEnvelop = new ArrayList<RfpEventSor>();

				List<RfpEventSor> envelopSors = new ArrayList<RfpEventSor>();
				if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
					for (RfpEventSor sor : envelop.getSorList()) {
						envelopSors.add(sor);
					}
				}

				for (RfpEventSor evntsor : eventSor) {
					for (RfpEventSor envsor : envelopSors) {
						if (evntsor.getName().equals(envsor.getName())) {
							sorsOfEnvelop.add(evntsor);
							break;
						}
					}
				}

				List<RfpCq> cqsOfEnvelop = new ArrayList<RfpCq>();
				List<RfpCq> envelopCqs = new ArrayList<RfpCq>();
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfpCq bq : envelop.getCqList()) {
						envelopCqs.add(bq);
					}
				}

				for (RfpCq evntCq : eventCq) {
					for (RfpCq envcq : envelopCqs) {
						if (evntCq.getName().equals(envcq.getName())) {
							cqsOfEnvelop.add(evntCq);
							break;
						}
					}
				}

				List<RfpEvaluatorUser> evalUser = new ArrayList<RfpEvaluatorUser>();
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RfpEvaluatorUser eval : envelop.getEvaluators()) {
						LOG.info("EV USER : " + eval.getUser().getId() + "  Active " + eval.getUser().isActive() + " envelop : " + envelop.getEnvelopTitle());
						if (eval.getUser().isActive()) {
							eval.setEnvelope(envelop);
							evalUser.add(eval);
						}
					}
				}

				List<RfpEnvelopeOpenerUser> openerUserList = new ArrayList<RfpEnvelopeOpenerUser>();
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfpEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
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
				envelop = rfpEnvelopDao.save(envelop);

			}
		}

		return newDbEvent;
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit buyerbusinessUnit) throws SubscriptionException, ApplicationException {
		RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(templateId);

		if (rfxTemplate != null && Status.INACTIVE == rfxTemplate.getStatus()) {
			LOG.info("inactive Template [" + rfxTemplate.getTemplateName() + "] found for Id .... " + rfxTemplate.getId());
			throw new ApplicationException("Template [" + rfxTemplate.getTemplateName() + "] is Inactive");
		}

		RfpEvent newEvent = new RfpEvent();
		newEvent.setTemplate(rfxTemplate);
		newEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newEvent.setAddSupplier(rfxTemplate.getAddSupplier());
		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());
		newEvent.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
		newEvent.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newEvent.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		newEvent.setEnableSuspensionApproval(rfxTemplate.getEnableSuspendApproval());
		newEvent.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());
		newEvent.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newEvent.setEnableAwardApproval(rfxTemplate.getEnableAwardApproval());

		// copy unmasking User

		List<RfpUnMaskedUser> unmaskingUser = new ArrayList<RfpUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RfpUnMaskedUser newRfiUnMaskedUser = new RfpUnMaskedUser();
				newRfiUnMaskedUser.setUser(team.getUser());
				newRfiUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRfiUnMaskedUser);

			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}

		// Copy Evaluation Conclusion Users from template PH-999
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RfpEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RfpEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RfpEvaluationConclusionUser conclusionUser = new RfpEvaluationConclusionUser();
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
		newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		newEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());

		// copy approval from template
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RfpEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfpEventApproval newRfApproval = new RfpEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfpApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfpApprovalUser approvalUser = new RfpApprovalUser();
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
			List<RfpEventSuspensionApproval> approvalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Suspension Approval Level :" + templateApproval.getLevel());
				RfpEventSuspensionApproval newRfApproval = new RfpEventSuspensionApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfpSuspensionApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfpSuspensionApprovalUser approvalUser = new RfpSuspensionApprovalUser();
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

		LOG.info(" ************** Template Award Approval ********* " + rfxTemplate.getAwardApprovals());

		if (CollectionUtil.isNotEmpty(rfxTemplate.getAwardApprovals())) {
			List<RfpEventAwardApproval> approvalList = new ArrayList<>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfpEventAwardApproval newRfApproval = new RfpEventAwardApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfpAwardApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfpAwardApprovalUser approvalUser = new RfpAwardApprovalUser();
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
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFP")) {
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
			newEvent.setErpEnable(erpSetup.getIsErpEnable());

		} else {
			newEvent.setErpEnable(false);
		}
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFP", newEvent.getBusinessUnit()));

		List<RfpTeamMember> teamMembers = new ArrayList<RfpTeamMember>();
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
			for (RfxEnvelopPojo en : envlopeList) {
				LOG.info("envlop name form rfx   :  " + en);

				RfpEnvelop rfienvlope = new RfpEnvelop();
				rfienvlope.setEnvelopTitle(en.getRfxEnvelope());
				rfienvlope.setEnvelopSequence(en.getRfxSequence());
				rfienvlope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rfienvlope.setRfxEvent(newEvent);
				rfienvlope.setEnvelopType(EnvelopType.CLOSED);
				envelopService.saveEnvelop(rfienvlope, null, null, null);
			}
		}

		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RfpTeamMember newTeamMembers = new RfpTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setEvent(newEvent);
				teamMembers.add(newTeamMembers);
				rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", rfxTemplate.getType(), newEvent.getId());
			}
			newEvent.setTeamMembers(teamMembers);
		}

		LOG.info("All new event :  " + newEvent.getIndustryCategory() + "   :Cost Center :   " + newEvent.getCostCenter());
		return newEvent;
	}

	@Override
	public RfpEventContact getEventContactById(String id) {
		return rfpEventContactDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteContact(RfpEventContact eventContact) {
		rfpEventContactDao.delete(eventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventContact(RfpEventContact rfpEventContact) {
		rfpEventContactDao.update(rfpEventContact);

	}

	@Override
	@Transactional(readOnly = false)
	public void saveRfpEventReminder(RfpReminder rfpReminder) {
		rfpReminderDao.saveOrUpdate(rfpReminder);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfpEventReminder(RfpReminder rfpReminder) {
		rfpReminderDao.update(rfpReminder);
	}

	@Override
	public List<RfpReminder> getAllRfpEventReminderForEvent(String eventId) {
		return rfpReminderDao.getAllRfpEventReminderForEvent(eventId);

	}

	@Override
	public RfpReminder getRfpEventReminderById(String id) {
		return rfpReminderDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfpReminder(RfpReminder rfpReminder) {
		rfpReminderDao.delete(rfpReminder);
	}

	@Override
	@Transactional(readOnly = true)
	public RfpEvent loadRfpBqsForSupplierByEventId(String id) {
		RfpEvent event = rfpEventDao.findBySupplierEventId(id);
		LOG.info("event in service : " + event);
		if (event != null) {
			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
			}
		}
		if (event.getEventOwner() != null) {
			event.getEventOwner().getName();
			event.getEventOwner().getCommunicationEmail();
			event.getEventOwner().getPhoneNumber();
			if (event.getEventOwner().getOwner() != null) {
				Owner usr = event.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		return event;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addViwersToList(String eventId, String userId) {
		RfpEvent rfpEvent = getRfpEventByeventId(eventId);
		List<User> viewers = rfpEvent.getEventViewers();
		if (viewers == null) {
			viewers = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		viewers.add(user);
		updateRfpEvent(rfpEvent);
		return viewers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeViewersfromList(String eventId, String userId) {
		RfpEvent rfpEvent = getRfpEventByeventId(eventId);
		User user = userService.getUsersById(userId);
		if (rfpEvent.getEventViewers() != null) {
			rfpEvent.getEventViewers().remove(user);
		}
		updateRfpEvent(rfpEvent);
		return rfpEvent.getEventViewers();
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfpTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " TeamMemberType: " + memberType);
		RfpEvent rfpEvent = getRfpEventByeventId(eventId);
		List<RfpTeamMember> teamMembers = rfpEvent.getTeamMembers();
		LOG.info("teamMembers :********: " + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfpTeamMember>();
		}

		User user = userService.findTeamUserById(userId);
		RfpTeamMember rfpTeamMember = new RfpTeamMember();
		rfpTeamMember.setEvent(rfpEvent);
		rfpTeamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (RfpTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				rfpTeamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;
			}
		}
		rfpTeamMember.setTeamMemberType(memberType);
		if (!exists) {
			teamMembers.add(rfpTeamMember);
		}
		LOG.info("TeamMember : " + rfpTeamMember.toLogString());
		rfpEvent.setTeamMembers(teamMembers);
		updateEvent(rfpEvent);

		try {
			if (!exists) {
				RfpEventAudit audit = new RfpEventAudit(null, rfpEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.added", new Object[] { user.getName(), memberType.getValue() }, Global.LOCALE), null);
				eventAuditService.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been added as '" + memberType.getValue() + "' for event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				LOG.info("************* RFA Team Member event audit added successfully *************");
			} else {
				if (!previousMemberType.equalsIgnoreCase(memberType.getValue())) {
					RfpEventAudit audit = new RfpEventAudit(null, rfpEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.changed", new Object[] { user.getName(), previousMemberType, memberType.getValue() }, Global.LOCALE), null);
					eventAuditService.save(audit);
					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been changed from '" + previousMemberType + " to '" + memberType.getValue() + "' for Event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					LOG.info("************* RFA Team Member event audit changed successfully *************");
				}
			}
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}

		/*
		 * List<User> userList = new ArrayList<>(); LOG.info("rfiEvent.getTeamMembers(): " + rfpEvent.getTeamMembers());
		 * for (RfpTeamMember rfpApp : rfpEvent.getTeamMembers()) { try { userList.add((User) rfpApp.getUser().clone());
		 * } catch (CloneNotSupportedException e) { LOG.error(
		 * "Error constructing list of users after adding TeamMember operation : " + e.getMessage(), e); } }
		 */

		return teamMembers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeTeamMemberfromList(String eventId, String userId, RfpTeamMember dbTeamMember) {
		LOG.info("ServiceImpl........." + "removeTeamMemberfromList----TeamMember" + " eventId: " + eventId + " userId: " + userId);
		RfpEvent rfpEvent = getRfpEventByeventId(eventId);
		List<RfpTeamMember> teamMembers = rfpEvent.getTeamMembers();
		LOG.info("**************" + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<>();
		}

		LOG.info("*****dbTeamMember********" + dbTeamMember);

		teamMembers.remove(dbTeamMember);
		dbTeamMember.setEvent(null);
		rfpEvent.setTeamMembers(teamMembers);
		rfpEventDao.update(rfpEvent);
		LOG.info("TeamMember removed." + "	" + teamMembers.size() + " teamMembers: " + teamMembers);

		try {
			RfpEventAudit audit = new RfpEventAudit(null, rfpEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.removed", new Object[] { dbTeamMember.getUser().getName(), dbTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), null);
			eventAuditService.save(audit);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + dbTeamMember.getUser().getName() + "' has been removed from '" + dbTeamMember.getTeamMemberType().getValue() + "' for Event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			LOG.info("************* RFP Team Member removed successfully *************");
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}

		List<User> userList = new ArrayList<User>();
		LOG.info("TeamMember getTeamMembers(): " + rfpEvent.getTeamMembers());
		for (RfpTeamMember rftApp : rfpEvent.getTeamMembers()) {
			try {
				userList.add((User) rftApp.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error constructing list of users after removing TeamMember operation : " + e.getMessage(), e);
			}

		}
		LOG.info(userList.size() + " Event ID :" + eventId);
		return userList;

	}

	@Override
	public RfpTeamMember getRfpTeamMemberByUserIdAndEventId(String eventId, String userId) {
		return rfpEventDao.getRfpTeamMemberByUserIdAndEventId(eventId, userId);
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {
		return rfpEventDao.getUserPemissionsForEvent(userId, eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId) {
		return rfpEventDao.getUserPemissionsForEnvelope(userId, eventId, envelopeId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventReminder(RfpReminder rfpReminder) {
		rfpReminderDao.update(rfpReminder);
	}

	@Override
	public boolean isExists(RfpEventContact rfpEventContact) {
		return rfpEventContactDao.isExists(rfpEventContact);
	}

	@Override
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		return rfpEventDao.getTeamMembersForEvent(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent updateEventApproval(RfpEvent event, User loggedInUser) {

		RfpEvent persistObj = rfpEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfpEventApproval approvalRequest : persistObj.getApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfpApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}
		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			int level = 1;
			for (RfpEventApproval app : event.getApprovals()) {
				app.setEvent(event);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				List<String> levelUsers = new ArrayList<String>();
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfpApprovalUser approvalUser : app.getApprovalUsers()) {
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
		persistObj.setModifiedBy(loggedInUser);
		persistObj.setModifiedDate(new Date());
		persistObj = rfpEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfpEventAudit audit = new RfpEventAudit(persistObj, loggedInUser, new Date(), AuditActionType.Update, auditMessage);
				rfpEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" + persistObj.getEventId() + "' ." + auditMessage, loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
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

			rfpEventTimeLineDao.deleteTimeline(id);

			RfpEvent rfpEvent = rfpEventDao.findById(id);
			// Publish Date
			RfpEventTimeLine timeline = new RfpEventTimeLine();
			timeline.setActivityDate(rfpEvent.getEventPublishDate());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfpEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.publish.date", new Object[] { rfpEvent.getEventName() }, Global.LOCALE));
			rfpEventTimeLineDao.save(timeline);

			// Event Start
			timeline = new RfpEventTimeLine();
			timeline.setActivityDate(rfpEvent.getEventStart());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfpEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.start.date", new Object[] { rfpEvent.getEventName() }, Global.LOCALE));
			rfpEventTimeLineDao.save(timeline);

			// End Date
			timeline = new RfpEventTimeLine();
			timeline.setActivityDate(rfpEvent.getEventEnd());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfpEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.end.date", new Object[] { rfpEvent.getEventName() }, Global.LOCALE));
			rfpEventTimeLineDao.save(timeline);

			// Event Reminders
			if (CollectionUtil.isNotEmpty(rfpEvent.getReminder())) {
				for (RfpReminder reminder : rfpEvent.getReminder()) {
					// Meeting Reminders
					timeline = new RfpEventTimeLine();
					timeline.setActivityDate(reminder.getReminderDate());
					timeline.setActivity(EventTimelineType.REMINDER);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfpEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.reminder.date", new Object[] { rfpEvent.getEventName() }, Global.LOCALE));
					rfpEventTimeLineDao.save(timeline);
				}
			}

			// Meetings
			if (CollectionUtil.isNotEmpty(rfpEvent.getMeetings())) {
				for (RfpEventMeeting meeting : rfpEvent.getMeetings()) {
					// Meeting
					timeline = new RfpEventTimeLine();
					timeline.setActivityDate(meeting.getAppointmentDateTime());
					timeline.setActivity(EventTimelineType.MEETING);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfpEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.meeting.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
					rfpEventTimeLineDao.save(timeline);

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
						// Meeting Reminders
						for (RfpEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
							timeline = new RfpEventTimeLine();
							timeline.setActivityDate(reminder.getReminderDate());
							timeline.setActivity(EventTimelineType.REMINDER);
							timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
							timeline.setEvent(rfpEvent);
							timeline.setDescription(messageSource.getMessage("timeline.event.meeting.reminder.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
							rfpEventTimeLineDao.save(timeline);
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
	public List<RfpEventTimeLine> getRfpEventTimeline(String id) {
		return rfpEventTimeLineDao.getRfpEventTimeline(id);
	}

	@Override
	public JasperPrint generatePdfforEvaluationSummary(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rfpSummary = new ArrayList<EvaluationPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		generatedSdf.setTimeZone(timeZone);
		parameters.put("generatedOn", generatedSdf.format(new Date()));
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RfpEvent event = getRfpEventByeventId(eventId);

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
				String type = "RFP";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName(event.getEventOwner().getBuyer().getCompanyName());
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> submittedSupplierList = null;
				List<EventSupplier> SupplierList = rfpEventSupplierService.getAllSuppliersByEventId(eventId);
				List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();

				if (event.getViewSupplerName()) {
					summary.setSupplierMaskingList(new ArrayList<SupplierMaskingPojo>());
				} else {
					summary.setSupplierMaskingList(buildSupplierMaskingData(SupplierList, eventId));
				}
				boolean isMasked = false;
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					isMasked = true;
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
				List<Bq> bqs = rfpEventBqDao.findBqbyEventId(eventId);
				if (CollectionUtil.isNotEmpty(bqs)) {
					for (Bq bq : bqs) {
						EvaluationBqPojo supBqs = new EvaluationBqPojo();
						supBqs.setName(bq.getName());
						List<EvaluationSuppliersPojo> suppBqComments = new ArrayList<EvaluationSuppliersPojo>();
						if (CollectionUtil.isNotEmpty(submittedSupplierList)) {
							for (EventSupplier supplier : submittedSupplierList) {
								EvaluationSuppliersPojo supList = new EvaluationSuppliersPojo();
								RfpSupplierBq supBq = rfpSupplierBqDao.findBqByBqId(bq.getId(), supplier.getSupplier().getId());
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
								List<RfpBqTotalEvaluationComments> comment = rfpBqTotalEvaluationCommentsService.getComments(supplier.getSupplier().getId(), eventId, bq.getId(), SecurityLibrary.getLoggedInUser());
								if (CollectionUtil.isNotEmpty(comment)) {
									for (RfpBqTotalEvaluationComments leadComments : comment) {
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

				rfpSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rfpSummary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfpSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@SuppressWarnings("deprecation")
	@Override
	public JasperPrint getEvaluationSummaryPdf(RfpEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rfpEventDao.findByEventId(event.getId());
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> summary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		timeFormat.setTimeZone(timeZone);

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
			eventDetails.setType("RFP");
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
			eventDetails.setEnableApprovalReminder(event.getEnableApprovalReminder());
			eventDetails.setAllowEvaluationForDisqualifySupplier(event.getAllowDisqualifiedSupplierDownload());
			if (Boolean.TRUE == event.getEnableApprovalReminder()) {
				eventDetails.setReminderAfterHour(event.getReminderAfterHour());
				eventDetails.setReminderCount(event.getReminderCount());
			}
			eventDetails.setNotifyEventOwner(event.getNotifyEventOwner());
			if (!event.getViewSupplerName() && CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
					String unMaskedOwner = "";
					for (RfpUnMaskedUser rfaUnmaskedUser : event.getUnMaskedUsers()) {
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
				for (RfpEvaluationConclusionUser rfaEvaluationUsers : event.getEvaluationConclusionUsers()) {
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
			if (CollectionUtil.isNotEmpty(event.getReminder())) {
				for (RfpReminder item : event.getReminder()) {
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
			eventDetails.setPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			SimpleDateFormat ddsdf = new SimpleDateFormat("dd/MM/yyyy");
			ddsdf.setTimeZone(sdf.getTimeZone());
			eventDetails.setDiliveryDate(event.getDeliveryDate() != null ? ddsdf.format(event.getDeliveryDate()) : "N/A");
			eventDetails.setValidityDays(event.getSubmissionValidityDays());
			String participationFeeCurrency = event.getParticipationFeeCurrency() != null ? event.getParticipationFeeCurrency().getCurrencyCode() : "";
			eventDetails.setParticipationFeeAndCurrency(participationFeeCurrency + " " + (event.getParticipationFees() != null ? formatedDecimalNumber("2", event.getParticipationFees()) : "-"));
			String depositCurrency = event.getDepositCurrency() != null ? event.getDepositCurrency().getCurrencyCode() : "";
			eventDetails.setDepositAndCurrency(depositCurrency + " " + (event.getDeposit() != null ? formatedDecimalNumber("2", event.getDeposit()) : "-"));

			// Solving issue PH-2916
			List<IndustryCategoryPojo> ics = new ArrayList<IndustryCategoryPojo>();
			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				LOG.info(" Industry Categories " + event.getIndustryCategories().size());
				for (IndustryCategory category : event.getIndustryCategories()) {
					LOG.info(" 1 ");
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(category.getName());
					ics.add(ic);
				}
			}
			eventDetails.setCategory(ics);

			// Correspond Address
			String correspondAddress = "";
			correspondAddress += event.getEventOwner().getBuyer().getLine1() + ", \r\n";
			if (event.getEventOwner().getBuyer().getLine2() != null) {
				correspondAddress += event.getEventOwner().getBuyer().getLine2() + ", \r\n";
			}
			correspondAddress += event.getEventOwner().getBuyer().getCity() + ", \r\n";
			if (event.getEventOwner().getBuyer().getState() != null) {
				correspondAddress += event.getEventOwner().getBuyer().getState().getStateName() + ", \r\n";
				correspondAddress += event.getEventOwner().getBuyer().getState().getCountry().getCountryName() + "\r\n";
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
			List<RfpEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfpEventContact contact : eventContacts) {
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
			eventDetails.setCostCenter((event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "") + " - " + ((event.getCostCenter() != null && event.getCostCenter().getDescription() != null) ? event.getCostCenter().getDescription() : ""));
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

			List<EvaluationCqPojo> allCqData = new ArrayList<EvaluationCqPojo>();
			List<RfpCq> cqList = rfpCqService.findCqForEvent(event.getId());
			if (CollectionUtil.isNotEmpty(cqList)) {
				for (RfpCq item : cqList) {
					EvaluationCqPojo cqs = new EvaluationCqPojo();
					cqs.setName(item.getName());
					cqs.setDescription(item.getDescription());

					List<EvaluationCqItemPojo> cqItemList = new ArrayList<EvaluationCqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getCqItems())) {
						for (RfpCqItem cqItem : item.getCqItems()) {
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
								for (RfpCqOption cqOption : cqItem.getCqOptions()) {
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

			List<Bq> bqs = rfpEventBqDao.findBqbyEventId(event.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
			if (CollectionUtil.isNotEmpty(bqs)) {
				for (Bq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<RfpBqItem> bqItems = rfpBqItemDao.findBqItemsForBq(item.getId());
					List<EvaluationBqItemPojo> evaluationBqItem = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfpBqItem bqItem : bqItems) {
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
			List<Sor> sors = rfpEventSorDao.findSorbyEventId(event.getId());
			List<EvaluationSorPojo> allSors = new ArrayList<EvaluationSorPojo>();
			if (CollectionUtil.isNotEmpty(sors)) {
				for (Sor item : sors) {
					EvaluationSorPojo bqPojo = new EvaluationSorPojo();
					bqPojo.setName(item.getName());

					List<RfpSorItem> bqItems = rfpSorItemDao.findSorItemsForSor(item.getId());
					List<EvaluationSorItemPojo> evaluationBqItem = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfpSorItem bqItem : bqItems) {
							EvaluationSorItemPojo bi = new EvaluationSorItemPojo();
							bi.setLevel(bqItem.getLevel() + "." + bqItem.getOrder());
							bi.setItemName(bqItem.getItemName());
							bi.setDescription(bqItem.getItemDescription());
							bi.setUom(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
							bi.setImgPath(imgPath);
							bi.setDecimal(event.getDecimal());
							evaluationBqItem.add(bi);
						}
					}
					bqPojo.setBqItems(evaluationBqItem);
					allSors.add(bqPojo);
				}
			}

			// Fetch List of suppliers Envelops
			List<RfpEnvelop> envelops = rfpEnvelopService.getAllEnvelopByEventId(event.getId(), loggedInUser);
			List<EvaluationEnvelopPojo> envlopList = new ArrayList<EvaluationEnvelopPojo>();
			if (CollectionUtil.isNotEmpty(envelops)) {
				for (RfpEnvelop envelop : envelops) {
					EvaluationEnvelopPojo env = new EvaluationEnvelopPojo();
					env.setEnvlopName(envelop.getEnvelopTitle());
					env.setDescription(envelop.getDescription());
					env.setType(envelop.getEnvelopType().getValue());
					env.setOpener(envelop.getOpener() != null ? envelop.getOpener().getName() : "");
					env.setOwner(envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "");
					env.setSequence(envelop.getEnvelopSequence());

					List<EvaluationBqPojo> envlopBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
						for (RfpEventBq item : envelop.getBqList()) {
							EvaluationBqPojo bq = new EvaluationBqPojo();
							bq.setName(item.getName());
							envlopBqs.add(bq);
						}
					}
					List<EvaluationCqPojo> envlopCqs = new ArrayList<EvaluationCqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
						for (RfpCq item : envelop.getCqList()) {
							EvaluationCqPojo cq = new EvaluationCqPojo();
							cq.setName(item.getName());
							envlopCqs.add(cq);
						}
					}

					List<EvaluationSorPojo> envlopSors = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
						for (RfpEventSor item : envelop.getSorList()) {
							EvaluationSorPojo bq = new EvaluationSorPojo();
							bq.setName(item.getName());
							envlopSors.add(bq);
						}
					}

					// List of evaluators
					List<EvaluationEnvelopPojo> evaluatorList = new ArrayList<EvaluationEnvelopPojo>();
					List<RfpEvaluatorUser> evaluators = envelop.getEvaluators();
					if (CollectionUtil.isNotEmpty(evaluators)) {
						for (RfpEvaluatorUser usr : evaluators) {
							EvaluationEnvelopPojo el = new EvaluationEnvelopPojo();
							el.setOwner(usr.getUser().getName());
							evaluatorList.add(el);
						}
					}

					// List of openers
					List<EvaluationEnvelopPojo> openersList = new ArrayList<EvaluationEnvelopPojo>();
					List<RfpEnvelopeOpenerUser> openers = envelop.getOpenerUsers();
					if (CollectionUtil.isNotEmpty(openers)) {
						for (RfpEnvelopeOpenerUser usr : openers) {
							EvaluationEnvelopPojo el = new EvaluationEnvelopPojo();
							el.setOwner(usr.getUser().getName());
							openersList.add(el);
						}
					}

					env.setOpenerUsers(openersList);
					env.setEvaluator(evaluatorList);
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

				List<RfpEvaluationConclusionUser> evaluationConclusionUserList = rfpEventDao.findEvaluationConclusionUsersByEventId(event.getId());
				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					Log.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RfpEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
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

			List<RfpEventTimeLine> timeline = getRfpEventTimeline(event.getId());
			List<EvaluationTimelinePojo> timelineList = new ArrayList<EvaluationTimelinePojo>();
			if (CollectionUtil.isNotEmpty(timeline)) {
				for (RfpEventTimeLine item : timeline) {
					EvaluationTimelinePojo et = new EvaluationTimelinePojo();
					et.setEventDate(item.getActivityDate() != null ? sdf.format(item.getActivityDate()) : "");
					et.setDescription(item.getDescription());
					et.setType(item.getActivity().name());
					timelineList.add(et);
				}
			}

			// Event Approvals
			RfpEvent rfpEvent = getEventById(event.getId());
			List<RfpEventApproval> approvals = rfpEvent.getApprovals();
			List<EvaluationApprovalsPojo> approvalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (RfpEventApproval item : approvals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(item.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfpApprovalUser usr : item.getApprovalUsers()) {
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
					approvalList.add(approve);
				}
			}

			// Suspension Approvals
			List<RfpEventSuspensionApproval> suspensionApprovals = rfpEvent.getSuspensionApprovals();
			List<EvaluationApprovalsPojo> susppensionApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionApprovals)) {
				for (RfpEventSuspensionApproval approval : suspensionApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfpSuspensionApprovalUser usr : approval.getApprovalUsers()) {
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
			List<RfpEventAwardApproval> awardApprovals = rfpEvent.getAwardApprovals();
			List<EvaluationApprovalsPojo> awardApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(awardApprovals)) {
				for (RfpEventAwardApproval approval : awardApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfpAwardApprovalUser usr : approval.getApprovalUsers()) {
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
				List<RfpEventDocument> document = rfpDocumentDao.findAllEventdocsbyEventId(event.getId());// event.getDocuments();
				for (RfpEventDocument docs : document) {
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
			List<RfpComment> comments = event.getComment();
			List<EvaluationCommentsPojo> commentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(comments)) {
				for (RfpComment item : comments) {
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
			List<RfpSuspensionComment> suspensionComments = event.getSuspensionComment();
			List<EvaluationCommentsPojo> suspensionCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionComments)) {
				for (RfpSuspensionComment comment : suspensionComments) {
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
			List<RfpAwardComment> awardComments = event.getAwardComment();
			List<EvaluationCommentsPojo> awardCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(awardComments)) {
				for (RfpAwardComment comment : awardComments) {
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
			List<RfpEventAudit> eventAudit = rfpEventAuditDao.getRfpEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RfpEventAudit ra : eventAudit) {
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
			eventDetails.setApprovals(approvalList);
			eventDetails.setTimelines(timelineList);
			eventDetails.setDocuments(documentList);
			eventDetails.setAuditDetails(auditList);
			eventDetails.setSuspensionApprovals(susppensionApprovalList);
			eventDetails.setSuspensionComments(suspensionCommentDetails);
			eventDetails.setAwardApprovals(awardApprovalList);
			eventDetails.setAwardComments(awardCommentDetails);

			summary.add(eventDetails);

			parameters.put("EVALUATION_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate RFP Evaluation Summary PDF Report. " + e.getMessage(), e);
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
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RfpEvent event, String imgPath, SimpleDateFormat sdf) {
		List<RfpEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfpEventMeeting meeting : meetingList) {
				EvaluationMeetingPojo em = new EvaluationMeetingPojo();
				em.setAppointmentDateTime(new Date(sdf.format(meeting.getAppointmentDateTime())));
				em.setRemarks(meeting.getRemarks());
				em.setStatus(meeting.getStatus().toString());
				em.setVenue(meeting.getVenue());
				em.setTitle(meeting.getTitle());
				em.setMandatoryMeeting(meeting.getMeetingAttendMandatory() ? "Yes" : "No");
				// Fetch Contact Details -Start
				List<EvaluationMeetingContactsPojo> contacts = new ArrayList<EvaluationMeetingContactsPojo>();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
					for (RfpEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
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
					for (RfpEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
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

					List<Sor> bqs = rfpEventSorDao.findSorbyEventId(eventId);

					List<EvaluationSorPojo> allBqs = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Sor bq : bqs) {
							EvaluationSorPojo bqItem = new EvaluationSorPojo();
							bqItem.setName(bq.getName());
							List<RfpSupplierSorItem> suppBqItems = rfpSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());

							List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RfpSupplierSorItem suppBqItem : suppBqItems) {

									EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
									evlBqItem.setDescription(suppBqItem.getItemName());
									evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
									evlBqItem.setAmount(null);
									evlBqItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
									evlBqItems.add(evlBqItem);
									if (suppBqItem.getChildren() != null) {
										for (RfpSupplierSorItem childBqItem : suppBqItem.getChildren()) {
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
					List<Bq> bqs = rfpEventBqDao.findBqbyEventId(eventId);

					List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Bq bq : bqs) {
							EvaluationBqPojo bqItem = new EvaluationBqPojo();
							bqItem.setName(bq.getName());
							List<RfpSupplierBqItem> suppBqItems = rfpSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());
							for (RfpSupplierBqItem rfpSupplierBqItem : suppBqItems) {
								BigDecimal grandTotal = rfpSupplierBqItem.getSupplierBq().getGrandTotal();
								bqSupplierPojo.setGrandTotal(grandTotal);
								LOG.info("GRANDTOTAL " + rfpSupplierBqItem.getSupplierBq().getGrandTotal());
							}

							List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RfpSupplierBqItem suppBqItem : suppBqItems) {
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
										for (RfpSupplierBqItem childBqItem : suppBqItem.getChildren()) {
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
											List<RfpBqEvaluationComments> bqItemComments = rfpBqEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getBqItem().getId(), null);
											if (CollectionUtil.isNotEmpty(bqItemComments)) {
												String reviews = "";
												for (RfpBqEvaluationComments review : bqItemComments) {
													EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
													bqComment.setCommentBy(review.getLoginName());
													bqComment.setComments(review.getComment());
													comments.add(bqComment);
													reviews += "[ " + review.getLoginName() + " ] " + review.getComment() + "\n";
												}
												evlBqChilItem.setReviews(reviews);
												evlBqChilItem.setReview(comments);
											}
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
					bqSupplierPojo.setBqs(allBqs);
					supplierBq.add(bqSupplierPojo);
				}
			}
		}
		return supplierBq;
	}

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, boolean isMasked) {

		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		List<RfpCq> cqList = rfpCqService.findCqForEvent(eventId);
		for (RfpCq cq : cqList) {
			EvaluationCqPojo cqPojo = new EvaluationCqPojo();
			cqPojo.setName(cq.getName());
			cqPojo.setDescription(cq.getDescription());

			List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
			if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
				for (RfpCqItem cqItem : cq.getCqItems()) {
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
					List<Supplier> suppList = rfpEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);

					for (Supplier supp : suppList) {
						if (isMasked) {
							supp.setCompanyName(MaskUtils.maskName("SUPPLIER", supp.getId(), eventId));
						}

					}
					// Below code to get Suppliers Answers of each CQ Items
					if (CollectionUtil.isNotEmpty(suppList)) {
						// List<RfpSupplierCqItem> responseList =
						// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppList);
						List<RfpSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
						if (CollectionUtil.isNotEmpty(responseList)) {

							for (RfpSupplierCqItem suppCqItem : responseList) {
								EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
								List<RfpSupplierCqOption> listAnswers = rfpSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());

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
									// List<RfpCqOption> rfpSupplierCqOptions = suppCqItem.getCqItem().getCqOptions();
									List<RfpCqOption> rfpSupplierCqOptions = rfpCqOptionDao.findOptionsByCqItem(suppCqItem.getCqItem().getId());// suppCqItem.getCqItem().getCqOptions();
									if (CollectionUtil.isNotEmpty(rfpSupplierCqOptions)) {
										for (RfpCqOption rfpSupplierCqOption : rfpSupplierCqOptions) {
											docIds.add(StringUtils.checkString(rfpSupplierCqOption.getValue()));
										}
									}
									List<EventDocument> eventDocuments = rfpDocumentService.findAllRfpEventDocsNamesByEventIdAndDocIds(docIds);
									if (eventDocuments != null) {
										String str = "";
										for (EventDocument docName : eventDocuments) {
											str = str + docName.getFileName() + "\n";
										}
										cqItemSupplierPojo.setAnswer(str);
									}
								}

								else if (CollectionUtil.isNotEmpty(listAnswers) && (suppCqItem.getCqItem().getCqType() == CqType.LIST || suppCqItem.getCqItem().getCqType() == CqType.CHECKBOX)) {
									String str = "";
									// List<RfpSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfpSupplierCqOption op : listAnswers) {
										str += op.getValue() + "\n";
									}
									cqItemSupplierPojo.setAnswer(str);
								} else if (CollectionUtil.isNotEmpty(listAnswers) && suppCqItem.getCqItem().getCqType() != CqType.DOCUMENT_DOWNLOAD_LINK) {
									String str = "";
									// List<RfpSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfpSupplierCqOption op : listAnswers) {
										// str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() :
										// "");
										int cqAnsListSize = (listAnswers).size();
										int index = (listAnswers).indexOf(op);
										// LOG.info("(cqAnsListindex )********" + index + "______(getListAnswersSize
										// ====)" + op.getCqItem().getListAnswers());
										str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));
									}
									cqItemSupplierPojo.setAnswer(str);
								}
								cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
								// Review Comments
								List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
								List<RfpCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
								if (CollectionUtil.isNotEmpty(comments)) {
									String evalComment = "";
									for (RfpCqEvaluationComments item : comments) {
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

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public RfpEvent cancelEvent(RfpEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer) throws Exception {
		RfpEvent persistObj = rfpEventDao.findById(event.getId());
		persistObj.setStatus(EventStatus.CANCELED);
		persistObj.setCancelReason(event.getCancelReason());
		persistObj = rfpEventDao.update(persistObj);

		// Decrease event count on cancel
		buyerDao.decreaseEventLimitCountByBuyerId(SecurityLibrary.getLoggedInUser().getBuyer().getId());

		if (persistObj != null) {

			byte[] summarySnapshot = null;
			try {
				JasperPrint eventSummary = getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			} catch (JRException e) {
				LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
			}

			RfpEventAudit audit = new RfpEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), persistObj, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Cancel, messageSource.getMessage("event.audit.canceled", new Object[] { persistObj.getEventName() }, Global.LOCALE), summarySnapshot);
			rfpEventAuditDao.save(audit);

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Event '" + persistObj.getEventId() + "' Cancelled", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			// Check if budget checking ERP interface is enabled
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush() && Boolean.TRUE == persistObj.getBusinessUnit().getBudgetCheck()) {
				erpIntegrationService.transferRejectRfsToErp(persistObj, erpSetup, SecurityLibrary.getLoggedInUser());
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
			RfpEvent event = getRfpEventByeventId(eventId);
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

				RfpEventMeeting meetingItem = rfpEventMeetingDao.findById(meetingId);
				EvaluationMeetingPojo meeting = new EvaluationMeetingPojo();
				meeting.setTitle(meetingItem.getTitle());
				if (meetingItem.getAppointmentDateTime() != null) {
					meeting.setAppointmentDateTime(new Date(sdf.format(meetingItem.getAppointmentDateTime())));
				}
				meeting.setRemarks(meetingItem.getRemarks());
				meeting.setVenue(meetingItem.getVenue());

				List<EvaluationMeetingContactsPojo> contacts = new ArrayList<EvaluationMeetingContactsPojo>();
				if (CollectionUtil.isNotEmpty(meetingItem.getRfxEventMeetingContacts())) {
					for (RfpEventMeetingContact mc : meetingItem.getRfxEventMeetingContacts()) {
						EvaluationMeetingContactsPojo contact = new EvaluationMeetingContactsPojo();
						contact.setContactEmail(mc.getContactEmail());
						contact.setContactName(mc.getContactName());
						contact.setContactNumber(mc.getContactNumber());
						contact.setImagePath(imgPath);
						contacts.add(contact);
					}
				}
				meeting.setMeetingContacts(contacts);
				List<EvaluationMeetingPojo> meetings = Arrays.asList(meeting);

				// Supplier Meeting Attendance
				List<EvaluationSuppliersPojo> meetingSuppliers = new ArrayList<EvaluationSuppliersPojo>();

				// rfpEventMeetingDao.getRfpMeetingById(meetingId);
				if (CollectionUtil.isNotEmpty(meetingItem.getInviteSuppliers())) {
					for (Supplier sup : meetingItem.getInviteSuppliers()) {
						EvaluationSuppliersPojo invitedSupplier = new EvaluationSuppliersPojo();
						RfpSupplierMeetingAttendance attendance = rfpSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meetingId, sup.getId());

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
	public RfpEvent concludeRfpEvent(RfpEvent event, User loggedInUser) {
		// Clear Award details if present.
		int deleteCount = rfpEventAwardDao.removeAwardDetails(event.getId());
		RfpEvent dbevent = getRfpEventByeventId(event.getId());

		if (deleteCount > 0) {
			try {
				RfpEventAudit audit = new RfpEventAudit();
				audit.setActionDate(new Date());
				audit.setActionBy(loggedInUser);
				audit.setDescription(messageSource.getMessage("event.award.discarded", new Object[] { dbevent.getEventName() }, Global.LOCALE));
				audit.setEvent(event);
				audit.setAction(AuditActionType.Discard);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISCARD, messageSource.getMessage("event.award.discarded", new Object[] { dbevent.getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error saving RFT Event Award Discard Audit " + e.getMessage());
			}
		}

		dbevent.setStatus(EventStatus.FINISHED);
		dbevent.setAwardedSuppliers(event.getAwardedSuppliers());
		dbevent.setAwardedPrice(event.getAwardedPrice());
		dbevent.setConcludeRemarks(event.getConcludeRemarks());
		dbevent.setConcludeBy(loggedInUser);
		dbevent.setConcludeDate(new Date());
		dbevent.setAwardStatus(null);

		return rfpEventDao.update(dbevent);
	}

	@Override
	@Transactional(readOnly = false)
	public String createNextEvent(RfpEvent rfpEvent, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String concludeRemarks, String[] invitedSupp) throws Exception {
		RfpEvent oldEvent = getRfpEventByeventId(rfpEvent.getId());

		// Clear Award details if present.
		int deleteCount = rfpEventAwardDao.removeAwardDetails(rfpEvent.getId());
		if (deleteCount > 0) {
			try {
				RfpEventAudit audit = new RfpEventAudit();
				audit.setActionDate(new Date());
				audit.setActionBy(loggedInUser);
				audit.setDescription(messageSource.getMessage("event.award.discarded", new Object[] { oldEvent.getEventName() }, Global.LOCALE));
				audit.setEvent(rfpEvent);
				audit.setAction(AuditActionType.Discard);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISCARD, messageSource.getMessage("event.award.discarded", new Object[] { oldEvent.getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error saving RFT Event Award Discard Audit " + e.getMessage());
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
		List<RfpSupplierBq> rfpSupplierBqs = rfpSupplierBqService.findRfpSummarySupplierBqbyEventId(rfpEvent.getId());
		if (StringUtils.checkString(idRfxTemplate).length() > 0) {
			template = rfxTemplateService.getRfxTemplateById(idRfxTemplate);
		}

		if (template != null && Status.INACTIVE == template.getStatus()) {
			LOG.info("inactive Template [" + template.getTemplateName() + "] found for Id .... " + template.getId());
			throw new ApplicationException("Template [" + template.getTemplateName() + "] is Inactive");
		}

		BusinessUnit selectedbusinessUnit = null;
		if (StringUtils.checkString(businessUnitId).length() > 0)
			selectedbusinessUnit = businessUnitService.getBusinessUnitById(businessUnitId);

		if (selectedRfxType != null) {
			switch (selectedRfxType) {
			case RFA: {
				List<RfpSupplierBq> rfpSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfpSupplierBqList = new ArrayList<RfpSupplierBq>();
					for (RfpSupplierBq bq : rfpSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfpSupplierBqList.add(bq);
						}
					}
				}

				AuctionRules auctionRules = new AuctionRules();
				RfaEvent newEvent = rfpEvent.createNextRfaEvent(oldEvent, auctionType, bqId, loggedInUser, invitedSupp, rfpSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
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

				// Auction Rules
				if (newEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || newEvent.getAuctionType() == AuctionType.REVERSE_ENGISH || newEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID || newEvent.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					newEvent.setBillOfQuantity(Boolean.TRUE);
				}

				newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFA", newEvent.getBusinessUnit()));

				RfaEvent newdbEvent = rfaEventService.saveRfaEvent(newEvent, loggedInUser);
				List<RfaSupplierBq> supplierBqs = newEvent.getRfaSupplierBqs();
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfaTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}

				newEvent.setId(newdbEvent.getId());

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
				if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {

					for (RfaEventBq bq : newEvent.getEventBqs()) {
						bq.setRfxEvent(newdbEvent);
						RfaEventBq dbBq = rfaBqDao.saveOrUpdate(bq);
						bq.setId(dbBq.getId());
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

						RfpEventAudit rfpAudit = new RfpEventAudit();
						rfpAudit.setAction(AuditActionType.Create);
						rfpAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfpAudit.setActionDate(new Date());
						rfpAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfpAudit.setEvent(oldEvent);
						eventAuditService.save(rfpAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + "' is pushed to '" + newdbEvent.getEventId() + "'", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				break;
			}
			case RFI: {
				RfiEvent newEvent = rfpEvent.createNextRfiEvent(oldEvent, loggedInUser, invitedSupp);
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

						RfpEventAudit rfpAudit = new RfpEventAudit();
						rfpAudit.setAction(AuditActionType.Create);
						rfpAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfpAudit.setActionDate(new Date());
						rfpAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfpAudit.setEvent(oldEvent);
						eventAuditService.save(rfpAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFP: {

				List<RfpSupplierBq> rfpSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfpSupplierBqList = new ArrayList<RfpSupplierBq>();
					for (RfpSupplierBq bq : rfpSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfpSupplierBqList.add(bq);
						}
					}
				}

				RfpEvent newEvent = rfpEvent.createNextRfpEvent(oldEvent, loggedInUser, invitedSupp, rfpSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());

				if (template != null) {
					newEvent.setTemplate(template);
					createRfpFromTemplate(newEvent, template, null, selectedbusinessUnit, loggedInUser, false);
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
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfpTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}

				// save Contacts
				if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
					for (RfpEventContact contact : newEvent.getEventContacts()) {
						contact.setRfxEvent(newdbEvent);
						saveEventContact(contact);
					}
				}
				// save Supplier
				for (RfpEventSupplier supp : newEvent.getSuppliers()) {
					supp.setRfxEvent(newdbEvent);
					rfpEventSupplierDao.save(supp);
				}

				// save BQ
				if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {

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

						RfpEventAudit rfpAudit = new RfpEventAudit();
						rfpAudit.setAction(AuditActionType.Create);
						rfpAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfpAudit.setActionDate(new Date());
						rfpAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfpAudit.setEvent(oldEvent);
						eventAuditService.save(rfpAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFQ: {
				List<RfpSupplierBq> rfpSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfpSupplierBqList = new ArrayList<RfpSupplierBq>();
					for (RfpSupplierBq bq : rfpSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfpSupplierBqList.add(bq);
						}
					}
				}
				RfqEvent newEvent = rfpEvent.createNextRfqEvent(oldEvent, loggedInUser, invitedSupp, rfpSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());

				if (template != null) {
					newEvent.setTemplate(template);
					rfqEventService.createRfqFromTemplate(newEvent, template, selectedbusinessUnit, loggedInUser, false);
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
				RfqEvent newdbEvent = rfqEventService.saveEvent(newEvent, loggedInUser);
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
						rfqEventService.saveEventContact(contact);
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
						RfqEventBq dbBq = rfqBqDao.saveOrUpdate(bq);
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

						RfpEventAudit rfpAudit = new RfpEventAudit();
						rfpAudit.setAction(AuditActionType.Create);
						rfpAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfpAudit.setActionDate(new Date());
						rfpAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfpAudit.setEvent(oldEvent);
						eventAuditService.save(rfpAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFT: {
				List<RfpSupplierBq> rfpSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfpSupplierBqList = new ArrayList<RfpSupplierBq>();
					for (RfpSupplierBq bq : rfpSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfpSupplierBqList.add(bq);
						}
					}
				}

				RftEvent newEvent = rfpEvent.createNextRftEvent(oldEvent, loggedInUser, invitedSupp, rfpSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());

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

						RfpEventAudit rfpAudit = new RfpEventAudit();
						rfpAudit.setAction(AuditActionType.Create);
						rfpAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfpAudit.setActionDate(new Date());
						rfpAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newDbEvent.getEventId() + " '");
						rfpAudit.setEvent(oldEvent);
						eventAuditService.save(rfpAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newDbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
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
		oldEvent.setConcludeRemarks(rfpEvent.getConcludeRemarks());
		oldEvent.setStatus(EventStatus.FINISHED);
		oldEvent.setConcludeBy(loggedInUser);
		oldEvent.setConcludeDate(new Date());
		oldEvent.setAwardStatus(null);
		rfpEventDao.update(oldEvent);

		try {
			tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(oldEvent.getId(), EventStatus.FINISHED);
		} catch (Exception e) {
			LOG.error("Error updating Tat Report " + e.getMessage(), e);
		}
		return newEventId;
	}

	@Override
	public void createRfpFromTemplate(RfpEvent newEvent, RfxTemplate rfxTemplate, Object object, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRequest) throws ApplicationException {
		LOG.info("----------------------create Rfp From Template call----------------------------");

		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RfpEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfpEventApproval newRfApproval = new RfpEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfpApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfpApprovalUser approvalUser = new RfpApprovalUser();
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
			List<RfpEventAwardApproval> approvalList = new ArrayList<>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfpEventAwardApproval newRfApproval = new RfpEventAwardApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfpAwardApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfpAwardApprovalUser approvalUser = new RfpAwardApprovalUser();
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
			List<RfpEventSuspensionApproval> suspApprovalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateSuspApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Susp Approval Level :" + templateSuspApproval.getLevel());
				RfpEventSuspensionApproval suspesionApproval = new RfpEventSuspensionApproval();
				suspesionApproval.setApprovalType(templateSuspApproval.getApprovalType());
				suspesionApproval.setLevel(templateSuspApproval.getLevel());
				suspesionApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateSuspApproval.getApprovalUsers())) {
					List<RfpSuspensionApprovalUser> rfpSuspApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateSuspApproval.getApprovalUsers()) {
						RfpSuspensionApprovalUser approvalUser = new RfpSuspensionApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(suspesionApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfpSuspApprovalList.add(approvalUser);
					}
					suspesionApproval.setApprovalUsers(rfpSuspApprovalList);
				}
				suspApprovalList.add(suspesionApproval);
			}
			newEvent.setSuspensionApprovals(suspApprovalList);
		}

		if (rfxTemplate.getAddBillOfQuantity() != null) {
			newEvent.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
		}
		if (rfxTemplate.getVisibleViewSupplierName()) {
			newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		}
		newEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());
		List<RfpUnMaskedUser> unmaskingUser = new ArrayList<RfpUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RfpUnMaskedUser newRftUnMaskedUser = new RfpUnMaskedUser();
				newRftUnMaskedUser.setUser(team.getUser());
				newRftUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRftUnMaskedUser);
			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}

		List<RfpTeamMember> teamMembers = new ArrayList<RfpTeamMember>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RfpTeamMember newTeamMembers = new RfpTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setEvent(newEvent);
				teamMembers.add(newTeamMembers);
				// rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(),
				// newTeamMembers.getTeamMemberType(), newEvent.getEventOwner(),
				// StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ",
				// newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ?
				// newEvent.getReferanceNumber() : " ",RfxTypes.RFP);
			}
			newEvent.setTeamMembers(teamMembers);
		}

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
			newEvent.setRfxEnvelop(new ArrayList<RfpEnvelop>());

			for (RfxEnvelopPojo en : envlopeList) {
				LOG.info("envlop name form rfx   :  " + en);

				RfpEnvelop rfienvlope = new RfpEnvelop();
				rfienvlope.setEnvelopTitle(en.getRfxEnvelope());
				rfienvlope.setEnvelopSequence(en.getRfxSequence());
				rfienvlope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rfienvlope.setRfxEvent(newEvent);
				rfienvlope.setEnvelopType(EnvelopType.CLOSED);
				newEvent.getRfxEnvelop().add(rfienvlope);
			}
		}
		// Copy Evaluation Conclusion Users from template
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RfpEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RfpEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RfpEvaluationConclusionUser conclusionUser = new RfpEvaluationConclusionUser();
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
							}
						} else {
							newEvent.setBudgetAmount(new BigDecimal(field.getDefaultValue()));
						}
						LOG.info("budget amount Default value :  " + field.getDefaultValue());
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
					}
					break;
				case SUPPLIER_ACCEPTANCE_DECLARATION:
					if (field.getDefaultValue() != null) {
						Declaration declaration = declarationDao.findById(field.getDefaultValue());
						newEvent.setSupplierAcceptanceDeclaration(declaration);
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
		if (eventIdSettingsDao.isBusinessSettingEnable(loggedInUser.getTenantId(), "RFP")) {
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
		return rfpEventDao.getAwardedSuppliers(eventId);
	}

	@Override
	public RfpEvent getPlainEventById(String eventId) {
		RfpEvent rfpEvent = rfpEventDao.getPlainEventById(eventId);
		for (RfpUnMaskedUser unmask : rfpEvent.getUnMaskedUsers()) {
			unmask.getUser();
		}

		return rfpEvent;
	}

	@Override
	@Transactional(readOnly = false)
	public void suspendEvent(RfpEvent event) {
		if (event.getSuspensionType() == SuspensionType.DELETE_NO_NOTIFY || event.getSuspensionType() == SuspensionType.DELETE_NOTIFY) {
			supplierCqItemDao.deleteSupplierCqItemsForEvent(event.getId());
			try {
				rfpSupplierCqDao.deleteSupplierCqForEvent(event.getId());
			} catch (Exception e) {
			}
			rfpSupplierCommentService.deleteSupplierComments(event.getId());
			rfpSupplierBqItemDao.deleteSupplierBqItemsForEvent(event.getId());
			rfpSupplierBqDao.deleteSupplierBqsForEvent(event.getId());
			rfpSupplierSorItemDao.deleteSupplierSorItemsForEvent(event.getId());
			rfpSupplierSorDao.deleteSupplierSorsForEvent(event.getId());

			rfpEventSupplierDao.updateSubmiTimeOnEventSuspend(event.getId());
		}
		rfpEventDao.suspendEvent(event.getId(), event.getSuspensionType(), event.getSuspendRemarks());
	}

	@Override
	@Transactional(readOnly = false)
	public void resumeEvent(RfpEvent event) {
		rfpEventDao.resumeEvent(event.getId());
	}

	@Override
	public long getEventSuppliersCount(String eventId) {
		return rfpEventSupplierDao.getEventSuppliersCount(eventId);
	}

	@Override
	public List<Date> getAllMeetingDateByEventId(String eventId) {
		return rfpEventMeetingDao.getAllMeetingDateByEventId(eventId);
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		return rfpEventDao.findAssignedTemplateCount(templateId);
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		return rfpEventDao.getPlainEventOwnerByEventId(eventId);
	}

	@Override
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		return rfpEventDao.getPlainTeamMembersForEvent(eventId);
	}

	@Override
	public MobileEventPojo getMobileEventDetails(String id, String userId) throws ApplicationException {
		RfpEvent event = rfpEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(event.getTenantId(), timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			List<Bq> bqlist = new ArrayList<>();
			for (RfpEventBq bq : event.getEventBqs()) {
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
		LOG.info("service method");
		for (EventTimeline timeLine : rfpEventTimeLineDao.getPlainRfpEventTimeline(event.getId())) {
			EventTimelinePojo time = new EventTimelinePojo();
			Date ActivityDatedate = timeLine.getActivityDate();

			String activityDate = df.format(ActivityDatedate);
			try {
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

		// eventPojo.setTimeLine(rfpEventTimeLineDao.getPlainRfpEventTimeline(event.getId()));
		eventPojo.setDocuments(rfpDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRfxEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RfpEnvelop envelope : event.getRfxEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RfpEventApproval eventApproval : event.getApprovals()) {
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
			for (RfpComment comment : event.getComment()) {
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
		else
			eventPojo.setCostCenter("N/A");
		return eventPojo;

	}

	@Override
	public MobileEventPojo getMobileEventDetailsForSupplier(String id, String userId, String tenantId) throws ApplicationException {
		RfpEvent event = rfpEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(tenantId, timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			List<Bq> bqlist = new ArrayList<>();
			for (RfpEventBq bq : event.getEventBqs()) {
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
		LOG.info("service method");
		for (EventTimeline timeLine : rfpEventTimeLineDao.getPlainRfpEventTimeline(event.getId())) {
			EventTimelinePojo time = new EventTimelinePojo();
			Date ActivityDatedate = timeLine.getActivityDate();

			String activityDate = df.format(ActivityDatedate);
			try {
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

		// eventPojo.setTimeLine(rfpEventTimeLineDao.getPlainRfpEventTimeline(event.getId()));
		eventPojo.setDocuments(rfpDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRfxEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RfpEnvelop envelope : event.getRfxEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RfpEventApproval eventApproval : event.getApprovals()) {
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
			for (RfpComment comment : event.getComment()) {
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
		else
			eventPojo.setCostCenter("N/A");
		return eventPojo;

	}

	@Override
	public void downloadEventDocument(String docId, HttpServletResponse response) throws Exception {
		RfpEventDocument docs = rfpDocumentDao.findDocsById(docId);
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
			RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " summary.pdf";
			}
			JasperPrint jasperPrint = rfpEventService.getEvaluationSummaryPdf(event, loggedInUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
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
	public RfpEvent getRfpEventByeventIdForSummaryReport(String eventId) {
		RfpEvent rfp = rfpEventDao.findByEventId(eventId);
		if (rfp.getEventOwner().getBuyer() != null) {
			rfp.getEventOwner().getBuyer().getLine1();
			rfp.getEventOwner().getBuyer().getLine2();
			rfp.getEventOwner().getBuyer().getCity();
			if (rfp.getEventOwner().getBuyer().getState() != null) {
				rfp.getEventOwner().getBuyer().getState().getStateName();
				if (rfp.getEventOwner().getBuyer().getState().getCountry() != null) {
					rfp.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (rfp.getBusinessUnit() != null) {
			rfp.getBusinessUnit().getUnitName();
		}
		if (rfp.getDeliveryAddress() != null) {
			rfp.getDeliveryAddress().getLine1();
			rfp.getDeliveryAddress().getLine2();
			rfp.getDeliveryAddress().getCity();
			if (rfp.getDeliveryAddress().getState() != null) {
				rfp.getDeliveryAddress().getState().getStateName();
				rfp.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getRftReminder())) {
			for (RfpReminder reminder : rfp.getRftReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getSuppliers())) {
			for (RfpEventSupplier item : rfp.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}
		if (rfp.getEventOwner().getBuyer() != null) {
			Buyer buyer = rfp.getEventOwner().getBuyer();
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
		if (CollectionUtil.isNotEmpty(rfp.getMeetings())) {
			for (RfpEventMeeting item : rfp.getMeetings()) {
				item.getStatus().name();
				item.getRemarks();
				item.getVenue();
				item.getAppointmentDateTime();
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingContacts())) {
					for (RfpEventMeetingContact contact : item.getRfxEventMeetingContacts()) {
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
					for (RfpEventMeetingDocument docs : item.getRfxEventMeetingDocument()) {
						docs.getId();
						docs.getFileName();
						docs.getFileSizeInKb();
						docs.getCredContentType();
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getComment())) {
			for (RfpComment comment : rfp.getComment()) {
				comment.getComment();
				comment.getCreatedBy();
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getName();
				}
				comment.getLoginName();
				comment.getUserName();
				comment.getRfpEvent();
			}
		}

		if (CollectionUtil.isNotEmpty(rfp.getIndustryCategories())) {
			for (IndustryCategory industryCategory : rfp.getIndustryCategories()) {
				industryCategory.getName();
				industryCategory.getCreatedBy();
				if (industryCategory.getCreatedBy() != null) {
					industryCategory.getCreatedBy().getName();
				}
				industryCategory.getCode();
				industryCategory.getBuyer();
			}
		}
		return rfp;
	}

	@Override
	public RfpSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId, String tenantId) {
		RfpSupplierBq rfpSupplierBq = rfpEventDao.getSupplierBQOfLeastTotalPrice(eventId, bqId);
		if (rfpSupplierBq != null) {

			if (rfpSupplierBq.getSupplierBqItems() != null) {
				for (RfpSupplierBqItem items : rfpSupplierBq.getSupplierBqItems()) {
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
		return rfpSupplierBq;
	}

	@Override
	public RfpSupplierBq getSupplierBQOfLowestItemisedPrice(String eventId, String bqId, String tenantId) {
		RfpSupplierBq rfpSupplierBq = new RfpSupplierBq();
		List<RfpSupplierBqItem> bqItemList = new ArrayList<RfpSupplierBqItem>();
		List<RfpSupplierBq> suppBqList = rfpEventDao.getSupplierBQOfLowestItemisedPrize(eventId, bqId);
		if (CollectionUtil.isNotEmpty(suppBqList)) {
			int bqItemCount = 1;
			for (RfpSupplierBq bq : suppBqList) {
				if (CollectionUtil.isNotEmpty(bq.getSupplierBqItems())) {
					for (RfpSupplierBqItem item : bq.getSupplierBqItems()) {
						item.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, item.getSupplier().getId()));
						// LOG.info(item.getBqItem().getId());
						if (item.getBqItem() != null) {

							if (item.getBqItem().getUom() != null) {
								LOG.info("items.getBqItem().getUom()  : " + item.getBqItem().getUom().getUom());
								item.getBqItem().getUom().getUom();
							}

							if (item.getBqItem().getOrder() == 0) {
								if (1 == bqItemCount) {
									bqItemList.add(item);
								}

							} else {
								LOG.info(item.getBqItem().getId() + "---------------" + item.getSupplier().getId());
								RfpSupplierBqItem bqItem = rfpEventDao.getMinItemisePrice(item.getBqItem().getId(), eventId);
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
									rfpSupplierBq.setBq(bq.getBq());
								}
							}

						}

						// LOG.info(bqItem);

					}
					bqItemCount++;
				}
				LOG.info(bq.getSupplierBqItems().size() + "..........." + bqItemList.size());
				rfpSupplierBq.setSupplierBqItems(bqItemList);

			}

		}

		return rfpSupplierBq;
	}

	@Override
	@Transactional(readOnly = true)
	public RfpSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId, String tenantId, String awardId) {
		RfpSupplierBq rfpSupplierBq = rfpEventDao.getSupplierBQwithSupplierId(eventId, bqId, supplierId);
		if (rfpSupplierBq != null) {

			if (rfpSupplierBq.getSupplierBqItems() != null) {
				for (RfpSupplierBqItem items : rfpSupplierBq.getSupplierBqItems()) {
					RfpEventAwardDetails rfpEventAwardDetails = null;
					if (StringUtils.checkString(awardId).length() > 0) {
						rfpEventAwardDetails = rfpEventAwardDetailsDao.rfpEventAwardByEventIdandBqId(awardId, items.getBqItem().getId());
					}
					items.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, (rfpEventAwardDetails != null && rfpEventAwardDetails.getSupplier() != null) ? rfpEventAwardDetails.getSupplier().getId() : items.getSupplier().getId()));
					if (items.getBqItem() != null) {
						items.getBqItem().getId();
						items.getBqItem().getLevel();
						if (items.getBqItem().getUom() != null) {
							items.getBqItem().getUom().getUom();
						}
					}

					// LOG.info(items.getBqItem().getItemName());
				}
			}
		}
		return rfpSupplierBq;
	}

	@Override
	public RfpEvent getRfpEventWithIndustryCategoriesByEventId(String eventId) {
		RfpEvent event = rfpEventDao.findByEventId(eventId);
		if (event != null) {
			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				for (IndustryCategory indCat : event.getIndustryCategories()) {
					indCat.getCode();
				}
			}
		}
		return event;
	}

	@Override
	public boolean isExistsRfpEventId(String tenantId, String eventId) {
		return rfpEventDao.isExistsRfpEventId(tenantId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfpTeamMember> addAssociateOwners(User createdBy, RfpEvent newEvent) {
		List<User> adminUser = userDao.fetchAllActivePlainAdminUsersForTenant(createdBy.getTenantId());
		List<RfpTeamMember> teamMemberList = new ArrayList<RfpTeamMember>();
		if (CollectionUtil.isNotEmpty(adminUser)) {
			for (User user : adminUser) {
				RfpTeamMember teamMember = new RfpTeamMember();
				teamMember.setUser(user);
				teamMember.setEvent(newEvent);
				teamMember.setTeamMemberType(TeamMemberType.Associate_Owner);
				teamMemberList.add(teamMember);
			}
			newEvent.setTeamMembers(teamMemberList);
		}
		rfpEventDao.saveOrUpdate(newEvent);
		return teamMemberList;
	}

	@Override
	@Transactional(readOnly = false)
	public void setDefaultEventContactDetail(String loggedInUserId, String eventId) {
		RfpEventContact eventContact = new RfpEventContact();
		User user = userDao.findById(loggedInUserId);
		eventContact.setContactName(user.getName());
		eventContact.setDesignation(user.getDesignation());
		eventContact.setContactNumber(user.getPhoneNumber());
		eventContact.setComunicationEmail(user.getCommunicationEmail());
		if (user.getBuyer() != null) {
			eventContact.setMobileNumber(user.getBuyer().getMobileNumber());
			eventContact.setFaxNumber(user.getBuyer().getFaxNumber());
		}
		RfpEvent rfpEvent = new RfpEvent();
		rfpEvent.setId(eventId);
		eventContact.setRfxEvent(rfpEvent);
		saveEventContact(eventContact);
	}

	@Override
	public RfpEvent findRfpEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId) {
		return rfpEventDao.findRfpEventByErpAwardRefNoAndTenantId(erpAwardRefId, tenantId);
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
	public String createPrFromAward(RfpEventAward rfpEventAward, String templateId, String userId, User loggedInUser) throws ApplicationException {
		User createdBy = userDao.findById(userId);
		List<String> supplierList = new ArrayList<String>();
		// List<ProductCategory> ctegoryList =
		// productCategoryMaintenanceService.getAllProductCategoryByTenantId(loggedInUser.getTenantId());
		// Supplier -> List of Sections -> List of Items
		Map<String, Map<String, List<PrItem>>> data = new HashMap<String, Map<String, List<PrItem>>>();
		String value = "";
		try {
			LOG.info("rfpEventAward.getRfxAwardDetails()--------" + rfpEventAward.getRfxAwardDetails().size());
			if (CollectionUtil.isNotEmpty(rfpEventAward.getRfxAwardDetails())) {

				BqItem section = null;

				for (RfpEventAwardDetails rfxAward : rfpEventAward.getRfxAwardDetails()) {
					LOG.info("order--------" + rfxAward.getBqItem().getLevel() + "--" + rfxAward.getBqItem().getOrder());
					LOG.info("---------------bqitemId--------------------" + rfxAward.getBqItem().getBqId());

					BqItem dbBqItem = rfpBqItemDao.getBqItemByBqIdAndBqItemId(rfxAward.getBqItem().getId());
					if (dbBqItem.getOrder() == 0) {
						section = dbBqItem;
					} else {
						String sid = rfxAward.getSupplier().getId();
						RfpSupplierBqItem supplierBqItem = rfpSupplierBqItemDao.getSupplierBqItemByBqItemId(rfxAward.getBqItem().getId(), sid);
						LOG.info("rfpEventAward.getRfxAwardDetails() new--------------" + rfxAward.getSupplier().getId());
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

							/*if (StringUtils.checkString(rfxAward.getProductCategory()).length() <= 0) {
								throw new ApplicationException("Please select a category for non-catalog item : " + dbBqItem.getItemName());
							}
*/
							ProductCategory category = productCategoryMaintenanceService.getProductCategoryById(rfxAward.getProductCategory());
							if (category != null) {
								item.setProductCategory(category);
							}
							item.setFreeTextItemEntered(Boolean.TRUE);
							item.setItemName(section.getItemName() == null? " " : prService.replaceSmartQuotes(dbBqItem.getItemName()));
							item.setItemDescription(section.getItemDescription() == null ? " " : prService.replaceSmartQuotes(dbBqItem.getItemDescription()));
							item.setUnit(dbBqItem.getUom());
							BigDecimal ap = rfxAward.getAwardedPrice();
							item.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, BigDecimal.ROUND_HALF_UP));
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
							LOG.info("*******SSS***********" + productItem.getUom());
							BigDecimal ap = rfxAward.getAwardedPrice();
							item.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, BigDecimal.ROUND_HALF_UP));
						}

						item.setBuyer(loggedInUser.getBuyer());

						if (supplierBqItem.getTaxType() == TaxType.Amount) {
							item.setItemTax(supplierBqItem.getTax() != null ? supplierBqItem.getTax().divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).toString() : null);
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
					RfpEvent rfxEvent = rfpEventService.getEventById(rfpEventAward.getRfxEvent().getId());
					Pr pr = prService.copyFromTemplateWithAward(templateId, createdBy, loggedInUser, loggedInUser.getTenantId(), null, favouriteSupplier, sections, rfxEvent);

					if (i == 0) {
						value = (StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : "") + ": " + pr.getPrId();
					} else {
						value += ", " + pr.getPrId();
					}
					LOG.info("*******" + pr.getPrId());

					i++;
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
					 * } level++; } }
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
	public String createContractFromAward(RfpEventAward rfpEventAward, String eventId, String contractStartDate, String contractEndDate, String groupCodeHid, String referenceNumberHid, User loggedInUser, HttpSession session, String contractCreator) throws ApplicationException {
		List<String> supplierList = new ArrayList<String>();

		Map<String, List<ProductContractItems>> map = new HashMap<>();
		String value = "";
		try {
			RfpEvent event = rfpEventDao.findByEventId(eventId);

			LOG.info("rfpEventAward.getRfxAwardDetails()--------" + rfpEventAward.getRfxAwardDetails().size());
			if (CollectionUtil.isNotEmpty(rfpEventAward.getRfxAwardDetails())) {

				for (RfpEventAwardDetails rfxAward : rfpEventAward.getRfxAwardDetails()) {
					LOG.info("order--------" + rfxAward.getBqItem().getLevel() + "--" + rfxAward.getBqItem().getOrder());
					LOG.info("---------------bqitemId--------------------" + rfxAward.getBqItem().getBqId());

					BqItem dbBqItem = rfpBqItemDao.getBqItemByBqIdAndBqItemId(rfxAward.getBqItem().getId());
					if (dbBqItem.getOrder() != 0) {
						String sid = rfxAward.getSupplier().getId();
						RfpSupplierBqItem supplierBqItem = rfpSupplierBqItemDao.getSupplierBqItemByBqItemId(rfxAward.getBqItem().getId(), sid);
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
							item.setProductItemType(null); // UI List
							BigDecimal ap = rfxAward.getAwardedPrice();
							item.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, BigDecimal.ROUND_HALF_UP));
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
							item.setUnitPrice(ap.divide(supplierBqItem.getQuantity(), 6, BigDecimal.ROUND_HALF_UP));
						}

						item.setQuantity(supplierBqItem.getQuantity());
						item.setBalanceQuantity(item.getQuantity());
						item.setTotalAmount(item.getUnitPrice().multiply(item.getQuantity()));

						if (supplierBqItem.getTaxType() == TaxType.Amount) {
							item.setTax(supplierBqItem.getTax() != null ? supplierBqItem.getTax().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
							item.setTax(item.getTax().divide(item.getTotalAmount(), 2, RoundingMode.HALF_UP));

							item.setTaxAmount(supplierBqItem.getTax());
						} else {
							item.setTax(supplierBqItem.getTax() != null ? supplierBqItem.getTax() : BigDecimal.ZERO);
							BigDecimal taxAmount = BigDecimal.ZERO.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_HALF_UP);
							if (item.getTax() != null) {
								taxAmount = item.getTax().setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_HALF_UP);
								taxAmount = item.getTotalAmount().multiply(taxAmount).divide(new BigDecimal(100), (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_HALF_UP);
								taxAmount = taxAmount.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), BigDecimal.ROUND_HALF_UP);
							}
							item.setTaxAmount(taxAmount);
						}
						if (item.getTax() == null) {
							item.setTax(BigDecimal.ZERO);
						}
						if (item.getTaxAmount() == null) {
							item.setTaxAmount(BigDecimal.ZERO);
						}

						item.setTotalAmountWithTax(item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0).setScale(Integer.parseInt("2"), BigDecimal.ROUND_DOWN));

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
					User user = userService.getUsersById(contractCreator);
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
					contract.setEventId(event.getEventId()); // ID
					if (eventIdSettingDao.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), "CTR")) {
						contract.setIdBasedOnBusinessUnit(Boolean.TRUE);
					}
					contract.setContractName(event.getEventName());
					contract.setSupplier(favouriteSupplier);
					contract.setCreatedDate(new Date());
					contract.setBusinessUnit(event.getBusinessUnit());
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
		getRfpEventsForExport(tenantId, eventArr, resultList, searchFilterEventPojo, select_all, input, startDate, endDate);
		return resultList;
	}

	private void getRfpEventsForExport(String tenantId, String[] eventArr, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		List<RfpEvent> rftList = rfpEventDao.getEventsByIds(tenantId, eventArr, searchFilterEventPojo, select_all, input, startDate, endDate);
		if (CollectionUtil.isNotEmpty(rftList)) {
			for (RfpEvent event : rftList) {
				DraftEventPojo eventPojo = new DraftEventPojo();
				eventPojo.setId(event.getId());
				if (event.getEventDescription() != null) {
					eventPojo.setEventDescription(event.getEventDescription());
				}
				eventPojo.setCostCenter(event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "");
				eventPojo.setInvitedSupplierCount(event.getSuppliers().size());
				eventPojo.setEventName(event.getEventName());
				eventPojo.setReferenceNumber(event.getReferanceNumber());
				eventPojo.setSysEventId(event.getEventId());
				eventPojo.setEventStart(event.getEventStart());
				eventPojo.setEventEnd(event.getEventEnd());
				eventPojo.setType(RfxTypes.RFP);
				eventPojo.setEventUser(event.getEventOwner().getName());
				eventPojo.setDeliveryDate(event.getDeliveryDate());
				eventPojo.setVisibility(event.getEventVisibility());
				eventPojo.setPublishDate(event.getEventPublishDate());
				eventPojo.setValidityDays(event.getSubmissionValidityDays());
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

				RfpEventSupplier leadingSupplier = null;
				int submittedCount = 0;
				int acceptedCount = 0;
				BigDecimal leadingAmount = BigDecimal.ZERO;
				for (RfpEventSupplier es : event.getSuppliers()) {
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
					for (RfpEventBq eventBq : event.getEventBqs()) {
						// int count = rfpSupplierBqItemDao.countIncompleteBqItemByBqIdsForSupplier(eventBq.getId(),
						// es.getSupplier().getId());
						RfpSupplierBq supBq = rfpSupplierBqDao.findBqByEventIdAndSupplierIdOfQualifiedSupplier(event.getId(), eventBq.getId(), es.getSupplier().getId());
						if (supBq == null) {
							allOk = false;
							break;
						}
						bqTotal = bqTotal.add(supBq.getTotalAfterTax());
					}
					if (!allOk)
						continue;
					if (leadingAmount.compareTo(new BigDecimal(0.0)) == 0) {
						leadingSupplier = es;
						leadingAmount = bqTotal;
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
	public JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rfpSummary = new ArrayList<EvaluationPojo>();
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RfpEvent event = getRfpEventByeventId(eventId);

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
				String type = "RFP";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName("");
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> SupplierList = rfpEventSupplierService.getAllSuppliersByEventId(eventId);
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
				List<Bq> bqs = rfpEventBqDao.findBqbyEventId(eventId);
				if (CollectionUtil.isNotEmpty(bqs)) {
					for (Bq bq : bqs) {
						EvaluationBqPojo supBqs = new EvaluationBqPojo();
						supBqs.setName(bq.getName());
						List<EvaluationSuppliersPojo> suppBqComments = new ArrayList<EvaluationSuppliersPojo>();
						if (CollectionUtil.isNotEmpty(SupplierList)) {
							for (EventSupplier supplier : SupplierList) {
								EvaluationSuppliersPojo supList = new EvaluationSuppliersPojo();
								RfpSupplierBq supBq = rfpSupplierBqDao.findBqByBqId(bq.getId(), supplier.getSupplier().getId());
								supList.setSupplierName(supplier.getSupplier() != null ? supplier.getSupplier().getCompanyName() : "");
								if (supBq != null) {
									supList.setGrandTotal(df.format(supBq.getGrandTotal()));
									supList.setTotalItemTaxAmt(supBq.getTotalAfterTax() != null ? df.format(supBq.getTotalAfterTax()) : "");
								}
								String comments = "";
								List<RfpBqTotalEvaluationComments> comment = rfpBqTotalEvaluationCommentsService.getComments(supplier.getSupplier().getId(), eventId, bq.getId(), SecurityLibrary.getLoggedInUser());
								if (CollectionUtil.isNotEmpty(comment)) {
									for (RfpBqTotalEvaluationComments leadComments : comment) {
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

				rfpSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rfpSummary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfpSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationSuppliersBqPojo> getAllSupplierBqforEvaluationSummary(List<EventSupplier> supplierList, String eventId, String envelopeId) {

		List<EvaluationSuppliersBqPojo> supplierBq = new ArrayList<EvaluationSuppliersBqPojo>();

		if (CollectionUtils.isNotEmpty(supplierList)) {
			for (EventSupplier supItem : supplierList) {
				if (supItem.getDisqualify() == Boolean.FALSE) {
					EvaluationSuppliersBqPojo bqSupplierPojo = new EvaluationSuppliersBqPojo();

					bqSupplierPojo.setSupplierName(supItem.getSupplierCompanyName());

					List<Bq> bqs = rfpEventBqDao.findBqbyEventIdAndEnvelopeId(eventId, envelopeId);

					List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Bq bq : bqs) {
							EvaluationBqPojo bqItem = new EvaluationBqPojo();
							bqItem.setName(bq.getName());
							List<RfpSupplierBqItem> suppBqItems = rfpSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());
							List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RfpSupplierBqItem suppBqItem : suppBqItems) {
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
										for (RfpSupplierBqItem childBqItem : suppBqItem.getChildren()) {
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
											List<RfpBqEvaluationComments> bqItemComments = rfpBqEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getBqItem().getId(), null);
											if (CollectionUtil.isNotEmpty(bqItemComments)) {
												String reviews = "";
												for (RfpBqEvaluationComments review : bqItemComments) {
													EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
													bqComment.setCommentBy(review.getLoginName());
													bqComment.setComments(review.getComment());
													comments.add(bqComment);
													reviews += "[ " + review.getLoginName() + " ] " + review.getComment() + "\n";
												}
												evlBqChilItem.setReviews(reviews);
												evlBqChilItem.setReview(comments);
											}
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
					bqSupplierPojo.setBqs(allBqs);
					supplierBq.add(bqSupplierPojo);
				}
			}
		}
		return supplierBq;

	}

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, String envelopeId) {

		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		List<RfpCq> cqList = rfpCqService.findCqForEventByEnvelopeId(eventId, envelopeId);
		for (RfpCq cq : cqList) {
			EvaluationCqPojo cqPojo = new EvaluationCqPojo();
			cqPojo.setName(cq.getName());
			cqPojo.setDescription(cq.getDescription());

			List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
			if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
				for (RfpCqItem cqItem : cq.getCqItems()) {
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
					List<Supplier> suppList = rfpEventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);

					// Below code to get Suppliers Answers of each CQ Items
					if (CollectionUtil.isNotEmpty(suppList)) {
						// List<RfpSupplierCqItem> responseList =
						// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppList);
						List<RfpSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
						if (CollectionUtil.isNotEmpty(responseList)) {
							for (RfpSupplierCqItem suppCqItem : responseList) {

								EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
								cqItemSupplierPojo.setSupplierName(suppCqItem.getSupplier().getCompanyName());
								List<RfpSupplierCqOption> listAnswers = rfpSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
								if (suppCqItem.getCqItem().getCqType() == CqType.TEXT) {
									cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
								} else if (CollectionUtil.isNotEmpty(listAnswers)) {
									String str = "";
									// List<RfpSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfpSupplierCqOption op : listAnswers) {
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
								List<RfpCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
								if (CollectionUtil.isNotEmpty(comments)) {
									String evalComment = "";
									for (RfpCqEvaluationComments item : comments) {
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
			LOG.error("Could not generate Bidding English PDF Report : " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private List<EvaluationAuctionBiddingPojo> buildEvalutionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			List<EventSupplier> supplierList = rfpEventSupplierDao.getAllSuppliersByEventId(eventId);
			RfpEvent event = rfpEventDao.findByEventId(eventId);

			if (event != null) {

				RfpEnvelop envelop = rfpEnvelopService.getRfpEnvelopById(evenvelopId);

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
					 */ }
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				buildSupplierCountData(supplierList, auction);
				buildHeadingReport(event, supplierList, auction, sdf, isMasked, envelop, true);
				auction.setIsMask(isMasked);
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
				buildMatchingIpAddressData(eventId, auction, supplierList);
				buildSupplierLineChartAndData(sdf, event, supplierList, auction, isMasked, envelop);
				List<String> sortedSupplierList = new ArrayList<>();
				List<String> top3Supplier = new ArrayList<>();

				buildEnvoleBQData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);
				buildEnvoleSORData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);
				buildEnvelopeCQData(event, auction, sdf, envelop, isMasked);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);

		} catch (Exception e) {
			LOG.error("Could not get build Evalution Report Data :" + e.getMessage(), e);
		}
		return auctionSummary;

	}

	private void buildEvaluatorDeclarationAcceptData(SimpleDateFormat sdf, String envelopId, String eventId, EvaluationAuctionBiddingPojo auction) {
		List<RfpEvaluatorDeclaration> evalutorDeclarationList = rfpEvaluatorDeclarationDao.getAllEvaluatorDeclarationByEnvelopAndEventId(envelopId, eventId);
		List<EvaluationDeclarationAcceptancePojo> evaluationDeclarationList = new ArrayList<EvaluationDeclarationAcceptancePojo>();
		if (CollectionUtil.isNotEmpty(evalutorDeclarationList)) {
			for (RfpEvaluatorDeclaration rfpEvaluatorDeclaration : evalutorDeclarationList) {
				EvaluationDeclarationAcceptancePojo evaluationDeclarationPojo = new EvaluationDeclarationAcceptancePojo();
				evaluationDeclarationPojo.setEvaluationCommittee(Boolean.TRUE == rfpEvaluatorDeclaration.getIsLeadEvaluator() ? "Evaluation Owner" : "Evaluation Team");
				evaluationDeclarationPojo.setAcceptedDate(sdf.format(rfpEvaluatorDeclaration.getAcceptedDate()));
				if (rfpEvaluatorDeclaration.getUser() != null) {
					evaluationDeclarationPojo.setUsername(rfpEvaluatorDeclaration.getUser().getName());
					evaluationDeclarationPojo.setUserLoginEmail(rfpEvaluatorDeclaration.getUser().getLoginId());
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
					if (suppItem.getSubmitted() != null && suppItem.getSubmitted() == true) {
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
			LOG.error("Could not get build Supplier Count Data :" + e.getMessage(), e);
		}
		return participatedSupplier;
	}

	private void buildHeadingReport(RfpEvent event, List<EventSupplier> supplierList, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, boolean isMasked, RfpEnvelop envelop, boolean isEvaluation) {
		try {
			List<EvaluationAuctionBiddingPojo> headerBqList = new ArrayList<EvaluationAuctionBiddingPojo>();
			// List<RfpEventBq> bq = rfpEventBqDao.findBqsByEventId(event.getId());

			List<RfpEventBq> bq = rfpEventBqDao.findBqbyEventIdAndEnvelopId(event.getId(), envelop.getId());
			EvaluationAuctionBiddingPojo headerPojo = new EvaluationAuctionBiddingPojo();
			if (CollectionUtil.isNotEmpty(bq)) {
				for (RfpEventBq rfpEventBq : bq) {
					headerPojo = new EvaluationAuctionBiddingPojo();
					List<RfaSupplierBqPojo> rfpEventSupplierIds = rfpSupplierBqDao.getAllRfpTopCompletedEventSuppliersIdByEventId(event.getId(), 5, rfpEventBq.getId());
					headerPojo.setHeaderBqName(rfpEventBq.getName());

					RfaSupplierBqPojo leadSupplier = null;
					for (RfaSupplierBqPojo rfaSupplierBqPojo : rfpEventSupplierIds) {

						if (rfaSupplierBqPojo.getCompleteness() == rfaSupplierBqPojo.getTotalItem()) {
							leadSupplier = rfaSupplierBqPojo;
							break;
						}
					}
					headerPojo.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
					buildLeadSupplierData(event, headerPojo, leadSupplier, rfpEventBq, envelop);
					buildTopCompletedBarChartData(event, headerPojo, rfpEventSupplierIds, isMasked, envelop);
					buildDisqualificationSuppliersData(event.getId(), headerPojo, isMasked, envelop, sdf, isEvaluation);
					buildMatchingIpAddressData(event.getId(), headerPojo, supplierList);
					headerBqList.add(headerPojo);
				}
			} else {
				headerPojo.setHeaderBqName("");
				buildDisqualificationSuppliersData(event.getId(), headerPojo, isMasked, envelop, sdf, isEvaluation);
				buildMatchingIpAddressData(event.getId(), headerPojo, supplierList);
				headerBqList.add(headerPojo);
			}
			auction.setHeader(headerBqList);
		} catch (Exception e) {
			LOG.error("Could not get build Heading Report :" + e.getMessage(), e);
		}

	}

	private void buildLeadSupplierData(RfpEvent event, EvaluationAuctionBiddingPojo auction, RfaSupplierBqPojo leadingbid, RfpEventBq rfaEventBq, RfpEnvelop envelop) {
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
				auction.setLeadSuppliergrandTotal((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + formatedDecimalNumber(event.getDecimal(), leadingbid.getTotalAfterTax()));
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					auction.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), leadingbid.getSupplierId(), envelop.getId()));
				} else {
					auction.setSupplierCompanyName(leadingbid.getSupplierCompanyName());
				}
				auction.setRevisedGrandTotal(leadingbid.getTotalAfterTax());
				auction.setBqName(leadingbid.getBqName());
			}

			auction.setSupplier(leadingbid);
		} catch (Exception e) {
			LOG.error("Could not get build Lead Supplier Data :" + e.getMessage(), e);
		}

	}

	private void buildTopCompletedBarChartData(RfpEvent event, EvaluationAuctionBiddingPojo auction, List<RfaSupplierBqPojo> eventBq, boolean isMasked, RfpEnvelop envelop) {
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
			LOG.error("Could not get Supplier Bidding Price Details :" + e.getMessage(), e);
		}
		auction.setBiddingPrice(bidPriceList);
	}

	private char[] c = new char[] { 'K', 'M', 'B', 'T' };

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

	private void buildDisqualificationSuppliersData(String eventId, EvaluationAuctionBiddingPojo auction, boolean isMasked, RfpEnvelop envelop, SimpleDateFormat sdf, boolean isEvaluation) {
		try {
			List<RfpEventSupplier> supplierList = null;
			if (isEvaluation) {
				supplierList = rfpEventSupplierService.findDisqualifySupplierForEvaluationReportByEventId(eventId);
			} else {
				supplierList = rfpEventSupplierService.findDisqualifySupplierByEventId(eventId);
			}
			List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
			if (CollectionUtils.isNotEmpty(supplierList)) {
				if (supplierList.size() == 1) {
					for (RfpEventSupplier supplier : supplierList) {
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
					for (int j = 0; j < supplierList.size(); j++) {
						/*
						 * if (SupplierList.size() == 1) { EvaluationSuppliersPojo e = new EvaluationSuppliersPojo();
						 * e.setSupplierName(SupplierList.get(j).getSupplierCompanyName());
						 * e.setRemark(SupplierList.get(j).getDisqualifyRemarks()); allSuppliers.add(e); break; }
						 */
						EvaluationSuppliersPojo e = new EvaluationSuppliersPojo();
						e.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), supplierList.get(j).getSupplier().getId(), envelop.getId())) : supplierList.get(j).getSupplierCompanyName());
						if (StringUtils.checkString(supplierList.get(j).getRejectionRemarks()).length() > 0 && !isEvaluation) {
							e.setRemark(supplierList.get(j).getRejectionRemarks());
						} else {
							e.setRemark(StringUtils.checkString(supplierList.get(j).getDisqualifyRemarks()));
						}
						e.setDisqualifiedTime(supplierList.get(j).getDisqualifiedTime() != null ? sdf.format(supplierList.get(j).getDisqualifiedTime()) : null);
						e.setDisqualifiedBy(supplierList.get(j).getDisqualifiedBy() != null ? supplierList.get(j).getDisqualifiedBy().getName() : null);
						e.setDisqualifiedEnvelope(supplierList.get(j).getDisqualifiedEnvelope() != null && StringUtils.checkString(supplierList.get(j).getDisqualifiedEnvelope().getEnvelopTitle()).length() > 0 ? supplierList.get(j).getDisqualifiedEnvelope().getEnvelopTitle() : null);
						e.setIsQualify(supplierList.get(j).getDisqualify());
						if (j % 2 == 0) {
							if (supplierList.size() > j + 1) {
								e.setSupplierName2(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), supplierList.get(j + 1).getSupplier().getId(), envelop.getId())) : supplierList.get(j + 1).getSupplierCompanyName());
								if (StringUtils.checkString(supplierList.get(j + 1).getRejectionRemarks()).length() > 0 && !isEvaluation) {
									e.setRemark2(supplierList.get(j + 1).getRejectionRemarks());
								} else {
									e.setRemark2(StringUtils.checkString(supplierList.get(j + 1).getDisqualifyRemarks()));
								}
								e.setDisqualifiedTime2(supplierList.get(j + 1).getDisqualifiedTime() != null ? sdf.format(supplierList.get(j + 1).getDisqualifiedTime()) : null);
								e.setDisqualifiedBy2(supplierList.get(j + 1).getDisqualifiedBy() != null ? supplierList.get(j + 1).getDisqualifiedBy().getName() : null);
								e.setDisqualifiedEnvelope2(supplierList.get(j + 1).getDisqualifiedEnvelope() != null && StringUtils.checkString(supplierList.get(j + 1).getDisqualifiedEnvelope().getEnvelopTitle()).length() > 0 ? supplierList.get(j + 1).getDisqualifiedEnvelope().getEnvelopTitle() : null);
								e.setIsQualify2(supplierList.get(j + 1).getDisqualify());
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
			LOG.error("Could not get build Disqualification Suppliers Data :" + e.getMessage(), e);
		}
	}

	private void buildMatchingIpAddressData(String eventId, EvaluationAuctionBiddingPojo auction, List<EventSupplier> participatedSupplier) {
		try {
			List<EvaluationBiddingIpAddressPojo> ipAddressList = new ArrayList<EvaluationBiddingIpAddressPojo>();
			Map<String, String> hm = new HashMap<String, String>();
			int i = 0;
			if (CollectionUtil.isNotEmpty(participatedSupplier)) {
				for (EventSupplier rfpSupplier : participatedSupplier) {
					if (StringUtils.checkString(rfpSupplier.getIpAddress()).length() > 0) {
						if (hm.containsKey(rfpSupplier.getIpAddress())) {
							if (StringUtils.checkString(hm.get(rfpSupplier.getIpAddress())).length() > 0) {
								hm.put(rfpSupplier.getIpAddress(), hm.get(rfpSupplier.getIpAddress()) + " & " + rfpSupplier.getSupplierCompanyName());
							} else {
								hm.put(rfpSupplier.getIpAddress(), rfpSupplier.getSupplierCompanyName());
							}
							i++;
						} else {
							int flag = 0;
							for (EventSupplier rfpSupplierIp : participatedSupplier) {
								if (StringUtils.checkString(rfpSupplierIp.getIpAddress()).length() > 0) {
									if (rfpSupplierIp.getIpAddress().equals(rfpSupplier.getIpAddress())) {
										flag++;
									}
								}
							}
							if (flag > 1) {
								hm.put(rfpSupplier.getIpAddress(), rfpSupplier.getSupplierCompanyName());

							}
						}
					}
				}
			}
			if (i >= 1) {
				for (Map.Entry<String, String> entry : hm.entrySet()) {
					EvaluationBiddingIpAddressPojo ipNumber = new EvaluationBiddingIpAddressPojo();
					ipNumber.setIpAddress((String) entry.getKey());
					ipNumber.setSupplierCompanyName((String) entry.getValue());
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

				}
			}
			auction.setIpAddressList(EvaluationBiddingIpAddressPojo1);
		} catch (Exception e) {
			LOG.error("Could not get build Matching IpAddress Data :" + e.getMessage(), e);
		}

	}

	private void buildEvaluatorsSummary(List<RfpEvaluatorUser> evaluators, EvaluationAuctionBiddingPojo auction) {
		try {
			List<EvaluationBqItemComments> evaluationSummary = new ArrayList<EvaluationBqItemComments>();
			for (RfpEvaluatorUser rfaEvaluatorUser : evaluators) {
				EvaluationBqItemComments comments = new EvaluationBqItemComments();
				comments.setCommentBy(rfaEvaluatorUser.getUser() != null ? rfaEvaluatorUser.getUser().getName() : "N/A");
				comments.setComments(StringUtils.checkString(rfaEvaluatorUser.getEvaluatorSummary()));
				comments.setFileName(rfaEvaluatorUser.getFileName() != null ? rfaEvaluatorUser.getFileName() : "N/A");
				evaluationSummary.add(comments);
			}
			auction.setEvaluationSummary(evaluationSummary);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildEventDetailData(SimpleDateFormat sdf, RfpEvent event, EvaluationAuctionBiddingPojo auction) {
		try {
			String auctionDate = event.getEventStart() != null ? sdf.format(event.getEventStart()) : "" + "-" + (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			String auctionTitle = "PRE & POST AUCTION PRICE (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : " ") + ")";
			String netSavingTitle = "Saving based on Budged(%)";
			auction.setAuctionId(event.getEventId());
			auction.setReferenceNo(event.getReferanceNumber());
			auction.setAuctionName(event.getEventName());
			auction.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
			auction.setDateTime(auctionDate);
			auction.setAuctionTitle(auctionTitle);
			auction.setNetSavingTitle(netSavingTitle);
			auction.setEventType(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			auction.setOwnerWithContact(event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + " Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			auction.setEventDescription(StringUtils.checkString(event.getEventDescription()));
			// here for RFX submission & evaluation report used
			auction.setAuctionType(RfxTypes.RFP.getValue());
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
			LOG.error("Could not get build Event Detail Data :" + e.getMessage(), e);
		}
	}

	private void buildBuyerAttachementData(String eventId, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf) {
		try {
			List<AuctionEvaluationDocumentPojo> documentList = new ArrayList<AuctionEvaluationDocumentPojo>();
			List<RfpEventDocument> documents = rfpDocumentDao.findAllRfpEventdocsbyEventId(eventId);
			if (CollectionUtil.isNotEmpty(documents)) {
				for (RfpEventDocument docs : documents) {
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
			LOG.error("Could not get build Buyer Attachement Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierLineChartAndData(SimpleDateFormat sdf, RfpEvent event, List<EventSupplier> participatedSupplier, EvaluationAuctionBiddingPojo auction, boolean isMasked, RfpEnvelop envelop) {
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
			List<RfaSupplierBqPojo> eventBq = rfpSupplierBqDao.findRfpSupplierParticipation(event.getId());
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
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Line Chart And Data :" + e.getMessage(), e);
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
			LOG.error("Could not get build Supplier Contact List Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierActivitySummaryData(RfpEvent event, RfaSupplierBqPojo eventSupp, List<EvaluationBidderContactPojo> supplierActivitySummaryList, SimpleDateFormat sdf) {
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

			String completeness = new Long(eventSupp.getCompleteness()).toString();
			String totalItem = new Long(eventSupp.getTotalItem()).toString();
			supplierActivitySummary.setActionDate((eventSupp.getSubmissionTime() != null ? sdf.format(eventSupp.getSubmissionTime()) : "N/A"));
			supplierActivitySummary.setTotalAfterTax(eventSupp.getTotalAfterTax());
			supplierActivitySummary.setCompleAndTotalItem(completeness + " / " + totalItem);
			supplierActivitySummary.setNumberOfBids(eventSupp.getNumberOfBids());
			supplierActivitySummary.setActionStatus(submissionRemark);
			supplierActivitySummary.setIpnumber(eventSupp.getIpAddress());

			supplierActivitySummaryList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Activity Summary Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierAcceptedListData(RfpEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderAcceptedList, SimpleDateFormat sdf, boolean isMasked, RfpEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			supplierActivitySummary.setActionDate(eventSupp.getSupplierEventReadTime() != null ? sdf.format(eventSupp.getSupplierEventReadTime()) : "N/A");
			bidderAcceptedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Accepted List Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierRejectedListData(RfpEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderRejectedList, SimpleDateFormat sdf, boolean isMasked, RfpEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			supplierActivitySummary.setActionDate(eventSupp.getRejectedTime() != null ? sdf.format(eventSupp.getRejectedTime()) : "N/A");
			supplierActivitySummary.setRemark(StringUtils.checkString(eventSupp.getDisqualifyRemarks()).length() > 0 ? eventSupp.getDisqualifyRemarks() : "N/A");
			bidderRejectedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Rejected List Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierInvidedListData(RfpEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderInvidedList, SimpleDateFormat sdf, boolean isMasked, RfpEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			bidderInvidedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Invided List Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierTotallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfpEvent event, SimpleDateFormat sdf, boolean isMasked, RfpEnvelop envelop) {

		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfpSupplierBqDao.findRfpSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<String> bqids = rfpEnvelopDao.getBqsByEnvelopId(envelopId);
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
			LOG.error("Could not get build Supplier Totally Complete Bids Data :" + e.getMessage(), e);
		}

	}

	private void buildSupplierPartiallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfpEvent event, boolean isMasked, RfpEnvelop envelop) {
		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfpSupplierBqDao.findRfpSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<String> bqids = rfpEnvelopDao.getBqsByEnvelopId(envelopId);
			if (CollectionUtil.isNotEmpty(bqids)) {

				if (CollectionUtil.isNotEmpty(eventBq)) {
					for (RfaSupplierBqPojo item : eventBq) {
						if (item.getCompleteness() != item.getTotalItem()) {
							EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();

							suppBidprice.setBidderName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId())) : item.getSupplierCompanyName());
							suppBidprice.setPostAuctionprice(item.getTotalAfterTax());

							String completeness = new Long(item.getCompleteness()).toString();
							String totalItem = new Long(item.getTotalItem()).toString();

							suppBidprice.setCompleAndTotalItem(completeness + " / " + totalItem);
							bidPriceList.add(suppBidprice);
						}
					}
				}
			}
			auction.setBidderPartiallyCompleteBidsList(bidPriceList);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Partially Complete Bids Data :" + e.getMessage(), e);
		}

	}


	private void buildEnvlopeSupplierSORorEvaluationSummary(String bqId, RfpEvent event, EnvelopeSorPojo bqPojo, RfpEnvelop envelop, boolean isMasked, List<String> sortedSupplierList) {
		LOG.info("Build envelope data for SOR ");
		try {

			// Here list same as Bq
			List<EvaluationSuppliersSorPojo> sors = rfpSupplierSorDao.getAllSorsBySorIdsAndEventId(bqId, event.getId());
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
						List<RfpSupplierSorItem> suppSorItems = rfpSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierId(sor.getSorId(), sor.getId());
						List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
						if (CollectionUtil.isNotEmpty(suppSorItems)) {
							for (RfpSupplierSorItem suppBqItem : suppSorItems) {
								EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									for (RfpSupplierSorItem childBqItem : suppBqItem.getChildren()) {
										EvaluationSorItemPojo evlBqChilItem = new EvaluationSorItemPojo();
										evlBqChilItem.setDescription(childBqItem.getItemName());
										evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
										evlBqChilItem.setUom(childBqItem.getUom().getUom());
										//Rate
										evlBqChilItem.setAmount(childBqItem.getTotalAmount());
										evlBqChilItem.setDecimal(event != null ? event.getDecimal() : "");
										evlBqItems.add(evlBqChilItem);

										List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
										List<RfpSorEvaluationComments> bqItemComments = rfpSorEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getSorItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RfpSorEvaluationComments review : bqItemComments) {
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


	private void buildEnvlopeSORTopSupplierComparisionforEvaluationSummary(String sorId, RfpEvent event, EnvelopeSorPojo bqPojo, boolean isMasked, RfpEnvelop envelop, List<String> top3Supplier) {
		try {
//          Here limit will not work, Later better logic
			List<RfaSupplierSorPojo> participatedSupplier = rfpSupplierSorDao.getAllRfpTopEventSuppliersIdByEventId(event.getId(), 3, sorId);
			Map<String, EvaluationSorItemPojo> itemsMap = new LinkedHashMap<String, EvaluationSorItemPojo>();
			List<EvaluationSuppliersSorPojo> pojoList = new ArrayList<EvaluationSuppliersSorPojo>();
			EvaluationSuppliersSorPojo pojo = new EvaluationSuppliersSorPojo();
			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierSorPojo rfaSupplierBqPojo = participatedSupplier.get(i);
				top3Supplier.add(rfaSupplierBqPojo.getSupplierCompanyName());
				bqPojo.setSorName(rfaSupplierBqPojo.getSorName());

				List<RfpSupplierSorItem> suppBqItems = rfpSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierIdParentIdNotNull(sorId, rfaSupplierBqPojo.getSupplierId());
				if (i == 0) {
					for (RfpSupplierSorItem suppBqItem : suppBqItems) {
						EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
						evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
						evlBqItem.setItemName(suppBqItem.getItemName());
						evlBqItem.setUom(suppBqItem.getUom() != null ? suppBqItem.getUom().getUom() : "");
						evlBqItem.setAmount(suppBqItem.getTotalAmount());
						pojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
						itemsMap.put(suppBqItem.getSorItem().getId(), evlBqItem);
					}
				} else {
					for (RfpSupplierSorItem suppBqItem : suppBqItems) {
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

	private void buildEnvoleSORData(RfpEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfpEnvelop envelop, boolean isMasked,
									List<String> sortedSupplierList, List<String> top3Supplier) {
		try {
			List<EnvelopeSorPojo> envopleSorPojos = new ArrayList<EnvelopeSorPojo>();
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<SorPojo> sorIds = rfpEnvelopDao.getSorsIdListByEnvelopIdByOrder(envelopId);

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

	private void buildEnvoleBQData(RfpEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfpEnvelop envelop, boolean isMasked,
								   List<String> sortedSupplierList, List<String> top3Supplier) {
		try {
			List<EnvelopeBqPojo> envopleBqPojos = new ArrayList<EnvelopeBqPojo>();
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			// List<String> bqids = rfpEnvelopDao.getBqsByEnvelopId(envelopId);
			List<BqPojo> bqids = rfpEnvelopDao.getBqsIdListByEnvelopIdByOrder(envelopId);
			for (BqPojo bqId : bqids) {
				EnvelopeBqPojo bqPojo = new EnvelopeBqPojo();
				buildEnvlopeTopSupplierComparisionforEvaluationSummary(bqId.getId(), event, bqPojo, isMasked, envelop, top3Supplier);
				buildEnvlopeSupplierBqforEvaluationSummary(bqId.getId(), event, bqPojo, isMasked, envelop, sortedSupplierList);
				envopleBqPojos.add(bqPojo);
			}
			auction.setEnvelopeBq(envopleBqPojos);
		} catch (Exception e) {
			LOG.error("Could not get build Envole BQ Data :" + e.getMessage(), e);
		}
	}

	private void buildEnvlopeSupplierBqforEvaluationSummary(String bqId, RfpEvent event, EnvelopeBqPojo bqPojo, boolean isMasked, RfpEnvelop envelop, List<String> sortedSupplierList) {
		try {
			List<EvaluationSuppliersBqPojo> bqs = rfpSupplierBqDao.getAllBqsByBqIdsAndEventId(bqId, event.getId());
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
						List<RfpSupplierBqItem> suppBqItems = rfpSupplierBqItemService.getAllSupplierBqItemForReportByBqIdAndSupplierId(bq.getBqId(), bq.getId());
						List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
						if (CollectionUtil.isNotEmpty(suppBqItems)) {
							for (RfpSupplierBqItem suppBqItem : suppBqItems) {
								EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									for (RfpSupplierBqItem childBqItem : suppBqItem.getChildren()) {
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
										List<RfpBqEvaluationComments> bqItemComments = rfpBqEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getBqItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RfpBqEvaluationComments review : bqItemComments) {
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
			LOG.error("Could not get build Envlope SupplierBq for EvaluationSummary :" + e.getMessage(), e);
		}
	}

	private void buildEnvlopeTopSupplierComparisionforEvaluationSummary(String bqId, RfpEvent event, EnvelopeBqPojo bqPojo, boolean isMasked, RfpEnvelop envelop, List<String>top3Supplier) {
		try {
			List<RfaSupplierBqPojo> participatedSupplier = rfpSupplierBqDao.getAllRfaTopEventSuppliersIdByEventId(event.getId(), 3, bqId);
			Map<String, EvaluationBqItemPojo> itemsMap = new LinkedHashMap<String, EvaluationBqItemPojo>();
			List<EvaluationSuppliersBqPojo> pojoList = new ArrayList<EvaluationSuppliersBqPojo>();
			EvaluationSuppliersBqPojo pojo = new EvaluationSuppliersBqPojo();
			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierBqPojo rfaSupplierBqPojo = participatedSupplier.get(i);
				top3Supplier.add(rfaSupplierBqPojo.getSupplierCompanyName());
				bqPojo.setBqName(rfaSupplierBqPojo.getBqName());
				List<RfpSupplierBqItem> suppBqItems = rfpSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierIds(bqId, rfaSupplierBqPojo.getSupplierId());
				if (i == 0) {
					for (RfpSupplierBqItem suppBqItem : suppBqItems) {
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
					for (RfpSupplierBqItem suppBqItem : suppBqItems) {
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
			LOG.error("Could not get build Envlope Top Supplier Comparision for EvaluationSummary :" + e.getMessage(), e);
		}
	}

	private void buildEnvelopeCQData(RfpEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfpEnvelop envelop, boolean isMasked) {
		try {
			List<EnvelopeCqPojo> allCqs = new ArrayList<EnvelopeCqPojo>();
			List<String> cqid = rfpEnvelopDao.getCqIdlistByEnvelopId(envelop.getId());
			if (CollectionUtil.isNotEmpty(cqid)) {
				List<RfpCq> cqList = rfpCqService.findRfaCqForEventByEnvelopeId(cqid, event.getId());
				for (RfpCq cq : cqList) {
					EnvelopeCqPojo cqPojo = new EnvelopeCqPojo();
					cqPojo.setName(cq.getName());
					buildSupplierCqforEvaluationSummary(cq, event, cqPojo, isMasked, envelop);
					allCqs.add(cqPojo);
				}
			}
			auction.setEnvelopeCq(allCqs);
		} catch (Exception e) {
			LOG.error("Could not get build Envelope CQ Data :" + e.getMessage(), e);
		}

	}

	private void buildSupplierCqforEvaluationSummary(RfpCq cq, RfpEvent event, EnvelopeCqPojo pojo, boolean isMasked, RfpEnvelop envelop) {

		try {
			List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();

			if (cq != null) {
				EvaluationCqPojo cqPojo = new EvaluationCqPojo();
				cqPojo.setName(cq.getName());
				cqPojo.setDescription(cq.getDescription());

				List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
				if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
					for (RfpCqItem cqItem : cq.getCqItems()) {

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
							cqItemPojo.setOptionType(cqItem.getCqType() != null ? cqItem.getCqType().getValue() : "");
							if (cqItem.getAttachment() == Boolean.TRUE) {
								itemName = cqItem.getItemName() + (Boolean.TRUE == cqItem.getIsSupplierAttachRequired() ? " (Attachment is Required) " : " (Attachment is Optional) ");

							} else {
								itemName = cqItem.getItemName();
							}
							cqItemPojo.setItemName(itemName);

							List<EvaluationCqItemSupplierPojo> cqItemSuppliers = new ArrayList<EvaluationCqItemSupplierPojo>();
							List<Supplier> suppList = rfpEventSupplierDao.getEventSuppliersForSummary(event.getId());
							// Below code to get Suppliers Answers of each CQ Items

							if (CollectionUtil.isNotEmpty(suppList)) {
								// List<RfpSupplierCqItem> responseList =
								// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(),
								// event.getId(),
								// suppList);
								List<RfpSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), event.getId(), suppList);
								if (CollectionUtil.isNotEmpty(responseList)) {
									for (RfpSupplierCqItem suppCqItem : responseList) {
										EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
										cqItemSupplierPojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), suppCqItem.getSupplier().getId(), envelop.getId())) : suppCqItem.getSupplier().getCompanyName());
										List<RfpSupplierCqOption> listAnswers = rfpSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());

										if (suppCqItem.getCqItem().getCqType() == CqType.TEXT || suppCqItem.getCqItem().getCqType() == CqType.DATE || suppCqItem.getCqItem().getCqType() == CqType.NUMBER || suppCqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
											cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
										} else if (CqType.DOCUMENT_DOWNLOAD_LINK == suppCqItem.getCqItem().getCqType()) {
											List<String> docIds = new ArrayList<String>();
											List<RfpCqOption> rfpSupplierCqOptions = suppCqItem.getCqItem().getCqOptions();
											
												if (CollectionUtil.isNotEmpty(rfpSupplierCqOptions)) {
													for (RfpCqOption rfpSupplierCqOption : rfpSupplierCqOptions) {
														docIds.add(StringUtils
																.checkString(rfpSupplierCqOption.getValue()));
													}

													List<EventDocument> eventDocuments = rfpDocumentService
															.findAllRfpEventDocsNamesByEventIdAndDocIds(event.getId(),
																	docIds);
													if (eventDocuments != null) {
														String str = "";
														for (EventDocument docName : eventDocuments) {
															str = str + docName.getFileName() + "\n";
														}
														cqItemSupplierPojo.setAnswer(str);
													}
											}
										}

										else if (CollectionUtil.isNotEmpty(listAnswers) && suppCqItem.getCqItem().getCqType() != CqType.DOCUMENT_DOWNLOAD_LINK) {
											String str = "";
											// List<RfpSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
											for (RfpSupplierCqOption op : listAnswers) {
												int cqAnsListSize = (listAnswers).size();
												int index = (listAnswers).indexOf(op);
												str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));
												cqItemSupplierPojo.setScores(op.getScoring());
											}
											cqItemSupplierPojo.setAnswer(str);
										}
										cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
										// Review Comments
										List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
										List<RfpCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), event.getId(), cqItem.getId(), null);
										if (CollectionUtil.isNotEmpty(comments)) {
											String evalComment = "";
											for (RfpCqEvaluationComments item : comments) {
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
			LOG.error("Could not get build SupplierCq for EvaluationSummary :" + e.getMessage(), e);
		}

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
			LOG.error("Could not generate Submission Report : " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationAuctionBiddingPojo> buildSubmissionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();

		try {
			RfpEvent event = rfpEventDao.findByEventId(eventId);
			List<EventSupplier> supplierList = rfpEventSupplierDao.getAllSuppliersByEventId(eventId);

			if (event != null) {
				RfpEnvelop envelop = rfpEnvelopService.getRfpEnvelopById(evenvelopId);
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

				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				auction.setIsMask(isMasked);
				buildSupplierCountData(supplierList, auction);

				buildHeadingReport(event, supplierList, auction, sdf, isMasked, envelop, false);
				auction.setEnvelopTitle(envelop != null ? envelop.getEnvelopTitle() : "NA");
				auction.setLeadEvaluater(envelop != null ? (envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "NA") : "NA");
				auction.setLeadEvaluatorSummary(envelop != null ? ((envelop.getEvaluatorSummaryDate() != null ? (sdf.format(envelop.getEvaluatorSummaryDate()) + ":") : "") + (envelop.getLeadEvaluatorSummary() != null ? envelop.getLeadEvaluatorSummary() : "")) : "N/A");
				auction.setFileName(envelop != null ? envelop.getFileName() : "N/A");
				if (envelop != null) {
					buildEvaluatorsSummary(envelop.getEvaluators(), auction);
				}
				buildEventDetailData(sdf, event, auction);
				buildBuyerAttachementData(event.getId(), auction, sdf);
				buildMatchingIpAddressData(eventId, auction, supplierList);
				buildSupplierLineChartAndData(sdf, event, supplierList, auction, isMasked, envelop);
				List<String> sortedSupplierList = new ArrayList<>();
				List<String> top3Supplier = new ArrayList<>();
				buildEnvoleBQData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);
				buildEnvoleSORData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);
				buildEnvelopeCQData(event, auction, sdf, envelop, isMasked);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);

		} catch (Exception e) {
			LOG.error("Could not get buildSubmissionReportData : " + e.getMessage(), e);
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
	public void autoSaveRfpDetails(RfpEvent rfpEvent, String[] industryCategory, BindingResult result, String strTimeZone) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (rfpEvent.getEventVisibilityDates() != null) {
				String visibilityDates[] = rfpEvent.getEventVisibilityDates().split("-");
				formatter.setTimeZone(timeZone);
				try {
					Date startDate = (Date) formatter.parse(visibilityDates[0]);
					Date endDate = (Date) formatter.parse(visibilityDates[1]);
					rfpEvent.setEventEnd(endDate);
					rfpEvent.setEventStart(startDate);
				} catch (Exception e) {
					LOG.error(e);
				}
			}
			if (industryCategory != null) {
				List<IndustryCategory> icList = new ArrayList<>();
				for (String industryCatId : industryCategory) {
					IndustryCategory ic = new IndustryCategory();
					ic.setId(industryCatId);
					icList.add(ic);
				}
				rfpEvent.setIndustryCategories(icList);
			}

			if (result.hasErrors()) {
				List<String> errMessages = new ArrayList<String>();
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.info("ERROR : " + err.getDefaultMessage());
				}
			} else {
				if (CollectionUtil.isNotEmpty(rfpEvent.getApprovals())) {
					int level = 1;
					for (RfpEventApproval app : rfpEvent.getApprovals()) {
						app.setEvent(rfpEvent);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
							for (RfpApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}

			}
			if (StringUtils.checkString(rfpEvent.getId()).length() > 0) {
				RfpEvent persistObj = rfpEventService.getRfpEventByeventId(rfpEvent.getId());
				Date notificationDateTime = null;
				if (rfpEvent.getEventPublishDate() != null && rfpEvent.getEventPublishTime() != null) {
					notificationDateTime = DateUtil.combineDateTime(rfpEvent.getEventPublishDate(), rfpEvent.getEventPublishTime(), timeZone);
				}
				rfpEvent.setEventPublishDate(notificationDateTime);
				setAndUpdateRfpEvent(rfpEvent, persistObj);
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}
	}

	private void setAndUpdateRfpEvent(RfpEvent rfpEvent, RfpEvent persistObj) throws ParseException {

		LOG.info("Rfa Event : Bq : " + rfpEvent.getBillOfQuantity() + " : cq : " + rfpEvent.getQuestionnaires() + " : Doc : " + rfpEvent.getDocumentReq() + " : Meet : " + rfpEvent.getMeetingReq());
		persistObj.setModifiedDate(new Date());
		LOG.info(rfpEvent.getIndustryCategory());
		persistObj.setIndustryCategory(rfpEvent.getIndustryCategory());
		// persistObj.setStatus(EventStatus.DRAFT); -- DONT SET THE STATUS AS
		// DRAFT AS THIS COULD BE A SUSPENDED EVENT
		// EDIT - @Nitin Otageri
		persistObj.setEventStart(rfpEvent.getEventStart());
		persistObj.setEventEnd(rfpEvent.getEventEnd());
		LOG.info("StartDate:" + rfpEvent.getEventStart());
		LOG.info("EndDate:" + rfpEvent.getEventEnd());
		persistObj.setEventVisibility(rfpEvent.getEventVisibility());
		persistObj.setEventName(rfpEvent.getEventName());
		persistObj.setReferanceNumber(rfpEvent.getReferanceNumber());
		persistObj.setUrgentEvent(rfpEvent.getUrgentEvent());
		persistObj.setEventVisibilityDates(rfpEvent.getEventVisibilityDates());
		persistObj.setDeliveryAddress(rfpEvent.getDeliveryAddress());
		persistObj.setParticipationFeeCurrency(rfpEvent.getParticipationFeeCurrency());
		persistObj.setParticipationFees(rfpEvent.getParticipationFees());
		persistObj.setDepositCurrency(rfpEvent.getDepositCurrency());
		persistObj.setDeposit(rfpEvent.getDeposit());
		// Should not assign this

		if (persistObj.getTemplate() != null && persistObj.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(persistObj.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (!rfxTemplate.getVisibleAddSupplier()) {
				rfpEvent.setAddSupplier(rfxTemplate.getAddSupplier());
			}
			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				rfpEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				rfpEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}
			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				rfpEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}
		}
		persistObj.setEventPublishDate(rfpEvent.getEventPublishDate());
		persistObj.setSubmissionValidityDays(rfpEvent.getSubmissionValidityDays());
		// Event Timeline
		// Date notificationDateTime =
		// DateUtil.combineDateTime(rfaEvent.getSupplierNotificationDate(),
		// rfaEvent.getSupplierNotificationTime());
		// persistObj.setSupplierNotificationDate(notificationDateTime);
		persistObj.setEventStart(rfpEvent.getEventStart());
		persistObj.setApprovals(rfpEvent.getApprovals());

		// Event Req
		persistObj.setEnableEvaluationDeclaration(rfpEvent.getEnableEvaluationDeclaration());
		persistObj.setEnableSupplierDeclaration(rfpEvent.getEnableSupplierDeclaration());
		persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == rfpEvent.getEnableEvaluationDeclaration() && rfpEvent.getEvaluationProcessDeclaration() != null ? rfpEvent.getEvaluationProcessDeclaration() : null);
		persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == rfpEvent.getEnableSupplierDeclaration() && rfpEvent.getSupplierAcceptanceDeclaration() != null ? rfpEvent.getSupplierAcceptanceDeclaration() : null);
		persistObj.setBillOfQuantity(rfpEvent.getBillOfQuantity());
		persistObj.setQuestionnaires(rfpEvent.getQuestionnaires());
		persistObj.setDocumentReq(rfpEvent.getDocumentReq());
		persistObj.setMeetingReq(rfpEvent.getMeetingReq());
		persistObj.setEventDetailCompleted(Boolean.TRUE);
		persistObj.setIndustryCategories(rfpEvent.getIndustryCategories());
		persistObj.setDeliveryDate(rfpEvent.getDeliveryDate());
		persistObj.setViewSupplerName(rfpEvent.getViewSupplerName());
		persistObj.setUnMaskedUser(rfpEvent.getUnMaskedUser());
		persistObj.setCloseEnvelope(rfpEvent.getCloseEnvelope());
		persistObj.setAddSupplier(rfpEvent.getAddSupplier());
		persistObj.setAllowToSuspendEvent(rfpEvent.getAllowToSuspendEvent());
		rfpEventService.updateRfpEvent(persistObj);

	}

	@Override
	public RfpEvent getRfpEventWithBuByeventId(String eventId) {

		RfpEvent rfp = rfpEventDao.findByEventId(eventId);
		if (rfp.getEventOwner().getBuyer() != null) {
			rfp.getEventOwner().getBuyer().getLine1();
			rfp.getEventOwner().getBuyer().getLine2();
			rfp.getEventOwner().getBuyer().getCity();
			if (rfp.getEventOwner().getBuyer().getState() != null) {
				rfp.getEventOwner().getBuyer().getState().getStateName();
				if (rfp.getEventOwner().getBuyer().getState().getCountry() != null) {
					rfp.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (rfp.getDeliveryAddress() != null) {
			rfp.getDeliveryAddress().getLine1();
			rfp.getDeliveryAddress().getLine2();
			rfp.getDeliveryAddress().getCity();
			if (rfp.getDeliveryAddress().getState() != null) {
				rfp.getDeliveryAddress().getState().getStateName();
				rfp.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getRftReminder())) {
			for (RfpReminder reminder : rfp.getRftReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfp.getSuppliers())) {
			for (RfpEventSupplier item : rfp.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}
		if (rfp.getEventOwner().getBuyer() != null) {
			Buyer buyer = rfp.getEventOwner().getBuyer();
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
		if (CollectionUtil.isNotEmpty(rfp.getMeetings())) {
			for (RfpEventMeeting item : rfp.getMeetings()) {
				item.getStatus().name();
				item.getRemarks();
				item.getVenue();
				item.getAppointmentDateTime();
				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingContacts())) {
					for (RfpEventMeetingContact contact : item.getRfxEventMeetingContacts()) {
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
					for (RfpEventMeetingDocument docs : item.getRfxEventMeetingDocument()) {
						docs.getId();
						docs.getFileName();
						docs.getFileSizeInKb();
						docs.getCredContentType();
					}
				}
			}
		}
		// if (CollectionUtil.isNotEmpty(rfp.getDocuments())) {
		// for (RfpEventDocument item : rfp.getDocuments()) {
		// item.getDescription();
		// item.getFileName();
		// item.getUploadDate();
		// item.getFileData();
		// }
		// }
		if (CollectionUtil.isNotEmpty(rfp.getComment())) {
			for (RfpComment comment : rfp.getComment()) {
				comment.getComment();
				comment.getCreatedBy();
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getName();
				}
				comment.getLoginName();
				comment.getUserName();
				comment.getRfpEvent();
			}
		}

		if (CollectionUtil.isNotEmpty(rfp.getIndustryCategories())) {
			for (IndustryCategory indCat : rfp.getIndustryCategories()) {
				indCat.getCode();
			}
		}
		if (rfp.getBusinessUnit() != null) {
			rfp.getBusinessUnit().getUnitName();
		}

		return rfp;

	}

	@Override
	public List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {

		List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		getRfpSearchEventsForExport(tenantId, eventIds, resultList, searchFilterEventPojo, select_all, startDate, endDate, sdf);
		return resultList;
	}

	private void getRfpSearchEventsForExport(String tenantId, String[] eventIds, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		List<RfpEvent> rftList = rfpEventDao.getSearchEventsByIds(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate);
		if (CollectionUtil.isNotEmpty(rftList)) {
			for (RfpEvent event : rftList) {
				DraftEventPojo eventPojo = new DraftEventPojo();
				eventPojo.setId(event.getId());
				if (event.getEventDescription() != null) {
					eventPojo.setEventDescription(event.getEventDescription());
				}
				eventPojo.setCostCenter(event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "");
				eventPojo.setInvitedSupplierCount(event.getSuppliers().size());
				eventPojo.setEventName(event.getEventName());
				eventPojo.setReferenceNumber(event.getReferanceNumber());
				eventPojo.setSysEventId(event.getEventId());
				eventPojo.setEventStart(event.getEventStart());
				eventPojo.setEventEnd(event.getEventEnd());
				eventPojo.setType(RfxTypes.RFP);
				eventPojo.setEventUser(event.getEventOwner().getName());
				eventPojo.setDeliveryDate(event.getDeliveryDate());
				eventPojo.setVisibility(event.getEventVisibility());
				eventPojo.setPublishDate(event.getEventPublishDate());
				eventPojo.setValidityDays(event.getSubmissionValidityDays());
				eventPojo.setHistricAmount(event.getHistoricaAmount() != null ? (event.getHistoricaAmount()) : BigDecimal.ZERO);
				eventPojo.setDeposite(event.getDeposit() != null ? (event.getDeposit()) : BigDecimal.ZERO);
				eventPojo.setEstimatedBudget(event.getBudgetAmount() != null ? (event.getBudgetAmount()) : BigDecimal.ZERO);
				eventPojo.setEventFees(event.getParticipationFees() != null ? (event.getParticipationFees()) : BigDecimal.ZERO);
				eventPojo.setTemplateName(event.getTemplate() != null ? (event.getTemplate().getTemplateName()) : "");
				eventPojo.setConcludedaDate(event.getConcludeDate() != null ? (event.getConcludeDate().toString()) : "");
				eventPojo.setAwardedDate(event.getAwardDate() != null ? event.getAwardDate().toString() : "");
				eventPojo.setPushDate(event.getEventPushDate() != null ? event.getEventPushDate().toString() : "");
				Double avarageBidValue = rfpEventService.getAvarageBidPriceSubmitted(event.getId());
				eventPojo.setAvarageBidSubmited(avarageBidValue != null ? avarageBidValue.toString() : "");

				if (event.getTeamMembers() != null) {
					String names = "";
					for (RfpTeamMember teamMember : event.getTeamMembers()) {
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

				List<EventSupplier> supplierList = rfpEventSupplierService.getAllSuppliersByEventId(event.getId());
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
				//
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

				RfpEventSupplier leadingSupplier = null;
				int submittedCount = 0;
				int acceptedCount = 0;
				BigDecimal leadingAmount = BigDecimal.ZERO;
				for (RfpEventSupplier es : event.getSuppliers()) {
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
					for (RfpEventBq eventBq : event.getEventBqs()) {
						RfpSupplierBq supBq = rfpSupplierBqDao.findBqByEventIdAndSupplierIdOfQualifiedSupplier(event.getId(), eventBq.getId(), es.getSupplier().getId());
						if (supBq == null) {
							allOk = false;
							break;
						}
						bqTotal = bqTotal.add(supBq.getTotalAfterTax());
					}
					if (!allOk)
						continue;
					if (leadingAmount.compareTo(new BigDecimal(0.0)) == 0) {
						leadingSupplier = es;
						leadingAmount = bqTotal;
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
	public EventTimerPojo getTimeEventByeventId(String eventId) {
		return rfpEventDao.getTimeEventByeventId(eventId);
	}

	private List<SupplierMaskingPojo> buildSupplierMaskingData(List<EventSupplier> supplierList, String eventId) {
		List<SupplierMaskingPojo> supplierMaskingList = new ArrayList<SupplierMaskingPojo>();
		List<RfpEnvelop> env = rfpEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFP);
		RfpEvent event = getRfpEventByeventId(eventId);
		for (EventSupplier eventSupplier : supplierList) {
			if (eventSupplier.getSupplier() != null) {
				SupplierMaskingPojo pojo = new SupplierMaskingPojo();
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					pojo.setSupplierName(MaskUtils.maskName("SUPPLIER", eventSupplier.getSupplier().getId(), eventId));
				} else {
					pojo.setSupplierName(eventSupplier.getSupplier() != null ? eventSupplier.getSupplier().getCompanyName() : "");
				}
				List<SupplierMaskingCodePojo> supplierMaskingCodes = new ArrayList<SupplierMaskingCodePojo>();
				for (RfpEnvelop rfpEnvelop : env) {
					SupplierMaskingCodePojo codePojo = new SupplierMaskingCodePojo();
					codePojo.setEnevelopeName(rfpEnvelop.getEnvelopTitle());
					codePojo.setMakedCode(MaskUtils.maskName(rfpEnvelop.getPreFix(), eventSupplier.getSupplier().getId(), rfpEnvelop.getId()));
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
	public List<RfpEvent> getAllRfpEventByTenantId(String tenantId) {
		return rfpEventDao.getAllRfpEventByTenantId(tenantId);
	}

	@Override
	public Event getEventByEventRefranceNo(String eventRefranceNo, String tenantID) {
		return rfpEventDao.getEventByEventRefranceNo(eventRefranceNo, tenantID);
	}

	@Override
	public EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId) {
		return rfpEventDao.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
	}

	@Override
	public EventPojo loadSupplierEventPojoForSummeryById(String eventId) {
		return rfpEventDao.loadSupplierEventPojoForSummeryById(eventId);
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		return rfpEventDao.getInvitedSupplierCount(eventId);
	}

	@Override
	public long getParticepatedSupplierCount(String eventId) {
		return rfpEventDao.getParticepatedSupplierCount(eventId);
	}

	@Override
	public long getSubmitedSupplierCount(String eventId) {
		return rfpEventDao.getSubmitedSupplierCount(eventId);
	}

	@Override
	public List<RfpSupplierBq> getLowestSubmissionPrice(String eventId) {
		return rfpEventDao.getLowestSubmissionPrice(eventId);
	}

	@Override
	public List<EventSupplierPojo> getSuppliersByStatus(String eventId) {
		return rfpEventDao.getSuppliersByStatus(eventId);
	}

	/*
	 * @Override public RfpSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId) { return
	 * rfpEventDao.getFinalBidsForSuppliers(eventId, supplierId); }
	 */
	@Override
	public RfpEnvelop getBqForEnvelope(String envelopeId) {
		return rfpEventDao.getBqForEnvelope(envelopeId);
	}

	@Override
	public EventPojo getRfpForPublicEventByeventId(String eventId) {
		return rfpEventDao.getRfpForPublicEventByeventId(eventId);
	}

	@Override
	public List<IndustryCategory> getIndustryCategoriesForRfpById(String eventId) {
		return rfpEventDao.getIndustryCategoriesForRfpById(eventId);
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
			Resource resource = applicationContext.getResource("classpath:reports/PublicEventsSummary.jasper");

			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(StringUtils.checkString(event.getEventName()));
			eventDetails.setEventStart(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			eventDetails.setType("RFP");
			eventDetails.setCompanyName(event.getCompanyName());
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			eventDetails.setVisibility(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			eventDetails.setPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			eventDetails.setDiliveryDate(event.getEventDeliveryDate() != null ? ddsdf.format(event.getEventDeliveryDate()) : "N/A");

			List<IndustryCategory> industryCategories = rfpEventDao.getIndustryCategoriesForRfpById(event.getId());
			if (CollectionUtil.isNotEmpty(industryCategories)) {
				List<IndustryCategoryPojo> categoryNames = new ArrayList<IndustryCategoryPojo>();
				for (IndustryCategory industryCategory : industryCategories) {
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(industryCategory.getName());
					categoryNames.add(ic);
				}
				eventDetails.setIndustryCategoryNames(categoryNames);
			}

			boolean siteVisit = rfpMeetingService.isSiteVisitExist(event.getId());
			eventDetails.setSiteVisit(siteVisit);
			// Event Contact Details.
			List<RfpEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfpEventContact contact : eventContacts) {
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

			List<Bq> bqs = rfpEventBqDao.findBqbyEventId(event.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
			if (CollectionUtil.isNotEmpty(bqs)) {
				for (Bq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<RfpBqItem> bqItems = rfpBqItemDao.findBqItemsForBq(item.getId());
					List<EvaluationBqItemPojo> evaluationBqItem = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfpBqItem bqItem : bqItems) {
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
			LOG.error("Could not generate Public Rfp Event Summary PDF Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return jasperPrint;
	}

	@Override
	public int updateEventPushedDate(String eventId) {
		return rfpEventDao.updateEventPushedDate(eventId);
	}

	@Override
	public Double getAvarageBidPriceSubmitted(String id) {
		return rfpEventDao.getAvarageBidPriceSubmitted(id);
	}

	@Override
	public List<String> getEventTeamMember(String eventId) {
		return rfpEnvelopDao.getEventTeamMember(eventId);
	}

	@Override
	public int updatePrPushDate(String eventId) {
		return rfpEventDao.updatePrPushDate(eventId);
	}

	@Override
	public int updateEventAward(String eventId) {
		return rfpEventDao.updateEventAward(eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String eventId, EventStatus status) {
		rfpEventDao.updateImmediately(eventId, status);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlagImmediately(String eventId) {
		rfpEventDao.updateEventStartMessageFlag(eventId);
	}

	@Override
	public List<RfpEvent> getAllEventByTenantIdInitial(String loggedInUserTenantId, String loggedInUser) throws SubscriptionException {
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

		List<RfpEvent> list = rfpEventDao.getAllEventByTenantIdInitial(loggedInUserTenantId, loggedInUser);
		for (RfpEvent rfpEvent : list) {

			if (rfpEvent.getTemplate() != null) {
				if (rfpEvent.getTemplate().getStatus() == Status.INACTIVE) {
					rfpEvent.setTemplateActive(true);
				} else {
					rfpEvent.setTemplateActive(false);
				}
			}

			if (CollectionUtil.isNotEmpty(rfpEvent.getIndustryCategories()))
				rfpEvent.setIndustryCategory(rfpEvent.getIndustryCategories().get(0));
		}
		return list;
	}

	@Override
	public Integer getCountByEventId(String eventId) {
		return rfpEventDao.getCountByEventId(eventId);
	}

	@Override
	public Event loadRfpEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUsers, List<User> suspApprovalUsers) throws CloneNotSupportedException {
		RfpEvent event = rfpEventDao.findByEventId(eventId);
		if (event != null) {
			if (event.getParticipationFeeCurrency() != null) {
				event.getParticipationFeeCurrency().getCurrencyName();
			}
			if (event.getDepositCurrency() != null) {
				event.getDepositCurrency().getCurrencyName();
			}
			if (event.getUnMaskedUser() != null) {
				event.getUnMaskedUser().getName();
				model.addAttribute("unMaskUser", event.getUnMaskedUser());
			}
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfpEventContact contact : event.getEventContacts()) {
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

			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfpUnMaskedUser teamMembers : event.getUnMaskedUsers()) {
					teamMembers.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RfpEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getName();
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
				for (RfpTeamMember approver : event.getTeamMembers()) {
					approver.getUser().getName();
					assignedTeamMembers.add((User) approver.getUser().clone());
				}
			}

			// List<User> userList = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfpEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfpApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();

							User us = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());

							if (!approvalUsers.contains(us)) {
								approvalUsers.add(us);
							}
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfpEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfpSuspensionApprovalUser user : approver.getApprovalUsers()) {
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

			if (CollectionUtil.isNotEmpty(event.getComment())) {
				LOG.info(" Comments  >>>  " + event.getComment());
				for (RfpComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
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
			buildRfpEventModal(model, eventId, event, null);
		}
		return event;
	}

	private void buildRfpEventModal(Model model, String eventId, Event event, List<User> userList) throws CloneNotSupportedException {

		((RfpEvent) (event)).setEventPublishTime(event.getEventPublishDate());

		if (((RfpEvent) (event)).getMinimumSupplierRating() != null) {
			((RfpEvent) (event)).setMinimumSupplierRating(((RfpEvent) (event)).getMinimumSupplierRating());
		}
		if (((RfpEvent) (event)).getMaximumSupplierRating() != null) {
			((RfpEvent) (event)).setMaximumSupplierRating(((RfpEvent) (event)).getMaximumSupplierRating());
		}

		RfpEventContact eventPContact = new RfpEventContact();
		eventPContact.setEventId(event.getId());
		// model.addAttribute("userList1", userList);
		model.addAttribute("eventContact", eventPContact);
		model.addAttribute("eventContactsList", ((RfpEvent) event).getEventContacts());
		model.addAttribute("reminderList", rfpEventService.getAllRfpEventReminderForEvent(eventId));
	}

	@Override
	public Event getSimpleEventDetailsById(String eventId) {
		return rfpEventDao.getSimpleEventDetailsById(eventId);
	}

	@Override
	public RfpEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId) {
		return rfpEventDao.getPlainEventByFormattedEventIdAndTenantId(eventId, tenantId);
	}

	@Override
	public List<RfpEventAwardAudit> getRfpEventAwardByEventId(String eventId) {
		return rfpEventDao.getRfpEventAwardByEventId(eventId);
	}

	@Override
	public JasperPrint getEventAuditPdf(RfpEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rfpEventDao.findByEventId(event.getId());
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
			eventDetails.setType("RFP");
			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<RfpEventAudit> eventAudit = rfpEventAuditDao.getRfpEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RfpEventAudit ra : eventAudit) {
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
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public Boolean isDefaultPreSetEnvlope(String eventId) {
		return rfpEventDao.isDefaultPreSetEnvlope(eventId);
	}

	@Override
	public void sendEvaluationCompletedPrematurelyNotification(User actionBy, Event event) {
		String url = APP_URL + "/buyer/" + RfxTypes.RFP.name() + "/eventSummary/" + event.getId();
		String msg = "\"" + actionBy.getName() + "\" has concluded evaluation prematurely for \"" + event.getEventName() + "\"";
		String timeZone = "GMT+8:00";
		String subject = "Evaluation Concluded Prematurely";

		User eventOwner = rfpEventDao.getPlainEventOwnerByEventId(event.getId());
		timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);

		// Send to event owner
		sendNotification(event, url, msg, eventOwner, timeZone, subject);

		// send to Associate Owners
		List<EventTeamMember> teamMembers = rfpEventDao.getPlainTeamMembersForEvent(event.getId());
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
			map.put("eventType", RfxTypes.RFP.getValue());
			map.put("businessUnit", StringUtils.checkString(rfpEventDao.findBusinessUnitName(event.getId())));
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
	public RfpEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		RfpEvaluationConclusionUser usr = rfpEventDao.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
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
		List<EvaluationConclusionPojo> rfpSummary = new ArrayList<EvaluationConclusionPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		parameters.put("generatedOn", sdf.format(new Date()));

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationConclusionReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationConclusionPojo summary = new EvaluationConclusionPojo();
			RfpEvent event = rfpEventDao.getPlainEventWithOwnerById(eventId);

			if (event != null) {

				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				summary.setType("Request For Proposal");
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

				List<RfpEnvelop> envelopList = rfpEnvelopDao.getEnvelopListByEventId(eventId);
				String evalutedEnvelop = "";
				String nonEvalutedEnvelop = "";
				if (CollectionUtil.isNotEmpty(envelopList)) {
					for (RfpEnvelop envelop : envelopList) {
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

				List<RfpEvaluationConclusionUser> evaluationConclusionUserList = rfpEventDao.findEvaluationConclusionUsersByEventId(eventId);
				String conclusionUser = "";

				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					Log.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RfpEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
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
				rfpSummary.add(summary);
			}
			parameters.put("EVALUATION_CONCLUSION", rfpSummary);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfpSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation conclusion  Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RfpEvent event, String name, String feeReference, String timezone) {
		try {
			LOG.info("Send Email to: " + recipientEmail + " for subject: " + subject + " with description:" + description + " and name: " + name + " and timezone: " + timezone);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", name);
			map.put("description", description);
			map.put("eventName", event.getEventName());
			map.put("eventFeeReference", feeReference);
			map.put("eventID", event.getEventId());
			map.put("eventParticipationAmount", (event.getParticipationFeeCurrency().getCurrencyCode() + " " + event.getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
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
	public long getRfpEventCountByTenantId(String searchVal, String tenantId, String userId) {
		return rfpEventDao.getRfpEventCountByTenantId(searchVal, tenantId, userId, null);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEvent updateEventSuspensionApproval(RfpEvent event, User user) {

		RfpEvent persistObj = rfpEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfpEventSuspensionApproval approvalRequest : persistObj.getSuspensionApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfpSuspensionApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}
		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			int level = 1;
			for (RfpEventSuspensionApproval app : event.getSuspensionApprovals()) {
				app.setEvent(event);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				List<String> levelUsers = new ArrayList<String>();
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfpSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
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
		persistObj = rfpEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfpEventAudit audit = new RfpEventAudit(persistObj, user, new Date(), AuditActionType.Update, auditMessage);
				rfpEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" + persistObj.getEventId() + "' ." + auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			} catch (Exception e) {
				LOG.error("Error saving Event Audit for editing of approvers : " + e.getMessage(), e);
			}
		}
		return persistObj;

	}

	@Override
	public void downloadRfpEvaluatorDocument(String id, HttpServletResponse response) {
		RfpEvaluatorUser docs = rfpEvaluatorUserDao.getEvaluationDocument(id);
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
	public void downloadRfpLeadEvaluatorDocument(String envelopId, HttpServletResponse response) {
		RfpEnvelop docs = rfpEnvelopDao.getEvaluationDocument(envelopId);
		LOG.info("Document is ready to download" + docs.getFileName());
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		try {
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		} catch (IOException e) {
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
		rfpEventDao.revertEventAwardStatus(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> createSpFormFromAward(RfpEventAward rfpEventAward, String templateId, String eventId, String userId, User loggedInUser) throws ApplicationException {
		RfpEvent rfpEvent = rfpEventService.getRfpEventByeventId(eventId);
		SupplierPerformanceTemplate spTemplate = spTemplateService.getSupplierPerformanceTemplatebyId(templateId);
		User formOwner = userService.getUsersById(userId);
		List<String> formIds = new ArrayList<String>();
		try {

			List<String> supplierIds = new ArrayList<String>();
			for (RfpEventAwardDetails rfxAward : rfpEventAward.getRfxAwardDetails()) {
				if (rfxAward.getSupplier() != null && StringUtils.checkString(rfxAward.getSupplier().getId()).length() > 0) {
					if (!supplierIds.contains(rfxAward.getSupplier().getId())) {
						supplierIds.add(rfxAward.getSupplier().getId());
					}
				}
			}
			for (String supplierId : supplierIds) {
				Supplier sup = new Supplier();
				sup.setId(supplierId);
				SupplierPerformanceForm form = new SupplierPerformanceForm();
				String formId = eventIdSettingsDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "SP", rfpEvent.getBusinessUnit());
				form.setFormId(formId);
				form.setFormName(spTemplate.getTemplateName());
				form.setFormOwner(formOwner);
				form.setCreatedBy(SecurityLibrary.getLoggedInUser());
				form.setCreatedDate(new Date());
				form.setEventId(eventId);
				form.setEventType(RfxTypes.RFP);
				form.setAwardedSupplier(sup);
				if (Boolean.TRUE == spTemplate.getProcurementCategoryVisible() && Boolean.TRUE == spTemplate.getProcurementCategoryDisabled()) {
					form.setProcurementCategory(spTemplate.getProcurementCategory());
				} else {
					if (rfpEvent.getProcurementCategories() != null) {
						form.setProcurementCategory(rfpEvent.getProcurementCategories());
					} else {
						// if it is available in template then use it.
						form.setProcurementCategory(spTemplate.getProcurementCategory());
					}
				}
				form.setReferenceNumber(rfpEvent.getEventId());
				form.setReferenceName(rfpEvent.getEventName());
				form.setTemplate(spTemplateService.getSupplierPerformanceTemplatebyId(templateId));
				form.setBusinessUnit(rfpEvent.getBusinessUnit());
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
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
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
		if(user.getEmailNotifications())
		notificationService.sendEmail(mailTo, subject, map, Global.CONTRACT_CREATION_MAIL);

	}
}
