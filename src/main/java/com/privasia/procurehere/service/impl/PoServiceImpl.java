package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.ImageIcon;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.dao.impl.PrDaoImpl;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.enums.*;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.*;
import net.sf.jasperreports.engine.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.PoAcceptDeclinePojo;
import com.privasia.procurehere.core.pojo.PoItemPojo;
import com.privasia.procurehere.core.pojo.PoItemSupplierPojo;
import com.privasia.procurehere.core.pojo.PoPojo;
import com.privasia.procurehere.core.pojo.PoReviseSnapshot;
import com.privasia.procurehere.core.pojo.PoRevisedSnapshotItem;
import com.privasia.procurehere.core.pojo.PoSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPoPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.web.controller.PrItemsSummaryPojo;
import com.privasia.procurehere.web.controller.PrSummaryPojo;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author Nitin Otageri
 */
@Service
@Transactional(readOnly = true)
public class PoServiceImpl implements PoService {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	PoDao poDao;

	@Autowired
	BuyerAddressDao buyerAddressDao;

	@Autowired
	PoItemDao poItemDao;

	@Autowired
	ServletContext context;

	@Autowired
	PoAuditService poAuditService;

	@Autowired
	PoService poService;

	@Autowired
	PrService prService;

	@Autowired
	PrDao prDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	PrItemDao prItemDao;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	PoFinanceDao poFinanceDao;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	PoReportDao poReportDao;

	@Autowired
	SupplierSettingsDao supplierSettingsDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	PurchaseOrderDocumentDao purchaseOrderDocumentDao;

	@Autowired
	POSnapshotDocumentDao poSnapshotDocumentDao;

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	UserService userService;

	@Autowired
	SupplierAuditTrailDao supplierAuditTrailDao;

	@Override
	@Transactional(readOnly = false)
	@Deprecated
	public Po createPo(Po po) {
		// po.setPoId(eventIdSettingsDao.generateEventId(po.getBuyer().getId(), "PO"));
		// LOG.info("Generated PO ID as : " + po.getPoId());
		// return poDao.saveOrUpdate(po);

		throw new RuntimeException("PoServiceImpl createPo Not implemented");
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { Exception.class })
	public Po savePo(Po po) {

		// Create Delivery Address if not already created.
		BuyerAddress deliveryAddress = buyerAddressDao
				.getBuyerAddressForTenantByTitle(po.getDeliveryAddress().getTitle(), po.getBuyer().getId());
		if (deliveryAddress == null) {
			deliveryAddress = buyerAddressDao.saveOrUpdate(po.getDeliveryAddress());
		}
		po.setDeliveryAddress(deliveryAddress);

		// Create Correspondence Address if not already created.
		BuyerAddress correspondenceAddress = buyerAddressDao
				.getBuyerAddressForTenantByTitle(po.getCorrespondenceAddress().getTitle(), po.getBuyer().getId());
		if (correspondenceAddress == null) {
			correspondenceAddress = buyerAddressDao.saveOrUpdate(po.getCorrespondenceAddress());
		}
		po.setCorrespondenceAddress(correspondenceAddress);

		// Create Delivery Address if not already created.
		if (CollectionUtil.isNotEmpty(po.getPoItems())) {
			for (PoItem poItem : po.getPoItems()) {
				if (poItem.getDeliveryAddress() != null
						&& StringUtils.checkString(poItem.getDeliveryAddress().getId()).length() == 0) {
					deliveryAddress = buyerAddressDao.getBuyerAddressForTenantByTitle(
							poItem.getDeliveryAddress().getTitle(), po.getBuyer().getId());
					if (deliveryAddress == null) {
						deliveryAddress = buyerAddressDao.saveOrUpdate(poItem.getDeliveryAddress());
					}
					poItem.setDeliveryAddress(deliveryAddress);
				}
			}
		}

		return poDao.saveOrUpdate(po);
	}

	@Override
	@Transactional(readOnly = false)
	public Po updatePo(Po po) {
		return poDao.saveOrUpdate(po);
	}

	@Override
	public List<PoItem> findAllPoItemByPoIdForSummary(String poId) {
		return poItemDao.getAllPoItemByPoId(poId);
	}

	@Override
	public long findTotalPo(String tenantId) {
		return poDao.findTotalPo(tenantId);
	}

	@Override
	public List<Po> findAllPo(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return poDao.findAllPo(tenantId, input, startDate, endDate);
	}

	@Override
	public long findTotalFilteredPo(String tenantId, TableDataInput input, Date startDate, Date endDate) {
		return poDao.findTotalFilteredPo(tenantId, input, startDate, endDate);
	}

	@Override
	public Po findPoById(String poId) {
		Po po = poDao.findById(poId);
		if (po != null) {
			if (po.getCurrency() != null) {
				po.getCurrency().getCurrencyCode();
			}
		}
		if (po.getSupplier() != null) {
			po.getSupplier().getFullName();
			if (po.getSupplier().getSupplier() != null) {
				po.getSupplier().getSupplier().getCompanyName();
			}
		}
		if (po.getBusinessUnit() != null) {
			po.getBusinessUnit().getDisplayName();
		}
		if (po.getDeliveryAddress() != null) {
			po.getDeliveryAddress().getTitle();
			if (po.getDeliveryAddress().getState() != null) {
				po.getDeliveryAddress().getState().getStateName();
				if (po.getDeliveryAddress().getState().getCountry() != null) {
					po.getDeliveryAddress().getState().getCountry().getCountryCode();
				}

			}
		}
		if (po.getCorrespondenceAddress() != null) {
			po.getCorrespondenceAddress().getTitle();
			if (po.getCorrespondenceAddress().getState() != null) {
				po.getCorrespondenceAddress().getState().getStateName();
				if (po.getCorrespondenceAddress().getState().getCountry() != null) {
					po.getCorrespondenceAddress().getState().getCountry().getCountryCode();
				}
			}
		}
		return po;
	}

	@Override
	public Po loadPoById(String poId) {
		Po po = poDao.findById(poId);
		if (po != null) {
			if (po.getCurrency() != null) {
				po.getCurrency().getCurrencyCode();
			}
		}
		if (po.getSupplier() != null) {
			po.getSupplier().getFullName();
			if (po.getSupplier().getSupplier() != null) {
				po.getSupplier().getSupplier().getCompanyName();
			}
		}
		if (po.getBusinessUnit() != null) {
			po.getBusinessUnit().getDisplayName();
		}
		if (po.getDeliveryAddress() != null) {
			po.getDeliveryAddress().getTitle();
			if (po.getDeliveryAddress().getState() != null) {
				po.getDeliveryAddress().getState().getStateName();
				if (po.getDeliveryAddress().getState().getCountry() != null) {
					po.getDeliveryAddress().getState().getCountry().getCountryCode();
				}

			}
		}
		if (po.getCorrespondenceAddress() != null) {
			po.getCorrespondenceAddress().getTitle();
			if (po.getCorrespondenceAddress().getState() != null) {
				po.getCorrespondenceAddress().getState().getStateName();
				if (po.getCorrespondenceAddress().getState().getCountry() != null) {
					po.getCorrespondenceAddress().getState().getCountry().getCountryCode();
				}
			}
		}
		if (CollectionUtil.isNotEmpty(po.getPoItems())) {
			for (PoItem item : po.getPoItems()) {
				item.getItemName();
			}
		}
		return po;
	}

