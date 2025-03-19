package com.privasia.procurehere.service.impl;

import com.privasia.procurehere.core.dao.MaintenanceNotificationDao;
import com.privasia.procurehere.core.entity.MaintenanceNotification;
import com.privasia.procurehere.service.MaintenanceNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MaintenanceNotificationServiceImpl implements MaintenanceNotificationService {

    @Autowired
    MaintenanceNotificationDao maintenanceNotificationDao;

    @Override
    public MaintenanceNotification getActiveMaintenanceNotification() {
        return maintenanceNotificationDao.getActiveMaintenanceNotification();
    }
}