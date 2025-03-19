package com.privasia.procurehere.web.controller;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.web.editors.BuyerAddressEditor;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PrDeliveryController extends PrBaseController {

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	BuyerAddressEditor buyerAddressEditor;

	@ModelAttribute("step")
	public String getStep() {
		return "4";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(BuyerAddress.class, buyerAddressEditor);
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy");
		deliveryDate.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "deliveryTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "deliveryDate", new CustomDateEditor(deliveryDate, true));
	}

	@RequestMapping(path = "/prDelivery/{prId}", method = RequestMethod.GET)
	public String prDelivery(@PathVariable String prId, Model model) {
		LOG.info("create pr Delivery GET called pr id :" + prId);
		Pr pr = prService.getLoadedPrById(prId);
		pr.setDeliveryTime(pr.getDeliveryDate());
		model.addAttribute("pr", pr);
		List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("addressList", addressList);
		model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), prId));
		return "prDelivery";
	}

	@RequestMapping(path = "/prDelivery", method = RequestMethod.POST)
	public ModelAndView savePrDelivery(@ModelAttribute Pr pr, Model model, RedirectAttributes redir, HttpSession session, boolean isDraft) {
		LOG.info("create pr Delivery POST called pr id :" + pr.getId());

		Pr persistObj = prService.getLoadedPrById(pr.getId());
		try {

			if (!isDraft && super.validatePr(pr, model, Pr.PrDelivery.class)) {
				List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("addressList", addressList);
				return new ModelAndView("prDelivery", "pr", pr);
			}
			// persistObj.setDeliveryDate(pr.getDeliveryDate());

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			Date prDeliveryDateTime = null;
			if (pr.getDeliveryDate() != null && pr.getDeliveryTime() != null) {
				prDeliveryDateTime = DateUtil.combineDateTime(pr.getDeliveryDate(), pr.getDeliveryTime(), timeZone);
			}
			LOG.info("prDeliveryDateTime  : " + prDeliveryDateTime);
			persistObj.setDeliveryDate(prDeliveryDateTime);

			persistObj.setDeliveryAddress(pr.getDeliveryAddress());
			persistObj.setDeliveryReceiver(pr.getDeliveryReceiver() != null? prService.replaceSmartQuotes(new String(pr.getDeliveryReceiver().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)): pr.getDeliveryReceiver());
			persistObj.setDeliveryCompleted(Boolean.TRUE);
			// save As Draft
			if (isDraft) {
				persistObj.setStatus(PrStatus.DRAFT);
				prService.updatePr(persistObj);
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { persistObj.getPrId() }, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			}
			prService.updatePr(persistObj);
			LOG.info("pr Delivery updated successfully");
		} catch (Exception e) {
			LOG.error("Error in saving Pr Delivery Address " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("pr.delivery.address.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("addressList", addressList);
			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), pr.getId()));
			return new ModelAndView("prDelivery", "pr", pr);
		}
		return new ModelAndView("redirect:prPurchaseItem/" + pr.getId());
	}

	/**
	 * @param pr
	 * @param model
	 * @param redir
	 * @return
	 */
	@RequestMapping(path = "/savePrDeliveryDraft", method = RequestMethod.POST)
	public ModelAndView savePrDraft(@ModelAttribute Pr pr, Model model, RedirectAttributes redir, HttpSession session) {
		savePrDelivery(pr, model, redir, session, Boolean.TRUE);
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

}