	@Override
	public void downloadPoReports(String tenantId, String[] poArr, HttpServletResponse response, HttpSession session,
								  boolean select_all, Date startDate, Date endDate, SearchFilterPoPojo searchFilterPoPojo) {
		try {
			List<PoSupplierPojo> poList = poDao.findSearchPoByIds(tenantId, poArr, select_all, startDate, endDate,
					searchFilterPoPojo);
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "poReports.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("PO Report");

			// Style of Heading Cells
			CellStyle styleHeading = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading.setFont(font);
			styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

			CellStyle totalStyle = workbook.createCellStyle();
			totalStyle.setAlignment(CellStyle.ALIGN_RIGHT);

			// Creating Headings
			Row rowHeading = sheet.createRow(0);
			int i = 0;
			for (String column : Global.SUPPLIER_PO_REPORT_EXCEL_COLUMNS) {
				Cell cell = rowHeading.createCell(i++);

				cell.setCellValue(column);
				cell.setCellStyle(styleHeading);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));

			int r = 1;
			// Write Data into Excel
			for (PoSupplierPojo po : poList) {
				// For Financial Standard
				DecimalFormat df = null;
				if (po.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (po.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (po.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (po.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (po.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (po.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(r - 1);
				row.createCell(cellNum++)
						.setCellValue(StringUtils.checkString(po.getPoNumber()).length() > 0 ? po.getPoNumber() : "");
				row.createCell(cellNum++)
						.setCellValue(StringUtils.checkString(po.getName()).length() > 0 ? po.getName() : "");
				// row.createCell(cellNum++).setCellValue(StringUtils.checkString(po.getDescription()).length()
				// > 0 ?
				// po.getDescription() : "");
				row.createCell(cellNum++).setCellValue(
						StringUtils.checkString(po.getBuyerCompanyName()).length() > 0 ? po.getBuyerCompanyName() : "");
				row.createCell(cellNum++).setCellValue(
						StringUtils.checkString(po.getBusinessUnit()).length() > 0 ? po.getBusinessUnit() : "");
				row.createCell(cellNum++)
						.setCellValue(po.getCreatedDate() != null ? sdf.format(po.getCreatedDate()) : "");
				row.createCell(cellNum++)
						.setCellValue(po.getAcceptRejectDate() != null ? sdf.format(po.getAcceptRejectDate()) : "");
				row.createCell(cellNum++)
						.setCellValue(StringUtils.checkString(po.getCurrency()).length() > 0 ? po.getCurrency() : "");
				Cell grandTotalCell = row.createCell(cellNum++);
				grandTotalCell.setCellStyle(totalStyle);
				grandTotalCell.setCellValue(po.getGrandTotal() != null ? df.format(po.getGrandTotal()) : "");
				row.createCell(cellNum++).setCellValue(po.getStatus() != null ? po.getStatus().toString() : "");

			}
			// Auto Fit
			for (int k = 0; k < 15; k++) {
				sheet.autoSizeColumn(k, true);
			}

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					LOG.info("Successfully written in Excel===========================");
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage(), e);
				}
			}
			LOG.info("updating Po Report Sent");
		} catch (Exception e) {
			LOG.error("Error while downloading PO Reports Excel : " + e.getMessage(), e);
		}
	}

	@Override
	public long findPoCountBasedOnStatusAndTenant(String loggedInUser, String tenantId, PoStatus status) {
		return poDao.findPoCountBasedOnStatusAndTenant(loggedInUser, tenantId, status);
	}

	@Override
	public long findPoCountBasedOnStatusAndTenantAndBuisnessUnit(String loggedInUser, String tenantId, PoStatus status,List<String> businessUnitIds) {
		return poDao.findPoCountBasedOnStatusAndTenantAndBusinessUnit(loggedInUser, tenantId, status,businessUnitIds);
	}

	@Override
	public List<Po> findAllPoByStatus(String loggedInUser, String tenantId, TableDataInput input, PoStatus status) {
		return poDao.findAllPoByStatus(loggedInUser, tenantId, input, status);
	}

	@Override
	public long findTotalFilteredPoByStatus(String loggedInUserId, String tenantId, TableDataInput input,
											PoStatus status) {
		return poDao.findTotalFilteredPoByStatus(loggedInUserId, tenantId, input, status);
	}

	@Override
	public Po getLoadedPoById(String poId) {
		Po po = poDao.findByPoId(poId);
		if (po != null) {
			LOG.info("PO Not null : " + po.getId());
			if (po.getCorrespondenceAddress() != null) {
				po.getCorrespondenceAddress().getState().getCountry().getCountryName();
			}
			if (po.getBuyer() != null) {
				po.getBuyer().getCompanyName();
				po.getBuyer().getCompanyContactNumber();
				po.getBuyer().getFaxNumber();
				po.getBuyer().getLine1();
				po.getBuyer().getLine2();
				po.getBuyer().getCity();
				if (po.getBuyer().getState() != null)
					po.getBuyer().getState().getStateName();
				if (po.getBuyer().getRegistrationOfCountry() != null)
					po.getBuyer().getRegistrationOfCountry().getCountryName();
			}
			if (po.getSupplier() != null) {
				FavouriteSupplier favSupplier = po.getSupplier();
				favSupplier.getSupplier().getCompanyName();
				favSupplier.getSupplier().getFaxNumber();
				favSupplier.getSupplier().getCompanyContactNumber();
				favSupplier.getSupplier().getLine1();
				favSupplier.getSupplier().getLine2();
				favSupplier.getSupplier().getCity();
				if (favSupplier.getSupplier().getState() != null) {
					favSupplier.getSupplier().getState().getStateName();
				}
				if (favSupplier.getSupplier().getRegistrationOfCountry() != null) {
					favSupplier.getSupplier().getRegistrationOfCountry().getCountryCode();
				}
			}
			if (po.getBusinessUnit() != null) {
				po.getBusinessUnit().getUnitName();
			}
			if (po.getCreatedBy() != null) {
				po.getCreatedBy().getLoginId();
				po.getCreatedBy().getName();
			}
			if (po.getPaymentTermes() != null) {
				po.getPaymentTermes().getPaymentTermCode();
				po.getPaymentTermes().getPaymentDays();
				po.getPaymentTermes().getId();
				po.getPaymentTermes().getDescription();
			}
			for (PoApproval approval : po.getApprovals()) {
				for (PoApprovalUser approvalUser : approval.getApprovalUsers()) {
					approvalUser.getRemarks();
					approvalUser.getUser().getLoginId();
				}
			}

			if (CollectionUtil.isNotEmpty(po.getComments())) {
				LOG.info(" Comments  >>>  " + po.getComments());
				for (PoComment comment : po.getComments()) {
					if (comment.getCreatedBy() != null) {
						comment.getCreatedBy().getLoginId();
					}
					LOG.info(" Comments  >>>  " + comment.getComment());
				}
			}

			if (po.getDeliveryAddress() != null) {
				po.getDeliveryAddress().getState();
				if (po.getDeliveryAddress().getState() != null) {
					po.getDeliveryAddress().getState().getStateName();
				}
				po.getDeliveryAddress().getCountry();
				if (po.getDeliveryAddress().getCountry() != null) {
					po.getDeliveryAddress().getCountry().getCountryName();
				}
			}
		}
		return po;

	}

	@Override
	public Po getPoItemByPoNumberAndBuyerId(String poNumber, String buyerId) {
		return poDao.getPoItemByPoNumberAndBuyerId(poNumber, buyerId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<PoItem> findAllPoItemByPoId(String poId) {
		List<PoItem> returnList = new ArrayList<PoItem>();
		List<PoItem> list = poItemDao.getAllPoItemByPoId(poId);
		LOG.info("List :" + list.size());

		if (CollectionUtil.isNotEmpty(list)) {
			for (PoItem item : list) {
				PoItem parent = item.createShallowCopy();
				if (item.getParent() == null) {
					returnList.add(parent);
				}

				if (item.getProductCategory() != null) {
					item.getProductCategory().getProductName();
				}

				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (PoItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<PoItem>());
						}
						if (child.getUnit() != null) {
							child.getUnit().getUom();
						}

						if (child.getProductCategory() != null) {
							child.getProductCategory().getProductName();
						}

						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public List<PoItem> findAllPoItemByPoIdPojo(String poId) {
		List<PoItem> returnList = new ArrayList<PoItem>();
		List<PoItem> list = poItemDao.getAllPoItemByPoIdPojo(poId);
		LOG.info("List :" + list.size());

		return returnList;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updatePoStatus(String poId, PoStatus poStatus, String remarks, User loggedInUser,
							   JRSwapFileVirtualizer virtualizer) throws ApplicationException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		Po po = poDao.findByPoId(poId);
		if (po == null) {
			throw new ApplicationException("PO details not found");
		}
		String buyerAuditMessage = "";
		String supplierAuditMessage = "";
		PoAuditType auditType = null;
		Boolean revised = po.getRevised();
		PoAudit buyerAudit = new PoAudit();

		switch (poStatus) {
			case ACCEPTED: {
				buyerAuditMessage = messageSource.getMessage("po.buyerAudit.accepted",
						new Object[] { po.getPoNumber(), po.getSupplier().getSupplier().getCompanyName(), remarks },
						Global.LOCALE);
				supplierAuditMessage = messageSource.getMessage("po.supplierAudit.accepted",
						new Object[] { po.getPoNumber(), remarks }, Global.LOCALE);
				auditType = PoAuditType.ACCEPTED;
				break;
			}
			case DECLINED: {
				buyerAuditMessage = messageSource.getMessage("po.buyerAudit.declined",
						new Object[] { po.getPoNumber(), po.getSupplier().getSupplier().getCompanyName(), remarks },
						Global.LOCALE);
				supplierAuditMessage = messageSource.getMessage("po.supplierAudit.declined",
						new Object[] { po.getPoNumber(), remarks }, Global.LOCALE);
				auditType = PoAuditType.DECLINED;
				break;
			}
			case CANCELLED: {
				buyerAuditMessage = messageSource.getMessage("po.buyeraudit.cancelled",
						new Object[] { po.getPoNumber(), remarks }, Global.LOCALE);
				supplierAuditMessage = messageSource.getMessage("po.audit.cancelled",
						new Object[] { po.getPoNumber(), po.getBuyer().getCompanyName(), remarks }, Global.LOCALE);
				auditType = PoAuditType.CANCELLED;
				po.setCancelReason(remarks);

				LOG.info(">>>>>>>>>>>>>> status before refresh approval:"+po.getStatus());

				if(PoStatus.SUSPENDED.equals(po.getStatus()) || (po.getApprovals() != null && !po.getApprovals().isEmpty())){

					LOG.info(">>>>>>>>>>>>>> Create New Approval for Cancellation flow");
					poStatus = PoStatus.PENDING;
					po.setCancelled(Boolean.TRUE);
					po.setRevised(Boolean.FALSE);
					po.setOldStatus(po.getStatus());

					//RESET APPROVAL TO NEW APPROVAL FOR CANCELLATION
					try {
						List<PoApproval> approvalList = po.getApprovals();
						if (CollectionUtil.isNotEmpty(approvalList)) {
							for (PoApproval approval : approvalList) {
								approval.setDone(false);
								approval.setActive(false);
								for (PoApprovalUser user : approval.getApprovalUsers()) {
									user.setActionDate(null);
									user.setApprovalStatus(ApprovalStatus.PENDING);
									user.setRemarks(null);
									user.setActionDate(null);
								}
							}
						}
						LOG.info(">>>>>>>>>>>>>> Reset Approval Done");
					} catch (Exception e) {
						LOG.error("Error while converting to json " + e.getMessage(), e);
					}
				}

				//PH-4113 send email to supplier for cancelation only from suspended status not draft (internally)
				/*
				if (PoStatus.SUSPENDED.equals(po.getStatus())){
					if (po.getSupplier() != null) {
						try {
							LOG.info(" >>>>>>>>>>>>>> Send Email Cancelation to supplier");
							approvalService.sendCancelPoEmailNotificationToSupplier(po, remarks,
									SecurityLibrary.getLoggedInUser());
						} catch (Exception e) {
							LOG.info("Error while sending cancellation PO notification to supplier:" + e.getMessage(), e);
						}
					}
				}
				*/

				break;
			}
			case ORDERED: {
				po.setOrderedDate(new Date());
				po.setOrderedBy(loggedInUser);

				if (Boolean.TRUE == revised) {
					po.setRevisePoDetails(null);
					po.setRevised(Boolean.FALSE);
					try {
						poSnapshotDocumentDao.deleteDocument(po.getId());
					} catch (Exception e) {
					}
					if (po.getOldStatus() != PoStatus.READY) {
						PoAudit supplierAudit = new PoAudit();
						supplierAudit.setAction(PoAuditType.REVISED);
						supplierAudit.setActionBy(loggedInUser);
						supplierAudit.setActionDate(new Date());
						supplierAudit.setBuyer(po.getBuyer());
						supplierAudit.setSupplier(po.getSupplier().getSupplier());
						supplierAudit.setDescription("PO " + po.getPoNumber() + " ");
						supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
						supplierAudit.setPo(po);
						poAuditService.save(supplierAudit);
					}

				}

				buyerAuditMessage = messageSource.getMessage("po.audit.ordered",
						new Object[] { po.getPoNumber(),
								StringUtils.checkString(po.getSupplierName()).length() > 0 ? po.getSupplierName() : "" },
						Global.LOCALE);
				supplierAuditMessage = messageSource.getMessage("po.audit.received",
						new Object[] { po.getPoNumber(), po.getBuyer().getCompanyName() }, Global.LOCALE);
				auditType = PoAuditType.ORDERED;
				break;
			}
			case REVISE: {
				LOG.info(">>>>>>>>>>>>>>>>>>>>> PO is revised");
				try {
					String safeRemarks = (remarks != null) ? remarks : ""; // Replace null with an empty string
					snapShotAuditService.doPoAudit(po, loggedInUser, PoAuditType.REVISED,
							"PO " + po.getPoNumber() + " revised. " + safeRemarks, virtualizer,
							PoAuditVisibilityType.BUYER);
				} catch (Exception e) {
					LOG.info("Error while auditing revised " + e.getMessage(), e);
				}

				if (PoStatus.ACCEPTED == po.getStatus() || PoStatus.ORDERED == po.getStatus() || PoStatus.SUSPENDED == po.getStatus()) {
					try {
						snapShotAuditService.doPoAudit(po, loggedInUser, PoAuditType.SUSPENDED,
								"PO " + po.getPoNumber() + " revision finished: " + remarks, virtualizer,
								PoAuditVisibilityType.SUPPLIER);
					} catch (Exception e) {
						LOG.info("Error while auditing " + e.getMessage(), e);
					}

				}


				try {
					PoReviseSnapshot snapshot = new PoReviseSnapshot();
					snapshot.setId(po.getId());
					snapshot.setRequester(po.getRequester());
					snapshot.setGrandTotal(po.getGrandTotal());
					snapshot.setTaxDescription(po.getTaxDescription());
					snapshot.setAdditionalTax(po.getAdditionalTax());
					//snapshot.setDeliveryAddress(po.getCorrespondenceAddress());
					snapshot.setDeliveryAddressLine1(po.getDeliveryAddressLine1());
					snapshot.setDeliveryAddressLine2(po.getDeliveryAddressLine2());
					snapshot.setDeliveryAddressCity(po.getDeliveryAddressCity());
					snapshot.setDeliveryAddressState(po.getDeliveryAddressState());
					snapshot.setDeliveryAddressCountry(po.getDeliveryAddressCountry());
					snapshot.setDeliveryAddressTitle(po.getDeliveryAddressTitle());
					snapshot.setDeliveryAddressZip(po.getDeliveryAddressZip());
					snapshot.setDeliveryDate(po.getDeliveryDate());
					snapshot.setDeliveryReceiver(po.getDeliveryReceiver());
					if (po.getPoItems() != null) {
						List<PoRevisedSnapshotItem> items = new ArrayList<PoRevisedSnapshotItem>();
						for (PoItem poItem : po.getPoItems()) {
							PoRevisedSnapshotItem item = new PoRevisedSnapshotItem();
							item.setId(poItem.getId());
							item.setField1(poItem.getField1());
							item.setField2(poItem.getField2());
							item.setField3(poItem.getField3());
							item.setField4(poItem.getField4());
							item.setField5(poItem.getField5());
							item.setField6(poItem.getField6());
							item.setField7(poItem.getField7());
							item.setField8(poItem.getField8());
							item.setField9(poItem.getField9());
							item.setField10(poItem.getField10());
							item.setItemCode(poItem.getItemCode());
							item.setItemDescription(poItem.getItemDescription());
							item.setItemName(poItem.getItemName());
							item.setItemTax(poItem.getItemTax());
							item.setLevel(poItem.getLevel());
							item.setOrder(poItem.getOrder());
							item.setQuantity(poItem.getQuantity());
							item.setTaxAmount(poItem.getTaxAmount());
							item.setTotalAmount(poItem.getTotalAmount());
							item.setTotalAmountWithTax(poItem.getTotalAmountWithTax());
							item.setUnit(poItem.getUnit() != null ? poItem.getUnit().getUom() : "");
							item.setUnitPrice(poItem.getUnitPrice());

							items.add(item);
						}
						snapshot.setPoItems(items);
					}

					if (CollectionUtil.isNotEmpty(po.getPurchaseOrderDocuments())) {
						for (PurchaseOrderDocument doc : po.getPurchaseOrderDocuments()) {
							com.privasia.procurehere.core.entity.POSnapshotDocument docSnapShot = new com.privasia.procurehere.core.entity.POSnapshotDocument();
							docSnapShot.setId(doc.getId());
							docSnapShot.setCredContentType(doc.getCredContentType());
							docSnapShot.setDescription(doc.getDescription());
							docSnapShot.setFileData(doc.getFileData());
							docSnapShot.setFileName(doc.getFileName());
							docSnapShot.setFileSizeInKb(doc.getFileSizeInKb());
							docSnapShot.setInternal(doc.getInternal());
							docSnapShot.setPo(po);
							docSnapShot.setUploadDate(doc.getUploadDate());
							poSnapshotDocumentDao.saveOrUpdate(docSnapShot);
						}
					}

					ObjectMapper mapper = new ObjectMapper();
					String json = mapper.writeValueAsString(snapshot);
					LOG.info("json : " + json);
					po.setRevisePoDetails(json);

				} catch (Exception e) {
					LOG.error("Error while converting to json " + e.getMessage(), e);
				}

				po.setReviseJustification(remarks);
				po.setRevised(Boolean.TRUE);
				po.setCancelled(Boolean.FALSE); //SET THIS ONCE PO REVISION WAS APPROVED PH-4113
				po.setPoRevisedDate(new Date());
				po.setOldStatus(po.getStatus());
				auditType = PoAuditType.REVISED;
				break;
			}
			case SUSPENDED: {
				try {
					snapShotAuditService.doPoAudit(po, loggedInUser, PoAuditType.SUSPENDED,
							"PO " + po.getPoNumber() + " suspended: " + remarks, virtualizer,
							PoAuditVisibilityType.BUYER);



				} catch (Exception e) {
					LOG.info("Error while auditing revised " + e.getMessage(), e);
				}

				if (PoStatus.ACCEPTED == po.getStatus() || PoStatus.ORDERED == po.getStatus()) {
					try {

						snapShotAuditService.doPoAudit(po, loggedInUser, PoAuditType.SUSPENDED,
								"PO " + po.getPoNumber() + " suspended: " + remarks, virtualizer,
								PoAuditVisibilityType.SUPPLIER);
					} catch (Exception e) {
						LOG.info("Error while auditing " + e.getMessage(), e);
					}

				}
				try {

					List<PoApproval> approvalList = po.getApprovals();
					if (CollectionUtil.isNotEmpty(approvalList)) {
						for (PoApproval approval : approvalList) {
							approval.setDone(false);
							approval.setActive(false);
							for (PoApprovalUser user : approval.getApprovalUsers()) {
								user.setActionDate(null);
								user.setApprovalStatus(ApprovalStatus.PENDING);
								user.setRemarks(null);
								user.setActionDate(null);
							}
						}
					}


				} catch (Exception e) {
					LOG.error("Error while converting to json " + e.getMessage(), e);
				}

				po.setReviseJustification(remarks);
				po.setPoRevisedDate(new Date());
				po.setOldStatus(po.getStatus());
				break;
			}

			case READY: {
				throw new ApplicationException("Invalid transaction");
			}
			default:
				break;
		}

		po.setStatus(poStatus);

		if(PoStatus.REVISE == poStatus){
			//PH-4113
			Boolean isAutoPublish = buyerSettingsService.isAutoPublishePoSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("This PO isAuto published : "+isAutoPublish);
			List<PoApproval> approvalList = po.getApprovals();
			if (CollectionUtil.isNotEmpty(approvalList)) {
				for (PoApproval approval : approvalList) {
					approval.setDone(false);
					approval.setActive(false);
					for (PoApprovalUser user : approval.getApprovalUsers()) {
						user.setActionDate(null);
						user.setApprovalStatus(ApprovalStatus.PENDING);
						user.setRemarks(null);
						user.setActionDate(null);
					}
				}
				po.setStatus(PoStatus.PENDING);
			}


			if(!isAutoPublish && !CollectionUtil.isNotEmpty(approvalList)){
				po.setStatus(PoStatus.READY);
			}

			if(isAutoPublish && !CollectionUtil.isNotEmpty(approvalList)){
				po.setStatus(PoStatus.ORDERED);
			}
		}

		if (PoStatus.ACCEPTED == poStatus || PoStatus.DECLINED == poStatus) {
			po.setActionDate(new Date());
		}
		if (StringUtils.checkString(remarks).length() > 0
				&& (PoStatus.ACCEPTED == poStatus || PoStatus.DECLINED == poStatus)) {
			po.setSupplierRemark(remarks);
		}

		po = poDao.saveOrUpdate(po);

		if(po.getBuyer() != null ) {
			if (!PoStatus.SUSPENDED.equals(poStatus) && !PoStatus.REVISE.equals(poStatus)) {
				buyerAudit.setAction(auditType);
				buyerAudit.setActionBy(loggedInUser);
				buyerAudit.setActionDate(new Date());
				buyerAudit.setBuyer(po.getBuyer());
				if (po.getSupplier() != null && po.getSupplier().getSupplier() != null) {
					buyerAudit.setSupplier(po.getSupplier().getSupplier());
				}
				buyerAudit.setDescription(buyerAuditMessage);
				buyerAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
				buyerAudit.setPo(po);
				LOG.info("Po Audit :" + buyerAudit.toLogString());
				poAuditService.save(buyerAudit);
			}
		}
		if (po.getSupplier() != null && po.getSupplier().getSupplier() != null) {
			if(PoStatus.SUSPENDED != poStatus && PoStatus.REVISE != poStatus) {
				PoAudit supplierAudit = new PoAudit();
				supplierAudit.setAction(auditType);
				supplierAudit.setActionBy(loggedInUser);
				supplierAudit.setActionDate(new Date());
				supplierAudit.setBuyer(po.getBuyer());
				supplierAudit.setSupplier(po.getSupplier().getSupplier());
				supplierAudit.setDescription(supplierAuditMessage);
				supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
				supplierAudit.setPo(po);
				poAuditService.save(supplierAudit);
			}
		}

		if (PoStatus.CANCELLED == po.getStatus()) {
			// If any New status finance POs delete
			List<FinancePo> poList = poFinanceDao.findFinancePoByIdsAndStatus(po.getId());
			if (CollectionUtil.isNotEmpty(poList)) {
				for (FinancePo fpo : poList) {
					poFinanceDao.deletePo(fpo.getId());
				}
			}
		}

		if (PoStatus.ACCEPTED == poStatus || PoStatus.DECLINED == poStatus) {
			ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(po.getBuyer().getId());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()
					&& Boolean.TRUE == erpSetup.getEnablePoAcceptDeclinePush()) {

				PoAcceptDeclinePojo poAcceptDeclinePojo = new PoAcceptDeclinePojo();
				poAcceptDeclinePojo.setId(po.getId());
				poAcceptDeclinePojo.setActionDate(new Date());
				poAcceptDeclinePojo.setReferenceNumber(po.getReferenceNumber());
				poAcceptDeclinePojo.setPoDocNo(po.getPoNumber());
				if (PoStatus.ACCEPTED == po.getStatus()) {
					poAcceptDeclinePojo.setAccepted(Boolean.TRUE);
				}
				if (PoStatus.DECLINED == po.getStatus()) {
					poAcceptDeclinePojo.setAccepted(Boolean.FALSE);
				}
				try {
					erpIntegrationService.sendPoAcceptDeclineToErp(poAcceptDeclinePojo, erpSetup);
				} catch (Exception e) {

				}
			}
			if((erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable()
					&& Boolean.TRUE == erpSetup.getEnablePoSendToSap()) && po.getFromIntegration()) {
				Boolean isSentToSap = erpIntegrationService.sendPoAcceptanceToSap(po, poStatus, Boolean.FALSE, remarks);
				po.setSentToSap(isSentToSap);
				po.setSentToSapFailed(isSentToSap ? Boolean.FALSE : Boolean.TRUE);
				po.setOldStatus(poStatus);
				if(!isSentToSap) {
					po.setStatus(PoStatus.ORDERED);
				}
				poService.updatePo(po);
			}
		}

	}

	@Override
	public List<Po> findAllSearchFilterPo(String loggedInUserId, String loggedInUserTenantId, TableDataInput input,
										  Date startDate, Date endDate,String viewType,String status,List<String> businessUntiIds) {
		return poDao.findAllSearchFilterPo(loggedInUserId, loggedInUserTenantId, input, startDate, endDate,viewType,status,businessUntiIds);
	}

	@Override
	public long findTotalSearchFilterPoCount(String loggedInuserId, String loggedInUserTenantId, TableDataInput input,
											 Date startDate, Date endDate,String viewType,String status,List<String> businessUntiIds) {
		return poDao.findTotalSearchFilterPoCount(loggedInuserId, loggedInUserTenantId, input, startDate, endDate,viewType,status,businessUntiIds);
	}

	@Override
	@Transactional(readOnly = false)
	public void downloadBuyerPoReports(String tenantId, String[] poArr, HttpServletResponse response,
									   HttpSession session, boolean select_all, Date startDate, Date endDate,
									   SearchFilterPoPojo searchFilterPoPojo, String loggedInUser) {
		LOG.info("Download PO reports... " + response.getHeaderNames());
		try {
			List<Po> poList = poDao.findSearchBuyerPoByIds(tenantId, poArr, select_all, startDate, endDate,
					searchFilterPoPojo, loggedInUser);
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "poReports.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("PO Report");

			// Style of Heading Cells
			CellStyle styleHeading = workbook.createCellStyle();
			Font font = workbook.createFont();

			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading.setFont(font);
			styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

			CellStyle totalStyle = workbook.createCellStyle();
			totalStyle.setAlignment(CellStyle.ALIGN_RIGHT);

			// Creating Headings
			Row rowHeading = sheet.createRow(0);
			int i = 0;
			for (String column : Global.BUYER_PO_REPORT_EXCEL_COLUMNS) {
				Cell cell = rowHeading.createCell(i++);

				cell.setCellValue(column);
				cell.setCellStyle(styleHeading);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));

			int r = 1;
			// Write Data into Excel
			for (Po po : poList) {
				// For Financial Standard
				DecimalFormat df = null;
				if (po.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (po.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (po.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (po.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (po.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (po.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(r - 1);
				row.createCell(cellNum++).setCellValue(po.getPoNumber() != null ? po.getPoNumber() : "");
				row.createCell(cellNum++).setCellValue(po.getName() != null ? po.getName() : "");
				row.createCell(cellNum++).setCellValue(po.getStatus() != null ? po.getStatus().toString() : "");
				row.createCell(cellNum++)
						.setCellValue((po.getSupplier() != null && po.getSupplier().getFullName() != null)
								? po.getSupplier().getSupplier().getCompanyName()
								: (po.getSupplierName() != null ? po.getSupplierName() : ""));
				row.createCell(cellNum++)
						.setCellValue(po.getBusinessUnit() != null ? po.getBusinessUnit().getUnitName() : "");
				row.createCell(cellNum++).setCellValue(po.getCreatedBy() != null ? po.getCreatedBy().getName() : "");
				row.createCell(cellNum++)
						.setCellValue(po.getCreatedDate() != null ? sdf.format(po.getCreatedDate()) : "");
				row.createCell(cellNum++).setCellValue(po.getOrderedBy() != null ? po.getOrderedBy().getName() : "");
				row.createCell(cellNum++)
						.setCellValue(po.getOrderedDate() != null ? sdf.format(po.getOrderedDate()) : "");
				row.createCell(cellNum++)
						.setCellValue(po.getPoRevisedDate() != null ? sdf.format(po.getPoRevisedDate()) : "");
				row.createCell(cellNum++)
						.setCellValue(po.getActionDate() != null ? sdf.format(po.getActionDate()) : "");
				row.createCell(cellNum++)
						.setCellValue(po.getCurrency() != null ? po.getCurrency().getCurrencyCode() : "");
				Cell grandTotalCell = row.createCell(cellNum++);
				grandTotalCell.setCellValue(po.getGrandTotal() != null ? df.format(po.getGrandTotal()) : "");
				grandTotalCell.setCellStyle(totalStyle);
				row.createCell(cellNum++).setCellValue(po.getPr() != null ? po.getPr().getPrId() : "");

			}
			// Auto Fit
			for (int k = 0; k < 15; k++) {
				sheet.autoSizeColumn(k, true);
			}

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");

			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					LOG.info("Successfully written in Excel===========================");
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while downloading PO Reports Excel : " + e.getMessage(), e);
		}

	}

	@Override
	public List<Po> findAllPoforSharingAll(String supplierId, String buyerId) {
		return poDao.findAllPoforSharingAll(supplierId, buyerId);
	}

	@Override
	public String getBusinessUnitname(String poId) {
		String displayName = null;
		displayName = poDao.getBusineessUnitname(poId);
		return StringUtils.checkString(displayName);
	}

	@Override
	public JasperPrint getBuyerPoPdf(Po poData, JRSwapFileVirtualizer virtualizer) {
		Po po = getLoadedPoById(poData.getId());
		JasperPrint jasperPrint = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String buyerTimeZone = buyerSettingsService.getBuyerTimeZoneByTenantId(po.getBuyer().getId());

		TimeZone timeZone = TimeZone.getDefault();

		if (StringUtils.checkString(buyerTimeZone).length() > 0) {
			timeZone = TimeZone.getTimeZone(buyerTimeZone);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
		sdf.setTimeZone(timeZone);

		DecimalFormat df = null;
		if (po.getDecimal().equals("1")) {
			df = new DecimalFormat("#,###,###,##0.0");
		} else if (po.getDecimal().equals("2")) {
			df = new DecimalFormat("#,###,###,##0.00");
		} else if (po.getDecimal().equals("3")) {
			df = new DecimalFormat("#,###,###,##0.000");
		} else if (po.getDecimal().equals("4")) {
			df = new DecimalFormat("#,###,###,##0.0000");
		} else if (po.getDecimal().equals("5")) {
			df = new DecimalFormat("#,###,###,##0.00000");
		} else if (po.getDecimal().equals("6")) {
			df = new DecimalFormat("#,###,###,##0.000000");
		} else {
			df = new DecimalFormat("#,###,###,##0.00");
		}

		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
		try {

			Resource resource = applicationContext.getResource("classpath:reports/PrSummary.jasper");
			File jasperfile = resource.getFile();

			PrSummaryPojo summary = new PrSummaryPojo();
			String createDate = po.getCreatedDate() != null ? sdf.format(po.getCreatedDate()).toUpperCase() : "";
			String deliveryDate = po.getDeliveryDate() != null ? sdf.format(po.getDeliveryDate()).toUpperCase() : "";
			String poRevisionDate = po.getPoRevisedDate() != null ? sdf.format(po.getPoRevisedDate()).toUpperCase()
					: "-";
			summary.setPrName(po.getName());
			summary.setRemarks(po.getRemarks());
			summary.setPaymentTerm(po.getPaymentTerm());
			summary.setPaymentTermDays(po.getPaymentTermDays());
			summary.setTermsAndConditions(
					po.getTermsAndConditions() != null ? po.getTermsAndConditions().replaceAll("(?m)^[ \t]*\r?\n", "")
							: "");
			summary.setRequester(po.getRequester());
			summary.setPoNumber(po.getPoNumber());
			summary.setCreatedDate(createDate);
			summary.setPoRevisionDate(poRevisionDate);

			summary.setReferenceNumber(po.getReferenceNumber());
			BusinessUnit bUnit = po.getBusinessUnit();
			Buyer buyer = po.getBuyer();
			// Buyer Address
			String buyerAddress = "";

			if (bUnit != null) {
				ImageIcon n;
				if (bUnit.getFileAttatchment() != null) {
					n = new ImageIcon(bUnit.getFileAttatchment());
					summary.setComanyName(null);
				} else {
					n = new ImageIcon();
					summary.setComanyName(bUnit.getDisplayName());
				}
				summary.setLogo(n.getImage());

				getSummaryOfAddressAndPoitems(po, df, summary, deliveryDate);
				if (StringUtils.checkString(bUnit.getLine1()).length() > 0) {
					buyerAddress = bUnit.getLine1() + "\r\n";
				} else {
					buyerAddress = " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine2()).length() > 0) {
					buyerAddress += bUnit.getLine2() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine3()).length() > 0) {
					buyerAddress += bUnit.getLine3() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine4()).length() > 0) {
					buyerAddress += bUnit.getLine4() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine5()).length() > 0) {
					buyerAddress += bUnit.getLine5() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine6()).length() > 0) {
					buyerAddress += bUnit.getLine6() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}
				if (StringUtils.checkString(bUnit.getLine7()).length() > 0) {
					buyerAddress += bUnit.getLine7() + "\r\n";
				} else {
					buyerAddress += " \r\n";
				}

				summary.setDisplayName(bUnit.getDisplayName());
			} else {
				buyerAddress += "\r\n" + buyer.getCity();
				if (buyer.getState() != null) {
					buyerAddress += ", " + buyer.getState().getStateName();
					buyerAddress += "\r\n" + buyer.getState().getCountry().getCountryName();
				}
				buyerAddress += "\r\n";
				buyerAddress += "TEL : " + buyer.getCompanyContactNumber();
				buyerAddress += " FAX : ";
				if (buyer.getFaxNumber() != null) {
					buyerAddress += buyer.getFaxNumber();
				}
				summary.setComanyName(buyer.getCompanyName());
				summary.setDisplayName(buyer.getCompanyName());
			}

			summary.setBuyerAddress(buyerAddress);

			List<PrSummaryPojo> prSummary = Arrays.asList(summary);

			// Supplier Address
			String supplierAddress = "";

			if (po.getSupplier() != null) {
				FavouriteSupplier supplier = po.getSupplier();
				supplierAddress += supplier.getSupplier().getCompanyName() + "\r\n";
				supplierAddress += supplier.getSupplier().getLine1();
				if (StringUtils.checkString(po.getSupplier().getSupplier().getLine2()).length() > 0) {
					supplierAddress += "\r\n" + supplier.getSupplier().getLine2();
				}
				supplierAddress += "\r\n" + supplier.getSupplier().getCity() + ", ";
				if (supplier.getSupplier().getState() != null) {
					supplierAddress += supplier.getSupplier().getState().getStateName() + "\r\n\n";
				}
				supplierAddress += "TEL : ";

				if (supplier.getSupplier().getCompanyContactNumber() != null) {
					supplierAddress += supplier.getSupplier().getCompanyContactNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (supplier.getSupplier().getFaxNumber() != null) {
					supplierAddress += supplier.getSupplier().getFaxNumber() + "\n\n";
				}
				supplierAddress += "Attention: " + supplier.getFullName() + "\nEmail: "
						+ supplier.getCommunicationEmail() + "\n";
			} else {
				supplierAddress += po.getSupplierName() + "\r\n";
				supplierAddress += po.getSupplierAddress() + "\r\n\n";
				supplierAddress += "TEL :";
				if (po.getSupplierTelNumber() != null) {
					supplierAddress += po.getSupplierTelNumber();
				}
				supplierAddress += "\r\nFAX : ";
				if (po.getSupplierFaxNumber() != null) {
					supplierAddress += po.getSupplierFaxNumber();
				}
			}
			if (po.getSupplier() != null) {
				summary.setSupplierName(
						po.getSupplier().getSupplier() != null ? po.getSupplier().getSupplier().getCompanyName() : "");
			} else {
				summary.setSupplierName(po.getSupplierName());
			}
			summary.setSupplierAddress(supplierAddress);
			summary.setTaxnumber(po.getSupplierTaxNumber() != null ? po.getSupplierTaxNumber() : "");

			parameters.put("PR_SUMMARY", prSummary);
			JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(prSummary, false);
			jasperPrint = JasperFillManager.fillReport(jasperfile.getPath(), parameters, beanCollectionDataSource);
		} catch (

				Exception e) {
			LOG.error("Could not generate PO  Report. " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	private void getSummaryOfAddressAndPoitems(Po po, DecimalFormat df, PrSummaryPojo summary, String deliveryDate) {

		try {

			// Delivery Address

			String deliveryAddress = "";
			if (po.getDeliveryAddress() != null) {
				deliveryAddress += po.getDeliveryAddress().getTitle() + "\n";
				deliveryAddress += po.getDeliveryAddress().getLine1();
				if (po.getDeliveryAddress().getLine2() != null) {
					deliveryAddress += "\n" + po.getDeliveryAddress().getLine2();
				}
				deliveryAddress += "\n" + po.getDeliveryAddress().getZip() + ", " + po.getDeliveryAddress().getCity()
						+ "\n";
				deliveryAddress += po.getDeliveryAddress().getState().getStateName() + ", "
						+ po.getDeliveryAddress().getState().getCountry().getCountryName();
			}

			summary.setDeliveryAddress(deliveryAddress);
			summary.setDeliveryReceiver(po.getDeliveryReceiver());
			summary.setDeliveryDate(deliveryDate);

			// Correspondence Address
			if (po.getCorrespondenceAddress() != null) {

				String correspondAddress = "";
				correspondAddress += po.getCorrespondenceAddress().getTitle();
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getLine1();
				if (po.getCorrespondenceAddress().getLine2() != null) {
					correspondAddress += ", " + po.getCorrespondenceAddress().getLine2();
				}
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getZip() + ", "
						+ po.getCorrespondenceAddress().getCity();
				correspondAddress += "\r\n" + po.getCorrespondenceAddress().getState().getStateName() + ", "
						+ po.getCorrespondenceAddress().getState().getCountry().getCountryName();
				summary.setCorrespondAddress(correspondAddress);
			}
			// Po items List
			List<PrItemsSummaryPojo> prItemList = new ArrayList<PrItemsSummaryPojo>();
			List<PoItem> poList = findAllPoItemByPoId(po.getId());
			if (CollectionUtil.isNotEmpty(poList)) {
				for (PoItem item : poList) {
					PrItemsSummaryPojo pos = new PrItemsSummaryPojo();
					pos.setSlno(item.getLevel() + "." + item.getOrder());
					pos.setItemName(item.getItemName());
					pos.setCurrency(item.getPo().getCurrency().getCurrencyCode());
					pos.setItemDescription(item.getItemDescription());
					pos.setAdditionalTax(df.format(po.getAdditionalTax()));
					pos.setGrandTotal(df.format(item.getPo().getGrandTotal()));
					pos.setSumAmount(df.format(po.getTotal()));
					pos.setTaxDescription(po.getTaxDescription());
					pos.setDecimal(po.getDecimal());
					prItemList.add(pos);
					if (item.getChildren() != null) {
						for (PoItem childItem : item.getChildren()) {
							PrItemsSummaryPojo childPr = new PrItemsSummaryPojo();
							childPr.setSlno(childItem.getLevel() + "." + childItem.getOrder());
							childPr.setItemName(childItem.getItemName());
							childPr.setItemDescription(childItem.getItemDescription());
							childPr.setPricePerUnit(df.format(item.getPricePerUnit()));
							childPr.setQuantity(df.format(childItem.getQuantity()));
							childPr.setUnitPrice(df.format(childItem.getUnitPrice()));
							childPr.setTaxAmount(df.format(childItem.getTaxAmount()));
							childPr.setTotalAmount(df.format(childItem.getTotalAmount()));
							childPr.setTotalAmountWithTax(df.format(childItem.getTotalAmountWithTax()));
							childPr.setUom(
									childItem.getProduct() != null
											? (childItem.getProduct().getUom() != null
											? childItem.getProduct().getUom().getUom()
											: "")
											: (childItem.getUnit() != null ? childItem.getUnit().getUom() : ""));
							childPr.setCurrency(childItem.getPo().getCurrency().getCurrencyCode());
							childPr.setAdditionalTax(df.format(po.getAdditionalTax()));
							childPr.setGrandTotal(df.format(childItem.getPo().getGrandTotal()));
							childPr.setSumAmount(df.format(po.getTotal()));
							childPr.setTaxDescription(po.getTaxDescription());
							childPr.setSumTaxAmount(childItem.getTaxAmount());
							childPr.setSumTotalAmt(childItem.getTotalAmount());
							childPr.setDecimal(po.getDecimal());
							prItemList.add(childPr);
						}
					}

				}
			}
			summary.setPrItems(prItemList);
		} catch (Exception e) {
			LOG.error("Could not Get PO Items and Address " + e.getMessage(), e);
		}
	}

	@Override
	public Po findByPrId(String prId) {
		return poDao.findByPrId(prId);
	}

	@Override
	public Po findById(String poId) {
		return poDao.findPoById(poId);
	}

	@Override
	public Po findSupplierByFavSupplierId(String id) {
		return poDao.findSupplierByFavSupplierId(id);
	}

	@Override
	public Po findPoByPoNumber(String poNumber, String tenantId) {
		return poDao.findByPoNomber(poNumber, tenantId);
	}

	public Po findPoByReferenceNumber(String referenceNumber, String tenantId) {
		return poDao.findByPoReferenceNomber(referenceNumber, tenantId);
	}

	@Override
	public List<Po> findAllPoByTenantId(String loggedInUserTenantId) {
		return poDao.findAllPoByTenantId(loggedInUserTenantId);
	}

	@Override
	public void downloadCsvFileForPoSummary(HttpServletResponse response, File file, String[] poIds,
											SearchFilterPoPojo searchFilterPoPojo, Date startDate, Date endDate, boolean select_all, String tenantId,
											String loggedInUser, HttpSession session,String poType,String status) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.BUYER_PO_REPORT_EXCEL_COLUMNS;

			String[] columns = { "srNo", "poNumber", "name", "postatus", "supplier", "businessUnit", "createdby",
					"createdDateStr", "orderedby", "orderedDateStr", "poRevisedDateStr", "actionDateStr", "currency",
					"grandTotal", "prId" };

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			long count = poDao.findTotalPoSummaryCountForCsv(tenantId, startDate, endDate);

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file),
					new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2))
							.build());
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<PoPojo> list = findPoSummaryBuyerSupplierForCsv(tenantId, PAGE_SIZE, pageNo, poIds,
						searchFilterPoPojo, select_all, startDate, endDate, loggedInUser,poType,status);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (PoPojo pojo : list) {
					if (pojo.getCreatedDate() != null) {
						pojo.setCreatedDateStr(sdf.format(pojo.getCreatedDate()));
					}
					if (pojo.getActionDate() != null) {
						pojo.setActionDateStr(sdf.format(pojo.getActionDate()));
					}
					if (pojo.getOrderedDate() != null) {
						pojo.setOrderedDateStr(sdf.format(pojo.getOrderedDate()));
					}
					if (pojo.getPoRevisedDate() != null) {
						pojo.setPoRevisedDateStr(sdf.format(pojo.getPoRevisedDate()));
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

				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional()

		};
		return processors;
	}

	private List<PoPojo> findPoSummaryBuyerSupplierForCsv(String tenantId, int pageSize, int pageNo, String[] poIds,
														  SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate, String userId,String poType,String status) {
		List<String> bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());

		List<Po> poList = poDao.findPoForTenantIdForCsv(tenantId, pageSize, pageNo, poIds, searchFilterPoPojo,
				select_all, startDate, endDate, userId,poType,bizUnitIds,status);

		List<PoPojo> poPojoList = new ArrayList<>();
		int r = 1;
		for (Po po : poList) {

			PoPojo popojo = new PoPojo();
			popojo.setSrNo(r++);
			popojo.setId(po.getId());
			popojo.setPoNumber(po.getPoNumber());
			popojo.setCreatedBy(po.getCreatedBy().getName());
			popojo.setUnitName(po.getBusinessUnit().getUnitName());
			popojo.setName(po.getName());
			popojo.setGrandTotal(po.getGrandTotal());
			popojo.setPoStatus(po.getStatus().toString());
			popojo.setBusinessUnit(po.getBusinessUnit() != null ? po.getBusinessUnit().getUnitName() : "");
			popojo.setPoId(po.getPoId());
			popojo.setSupplierName(po.getSupplierName());
			popojo.setSupplier(po.getSupplier());
			popojo.setOrderedBy(po.getOrderedBy() != null ? po.getOrderedBy().getName() : "");
			popojo.setOrderedDate(po.getOrderedDate());
			popojo.setCurrency(po.getCurrency().getCurrencyCode());
			popojo.setActionDate(po.getActionDate());
			popojo.setCreatedDate(po.getCreatedDate());
			popojo.setPrId(po.getPr() != null ? po.getPr().getPrId(): "");
			popojo.setPoRevisedDate(po.getPoRevisedDate());
			poPojoList.add(popojo);
		}

		return poPojoList;
	}

	@Override
	public void downloadCsvFileForPoSupplierSummary(HttpServletResponse response, File file, String[] poIds,
													SearchFilterPoPojo searchFilterPoPojo, Date startDate, Date endDate, boolean select_all, String tenantId,
													String loggedInUser, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {

			String[] columnHeadings = Global.SUPPLIER_PO_REPORT_EXCEL_COLUMNS;

			String[] columns = { "srNo", "poNumber", "name", "buyerCompanyName", "businessUnit", "createdDateStr",
					"acceptRejectdateStr", "currency", "grandTotal", "status" };

			long count = poDao.findTotalPoSupplierSummaryCountForCsv(tenantId, startDate, endDate);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			int PAGE_SIZE = 5000;
			int index = 1;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file),
					new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2))
							.build());
			CellProcessor[] processors = getProcessor();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<PoSupplierPojo> list = poDao.findPoSummaryForSupplierCsvReport(tenantId, PAGE_SIZE, pageNo, poIds,
						searchFilterPoPojo, select_all, startDate, endDate);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (PoSupplierPojo pojo : list) {
					pojo.setSrNo(index++);
					LOG.info("#################" + pojo.getBuyer() + " " + pojo.getActionDate());
					if (pojo.getCreatedDate() != null) {
						pojo.setCreatedDateStr(sdf.format(pojo.getCreatedDate()));
					}
					if (pojo.getAcceptRejectDate() != null) {
						pojo.setAcceptRejectdateStr(sdf.format(pojo.getAcceptRejectDate()));
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

	private static CellProcessor[] getProcessor() {
		final CellProcessor[] processor = new CellProcessor[] { new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), };

		return processor;
	}

	@Override
	@Transactional(readOnly = false)
	public void generatePoReport(HttpServletResponse response, Po po) {
		try {
			PoReport reportObj = poReportDao.findReportByPoId(po.getId());
			if (reportObj != null) {
				LOG.info("PDF is Present in Database");
				response.setContentType("application/pdf");
				response.setContentLength(reportObj.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + reportObj.getFileName() + "\"");
				FileCopyUtils.copy(reportObj.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				String poFilename = "UnknownPO.pdf";
				if (po.getPoNumber() != null) {
					poFilename = (po.getPoNumber()).replace("/", "-") + ".pdf";
				}
				String filename = poFilename;
				JasperPrint jasperPrint = savePoPdf(po);
				if (jasperPrint != null) {
					response.setContentType("application/pdf");
					response.addHeader("Content-Disposition", "attachment; filename=" + filename);
					JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
			}
		} catch (Exception e) {
			LOG.error("Error while generating PO Summary Report : " + e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public JasperPrint savePoPdf(Po po) {
		JasperPrint jasperPrint = null;
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024),
				false);

		try {
			jasperPrint = getBuyerPoPdf(po, virtualizer);
			String poFilename = "UnknownPO.pdf";
			if (po.getPoNumber() != null) {
				poFilename = (po.getPoNumber()).replace("/", "-") + ".pdf";
			}
			String filename = poFilename;
			if (jasperPrint != null && (PoStatus.READY == po.getStatus() || PoStatus.ORDERED == po.getStatus()
					|| PoStatus.ACCEPTED == po.getStatus())) {
				byte[] outputFile = JasperExportManager.exportReportToPdf(jasperPrint);
				PoReport attach = new PoReport();
				attach.setFileData(outputFile);
				attach.setFileName(filename);
				attach.setPoNumber(po.getPoNumber());
				attach.setPo(po);
				poReportDao.saveOrUpdate(attach);
			}
		} catch (Exception e) {
			LOG.error("Error while generating PO Summary Report : " + e.getMessage(), e);
		}
		return jasperPrint;
	}

	@Override
	@Transactional
	public PoItem getPoItembyPoIdAndPoItemId(String poId, String poItemId) {
		return poItemDao.getPoItembyPoIdAndPoItemId(poId, poItemId);
	}

	@Override
	public String replaceSmartQuotes(String input) {
		return input.replace("‘", "'").replace("’", "'").replace("“", "\"").replace("”", "\"");
	}

	//PH - 4113
	@Override
	@Transactional
	public boolean isExists(PoItem poItem,boolean isSection) {
		return poItemDao.isExists(poItem,isSection);
	}

	//PH - 4113
	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public PoItem savePoItem(PoItem poItem, User loggedInUser) throws Exception {
		PoItem item = new PoItem();
		Po po = poDao.findById(poItem.getPo().getId());
		if (poItem.getProduct() != null)
			item.setProduct(productListMaintenanceService.getProductCategoryById(poItem.getProduct().getId()));
		item.setBuyer(loggedInUser.getBuyer());
		// Assuming po.item contains smart quotes
		item.setItemDescription(poService.replaceSmartQuotes(poItem.getItemDescription()));
		item.setItemName(poService.replaceSmartQuotes(poItem.getItemName()));
		item.setItemTax(poItem.getItemTax());
		if (poItem.getUnitPrice() != null) {
			item.setUnitPrice(poItem.getUnitPrice().setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
		}
		if (poItem.getQuantity() != null) {
			item.setQuantity(poItem.getQuantity().setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
		}

		try {
			item.setTotalAmount((poItem.getUnitPrice() != null && poItem.getQuantity() != null) ? poItem.getUnitPrice().multiply(poItem.getQuantity()).setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_HALF_UP) : new BigDecimal(0));
			BigDecimal taxAmount = BigDecimal.ZERO.setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_HALF_UP);
			if (poItem.getItemTax() != null) {
				taxAmount = new BigDecimal(poItem.getItemTax()).setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_HALF_UP);
				taxAmount = item.getTotalAmount().multiply(taxAmount).divide(new BigDecimal(100), Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_HALF_UP);
				taxAmount = taxAmount.setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_HALF_UP);
			}
			item.setTaxAmount(taxAmount);
			item.setTotalAmountWithTax(item.getTotalAmount() != null ? item.getTotalAmount().add(item.getTaxAmount()) : new BigDecimal(0));
		} catch (Exception e) {
			LOG.error("Error because of : " + e.getMessage(), e);
			throw new NotAllowedException(messageSource.getMessage("common.number.format.error", new Object[] {}, Global.LOCALE));
		}
		item.setPo(poItem.getPo());
		item.setParent(poItem.getParent());
		if (poItem.getProductContractItem() != null) {
			item.setProductContractItem(poItem.getProductContractItem());
		}
		item.setUnit(poItem.getUnit());
		item.setProductCategory(poItem.getProductCategory());
		item.setFreeTextItemEntered(poItem.getFreeTextItemEntered());

		item.setField1(poItem.getField1());
		item.setField2(poItem.getField2());
		item.setField3(poItem.getField3());
		item.setField4(poItem.getField4());
		item.setField5(poItem.getField5());
		item.setField6(poItem.getField6());
		item.setField7(poItem.getField7());
		item.setField8(poItem.getField8());
		item.setField9(poItem.getField9());
		item.setField10(poItem.getField10());
		item.setBuyer(loggedInUser.getBuyer());
		if (item.getParent() == null) {
			int itemLevel = 0;
			List<PoItem> list = poItemDao.getPoItemLevelOrder(item.getPo().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			item.setLevel(itemLevel + 1);
			item.setOrder(0);
		} else {
			PoItem parent = poItemDao.findById(item.getParent().getId());
			item.setLevel(parent.getLevel());
			LOG.info("parent.getChildren() " + parent.getChildren());
			item.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);

			item.setBalanceQuantity(BigDecimal.ZERO);
			item.setLockedQuantity(BigDecimal.ZERO);
		}

		// Updating total and grand total after add new item

		LOG.info("===================" + item.getTotalAmountWithTax());
		LOG.info("===================" + po.getTotal());
		LOG.info("===================" + po.getAdditionalTax());
		if (po.getTotal() == null) {
			po.setTotal(BigDecimal.ZERO);
		}
		if (po.getAdditionalTax() == null) {
			po.setAdditionalTax(BigDecimal.ZERO);
		}
		if (po.getGrandTotal() == null) {
			po.setGrandTotal(BigDecimal.ZERO);
		}

		LOG.info("Currency Id : " + po.getCurrency().getCurrencyCode());
		//po.setCurrency(po.getCurrency());

		po.setTotal(po.getTotal() != null && item.getTotalAmountWithTax() != null ? po.getTotal().add(item.getTotalAmountWithTax()) : new BigDecimal(0));
		po.setGrandTotal(po.getTotal() != null && po.getAdditionalTax() != null ? po.getTotal().add(po.getAdditionalTax()) : new BigDecimal(0));
		LOG.info("===================" + po.getGrandTotal());
		LOG.info("===================" + po.getTotal());
		if (CollectionUtil.isNotEmpty(validatePo(po, Po.PoPurchaseItem.class)) || CollectionUtil.isNotEmpty(validatePo(po, PrItem.PurchaseItem.class))) {
			String message = ", ";
			if (CollectionUtil.isNotEmpty(validatePo(po, Po.PoPurchaseItem.class))) {
				for (String errMessage : validatePo(po, Po.PoPurchaseItem.class)) {
					message = errMessage + message;
				}
			}
			if (CollectionUtil.isNotEmpty(validatePo(po, Po.PoPurchaseItem.class))) {
				for (String errMessage : validatePo(po, Po.PoPurchaseItem.class)) {
					message = errMessage + message;
				}
			}
			LOG.error("message :" + message);
			throw new NotAllowedException(message);
		}

		poDao.update(po);

		return poItemDao.saveOrUpdate(item);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void updatePoItem(PoItem poItem) throws Exception, NotAllowedException {
		Po po = poDao.findById(poItem.getPo().getId());
		PoItem persistPoItemObj = poItemDao.findById(poItem.getId());

		persistPoItemObj.setItemName(poItem.getItemName());
		// strip additional decimals from quantity and price
		persistPoItemObj.setUnitPrice(poItem.getUnitPrice() != null
				? poItem.getUnitPrice().setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN)
				: new BigDecimal(0).setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
		persistPoItemObj.setQuantity(poItem.getQuantity() != null
				? poItem.getQuantity().setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN)
				: new BigDecimal(0).setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
		persistPoItemObj.setItemDescription(poItem.getItemDescription());
		persistPoItemObj.setItemTax(poItem.getItemTax());
		persistPoItemObj.setUnit(poItem.getUnit());

		try {
			if (persistPoItemObj.getParent() != null) {
				// subtract it initially to add the updated value later.
				po.setTotal(po.getTotal() != null ? po.getTotal().subtract(persistPoItemObj.getTotalAmountWithTax())
						: new BigDecimal(0).setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));

				persistPoItemObj.setLockedQuantity(BigDecimal.ZERO);
				persistPoItemObj.setBalanceQuantity(BigDecimal.ZERO);
				persistPoItemObj.setTotalAmount(
						persistPoItemObj.getUnitPrice() != null && persistPoItemObj.getQuantity() != null
								? persistPoItemObj.getUnitPrice().multiply(persistPoItemObj.getQuantity())
								.setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_HALF_UP)
								: new BigDecimal(0).setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
				BigDecimal taxAmount = BigDecimal.ZERO.setScale(Integer.parseInt(po.getDecimal()),
						BigDecimal.ROUND_HALF_UP);
				if (poItem.getItemTax() != null) {
					taxAmount = new BigDecimal(poItem.getItemTax()).setScale(Integer.parseInt(po.getDecimal()),
							BigDecimal.ROUND_HALF_UP);
					taxAmount = persistPoItemObj.getTotalAmount().multiply(taxAmount).divide(new BigDecimal(100),
							Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_HALF_UP);
					// Roundup the decimals after calculation
					taxAmount = taxAmount.setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_HALF_UP);
				}
				persistPoItemObj.setTaxAmount(taxAmount);
				persistPoItemObj.setTotalAmountWithTax(persistPoItemObj.getTotalAmount() != null
						? persistPoItemObj.getTotalAmount().add(persistPoItemObj.getTaxAmount())
						: new BigDecimal(0).setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
			}
		} catch (Exception e) {
			throw new NotAllowedException(
					messageSource.getMessage("common.number.format.error", new Object[] {}, Global.LOCALE));
		}

		// Updating total and grand total after update item
		if (po.getTotal() == null) {
			po.setTotal(BigDecimal.ZERO.setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
		}
		if (persistPoItemObj.getTotalAmountWithTax() == null) {
			persistPoItemObj.setTotalAmountWithTax(
					BigDecimal.ZERO.setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
		}
		if (po.getAdditionalTax() == null) {
			po.setAdditionalTax(BigDecimal.ZERO.setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
		} else {
			po.setAdditionalTax(
					po.getAdditionalTax().setScale(Integer.parseInt(po.getDecimal()), BigDecimal.ROUND_DOWN));
		}

		// Add back the updated value as we had deducted the old value
		po.setTotal(po.getTotal().add(persistPoItemObj.getTotalAmountWithTax()));
		po.setGrandTotal(po.getTotal().add(po.getAdditionalTax()));
		if (CollectionUtil.isNotEmpty(validatePo(po, Po.PoPurchaseItem.class))
				|| CollectionUtil.isNotEmpty(validatePo(po, PoItem.PurchaseItem.class))) {
			String message = ", ";
			if (CollectionUtil.isNotEmpty(validatePo(po, Po.PoPurchaseItem.class))) {
				for (String errMessage : validatePo(po, Po.PoPurchaseItem.class)) {
					message = errMessage + message;
				}
			}
			if (CollectionUtil.isNotEmpty(validatePo(po, PoItem.PurchaseItem.class))) {
				for (String errMessage : validatePo(po, PoItem.PurchaseItem.class)) {
					message = errMessage + message;
				}
			}
			LOG.error("message :" + message);
			throw new NotAllowedException(message);
		}
		poDao.update(po);
		poItemDao.update(persistPoItemObj);
	}


	@Override
	public List<PoItem> getPoItemListByPoId(String poId) {
		return poItemDao.getPoItemListByPoId(poId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = ApplicationException.class)
	public void deletePoItemByItemIdAndPoId(String poItemId, String poId) throws ApplicationException {
		LOG.info("**************** Service");
		poItemDao.deletePoItems(poItemId, poId);
	}

	@Override
	public EventPermissions getUserPemissionsForPo(String userId, String poId) {
		return poDao.getUserPemissionsForPo(userId, poId);
	}

	/**
	 * @param mailTo
	 * @param po
	 * @param actionBy
	 */
	public void sendRevisePoEmailNotification(User mailTo, Po po, User actionBy) {
		LOG.info("Sending Revision PO email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
		String subject = "Revise PO Finished";

		String prKey = "";
		String poId = po.getId();

		if(po.getPr() != null){
			prKey = po.getPr().getId();
		}

		String url = APP_URL + "/buyer/poView/" + poId+"?prId="+prKey;
		if (TenantType.SUPPLIER == mailTo.getTenantType()) {
			url = APP_URL + "/supplier/supplierPrView/" + poId;
		}

		List<PoTeamMember> teamMember = poDao.findAssociateOwnerOfPo(poId, TeamMemberType.Associate_Owner);
		String poCreator = po.getCreatedBy().getId();
		String userLoggedIn = SecurityLibrary.getLoggedInUser().getId();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName()); //po creator
		map.put("actionByName", actionBy.getName());
		map.put("message", "has finished the following PO revision");
		LOG.info("Message send to PO creator and Owner :"+actionBy.getName()+" has finished the following PO revision");

		map.put("po", po);
		String actionByName = actionBy.getName() + " " + actionBy.getLoginId();
		map.put("actionBy", actionByName);
		map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
		map.put("justification", StringUtils.checkString(po.getReviseJustification()));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("poDate", df.format(po.getCreatedDate()));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);

		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			//PH-4113
			//send to PO owner from PR Team member which as PO Associate Owner
			if(!poCreator.equals(userLoggedIn)){
				if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && po.getCreatedBy().getEmailNotifications()) {
					LOG.info("sending email to PO Creator with login id :" + mailTo);
					sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.REVISE_PO_TEMPLATE);
					//send to Owner not loggedIn user
					if (CollectionUtil.isNotEmpty(teamMember)) {
						for (PoTeamMember assOwn : teamMember) {
							if (!assOwn.getUser().getId().equals(userLoggedIn)){ //do not send email to ownself as owner
								if (StringUtils.checkString(assOwn.getUser().getCommunicationEmail()).length() > 0 && assOwn.getUser().getEmailNotifications()) {
									LOG.info("sending email to Other PO associate owner login id :" + assOwn.getUser().getLoginId());
									map.put("eventOwner", assOwn.getUser().getName());
									Log.info("Sending email to PO Owner >>>>>>>>>>>> "+assOwn.getUser().getCommunicationEmail());
									sendEmail(assOwn.getUser().getCommunicationEmail(), subject, map, Global.REVISE_PO_TEMPLATE);
								}
							}
						}
					}
				} else {
					LOG.warn("No communication email configured for user : " + po.getCreatedBy().getLoginId() + "... Not going to send email notification");
				}
			}else{
				LOG.info("Sending revise email only to PO Team Owner");
				if (CollectionUtil.isNotEmpty(teamMember)) {
					for (PoTeamMember assOwn : teamMember) {
						if (StringUtils.checkString(assOwn.getUser().getCommunicationEmail()).length() > 0 && assOwn.getUser().getEmailNotifications()) {
							LOG.info("sending email to PR associate owner login id :" + assOwn.getUser().getLoginId());
							map.put("eventOwner", assOwn.getUser().getName());
							Log.info("Sent email to PO Owner Only >>>>>>>>>>>> ");
							sendEmail(assOwn.getUser().getCommunicationEmail(), subject, map, Global.REVISE_PO_TEMPLATE);
						}
					}
				}
			}
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
					+ "... Not going to send email notification");
		}
	}

	/**
	 * @param mailTo
	 * @param po
	 * @param actionBy
	 */
	//PH-4113
	public void sendSuspendPoEmailNotification(User mailTo,Po po, User actionBy) {
		String poId = po.getId();
		String prKey = (po.getPr() != null) ? po.getPr().getId() : "";
		String subject = "PO Suspended";
		List<PoTeamMember> teamMembers = poDao.findAssociateOwnerOfPo(poId, TeamMemberType.Associate_Owner);
		String poCreatorId = po.getCreatedBy().getId();
		String userLoggedInId = SecurityLibrary.getLoggedInUser().getId();
		HashMap<String, Object> emailData = createEmailData(po, prKey, mailTo, userLoggedInId);
		//included po creator & associate owner & supplier
		sendEmailsWhenSuspend(po, teamMembers, emailData, subject);
	}
	private HashMap<String, Object> createEmailData(Po po, String prKey, User mailTo, String userLoggedInId) {
		HashMap<String, Object> map = new HashMap<>();
		String url = APP_URL + "/buyer/poView/" + po.getId() + "prId=" + prKey;
		String message = SecurityLibrary.getLoggedInUser().getName() + " has suspended following PO";

		if (TenantType.SUPPLIER == mailTo.getTenantType()) {
			url = APP_URL + "/supplier/supplierPrView/" + po.getId();
			message = "Your PO is suspended";
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), "GMT+8:00");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));

		map.put("appUrl", url);
		map.put("message", message);
		map.put("po", po);
		map.put("userName", SecurityLibrary.getLoggedInUser().getName());
		map.put("actionByName", SecurityLibrary.getLoggedInUser().getName());
		map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
		map.put("justification", StringUtils.checkString(po.getReviseJustification()));
		map.put("poDate", df.format(po.getCreatedDate()));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");

		return map;
	}
	private void sendEmailsWhenSuspend(Po po, List<PoTeamMember> teamMembers, HashMap<String, Object> emailData, String subject) {
		String poCreatorEmail = po.getCreatedBy().getCommunicationEmail();
		String poCreatorId = po.getCreatedBy().getId();
		String userLoggedInId = SecurityLibrary.getLoggedInUser().getId();

		LOG.info("Po Creator >>>>>>>>>>> "+poCreatorId);
		LOG.info("User LoggedIn >>>>>>>>>>> "+userLoggedInId);

		if(!poCreatorId.equals(userLoggedInId)){
			LOG.info("Sending email to PO Creator: " + poCreatorEmail);
			emailData.put("userName", po.getCreatedBy().getName());
			sendEmail(poCreatorEmail, subject, emailData, Global.SUSPEND_PO_TEMPLATE);
		}

		for (PoTeamMember teamMember : teamMembers) {
			if (!teamMember.getUser().getId().equals(userLoggedInId)) {
				LOG.info("Sending email to PO associate owner: " + teamMember.getUser().getCommunicationEmail());
				emailData.put("userName", teamMember.getUser().getName());
				sendEmail(teamMember.getUser().getCommunicationEmail(), subject, emailData, Global.SUSPEND_PO_TEMPLATE);
			}
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

	private String getBusinessUnitnameByPoId(String poId) {
		String displayName = null;
		displayName = poDao.getBusineessUnitnameByPoId(poId);
		return StringUtils.checkString(displayName);
	}

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		notificationService.sendEmail(mailTo, subject, map, template);
	}

	@Override
	public void sendRevisePoFinishedEmailNotification(User mailTo, Po po, User actionBy) {
		LOG.info("Sending Revised PO Finished email to (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
		String subject = "Revise PO Finished";

		String prKey = "";
		if(po.getPr()!=null){
			prKey = po.getPr().getId();
		}
		String url = APP_URL + "/buyer/poView/" + po.getId()+"?prId="+prKey;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", mailTo.getName());
		map.put("po", po);
		String actionByName = actionBy.getName() + " " + actionBy.getLoginId();
		map.put("actionBy", actionByName);
		map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("poDate", df.format(po.getCreatedDate()));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);

		if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
			sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.REVISE_PO_FINISHED_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + mailTo.getLoginId()
					+ "... Not going to send email notification");
		}

	}

	@Override
	public List<Po> findAllPendingPo(String userId, String tenantId, TableDataInput input) {
		return poDao.findAllPendingPo(userId, tenantId, input);
	}

	@Override
	public List<Po> findRevisePo(String userId, String tenantId, TableDataInput input) {
		Po param = new Po();
		param.setStatus(PoStatus.SUSPENDED);
		param.setRevised(true);
		return poDao.findPoByParams(userId, tenantId, input, param);
	}

	@Override
	public long countRevisePo(String userId, String tenantId, TableDataInput input) {
		Po param = new Po();
		param.setStatus(PoStatus.SUSPENDED);
		param.setRevised(true);
		return poDao.countPoByParams(userId, tenantId, input, param);
	}

	@Override
	public List<Po> findPendingCancelPo(String userId, String tenantId, TableDataInput input) {
		Po param = new Po();
		param.setStatus(PoStatus.PENDING);
		param.setCancelled(true);
		return poDao.findPoByParams(userId, tenantId, input, param);
	}

	@Override
	public long countPendingCancelPo(String userId, String tenantId, TableDataInput input) {
		Po param = new Po();
		param.setStatus(PoStatus.PENDING);
		param.setCancelled(true);
		return poDao.countPoByParams(userId, tenantId, input, param);
	}

	@Override
	public List<Po> findPendingPo(String userId, String tenantId, TableDataInput input) {
		Po param = new Po();
		param.setStatus(PoStatus.PENDING);
		return poDao.findPoByParams(userId, tenantId, input, param);
	}

	@Override
	public long countPendingPo(String userId, String tenantId, TableDataInput input) {
		Po param = new Po();
		param.setStatus(PoStatus.PENDING);
		return poDao.countPoByParams(userId, tenantId, input, param);
	}

	@Override
	public long findTotalPendingPo(String tenantId, String userId) {
		return poDao.findTotalPendingPo(tenantId, userId);
	}

	@Override
	public long findTotalFilteredPendingPo(String tenantId, String userId, TableDataInput input) {
		return poDao.findTotalFilteredPendingPo(tenantId, userId, input);
	}

	// Usecase 1.3 PH-4106
	@Override
	public long findTotalCancelPo(String tenantId, String userId) {
		return poDao.findTotalCancelPo(tenantId, userId);
	}

	@Override
	public long findTotalFilteredCancelPo(String tenantId, String userId, TableDataInput input) {
		return poDao.findTotalFilteredCancelPo(tenantId, userId,input);
	}

	@Override
	public List<Po> findAllCancelPo(String tenantId, String userId, TableDataInput input) {
		return poDao.findAllCancelPo(tenantId, userId, input);
	}
	// Usecase 1.3 PH-4106

	@Override
	@Transactional(readOnly = false)
	public Po updatePoDetails(Po po, JRSwapFileVirtualizer virtualizer, User loggedInUser) throws Exception {
		LOG.info("Po update:      " + po.getId());
		Po persistObj = loadPoById(po.getId());

		persistObj.setEnableApprovalRoute(po.getEnableApprovalRoute());
		persistObj.setEnableApprovalReminder(po.getEnableApprovalReminder());
		persistObj.setReminderAfterHour(po.getReminderAfterHour());
		persistObj.setReminderCount(po.getReminderCount());
		persistObj.setNotifyEventOwner(po.getNotifyEventOwner());
		if (Boolean.compare(po.getEnableApprovalRoute(), true) == 0 && Boolean.TRUE == po.getEnableApprovalRoute()) {
			persistObj.setApprovals(po.getApprovals());
		} else {
			persistObj.setApprovals(new ArrayList<PoApproval>());
		}
		persistObj.setDeliveryAddress(po.getDeliveryAddress());
		persistObj.setDeliveryReceiver(po.getDeliveryReceiver());
		persistObj.setDeliveryDate(po.getDeliveryDate());

		persistObj.setDeliveryAddressTitle(po.getDeliveryAddress().getTitle());
		persistObj.setDeliveryAddressLine1(po.getDeliveryAddress().getLine1());
		persistObj.setDeliveryAddressLine2(po.getDeliveryAddress().getLine2());
		persistObj.setDeliveryAddressCity(po.getDeliveryAddress().getCity());
		persistObj.setDeliveryAddressState(po.getDeliveryAddress().getState().getStateName());
		persistObj.setDeliveryAddressZip(po.getDeliveryAddress().getZip());
		persistObj.setDeliveryAddressCountry(po.getDeliveryAddress().getState().getCountry().getCountryName());
		persistObj.setAdditionalTax(po.getAdditionalTax());
		persistObj.setTaxDescription(po.getTaxDescription());

		persistObj = updatePo(persistObj);

		persistObj = approvalService.doApproval(po, loggedInUser);

		sendRevisePoFinishedEmailNotification(persistObj.getCreatedBy(), persistObj, loggedInUser);
		return persistObj;
	}

	@Override
	@Transactional(readOnly = false)
	public void deletePoReportByPoId(String poId, String tenantId) {
		PoReport reportObj = poReportDao.findReportByPoId(poId);
		if (reportObj != null) {
			poReportDao.deletePoReportByPoId(poId);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public PurchaseOrderDocument savePoDocument(PurchaseOrderDocument prDocument) {
		return purchaseOrderDocumentDao.saveOrUpdate(prDocument);
	}

	@Override
	public List<PurchaseOrderDocument> findAllPlainPoDocsbyPoId(String poId) {
		return purchaseOrderDocumentDao.findAllPlainPoDocsbyPoId(poId);
	}

	@Override
	public List<PurchaseOrderDocument> findAllPlainPoDocsbyPoIdForSupplier(String poId) {
		return purchaseOrderDocumentDao.findAllPlainPoDocsbyPoIdForSupplier(poId);
	}

	@Override
	public PurchaseOrderDocument findPoDocById(String docId) {
		return purchaseOrderDocumentDao.findById(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public void removePoDocument(String docId, String poId) {
		purchaseOrderDocumentDao.deleteDocument(docId, poId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updatePoDocumentDesc(String docId, String docDesc, String poId, boolean internal) {
		PurchaseOrderDocument prDocument = findPoDocById(docId);
		prDocument.setDescription(docDesc);
		prDocument.setInternal(internal);
		Po pr = getLoadedPoById(poId);
		prDocument.setPo(pr);
		savePoDocument(prDocument);
	}

	@Override
	public void downloadPrDocument(String docId, HttpServletResponse response) throws Exception {
		PurchaseOrderDocument docs = findPoDocById(docId);
		if (docs == null) {
			POSnapshotDocument pOSnapshotDocument = poSnapshotDocumentDao.findById(docId);
			if (pOSnapshotDocument != null) {
				response.setContentType(pOSnapshotDocument.getCredContentType());
				response.setContentLength(pOSnapshotDocument.getFileData().length);
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + pOSnapshotDocument.getFileName() + "\"");
				FileCopyUtils.copy(pOSnapshotDocument.getFileData(), response.getOutputStream());
			}
		} else {
			response.setContentType(docs.getCredContentType());
			response.setContentLength(docs.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		}
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadPoDocument(String docId, HttpServletResponse response) throws Exception {
		PurchaseOrderDocument docs = findPoDocById(docId);
		if (docs == null) {
			POSnapshotDocument pOSnapshotDocument = poSnapshotDocumentDao.findById(docId);
			if (pOSnapshotDocument != null) {
				response.setContentType(pOSnapshotDocument.getCredContentType());
				response.setContentLength(pOSnapshotDocument.getFileData().length);
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + pOSnapshotDocument.getFileName() + "\"");
				FileCopyUtils.copy(pOSnapshotDocument.getFileData(), response.getOutputStream());
			}
		} else {
			response.setContentType(docs.getCredContentType());
			response.setContentLength(docs.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		}
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	@Transactional(readOnly = false)
	public void updatePoRevisedSnapshot(String poId, String revisePoDetails) {
		poDao.updatePoRevisedSnapshot(poId, revisePoDetails);
	}

	@Override
	@Transactional(readOnly = false)
	public List<PoItem> deletePoItems(String[] poItemIds, String poId) {
		poItemDao.deletePoItems(poItemIds, poId);
		return findAllPoItemByPoId(poId);
	}

	@Override
	@Transactional(readOnly = true)
	public Long findItemCountByPrItemIds(String[] prItemsIds, String poId) {
		return poItemDao.findItemCountByPrItemIds(prItemsIds, poId);
	}

	@Override
	public List<POSnapshotDocument> findAllSnapshotPlainPoDocsbyPoIdForSupplier(String poId) {
		return poSnapshotDocumentDao.findAllPlainPoDocsbyPoIdForSupplier(poId);
	}

	@Override
	public POSnapshotDocument findPoSnapShotDocById(String docId) {
		return poSnapshotDocumentDao.findById(docId);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveOrUpdatePoSnapShotDocument(POSnapshotDocument pOSnapshotDocument) {
		poSnapshotDocumentDao.saveOrUpdate(pOSnapshotDocument);
	}

	@Override
	public void downloadCsvFileForPoItemSummary(HttpServletResponse response, File file, String[] poIds,
												SearchFilterPoPojo searchFilterPoPojo, Date startDate, Date endDate, boolean select_all, String tenantId,
												String loggedInUser, HttpSession session,String poType,String status) {

		ICsvBeanWriter beanWriter = null;
		Log.info("Logged In User " + loggedInUser);
		try {

			String[] columnHeadings = Global.BUYER_PO_ITEMS_REPORT_EXCEL_COLUMNS;

			String[] columns = { "srNo", "poNumber", "name", "status", "vendorCode", "companyName", "unitName", "createdby",
					"createdDateStr", "orderedby", "orderedDateStr", "revisedDateStr", "actionDateStr" , "productCode", "productCategory",
					"itemName", "itemDescription", "uom", "quantity","lockedQuantity","balanceQuantity", "currency", "unitPrice","pricePerUnit", "totalAmount", "itemTax",
					"taxAmount", "totalAmountWithTax", "prId" };

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}

			long count = poDao.findTotalPoSummaryCountForCsv(tenantId, startDate, endDate);

			int pageSize = 5000;
			int index = 1;
			int totalPages = (int) Math.ceil(count / (double) pageSize);

			beanWriter = new CsvBeanWriter(new FileWriter(file),
					new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2))
							.build());
			CellProcessor[] processors = getProcessores();
			beanWriter.writeHeader(columnHeadings);
			List<String> businessUnitIds = businessUnitService.getBusinessUnitIdByUserId(SecurityLibrary.getLoggedInUser().getId());
			for (int pageNo = 0; pageNo < totalPages; pageNo++) {

				List<PoItemPojo> list = poItemDao.findPoItemForTenantIdForCsv(tenantId, pageSize, pageNo, poIds,
						searchFilterPoPojo, select_all, startDate, endDate, loggedInUser,poType,businessUnitIds,status);


				LOG.info("size ........" + list.size() + ".... count " + count);
				for (PoItemPojo pojo : list) {
					pojo.setSrNo(index++);
					if (pojo.getCreatedDate() != null) {
						pojo.setCreatedDateStr(sdf.format(pojo.getCreatedDate()));
					}
					if (pojo.getOrderedDate() != null) {
						pojo.setOrderedDateStr(sdf.format(pojo.getOrderedDate()));
					}
					if (pojo.getPoRevisedDate() != null) {
						pojo.setRevisedDateStr(sdf.format(pojo.getPoRevisedDate()));
					}

					if (pojo.getActionDate() != null) {
						pojo.setActionDateStr(sdf.format(pojo.getActionDate()));
					}



					if (pojo.getPricePerUnit() == null) {
						pojo.setPricePerUnit(BigDecimal.ONE);
					}
					if (pojo.getUnitPrice()==null) {
						pojo.setLockedQuantity(null);
						pojo.setBalanceQuantity(null);
						pojo.setPricePerUnit(null);
					}else{
						if(pojo.getBalanceQuantity()==null)
							pojo.setBalanceQuantity(BigDecimal.ZERO);
						if(pojo.getLockedQuantity()==null)
							pojo.setLockedQuantity(BigDecimal.ZERO);
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

	private static CellProcessor[] getProcessores() {
		final CellProcessor[] processors = new CellProcessor[] {

				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(),new Optional(), new Optional(),new Optional(),

		};
		return processors;
	}

	@Override
	public void downloadCsvFileForPoItemSupplierSummary(HttpServletResponse response, File file, String[] poIds,
														SearchFilterPoPojo searchFilterPoPojo, Date startDate, Date endDate, boolean select_all, String tenantId,
														String userId, HttpSession session) {
		try {

			String[] columnHeadings = Global.SUPPLIER_PO_ITEM_REPORT_EXCEL_COLUMNS;

			String[] columns = { "srNo", "poNumber", "name", "status", "companyName", "unitName", "createdDateStr",
					"acceptRejectDateStr" , "itemName", "itemDescription", "uom", "quantity","lockedQuantity","balanceQuantity", "currency", "unitPrice","pricePerUnit", "totalAmount", "itemTax",
					"taxAmount", "totalAmountWithTax" };

			long count = poItemDao.findTotalPoItemSupplierSummaryCountForCsv(tenantId, startDate, endDate);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
				sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			} else {
				sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			}



			int pageSize = 5000;
			int index = 1;
			int totalPages = (int) Math.ceil(count / (double) pageSize);

			CsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(file),
					new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(new ColumnQuoteMode(2))
							.build());
			CellProcessor[] processors = getProcessorees();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<PoItemSupplierPojo> list = poItemDao.findPoItemSummaryForSupplierCsvReport(tenantId, pageSize,
						pageNo, poIds, searchFilterPoPojo, select_all, startDate, endDate);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (PoItemSupplierPojo pojo : list) {
					pojo.setSrNo(index++);
					if (pojo.getCreatedDate() != null) {
						pojo.setCreatedDateStr(sdf.format(pojo.getCreatedDate()));
					}
					if (pojo.getAcceptRejectDate() != null) {
						pojo.setAcceptRejectdateStr(sdf.format(pojo.getAcceptRejectDate()));
					}


					if (Objects.equals(pojo.getItemIndicator(), "003")) {
						pojo.setLockedQuantity(null);
						pojo.setBalanceQuantity(null);
						pojo.setPricePerUnit(null);
					}else{
						if (pojo.getLockedQuantity() == null) {
							pojo.setLockedQuantity(BigDecimal.ZERO);
						}

						if (pojo.getBalanceQuantity() == null) {
							pojo.setLockedQuantity(BigDecimal.ZERO);
						}

						if (pojo.getPricePerUnit() == null) {
							pojo.setPricePerUnit(BigDecimal.ONE);
						}
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

	private static CellProcessor[] getProcessorees() {
		final CellProcessor[] processors = new CellProcessor[] {

				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional()

		};
		return processors;
	}

	/**
	 * @param po
	 */
	public List<String> validatePo(Po po, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<Po>> constraintViolations = validator.validate(po, validations);
		for (ConstraintViolation<Po> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	@Override
	public long findPoPendingCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,PoStatus status,List<String> businessUnitIds) {
		Long count =  poDao.findPoPendingCountBaseOnTenantAndBuisnessUnit(loggedInUser, tenantId, status,businessUnitIds);
		return (count != null) ? count : 0;
	}

	@Override
	public long findPoRevisedCountBaseOnTenantAndBuisnessUnit(String loggedInUser, String tenantId,List<String> businessUnitIds) {
		Long count =  poDao.findPoRevisedCountBaseOnTenantAndBuisnessUnit(loggedInUser, tenantId, businessUnitIds);
		return (count != null) ? count : 0;
	}

	@Override
	public long findPoOnCancellationCountBaseOnTenantAndBuisnessUnit(String loggedInUser , String tenantId,List<String> businessUnitIds) {
		Long count = poDao.findPoOnCancellationCountBaseOnTenantAndBuisnessUnit(loggedInUser , tenantId, businessUnitIds);
		return (count != null) ? count : 0;
	}

	//PH-PG-4113
	@Override
	@Transactional(readOnly = false)
	public void sendPoFinishMail(Po po,String action) {
		try {
			String poId = po.getId();
			String prKey = (po.getPr() != null) ? po.getPr().getId() : "";
			List<PoTeamMember> teamMembers = poDao.findAssociateOwnerOfPo(poId, TeamMemberType.Associate_Owner);

			User poCreator = po.getCreatedBy();
			String userLoggedIn = SecurityLibrary.getLoggedInUser().getId();
			String poCreatorEmail = poCreator.getCommunicationEmail();

			LOG.info("PO Creator: " + poCreator.getId());
			LOG.info("User  Logged In: " + userLoggedIn);

			String subject = action.equals("draft")?"PO Finished.":" Revise PO Finished.";
			String url = APP_URL + "/buyer/poView/" + poId + "?prId=" + prKey;
			HashMap<String, Object> emailContent = createEmailFinishContent(po, url,action);

			// Send email to other PO owners
			sendEmailToPoOwners(po,teamMembers, userLoggedIn, emailContent, subject);

			// Send dashboard notification
			sendDashboardNotificationToCreator(po, url);

			//is need to send notification mail to supplier?based on condition
		} catch (Exception e) {
			LOG.error("Error while sending PO finish mail: " + e.getMessage(), e);
		}
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage, User user,NotificationType notificationType) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(user);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(notificationType);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	@Override
	@Transactional(readOnly = false)
	public Po updatePoApproval(Po po, User loggedInUser ) {
		Po persistObj = poDao.findById(po.getId());
		List<String> auditMessages = new ArrayList<>();

		// Maps for existing users and approval types by level
		Map<Integer, List<String>> existingUsers = new HashMap<>();
		Map<Integer, ApprovalType> existingTypes = new HashMap<>();

		// Populate existing data
		for (PoApproval approvalRequest : persistObj.getApprovals()) {
			Integer level = approvalRequest.getLevel();
			List<String> users = existingUsers.computeIfAbsent(level, k -> new ArrayList<>());

			if (CollectionUtil.isNotEmpty(approvalRequest.getApprovalUsers())) {
				for (PoApprovalUser  approvalUser  : approvalRequest.getApprovalUsers()) {
					users.add(approvalUser.getUser().getName());
				}
			}
			existingTypes.put(level, approvalRequest.getApprovalType());
		}

		int newLevel = 1;

		if (CollectionUtil.isNotEmpty(po.getApprovals())) {
			for (PoApproval app : po.getApprovals()) {
				if(!app.getApprovalUsers().isEmpty()){
					LOG.info(" >>>>>>>>>>>>>>>>>>>>>>>"+app.getApprovalUsers());
					app.setPo(po);
					app.setLevel(newLevel);
					ApprovalType existingType = existingTypes.get(newLevel);
					List<String> existingUser_List = existingUsers.get(newLevel);
					List<String> levelUsers = new ArrayList<>();

					LOG.info("app Type: {}", app.getApprovalType());

					if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
						for (PoApprovalUser  approvalUser  : app.getApprovalUsers()) {
							approvalUser .setApproval(app);
							approvalUser .setId(null); // Reset ID for new user
							String userName = approvalUser.getUser().getName();

							// Check if user is new
							if (CollectionUtil.isEmpty(existingUser_List) || !existingUser_List.contains(userName)) {
								auditMessages.add("Approval Level " + app.getLevel() + " User " + userName + " has been added as Approver");
							}
							levelUsers.add(userName);
						}

						// Check for removed users
						if (CollectionUtil.isNotEmpty(existingUser_List)) {
							for (String existingUser  : existingUser_List) {
								if (!levelUsers.contains(existingUser )) {
									auditMessages.add("Approval Level " + app.getLevel() + " User " + existingUser  + " has been removed as Approver");
								}
							}
						}
					}

					// Check if approval type has changed
					if (existingType != null && existingType != app.getApprovalType()) {
						auditMessages.add("Approval Level " + app.getLevel() + " Type changed from " +
								(existingType == ApprovalType.OR ? "Any" : "All") + " to " +
								(app.getApprovalType() == ApprovalType.OR ? "Any" : "All"));
					}

					newLevel++;
				}
			}
		} else {
			LOG.warn("Approval levels are empty.");
		}

		// Handle removal of users in levels beyond the new highest level
		while (CollectionUtil.isNotEmpty(existingUsers.get(newLevel))) {
			for (String existingUser  : existingUsers.get(newLevel)) {
				auditMessages.add("Approval Level " + newLevel + " User " + existingUser  + " has been removed as Approver");
			}
			newLevel++;
		}

		// Update persistent object
		persistObj.setApprovals(po.getApprovals());
		persistObj.setActionBy(loggedInUser );
		persistObj.setActionDate(new Date());
		persistObj = poDao.saveOrUpdate(persistObj);

		// Log audit messages if any
		if (CollectionUtil.isNotEmpty(auditMessages)) {
			String auditMessage = String.join(". ", auditMessages) + ". ";
			try {
				PoAudit audit = new PoAudit(po, loggedInUser , new Date(), PoAuditType.UPDATE, auditMessage);
				poAuditService.save(audit);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(
						AuditTypes.UPDATE,
						"Approval Route is updated for PO '" + persistObj.getId() + "'. " + auditMessage,
						loggedInUser .getTenantId(),
						loggedInUser ,
						new Date(),
						ModuleType.PO
				);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error saving Sourcing Request Audit for change of approvers: {}", e.getMessage(), e);
			}
		}
		return persistObj;
	}


	@Override
	@Transactional(readOnly = false)
	public List<PoTeamMember> addTeamMemberToList(String poId, String userId, TeamMemberType memberType) {
		LOG.info("ServiceImpl........." + "addTeamMemberToList----TeamMember" + " poId: " + poId + " userId: " + userId);
		Po po = getLoadedPoById(poId);
		List<PoTeamMember> teamMembers = po.getPoTeamMembers();
		LOG.info("teamMembers :********: " + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<PoTeamMember>();
		}
		User user = userService.getUsersById(userId);
		PoTeamMember poTeamMember = new PoTeamMember();
		poTeamMember.setPo(po);
		poTeamMember.setUser(user);

		boolean exists = false;
		String previousMemberType = "";
		for (PoTeamMember member : teamMembers) {
			if (member.getUser().getId().equals(userId)) {
				poTeamMember = member;
				previousMemberType = member.getTeamMemberType().getValue();
				exists = true;
				break;

			}
		}
		poTeamMember.setTeamMemberType(memberType);
		if (!exists) {
			teamMembers.add(poTeamMember);
		}
		LOG.info("TeamMembers : " + poTeamMember.toLogString());

		po.setPoTeamMembers(teamMembers);
		poDao.update(po);

		try {
			if (!exists) {
				PoAudit audit = new PoAudit(SecurityLibrary.getLoggedInUser(), new java.util.Date(), PoAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.added", new Object[] { user.getName(), memberType.getValue() }, Global.LOCALE), po);
				poAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been added as '"+memberType.getValue()+ "' for PO '"+po.getPoId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PO);
				buyerAuditTrailDao.save(buyerAuditTrail);

				LOG.info("************* PO Team Member event audit added successfully *************");
			} else {
				if (!previousMemberType.equalsIgnoreCase(memberType.getValue())) {
					PoAudit audit = new PoAudit(SecurityLibrary.getLoggedInUser(), new java.util.Date(), PoAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.changed", new Object[] { user.getName(), previousMemberType, memberType.getValue() }, Global.LOCALE), po);
					poAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + user.getName() + "' has been changed from '"+previousMemberType+ "'to '"+memberType.getValue()+"' for PO '"+po.getPoId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PO);
					buyerAuditTrailDao.save(buyerAuditTrail);

					LOG.info("************* PO Team Member event audit changed successfully *************");
				}
			}

		} catch (Exception e) {
			LOG.info("Error saving audit details: " + e.getMessage());
		}
		return teamMembers;
	}

	@Override
	public List<EventTeamMember> getPlainTeamMembersForPo(String poId) {
		return poDao.getPlainTeamMembersForPo(poId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<User> removeTeamMemberfromList(String poId, String userId, PoTeamMember dbTeamMember) {
		Po po = getLoadedPoById(poId);
		List<PoTeamMember> teamMembers = po.getPoTeamMembers();
		LOG.info("**************" + teamMembers);
		if (teamMembers == null) {
			teamMembers = new ArrayList<PoTeamMember>();
		}
		// LOG.info(teamMembers);
		teamMembers.remove(dbTeamMember);
		dbTeamMember.setPo(null);
		po.setPoTeamMembers(teamMembers);
		poDao.update(po);

		try {
			PoAudit audit = new PoAudit(SecurityLibrary.getLoggedInUser(), new java.util.Date(), PoAuditType.UPDATE, messageSource.getMessage("pr.team.member.audit.removed", new Object[] { dbTeamMember.getUser().getName(), dbTeamMember.getTeamMemberType().getValue() }, Global.LOCALE), po);
			poAuditService.save(audit);

			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "'" + dbTeamMember.getUser().getName() + "' has been removed as '"+dbTeamMember.getTeamMemberType().getValue()+ "' for PO '"+po.getPrId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.PR);
			buyerAuditTrailDao.save(buyerAuditTrail);

			LOG.info("************* PR Team Member removed successfully *************");
		} catch (Exception e) {
			LOG.info("Error removing audit details: " + e.getMessage());
		}

		List<User> userList = new ArrayList<User>();
		LOG.info("TeamMembers getTeamMembers(): " + po.getPoTeamMembers());
		for (PoTeamMember app : po.getPoTeamMembers()) {
			try {
				userList.add((User) app.getUser().clone());
			} catch (Exception e) {
				LOG.error("Error constructing list of users after removing TeamMembers operation : " + e.getMessage(), e);
			}
		}
		return userList;
	}

	@Override
	public PoTeamMember getPoTeamMemberByUserIdAndPoId(String poId, String userId) {
		return poDao.getPoTeamMemberByUserIdAndPoId(poId, userId);
	}

	@Override
	public List<PoItem> findAllChildPoItemByPoId(String poId) {
		return poItemDao.findAllChildPoItemByPoId(poId);
	}

	@Override
	public List<PoItem> findAllLoadedChildPoItemByPoId(String poId) {
		List<PoItem> list = poItemDao.findAllChildPoItemByPoId(poId);
		for (PoItem poItem : list) {
			if (poItem.getProductCategory() != null)
				poItem.getProductCategory().getId();
		}

		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public List<PoItem> deletePoItemsByPoId(String poId) {
		List<PoItem> returnList = new ArrayList<PoItem>();
		poItemDao.deletePoItemsByPoId(poId);
		List<PoItem> list = poItemDao.getPoItemsbyId(poId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (PoItem item : list) {
				PoItem parent = item.createShallowCopy();
				if (item.getParent() == null) {
					returnList.add(parent);
				}
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (PoItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<PoItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
						LOG.info("tot with tax :" + child.getTotalAmountWithTax());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void deletefieldInPoItems(String label, String poId) {
		poItemDao.deleteNewFieldPo(label, poId);
	}

	@Override
	@Transactional(readOnly = false)
	@Deprecated
	public PoItem getPoItembyPoItemId(String itemId) {
		return poItemDao.getPoItembyPoItemId(itemId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reOrderPoItems(PoItem poItem) throws NotAllowedException {
		LOG.info("po ITEM Object :: " + poItem.toLogString());
		PoItem poDbItem = getPoItembyPoItemId(poItem.getId());
		if (CollectionUtil.isNotEmpty(poDbItem.getChildren()) && poItem.getParent() != null) {
			LOG.info("Po Item cannot be made a child if it has sub items");
			throw new NotAllowedException(messageSource.getMessage("subitem.reOrder.error", new Object[] { poDbItem.getItemName() }, Global.LOCALE));
		}
		LOG.info("DB Po ITEM DETAILS ::" + poDbItem.toLogString());
		int oldOrder = poDbItem.getOrder();
		int newOrder = poItem.getOrder();
		int oldLevel = poDbItem.getLevel();
		int newLevel = poItem.getOrder(); // this will be ignored if it is made a child

		PoItem newParent = null;
		if (poItem.getParent() != null && StringUtils.checkString(poItem.getParent().getId()).length() > 0) {
			newParent = getPoItembyPoItemId(poItem.getParent().getId());
		}
		PoItem oldParent = poDbItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}
		// Update it to new position.
		poDbItem.setOrder(newOrder);
		poDbItem.setLevel(newParent == null ? poItem.getOrder() : poItem.getParent().getLevel());
		poDbItem.setParent(newParent);
		poItemDao.updateItemOrder(poDbItem.getPo().getId(), poDbItem, (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
	}


	@Override
	public List<PoTeamMember> getPlainTeamMembersForPoSummary(String poId) {
		return poDao.getPlainTeamMembersForPoSummary(poId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void handlePoAction(String poId, PoStatus poStatus, String remarks, User loggedInUser , JRSwapFileVirtualizer virtualizer) throws ApplicationException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		Po po = poDao.findByPoId(poId);
		if (po == null) {
			throw new ApplicationException("PO details not found");
		}

		LOG.info("is Revised >>>> "+po.getRevised());
		LOG.info("is Cancelled >>>> "+po.getCancelled());
		LOG.info("Current Status >>>> "+po.getStatus());
		LOG.info("Old Status >>>> "+po.getOldStatus());
		Boolean revised = po.getRevised();

		// Handle different PO status cases
		switch (poStatus) {
			case ACCEPTED:
				po.setStatus(PoStatus.ACCEPTED);
				break;
			case DECLINED:
				//already handle in EventSupplierSubmissionDetailController func /delinedPo
				po.setStatus(PoStatus.DECLINED);
				break;
			case SUSPENDED:
				handleSuspendedPo(po, loggedInUser , remarks, virtualizer);
				break;
			case CANCELLED:
				handleCancelledPo(po, remarks);
				break;
			case ORDERED:
				handleOrderedPo(po, loggedInUser , revised);
				po.setStatus(PoStatus.ORDERED);
				break;
			case REVISE:
				handleRevisedPo(po, loggedInUser , remarks, virtualizer);
				break;
			case READY:
				break;

			default:
				break;
		}
		updatePoChangeDate(po, poStatus, loggedInUser);
	}

	private void updatePoChangeDate(Po po, PoStatus poStatus, User loggedInUser) {
		po.setModifiedBy(loggedInUser );
		po.setModifiedDate(new Date());
		po.setOldStatus(po.getStatus());
		// Save the updated PO

		poDao.save(po);
		LOG.info("Last Status after action >>>> "+po.getStatus());
	}


	private void createAuditTrailForSupplierRevised(Po po, User loggedInUser , String remarks) {
		createSupplierAuditTrail(po, loggedInUser , AuditTypes.REVISED,
				PoAuditType.REVISED,
				String.format("PO \"%s\" revision finished.%s", po.getPoNumber(), getRemarkFromAction(remarks)));
	}

	private void createAuditTrailForBuyerRevised(Po po, User loggedInUser , String remarks) {
		createBuyerAuditTrail(po, loggedInUser , AuditTypes.REVISED,
				PoAuditType.REVISED,
				String.format("PO \"%s\" revision finished.%s", po.getPoNumber(), getRemarkFromAction(remarks)));
	}

	private void createAuditTrailForSupplierSuspended(Po po, User loggedInUser , String remarks) {
		createSupplierAuditTrail(po, loggedInUser , AuditTypes.SUSPENDED,
				PoAuditType.SUSPENDED,
				String.format("PO \"%s\" suspended.%s", po.getPoNumber(), getRemarkFromAction(remarks)));
	}

	private void createAuditTrailForBuyerSuspended(Po po, User loggedInUser , String remarks) {
		createBuyerAuditTrail(po, loggedInUser , AuditTypes.SUSPENDED,
				PoAuditType.SUSPENDED,
				String.format("PO \"%s\" suspended.%s", po.getPoNumber(), getRemarkFromAction(remarks)));
	}

	private void createAuditTrailForBuyerOrdered(Po po, User loggedInUser , String remarks) {
		createBuyerAuditTrail(po, loggedInUser , AuditTypes.ORDERED,
				PoAuditType.ORDERED,
				String.format("PO \"%s\" sent to supplier \"%s\".",
						po.getPoNumber(),
						po.getSupplier().getSupplier().getCompanyName()));
	}

	private void createAuditTrailForSupplierOrdered(Po po, User loggedInUser , String remarks) {
		createSupplierAuditTrail(po, loggedInUser , AuditTypes.ORDERED,
				PoAuditType.ORDERED,
				String.format("PO \"%s\" received from \"Service Desk\".%s",
						po.getPoNumber(), getRemarkFromAction(remarks)));
	}

	private void createAuditTrailForBuyerLastApproverCancelled(Po po, User loggedInUser , String remarks) {
		createBuyerAuditTrail(po, loggedInUser , AuditTypes.CANCELLED,
				PoAuditType.CANCELLED,
				String.format("PO \"%s\" cancelled.%s", po.getPoNumber(), getRemarkFromAction(remarks)));
	}

	private void createAuditTrailForSupplierLastApproverCancelled(Po po, User loggedInUser , String remarks) {

		createSupplierAuditTrail(po, loggedInUser , AuditTypes.CANCELLED,
				PoAuditType.CANCELLED,
				String.format("PO \"%s\" cancelled by \"Service Desk\".%s",
						po.getPoNumber(), getRemarkFromAction(remarks)));

	}

	private void createAuditTrailForBuyerSubmitCancel(Po po, User loggedInUser , String remarks,boolean hasApprovals,PoStatus currentStatus) {
		//on suspend with approval
		LOG.info("Status"+currentStatus);
		LOG.info("has approval"+hasApprovals);

		if(PoStatus.SUSPENDED.equals(currentStatus) && hasApprovals){
			createBuyerAuditTrail(po, loggedInUser , AuditTypes.FINISH,
					PoAuditType.FINISH,
					String.format("PO \"%s\" to to cancel.%s", po.getPoNumber(), getRemarkFromAction(remarks)));
		//on suspend without approval
		}else if(PoStatus.SUSPENDED.equals(currentStatus) && !hasApprovals){
			createBuyerAuditTrail(po, loggedInUser , AuditTypes.CANCELLED,
					PoAuditType.CANCELLED,
					String.format("PO \"%s\" cancelled.%s", po.getPoNumber(), getRemarkFromAction(remarks)));
		//draft
		}else{
			createBuyerAuditTrail(po, loggedInUser , AuditTypes.CANCELLED,
					PoAuditType.CANCELLED,
					String.format("PO \"%s\" cancelled.%s", po.getPoNumber(), getRemarkFromAction(remarks)));
		}


	}

	private void createAuditTrailForSupplierSubmitCancel(Po po, User loggedInUser , String remarks,boolean hasApprovals,PoStatus currentStatus) {
		if(po.getRevised()) {
			createSupplierAuditTrail(po, loggedInUser, AuditTypes.FINISH,
					PoAuditType.FINISH,
					String.format("PO \"%s\" cancelled by \"Service Desk\".%s",
							po.getPoNumber(), getRemarkFromAction(remarks)));
		}
	}

	private void createBuyerAuditTrail(Po po, User loggedInUser , AuditTypes auditType,
									   PoAuditType poAuditType, String description) {
		try {
			LOG.info("BuyerAudit Message >>>>>>>>>>>>>>>> "+description);
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(
					auditType,
					description,
					loggedInUser.getTenantId(),
					loggedInUser,
					new Date(),
					ModuleType.PO
			);
			buyerAuditTrailDao.save(buyerAuditTrail);
			savePoAudit(po, loggedInUser , poAuditType, description, PoAuditVisibilityType.BUYER);
		} catch (Exception e) {
			LOG.error("Error creating buyer audit trail: {}", e.getMessage(), e);
		}
	}

	private void createSupplierAuditTrail(Po po, User loggedInUser , AuditTypes auditType,
										  PoAuditType poAuditType, String description) {
		try {
			LOG.info("SupplierAudit Trail Message >>>>"+description);
			SupplierAuditTrail supplierAuditTrail = new SupplierAuditTrail(
					auditType,
					description,
					loggedInUser.getTenantId(),
					loggedInUser,
					new Date(),
					ModuleType.PO
			);
			supplierAuditTrailDao.save(supplierAuditTrail);
			savePoAudit(po, loggedInUser , poAuditType, description, PoAuditVisibilityType.SUPPLIER);
		} catch (Exception e) {
			LOG.error("Error creating supplier audit trail: {}", e.getMessage(), e);
		}
	}

	private void savePoAudit(Po po, User loggedInUser , PoAuditType poAuditType,
							 String description, PoAuditVisibilityType visibilityType) {
		try {
			PoAudit poAudit = new PoAudit();
			poAudit.setAction(poAuditType);
			poAudit.setActionBy(loggedInUser);
			poAudit.setActionDate(new Date());
			poAudit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
			if (po.getSupplier() != null && po.getSupplier().getSupplier() != null) {
				poAudit.setSupplier(po.getSupplier().getSupplier());
			}
			poAudit.setDescription(description);
			poAudit.setVisibilityType(visibilityType);
			poAudit.setPo(po);
			poAuditService.save(poAudit);
		} catch (Exception e) {
			LOG.error("Error saving PO audit: {}", e.getMessage(), e);
		}
	}

	private String getRemarkFromAction(String remarks){
		return remarks!=null?"Remarks:"+remarks:"";
	}

	private void handleCancelledPo(Po po, String remarks) {
		po.setCancelReason(remarks);
		LOG.info("Status before refresh approval: " + po.getStatus());

		boolean hasApprovals = po.getApprovals() != null && !po.getApprovals().isEmpty();
		PoStatus currentStatus = po.getStatus();

		LOG.info("has approval : "+hasApprovals);
		LOG.info("current status : "+po.getStatus());

		if (PoStatus.DRAFT.equals(currentStatus)) {
			LOG.info("Force PO status to cancelled");
			updatePoByEventStatus(po,false,false,PoStatus.CANCELLED);
			resetApproval(po);

		}else if (PoStatus.SUSPENDED.equals(currentStatus)) {
				if (hasApprovals) {
					LOG.info("Create New Approval for Cancellation flow");
					updatePoByEventStatus(po,false,true, PoStatus.PENDING);
					resetApproval(po);
					activateApprovalLevel(po.getApprovals().get(0), po);
				} else {
					LOG.info("Force PO status to cancelled");
					updatePoByEventStatus(po,false,false,PoStatus.CANCELLED);
				}
		}else{
			LOG.info("Error >>>>>>>>>>>> Invalid Operation.not is Draft or Suspend");
		}


		createAuditTrailForBuyerSubmitCancel(po, SecurityLibrary.getLoggedInUser(),remarks,hasApprovals,currentStatus );
		createAuditTrailForSupplierSubmitCancel(po, SecurityLibrary.getLoggedInUser(),remarks,hasApprovals,currentStatus );

	}

	private void updatePoByEventStatus(Po po,boolean isRevised,boolean isCancelled, PoStatus newStatus) {
		po.setRevised(isRevised);
		po.setCancelled(isCancelled);
		po.setStatus(newStatus);
	}

	private void activateApprovalLevel(PoApproval poApproval, Po po) {
		LOG.info("Setting Approval level " + poApproval.getLevel() + " as Active level");
		poApproval.setActive(true);

		for (PoApprovalUser  nextLevelUser  : poApproval.getApprovalUsers()) {
			String buyerTimeZone = getTimeZoneByBuyerSettings(nextLevelUser.getUser().getTenantId(), "GMT+8:00");
			sendEmailToPoApprovers(po, nextLevelUser , buyerTimeZone);
		}
	}

	/**
	 * @param po
	 * @param user
	 */
	public void sendEmailToPoApprovers(Po po, PoApprovalUser  user, String buyerTimeZone) {
		LOG.info(">>>>>>>>>>>>>>> Email Request Approval ");

		String poId = po.getId();
		String prKey = (po.getPr() != null) ? po.getPr().getId() : "";
		String mailTo = user.getUser().getCommunicationEmail();
		String subject = po.getCancelled() ? "Cancel PO Approval Request" : "Revise PO Approval Request";
		LOG.info("Subject Email: " + subject);

		String url = APP_URL + "/buyer/poView/" + poId + "?prId=" + prKey;
		HashMap<String, Object> map = createEmailMap(po, user, buyerTimeZone, url);
		map.put("message", po.getCancelled() ?
				"Your attention is required for Approval of following PO cancellation" :
				"Your attention is required for Approval of following PO");

		if (isEmailValid(mailTo, user)) {
			String template = po.getCancelled() ? Global.PO_CANCEL_APPROVAL_REQUEST_TEMPLATE : Global.PO_REVISE_APPROVAL_REQUEST_TEMPLATE;
			sendEmail(mailTo, subject, map, template);
		} else {
			LOG.warn("No communication email configured for user: " + user.getUser().getLoginId() + "... Not going to send email notification");
		}

		String notificationMessage = messageSource.getMessage("revised.po.approval.request.notification.message", new Object[]{po.getName()}, Global.LOCALE);
		sendDashboardNotification(user.getUser(), url, subject, notificationMessage,user.getUser(), NotificationType.APPROVAL_MESSAGE);
	}

	private boolean isEmailValid(String email, PoApprovalUser  user) {
		return StringUtils.checkString(email).length() > 0 && user.getUser().getEmailNotifications();
	}

	private HashMap<String, Object> createEmailMap(Po po, PoApprovalUser  user, String buyerTimeZone, String url) {
		HashMap<String, Object> map = new HashMap<>();
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

		return map;
	}

	private void handleOrderedPo(Po po, User loggedInUser , Boolean revised) {
		po.setOrderedDate(new Date());
		po.setOrderedBy(loggedInUser );

		if (Boolean.TRUE.equals(revised)) {
			po.setRevisePoDetails(null);
			po.setRevised(Boolean.FALSE);
			try {
				poSnapshotDocumentDao.deleteDocument(po.getId());
			} catch (Exception e) {
				LOG.error("Error while deleting PO document " + e.getMessage(), e);
			}

		}

		createAuditTrailForBuyerOrdered(po, SecurityLibrary.getLoggedInUser(),null );
		createAuditTrailForSupplierOrdered(po, SecurityLibrary.getLoggedInUser(),null );

	}
	private void handleRevisedPo(Po po, User loggedInUser , String remarks, JRSwapFileVirtualizer virtualizer) {
		LOG.info("PO is revised");

		boolean hasApprovals = po.getApprovals() != null && !po.getApprovals().isEmpty();
		PoStatus currentStatus = po.getStatus();

		if (PoStatus.SUSPENDED.equals(currentStatus)) {
			if (hasApprovals) {
				LOG.info("Create New Approval for Cancellation flow");
				resetApproval(po);
				activateApprovalLevel(po.getApprovals().get(0), po);
				updatePoByEventStatus(po,true,false, PoStatus.PENDING);
			} else {
				LOG.info("Force PO status to READY");
				updatePoByEventStatus(po,false,false,PoStatus.READY);
			}
		}

		//createPoReviseSnapshot(po, remarks);
		po.setReviseJustification(remarks);
		po.setPoRevisedDate(new Date());

		//only for buyer ,supplier no need
		createAuditTrailForBuyerRevised(po, SecurityLibrary.getLoggedInUser(),remarks );

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
	private void handleSuspendedPo(Po po, User loggedInUser , String remarks, JRSwapFileVirtualizer virtualizer) {
		resetApprovalStatus(po, po.getApprovals());
		po.setReviseJustification(remarks);
		po.setStatus(PoStatus.SUSPENDED);
		createAuditTrailForBuyerSuspended(po, loggedInUser,remarks);
		createAuditTrailForSupplierSuspended(po,loggedInUser,remarks);
	}

	private void resetApproval(Po po) {
		try {
			List<PoApproval> approvalList = po.getApprovals();
			if (CollectionUtil.isNotEmpty(approvalList)) {
				for (PoApproval approval : approvalList) {
					approval.setDone(false);
					approval.setActive(false);
					for (PoApprovalUser  user : approval.getApprovalUsers()) {
						user.setActionDate(null);
						user.setApprovalStatus(ApprovalStatus.PENDING);
						user.setRemarks(null);
					}
				}
				LOG.info("Reset Approval Done");
			}
		} catch (Exception e) {
			LOG.error("Error while resetting approval " + e.getMessage(), e);
		}
	}
	/**
	 * @param mailTo
	 * @param po
	 * @param actionBy
	 */
	public void sendPoEmailNotificationToSupplier(User mailTo, Po po, User actionBy,String action) {
		String prKey = "";
		String poId = po.getId();
		if(po.getPr() != null){
			prKey = po.getPr().getId();
		}
		String subject = action.equals("suspended") ? "PO suspended" :
				action.equals("cancelled") ? "Your PO is cancelled" :
						"PO Finish";

		String template  = action.equals("suspended")?Global.SUSPEND_PO_SUPPLIER_TEMPLATE:
				action.equals("suspended")?Global.PO_FINISH_TEMPLATE:Global.PO_FINISH_TEMPLATE;

		String url = APP_URL + "/supplier/supplierPrView/" + poId;
		String poCreator = po.getCreatedBy().getId();
		String userLoggedIn = SecurityLibrary.getLoggedInUser().getId();

		String message = action.equals("suspended") ? "Your PO is suspended" :
				action.equals("cancelled") ? "Your PO is cancelled" :
						"has finished the following PO";

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("po", po);
		map.put("userName", mailTo.getName()); //email To
		map.put("actionByName", actionBy.getName());
		map.put("message", message);

		String actionByName = actionBy.getName() + " " + actionBy.getLoginId();
		map.put("actionBy", actionByName);
		map.put("businessUnit", StringUtils.checkString(getBusinessUnitnameByPoId(po.getId())));
		map.put("justification", StringUtils.checkString(po.getReviseJustification()));
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("poDate", df.format(po.getCreatedDate()));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);

		sendEmail(mailTo.getCommunicationEmail(), subject, map, template);

	}
	private HashMap<String, Object> createEmailFinishContent(Po po, String url,String action) {
		HashMap<String, Object> map = new HashMap<>();
		String prefiMessage = SecurityLibrary.getLoggedInUser().getName();
		String message = action=="draft"?" has Finished the following PO":" has finished the following PO revision";
		map.put("message",prefiMessage+message );
		map.put("po", po);
		map.put("poNumber", po.getPoNumber());
		map.put("poName", po.getName());
		map.put("businessUnit", StringUtils.checkString(getBusinessUnitname(po.getId())));
		map.put("poReferenceNumber", StringUtils.checkString(po.getReferenceNumber()));

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = getTimeZoneByBuyerSettings(po.getCreatedBy().getTenantId(), "GMT+8:00");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", url);

		return map;
	}
	private void sendDashboardNotificationToCreator(Po po, String url) {
		String notificationMessage = messageSource.getMessage("po.finish.notification.message", new Object[]{po.getName()}, Global.LOCALE);
		sendDashboardNotification(po.getCreatedBy(), url, "PO Finished", notificationMessage, po.getCreatedBy(),NotificationType.FINISH_MESSAGE);
	}

	private void sendEmailToPoOwners(Po po,List<PoTeamMember> teamMembers, String userLoggedIn, HashMap<String, Object> emailContent, String subject) {
		// Send email to PO Creator if not the logged-in user
		LOG.info("Po Creator >>>>> "+po.getCreatedBy().getId());
		LOG.info("User Login >>>>> "+userLoggedIn);
		User poCreator = po.getCreatedBy();

		if (!po.getCreatedBy().getId().equals(userLoggedIn)) {
			emailContent.put("userName",poCreator.getName());
			sendEmailToPoCreator(poCreator, emailContent, subject);
		}

		if (CollectionUtil.isNotEmpty(teamMembers)) {
			for (PoTeamMember assOwn : teamMembers) {
				if (!assOwn.getUser().getId().equals(userLoggedIn)) { // Do not send email to self
					String ownerEmail = assOwn.getUser().getCommunicationEmail();
					if (StringUtils.checkString(ownerEmail).length() > 0 && assOwn.getUser().getEmailNotifications()) {
						LOG.info("Sending email to other PO associate owner: " + ownerEmail);
						emailContent.put("eventOwner", assOwn.getUser().getName());
						sendEmail(ownerEmail, subject, emailContent, Global.PO_FINISH_TEMPLATE_OWNER);
					}
				}
			}
		}
	}
	private void sendEmailToPoCreator(User poCreator, HashMap<String, Object> emailContent, String subject) {
		if (StringUtils.checkString(poCreator.getCommunicationEmail()).length() > 0 && poCreator.getEmailNotifications()) {
			LOG.info("Sending email to PO Creator: " + poCreator.getCommunicationEmail());
			sendEmail(poCreator.getCommunicationEmail(), subject, emailContent, Global.PO_FINISH_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user: " + poCreator.getLoginId() + "... Not going to send email notification");
		}
	}

	private void sendEmailsToOwnersOnly(List<PoTeamMember> teamMembers, HashMap<String, Object> emailData, String subject) {
		LOG.info("Sending suspend email only to PO Associate Owners");

		for (PoTeamMember teamMember : teamMembers) {
			if(!teamMember.getUser().getCommunicationEmail().isEmpty()){
				LOG.info("Sending email to PO associate owner: " + teamMember.getUser().getCommunicationEmail());
				emailData.put("userName", teamMember.getUser().getName());
				sendEmail(teamMember.getUser().getCommunicationEmail(), subject, emailData, Global.SUSPEND_PO_TEMPLATE);
			}
		}
	}
}
