package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.RfaSorDao;
import com.privasia.procurehere.core.dao.RfaSorItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierSorDao;
import com.privasia.procurehere.core.pojo.EnvelopeSorPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersSorPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierSorPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.RfaSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfaSupplierSorItemService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.FileCopyUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.AuctionBidsDao;
import com.privasia.procurehere.core.dao.AuctionRulesDao;
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
import com.privasia.procurehere.core.dao.RfaCqDao;
import com.privasia.procurehere.core.dao.RfaCqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfaCqItemDao;
import com.privasia.procurehere.core.dao.RfaCqOptionDao;
import com.privasia.procurehere.core.dao.RfaDocumentDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEvaluatorDeclarationDao;
import com.privasia.procurehere.core.dao.RfaEvaluatorUserDao;
import com.privasia.procurehere.core.dao.RfaEventAuditDao;
import com.privasia.procurehere.core.dao.RfaEventAwardDao;
import com.privasia.procurehere.core.dao.RfaEventAwardDetailsDao;
import com.privasia.procurehere.core.dao.RfaEventContactDao;
import com.privasia.procurehere.core.dao.RfaEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaEventTimeLineDao;
import com.privasia.procurehere.core.dao.RfaReminderDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqOptionDao;
import com.privasia.procurehere.core.dao.RfaSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.dao.RfaSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.AuctionConsolePriceVenderType;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.DurationMinSecType;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventTimelineType;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierPerformanceAuditActionType;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.TimeExtensionType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.AuctionEvaluationDocumentPojo;
import com.privasia.procurehere.core.pojo.AuctionSupplierBidPojo;
import com.privasia.procurehere.core.pojo.AuctionSupplierPojo;
import com.privasia.procurehere.core.pojo.BidHistoryChartPojo;
import com.privasia.procurehere.core.pojo.BidHistoryChartSupplierPojo;
import com.privasia.procurehere.core.pojo.BidHistoryPojo;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.EnvelopeBqPojo;
import com.privasia.procurehere.core.pojo.EnvelopeCqPojo;
import com.privasia.procurehere.core.pojo.EvaluationApprovalsPojo;
import com.privasia.procurehere.core.pojo.EvaluationAprovalUsersPojo;
import com.privasia.procurehere.core.pojo.EvaluationAuctionBiddingPojo;
import com.privasia.procurehere.core.pojo.EvaluationAuctionPojo;
import com.privasia.procurehere.core.pojo.EvaluationAuctionRulePojo;
import com.privasia.procurehere.core.pojo.EvaluationAuditPojo;
import com.privasia.procurehere.core.pojo.EvaluationBidHistoryPojo;
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
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.EventTimelinePojo;
import com.privasia.procurehere.core.pojo.EventTimerPojo;
import com.privasia.procurehere.core.pojo.FinishedEventPojo;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.MobileEventPojo;
import com.privasia.procurehere.core.pojo.OngoingEventPojo;
import com.privasia.procurehere.core.pojo.RfaEventCAddressPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.RfxEnvelopPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SupplierMaskingCodePojo;
import com.privasia.procurehere.core.pojo.SupplierMaskingPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
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
import com.privasia.procurehere.job.DutchAuctionJob;
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
import com.privasia.procurehere.service.RfaBqEvaluationCommentsService;
import com.privasia.procurehere.service.RfaBqTotalEvaluationCommentsService;
import com.privasia.procurehere.service.RfaCqEvaluationCommentsService;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEnvelopService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfpSupplierBqService;
import com.privasia.procurehere.service.RfqEnvelopService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RfqSupplierBqService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RftSupplierBqService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SupplierPerformanceAuditService;
import com.privasia.procurehere.service.SupplierPerformanceFormService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;

import freemarker.template.Configuration;
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
public class RfaEventServiceImpl implements RfaEventService {

	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	private static final Logger DLOG = LogManager.getLogger(Global.DOWNLOAD_LOG);
	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	UserDao userDao;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaBqDao rfaEventBqDao;

	@Autowired
	RfaEventContactDao rfaEventContactDao;

	@Autowired
	RfaEventCorrespondenceAddressDao rfaEventCorrespondenceAddressDao;

	@Autowired
	NaicsCodesDao naicsCodesDao;

	@Autowired
	IndustryCategoryDao industryCategoryDao;

	@Autowired
	RfaEventMeetingDao rfaEventMeetingDao;

	@Autowired
	RfaDocumentDao rfaDocumentDao;

	@Autowired
	RfaBqItemDao rfaBqItemDao;

	@Autowired
	RfaCqDao rfaCqDao;

	@Autowired
	RfaCqItemDao rfaCqItemDao;

	@Autowired
	RfaCqEvaluationCommentsDao cqEvaluationCommentsDao;

	@Autowired
	RfaEventMeetingDocumentDao rfaEventMeetingDocumentDao;

	@Autowired
	RfaSupplierSorItemService rfaSupplierSorItemService;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	RfaSupplierMeetingAttendanceDao rfaSupplierMeetingAttendanceDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

	@Autowired
	RfaSorEvaluationCommentsService rfaSorEvaluationCommentsService;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	RfaBqTotalEvaluationCommentsService rfaBqTotalEvaluationCommentsService;

	@Autowired
	RfaCqEvaluationCommentsService cqEvaluationCommentsService;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	CostCenterDao costCenterDao;

	@Autowired
	RfaSupplierSorDao rfaSupplierSorDao;

	@Autowired
	RfaReminderDao rfaReminderDao;

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Autowired
	UserService userService;

	@Autowired
	AuctionRulesDao auctionRulesDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	BuyerService buyerService;

	@Autowired
	RfaEnvelopService rfaEnvelopService;

	@Autowired
	AuctionBidsDao auctionBidsDao;

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	RfaEventTimeLineDao rfaEventTimeLineDao;

	@Autowired
	RfaEventAuditDao rfaEventAuditDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	RfaBqEvaluationCommentsService rfaBqEvaluationCommentsService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	RfaSupplierCqItemDao supplierCqItemDao;

	@Autowired
	ServletContext context;

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
	RfaBqDao rfaBqDao;

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
	RfaSupplierBqDao rfaSupplierBqDao;

	@Autowired
	RfaSupplierTeamMemberDao rfaSupplierTeamMemberDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	UomService uomService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@Autowired
	PrService prService;

	@Autowired
	RfaDocumentService rfaDocumentService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfpSupplierBqService rfpSupplierBqService;

	@Autowired
	RfqSupplierBqService rfqSupplierBqService;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	RftSupplierBqService rftSupplierBqService;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RfqEnvelopService rfqEnvelopService;

	@Autowired
	RfpEnvelopService rfpEnvelopService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	RfaEnvelopService envelopService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	DeclarationDao declarationDao;

	@Autowired
	RfaEvaluatorDeclarationDao rfaEvaluatorDeclarationDao;

	@Autowired
	RfaSupplierCqOptionDao rfaSupplierCqOptionDao;

	@Autowired
	RfaCqOptionDao rfaCqOptionDao;

	@Autowired
	ProcurementMethodDao procurementMethodDao;

	@Autowired
	ProcurementCategoriesDao procurementCategoriesDao;

	@Autowired
	GroupCodeDao groupCodeDao;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	RfaEvaluatorUserDao rfaEvaluatorUserDao;

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
	RfaEventAwardDetailsDao rfaEventAwardDetailsDao;

	@Autowired
	SupplierPerformanceTemplateService spTemplateService;

	@Autowired
	SupplierPerformanceFormService spFormService;

	@Autowired
	SupplierPerformanceAuditService formAuditService;

	@Autowired
	ContractAuditDao contractAuditDao;

	@Autowired
	RfaEventAwardDao rfaEventAwardDao;

	@Autowired
	RfaSorDao rfaEventSorDao;

	@Autowired
	RfaSorItemDao rfaSorItemDao;

	public List<EvaluationBiddingPricePojo> getSortedEvaluationSupplierBids(List<EvaluationBiddingPricePojo> supplierBidsLists) {
		Collections.sort(supplierBidsLists);
		return supplierBidsLists;
	}

	public List<EvaluationBiddingPricePojo> getSortedEvaluationTopSupplierBids(List<EvaluationBiddingPricePojo> supplierBidsLists) {
		Collections.sort(supplierBidsLists);
		return supplierBidsLists;
	}

	public List<EvaluationBiddingPricePojo> getSortedEvaluationSupplierBidsLineChart(List<EvaluationBiddingPricePojo> supplierBidsLists) {
		Collections.sort(supplierBidsLists);
		return supplierBidsLists;
	}

	public List<EvaluationBiddingPricePojo> getSortedEvaluationSupplierBidsLineTimeChart(List<EvaluationBiddingPricePojo> supplierBidsLists) {
		Collections.sort(supplierBidsLists, new Comparator<EvaluationBiddingPricePojo>() {
			public int compare(EvaluationBiddingPricePojo o1, EvaluationBiddingPricePojo o2) {
				if (o1.getSubmitionDate() == null || o2.getSubmitionDate() == null) {
					return 0;
				}
				return o1.getSubmitionDate().compareTo(o2.getSubmitionDate());
			}
		});
		return supplierBidsLists;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent saveRfaEvent(RfaEvent newEvent, User loggedInUser) throws SubscriptionException {
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
		newEvent.setCreatedDate(new Date());
		newEvent.setEventOwner(loggedInUser);
		newEvent.setTenantId(loggedInUser.getTenantId());
		newEvent.setCreatedBy(loggedInUser);
		LOG.info("Save Event Name :" + newEvent.getEventId());
		RfaEvent dbObj = rfaEventDao.saveOrUpdate(newEvent);
		// If there are unsaved envelopes attached to the event, save them as well
		try {
			if (CollectionUtil.isNotEmpty(newEvent.getRfaEnvelop())) {
				for (RfaEnvelop env : newEvent.getRfaEnvelop()) {
					LOG.info("*******" + env.getEnvelopTitle());
					if (StringUtils.checkString(env.getId()).length() == 0 && env.getEnvelopTitle() != null) {
						env.setRfxEvent(dbObj);
						rfaEnvelopDao.save(env);
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
		if (newEvent.isUploadDocuments()) {
			dbObj.setUploadDocuments(true);
		}
		return dbObj;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent updateRfaEvent(RfaEvent rfaEvent) {
		RfaEvent event = rfaEventDao.update(rfaEvent);
		event.getCreatedBy().getLoginId();
		return event;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfaEvent(RfaEvent rfaEvent) {
		rfaEventDao.delete(rfaEvent);
	}

	@Override
	public RfaEvent getRfaEventById(String id) {
		RfaEvent rfa = rfaEventDao.findById(id);
		for (RfaEventApproval approval : rfa.getApprovals()) {
			for (RfaApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}

		for (RfaEventSuspensionApproval approval : rfa.getSuspensionApprovals()) {
			for (RfaSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getRemarks();
			}
		}

		for (RfaEventAwardApproval approval : rfa.getAwardApprovals()) {
			for (RfaAwardApprovalUser approvalUser : approval.getApprovalUsers()) {
				approvalUser.getUser();
				approvalUser.getRemarks();
			}
		}

		if (rfa.getEventOwner() != null) {
			rfa.getEventOwner().getName();
			rfa.getEventOwner().getCommunicationEmail();
			rfa.getEventOwner().getPhoneNumber();
			if (rfa.getEventOwner().getOwner() != null) {
				Owner usr = rfa.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}

		if (rfa.getBusinessUnit() != null) {
			rfa.getBusinessUnit().getUnitName();
		}

		if (rfa.getCostCenter() != null) {
			rfa.getCostCenter().getCostCenter();
		}

		if (CollectionUtil.isNotEmpty(rfa.getAwardComment())) {
			for (RfaAwardComment comment : rfa.getAwardComment()) {
				if (comment.getCreatedBy() != null) {
					comment.getCreatedBy().getLoginId();
				}
			}
		}

		if (rfa.getTemplate() != null) {
			rfa.getTemplate().getTemplateName();
		}

		return rfa;
	}

	@Override
	public List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue) {
		return naicsCodesDao.findLeafIndustryCategoryByName(searchValue);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRfaEventContact(RfaEventContact rfaEventContact) {
		rfaEventContactDao.saveOrUpdate(rfaEventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfaEventContact(RfaEventContact rfaEventContact) {
		rfaEventContactDao.update(rfaEventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveRfaEventCorrespondenceAddress(RfaEventCorrespondenceAddress rfaEventCorrespondenceAddress) {
		rfaEventCorrespondenceAddressDao.save(rfaEventCorrespondenceAddress);
	}

	@Override
	public RfaEvent getRfaEventByeventId(String eventId) {
		RfaEvent rfa = rfaEventDao.findByEventId(eventId);
		if(CollectionUtil.isNotEmpty(rfa.getDocuments())){
			for(RfaEventDocument doc : rfa.getDocuments()){
				if (doc.getUploadBy() != null) {
					doc.getUploadBy().getLoginId();
				}
				doc.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getEventContacts())){
			for(RfaEventContact contact : rfa.getEventContacts()){
				contact.getContactName();
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getEventCorrespondenceAddress())){
			for(EventCorrespondenceAddress address: rfa.getEventCorrespondenceAddress()){
				address.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getEventBqs())){
			for(RfaEventBq bq: rfa.getEventBqs()){
				bq.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getEventSors())){
			for(RfaEventSor sor: rfa.getEventSors()){
				sor.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getCqs())){
			for(RfaCq cq: rfa.getCqs()) {
				cq.getId();
			}
		}

		if(CollectionUtil.isNotEmpty(rfa.getEventViewers())){
			for(User viewr: rfa.getEventViewers()){
				viewr.getLoginId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getEventEditors())){
			for(User editor: rfa.getEventEditors()){
				editor.getLoginId();
			}
		}
		if (CollectionUtil.isNotEmpty(rfa.getTeamMembers())) {
			for (RfaTeamMember team : rfa.getTeamMembers()) {
				team.getTeamMemberType();
				if (team.getUser() != null) {
					team.getUser().getLoginId();
				}
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getAwardedSuppliers())){
			for(Supplier sup : rfa.getAwardedSuppliers()){
				sup.getId();
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getAwardComment())){
			for(RfaAwardComment awardComment: rfa.getAwardComment()){
				awardComment.getComment();
			}
		}
		if(CollectionUtil.isNotEmpty(rfa.getSuspensionComment())){
			for(RfaSuspensionComment rfaSuspensionComment: rfa.getSuspensionComment()){
				rfaSuspensionComment.getComment();
			}
		}
		if(rfa.getBusinessUnit() != null){
			rfa.getBusinessUnit().getId();
		}
		if (CollectionUtils.isNotEmpty(rfa.getApprovals())) {
			for (RfaEventApproval approval : rfa.getApprovals()) {
				for (RfaApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}

		if (CollectionUtils.isNotEmpty(rfa.getSuspensionApprovals())) {
			for (RfaEventSuspensionApproval approval : rfa.getSuspensionApprovals()) {
				for (RfaSuspensionApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
					approvalUser.getUser().getCommunicationEmail();
				}
			}
		}

		rfa.getProcurementCategories();
		rfa.getProcurementMethod();

		if (CollectionUtils.isNotEmpty(rfa.getAwardApprovals())) {
			for (RfaEventAwardApproval approval : rfa.getAwardApprovals()) {
				for (RfaAwardApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
					approvalUser.getUser().getCommunicationEmail();
					approvalUser.getUser();
				}
			}
		}

		for (RfaEnvelop rf : rfa.getRfaEnvelop()) {
			rf.getEnvelopTitle();
			if (CollectionUtil.isNotEmpty(rf.getOpenerUsers())) {
				for (RfaEnvelopeOpenerUser item : rf.getOpenerUsers()) {
					item.getUser();
				}
			}
		}

		if (CollectionUtil.isNotEmpty(rfa.getUnMaskedUsers())) {
			for (RfaUnMaskedUser item : rfa.getUnMaskedUsers()) {
				item.getUser();
			}
		}

		if (CollectionUtil.isNotEmpty(rfa.getEvaluationConclusionUsers())) {
			for (RfaEvaluationConclusionUser usr : rfa.getEvaluationConclusionUsers()) {
				usr.getConcluded();
				usr.getUser().getId();
			}
		}

		if (CollectionUtil.isNotEmpty(rfa.getEvaluationConclusionUsers())) {
			for (RfaEvaluationConclusionUser item : rfa.getEvaluationConclusionUsers()) {
				item.getUser().getName();
			}
		}

		if (rfa != null) {
			rfa.getHistoricaAmount();
			rfa.getDecimal();
		}
		if (rfa.getCostCenter() != null) {
			rfa.getCostCenter().getCostCenter();
		}

		if (CollectionUtil.isNotEmpty(rfa.getSuppliers())) {
			for (RfaEventSupplier supplier : rfa.getSuppliers()) {
				supplier.getSupplier().getFullName();
				supplier.getSupplier().getCommunicationEmail();
				supplier.getSupplier().getCompanyContactNumber();
				supplier.getSupplier().getCompanyName();
				supplier.getSupplier().getStatus();
			}
		}

		if (rfa.getEventOwner().getBuyer() != null) {
			rfa.getEventOwner().getBuyer().getLine1();
			rfa.getEventOwner().getBuyer().getLine2();
			rfa.getEventOwner().getBuyer().getCity();
			if (rfa.getEventOwner().getBuyer().getState() != null) {
				rfa.getEventOwner().getBuyer().getState().getStateName();
				rfa.getEventOwner().getBuyer().getState().getCountry().getCountryName();
			}

		}
		if (rfa.getWinningSupplier() != null) {
			rfa.getWinningSupplier().getCompanyName();
		}

		if (rfa.getBaseCurrency() != null) {
			rfa.getBaseCurrency().getCurrencyCode();
		}

		if (CollectionUtil.isNotEmpty(rfa.getMeetings())) {
			for (RfaEventMeeting meeting : rfa.getMeetings()) {
				meeting.getAppointmentDateTime();
				meeting.getAppointmentTime();
				meeting.getInviteSuppliers();
				meeting.getTitle();
				meeting.getRfxEventMeetingContacts();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
					for (RfaEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
						meetingContact.getContactName();
						meetingContact.getContactNumber();
						meetingContact.getContactEmail();
						meetingContact.getId();

					}
				}
				meeting.getStatus();
				meeting.getVenue();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
					for (RfaEventMeetingDocument meetingDocument : meeting.getRfxEventMeetingDocument()) {
						meetingDocument.getId();
						meetingDocument.getCredContentType();
						meetingDocument.getFileSizeInKb();
						meetingDocument.getFileName();
					}
				}
				meeting.getRemarks();
				if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
					for (Supplier supplier : meeting.getInviteSuppliers()) {
						supplier.getCompanyName();
						supplier.getCommunicationEmail();
					}
				}
			}
		}

		if (rfa.getProcurementMethod() != null) {
			rfa.getProcurementMethod().getProcurementMethod();
		}
		if (rfa.getProcurementCategories() != null) {
			rfa.getProcurementCategories().getProcurementCategories();
		}
		// if (CollectionUtil.isNotEmpty(rfa.getDocuments())) {
		// for (RfaEventDocument item : rfa.getDocuments()) {
		// item.getDescription();
		// item.getFileName();
		// item.getUploadDate();
		// item.getFileData();
		// }
		// }
		if (CollectionUtil.isNotEmpty(rfa.getRfaStartReminder())) {
			for (RfaReminder reminder : rfa.getRfaStartReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfa.getRfaEndReminder())) {
			for (RfaReminder reminder : rfa.getRfaEndReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfa.getComment())) {
			for (RfaComment comment : rfa.getComment()) {
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
		if (CollectionUtil.isNotEmpty(rfa.getIndustryCategories())) {
			for (IndustryCategory indCat : rfa.getIndustryCategories()) {
				indCat.getCode();
			}
		}
		if (rfa.getGroupCode() != null) {
			rfa.getGroupCode().getId();
			rfa.getGroupCode().getGroupCode();
			rfa.getGroupCode().getStatus();
		}

		return rfa;
	}

	// some chages to ssave Rfa
	@Override
	public IndustryCategory getIndustryCategoryForRfaById(String id) {
		return industryCategoryDao.getIndustryCategoryForRftById(id);
	}

	@Override
	public List<RfaEventContact> getAllContactForEvent(String eventId) {
		return rfaEventContactDao.findAllEventContactById(eventId);

	}

	@Override
	public List<RfaEventCorrespondenceAddress> getAllCAddressForEvent(String eventId) {
		List<RfaEventCorrespondenceAddress> list = rfaEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaEventCorrespondenceAddress address : list) {
				LOG.info("State : " + address.getState().getStateName());
				if (address != null && address.getState() != null && address.getState().getCountry() != null) {
					address.getState().getCountry().getCountryName();
				}
			}
		}
		return list;
	}

	@Override
	public List<RfaEventCAddressPojo> getAllCorrespondenceAddressPojo(String eventId) {
		List<RfaEventCAddressPojo> returnList = new ArrayList<RfaEventCAddressPojo>();

		List<RfaEventCorrespondenceAddress> list = rfaEventCorrespondenceAddressDao.findAllEventCAddressById(eventId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaEventCorrespondenceAddress rfaEventCorrespondenceAddress : list) {
				if (rfaEventCorrespondenceAddress.getState() != null)
					rfaEventCorrespondenceAddress.getState().getCountry();
				if (rfaEventCorrespondenceAddress.getState() != null)
					rfaEventCorrespondenceAddress.getState().getStateName();

				RfaEventCAddressPojo rep = new RfaEventCAddressPojo(rfaEventCorrespondenceAddress);
				rep.setCountry(rfaEventCorrespondenceAddress.getState().getCountry().getCountryName());
				returnList.add(rep);
			}
		}

		return returnList;
	}

	@Override
	public List<FavouriteSupplier> searchCustomSupplier(SupplierSearchPojo searchParams) {
		return favoriteSupplierDao.searchFavSuppliers(searchParams);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent loadRfaEventById(String id) {
		RfaEvent event = rfaEventDao.findByEventId(id);
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
			if (event.getRevertBidUser() != null) {
				event.getRevertBidUser().getName();
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
				for (RfaEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}

			/**
			 * Check with Nitin if you want to uncomment these
			 */
			// if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
			// for (RftEventSupplier supplier : event.getSuppliers()) {
			// LOG.info("Company Name : " +
			// supplier.getSupplier().getCompanyName());
			// supplier.getSupplier().getCompanyName();
			// }
			// }
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
				for (RfaTeamMember approver : event.getTeamMembers()) {
					approver.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfaEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfaApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfaEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfaSuspensionApprovalUser user : approver.getApprovalUsers()) {
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
			if (event.getEvaluationProcessDeclaration() != null) {
				event.getEvaluationProcessDeclaration().getContent();
			}
			if (event.getGroupCode() != null) {
				event.getGroupCode().getId();
				event.getGroupCode().getGroupCode();
				event.getGroupCode().getDescription();
			}

			if (event.getTemplate() != null) {
				event.getTemplate().getOptionalSuspendApproval();
				event.getTemplate().getReadOnlySuspendApproval();
				event.getTemplate().getVisibleSuspendApproval();
			}
		}
		return event;
	}

	@Override
	public boolean isExists(RfaEvent rfaEvent) {
		return rfaEventDao.isExists(rfaEvent);
	}

	@Override
	public List<FavouriteSupplier> searchFavouriteSupplierByCompanyNameOrRegistrationNo(String searchParam, String buyerId) {
		return favoriteSupplierDao.searchFavouriteSupplierByCompanyNameOrRegistrationNo(searchParam, buyerId, null);
	}

	@Override
	public List<DraftEventPojo> getAllDraftEventForBuyer(String tenantId) {
		return rfaEventDao.getAllDraftEventsForBuyer(tenantId);
	}

	@Override
	public List<OngoingEventPojo> getAllOngoingEventsForBuyer(String tenantId, Date lastLoginTime) {
		List<OngoingEventPojo> list = rfaEventDao.getAllOngoingEventsForBuyer(tenantId, lastLoginTime);
		return list;
	}

	@Override
	public List<FavouriteSupplier> getFavouriteSupplierBySupplierId(List<Supplier> suppliers) {
		return favoriteSupplierDao.findAllFavouriteSuppliersForSuppliers(suppliers);
	}

	@Override
	public List<RfxView> getAllEventForSupplier(String supplierId) {
		return rfaEventDao.findAllEventForSupplier(supplierId);
	}

	@Override
	public List<FinishedEventPojo> getAllFinishedEventsForBuyer(String tenantId, Date lastLoginTime, int days) {
		List<FinishedEventPojo> list = rfaEventDao.getAllFinishedEventsForBuyer(tenantId, lastLoginTime, days);
		return list;
	}

	@Override
	public RfaEvent loadRfaEventForSummeryById(String id) {
		RfaEvent event = rfaEventDao.findByEventId(id);
		LOG.info("event in service : " + event);
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
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfaEventContact contact : event.getEventContacts()) {
					contact.getContactName();
				}
			}
			/*
			 * if (CollectionUtil.isNotEmpty(event.getSuppliers())) { for (RfaEventSupplier supplier :
			 * event.getSuppliers()) { LOG.info("Company Name : " + supplier.getSupplier().getCompanyName());
			 * supplier.getSupplier().getCompanyName(); } }
			 */
			if (CollectionUtil.isNotEmpty(event.getTeamMembers())) {
				for (RfaTeamMember teamMembers : event.getTeamMembers()) {
					teamMembers.getTeamMemberType();
					teamMembers.getUser().getName();
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
				for (RfaUnMaskedUser unmaskuser : event.getUnMaskedUsers()) {
					unmaskuser.getUser().getCommunicationEmail();
					unmaskuser.getUser().getName();
					unmaskuser.getUser().getPhoneNumber();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RfaEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getCommunicationEmail();
					user.getUser().getName();
					user.getUser().getPhoneNumber();
				}
			}

			// Touch Event reminders...
			if (CollectionUtils.isNotEmpty(event.getRfaStartReminder())) {
				for (RfaReminder reminder : event.getRfaStartReminder()) {
					reminder.getInterval();
				}
			}
			if (CollectionUtils.isNotEmpty(event.getRfaEndReminder())) {
				for (RfaReminder reminder : event.getRfaEndReminder()) {
					reminder.getInterval();
				}
			}

			// if (CollectionUtil.isNotEmpty(event.getDocuments())) {
			// for (RfaEventDocument document : event.getDocuments()) {
			// document.getDescription();
			// }
			// }
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfaEventMeeting meeting : event.getMeetings()) {
					meeting.getRemarks();
					if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
						for (Supplier supplier : meeting.getInviteSuppliers()) {
							supplier.getCompanyName();
						}
					}
					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
						for (RfaEventMeetingContact contact : meeting.getRfxEventMeetingContacts()) {
							contact.getContactName();
						}
					}
					// if
					// (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument()))
					// {
					// for (RfaEventMeetingDocument document :
					// meeting.getRfxEventMeetingDocument()) {
					// document.getFileName();
					// }
					// }
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
				for (RfaEventBq bq : event.getEventBqs()) {
					if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
						for (RfaBqItem item : bq.getBqItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getEventSors())) {
				for (RfaEventSor bq : event.getEventSors()) {
					if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
						for (RfaSorItem item : bq.getSorItems()) {
							item.getItemName();
							if (item.getUom() != null) {
								item.getUom().getUom();
							}
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getComment())) {
				for (RfaComment comment : event.getComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(event.getSuspensionComment())) {
				for (RfaSuspensionComment comment : event.getSuspensionComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfaEventApproval approvalLevel : event.getApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfaApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfaEventSuspensionApproval approvalLevel : event.getSuspensionApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfaSuspensionApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardApprovals())) {
				for (RfaEventAwardApproval approvalLevel : event.getAwardApprovals()) {
					approvalLevel.getLevel();
					if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
						for (RfaAwardApprovalUser user : approvalLevel.getApprovalUsers()) {
							user.getApproval();
							user.getUser().getLoginId();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getAwardComment())) {
				for (RfaAwardComment comment : event.getAwardComment()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
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
		LOG.info("event At last : " + event);
		return event;
	}

	@Override
	public Integer getCountOfEnvelopByEventId(String eventId) {
		return rfaEventDao.getCountOfEnvelopByEventId(eventId);
	}

	@Override
	public Integer getCountOfRfaDocumentByEventId(String eventId) {
		return rfaDocumentDao.getCountOfRfaDocumentByEventId(eventId);
	}

	public Integer getCountOfRfaCqByEventId(String eventId) {
		return rfaCqDao.getCountOfRfaCqByEventId(eventId);
	}

	@Override
	public List<DraftEventPojo> getAllRfaEventByTenantId(String tenantId, String loggedInUser, String pageNo, String searchVal, String indCat) throws SubscriptionException {
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
		List<DraftEventPojo> list = rfaEventDao.getAllRfaEventByTenantId(tenantId, loggedInUser, pageNo, searchVal, indCat);
		return list;
	}

	@Override
	public List<RfaEvent> findByEventNameAndRefNumAndIndCatForTenant(String eventName, String referenceNumber, String industryCategory, String tenantId) {
		List<RfaEvent> rfaList = rfaEventDao.findByEventNameaAndRefNumAndIndCatForTenant(eventName, industryCategory, tenantId, null);
		return rfaList;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public RfaEvent copyFrom(String eventId, User createdBy, BusinessUnit businessUnit) throws SubscriptionException, ApplicationException {
		RfaEvent existingEvent = this.getRfaEventByeventId(eventId);

		if (existingEvent.getGroupCode() != null && Status.INACTIVE == existingEvent.getGroupCode().getStatus()) {
			LOG.error("The group code " + existingEvent.getGroupCode().getGroupCode() + " used in Previous Event is inactive");
			throw new ApplicationException("The group code '" + existingEvent.getGroupCode().getGroupCode() + "' used in Event is inactive");
		}

		if (existingEvent.getDeliveryAddress() != null && existingEvent.getDeliveryAddress().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Delivery address found ....");
			throw new ApplicationException("Delivery address is Inactive for event:" + existingEvent.getEventId());
		} else {
			LOG.info("active Delivery address found ........");
		}
		if (existingEvent.getTemplate() != null && Status.INACTIVE == existingEvent.getTemplate().getStatus()) {
			LOG.info("inactive Template found for Id .... " + existingEvent.getTemplate().getId());
			throw new ApplicationException("Template [" + existingEvent.getTemplate().getTemplateName() + "] used by the event [" + existingEvent.getEventId() + "] is Inactive");
		}

		RfaEvent newEvent = existingEvent.copyFrom(existingEvent);
		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());
		newEvent.setEnableAwardApproval(existingEvent.getEnableAwardApproval());
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFA")) {
			if (businessUnit != null) {
				LOG.info("business unit selected by user choice");
				newEvent.setBusinessUnit(businessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (newEvent.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select");
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

		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFA", newEvent.getBusinessUnit()));
		newEvent.setStatus(EventStatus.DRAFT);

		// Save teamMember
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RfaTeamMember> tm = new ArrayList<RfaTeamMember>();
			for (RfaTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
			}
		}

		// Save unmask

		if (CollectionUtil.isNotEmpty(newEvent.getUnMaskedUsers())) {
			List<RfaUnMaskedUser> tm = new ArrayList<RfaUnMaskedUser>();
			for (RfaUnMaskedUser unmaskUser : newEvent.getUnMaskedUsers()) {
				unmaskUser.setEvent(newEvent);
				tm.add(unmaskUser);
			}
		}

		// save Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getApprovals())) {
			for (RfaEventApproval app : newEvent.getApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfaApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);

					}
				}
			}
		}

		// save Suspension Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getSuspensionApprovals())) {
			for (RfaEventSuspensionApproval app : newEvent.getSuspensionApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfaSuspensionApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);
					}
				}
			}
		}

		// save Award Approvals
		if (CollectionUtil.isNotEmpty(newEvent.getAwardApprovals())) {
			for (RfaEventAwardApproval app : newEvent.getAwardApprovals()) {
				app.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfaAwardApprovalUser appUser : app.getApprovalUsers()) {
						appUser.setApproval(app);
					}
				}
			}
		}

		List<RfaEnvelop> envelops = newEvent.getRfaEnvelop();
		newEvent.setRfaEnvelop(null);
		RfaEvent newDbEvent = this.saveRfaEvent(newEvent, createdBy);

		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			List<RfaTeamMember> tm = new ArrayList<RfaTeamMember>();
			for (RfaTeamMember teamMember : newEvent.getTeamMembers()) {
				LOG.info("Team Member : " + teamMember.getTeamMemberType());
				teamMember.setEvent(newEvent);
				tm.add(teamMember);
				sendTeamMemberEmailNotificationEmail(teamMember.getUser(), teamMember.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFA, newDbEvent.getId());
			}
		}
		// Save contacts
		if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
			for (RfaEventContact contact : newEvent.getEventContacts()) {
				contact.setRfaEvent(newDbEvent);
				saveRfaEventContact(contact);
			}
		}

		// save suppliers
		if (CollectionUtil.isNotEmpty(newEvent.getSuppliers())) {
			for (RfaEventSupplier supp : newEvent.getSuppliers()) {
				supp.setRfxEvent(newDbEvent);
				rfaEventSupplierDao.save(supp);
			}
		}
		List<RfaEventBq> eventBq = new ArrayList<RfaEventBq>();
		// save BQ
		if (CollectionUtil.isNotEmpty(newEvent.getEventBqs())) {
			for (RfaEventBq bq : newEvent.getEventBqs()) {
				bq.setRfxEvent(newDbEvent);
				RfaEventBq dbBq = rfaEventBqDao.saveOrUpdate(bq);
				eventBq.add(dbBq);
				// save BQ Items
				if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
					RfaBqItem parent = null;
					for (RfaBqItem item : bq.getBqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setBq(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rfaBqItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}

			}

		}


		// save SOR
		List<RfaEventSor> eventSor = new ArrayList<RfaEventSor>();
		if (CollectionUtil.isNotEmpty(newEvent.getEventSors())) {
			for (RfaEventSor bq : newEvent.getEventSors()) {
				bq.setRfxEvent(newDbEvent);
				RfaEventSor dbBq = rfaEventSorDao.saveOrUpdate(bq);
				eventSor.add(dbBq);
				if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
					RfaSorItem parent = null;
					for (RfaSorItem item : bq.getSorItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setSor(dbBq);
						item.setRfxEvent(newDbEvent);
						item = rfaSorItemDao.saveOrUpdate(item);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
			}
		}



		List<RfaCq> eventCq = new ArrayList<RfaCq>();
		// save CQ
		if (CollectionUtil.isNotEmpty(newEvent.getCqs())) {
			for (RfaCq cq : newEvent.getCqs()) {
				cq.setRfxEvent(newDbEvent);
				RfaCqItem parent = null;
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfaCqItem item : cq.getCqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
						}
						item.setRfxEvent(newDbEvent);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}
				}
				RfaCq dbCq = rfaCqDao.saveOrUpdate(cq);
				eventCq.add(dbCq);
			}
		}
		// save Doc
		if(CollectionUtil.isNotEmpty(newEvent.getDocuments())){
			for(RfaEventDocument rfaDocument: newEvent.getDocuments()){
				rfaDocument.copyFrom(newEvent);
				LOG.info("Saving document...");
				rfaDocumentService.saveRfaDocuments(rfaDocument);
			}
		}

		// save envelop
		if (CollectionUtil.isNotEmpty(envelops)) {
			for (RfaEnvelop envelop : envelops) {
				envelop.setRfxEvent(newDbEvent);
				List<RfaEventBq> bqsOfEnvelop = new ArrayList<RfaEventBq>();

				List<RfaEventBq> envelopBqs = new ArrayList<RfaEventBq>();
				if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
					for (RfaEventBq bq : envelop.getBqList()) {
						envelopBqs.add(bq);
					}
				}
				for (RfaEventBq evntbq : eventBq) {
					for (RfaEventBq envbq : envelopBqs) {
						if (evntbq.getName().equals(envbq.getName())) {
							bqsOfEnvelop.add(evntbq);
							break;
						}
					}
				}

				List<RfaEventSor> sorsOfEnvelop = new ArrayList<RfaEventSor>();

				List<RfaEventSor> envelopSors = new ArrayList<RfaEventSor>();
				if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
					for (RfaEventSor sor : envelop.getSorList()) {
						envelopSors.add(sor);
					}
				}

				for (RfaEventSor evntsor : eventSor) {
					for (RfaEventSor envsor : envelopSors) {
						if (evntsor.getName().equals(envsor.getName())) {
							sorsOfEnvelop.add(evntsor);
							break;
						}
					}
				}

				List<RfaCq> cqsOfEnvelop = new ArrayList<RfaCq>();
				List<RfaCq> envelopCqs = new ArrayList<RfaCq>();
				if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
					for (RfaCq bq : envelop.getCqList()) {
						envelopCqs.add(bq);
					}
				}

				for (RfaCq evntCq : eventCq) {
					for (RfaCq envcq : envelopCqs) {
						if (evntCq.getName().equals(envcq.getName())) {
							cqsOfEnvelop.add(evntCq);
							break;
						}
					}
				}
				List<RfaEvaluatorUser> evalUser = new ArrayList<RfaEvaluatorUser>();
				if (CollectionUtil.isNotEmpty(envelop.getEvaluators())) {
					for (RfaEvaluatorUser eval : envelop.getEvaluators()) {
						if (eval.getUser().isActive()) {
							eval.setEnvelope(envelop);
							evalUser.add(eval);
						}
					}
				}

				List<RfaEnvelopeOpenerUser> openerUserList = new ArrayList<RfaEnvelopeOpenerUser>();
				if (CollectionUtil.isNotEmpty(envelop.getOpenerUsers())) {
					for (RfaEnvelopeOpenerUser opener : envelop.getOpenerUsers()) {
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
				envelop = rfaEnvelopDao.saveOrUpdate(envelop);

			}
		}

		AuctionRules existingAuctionRules = getAuctionRulesByEventId(eventId);
		AuctionRules newAuctionRules = existingAuctionRules.copyFrom(existingAuctionRules);
		newAuctionRules.setEvent(newDbEvent);
		auctionRulesDao.save(newAuctionRules);
		return newDbEvent;
	}

	@Override
	public boolean isExists(RfaSupplierMeetingAttendance rfaSupplierMeetingAttendance) {
		return rfaSupplierMeetingAttendanceDao.isExists(rfaSupplierMeetingAttendance);

	}

	@Override
	public RfaSupplierMeetingAttendance loadSupplierMeetingAttendenceByEventId(String meetingId, String eventId, String tenantId) {
		return rfaSupplierMeetingAttendanceDao.attendenceByEventId(meetingId, eventId, tenantId);

	}

	@Override
	public List<Supplier> getEventSuppliers(String eventId) {
		return rfaEventDao.getEventSuppliers(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent copyFromTemplate(String templateId, User createdBy, BusinessUnit buyerbusinessUnit) throws SubscriptionException, ApplicationException {
		RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateById(templateId);
		if (rfxTemplate != null && Status.INACTIVE == rfxTemplate.getStatus()) {
			LOG.info("inactive Template [" + rfxTemplate.getTemplateName() + "] found for Id .... " + rfxTemplate.getId());
			throw new ApplicationException("Template [" + rfxTemplate.getTemplateName() + "] is Inactive");
		}

		RfaEvent newEvent = new RfaEvent();
		AuctionRules auctionRules = new AuctionRules();
		newEvent.setTemplate(rfxTemplate);
		if (rfxTemplate.getType() == RfxTypes.RFA) {
			newEvent.setAuctionType(rfxTemplate.getTemplateAuctionType());
		}
		newEvent.setCreatedBy(createdBy);
		newEvent.setCreatedDate(new Date());
		newEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
		newEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
		newEvent.setAddSupplier(rfxTemplate.getAddSupplier());
		newEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
		newEvent.setViewAuctionHall(rfxTemplate.getViewAuctionHall());
		newEvent.setRevertLastBid(rfxTemplate.getRevertLastBid());
		newEvent.setRevertBidUser(rfxTemplate.getRevertBidUser());
		newEvent.setUnMaskedUser(rfxTemplate.getUnMaskedUser());
		newEvent.setAddBillOfQuantity(rfxTemplate.getAddBillOfQuantity());
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newEvent.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newEvent.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		newEvent.setEnableSuspensionApproval(rfxTemplate.getEnableSuspendApproval());
		newEvent.setAllowDisqualifiedSupplierDownload(rfxTemplate.getAllowDisqualifiedSupplierDownload());
		newEvent.setEnableAwardApproval(rfxTemplate.getEnableAwardApproval());

		// copy unmasking User

		List<RfaUnMaskedUser> unmaskingUser = new ArrayList<RfaUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RfaUnMaskedUser newRfiUnMaskedUser = new RfaUnMaskedUser();
				newRfiUnMaskedUser.setUser(team.getUser());
				newRfiUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRfiUnMaskedUser);

			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}

		// Copy Evaluation Conclusion Users from template - PH-999
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RfaEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RfaEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RfaEvaluationConclusionUser conclusionUser = new RfaEvaluationConclusionUser();
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

		// copy approval from template
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RfaEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfaEventApproval newRfApproval = new RfaEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfaApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfaApprovalUser approvalUser = new RfaApprovalUser();
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
			List<RfaEventSuspensionApproval> approvalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfaEventSuspensionApproval newRfApproval = new RfaEventSuspensionApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfaSuspensionApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfaSuspensionApprovalUser approvalUser = new RfaSuspensionApprovalUser();
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
			List<RfaEventAwardApproval> approvalList = new ArrayList<>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfaEventAwardApproval newRfApproval = new RfaEventAwardApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfaAwardApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfaAwardApprovalUser approvalUser = new RfaAwardApprovalUser();
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
						// LOG.info("businessUnit : " + businessUnit + "Default
						// value : " + field.getDefaultValue());
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

						// Setting multiple event categories previously it was
						// only one
						String[] icArr = field.getDefaultValue().split(",");
						List<IndustryCategory> icList = new ArrayList<IndustryCategory>();
						if (icArr != null) {
							for (String icId : icArr) {
								// IndustryCategory ic =
								// industryCategoryService.getIndustryCategoryById(icId);
								IndustryCategory ic = new IndustryCategory();
								ic.setId(icId);
								icList.add(ic);
							}
							newEvent.setIndustryCategories(icList);
						}
						// newEvent.setIndustryCategory(industryCategory);
						// LOG.info("industryCategory : " + industryCategory +
						// "Default value : " +
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
				case _UNKNOWN:
					if (field.getDefaultValue() != null) {
					}
					break;
				case AUCTION_PRICE_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setAuctionConsolePriceType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;
				case AUCTION_RANK_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setAuctionConsoleRankType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;
				case AUCTION_SUPPLIER_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setAuctionConsoleVenderType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;

				case AUCTION_BUY_PRICE_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBuyerAuctionConsolePriceType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;
				case AUCTION_BUY_RANK_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBuyerAuctionConsoleRankType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;
				case AUCTION_BUY_SUPPLIER_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBuyerAuctionConsoleVenderType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;

				case BIDDER_DISQUALIFY_COUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setBidderDisqualify(Integer.parseInt(field.getDefaultValue()));
					}
					break;
				case BIDDING_TYPE:
					if (field.getDefaultValue() != null) {
						if (field.getDefaultValue().equals("ITEMIZEDWITHTAX")) {
							auctionRules.setItemizedBiddingWithTax(Boolean.TRUE);
						} else if (field.getDefaultValue().equals("ITEMIZEDWITHOUTTAX")) {
							auctionRules.setItemizedBiddingWithTax(Boolean.FALSE);
						} else if (field.getDefaultValue().equals("LUMPSUMWITHTAX")) {
							auctionRules.setLumsumBiddingWithTax(Boolean.TRUE);
						} else if (field.getDefaultValue().equals("LUMPSUMWITHOUTTAX")) {
							auctionRules.setLumsumBiddingWithTax(Boolean.FALSE);
						}
						// auctionRules.setItemizedBiddingWithTax(field.getDefaultValue());
					}
					break;
				case BID_HIGHER_LEAD_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBiddingPriceHigherLeadingBidType(ValueType.valueOf(field.getDefaultValue()));
					}
					break;
				case BID_HIGHER_LEAD_VAL:
					if (field.getDefaultValue() != null) {
						auctionRules.setBiddingPriceHigherLeadingBidValue(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case BID_INCR_OWN_PRE_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBiddingMinValueType(ValueType.valueOf(field.getDefaultValue()));
					}
					break;
				case BID_INCR_OWN_PRE_VAL:
					if (field.getDefaultValue() != null) {
						auctionRules.setBiddingMinValue(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case EXTENSION_COUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setExtensionCount(Integer.parseInt(field.getDefaultValue()));
					}
					break;
				case IS_AUTO_BIDDER_DISQUALIFY:
					if (field.getDefaultValue() != null) {
						newEvent.setAutoDisqualify(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case IS_BIDDING_SAMEPRICE_SUPPLIER:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsBiddingAllowSupplierSameBid(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case IS_BID_HIGHER_LEADING:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsBiddingPriceHigherLeadingBid(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case IS_BID_INCR_OWN_PREVIOUS:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsBiddingMinValueFromPrevious(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case PRE_BID_BY:
					if (field.getDefaultValue() != null) {
						auctionRules.setPreBidBy(PreBidByType.valueOf(field.getDefaultValue()));
					}
					break;
				case PRE_SUPPLIER_PROVIDE_HIGHER:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsPreBidHigherPrice(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case PRE_SUPPLIER_SAME_BID:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsPreBidSameBidPrice(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case START_GATE:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsStartGate(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case TIME_EXT_DURATION_TYPE:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionDurationType(DurationType.valueOf(field.getDefaultValue()));
					}
					break;
				case TIME_EXT_TYPE:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionType(TimeExtensionType.valueOf(field.getDefaultValue()));
					}
					break;
				case TIME_TRIGGER_TYPE:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionLeadingBidType(DurationType.valueOf(field.getDefaultValue()));
					}
					break;
				case TIME_TRIGGER_VAL:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionLeadingBidValue(Integer.parseInt(field.getDefaultValue()));
					}
					break;
				case TIME_EXT_DURATION:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionDuration(Integer.parseInt(field.getDefaultValue()));
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
				case PRE_AS_FIRSTBID:
					if (field.getDefaultValue() != null) {
						auctionRules.setPrebidAsFirstBid(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case DISABLE_TOTAL_AMOUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setDisableTotalAmount(Boolean.valueOf(field.getDefaultValue()));
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
				case PRE_SET_SAME_PRE_BID_ALL_SUPPLIER:
					if (field.getDefaultValue() != null) {
						auctionRules.setPreSetSamePreBidForAllSuppliers(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				default:
					break;

				}
			}

		}

		// if buyer setting is enable for id generation upon business unit then
		// user can select the own business unit
		if (eventIdSettingsDao.isBusinessSettingEnable(createdBy.getTenantId(), "RFA")) {
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
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(createdBy.getTenantId(), "RFA", newEvent.getBusinessUnit()));
		newEvent = this.saveRfaEvent(newEvent, createdBy);

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

				RfaEnvelop rfaEnvlop = new RfaEnvelop();
				rfaEnvlop.setEnvelopTitle(en.getRfxEnvelope());
				rfaEnvlop.setEnvelopSequence(en.getRfxSequence());
				rfaEnvlop.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rfaEnvlop.setRfxEvent(newEvent);
				rfaEnvlop.setEnvelopType(EnvelopType.CLOSED);
				envelopService.saveRfaEnvelop(rfaEnvlop, null, null, null);
			}
		}

		List<RfaTeamMember> teamMembers = new ArrayList<RfaTeamMember>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RfaTeamMember newTeamMembers = new RfaTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setEvent(newEvent);
				teamMembers.add(newTeamMembers);
				sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), createdBy, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", rfxTemplate.getType(), newEvent.getId());
			}
			newEvent.setTeamMembers(teamMembers);
		}
		setDefaultEventContactDetail(createdBy.getId(), newEvent.getId());
		if (newEvent.getAuctionType() == AuctionType.FORWARD_DUTCH || newEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || newEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
			auctionRules.setFowardAuction(Boolean.TRUE);
		} else {
			auctionRules.setFowardAuction(Boolean.FALSE);
		}
		// AuctionRules auctionRules = new AuctionRules();
		auctionRules.setAuctionType(newEvent.getAuctionType());
		auctionRules.setEvent(newEvent);

		// this code is commented for further enhancement of template.

		// auctionRules.setAmountPerIncrementDecrement(auctionRules.getAmountPerIncrementDecrement());
		// auctionRules.setAuctionConsolePriceType(auctionRules.getAuctionConsolePriceType());
		// auctionRules.setAuctionConsoleRankType(auctionRules.getAuctionConsoleRankType());
		// auctionRules.setAuctionConsoleVenderType(auctionRules.getAuctionConsoleVenderType());
		// auctionRules.setAuctionType(auctionRules.getAuctionType());
		// auctionRules.setBiddingMinValue(auctionRules.getBiddingMinValue());
		// auctionRules.setBiddingMinValueType(auctionRules.getBiddingMinValueType());
		// auctionRules.setBiddingPriceHigherLeadingBidType(auctionRules.getBiddingPriceHigherLeadingBidType());
		// auctionRules.setBiddingPriceHigherLeadingBidValue(auctionRules.getBiddingPriceHigherLeadingBidValue());
		// auctionRules.setDutchMinimumPrice(auctionRules.getDutchMinimumPrice());
		// auctionRules.setDutchStartPrice(auctionRules.getDutchStartPrice());
		// // Setting new Event
		// auctionRules.setEvent(newEvent);
		// auctionRules.setFowardAuction(auctionRules.getFowardAuction());
		// auctionRules.setInterval(auctionRules.getInterval());
		// auctionRules.setIntervalType(auctionRules.getIntervalType());
		// auctionRules.setIsBiddingAllowSupplierSameBid(auctionRules.getIsBiddingAllowSupplierSameBid());
		// auctionRules.setIsBiddingMinValueFromPrevious(auctionRules.getIsBiddingMinValueFromPrevious());
		// auctionRules.setIsBiddingPriceHigherLeadingBid(auctionRules.getIsBiddingPriceHigherLeadingBid());
		// auctionRules.setIsPreBidPrerequisite(auctionRules.getIsPreBidPrerequisite());
		// auctionRules.setIsPreBidHigherPrice(auctionRules.getIsPreBidHigherPrice());
		// auctionRules.setIsPreBidSameBidPrice(auctionRules.getIsPreBidSameBidPrice());
		// auctionRules.setIsStartGate(auctionRules.getIsStartGate());
		// auctionRules.setPreBidBy(auctionRules.getPreBidBy());
		auctionRulesDao.save(auctionRules);
		LOG.info("All new event :  " + newEvent.getIndustryCategory() + "   :Cost Center :   " + newEvent.getCostCenter());
		return newEvent;
	}

	@Override
	public RfaEventContact getEventContactById(String id) {
		return rfaEventContactDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfaContact(RfaEventContact eventContact) {
		rfaEventContactDao.delete(eventContact);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveEventReminder(RfaReminder rfaReminder) {
		rfaReminderDao.saveOrUpdate(rfaReminder);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventReminder(RfaReminder rfaReminder) {
		rfaReminderDao.update(rfaReminder);
	}

	@Override
	public List<RfaReminder> getAllRfaEventReminderForEvent(String eventId, Boolean startReminder) throws ApplicationException {
		return rfaReminderDao.getAllRfaEventReminderForEvent(eventId, startReminder);

	}

	@Override
	public RfaReminder getEventReminderById(String id) {
		return rfaReminderDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfaReminder(RfaReminder rfaReminder) {
		rfaReminderDao.delete(rfaReminder);
	}

	@Override
	public List<RfaEvent> getAllFinishAndApprovedRfaEventByTenantId(String tenantId) {
		return rfaEventDao.getAllFinishAndApprovedRfaEventByTenantId(tenantId);
	}

	@Override
	public List<RfaEvent> findRfaEventByNameAndTenantId(String searchValue, String tenantId) {
		return rfaEventDao.findRfaEventByNameAndTenantId(searchValue, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addEditorUser(String eventId, String userId) {
		RfaEvent rfaEvent = getRfaEventByeventId(eventId);
		List<User> editors = rfaEvent.getEventEditors();
		if (editors == null) {
			editors = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		editors.add(user);
		rfaEvent.setEventEditors(editors);
		updateRfaEvent(rfaEvent);
		return editors;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> addViewersToList(String eventId, String userId) {
		RfaEvent rfaEvent = getRfaEventByeventId(eventId);
		List<User> viewers = rfaEvent.getEventViewers();
		if (viewers == null) {
			viewers = new ArrayList<User>();
		}
		User user = userService.getUsersById(userId);
		viewers.add(user);
		updateRfaEvent(rfaEvent);
		return viewers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfaTeamMember> addTeamMemberToList(String eventId, String userId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " eventId: " + eventId + " userId: " + userId);
		RfaEvent rfaEvent = getRfaEventByeventId(eventId);
		List<RfaTeamMember> teamMembers = rfaEvent.getTeamMembers();
		LOG.info("teamMembers :********: " + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfaTeamMember>();
		}
		User user = userService.findTeamUserById(userId);

		RfaTeamMember rfaTeamMember = new RfaTeamMember();
		rfaTeamMember.setEvent(rfaEvent);
		rfaTeamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (RfaTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				rfaTeamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;

			}
		}
		rfaTeamMember.setTeamMemberType(memberType);
		if (!exists) {
			teamMembers.add(rfaTeamMember);
		}
		LOG.info("TeamMembers : " + rfaTeamMember.toLogString());

		rfaEvent.setTeamMembers(teamMembers);
		rfaEventDao.update(rfaEvent);

		try {
			if (!exists) {
				RfaEventAudit audit = new RfaEventAudit(null, rfaEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.added", new Object[] { user.getName(), memberType.getValue() }, Global.LOCALE), null);
				eventAuditService.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been added as '" + memberType.getValue() + "' for event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			} else {
				if (!previousMemberType.equalsIgnoreCase(memberType.getValue())) {
					RfaEventAudit audit = new RfaEventAudit(null, rfaEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.changed", new Object[] { user.getName(), previousMemberType, memberType.getValue() }, Global.LOCALE), null);
					eventAuditService.save(audit);
					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been changed from '" + previousMemberType + "' to '" + memberType.getValue() + "' for Event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}

		/*
		 * List<User> userList = new ArrayList<User>(); LOG.info("rfaEvent.getTeamMembers(): " +
		 * rfaEvent.getTeamMembers()); for (RfaTeamMember app : rfaEvent.getTeamMembers()) { LOG.info(app.getUser() +
		 * "---------------------	TeamMember"); userList.add(app.getUser()); }
		 */
		return teamMembers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeEditorUser(String eventId, String userId) {
		RfaEvent rfaEvent = getRfaEventByeventId(eventId);
		User user = userService.getUsersById(userId);
		if (rfaEvent.getEventEditors() != null) {
			rfaEvent.getEventEditors().remove(user);
		}
		updateRfaEvent(rfaEvent);
		return rfaEvent.getEventEditors();
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeViewersfromList(String eventId, String userId) {
		RfaEvent rfaEvent = getRfaEventByeventId(eventId);
		User user = userService.getUsersById(userId);
		if (rfaEvent.getEventViewers() != null) {
			rfaEvent.getEventViewers().remove(user);
		}
		updateRfaEvent(rfaEvent);
		return rfaEvent.getEventViewers();
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeTeamMemberfromList(String eventId, String userId, RfaTeamMember dbTeamMember) {
		RfaEvent rfaEvent = getRfaEventByeventId(eventId);
		List<RfaTeamMember> teamMembers = rfaEvent.getTeamMembers();
		LOG.info("**************" + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<RfaTeamMember>();
		}

		teamMembers.remove(dbTeamMember);
		dbTeamMember.setEvent(null);
		rfaEvent.setTeamMembers(teamMembers);
		rfaEventDao.update(rfaEvent);

		try {
			RfaEventAudit audit = new RfaEventAudit(null, rfaEvent, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Update, messageSource.getMessage("event.team.member.audit.removed", new Object[] { dbTeamMember.getUser().getName(), dbTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), null);
			eventAuditService.save(audit);

			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + dbTeamMember.getUser().getName() + "' has been removed from '" + dbTeamMember.getTeamMemberType().getValue() + "' for Event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error removing audit details: " + e.getMessage());
		}

		List<User> userList = new ArrayList<User>();
		LOG.info("TeamMembers getTeamMembers(): " + rfaEvent.getTeamMembers());
		for (RfaTeamMember app : rfaEvent.getTeamMembers()) {
			try {
				userList.add((User) app.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error constructing list of users after removing TeamMembers operation : " + e.getMessage(), e);
			}
		}
		return userList;
	}

	@Override
	public RfaTeamMember getRfaTeamMemberByUserIdAndEventId(String eventId, String userId) {
		return rfaEventDao.getRfaTeamMemberByUserIdAndEventId(eventId, userId);
	}

	/*
	 * @Override
	 * @Transactional(readOnly = false) public List<User> reorderApprovers(String eventId, String[] approverList) {
	 * RfaEvent rfaEvent = getRfaEventByeventId(eventId); List<RfaTeamMember> approvers = rfaEvent.getEventApprovers();
	 * int index = 1; for (String id : approverList) { for (RfaTeamMember app : approvers) { if
	 * (app.getUser().getId().equals(id)) { app.setApproverPosition(index); break; } } index++; }
	 * rfaEvent.setEventApprovers(approvers); rfaEvent = rfaEventDao.update(rfaEvent);
	 * Collections.sort(rfaEvent.getEventApprovers()); List<User> userList = new ArrayList<User>(); for (RfaTeamMember
	 * approver : rfaEvent.getEventApprovers()) { try { userList.add((User) approver.getUser().clone()); } catch
	 * (Exception e) { LOG.error("Error constructing list of users after reorder operation : " + e.getMessage(), e); } }
	 * return userList; }
	 */
	@Override
	@Transactional(readOnly = false)
	public AuctionRules saveAuctionRules(AuctionRules auctionRules) {
		return auctionRulesDao.saveOrUpdate(auctionRules);
	}

	@Override
	public AuctionRules getAuctionRulesByEventId(String eventId) {
		return auctionRulesDao.findAuctionRulesByEventId(eventId);
	}

	@Override
	public AuctionRules getAuctionRulesById(String id) {
		return auctionRulesDao.findById(id);
	}

	@Override
	public AuctionRules getAuctionRulesWithEventById(String id) {
		return auctionRulesDao.findAuctionRulesWithEventById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public AuctionRules updateAuctionRules(AuctionRules auctionRules) {
		return auctionRulesDao.update(auctionRules);
	}

	@Override
	public boolean isExists(RfaEventContact rfaEventContact) {
		return rfaEventContactDao.isExists(rfaEventContact);
	}

	@Override
	public List<EventTeamMember> getTeamMembersForEvent(String eventId) {
		return rfaEventDao.getTeamMembersForEvent(eventId);
	}

	@Override
	public BigDecimal findAuctionRulesCurrentPriceForStepNo(Integer currentStep, String auctionId) {
		return auctionRulesDao.findAuctionRulesCurrentPriceForStepNo(currentStep, auctionId);
	}

	@Override
	@Transactional(readOnly = false)
	public synchronized void submitDutchAuction(Integer currentStep, String auctionId, RfaEvent event, User loggedInUser, String loggedInTenantId, String ipAddress) throws ApplicationException {
		event = getPlainEventById(event.getId());
		if (event.getWinningSupplier() == null) {

			BigDecimal currentStepPrice = auctionRulesDao.findAuctionRulesCurrentPriceForStepNo(currentStep, auctionId);
			// Supplier supplier = supplierDao.findSuppById(loggedInTenantId);
			Supplier supplier = new Supplier();
			supplier.setId(loggedInTenantId);
			LOG.info("currentStep : " + currentStep + "  auctionId  " + auctionId + " :event :" + event.getId() + " loggedInUser : " + loggedInUser + " :loggedInTenantId : " + loggedInTenantId);
			EventStatus status = EventStatus.CLOSED;
			if (!event.getBillOfQuantity() && !event.getQuestionnaires()) {
				status = EventStatus.COMPLETE;
			}
			rfaEventDao.updateDuctionAuctionWinningSupplier(event.getId(), supplier, currentStepPrice, status);

			// Update the revised bid grand total - The supplier will have to
			// submit
			// the revised BQ based on this later.
			rfaSupplierBqDao.updateDuctionAuctionBidForSupplier(event.getId(), loggedInTenantId, currentStepPrice);

			RfaEventSupplier eventSupplier = rfaEventSupplierDao.getEventSupplierBySupplierAndEventId(loggedInTenantId, event.getId());
			eventSupplier.setBidByUser(loggedInUser);
			eventSupplier.setBidDateAndTime(new Date());
			eventSupplier.setIpAddress(ipAddress);
			rfaEventSupplierDao.update(eventSupplier);

			String timeZone = "GMT+8:00";
			String eventId = event.getId();
			try {
				List<String> suppIdList = rfaEventDao.getEventSuppliersId(eventId);
				String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + RfxTypes.RFA.name() + "/" + eventId;
				if (CollectionUtil.isNotEmpty(suppIdList)) {
					for (String suppId : suppIdList) {
						timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);

						// Sending Mails and Notifications for supplier admin
						// users
						List<User> userList = userDao.getAllAdminPlainUsersForSupplier(suppId);
						for (User adminUser : userList) {
							try {
								sendEventClosedNotification(adminUser, event, suppUrl, timeZone, RfxTypes.RFA.getValue());
							} catch (Exception e) {
								LOG.error("Error while sending nitification to : " + adminUser.getName() + ", " + e.getMessage(), e);
							}
						}

					}
				}
				String buyerUrl = APP_URL + "/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + eventId;
				// Sending Mails and Notifications for Event owner
				User rfaEventOwner = rfaEventDao.getPlainEventOwnerByEventId(eventId);
				timeZone = getTimeZoneByBuyerSettings(rfaEventOwner, timeZone);
				sendEventClosedNotification(rfaEventOwner, event, buyerUrl, timeZone, RfxTypes.RFA.getValue());
				// Sending Mails and Notifications for event buyer team members
				List<RfaTeamMember> rfaBuyermembers = rfaEventDao.getBuyerTeamMemberByEventId(eventId);
				if (CollectionUtil.isNotEmpty(rfaBuyermembers)) {
					for (RfaTeamMember buyerTeamMember : rfaBuyermembers) {
						try {
							sendEventClosedNotification(buyerTeamMember.getUser(), event, buyerUrl, timeZone, RfxTypes.RFA.getValue());
						} catch (Exception e) {
							LOG.error("Error while sending nitification to : " + buyerTeamMember.getUser().getName() + ", " + e.getMessage(), e);
						}
					}
				}

				int countClosedEnvelop = rfaEnvelopService.getcountClosedEnvelop(eventId);
				if (countClosedEnvelop > 0) {
					List<RfaEnvelop> envelopList = rfaEnvelopService.getAllClosedEnvelopAndOpener(eventId);
					if (CollectionUtil.isNotEmpty(envelopList)) {
						List<String> envelopTitleList = new ArrayList<>();
						for (RfaEnvelop rfaEnvelop : envelopList) {
							try {
								sendEnvelopOpenedNotification(rfaEnvelop.getOpener(), event, buyerUrl, timeZone, RfxTypes.RFA.getValue(), null, rfaEnvelop.getEnvelopTitle());
								envelopTitleList.add(rfaEnvelop.getEnvelopTitle());
							} catch (Exception e) {
								LOG.error("Error while sending nitification to : " + rfaEnvelop.getOpener().getName() + ", " + e.getMessage(), e);
							}
						}
						try {
							sendEnvelopOpenedNotification(rfaEventOwner, event, buyerUrl, timeZone, RfxTypes.RFA.getValue(), envelopTitleList, null);
						} catch (Exception e) {
							LOG.error("Error while sending nitification to : " + rfaEventOwner.getName() + ", " + e.getMessage(), e);
						}
					}
				} else {
					List<User> evaluatorList = rfaEnvelopService.getAllEnvelopEvaluatorUsers(eventId);
					if (CollectionUtil.isNotEmpty(evaluatorList)) {
						for (User evaluator : evaluatorList) {
							try {
								sendEventReadyEvaluationNotification(evaluator, event, buyerUrl, timeZone, RfxTypes.RFA.getValue());
							} catch (Exception e) {
								LOG.error("Error while sending nitification to : " + evaluator.getName() + ", " + e.getMessage(), e);
							}
						}
						try {
							sendEventReadyEvaluationNotification(rfaEventOwner, event, buyerUrl, timeZone, RfxTypes.RFA.getValue());
						} catch (Exception e) {
							LOG.error("Error while sending nitification to : " + rfaEventOwner.getName() + ", " + e.getMessage(), e);
						}
					}
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification for ducth auction closed : " + e.getMessage(), e);
			}
			try {
				RfaEventAudit audit = new RfaEventAudit(event, null, new Date(), AuditActionType.Close, messageSource.getMessage("event.audit.close", new Object[] { event.getEventName() }, Global.LOCALE));
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error("Error while audit dutch auction closed : " + e.getMessage(), e);
			}
		} else {
			LOG.error("Auction already concluded by another supplier");
			throw new ApplicationException("Auction already concluded by another supplier");
		}
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent suspendDutchAuction(RfaEvent event) {
		AuctionRules auctionRules = auctionRulesDao.findAuctionRulesByEventId(event.getId());

		RfaEvent persistObj = getPlainEventById(event.getId());
		JobKey jobKey = new JobKey("JOB" + auctionRules.getId(), "DUTCHAUCTION");
		try {
			LOG.info("at Starting stage date time  : " + new Date());
			LOG.info("Scheduler check exists Before  : " + schedulerFactoryBean.getScheduler().checkExists(jobKey));
			if (schedulerFactoryBean.getScheduler().checkExists(jobKey)) {
				LOG.info("Pause the job  : " + jobKey);
				schedulerFactoryBean.getScheduler().pauseJob(jobKey);
				LOG.info("Scheduler check exists  after : " + schedulerFactoryBean.getScheduler().checkExists(jobKey));
			}
			persistObj.setAuctionSuspandAmount(auctionRules.getDutchAuctionCurrentStepAmount());
			persistObj.setStatus(EventStatus.SUSPENDED);
			persistObj.setSuspendRemarks(event.getSuspendRemarks());
			rfaEventDao.update(persistObj);
			LOG.info("Dutch auction (auctionId): " + persistObj.getId() + " Suspended by : " + SecurityLibrary.getLoggedInUser());
		} catch (Exception e) {
			LOG.error("Error during suspend auction by buyer : " + e.getMessage(), e);
			try {
				if (schedulerFactoryBean.getScheduler().checkExists(jobKey)) {
					schedulerFactoryBean.getScheduler().resumeJob(jobKey);
				}
			} catch (SchedulerException e1) {
				LOG.error("Error during suspend auction by buyer : " + e1.getMessage(), e1);
			}
		}
		return persistObj;
	}

	@Override
	public EventPermissions getUserPemissionsForEvent(String userId, String eventId) {
		return rfaEventDao.getUserPemissionsForEvent(userId, eventId);
	}

	@Override
	@Transactional(readOnly = true)
	public EventPermissions getUserPemissionsForEnvelope(String userId, String eventId, String envelopeId) {
		return rfaEventDao.getUserPemissionsForEnvelope(userId, eventId, envelopeId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent updateEventApproval(RfaEvent event, User loggedInUser) {
		RfaEvent persistObj = rfaEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<>();
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfaEventApproval approvalRequest : persistObj.getApprovals()) {
			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfaApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}
		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			int level = 1;
			for (RfaEventApproval app : event.getApprovals()) {
				app.setEvent(event);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;

				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfaApprovalUser approvalUser : app.getApprovalUsers()) {
						approvalUser.setApproval(app);
						approvalUser.setId(null);

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
					if (CollectionUtil.isNotEmpty(existingUserList)) {
						for (String existing : existingUserList) {
							if (!levelUsers.contains(existing)) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + existing + " has been removed as Approver");
							}
						}
					}
				}
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
		persistObj = rfaEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfaEventAudit audit = new RfaEventAudit(persistObj, loggedInUser, new Date(), AuditActionType.Update, auditMessage);
				rfaEventAuditDao.save(audit);
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" + persistObj.getEventId() + "' ." + auditMessage, loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
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
	public BidHistoryPojo getMinMaxBidPriceForEvent(String eventId) {
		return rfaEventDao.getMinMaxBidPriceForEvent(eventId);
	}

	@Override
	public List<AuctionSupplierBidPojo> getAuctionBidsListBySupplierIdAndEventId(String supplierId, String eventId) {
		return auctionBidsDao.getAuctionBidsListBySupplierIdAndEventId(supplierId, eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void insertTimeLine(String id) throws ApplicationException {
		try {

			rfaEventTimeLineDao.deleteTimeline(id);

			RfaEvent rfaEvent = rfaEventDao.findById(id);
			// Publish Date
			RfaEventTimeLine timeline = new RfaEventTimeLine();
			timeline.setActivityDate(rfaEvent.getEventPublishDate());
			LOG.info("PublishDate:" + rfaEvent.getEventPublishDate());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfaEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.publish.date", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
			rfaEventTimeLineDao.save(timeline);

			// Event Start
			timeline = new RfaEventTimeLine();
			timeline.setActivityDate(rfaEvent.getEventStart());
			LOG.info("StartDate:" + rfaEvent.getEventStart());
			timeline.setActivity(EventTimelineType.EVENT);
			timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			timeline.setEvent(rfaEvent);
			timeline.setDescription(messageSource.getMessage("timeline.event.start.date", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
			rfaEventTimeLineDao.save(timeline);

			// End Date
			if (rfaEvent.getEventEnd() != null) {
				timeline = new RfaEventTimeLine();
				timeline.setActivityDate(rfaEvent.getEventEnd());
				LOG.info("EndDate:" + rfaEvent.getEventEnd());
				timeline.setActivity(EventTimelineType.EVENT);
				timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				timeline.setEvent(rfaEvent);
				timeline.setDescription(messageSource.getMessage("timeline.event.end.date", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
				rfaEventTimeLineDao.save(timeline);
			}

			// Event Reminders
			if (CollectionUtil.isNotEmpty(rfaEvent.getRfaStartReminder())) {
				// Start Reminders
				for (RfaReminder reminder : rfaEvent.getRfaStartReminder()) {
					timeline = new RfaEventTimeLine();
					timeline.setActivityDate(reminder.getReminderDate());
					LOG.info("EventReminderDates:" + reminder.getReminderDate());
					timeline.setActivity(EventTimelineType.REMINDER);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfaEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.reminder.date", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
					rfaEventTimeLineDao.save(timeline);
				}
			}
			if (CollectionUtil.isNotEmpty(rfaEvent.getRfaEndReminder())) {
				// End Reminders
				for (RfaReminder reminder : rfaEvent.getRfaEndReminder()) {
					timeline = new RfaEventTimeLine();
					timeline.setActivityDate(reminder.getReminderDate());
					LOG.info("EventReminderDates:" + reminder.getReminderDate());
					timeline.setActivity(EventTimelineType.REMINDER);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfaEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.reminder.date", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
					rfaEventTimeLineDao.save(timeline);
				}
			}

			// Meetings
			if (CollectionUtil.isNotEmpty(rfaEvent.getMeetings())) {
				for (RfaEventMeeting meeting : rfaEvent.getMeetings()) {
					// Meeting
					timeline = new RfaEventTimeLine();
					timeline.setActivityDate(meeting.getAppointmentDateTime());
					LOG.info("MeetingDates:" + meeting.getAppointmentDateTime());
					timeline.setActivity(EventTimelineType.MEETING);
					timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					timeline.setEvent(rfaEvent);
					timeline.setDescription(messageSource.getMessage("timeline.event.meeting.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
					rfaEventTimeLineDao.save(timeline);

					if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingReminder())) {
						// Meeting Reminders
						for (RfaEventMeetingReminder reminder : meeting.getRfxEventMeetingReminder()) {
							timeline = new RfaEventTimeLine();
							timeline.setActivityDate(reminder.getReminderDate());
							LOG.info("MeetingReminderDates:" + reminder.getReminderDate());
							timeline.setActivity(EventTimelineType.REMINDER);
							timeline.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
							timeline.setEvent(rfaEvent);
							timeline.setDescription(messageSource.getMessage("timeline.event.meeting.reminder.date", new Object[] { meeting.getTitle() }, Global.LOCALE));
							rfaEventTimeLineDao.save(timeline);
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
	public List<RfaEventTimeLine> getRfaEventTimeline(String id) {
		return rfaEventTimeLineDao.getRfaEventTimeline(id);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public RfaEvent cancelEvent(RfaEvent event, HttpSession session, JRSwapFileVirtualizer virtualizer, User logedUser) throws Exception {
		RfaEvent persistObj = rfaEventDao.findById(event.getId());
		persistObj.setStatus(EventStatus.CANCELED);
		persistObj.setCancelReason(event.getCancelReason());
		persistObj = rfaEventDao.update(persistObj);

		// Decrease event count on cancel
		buyerDao.decreaseEventLimitCountByBuyerId(logedUser.getBuyer().getId());

		if (persistObj != null) {

			byte[] summarySnapshot = null;
			try {
				JasperPrint eventSummary = getEvaluationSummaryPdf(event, logedUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			} catch (JRException e) {
				LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
			}

			try {
				RfaEventAudit audit = new RfaEventAudit(logedUser.getBuyer(), persistObj, logedUser, new java.util.Date(), AuditActionType.Cancel, messageSource.getMessage("event.audit.canceled", new Object[] { persistObj.getEventName() }, Global.LOCALE), summarySnapshot);
				rfaEventAuditDao.save(audit);
			} catch (Exception e) {
				LOG.error("Error creating cancel rfa audit : " + e.getMessage(), e);
			}

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Event '" + persistObj.getEventId() + "' Cancelled", logedUser.getTenantId(), logedUser, new Date(), ModuleType.RFA);
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

	public JasperPrint generatePdfforEvaluationSummary(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rfaSummary = new ArrayList<EvaluationPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		generatedSdf.setTimeZone(timeZone);
		parameters.put("generatedOn", generatedSdf.format(new Date()));

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			summary.setGeneratedOn(generatedSdf.format(new Date()));
			RfaEvent event = getRfaEventByeventId(eventId);

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
				String type = "RFA";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName(event.getEventOwner().getBuyer().getCompanyName());
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> subMittedSupplierList = null;
				List<EventSupplier> SupplierList = rfaEventSupplierService.getAllSuppliersByEventId(eventId);
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
					subMittedSupplierList = new ArrayList<EventSupplier>();
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

						if (supplier.getDisqualify() == Boolean.TRUE) {
							suppliers.setReason(supplier.getDisqualifyRemarks());
						}

						allSuppliers.add(suppliers);
						if (Boolean.TRUE == supplier.getSubmitted()) {
							subMittedSupplierList.add(supplier);
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
				List<EvaluationCqPojo> allCqs = getAllSupplierCqforEvaluationSummary(eventId, isMasked);

				// Below functionality to get all BQ Items along with suppliers
				// response of each BQ Items
				List<EvaluationSuppliersBqPojo> supplierBq = getAllSupplierBqforEvaluationSummary(subMittedSupplierList, eventId, isMasked);

				// GRAND TOTAL BQ LIST
				List<Bq> bqs = rfaEventBqDao.findRfaBqbyEventId(eventId);
				if (CollectionUtil.isNotEmpty(bqs)) {
					for (Bq bq : bqs) {
						EvaluationBqPojo supBqs = new EvaluationBqPojo();
						supBqs.setName(bq.getName());
						List<EvaluationSuppliersPojo> suppBqComments = new ArrayList<EvaluationSuppliersPojo>();
						if (CollectionUtil.isNotEmpty(subMittedSupplierList)) {
							for (EventSupplier supplier : subMittedSupplierList) {
								EvaluationSuppliersPojo supList = new EvaluationSuppliersPojo();
								RfaSupplierBq supBq = rfaSupplierBqDao.findBqByBqId(bq.getId(), supplier.getSupplier().getId());
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
								List<RfaBqTotalEvaluationComments> comment = rfaBqTotalEvaluationCommentsService.getComments(supplier.getSupplier().getId(), eventId, bq.getId(), SecurityLibrary.getLoggedInUser());
								if (CollectionUtil.isNotEmpty(comment)) {
									for (RfaBqTotalEvaluationComments leadComments : comment) {
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


				// SOR DETAILS
				List<EvaluationSuppliersSorPojo> supplierSor = getAllSupplierSorforEvaluationSummary(subMittedSupplierList, eventId, isMasked);


				summary.setBqLeadCommentsList(bqGranTotalList);
				summary.setSuppliers(allSuppliers);
				summary.setCqs(allCqs);
				summary.setBqSuppliers(supplierBq);
				summary.setSorSuppliers(supplierSor);

				rfaSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rfaSummary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfaSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private List<SupplierMaskingPojo> buildSupplierMaskingData(List<EventSupplier> supplierList, String eventId) {
		List<SupplierMaskingPojo> supplierMaskingList = new ArrayList<SupplierMaskingPojo>();
		List<RfaEnvelop> env = rfaEnvelopDao.getAllEnvelopByEventId(eventId, RfxTypes.RFA);
		RfaEvent event = getRfaEventByeventId(eventId);
		for (EventSupplier eventSupplier : supplierList) {
			if (eventSupplier.getSupplier() != null) {
				SupplierMaskingPojo pojo = new SupplierMaskingPojo();

				if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
					pojo.setSupplierName(MaskUtils.maskName("SUPPLIER", eventSupplier.getSupplier().getId(), eventId));
				} else {
					pojo.setSupplierName(eventSupplier.getSupplier() != null ? eventSupplier.getSupplier().getCompanyName() : "");
				}
				List<SupplierMaskingCodePojo> supplierMaskingCodes = new ArrayList<SupplierMaskingCodePojo>();
				for (RfaEnvelop rfaEnvelop : env) {
					SupplierMaskingCodePojo codePojo = new SupplierMaskingCodePojo();
					codePojo.setEnevelopeName(rfaEnvelop.getEnvelopTitle());
					codePojo.setMakedCode(MaskUtils.maskName(rfaEnvelop.getPreFix(), eventSupplier.getSupplier().getId(), rfaEnvelop.getId()));
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

	@SuppressWarnings("deprecation")
	@Override
	public JasperPrint getEvaluationSummaryPdf(RfaEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rfaEventDao.findByEventId(event.getId());
		JasperPrint jasperPrint = null;
		List<EvaluationPojo> summary = new ArrayList<EvaluationPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		SimpleDateFormat sdf = gettimeZone(strTimeZone);
		TimeZone timeZone = TimeZone.getDefault();
		// String strTimeZone = (String)
		// session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		timeFormat.setTimeZone(timeZone);

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		try {
			Resource resource = applicationContext.getResource("classpath:reports/GenerateEvaluationSummary.jasper");

			String imgPath = context.getRealPath("resources/images");

			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			getEventSummaryDetails(event, sdf, eventDetails);

			// Meeting Details.
			List<EvaluationMeetingPojo> meetings = summaryMeetingDetails(event, imgPath, sdf);

			// RFA AuctionDetails.
			List<EvaluationAuctionRulePojo> auctionRuleList = auctionRuleDetails(event, sdf);
			// CQ Items
			List<EvaluationCqPojo> allCqData = new ArrayList<EvaluationCqPojo>();
			List<RfaCq> cqList = rfaCqService.findRfaCqForEvent(event.getId());
			if (CollectionUtil.isNotEmpty(cqList)) {
				for (RfaCq item : cqList) {
					EvaluationCqPojo cqs = new EvaluationCqPojo();
					cqs.setName(item.getName());
					cqs.setDescription(item.getDescription());

					List<EvaluationCqItemPojo> cqItemList = new ArrayList<EvaluationCqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getCqItems())) {
						for (RfaCqItem cqItem : item.getCqItems()) {
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
								for (RfaCqOption cqOption : cqItem.getCqOptions()) {
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

			List<Bq> bqs = rfaEventBqDao.findRfaBqbyEventId(event.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
			if (CollectionUtil.isNotEmpty(bqs)) {
				for (Bq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<RfaBqItem> bqItems = rfaBqItemDao.findBqItemsForBq(item.getId());
					List<EvaluationBqItemPojo> evaluationBqItem = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfaBqItem bqItem : bqItems) {
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


			// Fetch all list of SORS
			List<Sor> sors = rfaEventSorDao.findSorbyEventId(event.getId());
			List<EvaluationSorPojo> allSors = new ArrayList<EvaluationSorPojo>();
			if (CollectionUtil.isNotEmpty(sors)) {
				for (Sor item : sors) {
					EvaluationSorPojo bqPojo = new EvaluationSorPojo();
					bqPojo.setName(item.getName());

					List<RfaSorItem> bqItems = rfaSorItemDao.findSorItemsForSor(item.getId());
					List<EvaluationSorItemPojo> evaluationBqItem = new ArrayList<EvaluationSorItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfaSorItem bqItem : bqItems) {
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
			List<RfaEnvelop> envelops = rfaEnvelopService.getAllEnvelopByEventId(event.getId(), loggedInUser);
			List<EvaluationEnvelopPojo> envlopList = new ArrayList<EvaluationEnvelopPojo>();
			if (CollectionUtil.isNotEmpty(envelops)) {
				for (RfaEnvelop envelop : envelops) {
					EvaluationEnvelopPojo env = new EvaluationEnvelopPojo();
					env.setEnvlopName(envelop.getEnvelopTitle());
					env.setDescription(envelop.getDescription());
					env.setType(envelop.getEnvelopType().getValue());
					env.setOpener(envelop.getOpener() != null ? envelop.getOpener().getName() : "");
					env.setOwner(envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "");
					env.setSequence(envelop.getEnvelopSequence());
					List<EvaluationBqPojo> envlopBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getBqList())) {
						for (RfaEventBq item : envelop.getBqList()) {
							EvaluationBqPojo bq = new EvaluationBqPojo();
							bq.setName(item.getName());
							envlopBqs.add(bq);
						}
					}
					List<EvaluationCqPojo> envlopCqs = new ArrayList<EvaluationCqPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getCqList())) {
						for (RfaCq item : envelop.getCqList()) {
							EvaluationCqPojo cq = new EvaluationCqPojo();
							cq.setName(item.getName());
							envlopCqs.add(cq);
						}
					}

					List<EvaluationSorPojo> envlopSors = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(envelop.getSorList())) {
						for (RfaEventSor item : envelop.getSorList()) {
							EvaluationSorPojo bq = new EvaluationSorPojo();
							bq.setName(item.getName());
							envlopSors.add(bq);
						}
					}

					// List of evaluators
					List<EvaluationEnvelopPojo> evaluatorList = new ArrayList<EvaluationEnvelopPojo>();
					List<RfaEvaluatorUser> evaluators = envelop.getEvaluators();
					if (CollectionUtil.isNotEmpty(evaluators)) {
						for (RfaEvaluatorUser usr : evaluators) {
							EvaluationEnvelopPojo el = new EvaluationEnvelopPojo();
							el.setOwner(usr.getUser().getName());
							evaluatorList.add(el);
						}
					}

					// List of openers
					List<EvaluationEnvelopPojo> openersList = new ArrayList<EvaluationEnvelopPojo>();
					List<RfaEnvelopeOpenerUser> openers = envelop.getOpenerUsers();
					if (CollectionUtil.isNotEmpty(openers)) {
						for (RfaEnvelopeOpenerUser usr : openers) {
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

			eventDetails.setEnableApprovalReminder(event.getEnableApprovalReminder());
			if (Boolean.TRUE == event.getEnableApprovalReminder()) {
				eventDetails.setReminderAfterHour(event.getReminderAfterHour());
				eventDetails.setReminderCount(event.getReminderCount());
			}
			eventDetails.setNotifyEventOwner(event.getNotifyEventOwner());
			// Evaluation Conclusion Users

			boolean haveUserConcluded = false;
			if (event.getEnableEvaluationConclusionUsers() && event.getEvaluationConclusionEnvelopeNonEvaluatedCount() != null) {
				eventDetails.setEnvelopeEvaluatedCount(event.getEvaluationConclusionEnvelopeEvaluatedCount());
				eventDetails.setEnvelopeNonEvaluatedCount(event.getEvaluationConclusionEnvelopeNonEvaluatedCount());
				eventDetails.setDisqualifiedSupplierCount(event.getEvaluationConclusionDisqualifiedSupplierCount());
				eventDetails.setRemainingSupplierCount(event.getEvaluationConclusionRemainingSupplierCount());

				List<RfaEvaluationConclusionUser> evaluationConclusionUserList = rfaEventDao.findEvaluationConclusionUsersByEventId(event.getId());
				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					LOG.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RfaEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
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

			List<RfaEventTimeLine> timeline = getRfaEventTimeline(event.getId());
			List<EvaluationTimelinePojo> timelineList = new ArrayList<EvaluationTimelinePojo>();
			if (CollectionUtil.isNotEmpty(timeline)) {
				for (RfaEventTimeLine item : timeline) {
					EvaluationTimelinePojo et = new EvaluationTimelinePojo();
					et.setEventDate(item.getActivityDate() != null ? sdf.format(item.getActivityDate()) : "");
					et.setDescription(item.getDescription());
					et.setType(item.getActivity().name());
					timelineList.add(et);
				}
			}

			// Event Approvals
			RfaEvent rfaEvent = getRfaEventById(event.getId());
			List<RfaEventApproval> approvals = rfaEvent.getApprovals();
			List<EvaluationApprovalsPojo> approvalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (RfaEventApproval item : approvals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(item.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfaApprovalUser usr : item.getApprovalUsers()) {
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
								if (usr.getApproval() != null) {
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
					approve.setApprovalUsers(approvUserList);
					approvalList.add(approve);
				}
			}

			// Suspension Approvals
			List<RfaEventSuspensionApproval> suspensionApprovals = rfaEvent.getSuspensionApprovals();
			List<EvaluationApprovalsPojo> susppensionApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionApprovals)) {
				for (RfaEventSuspensionApproval approval : suspensionApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfaSuspensionApprovalUser usr : approval.getApprovalUsers()) {
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
			List<RfaEventAwardApproval> awardApprovals = rfaEvent.getAwardApprovals();
			List<EvaluationApprovalsPojo> awardApprovalList = new ArrayList<EvaluationApprovalsPojo>();
			if (CollectionUtil.isNotEmpty(awardApprovals)) {
				for (RfaEventAwardApproval approval : awardApprovals) {
					EvaluationApprovalsPojo approve = new EvaluationApprovalsPojo();
					approve.setLevel(approval.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						Integer index = 1;
						boolean statusOrFlag = false;
						boolean statusAndFlag = false;
						for (RfaAwardApprovalUser usr : approval.getApprovalUsers()) {
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
				List<RfaEventDocument> document = rfaDocumentDao.findAllRfaEventdocsbyEventId(event.getId());// event.getDocuments();
				for (RfaEventDocument docs : document) {
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
			List<RfaComment> comments = event.getComment();
			List<EvaluationCommentsPojo> commentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(comments)) {
				for (RfaComment item : comments) {
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
			List<RfaSuspensionComment> suspensionComments = event.getSuspensionComment();
			List<EvaluationCommentsPojo> suspensionCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(suspensionComments)) {
				for (RfaSuspensionComment comment : suspensionComments) {
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
			List<RfaAwardComment> awardComments = event.getAwardComment();
			List<EvaluationCommentsPojo> awardCommentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(awardComments)) {
				for (RfaAwardComment comment : awardComments) {
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
			List<RfaEventAudit> eventAudit = rfaEventAuditDao.getRfaEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RfaEventAudit ra : eventAudit) {
					EvaluationAuditPojo audit = new EvaluationAuditPojo();
					audit.setAuctionDate(ra.getActionDate() != null ? sdf.format(ra.getActionDate()) : "");
					audit.setAuctionBy(ra.getActionBy() != null ? ra.getActionBy().getName() : "");
					audit.setAuction(ra.getAction().getValue());
					audit.setDescription(ra.getDescription());
					auditList.add(audit);
				}
			}

			String participationFeeCurrency = event.getParticipationFeeCurrency() != null ? event.getParticipationFeeCurrency().getCurrencyCode() : "";
			eventDetails.setParticipationFeeAndCurrency(participationFeeCurrency + " " + (event.getParticipationFees() != null ? formatedDecimalNumber("2", event.getParticipationFees()) : "-"));
			String depositCurrency = event.getDepositCurrency() != null ? event.getDepositCurrency().getCurrencyCode() : "";
			eventDetails.setDepositAndCurrency(depositCurrency + " " + (event.getDeposit() != null ? formatedDecimalNumber("2", event.getDeposit()) : "-"));
			eventDetails.setInternalRemarks(event.getInternalRemarks() != null ? event.getInternalRemarks() : " - ");
			eventDetails.setComments(commentDetails);
			eventDetails.setMeetings(meetings);
			eventDetails.setAuctionRules(auctionRuleList);
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
	 * @param sdf
	 * @param eventDetails
	 */
	private void getEventSummaryDetails(RfaEvent event, SimpleDateFormat sdf, EvaluationPojo eventDetails) {
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
		eventDetails.setType("RFA");
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
		eventDetails.setViewAuctionHall(event.getViewAuctionHall());
		eventDetails.setAllowEvaluationForDisqualifySupplier(event.getAllowDisqualifiedSupplierDownload());
		User revertLastBidUser = rfaEventDao.getRevertLastBidUserNameAndMailByEventId(event.getId());
		if (event.getRevertLastBid() && revertLastBidUser != null) {
			String revertBidUser = revertLastBidUser.getName() + "\r\n" + revertLastBidUser.getLoginId() + "\r\n" + StringUtils.checkString(revertLastBidUser.getPhoneNumber());
			eventDetails.setRevertBidUser(revertBidUser);
		} else {
			eventDetails.setRevertBidUser("N/A");
		}

		if (!event.getViewSupplerName() && CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				String unMaskedOwner = "";
				for (RfaUnMaskedUser rfaUnmaskedUser : event.getUnMaskedUsers()) {
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
			for (RfaEvaluationConclusionUser rfaEvaluationUsers : event.getEvaluationConclusionUsers()) {
				if (rfaEvaluationUsers.getUser() != null) {
					evaluationUsers += rfaEvaluationUsers.getUser().getName() + "\r\n" + rfaEvaluationUsers.getUser().getLoginId() + "," + "\r\n";
				}
			}
			if (StringUtils.checkString(evaluationUsers).length() > 0) {
				evaluationUsers = StringUtils.checkString(evaluationUsers).substring(0, StringUtils.checkString(evaluationUsers).length() - 1);
				eventDetails.setEvalConclusionOwners(evaluationUsers);
			}
		}

		List<EvaluationPojo> eventStartRemider = new ArrayList<EvaluationPojo>();
		if (CollectionUtil.isNotEmpty(event.getRfaEndReminder())) {
			for (RfaReminder item : event.getRfaEndReminder()) {
				EvaluationPojo startRemider = new EvaluationPojo();
				startRemider.setEventStart(item.getReminderDate() != null ? sdf.format(item.getReminderDate()) : "");
				eventStartRemider.add(startRemider);
			}
		}
		eventDetails.setReminderDate(eventStartRemider);

		List<EvaluationPojo> eventRemider = new ArrayList<EvaluationPojo>();
		if (CollectionUtil.isNotEmpty(event.getRfaEndReminder())) {
			for (RfaReminder item : event.getRfaEndReminder()) {
				EvaluationPojo remider = new EvaluationPojo();
				remider.setEventEnd(item.getReminderDate() != null ? sdf.format(item.getReminderDate()) : "");
				eventRemider.add(remider);
			}
		}
		eventDetails.setReminderDate(eventRemider);

		eventDetails.setVisibility(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");

		eventDetails.setPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
		eventDetails.setValidityDays(event.getSubmissionValidityDays());
		eventDetails.setParticipationFee(event.getParticipationFees()); // Participation
																		// Fee
																		// only
																		// for
																		// RFT

		// setting Temp first category only
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

		// Time Extension
		String extensionDuration = "", autoDisqualify = "", extensionTrigger = "";
		eventDetails.setAuctionType(event.getAuctionType() != null ? event.getAuctionType().getValue() : "");
		eventDetails.setExtentionType(event.getTimeExtensionType() != null ? event.getTimeExtensionType().name() : "");
		if (event.getTimeExtensionType() != null && event.getTimeExtensionType() == TimeExtensionType.AUTOMATIC) {
			LOG.info(event.getId() + "-------Time Extension Duration type ------------" + event.getTimeExtensionDurationType());
			extensionDuration = event.getTimeExtensionDuration() + "-" + event.getTimeExtensionDurationType().name();
			if (event.getAutoDisqualify() == Boolean.TRUE) {
				autoDisqualify = "Yes";
			} else {
				autoDisqualify = "No";
			}
			extensionTrigger = event.getTimeExtensionLeadingBidValue() + "-" + event.getTimeExtensionLeadingBidType().name();
			eventDetails.setExtensionDuration(extensionDuration);
			eventDetails.setExtensionRound(event.getExtensionCount());
			eventDetails.setExtensionTrigger(extensionTrigger);
			eventDetails.setAutoDisqualify(autoDisqualify);
			eventDetails.setDisqualifyCount(event.getBidderDisqualify());

		}
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
		List<RfaEventContact> eventContacts = getAllContactForEvent(event.getId());
		List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
		if (CollectionUtil.isNotEmpty(eventContacts)) {
			for (RfaEventContact contact : eventContacts) {
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
		eventDetails.setBudgetAmt(event.getBudgetAmount());
		eventDetails.setDecimal(event.getDecimal());
		eventDetails.setDescription(event.getEventDescription());
		eventDetails.setEstimatedBudget(event.getEstimatedBudget());

		SimpleDateFormat ddsdf = new SimpleDateFormat("dd/MM/yyyy");
		ddsdf.setTimeZone(sdf.getTimeZone());
		eventDetails.setDiliveryDate(event.getDeliveryDate() != null ? ddsdf.format(event.getDeliveryDate()) : "N/A");

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
	}

	private List<EvaluationAuctionRulePojo> auctionRuleDetails(RfaEvent event, SimpleDateFormat sdf) {
		List<EvaluationAuctionRulePojo> auctionRule = new ArrayList<EvaluationAuctionRulePojo>();
		String supplierValue = "", biddingType = "", ownPrevious = "", leadBid = "", startGate = "", bidMinValue = "", heigherLeadBid = "", prebid = "";

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

		try {
			AuctionRules rules = auctionRulesDao.findAuctionRulesByEventId(event.getId());
			if (rules != null) {
				EvaluationAuctionRulePojo ar = new EvaluationAuctionRulePojo();
				ar.setEventName(rules.getEvent().getEventName());
				ar.setAuctionType(rules.getAuctionType().getValue());
				ar.setDecimal(event.getDecimal());
				if (rules.getAuctionType() == AuctionType.FORWARD_DUTCH || rules.getAuctionType() == AuctionType.REVERSE_DUTCH) {
					ar.setDutchStartPrice(rules.getDutchStartPrice());
					ar.setFowardAuction(rules.getFowardAuction());
					ar.setMinPrice(rules.getDutchMinimumPrice());
					ar.setAmountPerIncrementDecrement(rules.getAmountPerIncrementDecrement());
					ar.setInterval(rules.getInterval());
					ar.setIntervalType(rules.getIntervalType().name());
				}
				if (rules.getAuctionType() != null && (rules.getAuctionType() == AuctionType.FORWARD_ENGISH || rules.getAuctionType() == AuctionType.REVERSE_ENGISH || rules.getAuctionType() == AuctionType.FORWARD_SEALED_BID || rules.getAuctionType() == AuctionType.REVERSE_SEALED_BID)) {
					ar.setPrebidByTitle("Pre-bid By");
					ar.setSupplierAuctionSetting("Supplier Auction Console Settings :");
					ar.setPreBidBy(rules.getPreBidBy() != null ? rules.getPreBidBy().name() : "");
					ar.setFowardAuction(rules.getFowardAuction());

					if (rules.getPreBidBy() != null && rules.getPreBidBy() == PreBidByType.BUYER) {
						if (rules.getFowardAuction() != null && rules.getIsPreBidHigherPrice() != null) {
							if (rules.getFowardAuction() == Boolean.TRUE) {
								supplierValue = "Supplier must provide Higher price or same price";
							} else if (rules.getFowardAuction() == Boolean.FALSE) {
								supplierValue = "Supplier must provide Lower price or same price";
							}
						}
					}

					ar.setSupplierMustProvide(supplierValue);
					ar.setPreBidSameBidPriceValue("Allow supplier to have same pre bid price");
					ar.setPreSetSamePreBidForAllSuppValue("Pre-set the same pre-bid price for all suppliers");
					ar.setIsPreSetSamePreBidForAllSupp(rules.getPreSetSamePreBidForAllSuppliers());

					if (rules.getItemizedBiddingWithTax() != null) {
						if (rules.getItemizedBiddingWithTax() == Boolean.TRUE) {
							biddingType = "Itemized Bidding with tax";
						} else {
							biddingType = "Itemized Bidding without tax";
						}
					}
					if (rules.getLumsumBiddingWithTax() != null) {
						if (rules.getLumsumBiddingWithTax() == Boolean.TRUE) {
							biddingType = "Lumpsum Bidding with tax";
						} else {
							biddingType = "Lumpsum Bidding without tax";
						}
					}
					if (rules.getIsBiddingMinValueFromPrevious()) {
						if (rules.getFowardAuction()) {
							ownPrevious = "Minimum increment from own previous";
						} else {
							ownPrevious = "Minimum decrement from own previous";
						}
					}
					if (rules.getIsBiddingPriceHigherLeadingBid()) {
						if (rules.getFowardAuction()) {
							leadBid = "Price must be higher than leading bid";
						} else {
							leadBid = "Price must be lower than leading bid";
						}
					}
					ar.setBiddingType(biddingType);
					ar.setOwnPrevious(ownPrevious);
					ar.setLeadBid(leadBid);
					ar.setIsPreBidSameBidPrice(rules.getIsPreBidSameBidPrice());
					ar.setIsPreBidHigherPrice(rules.getIsPreBidHigherPrice());
					ar.setItemizedBiddingWithTax(rules.getItemizedBiddingWithTax());
					ar.setLumsumBiddingWithTax(rules.getLumsumBiddingWithTax());
					ar.setIsBiddingMinValueFromPrevious(rules.getIsBiddingMinValueFromPrevious());
					ar.setBiddingMinValueType(rules.getBiddingMinValueType() != null ? rules.getBiddingMinValueType().name() : "");
					if (rules.getBiddingMinValueType() == ValueType.PERCENTAGE) {
						bidMinValue = df.format(rules.getBiddingMinValue() != null ? rules.getBiddingMinValue() : new BigDecimal(0)) + " %";
						heigherLeadBid = df.format(rules.getBiddingPriceHigherLeadingBidValue() != null ? rules.getBiddingPriceHigherLeadingBidValue() : new BigDecimal(0)) + " %";
					} else {
						bidMinValue = df.format(rules.getBiddingMinValue() != null ? rules.getBiddingMinValue() : new BigDecimal(0));
						heigherLeadBid = df.format(rules.getBiddingPriceHigherLeadingBidValue() != null ? rules.getBiddingPriceHigherLeadingBidValue() : new BigDecimal(0));
					}
					ar.setBiddingMinValue(bidMinValue);

					if (rules.getIsStartGate() != null) {
						if (rules.getIsStartGate() == Boolean.TRUE) {
							startGate = "YES";
						} else {
							startGate = "NO";
						}
						ar.setIsStartGate(startGate);
					}
					ar.setIsBiddingPriceHigherLeadingBid(rules.getIsBiddingPriceHigherLeadingBid());
					ar.setBiddingPriceHigherLeadingBidType(rules.getBiddingPriceHigherLeadingBidType() != null ? rules.getBiddingPriceHigherLeadingBidType().name() : "");
					ar.setBiddingPriceHigherLeadingBidValue(heigherLeadBid);
					ar.setIsBiddingAllowSupplierSameBid(rules.getIsBiddingAllowSupplierSameBid());
					ar.setAuctionConsolePriceType(rules.getAuctionConsolePriceType() != null ? rules.getAuctionConsolePriceType().getValue() : "");
					ar.setAuctionConsoleVenderType(rules.getAuctionConsolePriceType() != null ? rules.getAuctionConsoleVenderType().getValue() : "");
					ar.setAuctionConsoleRankType(rules.getAuctionConsoleRankType().getValue());

					ar.setBuyerAuctionConsolePriceType(rules.getBuyerAuctionConsolePriceType() != null ? rules.getBuyerAuctionConsolePriceType().getValue() : "");
					ar.setBuyerAuctionConsoleVenderType(rules.getBuyerAuctionConsoleVenderType() != null ? rules.getBuyerAuctionConsoleVenderType().getValue() : "");
					ar.setBuyerAuctionConsoleRankType(rules.getBuyerAuctionConsoleRankType().getValue());
					if (rules.getPrebidAsFirstBid() != null) {
						if (rules.getPrebidAsFirstBid() == Boolean.TRUE) {
							prebid = "YES";
						} else {
							prebid = "NO";
						}
						ar.setPrebidAsFirstBid(prebid);
					}

				}
				auctionRule.add(ar);
			}
		} catch (Exception e) {
			LOG.error("Could not Auction Rules Values " + e.getMessage(), e);
		}
		return auctionRule;
	}

	/**
	 * @param event
	 * @param imgPath
	 * @param sdf
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private List<EvaluationMeetingPojo> summaryMeetingDetails(RfaEvent event, String imgPath, SimpleDateFormat sdf) {
		List<RfaEventMeeting> meetingList = event.getMeetings();
		List<EvaluationMeetingPojo> meetings = new ArrayList<EvaluationMeetingPojo>();
		if (CollectionUtil.isNotEmpty(meetingList)) {
			for (RfaEventMeeting meeting : meetingList) {
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
					for (RfaEventMeetingContact mc : meeting.getRfxEventMeetingContacts()) {
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
					for (RfaEventMeetingDocument docs : meeting.getRfxEventMeetingDocument()) {
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

					List<Bq> bqs = rfaEventBqDao.findRfaBqbyEventId(eventId);

					List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Bq bq : bqs) {
							EvaluationBqPojo bqItem = new EvaluationBqPojo();
							bqItem.setName(bq.getName());
							List<RfaSupplierBqItem> suppBqItems = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());
							for (RfaSupplierBqItem rfaSupplierBqItem : suppBqItems) {
								BigDecimal grandTotal = rfaSupplierBqItem.getSupplierBq().getGrandTotal();
								bqSupplierPojo.setGrandTotal(grandTotal);
								LOG.info("GRANDTOTAL " + rfaSupplierBqItem.getSupplierBq().getGrandTotal());
							}

							List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RfaSupplierBqItem suppBqItem : suppBqItems) {

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
										for (RfaSupplierBqItem childBqItem : suppBqItem.getChildren()) {
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
											List<RfaBqEvaluationComments> bqItemComments = rfaBqEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getBqItem().getId(), null);
											if (CollectionUtil.isNotEmpty(bqItemComments)) {
												String reviews = "";
												for (RfaBqEvaluationComments review : bqItemComments) {
													EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
													bqComment.setCommentBy(review.getUserName());
													bqComment.setComments(review.getComment());
													comments.add(bqComment);
													reviews += "[ " + review.getUserName() + " ] " + review.getComment() + "\n";
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

					List<Sor> bqs = rfaEventSorDao.findSorbyEventId(eventId);

					List<EvaluationSorPojo> allBqs = new ArrayList<EvaluationSorPojo>();
					if (CollectionUtil.isNotEmpty(bqs)) {
						for (Sor bq : bqs) {
							EvaluationSorPojo bqItem = new EvaluationSorPojo();
							bqItem.setName(bq.getName());
							List<RfaSupplierSorItem> suppBqItems = rfaSupplierSorItemService.getAllSupplierSorItemBySorIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());

							List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
							if (CollectionUtil.isNotEmpty(suppBqItems)) {
								for (RfaSupplierSorItem suppBqItem : suppBqItems) {

									EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
									evlBqItem.setDescription(suppBqItem.getItemName());
									evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
									evlBqItem.setAmount(null);
									evlBqItem.setDecimal(suppBqItem.getEvent() != null ? suppBqItem.getEvent().getDecimal() : "");
									evlBqItems.add(evlBqItem);
									if (suppBqItem.getChildren() != null) {
										for (RfaSupplierSorItem childBqItem : suppBqItem.getChildren()) {
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

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, boolean isMAsked) {

		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		List<RfaCq> cqList = rfaCqService.findRfaCqForEvent(eventId);
		for (RfaCq cq : cqList) {
			EvaluationCqPojo cqPojo = new EvaluationCqPojo();
			cqPojo.setName(cq.getName());
			cqPojo.setDescription(cq.getDescription());

			List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
			if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
				for (RfaCqItem cqItem : cq.getCqItems()) {

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
					List<Supplier> suppList = rfaEventSupplierDao.getEventSuppliersForSummary(eventId);
					for (Supplier supp : suppList) {
						if (isMAsked) {
							supp.setCompanyName(MaskUtils.maskName("SUPPLIER", supp.getId(), eventId));
						}
					}
					// Below code to get Suppliers Answers of each CQ Items

					if (CollectionUtil.isNotEmpty(suppList)) {
						// List<RfaSupplierCqItem> responseList =
						// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppList);
						List<RfaSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
						if (CollectionUtil.isNotEmpty(responseList)) {
							for (RfaSupplierCqItem suppCqItem : responseList) {
								EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
								List<RfaSupplierCqOption> listAnswers = rfaSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
								if (isMAsked) {
									cqItemSupplierPojo.setSupplierName(MaskUtils.maskName("SUPPLIER", suppCqItem.getSupplier().getId(), eventId));
								} else {
									cqItemSupplierPojo.setSupplierName(suppCqItem.getSupplier().getCompanyName());
								}
								if (suppCqItem.getCqItem().getCqType() == CqType.TEXT || suppCqItem.getCqItem().getCqType() == CqType.DATE || suppCqItem.getCqItem().getCqType() == CqType.NUMBER || suppCqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
									cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
								} else if (CqType.DOCUMENT_DOWNLOAD_LINK == suppCqItem.getCqItem().getCqType()) {
									List<String> docIds = new ArrayList<String>();
									// List<RfaCqOption> rfaSupplierCqOptions = suppCqItem.getCqItem().getCqOptions();
									List<RfaCqOption> rfaSupplierCqOptions = rfaCqOptionDao.findOptionsByCqItem(suppCqItem.getCqItem().getId());// suppCqItem.getCqItem().getCqOptions();
									if (CollectionUtil.isNotEmpty(rfaSupplierCqOptions)) {
										for (RfaCqOption rfaSupplierCqOption : rfaSupplierCqOptions) {
											docIds.add(StringUtils.checkString(rfaSupplierCqOption.getValue()));
										}
									}
									List<EventDocument> eventDocuments = rfaDocumentService.findAllRfaEventDocsNamesByEventIdAndDocIds(docIds);
									if (eventDocuments != null) {
										String str = "";
										for (EventDocument docName : eventDocuments) {
											str = str + docName.getFileName() + "  ";
										}
										cqItemSupplierPojo.setAnswer(str);
									}
								} else if (CollectionUtil.isNotEmpty(listAnswers) && (suppCqItem.getCqItem().getCqType() == CqType.LIST || suppCqItem.getCqItem().getCqType() == CqType.CHECKBOX)) {
									String str = "";
									// List<RfaSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfaSupplierCqOption op : listAnswers) {
										str += op.getValue() + "\n";
									}
									cqItemSupplierPojo.setAnswer(str);
								} else if (CollectionUtil.isNotEmpty(listAnswers) && suppCqItem.getCqItem().getCqType() != CqType.DOCUMENT_DOWNLOAD_LINK) {
									String str = "";
									// List<RfaSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
									for (RfaSupplierCqOption op : listAnswers) {
										int cqAnsListSize = (listAnswers).size();
										int index = (listAnswers).indexOf(op);
										str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));
									}
									cqItemSupplierPojo.setAnswer(str);
								}
								cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
								// Review Comments
								List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
								List<RfaCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
								if (CollectionUtil.isNotEmpty(comments)) {
									String evalComment = "";
									for (RfaCqEvaluationComments item : comments) {
										EvaluationCqItemSupplierCommentsPojo supCmnts = new EvaluationCqItemSupplierCommentsPojo();
										supCmnts.setComment(item.getComment());
										supCmnts.setCommentBy(item.getUserName());
										evalComments.add(supCmnts);
										evalComment += "[ " + item.getUserName() + " ] " + item.getComment() + "\n";
									}
									cqItemSupplierPojo.setEvalComment(evalComment);

									cqItemSupplierPojo.setComments(evalComments);
								}
								LOG.info("Comment>>>>>>>>>>>>" + cqItemSupplierPojo.getEvalComment());
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
	@Transactional(readOnly = false)
	public AuctionBids saveAuctionBids(AuctionBids auctionBids) {
		return auctionBidsDao.saveOrUpdate(auctionBids);
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
			RfaEvent event = getRfaEventByeventId(eventId);
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

				RfaEventMeeting meetingItem = rfaEventMeetingDao.findById(meetingId);
				EvaluationMeetingPojo meeting = new EvaluationMeetingPojo();
				meeting.setTitle(meetingItem.getTitle());
				if (meetingItem.getAppointmentDateTime() != null) {
					meeting.setAppointmentDateTime(new Date(sdf.format(meetingItem.getAppointmentDateTime())));
				}
				meeting.setRemarks(meetingItem.getRemarks());
				meeting.setVenue(meetingItem.getVenue());

				List<EvaluationMeetingContactsPojo> contacts = new ArrayList<EvaluationMeetingContactsPojo>();
				if (CollectionUtil.isNotEmpty(meetingItem.getRfxEventMeetingContacts())) {
					for (RfaEventMeetingContact mc : meetingItem.getRfxEventMeetingContacts()) {
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
						RfaSupplierMeetingAttendance attendance = rfaSupplierMeetingAttendanceDao.getAttendanceForMeetingForSupplier(meetingId, sup.getId());

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

	/**
	 * @param strTimeZone
	 * @return
	 */
	private SimpleDateFormat gettimeZone(String strTimeZone) {
		TimeZone timeZone = TimeZone.getDefault();
		// String strTimeZone = (String)
		// session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		return sdf;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent concludeRfaEvent(RfaEvent event, User loggedInUser) {
		// Clear Award details if present.
		int deleteCount = rfaEventAwardDao.removeAwardDetails(event.getId());

		RfaEvent dbevent = getRfaEventByeventId(event.getId());

		if (deleteCount > 0) {
			try {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setActionDate(new Date());
				audit.setActionBy(loggedInUser);
				audit.setDescription(messageSource.getMessage("event.award.discarded", new Object[] { dbevent.getEventName() }, Global.LOCALE));
				audit.setEvent(dbevent);
				audit.setAction(AuditActionType.Discard);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISCARD, messageSource.getMessage("event.award.discarded", new Object[] { dbevent.getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
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
		return rfaEventDao.update(dbevent);
	}

	@Override
	public JasperPrint getBuyerAuctionReport(String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		JasperPrint jasperPrint = null;
		List<EvaluationAuctionPojo> auctionSummary = new ArrayList<EvaluationAuctionPojo>();
		Map<String, Object> parameters = new HashMap<String, Object>();

		TimeZone timeZone = TimeZone.getDefault();
		// String strTimeZone = (String)
		// session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
		sdf.setTimeZone(timeZone);

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		try {
			Resource resource = applicationContext.getResource("classpath:reports/BuyerAuctionReport.jasper");
			// String imgPath = context.getRealPath("resources/images");
			File jasperfile = resource.getFile();
			RfaEvent event = rfaEventDao.findByEventId(eventId);
			List<EventSupplier> supplierList = rfaEventSupplierDao.getAllSuppliersByEventId(eventId);
			int supplierCount = 0, submittedCnt = 0, totalBids = 0;

			List<EventSupplier> participatedSupplier = new ArrayList<EventSupplier>();
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
			if (event != null) {
				EvaluationAuctionPojo auction = new EvaluationAuctionPojo();
				LOG.info("eventId: " + event.getId() + "=== Event Start: " + event.getEventStart() + "===Event End: " + event.getEventEnd());
				String auctionDate = event.getEventStart() != null ? sdf.format(event.getEventStart()) : "" + "-" + (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
				String auctionTitle = "PRE & POST AUCTION PRICE (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : " ") + ")";
				String netSavingTitle = "Saving based on Budged(%)";
				auction.setAuctionId(event.getEventId());
				auction.setReferenceNo(event.getReferanceNumber());
				auction.setAuctionName(event.getEventName());
				auction.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				auction.setAuctionType(event.getAuctionType().getValue());
				auction.setDateTime(auctionDate);
				auction.setAuctionTitle(auctionTitle);
				auction.setNetSavingTitle(netSavingTitle);
				// Bid Details.
				auction.setBuyerName(event.getEventOwner().getBuyer().getCompanyName());
				auction.setAuctionPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
				auction.setAuctionStartDate(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
				auction.setAuctionEndDate(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
				auction.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
				auction.setAuctionExtension(event.getTimeExtensionType() != null ? event.getTimeExtensionType().name() : "");
				auction.setTotalExtension(event.getTotalExtensions());
				auction.setSupplierInvited(supplierCount);
				auction.setSupplierParticipated(submittedCnt);
				auction.setAuctionStatus(event.getStatus().name());
				auction.setTotalBilds(totalBids);
				auction.setIsBuyer(Boolean.TRUE);
				auction.setDecimal(event.getDecimal());
				if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {
					AuctionRules auctionRules = auctionRulesDao.findAuctionRulesByEventId(event.getId());

					auction.setAuctionCompletionDate(event.getAuctionComplitationTime() != null ? sdf.format(event.getAuctionComplitationTime()) : "");
					if (auctionRules != null) {
						auction.setStartPrice(auctionRules.getDutchStartPrice());
						auction.setDuctchPrice(auctionRules.getDutchMinimumPrice());
						auction.setIntervalType(auctionRules.getIntervalType().name());
						auction.setInterval(auctionRules.getInterval());

					}
					auction.setWinner(event.getWinningSupplier() != null ? event.getWinningSupplier().getCompanyName() : "");
					auction.setWinningPrice(event.getWinningPrice());
					auction.setWinningDate(event.getAuctionComplitationTime() != null ? sdf.format(event.getAuctionComplitationTime()) : "");
				}

				// Bid History
				List<EvaluationBidHistoryPojo> bidHistoryDetails = new ArrayList<EvaluationBidHistoryPojo>();
				List<AuctionBids> bidHistory = auctionBidsDao.getAuctionBidsListByEventIdForReport(event.getId());
				if (CollectionUtil.isNotEmpty(bidHistory)) {
					for (AuctionBids item : bidHistory) {
						EvaluationBidHistoryPojo bd = new EvaluationBidHistoryPojo();
						bd.setBidPrice(item.getAmount());
						bd.setCompanyName(item.getBidBySupplier().getCompanyName());
						bd.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
						bd.setDateTime(item.getBidSubmissionDate() != null ? sdf.format(item.getBidSubmissionDate()) : "");
						bd.setDecimal(event.getDecimal());
						bidHistoryDetails.add(bd);

					}
				}
				auction.setBidHistory(bidHistoryDetails);

				// Bidder Contacts
				List<EvaluationSupplierBidsPojo> supplierBidHistory = new ArrayList<EvaluationSupplierBidsPojo>();
				List<EvaluationBidderContactPojo> bidderContact = new ArrayList<EvaluationBidderContactPojo>();
				if (CollectionUtil.isNotEmpty(participatedSupplier)) {
					for (EventSupplier suppItem : participatedSupplier) {
						EvaluationBidderContactPojo supplierContact = new EvaluationBidderContactPojo();
						supplierContact.setCompanyName(suppItem.getSupplierCompanyName());
						if (suppItem.getSupplier() != null) {
							supplierContact.setContactName(suppItem.getSupplier().getFullName());
							supplierContact.setPhno(suppItem.getSupplier().getCompanyContactNumber());
							supplierContact.setMobileNo(suppItem.getSupplier().getMobileNumber());
							supplierContact.setEmail(suppItem.getSupplier().getCommunicationEmail());

							// Add IP Address
							RfaEventSupplier rfaSupplier = rfaEventSupplierDao.getEventSupplierBySupplierAndEventId(suppItem.getSupplier().getId(), eventId);
							if (rfaSupplier != null) {
								supplierContact.setIpnumber(rfaSupplier.getIpAddress());
							}
						}
						bidderContact.add(supplierContact);

						// Each Supplier Bidding Details
						bidderPriceHistory(eventId, event, supplierBidHistory, suppItem, sdf);
					}
				}
				auction.setSupplierBidsList(supplierBidHistory);
				auction.setBidContacts(bidderContact);
				// Bidding Price Details
				List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
				try {
					List<String> rfaEventSupplierIds = rfaEventSupplierService.getAllRfaEventSuppliersIdByEventId(eventId);
					Integer limitSupplier = null;
					if (CollectionUtil.isNotEmpty(rfaEventSupplierIds)) {
						List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaSupplierBqBySupplierIdsOdrByRank(eventId, limitSupplier);
						if (CollectionUtil.isNotEmpty(eventBq)) {
							for (RfaSupplierBqPojo item : eventBq) {
								EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
								BigDecimal saving = BigDecimal.ZERO, percentage = BigDecimal.ZERO;

								if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
									saving = item.getInitialPrice().subtract(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
									percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
									suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
								}

								if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
									saving = (item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice()).subtract(item.getInitialPrice());
									percentage = (saving.multiply(new BigDecimal(100)).divide(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
									suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
								}

								suppBidprice.setBidderName(item.getSupplierCompanyName());
								suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
								suppBidprice.setDecimal(event.getDecimal());
								suppBidprice.setPreAuctionPrice(item.getInitialPrice());
								if (item.getInitialPrice() != null) {
									suppBidprice.setPreAuctionStrPrice(coolFormat(item.getInitialPrice().doubleValue(), 0));
								}

								if (item.getRevisedGrandTotal() != null) {
									suppBidprice.setPostAuctionStrPrice(coolFormat(item.getRevisedGrandTotal().doubleValue(), 0));
								}

								suppBidprice.setSaving(saving);
								LOG.info("Parcent : " + percentage);
								suppBidprice.setPercentage(percentage);
								suppBidprice.setAuctionType(event.getAuctionType().getValue());
								bidPriceList.add(suppBidprice);
							}
						}
					}
				} catch (Exception e) {
					LOG.error("Could not get Supplier Bidding Price Details" + e.getMessage(), e);
				}
				auction.setBiddingPrice(bidPriceList);
				auctionSummary.add(auction);
			}
			int i = 1;
			for (EvaluationAuctionPojo auction : auctionSummary) {
				if (i == 1) {
					parameters.put("AUCTION", auction.getBidContacts());
				}

			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auctionSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Buyer Auction PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	/**
	 * @param eventId
	 * @param event
	 * @param supplierBidHistory
	 * @param suppItem
	 */
	private void bidderPriceHistory(String eventId, RfaEvent event, List<EvaluationSupplierBidsPojo> supplierBidHistory, EventSupplier suppItem, SimpleDateFormat sdf) {
		try {
			List<RfaSupplierBq> bqList = rfaSupplierBqDao.findSupplierBqbyEventId(eventId);
			String bqId = null;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RfaSupplierBq rfaSupplierEventBq : bqList) {
					bqId = rfaSupplierEventBq.getBq().getId();
				}
			}
			RfaSupplierBq supBq = rfaSupplierBqDao.findBqByBqId(bqId, suppItem.getSupplier().getId());
			if (supBq != null) {
				EvaluationSupplierBidsPojo bidderPriceHistory = new EvaluationSupplierBidsPojo();
				bidderPriceHistory.setSupplierName(supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : "");
				bidderPriceHistory.setBqDescription(supBq.getName());
				bidderPriceHistory.setInitialPrice(supBq.getInitialPrice());
				bidderPriceHistory.setDecimals(event.getDecimal());
				bidderPriceHistory.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
				List<EvaluationBiddingPricePojo> BidderPriceList = new ArrayList<EvaluationBiddingPricePojo>();
				List<AuctionBids> supplierBidsList = auctionBidsDao.getAuctionBidsForSupplierForReport(suppItem.getSupplier().getId(), event.getId());
				if (CollectionUtil.isNotEmpty(supplierBidsList)) {
					BigDecimal reductionPrice = BigDecimal.ZERO, temp = BigDecimal.ZERO, percentage = BigDecimal.ZERO;
					temp = supBq.getInitialPrice();
					int bidNumber = 1;
					for (AuctionBids suppBids : supplierBidsList) {
						LOG.info("toLogString " + suppBids.toLogString());
						reductionPrice = BigDecimal.ZERO;
						percentage = BigDecimal.ZERO;
						EvaluationBiddingPricePojo bidderPrice = new EvaluationBiddingPricePojo();

						// if (temp == BigDecimal.ZERO) {
						// reductionPrice = temp;
						// // percentage = reductionPrice.multiply(new
						// // BigDecimal(100)).divide(temp,
						// // BigDecimal.ROUND_FLOOR);
						// percentage = BigDecimal.ZERO;
						// } else {
						if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
							reductionPrice = temp.subtract(suppBids.getAmount());
						}
						if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
							reductionPrice = suppBids.getAmount().subtract(temp);
						}

						percentage = reductionPrice.multiply(new BigDecimal(100)).divide(temp, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
						// }
						// temp = suppBids.getAmount();
						bidderPrice.setBidNumber(bidNumber);
						bidderPrice.setDecimal(event.getDecimal());
						bidderPrice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");

						bidderPrice.setDisplayValue(coolFormat(suppBids.getAmount().doubleValue(), 0));
						bidderPrice.setPriceSubmission(suppBids.getAmount());
						bidderPrice.setPriceReduction(reductionPrice);
						bidderPrice.setPercentage(percentage);
						bidderPrice.setSubmitedDate(suppBids.getBidSubmissionDate() != null ? sdf.format(suppBids.getBidSubmissionDate()) : "");
						bidderPrice.setBidderName(supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : "");
						BidderPriceList.add(bidderPrice);
						bidNumber += 1;
					}
				}
				bidderPriceHistory.setPriceSubmissionList(BidderPriceList);
				supplierBidHistory.add(bidderPriceHistory);
			}
		} catch (Exception e) {
			LOG.error("Could not get Bidder Price submission History Details " + e.getMessage(), e);
		}

	}

	@Override
	@Transactional(readOnly = false)
	public boolean automaticTimeExtension(RfaEvent event, TimeZone timeZone, HttpSession session) {
		boolean returnValue = false;
		try {
			LOG.info("TIme extension called" + timeZone);
			Calendar cal = Calendar.getInstance(timeZone);
			cal.setTime(event.getEventEnd());
			Date eventEnd = event.getEventEnd();
			int timeExtBefore = event.getTimeExtensionLeadingBidValue();
			if (event.getTimeExtensionLeadingBidType() == DurationType.HOUR) {
				cal.add(Calendar.HOUR, -timeExtBefore);
				LOG.info("time extension HOUR before : " + cal.getTime());
			} else {
				cal.add(Calendar.MINUTE, -timeExtBefore);
				LOG.info("time extension MINUTE before : " + cal.getTime());
			}
			if (new Date().after(cal.getTime())) {
				// cal.getTime is after new date
				LOG.info("condition for Time extension true : " + cal.getTime());
				LOG.info("Total Extension Count : " + event.getTotalExtensions() + " < The Extension Count : " + event.getExtensionCount());
				if (event.getTotalExtensions() < event.getExtensionCount()) {
					cal.setTime(event.getEventEnd());

					if (event.getTimeExtensionDurationType() == DurationType.HOUR) {

						cal.add(Calendar.HOUR, event.getTimeExtensionDuration());
						LOG.info("new event end date after time extension with HOUR extension : " + cal.getTime());
					} else {
						cal.add(Calendar.MINUTE, event.getTimeExtensionDuration());
						LOG.info("new event end date after time extension with Minute extension : " + cal.getTime());
					}
					User rfaEventOwner = rfaEventDao.getPlainEventOwnerByEventId(event.getId());
					RfaEvent rfaEvent = null;
					if (event.getAutoDisqualify()) {
						LOG.info("Disqulify Supplier");
						if (event.getBidderDisqualify() > 0) {
							LOG.info("Disqulify Supplier bidder to dis : " + event.getBidderDisqualify());
							List<RfaSupplierBqPojo> supplirBqPojoList = rfaSupplierBqDao.getSupplierListForAuctionConsole(event.getId(), null);
							LOG.info("Disqulify Supplier bidder pojo list size : " + supplirBqPojoList.size());
							int count = event.getBidderDisqualify();
							LOG.info("Count fo supplier dis : " + count);
							for (int i = supplirBqPojoList.size() - 1; i >= 0; i--) {
								RfaSupplierBqPojo suppPojo = supplirBqPojoList.get(i);
								LOG.info("suppl pjo : " + suppPojo.getSupplierCompanyName() + " : rank : " + suppPojo.getRankOfSupplier() + " :i:   " + i);
							}
							for (int i = supplirBqPojoList.size() - 1; i >= 0; i--) {
								LOG.info("Count fo supplier dis befor check  : " + count + " and : i  =   " + i);
								if (count > 0) {
									RfaSupplierBqPojo suppPojo = supplirBqPojoList.get(i);
									LOG.info("supplier name in loop " + suppPojo.getSupplierCompanyName());
									if (suppPojo.getRankOfSupplier() != 999) {
										LOG.info("supplier name for dis final " + suppPojo.getSupplierCompanyName());

										rfaEventSupplierDao.updateEventSupplierDisqualify(event.getId(), suppPojo.getSupplierId(), rfaEventOwner, "Auto disqualify due to auction rules");
										count--;
										try {
											// sending mail on disqualify
											String buyerTimeZone = "GMT+8:00";
											String buyerUrl = APP_URL + "/buyer/RFA/eventSummary/" + event.getId();
											String msg = "";
											rfaEvent = rfaEventDao.getEventNameAndReferenceNumberById(event.getId());
											buyerTimeZone = getTimeZoneByBuyerSettings(rfaEventOwner, buyerTimeZone);

											// Sending Mails and Notifications
											// for Event owner
											msg = "<i>\"" + suppPojo.getSupplierCompanyName() + "\"</i> has been disqualified for the following event.";
											sendSupplierDisqualifyNotification(rfaEventOwner, rfaEvent, buyerUrl, buyerTimeZone, RfxTypes.RFA.getValue(), msg);

											List<User> buyerUserList = rfaEventDao.getUserBuyerTeamMemberByEventId(event.getId());
											if (CollectionUtil.isNotEmpty(buyerUserList)) {
												for (User teamUser : buyerUserList) {
													sendSupplierDisqualifyNotification(teamUser, rfaEvent, buyerUrl, buyerTimeZone, RfxTypes.RFA.getValue(), msg);
												}
											}

											String suppUrl = APP_URL + "/supplier/viewSupplierEvent/RFA/" + event.getId();
											String suppTimeZone = getTimeZoneBySupplierSettings(suppPojo.getSupplierId(), "GMT+8:00");
											msg = "You have been disqualified  for the following event.";

											// Sending Mails and Notifications
											// for supplier admin users
											List<User> userList = userDao.getAllAdminPlainUsersForSupplier(suppPojo.getSupplierId());
											for (User adminUser : userList) {
												sendSupplierDisqualifyNotification(adminUser, rfaEvent, suppUrl, suppTimeZone, RfxTypes.RFA.getValue(), msg);
											}

											// Sending Mails and Notifications
											// for supplier team members
											List<User> rfaSupplierMembers = rfaSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
											if (CollectionUtil.isNotEmpty(rfaSupplierMembers)) {
												for (User supplierTeamUser : rfaSupplierMembers) {
													sendSupplierDisqualifyNotification(supplierTeamUser, rfaEvent, suppUrl, suppTimeZone, RfxTypes.RFA.getValue(), msg);
												}
											}

										} catch (Exception e) {
											LOG.error("Error While Sending notification on auto disqualify :" + e.getMessage(), e);
											returnValue = false;
										}
									}
								}
							}
						}

					}
					List<RfaEvent> relativeEventList = rfaEventDao.getAllAssosiateAuctionForReschdule(event.getId());
					if (CollectionUtil.isNotEmpty(relativeEventList)) {
						manageRelativeEventOnTimeExt(event, timeZone, event.getTimeExtensionDuration(), event.getTimeExtensionDurationType());
					}
					rfaEventDao.updateTimeExtension(event.getId(), (event.getTotalExtensions() + 1), cal.getTime());
					// sending auto time extension mail and notifications
					String buyerTimeZone = "GMT+8:00";
					try {
						LOG.info("Extended time is :" + event.getTimeExtensionDuration() + " " + event.getTimeExtensionLeadingBidType());

						try {
							// jmsTemplate.setDefaultDestinationName("QUEUE.RFA.EVENT.EXTENDED.NOTIFICATION");
							jmsTemplate.send("QUEUE.RFA.EVENT.EXTENDED.NOTIFICATION", new MessageCreator() {
								@Override
								public Message createMessage(Session session) throws JMSException {
									TextMessage objectMessage = session.createTextMessage();
									objectMessage.setText(event.getId());
									return objectMessage;
								}
							});
						} catch (Exception e) {
							LOG.error("Error sending message to queue : " + e.getMessage(), e);
						}

					} catch (Exception e) {
						LOG.error("Error while sending auto time extension : " + e.getMessage(), e);
						returnValue = false;
					}
					byte[] summarySnapshot = null;
					// try {
					// JasperPrint eventSummary = getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(),
					// (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
					// summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
					// } catch (JRException e) {
					// LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
					// }
					buyerTimeZone = getTimeZoneByBuyerSettings(rfaEventOwner, buyerTimeZone);

					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
					RfaEventAudit audit = new RfaEventAudit(event.getCreatedBy().getBuyer(), event, event.getCreatedBy(), new java.util.Date(), AuditActionType.Extension, messageSource.getMessage("event.audit.time.auto.extended", new Object[] { df.format(eventEnd), event.getTimeExtensionDuration(), event.getTimeExtensionDurationType() }, Global.LOCALE), summarySnapshot);
					rfaEventAuditDao.save(audit);

				}
			}
		} catch (Exception e) {
			LOG.error("Error  : " + e.getMessage(), e);
			returnValue = false;
		}
		return returnValue;
	}

	@Override
	@Transactional(readOnly = false)
	public String createNextEvent(RfaEvent rfaEvent, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, String[] invitedSupp, String concludeRemarks) throws Exception {
		LOG.info("createNextEvent RFA idRfxTemplate: " + idRfxTemplate + " businessUnitId : " + businessUnitId);
		RfaEvent oldEvent = getRfaEventByeventId(rfaEvent.getId());

		// Clear Award details if present.
		int deleteCount = rfaEventAwardDao.removeAwardDetails(rfaEvent.getId());

		if (deleteCount > 0) {
			try {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setActionDate(new Date());
				audit.setActionBy(loggedInUser);
				audit.setDescription(messageSource.getMessage("event.award.discarded", new Object[] { oldEvent.getEventName() }, Global.LOCALE));
				audit.setEvent(rfaEvent);
				audit.setAction(AuditActionType.Discard);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISCARD, messageSource.getMessage("event.award.discarded", new Object[] { oldEvent.getEventId() }, Global.LOCALE), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
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

		if (oldEvent.getGroupCode() != null && oldEvent.getGroupCode().getStatus() == Status.INACTIVE) {
			LOG.info("inactive Group Code found ....");
			throw new ApplicationException("The group code '" + oldEvent.getGroupCode().getGroupCode() + "' used in Event is inactive");
		} else {
			LOG.info("active Group Code found ........");
		}

		RfxTemplate rfxTemplate = null;
		if (StringUtils.checkString(idRfxTemplate).length() > 0) {
			rfxTemplate = rfxTemplateService.getRfxTemplateById(idRfxTemplate);
		}
		if (rfxTemplate != null && Status.INACTIVE == rfxTemplate.getStatus()) {
			LOG.info("inactive Template [" + rfxTemplate.getTemplateName() + "] found for Id .... " + rfxTemplate.getId());
			throw new ApplicationException("Template [" + rfxTemplate.getTemplateName() + "] is Inactive");
		}

		String newEventId = null;

		BusinessUnit selectedbusinessUnit = null;
		if (StringUtils.checkString(businessUnitId).length() > 0) {
			LOG.info("---------------finding BU --------------------");
			selectedbusinessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
		}
		List<RfaSupplierBq> rfaSupplierBqs = rfaSupplierBqService.findRfaSummarySupplierBqbyEventId(rfaEvent.getId());
		if (selectedRfxType != null) {
			switch (selectedRfxType) {
			case RFA: {

				// get All SupplierBq by event Id - match it with selected/qualified suppliers and pass only their BQs
				// to clone.
				List<RfaSupplierBq> rfaSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfaSupplierBqList = new ArrayList<RfaSupplierBq>();
					for (RfaSupplierBq bq : rfaSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfaSupplierBqList.add(bq);
						}
					}
				}

				// if no suppliers selected then copy all suppliers from previous event.
				RfaEvent newEvent = rfaEvent.createNextRfaEvent(oldEvent, auctionType, bqId, loggedInUser, invitedSupp, rfaSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());

				// Take the list out as its transient.
				List<RfaSupplierBq> supplierBqs = newEvent.getRfaSupplierBqs();

				AuctionRules auctionRules = new AuctionRules();
				if (rfxTemplate != null) {
					newEvent.setTemplate(rfxTemplate);

					createRfaFromTemplate(newEvent, rfxTemplate, auctionRules, selectedbusinessUnit, loggedInUser, false);

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
					rfaEvent.setErpEnable(false);
				}

				newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFA", newEvent.getBusinessUnit()));

				// newEvent.setEventId(eventIdSettingsDao.generateEventId(loggedInUser.getTenantId(),
				// "RFA"));
				RfaEvent newdbEvent = saveRfaEvent(newEvent, loggedInUser);
				newEvent.setId(newdbEvent.getId());
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfaTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

					}
				}
				// Auction Rules
				if (newdbEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || newdbEvent.getAuctionType() == AuctionType.REVERSE_ENGISH || newdbEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID || newdbEvent.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					newdbEvent.setBillOfQuantity(Boolean.TRUE);
				}
				newdbEvent = rfaEventService.saveRfaEvent(newdbEvent, loggedInUser);

				// save Auction Rule
				if (invitedSupp != null && invitedSupp.length > 0) {
					auctionRules.setPreBidBy(PreBidByType.BUYER);
				}
				auctionRules.setEvent(newdbEvent);
				auctionRules.setAuctionType(newdbEvent.getAuctionType());
				if (auctionRules.getAuctionType() != null && (auctionRules.getAuctionType() == AuctionType.FORWARD_DUTCH || auctionRules.getAuctionType() == AuctionType.FORWARD_ENGISH || auctionRules.getAuctionType() == AuctionType.FORWARD_SEALED_BID)) {
					auctionRules.setFowardAuction(Boolean.TRUE);
				} else {
					auctionRules.setFowardAuction(Boolean.FALSE);
				}

				rfaEventService.saveAuctionRules(auctionRules);

				// save Contacts
				if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
					for (RfaEventContact contact : newEvent.getEventContacts()) {
						contact.setRfaEvent(newdbEvent);
						saveRfaEventContact(contact);
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
						RfaEventBq dbBq = rfaEventBqDao.saveOrUpdate(bq);
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

						bq.setId(dbBq.getId());
					}

				}

				// Save Supplier Bqs
				if (invitedSupp != null && invitedSupp.length > 0) {
					if (CollectionUtil.isNotEmpty(supplierBqs)) {
						for (RfaSupplierBq sbq : supplierBqs) {
							RfaSupplierBq sbqDb = rfaSupplierBqService.saveOrUpdateSupplierBq(sbq);
							RfaSupplierBqItem parent = null;
							for (RfaSupplierBqItem sbqi : sbq.getSupplierBqItems()) {
								sbqi.setSupplierBq(sbqDb);
								if (sbqi.getOrder() != 0) {
									sbqi.setParent(parent);
								}
								RfaSupplierBqItem sbqiDb = rfaSupplierBqItemDao.save(sbqi);
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

						RfaEventAudit RfaAudit = new RfaEventAudit();
						RfaAudit.setAction(AuditActionType.Create);
						RfaAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						RfaAudit.setActionDate(new Date());
						RfaAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						RfaAudit.setEvent(oldEvent);
						eventAuditService.save(RfaAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				break;
			}
			case RFI: {
				RfiEvent newEvent = rfaEvent.createNextRfiEvent(oldEvent, loggedInUser, invitedSupp);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());

				if (rfxTemplate != null) {
					newEvent.setTemplate(rfxTemplate);
					rfiEventService.createRfiFromTemplate(newEvent, rfxTemplate, selectedbusinessUnit, loggedInUser, false);
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

				// newEvent.setEventId(eventIdSettingsDao.generateEventId(loggedInUser.getTenantId(),
				// "RFI"));
				RfiEvent newdbEvent = rfiEventService.saveRfiEvent(newEvent);
				if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
					for (RfiTeamMember newTeamMembers : newEvent.getTeamMembers()) {
						sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

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

						RfaEventAudit RfaAudit = new RfaEventAudit();
						RfaAudit.setAction(AuditActionType.Create);
						RfaAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						RfaAudit.setActionDate(new Date());
						RfaAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						RfaAudit.setEvent(oldEvent);
						eventAuditService.save(RfaAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to ' " + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFP: {
				List<RfaSupplierBq> rfaSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfaSupplierBqList = new ArrayList<RfaSupplierBq>();
					for (RfaSupplierBq bq : rfaSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfaSupplierBqList.add(bq);
						}
					}
				}

				RfpEvent newEvent = rfaEvent.createNextRfpEvent(oldEvent, loggedInUser, invitedSupp, rfaSupplierBqList, bqId);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());

				// Take the list out as its transient.
				// List<RfpSupplierBq> supplierBqs = newEvent.getRfpSupplierBqs();

				// newEvent.setEventId(eventIdSettingsDao.generateEventId(loggedInUser.getTenantId(),
				// "RFP"));

				if (rfxTemplate != null) {
					newEvent.setTemplate(rfxTemplate);
					rfpEventService.createRfpFromTemplate(newEvent, rfxTemplate, null, selectedbusinessUnit, loggedInUser, false);
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
						sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFI, newdbEvent.getId());

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
				// // Save Supplier Bqs
				// if (invitedSupp != null && invitedSupp.length > 0) {
				// for (RfpSupplierBq sbq : supplierBqs) {
				// RfpSupplierBq sbqDb = rfpSupplierBqService.updateSupplierBq(sbq);
				// RfpSupplierBqItem parent = null;
				// for (RfpSupplierBqItem sbqi : sbq.getSupplierBqItems()) {
				// sbqi.setSupplierBq(sbqDb);
				// if (sbqi.getOrder() != 0) {
				// sbqi.setParent(parent);
				// }
				//
				// RfpSupplierBqItem sbpiDb = rfpSupplierBqItemDao.save(sbqi);
				// if (sbqi.getOrder() == 0) {
				// parent = sbpiDb;
				// }
				//
				// }
				// }
				// }
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

						RfaEventAudit RfaAudit = new RfaEventAudit();
						RfaAudit.setAction(AuditActionType.Create);
						RfaAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						RfaAudit.setActionDate(new Date());
						RfaAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						RfaAudit.setEvent(oldEvent);
						eventAuditService.save(RfaAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to ' " + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFQ: {

				List<RfaSupplierBq> rfaSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfaSupplierBqList = new ArrayList<RfaSupplierBq>();
					for (RfaSupplierBq bq : rfaSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfaSupplierBqList.add(bq);
						}
					}
				}
				RfqEvent newEvent = rfaEvent.createNextRfqEvent(oldEvent, loggedInUser, invitedSupp, rfaSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());

				// List<RfqSupplierBq> supplierBqs = newEvent.getRfqSupplierBqs();
				if (rfxTemplate != null) {
					newEvent.setTemplate(rfxTemplate);
					rfqEventService.createRfqFromTemplate(newEvent, rfxTemplate, selectedbusinessUnit, loggedInUser, false);
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
						bq.setId(dbBq.getId());
					}
				}
				// if (invitedSupp != null && invitedSupp.length > 0) {
				// for (RfqSupplierBq sbq : supplierBqs) {
				// RfqSupplierBq sbqDb = rfqSupplierBqService.updateSupplierBq(sbq);
				// RfqSupplierBqItem parent = null;
				// for (RfqSupplierBqItem sbqi : sbq.getSupplierBqItems()) {
				// sbqi.setSupplierBq(sbqDb);
				// if (sbqi.getOrder() != 0) {
				// sbqi.setParent(parent);
				// }
				//
				// RfqSupplierBqItem sbqiDb = rfqSupplierBqItemDao.save(sbqi);
				// if (sbqi.getOrder() == 0) {
				// parent = sbqiDb;
				// }
				//
				// }
				// }
				// }
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

						RfaEventAudit RfaAudit = new RfaEventAudit();
						RfaAudit.setAction(AuditActionType.Create);
						RfaAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						RfaAudit.setActionDate(new Date());
						RfaAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newdbEvent.getEventId() + " '");
						RfaAudit.setEvent(oldEvent);
						eventAuditService.save(RfaAudit);

						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Event '" + oldEvent.getEventId() + " ' is pushed to ' " + newdbEvent.getEventId() + " '", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}

				break;
			}
			case RFT: {

				List<RfaSupplierBq> rfaSupplierBqList = null;
				if (invitedSupp != null) {
					List<String> invitedList = Arrays.asList(invitedSupp);
					rfaSupplierBqList = new ArrayList<RfaSupplierBq>();
					for (RfaSupplierBq bq : rfaSupplierBqs) {
						if (invitedList.contains(bq.getSupplier().getId()) && bq.getBq().getId().equals(bqId)) {
							rfaSupplierBqList.add(bq);
						}
					}
				}

				RftEvent newEvent = rfaEvent.createNextRftEvent(oldEvent, loggedInUser, invitedSupp, rfaSupplierBqList);
				newEvent.setReferanceNumber(oldEvent.getReferanceNumber());
				// List<RftSupplierBq> supplierBqs = newEvent.getRftSupplierBqs();
				// newEvent.setEventId(eventIdSettingsDao.generateEventId(loggedInUser.getTenantId(),
				// "RFT"));
				if (rfxTemplate != null) {

					newEvent.setTemplate(rfxTemplate);
					rftEventService.createRftFromTemplate(newEvent, rfxTemplate, selectedbusinessUnit, loggedInUser, false);
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
						bq.setId(dbBq.getId());
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

						RfaEventAudit RfaAudit = new RfaEventAudit();
						RfaAudit.setAction(AuditActionType.Create);
						RfaAudit.setActionBy(SecurityLibrary.getLoggedInUser());
						RfaAudit.setActionDate(new Date());
						RfaAudit.setDescription("Event '" + oldEvent.getEventId() + " ' is pushed to '" + newDbEvent.getEventId() + " '");
						RfaAudit.setEvent(oldEvent);
						eventAuditService.save(RfaAudit);

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
		oldEvent.setConcludeRemarks(rfaEvent.getConcludeRemarks());
		oldEvent.setStatus(EventStatus.FINISHED);
		oldEvent.setConcludeDate(new Date());
		oldEvent.setConcludeBy(loggedInUser);
		oldEvent.setAwardStatus(null);
		rfaEventDao.update(oldEvent);

		try {
			tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(oldEvent.getId(), EventStatus.FINISHED);
		} catch (Exception e) {
			LOG.error("Error updating Tat Report " + e.getMessage(), e);
		}

		return newEventId;
	}

	@Override
	public void createRfaFromTemplate(RfaEvent newEvent, RfxTemplate rfxTemplate, AuctionRules auctionRules, BusinessUnit selectedbusinessUnit, User loggedInUser, boolean isFromRequest) throws Exception {
		LOG.info("----------------------create Rfa From Template call----------------------------");
		if (CollectionUtil.isNotEmpty(rfxTemplate.getApprovals())) {
			List<RfaEventApproval> approvalList = new ArrayList<>();
			for (TemplateEventApproval templateApproval : rfxTemplate.getApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfaEventApproval newRfApproval = new RfaEventApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfaApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfaApprovalUser approvalUser = new RfaApprovalUser();
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
			List<RfaEventAwardApproval> approvalList = new ArrayList<>();
			for (TemplateAwardApproval templateApproval : rfxTemplate.getAwardApprovals()) {
				LOG.info("RFX Template Approval Level :" + templateApproval.getLevel());
				RfaEventAwardApproval newRfApproval = new RfaEventAwardApproval();
				newRfApproval.setApprovalType(templateApproval.getApprovalType());
				newRfApproval.setLevel(templateApproval.getLevel());
				newRfApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateApproval.getApprovalUsers())) {
					List<RfaAwardApprovalUser> rfApprovalList = new ArrayList<>();
					for (TemplateAwardApprovalUser templateApprovalUser : templateApproval.getApprovalUsers()) {
						RfaAwardApprovalUser approvalUser = new RfaAwardApprovalUser();
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
			List<RfaEventSuspensionApproval> suspApprovalList = new ArrayList<>();
			for (TemplateSuspensionApproval templateSuspApproval : rfxTemplate.getSuspensionApprovals()) {
				LOG.info("RFX Template Susp Approval Level :" + templateSuspApproval.getLevel());
				RfaEventSuspensionApproval suspesionApproval = new RfaEventSuspensionApproval();
				suspesionApproval.setApprovalType(templateSuspApproval.getApprovalType());
				suspesionApproval.setLevel(templateSuspApproval.getLevel());
				suspesionApproval.setEvent(newEvent);
				if (CollectionUtil.isNotEmpty(templateSuspApproval.getApprovalUsers())) {
					List<RfaSuspensionApprovalUser> rfaSuspApprovalList = new ArrayList<>();
					for (TemplateSuspensionApprovalUser templateApprovalUser : templateSuspApproval.getApprovalUsers()) {
						RfaSuspensionApprovalUser approvalUser = new RfaSuspensionApprovalUser();
						approvalUser.setApprovalStatus(templateApprovalUser.getApprovalStatus());
						approvalUser.setApproval(suspesionApproval);
						approvalUser.setRemarks(templateApprovalUser.getRemarks());
						approvalUser.setUser(templateApprovalUser.getUser());
						rfaSuspApprovalList.add(approvalUser);
					}
					suspesionApproval.setApprovalUsers(rfaSuspApprovalList);
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
		List<RfaUnMaskedUser> unmaskingUser = new ArrayList<RfaUnMaskedUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getUnMaskedUsers())) {
			for (TemplateUnmaskUser team : rfxTemplate.getUnMaskedUsers()) {
				RfaUnMaskedUser newRftUnMaskedUser = new RfaUnMaskedUser();
				newRftUnMaskedUser.setUser(team.getUser());
				newRftUnMaskedUser.setEvent(newEvent);
				unmaskingUser.add(newRftUnMaskedUser);
			}
			newEvent.setUnMaskedUsers(unmaskingUser);
		}
		newEvent.setRevertLastBid(rfxTemplate.getRevertLastBid());
		newEvent.setRevertBidUser(rfxTemplate.getRevertBidUser());

		List<RfaTeamMember> teamMembers = new ArrayList<RfaTeamMember>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getTeamMembers())) {
			for (TemplateEventTeamMembers team : rfxTemplate.getTeamMembers()) {
				RfaTeamMember newTeamMembers = new RfaTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setEvent(newEvent);
				teamMembers.add(newTeamMembers);
				// sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(),
				// newTeamMembers.getTeamMemberType(),
				// newEvent.getEventOwner(),
				// StringUtils.checkString(newEvent.getEventName()).length() > 0
				// ?
				// newEvent.getEventName() : " ", newEvent.getEventId(),
				// StringUtils.checkString(newEvent.getReferanceNumber()).length()
				// > 0 ? newEvent.getReferanceNumber() :
				// " ", rfxTemplate.getType());
			}
			newEvent.setTeamMembers(teamMembers);
		}

		// copy envlope from rxf template

		newEvent.setRfxEnvelopeReadOnly(rfxTemplate.getRfxEnvelopeReadOnly());
		newEvent.setRfxEnvelopeOpening(rfxTemplate.getRfxEnvelopeOpening());
		newEvent.setRfxEnvOpeningAfter(rfxTemplate.getRfxEnvOpeningAfter());
		newEvent.setEnableEvaluationDeclaration(rfxTemplate.getEnableEvaluationDeclaration());
		newEvent.setEnableSupplierDeclaration(rfxTemplate.getEnableSupplierDeclaration());
		newEvent.setViewAuctionHall(rfxTemplate.getViewAuctionHall());
		newEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
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
			newEvent.setRfaEnvelop(new ArrayList<RfaEnvelop>());
			for (RfxEnvelopPojo en : envlopeList) {
				LOG.info("envlop name form rfx   :  " + en);

				RfaEnvelop rfienvlope = new RfaEnvelop();
				rfienvlope.setEnvelopTitle(en.getRfxEnvelope());
				rfienvlope.setEnvelopSequence(en.getRfxSequence());
				rfienvlope.setLeadEvaluater(SecurityLibrary.getLoggedInUser());
				rfienvlope.setRfxEvent(newEvent);
				rfienvlope.setEnvelopType(EnvelopType.CLOSED);
				newEvent.getRfaEnvelop().add(rfienvlope);
			}
		}

		// Copy Evaluation Conclusion Users from template
		newEvent.setEnableEvaluationConclusionUsers(rfxTemplate.getEnableEvaluationConclusionUsers());
		List<RfaEvaluationConclusionUser> evaluationConclusionUsers = new ArrayList<RfaEvaluationConclusionUser>();
		if (CollectionUtil.isNotEmpty(rfxTemplate.getEvaluationConclusionUsers())) {
			LOG.info("Copying Evaluation Conclusion Users from Template : " + rfxTemplate.getEvaluationConclusionUsers().size());
			for (User user : rfxTemplate.getEvaluationConclusionUsers()) {
				RfaEvaluationConclusionUser conclusionUser = new RfaEvaluationConclusionUser();
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
						// LOG.info("businessUnit : " + businessUnit + "Default
						// value : " +
						// field.getDefaultValue());
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

						// Setting multiple event categories previously it was
						// only one
						String[] icArr = field.getDefaultValue().split(",");
						List<IndustryCategory> icList = new ArrayList<IndustryCategory>();
						if (icArr != null) {
							for (String icId : icArr) {
								// IndustryCategory ic =
								// industryCategoryService.getIndustryCategoryById(icId);
								IndustryCategory ic = new IndustryCategory();
								ic.setId(icId);
								icList.add(ic);
							}
							newEvent.setIndustryCategories(icList);
						}
						// newEvent.setIndustryCategory(industryCategory);
						// LOG.info("industryCategory : " + industryCategory +
						// "Default value : " +
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
				case _UNKNOWN:
					if (field.getDefaultValue() != null) {
					}
					break;
				case AUCTION_PRICE_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setAuctionConsolePriceType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;
				case AUCTION_RANK_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setAuctionConsoleRankType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;
				case AUCTION_SUPPLIER_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setAuctionConsoleVenderType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;

				case AUCTION_BUY_PRICE_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBuyerAuctionConsolePriceType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;
				case AUCTION_BUY_RANK_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBuyerAuctionConsoleRankType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;
				case AUCTION_BUY_SUPPLIER_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBuyerAuctionConsoleVenderType(AuctionConsolePriceVenderType.valueOf(field.getDefaultValue()));
					}
					break;

				case BIDDER_DISQUALIFY_COUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setBidderDisqualify(Integer.parseInt(field.getDefaultValue()));
					}
					break;
				case BIDDING_TYPE:
					if (field.getDefaultValue() != null) {
						if (field.getDefaultValue().equals("ITEMIZEDWITHTAX")) {
							auctionRules.setItemizedBiddingWithTax(Boolean.TRUE);
						} else if (field.getDefaultValue().equals("ITEMIZEDWITHOUTTAX")) {
							auctionRules.setItemizedBiddingWithTax(Boolean.FALSE);
						} else if (field.getDefaultValue().equals("LUMPSUMWITHTAX")) {
							auctionRules.setLumsumBiddingWithTax(Boolean.TRUE);
						} else if (field.getDefaultValue().equals("LUMPSUMWITHOUTTAX")) {
							auctionRules.setLumsumBiddingWithTax(Boolean.FALSE);
						}
						// auctionRules.setItemizedBiddingWithTax(field.getDefaultValue());
					}
					break;
				case BID_HIGHER_LEAD_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBiddingPriceHigherLeadingBidType(ValueType.valueOf(field.getDefaultValue()));
					}
					break;
				case BID_HIGHER_LEAD_VAL:
					if (field.getDefaultValue() != null) {
						auctionRules.setBiddingPriceHigherLeadingBidValue(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case BID_INCR_OWN_PRE_TYPE:
					if (field.getDefaultValue() != null) {
						auctionRules.setBiddingMinValueType(ValueType.valueOf(field.getDefaultValue()));
					}
					break;
				case BID_INCR_OWN_PRE_VAL:
					if (field.getDefaultValue() != null) {
						auctionRules.setBiddingMinValue(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case EXTENSION_COUNT:
					if (field.getDefaultValue() != null) {
						newEvent.setExtensionCount(Integer.parseInt(field.getDefaultValue()));
					}
					break;
				case IS_AUTO_BIDDER_DISQUALIFY:
					if (field.getDefaultValue() != null) {
						newEvent.setAutoDisqualify(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case IS_BIDDING_SAMEPRICE_SUPPLIER:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsBiddingAllowSupplierSameBid(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case IS_BID_HIGHER_LEADING:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsBiddingPriceHigherLeadingBid(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case IS_BID_INCR_OWN_PREVIOUS:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsBiddingMinValueFromPrevious(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case PRE_BID_BY:
					if (field.getDefaultValue() != null) {
						auctionRules.setPreBidBy(PreBidByType.valueOf(field.getDefaultValue()));
					}
					break;
				case PRE_SUPPLIER_PROVIDE_HIGHER:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsPreBidHigherPrice(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case PRE_SUPPLIER_SAME_BID:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsPreBidSameBidPrice(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case START_GATE:
					if (field.getDefaultValue() != null) {
						auctionRules.setIsStartGate(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				case TIME_EXT_DURATION_TYPE:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionDurationType(DurationType.valueOf(field.getDefaultValue()));
					}
					break;
				case TIME_EXT_TYPE:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionType(TimeExtensionType.valueOf(field.getDefaultValue()));
					}
					break;
				case TIME_TRIGGER_TYPE:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionLeadingBidType(DurationType.valueOf(field.getDefaultValue()));
					}
					break;
				case TIME_TRIGGER_VAL:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionLeadingBidValue(Integer.parseInt(field.getDefaultValue()));
					}
					break;
				case TIME_EXT_DURATION:
					if (field.getDefaultValue() != null) {
						newEvent.setTimeExtensionDuration(Integer.parseInt(field.getDefaultValue()));
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
				case PRE_AS_FIRSTBID:
					if (field.getDefaultValue() != null) {
						auctionRules.setPrebidAsFirstBid(Boolean.valueOf(field.getDefaultValue()));
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
				case PRE_SET_SAME_PRE_BID_ALL_SUPPLIER:
					if (field.getDefaultValue() != null) {
						auctionRules.setPreSetSamePreBidForAllSuppliers(Boolean.valueOf(field.getDefaultValue()));
					}
					break;
				default:
					break;

				}
			}

		}

		if (eventIdSettingsDao.isBusinessSettingEnable(loggedInUser.getTenantId(), "RFA")) {
			if (selectedbusinessUnit != null) {
				LOG.info("business unit selected by user choice selected");
				newEvent.setBusinessUnit(selectedbusinessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (newEvent.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select its own business unit");
					throw new ApplicationException("BUSINESS_UNIT_EMPTY");
				}

				/*******************************************************************************************/
			}
		}

	}

	@Override
	public AuctionRules getAuctionRulesForAuctionConsole(String eventId) {
		AuctionRules auctionRulesForAuction = auctionRulesDao.getAuctionRulesForAuctionConsole(eventId);
		return auctionRulesForAuction;
	}

	@Override
	public RfaEvent getLeanEventbyEventId(String eventId) {
		return rfaEventDao.getLeanEventbyEventId(eventId);
	}

	@Override
	public List<Supplier> getAwardedSuppliers(String eventId) {
		return rfaEventDao.getAwardedSuppliers(eventId);
	}

	@Override
	public RfaEvent getPlainEventById(String eventId) {
		return rfaEventDao.getPlainEventById(eventId);
	}

	@Override
	public AuctionRules getAuctionRulesForBidSumission(String eventId) {
		return auctionRulesDao.getAuctionRulesForBidSumission(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateLumsumAuction(String eventId, String supplierId, String bqId, BigDecimal totalAfterTax, BigDecimal differencePercentage) {
		rfaSupplierBqDao.updateLumsumAuction(eventId, supplierId, bqId, totalAfterTax, differencePercentage);
	}

	@Override
	public AuctionRules getLeanAuctionRulesByEventId(String eventId) {
		return auctionRulesDao.findLeanAuctionRulesByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventSupplierForAuction(String eventId, String supplierId, String ipAddress) {
		rfaEventSupplierDao.updateEventSupplierForAuction(eventId, supplierId, ipAddress);
	}

	@Override
	public List<AuctionBids> getAuctionBidsForSupplier(String supplierId, String eventId) {
		List<AuctionBids> auctionBids = auctionBidsDao.getAuctionBidsForSupplier(supplierId, eventId);
		for (AuctionBids auctionBids2 : auctionBids) {
			LOG.info(auctionBids2.getBidSubmissionDate());
		}
		return auctionBidsDao.getAuctionBidsForSupplier(supplierId, eventId);

	}

	@Override
	public AuctionBids getAuctionBidForBidId(String bidId) {
		return auctionBidsDao.findById(bidId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent suspendEnglishAuction(RfaEvent event) {
		RfaEvent persistObj = getPlainEventById(event.getId());
		try {
			LOG.info("at Starting stage date time  : " + new Date());
			persistObj.setStatus(EventStatus.SUSPENDED);
			persistObj.setSuspendRemarks(event.getSuspendRemarks());
			persistObj = rfaEventDao.update(persistObj);
			// This is for if RevertBidUser configured
			if (persistObj.getRevertBidUser() != null) {
				persistObj.getRevertBidUser().getLoginId();
			}
			LOG.info("Dutch auction (auctionId): " + persistObj.getId() + " Suspended by : " + SecurityLibrary.getLoggedInUser());
		} catch (Exception e) {
			LOG.error("Error during suspend auction by buyer : " + e.getMessage(), e);
		}
		return persistObj;
	}

	@Override
	public long getEventSuppliersCount(String eventId) {
		return rfaEventSupplierDao.getEventSuppliersCount(eventId);
	}

	@Override
	public RfaEvent getRfaEventForBidHistory(String eventId) {
		return rfaEventDao.getRfaEventForBidHistory(eventId);
	}

	@Override
	public List<Date> getAllMeetingDateByEventId(String eventId) {
		return rfaEventMeetingDao.getAllMeetingDateByEventId(eventId);
	}

	@Override
	public EventStatus checkRelativeEventStatus(String relativeEventId) {
		return rfaEventDao.checkRelativeEventStatus(relativeEventId);
	}

	@Override
	public List<RfaEvent> getAllAssosiateAuction(String eventId) {
		return rfaEventDao.getAllAssosiateAuction(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void suspendAllRelativeEvents(String eventId) {
		// Step 1 main events relative event
		List<RfaEvent> relativeEvents = rfaEventDao.getAllAssosiateAuction(eventId);
		LOG.info("relative event to suspand : " + relativeEvents.size());
		if (CollectionUtil.isNotEmpty(relativeEvents)) {
			for (RfaEvent rfaEvent : relativeEvents) {

				// Step2 relative event's relative event
				List<RfaEvent> relativeEventStep2 = rfaEventDao.getAllAssosiateAuction(rfaEvent.getId());
				if (CollectionUtil.isNotEmpty(relativeEventStep2)) {
					for (RfaEvent rfaEvent2 : relativeEventStep2) {

						// Step 3
						List<RfaEvent> relativeEventStep3 = rfaEventDao.getAllAssosiateAuction(rfaEvent2.getId());
						if (CollectionUtil.isNotEmpty(relativeEventStep3)) {
							for (RfaEvent rfaEvent3 : relativeEventStep3) {
								LOG.info("relative event step 3 : event name = " + rfaEvent3.getStatus() + " status : " + rfaEvent3.getStatus());
								rfaEvent3.setSuspendRemarks("This auction supended because the parent event is suspended by buyer");
								rfaEvent3.setStatus(EventStatus.SUSPENDED);
								rfaEventDao.update(rfaEvent3);
							}
						}
						LOG.info("relative event step 2 : event name = " + rfaEvent2.getEventName() + " status : " + rfaEvent2.getStatus());
						rfaEvent2.setSuspendRemarks("This auction supended because the parent event is suspended by buyer");
						rfaEvent2.setStatus(EventStatus.SUSPENDED);
						rfaEventDao.update(rfaEvent2);
					}
				}
				LOG.info("relative event step 1 : event name = " + rfaEvent.getEventName() + " status : " + rfaEvent.getStatus());
				rfaEvent.setSuspendRemarks("This auction supended because the parent event is suspended by buyer");
				rfaEvent.setStatus(EventStatus.SUSPENDED);
				rfaEventDao.update(rfaEvent);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void manageRelativeEventOnTimeExt(RfaEvent event, TimeZone timeZone, Integer duration, DurationType durationType) {
		try {
			LOG.info("Method called");
			List<RfaEvent> relativeEventList = rfaEventDao.getAllAssosiateAuctionForReschdule(event.getId());
			Calendar startCal = Calendar.getInstance(timeZone);
			Calendar endCal = Calendar.getInstance(timeZone);
			for (RfaEvent rfaEvent : relativeEventList) {
				LOG.info("Method called relative rfa 1 : " + rfaEvent.getEventName());
				LOG.info("Method called relative rfa 2 start Date : " + rfaEvent.getEventStart());
				LOG.info("Method called relative rfa 3 end Date : " + rfaEvent.getEventEnd());
				LOG.info("Method called relative rfa 3 end Date : " + duration);
				LOG.info("Method called relative rfa 3 end Date : " + durationType);
				startCal.setTime(rfaEvent.getEventStart());
				endCal.setTime(rfaEvent.getEventEnd());

				if (durationType == DurationType.HOUR) {
					LOG.info("Method called relative rfa 4 hour  end Date : ");
					startCal.add(Calendar.HOUR, duration);
					endCal.add(Calendar.HOUR, duration);
				} else {
					LOG.info("Method called relative rfa 5 miniute  end Date : ");
					startCal.add(Calendar.MINUTE, duration);
					endCal.add(Calendar.MINUTE, duration);
				}
				rfaEvent.setEventStart(startCal.getTime());
				rfaEvent.setEventEnd(endCal.getTime());
				LOG.info("Method called relative rfa 6 miniute  start  Date : " + rfaEvent.getEventStart());
				LOG.info("Method called relative rfa 7 miniute  end Date : " + rfaEvent.getEventEnd());
				LOG.info("Start Date : " + rfaEvent.getEventStart() + "end Date : " + rfaEvent.getEventEnd());
				LOG.info("Aution Type " + rfaEvent.getAuctionType());
				if (rfaEvent.getAuctionType() == AuctionType.FORWARD_DUTCH || rfaEvent.getAuctionType() == AuctionType.REVERSE_DUTCH) {
					AuctionRules auctionRules = auctionRulesDao.findAuctionRulesByEventId(rfaEvent.getId());
					LOG.info("agin schduer");
					scheduleAuction(auctionRules);
				}
			}

		} catch (Exception e) {
			LOG.error("Error :" + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void scheduleAuction(AuctionRules auctionRulesData) throws ApplicationException {
		try {
			LOG.info("agin schduer for auto time");
			AuctionRules auctionRules = auctionRulesDao.findAuctionRulesWithEventById(auctionRulesData.getId());
			LOG.info("auctionRules:" + auctionRules.toLogString());
			// RfaEvent event = auctionRules.getEvent();
			schedulerFactoryBean.getScheduler().pauseAll();
			JobKey jobKey = new JobKey("JOB" + auctionRules.getId(), "DUTCHAUCTION");
			JobDetail jobDetail = null;
			if (jobKey != null) {
				jobDetail = (JobDetail) schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
			}
			TriggerKey triggerKey = new TriggerKey("TRIGGER" + auctionRules.getId(), "DUTCHAUCTION");
			SimpleTriggerImpl trigger = null;
			if (triggerKey != null) {
				trigger = (SimpleTriggerImpl) schedulerFactoryBean.getScheduler().getTrigger(triggerKey);
			}

			LOG.info("Scheduling auction : " + auctionRules.getEvent().getEventName());
			if (jobDetail == null) {
				JobDetailFactoryBean jobDetailBean = new JobDetailFactoryBean();
				jobDetailBean.setJobClass(DutchAuctionJob.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("auctionId", auctionRules.getId());
				jobDetailBean.setJobDataAsMap(map);
				jobDetailBean.setGroup("DUTCHAUCTION");
				jobDetailBean.setName("JOB" + auctionRules.getId());
				jobDetailBean.afterPropertiesSet();
				jobDetail = jobDetailBean.getObject();
			}

			if (trigger == null) {
				SimpleTriggerFactoryBean triggerBean = new SimpleTriggerFactoryBean();
				triggerBean.setName("TRIGGER" + auctionRules.getId());
				triggerBean.setGroup("DUTCHAUCTION");
				triggerBean.setJobDetail(jobDetail);
				if (auctionRules.getIntervalType() == DurationMinSecType.MINUTE) {
					triggerBean.setStartTime(auctionRules.getEvent().getEventStart());
					triggerBean.setRepeatInterval(auctionRules.getInterval() * 1000 * 60);
					triggerBean.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("Schedule a job If Minute  new time: ");
				} else if (auctionRules.getIntervalType() == DurationMinSecType.SECONDS) {
					triggerBean.setStartTime(auctionRules.getEvent().getEventStart());
					triggerBean.setRepeatInterval(auctionRules.getInterval() * 1000);
					triggerBean.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("Schedule a job If Seconds new Time: ");
				}
				triggerBean.afterPropertiesSet();
				trigger = (SimpleTriggerImpl) triggerBean.getObject();
				schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
			} else {
				if (auctionRules.getIntervalType() == DurationMinSecType.MINUTE) {
					if (auctionRules.getEvent().getAuctionResumeDateTime() != null) {
						trigger.setStartTime(auctionRules.getEvent().getAuctionResumeDateTime());
						LOG.info("here after suspened" + auctionRules.getEvent().getAuctionResumeDateTime());
					} else {
						trigger.setStartTime(auctionRules.getEvent().getEventStart());
						LOG.info("normal event");
					}
					trigger.setRepeatInterval(auctionRules.getInterval() * 1000 * 60);
					trigger.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("ReSchedule a job If Minute : ");
				} else if (auctionRules.getIntervalType() == DurationMinSecType.SECONDS) {
					if (auctionRules.getEvent().getAuctionResumeDateTime() != null) {
						trigger.setStartTime(auctionRules.getEvent().getAuctionResumeDateTime());
						LOG.info("here after suspened sec" + auctionRules.getEvent().getAuctionResumeDateTime());
					} else {
						trigger.setStartTime(auctionRules.getEvent().getEventStart());
					}
					trigger.setRepeatInterval(auctionRules.getInterval() * 1000);
					trigger.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("ReSchedule a job If Seconds : ");
				}
				schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, trigger);
			}
		} catch (Exception e) {
			LOG.error("Error in scheduling auction : " + e.getMessage(), e);
			throw new ApplicationException("Error while scheduling auction : " + e.getMessage(), e);
		} finally {
			try {
				schedulerFactoryBean.getScheduler().resumeAll();
			} catch (SchedulerException e) {
				LOG.fatal("Error in resuming Schedule jobs : " + e.getMessage(), e);
			}
		}

	}

	@Override
	public Date calculateEndDateForDutchAuction(Integer interval, DurationMinSecType intervalType, Integer totalSteps, Date startDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		LOG.info("auction startDate " + startDate);
		int auctionDuration = (interval * totalSteps);
		if (intervalType == DurationMinSecType.MINUTE) {
			cal.add(Calendar.MINUTE, auctionDuration);
		} else {
			cal.add(Calendar.SECOND, auctionDuration);
		}
		LOG.info("auction endDate " + cal.getTime());
		return cal.getTime();
	}

	@Override
	public Integer findAssignedTemplateCount(String templateId) {
		return rfaEventDao.findAssignedTemplateCount(templateId);
	}

	@Override
	public List<RfaEvent> getAllAssosiateAuctionForReschdule(String eventId) {
		return rfaEventDao.getAllAssosiateAuction(eventId);
	}

	@Override
	public RfaEvent getRfaEventForTimeExtensionAndBidSubmission(String eventId) {
		return rfaEventDao.getRfaEventForTimeExtensionAndBidSubmission(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTimeExtension(String eventId, Integer totalExtensions, Date eventEnd) {
		rfaEventDao.updateTimeExtension(eventId, totalExtensions, eventEnd);
	}

	@Override
	public User getPlainEventOwnerByEventId(String eventId) {
		return rfaEventDao.getPlainEventOwnerByEventId(eventId);
	}

	private void sendSupplierDisqualifyNotification(User user, Event event, String url, String timeZone, String eventType, String msg) {
		String mailTo = "";
		String subject = "Supplier Disqualified";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("msg", msg);
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", findBusinessUnit(event.getId()));
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.SUPPLIER_DISQUALIFY_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending mail Notification on auto disqualify supplier:" + e.getMessage(), e);
		}
		try {
			String notificationMessage = msg;
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashboard Notification on auto disqualify supplier:" + e.getMessage(), e);
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

	/**
	 * @param user
	 * @param timeZone
	 * @return timeZone
	 */
	private String getTimeZoneByBuyerSettings(User user, String timeZone) {
		try {
			if (StringUtils.checkString(user.getTenantId()).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(user.getTenantId());
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	/**
	 * @param suppId
	 * @param timeZone
	 * @return
	 */
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
	public List<EventTeamMember> getPlainTeamMembersForEvent(String eventId) {
		return rfaEventDao.getPlainTeamMembersForEvent(eventId);
	}

	private void sendEventClosedNotification(User user, RfaEvent event, String url, String timeZone, String eventType) {

		// error occues in send email when event name is empty fixed issue using
		// thos
		if (event != null) {
			if (StringUtils.checkString(event.getEventName()).length() <= 0) {
				event.setEventName("");
			}
		}
		String mailTo = user.getCommunicationEmail();
		String subject = "Event Ended";
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("eventType", eventType);
		map.put("referenceNumber", event.getReferanceNumber());
		map.put("businessUnit", StringUtils.checkString(rfaEventDao.findBusinessUnitName(event.getId())));
		map.put("eventName", StringUtils.checkString(event.getEventName()).length() > 0 ? event.getEventName() : "N/A");
		map.put("eventName", event.getEventName() != null ? event.getEventName() : ' ');
		map.put("eventEndDateTime", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		if(user.getEmailNotifications())
		notificationService.sendEmail(mailTo, subject, map, Global.EVENT_CLOSED_TEMPLATE);

		String notificationMessage = messageSource.getMessage("common.event.closed.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
		sendDashboardNotification(user, url, subject, notificationMessage);
	}

	private void sendEnvelopOpenedNotification(User user, Event event, String url, String timeZone, String eventType, List<String> envelopTitleList, String envelopTitle) {
		String mailTo = user.getCommunicationEmail();
		String subject = "Envelope is ready to be opened";
		HashMap<String, Object> map = new HashMap<String, Object>();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("envelopTitle", CollectionUtil.isNotEmpty(envelopTitleList) ? envelopTitleList.toString() : envelopTitle);
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("eventType", eventType);
		map.put("referenceNumber", event.getReferanceNumber());
		map.put("eventName", event.getEventName());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);
		map.put("businessUnit", findBusinessUnit(event.getId()));
		if(user.getEmailNotifications())
		notificationService.sendEmail(mailTo, subject, map, Global.ENVELOPE_READY_OPENED_TEMPLATE);

		String notificationMessage = messageSource.getMessage("common.envelop.opened.notification.message", new Object[] { event.getReferanceNumber(), CollectionUtil.isNotEmpty(envelopTitleList) ? envelopTitleList.toString() : envelopTitle }, Global.LOCALE);
		sendDashboardNotification(user, url, subject, notificationMessage);
	}

	private void sendEventReadyEvaluationNotification(User user, Event event, String url, String timeZone, String eventType) {
		String subject = "Event is ready for evaluation";
		try {
			String mailTo = user.getCommunicationEmail();
			String msg = "Event \"" + event.getReferanceNumber() + "\" is ready for evaluation";
			HashMap<String, Object> map = new HashMap<String, Object>();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("msg", msg);
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", findBusinessUnit(event.getId()));
			if(user.getEmailNotifications())
			notificationService.sendEmail(mailTo, subject, map, Global.EVENT_EVALUATION_TEMPLATE);
		} catch (Exception e) {
			LOG.error("Error while sending mail Event is ready for evaluation : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("common.event.ready.evaluation.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error while sending dashborad notification Event is ready for evaluation : " + e.getMessage(), e);
		}
	}

	@Override
	public BidHistoryChartPojo getBidHistoryChartData(String eventId, String timeZone, String arrangeBidBy) {
		try {
			LOG.info(arrangeBidBy + " : arrange Bid By");
			BidHistoryChartPojo dataObject = new BidHistoryChartPojo();

			// RfaEvent event = this.getRfaEventForBidHistory(eventId);
			// dataObject.setAuctionStatus(event.getStatus());
			// dataObject.setStartDateTime(event.getEventStart());
			// dataObject.setEndDateTime(event.getEventEnd());
			BidHistoryPojo minMaxBid = this.getMinMaxBidPriceForEvent(eventId);
			dataObject.setMinPrice(minMaxBid.getMinPrice());
			dataObject.setMaxPrice(minMaxBid.getMaxPrice());

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));

			SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			df1.setTimeZone(TimeZone.getTimeZone(timeZone));

			List<RfaEventSupplier> supplier = rfaEventSupplierService.findSupplierByEventIdOnlyRank(eventId);
			Map<String, Integer> supplierRankMap = new HashMap<String, Integer>();
			for (RfaEventSupplier rfaEventSupplier : supplier) {
				if (rfaEventSupplier.getAuctionRankingOfSupplier() != null) {
					supplierRankMap.put(rfaEventSupplier.getSupplier().getId(), rfaEventSupplier.getAuctionRankingOfSupplier());
				}
			}
			supplier.clear();
			supplier = null;

			AuctionRules auctionRules = rfaEventService.getAuctionRulesForAuctionConsole(eventId);

			List<AuctionBids> auctionBids = auctionBidsDao.getAuctionBidsListByEventIdForReport(eventId);
			if (auctionBids != null) {
				dataObject.setTotalBids(auctionBids.size());
				Map<String, BidHistoryChartSupplierPojo> supplierMap = new HashMap<String, BidHistoryChartSupplierPojo>();
				int counter = 1;
				for (AuctionBids ab : auctionBids) {

					if (auctionRules.getBuyerAuctionConsolePriceType() == AuctionConsolePriceVenderType.SHOW_NONE) {
						ab.setAmount(null);
					} else if (auctionRules.getBuyerAuctionConsolePriceType() == AuctionConsolePriceVenderType.SHOW_LEADING && (auctionRules.getAuctionConsoleRankType() != null && 1 != ab.getRankForBid())) {
						// Price : show leading
						// LOG.info("----------SHOW_LEADING-----------");
						ab.setAmount(null);
					}

					List<String> bidData = new ArrayList<String>();
					if ("Time".equals(arrangeBidBy)) {
						DateTime dt = new DateTime(ab.getBidSubmissionDate());
						DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
						DateTime dtus = dt.withZone(dtZone);
						Date nowDateWithTimeZone = dtus.toLocalDateTime().toDate();
						bidData.add("" + nowDateWithTimeZone.getTime());
					} else {
						bidData.add("" + counter);
					}
					bidData.add("" + (ab.getAmount()));
					bidData.add("" + df.format((ab.getBidSubmissionDate())));

					BidHistoryChartSupplierPojo supplierData = supplierMap.get(ab.getBidBySupplier().getId());
					if (supplierData == null) {
						supplierData = new BidHistoryChartSupplierPojo();

						// LOG.info("COnsole Type : " +
						// auctionRules.getBuyerAuctionConsoleVenderType() + " -
						// " +
						// ab.getRankForBid() + " - " +
						// ab.getBidBySupplier().getCompanyName());

						Integer rank = supplierRankMap.get(ab.getBidBySupplier().getId());
						if (rank == null) {
							rank = -1;
						}

						if (auctionRules.getBuyerAuctionConsoleVenderType() == AuctionConsolePriceVenderType.SHOW_NONE) {
							supplierData.setLabel("");
						} else if (auctionRules.getBuyerAuctionConsoleVenderType() == AuctionConsolePriceVenderType.SHOW_LEADING && (1 != rank)) {
							supplierData.setLabel("");
							ab.setAmount(null);
							continue;
						} else {
							supplierData.setLabel(ab.getBidBySupplier().getCompanyName());
						}

						supplierData.setData(new ArrayList<List<String>>());
						supplierMap.put(ab.getBidBySupplier().getId(), supplierData);
					}
					supplierData.getData().add(bidData);
					counter++;
				}
				dataObject.setSupplierBids(new ArrayList<BidHistoryChartSupplierPojo>(supplierMap.values()));
			}
			return dataObject;
		} catch (Exception e) {
			LOG.error("Error While generating graph data : " + e.getMessage(), e);
			return null;
		}
	}

	private String findBusinessUnit(String eventId) {
		String displayName = rfaEventDao.findBusinessUnitName(eventId);
		return StringUtils.checkString(displayName);
	}

	private char[] c = new char[] { 'K', 'M', 'B', 'T' };

	/**
	 * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
	 * 
	 * @param n the number to format
	 * @param iteration in fact this is the class from the array c
	 * @return a String representing the number n formatted in a cool looking way.
	 */
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

	@Override
	public MobileEventPojo getMobileEventDetails(String id, String userId) throws ApplicationException {

		RfaEvent event = rfaEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(event.getTenantId(), timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}

		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			List<Bq> bqlist = new ArrayList<>();
			for (RfaEventBq bq : event.getEventBqs()) {
				bqlist.add(bq.createMobileShallowCopy());
			}
			eventPojo.setBqs(bqlist);
		}
		if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
			List<EventSupplier> supplierList = new ArrayList<>();
			for (RfaEventSupplier eventSupplier : event.getSuppliers()) {
				RfaEventSupplier supp = eventSupplier.createMobileRfaShallowCopy();
				supp.setRevisedBidSubmitted(eventSupplier.getRevisedBidSubmitted());
				supplierList.add(supp);
			}
			eventPojo.setSuppliers(supplierList);
		}
		List<EventTimelinePojo> timeLineList = new ArrayList<>();
		LOG.info("service method");
		for (EventTimeline timeLine : rfaEventTimeLineDao.getPlainRfaEventTimeline(event.getId())) {
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
		eventPojo.setDocuments(rfaDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRfaEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RfaEnvelop envelope : event.getRfaEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RfaEventApproval eventApproval : event.getApprovals()) {
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
			for (RfaComment comment : event.getComment()) {
				String actionDateApk = df.format(comment.getCreatedDate());
				comment.setCreatedDateApk(actionDateApk);
				comment.setTransientIsApproved(comment.isApproved());
				commentList.add(comment.createMobileShallowCopy());
			}
			eventPojo.setComments(commentList);
		}
		eventPojo.setAuctionRules(auctionRulesDao.findPlainAuctionRulesByEventId(event.getId()));

		if (eventPojo.getAuctionRules() != null && eventPojo.getAuctionRules().getLumsumBiddingWithTax() != null) {
			eventPojo.setHasReviseBid(true);
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

		RfaEvent event = rfaEventDao.getMobileEventDetails(id);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneBySupplierSettings(tenantId, timeZone);
		if (TimeZone.getTimeZone(timeZone) != null) {
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
		}

		MobileEventPojo eventPojo = new MobileEventPojo(event);
		if (CollectionUtil.isNotEmpty(event.getEventBqs())) {
			List<Bq> bqlist = new ArrayList<>();
			for (RfaEventBq bq : event.getEventBqs()) {
				bqlist.add(bq.createMobileShallowCopy());
			}
			eventPojo.setBqs(bqlist);
		}
		if (CollectionUtil.isNotEmpty(event.getSuppliers())) {
			List<EventSupplier> supplierList = new ArrayList<>();
			for (RfaEventSupplier eventSupplier : event.getSuppliers()) {
				RfaEventSupplier supp = eventSupplier.createMobileRfaShallowCopy();
				supp.setRevisedBidSubmitted(eventSupplier.getRevisedBidSubmitted());
				supplierList.add(supp);
			}
			eventPojo.setSuppliers(supplierList);
		}
		List<EventTimelinePojo> timeLineList = new ArrayList<>();
		LOG.info("service method");
		for (EventTimeline timeLine : rfaEventTimeLineDao.getPlainRfaEventTimeline(event.getId())) {
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
		eventPojo.setDocuments(rfaDocumentDao.findAllEventDocsNameAndId(event.getId()));
		if (CollectionUtil.isNotEmpty(event.getRfaEnvelop())) {
			List<Envelop> envelopList = new ArrayList<>();
			for (RfaEnvelop envelope : event.getRfaEnvelop()) {
				if (!envelope.getIsOpen() && envelope.getEnvelopType() == EnvelopType.CLOSED && envelope.getOpener() != null && envelope.getOpener().getId().equals(userId) && event.getStatus() == EventStatus.CLOSED) {
					envelope.setAllowOpen(true);
				}
				envelopList.add(envelope.createMobileShallowCopy());
			}
			eventPojo.setEnvelops(envelopList);
		}
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			List<EventApproval> approvalList = new ArrayList<>();
			for (RfaEventApproval eventApproval : event.getApprovals()) {
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
			for (RfaComment comment : event.getComment()) {
				String actionDateApk = df.format(comment.getCreatedDate());
				comment.setCreatedDateApk(actionDateApk);
				comment.setTransientIsApproved(comment.isApproved());
				commentList.add(comment.createMobileShallowCopy());
			}
			eventPojo.setComments(commentList);
		}
		eventPojo.setAuctionRules(auctionRulesDao.findPlainAuctionRulesByEventId(event.getId()));

		if (eventPojo.getAuctionRules() != null && eventPojo.getAuctionRules().getLumsumBiddingWithTax() != null) {
			eventPojo.setHasReviseBid(true);
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
	public BidHistoryPojo getBidHistoryOfOwnSupplierBid(String eventId, String supplierId) {
		RfaEvent event = rfaEventService.getRfaEventForBidHistory(eventId);
		BidHistoryPojo bidHistory = new BidHistoryPojo();
		bidHistory.setAuctionStatus(event.getStatus());
		bidHistory.setStartDateTime(event.getEventStart());
		bidHistory.setEndDateTime(event.getEventEnd());

		long auctionDuration = event.getEventEnd().getTime() - event.getEventStart().getTime();

		long auctionDurationInMin = auctionDuration / (60 * 1000) % 60;

		bidHistory.setDuration(auctionDurationInMin);

		BidHistoryPojo minMaxBid = rfaEventService.getMinMaxBidPriceForEvent(eventId);
		bidHistory.setMinPrice(minMaxBid.getMinPrice());
		bidHistory.setMaxPrice(minMaxBid.getMaxPrice());

		List<AuctionSupplierPojo> auctionSupplierList = new ArrayList<AuctionSupplierPojo>();
		RfaEventSupplier rfaEventSupplier = rfaEventSupplierService.getEventSupplierBySupplierAndEventId(supplierId, eventId);
		AuctionSupplierPojo auctionSupplierPojo = new AuctionSupplierPojo();
		auctionSupplierPojo.setSupplierCompanyName(rfaEventSupplier.getSupplier().getCompanyName());
		auctionSupplierPojo.setSupplierId(rfaEventSupplier.getId());

		List<AuctionSupplierBidPojo> listAuctionSupplierBidList = rfaEventService.getAuctionBidsListBySupplierIdAndEventId(supplierId, eventId);
		auctionSupplierPojo.setAuctionSupplierBids(listAuctionSupplierBidList);
		auctionSupplierList.add(auctionSupplierPojo);

		bidHistory.setSupplierAuction(auctionSupplierList);
		return bidHistory;
	}

	@Override
	public BidHistoryChartPojo getBidHistoryChartDataForSupplier(String eventId, String supplierId, String timeZone, String arrangeBidBy) {
		try {
			LOG.info(arrangeBidBy + " : arrange Bid By");
			BidHistoryChartPojo dataObject = new BidHistoryChartPojo();

			// RfaEvent event = this.getRfaEventForBidHistory(eventId);
			// dataObject.setAuctionStatus(event.getStatus());
			// dataObject.setStartDateTime(event.getEventStart());
			// dataObject.setEndDateTime(event.getEventEnd());
			BidHistoryPojo minMaxBid = this.getMinMaxBidPriceForEvent(eventId);
			dataObject.setMinPrice(minMaxBid.getMinPrice());
			dataObject.setMaxPrice(minMaxBid.getMaxPrice());

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));

			SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			// df1.setTimeZone(TimeZone.getTimeZone(timeZone));

			List<AuctionSupplierBidPojo> auctionBids = auctionBidsDao.getAuctionBidsListBySupplierIdAndEventId(supplierId, eventId);
			RfaEventSupplier rfaEventSupplier = rfaEventSupplierService.getEventSupplierBySupplierAndEventId(supplierId, eventId);
			AuctionSupplierPojo auctionSupplierPojo = new AuctionSupplierPojo();
			auctionSupplierPojo.setSupplierCompanyName(rfaEventSupplier.getSupplier().getCompanyName());
			auctionSupplierPojo.setSupplierId(rfaEventSupplier.getId());

			if (auctionBids != null) {
				dataObject.setTotalBids(auctionBids.size());
				Map<String, BidHistoryChartSupplierPojo> supplierMap = new HashMap<String, BidHistoryChartSupplierPojo>();
				int counter = 1;
				for (AuctionSupplierBidPojo ab : auctionBids) {

					List<String> bidData = new ArrayList<String>();

					if ("Time".equals(arrangeBidBy)) {
						DateTime dt = new DateTime(ab.getBidDateAndTime());
						DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone));
						DateTime dtus = dt.withZone(dtZone);
						Date nowDateWithTimeZone = dtus.toLocalDateTime().toDate();
						bidData.add("" + nowDateWithTimeZone.getTime());
					} else {
						bidData.add("" + counter);
					}
					bidData.add("" + (ab.getBidPrice()));
					bidData.add("" + df.format((ab.getBidDateAndTime())));
					Date date2 = df1.parse(df.format((ab.getBidDateAndTime())));
					LOG.info("date 2 : " + date2.getTime());
					BidHistoryChartSupplierPojo supplierData = supplierMap.get(supplierId);
					if (supplierData == null) {
						supplierData = new BidHistoryChartSupplierPojo();
						supplierData.setLabel(rfaEventSupplier.getSupplier().getCompanyName());
						supplierData.setData(new ArrayList<List<String>>());
						supplierMap.put(supplierId, supplierData);
					}
					supplierData.getData().add(bidData);
					counter++;
				}
				dataObject.setSupplierBids(new ArrayList<BidHistoryChartSupplierPojo>(supplierMap.values()));
			}
			return dataObject;
		} catch (Exception e) {
			LOG.error("Error While generating graph data : " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void downloadEventDocument(String docId, HttpServletResponse response) throws Exception {
		RfaEventDocument docs = rfaDocumentDao.findRfaDocsById(docId);
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
			RfaEvent event = getRfaEventByeventId(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " summary.pdf";
			}
			JasperPrint jasperPrint = rfaEventService.getEvaluationSummaryPdf(event, loggedInUser, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
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
	public RfaEvent getRfaEventByeventIdForSummaryReport(String eventId) {

		RfaEvent rfa = rfaEventDao.findByEventId(eventId);
		if (CollectionUtils.isNotEmpty(rfa.getApprovals())) {
			for (RfaEventApproval approval : rfa.getApprovals()) {
				for (RfaApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
				}
			}
		}
		if (rfa.getBusinessUnit() != null) {
			rfa.getBusinessUnit().getUnitName();
		}
		if (rfa != null) {
			rfa.getHistoricaAmount();
			rfa.getDecimal();
		}
		if (rfa.getCostCenter() != null) {
			rfa.getCostCenter().getCostCenter();
		}

		if (CollectionUtil.isNotEmpty(rfa.getSuppliers())) {
			for (RfaEventSupplier supplier : rfa.getSuppliers()) {
				supplier.getSupplier().getFullName();
				supplier.getSupplier().getCommunicationEmail();
				supplier.getSupplier().getCompanyContactNumber();
				supplier.getSupplier().getCompanyName();
				supplier.getSupplier().getStatus();
			}
		}
		if (rfa.getEventOwner().getBuyer() != null) {
			rfa.getEventOwner().getBuyer().getLine1();
			rfa.getEventOwner().getBuyer().getLine2();
			rfa.getEventOwner().getBuyer().getCity();
			if (rfa.getEventOwner().getBuyer().getState() != null) {
				rfa.getEventOwner().getBuyer().getState().getStateName();
				rfa.getEventOwner().getBuyer().getState().getCountry().getCountryName();
			}

		}
		if (rfa.getWinningSupplier() != null) {
			rfa.getWinningSupplier().getCompanyName();
		}

		if (CollectionUtil.isNotEmpty(rfa.getMeetings())) {
			for (RfaEventMeeting meeting : rfa.getMeetings()) {
				meeting.getAppointmentDateTime();
				meeting.getAppointmentTime();
				meeting.getInviteSuppliers();
				meeting.getTitle();
				meeting.getRfxEventMeetingContacts();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingContacts())) {
					for (RfaEventMeetingContact meetingContact : meeting.getRfxEventMeetingContacts()) {
						meetingContact.getContactName();
						meetingContact.getContactNumber();
						meetingContact.getContactEmail();
						meetingContact.getId();

					}
				}
				meeting.getStatus();
				meeting.getVenue();
				if (CollectionUtil.isNotEmpty(meeting.getRfxEventMeetingDocument())) {
					for (RfaEventMeetingDocument meetingDocument : meeting.getRfxEventMeetingDocument()) {
						meetingDocument.getId();
						meetingDocument.getCredContentType();
						meetingDocument.getFileSizeInKb();
						meetingDocument.getFileName();
					}
				}
				meeting.getRemarks();
				if (CollectionUtil.isNotEmpty(meeting.getInviteSuppliers())) {
					for (Supplier supplier : meeting.getInviteSuppliers()) {
						supplier.getCompanyName();
						supplier.getCommunicationEmail();
					}
				}
			}
		}
		if (CollectionUtil.isNotEmpty(rfa.getRfaStartReminder())) {
			for (RfaReminder reminder : rfa.getRfaStartReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfa.getRfaEndReminder())) {
			for (RfaReminder reminder : rfa.getRfaEndReminder()) {
				reminder.getReminderDate();
			}
		}
		if (CollectionUtil.isNotEmpty(rfa.getComment())) {
			for (RfaComment comment : rfa.getComment()) {
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
		if (CollectionUtil.isNotEmpty(rfa.getIndustryCategories())) {
			for (IndustryCategory industryCategory : rfa.getIndustryCategories()) {
				industryCategory.getName();
				industryCategory.getCreatedBy();
				if (industryCategory.getCreatedBy() != null) {
					industryCategory.getCreatedBy().getName();
				}
				industryCategory.getCode();
				industryCategory.getBuyer();
			}
		}

		return rfa;
	}

	@Override
	public RfaSupplierBq getSupplierBQOfLeastTotalPrice(String eventId, String bqId, String tenantId) {
		RfaSupplierBq rfaSupplierBq = rfaEventDao.getSupplierBQOfLeastTotalPrice(eventId, bqId);
		if (rfaSupplierBq != null) {

			if (rfaSupplierBq.getSupplierBqItems() != null) {
				for (RfaSupplierBqItem items : rfaSupplierBq.getSupplierBqItems()) {
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
		return rfaSupplierBq;
	}

	@Override
	public RfaSupplierBq getSupplierBQOfLowestItemisedPrice(String eventId, String bqId, String tenantId) {
		RfaSupplierBq rfaSupplierBq = new RfaSupplierBq();
		List<RfaSupplierBqItem> bqItemList = new ArrayList<RfaSupplierBqItem>();
		List<RfaSupplierBq> suppBqList = rfaEventDao.getSupplierBQOfLowestItemisedPrize(eventId, bqId);
		if (CollectionUtil.isNotEmpty(suppBqList)) {
			int bqItemCount = 1;
			for (RfaSupplierBq bq : suppBqList) {
				if (CollectionUtil.isNotEmpty(bq.getSupplierBqItems())) {
					for (RfaSupplierBqItem item : bq.getSupplierBqItems()) {
						// LOG.info(item.getBqItem().getId());
						if (item.getBqItem() != null) {
							item.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, item.getSupplier().getId()));
							if (item.getBqItem().getOrder() == 0) {
								if (1 == bqItemCount) {
									bqItemList.add(item);
								}

							} else {
								RfaSupplierBqItem bqItem = rfaEventDao.getMinItemisePrice(item.getBqItem().getId(), eventId);
								if (bqItem != null && !bqItemList.contains(bqItem)) {
									bqItemList.add(bqItem);
									rfaSupplierBq.setBq(bq.getBq());
								}
							}

						}
					}
					bqItemCount++;
				}
				rfaSupplierBq.setSupplierBqItems(bqItemList);

			}

		}

		return rfaSupplierBq;
	}

	@Override
	public RfaSupplierBq getSupplierBQwithSupplierId(String eventId, String bqId, String supplierId, String tenantId, String awardId) {
		RfaSupplierBq rfaSupplierBq = rfaEventDao.getSupplierBQwithSupplierId(eventId, bqId, supplierId);
		if (rfaSupplierBq != null) {

			if (rfaSupplierBq.getSupplierBqItems() != null) {
				for (RfaSupplierBqItem items : rfaSupplierBq.getSupplierBqItems()) {
					RfaEventAwardDetails rfaEventAwardDetails = null;
					if (StringUtils.checkString(awardId).length() > 0) {
						rfaEventAwardDetails = rfaEventAwardDetailsDao.rfaEventAwardByEventIdandBqId(awardId, items.getBqItem().getId());
					}
					items.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(tenantId, (rfaEventAwardDetails != null && rfaEventAwardDetails.getSupplier() != null) ? rfaEventAwardDetails.getSupplier().getId() : items.getSupplier().getId()));

					if (items.getBqItem() != null) {
						items.getBqItem().getId();
						items.getBqItem().getLevel();
					}

					// LOG.info(items.getBqItem().getItemName());
				}
			}
		}
		return rfaSupplierBq;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean disqualifySupplier(String supplierId, String remarks, String eventId, User loggedInUser) {
		if (rfaEventSupplierDao.updateEventSupplierDisqualify(eventId, supplierId, loggedInUser, remarks)) {

			LOG.info("...........in service............");
			RfaEvent rfaEvent = rfaEventDao.getEventNameAndReferenceNumberById(eventId);
			User rfaEventOwner = rfaEventDao.getPlainEventOwnerByEventId(eventId);
			String buyerTimeZone = "GMT+8:00";
			String buyerUrl = APP_URL + "/buyer/RFA/eventSummary/" + eventId;
			String msg = "";
			buyerTimeZone = getTimeZoneByBuyerSettings(rfaEventOwner, buyerTimeZone);
			// Sending Mails and Notifications
			// for Event owner

			Supplier supplier = supplierDao.findPlainSupplierUsingConstructorById(supplierId);
			msg = "<i>\"" + supplier.getCompanyName() + "\"</i> has been disqualified for the following event.";
			LOG.info(msg);
			sendSupplierDisqualifyNotification(rfaEventOwner, rfaEvent, buyerUrl, buyerTimeZone, RfxTypes.RFA.getValue(), msg);

			String suppUrl = APP_URL + "/supplier/viewSupplierEvent/RFA/" + eventId;
			String suppTimeZone = getTimeZoneBySupplierSettings(supplierId, "GMT+8:00");
			msg = "You have been disqualified  for the following event.";

			// Sending Mails and Notifications
			// for supplier admin users

			AuctionRules auctionRules = auctionRulesDao.findAuctionRulesByEventId(eventId);
			if (auctionRules.getFowardAuction()) {
				LOG.info("Here to update the rank : FRD :   ");
				Integer rank = rfaEventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplierId);
				LOG.info("Rank of supplier for Forward Auction:   " + rank);
			} else {
				Integer rank = rfaEventSupplierDao.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplierId);
				LOG.info("Rank of supplier for Reverse Auction:   " + rank);
			}

			User adminUser = userDao.getPlainUserByLoginId(supplier.getLoginEmail());

			sendSupplierDisqualifyNotification(adminUser, rfaEvent, suppUrl, suppTimeZone, RfxTypes.RFA.getValue(), msg);

			return true;
		}
		return false;

	}

	@Override
	public RfaEvent getRfaEventWithIndustryCategoriesByEventId(String eventId) {
		RfaEvent event = rfaEventDao.findByEventId(eventId);
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
	public boolean isExistsRfaEventId(String tenantId, String eventId) {
		return rfaEventDao.isExistsRfaEventId(tenantId, eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<RfaTeamMember> addAssociateOwners(User createdBy, RfaEvent newEvent) {
		List<User> adminUser = userDao.fetchAllActivePlainAdminUsersForTenant(createdBy.getTenantId());
		List<RfaTeamMember> teamMemberList = new ArrayList<RfaTeamMember>();
		if (CollectionUtil.isNotEmpty(adminUser)) {
			for (User user : adminUser) {
				RfaTeamMember teamMember = new RfaTeamMember();
				teamMember.setUser(user);
				teamMember.setEvent(newEvent);
				teamMember.setTeamMemberType(TeamMemberType.Associate_Owner);
				teamMemberList.add(teamMember);
			}
			newEvent.setTeamMembers(teamMemberList);
		}
		rfaEventDao.saveOrUpdate(newEvent);
		return teamMemberList;
	}

	@Override
	@Transactional(readOnly = false)
	public void setDefaultEventContactDetail(String loggedInUserId, String eventId) {
		RfaEventContact eventContact = new RfaEventContact();
		User user = userDao.findById(loggedInUserId);
		eventContact.setContactName(user.getName());
		eventContact.setDesignation(user.getDesignation());
		eventContact.setContactNumber(user.getPhoneNumber());
		eventContact.setComunicationEmail(user.getCommunicationEmail());
		if (user.getBuyer() != null) {
			eventContact.setMobileNumber(user.getBuyer().getMobileNumber());
			eventContact.setFaxNumber(user.getBuyer().getFaxNumber());
		}
		RfaEvent rfaEvent = new RfaEvent();
		rfaEvent.setId(eventId);
		eventContact.setRfaEvent(rfaEvent);
		saveRfaEventContact(eventContact);
	}

	@Override
	public void sendTeamMemberEmailNotificationEmail(User user, TeamMemberType memberType, User cretedBy, String eventName, String eventId, String referanceNumber, RfxTypes eventType, String id) {

		try {
			String subject = "You have been Invited as TEAM MEMBER in Event";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();

			LOG.info("===========sending Email=================");
			LOG.info("=====userName======" + user.getName());
			LOG.info("=====memberType======" + memberType.getValue());
			LOG.info("===========" + eventName);
			LOG.info("===========" + cretedBy.getName());
			LOG.info("===========" + eventId);
			LOG.info("===========" + referanceNumber);

			map.put("userName", user.getName());
			map.put("memberType", memberType);

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the Event but not finish the Event");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the Event without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the Event Owner.");

			map.put("eventName", eventName);
			map.put("eventType", eventType.toString());
			map.put("createdBy", cretedBy.getName());
			map.put("eventId", eventId);
			map.put("eventRefNum", referanceNumber);

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);

			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.TEAM_MEMBER_TEMPLATE), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			url = APP_URL + "/buyer/" + eventType.name() + "/createEventDetails/" + id;
			String notificationMessage = messageSource.getMessage("team.event.add", new Object[] { memberType, eventName }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);

		} catch (Exception e) {
			LOG.error("error in sending team member email " + e.getMessage(), e);
		}

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
	public RfaEvent findRfaEventByErpAwardRefNoAndTenantId(String erpAwardRefId, String tenantId) {
		return rfaEventDao.findRfaEventByErpAwardRefNoAndTenantId(erpAwardRefId, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public String createPrFromAward(RfaEventAward rfaEventAward, String templateId, String userId, User loggedInUser) throws ApplicationException {
		User createdBy = userDao.findById(userId);
		List<String> supplierList = new ArrayList<String>();
		// List<ProductCategory> ctegoryList =
		// productCategoryMaintenanceService.getAllProductCategoryByTenantId(loggedInUser.getTenantId());
		// Supplier -> List of Sections -> List of Items
		Map<String, Map<String, List<PrItem>>> data = new HashMap<>();
		String value = "";
		try {
			LOG.info("rfaEventAward.getRfxAwardDetails()--------" + rfaEventAward.getRfxAwardDetails().size());
			if (CollectionUtil.isNotEmpty(rfaEventAward.getRfxAwardDetails())) {

				BqItem section = null;

				for (RfaEventAwardDetails rfxAward : rfaEventAward.getRfxAwardDetails()) {
					LOG.info("order--------" + rfxAward.getBqItem().getLevel() + "--" + rfxAward.getBqItem().getOrder());
					LOG.info("---------------bqitemId--------------------" + rfxAward.getBqItem().getBqId());

					BqItem dbBqItem = rfaBqItemDao.getBqItemByBqIdAndBqItemId(rfxAward.getBqItem().getId());
					if (dbBqItem.getOrder() == 0) {
						section = dbBqItem;
					} else {
						String sid = rfxAward.getSupplier().getId();
						RfaSupplierBqItem supplierBqItem = rfaSupplierBqItemDao.getSupplierBqItemByBqItemId(rfxAward.getBqItem().getId(), sid);
						LOG.info("rfaEventAward.getRfxAwardDetails() new--------------" + rfxAward.getSupplier().getId());
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

							itemList.add(item);
						}
						PrItem item = new PrItem();
						ProductItem productItem = productCategoryMaintenanceService.checkProductItemExistOrNot(dbBqItem.getItemName(), sid, loggedInUser.getTenantId());

						if (productItem == null) {

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
							LOG.info("---------------bqitemId--------------------");
							/*
							 * productItem = new ProductItem(); productItem.setProductName(dbBqItem.getItemName() );
							 * productItem.setProductCode(productItem. getProductName()); if (dbBqItem.getUom() != null)
							 * { productItem.setUom(dbBqItem.getUom()); }
							 * productItem.setProductCategory(ctegoryList.get(0) );
							 * productItem.setTax(rfxAward.getTax()); productItem.setCreatedBy(loggedInUser);
							 * productItem.setCreatedDate(new Date()); BigDecimal ap = rfxAward.getAwardedPrice();
							 * productItem.setUnitPrice(ap.divide(new BigDecimal(supplierBqItem.getQuantity()), 6,
							 * BigDecimal.ROUND_HALF_UP)); productItem.setBuyer(loggedInUser.getBuyer());
							 * FavouriteSupplier favouriteSupplier = favoriteSupplierDao.
							 * getFavouriteSupplierBySupplierId(sid, loggedInUser.getTenantId());
							 * productItem.setFavoriteSupplier(favouriteSupplier ); productCategoryMaintenanceService.
							 * saveNewProductItem(productItem);
							 */
						} else {
							LOG.info("---------------bqitemId---============>--------------------" + productItem.getUom().getUom());
							item.setProduct(productItem);
							item.setProductCategory(productItem.getProductCategory());
							item.setItemName(dbBqItem.getItemName());
							item.setItemDescription(dbBqItem.getItemDescription());
							item.setUnit(productItem.getUom());
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
						LOG.info("---------------bqitemId---============>--" + item.getUnit().getUom());
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
					RfaEvent rfxEvent = rfaEventService.getRfaEventById(rfaEventAward.getRfxEvent().getId());
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
					 * item.getTotalAmount().setScale(Integer.parseInt(pr. getDecimal()),
					 * BigDecimal.ROUND_HALF_UP).add(item.getTaxAmount(). setScale(Integer.parseInt(pr.getDecimal()),
					 * BigDecimal.ROUND_HALF_UP)) : new BigDecimal(0)); } catch (Exception e) { LOG.error("Error : " +
					 * e.getMessage(), e); throw new NotAllowedException(messageSource.getMessage(
					 * "common.number.format.error", new Object[] {}, Global.LOCALE)); } item =
					 * prService.savePrItemBare(item); pr.setTotal(pr.getTotal() != null && item.getTotalAmountWithTax()
					 * != null ? pr.getTotal().add(item.getTotalAmountWithTax()) : new BigDecimal(0));
					 * pr.setGrandTotal(pr.getTotal() != null && pr.getAdditionalTax() != null ?
					 * pr.getTotal().add(pr.getAdditionalTax()) : new BigDecimal(0)); prService.updatePr(pr); } order++;
					 * } level++; } }
					 */
				}
			}
		} catch (

		Exception e) {
			LOG.error("Error generating Auto PRs during Event Award : " + e.getMessage(), e);
			throw new ApplicationException(e.getMessage(), e);
		}
		return value;
		//
		// LOG.info("supplierList after--------------------" +
		// supplierList.size());
		// for (String supplierId : supplierList) {
		// LOG.info("Supplier ID--------" + supplierId);
		// LOG.info("Tenant id---------" + loggedInUser.getTenantId());
		// FavouriteSupplier favouriteSupplier =
		// favoriteSupplierDao.getFavouriteSupplierBySupplierId(supplierId,
		// loggedInUser.getTenantId());
		// Pr pr = prService.copyFromTemplateWithAward(templateId, createdBy,
		// loggedInUser, loggedInUser.getTenantId(),
		// null, favouriteSupplier);
		// for (RftEventAwardDetails rfxAward :
		// rftEventAward.getRfxAwardDetails()) {
		// LOG.info("UOM----" + rfxAward.getBqItem().getUom() + "----" +
		// rfxAward.getAwardedPrice());
		// setProductData(rfxAward, pr, loggedInUser, supplierId);
		// }
		// }

	}

	@Override
	@Transactional(readOnly = false)
	public String createContractFromAward(RfaEventAward rfaEventAward, String eventId, String contractStartDate, String contractEndDate, String groupCodeHid, String referenceNumberHid, User loggedInUser, HttpSession session, String contractCreator) throws ApplicationException {
		List<String> supplierList = new ArrayList<String>();

		Map<String, List<ProductContractItems>> map = new HashMap<>();
		String value = "";
		try {
			RfaEvent event = rfaEventDao.findByEventId(eventId);
			LOG.info("rfaEventAward.getRfxAwardDetails()--------" + rfaEventAward.getRfxAwardDetails().size());
			if (CollectionUtil.isNotEmpty(rfaEventAward.getRfxAwardDetails())) {

				for (RfaEventAwardDetails rfxAward : rfaEventAward.getRfxAwardDetails()) {
					LOG.info("order--------" + rfxAward.getBqItem().getLevel() + "--" + rfxAward.getBqItem().getOrder());
					LOG.info("---------------bqitemId--------------------" + rfxAward.getBqItem().getBqId());

					BqItem dbBqItem = rfaBqItemDao.getBqItemByBqIdAndBqItemId(rfxAward.getBqItem().getId());
					if (dbBqItem.getOrder() != 0) {
						String sid = rfxAward.getSupplier().getId();
						RfaSupplierBqItem supplierBqItem = rfaSupplierBqItemDao.getSupplierBqItemByBqItemId(rfxAward.getBqItem().getId(), sid);
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

					// update contract set contractCreator = createdBy where contractCreator is nullz`z`
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
	public JasperPrint getBiddingReportEnglish(String eventId, String strTimeZone, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;

		TimeZone timeZone = TimeZone.getDefault();
		// String strTimeZone = (String)
		// session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		/*
		 * SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a"); sdf.setTimeZone(timeZone);
		 */

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		sdf.setTimeZone(timeZone);

		Map<String, Object> parameters = new HashMap<String, Object>();

		// Virtualizar - To increase the performance
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		generatedSdf.setTimeZone(timeZone);
		parameters.put("generatedOn", generatedSdf.format(new Date()));

		try {
			Resource resource = applicationContext.getResource("classpath:reports/BiddingReportEnglishBook.jasper");
			// String imgPath = context.getRealPath("resources/images");
			File jasperfile = resource.getFile();

			List<EvaluationAuctionBiddingPojo> auctionSummary = buildBiddingReportEnglishData(eventId, sdf, parameters);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auctionSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Bidding English PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationAuctionBiddingPojo> buildBiddingReportEnglishData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			RfaEvent event = rfaEventDao.findByEventId(eventId);
			List<RfaEventSupplier> supplierList = rfaEventSupplierDao.getAllRfaEventSuppliersByEventId(eventId);
			if (event != null) {

				boolean isMasked = false;
				/*
				 * if (event.getViewSupplerName() != null && !event.getViewSupplerName() && (event.getStatus() !=
				 * EventStatus.COMPLETE && event.getStatus() != EventStatus.FINISHED)) { for (EventSupplier
				 * eventSupplier : supplierList) { eventSupplier.setSupplierCompanyName(MaskUtils.maskName("Supplier",
				 * eventSupplier.getSupplier().getId(), eventId)); } isMasked = true; }
				 */

				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				auction.setIsMask(isMasked);
				buildSupplierCountData(supplierList, auction);
				List<RfaEnvelop> envelop = rfaEnvelopService.getAllRfaEnvelopByEventId(eventId);
				RfaSupplierBqPojo leadSupplier = buildHeadingReportForBidding(event, supplierList, auction, sdf, auctionRules, isMasked, envelop.get(0));
				auction.setEnvelopTitle(getCommaEnvelope(envelop));
				buildEventDetailData(sdf, event, auction);
				buildSupplierLineChartAndDataForBidding(sdf, event, supplierList, auction, auctionRules, isMasked, envelop.get(0));
				buildSupplierLineAndBidHistoryChartData(sdf, event, auction, isMasked, envelop.get(0));
				buildBidHistoryData(sdf, event, auction, leadSupplier, isMasked, envelop.get(0));
				List<EvaluationBidHistoryPojo> bidHistoryDetails = new ArrayList<EvaluationBidHistoryPojo>();
				List<AuctionBids> bidHistory = auctionBidsDao.getAuctionBidsListByEventIdForReport(event.getId());
				if (CollectionUtil.isNotEmpty(bidHistory)) {
					int i = 1;
					for (AuctionBids item : bidHistory) {
						EvaluationBidHistoryPojo bd = new EvaluationBidHistoryPojo();
						bd.setBidPrice(item.getAmount());
						bd.setCompanyName(isMasked ? "Supplier" + i : item.getBidBySupplier().getCompanyName());
						bd.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
						bd.setDateTime(item.getBidSubmissionDate() != null ? sdf.format(item.getBidSubmissionDate()) : "");
						bd.setDecimal(event.getDecimal());
						bidHistoryDetails.add(bd);
						i++;
					}
				}
				buildReverttoLastBidData(sdf, event, auction, supplierList);
				auction.setBidHistory(bidHistoryDetails);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);
		} catch (Exception e) {
			LOG.error("error while build Bidding Report EnglishData :" + e.getMessage(), e);
		}
		return auctionSummary;
	}

	private String getCommaEnvelope(List<RfaEnvelop> envelop) {
		String envelopTitle = "";
		int j = 0;
		for (RfaEnvelop rfaEnvelop : envelop) {
			if (j != 0) {
				envelopTitle += ", " + rfaEnvelop.getEnvelopTitle();
			} else {
				envelopTitle = rfaEnvelop.getEnvelopTitle();
			}
			j++;
		}
		return envelopTitle;
	}

	private RfaSupplierBqPojo buildHeadingReport(RfaEvent event, List<RfaEventSupplier> supplierList, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, AuctionRules auctionRules, boolean isMasked, RfaEnvelop envelop, boolean isEvaluation) {
		RfaSupplierBqPojo leadSupplier = null;
		try {
			List<EvaluationAuctionBiddingPojo> headerBqList = new ArrayList<EvaluationAuctionBiddingPojo>();
			// List<RfaEventBq> bq = rfaBqDao.findBqsByEventId(event.getId());

			List<RfaEventBq> bq = rfaEventBqDao.findBqbyEventIdAndEnvelopId(event.getId(), envelop.getId());

			EvaluationAuctionBiddingPojo headerPojo = new EvaluationAuctionBiddingPojo();
			if (CollectionUtil.isNotEmpty(bq)) {
				for (RfaEventBq rfaEventBq : bq) {
					headerPojo = new EvaluationAuctionBiddingPojo();
					headerPojo.setHeaderBqName(rfaEventBq.getName());
					List<RfaSupplierBqPojo> rfaEventSupplierIds = rfaSupplierBqDao.getAllRfaTopEventSuppliersIdByEventId(event.getId(), 5, rfaEventBq.getId());
					int i = 0;

					RfaSupplierBqPojo secondLeadSupplier = null;
					for (RfaSupplierBqPojo rfaSupplierBqPojo : rfaEventSupplierIds) {
						if (i == 0) {
							leadSupplier = rfaSupplierBqPojo;
						} else if (i == 1) {
							secondLeadSupplier = rfaSupplierBqPojo;
							break;
						}
						i++;

					}
					headerPojo.setDecimal(event.getDecimal());
					headerPojo.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "N/A");
					buildLeadSupplierData(event, headerPojo, leadSupplier, auction, auctionRules, isMasked, envelop);
					buildTopCompletedBarChartData(event, headerPojo, isMasked, envelop);
					buildTopSupplierLineChartData(sdf, event, headerPojo, rfaEventSupplierIds, isMasked, envelop);
					buildDisqualificationSuppliersData(event.getId(), headerPojo, auction, isMasked, envelop, sdf, isEvaluation);
					buildMatchingIpAddressData(event.getId(), headerPojo, supplierList);
					buildLeadingSecondComparison(auction, leadSupplier, secondLeadSupplier, event.getDecimal(), event, isMasked, envelop);
					headerBqList.add(headerPojo);
				}
			} else {
				headerPojo.setHeaderBqName("");
				buildDisqualificationSuppliersData(event.getId(), headerPojo, auction, isMasked, envelop, sdf, false);
				buildMatchingIpAddressData(event.getId(), headerPojo, supplierList);

				headerBqList.add(headerPojo);
			}
			auction.setHeader(headerBqList);
		} catch (Exception e) {
			LOG.error("error while build Heading Report :" + e.getMessage(), e);
		}
		return leadSupplier;
	}

	private RfaSupplierBqPojo buildHeadingReportForBidding(RfaEvent event, List<RfaEventSupplier> supplierList, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, AuctionRules auctionRules, boolean isMasked, RfaEnvelop envelop) {
		RfaSupplierBqPojo leadSupplier = null;
		try {
			List<EvaluationAuctionBiddingPojo> headerBqList = new ArrayList<EvaluationAuctionBiddingPojo>();
			List<RfaEventBq> bq = rfaBqDao.findBqsByEventId(event.getId());

			// List<RfaEventBq> bq = rfaEventBqDao.findBqbyEventIdAndEnvelopId(event.getId(), envelop.getId());

			EvaluationAuctionBiddingPojo headerPojo = new EvaluationAuctionBiddingPojo();
			if (CollectionUtil.isNotEmpty(bq)) {
				for (RfaEventBq rfaEventBq : bq) {
					headerPojo = new EvaluationAuctionBiddingPojo();
					headerPojo.setHeaderBqName(rfaEventBq.getName());
					List<RfaSupplierBqPojo> rfaEventSupplierIds = rfaSupplierBqDao.getAllRfaTopEventSuppliersIdByEventId(event.getId(), 5, rfaEventBq.getId());
					int i = 0;

					RfaSupplierBqPojo secondLeadSupplier = null;
					for (RfaSupplierBqPojo rfaSupplierBqPojo : rfaEventSupplierIds) {
						if (i == 0) {
							leadSupplier = rfaSupplierBqPojo;
						} else if (i == 1) {
							secondLeadSupplier = rfaSupplierBqPojo;
							break;
						}
						i++;

					}
					headerPojo.setDecimal(event.getDecimal());
					headerPojo.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "N/A");
					buildLeadSupplierData(event, headerPojo, leadSupplier, auction, auctionRules, isMasked, envelop);
					buildTopCompletedBarChartData(event, headerPojo, isMasked, envelop);
					buildTopSupplierLineChartData(sdf, event, headerPojo, rfaEventSupplierIds, isMasked, envelop);
					buildDisqualificationSuppliersData(event.getId(), headerPojo, auction, isMasked, envelop, sdf, false);
					buildMatchingIpAddressData(event.getId(), headerPojo, supplierList);
					buildLeadingSecondComparison(auction, leadSupplier, secondLeadSupplier, event.getDecimal(), event, isMasked, envelop);
					headerBqList.add(headerPojo);
				}
			} else {
				headerPojo.setHeaderBqName("");
				buildDisqualificationSuppliersData(event.getId(), headerPojo, auction, isMasked, envelop, sdf, false);
				buildMatchingIpAddressData(event.getId(), headerPojo, supplierList);

				headerBqList.add(headerPojo);
			}
			auction.setHeader(headerBqList);
		} catch (Exception e) {
			LOG.error("error while build Heading Report :" + e.getMessage(), e);
		}
		return leadSupplier;
	}

	private void buildLeadingSecondComparison(EvaluationAuctionBiddingPojo auction, RfaSupplierBqPojo leadSupplier, RfaSupplierBqPojo secondLeadSupplier, String decimal, RfaEvent event, boolean isMasked, RfaEnvelop envelop) {
		try {
			if (leadSupplier != null && secondLeadSupplier != null) {

				BigDecimal leadDiffSecondComparison = BigDecimal.ZERO;
				BigDecimal leadSecondComparison = BigDecimal.ZERO;
				BigDecimal percentage = BigDecimal.ZERO;

				if ((leadSupplier.getRevisedGrandTotal() != null) && leadSupplier.getRevisedGrandTotal().compareTo(secondLeadSupplier.getRevisedGrandTotal()) < 0) {
					leadDiffSecondComparison = (secondLeadSupplier.getRevisedGrandTotal() != null ? secondLeadSupplier.getRevisedGrandTotal() : secondLeadSupplier.getInitialPrice()).subtract(leadSupplier.getRevisedGrandTotal() != null ? leadSupplier.getRevisedGrandTotal() : leadSupplier.getInitialPrice());
					// PH-232. leadSecondComparison = leadSupplier.getRevisedGrandTotal() != null ?
					// leadSupplier.getRevisedGrandTotal() : leadSupplier.getInitialPrice();

				} else {
					leadDiffSecondComparison = (leadSupplier.getRevisedGrandTotal() != null ? leadSupplier.getRevisedGrandTotal() : leadSupplier.getInitialPrice()).subtract(secondLeadSupplier.getRevisedGrandTotal() != null ? secondLeadSupplier.getRevisedGrandTotal() : secondLeadSupplier.getInitialPrice());
					// PH-232 leadSecondComparison = secondLeadSupplier.getRevisedGrandTotal() != null ?
					// secondLeadSupplier.getRevisedGrandTotal() : secondLeadSupplier.getInitialPrice();
				}

				leadSecondComparison = secondLeadSupplier.getRevisedGrandTotal() != null ? secondLeadSupplier.getRevisedGrandTotal() : secondLeadSupplier.getInitialPrice();

				percentage = (leadDiffSecondComparison.setScale(Integer.parseInt(decimal), RoundingMode.HALF_UP).multiply(new BigDecimal(100))).divide(leadSecondComparison, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
				LOG.info("leadDiffSecondComparison" + leadDiffSecondComparison);
				LOG.info("leadSecondComparison" + leadSecondComparison);
				LOG.info("percentage" + percentage);

				auction.setLeadingSecondComparison("The price difference between the leading bid ( " + (isMasked ? MaskUtils.maskName(envelop.getPreFix(), leadSupplier.getSupplierId(), envelop.getId()) : leadSupplier.getSupplierCompanyName()) + " )  and the second-leading bid ( " + (isMasked ? MaskUtils.maskName(envelop.getPreFix(), secondLeadSupplier.getSupplierId(), envelop.getId()) : secondLeadSupplier.getSupplierCompanyName()) + " ) is " + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + leadDiffSecondComparison + " or " + percentage + " %");
				LOG.info("LeadingSecondComparison" + auction.getLeadingSecondComparison());
			} else {
				auction.setLeadingSecondComparison("N/A");
			}
		} catch (NumberFormatException e) {
			LOG.error("error while build Leading Second Comparison :" + e.getMessage(), e);
		}
	}

	private void buildMatchingIpAddressData(String eventId, EvaluationAuctionBiddingPojo auction, List<RfaEventSupplier> participatedSupplier) {
		try {
			List<EvaluationBiddingIpAddressPojo> ipAddressList = new ArrayList<EvaluationBiddingIpAddressPojo>();
			Map<String, String> hm = new HashMap<String, String>();
			int i = 0;
			if (CollectionUtil.isNotEmpty(participatedSupplier)) {
				for (RfaEventSupplier rfaSupplier : participatedSupplier) {
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
							for (EventSupplier rfaSupplierIp : participatedSupplier) {
								if (StringUtils.checkString(rfaSupplierIp.getIpAddress()).length() > 0) {
									if (rfaSupplierIp.getIpAddress().equals(rfaSupplier.getIpAddress())) {
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
						e.setIpAddress(StringUtils.checkString(ipAddressList.get(j).getIpAddress()).length() > 0 ? ipAddressList.get(j).getIpAddress() : "N/A");
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
			LOG.error("error while build Matching IpAddress Data :" + e.getMessage(), e);
		}

	}

	private void buildTopSupplierLineChartData(SimpleDateFormat sdf, RfaEvent event, EvaluationAuctionBiddingPojo auction, List<RfaSupplierBqPojo> topSupplierList, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<EvaluationSupplierBidsPojo> supplierBidHistory = new ArrayList<EvaluationSupplierBidsPojo>();
			List<EvaluationBiddingPricePojo> evaluationTopSupplierBidsPojoList = new ArrayList<EvaluationBiddingPricePojo>();
			if (CollectionUtil.isNotEmpty(topSupplierList)) {
				for (RfaSupplierBqPojo suppItem : topSupplierList) {
					bidderTopPriceHistory(event, supplierBidHistory, suppItem, sdf, isMasked, envelop);
				}
			}
			for (EvaluationSupplierBidsPojo evaluationTopSupplierBidsPojo : supplierBidHistory) {
				for (EvaluationBiddingPricePojo evaluationBiddingPricePojo : evaluationTopSupplierBidsPojo.getPriceSubmissionList()) {
					evaluationTopSupplierBidsPojoList.add(evaluationBiddingPricePojo);
				}
			}
			if (CollectionUtil.isNotEmpty(evaluationTopSupplierBidsPojoList)) {
				evaluationTopSupplierBidsPojoList = getSortedEvaluationTopSupplierBids(evaluationTopSupplierBidsPojoList);
			}
			auction.setEvaluationTopSupplierBidsPojoList(evaluationTopSupplierBidsPojoList);
			auction.setSupplierTopBidsList(supplierBidHistory);
		} catch (Exception e) {
			LOG.error("error while build Top Supplier Line Chart Data :" + e.getMessage(), e);
		}
	}

	private void bidderTopPriceHistory(RfaEvent event, List<EvaluationSupplierBidsPojo> supplierBidHistory, RfaSupplierBqPojo suppItem, SimpleDateFormat sdf, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<RfaSupplierBq> bqList = rfaSupplierBqDao.findSupplierBqbyEventId(event.getId());
			String bqId = null;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RfaSupplierBq rfaSupplierEventBq : bqList) {
					bqId = rfaSupplierEventBq.getBq().getId();
				}
			}
			RfaSupplierBq supBq = rfaSupplierBqDao.findBqByBqId(bqId, suppItem.getSupplierId());

			if (supBq != null) {
				EvaluationSupplierBidsPojo bidderPriceHistory = new EvaluationSupplierBidsPojo();
				bidderPriceHistory.setSupplierName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), supBq.getSupplier().getId(), envelop.getId()) : (supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : ""));

				bidderPriceHistory.setBqDescription(supBq.getName());
				bidderPriceHistory.setInitialPrice(supBq.getInitialPrice());
				bidderPriceHistory.setDecimals(event.getDecimal());
				bidderPriceHistory.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
				List<EvaluationBiddingPricePojo> BidderPriceList = new ArrayList<EvaluationBiddingPricePojo>();
				List<AuctionBids> supplierBidsList = auctionBidsDao.getAuctionBidsForSupplierForReport(suppItem.getSupplierId(), event.getId());

				if (CollectionUtil.isNotEmpty(supplierBidsList)) {
					BigDecimal reductionPrice = BigDecimal.ZERO, temp = BigDecimal.ZERO, percentage = BigDecimal.ZERO;
					temp = supBq.getInitialPrice();
					int bidNumber = 1;
					for (AuctionBids suppBids : supplierBidsList) {

						reductionPrice = BigDecimal.ZERO;
						percentage = BigDecimal.ZERO;
						EvaluationBiddingPricePojo bidderPrice = new EvaluationBiddingPricePojo();

						if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
							reductionPrice = temp.subtract(suppBids.getAmount());
						}
						if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
							reductionPrice = suppBids.getAmount().subtract(temp);
						}

						percentage = reductionPrice.multiply(new BigDecimal(100)).divide(temp, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);

						bidderPrice.setBidNumber(bidNumber);
						bidderPrice.setDecimal(event.getDecimal());
						bidderPrice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");

						bidderPrice.setDisplayValue(coolFormat(suppBids.getAmount().doubleValue(), 0));
						bidderPrice.setPriceSubmission(suppBids.getAmount());
						bidderPrice.setPriceReduction(reductionPrice);
						bidderPrice.setPercentage(percentage);
						bidderPrice.setSubmitedDate(suppBids.getBidSubmissionDate() != null ? sdf.format(suppBids.getBidSubmissionDate()) : "");
						bidderPrice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), supBq.getSupplier().getId(), envelop.getId()) : (supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : ""));
						BidderPriceList.add(bidderPrice);
						bidNumber += 1;
					}
				}
				bidderPriceHistory.setPriceSubmissionList(BidderPriceList);

				supplierBidHistory.add(bidderPriceHistory);
			}
		} catch (Exception e) {
			LOG.error("Could not get Bidder Price submission History Details :" + e.getMessage(), e);
		}

	}

	private void buildSupplierLineChartAndDataForBidding(SimpleDateFormat sdf, RfaEvent event, List<RfaEventSupplier> participatedSupplier, EvaluationAuctionBiddingPojo auction, AuctionRules auctionRules, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<EvaluationSupplierBidsPojo> supplierBidHistory = new ArrayList<EvaluationSupplierBidsPojo>();
			List<EvaluationBidderContactPojo> bidderContactList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderAcceptedList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderRejectedList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderInvidedList = new ArrayList<EvaluationBidderContactPojo>();

			List<EvaluationBidderContactPojo> supplierActivitySummaryList = new ArrayList<EvaluationBidderContactPojo>();
			if (CollectionUtil.isNotEmpty(participatedSupplier)) {
				for (RfaEventSupplier eventSupp : participatedSupplier) {

					if (!isMasked) {
						buildSupplierContactListData(bidderContactList, eventSupp);
					} else {
						eventSupp.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId()));
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
					if (SubmissionStatusType.COMPLETED == eventSupp.getSubmissionStatus()) {
						bidderPriceSubmissionHistoryAndBqItemData(event, supplierBidHistory, eventSupp, sdf, auction, auctionRules, isMasked, envelop);
					}
				}
			}

			List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaSupplierBqCompleteNessBySupplierIds(event.getId());
			for (RfaSupplierBqPojo eventSupp : eventBq) {
				if (isMasked) {
					eventSupp.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplierId(), envelop.getId()));
				}
				buildSupplierActivitySummaryData(event, eventSupp, supplierActivitySummaryList, sdf);
			}

			buildSupplierTotallyCompleteBidsDataForBidding(auction, event, isMasked, envelop);
			buildSupplierPartiallyCompleteBidsDataForBidding(auction, event, isMasked, envelop);
			buildNetSaving(auction, event);
			buildSupplierDisqualifiedCompleteBidsData(auction, event, isMasked, envelop);
			auction.setSupplierBidsList(supplierBidHistory);
			auction.setBidContacts(bidderContactList);
			auction.setSupplierActivitySummary(supplierActivitySummaryList);
			auction.setSupplierAcceptedBids(bidderAcceptedList);
			auction.setSupplierRejectedBids(bidderRejectedList);
			auction.setSupplierInvitedBids(bidderInvidedList);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier LineChart And Data :" + e.getMessage(), e);
		}

	}

	private void buildSupplierLineChartAndData(SimpleDateFormat sdf, RfaEvent event, List<RfaEventSupplier> participatedSupplier, EvaluationAuctionBiddingPojo auction, AuctionRules auctionRules, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<EvaluationSupplierBidsPojo> supplierBidHistory = new ArrayList<EvaluationSupplierBidsPojo>();
			List<EvaluationBidderContactPojo> bidderContactList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderAcceptedList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderRejectedList = new ArrayList<EvaluationBidderContactPojo>();
			List<EvaluationBidderContactPojo> bidderInvidedList = new ArrayList<EvaluationBidderContactPojo>();

			List<EvaluationBidderContactPojo> supplierActivitySummaryList = new ArrayList<EvaluationBidderContactPojo>();
			if (CollectionUtil.isNotEmpty(participatedSupplier)) {
				for (RfaEventSupplier eventSupp : participatedSupplier) {

					if (!isMasked) {
						buildSupplierContactListData(bidderContactList, eventSupp);
					} else {
						eventSupp.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId()));
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
					bidderPriceSubmissionHistoryAndBqItemData(event, supplierBidHistory, eventSupp, sdf, auction, auctionRules, isMasked, envelop);
				}
			}

			List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaSupplierBqCompleteNessBySupplierIds(event.getId());
			for (RfaSupplierBqPojo eventSupp : eventBq) {
				if (isMasked) {
					eventSupp.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplierId(), envelop.getId()));
				}
				if (SubmissionStatusType.COMPLETED == eventSupp.getSubmissionStatus()) {
					buildSupplierActivitySummaryData(event, eventSupp, supplierActivitySummaryList, sdf);
				}
			}

			buildSupplierTotallyCompleteBidsData(auction, event, isMasked, envelop);
			buildSupplierPartiallyCompleteBidsData(auction, event, isMasked, envelop);
			buildNetSaving(auction, event);
			buildSupplierDisqualifiedCompleteBidsData(auction, event, isMasked, envelop);
			auction.setSupplierBidsList(supplierBidHistory);
			auction.setBidContacts(bidderContactList);
			auction.setSupplierActivitySummary(supplierActivitySummaryList);
			auction.setSupplierAcceptedBids(bidderAcceptedList);
			auction.setSupplierRejectedBids(bidderRejectedList);
			auction.setSupplierInvitedBids(bidderInvidedList);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier LineChart And Data :" + e.getMessage(), e);
		}

	}

	private void buildNetSaving(EvaluationAuctionBiddingPojo auction, RfaEvent event) {
		try {
			List<EvaluationBiddingPricePojo> netSavingList = new ArrayList<EvaluationBiddingPricePojo>();
			BigDecimal saving = BigDecimal.ZERO, percentage = BigDecimal.ZERO;
			BigDecimal postPrice = BigDecimal.ZERO, initailPrice = BigDecimal.ZERO;
			initailPrice = rfaSupplierBqDao.findInitialMinPrice(event.getId(), event.getAuctionType());
			postPrice = rfaSupplierBqDao.findPostMinPrice(event.getId(), event.getAuctionType());
			EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
			if (initailPrice != null && postPrice != null) {
				if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					saving = initailPrice.subtract(postPrice);
					percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide((initailPrice), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
				}
				if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
					saving = postPrice.subtract(initailPrice);
					percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide((initailPrice), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
				}
			}
			suppBidprice.setPreAuctionPrice(initailPrice);
			suppBidprice.setPostAuctionprice(postPrice);
			suppBidprice.setPercentage(percentage);
			suppBidprice.setSaving(saving);
			netSavingList.add(suppBidprice);
			auction.setNetSavingList(netSavingList);
		} catch (Exception e) {
			LOG.error("Could not get build Net Saving :" + e.getMessage(), e);
		}

	}

	private void buildSupplierDisqualifiedCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfaEvent event, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaSupplierBqByDisqualifiedSupplierIdsOdrByRank(event.getId(), null);
			if (CollectionUtil.isNotEmpty(eventBq)) {
				for (RfaSupplierBqPojo item : eventBq) {
					EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
					BigDecimal saving = BigDecimal.ZERO, percentage = BigDecimal.ZERO;

					if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
						saving = item.getInitialPrice().subtract(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
						percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
						suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
					}

					if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
						saving = (item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice()).subtract(item.getInitialPrice());
						percentage = (saving.multiply(new BigDecimal(100)).divide(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
						suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
					}

					suppBidprice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId()) : item.getSupplierCompanyName());

					suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
					suppBidprice.setDecimal(event.getDecimal());
					suppBidprice.setPreAuctionPrice(item.getInitialPrice());
					if (item.getInitialPrice() != null) {
						suppBidprice.setPreAuctionStrPrice(coolFormat(item.getInitialPrice().doubleValue(), 0));
					}

					if (item.getRevisedGrandTotal() != null) {
						suppBidprice.setPostAuctionStrPrice(coolFormat(item.getRevisedGrandTotal().doubleValue(), 0));
					}

					suppBidprice.setDisqualifyRemarks(item.getDisqualifyRemarks());
					suppBidprice.setDisqualifyBy(item.getName() != null ? item.getName() : "");
					suppBidprice.setDisqualifiedTime(item.getDisqualifiedTime());
					suppBidprice.setSaving(saving);
					suppBidprice.setPercentage(percentage);
					suppBidprice.setAuctionType(event.getAuctionType().getValue());
					suppBidprice.setCompleAndTotalItem(item.getCompleteness() + " / " + item.getTotalItem());
					bidPriceList.add(suppBidprice);
				}
			}
			auction.setBidderDisqualifiedCompleteBidsList(bidPriceList);
		} catch (NumberFormatException e) {
			LOG.error("Could not get build Supplier Disqualified Complete Bids Data :" + e.getMessage(), e);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Disqualified Complete Bids Data :" + e.getMessage(), e);
		}

	}

	private void buildSupplierPartiallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfaEvent event, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);
			List<String> bqids = rfaEnvelopDao.getBqIdlistByEnvelopId(envelop.getId());

			if (CollectionUtil.isNotEmpty(bqids)) {
				if (CollectionUtil.isNotEmpty(eventBq)) {
					for (RfaSupplierBqPojo item : eventBq) {
						if (item.getCompleteness() != item.getTotalItem()) {
							EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
							BigDecimal saving = BigDecimal.ZERO, percentage = BigDecimal.ZERO;

							if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
								saving = item.getInitialPrice().subtract(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
								percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
								suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
							}

							if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
								saving = (item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice()).subtract(item.getInitialPrice());
								percentage = (saving.multiply(new BigDecimal(100)).divide(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
								suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
							}

							suppBidprice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId()) : item.getSupplierCompanyName());
							suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
							suppBidprice.setDecimal(event.getDecimal());
							suppBidprice.setPreAuctionPrice(item.getInitialPrice());
							if (item.getInitialPrice() != null) {
								suppBidprice.setPreAuctionStrPrice(coolFormat(item.getInitialPrice().doubleValue(), 0));
							}

							if (item.getRevisedGrandTotal() != null) {
								suppBidprice.setPostAuctionStrPrice(coolFormat(item.getRevisedGrandTotal().doubleValue(), 0));
							}

							// Ip Address
							suppBidprice.setIpAddress(item.getIpAddress());
							suppBidprice.setSaving(saving);
							LOG.info("Parcent : " + percentage);
							suppBidprice.setPercentage(percentage);
							suppBidprice.setAuctionType(event.getAuctionType().getValue());

							String completeness = new Long(item.getCompleteness()).toString();
							String totalItem = new Long(item.getTotalItem()).toString();

							suppBidprice.setCompleAndTotalItem(completeness + " / " + totalItem);

							bidPriceList.add(suppBidprice);

						}
					}
				}
				auction.setBidderPartiallyCompleteBidsList(bidPriceList);
			}
		} catch (NumberFormatException e) {
			LOG.error("Could not get build Supplier Partially Complete Bids Data :" + e.getMessage(), e);
		}

	}

	private void buildSupplierPartiallyCompleteBidsDataForBidding(EvaluationAuctionBiddingPojo auction, RfaEvent event, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);

			if (CollectionUtil.isNotEmpty(eventBq)) {
				for (RfaSupplierBqPojo item : eventBq) {
					if (item.getCompleteness() != item.getTotalItem()) {
						EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
						BigDecimal saving = BigDecimal.ZERO, percentage = BigDecimal.ZERO;

						if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
							saving = item.getInitialPrice().subtract(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
							percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
							suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
						}

						if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
							saving = (item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice()).subtract(item.getInitialPrice());
							percentage = (saving.multiply(new BigDecimal(100)).divide(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
							suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
						}

						suppBidprice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId()) : item.getSupplierCompanyName());
						suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
						suppBidprice.setDecimal(event.getDecimal());
						suppBidprice.setPreAuctionPrice(item.getInitialPrice());
						if (item.getInitialPrice() != null) {
							suppBidprice.setPreAuctionStrPrice(coolFormat(item.getInitialPrice().doubleValue(), 0));
						}

						if (item.getRevisedGrandTotal() != null) {
							suppBidprice.setPostAuctionStrPrice(coolFormat(item.getRevisedGrandTotal().doubleValue(), 0));
						}

						// Ip Address
						suppBidprice.setIpAddress(item.getIpAddress());
						suppBidprice.setSaving(saving);
						LOG.info("Parcent : " + percentage);
						suppBidprice.setPercentage(percentage);
						suppBidprice.setAuctionType(event.getAuctionType().getValue());

						String completeness = new Long(item.getCompleteness()).toString();
						String totalItem = new Long(item.getTotalItem()).toString();

						suppBidprice.setCompleAndTotalItem(completeness + " / " + totalItem);

						bidPriceList.add(suppBidprice);

					}
				}
				auction.setBidderPartiallyCompleteBidsList(bidPriceList);
			}
		} catch (NumberFormatException e) {
			LOG.error("Could not get build Supplier Partially Complete Bids Data :" + e.getMessage(), e);
		}

	}

	private void buildSupplierInvidedListData(RfaEvent event, RfaEventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderInvidedList, SimpleDateFormat sdf, boolean isMasked, RfaEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			bidderInvidedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Invided List Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierRejectedListData(RfaEvent event, RfaEventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderRejectedList, SimpleDateFormat sdf, boolean isMasked, RfaEnvelop envelop) {
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

	private void buildSupplierAcceptedListData(RfaEvent event, RfaEventSupplier eventSupp, List<EvaluationBidderContactPojo> bidderAcceptedList, SimpleDateFormat sdf, boolean isMasked, RfaEnvelop envelop) {
		try {
			EvaluationBidderContactPojo supplierActivitySummary = new EvaluationBidderContactPojo();
			supplierActivitySummary.setCompanyName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), eventSupp.getSupplier().getId(), envelop.getId())) : eventSupp.getSupplierCompanyName());
			supplierActivitySummary.setActionDate(eventSupp.getSupplierEventReadTime() != null ? sdf.format(eventSupp.getSupplierEventReadTime()) : "N/A");
			bidderAcceptedList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Accepted List Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierTotallyCompleteBidsData(EvaluationAuctionBiddingPojo auction, RfaEvent event, boolean isMasked, RfaEnvelop envelop) {

		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);
			List<String> bqids = rfaEnvelopDao.getBqIdlistByEnvelopId(envelop.getId());

			if (CollectionUtil.isNotEmpty(bqids)) {
				if (CollectionUtil.isNotEmpty(eventBq)) {
					for (RfaSupplierBqPojo item : eventBq) {
						if (item.getCompleteness() == item.getTotalItem()) {
							EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
							BigDecimal saving = BigDecimal.ZERO, percentage = BigDecimal.ZERO;

							if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
								saving = item.getInitialPrice().subtract(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
								percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
								suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
							}

							if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
								saving = (item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice()).subtract(item.getInitialPrice());
								percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
								suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
							}

							suppBidprice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId()) : item.getSupplierCompanyName());
							suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
							suppBidprice.setDecimal(event.getDecimal());
							suppBidprice.setPreAuctionPrice(item.getInitialPrice());
							if (item.getInitialPrice() != null) {
								suppBidprice.setPreAuctionStrPrice(coolFormat(item.getInitialPrice().doubleValue(), 0));
							}

							if (item.getRevisedGrandTotal() != null) {
								suppBidprice.setPostAuctionStrPrice(coolFormat(item.getRevisedGrandTotal().doubleValue(), 0));
							}

							suppBidprice.setSaving(saving);
							LOG.info("Parcent : " + percentage);
							suppBidprice.setPercentage(percentage);
							suppBidprice.setAuctionType(event.getAuctionType().getValue());
							bidPriceList.add(suppBidprice);
						}
					}
				}
			}
			auction.setBidderTotallyCompleteBidsList(bidPriceList);
		} catch (NumberFormatException e) {
			LOG.error("Could not get build Supplier Totally Complete Bids Data :" + e.getMessage(), e);
		}

	}

	private void buildSupplierTotallyCompleteBidsDataForBidding(EvaluationAuctionBiddingPojo auction, RfaEvent event, boolean isMasked, RfaEnvelop envelop) {

		try {
			List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaSupplierBqCompleteNessBySupplierIdsOdrByRank(event.getId(), null);

			if (CollectionUtil.isNotEmpty(eventBq)) {
				for (RfaSupplierBqPojo item : eventBq) {
					if (item.getCompleteness() == item.getTotalItem()) {
						EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
						BigDecimal saving = BigDecimal.ZERO, percentage = BigDecimal.ZERO;

						if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
							saving = item.getInitialPrice().subtract(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
							percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
							suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
						}

						if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
							saving = (item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice()).subtract(item.getInitialPrice());
							percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
							suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
						}

						suppBidprice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId()) : item.getSupplierCompanyName());
						suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
						suppBidprice.setDecimal(event.getDecimal());
						suppBidprice.setPreAuctionPrice(item.getInitialPrice());
						if (item.getInitialPrice() != null) {
							suppBidprice.setPreAuctionStrPrice(coolFormat(item.getInitialPrice().doubleValue(), 0));
						}

						if (item.getRevisedGrandTotal() != null) {
							suppBidprice.setPostAuctionStrPrice(coolFormat(item.getRevisedGrandTotal().doubleValue(), 0));
						}

						suppBidprice.setSaving(saving);
						LOG.info("Parcent : " + percentage);
						suppBidprice.setPercentage(percentage);
						suppBidprice.setAuctionType(event.getAuctionType().getValue());
						bidPriceList.add(suppBidprice);
					}
				}
			}
			auction.setBidderTotallyCompleteBidsList(bidPriceList);
		} catch (NumberFormatException e) {
			LOG.error("Could not get build Supplier Totally Complete Bids Data :" + e.getMessage(), e);
		}

	}

	private void buildSupplierActivitySummaryData(RfaEvent event, RfaSupplierBqPojo eventSupp, List<EvaluationBidderContactPojo> supplierActivitySummaryList, SimpleDateFormat sdf) {
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
			supplierActivitySummary.setActionStatus(submissionRemark);
			supplierActivitySummary.setIpnumber(eventSupp.getIpAddress());

			supplierActivitySummaryList.add(supplierActivitySummary);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Activity Summary Data :" + e.getMessage(), e);
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

	private void bidderPriceSubmissionHistoryAndBqItemData(RfaEvent event, List<EvaluationSupplierBidsPojo> supplierBidHistory, EventSupplier eventSupplier, SimpleDateFormat sdf, EvaluationAuctionBiddingPojo auction, AuctionRules auctionRules, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<RfaSupplierBq> bqList = rfaSupplierBqDao.findSupplierBqbyEventId(event.getId());
			String bqId = null;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RfaSupplierBq rfaSupplierEventBq : bqList) {
					bqId = rfaSupplierEventBq.getBq().getId();
				}
			}
			RfaSupplierBq supBq = rfaSupplierBqDao.findBqByBqId(bqId, eventSupplier.getSupplier().getId());
			if (supBq != null) {
				EvaluationSupplierBidsPojo bidderPriceHistory = new EvaluationSupplierBidsPojo();
				bidderPriceHistory.setSupplierName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()) : (supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : ""));
				bidderPriceHistory.setRemark(StringUtils.checkString(supBq.getRemark()));
				bidderPriceHistory.setBqDescription(supBq.getName());
				bidderPriceHistory.setInitialPrice(supBq.getInitialPrice());
				bidderPriceHistory.setDecimals(event.getDecimal());
				bidderPriceHistory.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
				List<EvaluationBiddingPricePojo> BidderPriceList = new ArrayList<EvaluationBiddingPricePojo>();
				List<AuctionBids> supplierBidsList = auctionBidsDao.getAuctionBidsForSupplierForReport(eventSupplier.getSupplier().getId(), event.getId());
				if (CollectionUtil.isNotEmpty(supplierBidsList)) {
					BigDecimal reductionPrice = BigDecimal.ZERO, temp = BigDecimal.ZERO, percentage = BigDecimal.ZERO;
					temp = supBq.getInitialPrice();
					int bidNumber = 1;
					for (AuctionBids suppBids : supplierBidsList) {
						LOG.info("toLogString " + suppBids.toLogString());
						reductionPrice = BigDecimal.ZERO;
						percentage = BigDecimal.ZERO;
						EvaluationBiddingPricePojo bidderPrice = new EvaluationBiddingPricePojo();
						if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
							reductionPrice = temp.subtract(suppBids.getAmount());
						}
						if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
							reductionPrice = suppBids.getAmount().subtract(temp);
						}
						percentage = reductionPrice.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(temp, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
						bidderPrice.setBidNumber(bidNumber);
						bidderPrice.setDecimal(event.getDecimal());
						bidderPrice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
						bidderPrice.setDisplayValue(coolFormat(suppBids.getAmount().doubleValue(), 0));
						bidderPrice.setPriceSubmission(suppBids.getAmount());
						bidderPrice.setPriceReduction(reductionPrice);
						bidderPrice.setPercentage(percentage);
						bidderPrice.setSubmitedDate(suppBids.getBidSubmissionDate() != null ? sdf.format(suppBids.getBidSubmissionDate()) : "");
						bidderPrice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), eventSupplier.getSupplier().getId(), envelop.getId()) : (supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : ""));
						BidderPriceList.add(bidderPrice);
						bidNumber += 1;
					}
				}
				bidderPriceHistory.setPriceSubmissionList(BidderPriceList);
				RfaSupplierBq bq = rfaSupplierBqDao.getRfaSupplierBqByEventIdAndSupplierId(event.getId(), eventSupplier.getSupplier().getId());
				buildSupplierBqData(event, bq, bidderPriceHistory, auction, eventSupplier.getSupplier().getId(), auctionRules);
				supplierBidHistory.add(bidderPriceHistory);
			}
		} catch (Exception e) {
			LOG.error("Could not get Bidder Price submission History Details " + e.getMessage(), e);
		}

	}

	private void buildSupplierBqData(RfaEvent event, RfaSupplierBq supplierBq, EvaluationSupplierBidsPojo bidderPriceHistory, EvaluationAuctionBiddingPojo auction, String supplierId, AuctionRules auctionRules) {
		try {
			List<RfaSupplierBqItem> supBqItem = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(supplierBq.getBq().getId(), supplierId);
			RfaEventSupplier eventSupplier = rfaEventSupplierService.findEventSupplierByEventIdAndSupplierIgnoreSubmitStatus(event.getId(), supplierId);

			BigDecimal additionalTax = null, grandTotal = null, totalAfterTax = null;
			String additionalTaxDesc = "";
			if (supplierBq != null) {
				additionalTax = supplierBq.getAdditionalTax();
				additionalTaxDesc = supplierBq.getTaxDescription();
				grandTotal = supplierBq.getGrandTotal();
				totalAfterTax = supplierBq.getTotalAfterTax();
				bidderPriceHistory.setSupplierRemark(supplierBq.getRemark());
			}
			List<EvaluationBqItemPojo> bqItems = new ArrayList<EvaluationBqItemPojo>();
			for (RfaSupplierBqItem item : supBqItem) {
				EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
				evlBqItem.setDescription(item.getItemName());
				evlBqItem.setLevel(item.getLevel() + "." + item.getOrder());
				bqItems.add(evlBqItem);
				if (item.getChildren() != null) {
					for (RfaSupplierBqItem childBqItem : item.getChildren()) {
						EvaluationBqItemPojo evlBqChilItem = new EvaluationBqItemPojo();
						evlBqChilItem.setDescription(childBqItem.getItemName());
						evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
						evlBqChilItem.setQuantity(childBqItem.getQuantity());
						evlBqChilItem.setUom(childBqItem.getUom().getUom());
						evlBqChilItem.setTaxAmt(childBqItem.getTax() != null ? childBqItem.getTax() : BigDecimal.ZERO);
						evlBqChilItem.setUnitPrice(childBqItem.getUnitPrice());
						evlBqChilItem.setTotalAmt(childBqItem.getTotalAmountWithTax());
						evlBqChilItem.setAmount(childBqItem.getTotalAmount());
						evlBqChilItem.setAdditionalTax(additionalTax);
						evlBqChilItem.setAdditionalTaxDesc(additionalTaxDesc);
						if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
							LOG.info("===========================>");
							evlBqChilItem.setRevisedBidSubmitted(eventSupplier.getRevisedBidSubmitted() != null ? eventSupplier.getRevisedBidSubmitted() : Boolean.TRUE);
						} else {
							LOG.info("===========================>");
						}
						evlBqChilItem.setGrandTotal(grandTotal != null ? grandTotal : BigDecimal.ZERO);
						evlBqChilItem.setDecimal(childBqItem.getEvent() != null ? childBqItem.getEvent().getDecimal() : "");
						evlBqChilItem.setTotalAfterTax(totalAfterTax);
						bqItems.add(evlBqChilItem);
					}
				}
			}
			bidderPriceHistory.setBqItems(bqItems);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Bq Data :" + e.getMessage(), e);
		}
	}

	private void buildDisqualificationSuppliersData(String eventId, EvaluationAuctionBiddingPojo auction, EvaluationAuctionBiddingPojo parentAuction, boolean isMasked, RfaEnvelop envelop, SimpleDateFormat sdf, boolean isEvaluation) {
		try {

			List<RfaEventSupplier> supplierList = null;
			if (isEvaluation) {
				supplierList = rfaEventSupplierService.findDisqualifySupplierForEvaluationReportByEventId(eventId);
			} else {
				supplierList = rfaEventSupplierService.findDisqualifySupplierByEventId(eventId);
			}
			List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
			if (CollectionUtils.isNotEmpty(supplierList)) {
				LOG.info("List is not empty : " + supplierList.size());
				if (supplierList.size() == 1) {
					for (RfaEventSupplier supplier : supplierList) {
						EvaluationSuppliersPojo suppliers = new EvaluationSuppliersPojo();
						LOG.info("supplier.getDisqualify() : " + supplier.getDisqualify());

						suppliers.setIsQualify(supplier.getDisqualify());
						if (StringUtils.checkString(supplier.getRejectionRemarks()).length() > 0 && !isEvaluation) {
							suppliers.setRemark(supplier.getRejectionRemarks());
						} else {
							suppliers.setRemark(StringUtils.checkString(supplier.getDisqualifyRemarks()));
						}
						suppliers.setSupplierName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), supplier.getSupplier().getId(), envelop.getId()) : supplier.getSupplierCompanyName());
						suppliers.setDisqualifiedTime(supplier.getDisqualifiedTime() != null ? sdf.format(supplier.getDisqualifiedTime()) : null);
						suppliers.setDisqualifiedBy(supplier.getDisqualifiedBy() != null ? supplier.getDisqualifiedBy().getName() : null);
						suppliers.setDisqualifiedEnvelope(supplier.getDisqualifiedEnvelope() != null && StringUtils.checkString(supplier.getDisqualifiedEnvelope().getEnvelopTitle()).length() > 0 ? supplier.getDisqualifiedEnvelope().getEnvelopTitle() : null);
						allSuppliers.add(suppliers);
					}

					auction.setShowSingleDisQaulify("true");
				} else {
					for (int j = 0; j < supplierList.size(); j++) {
						EvaluationSuppliersPojo e = new EvaluationSuppliersPojo();
						LOG.info("supplier.getDisqualify() : " + supplierList.get(j).getDisqualify());
						e.setSupplierName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), supplierList.get(j).getSupplier().getId(), envelop.getId()) : supplierList.get(j).getSupplierCompanyName());
						if (StringUtils.checkString(supplierList.get(j).getRejectionRemarks()).length() > 0 && !isEvaluation) {
							e.setRemark(supplierList.get(j).getRejectionRemarks());
						} else {
							e.setRemark(StringUtils.checkString(supplierList.get(j).getDisqualifyRemarks()));
						}
						e.setDisqualifiedBy(supplierList.get(j).getDisqualifiedBy() != null ? supplierList.get(j).getDisqualifiedBy().getName() : null);
						e.setDisqualifiedTime(supplierList.get(j).getDisqualifiedTime() != null ? sdf.format(supplierList.get(j).getDisqualifiedTime()) : null);
						e.setDisqualifiedEnvelope(supplierList.get(j).getDisqualifiedEnvelope() != null && StringUtils.checkString(supplierList.get(j).getDisqualifiedEnvelope().getEnvelopTitle()).length() > 0 ? supplierList.get(j).getDisqualifiedEnvelope().getEnvelopTitle() : null);
						e.setIsQualify(supplierList.get(j).getDisqualify());
						if (j % 2 == 0) {
							if (supplierList.size() > j + 1) {
								LOG.info("supplier.getDisqualify() : " + supplierList.get(j + 1).getDisqualify());
								e.setSupplierName2(isMasked ? MaskUtils.maskName(envelop.getPreFix(), supplierList.get(j + 1).getSupplier().getId(), envelop.getId()) : supplierList.get(j + 1).getSupplierCompanyName());
								if (StringUtils.checkString(supplierList.get(j + 1).getRejectionRemarks()).length() > 0 && !isEvaluation) {
									e.setRemark2(supplierList.get(j + 1).getRejectionRemarks());
								} else {
									e.setRemark2(StringUtils.checkString(supplierList.get(j + 1).getDisqualifyRemarks()));
								}
								e.setDisqualifiedBy2(supplierList.get(j + 1).getDisqualifiedBy() != null ? supplierList.get(j + 1).getDisqualifiedBy().getName() : null);
								e.setDisqualifiedTime2(supplierList.get(j + 1).getDisqualifiedTime() != null ? sdf.format(supplierList.get(j + 1).getDisqualifiedTime()) : null);
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
			// this is for RFA only please check before copy for other
			parentAuction.setDisQualifiedSuppliers(allSuppliers);
		} catch (Exception e) {
			LOG.error("Could not get build Disqualification Suppliers Data :" + e.getMessage(), e);
		}
	}

	private void buildTopCompletedBarChartData(RfaEvent event, EvaluationAuctionBiddingPojo auction, boolean isMasked, RfaEnvelop envelop) {
		List<EvaluationBiddingPricePojo> bidPriceList = new ArrayList<EvaluationBiddingPricePojo>();
		try {
			List<RfaSupplierBqPojo> eventBq = rfaSupplierBqDao.findRfaTopSupplierBqBySupplierIdsOdrByRank(event.getId(), 5);
			if (CollectionUtil.isNotEmpty(eventBq)) {
				for (RfaSupplierBqPojo item : eventBq) {
					EvaluationBiddingPricePojo suppBidprice = new EvaluationBiddingPricePojo();
					BigDecimal saving = BigDecimal.ZERO, percentage = BigDecimal.ZERO;

					if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
						saving = item.getInitialPrice().subtract(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
						percentage = (saving.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
						suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
					}
					if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
						saving = (item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice()).subtract(item.getInitialPrice());
						percentage = (saving.multiply(new BigDecimal(100)).divide(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
						suppBidprice.setPostAuctionprice(item.getRevisedGrandTotal() != null ? item.getRevisedGrandTotal() : item.getInitialPrice());
					}
					suppBidprice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), item.getSupplierId(), envelop.getId()) : item.getSupplierCompanyName());
					suppBidprice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
					suppBidprice.setDecimal(event.getDecimal());
					suppBidprice.setPreAuctionPrice(item.getInitialPrice());
					if (item.getInitialPrice() != null) {
						suppBidprice.setPreAuctionStrPrice(coolFormat(item.getInitialPrice().doubleValue(), 0));
					}
					if (item.getRevisedGrandTotal() != null) {
						suppBidprice.setPostAuctionStrPrice(coolFormat(item.getRevisedGrandTotal().doubleValue(), 0));
					}
					suppBidprice.setSaving(saving);
					suppBidprice.setPercentage(percentage);
					suppBidprice.setAuctionType(event.getAuctionType().getValue());
					bidPriceList.add(suppBidprice);
				}
			}
		} catch (Exception e) {
			LOG.error("Could not get build Top Completed Bar Chart Data :" + e.getMessage(), e);
		}
		auction.setBiddingPrice(bidPriceList);
	}

	private void buildEventDetailData(SimpleDateFormat sdf, RfaEvent event, EvaluationAuctionBiddingPojo auction) {
		try {
			String auctionDate = event.getEventStart() != null ? sdf.format(event.getEventStart()) : "" + "-" + (event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			String auctionTitle = "PRE & POST AUCTION PRICE (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : " ") + ")";
			String netSavingTitle = "Saving based on Budged(%)";
			auction.setAuctionId(event.getEventId());
			auction.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "N/A");
			auction.setEventDescription(StringUtils.checkString(event.getEventDescription()));
			auction.setReferenceNo(event.getReferanceNumber());
			auction.setAuctionName(event.getEventName());
			auction.setEventType(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			auction.setOwnerWithContact(event.getEventOwner() != null ? event.getEventOwner().getName() + " ( " + event.getEventOwner().getCommunicationEmail() + " Tel :" + (StringUtils.checkString(event.getEventOwner().getPhoneNumber()).length() > 0 ? event.getEventOwner().getPhoneNumber() : " N/A") + ")" : "");
			auction.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
			auction.setAuctionType(event.getAuctionType() != null ? event.getAuctionType().getValue() : "");
			auction.setDateTime(auctionDate);
			auction.setAuctionTitle(auctionTitle);
			auction.setNetSavingTitle(netSavingTitle);
			auction.setBuyerName(event.getEventOwner().getBuyer().getCompanyName());
			auction.setAuctionCreatorDate(event.getCreatedDate() != null ? sdf.format(event.getCreatedDate()) : "");
			auction.setAuctionPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			auction.setAuctionStartDate(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			auction.setAuctionEndDate(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			auction.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
			auction.setAuctionExtension(event.getTimeExtensionType() != null ? event.getTimeExtensionType().name() : "");
			auction.setTotalExtension(event.getTotalExtensions());
			auction.setAuctionStatus(event.getStatus().name());
			auction.setIsBuyer(Boolean.TRUE);
			auction.setDecimal(event.getDecimal());
		} catch (Exception e) {
			LOG.error("Could not get build Event Detail Data :" + e.getMessage(), e);
		}
	}

	private void buildLeadSupplierData(RfaEvent event, EvaluationAuctionBiddingPojo auction, RfaSupplierBqPojo leadingbid, EvaluationAuctionBiddingPojo mainPojo, AuctionRules auctionRules, boolean isMasked, RfaEnvelop envelop) {
		try {
			if (leadingbid != null) {
				BigDecimal savingVsLowest = BigDecimal.ZERO, percentageVsLowest = BigDecimal.ZERO;
				BigDecimal savingVsBudget = BigDecimal.ZERO, percentageVsBudget = BigDecimal.ZERO;
				BigDecimal savingVsHistoric = BigDecimal.ZERO, percentageVsHistoric = BigDecimal.ZERO;
				if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					savingVsLowest = leadingbid.getInitialPrice().subtract(leadingbid.getRevisedGrandTotal() != null ? leadingbid.getRevisedGrandTotal() : leadingbid.getInitialPrice());
					percentageVsLowest = (savingVsLowest.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(leadingbid.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
				}
				if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
					savingVsLowest = (leadingbid.getRevisedGrandTotal() != null ? leadingbid.getRevisedGrandTotal() : leadingbid.getInitialPrice()).subtract(leadingbid.getInitialPrice());
					percentageVsLowest = (savingVsLowest.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(leadingbid.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
				}

				if (event.getBudgetAmount() != null && event.getBudgetAmount().compareTo(BigDecimal.ZERO) != 0) {
					savingVsBudget = event.getBudgetAmount().subtract(leadingbid.getTotalAfterTax() != null ? leadingbid.getTotalAfterTax() : BigDecimal.ZERO);
					percentageVsBudget = (savingVsBudget.multiply(new BigDecimal(100)).divide(event.getBudgetAmount(), BigDecimal.ROUND_HALF_UP).setScale(2, RoundingMode.HALF_UP));
					auction.setRemarkComparerBudgetPrice((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + formatedDecimalNumber(event.getDecimal(), savingVsBudget) + " or " + formatedDecimalNumber(event.getDecimal(), percentageVsBudget) + "% vs budgeted price");
				}
				if (event.getHistoricaAmount() != null && event.getHistoricaAmount().compareTo(BigDecimal.ZERO) != 0) {
					savingVsHistoric = event.getHistoricaAmount().subtract(leadingbid.getTotalAfterTax() != null ? leadingbid.getTotalAfterTax() : BigDecimal.ZERO);
					percentageVsHistoric = (savingVsHistoric.multiply(new BigDecimal(100)).divide(event.getHistoricaAmount(), BigDecimal.ROUND_HALF_UP).setScale(2, RoundingMode.HALF_UP));
					auction.setRemarkCompareHistoricPrice((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + formatedDecimalNumber(event.getDecimal(), savingVsHistoric) + " or " + formatedDecimalNumber(event.getDecimal(), percentageVsHistoric) + "% vs historic price");
				}

				auction.setSupplierCompanyName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), leadingbid.getSupplierId(), envelop.getId()) : leadingbid.getSupplierCompanyName());
				// this is only for RFA
				mainPojo.setSupplierCompanyName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), leadingbid.getSupplierId(), envelop.getId()) : leadingbid.getSupplierCompanyName());
				auction.setLeadSuppliergrandTotal((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + formatedDecimalNumber(event.getDecimal(), leadingbid.getTotalAfterTax()));
				auction.setRevisedGrandTotal(leadingbid.getTotalAfterTax());
				auction.setRemarkCompareLowestPrice((event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + " " + formatedDecimalNumber(event.getDecimal(), savingVsLowest) + " or " + formatedDecimalNumber(event.getDecimal(), percentageVsLowest) + "% vs lowest pre-bid price");

				RfaSupplierBq bq = rfaSupplierBqDao.getRfaSupplierBqByEventIdAndSupplierId(event.getId(), leadingbid.getSupplierId());
				buildLeadSupplierBqData(event, bq, mainPojo, leadingbid.getSupplierId(), auctionRules);
			} else {
				LOG.error("Could not get Supplier leadin=================>");
			}
			auction.setSupplier(leadingbid);
		} catch (Exception e) {
			LOG.error("Could not get Supplier leadingbid Details" + e.getMessage(), e);
		}

	}

	private void buildLeadSupplierBqData(RfaEvent event, RfaSupplierBq supplierBq, EvaluationAuctionBiddingPojo auction, String supplierId, AuctionRules auctionRules) {
		try {

			List<RfaSupplierBqItem> supBqItem = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(supplierBq.getBq().getId(), supplierId);

			RfaEventSupplier eventSupplier = rfaEventSupplierService.findEventSupplierByEventIdAndSupplierIgnoreSubmitStatus(event.getId(), supplierId);
			BigDecimal additionalTax = null, grandTotal = null, totalAfterTax = null;
			String additionalTaxDesc = "";
			if (supplierBq != null) {
				additionalTax = supplierBq.getAdditionalTax();
				additionalTaxDesc = supplierBq.getTaxDescription();
				grandTotal = supplierBq.getGrandTotal();
				totalAfterTax = supplierBq.getTotalAfterTax();
				auction.setLeadingSupplierBqRemak(supplierBq.getRemark());
			}
			List<EvaluationBqItemPojo> bqItems = new ArrayList<EvaluationBqItemPojo>();
			for (RfaSupplierBqItem item : supBqItem) {
				EvaluationBqItemPojo items = new EvaluationBqItemPojo();
				items.setLevel(item.getLevel() + "." + item.getOrder());
				items.setDescription(item.getItemName());
				bqItems.add(items);
				if (item.getChildren() != null) {
					for (RfaSupplierBqItem childBqItem : item.getChildren()) {
						EvaluationBqItemPojo evlBqChilItem = new EvaluationBqItemPojo();
						evlBqChilItem.setDescription(childBqItem.getItemName());
						evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
						evlBqChilItem.setQuantity(childBqItem.getQuantity());
						evlBqChilItem.setUom(childBqItem.getUom().getUom());
						evlBqChilItem.setTaxAmt(childBqItem.getTax());
						evlBqChilItem.setAmount(childBqItem.getTotalAmount());
						evlBqChilItem.setUnitPrice(childBqItem.getUnitPrice());
						evlBqChilItem.setTotalAmt(childBqItem.getTotalAmountWithTax());
						evlBqChilItem.setAdditionalTax(additionalTax);
						evlBqChilItem.setAdditionalTaxDesc(additionalTaxDesc);

						evlBqChilItem.setGrandTotal(grandTotal != null ? grandTotal : BigDecimal.ZERO);
						evlBqChilItem.setTotalAfterTax(totalAfterTax);
						evlBqChilItem.setDecimal(childBqItem.getEvent() != null ? childBqItem.getEvent().getDecimal() : "");
						if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
							evlBqChilItem.setRevisedBidSubmitted(eventSupplier.getRevisedBidSubmitted() != null ? eventSupplier.getRevisedBidSubmitted() : Boolean.TRUE);
						}
						bqItems.add(evlBqChilItem);
					}
				}
			}
			auction.setTopSupplierbqItem(bqItems);
			if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
				auction.setRevisedBidSubmitted(eventSupplier.getRevisedBidSubmitted() != null ? eventSupplier.getRevisedBidSubmitted() : Boolean.TRUE);
			}
		} catch (Exception e) {
			LOG.error("Could not get build Lead Supplier Bq Data" + e.getMessage(), e);
		}
	}

	// Bid History
	private void buildSupplierLineAndBidHistoryChartData(SimpleDateFormat sdf, RfaEvent event, EvaluationAuctionBiddingPojo auction, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<EvaluationSupplierBidsPojo> supplierBidHistory = new ArrayList<EvaluationSupplierBidsPojo>();
			List<EvaluationBiddingPricePojo> evaluationSupplierBidsPojoList = new ArrayList<EvaluationBiddingPricePojo>();
			List<RfaSupplierBqPojo> SupplierList = rfaSupplierBqDao.getAllRfaSuppliersIdByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(SupplierList)) {

				for (RfaSupplierBqPojo suppItem : SupplierList) {
					bidderBidHistory(event, supplierBidHistory, suppItem, sdf, isMasked, envelop);
				}
			}
			for (EvaluationSupplierBidsPojo evaluationSupplierBidsPojo : supplierBidHistory) {
				for (EvaluationBiddingPricePojo evaluationBiddingPricePojo : evaluationSupplierBidsPojo.getPriceSubmissionList()) {
					evaluationSupplierBidsPojoList.add(evaluationBiddingPricePojo);
				}
			}
			if (CollectionUtil.isNotEmpty(evaluationSupplierBidsPojoList)) {
				evaluationSupplierBidsPojoList = getSortedEvaluationSupplierBids(evaluationSupplierBidsPojoList);
			}
			auction.setEvaluationSupplierBidsPojoList(evaluationSupplierBidsPojoList);
			auction.setSupplierBidHistoryList(supplierBidHistory);
		} catch (Exception e) {
			LOG.error("Could not get build Supplier Line And BidHistory Chart Data" + e.getMessage(), e);
		}
	}

	private void bidderBidHistory(RfaEvent event, List<EvaluationSupplierBidsPojo> supplierBidHistory, RfaSupplierBqPojo suppItem, SimpleDateFormat sdf, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<RfaSupplierBq> bqList = rfaSupplierBqDao.findSupplierBqbyEventId(event.getId());
			String bqId = null;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RfaSupplierBq rfaSupplierEventBq : bqList) {
					bqId = rfaSupplierEventBq.getBq().getId();
				}
			}
			RfaSupplierBq supBq = rfaSupplierBqDao.findBqByBqId(bqId, suppItem.getSupplierId());
			if (supBq != null) {
				EvaluationSupplierBidsPojo bidderPriceHistory = new EvaluationSupplierBidsPojo();
				bidderPriceHistory.setSupplierName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), suppItem.getSupplierId(), envelop.getId()) : supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : "");
				LOG.info("SUPPLIER TOP 5" + supBq.getSupplier().getCompanyName());
				bidderPriceHistory.setBqDescription(supBq.getName());
				bidderPriceHistory.setInitialPrice(supBq.getInitialPrice());
				bidderPriceHistory.setDecimals(event.getDecimal());
				bidderPriceHistory.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
				List<EvaluationBiddingPricePojo> BidderPriceList = new ArrayList<EvaluationBiddingPricePojo>();

				List<AuctionBids> supplierBidsList = auctionBidsDao.getAuctionBidsForSupplierForReport(suppItem.getSupplierId(), event.getId());
				LOG.info("supplierBidsList " + supplierBidsList);

				if (CollectionUtil.isNotEmpty(supplierBidsList)) {
					BigDecimal reductionPrice = BigDecimal.ZERO, temp = BigDecimal.ZERO, percentage = BigDecimal.ZERO;
					temp = supBq.getInitialPrice();
					int bidNumber = 1;
					for (AuctionBids suppBids : supplierBidsList) {
						LOG.info("toLogString " + suppBids.toLogString());
						reductionPrice = BigDecimal.ZERO;
						percentage = BigDecimal.ZERO;
						EvaluationBiddingPricePojo bidderPrice = new EvaluationBiddingPricePojo();

						// if (temp == BigDecimal.ZERO) {
						// reductionPrice = temp;
						// // percentage = reductionPrice.multiply(new
						// // BigDecimal(100)).divide(temp,
						// // BigDecimal.ROUND_FLOOR);
						// percentage = BigDecimal.ZERO;
						// } else {
						if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
							reductionPrice = temp.subtract(suppBids.getAmount());
						}
						if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
							reductionPrice = suppBids.getAmount().subtract(temp);
						}

						percentage = reductionPrice.multiply(new BigDecimal(100)).divide(temp, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
						// }
						// temp = suppBids.getAmount();
						bidderPrice.setBidNumber(bidNumber);
						bidderPrice.setDecimal(event.getDecimal());
						bidderPrice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");

						bidderPrice.setDisplayValue(coolFormat(suppBids.getAmount().doubleValue(), 0));
						bidderPrice.setPriceSubmission(suppBids.getAmount());
						bidderPrice.setPriceReduction(reductionPrice);
						bidderPrice.setPercentage(percentage);
						bidderPrice.setSubmitedDate(suppBids.getBidSubmissionDate() != null ? sdf.format(suppBids.getBidSubmissionDate()) : "");
						bidderPrice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), suppItem.getSupplierId(), envelop.getId()) : supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : "");
						BidderPriceList.add(bidderPrice);
						bidNumber += 1;
					}
				}
				bidderPriceHistory.setPriceSubmissionList(BidderPriceList);

				supplierBidHistory.add(bidderPriceHistory);
			}
		} catch (Exception e) {
			LOG.error("Could not get Bidder Price submission History Details " + e.getMessage(), e);
		}

	}

	@Override
	public List<DraftEventPojo> getAllExcelEventReportForBuyer(String tenantId, String[] eventArr, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {

		List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		getRfaEventsForExport(tenantId, eventArr, resultList, searchFilterEventPojo, select_all, input, startDate, endDate);
		return resultList;
	}

	private void getRfaEventsForExport(String tenantId, String[] eventArr, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		try {
			List<RfaEvent> rftList = rfaEventDao.getEventsByIds(tenantId, eventArr, searchFilterEventPojo, select_all, input, startDate, endDate);
			LOG.info("rftList>." + rftList.size());
			if (CollectionUtil.isNotEmpty(rftList)) {
				for (RfaEvent event : rftList) {
					DraftEventPojo eventPojo = new DraftEventPojo();
					if (event.getEventDescription() != null) {
						eventPojo.setEventDescription(event.getEventDescription());
					}
					eventPojo.setId(event.getId());
					eventPojo.setInvitedSupplierCount(event.getSuppliers().size());
					eventPojo.setCostCenter(event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "");
					eventPojo.setEventName(event.getEventName());
					eventPojo.setReferenceNumber(event.getReferanceNumber());
					eventPojo.setSysEventId(event.getEventId());
					eventPojo.setEventStart(event.getEventStart());
					eventPojo.setEventEnd(event.getEventEnd());
					eventPojo.setType(RfxTypes.RFA);
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

					RfaEventSupplier leadingSupplier = null;
					int submittedCount = 0;
					int acceptedCount = 0;
					BigDecimal leadingAmount = BigDecimal.ZERO;
					for (RfaEventSupplier es : event.getSuppliers()) {
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
						for (RfaEventBq eventBq : event.getEventBqs()) {
							// Check if any items is zero
							// int count =
							// rfaSupplierBqItemDao.countIncompleteBqItemByBqIdsForSupplier(eventBq.getId(),
							// es.getSupplier().getId());
							// if (count > 0) {
							// allOk = false;
							// break;
							// }
							RfaSupplierBq supBq = rfaSupplierBqDao.findBqByEventIdAndSupplierIdOfQualifiedSupplier(event.getId(), eventBq.getId(), es.getSupplier().getId());

							if (supBq == null) {
								allOk = false;
								break;
							}
							if (event.getAuctionType().equals(AuctionType.FORWARD_ENGISH) || event.getAuctionType().equals(AuctionType.FORWARD_SEALED_BID) || event.getAuctionType().equals(AuctionType.FORWARD_DUTCH)) {
								LOG.info("FORWORD AUCTION");
								BigDecimal minLeadingPrice = rfaSupplierBqDao.findMaxLeadingPrice(event.getId(), eventBq.getId(), es.getSupplier().getId());
								bqTotal = bqTotal.add(minLeadingPrice);
								LOG.info("bqTotal for Forword Auction " + bqTotal);
							} else {
								LOG.info("REVERSE AUCTION");
								BigDecimal maxLeadingPrice = rfaSupplierBqDao.findMinLeadingPrice(event.getId(), eventBq.getId(), es.getSupplier().getId());
								bqTotal = bqTotal.add(maxLeadingPrice);
								LOG.info("bqTotal for Reverse Auction " + bqTotal);
							}
						}
						if (!allOk)
							continue;
						if (leadingAmount.compareTo(new BigDecimal(0.0)) == 0) {
							if (es.getRevisedBidSubmitted()) {
								leadingSupplier = es;
								leadingAmount = bqTotal;
							}
						} else if (leadingAmount.compareTo(new BigDecimal(0.0)) != 0) {
							if (event.getAuctionType().equals(AuctionType.FORWARD_ENGISH) || event.getAuctionType().equals(AuctionType.FORWARD_SEALED_BID) || event.getAuctionType().equals(AuctionType.FORWARD_DUTCH)) {
								if (bqTotal.doubleValue() > leadingAmount.doubleValue()) {
									if (es.getRevisedBidSubmitted()) {
										leadingSupplier = es;
										leadingAmount = bqTotal;
									}
								}
							} else {
								if (bqTotal.doubleValue() < leadingAmount.doubleValue()) {
									if (es.getRevisedBidSubmitted()) {
										leadingSupplier = es;
										leadingAmount = bqTotal;
									}
								}
							}
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
		} catch (Exception e) {
			LOG.error("Could not get Rfa Events For Export " + e.getMessage(), e);
		}
	}

	private void buildBidHistoryData(SimpleDateFormat sdf, RfaEvent event, EvaluationAuctionBiddingPojo auction, RfaSupplierBqPojo leadSupplier, boolean isMasked, RfaEnvelop envelop) {
		try {
			List<EvaluationSupplierBidsPojo> supplierBidHistory = new ArrayList<EvaluationSupplierBidsPojo>();
			List<EvaluationSupplierBidsPojo> supplierBidHistoryTable = new ArrayList<EvaluationSupplierBidsPojo>();
			List<EvaluationBiddingPricePojo> evaluationSupplierBidsLineChartPojoList = new ArrayList<EvaluationBiddingPricePojo>();
			List<EvaluationBiddingPricePojo> evaluationSupplierBidsLineTimeChartPojoList = new ArrayList<EvaluationBiddingPricePojo>();

			List<RfaSupplierBqPojo> supplierList = rfaSupplierBqDao.getAllRfaSuppliersIdByEventId(event.getId());
			/*
			 * if (leadSupplier != null) { List<AuctionBids> topSupplierPreviousBidsList =
			 * auctionBidsDao.getAuctionTopPreviousBidsForSupplierForReport(leadSupplier.getSupplierId(),
			 * event.getId()); for (AuctionBids auctionBids : topSupplierPreviousBidsList) { LeadPriviousAuctionBid =
			 * auctionBids; } }
			 */
			if (CollectionUtil.isNotEmpty(supplierList)) {
				for (RfaSupplierBqPojo suppItem : supplierList) {
					bidderBidHistoryAndLineChart(event, supplierBidHistory, suppItem, sdf, isMasked, envelop);
					if (isMasked) {
						suppItem.setSupplierCompanyName(MaskUtils.maskName(envelop.getPreFix(), suppItem.getSupplierId(), envelop.getId()));
					}
					bidderBidHistoryAndTable(event, supplierBidHistoryTable, suppItem, sdf, leadSupplier);
				}
			}

			for (EvaluationSupplierBidsPojo evaluationSupplierBidsPojo : supplierBidHistory) {
				for (EvaluationBiddingPricePojo evaluationBiddingPricePojo : evaluationSupplierBidsPojo.getPriceSubmissionList()) {
					evaluationSupplierBidsLineChartPojoList.add(evaluationBiddingPricePojo);
				}
			}
			if (CollectionUtil.isNotEmpty(evaluationSupplierBidsLineChartPojoList)) {
				evaluationSupplierBidsLineChartPojoList = getSortedEvaluationSupplierBidsLineChart(evaluationSupplierBidsLineChartPojoList);
			}
			if (CollectionUtil.isNotEmpty(evaluationSupplierBidsLineChartPojoList)) {
				evaluationSupplierBidsLineTimeChartPojoList = getSortedEvaluationSupplierBidsLineTimeChart(evaluationSupplierBidsLineChartPojoList);
			}

			/*
			 * for (EvaluationBiddingPricePojo evaluationBiddingPricePojo : evaluationSupplierBidsLineTimeChartPojoList)
			 * { //this is for time chart data in pdf to show long time
			 * evaluationBiddingPricePojo.setSubmitedDate(evaluationBiddingPricePojo.getSubmitedDate().replace(" ",
			 * "\n")); }
			 */
			auction.setEvaluationSupplierBidsLineChartPojoList(evaluationSupplierBidsLineChartPojoList);
			auction.setEvaluationSupplierBidsLineTimeChartPojoList(evaluationSupplierBidsLineTimeChartPojoList);
			auction.setSupplierBidHistoryTable(supplierBidHistoryTable);
			auction.setSuppBidHistoryList(supplierBidHistory);
		} catch (Exception e) {
			LOG.error("Could not get build Bid History Data " + e.getMessage(), e);
		}
	}

	private void bidderBidHistoryAndTable(RfaEvent event, List<EvaluationSupplierBidsPojo> supplierBidHistory, RfaSupplierBqPojo suppItem, SimpleDateFormat sdf, RfaSupplierBqPojo leadSupplier) {
		try {

			EvaluationSupplierBidsPojo bidderPriceHistory = new EvaluationSupplierBidsPojo();
			List<AuctionBids> supplierBidsList = auctionBidsDao.getAuctionPreviousBidsForSupplierForReportOrderByDate(suppItem.getSupplierId(), event.getId());
			bidderPriceHistory.setSupplierName(suppItem.getSupplierCompanyName() != null ? suppItem.getSupplierCompanyName() : "N/A");
			BigDecimal changeForPreviousLeadingBid = BigDecimal.ZERO, changeFromOwnPrevious = BigDecimal.ZERO;
			if (CollectionUtil.isNotEmpty(supplierBidsList)) {
				for (AuctionBids suppBids : supplierBidsList) {
					bidderPriceHistory.setTime(suppBids.getBidSubmissionDate() != null ? sdf.format(suppBids.getBidSubmissionDate()) : "");
					break;
				}
			}

			if (leadSupplier != null) {

				if ((suppItem.getRevisedGrandTotal() != null ? suppItem.getRevisedGrandTotal() : suppItem.getInitialPrice()).compareTo((leadSupplier.getRevisedGrandTotal() != null ? leadSupplier.getRevisedGrandTotal() : leadSupplier.getInitialPrice())) < 0) {
					changeForPreviousLeadingBid = (leadSupplier.getRevisedGrandTotal() != null ? leadSupplier.getRevisedGrandTotal() : leadSupplier.getInitialPrice()).subtract((suppItem.getRevisedGrandTotal() != null ? suppItem.getRevisedGrandTotal() : suppItem.getInitialPrice()).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(leadSupplier.getRevisedGrandTotal() != null ? leadSupplier.getRevisedGrandTotal() : leadSupplier.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
				} else {
					changeForPreviousLeadingBid = ((suppItem.getRevisedGrandTotal() != null ? suppItem.getRevisedGrandTotal() : suppItem.getInitialPrice()).subtract(leadSupplier.getRevisedGrandTotal() != null ? leadSupplier.getRevisedGrandTotal() : leadSupplier.getInitialPrice()).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(leadSupplier.getRevisedGrandTotal() != null ? leadSupplier.getRevisedGrandTotal() : leadSupplier.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
				}
				if (suppItem.getRevisedGrandTotal() != null) {

					if (suppItem.getRevisedGrandTotal().compareTo(suppItem.getInitialPrice()) < 0) {
						changeFromOwnPrevious = suppItem.getInitialPrice().subtract(suppItem.getRevisedGrandTotal()).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(suppItem.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
					} else {
						changeFromOwnPrevious = suppItem.getRevisedGrandTotal().subtract(suppItem.getInitialPrice()).setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP).multiply(new BigDecimal(100)).divide(suppItem.getInitialPrice(), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
					}
				}
				bidderPriceHistory.setAmount(suppItem.getRevisedGrandTotal() != null ? suppItem.getRevisedGrandTotal() : BigDecimal.ZERO);

				bidderPriceHistory.setChangeForPreviousLeadingBid(changeForPreviousLeadingBid);
				bidderPriceHistory.setChangeFromOwnPrevious(changeFromOwnPrevious);
				supplierBidHistory.add(bidderPriceHistory);
			}
		} catch (Exception e) {
			LOG.error("Could not get bidder Bid History And Table Details " + e.getMessage(), e);
		}

	}

	private void bidderBidHistoryAndLineChart(RfaEvent event, List<EvaluationSupplierBidsPojo> supplierBidHistory, RfaSupplierBqPojo suppItem, SimpleDateFormat sdf, boolean isMasked, RfaEnvelop envelop) {
		try {

			List<RfaSupplierBq> bqList = rfaSupplierBqDao.findSupplierBqbyEventId(event.getId());
			String bqId = null;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RfaSupplierBq rfaSupplierEventBq : bqList) {
					bqId = rfaSupplierEventBq.getBq().getId();
				}
			}
			RfaSupplierBq supBq = rfaSupplierBqDao.findBqByBqId(bqId, suppItem.getSupplierId());
			if (supBq != null) {
				EvaluationSupplierBidsPojo bidderPriceHistory = new EvaluationSupplierBidsPojo();
				bidderPriceHistory.setSupplierName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), suppItem.getSupplierId(), envelop.getId()) : supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : "");
				LOG.info("SUPPLIER TOP" + supBq.getSupplier().getCompanyName());
				bidderPriceHistory.setBqDescription(supBq.getName());
				bidderPriceHistory.setInitialPrice(supBq.getInitialPrice());
				bidderPriceHistory.setDecimals(event.getDecimal());
				bidderPriceHistory.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
				List<EvaluationBiddingPricePojo> BidderPriceList = new ArrayList<EvaluationBiddingPricePojo>();
				List<AuctionBids> supplierBidsList = auctionBidsDao.getAuctionBidsForSupplierForReport(suppItem.getSupplierId(), event.getId());
				LOG.info("supplierBidsList " + supplierBidsList);
				if (CollectionUtil.isNotEmpty(supplierBidsList)) {
					BigDecimal reductionPrice = BigDecimal.ZERO, temp = BigDecimal.ZERO, percentage = BigDecimal.ZERO;
					temp = supBq.getInitialPrice();
					int bidNumber = 1;
					for (AuctionBids suppBids : supplierBidsList) {
						LOG.info("toLogString " + suppBids.toLogString());
						reductionPrice = BigDecimal.ZERO;
						percentage = BigDecimal.ZERO;
						EvaluationBiddingPricePojo bidderPrice = new EvaluationBiddingPricePojo();
						if (event.getAuctionType() == AuctionType.REVERSE_ENGISH || event.getAuctionType() == AuctionType.REVERSE_DUTCH || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
							reductionPrice = temp.subtract(suppBids.getAmount());
						}
						if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
							reductionPrice = suppBids.getAmount().subtract(temp);
						}

						percentage = reductionPrice.multiply(new BigDecimal(100)).divide(temp, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
						bidderPrice.setBidNumber(bidNumber);
						bidderPrice.setDecimal(event.getDecimal());
						bidderPrice.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
						bidderPrice.setDisplayValue(coolFormat(suppBids.getAmount().doubleValue(), 0));
						bidderPrice.setPriceSubmission(suppBids.getAmount());
						bidderPrice.setPriceReduction(reductionPrice);
						bidderPrice.setPercentage(percentage);
						bidderPrice.setSubmitionDate(suppBids.getBidSubmissionDate());
						bidderPrice.setSubmitedDate(suppBids.getBidSubmissionDate() != null ? sdf.format(suppBids.getBidSubmissionDate()) : "");
						bidderPrice.setBidderName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), suppItem.getSupplierId(), envelop.getId()) : supBq.getSupplier() != null ? supBq.getSupplier().getCompanyName() : "");
						BidderPriceList.add(bidderPrice);
						bidNumber += 1;
					}
				}
				bidderPriceHistory.setPriceSubmissionList(BidderPriceList);
				supplierBidHistory.add(bidderPriceHistory);
			}
		} catch (Exception e) {
			LOG.error("Could not get bidder Bid History And Line Chart " + e.getMessage(), e);
		}
	}

	private void buildReverttoLastBidData(SimpleDateFormat sdf, RfaEvent event, EvaluationAuctionBiddingPojo auction, List<RfaEventSupplier> supplierList) {
		try {
			List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
			for (RfaEventSupplier rfaEventSupplier : supplierList) {
				EvaluationSuppliersPojo suppliers = new EvaluationSuppliersPojo();
				List<AuctionBids> supplierBidsList = auctionBidsDao.getRevertAuctionBidsForSupplierForReport(rfaEventSupplier.getSupplier().getId(), event.getId());

				for (AuctionBids auctionBids : supplierBidsList) {
					if (auctionBids.getIsReverted()) {
						suppliers.setSupplierName(rfaEventSupplier.getSupplierCompanyName());
						suppliers.setDisqualifiedBy(auctionBids.getRevertedBy() != null ? auctionBids.getRevertedBy().getName() : "N/A");
						suppliers.setSubmisionTime(auctionBids.getBidSubmissionDate() != null ? sdf.format(auctionBids.getBidSubmissionDate()) : "N/A");
						suppliers.setRemark(StringUtils.checkString(auctionBids.getRemark()).length() > 0 ? auctionBids.getRemark() : "");
						allSuppliers.add(suppliers);
					}
				}
			}
			auction.setReverToBidSuppliers(allSuppliers);
		} catch (Exception e) {
			LOG.error("Could not get build Revert to Last Bid Data " + e.getMessage(), e);
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
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat generatedSdf = new SimpleDateFormat("dd MMMMM yyyy HH:mm");
		generatedSdf.setTimeZone(timeZone);
		parameters.put("generatedOn", generatedSdf.format(new Date()));
		try {
			Resource resource = applicationContext.getResource("classpath:reports/AuctionEvaluationBook.jasper");
			File jasperfile = resource.getFile();

			List<EvaluationAuctionBiddingPojo> auctionSummary = buildEvalutionReportData(eventId, sdf, parameters, evenvelopId);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auctionSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Report PDF . " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationAuctionBiddingPojo> buildEvalutionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			RfaEvent event = rfaEventDao.findByEventId(eventId);

			List<RfaEventSupplier> supplierList = rfaEventSupplierDao.getAllRfaEventSuppliersByEventId(eventId);
			if (event != null) {
				RfaEnvelop envelop = rfaEnvelopService.getRfaEnvelopById(evenvelopId);

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

				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				auction.setIsMask(isMasked);
				List<EventSupplier> participatedSupplier = buildSupplierCountData(supplierList, auction);
				RfaSupplierBqPojo leadSupplier = buildHeadingReport(event, supplierList, auction, sdf, auctionRules, isMasked, envelop, true);
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
				buildSupplierLineChartAndData(sdf, event, supplierList, auction, auctionRules, isMasked, envelop);
				buildBidHistoryData(sdf, event, auction, leadSupplier, isMasked, envelop);

				List<String> sortedSupplierList = new ArrayList<>();
				List<String> top3Supplier = new ArrayList<>();
				buildEnvoleBQData(event, auction, sdf, envelop, participatedSupplier, auctionRules, isMasked, sortedSupplierList, top3Supplier);
				buildEnvoleSORData(event, auction, sdf, envelop, isMasked, sortedSupplierList, top3Supplier);

				buildEnvelopeCQData(event, auction, sdf, envelop, participatedSupplier, isMasked);

				List<EvaluationBidHistoryPojo> bidHistoryDetails = new ArrayList<EvaluationBidHistoryPojo>();

				List<AuctionBids> bidHistory = auctionBidsDao.getAuctionBidsListByEventIdForReport(event.getId());
				if (CollectionUtil.isNotEmpty(bidHistory)) {
					for (AuctionBids item : bidHistory) {
						EvaluationBidHistoryPojo bd = new EvaluationBidHistoryPojo();
						bd.setBidPrice(item.getAmount());
						bd.setCompanyName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), item.getBidBySupplier().getId(), envelop.getId()) : item.getBidBySupplier().getCompanyName());
						bd.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
						bd.setDateTime(item.getBidSubmissionDate() != null ? sdf.format(item.getBidSubmissionDate()) : "");
						bd.setDecimal(event.getDecimal());
						bidHistoryDetails.add(bd);
					}
				}
				auction.setBidHistory(bidHistoryDetails);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);

		} catch (Exception e) {
			LOG.error("Could not get build Evalution Report Data :" + e.getMessage(), e);
		}
		return auctionSummary;

	}

	private void buildEvaluatorDeclarationAcceptData(SimpleDateFormat sdf, String envelopId, String eventId, EvaluationAuctionBiddingPojo auction) {
		List<RfaEvaluatorDeclaration> evalutorDeclarationList = rfaEvaluatorDeclarationDao.getAllEvaluatorDeclarationByEnvelopAndEventId(envelopId, eventId);
		List<EvaluationDeclarationAcceptancePojo> evaluationDeclarationList = new ArrayList<EvaluationDeclarationAcceptancePojo>();
		if (CollectionUtil.isNotEmpty(evalutorDeclarationList)) {
			for (RfaEvaluatorDeclaration rfaEvaluatorDeclaration : evalutorDeclarationList) {
				EvaluationDeclarationAcceptancePojo evaluationDeclarationPojo = new EvaluationDeclarationAcceptancePojo();
				evaluationDeclarationPojo.setEvaluationCommittee(Boolean.TRUE == rfaEvaluatorDeclaration.getIsLeadEvaluator() ? "Evaluation Owner" : "Evaluation Team");
				evaluationDeclarationPojo.setAcceptedDate(sdf.format(rfaEvaluatorDeclaration.getAcceptedDate()));
				if (rfaEvaluatorDeclaration.getUser() != null) {
					evaluationDeclarationPojo.setUsername(rfaEvaluatorDeclaration.getUser().getName());
					evaluationDeclarationPojo.setUserLoginEmail(rfaEvaluatorDeclaration.getUser().getLoginId());
				}
				evaluationDeclarationList.add(evaluationDeclarationPojo);
			}
		}
		auction.setEvaluationDeclarationAcceptList(evaluationDeclarationList);

	}

	private List<EventSupplier> buildSupplierCountData(List<RfaEventSupplier> supplierList, EvaluationAuctionBiddingPojo auction) {
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


	private void buildEnvoleSORData(RfaEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfaEnvelop envelop, boolean isMasked,
									List<String> sortedSupplierList, List<String> top3Supplier) {
		try {
			List<EnvelopeSorPojo> envopleSorPojos = new ArrayList<EnvelopeSorPojo>();
			List<String> envelopId = new ArrayList<String>();
			envelopId.add(envelop.getId());
			List<SorPojo> sorIds = rfaEnvelopDao.getSorsIdListByEnvelopIdByOrder(envelopId);

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


	private void buildEnvlopeSupplierSORorEvaluationSummary(String bqId, RfaEvent event, EnvelopeSorPojo bqPojo, RfaEnvelop envelop, boolean isMasked, List<String> sortedSupplierList) {
		LOG.info("Build envelope data for SOR ");
		try {
			// Same order as BQ
			List<EvaluationSuppliersSorPojo> sors = rfaSupplierSorDao.getAllSorsBySorIdsAndEventId(bqId, event.getId());
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
						List<RfaSupplierSorItem> suppSorItems = rfaSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierId(sor.getSorId(), sor.getId());
						List<EvaluationSorItemPojo> evlBqItems = new ArrayList<EvaluationSorItemPojo>();
						if (CollectionUtil.isNotEmpty(suppSorItems)) {
							for (RfaSupplierSorItem suppBqItem : suppSorItems) {
								EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									for (RfaSupplierSorItem childBqItem : suppBqItem.getChildren()) {
										EvaluationSorItemPojo evlBqChilItem = new EvaluationSorItemPojo();
										evlBqChilItem.setDescription(childBqItem.getItemName());
										evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
										evlBqChilItem.setUom(childBqItem.getUom().getUom());
										//Rate
										evlBqChilItem.setAmount(childBqItem.getTotalAmount());
										evlBqChilItem.setDecimal(event != null ? event.getDecimal() : "");
										evlBqItems.add(evlBqChilItem);

										List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
										List<RfaSorEvaluationComments> bqItemComments = rfaSorEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getSorItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RfaSorEvaluationComments review : bqItemComments) {
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


	private void buildEnvlopeSORTopSupplierComparisionforEvaluationSummary(String sorId, RfaEvent event, EnvelopeSorPojo bqPojo, boolean isMasked, RfaEnvelop envelop, List<String> top3Supplier) {
		try {
			// Here limit will not work
			// we will filter datq based on Bq top three
			List<RfaSupplierSorPojo> participatedSupplier = rfaSupplierSorDao.getAllRfqTopEventSuppliersIdByEventId(event.getId(), 3, sorId);
			Map<String, EvaluationSorItemPojo> itemsMap = new LinkedHashMap<String, EvaluationSorItemPojo>();
			List<EvaluationSuppliersSorPojo> pojoList = new ArrayList<EvaluationSuppliersSorPojo>();
			EvaluationSuppliersSorPojo pojo = new EvaluationSuppliersSorPojo();
			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierSorPojo rfaSupplierBqPojo = participatedSupplier.get(i);
				top3Supplier.add(rfaSupplierBqPojo.getSupplierCompanyName());
				bqPojo.setSorName(rfaSupplierBqPojo.getSorName());
				List<RfaSupplierSorItem> suppBqItems = rfaSupplierSorItemService.getAllSupplierSorItemForReportBySorIdAndSupplierIdParentIdNotNull(sorId, rfaSupplierBqPojo.getSupplierId());
				if (i == 0) {
					for (RfaSupplierSorItem suppBqItem : suppBqItems) {
						EvaluationSorItemPojo evlBqItem = new EvaluationSorItemPojo();
						evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
						evlBqItem.setItemName(suppBqItem.getItemName());
						evlBqItem.setUom(suppBqItem.getUom() != null ? suppBqItem.getUom().getUom() : "");
						evlBqItem.setAmount(suppBqItem.getTotalAmount());
						pojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
						itemsMap.put(suppBqItem.getSorItem().getId(), evlBqItem);
					}
				} else {
					for (RfaSupplierSorItem suppBqItem : suppBqItems) {
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


	private void buildEnvoleBQData(RfaEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfaEnvelop envelop, List<EventSupplier> participatedSupplier,
								   AuctionRules auctionRules, boolean isMasked, List<String> sortedSupplierList, List<String> top3Supplier) {
		try {
			List<EnvelopeBqPojo> envopleBqPojos = new ArrayList<EnvelopeBqPojo>();
			List<String> bqids = rfaEnvelopDao.getBqIdlistByEnvelopId(envelop.getId());

			for (String bqId : bqids) {
				// for (EvaluationSuppliersBqPojo bq : bqs) {

				EnvelopeBqPojo bqPojo = new EnvelopeBqPojo();
				buildEnvlopeTopSupplierComparisionforEvaluationSummary(bqId, event, bqPojo, auctionRules, isMasked, envelop, top3Supplier);

				buildEnvlopeSupplierBqforEvaluationSummary(participatedSupplier, bqId, event, bqPojo, auctionRules, isMasked, envelop, sortedSupplierList);
				envopleBqPojos.add(bqPojo);
			}
			auction.setEnvelopeBq(envopleBqPojos);
		} catch (Exception e) {
			LOG.error("Could not get build EnvoleBQ Data :" + e.getMessage(), e);
		}

	}

	private void buildEnvlopeTopSupplierComparisionforEvaluationSummary(String bqId, RfaEvent event, EnvelopeBqPojo bqPojo, AuctionRules auctionRules, boolean isMasked,
																		RfaEnvelop envelop, List<String>top3Supplier) {
		try {
			List<RfaSupplierBqPojo> participatedSupplier = rfaSupplierBqDao.getAllRfaTopEventSuppliersIdByEventId(event.getId(), 3, bqId);
			Map<String, EvaluationBqItemPojo> itemsMap = new LinkedHashMap<String, EvaluationBqItemPojo>();
			List<EvaluationSuppliersBqPojo> pojoList = new ArrayList<EvaluationSuppliersBqPojo>();
			EvaluationSuppliersBqPojo pojo = new EvaluationSuppliersBqPojo();
			boolean revisedSubmited = false;
			boolean revisedSubmited1 = false;
			boolean revisedSubmited2 = false;

			for (int i = 0; i < participatedSupplier.size(); i++) {
				RfaSupplierBqPojo rfaSupplierBqPojo = participatedSupplier.get(i);
				top3Supplier.add(rfaSupplierBqPojo.getSupplierCompanyName());
				bqPojo.setBqName(rfaSupplierBqPojo.getBqName());
				LOG.info("Bq name in comparision:" + rfaSupplierBqPojo.getBqName());

				List<RfaSupplierBqItem> suppBqItems = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierIds(bqId, rfaSupplierBqPojo.getSupplierId());
				RfaEventSupplier eventSupplier = rfaEventSupplierService.findEventSupplierByEventIdAndSupplierIgnoreSubmitStatus(event.getId(), rfaSupplierBqPojo.getSupplierId());
				LOG.info("RevisedBidSubmittedTOP >>>>>" + eventSupplier.toLogString());

				if (i == 0) {

					revisedSubmited = eventSupplier.getRevisedBidSubmitted() != null ? eventSupplier.getRevisedBidSubmitted() : Boolean.TRUE;
					pojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
					pojo.setGrandTotal(rfaSupplierBqPojo.getGrandTotal());
					pojo.setAdditionalTax(rfaSupplierBqPojo.getAdditionalTax());
					pojo.setTotalAfterTax(rfaSupplierBqPojo.getTotalAfterTax());

					if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
						pojo.setRevisedBidSubmitted(revisedSubmited);
						pojo.setRevisedBidSubmitted1(revisedSubmited1);
						pojo.setRevisedBidSubmitted2(revisedSubmited2);
					}

					for (RfaSupplierBqItem suppBqItem : suppBqItems) {
						EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
						evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
						evlBqItem.setQuantity(suppBqItem.getQuantity());
						evlBqItem.setItemName(suppBqItem.getItemName());
						evlBqItem.setUom(suppBqItem.getUom() != null ? suppBqItem.getUom().getUom() : "");
						evlBqItem.setUnitPrice(suppBqItem.getUnitPrice());
						evlBqItem.setAmount(suppBqItem.getParent() != null ? suppBqItem.getTotalAmountWithTax() : null);
						if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
							evlBqItem.setRevisedBidSubmitted(true);
							evlBqItem.setRevisedBidSubmitted1(true);
							evlBqItem.setRevisedBidSubmitted2(true);
						}
						itemsMap.put(suppBqItem.getBqItem().getId(), evlBqItem);

					}
				} else {
					if (i == 1) {
						revisedSubmited1 = eventSupplier.getRevisedBidSubmitted() != null ? eventSupplier.getRevisedBidSubmitted() : Boolean.TRUE;
						pojo.setSupplierName1(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
						pojo.setGrandTotal1(rfaSupplierBqPojo.getGrandTotal());
						pojo.setAdditionalTax1(rfaSupplierBqPojo.getAdditionalTax());
						pojo.setTotalAfterTax1(rfaSupplierBqPojo.getTotalAfterTax());

					}
					if (i == 2) {
						revisedSubmited2 = eventSupplier.getRevisedBidSubmitted() != null ? eventSupplier.getRevisedBidSubmitted() : Boolean.TRUE;
						pojo.setSupplierName2(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), rfaSupplierBqPojo.getSupplierId(), envelop.getId())) : rfaSupplierBqPojo.getSupplierCompanyName());
						pojo.setGrandTotal2(rfaSupplierBqPojo.getGrandTotal());
						pojo.setAdditionalTax2(rfaSupplierBqPojo.getAdditionalTax());
						pojo.setTotalAfterTax2(rfaSupplierBqPojo.getTotalAfterTax());

					}
					for (RfaSupplierBqItem suppBqItem : suppBqItems) {
						EvaluationBqItemPojo evlBqItem = itemsMap.get(suppBqItem.getBqItem().getId());
						if (evlBqItem != null) {
							if (i == 1) {
								if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
									evlBqItem.setRevisedBidSubmitted1(true);
								}
								evlBqItem.setSupplier1UnitPrice(suppBqItem.getUnitPrice());
								evlBqItem.setSupplier1TotalAmt(suppBqItem.getParent() != null ? suppBqItem.getTotalAmountWithTax() : null);
							}
							if (i == 2) {
								if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
									evlBqItem.setRevisedBidSubmitted2(true);
								}
								evlBqItem.setSupplier2UnitPrice(suppBqItem.getUnitPrice());
								evlBqItem.setSupplier2TotalAmt(suppBqItem.getParent() != null ? suppBqItem.getTotalAmountWithTax() : null);
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
				if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
					footr.setRevisedBidSubmitted(true);
					footr.setRevisedBidSubmitted1(true);
					footr.setRevisedBidSubmitted2(true);
				}
				pojo.getTopSupplierItemList().add(footr);

				footr = new EvaluationBqItemPojo();
				footr.setItemName("Additional Charges");
				footr.setAmount(pojo.getAdditionalTax());
				footr.setSupplier1TotalAmt(pojo.getAdditionalTax1());
				footr.setSupplier2TotalAmt(pojo.getAdditionalTax2());
				if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
					footr.setRevisedBidSubmitted(true);
					footr.setRevisedBidSubmitted1(true);
					footr.setRevisedBidSubmitted2(true);
				}
				pojo.getTopSupplierItemList().add(footr);

				footr = new EvaluationBqItemPojo();
				footr.setItemName("Grand Total After Additional Charges");
				footr.setAmount(pojo.getTotalAfterTax());
				footr.setSupplier1TotalAmt(pojo.getTotalAfterTax1());
				footr.setSupplier2TotalAmt(pojo.getTotalAfterTax2());
				if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
					footr.setRevisedBidSubmitted(revisedSubmited);
					footr.setRevisedBidSubmitted1(revisedSubmited1);
					footr.setRevisedBidSubmitted2(revisedSubmited2);
				}

				pojo.getTopSupplierItemList().add(footr);
			}
			pojoList.add(pojo);
			bqPojo.setTopSupplierBq(pojoList);
		} catch (Exception e) {
			LOG.error("Could not get build Envlope TopSupplier Comparision for EvaluationSummary :" + e.getMessage(), e);
		}
	}

	private void buildBuyerAttachementData(String eventId, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf) {
		try {
			List<AuctionEvaluationDocumentPojo> documentList = new ArrayList<AuctionEvaluationDocumentPojo>();
			List<RfaEventDocument> documents = rfaDocumentDao.findAllRfaEventdocsbyEventId(eventId);
			if (CollectionUtil.isNotEmpty(documents)) {
				for (RfaEventDocument docs : documents) {
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

	private void buildEvaluatorsSummary(List<RfaEvaluatorUser> evaluators, EvaluationAuctionBiddingPojo auction) {
		try {
			List<EvaluationBqItemComments> evaluationSummary = new ArrayList<EvaluationBqItemComments>();
			for (RfaEvaluatorUser rfaEvaluatorUser : evaluators) {
				EvaluationBqItemComments comments = new EvaluationBqItemComments();
				comments.setCommentBy(rfaEvaluatorUser.getUser() != null ? rfaEvaluatorUser.getUser().getName() : "N/A");
				comments.setComments(StringUtils.checkString(rfaEvaluatorUser.getEvaluatorSummary()));
				comments.setFileName(rfaEvaluatorUser.getFileName() != null ? rfaEvaluatorUser.getFileName() : "N/A");
				evaluationSummary.add(comments);
			}
			auction.setEvaluationSummary(evaluationSummary);
		} catch (Exception e) {
			LOG.error("Could not get build Evaluators Summary :" + e.getMessage(), e);
		}
	}

	private void buildEnvlopeSupplierBqforEvaluationSummary(List<EventSupplier> supplierList, String bqId, RfaEvent event, EnvelopeBqPojo bqPojo, AuctionRules auctionRules,
															boolean isMasked, RfaEnvelop envelop, List<String> sortedSupplierList) {
		try {
			List<EvaluationSuppliersBqPojo> bqs = rfaSupplierBqDao.getAllBqsByBqIdsAndEventId(bqId, event.getId(), event.getAuctionType());
			List<EvaluationSuppliersBqPojo> supplierBq = new ArrayList<EvaluationSuppliersBqPojo>();
			if (CollectionUtils.isNotEmpty(bqs)) {
				for (EvaluationSuppliersBqPojo bq : bqs) {
					EvaluationSuppliersBqPojo bqSupplierPojo = new EvaluationSuppliersBqPojo();
					sortedSupplierList.add(bq.getSupplierName());
					bqPojo.setBqName(bq.getBqName());
					LOG.info("Bq name in evaluation:" + bq.getBqName() + " Supplier : " + bq.getSupplierName());
					bqSupplierPojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), bq.getId(), envelop.getId())) : bq.getSupplierName());
					List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
					if (bq != null) {
						EvaluationBqPojo bqItem = new EvaluationBqPojo();
						bqSupplierPojo.setGrandTotal(bq.getGrandTotal());
						bqSupplierPojo.setTotalAfterTax(bq.getTotalAfterTax());
						bqSupplierPojo.setAdditionalTax(bq.getAdditionalTax());
						if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
							bqSupplierPojo.setRevisedBidSubmitted(bq.getRevisedBidSubmitted() != null ? bq.getRevisedBidSubmitted() : Boolean.FALSE);
						}
						bqSupplierPojo.setRemark(bq.getRemark());
						bqSupplierPojo.setBqName(bq.getBqName());
						bqItem.setName(bq.getBqName());
						List<RfaSupplierBqItem> suppBqItems = rfaSupplierBqItemService.getAllSupplierBqItemForReportByBqIdAndSupplierId(bq.getBqId(), bq.getId());
						List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
						if (CollectionUtil.isNotEmpty(suppBqItems)) {
							LOG.info("suppBqItems size : " + suppBqItems.size());
							for (RfaSupplierBqItem suppBqItem : suppBqItems) {
								EvaluationBqItemPojo evlBqItem = new EvaluationBqItemPojo();
								evlBqItem.setDescription(suppBqItem.getItemName());
								evlBqItem.setLevel(suppBqItem.getLevel() + "." + suppBqItem.getOrder());
								evlBqItem.setQuantity(suppBqItem.getQuantity());
								// evlBqItem.setUom(suppBqItem.getUom().getUom());

								/*
								 * evlBqItem.setTaxAmt(suppBqItem.getTax());
								 * evlBqItem.setUnitPrice(suppBqItem.getUnitPrice());
								 * evlBqItem.setTotalAmt(suppBqItem.getTotalAmountWithTax());
								 * evlBqItem.setAmount(suppBqItem.getTotalAmount());
								 */
								evlBqItem.setDecimal(event != null ? event.getDecimal() : "");
								if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
									evlBqItem.setRevisedBidSubmitted(bq.getRevisedBidSubmitted() != null ? bq.getRevisedBidSubmitted() : Boolean.FALSE);
								}
								evlBqItems.add(evlBqItem);
								if (suppBqItem.getChildren() != null) {
									LOG.info("suppBqItems children size : " + suppBqItem.getChildren().size());
									for (RfaSupplierBqItem childBqItem : suppBqItem.getChildren()) {
										EvaluationBqItemPojo evlBqChilItem = new EvaluationBqItemPojo();
										evlBqChilItem.setDescription(childBqItem.getItemName());
										evlBqChilItem.setLevel(childBqItem.getLevel() + "." + childBqItem.getOrder());
										evlBqChilItem.setQuantity(childBqItem.getQuantity());
										evlBqChilItem.setUom(childBqItem.getUom().getUom());

										evlBqChilItem.setTaxAmt(childBqItem.getTax());
										evlBqChilItem.setUnitPrice(childBqItem.getUnitPrice());
										evlBqChilItem.setTotalAmt(childBqItem.getTotalAmountWithTax());
										evlBqChilItem.setAmount(childBqItem.getTotalAmount());
										if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null) {
											evlBqChilItem.setRevisedBidSubmitted(bq.getRevisedBidSubmitted() != null ? bq.getRevisedBidSubmitted() : Boolean.FALSE);
										}
										evlBqChilItem.setDecimal(event != null ? event.getDecimal() : "");
										// Review Comments
										List<EvaluationBqItemComments> comments = new ArrayList<EvaluationBqItemComments>();
										List<RfaBqEvaluationComments> bqItemComments = rfaBqEvaluationCommentsService.getCommentsForSupplier(suppBqItem.getSupplier(), event.getId(), childBqItem.getBqItem().getId(), null);
										if (CollectionUtil.isNotEmpty(bqItemComments)) {
											String reviews = "";
											for (RfaBqEvaluationComments review : bqItemComments) {
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

	private void buildEnvelopeCQData(RfaEvent event, EvaluationAuctionBiddingPojo auction, SimpleDateFormat sdf, RfaEnvelop envelop, List<EventSupplier> participatedSupplier, boolean isMasked) {
		try {
			List<EnvelopeCqPojo> allCqs = new ArrayList<EnvelopeCqPojo>();
			List<String> cqid = rfaEnvelopDao.getCqIdlistByEnvelopId(envelop.getId());
			if (CollectionUtil.isNotEmpty(cqid)) {
				// List<Supplier> suppList =
				// rfaEventSupplierDao.getEventSuppliersForSummary(event.getId());
				// List<RfaSupplierCqItem> cqList =
				// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqid,
				// event.getId(), suppList);
				List<RfaCq> cqList = rfaCqService.findRfaCqForEventByEnvelopeId(cqid, event.getId());
				for (RfaCq cq : cqList) {
					EnvelopeCqPojo cqPojo = new EnvelopeCqPojo();
					cqPojo.setName(cq.getName());
					buildSupplierCqforEvaluationSummary(participatedSupplier, cq, event, cqPojo, isMasked, envelop);
					allCqs.add(cqPojo);
				}
			}
			auction.setEnvelopeCq(allCqs);
		} catch (Exception e) {
			LOG.error("Could not get build Envelope CQ Data :" + e.getMessage(), e);
		}
	}

	private void buildSupplierCqforEvaluationSummary(List<EventSupplier> participatedSupplier, RfaCq cq, RfaEvent event, EnvelopeCqPojo pojo, boolean isMasked, RfaEnvelop envelop) {

		try {
			List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();

			if (cq != null) {
				EvaluationCqPojo cqPojo = new EvaluationCqPojo();
				cqPojo.setName(cq.getName());
				cqPojo.setDescription(cq.getDescription());

				List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
				if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
					for (RfaCqItem cqItem : cq.getCqItems()) {

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

							LOG.info("itemName>>>>>>>>" + itemName);

							List<EvaluationCqItemSupplierPojo> cqItemSuppliers = new ArrayList<EvaluationCqItemSupplierPojo>();
							List<Supplier> suppList = rfaEventSupplierDao.getEventSuppliersForSummary(event.getId());
							// Below code to get Suppliers Answers of each CQ Items

							if (CollectionUtil.isNotEmpty(suppList)) {
								// List<RfaSupplierCqItem> responseList =
								// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(),
								// event.getId(),
								// suppList);
								List<RfaSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), event.getId(), suppList);
								if (CollectionUtil.isNotEmpty(responseList)) {
									for (RfaSupplierCqItem suppCqItem : responseList) {
										DLOG.info("CQ : " + suppCqItem.getCqItem().getItemName());
										EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
										cqItemSupplierPojo.setSupplierName(isMasked ? (MaskUtils.maskName(envelop.getPreFix(), suppCqItem.getSupplier().getId(), envelop.getId())) : suppCqItem.getSupplier().getCompanyName());

										List<RfaSupplierCqOption> listAnswers = rfaSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());

										if (suppCqItem.getCqItem().getCqType() == CqType.TEXT || suppCqItem.getCqItem().getCqType() == CqType.DATE || suppCqItem.getCqItem().getCqType() == CqType.NUMBER || suppCqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
											cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
										} else if (CqType.DOCUMENT_DOWNLOAD_LINK == suppCqItem.getCqItem().getCqType()) {
											List<String> docIds = new ArrayList<String>();
											// List<RfaCqOption> rfaSupplierCqOptions =
											// suppCqItem.getCqItem().getCqOptions();
											List<RfaCqOption> rfaSupplierCqOptions = rfaCqOptionDao.findOptionsByCqItem(suppCqItem.getCqItem().getId());// suppCqItem.getCqItem().getCqOptions();
											if (CollectionUtil.isNotEmpty(rfaSupplierCqOptions)) {
												for (RfaCqOption rfaSupplierCqOption : rfaSupplierCqOptions) {
													docIds.add(StringUtils.checkString(rfaSupplierCqOption.getValue()));
												}
												List<EventDocument> eventDocuments = rfaDocumentService.findAllRfaEventDocsNamesByEventIdAndDocIds(event.getId(), docIds);
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
											// List<RfaSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
											for (RfaSupplierCqOption op : listAnswers) {
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
										List<RfaCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), event.getId(), cqItem.getId(), null);
										if (CollectionUtil.isNotEmpty(comments)) {
											String evalComment = "";
											for (RfaCqEvaluationComments item : comments) {
												EvaluationCqItemSupplierCommentsPojo supCmnts = new EvaluationCqItemSupplierCommentsPojo();
												supCmnts.setComment(item.getComment());
												supCmnts.setCommentBy(item.getUserName());
												evalComments.add(supCmnts);
												evalComment += "[ " + item.getUserName() + " ] " + item.getComment() + "\n";
											}
											cqItemSupplierPojo.setEvalComment(evalComment);
											cqItemSupplierPojo.setComments(evalComments);
										}
										LOG.info("Comment>>>>>>>>>>>>" + cqItemSupplierPojo.getEvalComment());
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
			Resource resource = applicationContext.getResource("classpath:reports/RfaEvaluationBook.jasper");
			File jasperfile = resource.getFile();

			List<EvaluationAuctionBiddingPojo> auctionSummary = buildSubmissionReportData(eventId, sdf, parameters, evenvelopId);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(auctionSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Submission Report PDF Report. " + e.getMessage(), e);
		}
		return jasperPrint;

	}

	private List<EvaluationAuctionBiddingPojo> buildSubmissionReportData(String eventId, SimpleDateFormat sdf, Map<String, Object> parameters, String evenvelopId) {
		List<EvaluationAuctionBiddingPojo> auctionSummary = new ArrayList<EvaluationAuctionBiddingPojo>();
		try {
			RfaEvent event = rfaEventDao.findByEventId(eventId);

			List<RfaEventSupplier> supplierList = rfaEventSupplierDao.getAllRfaEventSuppliersByEventId(eventId);
			if (event != null) {
				EvaluationAuctionBiddingPojo auction = new EvaluationAuctionBiddingPojo();
				RfaEnvelop envelop = rfaEnvelopService.getRfaEnvelopById(evenvelopId);
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
				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				List<EventSupplier> participatedSupplier = buildSupplierCountData(supplierList, auction);
				RfaSupplierBqPojo leadSupplier = buildHeadingReport(event, supplierList, auction, sdf, auctionRules, isMasked, envelop, false);
				auction.setEnvelopTitle(envelop != null ? envelop.getEnvelopTitle() : "NA");
				auction.setLeadEvaluater(envelop != null ? (envelop.getLeadEvaluater() != null ? envelop.getLeadEvaluater().getName() : "NA") : "NA");
				auction.setLeadEvaluatorSummary(envelop != null ? ((envelop.getEvaluatorSummaryDate() != null ? (sdf.format(envelop.getEvaluatorSummaryDate()) + ":") : "") + (envelop.getLeadEvaluatorSummary() != null ? envelop.getLeadEvaluatorSummary() : "")) : "N/A");
				auction.setFileName(envelop != null ? envelop.getFileName() : "N/A");
				if (envelop != null) {
					buildEvaluatorsSummary(envelop.getEvaluators(), auction);
				}
				buildEventDetailData(sdf, event, auction);
				buildBuyerAttachementData(event.getId(), auction, sdf);
				buildSupplierLineChartAndData(sdf, event, supplierList, auction, auctionRules, isMasked, envelop);
				buildBidHistoryData(sdf, event, auction, leadSupplier, isMasked, envelop);

				List<String> sortedSupplierList = new ArrayList<>();
				List<String> top3Supplier = new ArrayList<>();

				buildEnvoleBQData(event, auction, sdf, envelop, participatedSupplier, auctionRules, isMasked, sortedSupplierList, top3Supplier);
				buildEnvoleSORData(event, auction, sdf, envelop, isMasked, sortedSupplierList,top3Supplier);

				buildEnvelopeCQData(event, auction, sdf, envelop, participatedSupplier, isMasked);

				List<EvaluationBidHistoryPojo> bidHistoryDetails = new ArrayList<EvaluationBidHistoryPojo>();
				List<AuctionBids> bidHistory = auctionBidsDao.getAuctionBidsListByEventIdForReport(event.getId());
				if (CollectionUtil.isNotEmpty(bidHistory)) {
					for (AuctionBids item : bidHistory) {
						EvaluationBidHistoryPojo bd = new EvaluationBidHistoryPojo();
						bd.setBidPrice(item.getAmount());
						bd.setCompanyName(isMasked ? MaskUtils.maskName(envelop.getPreFix(), item.getBidBySupplier().getId(), envelop.getId()) : item.getBidBySupplier().getCompanyName());
						bd.setCurrencyCode(event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "");
						bd.setDateTime(item.getBidSubmissionDate() != null ? sdf.format(item.getBidSubmissionDate()) : "");
						bd.setDecimal(event.getDecimal());
						bidHistoryDetails.add(bd);
					}
				}
				auction.setBidHistory(bidHistoryDetails);
				auctionSummary.add(auction);
			}

			parameters.put("AUCTION_SUMMARY", auctionSummary);

		} catch (Exception e) {
			LOG.error("Could not get build Submission Report Data :" + e.getMessage(), e);
		}
		return auctionSummary;

	}

	@Override
	public JasperPrint generatePdfforEvaluationSummary(String eventId, String envelopeId, JRSwapFileVirtualizer virtualizer) {

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationPojo> rfaSummary = new ArrayList<EvaluationPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationSummaryReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationPojo summary = new EvaluationPojo();
			RfaEvent event = getRfaEventByeventId(eventId);

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
				String type = "RFA";
				summary.setEmail(event.getEventOwner() != null ? event.getEventOwner().getCommunicationEmail() : "");
				summary.setOwner(event.getEventOwner() != null ? event.getEventOwner().getName() : "");
				summary.setMobileno(event.getEventOwner().getPhoneNumber());
				summary.setContactno("");
				summary.setCompanyName("");
				summary.setType(type);

				// Below code for display all Suppliers List

				List<EventSupplier> subMittedSupplierList = null;
				List<EventSupplier> SupplierList = rfaEventSupplierService.getAllSuppliersByEventId(eventId);
				List<EvaluationSuppliersPojo> allSuppliers = new ArrayList<EvaluationSuppliersPojo>();
				List<EvaluationBqPojo> bqGranTotalList = new ArrayList<EvaluationBqPojo>();
				if (CollectionUtils.isNotEmpty(SupplierList)) {
					subMittedSupplierList = new ArrayList<EventSupplier>();
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
							subMittedSupplierList.add(supplier);
						}

					}
				}

				// Below Code to show all CQ details
				List<EvaluationCqPojo> allCqs = getAllSupplierCqforEvaluationSummary(eventId, envelopeId);

				// Below functionality to get all BQ Items along with suppliers
				// response of each BQ Items
				List<EvaluationSuppliersBqPojo> supplierBq = getAllSupplierBqforEvaluationSummary(subMittedSupplierList, eventId, envelopeId);

				// GRAND TOTAL BQ LIST
				List<Bq> bqs = rfaEventBqDao.findRfaBqbyEventId(eventId);
				if (CollectionUtil.isNotEmpty(bqs)) {
					for (Bq bq : bqs) {
						EvaluationBqPojo supBqs = new EvaluationBqPojo();
						supBqs.setName(bq.getName());
						List<EvaluationSuppliersPojo> suppBqComments = new ArrayList<EvaluationSuppliersPojo>();
						if (CollectionUtil.isNotEmpty(subMittedSupplierList)) {
							for (EventSupplier supplier : subMittedSupplierList) {
								EvaluationSuppliersPojo supList = new EvaluationSuppliersPojo();
								RfaSupplierBq supBq = rfaSupplierBqDao.findBqByBqId(bq.getId(), supplier.getSupplier().getId());
								supList.setSupplierName(supplier.getSupplier() != null ? supplier.getSupplier().getCompanyName() : "");
								if (supBq != null) {
									supList.setTotalItemTaxAmt(df.format(supBq.getGrandTotal()));
									supList.setTotalAmt(df.format(supBq.getTotalAfterTax().subtract(supBq.getGrandTotal() != null ? supBq.getGrandTotal() : BigDecimal.ZERO)));
									supList.setGrandTotal(supBq.getTotalAfterTax() != null ? df.format(supBq.getTotalAfterTax()) : "");
								}
								String comments = "";
								List<RfaBqTotalEvaluationComments> comment = rfaBqTotalEvaluationCommentsService.getComments(supplier.getSupplier().getId(), eventId, bq.getId(), SecurityLibrary.getLoggedInUser());
								if (CollectionUtil.isNotEmpty(comment)) {
									for (RfaBqTotalEvaluationComments leadComments : comment) {
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

				rfaSummary.add(summary);
			}
			parameters.put("EVALUATION_SUMMARY", rfaSummary);
			// LOG.info(" SUMMARY DETAILS :: " + rftSummary.toString());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfaSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private List<EvaluationCqPojo> getAllSupplierCqforEvaluationSummary(String eventId, String envelopeId) {
		List<EvaluationCqPojo> allCqs = new ArrayList<EvaluationCqPojo>();
		try {

			List<RfaCq> cqList = rfaCqService.findRfaCqForEventByEnvelopeId(eventId, envelopeId);
			for (RfaCq cq : cqList) {
				EvaluationCqPojo cqPojo = new EvaluationCqPojo();
				cqPojo.setName(cq.getName());
				cqPojo.setDescription(cq.getDescription());

				List<EvaluationCqItemPojo> allCqItems = new ArrayList<EvaluationCqItemPojo>();
				if (CollectionUtils.isNotEmpty(cq.getCqItems())) {
					for (RfaCqItem cqItem : cq.getCqItems()) {

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
						List<Supplier> suppList = rfaEventSupplierDao.getEventSuppliersForSummary(eventId);
						// Below code to get Suppliers Answers of each CQ Items
						if (CollectionUtil.isNotEmpty(suppList)) {
							// List<RfaSupplierCqItem> responseList =
							// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId,
							// suppList);
							List<RfaSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppList);
							if (CollectionUtil.isNotEmpty(responseList)) {
								for (RfaSupplierCqItem suppCqItem : responseList) {
									EvaluationCqItemSupplierPojo cqItemSupplierPojo = new EvaluationCqItemSupplierPojo();
									cqItemSupplierPojo.setSupplierName(suppCqItem.getSupplier().getCompanyName());
									List<RfaSupplierCqOption> listAnswers = rfaSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(suppCqItem.getId());
									if (suppCqItem.getCqItem().getCqType() == CqType.TEXT || suppCqItem.getCqItem().getCqType() == CqType.DATE || suppCqItem.getCqItem().getCqType() == CqType.NUMBER || suppCqItem.getCqItem().getCqType() == CqType.PARAGRAPH) {
										cqItemSupplierPojo.setAnswer(suppCqItem.getTextAnswers());
									} else if (CollectionUtil.isNotEmpty(listAnswers)) {
										String str = "";
										// List<RfaSupplierCqOption> listAnswers = suppCqItem.getListAnswers();
										for (RfaSupplierCqOption op : listAnswers) {
											int cqAnsListSize = (listAnswers).size();
											int index = (listAnswers).indexOf(op);
											str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : cqAnsListSize == 0 ? "" : (index == (cqAnsListSize - 1) ? "" : "\n\n"));

											cqItemSupplierPojo.setScores(op.getScoring());
											cqItemSupplierPojo.setAnswer(str);
										}
									}
									cqItemSupplierPojo.setEvaluatorComments(cqItem.getLeadEvaluationComment() != null ? cqItem.getLeadEvaluationComment() : "");
									// Review Comments
									List<EvaluationCqItemSupplierCommentsPojo> evalComments = new ArrayList<EvaluationCqItemSupplierCommentsPojo>();
									List<RfaCqEvaluationComments> comments = cqEvaluationCommentsService.getCommentsForSupplier(suppCqItem.getSupplier(), eventId, cqItem.getId(), null);
									if (CollectionUtil.isNotEmpty(comments)) {
										String evalComment = "";
										for (RfaCqEvaluationComments item : comments) {
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
			LOG.error("Could not get All SupplierCq for EvaluationSummary " + e.getMessage(), e);
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

						List<Bq> bqs = rfaEventBqDao.findRfaBqbyEventIdByEnvelopeId(eventId, envelopeId);

						List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
						if (CollectionUtil.isNotEmpty(bqs)) {
							for (Bq bq : bqs) {
								EvaluationBqPojo bqItem = new EvaluationBqPojo();
								bqItem.setName(bq.getName());
								List<RfaSupplierBqItem> suppBqItems = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bq.getId(), supItem.getSupplier().getId());
								List<EvaluationBqItemPojo> evlBqItems = new ArrayList<EvaluationBqItemPojo>();
								if (CollectionUtil.isNotEmpty(suppBqItems)) {
									for (RfaSupplierBqItem suppBqItem : suppBqItems) {
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
											for (RfaSupplierBqItem childBqItem : suppBqItem.getChildren()) {
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
												List<RfaBqEvaluationComments> bqItemComments = rfaBqEvaluationCommentsService.getCommentsForSupplier(supItem.getSupplier(), eventId, childBqItem.getBqItem().getId(), null);
												if (CollectionUtil.isNotEmpty(bqItemComments)) {
													String reviews = "";
													for (RfaBqEvaluationComments review : bqItemComments) {
														EvaluationBqItemComments bqComment = new EvaluationBqItemComments();
														bqComment.setCommentBy(review.getUserName());
														bqComment.setComments(review.getComment());
														comments.add(bqComment);
														reviews += "[ " + review.getUserName() + " ] " + review.getComment() + "\n";
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

		} catch (Exception e) {
			LOG.error("Could not get All SupplierBq for EvaluationSummary " + e.getMessage(), e);
		}
		return supplierBq;
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
	public List<IndustryCategoryPojo> getTopFiveCategory(String tanentId) {
		return rfaEventDao.getTopFiveCategory(tanentId);

	}

	@Override
	public List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {

		List<DraftEventPojo> resultList = new ArrayList<DraftEventPojo>();
		getRfaSearchEventsForExport(tenantId, eventIds, resultList, searchFilterEventPojo, select_all, startDate, endDate, sdf);
		return resultList;
	}

	private void getRfaSearchEventsForExport(String tenantId, String[] eventIds, List<DraftEventPojo> resultList, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		try {
			List<RfaEvent> rftList = rfaEventDao.getSearchEventByIds(tenantId, eventIds, searchFilterEventPojo, select_all, endDate, startDate);
			LOG.info("rftList>." + rftList.size());
			if (CollectionUtil.isNotEmpty(rftList)) {
				for (RfaEvent event : rftList) {
					DraftEventPojo eventPojo = new DraftEventPojo();
					if (event.getEventDescription() != null) {
						eventPojo.setEventDescription(event.getEventDescription());
					}
					eventPojo.setId(event.getId());
					eventPojo.setInvitedSupplierCount(event.getSuppliers().size());
					eventPojo.setCostCenter(event.getCostCenter() != null ? event.getCostCenter().getCostCenter() : "");
					eventPojo.setEventName(event.getEventName());
					eventPojo.setReferenceNumber(event.getReferanceNumber());
					eventPojo.setSysEventId(event.getEventId());
					eventPojo.setEventStart(event.getEventStart());
					eventPojo.setEventEnd(event.getEventEnd());
					eventPojo.setType(RfxTypes.RFA);
					eventPojo.setDeliveryDate(event.getDeliveryDate());
					eventPojo.setVisibility(event.getEventVisibility());
					eventPojo.setPublishDate(event.getEventPublishDate());
					eventPojo.setValidityDays(event.getSubmissionValidityDays());
					eventPojo.setEventUser(event.getEventOwner().getName());
					eventPojo.setAuctionType(event.getAuctionType() != null ? (event.getAuctionType().toString()) : "");
					eventPojo.setHistricAmount(event.getHistoricaAmount() != null ? (event.getHistoricaAmount()) : BigDecimal.ZERO);
					eventPojo.setDeposite(event.getDeposit() != null ? (event.getDeposit()) : BigDecimal.ZERO);
					eventPojo.setEstimatedBudget(event.getBudgetAmount() != null ? (event.getBudgetAmount()) : BigDecimal.ZERO);
					eventPojo.setEventFees(event.getParticipationFees() != null ? (event.getParticipationFees()) : BigDecimal.ZERO);
					eventPojo.setTemplateName(event.getTemplate() != null ? (event.getTemplate().getTemplateName()) : "");
					eventPojo.setConcludedaDate(event.getConcludeDate() != null ? (event.getConcludeDate().toString()) : "");
					eventPojo.setAwardedDate(event.getAwardDate() != null ? event.getAwardDate().toString() : "");
					eventPojo.setPushDate(event.getEventPushDate() != null ? event.getEventPushDate().toString() : "");
					eventPojo.setProcurementMethod(event.getProcurementMethod() != null ? event.getProcurementMethod().getProcurementMethod() : "");
					eventPojo.setProcurementCategories(event.getProcurementCategories() != null ? event.getProcurementCategories().getProcurementCategories() : "");
					Double avarageBidValue = rfaEventService.getAvarageBidPriceSubmitted(event.getId());
					eventPojo.setAvarageBidSubmited(avarageBidValue != null ? avarageBidValue.toString() : "");
					if (event.getTeamMembers() != null) {
						String names = "";
						for (RfaTeamMember teamMember : event.getTeamMembers()) {
							if (teamMember.getTeamMemberType() == TeamMemberType.Associate_Owner) {
								names += teamMember.getUser() != null ? (teamMember.getUser().getName() + ",") : "";
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

					List<EventSupplier> supplierList = rfaEventSupplierService.getAllSuppliersByEventId(event.getId());
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
					// }F
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

					RfaEventSupplier leadingSupplier = null;
					int submittedCount = 0;
					int acceptedCount = 0;
					BigDecimal leadingAmount = BigDecimal.ZERO;
					for (RfaEventSupplier es : event.getSuppliers()) {
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
						for (RfaEventBq eventBq : event.getEventBqs()) {
							// Check if any items is zero
							// int count =
							// rfaSupplierBqItemDao.countIncompleteBqItemByBqIdsForSupplier(eventBq.getId(),
							// es.getSupplier().getId());
							// if (count > 0) {
							// allOk = false;
							// break;
							// }
							RfaSupplierBq supBq = rfaSupplierBqDao.findBqByEventIdAndSupplierIdOfQualifiedSupplier(event.getId(), eventBq.getId(), es.getSupplier().getId());

							if (supBq == null) {
								allOk = false;
								break;
							}
							if (event.getAuctionType().equals(AuctionType.FORWARD_ENGISH) || event.getAuctionType().equals(AuctionType.FORWARD_SEALED_BID) || event.getAuctionType().equals(AuctionType.FORWARD_DUTCH)) {
								LOG.info("FORWORD AUCTION");
								BigDecimal minLeadingPrice = rfaSupplierBqDao.findMaxLeadingPrice(event.getId(), eventBq.getId(), es.getSupplier().getId());
								bqTotal = bqTotal.add(minLeadingPrice);
								LOG.info("bqTotal for Forword Auction " + bqTotal);
							} else {
								LOG.info("REVERSE AUCTION");
								BigDecimal maxLeadingPrice = rfaSupplierBqDao.findMinLeadingPrice(event.getId(), eventBq.getId(), es.getSupplier().getId());
								bqTotal = bqTotal.add(maxLeadingPrice);
								LOG.info("bqTotal for Reverse Auction " + bqTotal);
							}
						}
						if (!allOk)
							continue;
						if (leadingAmount.compareTo(new BigDecimal(0.0)) == 0) {
							if (es.getRevisedBidSubmitted()) {
								leadingSupplier = es;
								leadingAmount = bqTotal;
							}
						} else if (leadingAmount.compareTo(new BigDecimal(0.0)) != 0) {
							if (event.getAuctionType().equals(AuctionType.FORWARD_ENGISH) || event.getAuctionType().equals(AuctionType.FORWARD_SEALED_BID) || event.getAuctionType().equals(AuctionType.FORWARD_DUTCH)) {
								if (bqTotal.doubleValue() > leadingAmount.doubleValue()) {
									if (es.getRevisedBidSubmitted()) {
										leadingSupplier = es;
										leadingAmount = bqTotal;
									}
								}
							} else {
								if (bqTotal.doubleValue() < leadingAmount.doubleValue()) {
									if (es.getRevisedBidSubmitted()) {
										leadingSupplier = es;
										leadingAmount = bqTotal;
									}
								}
							}
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
		} catch (Exception e) {
			LOG.error("Could not get Rfa Events For Export " + e.getMessage(), e);
		}
	}

	@Override
	public EventTimerPojo getTimeRfaEventByeventId(String eventId) {

		return rfaEventDao.getTimeRfaEventByeventId(eventId);
	}

	@Override
	public List<RfaEvent> getAllRfaEventByTenantId(String tenantId) {

		return rfaEventDao.getAllRfaEventByTenantId(tenantId);
	}

	@Override
	public Event getEventByEventRefranceNo(String eventRefranceNo, String tenantId) {
		return rfaEventDao.getEventByEventRefranceNo(eventRefranceNo, tenantId);
	}

	@Override
	public EventPojo loadEventPojoForSummeryDetailPageForSupplierById(String eventId) {
		return rfaEventDao.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
	}

	@Override
	public EventPojo loadSupplierEventPojoForSummeryById(String eventId) {
		return rfaEventDao.loadSupplierEventPojoForSummeryById(eventId);
	}

	@Override
	public long getInvitedSupplierCount(String eventId) {
		return rfaEventDao.getInvitedSupplierCount(eventId);
	}

	@Override
	public long getSubmitedSupplierCount(String eventId) {
		return rfaEventDao.getSubmitedSupplierCount(eventId);
	}

	@Override
	public long getParticepatedSupplierCount(String eventId) {
		return rfaEventDao.getParticipatedSupplierCount(eventId);
	}

	@Override
	public List<RfaSupplierBq> getLowestSubmissionPrice(String eventId, Boolean prebidFlag) {
		return rfaEventDao.getLowestSubmissionPrice(eventId, prebidFlag);
	}

	@Override
	public List<RfaSupplierBq> getHighestSubmissionPrice(String eventId, Boolean prebidFlag) {
		return rfaEventDao.getHighestSubmissionPrice(eventId, prebidFlag);
	}

	@Override
	public List<RfaEventSupplier> getSuppliersByStatus(String eventId) {

		List<RfaEventSupplier> rfaSuppList = rfaEventDao.getSuppliersByStatus(eventId);
		for (RfaEventSupplier rfaEventSupplier : rfaSuppList) {
			if (rfaEventSupplier.getSupplier() != null) {
				rfaEventSupplier.getSupplier().getCompanyName();
			}
		}
		return rfaSuppList;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaSupplierBq getFinalBidsForSuppliers(String eventId, String supplierId) {
		return rfaEventDao.getFinalBidsForSuppliers(eventId, supplierId);
	}

	@Override
	public Boolean findAuctionRulesWithEventId(String eventId) {
		return auctionRulesDao.findAuctionRulesWithEventId(eventId);
	}

	@Override
	public long getNumberOfBidForRfa(String eventId) {
		return rfaEventDao.getNumberOfBidForRfa(eventId);
	}

	@Override
	public RfaEnvelop getBqForEnvelope(String envelopeId) {
		return rfaEventDao.getBqForEnvelope(envelopeId);
	}

	/*
	 * @Override public String getSuppliersByRevisedBidTime(String supplierId) { return
	 * rfaEventDao.getSuppliersByRevisedBidTime(supplierId); }
	 */

	@Override
	public EventPojo getRfaForPublicEventByeventId(String eventId) {
		return rfaEventDao.getRfaForPublicEventByeventId(eventId);
	}

	@Override
	public List<IndustryCategory> getIndustryCategoriesForRfaById(String eventId) {
		return rfaEventDao.getIndustryCategoriesForRfaById(eventId);
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

			@SuppressWarnings("unused")
			String imgPath = context.getRealPath("resources/images");

			File jasperfile = resource.getFile();

			EvaluationPojo eventDetails = new EvaluationPojo();
			eventDetails.setReferenceNo(event.getReferanceNumber());
			eventDetails.setEventName(StringUtils.checkString(event.getEventName()));
			eventDetails.setEventStart(event.getEventStart() != null ? sdf.format(event.getEventStart()) : "");
			eventDetails.setType("RFA");
			eventDetails.setCompanyName(event.getCompanyName());
			eventDetails.setEventEnd(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : "");
			eventDetails.setVisibility(event.getEventVisibility() != null ? event.getEventVisibility().name() : "");
			eventDetails.setPublishDate(event.getEventPublishDate() != null ? sdf.format(event.getEventPublishDate()) : "");
			eventDetails.setDiliveryDate(event.getEventDeliveryDate() != null ? ddsdf.format(event.getEventDeliveryDate()) : "N/A");
			eventDetails.setAuctionType(event.getAuctionType() != null ? event.getAuctionType().getValue() : "");
			eventDetails.setExtentionType(event.getTimeExtensionType() != null ? event.getTimeExtensionType().name() : "");

			List<IndustryCategory> industryCategories = rfaEventDao.getIndustryCategoriesForRfaById(event.getId());
			if (CollectionUtil.isNotEmpty(industryCategories)) {
				List<IndustryCategoryPojo> categoryNames = new ArrayList<IndustryCategoryPojo>();
				for (IndustryCategory industryCategory : industryCategories) {
					IndustryCategoryPojo ic = new IndustryCategoryPojo();
					ic.setName(industryCategory.getName());
					categoryNames.add(ic);
				}
				eventDetails.setIndustryCategoryNames(categoryNames);
			}

			boolean siteVisit = rfaMeetingService.isSiteVisitExist(event.getId());
			eventDetails.setSiteVisit(siteVisit);
			// Event Contact Details.
			List<RfaEventContact> eventContacts = getAllContactForEvent(event.getId());
			List<EvaluationContactsPojo> contactList = new ArrayList<EvaluationContactsPojo>();
			if (CollectionUtil.isNotEmpty(eventContacts)) {
				for (RfaEventContact contact : eventContacts) {
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

			List<Bq> bqs = rfaEventBqDao.findRfaBqbyEventId(event.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();
			if (CollectionUtil.isNotEmpty(bqs)) {
				for (Bq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<RfaBqItem> bqItems = rfaBqItemDao.findBqItemsForBq(item.getId());
					List<EvaluationBqItemPojo> evaluationBqItem = new ArrayList<EvaluationBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (RfaBqItem bqItem : bqItems) {
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
			parameters.put("industryCategories", eventDetails.getIndustryCategoryNames());
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(summary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Public RFA Event Summary PDF Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return jasperPrint;
	}

	@Override
	public int updateEventPushedDate(String eventId) {
		return rfaEventDao.updateEventPushedDate(eventId);
	}

	@Override
	public Double getAvarageBidPriceSubmitted(String id) {
		return rfaEventDao.getAvarageBidPriceSubmitted(id);
	}

	@Override
	public List<String> getEventTeamMember(String eventId) {
		return rfaEventDao.getEventTeamMember(eventId);
	}

	@Override
	public List<DraftEventPojo> getAllExcelSearchEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate) {
		return null;
	}

	@Override
	public int updatePrPushDate(String eventId) {
		return rfaEventDao.updatePrPushDate(eventId);
	}

	@Override
	public int updateEventAward(String eventId) {
		return rfaEventDao.updateEventAward(eventId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String eventId, EventStatus status, Date auctionComplitationTime) {
		rfaEventDao.updateImmediately(eventId, status, auctionComplitationTime);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateEventStartMessageFlagImmediately(String eventId) {
		rfaEventDao.updateEventStartMessageFlag(eventId);
	}

	@Override
	public List<RfaEvent> getAllRfaEventByTenantIdInitial(String loggedInUserTenantId, String string) throws SubscriptionException {
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
		List<RfaEvent> list = rfaEventDao.getAllRfaEventByTenantIdInitial(loggedInUserTenantId, string);
		for (RfaEvent rfaEvent : list) {

			if (rfaEvent.getTemplate() != null) {
				if (rfaEvent.getTemplate().getStatus() == Status.INACTIVE) {
					rfaEvent.setTemplateActive(true);
				} else {
					rfaEvent.setTemplateActive(false);
				}
			}
			if (CollectionUtil.isNotEmpty(rfaEvent.getIndustryCategories())) {
				rfaEvent.setIndustryCategory(rfaEvent.getIndustryCategories().get(0));
			}

		}
		return list;
	}

	@Override
	public Integer getCountByEventId(String eventId) {
		return rfaEventDao.getCountByEventId(eventId);
	}

	private String getComasepratedIndustry(List<IndustryCategory> industryCategory) {

		String eventCategories = "";
		if (CollectionUtil.isNotEmpty(industryCategory)) {
			for (IndustryCategory ic : industryCategory) {
				eventCategories += ic.getName() + ",";
			}
			if (eventCategories.length() > 0) {
				eventCategories = eventCategories.substring(0, eventCategories.length() - 1);
			}
		}
		return eventCategories;
	}

	@Override
	public List<DraftEventPojo> getAuctionEventsForAuctionSummaryReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<AuctionType> auctionTypeList, String auctionTypeS) {

		List<DraftEventPojo> returnList = rfaEventDao.getAllAuctionEventsForAuctionSummaryReport(loggedInUserTenantId, input, startDate, endDate, auctionTypeList, auctionTypeS);
		for (DraftEventPojo draftEventPojo : returnList) {
			draftEventPojo.setEventCategories(getComasepratedIndustry(rfaEventDao.getIndustryCategoryListForRfaById(draftEventPojo.getId())));
		}
		return returnList;
	}

	@Override
	public long getAuctionEventsCountForBuyerEventReport(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, List<AuctionType> auctionTypeList, String auctionTypeS) {
		return rfaEventDao.getAuctionEventsCountForBuyerEventReport(loggedInUserTenantId, input, startDate, endDate, auctionTypeList, auctionTypeS);
	}

	@Override
	public long findTotalEventForBuyer(String tenantId, Object userId, List<AuctionType> auctionTypeList, String auctionTypeS) {
		return rfaEventDao.findTotalEventForBuyer(tenantId, userId, auctionTypeList, auctionTypeS);
	}

	@Override
	@SuppressWarnings("unused")
	public List<DraftEventPojo> getAllSearchAuctionEventReportForBuyer(String tenantId, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf, String auctionTypeS) {
		List<DraftEventPojo> eventsData = rfaEventDao.getAllAuctionEventwithSearchFilter(tenantId, eventIds, searchFilterEventPojo, select_all, startDate, endDate, auctionTypeS);
		BigDecimal savingsAmountBudget = BigDecimal.ZERO, savingsAmountHistoric = BigDecimal.ZERO, savingsPercentageBudget = BigDecimal.ZERO,
				savingsPercentageHistoric = BigDecimal.ZERO;

		for (DraftEventPojo draftEventPojo1 : eventsData) {
			if (auctionTypeS.equals("FORWARD")) {
				LOG.info("auctionType---Service IMPL-------->" + auctionTypeS);
				savingsAmountBudget = draftEventPojo1.getSumAwardedPrice().subtract(draftEventPojo1.getBudgetAmount() != null ? draftEventPojo1.getBudgetAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				BigDecimal budgetAmount = BigDecimal.ONE;
				if (draftEventPojo1.getBudgetAmount() != null && draftEventPojo1.getBudgetAmount().floatValue() != 0) {
					budgetAmount = draftEventPojo1.getBudgetAmount();
				}
				savingsPercentageBudget = ((savingsAmountBudget.divide(budgetAmount, 5, RoundingMode.HALF_UP)).multiply(new BigDecimal(100))).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);
				savingsAmountHistoric = draftEventPojo1.getSumAwardedPrice().subtract(draftEventPojo1.getHistricAmount() != null ? draftEventPojo1.getHistricAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				BigDecimal historicAmount = BigDecimal.ONE;
				if (draftEventPojo1.getHistricAmount() != null && draftEventPojo1.getHistricAmount().floatValue() != 0) {
					historicAmount = draftEventPojo1.getHistricAmount();
				}

				savingsPercentageHistoric = ((savingsAmountHistoric.divide(historicAmount, 5, RoundingMode.HALF_UP)).multiply(new BigDecimal(100))).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				draftEventPojo1.setSavingsBudget(savingsAmountBudget != null ? savingsAmountBudget.toString() : "");
				draftEventPojo1.setSavingsBudgetPercentage(savingsPercentageBudget != null ? savingsPercentageBudget.toString() : "");
				draftEventPojo1.setSavingsHistoric(savingsAmountHistoric != null ? savingsAmountHistoric.toString() : "");
				draftEventPojo1.setSavingsHistoricPercentage(savingsPercentageHistoric != null ? savingsPercentageHistoric.toString() : "");
				draftEventPojo1.setBudgetAmount((draftEventPojo1.getBudgetAmount() != null ? draftEventPojo1.getBudgetAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setHistricAmount((draftEventPojo1.getHistricAmount() != null ? draftEventPojo1.getHistricAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setLeadingSuppierBid((draftEventPojo1.getLeadingSuppierBid() != null ? draftEventPojo1.getLeadingSuppierBid() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setSumAwardedPrice((draftEventPojo1.getSumAwardedPrice() != null ? draftEventPojo1.getSumAwardedPrice() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));

				if (draftEventPojo1.getAwardedPrice() != null && draftEventPojo1.getAwardedPrice().trim().length() > 0) {
					String[] values = draftEventPojo1.getAwardedPrice().split(",");
					String rounded = "";
					for (String value : values) {
						LOG.info("Value : " + value.trim());
						rounded += (new BigDecimal(value.trim()).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP)).toString() + ",";
					}
					if (rounded.length() > 0 && rounded.indexOf(',') > 0) {
						rounded = rounded.substring(0, rounded.length() - 1);
					}
					draftEventPojo1.setAwardedPrice(rounded);
				}

			} else {
				LOG.info("auctionType---Service IMPL-------->" + auctionTypeS);

				savingsAmountBudget = draftEventPojo1.getBudgetAmount().subtract(draftEventPojo1.getSumAwardedPrice() != null ? draftEventPojo1.getSumAwardedPrice() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				BigDecimal budgetAmount = BigDecimal.ONE;
				if (draftEventPojo1.getBudgetAmount() != null && draftEventPojo1.getBudgetAmount().floatValue() != 0) {
					budgetAmount = draftEventPojo1.getBudgetAmount();
				}
				savingsPercentageBudget = savingsAmountBudget.divide(budgetAmount, 5, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);
				savingsAmountHistoric = draftEventPojo1.getHistricAmount().subtract(draftEventPojo1.getSumAwardedPrice() != null ? draftEventPojo1.getSumAwardedPrice() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				BigDecimal historicAmount = BigDecimal.ONE;
				if (draftEventPojo1.getHistricAmount() != null && draftEventPojo1.getHistricAmount().floatValue() != 0) {
					historicAmount = draftEventPojo1.getHistricAmount();
				}
				savingsPercentageHistoric = ((savingsAmountHistoric.divide(historicAmount, 5, RoundingMode.HALF_UP)).multiply(new BigDecimal(100))).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				draftEventPojo1.setSavingsBudget(savingsAmountBudget != null ? savingsAmountBudget.toString() : "");
				draftEventPojo1.setSavingsBudgetPercentage(savingsPercentageBudget != null ? savingsPercentageBudget.toString() : "");
				draftEventPojo1.setSavingsHistoric(savingsAmountHistoric != null ? savingsAmountHistoric.toString() : "");
				draftEventPojo1.setSavingsHistoricPercentage(savingsPercentageHistoric != null ? savingsPercentageHistoric.toString() : "");
				draftEventPojo1.setBudgetAmount((draftEventPojo1.getBudgetAmount() != null ? draftEventPojo1.getBudgetAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setHistricAmount((draftEventPojo1.getHistricAmount() != null ? draftEventPojo1.getHistricAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setLeadingSuppierBid((draftEventPojo1.getLeadingSuppierBid() != null ? draftEventPojo1.getLeadingSuppierBid() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setSumAwardedPrice((draftEventPojo1.getSumAwardedPrice() != null ? draftEventPojo1.getSumAwardedPrice() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));

				if (draftEventPojo1.getAwardedPrice() != null && draftEventPojo1.getAwardedPrice().trim().length() > 0) {
					String[] values = draftEventPojo1.getAwardedPrice().split(",");
					String rounded = "";
					for (String value : values) {
						LOG.info("Value : " + value.trim());
						rounded += (new BigDecimal(value.trim()).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP)).toString() + ",";
					}
					if (rounded.length() > 0 && rounded.indexOf(',') > 0) {
						rounded = rounded.substring(0, rounded.length() - 1);
					}
					draftEventPojo1.setAwardedPrice(rounded);
				}
			}
		}
		return eventsData;
	}

	@Override
	public Event loadRfaEventModalById(String eventId, Model model, SimpleDateFormat format, List<User> assignedTeamMembers, List<User> approvalUsers, List<User> suspApprovalUsers) throws CloneNotSupportedException, ApplicationException {
		RfaEvent event = rfaEventDao.findByEventId(eventId);
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
			if (event.getCostCenter() != null) {
				event.getCostCenter().getCostCenter();
			}
			if (event.getBusinessUnit() != null) {
				event.getBusinessUnit().getUnitName();
			}
			if (event.getUnMaskedUser() != null) {
				event.getUnMaskedUser().getName();
				model.addAttribute("unMaskUser", event.getUnMaskedUser());
			}

			if (CollectionUtil.isNotEmpty(event.getUnMaskedUsers())) {
				for (RfaUnMaskedUser um : event.getUnMaskedUsers()) {
					if (um.getUser() != null) {
						um.getUser().getLoginId();
					}
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEvaluationConclusionUsers())) {
				for (RfaEvaluationConclusionUser user : event.getEvaluationConclusionUsers()) {
					user.getUser().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				for (RfaEventContact contact : event.getEventContacts()) {
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
				for (RfaTeamMember rfaTeamMember : event.getTeamMembers()) {
					rfaTeamMember.getUser().getName();
					assignedTeamMembers.add((User) rfaTeamMember.getUser().clone());
				}
			}

			// List<User> userList = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(event.getApprovals())) {
				for (RfaEventApproval approver : event.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfaApprovalUser user : approver.getApprovalUsers()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();

							// PH-2124 #34 Suspended Event -> Approver name is duplicated.
							User us = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());

							if (!approvalUsers.contains(us)) {
								approvalUsers.add(us);
							}
						}
					}
				}
			}

			// List<User> suspApprvlUserList = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RfaEventSuspensionApproval approver : event.getSuspensionApprovals()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsers())) {
						for (RfaSuspensionApprovalUser user : approver.getApprovalUsers()) {
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
			buildRfaEventDetailPage(model, eventId, event, null);
		}
		return event;
	}

	private void buildRfaEventDetailPage(Model model, String eventId, Event event, List<User> userList) throws CloneNotSupportedException, ApplicationException {
		Date eventStartDate = event.getEventStart();
		Date eventEndDate = event.getEventEnd();

		Date eventPublishDate = event.getEventPublishDate();
		((RfaEvent) (event)).setEventStartTime(eventStartDate);
		((RfaEvent) (event)).setEventEndTime(eventEndDate);
		((RfaEvent) (event)).setEventPublishTime(eventPublishDate);
		if (((RfaEvent) (event)).getMinimumSupplierRating() != null) {
			((RfaEvent) (event)).setMinimumSupplierRating(((RfaEvent) (event)).getMinimumSupplierRating());
		}
		if (((RfaEvent) (event)).getMaximumSupplierRating() != null) {
			((RfaEvent) (event)).setMaximumSupplierRating(((RfaEvent) (event)).getMaximumSupplierRating());
		}
		RfaEventContact eventAContact = new RfaEventContact();
		eventAContact.setRfaEvent((RfaEvent) event);
		eventAContact.setEventId(event.getId());
		model.addAttribute("eventContact", eventAContact);
		// model.addAttribute("userList1", userList);
		model.addAttribute("eventContactsList", ((RfaEvent) event).getEventContacts());
		model.addAttribute("reminderList", rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.FALSE));
		model.addAttribute("startReminderList", rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.TRUE));
		model.addAttribute("auctionType", ((RfaEvent) event).getAuctionType());
	}

	@Override
	public Event getSimpleEventDetailsById(String eventId) {
		return rfaEventDao.getSimpleEventDetailsById(eventId);
	}

	@Override
	public RfaEvent getPlainEventByFormattedEventIdAndTenantId(String eventId, String tenantId) {
		return rfaEventDao.getPlainEventByFormattedEventIdAndTenantId(eventId, tenantId);
	}

	@Override
	public XSSFWorkbook buildBqComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse response, String eventId, String evelopId, RfxTypes eventType) throws IOException {
		LOG.info("Event Id " + eventId);
		Event event = null;
		Envelop envolope = null;
		switch (eventType) {
		case RFA:
			event = rfaEventService.getRfaEventByeventId(eventId);
			envolope = rfaEnvelopService.getRfaEnvelopById(evelopId);
			break;
		case RFT:
			event = rftEventService.getRftEventById(eventId);
			envolope = rftEnvelopService.getRftEnvelopById(evelopId);
			break;
		case RFP:
			event = rfpEventService.getRfpEventByeventId(eventId);
			envolope = rfpEnvelopService.getRfpEnvelopById(evelopId);
			break;
		case RFQ:
			event = rfqEventService.getEventById(eventId);
			envolope = rfqEnvelopService.getRfqEnvelopById(evelopId);
			break;
		case RFI:
			event = rfiEventService.getRfiEventByeventId(eventId);
			envolope = rfiEnvelopService.getRfiEnvelopById(evelopId);
			break;
		default:
			break;
		}
		if (CollectionUtil.isNotEmpty(list)) {
			int k = 1;
			for (EventEvaluationPojo evaluationPojo : list) {
				LOG.info("decimal :" + evaluationPojo.getDecimal() + " With Tax : " + evaluationPojo.getWithTax());
				// For Financial Standard
				DecimalFormat df = null;
				if (evaluationPojo.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (evaluationPojo.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (evaluationPojo.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (evaluationPojo.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (evaluationPojo.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (evaluationPojo.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				}
				XSSFSheet sheet = workbook.createSheet("BQ price comparison" + k);
				XSSFRow rowHeading = sheet.createRow(0);

				XSSFFont font = workbook.createFont();
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

				XSSFCellStyle headingAlignLeft = workbook.createCellStyle();
				headingAlignLeft.setFont(font);
				headingAlignLeft.setAlignment(HorizontalAlignment.LEFT);
				headingAlignLeft.setVerticalAlignment(VerticalAlignment.CENTER);

				XSSFCellStyle headingAlignRight = workbook.createCellStyle();
				headingAlignRight.setFont(font);
				headingAlignRight.setVerticalAlignment(VerticalAlignment.CENTER);
				headingAlignRight.setAlignment(HorizontalAlignment.RIGHT);

				XSSFCellStyle headingAlignCenter = workbook.createCellStyle();
				headingAlignCenter.setFont(font);
				headingAlignCenter.setAlignment(HorizontalAlignment.CENTER);
				headingAlignCenter.setVerticalAlignment(VerticalAlignment.CENTER);

				XSSFCellStyle textAlignLeft = workbook.createCellStyle();
				textAlignLeft.setAlignment(HorizontalAlignment.LEFT);
				textAlignLeft.setVerticalAlignment(VerticalAlignment.CENTER);

				XSSFCellStyle textAlignRight = workbook.createCellStyle();
				textAlignRight.setVerticalAlignment(VerticalAlignment.CENTER);
				textAlignRight.setAlignment(HorizontalAlignment.RIGHT);

				int r = 1;
				short[] arr = { IndexedColors.LIGHT_ORANGE.getIndex(), IndexedColors.LIGHT_GREEN.getIndex(), IndexedColors.PINK.getIndex(), IndexedColors.LIGHT_TURQUOISE.getIndex(), IndexedColors.LIGHT_YELLOW.getIndex(), IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex() };
				int colorIndex = 0;
				int i = 5;

				colorIndex = 0;
				for (Supplier column : evaluationPojo.getColumns()) {
					XSSFCell cell = null;
					int cellFirstMerge = 0;
					int lastCellForMerge = 0;
					cellFirstMerge = i;
					i = i + 2;
					if (Boolean.TRUE == evaluationPojo.getWithTax()) {
						i = i + 2;
					}
					lastCellForMerge = i - 1;
					sheet.addMergedRegion(new CellRangeAddress(0, 0, cellFirstMerge, lastCellForMerge));
					cell = rowHeading.createCell(cellFirstMerge);
					XSSFCellStyle styleHeadingb = workbook.createCellStyle();
					styleHeadingb.setFont(font);
					styleHeadingb.setAlignment(HorizontalAlignment.CENTER);
					styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
					styleHeadingb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						cell.setCellValue(MaskUtils.maskName(envolope.getPreFix(), column.getId(), envolope.getId()));
					} else {
						cell.setCellValue(column.getCompanyName());
					}

					cell.setCellStyle(styleHeadingb);
					colorIndex++;

					for (int p = cellFirstMerge + 1; p <= lastCellForMerge; p++) {
						cell = rowHeading.createCell(p);
						cell.setCellValue("");
						cell.setCellStyle(styleHeadingb);
					}

					cellFirstMerge = i;
				}

				i = 0;

				XSSFRow row = sheet.createRow(r++);
				XSSFCell cell = row.createCell(i++);
				cell.setCellValue("No");
				cell.setCellStyle(headingAlignLeft);

				cell = row.createCell(i++);
				cell.setCellValue("Item");
				cell.setCellStyle(headingAlignLeft);

				cell = row.createCell(i++);
				cell.setCellValue("Description");
				cell.setCellStyle(headingAlignLeft);

				cell = row.createCell(i++);
				cell.setCellValue("UOM");
				cell.setCellStyle(headingAlignLeft);

				cell = row.createCell(i++);
				cell.setCellValue("Quantity");
				cell.setCellStyle(headingAlignRight);

				List<Supplier> columns = evaluationPojo.getColumns();
				colorIndex = 0;
				for (int j = 0; j < columns.size(); j++) {
					XSSFCellStyle styleHeadingAlign = workbook.createCellStyle();
					styleHeadingAlign.setFont(font);
					styleHeadingAlign.setFillForegroundColor(arr[colorIndex % 6]);
					styleHeadingAlign.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					styleHeadingAlign.setVerticalAlignment(VerticalAlignment.CENTER);
					styleHeadingAlign.setAlignment(HorizontalAlignment.RIGHT);

					cell = row.createCell(i++);
					cell.setCellValue("Unit Price");
					cell.setCellStyle(styleHeadingAlign);

					cell = row.createCell(i++);
					cell.setCellValue("Amount");
					cell.setCellStyle(styleHeadingAlign);

					if (Boolean.TRUE == evaluationPojo.getWithTax()) {
						cell = row.createCell(i++);
						cell.setCellValue("Tax");
						cell.setCellStyle(styleHeadingAlign);

						cell = row.createCell(i++);
						cell.setCellValue("Amount Inc Tax");
						cell.setCellStyle(styleHeadingAlign);
					}

					colorIndex++;
				}

				for (List<String> data : evaluationPojo.getData()) {
					row = sheet.createRow(r++);
					int cellNum = 0;
					for (String answers : data) {
						if (cellNum <= 3) {
							row.createCell(cellNum++).setCellValue(answers);
						} else {
							if (answers != null && StringUtils.checkString(answers).length() > 0) {
								XSSFCell cell1 = row.createCell(cellNum++);
								cell1.setCellValue(StringUtils.checkString(answers).length() > 0 ? df.format(new BigDecimal(answers)) : ""); // cells
																																				// data
								cell1.setCellStyle(textAlignRight);

							} else {
								row.createCell(cellNum++).setCellValue(answers);
							}
						}
					}
				}

				r++;
				int cellNum = 4;
				XSSFRow totalRow = sheet.createRow(r++);
				XSSFCell totalCell = totalRow.createCell(cellNum++);
				totalCell.setCellValue("Sub Total");
				totalCell.setCellStyle(textAlignLeft);
				for (BigDecimal score : evaluationPojo.getTotalAmount()) {
					totalCell = totalRow.createCell(cellNum++);
					totalCell.setCellValue(score != null ? df.format(score) : "");
					totalCell.setCellStyle(textAlignRight);
					if (score == null)
						totalCell.setCellType(Cell.CELL_TYPE_BLANK);
				}

				if (Boolean.TRUE == evaluationPojo.getWithTax()) {
					cellNum = 4;
					totalRow = sheet.createRow(r++);
					totalCell = totalRow.createCell(cellNum++);
					totalCell.setCellValue("Additional Tax");
					totalCell.setCellStyle(textAlignLeft);
					for (String taxInfo : evaluationPojo.getAddtionalTaxInfo()) {
						LOG.info("taxInfo   :  " + taxInfo);
						totalCell = totalRow.createCell(cellNum++);
						if (taxInfo == null || StringUtils.checkString(taxInfo).length() == 0) {
							totalCell.setCellType(Cell.CELL_TYPE_BLANK);
						} else {
							totalCell.setCellValue(StringUtils.checkString(taxInfo));
							totalCell.setCellStyle(textAlignRight);
						}
					}
				}

				cellNum = 4;
				totalRow = sheet.createRow(r++);
				totalCell = totalRow.createCell(cellNum++);
				totalCell.setCellValue("Grand Total (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + ")");
				totalCell.setCellStyle(headingAlignLeft);
				for (BigDecimal score : evaluationPojo.getGrandTotals()) {
					totalCell = totalRow.createCell(cellNum++);
					totalCell.setCellValue(score != null ? df.format(score) : "");
					totalCell.setCellStyle(headingAlignRight);
					if (score == null)
						totalCell.setCellType(Cell.CELL_TYPE_BLANK);
				}

				for (int columnPosition = 0; columnPosition < 50; columnPosition++) {
					sheet.autoSizeColumn((short) (columnPosition));
				}
				if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading()) || CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
					createSheet(workbook, font, headingAlignLeft, headingAlignRight, evaluationPojo, colorIndex, arr, k);
				}
				k++;

			}

			if (response != null) {

				String downloadFolder = System.getProperty("user.home");
				String fileName = "BqComparisonTable.xlsx";
				FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
				workbook.write(out);
				out.close();
				Path file = Paths.get(downloadFolder, fileName);

				if (Files.exists(file)) {
					response.setContentType("application/vnd.ms-excel");
					response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
					try {
						Files.copy(file, response.getOutputStream());
						response.getOutputStream().flush();
					} catch (IOException e) {
						LOG.error("Error :- " + e.getMessage());
					}
				}
			}
		}
		return workbook;

	}


	@Override
	public XSSFWorkbook buildSorComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse response, String eventId, String evelopId, RfxTypes eventType) throws IOException {
		LOG.info("Event Id " + eventId);

		Event event = null;
		Envelop envolope = null;
		switch (eventType) {
			case RFA:
				event = rfaEventService.getRfaEventByeventId(eventId);
				envolope = rfaEnvelopService.getRfaEnvelopById(evelopId);
				break;
			case RFT:
				event = rftEventService.getRftEventById(eventId);
				envolope = rftEnvelopService.getRftEnvelopById(evelopId);
				break;
			case RFP:
				event = rfpEventService.getRfpEventByeventId(eventId);
				envolope = rfpEnvelopService.getRfpEnvelopById(evelopId);
				break;
			case RFQ:
				event = rfqEventService.getEventById(eventId);
				envolope = rfqEnvelopService.getRfqEnvelopById(evelopId);
				break;
			case RFI:
				event = rfiEventService.getRfiEventByeventId(eventId);
				envolope = rfiEnvelopService.getRfiEnvelopById(evelopId);
				break;
			default:
				break;
		}
		if (CollectionUtil.isNotEmpty(list)) {
			int k = 1;
			for (EventEvaluationPojo evaluationPojo : list) {
				LOG.info("decimal :" + evaluationPojo.getDecimal() + " With Tax : " + evaluationPojo.getWithTax());
				DecimalFormat df = null;
				df = new DecimalFormat("#,###,###,##0.0");
				XSSFSheet sheet = workbook.createSheet("SOR comparison" + k);
				XSSFRow rowHeading = sheet.createRow(0);
				//k++;

				XSSFFont font = workbook.createFont();
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);

				XSSFCellStyle headingAlignLeft = workbook.createCellStyle();
				headingAlignLeft.setFont(font);
				headingAlignLeft.setAlignment(HorizontalAlignment.LEFT);
				headingAlignLeft.setVerticalAlignment(VerticalAlignment.CENTER);

				XSSFCellStyle headingAlignRight = workbook.createCellStyle();
				headingAlignRight.setFont(font);
				headingAlignRight.setVerticalAlignment(VerticalAlignment.CENTER);
				headingAlignRight.setAlignment(HorizontalAlignment.RIGHT);

				XSSFCellStyle headingAlignCenter = workbook.createCellStyle();
				headingAlignCenter.setFont(font);
				headingAlignCenter.setAlignment(HorizontalAlignment.CENTER);
				headingAlignCenter.setVerticalAlignment(VerticalAlignment.CENTER);

				XSSFCellStyle textAlignLeft = workbook.createCellStyle();
				textAlignLeft.setAlignment(HorizontalAlignment.LEFT);
				textAlignLeft.setVerticalAlignment(VerticalAlignment.CENTER);

				XSSFCellStyle textAlignRight = workbook.createCellStyle();
				textAlignRight.setVerticalAlignment(VerticalAlignment.CENTER);
				textAlignRight.setAlignment(HorizontalAlignment.RIGHT);

				int r = 1;
				short[] arr = { IndexedColors.LIGHT_ORANGE.getIndex(), IndexedColors.LIGHT_GREEN.getIndex(), IndexedColors.PINK.getIndex(), IndexedColors.LIGHT_TURQUOISE.getIndex(), IndexedColors.LIGHT_YELLOW.getIndex(), IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex() };
				int colorIndex = 0;
				int i = 4;

				colorIndex = 0;

				if(evaluationPojo.getColumns() != null) {
					for (Supplier column : evaluationPojo.getColumns()) {
						XSSFCell cell = null;
						int cellFirstMerge = 0;
						int lastCellForMerge = 0;
						cellFirstMerge = i;
						i = i + 1;
						lastCellForMerge = i - 1;
						sheet.addMergedRegion(new CellRangeAddress(0, 0, cellFirstMerge, lastCellForMerge));
						cell = rowHeading.createCell(cellFirstMerge);
						XSSFCellStyle styleHeadingb = workbook.createCellStyle();
						styleHeadingb.setFont(font);
						styleHeadingb.setAlignment(HorizontalAlignment.CENTER);
						styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
						styleHeadingb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
							cell.setCellValue(MaskUtils.maskName(envolope.getPreFix(), column.getId(), envolope.getId()));
						} else {
							cell.setCellValue(column.getCompanyName());
						}

						cell.setCellStyle(styleHeadingb);
						colorIndex++;

						for (int p = cellFirstMerge + 1; p <= lastCellForMerge; p++) {
							cell = rowHeading.createCell(p);
							cell.setCellValue("");
							cell.setCellStyle(styleHeadingb);
						}

						cellFirstMerge = i;
					}
				}

				i = 0;

				XSSFRow row = sheet.createRow(r++);
				XSSFCell cell = row.createCell(i++);
				cell.setCellValue("No");
				cell.setCellStyle(headingAlignLeft);

				cell = row.createCell(i++);
				cell.setCellValue("Item");
				cell.setCellStyle(headingAlignLeft);

				cell = row.createCell(i++);
				cell.setCellValue("Description");
				cell.setCellStyle(headingAlignLeft);

				cell = row.createCell(i++);
				cell.setCellValue("UOM");
				cell.setCellStyle(headingAlignLeft);

				List<Supplier> columns = evaluationPojo.getColumns();

				colorIndex = 0;
				if(columns != null) {
					for (int j = 0; j < columns.size(); j++) {
						XSSFCellStyle styleHeadingAlign = workbook.createCellStyle();
						styleHeadingAlign.setFont(font);
						styleHeadingAlign.setFillForegroundColor(arr[colorIndex % 6]);
						styleHeadingAlign.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						styleHeadingAlign.setVerticalAlignment(VerticalAlignment.CENTER);
						styleHeadingAlign.setAlignment(HorizontalAlignment.RIGHT);

						cell = row.createCell(i++);
						cell.setCellValue("Rate");
						cell.setCellStyle(styleHeadingAlign);
						colorIndex++;
					}
				}

				if(evaluationPojo.getData() != null) {
					for (List<String> data : evaluationPojo.getData()) {
						row = sheet.createRow(r++);
						int cellNum = 0;
						for (String answers : data) {
							if (cellNum <= 3) {
								row.createCell(cellNum++).setCellValue(answers);
							} else {
								if (answers != null && StringUtils.checkString(answers).length() > 0) {
									XSSFCell cell1 = row.createCell(cellNum++);
									cell1.setCellValue(StringUtils.checkString(answers).length() > 0 ? df.format(new BigDecimal(answers)) : ""); // cells
									// data
									cell1.setCellStyle(textAlignRight);

								} else {
									row.createCell(cellNum++).setCellValue(answers);
								}
							}
						}
					}
				}
				k++;
			}

			if (response != null) {

				String downloadFolder = System.getProperty("user.home");
				String fileName = "SorComparisonTable.xlsx";
				FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
				workbook.write(out);
				out.close();
				Path file = Paths.get(downloadFolder, fileName);

				if (Files.exists(file)) {
					response.setContentType("application/vnd.ms-excel");
					response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
					try {
						Files.copy(file, response.getOutputStream());
						response.getOutputStream().flush();
					} catch (IOException e) {
						LOG.error("Error :- " + e.getMessage());
					}
				}
			}
		}
		return workbook;

	}

	private void createSheet(XSSFWorkbook workbook, XSSFFont font, XSSFCellStyle headingAlignLeft, XSSFCellStyle headingAlignRight, EventEvaluationPojo evaluationPojo, int colorIndex, short[] arr, int index) {

		XSSFSheet sheet2 = workbook.createSheet("BQ non-price comparison " + index);

		int r2 = 1;
		int i2 = 0;
		XSSFRow row2 = sheet2.createRow(r2++);
		XSSFCell cell2 = row2.createCell(i2++);

		cell2.getCellStyle().setAlignment(HorizontalAlignment.LEFT);
		cell2.setCellValue("No");
		cell2.setCellStyle(headingAlignLeft);

		cell2 = row2.createCell(i2++);
		cell2.setCellValue("Item");
		cell2.setCellStyle(headingAlignLeft);

		cell2 = row2.createCell(i2++);
		cell2.setCellValue("Description");
		cell2.setCellStyle(headingAlignLeft);

		Cell cell21 = row2.createCell(i2++);
		cell21.setCellValue("UOM");
		cell21.setCellStyle(headingAlignLeft);

		cell2 = row2.createCell(i2++);
		cell2.setCellValue("Quantity");
		cell2.setCellStyle(headingAlignRight);

		LOG.info("--------------------------------------------------------");

		if (evaluationPojo.getBqNonPriceComprision() != null) {
			if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
				for (String buyerHeading : evaluationPojo.getBqNonPriceComprision().getBuyerHeading()) {
					XSSFCellStyle styleHeadingb = workbook.createCellStyle();
					XSSFFont font1 = workbook.createFont();
					styleHeadingb.setFont(font1);
					font1.setColor(IndexedColors.WHITE.getIndex());
					font1.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
					styleHeadingb.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
					styleHeadingb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell2 = row2.createCell(i2++);
					cell2.setCellValue(buyerHeading);
					cell2.setCellStyle(styleHeadingb);

				}
			}
			colorIndex = 0;
			LOG.info("i2 after printing buyer header count" + i2);
			if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierName())) {
				for (int supp = 0; supp < evaluationPojo.getBqNonPriceComprision().getSupplierName().size(); supp++) {
					XSSFCellStyle styleHeadingb = workbook.createCellStyle();
					styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
					styleHeadingb.setFont(font);
					styleHeadingb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
						for (String supplierHeading : evaluationPojo.getBqNonPriceComprision().getSupplierHeading()) {
							cell2 = row2.createCell(i2++);
							cell2.setCellValue(supplierHeading);
							cell2.setCellStyle(styleHeadingb);
						}
					}
					colorIndex++;
				}
			}
		}

		LOG.info("i2 after printing supplier header count" + i2);
		int storeI2ForSupplier = i2;

		LOG.info("storeI2ForSupplier : " + storeI2ForSupplier);
		int storeCellNum1 = 0;
		int storeCellNum1ForSupplier = 0;
		int storeIndex = 0;

		XSSFCellStyle textAlignRight = workbook.createCellStyle();
		textAlignRight.setVerticalAlignment(VerticalAlignment.CENTER);
		textAlignRight.setAlignment(HorizontalAlignment.RIGHT);

		for (List<String> data : evaluationPojo.getData()) {
			row2 = sheet2.createRow(r2++);
			int cellNum1 = 0;
			for (String answers : data) {
				if (cellNum1 <= 4) {
					XSSFCell cellQuantity = row2.createCell(cellNum1++);
					cellQuantity.setCellValue(answers);
					if (cellNum1 == 5) {
						cellQuantity.setCellStyle(textAlignRight);
					}

					// row2.createCell(cellNum1++).setCellValue(answers);
					storeCellNum1 = cellNum1;
				}

			}

		}
		int rowIndex = 2;
		if (evaluationPojo.getBqNonPriceComprision() != null) {
			if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
				for (List<String> buyerDataList : evaluationPojo.getBqNonPriceComprision().getBuyerFeildData()) {
					row2 = sheet2.getRow(rowIndex++);
					int buyerCell = storeCellNum1;
					for (String data : buyerDataList) {
						row2.createCell(buyerCell++).setCellValue(data);
						storeCellNum1ForSupplier = buyerCell;
					}
				}
			} else {
				storeCellNum1ForSupplier = storeCellNum1;
			}
		}

		LOG.info("==storeIndex==" + storeIndex);

		rowIndex = 2;
		if (evaluationPojo.getBqNonPriceComprision().getSupplierData() != null && evaluationPojo.getBqNonPriceComprision().getSupplierData().size() > 0) {
			for (Entry<String, List<String>> entry : evaluationPojo.getBqNonPriceComprision().getSupplierData().entrySet()) {
				row2 = sheet2.getRow(rowIndex++);
				int cellNumber = storeCellNum1ForSupplier;
				for (String supplierData : entry.getValue()) {
					row2.createCell(cellNumber++).setCellValue(supplierData);

				}
			}
		}
		row2 = sheet2.getRow(0);
		if (row2 == null) {
			row2 = sheet2.createRow(0);
		}

		if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
			for (int colcount = 5; colcount < (5 + evaluationPojo.getBqNonPriceComprision().getBuyerHeading().size()); colcount++) {
				XSSFCellStyle styleHeadingb = workbook.createCellStyle();
				styleHeadingb.setFont(font);
				styleHeadingb.setAlignment(HorizontalAlignment.CENTER);
				styleHeadingb.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
				styleHeadingb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				cell2 = row2.createCell(colcount);
				cell2.setCellValue("");
				cell2.setCellStyle(styleHeadingb);
			}

		}

		int fixedheadingCount = 4;
		int buyerheadingCount = 0;
		if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
			buyerheadingCount = evaluationPojo.getBqNonPriceComprision().getBuyerHeading().size();
		}
		int supplierheadingCount = 0;
		if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
			supplierheadingCount = evaluationPojo.getBqNonPriceComprision().getSupplierHeading().size();
		}

		colorIndex = 0;
		int cellNub = fixedheadingCount + buyerheadingCount + 1;

		if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
			if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierName())) {
				for (String supName : evaluationPojo.getBqNonPriceComprision().getSupplierName()) {
					LOG.info("cellNub" + cellNub);
					XSSFCellStyle styleHeadingb = workbook.createCellStyle();
					styleHeadingb.setFont(font);
					styleHeadingb.setAlignment(HorizontalAlignment.CENTER);
					styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
					styleHeadingb.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					sheet2.addMergedRegion(new CellRangeAddress(0, 0, cellNub, cellNub + supplierheadingCount - 1));
					cell2 = row2.createCell(cellNub);
					cell2.setCellValue(supName);
					cell2.setCellStyle(styleHeadingb);
					// row2.createCell(cellNub).setCellValue(supName);
					cellNub = cellNub + supplierheadingCount;
					colorIndex++;
				}
			}
		}

		for (int columnPosition = 0; columnPosition < 50; columnPosition++) {
			sheet2.autoSizeColumn((short) (columnPosition));
		}
	}

	@Override
	public XSSFWorkbook buildCqComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse response) throws IOException {
		if (CollectionUtil.isNotEmpty(list)) {
			for (EventEvaluationPojo evaluationPojo : list) {
				XSSFSheet sheet = workbook.createSheet();
				XSSFRow rowHeading = sheet.createRow(0);
				XSSFCellStyle styleHeading = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				styleHeading.setFont(font);
				// styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
				styleHeading.setAlignment(HorizontalAlignment.CENTER);
				int r = 1;

				int i = 3;
				int cellMerge = 2;
				for (Supplier column : evaluationPojo.getColumns()) {
					cellMerge++;
					XSSFCell cell = rowHeading.createCell(i++);
					cell.setCellValue(column.getCompanyName());
					cell.setCellStyle(styleHeading);
					cell = rowHeading.createCell(i++);
					cell.setCellStyle(styleHeading);
					sheet.addMergedRegion(new CellRangeAddress(0, 0, cellMerge, ++cellMerge));
				}

				i = 0;
				XSSFRow row = sheet.createRow(r++);
				XSSFCell cell = row.createCell(i++);
				cell.setCellValue("No");
				// cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Question");
				// cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Scoring Scale");
				// cell.setCellStyle(styleHeading);

				List<Supplier> columns = evaluationPojo.getColumns();
				for (int j = 0; j < columns.size(); j++) {
					cell = row.createCell(i++);
					cell.setCellValue("Answer");
					cell.getCellStyle().setWrapText(true);
					// cell.setCellStyle(styleHeading);
					cell = row.createCell(i++);
					cell.setCellValue("Score");
					// cell.setCellStyle(styleHeading);
				}

				for (List<String> data : evaluationPojo.getData()) {
					row = sheet.createRow(r++);
					int cellNum = 0;
					for (String answers : data) {
						Cell anserwCell = row.createCell(cellNum++);
						anserwCell.setCellValue(answers);
						anserwCell.getCellStyle().setWrapText(true);
					}
				}

				XSSFCellStyle alignCenter = workbook.createCellStyle();
				alignCenter.setAlignment(HorizontalAlignment.LEFT);

				XSSFCellStyle alignRight = workbook.createCellStyle();
				alignRight.setAlignment(HorizontalAlignment.RIGHT);

				r++;
				int cellNum = 1;
				XSSFRow totalRow = sheet.createRow(r++);
				cell = totalRow.createCell(cellNum++);
				cell.setCellValue("Total Scoring");
				cell.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
				for (String score : evaluationPojo.getScoring()) {
					if (cellNum == 2) {
						cell = totalRow.createCell(cellNum++);
						cell.setCellValue(score);
						cell.setCellStyle(alignCenter);
					} else {
						cell = totalRow.createCell(cellNum++);
						cell = totalRow.createCell(cellNum++);
						cell.setCellValue(score);
						cell.setCellStyle(alignRight);
					}

				}

				for (int columnPosition = 0; columnPosition < 25; columnPosition++) {
					sheet.autoSizeColumn((short) (columnPosition));
				}
			}

			if (response != null) {

				String downloadFolder = System.getProperty("user.home");
				String fileName = "CqComparisonTable.xlsx";
				FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
				workbook.write(out);
				out.close();
				Path file = Paths.get(downloadFolder, fileName);

				if (Files.exists(file)) {
					response.setContentType("application/vnd.ms-excel");
					response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
					try {
						Files.copy(file, response.getOutputStream());
						response.getOutputStream().flush();
					} catch (IOException e) {
						LOG.error("Error :- " + e.getMessage());
					}
				}
			}
		}
		return workbook;
	}

	@Override
	public List<RfaEventAwardAudit> getRfaEventAwardByEventId(String eventId) {
		return rfaEventDao.getRfaEventAwardByEventId(eventId);
	}

	@Override
	public JasperPrint getEventAuditPdf(RfaEvent event, User loggedInUser, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		event = rfaEventDao.findByEventId(event.getId());
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
			eventDetails.setType("RFA");
			List<EvaluationAuditPojo> auditList = new ArrayList<EvaluationAuditPojo>();
			List<RfaEventAudit> eventAudit = rfaEventAuditDao.getRfaEventAudit(event.getId());
			if (CollectionUtil.isNotEmpty(eventAudit)) {
				for (RfaEventAudit ra : eventAudit) {
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
		return rfaEventDao.isDefaultPreSetEnvlope(eventId);
	}

	@Override
	public RfaSupplierBq getSupplierBQOfHighestTotalPrice(String eventId, String bqId, String loggedInUserTenantId) {
		RfaSupplierBq rfaSupplierBq = rfaEventDao.getSupplierBQOfHighestTotalPrice(eventId, bqId);
		if (rfaSupplierBq != null) {

			if (rfaSupplierBq.getSupplierBqItems() != null) {
				for (RfaSupplierBqItem items : rfaSupplierBq.getSupplierBqItems()) {
					items.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(loggedInUserTenantId, items.getSupplier().getId()));
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
		return rfaSupplierBq;
	}

	@Override
	public RfaSupplierBq getSupplierBQOfHighestItemisedPrice(String eventId, String bqId, String loggedInUserTenantId) {
		RfaSupplierBq rfaSupplierBq = new RfaSupplierBq();
		List<RfaSupplierBqItem> bqItemList = new ArrayList<RfaSupplierBqItem>();
		List<RfaSupplierBq> suppBqList = rfaEventDao.getSupplierBQOfHighestItemisedPrize(eventId, bqId);
		if (CollectionUtil.isNotEmpty(suppBqList)) {
			int bqItemCount = 1;
			for (RfaSupplierBq bq : suppBqList) {
				if (CollectionUtil.isNotEmpty(bq.getSupplierBqItems())) {
					for (RfaSupplierBqItem item : bq.getSupplierBqItems()) {
						// LOG.info(item.getBqItem().getId());
						if (item.getBqItem() != null) {
							item.setProductCategoryList(favoriteSupplierDao.getSupplierProductCategoryBySupIdORTenantId(loggedInUserTenantId, item.getSupplier().getId()));
							if (item.getBqItem().getOrder() == 0) {
								if (1 == bqItemCount) {
									bqItemList.add(item);
								}

							} else {
								RfaSupplierBqItem bqItem = rfaEventDao.getMaxItemisePrice(item.getBqItem().getId(), eventId);
								if (bqItem != null && !bqItemList.contains(bqItem)) {
									bqItemList.add(bqItem);
									rfaSupplierBq.setBq(bq.getBq());
								}
							}

						}
					}
					bqItemCount++;
				}
				rfaSupplierBq.setSupplierBqItems(bqItemList);

			}

		}

		return rfaSupplierBq;
	}

	@Override
	public void sendEvaluationCompletedPrematurelyNotification(User actionBy, Event event) {
		String url = APP_URL + "/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
		String msg = "\"" + actionBy.getName() + "\" has concluded evaluation prematurely for \"" + event.getEventName() + "\"";
		String timeZone = "GMT+8:00";
		String subject = "Evaluation Concluded Prematurely";

		User rfaEventOwner = rfaEventDao.getPlainEventOwnerByEventId(event.getId());
		timeZone = getTimeZoneByBuyerSettings(rfaEventOwner, timeZone);

		// Send to event owner
		sendNotification(event, url, msg, rfaEventOwner, timeZone, subject);

		// send to Associate Owners
		List<EventTeamMember> teamMembers = rfaEventDao.getPlainTeamMembersForEvent(event.getId());
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
			map.put("eventType", RfxTypes.RFA.getValue());
			map.put("businessUnit", StringUtils.checkString(rfaEventDao.findBusinessUnitName(event.getId())));
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

	@Override
	public RfaEvaluationConclusionUser getEvaluationConclusionUserAttachment(String eventId, String evalConUserId) {
		RfaEvaluationConclusionUser usr = rfaEventDao.getEvaluationConclusionUserAttachment(eventId, evalConUserId);
		if (usr != null && usr.getFileData() != null) {
			@SuppressWarnings("unused")
			long len = usr.getFileData().length;
		}
		return usr;
	}

	@Override
	public EventPermissions getUserEventPemissions(String userId, String eventId) {
		return rfaEventDao.getUserEventPemissions(userId, eventId);
	}

	@Override
	public JasperPrint generatePdfforEvaluationConclusion(String eventId, TimeZone timeZone, JRSwapFileVirtualizer virtualizer) {
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<EvaluationConclusionPojo> rfaSummary = new ArrayList<EvaluationConclusionPojo>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		parameters.put("generatedOn", sdf.format(new Date()));

		try {
			Resource resource = applicationContext.getResource("classpath:reports/RfxEvaluationConclusionReport.jasper");
			File jasperfile = resource.getFile();
			EvaluationConclusionPojo summary = new EvaluationConclusionPojo();
			RfaEvent event = rfaEventDao.getPlainEventWithOwnerById(eventId);

			if (event != null) {

				summary.setEventId(event.getEventId());
				summary.setEventName(event.getEventName());
				summary.setReferenceNo(event.getReferanceNumber());
				summary.setType("Request For Auction");
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

				List<RfaEnvelop> envelopList = rfaEnvelopDao.getEnvelopListByEventId(eventId);
				String evalutedEnvelop = "";
				String nonEvalutedEnvelop = "";
				if (CollectionUtil.isNotEmpty(envelopList)) {
					for (RfaEnvelop envelop : envelopList) {
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

				List<RfaEvaluationConclusionUser> evaluationConclusionUserList = rfaEventDao.findEvaluationConclusionUsersByEventId(eventId);
				String conclusionUser = "";

				List<EvaluationConclusionUsersPojo> evaluationConclusionList = new ArrayList<EvaluationConclusionUsersPojo>();
				if (CollectionUtil.isNotEmpty(evaluationConclusionUserList)) {
					LOG.info("List:" + evaluationConclusionUserList.size());
					Integer userIndex = 1;
					for (RfaEvaluationConclusionUser evaluationConclusionUser : evaluationConclusionUserList) {
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
				rfaSummary.add(summary);
			}
			parameters.put("EVALUATION_CONCLUSION", rfaSummary);

			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(rfaSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation conclusion  Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public void sendEmailAfterParticipationFees(String recipientEmail, String subject, String description, RfaEvent event, String name, String feeReference, String timezone) {
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
	public void downloadCsvFileForEvents(HttpServletResponse response, File file, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, Date startDate, Date endDate, boolean select_all, String auctionTypeS, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = {};
			String[] columns = {};
			List<AuctionType> auctionTypeList = new ArrayList<AuctionType>();
			if (auctionTypeS.equals("FORWARD")) {
				LOG.info("auctionType------------>" + auctionTypeS);
				auctionTypeList.add(AuctionType.FORWARD_DUTCH);
				auctionTypeList.add(AuctionType.FORWARD_ENGISH);
				auctionTypeList.add(AuctionType.FORWARD_SEALED_BID);

				columnHeadings = Global.AUCTION_EVENT_REPORT_FORWARD_CSV_COLUMNS;
				columns = new String[] { "unitName", "eventCategories", "referanceNumber", "sysEventId", "eventStartDate", "eventEndDate", "eventName", "templateName", "eventVisibility", "eventUser", "auctionType", "invitedSupplierCount", "selfInvitedSupplierCount", "participatedSupplierCount", "submittedSupplierCount", "ratio", "noOfBids", "currencyName", "leadingSuppierBid", "leadingSuppier", "selfInvitedWinner", "supplierTags", "sumAwardedPrice", "budgetAmount", "histricAmount", "savingsBudget", "savingsBudgetPercentage", "savingsHistoric", "savingsHistoricPercentage" };
			} else {
				LOG.info("auctionType------------>" + auctionTypeS);
				auctionTypeList.add(AuctionType.REVERSE_DUTCH);
				auctionTypeList.add(AuctionType.REVERSE_ENGISH);
				auctionTypeList.add(AuctionType.REVERSE_SEALED_BID);

				columnHeadings = Global.AUCTION_EVENT_REPORT_REVERSE_CSV_COLUMNS;
				columns = new String[] { "unitName", "eventCategories", "referanceNumber", "sysEventId", "eventStartDate", "eventEndDate", "eventName", "templateName", "eventVisibility", "eventUser", "auctionType", "invitedSupplierCount", "selfInvitedSupplierCount", "participatedSupplierCount", "submittedSupplierCount", "ratio", "noOfBids", "currencyName", "leadingSuppierBid", "leadingSuppier", "selfInvitedWinner", "supplierTags", "sumAwardedPrice", "budgetAmount", "histricAmount", "savingsBudget", "savingsBudgetPercentage", "savingsHistoric", "savingsHistoricPercentage" };
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}
			long count = rfaEventDao.findTotalEventsCountForCsv(tenantId, auctionTypeList);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<DraftEventPojo> list = findAllActiveEventsForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, eventIds, searchFilterEventPojo, select_all, startDate, endDate, auctionTypeS);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (DraftEventPojo pojo : list) {
					if (pojo.getEventStart() != null) {
						pojo.setEventStartDate(sdf.format(pojo.getEventStart()));
					}
					if (pojo.getEventEnd() != null) {
						pojo.setEventEndDate(sdf.format(pojo.getEventEnd()));
					}

					if (pojo.getBudgetAmount() != null) {
						pojo.setBudgetAmount((pojo.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("0.00") : pojo.getBudgetAmount()));
					}
					if (pojo.getHistricAmount() != null) {
						pojo.setHistricAmount((pojo.getHistricAmount().compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal("0.00") : pojo.getHistricAmount()));
					}
					if (pojo.getSavingsBudget() != null) {
						pojo.setSavingsBudget((pojo.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0 ? "N/A" : pojo.getSavingsBudget()));
					}

					if (pojo.getSavingsBudgetPercentage() != null) {
						pojo.setSavingsBudgetPercentage((pojo.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0 ? "N/A" : pojo.getSavingsBudgetPercentage()));
					}

					if (pojo.getSavingsHistoric() != null) {
						pojo.setSavingsHistoric((pojo.getHistricAmount().compareTo(BigDecimal.ZERO) == 0 ? "N/A" : pojo.getSavingsHistoric()));
					}
					if (pojo.getSavingsHistoricPercentage() != null) {
						pojo.setSavingsHistoricPercentage((pojo.getHistricAmount().compareTo(BigDecimal.ZERO) == 0 ? "N/A" : pojo.getSavingsHistoricPercentage()));
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
		final CellProcessor[] processors = new CellProcessor[] {

				new Optional(), // Unit
				new Optional(), // category
				new Optional(), //
				new NotNull(), // Event id
				new Optional(), // start
				new Optional(), // end
				new Optional(), //
				new Optional(), //
				new Optional(), // Event type
				new Optional(), // Owner
				new Optional(), // Auction type
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // PARTICIPATION RATIO
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // SELF-INVITED
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // HISTORIC AMOUNT
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
		};
		return processors;
	}

	private List<DraftEventPojo> findAllActiveEventsForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, String auctionTypeS) {
		List<DraftEventPojo> eventsData = rfaEventDao.findAllActiveEventsForTenantIdForCsv(tenantId, pageSize, pageNo, eventIds, searchFilterEventPojo, select_all, startDate, endDate, auctionTypeS);
		BigDecimal savingsAmountBudget = BigDecimal.ZERO, savingsAmountHistoric = BigDecimal.ZERO, savingsPercentageBudget = BigDecimal.ZERO,
				savingsPercentageHistoric = BigDecimal.ZERO;

		for (DraftEventPojo draftEventPojo1 : eventsData) {
			if (auctionTypeS.equals("FORWARD")) {
				LOG.info("auctionType---Service IMPL-------->" + auctionTypeS);
				savingsAmountBudget = draftEventPojo1.getSumAwardedPrice().subtract(draftEventPojo1.getBudgetAmount() != null ? draftEventPojo1.getBudgetAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				BigDecimal budgetAmount = BigDecimal.ONE;
				if (draftEventPojo1.getBudgetAmount() != null && draftEventPojo1.getBudgetAmount().floatValue() != 0) {
					budgetAmount = draftEventPojo1.getBudgetAmount();
				}
				savingsPercentageBudget = ((savingsAmountBudget.divide(budgetAmount, 5, RoundingMode.HALF_UP)).multiply(new BigDecimal(100))).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);
				savingsAmountHistoric = draftEventPojo1.getSumAwardedPrice().subtract(draftEventPojo1.getHistricAmount() != null ? draftEventPojo1.getHistricAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				BigDecimal historicAmount = BigDecimal.ONE;
				if (draftEventPojo1.getHistricAmount() != null && draftEventPojo1.getHistricAmount().floatValue() != 0) {
					historicAmount = draftEventPojo1.getHistricAmount();
				}

				savingsPercentageHistoric = ((savingsAmountHistoric.divide(historicAmount, 5, RoundingMode.HALF_UP)).multiply(new BigDecimal(100))).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				draftEventPojo1.setSavingsBudget(savingsAmountBudget != null ? savingsAmountBudget.toString() : "");
				draftEventPojo1.setSavingsBudgetPercentage(savingsPercentageBudget != null ? savingsPercentageBudget.toString() : "");
				draftEventPojo1.setSavingsHistoric(savingsAmountHistoric != null ? savingsAmountHistoric.toString() : "");
				draftEventPojo1.setSavingsHistoricPercentage(savingsPercentageHistoric != null ? savingsPercentageHistoric.toString() : "");
				draftEventPojo1.setBudgetAmount((draftEventPojo1.getBudgetAmount() != null ? draftEventPojo1.getBudgetAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setHistricAmount((draftEventPojo1.getHistricAmount() != null ? draftEventPojo1.getHistricAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setLeadingSuppierBid((draftEventPojo1.getLeadingSuppierBid() != null ? draftEventPojo1.getLeadingSuppierBid() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setSumAwardedPrice((draftEventPojo1.getSumAwardedPrice() != null ? draftEventPojo1.getSumAwardedPrice() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));

				if (draftEventPojo1.getAwardedPrice() != null && draftEventPojo1.getAwardedPrice().trim().length() > 0) {
					String[] values = draftEventPojo1.getAwardedPrice().split(",");
					String rounded = "";
					for (String value : values) {
						LOG.info("Value : " + value.trim());
						rounded += (new BigDecimal(value.trim()).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP)).toString() + ",";
					}
					if (rounded.length() > 0 && rounded.indexOf(',') > 0) {
						rounded = rounded.substring(0, rounded.length() - 1);
					}
					draftEventPojo1.setAwardedPrice(rounded);
				}

			} else {
				LOG.info("auctionType---Service IMPL-------->" + auctionTypeS);

				savingsAmountBudget = draftEventPojo1.getBudgetAmount().subtract(draftEventPojo1.getSumAwardedPrice() != null ? draftEventPojo1.getSumAwardedPrice() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				BigDecimal budgetAmount = BigDecimal.ONE;
				if (draftEventPojo1.getBudgetAmount() != null && draftEventPojo1.getBudgetAmount().floatValue() != 0) {
					budgetAmount = draftEventPojo1.getBudgetAmount();
				}
				savingsPercentageBudget = savingsAmountBudget.divide(budgetAmount, 5, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);
				savingsAmountHistoric = draftEventPojo1.getHistricAmount().subtract(draftEventPojo1.getSumAwardedPrice() != null ? draftEventPojo1.getSumAwardedPrice() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				BigDecimal historicAmount = BigDecimal.ONE;
				if (draftEventPojo1.getHistricAmount() != null && draftEventPojo1.getHistricAmount().floatValue() != 0) {
					historicAmount = draftEventPojo1.getHistricAmount();
				}
				savingsPercentageHistoric = ((savingsAmountHistoric.divide(historicAmount, 5, RoundingMode.HALF_UP)).multiply(new BigDecimal(100))).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP);

				draftEventPojo1.setSavingsBudget(savingsAmountBudget != null ? savingsAmountBudget.toString() : "");
				draftEventPojo1.setSavingsBudgetPercentage(savingsPercentageBudget != null ? savingsPercentageBudget.toString() : "");
				draftEventPojo1.setSavingsHistoric(savingsAmountHistoric != null ? savingsAmountHistoric.toString() : "");
				draftEventPojo1.setSavingsHistoricPercentage(savingsPercentageHistoric != null ? savingsPercentageHistoric.toString() : "");
				draftEventPojo1.setBudgetAmount((draftEventPojo1.getBudgetAmount() != null ? draftEventPojo1.getBudgetAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setHistricAmount((draftEventPojo1.getHistricAmount() != null ? draftEventPojo1.getHistricAmount() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setLeadingSuppierBid((draftEventPojo1.getLeadingSuppierBid() != null ? draftEventPojo1.getLeadingSuppierBid() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));
				draftEventPojo1.setSumAwardedPrice((draftEventPojo1.getSumAwardedPrice() != null ? draftEventPojo1.getSumAwardedPrice() : BigDecimal.ZERO).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP));

				if (draftEventPojo1.getAwardedPrice() != null && draftEventPojo1.getAwardedPrice().trim().length() > 0) {
					String[] values = draftEventPojo1.getAwardedPrice().split(",");
					String rounded = "";
					for (String value : values) {
						LOG.info("Value : " + value.trim());
						rounded += (new BigDecimal(value.trim()).setScale(draftEventPojo1.getDecimalValue(), RoundingMode.HALF_UP)).toString() + ",";
					}
					if (rounded.length() > 0 && rounded.indexOf(',') > 0) {
						rounded = rounded.substring(0, rounded.length() - 1);
					}
					draftEventPojo1.setAwardedPrice(rounded);
				}
			}
		}
		return eventsData;
	}

	@Override
	public long getRfaEventCountByTenantId(String searchValue, String tenantId, String userId) {
		return rfaEventDao.getRfaEventCountByTenantId(searchValue, tenantId, userId, null);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEvent updateEventSuspensionApproval(RfaEvent event, User user) {
		RfaEvent persistObj = rfaEventDao.findById(event.getId());

		List<String> auditMessages = new ArrayList<>();
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (RfaEventSuspensionApproval approvalRequest : persistObj.getSuspensionApprovals()) {
			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (RfaSuspensionApprovalUser auser : approvalRequest.getApprovalUsers()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}
		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			int level = 1;
			for (RfaEventSuspensionApproval app : event.getSuspensionApprovals()) {
				app.setEvent(event);

				ApprovalType existingType = existingTypes.get(level);
				List<String> existingUserList = existingUsers.get(level);

				app.setLevel(level++);
				newLevel = level;

				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (RfaSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
						approvalUser.setApproval(app);
						approvalUser.setId(null);

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
					if (CollectionUtil.isNotEmpty(existingUserList)) {
						for (String existing : existingUserList) {
							if (!levelUsers.contains(existing)) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + existing + " has been removed as Approver");
							}
						}
					}
				}
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
		persistObj = rfaEventDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RfaEventAudit audit = new RfaEventAudit(persistObj, user, new Date(), AuditActionType.Update, auditMessage);
				rfaEventAuditDao.save(audit);
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Event '" + persistObj.getEventId() + "' ." + auditMessage, user.getTenantId(), user, new Date(), ModuleType.RFA);
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
	public List<User> getAssociateOwnersOfEventsByEventId(String eventId, TeamMemberType memberType) {
		return rfaEventDao.getAssociateOwnersOfEventsByEventId(eventId, memberType);
	}

	@Override
	public void downloadRfaEvaluatorDocument(String id, HttpServletResponse response) {
		RfaEvaluatorUser docs = rfaEvaluatorUserDao.getEvaluationDocument(id);
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
	public void downloadRfaLeadEvaluatorDocument(String envelopId, HttpServletResponse response) {
		RfaEnvelop docs = rfaEnvelopDao.getRfaEnvelopDocument(envelopId);
		LOG.info("xyz " + docs);
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
		rfaEventDao.revertEventAwardStatus(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<String> createSpFormFromAward(RfaEventAward rfaEventAward, String templateId, String eventId, String userId, User loggedInUser) throws ApplicationException {
		RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
		SupplierPerformanceTemplate spTemplate = spTemplateService.getSupplierPerformanceTemplatebyId(templateId);
		User formOwner = userService.getUsersById(userId);
		List<String> formIds = new ArrayList<String>();
		try {

			List<String> supplierIds = new ArrayList<String>();
			for (RfaEventAwardDetails rfxAward : rfaEventAward.getRfxAwardDetails()) {
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
				String formId = eventIdSettingsDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "SP", rfaEvent.getBusinessUnit());
				form.setFormId(formId);
				form.setFormName(spTemplate.getTemplateName());
				form.setFormOwner(formOwner);
				form.setCreatedBy(SecurityLibrary.getLoggedInUser());
				form.setCreatedDate(new Date());
				form.setEventId(eventId);
				form.setEventType(RfxTypes.RFA);
				form.setAwardedSupplier(sup);
				if (Boolean.TRUE == spTemplate.getProcurementCategoryVisible() && Boolean.TRUE == spTemplate.getProcurementCategoryDisabled()) {
					form.setProcurementCategory(spTemplate.getProcurementCategory());
				} else {
					if (rfaEvent.getProcurementCategories() != null) {
						form.setProcurementCategory(rfaEvent.getProcurementCategories());
					} else {
						// if it is available in template then use it.
						form.setProcurementCategory(spTemplate.getProcurementCategory());
					}
				}
				form.setReferenceNumber(rfaEvent.getEventId());
				form.setReferenceName(rfaEvent.getEventName());
				form.setTemplate(spTemplateService.getSupplierPerformanceTemplatebyId(templateId));
				form.setBusinessUnit(rfaEvent.getBusinessUnit());
				form.setFormStatus(SupplierPerformanceFormStatus.DRAFT);
				form.copyCriteriaDetails(form, spTemplate);
				form.setBuyer(loggedInUser.getBuyer());
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

		String mailTo = user.getCommunicationEmail();
		String subject = "You have been assigned as Form Owner for Supplier Performance Evaluation Form";
		HashMap<String, Object> map = new HashMap<String, Object>();
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


	@Override
	public List<RfaReminder> getAllRfaEventReminderForEvent(String eventId) {
		return rfaReminderDao.getAllRfaEventReminderForEvent(eventId);
	}

}
