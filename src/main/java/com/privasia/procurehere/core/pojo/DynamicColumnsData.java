/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nitin Otageri
 */
public class DynamicColumnsData implements Serializable {

	private static final long serialVersionUID = 119505704561552646L;

	private String tableId;
	private String id;

	private List<List<String>> columns = new ArrayList<List<String>>();
	private List<List<String>> data = new ArrayList<List<String>>();
	
	/**
	 * "columnDefs": [
            {
                // The `data` parameter refers to the data for the cell (defined by the
                // `data` option, which defaults to the column being worked with, in
                // this case `data: 0`.
                "render": function ( data, type, row ) {
                    return data +' ('+ row[3]+')';
                },
                "targets": 0
            },
            { "visible": false,  "targets": [ 3 ] }
        ]
	 */
	
	//private List<ColumnDef> columnDefs = new ArrayList<ColumnDef>();

	/**
	 * @return the columns
	 */
	public List<List<String>> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<List<String>> columns) {
		this.columns = columns;
	}

	/**
	 * @return the data
	 */
	public List<List<String>> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<List<String>> data) {
		this.data = data;
	}

	/**
	 * @return the tableId
	 */
	public String getTableId() {
		return tableId;
	}

	/**
	 * @param tableId the tableId to set
	 */
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
