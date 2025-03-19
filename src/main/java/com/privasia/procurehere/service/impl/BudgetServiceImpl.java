package com.privasia.procurehere.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.dao.BudgetDao;
import com.privasia.procurehere.core.dao.BudgetDocumentDao;
import com.privasia.procurehere.core.dao.BusinessUnitDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.entity.BudgetApproval;
import com.privasia.procurehere.core.entity.BudgetApprovalUser;
import com.privasia.procurehere.core.entity.BudgetComment;
import com.privasia.procurehere.core.entity.BudgetDocument;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.enums.IdSettingType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BudgetPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.supplier.dao.BuyerDao;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BudgetService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerAuditTrailService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.EventIdSettingsService;

/**
 * @author shubham
 */
@Service
@Transactional(readOnly = true)
public class BudgetServiceImpl implements BudgetService {

	@SuppressWarnings("unused")
	private static Logger LOG = LogManager.getLogger(Global.BUDGET_PLANNER);

	@Autowired
	BudgetDao budgetDao;

	@Autowired
	BuyerDao buyerDao;

	@Autowired
	UserDao userDao;

	@Autowired
	BudgetDocumentDao budgetDocumentDao;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	BuyerAuditTrailService buyerAuditTrailService;

	@Autowired
	EventIdSettingsDao eventIdSettingDao;

