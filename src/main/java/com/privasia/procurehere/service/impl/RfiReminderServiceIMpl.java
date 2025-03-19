/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfiReminderDao;
import com.privasia.procurehere.core.entity.RfiReminder;
import com.privasia.procurehere.service.RfiReminderService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RfiReminderServiceIMpl implements RfiReminderService {

	@Autowired
	RfiReminderDao rfiReminderDao;

	@Override
	@Transactional(readOnly = true)
	public List<RfiReminder> getEventRemindersForNotification() {
		return rfiReminderDao.getEventRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rfiReminderDao.updateImmediately(reminderId);
	}

}
