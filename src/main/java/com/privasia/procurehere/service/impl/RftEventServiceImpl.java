package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.RftSorDao;
import com.privasia.procurehere.core.dao.RftSorItemDao;
import com.privasia.procurehere.core.dao.RftSupplierSorDao;
import com.privasia.procurehere.core.dao.RftSupplierSorItemDao;
import com.privasia.procurehere.core.pojo.EnvelopeSorPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RftSorEvaluationCommentsService;
import com.privasia.procurehere.service.RftSupplierSorItemService;
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
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

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
import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfiReminderDao;
import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftCqDao;
import com.privasia.procurehere.core.dao.RftCqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RftCqItemDao;
import com.privasia.procurehere.core.dao.RftCqOptionDao;
import com.privasia.procurehere.core.dao.RftDocumentDao;
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RftEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RftEventAuditDao;
import com.privasia.procurehere.core.dao.RftEventAwardDao;
import com.privasia.procurehere.core.dao.RftEventAwardDetailsDao;
import com.privasia.procurehere.core.dao.RftEventContactDao;
import com.privasia.procurehere.core.dao.RftEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDao;
import com.privasia.procurehere.core.dao.RftEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftEventTimeLineDao;
import com.privasia.procurehere.core.dao.RftReminderDao;
import com.privasia.procurehere.core.dao.RftSupplierBqDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierCqDao;
import com.privasia.procurehere.core.dao.RftSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.RftSupplierMeetingAttendanceDao;
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
import com.privasia.procurehere.core.enums.FilterTypes;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.MeetingType;
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
import com.privasia.procurehere.core.pojo.ActiveEventPojo;
import com.privasia.procurehere.core.pojo.ApprovedRejectEventPojo;
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
import com.privasia.procurehere.core.pojo.EvaluationSummaryPojo;
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
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.PendingEventPojo;
import com.privasia.procurehere.core.pojo.PublicEventPojo;
import com.privasia.procurehere.core.pojo.RequestParamPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.RftEventCAddressPojo;
import com.privasia.procurehere.core.pojo.RfxEnvelopPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SearchSortFilterPojo;
import com.privasia.procurehere.core.pojo.SupplierListPojo;
import com.privasia.procurehere.core.pojo.SupplierMaskingCodePojo;
import com.privasia.procurehere.core.pojo.SupplierMaskingPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
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
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.ErpIntegrationService;
import com.privasia.procurehere.service.ErpSetupService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.PrService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.service.ProductContractReminderService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpSupplierBqService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqSupplierBqService;
import com.privasia.procurehere.service.RftBqEvaluationCommentsService;
import com.privasia.procurehere.service.RftBqTotalEvaluationCommentsService;
import com.privasia.procurehere.service.RftCqEvaluationCommentsService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftDocumentService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.RftSupplierBqItemService;
import com.privasia.procurehere.service.RftSupplierBqService;
import com.privasia.procurehere.service.RftSupplierCommentService;
import com.privasia.procurehere.service.RftSupplierCqItemService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JREmptyDataSource;
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
public class RftEventServiceImpl implements RftEventService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);
	private static final Logger LOG_S = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	PrService prService;

	@Autowired
	RftBqDao rftEventBqDao;

	@Autowired
	RftEventContactDao rftEventContactDao;

	@Autowired
	RftEventCorrespondenceAddressDao rftEventCorrespondenceAddressDao;

	@Autowired
	NaicsCodesDao naicsCodesDao;

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	RftEventMeetingDao rftEventMeetingDao;

	@Autowired
	RftDocumentDao rftDocumentDao;

	@Autowired
	RftCqEvaluationCommentsDao cqEvaluationCommentsDao;

	@Autowired
	RftBqItemDao rftBqItemDao;

	@Autowired
	RftCqDao rftCqDao;

	@Autowired
	RftCqItemDao rftCqItemDao;

	@Autowired
	RftEventMeetingDocumentDao rftEventMeetingDocumentDao;

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Autowired
	RftSorEvaluationCommentsService rftSorEvaluationCommentsService;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	RftSupplierMeetingAttendanceDao rftSupplierMeetingAttendanceDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	RftBqTotalEvaluationCommentsService rftBqTotalEvaluationCommentsService;

	@Autowired
	RftCqEvaluationCommentsService cqEvaluationCommentsService;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	CostCenterDao costCenterDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RftReminderDao rftReminderDao;

	@Autowired
	RfiReminderDao rfiReminderDao;

	@Autowired
	UserService userService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	BuyerService buyerService;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	RftSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RftSupplierCqDao rftSupplierCqDao;

	@Autowired
	RftEventSupplierDao rftEventSupplierDao;

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	ServletContext context;

	@Autowired
	RftEventTimeLineDao rftEventTimeLineDao;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RftBqEvaluationCommentsService rftBqEvaluationCommentsService;

	@Autowired
	RftSupplierCqItemService supplierCqItemService;

	@Autowired
	RftSupplierBqService supplierBqService;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RftEventAuditDao rftEventAuditDao;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	UserDao userDao;

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
	RfiEventService rfiEventService;

	@Autowired
	RfiEventSupplierDao rfiEventSupplierDao;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfqEventSupplierDao rfqEventSupplierDao;

	@Autowired
	RfqBqDao rfqBqDao;

	@Autowired
	RfqBqItemDao rfqBqItemDao;

	@Autowired
	RftSupplierBqDao rftSupplierBqDao;

	@Autowired
	RftSupplierBqItemService rftSupplierBqItemService;

	@Autowired
	RftSupplierCommentService rftSupplierCommentService;

	@Autowired
	RftCqOptionDao rftCqOptionDao;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	PublishEventService publishEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RftSupplierBqService rftSupplierBqService;

	@Autowired
	RfqSupplierBqService rfqSupplierBqService;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;

	@Autowired
	RfpSupplierBqService rfpSupplierBqService;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	RfqEnvelopDao rfqEnvelopDao;

	@Autowired
	RfpEnvelopDao rfpEnvelopDao;

	@Autowired
	RftEnvelopService envelopService;

	@Autowired
	RfiEnvelopDao rfiEnvelopDao;

	@Autowired
	DeclarationDao declarationDao;

	@Autowired
	RftEvaluatorDeclarationDao rftEvaluatorDeclarationDao;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	RftSupplierCqOptionDao rftSupplierCqOptionDao;

	@Autowired
	ProcurementMethodDao procurementMethodDao;

	@Autowired
	ProcurementCategoriesDao procurementCategoriesDao;

	@Autowired
	GroupCodeDao groupCodeDao;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	RftEvaluatorUserDao rftEvaluatorUserDao;

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
	RftEventAwardDetailsDao rftEventAwardDetailsDao;

	@Autowired
	SupplierPerformanceTemplateService spTemplateService;

	@Autowired
	SupplierPerformanceFormService spFormService;

	@Autowired
	SupplierPerformanceAuditService formAuditService;

	@Autowired
	ContractAuditDao contractAuditDao;

	@Autowired
	RftEventAwardDao rftEventAwardDao;

	@Autowired
	RftSorDao rftEventSorDao;

	@Autowired
	RftSorItemDao rftSorItemDao;

	@Autowired
	RftSupplierSorDao rftSupplierSorDao;

	@Autowired
	RftSupplierSorItemDao rftSupplierSorItemDao;

	@Autowired
	RftSupplierSorItemService rftSupplierSorItemService;

	public List<EvaluationBiddingPricePojo> getSortedEvaluationTopSupplierBids(List<EvaluationBiddingPricePojo> supplierBidsLists) {
		Collections.sort(supplierBidsLists);
		return supplierBidsLists;
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent saveRftEvent(RftEvent rftEvent, User loggedInUser) throws SubscriptionException {

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

		rftEvent.setCreatedDate(new Date());
		rftEvent.setEventOwner(loggedInUser);
		rftEvent.setTenantId(loggedInUser.getTenantId());
		rftEvent.setCreatedBy(loggedInUser);
		LOG.info("Save Event Name :" + rftEvent.getEventId());
		RftEvent dbObj = rftEventDao.saveOrUpdate(rftEvent);

		// If there are unsaved envelopes attached to the event, save them as well
		try {
			if (CollectionUtil.isNotEmpty(rftEvent.getRftEnvelop())) {
				for (RftEnvelop env : rftEvent.getRftEnvelop()) {
					if (StringUtils.checkString(env.getId()).length() == 0 && env.getEnvelopTitle() != null) {
						env.setRfxEvent(dbObj);
						rftEnvelopDao.save(env);
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
		if (rftEvent.isUploadDocuments()) {
			dbObj.setUploadDocuments(true);
		}
		return dbObj;
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent updateRftEvent(RftEvent rftEvent) {
		RftEvent event = rftEventDao.update(rftEvent);
		if (event != null && event.getCreatedBy() != null) {
			event.getCreatedBy().getLoginId();
			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RftEventSupplier sup : event.getSuppliers()) {
					sup.getSupplierCompanyName();
					if (sup.getSupplier() != null)
						sup.getSupplier().getCompanyName();
				}
			}
		}
		return event;

	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent updateEventSimple(RftEvent rftEvent) {
		return rftEventDao.update(rftEvent);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {
		return rftEventDao.getUserPemissionsForEvent(userId, eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId) {
		return rftEventDao.getUserPemissionsForEnvelope(userId, eventId, envelopeId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRftEvent(RftEvent rftEvent) {
		rftEventDao.delete(rftEvent);
	}

	@Override
	public RftEvent getEventById(String id) {
		RftEvent rft = rftEventDao.findById(id);
		for (RftEventApproval approval : rft.getApprovals()) {
			for (RftApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}

		if (CollectionUtil.isNotEmpty(rft.getSuspensionApprovals())) {
			for (RftEventSuspensionApproval approval : rft.getSuspensionApprovals()) {
				for (RftSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}

		if (rft.getBaseCurrency() != null) {
			rft.getBaseCurrency().getCurrencyCode();
		}

		for (RftEnvelop rf : rft.getRftEnvelop()) {
			rf.getEnvelopTitle();
			if (CollectionUtil.isNotEmpty(rf.getOpenerUsers())) {
				for (RftEnvelopeOpenerUser item : rf.getOpenerUsers()) {
					item.getUser();
				}
			}
		}

		if (rft.getEventOwner() != null) {
			rft.getEventOwner().getName();
			rft.getEventOwner().getCommunicationEmail();
			rft.getEventOwner().getPhoneNumber();
			if (rft.getEventOwner().getOwner() != null) {
				Owner usr = rft.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}

		if (CollectionUtil.isNotEmpty(rft.getUnMaskedUsers())) {
			for (RftUnMaskedUser usr : rft.getUnMaskedUsers()) {
				usr.getUserUnmasked();
			}
		}

		if (CollectionUtil.isNotEmpty(rft.getEvaluationConclusionUsers())) {
			for (RftEvaluationConclusionUser usr : rft.getEvaluationConclusionUsers()) {
				usr.getConcluded();
				usr.getUser().getId();
			}
		}

		if (rft.getEvaluationProcessDeclaration() != null) {
			rft.getEvaluationProcessDeclaration().getContent();
		}

		if (rft.getProcurementMethod() != null) {
			rft.getProcurementMethod().getProcurementMethod();
		}
		if (rft.getProcurementCategories() != null) {
			rft.getProcurementCategories().getProcurementCategories();
		}
		if (rft.getGroupCode() != null) {
			rft.getGroupCode().getId();
			rft.getGroupCode().getGroupCode();
		}
		return rft;
	}

	@Override
	public RftEvent getRftEventById(String id) {
		RftEvent rft = rftEventDao.findById(id);
		for (RftEventApproval approval : rft.getApprovals()) {
			for (RftApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}

		for (RftEventSuspensionApproval approval : rft.getSuspensionApprovals()) {
			for (RftSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}

		if (rft.getTemplate() != null) {
			rft.getTemplate().getTemplateName();
		}

		for (RftEventAwardApproval approval : rft.getAwardApprovals()) {
			for (RftAwardApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getUser();
				approvalUser.getRemarks();
			}
		}

		for (RftEnvelop rf : rft.getRftEnvelop()) {
			rf.getEnvelopTitle();
			if (CollectionUtil.isNotEmpty(rf.getOpenerUsers())) {
				for (RftEnvelopeOpenerUser item : rf.getOpenerUsers()) {
					item.getUser();
				}
			}
		}

		if (rft.getBaseCurrency() != null) {
			rft.getBaseCurrency().getCurrencyCode();
		}
		if (rft.getEventOwner() != null) {
			rft.getEventOwner().getName();
			rft.getEventOwner().getCommunicationEmail();
			rft.getEventOwner().getPhoneNumber();
			if (rft.getEventOwner().getOwner() != null) {
				Owner usr = rft.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		if (rft.getTemplate() != null) {
			rft.getTemplate().getReadOnlyCloseEnvelope();
		}

		if (CollectionUtil.isNotEmpty(rft.getUnMaskedUsers())) {
			for (RftUnMaskedUser usr : rft.getUnMaskedUsers()) {
				usr.getUserUnmasked();
			}
		}

		if (CollectionUtil.isNotEmpty(rft.getEvaluationConclusionUsers())) {
			for (RftEvaluationConclusionUser usr : rft.getEvaluationConclusionUsers()) {
				usr.getConcluded();
				usr.getUser().getId();
			}
		}
		if (rft.getGroupCode() != null) {
			rft.getGroupCode().getId();
			rft.getGroupCode().getGroupCode();
		}

		if (rft.getBusinessUnit() != null) {
			rft.getBusinessUnit().getUnitName();
		}

		if (rft.getCostCenter() != null) {
			rft.getCostCenter().getCostCenter();
		}

		if (CollectionUtil.isNotEmpty(rft.getAwardComment())) {
			for (RftAwardComment comment : rft.getAwardComment()) {
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getLoginId();
				}
			}
		}

		return rft;
	}

	@Override
	public List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue) {
		return naicsCodesDao.findLeafIndustryCategoryByName(searchValue);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRftEventContact(RftEventContact rftEventContact) {
		rftEventContactDao.saveOrUpdate(rftEventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRftEventContact(RftEventContact rftEventContact) {
		rftEventContactDao.update(rftEventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRftEventCorrespondenceAddress(RftEventCorrespondenceAddress rftEventCorrespondenceAddress) {
		rftEventCorrespondenceAddressDao.save(rftEventCorrespondenceAddress);
	}

	@Override
	public RftEvent getRftEventByeventId(String eventId) {
		RftEvent event = rftEventDao.findByEventId(eventId);
		if (event != null) {
			if (CollectionUtil.isNotEmpty(event.getCqs())) {
				for (RftCq cq : event.getCqs()) {
					cq.getName();
					if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
						for (RftCqItem item : cq.getCqItems()) {
							item.getItemName();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RftEventSuspensionApproval approval : event.getSuspensionApprovals()) {
					for (RftSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
						approvalUser.getUser().getCommunicationEmail();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RftUnMaskedUser item : event.getUnMaskedUsers()) {
					item.getUser();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RftEvaluationConclusionUser item : event.getEvaluationConclusionUsers()) {
					item.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RftEventSupplier supplier : event.getSuppliers()) {
					supplier.getSupplier().getFullName();
					supplier.getSupplier().getCommunicationEmail();
					supplier.getSupplier().getCompanyContactNumber();
					supplier.getSupplier().getCompanyName();
					supplier.getSupplier().getStatus();
				}
			}
			if (event.getEventOwner().getBuyer() != null) {
				Buyer buyer = event.getEventOwner().getBuyer();
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
			if (event.getDeliveryAddress() != null) {
				event.getDeliveryAddress().getTitle();
				event.getDeliveryAddress().getLine1();
				event.getDeliveryAddress().getLine2();
				event.getDeliveryAddress().getCity();
				event.getDeliveryAddress().getState().getStateName();
				event.getDeliveryAddress().getState().getCountry().getCountryName();
			}
			if (CollectionUtil.isNotEmpty(event.getRftReminder())) {
				for (RftReminder reminder : event.getRftReminder()) {
					reminder.getReminderDate();
					reminder.getStartReminder();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RftEventMeeting meeting : event.getMeetings()) {
					meeting.getAppointmentDateTime();
					meeting.getAppointmentTime();
					meeting.getInviteSuppliers();
					meeting.getTitle();
					meeting.getRfxEventMeetingContacts();
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RftEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
							meetingContact.getContactName();
							meetingContact.getContactNumber();
							meetingContact.getContactEmail();
							meetingContact.getId();

						}
					}
					meeting.getStatus();
					meeting.getVenue();

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
						for (RftEventMeetingDocument meetingDocument : meeting.getRfxEventMeetingDocument()) {
							meetingDocument.getId();
							meetingDocument.getFileSizeInKb();
							meetingDocument.getCredContentType();
							meetingDocument.getFileName();
						}
					}

					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier suppliers : meeting.getInviteSuppliers()) {
							suppliers.getCompanyName();
							suppliers.getCommunicationEmail();
							suppliers.getCompanyContactNumber();
						}
					}
					meeting.getRemarks();
				}
			}
			// if (CollectionUtil.isNotEmpty(event.getDocuments())) {
			// for (RftEventDocument item : event.getDocuments()) {
			// item.getDescription();
			// item.getFileName();
			// item.getUploadDate();
			// item.getFileData();
			// }
			// }
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				for (RftComment comment : event.getComment()) {
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
			if (event.getEventOwner() != null) {
				event.getEventOwner().getName();
				event.getEventOwner().getCommunicationEmail();
				event.getEventOwner().getPhoneNumber();
				if (event.getEventOwner().getOwner() != null) {
					Owner usr = event.getEventOwner().getOwner();
					usr.getCompanyContactNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				for (IndustryCategory indCat : event.getIndustryCategories()) {
					indCat.getCode();
				}
			}
			if (event.getProcurementMethod() != null) {
				event.getProcurementMethod().getProcurementMethod();
			}
			if (event.getProcurementCategories() != null) {
				event.getProcurementCategories().getProcurementCategories();
			}
			if (event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getStatus();
			}
			event.getProcurementCategories();
			event.getProcurementMethod();

			if(CollectionUtil.isNotEmpty(event.getDocuments())){
				for(RftEventDocument doc : event.getDocuments()){
					if (doc.getUploadBy() != null) {
						doc.getUploadBy().getLoginId();
					}
					doc.getId();
				}
			}
			if(CollectionUtil.isNotEmpty(event.getEventContacts())){
				for(RftEventContact contact : event.getEventContacts()){
					contact.getContactName();
				}
			}
			if(CollectionUtil.isNotEmpty(event.getEventCorrespondenceAddress())){
				for(EventCorrespondenceAddress address: event.getEventCorrespondenceAddress()){
					address.getId();
				}
			}
			if(CollectionUtil.isNotEmpty(event.getEventBqs())){
				for(RftEventBq bq: event.getEventBqs()){
					bq.getId();
				}
			}
			for (RftEnvelop rf : event.getRftEnvelop()) {
				rf.getEnvelopTitle();
				if (CollectionUtil.isNotEmpty(rf.getOpenerUsers())) {
					for (RftEnvelopeOpenerUser item : rf.getOpenerUsers()) {
						item.getUser();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RftTeamMember team : event.getTeamMembers()) {
					team.getTeamMemberType();
					if (team.getUser() != null) {
						team.getUser().getLoginId();
					}
				}
			}
			if (CollectionUtils.isNotEmpty(event.getApprovals())) {
				for (RftEventApproval approval : event.getApprovals()) {
					for (RftApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
					}
				}
			}
			if(CollectionUtil.isNotEmpty(event.getAwardedSuppliers())){
				for(Supplier supplier: event.getAwardedSuppliers()){
					supplier.getId();
				}
			}
			if(CollectionUtil.isNotEmpty(event.getAwardComment())){
				for(RftAwardComment awardComment: event.getAwardComment()){
					awardComment.getComment();
				}
			}
			if(CollectionUtil.isNotEmpty(event.getSuspensionComment())){
				for(RftSuspensionComment rftSuspensionComment: event.getSuspensionComment()){
					rftSuspensionComment.getComment();
				}
			}
			if (CollectionUtils.isNotEmpty(event.getAwardApprovals())) {
				for (RftEventAwardApproval approval : event.getAwardApprovals()) {
					for (RftAwardApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
						approvalUser.getUser().getCommunicationEmail();
						approvalUser.getUser();
					}
				}
			}
			if(event.getBusinessUnit() != null){
				event.getBusinessUnit().getId();
			}
			if(CollectionUtil.isNotEmpty(event.getAdditionalDocument())){
				for(AdditionalDocument doc : event.getAdditionalDocument()){
					doc.getId();
				}
			}
		}
		return event;
	}

	@Override
	public RftEvent getRftEventWithBuByeventId(String eventId) {
		RftEvent event = rftEventDao.findByEventId(eventId);
		if (event != null) {
			if (CollectionUtil.isNotEmpty(event.getCqs())) {
				for (RftCq cq : event.getCqs()) {
					cq.getName();
					if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
						for (RftCqItem item : cq.getCqItems()) {
							item.getItemName();
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RftEventSupplier supplier : event.getSuppliers()) {
					supplier.getSupplier().getFullName();
					supplier.getSupplier().getCommunicationEmail();
					supplier.getSupplier().getCompanyContactNumber();
					supplier.getSupplier().getCompanyName();
					supplier.getSupplier().getStatus();
				}
			}
			if (event.getEventOwner().getBuyer() != null) {
				Buyer buyer = event.getEventOwner().getBuyer();
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
			if (event.getDeliveryAddress() != null) {
				event.getDeliveryAddress().getTitle();
				event.getDeliveryAddress().getLine1();
				event.getDeliveryAddress().getLine2();
				event.getDeliveryAddress().getCity();
				event.getDeliveryAddress().getState().getStateName();
				event.getDeliveryAddress().getState().getCountry().getCountryName();
			}
			if (CollectionUtil.isNotEmpty(event.getRftReminder())) {
				for (RftReminder reminder : event.getRftReminder()) {
					reminder.getReminderDate();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RftEventMeeting meeting : event.getMeetings()) {
					meeting.getAppointmentDateTime();
					meeting.getAppointmentTime();
					meeting.getInviteSuppliers();
					meeting.getTitle();
					meeting.getRfxEventMeetingContacts();
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RftEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
							meetingContact.getContactName();
							meetingContact.getContactNumber();
							meetingContact.getContactEmail();
							meetingContact.getId();

						}
					}
					meeting.getStatus();
					meeting.getVenue();

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
						for (RftEventMeetingDocument meetingDocument : meeting.getRfxEventMeetingDocument()) {
							meetingDocument.getId();
							meetingDocument.getFileSizeInKb();
							meetingDocument.getCredContentType();
							meetingDocument.getFileName();
						}
					}

					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier suppliers : meeting.getInviteSuppliers()) {
							suppliers.getCompanyName();
							suppliers.getCommunicationEmail();
							suppliers.getCompanyContactNumber();
						}
					}
					meeting.getRemarks();
				}
			}
			// if (CollectionUtil.isNotEmpty(event.getDocuments())) {
			// for (RftEventDocument item : event.getDocuments()) {
			// item.getDescription();
			// item.getFileName();
			// item.getUploadDate();
			// item.getFileData();
			// }
			// }
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				for (RftComment comment : event.getComment()) {
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
			if (event.getEventOwner() != null) {
				event.getEventOwner().getName();
				event.getEventOwner().getCommunicationEmail();
				event.getEventOwner().getPhoneNumber();
				if (event.getEventOwner().getOwner() != null) {
					Owner usr = event.getEventOwner().getOwner();
					usr.getCompanyContactNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				for (IndustryCategory indCat : event.getIndustryCategories()) {
					indCat.getCode();
				}
			}
		}
		if (event.getBusinessUnit() != null) {
			event.getBusinessUnit().getUnitName();
		}
		return event;
	}

	@Override
	@Transactional(readOnly = false)
	public List<RftTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType teamMemberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " eventId: " + eventId + " userId: " + userId + " TeamMember_Type: " + teamMemberType);
		RftEvent rftEvent = getRftEventByeventId(eventId);
		LOG.info("rftEvent *****:" + rftEvent);
		List<RftTeamMember> teamMembers = rftEvent.getTeamMembers();
		if (teamMembers == null) {
			teamMembers = new ArrayList<RftTeamMember>();
		}
		LOG.info("teamMembers : *******" + teamMembers);
		RftTeamMember rftTeamMember = new RftTeamMember();
		rftTeamMember.setEvent(rftEvent);
		User user = userService.findTeamUserById(userId);
		rftTeamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (RftTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(user.getId())) {
				rftTeamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;
			}
		}
		rftTeamMember.setTeamMemberType(teamMemberType);
		if (!exists) {
			teamMembers.add(rftTeamMember);
		}

		rftEvent.setTeamMembers(teamMembers);
		rftEventDao.update(rftEvent);

		try {
			if (!exists) {
				RftEventAudit audit = new RftEventAudit(null, rftEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.added", new Object[] { user.getName(), teamMemberType.getValue() }, Global.LOCALE), null);
				eventAuditService.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been added as '" + teamMemberType.getValue() + "' for event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				LOG.info("************* RFT Team Member event audit added successfully *************");
			} else {
				if (!previousMemberType.equalsIgnoreCase(teamMemberType.getValue())) {
					RftEventAudit audit = new RftEventAudit(null, rftEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.changed", new Object[] { user.getName(), previousMemberType, teamMemberType.getValue() }, Global.LOCALE), null);
					eventAuditService.save(audit);
					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been changed from '" + previousMemberType + " to '" + teamMemberType.getValue() + "' for Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
					LOG.info("************* RFT Team Member event audit changed successfully *************");
				}
			}
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}
		return teamMembers;
	}

	public IndustryCategory getIndustryCategoryForRftById(String id) {
		return industryCategoryDao.getIndustryCategoryForRftById(id);
	}

	@Override
	public List<RftEventContact> getAllContactForEvent(String eventId) {
		return rftEventContactDao.findAllEventContactById(eventId);

	}

	@Override
	public List<RftEventCorrespondenceAddress> getAllCAddressForEvent(String eventId) {
		List<RftEventCorrespondenceAddress> list = rftEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftEventCorrespondenceAddress address : list) {
				LOG.info("State : " + address.getState().getStateName());
				if (address != null && address.getState() != null && address.getState().getCountry() != null) {
					address.getState().getCountry().getCountryName();
				}
			}
		}
		return list;
	}

	@Override
	public List<RftEventCAddressPojo> getAllCorrespondenceAddressPojo(String eventId) {
		List<RftEventCAddressPojo> returnList = new ArrayList<RftEventCAddressPojo>();

		List<RftEventCorrespondenceAddress> list = rftEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftEventCorrespondenceAddress rftEventCorrespondenceAddress : list) {
				if (rftEventCorrespondenceAddress.getState() != null)
					rftEventCorrespondenceAddress.getState().getCountry();
				if (rftEventCorrespondenceAddress.getState() != null)
					rftEventCorrespondenceAddress.getState().getStateName();

				RftEventCAddressPojo rep = new RftEventCAddressPojo(rftEventCorrespondenceAddress);
				rep.setCountry(rftEventCorrespondenceAddress.getState().getCountry().getCountryName());
				returnList.add(rep);
			}
		}

		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent loadRftEventById(String id) {
		RftEvent event = rftEventDao.findByEventId(id);
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
				for (RftEventContact contact : event.getEventContacts()) {
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

			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RftTeamMember teamMember : event.getTeamMembers()) {
					teamMember.getUser().getName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RftEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RftApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RftEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RftSuspensionApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAdditionalDocument())) {
				for (AdditionalDocument additionalDocument : event.getAdditionalDocument()) {
					additionalDocument.getId();
					additionalDocument.getFileSizeInKb();
					additionalDocument.getCredContentType();
					additionalDocument.getFileName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				for (IndustryCategory indCat : event.getIndustryCategories()) {
					indCat.getCode();
				}
			}
			if (event.getEvaluationProcessDeclaration() != null) {
				event.getEvaluationProcessDeclaration().getContent();
			}

			if (event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getDescription();
			}

			if (event.getTemplate() != null) {
				event.getTemplate().getReadOnlySuspendApproval();
				event.getTemplate().getOptionalSuspendApproval();
				event.getTemplate().getVisibleSuspendApproval();
			}
		}
		return event;
	}

	@Override
	public boolean isExists(RftEvent rftEvent) {
		return rftEventDao.isExists(rftEvent);
	}

	@Override
	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId) {
		return favoriteSupplierDao.searchFavouriteSupplierByCompanyNameOrRegistrationNo(searchParam, buyerId, null);
	}

	@Override
	public List<DraftEventPojo> getAllDraftEventForBuyer(String tenantId, TableDataInput input, String userId) {
		return rftEventDao.getAllDraftEventsForBuyer(tenantId, input, userId);
	}

	@Override
	public List<DraftEventPojo> getAllSuspendedEventsForBuyer(String tenantId, TableDataInput input, String userId) {
		return rftEventDao.getAllSuspendedEventsForBuyer(tenantId, input, userId);
	}

	@Override
	public List<DraftEventPojo> getAllCancelledEventForBuyer(String tenantId, TableDataInput input, String userId) {
		return rftEventDao.getAllCancelledEventsForBuyer(tenantId, input, userId);
	}

	@Override
	public List<DraftEventPojo> getAllClosedEventForBuyer(String tenantId, TableDataInput input, String userId) {
		return rftEventDao.getAllClosedEventsForBuyer(tenantId, input, userId);
	}

	@Override
	public List<DraftEventPojo> getAllCompletedEventsForBuyer(String tenantId, TableDataInput input, String userId) {
		return rftEventDao.getAllCompletedEventsForBuyer(tenantId, input, userId);
	}

	@Override
	public List<OngoingEventPojo> getAllOngoingEventsForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId) {
		List<OngoingEventPojo> list = rftEventDao.getAllOngoingEventsForBuyer(tenantId, lastLoginTime, input, userId);
		return list;
	}

	@Override
	public List<PublicEventPojo> getActivePublicEvents(Country country, Buyer buyer) {
		return rftEventDao.getActivePublicEvents(country, buyer);
	}

	@Override
	public List<PublicEventPojo> getActivePublicEventsForIntegration(Country country, Buyer buyer) {
		List<PublicEventPojo> events = rftEventDao.getActivePublicEvents(country, buyer);
		for (PublicEventPojo event : events) {
			event.getCountry().setCreatedBy(null);
			event.getCountry().setCreatedDate(null);
			event.getCountry().setModifiedBy(null);
			event.getIndustryCategory().setCreatedBy(null);
			event.getIndustryCategory().setCreatedDate(null);
			event.getEventOwner().setTenantId(null);
			event.getEventOwner().setTenantType(null);
			event.getEventOwner().setLoginIpAddress(null);
			event.getEventOwner().setCreatedDate(null);
			event.getEventOwner().setLastLoginTime(null);
			event.getEventOwner().setLastFailedLoginTime(null);
			event.getEventOwner().setModifiedDate(null);
			event.getEventOwner().setLastPasswordChangedDate(null);
			event.getBuyer().setPassword(null);
			event.getBuyer().setLoginEmail(null);
			event.getBuyer().setCompanyRegistrationNumber(null);
			event.getBuyer().setSubscriptionDate(null);
			event.getBuyer().setNoOfEvents(null);
			event.getBuyer().setNoOfUsers(null);
			event.getBuyer().setSubscriptionFrom(null);
			event.getBuyer().setSubscriptionTo(null);
			event.getBuyer().setMobileNumber(null);
			event.getBuyer().setFaxNumber(null);
			event.getBuyer().setRegistrationCompleteDate(null);
			event.getBuyer().setTermsOfUseAcceptedDate(null);
			event.getBuyer().setCompanyContactNumber(null);
			event.getBuyer().setState(null);
		}
		return events;
	}

	@Override
	public List<Buyer> getAllActivePublicEventBuyers() {
		return rftEventDao.getAllActivePublicEventBuyers();
	}

	@Override
	public List<Buyer> getAllActiveBuyersForIntegration() {
		List<Buyer> list = rftEventDao.getAllActivePublicEventBuyers();
		if (CollectionUtil.isNotEmpty(list)) {
			for (Buyer buyer : list) {
				buyer.setActionBy(null);
				buyer.setActionDate(null);
				buyer.setBuyerPackage(null);
				buyer.setCity(null);
				buyer.setCommunicationEmail(null);
				buyer.setCompanyContactNumber(null);
				buyer.setCompanyRegistrationNumber(null);
				buyer.setCreatedDate(null);
				buyer.setCurrentSubscription(null);
				buyer.setFavouriteSuppliers(null);
				buyer.setSubscriptionHistory(null);
				buyer.setRegistrationOfCountry(null);
				buyer.setState(null);
				buyer.setLoginEmail(null);
			}
		}
		return list;
	}

	@Override
	public List<FavouriteSupplier> getFavouriteSupplierBySupplierId(List<Supplier> suppliers) {
		return favoriteSupplierDao.findAllFavouriteSuppliersForSuppliers(suppliers);
	}

	@Override
	public List<RfxView> getAllEventForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findAllEventForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public List<FinishedEventPojo> getAllFinishedEventsForBuyer(String tenantId, Date lastLoginTime, int days, TableDataInput input, String userId) {
		List<FinishedEventPojo> list = rftEventDao.getAllFinishedEventsForBuyer(tenantId, lastLoginTime, days, input, userId);
		return list;
	}

	@Override
	public RftEvent loadRftEventForSummeryById(String id) {
		RftEvent event = rftEventDao.findBySupplierEventId(id);
		if (event != null) {

			if (event.getTemplate() != null) {
				event.getTemplate().getTemplateName();
			}
			if (event.getParticipationFeeCurrency() != null) {
				event.getParticipationFeeCurrency().getCurrencyCode();
			}
			if (event.getDepositCurrency() != null) {
				event.getDepositCurrency().getCurrencyCode();
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
			// User unMaskedUser = rftEventDao.getUnMaskedUserNameAndMailByEventId(event.getId());
			// if (unMaskedUser != null) {
			// event.setUnMaskedUser(unMaskedUser);
			// }

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RftEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RftEventSupplier supplier : event.getSuppliers()) {
					LOG.info("Company Name : " + supplier.getSupplier().getCompanyName());
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

			// Touch Event Reminders...
			if (CollectionUtils.isNotEmpty(event.getRftReminder())) {
				for (RftReminder reminder : event.getRftReminder()) {
					reminder.getInterval();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RftTeamMember teamMembers : event.getTeamMembers()) {
					teamMembers.getTeamMemberType();
					teamMembers.getUser().getName();
				}
			}
			// if (CollectionUtil.isNotEmpty(event.getDocuments())) {
			// for (RftEventDocument document : event.getDocuments()) {
			// document.getDescription();
			// }
			// }
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RftEventMeeting meeting : event.getMeetings()) {
					meeting.getRemarks();
					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier supplier : meeting.getInviteSuppliers()) {
							supplier.getCompanyName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RftEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
							contact.getContactName();
						}
					}

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
						for (RftEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
							reminder.getReminderDate();
						}
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

			if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
				for (RftEventBq bq : event.getEventBqs()) {
					if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
						for (RftBqItem item : bq.getBqItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				for (RftComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RftEventSupplier eventSupplier : event.getSuppliers()) {
					if (eventSupplier.getSupplier() != null) {
						eventSupplier.getSupplier().getCompanyName();
					}
					if (CollectionUtil.isNotEmpty(eventSupplier.getTeamMembers())) {
						for (RftSupplierTeamMember supplierTeamMember : eventSupplier.getTeamMembers())
							supplierTeamMember.getUser().getName();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RftEventApproval approvalLevel : event.getApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RftApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getAdditionalDocument())) {
				for (AdditionalDocument additionalDocument : event.getAdditionalDocument()) {
					additionalDocument.getId();
					additionalDocument.getFileSizeInKb();
					additionalDocument.getCredContentType();
					additionalDocument.getFileName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getAwardedSuppliers())) {
				for (Supplier supp : event.getAwardedSuppliers()) {
					supp.getCompanyName();
				}
			}

			if (event.getConcludeBy() != null) {
				event.getConcludeBy().getName();
			}

			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RftUnMaskedUser unmaskuser : event.getUnMaskedUsers()) {
					unmaskuser.getUser().getCommunicationEmail();
					unmaskuser.getUser().getName();
					unmaskuser.getUser().getPhoneNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RftEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getCommunicationEmail();
					user.getUser().getName();
					user.getUser().getPhoneNumber();
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
			if (event.getGroupCode() != null) {
				event.getGroupCode().getGroupCode();
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RftEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RftSuspensionApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}
		}
		return event;
	}

	@Override
	public Integer getCountOfEnvelopByEventId(String eventId) {
		return rftEventDao.getCountOfEnvelopByEventId(eventId);
	}

	@Override
	public Integer getCountOfRftDocumentByEventId(String eventId) {
		return rftDocumentDao.getCountOfRftDocumentByEventId(eventId);
	}

	public Integer getCountOfRftCqByEventId(String eventId) {
		return rftCqDao.getCountOfRftCqByEventId(eventId);
	}

	@Override
	public List<DraftEventPojo> getAllRftEventByTenantId(String tenantId, String loggedInUser, String pageNo, String serchVal, String indCat) throws SubscriptionException {
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

		List<DraftEventPojo> list = rftEventDao.getAllRftEventByTenantId(tenantId, loggedInUser, pageNo, serchVal, indCat);
		return list;

		// return rftEventDao.getAllRftEventByTenantId(tenantId, loggedInUser);
	}

	@Override
	public List<RftEvent> getAllRftEventbyTenantidInitial(String tenantId, String loggedInUser) throws SubscriptionException {
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

		List<RftEvent> list = rftEventDao.getAllRftEventbyTenantidInitial(tenantId, loggedInUser);
		for (RftEvent rftEvent : list) {
			if (rftEvent.getTemplate() != null) {
				if (rftEvent.getTemplate().getStatus() == Status.INACTIVE) {
					rftEvent.setTemplateActive(true);
				} else {
					rftEvent.setTemplateActive(false);
				}
			}

			if (CollectionUtil.isNotEmpty(rftEvent.getIndustryCategories()))
				rftEvent.setIndustryCategory(rftEvent.getIndustryCategories().get(0));
		}
		return list;

	}

	@Override
	public List<Event> findByEventNameaAndRefNumAndIndCatForTenant(String searchValue, String industryCategory, String tenantId, String eventType, String loggedInUser) {
		List<Event> eventList = new ArrayList<Event>();
		// LOG.info("rfx "+RfxTypes.valueOf(eventType));
		LOG.info("event Type : " + eventType);
		switch (RfxTypes.valueOf(eventType)) {
		case RFA:
			List<RfaEvent> rfaList = rfaEventDao.findByEventNameaAndRefNumAndIndCatForTenant(searchValue, industryCategory, tenantId, loggedInUser);
			for (RfaEvent rfaEvent : rfaList) {
				if (rfaEvent.getTemplate() != null) {
					if (rfaEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rfaEvent.setTemplateActive(false);
					} else {
						rfaEvent.setTemplateActive(true);
					}
				}
				if (CollectionUtil.isNotEmpty(rfaEvent.getIndustryCategories())) {
					rfaEvent.setIndustryCategory(rfaEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rfaList);
			break;
		case RFI:
			List<RfiEvent> rfiList = rfiEventDao.findByEventNameaAndRefNumAndIndCatForTenant(searchValue, industryCategory, tenantId, loggedInUser);

			for (RfiEvent rfiEvent : rfiList) {
				if (rfiEvent.getTemplate() != null) {
					if (rfiEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rfiEvent.setTemplateActive(false);
					} else {
						rfiEvent.setTemplateActive(true);
					}

				}
				if (CollectionUtil.isNotEmpty(rfiEvent.getIndustryCategories())) {
					rfiEvent.setIndustryCategory(rfiEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rfiList);
			break;
		case RFP:
			List<RfpEvent> rfpList = rfpEventDao.findByEventNameaAndRefNumAndIndCatForTenant(searchValue, industryCategory, tenantId, loggedInUser);

			for (RfpEvent rfpEvent : rfpList) {
				if (rfpEvent.getTemplate() != null) {
					if (rfpEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rfpEvent.setTemplateActive(false);
					} else {
						rfpEvent.setTemplateActive(true);
					}
				}
				if (CollectionUtil.isNotEmpty(rfpEvent.getIndustryCategories())) {
					rfpEvent.setIndustryCategory(rfpEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rfpList);
			break;
		case RFQ:
			List<RfqEvent> rfqList = rfqEventDao.findByEventNameaAndRefNumAndIndCatForTenant(searchValue, industryCategory, tenantId, loggedInUser);
			for (RfqEvent rfqEvent : rfqList) {
				if (rfqEvent.getTemplate() != null) {
					if (rfqEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rfqEvent.setTemplateActive(false);
					} else {
						rfqEvent.setTemplateActive(true);
					}
				}
				if (CollectionUtil.isNotEmpty(rfqEvent.getIndustryCategories())) {
					rfqEvent.setIndustryCategory(rfqEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rfqList);
			break;
		case RFT:
			List<RftEvent> rftList = rftEventDao.findByEventNameaAndRefNumAndIndCatForTenant(searchValue, industryCategory, tenantId, loggedInUser);
			for (RftEvent rftEvent : rftList) {
				if (rftEvent.getTemplate() != null) {
					if (rftEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rftEvent.setTemplateActive(false);
					} else {
						rftEvent.setTemplateActive(true);
					}
				}
				if (CollectionUtil.isNotEmpty(rftEvent.getIndustryCategories())) {
					rftEvent.setIndustryCategory(rftEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rftList);
			break;
		default:
			break;

		}
		return eventList;
	}

	private List<Event> convertToEventList(List<?> list) {
		List<Event> eventList = new ArrayList<Event>();
		for (Object e : list) {
			Event event = ((Event) e).createShallowCopy();
			event.setId(((Event) e).getId());
			if (event.getIndustryCategory() != null) {
				event.getIndustryCategory().setCreatedBy(null);
				event.getIndustryCategory().setModifiedBy(null);
			}
			eventList.add(event);
		}
		return eventList;
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent copyFrom(String eventId, User createdBy, BusinessUnit buyerbusinessUnit) throws SubscriptionException, ApplicationException {
		RftEvent rftEvent = this.getRftEventByeventId(eventId);

		if (rftEvent.getGroupCode() != null && Status.INACTIVE == rftEvent.getGroupCode().getStatus()) {
			LOG.error("The group code " + rftEvent.getGroupCode().getGroupCode() + " used in Previous Event is inactive");
			throw new ApplicationException("The group code '" + rftEvent.getGroupCode().getGroupCode() + "' used in Event is inactive");
		}

		if (rftEvent.getDeliveryAddress() != null && rftEvent.getDeliveryAddress().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Delivery address found ....");
			throw new ApplicationException("Delivery address is Inactive for event:" + rftEvent.getEventId());
		} else {
			LOG.info("active Delivery address found ........");
		}
		if (rftEvent.getTemplate() != null && Status.INACTIVE == rftEvent.getTemplate().getStatus()) {
			LOG.info("inactive Template found for Id .... " + rftEvent.getTemplate().getId());
			throw new ApplicationException("Template [" + rftEvent.getTemplate().getTemplateName() + "] used by the event [" + rftEvent.getEventId() + "] is Inactive");
		}

		RftEvent newEvent = rftEvent.copyFrom(rftEvent);

		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());
		newEvent.setEnableAwardApproval(rftEvent.getEnableAwardApproval());
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFT")) {
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

		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFT", newEvent.getBusinessUnit()));

		newEvent.setStatus(EventStatus.DRAFT);

		// Save teamMember
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RftTeamMember> tm = new ArrayList<RftTeamMember>();
			for (RftTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
			}
		}

		if (CollectionUtil.isNotEmpty(newEvent.getUnMaskedUsers())) {
			List<RftUnMaskedUser> tm = new ArrayList<RftUnMaskedUser>();
			for (RftUnMaskedUser unmaskUser : newEvent.getUnMaskedUsers()) {
				unmaskUser.setEvent(newEvent);
				tm.add(unmaskUser);
			}
		}

		// save Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getApprovals())) {
			for (RftEventApproval app : newEvent.getApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RftApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);

					}
				}
			}
		}

		// save Doc
		if(CollectionUtil.isNotEmpty(newEvent.getDocuments())){
			for(RftEventDocument rftDocument: newEvent.getDocuments()){
				rftDocument.copyFrom(newEvent);
				LOG.info("Saving document...");
				rftDocumentService.saveRftDocuments(rftDocument);
			}
		}

		// save Suspension Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getSuspensionApprovals())) {
			for (RftEventSuspensionApproval app : newEvent.getSuspensionApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RftSuspensionApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);

					}
				}
			}
		}

		// save Award Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getAwardApprovals())) {
			for (RftEventAwardApproval app : newEvent.getAwardApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RftAwardApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);
					}
				}
			}
		}

		List<RftEnvelop> envelops = newEvent.getRftEnvelop();
		newEvent.setRftEnvelop(null);
		RftEvent newDbEvent = this.saveRftEvent(newEvent, createdBy);

		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RftTeamMember> tm = new ArrayList<RftTeamMember>();
			for (RftTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
				rfaEventService.sendTeamMemberEmailNotificationEmail(teamMember.getUser(), teamMember.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFT, newDbEvent.getId());
			}
		}
		// Save contacts
		if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
			for (RftEventContact contact : newEvent.getEventContacts()) {
				contact.setRfxEvent(newDbEvent);
				saveRftEventContact(contact);
			}
		}

		// save suppliers
		if (CollectionUtil.isNotEmpty(newEvent.getSuppliers())) {
			for (RftEventSupplier supp : newEvent.getSuppliers()) {
				supp.setRfxEvent(newDbEvent);
				rftEventSupplierDao.save(supp);
			}
		}

		List<RftEventBq> eventBq = new ArrayList<RftEventBq>();

		// save BQ
		if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {
			for (RftEventBq bq : newEvent.getEventBqs()) {
				bq.setRfxEvent(newDbEvent);
				RftEventBq dbBq = rftEventBqDao.saveOrUpdate(bq);
				eventBq.add(dbBq);
				// save BQ Items
				if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
					RftBqItem parent = null;
					for (RftBqItem item : bq.getBqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setBq(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rftBqItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
			}

		}

		// save SOR
		List<RftEventSor> eventSor = new ArrayList<RftEventSor>();
		if (CollectionUtil.isNotEmpty(newEvent.getEventSors())) {
			for (RftEventSor bq : newEvent.getEventSors()) {
				bq.setRfxEvent(newDbEvent);
				RftEventSor dbBq = rftEventSorDao.saveOrUpdate(bq);
				eventSor.add(dbBq);
				if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
					RftSorItem parent = null;
					for (RftSorItem item : bq.getSorItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setSor(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rftSorItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
			}
		}


		List<RftCq> eventCq = new ArrayList<RftCq>();
		// save CQ
		if (CollectionUtil.isNotEmpty(newEvent.getCqs())) {
			for (RftCq cq : newEvent.getCqs()) {
				cq.setRfxEvent(newDbEvent);
				RftCqItem parent = null;
				// save CQ Items
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RftCqItem item : cq.getCqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setRfxEvent(newDbEvent);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
				RftCq dbCq = rftCqDao.saveOrUpdate(cq);
				eventCq.add(dbCq);
			}
		}

		// save envelop
		if (CollectionUtil.isNotEmpty(envelops)) {
			for (RftEnvelop envelop : envelops) {
				envelop.setRfxEvent(newDbEvent);
				List<RftEventBq> bqsOfEnvelop = new ArrayList<RftEventBq>();

				List<RftEventBq> envelopBqs = new ArrayList<RftEventBq>();
				if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
					for (RftEventBq bq : envelop.getBqList()) {
						envelopBqs.add(bq);
					}
				}

				for (RftEventBq evntbq : eventBq) {
					for (RftEventBq envbq : envelopBqs) {
						if (evntbq.getName().equals(envbq.getName())) {
							bqsOfEnvelop.add(evntbq);
							break;
						}
					}
				}

				List<RftEventSor> sorsOfEnvelop = new ArrayList<RftEventSor>();

				List<RftEventSor> envelopSors = new ArrayList<RftEventSor>();
				if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
					for (RftEventSor sor : envelop.getSorList()) {
						envelopSors.add(sor);
					}
				}

				for (RftEventSor evntsor : eventSor) {
					for (RftEventSor envsor : envelopSors) {
						if (evntsor.getName().equals(envsor.getName())) {
							sorsOfEnvelop.add(evntsor);
							break;
						}
					}
				}

				List<RftCq> cqsOfEnvelop = new ArrayList<RftCq>();
				List<RftCq> envelopCqs = new ArrayList<RftCq>();
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RftCq bq : envelop.getCqList()) {
						envelopCqs.add(bq);
					}
				}

				for (RftCq evntCq : eventCq) {
					for (RftCq envcq : envelopCqs) {
						if (evntCq.getName().equals(envcq.getName())) {
							cqsOfEnvelop.add(evntCq);
							break;
						}
					}
				}

				List<RftEvaluatorUser> evalUser = new ArrayList<RftEvaluatorUser>();
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RftEvaluatorUser eval : envelop.getEvaluators()) {
						LOG.info("EV USER : " + eval.getUser().getId() + "  Active " + eval.getUser().isActive() + " envelop : " + envelop.getEnvelopTitle());
						if (eval.getUser().isActive()) {
							eval.setEnvelop(envelop);
							evalUser.add(eval);
						}
					}
				}

				List<RftEnvelopeOpenerUser> openerUserList = new ArrayList<RftEnvelopeOpenerUser>();
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RftEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
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
				envelop = rftEnvelopDao.save(envelop);

			}
		}

		return newDbEvent;
	}

	@Override
	public boolean isExists(RftSupplierMeetingAttendance rftSupplierMeetingAttendance) {
		return rftSupplierMeetingAttendanceDao.isExists(rftSupplierMeetingAttendance);

	}

	@Override
	public RftSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId) {
		return rftSupplierMeetingAttendanceDao.attendenceByEventId(meetingId, eventId, tenantId);

	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rftEventDao.getEventSuppliers(eventId);
	}

	@Override
	public List<Supplier> getAwardedSuppliers(String eventId) {
		return rftEventDao.getAwardedSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit buyerbusinessUnit) throws SubscriptionException, ApplicationException {
		RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(templateId);
		LOG.info("Event is creating from template name " + rfxTemplate.getTemplateName());
		if (rfxTemplate != null && Status.INACTIVE == rfxTemplate.getStatus()) {
			LOG.info("inactive Template [" + rfxTemplate.getTemplateName() + "] found for Id .... " + rfxTemplate.getId());
			throw new ApplicationException("Template [" + rfxTemplate.getTemplateName() + "] is Inactive");
		}

		RftEvent newEvent = new RftEvent();
		newEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newEvent.setAddSupplier(rfxTemplate.getAddSupplier());
		newEvent.setTemplate(rfxTemplate);
		newEvent.setCreatedBy(createdBy);
		newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		newEvent.setCreatedDate(new Date());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());
		newEvent.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
		boolean isUserControl = Boolean.TRUE.equals(createdBy.getBuyer().getEnableEventUserControle());
		newEvent.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newEvent.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newEvent.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		newEvent.setEnableSuspensionApproval(rfxTemplate.getEnableSuspendApproval());
		newEvent.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());
		newEvent.setEnableAwardApproval(rfxTemplate.getEnableAwardApproval());

		// copy unmasking User

		List<RftUnMaskedUser> unmaskingUser = new ArrayList<RftUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			LOG.info("Copying unMasking Users from Template : " + rfxTemplate.getUnMaskedUsers().size());
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RftUnMaskedUser newRftUnMaskedUser = new RftUnMaskedUser();
				newRftUnMaskedUser.setUser(team.getUser());
				newRftUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRftUnMaskedUser);
			}
			newEvent.setUnMaskedUsers(unmaskingUser);
			LOG.info("Added unMask user to Event  : " + (newEvent.getUnMaskedUsers() != null ? newEvent.getUnMaskedUsers().size() : 0));
		}

		// Copy Evaluation Conclusion Users from template
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RftEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RftEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RftEvaluationConclusionUser conclusionUser = new RftEvaluationConclusionUser();
				conclusionUser.setUser(user);
				conclusionUser.setEvent(newEvent);
				evaluationConclusionUsers.add(conclusionUser);
			}
			newEvent.setEvaluationConclusionUsers(evaluationConclusionUsers);
			LOG.info("Added Evaluation Conclusion user to Event  : " + (newEvent.getEvaluationConclusionUsers() != null ? newEvent.getEvaluationConclusionUsers().size() : 0));
		}

		/**
		 * commmented for event publish type CR PH-352
		 */
		// newEvent.setEventVisibility(EventVisibilityType.PRIVATE);

		newEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
		// copy approval from template
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RftEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RftEventApproval newRfApproval = new RftEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RftApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						if (isUserControl && templateApprovalUser.getUser() != null && templateApprovalUser.getUser().getId().equals(createdBy.getId())) {
							throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Event Approval User" }, Global.LOCALE));
						}
						RftApprovalUser approvalUser = new RftApprovalUser();
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
			List<RftEventSuspensionApproval> approvalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Suspension Approval Level :" + templateApproval.getLevel());
				RftEventSuspensionApproval newRfApproval = new RftEventSuspensionApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RftSuspensionApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RftSuspensionApprovalUser approvalUser = new RftSuspensionApprovalUser();
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
			List<RftEventAwardApproval> approvalList = new ArrayList<>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RftEventAwardApproval newRfApproval = new RftEventAwardApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RftAwardApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RftAwardApprovalUser approvalUser = new RftAwardApprovalUser();
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
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFT")) {
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

		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFT", newEvent.getBusinessUnit()));
		List<RftTeamMember> teamMembers = new ArrayList<RftTeamMember>();
		newEvent = this.saveRftEvent(newEvent, createdBy);
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

				RftEnvelop rftEnvlop = new RftEnvelop();
				rftEnvlop.setEnvelopTitle(en.getRfxEnvelope());
				rftEnvlop.setEnvelopSequence(en.getRfxSequence());
				rftEnvlop.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rftEnvlop.setRfxEvent(newEvent);
				rftEnvlop.setEnvelopType(EnvelopType.CLOSED);
				envelopService.saveRftEnvelop(rftEnvlop, null, null, null);
			}
		}

		setDefaultEventContactDetail(createdBy.getId(), newEvent.getId());
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {

				if (isUserControl && team.getUser() != null && team.getUser().getId().equals(createdBy.getId())) {
					throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Team Member User" }, Global.LOCALE));
				}

				RftTeamMember newTeamMembers = new RftTeamMember();
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
	public RftEventContact getEventContactById(String id) {
		return rftEventContactDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRftContact(RftEventContact eventContact) {
		rftEventContactDao.delete(eventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveEventReminder(RftReminder rftReminder) {
		rftReminderDao.saveOrUpdate(rftReminder);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventReminder(RftReminder rftReminder) {
		LOG.info("Update : " + rftReminder.getReminderDate());
		LOG.info("TOstring : " + rftReminder.toString());
		rftReminderDao.update(rftReminder);
	}

	@Override
	public List<RftReminder> getAllRftEventReminderForEvent(String eventId) {
		return rftReminderDao.getAllRftEventReminderForEvent(eventId);

	}

	@Override
	public RftReminder getEventReminderById(String id) {
		return rftReminderDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRftReminder(RftReminder rftReminder) {
		rftReminderDao.delete(rftReminder);
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeTeamMemberfromList(String eventId, String userId, RftTeamMember dbTeamMember) {
		RftEvent rftEvent = getRftEventByeventId(eventId);
		LOG.info("rftEvent**************" + rftEvent);
		List<RftTeamMember> teamMembers = rftEvent.getTeamMembers();
		LOG.info("**************" + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<>();
		}
		User user = userService.getUsersById(userId);

		LOG.info("*****dbTeamMember********" + dbTeamMember);
		LOG.info("**************" + user);
		teamMembers.remove(dbTeamMember);
		dbTeamMember.setEvent(null);
		rftEvent.setTeamMembers(teamMembers);
		rftEventDao.update(rftEvent);
		LOG.info("TeamMember removed." + "	" + teamMembers.size() + " teamMembers: " + teamMembers);

		try {
			RftEventAudit audit = new RftEventAudit(null, rftEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.removed", new Object[] { user.getName(), dbTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), null);
			eventAuditService.save(audit);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + dbTeamMember.getUser().getName() + "' has been removed from '" + dbTeamMember.getTeamMemberType().getValue() + "' for Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			LOG.info("************* RFT Team Member removed successfully *************");
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}
		List<User> userList = new ArrayList<User>();
		try {
			LOG.info("TeamMember getTeamMembers(): " + rftEvent.getTeamMembers());

			for (RftTeamMember rftApp : rftEvent.getTeamMembers()) {
				userList.add((User) rftApp.getUser().clone());
			}
			LOG.info(userList.size() + " Event ID :" + eventId);
		} catch (Exception e) {
			LOG.error("Error constructing list of users after remove operation : " + e.getMessage(), e);
		}
		return userList;

	}

	@Override
	public RftTeamMember getRftTeamMemberByUserIdAndEventId(String eventId, String userId) {
		return rftEventDao.getRftTeamMemberByUserIdAndEventId(eventId, userId);
	}

	@Override
	public boolean isExists(RftEventContact rftEventContact) {
		return rftEventContactDao.isExists(rftEventContact);
	}

	@Override
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		return rftEventDao.getTeamMembersForEvent(eventId);
	}

	@Override
	public long findTotalDraftEventForBuyer(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalDraftEventForBuyer(tenantId, userId, input);
	}

	@Override
	public long findTotalSuspendedEventForBuyer(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalSuspendedEventForBuyer(tenantId, userId, input);
	}

	@Override
	public long findTotalCancelledEventForBuyer(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalCancelledEventForBuyer(tenantId, userId, input);
	}

	@Override
	public long findTotalCompletedEventForBuyer(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalCompletedEventForBuyer(tenantId, userId, input);
	}

	@Override
	public long findTotalClosedEventForBuyer(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalClosedEventForBuyer(tenantId, userId, input);
	}

	@Override
	public long findTotalOngoingEventForBuyer(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalOngoingEventForBuyer(tenantId, userId, input);
	}

	@Override
	public long findTotalFinishedEventForBuyer(String tenantId, String userId, int days, TableDataInput input) {
		return rftEventDao.findTotalFinishedEventForBuyer(tenantId, userId, days, input);
	}

	@Override
	public List<RftEventApproval> getApprovalsForEvent(String id) {
		List<RftEventApproval> approvalsList = rftEventDao.getApprovalsForEvent(id);
		return approvalsList;
	}

	@Override
	public List<ActiveEventPojo> getAllActiveEventForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId) {
		List<ActiveEventPojo> list = rftEventDao.getAllActiveEventsForBuyer(tenantId, lastLoginTime, input, userId);
		return list;
	}

	@Override
	public long findTotalActiveEventForBuyer(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalActiveEventForBuyer(tenantId, userId, input);
	}

	@Override
	public List<PendingEventPojo> getAllPendingEventForBuyer(String tenantId, Date lastLoginTime, TableDataInput input, String userId) {
		List<PendingEventPojo> list = rftEventDao.getAllPendingEventsForBuyer(tenantId, lastLoginTime, input, userId);
		return list;
	}

	@Override
	public long findTotalPendingEventForBuyer(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalPendingEventForBuyer(tenantId, userId, input);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent updateEventApproval(RftEvent event, User loggedInUser) {

		RftEvent persistObj = rftEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RftEventApproval approvalRequest : persistObj.getApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RftApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}
		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			int level = 1;
			for (RftEventApproval app : event.getApprovals()) {
				app.setEvent(event);
				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RftApprovalUser approvalUser : app.getApprovalUsers()) {
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
		persistObj = rftEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RftEventAudit audit = new RftEventAudit(persistObj, loggedInUser, new Date(), AuditActionType.Update, auditMessage);
				rftEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" + persistObj.getEventId() + "' ." + auditMessage, loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
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
	public void insertTimeLine(String eventId) throws ApplicationException {
		try {

			rftEventTimeLineDao.deleteTimeline(eventId);

			RftEvent rftEvent = rftEventDao.findById(eventId);
			// Publish Date
			RftEventTimeLine timeline = new RftEventTimeLine();
			timeline.setActivityDate(rftEvent.getEventPublishDate());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rftEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.publish.date", new Object[] { rftEvent.getEventName() }, Global.LOCALE));
			rftEventTimeLineDao.save(timeline);

			// Event Start
			timeline = new RftEventTimeLine();
			timeline.setActivityDate(rftEvent.getEventStart());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rftEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.start.date", new Object[] { rftEvent.getEventName() }, Global.LOCALE));
			rftEventTimeLineDao.save(timeline);

			// End Date
			timeline = new RftEventTimeLine();
			timeline.setActivityDate(rftEvent.getEventEnd());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rftEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.end.date", new Object[] { rftEvent.getEventName() }, Global.LOCALE));
			rftEventTimeLineDao.save(timeline);

			// Event Reminders
			if (CollectionUtil.isNotEmpty(rftEvent.getRftReminder())) {
				for (RftReminder reminder : rftEvent.getRftReminder()) {
					// Meeting Reminders
					timeline = new RftEventTimeLine();
					timeline.setActivityDate(reminder.getReminderDate());
					timeline.setActivity(EventTimelineType.REMINDER);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rftEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.reminder.date", new Object[] { rftEvent.getEventName() }, Global.LOCALE));
					rftEventTimeLineDao.save(timeline);
				}
			}

			// Meetings
			if (CollectionUtil.isNotEmpty(rftEvent.getMeetings())) {
				for (RftEventMeeting meeting : rftEvent.getMeetings()) {
					// Meeting
					timeline = new RftEventTimeLine();
					timeline.setActivityDate(meeting.getAppointmentDateTime());
					timeline.setActivity(EventTimelineType.MEETING);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rftEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.meeting.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
					rftEventTimeLineDao.save(timeline);

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
						// Meeting Reminders
						for (RftEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
							timeline = new RftEventTimeLine();
							timeline.setActivityDate(reminder.getReminderDate());
							timeline.setActivity(EventTimelineType.REMINDER);
							timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
							timeline.setEvent(rftEvent);
							timeline.setDescription(messageSource.getMessage("timeline.event.meeting.reminder.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
							rftEventTimeLineDao.save(timeline);
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
	public List<RftEventTimeLine> getRftEventTimeline(String id) {
		return rftEventTimeLineDao.getRftEventTimeline(id);
	}

	@Override
	public JasperPrint generatePdfforEvaluationSummary(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {
		LOG.info(" generatePdfforEvaluationSummary is called here" + timeZone);
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rftSummary = new ArrayList<EvaluationPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		sdf.setTimeZone(timeZone);
		parameters.put("generatedOn", sdf.format(new Date()));

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RftEvent event = getRftEventByeventId(eventId);

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
				String type = "RFT";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName(event.getEventOwner().getBuyer().getCompanyName());
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> submittedSupplierList = null;
				List<EventSupplier> SupplierList = rftEventSupplierService.getAllSuppliersByEventId(eventId);
				if (event.getViewSupplerName()) {
					summary.setSupplierMaskingList(new ArrayList<SupplierMaskingPojo>());
				} else {
					summary.setSupplierMaskingList(buildSupplierMaskingData(SupplierList, eventId));
				}

				boolean isMasked = false;
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					isMasked = true;
				}

				List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
				List<EvaluationBqPojo> bqGranTotalList = new ArrayList<EvaluationBqPojo>();
				if (CollectionUtils.isNotEmpty(SupplierList)) {
					submittedSupplierList = new ArrayList<EventSupplier>();
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
						suppliers.setStatus(supplier.getSubmissionStatus().name());
						suppliers.setContactNo(supplier.getSupplier().getMobileNumber());
						suppliers.setIsQualify(supplier.getDisqualify());
						suppliers.setSubmitted(supplier.getSubmitted());

						if (supplier.getDisqualify() == Boolean.TRUE) {
							suppliers.setReason(supplier.getDisqualifyRemarks());
						}
						allSuppliers.add(suppliers);
						if (Boolean.TRUE == supplier.getSubmitted()) {
							submittedSupplierList.add(supplier);
						}
						if (SubmissionStatusType.COMPLETED == supplier.getSubmissionStatus()) {
							suppliersForLeadEvaluators.add(suppliers);
						}
					}
					if (suppliersForLeadEvaluators.size() > 0) {
						summary.setSuppliersForLeadEvaluators(suppliersForLeadEvaluators);
					}
				}

				// Below Code to show all CQ details
				List<EvaluationCqPojo> allCqs = getAllSupplierCqforEvaluationSummary(eventId, isMasked, sdf);

				// Below functionality to get all BQ Items along with suppliers
				// response of each BQ Items
				List<EvaluationSuppliersBqPojo> supplierBq = getAllSupplierBqforEvaluationSummary(submittedSupplierList, eventId, isMasked, sdf);

				// GRAND TOTAL BQ LIST
				List<Bq> bqs = rftEventBqDao.findRftBqbyEventId(eventId);
				if (CollectionUtil.isNotEmpty(bqs)) {
					for (Bq bq : bqs) {
						EvaluationBqPojo supBqs = new EvaluationBqPojo();
						supBqs.setName(bq.getName());
						List<EvaluationSuppliersPojo> suppBqComments = new ArrayList<EvaluationSuppliersPojo>();
						if (CollectionUtil.isNotEmpty(submittedSupplierList)) {
							for (EventSupplier supplier : submittedSupplierList) {
								EvaluationSuppliersPojo supList = new EvaluationSuppliersPojo();
								RftSupplierBq supBq = rftSupplierBqDao.findBqByBqId(bq.getId(), supplier.getSupplier().getId());
								if (isMasked) {
									supList.setSupplierName(MaskUtils.maskName("SUPPLIER", supplier.getSupplier().getId(), eventId));
								} else {
									supList.setSupplierName(supplier.getSupplier() != null ? supplier.getSupplier().getCompanyName() : "");
								}
								if (supBq != null) {
									supList.setTotalItemTaxAmt(df.format(supBq.getGrandTotal()));
									supList.setTotalAmt(df.format(supBq.getAdditionalTax() != null ? supBq.getAdditionalTax() : BigDecimal.ZERO));
									supList.setGrandTotal(supBq.getTotalAfterTax() != null ? df.format(supBq.getTotalAfterTax()) : "");
								}
								String comments = "";
								List<RftBqTotalEvaluationComments> comment = rftBqTotalEvaluationCommentsService.getComments(supplier.getSupplier().getId(), eventId, bq.getId(), SecurityLibrary.getLoggedInUser());
								if (CollectionUtil.isNotEmpty(comment)) {
									for (RftBqTotalEvaluationComments leadComments : comment) {
										comments += "[" + (sdf.format(leadComments.getCreatedDate()) + ":") + leadComments.getLoginName() + " ] " + leadComments.getComment() + "\n";
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

				List<EvaluationSuppliersSorPojo> supplierSor = getAllSupplierSorforEvaluationSummary(SupplierList, eventId, isMasked);

				// LOG.info(supplierBq.toString());
				summary.setBqLeadCommentsList(bqGranTotalList);
				summary.setSuppliers(allSuppliers);
				summary.setCqs(allCqs);
				summary.setBqSuppliers(supplierBq);
				summary.setSorSuppliers(supplierSor);

				rftSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rftSummary);

			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rftSummary, false);
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

					List<Sor> bqs = rftEventSorDao.findSorbyEventId(eventId);

					List<EvaluationSorPojo> allBqs = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Sor bq : bqs) {
							EvaluationSorPojo bqItem = new EvaluationSorPojo();
							bqItem.setName(bq.getName());
							List<RftSupplierSorItem> suppBqItems = rftSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());

							List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RftSupplierSorItem suppBqItem : suppBqItems) {

									EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
									evlBqItem.setDescription(suppBqItem.getItemName());
									evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
									evlBqItem.setAmount(null);
									evlBqItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
									evlBqItems.add(evlBqItem);
									if (suppBqItem.getChildren() != null) {
										for (RftSupplierSorItem childBqItem : suppBqItem.getChildren()) {
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

	private List<EvaluationSuppliersBqPojo> getAllSupplierBqforEvaluationSummary(List<EventSupplier> supplierList, String eventId, boolean isMasked, SimpleDateFormat sdf) {
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
					List<Bq> bqs = rftEventBqDao.findRftBqbyEventId(eventId);

					List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Bq bq : bqs) {
							EvaluationBqPojo bqItem = new EvaluationBqPojo();
							bqItem.setName(bq.getName());
							// List<RftSupplierBqItem> suppBqItems =
							// rftSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bq.getId(),
							// supItem.getSupplier().getId());
							List<RftSupplierBqItem> suppBqItems = rftSupplierBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());
							for (RftSupplierBqItem rftSupplierBqItem : suppBqItems) {
								BigDecimal grandTotal = rftSupplierBqItem.getSupplierBq().getGrandTotal();
								bqSupplierPojo.setGrandTotal(grandTotal);
								LOG.info("GRANDTOTAL " + rftSupplierBqItem.getSupplierBq().getGrandTotal());
							}

							List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RftSupplierBqItem suppBqItem : suppBqItems) {
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
										for (RftSupplierBqItem childBqItem : suppBqItem.getChildren()) {
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
											List<RftBqEvaluationComments> bqItemComments = rftBqEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getBqItem().getId(), null);
											if (CollectionUtil.isNotEmpty(bqItemComments)) {
												String reviews = "";
												for (RftBqEvaluationComments review : bqItemComments) {
													EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
													bqComment.setCommentBy(review.getLoginName());
													bqComment.setComments(review.getComment());
													comments.add(bqComment);
													reviews += "[ " + (sdf.format(review.getCreatedDate()) + ":") + review.getLoginName() + " ] " + review.getComment() + "\n";
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

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, boolean isMasked, SimpleDateFormat sdf) {

		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		List<RftCq> cqList = rftCqService.findRftCqForEvent(eventId);
		for (RftCq cq : cqList) {
			EvaluationCqPojo cqPojo = new EvaluationCqPojo();
			cqPojo.setName(cq.getName());
			cqPojo.setDescription(cq.getDescription());

			List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
			if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
				for (RftCqItem cqItem : cq.getCqItems()) {
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
					List<Supplier> suppList = rftEventSupplierDao.getEventSuppliersForSummary(eventId);
					for (Supplier supp : suppList) {
						if (isMasked) {
							supp.setCompanyName(MaskUtils.maskName("SUPPLIER", supp.getId(), eventId));
						}
					}
					// Below code to get Suppliers Answers of each CQ Items
					if (CollectionUtil.isNotEmpty(suppList)) {
						// List<RftSupplierCqItem> responseList =
						// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppList);
						List<RftSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
						if (CollectionUtil.isNotEmpty(responseList)) {

							for (RftSupplierCqItem suppCqItem : responseList) {
								EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
								List<RftSupplierCqOption> listAnswers = rftSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
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
									// List<RftCqOption> rftSupplierCqOptions = suppCqItem.getCqItem().getCqOptions();
									List<RftCqOption> rftSupplierCqOptions = rftCqOptionDao.findOptionsByCqItem(suppCqItem.getCqItem().getId());// suppCqItem.getCqItem().getCqOptions();
									if (CollectionUtil.isNotEmpty(rftSupplierCqOptions)) {
										for (RftCqOption rftSupplierCqOption : rftSupplierCqOptions) {
											docIds.add(StringUtils.checkString(rftSupplierCqOption.getValue()));
										}
									}
									List<EventDocument> eventDocuments = rftDocumentService.findAllRftEventDocsNamesByEventIdAndDocIds(docIds);
									if (eventDocuments != null) {
										String str = "";
										for (EventDocument docName : eventDocuments) {
											str = str + docName.getFileName() + "\n";
										}
										cqItemSupplierPojo.setAnswer(str);
									}
								} else if (CollectionUtil.isNotEmpty(listAnswers) && (suppCqItem.getCqItem().getCqType() == CqType.LIST || suppCqItem.getCqItem().getCqType() == CqType.CHECKBOX)) {
									String str = "";
									// List<RftSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RftSupplierCqOption op : listAnswers) {
										// int cqAnsListSize = (listAnswers).size();
										// int index = (listAnswers).indexOf(op);
										str += op.getValue() + "\n";
									}
									cqItemSupplierPojo.setAnswer(str);
								} else if (CollectionUtil.isNotEmpty(listAnswers) && suppCqItem.getCqItem().getCqType() != CqType.DOCUMENT_DOWNLOAD_LINK) {
									String str = "";
									// List<RftSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RftSupplierCqOption op : listAnswers) {
										int cqAnsListSize = (listAnswers).size();
										int index = (listAnswers).indexOf(op);
										str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));

										// str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() :
										// "");
									}
									cqItemSupplierPojo.setAnswer(str);
								}
								cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
								// Review Comments
								List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
								List<RftCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
								if (CollectionUtil.isNotEmpty(comments)) {
									String evalComment = "";
									for (RftCqEvaluationComments item : comments) {
										EvaluationCqItemSupplierCommentsPojo supCmnts = new EvaluationCqItemSupplierCommentsPojo();
										supCmnts.setComment(item.getComment());
										supCmnts.setCommentBy(item.getUserName());
										evalComments.add(supCmnts);
										evalComment += "[ " + (sdf.format(item.getCreatedDate()) + ":") + item.getUserName() + " ] " + item.getComment() + "\n";
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
	public JasperPrint getEvaluationSummaryPdf(RftEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rftEventDao.findByEventId(event.getId());
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> summary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();
		if (StringUtils.checkString(strTimeZone).length() > 0) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		// String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		// if (strTimeZone != null) {
		// timeZone = TimeZone.getTimeZone(strTimeZone);
		// }

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
			eventDetails.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
			eventDetails.setType("RFT");
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
					for (RftUnMaskedUser rfaUnmaskedUser : event.getUnMaskedUsers()) {
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
				for (RftEvaluationConclusionUser rfaEvaluationUsers : event.getEvaluationConclusionUsers()) {
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
				for (RftReminder item : event.getRftReminder()) {
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

			// Correspondance Address
			String correspondAddress = "";
			if (event.getEventOwner() != null) {
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
				deliveryAddress += event.getDeliveryAddress().getTitle() + "\r\n" + event.getDeliveryAddress().getLine1() + ", \r\n";
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
			List<RftEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RftEventContact contact : eventContacts) {
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
			// List<RftEventSupplier> suppliersList = event.getSuppliers();
			List<Supplier> suppliersList = getEventSuppliers(event.getId());
			List<EvaluationSuppliersPojo> suppliers = new ArrayList<EvaluationSuppliersPojo>();
			if (CollectionUtil.isNotEmpty(suppliersList)) {
				for (Supplier evntSupplier : suppliersList) {
					EvaluationSuppliersPojo es = new EvaluationSuppliersPojo();
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						es.setSupplierName(MaskUtils.maskName("SUPPLIER", evntSupplier.getId(), event.getId()));
						eventDetails.setIsMask(true);
					} else {
						es.setSupplierName(evntSupplier.getCompanyName() != null ? evntSupplier.getCompanyName() : "");
						eventDetails.setIsMask(false);
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

			// Fetch List of suppliers CQ
			// List<EvaluationCqPojo> allCqs =
			// getAllSupplierCqforEvaluationSummary(event.getId());
			// CQ Items
			List<EvaluationCqPojo> allCqData = new ArrayList<EvaluationCqPojo>();
			List<RftCq> cqList = rftCqService.findRftCqForEvent(event.getId());
			if (CollectionUtil.isNotEmpty(cqList)) {
				for (RftCq item : cqList) {
					EvaluationCqPojo cqs = new EvaluationCqPojo();
					cqs.setName(item.getName());
					cqs.setDescription(item.getDescription());

					List<EvaluationCqItemPojo> cqItemList = new ArrayList<EvaluationCqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getCqItems())) {
						for (RftCqItem cqItem : item.getCqItems()) {
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
								for (RftCqOption cqOption : cqItem.getCqOptions()) {
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

			// Fetch all list of SORS
			List<Sor> sors = rftEventSorDao.findSorbyEventId(event.getId());
			List<EvaluationSorPojo> allSors = new ArrayList<EvaluationSorPojo>();
			if (CollectionUtil.isNotEmpty(sors)) {
				for (Sor item : sors) {
					EvaluationSorPojo bqPojo = new EvaluationSorPojo();
					bqPojo.setName(item.getName());

					List<RftSorItem> bqItems = rftSorItemDao.findSorItemsForSor(item.getId());
					List<EvaluationSorItemPojo> evaluationBqItem = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RftSorItem bqItem : bqItems) {
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


			// Fetch List of suppliers BQ

			List<Bq> bqs = rftEventBqDao.findRftBqbyEventId(event.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
			if (CollectionUtil.isNotEmpty(bqs)) {
				for (Bq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<RftBqItem> bqItems = rftBqItemDao.findBqItemsForBq(item.getId());
					List<EvaluationBqItemPojo> evaluationBqItem = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RftBqItem bqItem : bqItems) {
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
							bi.setPriceType(priceType);
							bi.setUnitPrice(bqItem.getUnitPrice());
							bi.setImgPath(imgPath);
							bi.setDecimal(event.getDecimal());
							evaluationBqItem.add(bi);
						}
					}
					bqPojo.setBqItems(evaluationBqItem);
					allBqs.add(bqPojo);

				}
			}
			// Fetch List of suppliers Envelops
			List<RftEnvelop> envelops = rftEnvelopService.getAllRftEnvelopByEventId(event.getId(), loggedInUser);
			List<EvaluationEnvelopPojo> envlopList = new ArrayList<EvaluationEnvelopPojo>();
			if (CollectionUtil.isNotEmpty(envelops)) {
				for (RftEnvelop envelop : envelops) {
					EvaluationEnvelopPojo env = new EvaluationEnvelopPojo();
					env.setEnvlopName(envelop.getEnvelopTitle());
					env.setDescription(envelop.getDescription());
					env.setType(envelop.getEnvelopType().getValue());
					env.setOpener(envelop.getOpener() != null ? envelop.getOpener().getName() : "");
					env.setOwner(envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "");
					env.setSequence(envelop.getEnvelopSequence());
					List<EvaluationBqPojo> envlopBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
						for (RftEventBq item : envelop.getBqList()) {
							EvaluationBqPojo bq = new EvaluationBqPojo();
							bq.setName(item.getName());
							envlopBqs.add(bq);
						}
					}
					List<EvaluationSorPojo> envlopSors = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
						for (RftEventSor item : envelop.getSorList()) {
							EvaluationSorPojo bq = new EvaluationSorPojo();
							bq.setName(item.getName());
							envlopSors.add(bq);
						}
					}
					List<EvaluationCqPojo> envlopCqs = new ArrayList<EvaluationCqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
						for (RftCq item : envelop.getCqList()) {
							EvaluationCqPojo cq = new EvaluationCqPojo();
							cq.setName(item.getName());
							envlopCqs.add(cq);
						}
					}

					// List of evaluators
					List<EvaluationEnvelopPojo> evaluatorList = new ArrayList<EvaluationEnvelopPojo>();
					List<RftEvaluatorUser> evaluators = envelop.getEvaluators();
					if (CollectionUtil.isNotEmpty(evaluators)) {
						for (RftEvaluatorUser usr : evaluators) {
							EvaluationEnvelopPojo el = new EvaluationEnvelopPojo();
							el.setOwner(usr.getUser().getName());
							evaluatorList.add(el);
						}
					}

					// List of openers
					List<EvaluationEnvelopPojo> openersList = new ArrayList<EvaluationEnvelopPojo>();
					List<RftEnvelopeOpenerUser> openers = envelop.getOpenerUsers();
					if (CollectionUtil.isNotEmpty(openers)) {
						for (RftEnvelopeOpenerUser usr : openers) {
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

			boolean haveUserConcluded = false;
			if (event.getEnableEvaluationConclusionUsers() && event.getEvaluationConclusionEnvelopeNonEvaluatedCount() != null) {
				eventDetails.setEnvelopeEvaluatedCount(event.getEvaluationConclusionEnvelopeEvaluatedCount());
				eventDetails.setEnvelopeNonEvaluatedCount(event.getEvaluationConclusionEnvelopeNonEvaluatedCount());
				eventDetails.setDisqualifiedSupplierCount(event.getEvaluationConclusionDisqualifiedSupplierCount());
				eventDetails.setRemainingSupplierCount(event.getEvaluationConclusionRemainingSupplierCount());

				List<RftEvaluationConclusionUser> evaluationConclusionUserList = rftEventDao.findEvaluationConclusionUsersByEventId(event.getId());
				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					Log.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RftEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
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

			List<RftEventTimeLine> timeline = getRftEventTimeline(event.getId());
			List<EvaluationTimelinePojo> timelineList = new ArrayList<EvaluationTimelinePojo>();
			if (CollectionUtil.isNotEmpty(timeline)) {
				for (RftEventTimeLine item : timeline) {
					EvaluationTimelinePojo et = new EvaluationTimelinePojo();
					et.setEventDate(item.getActivityDate() != null ? sdf.format(item.getActivityDate()) : "");
					et.setDescription(item.getDescription());
					et.setType(item.getActivity().name());
					timelineList.add(et);
				}
			}

			// Event Approvals
			RftEvent rftEvent = getRftEventById(event.getId());
			List<RftEventApproval> approvals = rftEvent.getApprovals();
			List<EvaluationApprovalsPojo> approvalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (RftEventApproval item : approvals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(item.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RftApprovalUser usr : item.getApprovalUsers()) {
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
			List<RftEventSuspensionApproval> suspensionApprovals = rftEvent.getSuspensionApprovals();
			List<EvaluationApprovalsPojo> susppensionApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionApprovals)) {
				for (RftEventSuspensionApproval approval : suspensionApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> suspApprovUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RftSuspensionApprovalUser usr : approval.getApprovalUsers()) {
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
								suspApprovUserList.add(usrs);
							}
							index++;
						}
					}
					approve.setApprovalUsers(suspApprovUserList);
					susppensionApprovalList.add(approve);
				}
			}

			// Award Approvals
			List<RftEventAwardApproval> awardApprovals = rftEvent.getAwardApprovals();
			List<EvaluationApprovalsPojo> awardApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(awardApprovals)) {
				for (RftEventAwardApproval approval : awardApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RftAwardApprovalUser usr : approval.getApprovalUsers()) {
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
				List<RftEventDocument> document = rftDocumentDao.findAllRftEventdocsbyEventId(event.getId());// event.getDocuments();
				for (RftEventDocument docs : document) {
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
			List<RftComment> comments = event.getComment();
			List<EvaluationCommentsPojo> commentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(comments)) {
				for (RftComment item : comments) {
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
			List<RftSuspensionComment> suspensionComments = event.getSuspensionComment();
			List<EvaluationCommentsPojo> suspensionCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionComments)) {
				for (RftSuspensionComment comment : suspensionComments) {
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
			List<RftAwardComment> awardComments = event.getAwardComment();
			List<EvaluationCommentsPojo> awardCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(awardComments)) {
				for (RftAwardComment comment : awardComments) {
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
			List<RftEventAudit> eventAudit = rftEventAuditDao.getRftEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RftEventAudit ra : eventAudit) {
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
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RftEvent event, String imgPath, SimpleDateFormat sdf) {
		List<RftEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RftEventMeeting meeting : meetingList) {
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
					for (RftEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
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
					for (RftEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
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
	public List<RfxView> getAllEventBasedOnSearchvalue(String supplierId, String opVal, String status, String type, Date startDate, Date endDate, String userId) {
		return rftEventDao.findAllEventForSearchvalue(supplierId, opVal, status, type, startDate, endDate, userId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public RftEvent cancelEvent(RftEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer, User loggedInUser) throws Exception {
		boolean isCancelAfterSuspend = false;
		RftEvent persistObj = rftEventDao.findById(event.getId());
		if (EventStatus.SUSPENDED == persistObj.getStatus()) {
			isCancelAfterSuspend = true;
		}
		persistObj.setStatus(EventStatus.CANCELED);
		persistObj.setCancelReason(event.getCancelReason());
		persistObj = rftEventDao.update(persistObj);

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

			RftEventAudit audit = new RftEventAudit(loggedInUser.getBuyer(), persistObj, loggedInUser, new java.util.Date(), AuditActionType.Cancel, messageSource.getMessage("event.audit.canceled", new Object[] { persistObj.getEventName() }, Global.LOCALE), summarySnapshot);
			rftEventAuditDao.save(audit);

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Event '" + persistObj.getEventId() + " ' is Cancelled", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			if (isCancelAfterSuspend) {
				try {
					LOG.info("publishing rft cancel event to epportal");
					publishEventService.pushRftEvent(persistObj.getId(), persistObj.getTenantId(), false);
				} catch (Exception e) {
					LOG.error("Error while publishing RFT event to EPortal:" + e.getMessage(), e);
				}
			}

			if (persistObj.getBusinessUnit() != null) {
				persistObj.getBusinessUnit().getUnitCode();
			}
			// Check if budget checking ERP interface is enabled
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush() && persistObj.getBusinessUnit() != null && Boolean.TRUE == persistObj.getBusinessUnit().getBudgetCheck()) {
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
			RftEvent event = getRftEventByeventId(eventId);
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

				RftEventMeeting meetingItem = rftEventMeetingDao.findById(meetingId);
				EvaluationMeetingPojo meeting = new EvaluationMeetingPojo();
				meeting.setTitle(meetingItem.getTitle());
				if (meetingItem.getAppointmentDateTime() != null) {
					meeting.setAppointmentDateTime(new Date(sdf.format(meetingItem.getAppointmentDateTime())));
				}
				meeting.setRemarks(meetingItem.getRemarks());
				meeting.setVenue(meetingItem.getVenue());

				List<EvaluationMeetingContactsPojo> contacts = new ArrayList<EvaluationMeetingContactsPojo>();
				if (CollectionUtil.isNotEmpty(meetingItem.getRfxEventMeetingContacts())) {
					for (RftEventMeetingContact mc : meetingItem.getRfxEventMeetingContacts()) {
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

				// rftEventMeetingDao.getRftMeetingById(meetingId);
				if (CollectionUtil.isNotEmpty(meetingItem.getInviteSuppliers())) {
					for (Supplier sup : meetingItem.getInviteSuppliers()) {
						EvaluationSuppliersPojo invitedSupplier = new EvaluationSuppliersPojo();
						RftSupplierMeetingAttendance attendance = rftSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meetingId, sup.getId());

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
	public RftEvent concludeRftEvent(RftEvent event, User loggedInUser) {
		// Clear Award details if present.
		int deleteCount = rftEventAwardDao.removeAwardDetails(event.getId());
		RftEvent dbevent = getRftEventByeventId(event.getId());
		if (deleteCount > 0) {
			try {
				RftEventAudit audit = new RftEventAudit();
				audit.setActionDate(new Date());
				audit.setActionBy(loggedInUser);
				audit.setDescription(messageSource.getMessage("event.award.discarded", new Object[] { dbevent.getEventName() }, Global.LOCALE));
				audit.setEvent(event);
				audit.setAction(AuditActionType.Discard);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISCARD, messageSource.getMessage("event.award.discarded", new Object[] { dbevent.getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
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
		return rftEventDao.update(dbevent);
	}

	@Override
	@Transactional(readOnly = false)
	public String createNextEvent(RftEvent rftevent, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String concludeRemarks, String[] invitedSupp) throws Exception {
		RftEvent oldEvent = getRftEventByeventId(rftevent.getId());

		// Clear Award details if present.
		int deleteCount = rftEventAwardDao.removeAwardDetails(rftevent.getId());
		if (deleteCount > 0) {
			try {
				RftEventAudit audit = new RftEventAudit();
				audit.setActionDate(new Date());
				audit.setActionBy(loggedInUser);
				audit.setDescription(messageSource.getMessage("event.award.discarded", new Object[] { oldEvent.getEventName() }, Global.LOCALE));
				audit.setEvent(rftevent);
				audit.setAction(AuditActionType.Discard);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISCARD, messageSource.getMessage("event.award.discarded", new Object[] { oldEvent.getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFT);
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

		BusinessUnit selectedbusinessUnit = null;
		if (StringUtils.checkString(businessUnitId).length() > 0) {
			selectedbusinessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
		}
		List<RftSupplierBq> rftSupplierBqs = rftSupplierBqService.findRftSummarySupplierBqbyEventId(rftevent.getId());

		RfxTemplate template = null;
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

				// get All SupplierBq by event Id - match it with selected/qualified suppliers and pass only their BQs
				// to clone.
				List<RftSupplierBq> rftSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rftSupplierBqList = new ArrayList<RftSupplierBq>();
					for (RftSupplierBq bq : rftSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rftSupplierBqList.add(bq);
						}
					}
				}

				RfaEvent newEvent = rftevent.createNextRfaEvent(oldEvent, auctionType, bqId, loggedInUser, invitedSupp, rftSupplierBqList);
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

						RftEventAudit rftAudit = new RftEventAudit();
						rftAudit.setAction(AuditActionType.Create);
						rftAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rftAudit.setActionDate(new Date());
						rftAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rftAudit.setEvent(oldEvent);
						eventAuditService.save(rftAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFI: {

				RfiEvent newEvent = rftevent.createNextRfiEvent(oldEvent, loggedInUser, invitedSupp);
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

						RftEventAudit rftAudit = new RftEventAudit();
						rftAudit.setAction(AuditActionType.Create);
						rftAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rftAudit.setActionDate(new Date());
						rftAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rftAudit.setEvent(oldEvent);
						eventAuditService.save(rftAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}

				break;
			}
			case RFP: {

				List<RftSupplierBq> rftSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rftSupplierBqList = new ArrayList<RftSupplierBq>();
					for (RftSupplierBq bq : rftSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rftSupplierBqList.add(bq);
						}
					}
				}

				// get All SupplierBq by event Id - match it with selected/qualified suppliers and pass only their BQs
				// to clone.

				RfpEvent newEvent = rftevent.createNextRfpEvent(oldEvent, loggedInUser, invitedSupp, rftSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				// List<RfpSupplierBq> supplierBqs = newEvent.getRfpSupplierBqs();
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
				if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {
					for (RfpEventBq bq : newEvent.getEventBqs()) {
						bq.setRfxEvent(newdbEvent);
						RfpEventBq dbBq = rfpBqDao.saveOrUpdate(bq);
						bq.setId(dbBq.getId());
						if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
							RfpBqItem parent = null;
							for (RfpBqItem item : bq.getBqItems()) {
								if (item.getOrder() != 0) {
									item.setParent(parent);
								}
								item.setBq(dbBq);
								item.setRfxEvent(newdbEvent);
								RfpBqItem dbItem = rfpBqItemDao.saveOrUpdate(item);
								item.setId(dbItem.getId());
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

						RftEventAudit rftAudit = new RftEventAudit();
						rftAudit.setAction(AuditActionType.Create);
						rftAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rftAudit.setActionDate(new Date());
						rftAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rftAudit.setEvent(oldEvent);
						eventAuditService.save(rftAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", oldEvent.getCreatedBy().getTenantId(), oldEvent.getCreatedBy(), new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFQ: {
				// get All SupplierBq by event Id - match it with selected/qualified suppliers and pass only their BQs
				// to clone.
				List<RftSupplierBq> rftSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rftSupplierBqList = new ArrayList<RftSupplierBq>();
					for (RftSupplierBq bq : rftSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rftSupplierBqList.add(bq);
						}
					}
				}

				RfqEvent newEvent = rftevent.createNextRfqEvent(oldEvent, loggedInUser, invitedSupp, rftSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				// List<RfqSupplierBq> supplierBqs = newEvent.getRfqSupplierBqs();
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

						RftEventAudit rftAudit = new RftEventAudit();
						rftAudit.setAction(AuditActionType.Create);
						rftAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rftAudit.setActionDate(new Date());
						rftAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						rftAudit.setEvent(oldEvent);
						eventAuditService.save(rftAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFT: {
				// get All SupplierBq by event Id - match it with selected/qualified suppliers and pass only their BQs
				// to clone.
				List<RftSupplierBq> rftSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rftSupplierBqList = new ArrayList<RftSupplierBq>();
					for (RftSupplierBq bq : rftSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rftSupplierBqList.add(bq);
						}
					}
				}

				RftEvent newEvent = rftevent.createNextRftEvent(oldEvent, loggedInUser, invitedSupp, rftSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				// List<RftSupplierBq> supplierBqs = newEvent.getRftSupplierBqs();
				if (template != null) {
					newEvent.setTemplate(template);
					createRftFromTemplate(newEvent, template, selectedbusinessUnit, loggedInUser, false);
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
				RftEvent newDbEvent = saveRftEvent(newEvent, loggedInUser);
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
						saveRftEventContact(contact);
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
						RftEventBq dbBq = rftEventBqDao.saveOrUpdate(bq);
						bq.setId(dbBq.getId());
						// save BQ Items
						if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
							RftBqItem parent = null;
							for (RftBqItem bqItem : bq.getBqItems()) {
								if (bqItem.getOrder() != 0) {
									bqItem.setParent(parent);
								}
								bqItem.setBq(dbBq);
								bqItem.setRfxEvent(newDbEvent);
								RftBqItem bqItemDb = rftBqItemDao.saveOrUpdate(bqItem);
								bqItem.setId(bqItemDb.getId());
								if (bqItem.getOrder() == 0) {
									parent = bqItem;
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

						RftEventAudit rftAudit = new RftEventAudit();
						rftAudit.setAction(AuditActionType.Create);
						rftAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						rftAudit.setActionDate(new Date());
						rftAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newDbEvent.getEventId() + " '");
						rftAudit.setEvent(oldEvent);
						eventAuditService.save(rftAudit);

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
		oldEvent.setConcludeRemarks(rftevent.getConcludeRemarks());
		oldEvent.setStatus(EventStatus.FINISHED);
		oldEvent.setConcludeBy(loggedInUser);
		oldEvent.setConcludeDate(new Date());
		oldEvent.setAwardStatus(null);
		rftEventDao.update(oldEvent);

		try {
			tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(oldEvent.getId(), EventStatus.FINISHED);
		} catch (Exception e) {
			LOG.error("Error updating Tat Report " + e.getMessage(), e);
		}
		return newEventId;

	}

	@Override
	public void createRftFromTemplate(RftEvent newEvent, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRequest) throws ApplicationException {
		LOG.info("----------------------create Rft From Template call----------------------------");
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RftEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RftEventApproval newRfApproval = new RftEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RftApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RftApprovalUser approvalUser = new RftApprovalUser();
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
			List<RftEventAwardApproval> approvalList = new ArrayList<>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RftEventAwardApproval newRfApproval = new RftEventAwardApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RftAwardApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RftAwardApprovalUser approvalUser = new RftAwardApprovalUser();
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
			List<RftEventSuspensionApproval> suspApprovalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateSuspApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Susp Approval Level :" + templateSuspApproval.getLevel());
				RftEventSuspensionApproval suspesionApproval = new RftEventSuspensionApproval();
				suspesionApproval.setApprovalType(templateSuspApproval.getApprovalType());
				suspesionApproval.setLevel(templateSuspApproval.getLevel());
				suspesionApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateSuspApproval.getApprovalUsers())) {
					List<RftSuspensionApprovalUser> rfpSuspApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateSuspApproval.getApprovalUsers()) {
						RftSuspensionApprovalUser approvalUser = new RftSuspensionApprovalUser();
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
			newEvent.setRftEnvelop(new ArrayList<RftEnvelop>());

			for (RfxEnvelopPojo en : envlopeList) {
				LOG.info("envlop name form rfx   :  " + en);

				RftEnvelop rfienvlope = new RftEnvelop();
				rfienvlope.setEnvelopTitle(en.getRfxEnvelope());
				rfienvlope.setEnvelopSequence(en.getRfxSequence());
				rfienvlope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rfienvlope.setRfxEvent(newEvent);
				rfienvlope.setEnvelopType(EnvelopType.CLOSED);
				newEvent.getRftEnvelop().add(rfienvlope);
			}
		}

		if (rfxTemplate.getAddBillOfQuantity() != null) {
			newEvent.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
		}
		if (rfxTemplate.getVisibleViewSupplierName()) {
			newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		}

		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());

		List<RftUnMaskedUser> unmaskingUser = new ArrayList<RftUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RftUnMaskedUser newRftUnMaskedUser = new RftUnMaskedUser();
				newRftUnMaskedUser.setUser(team.getUser());
				newRftUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRftUnMaskedUser);
			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}

		List<RftTeamMember> teamMembers = new ArrayList<RftTeamMember>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RftTeamMember newTeamMembers = new RftTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setEvent(newEvent);
				teamMembers.add(newTeamMembers);
				// rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(),
				// newTeamMembers.getTeamMemberType(), newEvent.getEventOwner(),
				// StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ",
				// newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ?
				// newEvent.getReferanceNumber() : " ", RfxTypes.RFT);
			}
			newEvent.setTeamMembers(teamMembers);
		}
		// Copy Evaluation Conclusion Users from template
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RftEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RftEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RftEvaluationConclusionUser conclusionUser = new RftEvaluationConclusionUser();
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
								newEvent.setDecimal(field.getDefaultValue());
								LOG.info("Decimal Default value :  " + field.getDefaultValue());

								newEvent.setEstimatedBudget(new BigDecimal(field.getDefaultValue()));
							}
						} else {
							newEvent.setDecimal(field.getDefaultValue());
							LOG.info("Decimal Default value :  " + field.getDefaultValue());

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
		if (eventIdSettingsDao.isBusinessSettingEnable(loggedInUser.getTenantId(), "RFT")) {
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
	public RftEvent getPlainEventById(String eventId) {
		RftEvent eftEvent = rftEventDao.getPlainEventById(eventId);
		for (RftUnMaskedUser unmask : eftEvent.getUnMaskedUsers()) {
			unmask.getUser();
		}

		return eftEvent;
	}

	@Override
	public RftEvent getPlainEventWithOwnerById(String eventId) {
		return rftEventDao.getPlainEventWithOwnerById(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void suspendEvent(RftEvent event) {
		if (event.getSuspensionType() == SuspensionType.DELETE_NO_NOTIFY || event.getSuspensionType() == SuspensionType.DELETE_NOTIFY) {
			supplierCqItemDao.deleteSupplierCqItemsForEvent(event.getId());
			try {
				rftSupplierCqDao.deleteSupplierCqForEvent(event.getId());
			} catch (Exception e) {
			}

			rftSupplierCommentService.deleteSupplierComments(event.getId());
			rftSupplierBqItemDao.deleteSupplierBqItemsForEvent(event.getId());
			rftSupplierBqDao.deleteSupplierBqsForEvent(event.getId());
			rftSupplierSorItemDao.deleteSupplierSorItemsForEvent(event.getId());
			rftSupplierSorDao.deleteSupplierSorsForEvent(event.getId());
			rftEventSupplierDao.updateSubmiTimeOnEventSuspend(event.getId());
		}

		rftEventDao.suspendEvent(event.getId(), event.getSuspensionType(), event.getSuspendRemarks());
	}

	@Override
	@Transactional(readOnly = false)
	public void resumeEvent(RftEvent event) {
		rftEventDao.resumeEvent(event.getId());
	}

	@Override
	public List<Supplier> getEventSuppliersForSummary(String eventId) {
		return rftEventSupplierDao.getEventSuppliersForSummary(eventId);
	}

	@Override
	public long getEventSuppliersCount(String eventId) {
		return rftEventSupplierDao.getEventSuppliersCount(eventId);
	}

	@Override
	public List<Date> getAllMeetingDateByEventId(String eventId) {
		return rftEventMeetingDao.getAllMeetingDateByEventId(eventId);
	}

	@Override
	public long findTotalMyPendingApprovals(String tenantId, String userId) {
		return rftEventDao.findTotalMyPendingApprovals(tenantId, userId);
	}

	@Override
	public long findTotalEventMyPendingApprovals(String tenantId, String userId) {
		return rftEventDao.findTotalEventMyPendingApprovals(tenantId, userId);
	}

	@Override
	public long findTotalPrMyPendingApprovals(String tenantId, String userId) {
		return rftEventDao.findTotalPrMyPendingApprovals(tenantId, userId);
	}

	@Override
	public long findTotalRfsMyPendingApprovals(String tenantId, String userId) {
		return rftEventDao.findTotalRfsMyPendingApprovals(tenantId, userId);
	}

	@Override
	public long findTotalSupplierFormMyPendingApprovals(String tenantId, String userId) {
		return rftEventDao.findTotalSupplierFormMyPendingApprovals(tenantId, userId);
	}

	@Override
	public long findTotalMyPendingEvaluation(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalMyPendingEvaluation(tenantId, userId, input);
	}

	@Override
	public long findTotalMyPendingEvaluationBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds) {
		//return rftEventDao.findTotalMyPendingEvaluation(tenantId, userId, input);
		return rftEventDao.findTotalMyPendingEvaluationBizUnit(tenantId, userId, input,businessUnitIds);
	}

	@Override
	public List<PendingEventPojo> findMyPendingEvaluation(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findMyPendingEvaluation(tenantId, userId, input);
	}

	@Override
	public List<PendingEventPojo> findMyPendingRfxApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findMyPendingRfxApprovals(tenantId, userId, input);
	}

	@Override
	public List<PendingEventPojo> findMyPendingPrApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findMyPendingPrApprovals(tenantId, userId, input);
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		return rftEventDao.findAssignedTemplateCount(templateId);
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		return rftEventDao.getPlainEventOwnerByEventId(eventId);
	}

	@Override
	public long findTotalMyPendingPrApprovals(String loggedInUserTenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalMyPendingPrApprovals(loggedInUserTenantId, userId, input);
	}

	@Override
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		return rftEventDao.getPlainTeamMembersForEvent(eventId);
	}

	@Override
	public boolean checkTemplateStatusForEvent(String eventId, String eventType) {
		return rftEventDao.checkTemplateStatusForEvent(eventId, eventType);
	}

	@Override
	@Transactional(readOnly = true)
	public String findBusinessUnitName(String eventId) {
		return rftEventDao.findBusinessUnitName(eventId);
	}

	@Override
	public List<ApprovedRejectEventPojo> findMyAprrovedRejectList(String tenantId, String userId, SearchSortFilterPojo search) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm  ");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(tenantId, timeZone);
		List<ApprovedRejectEventPojo> list = rftEventDao.findMyAprrovedRejectList(tenantId, userId, search);

		for (ApprovedRejectEventPojo approvedRejectEventPojo : list) {

			if (approvedRejectEventPojo.getActionDate() != null)
				approvedRejectEventPojo.setActionDateMb(df.format(approvedRejectEventPojo.getActionDate()));
			else
				approvedRejectEventPojo.setActionDateMb("N/A");

			if (approvedRejectEventPojo.getActionDate() != null)
				approvedRejectEventPojo.setCreatedDateMb(df.format(approvedRejectEventPojo.getCreatedDate()));
			else
				approvedRejectEventPojo.setCreatedDateMb("N/A");

		}
		return list;
	}

	@Override
	public List<PendingEventPojo> findMyToDoList(String tenantId, String userId, SearchSortFilterPojo search) {
		return rftEventDao.findMyToDoList(tenantId, userId, search);
	}

	@Override
	public List<PendingEventPojo> findMyToDoListForSupplier(String tenantId, String id, SearchSortFilterPojo search) {
		List<PendingEventPojo> list = rftEventDao.findMyToDoListForSupplier(tenantId, id, search);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a ");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(tenantId, timeZone);
		LOG_S.info("timeZone........................" + timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		// df.setTimeZone(TimeZone.getTimeZone(timeZone));
		for (PendingEventPojo pendingEventPojo : list) {
			LOG_S.info("Event Start Date " + pendingEventPojo.geteDate());
			String eStartDate = df.format(pendingEventPojo.geteDate());
			String eEndDate = df.format(pendingEventPojo.getEventEnd());
			pendingEventPojo.seteStartDate(eStartDate);
			pendingEventPojo.seteEndDate(eEndDate);
		}
		List<PendingEventPojo> poList = supplierDao.findAllPOForSupplierMobile(tenantId, search);
		if (CollectionUtil.isNotEmpty(poList)) {
			if (CollectionUtil.isNotEmpty(list)) {
				list.addAll(poList);
			} else {
				return poList;
			}
		}
		List<PendingEventPojo> resultList = (List<PendingEventPojo>) list.stream().sorted(Comparator.comparing(PendingEventPojo::getCreatedDate)).collect(Collectors.toList());
		return resultList;
	}

	@Override
	public MobileEventPojo getMobileEventDetails(String id, String userId) throws ApplicationException {
		RftEvent event = rftEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a ");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(event.getTenantId(), timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			List<Bq> bqlist = new ArrayList<>();
			for (RftEventBq bq : event.getEventBqs()) {
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
		for (EventTimeline timeLine : rftEventTimeLineDao.getPlainRftEventTimeline(event.getId())) {
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
		// eventPojo.setTimeLine(rftEventTimeLineDao.getPlainRftEventTimeline(event.getId()));
		eventPojo.setDocuments(rftDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRftEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RftEnvelop envelope : event.getRftEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}

		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RftEventApproval eventApproval : event.getApprovals()) {
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
			for (RftComment comment : event.getComment()) {
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
		RftEvent event = rftEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a ");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(tenantId, timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			List<Bq> bqlist = new ArrayList<>();
			for (RftEventBq bq : event.getEventBqs()) {
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
		for (EventTimeline timeLine : rftEventTimeLineDao.getPlainRftEventTimeline(event.getId())) {
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
		// eventPojo.setTimeLine(rftEventTimeLineDao.getPlainRftEventTimeline(event.getId()));
		eventPojo.setDocuments(rftDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRftEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RftEnvelop envelope : event.getRftEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}

		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RftEventApproval eventApproval : event.getApprovals()) {
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
			for (RftComment comment : event.getComment()) {
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
	public List<ApprovedRejectEventPojo> findMyEventList(String tenantId, String userId, SearchSortFilterPojo search) {
		return rftEventDao.findMyEventList(tenantId, userId, search);
	}

	@Override
	public List<ApprovedRejectEventPojo> findMyEventListForSupplier(String tenantId, String userId, SearchSortFilterPojo search) {
		List<ApprovedRejectEventPojo> list = new ArrayList<ApprovedRejectEventPojo>();
		if (search.getType() == null || search.getType() != FilterTypes.PR) {
			list = rftEventDao.findMyAllEventListForSupplier(tenantId, userId, search);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a ");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneBySupplierSettings(tenantId, timeZone);
			LOG_S.info("timeZone........................" + timeZone);
			if (TimeZone.getTimeZone(timeZone) != null) {
				df.setTimeZone(TimeZone.getTimeZone(timeZone));
			}
			// df.setTimeZone(TimeZone.getTimeZone(timeZone));
			for (ApprovedRejectEventPojo approvedRejectEventPojo : list) {
				LOG_S.info("Event Start Date " + approvedRejectEventPojo.geteDate());
				String eStartDate = df.format(approvedRejectEventPojo.geteDate());
				String eEndDate = df.format(approvedRejectEventPojo.getEventEnd());
				approvedRejectEventPojo.seteStartDate(eStartDate);
				approvedRejectEventPojo.seteEndDate(eEndDate);
			}
		}

		if (search.getType() == null || search.getType() == FilterTypes.PR) {

			List<PendingEventPojo> poList = supplierDao.findAllPOForSupplierMobile(tenantId, search);
			if (CollectionUtil.isNotEmpty(poList)) {
				for (PendingEventPojo po : poList) {
					ApprovedRejectEventPojo pojo = new ApprovedRejectEventPojo();
					pojo.setId(po.getId());
					pojo.setType("PR");
					// pojo.setCreatedBy(po.getCreatedBy().getName());
					pojo.setEventName(po.getEventName());
					pojo.setReferenceNumber(po.getReferenceNumber());
					pojo.setStatus(po.getStatus());
					pojo.setAuctionType("PR");
					pojo.setCreatedDate(po.getCreatedDate());
					// pojo.setEventId(po.getEventId());
					pojo.setUnitName(po.getUnitName());
					// pojo.setBuyerName(po.getBuyerName());
					// pojo.setEventStart(po.getEventStart());
					if (list == null) {
						list = new ArrayList<ApprovedRejectEventPojo>();
					}
					list.add(pojo);
				}
			}
			List<ApprovedRejectEventPojo> resultList = (List<ApprovedRejectEventPojo>) list.stream().sorted(Comparator.comparing(ApprovedRejectEventPojo::getCreatedDate)).collect(Collectors.toList());
			return resultList;
		}

		return list;

	}

	@Override
	public long findAggregateEventCountForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception {
		return rftEventDao.findAggregateEventCountForTenant(tenantId, status, filter);
	}

	@Override
	public BigDecimal findAggregateEventBudgetAmountValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception {
		return rftEventDao.findAggregateEventBudgetAmountValueForTenant(tenantId, status, filter);
	}

	@Override
	public BigDecimal findAggregateEventAwardedPriceValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception {
		return rftEventDao.findAggregateEventAwardedPriceValueForTenant(tenantId, status, filter);
	}

	@Override
	public void downloadEventDocument(String docId, HttpServletResponse response) throws Exception {
		RftEventDocument docs = rftDocumentDao.findRftDocsById(docId);
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
			RftEvent event = getRftEventByeventId(eventId);
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
	public RftEvent getRftEventByeventIdForSummaryReport(String eventId) {
		RftEvent event = rftEventDao.findByEventId(eventId);
		if (event != null) {
			if (CollectionUtil.isNotEmpty(event.getCqs())) {
				for (RftCq cq : event.getCqs()) {
					cq.getName();
					if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
						for (RftCqItem item : cq.getCqItems()) {
							item.getItemName();
						}
					}
				}
			}
			if (event.getBusinessUnit() != null) {
				event.getBusinessUnit().getUnitName();
			}
			if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
				for (RftEventSupplier supplier : event.getSuppliers()) {
					supplier.getSupplier().getFullName();
					supplier.getSupplier().getCommunicationEmail();
					supplier.getSupplier().getCompanyContactNumber();
					supplier.getSupplier().getCompanyName();
					supplier.getSupplier().getStatus();
				}
			}
			if (event.getEventOwner().getBuyer() != null) {
				Buyer buyer = event.getEventOwner().getBuyer();
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
			if (event.getDeliveryAddress() != null) {
				event.getDeliveryAddress().getTitle();
				event.getDeliveryAddress().getLine1();
				event.getDeliveryAddress().getLine2();
				event.getDeliveryAddress().getCity();
				event.getDeliveryAddress().getState().getStateName();
				event.getDeliveryAddress().getState().getCountry().getCountryName();
			}
			if (CollectionUtil.isNotEmpty(event.getRftReminder())) {
				for (RftReminder reminder : event.getRftReminder()) {
					reminder.getReminderDate();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RftEventMeeting meeting : event.getMeetings()) {
					meeting.getAppointmentDateTime();
					meeting.getAppointmentTime();
					meeting.getInviteSuppliers();
					meeting.getTitle();
					meeting.getRfxEventMeetingContacts();
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RftEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
							meetingContact.getContactName();
							meetingContact.getContactNumber();
							meetingContact.getContactEmail();
							meetingContact.getId();

						}
					}
					meeting.getStatus();
					meeting.getVenue();

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
						for (RftEventMeetingDocument meetingDocument : meeting.getRfxEventMeetingDocument()) {
							meetingDocument.getId();
							meetingDocument.getFileSizeInKb();
							meetingDocument.getCredContentType();
							meetingDocument.getFileName();
						}
					}

					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier suppliers : meeting.getInviteSuppliers()) {
							suppliers.getCompanyName();
							suppliers.getCommunicationEmail();
							suppliers.getCompanyContactNumber();
						}
					}
					meeting.getRemarks();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				for (RftComment comment : event.getComment()) {
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
			if (event.getEventOwner() != null) {
				event.getEventOwner().getName();
				event.getEventOwner().getCommunicationEmail();
				event.getEventOwner().getPhoneNumber();
				if (event.getEventOwner().getOwner() != null) {
					Owner usr = event.getEventOwner().getOwner();
					usr.getCompanyContactNumber();
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
		}
		return event;
	}

	@Override
	public BigDecimal findAggregateClosedCompletedEventValueForTenant(String tenantId, List<String> status, RequestParamPojo filter) throws Exception {
		return rftEventDao.findAggregateClosedCompletedEventValueForTenant(tenantId, status, filter);
	}

	@Override
	public RftSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId, String tenantId) {
		RftSupplierBq rftSupplierBq = rftEventDao.getSupplierBQOfLeastTotalPrice(eventId, bqId);
		if (rftSupplierBq != null) {

			if (rftSupplierBq.getSupplierBqItems() != null) {
				for (RftSupplierBqItem items : rftSupplierBq.getSupplierBqItems()) {
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
		return rftSupplierBq;
	}

	@Override
	public RftSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId, String tenantId, String awardId) {
		RftSupplierBq rftSupplierBq = rftEventDao.getSupplierBQwithSupplierId(eventId, bqId, supplierId);
		if (rftSupplierBq != null) {

			if (rftSupplierBq.getSupplierBqItems() != null) {
				for (RftSupplierBqItem items : rftSupplierBq.getSupplierBqItems()) {

					RftEventAwardDetails rftEventAwardDetails = null;
					if (StringUtils.checkString(awardId).length() > 0) {
						rftEventAwardDetails = rftEventAwardDetailsDao.rftEventAwardByEventIdandBqId(awardId, items.getBqItem().getId());
					}
					items.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, (rftEventAwardDetails != null && rftEventAwardDetails.getSupplier() != null) ? rftEventAwardDetails.getSupplier().getId() : items.getSupplier().getId()));
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
		return rftSupplierBq;
	}

	@Override
	public RftSupplierBq getSupplierBQOfLowestItemisedPrice(String eventId, String bqId, String tenantId) {
		RftSupplierBq rftSupplierBq = new RftSupplierBq();
		List<RftSupplierBqItem> bqItemList = new ArrayList<RftSupplierBqItem>();
		List<RftSupplierBq> suppBqList = rftEventDao.getSupplierBQOfLowestItemisedPrize(eventId, bqId);
		if (CollectionUtil.isNotEmpty(suppBqList)) {
			int bqItemCount = 1;
			for (RftSupplierBq bq : suppBqList) {
				if (CollectionUtil.isNotEmpty(bq.getSupplierBqItems())) {
					for (RftSupplierBqItem item : bq.getSupplierBqItems()) {
						item.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, item.getSupplier().getId()));
						// LOG.info(item.getBqItem().getId());
						if (item.getBqItem() != null) {

							if (item.getBqItem().getOrder() == 0) {
								if (1 == bqItemCount) {
									bqItemList.add(item);
								}

							} else {
								LOG.info(item.getBqItem().getId() + "---------------" + item.getSupplier().getId());
								RftSupplierBqItem bqItem = rftEventDao.getMinItemisePrice(item.getBqItem().getId(), eventId);
								// LOG.info(bqItem);
								if (bqItem.getBqItem() != null) {
									if (bqItem.getBqItem().getUom() != null) {
										bqItem.getBqItem().getUom().getUom();
									}
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
									rftSupplierBq.setBq(bq.getBq());
								}
							}

						}

						// LOG.info(bqItem);

					}
					bqItemCount++;
				}
				LOG.info(bq.getSupplierBqItems().size() + "..........." + bqItemList.size());
				rftSupplierBq.setSupplierBqItems(bqItemList);

			}

		}

		return rftSupplierBq;
	}

	@Override
	public long findCountOfAllActiveEventForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllActiveEventForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public long findCountOfAllSuspendedEventForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllSuspendedEventForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public long findCountOfAllClosedEventForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllClosedEventForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public long findCountOfAllRejectedEventForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllRejectedEventForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public long findCountOfAllPendingEventForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllPendingEventForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public long findCountOfAllCompletedEventForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllCompletedEventForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public List<RfxView> getOnlyAllSuspendedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyAllSuspendedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public List<RfxView> getOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput input, String userId) {

		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyAllActiveEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public List<RfxView> getOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyAllClosedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public List<RfxView> getOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyAllCompletedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public List<RfxView> getOnlyAllRejectedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyAllRejectedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public List<RfxView> getOnlyAllPendingEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyAllPendingEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalPendingEventForForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalPendingEventForForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalOnlyAllSuspendedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalOnlyAllSuspendedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalOnlyAllActiveEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalOnlyAllActiveEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalOnlyAllClosedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalOnlyAllClosedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalOnlyAllCompletedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalOnlyAllCompletedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalOnlyAllRejectedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalOnlyAllRejectedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public RftEvent getRftEventWithIndustryCategoriesByEventId(String eventId) {
		RftEvent event = rftEventDao.findByEventId(eventId);
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
	public boolean isExistsRftEventId(String tenantId, String eventId) {
		return rftEventDao.isExistsRftEventId(tenantId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RftTeamMember> addAssociateOwners(User createdBy, RftEvent newEvent) {
		List<User> adminUser = userDao.fetchAllActivePlainAdminUsersForTenant(createdBy.getTenantId());
		List<RftTeamMember> teamMemberList = new ArrayList<RftTeamMember>();
		if (CollectionUtil.isNotEmpty(adminUser)) {
			for (User user : adminUser) {
				RftTeamMember teamMember = new RftTeamMember();
				teamMember.setUser(user);
				teamMember.setEvent(newEvent);
				teamMember.setTeamMemberType(TeamMemberType.Associate_Owner);
				teamMemberList.add(teamMember);
			}
			newEvent.setTeamMembers(teamMemberList);
		}
		rftEventDao.saveOrUpdate(newEvent);
		return teamMemberList;
	}

	@Override
	@Transactional(readOnly = false)
	public void setDefaultEventContactDetail(String loggedInUserId, String eventId) {
		RftEventContact eventContact = new RftEventContact();
		User user = userDao.findById(loggedInUserId);
		eventContact.setContactName(user.getName());
		eventContact.setDesignation(user.getDesignation());
		eventContact.setContactNumber(user.getPhoneNumber());
		eventContact.setComunicationEmail(user.getCommunicationEmail());
		if (user.getBuyer() != null) {
			eventContact.setMobileNumber(user.getBuyer().getMobileNumber());
			eventContact.setFaxNumber(user.getBuyer().getFaxNumber());
		}
		RftEvent rfpEvent = new RftEvent();
		rfpEvent.setId(eventId);
		eventContact.setRfxEvent(rfpEvent);
		saveRftEventContact(eventContact);
	}

	@Override
	public List<DraftEventPojo> getAllEventForBuyer(String tenantId, TableDataInput input, String userid, Date startDate, Date endDate) {

		return rftEventDao.getAllEventsForBuyer(tenantId, input, userid, startDate, endDate);
	}

	@Override
	public List<DraftEventPojo> getAllExcelEventReportForBuyer(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {

		List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		getRftEventsForExport(tenantId, eventArr, resultList, searchFilterEventPojo, select_all, input, startDate, endDate);
		return resultList;
	}

	private void getRftEventsForExport(String tenantId, String[] eventArr, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		List<RftEvent> rftList = rftEventDao.getEventsByIds(tenantId, eventArr, searchFilterEventPojo, select_all, input, startDate, endDate);

		if (CollectionUtil.isNotEmpty(rftList)) {
			for (RftEvent event : rftList) {
				DraftEventPojo eventPojo = new DraftEventPojo();
				if (event.getEventDescription() != null) {
					eventPojo.setEventDescription(event.getEventDescription());
				}
				eventPojo.setCostCenter(event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "");
				eventPojo.setId(event.getId());
				eventPojo.setInvitedSupplierCount(event.getSuppliers().size());
				eventPojo.setEventName(event.getEventName());
				eventPojo.setReferenceNumber(event.getReferanceNumber());
				eventPojo.setSysEventId(event.getEventId());
				eventPojo.setEventStart(event.getEventStart());
				eventPojo.setEventEnd(event.getEventEnd());
				eventPojo.setType(RfxTypes.RFT);
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

				RftEventSupplier leadingSupplier = null;
				int submittedCount = 0;
				int acceptedCount = 0;
				BigDecimal leadingAmount = BigDecimal.ZERO;
				for (RftEventSupplier es : event.getSuppliers()) {
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
					for (RftEventBq eventBq : event.getEventBqs()) {
						RftSupplierBq supBq = rftSupplierBqDao.findBqByEventIdAndSupplierIdOfQualifiedSupplier(event.getId(), eventBq.getId(), es.getSupplier().getId());
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
	public List<RfxView> getOnlyAllAcceptedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyAllAcceptedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalAcceptedEventForForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalAcceptedEventForForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findCountOfAllAcceptedEventForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllAcceptedEventForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public RftEvent findRftEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId) {
		return rftEventDao.findRftEventByErpAwardRefNoAndTenantId(erpAwardRefId, tenantId);

	}

	@Override
	@Transactional(readOnly = false)
	public String createPrFromAward(RftEventAward rftEventAward, String templateId, String userId, User loggedInUser) throws ApplicationException {
		User createdBy = userDao.findById(userId);
		List<String> supplierList = new ArrayList<String>();
		// List<ProductCategory> ctegoryList =
		// productCategoryMaintenanceService.getAllProductCategoryByTenantId(loggedInUser.getTenantId());
		// Supplier -> List of Sections -> List of Items
		Map<String, Map<String, List<PrItem>>> data = new HashMap<String, Map<String, List<PrItem>>>();
		String value = "";
		try {
			LOG.info("rftEventAward.getRfxAwardDetails()--------" + rftEventAward.getRfxAwardDetails().size());
			if (CollectionUtil.isNotEmpty(rftEventAward.getRfxAwardDetails())) {

				BqItem section = null;

				for (RftEventAwardDetails rfxAward : rftEventAward.getRfxAwardDetails()) {
					LOG.info("order--------" + rfxAward.getBqItem().getLevel() + "--" + rfxAward.getBqItem().getOrder());
					LOG.info("---------------bqitemId--------------------" + rfxAward.getBqItem().getBqId());

					BqItem dbBqItem = rftBqItemDao.getBqItemByBqIdAndBqItemId(rfxAward.getBqItem().getId());
					if (dbBqItem.getOrder() == 0) {
						section = dbBqItem;
					} else {
						String sid = rfxAward.getSupplier().getId();
						RftSupplierBqItem supplierBqItem = rftSupplierBqItemDao.getSupplierBqItemByBqItemId(rfxAward.getBqItem().getId(), sid);
						LOG.info("rftEventAward.getRfxAwardDetails() new--------------" + rfxAward.getSupplier().getId());
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
							item.setItemName(section.getItemName());
							item.setItemDescription(section.getItemDescription());
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
							}*/

							ProductCategory category = productCategoryMaintenanceService.getProductCategoryById(rfxAward.getProductCategory());

							if (category != null) {
								item.setProductCategory(category);
							}

							item.setFreeTextItemEntered(Boolean.TRUE);
							item.setItemName(dbBqItem.getItemName());
							item.setItemDescription(dbBqItem.getItemDescription());
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
							item.setItemName(dbBqItem.getItemName());
							item.setItemDescription(dbBqItem.getItemDescription());
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
						LOG.info("Order Check" + item.getOrder());

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
					RftEvent rfxEvent = rftEventService.getEventById(rftEventAward.getRfxEvent().getId());
					Pr pr = prService.copyFromTemplateWithAward(templateId, createdBy, loggedInUser, loggedInUser.getTenantId(), null, favouriteSupplier, sections, rfxEvent);
					LOG.info("================" + pr.getPrId());
					if (i == 0) {
						value = (StringUtils.checkString(pr.getName()).length() > 0 ? pr.getName() : "") + ": " + pr.getPrId();
					} else {
						value += ", " + pr.getPrId();
					}
					i++;

				}

			}
		} catch (Exception e) {
			LOG.error("Error generating Auto PRs during Event Award : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
		return value;
	}

	@Override
	@Transactional(readOnly = false)
	public String createContractFromAward(RftEventAward rftEventAward, String eventId, String contractStartDate, String contractEndDate, String groupCodeHid, String referenceNumberHid, User loggedInUser, HttpSession session, String contractCreatorHid) throws ApplicationException {
		List<String> supplierList = new ArrayList<String>();

		Map<String, List<ProductContractItems>> map = new HashMap<>();
		String value = "";
		try {
			RftEvent event = rftEventDao.findByEventId(eventId);

			LOG.info("rfpEventAward.getRfxAwardDetails()--------" + rftEventAward.getRfxAwardDetails().size());
			if (CollectionUtil.isNotEmpty(rftEventAward.getRfxAwardDetails())) {

				for (RftEventAwardDetails rfxAward : rftEventAward.getRfxAwardDetails()) {
					LOG.info("order--------" + rfxAward.getBqItem().getLevel() + "--" + rfxAward.getBqItem().getOrder());
					LOG.info("---------------bqitemId--------------------" + rfxAward.getBqItem().getBqId());

					BqItem dbBqItem = rftBqItemDao.getBqItemByBqIdAndBqItemId(rfxAward.getBqItem().getId());
					if (dbBqItem.getOrder() != 0) {
						String sid = rfxAward.getSupplier().getId();
						RftSupplierBqItem supplierBqItem = rftSupplierBqItemDao.getSupplierBqItemByBqItemId(rfxAward.getBqItem().getId(), sid);
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
							item.setProductItemType(rfxAward.getProductType());
							item.setItemName(dbBqItem.getItemName());
							item.setItemCode(rfxAward.getProductCode()); // UI List
							item.setItemDescription(dbBqItem.getItemDescription());
							item.setUom(dbBqItem.getUom());
							item.setProductItemType(rfxAward.getProductType()); // UI List
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
	public JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rftSummary = new ArrayList<EvaluationPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RftEvent event = getRftEventByeventId(eventId);

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
				String type = "RFT";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName("");
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> submittedSupplierList = null;
				List<EventSupplier> SupplierList = rftEventSupplierService.getAllSuppliersByEventId(eventId);
				List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
				List<EvaluationBqPojo> bqGranTotalList = new ArrayList<EvaluationBqPojo>();
				if (CollectionUtils.isNotEmpty(SupplierList)) {
					submittedSupplierList = new ArrayList<EventSupplier>();
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
						if (Boolean.TRUE == supplier.getSubmitted()) {
							submittedSupplierList.add(supplier);
						}

					}
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
				sdf.setTimeZone(timeZone);

				// Below Code to show all CQ details
				List<EvaluationCqPojo> allCqs = getAllSupplierCqforEvaluationSummary(eventId, envelopeId, sdf);

				// Below functionality to get all BQ Items along with suppliers
				// response of each BQ Items
				List<EvaluationSuppliersBqPojo> supplierBq = getAllSupplierBqforEvaluationSummary(submittedSupplierList, eventId, envelopeId, sdf);

				// GRAND TOTAL BQ LIST
				List<Bq> bqs = rftEventBqDao.findRftBqbyEventId(eventId);
				if (CollectionUtil.isNotEmpty(bqs)) {
					for (Bq bq : bqs) {
						EvaluationBqPojo supBqs = new EvaluationBqPojo();
						supBqs.setName(bq.getName());
						List<EvaluationSuppliersPojo> suppBqComments = new ArrayList<EvaluationSuppliersPojo>();
						if (CollectionUtil.isNotEmpty(submittedSupplierList)) {
							for (EventSupplier supplier : submittedSupplierList) {
								EvaluationSuppliersPojo supList = new EvaluationSuppliersPojo();
								RftSupplierBq supBq = rftSupplierBqDao.findBqByBqId(bq.getId(), supplier.getSupplier().getId());
								supList.setSupplierName(supplier.getSupplier() != null ? supplier.getSupplier().getCompanyName() : "");
								if (supBq != null) {
									supList.setTotalItemTaxAmt(df.format(supBq.getGrandTotal()));
									supList.setTotalAmt(df.format(supBq.getTotalAfterTax().subtract(supBq.getGrandTotal() != null ? supBq.getGrandTotal() : BigDecimal.ZERO)));
									supList.setGrandTotal(supBq.getTotalAfterTax() != null ? df.format(supBq.getTotalAfterTax()) : "");
								}
								String comments = "";
								List<RftBqTotalEvaluationComments> comment = rftBqTotalEvaluationCommentsService.getComments(supplier.getSupplier().getId(), eventId, bq.getId(), SecurityLibrary.getLoggedInUser());
								if (CollectionUtil.isNotEmpty(comment)) {
									for (RftBqTotalEvaluationComments leadComments : comment) {
										comments += "[" + (sdf.format(leadComments.getCreatedDate()) + ":") + leadComments.getLoginName() + " ] " + leadComments.getComment() + "\n";
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

				rftSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rftSummary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rftSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationSuppliersBqPojo> getAllSupplierBqforEvaluationSummary(List<EventSupplier> submittedSupplierList, String eventId, String envelopeId, SimpleDateFormat sdf) {

		List<EvaluationSuppliersBqPojo> supplierBq = new ArrayList<EvaluationSuppliersBqPojo>();

		if (CollectionUtils.isNotEmpty(submittedSupplierList)) {
			for (EventSupplier supItem : submittedSupplierList) {
				if (supItem.getDisqualify() == Boolean.FALSE) {
					EvaluationSuppliersBqPojo bqSupplierPojo = new EvaluationSuppliersBqPojo();

					bqSupplierPojo.setSupplierName(supItem.getSupplierCompanyName());

					List<Bq> bqs = rftEventBqDao.findRftBqbyEventIdAndEnvelopeId(eventId, envelopeId);

					List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Bq bq : bqs) {
							EvaluationBqPojo bqItem = new EvaluationBqPojo();
							bqItem.setName(bq.getName());
							// List<RftSupplierBqItem> suppBqItems =
							// rftSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bq.getId(),
							// supItem.getSupplier().getId());
							List<RftSupplierBqItem> suppBqItems = rftSupplierBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());
							List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RftSupplierBqItem suppBqItem : suppBqItems) {
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
										for (RftSupplierBqItem childBqItem : suppBqItem.getChildren()) {
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
											List<RftBqEvaluationComments> bqItemComments = rftBqEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getBqItem().getId(), null);
											if (CollectionUtil.isNotEmpty(bqItemComments)) {
												String reviews = "";
												for (RftBqEvaluationComments review : bqItemComments) {
													EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
													bqComment.setCommentBy(review.getLoginName());
													bqComment.setComments(review.getComment());
													comments.add(bqComment);
													reviews += "[ " + (sdf.format(review.getCreatedDate()) + ":") + review.getLoginName() + " ] " + review.getComment() + "\n";
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

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, String envelopeId, SimpleDateFormat sdf) {

		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		List<RftCq> cqList = rftCqService.findRftCqForEventByEnvelopeId(eventId, envelopeId);
		for (RftCq cq : cqList) {
			EvaluationCqPojo cqPojo = new EvaluationCqPojo();
			cqPojo.setName(cq.getName());
			cqPojo.setDescription(cq.getDescription());

			List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
			if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
				for (RftCqItem cqItem : cq.getCqItems()) {
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
					List<Supplier> suppList = rftEventSupplierDao.getEventSuppliersForSummary(eventId);
					// Below code to get Suppliers Answers of each CQ Items

					if (CollectionUtil.isNotEmpty(suppList)) {
						// List<RftSupplierCqItem> responseList =
						// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppList);
						List<RftSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
						if (CollectionUtil.isNotEmpty(responseList)) {

							for (RftSupplierCqItem suppCqItem : responseList) {
								EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
								cqItemSupplierPojo.setSupplierName(suppCqItem.getSupplier().getCompanyName());
								List<RftSupplierCqOption> listAnswers = rftSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
								if (suppCqItem.getCqItem().getCqType() == CqType.TEXT) {
									cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
								} else if (CollectionUtil.isNotEmpty(listAnswers)) {
									String str = "";
									// List<RftSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RftSupplierCqOption op : listAnswers) {
										int cqAnsListSize = (listAnswers).size();
										int index = (listAnswers).indexOf(op);
										str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));

										// str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() :
										// "");
										cqItemSupplierPojo.setAnswer(str);
									}
								}
								cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
								// Review Comments
								List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
								List<RftCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
								if (CollectionUtil.isNotEmpty(comments)) {
									String evalComment = "";
									for (RftCqEvaluationComments item : comments) {
										EvaluationCqItemSupplierCommentsPojo supCmnts = new EvaluationCqItemSupplierCommentsPojo();
										supCmnts.setComment(item.getComment());
										supCmnts.setCommentBy(item.getUserName());
										evalComments.add(supCmnts);
										evalComment += "[ " + (sdf.format(item.getCreatedDate()) + ":") + item.getUserName() + " ] " + item.getComment() + "\n";
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

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
			LOG.error("Could not generate Bidding English PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private List<EvaluationAuctionBiddingPojo> buildEvalutionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			RftEvent event = rftEventDao.findByEventId(eventId);

			List<EventSupplier> supplierList = rftEventSupplierDao.getAllSuppliersByEventId(eventId);
			if (event != null) {
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				RftEnvelop envelop = rftEnvelopService.getRftEnvelopById(evenvelopId);
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
				buildSupplierCountData(supplierList, auction);
				auction.setIsMask(isMasked);
				buildHeadingReport(event, supplierList, auction, sdf, isMasked, envelop, true);
				auction.setEnvelopTitle(envelop != null ? envelop.getEnvelopTitle() : "NA");
				auction.setLeadEvaluater(envelop != null ? (envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "NA") : "NA");
				auction.setLeadEvaluatorSummary(envelop != null ? ((envelop.getEvaluatorSummaryDate() != null ? (sdf.format(envelop.getEvaluatorSummaryDate()) + ":") : "") + (envelop.getLeadEvaluatorSummary() != null ? envelop.getLeadEvaluatorSummary() : "")) : "N/A");
				auction.setFileName(envelop != null ? envelop.getFileName() : "N/A");
				if (envelop != null) {
					buildEvaluatorsSummary(envelop.getEvaluators(), auction, sdf);
					if (event.getEnableEvaluationDeclaration()) {
						buildEvaluatorDeclarationAcceptData(sdf, envelop.getId(), eventId, auction);
					}
				}
				buildEventDetailData(sdf, event, auction);
				buildBuyerAttachementData(event.getId(), auction, sdf);
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
		List<RftEvaluatorDeclaration> evalutorDeclarationList = rftEvaluatorDeclarationDao.getAllEvaluatorDeclarationByEnvelopAndEventId(envelopId, eventId);
		List<EvaluationDeclarationAcceptancePojo> evaluationDeclarationList = new ArrayList<EvaluationDeclarationAcceptancePojo>();
		if (CollectionUtil.isNotEmpty(evalutorDeclarationList)) {
			for (RftEvaluatorDeclaration rftEvaluatorDeclaration : evalutorDeclarationList) {
				EvaluationDeclarationAcceptancePojo evaluationDeclarationPojo = new EvaluationDeclarationAcceptancePojo();
				evaluationDeclarationPojo.setEvaluationCommittee(Boolean.TRUE == rftEvaluatorDeclaration.getIsLeadEvaluator() ? "Evaluation Owner" : "Evaluation Team");
				evaluationDeclarationPojo.setAcceptedDate(sdf.format(rftEvaluatorDeclaration.getAcceptedDate()));
				if (rftEvaluatorDeclaration.getUser() != null) {
					evaluationDeclarationPojo.setUsername(rftEvaluatorDeclaration.getUser().getName());
					evaluationDeclarationPojo.setUserLoginEmail(rftEvaluatorDeclaration.getUser().getLoginId());
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

	private void buildHeadingReport(RftEvent event, List<EventSupplier> supplierList, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, boolean isMasked, RftEnvelop envelop, boolean isEvaluation) {
		try {
			List<EvaluationAuctionBiddingPojo> headerBqList = new ArrayList<EvaluationAuctionBiddingPojo>();
			List<RftEventBq> bq = rftEventBqDao.findBqbyEventIdAndEnvelopeId(event.getId(), envelop.getId());
			EvaluationAuctionBiddingPojo headerPojo = new EvaluationAuctionBiddingPojo();

			// List<RftEventBq> bq = rftEventBqDao.findBqsByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(bq)) {
				for (RftEventBq rfaEventBq : bq) {
					headerPojo = new EvaluationAuctionBiddingPojo();
					List<RfaSupplierBqPojo> rfaEventSupplierIds = rftSupplierBqDao.getAllRftTopCompletedEventSuppliersIdByEventId(event.getId(), 5, rfaEventBq.getId());
					headerPojo.setHeaderBqName(rfaEventBq.getName());

					RfaSupplierBqPojo leadSupplier = null;
					for (RfaSupplierBqPojo rfaSupplierBqPojo : rfaEventSupplierIds) {
						if (rfaSupplierBqPojo.getCompleteness() == rfaSupplierBqPojo.getTotalItem()) {
							leadSupplier = rfaSupplierBqPojo;
							break;
						}
					}
					headerPojo.setDecimal(event.getDecimal());
					headerPojo.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "N/A");
					buildLeadSupplierData(event, headerPojo, leadSupplier, rfaEventBq, isMasked, envelop);
					buildTopCompletedBarChartData(event, headerPojo, rfaEventSupplierIds, isMasked, envelop);
					buildDisqualificationSuppliersData(event, headerPojo, isMasked, envelop, sdf, isEvaluation);
					buildMatchingIpAddressData(event.getId(), headerPojo, supplierList);
					headerBqList.add(headerPojo);
				}
			} else {
				headerPojo.setHeaderBqName("");
				buildDisqualificationSuppliersData(event, headerPojo, isMasked, envelop, sdf, isEvaluation);
				buildMatchingIpAddressData(event.getId(), headerPojo, supplierList);
				headerBqList.add(headerPojo);
			}
			auction.setHeader(headerBqList);

		} catch (Exception e) {
			LOG.error("Could not get build Heading Report :" + e.getMessage(), e);
		}
	}

	private void buildLeadSupplierData(RftEvent event, EvaluationAuctionBiddingPojo auction, RfaSupplierBqPojo leadingbid, RftEventBq rfaEventBq, boolean isMasked, RftEnvelop envelop) {
		try {
			if (leadingbid != null) {
				BigDecimal savingVsBudget = BigDecimal.ZERO, percentageVsBudget = BigDecimal.ZERO;
				BigDecimal savingVsHistoric = BigDecimal.ZERO, percentageVsHistoric = BigDecimal.ZERO;
				if (event.getBudgetAmount() != null && event.getBudgetAmount().compareTo(BigDecimal.ZERO) != 0) {
					savingVsBudget = event.getBudgetAmount().subtract(leadingbid.getTotalAfterTax() != null ? leadingbid.getTotalAfterTax() : BigDecimal.ZERO);
					percentageVsBudget = (savingVsBudget.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(event.getBudgetAmount(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
					auction.setRemarkComparerBudgetPrice((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : " ") + " " + formatedDecimalNumber(event.getDecimal(), savingVsBudget) + " or " + formatedDecimalNumber(event.getDecimal(), percentageVsBudget) + "% vs budgeted price");
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
			LOG.error("Could not get Supplier leadingbid Details" + e.getMessage(), e);
		}

	}

	private void buildTopCompletedBarChartData(RftEvent event, EvaluationAuctionBiddingPojo auction, List<RfaSupplierBqPojo> eventBq, boolean isMasked, RftEnvelop envelop) {
		List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
		try {
			if (CollectionUtil.isNotEmpty(eventBq)) {
				for (RfaSupplierBqPojo item : eventBq) {
					// if (item.getCompleteness() == item.getTotalItem()) {
					EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
					suppBidprice.setPostAuctionprice(item.getTotalAfterTax() != null ? item.getTotalAfterTax() : item.getInitialPrice());
					suppBidprice.setBidderName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId())) : item.getSupplierCompanyName() + "(" + item.getCompleteness() + "/" + item.getTotalItem() + ")");
					suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
					suppBidprice.setDecimal(event.getDecimal());
					suppBidprice.setPostAuctionprice(item.getTotalAfterTax());
					suppBidprice.setPostAuctionStrPrice(coolFormat(item.getTotalAfterTax().doubleValue(), 0));
					bidPriceList.add(suppBidprice);
					// }
				}
				// this is for add partialy completed supplier after complited supplier
				// for (RfaSupplierBqPojo item : eventBq) {
				// if (item.getCompleteness() != item.getTotalItem()) {
				// EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
				// suppBidprice.setPostAuctionprice(item.getTotalAfterTax() != null ? item.getTotalAfterTax() :
				// item.getInitialPrice());
				// suppBidprice.setBidderName(item.getSupplierCompanyName());
				// suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ?
				// event.getBaseCurrency().getCurrencyCode() : "");
				// suppBidprice.setDecimal(event.getDecimal());
				// suppBidprice.setPostAuctionprice(item.getTotalAfterTax());
				// suppBidprice.setPostAuctionStrPrice(coolFormat(item.getTotalAfterTax().doubleValue(), 0));
				// bidPriceList.add(suppBidprice);
				// }
				// }

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

	private void buildDisqualificationSuppliersData(RftEvent event, EvaluationAuctionBiddingPojo auction, boolean isMasked, RftEnvelop envelop, SimpleDateFormat sdf, boolean isEvaluation) {

		try {
			List<RftEventSupplier> supplierList = null;
			if (isEvaluation) {
				supplierList = rftEventSupplierService.findDisqualifySupplierForEvaluationReportByEventId(event.getId());
			} else {
				supplierList = rftEventSupplierService.findDisqualifySupplierByEventId(event.getId());
			}
			List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
			if (CollectionUtils.isNotEmpty(supplierList)) {
				if (supplierList.size() == 1) {
					for (RftEventSupplier supplier : supplierList) {
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
			LOG.error("Could not get build Disqualification Suppliers Data : " + e.getMessage(), e);
		}
	}

	private void buildMatchingIpAddressData(String eventId, EvaluationAuctionBiddingPojo auction, List<EventSupplier> participatedSupplier) {
		try {
			List<EvaluationBiddingIpAddressPojo> ipAddressList = new ArrayList<EvaluationBiddingIpAddressPojo>();
			Map<String, String> hm = new HashMap<String, String>();
			int i = 0;
			if (CollectionUtil.isNotEmpty(participatedSupplier)) {
				for (EventSupplier rfaSupplier : participatedSupplier) {
					if (StringUtils.checkString(rfaSupplier.getIpAddress()).length() > 0) {
						if (hm.containsKey(rfaSupplier.getIpAddress())) {
							if (StringUtils.checkString(hm.get(rfaSupplier.getIpAddress())).length() > 0) {
								hm.put(rfaSupplier.getIpAddress(), hm.get(rfaSupplier.getIpAddress()) + " & " + rfaSupplier.getSupplierCompanyName());
							} else {
								hm.put(rfaSupplier.getIpAddress(), rfaSupplier.getSupplierCompanyName());
							}
							i++;
						} else {
							int flag = 0;
							for (EventSupplier rftSupplierIp : participatedSupplier) {
								if (StringUtils.checkString(rftSupplierIp.getIpAddress()).length() > 0) {
									if (rftSupplierIp.getIpAddress().equals(rfaSupplier.getIpAddress())) {
										flag++;
									}
								}
							}
							if (flag > 1) {
								hm.put(rfaSupplier.getIpAddress(), rfaSupplier.getSupplierCompanyName());
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
			LOG.error("Could not get build Matching IpAddress Data :" + e.getMessage(), e);
		}

	}

	private void buildEvaluatorsSummary(List<RftEvaluatorUser> evaluators, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf) {
		try {
			List<EvaluationBqItemComments> evaluationSummary = new ArrayList<EvaluationBqItemComments>();
			for (RftEvaluatorUser rfaEvaluatorUser : evaluators) {
				EvaluationBqItemComments comments = new EvaluationBqItemComments();
				comments.setCommentBy(rfaEvaluatorUser.getUser() != null ? ((sdf.format(rfaEvaluatorUser.getEvaluatorSummaryDate()) + ":") + rfaEvaluatorUser.getUser().getName()) : "N/A");
				comments.setComments(StringUtils.checkString(rfaEvaluatorUser.getEvaluatorSummary()));
				comments.setFileName(rfaEvaluatorUser.getFileName() != null ? rfaEvaluatorUser.getFileName() : "N/A");
				evaluationSummary.add(comments);
			}
			auction.setEvaluationSummary(evaluationSummary);
		} catch (Exception e) {
			LOG.error("Could not get build Evaluators Summary :" + e.getMessage(), e);
		}
	}

	private void buildEventDetailData(SimpleDateFormat sdf, RftEvent event, EvaluationAuctionBiddingPojo auction) {
		try {
			String auctionDate = event.getEventStart() != null ? sdf.format(event.getEventStart()) : "" + "-" + (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			String auctionTitle = "PRE & POST AUCTION PRICE (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + ")";
			String netSavingTitle = "Saving based on Budged(%)";
			auction.setAuctionId(event.getEventId());
			auction.setReferenceNo(event.getReferanceNumber());
			auction.setAuctionName(event.getEventName());
			auction.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
			auction.setDateTime(auctionDate);
			auction.setEventType(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			auction.setOwnerWithContact(event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + " Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			auction.setEventDescription(StringUtils.checkString(event.getEventDescription()));
			// here for RFX submission & evaluation report used
			auction.setAuctionType(RfxTypes.RFT.getValue());
			auction.setAuctionTitle(auctionTitle);
			auction.setNetSavingTitle(netSavingTitle);
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
			List<RftEventDocument> documents = rftDocumentDao.findAllRftEventdocsbyEventId(eventId);
			if (CollectionUtil.isNotEmpty(documents)) {
				for (RftEventDocument docs : documents) {
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

	private void buildSupplierLineChartAndData(SimpleDateFormat sdf, RftEvent event, List<EventSupplier> participatedSupplier, EvaluationAuctionBiddingPojo auction, boolean isMasked, RftEnvelop envelop) {
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
			List<RfaSupplierBqPojo> eventBq = rftSupplierBqDao.findRftSupplierParticipation(event.getId());
			for (RfaSupplierBqPojo eventSupp : eventBq) {
				if (isMasked) {
					eventSupp.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplierId(), envelop.getId()));
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
			LOG.error("Could not get build Supplier Line Chart And Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierActivitySummaryData(RftEvent event, RfaSupplierBqPojo eventSupp, List<EvaluationBidderContactPojo> supplierActivitySummaryList, SimpleDateFormat sdf) {
		try {
			// event.getId());
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

	private void buildSupplierAcceptedListData(RftEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderAcceptedList, SimpleDateFormat sdf, boolean isMasked, RftEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			supplierActivitySummary.setActionDate(eventSupp.getSupplierEventReadTime() != null ? sdf.format(eventSupp.getSupplierEventReadTime()) : "N/A");
			bidderAcceptedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Accepted List Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierRejectedListData(RftEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderRejectedList, SimpleDateFormat sdf, boolean isMasked, RftEnvelop envelop) {
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

	private void buildSupplierInvidedListData(RftEvent event, EventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderInvidedList, SimpleDateFormat sdf, boolean isMasked, RftEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			bidderInvidedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Invided List Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierTotallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RftEvent event, SimpleDateFormat sdf, boolean isMasked, RftEnvelop envelop) {
		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rftSupplierBqDao.findRftSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);
			List<String> bqids = rftEnvelopDao.getBqIdlistByEnvelopId(envelop.getId());
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

	private void buildSupplierPartiallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RftEvent event, boolean isMasked, RftEnvelop envelop) {
		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rftSupplierBqDao.findRftSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);
			List<String> bqids = rftEnvelopDao.getBqIdlistByEnvelopId(envelop.getId());
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

	private void buildEnvoleSORData(RftEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RftEnvelop envelop, boolean isMasked,
									List<String> sortedSupplierList, List<String> top3Supplier) {
		try {
			List<EnvelopeSorPojo> envopleSorPojos = new ArrayList<EnvelopeSorPojo>();
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<SorPojo> sorIds = rftEnvelopDao.getSorsIdListByEnvelopIdByOrder(envelopId);

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

	private void buildEnvlopeSupplierSORorEvaluationSummary(String bqId, RftEvent event, EnvelopeSorPojo bqPojo, RftEnvelop envelop, boolean isMasked, List<String> sortedSupplierList) {
		LOG.info("Build envelope data for SOR ");
		try {
			List<EvaluationSuppliersSorPojo> sors = rftSupplierSorDao.getAllSorsBySorIdsAndEventId(bqId, event.getId());
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
						List<RftSupplierSorItem> suppSorItems = rftSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierId(sor.getSorId(), sor.getId());
						List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
						if (CollectionUtil.isNotEmpty(suppSorItems)) {
							for (RftSupplierSorItem suppBqItem : suppSorItems) {
								EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									for (RftSupplierSorItem childBqItem : suppBqItem.getChildren()) {
										EvaluationSorItemPojo evlBqChilItem = new EvaluationSorItemPojo();
										evlBqChilItem.setDescription(childBqItem.getItemName());
										evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
										evlBqChilItem.setUom(childBqItem.getUom().getUom());
										//Rate
										evlBqChilItem.setAmount(childBqItem.getTotalAmount());
										evlBqChilItem.setDecimal(event != null ? event.getDecimal() : "");
										evlBqItems.add(evlBqChilItem);

										List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
										List<RftSorEvaluationComments> bqItemComments = rftSorEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getSorItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RftSorEvaluationComments review : bqItemComments) {
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


	private void buildEnvlopeSORTopSupplierComparisionforEvaluationSummary(String sorId, RftEvent event, EnvelopeSorPojo bqPojo, boolean isMasked, RftEnvelop envelop, List<String> top3Supplier) {
		try {
			// Here limit will not work
			List<RfaSupplierSorPojo> participatedSupplier = rftSupplierSorDao.getAllRftTopEventSuppliersIdByEventId(event.getId(), 3, sorId);
			Map<String, EvaluationSorItemPojo> itemsMap = new LinkedHashMap<String, EvaluationSorItemPojo>();
			List<EvaluationSuppliersSorPojo> pojoList = new ArrayList<EvaluationSuppliersSorPojo>();
			EvaluationSuppliersSorPojo pojo = new EvaluationSuppliersSorPojo();
			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierSorPojo rfaSupplierBqPojo = participatedSupplier.get(i);
				top3Supplier.add(rfaSupplierBqPojo.getSupplierCompanyName());
				bqPojo.setSorName(rfaSupplierBqPojo.getSorName());

				List<RftSupplierSorItem> suppBqItems = rftSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierIdParentIdNotNull(sorId, rfaSupplierBqPojo.getSupplierId());
				if (i == 0) {
					for (RftSupplierSorItem suppBqItem : suppBqItems) {
						EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
						evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
						evlBqItem.setItemName(suppBqItem.getItemName());
						evlBqItem.setUom(suppBqItem.getUom() != null ? suppBqItem.getUom().getUom() : "");
						evlBqItem.setAmount(suppBqItem.getTotalAmount());
						pojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
						itemsMap.put(suppBqItem.getSorItem().getId(), evlBqItem);
					}
				} else {
					for (RftSupplierSorItem suppBqItem : suppBqItems) {
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

	private void buildEnvoleBQData(RftEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RftEnvelop envelop, boolean isMasked,
								   List<String> sortedSupplierList, List<String> top3Supplier) {
		try {
			List<EnvelopeBqPojo> envopleBqPojos = new ArrayList<EnvelopeBqPojo>();
			// List<String> bqids = rftEnvelopDao.getBqIdlistByEnvelopId(envelop.getId());
			List<RftEventBq> bqids = rftEnvelopDao.getBqIdlistByEnvelopIdByOrder(envelop.getId());
			for (RftEventBq bqId : bqids) {
				// for (EvaluationSuppliersBqPojo bq : bqs) {

				EnvelopeBqPojo bqPojo = new EnvelopeBqPojo();
				buildEnvlopeTopSupplierComparisionforEvaluationSummary(bqId.getId(), event, bqPojo, isMasked, envelop, top3Supplier);

				buildEnvlopeSupplierBqforEvaluationSummary(bqId.getId(), event, bqPojo, isMasked, envelop, sdf, sortedSupplierList);
				envopleBqPojos.add(bqPojo);
			}

			auction.setEnvelopeBq(envopleBqPojos);
		} catch (Exception e) {
			LOG.error("Could not get build EnvoleBQ Data :" + e.getMessage(), e);
		}
	}

	private void buildEnvlopeSupplierBqforEvaluationSummary(String bqId, RftEvent event, EnvelopeBqPojo bqPojo, boolean isMasked, RftEnvelop envelop, SimpleDateFormat sdf, List<String> sortedSupplierList) {
		try {
			List<EvaluationSuppliersBqPojo> bqs = rftSupplierBqDao.getAllBqsByBqIdsAndEventId(bqId, event.getId());
			List<EvaluationSuppliersBqPojo> supplierBq = new ArrayList<EvaluationSuppliersBqPojo>();
			if (CollectionUtils.isNotEmpty(bqs)) {
				for (EvaluationSuppliersBqPojo bq : bqs) {
					sortedSupplierList.add(bq.getSupplierName());
					EvaluationSuppliersBqPojo bqSupplierPojo = new EvaluationSuppliersBqPojo();
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
						List<RftSupplierBqItem> suppBqItems = rftSupplierBqItemService.getAllSupplierBqItemForReportByBqIdAndSupplierId(bq.getBqId(), bq.getId());
						List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
						if (CollectionUtil.isNotEmpty(suppBqItems)) {
							for (RftSupplierBqItem suppBqItem : suppBqItems) {
								EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setQuantity(suppBqItem.getQuantity());
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									for (RftSupplierBqItem childBqItem : suppBqItem.getChildren()) {
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
										List<RftBqEvaluationComments> bqItemComments = rftBqEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getBqItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RftBqEvaluationComments review : bqItemComments) {
												EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
												bqComment.setCommentBy(review.getUserName());
												bqComment.setComments(review.getComment());
												comments.add(bqComment);
												reviews += "[ " + (sdf.format(review.getCreatedDate()) + ":") + review.getUserName() + " ] " + review.getComment() + "\n";
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

	private void buildEnvlopeTopSupplierComparisionforEvaluationSummary(String bqId, RftEvent event, EnvelopeBqPojo bqPojo, boolean isMasked, RftEnvelop envelop, List<String>top3Supplier) {
		try {
			List<RfaSupplierBqPojo> participatedSupplier = rftSupplierBqDao.getAllRfaTopEventSuppliersIdByEventId(event.getId(), 3, bqId);
			Map<String, EvaluationBqItemPojo> itemsMap = new LinkedHashMap<String, EvaluationBqItemPojo>();
			List<EvaluationSuppliersBqPojo> pojoList = new ArrayList<EvaluationSuppliersBqPojo>();
			EvaluationSuppliersBqPojo pojo = new EvaluationSuppliersBqPojo();

			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierBqPojo rfaSupplierBqPojo = participatedSupplier.get(i);

				top3Supplier.add(rfaSupplierBqPojo.getSupplierCompanyName());
				bqPojo.setBqName(rfaSupplierBqPojo.getBqName());
				LOG.info("Bq name in comparision:" + rfaSupplierBqPojo.getBqName());
				List<RftSupplierBqItem> suppBqItems = rftSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierIds(bqId, rfaSupplierBqPojo.getSupplierId());
				if (i == 0) {
					for (RftSupplierBqItem suppBqItem : suppBqItems) {
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
					for (RftSupplierBqItem suppBqItem : suppBqItems) {
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

	private void buildEnvelopeCQData(RftEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RftEnvelop envelop, boolean isMasked) {

		try {
			List<EnvelopeCqPojo> allCqs = new ArrayList<EnvelopeCqPojo>();
			List<String> cqid = rftEnvelopDao.getCqIdlistByEnvelopId(envelop.getId());
			if (CollectionUtil.isNotEmpty(cqid)) {
				List<RftCq> cqList = rftCqService.findRfaCqForEventByEnvelopeId(cqid, event.getId());
				for (RftCq cq : cqList) {
					EnvelopeCqPojo cqPojo = new EnvelopeCqPojo();
					cqPojo.setName(cq.getName());
					buildSupplierCqforEvaluationSummary(cq, event, cqPojo, isMasked, envelop, sdf);
					allCqs.add(cqPojo);
				}
			}

			auction.setEnvelopeCq(allCqs);
		} catch (Exception e) {
			LOG.error("Could not get build Envelope CQ Data :eclipse-javadoc:%E2%98%82=procurehere/src%5C/main%5C/java%3Ccom" + e.getMessage(), e);
		}

	}

	private void buildSupplierCqforEvaluationSummary(RftCq cq, RftEvent event, EnvelopeCqPojo pojo, boolean isMasked, RftEnvelop envelop, SimpleDateFormat sdf) {

		try {
			List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();

			if (cq != null) {
				EvaluationCqPojo cqPojo = new EvaluationCqPojo();
				cqPojo.setName(cq.getName());
				cqPojo.setDescription(cq.getDescription());

				List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
				if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
					for (RftCqItem cqItem : cq.getCqItems()) {

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
							List<Supplier> suppList = rftEventSupplierDao.getEventSuppliersForSummary(event.getId());
							// Below code to get Suppliers Answers of each CQ Items

							if (CollectionUtil.isNotEmpty(suppList)) {
								// List<RftSupplierCqItem> responseList =
								// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(),
								// event.getId(),
								// suppList);
								List<RftSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), event.getId(), suppList);
								if (CollectionUtil.isNotEmpty(responseList)) {
									for (RftSupplierCqItem suppCqItem : responseList) {
										List<RftSupplierCqOption> listAnswers = rftSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());

										EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
										cqItemSupplierPojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), suppCqItem.getSupplier().getId(), envelop.getId())) : suppCqItem.getSupplier().getCompanyName());
										if (suppCqItem.getCqItem().getCqType() == CqType.TEXT || suppCqItem.getCqItem().getCqType() == CqType.DATE || suppCqItem.getCqItem().getCqType() == CqType.NUMBER || suppCqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
											cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
										} else if (CqType.DOCUMENT_DOWNLOAD_LINK == suppCqItem.getCqItem().getCqType()) {
											List<String> docIds = new ArrayList<String>();
											List<RftCqOption> rftSupplierCqOptions = suppCqItem.getCqItem().getCqOptions();
											
											if (CollectionUtil.isNotEmpty(rftSupplierCqOptions)) {
												for (RftCqOption rftSupplierCqOption : rftSupplierCqOptions) {
													docIds.add(StringUtils.checkString(rftSupplierCqOption.getValue()));
												}
												List<EventDocument> eventDocuments = rftDocumentService
														.findAllRftEventDocsNamesByEventIdAndDocIds(event.getId(),
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

											// List<RftSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
											for (RftSupplierCqOption op : listAnswers) {
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
										List<RftCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), event.getId(), cqItem.getId(), null);
										if (CollectionUtil.isNotEmpty(comments)) {
											String evalComment = "";
											for (RftCqEvaluationComments item : comments) {
												EvaluationCqItemSupplierCommentsPojo supCmnts = new EvaluationCqItemSupplierCommentsPojo();
												supCmnts.setComment(item.getComment());
												supCmnts.setCommentBy(item.getUserName());
												evalComments.add(supCmnts);
												evalComment += "[ " + (sdf.format(item.getCreatedDate()) + ":") + item.getUserName() + " ] " + item.getComment() + "\n";
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

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
			LOG.error("Could not generate Submission PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationAuctionBiddingPojo> buildSubmissionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			RftEvent event = rftEventDao.findByEventId(eventId);
			List<EventSupplier> supplierList = rftEventSupplierDao.getAllSuppliersByEventId(eventId);
			if (event != null) {
				RftEnvelop envelop = rftEnvelopService.getRftEnvelopById(evenvelopId);
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
					buildEvaluatorsSummary(envelop.getEvaluators(), auction, sdf);
				}
				buildEventDetailData(sdf, event, auction);
				buildBuyerAttachementData(event.getId(), auction, sdf);
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
			LOG.error("Could not get build SubmissionReport Data :" + e.getMessage(), e);
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
	public void autoSaveRfaDetails(RftEvent rftEvent, String[] industryCategory, BindingResult result, String strTimeZone) {
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (rftEvent.getEventVisibilityDates() != null) {
				String visibilityDates[] = rftEvent.getEventVisibilityDates().split("-");
				formatter.setTimeZone(timeZone);
				try {
					Date startDate = (Date) formatter.parse(visibilityDates[0]);
					Date endDate = (Date) formatter.parse(visibilityDates[1]);
					rftEvent.setEventEnd(endDate);
					rftEvent.setEventStart(startDate);
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
				rftEvent.setIndustryCategories(icList);
			}

			if (result.hasErrors()) {
				List<String> errMessages = new ArrayList<String>();
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.info("ERROR : " + err.getDefaultMessage());
				}
			} else {

				if (CollectionUtil.isNotEmpty(rftEvent.getApprovals())) {
					int level = 1;
					for (RftEventApproval app : rftEvent.getApprovals()) {
						app.setEvent(rftEvent);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
							for (RftApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}

			}
			if (StringUtils.checkString(rftEvent.getId()).length() > 0) {
				RftEvent persistObj = getRftEventByeventId(rftEvent.getId());
				Date notificationDateTime = null;
				if (rftEvent.getEventPublishDate() != null && rftEvent.getEventPublishTime() != null) {
					notificationDateTime = DateUtil.combineDateTime(rftEvent.getEventPublishDate(), rftEvent.getEventPublishTime(), timeZone);
				}
				rftEvent.setEventPublishDate(notificationDateTime);

				setAndUpdateRftEvent(rftEvent, persistObj);
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);

		}
	}

	private void setAndUpdateRftEvent(RftEvent rftEvent, RftEvent persistObj) throws ParseException {

		LOG.info("Rfa Event : Bq : " + rftEvent.getBillOfQuantity() + " : cq : " + rftEvent.getQuestionnaires() + " : Doc : " + rftEvent.getDocumentReq() + " : Meet : " + rftEvent.getMeetingReq());
		persistObj.setModifiedDate(new Date());
		persistObj.setIndustryCategory(rftEvent.getIndustryCategory());
		// persistObj.setStatus(EventStatus.DRAFT); -- DONT SET THE STATUS AS
		// DRAFT AS THIS COULD BE A SUSPENDED EVENT
		// EDIT - @Nitin Otageri
		persistObj.setEventStart(rftEvent.getEventStart());
		persistObj.setEventEnd(rftEvent.getEventEnd());
		LOG.info("StartDate:" + rftEvent.getEventStart());
		LOG.info("EndDate:" + rftEvent.getEventEnd());
		persistObj.setEventVisibility(rftEvent.getEventVisibility());
		persistObj.setEventName(rftEvent.getEventName());
		persistObj.setReferanceNumber(rftEvent.getReferanceNumber());
		persistObj.setUrgentEvent(rftEvent.getUrgentEvent());
		persistObj.setEventVisibilityDates(rftEvent.getEventVisibilityDates());
		persistObj.setDeliveryAddress(rftEvent.getDeliveryAddress());
		persistObj.setParticipationFeeCurrency(rftEvent.getParticipationFeeCurrency());
		persistObj.setParticipationFees(rftEvent.getParticipationFees());
		persistObj.setDepositCurrency(rftEvent.getDepositCurrency());
		persistObj.setDeposit(rftEvent.getDeposit());
		// Should not assign this
		if (persistObj.getTemplate() != null && persistObj.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(persistObj.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			if (!rfxTemplate.getVisibleAddSupplier()) {
				rftEvent.setAddSupplier(rfxTemplate.getAddSupplier());
			}
			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				rftEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				rftEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}
			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				rftEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}
		}
		persistObj.setEventPublishDate(rftEvent.getEventPublishDate());
		persistObj.setSubmissionValidityDays(rftEvent.getSubmissionValidityDays());
		// Event Timeline
		// Date notificationDateTime =
		// DateUtil.combineDateTime(rfaEvent.getSupplierNotificationDate(),
		// rfaEvent.getSupplierNotificationTime());
		// persistObj.setSupplierNotificationDate(notificationDateTime);
		persistObj.setEventStart(rftEvent.getEventStart());
		persistObj.setApprovals(rftEvent.getApprovals());

		// Event Req
		persistObj.setEnableEvaluationDeclaration(rftEvent.getEnableEvaluationDeclaration());
		persistObj.setEnableSupplierDeclaration(rftEvent.getEnableSupplierDeclaration());
		persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == rftEvent.getEnableEvaluationDeclaration() && rftEvent.getEvaluationProcessDeclaration() != null ? rftEvent.getEvaluationProcessDeclaration() : null);
		persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == rftEvent.getEnableSupplierDeclaration() && rftEvent.getSupplierAcceptanceDeclaration() != null ? rftEvent.getSupplierAcceptanceDeclaration() : null);
		persistObj.setBillOfQuantity(rftEvent.getBillOfQuantity());
		persistObj.setScheduleOfRate(rftEvent.getScheduleOfRate());
		persistObj.setQuestionnaires(rftEvent.getQuestionnaires());
		persistObj.setDocumentReq(rftEvent.getDocumentReq());
		persistObj.setMeetingReq(rftEvent.getMeetingReq());
		persistObj.setEventDetailCompleted(Boolean.TRUE);
		persistObj.setIndustryCategories(rftEvent.getIndustryCategories());
		persistObj.setDeliveryDate(rftEvent.getDeliveryDate());
		persistObj.setCloseEnvelope(rftEvent.getCloseEnvelope());
		persistObj.setAddSupplier(rftEvent.getAddSupplier());
		persistObj.setViewSupplerName(rftEvent.getViewSupplerName());
		persistObj.setUnMaskedUser(rftEvent.getUnMaskedUser());
		persistObj.setUnMaskedUser(rftEvent.getUnMaskedUser());
		persistObj.setAllowToSuspendEvent(rftEvent.getAllowToSuspendEvent());
		updateRftEvent(persistObj);

	}

	@Override
	public List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {

		return rftEventDao.getAllEventwithSearchFilter(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate);
		// List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		// getRftSearchEventsForExport(tenantId, eventIds, resultList, searchFilterEventPojo, select_all, startDate,
		// endDate, sdf);
		// return resultList;
	}

	@Override
	public EventTimerPojo getTimeEventByeventId(String eventId) {
		return rftEventDao.getTimeEventByeventId(eventId);
	}

	@Override
	public RftEvent loadRftEventForSummeryPageById(String id) {
		RftEvent event = rftEventDao.findBySupplierEventId(id);
		RftEvent event2 = rftEventDao.findEventSorByEventId(id);
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
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RftEventContact contact : event.getEventContacts()) {
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

			// Touch Event Reminders...
			if (CollectionUtils.isNotEmpty(event.getRftReminder())) {
				for (RftReminder reminder : event.getRftReminder()) {
					reminder.getInterval();
				}
			}

			if (CollectionUtil.isNotEmpty(event2.getEventSors())) {
				for (RftEventSor bq : event2.getEventSors()) {
					if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
						for (RftSorItem item : bq.getSorItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
				event.setEventSors(event2.getEventSors());
			}

			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RftTeamMember teamMembers : event.getTeamMembers()) {
					teamMembers.getTeamMemberType();
					teamMembers.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RftEventMeeting meeting : event.getMeetings()) {
					meeting.getRemarks();
					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier supplier : meeting.getInviteSuppliers()) {
							supplier.getCompanyName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RftEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
							contact.getContactName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
						for (RftEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
							reminder.getReminderDate();
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RftUnMaskedUser unmaskuser : event.getUnMaskedUsers()) {
					unmaskuser.getUser().getCommunicationEmail();
					unmaskuser.getUser().getName();
					unmaskuser.getUser().getPhoneNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RftEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
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

			if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
				for (RftEventBq bq : event.getEventBqs()) {
					if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
						for (RftBqItem item : bq.getBqItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				for (RftComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RftEventApproval approvalLevel : event.getApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RftApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RftEventSuspensionApproval approvalLevel : event.getSuspensionApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RftSuspensionApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
				for (RftEventAwardApproval approvalLevel : event.getAwardApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RftAwardApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardedSuppliers())) {
				for (Supplier supp : event.getAwardedSuppliers()) {
					supp.getCompanyName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAdditionalDocument())) {
				for (AdditionalDocument additionalDocument : event.getAdditionalDocument()) {
					additionalDocument.getId();
					additionalDocument.getFileSizeInKb();
					additionalDocument.getCredContentType();
					additionalDocument.getFileName();
				}
			}

			if (event.getConcludeBy() != null) {
				event.getConcludeBy().getName();
			}
			if (event.getSupplierAcceptanceDeclaration() != null) {
				event.getSupplierAcceptanceDeclaration().getTitle();
			}
			if (event.getEvaluationProcessDeclaration() != null) {
				event.getEvaluationProcessDeclaration().getTitle();
			}

			if (event.getProcurementMethod() != null) {
				event.getProcurementMethod().getProcurementMethod();
			}
			if (event.getProcurementCategories() != null) {
				event.getProcurementCategories().getProcurementCategories();
			}

			if (event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getDescription();
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionComment())) {
				for (RftSuspensionComment comment : event.getSuspensionComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getAwardComment())) {
				for (RftAwardComment comment : event.getAwardComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
				}
			}
		}
		return event;
	}

	@Override
	public RftEvent loadEventForSummeryPageForSupplierById(String id) {
		RftEvent event = rftEventDao.findBySupplierEventId(id);
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
				for (RftEventContact contact : event.getEventContacts()) {
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

		}
		return event;
	}

	private List<SupplierMaskingPojo> buildSupplierMaskingData(List<EventSupplier> supplierList, String eventId) {
		List<SupplierMaskingPojo> supplierMaskingList = new ArrayList<SupplierMaskingPojo>();
		List<RftEnvelop> env = rftEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFT);
		RftEvent event = getRftEventByeventId(eventId);
		for (EventSupplier eventSupplier : supplierList) {
			if (eventSupplier.getSupplier() != null) {
				SupplierMaskingPojo pojo = new SupplierMaskingPojo();
				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					pojo.setSupplierName(MaskUtils.maskName("SUPPLIER", eventSupplier.getSupplier().getId(), eventId));
				} else {
					pojo.setSupplierName(eventSupplier.getSupplier() != null ? eventSupplier.getSupplier().getCompanyName() : "");
				}

				List<SupplierMaskingCodePojo> supplierMaskingCodes = new ArrayList<SupplierMaskingCodePojo>();
				for (RftEnvelop rftEnvelop : env) {
					SupplierMaskingCodePojo codePojo = new SupplierMaskingCodePojo();
					codePojo.setEnevelopeName(rftEnvelop.getEnvelopTitle());
					codePojo.setMakedCode(MaskUtils.maskName(rftEnvelop.getPreFix(), eventSupplier.getSupplier().getId(), rftEnvelop.getId()));
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
	public List<RftEvent> getAllRftEventByTenantId(String tenantId) {

		return rftEventDao.getAllRftEventByTenantId(tenantId);
	}

	@Override
	public Event getEventByEventRefranceNo(String eventRefranceNo, String tenantId) {
		return rftEventDao.getEventByEventRefranceNo(eventRefranceNo, tenantId);
	}

	@Override
	public EventPojo loadEventPojoForSummeryPageForSupplierById(String eventId) {
		return rftEventDao.findEventPojoById(eventId);
	}

	@Override
	public EventPojo loadSupplierEventPojoForSummeryById(String eventId) {
		return rftEventDao.loadSupplierEventPojoForSummeryById(eventId);
	}

	@Override
	public List<DraftEventPojo> getAllEventsForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate) {
		List<DraftEventPojo> returnList = rftEventDao.getAllEventsForBuyerEventReport(loggedInUserTenantId, input, startDate, endDate);
		for (DraftEventPojo draftEventPojo : returnList) {
			switch (draftEventPojo.getType()) {
			case RFA:
				draftEventPojo.setEventCategories(getComasepratedIndustry(rfaEventDao.getIndustryCategoryListForRfaById(draftEventPojo.getId())));
				break;
			case RFI:
				draftEventPojo.setEventCategories(getComasepratedIndustry(rfiEventDao.getIndustryCategoryListForRfaById(draftEventPojo.getId())));
				break;
			case RFP:
				draftEventPojo.setEventCategories(getComasepratedIndustry(rfpEventDao.getIndustryCategoryListForRfaById(draftEventPojo.getId())));
				break;
			case RFQ:
				draftEventPojo.setEventCategories(getComasepratedIndustry(rfqEventDao.getIndustryCategoryListForRfaById(draftEventPojo.getId())));
				break;
			case RFT:
				draftEventPojo.setEventCategories(getComasepratedIndustry(rftEventDao.getIndustryCategoryListForRfaById(draftEventPojo.getId())));
				break;
			default:
				break;
			}
		}
		return returnList;
	}

	@Override
	public long findTotalAllEventsForBuyerEventReport(String loggedInUserTenantId) {
		return rftEventDao.findTotalAllEventsForBuyerEventReport(loggedInUserTenantId);
	}

	private String getComasepratedIndustry(List<IndustryCategory> industryCategory) {

		String eventCategories = "";
		if (CollectionUtil.isNotEmpty(industryCategory)) {
			for (IndustryCategory ic : industryCategory) {
				eventCategories += (ic.getName() != null ? ic.getName() : "") + ",";
			}
			if (eventCategories.length() > 0) {
				eventCategories = eventCategories.substring(0, eventCategories.length() - 1);
			}
		}
		return eventCategories;
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		return rftEventDao.getInvitedSupplierCount(eventId);
	}

	@Override
	public long getParticepatedSupplierCount(String eventId) {
		return rftEventDao.getParticepatedSupplierCount(eventId);
	}

	@Override
	public long getSubmitedSupplierCount(String eventId) {
		return rftEventDao.getSubmitedSupplierCount(eventId);
	}

	@Override
	public List<RftSupplierBq> getLowestSubmissionPrice(String eventId) {
		return rftEventDao.getLowestSubmissionPrice(eventId);
	}

	@Override
	public List<EventSupplierPojo> getSuppliersByStatus(String eventId) {
		return rftEventDao.getSuppliersByStatus(eventId);
	}

	/*
	 * @Override public RftSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId) { return
	 * rftEventDao.getFinalBidsForSuppliers(eventId, supplierId); }
	 */
	@Override
	public RftEnvelop getBqForEnvelope(String envelopeId) {
		return rftEventDao.getBqForEnvelope(envelopeId);
	}

	@Override
	public List<PublicEventPojo> getActivePublicEventsByTenantId(String buyerId) {
		LOG.info(" *** getActivePublicEventsByTenantId(buyerId) called  *** ");
		return rftEventDao.getActivePublicEventsByTenantId(buyerId);

		/*
		 * List<PublicEventPojo> list = rftEventDao.getActivePublicEventsByTenantId(buyerId); if
		 * (CollectionUtil.isNotEmpty(list)) { for (PublicEventPojo pevnt : list) { if (pevnt.getCountry() != null)
		 * pevnt.getCountry().getCountryName(); if(pevnt.getEventOwner() != null) pevnt.getEventOwner().getName();
		 * if(pevnt.getIndustryCategory() != null) pevnt.getIndustryCategory().getCode();
		 * pevnt.getIndustryCategory().getName(); if(pevnt.getBuyer() != null) pevnt.getBuyer().getId(); } }
		 * System.out.println("list:"+list); return list;
		 */

	}

	@Override
	public long findTotalFilteredActivePublicEventsList(String buyerId, TableDataInput input) {
		LOG.info("  findTotalFilteredActivePublicEventsList(buyerId, input) called ");
		return rftEventDao.findTotalFilteredActivePublicEventsList(buyerId, input);
	}

	@Override
	public long findTotalActivePublicEventsList(String buyerId) {
		LOG.info("  findTotalActivePublicEventsList(buyerId, input) called ");
		return rftEventDao.findTotalActivePublicEventsList(buyerId);
	}

	@Override
	public List<PublicEventPojo> findActivePublicEventsByTenantId(String buyerId, TableDataInput input) {
		LOG.info("  findActivePublicEventsByTenantId(buyerId, input) called ");
		return rftEventDao.findActivePublicEventsByTenantId(buyerId, input);
	}

	@Override
	public List<PublicEventPojo> findActivePublicEventsListByTenantId(String tenantId, TableDataInput input) {
		List<PublicEventPojo> list = rftEventDao.findActivePublicEventsListByTenantId(tenantId, input);
		List<PublicEventPojo> peSet = null;
		String strtimeZone = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
		TimeZone timezone = TimeZone.getDefault();
		if (StringUtils.checkString(strtimeZone).length() > 0) {
			timezone = TimeZone.getTimeZone(strtimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timezone);
		if (CollectionUtil.isNotEmpty(list)) {
			// boolean foundSiteVisitMeeting = false;
			peSet = new ArrayList<PublicEventPojo>();
			for (PublicEventPojo publicEventPojo : list) {
				PublicEventPojo pep = publicEventPojo;
				boolean found = false;
				String siteVisit = "";
				if (peSet.contains(publicEventPojo)) {
					pep = peSet.get(peSet.indexOf(publicEventPojo));
					found = true;

				}
				if (publicEventPojo.getMeetingType() != null && publicEventPojo.getMeetingType() == MeetingType.SITE_VISIT) {
					if (pep.getSiteVisitDate() != null && publicEventPojo.getSiteVisitDate().before(pep.getSiteVisitDate())) {
						pep.setSiteVisitDate(publicEventPojo.getSiteVisitDate());
						pep.setContactName(publicEventPojo.getContactName());
						pep.setContactNumber(publicEventPojo.getContactNumber());
					}
					siteVisit = sdf.format(publicEventPojo.getSiteVisitDate()) + "  " + publicEventPojo.getContactName() + "," + publicEventPojo.getContactNumber();
					pep.setSiteVisitMeetingDetails(siteVisit);
				}

				IndustryCategory ic = new IndustryCategory();
				ic.setCode(publicEventPojo.getCode());
				ic.setName(publicEventPojo.getName());

				if (pep.getIndustryCategories() == null) {
					pep.setIndustryCategories(new ArrayList<>());
				}

				if (!pep.getIndustryCategories().contains(ic)) {
					pep.getIndustryCategories().add(ic);
				}
				String eventCategories = "";
				for (IndustryCategory category : pep.getIndustryCategories()) {
					eventCategories += category.getName() + ",";
				}
				if (eventCategories.length() > 0) {
					eventCategories = eventCategories.substring(0, eventCategories.length() - 1);
				}
				pep.setIndustryCategoriesNames(eventCategories);
				pep.setParticipationFees(pep.getParticipationFees() != null ? pep.getParticipationFees()
						: BigDecimal.valueOf(0));
				pep.setCurrencyCode(pep.getCurrencyCode() == null ? "MYR" : pep.getCurrencyCode());

				if (!found) {
					peSet.add(pep);
				}
			}
		}
		return peSet;
	}

	@Override
	public String findTenantIdBasedOnEventIdAndEventType(String eventId, RfxTypes eventType) {
		return rftEventDao.findTenantIdBasedOnEventIdAndEventType(eventId, eventType);

	}

	@Override
	@SuppressWarnings("unused")
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

			String imgPath = context.getRealPath("resources/images");

			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(StringUtils.checkString(event.getEventName()));
			eventDetails.setEventStart(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			eventDetails.setType("RFT");
			eventDetails.setCompanyName(event.getCompanyName());
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			eventDetails.setVisibility(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			eventDetails.setPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			eventDetails.setDiliveryDate(event.getEventDeliveryDate() != null ? ddsdf.format(event.getEventDeliveryDate()) : "N/A");

			List<IndustryCategory> industryCategories = rftEventDao.getIndustryCategoriesForRftById(event.getId());

			if (CollectionUtil.isNotEmpty(industryCategories)) {
				List<IndustryCategoryPojo> categoryNames = new ArrayList<IndustryCategoryPojo>();
				for (IndustryCategory industryCategory : industryCategories) {
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(industryCategory.getName());
					categoryNames.add(ic);
				}
				eventDetails.setIndustryCategoryNames(categoryNames);
			}

			boolean siteVisit = rftMeetingService.isSiteVisitExist(event.getId());
			eventDetails.setSiteVisit(siteVisit);
			// Event Contact Details.
			List<RftEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RftEventContact contact : eventContacts) {
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

			List<Bq> bqs = rftEventBqDao.findRftBqbyEventId(event.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
			if (CollectionUtil.isNotEmpty(bqs)) {
				for (Bq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<RftBqItem> bqItems = rftBqItemDao.findBqItemsForBq(item.getId());
					List<EvaluationBqItemPojo> evaluationBqItem = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RftBqItem bqItem : bqItems) {
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
			LOG.error("Could not generate Evaluation Summary PDF Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return jasperPrint;
	}

	@Override
	public EventPojo getRftForPublicEventByeventId(String eventId) {
		return rftEventDao.getRftForPublicEventByeventId(eventId);
	}

	@Override
	public List<IndustryCategory> getIndustryCategoriesForRftById(String eventId) {
		List<IndustryCategory> industryCategoryList = rftEventDao.getIndustryCategoriesForRftById(eventId);
		if (CollectionUtil.isNotEmpty(industryCategoryList)) {
			for (IndustryCategory indCat : industryCategoryList) {
				indCat.getCode();
			}
		}
		return industryCategoryList;
	}

	@Override
	public EventPojo findMinMaxRatingsByEventId(String eventId, RfxTypes eventType) {
		return rftEventDao.findMinMaxRatingsByEventId(eventId, eventType);
	}

	@Override
	public boolean isIndustryCategoryMandatoryInEvent(String eventId, RfxTypes eventType) {
		return rftEventDao.isIndustryCategoryMandatoryInEvent(eventId, eventType);
	}

	@Override
	public long getAllEventsCountForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate) {
		return rftEventDao.getAllEventsCountForBuyerEventReport(loggedInUserTenantId, input, startDate, endDate);

	}

	@Override
	public int updateEventPushedDate(String eventId) {
		int result = rftEventDao.updateEventPushedDate(eventId);
		return result;
	}

	@Override
	public Double getAvarageBidPriceSubmitted(String id) {
		return rftEventDao.getAvarageBidPriceSubmitted(id);
	}

	@Override
	public List<String> getEventTeamMember(String eventId) {
		return rftEventDao.getEventTeamMember(eventId);
	}

	@Override
	public int updatePrPushDate(String eventId) {
		return rftEventDao.updatePrPushDate(eventId);
	}

	@Override
	public int updateEventAward(String eventId) {
		return rftEventDao.updateEventAward(eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String eventId, EventStatus status) {
		rftEventDao.updateImmediately(eventId, status);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlagImmediately(String eventId) {
		rftEventDao.updateEventStartMessageFlag(eventId);
	}

	@Override
	public long findTotalEventForBuyer(String tenantId, String userId) {
		return rftEventDao.findTotalEventForBuyer(tenantId, userId);
	}

	@Override
	public long findTotalAdminEventByTenantId(String tenantId) {
		return rftEventDao.findTotalAdminEventByTenantId(tenantId);
	}

	@Override
	public Integer getCountByEventId(String eventId) {
		return rftEventDao.getCountByEventId(eventId);
	}

	@Override
	public RftEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId) {
		return rftEventDao.getPlainEventByFormattedEventIdAndTenantId(eventId, tenantId);
	}

	@Override
	public Event loadRftEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUsers, List<User> suspApprovalUsers) throws CloneNotSupportedException {
		RftEvent event = rftEventDao.findByEventId(eventId);
		if (event != null) {
			if (event.getParticipationFeeCurrency() != null) {
				event.getParticipationFeeCurrency().getCurrencyName();
			}
			if (event.getDepositCurrency() != null) {
				event.getDepositCurrency().getCurrencyName();
			}
			if (event.getUnMaskedUser() != null) {
				event.getUnMaskedUser().getName();
				model.addAttribute("unMaskedUser", event.getUnMaskedUser());
			}
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RftEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RftUnMaskedUser user : event.getUnMaskedUsers()) {
					user.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RftEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getName();
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

			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RftTeamMember teamMember : event.getTeamMembers()) {
					assignedTeamMembers.add((User) teamMember.getUser().clone());
				}
			}
			// List<User> userList = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RftEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RftApprovalUser user : approver.getApprovalUsers()) {
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
				for (RftEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RftSuspensionApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();

							User us = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(),user.getUser().getTenantId(), user.getUser().isDeleted());

							if (!suspApprovalUsers.contains(us)) {
								suspApprovalUsers.add(us);
							}
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAdditionalDocument())) {
				for (AdditionalDocument additionalDocument : event.getAdditionalDocument()) {
					additionalDocument.getId();
					additionalDocument.getFileSizeInKb();
					additionalDocument.getCredContentType();
					additionalDocument.getFileName();
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
			buildRftEventDetailModal(model, eventId, event, null);
		}
		return event;
	}

	private void buildRftEventDetailModal(Model model, String eventId, Event event, List<User> userList) {

		((RftEvent) (event)).setEventPublishTime(event.getEventPublishDate());
		if (((RftEvent) (event)).getMinimumSupplierRating() != null) {
			((RftEvent) (event)).setMinimumSupplierRating(((RftEvent) (event)).getMinimumSupplierRating());
		}
		if (((RftEvent) (event)).getMaximumSupplierRating() != null) {
			((RftEvent) (event)).setMaximumSupplierRating(((RftEvent) (event)).getMaximumSupplierRating());
		}

		RftEventContact eventTContact = new RftEventContact();
		eventTContact.setEventId(event.getId());
		model.addAttribute("eventContact", eventTContact);
		// model.addAttribute("userList1", userList);
		model.addAttribute("eventContactsList", ((RftEvent) event).getEventContacts());
		model.addAttribute("reminderList", rftEventService.getAllRftEventReminderForEvent(eventId));
	}

	@Override
	public Event getSimpleEventDetailsById(String eventId) {
		return rftEventDao.getSimpleEventDetailsById(eventId);
	}

	@Override
	public String getEventOwnerId(String eventId) {
		return rftEventDao.getEventOwnerId(eventId);
	}

	@Override
	public boolean doValidateOwnerUserEnvople(String eventId) {
		return rftEventDao.doValidateOwnerUserEnvelope(eventId);
	}

	@Override
	public boolean doValidateOwnerUserUnmaskUser(String eventId) {
		return rftEventDao.doValidateOwnerUserUnmaskUser(eventId);
	}

	@Override
	public boolean doValidateOwnerUserApprover(String eventId) {
		return rftEventDao.doValidateOwnerUserApprover(eventId);
	}

	@Override
	public boolean doValidateOwnerUserTeamMember(String eventId) {
		return rftEventDao.doValidateOwnerUserTeamMember(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public JasperPrint generateShortEvaluationSummaryReport(String eventTypeStr, String eventId, String envelopeId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		try {

			RfxTypes eventType = RfxTypes.fromStringToRfxType(eventTypeStr);

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
			EvaluationSummaryPojo summaryPojo = new EvaluationSummaryPojo();
			BigDecimal savingPrice = null;

			Map<String, Object> parameters = new HashMap<String, Object>();
			SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
			generatedSdf.setTimeZone(timeZone);
			df.setTimeZone(timeZone);
			parameters.put("generatedOn", generatedSdf.format(new Date()));
			boolean isBidsAvailable = false;

			switch (eventType) {
			case RFA:
				RfaEvent rfaEvent = rfaEventDao.getRfaEventForShortSummary(eventId);
				String rfaVisibility = (rfaEvent.getEventVisibility() != null ? rfaEvent.getEventVisibility().toString() + " event. " : "");

				boolean isMask = false;
				RfaEnvelop rfaEnvelope = null;
				if (rfaEvent.getViewSupplerName() != null && !rfaEvent.getViewSupplerName() && !rfaEvent.getDisableMasking()) {
					isMask = true;
					rfaEnvelope = rfaEnvelopDao.findById(envelopeId);
				}

				final int dec = Integer.parseInt(rfaEvent.getDecimal());
				DecimalFormat formatter = null;
				switch (dec) {
				case 1:
					formatter = new DecimalFormat("#,###,###,##0.0");
					break;
				case 2:
					formatter = new DecimalFormat("#,###,###,##0.00");
					break;
				case 3:
					formatter = new DecimalFormat("#,###,###,##0.000");
					break;
				case 4:
					formatter = new DecimalFormat("#,###,###,##0.0000");
					break;
				case 5:
					formatter = new DecimalFormat("#,###,###,##0.00000");
					break;
				case 6:
					formatter = new DecimalFormat("#,###,###,##0.000000");
					break;
				default:
					formatter = new DecimalFormat("#,###,###,##0.00");
					break;
				}

				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				Boolean prebidFlag = false;
				if (auctionRules != null && auctionRules.getPrebidAsFirstBid() == Boolean.TRUE) {
					prebidFlag = true;
				}
				long invited = rfaEventService.getInvitedSupplierCount(eventId);
				// long participted = rfaEventService.getParticepatedSupplierCount(eventId);
				long numberOfBidForRfa = rfaEventService.getNumberOfBidForRfa(eventId);
				long submited = rfaEventService.getSubmitedSupplierCount(eventId);
				List<RfaSupplierBq> rfaSupplierBqLowest = rfaEventService.getLowestSubmissionPrice(eventId, prebidFlag);
				List<RfaSupplierBq> rfaSupplierBqHighest = rfaEventService.getHighestSubmissionPrice(eventId, prebidFlag);
				Boolean auctionRuleValue = rfaEventService.findAuctionRulesWithEventId(eventId);

				RfaEnvelop rfaBqEnvelope = rfaEventService.getBqForEnvelope(envelopeId);
				boolean isRfaBqAvailableForEnv = false;
				isRfaBqAvailableForEnv = CollectionUtil.isNotEmpty(rfaBqEnvelope.getBqList()) ? true : false;

				BigDecimal rfaLowestRevised = BigDecimal.ZERO;
				String supplierCompanyNameLowest = "";
				if (CollectionUtil.isNotEmpty(rfaSupplierBqLowest)) {
					rfaLowestRevised = (rfaSupplierBqLowest.get(0) != null ? rfaSupplierBqLowest.get(0).getTotalAfterTax() : BigDecimal.ZERO);
					if (isMask) {
						supplierCompanyNameLowest = MaskUtils.maskName(rfaEnvelope.getPreFix(), rfaSupplierBqLowest.get(0).getSupplier().getId(), rfaEnvelope.getId());
					} else {
						supplierCompanyNameLowest = (rfaSupplierBqLowest.get(0) != null ? rfaSupplierBqLowest.get(0).getSupplier().getCompanyName() : "N/A");
					}
				}
				BigDecimal rfaHighestRevised = BigDecimal.ZERO;
				String supplierCompanyNameHighest = "";
				if (CollectionUtil.isNotEmpty(rfaSupplierBqHighest)) {
					rfaHighestRevised = (rfaSupplierBqHighest.get(0) != null ? rfaSupplierBqHighest.get(0).getTotalAfterTax() : BigDecimal.ZERO);
					if (isMask) {
						supplierCompanyNameHighest = MaskUtils.maskName(rfaEnvelope.getPreFix(), rfaSupplierBqHighest.get(0).getSupplier().getId(), rfaEnvelope.getId());
					} else {
						supplierCompanyNameHighest = (rfaSupplierBqHighest.get(0) != null ? rfaSupplierBqHighest.get(0).getSupplier().getCompanyName() : "N/A");
					}
				}

				boolean showNote = false;
				// *****************Supplier Table List*****************
				List<SupplierListPojo> listRfa = new ArrayList<>();
				List<RfaEventSupplier> supplierList = rfaEventService.getSuppliersByStatus(eventId);

				Set<RfaEventSupplier> rfaSupplierSet = CollectionUtil.isNotEmpty(supplierList) ? new LinkedHashSet<>(supplierList) : new LinkedHashSet<>();
				supplierList = new ArrayList<>(rfaSupplierSet);

				if (isRfaBqAvailableForEnv == true) {
					if (CollectionUtil.isNotEmpty(supplierList)) {
						for (RfaEventSupplier rfaEventSupplier : supplierList) {
							if (rfaEventSupplier.getNumberOfBids() > 0) {

								SupplierListPojo suppPojo = new SupplierListPojo();
								if (auctionRuleValue != null && rfaEventSupplier.getRevisedBidSubmitted() != null && !rfaEventSupplier.getRevisedBidSubmitted()) {
									showNote = true;
								}

								suppPojo.setRevisedBidSubmitted(rfaEventSupplier.getRevisedBidSubmitted() != null ? rfaEventSupplier.getRevisedBidSubmitted() : Boolean.TRUE);
								if (auctionRuleValue != null && suppPojo.isRevisedBidSubmitted() == false) {
									if (isMask) {
										suppPojo.setSupplierName_NotRevisedBq(MaskUtils.maskName(rfaEnvelope.getPreFix(), rfaEventSupplier.getSupplier().getId(), rfaEnvelope.getId()) + "*".replaceAll("&", "&amp;"));
									} else {
										suppPojo.setSupplierName_NotRevisedBq(rfaEventSupplier.getSupplier().getCompanyName() + "*" .replaceAll("&", "&amp;"));
									}
								} else {
									if (isMask) {
										suppPojo.setSupplierName(MaskUtils.maskName(rfaEnvelope.getPreFix(), rfaEventSupplier.getSupplier().getId(), rfaEnvelope.getId()).replaceAll("&", "&amp;"));
									} else {
										suppPojo.setSupplierName(rfaEventSupplier.getSupplier().getCompanyName().replaceAll("&", "&amp;"));
									}
								}
								suppPojo.setNumberOfBids(rfaEventSupplier.getNumberOfBids() != null ? rfaEventSupplier.getNumberOfBids() : new Integer(0));
								String supplierId = rfaEventSupplier.getSupplier().getId();
								RfaSupplierBq finalBid = rfaEventService.getFinalBidsForSuppliers(eventId, supplierId);
								suppPojo.setSubmissionPrice(finalBid != null ? (finalBid.getTotalAfterTax() != null ? new BigDecimal(formatter.format(finalBid.getTotalAfterTax()).replaceAll(",", "")) : BigDecimal.ZERO) : BigDecimal.ZERO);
								suppPojo.setShowNote(showNote);
								listRfa.add(suppPojo);
								showNote = false;
								isBidsAvailable = true;
							}
						}
					}
				}
				if (rfaEvent.getAuctionType() == AuctionType.FORWARD_DUTCH || rfaEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || rfaEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
					if (rfaEvent.getBudgetAmount() != null) {
						savingPrice = rfaEvent.getBudgetAmount().subtract(rfaHighestRevised);
					}
					String rfaForwardSavingPriceStr = (rfaEvent.getBudgetAmount() != null ? (savingPrice != null ? formatter.format(savingPrice) : "N/A") : "N/A");

					summaryPojo.setEstimationDescription("The estimate for the auction is " + ((rfaEvent.getBaseCurrency() != null && rfaEvent.getBaseCurrency().getCurrencyCode() != null) ? rfaEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfaEvent.getBudgetAmount() != null ? formatter.format(rfaEvent.getBudgetAmount()) : "N/A") + " and the highest bids received is " + ((rfaEvent.getBaseCurrency() != null && rfaEvent.getBaseCurrency().getCurrencyCode() != null) ? rfaEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfaHighestRevised.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rfaHighestRevised)) + " from " + (supplierCompanyNameHighest.length() == 0 ? "N/A" : supplierCompanyNameHighest) + ". Savings from this " + eventType + " is estimated to be " + ((rfaEvent.getBaseCurrency() != null && rfaEvent.getBaseCurrency().getCurrencyCode() != null) ? rfaEvent.getBaseCurrency().getCurrencyCode() : "") + " " + rfaForwardSavingPriceStr + ".");
					summaryPojo.setResultDescription("Based on the above results, the recommendation is to award to the highest bidder that met all requirements which is " + (supplierCompanyNameHighest.length() == 0 ? "N/A" : supplierCompanyNameHighest) + " at the bid amount of " + ((rfaEvent.getBaseCurrency() != null && rfaEvent.getBaseCurrency().getCurrencyCode() != null) ? rfaEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfaHighestRevised.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rfaHighestRevised)) + ".");
				}

				if (rfaEvent.getAuctionType() == AuctionType.REVERSE_ENGISH || rfaEvent.getAuctionType() == AuctionType.REVERSE_DUTCH || rfaEvent.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					if (rfaEvent.getBudgetAmount() != null) {
						savingPrice = rfaEvent.getBudgetAmount().subtract(rfaLowestRevised);
					}
					String rfaReverseSavingPriceStr = (rfaEvent.getBudgetAmount() != null ? (savingPrice != null ? formatter.format(savingPrice) : "N/A") : "N/A");

					summaryPojo.setEstimationDescription("The estimate for the auction is " + ((rfaEvent.getBaseCurrency() != null && rfaEvent.getBaseCurrency().getCurrencyCode() != null) ? rfaEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfaEvent.getBudgetAmount() != null ? formatter.format(rfaEvent.getBudgetAmount()) : "N/A") + " and the lowest bids received is " + ((rfaEvent.getBaseCurrency() != null && rfaEvent.getBaseCurrency().getCurrencyCode() != null) ? rfaEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfaLowestRevised.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rfaLowestRevised)) + " from " + (supplierCompanyNameLowest.length() == 0 ? "N/A" : supplierCompanyNameLowest) + ". Savings from this " + eventType + " is estimated to be " + ((rfaEvent.getBaseCurrency() != null && rfaEvent.getBaseCurrency().getCurrencyCode() != null) ? rfaEvent.getBaseCurrency().getCurrencyCode() : "") + " " + rfaReverseSavingPriceStr + ".");
					summaryPojo.setResultDescription("Based on the above results, the recommendation is to award to the lowest bidder that met all requirements which is " + (supplierCompanyNameLowest.length() == 0 ? "N/A" : supplierCompanyNameLowest) + " at the bid amount of " + ((rfaEvent.getBaseCurrency() != null && rfaEvent.getBaseCurrency().getCurrencyCode() != null) ? rfaEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfaLowestRevised.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rfaLowestRevised)) + ".");
				}
				summaryPojo.setBidsSubmitted(isBidsAvailable);
				summaryPojo.setDecimal(rfaEvent.getDecimal());
				summaryPojo.setBqAvailable(isRfaBqAvailableForEnv);
				summaryPojo.setSupplierList(listRfa);
				summaryPojo.setEventType(eventType.name());
				summaryPojo.setCompanyName(rfaEvent.getEventOwner().getBuyer().getCompanyName());
				summaryPojo.setTitle("RFA Short Submission Summary");
				summaryPojo.setTimelineDescription("The auction reference number " + rfaEvent.getEventId() + " is based on eProcurement " + rfaEvent.getAuctionType() + " method using " + rfaVisibility + "The auction started on " + df.format(rfaEvent.getEventStart()) + " and ended on " + df.format(rfaEvent.getEventEnd()) + ".");
				summaryPojo.setParticipationDescription("Total of " + invited + " companies were invited and total of " + submited + " companies participated. Total of " + numberOfBidForRfa + " number of bids were received from all the companies that participated.");

				break;

			case RFI:
				RfiEvent rfiEvent = rfiEventDao.getRfiEventForShortSummary(eventId);
				String rfiVisibility = (rfiEvent.getEventVisibility() != null ? rfiEvent.getEventVisibility().toString() + "event. " : "");

				invited = rfiEventService.getInvitedSupplierCount(eventId);
				// long participted = rfiEventService.getParticepatedSupplierCount(eventId);
				submited = rfiEventService.getSubmitedSupplierCount(eventId);

				summaryPojo.setDecimal(rfiEvent.getDecimal());
				summaryPojo.setEventType(eventType.name());
				summaryPojo.setTitle("RFI Short Submission Summary");
				summaryPojo.setCompanyName(rfiEvent.getEventOwner().getBuyer().getCompanyName());
				summaryPojo.setTimelineDescription("The " + eventType.getValue() + " reference number " + rfiEvent.getEventId() + " is based on eProcurement RFI method using " + rfiVisibility + "The RFI started on " + df.format(rfiEvent.getEventStart()) + " and ended on " + df.format(rfiEvent.getEventEnd()) + ".");
				summaryPojo.setParticipationDescription("Total of " + invited + " companies were invited and total of " + submited + " companies participated. Total of " + submited + " number of submissions were received from all the companies that participated.");

				break;

			case RFT:
				RftEvent rftEvent = rftEventDao.getRftEventForShortSummary(eventId);
				String rftVisibility = (rftEvent.getEventVisibility() != null ? rftEvent.getEventVisibility().toString() + " event. " : "");

				isMask = false;
				RftEnvelop rftEnvelope = null;
				if (rftEvent.getViewSupplerName() != null && !rftEvent.getViewSupplerName() && !rftEvent.getDisableMasking()) {
					isMask = true;
					rftEnvelope = rftEnvelopDao.findById(envelopeId);
				}

				final int dec1 = Integer.parseInt(rftEvent.getDecimal());
				formatter = null;
				switch (dec1) {
				case 1:
					formatter = new DecimalFormat("#,###,###,##0.0");
					break;

				case 2:
					formatter = new DecimalFormat("#,###,###,##0.00");
					break;

				case 3:
					formatter = new DecimalFormat("#,###,###,##0.000");
					break;

				case 4:
					formatter = new DecimalFormat("#,###,###,##0.0000");
					break;
				case 5:
					formatter = new DecimalFormat("#,###,###,##0.00000");
					break;
				case 6:
					formatter = new DecimalFormat("#,###,###,##0.000000");
					break;
				default:
					formatter = new DecimalFormat("#,###,###,##0.00");
					break;
				}

				// participted = rftEventService.getParticepatedSupplierCount(eventId);
				invited = rftEventService.getInvitedSupplierCount(eventId);
				submited = rftEventService.getSubmitedSupplierCount(eventId);
				// List<RftSupplierBq> rftSupplierBqLowest = rftEventService.getLowestSubmissionPrice(eventId);

				RftEnvelop rftBqEnvelope = rftEventService.getBqForEnvelope(envelopeId);
				boolean isRftBqAvailableForEnv = false;
				isRftBqAvailableForEnv = CollectionUtil.isNotEmpty(rftBqEnvelope.getBqList()) ? true : false;

				BigDecimal rftGrandTotal = BigDecimal.ZERO;
				String rftSupplierCompanyName = "";
				/*
				 * if (CollectionUtil.isNotEmpty(rftSupplierBqLowest)) { for (RftSupplierBq rftSupplierBq :
				 * rftSupplierBqLowest) { } rftGrandTotal = (rftSupplierBqLowest.get(0) != null ?
				 * rftSupplierBqLowest.get(0).getTotalAfterTax() : BigDecimal.ZERO); if (isMask) {
				 * rftSupplierCompanyName = MaskUtils.maskName(rftEnvelope.getPreFix(),
				 * rftSupplierBqLowest.get(0).getSupplier().getId(), rftEnvelope.getId()); } else {
				 * rftSupplierCompanyName = (rftSupplierBqLowest.get(0) != null ?
				 * rftSupplierBqLowest.get(0).getSupplier().getCompanyName() : ""); } }
				 */

				List<SupplierListPojo> listRft = new ArrayList<>();
				List<EventSupplierPojo> supplierListRft = rftEventService.getSuppliersByStatus(eventId);
				if (isRftBqAvailableForEnv == true) {
					if (CollectionUtil.isNotEmpty(supplierListRft)) {

						rftGrandTotal = (supplierListRft.get(0) != null ? supplierListRft.get(0).getTotalBqPrice() : BigDecimal.ZERO);
						if (isMask) {
							rftSupplierCompanyName = MaskUtils.maskName(rftEnvelope.getPreFix(), supplierListRft.get(0).getSupplierId(), rftEnvelope.getId());
						} else {
							rftSupplierCompanyName = (supplierListRft.get(0) != null ? supplierListRft.get(0).getCompanyName() : "N/A");
						}

						if (rftEvent.getBudgetAmount() != null) {
							savingPrice = rftEvent.getBudgetAmount().subtract(rftGrandTotal);
						}
						// For table
						for (EventSupplierPojo rftEventSupplier : supplierListRft) {
							SupplierListPojo suppPojo = new SupplierListPojo();
							if (isMask) {
								suppPojo.setSupplierName(MaskUtils.maskName(rftEnvelope.getPreFix(), rftEventSupplier.getSupplierId(), rftEnvelope.getId()).replaceAll("&", "&amp;"));
							} else {
								suppPojo.setSupplierName(rftEventSupplier.getCompanyName() != null ? rftEventSupplier.getCompanyName().replaceAll("&", "&amp;") : "N/A");
							}
							suppPojo.setSubmissionPrice(rftEventSupplier != null ? (rftEventSupplier.getTotalBqPrice() != null ? new BigDecimal(formatter.format(rftEventSupplier.getTotalBqPrice()).replaceAll(",", "")) : BigDecimal.ZERO) : BigDecimal.ZERO);
							suppPojo.setRevisedBidSubmitted(Boolean.TRUE);
							listRft.add(suppPojo);
						}
					}
				}
				summaryPojo.setBidsSubmitted(true);
				summaryPojo.setDecimal(rftEvent.getDecimal());
				String rftSavingPriceStr = (rftEvent.getBudgetAmount() != null ? (savingPrice != null ? formatter.format(savingPrice) : "N/A") : "N/A");
				summaryPojo.setBqAvailable(isRftBqAvailableForEnv);
				summaryPojo.setSupplierList(listRft);
				summaryPojo.setEventType(eventType.name());
				summaryPojo.setCompanyName(rftEvent.getEventOwner().getBuyer().getCompanyName());
				summaryPojo.setTitle("RFT Short Submission Summary");
				summaryPojo.setTimelineDescription("The " + eventType.getValue() + " reference number " + rftEvent.getEventId() + " is based on eProcurement RFT method using " + rftVisibility + "The RFT started on " + df.format(rftEvent.getEventStart()) + " and ended on " + df.format(rftEvent.getEventEnd()) + ".");
				summaryPojo.setParticipationDescription("Total of " + invited + " companies were invited and total of " + submited + " companies participated. Total of " + submited + " number of submissions were received from all the companies that participated.");
				summaryPojo.setEstimationDescription("The estimate for the " + eventType.name() + " is " + ((rftEvent.getBaseCurrency() != null && rftEvent.getBaseCurrency().getCurrencyCode() != null) ? rftEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rftEvent.getBudgetAmount() != null ? formatter.format(rftEvent.getBudgetAmount()) : "N/A") + " and the lowest submissions received is " + ((rftEvent.getBaseCurrency() != null && rftEvent.getBaseCurrency().getCurrencyCode() != null) ? rftEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rftGrandTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rftGrandTotal)) + " from " + (rftSupplierCompanyName.length() == 0 ? "N/A" : rftSupplierCompanyName) + ". Savings from this " + eventType + " is estimated to be " + ((rftEvent.getBaseCurrency() != null && rftEvent.getBaseCurrency().getCurrencyCode() != null) ? rftEvent.getBaseCurrency().getCurrencyCode() : "") + " " + rftSavingPriceStr + ".");
				summaryPojo.setResultDescription("Based on the above results, the recommendation is to award to the lowest submission that met all requirements which is " + (rftSupplierCompanyName.length() == 0 ? "N/A" : rftSupplierCompanyName) + " at the submission amount of " + ((rftEvent.getBaseCurrency() != null && rftEvent.getBaseCurrency().getCurrencyCode() != null) ? rftEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rftGrandTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rftGrandTotal)) + ".");

				break;

			case RFQ:
				RfqEvent rfqEvent = rfqEventDao.getRfqEventForShortSummary(eventId);
				String rfqVisibility = (rfqEvent.getEventVisibility() != null ? rfqEvent.getEventVisibility().toString() + " event. " : "");

				isMask = false;
				RfqEnvelop rfqEnvelope = null;
				if (rfqEvent.getViewSupplerName() != null && !rfqEvent.getViewSupplerName() && !rfqEvent.getDisableMasking()) {
					isMask = true;
					rfqEnvelope = rfqEnvelopDao.findById(envelopeId);
				}

				final int dec2 = Integer.parseInt((StringUtils.checkString(rfqEvent.getDecimal()).length() > 0 ? rfqEvent.getDecimal() : "2"));
				formatter = null;
				switch (dec2) {
				case 1:
					formatter = new DecimalFormat("#,###,###,##0.0");
					break;

				case 2:
					formatter = new DecimalFormat("#,###,###,##0.00");
					break;

				case 3:
					formatter = new DecimalFormat("#,###,###,##0.000");
					break;

				case 4:
					formatter = new DecimalFormat("#,###,###,##0.0000");
					break;
				case 5:
					formatter = new DecimalFormat("#,###,###,##0.00000");
					break;
				case 6:
					formatter = new DecimalFormat("#,###,###,##0.000000");
					break;
				default:
					formatter = new DecimalFormat("#,###,###,##0.00");
					break;
				}
				invited = rfqEventService.getInvitedSupplierCount(eventId);
				// participted = rfqEventService.getParticepatedSupplierCount(eventId);
				submited = rfqEventService.getSubmitedSupplierCount(eventId);
				// List<RfqSupplierBq> rfqSupplierBqLowest = rfqEventService.getLowestSubmissionPrice(eventId);

				RfqEnvelop rfqBqEnvelope = rfqEventService.getBqForEnvelope(envelopeId);
				boolean isRfqBqAvailableForEnv = false;
				isRfqBqAvailableForEnv = CollectionUtil.isNotEmpty(rfqBqEnvelope.getBqList()) ? true : false;

				BigDecimal rfqGrandTotal = BigDecimal.ZERO;
				String rfqSupplierCompanyName = "";

				List<SupplierListPojo> listRfq = new ArrayList<>();
				List<EventSupplierPojo> supplierListRfq = rfqEventService.getSuppliersByStatus(eventId);
				if (isRfqBqAvailableForEnv == true) {
					if (CollectionUtil.isNotEmpty(supplierListRfq)) {
						rfqGrandTotal = (supplierListRfq.get(0) != null ? supplierListRfq.get(0).getTotalBqPrice() : BigDecimal.ZERO);

						if (isMask) {
							rfqSupplierCompanyName = (MaskUtils.maskName(rfqEnvelope.getPreFix(), supplierListRfq.get(0).getSupplierId(), rfqEnvelope.getId()));
						} else {
							rfqSupplierCompanyName = (supplierListRfq.get(0).getCompanyName() != null ? supplierListRfq.get(0).getCompanyName() : "N/A");
						}

						if (rfqEvent.getBudgetAmount() != null) {
							savingPrice = rfqEvent.getBudgetAmount().subtract(rfqGrandTotal);
						}

						for (EventSupplierPojo rfqEventSupplier : supplierListRfq) {
							SupplierListPojo suppPojo = new SupplierListPojo();
							if (isMask) {
								suppPojo.setSupplierName(MaskUtils.maskName(rfqEnvelope.getPreFix(), rfqEventSupplier.getSupplierId(), rfqEnvelope.getId()).replaceAll("&", "&amp;"));
							} else {
								suppPojo.setSupplierName(rfqEventSupplier.getCompanyName() != null ? rfqEventSupplier.getCompanyName().replaceAll("&", "&amp;") : "N/A");
							}
							suppPojo.setSubmissionPrice(rfqEventSupplier != null ? (rfqEventSupplier.getTotalBqPrice() != null ? new BigDecimal(formatter.format(rfqEventSupplier.getTotalBqPrice()).replaceAll(",", "")) : BigDecimal.ZERO) : BigDecimal.ZERO);
							suppPojo.setRevisedBidSubmitted(Boolean.TRUE);
							listRfq.add(suppPojo);
						}
					}
				}
				summaryPojo.setBidsSubmitted(true);
				summaryPojo.setDecimal(rfqEvent.getDecimal());
				String rfqSavingPriceStr = (rfqEvent.getBudgetAmount() != null ? (savingPrice != null ? formatter.format(savingPrice) : "N/A") : "N/A");
				summaryPojo.setBqAvailable(isRfqBqAvailableForEnv);
				summaryPojo.setSupplierList(listRfq);
				summaryPojo.setEventType(eventType.name());
				summaryPojo.setCompanyName(rfqEvent.getEventOwner().getBuyer().getCompanyName());
				summaryPojo.setTitle("RFQ Short Submission Summary");
				summaryPojo.setTimelineDescription("The " + eventType.getValue() + " reference number " + rfqEvent.getEventId() + " is based on eProcurement RFQ method using " + rfqVisibility + "The RFQ started on " + df.format(rfqEvent.getEventStart()) + " and ended on " + df.format(rfqEvent.getEventEnd()) + ".");
				summaryPojo.setParticipationDescription("Total of " + invited + " companies were invited and total of " + submited + " companies participated. Total of " + submited + " number of submissions were received from all the companies that participated.");
				summaryPojo.setEstimationDescription("The estimate for the " + eventType.name() + " is " + ((rfqEvent.getBaseCurrency() != null && rfqEvent.getBaseCurrency().getCurrencyCode() != null) ? rfqEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfqEvent.getBudgetAmount() != null ? formatter.format(rfqEvent.getBudgetAmount()) : BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP)) + " and the lowest submissions received is " + ((rfqEvent.getBaseCurrency() != null && rfqEvent.getBaseCurrency().getCurrencyCode() != null) ? rfqEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfqGrandTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rfqGrandTotal)) + " from " + (rfqSupplierCompanyName.length() == 0 ? "N/A" : rfqSupplierCompanyName) + ". Savings from this " + eventType + " is estimated to be " + ((rfqEvent.getBaseCurrency() != null && rfqEvent.getBaseCurrency().getCurrencyCode() != null) ? rfqEvent.getBaseCurrency().getCurrencyCode() : "") + " " + rfqSavingPriceStr + ".");
				summaryPojo.setResultDescription("Based on the above results, the recommendation is to award to the lowest submission that met all requirements which is " + (rfqSupplierCompanyName.length() == 0 ? "N/A" : rfqSupplierCompanyName) + " at the submission amount of " + ((rfqEvent.getBaseCurrency() != null && rfqEvent.getBaseCurrency().getCurrencyCode() != null) ? rfqEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfqGrandTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rfqGrandTotal)) + ".");

				break;

			case RFP:
				RfpEvent rfpEvent = rfpEventDao.getRfpEventForShortSummary(eventId);
				String rfpVisibility = (rfpEvent.getEventVisibility() != null ? rfpEvent.getEventVisibility().toString() + " event. " : "");

				isMask = false;
				RfpEnvelop rfpEnvelope = null;
				if (rfpEvent.getViewSupplerName() != null && !rfpEvent.getViewSupplerName() && !rfpEvent.getDisableMasking()) {
					isMask = true;
					rfpEnvelope = rfpEnvelopDao.findById(envelopeId);
				}

				final int dec3 = Integer.parseInt(rfpEvent.getDecimal());
				formatter = null;
				switch (dec3) {
				case 1:
					formatter = new DecimalFormat("#,###,###,##0.0");
					break;

				case 2:
					formatter = new DecimalFormat("#,###,###,##0.00");
					break;

				case 3:
					formatter = new DecimalFormat("#,###,###,##0.000");
					break;

				case 4:
					formatter = new DecimalFormat("#,###,###,##0.0000");
					break;
				case 5:
					formatter = new DecimalFormat("#,###,###,##0.00000");
					break;
				case 6:
					formatter = new DecimalFormat("#,###,###,##0.000000");
					break;
				default:
					formatter = new DecimalFormat("#,###,###,##0.00");
					break;
				}
				invited = rfpEventService.getInvitedSupplierCount(eventId);
				// participted = rfpEventService.getParticepatedSupplierCount(eventId);
				submited = rfpEventService.getSubmitedSupplierCount(eventId);
				// List<RfpSupplierBq> rfpSupplierBqLowest = rfpEventService.getLowestSubmissionPrice(eventId);

				RfpEnvelop rfpBqEnvelope = rfpEventService.getBqForEnvelope(envelopeId);
				boolean isRfpBqAvailableForEnv = false;
				isRfpBqAvailableForEnv = CollectionUtil.isNotEmpty(rfpBqEnvelope.getBqList()) ? true : false;

				BigDecimal rfpGrandTotal = BigDecimal.ZERO;
				String rfpSupplierCompanyName = "";
				List<SupplierListPojo> listRfp = new ArrayList<>();
				List<EventSupplierPojo> supplierListRfp = rfpEventService.getSuppliersByStatus(eventId);
				if (isRfpBqAvailableForEnv == true) {
					if (CollectionUtil.isNotEmpty(supplierListRfp)) {

						rfpGrandTotal = (supplierListRfp.get(0) != null ? supplierListRfp.get(0).getTotalBqPrice() : BigDecimal.ZERO);
						if (isMask) {
							rfpSupplierCompanyName = (MaskUtils.maskName(rfpEnvelope.getPreFix(), supplierListRfp.get(0).getSupplierId(), rfpEnvelope.getId()));
						} else {
							rfpSupplierCompanyName = (supplierListRfp.get(0) != null ? supplierListRfp.get(0).getCompanyName() : "N/A");
						}

						if (rfpEvent.getBudgetAmount() != null) {
							savingPrice = rfpEvent.getBudgetAmount().subtract(rfpGrandTotal);
						}

						for (EventSupplierPojo rfpEventSupplier : supplierListRfp) {
							SupplierListPojo suppPojo = new SupplierListPojo();

							if (isMask) {
								suppPojo.setSupplierName(MaskUtils.maskName(rfpEnvelope.getPreFix(), rfpEventSupplier.getSupplierId(), rfpEnvelope.getId()).replaceAll("&", "&amp;"));
							} else {
								suppPojo.setSupplierName(rfpEventSupplier.getCompanyName() != null ? rfpEventSupplier.getCompanyName().replaceAll("&", "&amp;") : "N/A");
							}
							suppPojo.setSubmissionPrice(rfpEventSupplier != null ? (rfpEventSupplier.getTotalBqPrice() != null ? new BigDecimal(formatter.format(rfpEventSupplier.getTotalBqPrice()).replaceAll(",", "")) : BigDecimal.ZERO) : BigDecimal.ZERO);
							suppPojo.setRevisedBidSubmitted(Boolean.TRUE);
							listRfp.add(suppPojo);
						}
					}
				}
				summaryPojo.setBidsSubmitted(true);
				summaryPojo.setDecimal(rfpEvent.getDecimal());
				String rfpSavingPriceStr = (rfpEvent.getBudgetAmount() != null ? (savingPrice != null ? formatter.format(savingPrice) : "N/A") : "N/A");
				summaryPojo.setBqAvailable(isRfpBqAvailableForEnv);
				summaryPojo.setSupplierList(listRfp);
				summaryPojo.setEventType(eventType.name());
				summaryPojo.setCompanyName(rfpEvent.getEventOwner().getBuyer().getCompanyName());
				summaryPojo.setTitle("RFP Short Submission Summary");
				summaryPojo.setTimelineDescription("The " + eventType.getValue() + " reference number " + rfpEvent.getEventId() + " is based on eProcurement RFP method using " + rfpVisibility + "The RFP started on " + df.format(rfpEvent.getEventStart()) + " and ended on " + df.format(rfpEvent.getEventEnd()) + ".");
				summaryPojo.setParticipationDescription("Total of " + invited + " companies were invited and total of " + submited + " companies participated. Total of " + submited + " number of submissions were received from all the companies that participated.");
				summaryPojo.setEstimationDescription("The estimate for the " + eventType.name() + " is " + ((rfpEvent.getBaseCurrency() != null && rfpEvent.getBaseCurrency().getCurrencyCode() != null) ? rfpEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfpEvent.getBudgetAmount() != null ? formatter.format(rfpEvent.getBudgetAmount()) : "N/A") + " and the lowest submissions received is " + ((rfpEvent.getBaseCurrency() != null && rfpEvent.getBaseCurrency().getCurrencyCode() != null) ? rfpEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfpGrandTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rfpGrandTotal)) + " from " + (rfpSupplierCompanyName.length() == 0 ? "N/A" : rfpSupplierCompanyName) + ". Savings from this " + eventType + " is estimated to be " + ((rfpEvent.getBaseCurrency() != null && rfpEvent.getBaseCurrency().getCurrencyCode() != null) ? rfpEvent.getBaseCurrency().getCurrencyCode() : "") + " " + rfpSavingPriceStr + ".");
				summaryPojo.setResultDescription("Based on the above results, the recommendation is to award to the lowest submission that met all requirements which is " + (rfpSupplierCompanyName.length() == 0 ? "N/A" : rfpSupplierCompanyName) + " at the submission amount of " + ((rfpEvent.getBaseCurrency() != null && rfpEvent.getBaseCurrency().getCurrencyCode() != null) ? rfpEvent.getBaseCurrency().getCurrencyCode() : "") + " " + (rfpGrandTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP) : formatter.format(rfpGrandTotal)) + ".");

				break;
			default:
				break;
			}
			List<EvaluationSummaryPojo> shortSummary = new ArrayList<EvaluationSummaryPojo>();

			parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			try {
				Resource resource = applicationContext.getResource("classpath:reports/ShortEvaluationSummary.jasper");

				File jasperfile = resource.getFile();
				if (eventType.equals("RFA")) {
					summaryPojo.setSummaryLowHighestTitle("The summary of bids received in order of lowest to highest:");
				} else {
					summaryPojo.setSummaryLowHighestTitle("The summary of submissions received in order of lowest to highest:");
				}

				shortSummary.add(summaryPojo);
				parameters.put("SHORTEVALUATION_SUMMARY", shortSummary);
				JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(shortSummary, false);
				jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

			} catch (Exception e) {
				LOG.error("Could not generate Short Evaluation Summary PDF Report. " + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Could not Download ShortEvaluation Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	@Override
	public List<RftEventAwardAudit> getRftEventAwardByEventId(String eventId) {
		return rftEventDao.getRftEventAwardByEventId(eventId);
	}

	@Override
	public JasperPrint getEventAuditPdf(RftEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rftEventDao.findByEventId(event.getId());
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
			eventDetails.setType("RFT");
			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<RftEventAudit> eventAudit = rftEventAuditDao.getRftEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RftEventAudit ra : eventAudit) {
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
		return rftEventDao.isDefaultPreSetEnvlope(eventId);
	}

	@Override
	public byte[] buildFaxForSupplierPdf(String supplierName, String buyerName, String eventName, BusinessUnit businessUnit, RfxTypes rfxTypes, String refrance, EventStatus eventStatus) {
		try {

			Resource resource = applicationContext.getResource("classpath:reports/Fax_Publish_Event.jasper");
			File jasperfile = resource.getFile();

			Map<String, Object> parameters = new HashMap<String, Object>();
			JRSwapFileVirtualizer virtualizer = null;
			virtualizer = new JRSwapFileVirtualizer(500, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			parameters.put("url", APP_URL);
			parameters.put("supplierName", supplierName);
			parameters.put("buyerName", buyerName);
			parameters.put("eventName", eventName);
			parameters.put("businessUnit", businessUnit != null ? businessUnit.getUnitName() : "NA");
			parameters.put("eventType", rfxTypes.getValue());
			parameters.put("refrance", refrance);
			parameters.put("status", eventStatus.name());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, new JREmptyDataSource());
			return JasperExportManager.exportReportToPdf(jasperPrint);

		} catch (Exception e) {
			LOG.error("Error wile genrate Fax PDF-->" + e.getMessage(), e);
		}

		return null;

	}

	@Override
	public List<String> getIndustryCategoriesIdForRftById(String eventId, RfxTypes eventType) {
		return rftEventDao.getIndustryCategoriesIdForRftById(eventId, eventType);
	}

	@Override
	public Declaration getDeclarationForSupplierByEventId(String eventId, RfxTypes eventType) {
		return rftEventDao.getDeclarationForSupplierByEventId(eventId, eventType);
	}

	@Override
	public void sendEvaluationCompletedPrematurelyNotification(User actionBy, Event event) {
		String url = APP_URL + "/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
		String msg = "\"" + actionBy.getName() + "\" has concluded evaluation prematurely for \"" + event.getEventName() + "\"";
		String timeZone = "GMT+8:00";
		String subject = "Evaluation Concluded Prematurely";

		User eventOwner = rftEventDao.getPlainEventOwnerByEventId(event.getId());
		timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);

		// Send to event owner
		sendNotification(event, url, msg, eventOwner, timeZone, subject);

		// send to Associate Owners
		List<EventTeamMember> teamMembers = rftEventDao.getPlainTeamMembersForEvent(event.getId());
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
			map.put("eventType", RfxTypes.RFT.getValue());
			map.put("businessUnit", StringUtils.checkString(rftEventDao.findBusinessUnitName(event.getId())));
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
	public RftEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		RftEvaluationConclusionUser usr = rftEventDao.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
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
		List<EvaluationConclusionPojo> rftSummary = new ArrayList<EvaluationConclusionPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		parameters.put("generatedOn", sdf.format(new Date()));

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationConclusionReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationConclusionPojo summary = new EvaluationConclusionPojo();
			RftEvent event = rftEventDao.getPlainEventWithOwnerById(eventId);

			if (event != null) {

				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				summary.setType("Request For Tender");
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

				List<RftEnvelop> envelopList = rftEnvelopDao.getEnvelopListByEventId(eventId);
				String evalutedEnvelop = "";
				String nonEvalutedEnvelop = "";
				if (CollectionUtil.isNotEmpty(envelopList)) {
					for (RftEnvelop envelop : envelopList) {
						if (Boolean.TRUE == envelop.getEvaluationCompletedPrematurely()) {
							nonEvalutedEnvelop += envelop.getEnvelopTitle() + ",";
						} else {
							evalutedEnvelop += envelop.getEnvelopTitle() + ",";
						}
					}
				}
				if (StringUtils.checkString(evalutedEnvelop).length() > 0) {
					LOG.info("evalutedEnvelop:" + evalutedEnvelop);
					evalutedEnvelop = StringUtils.checkString(evalutedEnvelop).substring(0, StringUtils.checkString(evalutedEnvelop).length() - 1);
				}
				if (StringUtils.checkString(nonEvalutedEnvelop).length() > 0) {
					LOG.info("nonEvalutedEnvelop:" + nonEvalutedEnvelop);
					nonEvalutedEnvelop = StringUtils.checkString(nonEvalutedEnvelop).substring(0, StringUtils.checkString(nonEvalutedEnvelop).length() - 1);
				}
				summary.setEnvelopEvaluted(StringUtils.checkString(evalutedEnvelop).length() > 0 ? " (" + evalutedEnvelop + ")" : "");
				summary.setEnvelopNonEvaluted(StringUtils.checkString(nonEvalutedEnvelop).length() > 0 ? " (" + nonEvalutedEnvelop + ")" : "");

				List<RftEvaluationConclusionUser> evaluationConclusionUserList = rftEventDao.findEvaluationConclusionUsersByEventId(eventId);
				String conclusionUser = "";

				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					Log.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RftEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
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
				rftSummary.add(summary);
			}
			parameters.put("EVALUATION_CONCLUSION", rftSummary);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rftSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);

		} catch (Exception e) {
			LOG.error("Could not generate Evaluation conclusion  Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public EventPermissions getUserEventPemissions(String userId, String eventId) {
		return rftEventDao.getUserEventPemissions(userId, eventId);
	}

	@Override
	public long findTotalMyPendingRfxApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalMyPendingRfxApprovals(tenantId, userId, input);
	}

	@Override
	public void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RftEvent event, String name, String feeReference, String timezone) {
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
	public long findCountOfAllActivePendingEventCountForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllActivePendingEventCountForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public long findCountOfAllActiveSubmittedEventCountForSupplier(String supplierId, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.findCountOfAllActiveSubmittedEventCountForSupplier(supplierId, (isAdmin ? null : userId));
	}

	@Override
	public List<RfxView> getOnlyActivePendingEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyActivePendingEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalCountOfActivePendingEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalCountOfActivePendingEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public List<RfxView> getOnlyActiveSubmittedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}

		return rftEventDao.getOnlyActiveSubmittedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public long findTotalCountOfActiveSubmittedEventsForSupplier(String supplierId, TableDataInput input, String userId) {
		boolean isAdmin = false;
		List<User> users = userDao.getAllAdminUsersForSupplier(supplierId);
		if (CollectionUtil.isNotEmpty(users)) {
			for (User user : users) {
				if (user.getId().equals(userId)) {
					isAdmin = true;
					break;
				}
			}
		}
		return rftEventDao.findTotalCountOfActiveSubmittedEventsForSupplier(supplierId, input, (isAdmin ? null : userId));
	}

	@Override
	public void downloadCsvFileForEvents(HttpServletResponse response, File file, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, Date startDate, Date endDate, boolean select_all, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.ALL_EVENT_REPORT_CSV_COLUMNS;
			final String[] columns = new String[] { "sysEventId", "referenceNumber", "eventName", "eventDescription", "eventUser", "publishDateForm", "startDate", "endDate", "deliveryDateForm", "visibility", "validityDays", "type", "currencyName", "unitName", "costCenter", "groupCode", "procurementMethod", "procurementCategories", "status", "leadingSupplier", "leadingAmount", "invitedSupplierCount", "acceptedSupplierCount", "submittedSupplierCount", "budgetAmount", "estimatedBudget", "histricAmount", "templateName", "assoiciateOwner", "unmaskOwner", "addressTitle", "auctionType", "participationFees", "deposite", "preViewSupplierCount", "rejectedSupplierCount", "disqualifedSuppliers", "concluded", "concludedaDate", "pushToEvent", "pushDate", "pushToPr", "prToPushDate", "awardedDate", "avgGrandTotal", "eventCategories" };

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			long count = rftEventDao.findTotalEventsCountForCsv(tenantId);

			int PAGE_SIZE = 90000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<DraftEventPojo> list = rftEventDao.findAllActiveEventsForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, eventIds, searchFilterEventPojo, select_all, startDate, endDate);
				LOG.info("size ........" + list.size() + ".... count " + count);
				for (DraftEventPojo pojo : list) {

					if (pojo.getPublishDate() != null) {
						pojo.setPublishDateForm(sdf.format(pojo.getPublishDate()));
					}
					if (pojo.getEventStart() != null) {
						pojo.setStartDate(sdf.format(pojo.getEventStart()));
					}
					if (pojo.getEventEnd() != null) {
						pojo.setEndDate(sdf.format(pojo.getEventEnd()));
					}
					if (pojo.getDeliveryDate() != null) {
						pojo.setDeliveryDateForm(sdf.format(pojo.getDeliveryDate()));
					}
					if (pojo.getEventPushDate() != null) {
						pojo.setPushDate(sdf.format(pojo.getEventPushDate()));
					}
					if (pojo.getPrPushDate() != null) {
						pojo.setPrToPushDate(sdf.format(pojo.getPrPushDate()));
					}
					if (pojo.getEventConcludeDate() != null) {
						pojo.setConcludedaDate(sdf.format(pojo.getEventConcludeDate()));
					}
					if (pojo.getAwardDate() != null) {
						pojo.setAwardedDate(sdf.format(pojo.getAwardDate()));
					}

					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.info("Error ..." + e, e);
		}
	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // Event id
				new Optional(), // referenceNumber
				new Optional(), // eventName
				new Optional(), // eventDescription
				new Optional(), // ownerName
				new Optional(),

				new Optional(), // Publish
				new Optional(), // start
				new Optional(), // end
				new Optional(), // delivery date
				new Optional(), // visibility

				new Optional(), // Validity
				new Optional(), new Optional(), // curren
				new Optional(), // unit
				new Optional(), // CC

				new Optional(), new Optional(), // Leading supp
				new Optional(), // Leading amount
				new Optional(), //
				new Optional(), //

				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //

				new Optional(), // unmasked
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //

				new Optional(), // preViewSupplierCount
				new Optional(), //
				new Optional(), //
				new Optional(), // concluded
				new Optional(), //

				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // pr push date
				new Optional(), //

				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), };
		return processors;
	}

	@Override
	public long getRftEventCountByTenantId(String searchVal, String tenantId, String userId) {
		return rftEventDao.getRftEventCountByTenantId(searchVal, tenantId, userId, null);
	}

	@Override
	public List<Event> findEventsByEventNameAndRefNumAndIndCatForTenant(String searchValue, Integer pageNo, Integer pageLength, String industryCategory, RfxTypes eventType, String tenantId, String loggedInUser) throws SubscriptionException {

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

		List<Event> eventList = new ArrayList<Event>();
		LOG.info("event Type : " + eventType);

		Integer start = 0;
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (pageLength != null) {
			start = start * pageLength;
		}

		switch (eventType) {
		case RFA:
			List<RfaEvent> rfaList = rfaEventDao.findEventsByEventNameAndRefNumAndIndCatForTenant(searchValue, pageNo, pageLength, start, industryCategory, eventType, tenantId, loggedInUser);
			for (RfaEvent rfaEvent : rfaList) {
				if (rfaEvent.getTemplate() != null) {
					if (rfaEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rfaEvent.setTemplateActive(false);
					} else {
						rfaEvent.setTemplateActive(true);
					}
				}
				if (rfaEvent.getAuctionType() != null) {
					rfaEvent.setRfaAuctionType(rfaEvent.getAuctionType());
				}
				if (CollectionUtil.isNotEmpty(rfaEvent.getIndustryCategories())) {
					rfaEvent.setIndustryCategory(rfaEvent.getIndustryCategories().get(0));
				}

			}
			eventList = convertToEventList(rfaList);
			break;
		case RFI:
			List<RfiEvent> rfiList = rfiEventDao.findEventsByEventNameAndRefNumAndIndCatForTenant(searchValue, pageNo, pageLength, start, industryCategory, eventType, tenantId, loggedInUser);

			for (RfiEvent rfiEvent : rfiList) {
				if (rfiEvent.getTemplate() != null) {
					if (rfiEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rfiEvent.setTemplateActive(false);
					} else {
						rfiEvent.setTemplateActive(true);
					}
				}
				if (CollectionUtil.isNotEmpty(rfiEvent.getIndustryCategories())) {
					rfiEvent.setIndustryCategory(rfiEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rfiList);
			break;
		case RFP:
			List<RfpEvent> rfpList = rfpEventDao.findEventsByEventNameAndRefNumAndIndCatForTenant(searchValue, pageNo, pageLength, start, industryCategory, eventType, tenantId, loggedInUser);

			for (RfpEvent rfpEvent : rfpList) {
				if (rfpEvent.getTemplate() != null) {
					if (rfpEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rfpEvent.setTemplateActive(false);
					} else {
						rfpEvent.setTemplateActive(true);
					}
				}
				if (CollectionUtil.isNotEmpty(rfpEvent.getIndustryCategories())) {
					rfpEvent.setIndustryCategory(rfpEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rfpList);
			break;
		case RFQ:
			List<RfqEvent> rfqList = rfqEventDao.findEventsByEventNameAndRefNumAndIndCatForTenant(searchValue, pageNo, pageLength, start, industryCategory, eventType, tenantId, loggedInUser);
			for (RfqEvent rfqEvent : rfqList) {
				if (rfqEvent.getTemplate() != null) {
					if (rfqEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rfqEvent.setTemplateActive(false);
					} else {
						rfqEvent.setTemplateActive(true);
					}
				}
				if (CollectionUtil.isNotEmpty(rfqEvent.getIndustryCategories())) {
					rfqEvent.setIndustryCategory(rfqEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rfqList);
			break;
		case RFT:
			List<RftEvent> rftList = rftEventDao.findEventsByEventNameAndRefNumAndIndCatForTenant(searchValue, pageNo, pageLength, start, industryCategory, eventType, tenantId, loggedInUser);
			for (RftEvent rftEvent : rftList) {
				if (rftEvent.getTemplate() != null) {
					if (rftEvent.getTemplate().getStatus() == Status.ACTIVE) {
						rftEvent.setTemplateActive(false);
					} else {
						rftEvent.setTemplateActive(true);
					}
				}
				if (CollectionUtil.isNotEmpty(rftEvent.getIndustryCategories())) {
					rftEvent.setIndustryCategory(rftEvent.getIndustryCategories().get(0));
				}
			}
			eventList = convertToEventList(rftList);
			break;
		default:
			break;

		}
		return eventList;

	}

	@Override
	public long findActiveEventCountByRfxTypeForTenantId(String searchValue, String tenantId, RfxTypes eventType, String userId, String industryCategory) {

		long count = 0;
		switch (eventType) {
		case RFA:
			count = rfaEventDao.getRfaEventCountByTenantId(searchValue, tenantId, userId, industryCategory);
			break;
		case RFI:
			count = rfiEventDao.getRfiEventCountByTenantId(searchValue, tenantId, userId, industryCategory);
			break;
		case RFP:
			count = rfpEventDao.getRfpEventCountByTenantId(searchValue, tenantId, userId, industryCategory);
			break;
		case RFQ:
			count = rfqEventDao.getRfqEventCountByTenantId(searchValue, tenantId, userId, industryCategory);
			break;
		case RFT:
			count = rftEventDao.getRftEventCountByTenantId(searchValue, tenantId, userId, industryCategory);
			break;
		default:
			break;
		}
		return count;
	}

	@Override
	public List<PendingEventPojo> findSuspendedEventsPendingApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findSuspendedEventsPendingApprovals(tenantId, userId, input);
	}

	@Override
	public long findCountOfSuspendedEventsPendingApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findCountOfSuspendedEventsPendingApprovals(tenantId, userId, input);
	}

	@Override
	public long findCountOfSuspendedEventPendingApprovals(String tenantId, String userId) {
		return rftEventDao.findCountOfSuspendedEventPendingApprovals(tenantId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEvent updateEventSuspensionApproval(RftEvent event, User user) {

		RftEvent persistObj = rftEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RftEventSuspensionApproval approvalRequest : persistObj.getSuspensionApprovals()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RftSuspensionApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}
		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			int level = 1;
			for (RftEventSuspensionApproval app : event.getSuspensionApprovals()) {
				app.setEvent(event);
				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;
				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RftSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
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
		persistObj = rftEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RftEventAudit audit = new RftEventAudit(persistObj, user, new Date(), AuditActionType.Update, auditMessage);
				rftEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" + persistObj.getEventId() + "' ." + auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFT);
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
	public void downloadRftEvaluatorDocument(String id, HttpServletResponse response) {
		RftEvaluatorUser docs = rftEvaluatorUserDao.getEvaluationDocument(id);
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
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadRftLeadEvaluatorDocument(String envelopId, HttpServletResponse response) {
		RftEnvelop docs = rftEnvelopDao.getEvaluationDocument(envelopId);
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
	public List<PendingEventPojo> findMyPendingPoApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findMyPendingPoApprovals(tenantId, userId, input);
	}

	@Override
	public long findTotalRevisePendingPoApprovals(String tenantId, String userId) {
		return rftEventDao.findTotalRevisePendingPoApprovals(tenantId, userId);
	}

	@Override
	public long findTotalRevisePendingPoApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalRevisePendingPoApprovals(tenantId, userId, input);
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> createSpFormFromAward(RftEventAward rftEventAward, String templateId, String eventId, String userId, User loggedInUser) throws ApplicationException {
		RftEvent rftEvent = rftEventService.getRftEventByeventId(eventId);
		SupplierPerformanceTemplate spTemplate = spTemplateService.getSupplierPerformanceTemplatebyId(templateId);
		User formOwner = userService.getUsersById(userId);
		List<String> formIds = new ArrayList<String>();
		try {

			List<String> supplierIds = new ArrayList<String>();
			for (RftEventAwardDetails rfxAward : rftEventAward.getRfxAwardDetails()) {
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
				String formId = eventIdSettingsDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "SP", rftEvent.getBusinessUnit());
				form.setFormId(formId);
				form.setFormName(spTemplate.getTemplateName());
				form.setFormOwner(formOwner);
				form.setCreatedBy(SecurityLibrary.getLoggedInUser());
				form.setCreatedDate(new Date());
				form.setEventId(eventId);
				form.setEventType(RfxTypes.RFT);
				form.setAwardedSupplier(sup);
				if (Boolean.TRUE == spTemplate.getProcurementCategoryVisible() && Boolean.TRUE == spTemplate.getProcurementCategoryDisabled()) {
					form.setProcurementCategory(spTemplate.getProcurementCategory());
				} else {
					if (rftEvent.getProcurementCategories() != null) {
						form.setProcurementCategory(rftEvent.getProcurementCategories());
					} else {
						// if it is available in template then use it.
						form.setProcurementCategory(spTemplate.getProcurementCategory());
					}
				}
				form.setReferenceNumber(rftEvent.getEventId());
				form.setReferenceName(rftEvent.getEventName());
				form.setTemplate(spTemplateService.getSupplierPerformanceTemplatebyId(templateId));
				form.setBusinessUnit(rftEvent.getBusinessUnit());
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

	@Override
	@Transactional(readOnly = false)
	public void revertEventAwardStatus(String eventId) {
		rftEventDao.revertEventAwardStatus(eventId);
	}

	@Override
	public List<PendingEventPojo> findMyPendingRfxAwardApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findMyPendingRfxAwardApprovals(tenantId, userId, input);
	}

	@Override
	public long findTotalMyPendingRfxAwardApprovals(String tenantId, String userId, TableDataInput input) {
		return rftEventDao.findTotalMyPendingRfxAwardApprovals(tenantId, userId, input);
	}

	@Override
	public long findTotalMyPendingAwardApprovals(String tenantId, String userId) {
		return rftEventDao.findTotalMyPendingAwardApprovals(tenantId, userId);
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

	@Override
	public RftEvent loadEventForSummeryPageById(String id) {
		RftEvent event = rftEventDao.findBySupplierEventId(id);

		// Here on the above method there is some issue to retreive sorlist as already
		// it is retreving BqList . that's why two method.
		RftEvent event2 = rftEventDao.findEventSorByEventId(id);
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
				for (RftUnMaskedUser unmaskuser : event.getUnMaskedUsers()) {
					unmaskuser.getUser().getCommunicationEmail();
					unmaskuser.getUser().getName();
					unmaskuser.getUser().getPhoneNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RftEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getCommunicationEmail();
					user.getUser().getName();
					user.getUser().getPhoneNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RftEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RftTeamMember teamMembers : event.getTeamMembers()) {
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
				for (RftReminder reminder : event.getRftReminder()) {
					reminder.getInterval();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RftEventMeeting meeting : event.getMeetings()) {
					meeting.getAppointmentDateTime();
					meeting.getAppointmentTime();
					meeting.getInviteSuppliers();
					meeting.getTitle();
					meeting.getRfxEventMeetingContacts();
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RftEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
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
				for (RftEventMeeting meeting : event.getMeetings()) {
					meeting.getRemarks();
					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier supplier : meeting.getInviteSuppliers()) {
							supplier.getCompanyName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RftEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
							contact.getContactName();
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
				for (RftEventBq bq : event.getEventBqs()) {
					if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
						for (RftBqItem item : bq.getBqItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event2.getEventSors())) {
				for (RftEventSor bq : event2.getEventSors()) {
					if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
						for (RftSorItem item : bq.getSorItems()) {
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
				for (RftComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RftEventApproval approvalLevel : event.getApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RftApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RftEventSuspensionApproval approvalLevel : event.getSuspensionApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RftSuspensionApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionComment())) {
				for (RftSuspensionComment comment : event.getSuspensionComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
				for (RftEventAwardApproval approvalLevel : event.getAwardApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RftAwardApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardComment())) {
				LOG.info(" Comments  >>>  " + event.getAwardComment());
				for (RftAwardComment comment : event.getAwardComment()) {
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
	public List<DraftEventPojo> getAllFinishedEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, int days, TableDataInput input, String userId,List<String> businessUnitIds) {

		return rftEventDao.getAllFinishedEventsForBizUnit(tenantId, lastLoginTime, days, input, userId,businessUnitIds);

	}

	@Override
	public long findTotalFinishedEventForBizUnit(String tenantId, String userId, int days, TableDataInput input,List<String> businessUnitIds) {
		return rftEventDao.findTotalFinishedEventForBizUnit(tenantId, userId, days, input,businessUnitIds);
	}

	@Override
	public List<DraftEventPojo> getAllClosedEventForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds) {

		return rftEventDao.getAllClosedEventsForBizUnit(tenantId, input, userId,businessUnitIds);
	}

	@Override
	public long findTotalClosedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {

		return rftEventDao.findTotalClosedEventForBizUnit(tenantId, userId, input,businessUnitIds);
	}

	@Override
	public List<DraftEventPojo> getAllSuspendedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds) {
		return rftEventDao.getAllSuspendedEventsForBizUnit(tenantId, input, userId,businessUnitIds);
	}

	@Override
	public long findTotalSuspendedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
		return rftEventDao.findTotalSuspendedEventForBizUnit(tenantId, userId, input,businessUnitIds);
	}

	@Override
	public List<DraftEventPojo> getAllCompletedEventsForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds) {

		return rftEventDao.getAllCompletedEventsForBizUnit(tenantId, input, userId,businessUnitIds);
	}
	@Override
	public long findTotalCompletedEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {

		return rftEventDao.findTotalCompletedEventForBizUnit(tenantId, userId, input,businessUnitIds);
	}

	@Override
	public List<DraftEventPojo> getAllCancelledEventForBizUnit(String tenantId, TableDataInput input, String userId,List<String> businessUnitIds) {
		return rftEventDao.getAllCancelledEventsForBizUnit(tenantId, input, userId,businessUnitIds);

	}
	@Override
	public long findTotalCancelledEventForBizUnit(String tenantId, String userId, TableDataInput input,List<String> businessUnitIds) {
		return rftEventDao.findTotalCancelledEventForBizUnit(tenantId, userId, input,businessUnitIds);

	}

	@Override
	public List<PendingEventPojo> getAllPendingEventForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds) {
		List<PendingEventPojo> list = rftEventDao.getAllPendingEventsForBuyerBizUnit(tenantId, lastLoginTime, input, userId, businessUnitIds);
		return list;
	}

	@Override
	public long findTotalPendingEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds) {
		return rftEventDao.findTotalPendingEventForBuyerBizUnit(tenantId, userId, input, businessUnitIds);
	}

	@Override
	public List<ActiveEventPojo> getAllActiveEventForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds) {
		List<ActiveEventPojo> list = rftEventDao.getAllActiveEventsForBuyerBizUnit(tenantId, lastLoginTime, input, userId, businessUnitIds);
		return list;
	}

	@Override
	public long findTotalActiveEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds) {
		return rftEventDao.findTotalActiveEventForBuyerBizUnit(tenantId, userId, input, businessUnitIds);
	}

	@Override
	public List<OngoingEventPojo> getAllOngoingEventsForBuyerBizUnit(String tenantId, Date lastLoginTime, TableDataInput input, String userId, List<String> businessUnitIds) {
		List<OngoingEventPojo> list = rftEventDao.getAllOngoingEventsForBuyerBizUnit(tenantId, lastLoginTime, input, userId, businessUnitIds);
		return list;
	}

	@Override
	public long findTotalOngoingEventForBuyerBizUnit(String tenantId, String userId, TableDataInput input, List<String> businessUnitIds) {
		return rftEventDao.findTotalOngoingEventForBuyerBizUnit(tenantId, userId, input, businessUnitIds);
	}

}
