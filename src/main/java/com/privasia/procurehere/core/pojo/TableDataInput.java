/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author arc
 */
public class TableDataInput implements Serializable {

	private static final long serialVersionUID = 4689567215929320520L;

	/**
	 * Draw counter. This is used by DataTables to ensure that the Ajax returns from server-side processing requests are
	 * drawn in sequence by DataTables (Ajax requests are asynchronous and thus can return out of sequence). This is
	 * used as part of the draw return parameter (see below).
	 */
	private Integer draw;

	/**
	 * Paging first record indicator. This is the start point in the current data set (0 index based - i.e. 0 is the
	 * first record).
	 */
	private Integer start;

	/**
	 * Number of records that the table can display in the current draw. It is expected that the number of records
	 * returned will be equal to this number, unless the server has fewer records to return. Note that this can be -1 to
	 * indicate that all records should be returned (although that negates any benefits of server-side processing!)
	 */
	private Integer length;

	/**
	 * Global search parameter.
	 */
	private SearchParameter search;

	/**
	 * Order parameter
	 */
	private List<OrderParameter> order;

	/**
	 * Per-column search parameter
	 */
	private List<ColumnParameter> columns;

	private String sort;

	private Integer size;

	public TableDataInput() {
		this.draw = 1;
		this.start = 0;
		this.size = 0;
		this.length = 10;
		this.search = new SearchParameter();
		this.order = new ArrayList<OrderParameter>();
		this.columns = new ArrayList<ColumnParameter>();
	}
	
	public TableDataInput(int start, int length) {
		this.draw = 1;
		this.start = start;
		this.size = 0;
		this.length = length;
		this.search = new SearchParameter();
		this.order = new ArrayList<OrderParameter>();
		this.columns = new ArrayList<ColumnParameter>();
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public SearchParameter getSearch() {
		return search;
	}

	public void setSearch(SearchParameter search) {
		this.search = search;
	}

	public List<OrderParameter> getOrder() {
		return order;
	}

	public void setOrder(List<OrderParameter> order) {
		this.order = order;
	}

	public List<ColumnParameter> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnParameter> columns) {
		this.columns = columns;
	}

	/**
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * @param sort the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * @return a {@link Map} of {@link ColumnParameter} indexed by name
	 */
	public Map<String, ColumnParameter> getColumnsAsMap() {
		Map<String, ColumnParameter> map = new HashMap<String, ColumnParameter>();
		for (ColumnParameter column : columns) {
			map.put(column.getData(), column);
		}
		return map;
	}

	/**
	 * Find a column by its name
	 *
	 * @param columnName the name of the column
	 * @return the given Column, or <code>null</code> if not found
	 */
	public ColumnParameter getColumn(String columnName) {
		if (columnName == null) {
			return null;
		}
		for (ColumnParameter column : columns) {
			if (columnName.equals(column.getData())) {
				return column;
			}
		}
		return null;
	}

	/**
	 * Add a new column
	 *
	 * @param columnName the name of the column
	 * @param searchable whether the column is searchable or not
	 * @param orderable whether the column is orderable or not
	 * @param searchValue if any, the search value to apply
	 */
	public void addColumn(String columnName, boolean searchable, boolean orderable, String searchValue) {
		this.columns.add(new ColumnParameter(columnName, "", searchable, orderable, new SearchParameter(searchValue, false)));
	}

	/**
	 * Add an order on the given column
	 *
	 * @param columnName the name of the column
	 * @param ascending whether the sorting is ascending or descending
	 */
	public void addOrder(String columnName, boolean ascending) {
		if (columnName == null) {
			return;
		}
		for (int i = 0; i < columns.size(); i++) {
			if (!columnName.equals(columns.get(i).getData())) {
				continue;
			}
			order.add(new OrderParameter(i, ascending ? "asc" : "desc"));
		}
	}

	@Override
	public String toString() {
		return "DataTablesInput [draw=" + draw + ", start=" + start + ", length=" + length + ", search=" + search + ", order=" + order + ", columns=" + columns + "]";
	}
}
