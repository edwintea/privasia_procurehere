/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.privasia.procurehere.core.dao.PaymentTransactionDao;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.pojo.PaymentTransactionPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.PaymentTransactionService;

/**
 * @author Nitin Otageri
 */
@Service
@Transactional(readOnly = true)
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

	@SuppressWarnings("unused")
	private static Logger LOG = LogManager.getLogger(PaymentTransactionServiceImpl.class);

	@Autowired
	PaymentTransactionDao paymentTransactionDao;

	@Override
	public List<PaymentTransaction> findPaymentTransactions(TableDataInput tableParams, Date startDate, Date endDate) {
		List<PaymentTransaction> list = paymentTransactionDao.findPaymentTransactions(tableParams, startDate, endDate);
		if (CollectionUtil.isNotEmpty(list)) {
			for (PaymentTransaction t : list) {

				if (t != null && t.getCountry() != null) {
					t.getCountry().setCreatedBy(null);
					t.getCountry().setModifiedBy(null);
					t.getCountry().getCountryName();
				}
				if (t.getBuyerPlan() != null) {
					t.getBuyerPlan().getShortDescription();
					t.getBuyerPlan().setCreatedBy(null);
					t.getBuyerPlan().setModifiedBy(null);
					t.getBuyerPlan().setRangeList(null);
				}
				if (t.getSupplierPlan() != null) {
					t.getSupplierPlan().getShortDescription();
					t.getSupplierPlan().setCreatedBy(null);
					t.getSupplierPlan().setModifiedBy(null);
				}
				t.setPromoCode(null);
			}
		}
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentTransaction save(PaymentTransaction paymentTransaction) {
		return paymentTransactionDao.saveOrUpdate(paymentTransaction);
	}

	@Override
	@Transactional(readOnly = false)
	public PaymentTransaction update(PaymentTransaction paymentTransaction) {
		return paymentTransactionDao.update(paymentTransaction);
	}

	@Override
	public PaymentTransaction getPaymentTransactionById(String id) {
		return paymentTransactionDao.findById(id);
	}

	@Override
	public long findTotalPaymentTransactions() {
		return paymentTransactionDao.findTotalPaymentTransactions();
	}

	@Override
	public long findTotalFilteredPaymentTransactions(TableDataInput tableParams, Date startDate, Date endDate) {
		return paymentTransactionDao.findTotalFilteredPaymentTransactions(tableParams, startDate, endDate);
	}

	@Override
	public long findTotalPaymentTransactionForBuyer(String buyerId) {
		return paymentTransactionDao.findTotalPaymentTransactionsForBuyer(buyerId);
	}

	@Override
	public long findTotalFilteredPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams) {
		return paymentTransactionDao.findTotalFilteredPaymentTransactionsForBuyer(buyerId, tableParams);
	}

	@Override
	public List<PaymentTransaction> findPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams) {
		List<PaymentTransaction> list = paymentTransactionDao.findPaymentTransactionsForBuyer(buyerId, tableParams);
		for (PaymentTransaction t : list) {
			if (t.getBuyerPlan() != null) {
				t.getBuyerPlan().getShortDescription();
				t.getBuyerPlan().setCreatedBy(null);
				t.getBuyerPlan().setModifiedBy(null);
				t.getBuyerPlan().setRangeList(null);
				t.getBuyerPlan().setPlanPeriodList(null);
			}
			if (t.getPromoCode() != null) {
				t.getPromoCode().getPromoCode();
				t.getPromoCode().setCreatedBy(null);
			}
		}
		return list;
	}

	@Override
	public long findTotalSuccessfulPaymentTransactionForBuyer(String buyerId) {
		return paymentTransactionDao.findTotalSuccessfulPaymentTransactionsForBuyer(buyerId);
	}

	@Override
	public long findTotalSuccessfulFilteredPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams) {
		return paymentTransactionDao.findTotalSuccessfulFilteredPaymentTransactionsForBuyer(buyerId, tableParams);
	}

	@Override
	public List<PaymentTransaction> findSuccessfulPaymentTransactionsForBuyer(String buyerId, TableDataInput tableParams) {
		List<PaymentTransaction> list = paymentTransactionDao.findSuccessfulPaymentTransactionsForBuyer(buyerId, tableParams);
		for (PaymentTransaction t : list) {
			if (t.getBuyerPlan() != null) {
				t.getBuyerPlan().getShortDescription();
				t.getBuyerPlan().setCreatedBy(null);
				t.getBuyerPlan().setModifiedBy(null);
				t.getBuyerPlan().setRangeList(null);
				t.getBuyerPlan().setPlanPeriodList(null);
			}
			if (t.getPromoCode() != null) {
				t.getPromoCode().setCreatedBy(null);
			}
		}
		return list;
	}

	@Override
	public List<PaymentTransaction> findSuccessfulPaymentTransactionsForSupplier(String loggedInUserTenantId, TableDataInput input) {
		List<PaymentTransaction> list = paymentTransactionDao.findSuccessfulPaymentTransactionsForSupplier(loggedInUserTenantId, input);
		for (PaymentTransaction t : list) {
			if (t.getSupplierPlan() != null) {
				t.getSupplierPlan().getShortDescription();
				t.getSupplierPlan().setCreatedBy(null);
				t.getSupplierPlan().setModifiedBy(null);
			}

			if (t.getPromoCode() != null) {
				t.getPromoCode().setCreatedBy(null);
			}
		}
		return list;
	}

	@Override
	public long findTotalSuccessfulFilteredPaymentTransactionsForSupplier(String loggedInUserTenantId, TableDataInput input) {
		return paymentTransactionDao.findTotalSuccessfulFilteredPaymentTransactionsForSupplier(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalSuccessfulPaymentTransactionForSupplier(String loggedInUserTenantId) {
		return paymentTransactionDao.findTotalSuccessfulPaymentTransactionForSupplier(loggedInUserTenantId);
	}

	@Override
	public PaymentTransaction getPaymentTransactionWithSupplierPlanByPaymentTransactionId(String paymentTransactionId) {
		return paymentTransactionDao.getPaymentTransactionWithSupplierPlanByPaymentTransactionId(paymentTransactionId);
	}

	@Override
	public PaymentTransaction getPaymentTransactionWithBuyerPlanById(String paymentTransactionId) {
		return paymentTransactionDao.getPaymentTransactionWithBuyerPlanById(paymentTransactionId);
	}

	@Override
	public void downloadCsvFileForPaymentTransactionList(HttpServletResponse response, File file, PaymentTransactionPojo paymentTransactionPojo, boolean select_all, Date startDate, Date endDate, String[] paymentIds, DateFormat formatter) {
		ICsvBeanWriter beanWriter = null;
		try {
			LOG.info("Company Name:" + paymentTransactionPojo.getCompanyname());
			String[] columnHeadings = Global.ALL_PAYMENT_REPORT_EXCEL_COLUMNS;

			final String[] columns = new String[] { "companyname", "country", "transactionid", "amountWithCurrency", "plan", "transactionTime", "status" };

			long count = 1;

			int PAGE_SIZE = 5000;
			int totalPages = (int) Math.ceil(count / (double) PAGE_SIZE);

			beanWriter = new CsvBeanWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);
			CellProcessor[] processors = getProcessors();
			beanWriter.writeHeader(columnHeadings);

			for (int pageNo = 0; pageNo < totalPages; pageNo++) {
				List<PaymentTransactionPojo> list = paymentTransactionDao.getAllPaymentTransactionsForExcelReport(paymentIds, paymentTransactionPojo, select_all, startDate, endDate, null);
				for (PaymentTransactionPojo pojo : list) {
					if (pojo.getCreatedDate() != null) {
						pojo.setTransactionTime(formatter.format(pojo.getCreatedDate()));
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
		final CellProcessor[] processors = new CellProcessor[] { new Optional(), // companyName
				new Optional(), // country
				new Optional(), // referenceTransactionId
				new Optional(), // totalPriceAmount
				new Optional(), // planName
				new Optional(), // Created Date
				new NotNull() // STATUS
		};
		return processors;
	}

}
