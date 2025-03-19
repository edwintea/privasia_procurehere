package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrContact;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BudgetService;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.ProductCategoryMaintenanceService;
import com.privasia.procurehere.web.editors.FavouriteSupplierEditor;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrSupplierController extends PrBaseController {

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	FavouriteSupplierEditor favouriteSupplierEditor;

	@Autowired
	FavoriteSupplierService supplierService;

	@Autowired
	BudgetService budgetService;

	@ModelAttribute("step")
	public String getStep() {
		return "3";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(FavouriteSupplier.class, favouriteSupplierEditor);
	}

	@RequestMapping(path = "/prSupplier/{prId}", method = RequestMethod.GET)
	public String PrSupplier(@PathVariable String prId, Model model) {
		LOG.info("create pr Supplier GET called pr id :" + prId);
		Pr pr = prService.findPrSupplierByPrId(prId);
		model.addAttribute("pr", pr);
		boolean prItemExists = Boolean.TRUE;
		if (CollectionUtil.isEmpty(pr.getPrItems())) {
			prItemExists = Boolean.FALSE;
		}
		model.addAttribute("prItemExists", prItemExists);
		PrContact prContact = new PrContact();
		prContact.setPr(pr);
		model.addAttribute("prContact", prContact);
		model.addAttribute("prContactList", pr.getPrContacts());
		model.addAttribute("supplierList", favoriteSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), null, null, null));
		model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId));
		return "prSupplier";
	}

	@RequestMapping(path = "/prSupplier", method = RequestMethod.POST)
	public ModelAndView savePrSupplier(@ModelAttribute Pr pr, @RequestParam String supplierChoice, Model model, RedirectAttributes redir, boolean isDraft) {
		LOG.info("create pr Supplier Post called pr id :" + pr.getId() + " supplierChoice :" + supplierChoice);
		try {
			// Pr persistObj = prService.findPrSupplierByPrId(pr.getId());
			super.constructPrSupplierAttributes(model);
			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), pr.getId()));
			// save As Draft
			if (isDraft) {
				pr.setStatus(PrStatus.DRAFT);
				prService.updatePrSupplier(pr, supplierChoice, isDraft);
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { pr.getPrId() }, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			}
			// Temporary assign it for validation
			pr.setPrContacts(prService.findAllPrContactsByPrId(pr.getId()));

			boolean validationError = false;

			if (pr.getSupplier() == null && (StringUtils.checkString(pr.getSupplierName()).length() == 0)) {
				// model.addAttribute("error", "Supplier must be selected");
				model.addAttribute("error", messageSource.getMessage("prsupplier.must.selected.error", new Object[] {}, Global.LOCALE));
				validationError = true;
			} else if (pr.getSupplier() != null) {
				validationError = super.validatePr(pr, model, Pr.PrSupplierList.class);
			} else if (StringUtils.checkString(pr.getSupplierName()).length() > 0) {
				validationError = super.validatePr(pr, model, Pr.PrSupplierManual.class);
			}

			if (validationError) {
				return new ModelAndView("prSupplier", "pr", prService.findPrSupplierByPrId(pr.getId()));
			}
			prService.updatePrSupplier(pr, supplierChoice, isDraft);

		} catch (Exception e) {
			LOG.error("Error in saving Pr supplier " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("pr.supplier.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("prSupplier", "pr", pr);
		}
		return new ModelAndView("redirect:prDelivery/" + pr.getId());
	}

	@RequestMapping(path = "/addPrContactPerson/{prId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrContact>> savePrContact(@PathVariable String prId, @ModelAttribute PrContact prContact) {
		LOG.info("Save pr Contact Called prId :" + prId);
		LOG.info(prContact.getComunicationEmail());
		LOG.info(prContact.getContactNumber());
		LOG.info(prContact.getMobileNumber());
		LOG.info(prContact.getFaxNumber());
		HttpHeaders headers = new HttpHeaders();
		Pr pr = null;
		try {
			if (prService.isExistsPrContact(prContact, prId)) {
				headers.add("error", messageSource.getMessage("pr.contact.duplicate.error", new Object[] { prContact.getContactName() }, Global.LOCALE));
				return new ResponseEntity<List<PrContact>>(new ArrayList<>(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.checkString(prContact.getComunicationEmail()).length() > 0 || StringUtils.checkString(prContact.getContactNumber()).length() > 0 || StringUtils.checkString(prContact.getMobileNumber()).length() > 0 || StringUtils.checkString(prContact.getFaxNumber()).length() > 0) {
				if (StringUtils.checkString(prId).length() > 0) {
					pr = prService.findPrSupplierByPrId(prId);
					if (StringUtils.checkString(prContact.getId()).length() == 0) {
						LOG.info("pr name in contact :  " + pr.getName());
						PrContact prNewContact = new PrContact();
						prNewContact.setTitle(prContact.getTitle());
						prNewContact.setContactName(prContact.getContactName());
						prNewContact.setDesignation(prContact.getDesignation());
						prNewContact.setContactNumber(prContact.getContactNumber());
						prNewContact.setMobileNumber(prContact.getMobileNumber());
						prNewContact.setFaxNumber(prContact.getFaxNumber());
						prNewContact.setComunicationEmail(prContact.getComunicationEmail());
						prNewContact.setPr(pr);
						List<PrContact> prContactList = new ArrayList<>();
						prContactList.add(prNewContact);
						pr.setPrContacts(prContactList);
						prService.updatePr(pr);
						headers.add("success", messageSource.getMessage("pr.contact.save.success", new Object[] { prContact.getContactName() }, Global.LOCALE));
						LOG.info("Save Contact Called");
					} else {
						PrContact persistObj = prService.getPrContactById(prContact.getId());
						persistObj.setTitle(prContact.getTitle());
						persistObj.setContactName(prContact.getContactName());
						persistObj.setDesignation(prContact.getDesignation());
						persistObj.setContactNumber(prContact.getContactNumber());
						persistObj.setMobileNumber(prContact.getMobileNumber());
						persistObj.setFaxNumber(prContact.getFaxNumber());
						persistObj.setComunicationEmail(prContact.getComunicationEmail());
						persistObj.setPr(pr);
						List<PrContact> prContactList = new ArrayList<>();
						prContactList.add(persistObj);
						pr.setPrContacts(prContactList);
						prService.updatePr(pr);
						headers.add("success", messageSource.getMessage("pr.contact.save.update", new Object[] { prContact.getContactName() }, Global.LOCALE));
						LOG.info("Update Contact Called");
					}
				}
			} else {
				headers.add("error", "Please select one of Contact number, Mobile number, Communication email.");
				return new ResponseEntity<List<PrContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			}

		} catch (Exception e) {
			LOG.error("Error While Save Pr contact" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("pr.contact.save.error", new Object[] { prContact.getContactName() }, Global.LOCALE));
			return new ResponseEntity<List<PrContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		pr = prService.findPrSupplierByPrId(prId);
		return new ResponseEntity<List<PrContact>>(pr.getPrContacts(), headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/editPrContact", method = RequestMethod.POST)
	public ResponseEntity<PrContact> editPrContact(@RequestParam(value = "contactId") String contactId) {
		LOG.info("Getting the editPrContact. : " + contactId);
		PrContact prContact = prService.getPrContactById(contactId);
		return new ResponseEntity<PrContact>(prContact, HttpStatus.OK);
	}

	@RequestMapping(path = "/deletePrContact", method = RequestMethod.POST)
	public ResponseEntity<List<PrContact>> deletePrContact(@RequestParam(value = "contactId") String contactId, @RequestParam(value = "prId") String prId) {
		LOG.info("Getting the Delete Contact. : " + contactId);
		HttpHeaders headers = new HttpHeaders();
		Pr pr = null;
		try {
			prService.deletePrContact(contactId);
			LOG.info("Delete the contact");
			headers.add("success", messageSource.getMessage("pr.contact.delete.success", new Object[] {}, Global.LOCALE));
			pr = prService.findPrSupplierByPrId(prId);
			return new ResponseEntity<List<PrContact>>(pr.getPrContacts(), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error While Delete Pr contact" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("pr.contact.delete.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<PrContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/savePrSupplierDraft", method = RequestMethod.POST)
	public ModelAndView savePrDraft(@ModelAttribute Pr pr, @RequestParam String supplierChoice, Model model, RedirectAttributes redir) {
		savePrSupplier(pr, supplierChoice, model, redir, Boolean.TRUE);
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/deletePrSupplerDetails", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrContact>> deletePrSupplerDetails(@RequestParam(name = "prId") String prId, @RequestParam(name = "supplierId", required = false) String supplierId) {
		LOG.info("DELETE pr prId :: " + prId + "== supplierId : " + supplierId);
		HttpHeaders headers = new HttpHeaders();
		try {
			prService.deletePrItemsbyPrId(prId);
			prService.deletePrContactsByPrId(prId);
			List<PrContact> contactList = new ArrayList<>();
			if (StringUtils.checkString(supplierId).length() > 0) {
				FavouriteSupplier favouriteSupplier = supplierService.findFavSupplierByFavSupplierIdForDefault(supplierId);
				if (favouriteSupplier != null) {
					Pr pr = prService.findPrSupplierByPrId(prId);
					LOG.info("pr prId :  " + prId);
					PrContact prNewContact = new PrContact();
					prNewContact.setContactName(favouriteSupplier.getFullName());
					prNewContact.setContactNumber(favouriteSupplier.getCompanyContactNumber());
					prNewContact.setDesignation(favouriteSupplier.getDesignation());
					prNewContact.setComunicationEmail(favouriteSupplier.getCommunicationEmail());
					if (favouriteSupplier.getSupplier() != null) {
						prNewContact.setFaxNumber(favouriteSupplier.getSupplier().getFaxNumber());
						prNewContact.setMobileNumber(favouriteSupplier.getSupplier().getMobileNumber());
					}
					prNewContact.setPr(pr);
					List<PrContact> prContactList = new ArrayList<>();
					prContactList.add(prNewContact);
					pr.setPrContacts(prContactList);
					// saving supplier
					pr.setSupplier(favouriteSupplier);
					prService.updatePr(pr);
					LOG.info("Save Contact Called");
					pr = prService.findPrSupplierByPrId(prId);
					contactList = pr.getPrContacts();
				}
			}
			headers.add("success", messageSource.getMessage("prsupplier.details.delete.success", new Object[] {}, Global.LOCALE));
			LOG.info("DELETEd ");
			return new ResponseEntity<List<PrContact>>(contactList, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error deleting items : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.delete.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PrContact>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/addPrContactPersonByDefault", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrContact>> saveDefaultPrContact(@RequestParam(name = "prId") String prId, @RequestParam(name = "supplierId") String supplierId) {
		LOG.info("saveDefaultPrContact method is called here");
		LOG.info("supplierId" + prId);
		HttpHeaders headers = new HttpHeaders();
		Pr pr = null;
		try {

			if (StringUtils.checkString(prId).length() > 0) {
				pr = prService.findPrSupplierByPrId(prId);
				List<PrContact> prConList = pr.getPrContacts();
				if (CollectionUtil.isNotEmpty(prConList)) {
					for (PrContact prContact : prConList) {
						prService.deletePrContact(prContact.getId());
					}
				}
				if (pr != null)
					if (StringUtils.checkString(supplierId).length() != 0) {
						FavouriteSupplier favouriteSupplier = supplierService.findFavSupplierByFavSupplierIdForDefault(supplierId);
						if (StringUtils.checkString(favouriteSupplier.getId()).length() != 0) {
							LOG.info("pr name in contact :  " + pr.getName());
							PrContact prNewContact = new PrContact();
							prNewContact.setContactName(favouriteSupplier.getFullName());
							prNewContact.setContactNumber(favouriteSupplier.getCompanyContactNumber());
							prNewContact.setDesignation(favouriteSupplier.getDesignation());
							prNewContact.setComunicationEmail(favouriteSupplier.getCommunicationEmail());
							if (favouriteSupplier.getSupplier() != null) {
								prNewContact.setFaxNumber(favouriteSupplier.getSupplier().getFaxNumber());
								prNewContact.setMobileNumber(favouriteSupplier.getSupplier().getMobileNumber());
							}

							prNewContact.setPr(pr);
							List<PrContact> prContactList = new ArrayList<>();
							prContactList.add(prNewContact);
							pr.setPrContacts(prContactList);

							// saving supplier
							pr.setSupplier(favouriteSupplier);
							prService.updatePr(pr);
							LOG.info("Save Contact Called");
						}
					}

			}
		} catch (Exception e) {
			LOG.error("Error saving prcontact items : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.delete.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PrContact>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		pr = prService.findPrSupplierByPrId(prId);
		return new ResponseEntity<List<PrContact>>(pr.getPrContacts(), headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/savePrSupplier", method = RequestMethod.POST)
	public ModelAndView savePrOpenSupplier(@ModelAttribute Pr pr, @RequestParam String supplierChoice, Model model, RedirectAttributes redir, boolean isDraft) {

		LOG.info("my supplier chpoice===========================" + supplierChoice);
		LOG.info("create pr Supplier Post called pr id :" + pr.getId() + " supplierChoice :" + supplierChoice);
		try {

			super.constructPrSupplierAttributes(model);
			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), pr.getId()));

			// save As Draft
			if (isDraft) {

				pr.setStatus(PrStatus.DRAFT);
				prService.updatePrSupplier(pr, supplierChoice, isDraft);
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { pr.getPrId() }, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			}
			// Temporary assign it for validation
			pr.setPrContacts(prService.findAllPrContactsByPrId(pr.getId()));

			LOG.error("pr--------------");

			// boolean validationError = false;

			/*
			 * if (pr.getSupplier() == null && (StringUtils.checkString(pr.getSupplierName()).length() == 0)) {
			 * model.addAttribute("error", "Supplier must be selected"); LOG.error("pr--------------" +
			 * validationError); validationError = true; } else if (pr.getSupplier() != null) { validationError =
			 * super.validatePr(pr, model, Pr.PrSupplierList.class); LOG.error("pr--------------" + validationError); }
			 * else if (StringUtils.checkString(pr.getSupplierName()).length() > 0) { validationError =
			 * super.validatePr(pr, model, Pr.PrSupplierManual.class); LOG.error("pr--------------" + validationError);
			 * }
			 */

			/*
			 * LOG.error("pr--------------" + validationError); if (validationError) { return new
			 * ModelAndView("redirect:prPurchaseItem/" + pr.getId()); }
			 */

			if (pr.getSupplier() != null) {
				List<ProductCategory> productList = productCategoryMaintenanceService.findProductCategoryByTenantIDSupplierID(SecurityLibrary.getLoggedInUserTenantId(), pr.getSupplier().getId());
				if (CollectionUtil.isEmpty(productList)) {
					redir.addFlashAttribute("error", messageSource.getMessage("assign.product.category.supplier", new Object[] {}, Global.LOCALE));
					return new ModelAndView("redirect:prPurchaseItem/" + pr.getId());
				}
			}
			Pr newPr = prService.updatePrSupplier(pr, supplierChoice, isDraft);

			// check budget Remaining amount is less than PR grand Total
			try {
				Pr prForBudget = prService.findPrBUAndCCForBudgetById(StringUtils.checkString(pr.getId()));
				if (prForBudget.getLockBudget()) {
					if (prForBudget.getBusinessUnit() != null && prForBudget.getCostCenter() != null) {
						Budget budget = budgetService.findBudgetByBusinessUnitAndCostCenter(prForBudget.getBusinessUnit().getId(), prForBudget.getCostCenter().getId());
						// convert PR amount if currency is different
						BigDecimal prAfterConversion = null;
						if (null != prForBudget.getConversionRate() && !(0 == prForBudget.getConversionRate().compareTo(BigDecimal.ZERO))) {
							prAfterConversion = prForBudget.getGrandTotal().multiply(prForBudget.getConversionRate());
							LOG.info("**************************prAfterConversion " + prAfterConversion);
						}
						if (budget != null && (!budget.getBudgetOverRun()) && (-1 == budget.getRemainingAmount().compareTo(prAfterConversion != null ? prAfterConversion : prForBudget.getGrandTotal()))) {
							// if PR grandTotal is greater than budget remaining amount and budget overrun is not
							// enabled
							if (null != prAfterConversion) {
								redir.addFlashAttribute("error", messageSource.getMessage("pr.budget.afterConversion.error.save", new Object[] {}, Global.LOCALE));
							} else {
								redir.addFlashAttribute("error", messageSource.getMessage("pr.budget.error.save", new Object[] {}, Global.LOCALE));
							}
							return new ModelAndView("redirect:prPurchaseItem/" + newPr.getId());
						}
					}
				}
			} catch (Exception e) {
				LOG.error("Error in saving Pr with budget " + e.getMessage(), e);
				redir.addFlashAttribute("error", messageSource.getMessage("pr.supplier.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			}

		} catch (Exception e) {
			LOG.error("Error in saving Pr supplier " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("pr.supplier.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("redirect:prPurchaseItem/" + pr.getId());
		}
		return new ModelAndView("redirect:/buyer/purchaseItemNext/" + pr.getId());
	}

	@RequestMapping(value = "/savePrForOpenSupp", method = RequestMethod.POST)
	public ModelAndView savePrForOpenSupp(@ModelAttribute Pr pr, @RequestParam String supplierChoice, Model model, boolean isDraft, RedirectAttributes redir) {
		LOG.info("create pr Supplier Post called pr id :" + pr.getId() + " supplierChoice :" + supplierChoice + " " + isDraft);
		try {
			super.constructPrSupplierAttributes(model);
			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), pr.getId()));
			// save As Draft
			if (isDraft) {
				pr.setStatus(PrStatus.DRAFT);
				prService.updatePrSupplier(pr, supplierChoice, isDraft);
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { pr.getPrId() }, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			}
			// Temporary assign it for validation
			pr.setPrContacts(prService.findAllPrContactsByPrId(pr.getId()));
			LOG.error("pr--------------");
			if (pr.getSupplier() != null) {
				List<ProductCategory> productList = productCategoryMaintenanceService.findProductCategoryByTenantIDSupplierID(SecurityLibrary.getLoggedInUserTenantId(), pr.getSupplier().getId());
				if (CollectionUtil.isEmpty(productList)) {
					redir.addFlashAttribute("error", messageSource.getMessage("assign.product.category.supplier", new Object[] {}, Global.LOCALE));
					return new ModelAndView("redirect:prPurchaseItem/" + pr.getId());
				}
			}
			prService.updatePrSupplier(pr, supplierChoice, isDraft);
			// redir.addFlashAttribute("success", "Supplier saved successfully");
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.saved", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error in saving Pr supplier " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("pr.supplier.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("redirect:prPurchaseItem/" + pr.getId());
		}
		return new ModelAndView("redirect:/buyer/prPurchaseItem/" + pr.getId());
	}

}
