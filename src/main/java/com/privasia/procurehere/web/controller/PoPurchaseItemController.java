package com.privasia.procurehere.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.exceptions.ApplicationException;
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
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PoPurchaseItemController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Resource
	MessageSource messageSource;

	@Autowired
	PrService prService;

	@Autowired
	PoService poService;

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

	@RequestMapping(value = "/poPurchaseItemNext/{poId}", method = RequestMethod.GET)
	public String poPurchaseItemNext(@PathVariable String poId,@RequestParam String prId, RedirectAttributes redir) {
		LOG.info("prId : " + prId);
		Pr pr = prService.getPrById(prId);
		Po po = poService.findPoById(poId);

		LOG.info("SUPPLIER NAME : " + pr.getSupplierName());

		if (pr != null) {
			if (pr.getSupplier() == null && pr.getSupplierName() == null) {
				redir.addFlashAttribute("error", messageSource.getMessage("pr.supplier.required", null, Global.LOCALE));
				return "redirect:/buyer/poPurchaseItem/" + poId;
			}
			List<PoItem> itemList = poService.findAllChildPoItemByPoId(poId);
			LOG.info("itemList :" + itemList.size());
			if (CollectionUtil.isEmpty(itemList)) {
				redir.addFlashAttribute("error", messageSource.getMessage("pr.item.required", null, Global.LOCALE));
				return "redirect:/buyer/poPurchaseItem/" + poId;
			} else {
				for (PoItem poItem : itemList) {
					if (poItem.getProductCategory() == null) {
						redir.addFlashAttribute("error", messageSource.getMessage("pr.item.category.required", null, Global.LOCALE));
						return "redirect:/buyer/poPurchaseItem/" + poId;
					}
				}
			}
			po.setPoItemCompleted(Boolean.TRUE);
			poService.updatePo(po);
			LOG.info("po purchaseItem updated");
			return "redirect:/buyer/poRemark/" + poId+"?prId="+prId;
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping(path = "/poPurchaseItem/{poId}", method = RequestMethod.GET)
	public String poPurchaseItem(@PathVariable String poId,@RequestParam String prId, Model model) {
		LOG.info("create po Purchase Item GET called po id :" + poId+" from prId : "+prId);

		Pr prForBudget = null;
		Budget budget = null;
		try {
			// check budget remaining amount
			prForBudget = prService.findPrBUAndCCForBudgetById(prId);
			if(prForBudget != null){
				budget = budgetService.findBudgetByBusinessUnitAndCostCenter(prForBudget.getBusinessUnit().getId(), prForBudget.getCostCenter().getId());
			}

		} catch (Exception e) {
			LOG.error("error in PO Bugdet not created " + e.getStackTrace());
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

		Po po = poService.getLoadedPoById(poId);
		model.addAttribute("po", po);

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
			model.addAttribute("po", po);
		}

		List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("uomList", uomList);
		List<PoItem> polist = poService.findAllPoItemByPoId(poId);
		model.addAttribute("polist", polist);
		model.addAttribute("eventPermissions", poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), poId));
		List<EventSupplierPojo> favouriteSupplierList = null;
		List<PoItem> poItemlist = poService.findAllLoadedChildPoItemByPoId(poId);
		List<String> catList = new ArrayList<>();
		LOG.info("productList=========open=============  : " + poItemlist.size());

		for (PoItem poItem : poItemlist) {
			if (poItem.getProductCategory() != null) {
				if (poItem.getParent() != null && !catList.contains(poItem.getProductCategory().getId())) {
					catList.add(poItem.getProductCategory().getId());
				}
			}
		}

		favouriteSupplierList = favoriteSupplierService.searchPrSuppliers(SecurityLibrary.getLoggedInUserTenantId(), false, null, null);

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

		return "poPurchaseItem";
	}



	@RequestMapping(path = "/viewPoItems", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PoItem>> viewItem(@RequestBody PoItem poItem) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		List<PoItem> polist = null;
		try {
			polist = poService.findAllLoadedChildPoItemByPoId(poItem.getPo().getId());
			return new ResponseEntity<>(polist, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error While viwong po item: " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<>(polist, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/deletePoNewField", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<PoItem>> deletePrNewField(@RequestParam("poId") String poId, @RequestParam("label") String label) {
		LOG.info("label ::" + label + " poId :: " + poId);
		HttpHeaders headers = new HttpHeaders();
		List<PoItem> poItemList = null;
		try {
			if (StringUtils.checkString(label).length() > 0 && StringUtils.checkString(poId).length() > 0) {
				poService.deletefieldInPoItems(label, poId);
				poItemList = poService.findAllPoItemByPoId(poId);
				setLazyPropertiesNull(poItemList);
				headers.add("success", messageSource.getMessage("delete.field.success", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error while deleting Pr New Field " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("delete.field.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(poItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<PoItem>>(poItemList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/deletePoItem/{poId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PoItem>> deletePoItems(@RequestBody String[] items, @PathVariable(name = "poId") String poId) {
		LOG.info("DELETE po poId :: " + poId);
		HttpHeaders headers = new HttpHeaders();

		// Validate input
		if (items == null || items.length == 0) {
			LOG.error("Please select at least one Item");
			headers.add("error", messageSource.getMessage("prItem.delete.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<>(new ArrayList<>(), headers, HttpStatus.BAD_REQUEST);
		}

		try {
			// Perform deletion
			poService.deletePoItems(items, poId);
			headers.add("success", messageSource.getMessage("prItem.delete.success", new Object[] {}, Global.LOCALE));

			return new ResponseEntity<>(new ArrayList<PoItem>(), headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error deleting items: " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.delete.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<>(new ArrayList<>(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	private void nullifyProperties(PoItem poItem) {
		if (poItem.getUnit() != null) {
			poItem.getUnit().createShallowCopy(); // Assuming this method is necessary
		}

		if (poItem.getProductCategory() != null) {
			poItem.getProductCategory().setBuyer(null);
			poItem.getProductCategory().setCreatedBy(null);
			poItem.getProductCategory().setModifiedBy(null);
		}

		if (poItem.getProduct() != null) {
			poItem.getProduct().setCreatedBy(null);
			poItem.getProduct().setModifiedBy(null);
			poItem.getProduct().setProductCategory(null);
			poItem.getProduct().setFavoriteSupplier(null);
		}

		poItem.setParent(null);
		poItem.setBuyer(null);

		// Nullify properties of the associated Po entity
		if (poItem.getPo() != null) {
			poItem.getPo().setCurrency(null);
			poItem.getPo().setSupplier(null);
			poItem.getPo().setCostCenter(null);
			poItem.getPo().setBusinessUnit(null);
			poItem.getPo().setCreatedBy(null);
			poItem.getPo().setModifiedBy(null);
		}

		poItem.setProductContractItem(null);
	}

	/**
	 * @param poItemList
	 */
	private void setLazyPropertiesNull(List<PoItem> poItemList) {
		if (CollectionUtil.isNotEmpty(poItemList)) {
			for (PoItem poItem : poItemList) {
				// Nullify properties to avoid serialization issues
				nullifyProperties(poItem);
				if (CollectionUtil.isNotEmpty(poItem.getChildren())) {
					for (PoItem child : poItem.getChildren()) {
						nullifyProperties(child);
					}
				}
			}
		}
	}



	@RequestMapping(path = "/resetPoItem", method = RequestMethod.POST)
	public String reset(@RequestParam String poId,@RequestParam (required = false) String prId, RedirectAttributes redir) throws JsonProcessingException {
		LOG.info("DELETE po poId :: " + poId);

		List<PoItem> poItemList = null;
		HttpHeaders headers = new HttpHeaders();

		try {
			Po po = poService.findPoById(poId);
			LOG.info("poId+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + po.getId());
			poItemList = poService.deletePoItemsByPoId(poId);
			setLazyPropertiesNull(poItemList);

			po.setSupplier(null);
			po.setSupplierName(null);
			po.setSupplierAddress(null);
			po.setSupplierFaxNumber(null);
			po.setSupplierTaxNumber(null);
			po.setSupplierTelNumber(null);
			po.setSupplierCompleted(null);
			po.setGrandTotal(BigDecimal.ZERO);
			po.setTotal(BigDecimal.ZERO);
			po.setAdditionalTax(BigDecimal.ZERO);

			poService.updatePo(po);
			headers.add("success", messageSource.getMessage("prItem.delete.success", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/poPurchaseItem/" + poId+"?prId="+prId;
		} catch (Exception e) {
			LOG.error("Error deleting items : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.delete.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/poPurchaseItem	/" + poId+"?prId="+prId;
		}
	}

	@RequestMapping(path = "/addNewColumnsPoItem", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PoItem>> addNewColumns(@RequestBody Po po) throws JsonProcessingException {
		LOG.info("addNewColumns getField1Label :" + po.getField1Label() + " getField2Label : " + po.getField2Label() + " getField3Label :" + po.getField3Label() + " getField4Label :" + po.getField4Label());
		HttpHeaders headers = new HttpHeaders();

		// checking duplicate column
		List<String> fieldLabelList = new ArrayList<String>();
		if (po.getField1Label() != null && fieldLabelList.contains(po.getField1Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField1Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField1Label() != null) {
			fieldLabelList.add(po.getField1Label().toLowerCase());
		}
		if (po.getField2Label() != null && fieldLabelList.contains(po.getField2Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField2Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField2Label() != null) {
			fieldLabelList.add(po.getField2Label().toLowerCase());
		}
		if (po.getField3Label() != null && fieldLabelList.contains(po.getField3Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField3Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField3Label() != null) {
			fieldLabelList.add(po.getField3Label().toLowerCase());
		}
		if (po.getField4Label() != null && fieldLabelList.contains(po.getField4Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField4Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField4Label() != null) {
			fieldLabelList.add(po.getField4Label().toLowerCase());
		}

		if (po.getField5Label() != null && fieldLabelList.contains(po.getField5Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField5Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField5Label() != null) {
			fieldLabelList.add(po.getField5Label().toLowerCase());
		}

		if (po.getField6Label() != null && fieldLabelList.contains(po.getField6Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField6Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField6Label() != null) {
			fieldLabelList.add(po.getField6Label().toLowerCase());
		}

		if (po.getField7Label() != null && fieldLabelList.contains(po.getField7Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField7Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField7Label() != null) {
			fieldLabelList.add(po.getField7Label().toLowerCase());
		}

		if (po.getField8Label() != null && fieldLabelList.contains(po.getField8Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField8Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField8Label() != null) {
			fieldLabelList.add(po.getField8Label().toLowerCase());
		}

		if (po.getField9Label() != null && fieldLabelList.contains(po.getField9Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField9Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField9Label() != null) {
			fieldLabelList.add(po.getField9Label().toLowerCase());
		}

		if (po.getField10Label() != null && fieldLabelList.contains(po.getField10Label().toLowerCase())) {
			headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { po.getField10Label() }, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (po.getField10Label() != null) {
			fieldLabelList.add(po.getField10Label().toLowerCase());
		}

		List<PoItem> poItemList = null;
		Po poObj = null;
		if (po.getField1Label() == null && po.getField2Label() == null && po.getField3Label() == null && po.getField4Label() == null && po.getField5Label() == null && po.getField6Label() == null && po.getField7Label() == null && po.getField8Label() == null && po.getField9Label() == null && po.getField10Label() == null) {
			headers.add("error", messageSource.getMessage("new.field.required", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {

			try {
				poObj = poService.getLoadedPoById(po.getId());
				poObj.setField1Label(po.getField1Label());
				poObj.setField2Label(po.getField2Label());
				poObj.setField3Label(po.getField3Label());
				poObj.setField4Label(po.getField4Label());

				poObj.setField5Label(po.getField5Label());
				poObj.setField6Label(po.getField6Label());
				poObj.setField7Label(po.getField7Label());
				poObj.setField8Label(po.getField8Label());
				poObj.setField9Label(po.getField9Label());
				poObj.setField10Label(po.getField10Label());
				poService.updatePo(poObj);

				LOG.info(">>>>>>>>>>>>. here");
				poItemList = poService.findAllPoItemByPoId(poObj.getId());
				if (CollectionUtil.isNotEmpty(poItemList)) {
					setLazyPropertiesNull(poItemList);
				}
				headers.add("success", messageSource.getMessage("new.field.success", new Object[] {}, Global.LOCALE));
			} catch (Exception e) {
				headers.add("error", messageSource.getMessage("new.field.error", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ResponseEntity<List<PoItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<List<PoItem>>(poItemList, headers, HttpStatus.OK);
	}

	/**
	 * @param poItem
	 * @return
	 */
	@RequestMapping(path = "/poOrder", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<PoItem>> prOrder(@RequestBody PoItem poItem) {
		LOG.info("Parent : " + poItem.getParent().getLevel() + " Item Id : " + poItem.getId() + " New Position : " + poItem.getOrder());
		HttpHeaders headers = new HttpHeaders();
		List<PoItem> poList = null;
		try {
			if (StringUtils.checkString(poItem.getId()).length() > 0) {
				PoItem poDbItem = poService.getPoItembyPoItemId(poItem.getId());
				if (poDbItem.getOrder() > 0 && StringUtils.checkString(poItem.getParent().getId()).length() == 0) {
					headers.add("error", messageSource.getMessage("child.reOrder.error", new Object[] { poDbItem.getItemName() }, Global.LOCALE));
					poList = poService.findAllPoItemByPoId(poItem.getPo().getId());
					setLazyPropertiesNull(poList);
					return new ResponseEntity<List<PoItem>>(poList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (poDbItem.getOrder() == 0 && StringUtils.checkString(poItem.getParent().getId()).length() > 0) {
					headers.add("error", messageSource.getMessage("parent.reOrder.error", new Object[] { poDbItem.getItemName() }, Global.LOCALE));
					poList = poService.findAllPoItemByPoId(poItem.getPo().getId());
					setLazyPropertiesNull(poList);
					return new ResponseEntity<List<PoItem>>(poList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				if (poDbItem.getParent() != null && !poDbItem.getParent().getId().equals(poItem.getParent().getId()) && poService.isExists(poItem,false)) {
					throw new ApplicationException(messageSource.getMessage("prItem.save.duplicate", new Object[] { poDbItem.getItemName() }, Global.LOCALE));
				}
				poService.reOrderPoItems(poItem);
				poList = poService.findAllPoItemByPoId(poItem.getPo().getId());
				headers.add("success", messageSource.getMessage("reOrder.success", new Object[] { poItem.getItemName() }, Global.LOCALE));
				setLazyPropertiesNull(poList);
			}
		} catch (Exception e) {
			LOG.error("Error while moving Po Item : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("moving.reOrder.error", new Object[] { e.getMessage() }, Global.LOCALE));
			poList = poService.findAllPoItemByPoId(poItem.getPo().getId());
			setLazyPropertiesNull(poList);
			return new ResponseEntity<List<PoItem>>(poList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<PoItem>>(poList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getPoForNewFields", method = RequestMethod.GET)
	public ResponseEntity<Po> getPrForNewFields(@RequestParam String poId) {
		LOG.info("Po EDIT :: poId" + poId);
		HttpHeaders headers = new HttpHeaders();
		Po po = null;
		try {
			po = poService.getLoadedPoById(poId);
		} catch (Exception e) {
			LOG.error("Error while getting Po Item for add sub item  : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("prItem.pr.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<Po>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (po != null) {
			po.setCurrency(null);
			po.setSupplier(null);
			po.setCreatedBy(null);
			po.setCostCenter(null);
			po.setBusinessUnit(null);
			return new ResponseEntity<Po>(po, HttpStatus.OK);
		} else {
			headers.add("error", messageSource.getMessage("prItem.edit.not.found.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<Po>(null, headers, HttpStatus.NOT_FOUND);
		}
	}
}