	@Autowired
	BusinessUnitDao businessUnitDao;
	
	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Override
	@Transactional(readOnly = false)
	public Budget saveBudget(Budget budget, BudgetPojo budgetPojo) throws ApplicationException {

		// update budget
		if (StringUtils.checkString(budgetPojo.getId()).length() > 0) {
			Budget persistBudget = budgetDao.findBudgetById(budgetPojo.getId());

			if (null != budgetPojo.getBudgetId()) {
				persistBudget.setBudgetId(budgetPojo.getBudgetId());
			}
			if (null != budgetPojo.getBudgetName()) {
				persistBudget.setBudgetName(budgetPojo.getBudgetName());
			}

			persistBudget.setModifiedDate(budget.getModifiedDate());
			persistBudget.setModifiedBy(budget.getModifiedBy());

			IdSettings idSettings = eventIdSettingsService.getIdSettingsByIdTypeForTenanatId(SecurityLibrary.getLoggedInUserTenantId(), "BG");
			if(IdSettingType.BUSINESS_UNIT != idSettings.getIdSettingType()) {
				if (null != budgetPojo.getBusinessUnit()) {
					BusinessUnit businessUnit = new BusinessUnit();
					businessUnit.setId(budgetPojo.getBusinessUnit().getId());
					persistBudget.setBusinessUnit(businessUnit);
				}
				
			}

			if (null != budgetPojo.getCostCenter()) {
				CostCenter costCenter = new CostCenter();
				costCenter.setId(budgetPojo.getCostCenter().getId());
				persistBudget.setCostCenter(costCenter);
			}

			if (null != budgetPojo.getValidFrom()) {
				persistBudget.setValidFrom(budgetPojo.getValidFrom());
			}
			if (null != budgetPojo.getValidFrom()) {
				persistBudget.setValidTo(budgetPojo.getValidTo());
			}

			// persistBudget.setBudgetLock(budgetPojo.getBudgetLock());
			persistBudget.setBudgetOverRun(budgetPojo.getBudgetOverRun());
			persistBudget.setBudgetOverRunNotification(budgetPojo.getBudgetOverRunNotification());

			if (null != budgetPojo.getTotalAmount()) {
				persistBudget.setTotalAmount(budgetPojo.getTotalAmount());
				persistBudget.setRemainingAmount(budgetPojo.getTotalAmount());
			}

			persistBudget.setApprovedAmount(BigDecimal.ZERO);
			persistBudget.setPaidAmount(BigDecimal.ZERO);
			persistBudget.setLockedAmount(BigDecimal.ZERO);
			persistBudget.setPendingAmount(BigDecimal.ZERO);
			persistBudget.setTransferAmount(BigDecimal.ZERO);

			if (null != budgetPojo.getBaseCurrency()) {
				Currency currency = new Currency();
				currency.setId(budgetPojo.getBaseCurrency().getId());
				persistBudget.setBaseCurrency(currency);
			}

			List<BudgetDocument> budgetDocuments = new ArrayList<BudgetDocument>();

			if (CollectionUtil.isNotEmpty(budgetPojo.getBudgetFiles())) {
				for (MultipartFile file : budgetPojo.getBudgetFiles()) {
					byte[] bytes;
					try {
						bytes = file.getBytes();
						if (bytes.length > 0) {
							LOG.info("FILE CONTENT Size : " + bytes.length);
							BudgetDocument document = new BudgetDocument();
							document.setContentType(file.getContentType());
							document.setFileName(file.getOriginalFilename());
							document.setFileData(bytes);
							document.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
							document.setUploadDate(new Date());
							budgetDocuments.add(document);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (CollectionUtil.isNotEmpty(budgetDocuments)) {
					budgetPojo.setBudgetDocuments(budgetDocuments);
					persistBudget.getBudgetDocuments().addAll(budgetPojo.getBudgetDocuments());
				}
			}
			persistBudget.getApprovals().clear();
			// budget = budgetDao.saveOrUpdate(budget);
			if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
				LOG.info("get budget form approval size initially ---------" + budgetPojo.getApprovals().size());
				int level = 1;
				List<BudgetApproval> appList = null;
				appList = persistBudget.getApprovals();
				for (BudgetApproval app : budgetPojo.getApprovals()) {
					if (level == 1) {
						app.setActive(true);
					}
					app.setBudget(persistBudget);
					app.setLevel(level++);
					if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
						for (BudgetApprovalUser approvalUser : app.getApprovalUsers()) {
							LOG.info("approver User name:" + approvalUser.getUser().getLoginId());
							approvalUser.setApproval(app);
							approvalUser.setApprovalStatus(ApprovalStatus.PENDING);
						}
					}
					appList.add(app);
				}
				persistBudget.setApprovals(appList);
				persistBudget.setBudgetStatus(BudgetStatus.PENDING);
			} else {
				persistBudget.setBudgetStatus(BudgetStatus.DRAFT);
			}
			persistBudget = budgetDao.saveOrUpdate(persistBudget);
			if (persistBudget != null) {
				if (persistBudget.getBusinessUnit() != null) {
					BusinessUnit businessUnit = businessUnitDao.getPlainBusinessUnitById(persistBudget.getBusinessUnit().getId());
					persistBudget.setBusinessUnit(businessUnit);
				}
				try {
					if (CollectionUtil.isNotEmpty(persistBudget.getApprovals())) {
						for (BudgetApproval app : persistBudget.getApprovals()) {
							if (app.isActive()) {
								for (BudgetApprovalUser nextLevelUser : app.getApprovalUsers()) {
									LOG.info("Sending email for next approver level user:" + nextLevelUser.getUser().getLoginId());
									approvalService.sendBudgetApprovalReqEmailsOnCreate(persistBudget, nextLevelUser);
								}
								break;
							}
						}
					}
				} catch (Exception e) {
					LOG.error("Error while sending email for next approval leval user:" + e.getMessage(), e);
				}
			}
			return persistBudget;

		} else {

			// create new budget
			bindBudgetData(budget, budgetPojo);

			try {
				budget.setBudgetId(eventIdSettingDao.generateEventIdByBusinessUnit(budget.getTenantId(), "BG", budget.getBusinessUnit()));
			} catch (ApplicationException e) {
				LOG.error("Error while generating Budget Id :  " + e.getMessage(), e);
				throw e;
			}

			if (CollectionUtil.isNotEmpty(budget.getApprovals())) {
				int level = 1;
				for (BudgetApproval app : budget.getApprovals()) {
					LOG.info("get sourcing form approval---------");
					if (level == 1) {
						app.setActive(true);
					}
					app.setBudget(budget);
					app.setBatchNo(0);
					app.setLevel(level++);
					if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
						for (BudgetApprovalUser approvalUser : app.getApprovalUsers()) {
							LOG.info("approver User name:" + approvalUser.getUser().getLoginId());
							approvalUser.setApproval(app);
							approvalUser.setApprovalStatus(ApprovalStatus.PENDING);
						}
					}
				}
				budget.setBudgetStatus(BudgetStatus.PENDING);
			} else {
				budget.setBudgetStatus(BudgetStatus.DRAFT);
			}
			if (CollectionUtil.isNotEmpty(budget.getApprovals())) {
				budget.setApprovals(budget.getApprovals());
			} else {
				budget.setApprovals(new ArrayList<BudgetApproval>());
			}
			budget = budgetDao.saveOrUpdate(budget);
			if (budget != null) {
				if (budget.getBusinessUnit() != null) {
					BusinessUnit businessUnit = businessUnitDao.getPlainBusinessUnitById(budget.getBusinessUnit().getId());
					budget.setBusinessUnit(businessUnit);
				}
				try {
					if (CollectionUtil.isNotEmpty(budget.getApprovals())) {
						for (BudgetApproval app : budget.getApprovals()) {
							if (app.isActive()) {
								for (BudgetApprovalUser nextLevelUser : app.getApprovalUsers()) {
									LOG.info("Sending email for next approver level user:" + nextLevelUser.getUser().getLoginId());
									approvalService.sendBudgetApprovalReqEmailsOnCreate(budget, nextLevelUser);
								}
								break;
							}
						}
					}
				} catch (Exception e) {
					LOG.error("Error while sending email for next approval leval user:" + e.getMessage(), e);
				}
			}
			return budget;
		}
	}

