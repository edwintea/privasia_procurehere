package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.privasia.procurehere.core.entity.RfaEventDocument;
import com.privasia.procurehere.core.entity.RfiEventDocument;
import com.privasia.procurehere.core.entity.RfpEventDocument;
import com.privasia.procurehere.core.entity.RfqEventDocument;
import com.privasia.procurehere.core.entity.RfsTemplateDocument;
import com.privasia.procurehere.core.entity.RftEventDocument;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfpDocumentService;
import com.privasia.procurehere.service.RfqDocumentService;
import com.privasia.procurehere.service.RftDocumentService;
import com.privasia.procurehere.core.dao.RfaSorDao;
import com.privasia.procurehere.core.dao.RfaSorItemDao;
import com.privasia.procurehere.core.dao.RfiSorDao;
import com.privasia.procurehere.core.dao.RfiSorItemDao;
import com.privasia.procurehere.core.dao.RfpSorDao;
import com.privasia.procurehere.core.dao.RfpSorItemDao;
import com.privasia.procurehere.core.dao.RfqSorDao;
import com.privasia.procurehere.core.dao.RfqSorItemDao;
import com.privasia.procurehere.core.dao.RftSorDao;
import com.privasia.procurehere.core.dao.RftSorItemDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestSorDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestSorItemDao;
import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.RfaSorItem;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.RfiSorItem;
import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.RfpSorItem;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.RfqSorItem;
import com.privasia.procurehere.core.entity.RftEventSor;
import com.privasia.procurehere.core.entity.RftSorItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import com.privasia.procurehere.core.pojo.EvaluationSorPojo;
import com.privasia.procurehere.service.SourcingFormRequestSorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.ApprovalDocumentDao;
import com.privasia.procurehere.core.dao.AuctionBidsDao;
import com.privasia.procurehere.core.dao.AuctionRulesDao;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.CostCenterDao;
import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.FavoriteSupplierDao;
import com.privasia.procurehere.core.dao.GroupCodeDao;
import com.privasia.procurehere.core.dao.IndustryCategoryDao;
import com.privasia.procurehere.core.dao.NaicsCodesDao;
import com.privasia.procurehere.core.dao.ProcurementCategoriesDao;
import com.privasia.procurehere.core.dao.ProcurementMethodDao;
import com.privasia.procurehere.core.dao.RequestAuditDao;
import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.dao.RfaCqDao;
import com.privasia.procurehere.core.dao.RfaCqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfaCqItemDao;
import com.privasia.procurehere.core.dao.RfaDocumentDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventAuditDao;
import com.privasia.procurehere.core.dao.RfaEventContactDao;
import com.privasia.procurehere.core.dao.RfaEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDao;
import com.privasia.procurehere.core.dao.RfaEventMeetingDocumentDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaEventTimeLineDao;
import com.privasia.procurehere.core.dao.RfaReminderDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.dao.RfaSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfaSupplierMeetingAttendanceDao;
import com.privasia.procurehere.core.dao.RfaSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfsDocumentDao;
import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.SourcingFormApprovalRequestDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqItemDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.dao.TatReportDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.ApprovalDocument;
import com.privasia.procurehere.core.entity.ApprovalUser;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Comments;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.RequestComment;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventContact;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventContact;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpEventContact;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqEventContact;
import com.privasia.procurehere.core.entity.RfqTeamMember;
import com.privasia.procurehere.core.entity.RfsDocument;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftEventContact;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUser;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUserRequest;
import com.privasia.procurehere.core.entity.SourcingFormCqOption;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqOption;
import com.privasia.procurehere.core.entity.SourcingFormTeamMember;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateApproval;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.entity.SourcingTemplateField;
import com.privasia.procurehere.core.entity.TatReport;
import com.privasia.procurehere.core.entity.TemplateSourcingTeamMembers;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RequestAuditType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.EvaluationAprovalUsersPojo;
import com.privasia.procurehere.core.pojo.EvaluationBqPojo;
import com.privasia.procurehere.core.pojo.EvaluationCommentsPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqItemPojo;
import com.privasia.procurehere.core.pojo.EvaluationCqPojo;
import com.privasia.procurehere.core.pojo.EvaluationDocumentPojo;
import com.privasia.procurehere.core.pojo.EvaluationTeamsPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.MobileRequestPojo;
import com.privasia.procurehere.core.pojo.RequestAuditPojo;
import com.privasia.procurehere.core.pojo.RequestBqItemPojo;
import com.privasia.procurehere.core.pojo.RfsDocumentPojo;
import com.privasia.procurehere.core.pojo.SearchParameter;
import com.privasia.procurehere.core.pojo.SourcingFormRequestPojo;
import com.privasia.procurehere.core.pojo.SourcingSummaryPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.supplier.dao.SupplierDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.ErpIntegrationService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaBqEvaluationCommentsService;
import com.privasia.procurehere.service.RfaBqTotalEvaluationCommentsService;
import com.privasia.procurehere.service.RfaCqEvaluationCommentsService;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SourcingFormCqService;
import com.privasia.procurehere.service.SourcingFormRequestBqService;
import com.privasia.procurehere.service.SourcingFormRequestCqItemService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SourcingTemplateService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional(readOnly = true)
public class SourcingFormRequestServiceImpl implements SourcingFormRequestService {
	private static final Logger LOG = LogManager.getLogger(Global.RFS_LOG);

	@Autowired
	SourcingFormRequestDao sourcingFormRequestDao;

	@Autowired
	SourcingTemplateService sourcingTemplateService;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	SourcingFormCqService sourcingFormCqSerice;

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
	RfaSorItemDao rfaSorItemDao;

	@Autowired
	RfpSorItemDao rfpSorItemDao;


	@Autowired
	RfqSorItemDao rfqSorItemDao;


	@Autowired
	RfiSorItemDao rfiSorItemDao;

	@Autowired
	RftSorItemDao rftSorItemDao;

	@Autowired
	RfaCqDao rfaCqDao;

	@Autowired
	RfaCqItemDao rfaCqItemDao;

	@Autowired
	RfaCqEvaluationCommentsDao cqEvaluationCommentsDao;

	@Autowired
	RfaEventMeetingDocumentDao rfaEventMeetingDocumentDao;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	RfaSupplierMeetingAttendanceDao rfaSupplierMeetingAttendanceDao;

	@Autowired
	FavoriteSupplierDao favoriteSupplierDao;

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
	RfaReminderDao rfaReminderDao;

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
	RfaSorDao rfaSorDao;

	@Autowired
	RfpSorDao rfpSorDao;

	@Autowired
	RfqSorDao rfqSorDao;

	@Autowired
	RfiSorDao rfiSorDao;

	@Autowired
	RftSorDao rftSorDao;

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
	SourcingFormRequestBqService sourcingFormRequestBqService;

	@Autowired
	SourcingFormRequestSorService sourcingFormRequestSorService;


	@Autowired
	EventIdSettingsDao eventIdSettingDao;

	@Autowired
	RequestAuditDao requestAuditDao;

	@Autowired
	private SourcingFormRequestService requestService;

	@Autowired
	SourcingFormRequestBqDao bqDao;
	@Autowired
	SourcingFormRequestBqItemDao bqItemdao;


	@Autowired
	SourcingFormRequestSorDao sorDao;

	@Autowired
	SourcingFormRequestSorItemDao sorItemdao;

	@Autowired
	RfsDocumentDao rfsDocumentDao;

	@Autowired
	ApprovalDocumentDao approvalDocumentDao;

	@Autowired
	SourcingFormApprovalRequestDao sourcingFormApprovalRequestDao;

	@Autowired
	SourcingFormRequestCqItemService sourcingFormRequestCqItemService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	ProcurementMethodDao procurementMethodDao;

	@Autowired
	ProcurementCategoriesDao procurementCategoriesDao;

	@Autowired
	GroupCodeService groupCodeService;

	@Autowired
	GroupCodeDao groupCodeDao;

	@Autowired
	TatReportDao tatReportDao;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfaDocumentService rfaDocumentService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	@Autowired
	RfqDocumentService rfqDocumentService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	RfiDocumentService rfiDocumentService;

	public SourcingFormRequest loadFormById(String formId) {
		SourcingFormRequest sourcingRequest = sourcingFormRequestDao.findByFormId(formId);
		if (sourcingRequest != null) {
			if (sourcingRequest.getFormOwner().getBuyer() != null) {
				sourcingRequest.getFormOwner().getBuyer().getFullName();
				if (sourcingRequest.getFormOwner().getBuyer().getState() != null) {
					sourcingRequest.getFormOwner().getBuyer().getState().getStateName();
					if (sourcingRequest.getFormOwner().getBuyer().getState().getCountry() != null) {
						sourcingRequest.getFormOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormApprovalRequests())) {
				LOG.info("Not Empty approval User--------------------------");
				for (SourcingFormApprovalRequest approver : sourcingRequest.getSourcingFormApprovalRequests()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsersRequest())) {
						for (SourcingFormApprovalUserRequest user : approver.getApprovalUsersRequest()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormTeamMember())) {
				for (SourcingFormTeamMember teamMember : sourcingRequest.getSourcingFormTeamMember()) {
					teamMember.getUser().getName();
				}
			}

			if (sourcingRequest != null) {
				sourcingRequest.getSourcingForm().getFormName();
				if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingForm().getFields())) {
					for (SourcingTemplateField field : sourcingRequest.getSourcingForm().getFields()) {
						field.getDefaultValue();
						field.getReadOnly();
						field.getOptional();
						field.getVisible();
					}
				}
			}

			if (sourcingRequest.getSourcingForm() != null) {
				sourcingRequest.getSourcingForm().getFormName();

			}
			if (sourcingRequest.getSourcingForm().getApprovalsCount() != null) {
				sourcingRequest.getSourcingForm().getApprovalsCount();
			}

			if (sourcingRequest.getCostCenter() != null) {
				sourcingRequest.getCostCenter().getCostCenter();
			}

			if (sourcingRequest.getBusinessUnit() != null) {
				sourcingRequest.getBusinessUnit().getUnitCode();
			}

			if (sourcingRequest.getCurrency() != null) {
				sourcingRequest.getCurrency().getCurrencyCode();
			}

			if (sourcingRequest.getProcurementMethod() != null) {
				sourcingRequest.getProcurementMethod().getProcurementMethod();
			}

			if (sourcingRequest.getProcurementCategories() != null) {
				sourcingRequest.getProcurementCategories().getProcurementCategories();
			}

			if (sourcingRequest.getGroupCode() != null) {
				sourcingRequest.getGroupCode().getId();
				sourcingRequest.getGroupCode().getGroupCode();
				sourcingRequest.getGroupCode().getDescription();
			}

		}

