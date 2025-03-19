package com.privasia.procurehere.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.privasia.procurehere.core.entity.TransactionLog;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TransactionLogPojo;

public interface TransactionLogService {

	TransactionLog saveTransactionLog(TransactionLog transactionLog);

	List<TransactionLogPojo> getAlltransactionLogsForTenantId(String loggedInUserTenantId, TableDataInput input);

	long findfilteredTotalCountTransactionLogForTenantId(String loggedInUserTenantId, TableDataInput input);

	long findTotalTransactionLogForTenantId(String loggedInUserTenantId, TableDataInput input);

	void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<TransactionLogPojo> getAlltransactionLogsForTenantId(String loggedInUserTenantId);

	/**
	 * @param response
	 * @param file
	 * @param timeFormat
	 * @param tenantId
	 */
	void downloadCsvFileForTransactionLogs(HttpServletResponse response, File file, SimpleDateFormat timeFormat, String tenantId, HttpSession session);
}
