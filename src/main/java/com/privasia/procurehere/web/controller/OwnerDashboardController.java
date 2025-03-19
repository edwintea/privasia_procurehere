/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.pojo.OwnerDashboardMetricPojo;
import com.privasia.procurehere.core.pojo.OwnerDashboardPojo;
import com.privasia.procurehere.core.pojo.StatisticsPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.YearStatisticsPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.OwnerDashboardService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.CountryEditor;

/**
 * @author VIPUL
 */
@Controller
@RequestMapping(value = "/owner")
public class OwnerDashboardController extends DashboardBase {
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	OwnerDashboardService ownerDashboardService;

	@Autowired
	CountryService countryService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	UserService userService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, "startDate", new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(Country.class, countryEditor);
	}

	@RequestMapping(value = "/extendSession", method = RequestMethod.GET)
	public ResponseEntity<String> extendSession(Model model) {
		try {
			LOG.info("Extend Session requested by : " + SecurityLibrary.getLoggedInUserLoginId());
		} catch (Exception e) {
			LOG.error("Error in session extension : " + e.getMessage(), e);
		}
		return new ResponseEntity<String>("{\"id\" : \"Success\"}", HttpStatus.OK);
	}

	@RequestMapping(value = "/ownerDashboard", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView buyerDashboard(@Valid @ModelAttribute("dashboardObject") OwnerDashboardPojo uiDashboard, Model model, HttpSession session) throws JsonProcessingException {

		OwnerDashboardPojo dashboard = ownerDashboardService.findAllBuyerAndSupplierStatus(uiDashboard.getCountry());

		// TODO no Trail in buyer subscription
		int trailBuyer = 0;// ownerDashboardService.countAllCurrentBuyersOnTrial(uiDashboard.getCountry());

		int totalBuyer = ownerDashboardService.countAllTotalRegisteredBuyers(uiDashboard.getCountry());

		Calendar startTime = Calendar.getInstance();
		startTime.set(Calendar.HOUR_OF_DAY, 0);
		startTime.set(Calendar.MINUTE, 0);
		startTime.set(Calendar.SECOND, 0);

		Calendar endTime = Calendar.getInstance();
		endTime.set(Calendar.HOUR_OF_DAY, 23);
		endTime.set(Calendar.MINUTE, 59);
		endTime.set(Calendar.SECOND, 59);

		Date dateRangeStart = startTime.getTime();
		Calendar cDateRangeEnd = (Calendar) startTime.clone();
		cDateRangeEnd.add(Calendar.DATE, -30);

		model.addAttribute("dateRangeStart", dateRangeStart);

		int regToday = ownerDashboardService.countAllRegisteredBuyersForDateRange(startTime.getTime(), endTime.getTime(), uiDashboard.getCountry());

		Calendar lastDateOfWeek = Calendar.getInstance();

		lastDateOfWeek.add(Calendar.DATE, -7);
		LOG.info("lastDateOfWeek   : " + lastDateOfWeek.getTime());

		int reqLastWeek = ownerDashboardService.countAllRegisteredBuyersForDateRange(lastDateOfWeek.getTime(), new Date(), uiDashboard.getCountry());

		Calendar lastMonth = Calendar.getInstance();
		lastMonth.add(Calendar.MONTH, -1);

		int reqLastMonth = ownerDashboardService.countAllRegisteredBuyersForDateRange(lastMonth.getTime(), new Date(), uiDashboard.getCountry());

		model.addAttribute("trailBuyer", trailBuyer);
		model.addAttribute("currentTime", System.currentTimeMillis());
		model.addAttribute("totalBuyer", totalBuyer);
		model.addAttribute("regToday", regToday);
		model.addAttribute("reqLastWeek", reqLastWeek);
		model.addAttribute("reqLastMonth", reqLastMonth);
		model.addAttribute("countryList", countryService.getAllCountries());
		model.addAttribute("dashboard", dashboard);

		return new ModelAndView("ownerDashboard");
	}

	@RequestMapping(path = "/searchRegisteredBuyers/{regDate}/{regCountry}", method = RequestMethod.POST)
	public ResponseEntity<TableData<Buyer>> searchRegisteredBuyers(@PathVariable("regDate") String regDate, @PathVariable(name = "regCountry", required = false) String regCountry, TableDataInput input) {
		HttpHeaders headers = new HttpHeaders();
		try {
			Country country = null;
			if (StringUtils.checkString(regCountry).length() > 0) {
				country = countryService.getCountryById(regCountry);
			}
			int date = Integer.parseInt(regDate);

			Calendar buyerday = Calendar.getInstance();
			buyerday.add(Calendar.DATE, -date);
			if (regCountry == "dummy") {
				country = null;
			}

			List<Buyer> list = ownerDashboardService.findListOfAllBuyerForDateRange(buyerday.getTime(), new Date(), country, input);
			for (Buyer bu : list) {
				if (bu.getRegistrationOfCountry() != null) {
					bu.getRegistrationOfCountry().setCreatedBy(null);
					bu.getRegistrationOfCountry().setModifiedBy(null);
				}
			}

			TableData<Buyer> data = new TableData<>(list);
			return new ResponseEntity<TableData<Buyer>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Search Buyer: " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("buyer.search.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<TableData<Buyer>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/searchRegisteredSuppliers/{regDate}/{regCountry}", method = RequestMethod.POST)
	public ResponseEntity<TableData<SupplierPojo>> searchRegisteredSuppliers(@PathVariable("regDate") String regDate, @PathVariable(name = "regCountry", required = false) String regCountry) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info(" searchRegisteredSuppliers " + regCountry);
			List<SupplierPojo> supplierList = null;
			Country country = null;
			if (!"dummy".equals(regCountry) && StringUtils.checkString(regCountry).length() > 0) {
				country = countryService.getCountryById(regCountry);
			}

			int date = Integer.parseInt(regDate);
			Calendar supplierday = Calendar.getInstance();
			supplierday.add(Calendar.DATE, -date);
			supplierList = ownerDashboardService.findListOfAllSupplierForDateRange(supplierday.getTime(), new Date(), country);

			TableData<SupplierPojo> data = new TableData<SupplierPojo>(supplierList);
			return new ResponseEntity<TableData<SupplierPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while Search : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("supplier.search.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<TableData<SupplierPojo>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/ownerMetric", method = RequestMethod.POST)
	public ResponseEntity<OwnerDashboardMetricPojo> searchownerMetric(@RequestParam(required = true, value = "startDate") String startDate, @RequestParam(required = true, value = "endDate") String endDate, @RequestParam(required = false, value = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info(" searchownerMetric ");
			if (StringUtils.checkString(startDate).length() == 0 || StringUtils.checkString(endDate).length() == 0) {
				headers.add("error", messageSource.getMessage("owner.search.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
			}
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			OwnerDashboardMetricPojo pojo = new OwnerDashboardMetricPojo();
			Date sDate = (Date) formatter.parse(startDate);
			Date eDate = (Date) formatter.parse(endDate);

			if (startDate != null && endDate != null) {
				sDate = (Date) formatter.parse(startDate);
				eDate = (Date) formatter.parse(endDate);
			}

			// TODO NO Trail For Buyer
			int trailProgress = 0;// ownerDashboardService.countAllCurrentBuyersOnTrial(sDate, eDate, regCountry);
			pojo.setTrailInProgress(trailProgress);

			// TODO NO Trail For Buyer
			int conversionRate = 0; // ownerDashboardService.countAllBuyerConvesionRate(sDate, eDate, regCountry);
			pojo.setConversionRate(conversionRate);

			int suspandendBuyer = ownerDashboardService.countAllCurrentBuyersSuspended(sDate, eDate, regCountry);
			pojo.setSuspendedBuyers(suspandendBuyer);

			int activeBuyer = ownerDashboardService.countAllCurrentBuyersActive(sDate, eDate, regCountry);
			pojo.setActiveBuyers(activeBuyer);

			int newBuyer = ownerDashboardService.countAllNewBuyers(sDate, eDate, regCountry);
			pojo.setNewBuyer(newBuyer);

			int eventCancel = ownerDashboardService.countAllEventCancelled(sDate, eDate, regCountry);
			pojo.setCanceledEvents(eventCancel);

			int eventFinish = ownerDashboardService.countAllEventFinished(sDate, eDate, regCountry);
			pojo.setTotalEvents(eventFinish);

			int totalPr = ownerDashboardService.countAllTotalPr(sDate, eDate, regCountry);
			pojo.setTotalPr(totalPr);

			int totalPo = ownerDashboardService.countAllTotalPo(sDate, eDate, regCountry);
			pojo.setTotalPo(totalPo);

			int totalSupplier = ownerDashboardService.countAllTotalSupplier(sDate, eDate, regCountry);
			pojo.setTotalSuppliers(totalSupplier);

			int totalBuyer = ownerDashboardService.countAllTotalBuyer(sDate, eDate, regCountry);
			pojo.setTotalBuyer(totalBuyer);

			int failedPayment = ownerDashboardService.countAllFailedPaymentsTransaction(sDate, eDate, regCountry);
			pojo.setFailedPaymentTransaction(failedPayment);

			int revenue = ownerDashboardService.countAllRevenueGenerated(sDate, eDate, regCountry);
			pojo.setRevenueGenerated(revenue);

			double eventCategory = ownerDashboardService.countAllEventPerCategory(sDate, eDate, regCountry);
			pojo.setEventPerCategory(eventCategory);

			double averageEvent = ownerDashboardService.countAllAverageTimeEvent(sDate, eDate, regCountry);
			pojo.setAverageTimePerWeek(averageEvent);

			double totalSavings = ownerDashboardService.countRfaTotalAuctionSaving(sDate, eDate, regCountry);
			pojo.setTotalSavings(totalSavings);

			double averageSavings = ownerDashboardService.countRfaAverageAuctionSaving(sDate, eDate, regCountry);
			pojo.setAverageSavings(averageSavings);

			return new ResponseEntity<OwnerDashboardMetricPojo>(pojo, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.search.values", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<OwnerDashboardMetricPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/showBuyerPlan", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> getNewBuyerBreakup(@RequestParam(required = false, value = "startDate") String startDate, @RequestParam(required = false, value = "endDate") String endDate, @RequestParam(required = false, value = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("   getNewBuyerBreakup  ");
		try {
			if (StringUtils.checkString(startDate).length() == 0 || StringUtils.checkString(endDate).length() == 0) {
				headers.add("error", messageSource.getMessage("owner.search.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date sDate = (Date) formatter.parse(startDate);
			Date eDate = (Date) formatter.parse(endDate);
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			if (startDate != null && endDate != null) {
				sDate = (Date) formatter.parse(startDate);
				eDate = (Date) formatter.parse(endDate);
			}

			Map<String, String> data = new HashMap<>();
			data = ownerDashboardService.getAllNewBuyerBreakups(sDate, eDate, regCountry);
			return new ResponseEntity<Map<String, String>>(data, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.newBuyer.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, String>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/showTotalBuyerPlan", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> getTotalBuyerBreakup(@RequestParam(required = false, value = "startDate") String startDate, @RequestParam(required = false, value = "endDate") String endDate, @RequestParam(required = false, value = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("   getTotalBuyerBreakup  ");
		try {
			if (StringUtils.checkString(startDate).length() == 0 || StringUtils.checkString(endDate).length() == 0) {
				LOG.info("Error ");
				headers.add("error", "Please Select Date Range ");
				return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date sDate = (Date) formatter.parse(startDate);
			Date eDate = (Date) formatter.parse(endDate);
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			if (startDate != null && endDate != null) {
				sDate = (Date) formatter.parse(startDate);
				eDate = (Date) formatter.parse(endDate);
			}
			Map<String, String> data = new HashMap<>();
			data = ownerDashboardService.getAllTotalBuyerBreakups(sDate, eDate, regCountry);
			return new ResponseEntity<Map<String, String>>(data, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.totalBuyer.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, String>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/showSuspendedBuyerPlan", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> getSuspendedBuyerBreakup(@RequestParam(required = false, value = "startDate") String startDate, @RequestParam(required = false, value = "endDate") String endDate, @RequestParam(required = false, value = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("   getSuspendedBuyerBreakup  ");
		try {
			if (StringUtils.checkString(startDate).length() == 0 || StringUtils.checkString(endDate).length() == 0) {
				headers.add("error", "Please Select Date Range ");
				return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date sDate = (Date) formatter.parse(startDate);
			Date eDate = (Date) formatter.parse(endDate);
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			if (startDate != null && endDate != null) {
				sDate = (Date) formatter.parse(startDate);
				eDate = (Date) formatter.parse(endDate);
			}
			Map<String, String> data = new HashMap<>();
			data = ownerDashboardService.getSuspendedBuyerBreakups(sDate, eDate, regCountry);
			return new ResponseEntity<Map<String, String>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.suspendBuyer.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, String>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/showRevenuePlan", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> getRevenuePlanBreakup(@RequestParam(required = false, value = "startDate") String startDate, @RequestParam(required = false, value = "endDate") String endDate, @RequestParam(required = false, value = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("   getRevenuePlanBreakup  ");
		try {
			if (StringUtils.checkString(startDate).length() == 0 || StringUtils.checkString(endDate).length() == 0) {
				LOG.info("Error In Date Selection ");
				headers.add("error", "Please Select Date Range ");
				return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date sDate = (Date) formatter.parse(startDate);
			Date eDate = (Date) formatter.parse(endDate);
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			if (startDate != null && endDate != null) {
				sDate = (Date) formatter.parse(startDate);
				eDate = (Date) formatter.parse(endDate);
			}
			Map<String, String> data = new HashMap<>();
			data = ownerDashboardService.getRevenuePlanBreakups(sDate, eDate, regCountry);
			return new ResponseEntity<Map<String, String>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.revenue.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, String>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/showConversionPlan", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> getConversionPlanBreakup(@RequestParam(required = false, value = "startDate") String startDate, @RequestParam(required = false, value = "endDate") String endDate, @RequestParam(required = false, value = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("   getConversionPlanBreakup  ");
		try {
			if (StringUtils.checkString(startDate).length() == 0 || StringUtils.checkString(endDate).length() == 0) {
				headers.add("error", "Please Select Date Range ");
				return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date sDate = (Date) formatter.parse(startDate);
			Date eDate = (Date) formatter.parse(endDate);
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			if (startDate != null && endDate != null) {
				sDate = (Date) formatter.parse(startDate);
				eDate = (Date) formatter.parse(endDate);
			}
			Map<String, String> data = new HashMap<>();

			// TODO NO Conversion
			// data= ownerDashboardService.getConversionPlanBreakups(sDate, eDate, regCountry);
			return new ResponseEntity<Map<String, String>>(data, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.conversion.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, String>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/showTotalAuctionSaving", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> getTotalAuctionSavingBreakup(@RequestParam(required = false, value = "startDate") String startDate, @RequestParam(required = false, value = "endDate") String endDate, @RequestParam(required = false, value = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("   getTotalAuctionSaving  ");
		try {
			if (StringUtils.checkString(startDate).length() == 0 || StringUtils.checkString(endDate).length() == 0) {
				headers.add("error", "Please Select Date Range ");
				return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date sDate = (Date) formatter.parse(startDate);
			Date eDate = (Date) formatter.parse(endDate);
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			if (startDate != null && endDate != null) {
				sDate = (Date) formatter.parse(startDate);
				eDate = (Date) formatter.parse(endDate);
			}
			Map<String, String> data = new HashMap<>();
			data = ownerDashboardService.getTotalAuctionSavingBreakup(sDate, eDate, regCountry);
			return new ResponseEntity<Map<String, String>>(data, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.conversion.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, String>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/showPerWeekEvent", method = RequestMethod.POST)
	public ResponseEntity<Map<String, String>> getPerWeekEventBreakup(@RequestParam(required = false, value = "startDate") String startDate, @RequestParam(required = false, value = "endDate") String endDate, @RequestParam(required = false, value = "country") String country) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("   getPerWeekEventBreakup  ");
		try {
			if (StringUtils.checkString(startDate).length() == 0 || StringUtils.checkString(endDate).length() == 0) {
				headers.add("error", "Please Select Date Range ");
				return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date sDate = (Date) formatter.parse(startDate);
			Date eDate = (Date) formatter.parse(endDate);
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			if (startDate != null && endDate != null) {
				sDate = (Date) formatter.parse(startDate);
				eDate = (Date) formatter.parse(endDate);
			}
			Map<String, String> data = new HashMap<>();
			data = ownerDashboardService.getPerWeekEventBreakups(sDate, eDate, regCountry);
			LOG.info("data" + data);
			return new ResponseEntity<Map<String, String>>(data, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.week.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Map<String, String>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/showRevenueGeneratedWeek", method = RequestMethod.POST)
	public ResponseEntity<List<Map<String, Double>>> getRevenueGeneratedForWeek(@RequestParam(required = false, value = "country") String country) throws ParseException {
		LOG.info("   getRevenueGenerated  ");
		HttpHeaders headers = new HttpHeaders();
		List<Map<String, Double>> data = new ArrayList<Map<String, Double>>();
		try {
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			Map<String, Double> mapData = ownerDashboardService.getBuyerRevenueByWeek(new Date(), 4, regCountry, false);
			for (Map.Entry<String, Double> entry : mapData.entrySet()) {
				Map<String, Double> tmp = new HashMap<String, Double>();
				tmp.put(entry.getKey(), entry.getValue());
				data.add(tmp);
			}

		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.revenueweek.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Map<String, Double>>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Map<String, Double>>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/showRevenueGeneratedMonth", method = RequestMethod.POST)
	public ResponseEntity<List<Map<String, Double>>> getRevenueGeneratedForMonth(@RequestParam(required = false, value = "country") String country) throws ParseException {
		LOG.info("   getRevenueGenerated  ");
		HttpHeaders headers = new HttpHeaders();
		List<Map<String, Double>> data = new ArrayList<Map<String, Double>>();
		try {
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			Map<String, Double> mapMonData = ownerDashboardService.getBuyerRevenueByMonth(new Date(), 6, regCountry, false);
			for (Map.Entry<String, Double> entry : mapMonData.entrySet()) {
				Map<String, Double> tmp = new HashMap<String, Double>();
				tmp.put(entry.getKey(), entry.getValue());
				data.add(tmp);
			}
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.revenuemonth.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Map<String, Double>>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Map<String, Double>>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/showRevenueGeneratedQuarter", method = RequestMethod.POST)
	public ResponseEntity<List<StatisticsPojo>> getRevenueGeneratedForQuarter(@RequestParam(required = false, value = "country") String country) throws ParseException {
		LOG.info("   getRevenueGeneratedForQuarter  ");
		HttpHeaders headers = new HttpHeaders();
		try {
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			List<StatisticsPojo> data = ownerDashboardService.getBuyerRevenueForQuaterly(new Date(), 4, regCountry, false);
			return new ResponseEntity<List<StatisticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.revenuequarter.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<StatisticsPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/showRevenueGeneratedYear", method = RequestMethod.POST)
	public ResponseEntity<List<YearStatisticsPojo>> getRevenueGeneratedForYear(@RequestParam(required = false, value = "country") String country) throws ParseException {
		LOG.info("   getRevenueGeneratedForYear  ");
		HttpHeaders headers = new HttpHeaders();
		try {
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			List<YearStatisticsPojo> data = ownerDashboardService.getBuyerRevenueHalfYearly(new Date(), 5, regCountry, false);
			return new ResponseEntity<List<YearStatisticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.revenueyear.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<YearStatisticsPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/showSubscriptionGeneratedWeek", method = RequestMethod.POST)
	public ResponseEntity<List<Map<String, Double>>> getSubscriptionGeneratedWeek(@RequestParam(required = false, value = "country") String country) throws ParseException {
		LOG.info("   getSubscriptionGeneratedWeek  ");
		HttpHeaders headers = new HttpHeaders();
		List<Map<String, Double>> data = new ArrayList<Map<String, Double>>();
		try {
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			Map<String, Double> mapData = ownerDashboardService.getBuyerSubscriptionByWeek(new Date(), 4, regCountry, false);
			for (Map.Entry<String, Double> entry : mapData.entrySet()) {
				Map<String, Double> tmp = new HashMap<String, Double>();
				tmp.put(entry.getKey(), entry.getValue());
				data.add(tmp);
			}

		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.subscriptionweek.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Map<String, Double>>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Map<String, Double>>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/showSubscriptionGeneratedMonth", method = RequestMethod.POST)
	public ResponseEntity<List<Map<String, Double>>> getSubscriptionGeneratedForMonth(@RequestParam(required = false, value = "country") String country) throws ParseException {
		LOG.info("   getSubscriptionGeneratedForMonth  ");
		HttpHeaders headers = new HttpHeaders();
		List<Map<String, Double>> data = new ArrayList<Map<String, Double>>();
		try {
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			Map<String, Double> mapMonData = ownerDashboardService.getBuyerSubscriptionByMonth(new Date(), 6, regCountry, false);
			for (Map.Entry<String, Double> entry : mapMonData.entrySet()) {
				Map<String, Double> tmp = new HashMap<String, Double>();
				tmp.put(entry.getKey(), entry.getValue());
				data.add(tmp);
			}
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.subscriptionmonth.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Map<String, Double>>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Map<String, Double>>>(data, HttpStatus.OK);
	}

	@RequestMapping(value = "/showSubscriptionGeneratedQuarter", method = RequestMethod.POST)
	public ResponseEntity<List<StatisticsPojo>> getSubscriptionGeneratedForQuarter(@RequestParam(required = false, value = "country") String country) throws ParseException {
		LOG.info("   getRevenueGeneratedForQuarter  ");
		HttpHeaders headers = new HttpHeaders();
		try {
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			List<StatisticsPojo> data = ownerDashboardService.getBuyerSubscriptionForQuaterly(new Date(), 4, regCountry, false);
			return new ResponseEntity<List<StatisticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.subscriptionquarter.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<StatisticsPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/showSubscriptionGeneratedYear", method = RequestMethod.POST)
	public ResponseEntity<List<YearStatisticsPojo>> getSubscriptionGeneratedForYear(@RequestParam(required = false, value = "country") String country) throws ParseException {
		LOG.info("   getRevenueGeneratedForYear  ");
		HttpHeaders headers = new HttpHeaders();
		try {
			Country regCountry = null;
			if (StringUtils.checkString(country).length() > 0) {
				regCountry = countryService.getCountryById(country);
			}
			List<YearStatisticsPojo> data = ownerDashboardService.getBuyerSubscriptionHalfYearly(new Date(), 5, regCountry, false);
			return new ResponseEntity<List<YearStatisticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("owner.subscriptionyear.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<YearStatisticsPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