		return sourcingRequest;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateSourcingFormRequest(SourcingFormRequest sourcingForm) {
		sourcingFormRequestDao.update(sourcingForm);
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequest copySourcingTemplate(String sourcingTemplateId, User loggedInUser, String tenantId, BusinessUnit businessUnit) throws ApplicationException {
		LOG.info(" copySourcingTemplate "+sourcingTemplateId);
		SourcingFormTemplate sourcingTemplate = sourcingTemplateService.getSourcingFormbyId(sourcingTemplateId);
		SourcingFormRequest sourcingRequest = new SourcingFormRequest();
		sourcingRequest.setSourcingForm(sourcingTemplate);
		sourcingRequest.setCreatedBy(loggedInUser);
		sourcingRequest.setFormOwner(loggedInUser);
		sourcingRequest.setStatus(SourcingFormStatus.DRAFT);
		sourcingRequest.setCreatedDate(new Date());
		sourcingRequest.setTenantId(tenantId);
		sourcingRequest.setSourcingFormName(sourcingTemplate.getFormName());
		sourcingRequest.setDecimal(sourcingTemplate.getDecimal());
		if (sourcingTemplate.getApprovalsCount() != null) {
			sourcingRequest.setApprovalsCount(sourcingTemplate.getApprovalsCount());
		}
		if (sourcingTemplate.getAddAdditionalApprovals() != null) {
			sourcingRequest.setAddAdditionalApprovals(sourcingTemplate.getAddAdditionalApprovals());
		}
		if (CollectionUtil.isNotEmpty(sourcingTemplate.getSourcingFormApproval())) {
			List<SourcingFormApprovalRequest> approvalList = new ArrayList<SourcingFormApprovalRequest>();
			for (SourcingTemplateApproval approval : sourcingTemplate.getSourcingFormApproval()) {
				SourcingFormApprovalRequest sourcingApprovalRequest = new SourcingFormApprovalRequest();
				sourcingApprovalRequest.setApprovalType(approval.getApprovalType());
				sourcingApprovalRequest.setLevel(approval.getLevel());
				sourcingApprovalRequest.setSourcingFormRequest(sourcingRequest);

				if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
					LOG.info("Sourcing Template approvall user.............." + approval.getApprovalUsers());
					List<SourcingFormApprovalUserRequest> sourcingApprovalUserList = new ArrayList<SourcingFormApprovalUserRequest>();
					for (SourcingFormApprovalUser approvalUser : approval.getApprovalUsers()) {
						SourcingFormApprovalUserRequest approvalUserRequest = new SourcingFormApprovalUserRequest();
						approvalUserRequest.setApprovalStatus(approvalUser.getApprovalStatus());
						approvalUserRequest.setApprovalRequest(sourcingApprovalRequest);
						approvalUserRequest.setRemarks(approvalUser.getRemarks());
						approvalUserRequest.setUser(approvalUser.getUser());
						sourcingApprovalUserList.add(approvalUserRequest);
					}
					sourcingApprovalRequest.setApprovalUsersRequest(sourcingApprovalUserList);
				}
				approvalList.add(sourcingApprovalRequest);
			}
			sourcingRequest.setSourcingFormApprovalRequests(approvalList);
		}

		if (CollectionUtil.isNotEmpty(sourcingTemplate.getFields())) {
			LOG.info("sourcingTemplate.getFields() not empty ");
			for (SourcingTemplateField field : sourcingTemplate.getFields()) {
				switch (field.getFieldName()) {
				case BUDGET_AMOUNT:
					if (field.getDefaultValue() != null) {
						sourcingRequest.setBudgetAmount(new BigDecimal(field.getDefaultValue()));
						LOG.info("budget amount Default value :  " + field.getDefaultValue());
					}
					break;
				case COST_CENTER:
					if (field.getDefaultValue() != null) {
						CostCenter costCenter = costCenterDao.findById(field.getDefaultValue());
						sourcingRequest.setCostCenter(costCenter);
						LOG.info("costCenter : " + costCenter + "Default value :  " + field.getDefaultValue());
					}
					break;
				case BUSINESS_UNIT:
					if (field.getDefaultValue() != null) {
						BusinessUnit businessUnittemp = businessUnitService.getPlainBusinessUnitById(field.getDefaultValue());
						sourcingRequest.setBusinessUnit(businessUnittemp);
					}
					break;
				case HISTORIC_AMOUNT:
					if (field.getDefaultValue() != null) {
						sourcingRequest.setHistoricaAmount(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case MINIMUM_SUPPLIER_RATING:
					if (field.getDefaultValue() != null && !StringUtils.checkString(field.getDefaultValue()).isEmpty()) {
						sourcingRequest.setMinimumSupplierRating(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case MAXIMUM_SUPPLIER_RATING:
					if (field.getDefaultValue() != null && !StringUtils.checkString(field.getDefaultValue()).isEmpty()) {
						sourcingRequest.setMaximumSupplierRating(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case BASE_CURRENCY:
					if (field.getDefaultValue() != null) {
						LOG.info("Base currency value : " + field.getDefaultValue());
						sourcingRequest.setCurrency(currencyService.getCurrency(field.getDefaultValue()));
					}
					break;
				case ESTIMATED_BUDGET:
					if (field.getDefaultValue() != null) {
						sourcingRequest.setEstimatedBudget(new BigDecimal(field.getDefaultValue()));
					}
					break;
				case PROCUREMENT_METHOD:
					if (field.getDefaultValue() != null) {
						ProcurementMethod procurementMethod = procurementMethodDao.findById(field.getDefaultValue());
						sourcingRequest.setProcurementMethod(procurementMethod);
					}
					break;
				case PROCUREMENT_CATEGORY:
					if (field.getDefaultValue() != null) {
						ProcurementCategories procurementCategories = procurementCategoriesDao.findById(field.getDefaultValue());
						sourcingRequest.setProcurementCategories(procurementCategories);
					}
					break;
				case GROUP_CODE:
					if (field.getDefaultValue() != null) {
						GroupCode groupCode = groupCodeDao.findById(field.getDefaultValue());
						if (Status.ACTIVE == groupCode.getStatus()) {
							sourcingRequest.setGroupCode(groupCode);
							LOG.info("Group Code : " + groupCode + "Default value :  " + field.getDefaultValue());
						} else {
							LOG.info("INACTIVE >>>>>>>GPC  " + field.getDefaultValue());
							throw new ApplicationException("The group code '" + groupCode.getGroupCode() + "' used in Template is inactive");
						}
					}
					break;
				default:
					break;

				}
			}
		}

		if (eventIdSettingsDao.isBusinessSettingEnable(tenantId, "SR")) {
			if (businessUnit != null) {
				LOG.info("business unit selected by user choice selected");
				sourcingRequest.setBusinessUnit(businessUnit);
			} else {
				LOG.info("business unit selected privious");
				if (sourcingRequest.getBusinessUnit() == null) {
					LOG.info("business unit exception throw for buyer select its own business unit");
					throw new ApplicationException("BUSINESS_UNIT_EMPTY");
				}

			}
		}

		LOG.info(">>>>>>>>>>>> businessUnit : " + businessUnit + " : >>>>>>>>>>> : " + sourcingRequest.getBusinessUnit());
		if (businessUnit != null) {
			sourcingRequest.setFormId(eventIdSettingsDao.generateEventIdByBusinessUnit(tenantId, "SR", businessUnit));
		} else {
			sourcingRequest.setFormId(eventIdSettingsDao.generateEventIdByBusinessUnit(tenantId, "SR", sourcingRequest.getBusinessUnit()));
		}

		sourcingRequest = sourcingFormRequestDao.saveOrUpdate(sourcingRequest);
		if(sourcingTemplate.getRfsTemplateDocuments() != null){
			for(RfsTemplateDocument templateDocument : sourcingTemplate.getRfsTemplateDocuments()){
				RfsDocument doc = new RfsDocument();
				doc.setCredContentType(templateDocument.getCredContentType());
				doc.setDescription(templateDocument.getDescription());
				doc.setFileName(templateDocument.getFileName());
				doc.setFileData(templateDocument.getFileData());
				doc.setUploadDate(templateDocument.getUploadDate());
				doc.setInternal(templateDocument.getInternal());
				doc.setUploadBy(templateDocument.getUploadBy());
				doc.setFileSizeInKb(templateDocument.getFileSizeInKb());
				doc.setSourcingFormRequest(sourcingRequest);
				saveRfsDocument(doc);
			}
		}
		List<SourcingFormTeamMember> teamMembers = new ArrayList<SourcingFormTeamMember>();
		if (CollectionUtil.isNotEmpty(sourcingTemplate.getTeamMembers())) {
			for (TemplateSourcingTeamMembers team : sourcingTemplate.getTeamMembers()) {
				SourcingFormTeamMember newTeamMembers = new SourcingFormTeamMember();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setSourcingFormRequest(sourcingRequest);
				teamMembers.add(newTeamMembers);
				sourcingTemplateService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(sourcingRequest.getSourcingFormName()).length() > 0 ? sourcingRequest.getSourcingFormName() : " ", StringUtils.checkString(sourcingRequest.getReferanceNumber()).length() > 0 ? sourcingRequest.getReferanceNumber() : " ", sourcingRequest.getId(), sourcingRequest.getFormId());
			}
			sourcingRequest.setSourcingFormTeamMember(teamMembers);
		}
		return sourcingRequest;
	}

	@Override
	public SourcingFormRequest getSourcingRequestById(String formId) {
		SourcingFormRequest sourcingRequest = sourcingFormRequestDao.findById(formId);
		if (sourcingRequest != null) {

			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormApprovalRequests())) {
				for (SourcingFormApprovalRequest sfr : sourcingRequest.getSourcingFormApprovalRequests()) {
					sfr.getApprovalUsersRequest();
					LOG.info(sfr.getApprovalUsersRequest());
					sfr.getUsers();
				}

			}
			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormTeamMember())) {
				for (SourcingFormTeamMember teamMember : sourcingRequest.getSourcingFormTeamMember()) {
					teamMember.getUser().getName();
				}
			}
			if (sourcingRequest.getFormOwner().getBuyer() != null) {
				sourcingRequest.getFormOwner().getBuyer().getFullName();
				if (sourcingRequest.getFormOwner().getBuyer().getState() != null) {
					sourcingRequest.getFormOwner().getBuyer().getState().getStateName();
					if (sourcingRequest.getFormOwner().getBuyer().getState().getCountry() != null) {
						sourcingRequest.getFormOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}

			if (sourcingRequest.getCostCenter() != null) {
				sourcingRequest.getCostCenter().getId();
				sourcingRequest.getCostCenter().getCostCenter();
				sourcingRequest.getCostCenter().getDescription();
			}

			if (sourcingRequest.getGroupCode() != null) {
				sourcingRequest.getGroupCode().getId();
				sourcingRequest.getGroupCode().getGroupCode();
				sourcingRequest.getGroupCode().getDescription();
			}

			if (sourcingRequest.getBusinessUnit() != null) {
				sourcingRequest.getBusinessUnit().getId();
				sourcingRequest.getBusinessUnit().getUnitName();
				sourcingRequest.getBusinessUnit().getUnitCode();
			}

			if (sourcingRequest.getProcurementCategories() != null) {
				sourcingRequest.getProcurementCategories().getId();
				sourcingRequest.getProcurementCategories().getProcurementCategories();
				sourcingRequest.getProcurementCategories().getDescription();
			}

			if (sourcingRequest.getProcurementMethod() != null) {
				sourcingRequest.getProcurementMethod().getId();
				sourcingRequest.getProcurementMethod().getProcurementMethod();
				sourcingRequest.getProcurementMethod().getDescription();
			}

			if (sourcingRequest.getCurrency() != null) {
				sourcingRequest.getCurrency().getId();
				sourcingRequest.getCurrency().getCurrencyCode();
				sourcingRequest.getCurrency().getCurrencyName();
			}

		}
		return sourcingRequest;
	}

	@Override
	public SourcingFormRequest getSourcingRequestByIdForSummary(String formId) {
		SourcingFormRequest sourcingRequest = sourcingFormRequestDao.findById(formId);
		if (sourcingRequest != null) {
			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormApprovalRequests())) {
				for (SourcingFormApprovalRequest sfr : sourcingRequest.getSourcingFormApprovalRequests()) {
					if (CollectionUtil.isNotEmpty(sfr.getApprovalUsersRequest())) {
						for (SourcingFormApprovalUserRequest req : sfr.getApprovalUsersRequest()) {
							req.getUserName();
							req.getUser().getName();
						}
					}
					//
					if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormTeamMember())) {
						for (SourcingFormTeamMember teamMember : sourcingRequest.getSourcingFormTeamMember()) {
							teamMember.getUser().getName();
						}
					}
					if (CollectionUtil.isNotEmpty(sfr.getUsers())) {
						for (ApprovalUser user : sfr.getUsers()) {
							user.getUserName();
						}
					}
				}

			}

			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormApprovalRequests())) {
				for (SourcingFormApprovalRequest approver : sourcingRequest.getSourcingFormApprovalRequests()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsersRequest())) {
						for (SourcingFormApprovalUserRequest user : approver.getApprovalUsersRequest()) {
							user.getRemarks();
							if (user.getUser() != null) {
								user.getUser().getName();
							}
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}

			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormTeamMember())) {
				for (SourcingFormTeamMember teamMember : sourcingRequest.getSourcingFormTeamMember()) {
					teamMember.getUser().getName();
				}
			}
			if (sourcingRequest.getFormOwner().getBuyer() != null) {
				sourcingRequest.getFormOwner().getBuyer().getFullName();
				if (sourcingRequest.getFormOwner().getBuyer().getState() != null) {
					sourcingRequest.getFormOwner().getBuyer().getState().getStateName();
					if (sourcingRequest.getFormOwner().getBuyer().getState().getCountry() != null) {
						sourcingRequest.getFormOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(sourcingRequest.getRequestComments())) {
				for (RequestComment comment : sourcingRequest.getRequestComments()) {
					//
					comment.getApprovalUserId();
					//
					comment.getCreatedBy().getName();
				}
			}

			if (CollectionUtil.isNotEmpty(sourcingRequest.getApprovalDocuments())) {
				for (ApprovalDocument rd : sourcingRequest.getApprovalDocuments()) {
					rd.getFileData();
					rd.getDescription();
					rd.getUploadDate();
					rd.getId();
					rd.getFileSize();
				}
			}

			if (CollectionUtil.isNotEmpty(sourcingRequest.getRfsDocuments())) {
				for (RfsDocument rd : sourcingRequest.getRfsDocuments()) {
					rd.getFileData();
					rd.getDescription();
					rd.getUploadDate();
					rd.getId();
					rd.getFileSize();
					if (rd.getUploadBy() != null) {
						rd.getUploadBy().getLoginId();
					}
				}
			}

			if (sourcingRequest.getBusinessUnit() != null) {
				sourcingRequest.getBusinessUnit().getUnitCode();
			}
			if (sourcingRequest.getCostCenter() != null) {
				sourcingRequest.getCostCenter().getCostCenter();
			}

			if (sourcingRequest.getCurrency() != null) {
				sourcingRequest.getCurrency().getCurrencyCode();
			}
			if (sourcingRequest.getProcurementMethod() != null) {
				sourcingRequest.getProcurementMethod().getProcurementMethod();
			}
			if (sourcingRequest.getProcurementCategories() != null) {
				sourcingRequest.getProcurementCategories().getProcurementCategories();
			}
			if (sourcingRequest.getGroupCode() != null) {
				sourcingRequest.getGroupCode().getId();
				sourcingRequest.getGroupCode().getGroupCode();
				sourcingRequest.getGroupCode().getDescription();
			}

		}
		return sourcingRequest;
	}

	@Override
	public List<SourcingFormRequest> findAllSourcingFormForTenant(String loggedInUserTenantId, TableDataInput input) {
		return sourcingFormRequestDao.findAllSourcingFormForTenant(loggedInUserTenantId, input);
	}

	@Override
	public List<SourcingTemplateCq> getCq(String formId) {
		return sourcingFormRequestDao.getCq(formId);
	}

	@Override
	public List<SourcingFormRequestBq> getBq(String formId) {

		return sourcingFormRequestDao.getBq(formId);
	}

	@Override
	public SourcingFormTemplate getSourcingFormByReqId(String requestId) {

		return sourcingFormRequestDao.getSourcingFormByReqId(requestId);
	}

	@Override
	public List<SourcingFormApprovalRequest> getApproval(String requestId) {
		return sourcingFormRequestDao.getApproval(requestId);
	}

	@Override
	public List<SourcingFormRequestCqItem> getCqItembyRequestId(String requestId) {
		List<SourcingFormRequestCqItem> list = sourcingFormRequestDao.getCqItembyRequestId(requestId);
		for (SourcingFormRequestCqItem sourcingFormRequestCqItem : list) {
			if (sourcingFormRequestCqItem.getCq() != null)
				sourcingFormRequestCqItem.getCq().getName();
		}

		return list;

	}

	@Override

	public List<SourcingFormRequestPojo> findTotalMyPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input) {

		return convertToPojo(sourcingFormRequestDao.findTotalMyPendingRequestList(loggedInUserTenantId, id, input));
	}

	@Override
	public long findTotalMyPendingRequestCount(String loggedInUserTenantId, String id) {

		return sourcingFormRequestDao.findTotalMyPendingRequestCount(loggedInUserTenantId, id);
	}

	@Override
	public List<SourcingFormRequest> myDraftRequestList(String loggedInUserTenantId, String id, TableDataInput input) {
		return sourcingFormRequestDao.myDraftRequestList(loggedInUserTenantId, id, input);
	}

	@Override
	public long myDraftRequestListCount(String loggedInUserTenantId, String id, TableDataInput input) {
		return sourcingFormRequestDao.myDraftRequestListCount(loggedInUserTenantId, id, input);
	}

	@Override
	public long myApprovedRequestListCount(String loggedInUserTenantId, String id, TableDataInput input) {
		return sourcingFormRequestDao.myApprovedRequestListCount(loggedInUserTenantId, id, input);
	}

	@Override
	public long myApprovedRequestListCountBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds) {
		return sourcingFormRequestDao.myApprovedRequestListCountBizUnit(loggedInUserTenantId, id, input,businessUnitIds);
		//return sourcingFormRequestDao.myApprovedRequestListCount(loggedInUserTenantId, id, input);
	}

	@Override
	public List<SourcingFormRequestPojo> myPendingRequestList(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds) {
		return convertToPojo(sourcingFormRequestDao.myPendingRequestList(loggedInUserTenantId, id, input,businessUnitIds));
	}

	@Override
	public long myPendingRequestListCount(String loggedInUserTenantId, String id, TableDataInput input,List<String> bizUnitIds) {
		return sourcingFormRequestDao.myPendingRequestListCount(loggedInUserTenantId, id, input,bizUnitIds);
	}

	public List<SourcingFormRequestBq> getSourcingRequestBq(String requestId) {
		return sourcingFormRequestDao.getSourcingRequestBq(requestId);

	}


	public List<SourcingFormRequestSor> getSourcingRequestSor(String requestId) {
		return sourcingFormRequestDao.getSourcingRequestSor(requestId);
	}

	@Override
	public List<String> getSourcingRequestBqNames(String requestId) {
		return sourcingFormRequestDao.getSourcingRequestBqNames(requestId);
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequest update(SourcingFormRequest sourcingFormRequest) {
		return sourcingFormRequestDao.update(sourcingFormRequest);
	}

	@Override
	@Transactional(readOnly = false)
	public String createNextEvent(String requestId, RfxTypes selectedRfxType, AuctionType auctionType, String bqId, User loggedInUser, String idRfxTemplate, String businessUnitId, List<String> docLists) throws Exception {
		String newEventId = null;

		SourcingFormRequest request = sourcingFormRequestDao.findById(requestId);

		RfxTemplate rfxTemplate = null;
		if (StringUtils.checkString(idRfxTemplate).length() > 0) {
			rfxTemplate = rfxTemplateService.getRfxTemplateById(idRfxTemplate);
		}
		if (rfxTemplate != null && Status.INACTIVE == rfxTemplate.getStatus()) {
			LOG.info("inactive Template [" + rfxTemplate.getTemplateName() + "] found for Id .... " + rfxTemplate.getId());
			throw new ApplicationException("Template [" + rfxTemplate.getTemplateName() + "] is Inactive");
		}

		BusinessUnit selectedbusinessUnit = null;
		if (StringUtils.checkString(businessUnitId).length() > 0) {
			LOG.info("---------------finding BU --------------------");
			selectedbusinessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
		}
		if (selectedRfxType != null) {
			switch (selectedRfxType) {
			case RFA: {
				newEventId = createRfa(auctionType, bqId, loggedInUser, request, rfxTemplate, selectedbusinessUnit, docLists);
				break;
			}
			case RFP: {
				newEventId = createRfp(loggedInUser, request, rfxTemplate, selectedbusinessUnit, docLists);
				break;
			}
			case RFQ: {
				newEventId = createRfq(loggedInUser, request, rfxTemplate, selectedbusinessUnit, docLists);
				break;
			}
			case RFT: {
				newEventId = createRft(loggedInUser, request, rfxTemplate, selectedbusinessUnit, docLists);
				break;
			}
			case RFI: {
				newEventId = createRfi(loggedInUser, request, rfxTemplate, selectedbusinessUnit, docLists);
				break;
			}
			default:
				break;
			}
		}

		SourcingFormRequest req = requestService.findById(request.getId());
		if (req != null && req.getStatus() != SourcingFormStatus.FINISHED) {
			req.setStatus(SourcingFormStatus.FINISHED);
			requestService.update(req);
		}

		return newEventId;
	}

	private String createRfi(User loggedInUser, SourcingFormRequest request, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, List<String> selectedDocs) throws ApplicationException, SubscriptionException {
		String newEventId;
		RfiEvent newEvent = request.createNextRfiEvent(request, loggedInUser);
		newEvent.setBaseCurrency(request.getCurrency());
		if (rfxTemplate != null) {
			newEvent.setTemplate(rfxTemplate);
			rfiEventService.createRfiFromTemplate(newEvent, rfxTemplate, selectedbusinessUnit, loggedInUser, true);
		} else {

			if (selectedbusinessUnit != null) {
				LOG.info("setting business unit while temlate is null ......");
				newEvent.setBusinessUnit(selectedbusinessUnit);
			}
		}
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFI", newEvent.getBusinessUnit()));
		// default bill of quantity false
		newEvent.setBillOfQuantity(Boolean.FALSE);

		List<SourcingFormRequestSor> sorList = sourcingFormRequestSorService.findSorByFormIdByOrder(request.getId());

		if(sorList != null && sorList.size() > 0) {
			newEvent.setScheduleOfRate(Boolean.TRUE);
		}


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

		List<RfiEventDocument> documents = new ArrayList<RfiEventDocument>();
		//save Document
		if (selectedDocs != null && !selectedDocs.isEmpty()) {
			for (String docId : selectedDocs) {
				// Logic to carry forward the document
				RfsDocument rfsDocument = rfsDocumentDao.findById(docId);
				if (rfsDocument != null) {
					// Copy the document to the new event or handle it accordingly
					RfiEventDocument doc = new RfiEventDocument();
					doc.setCredContentType(rfsDocument.getCredContentType());
					doc.setDescription(rfsDocument.getDescription());
					doc.setFileName(rfsDocument.getFileName());
					doc.setFileData(rfsDocument.getFileData());
					doc.setUploadDate(rfsDocument.getUploadDate());
					doc.setInternal(rfsDocument.getInternal());
					doc.setFileSizeInKb(rfsDocument.getFileSizeInKb());
					doc.setUploadBy(rfsDocument.getUploadBy());
					doc.setRfxEvent(newdbEvent);
					rfiDocumentService.saveRfiDocuments(doc);
					documents.add(doc);
				}
			}
			newdbEvent.setDocuments(documents);
		}

		newdbEvent = rfiEventService.saveRfiEvent(newdbEvent);


		for (SourcingFormRequestSor bq : sorList) {
			LOG.info("sor.getName " + bq.getName());
			RfiEventSor newBq = new RfiEventSor(bq);
			newBq.setRfxEvent(newdbEvent);
			newBq = rfiSorDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
				LOG.info("sor.getBqItems().size()" + bq.getSorItems().size());
				RfiSorItem parent = null;
				newBq.setSorItems(new ArrayList<RfiSorItem>());
				for (SourcingFormRequestSorItem bqItem : bq.getSorItems()) {
					LOG.info("sorItem.getItemName " + bqItem.getItemName());
					LOG.info("sorItem.getOrder " + bqItem.getOrder());
					LOG.info("sorItem.getLevel " + bqItem.getLevel());

					RfiSorItem newBqItem = bqItem.copyForRfi(bqItem);
					newBqItem.setSor(newBq);
					newBqItem.setRfxEvent(newdbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("sorItem.getItemName " + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getSorItems().add(newBqItem);
					rfiSorItemDao.save(newBqItem);
				}
			}
		}

		RequestAudit audit = new RequestAudit();
		audit.setActionDate(new Date());
		audit.setAction(RequestAuditType.CREATE);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
		audit.setReq(request);
		audit.setDescription("Processed to RFI -" + newdbEvent.getEventId());
		audit = requestService.saveAudit(audit);

		List<TatReport> dbTatReportList = tatReportDao.getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(request.getId(), loggedInUser.getTenantId());

		if (CollectionUtil.isNotEmpty(dbTatReportList)) {
			for (TatReport dbTatReport : dbTatReportList) {
				if (StringUtils.checkString(dbTatReport.getEventId()).length() == 0) {
					TatReport updatedtatReport = copyEventDetails(newEvent, dbTatReport, RfxTypes.RFI);
					updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
					tatReportDao.saveOrUpdate(updatedtatReport);
					break;
				} else {
					TatReport tatReport = copyRequestDetails(dbTatReport, loggedInUser);
					TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFI);
					tatReportDao.save(updatedtatReport);
					break;
				}
			}
		} else {
			TatReport tatReport = new TatReport();
			tatReport.setRequestGeneratedId(request.getId());
			tatReport.setFormId(request.getFormId());
			tatReport.setSourcingFormName(request.getSourcingFormName());
			tatReport.setBusinessUnit(request.getBusinessUnit().getUnitName());
			tatReport.setCostCenter(request.getCostCenter() != null ? request.getCostCenter().getCostCenter() : "");
			tatReport.setRequestOwner(request.getFormOwner() != null ? (request.getFormOwner().getName() + " " + request.getFormOwner().getLoginId()) : "");
			tatReport.setGroupCode(request.getGroupCode() != null ? request.getGroupCode().getGroupCode() : (request.getGroupCodeOld() != null ? request.getGroupCodeOld() : ""));
			tatReport.setAvailableBudget(request.getBudgetAmount());
			tatReport.setEstimatedBudget(request.getEstimatedBudget());
			tatReport.setCreatedDate(request.getCreatedDate());
			tatReport.setProcurementMethod(request.getProcurementMethod() != null ? request.getProcurementMethod().getProcurementMethod() : "");
			tatReport.setProcurementCategories(request.getProcurementCategories() != null ? request.getProcurementCategories().getProcurementCategories() : "");
			tatReport.setBaseCurrency(request.getCurrency() != null ? request.getCurrency().getCurrencyCode() : "");
			tatReport.setTenantId(loggedInUser.getTenantId());
			tatReport.setReqDecimal(request.getDecimal());

			tatReport.setFirstApprovedDate(request.getFirstApprovedDate() != null ? request.getFirstApprovedDate() : null);
			tatReport.setLastApprovedDate(request.getApprovedDate() != null ? request.getApprovedDate() : null);

			if (request.getApprovedDate() != null && request.getSubmittedDate() != null) {
				double diffInDays = DateUtil.differenceInDays(request.getApprovedDate(), request.getSubmittedDate());
				tatReport.setReqApprovalDaysCount(BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP));
			}
			tatReport.setFinishDate(request.getSubmittedDate());
			tatReport.setEventGeneratedId(newEvent.getId());
			TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFI);
			updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
			tatReportService.saveTatReport(updatedtatReport);
		}

		newEventId = newdbEvent.getId();

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing request '" + request.getFormId() + "' Processed to RFI -" + newdbEvent.getEventId(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}

		return newEventId;
	}

	/**
	 * @param loggedInUser
	 * @param request
	 * @param rfxTemplate
	 * @param selectedbusinessUnit
	 * @return
	 * @throws ApplicationException
	 * @throws SubscriptionException
	 */
	private String createRft(User loggedInUser, SourcingFormRequest request, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, List<String> selectedDocs) throws ApplicationException, SubscriptionException {
		String newEventId;
		RftEvent newEvent = request.createNextRftEvent(request, loggedInUser);
		newEvent.setBaseCurrency(request.getCurrency());
		if (rfxTemplate != null) {
			newEvent.setTemplate(rfxTemplate);
			rftEventService.createRftFromTemplate(newEvent, rfxTemplate, selectedbusinessUnit, loggedInUser, true);
		} else {
			if (selectedbusinessUnit != null) {
				LOG.info("setting business unit while temlate is null ......");
				newEvent.setBusinessUnit(selectedbusinessUnit);
			}
		}
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFT", newEvent.getBusinessUnit()));

		List<SourcingFormRequestSor> sorList = sourcingFormRequestSorService.findSorByFormIdByOrder(request.getId());

		if(sorList != null && sorList.size() > 0) {
			newEvent.setScheduleOfRate(Boolean.TRUE);
		}

		RftEvent newDbEvent = rftEventService.saveRftEvent(newEvent, loggedInUser);
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			for (RftTeamMember newTeamMembers : newEvent.getTeamMembers()) {
				rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFT, newDbEvent.getId());

			}
		}
		// Save contacts
		if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
			for (RftEventContact contact : newEvent.getEventContacts()) {
				contact.setRfxEvent(newDbEvent);
				rftEventService.saveRftEventContact(contact);
			}
		}

		List<RftEventDocument> documents = new ArrayList<RftEventDocument>();
		//save Document
		if (selectedDocs != null && !selectedDocs.isEmpty()) {
			for (String docId : selectedDocs) {
				// Logic to carry forward the document
				RfsDocument rfsDocument = rfsDocumentDao.findById(docId);
				if (rfsDocument != null) {
					// Copy the document to the new event or handle it accordingly
					RftEventDocument doc = new RftEventDocument();
					doc.setCredContentType(rfsDocument.getCredContentType());
					doc.setDescription(rfsDocument.getDescription());
					doc.setFileName(rfsDocument.getFileName());
					doc.setFileData(rfsDocument.getFileData());
					doc.setUploadDate(rfsDocument.getUploadDate());
					doc.setInternal(rfsDocument.getInternal());
					doc.setFileSizeInKb(rfsDocument.getFileSizeInKb());
					doc.setUploadBy(rfsDocument.getUploadBy());
					doc.setRfxEvent(newDbEvent);
					rftDocumentService.saveRftDocuments(doc);
					documents.add(doc);
				}
			}
			newDbEvent.setDocuments(documents);
		}

		newDbEvent = rftEventService.saveRftEvent(newDbEvent, loggedInUser);
		List<SourcingFormRequestBq> bqlist = sourcingFormRequestBqService.getAllBqListByFormId(request.getId());
		for (SourcingFormRequestBq bq : bqlist) {
			LOG.info("Bq Name " + bq.getName());
			RftEventBq newBq = new RftEventBq(bq);
			newBq.setRfxEvent(newDbEvent);
			newBq = rftBqDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
				LOG.info("Bq Item size" + bq.getBqItems().size());
				RftBqItem parent = null;
				newBq.setBqItems(new ArrayList<RftBqItem>());
				for (SourcingFormRequestBqItem bqItem : bq.getBqItems()) {
					RftBqItem newBqItem = bqItem.copyForRft(bqItem);
					newBqItem.setBq(newBq);
					newBqItem.setRfxEvent(newDbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("ItemName" + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getBqItems().add(newBqItem);
					newBqItem = rftBqItemDao.save(newBqItem);
				}
			}

		}


		for (SourcingFormRequestSor bq : sorList) {
			LOG.info("sor.getName " + bq.getName());
			RftEventSor newBq = new RftEventSor(bq);
			newBq.setRfxEvent(newDbEvent);
			newBq = rftSorDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
				LOG.info("sor.getBqItems().size()" + bq.getSorItems().size());
				RftSorItem parent = null;
				newBq.setSorItems(new ArrayList<RftSorItem>());
				for (SourcingFormRequestSorItem bqItem : bq.getSorItems()) {
					LOG.info("sorItem.getItemName " + bqItem.getItemName());
					LOG.info("sorItem.getOrder " + bqItem.getOrder());
					LOG.info("sorItem.getLevel " + bqItem.getLevel());

					RftSorItem newBqItem = bqItem.copyForRft(bqItem);
					newBqItem.setSor(newBq);
					newBqItem.setRfxEvent(newDbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("sorItem.getItemName " + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getSorItems().add(newBqItem);
					rftSorItemDao.save(newBqItem);
				}
			}
		}

		RequestAudit audit = new RequestAudit();
		audit.setActionDate(new Date());
		audit.setAction(RequestAuditType.CREATE);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
		audit.setReq(request);
		audit.setDescription("Processed to RFT -" + newDbEvent.getEventId());
		audit = requestService.saveAudit(audit);

		List<TatReport> dbTatReportList = tatReportDao.getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(request.getId(), loggedInUser.getTenantId());

		if (CollectionUtil.isNotEmpty(dbTatReportList)) {
			for (TatReport dbTatReport : dbTatReportList) {
				if (StringUtils.checkString(dbTatReport.getEventId()).length() == 0) {
					TatReport updatedtatReport = copyEventDetails(newEvent, dbTatReport, RfxTypes.RFT);
					updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
					tatReportDao.saveOrUpdate(updatedtatReport);
					break;
				} else {
					TatReport tatReport = copyRequestDetails(dbTatReport, loggedInUser);
					TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFT);
					tatReportDao.save(updatedtatReport);
					break;
				}
			}
		} else {
			TatReport tatReport = new TatReport();
			tatReport.setRequestGeneratedId(request.getId());
			tatReport.setFormId(request.getFormId());
			tatReport.setSourcingFormName(request.getSourcingFormName());
			tatReport.setBusinessUnit(request.getBusinessUnit().getUnitName());
			tatReport.setCostCenter(request.getCostCenter() != null ? request.getCostCenter().getCostCenter() : "");
			tatReport.setRequestOwner(request.getFormOwner() != null ? (request.getFormOwner().getName() + " " + request.getFormOwner().getLoginId()) : "");
			tatReport.setGroupCode(request.getGroupCode() != null ? request.getGroupCode().getGroupCode() : (request.getGroupCodeOld() != null ? request.getGroupCodeOld() : ""));
			tatReport.setAvailableBudget(request.getBudgetAmount());
			tatReport.setEstimatedBudget(request.getEstimatedBudget());
			tatReport.setCreatedDate(request.getCreatedDate());
			tatReport.setProcurementMethod(request.getProcurementMethod() != null ? request.getProcurementMethod().getProcurementMethod() : "");
			tatReport.setProcurementCategories(request.getProcurementCategories() != null ? request.getProcurementCategories().getProcurementCategories() : "");
			tatReport.setBaseCurrency(request.getCurrency() != null ? request.getCurrency().getCurrencyCode() : "");
			tatReport.setTenantId(loggedInUser.getTenantId());
			tatReport.setReqDecimal(request.getDecimal());

			tatReport.setFirstApprovedDate(request.getFirstApprovedDate() != null ? request.getFirstApprovedDate() : null);
			tatReport.setLastApprovedDate(request.getApprovedDate() != null ? request.getApprovedDate() : null);

			if (request.getApprovedDate() != null && request.getSubmittedDate() != null) {
				double diffInDays = DateUtil.differenceInDays(request.getApprovedDate(), request.getSubmittedDate());
				tatReport.setReqApprovalDaysCount(BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP));
			}
			tatReport.setFinishDate(request.getSubmittedDate());
			tatReport.setEventGeneratedId(newEvent.getId());
			TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFT);
			updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
			tatReportService.saveTatReport(updatedtatReport);
		}

		newEventId = newDbEvent.getId();

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing Form '" + request.getFormId() + "' Processed to RFT -" + newDbEvent.getEventId(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}

		return newEventId;
	}

	/**
	 * @param loggedInUser
	 * @param request
	 * @param rfxTemplate
	 * @param selectedbusinessUnit
	 * @return
	 * @throws ApplicationException
	 * @throws SubscriptionException
	 */
	private String createRfq(User loggedInUser, SourcingFormRequest request, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, List<String> selectedDocs) throws ApplicationException, SubscriptionException {
		String newEventId;
		RfqEvent newEvent = request.createNextRfqEvent(request, loggedInUser);
		newEvent.setBaseCurrency(request.getCurrency());
		if (rfxTemplate != null) {
			newEvent.setTemplate(rfxTemplate);
			rfqEventService.createRfqFromTemplate(newEvent, rfxTemplate, selectedbusinessUnit, loggedInUser, true);
		} else {

			if (selectedbusinessUnit != null) {
				LOG.info("setting business unit while temlate is null ......");
				newEvent.setBusinessUnit(selectedbusinessUnit);
			}
		}
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFQ", newEvent.getBusinessUnit()));
		List<SourcingFormRequestSor> sorList = sourcingFormRequestSorService.findSorByFormIdByOrder(request.getId());

		if(sorList != null && sorList.size() > 0) {
			newEvent.setScheduleOfRate(Boolean.TRUE);
		}

		RfqEvent newdbEvent = rfqEventService.saveEvent(newEvent, loggedInUser);
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			for (RfqTeamMember newTeamMembers : newEvent.getTeamMembers()) {
				rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFQ, newdbEvent.getId());

			}
		}
		// save Contacts
		if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
			for (RfqEventContact contact : newEvent.getEventContacts()) {
				contact.setRfxEvent(newdbEvent);
				rfqEventService.saveEventContact(contact);
			}
		}

		List<RfqEventDocument> documents = new ArrayList<RfqEventDocument>();
		//save Document
		if (selectedDocs != null && !selectedDocs.isEmpty()) {
			for (String docId : selectedDocs) {
				// Logic to carry forward the document
				RfsDocument rfsDocument = rfsDocumentDao.findById(docId);
				if (rfsDocument != null) {
					// Copy the document to the new event or handle it accordingly
					RfqEventDocument doc = new RfqEventDocument();
					doc.setCredContentType(rfsDocument.getCredContentType());
					doc.setDescription(rfsDocument.getDescription());
					doc.setFileName(rfsDocument.getFileName());
					doc.setFileData(rfsDocument.getFileData());
					doc.setUploadDate(rfsDocument.getUploadDate());
					doc.setInternal(rfsDocument.getInternal());
					doc.setFileSizeInKb(rfsDocument.getFileSizeInKb());
					doc.setUploadBy(rfsDocument.getUploadBy());
					doc.setRfxEvent(newdbEvent);
					rfqDocumentService.saveDocuments(doc);
					documents.add(doc);
				}
			}
			newdbEvent.setDocuments(documents);
		}

		newdbEvent = rfqEventService.saveEvent(newdbEvent, loggedInUser);

		List<SourcingFormRequestBq> bqlist = sourcingFormRequestBqService.getAllBqListByFormId(request.getId());
		for (SourcingFormRequestBq bq : bqlist) {
			LOG.info("=============================" + bq.getName());
			RfqEventBq newBq = new RfqEventBq(bq);
			newBq.setRfxEvent(newdbEvent);
			newBq = rfqBqDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
				LOG.info("=============================" + bq.getBqItems().size());
				RfqBqItem parent = null;
				newBq.setBqItems(new ArrayList<RfqBqItem>());
				for (SourcingFormRequestBqItem bqItem : bq.getBqItems()) {
					LOG.info("=============================" + bqItem.getItemName());
					LOG.info("=============================" + bqItem.getOrder());
					LOG.info("=============================" + bqItem.getLevel());

					RfqBqItem newBqItem = bqItem.copyForRfq(bqItem);
					newBqItem.setBq(newBq);
					newBqItem.setRfxEvent(newdbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("==============parent===============" + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getBqItems().add(newBqItem);
					newBqItem = rfqBqItemDao.save(newBqItem);
				}
			}

		}


		for (SourcingFormRequestSor bq : sorList) {
			LOG.info("sor.getName " + bq.getName());
			RfqEventSor newBq = new RfqEventSor(bq);
			newBq.setRfxEvent(newdbEvent);
			newBq = rfqSorDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
				LOG.info("sor.getSorItems().size()" + bq.getSorItems().size());
				RfqSorItem parent = null;
				newBq.setSorItems(new ArrayList<RfqSorItem>());
				for (SourcingFormRequestSorItem bqItem : bq.getSorItems()) {
					LOG.info("sorItem.getItemName " + bqItem.getItemName());
					LOG.info("sorItem.getOrder " + bqItem.getOrder());
					LOG.info("sorItem.getLevel " + bqItem.getLevel());

					RfqSorItem newBqItem = bqItem.copyForRfq(bqItem);
					newBqItem.setSor(newBq);
					newBqItem.setRfxEvent(newdbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("sorItem.getItemName " + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getSorItems().add(newBqItem);
					rfqSorItemDao.save(newBqItem);
				}
			}
		}

		RequestAudit audit = new RequestAudit();
		audit.setActionDate(new Date());
		audit.setAction(RequestAuditType.CREATE);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
		audit.setReq(request);
		audit.setDescription("Processed to RFQ -" + newdbEvent.getEventId());
		audit = requestService.saveAudit(audit);

		List<TatReport> dbTatReportList = tatReportDao.getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(request.getId(), loggedInUser.getTenantId());

		if (CollectionUtil.isNotEmpty(dbTatReportList)) {
			for (TatReport dbTatReport : dbTatReportList) {
				if (StringUtils.checkString(dbTatReport.getEventId()).length() == 0) {
					TatReport updatedtatReport = copyEventDetails(newEvent, dbTatReport, RfxTypes.RFQ);
					updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
					tatReportDao.saveOrUpdate(updatedtatReport);
					break;
				} else {
					TatReport tatReport = copyRequestDetails(dbTatReport, loggedInUser);
					TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFQ);
					tatReportDao.save(updatedtatReport);
					break;
				}
			}
		} else {
			TatReport tatReport = new TatReport();
			tatReport.setRequestGeneratedId(request.getId());
			tatReport.setFormId(request.getFormId());
			tatReport.setSourcingFormName(request.getSourcingFormName());
			tatReport.setBusinessUnit(request.getBusinessUnit().getUnitName());
			tatReport.setCostCenter(request.getCostCenter() != null ? request.getCostCenter().getCostCenter() : "");
			tatReport.setRequestOwner(request.getFormOwner() != null ? (request.getFormOwner().getName() + " " + request.getFormOwner().getLoginId()) : "");
			tatReport.setGroupCode(request.getGroupCode() != null ? request.getGroupCode().getGroupCode() : (request.getGroupCodeOld() != null ? request.getGroupCodeOld() : ""));
			tatReport.setAvailableBudget(request.getBudgetAmount());
			tatReport.setEstimatedBudget(request.getEstimatedBudget());
			tatReport.setCreatedDate(request.getCreatedDate());
			tatReport.setProcurementMethod(request.getProcurementMethod() != null ? request.getProcurementMethod().getProcurementMethod() : "");
			tatReport.setProcurementCategories(request.getProcurementCategories() != null ? request.getProcurementCategories().getProcurementCategories() : "");
			tatReport.setBaseCurrency(request.getCurrency() != null ? request.getCurrency().getCurrencyCode() : "");
			tatReport.setTenantId(loggedInUser.getTenantId());
			tatReport.setReqDecimal(request.getDecimal());

			tatReport.setFirstApprovedDate(request.getFirstApprovedDate() != null ? request.getFirstApprovedDate() : null);
			tatReport.setLastApprovedDate(request.getApprovedDate() != null ? request.getApprovedDate() : null);

			if (request.getApprovedDate() != null && request.getSubmittedDate() != null) {
				double diffInDays = DateUtil.differenceInDays(request.getApprovedDate(), request.getSubmittedDate());
				tatReport.setReqApprovalDaysCount(BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP));
			}
			tatReport.setFinishDate(request.getSubmittedDate());
			tatReport.setEventGeneratedId(newEvent.getId());
			TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFQ);
			updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
			tatReportService.saveTatReport(updatedtatReport);
		}

		newEventId = newdbEvent.getId();

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing Form '" + request.getFormId() + "' Processed to RFQ -" + newdbEvent.getEventId(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}

		return newEventId;
	}

	/**
	 * @param loggedInUser
	 * @param request
	 * @param rfxTemplate
	 * @param selectedbusinessUnit
	 * @return
	 * @throws ApplicationException
	 * @throws SubscriptionException
	 */
	private String createRfp(User loggedInUser, SourcingFormRequest request, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, List<String> selectedDocs) throws ApplicationException, SubscriptionException {
		String newEventId;
		RfpEvent newEvent = request.createNextRfpEvent(request, loggedInUser);
		newEvent.setBaseCurrency(request.getCurrency());

		if (rfxTemplate != null) {
			newEvent.setTemplate(rfxTemplate);
			rfpEventService.createRfpFromTemplate(newEvent, rfxTemplate, null, selectedbusinessUnit, loggedInUser, true);
		} else {

			if (selectedbusinessUnit != null) {
				LOG.info("setting business unit while temlate is null ......");
				newEvent.setBusinessUnit(selectedbusinessUnit);
			}
		}
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "RFP", newEvent.getBusinessUnit()));

		List<SourcingFormRequestSor> sorList = sourcingFormRequestSorService.findSorByFormIdByOrder(request.getId());
		if(sorList != null && sorList.size() > 0) {
			newEvent.setScheduleOfRate(Boolean.TRUE);
		}

		RfpEvent newdbEvent = rfpEventService.saveEvent(newEvent, loggedInUser);
		if (CollectionUtil.isNotEmpty(newEvent.getTeamMembers())) {
			for (RfpTeamMember newTeamMembers : newEvent.getTeamMembers()) {
				rfaEventService.sendTeamMemberEmailNotificationEmail(newTeamMembers.getUser(), newTeamMembers.getTeamMemberType(), loggedInUser, StringUtils.checkString(newEvent.getEventName()).length() > 0 ? newEvent.getEventName() : " ", newEvent.getEventId(), StringUtils.checkString(newEvent.getReferanceNumber()).length() > 0 ? newEvent.getReferanceNumber() : " ", RfxTypes.RFP, newdbEvent.getId());

			}
		}
		// save Contacts
		if (CollectionUtil.isNotEmpty(newEvent.getEventContacts())) {
			for (RfpEventContact contact : newEvent.getEventContacts()) {
				contact.setRfxEvent(newdbEvent);
				rfpEventService.saveEventContact(contact);
			}
		}

		List<SourcingFormRequestBq> bqlist = sourcingFormRequestBqService.findBqByFormId(request.getId());
		for (SourcingFormRequestBq bq : bqlist) {
			LOG.info("bq.getName " + bq.getName());
			RfpEventBq newBq = new RfpEventBq(bq);
			newBq.setRfxEvent(newdbEvent);
			newBq = rfpBqDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
				LOG.info("bq.getBqItems().size()" + bq.getBqItems().size());
				RfpBqItem parent = null;
				newBq.setBqItems(new ArrayList<RfpBqItem>());
				for (SourcingFormRequestBqItem bqItem : bq.getBqItems()) {
					LOG.info("bqItem.getItemName " + bqItem.getItemName());
					LOG.info("bqItem.getOrder " + bqItem.getOrder());
					LOG.info("bqItem.getLevel " + bqItem.getLevel());

					RfpBqItem newBqItem = bqItem.copyForRfp(bqItem);
					newBqItem.setBq(newBq);
					newBqItem.setRfxEvent(newdbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("bqItem.getItemName " + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getBqItems().add(newBqItem);
					newBqItem = rfpBqItemDao.save(newBqItem);
				}
			}

		}
		List<RfpEventDocument> documents = new ArrayList<RfpEventDocument>();
		//save Document
		if (selectedDocs != null && !selectedDocs.isEmpty()) {
			for (String docId : selectedDocs) {
				// Logic to carry forward the document
				RfsDocument rfsDocument = rfsDocumentDao.findById(docId);
				if (rfsDocument != null) {
					// Copy the document to the new event or handle it accordingly
					RfpEventDocument doc = new RfpEventDocument();
					doc.setCredContentType(rfsDocument.getCredContentType());
					doc.setDescription(rfsDocument.getDescription());
					doc.setFileName(rfsDocument.getFileName());
					doc.setFileData(rfsDocument.getFileData());
					doc.setUploadDate(rfsDocument.getUploadDate());
					doc.setInternal(rfsDocument.getInternal());
					doc.setFileSizeInKb(rfsDocument.getFileSizeInKb());
					doc.setUploadBy(rfsDocument.getUploadBy());
					doc.setRfxEvent(newdbEvent);
					rfpDocumentService.saveDocuments(doc);
					documents.add(doc);
				}
			}
			newdbEvent.setDocuments(documents);
		}

		newdbEvent = rfpEventService.saveEvent(newdbEvent, loggedInUser);


		for (SourcingFormRequestSor bq : sorList) {
			LOG.info("sor.getName " + bq.getName());
			RfpEventSor newBq = new RfpEventSor(bq);
			newBq.setRfxEvent(newdbEvent);
			newBq = rfpSorDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(bq.getSorItems())) {
				LOG.info("sor.getBqItems().size()" + bq.getSorItems().size());
				RfpSorItem parent = null;
				newBq.setSorItems(new ArrayList<RfpSorItem>());
				for (SourcingFormRequestSorItem bqItem : bq.getSorItems()) {
					LOG.info("sorItem.getItemName " + bqItem.getItemName());
					LOG.info("sorItem.getOrder " + bqItem.getOrder());
					LOG.info("sorItem.getLevel " + bqItem.getLevel());

					RfpSorItem newBqItem = bqItem.copyForRfp(bqItem);
					newBqItem.setSor(newBq);
					newBqItem.setRfxEvent(newdbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("sorItem.getItemName " + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getSorItems().add(newBqItem);
					rfpSorItemDao.save(newBqItem);
				}
			}

		}

		RequestAudit audit = new RequestAudit();
		audit.setActionDate(new Date());
		audit.setAction(RequestAuditType.CREATE);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
		audit.setReq(request);
		audit.setDescription("Processed to RFP -" + newdbEvent.getEventId());
		audit = requestService.saveAudit(audit);

		List<TatReport> dbTatReportList = tatReportDao.getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(request.getId(), loggedInUser.getTenantId());

		if (CollectionUtil.isNotEmpty(dbTatReportList)) {
			for (TatReport dbTatReport : dbTatReportList) {
				if (StringUtils.checkString(dbTatReport.getEventId()).length() == 0) {
					TatReport updatedtatReport = copyEventDetails(newEvent, dbTatReport, RfxTypes.RFP);
					updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
					tatReportDao.saveOrUpdate(updatedtatReport);
					break;
				} else {
					TatReport tatReport = copyRequestDetails(dbTatReport, loggedInUser);
					TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFP);
					tatReportDao.save(updatedtatReport);
					break;
				}
			}
		} else {

			TatReport tatReport = new TatReport();
			tatReport.setRequestGeneratedId(request.getId());
			tatReport.setFormId(request.getFormId());
			tatReport.setSourcingFormName(request.getSourcingFormName());
			tatReport.setBusinessUnit(request.getBusinessUnit().getUnitName());
			tatReport.setCostCenter(request.getCostCenter() != null ? request.getCostCenter().getCostCenter() : "");
			tatReport.setRequestOwner(request.getFormOwner() != null ? (request.getFormOwner().getName() + " " + request.getFormOwner().getLoginId()) : "");
			tatReport.setGroupCode(request.getGroupCode() != null ? request.getGroupCode().getGroupCode() : (request.getGroupCodeOld() != null ? request.getGroupCodeOld() : ""));
			tatReport.setAvailableBudget(request.getBudgetAmount());
			tatReport.setEstimatedBudget(request.getEstimatedBudget());
			tatReport.setCreatedDate(request.getCreatedDate());
			tatReport.setProcurementMethod(request.getProcurementMethod() != null ? request.getProcurementMethod().getProcurementMethod() : "");
			tatReport.setProcurementCategories(request.getProcurementCategories() != null ? request.getProcurementCategories().getProcurementCategories() : "");
			tatReport.setBaseCurrency(request.getCurrency() != null ? request.getCurrency().getCurrencyCode() : "");
			tatReport.setTenantId(loggedInUser.getTenantId());
			tatReport.setReqDecimal(request.getDecimal());

			tatReport.setFirstApprovedDate(request.getFirstApprovedDate() != null ? request.getFirstApprovedDate() : null);
			tatReport.setLastApprovedDate(request.getApprovedDate() != null ? request.getApprovedDate() : null);

			if (request.getApprovedDate() != null && request.getSubmittedDate() != null) {
				double diffInDays = DateUtil.differenceInDays(request.getApprovedDate(), request.getSubmittedDate());
				tatReport.setReqApprovalDaysCount(BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP));
			}
			tatReport.setFinishDate(request.getSubmittedDate());
			tatReport.setEventGeneratedId(newEvent.getId());
			TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFP);
			updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
			tatReportService.saveTatReport(updatedtatReport);
		}

		newEventId = newdbEvent.getId();

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing Form '" + request.getFormId() + "' Processed to RFP -" + newdbEvent.getEventId(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}

		return newEventId;
	}

	/**
	 * @param auctionType
	 * @param bqId
	 * @param loggedInUser
	 * @param request
	 * @param rfxTemplate
	 * @param selectedbusinessUnit
	 * @return
	 * @throws Exception
	 * @throws ApplicationException
	 * @throws SubscriptionException
	 */
	private String createRfa(AuctionType auctionType, String bqId, User loggedInUser, SourcingFormRequest request, RfxTemplate rfxTemplate, BusinessUnit selectedbusinessUnit, List<String> selectedDocs) throws Exception, ApplicationException, SubscriptionException {
		String newEventId;
		RfaEvent newEvent = request.createNextRfaEvent(request, auctionType, bqId, loggedInUser);
		AuctionRules auctionRules = new AuctionRules();
		if (rfxTemplate != null) {
			newEvent.setTemplate(rfxTemplate);
			rfaEventService.createRfaFromTemplate(newEvent, rfxTemplate, auctionRules, selectedbusinessUnit, loggedInUser, true);
		} else {
			if (selectedbusinessUnit != null) {
				LOG.info("setting business unit while temlate is null ......");
				newEvent.setBusinessUnit(selectedbusinessUnit);
			}
		}
		newEvent.setEventId(eventIdSettingsDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFA", newEvent.getBusinessUnit()));

		RfaEvent newdbEvent = rfaEventService.saveRfaEvent(newEvent, loggedInUser);

		if (newdbEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || newdbEvent.getAuctionType() == AuctionType.REVERSE_ENGISH || newdbEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID || newdbEvent.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
			newdbEvent.setBillOfQuantity(Boolean.TRUE);
		}
		List<RfaEventDocument> documents = new ArrayList<RfaEventDocument>();
        //save Document
		if (selectedDocs != null && !selectedDocs.isEmpty()) {
			for (String docId : selectedDocs) {
				// Logic to carry forward the document
				RfsDocument rfsDocument = rfsDocumentDao.findById(docId);
				if (rfsDocument != null) {
					// Copy the document to the new event or handle it accordingly
					RfaEventDocument rfaDocument = new RfaEventDocument();
					rfaDocument.setCredContentType(rfsDocument.getCredContentType());
					rfaDocument.setDescription(rfsDocument.getDescription());
					rfaDocument.setFileName(rfsDocument.getFileName());
					rfaDocument.setFileData(rfsDocument.getFileData());
					rfaDocument.setUploadDate(rfsDocument.getUploadDate());
					rfaDocument.setInternal(rfsDocument.getInternal());
					rfaDocument.setFileSizeInKb(rfsDocument.getFileSizeInKb());
					rfaDocument.setUploadBy(rfsDocument.getUploadBy());
					rfaDocument.setRfxEvent(newdbEvent);
					rfaDocumentService.saveRfaDocuments(rfaDocument);
					documents.add(rfaDocument);
				}
			}
			newdbEvent.setDocuments(documents);
		}

		List<SourcingFormRequestSor> sorList = sourcingFormRequestSorService.findSorByFormIdByOrder(request.getId());

		if(sorList != null && sorList.size() > 0) {
			newdbEvent.setScheduleOfRate(Boolean.TRUE);
		}

		newdbEvent = rfaEventService.saveRfaEvent(newdbEvent, loggedInUser);

		// save Auction Rule
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
				rfaEventService.saveRfaEventContact(contact);
			}
		}

		SourcingFormRequestBq bq = sourcingFormRequestBqService.getBqById(bqId);
		if (bq != null) {
			RfaEventBq newBq = new RfaEventBq(bq);
			newBq.setRfxEvent(newdbEvent);
			newBq = rfaBqDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(bq.getBqItems())) {
				LOG.info("=============================" + bq.getBqItems().size());
				RfaBqItem parent = null;
				newBq.setBqItems(new ArrayList<RfaBqItem>());
				for (SourcingFormRequestBqItem bqItem : bq.getBqItems()) {
					RfaBqItem newBqItem = bqItem.copyForRfa(bqItem);
					newBqItem.setBq(newBq);
					newBqItem.setRfxEvent(newdbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("==============parent===============" + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getBqItems().add(newBqItem);
					newBqItem = rfaBqItemDao.save(newBqItem);
				}
			}

		}


		for (SourcingFormRequestSor sor : sorList) {
			LOG.info("sor.getName " + sor.getName());
			RfaEventSor newBq = new RfaEventSor(sor);
			newBq.setRfxEvent(newdbEvent);
			newBq = rfaSorDao.saveOrUpdate(newBq);
			if (CollectionUtil.isNotEmpty(sor.getSorItems())) {
				LOG.info("sor.getSorItems().size()" + sor.getSorItems().size());
				RfaSorItem parent = null;
				newBq.setSorItems(new ArrayList<RfaSorItem>());
				for (SourcingFormRequestSorItem bqItem : sor.getSorItems()) {
					LOG.info("sorItem.getItemName " + bqItem.getItemName());
					LOG.info("sorItem.getOrder " + bqItem.getOrder());
					LOG.info("sorItem.getLevel " + bqItem.getLevel());

					RfaSorItem newBqItem = bqItem.copyForRfa(bqItem);
					newBqItem.setSor(newBq);
					newBqItem.setRfxEvent(newdbEvent);
					if (newBqItem.getOrder() != 0) {
						LOG.info("sorItem.getItemName " + bqItem.getItemName());
						newBqItem.setParent(parent);
					}
					if (newBqItem.getOrder() == 0) {
						parent = newBqItem;
					}
					newBq.getSorItems().add(newBqItem);
					rfaSorItemDao.save(newBqItem);
				}
			}

		}

		RequestAudit audit = new RequestAudit();
		audit.setActionDate(new Date());
		audit.setAction(RequestAuditType.CREATE);
		audit.setActionBy(SecurityLibrary.getLoggedInUser());
		audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
		audit.setReq(request);
		audit.setDescription("Processed to RFA -" + newdbEvent.getEventId());
		audit = requestService.saveAudit(audit);

		List<TatReport> dbTatReportList = tatReportDao.getRfsListForTatReportListByRfsFormIdAndIdAndTenantId(request.getId(), loggedInUser.getTenantId());

		if (CollectionUtil.isNotEmpty(dbTatReportList)) {
			for (TatReport dbTatReport : dbTatReportList) {
				if (StringUtils.checkString(dbTatReport.getEventId()).length() == 0) {
					TatReport updatedtatReport = copyEventDetails(newEvent, dbTatReport, RfxTypes.RFA);
					updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
					tatReportDao.saveOrUpdate(updatedtatReport);
					break;
				} else {
					TatReport tatReport = copyRequestDetails(dbTatReport, loggedInUser);
					TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFA);
					tatReportDao.save(updatedtatReport);
					break;
				}
			}
		} else {
			TatReport tatReport = new TatReport();
			tatReport.setRequestGeneratedId(request.getId());
			tatReport.setFormId(request.getFormId());
			tatReport.setSourcingFormName(request.getSourcingFormName());
			tatReport.setBusinessUnit(request.getBusinessUnit().getUnitName());
			tatReport.setCostCenter(request.getCostCenter() != null ? request.getCostCenter().getCostCenter() : "");
			tatReport.setRequestOwner(request.getFormOwner() != null ? (request.getFormOwner().getName() + " " + request.getFormOwner().getLoginId()) : "");
			tatReport.setGroupCode(request.getGroupCode() != null ? request.getGroupCode().getGroupCode() : (request.getGroupCodeOld() != null ? request.getGroupCodeOld() : ""));
			tatReport.setAvailableBudget(request.getBudgetAmount());
			tatReport.setEstimatedBudget(request.getEstimatedBudget());
			tatReport.setCreatedDate(request.getCreatedDate());
			tatReport.setProcurementMethod(request.getProcurementMethod() != null ? request.getProcurementMethod().getProcurementMethod() : "");
			tatReport.setProcurementCategories(request.getProcurementCategories() != null ? request.getProcurementCategories().getProcurementCategories() : "");
			tatReport.setBaseCurrency(request.getCurrency() != null ? request.getCurrency().getCurrencyCode() : "");
			tatReport.setTenantId(loggedInUser.getTenantId());
			tatReport.setReqDecimal(request.getDecimal());

			tatReport.setFirstApprovedDate(request.getFirstApprovedDate() != null ? request.getFirstApprovedDate() : null);
			tatReport.setLastApprovedDate(request.getApprovedDate() != null ? request.getApprovedDate() : null);

			if (request.getApprovedDate() != null && request.getSubmittedDate() != null) {
				double diffInDays = DateUtil.differenceInDays(request.getApprovedDate(), request.getSubmittedDate());
				tatReport.setReqApprovalDaysCount(BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP));
			}
			tatReport.setFinishDate(request.getSubmittedDate());
			tatReport.setEventGeneratedId(newEvent.getId());
			TatReport updatedtatReport = copyEventDetails(newEvent, tatReport, RfxTypes.RFA);
			updatedtatReport.setRequestStatus(SourcingFormStatus.FINISHED);
			tatReportService.saveTatReport(updatedtatReport);
		}

		newEventId = newdbEvent.getId();

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing Form '" + request.getFormId() + "' Processed to RFA -" + newdbEvent.getEventId(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}

		return newEventId;
	}

	private TatReport copyEventDetails(Event newEvent, TatReport tatReport, RfxTypes eventType) {
		tatReport.setEventGeneratedId(newEvent.getId());
		tatReport.setEventId(newEvent.getEventId());
		tatReport.setEventType(eventType);
		tatReport.setStatus(newEvent.getStatus());
		tatReport.setEventOwner(newEvent.getEventOwner() != null ? (newEvent.getEventOwner().getName() + " " + newEvent.getEventOwner().getLoginId()) : "");
		tatReport.setEventCreatedDate(newEvent.getCreatedDate());
		return tatReport;
	}

	private TatReport copyRequestDetails(TatReport dbTatReport, User loggedInUser) {
		TatReport tatReport = new TatReport();
		tatReport.setRequestGeneratedId(dbTatReport.getRequestGeneratedId());
		tatReport.setFormId(dbTatReport.getFormId());
		tatReport.setSourcingFormName(dbTatReport.getSourcingFormName());
		tatReport.setFormDescription(dbTatReport.getFormDescription());
		tatReport.setBusinessUnit(dbTatReport.getBusinessUnit());
		tatReport.setCostCenter(dbTatReport.getCostCenter());
		tatReport.setRequestOwner(dbTatReport.getRequestOwner());
		tatReport.setGroupCode(dbTatReport.getGroupCode());
		tatReport.setAvailableBudget(dbTatReport.getAvailableBudget());
		tatReport.setEstimatedBudget(dbTatReport.getEstimatedBudget());
		tatReport.setCreatedDate(dbTatReport.getCreatedDate());
		tatReport.setProcurementMethod(dbTatReport.getProcurementMethod() != null ? dbTatReport.getProcurementMethod() : "");
		tatReport.setProcurementCategories(dbTatReport.getProcurementCategories() != null ? dbTatReport.getProcurementCategories() : "");
		tatReport.setRequestStatus(SourcingFormStatus.FINISHED);
		tatReport.setBaseCurrency(dbTatReport.getBaseCurrency() != null ? dbTatReport.getBaseCurrency() : "");
		tatReport.setReqDecimal(dbTatReport.getReqDecimal());
		tatReport.setFinishDate(dbTatReport.getFinishDate());
		tatReport.setSapPrId(dbTatReport.getSapPrId());
		tatReport.setTenantId(loggedInUser.getTenantId());
		tatReport.setFirstApprovedDate(dbTatReport.getFirstApprovedDate());
		tatReport.setLastApprovedDate(dbTatReport.getLastApprovedDate());
		tatReport.setReqRejectedDate(dbTatReport.getReqRejectedDate());
		tatReport.setReqApprovalDaysCount(dbTatReport.getReqApprovalDaysCount());

		return tatReport;
	}

	@Override
	public EventPermissions getUserPemissionsForRequest(User user, String requestId) {
		return sourcingFormRequestDao.getUserPemissionsForRequest(user.getId(), requestId);
	}

	@Override
	public long getBqCount(String requestId) {
		return sourcingFormRequestDao.getBqItemCount(requestId);

	}

	@Transactional(readOnly = false)
	@Override
	public RequestAudit saveAudit(RequestAudit audit) {
		RequestAudit aud = requestAuditDao.save(audit);
		LOG.info("saved logs ++++++++ " + aud.getId());
		return aud;
	}

	@Override
	public List<RequestAudit> getReqAudit(String formId) {
		return requestAuditDao.RequestAuditById(formId);
	}

	@Override
	public boolean checkSourcingRequestStatus(String formId) {
		return sourcingFormRequestDao.checkSourcingRequestStatus(formId);
	}

	@Override
	public SourcingFormRequest findById(String requestId) {
		return sourcingFormRequestDao.findById(requestId);
	}

	@Override
	public List<SourcingFormRequest> searchSourcingRequestByNameAndRefNum(String searchValue, String tenantId, String userId, String pageNo) {

		List<SourcingFormRequest> sourcingFormRequestList = sourcingFormRequestDao.searchSourcingRequestByNameAndRefNum(searchValue, tenantId, userId, pageNo);
		if (CollectionUtil.isNotEmpty(sourcingFormRequestList)) {
			for (SourcingFormRequest sourcingFormRequest : sourcingFormRequestList) {
				sourcingFormRequest.setModifiedBy(null);
				// sourcingFormRequest.setCreatedBy(null);
				sourcingFormRequest.setActionBy(null);
				sourcingFormRequest.setBusinessUnit(null);
				sourcingFormRequest.setSourcingFormApprovalRequests(null);
			}
		}
		return sourcingFormRequestList;
	}

	@Override
	public List<SourcingFormRequestPojo> myDraftRequestPojoList(String loggedInUserTenantId, String id, TableDataInput input) {
		return convertToPojo(sourcingFormRequestDao.myDraftRequestList(loggedInUserTenantId, id, input));
	}

	private List<SourcingFormRequestPojo> convertToPojo(List<SourcingFormRequest> list) {

		List<SourcingFormRequestPojo> pojolist = new ArrayList<SourcingFormRequestPojo>();
		if (CollectionUtil.isNotEmpty(list)) {
			for (SourcingFormRequest request : list) {
				SourcingFormRequestPojo pojo = new SourcingFormRequestPojo();
				pojo.setId(request.getId());
				pojo.setSourcingFormName(request.getSourcingFormName());
				pojo.setStatus(request.getStatus());
				pojo.setReferanceNumber(request.getReferanceNumber());
				pojo.setFormId(request.getFormId());
				pojo.setCreatedBy(request.getCreatedBy() != null ? request.getCreatedBy().getName() : "");
				pojo.setCreatedDate(request.getCreatedDate());
				pojo.setModifiedBy(request.getModifiedBy() != null ? request.getModifiedBy().getName() : "");
				pojo.setFormOwner(request.getFormOwner() != null ? request.getFormOwner().getName() : "");
				pojo.setBusinessUnit(request.getBusinessUnit() != null ? request.getBusinessUnit().getUnitName() : "");
				pojo.setCostCenter(request.getCostCenter() != null ? request.getCostCenter().getCostCenter() : "");
				pojo.setBaseCurrency(request.getCurrency() != null ? request.getCurrency().getCurrencyCode() : "");
				pojolist.add(pojo);
			}
		}
		return pojolist;

	}

	@Override
	public List<SourcingFormRequestPojo> myCompletedRequestList(String loggedInUserTenantId, String id, TableDataInput input) {
		return convertToPojo(sourcingFormRequestDao.myCompletedRequestList(loggedInUserTenantId, id, input));
	}

	@Override
	public MobileRequestPojo getMobileRequestDetails(String requestId) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

		LOG.info("========requestId===========");
		MobileRequestPojo pojo = new MobileRequestPojo();
		SourcingFormRequest request = sourcingFormRequestDao.findById(requestId);

		if (request != null) {

			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(request.getTenantId(), timeZone);
			if (TimeZone.getTimeZone(timeZone) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
			}

			if (CollectionUtil.isNotEmpty(request.getSourcingFormApprovalRequests())) {
				List<SourcingFormApprovalRequest> approvalList = new ArrayList<>();

				for (SourcingFormApprovalRequest requestApproval : request.getSourcingFormApprovalRequests()) {

					if (CollectionUtil.isNotEmpty(requestApproval.getApprovalUsersRequest())) {
						requestApproval.setSourcingFormRequest(null);

						for (SourcingFormApprovalUserRequest req : requestApproval.getApprovalUsersRequest()) {
							req.setApprovalRequest(null);
						}
						for (ApprovalUser approvalUser : requestApproval.getApprovalUsersRequest()) {
							if (approvalUser.getActionDate() != null) {
								approvalUser.setActionDateApk(sdf.format(approvalUser.getActionDate()));
							}
							requestApproval.addUsers(approvalUser.createMobileShallowCopy());
						}
					}
					approvalList.add(requestApproval);
				}
				pojo.setApprovers(approvalList);
			}
			if (CollectionUtil.isNotEmpty(request.getSourcingRequestBqs())) {

				List<SourcingFormRequestBq> bqlist = new ArrayList<SourcingFormRequestBq>();
				for (SourcingFormRequestBq bq : request.getSourcingRequestBqs()) {
					// for (SourcingFormRequestBqItem item : bq.getBqItems()) {
					bqlist.add(bq.createMobileShallowCopy());
					// }
				}

				pojo.setBqs(bqlist);
			}

			if (CollectionUtil.isNotEmpty(request.getRequestComments())) {
				List<Comments> commentList = new ArrayList<>();
				for (RequestComment comment : request.getRequestComments()) {
					comment.setTransientIsApproved(comment.isApproved());
					String createdDateApk = sdf.format(comment.getCreatedDate());
					comment.setCreatedDateApk(createdDateApk);
					commentList.add(comment.createMobileShallowCopy());
					LOG.info(comment.getCreatedDateApk());
				}
				LOG.info("Comment size====== " + commentList.size());
				pojo.setComments(commentList);
			}

			pojo.setDecimal(request.getDecimal());
			pojo.setStatus(request.getStatus());
			pojo.setRequestOwner(request.getFormOwner() != null ? request.getFormOwner().getName() : "");
			pojo.setRequestDescription(request.getDescription());
			pojo.setRequestName(request.getSourcingFormName());
			pojo.setRequestId(request.getFormId());
			pojo.setId(request.getId());
			pojo.setTemplateName(request.getSourcingForm() != null ? request.getSourcingForm().getFormName() : "");
		}
		return pojo;
	}

	@Override
	public List<SourcingFormRequestPojo> myApprvedRequestList(String loggedInUserTenantId, String id, TableDataInput input) {
		return convertToPojo(sourcingFormRequestDao.myApprvedRequestList(loggedInUserTenantId, id, input));
	}
	@Override
	public List<SourcingFormRequestPojo> myApprvedRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds) {
		//return convertToPojo(sourcingFormRequestDao.myApprvedRequestList(loggedInUserTenantId, id, input));
		return convertToPojo(sourcingFormRequestDao.myApprvedRequestListBizUnit(loggedInUserTenantId, id, input,businessUnitIds));
	}

	@Override
	public List<SourcingFormRequestPojo> myPendingRequestAppList(String loggedInUserTenantId, String id, TableDataInput input) {
		return convertToPojo(sourcingFormRequestDao.myPendingRequestAppList(loggedInUserTenantId, id, input));
	}

	@Override
	public long myPendingRequestAppListCount(String loggedInUserTenantId, String userid, TableDataInput input) {
		return sourcingFormRequestDao.myPendingRequestAppListCount(loggedInUserTenantId, userid, input);
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
	public long finishedRequestCount(String loggedInUserTenantId, TableDataInput input, String userId) {
		return sourcingFormRequestDao.finishedRequestCount(loggedInUserTenantId, input, userId);
	}

	@Override
	public long finishedRequestCountBizUnit(String loggedInUserTenantId, TableDataInput input, String userId,List<String> businessUnitIds) {

		return sourcingFormRequestDao.finishedRequestCountBizUnit(loggedInUserTenantId, input, userId,businessUnitIds);
	}

	public List<SourcingFormRequestPojo> getAllSourcingRequestList(User user, String id, TableDataInput input, Date startDate, Date endDate) {
		return convertToPojo(sourcingFormRequestDao.getAllSourcingRequestList(user, id, input, startDate, endDate));
	}

	@Override
	public long getAllSourcingRequestListCount(User user, String id, Date startDate, Date endDate) {
		return sourcingFormRequestDao.getAllSourcingRequestListCount(user, id, startDate, endDate);
	}

	@Override
	public long getCancelRequestCount(String loggedInUserTenantId, TableDataInput input, String userId) {
		return sourcingFormRequestDao.getCancelRequestCount(loggedInUserTenantId, input, userId);
	}
	@Override
	public long getCancelRequestCountBizUnit(String loggedInUserTenantId, TableDataInput input, String userId,List<String> businessUnitIds) {
		//return sourcingFormRequestDao.getCancelRequestCount(loggedInUserTenantId, input, userId);
		return sourcingFormRequestDao.getCancelRequestCountBizUnit(loggedInUserTenantId, input, userId,businessUnitIds);
	}
	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequest copyFromSourcingRequest(String formId, User loggedInUser) throws ApplicationException {
		SourcingFormRequest oldRequest = sourcingFormRequestDao.findByFormId(formId);

		if (oldRequest.getGroupCode() != null && Status.INACTIVE == oldRequest.getGroupCode().getStatus()) {
			throw new ApplicationException("The group code '" + oldRequest.getGroupCode().getGroupCode() + "' used in Request is inactive");
		}

		SourcingFormRequest newRequest = new SourcingFormRequest();
		newRequest.setActionBy(loggedInUser);
		newRequest.setActionDate(new Date());
		if (oldRequest != null) {
			if (oldRequest.getApprovalsCount() != null) {
				newRequest.setApprovalsCount(oldRequest.getApprovalsCount());
			}
			// newRequest.setConcludeRemarks(oldRequest.getConcludeRemarks());
			newRequest.setSourcingFormName(oldRequest.getSourcingFormName());
			newRequest.setCreatedBy(loggedInUser);
			newRequest.setCreatedDate(new Date());
			newRequest.setDecimal(oldRequest.getDecimal());
			newRequest.setDescription(oldRequest.getDescription());
			newRequest.setFormOwner(loggedInUser);
			newRequest.setFormType(oldRequest.getFormType());
			newRequest.setSourcingForm(oldRequest.getSourcingForm());
			newRequest.setUrgentForm(oldRequest.getUrgentForm());
			newRequest.setTenantId(loggedInUser.getTenantId());

			newRequest.setStatus(oldRequest.getStatus());
			newRequest.setReferanceNumber(oldRequest.getReferanceNumber());
			newRequest.setBudgetAmount(oldRequest.getBudgetAmount());
			newRequest.setHistoricaAmount(oldRequest.getHistoricaAmount());
			newRequest.setMinimumSupplierRating(oldRequest.getMinimumSupplierRating() != null ? oldRequest.getMinimumSupplierRating() : null);
			newRequest.setMaximumSupplierRating(oldRequest.getMaximumSupplierRating() != null ? oldRequest.getMaximumSupplierRating() : null);
			newRequest.setGroupCode(oldRequest.getGroupCode() != null ? oldRequest.getGroupCode() : null);
			newRequest.setEnableApprovalReminder(oldRequest.getEnableApprovalReminder());
			newRequest.setReminderAfterHour(oldRequest.getReminderAfterHour());
			newRequest.setReminderCount(oldRequest.getReminderCount());
			newRequest.setNotifyEventOwner(oldRequest.getNotifyEventOwner());
			newRequest.setEstimatedBudget(oldRequest.getEstimatedBudget());
			newRequest = sourcingFormRequestDao.save(newRequest);

			// copy CQ's
			SourcingFormTemplate template = oldRequest.getSourcingForm();
			List<SourcingTemplateCq> cqList = template.copyCq(template);
			for (SourcingTemplateCq cqs : cqList) {
				cqs.setSourcingForm(template);
			}
			newRequest.setSourcingForm(template);

			//copy doc
			if(CollectionUtil.isNotEmpty(template.getRfsTemplateDocuments())){
					for(RfsTemplateDocument templateDocument : template.getRfsTemplateDocuments()){
						RfsDocument doc = new RfsDocument();
						doc.setCredContentType(templateDocument.getCredContentType());
						doc.setDescription(templateDocument.getDescription());
						doc.setFileName(templateDocument.getFileName());
						doc.setFileData(templateDocument.getFileData());
						doc.setUploadDate(templateDocument.getUploadDate());
						doc.setInternal(templateDocument.getInternal());
						doc.setUploadBy(templateDocument.getUploadBy());
						doc.setFileSizeInKb(templateDocument.getFileSizeInKb());
						doc.setSourcingFormRequest(newRequest);
						saveRfsDocument(doc);
				}
			}

			if (oldRequest.getBusinessUnit() != null) {
				newRequest.setBusinessUnit(oldRequest.getBusinessUnit());
			}
			if (oldRequest.getCostCenter() != null) {
				newRequest.setCostCenter(oldRequest.getCostCenter());
			}
			// if (oldRequest.getApprovalsCount() != null) {
			// newRequest.setApprovalsCount(oldRequest.getApprovalsCount());
			// }
			if (oldRequest.getAddAdditionalApprovals() != null) {
				newRequest.setAddAdditionalApprovals(oldRequest.getAddAdditionalApprovals());
			}
			if (oldRequest.getCurrency() != null) {
				newRequest.setCurrency(oldRequest.getCurrency());
			}
			if (oldRequest.getProcurementMethod() != null) {
				newRequest.setProcurementMethod(oldRequest.getProcurementMethod());
			}
			if (oldRequest.getProcurementCategories() != null) {
				newRequest.setProcurementCategories(oldRequest.getProcurementCategories());
			}
		}

		// copy BQ and BqItem
		List<SourcingFormRequestBq> bqList = new ArrayList<SourcingFormRequestBq>();
		for (SourcingFormRequestBq bq : oldRequest.getSourcingRequestBqs()) {
			SourcingFormRequestBq newBq = copyBq(bq);
			newBq = bqDao.save(newBq);
			List<SourcingFormRequestBqItem> newBqItemList = new ArrayList<SourcingFormRequestBqItem>();
			List<SourcingFormRequestBqItem> bqItems = bq.getBqItems();

			for (SourcingFormRequestBqItem sourcingFormRequestBqItem : bqItems) {
				SourcingFormRequestBqItem newBqItem = copySourcingFormRequestBqItem(sourcingFormRequestBqItem);
				newBqItem.setBq(newBq);
				newBqItem.setSourcingFormRequest(newRequest);
				newBqItem = bqItemdao.save(newBqItem);
				newBqItemList.add(newBqItem);

			}
			newBq.setSourcingFormRequest(newRequest);
			newBq.setBqItems(newBqItemList);
			newBq = bqDao.update(newBq);
			bqList.add(newBq);
		}

		for (SourcingFormRequestBq bq : bqList) {
			SourcingFormRequestBqItem parent = null;
			for (SourcingFormRequestBqItem sourcingFormRequestBqItem : bq.getBqItems()) {
				if (sourcingFormRequestBqItem.getOrder() != 0) {
					sourcingFormRequestBqItem.setParent(parent);
				}
				sourcingFormRequestBqItem = bqItemdao.saveOrUpdate(sourcingFormRequestBqItem);
				if (sourcingFormRequestBqItem.getOrder() == 0) {
					parent = sourcingFormRequestBqItem;
				}
			}
		}

		newRequest.setSourcingRequestBqs(bqList);

		// copy SORS
		List<SourcingFormRequestSor> sorList = new ArrayList<SourcingFormRequestSor>();
		for (SourcingFormRequestSor bq : oldRequest.getSourcingRequestSors()) {
			SourcingFormRequestSor newSor = copySor(bq);
			newSor = sorDao.save(newSor);
			List<SourcingFormRequestSorItem> newSorItemList = new ArrayList<SourcingFormRequestSorItem>();
			List<SourcingFormRequestSorItem> bqItems = bq.getSorItems();

			for (SourcingFormRequestSorItem sourcingFormRequestSorItem : bqItems) {
				SourcingFormRequestSorItem newBqItem = copySourcingFormRequestSorItem(sourcingFormRequestSorItem);
				newBqItem.setSor(newSor);
				newBqItem.setSourcingFormRequest(newRequest);
				newBqItem = sorItemdao.save(newBqItem);
				newSorItemList.add(newBqItem);

			}
			newSor.setSourcingFormRequest(newRequest);
			newSor.setSorItems(newSorItemList);
			newSor = sorDao.update(newSor);
			sorList.add(newSor);
		}

		for (SourcingFormRequestSor sor : sorList) {
			SourcingFormRequestSorItem parent = null;
			for (SourcingFormRequestSorItem sourcingFormRequestSorItem : sor.getSorItems()) {
				if (sourcingFormRequestSorItem.getOrder() != 0) {
					sourcingFormRequestSorItem.setParent(parent);
				}
				sourcingFormRequestSorItem = sorItemdao.saveOrUpdate(sourcingFormRequestSorItem);
				if (sourcingFormRequestSorItem.getOrder() == 0) {
					parent = sourcingFormRequestSorItem;
				}
			}
		}
		newRequest.setSourcingRequestSors(sorList);


		if (eventIdSettingsDao.isBusinessSettingEnable(loggedInUser.getTenantId(), "SR")) {
			newRequest.setFormId(eventIdSettingDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "SR", newRequest.getBusinessUnit()));
		} else {
			newRequest.setFormId(eventIdSettingDao.generateEventIdByBusinessUnit(loggedInUser.getTenantId(), "SR", null));
		}
		newRequest.setStatus(SourcingFormStatus.DRAFT);

		// copy Approval Users
		List<SourcingFormApprovalRequest> newApprovalUserList = new ArrayList<>();
		for (SourcingFormApprovalRequest approvalRequest : oldRequest.getSourcingFormApprovalRequests()) {
			SourcingFormApprovalRequest newApppprovalRequest = copySourcingFormApprovalRequest(approvalRequest);
			newApppprovalRequest.setSourcingFormRequest(newRequest);
			newApprovalUserList.add(newApppprovalRequest);
		}

		// Save teamMember

		List<SourcingFormTeamMember> sourcingFormTeamMember = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(oldRequest.getSourcingFormTeamMember())) {
			for (SourcingFormTeamMember teamMember : oldRequest.getSourcingFormTeamMember()) {
				teamMember.setSourcingFormRequest(newRequest);
				sourcingFormTeamMember.add(teamMember);
			}
		}
		newRequest.setSourcingFormTeamMember(sourcingFormTeamMember);
		newRequest.setSourcingFormApprovalRequests(newApprovalUserList);
		newRequest = sourcingFormRequestDao.update(newRequest);

		try {
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing Form '" + newRequest.getFormId() + "' is created", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		return newRequest;
	}

	private SourcingFormRequestBqItem copySourcingFormRequestBqItem(SourcingFormRequestBqItem bqItem) {
		SourcingFormRequestBqItem newBqItem = new SourcingFormRequestBqItem();
		if (bqItem.getOrder() == 0) {
			newBqItem.setItemName(bqItem.getItemName());
			newBqItem.setItemDescription(bqItem.getItemDescription());
			newBqItem.setOrder(bqItem.getOrder());
			newBqItem.setLevel(bqItem.getLevel());
			return newBqItem;
		} else {

			newBqItem.setItemName(bqItem.getItemName());
			newBqItem.setItemDescription(bqItem.getItemDescription());
			newBqItem.setOrder(bqItem.getOrder());
			newBqItem.setLevel(bqItem.getLevel());
			if (bqItem.getUnitPrice() != null) {
				newBqItem.setUnitPrice(bqItem.getUnitPrice());
			}
			newBqItem.setQuantity(bqItem.getQuantity());
			newBqItem.setPriceType(bqItem.getPriceType());
			newBqItem.setUom(bqItem.getUom());

		}

		return newBqItem;
	}

	private SourcingFormRequestSorItem copySourcingFormRequestSorItem(SourcingFormRequestSorItem bqItem) {
		SourcingFormRequestSorItem newBqItem = new SourcingFormRequestSorItem();
		if (bqItem.getOrder() == 0) {
			newBqItem.setItemName(bqItem.getItemName());
			newBqItem.setItemDescription(bqItem.getItemDescription());
			newBqItem.setOrder(bqItem.getOrder());
			newBqItem.setLevel(bqItem.getLevel());
			return newBqItem;
		} else {
			newBqItem.setItemName(bqItem.getItemName());
			newBqItem.setItemDescription(bqItem.getItemDescription());
			newBqItem.setOrder(bqItem.getOrder());
			newBqItem.setLevel(bqItem.getLevel());
			newBqItem.setUom(bqItem.getUom());
		}

		return newBqItem;
	}

	private SourcingFormRequestBq copyBq(SourcingFormRequestBq bq) {
		SourcingFormRequestBq newBq = new SourcingFormRequestBq();
		newBq.setDescription(bq.getDescription());
		newBq.setName(bq.getName());
		newBq.setBqOrder(bq.getBqOrder());
		newBq.setCreatedDate(new Date());
		return newBq;
	}

	private SourcingFormRequestSor copySor(SourcingFormRequestSor bq) {
		SourcingFormRequestSor newBq = new SourcingFormRequestSor();
		newBq.setDescription(bq.getDescription());
		newBq.setName(bq.getName());
		newBq.setSorOrder(bq.getSorOrder());
		newBq.setCreatedDate(new Date());
		return newBq;
	}

	private SourcingFormApprovalRequest copySourcingFormApprovalRequest(SourcingFormApprovalRequest approvalRequest) {
		SourcingFormApprovalRequest appReq = new SourcingFormApprovalRequest();
		appReq.setLevel(approvalRequest.getLevel());
		appReq.setApprovalType(approvalRequest.getApprovalType());
		List<SourcingFormApprovalUserRequest> approvalUserReq = approvalRequest.getApprovalUsersRequest();
		List<SourcingFormApprovalUserRequest> newApprovalUserReq = new ArrayList<>();
		for (SourcingFormApprovalUserRequest sourcingFormApprovalUserRequest : approvalUserReq) {
			SourcingFormApprovalUserRequest approvalUserRequest = new SourcingFormApprovalUserRequest();
			approvalUserRequest.setActionDate(new Date());
			approvalUserRequest.setUser(sourcingFormApprovalUserRequest.getUser());
			approvalUserRequest.setUserId(sourcingFormApprovalUserRequest.getUserId());
			approvalUserRequest.setUserName(sourcingFormApprovalUserRequest.getUserName());
			approvalUserRequest.setApprovalRequest(appReq);
			newApprovalUserReq.add(approvalUserRequest);

		}
		appReq.setApprovalUsersRequest(newApprovalUserReq);
		return appReq;
	}

	@Override
	public List<SourcingFormRequestPojo> myCancelRequestList(String loggedInUserTenantId, String id, TableDataInput input) {
		return convertToPojo(sourcingFormRequestDao.myCancelRequestList(loggedInUserTenantId, id, input));

	}

	@Override
	public List<SourcingFormRequestPojo> myCancelRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds) {
		return convertToPojo(sourcingFormRequestDao.myCancelRequestListBizUnit(loggedInUserTenantId, id, input,businessUnitIds));

	}

	@Override
	public List<SourcingFormRequestPojo> myFinishRequestList(String loggedInUserTenantId, String id, TableDataInput input) {
		return convertToPojo(sourcingFormRequestDao.myFinishRequestList(loggedInUserTenantId, id, input));
	}

	@Override
	public List<SourcingFormRequestPojo> myFinishRequestListBizUnit(String loggedInUserTenantId, String id, TableDataInput input,List<String> businessUnitIds) {
		//return convertToPojo(sourcingFormRequestDao.myFinishRequestList(loggedInUserTenantId, id, input));
		return convertToPojo(sourcingFormRequestDao.myFinishRequestListBizUnit(loggedInUserTenantId, id, input,businessUnitIds));
	}

	@Override
	@Transactional(readOnly = false)
	public RfsDocument saveRfsDocument(RfsDocument rfsDocument) {
		return rfsDocumentDao.saveOrUpdate(rfsDocument);
	}

	@Override
	public List<RfsDocument> findAllPlainRfsDocsbyRfsId(String formId) {
		return rfsDocumentDao.findAllPlainRfsDocsbyRfsId(formId);
	}

	@Override
	public List<RfsDocument> findAllPlainRfsDocsbyRfsIdWithInternal(String formId) {
		return rfsDocumentDao.findAllPlainRfsDocsbyRfsIdWithInternal(formId);
	}
	@Override
	public List<RfsDocumentPojo> findAllPlainRfsDocsbyRfsIdAndUploadBy(String formId) {
		return rfsDocumentDao.findAllPlainRfsDocsbyRfsIdAndUploadBy(formId);
	}

	@Override
	public RfsDocument findRfsDocById(String removeDocId) {
		return rfsDocumentDao.findById(removeDocId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeRfsDocument(RfsDocument rfsDocument) {
		LOG.info("RfsDocument " + rfsDocument);
		rfsDocumentDao.delete(rfsDocument);
	}

	@Override
	public void downloadRfsDocument(String docId, HttpServletResponse response) throws Exception {
		RfsDocument docs = findRfsDocById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfsDocumentDesc(String docId, String docDesc, String formId, Boolean internal) {
		RfsDocument prDocument = findRfsDocById(docId);
		prDocument.setDescription(docDesc);
		prDocument.setInternal(internal);
		SourcingFormRequest sourcingRequest = getRfsById(formId);
		prDocument.setSourcingFormRequest(sourcingRequest);
		saveRfsDocument(prDocument);
	}

	private SourcingFormRequest getRfsById(String formId) {
		SourcingFormRequest sourcingRequest = sourcingFormRequestDao.findById(formId);
		if (sourcingRequest != null) {
			if (sourcingRequest.getCreatedBy() != null) {
				sourcingRequest.getCreatedBy().getName();
			}
		}

		return sourcingRequest;
	}

	@Override
	public SourcingFormRequest loadFormIdById(String formId) {
		SourcingFormRequest sourcingRequest = sourcingFormRequestDao.findByFormIdById(formId);
		if (sourcingRequest != null) {
			if (CollectionUtil.isNotEmpty(sourcingRequest.getRfsDocuments())) {
				for (RfsDocument rfs : sourcingRequest.getRfsDocuments()) {
					if (rfs.getUploadBy() != null) {
						rfs.getUploadBy().getLoginId();
					}
				}
			}
			if (sourcingRequest.getFormOwner().getBuyer() != null) {
				sourcingRequest.getFormOwner().getBuyer().getFullName();
				if (sourcingRequest.getFormOwner().getBuyer().getState() != null) {
					sourcingRequest.getFormOwner().getBuyer().getState().getStateName();
					if (sourcingRequest.getFormOwner().getBuyer().getState().getCountry() != null) {
						sourcingRequest.getFormOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormApprovalRequests())) {
				for (SourcingFormApprovalRequest approver : sourcingRequest.getSourcingFormApprovalRequests()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsersRequest())) {
						for (SourcingFormApprovalUserRequest user : approver.getApprovalUsersRequest()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}
			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormTeamMember())) {
				for (SourcingFormTeamMember teamMember : sourcingRequest.getSourcingFormTeamMember()) {
					teamMember.getUser().getName();
				}
			}
			if (sourcingRequest.getSourcingForm() != null) {
				sourcingRequest.getSourcingForm().getFormName();
			}
		}

		return sourcingRequest;
	}

	@Override
	public List<ApprovalDocument> findAllPlainApprovalDocsbyRfsId(String formId) {
		return approvalDocumentDao.findAllPlainApprovalDocsbyRfsId(formId);
	}

	@Override
	public void downloadApprovalDocument(String docId, HttpServletResponse response) throws Exception {

		ApprovalDocument docs = findApprovalDocById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);

	}

	@Override
	@Transactional(readOnly = false)
	public void updateApprovalDocumentDesc(String docId, String docDesc, String formId) {
		ApprovalDocument approvalDocument = findApprovalDocById(docId);
		approvalDocument.setDescription(docDesc);
		SourcingFormRequest sourcingRequest = getRfsById(formId);
		approvalDocument.setSourcingFormRequest(sourcingRequest);
		saveApprovalDocument(approvalDocument);
	}

	@Override
	@Transactional(readOnly = false)
	public ApprovalDocument saveApprovalDocument(ApprovalDocument approvalDocument) {
		return approvalDocumentDao.saveOrUpdate(approvalDocument);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeApprovalDocument(ApprovalDocument approvalDocument) {
		approvalDocumentDao.delete(approvalDocument);
	}

	@Override
	public ApprovalDocument findApprovalDocById(String removeDocId) {
		return approvalDocumentDao.findById(removeDocId);
	}

	@Override
	public SourcingFormRequest loadApprovaldocuemntFormIdById(String formId) {
		SourcingFormRequest sourcingRequest = sourcingFormRequestDao.findByApprovalDocumentFormIdById(formId);
		if (sourcingRequest != null) {
			if (sourcingRequest.getFormOwner().getBuyer() != null) {
				sourcingRequest.getFormOwner().getBuyer().getFullName();
				if (sourcingRequest.getFormOwner().getBuyer().getState() != null) {
					sourcingRequest.getFormOwner().getBuyer().getState().getStateName();
					if (sourcingRequest.getFormOwner().getBuyer().getState().getCountry() != null) {
						sourcingRequest.getFormOwner().getBuyer().getState().getCountry().getCountryName();
					}
				}
			}
			if (CollectionUtil.isNotEmpty(sourcingRequest.getSourcingFormApprovalRequests())) {
				for (SourcingFormApprovalRequest approver : sourcingRequest.getSourcingFormApprovalRequests()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsersRequest())) {
						for (SourcingFormApprovalUserRequest user : approver.getApprovalUsersRequest()) {
							user.getRemarks();
							user.getUser().getCommunicationEmail();
						}
					}
				}
			}
			if (sourcingRequest.getSourcingForm() != null) {
				sourcingRequest.getSourcingForm().getFormName();
			}
		}

		return sourcingRequest;
	}

	@Override
	public SourcingFormRequest getSourcingFormForAdditionalApproverById(String rfsId) {
		return sourcingFormRequestDao.getSourcingFormForAdditionalApproverById(rfsId);
	}

	@Override
	@Transactional(readOnly = false)
	public void addAdditionalApprover(SourcingFormRequest sourcingAdditionalApprovals, String rfsId, User logInUser) {

		SourcingFormRequest sourcingFormRequest = sourcingFormRequestDao.getSourcingFormForAdditionalApproverById(rfsId);
		if (sourcingFormRequest != null) {
			addAditionalApprover(sourcingAdditionalApprovals, sourcingFormRequest, logInUser);
			sourcingFormRequestDao.update(sourcingFormRequest);
		}

	}

	private void addAditionalApprover(SourcingFormRequest sourcingFormRequest, SourcingFormRequest persistObj, User logInUser) {
		int batchNo = 0;
		List<SourcingFormApprovalRequest> additionalApprover = persistObj.getSourcingFormApprovalRequests();

		if (CollectionUtil.isEmpty(additionalApprover)) {
			additionalApprover = new ArrayList<SourcingFormApprovalRequest>();
		}

		for (SourcingFormApprovalRequest iterable_element : persistObj.getSourcingFormApprovalRequests()) {
			if (iterable_element.isDone()) {
				batchNo = iterable_element.getBatchNo() != null ? iterable_element.getBatchNo() : 0;
			}
		}
		if (batchNo == 0) {
			batchNo = 1;
		} else {
			batchNo++;
		}
		LOG.info("batchNo : " + batchNo);

		List<SourcingFormApprovalRequest> finalList = new ArrayList<SourcingFormApprovalRequest>();
		Map<Integer, SourcingFormApprovalRequest> map = new HashMap<Integer, SourcingFormApprovalRequest>();
		if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingFormApprovalRequests())) {
			Integer level = 0;
			for (SourcingFormApprovalRequest app : sourcingFormRequest.getSourcingFormApprovalRequests()) {
				LOG.info("App Level :" + app.getLevel());

				if (StringUtils.checkString(app.getId()).length() > 0) {
					SourcingFormApprovalRequest app1 = sourcingFormApprovalRequestDao.findSourcingFormApprovalById(app.getId());
					app.setActive(app1.isActive());
					app.setBatchNo(app1.getBatchNo());
					app.setDone(app1.isDone());
					app.setLevel(app1.getLevel());
					app.setSourcingFormRequest(app1.getSourcingFormRequest());
				} else {
					app.setSourcingFormRequest(persistObj);
					app.setBatchNo(batchNo);
					app.setId(null);
					app.setCreatedBy(logInUser);
					if (app.getLevel() == null || app.getLevel() == 0) {
						app.setApprovalUsersRequest(null);
					}
				}

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsersRequest())) {
					for (SourcingFormApprovalUserRequest formApprovalUser : app.getApprovalUsersRequest()) {
						formApprovalUser.setApprovalRequest(app);
						LOG.info("Sourcing Form Request Id===============>" + formApprovalUser.getId());
						formApprovalUser.setId(null);
					}
					map.put(++level, app);
				}
				LOG.info("App Level :" + level);
			}

			for (Map.Entry<Integer, SourcingFormApprovalRequest> entry : map.entrySet()) {
				LOG.info("Key  : " + entry.getKey() + "   Level : " + entry.getValue().getLevel());
				SourcingFormApprovalRequest appp = entry.getValue();
				appp.setLevel(entry.getKey());
				finalList.add(appp);
			}
		}

		if (CollectionUtil.isNotEmpty(finalList)) {
			persistObj.setSourcingFormApprovalRequests(finalList);
		} else {
			persistObj.setSourcingFormApprovalRequests(null);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void finishAdditionalApprover(SourcingFormRequest sourcingAdditionalApprovals, String rfsId, User logInUser) throws ApplicationException {
		SourcingFormRequest sourcingFormRequest = getSourcingFormForAdditionalApproverById(rfsId);
		if (sourcingFormRequest != null) {

			boolean flag = true;
			for (SourcingFormApprovalRequest sourcingFormApprovalRequest : sourcingFormRequest.getSourcingFormApprovalRequests()) {
				for (SourcingFormApprovalUserRequest iterable_element : sourcingFormApprovalRequest.getApprovalUsersRequest()) {
					LOG.info("======iterable_element.getApprovalStatu=======>" + iterable_element.getApprovalStatus());
					if (ApprovalStatus.PENDING == iterable_element.getApprovalStatus()) {
						flag = false;
						break;
					}
				}

			}
			if (flag) {
				throw new ApplicationException("Please Save atleast one Additional Approval");
			}

			addAditionalApprover(sourcingAdditionalApprovals, sourcingFormRequest, logInUser);

			boolean additionalApproved = false;
			if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingFormApprovalRequests())) {
				for (SourcingFormApprovalRequest app : sourcingFormRequest.getSourcingFormApprovalRequests()) {
					if (!app.isDone()) {
						additionalApproved = true;
						app.setActive(Boolean.TRUE);
						for (SourcingFormApprovalUserRequest nextLevelUser : app.getApprovalUsersRequest()) {
							if (Boolean.TRUE == sourcingFormRequest.getEnableApprovalReminder()) {
								Integer reminderHr = sourcingFormRequest.getReminderAfterHour();
								Integer reminderCpunt = sourcingFormRequest.getReminderCount();
								if (reminderHr != null && reminderCpunt != null) {
									Calendar now = Calendar.getInstance();
									now.add(Calendar.HOUR, reminderHr);
									nextLevelUser.setNextReminderTime(now.getTime());
									nextLevelUser.setReminderCount(reminderCpunt);
								}
							}

						}
						sourcingFormRequest.setApprovedDate(null);
						sourcingFormRequest.setApprovalDaysHours(null);
						sourcingFormRequest.setApprovalTotalLevels(null);
						sourcingFormRequest.setApprovalTotalUsers(null);
						sourcingFormRequest.setStatus(SourcingFormStatus.PENDING);
						break;
					}
				}
			}

			if (additionalApproved) {
				RequestAudit audit = new RequestAudit();
				audit.setActionDate(new Date());
				audit.setAction(RequestAuditType.UPDATE);
				audit.setActionBy(logInUser);
				audit.setBuyer(logInUser.getBuyer());
				audit.setReq(sourcingFormRequest);
				audit.setDescription("Additional Approver Added");
				audit = requestService.saveAudit(audit);

				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Additional Approver added for Sourcing Form '" + sourcingFormRequest.getFormId() + "'", logInUser.getTenantId(), logInUser, new Date(), ModuleType.RFS);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			}
			sourcingFormRequestDao.update(sourcingFormRequest);
			try {
				sendEmailNotificationToActiveApprovers(sourcingFormRequest);
			} catch (Exception e) {
				LOG.info("Error while sending email to active approver users:" + e.getMessage());
			}
			tatReportService.updateTatReportReqStatus(sourcingFormRequest.getFormId(), logInUser.getTenantId(), sourcingFormRequest.getId(), SourcingFormStatus.PENDING);

		}

	}

	private void sendEmailNotificationToActiveApprovers(SourcingFormRequest sourcingFormRequest) {
		SourcingFormApprovalRequest approvals = sourcingFormApprovalRequestDao.getSourcingFormActiveApproverById(sourcingFormRequest.getId());
		if (approvals != null && CollectionUtil.isNotEmpty(approvals.getApprovalUsersRequest())) {
			LOG.info("Sending email approval request to active users");
			String buyerTimeZone = "GMT+8:00";
			for (SourcingFormApprovalUserRequest formAppUser : approvals.getApprovalUsersRequest()) {
				buyerTimeZone = getTimeZoneByBuyerSettings(formAppUser.getUser().getTenantId(), buyerTimeZone);
				approvalService.sendEmailToRequestApprovers(approvals.getSourcingFormRequest(), formAppUser, buyerTimeZone);

			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public List<SourcingFormTeamMember> addTeamMemberToList(String formId, String userId, TeamMemberType memberType, User loggedInUser) {
		SourcingFormRequest sourcingFormRequest = loadFormById(formId);
		List<SourcingFormTeamMember> teamMembers = sourcingFormRequest.getSourcingFormTeamMember();
		if (teamMembers == null) {
			teamMembers = new ArrayList<SourcingFormTeamMember>();
		}
		User user = userService.getUsersById(userId);
		SourcingFormTeamMember sourcingTeamMember = new SourcingFormTeamMember();
		sourcingTeamMember.setSourcingFormRequest(sourcingFormRequest);
		sourcingTeamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (SourcingFormTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				sourcingTeamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;

			}
		}
		sourcingTeamMember.setTeamMemberType(memberType);
		if (!exists) {
			teamMembers.add(sourcingTeamMember);
		}
		sourcingFormRequest.setSourcingFormTeamMember(teamMembers);
		sourcingFormRequestDao.update(sourcingFormRequest);

		try {
			if (!exists) {
				RequestAudit audit = new RequestAudit(loggedInUser, new java.util.Date(), RequestAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.added", new Object[] { user.getName(), memberType.getValue() }, Global.LOCALE), sourcingFormRequest, loggedInUser.getTenantId());
				requestService.saveAudit(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, " '" + user.getName() + "' has been added as  '" + memberType.getValue() + "' for Sourcing Form " + sourcingFormRequest.getFormId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} else {
				if (!previousMemberType.equalsIgnoreCase(memberType.getValue())) {
					RequestAudit audit = new RequestAudit(loggedInUser, new java.util.Date(), RequestAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.changed", new Object[] { user.getName(), previousMemberType, memberType.getValue() }, Global.LOCALE), sourcingFormRequest, loggedInUser.getTenantId());
					requestService.saveAudit(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, " '" + user.getName() + " 'has been changed from '" + previousMemberType + "' to '" + memberType.getValue() + "' for Sourcing Form " + sourcingFormRequest.getFormId() + "' ", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
					buyerAuditTrailDao.save(buyerAuditTrail);
				}
			}
		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}
		return teamMembers;
	}

	@Override
	public List<EventTeamMember> getPlainTeamMembersForSourcing(String formId) {
		return sourcingFormRequestDao.getPlainTeamMembersForSourcing(formId);
	}

	@Override
	public SourcingFormTeamMember getTeamMemberByUserIdAndFormId(String formId, String userId) {
		return sourcingFormRequestDao.getTeamMemberByUserIdAndFormId(formId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeTeamMemberfromList(String formId, String userId, SourcingFormTeamMember sourcingFormTeamMember) {
		SourcingFormRequest sourcingFormRequest = loadFormById(formId);
		List<SourcingFormTeamMember> teamMembers = sourcingFormRequest.getSourcingFormTeamMember();
		if (teamMembers == null) {
			teamMembers = new ArrayList<SourcingFormTeamMember>();
		}
		teamMembers.remove(sourcingFormTeamMember);
		sourcingFormTeamMember.setSourcingFormRequest(sourcingFormRequest);
		sourcingFormRequest.setSourcingFormTeamMember(teamMembers);
		sourcingFormRequestDao.update(sourcingFormRequest);

		try {
			RequestAudit audit = new RequestAudit(SecurityLibrary.getLoggedInUser(), new java.util.Date(), RequestAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.removed", new Object[] { sourcingFormTeamMember.getUser().getName(), sourcingFormTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), sourcingFormRequest);
			requestService.saveAudit(audit);
			LOG.info("************* Sourcing form Team Member event audit removed successfully *************");
		} catch (Exception e) {
			LOG.info("Error removing audit details: " + e.getMessage());
		}
		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + sourcingFormTeamMember.getUser().getName() + "' has been removed from '" + sourcingFormTeamMember.getTeamMemberType().getValue() + "' for Sourcing Form '" + sourcingFormRequest.getFormId() + "' ", sourcingFormRequest.getCreatedBy().getTenantId(), sourcingFormRequest.getCreatedBy(), new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}

		List<User> userList = new ArrayList<User>();
		for (SourcingFormTeamMember app : sourcingFormRequest.getSourcingFormTeamMember()) {
			try {
				userList.add((User) app.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error constructing list of users after removing TeamMembers operation : " + e.getMessage(), e);
			}
		}
		return userList;
	}

	@SuppressWarnings("deprecation")
	@Override
	public JasperPrint getSourcingSummaryPdf(SourcingFormRequest sourcingFormRequest, String strTimeZone) {
		LOG.info("getSourcingSummaryPdf is called here.");
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);
		// String imgPath = context.getRealPath("resources/images");
		/*
		 * DecimalFormat df = null; if (sourcingFormRequest.getDecimal().equals("1")) { df = new
		 * DecimalFormat("#,###,###,##0.0"); } else if (sourcingFormRequest.getDecimal().equals("2")) { df = new
		 * DecimalFormat("#,###,###,##0.00"); } else if (sourcingFormRequest.getDecimal().equals("3")) { df = new
		 * DecimalFormat("#,###,###,##0.000"); } else if (sourcingFormRequest.getDecimal().equals("4")) { df = new
		 * DecimalFormat("#,###,###,##0.0000"); } else if (sourcingFormRequest.getDecimal().equals("5")) { df = new
		 * DecimalFormat("#,###,###,##0.00000"); } else if (sourcingFormRequest.getDecimal().equals("6")) { df = new
		 * DecimalFormat("#,###,###,##0.000000"); } else { df = new DecimalFormat("#,###,###,##0.00"); }
		 */
		try {
			Resource resource = applicationContext.getResource("classpath:reports/GenerateSourcingSummaryReport.jasper");
			File jasperfile = resource.getFile();

			SourcingSummaryPojo summary = new SourcingSummaryPojo();
			String imgPath = context.getRealPath("resources/images");
			// general information
			summary.setSourcingFormName(sourcingFormRequest.getSourcingFormName() != null ? sourcingFormRequest.getSourcingFormName() : "");

			String createDate = sourcingFormRequest.getCreatedDate() != null ? sdf.format(sourcingFormRequest.getCreatedDate()).toUpperCase() : "";
			summary.setReferencenumber(sourcingFormRequest.getReferanceNumber() != null ? sourcingFormRequest.getReferanceNumber() : "");
			summary.setSourcingId(sourcingFormRequest.getFormId() != null ? sourcingFormRequest.getFormId() : "");
			summary.setCreatedDate(createDate);
			summary.setDescription(sourcingFormRequest.getDescription() != null ? sourcingFormRequest.getDescription() : "");
			String requestor = "";
			try {
				if (sourcingFormRequest.getCreatedBy() != null) {
					requestor += sourcingFormRequest.getCreatedBy().getName() + "\n" + sourcingFormRequest.getCreatedBy().getCommunicationEmail() + "\n";
					summary.setRequester(requestor);
				}
			} catch (Exception e) {
				Log.error("Unable to get the RFS Owner Details.");
			}

			summary.setEnableApprovalReminder(sourcingFormRequest.getEnableApprovalReminder());
			if (Boolean.TRUE == sourcingFormRequest.getEnableApprovalReminder()) {
				summary.setReminderAfterHour(sourcingFormRequest.getReminderAfterHour());
				summary.setReminderCount(sourcingFormRequest.getReminderCount());
			}
			summary.setNotifyEventOwner(sourcingFormRequest.getNotifyEventOwner());
			// finance information
			summary.setBaseCurrency(sourcingFormRequest.getCurrency() != null ? sourcingFormRequest.getCurrency().getCurrencyCode() : null);
			summary.setDecimal(sourcingFormRequest.getDecimal());
			summary.setCostCenter((sourcingFormRequest.getCostCenter() != null ? sourcingFormRequest.getCostCenter().getCostCenter() : "") + " - " + ((sourcingFormRequest.getCostCenter() != null && sourcingFormRequest.getCostCenter().getDescription() != null) ? sourcingFormRequest.getCostCenter().getDescription() : ""));
			summary.setGroupCode((sourcingFormRequest.getGroupCode() != null ? sourcingFormRequest.getGroupCode().getGroupCode() : (sourcingFormRequest.getGroupCodeOld() != null ? sourcingFormRequest.getGroupCodeOld() : " ")) + " - " + ((sourcingFormRequest.getGroupCode() != null && sourcingFormRequest.getGroupCode().getDescription() != null) ? sourcingFormRequest.getGroupCode().getDescription() : ""));
			summary.setBusinesUnit(sourcingFormRequest.getBusinessUnit() != null ? sourcingFormRequest.getBusinessUnit().getDisplayName() : "");
			summary.setBudgetAmount(sourcingFormRequest.getBudgetAmount() != null ? sourcingFormRequest.getBudgetAmount().setScale(Integer.parseInt(sourcingFormRequest.getDecimal()), RoundingMode.HALF_UP) : BigDecimal.ZERO);
			summary.setHistoricaAmount(sourcingFormRequest.getHistoricaAmount() != null ? sourcingFormRequest.getHistoricaAmount().setScale(Integer.parseInt(sourcingFormRequest.getDecimal()), RoundingMode.HALF_UP) : BigDecimal.ZERO);
			summary.setEstimatedBudget(sourcingFormRequest.getEstimatedBudget() != null ? sourcingFormRequest.getEstimatedBudget().setScale(Integer.parseInt(sourcingFormRequest.getDecimal()), RoundingMode.HALF_UP) : BigDecimal.ZERO);
			summary.setMinimumSupplierRating(sourcingFormRequest.getMinimumSupplierRating() != null ? sourcingFormRequest.getMinimumSupplierRating() : null);
			summary.setMaximumSupplierRating(sourcingFormRequest.getMaximumSupplierRating() != null ? sourcingFormRequest.getMaximumSupplierRating() : null);
			summary.setProcurementMethod(sourcingFormRequest.getProcurementMethod() != null ? sourcingFormRequest.getProcurementMethod().getProcurementMethod() : null);
			summary.setProcurementCategories(sourcingFormRequest.getProcurementCategories() != null ? sourcingFormRequest.getProcurementCategories().getProcurementCategories() : null);

			// Rfs Team Members
			List<EventTeamMember> teamMembers = getTeamMembersForEvent(sourcingFormRequest.getId());
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

			summary.setSourcingTeam(membersList);

			SourcingFormRequest request = requestService.getSourcingRequestByIdForSummary(sourcingFormRequest.getId());

			// Rfs Documents
			List<EvaluationDocumentPojo> documentList = new ArrayList<EvaluationDocumentPojo>();
			List<RfsDocument> document = rfsDocumentDao.findAllPlainRfsDocsbyRfsId(sourcingFormRequest.getId());
			if (CollectionUtil.isNotEmpty(document)) {
				for (RfsDocument docs : document) {
					EvaluationDocumentPojo item = new EvaluationDocumentPojo();
					item.setDescription(docs.getDescription());
					item.setFileName(docs.getFileName());
					item.setUploadedDate(sdf.format(docs.getUploadDate()));
					item.setUploadedBy(docs.getUploadBy() != null ? docs.getUploadBy().getName() : "N/A");
					LOG.info(">>>>>>>" + item.getUploadDate());
					item.setSize(docs.getFileSizeInKb() != null ? (double) docs.getFileSizeInKb() : 0);
					documentList.add(item);
				}
			}

			summary.setDocuments(documentList);

			// Approval Documents
			List<EvaluationDocumentPojo> approvalDocumentList = new ArrayList<EvaluationDocumentPojo>();
			List<ApprovalDocument> approvaldocument = approvalDocumentDao.findAllPlainApprovalDocsbyRfsId(sourcingFormRequest.getId());
			if (CollectionUtil.isNotEmpty(approvaldocument)) {
				for (ApprovalDocument docs : approvaldocument) {
					EvaluationDocumentPojo item = new EvaluationDocumentPojo();
					item.setDescription(docs.getDescription());
					item.setFileName(docs.getFileName());
					item.setUploadedDate(sdf.format(docs.getUploadDate()));
					item.setSize(docs.getFileSizeInKb() != null ? (double) docs.getFileSizeInKb() : 0);
					approvalDocumentList.add(item);
				}
			}
			summary.setApprovalDocuments(approvalDocumentList);

			// CQ Items
			List<EvaluationCqPojo> allCqData = new ArrayList<EvaluationCqPojo>();
			List<SourcingTemplateCq> cqList = sourcingTemplateService.getAllQuestionnarie(sourcingFormRequest.getSourcingForm().getId());
			if (CollectionUtil.isNotEmpty(cqList)) {
				for (SourcingTemplateCq item : cqList) {
					EvaluationCqPojo cqs = new EvaluationCqPojo();
					cqs.setName(item.getName());
					cqs.setDescription(item.getDescription());
					List<SourcingFormRequestCqItem> sourcingReqCqItem = requestService.getCqItembyRequestIdCqId(sourcingFormRequest.getId(), item.getId());
					List<EvaluationCqItemPojo> cqItemList = new ArrayList<EvaluationCqItemPojo>();
					if (CollectionUtil.isNotEmpty(item.getCqItems())) {
						for (SourcingTemplateCqItem cqItem : item.getCqItems()) {
							String itemName = "";
							EvaluationCqItemPojo cqItems = new EvaluationCqItemPojo();
							if (cqItem.getOrder() > 0 && Boolean.TRUE == cqItem.getOptional()) {
								cqItems.setLevel(" * " + cqItem.getLevel() + "." + cqItem.getOrder());
							} else {
								cqItems.setLevel(cqItem.getLevel() + "." + cqItem.getOrder());
							}
							if (cqItem.getAttachment() == Boolean.TRUE) {
								itemName = cqItem.getItemName() + (Boolean.TRUE == cqItem.getIsSupplierAttachRequired() ? " (Attachment is Required) " : " (Attachment is Optional) ");
							} else {
								itemName = cqItem.getItemName();
							}

							cqItems.setItemName(itemName);
							if (cqItem.getOrder() == 0) {
								cqItems.setIsSection(Boolean.TRUE);
							} else {
								cqItems.setIsSection(Boolean.FALSE);
							}
							cqItems.setItemDescription(cqItem.getItemDescription());

							String fileName = "";
							String availableOptions = "";
							List<String> answerCq = new ArrayList<String>();
							if (CollectionUtil.isNotEmpty(sourcingReqCqItem)) {
								for (SourcingFormRequestCqItem cqOption1 : sourcingReqCqItem) {
									// Get the answer for the matching CQ Item only
									if (cqOption1.getCqItem().getId().equals(cqItem.getId())) {
										if (StringUtils.checkString(cqOption1.getFileName()).length() > 0) {
											fileName += "Attachment: ";
										}
										fileName += StringUtils.checkString(cqOption1.getFileName());
										if (cqItem.getCqType() == CqType.TEXT) {
											availableOptions = "Answer: " + (StringUtils.checkString(cqOption1.getTextAnswers()));
											if (StringUtils.checkString(cqOption1.getTextAnswers()).length() == 0) {
												availableOptions += "____________";
											}
										}
										if (CollectionUtil.isNotEmpty(cqOption1.getListAnswers())) {
											if (cqItem.getCqType() != null) {
												if (cqItem.getCqType() == CqType.CHOICE || cqItem.getCqType() == CqType.LIST || cqItem.getCqType() == CqType.CHECKBOX || cqItem.getCqType() == CqType.CHOICE_WITH_SCORE) {
													for (SourcingFormRequestCqOption cqOptionAns : cqOption1.getListAnswers()) {
														answerCq.add(StringUtils.checkString(cqOptionAns.getValue()));
													}
												}
											}
										}
										break;
									}
								}
							}

							if (CollectionUtil.isNotEmpty(cqItem.getCqOptions())) {
								for (SourcingFormCqOption cqOption : cqItem.getCqOptions()) {
									if (cqItem.getCqType() != null) {
										cqItems.setOptionType(cqItem.getCqType() != null ? cqItem.getCqType().getValue() : "");
										if (answerCq.contains(StringUtils.checkString(cqOption.getValue()))) {
											availableOptions += "\u2022 ";
										} else {
											availableOptions += "  ";
										}
										if (cqItem.getCqType() == CqType.CHOICE || cqItem.getCqType() == CqType.LIST || cqItem.getCqType() == CqType.CHECKBOX) {
											availableOptions += cqOption.getValue() + "\n";
										} else if (cqItem.getCqType() == CqType.CHOICE_WITH_SCORE) {
											availableOptions += cqOption.getValue() + "/" + cqOption.getScoring() + "\n";
										}
									}
								}
							}

							if (cqItem.getCqType() == CqType.DATE) {
								cqItems.setOptionType("Date");
							}
							LOG.info("sourcingReqCqItem ANSWER>>>> Item " + cqItem.getItemName() + " Answer : " + availableOptions);
							cqItems.setFileName(fileName);
							cqItems.setAnswer(availableOptions);
							cqItemList.add(cqItems);
						}
					}

					cqs.setCqItem(cqItemList);
					allCqData.add(cqs);
				}
			}

			summary.setCqs(allCqData);
			// BQ

			List<SourcingFormRequestBq> bqs = bqDao.findBqsByFormId(sourcingFormRequest.getId());
			List<EvaluationBqPojo> allBqs = new ArrayList<EvaluationBqPojo>();

			if (CollectionUtil.isNotEmpty(bqs)) {
				for (SourcingFormRequestBq item : bqs) {
					EvaluationBqPojo bqPojo = new EvaluationBqPojo();
					bqPojo.setName(item.getName());

					List<SourcingFormRequestBqItem> bqItems = bqItemdao.getAllbqItemByBqId(item.getId());
					List<RequestBqItemPojo> evaluationBqItem = new ArrayList<RequestBqItemPojo>();
					if (CollectionUtil.isNotEmpty(bqItems)) {
						for (SourcingFormRequestBqItem bqItem : bqItems) {
							RequestBqItemPojo bi = new RequestBqItemPojo();
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
							bi.setDecimal(sourcingFormRequest.getDecimal());
							bi.setTotalAmt(bqItem.getTotalAmount() != null ? bqItem.getTotalAmount() : BigDecimal.ZERO);
							bi.setTotalAfterTax(bqItem.getTotalAmountWithTax() != null ? bqItem.getTotalAmountWithTax() : BigDecimal.ZERO);
							bi.setTaxAmt(bqItem.getTax() != null ? bqItem.getTax() : BigDecimal.ZERO);
							bi.setUnitBudgetPrice(bqItem.getUnitBudgetPrice());
							evaluationBqItem.add(bi);
						}
					}
					bqPojo.setRequestItems(evaluationBqItem);
					allBqs.add(bqPojo);
				}
			}
			summary.setBqs(allBqs);

			List<SourcingFormRequestSor> sors = sorDao.findSorsByFormId(sourcingFormRequest.getId());
			List<EvaluationSorPojo> allSors = new ArrayList<EvaluationSorPojo>();

			if (CollectionUtil.isNotEmpty(sors)) {
				for (SourcingFormRequestSor item : sors) {
					EvaluationSorPojo bqPojo = new EvaluationSorPojo();
					bqPojo.setName(item.getName());

					List<SourcingFormRequestSorItem> sorItems = sorItemdao.getAllsorItemBySorId(item.getId());
					List<RequestBqItemPojo> evaluationBqItem = new ArrayList<RequestBqItemPojo>();
					if (CollectionUtil.isNotEmpty(sorItems)) {
						for (SourcingFormRequestSorItem bqItem : sorItems) {
							RequestBqItemPojo bi = new RequestBqItemPojo();
							String priceType = "";
							bi.setLevel(bqItem.getLevel() + "." + bqItem.getOrder());
							bi.setItemName(bqItem.getItemName());
							bi.setDescription(bqItem.getItemDescription());
							bi.setUom(bqItem.getUom() != null ? bqItem.getUom().getUom() : "");
							bi.setImgPath(imgPath);
							bi.setPriceType(priceType);
							bi.setDecimal(sourcingFormRequest.getDecimal());
							bi.setTotalAmt(bqItem.getTotalAmount() != null ? bqItem.getTotalAmount() : BigDecimal.ZERO);
							evaluationBqItem.add(bi);
						}
					}
					bqPojo.setRequestItems(evaluationBqItem);
					allSors.add(bqPojo);
				}
			}

			summary.setSors(allSors);

			// Audit
			List<RequestAudit> requestAudit = requestService.getReqAudit(sourcingFormRequest.getId());
			List<RequestAuditPojo> timelineList = new ArrayList<RequestAuditPojo>();
			if (CollectionUtil.isNotEmpty(requestAudit)) {
				for (RequestAudit audit : requestAudit) {
					RequestAuditPojo requestaudit = new RequestAuditPojo();
					requestaudit.setActionDate(audit.getActionDate() != null ? sdf.format(audit.getActionDate()) : "");
					requestaudit.setDescription(audit.getDescription());
					requestaudit.setType(audit.getAction().name());
					requestaudit.setActionBy(audit.getActionBy() != null && StringUtils.checkString(audit.getActionBy().getName()).length() > 0 ? audit.getActionBy().getName() : "");
					timelineList.add(requestaudit);
				}
			}
			summary.setRequestAuditPojo(timelineList);

			// RFS Approvals
			List<SourcingFormApprovalRequest> approvals = request.getSourcingFormApprovalRequests();
			List<EvaluationAprovalUsersPojo> approvalList = new ArrayList<EvaluationAprovalUsersPojo>();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (SourcingFormApprovalRequest item : approvals) {
					EvaluationAprovalUsersPojo approve = new EvaluationAprovalUsersPojo();
					approve.setLevel(item.getLevel());
					List<EvaluationAprovalUsersPojo> approvUserList = new ArrayList<EvaluationAprovalUsersPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsersRequest())) {
						Integer index = 1;
						boolean statusFlag = false;
						boolean statusAndFlag = false;
						for (SourcingFormApprovalUserRequest usr : item.getApprovalUsersRequest()) {
							EvaluationAprovalUsersPojo usrs = new EvaluationAprovalUsersPojo();
							Integer cnt = item.getApprovalUsersRequest().size();

							if (usr.getApprovalStatus() == ApprovalStatus.APPROVED && usr.getApprovalRequest().getApprovalType() == ApprovalType.OR) {
								statusFlag = true;
							}
							if (usr.getApprovalStatus() == ApprovalStatus.PENDING && usr.getApprovalRequest().getApprovalType() == ApprovalType.AND) {
								statusAndFlag = true;
							}
							userName += "  " + usr.getUser().getName() + "  ";
							if (cnt > index) {
								userName += usr.getApprovalRequest().getApprovalType() != null ? usr.getApprovalRequest().getApprovalType().name() : "" + "  ";
							}
							if (cnt == index) {
								usrs.setName(userName);
								if (statusFlag) {
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
					approve.setApprovalList(approvUserList);
					approvalList.add(approve);
				}
			}
			summary.setApprovals(approvalList);

			// Comments
			List<RequestComment> comments = findAllSourcingCommentsByFormId(sourcingFormRequest.getId());
			List<EvaluationCommentsPojo> commentDetails = new ArrayList<EvaluationCommentsPojo>();
			if (CollectionUtil.isNotEmpty(comments)) {
				for (RequestComment item : comments) {
					EvaluationCommentsPojo ec = new EvaluationCommentsPojo();
					ec.setComment(item.getComment());
					ec.setCreatedBy(item.getCreatedBy().getName());
					if (item.getCreatedDate() != null) {
						ec.setCreatedDate(new Date(sdf.format(item.getCreatedDate())));
					}

					commentDetails.add(ec);
				}
			}

			summary.setApprovalComments(commentDetails);

			List<SourcingSummaryPojo> requestSummary = Arrays.asList(summary);
			parameters.put("RFS_SUMMARY", requestSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(requestSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate RFS Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private List<RequestComment> findAllSourcingCommentsByFormId(String formId) {
		return sourcingFormRequestDao.findAllSourcingCommentsByFormId(formId);
	}

	private List<EventTeamMember> getTeamMembersForEvent(String formId) {
		return sourcingFormRequestDao.getPlainTeamMembersForSourcing(formId);
	}

	@Override
	public SourcingFormRequest getSourcingFormByFormIdAndTenant(String formId, String tenantId) {
		return sourcingFormRequestDao.getSourcingFormByFormIdAndTenant(formId, tenantId);
	}

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Override
	@Transactional(readOnly = false)
	public void cancelSourcingRequest(String requestId, String reason) {
		try {

			SourcingFormRequest req = requestService.getSourcingRequestById(requestId);
			LOG.info("req-------------- " + req.getId());
			if (req != null) {
				req.setStatus(SourcingFormStatus.CANCELED);
				RequestAudit audit = new RequestAudit();
				audit.setActionDate(new Date());
				audit.setAction(RequestAuditType.CANCELLED);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setReq(req);
				audit.setDescription(reason);
				audit = requestService.saveAudit(audit);
				requestService.update(req);
				LOG.info("Audit ID " + audit.getId() + " Form ID " + audit.getReq().getId() + " Audit created Date is " + audit.getActionDate());
			}
		} catch (Exception e) {

			throw e;
		}
		try {
			SourcingFormRequest request = requestService.getSourcingRequestById(requestId);
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "Sourcing Form'" + request.getFormId() + "' is cancelled", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
	}

	@Override
	public List<SourcingFormRequestCqItem> getCqItembyRequestIdCqId(String requestId, String cqId) {
		List<SourcingFormRequestCqItem> list = sourcingFormRequestDao.getCqItembyRequestIdCqId(requestId, cqId);
		for (SourcingFormRequestCqItem sourcingFormRequestCqItem : list) {
			if (sourcingFormRequestCqItem.getCq() != null)
				sourcingFormRequestCqItem.getCq().getName();
		}
		return list;
	}

	@Override
	public boolean isBudgetCheckingEnabledForBusinessUnit(String tenantId, String formId) {
		return sourcingFormRequestDao.isBudgetCheckingEnabledForBusinessUnit(tenantId, formId);
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequest updateSourcingFormRequestApproval(SourcingFormRequest sourcingFormRequest, User loggedInUser) {
		SourcingFormRequest persistObj = sourcingFormRequestDao.findById(sourcingFormRequest.getId());
		List<SourcingFormApprovalRequest> finalList = new ArrayList<SourcingFormApprovalRequest>();
		int batchNo = 0;
		Map<Integer, SourcingFormApprovalRequest> map = new HashMap<Integer, SourcingFormApprovalRequest>();

		for (SourcingFormApprovalRequest iterable_element : persistObj.getSourcingFormApprovalRequests()) {
			if (iterable_element.isDone()) {
				batchNo = iterable_element.getBatchNo() != null ? iterable_element.getBatchNo() : 0;
			} else {
				batchNo = iterable_element.getBatchNo() != null ? iterable_element.getBatchNo() : 0;
				batchNo--;
				break;
			}
		}
		if (batchNo == 0) {
			batchNo = 1;
		} else {
			batchNo++;
		}

		List<String> auditMessages = new ArrayList<String>();

		// map of level < - > List of users
		Map<Integer, List<String>> existingUsers = new HashMap<Integer, List<String>>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<Integer, ApprovalType>();

		// existing data
		for (SourcingFormApprovalRequest approvalRequest : persistObj.getSourcingFormApprovalRequests()) {

			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.get(level);
			if (CollectionUtil.isEmpty(users)) {
				users = new ArrayList<String>();
			}
			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsersRequest())) {
				for (SourcingFormApprovalUserRequest auser : approvalRequest.getApprovalUsersRequest()) {
					users.add(auser.getUser().getName());
				}
			}
			existingUsers.put(level, users);
			existingTypes.put(level, approvalRequest.getApprovalType());
		}
		int newLevel = 1;
		if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingFormApprovalRequests())) {
			int sourcinglevel = 1;
			int level = 0;
			int level1 = 0;
			for (SourcingFormApprovalRequest app : sourcingFormRequest.getSourcingFormApprovalRequests()) {

				LOG.info("Id : " + app.getId() + " Level : " + app.getLevel());
				if (StringUtils.checkString(app.getId()).length() > 0) {
					SourcingFormApprovalRequest app1 = sourcingFormApprovalRequestDao.findSourcingFormApprovalById(app.getId());
					app.setActive(app1.isActive());
					app.setBatchNo(app1.getBatchNo());
					app.setDone(app1.isDone());
					app.setLevel(app1.getLevel());
					app.setSourcingFormRequest(app1.getSourcingFormRequest());
					level1 = app1.getLevel();
				} else {
					app.setSourcingFormRequest(persistObj);
					app.setLevel(level1++);
					app.setBatchNo(batchNo);
				}

				ApprovalType existingType = existingTypes.get(sourcinglevel);
				List<String> existingUserList = existingUsers.get(sourcinglevel);

				// Variable to store user list coming from frontend for current level
				List<String> levelUsers = new ArrayList<String>();

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsersRequest())) {
					for (SourcingFormApprovalUserRequest approvalUser : app.getApprovalUsersRequest()) {
						approvalUser.setApprovalRequest(app);
						approvalUser.setId(null);

						// If db existing user list is empty, then it means this is a new level which is not existing in
						// database yet.
						// therefore we need to add all user list coming from frontend for current level as new users
						if (CollectionUtil.isEmpty(existingUserList)) {
							LOG.info("existing level user empty sourcinglevel:" + sourcinglevel);
							auditMessages.add("Approval Level " + sourcinglevel + " User " + approvalUser.getUser().getName() + " has been added as Approver");
						} else {
							// If db existing user list does not contain the user coming from frontend, then it has been
							// added as new user for current level
							if (!existingUserList.contains(approvalUser.getUser().getName())) {
								LOG.info("existing level user added sourcinglevel:" + sourcinglevel);
								auditMessages.add("Approval Level " + sourcinglevel + " User " + approvalUser.getUser().getName() + " has been added as Approver");
							}
						}

						levelUsers.add(approvalUser.getUser().getName());
					}

					map.put(++level, app);

					/*
					 * Loop through the db existing user list for the current level and check if they exist in the
					 * userlist coming from frontend.
					 */
					if (CollectionUtil.isNotEmpty(existingUserList)) {
						for (String existing : existingUserList) {
							if (!levelUsers.contains(existing)) {
								auditMessages.add("Approval Level " + sourcinglevel + " User " + existing + " has been removed as Approver");
							}
						}
					}
				}
				// to check if approval type is changed
				if (existingType != null && existingType != app.getApprovalType() && CollectionUtil.isNotEmpty(app.getApprovalUsersRequest())) {
					auditMessages.add("Approval Level " + sourcinglevel + " Type changed from " + (existingType == ApprovalType.OR ? "Any" : "All") + " to " + (app.getApprovalType() == ApprovalType.OR ? "Any" : "All"));
				}
				sourcinglevel++;
				newLevel = sourcinglevel;
			}
			for (Map.Entry<Integer, SourcingFormApprovalRequest> entry : map.entrySet()) {
				LOG.info("Key  : " + entry.getKey() + "   Level : " + entry.getValue().getLevel());
				SourcingFormApprovalRequest appp = entry.getValue();
				appp.setLevel(entry.getKey());
				finalList.add(appp);
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

		if (CollectionUtil.isNotEmpty(finalList)) {
			persistObj.setSourcingFormApprovalRequests(finalList);
		} else {
			persistObj.setSourcingFormApprovalRequests(null);
		}
		persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
		persistObj.setModifiedDate(new Date());
		persistObj = sourcingFormRequestDao.saveOrUpdate(persistObj);

		if (CollectionUtil.isNotEmpty(auditMessages)) {
			// add to audit
			String auditMessage = "";
			for (String msg : auditMessages) {
				auditMessage += msg + ". ";
			}
			try {
				RequestAudit audit = new RequestAudit(loggedInUser, new Date(), RequestAuditType.UPDATE, auditMessage, persistObj);
				requestAuditDao.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Approval Route is updated for Sourcing Request '" + persistObj.getFormId() + "' ." + auditMessage, loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.RFS);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error("Error saving Sourcing Request Audit for change of approvers : " + e.getMessage(), e);
			}
		}
		try {
			sendEmailNotificationToActiveApprovers(persistObj);
		} catch (Exception e) {
			LOG.info("Error while sending email to active approver users:" + e.getMessage());
		}
		return persistObj;

	}

	@Override
	public List<SourcingFormRequestPojo> getAllExcelSearchSourcingReportForBuyer(String tenantId, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return sourcingFormRequestDao.getAllSourcingWithSearchFilter(tenantId, eventIds, sourcingFormRequestPojo, select_all, startDate, endDate);
	}

	@Override
	public List<SourcingFormRequestPojo> getAllSourcingFormRequestList(User user, Object id, TableDataInput input, Date startDate, Date endDate) {
		return convertToPojo(sourcingFormRequestDao.getAllSourcingFormRequestList(user, (String) id, input, startDate, endDate));
	}

	@Override
	public long getAllSourcingFormRequestFilterList(User user, Object id, TableDataInput input, Date startDate, Date endDate) {
		return sourcingFormRequestDao.getAllSourcingFormRequestFilterList(user, (String) id, input, startDate, endDate);
	}

	@Override
	public String findUploadFileName(String docId) {
		return rfsDocumentDao.findUploadFileName(docId);
	}

	@Override
	public List<String> getNotSectionAddedRfsBq(String requestId) {
		return sourcingFormRequestDao.getNotSectionAddedRfsBq(requestId);
	}

	@Override
	public List<String> getNotSectionItemAddedRfsBq(String requestId) {
		return sourcingFormRequestDao.getNotSectionItemAddedRfsBq(requestId);
	}

	@Override
	public long findTotaApprovalLevelsRequestCount(String requestId) {
		return sourcingFormRequestDao.findTotaApprovalLevelsRequestCount(requestId);
	}

	@Override
	public void downloadCsvFileForSourcing(HttpServletResponse response, File file, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, Date startDate, Date endDate, boolean select_all, String tenantId, User user, String id, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.SOURCING_REPORT_CSV_COLUMNS_EXPORT;
			String[] columns = { "templateName", "formId", "referanceNumber", "sourcingFormName", "description", "formOwner", "createdDateStr", "submittedDatestr", "approvedDateStr", "approvalDaysHours", "approvalTotalLevels", "approvalTotalUsers", "procurementMethod", "procurementCategories", "businessunit", "costcenter", "groupCode", "baseCurrency", "availableBudget", "estimatedBudget", "historicAmount", "status" };
//TODO
			long count = sourcingFormRequestDao.getTotalSourcingRequestCountForList(user, id, startDate, endDate);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<SourcingFormRequestPojo> list = findAllActiveSourcingEventsForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, eventIds, sourcingFormRequestPojo, select_all, startDate, endDate, user, id);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (SourcingFormRequestPojo pojo : list) {
					LOG.info("BUUUUU .... :" + pojo.getBusinessunit() + " :.... costcenter " + pojo.getCostcenter() + " .. ." + "..   grp code.... " + pojo.getGroupCode());

					if (pojo.getCreatedDate() != null) {
						LOG.info("*******************" + pojo.getCreatedDate());
						pojo.setCreatedDateStr(sdf.format(pojo.getCreatedDate()));
					}
					if (pojo.getApprovedDate() != null) {
						pojo.setApprovedDateStr(sdf.format(pojo.getApprovedDate()));
					}
					if (pojo.getSubmittedDate() != null) {
						pojo.setSubmittedDatestr(sdf.format(pojo.getSubmittedDate()));
					}

					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.error("Error ..." + e.getMessage(), e);
		}
	}

	private static CellProcessor[] getProcessors() {
		final CellProcessor[] processors = new CellProcessor[] {

				new Optional(), // Temp name
				new NotNull(), // Event id
				new Optional(), new Optional(), new Optional(), new Optional(), //
				new Optional(), //
				new Optional(), // Submitted Date
				new Optional(), new Optional(), //
				new Optional(), // approvalTotalLevels
				new Optional(), new Optional(), new Optional(), // costCenter
				new Optional(), //
				new Optional(), new Optional(), new Optional(), //
				new Optional(), //
				new Optional(), new Optional(), new Optional() };
		return processors;
	}

	private List<SourcingFormRequestPojo> findAllActiveSourcingEventsForTenantIdForCsv(String tenantId, int page_size, int pageNo, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate, User user, String id) {

		TableDataInput tdi = new TableDataInput();
		tdi.setColumns(new ArrayList<ColumnParameter>());

		if (eventIds != null && !select_all) {
			ColumnParameter cp = new ColumnParameter();
			cp.setSearch(new SearchParameter());
			cp.setData("eventIds");
			cp.getSearch().setValue(String.join(",", eventIds));
			cp.setSearchable(true);
			tdi.getColumns().add(cp);
		}

		if (sourcingFormRequestPojo != null) {
			if (eventIds != null && !select_all) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("eventIds");
				cp.getSearch().setValue(String.join(",", eventIds));
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getSourcingrequestid()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("sourcingrequestid");
				cp.getSearch().setValue(sourcingFormRequestPojo.getSourcingrequestid());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getNameofrequest()).length() > 0) {

				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("sourcingFormName");
				cp.getSearch().setValue(sourcingFormRequestPojo.getNameofrequest());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getReferencenumber()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("referencenumber");
				cp.getSearch().setValue(sourcingFormRequestPojo.getReferencenumber());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getCreatedBy()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("createdBy");
				cp.getSearch().setValue(sourcingFormRequestPojo.getCreatedBy());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getRequestowner()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("requestowner");
				cp.getSearch().setValue(sourcingFormRequestPojo.getRequestowner());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getBusinessunit()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("businessunit");
				cp.getSearch().setValue(sourcingFormRequestPojo.getBusinessunit());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getCostcenter()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("costcenter");
				cp.getSearch().setValue(sourcingFormRequestPojo.getCostcenter());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getBaseCurrency()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("baseCurrency");
				cp.getSearch().setValue(sourcingFormRequestPojo.getBaseCurrency());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (sourcingFormRequestPojo.getStatus() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("status");
				cp.getSearch().setValue(sourcingFormRequestPojo.getStatus().toString());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (sourcingFormRequestPojo.getProcurementMethod() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("procurementMethod");
				cp.getSearch().setValue(sourcingFormRequestPojo.getProcurementMethod());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (sourcingFormRequestPojo.getProcurementCategories() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("procurementCategories");
				cp.getSearch().setValue(sourcingFormRequestPojo.getProcurementCategories());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
		}

		List<SourcingFormRequestPojo> requestList = sourcingFormRequestDao.getAllSourcingRequestForCsv(user, id, tdi, startDate, endDate, page_size, pageNo);

		return requestList;
	}

	private List<SourcingFormRequestPojo> findAllActiveSourcingEventsForBizUnitForCsv(String tenantId, int page_size, int pageNo, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate, User user, String id,List<String> bizUnitIds) {

		TableDataInput tdi = new TableDataInput();
		tdi.setColumns(new ArrayList<ColumnParameter>());

		if (eventIds != null && !select_all) {
			ColumnParameter cp = new ColumnParameter();
			cp.setSearch(new SearchParameter());
			cp.setData("eventIds");
			cp.getSearch().setValue(String.join(",", eventIds));
			cp.setSearchable(true);
			tdi.getColumns().add(cp);
		}

		if (sourcingFormRequestPojo != null) {
			if (eventIds != null && !select_all) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("eventIds");
				cp.getSearch().setValue(String.join(",", eventIds));
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getSourcingrequestid()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("sourcingrequestid");
				cp.getSearch().setValue(sourcingFormRequestPojo.getSourcingrequestid());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getNameofrequest()).length() > 0) {

				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("sourcingFormName");
				cp.getSearch().setValue(sourcingFormRequestPojo.getNameofrequest());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getReferencenumber()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("referencenumber");
				cp.getSearch().setValue(sourcingFormRequestPojo.getReferencenumber());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getCreatedBy()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("createdBy");
				cp.getSearch().setValue(sourcingFormRequestPojo.getCreatedBy());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getRequestowner()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("requestowner");
				cp.getSearch().setValue(sourcingFormRequestPojo.getRequestowner());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getBusinessunit()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("businessunit");
				cp.getSearch().setValue(sourcingFormRequestPojo.getBusinessunit());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getCostcenter()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("costcenter");
				cp.getSearch().setValue(sourcingFormRequestPojo.getCostcenter());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(sourcingFormRequestPojo.getBaseCurrency()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("baseCurrency");
				cp.getSearch().setValue(sourcingFormRequestPojo.getBaseCurrency());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (sourcingFormRequestPojo.getStatus() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("status");
				cp.getSearch().setValue(sourcingFormRequestPojo.getStatus().toString());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (sourcingFormRequestPojo.getProcurementMethod() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("procurementMethod");
				cp.getSearch().setValue(sourcingFormRequestPojo.getProcurementMethod());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (sourcingFormRequestPojo.getProcurementCategories() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("procurementCategories");
				cp.getSearch().setValue(sourcingFormRequestPojo.getProcurementCategories());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
		}

		List<SourcingFormRequestPojo> requestList = sourcingFormRequestDao.getAllSourcingRequestForCsv(user, id, tdi, startDate, endDate, page_size, pageNo);
		//List<SourcingFormRequestPojo> requestListNew = sourcingFormRequestDao.getAllSourcingRequestBizUnitForCsv(user, id, tdi, startDate, endDate, page_size, pageNo,bizUnitIds);

		return requestList;
	}


	@Override
	public List<SourcingFormRequestPojo> getAllSourcingRequestForList(User user, String id, TableDataInput input, Date startDate, Date endDate) {
		return sourcingFormRequestDao.getAllSourcingRequestForList(user, id, input, startDate, endDate, 0, 0);
	}

	@Override
	public long getFilteredCountOfSourcingRequestForList(User user, String id, TableDataInput input, Date startDate, Date endDate) {
		return sourcingFormRequestDao.getFilteredCountOfSourcingRequestForList(user, id, input, startDate, endDate);
	}

	@Override
	public long getTotalSourcingRequestCountForList(User user, String id, Date startDate, Date endDate) {
		return sourcingFormRequestDao.getTotalSourcingRequestCountForList(user, id, startDate, endDate);
	}

	@Override
	public long getTotalSourcingRequestCountForTenantId(String searchValue, String tenantId, String userId) {
		return sourcingFormRequestDao.getTotalSourcingRequestCountForTenantId(searchValue, tenantId, userId);
	}

	@Override
	public List<SourcingFormRequest> searchSourcingRequestByNameAndRefNumForTenantId(String searchValue, Integer pageNo, Integer pageLength, String tenantId, String userId) {
		Integer start = 0;
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (pageLength != null) {
			start = start * pageLength;
		}
		LOG.info(" start  : " + start);

		List<SourcingFormRequest> sourcingFormRequestList = sourcingFormRequestDao.searchSourcingRequestByNameAndRefNumForTenantId(searchValue, pageNo, pageLength, start, tenantId, userId);

		if (CollectionUtil.isNotEmpty(sourcingFormRequestList)) {
			for (SourcingFormRequest sourcingFormRequest : sourcingFormRequestList) {
				sourcingFormRequest.setModifiedBy(null);
				sourcingFormRequest.setActionBy(null);
				sourcingFormRequest.setBusinessUnit(null);
				sourcingFormRequest.setSourcingFormApprovalRequests(null);
				sourcingFormRequest.setCreatedByName(sourcingFormRequest.getCreatedBy().getName());
				sourcingFormRequest.setCostCenter(null);
				sourcingFormRequest.setCurrency(null);
				sourcingFormRequest.setProcurementCategories(null);
				sourcingFormRequest.setProcurementMethod(null);
				sourcingFormRequest.setGroupCode(null);
			}
		}
		return sourcingFormRequestList;
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequest concludeSourcingRequest(SourcingFormRequest sourcingRequest, User loggedInUser) {
		SourcingFormRequest dbRequest = getSourcingRequestById(sourcingRequest.getId());
		dbRequest.setStatus(SourcingFormStatus.CONCLUDED);
		dbRequest.setConcludeRemarks(sourcingRequest.getConcludeRemarks());
		dbRequest.setConcludeBy(loggedInUser);
		dbRequest.setConcludeDate(new Date());
		SourcingFormRequest requet = sourcingFormRequestDao.update(dbRequest);

		// TatReport tatReport = tatReportService.getRfsForTatReportByRfsIDAndFormIdAndTenantId(requet.getFormId(),
		// loggedInUser.getTenantId(), requet.getId());
		long resul = tatReportService.updateTatReportReqStatus(requet.getFormId(), loggedInUser.getTenantId(), requet.getId(), SourcingFormStatus.CONCLUDED);
		if (resul > 0l) {
			LOG.info("Tat Report updated conclude status Form Id  " + requet.getFormId() + " and RFS ID : " + requet.getId());
		} else {
			TatReport tatReport = new TatReport();
			tatReport.setRequestGeneratedId(dbRequest.getId());
			tatReport.setFormId(dbRequest.getFormId());
			tatReport.setSourcingFormName(dbRequest.getSourcingFormName());
			tatReport.setBusinessUnit(dbRequest.getBusinessUnit().getUnitName());
			tatReport.setCostCenter(dbRequest.getCostCenter() != null ? dbRequest.getCostCenter().getCostCenter() : "");
			tatReport.setRequestOwner(dbRequest.getFormOwner() != null ? (dbRequest.getFormOwner().getName() + " " + dbRequest.getFormOwner().getLoginId()) : "");
			tatReport.setGroupCode(dbRequest.getGroupCode() != null ? dbRequest.getGroupCode().getGroupCode() : (dbRequest.getGroupCodeOld() != null ? dbRequest.getGroupCodeOld() : ""));
			tatReport.setCreatedDate(dbRequest.getCreatedDate());
			tatReport.setProcurementMethod(dbRequest.getProcurementMethod() != null ? dbRequest.getProcurementMethod().getProcurementMethod() : "");
			tatReport.setProcurementCategories(dbRequest.getProcurementCategories() != null ? dbRequest.getProcurementCategories().getProcurementCategories() : "");
			tatReport.setBaseCurrency(dbRequest.getCurrency() != null ? dbRequest.getCurrency().getCurrencyCode() : "");
			tatReport.setTenantId(loggedInUser.getTenantId());
			tatReport.setReqDecimal(dbRequest.getDecimal());

			tatReport.setFirstApprovedDate(dbRequest.getFirstApprovedDate() != null ? dbRequest.getFirstApprovedDate() : null);
			tatReport.setLastApprovedDate(dbRequest.getApprovedDate() != null ? dbRequest.getApprovedDate() : null);

			if (dbRequest.getApprovedDate() != null && dbRequest.getSubmittedDate() != null) {
				double diffInDays = DateUtil.differenceInDays(dbRequest.getApprovedDate(), dbRequest.getSubmittedDate());
				tatReport.setReqApprovalDaysCount(BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP));
			}

			tatReport.setRequestStatus(SourcingFormStatus.CONCLUDED);
			tatReportService.saveTatReport(tatReport);
		}

		return requet;
	}

	@Override
	public List<SourcingFormRequestPojo> getAllSourcingRequestListForAssignedBizUnit(User user, String id, TableDataInput input, Date startDate, Date endDate,List<String> businessUnitIds) {
		return sourcingFormRequestDao.getAllSourcingRequestListForAssignedBizUnit(user, id, input, startDate, endDate, 0, 0,businessUnitIds);
	}

	@Override
	public long getFilteredCountOfSourcingRequestListBizUnit(User user, String id, TableDataInput input, Date startDate, Date endDate,List<String> businessUnitIds) {
		return sourcingFormRequestDao.getFilteredCountOfSourcingRequestListBizUnit(user, id, input, startDate, endDate,businessUnitIds);
	}

	@Override
	public long getTotalSourcingRequestCountListBizUnit(User user, String id, Date startDate, Date endDate,List<String> businessUnitIds) {
		return sourcingFormRequestDao.getTotalSourcingRequestCountBizUnit(user, id, startDate, endDate,businessUnitIds);
	}

	public void downloadCsvFileForSourcingBizUnit(HttpServletResponse response, File file, String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, Date startDate, Date endDate, boolean select_all, String tenantId, User user, String id, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.SOURCING_REPORT_CSV_COLUMNS_EXPORT;
			String[] columns = { "templateName", "formId", "referanceNumber", "sourcingFormName", "description", "formOwner", "createdDateStr", "submittedDatestr", "approvedDateStr", "approvalDaysHours", "approvalTotalLevels", "approvalTotalUsers", "procurementMethod", "procurementCategories", "businessunit", "costcenter", "groupCode", "baseCurrency", "availableBudget", "estimatedBudget", "historicAmount", "status" };
//TODO
			long count = sourcingFormRequestDao.getTotalSourcingRequestCountForList(user, id, startDate, endDate);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<SourcingFormRequestPojo> list = findAllActiveSourcingEventsForTenantIdForCsv(tenantId, PAGE_SIZE, pageNo, eventIds, sourcingFormRequestPojo, select_all, startDate, endDate, user, id);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (SourcingFormRequestPojo pojo : list) {
					LOG.info("BUUUUU .... :" + pojo.getBusinessunit() + " :.... costcenter " + pojo.getCostcenter() + " .. ." + "..   grp code.... " + pojo.getGroupCode());

					if (pojo.getCreatedDate() != null) {
						LOG.info("*******************" + pojo.getCreatedDate());
						pojo.setCreatedDateStr(sdf.format(pojo.getCreatedDate()));
					}
					if (pojo.getApprovedDate() != null) {
						pojo.setApprovedDateStr(sdf.format(pojo.getApprovedDate()));
					}
					if (pojo.getSubmittedDate() != null) {
						pojo.setSubmittedDatestr(sdf.format(pojo.getSubmittedDate()));
					}

					beanWriter.write(pojo, columns, processors);
				}
				beanWriter.flush();
				LOG.info("Write done........");
			}

			beanWriter.close();
			beanWriter = null;

		} catch (IOException e) {
			LOG.error("Error ..." + e.getMessage(), e);
		}
	}

}