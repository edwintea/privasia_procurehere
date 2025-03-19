/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.CurrencyDao;
import com.privasia.procurehere.core.dao.SupplierAuditTrailDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.entity.SupplierProjects;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.pojo.Coverage.CoverageType;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CompanyStatusService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.NaicsCodesService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.CompanyStatusEditor;
import com.privasia.procurehere.web.editors.CountryCodeEditor;
import com.privasia.procurehere.web.editors.CoverageEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.NaicsCodesEditor;
import com.privasia.procurehere.web.editors.RegionEditor;
import com.privasia.procurehere.web.editors.StateEditor;

/**
 * @author Vipul
 */
@Controller
@RequestMapping(value = "/supplier")
public class SupplierProfileController implements Serializable {

	private static final long serialVersionUID = -3511236085076614718L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	// private static final String SESSION_ATTRIBUTE_OTHER_CRED_LIST =
	// "otherCredList";
	// private static final String SESSION_ATTRIBUTE_COMPANY_PRO =
	// "uploadCompnayDetails";

	// private Validator validator =
	// Validation.buildDefaultValidatorFactory().getValidator();

	// @Autowired
	// SmartValidator validator;

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	SupplierService supplierService;

	@Autowired
	CountryService countryService;

	@Autowired
	CountryCodeEditor countryEditor;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	RegionEditor regionEditor;

	@Autowired
	StateEditor stateEditor;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CompanyStatusEditor companyStatusEditor;

	@Autowired
	CoverageEditor coverageEditor;

	@Autowired
	NaicsCodesService industryCategoryService;

	@Autowired
	CompanyStatusService companyStatusService;

	@Autowired
	StateService stateService;

	@Autowired
	NaicsCodesEditor industryCategoryEditor;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	UserService userService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	SupplierAuditTrailDao supplierAuditTrailDao;

	@Autowired
	CurrencyDao currencyDao;

	@Autowired
	private Environment env;

	@ModelAttribute("registeredCountry")
	public List<Country> populateCountries() {
		return countryService.findAllActiveCountries();
	}

