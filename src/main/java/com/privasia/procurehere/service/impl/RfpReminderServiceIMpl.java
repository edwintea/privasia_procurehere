/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpReminderDao;
import com.privasia.procurehere.core.entity.RfpReminder;
import com.privasia.procurehere.service.RfpReminderService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RfpReminderServiceIMpl implements RfpReminderService {

	@Autowired
	RfpReminderDao rfpReminderDao;

	@Override
	@Transactional(readOnly = true)
	public List<RfpReminder> getEventRemindersForNotification() {
		return rfpReminderDao.getEventRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rfpReminderDao.updateImmediately(reminderId);
	}

}
