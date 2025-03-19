package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.privasia.procurehere.core.utils.Global;

/**
 * @author Vipul
 */
public class OwnerDashboardMetricPojo implements Serializable{

	private static final long serialVersionUID = -4259420261285837652L;

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	private int trailInProgress = 0;
	private int conversionRate = 0;
	private int suspendedBuyers = 0;
	private int activeBuyers = 0;
	private int newBuyer = 0;
	private int totalBuyer = 0;
	private int failedPaymentTransaction = 0;
	private int revenueGenerated = 0;
	private int autoExtention = 0;
	private int manualExtention = 0;
	private int CanceledEvents = 0;
	private int totalEvents = 0;
	private double averageTimePerWeek = 0;
	private double eventPerCategory = 0;
	private int totalSuppliers = 0;
	private int totalPr = 0;
	private int totalPo = 0;
	private double totalSavings = 0;
	private double averageSavings = 0;

	public OwnerDashboardMetricPojo() {
	}

	/**
	 * @return the trailInProgress
	 */
	public int getTrailInProgress() {
		return trailInProgress;
	}

	/**
	 * @param trailInProgress the trailInProgress to set
	 */
	public void setTrailInProgress(int trailInProgress) {
		this.trailInProgress = trailInProgress;
	}

	/**
	 * @return the conversionRate
	 */
	public int getConversionRate() {
		return conversionRate;
	}

	/**
	 * @param conversionRate the conversionRate to set
	 */
	public void setConversionRate(int conversionRate) {
		this.conversionRate = conversionRate;
	}

	/**
	 * @return the suspendedBuyers
	 */
	public int getSuspendedBuyers() {
		return suspendedBuyers;
	}

	/**
	 * @param suspendedBuyers the suspendedBuyers to set
	 */
	public void setSuspendedBuyers(int suspendedBuyers) {
		this.suspendedBuyers = suspendedBuyers;
	}

	/**
	 * @return the activeBuyers
	 */
	public int getActiveBuyers() {
		return activeBuyers;
	}

	/**
	 * @param activeBuyers the activeBuyers to set
	 */
	public void setActiveBuyers(int activeBuyers) {
		this.activeBuyers = activeBuyers;
	}

	/**
	 * @return the newBuyer
	 */
	public int getNewBuyer() {
		return newBuyer;
	}

	/**
	 * @param newBuyer the newBuyer to set
	 */
	public void setNewBuyer(int newBuyer) {
		this.newBuyer = newBuyer;
	}

	/**
	 * @return the totalBuyer
	 */
	public int getTotalBuyer() {
		return totalBuyer;
	}

	/**
	 * @param totalBuyer the totalBuyer to set
	 */
	public void setTotalBuyer(int totalBuyer) {
		this.totalBuyer = totalBuyer;
	}

	/**
	 * @return the failedPaymentTransaction
	 */
	public int getFailedPaymentTransaction() {
		return failedPaymentTransaction;
	}

	/**
	 * @param failedPaymentTransaction the failedPaymentTransaction to set
	 */
	public void setFailedPaymentTransaction(int failedPaymentTransaction) {
		this.failedPaymentTransaction = failedPaymentTransaction;
	}

	/**
	 * @return the revenueGenerated
	 */
	public int getRevenueGenerated() {
		return revenueGenerated;
	}

	/**
	 * @param revenueGenerated the revenueGenerated to set
	 */
	public void setRevenueGenerated(int revenueGenerated) {
		this.revenueGenerated = revenueGenerated;
	}

	/**
	 * @return the autoExtention
	 */
	public int getAutoExtention() {
		return autoExtention;
	}

	/**
	 * @param autoExtention the autoExtention to set
	 */
	public void setAutoExtention(int autoExtention) {
		this.autoExtention = autoExtention;
	}

	/**
	 * @return the manualExtention
	 */
	public int getManualExtention() {
		return manualExtention;
	}

	/**
	 * @param manualExtention the manualExtention to set
	 */
	public void setManualExtention(int manualExtention) {
		this.manualExtention = manualExtention;
	}

	/**
	 * @return the canceledEvents
	 */
	public int getCanceledEvents() {
		return CanceledEvents;
	}

	/**
	 * @param canceledEvents the canceledEvents to set
	 */
	public void setCanceledEvents(int canceledEvents) {
		CanceledEvents = canceledEvents;
	}

	/**
	 * @return the totalEvents
	 */
	public int getTotalEvents() {
		return totalEvents;
	}

	/**
	 * @param totalEvents the totalEvents to set
	 */
	public void setTotalEvents(int totalEvents) {
		this.totalEvents = totalEvents;
	}

	/**
	 * @return the averageTimePerWeek
	 */
	public double getAverageTimePerWeek() {
		return averageTimePerWeek;
	}

	/**
	 * @param averageTimePerWeek the averageTimePerWeek to set
	 */
	public void setAverageTimePerWeek(double averageTimePerWeek) {
		this.averageTimePerWeek = averageTimePerWeek;
	}

	/**
	 * @return the eventPerCategory
	 */
	public double getEventPerCategory() {
		return eventPerCategory;
	}

	/**
	 * @param eventPerCategory the eventPerCategory to set
	 */
	public void setEventPerCategory(double eventPerCategory) {
		this.eventPerCategory = eventPerCategory;
	}

	/**
	 * @return the totalSuppliers
	 */
	public int getTotalSuppliers() {
		return totalSuppliers;
	}

	/**
	 * @param totalSuppliers the totalSuppliers to set
	 */
	public void setTotalSuppliers(int totalSuppliers) {
		this.totalSuppliers = totalSuppliers;
	}

	/**
	 * @return the totalPr
	 */
	public int getTotalPr() {
		return totalPr;
	}

	/**
	 * @param totalPr the totalPr to set
	 */
	public void setTotalPr(int totalPr) {
		this.totalPr = totalPr;
	}

	/**
	 * @return the totalPo
	 */
	public int getTotalPo() {
		return totalPo;
	}

	/**
	 * @param totalPo the totalPo to set
	 */
	public void setTotalPo(int totalPo) {
		this.totalPo = totalPo;
	}

	/**
	 * @return the totalSavings
	 */
	public double getTotalSavings() {
		return totalSavings;
	}

	/**
	 * @param totalSavings the totalSavings to set
	 */
	public void setTotalSavings(double totalSavings) {
		this.totalSavings = totalSavings;
	}

	/**
	 * @return the averageSavings
	 */
	public double getAverageSavings() {
		return averageSavings;
	}

	/**
	 * @param averageSavings the averageSavings to set
	 */
	public void setAverageSavings(double averageSavings) {
		this.averageSavings = averageSavings;
	}

	public String toLogString() {
		return "OwnerDashboardMetricPojo [trailInProgress=" + trailInProgress + ", conversionRate=" + conversionRate + ", suspendedBuyers=" + suspendedBuyers + ", activeBuyers=" + activeBuyers + ", newBuyer=" + newBuyer + ", totalBuyer=" + totalBuyer + ", failedPaymentTransaction=" + failedPaymentTransaction + ", revenueGenerated=" + revenueGenerated + ", autoExtention=" + autoExtention + ", manualExtention=" + manualExtention + ", CanceledEvents=" + CanceledEvents + ", totalEvents=" + totalEvents + ", averageTimePerWeek=" + averageTimePerWeek + ", eventPerCategory=" + eventPerCategory + ", totalSuppliers=" + totalSuppliers + ", totalPr=" + totalPr + ", totalPo=" + totalPo + ", totalSavings=" + totalSavings + ", averageSavings=" + averageSavings + "]";
	}

}
