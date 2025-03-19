/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.OneSignalPushMessage;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.RequestResponseLoggingInterceptor;
import com.privasia.procurehere.integration.RestTemplateResponseErrorHandler;
import com.privasia.procurehere.service.NotificationService;
import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.MailSettings;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.Setting;

import freemarker.template.Configuration;

/**
 * @author Arc
 */
@Service("sendGridnotificationService")
public class SendGridNotificationServiceImpl implements NotificationService {

	private static final Logger LOG = LogManager.getLogger(Global.EMAIL_LOG);

	@Value("${email.sendgrid.api.key}")
	String SENDGRID_API_KEY;

	@Value("${email.mail.from}")
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

	@Value("${environment-key}")
	String ENVIRONMENT_KEY;

	public SendGridNotificationServiceImpl() {
		// Key for sandbox
		this.EMAIL_FROM = "no-reply-sandbox@procurehere.com";
		this.SENDGRID_API_KEY = "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";
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
		sendEmailViaSendGrid(mailto, subject, message, EmailContentType.HTML, null, null, null, false);
	}

	@Override
	public void sendEmailWithcc(String mailto, String mailBcc, String subject, String message) throws Exception {
		sendEmailViaSendGrid(mailto, subject, message, EmailContentType.HTML, null, null, mailBcc, false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.ipms.service.NotificationService#sendPlainTextEmail(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void sendPlainTextEmail(String mailto, String subject, String message) throws Exception {
		sendEmailViaSendGrid(mailto, subject, message, EmailContentType.PLAIN_TEXT, null, null, null, false);
	}

	@Override
	public void sendEmailWithAttachment(String mailto, String subject, String message, File file, String attachedFileName) throws ApplicationException {
		try {
			sendEmailViaSendGrid(mailto, subject, message, EmailContentType.HTML, file, attachedFileName, null, false);
		} catch (Exception e) {
			LOG.error("Mail Notification error : " + e.getMessage(), e);
			throw new ApplicationException("Error sending email with attachment : " + e.getMessage());
		}
	}

	/**
	 * @param mailto
	 * @param subject
	 * @param message
	 * @param contentType
	 * @param mailBcc TODO
	 * @param publicEmail TODO
	 * @throws Exception
	 */
	private void sendEmailViaSendGrid(String mailto, String subject, String message, EmailContentType contentType, File file, String attachedFileName, String mailBcc, boolean publicEmail) throws Exception {
		Email from = null;
		if (publicEmail) {
			from = new Email(PUBLIC_EMAIL_FROM, "Procurehere");
		} else {
			from = new Email(EMAIL_FROM, "Procurehere");
		}
		Content content = new Content(contentType.getValue(), message);
		Mail mail = new Mail();
		if (StringUtils.checkString(ENVIRONMENT_KEY).equalsIgnoreCase("local")) {
			MailSettings mailSettings = new MailSettings();
			Setting setting = new Setting();
			setting.setEnable(true);
			mailSettings.setSandboxMode(setting);
			mail.setMailSettings(mailSettings);
		}

		mail.setFrom(from);
		mail.setSubject(subject);
		mail.addContent(content);
		Personalization personalization = new Personalization();

		mailto = mailto.replaceAll(" ", "");
		String[] mailList = null;
		if (mailto != null && (mailto.contains(",") || mailto.contains(";")))
			mailList = StringUtils.checkString(mailto).split("\\,|\\;");

		if (mailList != null)
			for (String to : mailList) {
				personalization.addTo(new Email(to));
			}
		else {
			personalization.addTo(new Email(mailto));
		}

		if (StringUtils.checkString(mailBcc).length() > 0) {
			mailBcc = mailBcc.replaceAll(" ", "");
			String[] mailBccList = null;
			if (mailBcc != null && (mailBcc.contains(",") || mailBcc.contains(";")))
				mailBccList = StringUtils.checkString(mailBcc).split("\\,|\\;");

			if (mailBccList != null)
				for (String to : mailBccList) {
					personalization.addBcc(new Email(to));
				}
			else {
				personalization.addBcc(new Email(mailBcc));
			}
		}
		mail.addPersonalization(personalization);

		// Attachments
		if (file != null && StringUtils.checkString(attachedFileName).length() > 0) {
			Attachments attachments = new Attachments();

			byte[] fileData = IOUtils.toByteArray(new FileInputStream(file));

			Base64 base64 = new Base64();
			String dataString = base64.encodeAsString(fileData);

			attachments.setContent(dataString);
			// attachments.setType("application/pdf");
			attachments.setFilename(attachedFileName);
			attachments.setDisposition("attachment");
			// attachments.setContentId("Balance Sheet");
			mail.addAttachments(attachments);
		}

		SendGrid sg = new SendGrid(SENDGRID_API_KEY);
		// SendGrid sg = new SendGrid("8fWP1Yf3R8Cgw-WWACR8gg");
		Request request = new Request();
		try {
			// request.setMethod(Method.POST);
			// request.setEndpoint("mail/send");
			// request.setBody(mail.build());
			request.method = Method.POST;
			request.endpoint = "mail/send";
			request.body = mail.build();
			Response response = sg.api(request);
			LOG.info("SendGrid response status code : " + response.statusCode);
			LOG.info("SendGrid response body : " + response.body);
			LOG.info("SendGrid response headers : " + response.headers);

			if (response.statusCode > 300) {
				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> map = new HashMap<String, Object>();
				map = mapper.readValue(response.body, new TypeReference<Map<String, String>>() {
				});
				if (map != null) {
					throw new Exception((String) map.get("message"));
				}
			}
		} catch (Exception ex) {
			LOG.error("Error sending email : " + ex.getMessage(), ex);
			throw ex;
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
				sendEmailViaSendGrid(mailTo, subject, message, EmailContentType.HTML, null, null, mailBcc, false);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	public static void main(String[] args) {
		System.out.println("hiiii");

		try {
			// sendFax("test", "yogesh.djadhav@privatech.in", "test", "test", "hii", null, buildPdf("appUrl",
			// "supplierName", "buyerName", "eventName", "businessUnit", "eventType", "refrance", "status"), "test.pdf",
			// "gator4217.hostgator.com", "587", false, "noreply@proficient.my", "simbiotik@123");
			// sendSMS("9762910780", "Hii", "", "", "", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("wow");
		/*
		 * try { new SendGridNotificationServiceImpl().sendEmail("nitin@recstech.com", "Test email from SENDGRID",
		 * "Dekh chalta hai kya ye email service. Chala toh think warna phek de bhidu."); } catch (Exception e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
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

			// String strJsonBody = "{" + "\"app_id\": \"6691b4b7-ad41-45ed-baf2-8d1388438ec4\","
			// + "\"included_segments\": [\"All\"]," + "\"data\": {\"foo\": \"bar\"},"
			// + "\"contents\": {\"en\": \"English Message\"}" + "}";

			// LOG.info("strJsonBody:\n" + strJsonBody);

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
		/*
		 * if (!to.startsWith("60")) { to = "6" + to; }
		 */

		message = URLEncoder.encode(message, "UTF-8");
		if (isValidPhoneNumber(to) && isValidMessage(message)) {
			// DefaultHttpClient httpclient = new DefaultHttpClient();
			// // System.out.println(url + "?" + "username=" + username + "&password=" + password + "&type=" + type +
			// // "&from=" + from + "&to=" + to + "&message=" + message);
			// HttpGet httpget = new HttpGet(url + "?" + "username=" + username + "&password=" + password + "&type=" +
			// type + "&from=" + from + "&to=" + to + "&message=" + message);
			// HttpResponse response = httpclient.execute(httpget);
			// StatusLine status = response.getStatusLine();
			// if (status.getStatusCode() == 200) {
			// // System.out.println(status);
			// // System.out.println("message sent...");
			// }
			// httpclient.getConnectionManager().shutdown();
			System.out.println("hiiii");
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

				/*
				 * DefaultHttpClient httpclient = new DefaultHttpClient(); HttpPost httpPost = new HttpPost(url);
				 * List<NameValuePair> params = new ArrayList<NameValuePair>(); params.add(new
				 * BasicNameValuePair("Gw-Username", SMSHttpEx.username)); params.add(new
				 * BasicNameValuePair("Gw-Password", SMSHttpEx.password)); params.add(new BasicNameValuePair("Gw-from",
				 * SMSHttpEx.from)); params.add(new BasicNameValuePair("Gw-to", toMultiple)); params.add(new
				 * BasicNameValuePair("Gw-Text", message)); httpPost.setEntity(new UrlEncodedFormEntity(params));
				 * HttpResponse response = httpclient.execute(httpPost); StatusLine status = response.getStatusLine();
				 * String responseString = new BasicResponseHandler().handleResponse(response); if
				 * (status.getStatusCode() == 202 && responseString.indexOf("status=0") != -1) {
				 * //System.out.println(status); //System.out.println("message sent..." + responseString); } else {
				 * System.out.println(status); System.out.println("message send failed..." + responseString); throw new
				 * Exception("SMS could not be sent : " + responseString); }
				 * httpclient.getConnectionManager().shutdown(); } else { //System.out.println("message not sent..."); }
				 */

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
