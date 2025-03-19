package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.EventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.RfaEventDocument;
import com.privasia.procurehere.core.entity.RfiSorEvaluationComments;
import com.privasia.procurehere.core.entity.RfqEventDocument;
import com.privasia.procurehere.core.entity.RfxTemplateDocument;
import com.privasia.procurehere.core.dao.RfiSorDao;
import com.privasia.procurehere.core.dao.RfiSorItemDao;
import com.privasia.procurehere.core.dao.RfiSupplierSorDao;
import com.privasia.procurehere.core.dao.RfiSupplierSorItemDao;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.RfiSorItem;
import com.privasia.procurehere.core.entity.RfiSupplierSorItem;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.pojo.EnvelopeSorPojo;
import com.privasia.procurehere.core.pojo.EvaluationBqPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RfiSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfiSupplierSorItemService;
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
import com.privasia.procurehere.core.dao.CostCenterDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.DeclarationDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.GroupCodeDao;
import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.dao.ProcurementCategoriesDao;
import com.privasia.procurehere.core.dao.ProcurementMethodDao;
import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfiCqDao;
import com.privasia.procurehere.core.dao.RfiCqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfiCqOptionDao;
import com.privasia.procurehere.core.dao.RfiDocumentDao;
import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RfiEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RfiEventAuditDao;
import com.privasia.procurehere.core.dao.RfiEventContactDao;
import com.privasia.procurehere.core.dao.RfiEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiEventMeetingDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfiEventTimeLineDao;
import com.privasia.procurehere.core.dao.RfiReminderDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.RfiSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.ApprovalUser;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.BuyerPackage;
import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventApproval;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.EventTimeline;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventContact;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfiApprovalUser;
import com.privasia.procurehere.core.entity.RfiComment;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfiEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfiEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventApproval;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfiEventContact;
import com.privasia.procurehere.core.entity.RfiEventCorrespondenceAddress;
import com.privasia.procurehere.core.entity.RfiEventDocument;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventMeetingContact;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEventMeetingReminder;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfiEventTimeLine;
import com.privasia.procurehere.core.entity.RfiReminder;
import com.privasia.procurehere.core.entity.RfiSupplierCqItem;
import com.privasia.procurehere.core.entity.RfiSupplierCqOption;
import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfiSupplierTeamMember;
import com.privasia.procurehere.core.entity.RfiSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfiSuspensionComment;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.RfiUnMaskedUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfpEventContact;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RfqEventContact;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RfqTeamMember;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventContact;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.TemplateApprovalUser;
import com.privasia.procurehere.core.entity.TemplateEventApproval;
import com.privasia.procurehere.core.entity.TemplateEventTeamMembers;
import com.privasia.procurehere.core.entity.TemplateField;
import com.privasia.procurehere.core.entity.TemplateSuspensionApproval;
import com.privasia.procurehere.core.entity.TemplateSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.TemplateUnmaskUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventTimelineType;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.AuctionEvaluationDocumentPojo;
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
import com.privasia.procurehere.core.pojo.EvaluationSuppliersPojo;
import com.privasia.procurehere.core.pojo.EvaluationTeamsPojo;
import com.privasia.procurehere.core.pojo.EvaluationTimelinePojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventTimelinePojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.RfxEnvelopPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SupplierMaskingCodePojo;
import com.privasia.procurehere.core.pojo.SupplierMaskingPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.MaskUtils;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.PublishEventService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.ErpSetupService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfiCqEvaluationCommentsService;
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.TatReportService;
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
public class RfiEventServiceImpl implements RfiEventService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	RfiMeetingService rfiMeetingService;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfiEventContactDao rfiEventContactDao;

	@Autowired
	RfiEventCorrespondenceAddressDao rfiEventCorrespondenceAddressDao;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	CostCenterDao costCenterDao;

	@Autowired
	RfiCqEvaluationCommentsDao cqEvaluationCommentsDao;

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	RfiSorEvaluationCommentsService rfiSorEvaluationCommentsService;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	RfiReminderDao rfiReminderDao;

	@Autowired
	UserService userService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	UserDao userDao;

	@Autowired
	RfiSupplierMeetingAttendanceDao rfiSupplierMeetingAttendanceDao;

	@Autowired
	BuyerService buyerService;

	@Autowired
	RfiEventTimeLineDao rfiEventTimeLineDao;

	@Autowired
	RfiDocumentDao rfiDocumentDao;

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	ServletContext context;

	@Autowired
	RfiEventMeetingDao rfiEventMeetingDao;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfiCqEvaluationCommentsService cqEvaluationCommentsService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;

	@Autowired
	RfiCqService rfiCqService;

	@Autowired
	RfiEventSupplierDao rfiEventSupplierDao;

	@Autowired
	RfiEnvelopDao rfiEnvelopDao;

	@Autowired
	RfiSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfiSupplierCqDao rfiSupplierCqDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	RfiEventAuditDao rfiEventAuditDao;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfpEventSupplierDao rfpEventSupplierDao;

	@Autowired
	RfpBqDao rfpBqDao;

	@Autowired
	RfpBqItemDao rfpBqItemDao;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

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
	RfiCqDao rfiCqDao;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	RfiDocumentService rfiDocumentService;

	@Autowired
	PublishEventService publishEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfiEnvelopService envelopService;

	@Autowired
	DeclarationDao declarationDao;

	@Autowired
	RfiEvaluatorDeclarationDao rfiEvaluatorDeclarationDao;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	RfiSupplierCqOptionDao rfiSupplierCqOptionDao;

	@Autowired
	RfiCqOptionDao rfiCqOptionDao;

	@Autowired
	ProcurementMethodDao procurementMethodDao;

	@Autowired
	ProcurementCategoriesDao procurementCategoriesDao;
	
	@Autowired
	GroupCodeDao groupCodeDao;
	
	@Autowired
	TatReportService tatReportService;

	@Autowired
	RfiEvaluatorUserDao rfiEvaluatorUserDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;


	@Autowired
	RfiSorDao rfiEventSorDao;

	@Autowired
	RfiSorItemDao rfiSorItemDao;

	@Autowired
	RfiSupplierSorItemService rfiSupplierSorItemService;

	@Autowired
	RfiSupplierSorDao rfiSupplierSorDao;

	@Autowired
	RfiSupplierSorItemDao supplierSorItemDao;

	@Override
	@Transactional(readOnly = false)
	public RfiEvent saveRfiEvent(RfiEvent rfiEvent) throws SubscriptionException {
		// Check subscription limit
		Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(SecurityLibrary.getLoggedInUser().getBuyer().getId());

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
		rfiEvent.setCreatedDate(new Date());
		rfiEvent.setEventOwner(SecurityLibrary.getLoggedInUser());
		rfiEvent.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
		rfiEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
		LOG.info("Save Event Name :" + rfiEvent.getEventId());
		RfiEvent dbObj = rfiEventDao.saveOrUpdate(rfiEvent);

		// If there are unsaved envelopes attached to the event, save them as well
		try {
			if (CollectionUtil.isNotEmpty(rfiEvent.getRfiEnvelop())) {
				for (RfiEnvelop env : rfiEvent.getRfiEnvelop()) {
					if (StringUtils.checkString(env.getId()).length() == 0 && env.getEnvelopTitle() != null) {
						env.setRfxEvent(dbObj);
						rfiEnvelopDao.save(env);
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
		if (rfiEvent.isUploadDocuments()) {
			dbObj.setUploadDocuments(true);
		}
		return dbObj;
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEvent updateRfiEvent(RfiEvent rfiEvent) {
		RfiEvent event = rfiEventDao.update(rfiEvent);
		event.getCreatedBy().getLoginId();
		return event;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfiEvent(RfiEvent rfiEvent) {
		rfiEventDao.delete(rfiEvent);

	}

	@Override
	public RfiEvent getRfiEventByeventId(String eventId) {
		RfiEvent rfi = rfiEventDao.findByEventId(eventId);
		if (rfi.getEventOwner().getBuyer() != null) {
			rfi.getEventOwner().getBuyer().getLine1();
			rfi.getEventOwner().getBuyer().getLine2();
			rfi.getEventOwner().getBuyer().getCity();
			if (rfi.getEventOwner().getBuyer().getState() != null) {
				rfi.getEventOwner().getBuyer().getState().getStateName();
				if (rfi.getEventOwner().getBuyer().getState().getCountry() != null) {
					rfi.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (CollectionUtil.isNotEmpty(rfi.getSuppliers())) {
			for (RfiEventSupplier item : rfi.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}

		if (CollectionUtil.isNotEmpty(rfi.getUnMaskedUsers())) {
			for (RfiUnMaskedUser item : rfi.getUnMaskedUsers()) {
				item.getUser();
			}
		}

		if (CollectionUtil.isNotEmpty(rfi.getEvaluationConclusionUsers())) {
			for (RfiEvaluationConclusionUser item : rfi.getEvaluationConclusionUsers()) {
				item.getUser().getName();
			}
		}

		rfi.getProcurementCategories();
		rfi.getProcurementMethod();

		if (rfi.getBaseCurrency() != null) {
			rfi.getBaseCurrency().getCurrencyCode();
		}
		if (rfi.getEventOwner().getBuyer() != null) {
			Buyer buyer = rfi.getEventOwner().getBuyer();
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
		if (rfi.getDeliveryAddress() != null) {
			rfi.getDeliveryAddress().getLine1();
			rfi.getDeliveryAddress().getLine2();
			rfi.getDeliveryAddress().getCity();
			if (rfi.getDeliveryAddress().getState() != null) {
				rfi.getDeliveryAddress().getState().getStateName();
				rfi.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfi.getRfiReminder())) {
			for (RfiReminder reminder : rfi.getRfiReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfi.getMeetings())) {
			for (RfiEventMeeting item : rfi.getMeetings()) {
				item.getStatus().name();
				item.getRemarks();
				item.getVenue();
				item.getAppointmentDateTime();

				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingContacts())) {
					for (RfiEventMeetingContact contact : item.getRfxEventMeetingContacts()) {
						contact.getContactEmail();
						contact.getContactName();
						contact.getContactNumber();
						contact.getRfxEventMeeting().getAppointmentDateTime();
						contact.getRfxEventMeeting().getAppointmentTime();
						contact.getRfxEventMeeting().getCancelReason();
					}
				}
				if (CollectionUtil.isNotEmpty(item.getInviteSuppliers())) {
					for (Supplier supplier : item.getInviteSuppliers()) {
						supplier.getCompanyName();
						supplier.getCommunicationEmail();
					}
				}

				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingDocument())) {
					for (RfiEventMeetingDocument docs : item.getRfxEventMeetingDocument()) {
						docs.getId();
						docs.getFileName();
						docs.getFileSizeInKb();
						docs.getCredContentType();
					}
				}
			}
		}
		// if (CollectionUtil.isNotEmpty(rfi.getDocuments())) {
		// for (RfiEventDocument item : rfi.getDocuments()) {
		// item.getDescription();
		// item.getFileName();
		// item.getUploadDate();
		// item.getFileData();
		// }
		// }
		if (CollectionUtil.isNotEmpty(rfi.getComment())) {
			for (RfiComment comment : rfi.getComment()) {
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

		if (CollectionUtil.isNotEmpty(rfi.getIndustryCategories())) {
			for (IndustryCategory indCat : rfi.getIndustryCategories()) {
				indCat.getCode();
			}
		}
		
		if (CollectionUtil.isNotEmpty(rfi.getSuspensionApprovals())) {
			for (RfiEventSuspensionApproval approver : rfi.getSuspensionApprovals()) {
				if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
					for (RfiSuspensionApprovalUser user : approver.getApprovalUsers()) {
						user.getRemarks();
						user.getUser().getCommunicationEmail();
					}
				}
			}
		}
		
		if(rfi.getGroupCode() != null) {
			rfi.getGroupCode().getId();
			rfi.getGroupCode().getGroupCode();
			rfi.getGroupCode().getDescription();
		}
		
		return rfi;
	}

	/*
	 * @Override public RfiEvent getRfiEventById(String id) { return rfiEventDao.findById(id); }
	 */

	@Override
	@Transactional(readOnly = false)
	public RfiEventContact saveRfiEventContact(RfiEventContact rfiEventContact) {
		return rfiEventContactDao.saveOrUpdate(rfiEventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRfiEventCorrespondenceAddress(RfiEventCorrespondenceAddress rfiEventCorrespondenceAddress) {
		rfiEventCorrespondenceAddressDao.save(rfiEventCorrespondenceAddress);
	}

	@Override
	public List<RfiEventContact> getAllContactForEvent(String eventId) {
		return rfiEventContactDao.findAllEventContactById(eventId);
	}

	@Override
	public List<RfiEventCorrespondenceAddress> getAllCAddressForEvent(String eventId) {
		List<RfiEventCorrespondenceAddress> list = rfiEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfiEventCorrespondenceAddress address : list) {
				LOG.info("State : " + address.getState().getStateName());
				if (address != null && address.getState() != null && address.getState().getCountry() != null) {
					address.getState().getCountry().getCountryName();
				}
			}
		}
		return list;
	}

	@Override
	public RfiEvent loadRfiEventById(String id) {
		RfiEvent event = rfiEventDao.findByEventId(id);
		if (event != null) {
			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
			}
			// if (event.getParticipationFeeCurrency() != null) {
			// event.getParticipationFeeCurrency().getCurrencyName();
			// }
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
				for (RfiEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			if (event.getDeliveryAddress() != null) {
				event.getDeliveryAddress().getCity();
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
				for (RfiTeamMember approver : event.getTeamMembers()) {
					approver.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfiEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfiApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}
			
			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfiEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfiSuspensionApprovalUser user : approver.getApprovalUsers()) {
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
			
			if(event.getTemplate() != null) {
				event.getTemplate().getOptionalSuspendApproval();
				event.getTemplate().getVisibleSuspendApproval();
				event.getTemplate().getReadOnlySuspendApproval();
			}
			
		}
		return event;
	}

	@Override
	public boolean isExists(RfiEvent rfiEvent) {
		return rfiEventDao.isExists(rfiEvent);
	}

	@Override
	public RfiEventContact getEventContactById(String id) {
		return rfiEventContactDao.findById(id);
	}

	@Override
	public List<DraftEventPojo> getAllRfiEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat) throws SubscriptionException {
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
		List<DraftEventPojo> list = rfiEventDao.getAllRfiEventByTenantId(tenantId, loggedInUser, pageNo, serchVal, indCat);
		return list;
	}

	@Override
	public List<RfiEvent> findByEventNameaAndRefNumAndIndCatForTenant(String eventName, String referenceNumber, String industryCategory, String tenantId) {
		List<RfiEvent> rftList = rfiEventDao.findByEventNameaAndRefNumAndIndCatForTenant(eventName, industryCategory, tenantId, null);
		return rftList;
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEvent copyFrom(String eventId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException {
		RfiEvent rfiEvent = this.getRfiEventById(eventId);
		
		if(rfiEvent.getGroupCode() != null &&  Status.INACTIVE == rfiEvent.getGroupCode().getStatus()) {
			LOG.error("The group code " + rfiEvent.getGroupCode().getGroupCode() + " used in Previous Event is inactive");
			throw new ApplicationException("The group code '" + rfiEvent.getGroupCode().getGroupCode() + "' used in Event is inactive");
		}
		
		if (rfiEvent.getDeliveryAddress() != null && rfiEvent.getDeliveryAddress().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Delivery address found ....");
			throw new ApplicationException("Delivery address is Inactive for event:" + rfiEvent.getEventId());
		} else {
			LOG.info("active Delivery address found ........");
		}
		if (rfiEvent.getTemplate() != null && Status.INACTIVE == rfiEvent.getTemplate().getStatus()) {
			LOG.info("inactive Template found for Id .... " + rfiEvent.getTemplate().getId());
			throw new ApplicationException("Template [" + rfiEvent.getTemplate().getTemplateName() + "] used by the event [" + rfiEvent.getEventId() + "] is Inactive");
		}

		RfiEvent newEvent = rfiEvent.copyFrom(rfiEvent);
		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFI")) {
			if (businessUnit != null) {
				LOG.info("business unit selected by user choice selected");
				newEvent.setBusinessUnit(businessUnit);
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

		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFI", newEvent.getBusinessUnit()));

		newEvent.setStatus(EventStatus.DRAFT);

		// Save teamMember
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RfiTeamMember> tm = new ArrayList<RfiTeamMember>();
			for (RfiTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
			}
		}

		if (CollectionUtil.isNotEmpty(newEvent.getUnMaskedUsers())) {
			List<RfiUnMaskedUser> tm = new ArrayList<RfiUnMaskedUser>();
			for (RfiUnMaskedUser unmaskUser : newEvent.getUnMaskedUsers()) {
				unmaskUser.setEvent(newEvent);
				tm.add(unmaskUser);
			}
		}

		// save Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getApprovals())) {
			for (RfiEventApproval app : newEvent.getApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfiApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);

					}
				}
			}
		}
		
		// save Suspension Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getSuspensionApprovals())) {
			for (RfiEventSuspensionApproval app : newEvent.getSuspensionApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfiSuspensionApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);
					}
				}
			}
		}

		// save Doc
		if(CollectionUtil.isNotEmpty(newEvent.getDocuments())){
			for(RfiEventDocument rfiDocument: newEvent.getDocuments()){
				rfiDocument.copyFrom(newEvent);
				LOG.info("Saving document...");
				rfiDocumentService.saveRfiDocuments(rfiDocument);
			}
		}

		List<RfiEnvelop> envelops = newEvent.getRfiEnvelop();
		newEvent.setRfiEnvelop(null);
		RfiEvent newDbEvent = this.saveRfiEvent(newEvent);

		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RfiTeamMember> tm = new ArrayList<RfiTeamMember>();
			for (RfiTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
				rfaEventService.sendTeamMemberEmailNotificationEmail(teamMember.getUser(), teamMember.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newDbEvent.getId());
			}
		}
		// save Approvals
		// Save contacts
		if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
			for (RfiEventContact contact : newEvent.getEventContacts()) {
				contact.setRfxEvent(newDbEvent);
				saveRfiEventContact(contact);
			}
		}

		// save suppliers
		if (CollectionUtil.isNotEmpty(newEvent.getSuppliers())) {
			for (RfiEventSupplier supp : newEvent.getSuppliers()) {
				supp.setRfxEvent(newDbEvent);
				rfiEventSupplierDao.save(supp);
			}
		}

		List<RfiCq> eventCq = new ArrayList<RfiCq>();
		// save CQ
		if (CollectionUtil.isNotEmpty(newEvent.getCqs())) {
			for (RfiCq cq : newEvent.getCqs()) {
				cq.setRfxEvent(newDbEvent);
				RfiCqItem parent = null;
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfiCqItem item : cq.getCqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setRfxEvent(newDbEvent);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
				RfiCq dbCq = rfiCqDao.saveOrUpdate(cq);
				eventCq.add(dbCq);
			}
		}

		// save SOR
		List<RfiEventSor> eventSor = new ArrayList<RfiEventSor>();
		if (CollectionUtil.isNotEmpty(newEvent.getEventSors())) {
			for (RfiEventSor bq : newEvent.getEventSors()) {
				bq.setRfxEvent(newDbEvent);
				RfiEventSor dbBq = rfiEventSorDao.saveOrUpdate(bq);
				eventSor.add(dbBq);
				if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
					RfiSorItem parent = null;
					for (RfiSorItem item : bq.getSorItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setSor(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rfiSorItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
			}
		}

		// save envelop
		if (CollectionUtil.isNotEmpty(envelops)) {
			for (RfiEnvelop envelop : envelops) {
				envelop.setRfxEvent(newDbEvent);

				List<RfiCq> cqsOfEnvelop = new ArrayList<RfiCq>();
				List<RfiCq> envelopCqs = new ArrayList<RfiCq>();
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfiCq bq : envelop.getCqList()) {
						envelopCqs.add(bq);
					}
				}

				List<RfiEventSor> sorsOfEnvelop = new ArrayList<RfiEventSor>();

				List<RfiEventSor> envelopSors = new ArrayList<RfiEventSor>();
				if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
					for (RfiEventSor sor : envelop.getSorList()) {
						envelopSors.add(sor);
					}
				}

				for (RfiEventSor evntsor : eventSor) {
					for (RfiEventSor envsor : envelopSors) {
						if (evntsor.getName().equals(envsor.getName())) {
							sorsOfEnvelop.add(evntsor);
							break;
						}
					}
				}

				for (RfiCq evntCq : eventCq) {
					for (RfiCq envcq : envelopCqs) {
						if (evntCq.getName().equals(envcq.getName())) {
							cqsOfEnvelop.add(evntCq);
							break;
						}
					}
				}
				List<RfiEvaluatorUser> evalUser = new ArrayList<RfiEvaluatorUser>();
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RfiEvaluatorUser eval : envelop.getEvaluators()) {
						if (eval.getUser().isActive()) {
							eval.setEnvelop(envelop);
							evalUser.add(eval);
						}
					}
				}
				List<RfiEnvelopeOpenerUser> openerUserList = new ArrayList<RfiEnvelopeOpenerUser>();
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfiEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
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
				envelop.setCqList(cqsOfEnvelop);
				envelop.setSorList(sorsOfEnvelop);
				envelop = rfiEnvelopDao.save(envelop);

			}
		}
		return newDbEvent;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addEditorUser(String eventId, String userId) {
		RfiEvent rfiEvent = getRfiEventById(eventId);
		List<User> editors = rfiEvent.getEventEditors();
		if (editors == null) {
			editors = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		editors.add(user);
		rfiEvent.setEventEditors(editors);
		updateRfiEvent(rfiEvent);
		LOG.info(editors.size() + "**************" + " Serv editors added: " + editors);
		return editors;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEditorUser(String eventId, String userId) {
		RfiEvent rfiEvent = getRfiEventById(eventId);
		User user = userService.getUsersById(userId);
		if (rfiEvent.getEventEditors() != null) {
			rfiEvent.getEventEditors().remove(user);
		}
		updateRfiEvent(rfiEvent);
		return rfiEvent.getEventEditors();
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit buyerbusinessUnit) throws SubscriptionException, ApplicationException {

		LOG.info("===========================================");
		RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(templateId);
		if (rfxTemplate != null && Status.INACTIVE == rfxTemplate.getStatus()) {
			LOG.info("inactive Template [" + rfxTemplate.getTemplateName() + "] found for Id .... " + rfxTemplate.getId());
			throw new ApplicationException("Template [" + rfxTemplate.getTemplateName() + "] is Inactive");
		}

		RfiEvent newEvent = new RfiEvent();
		newEvent.setTemplate(rfxTemplate);
		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());
		newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		newEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newEvent.setAddSupplier(rfxTemplate.getAddSupplier());
		newEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());
		newEvent.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newEvent.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newEvent.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		newEvent.setEnableSuspensionApproval(rfxTemplate.getEnableSuspendApproval());
		newEvent.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());

		// copy unmasking User

		List<RfiUnMaskedUser> unmaskingUser = new ArrayList<RfiUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RfiUnMaskedUser newRfiUnMaskedUser = new RfiUnMaskedUser();
				newRfiUnMaskedUser.setUser(team.getUser());
				newRfiUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRfiUnMaskedUser);
			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}

		// Copy Evaluation Conclusion Users from template
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RfiEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RfiEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RfiEvaluationConclusionUser conclusionUser = new RfiEvaluationConclusionUser();
				conclusionUser.setUser(user);
				conclusionUser.setEvent(newEvent);
				evaluationConclusionUsers.add(conclusionUser);
			}
			newEvent.setEvaluationConclusionUsers(evaluationConclusionUsers);
			LOG.info("Added Evaluation Conclusion user to Event  : " + (newEvent.getEvaluationConclusionUsers() != null ? newEvent.getEvaluationConclusionUsers().size() : 0));
		}

		// copy approval from template
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RfiEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfiEventApproval newRfApproval = new RfiEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfiApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfiApprovalUser approvalUser = new RfiApprovalUser();
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
			List<RfiEventSuspensionApproval> approvalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Suspension Approval Level :" + templateApproval.getLevel());
				RfiEventSuspensionApproval newRfApproval = new RfiEventSuspensionApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfiSuspensionApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfiSuspensionApprovalUser approvalUser = new RfiSuspensionApprovalUser();
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
						// Setting multiple event categories previously it was only one
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
				case EXPECTED_TENDER_STARTEND_DATTIME:
					LOG.info("============================" + field.getDefaultValue());
					if (field.getDefaultValue() != null) {
						try {
							String expectedTenderDates[];
							expectedTenderDates = field.getDefaultValue().split("-");
							newEvent.setExpectedTenderStartDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(expectedTenderDates[0]));
							newEvent.setExpectedTenderEndDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(expectedTenderDates[1]));
						} catch (Exception e) {
							LOG.error("error while date parse " + e.getMessage(), e);
						}
					}
					break;
				case FEE_STARTEND_DATETIME:
					LOG.info("============================" + field.getDefaultValue());
					if (field.getDefaultValue() != null) {
						try {
							String feeDates[];
							feeDates = field.getDefaultValue().split("-");
							newEvent.setFeeStartDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(feeDates[0]));
							newEvent.setFeeEndDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(feeDates[1]));
						} catch (Exception e) {
							LOG.error("error while date parse " + e.getMessage(), e);
						}
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
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFI")) {
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
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFI", newEvent.getBusinessUnit()));

		newEvent = this.saveRfiEvent(newEvent);
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
			for (RfxEnvelopPojo envpojo : envlopeList) {
				LOG.info("envlop name pojo   :  " + envpojo);

				RfiEnvelop rfienvlope = new RfiEnvelop();
				rfienvlope.setEnvelopTitle(envpojo.getRfxEnvelope());
				rfienvlope.setEnvelopSequence(envpojo.getRfxSequence());
				rfienvlope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rfienvlope.setRfxEvent(newEvent);
				rfienvlope.setEnvelopType(EnvelopType.CLOSED);
				envelopService.saveRfiEnvelop(rfienvlope, null, null, null);
			}
		}

		setDefaultEventContactDetail(createdBy.getId(), newEvent.getId());

		List<RfiTeamMember> teamMembers = new ArrayList<RfiTeamMember>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RfiTeamMember newTeamMembers = new RfiTeamMember();
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
	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId) {
		return favoriteSupplierDao.searchFavouriteSupplierByCompanyNameOrRegistrationNo(searchParam, buyerId, null);
	}

	@Override
	public List<FavouriteSupplier> searchCustomSupplier(SupplierSearchPojo searchParams, String buyerId) {
		return favoriteSupplierDao.searchCustomSupplier(searchParams, buyerId);
	}

	@Override
	public Integer getCountOfEnvelopByEventId(String eventId) {
		return rfiEventDao.getCountOfEnvelopByEventId(eventId);
	}

	@Override
	public RfiEvent loadRfiEventForSummeryById(String id) {
		RfiEvent event = rfiEventDao.findById(id);
		if (event != null) {
			if (event.getTemplate() != null) {
				event.getTemplate().getTemplateName();
			}

			if (CollectionUtil.isNotEmpty(event.getEventSors())) {
				for (RfiEventSor bq : event.getEventSors()) {
					if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
						for (RfiSorItem item : bq.getSorItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}

			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();

				if (event.getDeliveryAddress() != null) {
					event.getDeliveryAddress().getCity();
					if (event.getDeliveryAddress().getState() != null) {
						event.getDeliveryAddress().getState().getStateCode();
						event.getDeliveryAddress().getState().getCountry().getCountryName();

					}
				}
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
			// User unMaskedUser = rfiEventDao.getUnMaskedUserNameAndMailByEventId(event.getId());
			// if (unMaskedUser != null) {
			// event.setUnMaskedUser(unMaskedUser);
			// }

			/*
			 * if (event.getIndustryCategory() != null) { event.getIndustryCategory().getName(); }
			 */

			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfiUnMaskedUser unmaskuser : event.getUnMaskedUsers()) {
					unmaskuser.getUser().getCommunicationEmail();
					unmaskuser.getUser().getName();
					unmaskuser.getUser().getPhoneNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RfiEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getCommunicationEmail();
					user.getUser().getName();
					user.getUser().getPhoneNumber();
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
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfiEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RfiEventSupplier supplier : event.getSuppliers()) {
					supplier.getSupplier().getCompanyName();
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

			// Touch Event reminders...
			if (CollectionUtils.isNotEmpty(event.getRfiReminder())) {
				for (RfiReminder reminder : event.getRfiReminder()) {
					reminder.getInterval();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RfiTeamMember teamMembers : event.getTeamMembers()) {
					teamMembers.getTeamMemberType();
					teamMembers.getUser().getName();
				}
			}
			// if (CollectionUtil.isNotEmpty(event.getDocuments())) {
			// for (RfiEventDocument document : event.getDocuments()) {
			// document.getDescription();
			// }
			// }

			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfiEventMeeting meeting : event.getMeetings()) {
					meeting.getRemarks();
					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier supplier : meeting.getInviteSuppliers()) {
							supplier.getCompanyName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RfiEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
							contact.getContactName();
						}
					}
					// if
					// (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument()))
					// {
					// for (RfiEventMeetingDocument document :
					// meeting.getRfxEventMeetingDocument()) {
					// document.getFileName();
					// }
					// }
				}
			}
			if (CollectionUtil.isNotEmpty(event.getCqs())) {
				for (RfiCq bq : event.getCqs()) {
					if (CollectionUtil.isNotEmpty(bq.getCqItems())) {
						for (RfiCqItem item : bq.getCqItems()) {
							item.getItemName();
							if (CollectionUtil.isNotEmpty(item.getCqOptions())) {
								for (RfiCqOption option : item.getCqOptions()) {
									option.getValue();
								}
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				LOG.info(" Comments  >>>  " + event.getComment());
				for (RfiComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}
			
			if (CollectionUtil.isNotEmpty(event.getSuspensionComment())) {
				LOG.info(" Comments  >>>  " + event.getSuspensionComment());
				for (RfiSuspensionComment comment : event.getSuspensionComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}
			
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfiEventApproval approvalLevel : event.getApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfiApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}
			
			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfiEventSuspensionApproval approvalLevel : event.getSuspensionApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfiSuspensionApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}
			
			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfiUnMaskedUser unmaskuser : event.getUnMaskedUsers()) {
					unmaskuser.getUser().getCommunicationEmail();
					unmaskuser.getUser().getName();
					unmaskuser.getUser().getPhoneNumber();
				}
			}
			
			if(event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getDescription();
			}

			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RfiEventSupplier eventSupplier : event.getSuppliers()) {
					if (eventSupplier.getSupplier() != null) {
						eventSupplier.getSupplier().getCompanyName();
					}
					if (CollectionUtil.isNotEmpty(eventSupplier.getTeamMembers())) {
						for (RfiSupplierTeamMember supplierTeamMember : eventSupplier.getTeamMembers())
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
	@Transactional(readOnly = false)
	public void saveRfiEventReminder(RfiReminder rfiReminder) {
		rfiReminderDao.saveOrUpdate(rfiReminder);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfiEventReminder(RfiReminder rfiReminder) {
		rfiReminderDao.update(rfiReminder);
	}

	@Override
	public List<RfiReminder> getAllRfiEventReminderForEvent(String eventId) {
		return rfiReminderDao.getAllRfiEventReminderForEvent(eventId);

	}

	@Override
	public RfiReminder getRfiEventReminderById(String id) {
		return rfiReminderDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfiReminder(RfiReminder rfiReminder) {
		rfiReminderDao.delete(rfiReminder);
	}

	@Override
	public RfiEvent getRfiEventById(String eventId) {
		RfiEvent rfi = rfiEventDao.findByEventId(eventId);
		if(CollectionUtil.isNotEmpty(rfi.getDocuments())){
			for(RfiEventDocument doc : rfi.getDocuments()){
				if (doc.getUploadBy() != null) {
					doc.getUploadBy().getLoginId();
				}
				doc.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getMeetings())){
			for(RfiEventMeeting meeting: rfi.getMeetings()){
				meeting.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getEventContacts())){
			for(RfiEventContact contact : rfi.getEventContacts()){
				contact.getContactName();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getEventCorrespondenceAddress())){
			for(EventCorrespondenceAddress address: rfi.getEventCorrespondenceAddress()){
				address.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getCqs())){
			for(RfiCq cq: rfi.getCqs()) {
				cq.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getRfiReminder())){
			for(RfiReminder reminder : rfi.getRfiReminder()){
				reminder.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getEventViewers())){
			for(User viewr: rfi.getEventViewers()){
				viewr.getLoginId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getEventEditors())){
			for(User editor: rfi.getEventEditors()){
				editor.getLoginId();
			}
		}
		if (CollectionUtil.isNotEmpty(rfi.getTeamMembers())) {
			for (RfiTeamMember team : rfi.getTeamMembers()) {
				team.getTeamMemberType();
				if (team.getUser() != null) {
					team.getUser().getLoginId();
				}
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getComment())){
			for(RfiComment comment: rfi.getComment()){
				comment.getComment();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getIndustryCategories())){
			for(IndustryCategory industryCategory: rfi.getIndustryCategories()){
				industryCategory.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfi.getSuspensionComment())){
			for(RfiSuspensionComment suspensionComment: rfi.getSuspensionComment()){
				suspensionComment.getId();
			}
		}
		if(rfi.getBusinessUnit() != null){
			rfi.getBusinessUnit().getId();
		}
		for (RfiEnvelop rf : rfi.getRfiEnvelop()) {
			rf.getEnvelopTitle();
			if (CollectionUtil.isNotEmpty(rf.getOpenerUsers())) {
				for (RfiEnvelopeOpenerUser item : rf.getOpenerUsers()) {
					item.getUser();
				}
			}
		}

		for (RfiEventApproval approval : rfi.getApprovals()) {
			for (RfiApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}
		
		if(CollectionUtil.isNotEmpty(rfi.getSuspensionApprovals())) {
			for (RfiEventSuspensionApproval approval : rfi.getSuspensionApprovals()) {
				for (RfiSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}
		
		if (rfi.getEventOwner() != null) {
			rfi.getEventOwner().getName();
			rfi.getEventOwner().getCommunicationEmail();
			rfi.getEventOwner().getPhoneNumber();
			if (rfi.getEventOwner().getOwner() != null) {
				Owner usr = rfi.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		if (CollectionUtil.isNotEmpty(rfi.getUnMaskedUsers())) {
			for (RfiUnMaskedUser usr : rfi.getUnMaskedUsers()) {
				usr.getUserUnmasked();
			}
		}

		if (CollectionUtil.isNotEmpty(rfi.getEvaluationConclusionUsers())) {
			for (RfiEvaluationConclusionUser usr : rfi.getEvaluationConclusionUsers()) {
				usr.getConcluded();
				usr.getUser().getId();
			}
		}

		if (rfi.getEvaluationProcessDeclaration() != null) {
			rfi.getEvaluationProcessDeclaration().getContent();
		}
		if (rfi.getProcurementMethod() != null) {
			rfi.getProcurementMethod().getProcurementMethod();
		}
		if (rfi.getProcurementCategories() != null) {
			rfi.getProcurementCategories().getProcurementCategories();
		}
		
		if(rfi.getGroupCode() != null) {
			rfi.getGroupCode().getId();
			rfi.getGroupCode().getGroupCode();
			rfi.getGroupCode().getStatus();
		}
		return rfi;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addViwersToList(String eventId, String userId) {
		RfiEvent rfiEvent = getRfiEventByeventId(eventId);
		List<User> viewers = rfiEvent.getEventViewers();
		if (viewers == null) {
			viewers = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		viewers.add(user);
		updateRfiEvent(rfiEvent);
		return viewers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeViewersfromList(String eventId, String userId) {
		RfiEvent rfiEvent = getRfiEventByeventId(eventId);
		User user = userService.getUsersById(userId);
		if (rfiEvent.getEventViewers() != null) {
			rfiEvent.getEventViewers().remove(user);
		}
		updateRfiEvent(rfiEvent);
		return rfiEvent.getEventViewers();
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfiTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMember--" + " eventId: " + eventId + " userId: " + userId + " TeamMemberType: " + memberType);
		RfiEvent rfiEvent = getRfiEventByeventId(eventId);
		List<RfiTeamMember> teamMembers = rfiEvent.getTeamMembers();
		LOG.info("TeamMembers :********: " + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfiTeamMember>();
		}
		LOG.info("teamMembers size " + teamMembers.size());
		User user = userService.findTeamUserById(userId);
		RfiTeamMember teamMember = new RfiTeamMember();
		teamMember.setEvent(rfiEvent);
		teamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (RfiTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				teamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;
			}
		}
		teamMember.setTeamMemberType(memberType);
		if (!exists) {
			teamMembers.add(teamMember);
		}
		LOG.info("Team Member : " + teamMember.toLogString());

		rfiEvent.setTeamMembers(teamMembers);
		rfiEventDao.update(rfiEvent);

		try {
			if (!exists) {
				RfiEventAudit audit = new RfiEventAudit(null, rfiEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.added", new Object[] { user.getName(), memberType.getValue() }, Global.LOCALE), null);
				eventAuditService.save(audit);
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been added as '" + memberType.getValue() + "' for event '"+rfiEvent.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			} else {
				if (!previousMemberType.equalsIgnoreCase(memberType.getValue())) {
					RfiEventAudit audit = new RfiEventAudit(null, rfiEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.changed", new Object[] { user.getName(), previousMemberType, memberType.getValue() }, Global.LOCALE), null);
					eventAuditService.save(audit);
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been changed from '" + previousMemberType + "' to '" + memberType.getValue() + "' for Event '"+rfiEvent.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}

		/*
		 * List<User> userList = new ArrayList<User>(); LOG.info("rfiEvent.getTeamMembers(): " +
		 * rfiEvent.getTeamMembers()); for (RfiTeamMember member : rfiEvent.getTeamMembers()) {
		 * LOG.info(member.getUser() + "---------------------	Member"); try { userList.add((User)
		 * member.getUser().clone()); } catch (Exception e) { LOG.error(
		 * "Error constructing list of users after adding Team Member operation : " + e.getMessage(), e); } }
		 */
		return teamMembers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeTeamMembersfromList(String eventId, String userId, RfiTeamMember dbTeamMember) {
		RfiEvent rfiEvent = getRfiEventById(eventId);
		List<RfiTeamMember> teamMember = rfiEvent.getTeamMembers();
		LOG.info("**************" + teamMember);
		if (teamMember == null) {
			teamMember = new ArrayList<RfiTeamMember>();
		}
		User user = userService.getUsersById(userId);

		LOG.info("*****dbTeamMember********" + dbTeamMember);
		LOG.info("**************" + user);
		teamMember.remove(dbTeamMember);
		dbTeamMember.setEvent(null);
		rfiEvent.setTeamMembers(teamMember);
		rfiEventDao.update(rfiEvent);
		// LOG.info("Approver removed." + " " + approvers.size() + " approvers:
		// " + approvers);

		try {
			RfiEventAudit audit = new RfiEventAudit(null, rfiEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.removed", new Object[] { user.getName(), dbTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), null);
			eventAuditService.save(audit);
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + dbTeamMember.getUser().getName() + "' has been removed from '" + dbTeamMember.getTeamMemberType().getValue() + "' for Event '"+rfiEvent.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}

		List<User> userList = new ArrayList<User>();
		// LOG.info("Approver getEventApprovers(): " +
		// rfiEvent.getEventApprovers());
		for (RfiTeamMember app : rfiEvent.getTeamMembers()) {

			// LOG.info(app.getUser() + "--------------------- Approver");
			try {
				userList.add((User) app.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error constructing list of users after removing operation : " + e.getMessage(), e);
			}
		}
		LOG.info(userList.size() + " Event ID :" + eventId);

		return userList;
	}

	@Override
	public RfiTeamMember getRfiTeamMemberByUserIdAndEventId(String eventId, String userId) {
		return rfiEventDao.getRfiTeamMemberByUserIdAndEventId(eventId, userId);
	}

	/*
	 * @Override
	 * @Transactional(readOnly = false) public List<User> reorderApprovers(String eventId, String userId, TeamMemberType
	 * memberType) { RfiEvent rfiEvent = getRfiEventByeventId(eventId); List<RfiTeamMember> approvers =
	 * rfiEvent.getTeamMembers(); RfiTeamMember dbApprover = getRfiTeamMemberByUserIdAndEventId(eventId, userId);
	 * dbApprover.setTeamMemberType(memberType); rfiEvent = rfiEventDao.update(rfiEvent); LOG.info("Approver reordered."
	 * + "	" + approvers.size()); List<User> userList = new ArrayList<User>(); LOG.info(
	 * "Approver getEventApprovers(): " + rfiEvent.getTeamMembers()); Collections.sort(rfiEvent.getTeamMembers()); for
	 * (RfiTeamMember rfiApp : rfiEvent.getTeamMembers()) { try { userList.add((User) rfiApp.getUser().clone()); } catch
	 * (Exception e) { LOG.error("Error constructing list of users after reorder operation : " + e.getMessage(), e); } }
	 * LOG.info(userList.size() + " Event ID :" + eventId); return userList; }
	 */

	@Override
	@Transactional(readOnly = false)
	public void updateEventReminder(RfiReminder rfiReminder) {
		rfiReminderDao.update(rfiReminder);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteContact(RfiEventContact eventContact) {
		rfiEventContactDao.delete(eventContact);
	}

	@Override
	public boolean isExists(RfiEventContact rfiEventContact) {
		return rfiEventContactDao.isExists(rfiEventContact);
	}

	@Override
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		return rfiEventDao.getTeamMembersForEvent(eventId);
	}

	@Override
	public RfiSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId) {
		return rfiSupplierMeetingAttendanceDao.attendenceByEventId(meetingId, eventId, tenantId);
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {
		return rfiEventDao.getUserPemissionsForEvent(userId, eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId) {
		return rfiEventDao.getUserPemissionsForEnvelope(userId, eventId, envelopeId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEvent updateEventApproval(RfiEvent event, User loggedInUser) {

		RfiEvent persistObj = rfiEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfiEventApproval approvalRequest : persistObj.getApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfiApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			int level = 1;
			for (RfiEventApproval app : event.getApprovals()) {
				app.setEvent(event);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfiApprovalUser approvalUser : app.getApprovalUsers()) {
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
		persistObj = rfiEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfiEventAudit audit = new RfiEventAudit(persistObj, loggedInUser, new Date(), AuditActionType.Update, auditMessage);
				rfiEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" +persistObj.getEventId()+"' ."+ auditMessage, loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFI);
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

			rfiEventTimeLineDao.deleteTimeline(id);

			RfiEvent rfiEvent = rfiEventDao.findById(id);
			// Publish Date
			RfiEventTimeLine timeline = new RfiEventTimeLine();
			timeline.setActivityDate(rfiEvent.getEventPublishDate());
			LOG.info("Publish Date for RFI:" + rfiEvent.getEventPublishDate());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfiEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.publish.date", new Object[] { rfiEvent.getEventName() }, Global.LOCALE));
			rfiEventTimeLineDao.save(timeline);

			// Event Start
			timeline = new RfiEventTimeLine();
			timeline.setActivityDate(rfiEvent.getEventStart());
			LOG.info("Start Date for RFI:" + rfiEvent.getEventStart());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfiEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.start.date", new Object[] { rfiEvent.getEventName() }, Global.LOCALE));
			rfiEventTimeLineDao.save(timeline);

			// End Date
			timeline = new RfiEventTimeLine();
			timeline.setActivityDate(rfiEvent.getEventEnd());
			LOG.info("End Date for RFI:" + rfiEvent.getEventEnd());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfiEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.end.date", new Object[] { rfiEvent.getEventName() }, Global.LOCALE));
			rfiEventTimeLineDao.save(timeline);

			// Event Reminders
			if (CollectionUtil.isNotEmpty(rfiEvent.getRfiReminder())) {
				for (RfiReminder reminder : rfiEvent.getRfiReminder()) {
					// Meeting Reminders
					timeline = new RfiEventTimeLine();
					timeline.setActivityDate(reminder.getReminderDate());
					LOG.info("Reminder Date for RFI:" + reminder.getReminderDate());
					timeline.setActivity(EventTimelineType.REMINDER);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfiEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.reminder.date", new Object[] { rfiEvent.getEventName() }, Global.LOCALE));
					rfiEventTimeLineDao.save(timeline);
				}
			}

			// Meetings
			if (CollectionUtil.isNotEmpty(rfiEvent.getMeetings())) {
				for (RfiEventMeeting meeting : rfiEvent.getMeetings()) {
					// Meeting
					timeline = new RfiEventTimeLine();
					timeline.setActivityDate(meeting.getAppointmentDateTime());
					LOG.info("Meeting Appointment Date for RFI:" + meeting.getAppointmentDateTime());
					timeline.setActivity(EventTimelineType.MEETING);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfiEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.meeting.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
					rfiEventTimeLineDao.save(timeline);

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
						// Meeting Reminders
						for (RfiEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
							timeline = new RfiEventTimeLine();
							timeline.setActivityDate(reminder.getReminderDate());
							LOG.info("Meeting Reminder Date for RFI:" + reminder.getReminderDate());
							timeline.setActivity(EventTimelineType.REMINDER);
							timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
							timeline.setEvent(rfiEvent);
							timeline.setDescription(messageSource.getMessage("timeline.event.meeting.reminder.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
							rfiEventTimeLineDao.save(timeline);
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
	public List<RfiEventTimeLine> getRfiEventTimeline(String id) {
		return rfiEventTimeLineDao.getRfiEventTimeline(id);
	}

	@Override
	public JasperPrint generatePdfforEvaluationSummary(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rfiSummary = new ArrayList<EvaluationPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		generatedSdf.setTimeZone(timeZone);
		parameters.put("generatedOn", generatedSdf.format(new Date()));
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RfiEvent event = getRfiEventByeventId(eventId);
			boolean isMasked = false;
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
				isMasked = true;
			}

			if (event != null) {
				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				String type = "RFI";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName(event.getEventOwner().getBuyer().getCompanyName());
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> SupplierList = rfiEventSupplierService.getAllSuppliersByEventId(eventId);
				List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
				if (event.getViewSupplerName()) {
					summary.setSupplierMaskingList(new ArrayList<SupplierMaskingPojo>());
				} else {
					summary.setSupplierMaskingList(buildSupplierMaskingData(SupplierList, eventId));
				}
				if (CollectionUtils.isNotEmpty(SupplierList)) {
					List<EvaluationSuppliersPojo> suppliersForLeadEvaluators = new ArrayList<EvaluationSuppliersPojo>();
					for (EventSupplier supplier : SupplierList) {
						EvaluationSuppliersPojo suppliers = new EvaluationSuppliersPojo();
						suppliers.setContactName(supplier.getSupplier().getFullName());
						if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
							suppliers.setSupplierName(MaskUtils.maskName("SUPPLIER", supplier.getSupplier().getId(), event.getId()));
							summary.setIsMask(true);
						} else {
							summary.setIsMask(false);
							suppliers.setSupplierName(supplier.getSupplierCompanyName());
						}
						suppliers.setDesignation(supplier.getSupplier().getDesignation());
						suppliers.setEmail(supplier.getSupplier().getCommunicationEmail());
						suppliers.setStatus(supplier.getSubmissionStatus().name());
						suppliers.setContactNo(supplier.getSupplier().getMobileNumber());
						suppliers.setIsQualify(supplier.getDisqualify());

						if (supplier.getDisqualify() == Boolean.TRUE) {
							suppliers.setReason(supplier.getDisqualifyRemarks());
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

				List<EvaluationSuppliersSorPojo> supplierSor = getAllSupplierSorforEvaluationSummary(SupplierList, eventId, isMasked);

				// LOG.info(supplierBq.toString());
				summary.setSuppliers(allSuppliers);
				summary.setCqs(allCqs);
				summary.setSorSuppliers(supplierSor);

				rfiSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rfiSummary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfiSummary, false);
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

					List<Sor> bqs = rfiEventSorDao.findSorbyEventId(eventId);

					List<EvaluationSorPojo> allBqs = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Sor bq : bqs) {
							EvaluationSorPojo bqItem = new EvaluationSorPojo();
							bqItem.setName(bq.getName());
							List<RfiSupplierSorItem> suppBqItems = rfiSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());

							List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RfiSupplierSorItem suppBqItem : suppBqItems) {

									EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
									evlBqItem.setDescription(suppBqItem.getItemName());
									evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
									evlBqItem.setAmount(null);
									evlBqItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
									evlBqItems.add(evlBqItem);
									if (suppBqItem.getChildren() != null) {
										for (RfiSupplierSorItem childBqItem : suppBqItem.getChildren()) {
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

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, boolean isMasked) {

		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		List<RfiCq> cqList = rfiCqService.findRfiCqForEvent(eventId);
		for (RfiCq cq : cqList) {
			EvaluationCqPojo cqPojo = new EvaluationCqPojo();
			cqPojo.setName(cq.getName());
			cqPojo.setDescription(cq.getDescription());

			List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
			if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
				for (RfiCqItem cqItem : cq.getCqItems()) {
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
					List<Supplier> suppList = rfiEventSupplierDao.getEventSuppliersForSummary(eventId);

					for (Supplier supplier : suppList) {
						if (isMasked) {
							supplier.setCompanyName(MaskUtils.maskName("SUPPLIER", supplier.getId(), eventId));
						}
					}
					// Below code to get Suppliers Answers of each CQ Items
					if (CollectionUtil.isNotEmpty(suppList)) {
						// List<RfiSupplierCqItem> responseList =
						// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppList);
						List<RfiSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
						if (CollectionUtil.isNotEmpty(responseList)) {

							for (RfiSupplierCqItem suppCqItem : responseList) {
								List<RfiSupplierCqOption> listAnswers = rfiSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
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

									List<RfiCqOption> rfiSupplierCqOptions = rfiCqOptionDao.findOptionsByCqItem(suppCqItem.getCqItem().getId());// suppCqItem.getCqItem().getCqOptions();
									if (CollectionUtil.isNotEmpty(rfiSupplierCqOptions)) {
										for (RfiCqOption rfiSupplierCqOption : rfiSupplierCqOptions) {
											docIds.add(StringUtils.checkString(rfiSupplierCqOption.getValue()));
										}
									}
									List<EventDocument> eventDocuments = rfiDocumentService.findAllRfiEventDocsNamesByEventIdAndDocIds(docIds);
									if (eventDocuments != null) {
										String str = "";
										for (EventDocument docName : eventDocuments) {
											str = str + docName.getFileName() + "\n";
										}
										cqItemSupplierPojo.setAnswer(str);
									}
								} else if (CollectionUtil.isNotEmpty(listAnswers) && (suppCqItem.getCqItem().getCqType() == CqType.LIST || suppCqItem.getCqItem().getCqType() == CqType.CHECKBOX)) {
									String str = "";
									// List<RfiSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfiSupplierCqOption op : listAnswers) {
										@SuppressWarnings("unused")
										int cqAnsListSize = (listAnswers).size();
										@SuppressWarnings("unused")
										int index = (listAnswers).indexOf(op);
										str += op.getValue() + '\n';
									}
									cqItemSupplierPojo.setAnswer(str);
								} else if (CollectionUtil.isNotEmpty(listAnswers) && suppCqItem.getCqItem().getCqType() != CqType.DOCUMENT_DOWNLOAD_LINK) {
									String str = "";
									// List<RfiSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfiSupplierCqOption op : listAnswers) {
										// str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() :
										// "");
										int cqAnsListSize = listAnswers.size();
										int index = listAnswers.indexOf(op);
										str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));
										cqItemSupplierPojo.setAnswer(str);
									}
								}
								/*
								 * if (suppCqItem.getCqItem().getCqType() == CqType.DATE) {
								 * cqItemSupplierPojo.setAnswer(new
								 * SimpleDateFormat("dd/MM/yyyy").format(suppCqItem.getTextAnswers())); }
								 */
								cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
								// Review Comments
								List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
								List<RfiCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
								if (CollectionUtil.isNotEmpty(comments)) {
									String evalComment = "";
									for (RfiCqEvaluationComments item : comments) {
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
	public JasperPrint getEvaluationSummaryPdf(RfiEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = getRfiEventByeventId(event.getId());
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
			eventDetails.setType("RFI");
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
					for (RfiUnMaskedUser rfaUnmaskedUser : event.getUnMaskedUsers()) {
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
				for (RfiEvaluationConclusionUser rfaEvaluationUsers : event.getEvaluationConclusionUsers()) {
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
			if (CollectionUtil.isNotEmpty(event.getRfiReminder())) {
				for (RfiReminder item : event.getRfiReminder()) {
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

			// setting Temp first category only
			// Solving issue PH-2916
			List<IndustryCategoryPojo> industryCategories = new ArrayList<IndustryCategoryPojo>();
			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				LOG.info(" Industry Categories " + event.getIndustryCategories().size());
				for(IndustryCategory category : event.getIndustryCategories()) {
					LOG.info(" 1 ");
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(category.getName());
					industryCategories.add(ic);
				}
			} 
			eventDetails.setCategory(industryCategories);

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
			List<RfiEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfiEventContact contact : eventContacts) {
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
			eventDetails.setGroupCode((event.getGroupCode() != null ? event.getGroupCode().getGroupCode() : "")+ " - " + (event.getGroupCode() != null ? event.getGroupCode().getDescription() : ""));
			
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
			List<RfiCq> cqList = rfiCqService.findRfiCqForEvent(event.getId());
			if (CollectionUtil.isNotEmpty(cqList)) {
				for (RfiCq item : cqList) {
					EvaluationCqPojo cqs = new EvaluationCqPojo();
					cqs.setName(item.getName());
					cqs.setDescription(item.getDescription());

					List<EvaluationCqItemPojo> cqItemList = new ArrayList<EvaluationCqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getCqItems())) {
						for (RfiCqItem cqItem : item.getCqItems()) {
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
								for (RfiCqOption cqOption : cqItem.getCqOptions()) {
									if (cqItem.getCqType() != null) {
										cqItems.setOptionType(cqItem.getCqType() != null ? cqItem.getCqType().getValue() : "");
										if (cqItem.getCqType() == CqType.DATE || cqItem.getCqType() == CqType.CHOICE || cqItem.getCqType() == CqType.LIST || cqItem.getCqType() == CqType.CHECKBOX || cqItem.getCqType() == CqType.TEXT) {
											answer += "\u2022 " + cqOption.getValue() + "     ";
										} else if (cqItem.getCqType() == CqType.CHOICE_WITH_SCORE) {
											answer += "\u1F518 " + cqOption.getValue() + "/" + cqOption.getScoring() + "     ";
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

			List<Sor> sors = rfiEventSorDao.findSorbyEventId(event.getId());
			List<EvaluationSorPojo> allSors = new ArrayList<EvaluationSorPojo>();
			if (CollectionUtil.isNotEmpty(sors)) {
				for (Sor item : sors) {
					EvaluationSorPojo bqPojo = new EvaluationSorPojo();
					bqPojo.setName(item.getName());

					List<RfiSorItem> bqItems = rfiSorItemDao.findSorItemsForSor(item.getId());
					List<EvaluationSorItemPojo> evaluationBqItem = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfiSorItem bqItem : bqItems) {
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
			List<RfiEnvelop> envelops = rfiEnvelopService.getAllEnvelopByEventId(event.getId(), loggedInUser);
			List<EvaluationEnvelopPojo> envlopList = new ArrayList<EvaluationEnvelopPojo>();
			if (CollectionUtil.isNotEmpty(envelops)) {
				for (RfiEnvelop envelop : envelops) {
					EvaluationEnvelopPojo env = new EvaluationEnvelopPojo();
					env.setEnvlopName(envelop.getEnvelopTitle());
					env.setDescription(envelop.getDescription());
					env.setType(envelop.getEnvelopType().getValue());
					env.setOpener(envelop.getOpener() != null ? envelop.getOpener().getName() : "");
					env.setOwner(envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "");
					env.setSequence(envelop.getEnvelopSequence());

					List<EvaluationCqPojo> envlopCqs = new ArrayList<EvaluationCqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
						for (RfiCq item : envelop.getCqList()) {
							EvaluationCqPojo cq = new EvaluationCqPojo();
							cq.setName(item.getName());
							envlopCqs.add(cq);
						}
					}
					List<EvaluationSorPojo> envlopSors = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
						for (RfiEventSor item : envelop.getSorList()) {
							EvaluationSorPojo bq = new EvaluationSorPojo();
							bq.setName(item.getName());
							envlopSors.add(bq);
						}
					}
					// List of evaluators
					List<EvaluationEnvelopPojo> evaluatorList = new ArrayList<EvaluationEnvelopPojo>();
					List<RfiEvaluatorUser> evaluators = envelop.getEvaluators();
					if (CollectionUtil.isNotEmpty(evaluators)) {
						for (RfiEvaluatorUser usr : evaluators) {
							EvaluationEnvelopPojo el = new EvaluationEnvelopPojo();
							el.setOwner(usr.getUser().getName());
							evaluatorList.add(el);
						}
					}

					// List of openers
					List<EvaluationEnvelopPojo> openersList = new ArrayList<EvaluationEnvelopPojo>();
					List<RfiEnvelopeOpenerUser> openers = envelop.getOpenerUsers();
					if (CollectionUtil.isNotEmpty(openers)) {
						for (RfiEnvelopeOpenerUser usr : openers) {
							EvaluationEnvelopPojo el = new EvaluationEnvelopPojo();
							el.setOwner(usr.getUser().getName());
							openersList.add(el);
						}
					}

					env.setOpenerUsers(openersList);
					env.setEvaluator(evaluatorList);
					env.setImagePath(imgPath);
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

				List<RfiEvaluationConclusionUser> evaluationConclusionUserList = rfiEventDao.findEvaluationConclusionUsersByEventId(event.getId());
				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					Log.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RfiEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
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

			List<RfiEventTimeLine> timeline = getRfiEventTimeline(event.getId());
			List<EvaluationTimelinePojo> timelineList = new ArrayList<EvaluationTimelinePojo>();
			if (CollectionUtil.isNotEmpty(timeline)) {
				for (RfiEventTimeLine item : timeline) {
					EvaluationTimelinePojo et = new EvaluationTimelinePojo();
					et.setEventDate(item.getActivityDate() != null ? sdf.format(item.getActivityDate()) : "");
					et.setDescription(item.getDescription());
					et.setType(item.getActivity().name());
					timelineList.add(et);
				}
			}

			// Event Approvals
			RfiEvent rfiEvent = getRfiEventById(event.getId());
			List<RfiEventApproval> approvals = rfiEvent.getApprovals();
			List<EvaluationApprovalsPojo> approvalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (RfiEventApproval item : approvals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(item.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfiApprovalUser usr : item.getApprovalUsers()) {
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
			
//			Suspension Approval
			List<RfiEventSuspensionApproval> suspensionApprovals = rfiEvent.getSuspensionApprovals();
			List<EvaluationApprovalsPojo> susppensionApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionApprovals)) {
				for (RfiEventSuspensionApproval approval : suspensionApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfiSuspensionApprovalUser usr : approval.getApprovalUsers()) {
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

			// Event Documents
			List<EvaluationDocumentPojo> documentList = new ArrayList<EvaluationDocumentPojo>();
			if (CollectionUtil.isNotEmpty(event.getDocuments())) {
				List<RfiEventDocument> document = rfiDocumentDao.findAllRfiEventdocsbyEventId(event.getId());// event.getDocuments();
				for (RfiEventDocument docs : document) {
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
			List<RfiComment> comments = event.getComment();
			List<EvaluationCommentsPojo> commentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(comments)) {
				for (RfiComment item : comments) {
					EvaluationCommentsPojo ec = new EvaluationCommentsPojo();
//					if (item.isApproved() == Boolean.TRUE) {
						ec.setComment(item.getComment());
						ec.setCreatedBy(item.getCreatedBy().getName());
						if (item.getCreatedDate() != null) {
							ec.setCreatedDate(new Date(sdf.format(item.getCreatedDate())));
						}
						ec.setImgPath(imgPath);
						commentDetails.add(ec);
//					}
				}
			}
			
			// Suspension Approval Comments
			List<RfiSuspensionComment> suspensionComments = event.getSuspensionComment();
			List<EvaluationCommentsPojo> suspensionCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionComments)) {
				for (RfiSuspensionComment comment : suspensionComments) {
					EvaluationCommentsPojo ec = new EvaluationCommentsPojo();
//					if (comment.isApproved() == Boolean.TRUE) {
						ec.setComment(comment.getComment());
						ec.setCreatedBy(comment.getCreatedBy().getName());
						if (comment.getCreatedDate() != null) {
							ec.setCreatedDate(new Date(sdf.format(comment.getCreatedDate())));
						}
						ec.setImgPath(imgPath);
						suspensionCommentDetails.add(ec);
//					}
				}
			}

			// Event Audit Details
			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<RfiEventAudit> eventAudit = rfiEventAuditDao.getRfiEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RfiEventAudit ra : eventAudit) {
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
			eventDetails.setSors(allSors);
			eventDetails.setEnvelops(envlopList);
			eventDetails.setEvaluationTeam(membersList);
			eventDetails.setApprovals(approvalList);
			eventDetails.setTimelines(timelineList);
			eventDetails.setDocuments(documentList);
			eventDetails.setAuditDetails(auditList);
			eventDetails.setSuspensionApprovals(susppensionApprovalList);
			eventDetails.setSuspensionComments(suspensionCommentDetails);
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

	private String formatedDecimalNumber(String decimalPoint, BigDecimal value) {
		String decimal = StringUtils.checkString(decimalPoint).length() > 0 ? decimalPoint : "1";
		if (value != null) {
			DecimalFormat df = new DecimalFormat(decimal.equals("1") ? "#,###,###,##0.0" : decimal.equals("2") ? "#,###,###,##0.00" : decimal.equals("3") ? "#,###,###,##0.000" : decimal.equals("4") ? "#,###,###,##0.0000" : decimal.equals("5") ? "#,###,###,##0.00000" : decimal.equals("6") ? "#,###,###,##0.000000" : "#,###,###,##0.00");
			return df.format(value);
		}
		return "";
	}

	/**
	 * @param event
	 * @param imgPath
	 * @param sdf
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RfiEvent event, String imgPath, SimpleDateFormat sdf) {
		List<RfiEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfiEventMeeting meeting : meetingList) {
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
					for (RfiEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
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
					for (RfiEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
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
	public RfiEvent cancelEvent(RfiEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer, User loggedInUser) {

		boolean isCancelAfterSuspend = false;

		RfiEvent persistObj = rfiEventDao.findById(event.getId());

		if (EventStatus.SUSPENDED == persistObj.getStatus()) {
			isCancelAfterSuspend = true;
		}

		persistObj.setStatus(EventStatus.CANCELED);
		persistObj.setCancelReason(event.getCancelReason());
		persistObj = rfiEventDao.update(persistObj);

		// Decrease event count on cancel
		buyerDao.decreaseEventLimitCountByBuyerId(loggedInUser.getBuyer().getId());

		if (persistObj != null) {

			byte[] summarySnapshot = null;
			try {
				JasperPrint eventSummary = getEvaluationSummaryPdf(event, loggedInUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			} catch (JRException e) {
				LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
			}

			RfiEventAudit audit = new RfiEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), persistObj, loggedInUser, new java.util.Date(), AuditActionType.Cancel, messageSource.getMessage("event.audit.canceled", new Object[] { persistObj.getEventName() }, Global.LOCALE), summarySnapshot);
			rfiEventAuditDao.save(audit);

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Event '" +persistObj.getEventId()+ "' Cancelled", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			if (isCancelAfterSuspend) {
				try {
					LOG.info("publishing rfi cancel event to epportal");
					publishEventService.pushRfiEvent(persistObj.getId(), persistObj.getTenantId(), false);
				} catch (Exception e) {
					LOG.error("Error while publishing RFI event to EPortal:" + e.getMessage(), e);
				}
			}

		}
		return persistObj;
	}

	private void buildEnvoleSORData(RfiEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfiEnvelop envelop, boolean isMasked) {
		try {
			List<EnvelopeSorPojo> envopleSorPojos = new ArrayList<EnvelopeSorPojo>();
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<SorPojo> sorIds = rfiEnvelopDao.getSorsIdListByEnvelopIdByOrder(envelopId);

			for (SorPojo bqId : sorIds) {
				EnvelopeSorPojo bqPojo = new EnvelopeSorPojo();
				buildEnvlopeSORTopSupplierComparisionforEvaluationSummary(bqId.getId(), event, bqPojo, isMasked, envelop);

				buildEnvlopeSupplierSORorEvaluationSummary(bqId.getId(), event, bqPojo, envelop, isMasked);
				envopleSorPojos.add(bqPojo);
			}
			auction.setEnvelopeSor(envopleSorPojos);
		} catch (Exception e) {
			LOG.error("Could not get build EnvoleSOR Data : " + e.getMessage(), e);
		}
	}

	private void buildEnvlopeSupplierSORorEvaluationSummary(String bqId, RfiEvent event, EnvelopeSorPojo bqPojo, RfiEnvelop envelop, boolean isMasked) {
		LOG.info("Build envelope data for SOR ");
		try {
			List<EvaluationSuppliersSorPojo> sors = rfiSupplierSorDao.getAllSorsBySorIdsAndEventId(bqId, event.getId());
			List<EvaluationSuppliersSorPojo> supplierBq = new ArrayList<EvaluationSuppliersSorPojo>();
			if (CollectionUtils.isNotEmpty(sors)) {
				for (EvaluationSuppliersSorPojo sor : sors) {
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
						List<RfiSupplierSorItem> suppSorItems = rfiSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierId(sor.getSorId(), sor.getId());
						List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
						if (CollectionUtil.isNotEmpty(suppSorItems)) {
							for (RfiSupplierSorItem suppBqItem : suppSorItems) {
								EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									for (RfiSupplierSorItem childBqItem : suppBqItem.getChildren()) {
										EvaluationSorItemPojo evlBqChilItem = new EvaluationSorItemPojo();
										evlBqChilItem.setDescription(childBqItem.getItemName());
										evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
										evlBqChilItem.setUom(childBqItem.getUom().getUom());
										//Rate
										evlBqChilItem.setAmount(childBqItem.getTotalAmount());
										evlBqChilItem.setDecimal(event != null ? event.getDecimal() : "");
										evlBqItems.add(evlBqChilItem);

										List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
										List<RfiSorEvaluationComments> bqItemComments = rfiSorEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getSorItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RfiSorEvaluationComments review : bqItemComments) {
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


	private void buildEnvlopeSORTopSupplierComparisionforEvaluationSummary(String sorId, RfiEvent event, EnvelopeSorPojo bqPojo, boolean isMasked, RfiEnvelop envelop) {
		try {
			List<RfaSupplierSorPojo> participatedSupplier = rfiSupplierSorDao.getAllRfiTopEventSuppliersIdByEventId(event.getId(), 3, sorId);

			Map<String, EvaluationSorItemPojo> itemsMap = new LinkedHashMap<String, EvaluationSorItemPojo>();
			List<EvaluationSuppliersSorPojo> pojoList = new ArrayList<EvaluationSuppliersSorPojo>();
			EvaluationSuppliersSorPojo pojo = new EvaluationSuppliersSorPojo();
			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierSorPojo rfaSupplierBqPojo = participatedSupplier.get(i);

				bqPojo.setSorName(rfaSupplierBqPojo.getSorName());

				List<RfiSupplierSorItem> suppBqItems = rfiSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierIdParentIdNotNull(sorId, rfaSupplierBqPojo.getSupplierId());
				if (i == 0) {
					for (RfiSupplierSorItem suppBqItem : suppBqItems) {
						EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
						evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
						evlBqItem.setItemName(suppBqItem.getItemName());
						evlBqItem.setUom(suppBqItem.getUom() != null ? suppBqItem.getUom().getUom() : "");
						evlBqItem.setAmount(suppBqItem.getTotalAmount());
						pojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
						itemsMap.put(suppBqItem.getSorItem().getId(), evlBqItem);
					}
				} else {
					for (RfiSupplierSorItem suppBqItem : suppBqItems) {
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
			RfiEvent event = getRfiEventByeventId(eventId);
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

				RfiEventMeeting meetingItem = rfiEventMeetingDao.findById(meetingId);
				EvaluationMeetingPojo meeting = new EvaluationMeetingPojo();
				meeting.setTitle(meetingItem.getTitle());
				if (meetingItem.getAppointmentDateTime() != null) {
					meeting.setAppointmentDateTime(new Date(sdf.format(meetingItem.getAppointmentDateTime())));
				}
				meeting.setRemarks(meetingItem.getRemarks());
				meeting.setVenue(meetingItem.getVenue());

				List<EvaluationMeetingContactsPojo> contacts = new ArrayList<EvaluationMeetingContactsPojo>();
				if (CollectionUtil.isNotEmpty(meetingItem.getRfxEventMeetingContacts())) {
					for (RfiEventMeetingContact mc : meetingItem.getRfxEventMeetingContacts()) {
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

				if (CollectionUtil.isNotEmpty(meetingItem.getInviteSuppliers())) {
					for (Supplier sup : meetingItem.getInviteSuppliers()) {
						EvaluationSuppliersPojo invitedSupplier = new EvaluationSuppliersPojo();
						RfiSupplierMeetingAttendance attendance = rfiSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meetingId, sup.getId());

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
	public String createNextEvent(RfiEvent rfievent, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String concludeRemarks, String[] invitedSupp) throws Exception {
		RfiEvent oldEvent = getRfiEventByeventId(rfievent.getId());
		String oldEventIdDesc = "This Event is created from " + (oldEvent != null ? oldEvent.getEventId() : "") + " ";
		if (oldEvent.getDeliveryAddress() != null && oldEvent.getDeliveryAddress().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Delivery address found ....");
			throw new ApplicationException("Delivery address is Inactive for event:" + oldEvent.getEventId());
		} else {
			LOG.info("active Delivery address found ........");
		}
		if (oldEvent.getGroupCode()!= null && Status.INACTIVE == oldEvent.getGroupCode().getStatus()) {
			LOG.info("inactive Group Code found ....");
			throw new ApplicationException("The group code '" + oldEvent.getGroupCode().getGroupCode() + "' used in Event is inactive");
		} else {
			LOG.info("active Group Code found ........");
		}
		
		String newEventId = null;
		RfxTemplate template = null;

		BusinessUnit selectedbusinessUnit = null;
		if (StringUtils.isNotBlank(businessUnitId))
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
				RfaEvent newEvent = rfievent.createNextRfaEvent(oldEvent, auctionType, loggedInUser, invitedSupp);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
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
				if (newEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || newEvent.getAuctionType() == AuctionType.REVERSE_ENGISH || newEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID || newEvent.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					newEvent.setBillOfQuantity(Boolean.TRUE);
				}

				RfaEvent newdbEvent = rfaEventService.saveRfaEvent(newEvent, loggedInUser);
				// Auction Rules

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

				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfaTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

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

						RfiEventAudit rfiAudit = new RfiEventAudit();
						rfiAudit.setAction(AuditActionType.Create);
						rfiAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfiAudit.setActionDate(new Date());
						rfiAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfiAudit.setEvent(oldEvent);
						eventAuditService.save(rfiAudit);
						
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
						
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFI: {
				RfiEvent newEvent = rfievent.createNextRfiEvent(oldEvent, loggedInUser, invitedSupp);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				if (template != null) {
					newEvent.setTemplate(template);
					createRfiFromTemplate(newEvent, template, selectedbusinessUnit, loggedInUser, false);
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
				RfiEvent newdbEvent = saveRfiEvent(newEvent);
				// save Contacts
				if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
					for (RfiEventContact contact : newEvent.getEventContacts()) {
						contact.setRfxEvent(newdbEvent);
						saveRfiEventContact(contact);
					}
				}
				// save Supplier
				for (RfiEventSupplier supp : newEvent.getSuppliers()) {
					supp.setRfxEvent(newdbEvent);
					rfiEventSupplierDao.save(supp);
				}
				newEventId = newdbEvent.getId();

				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfiTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}

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

						RfiEventAudit rfiAudit = new RfiEventAudit();
						rfiAudit.setAction(AuditActionType.Create);
						rfiAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfiAudit.setActionDate(new Date());
						rfiAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfiAudit.setEvent(oldEvent);
						eventAuditService.save(rfiAudit);
						
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);
						
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				break;
			}
			case RFP: {
				RfpEvent newEvent = rfievent.createNextRfpEvent(oldEvent, loggedInUser, invitedSupp);
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

				newEventId = newdbEvent.getId();
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfpTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}

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

						RfiEventAudit rfiAudit = new RfiEventAudit();
						rfiAudit.setAction(AuditActionType.Create);
						rfiAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfiAudit.setActionDate(new Date());
						rfiAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfiAudit.setEvent(oldEvent);
						eventAuditService.save(rfiAudit);
						
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);
						
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				break;
			}
			case RFQ: {
				RfqEvent newEvent = rfievent.createNextRfqEvent(oldEvent, loggedInUser, invitedSupp);
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

				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfqTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

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

						RfiEventAudit rfiAudit = new RfiEventAudit();
						rfiAudit.setAction(AuditActionType.Create);
						rfiAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfiAudit.setActionDate(new Date());
						rfiAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rfiAudit.setEvent(oldEvent);
						eventAuditService.save(rfiAudit);
						
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
						
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				break;
			}
			case RFT: {
				RftEvent newEvent = rfievent.createNextRftEvent(oldEvent, loggedInUser, invitedSupp);
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
				newEventId = newDbEvent.getId();
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RftTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newDbEvent.getId());

					}
				}

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

						RfiEventAudit rfiAudit = new RfiEventAudit();
						rfiAudit.setAction(AuditActionType.Create);
						rfiAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rfiAudit.setActionDate(new Date());
						rfiAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newDbEvent.getEventId() + " '");
						rfiAudit.setEvent(oldEvent);
						eventAuditService.save(rfiAudit);
						
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
		oldEvent.setNextEventId(newEventId);
		oldEvent.setNextEventType(selectedRfxType);
		oldEvent.setStatus(EventStatus.FINISHED);
		oldEvent.setConcludeBy(loggedInUser);
		oldEvent.setConcludeDate(new Date());
		rfiEventDao.update(oldEvent);
		
		try {
			tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(oldEvent.getId(), EventStatus.FINISHED);
		} catch (Exception e) {
			LOG.error("Error updating Tat Report " + e.getMessage(), e);
		}		
		return newEventId;
	}

	@Override
	public void createRfiFromTemplate(RfiEvent newEvent, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRfs) throws ApplicationException {
		LOG.info("----------------------create Rfi From Template call----------------------------");
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RfiEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfiEventApproval newRfApproval = new RfiEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfiApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfiApprovalUser approvalUser = new RfiApprovalUser();
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
			List<RfiEventSuspensionApproval> suspApprovalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateSuspApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Susp Approval Level :" + templateSuspApproval.getLevel());
				RfiEventSuspensionApproval suspesionApproval = new RfiEventSuspensionApproval();
				suspesionApproval.setApprovalType(templateSuspApproval.getApprovalType());
				suspesionApproval.setLevel(templateSuspApproval.getLevel());
				suspesionApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateSuspApproval.getApprovalUsers())) {
					List<RfiSuspensionApprovalUser> rfiSuspApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateSuspApproval.getApprovalUsers()) {
						RfiSuspensionApprovalUser approvalUser = new RfiSuspensionApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(suspesionApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfiSuspApprovalList.add(approvalUser);
					}
					suspesionApproval.setApprovalUsers(rfiSuspApprovalList);
				}
				suspApprovalList.add(suspesionApproval);
			}
			newEvent.setSuspensionApprovals(suspApprovalList);
		}
		
		List<RfiTeamMember> teamMembers = new ArrayList<RfiTeamMember>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RfiTeamMember newTeamMembers = new RfiTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setEvent(newEvent);
				teamMembers.add(newTeamMembers);
			}
			newEvent.setTeamMembers(teamMembers);
		}

		// copy envlope from rxf template
		newEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newEvent.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newEvent.setAddSupplier(rfxTemplate.getAddSupplier());
		newEvent.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newEvent.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		newEvent.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());
		newEvent.setEnableSuspensionApproval(rfxTemplate.getEnableSuspendApproval());

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
			newEvent.setRfiEnvelop(new ArrayList<RfiEnvelop>());

			for (RfxEnvelopPojo en : envlopeList) {
				LOG.info("envlop name form rfx   :  " + en);
				RfiEnvelop rfienvlope = new RfiEnvelop();
				rfienvlope.setEnvelopTitle(en.getRfxEnvelope());
				rfienvlope.setEnvelopSequence(en.getRfxSequence());
				rfienvlope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rfienvlope.setRfxEvent(newEvent);
				rfienvlope.setEnvelopType(EnvelopType.CLOSED);
				newEvent.getRfiEnvelop().add(rfienvlope);
			}
		}

		if (rfxTemplate.getVisibleViewSupplierName()) {
			newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		}
		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());

		List<RfiUnMaskedUser> unmaskingUser = new ArrayList<RfiUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RfiUnMaskedUser newRftUnMaskedUser = new RfiUnMaskedUser();
				newRftUnMaskedUser.setUser(team.getUser());
				newRftUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRftUnMaskedUser);
			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}

		// Copy Evaluation Conclusion Users from template
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RfiEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RfiEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RfiEvaluationConclusionUser conclusionUser = new RfiEvaluationConclusionUser();
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
						if (isFromRfs) {
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
						if (isFromRfs) {
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
						if (isFromRfs) {
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
						if (isFromRfs) {
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
						// Setting multiple event categories previously it was only one
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
						if (isFromRfs) {
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
				case EXPECTED_TENDER_STARTEND_DATTIME:
					LOG.info("============================" + field.getDefaultValue());
					if (field.getDefaultValue() != null) {
						try {
							String expectedTenderDates[];
							expectedTenderDates = field.getDefaultValue().split("-");
							newEvent.setExpectedTenderStartDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(expectedTenderDates[0]));
							newEvent.setExpectedTenderEndDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(expectedTenderDates[1]));
						} catch (Exception e) {
							LOG.error("error while date parse " + e.getMessage(), e);
						}
					}
					break;
				case FEE_STARTEND_DATETIME:
					LOG.info("============================" + field.getDefaultValue());
					if (field.getDefaultValue() != null) {
						try {
							String feeDates[];
							feeDates = field.getDefaultValue().split("-");
							newEvent.setFeeStartDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(feeDates[0]));
							newEvent.setFeeEndDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(feeDates[1]));
						} catch (Exception e) {
							LOG.error("error while date parse " + e.getMessage(), e);
						}
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
				case ESTIMATED_BUDGET:
					if (field.getDefaultValue() != null) {
						if (isFromRfs) {
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
						if (isFromRfs) {
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
						if (isFromRfs) {
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
						if (isFromRfs) {
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
		if (eventIdSettingsDao.isBusinessSettingEnable(loggedInUser.getTenantId(), "RFI")) {
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
	public RfiEvent getPreviousEventById(String eventId) {
		return rfiEventDao.getPreviousEventById(eventId);
	}

	@Override
	public RfiEvent getPlainEventById(String eventId) {
		RfiEvent event = rfiEventDao.findById(eventId);
		if (event != null) {
			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
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
		}

		return event;
	}

	@Override
	@Transactional(readOnly = false)
	public void suspendEvent(RfiEvent event) {
		if (event.getSuspensionType() == SuspensionType.DELETE_NO_NOTIFY || event.getSuspensionType() == SuspensionType.DELETE_NOTIFY) {
			supplierCqItemDao.deleteSupplierCqItemsForEvent(event.getId());
			try {
				rfiSupplierCqDao.deleteSupplierCqForEvent(event.getId());
			} catch (Exception e) {
			}
			supplierSorItemDao.deleteSupplierSorItemsForEvent(event.getId());
			rfiSupplierSorDao.deleteSupplierSorsForEvent(event.getId());
			rfiEventSupplierDao.updateSubmiTimeOnEventSuspend(event.getId());
		}
		rfiEventDao.suspendEvent(event.getId(), event.getSuspensionType(), event.getSuspendRemarks());
	}

	@Override
	@Transactional(readOnly = false)
	public void resumeEvent(RfiEvent event) {
		rfiEventDao.resumeEvent(event.getId());
	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rfiEventDao.getEventSuppliers(eventId);
	}

	@Override
	public long getEventSuppliersCount(String eventId) {
		return rfiEventSupplierDao.getEventSuppliersCount(eventId);
	}

	@Override
	public List<Date> getAllMeetingDateByEventId(String eventId) {
		return rfiEventMeetingDao.getAllMeetingDateByEventId(eventId);
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		return rfiEventDao.findAssignedTemplateCount(templateId);
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		return rfiEventDao.getPlainEventOwnerByEventId(eventId);
	}

	@Override
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		return rfiEventDao.getPlainTeamMembersForEvent(eventId);
	}

	@Override
	public MobileEventPojo getMobileEventDetailsForSupplier(String id, String userId, String tenantId) throws ApplicationException {
		RfiEvent event = rfiEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a ");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(tenantId, timeZone);
		LOG.info("timeZone....................... " + timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			LOG.info("timeZone....................... if case " + timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}

		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
			List<EventSupplier> supplierList = new ArrayList<>();
			for (EventSupplier eventSupplier : event.getSuppliers()) {
				supplierList.add(eventSupplier.createMobileShallowCopy());
			}
			eventPojo.setSuppliers(supplierList);
		}

		List<EventTimelinePojo> timeLineList = new ArrayList<>();
		LOG.info("service method");
		for (EventTimeline timeLine : rfiEventTimeLineDao.getPlainRfiEventTimeline(event.getId())) {
			EventTimelinePojo time = new EventTimelinePojo();
			Date ActivityDatedate = timeLine.getActivityDate();

			String activityDate = df.format(ActivityDatedate);
			LOG.info("Date in String " + activityDate);
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
		eventPojo.setDocuments(rfiDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRfiEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RfiEnvelop envelope : event.getRfiEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RfiEventApproval eventApproval : event.getApprovals()) {
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
			for (RfiComment comment : event.getComment()) {
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

	private String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
		try {
			if (StringUtils.checkString(suppId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(suppId);
				if (time != null) {
					timeZone = time;
					LOG.info("time Zone :" + timeZone);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	@Override
	public MobileEventPojo getMobileEventDetails(String id, String userId) throws ApplicationException {
		RfiEvent event = rfiEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a ");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(event.getTenantId(), timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}

		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
			List<EventSupplier> supplierList = new ArrayList<>();
			for (EventSupplier eventSupplier : event.getSuppliers()) {
				supplierList.add(eventSupplier.createMobileShallowCopy());
			}
			eventPojo.setSuppliers(supplierList);
		}

		List<EventTimelinePojo> timeLineList = new ArrayList<>();
		LOG.info("service method");
		for (EventTimeline timeLine : rfiEventTimeLineDao.getPlainRfiEventTimeline(event.getId())) {
			EventTimelinePojo time = new EventTimelinePojo();
			Date ActivityDatedate = timeLine.getActivityDate();

			String activityDate = df.format(ActivityDatedate);
			LOG.info("Date in String " + activityDate);
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
		eventPojo.setDocuments(rfiDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRfiEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RfiEnvelop envelope : event.getRfiEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RfiEventApproval eventApproval : event.getApprovals()) {
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
			for (RfiComment comment : event.getComment()) {
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
		RfiEventDocument docs = rfiDocumentDao.findRfiDocsById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
	}

	@Override
	public void downloadEventSummary(String eventId, HttpServletResponse response, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer) {
		try {
			String filename = "unknowEventSummary.pdf";
			RfiEvent event = getRfiEventById(eventId);
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
	public RfiEvent getRfiEventByeventIdForSummaryReport(String eventId) {
		RfiEvent rfi = rfiEventDao.findByEventId(eventId);
		if (rfi.getEventOwner().getBuyer() != null) {
			rfi.getEventOwner().getBuyer().getLine1();
			rfi.getEventOwner().getBuyer().getLine2();
			rfi.getEventOwner().getBuyer().getCity();
			if (rfi.getEventOwner().getBuyer().getState() != null) {
				rfi.getEventOwner().getBuyer().getState().getStateName();
				if (rfi.getEventOwner().getBuyer().getState().getCountry() != null) {
					rfi.getEventOwner().getBuyer().getState().getCountry().getCountryName();
				}
			}
		}
		if (rfi.getBusinessUnit() != null) {
			rfi.getBusinessUnit().getUnitName();
		}
		if (CollectionUtil.isNotEmpty(rfi.getSuppliers())) {
			for (RfiEventSupplier item : rfi.getSuppliers()) {
				item.getSupplier().getStatus();
				item.getSupplier().getCompanyName();
			}
		}
		if (rfi.getEventOwner().getBuyer() != null) {
			Buyer buyer = rfi.getEventOwner().getBuyer();
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
		if (rfi.getDeliveryAddress() != null) {
			rfi.getDeliveryAddress().getLine1();
			rfi.getDeliveryAddress().getLine2();
			rfi.getDeliveryAddress().getCity();
			if (rfi.getDeliveryAddress().getState() != null) {
				rfi.getDeliveryAddress().getState().getStateName();
				rfi.getDeliveryAddress().getState().getCountry().getCountryName();
			}
		}
		if (CollectionUtil.isNotEmpty(rfi.getRfiReminder())) {
			for (RfiReminder reminder : rfi.getRfiReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfi.getMeetings())) {
			for (RfiEventMeeting item : rfi.getMeetings()) {
				item.getStatus().name();
				item.getRemarks();
				item.getVenue();
				item.getAppointmentDateTime();

				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingContacts())) {
					for (RfiEventMeetingContact contact : item.getRfxEventMeetingContacts()) {
						contact.getContactEmail();
						contact.getContactName();
						contact.getContactNumber();
						contact.getRfxEventMeeting().getAppointmentDateTime();
						contact.getRfxEventMeeting().getAppointmentTime();
						contact.getRfxEventMeeting().getCancelReason();
					}
				}
				if (CollectionUtil.isNotEmpty(item.getInviteSuppliers())) {
					for (Supplier supplier : item.getInviteSuppliers()) {
						supplier.getCompanyName();
						supplier.getCommunicationEmail();
					}
				}

				if (CollectionUtil.isNotEmpty(item.getRfxEventMeetingDocument())) {
					for (RfiEventMeetingDocument docs : item.getRfxEventMeetingDocument()) {
						docs.getId();
						docs.getFileName();
						docs.getFileSizeInKb();
						docs.getCredContentType();
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(rfi.getComment())) {
			for (RfiComment comment : rfi.getComment()) {
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

		if (CollectionUtil.isNotEmpty(rfi.getIndustryCategories())) {
			for (IndustryCategory industryCategory : rfi.getIndustryCategories()) {
				industryCategory.getName();
				industryCategory.getCreatedBy();
				if (industryCategory.getCreatedBy() != null) {
					industryCategory.getCreatedBy().getName();
				}
				industryCategory.getCode();
				industryCategory.getBuyer();
			}
		}
		return rfi;
	}

	@Override
	public RfiEvent getRfiEventWithIndustryCategoriesByEventId(String eventId) {
		RfiEvent rfi = rfiEventDao.findByEventId(eventId);
		if (rfi != null && CollectionUtil.isNotEmpty(rfi.getIndustryCategories())) {
			for (IndustryCategory indCat : rfi.getIndustryCategories()) {
				indCat.getCode();
			}
		}
		return rfi;
	}

	@Override
	public boolean isExistsRfiEventId(String tenantId, String eventId) {
		return rfiEventDao.isExistsRfiEventId(tenantId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfiTeamMember> addAssociateOwners(User createdBy, RfiEvent newEvent) {
		List<User> adminUser = userDao.fetchAllActivePlainAdminUsersForTenant(createdBy.getTenantId());
		List<RfiTeamMember> teamMemberList = new ArrayList<RfiTeamMember>();
		if (CollectionUtil.isNotEmpty(adminUser)) {
			for (User user : adminUser) {
				RfiTeamMember teamMember = new RfiTeamMember();
				teamMember.setUser(user);
				teamMember.setEvent(newEvent);
				teamMember.setTeamMemberType(TeamMemberType.Associate_Owner);
				teamMemberList.add(teamMember);
			}
			newEvent.setTeamMembers(teamMemberList);
		}
		rfiEventDao.saveOrUpdate(newEvent);
		return teamMemberList;
	}

	@Override
	@Transactional(readOnly = false)
	public void setDefaultEventContactDetail(String loggedInUserId, String eventId) {
		RfiEventContact eventContact = new RfiEventContact();
		User user = userDao.findById(loggedInUserId);
		eventContact.setContactName(user.getName());
		eventContact.setDesignation(user.getDesignation());
		eventContact.setContactNumber(user.getPhoneNumber());
		eventContact.setComunicationEmail(user.getCommunicationEmail());
		if (user.getBuyer() != null) {
			eventContact.setMobileNumber(user.getBuyer().getMobileNumber());
			eventContact.setFaxNumber(user.getBuyer().getFaxNumber());
		}

		RfiEvent rfiEvent = new RfiEvent();
		rfiEvent.setId(eventId);
		eventContact.setRfxEvent(rfiEvent);
		saveRfiEventContact(eventContact);
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

	@Override
	public List<DraftEventPojo> getAllExcelEventReportForBuyer(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {

		List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		getRfiEventsForExport(tenantId, eventArr, resultList, searchFilterEventPojo, select_all, input, startDate, endDate);
		return resultList;
	}

	private void getRfiEventsForExport(String tenantId, String[] eventArr, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		List<RfiEvent> rftList = rfiEventDao.getEventsByIds(tenantId, eventArr, searchFilterEventPojo, select_all, input, startDate, endDate);
		if (CollectionUtil.isNotEmpty(rftList)) {
			for (RfiEvent event : rftList) {
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
				eventPojo.setType(RfxTypes.RFI);
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
				int submittedCount = 0;
				int acceptedCount = 0;
				BigDecimal leadingAmount = BigDecimal.ZERO;
				for (RfiEventSupplier es : event.getSuppliers()) {
					if (SubmissionStatusType.COMPLETED == es.getSubmissionStatus()) {
						submittedCount++;
					}
					if (es.getAcceptedBy() != null) {
						acceptedCount++;
					}
					if (Boolean.TRUE == es.getDisqualify() || Boolean.FALSE == es.getSubmitted() || SubmissionStatusType.COMPLETED != es.getSubmissionStatus())
						continue;
					boolean allOk = true;

					if (!allOk)
						continue;

				}
				eventPojo.setSubmittedSupplierCount(submittedCount);
				eventPojo.setLeadingAmount(leadingAmount);
				eventPojo.setAcceptedSupplierCount(acceptedCount);
				resultList.add(eventPojo);
			}
		}
	}

	@Override
	public JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rfiSummary = new ArrayList<EvaluationPojo>();
		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RfiEvent event = getRfiEventByeventId(eventId);
			if (event != null) {
				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				String type = "RFI";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName("");
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> SupplierList = rfiEventSupplierService.getAllSuppliersByEventId(eventId);
				List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
				if (CollectionUtils.isNotEmpty(SupplierList)) {
					for (EventSupplier supplier : SupplierList) {
						EvaluationSuppliersPojo suppliers = new EvaluationSuppliersPojo();
						suppliers.setContactName(supplier.getSupplier().getFullName());
						suppliers.setSupplierName(supplier.getSupplierCompanyName());
						suppliers.setDesignation(supplier.getSupplier().getDesignation());
						suppliers.setEmail(supplier.getSupplier().getCommunicationEmail());
						suppliers.setStatus(supplier.getSubmissionStatus().name());
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

				// LOG.info(supplierBq.toString());
				summary.setSuppliers(allSuppliers);
				summary.setCqs(allCqs);

				rfiSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rfiSummary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfiSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, String envelopeId) {

		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		List<RfiCq> cqList = rfiCqService.findRfiCqForEventByEnvelopeId(eventId, envelopeId);
		for (RfiCq cq : cqList) {
			EvaluationCqPojo cqPojo = new EvaluationCqPojo();
			cqPojo.setName(cq.getName());
			cqPojo.setDescription(cq.getDescription());

			List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
			if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
				for (RfiCqItem cqItem : cq.getCqItems()) {
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
					List<Supplier> suppList = rfiEventSupplierDao.getEventSuppliersForSummary(eventId);

					// Below code to get Suppliers Answers of each CQ Items
					if (CollectionUtil.isNotEmpty(suppList)) {
						// List<RfiSupplierCqItem> responseList =
						// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppList);
						List<RfiSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
						if (CollectionUtil.isNotEmpty(responseList)) {
							for (RfiSupplierCqItem suppCqItem : responseList) {
								EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
								cqItemSupplierPojo.setSupplierName(suppCqItem.getSupplier().getCompanyName());

								List<RfiSupplierCqOption> listAnswers = rfiSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
								if (suppCqItem.getCqItem().getCqType() == CqType.TEXT) {
									cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
								} else if (CollectionUtil.isNotEmpty(suppCqItem.getListAnswers())) {
									String str = "";
									// List<RfiSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfiSupplierCqOption op : listAnswers) {
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
								List<RfiCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
								if (CollectionUtil.isNotEmpty(comments)) {
									String evalComment = "";
									for (RfiCqEvaluationComments item : comments) {
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
			RfiEvent event = rfiEventDao.findByEventId(eventId);
			RfiEnvelop envelope = rfiEnvelopService.getRfiEnvelopById(evenvelopId);
			List<EventSupplier> supplierList = rfiEventSupplierDao.getAllSuppliersByEventId(eventId);
			if (event != null) {
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				List<EventSupplier> participatedSupplier = buildSupplierCountData(supplierList, auction);

				RfiEnvelop envelop = rfiEnvelopService.getRfiEnvelopById(evenvelopId);

				boolean isMasked = false;
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					for (EventSupplier eventSupplier : supplierList) {
						eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
					}
					/*
					 * Collections.sort(supplierList, new Comparator<EventSupplier>() { public int compare(EventSupplier
					 * o1, EventSupplier o2) { if (o1.getSupplier().getCompanyName() == null ||
					 * o2.getSupplier().getCompanyName() == null) { return 0; } return
					 * o1.getSupplier().getCompanyName().compareTo(o2.getSupplier().getCompanyName()); } });
					 */
					isMasked = true;
				}
				buildHeadingReport(event, supplierList, auction, sdf, envelope, isMasked, true);
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
				buildMatchingIpAddressData(eventId, auction, supplierList, envelop);
				buildSupplierLineChartAndData(sdf, event, supplierList, auction, envelop, isMasked);
				buildEnvoleBQData(event, auction, sdf, envelop, participatedSupplier);
				buildEnvoleSORData(event, auction, sdf, envelop, isMasked);
				buildEnvelopeCQData(event, auction, sdf, envelop, participatedSupplier, isMasked);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);

		} catch (Exception e) {
			LOG.error("Could not get build Evalution Report Data : " + e.getMessage(), e);
		}
		return auctionSummary;
	}

	private void buildEvaluatorDeclarationAcceptData(SimpleDateFormat sdf, String envelopId, String eventId, EvaluationAuctionBiddingPojo auction) {
		List<RfiEvaluatorDeclaration> evalutorDeclarationList = rfiEvaluatorDeclarationDao.getAllEvaluatorDeclarationByEnvelopAndEventId(envelopId, eventId);
		List<EvaluationDeclarationAcceptancePojo> evaluationDeclarationList = new ArrayList<EvaluationDeclarationAcceptancePojo>();
		if (CollectionUtil.isNotEmpty(evalutorDeclarationList)) {
			for (RfiEvaluatorDeclaration rfiEvaluatorDeclaration : evalutorDeclarationList) {
				EvaluationDeclarationAcceptancePojo evaluationDeclarationPojo = new EvaluationDeclarationAcceptancePojo();
				evaluationDeclarationPojo.setEvaluationCommittee(Boolean.TRUE == rfiEvaluatorDeclaration.getIsLeadEvaluator() ? "Evaluation Owner" : "Evaluation Team");
				evaluationDeclarationPojo.setAcceptedDate(sdf.format(rfiEvaluatorDeclaration.getAcceptedDate()));
				if (rfiEvaluatorDeclaration.getUser() != null) {
					evaluationDeclarationPojo.setUsername(rfiEvaluatorDeclaration.getUser().getName());
					evaluationDeclarationPojo.setUserLoginEmail(rfiEvaluatorDeclaration.getUser().getLoginId());
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
			LOG.error("Could not get build Supplier Count Data : " + e.getMessage(), e);
		}
		return participatedSupplier;
	}

	private void buildHeadingReport(RfiEvent event, List<EventSupplier> supplierList, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfiEnvelop envelope, boolean isMasked, boolean isEvaluation) {
		try {
			List<EvaluationAuctionBiddingPojo> headerBqList = new ArrayList<EvaluationAuctionBiddingPojo>();
			auction.setSupplier(new RfaSupplierBqPojo());
			auction.setBiddingPrice(new ArrayList<EvaluationBiddingPricePojo>());

			EvaluationAuctionBiddingPojo headerPojo = new EvaluationAuctionBiddingPojo();
			headerPojo.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
			buildDisqualificationSuppliersData(event.getId(), headerPojo, isMasked, envelope, sdf, isEvaluation);
			buildMatchingIpAddressData(event.getId(), headerPojo, supplierList, envelope);
			headerBqList.add(headerPojo);
			auction.setHeader(headerBqList);
		} catch (Exception e) {
			LOG.error("Could not get build Heading Report : " + e.getMessage(), e);
		}
	}

	private void buildDisqualificationSuppliersData(String eventId, EvaluationAuctionBiddingPojo auction, boolean isMasked, RfiEnvelop envelope, SimpleDateFormat sdf, boolean isEvaluation) {
		try {
			List<RfiEventSupplier> SupplierList = null;
			if (isEvaluation) {
				SupplierList = rfiEventSupplierService.findDisqualifySupplierForEvaluationReportByEventId(eventId);
			} else {
				SupplierList = rfiEventSupplierService.findDisqualifySupplierByEventId(eventId);
			}
			List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
			if (CollectionUtils.isNotEmpty(SupplierList)) {
				if (SupplierList.size() == 1) {
					for (RfiEventSupplier supplier : SupplierList) {
						EvaluationSuppliersPojo suppliers = new EvaluationSuppliersPojo();
						suppliers.setIsQualify(supplier.getDisqualify());
						if (StringUtils.checkString(supplier.getRejectionRemarks()).length() > 0 && !isEvaluation) {
							suppliers.setRemark(supplier.getRejectionRemarks());
						} else {
							suppliers.setRemark(StringUtils.checkString(supplier.getDisqualifyRemarks()));
						}
						suppliers.setSupplierName(isMasked ? MaskUtils.maskName(envelope.getPreFix(), supplier.getSupplier().getId(), envelope.getId()) : supplier.getSupplierCompanyName());
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
						 * e.setRemark(SzupplierList.get(j).getDisqualifyRemarks()); allSuppliers.add(e); break; }
						 */
						EvaluationSuppliersPojo e = new EvaluationSuppliersPojo();
						e.setSupplierName(isMasked ? (MaskUtils.maskName(envelope.getPreFix(), SupplierList.get(j).getSupplier().getId(), envelope.getId())) : SupplierList.get(j).getSupplierCompanyName());
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
								e.setSupplierName2(isMasked ? (MaskUtils.maskName(envelope.getPreFix(), SupplierList.get(j + 1).getSupplier().getId(), envelope.getId())) : SupplierList.get(j + 1).getSupplierCompanyName());
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
		} catch (

		Exception e) {
			LOG.error("Could not get build Disqualification Suppliers Data : " + e.getMessage(), e);
		}
	}

	private void buildMatchingIpAddressData(String eventId, EvaluationAuctionBiddingPojo auction, List<EventSupplier> participatedSupplier, RfiEnvelop envelop) {
		try {
			List<EvaluationBiddingIpAddressPojo> ipAddressList = new ArrayList<EvaluationBiddingIpAddressPojo>();
			Map<String, String> hm = new HashMap<String, String>();
			int i = 0;

			if (CollectionUtil.isNotEmpty(participatedSupplier)) {
				for (EventSupplier rfiSupplier : participatedSupplier) {
					if (StringUtils.checkString(rfiSupplier.getIpAddress()).length() > 0) {
						if (hm.containsKey(rfiSupplier.getIpAddress())) {
							if (StringUtils.checkString(hm.get(rfiSupplier.getIpAddress())).length() > 0) {
								hm.put(rfiSupplier.getIpAddress(), hm.get(rfiSupplier.getIpAddress()) + " & " + rfiSupplier.getSupplierCompanyName());
							} else {
								hm.put(rfiSupplier.getIpAddress(), rfiSupplier.getSupplierCompanyName());
							}
							i++;
						} else {
							int flag = 0;
							for (EventSupplier rfiSupplierIp : participatedSupplier) {
								if (StringUtils.checkString(rfiSupplierIp.getIpAddress()).length() > 0) {
									if (rfiSupplierIp.getIpAddress().equals(rfiSupplier.getIpAddress())) {
										flag++;
									}
								}
							}
							if (flag > 1) {
								hm.put(rfiSupplier.getIpAddress(), rfiSupplier.getSupplierCompanyName());
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
						// }
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

	private void buildEvaluatorsSummary(List<RfiEvaluatorUser> evaluators, EvaluationAuctionBiddingPojo auction) {
		try {
			List<EvaluationBqItemComments> evaluationSummary = new ArrayList<EvaluationBqItemComments>();
			for (RfiEvaluatorUser rfaEvaluatorUser : evaluators) {
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

	private void buildEventDetailData(SimpleDateFormat sdf, RfiEvent event, EvaluationAuctionBiddingPojo auction) {
		try {
			String auctionDate = event.getEventStart() != null ? sdf.format(event.getEventStart()) : "" + "-" + (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			String auctionTitle = "PRE & POST AUCTION PRICE (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + ")";
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
			auction.setAuctionType(RfxTypes.RFI.getValue());
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
			List<RfiEventDocument> documents = rfiDocumentDao.findAllRfiEventdocsbyEventId(eventId);
			if (CollectionUtil.isNotEmpty(documents)) {
				for (RfiEventDocument docs : documents) {
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

	private void buildSupplierLineChartAndData(SimpleDateFormat sdf, RfiEvent event, List<EventSupplier> participatedSupplier, EvaluationAuctionBiddingPojo auction, RfiEnvelop envelop, boolean isMasked) {
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
						buildSupplierContactListData(bidderContactList, eventSupp, envelop);
					} else {
						eventSupp.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId()));
					}
					if (eventSupp.getAcceptedBy() != null) {
						buildSupplierAcceptedListData(event, eventSupp, bidderAcceptedList, sdf, isMasked, envelop);
					}
					if (SubmissionStatusType.REJECTED == eventSupp.getSubmissionStatus()) {
						buildSupplierRejectedListData(event, eventSupp, bidderRejectedList, sdf, isMasked, envelop);
					}
					buildSupplierInvidedListData(event, eventSupp, bidderInvidedList, sdf, isMasked, envelop);

				}
			}
			List<RfaSupplierBqPojo> eventBq = rfiEventSupplierDao.findRfiSupplierParticipation(event.getId());
			for (RfaSupplierBqPojo eventSupp : eventBq) {
				if (isMasked) {
					eventSupp.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplierId(), envelop.getId()));
				}

				buildSupplierActivitySummaryData(event, eventSupp, supplierActivitySummaryList, sdf);
			}
			buildSupplierTotallyCompleteBidsData(auction, event, isMasked);
			buildSupplierPartiallyCompleteBidsData(auction, event, envelop, isMasked);
			auction.setSupplierBidsList(supplierBidHistory);
			auction.setBidContacts(bidderContactList);
			auction.setSupplierActivitySummary(supplierActivitySummaryList);
			auction.setSupplierAcceptedBids(bidderAcceptedList);
			auction.setSupplierRejectedBids(bidderRejectedList);
			auction.setSupplierInvitedBids(bidderInvidedList);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Line Chart And Data : " + e.getMessage(), e);
		}

	}

	private void buildSupplierContactListData(List<EvaluationBidderContactPojo> bidderContact, EventSupplier eventSupp, RfiEnvelop envelop) {
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

	private void buildSupplierActivitySummaryData(RfiEvent event, RfaSupplierBqPojo eventSupp, List<EvaluationBidderContactPojo> supplierActivitySummaryList, SimpleDateFormat sdf) {
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

	private void buildSupplierAcceptedListData(RfiEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderAcceptedList, SimpleDateFormat sdf, boolean isMasked, RfiEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			supplierActivitySummary.setActionDate(eventSupp.getSupplierEventReadTime() != null ? sdf.format(eventSupp.getSupplierEventReadTime()) : "N/A");
			bidderAcceptedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Accepted List Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierRejectedListData(RfiEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderRejectedList, SimpleDateFormat sdf, boolean isMasked, RfiEnvelop envelop) {
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

	private void buildSupplierInvidedListData(RfiEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderInvidedList, SimpleDateFormat sdf, boolean isMasked, RfiEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			bidderInvidedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Invided List Data : " + e.getMessage(), e);
		}
	}

	private void buildSupplierTotallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfiEvent event, boolean isMasked) {
		auction.setBidderTotallyCompleteBidsList(new ArrayList<EvaluationBiddingPricePojo>());

	}

	private void buildSupplierPartiallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfiEvent event, RfiEnvelop envelop, boolean isMasked) {
		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			auction.setBidderPartiallyCompleteBidsList(bidPriceList);

		} catch (Exception e) {
			LOG.error("Could not get build Supplier Partially Complete Bids Data : " + e.getMessage(), e);
		}
	}

	private void buildEnvoleBQData(RfiEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfiEnvelop envelop, List<EventSupplier> participatedSupplier) {
		try {
			List<EnvelopeBqPojo> envopleBqPojos = new ArrayList<EnvelopeBqPojo>();
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			auction.setEnvelopeBq(envopleBqPojos);
		} catch (Exception e) {
			LOG.error("Could not get build EnvoleBQ Data : " + e.getMessage(), e);
		}
	}

	private void buildEnvelopeCQData(RfiEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfiEnvelop envelop, List<EventSupplier> participatedSupplier, boolean isMasked) {

		try {
			List<EnvelopeCqPojo> allCqs = new ArrayList<EnvelopeCqPojo>();
			List<String> cqid = rfiEnvelopDao.getCqIdlistByEnvelopId(envelop.getId());
			if (CollectionUtil.isNotEmpty(cqid)) {
				List<RfiCq> cqList = rfiCqService.findRfiCqForEventByEnvelopeId(cqid, event.getId());
				for (RfiCq cq : cqList) {
					EnvelopeCqPojo cqPojo = new EnvelopeCqPojo();
					cqPojo.setName(cq.getName());
					buildSupplierCqforEvaluationSummary(participatedSupplier, cq, event, cqPojo, isMasked, envelop, sdf);
					allCqs.add(cqPojo);
				}
			}

			auction.setEnvelopeCq(allCqs);
		} catch (Exception e) {
			LOG.error("Could not get build EnvelopeCQ Data : " + e.getMessage(), e);
		}

	}

	private void buildSupplierCqforEvaluationSummary(List<EventSupplier> participatedSupplier, RfiCq cq, RfiEvent event, EnvelopeCqPojo pojo, boolean isMasked, RfiEnvelop envelop, SimpleDateFormat sdf) {

		try {
			List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();

			if (cq != null) {
				EvaluationCqPojo cqPojo = new EvaluationCqPojo();
				cqPojo.setName(cq.getName());
				cqPojo.setDescription(cq.getDescription());

				List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
				if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
					for (RfiCqItem cqItem : cq.getCqItems()) {

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
							List<Supplier> suppList = rfiEventSupplierDao.getEventSuppliersForSummary(event.getId());
							// Below code to get Suppliers Answers of each CQ Items

							if (CollectionUtil.isNotEmpty(suppList)) {
								// List<RfiSupplierCqItem> responseList =
								// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(),
								// event.getId(),
								// suppList);
								List<RfiSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), event.getId(), suppList);
								if (CollectionUtil.isNotEmpty(responseList)) {
									for (RfiSupplierCqItem suppCqItem : responseList) {
										EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
										cqItemSupplierPojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), suppCqItem.getSupplier().getId(), envelop.getId())) : suppCqItem.getSupplier().getCompanyName());

										List<RfiSupplierCqOption> listAnswers = rfiSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());

										if (suppCqItem.getCqItem().getCqType() == CqType.TEXT || suppCqItem.getCqItem().getCqType() == CqType.DATE || suppCqItem.getCqItem().getCqType() == CqType.NUMBER || suppCqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
											cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
										} else if (CqType.DOCUMENT_DOWNLOAD_LINK == suppCqItem.getCqItem().getCqType()) {
											List<String> docIds = new ArrayList<String>();
											List<RfiCqOption> rfiSupplierCqOptions = suppCqItem.getCqItem().getCqOptions();
											
											if (CollectionUtil.isNotEmpty(rfiSupplierCqOptions)) {
												for (RfiCqOption rfiSupplierCqOption : rfiSupplierCqOptions) {
													docIds.add(StringUtils.checkString(rfiSupplierCqOption.getValue()));
												}
												List<EventDocument> eventDocuments = rfiDocumentService.findAllRfiEventDocsNamesByEventIdAndDocIds(event.getId(), docIds);
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
											for (RfiSupplierCqOption op : listAnswers) {
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
										List<RfiCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), event.getId(), cqItem.getId(), null);
										if (CollectionUtil.isNotEmpty(comments)) {
											String evalComment = "";
											for (RfiCqEvaluationComments item : comments) {
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
			LOG.error("Could not get build Supplier Cq for Evaluation Summary : " + e.getMessage(), e);
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
			LOG.error("Could not generate Submission Report Report. " + e.getMessage(), e);
		}

		return jasperPrint;

	}

	private List<EvaluationAuctionBiddingPojo> buildSubmissionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			RfiEvent event = rfiEventDao.findByEventId(eventId);

			List<EventSupplier> supplierList = rfiEventSupplierDao.getAllSuppliersByEventId(eventId);
			RfiEnvelop envelop = rfiEnvelopService.getRfiEnvelopById(evenvelopId);
			boolean isMasked = false;
			if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
				for (EventSupplier eventSupplier : supplierList) {
					eventSupplier.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()));
				}
				/*
				 * Collections.sort(supplierList, new Comparator<EventSupplier>() { public int compare(EventSupplier o1,
				 * EventSupplier o2) { if (o1.getSupplier().getCompanyName() == null ||
				 * o2.getSupplier().getCompanyName() == null) { return 0; } return
				 * o1.getSupplier().getCompanyName().compareTo(o2.getSupplier().getCompanyName()); } });
				 */
				isMasked = true;
			}
			if (event != null) {
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				auction.setIsMask(isMasked);
				List<EventSupplier> participatedSupplier = buildSupplierCountData(supplierList, auction);

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
				buildSupplierLineChartAndData(sdf, event, supplierList, auction, envelop, isMasked);
				buildEnvoleBQData(event, auction, sdf, envelop, participatedSupplier);
				buildEnvoleSORData(event, auction, sdf, envelop, isMasked);
				buildEnvelopeCQData(event, auction, sdf, envelop, participatedSupplier, isMasked);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);

		} catch (Exception e) {
			LOG.error("Could not get build Submission Report Data " + e.getMessage(), e);
		}
		return auctionSummary;
	}

	@Override
	@Transactional(readOnly = false)
	public void autoSaveRfaDetails(RfiEvent rfiEvent, String[] industryCategory, BindingResult result, String strTimeZone) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (rfiEvent.getEventVisibilityDates() != null) {
				String visibilityDates[] = rfiEvent.getEventVisibilityDates().split("-");
				formatter.setTimeZone(timeZone);
				try {
					Date startDate = (Date) formatter.parse(visibilityDates[0]);
					Date endDate = (Date) formatter.parse(visibilityDates[1]);
					rfiEvent.setEventEnd(endDate);
					rfiEvent.setEventStart(startDate);
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
				rfiEvent.setIndustryCategories(icList);
			}

			if (result.hasErrors()) {
				List<String> errMessages = new ArrayList<String>();
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.info("ERROR : " + err.getDefaultMessage());
				}
			} else {
				if (CollectionUtil.isNotEmpty(rfiEvent.getApprovals())) {
					int level = 1;
					for (RfiEventApproval app : rfiEvent.getApprovals()) {
						app.setEvent(rfiEvent);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
							for (RfiApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}
			}

			if (StringUtils.checkString(rfiEvent.getExpectedTenderDateTimeRange()).length() > 0) {
				String expectedTenderDates[] = rfiEvent.getExpectedTenderDateTimeRange().split("-");
				try {
					rfiEvent.setExpectedTenderStartDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(expectedTenderDates[0]));
					rfiEvent.setExpectedTenderEndDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(expectedTenderDates[1]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (StringUtils.checkString(rfiEvent.getFeeDateTimeRange()).length() > 0) {
				String feeDates[] = rfiEvent.getFeeDateTimeRange().split("-");
				try {
					rfiEvent.setFeeStartDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(feeDates[0]));
					rfiEvent.setFeeEndDate(new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(feeDates[1]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			if (StringUtils.checkString(rfiEvent.getId()).length() > 0) {
				RfiEvent persistObj = getRfiEventByeventId(rfiEvent.getId());
				Date notificationDateTime = null;
				if (rfiEvent.getEventPublishDate() != null && rfiEvent.getEventPublishTime() != null) {
					notificationDateTime = DateUtil.combineDateTime(rfiEvent.getEventPublishDate(), rfiEvent.getEventPublishTime(), timeZone);
				}
				rfiEvent.setEventPublishDate(notificationDateTime);

				setAndUpdateRfiEvent(rfiEvent, persistObj);
			}

		} catch (

		Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}
	}

	private void setAndUpdateRfiEvent(RfiEvent rfiEvent, RfiEvent persistObj) throws ParseException {

		LOG.info("Rfa Event : Bq : " + rfiEvent.getBillOfQuantity() + " : cq : " + rfiEvent.getQuestionnaires() + " : Doc : " + rfiEvent.getDocumentReq() + " : Meet : " + rfiEvent.getMeetingReq());
		persistObj.setModifiedDate(new Date());
		LOG.info("Event Visibilty " + rfiEvent.getEventVisibility());
		persistObj.setIndustryCategory(rfiEvent.getIndustryCategory());
		persistObj.setEventStart(rfiEvent.getEventStart());
		persistObj.setEventEnd(rfiEvent.getEventEnd());
		LOG.info("StartDate:" + rfiEvent.getEventStart());
		LOG.info("EndDate:" + rfiEvent.getEventEnd());
		persistObj.setEventVisibility(rfiEvent.getEventVisibility());
		persistObj.setEventName(rfiEvent.getEventName());
		persistObj.setReferanceNumber(rfiEvent.getReferanceNumber());
		persistObj.setUrgentEvent(rfiEvent.getUrgentEvent());
		persistObj.setEventVisibilityDates(rfiEvent.getEventVisibilityDates());
		persistObj.setDeliveryAddress(rfiEvent.getDeliveryAddress());
		persistObj.setParticipationFeeCurrency(rfiEvent.getParticipationFeeCurrency());
		persistObj.setParticipationFees(rfiEvent.getParticipationFees());
		persistObj.setDepositCurrency(rfiEvent.getDepositCurrency());
		persistObj.setDeposit(rfiEvent.getDeposit());

		// Should not assign this

		if (persistObj.getTemplate() != null && persistObj.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(persistObj.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (!rfxTemplate.getVisibleAddSupplier()) {
				rfiEvent.setAddSupplier(rfxTemplate.getAddSupplier());
			}
			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				rfiEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				rfiEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}
			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				rfiEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}
		}

		persistObj.setEventPublishDate(rfiEvent.getEventPublishDate());
		persistObj.setSubmissionValidityDays(rfiEvent.getSubmissionValidityDays());
		// Event Timeline
		// Date notificationDateTime =
		// DateUtil.combineDateTime(rfaEvent.getSupplierNotificationDate(),
		// rfaEvent.getSupplierNotificationTime());
		// persistObj.setSupplierNotificationDate(notificationDateTime);
		persistObj.setEventStart(rfiEvent.getEventStart());
		persistObj.setApprovals(rfiEvent.getApprovals());

		// Event Req
		persistObj.setEnableEvaluationDeclaration(rfiEvent.getEnableEvaluationDeclaration());
		persistObj.setEnableSupplierDeclaration(rfiEvent.getEnableSupplierDeclaration());
		persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == rfiEvent.getEnableEvaluationDeclaration() && rfiEvent.getEvaluationProcessDeclaration() != null ? rfiEvent.getEvaluationProcessDeclaration() : null);
		persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == rfiEvent.getEnableSupplierDeclaration() && rfiEvent.getSupplierAcceptanceDeclaration() != null ? rfiEvent.getSupplierAcceptanceDeclaration() : null);
		persistObj.setBillOfQuantity(rfiEvent.getBillOfQuantity());
		persistObj.setQuestionnaires(rfiEvent.getQuestionnaires());
		persistObj.setDocumentReq(rfiEvent.getDocumentReq());
		persistObj.setMeetingReq(rfiEvent.getMeetingReq());
		persistObj.setEventDetailCompleted(Boolean.TRUE);
		persistObj.setIndustryCategories(rfiEvent.getIndustryCategories());
		persistObj.setDeliveryDate(rfiEvent.getDeliveryDate());
		persistObj.setViewSupplerName(rfiEvent.getViewSupplerName());
		persistObj.setUnMaskedUser(rfiEvent.getUnMaskedUser());
		persistObj.setCloseEnvelope(rfiEvent.getCloseEnvelope());
		persistObj.setAddSupplier(rfiEvent.getAddSupplier());
		persistObj.setAllowToSuspendEvent(rfiEvent.getAllowToSuspendEvent());
		persistObj.setExpectedTenderStartDate(rfiEvent.getExpectedTenderStartDate());
		persistObj.setExpectedTenderEndDate(rfiEvent.getExpectedTenderEndDate());
		persistObj.setFeeStartDate(rfiEvent.getFeeStartDate());
		persistObj.setFeeEndDate(rfiEvent.getFeeEndDate());

		updateRfiEvent(persistObj);

	}

	@Override
	public List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {

		List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		getRfiSearchEventsForExport(tenantId, eventIds, resultList, searchFilterEventPojo, select_all, startDate, endDate, sdf);
		return resultList;
	}

	private void getRfiSearchEventsForExport(String tenantId, String[] eventIds, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		List<RfiEvent> rftList = rfiEventDao.getSearchEventsByIds(tenantId, eventIds, searchFilterEventPojo, select_all, endDate, startDate);
		if (CollectionUtil.isNotEmpty(rftList)) {
			for (RfiEvent event : rftList) {
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
				eventPojo.setType(RfxTypes.RFI);
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
				if (event.getTeamMembers() != null) {
					String names = "";
					for (RfiTeamMember teamMember : event.getTeamMembers()) {
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

				List<EventSupplier> supplierList = rfiEventSupplierService.getAllSuppliersByEventId(event.getId());
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
				int submittedCount = 0;
				int acceptedCount = 0;
				BigDecimal leadingAmount = BigDecimal.ZERO;
				for (RfiEventSupplier es : event.getSuppliers()) {
					if (SubmissionStatusType.COMPLETED == es.getSubmissionStatus()) {
						submittedCount++;
					}
					if (es.getAcceptedBy() != null) {
						acceptedCount++;
					}
					if (Boolean.TRUE == es.getDisqualify() || Boolean.FALSE == es.getSubmitted() || SubmissionStatusType.COMPLETED != es.getSubmissionStatus())
						continue;
					boolean allOk = true;

					if (!allOk)
						continue;

				}
				eventPojo.setSubmittedSupplierCount(submittedCount);
				eventPojo.setLeadingAmount(leadingAmount);
				eventPojo.setAcceptedSupplierCount(acceptedCount);
				resultList.add(eventPojo);
			}
		}
	}

	@Override
	public EventTimerPojo getTimeEventByeventId(String eventId) {
		return rfiEventDao.getTimeEventByeventId(eventId);
	}

	private List<SupplierMaskingPojo> buildSupplierMaskingData(List<EventSupplier> supplierList, String eventId) {
		List<SupplierMaskingPojo> supplierMaskingList = new ArrayList<SupplierMaskingPojo>();
		List<RfiEnvelop> env = rfiEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFI);
		RfiEvent event = getRfiEventByeventId(eventId);
		for (EventSupplier eventSupplier : supplierList) {
			if (eventSupplier.getSupplier() != null) {
				SupplierMaskingPojo pojo = new SupplierMaskingPojo();
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					pojo.setSupplierName(MaskUtils.maskName("SUPPLIER", eventSupplier.getSupplier().getId(), eventId));
				} else {
					pojo.setSupplierName(eventSupplier.getSupplier() != null ? eventSupplier.getSupplier().getCompanyName() : "");
				}
				List<SupplierMaskingCodePojo> supplierMaskingCodes = new ArrayList<SupplierMaskingCodePojo>();
				for (RfiEnvelop rfiEnvelop : env) {
					SupplierMaskingCodePojo codePojo = new SupplierMaskingCodePojo();
					codePojo.setEnevelopeName(rfiEnvelop.getEnvelopTitle());
					codePojo.setMakedCode(MaskUtils.maskName(rfiEnvelop.getPreFix(), eventSupplier.getSupplier().getId(), rfiEnvelop.getId()));
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
	public List<RfiEvent> getAllRfiEventByTenantId(String tenantId) {
		return rfiEventDao.getAllRfiEventByTenantId(tenantId);
	}

	@Override
	public Event getEventByEventRefranceNo(String eventRefranceNo, String string) {
		return rfiEventDao.getEventByEventRefranceNo(eventRefranceNo, string);
	}

	@Override
	public RfiEvent getPlainEventWithTemplateById(String id) {
		RfiEvent event = rfiEventDao.findById(id);
		if (event != null) {

			for (RfiUnMaskedUser unmask : event.getUnMaskedUsers()) {
				unmask.getUser();
			}

			if (event.getTemplate() != null) {
				event.getTemplate().getTemplateName();
			}
			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
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
		}

		return event;
	}

	@Override
	public EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId) {
		return rfiEventDao.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
	}

	@Override
	public EventPojo loadSupplierEventPojoForSummeryById(String eventId) {
		return rfiEventDao.loadSupplierEventPojoForSummeryById(eventId);
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		return rfiEventDao.getInvitedSupplierCount(eventId);
	}

	@Override
	public long getParticepatedSupplierCount(String eventId) {
		return rfiEventDao.getParticepatedSupplierCount(eventId);
	}

	@Override
	public long getSubmitedSupplierCount(String eventId) {
		return rfiEventDao.getSubmitedSupplierCount(eventId);
	}

	@Override
	public EventPojo getRfiForPublicEventByeventId(String eventId) {
		return rfiEventDao.getRfiForPublicEventByeventId(eventId);
	}

	@Override
	public List<IndustryCategory> getIndustryCategoriesForRfiById(String eventId) {
		return rfiEventDao.getIndustryCategoriesForRfiById(eventId);
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

			// String imgPath = context.getRealPath("resources/images");

			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(StringUtils.checkString(event.getEventName()));
			eventDetails.setEventStart(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			eventDetails.setType("RFI");
			eventDetails.setCompanyName(event.getCompanyName());
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			eventDetails.setVisibility(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			eventDetails.setPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			eventDetails.setDiliveryDate(event.getEventDeliveryDate() != null ? ddsdf.format(event.getEventDeliveryDate()) : "N/A");

			List<IndustryCategory> industryCategories = rfiEventDao.getIndustryCategoriesForRfiById(event.getId());

			if (CollectionUtil.isNotEmpty(industryCategories)) {
				List<IndustryCategoryPojo> categoryNames = new ArrayList<IndustryCategoryPojo>();
				for (IndustryCategory industryCategory : industryCategories) {
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(industryCategory.getName());
					categoryNames.add(ic);
				}
				eventDetails.setIndustryCategoryNames(categoryNames);
			}

			boolean siteVisit = rfiEventMeetingDao.isSiteVisitExist(event.getId());
			eventDetails.setSiteVisit(siteVisit);
			// Event Contact Details.
			List<RfiEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfiEventContact contact : eventContacts) {
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

			summary.add(eventDetails);

			parameters.put("PUBIC_EVENT_SUMMARY", summary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Public RFI Event Summary PDF Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return jasperPrint;
	}

	@Override
	public int updateEventPushedDate(String eventId) {
		int result = rfiEventDao.updateEventPushedDate(eventId);
		return result;

	}

	@Override
	public List<String> getEventTeamMember(String eventId) {
		return rfiEventDao.getEventTeamMember(eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String eventId, EventStatus status) {
		rfiEventDao.updateImmediately(eventId, status);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlagImmediately(String eventId) {
		rfiEventDao.updateEventStartMessageFlag(eventId);
	}

	@Override
	public List<RfiEvent> getAllRfiEventByTenantIdInitial(String loggedInUserTenantId, String string) throws SubscriptionException {
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
		List<RfiEvent> list = rfiEventDao.getAllRfiEventByTenantIdInitial(loggedInUserTenantId, string);
		for (RfiEvent rfiEvent : list) {

			if (rfiEvent.getTemplate() != null) {
				if (rfiEvent.getTemplate().getStatus() == Status.INACTIVE) {
					rfiEvent.setTemplateActive(true);
				} else {
					rfiEvent.setTemplateActive(false);
				}
			}

			if (CollectionUtil.isNotEmpty(rfiEvent.getIndustryCategories()))
				rfiEvent.setIndustryCategory(rfiEvent.getIndustryCategories().get(0));
		}
		return list;
	}

	@Override
	public Integer getCountByEventId(String eventId) {
		return rfiEventDao.getCountByEventId(eventId);
	}

	@Override
	public Event loadRfiEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUsers, List<User> suspApprovalUsers) throws CloneNotSupportedException {
		RfiEvent event = rfiEventDao.findByEventId(eventId);
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
			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfiUnMaskedUser um : event.getUnMaskedUsers()) {
					if (um.getUser() != null) {
						um.getUser().getLoginId();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfiEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			if (event.getDeliveryAddress() != null) {
				event.getDeliveryAddress().getCity();
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
				for (RfiTeamMember teamMembers : event.getTeamMembers()) {
					teamMembers.getUser().getName();
					assignedTeamMembers.add((User) teamMembers.getUser().clone());
				}
			}

			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfiUnMaskedUser teamMembers : event.getUnMaskedUsers()) {
					teamMembers.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RfiEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getName();
				}
			}

			// List<User> userList = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfiEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfiApprovalUser user : approver.getApprovalUsers()) {
							user.getUser().getName();
							
							User us =new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
							
							if (!approvalUsers.contains(us)) {
								approvalUsers.add(us);
							}
						}
					}
				}
			}

//			List<User> suspApprvlUserList = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfiEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfiSuspensionApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
							
							User us =new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
							
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
			if (event.getProcurementMethod() != null) {
				event.getProcurementMethod().getProcurementMethod();
			}
			if (event.getProcurementCategories() != null) {
				event.getProcurementCategories().getProcurementCategories();
			}
			if(event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getDescription();
			}
			buildRfiEventWithModal(model, eventId, format, event, assignedTeamMembers, null);
		}
		return event;
	}

	private void buildRfiEventWithModal(Model model, String eventId, SimpleDateFormat format, Event event, List<User> assignedTeamMembers, List<User> userList) throws CloneNotSupportedException {

		((RfiEvent) (event)).setEventPublishTime(event.getEventPublishDate());
		if (((RfiEvent) (event)).getExpectedTenderEndDate() != null && ((RfiEvent) (event)).getExpectedTenderStartDate() != null) {
			((RfiEvent) (event)).setExpectedTenderDateTimeRange(format.format(((RfiEvent) (event)).getExpectedTenderStartDate()) + " - " + format.format(((RfiEvent) (event)).getExpectedTenderEndDate()));
		}
		if (((RfiEvent) (event)).getFeeEndDate() != null && ((RfiEvent) (event)).getFeeStartDate() != null) {
			((RfiEvent) (event)).setFeeDateTimeRange(format.format(((RfiEvent) (event)).getFeeStartDate()) + " - " + format.format(((RfiEvent) (event)).getFeeEndDate()));
		}
		if (((RfiEvent) (event)).getMinimumSupplierRating() != null) {
			((RfiEvent) (event)).setMinimumSupplierRating(((RfiEvent) (event)).getMinimumSupplierRating());
		}
		if (((RfiEvent) (event)).getMaximumSupplierRating() != null) {
			((RfiEvent) (event)).setMaximumSupplierRating(((RfiEvent) (event)).getMaximumSupplierRating());
		}
		RfiEventContact eventContact = new RfiEventContact();
		eventContact.setEventId(event.getId());
		// model.addAttribute("userList1", userList);
		model.addAttribute("eventContact", eventContact);
		model.addAttribute("eventContactsList", ((RfiEvent) event).getEventContacts());
		model.addAttribute("reminderList", getAllRfiEventReminderForEvent(eventId));
	}

	@Override
	public Event getSimpleEventDetailsById(String eventId) {
		return rfiEventDao.getSimpleEventDetailsById(eventId);
	}

	@Override
	public JasperPrint getEventAuditPdf(RfiEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rfiEventDao.findByEventId(event.getId());
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
			eventDetails.setType("RFI");
			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<RfiEventAudit> eventAudit = rfiEventAuditDao.getRfiEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RfiEventAudit ra : eventAudit) {
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
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}

		}
		return jasperPrint;
	}

	@Override
	public Boolean isDefaultPreSetEnvlope(String eventId, User loggedInUser) {
		return rfiEventDao.isDefaultPreSetEnvlope(eventId, loggedInUser);
	}

	@Override
	public void sendEvaluationCompletedPrematurelyNotification(User actionBy, Event event) {
		String url = APP_URL + "/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
		String msg = "\"" + actionBy.getName() + "\" has concluded evaluation prematurely for \"" + event.getEventName() + "\"";
		String timeZone = "GMT+8:00";
		String subject = "Evaluation Concluded Prematurely";

		User eventOwner = rfiEventDao.getPlainEventOwnerByEventId(event.getId());
		timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);

		// Send to event owner
		sendNotification(event, url, msg, eventOwner, timeZone, subject);

		// send to Associate Owners
		List<EventTeamMember> teamMembers = rfiEventDao.getPlainTeamMembersForEvent(event.getId());
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
			map.put("eventType", RfxTypes.RFI.getValue());
			map.put("businessUnit", StringUtils.checkString(rfiEventDao.findBusinessUnitName(event.getId())));
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
	public RfiEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		RfiEvaluationConclusionUser usr = rfiEventDao.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
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
		List<EvaluationConclusionPojo> rfiSummary = new ArrayList<EvaluationConclusionPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		parameters.put("generatedOn", sdf.format(new Date()));

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationConclusionReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationConclusionPojo summary = new EvaluationConclusionPojo();
			RfiEvent event = rfiEventDao.getPlainEventWithOwnerById(eventId);

			if (event != null) {

				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				summary.setType("Request For Info");
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

				List<RfiEnvelop> envelopList = rfiEnvelopDao.getEnvelopListByEventId(eventId);
				String evalutedEnvelop = "";
				String nonEvalutedEnvelop = "";
				if (CollectionUtil.isNotEmpty(envelopList)) {
					for (RfiEnvelop envelop : envelopList) {
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

				List<RfiEvaluationConclusionUser> evaluationConclusionUserList = rfiEventDao.findEvaluationConclusionUsersByEventId(eventId);
				String conclusionUser = "";

				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					Log.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RfiEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
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
				rfiSummary.add(summary);
			}
			parameters.put("EVALUATION_CONCLUSION", rfiSummary);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfiSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation conclusion  Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RfiEvent event, String name, String feeReference, String timezone) {
		try {
			LOG.info("Send Email to: " + recipientEmail + " for subject: " + subject + " with description:" + description + " and name: " + name + " and timezone: " + timezone);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", name);
			map.put("description", description);
			map.put("eventName", event.getEventName());
			map.put("eventID", event.getEventId());
			map.put("eventFeeReference", feeReference);
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
	public long getRfiEventCountByTenantId(String searchVal, String tenantId, String userId) {
		return rfiEventDao.getRfiEventCountByTenantId(searchVal, tenantId, userId, null);
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEvent updateEventSuspensionApproval(RfiEvent event, User user) {

		RfiEvent persistObj = rfiEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfiEventSuspensionApproval approvalRequest : persistObj.getSuspensionApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfiSuspensionApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			int level = 1;
			for (RfiEventSuspensionApproval app : event.getSuspensionApprovals()) {
				app.setEvent(event);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfiSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
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
		persistObj = rfiEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfiEventAudit audit = new RfiEventAudit(persistObj, user, new Date(), AuditActionType.Update, auditMessage);
				rfiEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" +persistObj.getEventId()+ "' ."+auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFI);
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
	public void downloadRfiEvaluatorDocument(String id, HttpServletResponse response) {
		RfiEvaluatorUser docs = rfiEvaluatorUserDao.getEvaluationDocument(id);
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
	public void downloadRfiLeadEvaluatorDocument(String envelopId, HttpServletResponse response) {
		RfiEnvelop docs = rfiEnvelopDao.getEnvelopEvaluationDocument(envelopId);
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
	@Transactional(readOnly = false)
	public RfiEvent concludeRfiEvent(RfiEvent event, User loggedInUser) {
		RfiEvent dbevent = getRfiEventByeventId(event.getId());
		dbevent.setStatus(EventStatus.FINISHED);
		dbevent.setConcludeRemarks(event.getConcludeRemarks());
		dbevent.setConcludeBy(loggedInUser);
		dbevent.setConcludeDate(new Date());

		return rfiEventDao.update(dbevent);
	}
}
