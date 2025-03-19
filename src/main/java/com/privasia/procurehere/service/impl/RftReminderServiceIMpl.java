/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftReminderDao;
import com.privasia.procurehere.core.entity.RftReminder;
import com.privasia.procurehere.service.RftReminderService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RftReminderServiceIMpl implements RftReminderService {

	@Autowired
	RftReminderDao rftReminderDao;

	@Override
	@Transactional(readOnly = true)
	public List<RftReminder> getEventRemindersForNotification() {
		return rftReminderDao.getEventRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rftReminderDao.updateImmediately(reminderId);
	}

}
