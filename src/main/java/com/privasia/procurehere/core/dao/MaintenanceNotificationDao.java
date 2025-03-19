package com.privasia.procurehere.core.dao;

import com.privasia.procurehere.core.entity.MaintenanceNotification;

public interface MaintenanceNotificationDao extends GenericDao<MaintenanceNotification, String> {

    MaintenanceNotification getActiveMaintenanceNotification();
}
