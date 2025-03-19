/**
 * 
 */
package com.privasia.procurehere.integration;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.dao.BuyerSettingsDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventMeeting;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.MeetingType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.RfiEventPublishRequest;
import com.privasia.procurehere.core.pojo.RfiEventPublishResponse;
import com.privasia.procurehere.core.pojo.RfqEventPublishRequest;
import com.privasia.procurehere.core.pojo.RfqEventPublishResponse;
import com.privasia.procurehere.core.pojo.RftEventPublishRequest;
import com.privasia.procurehere.core.pojo.RftEventPublishResponse;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
@Service
@Transactional(readOnly = true)
public class PublishEventServiceImpl implements PublishEventService {

	private static final Logger LOG = LogManager.getLogger(Global.API_LOG);

	@Autowired
	BuyerSettingsDao buyerSettingsDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	ObjectMapper objectMapper;

	@Override
	public void pushRfiEvent(String eventId, String tenantId, boolean isCreate) throws ApplicationException {
		try {
			LOG.info("sending rfi event called to epportal : " + eventId);
			String publishUrl = "";
			if (Boolean.TRUE == isCreate) {
				publishUrl = buyerSettingsDao.getPublishUrlFromBuyerSettingsByTenantId(tenantId, RfxTypes.RFI);
				if (StringUtils.checkString(publishUrl).length() == 0) {
					LOG.info("publish  rfi url null for tenantId:" + tenantId);
					return;
				}
			} else {
				publishUrl = buyerSettingsDao.getPublishUpdateUrlFromBuyerSettingsByTenantId(tenantId, RfxTypes.RFI);
				if (StringUtils.checkString(publishUrl).length() == 0) {
					LOG.info("publish update rfi url null for tenantId:" + tenantId);
					return;
				}
			}

			RfiEvent event = rfiEventDao.findById(eventId);
			LOG.info("sending rfi event to epportal : " + event.getEventId());
			String strTimezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(tenantId);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimezone != null) {
				timeZone = TimeZone.getTimeZone(strTimezone);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			sdf.setTimeZone(timeZone);
			RfiEventPublishRequest rfiRequest = new RfiEventPublishRequest();

			if (EventStatus.CANCELED == event.getStatus()) {
				rfiRequest.setPublish("0");
			} else {
				rfiRequest.setPublish("1");
			}

			rfiRequest.setEventId(event.getEventId());
			rfiRequest.setEventName(StringUtils.checkString(event.getEventName()));
			rfiRequest.setEventDescription(StringUtils.checkString(event.getEventDescription()));

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				String categoryCode = "";
				for (IndustryCategory category : event.getIndustryCategories()) {
					categoryCode += category.getCode() + ",";
				}
				if (StringUtils.checkString(categoryCode).length() > 0) {
					categoryCode = StringUtils.checkString(categoryCode).substring(0, StringUtils.checkString(categoryCode).length() - 1);
					rfiRequest.setCategoryCode(categoryCode);
				}
			}

			rfiRequest.setEventStartDate(event.getEventStart() != null ? sdf.format(event.getEventStart()) : null);
			rfiRequest.setEventEndDate(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : null);
			rfiRequest.setTenderStartDate(event.getExpectedTenderStartDate() != null ? sdf.format(event.getExpectedTenderStartDate()) : null);
			rfiRequest.setTenderEndDate(event.getExpectedTenderEndDate() != null ? sdf.format(event.getExpectedTenderEndDate()) : null);
			rfiRequest.setFeeStartDate(event.getFeeStartDate() != null ? sdf.format(event.getFeeStartDate()) : null);
			rfiRequest.setFeeEndDate(event.getFeeEndDate() != null ? sdf.format(event.getFeeEndDate()) : null);

			if (event.getDeliveryAddress() != null) {
				rfiRequest.setDeliveryAddress(StringUtils.checkString(event.getDeliveryAddress().getTitle()));
			}
			rfiRequest.setMinimumRating(event.getMinimumSupplierRating() != null ? event.getMinimumSupplierRating().toString() : null);
			rfiRequest.setMaximumRating(event.getMaximumSupplierRating() != null ? event.getMaximumSupplierRating().toString() : null);
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfiEventMeeting meeting : event.getMeetings()) {
					if (MeetingType.SITE_VISIT == meeting.getMeetingType()) {
						rfiRequest.setSiteVisitDate(sdf.format(meeting.getAppointmentDateTime()));
						rfiRequest.setSiteVisitLocation(StringUtils.checkString(meeting.getVenue()));
						rfiRequest.setYesNo("Yes");
						break;
					}
				}
			}
			if (StringUtils.checkString(rfiRequest.getSiteVisitLocation()).length() == 0) {
				rfiRequest.setYesNo("No");
			}
			if (event.getBusinessUnit() != null) {
				rfiRequest.setBusinessUnit(StringUtils.checkString(event.getBusinessUnit().getUnitCode()));
			}
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				rfiRequest.setContactPerson(StringUtils.checkString(event.getEventContacts().get(0).getContactName()));
				rfiRequest.setEmailAddress(StringUtils.checkString(event.getEventContacts().get(0).getComunicationEmail()));
				rfiRequest.setContactNo(StringUtils.checkString(event.getEventContacts().get(0).getContactNumber()));
			}
			rfiRequest.setFee(event.getParticipationFees() != null ? event.getParticipationFees().toString() : null);
			rfiRequest.setTenderDeposit(event.getDeposit() != null ? event.getDeposit().toString() : null);

			String url = APP_URL + "/supplier/viewSupplierEvent/RFI/" + event.getId();
			rfiRequest.setSupplierInviteUrl(url);
			rfiRequest.setEventType("Tender");

			if (event.getParticipationFees() != null && event.getParticipationFees().floatValue() > 0) {
				rfiRequest.setSaleReady("1");
			} else if (event.getDeposit() != null && event.getDeposit().floatValue() > 0) {
				rfiRequest.setSaleReady("1");
			} else {
				rfiRequest.setSaleReady("0");
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			MultiValueMap<String, String> map = convert(rfiRequest);
			LOG.info("Data values >> : " + Arrays.asList(map));

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			RfiEventPublishResponse res = restTemplate.postForObject(publishUrl, request, RfiEventPublishResponse.class);

			if (StringUtils.checkString(res.getReturnResponse()).equalsIgnoreCase("SUCCESS")) {
				LOG.info("Successfully sent RFI event for publish : " + res.getTenderId());
			} else {
				LOG.error("Error response from Portal : " + res.getReturnResponse());
			}
		} catch (Exception e) {
			LOG.error("Error publishing RFI event info to Portal : " + e.getMessage(), e);

		}
	}

	@Override
	public void pushRftEvent(String eventId, String tenantId, boolean isCreate) throws ApplicationException {
		try {
			LOG.info("sending rft event called to epportal : " + eventId);
			String publishUrl = "";
			if (Boolean.TRUE == isCreate) {
				publishUrl = buyerSettingsDao.getPublishUrlFromBuyerSettingsByTenantId(tenantId, RfxTypes.RFT);
				if (StringUtils.checkString(publishUrl).length() == 0) {
					LOG.info("publish  rft url null for tenantId:" + tenantId);
					return;
				}
			} else {
				publishUrl = buyerSettingsDao.getPublishUpdateUrlFromBuyerSettingsByTenantId(tenantId, RfxTypes.RFT);
				if (StringUtils.checkString(publishUrl).length() == 0) {
					LOG.info("publish update rft url null for tenantId:" + tenantId);
					return;
				}
			}
			RftEvent event = rftEventDao.findById(eventId);
			LOG.info("sending rft event to epportal : " + event.getEventId());
			String strTimezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(tenantId);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimezone != null) {
				timeZone = TimeZone.getTimeZone(strTimezone);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			sdf.setTimeZone(timeZone);

			RftEventPublishRequest rftRequest = new RftEventPublishRequest();

			if (EventStatus.CANCELED == event.getStatus()) {
				rftRequest.setPublish("0");
			} else {
				rftRequest.setPublish("1");
			}

			rftRequest.setEventId(event.getEventId());
			rftRequest.setEventName(StringUtils.checkString(event.getEventName()));
			rftRequest.setTajukTender(StringUtils.checkString(event.getEventName()));
			rftRequest.setTenderNo(event.getReferanceNumber());
			rftRequest.setDeskripsiTender(StringUtils.checkString(event.getEventDescription()));
			rftRequest.setEventDescription(StringUtils.checkString(event.getEventDescription()));

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				String categoryCode = "";
				for (IndustryCategory category : event.getIndustryCategories()) {
					categoryCode += category.getCode() + ",";
				}
				if (StringUtils.checkString(categoryCode).length() > 0) {
					categoryCode = StringUtils.checkString(categoryCode).substring(0, StringUtils.checkString(categoryCode).length() - 1);
					rftRequest.setCategoryCode(categoryCode);
				}
			}
			rftRequest.setEventStartDate(event.getEventStart() != null ? sdf.format(event.getEventStart()) : null);
			rftRequest.setEventEndDate(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : null);

			String url = APP_URL + "/supplier/viewSupplierEvent/RFT/" + event.getId();
			rftRequest.setSupplierInviteUrl(url);

			if (event.getDeliveryAddress() != null) {
				rftRequest.setDeliveryAddress(StringUtils.checkString(event.getDeliveryAddress().getTitle()));
			}
			rftRequest.setMinimumRating(event.getMinimumSupplierRating() != null ? event.getMinimumSupplierRating().toString() : null);
			rftRequest.setMaximumRating(event.getMaximumSupplierRating() != null ? event.getMaximumSupplierRating().toString() : null);

			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RftEventMeeting meeting : event.getMeetings()) {
					if (MeetingType.SITE_VISIT == meeting.getMeetingType()) {
						rftRequest.setSiteVisitDate(sdf.format(meeting.getAppointmentDateTime()));
						rftRequest.setSiteVisitLocation(StringUtils.checkString(meeting.getVenue()));
						rftRequest.setYesNo("Yes");
						break;
					}
				}
			}
			if (StringUtils.checkString(rftRequest.getSiteVisitLocation()).length() == 0) {
				rftRequest.setYesNo("No");
			}
			if (event.getBusinessUnit() != null) {
				rftRequest.setBusinessUnit(StringUtils.checkString(event.getBusinessUnit().getUnitCode()));
			}
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				rftRequest.setContactPerson(StringUtils.checkString(event.getEventContacts().get(0).getContactName()));
				rftRequest.setEmailAddress(StringUtils.checkString(event.getEventContacts().get(0).getComunicationEmail()));
				rftRequest.setContactNo(StringUtils.checkString(event.getEventContacts().get(0).getContactNumber()));
			}
			rftRequest.setFee(event.getParticipationFees() != null ? event.getParticipationFees().toString() : null);
			rftRequest.setTenderDeposit(event.getDeposit() != null ? event.getDeposit().toString() : null);

			rftRequest.setEventType("Tender");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			MultiValueMap<String, String> map = convert(rftRequest);
			LOG.info("Data values >> : " + Arrays.asList(map));

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			RftEventPublishResponse res = restTemplate.postForObject(publishUrl, request, RftEventPublishResponse.class);

			if (StringUtils.checkString(res.getReturnResponse()).equalsIgnoreCase("SUCCESS")) {
				LOG.info("Successfully sent RFT event for publish : " + res.getTenderId());
			} else {
				LOG.error("Error response from Portal : " + res.getReturnResponse());
			}
		} catch (Exception e) {
			LOG.error("Error publishing RFT event info to Portal : " + e.getMessage(), e);

		}
	}

	@Override
	public void pushRfqEvent(String eventId, String tenantId, boolean isCreate) throws ApplicationException {
		try {
			LOG.info("sending rfq event called to epportal : " + eventId);
			String publishUrl = "";
			if (Boolean.TRUE == isCreate) {
				publishUrl = buyerSettingsDao.getPublishUrlFromBuyerSettingsByTenantId(tenantId, RfxTypes.RFQ);
				if (StringUtils.checkString(publishUrl).length() == 0) {
					LOG.info("publish  rfq url null for tenantId:" + tenantId);
					return;
				}
			} else {
				publishUrl = buyerSettingsDao.getPublishUpdateUrlFromBuyerSettingsByTenantId(tenantId, RfxTypes.RFQ);
				if (StringUtils.checkString(publishUrl).length() == 0) {
					LOG.info("publish update rfq url null for tenantId:" + tenantId);
					return;
				}

			}

			RfqEvent event = rfqEventDao.findById(eventId);
			LOG.info("sending rfq event to epportal : " + event.getEventId());
			String strTimezone = buyerSettingsDao.getBuyerTimeZoneByTenantId(tenantId);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimezone != null) {
				timeZone = TimeZone.getTimeZone(strTimezone);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			sdf.setTimeZone(timeZone);

			RfqEventPublishRequest rfqRequest = new RfqEventPublishRequest();

			if (EventStatus.CANCELED == event.getStatus()) {
				rfqRequest.setPublish("0");
			} else {
				rfqRequest.setPublish("1");
			}

			rfqRequest.setEventId(event.getEventId());
			rfqRequest.setEventName(StringUtils.checkString(event.getEventName()));
			rfqRequest.setTajukTender(StringUtils.checkString(event.getEventName()));
			rfqRequest.setTenderNo(event.getReferanceNumber());
			rfqRequest.setDeskripsiTender(StringUtils.checkString(event.getEventDescription()));
			rfqRequest.setEventDescription(StringUtils.checkString(event.getEventDescription()));

			if (CollectionUtil.isNotEmpty(event.getIndustryCategories())) {
				String categoryCode = "";
				for (IndustryCategory category : event.getIndustryCategories()) {
					categoryCode += category.getCode() + ",";
				}
				if (StringUtils.checkString(categoryCode).length() > 0) {
					categoryCode = StringUtils.checkString(categoryCode).substring(0, StringUtils.checkString(categoryCode).length() - 1);
					rfqRequest.setCategoryCode(categoryCode);
				}
			}
			rfqRequest.setEventStartDate(event.getEventStart() != null ? sdf.format(event.getEventStart()) : null);
			rfqRequest.setEventEndDate(event.getEventEnd() != null ? sdf.format(event.getEventEnd()) : null);

			String url = APP_URL + "/supplier/viewSupplierEvent/RFQ/" + event.getId();
			rfqRequest.setSupplierInviteUrl(url);

			if (event.getDeliveryAddress() != null) {
				rfqRequest.setDeliveryAddress(StringUtils.checkString(event.getDeliveryAddress().getTitle()));
			}
			rfqRequest.setMinimumRating(event.getMinimumSupplierRating() != null ? event.getMinimumSupplierRating().toString() : null);
			rfqRequest.setMaximumRating(event.getMaximumSupplierRating() != null ? event.getMaximumSupplierRating().toString() : null);
			if (CollectionUtil.isNotEmpty(event.getMeetings())) {
				for (RfqEventMeeting meeting : event.getMeetings()) {
					if (MeetingType.SITE_VISIT == meeting.getMeetingType()) {
						rfqRequest.setSiteVisitDate(sdf.format(meeting.getAppointmentDateTime()));
						rfqRequest.setSiteVisitLocation(StringUtils.checkString(meeting.getVenue()));
						rfqRequest.setYesNo("Yes");
						break;
					}
				}
			}
			if (StringUtils.checkString(rfqRequest.getSiteVisitLocation()).length() == 0) {
				rfqRequest.setYesNo("No");
			}
			if (event.getBusinessUnit() != null) {
				rfqRequest.setBusinessUnit(StringUtils.checkString(event.getBusinessUnit().getUnitCode()));
			}
			if (CollectionUtil.isNotEmpty(event.getEventContacts())) {
				rfqRequest.setContactPerson(StringUtils.checkString(event.getEventContacts().get(0).getContactName()));
				rfqRequest.setEmailAddress(StringUtils.checkString(event.getEventContacts().get(0).getComunicationEmail()));
				rfqRequest.setContactNo(StringUtils.checkString(event.getEventContacts().get(0).getContactNumber()));
			}
			rfqRequest.setFee(event.getParticipationFees() != null ? event.getParticipationFees().toString() : null);
			rfqRequest.setTenderDeposit(event.getDeposit() != null ? event.getDeposit().toString() : null);

			rfqRequest.setEventType("RFQ");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			MultiValueMap<String, String> map = convert(rfqRequest);
			LOG.info("Data values >> : " + Arrays.asList(map));

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			RfqEventPublishResponse res = restTemplate.postForObject(publishUrl, request, RfqEventPublishResponse.class);

			if (StringUtils.checkString(res.getReturnResponse()).equalsIgnoreCase("SUCCESS")) {
				LOG.info("Successfully sent RFQ event for publish : " + res.getTenderId());
			} else {
				LOG.error("Error response from Portal : " + res.getReturnResponse());
			}
		} catch (Exception e) {
			LOG.error("Error publishing RFQ event info to Portal : " + e.getMessage(), e);

		}
	}

	MultiValueMap<String, String> convert(Object obj) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		Map<String, String> maps = objectMapper.convertValue(obj, new TypeReference<Map<String, String>>() {
		});
		LOG.info("Data values : " + Arrays.asList(maps));
		parameters.setAll(maps);
		return parameters;
	}

}
