package com.privasia.procurehere.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.FinancePoAuditDao;
import com.privasia.procurehere.core.dao.PoFinanceDao;
import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.entity.FinancePoAudit;
import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.PoFinanceService;


@Service
@Transactional(readOnly = true)
public class PoFinanceServiceImpl implements PoFinanceService {

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@Autowired
	PoFinanceDao poFinanceDao;

	@Autowired
	FinancePoAuditDao financePoAuditDao;

	@Autowired
	ServletContext context;

	@Override
	public FinancePo getPoFinanceByPrIdAndSupID(String prId, String supId, String loggedInUserTenantId) {
		return poFinanceDao.getPoFinanceByPrIdAndSupID(prId, supId, loggedInUserTenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public FinancePo saveFinancePo(FinancePo pofinance) {
		return poFinanceDao.save(pofinance);

	}

	@Override
	public List<FinancePoAudit> getAuditForFinancePo(String prId) {
		return financePoAuditDao.getAuditForFinancePo(prId);
	}

	@Override
	public FinancePo findById(String id) {
		return poFinanceDao.findById(id);
	}

	@Override
	public List<FinancePo> findAllSearchFilterPoForFinance(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, FinancePoStatus approved, String selectedSupplier, FinancePoType financePoType) {
		return poFinanceDao.findAllSearchFilterPoForFinance(loggedInUserTenantId, input, startDate, endDate, approved, selectedSupplier, financePoType);
	}

	@Override
	public long findTotalSearchFilterPoForFinance(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate, FinancePoStatus approved, String selectedSupplier, FinancePoType financePoType) {
		return poFinanceDao.findTotalSearchFilterPoForFinance(loggedInUserTenantId, input, startDate, endDate, approved, selectedSupplier, financePoType);
	}

	@Override
	public long findTotalPoForFinance(String loggedInUserTenantId, FinancePoType financePoType) {
		return poFinanceDao.findTotalPoForFinance(loggedInUserTenantId, financePoType);
	}

	@Override
	@Transactional(readOnly = false)
	public FinancePo updateFinancePo(FinancePo persistFinancePo, User loggedInUser) {
		FinancePo financePo = poFinanceDao.saveOrUpdate(persistFinancePo);
		try {
			FinancePoAudit audit = new FinancePoAudit();
			audit.setAction(financePo.getFinancePoStatus());
			audit.setRemark(financePo.getRemark());
			audit.setReferralFee(financePo.getReferralFee());
			audit.setActionDate(new Date());
			audit.setActionBy(loggedInUser);
			audit.setPo(financePo);

			financePoAuditDao.save(audit);
		} catch (Exception e) {
			LOG.error("Error While save Po finance Audit: " + e.getMessage(), e);
		}

		return financePo;
	}

	@Override
	public long findTotalCountPoForFinanceByStatus(String tenantId, FinancePoStatus status, FinancePoType financePoType) {
		return poFinanceDao.findTotalCountPoForFinanceByStatus(tenantId, status, financePoType);
	}

	@Override
	public FinancePo getPoFinanceForSupplier(String poId, String tenantId) {

		FinancePo po = poFinanceDao.getPoFinanceForSupplier(poId, tenantId);
		return po;

	}

	@Override
	@Transactional(readOnly = true)
	public void downloadFinancePoReports(String[] prArr, HttpServletResponse response, HttpSession session) {
		LOG.info("Download Finance PO reports... " + response.getHeaderNames());
		try {
			List<FinancePo> poList = poFinanceDao.findFinancePoByIds(prArr);
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "financePoReports.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("PO");

			// Style of Heading Cells
			CellStyle styleHeading = workbook.createCellStyle();
			Font font = workbook.createFont();

			font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			styleHeading.setFont(font);
			styleHeading.setVerticalAlignment(CellStyle.ALIGN_LEFT);

			Row titleRow = sheet.createRow(0);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue("PO Level");
			titleCell.setCellStyle(styleHeading);

			// Creating Headings
			Row rowHeading = sheet.createRow(1);
			int i = 0;
			for (String column : Global.FINANCE_PO_REPORT_EXCEL_COLUMNS) {
				Cell cell = rowHeading.createCell(i++);

				cell.setCellValue(column);
				cell.setCellStyle(styleHeading);
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));

			int r = 2, no = 1;
			// Write Data into Excel
			for (FinancePo po : poList) {

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(no);
				no++;
				row.createCell(cellNum++).setCellValue(po.getPo().getBusinessUnit() != null ? po.getPo().getBusinessUnit().getUnitName() : "");
				row.createCell(cellNum++).setCellValue(po.getPo().getBuyer() != null ? po.getPo().getBuyer().getFullName() : "");
				row.createCell(cellNum++).setCellValue(po.getReferenceNum() != null ? po.getReferenceNum() : "");
				row.createCell(cellNum++).setCellValue(po.getPo().getCreatedDate() != null ? sdf.format(po.getPo().getCreatedDate()) : "");
				row.createCell(cellNum++).setCellValue(po.getPo().getPaymentTerm() != null ? po.getPo().getPaymentTerm() : "");
				row.createCell(cellNum++).setCellValue(po.getPo().getCorrespondAddressLine1() != null ? po.getPo().getCorrespondAddressLine1() : "");
				row.createCell(cellNum++).setCellValue(po.getPo().getCorrespondAddressLine2() != null ? po.getPo().getCorrespondAddressLine2() : "");
				Cell cell = row.createCell(cellNum++);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(po.getPo().getCorrespondAddressZip() != null ? po.getPo().getCorrespondAddressZip() : "");
				row.createCell(cellNum++).setCellValue(po.getPo().getCorrespondenceAddress().getCity() != null ? po.getPo().getCorrespondAddressCity() : "");
				row.createCell(cellNum++).setCellValue(po.getPo().getCorrespondenceAddress().getState() != null ? po.getPo().getCorrespondAddressState() : "");
				String deliveryDetails = po.getPo().getDeliveryReceiver() + "; " + po.getPo().getDeliveryAddressLine1() + ", " + po.getPo().getDeliveryAddressCity() + ", " + po.getPo().getDeliveryAddressState() + "; " + (po.getPo().getDeliveryDate() != null ? sdf.format(po.getPo().getDeliveryDate()) : "");
				Cell cell1 = row.createCell(cellNum++);
				cell1.setCellValue(deliveryDetails != null ? deliveryDetails : "");
				CellStyle cs = workbook.createCellStyle();
				cs.setWrapText(true);
				cell1.setCellStyle(cs);
				row.setHeightInPoints((2 * sheet.getDefaultRowHeightInPoints()));
				// sheet.autoSizeColumn(2);
			}
			// Auto Fit
			for (int k = 0; k < 15; k++) {
				sheet.autoSizeColumn(k, true);
			}
			// *********************************item sheet**********************************
			XSSFSheet itemSheet = workbook.createSheet("item");

			CellStyle itemStyleHeading = workbook.createCellStyle();
			Font fontForItem = workbook.createFont();
			fontForItem.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
			itemStyleHeading.setFont(fontForItem);
			itemStyleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

			titleRow = itemSheet.createRow(0);
			titleCell = titleRow.createCell(0);
			titleCell.setCellValue("ITEM Level");
			titleCell.setCellStyle(itemStyleHeading);

			Row ForItem = itemSheet.createRow(1);
			int iItem = 0;
			for (String column : Global.FINANCE_ITEM_REPORT_EXCEL_COLUMNS) {
				Cell cell = ForItem.createCell(iItem++);

				cell.setCellValue(column);
				cell.setCellStyle(itemStyleHeading);
			}

			SimpleDateFormat sdfForItem = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdfForItem.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));

			int rItem = 2;
			// Write Data into Excel
			for (FinancePo po : poList) {
				// For Financial Standard
				DecimalFormat df = null;
				if (po.getPo().getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (po.getPo().getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (po.getPo().getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (po.getPo().getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (po.getPo().getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (po.getPo().getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				// row.createCell(cellNum++).setCellValue(rItem - 1);
				for (PoItem item : po.getPo().getPoItems()) {
					int cellNumNew = 0;
					Row row = itemSheet.createRow(rItem++);
					// row = itemSheet.createRow(rItem++);
					row.createCell(cellNumNew++).setCellValue(po.getReferenceNum() != null ? po.getReferenceNum() : "");
					row.createCell(cellNumNew++).setCellValue((item.getLevel() != null) ? (item.getLevel() + "" + (item.getOrder() != null ? "." + item.getOrder() : "")) : "");
					row.createCell(cellNumNew++).setCellValue((item.getItemName() != null) ? item.getItemName() : "");
					row.createCell(cellNumNew++).setCellValue((item.getItemDescription() != null) ? item.getItemDescription().trim() : "");
					row.createCell(cellNumNew++).setCellValue(item.getProduct() != null ? (item.getProduct().getUom() != null ? item.getProduct().getUom().getUom() : "") : (item.getUnit() != null ? item.getUnit().getUom() : ""));
					Cell cell = row.createCell(cellNumNew++);
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue((item.getQuantity() != null) ? df.format(item.getQuantity()) : ((item.getOrder() == 0) ? "N/A" : ""));
					row.createCell(cellNumNew++).setCellValue((po.getPo().getCurrency() != null) ? po.getPo().getCurrency().getCurrencyCode() : "");
					Cell cell1 = row.createCell(cellNumNew++);
					cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell1.setCellValue((item.getUnitPrice() != null) ? df.format(item.getUnitPrice()) : (item.getOrder() == 0) ? "N/A" : "");

					Cell cell2 = row.createCell(cellNumNew++);
					cell2.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell2.setCellValue((item.getOrder() == 0) ? "N/A" : (item.getTotalAmount() != null ? df.format(item.getTotalAmount()) : " "));

					Cell cell3 = row.createCell(cellNumNew++);
					cell3.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell3.setCellValue((item.getOrder() == 0) ? "N/A" : (item.getTaxAmount() != null ? df.format(item.getTaxAmount()) : " "));

					Cell cell4 = row.createCell(cellNumNew++);
					cell4.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell4.setCellValue((item.getOrder() == 0) ? "N/A" : (item.getTotalAmountWithTax() != null ? df.format(item.getTotalAmountWithTax()) : " "));

					Cell cell5 = row.createCell(cellNumNew++);
					cell5.setCellType(Cell.CELL_TYPE_NUMERIC);
					cell5.setCellValue((item.getOrder() == 0) ? "N/A" : (po.getPo().getAdditionalTax() != null ? df.format(po.getPo().getAdditionalTax()) : " "));
				}
			}
			for (int k = 0; k < 15; k++) {
				itemSheet.autoSizeColumn(k, true);
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

}
