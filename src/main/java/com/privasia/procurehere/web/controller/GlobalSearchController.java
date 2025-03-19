/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ibm.icu.util.Calendar;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.GlobalSearchPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.GlobalSearchService;

/**
 * @author VIPUL
 */
@Controller
@RequestMapping(value = "/search")
public class GlobalSearchController implements Serializable {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	private static final long serialVersionUID = 1024473699180824175L;

	@Autowired
	GlobalSearchService globalSearchService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(value = "/searchGlobal", method = RequestMethod.POST)
	public String search(String opVal, String searchVal, Model model, RedirectAttributes redirectAttrs) {
		LOG.info("  search     " + opVal + "  searchVal  " + searchVal);
		redirectAttrs.addFlashAttribute("opVal", opVal);
		redirectAttrs.addFlashAttribute("searchVal", searchVal);
		return "redirect:searchGlobal";
	}

	@RequestMapping(value = "/searchGlobal", method = RequestMethod.GET)
	public ModelAndView searchDataGlobally(@ModelAttribute("opVal") String opVal, @ModelAttribute("searchVal") String searchVal, Model model) {
		try {
			User loggedInUser = SecurityLibrary.getLoggedInUser();
			if (searchVal != null) {
				switch (opVal) {
				case "option1":
					List<GlobalSearchPojo> event = globalSearchService.findAllRfxEventGlobally(searchVal, SecurityLibrary.getLoggedInUserTenantId(), (loggedInUser.getSupplier() != null ? true : false), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUser().getId());
					model.addAttribute("event", event);
					break;

				case "option2":
					List<Buyer> buyerSearchList = globalSearchService.findAllBuyerGlobally(searchVal);
					model.addAttribute("buyerSearchList", buyerSearchList);
					break;

				case "option3":
					if (loggedInUser.getBuyer() != null) {
						List<FavouriteSupplier> favouriteList = globalSearchService.findAllFavouriteSupplierGlobally(searchVal, SecurityLibrary.getLoggedInUserTenantId());
						List<Supplier> supplierSearchList = new ArrayList<Supplier>();
						for (FavouriteSupplier favuSupp : favouriteList) {
							Supplier sup = favuSupp.getSupplier();
							sup.setFavourite(true);
							sup.setFullName(favuSupp.getFullName());
							sup.setCommunicationEmail(favuSupp.getCommunicationEmail());
							sup.setCompanyContactNumber(favuSupp.getCompanyContactNumber());
							sup.setDesignation(favuSupp.getDesignation());

							supplierSearchList.add(sup);
						}
						model.addAttribute("supplierSearchList", supplierSearchList);
					}
					if (loggedInUser.getFinanceCompany() != null) {
						LOG.info("--------------Finance Global Search---------------------id:" + loggedInUser.getTenantId());
						model.addAttribute("supplierSearchList", globalSearchService.findAllSupplierGloballyForFinance(searchVal, loggedInUser.getTenantId()));
					} else {
						List<Supplier> supplierSearchList = globalSearchService.findAllSupplierGlobally(searchVal);
						model.addAttribute("supplierSearchList", supplierSearchList);
					}
					break;

				case "option4":
					LOG.info("------------------po serch global--------------------");
					boolean flag = false;
					if (loggedInUser.getFinanceCompany() != null) {
						LOG.info("------------------finance po serch global--------------------");
						List<Po> poSearchList = globalSearchService.findAllPrPoGloballyForFinance(searchVal, SecurityLibrary.getLoggedInUserTenantId());

						if (poSearchList.size() > 0) {
							flag = true;
						}
						LOG.info(flag);
						model.addAttribute("flag", flag);
						model.addAttribute("poSearchList", poSearchList);
					} else if (loggedInUser.getSupplier() != null) {
						List<Po> poSearchList = globalSearchService.findAllPoGlobally(searchVal, SecurityLibrary.getLoggedInUserTenantId(), null, null, null, null);
						if (poSearchList != null) {
							LOG.info("poSearchList : " + poSearchList.size());
							if (poSearchList.size() > 0) {
								flag = true;
							}

							LOG.info(flag);
							model.addAttribute("flag", flag);
						}
						model.addAttribute("poSearchList", poSearchList);
					} else {
						List<Pr> prSearchList = globalSearchService.findAllPrPoGlobally(searchVal, SecurityLibrary.getLoggedInUserTenantId(), (loggedInUser.getSupplier() != null ? true : false), SecurityLibrary.ifAnyGranted("ROLE_ADMIN") ? null : SecurityLibrary.getLoggedInUser().getId(), null, null, null, null);
						if (prSearchList != null) {
							LOG.info("prSearchList : " + prSearchList.size());
							if (prSearchList.size() > 0) {
								flag = true;
							}

							LOG.info(flag);
							model.addAttribute("flag", flag);
						}
						model.addAttribute("prSearchList", prSearchList);
					}
					break;
				}
			}

			model.addAttribute("searchVal", searchVal);
			model.addAttribute("opVal", opVal);
		} catch (Exception e) {
			LOG.error("Error while Global search :" + e.getMessage(), e);
		}
		return new ModelAndView("globalSearch");
	}

