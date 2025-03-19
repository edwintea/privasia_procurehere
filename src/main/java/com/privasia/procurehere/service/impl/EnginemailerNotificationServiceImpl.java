/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.EngineMailerEmail;
import com.privasia.procurehere.core.pojo.EngineMailerMailAttachments;
import com.privasia.procurehere.core.pojo.EngineMailerResponse;
import com.privasia.procurehere.core.pojo.OneSignalPushMessage;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.RequestResponseLoggingInterceptor;
import com.privasia.procurehere.integration.RestTemplateResponseErrorHandler;
import com.privasia.procurehere.service.NotificationService;

import freemarker.template.Configuration;

/**
 * @author ravi
 */
@Service("notificationService")
public class EnginemailerNotificationServiceImpl implements NotificationService {

	private static final Logger LOG = LogManager.getLogger(Global.EMAIL_LOG);

	@Value("${enginemailer.apikey}")
	String EM_API_KEY;

	@Value("${enginemailer.url}")
	String EM_API_URL;

	@Value("${enginemailer.userKey}")
	String EM_API_USER_KEY;

	@Value("${enginemailer.from.mail}")
	String EMAIL_FROM;

	@Value("${public.mail.from}")
	String PUBLIC_EMAIL_FROM;

	@Value("${push.notification.url}")
	String PUSH_APP_URL;

	@Value("${push.notification.app.id}")
	String PUSH_APP_ID;

	@Value("${push.notification.app.password}")
	String PUSH_APP_PASSWORD;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	RestTemplate restTemplate;

	public EnginemailerNotificationServiceImpl() {
		// Key for sandbox
		this.EMAIL_FROM = "v7@procurehere.com";
		this.EM_API_KEY = "a62824e3-ede6-4fcf-b195-30a50dedf64f";
	}

	public enum EmailContentType {
		PLAIN_TEXT("text/plain"), HTML("text/html");

		String value;

		private EmailContentType(String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.ipms.service.NotificationService#sendEmail(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void sendEmail(String mailto, String subject, String message) throws Exception {
		sendEmailViaEnginemailer(mailto, subject, message, EmailContentType.HTML, null, null, null, false);
	}

