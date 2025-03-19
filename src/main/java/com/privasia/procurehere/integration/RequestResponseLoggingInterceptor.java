/**
 * 
 */
package com.privasia.procurehere.integration;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import com.privasia.procurehere.core.utils.Global;

/**
 * @author pooja
 */
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger LOG = LogManager.getLogger(Global.API_LOG);

	/*
	 * (non-Javadoc)
	 * @see org.springframework.http.client.ClientHttpRequestInterceptor#intercept(org.springframework.http.HttpRequest,
	 * byte[], org.springframework.http.client.ClientHttpRequestExecution)
	 */
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);

		// Add optional additional headers
		response.getHeaders().add("headerName", "VALUE");

		return response;
	}

	private void logRequest(HttpRequest request, byte[] body) throws IOException {
		LOG.info("===========================request begin================================================");
		LOG.info("URI         : " + request.getURI());
		LOG.info("Method      : " + request.getMethod());
		LOG.info("Headers     : " + request.getHeaders());
		LOG.info("Request body: " + new String(body, "UTF-8"));
		LOG.info("==========================request end================================================");
	}

	private void logResponse(ClientHttpResponse response) throws IOException {
		LOG.info("============================response begin==========================================");
		LOG.info("Status code  : " + response.getStatusCode());
		LOG.info("Status text  : " + response.getStatusText());
		LOG.info("Headers      : " + response.getHeaders());
		LOG.info("Response body: " + StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
		LOG.info("=======================response end=================================================");
	}

}
