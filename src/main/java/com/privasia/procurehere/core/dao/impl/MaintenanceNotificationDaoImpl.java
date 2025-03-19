package com.privasia.procurehere.core.dao.impl;

import com.privasia.procurehere.core.dao.GenericDao;

import com.privasia.procurehere.core.dao.MaintenanceNotificationDao;
import com.privasia.procurehere.core.entity.InvoiceReport;
import com.privasia.procurehere.core.entity.MaintenanceNotification;
import com.privasia.procurehere.core.utils.CollectionUtil;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class MaintenanceNotificationDaoImpl extends GenericDaoImpl<MaintenanceNotification, String> implements MaintenanceNotificationDao {


    @Override
    public MaintenanceNotification getActiveMaintenanceNotification() {
        try {
            final Query query = getEntityManager().createQuery("from MaintenanceNotification m where m.isEnable = :isEnable");
            query.setParameter("isEnable", Boolean.TRUE);
            List<MaintenanceNotification> uList = query.getResultList();
            if (CollectionUtil.isNotEmpty(uList)) {
                return uList.get(0);
            } else {

                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}