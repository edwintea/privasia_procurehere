/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.Plan;
import com.privasia.procurehere.core.entity.SupplierPlan;
import com.privasia.procurehere.core.enums.PlanStatus;
import com.privasia.procurehere.core.pojo.PublicEventPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.PlanService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.SubscriptionService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.web.editors.BuyerEditor;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;

import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Nitin Otageri
 */
@ApiIgnore
@Controller
@RequestMapping(value = "/integration")
public class IntegrationController {

	private static final Logger LOG = LogManager.getLogger(Global.INTEGRATION_LOG);

	@Autowired
	PlanService planService;

	@Autowired
	SupplierPlanService supplierPlanService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	BuyerEditor buyerEditor;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	CountryService countryService;

	@Autowired
	BuyerService buyerService;

	@Resource
	MessageSource messageSource;

	@Value("${app.url}")
	String appUrl;

	@Autowired
	RftEventService rftEventService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(Buyer.class, buyerEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(Country.class, countryEditor);
	}

	@RequestMapping(path = "/buyerPlanList", method = RequestMethod.GET)
	public ResponseEntity<List<Plan>> planList() {
		HttpHeaders headers = new HttpHeaders();
		List<Plan> planList = planService.findAllPlansByStatusForIntegration(PlanStatus.ACTIVE);
		return new ResponseEntity<List<Plan>>(planList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/supplierPlanList", method = RequestMethod.GET)
	public ResponseEntity<List<SupplierPlan>> supplierPlanList() {
		HttpHeaders headers = new HttpHeaders();
		List<SupplierPlan> planList = supplierPlanService.findAllPlansByStatusForIntegration(PlanStatus.ACTIVE);
		return new ResponseEntity<List<SupplierPlan>>(planList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = { "/publicEventList/{country}/{buyer}", "/publicEventList/{country}", "/publicEventList" }, method = RequestMethod.GET)
	public ResponseEntity<List<PublicEventPojo>> publicEvents(@PathVariable("country") Optional<String> country, @PathVariable("buyer") Optional<String> buyer) {
		HttpHeaders headers = new HttpHeaders();
		Country selectedCountry = null;
		Buyer selectedBuyer = null;
		if (country.isPresent()) {
			// fetch country;
			// selectedCountry = fetch from DB based on ID
			try {
				selectedCountry = countryService.getCountryById(country.get());
			} catch (Exception e) {
				LOG.error("No country found by id : " + country.get() + ". Error : " + e.getMessage(), e);
			}
		}
		if (buyer.isPresent()) {
			try {
				selectedBuyer = buyerService.findPlainBuyerById(buyer.get());
			} catch (Exception e) {
				LOG.error("No Buyer found by id : " + buyer.get() + ". Error : " + e.getMessage(), e);
			}
		}
		List<PublicEventPojo> activeEvents = rftEventService.getActivePublicEventsForIntegration(selectedCountry, selectedBuyer);
		if (CollectionUtil.isNotEmpty(activeEvents)) {
			LOG.info("Found " + activeEvents.size() + " public events...");
		}
		return new ResponseEntity<List<PublicEventPojo>>(activeEvents, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/countryList", method = RequestMethod.GET)
	public ResponseEntity<List<Country>> countryList() {
		HttpHeaders headers = new HttpHeaders();
		List<Country> countryList = countryService.getActiveCountriesForIntegration();
		return new ResponseEntity<List<Country>>(countryList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/buyerList", method = RequestMethod.GET)
	public ResponseEntity<List<Buyer>> buyerList() {
		HttpHeaders headers = new HttpHeaders();
		List<Buyer> buyerList = rftEventService.getAllActiveBuyersForIntegration();
		return new ResponseEntity<List<Buyer>>(buyerList, headers, HttpStatus.OK);
	}

}
