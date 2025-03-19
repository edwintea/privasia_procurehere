package com.privasia.procurehere.web.controller;

import com.privasia.procurehere.core.entity.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PoSupplierController extends PrBaseController {

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


	@RequestMapping(path = "/savePoSupplier/{poId}", method = RequestMethod.POST)
	public ModelAndView savePoSupplier(@PathVariable String poId, @RequestParam(required=false) String prId,@RequestParam String supplierChoice, Model model, RedirectAttributes redir, boolean isDraft) {

		LOG.info("my supplier chpoice===========================" + supplierChoice);
		LOG.info("create po Supplier Post called pr id :" + prId + " supplierChoice :" + supplierChoice);

		return new ModelAndView("redirect:/buyer/poPurchaseItemNext/" + poId+"?prId="+prId);
	}
}
