package com.privasia.procurehere.core.utils;

import java.util.Locale;

/**
 * @author Ravi
 */
public class Global {

	public final static String SCHEMA = "proc";
	public static final int BATCH_INSERT_SIZE = 50;
	public static final String FILE_PATH = System.getProperty("user.home") + "/tmp";

	public static final String ACL_LIST_EXCEL_PARSER = "ACL_PARSER";
	public static final String INDUSTRY_CATEGORY_EXCEL_PARSER = "INDUSTRY_CATEGORY_PARSER";
	public static final String COUNTRY_EXCEL_PARSER = "COUNTRY_EXCEL_PARSER";
	public static final String STATE_EXCEL_PARSER = "STATE_EXCEL_PARSER";
	public static final String TIMEZONE_EXCEL_PARSER = "TIMEZONE_EXCEL_PARSER";
	public static final String CURRENCY_EXCEL_PARSER = "CURRENCY_EXCEL_PARSER";
	public static final String COMPANYSTATUS_EXCEL_PARSER = "COMPANYSTATUS_EXCEL_PARSER";
	public static final String UOM_EXCEL_PARSER = "UOM_EXCEL_PARSER";

	public static final int PASSWORD_EXPIRY = 90; // 90 Days
	public static final boolean PASSWORD_HISTORY_ENABLED = true; // Password history enabled/disabled
	public static final int PASSWORD_HISTORY_COUNT = 6;

	public static final Locale LOCALE = Locale.getDefault();
	public static final int LOGIN_FAILURE_ATTEMPTS = 6;

	public static final String SESSION_PROFILE_PICTURE_KEY = "profPic";
	public static final String SESSION_TIME_ZONE_LOCATION_KEY = "timeZoneLocation";
	public static final String SESSION_TIME_ZONE_KEY = "timeZone";
	public static final String SESSION_IP_ADDRESS_KEY = "ipAddress";

	public static final String ADMIN_LOG = "adminLog";
	public static final String ERROR_LOG = "errorLog";
	public static final String BUYER_LOG = "buyerLog";
	public static final String EMAIL_LOG = "emailLog";
	public static final String PR_LOG = "prLog";
	public static final String PO_LOG = "poLog";
	public static final String GR_LOG = "grLog";
	public static final String INVOICE_LOG = "invoiceLog";
	public static final String RFS_LOG = "rfsLog";
	public static final String SUPPLIER_LOG = "supplierLog";
	public static final String RFX_LOG = "rfxLog";
	public static final String AUCTION_LOG = "auctionLog";
	public static final String INTEGRATION_LOG = "integrationLog";
	public static final String SUBSCRIPTION_LOG = "subscriptionLog";
	public static final String FINANCE_COMPANY_LOG = "financeCompanyLog";
	public static final String ERP_LOG = "erpLog";
	public static final String API_LOG = "apiLog";
	public static final String DO_LOG = "doLog";
	public static final String INV_LOG = "invLog";
	public static final String DOWNLOAD_LOG = "downloadLog";
	public static final String GRN_LOG = "grnLog";
	public static final String TATREPORT_LOG = "tatLog";
	public static final String CONTRACT_LOG = "contractLog";

