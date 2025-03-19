/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.dao.SupplierPlanDao;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.SupplierPlanService;

/**
 * @author Nitin Otageri
 */
@Service
@Transactional(readOnly = true)
public class SupplierPlanServiceImpl implements SupplierPlanService {

	@SuppressWarnings("unused")
	private static Logger LOG = LogManager.getLogger(SupplierPlanServiceImpl.class);

	@Autowired
	SupplierPlanDao supplierplanDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	ServletContext context;

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Override
	public List<SupplierPlan> findAllPlansByStatus(PlanStatus planStatus) {
		return supplierplanDao.findAllPlansByStatus(planStatus);
	}

	@Override
	public List<SupplierPlan> findAllPlansByStatusForIntegration(PlanStatus planStatus) {
		List<SupplierPlan> planList = supplierplanDao.findAllPlansByStatus(planStatus);
		if (CollectionUtil.isNotEmpty(planList)) {
			for (SupplierPlan plan : planList) {
				plan.setArchiveDate(null);
				plan.setCreatedBy(null);
				plan.setCreatedDate(null);
				plan.setModifiedBy(null);
				plan.setModifiedDate(null);
				plan.setPlanUrl(APP_URL + "/suppliersubscription/get/" + plan.getId());
			}
		}
		return planList;
	}

	@Override
	public List<SupplierPlan> findAllPlansByStatuses(PlanStatus[] planStatuses) {
		return supplierplanDao.findAllPlansByStatuses(planStatuses);
	}

	@Override
	public List<SupplierPlan> findPlans(TableDataInput tableParams) {
		List<SupplierPlan> list = supplierplanDao.findPlans(tableParams);
		if (CollectionUtil.isNotEmpty(list)) {
			for (SupplierPlan plan : list) {
				if (plan.getCreatedBy() != null) {
					plan.getCreatedBy().setCreatedBy(null);
				}
			}
		}
		return list;
	}

	@Override
	public long findTotalPlans() {
		return supplierplanDao.findTotalPlans();
	}

	@Override
	public long findTotalFilteredPlans(TableDataInput tableParams) {
		return supplierplanDao.findTotalFilteredPlans(tableParams);
	}

	@Override
	public boolean isExists(SupplierPlan plan) {
		return supplierplanDao.isExists(plan);
	}

	@Override
	public SupplierPlan getPlanForEditById(String planId) {
		return supplierplanDao.getPlanForEditById(planId);
	}

	@Override
	public SupplierPlan getPlanById(String planId) {
		SupplierPlan plan = supplierplanDao.findById(planId);
		if (plan != null) {
			if (plan.getCurrency() != null) {
				plan.getCurrency().getCurrencyCode();
			}
		}
		return plan;
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPlan savePlan(SupplierPlan plan) {
		return supplierplanDao.saveOrUpdate(plan);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPlan updatePlan(SupplierPlan plan) {
		return supplierplanDao.update(plan);
	}

	@Override
	@Transactional(readOnly = false)
	public void deletePlan(SupplierPlan plan) {
		supplierplanDao.delete(plan);
	}

	@Override
	public List<SupplierPlan> findAllPlansForUpgradeByStatus(PlanStatus active, String planId) {
		return supplierplanDao.findAllPlansForUpgradeByStatus(active, planId);
	}

	@Override
	public SupplierPlan getAllBuyerSupplierPlan() {
		return supplierplanDao.getAllBuyerSupplierPlan();
	}

	@Override
	public void downloadPaymentTransactionExcel(HttpServletResponse response, String loggedInUserTenantId) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "SubscriptionHistory.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			workbook = getExcelWorkBookForPaymentTransaction(loggedInUserTenantId);
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
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error :- " + e.getMessage());
		}

	}

	private XSSFWorkbook getExcelWorkBookForPaymentTransaction(String loggedInUserTenantId) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("User List");
		// Creating Headings
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		for (String column : Global.PAYMENT_TRANSACTION_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}

		List<PaymentTransaction> paymentTransactionList = paymentTransactionDao.getAllPaymentTransactionForSupplierForExcel(loggedInUserTenantId, TransactionStatus.SUCCESS);

		int r = 1;
		if (CollectionUtil.isNotEmpty(paymentTransactionList)) {
			for (PaymentTransaction paymentTransaction : paymentTransactionList) {

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(paymentTransaction.getBuyerPlan() != null ? paymentTransaction.getBuyerPlan().getPlanName() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getCreatedDate() != null ? paymentTransaction.getCreatedDate().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getRemarks() != null ? paymentTransaction.getRemarks() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getReferenceTransactionId() != null ? paymentTransaction.getReferenceTransactionId() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getPriceAmount() != null ? paymentTransaction.getPriceAmount().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getPriceDiscount() != null ? paymentTransaction.getPriceDiscount().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getPromoCodeDiscount() != null ? paymentTransaction.getPromoCodeDiscount().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getAdditionalTax() != null ? paymentTransaction.getAdditionalTax().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getTotalPriceAmount() != null ? paymentTransaction.getTotalPriceAmount().toString() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getCurrencyCode() != null ? paymentTransaction.getCurrencyCode() : "");
				row.createCell(cellNum++).setCellValue(paymentTransaction.getPromoCode() != null ? paymentTransaction.getPromoCode().getPromoCode() : "N/A");
			}
		}
		for (int k = 0; k < 15; k++) {
			sheet.autoSizeColumn(k, true);
		}
		return workbook;
	}

}
