package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojo;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForBudget;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojoForInternalAndExternal;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.SpentAnalysisService;

@Controller
@RequestMapping("/buyer")
public class SpentAnalysisController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	SpentAnalysisService analysisService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	CostCenterService costCenterService;

	@RequestMapping(path = "/spentAnalysis")
	public String listId(Model model) {
		model.addAttribute("costCenterList", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		List<Integer> yearList = new ArrayList<Integer>();

		// Options to select last 5 years
		for (int i = 0; i < 5; i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, -i);
			yearList.add(cal.get(Calendar.YEAR));
		}
		model.addAttribute("yearList", yearList);

		return "spentAnalysis";

	}

	@RequestMapping(path = "/getPoDataByMonth/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoByMonth(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataByMonth(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataForSubsidiaryByMonth/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoDataForSubsidiaryByMonth(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataForSubsidiaryByMonth(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataForNonSubsidiaryByMonth/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoDataForNonSubsidiaryByMonth(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataForNonSubsidiaryByMonth(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoValueByMonth/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoValueByMonth(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoValueByMonth(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoValueForSubsidiaryByMonth/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoValueForSubsidiaryByMonth(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoValueForSubsidiaryByMonth(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoValueForNonSubsidiaryByMonth/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoValueForNonSubsidiaryByMonth(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoValueForNonSubsidiaryByMonth(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataForPreviousYears/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, List<SpentAnalysisPojo>>> getPoDataForPreviousYears(@PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<Map<String, List<SpentAnalysisPojo>>>(analysisService.getPoDataForPreviousYears(year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<Map<String, List<SpentAnalysisPojo>>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataForSubsidiaryPreviousYears/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, List<SpentAnalysisPojo>>> getPoDataForSubsidiaryPreviousYears(@PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<Map<String, List<SpentAnalysisPojo>>>(analysisService.getPoDataForSubsidiaryPreviousYears(year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<Map<String, List<SpentAnalysisPojo>>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataForNonSubsidiaryPreviousYears/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, List<SpentAnalysisPojo>>> getPoDataForNonSubsidiaryPreviousYears(@PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<Map<String, List<SpentAnalysisPojo>>>(analysisService.getPoDataForNonSubsidiaryPreviousYears(year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<Map<String, List<SpentAnalysisPojo>>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataBasedOnStatus/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoDataBasedOnStatus(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataBasedOnStatus(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataForSubsidiaryBasedOnStatus/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoDataForSubsidiaryBasedOnStatus(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataForSubsidiaryBasedOnStatus(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataForNonsubsidiaryBasedOnStatus/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoDataForNonsubsidiaryBasedOnStatus(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataForNonsubsidiaryBasedOnStatus(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataBasedOnCategory/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoDataBasedOnCategory(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataBasedOnCategory(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataBasedOnCategoryForSubsidiary/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoDataBasedOnCategoryForSubsidiary(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataBasedOnCategoryForSubsidiary(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataBasedOnCategoryForNonsubsidiary/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoDataBasedOnCategoryForNonsubsidiary(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoDataBasedOnCategoryForNonsubsidiary(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoValueBasedOnCategoryForSubsidiary/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoValueBasedOnCategoryForSubsidiary(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoValueBasedOnCategoryForSubsidiary(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoValueBasedOnCategoryForNonSubsidiary/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoValueBasedOnCategoryForNonSubsidiary(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoValueBasedOnCategoryForNonSubsidiary(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoValueBasedOnCategory/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getPoValueBasedOnCategory(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getPoValueBasedOnCategory(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoDataForInternalAndExternalCo/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojoForInternalAndExternal>> getPoDataForNonSubsidiaryTopSuppliers(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojoForInternalAndExternal>>(analysisService.findPoDataForInternalAndExternalCo(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojoForInternalAndExternal>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getPoCountForInternalAndExternalCo/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojoForInternalAndExternal>> getPoCountForNonSubsidiaryTopSuppliers(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojoForInternalAndExternal>>(analysisService.findPoCountForInternalAndExternalCo(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojoForInternalAndExternal>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getBudgetValue/{costCenter}/{businessUnit}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojoForBudget>> getBudgetValue(@PathVariable(name = "costCenter", required = true) String costCenter, @PathVariable(name = "businessUnit", required = true) String businessUnit, @PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojoForBudget>>(analysisService.getBudgetValue(costCenter, businessUnit, month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojoForBudget>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getTopSuppliersByVolume/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getTopSuppliersByVolume(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getTopSuppliersByVolume(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getTopSuppliersByData/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SpentAnalysisPojo>> getTopSuppliersByData(@PathVariable(name = "month", required = true) int month, @PathVariable(name = "year", required = true) int year) throws ApplicationException {
		try {
			return new ResponseEntity<List<SpentAnalysisPojo>>(analysisService.getTopSuppliersByValue(month, year), HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SpentAnalysisPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
