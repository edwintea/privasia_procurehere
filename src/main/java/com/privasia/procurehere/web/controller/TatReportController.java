/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.entity.TatReport;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.TatReportService;

/**
 * @author jayshree
 */
@Controller
@RequestMapping(path = "/tatReport")
public class TatReportController {

	private static final Logger LOG = LogManager.getLogger(Global.TATREPORT_LOG);

	@Autowired
	TatReportService tatReportService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	ObjectMapper objectMapper;

	@ModelAttribute("eventStatusList")
	public List<EventStatus> getNewStatusList() {
		return Arrays.asList(EventStatus.DRAFT, EventStatus.PENDING, EventStatus.APPROVED, EventStatus.CANCELED, EventStatus.SUSPENDED, EventStatus.ACTIVE, EventStatus.CLOSED, EventStatus.FINISHED, EventStatus.COMPLETE);
	}

	@ModelAttribute("requestStatusList")
	public List<SourcingFormStatus> getSourcingStatusList() {
		List<SourcingFormStatus> sourcingFormStatusList = Arrays.asList(SourcingFormStatus.FINISHED, SourcingFormStatus.DRAFT, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED);
		return sourcingFormStatusList;
	}

	@RequestMapping(value = "/tatReport", method = RequestMethod.GET)
	public String tatReportView() {
		return "tatReportList";
	}

	@RequestMapping(value = "/tatReportListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<TatReport>> tatReportList(HttpSession session, TableDataInput input, @RequestParam(required = false) String dateTimeRange) {
		TableData<TatReport> data = null;
		LOG.info("Getting Tat Report LIst DATA .. ");

		HttpHeaders headers = new HttpHeaders();
		try {

			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//				formatter.setTimeZone(timeZone);
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);

				startDate = DateUtil.formatDateToStartTime(startDate);
				endDate = DateUtil.formatDateToEndTime(endDate);

				LOG.info("Start date : " + startDate + " End Date : " + endDate);
				double openDurationInDays = DateUtil.differenceInDays(endDate, startDate);

				LOG.info("openDurationInDays > 90 >>>>>>>>>>> : " + (openDurationInDays > 90) + " openDurationInDays  " + openDurationInDays);

				if (openDurationInDays > 90) {
					headers.add("error", "Difference between start date and end date should not be greater than 90 days");
					return new ResponseEntity<TableData<TatReport>>(null, headers, HttpStatus.OK);
				}
			}

			data = new TableData<TatReport>(tatReportService.getTatReportDataForListByTenantId(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate));
			data.setDraw(input.getDraw());

			long filterTotalCount = tatReportService.getTatReportsCountForListByTenantId(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);

			long totalCount = tatReportService.findTotalTatReportsForListByTenantId(SecurityLibrary.getLoggedInUserTenantId());

			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(filterTotalCount);
			return new ResponseEntity<TableData<TatReport>>(data, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
			headers.add("error", "Error while loading draft list : " + e.getMessage());
			return new ResponseEntity<TableData<TatReport>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/exportTatCsvReport", method = RequestMethod.POST)
	public String downloadEventCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("tatReport") TatReportPojo tatReport, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			LOG.info("Downloading TAT report CSV file....... : ");
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//				formatter.setTimeZone(timeZone);
				startDate = formatter.parse(dateTimeArr[0]);
				endDate = formatter.parse(dateTimeArr[1]);
				
				 
				LOG.info("Start date : " + startDate + " End Date : " + endDate);

				startDate = DateUtil.formatDateToStartTime(startDate);
				endDate = DateUtil.formatDateToEndTime(endDate);

				double openDurationInDays = DateUtil.differenceInDays(endDate, startDate);
				LOG.info("openDurationInDays > 90 >>>>>>>>>>> : " + (openDurationInDays > 90) + " openDurationInDays  " + openDurationInDays);
				if (openDurationInDays > 90) {
					throw new ApplicationException("Difference between start date and end date should not be greater than 90 days");
				}

				LOG.info("After tart date : " + startDate + " End Date : " + endDate);
				tatReport.setEventStart(startDate);
				tatReport.setEventEnd(endDate);
			}

			tatReport.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
			tatReport.setUserId(SecurityLibrary.getLoggedInUser().getId());
			try {
				LOG.info("Tat Report :  " + tatReport.toLogString());
				String strMessage = objectMapper.writeValueAsString(tatReport);
				LOG.info("Tat Report After :  " + strMessage);
				// jmsTemplate.setDefaultDestinationName("QUEUE.TAT.REPORT");
				jmsTemplate.send("QUEUE.TAT.REPORT", new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						TextMessage objectMessage = session.createTextMessage();
						objectMessage.setText(strMessage);
						return objectMessage;
					}
				});
			} catch (Exception e) {
				LOG.error("Error sending message to queue : " + e.getMessage(), e);
			}

			redir.addFlashAttribute("success", messageSource.getMessage("tat.report.download.csv", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while downloading TAT report csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("tat.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:tatReport";
	}

}
