/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author arc
 */
public class TableData<T> implements Serializable {

	private static final long serialVersionUID = 4187181085150397267L;

	/**
	 * The draw counter that this object is a response to - from the draw parameter sent as part of the data request.
	 * Note that it is strongly recommended for security reasons that you cast this parameter to an integer, rather than
	 * simply echoing back to the client what it sent in the draw parameter, in order to prevent Cross Site Scripting
	 * (XSS) attacks.
	 */
	private int draw;

	/**
	 * The data to be displayed in the table. This is an array of data source objects, one for each row, which will be
	 * used by DataTables. Note that this parameter's name can be changed using the ajaxDT option's dataSrc property.
	 */
	private List<T> data;

	/**
	 * Total records, before filtering (i.e. the total number of records in the database)
	 */
	private long recordsTotal;

	/**
	 * Total records, after filtering (i.e. the total number of records after filtering has been applied - not just the
	 * number of records being returned for this page of data).
	 */
	private long recordsFiltered;

	/**
	 * Optional: If an error occurs during the running of the server-side processing script, you can inform the user of
	 * this error by passing back the error message to be displayed using this parameter. Do not include if there is no
	 * error.
	 */
	private String error;

	/**
	 * Additional data to be sent for custom code
	 */
	private EventStatus status;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date startDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date endDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date resumeDate;

	public TableData(List<T> data) {
		this.data = data;
		if (this.data == null) {
			this.data = new ArrayList<T>();
		}
		this.recordsTotal = this.data.size();
		this.recordsFiltered = this.data.size();
	}

	public TableData(List<T> data, long totalRecords) {
		this.data = data;
		this.recordsTotal = totalRecords;
		this.recordsFiltered = totalRecords;
		if (this.data == null) {
			this.data = new ArrayList<T>();
		}
	}

	public TableData(List<T> data, long totalRecords, long totalFiltered) {
		this.data = data;
		this.recordsTotal = totalRecords;
		this.recordsFiltered = totalFiltered;
		if (this.data == null) {
			this.data = new ArrayList<T>();
		}
	}

	/**
	 * @return the data
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<T> data) {
		this.data = data;
	}

	/**
	 * @return the recordsTotal
	 */
	public long getRecordsTotal() {
		return recordsTotal;
	}

	/**
	 * @param recordsTotal the recordsTotal to set
	 */
	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	/**
	 * @return the recordsFiltered
	 */
	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	/**
	 * @param recordsFiltered the recordsFiltered to set
	 */
	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the draw
	 */
	public int getDraw() {
		return draw;
	}

	/**
	 * @param draw the draw to set
	 */
	public void setDraw(int draw) {
		this.draw = draw;
	}

	/**
	 * @return the status
	 */
	public EventStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(EventStatus status) {
		this.status = status;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the resumeDate
	 */
	public Date getResumeDate() {
		return resumeDate;
	}

	/**
	 * @param resumeDate the resumeDate to set
	 */
	public void setResumeDate(Date resumeDate) {
		this.resumeDate = resumeDate;
	}

	@Override
	public String toString() {
		return "TablesData [draw=" + draw + ", recordsTotal=" + recordsTotal + ", recordsFiltered=" + recordsFiltered + ", error=" + error + "]";
	}
}
