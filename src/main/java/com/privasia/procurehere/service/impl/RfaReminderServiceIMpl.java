/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaReminderDao;
import com.privasia.procurehere.core.entity.RfaReminder;
import com.privasia.procurehere.service.RfaReminderService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RfaReminderServiceIMpl implements RfaReminderService {

	@Autowired
	RfaReminderDao rfaReminderDao;

	@Override
	@Transactional(readOnly = true)
	public List<RfaReminder> getEventRemindersForNotification() {
		return rfaReminderDao.getEventRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rfaReminderDao.updateImmediately(reminderId);
	}

}
