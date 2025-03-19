/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.privasia.procurehere.core.exceptions.RecaptchaServiceException;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RecaptchaService;

/**
 * @author Nitin Otageri
 */
@Service
public class RecaptchaServiceImpl implements RecaptchaService {

	private static final Logger LOG = LogManager.getLogger(RecaptchaServiceImpl.class);

//	@Autowired
//	private RestTemplate restTemplate;

	@Value("${recaptcha.url}")
	private String recaptchaUrl;

	@Value("${recaptcha.secret-key}")
	private String recaptchaSecretKey;

	private static class RecaptchaResponse {
		@JsonProperty("success")
		private boolean success;
		@JsonProperty("error-codes")
		private Collection<String> errorCodes;

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "RecaptchaResponse [success=" + success + ", errorCodes=" + errorCodes + "]";
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.privasia.procurehere.service.RecaptchaService#isResponseValid(java .lang.String, java.lang.String)
	 */
	@Override
	public boolean isResponseValid(String remoteIp, String response) throws RecaptchaServiceException {
		RecaptchaResponse recaptchaResponse;
		try {
			LOG.info("Invoking recaptcha API.... Remote IP: " + remoteIp + ", response : " + response + " secret : " + recaptchaSecretKey + " recaptchaUrl " + recaptchaUrl);
 			RestTemplate restTemplate = new RestTemplate();
			recaptchaResponse = restTemplate.postForEntity(StringUtils.checkString(recaptchaUrl), createBody(recaptchaSecretKey, remoteIp, response), RecaptchaResponse.class).getBody();
			LOG.info("Recaptcha Response : " + recaptchaResponse);
		} catch (RestClientException e) {
			LOG.error("Error recapter : " + e.getMessage());
			throw new RecaptchaServiceException("Recaptcha API not available due to exception", e);
		}
		return recaptchaResponse.success;
	}

	private MultiValueMap<String, String> createBody(String secret, String remoteIp, String response) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("secret", secret);
		form.add("remoteip", remoteIp);
		form.add("response", response);
		return form;
	}

	/**
	 * @return the recaptchaUrl
	 */
	public String getRecaptchaUrl() {
		return recaptchaUrl;
	}

	/**
	 * @param recaptchaUrl the recaptchaUrl to set
	 */
	public void setRecaptchaUrl(String recaptchaUrl) {
		this.recaptchaUrl = recaptchaUrl;
	}

	/**
	 * @return the recaptchaSecretKey
	 */
	public String getRecaptchaSecretKey() {
		return recaptchaSecretKey;
	}

	/**
	 * @param recaptchaSecretKey the recaptchaSecretKey to set
	 */
	public void setRecaptchaSecretKey(String recaptchaSecretKey) {
		this.recaptchaSecretKey = recaptchaSecretKey;
	}

	public static void main(String[] args) {
		RecaptchaResponse recaptchaResponse;
		try {
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
			form.add("secret", "6LezmB8TAAAAAPhbDlqFrCWIDC-mLdNfXJJLCYFo");
			form.add("remoteip", "127.0.0.1");
			form.add("response", "03AOPBWq9yNJO3ps4EL5kJAsHIf9bS196JTi4H4XJkMwlCzuG13_tG2tU8RUcsZadPl7FphG0anAdWJk6ErsWmNtuOYUnQBbDrd0GtvvnJy75NS-CxM1_eweNCvc8SuoVQ_-nYyLK_bFtVyURt7UzPfnwzcjwMzFfzjQsAnZKtoovwfUfMOkFM_Mgyq5wD9hf1SUIGNHrFbiI8lMhns3_u-Mc2gLKFGRcNXjKdc128qI6dT-QcD1SWJxODB4o-PDg4NbAqLKeJ1jCj4COc6tDVivpXBm_MTXtOsFn3ztl6ULyuluiNRqk_p78eqlHN61ramyrxsjWua_ScftUmdO3qtesKnOnWV_ZVtgkljCu3DjFEljWSQ_LOgApZPaY8Mkxodeo25HFKfYuwZFgFFoCBPyrJem6e5x-ncA");

			recaptchaResponse = restTemplate.postForEntity("https://www.google.com/recaptcha/api/siteverify", form, RecaptchaResponse.class).getBody();
			System.out.println("Recaptcha Response : " + recaptchaResponse);
		} catch (RestClientException e) {
			System.out.println("Error recapter : " + e.getMessage());
		}
	}
}
