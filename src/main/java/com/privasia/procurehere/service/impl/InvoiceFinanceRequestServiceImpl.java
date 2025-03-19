/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.InvoiceFinanceRequestDao;
import com.privasia.procurehere.core.dao.OnboardedBuyerDao;
import com.privasia.procurehere.core.dao.OnboardedSupplierDao;
import com.privasia.procurehere.core.dao.PoFinanceRequestDao;
import com.privasia.procurehere.core.dao.SupplierAuditTrailDao;
import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.entity.InvoiceFinanceRequest;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.PoFinanceRequest;
import com.privasia.procurehere.core.entity.PoFinanceRequestDocuments;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.enums.InvoiceStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.FinansHereInvoiceRequestPojo;
import com.privasia.procurehere.core.pojo.FinansHerePoRequestPojo;
import com.privasia.procurehere.core.pojo.FinansHereResponsePojo;
import com.privasia.procurehere.core.pojo.InvoiceFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestDocumentsPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.RequestResponseLoggingInterceptor;
import com.privasia.procurehere.integration.RestTemplateResponseErrorHandler;
import com.privasia.procurehere.service.InvoiceFinanceRequestService;
import com.privasia.procurehere.service.InvoiceService;
import com.privasia.procurehere.service.PoService;

/**
 * @author nitin
 */
@Service
@Transactional(readOnly = true)
public class InvoiceFinanceRequestServiceImpl implements InvoiceFinanceRequestService {

	private static final Logger LOG = LogManager.getLogger(InvoiceFinanceRequestServiceImpl.class);

	@Autowired
	InvoiceFinanceRequestDao invoiceFinanceRequestDao;

	@Autowired
	PoFinanceRequestDao poFinanceRequestDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	SupplierAuditTrailDao supplierAuditTrailDao;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	OnboardedBuyerDao onboardedBuyerDao;

	@Autowired
	OnboardedSupplierDao onboardedSupplierDao;

	@Autowired
	PoService poService;
	
	@Value("${financehere.api.url}")
	String finansHereApiUrl;
	
	@Value("${financehere.api.key}")
	String finansHereApiKey;
	
	@Autowired
	ObjectMapper objectMapper;

	@Override
	@Transactional(readOnly = false)
	public InvoiceFinanceRequest save(InvoiceFinanceRequest invoiceFinanceRequest) {
		return invoiceFinanceRequestDao.save(invoiceFinanceRequest);
	}

	@Override
	@Transactional(readOnly = false)
	public InvoiceFinanceRequest update(InvoiceFinanceRequest invoiceFinanceRequest) {
		return invoiceFinanceRequestDao.update(invoiceFinanceRequest);
	}

	@Override
	@Transactional(readOnly = false)
	public Invoice requestFinancing(String invoiceId, User actionBy) throws ApplicationException {
		Invoice invoice = invoiceService.findByInvoiceId(invoiceId);
		if (invoice == null) {
			LOG.error("Invoice not found by is : " + invoiceId);
			throw new ApplicationException("Invoice not found. Contact Procurehere Administrator");
		}

		if (invoice.getStatus() != InvoiceStatus.ACCEPTED) {
			throw new ApplicationException("Cannot raise request for financing for non-accepted invoices. Please contact your buyer.");
		}
		if (!invoice.getSupplier().getId().equals(actionBy.getTenantId())) {
			throw new ApplicationException("You are not authoried to request for financing for this invoice : " + invoice.getInvoiceId());
		}
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("AUTH-KEY", finansHereApiKey);

			FinansHereInvoiceRequestPojo request = new FinansHereInvoiceRequestPojo();
			request.setBuyerId(invoice.getBuyer().getId());
			request.setBuyerName(invoice.getBuyer().getCompanyName());
			request.setBuyerRoc(invoice.getBuyer().getCompanyRegistrationNumber());
			request.setCurrencyCode(invoice.getCurrency().getCurrencyCode());
			request.setDecimal(invoice.getDecimal());
			request.setPaymentTerms(invoice.getPaymentTermDays());
			request.setInvoiceAmount(invoice.getGrandTotal());
			request.setInvoiceDate(invoice.getInvoiceSendDate());
			request.setInvoiceId(invoice.getId());
			request.setInvoiceTitle(invoice.getName());
			request.setInvoiceNumber(invoice.getInvoiceId());
			request.setPoNumber(invoice.getPo().getPoNumber());
			request.setPoId(invoice.getPo().getId());
			request.setSupplierId(invoice.getSupplier().getId());
			request.setSupplierName(invoice.getSupplierName());
			request.setSupplierRoc(invoice.getSupplier().getCompanyRegistrationNumber());
			request.setActionBy(actionBy.getId());
			
			LOG.info("Request values >> : " + request.toLogString());

			
			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
			Map<String, String> maps = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {});
			LOG.info("Data values : " + Arrays.asList(maps));
			parameters.setAll(maps);
			
