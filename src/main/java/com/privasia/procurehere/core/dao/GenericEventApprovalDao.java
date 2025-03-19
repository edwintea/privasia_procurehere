package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.EventApproval;

/**
 * @author ravi
 */
public interface GenericEventApprovalDao<T extends EventApproval, PK extends Serializable> extends GenericDao<T, PK> {

	List<T> findApprovelLevelsForNotification();

}
