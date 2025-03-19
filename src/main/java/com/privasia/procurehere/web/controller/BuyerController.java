/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Notes;
import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.pojo.NotesPojo;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSubscriptionService;
import com.privasia.procurehere.service.PaymentTransactionService;
import com.privasia.procurehere.service.UserService;

/**
 * @author Arc
 */
@Controller
@RequestMapping(value = "/owner")
public class BuyerController implements Serializable {

	private static final long serialVersionUID = -6367432581209698965L;

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Resource
	MessageSource messageSource;

	@Autowired
	BuyerService buyerService;

	@Autowired
	UserService userService;

	@Autowired
	BuyerSubscriptionService buyerSubscriptionService;

	@Autowired
	PaymentTransactionService paymentTransactionService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@RequestMapping(value = "/buyerList", method = RequestMethod.GET)
	public String buyerList(Model model) {
		List<Buyer> list = buyerService.searchBuyersForPagination(BuyerStatus.PENDING.toString(), "Newest", null, "0");
		LOG.info("LIST SIZE : " + list.size());
		model.addAttribute("buyerList", list);
		model.addAttribute("buyerObj", new Buyer());
		return "buyerList";
	}

	@RequestMapping(value = "/buyerDetails/{buyerId}", method = RequestMethod.GET)
	public String showBuyerDetails(@PathVariable(name = "buyerId") String buyerId, Model model) throws JsonProcessingException {
		Buyer buyerObj = buyerService.findBuyerById(buyerId);
		if (buyerObj != null && StringUtils.checkString(buyerObj.getPublicContextPath()).length() == 0) {
			buyerObj.setPublicContextPath(buyerId);
			buyerService.updateBuyer(buyerObj);
		}
		List<Notes> notes = buyerService.findAllNotesById(buyerId);
		User user = userService.getAdminUserForBuyer(buyerObj);
		model.addAttribute("adminUser", user);
		model.addAttribute("buyerObj", buyerObj);

		model.addAttribute("paymentStatusTypeList", Arrays.asList(TransactionStatus.values()));
		model.addAttribute("subscriptionsList", buyerSubscriptionService.getBuyerSubscriptionFutureplan(buyerId));

		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("noteList", mapper.writeValueAsString(notes));
		return "buyerDetails";
	}

	@RequestMapping(path = "/viewBuyerPaymentTransaction/{transactionId}", method = RequestMethod.GET)
	public ResponseEntity<PaymentTransaction> viewBuyerPaymentTransaction(@PathVariable(name = "transactionId") String transactionId, TableDataInput input) {
		PaymentTransaction tr = paymentTransactionService.getPaymentTransactionById(transactionId);
		return new ResponseEntity<PaymentTransaction>(tr, HttpStatus.OK);
	}

