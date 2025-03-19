/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.entity.SupplierCompanyProfile;
import com.privasia.procurehere.core.entity.SupplierFinanicalDocuments;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierOtherCredentials;
import com.privasia.procurehere.core.entity.SupplierProjects;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.pojo.Coverage.CoverageType;
import com.privasia.procurehere.core.pojo.ErrorMessage;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CompanyStatusService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.NaicsCodesService;
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
 * @author Arc
 */
@Controller
@RequestMapping(value = "/")
public class SupplierRegistrationController implements Serializable {

	private static final long serialVersionUID = -3511236085076614718L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	private static final String SESSION_ATTRIBUTE_OTHER_CRED_LIST = "otherCredList";
	private static final String SESSION_ATTRIBUTE_COMPANY_PRO = "uploadCompnayDetails";

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	// @Autowired
	// SmartValidator validator;

	@Resource
	MessageSource messageSource;

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

	@RequestMapping(value = "/supplierProfile", method = RequestMethod.GET)
	public String supplierRegistrationView(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, @RequestParam(name = "supplierTrackDesc", required = false) String supplierTrackDesc, Model model) {
		Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
		LOG.info("     supplierRegistrationView   supplierProfile  Step : " + step);

		if (supplier == null) {
			LOG.error("Redirecting the user to login page as we could not identify the Supplier based on session stored TENANT ID ****************");
			return "/login?error=true";
		}
		LOG.info("     supplier term and use : " + supplier.getTermsOfUseAccepted());
		if ("8".equals(step)) {
			LOG.info("Project Id : " + projectId);
			supplier.setSupplierTrackDesc(supplierTrackDesc);
			supplierService.updateSupplier(supplier);
			model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, projectId, null, null));
			model.addAttribute("categories", industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null));
			model.addAttribute("coverageCountry", supplierService.doSearchCoverage(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null));

		} else {
			model.addAttribute("categories", industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null));
			model.addAttribute("coverageCountry", supplierService.doSearchCoverage(Global.SUPPLIER_COVERAGE, supplier.getId(), null, null, null));
		}

		model.addAttribute("isDeclared", supplier.getDeclaration());
		// model.addAttribute("yearOfEstablishedList", populateYearOfEstabished());
		model.addAttribute("otherCredList", supplier.getSupplierOtherCredentials());
		model.addAttribute("uploadCompnayDetails", supplier.getSupplierCompanyProfile());
		model.addAttribute("states", stateService.findAllActiveStatesForCountry(supplier.getRegistrationOfCountry().getId()));
		model.addAttribute("supplier", supplier);

		// Adding new attributes.
		model.addAttribute("uploadFinancialDocuments", supplier.getSupplierFinancialDocuments());
		model.addAttribute("boardOfDirectors", supplier.getSupplierBoardOfDirectors());
		model.addAttribute("boardOfDirector", new SupplierBoardOfDirectors());

		SupplierProjects project = null;
		if (StringUtils.checkString(projectId).length() > 0) {
			project = supplierService.findBySupplierId(projectId);
			project.setSupplierId(supplier.getId());
			model.addAttribute("registeredTrackCountry", supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, null, projectId, null, null));
		} else {
			project = new SupplierProjects();
			project.setSupplierId(supplier.getId());
			model.addAttribute("registeredTrackCountry", supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, null, null, null, null));
		}
		model.addAttribute("step", step);
		model.addAttribute("supplierProject", project);
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("activeCurrencyList", currencyService.getlActiveCurrencies());
		model.addAttribute("projects", supplierService.findProjectsForSupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		return "supplierProfile";
	}

	/**
	 * @param supplierId
	 * @param model
	 */
	@RequestMapping(value = "/supplierTrackRecord", method = RequestMethod.GET)
	public void supplierTrackRecordView(@RequestParam(name = "step", required = false) String step, @RequestParam(name = "projectId", required = false) String projectId, @RequestParam String supplierId, Model model) {
		SupplierProjects supplierProjects = new SupplierProjects();
		supplierProjects.setTracRecordCoverages(supplierService.doSearchCoverage(Global.PROJECT_COVERAGE, null, projectId, null, null));
		model.addAttribute("supplierProjects", supplierProjects);
		model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, projectId, null, null));
		model.addAttribute("supplierId", supplierId);
		model.addAttribute("currency", currencyService.getAllCurrency());
		// model.addAttribute("yearOfEstablishedList", populateYearOfEstabished());
		model.addAttribute("btnLabel", "Save");
	}

	private List<Coverage> constructProjectTracCoverage(String projectId) {
		List<Coverage> coverages = new ArrayList<Coverage>();

		List<Country> list = countryService.findAllActiveCountries();
		if (CollectionUtil.isNotEmpty(list)) {
			for (Country country : list) {
				Coverage coverage = new Coverage();
				coverage.setId(country.getId());
				coverage.setCode(country.getCountryCode());
				coverage.setName(country.getCountryName());
				coverage.setType(CoverageType.COUNTRY);
				if (StringUtils.checkString(projectId).length() > 0) {
					List<Country> assignedCountires = supplierService.assignedCountriesForProjectTrackId(projectId);
					if (CollectionUtil.isNotEmpty(assignedCountires)) {
						List<Coverage> children = new ArrayList<Coverage>();
						for (Country assignedCountry : assignedCountires) {
							if (assignedCountry.equals(country)) {
								List<State> stateList = stateService.statesForCountry(assignedCountry.getId());
								for (State state : stateList) {
									Coverage child = new Coverage();
									child.setId(state.getId());
									child.setCode(state.getStateCode());
									child.setName(state.getStateName());
									child.setType(CoverageType.STATE);
									children.add(child);
								}
								coverage.setChildren(children);
							}
						}
					}
				}
				coverages.add(coverage);
			}
		}
		return coverages;
	}

	@ModelAttribute("registeredCountry")
	public List<Country> populateCountries() {
		return countryService.findAllActiveCountries();
	}

	@ModelAttribute("companyStatusList")
	public List<CompanyStatus> populateCompanyStatus() {
		return companyStatusService.getAllComapnyStatus();
	}

	@RequestMapping(value = "/updateSupplierComunicationEmail/{supplierId}/{emailId:.+}", method = RequestMethod.POST)
	public ResponseEntity<String> updateComunicationEmail(@PathVariable(name = "supplierId") String supplierId, @PathVariable(name = "emailId") String emailId) {
		LOG.info("Request for Update Supplier comunication email to (" + emailId + ") received for Supplier Id : " + supplierId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			Supplier supplier = supplierService.findSuppById(supplierId);
			User adminUser = userService.getUserByLoginIdNoTouch(supplier.getLoginEmail());
			if (adminUser == null) {
				LOG.warn("Admin user for Supplier " + supplierId + " is not present!!!");

				supplierService.updateSupplierCommunicationEmailForSupplierOnly(supplierId, supplier.getCommunicationEmail(), emailId);
				// HttpHeaders headers = new HttpHeaders();
				// headers.add("error", "Error while comunication email : " + "Admin user for
				// Supplier " + supplierId +
				// " is not present. Contact the administrator.");
				// return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers,
				// HttpStatus.INTERNAL_SERVER_ERROR);
			} else {

				/**
				 * JUST UPDATE THE USER. THE SUPPLIER WILL GET UPDATED IN SERVICE LAYER
				 */

				LOG.info("Found admin account for Supplier : " + supplierId + " with login email : " + adminUser.getLoginId());
				// supplierService.updateSupplierCommunicationEmail(supplierId,
				// supplier.getCommunicationEmail(), emailId);
				adminUser.setCommunicationEmail(emailId);
				userService.updateUser(adminUser);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "Email updated successfully");
			return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updated comunication email : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while updating comunication email : " + e.getMessage());
			return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/supplierProfile", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<String>> saveSupplierRegistration(@Valid @ModelAttribute("supplier") Supplier supplier, BindingResult result, Model model, HttpSession session) {

		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. " + supplier.getCompanyName());
			List<String> errMessages = new ArrayList<String>();
			for (FieldError field : result.getFieldErrors()) {
				LOG.info("ERROR : " + field.getField());
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("errors", errMessages);
			return new ResponseEntity<List<String>>(errMessages, HttpStatus.BAD_REQUEST);
		} else {

			try {

				LOG.info("Supplier Details........." + supplier.toString() + " :: " + supplier.getSupplierOtherCredentials() + " :: " + supplier.getSupplierProjects());
				List<SupplierOtherCredentials> otherCredList = (List<SupplierOtherCredentials>) session.getAttribute(SESSION_ATTRIBUTE_OTHER_CRED_LIST);
				if (CollectionUtil.isNotEmpty(otherCredList)) {
					LOG.info("OTHER CRED : " + otherCredList.size());
					for (SupplierOtherCredentials otherCredentials : otherCredList) {
						otherCredentials.setSupplier(supplier);
					}
					supplier.setSupplierOtherCredentials(otherCredList);
				}

				List<Country> countries = new ArrayList<Country>();
				List<State> states = new ArrayList<State>();
				List<Coverage> coverages = supplier.getCoverages();

				if (CollectionUtil.isNotEmpty(coverages)) {
					for (Coverage coverage : coverages) {
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
					supplier.setCountries(countries);
					supplier.setStates(states);
				}

				LOG.info("Page submitted....................................... " + supplier.toString() + " :: " + supplier.getSupplierOtherCredentials() + " :: " + supplier.getSupplierProjects());
				//
				supplier.setProfileUpdatedBy(SecurityLibrary.getLoggedInUser());
				supplier.setProfileUpdatedDate(new Date());
				supplier.setRegistrationComplete(Boolean.TRUE);
				supplier.setRegistrationCompleteDate(new Date());
			
				// In case if buyer upload supplier then buyer should associate with this
				// supplier
				LOG.info("supplier.getId()******:" + supplier.getId());

				Supplier persistObj = supplierService.getSupplierWithAssoBuyersAndSubPackageById(supplier.getId());
				supplier.setAssociatedBuyers(persistObj.getAssociatedBuyers());
				supplier.setSupplierSubscription(persistObj.getSupplierSubscription());
				supplier.setSupplierPackage(persistObj.getSupplierPackage());

				// setting term and use from db
				supplier.setTermsOfUseAccepted(persistObj.getTermsOfUseAccepted());
				supplier.setTermsOfUseAcceptedDate(persistObj.getTermsOfUseAcceptedDate());
				LOG.info(" supplier term and use post : " + supplier.getTermsOfUseAccepted());

				supplier.setBuyerAccount(persistObj.getBuyerAccount());
				supplier.setIsOnboardingForm(persistObj.getIsOnboardingForm());
				supplier.setOnBoardingFromsubmitedDate(persistObj.getOnBoardingFromsubmitedDate());
				supplier.setOnBoardingFormSubmittedBy(persistObj.getOnBoardingFormSubmittedBy());
				supplier.setApprovedDate(persistObj.getApprovedDate());
				supplier.setCreatedDate(persistObj.getCreatedDate());
				supplier.setCreatedBy(persistObj.getCreatedBy());// touch
				supplier.setActionDate(persistObj.getActionDate());
				supplier.setActionBy(persistObj.getActionBy());// touch
				supplier.setSubscriptionDate(persistObj.getSubscriptionDate());
				supplier.setDesignation(persistObj.getDesignation());

				supplierService.updateSupplier(supplier);

				if (session.getAttribute(SESSION_ATTRIBUTE_OTHER_CRED_LIST) != null) {
					session.setAttribute(SESSION_ATTRIBUTE_OTHER_CRED_LIST, null);
					session.removeAttribute(SESSION_ATTRIBUTE_OTHER_CRED_LIST);
				}
				if (session.getAttribute(SESSION_ATTRIBUTE_COMPANY_PRO) != null) {
					session.setAttribute(SESSION_ATTRIBUTE_COMPANY_PRO, null);
					session.removeAttribute(SESSION_ATTRIBUTE_COMPANY_PRO);
				}
				if (Boolean.TRUE == supplier.getBuyerAccount()) {
					SupplierFormSubmition supplierFormSubmition = supplierService.findSupplierFormBySupplierId(supplier.getId());
					HttpHeaders headers = new HttpHeaders();
					headers.add("onboard", "true");
					headers.add("formId", supplierFormSubmition.getId());
					return new ResponseEntity<List<String>>(headers, HttpStatus.OK);
				}
			} catch (Exception e) {
				LOG.error("Error while submiting registration : " + e.getMessage(), e);
				model.addAttribute("error", messageSource.getMessage("supplier.error.registration", new Object[] { e.getMessage() }, Global.LOCALE));
				List<String> errMessages = new ArrayList<String>();
				// errMessages.add("Error while storing Supplier : " + e.getMessage());
				return new ResponseEntity<List<String>>(errMessages, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<List<String>>(HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/companyProfileUpload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierCompanyProfile>> companyProfileUpload(@RequestParam("companyProfileFile") MultipartFile companyProfileFile, HttpSession session) {
		String fileName = null;
		if (!companyProfileFile.isEmpty()) {
			try {
				fileName = companyProfileFile.getOriginalFilename();
				byte[] bytes = companyProfileFile.getBytes();
				Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
				List<SupplierCompanyProfile> list = supplier.getSupplierCompanyProfile();
				SupplierCompanyProfile companyProfile = null;
				SupplierCompanyProfile companyProfileWithoutData = new SupplierCompanyProfile();
				if (CollectionUtil.isNotEmpty(list)) {
					companyProfile = supplier.getSupplierCompanyProfile().get(0);
				} else {
					companyProfile = new SupplierCompanyProfile();
				}

				companyProfile.setFileData(bytes);
				companyProfile.setFileName(fileName);
				companyProfile.setCompanyProfileContentType(companyProfileFile.getContentType());
				companyProfile.setSupplier(supplier);
				// PH -1098 added upload date
				companyProfile.setUploadDate(new Date());

				companyProfile = supplierService.saveSupplierProfile(companyProfile);
				companyProfileWithoutData.setFileName(companyProfile.getFileName());
				companyProfileWithoutData.setCompanyProfileContentType(companyProfile.getCompanyProfileContentType());
				companyProfileWithoutData.setSupplier(companyProfile.getSupplier());
				companyProfileWithoutData.setId(companyProfile.getId());
				companyProfileWithoutData.setUploadDate(companyProfile.getUploadDate());
				List<SupplierCompanyProfile> companyProfileList = new ArrayList<SupplierCompanyProfile>();
				companyProfileList.add(companyProfileWithoutData);

				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.companyfile.upload", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<SupplierCompanyProfile>>(companyProfileList, headers, HttpStatus.OK);

			} catch (Exception e) {
				LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.error.fileupload", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<List<SupplierCompanyProfile>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		}
		return null;

	}

	@RequestMapping(value = "/removeCompanyProfile", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> removeCompanyProfile(@RequestParam("profileId") String profileId, @RequestParam("companyFileName") String companyFileName) {
		try {
			if (StringUtils.checkString(profileId).length() > 0) {
				supplierService.removeCompanyProfile(profileId);
			}
		} catch (Exception e) {
			LOG.error(" Error while removing Company Profie : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.error.removeprofile", new Object[] { e.getMessage() }, Global.LOCALE));

			return new ResponseEntity<>("error", headers, HttpStatus.EXPECTATION_FAILED);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("info", messageSource.getMessage("supplier.file.remove", new Object[] { companyFileName }, Global.LOCALE));
		return new ResponseEntity<String>("Success", headers, HttpStatus.OK);
	}

	/**
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/downloadCompanyProfile/{id}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Other Credential Download  :: :: " + id + "::::::");
			SupplierCompanyProfile companyProfile = supplierService.findCompanyProfileById(id);
			response.setContentType(companyProfile.getCompanyProfileContentType());
			response.setContentLength(companyProfile.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + companyProfile.getFileName() + "\"");
			FileCopyUtils.copy(companyProfile.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded Company Profie : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/otherCredentialUpload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierOtherCredentials>> otherCredential(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc, HttpSession session) {
		String fileName = null;
		if (!file.isEmpty()) {
			try {
				fileName = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				SupplierOtherCredentials otherCred = new SupplierOtherCredentials();
				otherCred.setDescription(desc);
				otherCred.setFileData(bytes);
				otherCred.setFileName(fileName);
				// PH -1098 added upload date
				otherCred.setUploadDate(new Date());
				otherCred.setCredContentType(file.getContentType());

				Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
				otherCred.setSupplier(supplier);
				supplierService.saveSupplierOtherCredentials(otherCred);

				List<SupplierOtherCredentials> otherCredList = supplierService.findAllOtherCredentialBySupplierId(supplier.getId());

				LOG.info("***** File upload successfuly with upload date : " + otherCred.getUploadDate() + " List " + otherCredList.size());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.othercredfile.upload", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<SupplierOtherCredentials>>(otherCredList, headers, HttpStatus.OK);
			} catch (Exception e) {
				LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.file.uploaderror", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<SupplierOtherCredentials>>(null, headers, HttpStatus.BAD_REQUEST);
			}
		}
		return null;

	}

	@RequestMapping(value = "/removeOtherCredential", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierOtherCredentials>> removeOtherCred(@RequestParam("removeOtherId") String removeOtherId, @RequestParam("otherCredFile") String otherCredFile) {
		try {
			if (StringUtils.checkString(removeOtherId).length() > 0) {
				supplierService.removeOtherCredentials(removeOtherId);
				List<SupplierOtherCredentials> list = supplierService.findAllOtherCredentialBySupplierId(SecurityLibrary.getLoggedInUserTenantId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.file.remove", new Object[] { otherCredFile }, Global.LOCALE));
				return new ResponseEntity<List<SupplierOtherCredentials>>(list, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while Error while removing Other Credential : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.file.removecredentialerror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierOtherCredentials>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("info", messageSource.getMessage("supplier.file.remove", new Object[] { otherCredFile }, Global.LOCALE));
		return new ResponseEntity<List<SupplierOtherCredentials>>(null, headers, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveSupplierInSession/{step}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<ErrorMessage>> saveSupplierInSession(@PathVariable String step, @ModelAttribute("supplier") Supplier supplier, BindingResult result, Model model, HttpSession session) {
		try {
			LOG.info("***** CURRENT STEP ****** " + step);
			Supplier obj = supplierService.findSupplierById(supplier.getId());
			ResponseEntity<List<ErrorMessage>> response = null;
			LOG.info("STEP : " + StringUtils.checkString(step));
			if ("1".equals(StringUtils.checkString(step))) {
				response = validateStepTwo(supplier, result);
			} else if ("2".equals(StringUtils.checkString(step))) {
				response = validateStepOne(supplier, result);
				if (obj != null && obj.getNaicsCodes() != null) {
					supplier.setNaicsCodes(obj.getNaicsCodes());
				}
			} else if ("3".equals(StringUtils.checkString(step))) {
				response = validateStepTwo(supplier, result);
			} else if ("4".equals(StringUtils.checkString(step))) {
				response = validateStepThree(supplier, result);
			}

			if (response != null && response.getStatusCode() != HttpStatus.OK)
				return response;

			List<SupplierOtherCredentials> otherCredList = (List<SupplierOtherCredentials>) session.getAttribute(SESSION_ATTRIBUTE_OTHER_CRED_LIST);
			if (CollectionUtil.isNotEmpty(otherCredList)) {
				LOG.info("OTHER CRED : " + otherCredList.size());
				for (SupplierOtherCredentials otherCredentials : otherCredList) {
					otherCredentials.setSupplier(supplier);
				}
				supplier.setSupplierOtherCredentials(otherCredList);
			}

			if (supplier.getNaicsCodes() != null) {
				LOG.info("INDUSTRY : " + supplier.getNaicsCodes().size());
			}
			List<Country> countries = new ArrayList<Country>();
			List<State> states = new ArrayList<State>();

			List<Coverage> coverages = supplier.getCoverages();
			if (CollectionUtil.isNotEmpty(coverages)) {
				for (Coverage coverage : coverages) {
					if (coverage.getType() == CoverageType.COUNTRY) {
						// LOG.info("COUNTRY : " + coverage.getName());
						Country country = new Country();
						country.setCountryCode(coverage.getCode());
						country.setId(coverage.getId());
						country.setCountryName(coverage.getName());
						countries.add(country);
					} else {
						// LOG.info("STATE : " + coverage.getName());
						State state = new State();
						state.setStateCode(coverage.getCode());
						state.setId(coverage.getId());
						state.setStateName(coverage.getName());
						states.add(state);
					}
				}
				supplier.setCountries(countries);
				supplier.setStates(states);
			}

			supplier.setTermsOfUseAccepted(obj.getTermsOfUseAccepted());
			supplier.setTermsOfUseAcceptedDate(obj.getTermsOfUseAcceptedDate());
			// set subscription plan and package
			supplier.setSupplierPackage(obj.getSupplierPackage());
			supplier.setSupplierSubscription(obj.getSupplierSubscription());
			supplier.setApprovedDate(obj.getApprovedDate());
			supplier.setCreatedDate(obj.getCreatedDate());
			supplier.setCreatedBy(obj.getCreatedBy());// touch
			supplier.setActionDate(obj.getActionDate());
			supplier.setActionBy(obj.getActionBy());// touch
			supplier.setSubscriptionDate(obj.getSubscriptionDate()); 
			supplier.setBuyerAccount(obj.getBuyerAccount());
			supplier.setIsOnboardingForm(obj.getIsOnboardingForm());
			supplier.setOnBoardingFromsubmitedDate(obj.getOnBoardingFromsubmitedDate());
			supplier.setOnBoardingFormSubmittedBy(obj.getOnBoardingFormSubmittedBy());
		

			LOG.info(" supp term and use :" + supplier.getTermsOfUseAccepted());

			// In case if buyer upload supplier then buyer should associate with this
			// supplier
			Supplier persistObj = supplierService.findSupplierAndAssocitedBuyersById(supplier.getId());
			supplier.setAssociatedBuyers(persistObj.getAssociatedBuyers());

			LOG.info("Save supplier " + supplier.toString());
			
				supplierService.updateSupplier(supplier);

			if (session.getAttribute(SESSION_ATTRIBUTE_OTHER_CRED_LIST) != null) {
				session.setAttribute(SESSION_ATTRIBUTE_OTHER_CRED_LIST, null);
				session.removeAttribute(SESSION_ATTRIBUTE_OTHER_CRED_LIST);
			}
		} catch (Exception e) {
			LOG.error("Error while updating supplier " + e.getMessage(), e);
			List<ErrorMessage> errorList = new ArrayList<ErrorMessage>();
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.update.error", new Object[] { e.getMessage() }, Global.LOCALE));
			// headers.add("error", e.getMessage());
			new ResponseEntity<List<ErrorMessage>>(errorList, headers, HttpStatus.BAD_REQUEST);
		}
		return null;
	}

	/**
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/supplierTrackRecord/{id}", method = RequestMethod.GET)
	public String supplierProjectsView(@PathVariable String id, Model model) {
		SupplierProjects supplierProjects = supplierService.findSupplierProjectById(id);
		List<Coverage> coverages = constructProjectTracCoverage(supplierProjects.getId());
		model.addAttribute("registeredTrackCountry", coverages);
		model.addAttribute("supplierProjects", supplierProjects);

		model.addAttribute("supplierId", SecurityLibrary.getLoggedInUserTenantId());
		// model.addAttribute("yearOfEstablishedList", populateYearOfEstabished());

		model.addAttribute("projectCategories", industryCategoryService.searchForCategories(Global.PROJECT_COVERAGE, null, supplierProjects.getId(), null, null)); // industryCategoryService.findParentIndustryCategoryIncludingAssignedForTrackProject(supplierProjects.getSupplier().getId()));
		model.addAttribute("coverageCountry", supplierService.doSearchCoverage(null, null, null, null, null));
		model.addAttribute("btnLabel", "Update");
		return "supplierTrackRecord";
	}

	/**
	 * @param SupplierProjects
	 * @param result
	 * @param model
	 * @return
	 */

	@RequestMapping(value = "/supplierTrackRecord", method = RequestMethod.POST)
	public ResponseEntity<List<SupplierProjects>> saveSupplierTrackRecord(@ModelAttribute("supplierProject") SupplierProjects supplierProject, BindingResult result, Model model) {
		// LOG.info(" Save SupplierProjects " + supplierProject.getProjectName() + "
		// Supplier Id : " +
		// supplierProject.getSupplierId() + " INDUSTRY : " +
		// supplierProject.getProjectIndustries().size());
		try {
			Supplier supplier = supplierService.findSupplierForProjectTrackById(supplierProject.getSupplierId());
			if (supplier != null) {
				LOG.info("UPDATING TRACK RECORD  : " + supplier.getCompanyName());
				supplierProject.setSupplier(supplier);
				HttpHeaders headers = new HttpHeaders();
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
					headers.add("error", messageSource.getMessage("supplier.project.trackupload", new Object[] { supplierProject.getProjectName() }, Global.LOCALE));
				} else {
					LOG.info("updating Supplier Projecr.... ");
					SupplierProjects projects2 = new SupplierProjects();
					projects2.setId(supplierProject.getId());
					projects2.setClientEmail(supplierProject.getClientEmail());
					projects2.setClientName(supplierProject.getClientName());
					projects2.setContactValue(supplierProject.getContactValue());
					projects2.setProjectIndustries(supplierProject.getProjectIndustries());
					projects2.setProjectName(supplierProject.getProjectName());
					projects2.setYear(supplierProject.getYear());
					projects2.setCurrency(supplierProject.getCurrency());
					projects2.setSupplier(supplierProject.getSupplier());
					projects2.setTracRecordCoverages(supplierProject.getTracRecordCoverages());

					supplierService.updateSupplierProject(projects2);
					headers.add("error", messageSource.getMessage("supplier.project.trackupload.update", new Object[] { supplierProject.getProjectName() }, Global.LOCALE));
				}
				List<SupplierProjects> list = supplierService.findProjectsForSupplierId(supplierProject.getSupplierId());
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

	private ResponseEntity<List<ErrorMessage>> validateStepOne(Supplier supplier, BindingResult result) {
		List<ErrorMessage> errorList = new ArrayList<ErrorMessage>();
		HttpHeaders headers = new HttpHeaders();
		LOG.info("Validaiting step  One....." + supplier.toString());

		Set<ConstraintViolation<Supplier>> constraintViolations = validator.validate(supplier, Supplier.SupplierStep1.class);

		for (ConstraintViolation<Supplier> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			headers.add("error", cv.getMessage());
		}

		return new ResponseEntity<List<ErrorMessage>>(errorList, headers, (headers.size() > 0) ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
	}

	private ResponseEntity<List<ErrorMessage>> validateStepTwo(Supplier supplier, BindingResult result) {
		List<ErrorMessage> errorList = new ArrayList<ErrorMessage>();
		HttpHeaders headers = new HttpHeaders();
		if (CollectionUtil.isEmpty(supplier.getNaicsCodes())) {
			headers.add("error", messageSource.getMessage("supplier.industy.required", new Object[] {}, Global.LOCALE));
		} else {
			for (NaicsCodes codes : supplier.getNaicsCodes()) {
				codes.setCreatedBy(null);
				codes.setModifiedBy(null);

			}
		}
		return new ResponseEntity<List<ErrorMessage>>(errorList, headers, (headers.size() > 0) ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
	}

	private ResponseEntity<List<ErrorMessage>> validateStepThree(Supplier supplier, BindingResult result) {
		List<ErrorMessage> errorList = new ArrayList<ErrorMessage>();
		HttpHeaders headers = new HttpHeaders();

		if (Boolean.FALSE == supplier.getDeclaration()) {
			headers.add("error", messageSource.getMessage("supplier.accept.term", new Object[] {}, Global.LOCALE));
		}
		return new ResponseEntity<List<ErrorMessage>>(errorList, headers, (headers.size() > 0) ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
	}

	/**
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "findChildIndustry", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<NaicsCodes>> findChildIndustry(@RequestBody SearchVo searchVo) {
		List<NaicsCodes> list = new ArrayList<NaicsCodes>();
		LOG.info("Start : ");
		try {
			// LOG.info("Parent " + searchVo.getId());
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

	@RequestMapping(value = "findStates", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<Coverage>> findStates(@RequestParam("countryId") String countryId) {
		List<Coverage> list = new ArrayList<Coverage>();
		try {
			LOG.info("Country  " + countryId);
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

	@RequestMapping(value = "searchCoverage", method = RequestMethod.GET)
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

	@RequestMapping(value = "/removeTrackProject", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> removeTrackProject(@RequestParam("projectId") String projectId, @RequestParam("removeProject") String removeProject) {
		try {
			if (StringUtils.checkString(projectId).length() > 0) {
				supplierService.removeTrackProject(projectId);
			}
		} catch (Exception e) {
			LOG.error("Error while Error while removing Track Project : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while removing Track Project : " +
			// e.getMessage());
			headers.add("error", messageSource.getMessage("supplier.removetrack.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<>("error", headers, HttpStatus.EXPECTATION_FAILED);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("info", messageSource.getMessage("supplier.project.remove", new Object[] { removeProject }, Global.LOCALE));
		return new ResponseEntity<String>("Success", headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/supplierProfileInfo", method = RequestMethod.POST)
	public ModelAndView updateSupplierProfile(@Valid @ModelAttribute(name = "supplier") Supplier supplier, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("supplier Settings create Called with update Supplier Profile");

		try {
			Supplier persistObject = supplierService.findSuppById(SecurityLibrary.getLoggedInUserTenantId());
			persistObject.setCompanyName(supplier.getCompanyName());
			persistObject.setCompanyRegistrationNumber(supplier.getCompanyRegistrationNumber());
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
			persistObject.setTaxRegistrationNumber(supplier.getTaxRegistrationNumber());
			persistObject.setCurrency(supplier.getCurrency());
			persistObject.setPaidUpCapital(supplier.getPaidUpCapital());
			persistObject.setDesignation(supplier.getDesignation());
			supplierService.updateSupplier(persistObject);
		} catch (Exception e) {
			LOG.error("Error while updating supplier profile " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.supplierinfo", new Object[] {}, Global.LOCALE));
			return new ModelAndView("redirect:/supplierProfileDetails");
		}

		redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.info.updated", new Object[] {}, Global.LOCALE));
		return new ModelAndView("redirect:/supplierProfileDetails");
	}

	@RequestMapping(path = "/supplierProductCategory", method = RequestMethod.POST)
	public ModelAndView supplierProductCategory(@Valid @ModelAttribute(name = "supplier") Supplier supplier, BindingResult result, Model model, RedirectAttributes redir) {
		LOG.info("supplier Settings create Called with supplier Product Category");

		List<Country> countries = new ArrayList<Country>();
		List<State> states = new ArrayList<State>();

		try {
			Supplier persistObject = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
			persistObject.setNaicsCodes(supplier.getNaicsCodes());

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
		} catch (Exception e) {
			LOG.error("Error while updating Naic Codes/Coverage " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.niacs", new Object[] {}, Global.LOCALE));
		}

		redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.naicscode.updated", new Object[] {}, Global.LOCALE));
		return new ModelAndView("redirect:/supplierProfileCategory");
	}

	@RequestMapping(value = "/updateSupplierTrackRecord/{editid}", method = RequestMethod.POST)
	public ResponseEntity<SupplierProjects> UpdateSupplierTrackRecord(@PathVariable(name = "editid") String id) {
		LOG.info(" UpdateSupplierTrackRecord ");
		try {

			SupplierProjects list = supplierService.findBySupplierId(id);

			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.project.trackupload", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<SupplierProjects>(list, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while storing track records : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.project.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SupplierProjects>(null, headers, HttpStatus.BAD_REQUEST);
		}
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
				supplierService.saveSupplierProject(projects2);
			}

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.trackrecord.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while storing track records : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.storing.trackrecord", new Object[] {}, Global.LOCALE));
		}

		// model.addAttribute("flag", Boolean.TRUE);
		return new ModelAndView("redirect:/suppAddEditTrackRecord");
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
		Boolean flag = Boolean.TRUE;

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
		// model.addAttribute("otfherCredList",
		// supplierObj.getSupplierOtherCredentials());
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

		return new ModelAndView("suppAddEditTrackRecord");
	}

	@RequestMapping(value = "/removeSupplierProfileTrackRecord/{projectId}", method = RequestMethod.GET)
	public String updateSupplierProfileTrackRecord(@PathVariable("projectId") String projectId, RedirectAttributes redir, Model model) {
		SupplierProjects persistObject = null;
		// Supplier supplierObj = null;
		try {
			persistObject = supplierService.findBySupplierId(projectId);
			// supplierObj =
			// supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
			if (StringUtils.checkString(projectId).length() > 0) {
				supplierService.removeTrackProject(projectId);
			}

			/*
			 * model.addAttribute("currency", currencyService.getAllCurrency()); SupplierProjects project = null;
			 * project = new SupplierProjects(); project.setSupplierId(supplierObj.getId());
			 * model.addAttribute("registeredTrackCountry", supplierService.doSearchCoverage(Global.PROJECT_COVERAGE,
			 * null, null, null, null)); model.addAttribute("supplierProject", project);
			 * model.addAttribute("categories", industryCategoryService.searchForCategories(Global.SUPPLIER_COVERAGE,
			 * supplierObj.getId(), null, null, null)); model.addAttribute("coverageCountry",
			 * supplierService.doSearchCoverage(Global.SUPPLIER_COVERAGE, supplierObj.getId(), null, null, null));
			 * model.addAttribute("supplier", supplierObj);
			 */
			redir.addFlashAttribute("success", messageSource.getMessage("supplier.project.remove", new Object[] { persistObject.getProjectName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Error while removing Track Project  :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.removetrack.error", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "redirect:/suppAddEditTrackRecord";

	}

	// PH-1098 Supplier can upload financial documents

	@RequestMapping(value = "/financialDocumentsUpload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierFinanicalDocuments>> financialDocumentsUpload(@RequestParam("financialDocumentsFile") MultipartFile financialDocumentsFile, @RequestParam("desc") String desc, HttpSession session) {
		String fileName = null;

		if (!financialDocumentsFile.isEmpty()) {
			try {
				LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is uploading a financial document.");
				fileName = financialDocumentsFile.getOriginalFilename();
				byte[] bytes = financialDocumentsFile.getBytes();
				SupplierFinanicalDocuments document = new SupplierFinanicalDocuments();
				document.setDescription(desc);
				document.setFileData(bytes);
				document.setFileName(fileName);
				document.setUploadDate(new Date());
				document.setFinancialDocContentType(financialDocumentsFile.getContentType());
				Supplier supplier = new Supplier();
				supplier.setId(SecurityLibrary.getLoggedInUserTenantId());
				document.setSupplier(supplier);
				document.setUploadedBy(SecurityLibrary.getLoggedInUser());
				supplierService.saveSupplierFinancialDocuments(document);
				List<SupplierFinanicalDocuments> documentList = supplierService.findAllFinancialDocumentsBySupplierID(supplier.getId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.financial.doc.upload", new Object[] { fileName }, Global.LOCALE));
				LOG.info("***** File upload successfuly with upload date : " + document.getUploadDate() + " List " + (documentList != null ? documentList.size() : 0));
				return new ResponseEntity<List<SupplierFinanicalDocuments>>(documentList, headers, HttpStatus.OK);
			} catch (Exception e) {
				LOG.error("You failed to upload " + fileName + ": " + e.getMessage(), e);
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.file.uploaderror", new Object[] { fileName }, Global.LOCALE));
				return new ResponseEntity<List<SupplierFinanicalDocuments>>(null, headers, HttpStatus.BAD_REQUEST);
			}

		}
		return null;

	}

	@RequestMapping(value = "/downloadFinancialDocuments/{id}", method = RequestMethod.GET)
	public void downloadFinancialDocuments(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is downloading a financial document.");
			SupplierFinanicalDocuments finanicalDocuments = supplierService.findFinancialDocumentId(id);
			response.setContentType(finanicalDocuments.getFinancialDocContentType());
			response.setContentLength(finanicalDocuments.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + finanicalDocuments.getFileName() + "\"");
			FileCopyUtils.copy(finanicalDocuments.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while downloading financial document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/removeFinancialDoc", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierFinanicalDocuments>> removeFinancialDocumentd(@RequestParam("id") String removeId, @RequestParam("file") String file) {
		try {
			if (StringUtils.checkString(removeId).length() > 0) {
				LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is removing a financial document with id " + removeId);
				supplierService.removeSupplierFinancialDocuments(removeId);
				List<SupplierFinanicalDocuments> list = supplierService.findAllFinancialDocumentsBySupplierID(SecurityLibrary.getLoggedInUserTenantId());
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", messageSource.getMessage("supplier.file.remove", new Object[] { file }, Global.LOCALE));
				return new ResponseEntity<List<SupplierFinanicalDocuments>>(list, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error while Error while removing FINANCIAL DOCUMENT : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.file.removecredentialerror", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierFinanicalDocuments>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("info", messageSource.getMessage("supplier.file.remove", new Object[] { file }, Global.LOCALE));
		return new ResponseEntity<List<SupplierFinanicalDocuments>>(null, headers, HttpStatus.OK);
	}

	// PH-1098 Supplier can upload board of directors

	@RequestMapping(path = "/addDirector", method = RequestMethod.POST)
	public ResponseEntity<List<SupplierBoardOfDirectors>> addBoardOfDirectors(@RequestBody SupplierBoardOfDirectors directors, HttpSession session) {

		try {
			LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is adding a board of director.");
			Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
			HttpHeaders headers = new HttpHeaders();
			if (directors.getId() == null || directors.getId() == "") {
				List<SupplierBoardOfDirectors> existingDirectors = supplierService.findDuplicateDirector(directors.getIdNumber());
				if (CollectionUtil.isNotEmpty(existingDirectors)) {
					for (SupplierBoardOfDirectors supplierBoardOfDirectors : existingDirectors) {
						if (supplierBoardOfDirectors.getSupplier().getId().equals(supplier.getId())) {
							LOG.error("Error while updating board of directors - Director with ID Number already exists");
							headers.add("error", messageSource.getMessage("flasherror.while.updating.board.of.dir.duplicate", new Object[] { directors.getDirectorName() }, Global.LOCALE));
							return new ResponseEntity<List<SupplierBoardOfDirectors>>(null, headers, HttpStatus.BAD_REQUEST);
						}
					}
				}
				directors.setModifiedBy(SecurityLibrary.getLoggedInUser());
				directors.setModifiedDate(new Date());
				directors.setSupplier(supplier);
				supplierService.saveSupplierBoardOfDirector(directors);
				headers.add("error", messageSource.getMessage("supplier.director.add", new Object[] { directors.getDirectorName() }, Global.LOCALE));
			} else {
				SupplierBoardOfDirectors updatedDirector = supplierService.findDirectorById(directors.getId());
				updatedDirector.setDirContact(directors.getDirContact());
				updatedDirector.setDirEmail(directors.getDirEmail());
				updatedDirector.setDirType(directors.getDirType());
				updatedDirector.setDirectorName(directors.getDirectorName());
				updatedDirector.setIdNumber(directors.getIdNumber());
				updatedDirector.setIdType(directors.getIdType());
				updatedDirector.setSupplier(supplier);
				updatedDirector.setModifiedBy(SecurityLibrary.getLoggedInUser());
				updatedDirector.setModifiedDate(new Date());
				supplierService.saveSupplierBoardOfDirector(updatedDirector);
				headers.add("error", messageSource.getMessage("supplier.director.update", new Object[] { directors.getDirectorName() }, Global.LOCALE));
			}
			List<SupplierBoardOfDirectors> directorList = supplierService.findAllDirectorsBySupplierID(supplier.getId());
			LOG.info("***** UPDATED BODs ***** " + (directorList != null ? directorList.size() : 0));
			return new ResponseEntity<List<SupplierBoardOfDirectors>>(directorList, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("You failed to add " + directors.getDirectorName() + ": " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.director.add.error", new Object[] { directors.getDirectorName() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierBoardOfDirectors>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/removeDirector", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierBoardOfDirectors>> removeDirector(@RequestParam("id") String removeId, @RequestParam("name") String removeName) {
		try {
			LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is removing a board of director with ID " + removeId);
			supplierService.removeBoardOfDirector(removeId);
			List<SupplierBoardOfDirectors> list = supplierService.findAllDirectorsBySupplierID(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("***** UPDATED BODs ***** " + (list != null ? list.size() : 0));
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.director.remove", new Object[] { removeName }, Global.LOCALE));
			return new ResponseEntity<List<SupplierBoardOfDirectors>>(list, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while removing director : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.director.remove.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<SupplierBoardOfDirectors>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/editDirector", method = RequestMethod.GET)
	public ResponseEntity<SupplierBoardOfDirectors> editDirector(@RequestParam("id") String editId) {
		try {
			LOG.info("***** User " + SecurityLibrary.getLoggedInUserLoginId() + " is editing a board of director with ID " + editId);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.director.edit", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<SupplierBoardOfDirectors>(supplierService.findDirectorById(editId), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while editing board of director : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("supplier.director.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SupplierBoardOfDirectors>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/updateSupplierCompanyName/{supplierId}", method = RequestMethod.POST)
	public ResponseEntity<String> updateCompanyName(@PathVariable(name = "supplierId") String supplierId, @RequestParam(name = "companyName") String companyName) {
		LOG.info("Request for Update Supplier company name to (" + companyName + ") received for Supplier Id : " + supplierId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		HttpHeaders headers = new HttpHeaders();
		try {
			if (TenantType.OWNER == SecurityLibrary.getLoggedInUser().getTenantType()) {
				Supplier supplier = supplierService.updateSupplierCompanyName(companyName, supplierId, SecurityLibrary.getLoggedInUserTenantId(), headers);
				if (supplier != null) {
					headers.add("success", "Supplier Company Name updated successfully");
				}
			}

			return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updating Company Name : " + e.getMessage(), e);
			headers.add("error", "Error while updating Supplier Company Name : " + e.getMessage());
			return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateSuppRegistrationNumber/{supplierId}", method = RequestMethod.POST)
	public ResponseEntity<String> updateRegistrationNumber(@PathVariable(name = "supplierId") String supplierId, @RequestParam(name = "regNumber") String regNumber) {
		LOG.info("Request for Update Supplier Registration Number to (" + regNumber + ") received for Supplier Id : " + supplierId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		HttpHeaders headers = new HttpHeaders();
		try {
			if (TenantType.OWNER == SecurityLibrary.getLoggedInUser().getTenantType()) {
				Supplier supplier = supplierService.updateSupplierRegistrationNumber(regNumber, supplierId, SecurityLibrary.getLoggedInUserTenantId(), headers);
				if (supplier != null) {
					headers.add("success", "Supplier Company Registration Number updated successfully");
				}
			}

			return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updating Registration Number : " + e.getMessage(), e);
			headers.add("error", "Error while updating Supplier Registration Number : " + e.getMessage());
			return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
