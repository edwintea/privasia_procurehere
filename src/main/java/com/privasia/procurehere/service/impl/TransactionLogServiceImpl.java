package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

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
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.ColumnQuoteMode;

import com.privasia.procurehere.core.entity.TransactionLog;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TransactionLogPojo;
import com.privasia.procurehere.core.supplier.dao.TransactionLogDao;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.TransactionLogService;

/**
 * @author shubham
 */
@Service
@Transactional(readOnly = true)
public class TransactionLogServiceImpl implements TransactionLogService {

	@SuppressWarnings("unused")
	private static Logger LOG = LogManager.getLogger(Global.BUDGET_PLANNER);

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	TransactionLogDao transactionLogDao;

	@Override
	@Transactional(readOnly = false)
	public TransactionLog saveTransactionLog(TransactionLog transactionLog) {
		return transactionLogDao.saveOrUpdate(transactionLog);
	}

	@Override
	public List<TransactionLogPojo> getAlltransactionLogsForTenantId(String loggedInUserTenantId, TableDataInput input) {
		return transactionLogDao.getAlltransactionLogsForTenantId(loggedInUserTenantId, input);
	}

	@Override
	public long findfilteredTotalCountTransactionLogForTenantId(String loggedInUserTenantId, TableDataInput input) {
		return transactionLogDao.findfilteredTotalCountTransactionLogForTenantId(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalTransactionLogForTenantId(String loggedInUserTenantId, TableDataInput input) {
		return transactionLogDao.findTotalTransactionLogForTenantId(loggedInUserTenantId, input);
	}

	@Override
	public void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.TRANSACTION_LOGS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@Override
	public List<TransactionLogPojo> getAlltransactionLogsForTenantId(String loggedInUserTenantId) {
		return transactionLogDao.getAlltransactionLogsForTenantId(loggedInUserTenantId);
	}

	@Override
	public void downloadCsvFileForTransactionLogs(HttpServletResponse response, File file, SimpleDateFormat timeFormat, String tenantId, HttpSession session) {
		ICsvBeanWriter beanWriter = null;
		try {
			String[] columnHeadings = Global.TRANSACTION_LOGS;

			String[] columns = { "referenceNumber", "transactionTimeStampStr", "unitName", "costCenter", "newAmount", "addAmount", "deductAmount", "fromBusinessUnit", "toBusinessUnit", "purchaseOrder", "txStatus", "locked", "prBaseCurrency", "budgetBaseCurrency", "conversionRateAmount", "amountAfterConversion", "remainingAmount" };

			long count = transactionLogDao.getTotalTransactionLogsByTenantIdForCsv(tenantId);
			LOG.info("................ count " + count);
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
				List<TransactionLogPojo> list = transactionLogDao.findAllActiveSupplierByTenantIdAndStatusForCsv(tenantId, PAGE_SIZE, pageNo);

				LOG.info("size ........" + list.size() + ".... count " + count);
				for (TransactionLogPojo pojo : list) {
					if (pojo.getTransactionTimeStamp() != null) {
						pojo.setTransactionTimeStampStr(sdf.format(pojo.getTransactionTimeStamp()));
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

				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // deductAmount
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), //
				new Optional(), // conversionRateAmount
				new Optional(), //
				new Optional(), //
		};
		return processors;
	}
}
