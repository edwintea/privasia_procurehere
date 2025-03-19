package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfaSupplierSor;
import com.privasia.procurehere.core.entity.RfiSupplierSor;
import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import com.privasia.procurehere.core.entity.RftSupplierSor;
import com.privasia.procurehere.service.RfaSupplierSorService;
import com.privasia.procurehere.service.RfiSupplierSorService;
import com.privasia.procurehere.service.RfpSupplierSorService;
import com.privasia.procurehere.service.RfqSupplierSorService;
import com.privasia.procurehere.service.RftSupplierSorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqOption;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierCq;
import com.privasia.procurehere.core.entity.RfaSupplierCqItem;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiSupplierCq;
import com.privasia.procurehere.core.entity.RfiSupplierCqItem;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqOption;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierCq;
import com.privasia.procurehere.core.entity.RfpSupplierCqItem;
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqCqOption;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierCq;
import com.privasia.procurehere.core.entity.RfqSupplierCqItem;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqOption;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.entity.RftSupplierCq;
import com.privasia.procurehere.core.entity.RftSupplierCqItem;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.RfaSupplierCqItemService;
import com.privasia.procurehere.service.RfaSupplierCqService;
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.RfiSupplierCqItemService;
import com.privasia.procurehere.service.RfiSupplierCqService;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfpDocumentService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.service.RfpSupplierBqService;
import com.privasia.procurehere.service.RfpSupplierCqItemService;
import com.privasia.procurehere.service.RfpSupplierCqService;
import com.privasia.procurehere.service.RfqCqService;
import com.privasia.procurehere.service.RfqDocumentService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RfqMeetingService;
import com.privasia.procurehere.service.RfqSupplierBqService;
import com.privasia.procurehere.service.RfqSupplierCqItemService;
import com.privasia.procurehere.service.RfqSupplierCqService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftDocumentService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.RftSupplierBqService;
import com.privasia.procurehere.service.RftSupplierCqItemService;
import com.privasia.procurehere.service.RftSupplierCqService;
import com.privasia.procurehere.service.supplier.SupplierService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

@Controller
@RequestMapping("/supplier")
public class SupplierEventSummaryController {
	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@Autowired
	RfiMeetingService rfiMeetingService;

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	RfqMeetingService rfqMeetingService;

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	RfiCqService rfiCqService;

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfqCqService rfqCqService;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfpSupplierBqService rfpSupplierBqService;

	@Autowired
	RfqSupplierBqService rfqSupplierBqService;

	@Autowired
	RftSupplierBqService rftSupplierBqService;

	@Autowired
	RfaSupplierCqItemService rfaSupplierCqItemService;

	@Autowired
	RfiSupplierCqItemService rfiSupplierCqItemService;

	@Autowired
	RfpSupplierCqItemService rfpSupplierCqItemService;

	@Autowired
	RfqSupplierCqItemService rfqSupplierCqItemService;

	@Autowired
	RftSupplierCqItemService rftSupplierCqItemService;

	@Autowired
	RfaDocumentService rfaDocumentService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	RfiDocumentService rfiDocumentService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	@Autowired
	RfqDocumentService rfqDocumentService;

	@Autowired
	SupplierService supplierService;
	
	@Autowired
	RfaSupplierCqService rfaSupplierCqService;
	
	@Autowired
	RfiSupplierCqService rfiSupplierCqService;
	
	@Autowired
	RfpSupplierCqService rfpSupplierCqService;
	
	@Autowired
	RfqSupplierCqService rfqSupplierCqService;
	
	@Autowired
	RftSupplierCqService rftSupplierCqService;

	@Autowired
	RfqSupplierSorService rfqSupplierSorService;

	@Autowired
	RfpSupplierSorService rfpSupplierSorService;

	@Autowired
	RfaSupplierSorService rfaSupplierSorService;

	@Autowired
	RfiSupplierSorService rfiSupplierSorService;

	@Autowired
	RftSupplierSorService rftSupplierSorService;

