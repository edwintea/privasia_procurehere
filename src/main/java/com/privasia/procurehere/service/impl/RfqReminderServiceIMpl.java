/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqReminderDao;
import com.privasia.procurehere.core.entity.RfqReminder;
import com.privasia.procurehere.service.RfqReminderService;

/**
 * @author ravi
 */
@Service
@Transactional(readOnly = true)
public class RfqReminderServiceIMpl implements RfqReminderService {

	@Autowired
	RfqReminderDao rfqReminderDao;

	@Override
	@Transactional(readOnly = true)
	public List<RfqReminder> getEventRemindersForNotification() {
		return rfqReminderDao.getEventRemindersForNotification();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateImmediately(String reminderId) {
		rfqReminderDao.updateImmediately(reminderId);
	}

}
