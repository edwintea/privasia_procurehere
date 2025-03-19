package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author parveen
 */
public class MobileBuyerMetrics implements Serializable {

	private static final long serialVersionUID = -7979344997449624911L;

	long localSupplierCount;

	long registeredUserCount;

	long activeUserCount;

	List<SupplierCountPojo> currentSupplierCountList;

	long aggregateTotalPrCount;

	long aggregatePendingPrCount;

	long aggregatePOCount;

	BigDecimal aggregateTotalPrValue;

	BigDecimal aggregatePendingPrValue;

	BigDecimal aggregatePOValue;

	long aggregateTotalEventCount;

	long aggregateActiveEventCount;

	long aggregateClosedCompletedEventCount;

	BigDecimal aggregateTotalEventValue;

	BigDecimal aggregateActiveEventValue;

	BigDecimal aggregateClosedCompletedEventValue;

	BigDecimal aggregateAwardedPriceValue;
	

	/**
	 * @return the localSupplierCount
	 */
	public long getLocalSupplierCount() {
		return localSupplierCount;
	}

	/**
	 * @param localSupplierCount the localSupplierCount to set
	 */
	public void setLocalSupplierCount(long localSupplierCount) {
		this.localSupplierCount = localSupplierCount;
	}

	/**
	 * @return the registeredUserCount
	 */
	public long getRegisteredUserCount() {
		return registeredUserCount;
	}

	/**
	 * @param registeredUserCount the registeredUserCount to set
	 */
	public void setRegisteredUserCount(long registeredUserCount) {
		this.registeredUserCount = registeredUserCount;
	}

	/**
	 * @return the activeUserCount
	 */
	public long getActiveUserCount() {
		return activeUserCount;
	}

	/**
	 * @param activeUserCount the activeUserCount to set
	 */
	public void setActiveUserCount(long activeUserCount) {
		this.activeUserCount = activeUserCount;
	}

	/**
	 * @return the currentSupplierCountList
	 */
	public List<SupplierCountPojo> getCurrentSupplierCountList() {
		return currentSupplierCountList;
	}

	/**
	 * @param currentSupplierCountList the currentSupplierCountList to set
	 */
	public void setCurrentSupplierCountList(List<SupplierCountPojo> currentSupplierCountList) {
		this.currentSupplierCountList = currentSupplierCountList;
	}

	/**
	 * @return the aggregateTotalPrCount
	 */
	public long getAggregateTotalPrCount() {
		return aggregateTotalPrCount;
	}

	/**
	 * @param aggregateTotalPrCount the aggregateTotalPrCount to set
	 */
	public void setAggregateTotalPrCount(long aggregateTotalPrCount) {
		this.aggregateTotalPrCount = aggregateTotalPrCount;
	}

	/**
	 * @return the aggregatePendingPrCount
	 */
	public long getAggregatePendingPrCount() {
		return aggregatePendingPrCount;
	}

	/**
	 * @param aggregatePendingPrCount the aggregatePendingPrCount to set
	 */
	public void setAggregatePendingPrCount(long aggregatePendingPrCount) {
		this.aggregatePendingPrCount = aggregatePendingPrCount;
	}

	/**
	 * @return the aggregatePOCount
	 */
	public long getAggregatePOCount() {
		return aggregatePOCount;
	}

	/**
	 * @param aggregatePOCount the aggregatePOCount to set
	 */
	public void setAggregatePOCount(long aggregatePOCount) {
		this.aggregatePOCount = aggregatePOCount;
	}

	/**
	 * @return the aggregateTotalPrValue
	 */
	public BigDecimal getAggregateTotalPrValue() {
		return aggregateTotalPrValue;
	}

	/**
	 * @param aggregateTotalPrValue the aggregateTotalPrValue to set
	 */
	public void setAggregateTotalPrValue(BigDecimal aggregateTotalPrValue) {
		this.aggregateTotalPrValue = aggregateTotalPrValue;
	}

