package com.privasia.procurehere.service;

import javax.servlet.http.HttpSession;

public interface TransferOwnershipService {

	void saveTransferOwnership(String fromUser,String toUser, HttpSession session, String loggedInUser);

}
