package com.privasia.procurehere.web.controller;

import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.PoService;
import com.privasia.procurehere.web.editors.BuyerAddressEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author parveen
 */
@Controller
@RequestMapping("/buyer")
public class PoDeliveryController extends PrBaseController {

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	BuyerAddressEditor buyerAddressEditor;

	@Autowired
	PoService poService;
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

	@RequestMapping(path = "/poDelivery/{poId}", method = RequestMethod.GET)
	public String poDelivery(@PathVariable String poId, @RequestParam("prId") String prId,Model model) {

		Po po = poService.getLoadedPoById(poId);
		Pr pr = prService.getLoadedPrById(prId);
		po.setDeliveryTime(po.getDeliveryDate());
		model.addAttribute("pr", pr);
		model.addAttribute("po", po);
		List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("addressList", addressList);
		model.addAttribute("eventPermissions", poService.getUserPemissionsForPo(SecurityLibrary.getLoggedInUser().getId(), poId));
		LOG.info("create po Delivery GET called po id :" + poId+" from prId: "+prId);
		return "poDelivery";
	}

	@RequestMapping(path = "/poDelivery", method = RequestMethod.POST)
	public ModelAndView savePoDelivery(@ModelAttribute Po po,@RequestParam String prId, Model model, RedirectAttributes redir, HttpSession session, boolean isDraft) {
		LOG.info("create po Delivery POST called po id :" + po.getId()+"Form PR :"+prId);

		Po persistObj = poService.getLoadedPoById(po.getId());
		try {

			if (CollectionUtil.isNotEmpty(poService.validatePo(po, Po.PoDelivery.class))){
				List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("addressList", addressList);
				return new ModelAndView("poDelivery", "po", po);
			}

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			Date pODeliveryDateTime = null;
			if (po.getDeliveryDate() != null && po.getDeliveryTime() != null) {
				pODeliveryDateTime = DateUtil.combineDateTime(po.getDeliveryDate(), po.getDeliveryTime(), timeZone);
			}
			LOG.info("poDeliveryDateTime  : " + pODeliveryDateTime);
			persistObj.setDeliveryDate(pODeliveryDateTime);
			LOG.info("poDeliveryAddress  : " + po.getDeliveryAddress());
			persistObj.setDeliveryAddress(po.getDeliveryAddress());
			persistObj.setDeliveryReceiver(po.getDeliveryReceiver() != null? prService.replaceSmartQuotes(new String(po.getDeliveryReceiver().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)): po.getDeliveryReceiver());
			persistObj.setDeliveryCompleted(Boolean.TRUE);
			// save As Draft
			if (isDraft) {
				persistObj.setStatus(PoStatus.DRAFT);
				poService.updatePo(persistObj);
				redir.addFlashAttribute("success", messageSource.getMessage("common.success.savedraft", new Object[] { persistObj.getPoNumber() }, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			}
			poService.updatePo(persistObj);
			LOG.info("po Delivery updated successfully");
		} catch (Exception e) {
			LOG.error("Error in saving Po Delivery Address " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("pr.delivery.address.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("addressList", addressList);
			model.addAttribute("eventPermissions", prService.getUserPemissionsForPr(SecurityLibrary.getLoggedInUser().getId(), po.getId()));
			return new ModelAndView("poDelivery", "po", po);
		}
		return new ModelAndView("redirect:poPurchaseItem/" + po.getId()+"?prId="+prId);
	}

	/**
	 * @param po
	 * @param model
	 * @param redir
	 * @return
	 */
	@RequestMapping(path = "/savePoDeliveryDraft", method = RequestMethod.POST)
	public ModelAndView savePoDraft(@ModelAttribute Po po,@RequestParam String prId, Model model, RedirectAttributes redir, HttpSession session) {
		savePoDelivery(po,prId, model, redir, session, Boolean.TRUE);
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

}