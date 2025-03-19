package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.FmtNumber;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.ContractAuditDao;
import com.privasia.procurehere.core.dao.ContractDocumentDao;
import com.privasia.procurehere.core.dao.ContractLoaAndAgreementDao;
import com.privasia.procurehere.core.dao.ContractTeamMemberDao;
import com.privasia.procurehere.core.dao.ProductContractDao;
import com.privasia.procurehere.core.dao.ProductContractItemsDao;
import com.privasia.procurehere.core.dao.ProductContractNotifyUsersDao;
import com.privasia.procurehere.core.dao.ProductContractReminderDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ContractApproval;
import com.privasia.procurehere.core.entity.ContractApprovalUser;
import com.privasia.procurehere.core.entity.ContractAudit;
import com.privasia.procurehere.core.entity.ContractComment;
import com.privasia.procurehere.core.entity.ContractDocument;
import com.privasia.procurehere.core.entity.ContractLoaAndAgreement;
import com.privasia.procurehere.core.entity.ContractTeamMember;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.core.entity.ProductContractItems;
import com.privasia.procurehere.core.entity.ProductContractNotifyUsers;
import com.privasia.procurehere.core.entity.ProductContractReminder;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.ApprovalType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SecurityRuntimeException;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.ContractApprovalUserPojo;
import com.privasia.procurehere.core.pojo.ContractAuditPojo;
import com.privasia.procurehere.core.pojo.ContractCommentPojo;
import com.privasia.procurehere.core.pojo.ContractDocumentPojo;
import com.privasia.procurehere.core.pojo.ContractLoaAndAgreementPojo;
import com.privasia.procurehere.core.pojo.ContractPojo;
import com.privasia.procurehere.core.pojo.ContractProductItemPojo;
import com.privasia.procurehere.core.pojo.ContractSummaryPojo;
import com.privasia.procurehere.core.pojo.ContractTeamUserPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.ProductContractItemsPojo;
import com.privasia.procurehere.core.pojo.ProductContractNotifyUserPojo;
import com.privasia.procurehere.core.pojo.ProductContractPojo;
import com.privasia.procurehere.core.pojo.ProductContractReminderPojo;
import com.privasia.procurehere.core.pojo.SearchParameter;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.ContractAuditService;
import com.privasia.procurehere.service.ProductContractService;
import com.privasia.procurehere.service.UserService;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

@Service
@Transactional(readOnly = true)
public class ProductContractServiceImpl implements ProductContractService {

	private static final Logger LOG = LogManager.getLogger(Global.CONTRACT_LOG);

	@Autowired
	ProductContractDao productContractDao;

	@Autowired
	ProductContractReminderDao productContractReminderDao;

	@Autowired
	ProductContractNotifyUsersDao productContractNotifyUsersDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	ContractAuditService contractAuditService;

	@Autowired
	UserService userService;

	@Autowired
	ContractAuditDao contractAuditDao;

	@Autowired
	ContractLoaAndAgreementDao contractLoaAndAgreementDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	ServletContext context;

	@Autowired
	ContractDocumentDao contractDocumentDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	ProductContractItemsDao productContractItemsDao;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	ContractTeamMemberDao contractTeamMemberDao;

	@Override
	public ProductContract findProductContractByReferenceNumber(String sapContractNumber, String tenantId) {
		return productContractDao.findByContractByReferenceNumber(sapContractNumber, tenantId);
	}

	@Transactional(readOnly = false)
	@Override
	public ProductContract createProductContract(ProductContract productContract) {
		return productContractDao.saveOrUpdate(productContract);
	}

	@Transactional(readOnly = false)
	@Override
	public ProductContract update(ProductContract productContract) {
		return productContractDao.update(productContract);
	}