	@RequestMapping(value = "/searchPrPo", method = RequestMethod.POST)
	public ModelAndView searchPrPo(@ModelAttribute("searchVal") String searchVal, @RequestParam(value = "status", required = false) String status, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "dateRangePrPo", required = false) String daterange, Model model) {
		LOG.info(" PR PO searchVal :" + searchVal + ",  Status :" + status + ",  type  :" + type + ",  daterange-date(mm/dd/yyyy)  :" + daterange);
		try {
			Date startDate = null;
			Date endDate = null;
			try {
				if (StringUtils.checkString(daterange).length() > 0) {
					DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
					String[] dateValue = daterange.split("-");
					startDate = (Date) formatter.parse(dateValue[0]);
					endDate = (Date) formatter.parse(dateValue[1]);

					Calendar startCal = Calendar.getInstance();
					startCal.setTime(startDate);
					startCal.set(Calendar.HOUR, 0);
					startCal.set(Calendar.MINUTE, 0);
					startCal.set(Calendar.SECOND, 0);
					startDate = startCal.getTime();

					Calendar endCal = Calendar.getInstance();
					endCal.setTime(endDate);
					endCal.set(Calendar.HOUR, 23);
					endCal.set(Calendar.MINUTE, 59);
					endCal.set(Calendar.SECOND, 59);
					endDate = endCal.getTime();
					LOG.info("startDate : " + startDate + ", endDate :" + endDate);
				}
			} catch (Exception e) {
				LOG.error("Error In Parsing Date Format" + e.getMessage(), e);
				model.addAttribute("error", "Error " + e.getMessage());
			}
			List<Pr> prSearchList = globalSearchService.findAllPrPoGlobally(searchVal, SecurityLibrary.getLoggedInUserTenantId(), false, SecurityLibrary.ifAnyGranted("ROLE_ADMIN") ? null : SecurityLibrary.getLoggedInUser().getId(), status, type, startDate, endDate);
			model.addAttribute("prSearchList", prSearchList);
		} catch (Exception e) {
			LOG.error("Error while search pr po" + e.getMessage(), e);
			model.addAttribute("error", "Error " + e.getMessage());
		}
		model.addAttribute("searchVal", searchVal);
		model.addAttribute("opVal", "option4");
		return new ModelAndView("globalSearch");
	}

	@RequestMapping(value = "/searchPo", method = RequestMethod.POST)
	public ModelAndView searchPo(@ModelAttribute("searchVal") String searchVal, @RequestParam(value = "status", required = false) String status, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "dateRangePrPo", required = false) String daterange, Model model) {
		LOG.info("  PO searchVal :" + searchVal + ",  Status :" + status + ",  type  :" + type + ",  daterange-date(mm/dd/yyyy)  :" + daterange);
		try {
			Date startDate = null;
			Date endDate = null;
			try {
				if (StringUtils.checkString(daterange).length() > 0) {
					DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
					String[] dateValue = daterange.split("-");
					startDate = (Date) formatter.parse(dateValue[0]);
					endDate = (Date) formatter.parse(dateValue[1]);

					Calendar startCal = Calendar.getInstance();
					startCal.setTime(startDate);
					startCal.set(Calendar.HOUR, 0);
					startCal.set(Calendar.MINUTE, 0);
					startCal.set(Calendar.SECOND, 0);
					startDate = startCal.getTime();

					Calendar endCal = Calendar.getInstance();
					endCal.setTime(endDate);
					endCal.set(Calendar.HOUR, 23);
					endCal.set(Calendar.MINUTE, 59);
					endCal.set(Calendar.SECOND, 59);
					endDate = endCal.getTime();
					LOG.info("startDate : " + startDate + ", endDate :" + endDate);
				}
			} catch (Exception e) {
				LOG.error("Error In Parsing Date Format" + e.getMessage(), e);
				model.addAttribute("error", "Error " + e.getMessage());
			}
			List<Po> poSearchList = globalSearchService.findAllPoGlobally(searchVal, SecurityLibrary.getLoggedInUserTenantId(), status, type, startDate, endDate);
			model.addAttribute("poSearchList", poSearchList);
		} catch (Exception e) {
			LOG.error("Error while search pr po" + e.getMessage(), e);
			model.addAttribute("error", "Error " + e.getMessage());
		}
		model.addAttribute("searchVal", searchVal);
		model.addAttribute("opVal", "option4");
		return new ModelAndView("globalSearch");
	}
}
