package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ErpIntegrationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.exceptions.SecurityRuntimeException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.PrErp2Pojo;
import com.privasia.procurehere.core.pojo.PrErpPojo;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.PublishEventService;
import com.privasia.procurehere.integration.RequestResponseLoggingInterceptor;
import com.privasia.procurehere.integration.RestTemplateResponseErrorHandler;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author Parveen
 */
@Service
@Transactional(readOnly = true)
public class ApprovalServiceImpl implements ApprovalService {

    public static final Logger LOG = LogManager.getLogger(ApprovalServiceImpl.class);

    @Autowired
    ErpIntegrationService erpIntegrationService;

    @Autowired
    PrService prService;

    @Autowired
    private SourcingFormRequestService requestService;
    @Autowired
    RftEventDao rftEventDao;

    @Autowired
    RfpEventDao rfpEventDao;

    @Autowired
    RfqEventDao rfqEventDao;

    @Autowired
    RfiEventDao rfiEventDao;

    @Autowired
    RfaEventDao rfaEventDao;

    @Autowired
    MessageSource messageSource;

    @Autowired
    NotificationService notificationService;

    @Autowired
    EventAuditService eventAuditService;

    @Value("${app.url}")
    String APP_URL;

    @Autowired
    PrDao prDao;

    @Autowired
    BudgetService budgetService;

    @Autowired
    TransactionLogService transactionLogService;

    @Autowired
    BudgetDao budgetDao;

    @Autowired
    RftEventService rftEventService;

    @Autowired
    RfpEventService rfpEventService;

    @Autowired
    RfiEventService rfiEventService;

    @Autowired
    RfaEventService rfaEventService;

    @Autowired
    RfqEventService rfqEventService;

    @Autowired
    SourcingFormRequestDao sourcingFormRequestDao;

    @Autowired
    SupplierSettingsService supplierSettingsService;

    @Autowired
    BuyerSettingsService buyerSettingsService;

    @Autowired
    DashboardNotificationService dashboardNotificationService;

    @Autowired
    UserDao userDao;

    @Autowired
    ErpSetupDao erpConfigDao;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ErpAuditService erpAuditService;

    @Autowired
    PrAuditService prAuditService;

    @Autowired
    ErpSetupService erpSetupService;

    @Autowired
    PoFinanceService poFinanceService;

    @Autowired
    PoFinanceDao poFinanceDao;

    @Autowired
    FinanceSettingsService financeSettingsService;

    @Autowired
    PoSharingBuyerDao poSharingBuyerDao;

    @Autowired
    PublishEventService publishEventService;

    @Autowired
    SnapShotAuditService snapShotAuditService;

    @Autowired
    ProductContractItemsDao productContractItemsDao;

    @Autowired
    PoService poService;

    @Autowired
    PoAuditService poAuditService;

    @Autowired
    FavoriteSupplierService favoriteSupplierService;

    @Autowired
    SupplierFormSubmissionDao supplierFormSubmissionDao;

    @Autowired
    TatReportService tatReportService;

    @Autowired
    RfaEventAuditDao rfaEventAuditDao;

    @Autowired
    RfiEventAuditDao rfiEventAuditDao;

    @Autowired
    RfpEventAuditDao rfpEventAuditDao;

    @Autowired
    RfqEventAuditDao rfqEventAuditDao;

    @Autowired
    RftEventAuditDao rftEventAuditDao;

    @Autowired
    BuyerAuditTrailDao buyerAuditTrailDao;

    @Autowired
    SupplierAuditTrailDao supplierAuditTrailDao;

    @Autowired
    SupplierPerformanceEvaluatorUserDao supplierPerformanceEvaluatorUserDao;

    @Autowired
    SupplierPerformanceEvaluatorUserService supplierPerformanceEvaluatorUserService;

    @Autowired
    PoDao poDao;

    @Autowired
    POSnapshotDocumentDao pOSnapshotDocumentDao;

    @Autowired
    SupplierPerformanceAuditService supplierPerformanceAuditService;

    @Autowired
    SupplierPerformanceEvaluationService supplierPerformanceEvaluationService;

    @Autowired
    ContractAuditDao contractAuditDao;

    @Autowired
    ProductContractDao productContractDao;

    @Autowired
    EventIdSettingsDao eventIdSettingsDao;

    @Autowired
    UserService userService;

    String remarkAction ="";

    @Override
    @Transactional(readOnly = false)
    public Pr doApproval(Pr pr, User loggedInUser, Boolean isFInish) throws Exception {
        try {
            String buyerTimeZone = "GMT+8:00";
            Buyer buyer = new Buyer();
            buyer.setId(loggedInUser.getTenantId());

            if (Boolean.TRUE == isFInish) {
                // Finish Audit
                try {
                    PrAudit audit = new PrAudit();
                    audit.setAction(PrAuditType.FINISH);
                    audit.setActionBy(loggedInUser);
                    audit.setActionDate(new Date());
                    audit.setBuyer(buyer);
                    audit.setDescription(
                            messageSource.getMessage("pr.audit.finish", new Object[] { pr.getPrId() }, Global.LOCALE));
                    audit.setPr(pr);
                    prAuditService.save(audit);
                } catch (Exception e) {
                    LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                }

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.FINISH,
                            "PR '" + pr.getPrId() + "' finished", loggedInUser.getTenantId(), loggedInUser, new Date(),
                            ModuleType.PR);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }
            }

            pr = prDao.findPrForApprovalById(pr.getId());
            List<PrApproval> approvalList = pr.getPrApprovals(); // prService.getAllPrApprovalsByPrId(pr.getId());