	@ModelAttribute("companyStatusList")
	public List<CompanyStatus> populateCompanyStatus() {
		return companyStatusService.getAllComapnyStatus();
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	/**
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Coverage.class, coverageEditor);
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(CompanyStatus.class, companyStatusEditor);
		binder.registerCustomEditor(State.class, stateEditor);
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(NaicsCodes.class, industryCategoryEditor);
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(Date.class, "registrationDate", new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy HH:mm a z"), true));

		binder.registerCustomEditor(List.class, "industryCategories", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					NaicsCodes group = industryCategoryService.getNaicsCodesById(id);
					return group;
				}
				return null;
			}
		});

		binder.registerCustomEditor(List.class, "projectIndustries", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					NaicsCodes group = industryCategoryService.getNaicsCodesById(id);
					return group;
				}
				return null;
			}
		});

		binder.registerCustomEditor(List.class, "coverages", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					Coverage coverage = null;
					String id = (String) element;
					Country country = countryService.getCountryById(id);
					if (country != null) {
						coverage = new Coverage(country);
					} else {
						State state = stateService.getState(id);
						coverage = new Coverage(state);
					}
					return coverage;
				}
				return null;
			}
		});
	}

	@RequestMapping(value = "findStates", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Coverage>> findStates(@RequestParam("countryId") String countryId) {
		List<Coverage> list = new ArrayList<Coverage>();
		try {
			LOG.info("Country " + countryId);
			if (StringUtils.checkString(countryId).length() > 0) {
				List<State> stateList = stateService.statesForCountry(countryId);
				if (CollectionUtil.isNotEmpty(stateList)) {
					list = new ArrayList<Coverage>();
					for (State state : stateList) {
						Coverage coverage = new Coverage(state);
						list.add(coverage);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error while getting states for Country " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.country.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Coverage>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Coverage>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/supplierProfileDetails", method = RequestMethod.GET)
	public String supplierProfileView(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, Model model) {
		LOG.info("Request..............................................." + projectId);
		Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());

		if (supplier == null) {
			LOG.error("Redirecting the user to login page as we could not identify the Supplier based on session stored TENANT ID ****************");
			return "/login?error=true";
		}
		model.addAttribute("isDeclared", supplier.getDeclaration());
		// model.addAttribute("otherCredList",
		// supplier.getSupplierOtherCredentials());
		// model.addAttribute("uploadCompnayDetails",
		// supplier.getSupplierCompanyProfile());
		model.addAttribute("states", stateService.findAllActiveStatesForCountry(supplier.getRegistrationOfCountry().getId()));
		model.addAttribute("supplier", supplier);

		model.addAttribute("supplierProfileInfo", true);

		supplierService.checkIfProfileIsComplete(model, supplier);
		LOG.info("Request...............................................End ");
		return "supplierProfileInfo";
	}

	@RequestMapping(path = "/supplierProfileCategory", method = RequestMethod.GET)
	public String supplierProfileCategory(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, Model model) {
		LOG.info("Request...............................................");
		Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());

		if (supplier == null) {
			LOG.error("Redirecting the user to login page as we could not identify the Supplier based on session stored TENANT ID ****************");
			return "/login?error=true";
		}
		if (StringUtils.checkString(projectId).length() == 0) {
			List<NaicsCodes> codes = industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null);
			model.addAttribute("categories", codes);
			model.addAttribute("coverageCountry", supplierService.doSearchCoverage(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null));

		}

		// PH-1098
		supplierService.checkIfProfileIsComplete(model, supplier);
		model.addAttribute("supplier", supplier);
		model.addAttribute("supplierProfileCategory", true);

		LOG.info("Request...............................................End ");
		return "supplierProfileCategory";
	}

	@RequestMapping(path = "/suppCompProfileAttachments", method = RequestMethod.GET)
	public String suppCompProfileAttachments(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, Model model) {
		LOG.info("Request...............................................");
		Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());

		if (supplier == null) {
			LOG.error("Redirecting the user to login page as we could not identify the Supplier based on session stored TENANT ID ****************");
			return "/login?error=true";
		}

		model.addAttribute("isDeclared", supplier.getDeclaration());
		model.addAttribute("otherCredList", supplier.getSupplierOtherCredentials());
		model.addAttribute("uploadCompnayDetails", supplier.getSupplierCompanyProfile());
		model.addAttribute("supplier", supplier);
		model.addAttribute("suppCompProfileAttachments", true);
		// PH-1098
		supplierService.checkIfProfileIsComplete(model, supplier);
		LOG.info("Request...............................................End ");

		return "suppCompProfileAttachments";
	}

	@RequestMapping(path = "/suppAddEditTrackRecord", method = RequestMethod.GET)
	public String suppAddEditTrackRecord(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, Model model) {
		LOG.info("Request...............................................");
		Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());

		if (supplier == null) {
			LOG.error("Redirecting the user to login page as we could not identify the Supplier based on session stored TENANT ID ****************");
			return "/login?error=true";
		}
		if (StringUtils.checkString(projectId).length() == 0) {
			model.addAttribute("categories", industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null));
			model.addAttribute("coverageCountry", supplierService.doSearchCoverage(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null));
		}

		model.addAttribute("projects", supplierService.findProjectsForSupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("isDeclared", supplier.getDeclaration());
		model.addAttribute("supplier", supplier);
		model.addAttribute("suppAddEditTrackRecord", true);
		SupplierProjects project = null;
		if (StringUtils.checkString(projectId).length() > 0) {
			project = supplierService.findBySupplierId(projectId);
			project.setSupplierId(supplier.getId());
			model.addAttribute("registeredTrackCountry", supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, null, projectId, null, null));
			model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, projectId, null, null));
		} else {
			project = new SupplierProjects();
			project.setSupplierId(supplier.getId());
			model.addAttribute("registeredTrackCountry", supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, null, null, null, null));
			model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, null, null, null));
		}
		// model.addAttribute("step", step);
		model.addAttribute("supplierProject", project);
		model.addAttribute("currency", currencyService.getAllCurrency());
		// PH-1098
		supplierService.checkIfProfileIsComplete(model, supplier);
		LOG.info("Request...............................................End ");

		return "suppAddEditTrackRecord";
	}

	@RequestMapping(path = "/supplierProfileInfo", method = RequestMethod.POST)
	public ModelAndView updateSupplierProfile(@Valid @ModelAttribute(name = "supplier") Supplier supplier, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("supplier Settings create Called with update Supplier Profile");
		String message = "";
		String messageForAuditTrail = "";
		User loggedInUser = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			Supplier persistObject = supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
			if ((!persistObject.getCompanyName().equals(supplier.getCompanyName())) && (!persistObject.getCompanyRegistrationNumber().equals(supplier.getCompanyRegistrationNumber()))) {
				// message = "The company name " + persistObject.getCompanyName() + " is changed
				// as " +
				// supplier.getCompanyName() + " and Company Registration No " +
				// persistObject.getCompanyRegistrationNumber() + " is changed as " +
				// supplier.getCompanyRegistrationNumber();
				message = "This e-mail is sent to notify you that supplier " + persistObject.getCompanyName() + " has changed their Company Name to " + supplier.getCompanyName() + " and Company Registration Number from " + persistObject.getCompanyRegistrationNumber() + " to " + supplier.getCompanyRegistrationNumber() + ".";
				messageForAuditTrail = "User updated Company Name from " + persistObject.getCompanyName() + " to " + supplier.getCompanyName() + " and Company Registration Number from " + persistObject.getCompanyRegistrationNumber() + " to " + supplier.getCompanyRegistrationNumber() + ".";
				LOG.info(message);

			} else if (!persistObject.getCompanyName().equals(supplier.getCompanyName())) {
				// message = "The company name " + persistObject.getCompanyName() + " is changed
				// as " +
				// supplier.getCompanyName();
				message = "This e-mail is sent to notify you that supplier " + persistObject.getCompanyName() + " has changed their Company Name to " + supplier.getCompanyName() + ".";
				messageForAuditTrail = "User updated Company Name from " + persistObject.getCompanyName() + " to " + supplier.getCompanyName() + ".";
				LOG.info(message);

			} else if (!persistObject.getCompanyRegistrationNumber().equals(supplier.getCompanyRegistrationNumber())) {
				// message = "The Company Registration No " +
				// persistObject.getCompanyRegistrationNumber() + " is
				// changed as " + supplier.getCompanyRegistrationNumber() +" for company
				// "+'"'+supplier.getCompanyName()+'"';
				message = "This e-mail is sent to notify you that supplier " + supplier.getCompanyName() + " has changed their Company Registration Number from " + persistObject.getCompanyRegistrationNumber() + " to " + supplier.getCompanyRegistrationNumber() + ".";
				messageForAuditTrail = "User updated Company Registration Number from " + persistObject.getCompanyRegistrationNumber() + " to " + supplier.getCompanyRegistrationNumber() + ".";
				LOG.info(message);
			}
			try {
				if (StringUtils.checkString(message).length() > 0) {
					List<Buyer> buyerList = persistObject.getAssociatedBuyers();
					if (CollectionUtil.isNotEmpty(buyerList)) {
						for (Buyer mailTo : buyerList) {
							User user = userService.getDetailsOfLoggedinUser(mailTo.getLoginEmail());
							if(user.getEmailNotifications())
							sendCompanyNameOrRegistrationNoForUser(mailTo.getFullName(), mailTo.getId(), mailTo.getCommunicationEmail(), mailTo.getLoginEmail(), message);
						}
					}

					User mailTo = userService.getUserByLoginId("admin@procurehere.com");
					if (mailTo != null && mailTo.getEmailNotifications()) {
						sendCompanyNameOrRegistrationNoForUser(mailTo.getName(), mailTo.getId(), mailTo.getCommunicationEmail(), mailTo.getLoginId(), message);
					}
					supplierService.saveAuitTrail(messageForAuditTrail, loggedInUser);
				}
			} catch (Exception e) {
				LOG.error("Error while sending mail for changed company name and registration no " + e.getMessage(), e);
			}
			
//			PH-1937 Suppliers will no longer be allowed to change the Company Name and Registration Number from the Supplier 
			persistObject.setCompanyName(supplier.getCompanyName());
			//persistObject.setCompanyRegistrationNumber(supplier.getCompanyRegistrationNumber());
			
			persistObject.setYearOfEstablished(supplier.getYearOfEstablished());
			persistObject.setCompanyContactNumber(supplier.getCompanyContactNumber());
			persistObject.setFaxNumber(supplier.getFaxNumber());
			persistObject.setCompanyWebsite(supplier.getCompanyWebsite());
			persistObject.setCommunicationEmail(supplier.getCommunicationEmail());
			persistObject.setCompanyStatus(supplier.getCompanyStatus());
			persistObject.setLine1(supplier.getLine1());
			persistObject.setLine2(supplier.getLine2());
			persistObject.setCity(supplier.getCity());
			persistObject.setState(supplier.getState());
			persistObject.setPostalCode(supplier.getPostalCode());
			persistObject.setMobileNumber(supplier.getMobileNumber());
			persistObject.setTaxRegistrationNumber(supplier.getTaxRegistrationNumber());
			persistObject.setCurrency(supplier.getCurrency());
			persistObject.setPaidUpCapital(supplier.getPaidUpCapital());
			persistObject.setDesignation(supplier.getDesignation());
			supplierService.updateSupplier(persistObject);
			User adminUser = userService.findUserByLoginId(persistObject.getLoginEmail());
			if (adminUser != null) {
				adminUser.setCommunicationEmail(persistObject.getCommunicationEmail());
				userService.updateUser(adminUser);
			}

		} catch (Exception e) {
			LOG.error("Error while updating supplier profile " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.supplierinfo", new Object[] {}, Global.LOCALE));
			return new ModelAndView("redirect:/supplier/supplierProfileDetails");
		}

		redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.info.updated", new Object[] {}, Global.LOCALE));
		return new ModelAndView("redirect:/supplier/supplierProfileDetails");
	}

	/**
	 * @param name
	 * @param id
	 * @param couminactionEmail
	 * @param loginId
	 * @param message
	 */
	private void sendCompanyNameOrRegistrationNoForUser(String name, String id, String couminactionEmail, String loginId, String message) {
		LOG.info("Sending Approval email to (" + name + ") : " + couminactionEmail);
		String subject = "Company Name and Registration No changed";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userName", name);
		map.put("message", message);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		String timeZone = "GMT+8:00";
		timeZone = getTimeZoneByBuyerSettings(id, timeZone);
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("loginUrl", APP_URL + "/login");

		if (StringUtils.checkString(couminactionEmail).length() > 0) {
			sendEmail(couminactionEmail, subject, map, Global.COMPANY_CHANGED_TEMPLATE);
		} else {
			LOG.warn("No communication email configured for user : " + loginId + "... Not going to send email notification");
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

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	@RequestMapping(path = "/supplierProductCategory", method = RequestMethod.POST)
	public ModelAndView supplierProductCategory(@Valid @ModelAttribute(name = "supplier") Supplier supplier, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("supplier Settings create Called with supplier Product Category");

		List<Country> countries = new ArrayList<Country>();
		List<State> states = new ArrayList<State>();

		try {
			Supplier persistObject = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("*****supplier.getNaicsCodes()******" + supplier.getNaicsCodes());
			persistObject.setNaicsCodes(supplier.getNaicsCodes());

			int selectedCount = 0;
			if (CollectionUtil.isNotEmpty(supplier.getNaicsCodes())) {
				for (NaicsCodes level1 : supplier.getNaicsCodes()) {
					int length = String.valueOf(level1.getCategoryCode()).length();
					if (length == 6) {
						selectedCount++;
					}
				}
			}
			List<Coverage> coverages = supplier.getCoverages();
			if (CollectionUtil.isNotEmpty(coverages)) {
				for (Coverage coverage : coverages) {
					LOG.info("   coverage.getType()  " + coverage.getType());
					if (coverage.getType() == CoverageType.COUNTRY) {
						LOG.info("COUNTRY : " + coverage.getName());
						Country country = new Country();
						country.setCountryCode(coverage.getCode());
						country.setId(coverage.getId());
						country.setCountryName(coverage.getName());
						countries.add(country);
					} else {
						LOG.info("STATE : " + coverage.getName());
						State state = new State();
						state.setStateCode(coverage.getCode());
						state.setId(coverage.getId());
						state.setStateName(coverage.getName());
						states.add(state);
					}
				}
				persistObject.setCountries(countries);
				persistObject.setStates(states);
			}
			persistObject.setCoverages(supplier.getCoverages());

			supplierService.updateSupplier(persistObject);
			LOG.info("***** selectedCount ******" + selectedCount);
			if (selectedCount > 25) {
				model.addAttribute("showValidationCheckFlag", true);
				LOG.info("Error while updating NRIC CODES - Please select a max of 25 NRIC CODES.");
				return new ModelAndView("redirect:/supplier/supplierProfileCategory");
			}
			// redir.addFlashAttribute("success", "Naics Codes/Coverage Updated");
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.naicscode.updated", new Object[] {}, Global.LOCALE));

		} catch (Exception e) {
			LOG.error("Error while updating Naics Codes/Coverage " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.niacs", new Object[] {}, Global.LOCALE));
		}

		return new ModelAndView("redirect:/supplier/supplierProfileCategory");
	}

	@RequestMapping(value = "/editSupplierTrackRecordOld/{projId}", method = RequestMethod.POST)
	public ResponseEntity<List<SupplierProjects>> editSupplierTrackRecordOld(@ModelAttribute("supplierProject") SupplierProjects supplierProject, @PathVariable String projId, BindingResult result, Model model) {
		LOG.info("Edit SupplierTrackRecord " + supplierProject.getSupplierId() + " projId " + projId);
		try {

			SupplierProjects persistObject = supplierService.findBySupplierId(projId);
			// persistObject.setId(projId);
			persistObject.setProjectName(supplierProject.getProjectName());
			persistObject.setClientName(supplierProject.getClientName());
			persistObject.setContactValue(supplierProject.getContactValue());
			persistObject.setProjectIndustries(supplierProject.getProjectIndustries());
			persistObject.setProjectName(supplierProject.getProjectName());
			persistObject.setYear(supplierProject.getYear());
			persistObject.setCurrency(supplierProject.getCurrency());
			// persistObject.setSupplier(supplierProject.getSupplier());
			persistObject.setSupplierId(supplierProject.getSupplierId());
			persistObject.setTracRecordCoverages(supplierProject.getTracRecordCoverages());

			supplierService.updateSupplierProject(persistObject);
			LOG.info("updating Supplier Projecr.... ");

			List<SupplierProjects> list = null;
			list = supplierService.findProjectsForSupplierId(supplierProject.getSupplierId());

			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "Supplier Track Record Updated");
			return new ResponseEntity<List<SupplierProjects>>(list, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while storing track records : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while storing track records");
			return new ResponseEntity<List<SupplierProjects>>(null, headers, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/editSupplierTrackRecord", method = RequestMethod.POST)
	public ModelAndView editSupplierTrackRecord(@ModelAttribute("supplierProject") SupplierProjects supplierProject, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("Edit SupplierTrackRecord " + supplierProject.getSupplierId() + " projId " + supplierProject.getId());
		String id = null;
		try {
			SupplierProjects persistObject = null;
			if (StringUtils.checkString(supplierProject.getId()).length() > 0) {
				persistObject = supplierService.findBySupplierId(supplierProject.getId());
				persistObject.setProjectName(supplierProject.getProjectName());
				persistObject.setClientName(supplierProject.getClientName());
				persistObject.setContactValue(supplierProject.getContactValue());
				persistObject.setProjectIndustries(supplierProject.getProjectIndustries());
				persistObject.setProjectName(supplierProject.getProjectName());
				persistObject.setYear(supplierProject.getYear());
				persistObject.setCurrency(supplierProject.getCurrency());
				// persistObject.setSupplier(supplierProject.getSupplier());
				persistObject.setSupplierId(supplierProject.getSupplierId());
				persistObject.setTracRecordCoverages(supplierProject.getTracRecordCoverages());

				supplierService.updateSupplierProject(persistObject);

				id = persistObject.getId();
				LOG.info("updating Supplier Projecr.... ");
			} else {
				SupplierProjects projects2 = new SupplierProjects();
				projects2.setClientEmail(supplierProject.getClientEmail());
				projects2.setClientName(supplierProject.getClientName());
				projects2.setContactValue(supplierProject.getContactValue());
				projects2.setProjectIndustries(supplierProject.getProjectIndustries());
				projects2.setProjectName(supplierProject.getProjectName());
				projects2.setYear(supplierProject.getYear());
				projects2.setCurrency(supplierProject.getCurrency());
				projects2.setTracRecordCoverages(supplierProject.getTracRecordCoverages());
				projects2.setSupplier(supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId()));

				projects2 = supplierService.saveSupplierProject(projects2);

				id = projects2.getId();
			}

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.trackrecord.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while storing track records : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.storing.trackrecord", new Object[] {}, Global.LOCALE));
		}
		model.addAttribute("suppAddEditTrackRecord", true);
		// model.addAttribute("flag", Boolean.TRUE);

		// return new ModelAndView("redirect:/supplier/suppAddEditTrackRecord");
		return new ModelAndView("redirect:/supplier/updateSupplierProfileTrackRecord/" + id);
	}

	@RequestMapping(value = "/supplierTrackAddProfile", method = RequestMethod.GET)
	public ResponseEntity<List<NaicsCodes>> supplierTrackAddProfile() {
		LOG.info(" UpdateSupplierTrackRecord ");
		try {

			// Supplier supplier =
			// supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());

			LOG.info("     supplierRegistrationView   supplierProfile  ");

			List<NaicsCodes> ic = industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE, null, null, null, null);

			LOG.info("     industryCategoryService  " + ic.size());

			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.project.trackupload", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<NaicsCodes>>(ic, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while storing track records : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.project.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<NaicsCodes>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/addSupplierProfileNewTrack", method = RequestMethod.GET)
	public ModelAndView addSupplierProfileNewTrack(@Valid @ModelAttribute(name = "supplier") Supplier supplier, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("addSupplierProfileNewTrack  ");
		Boolean flag = Boolean.FALSE;
		LOG.info(" Supplier Track Desc:" + supplier.getSupplierTrackDesc());
		if (supplier.getSupplierTrackDesc() != null) {
			Supplier persistObj = supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
			persistObj.setSupplierTrackDesc(supplier.getSupplierTrackDesc());
			supplierService.updateSupplier(persistObj);

			flag = Boolean.TRUE;
		}
		Supplier supplierObj = supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplierObj);
		model.addAttribute("projects", supplierService.findProjectsForSupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("registeredTrackCountry", supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, supplierObj.getId(), null, null, null));
		model.addAttribute("currency", currencyService.getAllActiveCurrencies());
		model.addAttribute("supplierProject", new SupplierProjects());
		model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, null, null, null));
		model.addAttribute("flag", flag);
		model.addAttribute("suppAddEditTrackRecord", true);
		return new ModelAndView("suppAddEditTrackRecord");
	}

	@RequestMapping(path = "/updateSupplierProfileTrack", method = RequestMethod.GET)
	public ModelAndView updateSupplierProfileTrack(@Valid @ModelAttribute(name = "supplier") Supplier supplier, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("addSupplierProfileNewTrack  ");
		Boolean flag = Boolean.FALSE;
		LOG.info(" Supplier Track Desc:" + supplier.getSupplierTrackDesc());
		if (supplier.getSupplierTrackDesc() != null) {
			Supplier persistObj = supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
			persistObj.setSupplierTrackDesc(supplier.getSupplierTrackDesc());
			supplierService.updateSupplier(persistObj);
		}
		Supplier supplierObj = supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplierObj);
		model.addAttribute("projects", supplierService.findProjectsForSupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("registeredTrackCountry", supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, supplierObj.getId(), null, null, null));
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("supplierProject", new SupplierProjects());
		model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, null, null, null));
		model.addAttribute("flag", flag);
		model.addAttribute("suppAddEditTrackRecord", true);
		return new ModelAndView("suppAddEditTrackRecord");
	}

