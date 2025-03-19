package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.enums.AuctionConsolePriceVenderType;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.enums.TimeExtensionType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.BidHistoryChartPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.WebSocketPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAuditTrailService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.GenericBqService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfaSupplierBqService;

@Controller
@RequestMapping("/supplier")
public class SupplierEnglishAuctionController {

	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	GenericBqService genericBqService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Resource
	MessageSource messageSource;
	
	@Autowired
	BuyerAuditTrailService buyerAuditTrailService;

	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.setAutoGrowCollectionLimit(2000); // this is for by default only 256 array of object allowed here // #PH-889 comment by Yogesh
	}

	@RequestMapping(path = "englishAuctionConsole/{eventId}", method = RequestMethod.GET)
	public String englishAuctionConsole(@PathVariable String eventId, Model model, RedirectAttributes redir) {
		try {
			LOG.info("english auction " + eventId);
			RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
			List<RfaSupplierBq> bqList = rfaBqService.findRfaSupplierBqbyEventId(eventId);
			if (CollectionUtil.isEmpty(bqList)) {
				redir.addFlashAttribute("error", messageSource.getMessage("bq.not.allow.auctionhall", new Object[] {}, Global.LOCALE));
				return "redirect:/supplier/viewSupplierEvent/RFA/" + eventId;
			}
			String bqId = null;
			if (CollectionUtil.isNotEmpty(bqList)) {
				for (RfaSupplierBq rfaSupplierEventBq : bqList) {
					bqId = rfaSupplierEventBq.getBq().getId();
				}
			}
			RfaSupplierBq rfaSupplierBq = rfaSupplierBqService.getSupplierBqByBqAndSupplierId(bqId, SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("rfaSupplierBq", rfaSupplierBq);
			if (StringUtils.checkString(bqId).length() > 0) {
				List<RfaSupplierBqItem> supplierBqItem = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId, SecurityLibrary.getLoggedInUserTenantId());
				// List<RfaSupplierBqItem> rfaSupplierBqItems=new
				// ArrayList<RfaSupplierBqItem>();
				RfaSupplierBq rfaSupplierBqObj = new RfaSupplierBq();
				Integer i = 1;
				for (RfaSupplierBqItem rfaSupplierBqItem : supplierBqItem) {
					if (i > 1) {
						break;
					} else {
						rfaSupplierBqObj = rfaSupplierBqItem.getSupplierBq();
					}
					i++;
				}

				// List<RfaEventSupplier> listEventSup =
				// rfaEventSupplierService.getSupplierListForBidderDisqualify(eventId,
				// SecurityLibrary.getLoggedInUserTenantId());
				// LOG.info("List : " + listEventSup.size());
				model.addAttribute("supplierId", SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("bqId", bqId);
				model.addAttribute("supplierBqItem", supplierBqItem);
				EventPermissions eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("eventPermissions", eventPermissions);
				model.addAttribute("event", event);
				LOG.info("field1Label : " + rfaSupplierBqObj.getField1Label() + " : field2Label : " + rfaSupplierBqObj.getField2Label() + " :  field3Label : " + rfaSupplierBqObj.getField3Label() + " : field4Label : " + rfaSupplierBqObj.getField4Label());
				model.addAttribute("field1Label", rfaSupplierBqObj.getField1Label());
				model.addAttribute("field2Label", rfaSupplierBqObj.getField2Label());
				model.addAttribute("field3Label", rfaSupplierBqObj.getField3Label());
				model.addAttribute("field4Label", rfaSupplierBqObj.getField4Label());
				model.addAttribute("taxTypeList", TaxType.values());
				Integer bidCountForSupplier = rfaEventSupplierService.getNumberOfBidsBySupplier(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("bidCountForSupplier", bidCountForSupplier);
				List<RfaSupplierBqPojo> supplirBqPojoList = rfaSupplierBqService.getSupplierListForAuctionConsole(eventId, null);
				model.addAttribute("supplirBqPojoList", supplirBqPojoList);
				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				model.addAttribute("auctionRules", auctionRules);
				model.addAttribute("eventSupplier", rfaEventSupplierService.getEventSupplierForAuctionBySupplierAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId));
				BigDecimal feasibleBid = BigDecimal.ZERO;
				if (auctionRules.getIsBiddingMinValueFromPrevious() || auctionRules.getIsBiddingPriceHigherLeadingBid()) {
					BigDecimal otherFeasibleBid = null;
					BigDecimal prevFeasibleBid = null;

					if (auctionRules.getIsBiddingMinValueFromPrevious()) {
						prevFeasibleBid = nextFesibleBidFromOwn(rfaSupplierBqObj, SecurityLibrary.getLoggedInUserTenantId(), bqId, eventId, (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2));
						LOG.info("prevFeasibleBid : " + prevFeasibleBid);
					}
					if (event.getAuctionType() != AuctionType.FORWARD_SEALED_BID && event.getAuctionType() != AuctionType.REVERSE_SEALED_BID) {
						if (auctionRules.getIsBiddingPriceHigherLeadingBid()) {
							otherFeasibleBid = nextFesibleBidFromOther(rfaSupplierBqObj, SecurityLibrary.getLoggedInUserTenantId(), bqId, eventId, (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2));
							LOG.info("otherFeasibleBid : " + otherFeasibleBid);
						}
					}
					if (prevFeasibleBid != null && otherFeasibleBid != null) {
						LOG.info(" prevFeasibleBid.compareTo(otherFeasibleBid): = " + prevFeasibleBid.compareTo(otherFeasibleBid));
						if (auctionRules.getFowardAuction()) {
							if (otherFeasibleBid.compareTo(prevFeasibleBid) > 0) {
								feasibleBid = otherFeasibleBid;
								LOG.info("From other");
							} else {
								feasibleBid = prevFeasibleBid;
								LOG.info("From own");
							}
						} else {
							if (prevFeasibleBid.compareTo(otherFeasibleBid) > 0) {
								feasibleBid = otherFeasibleBid;
								LOG.info("From other");
							} else {
								feasibleBid = prevFeasibleBid;
								LOG.info("From own");
							}

						}
					} else if (prevFeasibleBid != null && otherFeasibleBid == null) {
						LOG.info("From own and the other is nulll");
						feasibleBid = prevFeasibleBid;
					} else if (prevFeasibleBid == null && otherFeasibleBid != null) {
						LOG.info("From other and the own is null");
						feasibleBid = otherFeasibleBid;
					}
					LOG.info("Feasible Bid : " + feasibleBid);
					model.addAttribute("feasibleBid", feasibleBid.setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_UP));
					LOG.info("Feasible Bid new : " + feasibleBid);
					if (auctionRules.getFowardAuction()) {
						model.addAttribute("lastBidOfSuppliers", rfaSupplierBqService.getMaxBidFromAllBidsOfSuppliers(bqId));
					} else {
						model.addAttribute("lastBidOfSuppliers", rfaSupplierBqService.getMinBidFromAllBidsOfSuppliers(bqId));
					}

				} else {
					model.addAttribute("feasibleBid", "");
				}
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.during.loading.auctionhall", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		return "supplierEnglishAuctionConsole";
	}

	@RequestMapping(path = "submitEnglishAuction/{eventId}/{bqId}", method = RequestMethod.POST)
	public String submitEnglishAuction(@PathVariable String eventId, @PathVariable(required = false) String bqId, @ModelAttribute("rfaSupplierBq") RfaSupplierBq rfaSupplierBq, Model model, RedirectAttributes redir, HttpSession session, HttpServletRequest request) {
		LOG.info("Bq Id : " + bqId + " supplier Id :  " + SecurityLibrary.getLoggedInUserTenantId() + " : event Id : " + eventId + " : rfa supplier Bq ID: " + rfaSupplierBq.getId());
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		BigDecimal nextFesibleBid = BigDecimal.ZERO;
		BigDecimal currentBid = rfaSupplierBq.getTotalAfterTax();
		LOG.info("***************Additional tax " + rfaSupplierBq.getAdditionalTax());
		LOG.info("***************Additional taxDescr " + rfaSupplierBq.getTaxDescription());
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		try {

			AuctionRules auctionRules = rfaEventService.getAuctionRulesForBidSumission(eventId);
			BigDecimal maxMinBid = BigDecimal.ZERO;
			if (auctionRules.getFowardAuction()) {
				maxMinBid = rfaSupplierBqService.getMaxBidFromAllBidsOfSuppliers(bqId);
			} else {
				maxMinBid = rfaSupplierBqService.getMinBidFromAllBidsOfSuppliers(bqId);
			}
			RfaEvent event = rfaEventService.getRfaEventForTimeExtensionAndBidSubmission(eventId);

			if (event.getStatus() != EventStatus.ACTIVE) {
				redir.addFlashAttribute("error", "Auction Has closed,bid is not acceptable");
				return "redirect:/supplier/englishAuctionConsole/" + eventId;
			}
			// RfaEvent event = rfaEventService.getLeanEventbyEventId(eventId);
			if (rfaSupplierBq.getTotalAfterTax().floatValue() > 0) {
				if (auctionRules.getIsBiddingMinValueFromPrevious()) {
					if (!minValueIncrementFromOwnCheck(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), rfaSupplierBq.getBq().getId(), eventId, (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2))) {
						LOG.info("getIsBiddingMinValueFromPrevious ..........................1");
						redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable", new Object[] {}, Global.LOCALE));
						return "redirect:/supplier/englishAuctionConsole/" + eventId;
					}
				}
				if (event.getAuctionType() != AuctionType.FORWARD_SEALED_BID && event.getAuctionType() != AuctionType.REVERSE_SEALED_BID) {
					LOG.info("Only English Auction varification");
					if (auctionRules.getIsBiddingPriceHigherLeadingBid()) {
						if (!minValueIncrementFromMaxSupCheck(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), rfaSupplierBq.getBq().getId(), eventId)) {
							LOG.info("getIsBiddingPriceHigherLeadingBid ..........................1");
							redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable", new Object[] {}, Global.LOCALE));
							return "redirect:/supplier/englishAuctionConsole/" + eventId;
						}
						LOG.info("getIsBiddingPriceHigherLeadingBid ..........................2");
					}

					if (!auctionRules.getIsBiddingAllowSupplierSameBid()) {
						Integer countBidSup = rfaSupplierBqService.getCountsOfSameBidBySupliers(bqId, eventId, rfaSupplierBq.getTotalAfterTax());
						if (countBidSup != 0) {
							LOG.info("getIsBiddingAllowSupplierSameBid ..........................1");
							redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable", new Object[] {}, Global.LOCALE));
							return "redirect:/supplier/englishAuctionConsole/" + eventId;
						}
						LOG.info("getIsBiddingAllowSupplierSameBid ..........................2");
					}

				}

				// for manage if there is no bid for forward it cannot be lower
				// then its own
				RfaSupplierBq persistObj = rfaSupplierBqService.getRfaSupplierBqForLumsumBid(bqId, SecurityLibrary.getLoggedInUserTenantId());
				BigDecimal oldBid = persistObj.getTotalAfterTax();
				if (auctionRules.getFowardAuction()) {
					if (oldBid.compareTo(rfaSupplierBq.getTotalAfterTax()) > 0) {
						redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable", new Object[] {}, Global.LOCALE));
						return "redirect:/supplier/englishAuctionConsole/" + eventId;
					}
				} else {
					if (rfaSupplierBq.getTotalAfterTax().compareTo(oldBid) > 0) {
						redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable", new Object[] {}, Global.LOCALE));
						return "redirect:/supplier/englishAuctionConsole/" + eventId;
					}
				}
				LOG.info("Before update  ..........................");
				String ipAddress = request.getHeader("X-FORWARDED-FOR");
				if (ipAddress == null) {
					ipAddress = request.getRemoteAddr();
				}
				rfaSupplierBq = rfaSupplierBqService.saveOrUpdateSupplierBq(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), eventId, ipAddress);
				LOG.info("After update  ..........................");

				// do check extension
				if (event.getTimeExtensionType() == TimeExtensionType.AUTOMATIC && event.getTimeExtensionLeadingBidValue() != null) {
					boolean checkForTImeExtension = checkForTimeExtension(eventId, bqId, currentBid, maxMinBid);
					LOG.info("Get In for Time extension: " + checkForTImeExtension);
					if (checkForTImeExtension) {
						LOG.info("now we have to extends the time: ");
						boolean timeExtention = rfaEventService.automaticTimeExtension(event, timeZone, session);
						LOG.info(timeExtention);
						if (timeExtention) {
							String data = event.getTimeExtensionDuration().toString() + " " + event.getTimeExtensionDurationType().toString();
							WebSocketPojo webSocketPojo = new WebSocketPojo(data, "", "");
							simpMessagingTemplate.convertAndSend("/auctionTimeExtension/" + event.getId(), webSocketPojo);
							
							try {
								SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
								BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.EXTENSION, "Event end time extended from '" + df.format(event.getEventEnd()) + " '" + event.getTimeExtensionDuration() + " '" + event.getTimeExtensionDurationType() + "' for Event '"+event.getEventId()+ "' ", event.getTenantId(), event.getCreatedBy(), new Date(), ModuleType.RFA);
								buyerAuditTrailService.save(buyerAuditTrail);
							} catch (Exception e) {
								LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
							}
							
						}
					}
				}

				if (event.getAuctionType() != AuctionType.FORWARD_SEALED_BID && event.getAuctionType() != AuctionType.REVERSE_SEALED_BID) {
					if (auctionRules.getIsBiddingPriceHigherLeadingBid()) {
						nextFesibleBid = nextFesibleBidFromOther(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), rfaSupplierBq.getBq().getId(), eventId, (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2));
						LOG.info("final next fesible bid : " + nextFesibleBid);
						WebSocketPojo webSocketPojo = new WebSocketPojo("", "", "");
						webSocketPojo.setData(nextFesibleBid.toString());
						simpMessagingTemplate.convertAndSend("/auctionFesibleBid/" + event.getId(), webSocketPojo);
					}
				}
				if (auctionRules.getIsBiddingMinValueFromPrevious()) {
					nextFesibleBid = nextFesibleBidFromOwn(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), rfaSupplierBq.getBq().getId(), eventId, (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2));
					LOG.info("final next fesible bid : " + nextFesibleBid);
					WebSocketPojo webSocketPojo = new WebSocketPojo("", "", "");
					webSocketPojo.setData(nextFesibleBid.toString());
					simpMessagingTemplate.convertAndSend("/auctionFesibleBid/" + event.getId() + "/" + SecurityLibrary.getLoggedInUserTenantId(), webSocketPojo);
				}
				// websocket for bidder list
				simpMessagingTemplate.convertAndSend("/auctionSupplierList/" + event.getId(), "");
				// websocket for graph
				simpMessagingTemplate.convertAndSend("/bidHistorySupplierSide/" + event.getId() + "/" + SecurityLibrary.getLoggedInUserTenantId(), "Graph Up dated");
				simpMessagingTemplate.convertAndSend("/bidHistoryBuyerSide/" + event.getId(), "Graph Update on buyer side ");
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.successfully.submitted", new Object[] {}, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.cant.lessthan.zero", new Object[] {}, Global.LOCALE));
				return "redirect:/supplier/englishAuctionConsole/" + eventId;
			}
		} catch (ApplicationException e) {
			redir.addFlashAttribute("error", e.getMessage());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.during.bid.saving", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		LOG.info("Sucessfully Submited bid");
		return "redirect:/supplier/englishAuctionConsole/" + eventId;
	}

	@RequestMapping(path = "submitLumsumEnglishAuction/{eventId}", method = RequestMethod.POST)
	public String submitLumsumEnglishAuction(@PathVariable String eventId, @RequestParam("totalAmount") BigDecimal totalAmount, @RequestParam("bqId") String bqId, Model model, RedirectAttributes redir, HttpSession session, HttpServletRequest request) {
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
			BigDecimal maxMinBid = BigDecimal.ZERO;
			if (auctionRules.getFowardAuction()) {
				maxMinBid = rfaSupplierBqService.getMaxBidFromAllBidsOfSuppliers(bqId);
			} else {
				maxMinBid = rfaSupplierBqService.getMinBidFromAllBidsOfSuppliers(bqId);
			}
			RfaEvent event = rfaEventService.getRfaEventForTimeExtensionAndBidSubmission(eventId);
			RfaSupplierBq rfaSupplierBq = rfaSupplierBqService.getRfaSupplierBqForLumsumBid(bqId, SecurityLibrary.getLoggedInUserTenantId());

			// this is set just because in another method it get uses
			// this one is not for saving
			rfaSupplierBq.setTotalAfterTax(totalAmount);

			LOG.info("initial price : " + rfaSupplierBq.getInitialPrice());
			LOG.info("total after tax : " + rfaSupplierBq.getTotalAfterTax());

			BigDecimal diffPercentage = null;
			if (rfaSupplierBq.getInitialPrice() != null) {
				BigDecimal differenceAmount = rfaSupplierBq.getInitialPrice().subtract(rfaSupplierBq.getTotalAfterTax());
				MathContext mc = new MathContext(4, RoundingMode.HALF_EVEN);
				diffPercentage = differenceAmount.divide(rfaSupplierBq.getInitialPrice(), mc).multiply(new BigDecimal(100.0000)).setScale(2, RoundingMode.HALF_EVEN);
				LOG.info("Diff Percentage : " + diffPercentage);
			}
			if (rfaSupplierBq.getTotalAfterTax().floatValue() > 0) {
				if (auctionRules.getIsBiddingMinValueFromPrevious()) {
					if (!minValueIncrementFromOwnCheck(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), bqId, eventId, (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2))) {
						// redir.addFlashAttribute("error", "This bid value is not acceptable. It should be " +
						// (auctionRules.getFowardAuction() ? "higher" : "lower") + " than your previous bid.");
						redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.should.pervious.bid", new Object[] { auctionRules.getFowardAuction() ? "higher" : "lower" }, Global.LOCALE));
						return "redirect:/supplier/englishAuctionConsole/" + eventId;
					}
				}

				if (auctionRules.getAuctionType() != AuctionType.FORWARD_SEALED_BID && auctionRules.getAuctionType() != AuctionType.REVERSE_SEALED_BID) {
					if (auctionRules.getIsBiddingPriceHigherLeadingBid()) {
						if (!minValueIncrementFromMaxSupCheck(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), bqId, eventId)) {
							// redir.addFlashAttribute("error", "This bid value is not acceptable as it violates the
							// bidder rule.");
							redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable.biddervalue", new Object[] {}, Global.LOCALE));
							return "redirect:/supplier/englishAuctionConsole/" + eventId;
						}
					}

					if (auctionRules.getIsBiddingAllowSupplierSameBid() != null && !auctionRules.getIsBiddingAllowSupplierSameBid()) {
						Integer countBidSup = rfaSupplierBqService.getCountsOfSameBidBySupliers(bqId, eventId, totalAmount);
						if (countBidSup != 0) {
							// redir.addFlashAttribute("error", "This bid value is not acceptable because same bid price
							// already exists");
							redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable.bidexist", new Object[] {}, Global.LOCALE));
							return "redirect:/supplier/englishAuctionConsole/" + eventId;
						}
					}
				}

			}
			RfaSupplierBq persistObj = rfaSupplierBqService.getRfaSupplierBqForLumsumBid(bqId, SecurityLibrary.getLoggedInUserTenantId());
			BigDecimal oldBid = persistObj.getTotalAfterTax();
			if (auctionRules.getFowardAuction()) {
				if (oldBid.compareTo(rfaSupplierBq.getTotalAfterTax()) >= 0) {
					redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable", new Object[] {}, Global.LOCALE));
					return "redirect:/supplier/englishAuctionConsole/" + eventId;
				}
			} else {
				if (rfaSupplierBq.getTotalAfterTax().compareTo(oldBid) >= 0) {
					redir.addFlashAttribute("error", messageSource.getMessage("bidvalue.not.acceptable", new Object[] {}, Global.LOCALE));
					return "redirect:/supplier/englishAuctionConsole/" + eventId;
				}
			}
			try {
				rfaEventService.updateLumsumAuction(eventId, SecurityLibrary.getLoggedInUserTenantId(), bqId, totalAmount, (diffPercentage != null ? diffPercentage.abs() : null));
			} catch (Exception e) {
				throw new ApplicationException("Submitted value is too large");
			}
			AuctionBids auctionBids = new AuctionBids();
			auctionBids.setAmount(rfaSupplierBq.getTotalAfterTax());
			auctionBids.setEvent(event);
			auctionBids.setBidSubmissionDate(new Date());
			auctionBids.setBidBySupplier(SecurityLibrary.getLoggedInUser().getSupplier());

			// Update Rank
			Integer rankOfSupplier = null;
			if (auctionRules.getFowardAuction()) {
				rankOfSupplier = rfaEventSupplierService.updateSupplierAuctionRank(eventId, Boolean.TRUE, SecurityLibrary.getLoggedInUserTenantId());
				LOG.info("Here to after update the rank : FRD :   " + rankOfSupplier);
			} else {
				rankOfSupplier = rfaEventSupplierService.updateSupplierAuctionRank(eventId, Boolean.FALSE, SecurityLibrary.getLoggedInUserTenantId());
			}
			auctionBids.setRankForBid(rankOfSupplier);
			auctionBids = rfaEventService.saveAuctionBids(auctionBids);
			if (rankOfSupplier == null) {
				if (auctionRules.getFowardAuction()) {
					rankOfSupplier = rfaEventSupplierService.updateSupplierAuctionRank(eventId, Boolean.TRUE, SecurityLibrary.getLoggedInUserTenantId());
					LOG.info("Here to after update the rank : FRD :   " + rankOfSupplier);
				} else {
					rankOfSupplier = rfaEventSupplierService.updateSupplierAuctionRank(eventId, Boolean.FALSE, SecurityLibrary.getLoggedInUserTenantId());
				}
				auctionBids.setRankForBid(rankOfSupplier);
				rfaEventService.saveAuctionBids(auctionBids);
			}

			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
			auctionBids.setIpAddress(ipAddress);

			rfaEventService.updateEventSupplierForAuction(eventId, SecurityLibrary.getLoggedInUserTenantId(), ipAddress);
			if (event.getTimeExtensionType() == TimeExtensionType.AUTOMATIC && event.getTimeExtensionLeadingBidValue() != null) {
				LOG.info("Get In for Time extension: " + checkForTimeExtension(eventId, bqId, totalAmount, maxMinBid));
				if (checkForTimeExtension(eventId, bqId, totalAmount, maxMinBid)) {
					LOG.info("Get In for Time extension really: ");
					boolean timeExtension = rfaEventService.automaticTimeExtension(event, timeZone, session);
					if (timeExtension) {
						String data = event.getTimeExtensionDuration().toString() + " " + event.getTimeExtensionDurationType().toString();
						WebSocketPojo webSocketPojo = new WebSocketPojo(data, "", "");
						simpMessagingTemplate.convertAndSend("/auctionTimeExtension/" + event.getId(), webSocketPojo);
					}
				}
			}
			// websocket for graph
			simpMessagingTemplate.convertAndSend("/bidHistoryBuyerSide/" + event.getId(), "Graph Update on buyer side ");
			simpMessagingTemplate.convertAndSend("/bidHistorySupplierSide/" + event.getId() + "/" + SecurityLibrary.getLoggedInUserTenantId(), "Graph Up dated");
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.successfully.submitted", new Object[] {}, Global.LOCALE));
			LOG.info("Sucessfully Submited bid");
			BigDecimal nextFesibleBid = BigDecimal.ZERO;
			if (event.getAuctionType() != AuctionType.FORWARD_SEALED_BID && event.getAuctionType() != AuctionType.REVERSE_SEALED_BID) {
				if (auctionRules.getIsBiddingPriceHigherLeadingBid()) {
					nextFesibleBid = nextFesibleBidFromOther(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), bqId, eventId, (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2));
					LOG.info("final next fesible bid : " + nextFesibleBid);
					WebSocketPojo webSocketPojo = new WebSocketPojo("", "", "");
					webSocketPojo.setData(nextFesibleBid.toString());
					simpMessagingTemplate.convertAndSend("/auctionFesibleBid/" + event.getId(), webSocketPojo);
				}
			}
			if (auctionRules.getIsBiddingMinValueFromPrevious()) {
				nextFesibleBid = nextFesibleBidFromOwn(rfaSupplierBq, SecurityLibrary.getLoggedInUserTenantId(), bqId, eventId, (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2));
				LOG.info("final next fesible bid : " + nextFesibleBid);
				WebSocketPojo webSocketPojo = new WebSocketPojo("", "", "");
				webSocketPojo.setData(nextFesibleBid.toString());
				simpMessagingTemplate.convertAndSend("/auctionFesibleBid/" + event.getId() + "/" + SecurityLibrary.getLoggedInUserTenantId(), webSocketPojo);
			}
		} catch (ApplicationException e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// redir.addFlashAttribute("error", "Error during bid saving : " + e.getMessage());
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.during.bid.saving", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/supplier/englishAuctionConsole/" + eventId;
	}

	private boolean minValueIncrementFromOwnCheck(RfaSupplierBq rfaSupplierBq, String supplierId, String bqId, String eventId, int decimalPlaces) throws ApplicationException {
		try {
			BigDecimal differenceOfBids = null;
			LOG.info("Bq Id : " + bqId + " supplier Id :  " + supplierId);
			RfaSupplierBq persistObj = rfaSupplierBqService.getRfaSupplierBqForLumsumBid(bqId, supplierId);
			BigDecimal oldBid = (persistObj.getTotalAfterTax()).setScale(decimalPlaces, RoundingMode.HALF_UP);
			BigDecimal currentBid = (rfaSupplierBq.getTotalAfterTax()).setScale(decimalPlaces, RoundingMode.HALF_UP);

			LOG.info("old Bid : " + oldBid + " : current Bid : " + currentBid);
			AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(eventId);
			BigDecimal incrementValueOrPer = auctionRules.getBiddingMinValue().setScale(decimalPlaces, RoundingMode.HALF_UP);
			if (auctionRules.getFowardAuction()) {
				differenceOfBids = (currentBid.subtract(oldBid)).setScale(decimalPlaces, RoundingMode.HALF_UP);
				LOG.info("Differnece : " + differenceOfBids);
				if (auctionRules.getBiddingMinValueType() == ValueType.PERCENTAGE) {
					LOG.info("(oldBid.multiply(incrementValueOrPer)).setScale(decimalPlaces, RoundingMode.HALF_UP) : " + (oldBid.multiply(incrementValueOrPer)).setScale(decimalPlaces, RoundingMode.HALF_UP));
					LOG.info("(oldBid.multiply(incrementValueOrPer)) : " + (oldBid.multiply(incrementValueOrPer)));
					BigDecimal valueOfPercent = ((oldBid.multiply(incrementValueOrPer)).setScale(decimalPlaces, RoundingMode.HALF_UP)).divide(new BigDecimal(100)).setScale(decimalPlaces, RoundingMode.HALF_UP);
					LOG.info("value of per : " + valueOfPercent);
					if (differenceOfBids.compareTo(valueOfPercent) >= 0) {
						// differenceOfBids.longValue() >= valueOfPercent.longValue()
						return true;
					} else {
						return false;
					}
				} else {
					// 2000 inc 100
					LOG.info("Value to be minimum dcr ");
					if (differenceOfBids.compareTo(incrementValueOrPer) >= 0) {
						// differenceOfBids.longValue() >= incrementValueOrPer.longValue()
						return true;
					} else {
						return false;
					}
				}
			} else {
				// For reverse auction
				differenceOfBids = (oldBid.subtract(currentBid)).setScale(decimalPlaces, RoundingMode.HALF_UP);
				if (auctionRules.getBiddingMinValueType() == ValueType.PERCENTAGE) {
					BigDecimal valueOfPercent = ((oldBid.multiply(incrementValueOrPer)).setScale(decimalPlaces, RoundingMode.HALF_UP)).divide(new BigDecimal(100)).setScale(decimalPlaces, RoundingMode.HALF_UP);
					LOG.info("Differnece : " + differenceOfBids + " valueOfPercent :" + valueOfPercent);
					LOG.info("differenceOfBids.compareTo(valueOfPercent) : " + differenceOfBids.compareTo(valueOfPercent));
					if (differenceOfBids.compareTo(valueOfPercent) < 0) {
						return false;
					} else {
						return true;
					}
				} else {
					LOG.info("differenceOfBids.compareTo(incrementValueOrPer) : " + differenceOfBids.compareTo(incrementValueOrPer));
					if (differenceOfBids.compareTo(incrementValueOrPer) < 0) {
						return false;
					} else {
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error : " + e.getMessage(), e);
			throw new ApplicationException("Error during legal bid from own check : " + e.getMessage(), e);
		}
	}

	private boolean minValueIncrementFromMaxSupCheck(RfaSupplierBq rfaSupplierBq, String supplierId, String bqId, String eventId) throws ApplicationException {
		try {
			BigDecimal differenceOfBids = BigDecimal.ZERO;
			BigDecimal maxBidByAnySupplier = rfaSupplierBqService.getMaxBidFromAllBidsOfSuppliers(bqId);

			LOG.info("Bq Id : " + bqId + " supplier Id :  " + supplierId);
			// RfaSupplierBq persistObj =
			// rfaSupplierBqService.getSupplierBqByBqAndSupplierId(bqId,
			// supplierId);
			// LOG.info(" ---------- " + persistObj.getId());
			BigDecimal currentBid = rfaSupplierBq.getTotalAfterTax();
			LOG.info("maxBidByAnySupplier Bid : " + maxBidByAnySupplier + " : current Bid : " + currentBid);

			AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(eventId);
			BigDecimal incrementValueOrPer = auctionRules.getBiddingPriceHigherLeadingBidValue();
			LOG.info("incrementValueOrPer : " + auctionRules.getBiddingPriceHigherLeadingBidValue());
			if (auctionRules.getFowardAuction()) {
				differenceOfBids = currentBid.subtract(maxBidByAnySupplier);
				if (auctionRules.getBiddingPriceHigherLeadingBidType() == ValueType.PERCENTAGE) {
					BigDecimal valueOfPercent = (maxBidByAnySupplier.multiply(incrementValueOrPer)).divide(new BigDecimal(100));
					if (differenceOfBids.compareTo(valueOfPercent) >= 0) {
						// differenceOfBids.longValue() >= valueOfPercent.longValue()
						return true;
					} else {
						return false;
					}
				} else {
					if (differenceOfBids.compareTo(incrementValueOrPer) >= 0) {
						// differenceOfBids.longValue() >= incrementValueOrPer.longValue()
						return true;
					} else {
						return false;
					}
				}
			} else {
				BigDecimal minBidByAnySupplier = rfaSupplierBqService.getMinBidFromAllBidsOfSuppliers(bqId);
				BigDecimal differenceOfBids1 = minBidByAnySupplier.subtract(currentBid);
				LOG.info("diffwrnet supplier test : " + differenceOfBids1);
				differenceOfBids = (minBidByAnySupplier.subtract(currentBid));
				LOG.info("Min bid by supplier : " + minBidByAnySupplier);
				LOG.info("Difference of bids : " + differenceOfBids + " : incrementValueOrPer : " + incrementValueOrPer);
				if (auctionRules.getBiddingPriceHigherLeadingBidType() == ValueType.PERCENTAGE) {
					BigDecimal valueOfPercent = (minBidByAnySupplier.multiply(incrementValueOrPer)).divide(new BigDecimal(100));
					if (differenceOfBids.compareTo(valueOfPercent) < 0) {
						// valueOfPercent.longValue() > differenceOfBids.longValue()
						return false;
					} else {
						return true;
					}
				} else {
					if (differenceOfBids.compareTo(incrementValueOrPer) < 0) {
						// valueOfPercent.longValue() > differenceOfBids.longValue()
						return false;
					} else {
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error : " + e.getMessage(), e);
			throw new ApplicationException("Error during legal bid against top supplier check : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/refreshAuctionConsole/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfaSupplierBqPojo>> refreshAuctionConsole(@PathVariable String eventId) {
		List<RfaSupplierBqPojo> finalList = new ArrayList<RfaSupplierBqPojo>();
		rfaEventSupplierService.updateAuctionOnlineDateTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
		AuctionRules auctionRules = rfaEventService.getAuctionRulesForAuctionConsole(eventId);
		List<RfaSupplierBqPojo> supplirBqPojoList = rfaSupplierBqService.getSupplierListForAuctionConsole(eventId, null);

		for (RfaSupplierBqPojo rfaSupplierBqPojo : supplirBqPojoList) {
			rfaSupplierBqPojo.setOnlineStatus(null);
			if (auctionRules.getAuctionType() == AuctionType.FORWARD_ENGISH || auctionRules.getAuctionType() == AuctionType.REVERSE_ENGISH) {
				// Price : Show None
				if (auctionRules.getAuctionConsolePriceType() == AuctionConsolePriceVenderType.SHOW_NONE && !rfaSupplierBqPojo.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId())) {
					LOG.info("get inside to set price null" + rfaSupplierBqPojo.getSupplierCompanyName());
					rfaSupplierBqPojo.setCurrentPrice(null);
					rfaSupplierBqPojo.setInitialPrice(null);
					rfaSupplierBqPojo.setDifferencePercentage(null);
				} else if (auctionRules.getAuctionConsolePriceType() == AuctionConsolePriceVenderType.SHOW_LEADING && (rfaSupplierBqPojo.getRankOfSupplier() != null && 1 != rfaSupplierBqPojo.getRankOfSupplier() && !rfaSupplierBqPojo.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId()))) {
					// Price : show leading
					rfaSupplierBqPojo.setCurrentPrice(null);
					rfaSupplierBqPojo.setInitialPrice(null);
					rfaSupplierBqPojo.setDifferencePercentage(null);
				}

				// Supplier name
				if (auctionRules.getAuctionConsoleVenderType() == AuctionConsolePriceVenderType.SHOW_NONE) {
					if (!rfaSupplierBqPojo.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId())) {
						rfaSupplierBqPojo.setSupplierCompanyName("");
					}
					finalList.add(rfaSupplierBqPojo);
				} else if (auctionRules.getAuctionConsoleVenderType() == AuctionConsolePriceVenderType.SHOW_LEADING) {
					if (rfaSupplierBqPojo.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId()) || (rfaSupplierBqPojo.getRankOfSupplier() != null && 1 == rfaSupplierBqPojo.getRankOfSupplier())) {
						finalList.add(rfaSupplierBqPojo);
					} else {
						// Remove company name
						rfaSupplierBqPojo.setSupplierCompanyName("");
						finalList.add(rfaSupplierBqPojo);
					}
				} else {
					finalList.add(rfaSupplierBqPojo);
				}

				// rank
				if (auctionRules.getAuctionConsoleRankType() == AuctionConsolePriceVenderType.SHOW_NONE) {
					rfaSupplierBqPojo.setRankOfSupplier(null);
				} else if (auctionRules.getAuctionConsoleRankType() == AuctionConsolePriceVenderType.SHOW_MY_RANK && (!rfaSupplierBqPojo.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId()))) {
					rfaSupplierBqPojo.setRankOfSupplier(null);
				}
			} else if (auctionRules.getAuctionType() == AuctionType.FORWARD_SEALED_BID || auctionRules.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
				if (rfaSupplierBqPojo.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId())) {
					rfaSupplierBqPojo.setRankOfSupplier(null);
					finalList.add(rfaSupplierBqPojo);
				}
			}
		}
		// Remove no of bids and intial price and differce percentage for the
		// other suppliers.
		if (CollectionUtil.isNotEmpty(finalList)) {
			List<RfaSupplierBqPojo> superFinal = new ArrayList<RfaSupplierBqPojo>();
			for (RfaSupplierBqPojo sup : finalList) {
				if (StringUtils.checkString(sup.getSupplierCompanyName()).length() > 0 || sup.getRankOfSupplier() != null || sup.getInitialPrice() != null) {
					superFinal.add(sup);
					if (!sup.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId())) {
						sup.setNumberOfBids(null);
						sup.setInitialPrice(null);
						sup.setDifferencePercentage(null);
					}
				}
			}
			finalList = superFinal;
		}

		TableData<RfaSupplierBqPojo> data = new TableData<RfaSupplierBqPojo>(finalList, supplirBqPojoList.size());
		if (CollectionUtil.isNotEmpty(supplirBqPojoList)) {
			data.setStatus(supplirBqPojoList.get(0).getCurrentAuctionStatus());
			data.setStartDate(supplirBqPojoList.get(0).getStartDate());
			data.setEndDate(supplirBqPojoList.get(0).getEndDate());
			data.setResumeDate(supplirBqPojoList.get(0).getResumeDate());
		}

		return new ResponseEntity<TableData<RfaSupplierBqPojo>>(data, HttpStatus.OK);
	}

	private BigDecimal nextFesibleBidFromOther(RfaSupplierBq rfaSupplierBq, String supplierId, String bqId, String eventId, int decimalPlaces) throws ApplicationException {
		try {
			BigDecimal maxBidByAnySupplier = rfaSupplierBqService.getMaxBidFromAllBidsOfSuppliers(bqId);

			if(maxBidByAnySupplier == null) {
				maxBidByAnySupplier = BigDecimal.ZERO;
			}
			
			LOG.info("Bq Id : " + bqId + " supplier Id :  " + supplierId);
			BigDecimal currentBid = rfaSupplierBq.getTotalAfterTax();
			LOG.info("maxBidByAnySupplier Bid : " + maxBidByAnySupplier + " : current Bid : " + currentBid);

			AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(eventId);
			BigDecimal incrementValueOrPer = auctionRules.getBiddingPriceHigherLeadingBidValue();
			LOG.info("incrementValueOrPer : " + auctionRules.getBiddingPriceHigherLeadingBidValue());
			if (auctionRules.getFowardAuction()) {
				if (auctionRules.getBiddingPriceHigherLeadingBidType() == ValueType.PERCENTAGE) {
					BigDecimal valueOfPercent = (maxBidByAnySupplier.multiply(incrementValueOrPer).setScale(decimalPlaces, RoundingMode.HALF_UP)).divide(new BigDecimal(100)).setScale(decimalPlaces, RoundingMode.HALF_UP);
					LOG.info("Forword Auction -: next Feasible bid when bid is has to greater then max bid- in percentage case " + valueOfPercent.add(maxBidByAnySupplier));
					return valueOfPercent.add(maxBidByAnySupplier);
				} else {
					LOG.info("Forword Auction -: next Feasible bid when bid is has to greater then max bid- in value case " + incrementValueOrPer.add(maxBidByAnySupplier));
					return incrementValueOrPer.add(maxBidByAnySupplier);
				}
			} else {
				BigDecimal minBidByAnySupplier = rfaSupplierBqService.getMinBidFromAllBidsOfSuppliers(bqId);
				if(minBidByAnySupplier == null) {
					minBidByAnySupplier = BigDecimal.ZERO;
				}
				LOG.info("Min bid by supplier : " + minBidByAnySupplier);
				if (auctionRules.getBiddingPriceHigherLeadingBidType() == ValueType.PERCENTAGE) {
					BigDecimal valueOfPercent = (minBidByAnySupplier.multiply(incrementValueOrPer).setScale(decimalPlaces, RoundingMode.HALF_UP)).divide(new BigDecimal(100)).setScale(decimalPlaces, RoundingMode.HALF_UP);
					LOG.info("Reverse Auction :- next Feasible bid when bid is has to greater then max bid- in percentage case " + valueOfPercent.add(maxBidByAnySupplier));
					return minBidByAnySupplier.subtract(valueOfPercent);
				} else {
					LOG.info("Reverse Auction -: next Feasible bid when bid is has to greater then max bid- in value case " + incrementValueOrPer.add(maxBidByAnySupplier));
					return minBidByAnySupplier.subtract(incrementValueOrPer);
				}
			}
		} catch (Exception e) {
			LOG.info("Error : " + e.getMessage(), e);
			throw new ApplicationException("Error during next fesible bid against top supplier check : " + e.getMessage(), e);
		}
	}

	private BigDecimal nextFesibleBidFromOwn(RfaSupplierBq rfaSupplierBq, String supplierId, String bqId, String eventId, int decimalPlaces) throws ApplicationException {
		try {
			LOG.info("Bq Id : " + bqId + " supplier Id :  " + supplierId);
			BigDecimal currentBid = (rfaSupplierBq.getTotalAfterTax()).setScale(decimalPlaces, RoundingMode.HALF_UP);
			BigDecimal nextFesibleBid = BigDecimal.ZERO;
			AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(eventId);
			BigDecimal incrementValueOrPer = auctionRules.getBiddingMinValue();
			if (auctionRules.getFowardAuction()) {
				if (auctionRules.getBiddingMinValueType() == ValueType.PERCENTAGE) {
					BigDecimal valueOfPercent = ((currentBid.multiply(incrementValueOrPer)).setScale(decimalPlaces, RoundingMode.HALF_UP)).divide(new BigDecimal(100)).setScale(decimalPlaces, RoundingMode.HALF_UP);
					nextFesibleBid = valueOfPercent.add(currentBid);
					LOG.info("Forword Auction -: next Feasible bid when bid is has to greater then own bid- in percentage case " + nextFesibleBid);
					return nextFesibleBid;
				} else {
					nextFesibleBid = incrementValueOrPer.add(currentBid);
					LOG.info("Forword Auction -: next Feasible bid when bid is has to greater then own bid- in value case " + nextFesibleBid);
					return nextFesibleBid;
				}
			} else {
				// For reverse auction
				if (auctionRules.getBiddingMinValueType() == ValueType.PERCENTAGE) {
					BigDecimal valueOfPercent = (currentBid.multiply(incrementValueOrPer).setScale(decimalPlaces, RoundingMode.HALF_UP)).divide(new BigDecimal(100)).setScale(decimalPlaces, RoundingMode.HALF_UP);
					nextFesibleBid = currentBid.subtract(valueOfPercent);
					LOG.info("Reverse Auction -: next Feasible bid when bid is has to greater then own bid- in percentage case " + nextFesibleBid);
					return nextFesibleBid;
				} else {
					nextFesibleBid = currentBid.subtract(incrementValueOrPer);
					LOG.info("reverse Auction -: next Feasible bid when bid is has to greater then own bid- in value case " + nextFesibleBid);
					return nextFesibleBid;
				}
			}
		} catch (Exception e) {
			LOG.info("Error : " + e.getMessage(), e);
			throw new ApplicationException("Error during legal next fesibale bid from own check : " + e.getMessage(), e);
		}
	}

	private boolean checkForTimeExtension(String eventId, String bqId, BigDecimal currentBid, BigDecimal maxMinBid) {
		boolean result = Boolean.FALSE;
		// RfaEvent event = rfaEventService.getLeanEventbyEventId(eventId);
		AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(eventId);
		if (auctionRules.getFowardAuction()) {
			// BigDecimal maxBid = rfaSupplierBqService.getMaxBidFromAllBidsOfSuppliers(bqId);
			LOG.info("Current Bid : " + currentBid + "  :  Max Bid : " + maxMinBid);
			LOG.info("Result of current Bid and max Bid : " + currentBid.compareTo(maxMinBid));
			if (currentBid.compareTo(maxMinBid) > 0) {
				result = Boolean.TRUE;
			}
		} else {
			// BigDecimal minBid = rfaSupplierBqService.getMinBidFromAllBidsOfSuppliers(bqId);
			LOG.info("Current Bid : " + currentBid + "  :  Min Bid : " + maxMinBid);
			LOG.info("Result of current Bid and min Bid : " + currentBid.compareTo(maxMinBid));
			if (currentBid.compareTo(maxMinBid) < 0) {
				result = Boolean.TRUE;
			}
		}
		LOG.info("result   : " + result);
		return result;
	}

	@RequestMapping(path = "getBidHistoryOfOwnSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<BidHistoryChartPojo> getBidHistoryOfOwnSupplier(@RequestParam("eventId") String eventId, @RequestParam("arrangeBidBy") String arrangeBidBy, RedirectAttributes redir, HttpSession httpSession) {
		HttpHeaders headers = new HttpHeaders();

		try {
			String timeZone = "GMT+8:00";
			if (httpSession != null) {
				timeZone = (String) httpSession.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (timeZone == null) {
					timeZone = "GMT+8:00";
				}
			}
			httpSession.setAttribute("graphArrangeBy", arrangeBidBy);
			BidHistoryChartPojo bidHistory = rfaEventService.getBidHistoryChartDataForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId(), timeZone, arrangeBidBy);
			return new ResponseEntity<BidHistoryChartPojo>(bidHistory, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("error while getting the bid history of supplier : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.time.extension", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<BidHistoryChartPojo>(null, headers, HttpStatus.OK);
		}
	}

	public static void main(String[] args) {

		/*
		 * System.out.println(new Date()); SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
		 * df.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); String submissionDate = (df.format(new Date()));
		 * System.out.println("submissionDate : "+submissionDate); Date date; try { Date mnew =
		 * df.parse(submissionDate); System.out.println("df.parse(submissionDate); : "+mnew); date =
		 * df.parse(submissionDate); long data = date.getTime(); System.out.println(data); Timestamp my = new
		 * Timestamp(data); System.out.println(my); } catch (ParseException e) { e.printStackTrace(); }
		 */

		DateTime dt = new DateTime(new Date());
		System.out.println(new Date().getTime());
		DateTimeZone dtZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		DateTime dtus = dt.withZone(dtZone);
		Date nowDateWithTimeZone = dtus.toLocalDateTime().toDate();
		System.out.println(nowDateWithTimeZone);
		System.out.println(nowDateWithTimeZone.getTime());
	}

}
