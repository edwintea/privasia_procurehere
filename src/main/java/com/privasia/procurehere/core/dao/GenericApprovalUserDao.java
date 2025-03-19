package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.ApprovalUser;

/**
 * @author ravi
 */
public interface GenericApprovalUserDao<T extends ApprovalUser, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @return
	 */
	List<T> findApprovelUserForNotification();

}
