/**
 * 
 */
package com.privasia.procurehere.integration;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * @author pooja
 */
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

	/*
	 * (non-Javadoc)
	 * @see
	 * org.springframework.web.client.ResponseErrorHandler#hasError(org.springframework.http.client.ClientHttpResponse)
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return (response.getStatusCode().series() == Series.CLIENT_ERROR || response.getStatusCode().series() == Series.SERVER_ERROR);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.springframework.http.client.
	 * ClientHttpResponse )
	 */
	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {
		if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
			// handle SERVER_ERROR

			String errorMessage = StreamUtils.copyToString(httpResponse.getBody(), Charset.defaultCharset());
			throw new IOException("API invocation error : " + errorMessage);

		} else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
			// handle CLIENT_ERROR
			if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new IOException("Not found");
			}
		}

	}

}