	private void bindBudgetData(Budget budget, BudgetPojo budgetModelPojo) {
		List<MultipartFile> budgetFiles = budgetModelPojo.getBudgetFiles();
		List<BudgetDocument> budgetDocuments = new ArrayList<BudgetDocument>();
		try {
			if (CollectionUtil.isNotEmpty(budgetModelPojo.getApprovals())) {
				budget.setApprovals(budgetModelPojo.getApprovals());
			}
			budget.setBudgetId(budgetModelPojo.getBudgetId());
			budget.setBudgetName(budgetModelPojo.getBudgetName());
			budget.setModifiedBy(null);
			budget.setModifiedDate(null);

			BusinessUnit businessUnit = new BusinessUnit();
			businessUnit.setId(budgetModelPojo.getBusinessUnit().getId());
			budget.setBusinessUnit(businessUnit);

			CostCenter costCenter = new CostCenter();
			costCenter.setId(budgetModelPojo.getCostCenter().getId());
			budget.setCostCenter(costCenter);

			budget.setValidFrom(budgetModelPojo.getValidFrom());
			budget.setValidTo(budgetModelPojo.getValidTo());

			// budget.setBudgetLock(budgetModelPojo.getBudgetLock());
			budget.setBudgetOverRun(budgetModelPojo.getBudgetOverRun());
			budget.setBudgetOverRunNotification(budgetModelPojo.getBudgetOverRunNotification());

			budget.setTotalAmount(budgetModelPojo.getTotalAmount());
			budget.setRemainingAmount(budget.getTotalAmount());
			budget.setApprovedAmount(BigDecimal.ZERO);
			budget.setPaidAmount(BigDecimal.ZERO);
			budget.setLockedAmount(BigDecimal.ZERO);
			budget.setPendingAmount(BigDecimal.ZERO);
			budget.setTransferAmount(BigDecimal.ZERO);

			Currency currency = new Currency();
			currency.setId(budgetModelPojo.getBaseCurrency().getId());
			budget.setBaseCurrency(currency);

			if (CollectionUtil.isNotEmpty(budgetFiles)) {
				for (MultipartFile file : budgetFiles) {
					byte[] bytes = file.getBytes();
					if (bytes.length > 0) {
						LOG.info("FILE CONTENT Size : " + bytes.length);
						BudgetDocument document = new BudgetDocument();
						document.setContentType(file.getContentType());
						document.setFileName(file.getOriginalFilename());
						document.setFileData(bytes);
						document.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
						document.setUploadDate(new Date());
						budgetDocuments.add(document);
					}
				}
				if (CollectionUtil.isNotEmpty(budgetDocuments)) {
					budget.setBudgetDocuments(budgetDocuments);
				}
			}
		} catch (IOException e) {
			LOG.info(e.getMessage(), e);
		}
	}

