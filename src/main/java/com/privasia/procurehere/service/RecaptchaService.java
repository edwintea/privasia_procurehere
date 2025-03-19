/**
 * 
 */
package com.privasia.procurehere.service;

import com.privasia.procurehere.core.exceptions.RecaptchaServiceException;

/**
 * @author Nitin Otageri
 *
 */
public interface RecaptchaService {

	 boolean isResponseValid(String remoteIp, String response) throws RecaptchaServiceException;
	
}
