/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.privasia.procurehere.core.utils.Global;

/**
 * @author Arc
 */
@Service
public class SmsNotificationServiceImpl {

	private static final Logger LOG = LogManager.getLogger(Global.EMAIL_LOG);

	static String url = "http://110.4.44.41:11009/cgi-bin/sendsms";
	static String username = "iwk2";
	static String password = "kVh293";
	static String type = "0";
	static String from = "IWK";

	public static void sendSms(String to, String message, String url, String username, String password, String from) throws Exception {
		if (!to.startsWith("60")) {
			to = "6" + to;
		}
		// message = URLEncoder.encode(message,"Windows-1252");
		if (isValidPhoneNumber(to) && isValidMessage(message)) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("Gw-Username", username));
			params.add(new BasicNameValuePair("Gw-Password", password));
			params.add(new BasicNameValuePair("Gw-from", from));
			params.add(new BasicNameValuePair("Gw-to", to));
			params.add(new BasicNameValuePair("Gw-Text", message));
			httpPost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse response = httpclient.execute(httpPost);
			StatusLine status = response.getStatusLine();
			String responseString = new BasicResponseHandler().handleResponse(response);
			if (status.getStatusCode() == 202 && responseString.indexOf("status=0") != -1) {
				LOG.info("message Sent" + responseString + " Status:" + status);
			} else {
				LOG.info("message send failed..." + responseString + " Status:" + status);
				throw new Exception("SMS could not be sent : " + responseString);
			}
			httpclient.getConnectionManager().shutdown();
		} else {
			// System.out.println("message not send!");
		}
	}

	public static void sendSmsToMultiple(String[] to, String message, String url, String username, String password, String from) throws Exception {
		message = URLEncoder.encode(message, "UTF-8");
		if (to.length <= 25) {
			if (isValidPhoneNumber(to) && isValidMessage(message)) {
				String toMultiple = StringUtils.join(to, ",");

				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("Gw-Username", username));
				params.add(new BasicNameValuePair("Gw-Password", password));
				params.add(new BasicNameValuePair("Gw-from", from));
				params.add(new BasicNameValuePair("Gw-to", toMultiple));
				params.add(new BasicNameValuePair("Gw-Text", message));
				httpPost.setEntity(new UrlEncodedFormEntity(params));

				HttpResponse response = httpclient.execute(httpPost);
				StatusLine status = response.getStatusLine();
				String responseString = new BasicResponseHandler().handleResponse(response);
				if (status.getStatusCode() == 202 && responseString.indexOf("status=0") != -1) {
					LOG.info("message Sent" + responseString + " Status:" + status);
				} else {
					LOG.info("message send failed..." + responseString + " Status:" + status);
					throw new Exception("SMS could not be sent : " + responseString);
				}
				httpclient.getConnectionManager().shutdown();
			} else {
				// System.out.println("message not sent...");
			}
		} else {
			LOG.info("Should not be more than 25 Recipient!");
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
			LOG.info("Invalid Phone Number!! " + to);
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
				LOG.info("Phone number " + (toMultiple[i]) + " is not valid!");
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
			LOG.info("Message is valid.");
		} else {
			LOG.info("Message must not exceed 150 characters!!");
			isValid = false;
		}
		return isValid;
	}

	public static void main(String[] args) throws Exception {

		String phoneNo = "60126109500"; // nelson
		// "660122961926";
		// "0122961925";
		// "0122961925A";

		String[] phoneNoMultiple = { "60122961925", "60122961925", };
		// String[]phoneNoMultiple = {"60122961925","A0122961925","AA0122961925"};

		String msg = // "Hello 123!!";
				"Test message for IWK. Please inform Nitin on receipt";

		/*
		 * if (args.length != 2) { System.err.println("Usage: java iwk.sms.SMSHttp <60122822382> <text msg>");
		 * System.exit(-1); }
		 */

		// sendSms(phoneNo, msg);
		// send(phoneNoMultiple,msg);
	}
}
