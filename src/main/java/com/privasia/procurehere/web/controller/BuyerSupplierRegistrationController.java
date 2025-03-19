/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.privasia.procurehere.core.dao.SupplierSubscriptionDao;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.PasswordSettings;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.PasswordSettingService;
import com.privasia.procurehere.service.SupplierPlanService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.BuyerSupplierRegistrationService;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.privasia.procurehere.web.editors.CountryEditor;
import com.privasia.procurehere.web.editors.IndustryCategoryEditor;
import com.privasia.procurehere.web.editors.ProductCategoryMaintenanceEditor;

/**
 * @author ravi
 */
@Controller
@RequestMapping(value = "/")
public class BuyerSupplierRegistrationController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1284768145262445266L;

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerSupplierRegistrationService buyerSupplierRegistrationService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	CountryService countryService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	BuyerService buyerService;

	@Autowired
	SupplierSubscriptionDao supplierSubscriptionDao;

	@Autowired
	SupplierPlanService supplierPlanService;

	@Autowired
	PasswordSettingService passwordSettingService;

	@Autowired
	UserService userService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	ProductCategoryMaintenanceEditor productCategoryMaintenanceEditor;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	IndustryCategoryEditor industryCategoryEditor;

	/**
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(IndustryCategory.class, industryCategoryEditor);
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(path = "/{buyerId}/register", method = { RequestMethod.GET })
	public String supplierRegistration(@PathVariable("buyerId") String buyerId, Model model) {
		LOG.info(" *********** supplierRegistration called *************** " + buyerId);
		Buyer buyer = buyerService.getContextPathForRegistration(StringUtils.checkString(buyerId));

		if (buyer != null) {
			LOG.info("Buyer Details found .................");

			Supplier supplier = new Supplier();
			Country my = countryService.getCountryByCode("MY");
			Country country = new Country(my.getId(), my.getCountryCode(), my.getCountryName(), my.getStatus());
			supplier.setRegistrationOfCountry(country);
			model.addAttribute("countryList", countryService.getActiveCountries());
			model.addAttribute("supplier", supplier);
			model.addAttribute("companyName", buyer.getCompanyName());
			model.addAttribute("supplierRegistrationPath", buyerId);
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());

			try {

				if (buyerSettings != null && buyerSettings.getFileAttatchment() != null) {
					byte[] encodeBase64 = Base64.encodeBase64(buyerSettings.getFileAttatchment());
					String base64Encoded = new String(encodeBase64, "UTF-8");
					model.addAttribute("companyLogo", base64Encoded);

				}
			} catch (Exception e) {
				LOG.error("Error encoding Image" + e.getMessage(), e);
			}

			List<IndustryCategory> icList = industryCategoryService.findActiveIndustryCategoryByTenantId(buyer.getId());
			model.addAttribute("icList", icList);

			LOG.info("Sending details...");
			return "register";
		} else {
			LOG.info("Buyer Details Not found .................");
			return "redirect:/404_error";
		}
	}

	@RequestMapping(value = "/{buyerId}/register", method = RequestMethod.POST)
	public String saveRegistration(@PathVariable("buyerId") String buyerId, @ModelAttribute("supplier") Supplier supplier, BindingResult result, Model model) {
		Buyer buyer = buyerService.getContextPathForRegistration(buyerId);
		if (buyer == null) {
			LOG.info("Buyer Details Not found .................");
			return "redirect:/404_error";
		}
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError field : result.getAllErrors()) {
				LOG.info("ERROR : " + field.getObjectName() + " : " + field.getDefaultMessage());
				errMessages.add(field.getDefaultMessage());
			}
			model.addAttribute("errors", errMessages);
			model.addAttribute("companyName", buyer.getCompanyName());
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());
			List<IndustryCategory> icList = industryCategoryService.findActiveIndustryCategoryByTenantId(buyer.getId());
			model.addAttribute("icList", icList);

			try {

				if (buyerSettings != null && buyerSettings.getFileAttatchment() != null) {
					byte[] encodeBase64 = Base64.encodeBase64(buyerSettings.getFileAttatchment());
					String base64Encoded = new String(encodeBase64, "UTF-8");
					model.addAttribute("companyLogo", base64Encoded);

				}
			} catch (Exception e) {
				LOG.error("Error encoding Image" + e.getMessage(), e);
			}
			return "redirect:register";
		} else {
			try {
				if (validate(supplier, model)) {
					LOG.info("Registration country : " + supplier.getRegistrationOfCountry().getCountryName());
					model.addAttribute("supplier", supplier);
					model.addAttribute("countryList", countryService.getActiveCountries());
					model.addAttribute("companyName", buyer.getCompanyName());
					BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());
					List<IndustryCategory> icList = industryCategoryService.findActiveIndustryCategoryByTenantId(buyer.getId());
					model.addAttribute("icList", icList);

					try {

						if (buyerSettings != null && buyerSettings.getFileAttatchment() != null) {
							byte[] encodeBase64 = Base64.encodeBase64(buyerSettings.getFileAttatchment());
							String base64Encoded = new String(encodeBase64, "UTF-8");
							model.addAttribute("companyLogo", base64Encoded);
						}
					} catch (Exception e) {
						LOG.error("Error encoding Image" + e.getMessage(), e);
					}
					LOG.info("Returning from error....");
					return "register";
				}
				supplier = buyerSupplierRegistrationService.registerSupplier(supplier, buyer.getId());
				LOG.info("Supplier " + supplier.getCompanyName() + " Created successfully");
			} catch (ApplicationException e) {
				LOG.error("Error while storing Supplier : " + e.getMessage(), e);
				model.addAttribute("errors", e.getMessage());
				model.addAttribute("supplier", supplier);
				model.addAttribute("countryList", countryService.getActiveCountries());
				model.addAttribute("companyName", buyer.getCompanyName());
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());
				List<IndustryCategory> icList = industryCategoryService.findActiveIndustryCategoryByTenantId(buyer.getId());
				model.addAttribute("icList", icList);

				try {

					if (buyerSettings != null && buyerSettings.getFileAttatchment() != null) {
						byte[] encodeBase64 = Base64.encodeBase64(buyerSettings.getFileAttatchment());
						String base64Encoded = new String(encodeBase64, "UTF-8");
						model.addAttribute("companyLogo", base64Encoded);

					}
				} catch (Exception e1) {
					LOG.error("Error encoding Image" + e1.getMessage(), e1);
				}
				LOG.info("Returning from error....");
				return "register";

			} catch (Exception e) {
				model.addAttribute("errors", messageSource.getMessage("supplier.error.detail", new Object[] { supplier.getCompanyName(), e.getMessage() }, Global.LOCALE));
				model.addAttribute("supplier", supplier);
				model.addAttribute("countryList", countryService.getActiveCountries());
				model.addAttribute("companyName", buyer.getCompanyName());
				BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(buyer.getId());
				List<IndustryCategory> icList = industryCategoryService.findActiveIndustryCategoryByTenantId(buyer.getId());
				model.addAttribute("icList", icList);

				try {

					if (buyerSettings != null && buyerSettings.getFileAttatchment() != null) {
						byte[] encodeBase64 = Base64.encodeBase64(buyerSettings.getFileAttatchment());
						String base64Encoded = new String(encodeBase64, "UTF-8");
						model.addAttribute("companyLogo", base64Encoded);

					}
				} catch (Exception e1) {
					LOG.error("Error encoding Image" + e1.getMessage(), e1);
				}
				LOG.info("Returning from error....");
				return "register";
			}
			return "redirect:buyerSupplierCreationThankyou";
		}

	}

	@RequestMapping(path = "/{buyerId}/buyerSupplierCreationThankyou", method = RequestMethod.GET)
	public String buyerSupplierCreationThankyou(Model model, HttpSession session) {
		LOG.info("supplier Checkout Thank you GET called");
		return "buyerSupplierCreationThankyou";
	}

	private boolean validate(Supplier supplier, Model model) {
		boolean isError = false;

		User user = userService.getPlainUserByLoginId(supplier.getLoginEmail());
		String regex = null;
		String msg = null;
		if (user != null) {
			PasswordSettings settings = passwordSettingService.getPasswordRegex(user.getTenantId());
			if (settings != null) {
				regex = settings.getRegx();
				msg = null;
			}
		}
		if (supplierService.isExistsLoginEmail(supplier.getLoginEmail())) {
			model.addAttribute("errors", messageSource.getMessage("supplier.login.email.exisit", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
			isError = true;
		} else if (userService.getUserByLoginName(supplier.getLoginEmail()) != null) {
			model.addAttribute("errors", messageSource.getMessage("supplier.login.email.exisit", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
			isError = true;
		}
		if (!StringUtils.validatePasswordWithRegx(supplier.getPassword(), regex)) {
			model.addAttribute("errors", StringUtils.checkString(msg).length() > 0 ? msg : messageSource.getMessage("user.password.week", new Object[] { supplier.getLoginEmail() }, Global.LOCALE));
			isError = true;
		}

		if (supplierService.isExists(supplier)) {
			model.addAttribute("errors", messageSource.getMessage("supplier.error.duplicate", new Object[] { supplier.getCompanyName(), supplier.getCompanyRegistrationNumber(), supplier.getRegistrationOfCountry() }, Global.LOCALE));
			isError = true;
		}

		return isError;
	}

}