	/**
	 * @return the aggregatePendingPrValue
	 */
	public BigDecimal getAggregatePendingPrValue() {
		return aggregatePendingPrValue;
	}

	/**
	 * @param aggregatePendingPrValue the aggregatePendingPrValue to set
	 */
	public void setAggregatePendingPrValue(BigDecimal aggregatePendingPrValue) {
		this.aggregatePendingPrValue = aggregatePendingPrValue;
	}

	/**
	 * @return the aggregatePOValue
	 */
	public BigDecimal getAggregatePOValue() {
		return aggregatePOValue;
	}

	/**
	 * @param aggregatePOValue the aggregatePOValue to set
	 */
	public void setAggregatePOValue(BigDecimal aggregatePOValue) {
		this.aggregatePOValue = aggregatePOValue;
	}

	/**
	 * @return the aggregateTotalEventCount
	 */
	public long getAggregateTotalEventCount() {
		return aggregateTotalEventCount;
	}

	/**
	 * @param aggregateTotalEventCount the aggregateTotalEventCount to set
	 */
	public void setAggregateTotalEventCount(long aggregateTotalEventCount) {
		this.aggregateTotalEventCount = aggregateTotalEventCount;
	}

	/**
	 * @return the aggregateActiveEventCount
	 */
	public long getAggregateActiveEventCount() {
		return aggregateActiveEventCount;
	}

	/**
	 * @param aggregateActiveEventCount the aggregateActiveEventCount to set
	 */
	public void setAggregateActiveEventCount(long aggregateActiveEventCount) {
		this.aggregateActiveEventCount = aggregateActiveEventCount;
	}

	/**
	 * @return the aggregateClosedCompletedEventCount
	 */
	public long getAggregateClosedCompletedEventCount() {
		return aggregateClosedCompletedEventCount;
	}

	/**
	 * @param aggregateClosedCompletedEventCount the aggregateClosedCompletedEventCount to set
	 */
	public void setAggregateClosedCompletedEventCount(long aggregateClosedCompletedEventCount) {
		this.aggregateClosedCompletedEventCount = aggregateClosedCompletedEventCount;
	}

	/**
	 * @return the aggregateTotalEventValue
	 */
	public BigDecimal getAggregateTotalEventValue() {
		return aggregateTotalEventValue;
	}

	/**
	 * @param aggregateTotalEventValue the aggregateTotalEventValue to set
	 */
	public void setAggregateTotalEventValue(BigDecimal aggregateTotalEventValue) {
		this.aggregateTotalEventValue = aggregateTotalEventValue;
	}

	/**
	 * @return the aggregateActiveEventValue
	 */
	public BigDecimal getAggregateActiveEventValue() {
		return aggregateActiveEventValue;
	}

	/**
	 * @param aggregateActiveEventValue the aggregateActiveEventValue to set
	 */
	public void setAggregateActiveEventValue(BigDecimal aggregateActiveEventValue) {
		this.aggregateActiveEventValue = aggregateActiveEventValue;
	}

	/**
	 * @return the aggregateClosedCompletedEventValue
	 */
	public BigDecimal getAggregateClosedCompletedEventValue() {
		return aggregateClosedCompletedEventValue;
	}

	/**
	 * @param aggregateClosedCompletedEventValue the aggregateClosedCompletedEventValue to set
	 */
	public void setAggregateClosedCompletedEventValue(BigDecimal aggregateClosedCompletedEventValue) {
		this.aggregateClosedCompletedEventValue = aggregateClosedCompletedEventValue;
	}

	/**
	 * @return the aggregateAwardedPriceValue
	 */
	public BigDecimal getAggregateAwardedPriceValue() {
		return aggregateAwardedPriceValue;
	}

	/**
	 * @param aggregateAwardedPriceValue the aggregateAwardedPriceValue to set
	 */
	public void setAggregateAwardedPriceValue(BigDecimal aggregateAwardedPriceValue) {
		this.aggregateAwardedPriceValue = aggregateAwardedPriceValue;
	}

}