/**
 * 
 */
package com.privasia.procurehere.integration;

import com.privasia.procurehere.core.exceptions.ApplicationException;

/**
 * @author pooja
 */
public interface PublishEventService {

	/**
	 * @param eventId
	 * @param tenantId TODO
	 * @param isCreate TODO
	 * @throws ApplicationException
	 */
	void pushRfiEvent(String eventId, String tenantId, boolean isCreate) throws ApplicationException;

	/**
	 * @param eventId
	 * @param tenantId
	 * @param isCreate TODO
	 * @throws ApplicationException
	 */
	void pushRftEvent(String eventId, String tenantId, boolean isCreate) throws ApplicationException;

	/**
	 * @param eventId
	 * @param tenantId
	 * @param isCreate TODO
	 * @throws ApplicationException
	 */
	void pushRfqEvent(String eventId, String tenantId, boolean isCreate) throws ApplicationException;

}