            // If no approval setup - direct approve the PR
            if (CollectionUtil.isEmpty(approvalList)) {
                ErpSetup erpConfig = erpConfigDao.getErpConfigBytenantId(loggedInUser.getTenantId());
                if (erpConfig != null && Boolean.TRUE == erpConfig.getIsErpEnable()) {
                    LOG.info("ERP Integration is enabled.... sending PR to ERP...");
                    sendingPrToErpAgent(loggedInUser, pr, erpConfig);
                } else {
                    pr.setStatus(PrStatus.APPROVED);
                    LOG.info("status :" + pr.getStatus());

                    try {
                        PrAudit audit = new PrAudit();
                        audit.setAction(PrAuditType.APPROVED);
                        audit.setActionBy(loggedInUser);
                        audit.setActionDate(new Date());
                        audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
                        audit.setDescription(messageSource.getMessage("pr.audit.approve", new Object[] { pr.getPrId() },
                                Global.LOCALE));
                        audit.setPr(pr);
                        prAuditService.save(audit);
                    } catch (Exception e) {
                        LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                    }

                    try {
                        LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                        BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                                "PR '" + pr.getPrId() + "' Approved", loggedInUser.getTenantId(), loggedInUser,
                                new Date(), ModuleType.PR);
                        buyerAuditTrailDao.save(buyerAuditTrail);
                        LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                    } catch (Exception e) {
                        LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                    }
                    /********************** Call PR to Finance ***********************************/
                    Boolean isAutoCreatePo = buyerSettingsService
                            .isAutoCreatePoSettingsByTenantId(loggedInUser.getTenantId());
                    if(isAutoCreatePo){
                        erpConfig.setIsGeneratePo(true);
                    }
                    if (erpConfig != null && Boolean.TRUE == erpConfig.getIsGeneratePo()
                            && Boolean.TRUE == isAutoCreatePo) {
                        Po po = prService.createPo(pr.getCreatedBy(), pr);
                        if (po != null && StringUtils.checkString(po.getId()).length() > 0) {
                            LOG.info("po created succefully:" + po.getId());
                            pr.setPoNumber(po.getPoNumber());
                            pr.setPoCreatedDate(new Date());
                            pr.setIsPo(Boolean.TRUE);
                            sendPoCreatedEmail(pr.getCreatedBy(), pr, pr.getCreatedBy());
                            try {
                                if (po.getStatus() == PoStatus.ORDERED) {
                                    po = poService.getLoadedPoById(po.getId());
                                    if (po.getSupplier() != null) {
                                        sendPoReceivedEmailNotificationToSupplier(po, po.getCreatedBy());
                                    }
                                }
                            } catch (Exception e) {
                                LOG.error("Error while sending PO email notification to supplier:" + e.getMessage(), e);
                            }
                        }
                    }
                }
            } else {
                PrApproval currentLevel = null;
                if (pr.getStatus() == PrStatus.DRAFT) {
                    pr.setStatus(PrStatus.PENDING);
                    for (PrApproval prApproval : approvalList) {
                        if (prApproval.getLevel() == 1) {
                            prApproval.setActive(true);
                            break;
                        }
                    }
                } else {
                    for (PrApproval prApproval : approvalList) {
                        if (prApproval.isActive()) {
                            currentLevel = prApproval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (PrApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the PR.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, pr);
                    }
                }
                // Just send emails to users.
                for (PrApproval prApproval : approvalList) {
                    if (prApproval.isActive()) {
                        for (PrApprovalUser user : prApproval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                buyerTimeZone = getTimeZoneByBuyerSettings(user.getUser().getTenantId(), buyerTimeZone);
                                sendEmailToPrApprovers(pr, user, buyerTimeZone);

                                if (Boolean.TRUE == pr.getEnableApprovalReminder()) {
                                    Integer reminderHr = pr.getReminderAfterHour();
                                    Integer reminderCpunt = pr.getReminderCount();
                                    if (reminderHr != null && reminderCpunt != null) {
                                        Calendar now = Calendar.getInstance();
                                        now.add(Calendar.HOUR, reminderHr);
                                        user.setNextReminderTime(now.getTime());
                                        user.setReminderCount(reminderCpunt);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            pr.setSummaryCompleted(true);
            pr.setPrCreatedDate(new Date());
            pr = prDao.update(pr);
        } catch (Exception e) {
            LOG.info("ERROR While Approving Pr :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving Pr :" + e.getMessage());
        }
        return pr;
    }

    private PrErpPojo constructPrErpObject(Pr pr) {
        PrErpPojo prErpPojo = new PrErpPojo(pr);
        return prErpPojo;
    }

    @SuppressWarnings("unused")
    private String generatePo(Pr pr) {
        String poNumber = null;
        if (pr.getPrId().startsWith("PR")) {
            poNumber = pr.getPrId().replaceAll("PR", "PO");
        } else {
            poNumber = "PO" + pr.getPrId();
        }
        return poNumber;
    }

    @Override
    @Transactional(readOnly = false)
    public Pr doApproval(Pr pr, User actionBy, String remarks, boolean approved) throws Exception {

        pr = prDao.findPrForApprovalById(pr.getId());

        List<PrApproval> approvalList = pr.getPrApprovals();
        // prService.getAllPrApprovalsByPrId(pr.getId());
        List<PrTeamMember> teamMembers = pr.getPrTeamMembers();

        // Identify Current Approval Level
        PrApproval currentLevel = null;
        for (PrApproval prApproval : approvalList) {
            if (prApproval.isActive()) {
                currentLevel = prApproval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        PrApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (PrApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject PR '" + pr.getName()
                    + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject PR '"
                    + pr.getName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " PR at : "
                    + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " PR at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (pr.getPrComments() == null) {
            pr.setPrComments(new ArrayList<PrComment>());
        }
        PrComment prComment = new PrComment();
        prComment.setComment(remarks);
        prComment.setApproved(approved);
        prComment.setCreatedBy(actionBy);
        prComment.setCreatedDate(new Date());
        prComment.setPr(pr);
        prComment.setApprovalUserId(actionUser.getId());
        pr.getPrComments().add(prComment);

        // If rejected
        if (!approved) {

            // Reset all approvals for re-approval as the PR is rejected.
            for (PrApproval prApproval : approvalList) {
                prApproval.setDone(false);
                prApproval.setActive(false);
                for (PrApprovalUser user : prApproval.getApprovalUsers()) {
                    try {
                        // Send rejection email to all approver users
                        // if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                        sendPrRejectionEmail(user.getUser(), pr, actionBy, remarks);
                        // }
                    } catch (Exception e) {
                        LOG.info("ERROR while Sending PR reject mail :" + e.getMessage(), e);
                    }
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                }
            }

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (PrTeamMember teamMember : teamMembers) {
                    try {
                        sendPrRejectionEmail(teamMember.getUser(), pr, actionBy, remarks);
                    } catch (Exception e) {
                        LOG.info("ERROR while Sending PR reject mail :" + e.getMessage(), e);
                    }
                }
            }

            // actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);
            LOG.info("User " + actionBy.getName() + " has Rejected the PR : " + pr.getName());
            pr.setStatus(PrStatus.DRAFT);
            try {
                if (pr.getCreatedBy() != null) {
                    sendPrRejectionEmail(pr.getCreatedBy(), pr, actionBy, remarks);
                }

                Pr prForBudget = prService.findPrBUAndCCForBudgetById(pr.getId());
                if (Boolean.TRUE == prForBudget.getLockBudget()) {
                    // updateBudget when PR rejected
                    Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(
                            prForBudget.getBusinessUnit().getId(), prForBudget.getCostCenter().getId());
                    // convert PR amount if currency is different
                    BigDecimal prAfterConversion = null;

                    if (budget != null) {
                        // create new transaction of budget
                        TransactionLog transactionLog = new TransactionLog();
                        if (null != pr.getConversionRate()
                                && !(0 == pr.getConversionRate().compareTo(BigDecimal.ZERO))) {
                            prAfterConversion = prForBudget.getGrandTotal().multiply(pr.getConversionRate());
                            LOG.info("**************************prAfterConversion " + prAfterConversion);
                            transactionLog.setConversionRateAmount(prForBudget.getConversionRate());
                            transactionLog.setAfterConversionAmount(prAfterConversion);

                        }
                        // if budget locking enabled
                        if (prForBudget.getLockBudget()) {
                            budget.setLockedAmount(budget.getLockedAmount()
                                    .subtract(prAfterConversion != null ? prAfterConversion : pr.getGrandTotal()));
                        } else {
                            budget.setPendingAmount(budget.getPendingAmount()
                                    .subtract(prAfterConversion != null ? prAfterConversion : pr.getGrandTotal()));
                        }
                        budget.setRemainingAmount(budget.getRemainingAmount()
                                .add(prAfterConversion != null ? prAfterConversion : pr.getGrandTotal()));

                        transactionLog.setBudget(budget);
                        transactionLog.setReferanceNumber(budget.getBudgetId());
                        transactionLog.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
                        transactionLog.setTransactionTimeStamp(new Date());
                        transactionLog.setAddAmount(prAfterConversion != null ? prAfterConversion : pr.getGrandTotal());
                        transactionLog.setPrBaseCurrency(prForBudget.getCurrency().getCurrencyCode());
                        transactionLog.setTransactionLogStatus(TransactionLogStatus.RELEASE);
                        transactionLog.setRemainingAmount(budget.getRemainingAmount());
                        transactionLogService.saveTransactionLog(transactionLog);
                        budgetService.updateBudget(budget);
                    }
                }

            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the PR : " + pr.getName());

            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            // Send email notification to Creator
            sendPrApprovalEmail(pr.getCreatedBy(), pr, actionBy, remarks);

            // Send email notification to Creator
            sendPrApprovalEmail(actionBy, pr, actionBy, remarks);

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (PrTeamMember teamMember : teamMembers) {
                    try {
                        sendPrApprovalEmail(teamMember.getUser(), pr, actionBy, remarks);
                    } catch (Exception e) {
                        LOG.info("ERROR while Sending PR reject mail :" + e.getMessage(), e);
                    }
                }
            }

            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                try {
                    PrAudit audit = new PrAudit();
                    audit.setAction(PrAuditType.APPROVED);
                    audit.setActionBy(actionBy);
                    audit.setActionDate(new Date());
                    audit.setBuyer(pr.getBuyer());
                    audit.setDescription(
                            messageSource.getMessage("pr.audit.approve", new Object[] { pr.getPrId() }, Global.LOCALE));
                    audit.setPr(pr);
                    prAuditService.save(audit);
                } catch (Exception e) {
                    LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                }
                setNextOrAllDone(actionBy, approvalList, currentLevel, pr);
                // pr.setPrApprovals(approvalList);
                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "PR '" + pr.getPrId() + "' Approved", actionBy.getTenantId(), actionBy, new Date(),
                            ModuleType.PR);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }

            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (PrApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            LOG.info("All users of this level have not approved the PR.");
                            allUsersDone = false;
                            break;
                        }
                    }
                }

                try {
                    PrAudit audit = new PrAudit();
                    audit.setAction(PrAuditType.APPROVED);
                    audit.setActionBy(actionBy);
                    audit.setActionDate(new Date());
                    audit.setBuyer(pr.getBuyer());
                    audit.setDescription(
                            messageSource.getMessage("pr.audit.approve", new Object[] { pr.getPrId() }, Global.LOCALE));
                    audit.setPr(pr);
                    prAuditService.save(audit);
                } catch (Exception e) {
                    LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the PR.");
                    setNextOrAllDone(actionBy, approvalList, currentLevel, pr);
                }
                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "PR '" + pr.getPrId() + "' Approved", actionBy.getTenantId(), actionBy, new Date(),
                            ModuleType.PR);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }
            }
        }
        // pr.setPrApprovals(approvalList);
        return prDao.update(pr);
    }

    /**
     * @param pr
     * @param actionBy
     * @param remarks
     */
    private void sendPrRejectionEmail(User mailTo, Pr pr, User actionBy, String remarks) {
        LOG.info("Sending rejected request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
        String url = APP_URL + "/buyer/prView/" + pr.getId();
        String subject = "PR Rejected";
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("pr", pr);
        map.put("remarks", StringUtils.checkString(remarks));
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(pr.getId())));
        map.put("prReferanceNumber", StringUtils.checkString(pr.getReferenceNumber()));
        if (mailTo.getId().equals(actionBy.getId())) {
            map.put("message", "You have Rejected");
        } else {
            map.put("message", actionBy.getName() + " has Rejected");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.PR_REJECT_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("pr.rejection.notification.message",
                new Object[] { actionBy.getName(), pr.getName(), remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);

        if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", pr.getId());
                payload.put("messageType", NotificationType.REJECT_MESSAGE.toString());
                payload.put("eventType", FilterTypes.PR.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(mailTo.getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending PR reject Mobile push notification to '" + mailTo.getCommunicationEmail()
                        + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
        }
    }

    /**
     * @param pr
     * @param actionBy
     * @param remarks
     */
    private void sendPrApprovalEmail(User mailTo, Pr pr, User actionBy, String remarks) {
        LOG.info("Sending Approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
        String subject = "PR Approved";
        String url = APP_URL + "/buyer/prView/" + pr.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("pr", pr);
        map.put("remarks", StringUtils.checkString(remarks));
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(pr.getId())));
        map.put("prReferanceNumber", StringUtils.checkString(pr.getReferenceNumber()));
        if (mailTo.getId().equals(actionBy.getId())) {
            map.put("message", "You have Approved");
        } else {
            map.put("message", actionBy.getName() + " has Approved");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.PR_APPROVAL_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("pr.approval.notification.message",
                new Object[] { actionBy.getName(), pr.getName() }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

        if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", pr.getId());
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                payload.put("eventType", FilterTypes.PR.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(mailTo.getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Approval Mobile push notification to '" + mailTo.getCommunicationEmail()
                        + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
        }
    }

    /**
     * @param pr
     * @param user
     */
    public void sendEmailToPrApprovers(Pr pr, PrApprovalUser user, String buyerTimeZone) {
        String mailTo = user.getUser().getCommunicationEmail();
        String subject = "PR Approval request";
        String url = APP_URL + "/buyer/prView/" + pr.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", user.getUser().getName());
        map.put("pr", pr);
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(pr.getId())));
        map.put("prReferanceNumber", (pr.getReferenceNumber() == null ? "" : pr.getReferenceNumber()));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.PR_APPROVAL_REQUEST_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + user.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("pr.approval.request.notification.message",
                new Object[] { pr.getName() }, Global.LOCALE);
        sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
        if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo + "' and device Id :" + user.getUser().getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", pr.getId());
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                payload.put("eventType", FilterTypes.PR.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(user.getUser().getDeviceId()));
            } catch (Exception e) {
                LOG.error(
                        "Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(),
                        e);
            }
        } else {
            LOG.info("User '" + mailTo + "' Device Id is Null");
        }
    }

    private String getBusinessUnitname(String prId) {
        String displayName = null;
        displayName = prDao.getBusineessUnitname(prId);
        return StringUtils.checkString(displayName);
    }

    /**
     * @param mailTo
     * @param pr
     */
    private void sendPoCreatedEmail(User mailTo, Pr pr, User actionBy) {
        LOG.info("Sending PO created email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
        try {
            String subject = "PO Created";
            String url = APP_URL + "/buyer/prView/" + pr.getId();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("userName", mailTo.getName());
            map.put("message", "You have Created");
            map.put("pr", pr);
            map.put("buyerName", actionBy.getName());
            map.put("buyerLoginEmail", actionBy.getLoginId());
            map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(pr.getId())));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
            String timeZone = "GMT+8:00";
            timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
            df.setTimeZone(TimeZone.getTimeZone(timeZone));
            sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
            map.put("date", df.format(new Date()));
            map.put("pocreatedDate", sdf.format(new Date()));
            map.put("loginUrl", APP_URL + "/login");
            map.put("appUrl", url);

            if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
                sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.PO_CREATED_TEMPLATE);
            } else {
                LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                        + "... Not going to send email notification");
            }

            String notificationMessage = messageSource.getMessage("po.create.notification.message",
                    new Object[] { pr.getName() }, Global.LOCALE);
            sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.CREATED_MESSAGE);
            // prService.sendPrFinishMailToSupplier(pr);

        } catch (Exception e) {
            LOG.error("Error while Sending PO Created :" + e.getMessage(), e);
        }

    }

    private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage,
                                           NotificationType notificationType) {
        NotificationMessage message = new NotificationMessage();
        message.setCreatedBy(null);
        message.setCreatedDate(new Date());
        message.setMessage(notificationMessage);
        message.setNotificationType(notificationType);
        message.setMessageTo(messageTo);
        message.setSubject(subject);
        message.setTenantId(messageTo.getTenantId());
        message.setUrl(url);
        dashboardNotificationService.save(message);
    }

    private void sendDashboardNotificationForFinance(User messageTo, String url, String subject,
                                                     String notificationMessage, NotificationType notificationType) {
        FinanceNotificationMessage message = new FinanceNotificationMessage();
        message.setCreatedBy(null);
        message.setCreatedDate(new Date());
        message.setMessage(notificationMessage);
        message.setNotificationType(notificationType);
        message.setMessageTo(messageTo);
        message.setSubject(subject);
        message.setTenantId(messageTo.getTenantId());
        message.setUrl(url);
        dashboardNotificationService.saveFinanceNotification(message);
    }

    /**
     * @param actionBy
     * @param approvalList
     * @param currentLevel
     * @param pr
     * @throws Exception
     */
    public void setNextOrAllDone(User actionBy, List<PrApproval> approvalList, PrApproval currentLevel, Pr pr)
            throws Exception {
        String buyerTimeZone = "GMT+8:00";
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            if (Boolean.TRUE == pr.getLockBudget()) {
                if (null != pr.getBusinessUnit() && null != pr.getCostCenter()) {
                    // update Budget Amount
                    Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(pr.getBusinessUnit().getId(),
                            pr.getCostCenter().getId());
                    // convert PR amount if currency is different
                    BigDecimal prAfterConversion = null;
                    // conversionRate not equal to zero
                    if (null != pr.getConversionRate() && !(0 == pr.getConversionRate().compareTo(BigDecimal.ZERO))) {
                        prAfterConversion = pr.getGrandTotal().multiply(pr.getConversionRate());
                        LOG.info("**************************prAfterConversion " + prAfterConversion);
                    }
                    // if budget locking enabled
                    if (budget != null) {
                        if (pr.getLockBudget()) {
                            budget.setLockedAmount(budget.getLockedAmount()
                                    .subtract(prAfterConversion != null ? prAfterConversion : pr.getGrandTotal()));
                            budget.setApprovedAmount(budget.getApprovedAmount()
                                    .add(prAfterConversion != null ? prAfterConversion : pr.getGrandTotal()));
                        } else {
                            budget.setPendingAmount(budget.getPendingAmount()
                                    .subtract(prAfterConversion != null ? prAfterConversion : pr.getGrandTotal()));
                            budget.setApprovedAmount(budget.getApprovedAmount()
                                    .add(prAfterConversion != null ? prAfterConversion : pr.getGrandTotal()));
                        }
                        budgetService.updateBudget(budget);
                    }
                }
            }
            LOG.info("All approvals for this PR is done!!!. Going to Approved Mode.");
            ErpSetup erpConfig = erpConfigDao.getErpConfigBytenantId(actionBy.getTenantId());
            if (erpConfig != null && Boolean.TRUE == erpConfig.getIsErpEnable()) {
                LOG.info("ERP Integration is enabled.... sending PR to ERP...");
                sendingPrToErpAgent(actionBy, pr, erpConfig);
            } else {

                pr.setStatus(PrStatus.APPROVED);
                pr.setActionBy(actionBy);
                pr.setActionDate(new Date());

                Boolean isAutoCreatePo = buyerSettingsService.isAutoCreatePoSettingsByTenantId(actionBy.getTenantId());

                if (erpConfig != null && Boolean.TRUE == erpConfig.getIsGeneratePo()
                        && Boolean.TRUE == isAutoCreatePo) {
                    Po po = prService.createPo(pr.getCreatedBy(), pr);
                    if (po != null && StringUtils.checkString(po.getId()).length() > 0) {
                        pr.setPoNumber(po.getPoNumber());
                        pr.setPoCreatedDate(new Date());
                        pr.setIsPo(Boolean.TRUE);
                        sendPoCreatedEmailToCreater(pr.getCreatedBy(), pr, pr.getCreatedBy());
                        try {
                            if (po.getStatus() == PoStatus.ORDERED) {
                                po = poService.getLoadedPoById(po.getId());
                                if (po.getSupplier() != null) {
                                    sendPoReceivedEmailNotificationToSupplier(po, po.getCreatedBy());
                                }
                            }
                        } catch (Exception e) {
                            LOG.error("Error while sending PO email notification to supplier:" + e.getMessage(), e);
                        }
                    }
                }
            }

        } else {
            for (PrApproval prApproval : approvalList) {
                if (prApproval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + prApproval.getLevel() + " as Active level");
                    prApproval.setActive(true);
                    for (PrApprovalUser nextLevelUser : prApproval.getApprovalUsers()) {
                        buyerTimeZone = getTimeZoneByBuyerSettings(nextLevelUser.getUser().getTenantId(),
                                buyerTimeZone);
                        sendEmailToPrApprovers(pr, nextLevelUser, buyerTimeZone);
                        if (Boolean.TRUE == pr.getEnableApprovalReminder()) {
                            Integer reminderHr = pr.getReminderAfterHour();
                            Integer reminderCpunt = pr.getReminderCount();
                            if (reminderHr != null && reminderCpunt != null) {
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.HOUR, reminderHr);
                                nextLevelUser.setNextReminderTime(now.getTime());
                                nextLevelUser.setReminderCount(reminderCpunt);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * @param actionBy
     * @param pr
     * @param erpConfig
     * @throws Exception
     */
    void sendingPrToErpAgent(User actionBy, Pr pr, ErpSetup erpConfig) throws Exception {

        // sending PR to ERP
        try {

            ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(
                    new SimpleClientHttpRequestFactory());
            restTemplate = new RestTemplate(factory);
            restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
            restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

            HttpHeaders headers = new HttpHeaders();

            ObjectMapper mapperObj = new ObjectMapper();
            String payload = "";
            String responseMsg = "";
            // If SAP PR Interface type == FGV else Westports
            if (ErpIntegrationTypeForPr.TYPE_2 == erpConfig.getErpIntegrationTypeForPr()) {

                LOG.info("Its a type 2 interface for PR");

                // Fetch the contract reference numbers and storage location...
                if (pr.getTemplate() != null && Boolean.TRUE == pr.getTemplate().getContractItemsOnly()) {
                    for (PrItem item : pr.getPrItems()) {
                        if (item.getProduct() != null) {
                            ProductContractItems pcItem = productContractItemsDao
                                    .findProductContractItemByItemId(item.getProduct().getId(),
                                            item.getProductContractItem() != null
                                                    ? item.getProductContractItem().getId()
                                                    : null);
                            if (pcItem != null) {
                                item.setStorageLocation(pcItem.getStorageLocation());
                                item.setItemContractReferenceNumber(pcItem.getContractItemNumber());
                                item.setContractReferenceNumber(
                                        pcItem.getProductContract().getContractReferenceNumber());
                                item.setCostCenter(
                                        pcItem.getCostCenter() != null ? pcItem.getCostCenter().getCostCenter() : null);
                                // calculate balance quantity
                                pcItem.setBalanceQuantity(pcItem.getBalanceQuantity().subtract(item.getQuantity()));
                                item.setPurchaseGroup(pcItem.getProductContract().getGroupCodeStr());
                                productContractItemsDao.update(pcItem);
                            }
                        }
                    }
                }

                if (StringUtils.checkString(erpConfig.getErpUsername()).length() > 0
                        && StringUtils.checkString(erpConfig.getErpPassword()).length() > 0) {
                    String auth = StringUtils.checkString(erpConfig.getErpUsername()) + ":"
                            + StringUtils.checkString(erpConfig.getErpPassword());
                    byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")));
                    String authHeader = "Basic " + new String(encodedAuth);
                    headers.set("Authorization", authHeader);
                }

                PrErp2Pojo prErp2Pojo = new PrErp2Pojo(pr);
                payload = mapperObj.writeValueAsString(prErp2Pojo);
                LOG.info("jsonObject  :" + payload);

                HttpEntity<PrErp2Pojo> request = new HttpEntity<PrErp2Pojo>(prErp2Pojo, headers);
                try {
                    responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() + "/PH/PRCreate/", request,
                            String.class);
                } catch (HttpClientErrorException | HttpServerErrorException ex) {
                    responseMsg = ex.getMessage();
                    LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
                    LOG.error("Response Body : " + ex.getResponseBodyAsString());
                    throw new ApplicationException(
                            "Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(),
                            ex);
                }

            } else {
                LOG.info("Its a type 1 interface for PR");

                PrErpPojo prErpPojo = constructPrErpObject(pr);
                payload = mapperObj.writeValueAsString(prErpPojo);
                LOG.info("jsonObject  :" + payload);
                if (StringUtils.checkString(prErpPojo.getVendorCode()).length() == 0 && pr.getSupplier() != null) {
                    throw new ApplicationException("Vendor Code not Assigned to Supplier");
                }
                String erpSeqNo = erpSetupService.genrateSquanceNumber();
                prErpPojo.setErpSeqNo(erpSeqNo);

                headers.set(Global.X_AUTH_KEY_HEADER_PROPERTY, erpConfig.getAppId());
                HttpEntity<PrErpPojo> request = new HttpEntity<PrErpPojo>(prErpPojo, headers);

                try {
                    responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() + "/PrSendData", request,
                            String.class);
                } catch (HttpClientErrorException | HttpServerErrorException ex) {
                    responseMsg = ex.getMessage();
                    LOG.error("Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText());
                    LOG.error("Response Body : " + ex.getResponseBodyAsString());
                    throw new ApplicationException(
                            "Error received from ERP : " + ex.getMessage() + " Status Text : " + ex.getStatusText(),
                            ex);
                }
            }

            // String responseMsg = restTemplate.postForObject(erpConfig.getErpUrl() +
            // "/PrSendData", prErpPojo,
            // String.class);

            LOG.info("response :" + responseMsg);
            pr.setStatus(PrStatus.TRANSFERRED);
            pr.setErpPrTransferred(Boolean.TRUE);
            pr.setIsFinalApproved(Boolean.TRUE);
            // Storing audit history
            try {
                PrAudit audit = new PrAudit();
                audit.setAction(PrAuditType.TRANSFERRED);
                audit.setActionBy(actionBy);
                audit.setActionDate(new Date());
                audit.setBuyer(actionBy.getBuyer());
                audit.setDescription(
                        "Sucessfully transferred to ERP. Response : " + StringUtils.checkString(responseMsg));
                audit.setPr(pr);
                prAuditService.save(audit);
            } catch (Exception e) {
                LOG.error("Error while saving ERP Audit History :" + e.getMessage(), e);
            }

        } catch (Exception e) {
            LOG.error("Error while sending PR to ERP :" + e.getMessage(), e);
            pr.setIsFinalApproved(Boolean.TRUE);
            pr.setStatus(PrStatus.APPROVED);
            pr.setErpPrTransferred(Boolean.FALSE);
            try {
                if (pr.getCreatedBy() != null) {
                    sendPrErrorEmail(pr.getCreatedBy(), pr, e.getMessage());
                }
            } catch (Exception ee) {
                LOG.info("ERROR while Sending mail :" + ee.getMessage(), ee);
            }

            // Storing audit history for error
            try {
                PrAudit audit = new PrAudit();
                audit.setAction(PrAuditType.ERROR);
                audit.setActionBy(actionBy);
                audit.setActionDate(new Date());
                audit.setBuyer(actionBy.getBuyer());
                audit.setDescription(e.getMessage());
                audit.setPr(pr);
                prAuditService.save(audit);
            } catch (Exception error) {
                LOG.error("Error while saving ERP Audit History in catch block :" + error.getMessage(), error);
            }

            throw new Exception("Error while sending PR to ERP :" + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public RftEvent doApproval(RftEvent event, HttpSession session, User loggedInUser,
                               JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception {

        try {
            event = rftEventService.loadRftEventById(event.getId());
            List<RftEventApproval> approvalList = event.getApprovals(); // rftEventDao.getAllApprovalsForEvent(event.getId());
            if (CollectionUtil.isEmpty(approvalList)) {
                event.setStatus(EventStatus.APPROVED);
                LOG.info("status :" + event.getStatus());
                try {
                    LOG.info("publishing rft event to to epiportal if approval level is empty ");
                    publishEventService.pushRftEvent(event.getId(), loggedInUser.getBuyer().getId(), true);
                } catch (Exception e) {
                    LOG.error("Error while publishing RFT event to EPortal:" + e.getMessage(), e);
                }

                TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(),
                        loggedInUser.getTenantId());
                if (tatReport != null) {
                    BigDecimal diffInDay = BigDecimal.ZERO;
                    if (tatReport.getEventFinishDate() != null) {
                        double diffInDays = DateUtil.differenceInDays(new Date(), tatReport.getEventFinishDate());
                        diffInDay = BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP);
                    }
                    tatReportService.updateTatReportApproved(tatReport.getId(), eventApprovedAndFinishDate, null, diffInDay,
                            EventStatus.APPROVED);
                }

                JasperPrint eventSummary = rftEventService.getEvaluationSummaryPdf(event, loggedInUser,
                        (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
                byte[] summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
                RftEventAudit audit = new RftEventAudit(loggedInUser.getBuyer(), event, loggedInUser, new Date(),
                        AuditActionType.Approve, messageSource.getMessage("event.audit.approved",
                        new Object[] { event.getEventName() }, Global.LOCALE),
                        summarySnapshot);
                eventAuditService.save(audit);
                try {
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "Event '" + event.getEventId() + "' Approved ", loggedInUser.getTenantId(), loggedInUser,
                            new Date(), ModuleType.RFT);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                } catch (Exception e) {
                    LOG.info("Error to create audit trails message");
                }
            } else {
                if (event.getStatus() == EventStatus.DRAFT) {
                    event.setStatus(EventStatus.PENDING);
                    for (RftEventApproval approval : approvalList) {
                        if (approval.getLevel() == 1) {
                            approval.setActive(true);
                            break;
                        }
                    }
                } else {
                    RftEventApproval currentLevel = null;
                    for (RftEventApproval approval : approvalList) {
                        if (approval.isActive()) {
                            currentLevel = approval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (RftApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the Event.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, event, session, loggedInUser);
                    }
                }
                // Just send emails to users.
                for (RftEventApproval approval : approvalList) {
                    if (approval.isActive()) {
                        for (RftApprovalUser user : approval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                sendRfxApprovalEmails(event, user, RfxTypes.RFT);

                                if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                                    Integer reminderHr = event.getReminderAfterHour();
                                    Integer reminderCpunt = event.getReminderCount();
                                    if (reminderHr != null && reminderCpunt != null) {
                                        Calendar now = Calendar.getInstance();
                                        now.add(Calendar.HOUR, reminderHr);
                                        user.setNextReminderTime(now.getTime());
                                        user.setReminderCount(reminderCpunt);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            event = rftEventDao.update(event);
        } catch (

                Exception e) {
            LOG.info("ERROR While Approving RFT :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving RFT :" + e.getMessage());
        }
        return event;
    }

    @Override
    @Transactional(readOnly = false)
    public RftEvent doApproval(RftEvent event, User actionBy, String remarks, boolean approved, HttpSession session,
                               JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException {
        event = rftEventService.loadRftEventById(event.getId());
        List<RftEventApproval> approvalList = event.getApprovals(); // rftEventDao.getAllApprovalsForEvent(event.getId());
        List<RftTeamMember> teamMembers = event.getTeamMembers();

        // Identify Current Approval Level
        RftEventApproval currentLevel = null;
        for (RftEventApproval approval : approvalList) {
            if (approval.isActive()) {
                currentLevel = approval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        RftApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (RftApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFT '" + event.getEventName()
                    + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFT '"
                    + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFT at : "
                    + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " RFT at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (event.getComment() == null) {
            event.setComment(new ArrayList<RftComment>());
        }
        RftComment comment = new RftComment();
        comment.setComment(remarks);
        comment.setApproved(approved);
        comment.setCreatedBy(actionBy);
        comment.setCreatedDate(actionDate);
        comment.setRfxEvent(event);
        comment.setApprovalUserId(actionUser.getId());
        event.getComment().add(comment);

        // If rejected
        if (!approved) {

            // Reset all approvals for re-approval as the Event is rejected.
            for (RftEventApproval approval : approvalList) {
                approval.setDone(false);
                approval.setActive(false);
                for (RftApprovalUser user : approval.getApprovalUsers()) {
                    // Send rejection email to all approverss
                    // if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                    sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
                    // }
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                }
            }

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RftTeamMember teamMember : teamMembers) {
                    // send rejection email to all team members
                    try {
                        sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            // actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
            actionUser.setActionDate(actionDate);
            actionUser.setRemarks(remarks);
            LOG.info("User " + actionBy.getName() + " has Rejected the RFT : " + event.getEventName());
            event.setStatus(EventStatus.DRAFT);

            snapShotAuditService.doRftAudit(event, session, event, actionBy, AuditActionType.Reject,
                    "event.audit.rejected", virtualizer);

            try {
                LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED,
                        "RFT Event '" + event.getEventId() + "' is rejected", actionBy.getTenantId(), actionBy,
                        actionDate, ModuleType.RFT);
                buyerAuditTrailDao.save(buyerAuditTrail);
                LOG.info("--------------------------AFTER AUDIT---------------------------------------");
            } catch (Exception e) {
                LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
            }

            try {
                if (event.getCreatedBy() != null) {
                    sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFT, event.getReferanceNumber());
                }
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

            if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                tatReportService.updateTatReportEventRejection(event.getPreviousRequestId(), event.getEventId(),
                        actionBy.getTenantId(), actionDate, EventStatus.DRAFT);
            }
        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the RFT : " + event.getEventName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            // Send email notification to Creator
            sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                    event.getCreatedBy(), RfxTypes.RFT, false, event.getReferanceNumber());

            // Send email notification to actionBy
            sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(),
                    RfxTypes.RFT, true, event.getReferanceNumber());

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RftTeamMember teamMember : teamMembers) {
                    // send approved email to all team members
                    try {
                        sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFT, false, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            Integer cnt = 1;
            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                    if (cnt.equals(currentLevel.getLevel())) {
                        tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                event.getId(), actionBy.getTenantId(), actionDate);
                    }
                }
            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (RftApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            allUsersDone = false;
                            LOG.info("All users of this level have not approved the RFT.");
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the RFT.");
                    setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                    if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                        if (cnt.equals(currentLevel.getLevel())) {
                            tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                    event.getId(), actionBy.getTenantId(), actionDate);
                        }
                    }
                }
            }

        }
        // event.setApprovals(approvalList);
        return rftEventDao.update(event);
    }

    /**
     * @param eventId
     * @param eventName
     * @param actionBy
     * @param remarks
     * @param ownerUser
     * @param type
     */
    private void sentRfxRejectionEmail(User mailTo, String eventId, String eventName, User actionBy, String remarks,
                                       User ownerUser, RfxTypes type, String referanceNumber) {
        LOG.info("Sending rejected request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

        String subject = "Event Rejected";
        String url = APP_URL + "/buyer/" + type.name() + "/viewSummary/" + eventId;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("eventName", eventName);
        map.put("eventType", type.name());
        map.put("remarks", remarks);
        map.put("referanceNumber", referanceNumber);
        map.put("businessUnit", StringUtils.checkString(findBusinessUnit(eventId, type)));
        if (mailTo.getId().equals(actionBy.getId())) {
            map.put("message", "You have Rejected");
        } else {
            map.put("message", actionBy.getName() + " has Rejected");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");
        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.EVENT_REJECT_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("event.rejection.notification.message",
                new Object[] { actionBy.getName(), type.name(), eventName, remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);

        if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", eventId);
                payload.put("messageType", NotificationType.REJECT_MESSAGE.toString());
                payload.put("eventType", type.name());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(mailTo.getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Event reject Mobile push notification to '"
                        + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
        }
    }

    /**
     * @param eventId
     * @param eventName
     * @param actionBy
     * @param remarks
     * @param ownerUser
     * @param type
     * @param self
     */
    private void sentRfxApprovalEmail(User mailTo, String eventId, String eventName, User actionBy, String remarks,
                                      User ownerUser, RfxTypes type, boolean self, String referenceNumber) {
        LOG.info("Sending approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

        String subject = "Event Approved";
        String url = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + eventId;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("eventName", eventName);
        map.put("eventType", type.name());
        map.put("remarks", remarks);
        map.put("referenceNumber", referenceNumber);
        map.put("businessUnit", StringUtils.checkString(findBusinessUnit(eventId, type)));
        if (self) {
            map.put("message", "You have Approved");
        } else {
            map.put("message", actionBy.getName() + " has Approved");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");
        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.EVENT_APPROVAL_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("event.approval.notification.message",
                new Object[] { actionBy.getName(), type.name(), eventName, remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

        if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", eventId);
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                payload.put("eventType", type.name());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(mailTo.getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Event approve Mobile push notification to '"
                        + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
        }
    }

    /**
     * @param actionBy
     * @param approvalList
     * @param currentLevel
     * @param event
     * @param loggedInUser
     */
    private void setNextOrAllDone(User actionBy, List<RftEventApproval> approvalList, RftEventApproval currentLevel,
                                  RftEvent event, HttpSession session, User loggedInUser) {
        // Check if all approvals are done
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this RFT is done!!!. Going to Approved Mode.");
            event.setStatus(EventStatus.APPROVED);
            event.setActionBy(actionBy);
            event.setActionDate(new Date());

            TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(),
                    loggedInUser.getTenantId());
            if (tatReport != null) {
                double diffInDays = DateUtil.differenceInDays(new Date(), tatReport.getEventFinishDate());
                // If EventLastApprovedDate pass null in update query it will take new Date(),
                // If we pass
                // eventFirstApprovedDate value it wont update in database
                tatReportService.updateTatReportApproved(tatReport.getId(), new Date(), null,
                        BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP), EventStatus.APPROVED);
            }

            LOG.info("All approvals for this RFT is in Approved Mode.");

            try {
                LOG.info("publishing rft event to epiportal");
                publishEventService.pushRftEvent(event.getId(), actionBy.getBuyer().getId(), true);
            } catch (Exception e) {
                LOG.error("Error while publishing RFT event to EPortal:" + e.getMessage(), e);
            }

        } else {
            for (RftEventApproval approval : approvalList) {
                if (approval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
                    approval.setActive(true);
                    for (RftApprovalUser nextLevelUser : approval.getApprovalUsers()) {
                        sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFT);
                        if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                            Integer reminderHr = event.getReminderAfterHour();
                            Integer reminderCpunt = event.getReminderCount();
                            if (reminderHr != null && reminderCpunt != null) {
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.HOUR, reminderHr);
                                nextLevelUser.setNextReminderTime(now.getTime());
                                nextLevelUser.setReminderCount(reminderCpunt);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public RfpEvent doApproval(RfpEvent event, HttpSession session, User loggedInUser,
                               JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception {
        try {
            event = rfpEventService.loadRfpEventById(event.getId());
            List<RfpEventApproval> approvalList = event.getApprovals(); // rfpEventDao.getAllApprovalsForEvent(event.getId());
            if (CollectionUtil.isEmpty(approvalList)) {
                event.setStatus(EventStatus.APPROVED);
                LOG.info("status :" + event.getStatus());

                TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(),
                        loggedInUser.getTenantId());
                if (tatReport != null) {
                    Double diffInDays = 0d;
                    if (tatReport.getEventFinishDate() != null) {
                        diffInDays = DateUtil.differenceInDays(new Date(), tatReport.getEventFinishDate());
                    }
                    tatReportService.updateTatReportApproved(tatReport.getId(), eventApprovedAndFinishDate, null,
                            diffInDays != null ? BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP)
                                    : BigDecimal.ZERO,
                            EventStatus.APPROVED);
                }

                snapShotAuditService.doRfpAudit(event, session, event, loggedInUser, AuditActionType.Approve,
                        "event.audit.approved", virtualizer);

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "Event '" + event.getEventId() + "' Approved", loggedInUser.getTenantId(), loggedInUser,
                            new Date(), ModuleType.RFP);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }

            } else {
                if (event.getStatus() == EventStatus.DRAFT) {
                    event.setStatus(EventStatus.PENDING);
                    for (RfpEventApproval approval : approvalList) {
                        if (approval.getLevel() == 1) {
                            approval.setActive(true);
                            break;
                        }
                    }
                } else {
                    RfpEventApproval currentLevel = null;
                    for (RfpEventApproval approval : approvalList) {
                        if (approval.isActive()) {
                            currentLevel = approval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (RfpApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the Event.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, event, session, loggedInUser);
                    }
                }
                // Just send emails to users.
                for (RfpEventApproval approval : approvalList) {
                    if (approval.isActive()) {
                        for (RfpApprovalUser user : approval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                sendRfxApprovalEmails(event, user, RfxTypes.RFP);

                                if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                                    Integer reminderHr = event.getReminderAfterHour();
                                    Integer reminderCpunt = event.getReminderCount();
                                    if (reminderHr != null && reminderCpunt != null) {
                                        Calendar now = Calendar.getInstance();
                                        now.add(Calendar.HOUR, reminderHr);
                                        user.setNextReminderTime(now.getTime());
                                        user.setReminderCount(reminderCpunt);
                                    }
                                }

                            }
                        }
                    }
                }
            }
            event = rfpEventDao.update(event);
        } catch (Exception e) {
            LOG.info("ERROR While Approving RFP :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving RFP :" + e.getMessage());
        }
        return event;
    }

    @Override
    @Transactional(readOnly = false)
    public RfpEvent doApproval(RfpEvent event, User actionBy, String remarks, boolean approved, HttpSession session,
                               JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException {

        event = rfpEventService.loadRfpEventById(event.getId());
        List<RfpEventApproval> approvalList = event.getApprovals(); // rfpEventDao.getAllApprovalsForEvent(event.getId());
        List<RfpTeamMember> teamMembers = event.getTeamMembers(); //

        // Identify Current Approval Level
        RfpEventApproval currentLevel = null;
        for (RfpEventApproval approval : approvalList) {
            if (approval.isActive()) {
                currentLevel = approval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        RfpApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (RfpApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFP '" + event.getEventName()
                    + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFP '"
                    + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFP at : "
                    + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " RFP at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (event.getComment() == null) {
            event.setComment(new ArrayList<RfpComment>());
        }
        RfpComment comment = new RfpComment();
        comment.setComment(remarks);
        comment.setApproved(approved);
        comment.setCreatedBy(actionBy);
        comment.setCreatedDate(actionDate);
        comment.setRfxEvent(event);
        comment.setApprovalUserId(actionUser.getId());
        event.getComment().add(comment);

        // If rejected
        if (!approved) {

            // Reset all approvals for re-approval as the Event is rejected.
            for (RfpEventApproval approval : approvalList) {
                approval.setDone(false);
                approval.setActive(false);
                for (RfpApprovalUser user : approval.getApprovalUsers()) {
                    // Send rejection email to all approvers
                    // if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                    sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());
                    // }
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                }
            }

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RfpTeamMember teamMember : teamMembers) {
                    // send rejection email to all team members
                    try {
                        sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            // actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
            actionUser.setActionDate(actionDate);
            actionUser.setRemarks(remarks);

            LOG.info("User " + actionBy.getName() + " has Rejected the RFP : " + event.getEventName());
            event.setStatus(EventStatus.DRAFT);
            // rftEventDao.update(event);

            snapShotAuditService.doRfpAudit(event, session, event, actionBy, AuditActionType.Reject,
                    "event.audit.rejected", virtualizer);

            try {
                LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED,
                        "RFP Event '" + event.getEventId() + "' is rejected", actionBy.getTenantId(), actionBy,
                        actionDate, ModuleType.RFP);
                buyerAuditTrailDao.save(buyerAuditTrail);
                LOG.info("--------------------------AFTER AUDIT---------------------------------------");
            } catch (Exception e) {
                LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
            }

            try {
                if (event.getCreatedBy() != null) {
                    LOG.info("Sending rejected request email to owner : "
                            + event.getCreatedBy().getCommunicationEmail());
                    sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFP, event.getReferanceNumber());
                }
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

            if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                tatReportService.updateTatReportEventRejection(event.getPreviousRequestId(), event.getEventId(),
                        actionBy.getTenantId(), actionDate, EventStatus.DRAFT);
            }
        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the RFP : " + event.getEventName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(actionDate);
            actionUser.setRemarks(remarks);

            // Send email notification to Creator
            sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                    event.getCreatedBy(), RfxTypes.RFP, false, event.getReferanceNumber());

            // Send email notification to actionBy
            sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(),
                    RfxTypes.RFP, true, event.getReferanceNumber());

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RfpTeamMember teamMember : teamMembers) {
                    // send approved email to all team members
                    try {
                        sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFP, false, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            Integer cnt = 1;
            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                    if (cnt.equals(currentLevel.getLevel())) {
                        tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                event.getId(), actionBy.getTenantId(), actionDate);
                    }
                }
            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (RfpApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            allUsersDone = false;
                            LOG.info("All users of this level have not approved the RFP.");
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the RFP.");
                    setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                    if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                        if (cnt.equals(currentLevel.getLevel())) {
                            tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                    event.getId(), actionBy.getTenantId(), actionDate);
                        }
                    }
                }
            }
        }
        // event.setApprovals(approvalList);
        return rfpEventDao.update(event);
    }

    /**
     * @param actionBy
     * @param approvalList
     * @param currentLevel
     * @param event
     * @param loggedInUser
     */
    private void setNextOrAllDone(User actionBy, List<RfpEventApproval> approvalList, RfpEventApproval currentLevel,
                                  RfpEvent event, HttpSession session, User loggedInUser) {
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this RFP is done!!!. Going to Approved Mode.");
            event.setStatus(EventStatus.APPROVED);
            event.setActionBy(actionBy);
            event.setActionDate(new Date());

            TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(),
                    loggedInUser.getTenantId());
            if (tatReport != null) {
                double diffInDays = DateUtil.differenceInDays(new Date(), tatReport.getEventFinishDate());
                tatReportService.updateTatReportApproved(tatReport.getId(), new Date(), null,
                        BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP), EventStatus.APPROVED);

            }

        } else {
            for (RfpEventApproval approval : approvalList) {
                if (approval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
                    approval.setActive(true);
                    for (RfpApprovalUser nextLevelUser : approval.getApprovalUsers()) {
                        sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFP);
                        if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                            Integer reminderHr = event.getReminderAfterHour();
                            Integer reminderCpunt = event.getReminderCount();
                            if (reminderHr != null && reminderCpunt != null) {
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.HOUR, reminderHr);
                                nextLevelUser.setNextReminderTime(now.getTime());
                                nextLevelUser.setReminderCount(reminderCpunt);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public RfqEvent doApproval(RfqEvent event, HttpSession session, User loggedInUser,
                               JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception {
        try {
            event = rfqEventService.loadRfqEventById(event.getId());
            List<RfqEventApproval> approvalList = event.getApprovals(); // rfqEventDao.getAllApprovalsForEvent(event.getId());
            if (CollectionUtil.isEmpty(approvalList)) {
                event.setStatus(EventStatus.APPROVED);
                LOG.info("status :" + event.getStatus());
                try {
                    LOG.info("publishing rfq event to to epiportal if approval level is empty ");
                    publishEventService.pushRfqEvent(event.getId(), loggedInUser.getBuyer().getId(), true);
                } catch (Exception e) {
                    LOG.error("Error while publishing RFQ event to EPortal:" + e.getMessage(), e);
                }

                TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(),
                        loggedInUser.getTenantId());
                if (tatReport != null) {
                    Double diffInDays = 0d;
                    if (tatReport.getEventFinishDate() != null) {
                        diffInDays = DateUtil.differenceInDays(new Date(), tatReport.getEventFinishDate());
                    }
                    tatReportService.updateTatReportApproved(tatReport.getId(), eventApprovedAndFinishDate, null,
                            BigDecimal.valueOf(diffInDays != null ? diffInDays : 0).setScale(2, RoundingMode.HALF_UP),
                            EventStatus.APPROVED);

                }
                snapShotAuditService.doRfqAudit(event, session, event, loggedInUser, AuditActionType.Approve,
                        "Event.is.Approved", virtualizer);

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "Event '" + event.getEventId() + "' Approved", loggedInUser.getTenantId(), loggedInUser,
                            new Date(), ModuleType.RFQ);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Approve " + e.getMessage(), e);
                }

            } else {
                if (event.getStatus() == EventStatus.DRAFT) {
                    event.setStatus(EventStatus.PENDING);
                    for (RfqEventApproval approval : approvalList) {
                        if (approval.getLevel() == 1) {
                            approval.setActive(true);
                            break;
                        }
                    }
                } else {
                    RfqEventApproval currentLevel = null;
                    for (RfqEventApproval approval : approvalList) {
                        if (approval.isActive()) {
                            currentLevel = approval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (RfqApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the Event.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, event, session, loggedInUser);
                    }
                }
                // Just send emails to users.
                if (CollectionUtil.isNotEmpty(approvalList)) {
                    for (RfqEventApproval approval : approvalList) {
                        if (approval.isActive()) {
                            for (RfqApprovalUser user : approval.getApprovalUsers()) {
                                if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                    LOG.info("send mail to pending approvers ");
                                    sendRfxApprovalEmails(event, user, RfxTypes.RFQ);

                                    if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                                        Integer reminderHr = event.getReminderAfterHour();
                                        Integer reminderCpunt = event.getReminderCount();
                                        if (reminderHr != null && reminderCpunt != null) {
                                            Calendar now = Calendar.getInstance();
                                            now.add(Calendar.HOUR, reminderHr);
                                            user.setNextReminderTime(now.getTime());
                                            user.setReminderCount(reminderCpunt);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
            event = rfqEventDao.update(event);
        } catch (Exception e) {
            LOG.info("ERROR While Approving RFQ :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving RFQ :" + e.getMessage());
        }
        return event;
    }

    @Override
    @Transactional(readOnly = false)
    public RfqEvent doApproval(RfqEvent event, User actionBy, String remarks, boolean approved, HttpSession session,
                               JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException {

        event = rfqEventService.loadRfqEventById(event.getId());
        List<RfqEventApproval> approvalList = event.getApprovals(); // rfqEventDao.getAllApprovalsForEvent(event.getId());
        List<RfqTeamMember> teamMembers = event.getTeamMembers();

        // Identify Current Approval Level
        RfqEventApproval currentLevel = null;
        for (RfqEventApproval approval : approvalList) {
            if (approval.isActive()) {
                currentLevel = approval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        RfqApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (RfqApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFQ '" + event.getEventName()
                    + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFQ '"
                    + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFQ at : "
                    + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " RFQ at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (event.getComment() == null) {
            event.setComment(new ArrayList<RfqComment>());
        }
        RfqComment comment = new RfqComment();
        comment.setComment(remarks);
        comment.setApproved(approved);
        comment.setCreatedBy(actionBy);
        comment.setCreatedDate(actionDate);
        comment.setRfxEvent(event);
        comment.setApprovalUserId(actionUser.getId());
        event.getComment().add(comment);

        // If rejected
        if (!approved) {
            // For OR level Rejection should be handled differently
            // commented on user requirement don't want to hold if any user is rejecting

            // Reset all approvals for re-approval as the Event is rejected.
            for (RfqEventApproval approval : approvalList) {
                approval.setDone(false);
                approval.setActive(false);
                for (RfqApprovalUser user : approval.getApprovalUsers()) {
                    // Send rejection email to all approvers
                    // if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                    sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
                    // }
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                }
            }

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RfqTeamMember teamMember : teamMembers) {
                    try {
                        // send rejection email to all team members
                        sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            // actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
            actionUser.setActionDate(actionDate);
            actionUser.setRemarks(remarks);

            LOG.info("User " + actionBy.getName() + " has Rejected the RFQ : " + event.getEventName());
            event.setStatus(EventStatus.DRAFT);

            snapShotAuditService.doRfqAudit(event, session, event, actionBy, AuditActionType.Reject,
                    "event.audit.rejected", virtualizer);

            try {
                LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED,
                        "RFQ Event '" + event.getEventId() + "' is rejected", actionBy.getTenantId(), actionBy,
                        actionDate, ModuleType.RFQ);
                buyerAuditTrailDao.save(buyerAuditTrail);
                LOG.info("--------------------------AFTER AUDIT---------------------------------------");
            } catch (Exception e) {
                LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
            }

            try {
                if (event.getCreatedBy() != null) {
                    LOG.info("Sending rejected request email to owner : "
                            + event.getCreatedBy().getCommunicationEmail());
                    sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFQ, event.getReferanceNumber());
                }
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

            if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                tatReportService.updateTatReportEventRejection(event.getPreviousRequestId(), event.getEventId(),
                        actionBy.getTenantId(), actionDate, EventStatus.DRAFT);
            }

        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the RFQ : " + event.getEventName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(actionDate);
            actionUser.setRemarks(remarks);

            // Send email notification to Creator
            sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                    event.getCreatedBy(), RfxTypes.RFQ, false, event.getReferanceNumber());

            // Send email notification to actionBy
            sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(),
                    RfxTypes.RFQ, true, event.getReferanceNumber());

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RfqTeamMember teamMember : teamMembers) {
                    // send approved email to all team members
                    try {
                        sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFQ, false, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            Integer cnt = 1;
            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                    if (cnt.equals(currentLevel.getLevel())) {
                        tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                event.getId(), actionBy.getTenantId(), actionDate);
                    }
                }
            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (RfqApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            allUsersDone = false;
                            LOG.info("All users of this level have not approved the RFQ.");
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the RFQ.");
                    setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                    if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                        if (cnt.equals(currentLevel.getLevel())) {
                            tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                    event.getId(), actionBy.getTenantId(), actionDate);
                        }
                    }
                }
            }
        }
        // event.setApprovals(approvalList);
        return rfqEventDao.update(event);
    }

    /**
     * @param actionBy
     * @param approvalList
     * @param currentLevel
     * @param event
     * @param loggedInUser
     */
    private void setNextOrAllDone(User actionBy, List<RfqEventApproval> approvalList, RfqEventApproval currentLevel,
                                  RfqEvent event, HttpSession session, User loggedInUser) {
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this RFQ is done!!!. Going to Approved Mode.");
            event.setStatus(EventStatus.APPROVED);
            event.setActionBy(actionBy);
            event.setActionDate(new Date());
            Date eventFinishDate = tatReportService.getEventFinishDateByEventIdAndTenantId(event.getId(),
                    loggedInUser.getTenantId());
            if (eventFinishDate != null) {
                double diffInDays = DateUtil.differenceInDays(new Date(), eventFinishDate);
                tatReportService.updateTatReportApprovedAndApprovalDaysCountAndLastApprovedDate(event.getId(),
                        loggedInUser.getTenantId(), new Date(),
                        BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP), EventStatus.APPROVED);
            }

            LOG.info("All approvals for this RFQ is in Approved Mode.");
            try {
                LOG.info("publishing rfq event to epiportal");
                publishEventService.pushRfqEvent(event.getId(), actionBy.getBuyer().getId(), true);
            } catch (Exception e) {
                LOG.error("Error while publishing RFT event to EPortal:" + e.getMessage(), e);
            }
        } else {
            for (RfqEventApproval approval : approvalList) {
                if (approval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
                    approval.setActive(true);
                    for (RfqApprovalUser nextLevelUser : approval.getApprovalUsers()) {
                        sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFQ);
                        if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                            Integer reminderHr = event.getReminderAfterHour();
                            Integer reminderCpunt = event.getReminderCount();
                            if (reminderHr != null && reminderCpunt != null) {
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.HOUR, reminderHr);
                                nextLevelUser.setNextReminderTime(now.getTime());
                                nextLevelUser.setReminderCount(reminderCpunt);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public RfiEvent doApproval(RfiEvent event, HttpSession session, User loggedInUser,
                               JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception {
        try {
            LOG.info("Do Approval level is called..");
            event = rfiEventService.loadRfiEventById(event.getId());
            List<RfiEventApproval> approvalList = event.getApprovals(); // rfiEventDao.getAllApprovalsForEvent(event.getId());
            if (CollectionUtil.isEmpty(approvalList)) {
                event.setStatus(EventStatus.APPROVED);
                LOG.info("status :" + event.getStatus());
                try {
                    LOG.info("publishing rfi event to to epiportal if approval level is empty ");
                    publishEventService.pushRfiEvent(event.getId(), loggedInUser.getBuyer().getId(), true);
                } catch (Exception e) {
                    LOG.error("Error while publishing RFI event to EPortal:" + e.getMessage(), e);
                }

                TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(),
                        loggedInUser.getTenantId());
                if (tatReport != null) {
                    Double diffInDays = 0d;
                    if (tatReport.getEventFinishDate() != null) {
                        diffInDays = DateUtil.differenceInDays(new Date(), tatReport.getEventFinishDate());
                    }
                    tatReportService.updateTatReportApproved(tatReport.getId(), eventApprovedAndFinishDate, null,
                            BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP), EventStatus.APPROVED);
                }

                snapShotAuditService.doRfiAudit(event, session, event, loggedInUser, AuditActionType.Approve,
                        "event.audit.approved", virtualizer);

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "Event '" + event.getEventId() + "' Approved", loggedInUser.getTenantId(), loggedInUser,
                            new Date(), ModuleType.RFI);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }

            } else {
                if (event.getStatus() == EventStatus.DRAFT) {
                    event.setStatus(EventStatus.PENDING);
                    for (RfiEventApproval approval : approvalList) {
                        if (approval.getLevel() == 1) {
                            approval.setActive(true);
                            break;
                        }
                    }
                } else {
                    RfiEventApproval currentLevel = null;
                    for (RfiEventApproval approval : approvalList) {
                        if (approval.isActive()) {
                            currentLevel = approval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (RfiApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the Event.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, event, session, loggedInUser);
                    }
                }
                // Just send emails to users.
                for (RfiEventApproval approval : approvalList) {
                    if (approval.isActive()) {
                        for (RfiApprovalUser user : approval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                sendRfxApprovalEmails(event, user, RfxTypes.RFI);

                                if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                                    Integer reminderHr = event.getReminderAfterHour();
                                    Integer reminderCpunt = event.getReminderCount();
                                    if (reminderHr != null && reminderCpunt != null) {
                                        Calendar now = Calendar.getInstance();
                                        now.add(Calendar.HOUR, reminderHr);
                                        user.setNextReminderTime(now.getTime());
                                        user.setReminderCount(reminderCpunt);
                                    }
                                }

                            }
                        }
                    }
                }
            }
            event = rfiEventDao.update(event);
        } catch (Exception e) {
            LOG.info("ERROR While Approving RFI :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving RFI :" + e.getMessage());
        }
        return event;
    }

    @Override
    @Transactional(readOnly = false)
    public RfiEvent doApproval(RfiEvent event, User actionBy, String remarks, boolean approved, HttpSession session,
                               JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException {

        event = rfiEventService.loadRfiEventById(event.getId());
        List<RfiEventApproval> approvalList = event.getApprovals(); // rfiEventDao.getAllApprovalsForEvent(event.getId());
        List<RfiTeamMember> teamMembers = event.getTeamMembers();

        // Identify Current Approval Level
        RfiEventApproval currentLevel = null;
        for (RfiEventApproval approval : approvalList) {
            if (approval.isActive()) {
                currentLevel = approval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        RfiApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (RfiApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFI '" + event.getEventName()
                    + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFI '"
                    + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFI at : "
                    + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " RFI at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (event.getComment() == null) {
            event.setComment(new ArrayList<RfiComment>());
        }
        RfiComment comment = new RfiComment();
        comment.setComment(remarks);
        comment.setApproved(approved);
        comment.setCreatedBy(actionBy);
        comment.setCreatedDate(actionDate);
        comment.setRfxEvent(event);
        comment.setApprovalUserId(actionUser.getId());
        event.getComment().add(comment);

        // If rejected
        if (!approved) {
            // For OR level Rejection should be handled differently
            // commented on user requirement don't want to hold if any user is rejecting

            // Reset all approvals for re-approval as the Event is rejected.
            for (RfiEventApproval approval : approvalList) {
                approval.setDone(false);
                approval.setActive(false);
                for (RfiApprovalUser user : approval.getApprovalUsers()) {
                    // Send rejection email to all users
                    // if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                    sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFI, event.getReferanceNumber());
                    // }
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                }
            }

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RfiTeamMember teamMember : teamMembers) {
                    try {
                        // send rejection email to all team members
                        sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFI, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            // actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
            actionUser.setActionDate(actionDate);
            actionUser.setRemarks(remarks);

            LOG.info("User " + actionBy.getName() + " has Rejected the RFI : " + event.getEventName());
            event.setStatus(EventStatus.DRAFT);
            // rftEventDao.update(event);

            snapShotAuditService.doRfiAudit(event, session, event, actionBy, AuditActionType.Reject,
                    "event.audit.rejected", virtualizer);

            try {
                LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED,
                        "RFI Event '" + event.getEventId() + "' is rejected", actionBy.getTenantId(), actionBy,
                        actionDate, ModuleType.RFI);
                buyerAuditTrailDao.save(buyerAuditTrail);
                LOG.info("--------------------------AFTER AUDIT---------------------------------------");
            } catch (Exception e) {
                LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
            }
            try {
                if (event.getCreatedBy() != null) {
                    LOG.info("Sending rejected request email to owner : "
                            + event.getCreatedBy().getCommunicationEmail());
                    sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFI, event.getReferanceNumber());
                }
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

            if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                tatReportService.updateTatReportEventRejection(event.getPreviousRequestId(), event.getEventId(),
                        actionBy.getTenantId(), actionDate, EventStatus.DRAFT);
            }
        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the RFI : " + event.getEventName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            // Send email notification to Creator
            sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                    event.getCreatedBy(), RfxTypes.RFI, false, event.getReferanceNumber());

            // Send email notification to actionBy
            sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(),
                    RfxTypes.RFI, true, event.getReferanceNumber());

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RfiTeamMember teamMember : teamMembers) {
                    // send approved email to all team members
                    try {
                        sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFI, false, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            Integer cnt = 1;
            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                    if (cnt.equals(currentLevel.getLevel())) {
                        tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                event.getId(), actionBy.getTenantId(), actionDate);
                    }
                }
            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (RfiApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            allUsersDone = false;
                            LOG.info("All users of this level have not approved the RFI.");
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the RFI.");
                    setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                    if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                        if (cnt.equals(currentLevel.getLevel())) {
                            tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                    event.getId(), actionBy.getTenantId(), actionDate);
                        }
                    }
                }
            }

        }
        // event.setApprovals(approvalList);
        return rfiEventDao.update(event);
    }

    /**
     * @param actionBy
     * @param approvalList
     * @param currentLevel
     * @param event
     * @param loggedInUser
     */
    private void setNextOrAllDone(User actionBy, List<RfiEventApproval> approvalList, RfiEventApproval currentLevel,
                                  RfiEvent event, HttpSession session, User loggedInUser) {
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this RFI is done!!!. Going to Approved Mode.");
            event.setStatus(EventStatus.APPROVED);
            event.setActionBy(actionBy);
            event.setActionDate(new Date());

            Date eventFinishDate = tatReportService.getEventFinishDateByEventIdAndTenantId(event.getId(),
                    loggedInUser.getTenantId());
            if (eventFinishDate != null) {
                double diffInDays = DateUtil.differenceInDays(new Date(), eventFinishDate);
                tatReportService.updateTatReportApprovedAndApprovalDaysCountAndLastApprovedDate(event.getId(),
                        loggedInUser.getTenantId(), new Date(),
                        BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP), EventStatus.APPROVED);
            }

            LOG.info("All approvals for this RFI is in Approved Mode.");

            try {
                LOG.info("publishing rfi event to epiportal");
                publishEventService.pushRfiEvent(event.getId(), actionBy.getBuyer().getId(), true);
            } catch (Exception e) {
                LOG.error("Error while publishing RFI event to EPortal:" + e.getMessage(), e);
            }

        } else {
            for (RfiEventApproval approval : approvalList) {
                if (approval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
                    approval.setActive(true);
                    for (RfiApprovalUser nextLevelUser : approval.getApprovalUsers()) {
                        sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFI);
                        if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                            Integer reminderHr = event.getReminderAfterHour();
                            Integer reminderCpunt = event.getReminderCount();
                            if (reminderHr != null && reminderCpunt != null) {
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.HOUR, reminderHr);
                                nextLevelUser.setNextReminderTime(now.getTime());
                                nextLevelUser.setReminderCount(reminderCpunt);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public RfaEvent doApproval(RfaEvent event, HttpSession session, User loggedInUser,
                               JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception {
        try {
            event = rfaEventService.loadRfaEventById(event.getId());
            List<RfaEventApproval> approvalList = event.getApprovals(); // rfaEventDao.getAllApprovalsForEvent(event.getId());
            if (CollectionUtil.isEmpty(approvalList)) {
                event.setStatus(EventStatus.APPROVED);
                LOG.info("status :" + event.getStatus());

                TatReportPojo tatReport = tatReportService.geTatReportByEventIdAndTenantId(event.getId(),
                        loggedInUser.getTenantId());
                if (tatReport != null) {
                    Double diffInDays = 0d;
                    if (tatReport.getEventFinishDate() != null) {
                        diffInDays = DateUtil.differenceInDays(new Date(), tatReport.getEventFinishDate());
                    }
                    tatReportService.updateTatReportApproved(tatReport.getId(), eventApprovedAndFinishDate, null,
                            BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP), EventStatus.APPROVED);
                }
                snapShotAuditService.doRfaAudit(event, session, event, loggedInUser, AuditActionType.Approve,
                        "event.audit.approved", virtualizer);

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "Event '" + event.getEventId() + "' Approved", loggedInUser.getTenantId(), loggedInUser,
                            new Date(), ModuleType.RFA);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }
            } else {
                if (event.getStatus() == EventStatus.DRAFT) {
                    event.setStatus(EventStatus.PENDING);
                    for (RfaEventApproval approval : approvalList) {
                        if (approval.getLevel() == 1) {
                            approval.setActive(true);
                            break;
                        }
                    }
                } else {
                    RfaEventApproval currentLevel = null;
                    for (RfaEventApproval approval : approvalList) {
                        if (approval.isActive()) {
                            currentLevel = approval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (RfaApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the Event.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, event, session, loggedInUser);
                    }
                }
                // Just send emails to users.
                for (RfaEventApproval approval : approvalList) {
                    if (approval.isActive()) {
                        for (RfaApprovalUser user : approval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                sendRfxApprovalEmails(event, user, RfxTypes.RFA);

                                if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                                    Integer reminderHr = event.getReminderAfterHour();
                                    Integer reminderCpunt = event.getReminderCount();
                                    if (reminderHr != null && reminderCpunt != null) {
                                        Calendar now = Calendar.getInstance();
                                        now.add(Calendar.HOUR, reminderHr);
                                        user.setNextReminderTime(now.getTime());
                                        user.setReminderCount(reminderCpunt);
                                    }
                                }

                            }
                        }
                    }
                }
            }
            event = rfaEventDao.update(event);
        } catch (Exception e) {
            LOG.info("ERROR While Approving RFA :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving RFA :" + e.getMessage());
        }
        return event;
    }

    @Override
    @Transactional(readOnly = false)
    public RfaEvent doApproval(RfaEvent event, User actionBy, String remarks, boolean approved, HttpSession session,
                               JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException {

        event = rfaEventService.loadRfaEventById(event.getId());
        List<RfaEventApproval> approvalList = event.getApprovals(); // rfaEventDao.getAllApprovalsForEvent(event.getId());
        List<RfaTeamMember> teamMembers = event.getTeamMembers();

        // Identify Current Approval Level
        RfaEventApproval currentLevel = null;
        for (RfaEventApproval approval : approvalList) {
            if (approval.isActive()) {
                currentLevel = approval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        RfaApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (RfaApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFA '" + event.getEventName()
                    + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject RFA '"
                    + event.getEventName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFA at : "
                    + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " RFA at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (event.getComment() == null) {
            event.setComment(new ArrayList<RfaComment>());
        }
        RfaComment comment = new RfaComment();
        comment.setComment(remarks);
        comment.setApproved(approved);
        comment.setCreatedBy(actionBy);
        comment.setCreatedDate(actionDate);
        comment.setRfxEvent(event);
        comment.setApprovalUserId(actionUser.getId());
        event.getComment().add(comment);

        // If rejected
        if (!approved) {
            // For OR level Rejection should be handled differently
            // commented on user requirement don't want to hold if any user is rejecting

            // Reset all approvals for re-approval as the Event is rejected.
            for (RfaEventApproval approval : approvalList) {
                approval.setDone(false);
                approval.setActive(false);
                for (RfaApprovalUser user : approval.getApprovalUsers()) {
                    if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                        // Send rejection email
                        sentRfxRejectionEmail(user.getUser(), event.getId(), event.getEventName(), actionBy, remarks,
                                event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
                    }
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                }
            }

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RfaTeamMember teamMember : teamMembers) {
                    try {
                        sentRfxRejectionEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            // actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
            actionUser.setActionDate(actionDate);
            actionUser.setRemarks(remarks);

            LOG.info("User " + actionBy.getName() + " has Rejected the RFA : " + event.getEventName());
            event.setStatus(EventStatus.DRAFT);
            // rftEventDao.update(event);

            snapShotAuditService.doRfaAudit(event, session, event, actionBy, AuditActionType.Reject,
                    "event.audit.rejected", virtualizer);

            try {
                LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED,
                        "RFA Event '" + event.getEventId() + "' is rejected", actionBy.getTenantId(), actionBy,
                        new Date(), ModuleType.RFA);
                buyerAuditTrailDao.save(buyerAuditTrail);
                LOG.info("--------------------------AFTER AUDIT---------------------------------------");
            } catch (Exception e) {
                LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
            }

            try {
                if (event.getCreatedBy() != null) {
                    LOG.info("Sending rejected request email to owner : "
                            + event.getCreatedBy().getCommunicationEmail());
                    sentRfxRejectionEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                            event.getCreatedBy(), RfxTypes.RFA, event.getReferanceNumber());
                }
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

            if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                tatReportService.updateTatReportEventRejection(event.getPreviousRequestId(), event.getEventId(),
                        actionBy.getTenantId(), new Date(), EventStatus.DRAFT);
            }
        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the RFA : " + event.getEventName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(actionDate);
            actionUser.setRemarks(remarks);

            // Send email notification to Creator
            sentRfxApprovalEmail(event.getCreatedBy(), event.getId(), event.getEventName(), actionBy, remarks,
                    event.getCreatedBy(), RfxTypes.RFA, false, event.getReferanceNumber());
            // Send email notification to actionBy
            sentRfxApprovalEmail(actionBy, event.getId(), event.getEventName(), actionBy, remarks, event.getCreatedBy(),
                    RfxTypes.RFA, true, event.getReferanceNumber());

            if (CollectionUtil.isNotEmpty(teamMembers)) {
                for (RfaTeamMember teamMember : teamMembers) {
                    // send approved email to all team members
                    try {
                        sentRfxApprovalEmail(teamMember.getUser(), event.getId(), event.getEventName(), actionBy,
                                remarks, event.getCreatedBy(), RfxTypes.RFA, false, event.getReferanceNumber());
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            Integer cnt = 1;
            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                    if (cnt.equals(currentLevel.getLevel())) {
                        tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                event.getId(), actionBy.getTenantId(), new Date());
                    }
                }
            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (RfaApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            allUsersDone = false;
                            LOG.info("All users of this level have not approved the RFA.");
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the RFA.");
                    setNextOrAllDone(actionBy, approvalList, currentLevel, event, session, actionBy);

                    if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
                        if (cnt.equals(currentLevel.getLevel())) {
                            tatReportService.updateTatReportEventFirstApprovedDate(event.getPreviousRequestId(),
                                    event.getId(), actionBy.getTenantId(), actionDate);
                        }
                    }
                }
            }

        }
        // event.setApprovals(approvalList);
        return rfaEventDao.update(event);
    }

    /**
     * @param actionBy
     * @param approvalList
     * @param currentLevel
     * @param event
     * @param loggedInUser
     */
    private void setNextOrAllDone(User actionBy, List<RfaEventApproval> approvalList, RfaEventApproval currentLevel,
                                  RfaEvent event, HttpSession session, User loggedInUser) {
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this RFA is done!!!. Going to Approved Mode.");
            event.setStatus(EventStatus.APPROVED);
            event.setActionBy(actionBy);
            event.setActionDate(new Date());

            LOG.info("All approvals for this RFA is in Approved Mode.");

            Date eventFinishDate = tatReportService.getEventFinishDateByEventIdAndTenantId(event.getId(),
                    loggedInUser.getTenantId());
            if (eventFinishDate != null) {
                double diffInDays = DateUtil.differenceInDays(new Date(), eventFinishDate);
                tatReportService.updateTatReportApprovedAndApprovalDaysCountAndLastApprovedDate(event.getId(),
                        loggedInUser.getTenantId(), new Date(),
                        BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP), EventStatus.APPROVED);
            }

        } else {
            for (RfaEventApproval approval : approvalList) {
                if (approval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
                    approval.setActive(true);
                    for (RfaApprovalUser nextLevelUser : approval.getApprovalUsers()) {
                        sendRfxApprovalEmails(event, nextLevelUser, RfxTypes.RFA);
                        if (Boolean.TRUE == event.getEnableApprovalReminder()) {
                            Integer reminderHr = event.getReminderAfterHour();
                            Integer reminderCpunt = event.getReminderCount();
                            if (reminderHr != null && reminderCpunt != null) {
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.HOUR, reminderHr);
                                nextLevelUser.setNextReminderTime(now.getTime());
                                nextLevelUser.setReminderCount(reminderCpunt);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void sendRfxApprovalEmails(Event event, ApprovalUser user, RfxTypes type) {
        String mailTo = user.getUser().getCommunicationEmail();
        String subject = "Event Approval Request";
        String url = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + event.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", user.getUser().getName());
        map.put("event", event);
        map.put("eventType", type.name());
        map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(user.getUser().getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");
        if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.EVENT_APPROVAL_REQUEST_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + user.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("event.approval.request.notification.message",
                new Object[] { type.name(), event.getEventName() }, Global.LOCALE);
        sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

        if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + user.getUser().getCommunicationEmail() + "' and device Id :"
                        + user.getUser().getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", event.getId());
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                payload.put("eventType", type.name());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(user.getUser().getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Event approve Mobile push notification to '"
                        + user.getUser().getCommunicationEmail() + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + user.getUser().getCommunicationEmail() + "' Device Id is Null");
        }
    }

    @Override
    public String findBusinessUnit(String eventId, RfxTypes rfxTypes) {
        String displayName = null;
        switch (rfxTypes) {
            case RFA:
                displayName = rfaEventDao.findBusinessUnitName(eventId);
                break;
            case RFI:
                displayName = rfiEventDao.findBusinessUnitName(eventId);
                break;
            case RFP:
                displayName = rfpEventDao.findBusinessUnitName(eventId);
                break;
            case RFQ:
                displayName = rfqEventDao.findBusinessUnitName(eventId);
                break;
            case RFT:
                displayName = rftEventDao.findBusinessUnitName(eventId);
                break;
            default:
                break;
        }
        return displayName;
    }

    private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
        if (StringUtils.checkString(mailTo).length() > 0) {
            try {
                notificationService.sendEmail(mailTo, subject, map, template);
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }
        } else {
            LOG.warn("No communication email configured for user... Not going to send email notification");
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

    private String getErpNotifiactionEmailsByBuyerSettings(String tenantId) {
        try {
            if (StringUtils.checkString(tenantId).length() > 0) {
                LOG.info("fetching buyer setting-------------------");
                BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(tenantId);
                if (buyerSettings != null) {
                    return buyerSettings.getErpNotificationEmails();
                }
            }
        } catch (Exception e) {
            LOG.error("Error while fetching buyer setting :" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * @param suppId
     * @param timeZone
     * @return
     */
    public String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
        try {
            if (StringUtils.checkString(suppId).length() > 0) {
                String time = supplierSettingsService.getSupplierTimeZoneByTenantId(suppId);
                if (time != null) {
                    timeZone = time;
                    LOG.info("time Zone :" + timeZone);
                }
            }
        } catch (Exception e) {
            LOG.error("Error while fetching supplier time zone :" + e.getMessage(), e);
        }
        return timeZone;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean doMobileApproval(String eventId, FilterTypes type, AuthenticatedUser user, String remarks,
                                    boolean approve, HttpSession session, JRSwapFileVirtualizer virtualizer) throws Exception {
        boolean success = false;
        Date actionDate = new Date();
        User actionBy = userDao.findById(user.getId());
        try {
            switch (type) {
                case RFA:
                    RfaEvent rfaEvent = new RfaEvent();
                    rfaEvent.setId(eventId);
                    doApproval(rfaEvent, actionBy, remarks, approve, session, virtualizer, actionDate);

                    break;
                case RFI:
                    RfiEvent rfiEvent = new RfiEvent();
                    rfiEvent.setId(eventId);
                    doApproval(rfiEvent, actionBy, remarks, approve, session, virtualizer, actionDate);
                    break;
                case RFP:
                    RfpEvent rfpEvent = new RfpEvent();
                    rfpEvent.setId(eventId);
                    doApproval(rfpEvent, actionBy, remarks, approve, session, virtualizer, actionDate);
                    break;
                case RFQ:
                    RfqEvent rfqEvent = new RfqEvent();
                    rfqEvent.setId(eventId);
                    doApproval(rfqEvent, actionBy, remarks, approve, session, virtualizer, actionDate);
                    break;
                case RFT:
                    RftEvent event = new RftEvent();
                    event.setId(eventId);
                    doApproval(event, actionBy, remarks, approve, session, virtualizer, actionDate);
                    break;
                case PR:
                    Pr pr = new Pr();
                    pr.setId(eventId);
                    doApproval(pr, actionBy, remarks, approve);
                    break;

                case REQUEST:
                    SourcingFormRequest request = new SourcingFormRequest();
                    request.setId(eventId);
                    doApprovalRequest(request, actionBy, remarks, approve);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            throw e;
        }
        success = true;
        return success;
    }

    private void sendPrErrorEmail(User mailTo, Pr pr, String remarks) {
        LOG.info("Sending rejected request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
        String url = APP_URL + "/buyer/prView/" + pr.getId();
        String subject = "PR ERROR NOTIFICATION";
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", "");
        map.put("pr", pr);
        map.put("remarks", StringUtils.checkString(remarks));
        map.put("prReferanceNumber", StringUtils.checkString(pr.getReferenceNumber()));
        map.put("message", "Error to Transfer");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        String erpNotificationEmails = getErpNotifiactionEmailsByBuyerSettings(mailTo.getTenantId());
        LOG.warn("erpNotificationEmails---------->" + erpNotificationEmails);
        if (StringUtils.checkString(erpNotificationEmails).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(erpNotificationEmails, subject, map, Global.ERP_PR_ERROR_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("erp.pr.rejection.notification.message",
                new Object[] { pr.getName(), remarks }, Global.LOCALE);
        try {
            sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);
        } catch (Exception e) {
            LOG.error("Error sending dashboard notification : " + e.getMessage(), e);
        }

    }

    void sharePrtoFinance(Po po) {
        po = poService.getLoadedPoById(po.getId());
        LOG.info("---supplierId------------------------" + po.getSupplier().getId() + " Buyer Id : "
                + po.getBuyer().getId());
        FavouriteSupplier favouriteSupplier = favoriteSupplierService
                .findFavSupplierByFavSuppId(po.getSupplier().getId(), po.getBuyer().getId());
        if (favouriteSupplier != null) {

            SupplierSettings supplierSettings = supplierSettingsService
                    .getSupplierSettingsForFinanceByTenantId(favouriteSupplier.getSupplier().getId());
            if (supplierSettings != null) {
                LOG.info("---supplierSettings----------------------");
                if (supplierSettings.getPoShare() != null) {
                    if (supplierSettings.getPoShare() == PoShare.ALL) {
                        if (supplierSettings.getFinanceCompany() != null) {
                            LOG.info("Sharing  PO to finance company bassed on all setting-------------------------");
                            sharePOtoFinance(supplierSettings, po);
                        } else {
                            LOG.info("---Settings-----finance company---null--------------");
                        }
                    } else if (supplierSettings.getPoShare() == PoShare.BUYER) {
                        /*
                         * if (poSharingBuyerDao.checkPoSharingToFinanceonBuyerSetting(SecurityLibrary.
                         * getLoggedInUserTenantId(), pr.getSupplier().getSupplier().getId())) { LOG.
                         * info("Sharing PO to finance bassed on seleced buyer setting-------------------------"
                         * );
                         */
                        List<PoSharingBuyer> list = supplierSettingsService.getPoSharingBuyersbySupplierId(
                                po.getBuyer().getId(), favouriteSupplier.getSupplier().getId());
                        if (CollectionUtil.isNotEmpty(list)) {
                            LOG.info("Sharing  PO to finance company bassed on all setting-------------------------"
                                    + list.size());
                            for (PoSharingBuyer poSharingBuyer : list) {
                                LOG.info("---finance company id--------------"
                                        + poSharingBuyer.getFinanceCompany().getId());
                                supplierSettings.setFinanceCompany(poSharingBuyer.getFinanceCompany());
                                sharePOtoFinance(supplierSettings, po);
                                break;
                            }
                        } else {
                            LOG.info("---Settings-----finance company---null--------------");
                        }

                    }
                } else
                    LOG.info("---supplierSettings--------null--------------");

            }

        }
    }

    private void sharePOtoFinance(SupplierSettings supplierSettings, Po po) {
        if (supplierSettings.getFinanceCompany() != null) {
            FinancePo financePo = new FinancePo();
            financePo.setFinanceCompany(supplierSettings.getFinanceCompany());
            financePo.setSupplier(po.getSupplier().getSupplier());
            financePo.setPo(po);
            financePo.setReferenceNum(referenceNumber(supplierSettings.getFinanceCompany()));
            financePo.setCreatedDate(new Date());
            financePo.setFinancePoStatus(FinancePoStatus.NEW);
            financePo.setFinancePoType(FinancePoType.SHARED);
            financePo.setSharedDate(new Date());
            poFinanceDao.save(financePo);
            LOG.info("Sending PO share email to---------");
            sendPoShareEmailsToFinance(userDao.getAdminUserForFinance(supplierSettings.getFinanceCompany()), po);
        }

    }

    private String referenceNumber(FinanceCompany financeCompany) {
        String referenceNumber = "";
        Integer length = 6;
        String seqNo = "1";
        if (financeCompany != null) {
            FinanceCompanySettings financeCompanySettings = financeSettingsService
                    .getFinanceSettingsByTenantId(financeCompany.getId());
            if (financeCompanySettings != null) {
                if (StringUtils.checkString(financeCompanySettings.getPoSequencePrefix()).length() > 0) {
                    referenceNumber += financeCompanySettings.getPoSequencePrefix();
                }
                if (StringUtils.checkString(financeCompanySettings.getPoSequenceNumber()).length() > 0) {
                    seqNo = financeCompanySettings.getPoSequenceNumber();
                }
                if (financeCompanySettings.getPoSequenceLength() != null
                        && financeCompanySettings.getPoSequenceLength() != 0) {
                    length = financeCompanySettings.getPoSequenceLength();
                }

                referenceNumber += StringUtils.lpad(seqNo, length, '0');
                LOG.info("-----Updating settings-----------");
                int sequanceNum = Integer.parseInt((seqNo)) + 1;
                financeCompanySettings.setPoSequenceNumber("" + sequanceNum);
                financeSettingsService.updateFinanceSettingsSeqNumber(financeCompanySettings);
            } else {
                LOG.info("Finance Setting is null");
            }

        } else {
            LOG.info("Finance  is null");
        }
        LOG.info("-----referenceNumber----------->" + referenceNumber);
        return referenceNumber;
    }

    private void sendPoShareEmailsToFinance(User mailTo, Po po) {

        LOG.info("Sending PO share email to--------------------------------> (" + mailTo.getName() + ") : "
                + mailTo.getCommunicationEmail());
        try {
            String subject = "PO Share";
            String url = APP_URL + "/finance/financePOView/" + po.getId();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("userName", mailTo.getName());
            map.put("message", "");
            map.put("pr", po);

            String prKey = "";
            if(po.getPr()!=null){
                prKey = po.getPr().getId();
            }
            map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(prKey)));
            map.put("prReferanceNumber", (po.getPoNumber() == null ? "" : po.getPoNumber()));
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
            String timeZone = "GMT+8:00";
            timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
            df.setTimeZone(TimeZone.getTimeZone(timeZone));
            map.put("date", df.format(new Date()));
            map.put("loginUrl", APP_URL + "/login");
            map.put("appUrl", url);
            map.put("supplierName", "shared by " + po.getSupplierName());
            if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
                sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.FINANCEPO_SHARE_TEMPLATE);
            } else {
                LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                        + "... Not going to send email notification");
            }

            sendDashboardNotificationForFinance(mailTo, url, subject, "New PO shared by Supplier",
                    NotificationType.CREATED_MESSAGE);
        } catch (Exception e) {
            LOG.error("Error while Sending PO Created :" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public SourcingFormRequest doRequestApproval(SourcingFormRequest sourcingFormRequest, User loggedInUser)
            throws Exception {
        try {
            String buyerTimeZone = "GMT+8:00";

            sourcingFormRequest = sourcingFormRequestDao.findById(sourcingFormRequest.getId());
            if (sourcingFormRequest.getSubmittedDate() == null) {
                sourcingFormRequest.setSubmittedDate(new Date());
            }
            List<SourcingFormApprovalRequest> approvalList = sourcingFormRequest.getSourcingFormApprovalRequests();
            // If no approval setup - direct approve the Request
            if (CollectionUtil.isEmpty(approvalList)) {
                sourcingFormRequest.setStatus(SourcingFormStatus.APPROVED);
                if (sourcingFormRequest.getFirstApprovedDate() == null) {
                    sourcingFormRequest.setFirstApprovedDate(new Date());
                }
                sourcingFormRequest.setApprovedDate(new Date());
                Double days = DateUtil.getDiffHoursInDouble(sourcingFormRequest.getApprovedDate(),
                        sourcingFormRequest.getSubmittedDate());
                sourcingFormRequest.setApprovalDaysHours(BigDecimal.valueOf(days).setScale(2, RoundingMode.HALF_UP));
                sourcingFormRequest.setApprovalTotalLevels(0);
                sourcingFormRequest.setApprovalTotalUsers(0);

                sendrequestCreateEmail(loggedInUser, sourcingFormRequest, loggedInUser, buyerTimeZone);

                tatReportCreation(sourcingFormRequest);
            } else {

                SourcingFormApprovalRequest currentLevel = null;
                // Removed condition after discussing with Nitin
                if (sourcingFormRequest.getStatus() == SourcingFormStatus.DRAFT
                        || (sourcingFormRequest.getStatus() == SourcingFormStatus.PENDING)) {
                    sourcingFormRequest.setStatus(SourcingFormStatus.PENDING);
                    for (SourcingFormApprovalRequest prApproval : approvalList) {
                        if (prApproval.getLevel() == 1) {
                            prApproval.setActive(true);
                            break;
                        }
                    }
                } else {
                    for (SourcingFormApprovalRequest requestApproval : approvalList) {
                        if (requestApproval.isActive()) {
                            currentLevel = requestApproval;
                            break;
                        }

                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (SourcingFormApprovalUserRequest user : currentLevel.getApprovalUsersRequest()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the PR.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDoneForRequest(loggedInUser, approvalList, currentLevel, sourcingFormRequest);
                    }
                }

                // Just send emails to users.
                for (SourcingFormApprovalRequest requestApproval : approvalList) {
                    if (requestApproval.isActive()) {
                        for (SourcingFormApprovalUserRequest nextLevelUser : requestApproval
                                .getApprovalUsersRequest()) {
                            if (nextLevelUser.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                buyerTimeZone = getTimeZoneByBuyerSettings(nextLevelUser.getUser().getTenantId(),
                                        buyerTimeZone);
                                LOG.info(
                                        "***********************************************************************************************");
                                sendEmailToRequestApprovers(sourcingFormRequest, nextLevelUser, buyerTimeZone);

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
                        }
                    }
                }
            }
            sourcingFormRequest.setSummaryCompleted(true);

            sourcingFormRequest = requestService.update(sourcingFormRequest);

        } catch (Exception e) {
            LOG.error("ERROR While Approving request :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving request :" + e.getMessage());
        }
        return sourcingFormRequest;
    }

    private void setNextOrAllDoneForRequest(User actionBy, List<SourcingFormApprovalRequest> approvalList,
                                            SourcingFormApprovalRequest currentLevel, SourcingFormRequest sourcingFormRequest) {
        String buyerTimeZone = "GMT+8:00";
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this Request is done!!!. Going to Approved Mode.");

            sourcingFormRequest.setStatus(SourcingFormStatus.APPROVED);
            sourcingFormRequest.setActionBy(actionBy);
            sourcingFormRequest.setActionDate(new Date());
            if (sourcingFormRequest.getFirstApprovedDate() == null) {
                sourcingFormRequest.setFirstApprovedDate(new Date());
            }
            sourcingFormRequest.setApprovedDate(new Date());
            try {
                if (sourcingFormRequest.getApprovedDate() != null && sourcingFormRequest.getSubmittedDate() != null) {
                    Double days = DateUtil.getDiffHoursInDouble(sourcingFormRequest.getApprovedDate(),
                            sourcingFormRequest.getSubmittedDate());
                    sourcingFormRequest
                            .setApprovalDaysHours(BigDecimal.valueOf(days).setScale(2, RoundingMode.HALF_UP));
                } else {
                    sourcingFormRequest.setApprovalDaysHours(BigDecimal.ONE);
                }

                Integer approvalTotalUsers = 0;
                int maxLevel = 0;
                List<SourcingFormApprovalRequest> requestApprovalList = sourcingFormRequestDao
                        .getApprovalRequestList(sourcingFormRequest.getId());
                for (SourcingFormApprovalRequest approval : requestApprovalList) {
                    LOG.info("Level : " + approval.getLevel());
                    if (approval.getLevel() > maxLevel) {
                        maxLevel = approval.getLevel();
                    }
                    if (CollectionUtil.isNotEmpty(approval.getApprovalUsersRequest())) {
                        LOG.info("Users : " + approval.getApprovalUsersRequest().size());
                        approvalTotalUsers += approval.getApprovalUsersRequest().size();
                    }
                }
                LOG.info("approvalTotalUsers  : " + approvalTotalUsers);
                sourcingFormRequest.setApprovalTotalUsers(approvalTotalUsers);
                sourcingFormRequest.setApprovalTotalLevels(maxLevel);

                tatReportCreation(sourcingFormRequest);

            } catch (Exception e) {
                LOG.error("Error computing total approvers and levels and turn around time : " + e.getMessage(), e);
            }
        } else {
            for (SourcingFormApprovalRequest requestApproval : approvalList) {
                if (requestApproval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + requestApproval.getLevel() + " as Active level");
                    requestApproval.setActive(true);
                    for (SourcingFormApprovalUserRequest nextLevelUser : requestApproval.getApprovalUsersRequest()) {
                        buyerTimeZone = getTimeZoneByBuyerSettings(nextLevelUser.getUser().getTenantId(),
                                buyerTimeZone);
                        // send email to Approver
                        sendEmailToRequestApprovers(sourcingFormRequest, nextLevelUser, buyerTimeZone);

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
                    break;
                }
            }
        }

    }

    private void tatReportCreation(SourcingFormRequest sourcingFormRequest) {
        // TAT - Last Approval Date based on RFSID (Primary key)
        // update the total duration from Finished Date to last approval

        TatReportPojo tatReportPojo = tatReportService.getRfsFinishDatetByRfsIdAndIdAndTenantId(
                sourcingFormRequest.getId(), sourcingFormRequest.getTenantId());
        if (tatReportPojo != null) {
            double diffInDays = DateUtil.differenceInDays(new Date(), tatReportPojo.getFinishDate());
            tatReportService.updateTatReportReqApprovedAndApprovalDaysCountAndLastApprovedDate(tatReportPojo.getId(),
                    new Date(), BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP),
                    SourcingFormStatus.APPROVED);
        } else {
            TatReport tatReport = new TatReport();
            tatReport.setRequestGeneratedId(sourcingFormRequest.getId());
            tatReport.setFormId(sourcingFormRequest.getFormId());
            tatReport.setSourcingFormName(sourcingFormRequest.getSourcingFormName());
            tatReport.setBusinessUnit(sourcingFormRequest.getBusinessUnit().getUnitName());
            tatReport.setCostCenter(
                    sourcingFormRequest.getCostCenter() != null ? sourcingFormRequest.getCostCenter().getCostCenter()
                            : "");
            tatReport.setRequestOwner(
                    sourcingFormRequest.getFormOwner() != null
                            ? (sourcingFormRequest.getFormOwner().getName() + " "
                            + sourcingFormRequest.getFormOwner().getLoginId())
                            : "");
            tatReport.setGroupCode(sourcingFormRequest.getGroupCode() != null
                    ? sourcingFormRequest.getGroupCode().getGroupCode()
                    : (sourcingFormRequest.getGroupCodeOld() != null ? sourcingFormRequest.getGroupCodeOld() : ""));
            tatReport.setCreatedDate(sourcingFormRequest.getCreatedDate());
            tatReport.setProcurementMethod(sourcingFormRequest.getProcurementMethod() != null
                    ? sourcingFormRequest.getProcurementMethod().getProcurementMethod()
                    : "");
            tatReport.setProcurementCategories(sourcingFormRequest.getProcurementCategories() != null
                    ? sourcingFormRequest.getProcurementCategories().getProcurementCategories()
                    : "");
            tatReport.setBaseCurrency(
                    sourcingFormRequest.getCurrency() != null ? sourcingFormRequest.getCurrency().getCurrencyCode()
                            : "");
            tatReport.setTenantId(sourcingFormRequest.getTenantId());
            tatReport.setReqDecimal(sourcingFormRequest.getDecimal());
            tatReport.setFirstApprovedDate(new Date());
            tatReport.setLastApprovedDate(new Date());
            tatReport.setAvailableBudget(sourcingFormRequest.getBudgetAmount());
            tatReport.setEstimatedBudget(sourcingFormRequest.getEstimatedBudget());
            tatReport.setFinishDate(sourcingFormRequest.getSubmittedDate());

            double diffInDays = DateUtil.differenceInDays(new Date(), tatReport.getFinishDate());
            tatReport.setReqApprovalDaysCount(BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP));
            tatReport.setRequestStatus(SourcingFormStatus.APPROVED);
            tatReportService.saveTatReport(tatReport);
        }
    }

    /**
     * SEND EMAIL REUEST EMAIL HERE
     *
     * @author sudesha
     * @param mailTo
     * @param sourcingFormRequest
     * @param actionBy
     * @param remarks
     */

    private void sendrequestCreateEmail(User mailTo, SourcingFormRequest sourcingFormRequest, User actionBy,
                                        String remarks) {
        try {
            LOG.info("***********************************************************************************************");
            LOG.info("Sending Approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

            String subject = "RFS Created";
            String url = APP_URL + "/buyer/viewSourcingSummary/" + sourcingFormRequest.getId();
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("userName", mailTo.getName());
            map.put("actionByName", actionBy.getName());
            map.put("request", sourcingFormRequest);
            map.put("remarks", StringUtils.checkString(remarks));
            map.put("businessUnit", StringUtils.checkString(getBusineessUnitnamerequest(sourcingFormRequest.getId())));
            map.put("requestReferanceNumber", StringUtils.checkString(sourcingFormRequest.getReferanceNumber()));
            if (mailTo.getId().equals(actionBy.getId())) {
                map.put("message", "You have Approved");
            } else {
                map.put("message", actionBy.getName() + " has Approved");
            }
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
            String timeZone = "GMT+8:00";
            timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
            df.setTimeZone(TimeZone.getTimeZone(timeZone));
            map.put("date", df.format(new Date()));
            map.put("loginUrl", APP_URL + "/login");
            map.put("appUrl", url);
            if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
                sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.REQUEST_APPROVAL_CREATE_EMAIL);
            } else {
                LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                        + "... Not going to send email notification");
            }
            String notificationMessage = messageSource.getMessage("request.create.notification.message",
                    new Object[] { actionBy.getName(), sourcingFormRequest.getSourcingFormName() }, Global.LOCALE);
            sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

        } catch (Exception e) {
            LOG.error("ERROR While Appr" + e.getMessage(), e);
        }
    }

    /**
     * GET BUSINESS UNIT FOR SEND EMAIL CREATION FOR REQUEST
     *
     * @author sudesha
     * @param id
     * @return
     */
    private String getBusineessUnitnamerequest(String id) {
        String displayName = null;
        displayName = sourcingFormRequestDao.getBusineessUnitnamerequest(id);
        return StringUtils.checkString(displayName);
    }

    /**
     * SEND REQUEST APPROVAL EMAIL HERE
     *
     * @author sudesha
     * @param mailTo
     * @param sourcingFormRequest
     * @param actionBy
     * @param remarks
     */
    private void sendRequestApprovalEmail(User mailTo, SourcingFormRequest sourcingFormRequest, User actionBy,
                                          String remarks) {
        LOG.info("Sending Approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
        String subject = "RFS Approved";
        String url = APP_URL + "/buyer/viewSourcingSummary/" + sourcingFormRequest.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("request", sourcingFormRequest);
        map.put("remarks", StringUtils.checkString(remarks));
        map.put("businessUnit", StringUtils.checkString(getBusineessUnitnamerequest(sourcingFormRequest.getId())));
        map.put("requestReferanceNumber", StringUtils.checkString(sourcingFormRequest.getReferanceNumber()));
        if (mailTo.getId().equals(actionBy.getId())) {
            map.put("message", "You have Approved");
        } else {
            map.put("message", actionBy.getName() + " has Approved");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.REQUEST_APPROVAL_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }
        String notificationMessage = messageSource.getMessage("request.approval.notification.message",
                new Object[] { actionBy.getName(), sourcingFormRequest.getSourcingFormName() }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

    }

    @Override
    public void sendEmailToRequestApprovers(SourcingFormRequest sourcingFormRequest,
                                            SourcingFormApprovalUserRequest nextLevelUser, String buyerTimeZone) {
        String mailTo = nextLevelUser.getUser().getCommunicationEmail();
        String subject = "RFS Approval request";
        String url = APP_URL + "/buyer/viewSourcingSummary/" + sourcingFormRequest.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", nextLevelUser.getUser().getName());
        map.put("request", sourcingFormRequest);
        map.put("businessUnit", StringUtils.checkString(getBusineessUnitnamerequest(sourcingFormRequest.getId())));
        map.put("requestReferanceNumber",
                (sourcingFormRequest.getReferanceNumber() == null ? "" : sourcingFormRequest.getReferanceNumber()));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        if (StringUtils.checkString(mailTo).length() > 0 && nextLevelUser.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.REQURST_APPROVAL_REQUEST_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + nextLevelUser.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("request.approval.request.notification.message",
                new Object[] { sourcingFormRequest.getSourcingFormName() }, Global.LOCALE);
        sendDashboardNotification(nextLevelUser.getUser(), url, subject, notificationMessage,
                NotificationType.APPROVAL_MESSAGE);
        if (StringUtils.checkString(nextLevelUser.getUser().getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo + "' and device Id :" + nextLevelUser.getUser().getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", sourcingFormRequest.getId());
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                payload.put("eventType", FilterTypes.PR.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(nextLevelUser.getUser().getDeviceId()));
            } catch (Exception e) {
                LOG.error(
                        "Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(),
                        e);
            }
        } else {
            LOG.info("User '" + mailTo + "' Device Id is Null");
        }
    }

    /**
     * SEND REQUEST REJECTION EMAIL HERE
     *
     * @author sudesha
     * @param mailTo
     * @param sourcingFormRequest
     * @param actionBy
     * @param remarks
     */
    private void sendsourcingRejectionEmail(User mailTo, SourcingFormRequest sourcingFormRequest, User actionBy,
                                            String remarks) {
        LOG.info("Sending rejected request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
        String url = APP_URL + "/buyer/viewSourcingSummary/" + sourcingFormRequest.getId();
        String subject = "RFS Rejected";
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("request", sourcingFormRequest);
        map.put("remarks", StringUtils.checkString(remarks));
        map.put("businessUnit", StringUtils.checkString(getBusineessUnitnamerequest(sourcingFormRequest.getId())));
        map.put("requestReferanceNumber", StringUtils.checkString(sourcingFormRequest.getReferanceNumber()));
        if (mailTo.getId().equals(actionBy.getId())) {
            map.put("message", "You have Rejected");
        } else {
            map.put("message", actionBy.getName() + " has Rejected");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.SOURCING_REJECT_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("request.rejection.notification.message",
                new Object[] { actionBy.getName(), sourcingFormRequest.getSourcingFormName(), remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);

    }

    @Override
    @Transactional(readOnly = false)
    public SourcingFormRequest doApprovalRequest(SourcingFormRequest sourcingFormRequest, User actionBy, String remarks,
                                                 boolean approved) throws Exception {

        sourcingFormRequest = sourcingFormRequestDao.findById(sourcingFormRequest.getId());
        List<SourcingFormApprovalRequest> approvalList = sourcingFormRequest.getSourcingFormApprovalRequests();
        List<SourcingFormTeamMember> sourcingFormTeamMembers = sourcingFormRequest.getSourcingFormTeamMember();

        // Identify Current Approval Level
        SourcingFormApprovalRequest currentLevel = null;
        for (SourcingFormApprovalRequest sourcingFormApproval : approvalList) {
            if (sourcingFormApproval.isActive()) {
                currentLevel = sourcingFormApproval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        SourcingFormApprovalUserRequest actionUser = null;
        if (currentLevel != null) {
            for (SourcingFormApprovalUserRequest user : currentLevel.getApprovalUsersRequest()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject RFS '"
                    + sourcingFormRequest.getSourcingFormName() + "' at approval level : "
                    + (currentLevel != null ? currentLevel.getLevel() : "Not Found"));
            throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject PR '"
                    + sourcingFormRequest.getSourcingFormName() + "' at approval level : "
                    + (currentLevel != null ? currentLevel.getLevel() : ""));
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " RFS at : "
                    + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " RFS at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (sourcingFormRequest.getRequestComments() == null) {
            sourcingFormRequest.setRequestComments(new ArrayList<RequestComment>());
        }
        RequestComment requestComment = new RequestComment();
        requestComment.setComment(remarks);
        requestComment.setApproved(approved);
        requestComment.setCreatedBy(actionBy);
        requestComment.setCreatedDate(new Date());
        requestComment.setRequest(sourcingFormRequest);
        requestComment.setApprovalUserId(actionUser.getId());
        sourcingFormRequest.getRequestComments().add(requestComment);

        // If rejected
        if (!approved) {
            // Reset all approvals for re-approval as the PR is rejected.
            for (SourcingFormApprovalRequest sourcingFormApproval : approvalList) {
                sourcingFormApproval.setDone(false);
                sourcingFormApproval.setActive(false);

                for (SourcingFormApprovalUserRequest user : sourcingFormApproval.getApprovalUsersRequest()) {
                    try {
                        if (!sourcingFormRequest.getCreatedBy().getId().equals(user.getUser().getId())) {
                            sendsourcingRejectionEmail(user.getUser(), sourcingFormRequest, actionBy, remarks);
                        }
                    } catch (Exception e) {
                        LOG.info("ERROR while Sending RFS reject mail :" + e.getMessage(), e);
                    }
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                }
            }

            if (CollectionUtil.isNotEmpty(sourcingFormTeamMembers)) {
                for (SourcingFormTeamMember sourcingFormTeamMember : sourcingFormTeamMembers) {
                    try {
                        sendsourcingRejectionEmail(sourcingFormTeamMember.getUser(), sourcingFormRequest, actionBy, remarks);
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);
            LOG.info("User " + actionBy.getName() + " has Rejected the RFS : "
                    + sourcingFormRequest.getSourcingFormName());
            sourcingFormRequest.setStatus(SourcingFormStatus.DRAFT);
            sourcingFormRequest.setApprovedDate(null);
            sourcingFormRequest.setApprovalDaysHours(null);
            sourcingFormRequest.setApprovalTotalLevels(null);
            sourcingFormRequest.setApprovalTotalUsers(null);

            try {
                if (sourcingFormRequest.getCreatedBy() != null) {
                    sendsourcingRejectionEmail(sourcingFormRequest.getCreatedBy(), sourcingFormRequest, actionBy,
                            remarks);
                }
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

            // Check if budget checking ERP interface is enabled
            ErpSetup erpSetup = erpConfigDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
            if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()
                    && Boolean.TRUE == erpSetup.getEnableRfsErpPush()
                    && Boolean.TRUE == sourcingFormRequest.getBusinessUnit().getBudgetCheck()) {
                erpIntegrationService.transferRejectRfsToErp(sourcingFormRequest.getId(), erpSetup, actionBy);
            }

            sourcingFormRequest.setErpDocNo(null);
            sourcingFormRequest.setErpMessage(null);
            sourcingFormRequest.setErpTransferred(Boolean.FALSE);

            // TAT - update rejected time based on RFSID (primary key)
            TatReportPojo tatReportPojo = tatReportService.getRfsFinishDatetByRfsIdAndIdAndTenantId(
                    sourcingFormRequest.getId(), SecurityLibrary.getLoggedInUserTenantId());
            if (tatReportPojo != null) {
                tatReportService.updateTatReportReqRejection(tatReportPojo.getId(), new Date(),
                        SourcingFormStatus.DRAFT);
            } else {
                TatReport tatReport = new TatReport();
                tatReport.setRequestGeneratedId(sourcingFormRequest.getId());
                tatReport.setFormId(sourcingFormRequest.getFormId());
                tatReport.setSourcingFormName(sourcingFormRequest.getSourcingFormName());
                tatReport.setBusinessUnit(sourcingFormRequest.getBusinessUnit().getUnitName());
                tatReport.setCostCenter(sourcingFormRequest.getCostCenter() != null
                        ? sourcingFormRequest.getCostCenter().getCostCenter()
                        : "");
                tatReport.setRequestOwner(
                        sourcingFormRequest.getFormOwner() != null
                                ? (sourcingFormRequest.getFormOwner().getName() + " "
                                + sourcingFormRequest.getFormOwner().getLoginId())
                                : "");
                tatReport.setGroupCode(sourcingFormRequest.getGroupCode() != null
                        ? sourcingFormRequest.getGroupCode().getGroupCode()
                        : (sourcingFormRequest.getGroupCodeOld() != null ? sourcingFormRequest.getGroupCodeOld() : ""));
                tatReport.setCreatedDate(sourcingFormRequest.getCreatedDate());
                tatReport.setProcurementMethod(sourcingFormRequest.getProcurementMethod() != null
                        ? sourcingFormRequest.getProcurementMethod().getProcurementMethod()
                        : "");
                tatReport.setProcurementCategories(sourcingFormRequest.getProcurementCategories() != null
                        ? sourcingFormRequest.getProcurementCategories().getProcurementCategories()
                        : "");
                tatReport.setBaseCurrency(
                        sourcingFormRequest.getCurrency() != null ? sourcingFormRequest.getCurrency().getCurrencyCode()
                                : "");
                tatReport.setTenantId(actionBy.getTenantId());
                tatReport.setReqDecimal(sourcingFormRequest.getDecimal());
                tatReport.setReqRejectedDate(new Date());
                tatReport.setAvailableBudget(sourcingFormRequest.getBudgetAmount());
                tatReport.setEstimatedBudget(sourcingFormRequest.getEstimatedBudget());
                tatReport.setFinishDate(sourcingFormRequest.getSubmittedDate());
                tatReport.setRequestStatus(SourcingFormStatus.DRAFT);
                tatReportService.saveTatReport(tatReport);
            }

        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the RFS : "
                    + sourcingFormRequest.getSourcingFormName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            // Send email notification to Approval
            sendRequestApprovalEmail(sourcingFormRequest.getCreatedBy(), sourcingFormRequest, actionBy, remarks);

            // Send email notification to Approval
            if (!sourcingFormRequest.getCreatedBy().getId().equals(actionBy.getId())) {
                sendRequestApprovalEmail(actionBy, sourcingFormRequest, actionBy, remarks);
            }

            if (CollectionUtil.isNotEmpty(sourcingFormTeamMembers)) {
                for (SourcingFormTeamMember sourcingFormTeamMember : sourcingFormTeamMembers) {
                    try {
                        sendRequestApprovalEmail(sourcingFormTeamMember.getUser(), sourcingFormRequest, actionBy,
                                remarks);
                    } catch (Exception e) {
                        LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                    }
                }
            }

            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDoneForRequest(actionBy, approvalList, currentLevel, sourcingFormRequest);
                Integer cnt = 1;
                if (cnt.equals(currentLevel.getLevel())) {
                    tatReportService.updateTatReportReqFirstApprovedDate(sourcingFormRequest.getId(),
                            actionBy.getTenantId(), new Date());
                }
            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (SourcingFormApprovalUserRequest user : currentLevel.getApprovalUsersRequest()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            LOG.info("All users of this level have not approved the request.");
                            allUsersDone = false;
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the RFS.");
                    setNextOrAllDoneForRequest(actionBy, approvalList, currentLevel, sourcingFormRequest);
                    // if(currentLevel == 1)
                    // TAT - First Approval Date based on RFSID (Primary Key)

                    long count = tatReportService.getRfsCountForTatReportListByRfsFormIdAndIdAndTenantId(
                            sourcingFormRequest.getId(), SecurityLibrary.getLoggedInUserTenantId());
                    Integer cnt = 1;
                    if (count > 0 && cnt.equals(currentLevel.getLevel())) {
                        tatReportService.updateTatReportReqFirstApprovedDate(sourcingFormRequest.getId(),
                                actionBy.getTenantId(), new Date());
                    } else {
                        TatReport tatReport = new TatReport();
                        tatReport.setRequestGeneratedId(sourcingFormRequest.getId());
                        tatReport.setFormId(sourcingFormRequest.getFormId());
                        tatReport.setSourcingFormName(sourcingFormRequest.getSourcingFormName());
                        tatReport.setBusinessUnit(sourcingFormRequest.getBusinessUnit().getUnitName());
                        tatReport.setCostCenter(sourcingFormRequest.getCostCenter() != null
                                ? sourcingFormRequest.getCostCenter().getCostCenter()
                                : "");
                        tatReport.setRequestOwner(
                                sourcingFormRequest.getFormOwner() != null
                                        ? (sourcingFormRequest.getFormOwner().getName() + " "
                                        + sourcingFormRequest.getFormOwner().getLoginId())
                                        : "");
                        tatReport.setGroupCode(sourcingFormRequest.getGroupCode() != null
                                ? sourcingFormRequest.getGroupCode().getGroupCode()
                                : (sourcingFormRequest.getGroupCodeOld() != null ? sourcingFormRequest.getGroupCodeOld()
                                : ""));
                        tatReport.setCreatedDate(sourcingFormRequest.getCreatedDate());
                        tatReport.setProcurementMethod(sourcingFormRequest.getProcurementMethod() != null
                                ? sourcingFormRequest.getProcurementMethod().getProcurementMethod()
                                : "");
                        tatReport.setProcurementCategories(sourcingFormRequest.getProcurementCategories() != null
                                ? sourcingFormRequest.getProcurementCategories().getProcurementCategories()
                                : "");
                        tatReport.setBaseCurrency(sourcingFormRequest.getCurrency() != null
                                ? sourcingFormRequest.getCurrency().getCurrencyCode()
                                : "");
                        tatReport.setTenantId(actionBy.getTenantId());
                        tatReport.setReqDecimal(sourcingFormRequest.getDecimal());
                        tatReport.setFirstApprovedDate(new Date());
                        tatReport.setRequestStatus(sourcingFormRequest.getStatus());
                        tatReport.setAvailableBudget(sourcingFormRequest.getBudgetAmount());
                        tatReport.setEstimatedBudget(sourcingFormRequest.getEstimatedBudget());
                        tatReport.setFinishDate(sourcingFormRequest.getSubmittedDate());
                        tatReportService.saveTatReport(tatReport);
                    }
                }
            }
        }
        // pr.setPrApprovals(approvalList);
        return requestService.update(sourcingFormRequest);
    }

    @Override
    public void sendEmailToAssociatedBuyer(Supplier supplier, String supplierRemark, Buyer buyer, TimeZone timeZone) {
        LOG.info("Sending Email Request to associated buyer (" + buyer.getFullName() + ") : "
                + buyer.getPublishedProfileCommunicationEmail());
        try {
            String subject = "Supplier Request";
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("buyerName", buyer.getFullName());
            map.put("supplierName", supplier.getCompanyName());
            map.put("message", "You have received a new supplier request");
            map.put("supplierCountry", supplier.getRegistrationOfCountry().getCountryName());
            map.put("supplierRemark", supplierRemark);
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
            df.setTimeZone(timeZone);
            map.put("date", df.format(new Date()));
            map.put("loginUrl", APP_URL + "/login");
            map.put("appUrl", APP_URL + "/login");
            User user = userDao.getDetailsOfLoggedinUser(buyer.getLoginEmail());
            if (StringUtils.checkString(buyer.getPublishedProfileCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
                sendEmail(buyer.getPublishedProfileCommunicationEmail(), subject, map, Global.REQUEST_ASSOCIATE_BUYER);
            } else {
                LOG.warn("No communication email configured for user : " + buyer.getFullName()
                        + "... Not going to send email notification");
            }
        } catch (Exception e) {
            LOG.error("Error while Sending request email to associate buyer :" + e.getMessage(), e);
        }
    }

    @Override
    public void sendEmailToAssociatedSupplier(Supplier supplier, Buyer buyer, TimeZone timeZone,
                                              boolean approveRejectFlag, RequestedAssociatedBuyer associatedBuyer, String buyerRemark) {
        LOG.info("Sending Email to associated supplier (" + supplier.getFullName() + ") : "
                + supplier.getCommunicationEmail());

        try {

            HashMap<String, Object> map = new HashMap<String, Object>();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
            df.setTimeZone(timeZone);
            map.put("loginUrl", APP_URL + "/login");
            map.put("appUrl", APP_URL + "/login");
            if (approveRejectFlag == true) {
                // Request has been accepted.
                map.put("message", "Your request to be associated with the buyer has been accepted.");
                map.put("supplierName", supplier.getFullName());
                map.put("buyerName", buyer.getCompanyName());
                map.put("buyerCountry", buyer.getRegistrationOfCountry().getCountryName());
                map.put("reqDate", df.format(associatedBuyer.getRequestedDate()));
                map.put("date", df.format(new Date()));
                map.put("buyerRemarks", StringUtils.checkString(buyerRemark));
                User user = userDao.getDetailsOfLoggedinUser(supplier.getLoginEmail());
                if (StringUtils.checkString(supplier.getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
                    sendEmail(supplier.getCommunicationEmail(), "Request Accepted", map,
                            Global.REQUEST_ASSOCIATE_BUYER_ACCEPT);
                } else {
                    LOG.warn("No communication email configured for user : " + supplier.getCompanyName()
                            + "... Not going to send email notification");
                }
            } else {
                // Request has been rejected.
                map.put("supplierName", supplier.getFullName());
                map.put("buyerName", buyer.getCompanyName());
                map.put("message", "Your request to be associated with the buyer has been rejected.");
                map.put("buyerCountry", buyer.getRegistrationOfCountry().getCountryName());
                map.put("date", df.format(associatedBuyer.getRequestedDate()));
                map.put("rejectionDate", df.format(new Date()));
                map.put("buyerRemark", StringUtils.checkString(buyerRemark));
                User user= userDao.getDetailsOfLoggedinUser(supplier.getLoginEmail());
                if (StringUtils.checkString(supplier.getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
                    sendEmail(supplier.getCommunicationEmail(), "Request Rejected", map,
                            Global.REQUEST_ASSOCIATE_BUYER_REJECT);
                } else {
                    LOG.warn("No communication email configured for user : " + supplier.getFullName()
                            + "... Not going to send email notification");
                }
            }
        } catch (Exception e) {
            LOG.error("Error while Sending request email to associate buyer :" + e.getMessage(), e);
        }

    }

    @Override
    public void sendEmailToFavSupplier(Supplier supplier, Buyer buyer, TimeZone timeZone) {
        LOG.info("Sending Email Request to associated buyer (" + buyer.getFullName() + ") : "
                + buyer.getPublishedProfileCommunicationEmail());
        try {
            String subject = "Buyer Associated";
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("buyerName", buyer.getCompanyName());
            map.put("supplierName", supplier.getFullName());
            map.put("message", "You have been added as a supplier.");
            map.put("buyerCountry", buyer.getRegistrationOfCountry().getCountryName());
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
            df.setTimeZone(timeZone);
            map.put("date", df.format(new Date()));
            map.put("loginUrl", APP_URL + "/login");
            map.put("appUrl", APP_URL + "/login");
            User user = userDao.getDetailsOfLoggedinUser(supplier.getLoginEmail());
            if (StringUtils.checkString(supplier.getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
                sendEmail(supplier.getCommunicationEmail(), subject, map, Global.ADDED_FAV_SUPPLIER);
            } else {
                LOG.warn("No communication email configured for user : " + supplier.getFullName()
                        + "... Not going to send email notification");
            }
        } catch (Exception e) {
            LOG.error("Error while Sending request email to associate buyer :" + e.getMessage(), e);
        }

    }

    @Override
    @Transactional(readOnly = false)
    public Budget doBudgetApproval(Budget budget, User actionBy, String remarks, boolean approved, HttpSession session)
            throws NotAllowedException {
        budget = budgetService.findById(budget.getId());

        List<BudgetApproval> approvalList = budget.getApprovals();

        // Identify Current Approval Level
        BudgetApproval currentLevel = null;
        for (BudgetApproval approval : approvalList) {
            if (approval.isActive()) {//
                currentLevel = approval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        BudgetApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (BudgetApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject budget '"
                    + budget.getBudgetName() + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User " + actionBy.getName() + " is not allowed to Approve or Reject budget '"
                    + budget.getBudgetName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus() + " Budget at : "
                    + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " Budget at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (budget.getBudgetComment() == null) {
            budget.setBudgetComment(new ArrayList<BudgetComment>());
        }
        BudgetComment budgetComment = new BudgetComment();
        budgetComment.setComment(remarks);
        budgetComment.setApproved(approved);
        budgetComment.setCreatedBy(actionBy);
        budgetComment.setCreatedDate(new Date());
        budgetComment.setBudget(budget);
        budgetComment.setApprovalUserId(actionUser.getId());
        budget.getBudgetComment().add(budgetComment);

        // If rejected
        if (!approved) {
            // For OR level Rejection should be handled differently
            // commented on user requirement don't want to hold if any user is rejecting

            // reset revision amount and justification if additional approval rejected
            // budget
            if (null != budget.getRevisionJustification()) {
                budget.setRevisionAmount(null);
                budget.setRevisionJustification(null);
                budget.setConversionRate(null);
                budget.setToBusinessUnit(null);
                budget.setToCostCenter(null);

                Integer batchNo = 0;
                // get latest batch
                for (BudgetApproval approval : approvalList) {
                    if (approval.getBatchNo() > batchNo) {
                        batchNo = approval.getBatchNo();
                    }
                }
                // Reset approvals. if additional approval rejects budget .
                for (Iterator<BudgetApproval> iterator = approvalList.iterator(); iterator.hasNext();) {
                    BudgetApproval approval = iterator.next();
                    LOG.info("additional reject ==========> " + approval.getBatchNo());
                    if (batchNo == approval.getBatchNo()) {
                        LOG.info("additional reject current==========> " + approval.getBatchNo());
                        approval.setDone(false);
                        approval.setActive(false);
                        for (BudgetApprovalUser user : approval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                // Send rejection email
                                sentBudgetRejectionEmail(user.getUser(), budget.getId(), budget.getBudgetName(),
                                        actionBy, remarks, budget.getCreatedBy(), budget, budget.getBudgetId());
                            }
                            user.setActionDate(null);
                            // user.setApprovalStatus(ApprovalStatus.PENDING);
                            user.setRemarks(null);
                            user.setActionDate(null);
                        }
                    }
                }

                actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
                actionUser.setActionDate(new Date());
                actionUser.setRemarks(remarks);

                try {
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED,
                            "Budget '" + budget.getBudgetId() + "' is Rejected", actionBy.getTenantId(), actionBy,
                            new Date(), ModuleType.BudgetPlanner);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                } catch (SecurityRuntimeException e1) {
                    LOG.info("Error in creating Audit");
                }

                Date now = new Date();
                if (budget.getValidFrom() != null && now.after(budget.getValidFrom())) {
                    LOG.info("Changing BUDGET status APPROVED to ACTIVE " + budget.getId());
                    LOG.info("additional approval User " + actionBy.getName() + " has Rejected the budget : "
                            + budget.getBudgetName());
                    budget.setBudgetStatus(BudgetStatus.ACTIVE);

                } else {
                    LOG.info("additional approval User " + actionBy.getName() + " has Rejected the budget : "
                            + budget.getBudgetName());
                    budget.setBudgetStatus(BudgetStatus.APPROVED);

                }
                // removed additional approvals
                // budget.setApprovals(approvalList);
                try {
                    if (budget.getCreatedBy() != null && !actionBy.equals(budget.getCreatedBy())) {
                        LOG.info("Sending rejected request email to owner : "
                                + budget.getCreatedBy().getCommunicationEmail());
                        sentBudgetRejectionEmail(budget.getCreatedBy(), budget.getId(), budget.getBudgetName(),
                                actionBy, remarks, budget.getCreatedBy(), budget, budget.getBudgetId());
                    }
                } catch (Exception e) {
                    LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
                }

            } else {
                // if rejected by original budget approval
                // Reset all approvals for re-approval as the budget is rejected.
                for (BudgetApproval approval : approvalList) {
                    approval.setDone(false);
                    approval.setActive(false);
                    for (BudgetApprovalUser user : approval.getApprovalUsers()) {
                        if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                            // Send rejection email
                            sentBudgetRejectionEmail(user.getUser(), budget.getId(), budget.getBudgetName(), actionBy,
                                    remarks, budget.getCreatedBy(), budget, budget.getBudgetId());
                        }
                        user.setActionDate(null);
                        user.setApprovalStatus(ApprovalStatus.PENDING);
                        user.setRemarks(null);
                        user.setActionDate(null);
                    }
                }
                actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
                actionUser.setActionDate(new Date());
                actionUser.setRemarks(remarks);

                LOG.info("User " + actionBy.getName() + " has Rejected the budget : " + budget.getBudgetName());
                budget.setBudgetStatus(BudgetStatus.REJECTED);

                // snapShotAuditService.doRfaAudit(event, session, event, actionBy,
                // AuditActionType.Reject,
                // "event.audit.rejected");

                try {
                    if (budget.getCreatedBy() != null && !actionBy.equals(budget.getCreatedBy())) {
                        LOG.info("Sending rejected request email to owner : "
                                + budget.getCreatedBy().getCommunicationEmail());
                        sentBudgetRejectionEmail(budget.getCreatedBy(), budget.getId(), budget.getBudgetName(),
                                actionBy, remarks, budget.getCreatedBy(), budget, budget.getBudgetId());
                    }
                } catch (Exception e) {
                    LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
                }
            }

        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the budget : " + budget.getBudgetName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            try {
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                        "Budget '" + budget.getBudgetId() + "' is Approved", actionBy.getTenantId(), actionBy,
                        new Date(), ModuleType.BudgetPlanner);
                buyerAuditTrailDao.save(buyerAuditTrail);
            } catch (Exception e) {
                LOG.info("Error in creating Audit");
            }

            // Send email notification to Creator
            sentBudgetApprovalEmail(budget.getCreatedBy(), budget.getId(), budget.getBudgetName(), actionBy, remarks,
                    budget.getCreatedBy(), budget, false, budget.getBudgetId());
            // Send email notification to actionBy
            sentBudgetApprovalEmail(actionBy, budget.getId(), budget.getBudgetName(), actionBy, remarks,
                    budget.getCreatedBy(), budget, true, budget.getBudgetId());

            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDoneForBudget(actionBy, approvalList, currentLevel, budget, session, actionBy);
            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (BudgetApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            allUsersDone = false;
                            LOG.info("All users of this level have not approved the Budget.");
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the Budget.");
                    setNextOrAllDoneForBudget(actionBy, approvalList, currentLevel, budget, session, actionBy);
                }
            }
        }
        return budgetDao.update(budget);
    }

    private void sentBudgetRejectionEmail(User mailTo, String id, String budgetName, User actionBy, String remarks,
                                          User ownerUser, Budget budget, String budgetId) {
        LOG.info("Sending rejected request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

        String subject = "Budget Rejected";
        String url = APP_URL + "/admin/budgets/viewBudget/" + id;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("budgetName", budgetName);
        // map.put("eventType", type.name());
        map.put("remarks", remarks);
        map.put("budgetId", budgetId);
        map.put("businessUnit", StringUtils.checkString(budget.getBusinessUnit().getUnitName()));
        if (mailTo.getId().equals(actionBy.getId())) {
            map.put("message", "You have Rejected");
        } else {
            map.put("message", actionBy.getName() + " has Rejected");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");
        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.BUDGET_REJECT_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("event.rejection.notification.message",
                new Object[] { actionBy.getName(), budgetId, budgetName, remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);

        if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", id);
                payload.put("messageType", NotificationType.REJECT_MESSAGE.toString());
                // payload.put("eventType", type.name());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(mailTo.getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Event reject Mobile push notification to '"
                        + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
        }
    }

    private void sentBudgetApprovalEmail(User mailTo, String id, String budgetName, User actionBy, String remarks,
                                         User ownerUser, Budget budget, boolean self, String budgetId) {
        LOG.info("Sending approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

        String subject = "Budget Approved";
        String url = APP_URL + "/admin/budgets/viewBudget/" + budget.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("budgetName", budgetName);
        // map.put("eventType", type.name());
        map.put("remarks", remarks);
        map.put("budgetId", budgetId);
        map.put("businessUnit", StringUtils.checkString(budget.getBusinessUnit().getUnitName()));
        if (self) {
            map.put("message", "You have Approved");
        } else {
            map.put("message", actionBy.getName() + " has Approved");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");
        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.BUDGET_APPROVAL_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("event.approval.notification.message",
                new Object[] { actionBy.getName(), budgetId, remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

        if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", id);
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                // payload.put("eventType", type.name());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(mailTo.getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Budget approve Mobile push notification to '"
                        + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
        }
    }

    private void setNextOrAllDoneForBudget(User actionBy, List<BudgetApproval> approvalList,
                                           BudgetApproval currentLevel, Budget budget, HttpSession session, User loggedInUser) {
        // Check if all approvals are done
        currentLevel.setDone(true);
        currentLevel.setActive(false);
        // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this budget is done!!!. Going to Approved Mode.");
            budget.setBudgetStatus(BudgetStatus.APPROVED);
            budget.setModifiedBy(actionBy);
            budget.setModifiedDate(new Date());

            // reset revision amount and justification
            if (null != budget.getRevisionJustification()) {
                // budget add/deduct/transfer
                TransactionLog transactionLog = new TransactionLog();
                transactionLog.setBudget(budget);
                transactionLog.setReferanceNumber(budget.getBudgetId());
                transactionLog.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
                transactionLog.setTransactionTimeStamp(new Date());
                // if Add
                if (1 == budget.getRevisionAmount().compareTo(budget.getTotalAmount())) {
                    BigDecimal addAmount = budget.getRevisionAmount().subtract(budget.getTotalAmount());
                    // add amount
                    budget.setRemainingAmount((addAmount).add(budget.getRemainingAmount()));
                    budget.setTotalAmount(budget.getRevisionAmount());
                    budget.setRevisionAmount(null);
                    budget.setRevisionJustification(null);
                    // save transaction for Add
                    transactionLog.setAddAmount(addAmount);
                    transactionLog.setRemainingAmount(budget.getRemainingAmount());
                    transactionLog.setTransactionLogStatus(TransactionLogStatus.ADD);
                    transactionLogService.saveTransactionLog(transactionLog);

                } else if (-1 == budget.getRevisionAmount().compareTo(budget.getTotalAmount())
                        && null == budget.getToBusinessUnit()) {
                    // if deduct
                    BigDecimal deductAmount = budget.getTotalAmount().subtract(budget.getRevisionAmount());
                    budget.setRemainingAmount(budget.getRemainingAmount().subtract(deductAmount));
                    budget.setTotalAmount(budget.getTotalAmount().subtract(deductAmount));
                    budget.setRevisionAmount(null);
                    budget.setRevisionJustification(null);
                    // save transaction for deduct
                    transactionLog.setDeductAmount(deductAmount);
                    transactionLog.setRemainingAmount(budget.getRemainingAmount());
                    transactionLog.setTransactionLogStatus(TransactionLogStatus.DEDUCT);
                    transactionLogService.saveTransactionLog(transactionLog);
                } else if (-1 == budget.getRevisionAmount().compareTo(budget.getTotalAmount())
                        && null != budget.getToBusinessUnit() && null != budget.getToCostCenter()) {
                    // if transfer
                    // Budget budgetBUCC = budgetService.findTransferToBudgetById(budget.getId());
                    Budget toBudget = budgetService.findBudgetByBusinessUnitAndCostCenter(
                            budget.getToBusinessUnit().getId(), budget.getToCostCenter().getId());
                    BigDecimal changeAmount = budget.getTotalAmount().subtract(budget.getRevisionAmount());
                    if (budget.getConversionRate() != null) {
                        BigDecimal changeAmountAfterConversion = budget.getConversionRate().multiply(changeAmount);
                        LOG.info("***************change amount " + changeAmount);
                        LOG.info("*************** changeAmountAfterConversion " + changeAmountAfterConversion);
                        // save toBudget for add transfer amount
                        if (toBudget != null) {
                            toBudget.setTotalAmount(toBudget.getTotalAmount().add(changeAmountAfterConversion));
                            toBudget.setRemainingAmount(toBudget.getRemainingAmount().add(changeAmountAfterConversion));
                            budgetDao.update(toBudget);
                        }
                    } else {
                        if (toBudget != null) {
                            toBudget.setTotalAmount(toBudget.getTotalAmount().add(changeAmount));
                            toBudget.setRemainingAmount(toBudget.getRemainingAmount().add(changeAmount));
                            budgetDao.update(toBudget);
                        }
                    }
                    // save transaction for add transfer amount
                    TransactionLog addBudgetTransactionLog = new TransactionLog();
                    if (toBudget != null) {
                        addBudgetTransactionLog.setBudget(toBudget);
                        addBudgetTransactionLog.setReferanceNumber(toBudget.getBudgetId());
                    } else {
                        LOG.warn("Budget details not found ");
                    }
                    addBudgetTransactionLog.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
                    addBudgetTransactionLog.setTransactionTimeStamp(new Date());
                    addBudgetTransactionLog.setAddAmount(changeAmount);
                    if (budget.getConversionRate() != null) {
                        addBudgetTransactionLog.setConversionRateAmount(budget.getConversionRate());
                        addBudgetTransactionLog
                                .setAfterConversionAmount(budget.getConversionRate().multiply(changeAmount));
                    }
                    addBudgetTransactionLog.setRemainingAmount(toBudget.getRemainingAmount());
                    addBudgetTransactionLog.setTransactionLogStatus(TransactionLogStatus.TRANSFER);
                    addBudgetTransactionLog.setFromBusinessUnit(budget.getBusinessUnit());
                    transactionLogService.saveTransactionLog(addBudgetTransactionLog);

                    // save budget for deduct transfer amount
                    budget.setRemainingAmount(budget.getRemainingAmount().subtract(changeAmount));
                    // budget.setTotalAmount(budget.getRevisionAmount());
                    budget.setTransferAmount(budget.getTransferAmount().add(changeAmount));
                    budget.setRevisionAmount(null);
                    budget.setRevisionJustification(null);
                    budget.setConversionRate(null);
                    budget.setToBusinessUnit(null);
                    budget.setToCostCenter(null);
                    budgetDao.update(budget);

                    // save transaction for deduct transfer amount
                    TransactionLog deductBudgetTransactionLog = new TransactionLog();
                    deductBudgetTransactionLog.setBudget(budget);
                    deductBudgetTransactionLog.setReferanceNumber(budget.getBudgetId());
                    deductBudgetTransactionLog.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
                    deductBudgetTransactionLog.setTransactionTimeStamp(new Date());
                    deductBudgetTransactionLog.setDeductAmount(changeAmount);
                    if (budget.getConversionRate() != null) {
                        deductBudgetTransactionLog
                                .setConversionRateAmount(addBudgetTransactionLog.getConversionRateAmount());
                        deductBudgetTransactionLog
                                .setAfterConversionAmount(addBudgetTransactionLog.getAfterConversionAmount());
                    }
                    deductBudgetTransactionLog.setRemainingAmount(budget.getRemainingAmount());
                    deductBudgetTransactionLog.setTransactionLogStatus(TransactionLogStatus.TRANSFER);
                    deductBudgetTransactionLog.setToBusinessUnit(toBudget.getBusinessUnit());
                    deductBudgetTransactionLog.setCostCenter(toBudget.getCostCenter());
                    transactionLogService.saveTransactionLog(deductBudgetTransactionLog);

                }
            }

            // snapShotAuditService.doRftAudit(budget, session, budget, actionBy,
            // AuditActionType.Approve,
            // "event.audit.approved");
            LOG.info("All approvals for this budget is in Approved Mode.");

            try {
                // LOG.info("publishing rft event to epiportal");
                // publishEventService.pushRftEvent(budget.getId(), actionBy.getBuyer().getId(),
                // true);
            } catch (Exception e) {
                LOG.error("Error while publishing RFT event to EPortal:" + e.getMessage(), e);
            }

        } else {
            for (BudgetApproval approval : approvalList) {
                if (approval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
                    approval.setActive(true);
                    for (BudgetApprovalUser nextLevelUser : approval.getApprovalUsers()) {
                        LOG.info("Sending email for next approver level user:" + nextLevelUser.getUser().getLoginId());
                        sendBudgetApprovalRequestEmails(budget, nextLevelUser);
                    }
                    break;
                }
            }
        }
    }

    private void sendBudgetApprovalRequestEmails(Budget budget, ApprovalUser user) {
        LOG.info("UnitName" + budget.getBusinessUnit().getUnitName());
        String mailTo = user.getUser().getCommunicationEmail();
        String subject = "Budget Approval Request";
        String url = APP_URL + "/admin/budgets/viewBudget/" + budget.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", user.getUser().getName());
        map.put("budgetName", budget.getBudgetName());
        map.put("budgetId", budget.getBudgetId());
        map.put("businessUnit", budget.getBusinessUnit().getUnitName());
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(user.getUser().getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");
        if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.BUDGET_APPROVAL_REQUEST_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + user.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("event.approval.request.notification.message",
                new Object[] { "Budget", budget.getBudgetName() }, Global.LOCALE);
        sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

        if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + user.getUser().getCommunicationEmail() + "' and device Id :"
                        + user.getUser().getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", budget.getId());
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(user.getUser().getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Event approve Mobile push notification to '"
                        + user.getUser().getCommunicationEmail() + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + user.getUser().getCommunicationEmail() + "' Device Id is Null");
        }
    }

    public void sentBudgetUtilizedNotifications(Budget budget, BigDecimal percentageUtilized) {

        List<User> budgetCreatorAndApprovals = new ArrayList<User>();
        budgetCreatorAndApprovals.add(budget.getCreatedBy());

        if (CollectionUtil.isNotEmpty(budget.getApprovals())) {
            for (BudgetApproval budgetApproval : budget.getApprovals()) {
                for (BudgetApprovalUser budgetApprovalUser : budgetApproval.getApprovalUsers()) {
                    if (!budgetCreatorAndApprovals.contains(budgetApprovalUser.getUser())) {
                        budgetCreatorAndApprovals.add(budgetApprovalUser.getUser());
                    }
                }
            }
        }
        // send notifications to creator
        LOG.info("Sending budget utilized email to (" + budget.getCreatedBy().getName() + ") : "
                + budget.getCreatedBy().getCommunicationEmail());
        String subject = "Budget Utilization";
        String url = APP_URL + "/admin/budgets/viewBudget/" + budget.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("percentageUtilized", percentageUtilized);
        map.put("budgetName", budget.getBudgetName());
        map.put("budgetId", budget.getBudgetId());
        map.put("businessUnit", StringUtils.checkString(budget.getBusinessUnit().getUnitName()));
        map.put("costCenter", StringUtils.checkString(budget.getCostCenter().getCostCenter()));

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(budget.getCreatedBy().getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");

        String notificationMessage = messageSource.getMessage("budget.utilization.notification.message",
                new Object[] { percentageUtilized }, Global.LOCALE);
        for (User mailTo : budgetCreatorAndApprovals) {
            map.put("userName", mailTo.getName());
            if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
                sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.BUDGET_UTILIZED_TEMPLATE);
            } else {
                LOG.warn("No communication email configured for user : " + budget.getCreatedBy().getLoginId()
                        + "... Not going to send budget utilized email notification");
            }
            sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

            if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
                try {
                    LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                    Map<String, String> payload = new HashMap<String, String>();
                    payload.put("id", budget.getId());
                    payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                    notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                            Arrays.asList(mailTo.getDeviceId()));
                } catch (Exception e) {
                    LOG.error("Error While sending Budget utilzed Mobile push notification to '"
                            + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
                }
            } else {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
            }
        }

    }

    @Override
    public void sendBudgetOverrunNotification(Budget budget) {
        List<User> budgetCreatorAndApprovals = new ArrayList<User>();
        budgetCreatorAndApprovals.add(budget.getCreatedBy());

        if (CollectionUtil.isNotEmpty(budget.getApprovals())) {
            for (BudgetApproval budgetApproval : budget.getApprovals()) {
                for (BudgetApprovalUser budgetApprovalUser : budgetApproval.getApprovalUsers()) {
                    if (!budgetCreatorAndApprovals.contains(budgetApprovalUser.getUser())) {
                        budgetCreatorAndApprovals.add(budgetApprovalUser.getUser());
                    }
                }
            }
        }
        // send notifications to creator
        LOG.info("Sending budget overrun email to (" + budget.getCreatedBy().getName() + ") : "
                + budget.getCreatedBy().getCommunicationEmail());
        String subject = "Budget Overrun";
        String url = APP_URL + "/admin/budgets/viewBudget/" + budget.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("budgetName", budget.getBudgetName());
        map.put("budgetId", budget.getBudgetId());
        map.put("businessUnit", StringUtils.checkString(budget.getBusinessUnit().getUnitName()));
        map.put("costCenter", StringUtils.checkString(budget.getCostCenter().getCostCenter()));

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(budget.getCreatedBy().getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");

        String notificationMessage = messageSource.getMessage("budget.overrun.notification.message", new Object[] {},
                Global.LOCALE);
        for (User mailTo : budgetCreatorAndApprovals) {
            map.put("userName", mailTo.getName());
            if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
                LOG.info("Sending budget overrun email to (" + mailTo.getName() + ") : "
                        + mailTo.getCommunicationEmail());
                sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.BUDGET_OVERRUN_TEMPLATE);
            } else {
                LOG.warn("No communication email configured for user : " + budget.getCreatedBy().getLoginId()
                        + "... Not going to send budget overrun email notification");
            }
            sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

            if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
                try {
                    LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                    Map<String, String> payload = new HashMap<String, String>();
                    payload.put("id", budget.getId());
                    payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                    notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                            Arrays.asList(mailTo.getDeviceId()));
                } catch (Exception e) {
                    LOG.error("Error While sending Budget overrun Mobile push notification to '"
                            + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
                }
            } else {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
            }
        }

    }

    @Override
    public void sendBudgetApprovalReqEmailsOnCreate(Budget budget, BudgetApprovalUser nextLevelUser) {
        sendBudgetApprovalRequestEmails(budget, nextLevelUser);
    }

    @Override
    public void sendPoSupplierActionEmailNotificationToBuyer(Supplier supplier, boolean isAccept, Po po,
                                                             String supplierRemark) {
        LOG.info("Sending supplier po action email to (" + po.getCreatedBy().getName() + ") : "
                + po.getCreatedBy().getCommunicationEmail());
        List<User> userMailList = new ArrayList<User>();
        if (po.getCreatedBy() != null) {
            userMailList.add(po.getCreatedBy());
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(po.getCreatedBy().getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        String subject = "";
        HashMap<String, Object> map = new HashMap<String, Object>();
        if (isAccept) {
            subject = "PO Accepted";
            map.put("message", "Supplier " + supplier.getCompanyName() + " has accepted the PO " + po.getName());
        } else {
            subject = "PO Declined";
            map.put("message", "Supplier " + supplier.getCompanyName() + " has declined the PO " + po.getName());
        }

        map.put("supplierName", supplier.getFullName());
        map.put("supplierLoginEmail", supplier.getLoginEmail());
        map.put("acceptRejectDate", sdf.format(new Date()));
        map.put("date", df.format(new Date()));
        map.put("supplierRemark", StringUtils.checkString(supplierRemark).length() > 0 ? supplierRemark : "N/A");
        map.put("poDate", sdf.format(po.getCreatedDate()));
        map.put("poNumber", po.getPoNumber());
        map.put("isAccept", isAccept ? "Acceptance" : "Declined");
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", APP_URL + "/login");

        if (CollectionUtil.isNotEmpty(userMailList)) {
            for (User mailTo : userMailList) {
                if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
                    map.put("userName", mailTo.getName());
                    sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.PO_ACCEPT_REJECT_TEMPLATE);
                } else {
                    LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                            + "... Not going to send email notification");
                }
            }
        }

    }

    @Override
    public void sendCancelPoEmailNotificationToSupplier(Po po, String poRemarks, User actionBy) {
        LOG.info("Sending buyer cancel po  email to supplier (" + po.getSupplier().getSupplier().getFullName() + ") : "
                + po.getSupplier().getSupplier().getCommunicationEmail());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(actionBy.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        String subject = "PO Cancelled";
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("supplierName", po.getSupplier().getSupplier().getFullName());
        map.put("buyerLoginEmail", actionBy.getLoginId());
        map.put("date", df.format(new Date()));
        map.put("cancellationDate", sdf.format(new Date()));
        map.put("buyerRemark", StringUtils.checkString(poRemarks).length() > 0 ? poRemarks : "N/A");
        map.put("poDate", sdf.format(po.getCreatedDate()));
        map.put("poNumber", po.getPoNumber());
        map.put("buyerCompanyName", po.getBuyer().getCompanyName());
        map.put("businessUnit", po.getBusinessUnit().getUnitName());
        map.put("poName", po.getName());
        map.put("buyerName", actionBy.getName());
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", APP_URL + "/login");
        User user  = userDao.getDetailsOfLoggedinUser(po.getSupplier().getSupplier().getLoginEmail());
        if (StringUtils.checkString(po.getSupplier().getSupplier().getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
            sendEmail(po.getSupplier().getSupplier().getCommunicationEmail(), subject, map, Global.PO_CANCEL_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + po.getSupplier().getSupplier().getLoginEmail()
                    + "... Not going to send email notification");
        }
    }

    @Override
    public void sendPoReceivedEmailNotificationToSupplier(Po po, User actionBy) {

        if (po.getSupplier() != null && po.getSupplier().getSupplier() != null
                && StringUtils.checkString(po.getSupplier().getSupplier().getCommunicationEmail()).length() > 0) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            String timeZone = "GMT+8:00";
            timeZone = getTimeZoneByBuyerSettings(po.getCreatedBy().getTenantId(), timeZone);
            df.setTimeZone(TimeZone.getTimeZone(timeZone));
            sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
            String subject = "PO Received";
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("supplierName",
                    (po.getSupplier() != null ? po.getSupplier().getSupplier().getFullName() : po.getSupplierName()));
            map.put("buyerLoginEmail", actionBy.getLoginId());
            map.put("date", df.format(new Date()));
            map.put("poDate", sdf.format(po.getCreatedDate()));
            map.put("deliveryDate", sdf.format(po.getDeliveryDate()));
            map.put("poNumber", po.getPoNumber());
            map.put("buyerCompanyName", po.getBuyer().getCompanyName());
            map.put("businessUnit", po.getBusinessUnit().getUnitName());
            map.put("poName", po.getName());
            map.put("buyerName", actionBy.getName());
            map.put("loginUrl", APP_URL + "/login");
            map.put("appUrl", APP_URL + "/login");
            LOG.info("Sending po received email to supplier (" + po.getSupplier().getSupplier().getFullName() + ") : "
                    + po.getSupplier().getSupplier().getCommunicationEmail());
            User user = userDao.getDetailsOfLoggedinUser(po.getSupplier().getSupplier().getLoginEmail());
            if(user.getEmailNotifications()) {
                sendEmail(po.getSupplier().getSupplier().getCommunicationEmail(), subject, map,
                        Global.PO_RECEIVED_TEMPLATE);
            }
        } else {
            LOG.warn("No communication email configured for user : " + po.getSupplier().getSupplier().getLoginEmail()
                    + "... Not going to send email notification");
        }
    }

    @Override
    public void sendPoCreatedEmailToCreater(User mailTo, Pr pr, User actionBy) {
        sendPoCreatedEmail(mailTo, pr, actionBy);

    }

    @Override
    public void sharePoToFinance(Po po) {
        sharePrtoFinance(po);
    }

    @Override
    @Transactional(readOnly = false)
    public SupplierFormSubmition doApprovalFormSubmition(SupplierFormSubmition supplierFormSubmition, User actionBy,
                                                         String remarks, boolean approved) throws NotAllowedException {
        LOG.info("Supplier submission Form Id : " + supplierFormSubmition.getId());
        supplierFormSubmition = supplierFormSubmissionDao.findFormById(supplierFormSubmition.getId());
        List<SupplierFormSubmitionApproval> approvalList = supplierFormSubmition.getApprovals();

        // Identify Current Approval Level
        SupplierFormSubmitionApproval currentLevel = null;
        for (SupplierFormSubmitionApproval sourcingFormApproval : approvalList) {
            if (sourcingFormApproval.isActive()) {
                currentLevel = sourcingFormApproval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        SupplierFormSubmitionApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (SupplierFormSubmitionApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject Supplier Form '"
                    + supplierFormSubmition.getName() + "'");
            throw new NotAllowedException("User " + actionBy.getName()
                    + " is not allowed to Approve or Reject Supplier Form '" + supplierFormSubmition.getName() + "'");
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus()
                    + " Supplier Form at : " + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " Supplier Form at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (supplierFormSubmition.getFormComments() == null) {
            supplierFormSubmition.setFormComments(new ArrayList<SupplierFormSubmitionComment>());
        }
        SupplierFormSubmitionComment formComment = new SupplierFormSubmitionComment();
        formComment.setComment(remarks);
        formComment.setApproved(approved);
        formComment.setCreatedBy(actionBy);
        formComment.setCreatedDate(new Date());
        formComment.setSupplierFormSubmition(supplierFormSubmition);
        formComment.setApprovalUserId(actionUser.getId());
        supplierFormSubmition.getFormComments().add(formComment);

        // If rejected
        if (!approved) {

            actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);
            LOG.info("User " + actionBy.getName() + " has Rejected the Supplier Form : "
                    + supplierFormSubmition.getName());
            supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.REJECTED);

            for (SupplierFormSubmitionApproval supplierFormApproval : approvalList) {
                if (Boolean.TRUE == supplierFormApproval.isActive()) {
                    // Send email notification to Approval Active level User
                    for (SupplierFormSubmitionApprovalUser formAppUser : supplierFormApproval.getApprovalUsers()) {
                        if (!supplierFormSubmition.getRequestedBy().getId().equals(formAppUser.getUser().getId())) {
                            sendSupplierFormRejectionEmail(formAppUser.getUser(), supplierFormSubmition, actionBy,
                                    remarks);

                        }
                    }
                }
                supplierFormApproval.setActive(false);
            }
            // Send email notification to Form owner
            if (supplierFormSubmition.getRequestedBy() != null) {
                sendSupplierFormRejectionEmail(supplierFormSubmition.getRequestedBy(), supplierFormSubmition, actionBy,
                        remarks);
            }

        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the Supplier Form : "
                    + supplierFormSubmition.getName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            // Send email notification to Form owner
            if (supplierFormSubmition.getRequestedBy() != null) {
                sendSupplierFormApprovalEmail(supplierFormSubmition.getRequestedBy(), supplierFormSubmition, actionBy,
                        remarks);
            }
            // Send email notification to Approval Action User
            if (!supplierFormSubmition.getRequestedBy().getId().equals(actionBy.getId())) {
                sendSupplierFormApprovalEmail(actionBy, supplierFormSubmition, actionBy, remarks);
            }

            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDoneForSupplierForm(actionBy, approvalList, currentLevel, supplierFormSubmition);
            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (SupplierFormSubmitionApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            LOG.info("All users of this level have not approved the request.");
                            allUsersDone = false;
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the Supplier Form.");
                    setNextOrAllDoneForSupplierForm(actionBy, approvalList, currentLevel, supplierFormSubmition);
                }
            }
        }
        return supplierFormSubmissionDao.update(supplierFormSubmition);

    }

    private void setNextOrAllDoneForSupplierForm(User actionBy, List<SupplierFormSubmitionApproval> approvalList,
                                                 SupplierFormSubmitionApproval currentLevel, SupplierFormSubmition supplierFormSubmition) {
        String buyerTimeZone = "GMT+8:00";
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this form is done!!!. Going to Approved Mode.");
            supplierFormSubmition.setApprovalStatus(SupplierFormApprovalStatus.APPROVED);
        } else {
            for (SupplierFormSubmitionApproval formApproval : approvalList) {
                if (formApproval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + formApproval.getLevel() + " as Active level");
                    formApproval.setActive(true);
                    for (SupplierFormSubmitionApprovalUser nextLevelUser : formApproval.getApprovalUsers()) {
                        buyerTimeZone = getTimeZoneByBuyerSettings(nextLevelUser.getUser().getTenantId(),
                                buyerTimeZone);
                        // send email to Approver
                        sendEmailToSupplierFormApprovers(supplierFormSubmition, nextLevelUser, buyerTimeZone);
                    }
                    break;
                }
            }
        }

    }

    @Override
    public void sendEmailToSupplierFormApprovers(SupplierFormSubmition supplierFormSubmition,
                                                 SupplierFormSubmitionApprovalUser nextLevelUser, String buyerTimeZone) {
        LOG.info("Sending approval email request to approval : " + nextLevelUser.getUser().getCommunicationEmail());
        String mailTo = nextLevelUser.getUser().getCommunicationEmail();
        String subject = "Supplier Form Approval request";
        String url = APP_URL + "/buyer/supplierFormSubView/" + supplierFormSubmition.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", nextLevelUser.getUser().getName());
        map.put("supplierForm", supplierFormSubmition);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        map.put("submitedDate", sdf.format(supplierFormSubmition.getSubmitedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        if (StringUtils.checkString(mailTo).length() > 0 && nextLevelUser.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.SUPP_FORM_APPROVAL_REQUEST_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + nextLevelUser.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("supplierform.approval.request.notification.message",
                new Object[] { supplierFormSubmition.getName() }, Global.LOCALE);
        sendDashboardNotification(nextLevelUser.getUser(), url, subject, notificationMessage,
                NotificationType.APPROVAL_MESSAGE);

    }

    private void sendSupplierFormApprovalEmail(User mailTo, SupplierFormSubmition supplierFormSubmition, User actionBy,
                                               String remarks) {
        LOG.info("Sending approved supplier form email to (" + mailTo.getName() + ") : "
                + mailTo.getCommunicationEmail());
        String url = APP_URL + "/buyer/supplierFormSubView/" + supplierFormSubmition.getId();
        String subject = "Supplier Form Approved";
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("supplierForm", supplierFormSubmition);
        map.put("remarks", StringUtils.checkString(remarks));
        if (mailTo.getId().equals(actionBy.getId())) {
            map.put("message", "You have Approved the following Supplier Form:");
        } else {
            map.put("message", actionBy.getName() + " has Approved the following Supplier Form:");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("submitedDate", df.format(supplierFormSubmition.getSubmitedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.SUPPLIER_FORM_APPROVED_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("supplierform.approval.notification.message",
                new Object[] { actionBy.getName(), supplierFormSubmition.getName(), remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

    }

    private void sendSupplierFormRejectionEmail(User mailTo, SupplierFormSubmition supplierFormSubmition, User actionBy,
                                                String remarks) {
        LOG.info("Sending rejected supplier form email to (" + mailTo.getName() + ") : "
                + mailTo.getCommunicationEmail());
        String url = APP_URL + "/buyer/supplierFormSubView/" + supplierFormSubmition.getId();
        String subject = "Supplier Form Rejected";
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("supplierForm", supplierFormSubmition);
        map.put("remarks", StringUtils.checkString(remarks));
        if (mailTo.getId().equals(actionBy.getId())) {
            map.put("message", "You have Rejected the following Supplier Form:");
        } else {
            map.put("message", actionBy.getName() + " has Rejected the following Supplier Form:");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("submitedDate", df.format(supplierFormSubmition.getSubmitedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.SUPPLIER_FORM_APP_REJECT_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("supplierform.rejection.notification.message",
                new Object[] { actionBy.getName(), supplierFormSubmition.getName(), remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);

    }

    @Override
    @Transactional(readOnly = false)
    public Po doApproval(Po po, User loggedInUser) throws Exception {
        try {
            String buyerTimeZone = "GMT+8:00";

            Buyer buyer = new Buyer();
            buyer.setId(loggedInUser.getTenantId());

            po = poDao.findPoForApprovalById(po.getId());
            List<PoApproval> approvalList = po.getApprovals();

            // If no approval setup - direct approve the PO
            if (CollectionUtil.isEmpty(approvalList)) {

                Boolean isAutoPublishPo = buyerSettingsService
                        .isAutoPublishePoSettingsByTenantId(loggedInUser.getTenantId());
                Boolean revised = po.getRevised();
                if (Boolean.TRUE == isAutoPublishPo) {
                    po.setStatus(PoStatus.ORDERED);
                    po.setOrderedBy(loggedInUser);
                    po.setOrderedDate(new Date());
                    po.setRevisePoDetails(null);
                    po.setRevised(Boolean.FALSE);
                    try {
                        pOSnapshotDocumentDao.deleteDocument(po.getId());
                    } catch (Exception e) {
                    }

                    try {
                        if (Boolean.TRUE == revised && po.getOldStatus() != PoStatus.READY) {
                            PoAudit supplierAudit = new PoAudit();
                            supplierAudit.setAction(PoAuditType.REVISED);
                            supplierAudit.setActionBy(loggedInUser);
                            supplierAudit.setActionDate(new Date());
                            supplierAudit.setBuyer(po.getBuyer());
                            supplierAudit.setSupplier(po.getSupplier().getSupplier());
                            supplierAudit.setDescription("PO " + po.getPoNumber() + " revised.");
                            supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
                            supplierAudit.setPo(po);
                            poAuditService.save(supplierAudit);

                            sendRevisedPoReceivedEmailNotification(po, po.getSupplier().getSupplier(), buyerTimeZone,
                                    loggedInUser);
                        }
                    } catch (Exception e) {
                        LOG.info("Error while sending email to Supplier ... " + e.getMessage(), e);
                    }

                } else {
                    po.setStatus(PoStatus.READY);
                }

                LOG.info("status :" + po.getStatus());

            } else {
                PoApproval currentLevel = null;
                if (po.getStatus() == PoStatus.SUSPENDED || po.getStatus() == PoStatus.REVISE || po.getStatus() == PoStatus.DRAFT) {
                    po.setStatus(PoStatus.PENDING);
                    for (PoApproval poApproval : approvalList) {
                        if (poApproval.getLevel() == 1) {
                            poApproval.setActive(true);
                            break;
                        }
                    }
                } else {
                    for (PoApproval poApproval : approvalList) {
                        if (poApproval.isActive()) {
                            currentLevel = poApproval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (PoApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the PO.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, po,null,null);
                    }
                }
                // Just send emails to users.
                for (PoApproval poApproval : approvalList) {
                    if (poApproval.isActive()) {
                        for (PoApprovalUser user : poApproval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                buyerTimeZone = getTimeZoneByBuyerSettings(user.getUser().getTenantId(), buyerTimeZone);
                                sendEmailToPoApprovers(po, user, buyerTimeZone);

                                if (Boolean.TRUE == po.getEnableApprovalReminder()) {
                                    Integer reminderHr = po.getReminderAfterHour();
                                    Integer reminderCpunt = po.getReminderCount();
                                    if (reminderHr != null && reminderCpunt != null) {
                                        Calendar now = Calendar.getInstance();
                                        now.add(Calendar.HOUR, reminderHr);
                                        user.setNextReminderTime(now.getTime());
                                        user.setReminderCount(reminderCpunt);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            po = poDao.update(po);
        } catch (Exception e) {
            LOG.info("ERROR While Approving PO :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving PO :" + e.getMessage());
        }
        return po;
    }

    @Override
    @Transactional(readOnly = false)
    public Po doPoFinishApprovalFromDraft(Po po, User loggedInUser ) throws Exception {
        try {
            po = poDao.findPoForApprovalById(po.getId());
            List<PoApproval> approvalList = po.getApprovals();

            if (CollectionUtil.isEmpty(approvalList)) {
                handleNoApprovalSetup(po, loggedInUser );
            } else {
                handleApprovalSetup(po, approvalList, loggedInUser );
            }

            createAuditTrailForBuyer(po, loggedInUser, "","",PoAuditType.FINISH,AuditTypes.FINISH);

            return poDao.update(po);

        } catch (Exception e) {
            LOG.error("Error while approving PO: " + e.getMessage(), e);
            throw new Exception("Error while approving PO: " + e.getMessage(), e);
        }
    }

    private void setPoOldStatus(Po po){
        po.setOldStatus(po.getStatus());
    }

    private void handleNoApprovalSetup(Po po, User loggedInUser ) {
        Boolean isAutoPublishPo = buyerSettingsService.isAutoPublishePoSettingsByTenantId(loggedInUser .getTenantId());

        if (Boolean.TRUE.equals(isAutoPublishPo)) {
            finalizePoOrdering(po, loggedInUser );
        } else {
            po.setStatus(PoStatus.READY);
            setPoOldStatus(po);
            createAuditTrailForBuyer(po, loggedInUser, "","",PoAuditType.READY,AuditTypes.READY);
        }

        LOG.info("PO status set to: " + po.getStatus());
    }

    private void createAuditTrailForBuyer(Po po, User actionBy, String buyerAuditMessage,String supplierAuditMessage, PoAuditType poAudit, AuditTypes auditType) {
        try {
            String message = "PO \"" + po.getPoNumber();

            // Construct the message based on the audit type
            switch (auditType) {
                case ORDERED:
                    message += "\" ordered.";
                    break;
                case CANCELLED:
                    message += "\" cancelled.";
                    break;
                case SUSPENDED:
                    message += "\" suspended.";
                    break;
                case FINISH:
                    message += "\" finished.";
                    break;
                case DECLINED:
                    message += "\" declined.";
                    break;
                case READY:
                    message += "\" is ready.";
                    break;
                default:
                    message += "\" action performed.";
                    break;
            }

            // Create and save the PO audit
            PoAudit audit = new PoAudit();
            audit.setAction(poAudit);
            audit.setActionBy(actionBy);
            audit.setActionDate(new Date());
            audit.setBuyer(po.getBuyer());
            audit.setDescription(message);
            audit.setVisibilityType(PoAuditVisibilityType.BUYER);
            audit.setPo(po);

            // Save buyer PO audit
            poAuditService.save(audit);

            // Create and save the buyer audit trail
            BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(
                    auditType,
                    message,
                    actionBy.getTenantId(),
                    actionBy,
                    new Date(),
                    ModuleType.PO
            );
            buyerAuditTrailDao.save(buyerAuditTrail);

        } catch (Exception e) {
            LOG.error("Error while saving audit trail for PO " + po.getPoNumber() + ": " + e.getMessage(), e);
        }
    }

    private void finalizePoOrdering(Po po, User loggedInUser ) {
        po.setStatus(PoStatus.ORDERED);
        setPoOldStatus(po);
        po.setOrderedBy(loggedInUser );
        po.setOrderedDate(new Date());
        po.setRevisePoDetails(null);
        po.setRevised(Boolean.FALSE);
        po.setCancelled(Boolean.FALSE);

        try {
            pOSnapshotDocumentDao.deleteDocument(po.getId());
            //audit trail for buyer
            createAuditTrailForBuyer(po, loggedInUser, "","",PoAuditType.ORDERED,AuditTypes.ORDERED);
            createAuditTrailForSupplierOrdered(po, loggedInUser );
            sendPoReceivedEmailNotificationToSupplier(po, po.getCreatedBy());
        } catch (Exception e) {
            LOG.error("Error while sending email to Supplier: " + e.getMessage(), e);
        }
    }

    private void createAuditTrailForSupplierOrdered(Po po, User loggedInUser ) {
        SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(
                AuditTypes.ORDERED,
                "PO \"" + po.getPoNumber() + "\" received from."+po.getBuyer().getCompanyName(),
                SecurityLibrary.getLoggedInUserTenantId(),
                SecurityLibrary.getLoggedInUser (),
                new Date(),
                ModuleType.PO
        );
        supplierAuditTrailDao.save(supplierAuditTrail);

        PoAudit supplierAudit = new PoAudit();
        supplierAudit.setAction(PoAuditType.ORDERED);
        supplierAudit.setActionBy(loggedInUser );
        supplierAudit.setActionDate(new Date());
        supplierAudit.setBuyer(po.getBuyer());
        supplierAudit.setSupplier(po.getSupplier().getSupplier());
        supplierAudit.setDescription("PO \"" + po.getPoNumber() + "\" received from."+po.getBuyer().getCompanyName());
        supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
        supplierAudit.setPo(po);
        poAuditService.save(supplierAudit);
    }

    private void handleApprovalSetup(Po po, List<PoApproval> approvalList, User loggedInUser ) {
        po.setStatus(PoStatus.PENDING);
        po.setOldStatus(PoStatus.DRAFT);

        for (PoApproval poApproval : approvalList) {
            if (poApproval.getLevel() == 1) {
                poApproval.setActive(true);
                resetApprovalUsers(poApproval);
                sendEmailToFirstApprover(po, poApproval);
                break;
            }
        }
    }

    private void resetApprovalUsers(PoApproval poApproval) {
        for (PoApprovalUser  user : poApproval.getApprovalUsers()) {
            user.setActionDate(null);
            user.setApprovalStatus(ApprovalStatus.PENDING);
            user.setRemarks(null);
        }
    }

    private void sendEmailToFirstApprover(Po po, PoApproval poApproval) {
        for (PoApprovalUser  user : poApproval.getApprovalUsers()) {
            sendEmailToPoApproversFromDraft(po, user, "GMT+8:00");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Po doPoFinishApprovalFromSuspend(Po po, User loggedInUser ) throws Exception {
        try {
            String buyerTimeZone = "GMT+8:00";
            Buyer buyer = new Buyer();
            buyer.setId(loggedInUser .getTenantId());

            // Retrieve the PO for approval
            po = poDao.findPoForApprovalById(po.getId());
            List<PoApproval> approvalList = po.getApprovals();

            // If no approval setup, directly approve the PO
            if (CollectionUtil.isEmpty(approvalList)) {
                handleAutoApproval(po, loggedInUser , buyerTimeZone);
            } else {
                handleApprovalReset(po, approvalList, loggedInUser , buyerTimeZone);
            }

            // Update the PO in the database
            po = poDao.update(po);
        } catch (Exception e) {
            LOG.error("Error while approving revised PO: {}", e.getMessage(), e);
            throw new Exception("Error while approving revised PO: " + e.getMessage(), e);
        }
        return po;
    }

    private void handleAutoApproval(Po po, User loggedInUser , String buyerTimeZone) {
        Boolean isAutoPublishPo = buyerSettingsService.isAutoPublishePoSettingsByTenantId(loggedInUser .getTenantId());
        Boolean revised = po.getRevised();

        if (Boolean.TRUE.equals(isAutoPublishPo)) {
            // Set PO status to ORDERED
            po.setStatus(PoStatus.ORDERED);
            po.setOrderedBy(loggedInUser );
            po.setOrderedDate(new Date());
            po.setRevisePoDetails(null);
            po.setRevised(Boolean.FALSE);

            // Delete associated documents
            try {
                pOSnapshotDocumentDao.deleteDocument(po.getId());
            } catch (Exception e) {
                LOG.warn("Error while deleting document for PO ID {}: {}", po.getId(), e.getMessage());
            }

            // Log audit for revised PO
            if (Boolean.TRUE.equals(revised) && po.getOldStatus() != PoStatus.READY) {
                logPoAudit(po, loggedInUser , buyerTimeZone);
            }
        } else {
            po.setStatus(PoStatus.READY);
        }

        LOG.info("PO status set to: {}", po.getStatus());
    }

    private void logPoAudit(Po po, User loggedInUser , String buyerTimeZone) {
        PoAudit supplierAudit = new PoAudit();
        supplierAudit.setAction(PoAuditType.REVISED);
        supplierAudit.setActionBy(loggedInUser );
        supplierAudit.setActionDate(new Date());
        supplierAudit.setBuyer(po.getBuyer());
        supplierAudit.setSupplier(po.getSupplier().getSupplier());
        supplierAudit.setDescription("PO \"" + po.getPoNumber() + "\" revised");
        supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
        supplierAudit.setPo(po);
        poAuditService.save(supplierAudit);

        // Send email notification
        try {
            sendRevisedPoReceivedEmailNotification(po, po.getSupplier().getSupplier(), buyerTimeZone, loggedInUser );
        } catch (Exception e) {
            LOG.info("Error while sending email to Supplier: {}", e.getMessage(), e);
        }
    }

    private void handleApprovalReset(Po po, List<PoApproval> approvalList, User loggedInUser , String buyerTimeZone) {
        LOG.info("Current PO Status: {}", po.getStatus());
        LOG.info("Approval reset due to suspension.");

        // Reset approval levels
        for (PoApproval poApproval : approvalList) {
            if (poApproval.getLevel() == 1) {
                poApproval.setActive(true);
                break;
            }
        }

        // Send emails to pending approvers
        for (PoApproval poApproval : approvalList) {
            if (poApproval.isActive()) {
                for (PoApprovalUser  user : poApproval.getApprovalUsers()) {
                    if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                        LOG.info("Sending email to pending approver: {}", user.getUser().getCommunicationEmail());
                        buyerTimeZone = getTimeZoneByBuyerSettings(user.getUser ().getTenantId(), buyerTimeZone);
                        sendEmailToPoApproversFromSuspend(po, user, buyerTimeZone);
                        handleApprovalReminder(po, user);
                    }
                }
            }
        }
    }

    private void handleApprovalReminder(Po po, PoApprovalUser  user) {
        if (Boolean.TRUE.equals(po.getEnableApprovalReminder())) {
            Integer reminderHr = po.getReminderAfterHour();
            Integer reminderCount = po.getReminderCount();

            if (reminderHr != null && reminderCount != null) {
                Calendar now = Calendar.getInstance();
                now.add(Calendar.HOUR, reminderHr);
                user.setNextReminderTime(now.getTime());
                user.setReminderCount(reminderCount);
            }
        }
    }
    public void setNextOrAllDone(User actionBy, List<PoApproval> approvalList, PoApproval currentLevel, Po po,PoApprovalUser  actionUser,String action ) throws Exception {
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Mark current level as inactive

        if (currentLevel.getLevel() == approvalList.size()) {
            // All approvals are done
            LOG.info("Action User >>>>>>>>"+actionUser.getApprovalStatus());
            handleFinalApproval(actionBy, po,actionUser,action);
        } else {
            // Set the next approval level as active
            activateNextApprovalLevel(approvalList, currentLevel, po,false);
        }
    }

    private void handleFinalApproval(User actionBy, Po po,PoApprovalUser actionUser,String action) {

        LOG.info("All approvals for this PO are done!!! Transitioning to Approved Mode.");
        Boolean isAutoPublishPo = buyerSettingsService.isAutoPublishePoSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
        if (Boolean.TRUE.equals(isAutoPublishPo)) {
            finalizePoForAutoPublish(po, actionBy);
        } else {
            finalizePoForManualPublish(po,actionUser,action);
        }
    }

    private void updatePoNumberIfNeeded(Po po) {
        IdSettings idSettings = eventIdSettingsDao.getIdSettingsByIdTypeForTenanatId(po.getBuyer().getId(), "PO");
        if (idSettings != null && Boolean.TRUE.equals(idSettings.getEnableSuffix())) {
            String poNumber = po.getPoNumber();
            String[] arr = poNumber.split("-");

            if (arr.length > 1 && StringUtils.checkString(arr[1]).length() > 0) {
                int seq = Integer.parseInt(StringUtils.checkString(arr[1])) + 1;
                poNumber = StringUtils.checkString(arr[0]) + "-" + seq;
                po.setPoNumber(poNumber);
            } else {
                poNumber+= "-1";
                po.setPoNumber(poNumber);
            }
            updateRelatedPrWithPoNumber(po,poNumber);
        } else {
            LOG.info("No Suffix setting for this PO");
        }
    }

    private void updateRelatedPrWithPoNumber(Po po,String poNumber) {
        if (po.getPr() != null) {
            String prId = po.getPr().getId();
            LOG.info("Updating PO Number in PR "+prId);
            Pr pr = prService.findPrById(prId);
            pr.setPoNumber(poNumber);
            prService.updatePr(pr);
        }
    }

    private void finalizePoForAutoPublish(Po po, User actionBy) {
        if (po.getRevised() ){
            createSupplierAuditForRevisedPo(po, actionBy);
        }else if(po.getCancelled()){

        }

        sendPoReceivedEmailNotificationToSupplier(po, po.getCreatedBy());

        try {
            pOSnapshotDocumentDao.deleteDocument(po.getId());
        } catch (Exception e) {
            LOG.error("Error while deleting PO document: " + e.getMessage(), e);
        }

        po.setStatus(PoStatus.ORDERED);
        po.setOldStatus(po.getStatus());
        po.setOrderedBy(actionBy);
        po.setOrderedDate(new Date());
        po.setRevisePoDetails(null);
        po.setRevised(false);
        po.setCancelled(false);
    }

    private void createSupplierAuditForRevisedPo(Po po, User actionBy) {
        try {
            PoAudit supplierAudit = new PoAudit();
            supplierAudit.setAction(PoAuditType.REVISED);
            supplierAudit.setActionBy(actionBy);
            supplierAudit.setActionDate(new Date());
            supplierAudit.setBuyer(po.getBuyer());
            supplierAudit.setSupplier(po.getSupplier().getSupplier());
            supplierAudit.setDescription("PO " + po.getPoNumber() + " revised.");
            supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
            supplierAudit.setPo(po);
            poAuditService.save(supplierAudit);

            sendRevisedPoReceivedEmailNotification(po, po.getSupplier().getSupplier(), "GMT+8:00", actionBy);
        } catch (Exception e) {
            LOG.error("Error while sending email to Supplier: " + e.getMessage(), e);
        }
    }

    private void finalizePoForManualPublish(Po po, PoApprovalUser  actionUser,String action ) {
        if (po == null || actionUser  == null) {
            throw new IllegalArgumentException("PO and ActionUser  cannot be null");
        }

        ApprovalStatus approvalStatus = actionUser.getApprovalStatus();
        LOG.info("finalizePoForManualPublish >>>>>>>>>>>>>>>>>");
        LOG.info("isRevied"+po.getRevised());
        LOG.info("isCancelled"+po.getCancelled());
        LOG.info("currentStatus"+po.getStatus());

        if (!po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {
            LOG.info("!po.getRevised() && !po.getCancelled()");
            handleDraftPo(po, approvalStatus,action);
        } else if (po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {
            LOG.info("po.getRevised() && !po.getCancelled()");
            handleRevisedPo(po, actionUser , approvalStatus,action);
        } else if (!po.getRevised() && po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {//cancellation on suspend
            LOG.info("!po.getRevised() && po.getCancelled()");
            handleCancelledPo(po, actionUser,action );
        }
    }

    private void handleDraftPo(Po po, ApprovalStatus approvalStatus,String action) {
        if (action.equals("REJECTED")) {
            po.setStatus(PoStatus.DRAFT);
        } else if (action.equals("APPROVED")) {
            po.setStatus(PoStatus.READY);
        }
        po.setCancelled(false);
        po.setRevised(false);
    }

    private void handleRevisedPo(Po po, PoApprovalUser  actionUser , ApprovalStatus approvalStatus,String action) {
        if (action.equals("REJECTED")) {
            handleRejectionForRevisedPo(po,actionUser);
        } else if (action.equals("APPROVED")) {
            if (po.getRevised()) {
                updatePoNumberIfNeeded(po);
            }
            handleApprovalForRevisedPo(po, actionUser );
        }

        po.setOldStatus(po.getStatus());
    }

    private void handleRejectionForRevisedPo(Po po,PoApprovalUser actionUser) {
        LOG.info("Reject revision need back to suspend");
        LOG.info("isRevised >>>>> "+po.getRevised());

        if(po.getRevised()){
            po.setStatus(PoStatus.SUSPENDED);
        }else{
            po.setStatus(PoStatus.DRAFT);
        }

        po.setOldStatus(po.getStatus());

        String info = remarkAction!="" || remarkAction.isEmpty()?" Remarks: "+remarkAction:"";
        createAuditTrailForApproval(po, actionUser.getUser(), po.getPoNumber() + " revision rejected."+info, true);
    }

    private void handleApprovalForRevisedPo(Po po, PoApprovalUser  actionUser ) {
        Boolean isAutoPublishPo = buyerSettingsService.isAutoPublishePoSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
        if (Boolean.TRUE.equals(isAutoPublishPo)) {
            finalizePoForAutoPublish(po, actionUser.getUser());
        } else {
            po.setStatus(PoStatus.READY);
            po.setOldStatus(po.getStatus());
        }

    }

    private void handleCancelledPo(Po po, PoApprovalUser  actionUser,String action ) {

        LOG.info("handleCancelledPo >>>>>");
        LOG.info("isRevised : "+po.getRevised());
        LOG.info("isCancelled : "+po.getCancelled());
        LOG.info("action : "+action);


        if (action.equals("APPROVED")) {
            po.setStatus(PoStatus.CANCELLED);
            createAuditTrailForApproval(po, actionUser.getUser(), po.getPoNumber() + " cancelled.", true);
            sendCancelPoEmailNotificationToSupplier(po, actionUser .getRemarks(), actionUser.getUser());
        }else if(action.equals("REJECTED")) {
            LOG.info("PO CANCELLATION REJECTED WHEN SUSPENDED");
            po.setStatus(PoStatus.SUSPENDED);
            resetPoForEditing(po,po.getApprovals());
            //no need audit trail , it has been post in main function
        }
        po.setOldStatus(po.getStatus());
    }

    private User getSupplierEmail(Po po){
        if (po.getSupplier() != null) {
            User supUser = new User();
            supUser.setCommunicationEmail(po.getSupplier().getSupplier().getCommunicationEmail());
            User u = userService.getDetailsOfLoggedinUser(po.getSupplier().getSupplier().getLoginEmail());
            supUser.setEmailNotifications(u != null ? u.getEmailNotifications() : Boolean.TRUE);
            supUser.setName(po.getSupplier().getSupplier().getCompanyName());
            supUser.setTenantId(po.getSupplier().getSupplier().getId());
            supUser.setTenantType(TenantType.SUPPLIER);
            return supUser;
        }else{
            return new User();
        }

    }

    private void activateNextApprovalLevel(List<PoApproval> approvalList, PoApproval currentLevel, Po po, Boolean isReset) {
        if (isReset) {
            // Activate the first approval level if isReset is true
            if (!approvalList.isEmpty()) {
                activateApprovalLevel(approvalList.get(0), po); // Activate the first approval level
            }
        } else {
            for (PoApproval poApproval : approvalList) {
                if (poApproval.getLevel() == currentLevel.getLevel() + 1) {
                    activateApprovalLevel(poApproval, po);
                    break; // Exit once the next level is activated
                }
            }
        }
    }

    private void activateApprovalLevel(PoApproval poApproval, Po po) {
        LOG.info("Setting Approval level " + poApproval.getLevel() + " as Active level");
        poApproval.setActive(true);

        for (PoApprovalUser  nextLevelUser  : poApproval.getApprovalUsers()) {
            String buyerTimeZone = getTimeZoneByBuyerSettings(nextLevelUser.getUser().getTenantId(), "GMT+8:00");
            sendEmailToPoApprovers(po, nextLevelUser , buyerTimeZone);

            if (Boolean.TRUE.equals(po.getEnableApprovalReminder())) {
                setApprovalReminder(nextLevelUser , po);
            }
        }
    }

    private void setApprovalReminder(PoApprovalUser  nextLevelUser , Po po) {
        Integer reminderHr = po.getReminderAfterHour();
        Integer reminderCount = po.getReminderCount();

        if (reminderHr != null && reminderCount != null) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR, reminderHr);
            nextLevelUser .setNextReminderTime(now.getTime());
            nextLevelUser .setReminderCount(reminderCount);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Po doApproval(Po po, User actionBy, String remarks, boolean approved) throws Exception {
        // Fetch the PO for approval
        remarkAction=remarks;
        po = poDao.findPoForApprovalById(po.getId());
        List<PoApproval> approvalList = po.getApprovals();

        LOG.info("is Revised >>>> "+po.getRevised());
        LOG.info("is Cancelled >>>> "+po.getCancelled());
        LOG.info("Current Status >>>> "+po.getStatus());
        LOG.info("Old Status >>>> "+po.getOldStatus());

        // Identify Current Approval Level
        PoApproval currentLevel = findCurrentApprovalLevel(approvalList);
        PoApprovalUser  actionUser  = findActionUser(currentLevel, actionBy);

        // Validate action user
        validateActionUser (actionUser , actionBy, po, currentLevel);

        // Add remarks to comments
        addRemarksToPo(po, remarks, approved, actionBy, actionUser );

        // Process approval or rejection
        if (approved) {
            processApproval(po, actionBy, remarks, currentLevel, approvalList, actionUser );
        } else {
            processRejection(po, actionBy, remarks,currentLevel, approvalList, actionUser );
        }

        //move old status to current po status
        po.setOldStatus(po.getStatus());
        //now update
        return poDao.update(po);
    }

    private PoApproval findCurrentApprovalLevel(List<PoApproval> approvalList) {
        for (PoApproval poApproval : approvalList) {
            if (poApproval.isActive()) {
                LOG.info("Current Approval Level : " + poApproval.getLevel());
                return poApproval;
            }
        }
        return null;
    }

    private PoApprovalUser  findActionUser (PoApproval currentLevel, User actionBy) {
        if (currentLevel != null) {
            for (PoApprovalUser  user : currentLevel.getApprovalUsers()) {
                if (user.getUser ().getId().equals(actionBy.getId())) {
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                    return user;
                }
            }
        }
        return null;
    }

    private void validateActionUser (PoApprovalUser  actionUser , User actionBy, Po po, PoApproval currentLevel) throws NotAllowedException {
        if (actionUser  == null) {
            LOG.error("User  " + actionBy.getName() + " is not allowed to Approve or Reject PO '" + po.getName() +
                    "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User  " + actionBy.getName() + " is not allowed to Approve or Reject PO '" +
                    po.getName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            LOG.error("User  " + actionBy.getName() + " has already " + actionUser .getApprovalStatus() + " PO at : " +
                    actionUser .getActionDate());
            throw new NotAllowedException("User  " + actionBy.getName() + " has already " +
                    actionUser .getApprovalStatus() + " PO at : " + actionUser .getActionDate());
        }
    }

    private void addRemarksToPo(Po po, String remarks, boolean approved, User actionBy, PoApprovalUser  actionUser ) {
        if (po.getComments() == null) {
            po.setComments(new ArrayList<>());
        }

        PoComment poComment = new PoComment();
        poComment.setComment(remarks);
        poComment.setApproved(approved);
        poComment.setCreatedBy(actionBy);
        poComment.setCreatedDate(new Date());
        poComment.setPo(po);
        poComment.setApprovalUserId(actionUser .getId());
        po.getComments().add(poComment);
        actionUser .setActionDate(new Date());
        actionUser .setRemarks(remarks);
    }

    private void processRejection(Po po, User actionBy, String remarks, PoApproval currentLevel, List<PoApproval> approvalList, PoApprovalUser  actionUser ) throws Exception {
        LOG.info("User  " + actionBy.getName() + " has Rejection process the PO: " + po.getName());

        String action = ApprovalStatus.REJECTED.toString();
        // Create audit trail for the rejection
        createAuditTrailForRejection(po, actionBy, remarks);

        // Send rejection email to the creator of the PO
        sendPoRejectionEmail(po.getCreatedBy(), po, actionBy, remarks);

        actionUser.setApprovalStatus(ApprovalStatus.REJECTED);

        resetPoForEditing(po,approvalList);


        // Handle rejection based on the current PO state
        if (!po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) { //draft
            handleDraftRejection(po, actionUser , currentLevel,approvalList,action);
        }else{
            handleApprovalRejection(currentLevel, actionBy, approvalList, po, actionUser,action );
        }
    }

    private void resetPoForEditing(Po po,List<PoApproval> approvalList){
        //po.setRevised(false);
        po.setCancelled(false);
        resetApprovalStatus(po, approvalList);
    }

    private void handleDraftRejection(Po po, PoApprovalUser  actionUser ,PoApproval currentLevel, List<PoApproval> approvalList,String action) {
        po.setStatus(PoStatus.CANCELLED);
        po.setOldStatus(PoStatus.DRAFT);
        resetApprovalStatus(po, approvalList);
    }

    private void handleApprovalRejection(PoApproval currentLevel, User actionBy, List<PoApproval> approvalList, Po po, PoApprovalUser  actionUser,String action ) throws Exception {

        // Check if there are still pending approvals
        if (ApprovalType.OR.equals(currentLevel.getApprovalType())) {
            LOG.info("This level has OR set for approval. Marking level as done");
            setNextOrAllDone(actionBy, approvalList, currentLevel, po, actionUser,action );
        } else {
            // AND Operation
            LOG.info("This level has AND set for approvals");
            if (areAllUsersApproved(currentLevel)) {
                LOG.info("All users of this level have approved the PO.");
                setNextOrAllDone(actionBy, approvalList, currentLevel, po, actionUser,action );
            }
        }
    }

    private void resetApprovalStatus(Po po,List<PoApproval> approvalList) {
        for (PoApproval poApproval : approvalList) {
            poApproval.setDone(false);
            poApproval.setActive(false);
            for (PoApprovalUser  user : poApproval.getApprovalUsers()) {
                user.setActionDate(null);
                user.setApprovalStatus(ApprovalStatus.PENDING);
                user.setRemarks(null);
            }
        }
    }

    private void createAuditTrailForRejection(Po po, User actionBy, String remarks) {
        try {
            LOG.info("--------------------------PO REJECTION AUDIT---------------------------------------");

            PoAudit audit = new PoAudit();
            audit.setAction(PoAuditType.REJECT);
            audit.setActionBy(actionBy);
            audit.setActionDate(new Date());
            audit.setBuyer(po.getBuyer());
            audit.setVisibilityType(PoAuditVisibilityType.BUYER);
            audit.setPo(po);

            String message=null;
            // Check conditions for rejection
            if (!po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {
                message="PO \"" + po.getPoNumber() + "\" rejected. Remarks: " + remarks;
            } else if (po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {
                message="PO \"" + po.getPoNumber() + "\" revision rejected. Remarks: " + remarks;
            } else if (!po.getRevised() && po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {
                message="PO \"" + po.getPoNumber() + "\" cancellation rejected. Remarks: " + remarks;
            }

            //Save PO Audit
            audit.setDescription(message);
            poAuditService.save(audit);

            // Create buyer audit trail
            BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED,
                    message, actionBy.getTenantId(), actionBy, new Date(),
                    ModuleType.PO);
            buyerAuditTrailDao.save(buyerAuditTrail);
        } catch (Exception e) {
            LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
        }
    }

    private void processApproval(Po po, User actionBy, String remarks, PoApproval currentLevel, List<PoApproval> approvalList, PoApprovalUser  actionUser ) throws Exception {
        LOG.info("User  " + actionBy.getName() + " has Approved the PO : " + po.getName());
        actionUser.setApprovalStatus(ApprovalStatus.APPROVED);

        createAuditTrailForApproval(po, actionBy, remarks,false);

        // Send email notification to Creator and associate owner
        sendPoApprovalEmail(po.getCreatedBy(), po, actionBy, remarks);

        String action = ApprovalStatus.APPROVED.toString();

        handleApprovalRejection(currentLevel, actionBy, approvalList, po, actionUser,action );
    }

    private void createAuditTrailForApproval(Po po, User actionBy, String remarks,boolean isFinal) {
        try {
            LOG.info("--------------------------PO APPROVAL AUDIT---------------------------------------");
            // Check conditions for approval
            LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>getRevised: "+po.getRevised());
            LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>getCancelled: "+po.getCancelled());
            LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>poStatus: "+po.getStatus());

            PoAuditType poAuditType = PoAuditType.APPROVE;
            AuditTypes auditType = AuditTypes.APPROVED;

            PoAudit audit = new PoAudit();
            audit.setAction(poAuditType);
            audit.setActionBy(actionBy);
            audit.setActionDate(new Date());
            audit.setBuyer(po.getBuyer());
            audit.setVisibilityType(PoAuditVisibilityType.BUYER);
            audit.setPo(po);
            String message=null;

            //approval from DRAFT
            if (!po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {
                message ="PO \"" + po.getPoNumber() + "\" approved. Remarks: " + remarks;

            //approval from SUSPENDED
            } else if (po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {
                if(!isFinal){
                    message="PO \"" + po.getPoNumber() + "\" revision approved. Remarks: " + remarks;
                }else{
                    message="PO \"" + po.getPoNumber() + "\" revised.";
                }
            //approval from CANCELLED
            } else if (!po.getRevised() && po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())) {
                if(!isFinal){
                    message="PO \"" + po.getPoNumber() + "\" cancellation approved. Remarks: " + remarks;
                }else{
                    message="PO \"" + po.getPoNumber() + "\" cancelled.Remarks: " + remarks;
                }

            }

            if(message!=null){
                // Save buyer po audit
                audit.setDescription(message);
                poAuditService.save(audit);

                // Create buyer audit trail
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(auditType,
                        message, actionBy.getTenantId(), actionBy, new Date(),
                        ModuleType.PO);
                buyerAuditTrailDao.save(buyerAuditTrail);
            }
        } catch (Exception e) {
            LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
        }
    }

    private boolean areAllUsersApproved(PoApproval currentLevel) {
        for (PoApprovalUser  user : currentLevel.getApprovalUsers()) {
            if (ApprovalStatus.PENDING == user.getApprovalStatus() || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                LOG.info("All users of this level have not approved the PO.");
                return false;
            }
        }
        return true;
    }

    /**
     * @param po
     * @param user
     */
    public void sendEmailToPoApprovers(Po po, PoApprovalUser user, String buyerTimeZone) {

        String mailTo = user.getUser().getCommunicationEmail();
        String subject = "PO Approval Request";

        String prKey = "";
        if(po.getPr()!=null){
            prKey = po.getPr().getId();
        }
        String url = APP_URL + "/buyer/poView/" + po.getId()+"?prId="+prKey;

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", user.getUser().getName());
        map.put("po", po);
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        map.put("poDate", df.format(po.getCreatedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        map.put("justification", StringUtils.checkString(po.getReviseJustification()));
        if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.PO_APPROVAL_REQUEST_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + user.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("po.approval.request.notification.message",
                new Object[] { po.getName() }, Global.LOCALE);
        sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
    }

    /**
     * @param po
     * @param user
     */
    public void sendEmailToPoApproversFromDraft(Po po, PoApprovalUser user, String buyerTimeZone) {
        LOG.info(">>>>>>>>>>>>>>> Email Request Approval From Draft ");
        String prKey = "";
        String poId = po.getId();
        if(po.getPr()!=null){
            prKey = po.getPr().getId();
        }
        List<PoTeamMember> teamMember = poDao.findAssociateOwnerOfPo(poId, TeamMemberType.Associate_Owner);
        String mailTo = user.getUser().getCommunicationEmail();
        String subject = "PO Approval Request";
        String url = APP_URL + "/buyer/poView/" + poId+"?prId="+prKey;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", user.getUser().getName());
        map.put("po", po);
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        map.put("poDate", df.format(po.getCreatedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        map.put("justification", StringUtils.checkString(po.getReviseJustification()));
        if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.PO_APPROVAL_REQUEST_TEMPLATE);

        } else {
            LOG.warn("No communication email configured for user : " + user.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("revised.po.approval.request.notification.message",
                new Object[] { po.getName() }, Global.LOCALE);
        sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
    }

    /**
     * @param po
     * @param user
     */
    public void sendEmailToPoApproversFromSuspend(Po po, PoApprovalUser user, String buyerTimeZone) {
        LOG.info(">>>>>>>>>>>>>>> Email Request Approval From Suspend ");

        String prKey = "";
        String poId = po.getId();

        if(po.getPr()!=null){
            prKey = po.getPr().getId();
        }

        List<PoTeamMember> teamMember = poDao.findAssociateOwnerOfPo(poId, TeamMemberType.Associate_Owner);
        String mailTo = user.getUser().getCommunicationEmail();
        String subject = !po.getCancelled()?"Revise PO Approval Request":"Cancel PO Approval Request";
        LOG.info("Subject Email : "+subject );
        String url = APP_URL + "/buyer/poView/" + po.getId()+"?prId="+prKey;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("message", !po.getCancelled()?"Your attention is required for Approval of following PO":"Your attention is required for Approval of following PO cancellation");
        map.put("userName", user.getUser().getName());
        map.put("po", po);
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        map.put("poDate", df.format(po.getCreatedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        map.put("justification", StringUtils.checkString(po.getReviseJustification()));

        if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, !po.getCancelled()?Global.PO_REVISE_APPROVAL_REQUEST_TEMPLATE:Global.PO_CANCEL_APPROVAL_REQUEST_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + user.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("revised.po.approval.request.notification.message",
                new Object[] { po.getName() }, Global.LOCALE);
        sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
    }

    private String getBusinessUnitnameByPoId(String poId) {
        String displayName = null;
        displayName = poDao.getBusineessUnitnameByPoId(poId);
        return StringUtils.checkString(displayName);
    }

    /**
     * @param po
     * @param actionBy
     * @param remarks
     */
    private void sendPoRejectionEmail(User mailTo, Po po, User actionBy, String remarks) {

        String prKey = "";
        String poId = po.getId();
        if(po.getPr()!=null){
            prKey = po.getPr().getId();
        }
        List<PoTeamMember> teamMember = poDao.findAssociateOwnerOfPo(poId, TeamMemberType.Associate_Owner);
        User creator = po.getCreatedBy();
        String poCreator = creator.getCommunicationEmail();
        LOG.info("Sending Po Rejection request email to (" + po.getCreatedBy().getName() + ") : " + poCreator);

        String url = APP_URL + "/buyer/poView/" + poId+"?prId="+prKey;

        //PH-4113
        String subject= null;
        String message=null;

        //rejection from DRAFT / FIRST APPROVAL
        if(!po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())){
            subject ="PO Rejected";
            message = actionBy.getName()+" has rejected the following PO";
        }

        //rejection from REVISION
        if(po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())){
            subject ="PO Rejected";
            message = actionBy.getName()+" has rejected the following PO";
        }

        if(po.getRevised() && !po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())){
            subject="Revised PO Rejected";
            message = actionBy.getName()+" has rejected the following PO revision";
        }

        if(!po.getRevised() && po.getCancelled() && PoStatus.PENDING.equals(po.getStatus())){
            subject="Cancel PO Rejected";
            message = actionBy.getName()+" has rejected the following PO cancellation";
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", po.getCreatedBy().getName());
        map.put("actionByName", actionBy.getName());
        map.put("po", po);
        map.put("remarks", StringUtils.checkString(remarks));
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
        map.put("message", message);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("poDate", df.format(po.getCreatedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            if (!creator.getId().equals(actionBy.getId())){
                sendEmail(poCreator, subject, map, Global.PO_REJECTED_TEMPLATE);
            }

            //send to Owner not loggedIn user
            if (CollectionUtil.isNotEmpty(teamMember)) {
                for (PoTeamMember assOwn : teamMember) {
                    if (!assOwn.getUser().getId().equals(actionBy.getId())){ //do not send email to itself as owner
                        if (StringUtils.checkString(assOwn.getUser().getCommunicationEmail()).length() > 0 && assOwn.getUser().getEmailNotifications()) {
                            LOG.info("sending email to Other PO associate owner login id :" + assOwn.getUser().getLoginId());
                            map.put("eventOwner", assOwn.getUser().getName());
                            Log.info("Sending email to PO Owner >>>>>>>>>>>> "+assOwn.getUser().getCommunicationEmail());
                            sendEmail(assOwn.getUser().getCommunicationEmail(), subject, map, Global.PO_REJECTED_TO_OWNER_TEMPLATE);
                        }
                    }
                }
            }

        } else {
            LOG.warn("No communication email configured for teammember : ");
        }

        String notificationMessage = messageSource.getMessage("revise.po.rejection.notification.message",
                new Object[] { actionBy.getName(), po.getName(), remarks }, Global.LOCALE);
        sendDashboardNotification(po.getCreatedBy(), url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);
    }

    /**
     * @param po
     * @param actionBy
     * @param remarks
     */
    private void sendPoApprovalEmail(User mailTo, Po po, User actionBy, String remarks) {

        String prKey = "";
        String poId = po.getId();

        if(po.getPr()!=null){
            prKey = po.getPr().getId();
        }
        List<PoTeamMember> teamMember = poDao.findAssociateOwnerOfPo(poId, TeamMemberType.Associate_Owner);
        String poCreator = po.getCreatedBy().getCommunicationEmail();
        LOG.info("Sending Po Approval Notification  to (" + po.getCreatedBy().getName() + ") : " + poCreator);

        //PH-4113
        String subject= null;
        String message=null;

        if(!po.getRevised() && !po.getCancelled()){
            subject ="PO Approved";
            message = actionBy.getName()+" has approved the following PO";
        }

        if(po.getRevised() && !po.getCancelled()){
            subject="Revised PO Approved";
            message = actionBy.getName()+" has approved the following PO revision";
        }

        if(!po.getRevised() && po.getCancelled()){
            subject="Cancel PO Approved";
            message = actionBy.getName()+" has approved the cancellation of the following PO";
        }

        LOG.info(">>>>>>>>> Notification Email Approval");
        LOG.info(">>>>>>>>> Email Subject :"+subject);
        LOG.info(">>>>>>>>> Email Message :"+message);

        String url = APP_URL + "/buyer/poView/" + poId+"?prId="+prKey;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("po", po);
        map.put("remarks", StringUtils.checkString(remarks));
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
        map.put("message", message);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("poDate", df.format(po.getCreatedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(poCreator, subject, map, Global.PO_APPROVED_TEMPLATE);
            //send to Owner not loggedIn user
            if (CollectionUtil.isNotEmpty(teamMember)) {
                for (PoTeamMember assOwn : teamMember) {
                    if (!assOwn.getUser().getId().equals(actionBy.getId())){ //do not send email to itself as owner
                        if (StringUtils.checkString(assOwn.getUser().getCommunicationEmail()).length() > 0 && assOwn.getUser().getEmailNotifications()) {
                            LOG.info("sending email to Other PO associate owner for email approval notification :" + assOwn.getUser().getLoginId());
                            map.put("eventOwner", assOwn.getUser().getName());
                            Log.info("Sending email to PO Owner >>>>>>>>>>>> "+assOwn.getUser().getCommunicationEmail());
                            sendEmail(assOwn.getUser().getCommunicationEmail(), subject, map, Global.PO_APPROVED_TO_OWNER_TEMPLATE);
                        }
                    }
                }
            }
        } else {
            LOG.warn("No communication email configured for team member : " );
        }
    }

    @Override
    public void sendRevisedPoReceivedEmailNotification(Po po, Supplier mailTo, String buyerTimeZone, User actionBy) {
        LOG.info("Sending revised email to (" + mailTo.getCompanyName() + ") : " + mailTo.getCommunicationEmail());
        String subject = "Revised PO Received";
        String url = APP_URL + "/supplier/supplierPrView/" + po.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getCompanyName());
        map.put("actionByName", actionBy.getName());
        map.put("po", po);
        map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(actionBy.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("poDate", df.format(po.getCreatedDate()));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        User user = userDao.getDetailsOfLoggedinUser(mailTo.getLoginEmail());
        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && user.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.REVISE_PO_RECEIVED_TEMPLATE);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginEmail()
                    + "... Not going to send email notification");
        }

    }

    @Override
    @Transactional(readOnly = false)
    public SupplierPerformanceEvaluatorUser doApproval(SupplierPerformanceEvaluatorUser evaluatorUser,
                                                       HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer) throws Exception {
        try {
            evaluatorUser = supplierPerformanceEvaluatorUserService.getEvaluatorUserById(evaluatorUser.getId());
            List<PerformanceEvaluationApproval> approvalList = evaluatorUser.getEvaluationApprovals();
            if (CollectionUtil.isEmpty(approvalList)) {
                evaluatorUser.setEvaluationStatus(SupperPerformanceEvaluatorStatus.APPROVED);
                evaluatorUser.setApprovedDate(new Date());
                LOG.info("status :" + evaluatorUser.getEvaluationStatus());

                byte[] summarySnapshot = null;
                try {
                    // Owner
                    SupplierPerformanceAudit audit = new SupplierPerformanceAudit(evaluatorUser.getForm(), null,
                            loggedInUser, new java.util.Date(), SupplierPerformanceAuditActionType.Approve,
                            " Supplier Performance Evaluation Form \"" + evaluatorUser.getForm().getFormId()
                                    + "\" Approved for Evaluator " + evaluatorUser.getEvaluator().getName() + ".",
                            Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
                    supplierPerformanceAuditService.save(audit);

                    JasperPrint formSummary = supplierPerformanceEvaluationService.getPerformancEvaluationSummaryPdf(
                            evaluatorUser.getForm(), loggedInUser,
                            (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
                    summarySnapshot = JasperExportManager.exportReportToPdf(formSummary);
                    audit = new SupplierPerformanceAudit(evaluatorUser.getForm(), evaluatorUser, loggedInUser,
                            new java.util.Date(), SupplierPerformanceAuditActionType.Approve,
                            messageSource.getMessage("spf.audit.approved",
                                    new Object[] { evaluatorUser.getForm().getFormId() }, Global.LOCALE),
                            summarySnapshot, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE,
                            Boolean.TRUE);
                    supplierPerformanceAuditService.save(audit);
                } catch (Exception e) {
                    LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
                }

                try {
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "Supplier Performance Evaluation Form \"" + evaluatorUser.getForm().getFormId()
                                    + "\" Approved for Evaluator " + evaluatorUser.getEvaluator().getName(),
                            loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.PerformanceEvaluation);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                } catch (Exception e) {
                    LOG.error("Error while recording Audit " + e.getMessage(), e);
                }

            } else {
                if (evaluatorUser.getEvaluationStatus() == SupperPerformanceEvaluatorStatus.DRAFT) {
                    evaluatorUser.setEvaluationStatus(SupperPerformanceEvaluatorStatus.PENDING);
                    for (PerformanceEvaluationApproval approval : approvalList) {
                        if (approval.getLevel() == 1) {
                            approval.setActive(true);
                            break;
                        }
                    }
                } else {
                    PerformanceEvaluationApproval currentLevel = null;
                    for (PerformanceEvaluationApproval approval : approvalList) {
                        if (approval.isActive()) {
                            currentLevel = approval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (PerformanceEvaluationApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved for Evaluation.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, evaluatorUser, session,
                                loggedInUser);
                    }
                }
                // Just send emails to users.
                for (PerformanceEvaluationApproval approval : approvalList) {
                    if (approval.isActive()) {
                        for (PerformanceEvaluationApprovalUser user : approval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                sendEmailToEvaluatorUserPendingApprovers(evaluatorUser, evaluatorUser.getForm(), user);
                            }
                        }
                    }
                }
            }
            evaluatorUser = supplierPerformanceEvaluatorUserDao.update(evaluatorUser);
        } catch (Exception e) {
            LOG.info("ERROR While Approving Evaluation :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving Evaluation :" + e.getMessage());
        }
        return evaluatorUser;
    }

    private void setNextOrAllDone(User actionBy, List<PerformanceEvaluationApproval> approvalList,
                                  PerformanceEvaluationApproval currentLevel, SupplierPerformanceEvaluatorUser evaluatorUser,
                                  HttpSession session, User loggedInUser) {
        currentLevel.setDone(true);
        currentLevel.setActive(false);
        // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this Evaluator User is done!!!. Going to Approved Mode.");
            evaluatorUser.setEvaluationStatus(SupperPerformanceEvaluatorStatus.APPROVED);
            evaluatorUser.setApprovedDate(new Date());

            LOG.info("All approvals for this Evaluator User is in Approved Mode.");

        } else {
            for (PerformanceEvaluationApproval approval : approvalList) {
                if (approval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
                    approval.setActive(true);
                    for (PerformanceEvaluationApprovalUser nextLevelUser : approval.getApprovalUsers()) {
                        sendEmailToEvaluatorUserPendingApprovers(evaluatorUser, evaluatorUser.getForm(), nextLevelUser);
                    }
                    break;
                }
            }
        }
    }

    private void sendEmailToEvaluatorUserPendingApprovers(SupplierPerformanceEvaluatorUser evaluatorUser,
                                                          SupplierPerformanceForm form, PerformanceEvaluationApprovalUser user) {
        String buyerTimeZone = "GMT+8:00";
        String mailTo = user.getUser().getCommunicationEmail();
        String subject = " Supplier Performance Evaluation Approval Request";
        String url = APP_URL + "/buyer/spfEvaluatorApprovalSummary/" + evaluatorUser.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", user.getUser().getName());
        map.put("formId", form.getFormId());
        map.put("referenceName", form.getReferenceName());
        map.put("supplier", form.getAwardedSupplier().getCompanyName());
        map.put("referenceNumber", (form.getReferenceNumber() == null ? "" : form.getReferenceNumber()));
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.EVALUATION_FORM_APPROVAL_REQUEST);
        } else {
            LOG.warn("No communication email configured for user : " + user.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("pr.approval.request.notification.message",
                new Object[] { form.getFormName() }, Global.LOCALE);
        sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
        if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo + "' and device Id :" + user.getUser().getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", form.getFormId());
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                payload.put("eventType", FilterTypes.PR.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(user.getUser().getDeviceId()));
            } catch (Exception e) {
                LOG.error(
                        "Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(),
                        e);
            }
        } else {
            LOG.info("User '" + mailTo + "' Device Id is Null");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public SupplierPerformanceEvaluatorUser doApproval(SupplierPerformanceEvaluatorUser evaluatorUser, User actionBy,
                                                       String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer)
            throws NotAllowedException {

        evaluatorUser = supplierPerformanceEvaluatorUserService.getEvaluatorUserById(evaluatorUser.getId());
        List<PerformanceEvaluationApproval> approvalList = evaluatorUser.getEvaluationApprovals();

        // Identify Current Approval Level
        PerformanceEvaluationApproval currentLevel = null;
        for (PerformanceEvaluationApproval approval : approvalList) {
            if (approval.isActive()) {
                currentLevel = approval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        PerformanceEvaluationApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (PerformanceEvaluationApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject Performance Evaluation '"
                    + evaluatorUser.getForm().getFormName() + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException("User " + actionBy.getName()
                    + " is not allowed to Approve or Reject Performance Evaluation '"
                    + evaluatorUser.getForm().getFormName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus()
                    + " Performance Evaluation at : " + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " Performance Evaluation at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (evaluatorUser.getEvalApprovalComment() == null) {
            evaluatorUser.setEvalApprovalComment(new ArrayList<SpFormEvaluationAppComment>());
        }
        SpFormEvaluationAppComment comment = new SpFormEvaluationAppComment();
        comment.setComment(remarks);
        comment.setApproved(approved);
        comment.setCreatedBy(actionBy);
        comment.setCreatedDate(new Date());
        comment.setEvaluatorUser(evaluatorUser);
        comment.setApprovalUserId(actionUser.getId());
        evaluatorUser.getEvalApprovalComment().add(comment);

        // If rejected
        if (!approved) {

            // Reset all approvals for re-approval as the Event is rejected.
            for (PerformanceEvaluationApproval approval : approvalList) {
                approval.setDone(false);
                approval.setActive(false);
                for (PerformanceEvaluationApprovalUser user : approval.getApprovalUsers()) {
                    // Send rejection email to all approvers
                    sentEvaluationRejectionEmail(user.getUser(), evaluatorUser.getForm(), actionBy, remarks,
                            evaluatorUser.getEvaluator());
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                }
            }
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            LOG.info("User " + actionBy.getName() + " has Rejected the Performance Evaluation : "
                    + evaluatorUser.getForm().getFormId());
            evaluatorUser.setEvaluationStatus(SupperPerformanceEvaluatorStatus.DRAFT);
            // For evaluator and Approver
            try {
                SupplierPerformanceAudit audit = new SupplierPerformanceAudit(evaluatorUser.getForm(), evaluatorUser,
                        actionBy, new Date(), SupplierPerformanceAuditActionType.Reject,
                        messageSource.getMessage("spf.audit.rejected",
                                new Object[] { evaluatorUser.getForm().getFormId() }, Global.LOCALE),
                        Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
                supplierPerformanceAuditService.save(audit);
            } catch (Exception e) {
                LOG.error("Error while saving audit : " + e.getMessage(), e);
            }

            // For form Owner
            try {
                SupplierPerformanceAudit audit = new SupplierPerformanceAudit(evaluatorUser.getForm(), null,
                        SecurityLibrary.getLoggedInUser(), new Date(), SupplierPerformanceAuditActionType.Reject,
                        "Supplier Performance Evaluation Form \"" + evaluatorUser.getForm().getFormId()
                                + "\" Rejected for Evaluator " + evaluatorUser.getEvaluator().getName() + ".",
                        Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
                supplierPerformanceAuditService.save(audit);
            } catch (Exception e) {
                LOG.info("Error while saving Audit : " + e.getMessage(), e);
            }

            try {
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED,
                        "Supplier Performance Evaluation Form \"" + evaluatorUser.getForm().getFormId()
                                + "\" Rejected for Evaluator " + evaluatorUser.getEvaluator().getName() + ".",
                        actionBy.getTenantId(), actionBy, new Date(), ModuleType.PerformanceEvaluation);
                buyerAuditTrailDao.save(buyerAuditTrail);
            } catch (Exception e) {
                LOG.error("Error while recording audit " + e.getMessage(), e);
            }

            try {
                if (evaluatorUser.getEvaluator() != null) {
                    LOG.info("Sending rejected request email to Evaluator : "
                            + evaluatorUser.getEvaluator().getCommunicationEmail());
                    sentEvaluationRejectionEmail(evaluatorUser.getEvaluator(), evaluatorUser.getForm(), actionBy,
                            remarks, evaluatorUser.getEvaluator());
                }
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

        } else {
            LOG.info("User " + actionBy.getName() + " has Approved the Evaluation aPp : "
                    + evaluatorUser.getForm().getFormName());
            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            // Send email notification to Evalutor
            sentEvaluationApprovalEmail(evaluatorUser.getEvaluator(), evaluatorUser.getForm(), actionBy, remarks,
                    false);

            // Send email notification to actionBy
            sentEvaluationApprovalEmail(actionBy, evaluatorUser.getForm(), actionBy, remarks, true);

            // For evaluator and Approver
            try {
                SupplierPerformanceAudit audit = new SupplierPerformanceAudit(evaluatorUser.getForm(), evaluatorUser,
                        actionBy, new Date(), SupplierPerformanceAuditActionType.Approve,
                        messageSource.getMessage("spf.audit.approved",
                                new Object[] { evaluatorUser.getForm().getFormId() }, Global.LOCALE),
                        Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
                supplierPerformanceAuditService.save(audit);
            } catch (Exception e) {
                LOG.error("Error while saving audit : " + e.getMessage(), e);
            }

            // For form Owner
            try {
                SupplierPerformanceAudit audit = new SupplierPerformanceAudit(evaluatorUser.getForm(), null,
                        SecurityLibrary.getLoggedInUser(), new Date(), SupplierPerformanceAuditActionType.Approve,
                        "Supplier Performance Evaluation Form \"" + evaluatorUser.getForm().getFormId()
                                + "\" Approved for Evaluator " + evaluatorUser.getEvaluator().getName() + ".",
                        Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
                supplierPerformanceAuditService.save(audit);
            } catch (Exception e) {
                LOG.info("Error while saving Audit : " + e.getMessage(), e);
            }

            try {
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                        "Supplier Performance Evaluation Form \"" + evaluatorUser.getForm().getFormId()
                                + "\" Approved for Evaluator " + evaluatorUser.getEvaluator().getName() + ".",
                        actionBy.getTenantId(), actionBy, new Date(), ModuleType.PerformanceEvaluation);
                buyerAuditTrailDao.save(buyerAuditTrail);
            } catch (Exception e) {
                LOG.error("Error while recording audit " + e.getMessage(), e);
            }

            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                setNextOrAllDone(actionBy, approvalList, currentLevel, evaluatorUser, session, actionBy);

            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (PerformanceEvaluationApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            allUsersDone = false;
                            LOG.info("All users of this level have not approved for Evaluation App.");
                            break;
                        }
                    }
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the RFP.");
                    setNextOrAllDone(actionBy, approvalList, currentLevel, evaluatorUser, session, actionBy);

                }
            }
        }
        return supplierPerformanceEvaluatorUserDao.update(evaluatorUser);
    }

    /**
     * @param mailTo
     * @param form
     * @param actionBy
     * @param remarks
     * @param self
     */
    private void sentEvaluationApprovalEmail(User mailTo, SupplierPerformanceForm form, User actionBy, String remarks,
                                             boolean self) {
        LOG.info("Sending approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

        String subject = "Supplier Performance Evaluation Approved";
        String url = APP_URL + "/buyer/viewSPFSummary?formId=" + form.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("referenceName", form.getReferenceName());
        map.put("supplier", form.getAwardedSupplier().getCompanyName());
        map.put("remarks", remarks);
        map.put("formId", form.getFormId());
        map.put("referenceNumber", form.getReferenceNumber());
        if (self) {
            map.put("message", "You have approved the following Supplier Performance Evaluation.");
        } else {
            map.put("message", actionBy.getName() + " has approved the following Supplier Performance Evaluation.");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");
        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications() ) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.EVALUATION_FORM_APPROVED);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

    }

    /**
     * @param mailTo
     * @param form
     * @param actionBy
     * @param remarks
     * @param ownerUser
     */
    private void sentEvaluationRejectionEmail(User mailTo, SupplierPerformanceForm form, User actionBy, String remarks,
                                              User ownerUser) {
        LOG.info("Sending rejected request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());

        String subject = "Supplier Performance Evaluation Rejected";
        String url = APP_URL + "/buyer/viewSPFSummary?formId=" + form.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("actionByName", actionBy.getName());
        map.put("referenceName", form.getReferenceName());
        map.put("supplier", form.getAwardedSupplier().getCompanyName());
        map.put("remarks", remarks);
        map.put("formId", form.getFormId());
        map.put("referenceNumber", form.getReferenceNumber());
        if (mailTo.getId().equals(actionBy.getId())) {
            url = APP_URL + "/login";
            map.put("message", "You have rejected the following Supplier Performance Evaluation.");
        } else {
            map.put("message", actionBy.getName() + " has rejected the following Supplier Performance Evaluation.");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("appUrl", url);
        map.put("loginUrl", APP_URL + "/login");
        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.EVALUATION_FORM_REJECTED);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }
    }

    // Product Contract Approval
    @Override
    @Transactional(readOnly = false)
    public ProductContract doApproval(ProductContract contract, User loggedInUser, Boolean isFinish,
                                      HttpSession session, JRSwapFileVirtualizer virtualizer) throws Exception {
        try {
            String buyerTimeZone = "GMT+8:00";
            Buyer buyer = new Buyer();
            buyer.setId(loggedInUser.getTenantId());

            if (Boolean.TRUE == isFinish) {
                // Finish Audit
                try {
                    snapShotAuditService.doContractAudit(contract, session, contract, loggedInUser,
                            AuditActionType.Finish, messageSource.getMessage("contract.audit.finish",
                                    new Object[] { contract.getContractId() }, Global.LOCALE),
                            virtualizer);
                } catch (Exception e) {
                    LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                }

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.FINISH,
                            "Contract '" + contract.getContractId() + "' finished", loggedInUser.getTenantId(),
                            loggedInUser, new Date(), ModuleType.ContractList);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }
            }

            contract = productContractDao.findProductContractForApprovalById(contract.getId());
            List<ContractApproval> approvalList = contract.getApprovals();

            // If no approval setup - direct approve the Contract
            if (CollectionUtil.isEmpty(approvalList)) {
                ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(loggedInUser.getTenantId());
                LOG.info("Contract enabled in BU : " + contract.getBusinessUnit().getContractIntegration());
//				LOG.info("ERP Enabledd : " + erpSetup.getIsErpEnable() + " Enable contract push "
//						+ erpSetup.getEnableContractErpPush() + " Old Status : " + contract.getOldStatus()
//						+ " SAP Contact No" + contract.getSapContractNumber());
                if (Boolean.TRUE == contract.getBusinessUnit().getContractIntegration()
                        && (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()
                        && Boolean.TRUE == erpSetup.getEnableContractErpPush())) {
                    if (contract.getOldStatus() == null
                            && StringUtils.checkString(contract.getSapContractNumber()).length() == 0) {
                        erpIntegrationService.createContractInErp(contract.getId(), erpSetup, loggedInUser);
                    } else if (contract.getStatus() == ContractStatus.PENDING
                            && contract.getOldStatus() == ContractStatus.ACTIVE
                            && StringUtils.checkString(contract.getSapContractNumber()).length() > 0) {
                        erpIntegrationService.updateContractInErp(contract.getId(), erpSetup, loggedInUser);
                    }
                } else {
                    productContractItemsDao.updateErpTransfer(contract.getId());
                }
                contract.setStatus(ContractStatus.APPROVED);
                contract.setDocumentDate(new Date());
                LOG.info("status :" + contract.getStatus());

                // flag items as sent to erp so that they cannot be edited in edit mode in
                // suspended status

                // try {
                // snapShotAuditService.doContractAudit(contract, session, contract,
                // loggedInUser,
                // AuditActionType.Approved, messageSource.getMessage("contract.audit.approve",
                // new Object[] {
                // contract.getContractId() }, Global.LOCALE), virtualizer);
                // } catch (Exception e) {
                // LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                // }

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            "Contract '" + contract.getContractId() + "' Approved", loggedInUser.getTenantId(),
                            loggedInUser, new Date(), ModuleType.ContractList);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }

            } else {
                ContractApproval currentLevel = null;
                if (contract.getStatus() == ContractStatus.DRAFT) {
                    contract.setStatus(ContractStatus.PENDING);
                    for (ContractApproval approval : approvalList) {
                        if (approval.getLevel() == 1) {
                            approval.setActive(true);
                            break;
                        }
                    }
                } else {
                    for (ContractApproval approval : approvalList) {
                        if (approval.isActive()) {
                            currentLevel = approval;
                            break;
                        }
                    }
                    boolean allUsersDone = true;
                    if (currentLevel != null) {
                        for (ContractApprovalUser user : currentLevel.getApprovalUsers()) {
                            if (ApprovalStatus.PENDING == user.getApprovalStatus()) {
                                LOG.info("All users of this level have not approved the Contract.");
                                allUsersDone = false;
                                break;
                            }
                        }
                    }
                    if (allUsersDone) {
                        setNextOrAllDone(loggedInUser, approvalList, currentLevel, contract, "");
                    }
                }
                // Just send emails to users.
                for (ContractApproval approval : approvalList) {
                    if (approval.isActive()) {
                        for (ContractApprovalUser user : approval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                buyerTimeZone = getTimeZoneByBuyerSettings(user.getUser().getTenantId(), buyerTimeZone);
                                sendEmailToContractApprovers(contract, user, buyerTimeZone);

                                if (Boolean.TRUE == contract.getEnableApprovalReminder()) {
                                    Integer reminderHr = contract.getReminderAfterHour();
                                    Integer reminderCpunt = contract.getReminderCount();
                                    if (reminderHr != null && reminderCpunt != null) {
                                        Calendar now = Calendar.getInstance();
                                        now.add(Calendar.HOUR, reminderHr);
                                        user.setNextReminderTime(now.getTime());
                                        user.setReminderCount(reminderCpunt);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            contract.setContractSummaryCompleted(true);
            contract = productContractDao.update(contract);
        } catch (Exception e) {
            LOG.info("ERROR While Approving Contract :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving Contract :" + e.getMessage());
        }
        return contract;
    }

    public void setNextOrAllDone(User actionBy, List<ContractApproval> approvalList, ContractApproval currentLevel,
                                 ProductContract contract, String remarks) throws Exception, ErpIntegrationException {
        String buyerTimeZone = "GMT+8:00";
        currentLevel.setDone(true);
        currentLevel.setActive(false); // Check if all approvals are done
        if (currentLevel.getLevel() == approvalList.size()) {
            // all approvals done
            LOG.info("All approvals for this Contract is done!!!. Going to Approved Mode.");
            if (contract.getTerminationRequested()) {
                contract.setStatus(ContractStatus.TERMINATED);

                sendContractApprovalEmail(contract.getContractCreator(), contract, actionBy, remarks);
                // Send email notification to Creator
                sendContractApprovalEmail(actionBy, contract, actionBy, remarks);

                try {
                    snapShotAuditService.doContractAudit(contract, null, contract, actionBy, AuditActionType.Terminated,
                            messageSource.getMessage("contract.audit.terminate",
                                    new Object[] { contract.getContractId() }, Global.LOCALE),
                            null);
                } catch (Exception e) {
                    LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                }

                try {
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TERMINATED,
                            messageSource.getMessage("contract.audit.terminate",
                                    new Object[] { contract.getContractId() }, Global.LOCALE),
                            actionBy.getTenantId(), actionBy, new Date(), ModuleType.ContractList);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                } catch (Exception e) {
                    LOG.error("Error while recording contract termination audit " + e.getMessage(), e);
                }

            } else {

                ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(actionBy.getTenantId());
                LOG.info("Contract enabled in BU : " + contract.getBusinessUnit().getContractIntegration());
                if (Boolean.TRUE == contract.getBusinessUnit().getContractIntegration()
                        && (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()
                        && Boolean.TRUE == erpSetup.getEnableContractErpPush())) {
                    LOG.info("ERP Enabledd : " + erpSetup.getIsErpEnable() + " Enable contract push "
                            + erpSetup.getEnableContractErpPush() + " Old Status : " + contract.getOldStatus()
                            + " SAP Contact No" + contract.getSapContractNumber());

                    if (contract.getOldStatus() == null
                            && StringUtils.checkString(contract.getSapContractNumber()).length() == 0) {
                        erpIntegrationService.createContractInErp(contract.getId(), erpSetup, actionBy);
                    } else if (contract.getStatus() == ContractStatus.PENDING
                            && contract.getOldStatus() == ContractStatus.ACTIVE
                            && StringUtils.checkString(contract.getSapContractNumber()).length() > 0) {
                        erpIntegrationService.updateContractInErp(contract.getId(), erpSetup, actionBy);
                    }
                } else {
                    // flag items as sent to erp so that they cannot be edited in edit mode in
                    // suspended status
                    productContractItemsDao.updateErpTransfer(contract.getId());
                }
                contract.setStatus(ContractStatus.APPROVED);
                contract.setDocumentDate(new Date());

            }
        } else {
            for (ContractApproval approval : approvalList) {
                if (approval.getLevel() == currentLevel.getLevel() + 1) {
                    LOG.info("Setting Approval level " + approval.getLevel() + " as Active level");
                    approval.setActive(true);
                    for (ContractApprovalUser nextLevelUser : approval.getApprovalUsers()) {
                        buyerTimeZone = getTimeZoneByBuyerSettings(nextLevelUser.getUser().getTenantId(),
                                buyerTimeZone);
                        sendEmailToContractApprovers(contract, nextLevelUser, buyerTimeZone);
                        if (Boolean.TRUE == contract.getEnableApprovalReminder()) {
                            Integer reminderHr = contract.getReminderAfterHour();
                            Integer reminderCpunt = contract.getReminderCount();
                            if (reminderHr != null && reminderCpunt != null) {
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.HOUR, reminderHr);
                                nextLevelUser.setNextReminderTime(now.getTime());
                                nextLevelUser.setReminderCount(reminderCpunt);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = { Exception.class, ErpIntegrationException.class })
    public ProductContract doApproval(ProductContract contract, User actionBy, String remarks, boolean approved,
                                      HttpSession session) throws Exception, ErpIntegrationException {

        contract = productContractDao.findProductContractForApprovalById(contract.getId());

        List<ContractApproval> approvalList = contract.getApprovals();
        List<ContractTeamMember> contractTeamMembers = contract.getTeamMembers();

        // Identify Current Approval Level
        ContractApproval currentLevel = null;
        for (ContractApproval approval : approvalList) {
            if (approval.isActive()) {
                currentLevel = approval;
                LOG.info("Current Approval Level : " + currentLevel.getLevel());
                break;
            }
        }

        // Identify actionUser in the ApprovalUser of current level
        ContractApprovalUser actionUser = null;
        if (currentLevel != null) {
            for (ContractApprovalUser user : currentLevel.getApprovalUsers()) {
                if (user.getUser().getId().equals(actionBy.getId())) {
                    actionUser = user;
                    LOG.info("Approval being done by : " + actionBy.getLoginId());
                }
            }
        }
        if (actionUser == null) {
            // throw error
            LOG.error("User " + actionBy.getName() + " is not allowed to Approve or Reject Contract '"
                    + contract.getContractName() + "' at approval level : " + currentLevel.getLevel());
            throw new NotAllowedException(
                    "User " + actionBy.getName() + " is not allowed to Approve or Reject Contract '"
                            + contract.getContractName() + "' at approval level : " + currentLevel.getLevel());
        }

        if (actionUser.getApprovalStatus() != ApprovalStatus.PENDING) {
            // throw error
            LOG.error("User " + actionBy.getName() + " has already " + actionUser.getApprovalStatus()
                    + " Contract at : " + actionUser.getActionDate());
            throw new NotAllowedException("User " + actionBy.getName() + " has already "
                    + actionUser.getApprovalStatus() + " Contract at : " + actionUser.getActionDate());
        }

        // adding remarks into comments
        if (contract.getContractComments() == null) {
            contract.setContractComments(new ArrayList<ContractComment>());
        }
        ContractComment contractComment = new ContractComment();
        contractComment.setComment(remarks);
        contractComment.setApproved(approved);
        contractComment.setCreatedBy(actionBy);
        contractComment.setCreatedDate(new Date());
        contractComment.setProductContract(contract);
        contractComment.setApprovalUserId(actionUser.getId());
        contract.getContractComments().add(contractComment);

        // If rejected
        if (!approved) {

            // Reset all approvals for re-approval as the Contract is rejected.
            for (ContractApproval contractApproval : approvalList) {
                contractApproval.setDone(false);
                contractApproval.setActive(false);
                for (ContractApprovalUser user : contractApproval.getApprovalUsers()) {
                    try {
                        // Send rejection email to all approver users
                        // if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                        sendContractRejectionEmail(user.getUser(), contract, actionBy, remarks);
                        // }
                    } catch (Exception e) {
                        LOG.info("ERROR while Sending Contract reject mail :" + e.getMessage(), e);
                    }
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                    user.setNextReminderTime(null);
                    user.setReminderCount(null);
                }
            }

            for (ContractTeamMember contractTeamMember : contractTeamMembers) {
                try {
                    sendContractRejectionEmail(contractTeamMember.getUser(), contract, actionBy, remarks);
                } catch (Exception e) {
                    LOG.error("Error while sending mail to team member" + e.getMessage(), e);
                }
            }

            try {
                if (contract.getContractCreator() != null) {
                    sendContractRejectionEmail(contract.getContractCreator(), contract, actionBy, remarks);
                }
            } catch (Exception e) {
                LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
            }

            // actionUser.setApprovalStatus(ApprovalStatus.REJECTED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);
            LOG.info("User " + actionBy.getName() + " has Rejected the Contract : " + contract.getContractName());
            if (contract.getTerminationRequested()) {
                contract.setStatus(ContractStatus.ACTIVE);
                contract.setTerminationRemarks(null);
                contract.setTerminationRequested(Boolean.FALSE);
            } else {
                if (ContractStatus.ACTIVE == contract.getOldStatus()) {
                    contract.setStatus(ContractStatus.SUSPENDED);
                } else {
                    contract.setStatus(ContractStatus.DRAFT);
                }
            }

        } else { // If Approved
            LOG.info("User " + actionBy.getName() + " has Approved the Contract : " + contract.getContractName());

            actionUser.setApprovalStatus(ApprovalStatus.APPROVED);
            actionUser.setActionDate(new Date());
            actionUser.setRemarks(remarks);

            if (ApprovalType.OR == currentLevel.getApprovalType()) {
                LOG.info("This level has OR set for approval. Marking level as done");
                try {
                    ContractAudit contractAudit = new ContractAudit(contract, actionBy, new Date(),
                            AuditActionType.Approved, messageSource.getMessage("contract.audit.approve",
                            new Object[] { contract.getContractId() }, Global.LOCALE));
                    contractAuditDao.save(contractAudit);
                    // snapShotAuditService.doContractAudit(contract, session, contract, actionBy,
                    // AuditActionType.Approved, messageSource.getMessage("contract.audit.approve",
                    // new Object[] {
                    // contract.getContractId() }, Global.LOCALE), null);
                } catch (Exception e) {
                    LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                }
                setNextOrAllDone(actionBy, approvalList, currentLevel, contract, remarks);

                // Send email notification to Creator
                if (!contract.getTerminationRequested()) {
                    sendContractApprovalEmail(contract.getContractCreator(), contract, actionBy, remarks);

                    // Send email notification to Creator
                    sendContractApprovalEmail(actionBy, contract, actionBy, remarks);
                }

                for (ContractTeamMember teamMember : contractTeamMembers) {
                    try {
                        sendContractApprovalEmail(teamMember.getUser(), contract, actionBy, remarks);
                    } catch (Exception e) {
                        LOG.error("Error while sending contract approver email : " + e.getMessage(), e);
                    }
                }

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            messageSource.getMessage("contract.audit.approve",
                                    new Object[] { contract.getContractId() }, Global.LOCALE),
                            actionBy.getTenantId(), actionBy, new Date(), ModuleType.ContractList);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }

            } else {
                // AND Operation
                LOG.info("This level has AND set for approvals");
                boolean allUsersDone = true;
                if (currentLevel != null) {
                    for (ContractApprovalUser user : currentLevel.getApprovalUsers()) {
                        if (ApprovalStatus.PENDING == user.getApprovalStatus()
                                || ApprovalStatus.REJECTED == user.getApprovalStatus()) {
                            LOG.info("All users of this level have not approved the Contract.");
                            allUsersDone = false;
                            break;
                        }
                    }
                }

                try {
                    ContractAudit contractAudit = new ContractAudit(contract, actionBy, new Date(),
                            AuditActionType.Approved, messageSource.getMessage("contract.audit.approve",
                            new Object[] { contract.getContractId() }, Global.LOCALE));
                    contractAuditDao.save(contractAudit);
                } catch (Exception e) {
                    LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                }
                if (allUsersDone) {
                    LOG.info("All users of this level have approved the Contract.");
                    setNextOrAllDone(actionBy, approvalList, currentLevel, contract, remarks);
                }

                // Send email notification to Creator
                if (!contract.getTerminationRequested()) {
                    sendContractApprovalEmail(contract.getContractCreator(), contract, actionBy, remarks);

                    // Send email notification to Creator
                    sendContractApprovalEmail(actionBy, contract, actionBy, remarks);
                }

                try {
                    LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED,
                            messageSource.getMessage("contract.audit.approve",
                                    new Object[] { contract.getContractId() }, Global.LOCALE),
                            actionBy.getTenantId(), actionBy, new Date(), ModuleType.ContractList);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                    LOG.info("--------------------------AFTER AUDIT---------------------------------------");
                } catch (Exception e) {
                    LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
                }
            }
        }
        return productContractDao.update(contract);
    }

    /**
     * @param contract
     * @param user
     * @param buyerTimeZone
     */
    public void sendEmailToContractApprovers(ProductContract contract, ContractApprovalUser user,
                                             String buyerTimeZone) {
        String mailTo = user.getUser().getCommunicationEmail();
        String subject = null;
        if (Boolean.TRUE == contract.getTerminationRequested()) {
            subject = "Contract Termination Approval Request";
        } else {
            subject = "Contract Approval Request";
        }
        String url = APP_URL + "/buyer/contractView/" + contract.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", user.getUser().getName());
        map.put("contractId", contract.getContractId());
        map.put("contractName", contract.getContractName());
        map.put("contractReferenceNumber", StringUtils.checkString(contract.getContractReferenceNumber()));
        if (Boolean.TRUE == contract.getTerminationRequested()) {
            map.put("message", "Your attention is required for Approval of following contract termination");
        } else {
            map.put("message", "Your attention is required for Approval of following contract");
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        df.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);
        if (StringUtils.checkString(mailTo).length() > 0 && user.getUser().getEmailNotifications()) {
            sendEmail(mailTo, subject, map, Global.CONTRACT_APPROVAL_REQUEST);
        } else {
            LOG.warn("No communication email configured for user : " + user.getUser().getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("contract.approval.request.notification.message",
                new Object[] { contract.getContractName() }, Global.LOCALE);
        sendDashboardNotification(user.getUser(), url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);
        if (StringUtils.checkString(user.getUser().getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo + "' and device Id :" + user.getUser().getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", contract.getId());
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(user.getUser().getDeviceId()));
            } catch (Exception e) {
                LOG.error(
                        "Error While sending Approval Mobile push notification to '" + mailTo + "' : " + e.getMessage(),
                        e);
            }
        } else {
            LOG.info("User '" + mailTo + "' Device Id is Null");
        }
    }

    /**
     * @param mailTo
     * @param mailTo
     * @param actionBy
     * @param remarks
     */
    private void sendContractRejectionEmail(User mailTo, ProductContract contract, User actionBy, String remarks) {
        LOG.info("Sending rejected request email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
        String url = APP_URL + "/buyer/contractView/" + contract.getId();
        String subject = null;
        if (contract.getTerminationRequested()) {
            subject = "Contract Termination Rejected";
        } else {
            subject = "Contract Rejected";
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("contractId", contract.getContractId());
        map.put("contractName", contract.getContractName());
        map.put("contractReferanceNumber", StringUtils.checkString(contract.getContractReferenceNumber()));
        map.put("remark", StringUtils.checkString(remarks));
        if (mailTo.getId().equals(actionBy.getId())) {
            if (contract.getTerminationRequested()) {
                map.put("message", "You have rejected following contract termination");
            } else {
                map.put("message", "You have rejected following contract");
            }
        } else {
            if (contract.getTerminationRequested()) {
                map.put("message", actionBy.getName() + " has rejected following contract termination");
            } else {
                map.put("message", actionBy.getName() + " has rejected following contract");
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.CONTRACT_REJECTED);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("contract.rejection.notification.message",
                new Object[] { actionBy.getName(), contract.getContractName(), remarks }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.REJECT_MESSAGE);

        if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", contract.getId());
                payload.put("messageType", NotificationType.REJECT_MESSAGE.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(mailTo.getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Contract reject Mobile push notification to '"
                        + mailTo.getCommunicationEmail() + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
        }
    }

    /**
     * @param mailTo
     * @param contract
     * @param actionBy
     * @param remarks
     */
    private void sendContractApprovalEmail(User mailTo, ProductContract contract, User actionBy, String remarks) {
        LOG.info("Sending Approval email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
        String subject = null;
        if (contract.getTerminationRequested()) {
            subject = "Contract Termination Approved";
        } else {
            subject = "Contract Approved";
        }
        String url = APP_URL + "/buyer/contractView/" + contract.getId();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("userName", mailTo.getName());
        map.put("contractId", contract.getContractId());
        map.put("contractName", contract.getContractName());
        map.put("contractReferanceNumber", StringUtils.checkString(contract.getContractReferenceNumber()));
        map.put("remark", StringUtils.checkString(remarks));
        if (mailTo.getId().equals(actionBy.getId())) {
            if (contract.getTerminationRequested()) {
                map.put("message", "You have approved following contract termination");
            } else {
                map.put("message", "You have approved following contract");
            }
        } else {
            if (contract.getTerminationRequested()) {
                map.put("message", actionBy.getName() + " has approved following contract termination");
            } else {
                map.put("message", actionBy.getName() + " has approved following contract");
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
        String timeZone = "GMT+8:00";
        timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
        df.setTimeZone(TimeZone.getTimeZone(timeZone));
        map.put("date", df.format(new Date()));
        map.put("loginUrl", APP_URL + "/login");
        map.put("appUrl", url);

        if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
            sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.CONTRACT_APPROVED);
        } else {
            LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
                    + "... Not going to send email notification");
        }

        String notificationMessage = messageSource.getMessage("contract.approval.notification.message",
                new Object[] { actionBy.getName(), contract.getContractName() }, Global.LOCALE);
        sendDashboardNotification(mailTo, url, subject, notificationMessage, NotificationType.APPROVAL_MESSAGE);

        if (StringUtils.checkString(mailTo.getDeviceId()).length() > 0) {
            try {
                LOG.info("User '" + mailTo.getCommunicationEmail() + "' and device Id :" + mailTo.getDeviceId());
                Map<String, String> payload = new HashMap<String, String>();
                payload.put("id", contract.getId());
                payload.put("messageType", NotificationType.APPROVAL_MESSAGE.toString());
                notificationService.pushOneSignalNotification(notificationMessage, null, payload,
                        Arrays.asList(mailTo.getDeviceId()));
            } catch (Exception e) {
                LOG.error("Error While sending Approval Mobile push notification to '" + mailTo.getCommunicationEmail()
                        + "' : " + e.getMessage(), e);
            }
        } else {
            LOG.info("User '" + mailTo.getCommunicationEmail() + "' Device Id is Null");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ProductContract doTerminationApproval(String contractId, User loggedInUser, Boolean isFInish,
                                                 HttpSession session, String terminateReason) throws Exception {
        ProductContract contract = productContractDao.findProductContractForApprovalById(contractId);
        try {
            String buyerTimeZone = "GMT+8:00";
            Buyer buyer = new Buyer();
            buyer.setId(loggedInUser.getTenantId());

            contract.setTerminationRequested(Boolean.TRUE);
            contract.setTerminationRemarks(terminateReason);

            // Termination Audit
            try {
                snapShotAuditService
                        .doContractAudit(contract, session, contract, loggedInUser, AuditActionType.Terminated,
                                messageSource.getMessage("contract.terminate.request.audit",
                                        new Object[] { contract.getContractId(), terminateReason }, Global.LOCALE),
                                null);
            } catch (Exception e) {
                LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
            }

            try {
                BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TERMINATED,
                        messageSource.getMessage("contract.terminate.request.audit",
                                new Object[] { contract.getContractId(), terminateReason }, Global.LOCALE),
                        loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.ContractList);
                buyerAuditTrailDao.save(buyerAuditTrail);
            } catch (Exception e) {
                LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
            }

            List<ContractApproval> approvalList = contract.getApprovals();
            // If no approval setup - direct approve the Contract
            if (CollectionUtil.isEmpty(approvalList)) {
                contract.setStatus(ContractStatus.TERMINATED);
                LOG.info("status :" + contract.getStatus());

                try {
                    snapShotAuditService.doContractAudit(contract, session, contract, loggedInUser,
                            AuditActionType.Terminated, messageSource.getMessage("contract.audit.terminate",
                                    new Object[] { contract.getContractId() }, Global.LOCALE),
                            null);
                } catch (Exception e) {
                    LOG.error("Error While saving Audit Trail : " + e.getMessage(), e);
                }

                try {
                    BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.TERMINATED,
                            messageSource.getMessage("contract.audit.terminate",
                                    new Object[] { contract.getContractId() }, Global.LOCALE),
                            loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.ContractList);
                    buyerAuditTrailDao.save(buyerAuditTrail);
                } catch (Exception e) {
                    LOG.error("Error while recording contract termination audit " + e.getMessage(), e);
                }
            } else {

                if (contract.getStatus() == ContractStatus.SUSPENDED || contract.getStatus().equals(ContractStatus.ACTIVE)) {
                    contract.setStatus(ContractStatus.PENDING);
                    // reset the approval levels.
                    for (ContractApproval approval : approvalList) {
                        if (approval.getLevel() == 1) {
                            approval.setActive(true);
                        }
                        approval.setDone(false);
                        approval.setEscalated(Boolean.FALSE);
                        for (ContractApprovalUser u : approval.getApprovalUsers()) {
                            u.setActionDate(null);
                            u.setApprovalStatus(ApprovalStatus.PENDING);
                            u.setRemarks(null);
                            u.setReminderCount(0); // it will be set again whenever that level gets active
                            u.setNextReminderTime(null); // it will be set again whenever that level gets active
                        }
                    }
                }

                // Just send emails to users.
                for (ContractApproval approval : approvalList) {
                    if (approval.isActive()) {
                        for (ContractApprovalUser user : approval.getApprovalUsers()) {
                            if (user.getApprovalStatus() == ApprovalStatus.PENDING) {
                                LOG.info("send mail to pending approvers ");
                                buyerTimeZone = getTimeZoneByBuyerSettings(user.getUser().getTenantId(), buyerTimeZone);
                                sendEmailToContractApprovers(contract, user, buyerTimeZone);

                                if (Boolean.TRUE == contract.getEnableApprovalReminder()) {
                                    Integer reminderHr = contract.getReminderAfterHour();
                                    Integer reminderCpunt = contract.getReminderCount();
                                    if (reminderHr != null && reminderCpunt != null) {
                                        Calendar now = Calendar.getInstance();
                                        now.add(Calendar.HOUR, reminderHr);
                                        user.setNextReminderTime(now.getTime());
                                        user.setReminderCount(reminderCpunt);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            contract = productContractDao.update(contract);
        } catch (Exception e) {
            LOG.info("ERROR While Approving Contract Termination :" + e.getMessage(), e);
            throw new Exception("ERROR While Approving Contract Termination :" + e.getMessage());
        }
        return contract;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateContractDetailsOnErpError(User loggedInUser, String productContractId, String errormessage) {
        // Storing audit history for error
        try {
            ProductContract productContract = productContractDao.findById(productContractId);

            ContractAudit audit = new ContractAudit();
            audit.setAction(AuditActionType.Erp);
            audit.setActionBy(loggedInUser);
            audit.setActionDate(new Date());
            Buyer buyer = new Buyer();
            buyer.setId(productContract.getBuyer().getId());
            audit.setBuyer(buyer);
            audit.setDescription(StringUtils.checkString(errormessage));
            audit.setProductContract(productContract);
            contractAuditDao.save(audit);

            productContract.setErpTransferred(Boolean.FALSE);
            if (ContractStatus.ACTIVE == productContract.getOldStatus()) {
                productContract.setStatus(ContractStatus.SUSPENDED);
            } else {
                productContract.setStatus(ContractStatus.DRAFT);
            }
            List<ContractApproval> approvalList = productContract.getApprovals();
            for (ContractApproval contractApproval : approvalList) {
                contractApproval.setDone(false);
                contractApproval.setActive(false);
                for (ContractApprovalUser user : contractApproval.getApprovalUsers()) {
                    user.setActionDate(null);
                    user.setApprovalStatus(ApprovalStatus.PENDING);
                    user.setRemarks(null);
                    user.setActionDate(null);
                    user.setNextReminderTime(null);
                    user.setReminderCount(null);
                }
            }

            productContract = productContractDao.update(productContract);

        } catch (Exception error) {
            LOG.error("Error while saving Contract ERP Audit History in catch block :" + error.getMessage(), error);
        }
    }

}
