package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.SecurityTokenDao;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.SecurityToken;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FinanceCompanyStatus;
import com.privasia.procurehere.core.exceptions.SecurityTokenException;
import com.privasia.procurehere.core.exceptions.SecurityTokenExpiredException;
import com.privasia.procurehere.core.exceptions.SecurityTokenInvalidException;
import com.privasia.procurehere.core.utils.EncryptionUtils;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.FinanceCompanyService;
import com.privasia.procurehere.service.StateService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.StateEditor;

@Controller
@RequestMapping(path = "/finance")
public class FinanceCompanyProfileSetupController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4513666621371061367L;

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	UserService userService;

	@Autowired
	FinanceCompanyService financeCompanyService;

	@Autowired
	SecurityTokenDao securityTokenDao;

	@Autowired
	CountryService countryService;

	@Autowired
	StateService stateService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	StateEditor stateEditor;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(State.class, stateEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		simpleDateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(simpleDateFormat, false));
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/financeCompanyTermsOfUse", method = RequestMethod.GET)
	public String financeTermsOfUse(Model model, @RequestParam String d, @RequestParam String v) {

		try {
			Map<String, String> params = null;
			String email = null;
			String token = null;
			try {
				params = (Map<String, String>) EncryptionUtils.decryptObject(d, v);
				email = params.get("email");
				token = params.get("token");
				LOG.info("Token : " + token + ", Email : " + email);
			} catch (Exception e) {
				LOG.error("Error in request : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "financeCompanyTermsOfUse";
			}

			if (StringUtils.checkString(token).length() == 0) {
				LOG.error("Invalid security token");
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "financeCompanyProfileSetup";
			}
			if (StringUtils.checkString(email).length() == 0) {
				model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));

				return "financeCompanyTermsOfUse";
			}
			User user = userService.getUserByLoginId(email);
			if (user == null) {
				LOG.error("Invalid security token");
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "resetPassword";
			}

			SecurityToken securityToken = new SecurityToken();
			securityToken.setToken(token);
			securityToken.setUser(user);
			try {

				FinanceCompany financeCompany = financeCompanyService.getFinanceCompanyById(user.getFinanceCompany().getId());
				model.addAttribute("financeCompany", financeCompany);
				securityTokenDao.validateToken(securityToken);
				model.addAttribute("d", d);
				model.addAttribute("v", v);
			} catch (SecurityTokenException | SecurityTokenExpiredException | SecurityTokenInvalidException e) {
				LOG.error("Invalid security token : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("user.error.Invalid", new Object[] { e.getMessage() }, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while get finance term and use: " + e.getMessage(), e);
		}

		return "financeCompanyTermsOfUse";
	}

	@RequestMapping(value = "/financeCompanyTermsOfUse", method = RequestMethod.POST)
	public ModelAndView financeTermsOfUseSave(@ModelAttribute("financeCompany") FinanceCompany financeCompany, @RequestParam("id") String financeId, @RequestParam("d") String d, @RequestParam String v, Model model, RedirectAttributes redir) {
		LOG.info("Enter the POST Method of Terms of User");
		List<String> errMessages = new ArrayList<String>();
		try {
			FinanceCompany dbFinanceCompany = financeCompanyService.getFinanceCompanyById(financeId);
			dbFinanceCompany.setTermsOfUseAccepted(Boolean.TRUE);
			dbFinanceCompany.setTermsOfUseAcceptedDate(new Date());
			financeCompanyService.updateFinanceCompany(dbFinanceCompany);
			LOG.info("Enter the POST after update............................");
			redir.addFlashAttribute("d", d);
			redir.addFlashAttribute("v", v);
			redir.addFlashAttribute("financeCompany", dbFinanceCompany);
			LOG.info("End the POST Method of Terms of User");
			return new ModelAndView("redirect:financeCompanyProfileSetup");
		} catch (Exception e) {
			LOG.error("Error while storing Finance Terms of Use : " + e.getMessage(), e);
//			errMessages.add("Finance Company has not Accepted Terms of Use");
//			model.addAttribute("error", errMessages);
			model.addAttribute("error", messageSource.getMessage("finance.company.not.accepted", new Object[] {}, Global.LOCALE));
			return new ModelAndView("financeCompanyTermsOfUse");

		}

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/financeCompanyProfileSetup", method = RequestMethod.GET)
	public String financeCompanyProfileView(@ModelAttribute("financeCompany") FinanceCompany financeCompany, Model model) {
		LOG.info("Enter the POST Method of Profile setup " + model.asMap().get("d"));
		Map<String, String> params = null;
		String email = null;
		String token = null;
		String d = null;
		String v = null;
		try {
			try {
				d = (String) model.asMap().get("d");
				v = (String) model.asMap().get("v");
				if (d == null || v == null) {
					LOG.error("Invalid security token d : " + d + ", v : " + v);
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "financeCompanyProfileSetup";
				}
				params = (Map<String, String>) EncryptionUtils.decryptObject(d, v);
				email = params.get("email");
				token = params.get("token");
				LOG.info("Token : " + token + ", Email : " + email);
			} catch (Exception e) {
				LOG.error("Error in request : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "financeCompanyProfileSetup";
			}

			if (StringUtils.checkString(token).length() == 0) {
				LOG.error("Invalid security token");
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "financeCompanyProfileSetup";
			}
			if (StringUtils.checkString(email).length() == 0) {
				model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));

				return "financeCompanyProfileSetup";
			}
			User user = userService.getUserByLoginId(email);
			if (user == null) {
				LOG.error("Invalid security token");
				model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
				return "financeCompanyProfileSetup";
			}

			SecurityToken securityToken = new SecurityToken();
			securityToken.setToken(token);
			securityToken.setUser(user);
			try {
				financeCompany = financeCompanyService.getFinanceCompanyById(user.getFinanceCompany().getId());
				securityTokenDao.validateToken(securityToken);
				model.addAttribute("financeCompany", financeCompany);
				model.addAttribute("countryStates", financeCompany.getRegistrationOfCountry() != null ? stateService.statesForCountry(financeCompany.getRegistrationOfCountry().getId()) : null);
				model.addAttribute("d", d);
				model.addAttribute("v", v);
			} catch (SecurityTokenException | SecurityTokenExpiredException | SecurityTokenInvalidException e) {
				LOG.error("Invalid security token : " + e.getMessage());
				model.addAttribute("errors", messageSource.getMessage("user.error.Invalid", new Object[] { e.getMessage() }, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while Finance Company profile setup : " + e.getMessage(), e);
		}

		return "financeCompanyProfileSetup";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/financeCompanyProfileSetup", method = RequestMethod.POST)
	public String saveFinanceCompanyProfile(@ModelAttribute("financeCompany") FinanceCompany financeCompany, @RequestParam("id") String id, BindingResult result, Model model, RedirectAttributes redir, @RequestParam String d, @RequestParam String v) {
		List<String> errMessages = new ArrayList<String>();
		Map<String, String> params = null;
		String email = null;
		String tokenId = null;
		if (result.hasErrors()) {
			for (ObjectError field : result.getAllErrors()) {
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("errors", errMessages);

			return "financeCompanyProfileSetup";
		} else {

			FinanceCompany dbFinanceCompany = financeCompanyService.getFinanceCompanyById(id);
			LOG.info("Page submitted......... " + financeCompany.toString());

			try {

				try {
					params = (Map<String, String>) EncryptionUtils.decryptObject(d, v);
					email = params.get("email");
					tokenId = params.get("token");
					LOG.info("Token : " + tokenId + ", Email : " + email);
				} catch (Exception e) {
					LOG.error("Error in request : " + e.getMessage(), e);
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "financeCompanyProfileSetup";
				}

				if (StringUtils.checkString(tokenId).length() == 0) {
					LOG.error("Invalid security token");
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "financeCompanyProfileSetup";
				}
				if (StringUtils.checkString(email).length() == 0) {
					model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));

					return "financeCompanyProfileSetup";
				}
				User user = userService.getUserByLoginId(email);
				if (user == null) {
					LOG.error("Invalid security token");
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "financeCompanyProfileSetup";
				}

				SecurityToken securityToken = new SecurityToken();
				securityToken.setToken(tokenId);
				securityToken.setUser(user);
				try {
					securityTokenDao.validateToken(securityToken);
				} catch (SecurityTokenException | SecurityTokenExpiredException | SecurityTokenInvalidException e) {
					LOG.error("Invalid security token : " + e.getMessage());
					model.addAttribute("errors", messageSource.getMessage("user.error.Invalid", new Object[] { e.getMessage() }, Global.LOCALE));
				}

				if (validate(financeCompany, model)) {
					return "financeCompanyProfileSetup";
				}

				LOG.info("LOG :  " + financeCompany.toString());
				BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
				String password = enc.encode(financeCompany.getPassword());

				dbFinanceCompany.setStatus(FinanceCompanyStatus.ACTIVE);
				dbFinanceCompany.setYearOfEstablished(financeCompany.getYearOfEstablished());
				dbFinanceCompany.setFaxNumber(financeCompany.getFaxNumber());
				dbFinanceCompany.setCompanyWebsite(financeCompany.getCompanyWebsite());
				/*
				 * dbFinanceCompany.setCompanyRegistrationNumber(financeCompany.getCompanyRegistrationNumber());
				 * dbFinanceCompany.setMobileNumber(financeCompany.getMobileNumber());
				 * dbFinanceCompany.setCompanyContactNumber(financeCompany.getCompanyContactNumber());
				 * dbFinanceCompany.setCommunicationEmail(financeCompany.getCommunicationEmail());
				 */
				dbFinanceCompany.setPassword(password);
				/*
				 * dbFinanceCompany.setLine1(financeCompany.getLine1());
				 * dbFinanceCompany.setLine2(financeCompany.getLine2());
				 * dbFinanceCompany.setCity(financeCompany.getCity());
				 * dbFinanceCompany.setRegistrationOfCountry(financeCompany.getRegistrationOfCountry());
				 * dbFinanceCompany.setState(financeCompany.getState());
				 */
				dbFinanceCompany.setRegistrationComplete(Boolean.TRUE);
				dbFinanceCompany.setRegistrationCompleteDate(new Date());
				LOG.info(dbFinanceCompany.toString());

				financeCompanyService.updateFinanceCompany(dbFinanceCompany);

				user.setPassword(password);
				userService.updateUserPlain(user);

				errMessages.add("Finance Company Profile Setup has saved successfully");
				redir.addFlashAttribute("info", errMessages);

				SecurityToken token = securityTokenDao.findById(tokenId);
				token.setUsedDate(new Date());
				securityTokenDao.update(token);

				return "redirect:/login?bsuccess=success";
			} catch (Exception e) {
				LOG.error("Error while storing Finance Company : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("finance.error.store", new Object[] { e.getMessage() }, Global.LOCALE));
				return "financeCompanyProfileSetup";
			}
		}

	}

	private boolean validate(FinanceCompany financeCompany, Model model) {
		boolean isError = false;
		if (financeCompanyService.isExists(financeCompany)) {
			model.addAttribute("errors", messageSource.getMessage("finance.error.duplicate", new Object[] { financeCompany.getCompanyName(), financeCompany.getCompanyRegistrationNumber(), financeCompany.getRegistrationOfCountry() }, Global.LOCALE));
			isError = true;
		}
		return isError;
	}

	@ModelAttribute("registeredCountry")
	public List<Country> populateCountries() {
		return countryService.findAllActiveCountries();
	}

	@RequestMapping(value = "/financeCompanyProfileForm", method = RequestMethod.GET)
	public String financeProfileFormView(@ModelAttribute("financeCompany") FinanceCompany financeCompany, Model model) {
		LOG.info("Enter the get Method of Profile setup --------------" + SecurityLibrary.getLoggedInUser().toLogString());

		try {
			financeCompany = financeCompanyService.getFinanceCompanyById(SecurityLibrary.getLoggedInUserTenantId());

			model.addAttribute("financeCompany", financeCompany);

			model.addAttribute("countryStates", financeCompany.getRegistrationOfCountry() != null ? stateService.statesForCountry(financeCompany.getRegistrationOfCountry().getId()) : null);

		} catch (Exception e) {
			LOG.error("Error while Finance profile setup : " + e.getMessage(), e);
		}

		return "financeCompanyProfileForm";
	}

	@RequestMapping(value = "/financeCompanyProfileForm", method = RequestMethod.POST)
	public String saveFinanceProfileForm(@ModelAttribute("financeCompany") FinanceCompany financeCompany, @RequestParam("id") String id, BindingResult result, Model model, RedirectAttributes redir, @RequestParam String d, @RequestParam String v) {
		List<String> errMessages = new ArrayList<String>();
		if (result.hasErrors()) {
			for (ObjectError field : result.getAllErrors()) {
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("errors", errMessages);

			return "financeCompanyProfileForm";
		} else {

			LOG.info("Page submitted......... " + financeCompany.toString());

			try {
				FinanceCompany dbFinanceCompany = financeCompanyService.getFinanceCompanyById(id);
				userService.getUserByLoginId(SecurityLibrary.getLoggedInUser().getId());
				if (StringUtils.checkString(SecurityLibrary.getLoggedInUser().getLoginId()).length() == 0) {
					model.addAttribute("errors", messageSource.getMessage("user.error.security.login", new Object[] {}, Global.LOCALE));

					return "financeCompanyProfileForm";
				}
				User user = userService.getUserByLoginId(SecurityLibrary.getLoggedInUser().getLoginId());
				if (user == null) {
					LOG.error("Invalid security token");
					model.addAttribute("errors", messageSource.getMessage("user.error.security", new Object[] {}, Global.LOCALE));
					return "financeCompanyProfileForm";
				}

				if (validate(financeCompany, model)) {
					return "financeCompanyProfileForm";
				}

				LOG.info("LOG :  " + financeCompany.toString());

				dbFinanceCompany.setStatus(FinanceCompanyStatus.ACTIVE);
				dbFinanceCompany.setCompanyRegistrationNumber(financeCompany.getCompanyRegistrationNumber());
				dbFinanceCompany.setMobileNumber(financeCompany.getMobileNumber());
				dbFinanceCompany.setCompanyContactNumber(financeCompany.getCompanyContactNumber());
				dbFinanceCompany.setCommunicationEmail(financeCompany.getCommunicationEmail());
				dbFinanceCompany.setLine1(financeCompany.getLine1());
				dbFinanceCompany.setLine2(financeCompany.getLine2());
				dbFinanceCompany.setCity(financeCompany.getCity());
				dbFinanceCompany.setYearOfEstablished(financeCompany.getYearOfEstablished());
				dbFinanceCompany.setFaxNumber(financeCompany.getFaxNumber());
				dbFinanceCompany.setCompanyWebsite(financeCompany.getCompanyWebsite());
				dbFinanceCompany.setRegistrationOfCountry(financeCompany.getRegistrationOfCountry());
				dbFinanceCompany.setState(financeCompany.getState());
				dbFinanceCompany.setRegistrationComplete(Boolean.TRUE);
				dbFinanceCompany.setRegistrationCompleteDate(new Date());
				LOG.info(dbFinanceCompany.toString());
				financeCompanyService.updateFinanceCompany(dbFinanceCompany);
//				errMessages.add("Your Profile Updated Successfully");
//				redir.addFlashAttribute("success", errMessages);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.profile.updated", new Object[] {}, Global.LOCALE));

				model.addAttribute("countryStates", financeCompany.getRegistrationOfCountry() != null ? stateService.statesForCountry(financeCompany.getRegistrationOfCountry().getId()) : null);
				return "redirect:/finance/financeDashboard";
			} catch (Exception e) {
				LOG.error("Error while storing Finance : " + e.getMessage(), e);
				model.addAttribute("errors", messageSource.getMessage("finance.error.store", new Object[] { e.getMessage() }, Global.LOCALE));
				return "financeCompanyProfileForm";
			}
		}

	}

	@RequestMapping(value = "/financeCountryStates", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<State>> financeCountryStates(@RequestParam("countryId") String countryId) {
		List<State> stateList = null;
		try {
			LOG.info("Country  " + countryId);
			stateList = stateService.statesForCountry(countryId);
			for (State state : stateList) {
				state.setCreatedBy(null);
				state.setModifiedBy(null);
				state.getCountry().setCreatedBy(null);
				state.getCountry().setModifiedBy(null);
			}
		} catch (Exception e) {
			LOG.error("Error while getting states for Country " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", messageSource.getMessage("finance.error.state", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<State>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<State>>(stateList, HttpStatus.OK);
	}

}