	public static final String SUPPLIER_SESSION_STORE_KEY = "Supplier";
	public static final String SUPPLIER_COVERAGE = "step-2";
	public static final String PROJECT_COVERAGE = "step-6";
	public static final String[] CQ_EXCEL_COLUMNS = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "QUESTION TYPE", "ATTACHMENT", "REQUIRED", "ATTACHMENT REQUIRED", "OPTION-1", "OPTION-2", "OPTION-3", "OPTION-4", "OPTION-5", "OPTION-6", "OPTION-7", "OPTION-8", "OPTION-9", "OPTION-10", "OPTION-11", "OPTION-12", "OPTION-13", "OPTION-14", "OPTION-15", "OPTION-16", "OPTION-17", "OPTION-18", "OPTION-19", "OPTION-20", "OPTION-21", "OPTION-22", "OPTION-23", "OPTION-24", "OPTION-25", "OPTION-26", "OPTION-27", "OPTION-28", "OPTION-29", "OPTION-30" };
	public static final String[] CQ_EXCEL_OPTION_COLUMNS = new String[] { "OPTION-1", "OPTION-2", "OPTION-3", "OPTION-4", "OPTION-5", "OPTION-6", "OPTION-7", "OPTION-8", "OPTION-9", "OPTION-10", "OPTION-11", "OPTION-12", "OPTION-13", "OPTION-14", "OPTION-15", "OPTION-16", "OPTION-17", "OPTION-18", "OPTION-19", "OPTION-20", "OPTION-21", "OPTION-22", "OPTION-23", "OPTION-24", "OPTION-25", "OPTION-26", "OPTION-27", "OPTION-28", "OPTION-29", "OPTION-30" };
	public static final String[] BQ_EXCEL_COLUMNS_TYPE_1 = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "UNIT QUANTITY", "UNIT PRICE", "PRICE TYPE" };
	public static final String[] BQ_EXCEL_COLUMNS_TYPE_2 = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "UNIT QUANTITY", "UNIT PRICE", "PRICE TYPE", "UNIT BUDGET PRICE", "ITEM CODE", "ITEM CATEGORY", "ITEM TYPE", "ACCOUNT WBS ELEMENT", "ACCOUNT ASSET NO", "ACCOUNT BUSINESS AREA", "ACCOUNT ORDER ID", "ACCOUNT NETWORK", "ACCOUNT ACTIVITY", "STORE LOCATION" };
	public static final String[] PR_EXCEL_COLUMNS = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "UNIT QUANTITY", "UNIT PRICE", "PRICE PER UNIT", "TAX (%)" };

	public static final String[] SOR_EXCEL_COLUMNS = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM"};

	public static final String[] SOR_EXCEL_COLUMNS_TYPE_RFX = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM"};

	public static final String[] PO_REPORT_EXCEL_COLUMNS = new String[] { "SR No", "REFERENCE NUMBER", "NAME OF PO", "SUPPLIER NAME", "DESCRIPTION", "PO NUMBER", "PO CREATED BY", "PO CREATED DATE", "PO GRAND TOTAL", "BUSINESS UNIT", "PO STATUS" };
	public static final String[] PR_REPORT_EXCEL_COLUMNS = new String[] { "SR No", "REFERENCE NUMBER", "NAME OF PR", "SUPPLIER NAME", "DESCRIPTION", "PR NUMBER", "PR CREATED BY", "PR CREATED DATE", "PR APPROVED BY", "PR APPROVED DATE", "CURRENCY", "PR GRAND TOTAL", "BUSINESS UNIT", "PO NUMBER" };
	public static final String[] FINANCE_PO_REPORT_EXCEL_COLUMNS = new String[] { "SR No", "NAME OF BUSINESS UNIT", "BUYER/REQUESTER", "PO REFERENCE NUMBER", "DATE & TIME", "PAYMENT TERMS", "ADDRESS LINE 1", "ADDRESS LINE 2", "POSTAL CODE", "CITY", "STATE", "DELIVERY DETAIL" };
	public static final String[] FINANCE_ITEM_REPORT_EXCEL_COLUMNS = new String[] { "PO REFERENCE NUMBER", "NO. ", "ITEM NAME", "DESCRIPTION", "UNIT", "QUANTITY", "CURRENCY", "UNIT PRICE", "TOTAL AMOUNT ", "TAX AMOUNT ", "TOTAL AMOUNT WITH TAX", "ADDITIONAL CHARGES" };
	public static final String[] PO_FINANCE_REPORT_EXCEL_COLUMNS = new String[] { "PO Date", "Finance Company", "Supplier Name", "PO Total Value", "Fee Paid to Privasia", "Fee Payment Date", "System Generated REF" };

	public static final String PATH_SEPARATOR = "/";
	public static final String ZIP_FILE_EXTENTION = ".zip";
	public static final String[] SUPPLIER_EXCEL_COLUMNS = new String[] { "COMPANY NAME", "COUNTRY CODE", "REGISTRATION NUMBER", "CONTACT FULL NAME", "DESIGNATION", "MOBILE NUMBER", "CONTACT NUMBER", "LOGIN EMAIL", "FAX NUMBER", "INDUSTRY CATEGORY", "PRODUCT CATEGORY", "SUPPLIER CODE", "STATUS", "COMMUNICATION EMAIL", "RATINGS", "SUPPLIER TAGS" };
	public static final String[] DOWNLOAD_SUPPLIER_EXCEL_COLUMNS = new String[] { "COMPANY NAME", "COUNTRY CODE", "REGISTRATION NUMBER", "CONTACT FULL NAME", "DESIGNATION", "MOBILE NUMBER", "CONTACT NUMBER", "LOGIN EMAIL", "FAX NUMBER", "INDUSTRY CATEGORY", "PRODUCT CATEGORY", "SUPPLIER CODE", "STATUS", "RATINGS", "COMMUNICATION EMAIL", "YEAR OF ESTABLISHED", "REGISTRATION DATE", "SUPPLIER TAGS" };
	public static final String[] BQ_EVALUATION_EXCEL_COLUMNS = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "UNIT QUANTITY", "UNIT PRICE", "AMOUNT", "TAX AMOUNT", "TOTAL AMOUNT" };
	// This time is use for auction console to provide user

	public static final String[] SOR_EVALUATION_EXCEL_COLUMNS = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "RATE"};


	public static final int ONLINE_USER_DIFF_SEC = 60;
	public static final String REQUEST_APPROVAL_CREATE_EMAIL = "requestCreateEmail.ftl";
	public static final String SOURCING_REJECT_TEMPLATE = "sourcingRejectedEmail.ftl";
	public static final String REQUEST_APPROVAL_TEMPLATE = "requestApproved.ftl";
	public static final String REQURST_APPROVAL_REQUEST_TEMPLATE = "requestApprovalrequest.ftl";
	public static final String EVENT_CREATED_TEMPLATE = "eventCreated.ftl";
	public static final String PR_APPROVAL_REQUEST_TEMPLATE = "prApprovalRequest.ftl";
	public static final String PR_APPROVAL_TEMPLATE = "prApproved.ftl";
	public static final String COMPANY_CHANGED_TEMPLATE = "compChangeNotification.ftl";
	public static final String PR_REJECT_TEMPLATE = "prRejected.ftl";
	public static final String PR_FINISH_TEMPLATE = "prFinished.ftl";
	public static final String PR_FINISH_SUPPLIER_TEMPLATE = "prFinishedSupplier.ftl";
	public static final String PO_CREATED_TEMPLATE = "poCreated.ftl";
	public static final String TEAM_MEMBER_TEMPLATE = "teamMemberInv.ftl";
	public static final String REMOVE_TEAM_MEMBER_TEMPLATE = "removeTeamMemberInv.ftl";
	public static final String TEAM_MEMBER_TEMPLATE_PR = "teamMemberInvPR.ftl";
	public static final String TEAM_MEMBER_TEMPLATE_PO = "teamMemberInvPO.ftl";
	public static final String TEAM_MEMBER_TEMPLATE_SOURCING = "teamMemberInvSourcing.ftl";
	public static final String REMOVE_TEAM_MEMBER_TEMPLATE_PR = "removeTeamMemberPr.ftl";
	public static final String REMOVE_TEAM_MEMBER_TEMPLATE_SOURCING = "removeTeamMemberSourcing.ftl";
	public static final String EVENT_APPROVAL_REQUEST_TEMPLATE = "eventApprovalRequest.ftl";
	public static final String FINANCEPO_SHARE_TEMPLATE = "financePoShareEmail.ftl";
	public static final String FINANCE_SUSPENDED_TEMPLATE = "financeSuspendedEmail.ftl";
	public static final String EVENT_APPROVAL_TEMPLATE = "eventApproved.ftl";
	public static final String USER_CREATION_TEMPLATE = "userCreation.ftl";
	public static final String SUPPLIER_AUTO_CREATION_TEMPLATE = "autoCreatedSupplier.ftl";
	public static final String SUPPLIER_CREATION_TEMPLATE = "createdSupplier.ftl";
	public static final String EVENT_REJECT_TEMPLATE = "eventRejected.ftl";
	public static final String EVENT_ACTIVE_TEMPLATE = "eventActive.ftl";
	public static final String EVENT_INVITATION_TEMPLATE = "eventInvitation.ftl";
	public static final String EVENT_SUSPENDED_TEMPLATE = "eventSuspended.ftl";
	public static final String EVENT_SUSPENDED_REVERT_BID_TEMPLATE = "eventSuspendedRevertBid.ftl";
	public static final String EVENT_RESUMED_TEMPLATE = "eventResumed.ftl";
	public static final String MEETING_REMINDER_TEMPLATE = "meetingReminder.ftl";
	public static final String EVENT_REMINDER_TEMPLATE = "eventReminder.ftl";
	public static final String EVENT_START_REMINDER_TEMPLATE = "eventStartReminder.ftl";
	public static final String EVENT_START_TEMPLATE = "eventStart.ftl";
	public static final String SUPPLIER_EVENT_ACCEPTED_REJECTED_TEMPLATE = "supplierEventAcceptedRejectedTemplate.ftl";
	public static final String EVENT_MAILBOX_TEMPLATE = "eventMailBoxTemplate.ftl";
	public static final String SUPPLIER_EVENT_SUBMISSION_TEMPLATE = "supplierEventSubmissionTemplate.ftl";
	public static final String EVENT_BUYER_INVITATION_TEMPLATE = "eventBuyerInvitation.ftl";
	public static final String EVENT_CLOSED_TEMPLATE = "eventClosed.ftl";
	public static final String ENVELOPE_READY_OPENED_TEMPLATE = "envelopeReadyOpened.ftl";
	public static final String EVENT_EVALUATION_TEMPLATE = "eventEvaluation.ftl";
	public static final String ENVELOPE_OPENED_TEMPLATE = "envelopeOpened.ftl";
	public static final String ENVELOPE_COMPLETED_TEMPLATE = "envelopeCompleted.ftl";
	public static final String ENVELOPE_COMPLETED_TO_UN_MASKING_USR_TEMPLATE = "envelopeCompletedTounMaskingUser.ftl";
	public static final String SUPPLIER_DISQUALIFY_TEMPLATE = "supplierDisqualified.ftl";
	public static final String SUPPLIER_SUCCESS_SUBSCRIPTION = "supplierSubscription.ftl";
	public static final String SUPPLIER_FAILURE_SUBSCRIPTION = "supplierFailureSubscription.ftl";
	public static final String SUPPLIER_SUBSCRIPTION_FOR_BUYER = "supplierSubscriptionForBuyer.ftl";
	public static final String BUYER_SUBSCRIPTION = "buyerSubscription.ftl";
	public static final String BUYER_FAILURE_SUBSCRIPTION = "buyerFailureSubscription.ftl";
	public static final String CANCEL_SUBSCRIPTION = "cancelSubscription.ftl";
	public static final String LEAD_EVALUATOR_INVITED_TEMPLATE = "leadEvaluatorInvited.ftl";
	public static final String LEAD_EVALUATOR_REMOVED_TEMPLATE = "leadEvaluatorRemoved.ftl";
	public static final String OPENER_REMOVED_TEMPLATE = "openerRemoved.ftl";
	public static final String OPENER_INVITED_TEMPLATE = "openerInvited.ftl";
	public static final String EVALUATOR_REMOVED_TEMPLATE = "evaluatorRemoved.ftl";
	public static final String EVALUATOR_INVITED_TEMPLATE = "evaluatorInvited.ftl";
	public static final String TIME_EXTENSION_TEMPLATE = "timeExtension.ftl";
	public static final String BUYER_EXPIRY_SUBSCRIPTION = "buyerExpirySubscription.ftl";
	public static final String BUYER_NEXT_SUBSCRIPTION_ACTIVE = "buyerNextSubscriptionActive.ftl";
	public static final String SUPPLIER_EXPIRY_SUBSCRIPTION = "supplierExpirySubscription.ftl";
	public static final String SUPPLIER_NEXT_SUBSCRIPTION_ACTIVE = "supplierNextSubscriptionActive.ftl";
	public static final String EXPIRY_REMINDER_SUBSCRIPTION = "expiryReminderSubscription.ftl";
	public static final String USER_DEACTIVATE_REMINDER = "userDeactivateReminder.ftl";
	public static final String EVENT_CREDIT_REMINDER = "eventCreditReminder.ftl";
	public static final String BUYER_AUTO_SUBSCRIPTION_SUCCESS = "buyerAutoSubscriptionSuccess.ftl";
	public static final String BUYER_AUTO_SUBSCRIPTION_CANCEL = "buyerAutoSubscriptionCancel.ftl";
	public static final String BUYER_AUTO_SUBSCRIPTION_DUPLICATE = "buyerAutoSubscriptionDuplicate.ftl";
	public static final String ERP_EVENT_CREATED_TEMPLATE = "erpEventCreated.ftl";
	public static final String ERP_EVENT_PENDING_TEMPLATE = "erpEventPending.ftl";
	public static final String ERP_EVENT_DUPLICATE_TEMPLATE = "erpEventDuplicate.ftl";
	public static final String ERP_EVENT_ERROR_TEMPLATE = "erpEventError.ftl";
	public static final String ERP_PR_REJECT_TEMPLATE = "ErpPrRejected.ftl";
	public static final String ERP_EVENT_REJECT_TEMPLATE = "ErpEventRejected.ftl";
	public static final String ERP_PR_ERROR_TEMPLATE = "ErpPrError.ftl";
	public static final String TRIAL_SUBSCRIPTION_SIGNUP_TEMPLATE = "trialSubscriptionSignup.ftl";
	public static final String BUYER_FAILURE_PAYMENT = "buyerFailurePayment.ftl";
	public static final String TRIAL_BUYER_CREATION_TEMPLATE = "trialBuyerCreation.ftl";
	public static final String BUYER_PROFILE_SETUP = "buyerProfileSetup.ftl";
	public static final String TRIAL_SUBMITTION_NOTIFICATION_TEMPLATE = "freeTrialSubmitionNotification.ftl";
	public static final String CLOSE_ACCOUNT_TEMPLATE = "closeAccount.ftl";
	public static final String FINANCECOMPANY_INVIATION_TEMPLATE = "financeCompanyRegistraion.ftl";
	public static final String ANNOUNCEMNT_EMAIL_TEMPLATE = "emailAnnouncement.ftl";
	public static final String REQUEST_ASSOCIATE_BUYER = "requestAssocitedBuyer.ftl";
	public static final String REQUEST_ASSOCIATE_BUYER_REJECT = "requestAssocitedBuyerReject.ftl";
	public static final String REQUEST_ASSOCIATE_BUYER_ACCEPT = "requestAssocitedBuyerApprove.ftl";
	public static final String ADDED_FAV_SUPPLIER = "addedFavouriteSuppler.ftl";
	public static final String BUDGET_APPROVAL_TEMPLATE = "budgetApproved.ftl";
	public static final String BUDGET_REJECT_TEMPLATE = "budgetRejected.ftl";
	public static final String BUDGET_UTILIZED_TEMPLATE = "budgetUtilized.ftl";
	public static final String BUDGET_OVERRUN_TEMPLATE = "budgetOverrun.ftl";
	public static final String BUDGET_APPROVAL_REQUEST_TEMPLATE = "budgetApprovalRequest.ftl";
	public static final String PO_ACCEPT_REJECT_TEMPLATE = "poAcceptedRejected.ftl";
	public static final String PO_CANCEL_TEMPLATE = "poCancel.ftl";
	public static final String PO_RECEIVED_TEMPLATE = "poReceived.ftl";
	public static final String DO_RECEIVED_TEMPLATE = "doReceived.ftl";
	public static final String INVOICE_RECEIVED_TEMPLATE = "invoiceReceived.ftl";
	public static final String DO_CANCELLED_TEMPLATE = "doCancelled.ftl";
	public static final String INVOICE_CANCELLED_TEMPLATE = "invoiceCancelled.ftl";
	public static final String DO_ACCEPTED_TEMPLATE = "doAccepted.ftl";
	public static final String INVOICE_ACCEPTED_TEMPLATE = "invoiceAccepted.ftl";
	public static final String DO_DECLINE_TEMPLATE = "doDeclined.ftl";
	public static final String INVOICE_DECLINE__TEMPLATE = "invoiceDeclined.ftl";
	public static final String SUPPLIER_FORM_ACCEPTED_TEMPLATE = "supplierFormAccepted.ftl";
	public static final String SUPPLIER_FORM_REVISED_TEMPLATE = "supplierFormRevised.ftl";
	public static final String SUPPLIER_FORM_ASSIGNED_TEMPLATE = "supplierFormAssigned.ftl";
	public static final String APPROVEL_REMINDER_TEMPLATE = "approvalReminder.ftl";
	public static final String PR_APPROVEL_REMINDER_TEMPLATE = "prApprovalReminder.ftl";
	public static final String RFS_APPROVEL_REMINDER_TEMPLATE = "rfsApprovalReminder.ftl";
	public static final String SUPPLIER_FORM_APP_REJECT_TEMPLATE = "supplierFormRejectedEmail.ftl";
	public static final String SUPPLIER_FORM_APPROVED_TEMPLATE = "supplierFormApprovedEmail.ftl";
	public static final String SUPP_FORM_APPROVAL_REQUEST_TEMPLATE = "supplierFormApprovalrequest.ftl";
	public static final String SUPPLIER_FORM_SUBMITTED_TEMPLATE = "supplierFormSubmitted.ftl";
	public static final String EVENT_OWNER_APPROVAL_REMINDER_TEMPLATE = "eventOwnerApprovalReminder.ftl";
	public static final String PR_OWNER_APPROVAL_REMINDER_TEMPLATE = "prOwnerApprovalReminder.ftl";
	public static final String RFS_OWNER_APPROVAL_REMINDER_TEMPLATE = "rfsOwnerApprovalReminder.ftl";
	public static final String RFX_EVENTPARTICIPATION_FEE_RECEIPT_TEMPLATE = "EventParticipationReceipt.ftl";
	public static final String ERP_PO_ERROR_TEMPLATE = "erpPoCreationError.ftl";
	public static final String CONTRACT_EXPIRY_REMINDER_TEMPLATE = "contractExpiryReminderSubscription.ftl";
	public static final String GRN_ACCEPTED_TEMPLATE = "grnAccepted.ftl";
	public static final String GRN_DECLINE_TEMPLATE = "grnDeclined.ftl";
	public static final String COMPANY_NAME_CHANGED_TEMPLATE = "companyNameChanged.ftl";
	public static final String REGISTRATION_NUMBER_CHANGED_TEMPLATE = "regNoChanged.ftl";
	public static final String SUSPEND_EVENT_APPROVAL_REQUEST_TEMPLATE = "suspendEventApprovalRequest.ftl";
	public static final String SUSPEND_EVENT_REJECT_TEMPLATE = "suspendedEventRejected.ftl";
	public static final String SUSPENDED_EVENT_APPROVAL_TEMPLATE = "suspendedEventApproved.ftl";
	public static final String BUYER_SUPPLIER_CREATION = "buyerSuppliercreated.ftl";
	public static final String TAT_REPORT_EMAIL_TEMPLATE = "tatReportTemplate.ftl";
	public static final String REVISE_PR_APPROVAL_REQUEST_TEMPLATE = "revisePoApprovalRequest.ftl";
	public static final String REVISE_PO_REJECT_TEMPLATE = "revisePrRejected.ftl";
	public static final String REVISE_PO_APPROVED_TEMPLATE = "revisePoApproved.ftl";

	public static final String PO_REJECTED_TEMPLATE = "poRejected.ftl";
	public static final String PO_APPROVED_TEMPLATE = "poApproved.ftl";

	public static final String PO_REJECTED_TO_OWNER_TEMPLATE = "poRejectedToOwner.ftl";
	public static final String PO_APPROVED_TO_OWNER_TEMPLATE = "poApprovedToOwner.ftl";

	public static final String PO_APPROVAL_REQUEST_TEMPLATE = "poApprovalRequest.ftl";
	public static final String PO_REVISE_APPROVAL_REQUEST_TEMPLATE = "poReviseApprovalRequest.ftl";
	public static final String PO_CANCEL_APPROVAL_REQUEST_TEMPLATE = "poCancelApprovalRequest.ftl";

	public static final String SUSPEND_PO_TEMPLATE = "suspendPo.ftl";
	public static final String SUSPEND_PO_SUPPLIER_TEMPLATE = "suspendPoToSupplier.ftl";

	public static final String PO_EVENT_MAILBOX_TEMPLATE = "poEventMailBoxTemplate.ftl";

	public static final String REVISE_PO_TEMPLATE = "revisePo.ftl";
	public static final String REVISE_PO_FINISHED_TEMPLATE = "reviseFinishedPo.ftl";
	public static final String REVISE_PO_RECEIVED_TEMPLATE = "revisePoReceived.ftl";
	public static final String REVISE_PO_APPROVAL_REMINDER_TEMPLATE = "revisePoAppReminder.ftl";
	public static final String REVISE_PO_APPROVAL_INACTION_TEMPLATE = "revisePoAppInaction.ftl";
	public static final String CONTRACT_APPROVAL_REQUEST = "contractApprovalRequest.ftl";
	public static final String CONTRACT_APPROVED = "contractApproved.ftl";
	public static final String CONTRACT_REJECTED = "contractRejected.ftl";
	public static final String CONTRACT_APPROVAL_REMINDER = "contractApprovalReminder.ftl";
	public static final String CONTRACT_APPROVAL_REMINDER_COMPLETE = "contractApprovalReminderComplete.ftl";
	public static final String CONTRACT_TERMINATION_APPROVAL_REQUEST = "contractTerminationApprovalRequest.ftl";
	public static final String CONTRACT_TERMINATION_APPROVED = "contractTerminationApproved.ftl";
	public static final String CONTRACT_TERMINATION_REJECTED = "contractTerminationRejected.ftl";
	public static final String TEAM_MEMBER_TEMPLATE_CONTRACT = "teamMemberInvContract.ftl";
	public static final String TEAM_MEMBER_TEMPLATE_CONTRACT_UPDATE = "teamMemberUpdateContract.ftl";
	
	public static final String ASSIGNED_FORM_OWNER_FOR_SUPPLIER_PERFORMANCE_FORM = "assignedFormOwnerForSupplierPerformanceForm.ftl";
	public static final String EVALUATION_SUPPLIER_PERFORMANCE_REMINDER = "evaluationSupplierPerformanceReminder.ftl";
	public static final String EMAIL_NOTIFICATION_TO_FORM_OWNER = "emailNotificationToFormOwner.ftl";
	public static final String EVALUATION_FORM_APPROVAL_REQUEST = "evaluationFormApprovalRequest.ftl";
	public static final String EVALUATION_FORM_APPROVED = "evaluationFormApproved.ftl";
	public static final String EVALUATION_FORM_CREATED = "evaluationFormCreated.ftl";
	public static final String EVALUATION_FORM_REJECTED = "evaluationFormRejected.ftl";
	public static final String EVALUATION_FORM_RESUMED = "evaluationFormResumed.ftl";
	public static final String EVALUATION_FORM_SUSPENDED = "evaluationFormSuspended.ftl";
	public static final String EVALUATION_SUPPLIER_PERFORMANCE_ENDED = "evaluationSupplierPerformanceEnded.ftl";
	public static final String EVALUATION_SUPPLIER_PERFORMANCE_STARTED = "evaluationSupplierPerformanceStarted.ftl";
	public static final String NOTIFICATION_FOR_EVALUATOR = "notificationForEvaluator.ftl";
	public static final String SCORE_CARD_PDF_TEMPLATE = "scoreCardPdfTemplate.ftl";
	
	public static final String AWARD_EVENT_REJECT_TEMPLATE = "awardEventRejected.ftl";
	public static final String AWARD_EVENT_APPROVAL_REQUEST_TEMPLATE = "awardEventApprovalRequest.ftl";
	public static final String AWARD_APPROVAL_REQUEST_APPROVAL = "awardEventApproved.ftl";
	public static final String CONTRACT_CREATION_MAIL = "contractCreationMail.ftl";
	
	
	public static final String[] BUYER_DEFAULT_SYSTEM_ADMIN_SETTING_ACL_VALUES = new String[] { "ROLE_SETTINGS", "ROLE_BUYER_SETTINGS", "ROLE_UOM_LIST", "ROLE_UOM_EDIT", "ROLE_BUYER_INDUSTRY_CATEGORY_LIST", "ROLE_BUYER_INDUSTRY_CATEGORY_EDIT", "ROLE_COST_CENTER_LIST", "ROLE_COST_CENTER_EDIT", "ROLE_BUYER_ADDRESS_LIST", "ROLE_BUYER_ADDRESS_EDIT", "ROLE_PRODUCT_CATEGORY_LIST", "ROLE_PRODUCT_CATEGORY_EDIT", "ROLE_PRODUCT_LIST", "ROLE_PRODUCT_EDIT", "ROLE_MESSAGE_BOX", "ROLE_MESSAGE_BOX_EDIT", "ROLE_SUPPLIER_LIST", "ROLE_SUPPLIER_VIEW_ONLY", "ROLE_AUDITTRAIL_LIST", "ROLE_EVENT_TEMPLATE_LIST", "ROLE_EVENT_TEMPLATE_EDIT", "ROLE_PR_TEMPLATE_LIST", "ROLE_PR_TEMPLATE_LIST", "ROLE_BUYER_FAV_SUPPLIER_LIST", "ROLE_BUYER_FAV_SUPPLIER_EDIT", "ROLE_BUYER_BILLING", "ROLE_PR_PO", "ROLE_VIEW_PR_DRAFT", "ROLE_BUSINESS_UNIT","ROLE_PROC_TO_PAY" };
	public static final String[] BUYER_DEFAULT_USER_ADMIN_ACL_VALUES = new String[] { "ROLE_USER_ADMINISTRATION", "ROLE_USER_LIST", "ROLE_USER_EDIT", "ROLE_USER_ROLE_LIST", "ROLE_USER_ROLE_EDIT" };
	public static final String[] BUYER_DEFAULT_ACCOUNT_ADMIN_ACL_VALUES = new String[] { "ROLE_PROFILE_SETTINGS", "ROLE_SUBSCRIPTION_DETAILS", "ROLE_COMPANY_DETAILS", "ROLE_PAYMENT_DETAILS" };
	public static final String[] BUYER_DEFAULT_USER_ACL_VALUES = new String[] { "ROLE_USER" };
	public static final String[] BUYER_DEFAULT_EVENT_PR_CREATOR_ACL_VALUES = new String[] { "ROLE_SUPPLIER_LIST", "ROLE_SUPPLIER_VIEW_ONLY", "ROLE_RFT_LIST", "ROLE_RFT_CREATE", "ROLE_EVENTS", "ROLE_PR_CREATE", "ROLE_RFP_CREATE", "ROLE_RFQ_CREATE", "ROLE_RFI_CREATE", "ROLE_RFA_CREATE", "ROLE_BUYER_FAV_SUPPLIER_LIST", "ROLE_BUYER_FAV_SUPPLIER_VIEW_ONLY", "ROLE_PR_PO", "ROLE_VIEW_PO_LIST", "ROLE_VIEW_PR_DRAFT","PROC_TO_PAY","ROLE_PO_CREATE" };
	public static final String[] BUYER_DEFAULT_EVENT_CREATOR_ACL_VALUES = new String[] { "ROLE_SUPPLIER_LIST", "ROLE_SUPPLIER_VIEW_ONLY", "ROLE_RFT_LIST", "ROLE_RFT_CREATE", "ROLE_EVENTS", "ROLE_RFP_CREATE", "ROLE_RFQ_CREATE", "ROLE_RFI_CREATE", "ROLE_RFA_CREATE", "ROLE_BUYER_FAV_SUPPLIER_LIST", "ROLE_BUYER_FAV_SUPPLIER_VIEW_ONLY" };
	public static final String[] BUYER_DEFAULT_PR_CREATE_ACL_VALUES = new String[] { "ROLE_EVENTS", "ROLE_PR_CREATE", "ROLE_SUPPLIER_LIST", "ROLE_SUPPLIER_VIEW_ONLY", "ROLE_BUYER_FAV_SUPPLIER_LIST", "ROLE_BUYER_FAV_SUPPLIER_VIEW_ONLY", "ROLE_PR_PO", "ROLE_VIEW_PO_LIST", "ROLE_VIEW_PR_DRAFT","ROLE_PROC_TO_PAY","ROLE_PO_CREATE" };
	public static final String[] BUYER_DEFAULT_AUCTION_CREATE_ACL_VALUES = new String[] { "ROLE_SUPPLIER_LIST", "ROLE_SUPPLIER_VIEW_ONLY", "ROLE_EVENTS", "ROLE_RFA_CREATE" };
	public static final String[] BUYER_DEFAULT_RFX_CREATE_ACL_VALUES = new String[] { "ROLE_SUPPLIER_LIST", "ROLE_SUPPLIER_VIEW_ONLY", "ROLE_EVENTS", "ROLE_RFT_CREATE", "ROLE_RFP_CREATE", "ROLE_RFI_CREATE", "ROLE_RFQ_CREATE", "ROLE_BUYER_FAV_SUPPLIER_LIST", "ROLE_BUYER_FAV_SUPPLIER_VIEW_ONLY" };
	public static final String[] BUYER_DEFAULT_READONLY_ADMIN_ACL_VALUES = new String[] { "ROLE_USER", "ROLE_ADMIN_READONLY" };

	public static final String[] SUPPLIER_DEFAULT_USER_ACL_VALUES = new String[] { "ROLE_USER" };
	public static final String[] BUYER_DEFAULT_APPROVER_USER_ACL_VALUES = new String[] { "ROLE_USER", "ROLE_BUYER_USER_APPROVER" };
	public static final String[] BUYER_DEFAULT_REQUESTER_USER_ACL_VALUES = new String[] { "ROLE_USER", "ROLE_BUYER_USER_REQUESTER", "ROLE_REQUEST_CREATE" };

	public static final String[] COST_CENTER_EXCEL_COLUMNS = new String[] { "COST CENTER", "DESCRIPTION", "STATUS" };
	public static final String[] PRODUCT_CATEGORY_EXCEL_COLUMNS = new String[] { "CATEGORY CODE", "CATEGORY NAME", "STATUS" };
	public static final String[] PRODUCT_ITEM_EXCEL_COLUMNS = new String[] { "INTERFACE CODE", "PRODUCT NAME", "UOM", "PRODUCT CATEGORY", "SUPPLIER", "UNIT PRICE", "TAX (%)", "ITEM TYPE", "GL CODE", "UNSPSC CODE", "STATUS", "REMARKS", "HISTORIC PRICING REFNO", "PURCHASE GROUP CODE", "BRAND", "START DATE", "END DATE", "CONTRACT REFERENCE NUMBER" };

	public static final String[] UOM_EXCEL_COLUMNS = new String[] { "UID", "UOM", "DESCRIPTION", "STATUS" };
	public static final String[] INDUSTRY_CATEGORY_EXCEL_COLUMNS = new String[] { "UID", "CATEGORY CODE", "INDUSTRY CATEGORY", "STATUS" };
	public static final String[] BUSINESS_UNIT_EXCEL_COLUMNS = new String[] { "UID", "BUSINESS UNIT NAME", "UNIT CODE", "DISPLAY NAME", "PARENT BUSINESS UNIT", "PARENT BUSINESS UNIT CODE", "LINE 1", "LINE 2", "LINE 3", "LINE 4", "LINE 5", "LINE 6", "LINE 7", "STATUS", "BUDGET CHECK", "SPM Integration" };
	public static final String[] BUYER_ADDRESS_EXCEL_COLUMNS = new String[] { "UID", "TITLE", "ADDRESS LINE 1", "ADDRESS LINE 2", "CITY/TOWN", "COUNTRY", "STATE/PROVINCE", "ZIP CODE", "STATUS" };
	public static final String[] USER_EXCEL_COLUMNS = new String[] { "LOGIN ID", "USER NAME", "Communication Email", "Designation", "Contact No", "User Type", "User Role", "STATUS", "ASSIGN RFX TEMPLATE", "ASSIGN PR TEMPLATE", "ACCOUNT LOCKED", "Last Login Date", "Last Failed Login" };
	public static final String[] SUBSCRIPTION_HISTORY_COLUMNS = new String[] { "PLAN NAME", "USER LIMIT", "EVENT LIMIT", "START DATE", "END DATE", "AMOUNT", "STATUS", "PLAN TYPE" };
	public static final String[] PAYMENT_TRANSACTION_COLUMNS = new String[] { "PLAN NAME", "PAYMENT_TIME", "REMARK", "REFERENCE ID", "AMOUNT", "SUSCRIPTION DISCOUNT", "PROMOTIONAL DISCOUNT", "ADITIONAL TAX", "TOTAL AMOUNT", "CURRENCY", "PROMOTIONAL CODE" };
	public static final String[] EVENT_AWARD_EXCEL_COLUMNS = new String[] { "NO", "ITEM NAME", "SUPPLIER NAME", "SUPPLIER PRICE", "AWARDED PRICE", "TAX", "TAX VALUE", "TOTAL PRICE", "REF. NO." };

	public static final String[] EVENT_REPORT_EXCEL_COLUMNS = new String[] { "Name of Event", "Reference Number", "Event Id", "Event Start Date", "Event End Date", "Event Type", "Event Owner", "Business Unit", "Status" };
	public static final String[] ALL_EVENT_REPORT_EXCEL_COLUMNS = new String[] { "Event Reference ID", "Event Reference Number", "Complete Name", "Event Description", "Event Owner", "Event Publish Date & Time", "Event Start Date & Time", "Event End Date & Time", "Delivery Date", "Event Visibility", "Validity Days", "Event Type", "Base Currency", "Business Unit", "Cost Center", "Status", "Leading Vendor", "Leading Amount", "Invited Vendors", "Accepted Vendors", "Submitted Vendors", "Estimated Budget", "Historic Amount", "Template Name Used", "Associate Owner", "Unmasking Owner", "Delivery Address", "Auction Type", "Event Fees", "Event Deposit", " Previewed Suppliers", "Rejected Suppliers", "Disqualified Suppliers", "Concluded", "Concluded Date and Time", "Push to Event", "Pushed Event Date and Time", "Push to PR", "PR Pushed Date", "Awarded Date and Time", "Average Price of Submissions", "Event Categories" };

	public static final String X_AUTH_KEY_HEADER_PROPERTY = "X-AUTH-KEY";

	public static final String[] FINANCE_DEFAULT_USER_ADMIN_ACL_VALUES = new String[] { "ROLE_USER_ADMINISTRATION", "ROLE_USER_LIST", "ROLE_USER_EDIT", "ROLE_USER_ROLE_LIST", "ROLE_USER_ROLE_EDIT" };
	public static final String[] FINANCE_DEFAULT_SYSTEM_ADMIN_SETTING_ACL_VALUES = new String[] { "ROLE_SETTINGS", "ROLE_MESSAGE_BOX", "ROLE_MESSAGE_BOX_EDIT", "ROLE_SUPPLIER_LIST", "ROLE_SUPPLIER_VIEW_ONLY" };
	public static final String[] FINANCE_DEFAULT_USER_ACL_VALUES = new String[] { "ROLE_USER" };

	public static final String BUCKET_ITEM_LIST = "bucketItemList";
	public static final String BUCKET_ITEM_JSON = "bucketItemJson";

	public static final String[] AUCTION_EVENT_REPORT_REVERSE_EXCEL_COLUMNS = new String[] { "Business Unit", "Event Categories", "Reference Number", "Event ID", "Event Start Date", "Event End Date", "Auction Name", "Template Name", "Event Type", "Event Owner", "Auction Type", "Invited Suppliers", "Self-Invited Suppliers", "Accepted Supplier", "Submmited Suppliers", "Participation Ratio", "Total Bids Number", "Currency Code", "Leading Bid", "Leading Supplier", "Self-Invited", "Supplier Tags", "Awarded Price", "Budget Amount", "Historic Amount", "Savings Amount (Budget)", "Savings Percentage (Budget)", "Savings Amount (Historic)", "Savings Percentage (Historic)" };

	public static final String[] ALL_SOURCING_REPORT_EXCEL_COLUMNS = new String[] { "Template Name", "Sourcing Request ID", "Reference Number", "Sourcing Request Name", "Description", "Sourcing Request Owner", "Created Date", "Business Unit", "Cost Center", "Available Budget", "Estimated Budget", "Group Code", "Status" };

	public static final String[] ALL_SOURCING_REPORT_EXCEL_COLUMNS_CUR = new String[] { "Template Name", "Sourcing Request ID", "Reference Number", "Sourcing Request Name", "Description", "Sourcing Request Owner", "Created Date", "Submitted Date", "Approved Date", "Approval Days", "Approval Levels", "Total Approvers", "Business Unit", "Cost Center", "Base Currency", "Available Budget", "Estimated Budget", "Group Code", "Status" };

	public static final String[] AUCTION_EVENT_REPORT_FORWARD_EXCEL_COLUMNS = new String[] { "Business Unit", "Event Categories", "Reference Number", "Event ID", "Event Start Date", "Event End Date", "Auction Name", "Template Name", "Event Type", "Event Owner", "Auction Type", "Invited Suppliers", "Self-Invited Suppliers", "Accepted Supplier", "Submmited Suppliers", "Participation Ratio", "Total Bids Number", "Currency Code", "Leading Bid", "Leading Supplier", "Self-Invited", "Supplier Tags", "Awarded Price", "Budget Amount", "Historic Amount", "Gains Amount (Budget)", "Gains Percentage (Budget)", "Gains Amount (Historic)", "Gains Percentage (Historic)" };

	public static final String BUDGET_PLANNER = "budgetPlannerLog";
	public static final String[] TRANSACTION_LOGS = new String[] { "REFERENCE NUMBER", "TRANSACTION DATE/TIME", "BUSINESS UNIT", "COST CENTER", "NEW", "ADD", "DEDUCT", "TRANSFER FROM BUSINESS UNIT", "TRANSFER TO BUSINESS UNIT", "PURCHASE ORDER", "STATUS", "LOCKED", "PR BASE CURRENCY", "BUDGET CURRENCY", "CONVERSION RATE", "AMOUNT AFTER CONVERSION", "REMAINING AMOUNT" };

	public static final String[] SUPPLIER_PO_REPORT_EXCEL_COLUMNS = new String[] { "SR NO", "PO NUMBER", "PO NAME", "BUYER", "BUSINESS UNIT", "PO CREATED DATE", "PO ACCEPT/DECLINE DATE", "PO CURRENCY", "PO GRAND TOTAL", "PO STATUS" };
	public static final String[] PO_EXCEL_COLUMNS = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "PO QUANTITY", "LOCKED QUANTITY","BALANCE QUANTITY","UNIT PRICE","PRICE PER UNIT", "TAX (%)" };
	public static final String[] PO_EXCEL_COLUMNS_IN_DRAFT = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "PO QUANTITY", "UNIT PRICE","PRICE PER UNIT", "TAX (%)" };
	public static final String[] BUYER_PO_REPORT_EXCEL_COLUMNS = new String[] { "SR NO", "PO NUMBER", "PO NAME", "PO STATUS", "SUPPLIER", "BUSINESS UNIT" , "PO CREATED BY", "PO CREATED DATE", "PO ORDERED BY", "PO ORDERED DATE", "PO REVISION DATE","PO ACCEPT/DECLINE DATE", "PO CURRENCY", "PO GRAND TOTAL", "PR NUMBER" };
	public static final String[] FORM_EXCEL_COLUMNS = new String[] { "SR No", "ITEM NAME", "ITEM DESCRIPTION", "QUESTION TYPE", "ATTACHMENT", "REQUIRED", "ATTACHMENT REQUIRED", "OPTION-1", "OPTION-2", "OPTION-3", "OPTION-4", "OPTION-5", "OPTION-6", "OPTION-7", "OPTION-8", "OPTION-9", "OPTION-10", "OPTION-11", "OPTION-12", "OPTION-13", "OPTION-14", "OPTION-15", "OPTION-16", "OPTION-17", "OPTION-18", "OPTION-19", "OPTION-20", "OPTION-21", "OPTION-22", "OPTION-23", "OPTION-24", "OPTION-25", "OPTION-26", "OPTION-27", "OPTION-28", "OPTION-29", "OPTION-30" };
	public static final String[] FORM_EXCEL_OPTION_COLUMNS = new String[] { "OPTION-1", "OPTION-2", "OPTION-3", "OPTION-4", "OPTION-5", "OPTION-6", "OPTION-7", "OPTION-8", "OPTION-9", "OPTION-10", "OPTION-11", "OPTION-12", "OPTION-13", "OPTION-14", "OPTION-15", "OPTION-16", "OPTION-17", "OPTION-18", "OPTION-19", "OPTION-20", "OPTION-21", "OPTION-22", "OPTION-23", "OPTION-24", "OPTION-25", "OPTION-26", "OPTION-27", "OPTION-28", "OPTION-29", "OPTION-30" };

	public static final String[] ALL_SUPLLIER_INVOICE_REPORT_EXCEL_COLUMNS = new String[] { "Invoice Number", "Reference Number", "Invoice Name", "PO Number", "Buyer", "Business Unit", "Created By", "Created Date", "Invoice Date", "Accept/Decline Date", "Currency", "Grand Total", "Status" };

	public static final String[] ALL_BUYER_INVOICE_REPORT_EXCEL_COLUMNS = new String[] { "Invoice Number", "Invoice Name", "PO Number", "Supplier", "Business Unit", "Invoice Date", "Accept/Decline Date", "Currency", "Grand Total", "Status" };

	public static final String[] ALL_SUPLLIER_DO_REPORT_EXCEL_COLUMNS = new String[] { "DO Number", "Reference Number", "DO Name", "PO Number", "Buyer", "Business Unit", "Created By", "Created Date", "DO Date", "Accept/Decline Date", "Currency", "Grand Total", "Status" };

	public static final String[] ALL_BUYER_DO_REPORT_EXCEL_COLUMNS = new String[] { "DO Number", "DO Name", "PO Number", "Supplier", "Business Unit", "DO Date", "Accept/Decline Date", "Currency", "Grand Total", "Status" };

	public static final String[] ALL_BUYER_GRN_REPORT_EXCEL_COLUMNS = new String[] { "GRN Number", "GRN Name", "Reference Number", "PO Number", "Supplier", "Business Unit", "Created By", "Created Date", "Goods Receipt Date", "GRN Date", "Currency", "Grand Total", "Status" };

	public static final String[] ALL_SUPPLIER_GRN_REPORT_EXCEL_COLUMNS = new String[] { "GRN Id", "GRN Title", "Buyer", "Created Date", "Accept/Decline Date", "Currency", "Grand Total", "Status" };

	public static final String STRIPE_STATUS_SUCCESS = "succeeded";
	public static final String STRIPE_STATUS_PROCESSING = "processing";
	public static final String STRIPE_STATUS_REQUIRES_ATTENTION = "requires_payment_method";

	public static final String[] PRODUCT_ITEM_CSV_COLUMN_HEADINGS = new String[] { "INTERFACE CODE", "PRODUCT NAME", "UOM", "PRODUCT CATEGORY", "SUPPLIER", "UNIT PRICE", "TAX (%)", "ITEM TYPE", "GL CODE", "UNSPSC CODE", "STATUS", "REMARKS", "HISTORIC PRICING REFNO", "PURCHASE GROUP CODE", "BRAND", "START DATE", "END DATE", "CONTRACT REFERENCE NUMBER" };

	public static final String[] ALL_EVENT_REPORT_CSV_COLUMNS = new String[] { "EVENT REFERENCE ID", "EVENT REFERENCE NUMBER", "COMPLETE NAME", "EVENT DESCRIPTION", "EVENT OWNER", "EVENT PUBLISH DATE & TIME", "EVENT START DATE & TIME", "EVENT END DATE & TIME", "DELIVERY DATE", "EVENT VISIBILITY", "VALIDITY DAYS", "EVENT TYPE", "BASE CURRENCY", "BUSINESS UNIT", "COST CENTER", "GROUP CODE", "PROCUREMENT METHOD", "PROCUREMENT CATEGORY", "STATUS", "LEADING VENDOR", "LEADING AMOUNT", "INVITED VENDORS", "ACCEPTED VENDORS", "SUBMITTED VENDORS", "AVAILABLE BUDGET", "ESTIMATED BUDGET", "HISTORIC AMOUNT", "TEMPLATE NAME USED", "ASSOCIATE OWNER", "UNMASKING OWNER", "DELIVERY ADDRESS", "AUCTION TYPE", "EVENT FEES", "EVENT DEPOSIT", " PREVIEWED SUPPLIERS", "REJECTED SUPPLIERS", "DISQUALIFIED SUPPLIERS", "CONCLUDED", "CONCLUDED DATE AND TIME", "PUSH TO EVENT", "PUSHED EVENT DATE AND TIME", "PUSH TO PR", "PR PUSHED DATE", "AWARDED DATE AND TIME", "AVERAGE PRICE OF SUBMISSIONS", "EVENT CATEGORIES" };

	public static final String[] AUCTION_EVENT_REPORT_FORWARD_CSV_COLUMNS = new String[] { "BUSINESS UNIT", "EVENT CATEGORIES", "REFERENCE NUMBER", "EVENT ID", "EVENT START DATE", "EVENT END DATE", "AUCTION NAME", "TEMPLATE NAME", "EVENT TYPE", "EVENT OWNER", "AUCTION TYPE", "INVITED SUPPLIERS", "SELF-INVITED SUPPLIERS", "ACCEPTED SUPPLIER", "SUBMMITED SUPPLIERS", "PARTICIPATION RATIO", "TOTAL BIDS NUMBER", "CURRENCY CODE", "LEADING BID", "LEADING SUPPLIER", "SELF-INVITED", "SUPPLIER TAGS", "AWARDED PRICE", "BUDGET AMOUNT", "HISTORIC AMOUNT", "GAINS AMOUNT (BUDGET)", "GAINS PERCENTAGE (BUDGET)", "GAINS AMOUNT (HISTORIC)", "GAINS PERCENTAGE (HISTORIC)" };

	public static final String[] AUCTION_EVENT_REPORT_REVERSE_CSV_COLUMNS = new String[] { "BUSINESS UNIT", "EVENT CATEGORIES", "REFERENCE NUMBER", "EVENT ID", "EVENT START DATE", "EVENT END DATE", "AUCTION NAME", "TEMPLATE NAME", "EVENT TYPE", "EVENT OWNER", "AUCTION TYPE", "INVITED SUPPLIERS", "SELF-INVITED SUPPLIERS", "ACCEPTED SUPPLIER", "SUBMMITED SUPPLIERS", "PARTICIPATION RATIO", "TOTAL BIDS NUMBER", "CURRENCY CODE", "LEADING BID", "LEADING SUPPLIER", "SELF-INVITED", "SUPPLIER TAGS", "AWARDED PRICE", "BUDGET AMOUNT", "HISTORIC AMOUNT", "SAVINGS AMOUNT (BUDGET)", "SAVINGS PERCENTAGE (BUDGET)", "SAVINGS AMOUNT (HISTORIC)", "SAVINGS PERCENTAGE (HISTORIC)" };

	public static final String[] SOURCING_REPORT_CSV_COLUMNS = new String[] { "TEMPLATE NAME", "SOURCING REQUEST ID", "REFERENCE NUMBER", "SOURCING REQUEST NAME", "DESCRIPTION", "SOURCING REQUEST OWNER", "CREATED DATE", "SUBMITTED DATE", "APPROVED DATE", "APPROVAL DAYS", "APPROVAL LEVELS", "TOTAL APPROVERS", "BUSINESS UNIT", "COST CENTER", "BASE CURRENCY", "AVAILABLE BUDGET", "ESTIMATED BUDGET", "GROUP CODE", "STATUS" };

	public static final String[] DOWNLOAD_SUPPLIER_CSV_COLUMNS = new String[] { "COMPANY NAME", "COUNTRY CODE", "REGISTRATION NUMBER", "CONTACT FULL NAME", "DESIGNATION", "MOBILE NUMBER", "CONTACT NUMBER", "LOGIN EMAIL", "FAX NUMBER", "INDUSTRY CATEGORY", "PRODUCT CATEGORY", "SUPPLIER CODE", "STATUS", "RATINGS", "COMMUNICATION EMAIL", "YEAR OF ESTABLISHED", "REGISTRATION DATE", "SUPPLIER TAGS" };

	public static final String[] UOM_REPORT_CSV_COLUMNS = new String[] { "UID", "UOM", "DESCRIPTION", "STATUS" };

	public static final String[] BUYER_DO_REPORT_CSV_COLUMNS = new String[] { "DO NUMBER", "DO NAME", "PO NUMBER", "SUPPLIER", "BUSINESS UNIT", "DO DATE", "ACCEPT/DECLINE DATE", "CURRENCY", "GRAND TOTAL", "STATUS" };

	public static final String[] SUPLLIER_DO_REPORT_CSV_COLUMNS = new String[] { "DO NUMBER", "REFERENCE NUMBER", "DO NAME", "PO NUMBER", "BUYER", "BUSINESS UNIT", "CREATED BY", "CREATED DATE", "DO DATE", "ACCEPT/DECLINE DATE", "CURRENCY", "GRAND TOTAL", "STATUS" };

	public static final String[] BUYER_INVOICE_REPORT_EXCEL_COLUMNS = new String[] { "INVOICE NUMBER", "INVOICE NAME", "PO NUMBER", "SUPPLIER", "BUSINESS UNIT", "INVOICE DATE", "ACCEPT/DECLINE DATE", "CURRENCY", "GRAND TOTAL", "STATUS" };

	public static final String[] SUPLLIER_INVOICE_REPORT_EXCEL_COLUMNS = new String[] { "INVOICE NUMBER", "REFERENCE NUMBER", "INVOICE NAME", "PO NUMBER", "BUYER", "BUSINESS UNIT", "CREATED BY", "CREATED DATE", "INVOICE DATE", "ACCEPT/DECLINE DATE", "CURRENCY", "GRAND TOTAL", "STATUS" };

	public static final String[] INDUSTRY_CATEGORY_CSV_COLUMNS = new String[] { "UID", "CATEGORY CODE", "INDUSTRY CATEGORY", "STATUS" };

	public static final String[] BUSINESS_UNIT_REPORT_CSV_COLUMNS = new String[] { "UID", "BUSINESS UNIT NAME", "UNIT CODE", "DISPLAY NAME", "PARENT BUSINESS UNIT", "PARENT BUSINESS UNIT CODE", "LINE1", "LINE2", "LINE3", "LINE4", "LINE5", "LINE6", "LINE7", "STATUS", "BUDGET CHECK", "SPM Integration" };
	public static final String[] USER_CSV_COLUMNS = new String[] { "LOGIN ID", "USER NAME", "COMMUNICATION EMAIL", "DESIGNATION", "CONTACT NO", "USER TYPE", "USER ROLE", "STATUS", "ASSIGN RFX TEMPLATE", "ASSIGN PR TEMPLATE", "ACCOUNT LOCKED", "LAST LOGIN DATE", "LAST FAILED LOGIN" };

	public static final String[] ALL_PAYMENT_REPORT_EXCEL_COLUMNS = new String[] { "Company Name", "Country", "Transaction Id", "Amount", "Plan", "Transaction Time", "Status" };
	public static final String[] ALL_BUYER_REPORT_EXCEL_COLUMNS = new String[] { "Company Name", "Company Registration Number", "Company Type", "Year of Establishment", "Registration Date", "Account Status", "Company Address", "Country", "State", "Company Telephone Number", "Company Fax Number", "Company Website", "Primary Login Email", "Communication Email Address", "Primary Contact", "Primary Mobile Number", "Public Context Path", "Subscription Plan", "Subscription Validity", "No of Users", "No of Events" };
	public static final String[] ALL_SUPPLIER_REPORT_EXCEL_COLUMNS = new String[] { "Company Name", "Company Registration Number", "Company Type", "Year of Establishment", "Tax Registration Number", "Company Address", "Country", "State", "Company Telephone Number", "Company Fax Number", "Company Website", "Primary Login Email", "Communication Email Address", "Registration Date", "Approved Date", "Registration Completion Date", "Primary Contact", "Designation", "Primary Mobile Number", "Account Status", "Subscription Status", "Subscription Plan", "Promotional Codes Used", "Subscription Validity", "Associated Buyer", "Industry Sector", "Geographical Coverage", "Company Document", "Financial Information", "Organizational Details", "Track Record", "Other Documents", "Total Event Invited", "Total Event Participated", "Total Event Awarded" };

	public static final String[] CONTRACT_REPORT_CSV_COLUMNS = new String[] {"CONTRACT ID", "CONTRACT NAME", "EVENT ID", "REFERENCE NUMBER", "PREVIOUS CONTRACT NUMBER", "RENEWAL CONTRACT", "SUPPLIER", "SUPLLIER CODE", "BUSINESS UNIT", "BUSINESS UNIT CODE", "GROUP CODE", "PROCUREMENT CATEGORY", "AGREEMENT TYPE", "CURRENCY", "TOTAL CONTRACT VALUE", "CONTRACT START DATE", "CONTRACT END DATE", "EXPIRY REMINDER", "LOA DATE", "LOA DOCUMENT", "AGREEMENT DATE", "AGREEMENT DOCUMENT", "CONTRACT CREATOR", "CREATED DATE", "MODIFIED BY", "MODIFIED DATE", "STATUS" };

	public static final String[] PAYMENT_TERMES_EXCEL_COLUMNS = new String[] { "PAYMENT TERMS", "DESCRIPTION", "PAYMENT DAYS", "STATUS" };

	public static final String[] GROUP_CODE_CSV_COLUMNS = new String[] { "GROUP CODE", "DESCRIPTION", "STATUS" };

	public static final String[] SOURCING_REPORT_CSV_COLUMNS_EXPORT = new String[] { "TEMPLATE NAME", "SOURCING REQUEST ID", "REFERENCE NUMBER", "SOURCING REQUEST NAME", "DESCRIPTION", "SOURCING REQUEST OWNER", "CREATED DATE", "SUBMITTED DATE", "APPROVED DATE", "APPROVAL DAYS", "APPROVAL LEVELS", "TOTAL APPROVERS", "PROCUREMENT METHOD", "PROCUREMENT CATEGORY", "BUSINESS UNIT", "COST CENTER", "GROUP CODE", "BASE CURRENCY", "AVAILABLE BUDGET", "ESTIMATED BUDGET", "HISTORIC AMOUNT", "STATUS" };

	public static final String[] PROCUREMENT_METHOD_EXCEL_COLUMNS = new String[] { "PROCUREMENT METHOD CODE","PROCUREMENT METHOD", "DESCRIPTION", "STATUS" };

	public static final String[] PROCUREMENT_CATEGORIES_EXCEL_COLUMNS = new String[] { "PROCUREMENT CATEGORIES", "DESCRIPTION", "STATUS" };

	public static final String[] TAT_REPORT_CSV_COLUMNS = new String[] { "BUSINESS UNIT", "COST CENTER", "GROUP CODE", "REQUEST OWNER", "NAME OF REQUEST", "FORM DESCRIPTION", "SOURCING REQUEST ID", "REQUEST STATUS", "PROCUREMENT METHOD", "PROCUREMENT CATEGORY", "SAP PR ID", "REQUEST CREATED DATE", "REQUEST FINISH DATE", "AVAILABLE BUDGET", "ESTIMATED BUDGET", "REQUEST REJECT DATE", "REQUEST FIRST APPROVER DATE", "REQUEST LAST APPROVER DATE", "REQUEST COUNT DAYS TO APPROVE", "EVENT TYPE", "EVENT ID", "EVENT STATUS", "NAME OF EVENT", "EVENT CREATED DATE", "EVENT OWNER", "EVENT REFERENCE NUMBER", "TOTAL INVITED SUPPLIER", "COUNT SUPPLIER ACCEPT", "COUNT SUPPLIER SUBMIT", "EVENT FINISH DATE", "EVENT FIRST APPROVER DATE", "EVENT LAST APPROVER DATE", "EVENT REJECT DATE", "EVENT COUNT DAYS TO APPROVE", "EVENT PUBLISH DATE", "EVENT START DATE", "EVENT END DATE", "EVENT MEETING DATE", "EVENT OPEN DURATION", "FIRST ENVELOPE OPEN DATE", "EVALUATION COMPLETED DATE", "UNMASKING DATE", "EVENT CONCLUDE DATE", "AWARD DATE", "AWARDED SUPPLIER", "SAP PO ID", "SAP PO CREATION DATE", "AWARD VALUE", "PAPER APPROVAL DAYS", "OVERALL TAT" };

	public static final String[] SCORE_RATING_REPORT_CSV_COLUMNS = new String[] { "MINIMUM SCORE", "MAXIMUM SCORE", "RATING", "DESCRIPTION", "STATUS" };
	
	public static final String[] PERFORMANCE_EVALUATION_REPORT_CSV_COLUMNS = new String[] { "FORM ID", "FORM NAME", "REFERENCE NUMBER", "REFERENCE NAME", "FORM OWNER", "PROCUREMENT CATEGORY", "BUSINESS UNIT", "BUSINESS UNIT CODE", "SUPPLIER NAME", "SUPPLIER CODE", "SUPPLIER TAG", "TOTAL EVALUATOR", " TOTAL EVALUATION COMPLETED", "EVALUATION START DATE", "EVALUATION END DATE", "RECURRENT", "RECURRENCE EVALUATION", "NO OF RECURRENCE", "STATUS", "CONCLUDE DATE", "RATING", "OVERALL SCORE" };
	
	public static final String[] AGREEMENT_TYPE_EXCEL_COLUMNS = new String[] { "AGREEMENT TYPE", "DESCRIPTION", "STATUS" };

	public static final String[] AGREEMENT_TYPE_REPORT_CSV_COLUMNS = new String[] { "AGREEMENT TYPE", "DESCRIPTION", "STATUS" };
	public static final String[] BUYER_PO_ITEMS_REPORT_EXCEL_COLUMNS = new String[] { "SR NO", "PO NUMBER", "PO NAME", "PO STATUS", "SUPPLIER CODE", "SUPPLIER", "BUSINESS UNIT", "PO CREATED BY", "PO CREATED DATE", "PO ORDERED BY", "PO ORDERED DATE", "PO REVISION DATE", "PO ACCEPT/DECLINE DATE", "PRODUCT CODE", "PRODUCT CATEGORY", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "PO QUANTITY","LOCKED QUANTITY","BALANCE QUANTITY", "PO CURRENCY", "UNIT PRICE", "PRICE PER UNIT","TOTAL AMOUNT", "TAX(%)", "TAX AMOUNT", "TOTAL AMOUNT WITH TAX", "PR NUMBER"};
	public static final String[] SUPPLIER_PO_ITEM_REPORT_EXCEL_COLUMNS = new String[] { "SR NO", "PO NUMBER", "PO NAME","PO STATUS", "BUYER", "BUSINESS UNIT", "PO CREATED DATE", "PO ACCEPT/DECLINE DATE", "ITEM NAME", "ITEM DESCRIPTION", "UOM", "PO QUANTITY","LOCKED QUANTITY","BALANCE QUANTITY", "PO CURRENCY", "UNIT PRICE", "PRICE PER UNIT","TOTAL AMOUNT", "TAX(%)", "TAX AMOUNT", "TOTAL AMOUNT WITH TAX"};;

	//4113
	public static final String PO_FINISH_TEMPLATE = "poFinished.ftl";
	public static final String PO_FINISH_TEMPLATE_OWNER = "poFinishedOwner.ftl";
}