	@Override
	public List<BudgetPojo> getAllBudgetForTenantId(String loggedInUserTenantId, TableDataInput input) {
		return budgetDao.getAllBudgetForTenantId(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalCountBudgetForTenantId(String loggedInUserTenantId, TableDataInput input) {
		return budgetDao.findTotalCountBudgetForTenantId(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalBudgetForTenantId(String loggedInUserTenantId, TableDataInput input) {
		return budgetDao.findTotalBudgetForTenantId(loggedInUserTenantId, input);
	}

	@Override
	public long findfilteredTotalCountBudgetForTenantId(String loggedInUserTenantId, TableDataInput input) {
		return budgetDao.findfilteredTotalCountBudgetForTenantId(loggedInUserTenantId, input);
	}

	@Override
	public List<BudgetPojo> getAllBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input) {
		return budgetDao.getAllBudgetForApproval(loggedInUserTenantId, id, input);
	}

	@Override
	public long findTotalBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input) {
		return budgetDao.findTotalBudgetForApproval(loggedInUserTenantId, id, input);
	}

	@Override
	public long findTotalCountBudgetForApproval(String loggedInUserTenantId, String id, TableDataInput input) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Budget findById(String Id) {
		Budget budget = budgetDao.findById(Id);
		if (budget != null) {
			if (budget.getBaseCurrency() != null) {
				budget.getBaseCurrency().getCurrencyCode();
			}
		}
		return budget;
	}

	@Override
	public BudgetPojo findBudgetById(String id) {
		Budget budget = budgetDao.findBudgetById(id);
		BudgetPojo budgetPojo = new BudgetPojo();

		if (budget != null) {
			if (CollectionUtil.isNotEmpty(budget.getApprovals())) {
				for (BudgetApproval approval : budget.getApprovals()) {
					approval.getLevel();
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						for (BudgetApprovalUser approvalUser : approval.getApprovalUsers()) {
							approvalUser.getRemarks();
							approvalUser.getApproval();
							approvalUser.getUser().getLoginId();
						}
					}
					LOG.info("budget approval batch no :=============> " + approval.getBatchNo());
				}
				budgetPojo.setApprovals(budget.getApprovals());

			}

			if (CollectionUtil.isNotEmpty(budget.getBudgetComment())) {
				for (BudgetComment comment : budget.getBudgetComment()) {
					comment.getApprovalUserId();
					comment.getCreatedBy().getName();
				}
				budgetPojo.setBudgetComment(budget.getBudgetComment());
			}

			if (CollectionUtil.isNotEmpty(budget.getBudgetDocuments())) {
				for (BudgetDocument document : budget.getBudgetDocuments()) {
					document.getFileName();
					document.getId();
				}
				budgetPojo.setBudgetDocuments(budget.getBudgetDocuments());
			}
			if (budget.getBusinessUnit() != null) {
				budget.getBusinessUnit().getUnitName();
				budgetPojo.setBusinessUnit(budget.getBusinessUnit());
			}
			if (budget.getCostCenter() != null) {
				budget.getCostCenter().getCostCenter();
				budgetPojo.setCostCenter(budget.getCostCenter());
			}
			if (budget.getBaseCurrency() != null) {
				budget.getBaseCurrency().getCurrencyCode();
				budgetPojo.setBaseCurrency(budget.getBaseCurrency());
			}
			if (budget.getBudgetOwner() != null) {
				budgetPojo.setBudgetOwner(budget.getBudgetOwner());
			}
			if (budget.getToBusinessUnit() != null) {
				budget.getToBusinessUnit().getUnitName();
			}
			if (budget.getToCostCenter() != null) {
				budget.getToCostCenter().getCostCenter();
			}

		}

		budgetPojo.setId(budget.getId());
		budgetPojo.setBudgetId(budget.getBudgetId());
		budgetPojo.setBudgetName(budget.getBudgetName());
		budgetPojo.setValidFrom(budget.getValidFrom());
		budgetPojo.setValidTo(budget.getValidTo());
		budgetPojo.setCreatedBy(budget.getCreatedBy());
		budgetPojo.setCreatedDate(budget.getCreatedDate());
		// budgetPojo.setBudgetLock(budget.getBudgetLock());
		budgetPojo.setBudgetStatus(budget.getBudgetStatus());
		/*
		 * if (budgetPojo.getBudgetOverRun()) { budgetPojo.setBudgetOverRunNo(false); } if
		 * (budgetPojo.getBudgetOverRunNotification()) { budgetPojo.setBudgetOverRunNotificationNo(false); }
		 */
		budgetPojo.setBudgetOverRun(budget.getBudgetOverRun());
		budgetPojo.setBudgetOverRunNotification(budget.getBudgetOverRunNotification());
		budgetPojo.setTotalAmount(budget.getTotalAmount());
		budgetPojo.setRemainingAmount(budget.getRemainingAmount());
		if (null != budget.getRevisionDate()) {
			budgetPojo.setRevisionDate(budget.getRevisionDate());
		}
		if (null != budget.getRevisionJustification() && 1 == budget.getRevisionAmount().compareTo(budget.getTotalAmount())) {
			budgetPojo.setAddRevisionAmount(budget.getRevisionAmount());
			budgetPojo.setAddRevisionJustification(budget.getRevisionJustification());
			BigDecimal addAmount = budget.getRevisionAmount().subtract(budget.getTotalAmount());
			budgetPojo.setAddRevisionRemainingAmount((addAmount).add(budget.getRemainingAmount()));
		}
		if (null == budget.getToBusinessUnit() && null != budget.getRevisionJustification() && 1 == budget.getTotalAmount().compareTo(budget.getRevisionAmount())) {
			budgetPojo.setDeductRevisionAmount(budget.getRevisionAmount());
			budgetPojo.setDeductRevisionJustification(budget.getRevisionJustification());
			BigDecimal deductAmount = budget.getTotalAmount().subtract(budget.getRevisionAmount());
			budgetPojo.setDeductRevisionRemainingAmount(budget.getRemainingAmount().subtract(deductAmount));
		}
		if (budget.getToBusinessUnit() != null && budget.getToCostCenter() != null && budget.getRevisionJustification() != null) {
			if (budget.getConversionRate() != null) {
				budgetPojo.setConversionRate(budget.getConversionRate());
			}
			budgetPojo.setTransferRevisionAmount(budget.getRevisionAmount());
			budgetPojo.setTransferRevisionJustification(budget.getRevisionJustification());
			if (budget.getToBusinessUnit() != null) {
				budget.getToBusinessUnit().getUnitName();
				budgetPojo.setToBusinessUnit(budget.getBusinessUnit());
			}
			if (budget.getToCostCenter() != null) {
				budget.getToCostCenter().getCostCenter();
				budgetPojo.setToCostCenter(budget.getToCostCenter());
			}
			BigDecimal trasferAmount = budget.getTotalAmount().subtract(budget.getRevisionAmount());
			budgetPojo.setTransferRevisionRemainingAmount(budget.getRemainingAmount().subtract(trasferAmount));
			LOG.info("***************getTransferRevisionJustification*************** " + budgetPojo.getTransferRevisionJustification());
		}

		return budgetPojo;
	}

	@Override
	@Transactional(readOnly = false)
	public Budget updateBudget(Budget budget) {
		Budget savedBudget = budgetDao.update(budget);
		return savedBudget;
	}

	@Override
	public Budget findBudgetByBusinessUnit(String id) {
		return budgetDao.findBudgetByBusinessUnit(id);
	}

	@Override
	@Transactional(readOnly = false)
	public Budget saveBudgetAndDocuments(Budget budget) {
		budget = budgetDao.saveOrUpdate(budget);
		return budget;
	}

	@Override
	public Budget findBudgetAndDocumentById(String id) {
		Budget budget = budgetDao.findById(id);
		if (CollectionUtil.isNotEmpty(budget.getBudgetDocuments())) {
			budget.getBudgetDocuments();
		} else {
			budget.setBudgetDocuments(new ArrayList<BudgetDocument>());
		}

		return budget;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteDocumentById(String budgetId, String docId) {
		budgetDao.deleteDocumentById(budgetId, docId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updateImmediately(String id, BudgetStatus status) {
		return budgetDao.updateImmediately(id, status);
	}

	@Override
	@Transactional(readOnly = false)
	public BudgetDocument findBudgetDocById(String docId) {
		return budgetDocumentDao.findById(docId);
	}

	@Override
	public Boolean isCombinationOfBuAndCcExists(BudgetPojo budgetPojo) {
		List<Budget> allBudgets = budgetDao.findAll(Budget.class);
		for (Budget budget : allBudgets) {
			// don't check budgets with budget status EXPIRED and DRAFT and CANCELED
			if (BudgetStatus.EXPIRED == budget.getBudgetStatus() || BudgetStatus.DRAFT == budget.getBudgetStatus() || BudgetStatus.CANCELED == budget.getBudgetStatus()) {
				continue;
			}
			if (budget.getBusinessUnit().getId().equals(budgetPojo.getBusinessUnit().getId()) && budget.getCostCenter().getId().equals(budgetPojo.getCostCenter().getId())) {
				LOG.info("duplicate Business unit and cost center");
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	@Override
	public Budget findBudgetByBusinessUnitAndCostCenter(String businessUnitId, String costCenterId) {
		Budget budget = budgetDao.findBudgetByBusinessUnitAndCostCenter(businessUnitId, costCenterId);
		if (budget != null) {
			if (budget.getBaseCurrency() != null) {
				budget.getBaseCurrency().getCurrencyCode();
			}
			if (budget.getBusinessUnit() != null) {
				budget.getBusinessUnit().getUnitName();
			}
			if (budget.getCostCenter() != null) {
				budget.getCostCenter().getCostCenter();
			}

		}
		return budget;
	}

	@Override
	@Transactional(readOnly = false)
	public void addAdditionalApprovers(BudgetPojo budgetPojo, String id) {
		Budget savedBudget = budgetDao.findBudgetForAdditionalApprovalsById(id);
		if (savedBudget != null) {
			addAditionalApprover(budgetPojo, savedBudget);
		}
	}

	private void addAditionalApprover(BudgetPojo budgetPojo, Budget savedBudget) {

		List<BudgetApproval> oldApproverals = savedBudget.getApprovals();
		if (CollectionUtil.isEmpty(oldApproverals)) {
			oldApproverals = new ArrayList<BudgetApproval>();
		}
		LOG.info("Budget Id===============> : " + savedBudget.getId() + "  ====  : " + savedBudget.getBudgetId());

		if (CollectionUtil.isNotEmpty(budgetPojo.getApprovals())) {
			LOG.info("newApproval list size===============>" + budgetPojo.getApprovals().size());
			BudgetApproval lastApprover = oldApproverals.get(oldApproverals.size() - 1);
			LOG.info("lastApprover**level*********** : " + lastApprover.getLevel());
			int level = lastApprover.getLevel();
			int oldBatchNo = lastApprover.getBatchNo();
			int newlevel = level + 1;
			int newBatchNo = oldBatchNo + 1;
			int activeUser = 1;

			for (int i = 0; i < budgetPojo.getApprovals().size(); i++) {
				BudgetApproval app = budgetPojo.getApprovals().get(i);
				LOG.info("ID**************** : " + app.getId());
				LOG.info("level************* : " + app.getLevel());

				if (activeUser == 1) {
					app.setActive(true);
					if (savedBudget != null) {
						try {
							if (savedBudget.getBusinessUnit() != null) {
								BusinessUnit businessUnit = businessUnitDao.getPlainBusinessUnitById(savedBudget.getBusinessUnit().getId());
								savedBudget.setBusinessUnit(businessUnit);
								for (BudgetApprovalUser nextLevelUser : app.getApprovalUsers()) {
									LOG.info("Sending email for next approver level user:" + nextLevelUser.getUser().getLoginId());
									approvalService.sendBudgetApprovalReqEmailsOnCreate(savedBudget, nextLevelUser);
								}
							}

						} catch (Exception e) {
							LOG.error("Error while sending email for next approval leval user:" + e.getMessage(), e);
						}
					}
				}
				activeUser = activeUser + 1;
				app.setBudget(savedBudget);
				app.setBatchNo(newBatchNo);
				app.setLevel(newlevel++);//
				LOG.info("app Type :" + app.getApprovalType());

				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
					for (BudgetApprovalUser budgetApprovalUser : app.getApprovalUsers()) {
						budgetApprovalUser.setApproval(app);
						LOG.info("Budget  Request Id===============>" + budgetApprovalUser.getId());
						budgetApprovalUser.setApprovalStatus(ApprovalStatus.PENDING);
					}
				}
				oldApproverals.add(app);
			}
		}
		savedBudget.setApprovals(oldApproverals);
		budgetDao.update(savedBudget);
	}

	@Override
	public void sendBudgetUtilizedNotifications(String id, BigDecimal percentageUtilized) {
		Budget budget = findBudgetCreatorAndApprovalsById(id);
		// send notifications to creator and approvals
		approvalService.sentBudgetUtilizedNotifications(budget, percentageUtilized);
	}

	private Budget findBudgetCreatorAndApprovalsById(String id) {
		return budgetDao.findBudgetCreatorAndApprovalsById(id);
	}

	@Override
	public void sendBudgetOverrunNotification(String id) {
		Budget budget = findBudgetCreatorAndApprovalsById(id);
		approvalService.sendBudgetOverrunNotification(budget);
	}

	@Override
	public Budget findTransferToBudgetById(String id) {
		return budgetDao.findTransferToBudgetById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveBuyerAuditTrail(BuyerAuditTrail audit) {
		buyerAuditTrailService.save(audit);

	}

}
