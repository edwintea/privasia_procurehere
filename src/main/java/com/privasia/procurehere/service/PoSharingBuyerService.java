package com.privasia.procurehere.service;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;

public interface PoSharingBuyerService {

	void clearBuyerSetting(String id);

	void shareAllPotoFinaceCompany(Supplier supplier, FinanceCompany financeCompany, Buyer buyer, User loggedInUser);

}