	@Override
	public List<ContractPojo> findProductContractListForTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		return productContractDao.findProductContractListForTenant(loggedInUserTenantId, userId, input, startDate, endDate);
	}

	@Override
	public List<ContractPojo> findProductContractListForBizUnit(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate,List<String> businessUnitIds) {
		//return productContractDao.findProductContractListForTenant(loggedInUserTenantId, userId, input, startDate, endDate);
		return productContractDao.findProductContractListForBizUnit(loggedInUserTenantId, userId, input, startDate, endDate,businessUnitIds);
	}

	@Override
	public long findTotalFilteredProductListForTenant(String loggedInUserTenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		return productContractDao.findTotalFilteredProductListForTenant(loggedInUserTenantId, userId, input, startDate, endDate);
	}

	@Override
	public long findTotalProductListForTenant(String loggedInUserTenantId, String userId) {
		return productContractDao.findTotalProductListForTenant(loggedInUserTenantId, userId);
	}

	@Override
	public ProductContract findProductContractById(String id, String loggedInUser) {
		ProductContract productContract = productContractDao.findProductContractById(id, loggedInUser);
		if (productContract != null) {
			if (productContract.getContractCreator() != null) {
				productContract.getContractCreator().getLoginId();
			}
			List<ProductContractNotifyUsers> notifyUsersList = productContractNotifyUsersDao.getAllContractNotifyUsersByContractId(productContract.getId());
			if (CollectionUtil.isNotEmpty(notifyUsersList)) {
				productContract.setNotifyUsers(notifyUsersList);
				for (ProductContractNotifyUsers users : productContract.getNotifyUsers()) {
					users.getId();
					users.getUser().getName();
				}
			} else {
				productContract.setNotifyUsers(null);
			}
			if (CollectionUtil.isNotEmpty(productContract.getTeamMembers())) {
				for (ContractTeamMember member : productContract.getTeamMembers()) {
					member.getUser().getId();
					member.getUser().getName();
					member.getUser().getLoginId();
				}
			}
			if (productContract.getSupplier() != null) {
				productContract.getSupplier().getSupplier().getCompanyName();
			}
			if (productContract.getBusinessUnit() != null) {
				productContract.getBusinessUnit().getUnitName();
			}
			if (productContract != null && productContract.getProcurementCategory() != null) {
				productContract.getProcurementCategory().getProcurementCategories();
			}
			if (productContract != null && productContract.getAgreementType() != null) {
				productContract.getAgreementType().getAgreementType();
			}
			if (productContract != null && productContract.getCurrency() != null) {
				productContract.getCurrency().getCurrencyCode();
			}
			if (CollectionUtil.isNotEmpty(productContract.getApprovals())) {
				for (ContractApproval approval : productContract.getApprovals()) {
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						for (ContractApprovalUser approvalUser : approval.getApprovalUsers()) {
							approvalUser.getRemarks();
							approvalUser.getUser().getLoginId();
							approvalUser.getUser().getName();
							approvalUser.getUser().getCommunicationEmail();
						}
					}
				}
			}

		}
		return productContract;
	}

	@Override
	public ProductContract getProductContractById(String id) {
		ProductContract product = productContractDao.findById(id);
		if (product != null && product.getSupplier() != null) {
			product.getSupplier().getSupplier().getCompanyName();
			product.getSupplier().getSupplier().getFullName();
			product.getSupplier().getSupplier().getCompanyContactNumber();
		}
		if (product != null && product.getCreatedBy() != null) {
			product.getCreatedBy().getName();
			product.getCreatedBy().getPhoneNumber();
			product.getCreatedBy().getCommunicationEmail();
			product.getCreatedBy().getEmailNotifications();
		}
		if (product != null && product.getContractCreator() != null) {
			product.getContractCreator().getName();
			product.getContractCreator().getCommunicationEmail();
			product.getContractCreator().getEmailNotifications();
		}

		if (product != null && product.getModifiedBy() != null) {
			product.getModifiedBy().getName();
		}
		if (product != null && product.getBuyer() != null) {
			product.getBuyer().getCompanyContactNumber();
			product.getBuyer().getFaxNumber();
		}
		if (product != null && product.getBusinessUnit() != null) {
			product.getBusinessUnit().getUnitName();
		}
		if (product != null && product.getProcurementCategory() != null) {
			product.getProcurementCategory().getProcurementCategories();
		}
		if (product != null && product.getAgreementType() != null) {
			product.getAgreementType().getAgreementType();
		}
		if (product != null && product.getCurrency() != null) {
			product.getCurrency().getCurrencyCode();
		}
		if (CollectionUtil.isNotEmpty(product.getTeamMembers())) {
			for (ContractTeamMember teamMembers : product.getTeamMembers()) {
				teamMembers.getTeamMemberType();
				if (teamMembers.getUser() != null)
					teamMembers.getUser().getName();
			}
		}
		if (CollectionUtil.isNotEmpty(product.getApprovals())) {
			for (ContractApproval approval : product.getApprovals()) {
				for (ContractApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
					approvalUser.getUser().getLoginId();
				}
			}
		}

		return product;
	}

	@Override
	public long findNewUpcomingContractByTeanantId(String loggedInUserTenantId, String userId) {
		return productContractDao.findNewUpcomingContractByTeanantId(loggedInUserTenantId, userId);
	}

	@Override
	public long findContractBefore30DayExpireByTeanantId(String loggedInUserTenantId, String userId) {
		return productContractDao.findContractBefore30DayExpireByTeanantId(loggedInUserTenantId, userId);
	}

	@Override
	public long findContractBefore90DayExpireByTeanantId(String loggedInUserTenantId, String userId) {
		return productContractDao.findContractBefore90DayExpireByTeanantId(loggedInUserTenantId, userId);
	}

	@Override
	public long findContractBefore180DayExpireByTeanantId(String loggedInUserTenantId, String userId) {
		return productContractDao.findContractBefore180DayExpireByTeanantId(loggedInUserTenantId, userId);
	}

	@Override
	public long findContractGreaterThanSixMonthExpireByTeanantId(String loggedInUserTenantId, String userId) {
		return productContractDao.findContractGreaterThanSixMonthExpireByTeanantId(loggedInUserTenantId, userId);
	}

	@Override
	public List<ProductContractPojo> findContractListByExpiredDaysBetweenForTenant(String tenantId, String userId, TableDataInput input, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus) {
		return productContractDao.findContractListByExpiredDaysBetweenForTenant(tenantId, userId, input, currentDate, expiredDate, isNewUpcoming, isbetween, greaterThanSixMonth, isExpired, startDate, endDate, contractStatus);

	}

	@Override
	public long findTotalFilteredContractByExpiredDaysBetweenForTenant(String tenantId, String userId, TableDataInput input, Date currentDate, Date expiredDate, boolean isNewUpcoming, boolean isbetween, boolean greaterThanSixMonth, boolean isExpired, Date startDate, Date endDate, ContractStatus contractStatus) {
		return productContractDao.findTotalFilteredContractByExpiredDaysBetweenForTenant(tenantId, userId, input, currentDate, expiredDate, isNewUpcoming, isbetween, greaterThanSixMonth, isExpired, startDate, endDate, contractStatus);

	}

	@Override
	public void downloadCsvFileForContract(HttpServletResponse response, File file, String[] eventArr, ContractPojo productContractPojo, boolean select_all, String tenantId, SimpleDateFormat formatter, String userId, Date startDate, Date endDate) {
		LOG.info("tenantId......." + tenantId);
		LOG.info("Select All....." + select_all);
		LOG.info(" EventArr..." + Arrays.toString(eventArr));
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.CONTRACT_REPORT_CSV_COLUMNS;

			String[] columns = { "contractId", "contractName", "eventId", "contractReferenceNumber", "previousContractNo", "renewalContractStr", "companyName", "vendorCode", "businessUnit", "unitCode", "groupCodeStr", "procurementCategory", "agreementType", "currencyCode", "contractValue", "contractStartDate", "contractEndDate", "contractReminderDates", "loaDateStr", "loaCompletedStr", "agreementDateStr", "agreementCompletedStr", "creatorEmail", "createdDateStr", "modifiedBy", "modifiedDateStr", "status" };
			long count = productContractDao.findTotalContractEventCountForCsv(tenantId);
			LOG.info("Count ........" + count);
			int pageSize = 5000;
			int totalPages = (int) Math.ceil(count / (double) pageSize);
			LOG.info("total Pages...." + totalPages);

			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			beanWriter.writeHeader(columnHeadings);

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateFormat.setTimeZone(formatter.getTimeZone());
			
			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<ContractPojo> list = findAllActiveContractEventsForTenantIdForCsv(tenantId, pageSize, pageNo, eventArr, productContractPojo, select_all, userId, startDate, endDate);
				for (ContractPojo pojo : list) {
					if (pojo.getCreatedDate() != null) {
						pojo.setCreatedDateStr(formatter.format(pojo.getCreatedDate()));
					}
					if (pojo.getModifiedDate() != null) {
						pojo.setModifiedDateStr(formatter.format(pojo.getModifiedDate()));
					}
					if (pojo.getLoaDate() != null) {
						pojo.setLoaDateStr(dateFormat.format(pojo.getLoaDate()));
					}
					if (pojo.getAgreementDate() != null) {
						pojo.setAgreementDateStr(dateFormat.format(pojo.getAgreementDate()));
					}
					if (Boolean.TRUE == pojo.getLoaCompleted()) {
						pojo.setLoaCompletedStr("COMPLETE");
					} else {
						pojo.setLoaCompletedStr("INCOMPLETE");
					}
					if (Boolean.TRUE == pojo.getAgreementCompleted()) {
						pojo.setAgreementCompletedStr("COMPLETE");
					} else {
						pojo.setAgreementCompletedStr("INCOMPLETE");
					}
					if (Boolean.TRUE == pojo.getRenewalContract()) {
						pojo.setRenewalContractStr("YES");
					} else {
						pojo.setRenewalContractStr("NO");
					}
					beanWriter.write(pojo, columns, getProcessors(pojo.getDecimal()));
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

	private static CellProcessor[] getProcessors(String decimal) {

		DecimalFormat df = null;
		if (decimal.equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (decimal.equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (decimal.equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (decimal.equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (decimal.equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (decimal.equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		final CellProcessor[] processors = new CellProcessor[] { new Optional(), // contractId
				new Optional(), // contractName
				new Optional(), // eventId
				new Optional(), // contractReferenceNumber
				new Optional(), // previousContractNo
				new Optional(), // renewalContractStr
				new Optional(), // companyName
				new Optional(), // vendorCode
				new Optional(), // businessUnit
				new Optional(), // unitCode
				new Optional(), // groupCodeStr
				new Optional(), // procurementCategory
				new Optional(), // agreementType
				new Optional(), // currencyCode
				new Optional(new FmtNumber(df)), // contractValue
				new Optional(), // contractStartDate
				new Optional(), // contractEndDate
				new Optional(), // contractReminderDates
				new Optional(), // loaDateStr
				new Optional(), // loaCompletedStr
				new Optional(), // agreementDateStr
				new Optional(), // agreementCompletedStr
				new Optional(), // creatorEmail
				new Optional(), // createdDateStr
				new Optional(), // modifiedBy
				new Optional(), // modifiedDateStr
				new Optional() // status
		};
		return processors;
	}

	private List<ContractPojo> findAllActiveContractEventsForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] eventIds, ContractPojo productContractPojo, boolean select_all, String userId, Date startDate, Date endDate) {

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

		if (productContractPojo != null) {

			if (eventIds != null && !select_all) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("eventIds");
				cp.getSearch().setValue(String.join(",", eventIds));
				cp.setSearchable(true);
				tdi.getColumns().add(cp);

			}

			if (StringUtils.checkString(productContractPojo.getReferencenumber()).length() > 0) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("referenceNumber");
				cp.getSearch().setValue(productContractPojo.getReferencenumber());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);

			}
			if (StringUtils.checkString(productContractPojo.getGroupcode()).length() > 0) {

				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("groupcode");
				cp.getSearch().setValue(productContractPojo.getGroupcode());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (StringUtils.checkString(productContractPojo.getCurrencyCode()).length() > 0) {

				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("currencyCode");
				cp.getSearch().setValue(productContractPojo.getCurrencyCode());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (productContractPojo.getStatus() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("status");
				cp.getSearch().setValue(productContractPojo.getStatus().toString());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);

			}
			if (productContractPojo.getSupplier() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("supplier");
				cp.getSearch().setValue(productContractPojo.getSupplier());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (productContractPojo.getModifiedBy() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("modifiedBy");
				cp.getSearch().setValue(productContractPojo.getModifiedBy());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}
			if (productContractPojo.getCreatedBy() != null) {
				ColumnParameter cp = new ColumnParameter();
				cp.setSearch(new SearchParameter());
				cp.setData("createdBy");
				cp.getSearch().setValue(productContractPojo.getCreatedBy());
				cp.setSearchable(true);
				tdi.getColumns().add(cp);
			}

			tdi.setStart(pageSize * pageNo);
			tdi.setLength(pageSize);
		}

		List<ContractPojo> requestList = productContractDao.findProductContractListForTenantForCsv(tenantId, userId, tdi, startDate, endDate);

		return requestList;
	}

	@Override
	public void downloadCsvFileForContractList(HttpServletResponse response, File file, String userId, Date startDate, Date endDate, boolean isbetween, boolean greaterThanSixMonth, String tenantId, SimpleDateFormat formatter, ContractStatus status, Date expiryFrom, Date expiryTo) {
		LOG.info("tenantId......." + tenantId);
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.CONTRACT_REPORT_CSV_COLUMNS;

			String[] columns = { "contractId", "contractName", "eventId", "contractReferenceNumber", "previousContractNo", "renewalContractStr", "companyName", "vendorCode", "businessUnit", "unitCode", "groupCodeStr", "procurementCategory", "agreementType", "currencyCode", "contractValue", "contractStartDate", "contractEndDate", "contractReminderDates", "loaDateStr", "loaCompletedStr", "agreementDateStr", "agreementCompletedStr", "creatorEmail", "createdDateStr", "modifiedBy", "modifiedDateStr", "status" };
			long count = productContractDao.findTotalContractEventCountForCsv(tenantId);
			LOG.info("Count ........" + count);
			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);
			LOG.info("total Pages...." + totalPages);

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateFormat.setTimeZone(formatter.getTimeZone());
			
			beanWriter = new CsvBeanWriter(new FileWriter(file), new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2)).build());
			beanWriter.writeHeader(columnHeadings);
			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<ProductContractPojo> list = productContractDao.findProductContractListForCsvTenantRecords(tenantId, userId, startDate, endDate, isbetween, greaterThanSixMonth, status, expiryFrom, expiryTo);
				for (ProductContractPojo pojo : list) {
					if (pojo.getCreatedDate() != null) {
						pojo.setCreatedDateStr(formatter.format(pojo.getCreatedDate()));
					}
					if (pojo.getModifiedDate() != null) {
						pojo.setModifiedDateStr(formatter.format(pojo.getModifiedDate()));
					}
					if (pojo.getLoaDate() != null) {
						pojo.setLoaDateStr(dateFormat.format(pojo.getLoaDate()));
					}
					if (pojo.getAgreementDate() != null) {
						pojo.setAgreementDateStr(dateFormat.format(pojo.getAgreementDate()));
					}
					if (Boolean.TRUE == pojo.getLoaCompleted()) {
						pojo.setLoaCompletedStr("COMPLETE");
					} else {
						pojo.setLoaCompletedStr("INCOMPLETE");
					}
					if (Boolean.TRUE == pojo.getAgreementCompleted()) {
						pojo.setAgreementCompletedStr("COMPLETE");
					} else {
						pojo.setAgreementCompletedStr("INCOMPLETE");
					}
					if (Boolean.TRUE == pojo.getRenewalContract()) {
						pojo.setRenewalContractStr("YES");
					} else {
						pojo.setRenewalContractStr("NO");
					}

					beanWriter.write(pojo, columns, getProcessors(pojo.getDecimal()));
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

	@Override
	public boolean isExists(String contractReferenceNumber, String buyerId, String contractId) {
		return productContractDao.isExists(contractReferenceNumber, buyerId, contractId);
	}

	@Override
	@Transactional(readOnly = false)
	public ProductContract createContract(ProductContract productContract, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer) {
		String auditRemarks = "'" + productContract.getContractId() + "' Contract created";
		productContract = productContractDao.saveOrUpdate(productContract);
		LOG.info("Contract   " + productContract);

		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, auditRemarks, productContract.getCreatedBy().getTenantId(), productContract.getCreatedBy(), new Date(), ModuleType.ContractList);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.error("Error while capturing audit for contract " + e.getMessage(), e);
		}

		try {
			try {
				ContractAudit audit = new ContractAudit(productContract, loggedInUser, new java.util.Date(), AuditActionType.Create, auditRemarks);
				contractAuditService.save(audit);
			} catch (Exception e) {
				LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
			}

		} catch (SecurityRuntimeException e) {
			LOG.error("Error while capturing audit for contract " + e.getMessage(), e);
		}

		if (productContract.getBusinessUnit() != null) {
			productContract.getBusinessUnit().getUnitName();
		}
		return productContract;
	}

	@Override
	@Transactional(readOnly = false)
	public ProductContract updateContract(ProductContract productContract, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, boolean decimalChanged) {
		String auditRemarks = "Contract '" + productContract.getContractId() + "' updated";
		ProductContract pc = productContractDao.saveOrUpdate(productContract);

		// Update the decimals for items
		if (decimalChanged && CollectionUtil.isNotEmpty(pc.getProductContractItem())) {
			for (ProductContractItems item : pc.getProductContractItem()) {
				item.setUnitPrice(item.getUnitPrice().setScale(Integer.parseInt(pc.getDecimal()), RoundingMode.HALF_UP));
				item.setPricePerUnit(item.getPricePerUnit() != null ? item.getPricePerUnit().setScale(Integer.parseInt(pc.getDecimal()), RoundingMode.HALF_UP) : BigDecimal.ONE);
				item.setBalanceQuantity(item.getBalanceQuantity().setScale(Integer.parseInt(pc.getDecimal()), RoundingMode.HALF_UP));
				item.setQuantity(item.getQuantity().setScale(Integer.parseInt(pc.getDecimal()), RoundingMode.HALF_UP));
				productContractItemsDao.update(item);
			}
		}

		try {
			if (productContract.getStatus() != ContractStatus.DRAFT) {
				try {
					BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, auditRemarks, productContract.getModifiedBy().getTenantId(), productContract.getModifiedBy(), new Date(), ModuleType.ContractList);
					buyerAuditTrailDao.save(ownerAuditTrail);
				} catch (Exception e) {
					LOG.error("Error while capturing audit for contract " + e.getMessage(), e);
				}

				try {
					ContractAudit audit = new ContractAudit(productContract, loggedInUser, new java.util.Date(), AuditActionType.Update, auditRemarks);
					contractAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while capturing audit for contract " + e.getMessage(), e);
		}

		if (pc != null && pc.getBusinessUnit() != null) {
			pc.getBusinessUnit().getUnitName();
		}
		return pc;
	}

	@Override
	public ContractProductItemPojo getProductItemListByProductItemId(String productItemId) {
		return productContractDao.getProductItemListByProductItemId(productItemId);
	}

	@Override
	public long findDraftContractByTenantId(String loggedInUserTenantId, String userId) {
		return productContractDao.findDraftContractByTenantId(loggedInUserTenantId, userId);
	}

	@Override
	public long findPendingContractByTenantId(String loggedInUserTenantId, String userId) {
		return productContractDao.findPendingContractByTenantId(loggedInUserTenantId, userId);
	}

	@Override
	public long findContractByStatusForTeanant(String loggedInUserTenantId, String userId, ContractStatus contractStatus) {
		return productContractDao.findContractByStatusForTeanant(loggedInUserTenantId, userId, contractStatus);
	}

	@Override
	public List<ContractTeamMember> getPlainTeamMembersForContract(String contractId) {
		return productContractDao.getPlainTeamMembersForContract(contractId);
	}

	@Override
	public ContractTeamMember getContractTeamMemberByUserIdAndPrId(String contractId, String userId) {
		return productContractDao.getContractTeamMemberByUserIdAndContractId(contractId, userId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<ContractTeamMember> addTeamMemberToList(String contractId, String userId, TeamMemberType memberType, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " contractId: " + contractId + " userId: " + userId);
		ProductContract contract = productContractDao.findById(contractId);
		List<ContractTeamMember> teamMembers = contract.getTeamMembers();
		LOG.info("teamMembers :********: " + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<ContractTeamMember>();
		}
		User user = userService.getUsersById(userId);
		ContractTeamMember contractTeamMember = new ContractTeamMember();
		contractTeamMember.setProductContract(contract);
		contractTeamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (ContractTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				contractTeamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;

			}
		}
		contractTeamMember.setTeamMemberType(memberType);
		if (!exists) {
			LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + exists);
			teamMembers.add(contractTeamMember);
		}
		LOG.info("TeamMembers : " + contractTeamMember.toLogString());

		contract.setTeamMembers(teamMembers);
		contract = productContractDao.update(contract);

		LOG.info(contract.getId());
		LOG.info(contract.getTeamMembers().size());

		try {
			if (!exists && contract.getStatus() != ContractStatus.DRAFT) {
				String remark = "'" + user.getName() + "' has been added as '" + memberType.getValue() + "' for Contract '" + contract.getContractId() + "' ";
				try {
					ContractAudit audit = new ContractAudit(loggedInUser.getBuyer(), contract, loggedInUser, new java.util.Date(), AuditActionType.Create, remark, null);
					contractAuditService.save(audit);
				} catch (Exception e) {
					LOG.error("Error while Store Audit : " + e.getMessage(), e);
				}

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been added as '" + memberType.getValue() + "' for Contract '" + contract.getContractId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ContractList);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} else {
				if (!previousMemberType.equalsIgnoreCase(memberType.getValue()) && contract.getStatus() != ContractStatus.DRAFT) {
					String remark = "'" + user.getName() + "' has been added as '" + memberType.getValue() + "' for Contract '" + contract.getContractId() + "' ";
					try {
						ContractAudit audit = new ContractAudit(loggedInUser.getBuyer(), contract, loggedInUser, new java.util.Date(), AuditActionType.Create, remark, null);
						contractAuditService.save(audit);
					} catch (Exception e) {
						LOG.error("Error while Audit : " + e.getMessage(), e);
					}
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been changed from '" + previousMemberType + "'to '" + memberType.getValue() + "' for Contract '" + contract.getContractId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.ContractList);
					buyerAuditTrailDao.save(buyerAuditTrail);
				}
			}

		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}
		LOG.info(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< " + teamMembers.size());
		return teamMembers;
	}

	@Override
	@Transactional(readOnly = false)
	public List<ContractTeamMember> removeTeamMemberfromList(String contractId, String userId, User loggedInUser) throws ApplicationException {
		ProductContract contract = productContractDao.findById(contractId);
		if (contract == null) {
			throw new ApplicationException("Contract not found by ID : " + contractId);
		}
		ContractTeamMember dbTeamMember = productContractDao.getContractTeamMemberByUserIdAndContractId(contractId, userId);
		if (dbTeamMember == null) {
			throw new ApplicationException("Teammember not found by user id : " + userId);
		}
		List<ContractTeamMember> teamMembers = contract.getTeamMembers();
		if (teamMembers == null) {
			teamMembers = new ArrayList<ContractTeamMember>();
		}
		teamMembers.remove(dbTeamMember);
		dbTeamMember.setProductContract(null);
		contract.setTeamMembers(teamMembers);
		contract = productContractDao.update(contract);

		try {
			ContractAudit audit = new ContractAudit(contract.getBuyer(), contract, loggedInUser, new java.util.Date(), AuditActionType.Update, messageSource.getMessage("pr.team.member.audit.removed", new Object[] { dbTeamMember.getUser().getName(), dbTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), null);
			contractAuditService.save(audit);

			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + dbTeamMember.getUser().getName() + "' has been removed as '" + dbTeamMember.getTeamMemberType().getValue() + "' from Contract '" + contract.getContractId() + "' ", contract.getBuyer().getId(), loggedInUser, new Date(), ModuleType.ContractList);
			buyerAuditTrailDao.save(buyerAuditTrail);

			LOG.info("************* Contract Team Member removed successfully *************");
		} catch (Exception e) {
			LOG.info("Error inserting audit details during remove of team member: " + e.getMessage());
		}

		return teamMembers;
	}

	@Override
	public ContractLoaAndAgreement findContractLoaAndAgreementByContractId(String id) {
		return contractLoaAndAgreementDao.findContractLoaAndAgreementByContractId(id);
	}

	@Override
	@Transactional(readOnly = true)
	public JasperPrint getContractSummaryPdf(String productContractId, User userId, String strTimeZone, JRSwapFileVirtualizer virtualizer) {
		LOG.info("Get Contract Summary Pdf is called here." + productContractId);

		ProductContract productContract = productContractDao.findById(productContractId);

		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		TimeZone timeZone = TimeZone.getDefault();
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
		sdf.setTimeZone(timeZone);

		SimpleDateFormat dformat = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(timeZone);

		try {
			DecimalFormat df = null;
			if (productContract.getDecimal() != null) {
				if (productContract.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (productContract.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (productContract.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (productContract.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (productContract.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (productContract.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				}
			}

			Resource resource = applicationContext.getResource("classpath:reports/GenerateContractSummaryReport.jasper");
			File jasperfile = resource.getFile();

			ContractSummaryPojo summary = new ContractSummaryPojo();
			String imgPath = context.getRealPath("resources/images");

			// Contract information
			summary.setContractId(productContract.getContractId() != null ? productContract.getContractId() : "");
			summary.setContractName(productContract.getContractName() != null ? productContract.getContractName() : "");
			summary.setEventId(productContract.getEventId() != null ? productContract.getEventId() : "");
			String contractCreator = "";
			if (productContract.getContractCreator() != null) {
				contractCreator = productContract.getContractCreator().getName();
				contractCreator += "\r\n" + productContract.getContractCreator().getCommunicationEmail();
				if (StringUtils.checkString(productContract.getContractCreator().getBuyer().getCompanyContactNumber()).length() > 0) {
					contractCreator += "\r\nTel: " + productContract.getContractCreator().getBuyer().getCompanyContactNumber();
				}
				if (StringUtils.checkString(productContract.getContractCreator().getBuyer().getFaxNumber()).length() > 0) {
					contractCreator += "\r\nFax: " + productContract.getContractCreator().getBuyer().getFaxNumber();
				}
				if (StringUtils.checkString(productContract.getContractCreator().getPhoneNumber()).length() > 0) {
					contractCreator += "\r\nHP: " + productContract.getContractCreator().getPhoneNumber();
				}
			}
			summary.setContractCreator(StringUtils.checkString(contractCreator));
			summary.setSapContractNumber(productContract.getSapContractNumber() != null ? productContract.getSapContractNumber() : "");

			// Contract Details
			summary.setRenewalContract(Boolean.TRUE == productContract.getRenewalContract() ? "Yes" : "No");
			summary.setReferenceNumber(productContract.getContractReferenceNumber() != null ? productContract.getContractReferenceNumber() : "");
			summary.setPreviousContractNumber(productContract.getPreviousContractNo() != null ? productContract.getPreviousContractNo() : "");
			summary.setBusinessUnit(productContract.getBusinessUnit() != null ? productContract.getBusinessUnit().getUnitName() : "");
			summary.setGroupCode(productContract.getGroupCodeStr() != null ? productContract.getGroupCodeStr() : "");
			summary.setSupplier(productContract.getSupplier() != null ? productContract.getSupplier().getSupplier().getCompanyName() : productContract.getSupplierName());
			summary.setProcurementCategory(productContract.getProcurementCategory() != null ? productContract.getProcurementCategory().getProcurementCategories() : "");
			summary.setRemark(productContract.getRemark() != null ? productContract.getRemark() : " ");
			StringBuilder aType = new StringBuilder("");
			if (productContract.getAgreementType() != null) {
				aType.append(productContract.getAgreementType().getAgreementType());
				if (StringUtils.checkString(productContract.getAgreementType().getDescription()).length() > 0) {
					aType.append(" - " + productContract.getAgreementType().getDescription());
				}
			}
			summary.setAgreementType(aType.toString());

			// Contract Period
			summary.setContractStartDate(productContract.getContractStartDate() != null ? dformat.format(productContract.getContractStartDate()) : "");
			summary.setContractEndDate(productContract.getContractEndDate() != null ? dformat.format(productContract.getContractEndDate()) : "");

			// Contract Value
			summary.setBaseCurrency(productContract.getCurrency() != null ? productContract.getCurrency().getCurrencyCode() : "");
			summary.setDecimal(productContract.getDecimal());
			summary.setContractValue(productContract.getContractValue() != null ? df.format(productContract.getContractValue()) : df.format(BigDecimal.ZERO));

			if (productContract.getDocumentDate() != null) {
				summary.setDocumentDate(dformat.format(productContract.getDocumentDate()));
			}

			// Contract Documents
			List<ContractDocumentPojo> documentList = new ArrayList<ContractDocumentPojo>();
			List<ContractDocument> document = contractDocumentDao.findAllContractdocsbyContractId(productContract.getId());
			if (CollectionUtil.isNotEmpty(document)) {
				for (ContractDocument docs : document) {
					ContractDocumentPojo item = new ContractDocumentPojo();
					item.setDescription(docs.getDescription());
					item.setFileName(docs.getFileName());
					item.setUploadDate(sdf.format(docs.getUploadDate()));
					item.setUploadedBy(docs.getUploadedBy() != null ? docs.getUploadedBy().getName() : "");
					LOG.info(">>>>>>>" + item.getUploadDate());
					item.setFileSize(docs.getFileSizeInKb() != null ? docs.getFileSizeInKb() : 0);
					documentList.add(item);
				}
			}

			summary.setDocuments(documentList);

			// LOA And Agreement Document
			List<ContractLoaAndAgreementPojo> laDocumentList = new ArrayList<ContractLoaAndAgreementPojo>();
			List<ContractLoaAndAgreement> laDocument = contractLoaAndAgreementDao.findPlainContractLoaAndAgreementByContractId(productContract.getId());
			LOG.info("Document " + laDocument);
			if (CollectionUtil.isNotEmpty(laDocument)) {
				for (ContractLoaAndAgreement docs : laDocument) {
					ContractLoaAndAgreementPojo item = new ContractLoaAndAgreementPojo();
					item.setLoaFileName(docs.getLoaFileName());
					item.setLoaDescription(docs.getLoaDescription());
					item.setLoaUploadDate(docs.getLoaUploadDate() != null ? sdf.format(docs.getLoaUploadDate()) : "");
					item.setLoaUploadedBy(docs.getLoaUploadedBy() != null ? docs.getLoaUploadedBy().getName() : "");
					item.setAgreementUploadedBy(docs.getAgreementUploadedBy() != null ? docs.getAgreementUploadedBy().getName() : "");
					item.setLoaFileSizeInKb(docs.getLoaFileSizeInKb() != null ? docs.getLoaFileSizeInKb() : 0);
					item.setAgreementFileName(docs.getAgreementFileName() != null ? docs.getAgreementFileName() : "");
					item.setAgreementDescription(docs.getAgreementDescription() != null ? docs.getAgreementDescription() : "");
					item.setAgreementUploadDate(docs.getAgreementUploadDate() != null ? sdf.format(docs.getAgreementUploadDate()) : "");
					item.setAgreementFileSizeInKb(docs.getAgreementFileSizeInKb() != null ? docs.getAgreementFileSizeInKb() : 0);
					item.setLoaDate(docs.getLoaDate() != null ? dformat.format(docs.getLoaDate()) : "");
					item.setAgreementDate(docs.getAgreementDate() != null ? dformat.format(docs.getAgreementDate()) : "");
					if (StringUtils.checkString(summary.getLoaDocument()).length() == 0) {
						summary.setLoaDocument(docs.getLoaFileName() != null ? "COMPLETE" : "INCOMPLETE");
					}
					if (StringUtils.checkString(summary.getLoaDate()).length() == 0) {
						summary.setLoaDate(docs.getLoaDate() != null ? dformat.format(docs.getLoaDate()) : "");
					}
					if (StringUtils.checkString(summary.getAgreementDocument()).length() == 0) {
						summary.setAgreementDocument(docs.getAgreementFileName() != null ? "COMPLETE" : "INCOMPLETE");
					}
					if (StringUtils.checkString(summary.getAgreementDate()).length() == 0) {
						summary.setAgreementDate(docs.getAgreementDate() != null ? dformat.format(docs.getAgreementDate()) : "");
					}

					laDocumentList.add(item);
				}
			}

			summary.setLoaAndAssingDocument(laDocumentList);

			// Contract Audit
			List<ContractAudit> requestAudit = contractAuditDao.getContractAuditByContractId(productContract.getId());
			List<ContractAuditPojo> auditList = new ArrayList<ContractAuditPojo>();
			if (CollectionUtil.isNotEmpty(requestAudit)) {
				for (ContractAudit audit : requestAudit) {
					ContractAuditPojo contractaudit = new ContractAuditPojo();
					contractaudit.setActionDate(audit.getActionDate() != null ? sdf.format(audit.getActionDate()) : "");
					contractaudit.setAction(audit.getAction().toString());
					contractaudit.setDescription(audit.getDescription());
					contractaudit.setActionBy(audit.getActionBy() != null && StringUtils.checkString(audit.getActionBy().getName()).length() > 0 ? audit.getActionBy().getName() : "");
					auditList.add(contractaudit);
				}
			}
			summary.setContractAudit(auditList);

			// Contract Reminders
			List<ProductContractReminder> reminder = productContractReminderDao.getAllContractRemindersByContractId(productContract.getId());
			List<ProductContractReminderPojo> reminderList = new ArrayList<ProductContractReminderPojo>();
			if (CollectionUtil.isNotEmpty(reminder)) {
				int index = 1;
				for (ProductContractReminder reminders : reminder) {
					ProductContractReminderPojo contractReminder = new ProductContractReminderPojo();
					contractReminder.setReminderDate(reminders.getReminderDate() != null ? (index + ". " + dformat.format(reminders.getReminderDate())) : "");
					reminderList.add(contractReminder);
					index++;
				}
			}
			summary.setContractExpiryReminder(reminderList);

			// Contract Notify User
			List<ProductContractNotifyUsers> notifyUsers = productContractNotifyUsersDao.getAllContractNotifyUsersByContractId(productContract.getId());
			List<ProductContractNotifyUserPojo> notifyUser = new ArrayList<ProductContractNotifyUserPojo>();
			if (CollectionUtil.isNotEmpty(notifyUsers)) {
				int index = 1;
				for (ProductContractNotifyUsers user : notifyUsers) {
					ProductContractNotifyUserPojo contractNotifyUser = new ProductContractNotifyUserPojo();
					contractNotifyUser.setUserName(index + ". " + user.getUser().getName());
					notifyUser.add(contractNotifyUser);
					index++;
				}
			}

			summary.setReminderNotifyUser(notifyUser);

			// Contract Approvals
			List<ContractApproval> approvals = productContract.getApprovals();
			List<ContractApprovalUserPojo> approvalList = new ArrayList<ContractApprovalUserPojo>();
			if (CollectionUtil.isNotEmpty(approvals)) {
				for (ContractApproval item : approvals) {
					ContractApprovalUserPojo approve = new ContractApprovalUserPojo();
					approve.setLevel(item.getLevel());
					List<ContractApprovalUserPojo> approvUserList = new ArrayList<ContractApprovalUserPojo>();
					String userName = "";
					if (CollectionUtil.isNotEmpty(item.getApprovalUsers())) {
						Integer index = 1;
						boolean statusFlag = false;
						boolean statusAndFlag = false;
						for (ContractApprovalUser usr : item.getApprovalUsers()) {
							ContractApprovalUserPojo usrs = new ContractApprovalUserPojo();
							Integer cnt = item.getApprovalUsers().size();

							if (usr.getApprovalStatus() == ApprovalStatus.APPROVED && usr.getApproval().getApprovalType() == ApprovalType.OR) {
								statusFlag = true;
							}
							if (usr.getApprovalStatus() == ApprovalStatus.PENDING && usr.getApproval().getApprovalType() == ApprovalType.AND) {
								statusAndFlag = true;
							}
							userName += "  " + usr.getUser().getName() + "  ";
							if (cnt > index) {
								userName += usr.getApproval().getApprovalType() != null ? usr.getApproval().getApprovalType().name() : "" + "  ";
								userName += "  ";
							}
							if (cnt == index) {
								usrs.setName(userName);
								LOG.info(" ************** " + usrs.getName());
								if (usr.getApproval() != null) {
									usrs.setType(usr.getApproval().getApprovalType().name());
								}
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
			summary.setApprovalList(approvalList);

			// Contract Items
			List<ProductContractItemsPojo> itemDetails = new ArrayList<ProductContractItemsPojo>();
			List<ProductContractItems> Items = findAllContractItemsByContractId(productContract.getId());
			if (CollectionUtil.isNotEmpty(Items)) {
				for (ProductContractItems item : Items) {
					ProductContractItemsPojo ec = new ProductContractItemsPojo();
					ec.setItemName(item.getItemName());
					ec.setContractItemNumber(item.getContractItemNumber());
					ec.setItemCode(item.getItemCode() != null ? item.getItemCode() : "");
					ec.setProductCategory(item.getProductCategory() != null ? item.getProductCategory().getProductName() : "");
					ec.setUom(item.getUom().getUom() != null ? item.getUom().getUom() : "");
					ec.setQuantityStr(item.getQuantity() != null ? df.format(item.getQuantity()) : df.format(BigDecimal.ZERO));
					ec.setBalanceQuantityStr(item.getBalanceQuantity() != null ? df.format(item.getBalanceQuantity()) : df.format(BigDecimal.ZERO));
					ec.setUnitPriceStr(item.getUnitPrice() != null ? df.format(item.getUnitPrice()) : df.format(BigDecimal.ZERO));
					ec.setDeleted(item.getDeleted());
					itemDetails.add(ec);
				}
			}
			summary.setItems(itemDetails);

			// Comments
			List<ContractCommentPojo> commentDetails = new ArrayList<ContractCommentPojo>();
			List<ContractComment> comments = findAllContractCommentsByContractId(productContract.getId());
			if (CollectionUtil.isNotEmpty(comments)) {
				for (ContractComment item : comments) {
					ContractCommentPojo ec = new ContractCommentPojo();
					ec.setComment(item.getComment());
					ec.setCreatedBy(item.getCreatedBy().getName());
					if (item.getCreatedDate() != null) {
						ec.setCreatedDate(item.getCreatedDate() != null ? sdf.format(item.getCreatedDate()) : "");
					}
					commentDetails.add(ec);
				}
			}

			summary.setApprovalComments(commentDetails);

			List<ContractTeamMember> teamMembers = contractTeamMemberDao.findContractTeamMemberById(productContract.getId());
			if (CollectionUtil.isNotEmpty(teamMembers)) {
				List<ContractTeamUserPojo> list = new ArrayList<ContractTeamUserPojo>();
				for (ContractTeamMember tm : teamMembers) {
					ContractTeamUserPojo tmp = new ContractTeamUserPojo();
					tmp.setAccess(tm.getAccessType());
					tmp.setLoginEmail(tm.getUser().getLoginId());
					tmp.setName(tm.getUser().getName());
					list.add(tmp);
				}
				summary.setTeamMembers(list);
			}

			if (Boolean.TRUE == productContract.getEnableApprovalReminder()) {
				summary.setReminderAfterHour(productContract.getReminderAfterHour() != null ? productContract.getReminderAfterHour() : 0);
				summary.setReminderCount(productContract.getReminderCount() != null ? productContract.getReminderCount() : 0);
			}

			List<ContractSummaryPojo> requestSummary = Arrays.asList(summary);
			parameters.put("CONTRACT_SUMMARY", requestSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(requestSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (Exception e) {
			LOG.error("Could not generate RFS Summary Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	public List<ContractApprovalUser> fetchAllApprovalUsersByContractId(String id) {
		return productContractDao.fetchAllApprovalUsersByContractId(id);
	}

	@Override
	public List<ContractComment> findAllContractCommentsByContractId(String id) {
		return productContractDao.findAllContractCommentsByContractId(id);
	}

	@Override
	public EventPermissions getUserPemissionsForContract(String userId, String contractid) {
		return productContractDao.getUserPemissionsForContract(userId, contractid);
	}

	@Override
	public long findCountOfContractPendingApprovals(String tenantId, String id, TableDataInput input) {
		return productContractDao.findCountOfContractPendingApprovals(tenantId, id, input);
	}

	@Override
	public List<ProductContractPojo> getAllContractForApproval(String tenantId, String id, TableDataInput input) {
		return productContractDao.getAllContractForApproval(tenantId, id, input);
	}

	@Override
	public long findTotalFilteredContracForApproval(String tenantId, String userId, TableDataInput input) {
		return productContractDao.findTotalFilteredContracForApproval(tenantId, userId, input);
	}

	@Override
	public long findTotalCountContractForApproval(String tenantId, String userId, TableDataInput input) {
		return productContractDao.findTotalCountContractForApproval(tenantId, userId, input);
	}

	@Override
	public List<ProductContractPojo> findDraftContractListForTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		return productContractDao.findDraftContractListForTenant(tenantId, userId, input, startDate, endDate);
	}

	@Override
	public long findTotalFilteredDraftContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		return productContractDao.findTotalFilteredDraftContractByTenant(tenantId, userId, input, startDate, endDate);
	}

	@Override
	public List<ProductContractPojo> findPendingContractListByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		return productContractDao.findPendingContractListByTenant(tenantId, userId, input, startDate, endDate);
	}

	@Override
	public long findTotalFilteredPendingContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		return productContractDao.findTotalFilteredPendingContractByTenant(tenantId, userId, input, startDate, endDate);
	}

	@Override
	public List<ProductContractPojo> findTerminatedContractListByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		return productContractDao.findTerminatedContractListByTenant(tenantId, userId, input, startDate, endDate);
	}

	@Override
	public long findTotalFilteredTerminatedContractByTenant(String tenantId, String userId, TableDataInput input, Date startDate, Date endDate) {
		return productContractDao.findTotalFilteredTerminatedContractByTenant(tenantId, userId, input, startDate, endDate);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductContract getContractById(String id) {
		return productContractDao.findById(id);
	}

	@Override
	public List<ProductContractItems> findAllContractItemsByContractId(String id) {
		return productContractItemsDao.findProductContractItemsByProductContractId(id);
	}

	@Override
	@Transactional(readOnly = false)
	public ProductContract contractFinish(String contractId, User loggedInUser) throws ApplicationException {
		ProductContract contractObj = getProductContractById(contractId);
		if (Boolean.TRUE == contractObj.getEnableApprovalReminder()) {
			if (contractObj.getReminderAfterHour() == null) {
				throw new ApplicationException(messageSource.getMessage("approval.reminder.add.hour", new Object[] {}, Global.LOCALE));
			}
			if (contractObj.getReminderCount() == null) {
				throw new ApplicationException(messageSource.getMessage("approval.reminder.count.reminder", new Object[] {}, Global.LOCALE));
			}
		}

		if (CollectionUtil.isNotEmpty(contractObj.getApprovals()) && (ContractStatus.ACTIVE == contractObj.getStatus() || ContractStatus.SUSPENDED == contractObj.getStatus())) {
			for (ContractApproval approval : contractObj.getApprovals()) {
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
			contractObj.setStatus(ContractStatus.PENDING);
		}

		return productContractDao.update(contractObj);

	}

	@Override
	public ProductContract findProductContractByBuyerId(String sapContractNumber, String tenantId) {
		return productContractDao.findProductContractByBuyerId(sapContractNumber, tenantId);
	}

	@Override
	public void deleteProductContractById(String productContractId) {
		productContractDao.deleteProductContractById(productContractId);
	}

}
