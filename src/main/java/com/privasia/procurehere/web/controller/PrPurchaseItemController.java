package com.privasia.procurehere.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.*;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.*;
import com.privasia.procurehere.web.editors.FavouriteSupplierEditor;
import com.privasia.procurehere.web.editors.ProductCategoryMaintenanceEditor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrPurchaseItemController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Resource
	MessageSource messageSource;

	@Autowired
	PrService prService;

	@Autowired
	ProductCategoryMaintenanceService productCategoryMaintenanceService;

	@Autowired
	ProductCategoryMaintenanceEditor productCategoryMaintenanceEditor;

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	UomService uomService;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	protected FavoriteSupplierService favoriteSupplierService;

	@Autowired
	FavouriteSupplierEditor favouriteSupplierEditor;

	@Autowired
	PrTemplateService prTemplateService;

	@Autowired
	ProductContractItemsService productContractItemsService;

	@Autowired
	BudgetService budgetService;

	@ModelAttribute("step")
	public String getStep() {
		return "5";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(ProductCategory.class, productCategoryMaintenanceEditor);
		binder.registerCustomEditor(FavouriteSupplier.class, favouriteSupplierEditor);
	}

	@RequestMapping(value = "/purchaseItemNext/{prId}", method = RequestMethod.GET)
	public String documentNext(@PathVariable String prId, RedirectAttributes redir) {
		LOG.info("prId : " + prId);
		Pr pr = prService.getPrById(prId);
		if (pr != null) {
			if (pr.getSupplier() == null && pr.getSupplierName() == null) {
				redir.addFlashAttribute("error", messageSource.getMessage("pr.supplier.required", null, Global.LOCALE));
				return "redirect:/buyer/prPurchaseItem/" + prId;
			}
			List<PrItem> itemList = prService.findAllChildPrItemByPrId(prId);
			LOG.info("itemList :" + itemList.size());
			if (CollectionUtil.isEmpty(itemList)) {
				redir.addFlashAttribute("error", messageSource.getMessage("pr.item.required", null, Global.LOCALE));
				return "redirect:/buyer/prPurchaseItem/" + prId;
			} else {
				for (PrItem prItem : itemList) {
					if (prItem.getProductCategory() == null) {
						redir.addFlashAttribute("error", messageSource.getMessage("pr.item.category.required", null, Global.LOCALE));
						return "redirect:/buyer/prPurchaseItem/" + prId;
					}
					if(prItem.getItemName().isEmpty() || prItem.getItemName() == null){
						redir.addFlashAttribute("error", messageSource.getMessage("pr.item.Name.required", null, Global.LOCALE));
						return "redirect:/buyer/prPurchaseItem/" + prId;
					}
				}
			}
			pr.setPrItemCompleted(Boolean.TRUE);
			prService.updatePr(pr);
			LOG.info("pr purchaseItem updated");
			return "redirect:/buyer/prRemark/" + prId;
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(path = "/prPurchaseItem/{prId}", method = RequestMethod.GET)
	public String prPurchaseItem(@PathVariable String prId, Model model) {
		LOG.info("" +
				"create pr Purchase Item GET called pr id :" + prId);

		Pr prForBudget = null;
		Budget budget = null;
		try {
			// check budget remaining amount
			prForBudget = prService.findPrBUAndCCForBudgetById(prId);
			budget = budgetService.findBudgetByBusinessUnitAndCostCenter(prForBudget.getBusinessUnit().getId(), prForBudget.getCostCenter().getId());
		} catch (Exception e) {
			LOG.error("error in PR Bugdet not created " + e.getStackTrace());
		}

		ErpSetup setup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
		if (setup != null) {
			model.addAttribute("erpEnable", setup.getIsErpEnable());
		} else {
			model.addAttribute("erpEnable", false);
		}

		Pr pr = prService.getPlainPrById(prId);
		if (null != budget) {
			pr.setRemainingBudgetAmount(budget.getRemainingAmount());
			pr.setBudgetCurrencyCode(budget.getBaseCurrency().getCurrencyCode());
		}
		Boolean hideSupplier = false;

		if (pr.getSupplier() != null || pr.getSupplierAddress() != null && pr.getSupplierName() != null) {
			hideSupplier = true;
		}
		model.addAttribute("hideSupplier", hideSupplier);

		Pr prObj = prService.getPrById(prId);
		if (null != budget) {
			prObj.setRemainingBudgetAmount(budget.getRemainingAmount());
			prObj.setBudgetCurrencyCode(budget.getBaseCurrency().getCurrencyCode());
		}
		if (prObj.getSupplier() != null) {
			model.addAttribute("pr", prObj);
		} else {
			TableDataInput filter = new TableDataInput();
			filter.setStart(0);
			filter.setLength(10);
			model.addAttribute("pr", pr);
		}
		// model.addAttribute("productItemList",
		// productListMaintenanceService.findProductsByTenantId(SecurityLibrary.getLoggedInUserTenantId()));

		List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("uomList", uomList);
		List<PrItem> prlist = prService.findAllPrItemByPrId(prId);

		model.addAttribute("prlist", prlist);
		model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId));
		List<EventSupplierPojo> favouriteSupplierList = null;
		if (CollectionUtil.isEmpty(prlist) && pr.getSupplier() == null && pr.getSupplierName() == null) {
			favouriteSupplierList = favoriteSupplierService.searchPrSuppliers(SecurityLibrary.getLoggedInUserTenantId(), false, null, null);
		} else {
			List<PrItem> prItemlist = prService.findAllLoadedChildPrItemByPrId(prId);
			List<String> catList = new ArrayList<>();
			LOG.info("productList=========open=============  : " + prItemlist.size());
			for (PrItem prItem : prItemlist) {
				if (prItem.getProductCategory() != null) {
				if (prItem.getParent() != null && !catList.contains(prItem.getProductCategory().getId())) {
					catList.add(prItem.getProductCategory().getId());
				}
			}
			}
			if (CollectionUtil.isNotEmpty(catList)) {
				// favouriteSupplierList =
				// favoriteSupplierService.searchPrSuppliers(SecurityLibrary.getLoggedInUserTenantId(), true,
				// pr.getId(), null);
				favouriteSupplierList = productCategoryMaintenanceService.findFavSupplierByCategoryListForPr(catList);
			} else {
				favouriteSupplierList = favoriteSupplierService.searchPrSuppliers(SecurityLibrary.getLoggedInUserTenantId(), false, null, null);
			}
		}

		// if (CollectionUtil.isEmpty(favouriteSupplierList) && StringUtils.checkString(pr.getSupplierName()).length()
		// == 0) {
		// favouriteSupplierList =
		// favoriteSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), null, null,
		// null);
		// }
		LOG.info("favouriteSupplierList >>>>>>>>>>>>>>>> " + favouriteSupplierList.size());
		model.addAttribute("supplierList", favouriteSupplierList);
		List<ProductCategory> productList = null;
		if (pr.getSupplier() == null) {
			if (pr.getSupplierAddress() != null && pr.getSupplierName() != null) {
				productList = productCategoryMaintenanceService.findProductCategoryByTenantIDSupplierID(SecurityLibrary.getLoggedInUserTenantId(), null);
			} else {
				productList = productCategoryMaintenanceService.findAllActiveProductCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId());
			}
		} else {
			productList = productCategoryMaintenanceService.findProductCategoryByTenantIDSupplierID(SecurityLibrary.getLoggedInUserTenantId(), pr.getSupplier().getId());
		}
		model.addAttribute("productCategoryList", productList);

		return "prPurchaseItem";
	}

	@RequestMapping(value = "searchSuppliersForPr", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<EventSupplierPojo>> searchSuppliersForPr(@RequestParam("searchSupplier") String searchSupplier, @RequestParam("prId") String prId) {
		Long count = prService.findProductCategoryCountByPrId(prId);
		LOG.info("Category count : " + count);
		List<EventSupplierPojo> favouriteSupplierList = null;
		if (count > 0) {
			favouriteSupplierList = favoriteSupplierService.searchPrSuppliers(SecurityLibrary.getLoggedInUserTenantId(), true, prId, searchSupplier);
		} else {
			favouriteSupplierList = favoriteSupplierService.searchPrSuppliers(SecurityLibrary.getLoggedInUserTenantId(), false, null, searchSupplier);
		}

		return new ResponseEntity<List<EventSupplierPojo>>(favouriteSupplierList, HttpStatus.OK);
	}

	@RequestMapping(path = "/createPrItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrItem>> savePrItem(@RequestBody PrItem prItem) throws JsonProcessingException {
		LOG.info("prItem  " + prItem.toLogString());
		HttpHeaders headers = new HttpHeaders();
		List<PrItem> prlist = null;
		try {
			if (doValidate(prItem)) {
				prService.savePrItem(prItem, SecurityLibrary.getLoggedInUser());
				headers.add("success", messageSource.getMessage("prItem.save.success", new Object[] { prItem.getItemName() }, Global.LOCALE));
			} else {
				headers.add("error", messageSource.getMessage("prItem.save.duplicate", new Object[] { prItem.getItemName() }, Global.LOCALE));
				return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			prlist = prService.findAllPrItemByPrId(prItem.getPr().getId());
			setLazyPropertiesNull(prlist);
			return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error While saving pr item :" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/editPrItem", method = RequestMethod.GET)
	public ResponseEntity<PrItem> editPrItem(@RequestParam String prId, @RequestParam String prItemId) {
		LOG.info("editPrItem get called :: prId" + prId + " :: prItemId :: " + prItemId);
		HttpHeaders headers = new HttpHeaders();
		Uom uom = null;
		try {
			PrItem prItem = null;
			prItem = prService.getPrItembyPrIdAndPrItemId(prId, prItemId);
			if (prItem != null && prItem.getUnit() != null) {
				uom = prItem.getUnit().createShallow();
			}
			if (prItem != null) {
				if (prItem.getProductContractItem() != null) {
					LOG.info("=======" + prItem.getProductContractItem().getId());
					prItem.setId(prItemId + "-" + prItem.getProductContractItem().getId());
					prItem.setProductContractItem(null);
				}
				prItem = prItem.createShallowCopy();
				if (prItem.getProductCategory() != null) {
					prItem.getProductCategory().setBuyer(null);
					prItem.getProductCategory().setModifiedBy(null);
					prItem.getProductCategory().setCreatedBy(null);
				}
				prItem.setParent(null);
				prItem.setBuyer(null);
				prItem.getPr().setCurrency(null);
				prItem.getPr().setSupplier(null);
				prItem.getPr().setCreatedBy(null);
				prItem.getPr().setCostCenter(null);
				prItem.getPr().setBusinessUnit(null);
				prItem.getPr().setModifiedBy(null);
				if (prItem.getProduct() != null) {
					prItem.getProduct().setCreatedBy(null);
					prItem.getProduct().setModifiedBy(null);
				}
				// if (prItem.getUnit() != null) {
				// prItem.getUnit().createShallowCopy();
				// }
				if (uom != null) {
					prItem.setUnit(uom);
				}

				return new ResponseEntity<PrItem>(prItem, HttpStatus.OK);
			} else {
				LOG.warn("The PR Item for the specified Pr not found. Bad Request.");
				headers.add("error", messageSource.getMessage("prItem.edit.not.found.error", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<PrItem>(prItem, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error edit items : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.edit.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<PrItem>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updatePrItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrItem>> updatePrItem(@RequestBody PrItem prItem) {
		LOG.info(" prId " + prItem.getId() + " update prItem getParent " + prItem.getParent());
		HttpHeaders headers = new HttpHeaders();
		List<PrItem> prlist = new ArrayList<>();
		String[] rr = null;
		if (prItem.getId().contains("-")) {
			rr = prItem.getId().split("-");
			prItem.setItemId(rr[0]);
			LOG.info("========================>" + rr[0]);
			LOG.info("========================>" + rr[1]);
			prItem.setId(rr[0]);
		}
		try {

			Pr pr = prService.getLoadedPrById(prItem.getPr().getId());

			PrTemplate prTemplate = prTemplateService.getPrTemplateForEditById(pr.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			Boolean contractItemsOnly = Boolean.FALSE;

			// Assuming pr.item contains smart quotes
			prItem.setItemDescription(prService.replaceSmartQuotes(prItem.getItemDescription()));
			prItem.setItemName(prService.replaceSmartQuotes(prItem.getItemName()));

			if (prTemplate.getContractItemsOnly() != null) {
				contractItemsOnly = prTemplate.getContractItemsOnly();
			}
			if (contractItemsOnly) {
				ProductContractItems productContractItem = productContractItemsService.findProductContractItemById(rr[1]);

				if (productContractItem != null && (productContractItem.getProductContract().getContractEndDate() != null && productContractItem.getProductContract().getContractEndDate().after(new Date()))) {
					prItem.setProductContractItem(productContractItem);
					BigDecimal quantity = prItem.getQuantity() != null ? prItem.getQuantity() : BigDecimal.ZERO;
					BigDecimal balanceQuantity = productContractItem.getBalanceQuantity() != null ? productContractItem.getBalanceQuantity() : BigDecimal.ZERO;

					if (quantity.compareTo(balanceQuantity) == 1) {
						headers.add("error", "Specified quantity is more than the balance quantity. The balance quantity is " + balanceQuantity);
						return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					headers.add("error", "Selected Item does not have valid contract");
					return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);

				}
			}

			prItem.setItemTax(prItem != null ? (prItem.getItemTax() != null ? prItem.getItemTax().replaceAll("\\,", "") : "") : "");
			if (prItem.getFreeTextItemEntered() != null) { // prItem.getFreeTextItemEntered() is null in case of update
															// section
				if (!prItem.getFreeTextItemEntered()) {
					LOG.info("Item id outer if " + prItem.getId());
					ProductItem item = productListMaintenanceService.getProductItemByPrItemId(prItem.getId());

					if (item != null) {
						///tmp cmt out for changing item name (without checked freetext)

						/*if (!item.getProductName().equalsIgnoreCase(prItem.getItemName())) {
							headers.add("error", "please select Item from list");
							return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}*/
						if (prItem.getItemName().isEmpty() || item.getProductName() == null) {
							headers.add("error", "please select Item from list");
							return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						prItem.setProduct(item);
						// If Contract Item UOM will take from Contract
						if (prItem.getProductContractItem() != null) {
							prItem.setUnit(prItem.getProductContractItem().getUom());
						} else {
							prItem.setUnit(item.getUom());
						}
						prItem.setItemName(prItem.getItemName());
						prItem.setProductCategory(item.getProductCategory());
					}
					else {
						//headers.add("error", "please select Item from list");
						//return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						//PH-2746, issue #17
						prItem.setItemDescription(prItem.getItemDescription());
						prItem.setItemName(prItem.getItemName());
					}
				} else {
					//ProductCategory cat = productCategoryMaintenanceService.getProductCategoryById(prItem.getProductCategory().getId());
					prItem.setItemDescription(prItem.getItemDescription());
					prItem.setItemName(prItem.getItemName());
					prItem.setProduct(null);
				}
			}

			if (doValidate(prItem)) {
				prService.updatePrItem(prItem);
				headers.add("success", messageSource.getMessage("prItem.update.success", new Object[] { prItem.getItemName() }, Global.LOCALE));
				prlist = prService.findAllPrItemByPrId(prItem.getPr().getId());
				setLazyPropertiesNull(prlist);
				return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.OK);
			} else {
				headers.add("error", messageSource.getMessage("prItem.save.duplicate", new Object[] { prItem.getItemName() }, Global.LOCALE));
				return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Error while updating Pr Items to Buyer : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.update.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/deletePrItem/{prId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrItem>> deletePrItems(@RequestBody String[] items, @PathVariable(name = "prId") String prId) throws JsonProcessingException {
		LOG.info("DELETE pr prId :: " + prId);
		List<PrItem> prItemList = null;
		HttpHeaders headers = new HttpHeaders();
		if (items != null && items.length > 0) {
			try {
				prItemList = prService.deletePrItems(items, prId);
				setLazyPropertiesNull(prItemList);
				headers.add("success", messageSource.getMessage("prItem.delete.success", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				LOG.error("Error deleting items : " + e.getMessage(), e);
				headers.add("error", messageSource.getMessage("prItem.delete.error", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("Please select at least one Item");
			headers.add("error", messageSource.getMessage("prItem.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (prItemList == null) {
			prItemList = new ArrayList<PrItem>();
		}
		return new ResponseEntity<List<PrItem>>(prItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/resetPrItem", method = RequestMethod.POST)
	public String reset(@RequestParam String prId, RedirectAttributes redir) throws JsonProcessingException {
		LOG.info("DELETE pr prId :: " + prId);

		List<PrItem> prItemList = null;
		HttpHeaders headers = new HttpHeaders();
		// if (items != null && items.length > 0) {
		try {
			Pr pr = prService.findPrById(prId);
			LOG.info("prId+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + pr.getId());
			// prService.deletePrItemsbyPrId(prId);
			prItemList = prService.deletePrItemsByPrId(prId);
			setLazyPropertiesNull(prItemList);
			prService.deletePrContactsByPrId(prId);

			pr.setSupplier(null);
			pr.setSupplierName(null);
			pr.setSupplierAddress(null);
			pr.setSupplierFaxNumber(null);
			pr.setSupplierTaxNumber(null);
			pr.setSupplierTelNumber(null);
			pr.setSupplierCompleted(null);
			pr.setGrandTotal(BigDecimal.ZERO);
			pr.setTotal(BigDecimal.ZERO);
			pr.setAdditionalTax(BigDecimal.ZERO);

			prService.updatePr(pr);
			// pr = prService.findPrSupplierByPrId(prId);

			headers.add("success", messageSource.getMessage("prItem.delete.success", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error deleting items : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.delete.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/prPurchaseItem	/" + prId;
		}
		/*
		 * } else { LOG.error("Please select at least one Item"); headers.add("error",
		 * messageSource.getMessage("prItem.delete.required", new Object[] {}, Global.LOCALE)); return new
		 * ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR); }
		 */
		if (prItemList == null) {
			prItemList = new ArrayList<PrItem>();
		}
		return "redirect:/buyer/prPurchaseItem/" + prId;
	}

	@RequestMapping(path = "/deletePrNewField", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<PrItem>> deletePrNewField(@RequestParam("prId") String prId, @RequestParam("label") String label) {
		LOG.info("label ::" + label + " prId :: " + prId);
		HttpHeaders headers = new HttpHeaders();
		List<PrItem> prItemList = null;
		try {
			if (StringUtils.checkString(label).length() > 0 && StringUtils.checkString(prId).length() > 0) {
				prService.deletefieldInPrItems(label, prId);
				prItemList = prService.findAllPrItemByPrId(prId);
				setLazyPropertiesNull(prItemList);
				headers.add("success", messageSource.getMessage("delete.field.success", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while deleting Pr New Field " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("delete.field.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(prItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<PrItem>>(prItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getPrForNewFields", method = RequestMethod.GET)
	public ResponseEntity<Pr> getPrForNewFields(@RequestParam String prId) {
		LOG.info("Pr EDIT :: prId" + prId);
		HttpHeaders headers = new HttpHeaders();
		Pr pr = null;
		try {
			pr = prService.getLoadedPrById(prId);
		} catch (Exception e) {
			LOG.error("Error while getting Pr Item for add sub item  : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.pr.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Pr>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (pr != null) {
			pr.setCurrency(null);
			pr.setSupplier(null);
			pr.setCreatedBy(null);
			pr.setCostCenter(null);
			pr.setBusinessUnit(null);
			return new ResponseEntity<Pr>(pr, HttpStatus.OK);
		} else {
			headers.add("error", messageSource.getMessage("prItem.edit.not.found.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<Pr>(null, headers, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * @param prItemOrder
	 * @return
	 */
	@RequestMapping(path = "/prOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrItem>> prOrder(@RequestBody PrItem prItem) {
		LOG.info("Parent : " + prItem.getParent().getLevel() + " Item Id : " + prItem.getId() + " New Position : " + prItem.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<PrItem> prList = null;
		try {
			if (StringUtils.checkString(prItem.getId()).length() > 0) {
				PrItem prDbItem = prService.getPrItembyPrItemId(prItem.getId());
				if (prDbItem.getOrder() > 0 && StringUtils.checkString(prItem.getParent().getId()).length() == 0) {
					headers.add("error", messageSource.getMessage("child.reOrder.error", new Object[] { prDbItem.getItemName() }, Global.LOCALE));
					prList = prService.findAllPrItemByPrId(prItem.getPr().getId());
					setLazyPropertiesNull(prList);
					return new ResponseEntity<List<PrItem>>(prList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (prDbItem.getOrder() == 0 && StringUtils.checkString(prItem.getParent().getId()).length() > 0) {
					headers.add("error", messageSource.getMessage("parent.reOrder.error", new Object[] { prDbItem.getItemName() }, Global.LOCALE));
					prList = prService.findAllPrItemByPrId(prItem.getPr().getId());
					setLazyPropertiesNull(prList);
					return new ResponseEntity<List<PrItem>>(prList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (prDbItem.getParent() != null && !prDbItem.getParent().getId().equals(prItem.getParent().getId()) && prService.isExists(prItem)) {
					throw new ApplicationException(messageSource.getMessage("prItem.save.duplicate", new Object[] { prDbItem.getItemName() }, Global.LOCALE));
				}
				prService.reOrderPrItems(prItem);
				prList = prService.findAllPrItemByPrId(prItem.getPr().getId());
				headers.add("success", messageSource.getMessage("reOrder.success", new Object[] { prItem.getItemName() }, Global.LOCALE));
				setLazyPropertiesNull(prList);
			}
		} catch (Exception e) {
			LOG.error("Error while moving Pr Item : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("moving.reOrder.error", new Object[] { e.getMessage() }, Global.LOCALE));
			prList = prService.findAllPrItemByPrId(prItem.getPr().getId());
			setLazyPropertiesNull(prList);
			return new ResponseEntity<List<PrItem>>(prList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<PrItem>>(prList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/searchProductItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<ProductItem>> searchProductItem(@RequestParam("productItem") String productItem) {
		LOG.info("searchProductItem post called productItem : " + productItem);
		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		List<ProductItem> productList = productListMaintenanceService.findProductsByNameAndTenantAndFavSupplier(productItem, tenantId, null);
		for (ProductItem productListMaintenance : productList) {
			productListMaintenance.getUom().setCreatedBy(null);
			productListMaintenance.getUom().setModifiedBy(null);
			productListMaintenance.setCreatedBy(null);
			productListMaintenance.setModifiedBy(null);
		}
		LOG.info("productList :" + productList.size());
		return new ResponseEntity<List<ProductItem>>(productList, HttpStatus.OK);
	}

	@RequestMapping(path = "/addNewColumns", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrItem>> addNewColumns(@RequestBody Pr pr) throws JsonProcessingException {
		LOG.info("addNewColumns getField1Label :" + pr.getField1Label() + " getField2Label : " + pr.getField2Label() + " getField3Label :" + pr.getField3Label() + " getField4Label :" + pr.getField4Label());
		HttpHeaders headers = new HttpHeaders();

		// checking duplicate column
		List<String> fieldLabelList = new ArrayList<String>();
		if (pr.getField1Label() != null && fieldLabelList.contains(pr.getField1Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField1Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField1Label() != null) {
			fieldLabelList.add(pr.getField1Label().toLowerCase());
		}
		if (pr.getField2Label() != null && fieldLabelList.contains(pr.getField2Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField2Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField2Label() != null) {
			fieldLabelList.add(pr.getField2Label().toLowerCase());
		}
		if (pr.getField3Label() != null && fieldLabelList.contains(pr.getField3Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField3Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField3Label() != null) {
			fieldLabelList.add(pr.getField3Label().toLowerCase());
		}
		if (pr.getField4Label() != null && fieldLabelList.contains(pr.getField4Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField4Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField4Label() != null) {
			fieldLabelList.add(pr.getField4Label().toLowerCase());
		}

		if (pr.getField5Label() != null && fieldLabelList.contains(pr.getField5Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField5Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField5Label() != null) {
			fieldLabelList.add(pr.getField5Label().toLowerCase());
		}

		if (pr.getField6Label() != null && fieldLabelList.contains(pr.getField6Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField6Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField6Label() != null) {
			fieldLabelList.add(pr.getField6Label().toLowerCase());
		}

		if (pr.getField7Label() != null && fieldLabelList.contains(pr.getField7Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField7Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField7Label() != null) {
			fieldLabelList.add(pr.getField7Label().toLowerCase());
		}

		if (pr.getField8Label() != null && fieldLabelList.contains(pr.getField8Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField8Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField8Label() != null) {
			fieldLabelList.add(pr.getField8Label().toLowerCase());
		}

		if (pr.getField9Label() != null && fieldLabelList.contains(pr.getField9Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField9Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField9Label() != null) {
			fieldLabelList.add(pr.getField9Label().toLowerCase());
		}

		if (pr.getField10Label() != null && fieldLabelList.contains(pr.getField10Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { pr.getField10Label() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (pr.getField10Label() != null) {
			fieldLabelList.add(pr.getField10Label().toLowerCase());
		}

		List<PrItem> prItemList = null;
		Pr prObj = null;
		if (pr.getField1Label() == null && pr.getField2Label() == null && pr.getField3Label() == null && pr.getField4Label() == null && pr.getField5Label() == null && pr.getField6Label() == null && pr.getField7Label() == null && pr.getField8Label() == null && pr.getField9Label() == null && pr.getField10Label() == null) {
			headers.add("error", messageSource.getMessage("new.field.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			try {
				prObj = prService.getLoadedPrById(pr.getId());
				prObj.setField1Label(pr.getField1Label());
				prObj.setField2Label(pr.getField2Label());
				prObj.setField3Label(pr.getField3Label());
				prObj.setField4Label(pr.getField4Label());

				prObj.setField5Label(pr.getField5Label());
				prObj.setField6Label(pr.getField6Label());
				prObj.setField7Label(pr.getField7Label());
				prObj.setField8Label(pr.getField8Label());
				prObj.setField9Label(pr.getField9Label());
				prObj.setField10Label(pr.getField10Label());

				prService.updatePr(prObj);
				prItemList = prService.findAllPrItemByPrId(prObj.getId());
				if (CollectionUtil.isNotEmpty(prItemList)) {
					setLazyPropertiesNull(prItemList);
				}
				headers.add("success", messageSource.getMessage("new.field.success", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				headers.add("error", messageSource.getMessage("new.field.error", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<List<PrItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<List<PrItem>>(prItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateAdditionalTax", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<BigDecimal>> updateAdditionalTax(@RequestParam("additionalTax") String additionalTax, @RequestParam("taxDescription") String taxDescription, @RequestParam("prId") String prId) {
		LOG.info("additionalTax : " + additionalTax + " prId :" + prId + "taxDescription :" + taxDescription);
		HttpHeaders headers = new HttpHeaders();
		Pr pr = null;
		List<BigDecimal> list = new ArrayList<>();
		try {
			pr = prService.getPrById(prId);
			pr.setTaxDescription(taxDescription);
			try {
				additionalTax = additionalTax.replace(",", "");
				pr.setAdditionalTax(new BigDecimal(additionalTax));
				pr.setGrandTotal(pr.getTotal().add(new BigDecimal(additionalTax)));
			} catch (Exception e) {
				throw new NotAllowedException("Additional tax \"" + additionalTax + "\" should be numbers only");
			}
			if (CollectionUtil.isNotEmpty(validatePr(pr, Pr.PrPurchaseItem.class))) {
				String message = ", ";
				for (String errMessage : validatePr(pr, Pr.PrPurchaseItem.class)) {
					message = errMessage + message;
				}
				LOG.error("message :" + message);
				headers.add("error", messageSource.getMessage("--------", new Object[] {}, "Error :" + message, Global.LOCALE));
				return new ResponseEntity<List<BigDecimal>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			pr = prService.updatePr(pr);
			list.add(pr.getGrandTotal());
			list.add(new BigDecimal(pr.getDecimal()));
		} catch (Exception e) {
			LOG.error("ERROR WHile updating addtax :" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("--------", new Object[] {}, "Error :" + e.getMessage(), Global.LOCALE));
			return new ResponseEntity<List<BigDecimal>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<BigDecimal>>(list, headers, HttpStatus.OK);
	}

	/**
	 * @param prItem
	 * @return
	 */
	private boolean doValidate(PrItem prItem) {
		boolean validate = !prService.isExists(prItem);
        return validate;
	}

	/**
	 * @param prItemList
	 */
	private void setLazyPropertiesNull(List<PrItem> prItemList) {
		if (CollectionUtil.isNotEmpty(prItemList)) {
			for (PrItem pritem : prItemList) {

				if (pritem.getUnit() != null) {
					pritem.getUnit().createShallowCopy();
				}

				if (pritem.getProductCategory() != null) {
					pritem.getProductCategory().setBuyer(null);
					pritem.getProductCategory().setCreatedBy(null);
					pritem.getProductCategory().setModifiedBy(null);
				}
				if (pritem.getProduct() != null) {
					pritem.getProduct().setCreatedBy(null);
					pritem.getProduct().setModifiedBy(null);
					pritem.getProduct().setProductCategory(null);
					pritem.getProduct().setFavoriteSupplier(null);
				}

				pritem.setParent(null);
				pritem.setBuyer(null);
				pritem.getPr().setCurrency(null);
				pritem.getPr().setSupplier(null);
				pritem.getPr().setCostCenter(null);
				pritem.getPr().setBusinessUnit(null);
				pritem.getPr().setCreatedBy(null);
				pritem.getPr().setModifiedBy(null);
				pritem.setProductContractItem(null);
				if (CollectionUtil.isNotEmpty(pritem.getChildren()))
					for (PrItem child : pritem.getChildren()) {
						child.getPr().setCurrency(null);
						child.getPr().setCreatedBy(null);
						child.getPr().setModifiedBy(null);
						child.setProductContractItem(null);
						child.setBuyer(null);
						if (child.getProduct() != null) {
							child.getProduct().setCreatedBy(null);
							child.getProduct().setModifiedBy(null);
							child.getProduct().setProductCategory(null);
							child.getProduct().setFavoriteSupplier(null);
						}

						if (child.getUnit() != null) {
							child.getUnit().createShallowCopy();
						}

						if (child.getProductCategory() != null) {
							child.getProductCategory().setBuyer(null);
							child.getProductCategory().setCreatedBy(null);
							child.getProductCategory().setModifiedBy(null);
						}

					}
			}
		}
	}

	/**
	 * @param pr
	 */
	public List<String> validatePr(Pr pr, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<Pr>> constraintViolations = validator.validate(pr, validations);
		for (ConstraintViolation<Pr> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			System.out.println("Error : " + cv.getMessage() + " : " + cv.getPropertyPath());
			errorList.add(cv.getMessage());
		}
		return errorList;
	}

	@RequestMapping(value = "/addSubItem", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<ProductItem>> addSubItem(@RequestParam String supplierId) {
		try {
			List<ProductItem> productList = productListMaintenanceService.findProductsByNameAndTenantAndFavSupplier(null, SecurityLibrary.getLoggedInUserTenantId(), supplierId);
			for (ProductItem productItem : productList) {
				productItem.setCreatedBy(null);
				productItem.setModifiedBy(null);
				productItem.setUom(null);

			}
			LOG.info("addSubitem method called " + supplierId);

			return new ResponseEntity<List<ProductItem>>(productList, HttpStatus.OK);

		} catch (Exception e) {
			LOG.info("Error in geting productList" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<ProductItem>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/addCategory", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<ProductCategory>> addCategory(@RequestParam String supplierId) {
		try {
			List<ProductCategory> productList = productCategoryMaintenanceService.findProductCategoryByTenantIDSupplierID(SecurityLibrary.getLoggedInUserTenantId(), supplierId);
			LOG.info("size of Product Category +++++++++++++++++++++++++++++++++++++++++++++++++++" + productList.size());
			for (ProductCategory productItem : productList) {

				LOG.info("product category+++++++++++++++++++++productItem+++++++++++++++++++++++" + productItem.getProductName());
				productItem.setCreatedBy(null);
				productItem.setModifiedBy(null);

			}
			LOG.info("add ProductCategory method called " + supplierId);

			return new ResponseEntity<List<ProductCategory>>(productList, HttpStatus.OK);

		} catch (Exception e) {
			LOG.info("Error in geting Product Category" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<ProductCategory>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/addItemByCategory", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<ProductItem>> addItemByCategory(@RequestParam String supplierId, @RequestParam String productCategoryId) {
		try {
			List<ProductItem> productItemList = productCategoryMaintenanceService.findProductsBySupplierIdAndCategoryId(SecurityLibrary.getLoggedInUserTenantId(), supplierId, productCategoryId);
			LOG.info("size of Product Category +++++++++++++++++++++++++++++++++++++++++++++++++++" + productItemList.size());
			for (ProductItem productItem : productItemList) {

				LOG.info("product category+++++++++++++++++++++productItem+++++++++++++++++++++++" + productItem.getProductName());
				productItem.setCreatedBy(null);
				productItem.setModifiedBy(null);
				productItem.setUom(null);

			}
			LOG.info("add ProductCategory method called " + supplierId);

			return new ResponseEntity<List<ProductItem>>(productItemList, HttpStatus.OK);

		} catch (Exception e) {
			LOG.info("Error in geting Product Category" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<ProductItem>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/addCategoryByItem", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<ProductCategory>> addCategoryByItem(@RequestParam String itemId) {
		try {
			List<ProductCategory> productList = productCategoryMaintenanceService.findProductsCategoryByTenantAndItemId(SecurityLibrary.getLoggedInUserTenantId(), itemId);
			LOG.info("size of Product Category +++++++++++++++++++++++++++++++++++++++++++++++++++" + productList.size());
			for (ProductCategory productItem : productList) {

				LOG.info("product category+++++++++++++++++++++productItem+++++++++++++++++++++++" + productItem.getProductName());
				productItem.setCreatedBy(null);
				productItem.setModifiedBy(null);

			}
			LOG.info("add ProductCategory method called " + itemId);

			return new ResponseEntity<List<ProductCategory>>(productList, HttpStatus.OK);

		} catch (Exception e) {
			LOG.info("Error in geting Product Category" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<ProductCategory>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/getProductItem", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<Object> getProductItem(@RequestParam(name = "supplierId", required = false) String supplierId, @RequestParam(name = "categoryId", required = false) String categoryId) {
		try {

			LOG.info("productCategory:" + categoryId);
			LOG.info("supplierId:" + supplierId);

			List<ProductItem> productList = productCategoryMaintenanceService.findProductsBySupplierIdAndCategoryId(SecurityLibrary.getLoggedInUserTenantId(), supplierId, categoryId);
			/*
			 * if (supplierId == null && categoryId == null) { productList =
			 * productListMaintenanceService.findProductsByTenantId(SecurityLibrary.getLoggedInUserTenantId()); } else
			 * if (supplierId != null && categoryId == null) { productList =
			 * productCategoryMaintenanceService.findProductsByCategoryAndSupplierId(SecurityLibrary.
			 * getLoggedInUserTenantId(), supplierId, null); } else { productList =
			 * productCategoryMaintenanceService.findProductsByCategoryAndSupplierId(SecurityLibrary.
			 * getLoggedInUserTenantId(), supplierId, categoryId); }
			 */
			List<ProductItemPojo> productListPojo = new ArrayList<>();
			for (ProductItem productItem : productList) {

				ProductItemPojo item = new ProductItemPojo();
				item.setId(productItem.getId());
				item.setFavoriteSupplier(productItem.getFavoriteSupplier() != null ? productItem.getFavoriteSupplier().getSupplier().getCompanyName() : "");

				item.setItemCategory(productItem.getProductCategory().getProductName());
				item.setUnitPrice(productItem.getUnitPrice());
				item.setItemName(productItem.getProductName());
				productListPojo.add(item);
			}

			// ObjectMapper sMapper = new ObjectMapper();
			// sMapper.writeValueAsString(productList);

			// LOG.info("============================" + sMapper.toString());

			return new ResponseEntity<Object>(productListPojo, HttpStatus.OK);

		} catch (Exception e) {
			LOG.info("Error in geting productList" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Object>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// for jquery input picker
	@RequestMapping(value = "/getProdItem", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<Object> getProductItemForInputpicker(@RequestParam(name = "supplierId", required = false) String supplierId, @RequestParam(name = "categoryId", required = false) String categoryId, @RequestParam(name = "q", required = false) String searchParam, @RequestParam(name = "supplierName", required = false) String supplierName, @RequestParam(name = "prId") String prId, Model model) {
		try {
			LOG.info("productCategory:" + categoryId);
			LOG.info("supplierId:" + supplierId);
			LOG.info("PrId" + prId);
			Pr pr = prService.getLoadedPrById(prId);
			String unitName = "";
			if (pr.getBusinessUnit() != null) {
				unitName = pr.getBusinessUnit().getUnitName();
			}
			List<ProductItemPojo> productListPojo = new ArrayList<>();
			List<FavouriteSupplier> favouriteSupplierList = null;
			List<String> catList = new ArrayList<>();
			List<String> slist = new ArrayList<String>();
			if (StringUtils.checkString(supplierId).length() <= 0 && pr.getSupplier() == null && pr.getSupplierName() == null) {
				List<PrItem> prlist = prService.findAllLoadedChildPrItemByPrId(prId);
				for (PrItem prItem : prlist) {
					if (prItem.getParent() != null) {
						catList.add(prItem.getProductCategory().getId());
					}
				}
			}
			LOG.info("=======" + catList.size());
			if (CollectionUtil.isNotEmpty(catList)) {
				favouriteSupplierList = productCategoryMaintenanceService.findFavSupplierByCategoryListId(catList);
			} else {

				if (StringUtils.checkString(categoryId).length() > 0)
					catList.add(categoryId);
			}
			if (CollectionUtil.isNotEmpty(favouriteSupplierList)) {
				for (FavouriteSupplier favouriteSupplier : favouriteSupplierList) {
					slist.add(favouriteSupplier.getId());
				}
			} else {
				if (StringUtils.checkString(supplierId).length() > 0)
					slist.add(supplierId);
			}

			LOG.info("=======" + catList.size());
			LOG.info("=======" + slist.size());

			if (!prTemplateService.validateContractItemSetting(prId)) {
				List<ProductItem> productList = productCategoryMaintenanceService.searchProductsBySupplierIdList(SecurityLibrary.getLoggedInUserTenantId(), slist, searchParam, catList);
				for (ProductItem productItem : productList) {
					ProductItemPojo item = new ProductItemPojo();
					item.setId(productItem.getId());
					item.setFavoriteSupplier(productItem.getFavoriteSupplier() != null ? productItem.getFavoriteSupplier().getSupplier().getCompanyName() : "");
					item.setItemCategory(productItem.getProductCategory().getProductName());
					item.setUnitPrice(productItem.getUnitPrice());
					item.setItemName(productItem.getProductName());
					item.setContractItem(productItem.isContractItem());
					productListPojo.add(item);
				}
			} else {
				List<ProductContractItems> productContractList = productCategoryMaintenanceService.searchProductContractBySupplierIdList(SecurityLibrary.getLoggedInUserTenantId(), slist, searchParam, catList, unitName);
				for (ProductContractItems productItem : productContractList) {
					ProductItemPojo item = new ProductItemPojo();
					item.setId(productItem.getProductItem().getId() + "-" + productItem.getId());
					item.setFavoriteSupplier(productItem.getSupplier() != null ? productItem.getSupplier().getSupplier().getCompanyName() : "");
					item.setItemCategory(productItem.getProductCategory().getProductName());
					item.setUnitPrice(productItem.getUnitPrice());
					item.setItemName(productItem.getProductName());
					item.setContractItem(Boolean.TRUE);
					item.setBalanceQuantity(productItem.getBalanceQuantity());
					item.setStorageLocation(productItem.getStorageLocation());
					productListPojo.add(item);
				}
			}
			return new ResponseEntity<Object>(productListPojo, HttpStatus.OK);

		} catch (

		Exception e) {
			LOG.info("Error in geting productList" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Object>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/addCategoryAndSupplierByItem", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> addCategoryAndSupplierByItem(@RequestParam String itemId, @RequestParam String prId, RedirectAttributes redir) {
		try {

			HashMap<String, Object> hmap = new HashMap<String, Object>();
			boolean flag = false;
			boolean sflag = false;

			Pr pr = prService.getLoadedPrById(prId);

			ProductItem item = new ProductItem();
			ProductContractItems productContractItems = new ProductContractItems();
			List<ProductCategory> productList = new ArrayList<ProductCategory>();

			List<String> catList = new ArrayList<>();

			if (pr != null) {
				List<PrItem> prlist = prService.findAllLoadedChildPrItemByPrId(prId);
				if (pr.getSupplier() != null && pr.getSupplierName() != null) {
					for (PrItem prItem : prlist) {
						if (prItem.getParent() != null) {
							catList.add(prItem.getProductCategory().getId());
						}
					}
				}
			}

			List<SupplierPojo> supplierList = new ArrayList<SupplierPojo>();
			List<FavouriteSupplier> favouriteSupplierList = null;
			SupplierPojo supplierPojo = new SupplierPojo();
			List<Uom> uomTextList = new ArrayList<>();

			Boolean contractItemsOnly = prTemplateService.validateContractItemSetting(prId);
			if (Boolean.FALSE == contractItemsOnly) {
				item = productListMaintenanceService.findProductItembyId(itemId);
				productList.add(item.getProductCategory());
				if (CollectionUtil.isNotEmpty(catList)) {
					favouriteSupplierList = productCategoryMaintenanceService.findFavSupplierByCategoryListId(catList);
				} else {
					if (item.getFavoriteSupplier() != null) {
						sflag = true;
						supplierPojo.setCompanyName(item.getFavoriteSupplier().getSupplier().getCompanyName());
						supplierPojo.setId(item.getFavoriteSupplier().getId());
						List<ProductCategory> supplierproductList = productCategoryMaintenanceService.findProductCategoryByTenantIDSupplierID(SecurityLibrary.getLoggedInUserTenantId(), item.getFavoriteSupplier().getId());
                        flag = CollectionUtil.isEmpty(supplierproductList);

						supplierList.add(supplierPojo);
					} else {
						if (item.getProductCategory() != null) {
							favouriteSupplierList = productCategoryMaintenanceService.findFavSupplierByCategoryId(item.getProductCategory().getId(), null);

						}
					}
				}

				if (item.getUom() != null) {
					item.getUom().setBuyer(null);
					item.getUom().setCreatedBy(null);
					item.getUom().setModifiedBy(null);
					item.getUom().setOwner(null);
					uomTextList.add(item.getUom());

				}
			} else {
				String[] rr = itemId.split("-");
				item = productListMaintenanceService.findProductItembyId(rr[0]);
				productContractItems = productContractItemsService.findProductContractItemById(rr[1]);
				productList.add(productContractItems.getProductCategory());

				if (CollectionUtil.isNotEmpty(catList)) {
					favouriteSupplierList = productCategoryMaintenanceService.findFavSupplierByCategoryListId(catList);
				} else {
					if (productContractItems.getProductContract().getSupplier() != null) {
						sflag = true;
						supplierPojo.setCompanyName(productContractItems.getProductContract().getSupplier().getSupplier().getCompanyName());
						supplierPojo.setId(productContractItems.getProductContract().getSupplier().getId());
						List<ProductCategory> supplierproductList = productContractItemsService.findProductContractByTenantIDSupplierID(SecurityLibrary.getLoggedInUserTenantId(), productContractItems.getProductContract().getSupplier().getId());
                        flag = CollectionUtil.isEmpty(supplierproductList);

						supplierList.add(supplierPojo);
					} else {
						if (productContractItems.getProductCategory() != null) {
							favouriteSupplierList = productCategoryMaintenanceService.findFavSupplierByCategoryId(item.getProductCategory().getId(), null);
						}
					}
				}

				if (productContractItems.getUom() != null) {
					productContractItems.getUom().setBuyer(null);
					productContractItems.getUom().setCreatedBy(null);
					productContractItems.getUom().setModifiedBy(null);
					productContractItems.getUom().setOwner(null);
					uomTextList.add(productContractItems.getUom());

				}

			}

			if (CollectionUtil.isNotEmpty(favouriteSupplierList)) {
				for (FavouriteSupplier favouriteSupplier : favouriteSupplierList) {

					SupplierPojo pojo = new SupplierPojo();
					pojo.setCompanyName(favouriteSupplier.getSupplier().getCompanyName());
					pojo.setId(favouriteSupplier.getId());
					supplierList.add(pojo);
				}
			}
			for (ProductCategory productItem : productList) {
				productItem.setCreatedBy(null);
				productItem.setModifiedBy(null);
			}

			hmap.put("productList", productList);
			hmap.put("supplierList", supplierList);
			hmap.put("isSpllierEmptyCategoty", flag);

			hmap.put("uomTextList", uomTextList);
			ProductItemPojo itemPojo = new ProductItemPojo();
			itemPojo.setId(item.getId());
			if(item.getProductName()!=null) {
				itemPojo.setItemName(item.getProductName());
			}
			if (Boolean.FALSE == contractItemsOnly) {
				itemPojo.setUnitPrice(item.getUnitPrice());
			} else {
				itemPojo.setUnitPrice(productContractItems.getUnitPrice());
				itemPojo.setBalanceQuantity(productContractItems.getBalanceQuantity());
				itemPojo.setStorageLocation(productContractItems.getStorageLocation() != null ? productContractItems.getStorageLocation() : "");
			}
			itemPojo.setTax(item.getTax());
			itemPojo.setContractItem(item.isContractItem());
			hmap.put("isSupplier", sflag);
			hmap.put("item", itemPojo);

			return new ResponseEntity<HashMap<String, Object>>(hmap, HttpStatus.OK);

		} catch (Exception e) {
			LOG.info("Error in geting Product Category" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<HashMap<String, Object>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(path = "/savePrItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PrItem>> createPrItem(@RequestBody PrItem prItem, RedirectAttributes redir) throws JsonProcessingException {

		LOG.info("prItem  " + prItem.toLogString());
		HttpHeaders headers = new HttpHeaders();
		List<PrItem> prlist = null;
		try {

			if (prTemplateService.validateContractItemSetting(prItem.getPr().getId())) {
				String[] rr = prItem.getItemId().split("-");
				prItem.setItemId(rr[0]);

				LOG.info("========================>" + rr[0]);
				LOG.info("========================>" + rr[1]);

				ProductContractItems productContractItem = productContractItemsService.findProductContractItemById(rr[1]);

				if (productContractItem != null && (productContractItem.getProductContract().getContractEndDate() != null && productContractItem.getProductContract().getContractEndDate().after(new Date()))) {
					LOG.info("========================>" + productContractItem.getId());
					prItem.setProductContractItem(productContractItem);
					BigDecimal quantity = prItem.getQuantity() != null ? prItem.getQuantity() : BigDecimal.ZERO;
					BigDecimal balanceQuantity = productContractItem.getBalanceQuantity() != null ? productContractItem.getBalanceQuantity() : BigDecimal.ZERO;

					if (quantity.compareTo(balanceQuantity) == 1) {
						headers.add("error", "Specified quantity is more than the balance quantity. The balance quantity is " + balanceQuantity);
						return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
				} else {
					headers.add("error", "Selected Item does not have valid contract");
					return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);

				}
			}
			// Assuming pr.item contains smart quotes
			prItem.setItemDescription(prService.replaceSmartQuotes(prItem.getItemDescription()));
			prItem.setItemName(prService.replaceSmartQuotes(prItem.getItemName()));
			prItem.setItemTax(prItem != null ? (prItem.getItemTax() != null ? prItem.getItemTax().replaceAll("\\,", "") : "") : "");

			if (!prItem.getFreeTextItemEntered()) {
				LOG.info("Item id outer if " + prItem.getItemId());
				ProductItem item = productListMaintenanceService.getProductCategoryById(prItem.getItemId());

				if (item != null) {
					LOG.info("Item id 2nd outer if " + prItem.getId());
					if (!item.getProductName().equalsIgnoreCase(prItem.getItemName())) {
						LOG.info(item.getProductName() + "=======" + prItem.getItemName());
						LOG.info("Item id 3rd outer if " + prItem.getId());
						headers.add("error", "please select Item from list");
						return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					prItem.setProduct(item);
					// If contract item take UOM from contract Item
					if (prItem.getProductContractItem() != null) {
						prItem.setUnit(prItem.getProductContractItem().getUom());
					} else {
						prItem.setUnit(item.getUom());
					}
					prItem.setItemName(item.getProductName());
					prItem.setProductCategory(item.getProductCategory());
				} else {

					LOG.info("Item id outer else  " + prItem.getId());
					headers.add("error", "please select Item from list");
					return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {

				LOG.info("==========getProductCategory============" + prItem.getProductCategory().getId());
				ProductCategory cat = productCategoryMaintenanceService.getProductCategoryById(prItem.getProductCategory().getId());
				prItem.setItemDescription(prItem.getItemDescription());
				prItem.setItemName(prItem.getItemName());
				prItem.setProduct(null);
			}

			if (doValidate(prItem)) {

				LOG.info("IF block pr Purchase Controller ");
				prService.savePrItem(prItem, SecurityLibrary.getLoggedInUser());
				headers.add("success", messageSource.getMessage("prItem.save.success", new Object[] { prItem.getItemName() }, Global.LOCALE));
			} else {
				LOG.info("else block pr Purchase Controller ");
				headers.add("error", messageSource.getMessage("prItem.save.duplicate", new Object[] { prItem.getItemName() }, Global.LOCALE));
				return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			LOG.info("IF block pr Purchase Controller ");
			Pr pr = prService.getLoadedPrById(prItem.getPr().getId());
			if (pr.getSupplier() == null && StringUtils.checkString(prItem.getSupplierId()).length() > 0) {
				FavouriteSupplier supplier = favoriteSupplierService.findFavSupplierByFavSuppId(prItem.getSupplierId(), SecurityLibrary.getLoggedInUserTenantId());
				if (supplier != null) {
					pr.setSupplier(supplier);
					prService.updatePr(pr);
					LOG.info("save supplier done");
				}
			}

			prlist = prService.findAllPrItemByPrId(prItem.getPr().getId());
			for (PrItem prItem2 : prlist) {
				LOG.info("outside if block of prPurchaseController");
				prItem2.setSupplierId(prItem.getSupplierId());
			}

			setLazyPropertiesNull(prlist);

			return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error While saving pr item :" + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PrItem>>(prlist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/getSupplierBycategory", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierPojo>> addsupplierByCategory(@RequestParam String productCategoryId, @RequestParam String supplierId) {

		try {
			List<FavouriteSupplier> favouriteSupplierList = productCategoryMaintenanceService.findFavSupplierByCategoryId(productCategoryId, supplierId);
			List<SupplierPojo> supplierList = new ArrayList<SupplierPojo>();
			for (FavouriteSupplier productItem : favouriteSupplierList) {

				SupplierPojo supplierPojo = new SupplierPojo();
				supplierPojo.setId(productItem.getId());
				supplierPojo.setCompanyName(productItem.getSupplier().getCompanyName());
				supplierList.add(supplierPojo);
			}
			return new ResponseEntity<List<SupplierPojo>>(supplierList, HttpStatus.OK);
		} catch (Exception e) {
			LOG.info("Error in geting get Supplier By category" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error in geting get Supplier By category");
			return new ResponseEntity<List<SupplierPojo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/getUom", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<List<UomPojo>> getUom() {
		try {
			// UomService uomService = new UomServiceImpl();
			List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
			/*
			 * for (Uom uom : uomList) { uom.setCreatedBy(null); uom.setModifiedBy(null); uom.setBuyer(null); }
			 */
			return new ResponseEntity<List<UomPojo>>(uomList, HttpStatus.OK);

		} catch (

		Exception e) {
			LOG.info("Error in geting productList" + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while getting states for Country " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.productItem.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<UomPojo>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
