package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;

/**
 * @author parveen
 */
public interface EnvelopServiceBase {
	/**
	 * @param envelop
	 * @param eventType
	 * @param eventId
	 * @param loggedInUser TODO
	 */
	void sendEnvelopOpenNotification(Envelop envelop, RfxTypes eventType, String eventId, User loggedInUser, Boolean isAllOpen);

	/**
	 * 
	 * @param envelop
	 * @param eventType
	 * @param eventId
	 * @param loggedInUser
	 * @param isAllClosed
	 */
	void sendEnvelopCloseNotification(Envelop envelop, RfxTypes eventType, String eventId, User loggedInUser, Boolean isAllClosed);

}
