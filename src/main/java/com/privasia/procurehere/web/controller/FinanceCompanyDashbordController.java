package com.privasia.procurehere.web.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.entity.FinancePo;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.PoFinanceService;


@Controller
@RequestMapping(value = "/finance")
public class FinanceCompanyDashbordController extends DashboardBase {

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);
	@Autowired
	PoFinanceService poFinanceService;

	@RequestMapping(value = "/financeDashboard", method = RequestMethod.GET)
	public String FinanceDashBord(Model model) {

		model.addAttribute("newPo", poFinanceService.findTotalCountPoForFinanceByStatus(SecurityLibrary.getLoggedInUserTenantId(), FinancePoStatus.NEW,FinancePoType.SHARED));
		model.addAttribute("newRequestedPo", poFinanceService.findTotalCountPoForFinanceByStatus(SecurityLibrary.getLoggedInUserTenantId(), FinancePoStatus.NEW,FinancePoType.REQUESTED));
		model.addAttribute("submitted", poFinanceService.findTotalCountPoForFinanceByStatus(SecurityLibrary.getLoggedInUserTenantId(), FinancePoStatus.SUBMITED,FinancePoType.REQUESTED));
		model.addAttribute("bankSettle", poFinanceService.findTotalCountPoForFinanceByStatus(SecurityLibrary.getLoggedInUserTenantId(), FinancePoStatus.BANK_SETTLED,FinancePoType.REQUESTED));
		model.addAttribute("bankRejected", poFinanceService.findTotalCountPoForFinanceByStatus(SecurityLibrary.getLoggedInUserTenantId(), FinancePoStatus.BANK_REJECTED,FinancePoType.REQUESTED));
		model.addAttribute("financed", poFinanceService.findTotalCountPoForFinanceByStatus(SecurityLibrary.getLoggedInUserTenantId(), FinancePoStatus.FINANCED,FinancePoType.REQUESTED));
		model.addAttribute("bankCollected", poFinanceService.findTotalCountPoForFinanceByStatus(SecurityLibrary.getLoggedInUserTenantId(), FinancePoStatus.BANK_COLLECTED,FinancePoType.REQUESTED));
		model.addAttribute("financeSettled", poFinanceService.findTotalCountPoForFinanceByStatus(SecurityLibrary.getLoggedInUserTenantId(), FinancePoStatus.FINANCE_SETTLED,FinancePoType.REQUESTED));

		return "financeDashboard";
	}

	@RequestMapping(value = "/newPoData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> financenewPoList(TableDataInput input) {
		LOG.info("------------dash board financePoList-------------------");
		List<FinancePo> financePos = null;
		try {

			financePos = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, null, null, FinancePoStatus.NEW, null,FinancePoType.SHARED);
			for (FinancePo financePo : financePos) {
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}
		} catch (Exception e) {
			LOG.error("Error while loading  list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FinancePo>>(new TableData<FinancePo>(financePos, financePos.size()), HttpStatus.OK);
	}
	@RequestMapping(value = "/newRequestedPoData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> financenewRewuestedPoList(TableDataInput input) {
		LOG.info("------------dash board financePoList-------------------");
		List<FinancePo> financePos = null;
		try {

			financePos = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, null, null, FinancePoStatus.NEW, null,FinancePoType.REQUESTED);
			for (FinancePo financePo : financePos) {
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}
		} catch (Exception e) {
			LOG.error("Error while loading  list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FinancePo>>(new TableData<FinancePo>(financePos, financePos.size()), HttpStatus.OK);
	}
	@RequestMapping(value = "/submittedPoData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> financeSubmmitedPoList(TableDataInput input) {
		LOG.info("------------dash board financePoList-------------------");
		List<FinancePo> financePos = null;
		try {

			financePos = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, null, null, FinancePoStatus.SUBMITED, null,FinancePoType.REQUESTED);
			for (FinancePo financePo : financePos) {
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}
		} catch (Exception e) {
			LOG.error("Error while loading  list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FinancePo>>(new TableData<FinancePo>(financePos, financePos.size()), HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/bankRejectedData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> financeBankRejectedPoList(TableDataInput input) {
		LOG.info("------------dash board financePoList-------------------");
		List<FinancePo> financePos = null;
		try {

			financePos = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, null, null, FinancePoStatus.BANK_REJECTED, null,FinancePoType.REQUESTED);
			for (FinancePo financePo : financePos) {
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}
		} catch (Exception e) {
			LOG.error("Error while loading  list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FinancePo>>(new TableData<FinancePo>(financePos, financePos.size()), HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/financedData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> financeFinancedPoList(TableDataInput input) {
		LOG.info("------------dash board financePoList-------------------");
		List<FinancePo> financePos = null;
		try {

			financePos = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, null, null, FinancePoStatus.FINANCED, null,FinancePoType.REQUESTED);
			for (FinancePo financePo : financePos) {
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}
		} catch (Exception e) {
			LOG.error("Error while loading  list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FinancePo>>(new TableData<FinancePo>(financePos, financePos.size()), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/bankCollectedData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> financeBankCollectedPoList(TableDataInput input) {
		LOG.info("------------dash board financePoList-------------------");
		List<FinancePo> financePos = null;
		try {

			financePos = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, null, null, FinancePoStatus.BANK_COLLECTED, null,FinancePoType.REQUESTED);
for (FinancePo financePo : financePos) {
	financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
}
		} catch (Exception e) {
			LOG.error("Error while loading  list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FinancePo>>(new TableData<FinancePo>(financePos, financePos.size()), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/financeSettledData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>>financeSettledPoList(TableDataInput input) {
		LOG.info("------------dash board financePoList-------------------");
		List<FinancePo> financePos = null;
		try {

			financePos = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, null, null, FinancePoStatus.FINANCE_SETTLED, null,FinancePoType.REQUESTED);
			for (FinancePo financePo : financePos) {
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}
		} catch (Exception e) {
			LOG.error("Error while loading  list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FinancePo>>(new TableData<FinancePo>(financePos, financePos.size()), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/bankSettleData", method = RequestMethod.GET)
	public ResponseEntity<TableData<FinancePo>> financeBankSettlePoList(TableDataInput input) {
		LOG.info("------------dash board financePoList-------------------");
		List<FinancePo> financePos = null;
		try {

			financePos = poFinanceService.findAllSearchFilterPoForFinance(SecurityLibrary.getLoggedInUserTenantId(), input, null, null, FinancePoStatus.BANK_SETTLED, null,FinancePoType.REQUESTED);
			for (FinancePo financePo : financePos) {
				financePo.setStatusValue(financePo.getFinancePoStatus().getValue());
			}
		} catch (Exception e) {
			LOG.error("Error while loading  list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FinancePo>>(new TableData<FinancePo>(financePos, financePos.size()), HttpStatus.OK);
	}
	
}