	@RequestMapping(path = "/updateSupplierProfileTrackRecord/{editId}", method = RequestMethod.GET)
	public ModelAndView updateSupplierProfileTrackRecord(@PathVariable String editId, Model model) {
		LOG.info("addSupplierProfileNewTrack  " + editId);
		Supplier supplierObj = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("flag", Boolean.TRUE);
		model.addAttribute("supplier", supplierObj);
		model.addAttribute("projects", supplierService.findProjectsForSupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("uploadCompnayDetails", supplierObj.getSupplierCompanyProfile());
		model.addAttribute("otherCredList", supplierObj.getSupplierOtherCredentials());
		model.addAttribute("currency", currencyService.getAllCurrency());

		SupplierProjects project = null;
		if (StringUtils.checkString(editId).length() > 0) {
			project = supplierService.findBySupplierId(editId);
			project.setSupplierId(supplierObj.getId());
			model.addAttribute("registeredTrackCountry", supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, null, editId, null, null));
			model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, editId, null, null));
		}
		model.addAttribute("supplierProject", project);

		model.addAttribute("categories", industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE, supplierObj.getId(), null, null, null));
		model.addAttribute("coverageCountry", supplierService.doSearchCoverage(Global.SUPPLIER_COVERAGE, supplierObj.getId(), null, null, null));
		model.addAttribute("suppAddEditTrackRecord", true);
		return new ModelAndView("suppAddEditTrackRecord");
	}

	@RequestMapping(value = "/removeSupplierProfileTrackRecord/{projectId}", method = RequestMethod.GET)
	public String updateSupplierProfileTrackRecord(@PathVariable("projectId") String projectId, RedirectAttributes redir, Model model) {
		SupplierProjects persistObject = null;
		try {
			persistObject = supplierService.findBySupplierId(projectId);
			if (StringUtils.checkString(projectId).length() > 0) {
				supplierService.removeTrackProject(projectId);
			}

			redir.addFlashAttribute("success", messageSource.getMessage("supplier.project.remove", new Object[] { persistObject.getProjectName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Error while removing Track Project  :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.removetrack.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		model.addAttribute("suppAddEditTrackRecord", true);
		return "redirect:/supplier/suppAddEditTrackRecord";

	}

	@RequestMapping(value = "searchCategory", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<NaicsCodes>> searchCategory(@RequestParam("search") String search, @RequestParam("activeTab") String activeTab, @RequestParam("supplierId") String supplierId, @RequestParam("projectId") String projectId, @RequestParam(value = "checkedAlredy[]") String[] checkedAlredy) {
		List<NaicsCodes> list = null;
		try {
			LOG.info("activeTab : " + activeTab + " supplierId : " + supplierId + " projectId : " + projectId + " checkedAlredy : " + checkedAlredy + " search : " + search);
			list = industryCategoryService.searchForCategoriesForSupplierProfile(activeTab, supplierId, projectId, checkedAlredy, search);
		} catch (Exception e) {
			LOG.error("Error while Searching IndustryCategory " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.industrysearch.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<NaicsCodes>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (CollectionUtil.isNotEmpty(list)) {
			for (NaicsCodes codes : list) {
				codes.setCreatedBy(null);
				codes.setModifiedBy(null);

			}
		}
		return new ResponseEntity<List<NaicsCodes>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "findChildIndustry", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<NaicsCodes>> findChildIndustry(@RequestBody SearchVo searchVo) {
		List<NaicsCodes> list = new ArrayList<NaicsCodes>();
		LOG.info("Start : ");
		try {
			if (StringUtils.checkString(searchVo.getId()).length() > 0) {

				list = industryCategoryService.findChildForId(searchVo);

			}
		} catch (Exception e) {
			LOG.error("Error while getting child industires " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.child.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<NaicsCodes>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (CollectionUtil.isNotEmpty(list)) {
			for (NaicsCodes codes : list) {
				codes.setCreatedBy(null);
				codes.setModifiedBy(null);
				if (CollectionUtil.isNotEmpty(codes.getChildren())) {
					for (NaicsCodes code : codes.getChildren()) {
						code.setCreatedBy(null);
						code.setModifiedBy(null);
						if (CollectionUtil.isNotEmpty(code.getChildren())) {
							for (NaicsCodes cod : code.getChildren()) {
								cod.setCreatedBy(null);
								cod.setModifiedBy(null);
								if (CollectionUtil.isNotEmpty(cod.getChildren())) {
									for (NaicsCodes cd : cod.getChildren()) {
										cd.setCreatedBy(null);
										cd.setModifiedBy(null);
									}
								}
							}
						}
					}
				}
			}
		}
		LOG.info("End : ");
		return new ResponseEntity<List<NaicsCodes>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/supplierTrackRecord", method = RequestMethod.GET)
	public void supplierTrackRecordView(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, @RequestParam String supplierId, Model model) {
		SupplierProjects supplierProjects = new SupplierProjects();
		// Supplier supplierObj =
		// supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
		// model.addAttribute("supplier", supplierObj);
		supplierProjects.setTracRecordCoverages(supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, null, projectId, null, null));
		model.addAttribute("supplierProjects", supplierProjects);
		model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, projectId, null, null));
		model.addAttribute("supplierId", supplierId);
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("btnLabel", "Save");
	}

	@RequestMapping(value = "/supplierEditProfileTrackRecord", method = RequestMethod.POST)
	public ResponseEntity<List<SupplierProjects>> supplierEditProfileTrackRecord(@ModelAttribute("supplierProject") SupplierProjects supplierProject, BindingResult result, Model model) {
		LOG.info(" Save SupplierProjects " + supplierProject.getProjectName());
		try {
			Supplier supplier = null;
			LOG.info(" Save SupplierProjects " + supplierProject.getProjectName() + " Supplier Id : " + supplierProject.getSupplierId() + " Project Id : " + supplierProject.getId());
			if (supplierProject.getSupplierId() != null) {
				supplier = supplierService.findSupplierForProjectTrackById(supplierProject.getSupplierId());
			} else {
				supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
			}
			if (supplier != null) {
				LOG.info("UPDATING TRACK RECORD  : " + supplier.getCompanyName());
				supplierProject.setSupplier(supplier);

				if (StringUtils.checkString(supplierProject.getId()).length() == 0) {
					LOG.info("creating Supplier Projecr.... ");
					SupplierProjects projects2 = new SupplierProjects();
					projects2.setClientEmail(supplierProject.getClientEmail());
					projects2.setClientName(supplierProject.getClientName());
					projects2.setContactValue(supplierProject.getContactValue());
					projects2.setProjectIndustries(supplierProject.getProjectIndustries());
					projects2.setProjectName(supplierProject.getProjectName());
					projects2.setYear(supplierProject.getYear());
					projects2.setCurrency(supplierProject.getCurrency());
					projects2.setTracRecordCoverages(supplierProject.getTracRecordCoverages());

					projects2.setSupplier(supplier);

					supplierService.saveSupplierProject(projects2);
				}
				List<SupplierProjects> list = null;
				if (supplierProject.getSupplierId() != null) {
					list = supplierService.findProjectsForSupplierId(supplierProject.getSupplierId());
				} else {
					list = supplierService.findProjectsForSupplierId(SecurityLibrary.getLoggedInUserTenantId());
				}
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.project.trackupload", new Object[] { supplierProject.getProjectName() }, Global.LOCALE));
				return new ResponseEntity<List<SupplierProjects>>(list, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while storing track records : " + e.getMessage(), e);
			model.addAttribute("errors", "Error while storing Supplier Project: " + e.getMessage());
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.project.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierProjects>>(null, headers, HttpStatus.BAD_REQUEST);
		}
		return null;
	}

	@RequestMapping(value = "searchCoverage", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Coverage>> searchCoverage(@RequestParam("search") String search, @RequestParam("activeTab") String activeTab, @RequestParam("supplierId") String supplierId, @RequestParam("projectId") String projectId, @RequestParam(value = "checkedAlredy[]") String[] checkedAlredy) {
		List<Coverage> list = null;
		try {
			LOG.info("checkedAlredy : " + checkedAlredy);
			list = supplierService.doSearchCoverageForSupplierRegistration(activeTab, supplierId, projectId, checkedAlredy, search);

			LOG.info("LIST SIZE : " + list.size());
		} catch (Exception e) {
			LOG.error("Error while Searching Country/State " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.countrysearch.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Coverage>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Coverage>>(list, HttpStatus.OK);
	}

	// @SuppressWarnings("unchecked")
	@RequestMapping(value = "/supplierTermsOfUse", method = RequestMethod.GET)
	public String supplierTermsOfUse(Model model) {
		LOG.info("GET.....Supplier");
		LOG.info("Supplier............GET........user" + SecurityLibrary.getLoggedInUser().getId() + "...name" + SecurityLibrary.getLoggedInUser().getName());
		try {
			User user = SecurityLibrary.getLoggedInUser();
			Supplier supplier = supplierService.findSuppById(user.getSupplier().getId());
			model.addAttribute("supplier", supplier);
			LOG.info(supplier.getCompanyName());
		} catch (Exception e) {
			LOG.error("Invalid security token : " + e.getMessage());
			model.addAttribute("errors", messageSource.getMessage("user.error.Invalid", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "supplierTermsOfUse";
	}

	@RequestMapping(value = "/supplierTermsOfUse", method = RequestMethod.POST)
	public ModelAndView supplierTermsOfUseSave(@ModelAttribute("supplier") Supplier supplier, @RequestParam("id") String supplierId, Model model, RedirectAttributes redir) {
		LOG.info("Enter the POST Method of Terms of User" + supplierId);
		try {
			Supplier dbSupplier = supplierService.findSuppById(supplierId);
			dbSupplier.setTermsOfUseAccepted(Boolean.TRUE);
			dbSupplier.setTermsOfUseAcceptedDate(new Date());
			supplierService.updateSupplier(dbSupplier);

			redir.addFlashAttribute("supplier", supplier);
			LOG.info("End the POST Method of Terms of User");
			return new ModelAndView("redirect:/supplierProfile");
		} catch (Exception e) {
			LOG.error("Error while storing Buyer Terms of Use : " + e.getMessage(), e);
			// errMessages.add("Buyer has not Accepted Terms of Use");
			// model.addAttribute("error", errMessages);
			model.addAttribute("error", messageSource.getMessage("buyer.not.accepted.terms", new Object[] {}, Global.LOCALE));
			return new ModelAndView("supplierTermsOfUse");

		}
	}

	// PH-1098 Supplier can update board of directors

	@RequestMapping(value = "/suppBoardOfDirectors", method = RequestMethod.GET)
	public String supplierOrganizationDetailsView(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, Model model) {
		LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is fetching supplier board of directors");
		Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
		if (supplier == null) {
			LOG.error("Redirecting the user to login page as we could not identify the Supplier based on session stored TENANT ID ****************");
			return "/login?error=true";
		}
		if ((supplier.getSupplierBoardOfDirectors() != null ? supplier.getSupplierBoardOfDirectors().size() : 0) == 0) {
			model.addAttribute("incompleteOrgSection", true);
		}
		model.addAttribute("supplier", supplier);
		model.addAttribute("boardOfDirector", new SupplierBoardOfDirectors());
		model.addAttribute("boardOfDirectors", supplier.getSupplierBoardOfDirectors());
		model.addAttribute("suppBoardOfDirectors", true);
		supplierService.checkIfProfileIsComplete(model, supplier);
		return "suppBoardOfDirectors";
	}

	@RequestMapping(path = "/supplierOrganizationalDetails", method = RequestMethod.POST)
	public ModelAndView updateSupplierOrganizationalDetails(@Valid @ModelAttribute(name = "boardOfDirector") SupplierBoardOfDirectors director, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is updating supplier board of directors");
		try {
			Supplier persistObject = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
			if (director.getId() == null || director.getId() == "") {
				List<SupplierBoardOfDirectors> allDirectors = supplierService.findDuplicateDirector(director.getIdNumber());
				if (allDirectors.size() > 0) {
					for (SupplierBoardOfDirectors supplierBoardOfDirectors : allDirectors) {
						if (supplierBoardOfDirectors.getSupplier().getId().equals(persistObject.getId())) {
							LOG.error("Error while updating board of directors - Director with ID Number already exists");
							redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.board.of.dir.duplicate", new Object[] {}, Global.LOCALE));
							return new ModelAndView("redirect:/supplier/suppBoardOfDirectors");
						}
					}
				}
				director.setSupplier(persistObject);
				director.setCreatedBy(SecurityLibrary.getLoggedInUser());
				director.setCreatedDate(new Date());
				supplierService.saveSupplierBoardOfDirector(director);
				redir.addFlashAttribute("success", messageSource.getMessage("supplier.director.add", new Object[] {}, Global.LOCALE));
			} else {
				SupplierBoardOfDirectors existingDirector = supplierService.findDirectorById(director.getId());
				existingDirector.setDirContact(director.getDirContact());
				existingDirector.setDirEmail(director.getDirEmail());
				existingDirector.setDirType(director.getDirType());
				existingDirector.setDirectorName(director.getDirectorName());
				existingDirector.setIdNumber(director.getIdNumber());
				existingDirector.setIdType(director.getIdType());
				existingDirector.setModifiedBy(SecurityLibrary.getLoggedInUser());
				existingDirector.setModifiedDate(new Date());
				supplierService.saveSupplierBoardOfDirector(existingDirector);
				redir.addFlashAttribute("success", messageSource.getMessage("supplier.director.update", new Object[] {}, Global.LOCALE));
			}
			model.addAttribute("boardOfDirectors", supplierService.findAllDirectorsBySupplierID(persistObject.getId()));
		} catch (Exception e) {
			LOG.error("Error while updating supplier board of directors " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.board.of.dir", new Object[] {}, Global.LOCALE));
			return new ModelAndView("redirect:/supplier/suppBoardOfDirectors");
		}
		return new ModelAndView("redirect:/supplier/suppBoardOfDirectors");
	}

	// PH-1098 Supplier can update financial information

	@RequestMapping(value = "/suppFinancialInformation", method = RequestMethod.GET)
	public String supplierFinancialDetailsView(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, Model model) {
		LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is fetching supplier financial information");
		Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
		if (supplier == null) {
			LOG.error("Redirecting the user to login page as we could not identify the Supplier based on session stored TENANT ID ****************");
			return "/login?error=true";
		}
		if ((supplier.getSupplierFinancialDocuments() != null ? supplier.getSupplierFinancialDocuments().size() : 0) == 0 || supplier.getPaidUpCapital() == null || supplier.getCurrency() == null) {
			model.addAttribute("incompleteFinanceSection", true);
		}
		model.addAttribute("isDeclared", supplier.getDeclaration());
		model.addAttribute("currencyList", currencyService.getlActiveCurrencies());
		model.addAttribute("supplier", supplier);
		OwnerSettings settings = ownerSettingsService.getOwnersettings();

		// Updating file size to 100 MB as per requirement.
		if (settings != null)
			settings.setFileSizeLimit(Long.valueOf(100));

		model.addAttribute("ownerSettings", settings);
		model.addAttribute("uploadFinancialDocuments", supplier.getSupplierFinancialDocuments());
		model.addAttribute("suppFinancialInformation", true);
		supplierService.checkIfProfileIsComplete(model, supplier);
		return "suppFinancialInformation";
	}

	@RequestMapping(value = "/suppFinancialInformation", method = RequestMethod.POST)
	public ModelAndView updateSupplierFinancialDetailsView(@Valid @ModelAttribute("supplier") Supplier supplier, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is updating supplier financial information");
		try {
			Supplier dbSupplier = supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
			dbSupplier.setCurrency(supplier.getCurrency());
			dbSupplier.setPaidUpCapital(supplier.getPaidUpCapital());
			supplierService.updateSupplier(dbSupplier);
		} catch (Exception e) {
			LOG.error("Error while updating supplier financial information" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.supplierinfo", new Object[] {}, Global.LOCALE));
			return new ModelAndView("redirect:/supplier/suppFinancialInformation");
		}
		redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.info.updated", new Object[] {}, Global.LOCALE));
		return new ModelAndView("redirect:/supplier/suppFinancialInformation");

	}

}