			HttpEntity<MultiValueMap<String, String>> poRequest = new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			FinansHereResponsePojo res = restTemplate.postForObject(finansHereApiUrl + "create/invoice", poRequest, FinansHereResponsePojo.class);

			if (StringUtils.checkString(res.getStatus()).equalsIgnoreCase("OK")) {
				LOG.info("Invoice Finansing request sent successfully to FinansHere : " + res.getMessage());
			} else {
				LOG.error("Error from Finanshere API : " + res.getMessage());
				throw new ApplicationException("Error during requesting for Invoice Financing : " + res.getMessage());
			}
		} catch (ApplicationException e) {
			LOG.error("Error during requesting for Invoice Financing : " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error("Error while raising financing request. Error : " + e.getMessage(), e);
			throw new ApplicationException("Error while raising financing request. Error : " + e.getMessage());
		}
		
		
		
//		OnboardedBuyer ob = onboardedBuyerDao.getOnboardedBuyerByBuyerId(invoice.getBuyer().getId());
//
//		PoFinanceRequest req = null;
//		try {
//			req = poFinanceRequestDao.findPoFinanceRequestByPoId(invoice.getPo().getId());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		InvoiceFinanceRequest request = new InvoiceFinanceRequest(invoice);
//		request.setFinanshereBuyer(ob);
//		request.setRequesteBy(actionBy);
//		request.setRequestedDate(new Date());
//		request.setPoRequest(req);
//		if(req != null) {
//			if(req.getRequestStatus() != FinanceRequestStatus.ACTIVE) {
//				throw new ApplicationException("Cannot Accept request for financing for this invoice : " + invoice.getInvoiceId() + " as the Financing for PO : " + req.getPoNumber() + " is not yet in Active status");
//			}
//			request.setFunder(req.getFunder());
//			request.setRequestStatus(FinanceRequestStatus.IN_PROGRESS);
//			request.setAcceptedBy(req.getAcceptedBy());
//			request.setAcceptedDate(new Date());
//		}
//
//		request = invoiceFinanceRequestDao.save(request);

		return invoice;
	}
	

	@Override
	@Transactional(readOnly = false)
	public Po requestFinancingForPo(String poId, User actionBy) throws ApplicationException {
		Po po = poService.findById(poId);

		boolean onboarded = false;
		long supplierCount = findOnboardedSupplierForFinancingRequest(SecurityLibrary.getLoggedInUserTenantId());
		if (supplierCount > 0) {
			long buyerCount = findOnboardedBuyerForInvoiceRequest(po.getBuyer().getId());
			if (buyerCount > 0) {
				onboarded = true;
			}
		}

		if (!onboarded) {
			String party = "";
			if (supplierCount == 0) {
				party = "You are";
			} else {
				party = "the Buyer is";
			}
			throw new ApplicationException("You are not allowed to raise financing request as " + party + " not onboarded in Finanshere.");
		}
		
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("AUTH-KEY", finansHereApiKey);

			FinansHerePoRequestPojo request = new FinansHerePoRequestPojo();
			request.setBuyerId(po.getBuyer().getId());
			request.setBuyerName(po.getBuyer().getCompanyName());
			request.setBuyerRoc(po.getBuyer().getCompanyRegistrationNumber());
			request.setCurrencyCode(po.getCurrency().getCurrencyCode());
			request.setDecimal(po.getDecimal());
			request.setPaymentTerms(po.getPaymentTermDays());
			request.setPoAmount(po.getGrandTotal());
			request.setPoDate(po.getOrderedDate());
			request.setPoId(po.getId());
			request.setPoName(po.getName());
			request.setPoNumber(po.getPoNumber());
			request.setSupplierId(po.getSupplier().getSupplier().getId());
			request.setSupplierName(po.getSupplierName());
			request.setSupplierRoc(po.getSupplier().getSupplier().getCompanyRegistrationNumber());
			request.setActionBy(actionBy.getId());
			
			LOG.info("Request values >> : " + request.toLogString());

			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
			Map<String, String> maps = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {});
			LOG.info("Data values : " + Arrays.asList(maps));
			parameters.setAll(maps);
			
			HttpEntity<MultiValueMap<String, String>> poRequest = new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			FinansHereResponsePojo res = restTemplate.postForObject(finansHereApiUrl + "create/po", poRequest, FinansHereResponsePojo.class);

			if (StringUtils.checkString(res.getStatus()).equalsIgnoreCase("OK")) {
				LOG.info("PO Finansing request sent successfully to FinansHere : " + res.getMessage());
			} else {
				LOG.error("Error from Finanshere API : " + res.getMessage());
				throw new ApplicationException("Error during requesting for PO Financing : " + res.getMessage());
			}
		} catch (ApplicationException e) {
			LOG.error("Error during requesting for PO Financing : " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error("Error while raising financing request. Error : " + e.getMessage(), e);
			throw new ApplicationException("Error while raising financing request. Error : " + e.getMessage());
		}


//		PoFinanceRequest req = new PoFinanceRequest(po);
//		req.setRequestedBy(actionBy);
//
//		// Set the Finanshere Supplier
//		OnboardedSupplier os = onboardedSupplierDao.getOnboardedSupplierBySupplierId(req.getSupplier().getId());
//		req.setFinanshereSupplier(os);
//
//		// Set the Finanshere Buyer
//		OnboardedBuyer ob = onboardedBuyerDao.getOnboardedBuyerByBuyerId(req.getBuyer().getId());
//		req.setFinanshereBuyer(ob);
//
//		req = poFinanceRequestDao.save(req);

		return po;
	}

	@Override
	@Transactional(readOnly = false)
	public PoFinanceRequest acceptFinanshereOffer(String requestId, User actionBy) throws ApplicationException {

		PoFinanceRequest req = poFinanceRequestDao.findById(requestId);

		if(req == null) {
			throw new ApplicationException("Invalid PO Financing Request.");
		}
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("AUTH-KEY", finansHereApiKey);

			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
			parameters.add("poFinanceRequestId", requestId);
			parameters.add("actionBy", actionBy.getId());

			LOG.info("Data values >> : " + Arrays.asList(parameters));

			HttpEntity<MultiValueMap<String, String>> poRequest = new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			FinansHereResponsePojo res = restTemplate.postForObject(finansHereApiUrl + "supplier/accept/offer", poRequest, FinansHereResponsePojo.class);

			if (StringUtils.checkString(res.getStatus()).equalsIgnoreCase("OK")) {
				LOG.info("PO Financing Offer Acceptance request sent successfully to FinansHere : " + res.getMessage());
			} else {
				LOG.error("Error from Finanshere API during Accept of PO Financing Offer: " + res.getMessage());
				throw new ApplicationException("Error from Finanshere API during Accept of PO Financing Offer: " + res.getMessage());
			}
		} catch (ApplicationException e) {
			LOG.error("Error from Finanshere API during Accept of PO Financing Offer: " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error("Error while accepting offer. Error : " + e.getMessage(), e);
			throw new ApplicationException("Error while accepting offer. Error : " + e.getMessage());
		}

//		req.setAcceptedBy(actionBy);
//		req.setAcceptedDate(new Date());
//		req.setRequestStatus(FinanceRequestStatus.ACCEPTED);
//		req = poFinanceRequestDao.update(req);
		return req;
	}

	@Override
	@Transactional(readOnly = false)
	public PoFinanceRequest declineFinanshereOffer(String requestId, User actionBy) throws ApplicationException {

		PoFinanceRequest req = poFinanceRequestDao.findById(requestId);

		if(req == null) {
			throw new ApplicationException("Invalid PO Financing Request.");
		}
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("AUTH-KEY", finansHereApiKey);

			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
			parameters.add("poFinanceRequestId", requestId);
			parameters.add("actionBy", actionBy.getId());

			LOG.info("Data values >> : " + Arrays.asList(parameters));

			HttpEntity<MultiValueMap<String, String>> poRequest = new HttpEntity<MultiValueMap<String, String>>(parameters, headers);

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			FinansHereResponsePojo res = restTemplate.postForObject(finansHereApiUrl + "supplier/reject/offer", poRequest, FinansHereResponsePojo.class);

			if (StringUtils.checkString(res.getStatus()).equalsIgnoreCase("OK")) {
				LOG.info("PO Financing Offer Reject request sent successfully to FinansHere : " + res.getMessage());
			} else {
				LOG.error("Error from Finanshere API during Reject of PO Financing Offer: " + res.getMessage());
				throw new ApplicationException("Error from Finanshere API during Reject of PO Financing Offer: " + res.getMessage());
			}
		} catch (ApplicationException e) {
			LOG.error("Error from Finanshere API during Reject of PO Financing Offer: " + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			LOG.error("Error while rejecting offer. Error : " + e.getMessage(), e);
			throw new ApplicationException("Error while rejecting offer. Error : " + e.getMessage());
		}

//		PoFinanceRequest req = poFinanceRequestDao.findById(requestId);
//		req.setDeclinedBy(actionBy);
//		req.setDeclinedDate(new Date());
//		req.setRequestStatus(FinanceRequestStatus.DECLINED);
//		req = poFinanceRequestDao.update(req);
		return req;
	}

	@Override
	@Transactional(readOnly = false)
	public InvoiceFinanceRequest acceptFinancingRequest(String invoiceId, User actionBy) throws ApplicationException {
		Invoice invoice = invoiceService.findByInvoiceId(invoiceId);
		if (invoice == null) {
			LOG.error("Invoice not found by is : " + invoiceId);
			throw new ApplicationException("Invoice not found. Contact Procurehere Administrator");
		}

		if (invoice.getStatus() != InvoiceStatus.ACCEPTED) {
			throw new ApplicationException("Cannot accept request for financing for non-accepted invoices. Please contact your administrator.");
		}
		if (!invoice.getBuyer().getId().equals(actionBy.getTenantId())) {
			throw new ApplicationException("You are not authoried to Accept request for financing for this invoice : " + invoice.getInvoiceId());
		}

		PoFinanceRequest req = null;
		try {
			req = poFinanceRequestDao.findPoFinanceRequestByPoId(invoice.getPo().getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		InvoiceFinanceRequest request = invoiceFinanceRequestDao.findInvoiceFinanceRequestByInvoiceId(invoiceId);
		request.setRequestStatus(FinanceRequestStatus.ACCEPTED);
		// request.setRequestedDate(new Date());
		request.setAcceptedBy(actionBy);
		request.setAcceptedDate(new Date());
		
		if(req != null) {
			if(req.getRequestStatus() != FinanceRequestStatus.ACTIVE) {
				throw new ApplicationException("Cannot Accept request for financing for this invoice : " + invoice.getInvoiceId() + " as the Financing for PO : " + req.getPoNumber() + " is not yet in Active status");
			}
			request.setFunder(req.getFunder());
			request.setRequestStatus(FinanceRequestStatus.IN_PROGRESS);
			request.setAcceptedBy(req.getAcceptedBy());
			request.setAcceptedDate(new Date());
		}
		
		request = invoiceFinanceRequestDao.update(request);

		return request;
	}

	@Override
	@Transactional(readOnly = false)
	public InvoiceFinanceRequest declineFinancingRequest(String invoiceId, User actionBy) throws ApplicationException {
		Invoice invoice = invoiceService.findByInvoiceId(invoiceId);
		if (invoice == null) {
			LOG.error("Invoice not found by is : " + invoiceId);
			throw new ApplicationException("Invoice not found. Contact Procurehere Administrator");
		}

		if (invoice.getStatus() != InvoiceStatus.ACCEPTED) {
			throw new ApplicationException("Cannot decline request for financing for non-accepted invoices. Please contact your administrator.");
		}
		if (!invoice.getBuyer().getId().equals(actionBy.getTenantId())) {
			throw new ApplicationException("You are not authoried to Decline request for financing for this invoice : " + invoice.getInvoiceId());
		}

		InvoiceFinanceRequest request = invoiceFinanceRequestDao.findInvoiceFinanceRequestByInvoiceId(invoiceId);
		request.setRequestStatus(FinanceRequestStatus.DECLINED);
		request = invoiceFinanceRequestDao.update(request);

		return request;
	}

	@Override
	public InvoiceFinanceRequest findInvoiceFinanceRequestById(String id) {
		return null;
	}

	@Override
	public InvoiceFinanceRequest findInvoiceFinanceRequestByInvoiceId(String invoiceId) {
		return invoiceFinanceRequestDao.findInvoiceFinanceRequestByInvoiceId(invoiceId);
	}

	@Override
	public PoFinanceRequest findPoFinanceRequestByPoId(String poId) {
		return poFinanceRequestDao.findById(poId);
	}

	@Override
	public PoFinanceRequestPojo getPoFinanceRequestPojoByPoId(String poId) {
		return poFinanceRequestDao.getPoFinanceRequestPojoByPoId(poId);
	}

	@Override
	public long findTotalInvoiceFinanceRequestsForBuyer(String tenantId) {
		return invoiceFinanceRequestDao.findTotalInvoiceFinanceRequestsForBuyer(tenantId);
	}

	@Override
	public long findTotalInvoiceFinanceRequestsForSupplier(String tenantId) {
		return invoiceFinanceRequestDao.findTotalInvoiceFinanceRequestsForSupplier(tenantId);
	}

	@Override
	public long findTotalFilteredInvoiceFinanceRequestsForBuyer(TableDataInput tableParams, String tenantId) {
		return invoiceFinanceRequestDao.findTotalFilteredInvoiceFinanceRequestsForBuyer(tableParams, tenantId, null, null);
	}

	@Override
	public long findTotalFilteredInvoiceFinanceRequestsForSupplier(TableDataInput tableParams, String tenantId) {
		return invoiceFinanceRequestDao.findTotalFilteredInvoiceFinanceRequestsForSupplier(tableParams, tenantId, null, null);
	}

	@Override
	public List<InvoiceFinanceRequestPojo> findAllInvoiceFinanceRequestsForBuyer(TableDataInput tableParams, String tenantId) {
		return invoiceFinanceRequestDao.findAllInvoiceFinanceRequestsForBuyer(tableParams, tenantId, null, null);
	}

	@Override
	public List<InvoiceFinanceRequestPojo> findAllInvoiceFinanceRequestsForSupplier(TableDataInput tableParams, String tenantId) {
		return invoiceFinanceRequestDao.findAllInvoiceFinanceRequestsForSupplier(tableParams, tenantId, null, null);
	}

	@Override
	public long findOnboardedBuyerForInvoiceRequest(String buyerId) {
		return onboardedBuyerDao.findOnboardedBuyerForInvoiceRequest(buyerId);
	}

	@Override
	public long findOnboardedSupplierForFinancingRequest(String supplierId) {
		return onboardedSupplierDao.findOnboardedSupplierForFinancingRequest(supplierId);
	}

	@Override
	public void downloadFinansHereOfferDocument(String requestId, HttpServletResponse response) {
		
		PoFinanceRequest doc = poFinanceRequestDao.findById(requestId);
		
		response.setContentType("application/pdf");
		response.setContentLength(doc.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"FinansHere-Offer.pdf\"");
		try {
			FileCopyUtils.copy(doc.getFileData(), response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void downloadFinansHerePoDocument(String docId, HttpServletResponse response) {

		PoFinanceRequestDocuments doc = poFinanceRequestDao.getPoFinanceRequestDocumentById(docId);
		response.setContentType(doc.getContentType());
		response.setContentLength(doc.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getFileName() + "\"");
		try {
			FileCopyUtils.copy(doc.getFileData(), response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public List<PoFinanceRequestDocumentsPojo> getPoFinanceRequestDocumentsForRequestForSupplier(String requestId) {
		return poFinanceRequestDao.getPoFinanceRequestDocumentsForRequestForSupplier(requestId);
	}

}