	@RequestMapping(path = "/buyerPaymentTransactionData/{buyerId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<PaymentTransaction>> buyerPaymentTransactionData(@PathVariable(name = "buyerId") String buyerId, TableDataInput input) {
		try {
			TableData<PaymentTransaction> data = new TableData<PaymentTransaction>(paymentTransactionService.findPaymentTransactionsForBuyer(buyerId, input));
			data.setDraw(input.getDraw());
			long totalFilterCount = paymentTransactionService.findTotalFilteredPaymentTransactionsForBuyer(buyerId, input);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = paymentTransactionService.findTotalPaymentTransactionForBuyer(buyerId);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PaymentTransaction>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching buyer payment transaction list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching buyer payment transaction list : " + e.getMessage());
			return new ResponseEntity<TableData<PaymentTransaction>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/toggleAdminAccountLockedStatus", method = RequestMethod.POST)
	public String toggleAdminAccountLockedStatus(@ModelAttribute("adminUser") User adminUser, @RequestParam(name = "buyerId") String buyerId, Model model) {
		userService.toggleAdminAccountStatus(adminUser, buyerId);
		return "redirect:buyerDetails/" + buyerId;
	}

	@RequestMapping(value = "/toggleErpStatus", method = RequestMethod.POST)
	public String toggleErpStatus(@ModelAttribute("adminUser") User adminUser, @RequestParam(name = "buyerId") String buyerId, Model model) {
		userService.toggleErpStatus(adminUser, buyerId);
		return "redirect:buyerDetails/" + buyerId;
	}

	/**
	 * @param status
	 * @return
	 */
	@RequestMapping(value = "searchBuyer", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Buyer>> searchBuyer(@RequestBody SearchVo searchVo) {
		List<Buyer> list = null;
		try {
			LOG.info("Search Buyer by status " + BuyerStatus.valueOf(StringUtils.checkString(searchVo.getStatus()).toUpperCase()) + " Order " + searchVo.getOrder() + " Global search : " + searchVo.getGlobalSreach());
			list = buyerService.searchBuyersForPagination(searchVo.getStatus(), searchVo.getOrder(), searchVo.getGlobalSreach(), "0");
		} catch (Exception e) {
			LOG.error("Error while search Buyer " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while search Buyer " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.save.error", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Buyer>>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<Buyer>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "/addBuyerNotes", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Notes>> addBuyerNotes(@RequestBody NotesPojo notesPojo) {
		LOG.info("incidentType  :: " + notesPojo.getIncidentType() + " :: description  :: " + notesPojo.getDescription() + " :: buyerId ::" + notesPojo.getId());
		Notes notes = new Notes();
		try {
			Buyer buyer = buyerService.findBuyerById(notesPojo.getId());

			notes.setBuyer(buyer);
			notes.setIncidentType(notesPojo.getIncidentType());
			notes.setDescription(notesPojo.getDescription());
			notes.setCreatedBy(SecurityLibrary.getLoggedInUser());
			notes.setCreatedDate(new Date());

			buyerService.saveByerNotes(notes);

			List<Notes> noteList = buyerService.findAllNotesById(notesPojo.getId());

			HttpHeaders headers = new HttpHeaders();
			// headers.add("info", "Notes Saved successfully ");
			headers.add("info", messageSource.getMessage("buyer.notes.save", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<Notes>>(noteList, headers, HttpStatus.OK);
			// return new ResponseEntity<Notes>(null, headers, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while adding Notes to Buyer : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			// headers.add("error", "Error while adding Notes to Buyer " + e.getMessage());
			headers.add("error", messageSource.getMessage("buyer.error.notes", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<Notes>>(null, headers, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/resendBuyerActivationLink/{buyerId}", method = RequestMethod.POST)
	public ResponseEntity<String> resendActivationLinkEmail(@PathVariable(name = "buyerId") String buyerId, Model model) {
		LOG.info("Request for re-send of Buyer Activation email request received for Buyer Id : " + buyerId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			Buyer buyer = buyerService.findBuyerById(buyerId);

			User adminUser = userService.getAdminUserForBuyer(buyer);

			if (adminUser == null) {
				LOG.warn("Admin user for Buyer " + buyerId + " is not present!!!");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Error while sending activation email : " + "Admin user for Buyer " + buyerId + " is not present. Contact the administrator.");
				return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				LOG.info("Found admin account for Buyer : " + buyerId + " with login email : " + adminUser.getLoginId());
			}

			buyerService.sentBuyerCreationMail(buyer, adminUser);

			HttpHeaders headers = new HttpHeaders();
			headers.add("info", "Email sent successfully");
			return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while sending activation email : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while sending activation email : " + e.getMessage());
			return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/updateComunicationEmail/{buyerId}/{emailId:.+}", method = RequestMethod.POST)
	public ResponseEntity<String> updateComunicationEmail(@PathVariable(name = "buyerId") String buyerId, @PathVariable(name = "emailId") String emailId) {
		LOG.info("Request for Update Buyer comunication email to (" + emailId + ") received for Buyer Id : " + buyerId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			Buyer buyer = buyerService.findBuyerById(buyerId);
			User adminUser = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (adminUser == null) {
				LOG.warn("Admin user for Buyer " + buyerId + " is not present!!!");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Error while comunication email : " + "Admin user for Buyer " + buyerId + " is not present. Contact the administrator.");
				return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				LOG.info("Found admin account for Buyer : " + buyerId + " with login email : " + adminUser.getLoginId());

				buyer.setCommunicationEmail(emailId);
				LOG.info("Updated emailId : " + emailId);
				buyer.setActionDate(new Date());
				buyerService.updateBuyer(buyer);
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

	@RequestMapping(value = "/suspendBuyer", method = RequestMethod.POST)
	public String suspendBuyer(@RequestParam String buyerId, @RequestParam String remarks, RedirectAttributes redir) {
		LOG.info("Request for Susspend Buyer for Buyer Id : " + buyerId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			Buyer buyer = buyerService.findBuyerById(buyerId);
			User adminUser = userService.getAdminUserForBuyer(buyer);
			if (adminUser == null) {
				LOG.warn("Admin user for Buyer " + buyerId + " is not present!!!");
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.suspend.buyer", new Object[] {buyerId}, Global.LOCALE));
				return "redirect:buyerDetails/" + buyerId;
			} else {
				LOG.info("Found admin account for Buyer : " + buyerId + " with login email : " + adminUser.getLoginId());
				buyer.setStatus(BuyerStatus.SUSPENDED);
				buyer.setActionDate(new Date());
				buyer.setActionBy(SecurityLibrary.getLoggedInUser());
				buyer.setSuspendedRemarks(remarks);
				buyerService.updateBuyer(buyer);
			}

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.buyer.suspend", new Object[] {}, Global.LOCALE));
			return "redirect:buyerDetails/" + buyerId;
		} catch (Exception e) {
			LOG.error("Error while updated Susspend buyer  : " + e.getMessage(), e);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.updated.suspend.buyer", new Object[] {e.getMessage()}, Global.LOCALE));
			return "redirect:buyerDetails/" + buyerId;
		}
	}

	@RequestMapping(value = "/allowSupplierUpload/{buyerId}", method = RequestMethod.POST)
	public String allowSupplierUpload(@PathVariable(name = "buyerId") String buyerId, RedirectAttributes attr) {
		LOG.info("Request to Allow Supplier Upload for Buyer Id : " + buyerId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		HttpHeaders headers = new HttpHeaders();
		try {
			Buyer buyer = buyerService.findBuyerById(buyerId);
			if (buyer.getAllowSupplierUpload() == null) {
				buyer.setAllowSupplierUpload(false);
			}
			buyer.setAllowSupplierUpload(!buyer.getAllowSupplierUpload());
			buyer = buyerService.updateBuyer(buyer);
			headers.add("success", "Supplier Upload option " + (buyer.getAllowSupplierUpload() ? "Activated" : "Deactivated"));
			// return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
//			attr.addFlashAttribute("success", "Supplier Upload option " + (buyer.getAllowSupplierUpload() ? "Activated" : "Deactivated"));
			attr.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.upload.option", new Object[] {(buyer.getAllowSupplierUpload() ? "Activated" : "Deactivated")}, Global.LOCALE));
			return "redirect:/owner/buyerDetails/" + buyerId;
		} catch (Exception e) {
			LOG.error("Error while changing supplier upload option : " + e.getMessage(), e);
			headers.add("error", "Error while saving Supplier Upload option : " + e.getMessage());
			// return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers,
			// HttpStatus.INTERNAL_SERVER_ERROR);
			return "redirect:buyerDetails/" + buyerId;
		}
	}

	@RequestMapping(value = "/activateBuyer", method = RequestMethod.POST)
	public String activateBuyer(@RequestParam String buyerId, @RequestParam String remarks, RedirectAttributes redir) {
		LOG.info("Request for Activate Buyer for Buyer Id : " + buyerId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			Buyer buyer = buyerService.findBuyerById(buyerId);
			User adminUser = userService.getAdminUserForBuyer(buyer);
			if (adminUser == null) {
				LOG.warn("Admin user for Buyer " + buyerId + " is not present!!!");
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.suspend.buyer", new Object[] {buyerId}, Global.LOCALE));
				return "redirect:buyerDetails/" + buyerId;
			} else {
				LOG.info("Found admin account for Buyer : " + buyerId + " with login email : " + adminUser.getLoginId());
				buyer.setStatus(BuyerStatus.ACTIVE);
				buyer.setActionDate(new Date());
				buyer.setActionBy(SecurityLibrary.getLoggedInUser());
				buyer.setSuspendedRemarks(remarks);
				buyerService.updateBuyer(buyer);
			}

			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.buyer.activate", new Object[] {}, Global.LOCALE));
			return "redirect:buyerDetails/" + buyerId;
		} catch (Exception e) {
			LOG.error("Error while updated Activate buyer  : " + e.getMessage(), e);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.updated.activate.buyer", new Object[] {e.getMessage()}, Global.LOCALE));
			return "redirect:buyerDetails/" + buyerId;
		}
	}

	@RequestMapping(value = "/updateManualSubscription", method = RequestMethod.POST)
	public String updateManualSubscription(@RequestParam String buyerId, @RequestParam String startDate, @RequestParam String endDate, @RequestParam Integer userLimit, @RequestParam Integer eventLimit, Model model, RedirectAttributes redir) throws JsonProcessingException {
		try {
			buyerService.updateManualSubscription(buyerId, startDate, endDate, userLimit, eventLimit);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.subscription.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while update subscription : " + e.getMessage(), e);
		}
		return "redirect:buyerDetails/" + buyerId;
	}

	@RequestMapping(value = "/buyerListForPagination", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<Buyer>> buyerListForPagination(Model model, @RequestBody SearchVo searchVo) {
		List<Buyer> list = buyerService.searchBuyersForPagination(searchVo.getStatus(), searchVo.getOrder(), searchVo.getGlobalSreach(), searchVo.getPageNo());
		LOG.info("LIST SIZE : " + list.size());
		model.addAttribute("buyerList", list);
		model.addAttribute("buyerObj", new Buyer());
		return new ResponseEntity<List<Buyer>>(list, HttpStatus.OK);
	}

	@RequestMapping(value = "updatePublicContextPath/{buyerId}/{publicContextPath}", method = RequestMethod.POST)
	public ResponseEntity<String> updatePublicContextPath(@PathVariable(name = "buyerId") String buyerId, @PathVariable(name = "publicContextPath") String publicContextPath) {
		LOG.info("Request for Update Buyer public context path to (" + publicContextPath + ") received for Buyer Id : " + buyerId + " from User : " + SecurityLibrary.getLoggedInUserLoginId());
		try {
			Buyer buyer = buyerService.findBuyerById(buyerId);
			User adminUser = userService.getUserByLoginIdNoTouch(buyer.getLoginEmail());
			if (adminUser == null) {
				LOG.warn("Admin user for Buyer " + buyerId + " is not present!!!");
				HttpHeaders headers = new HttpHeaders();
				headers.add("error", "Error while public context path : " + "Admin user for Buyer " + buyerId + " is not present. Contact the administrator.");
				return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				LOG.info("Found admin account for Buyer : " + buyerId + " with login email : " + adminUser.getLoginId());
				if (isExistPublicContextPathForBuyer(publicContextPath, buyerId)) {
					buyer.setPublicContextPath(publicContextPath.toLowerCase());
					buyer.setActionDate(new Date());
					buyerService.updateBuyer(buyer);
				} else {
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "The Context Path : " + publicContextPath + " already exist for another buyer.");
					return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.add("success", "Context Path updated successfully");
			return new ResponseEntity<String>("{\"msg\":\"All is good\"}", headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while updated public context path : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error while updating public context path : " + e.getMessage());
			return new ResponseEntity<String>("{\"msg\":\"All is not good\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean isExistPublicContextPathForBuyer(String publicContextPath, String buyerId) {
		boolean uniquePublicContextPath = false;
		Integer existPublicContextPath = buyerService.isExistPublicContextPathForBuyer(publicContextPath, buyerId);
		if (existPublicContextPath == 0) {
			uniquePublicContextPath = true;
		}
		return uniquePublicContextPath;
	}

}
