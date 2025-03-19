/**
 * 
 */
package com.privasia.procurehere.core.enums;

/**
 * @author Teja
 */
public enum AuditActionType {
	General("General"), Create("Create"), Update("Update"), Cancel("Cancel"), Suspend("Suspend"), Resume("Resume"), Approve("Approve"), Close("Close"),
	Active("Active"), //
	Finish("Finish"), Complete("Complete"), Submitted("Submitted"), Accepted("Accepted"), Reject("Reject"), Extension("Extension"), Transfer("Transfer"),
	Start("Start"), //
	Disqualified("Disqualified"), Open("Open"), View("View"), Award("Award"), Conclude("Conclude"), Download("Download"), Requalified("Requalified"),
	Previewed("Previewed"), //
	Remark("Remark"), Review("Review"), Evaluate("Evaluate"), Reminder("Reminder"), Paid("Paid"), SuspendApprove("Suspend Approve"),
	SuspendReject("Suspend Reject"), //
	AwardApprove("Award Approve"), AwardReject("Award Reject"), ConcludeAward("Conclude Award"), Discard("Discard"), Approved("Approved"), Rejected("Rejected"),
	Terminated("Terminated"), //
	Erp("ERP"), Error("Error");

	private AuditActionType(String value) {
		this.value = value;
	}

	private String value;

	@Override
	public String toString() {
		return value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