	@Override
	public void sendEmailWithcc(String mailto, String mailBcc, String subject, String message) throws Exception {
		sendEmailViaEnginemailer(mailto, subject, message, EmailContentType.HTML, null, null, mailBcc, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.ipms.service.NotificationService#sendPlainTextEmail(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void sendPlainTextEmail(String mailto, String subject, String message) throws Exception {
		sendEmailViaEnginemailer(mailto, subject, message, EmailContentType.PLAIN_TEXT, null, null, null, false);
	}

	@Override
	public void sendEmailWithAttachment(String mailto, String subject, String message, File file, String attachedFileName) throws ApplicationException {
		try {
			sendEmailViaEnginemailer(mailto, subject, message, EmailContentType.HTML, file, attachedFileName, null, false);
		} catch (Exception e) {
			LOG.error("Mail Notification error : " + e.getMessage(), e);
			throw new ApplicationException("Error sending email with attachment : " + e.getMessage());
		}
	}

	private void sendEmailViaEnginemailer(String mailto, String subject, String message, EmailContentType contentType, File file, String attachedFileName, String mailBcc, boolean publicEmail) throws Exception {

		EngineMailerEmail engmail = new EngineMailerEmail();
		engmail.setaPIKey(EM_API_KEY);
		if (publicEmail) {
			engmail.setSenderEmail(PUBLIC_EMAIL_FROM);
		} else {
			engmail.setSenderEmail(EMAIL_FROM);
		}

		engmail.setUserKey(EM_API_USER_KEY);
		engmail.setSubject(subject);
		engmail.setSubmittedContent(message);
		engmail.setSenderName("Procurehere");
		engmail.setToEmail(mailto);

		if (StringUtils.checkString(mailBcc).length() > 0) {
			mailBcc = mailBcc.replaceAll(" ", "");
			String[] mailBccList = null;
			List<String> bccs = new ArrayList<String>();
			if (mailBcc != null && (mailBcc.contains(",") || mailBcc.contains(";")))
				mailBccList = StringUtils.checkString(mailBcc).split("\\,|\\;");
			if (mailBccList != null) {
				for (String bcc : mailBccList) {
					bccs.add(bcc);
				}
				engmail.setcCEmails(bccs);
			} else {
				bccs.add(mailBcc);
				engmail.setcCEmails(bccs);
			}
		}

			// Attachments
			if (file != null && StringUtils.checkString(attachedFileName).length() > 0) {
				List<EngineMailerMailAttachments> list = new ArrayList<EngineMailerMailAttachments>();
				EngineMailerMailAttachments attachments = new EngineMailerMailAttachments();
				byte[] fileData = IOUtils.toByteArray(new FileInputStream(file));

				Base64 base64 = new Base64();
				String dataString = base64.encodeAsString(fileData);
				attachments.setContent(dataString);
				attachments.setFilename(attachedFileName);
				list.add(attachments);
				if (list != null && list.size() > 0) {
					engmail.setAttachments(list);
				}
			}

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<EngineMailerEmail> request = new HttpEntity<EngineMailerEmail>(engmail, headers);
		String responseMsg = restTemplate.postForObject(EM_API_URL, request, String.class);
		LOG.info("Response : " + responseMsg);
//		responseMsg = responseMsg.substring(1, responseMsg.length() -1);
//		responseMsg = responseMsg.replace("\\\"", "\"");
//		responseMsg = responseMsg.replaceAll("\\\\u000d\\\\u000a\\s*", "");
//		ObjectMapper mapperObj = new ObjectMapper();
//		EngineMailerResponse response =	mapperObj.readValue(responseMsg, EngineMailerResponse.class);
//		LOG.info("Response : " + response.toString());
	}
	
	public static void main(String[] args) {
//		String responseMsg = "\"{\r\n"
//				+ "  \"Result\": {\r\n"
//				+ "    \"StatusCode\": \"200\",\r\n"
//				+ "    \"Status\": \"OK\",\r\n"
//				+ "    \"TransactionID\": \"2842973\"\r\n"
//				+ "  }\r\n"
//				+ "}\"";
		
		String responseMsg = "\"{\\u000d\\u000a  \\\"Result\\\": {\\u000d\\u000a    \\\"StatusCode\\\": \\\"200\\\",\\u000d\\u000a    \\\"Status\\\": \\\"OK\\\",\\u000d\\u000a    \\\"TransactionID\\\": \\\"2843010\\\"\\u000d\\u000a  }\\u000d\\u000a}\"";
		
		System.out.println("Input : " + responseMsg);
		responseMsg = responseMsg.substring(1, responseMsg.length() -1);
		System.out.println("Input : " + responseMsg);
		responseMsg = responseMsg.replace("\\\"", "\"");
		System.out.println("Input : " + responseMsg);
		responseMsg = responseMsg.replaceAll("\\\\u000d\\\\u000a\\s*", "");
		System.out.println("Input : " + responseMsg);
		
		ObjectMapper mapperObj = new ObjectMapper();
		EngineMailerResponse response = null;
		try {
			response = mapperObj.readValue(responseMsg, EngineMailerResponse.class);
			System.out.println("Response : " + response.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				String data = "";

				try {
					data = (String) map.get("eventName");
				} catch (Exception e) {
					LOG.info("ERROR while converting map to json :" + e.getMessage(), e);
				}

				LOG.info("Sending request email to : " + mailTo + " Subject : " + subject + " Data : " + data);

				String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), map);
				sendEmail(mailTo, subject, message);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	@Override
	@Async
	public void sendEmailWithBcc(String mailTo, String mailBcc, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				LOG.info("Sending request email to : " + mailTo + " Bcc : " + mailBcc);
				String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), map);
				sendEmailViaEnginemailer(mailTo, subject, message, EmailContentType.HTML, null, null, mailBcc, false);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	/**
	 * <pre>
	 * 		Map<String, String> additionalMessagePayload = new HashMap<String, String>();
	 * 		additionalMessagePayload.put("MESSAGE_TYPE", "EVENT_CLOSED");
	 * 		additionalMessagePayload.put("eventId", eventId);
	 * 		additionalMessagePayload.put("eventTitle", eventTitle);
	 * 		additionalMessagePayload.put("closedDate", closedDate);
	 * 		
	 * 		List<String> targetDeviceIds should be set to null to push notifications to all devices.
	 * 		
	 * 		notificationService.pushOneSignalNotification("This is a test Notification Message", null, additionalMessagePayload, targetDeviceIds);
	 * </pre>
	 */
	@Override
	@Async
	public void pushOneSignalNotification(String message, String messageUrl, Map<String, String> payload, List<String> targetDeviceIds) {

		try {
			OneSignalPushMessage pushMessage = new OneSignalPushMessage(PUSH_APP_ID, message, messageUrl, payload, targetDeviceIds);
			String fullMessage = "";
			try {
				ObjectMapper mapper = new ObjectMapper();
				fullMessage = mapper.writeValueAsString(pushMessage);
				LOG.info("Message Payload: " + fullMessage);
			} catch (Exception e) {
				LOG.error("Error printing message payload. Error : " + e.getMessage(), e);
				return;
			}

			String jsonResponse = null;

			URL url = new URL(PUSH_APP_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Authorization", "Basic " + PUSH_APP_PASSWORD);
			con.setRequestMethod("POST");

			byte[] sendBytes = fullMessage.getBytes("UTF-8");
			con.setFixedLengthStreamingMode(sendBytes.length);

			OutputStream outputStream = con.getOutputStream();
			outputStream.write(sendBytes);

			int httpResponse = con.getResponseCode();
			LOG.info("Push Notification httpResponse: " + httpResponse);

			if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
				Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			} else {
				Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
				jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
				scanner.close();
			}
			LOG.info("PUSH Notificaton jsonResponse:\n" + jsonResponse);

		} catch (Throwable e) {
			LOG.error("Error during push message. " + e.getMessage(), e);
		}

	}

	// ================================================================================================

	public static void sendSMS(String from, String to, String message, String userName, String password, String url) throws Exception {
		message = URLEncoder.encode(message, "UTF-8");
		if (isValidPhoneNumber(to) && isValidMessage(message)) {
			ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
			restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
			Map<String, String> params = new HashMap<String, String>();
			params.put("Gw-Username", userName);
			params.put("Gw-Password", password);
			params.put("Gw-from", from);
			params.put("Gw-to", to);
			params.put("Gw-Text", message);

			@SuppressWarnings("unused")
			String responseMsg = restTemplate.postForObject(url, params, String.class);

		} else {
			// System.out.println("message not send!");
		}
	}

	public static void sendSMS(String from, String[] to, String message, String userName, String password, String url) throws Exception {
		message = URLEncoder.encode(message, "UTF-8");
		if (to.length <= 25) {
			if (isValidPhoneNumber(to) && isValidMessage(message)) {
				String toMultiple = StringUtils.join(to, ",");

				ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
				RestTemplate restTemplate = new RestTemplate(factory);
				restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
				restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
				Map<String, String> params = new HashMap<String, String>();
				params.put("Gw-Username", userName);
				params.put("Gw-Password", password);
				params.put("Gw-from", from);
				params.put("Gw-to", toMultiple);
				params.put("Gw-Text", message);
				@SuppressWarnings("unused")
				String responseMsg = restTemplate.postForObject(url, params, String.class);
			}
		} else {
			System.out.println("Should not be more than 25 Recipient!");
		}
	}

	public static boolean isValidPhoneNumber(String to) throws Exception {
		boolean isValid;
		Pattern pattern = Pattern.compile("\\d{11,12}");
		Matcher matcher = pattern.matcher(to);

		if (matcher.matches()) {
			isValid = true;
			// System.out.println("Phone number valid!");
		} else {
			System.out.println("Invalid Phone Number!! " + to);
			isValid = false;
		}

		return isValid;
	}

	public static boolean isValidPhoneNumber(String[] toMultiple) throws Exception {
		boolean isValid = false;

		for (int i = 0; i < toMultiple.length; i++) {
			if (isValidPhoneNumber(toMultiple[i])) {
				isValid = true;
			} else {
				System.out.println("Phone number " + (toMultiple[i]) + " is not valid!");
				isValid = false;
				break;
			}
		}

		return isValid;
	}

	public static boolean isValidMessage(String message) throws Exception {
		boolean isValid;
		Pattern pattern = Pattern.compile("[\\w\\W]{0,150}");
		Matcher matcher = pattern.matcher(message);

		if (matcher.matches()) {
			isValid = true;
			// System.out.println("Message valid!");
		} else {
			System.out.println("Message must not exceed 150 characters!!");
			isValid = false;
		}
		return isValid;
	}

	public static void sendFax(String from, String faxto, String faxDomain, byte[] pdfBytes, String attachedFileName, String host, String port, String userName, String password) throws ApplicationException {

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		if (StringUtils.checkString(userName).length() > 0 && StringUtils.checkString(password).length() > 0) {
			mailSender.setUsername(userName);
			mailSender.setPassword(password);
		}

		Properties properties = new Properties();
		properties.setProperty("mail.debug", "false");
		properties.setProperty("mail.smtp.host", host);
		properties.setProperty("mail.smtp.port", port);
		mailSender.setJavaMailProperties(properties);

		MimeMessage mimeMessag = mailSender.createMimeMessage();
		try {
			faxto = faxto.replaceAll(" ", "") + faxDomain;
			LOG.info("sending fax to ------->" + faxto);
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessag, true);
			String[] mailList = null;
			if (faxto != null && (faxto.contains(";") || faxto.contains(";")))
				mailList = StringUtils.checkString(faxto).split("\\;|\\,");

			if (mailList != null)
				helper.setTo(mailList);
			else
				helper.setTo(faxto);

			helper.setFrom(from);
			// helper.setSubject(subject);
			// helper.setText(message, true);
			if (pdfBytes != null) {
				final InputStreamSource attachment = new ByteArrayResource(pdfBytes);
				helper.addAttachment(attachedFileName, attachment);
			}
			mailSender.send(mimeMessag);

		} catch (MessagingException e) {
			LOG.error("Fax Notification error : " + e.getMessage(), e);
			throw new ApplicationException("Error occured while sending Fax :- " + e.getMessage());
		} catch (MailSendException e) {
			LOG.error("Fax Notification error : " + e.getMessage(), e);
			if (e.getRootCause() != null && e.getRootCause().getMessage().contains("Connection refused")) {
				LOG.error("Error sending Fax message : " + e.getMessage(), e);
				throw new ApplicationException("Connection error occured while sending Fax :- " + e.getRootCause().getMessage());
			}
			throw new ApplicationException("Error occured while sending Fax :- " + e.getMessage());

		} catch (Exception e) {
			LOG.error("Fax Notification error : " + e.getMessage(), e);
			if (e instanceof ApplicationException) {
				throw (ApplicationException) e;
			} else {
				LOG.error("Error sending Fax message for pdf : " + e.getMessage(), e);
				throw new ApplicationException(e.getMessage());
			}
		}
	}

}
