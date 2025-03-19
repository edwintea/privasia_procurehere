package com.privasia.procurehere.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.DeliveryOrder;
import com.privasia.procurehere.core.entity.DeliveryOrderItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.DoStatus;
import com.privasia.procurehere.core.exceptions.EmailException;

/**
 * @author ravi
 */
public interface DeliveryOrderItemService {

	/**
	 * @param deliveryOrderItem
	 * @return
	 */
	DeliveryOrderItem updateItem(DeliveryOrderItem deliveryOrderItem);

	/**
	 * @param doItemId
	 * @return
	 */
	DeliveryOrderItem findById(String doItemId);

	/**
	 * @param items
	 * @param additionalTax
	 * @param footer
	 * @param status
	 * @param loggedInUser
	 * @param referenceNumber
	 * @param deliveryAddress
	 * @param deliveryReceiver
	 * @param deliveryDate
	 * @param deliveryTime
	 * @param attentionTo
	 * @param correspondenceAddress
	 * @param doName
	 * @param session
	 * @return
	 * @throws EmailException
	 */
	DeliveryOrder updateItems(List<DeliveryOrderItem> items, BigDecimal additionalTax, String footer, DoStatus status, User loggedInUser, String referenceNumber, String deliveryAddress, String deliveryReceiver, String deliveryDate, String deliveryTime, String attentionTo, String correspondenceAddress, String doName, HttpSession session, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, String trackingNumber, String courierName) throws EmailException;

	/**
	 * @param itemId
	 * @return
	 */
	public DeliveryOrderItem getDoItemById(String itemId);
}