package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Vipul
 */
public class OwnerDashboardPojo implements Serializable {

 
	private static final long serialVersionUID = 2800147700983635536L;

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	private int activeBuyers = 0;
	private int pendingBuyers = 0;
	private int suspendedBuyers = 0;

	private int activeSuppliers = 0;
	private int pendingSuppliers = 0;
	private int rejectedSuppliers = 0;
	private int suspendedSuppliers = 0;

	private Country country;
	
	public OwnerDashboardPojo() {
	}

	public OwnerDashboardPojo(List<Object[]> returnList) {
		if (returnList != null) {
			for (Object[] arr : returnList) {
				if (arr != null && arr.length > 1) {
					if (arr[0] instanceof SupplierStatus) {
						LOG.info("SupplierStatus OwnerDashboardPojo " + returnList.size());
						switch ((SupplierStatus) arr[0]) {
						case ALL:
							break;
						case APPROVED:
							this.activeSuppliers = ((Number) arr[1]).intValue();
							break;
						case PENDING:
							this.pendingSuppliers = ((Number) arr[1]).intValue();
							break;
						case REJECTED:
							this.rejectedSuppliers = ((Number) arr[1]).intValue();
							break;
						case SUSPENDED:
							this.suspendedSuppliers = ((Number) arr[1]).intValue();
							break;
						default:
							break;

						}
					}
					if (arr[0] instanceof BuyerStatus) {
						switch ((BuyerStatus) arr[0]) {
						case ACTIVE:
							this.activeBuyers = ((Number) arr[1]).intValue();
							break;
						case ALL:
							break;
						case PENDING:
							this.pendingBuyers = ((Number) arr[1]).intValue();
							break;
						case SUSPENDED:
							this.suspendedBuyers = ((Number) arr[1]).intValue();
							break;
						case UNPAID:
							break;
						default:
							break;
						}
					}
				}
			}
		}
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
	 * @return the pendingBuyers
	 */
	public int getPendingBuyers() {
		return pendingBuyers;
	}

	/**
	 * @param pendingBuyers the pendingBuyers to set
	 */
	public void setPendingBuyers(int pendingBuyers) {
		this.pendingBuyers = pendingBuyers;
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
	 * @return the activeSuppliers
	 */
	public int getActiveSuppliers() {
		return activeSuppliers;
	}

	/**
	 * @param activeSuppliers the activeSuppliers to set
	 */
	public void setActiveSuppliers(int activeSuppliers) {
		this.activeSuppliers = activeSuppliers;
	}

	/**
	 * @return the pendingSuppliers
	 */
	public int getPendingSuppliers() {
		return pendingSuppliers;
	}

	/**
	 * @param pendingSuppliers the pendingSuppliers to set
	 */
	public void setPendingSuppliers(int pendingSuppliers) {
		this.pendingSuppliers = pendingSuppliers;
	}

	/**
	 * @return the rejectedSuppliers
	 */
	public int getRejectedSuppliers() {
		return rejectedSuppliers;
	}

	/**
	 * @param rejectedSuppliers the rejectedSuppliers to set
	 */
	public void setRejectedSuppliers(int rejectedSuppliers) {
		this.rejectedSuppliers = rejectedSuppliers;
	}

	/**
	 * @return the suspendedSuppliers
	 */
	public int getSuspendedSuppliers() {
		return suspendedSuppliers;
	}

	/**
	 * @param suspendedSuppliers the suspendedSuppliers to set
	 */
	public void setSuspendedSuppliers(int suspendedSuppliers) {
		this.suspendedSuppliers = suspendedSuppliers;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	public String toLogString() {
		return "OwnerDashboardPojo [activeBuyers=" + activeBuyers + ", pendingBuyers=" + pendingBuyers + ", suspendedBuyers=" + suspendedBuyers + ", activeSuppliers=" + activeSuppliers + ", pendingSuppliers=" + pendingSuppliers + ", rejectedSuppliers=" + rejectedSuppliers + ", suspendedSuppliers=" + suspendedSuppliers + "]";
	}

}