	@RequestMapping(path = "/viewSupplierEventSummary/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewEventMessages(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable(name = "eventId") String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		EventPojo event = null;
		List<?> eventContacts = null;
		List<?> eventDocuments = null;
		List<?> listMeetObj = null;
		List<?> bqList = null;
		List<?> sorList = null;
		EventSupplier eventSupplier = null;
		EventPermissions eventPermissions = null;
		try {
			switch (eventType) {
			case RFA: {
				event = rfaEventService.loadSupplierEventPojoForSummeryById(eventId);
				eventContacts = rfaEventService.getAllContactForEvent(eventId);
				eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)){ 
					return "redirect:/400_error";
				}

				listMeetObj = rfaMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
				eventDocuments = rfaDocumentService.findAllRfaEventdocsbyEventId(eventId);
				bqList = RfaBqlist(eventId);

				if (CollectionUtil.isNotEmpty(bqList)) {
					model.addAttribute("bq", bqList.get(0));
				}
				sorList = RfaSorlist(eventId);

				if (CollectionUtil.isNotEmpty(sorList)) {
					model.addAttribute("sor", sorList.get(0));
				}

				List<RfaSupplierCq> supplierCq = rfaSupplierCqService.findSupplierCqStatusByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				List<RfaSupplierCqItem> supplierCqItem = rfaSupplierCqItemService.getSupplierCqItemsbySupplierIdAndEventId(eventId, SecurityLibrary.getLoggedInUserTenantId());
				Map<RfaCq, List<RfaSupplierCqItem>> cqList = new LinkedHashMap<RfaCq, List<RfaSupplierCqItem>>();
				for (RfaSupplierCqItem item : supplierCqItem) {

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<String> docIds = new ArrayList<String>();
						List<RfaCqOption> rfaCqOptions = item.getCqItem().getCqOptions();
						for (RfaCqOption rfaCqOption : rfaCqOptions) {
							docIds.add(StringUtils.checkString(rfaCqOption.getValue()));
						}
						List<EventDocument> eventDocs = rfaDocumentService.findAllRfaEventDocsByEventIdAndDocIds(eventId, docIds);
						model.addAttribute("eventDocs", eventDocs);
					}

					List<RfaSupplierCqItem> itemList = cqList.get(item.getCq());
					if (itemList == null) {
						itemList = new ArrayList<RfaSupplierCqItem>();
						itemList.add(item);
						
						if(CollectionUtil.isNotEmpty(supplierCq)) {
							RfaSupplierCq match = null;
							for(RfaSupplierCq scq : supplierCq) {
								if(scq.getCq().getId().equals(item.getCq().getId())) {
									match = scq;
									break;
								}
							}
							
							if(match != null) {
								item.getCq().setSupplierCqStatus(match.getSupplierCqStatus());
							}
						}
						
						cqList.put(item.getCq(), itemList);
					} else {
						itemList.add(item);
					}
				}
				eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("auctionRules", auctionRules);
				model.addAttribute("cqList", cqList);
				break;
			}
			case RFI: {
				event = rfiEventService.loadSupplierEventPojoForSummeryById(eventId);
				eventContacts = rfiEventService.getAllContactForEvent(eventId);
				eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)){ 
					return "redirect:/400_error";
				}
				listMeetObj = rfiMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
				eventDocuments = rfiDocumentService.findAllRfiEventdocsbyEventId(eventId);

				List<RfiSupplierCq> supplierCq = rfiSupplierCqService.findSupplierCqStatusByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				List<RfiSupplierCqItem> supplierCqItem = rfiSupplierCqItemService.getSupplierCqItemsbySupplierIdAndEventId(eventId, SecurityLibrary.getLoggedInUserTenantId());
				Map<RfiCq, List<RfiSupplierCqItem>> cqList = new LinkedHashMap<RfiCq, List<RfiSupplierCqItem>>();

				sorList = RfiSorlist(eventId);

				if (CollectionUtil.isNotEmpty(sorList)) {
					model.addAttribute("sor", sorList.get(0));
				}

				for (RfiSupplierCqItem item : supplierCqItem) {

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<String> docIds = new ArrayList<String>();
						List<RfiCqOption> rfiCqOptions = item.getCqItem().getCqOptions();
						for (RfiCqOption rfiCqOption : rfiCqOptions) {
							docIds.add(StringUtils.checkString(rfiCqOption.getValue()));
						}
						List<EventDocument> eventDocs = rfiDocumentService.findAllRfiEventDocsByEventIdAndDocIds(eventId, docIds);
						model.addAttribute("eventDocs", eventDocs);
					}

					List<RfiSupplierCqItem> itemList = cqList.get(item.getCq());
					if (itemList == null) {
						itemList = new ArrayList<RfiSupplierCqItem>();
						itemList.add(item);
						
						if(CollectionUtil.isNotEmpty(supplierCq)) {
							RfiSupplierCq match = null;
							for(RfiSupplierCq scq : supplierCq) {
								if(scq.getCq().getId().equals(item.getCq().getId())) {
									match = scq;
									break;
								}
							}
							
							if(match != null) {
								item.getCq().setSupplierCqStatus(match.getSupplierCqStatus());
							}
						}
						
						cqList.put(item.getCq(), itemList);
					} else {
						itemList.add(item);
					}
				}
				eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("cqList", cqList);
				break;
			}
			case RFP: {
				event = rfpEventService.loadSupplierEventPojoForSummeryById(eventId);
				eventContacts = rfpEventService.getAllContactForEvent(eventId);
				eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)){ 
					return "redirect:/400_error";
				}
				listMeetObj = rfpMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
				eventDocuments = rfpDocumentService.findAllEventdocsbyEventId(eventId);

				bqList = RfpBqlist(eventId);

				sorList = RfpSorlist(eventId);

				List<RfpSupplierCq> supplierCq = rfpSupplierCqService.findSupplierCqStatusByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				List<RfpSupplierCqItem> supplierCqItem = rfpSupplierCqItemService.getSupplierCqItemsbySupplierIdAndEventId(eventId, SecurityLibrary.getLoggedInUserTenantId());
				Map<RfpCq, List<RfpSupplierCqItem>> cqList = new LinkedHashMap<RfpCq, List<RfpSupplierCqItem>>();
				for (RfpSupplierCqItem item : supplierCqItem) {

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<String> docIds = new ArrayList<String>();
						List<RfpCqOption> rfpCqOptions = item.getCqItem().getCqOptions();
						for (RfpCqOption rfpCqOption : rfpCqOptions) {
							docIds.add(StringUtils.checkString(rfpCqOption.getValue()));
						}
						List<EventDocument> eventDocs = rfpDocumentService.findAllRfpEventDocsByEventIdAndDocIds(eventId, docIds);
						model.addAttribute("eventDocs", eventDocs);
					}
					List<RfpSupplierCqItem> itemList = cqList.get(item.getCq());
					if (itemList == null) {
						itemList = new ArrayList<RfpSupplierCqItem>();
						itemList.add(item);
						
						if(CollectionUtil.isNotEmpty(supplierCq)) {
							RfpSupplierCq match = null;
							for(RfpSupplierCq scq : supplierCq) {
								if(scq.getCq().getId().equals(item.getCq().getId())) {
									match = scq;
									break;
								}
							}
							
							if(match != null) {
								item.getCq().setSupplierCqStatus(match.getSupplierCqStatus());
							}
						}
						
						cqList.put(item.getCq(), itemList);
					} else {
						itemList.add(item);
					}
				}
				eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("cqList", cqList);
				break;
			}
			case RFQ: {
				event = rfqEventService.loadSupplierEventPojoForSummeryById(eventId);
				eventContacts = rfqEventService.getAllContactForEvent(eventId);
				eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)){ 
					return "redirect:/400_error";
				}
				listMeetObj = rfqMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
				eventDocuments = rfqDocumentService.findAllEventdocsbyEventId(eventId);

				bqList = RfqBqlist(eventId);
				sorList = RfqSorlist(eventId);

				List<RfqSupplierCq> supplierCq = rfqSupplierCqService.findSupplierCqStatusByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				List<RfqSupplierCqItem> supplierCqItem = rfqSupplierCqItemService.getSupplierCqItemsbySupplierIdAndEventId(eventId, SecurityLibrary.getLoggedInUserTenantId());
				Map<RfqCq, List<RfqSupplierCqItem>> cqList = new LinkedHashMap<RfqCq, List<RfqSupplierCqItem>>();
				for (RfqSupplierCqItem item : supplierCqItem) {

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<String> docIds = new ArrayList<String>();
						List<RfqCqOption> rfqCqOptions = item.getCqItem().getCqOptions();
						for (RfqCqOption rfqCqOption : rfqCqOptions) {
							docIds.add(StringUtils.checkString(rfqCqOption.getValue()));
						}
						List<EventDocument> eventDocs = rfqDocumentService.findAllRfqEventDocsByEventIdAndDocIds(eventId, docIds);
						model.addAttribute("eventDocs", eventDocs);
					}

					List<RfqSupplierCqItem> itemList = cqList.get(item.getCq());
					if (itemList == null) {
						itemList = new ArrayList<RfqSupplierCqItem>();
						itemList.add(item);
						
						if(CollectionUtil.isNotEmpty(supplierCq)) {
							RfqSupplierCq match = null;
							for(RfqSupplierCq scq : supplierCq) {
								if(scq.getCq().getId().equals(item.getCq().getId())) {
									match = scq;
									break;
								}
							}
							
							if(match != null) {
								item.getCq().setSupplierCqStatus(match.getSupplierCqStatus());
							}
						}
						
						cqList.put(item.getCq(), itemList);
					} else {
						itemList.add(item);
					}
				}
				eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("cqList", cqList);
				break;
			}
			case RFT: {
				event = rftEventService.loadSupplierEventPojoForSummeryById(eventId);
				eventContacts = rftEventService.getAllContactForEvent(eventId);
				eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)){ 
					return "redirect:/400_error";
				}
				listMeetObj = rftMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
				eventDocuments = rftDocumentService.findAllRftEventdocsbyEventId(eventId);

				bqList = RftBqlist(eventId);
				sorList = RftSorlist(eventId);
				List<RftSupplierCq> supplierCq = rftSupplierCqService.findSupplierCqStatusByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				List<RftSupplierCqItem> supplierCqItem = rftSupplierCqItemService.getSupplierCqItemsbySupplierIdAndEventId(eventId, SecurityLibrary.getLoggedInUserTenantId());
				Map<RftCq, List<RftSupplierCqItem>> cqList = new LinkedHashMap<RftCq, List<RftSupplierCqItem>>();
				for (RftSupplierCqItem item : supplierCqItem) {

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<String> docIds = new ArrayList<String>();
						List<RftCqOption> rftCqOptions = item.getCqItem().getCqOptions();
						for (RftCqOption rftCqOption : rftCqOptions) {
							docIds.add(StringUtils.checkString(rftCqOption.getValue()));
						}
						List<EventDocument> eventDocs = rfqDocumentService.findAllRfqEventDocsByEventIdAndDocIds(eventId, docIds);
						model.addAttribute("eventDocs", eventDocs);
					}

					List<RftSupplierCqItem> itemList = cqList.get(item.getCq());
					if (itemList == null) {
						itemList = new ArrayList<RftSupplierCqItem>();
						itemList.add(item);
						
						if(CollectionUtil.isNotEmpty(supplierCq)) {
							RftSupplierCq match = null;
							for(RftSupplierCq scq : supplierCq) {
								if(scq.getCq().getId().equals(item.getCq().getId())) {
									match = scq;
									break;
								}
							}
							
							if(match != null) {
								item.getCq().setSupplierCqStatus(match.getSupplierCqStatus());
							}
						}
						
						cqList.put(item.getCq(), itemList);
					} else {
						itemList.add(item);
					}
				}
				eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				model.addAttribute("cqList", cqList);
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error while loading summary page : " + e.getMessage(), e);
			return "redirect:/500_error";
		}

		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("eventMeetings", listMeetObj);
		model.addAttribute("eventContacts", eventContacts);
		model.addAttribute("eventDocuments", eventDocuments);
		model.addAttribute("supplierEventSummary", true);
		model.addAttribute("sorList", sorList);
		model.addAttribute("bqList", bqList);
		model.addAttribute("event", event);
		return "viewSupplierEventSummary";
	}

	private List<?> RftBqlist(String eventId) {
		List<RftSupplierBq> bqList = rftSupplierBqService.findRftSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
		for (RftSupplierBq bq : bqList) {
			int i = 7;

			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (bq.getField1ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField2Label()).length() > 0 && (bq.getField2ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField3Label()).length() > 0 && (bq.getField3ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField4Label()).length() > 0 && (bq.getField4ToShowSupplier())) {
				i++;
			}

			bq.setHeaderCount(i);
		}
		return bqList;
	}

	private List<?> RfpBqlist(String eventId) {
		List<RfpSupplierBq> bqList = rfpSupplierBqService.findRfpSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());

		for (RfpSupplierBq bq : bqList) {
			int i = 7;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (bq.getField1ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField2Label()).length() > 0 && (bq.getField2ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField3Label()).length() > 0 && (bq.getField3ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField4Label()).length() > 0 && (bq.getField4ToShowSupplier())) {
				i++;
			}

			bq.setHeaderCount(i);
		}
		return bqList;
	}

	private List<?> RfaBqlist(String eventId) {
		List<RfaSupplierBq> bqList = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());

		for (RfaSupplierBq bq : bqList) {
			int i = 7;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (bq.getField1ToShowSupplier())) {
				i++;

			}
			if (StringUtils.checkString(bq.getField2Label()).length() > 0 && (bq.getField2ToShowSupplier())) {
				i++;

			}
			if (StringUtils.checkString(bq.getField3Label()).length() > 0 && (bq.getField3ToShowSupplier())) {
				i++;

			}
			if (StringUtils.checkString(bq.getField4Label()).length() > 0 && (bq.getField4ToShowSupplier())) {
				i++;

			}

			bq.setHeaderCount(i);
		}
		return bqList;
	}

	private List<?> RfqBqlist(String eventId) {
		List<RfqSupplierBq> bqList = rfqSupplierBqService.findRfqSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());

		for (RfqSupplierBq bq : bqList) {
			int i = 7;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (Boolean.TRUE == bq.getField1ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField2Label()).length() > 0 && (Boolean.TRUE == bq.getField2ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField3Label()).length() > 0 && (Boolean.TRUE == bq.getField3ToShowSupplier())) {
				i++;
			}
			if (StringUtils.checkString(bq.getField4Label()).length() > 0 && (Boolean.TRUE == bq.getField4ToShowSupplier())) {
				i++;
			}
			bq.setHeaderCount(i);
		}
		return bqList;
	}


	private List<?> RfqSorlist(String eventId) {
		List<RfqSupplierSor> sorList = rfqSupplierSorService.findRfqSupplierSorbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());

		for (RfqSupplierSor bq : sorList) {
			int i = 7;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (Boolean.TRUE == bq.getField1ToShowSupplier())) {
				i++;
			}
			bq.setHeaderCount(i);
		}
		return sorList;
	}

	private List<?> RfaSorlist(String eventId) {
		List<RfaSupplierSor> sorList = rfaSupplierSorService.findRfaSupplierSorbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());

		for (RfaSupplierSor bq : sorList) {
			int i = 7;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (Boolean.TRUE == bq.getField1ToShowSupplier())) {
				i++;
			}
			bq.setHeaderCount(i);
		}
		return sorList;
	}

	private List<?> RftSorlist(String eventId) {
		List<RftSupplierSor> sorList = rftSupplierSorService.findRftSupplierSorbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());

		for (RftSupplierSor bq : sorList) {
			int i = 7;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (Boolean.TRUE == bq.getField1ToShowSupplier())) {
				i++;
			}
			bq.setHeaderCount(i);
		}
		return sorList;
	}


	private List<?> RfpSorlist(String eventId) {
		List<RfpSupplierSor> sorList = rfpSupplierSorService.findRfpSupplierSorbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());

		for (RfpSupplierSor bq : sorList) {
			int i = 7;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (Boolean.TRUE == bq.getField1ToShowSupplier())) {
				i++;
			}
			bq.setHeaderCount(i);
		}
		return sorList;
	}

	private List<?> RfiSorlist(String eventId) {
		List<RfiSupplierSor> sorList = rfiSupplierSorService.findRfiSupplierSorbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());

		for (RfiSupplierSor bq : sorList) {
			int i = 7;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0 && (Boolean.TRUE == bq.getField1ToShowSupplier())) {
				i++;
			}
			bq.setHeaderCount(i);
		}
		return sorList;
	}

	@RequestMapping(path = "/supplierSummaryReport/{eventType}/{eventId}", method = RequestMethod.POST)
	public void downloadSupplierSummaryReport(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable(name = "eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		LOG.info("Event Type : " + eventType + " Event ID :: " + eventId);
		String filename = "UnknownEventSupplierSummary.pdf";
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		JasperPrint jasperPrint = null;
		try {
			switch (eventType) {
			case RFA:
				try {
					RfaEvent event = rfaEventService.getPlainEventById(eventId);
					if (event.getEventId() != null) {
						filename = (event.getEventId()).replace("/", "-") + "_SupplierSummary.pdf";
						event = null;
						event = new RfaEvent();
						event.setId(eventId);
						jasperPrint = rfaEventSupplierService.generateSupplierSummary(event, SecurityLibrary.getLoggedInUserTenantId(), session, virtualizer);
					}

				} catch (Exception e) {
					LOG.error("Could not generate RFA Supplier Summary Report. " + e.getMessage(), e);
				}
				break;
			case RFI:
				try {
					RfiEvent event = rfiEventService.getPlainEventById(eventId);
					if (event.getEventId() != null) {
						filename = (event.getEventId()).replace("/", "-") + "_SupplierSummary.pdf";
						event = null;
						event = new RfiEvent();
						event.setId(eventId);
						jasperPrint = rfiEventSupplierService.generateSupplierSummary(event, SecurityLibrary.getLoggedInUserTenantId(), session, virtualizer);
					}

				} catch (Exception e) {
					LOG.error("Could not generate RFI Supplier Summary Report. " + e.getMessage(), e);
				}
				break;
			case RFP:
				try {
					RfpEvent event = rfpEventService.getPlainEventById(eventId);
					if (event.getEventId() != null) {
						filename = (event.getEventId()).replace("/", "-") + "_SupplierSummary.pdf";
						event = null;
						event = new RfpEvent();
						event.setId(eventId);
						jasperPrint = rfpEventSupplierService.generateSupplierSummary(event, SecurityLibrary.getLoggedInUserTenantId(), session, virtualizer);
					}

				} catch (Exception e) {
					LOG.error("Could not generate RFP Supplier Summary Report. " + e.getMessage(), e);
				}
				break;
			case RFQ:
				try {
					RfqEvent event = rfqEventService.getPlainEventById(eventId);
					if (event.getEventId() != null) {
						filename = (event.getEventId()).replace("/", "-") + "_SupplierSummary.pdf";
						event = null;
						event = new RfqEvent();
						event.setId(eventId);
						jasperPrint = rfqEventSupplierService.generateSupplierSummary(event, SecurityLibrary.getLoggedInUserTenantId(), session, virtualizer);
					}

				} catch (Exception e) {
					LOG.error("Could not generate RFQ Supplier Summary Report. " + e.getMessage(), e);
				}
				break;
			case RFT:
				try {
					RftEvent event = rftEventService.getPlainEventById(eventId);
					if (event.getEventId() != null) {
						filename = (event.getEventId()).replace("/", "-") + "_SupplierSummary.pdf";
						event = null;
						event = new RftEvent();
						event.setId(eventId);
						jasperPrint = rftEventSupplierService.generateSupplierSummary(event, SecurityLibrary.getLoggedInUserTenantId(), session, virtualizer);
					}
				} catch (Exception e) {
					LOG.error("Could not generate RFT Supplier Summary Report. " + e.getMessage(), e);
				}
				break;
			default:
				break;
			}
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("ERROR :  " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	// Supplier Auction Report
	@RequestMapping(path = "/downloadSupplierAuctionReport/{eventType}/{eventId}", method = RequestMethod.POST)
	public void supplierAuctionReport(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable(name = "eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			String filename = "SupplierAuctionReport.pdf";
			JasperPrint jasperPrint = rfaEventSupplierService.getSupplierAuctionReport(eventId, SecurityLibrary.getLoggedInUserTenantId(), session, virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}