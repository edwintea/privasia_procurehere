/**
 * 
 */
package com.privasia.procurehere.core.parsers;

import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sana
 * @param <E> the instance type to be returned after parsing.
 */
public class RFSSorFileParser<E extends SourcingFormRequestSorItem> {

	private static final Logger LOG = LogManager.getLogger(RFSSorFileParser.class);

	protected Workbook workbook = null;
	protected FileInputStream fs = null;

	protected Class<E> clazz;

	public RFSSorFileParser(Class<E> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @param file the file
	 * @return the list of entities after parsing the file.
	 * @throws ExcelParseException if any error during parsing.
	 */
	public List<E> parse(File file) throws ExcelParseException {
		List<E> list = null;
		try {
			Sheet sheet = processExcel(file);
			list = readContents(sheet);
			LOG.info("RFS SOR Item List Records : " + list.size());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ExcelParseException("Error parsing file : " + e.getMessage(), file.getName());
		} finally {
			// if (workbook != null) {
			// workbook.close();
			// }
			workbook = null;
			try {
				if (fs != null) {
					fs.close();
				}
				fs = null;
			} catch (IOException e) {
				LOG.error("Error cleanup : " + e.getMessage(), e);
			}
		}
		return list;

	}

	/**
	 * @param file the file
	 * @return the Sheet
	 * @throws ExcelParseException if any problem in parsing the excel file
	 */
	protected Sheet processExcel(File file) throws ExcelParseException {
		Sheet sheet = null;
		try {
			LOG.debug("Start parsing Uploaded excel file");

			workbook = WorkbookFactory.create(file);

			sheet = workbook.getSheetAt(0);

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ExcelParseException("Error opening file for reading. " + e.getMessage(), file.getName());
		}
		return sheet;
	}

	/**
	 * @param sheet
	 * @return
	 * @throws ExcelParseException
	 * @throws IOException
	 */
	private List<E> readContents(Sheet sheet) throws ExcelParseException, IOException {

		List<E> list = new ArrayList<E>();

		// Cell[] rowData = null;
		Row rowData = null;
		int rowCount;
		int columnCount;

		try {
			rowCount = sheet.getLastRowNum() + 1;
			LOG.info("Row count is>>>>>>>>>>>>>>>" + rowCount);

			int startingRow = 0;
			boolean headingFound = false;
			// Skip to the line containing the column headings....
			if (rowCount > 0) {
				for (startingRow = 0; startingRow < rowCount; startingRow++) {
					rowData = sheet.getRow(startingRow);
					if (rowData != null && rowData.getCell(0) != null && rowData.getCell(0).getStringCellValue().length() > 0) {
						if (rowData.getCell(0).getStringCellValue().trim().equals("SR No")) {
							headingFound = true;
							break;
						}
					}
				}
			} else {
				throw new ExcelParseException("Empty excel file provided.", "");
			}

			if (!headingFound) {
				throw new ExcelParseException("Invalid excel file format. Could not find 'SR No' as column heading in any row of the file at 1st column to begin data processing.", "");
			}


			List<String> columnTitle = new ArrayList<String>();
			rowData = sheet.getRow(startingRow);
			// columnCount = rowData.length;
			columnCount = rowData.getLastCellNum();
			if (columnCount > 0 && rowData.getCell(0).getStringCellValue().trim().length() > 0) {
				for (int j = 0; j < columnCount; j++) {
					String colTitle = rowData.getCell(j).getStringCellValue().trim();

					if (j < 4 && !colTitle.equalsIgnoreCase(Global.BQ_EXCEL_COLUMNS_TYPE_2[j])) {
						throw new ExcelParseException("RFS SOR Item List excel file must contain '" + Global.BQ_EXCEL_COLUMNS_TYPE_2[j] + "' at column " + (j + 1), "");
					}
					if (j >= 4) {
						if (StringUtils.checkString(colTitle).length() > 0) {
							LOG.info("colTitle : " + colTitle);
							columnTitle.add(colTitle);
						}

					}
				}
			}

			startingRow++;

			// List for checking duplicate ItemName
			List<String> parentList = new ArrayList<>();
			List<String> childList = new ArrayList<>();
			int level = 0;
			int order = 0;
			int oldLevel = 0;

			for (int i = startingRow; i < rowCount; i++) {

				rowData = sheet.getRow(i);

				if (rowData != null) {
					if (columnCount > 0 && rowData.getCell(0) != null) {
						Cell cell = rowData.getCell(0);
						String data = getCellData(cell);

						// Break at the first occurrence of empty data at column 0;
						if (StringUtils.checkString(data).length() == 0)
							break;

						E item = clazz.newInstance();
						item.setColumnTitles(columnTitle);
						for (int j = 0; j < columnCount; j++) {
							// LOG.info("columnCount :" + columnCount);

							/*
							 * if (j >= rowData.getLastCellNum()) { break; }
							 */

							String cellData = getCellData(rowData.getCell(j));

							switch (j) {
								/*
							 * case 0: // ID item.setId(StringUtils.checkString(cellData)); break;
							 */
								case 0:
									// SR NO
									if (j == 0 && rowData.getCell(j) != null && rowData.getCell(j).getCellType() == 0) {
										if (StringUtils.checkString(cellData).length() > 0) {
											if (cellData.indexOf(".") == -1) {
												cellData = cellData.concat(".0");
											}
										}

									}
									LOG.info("{ ============>   :" + cellData + "==== " + i + "==== " + oldLevel);

									if(cellData.contains(".")) {
										String[] sections = cellData.split("\\.");

										if (sections != null && sections.length > 1) {
											if (oldLevel != Integer.valueOf(sections[0]) && Integer.valueOf(sections[1]) != 0) {
												throw new ExcelParseException("Section is missing row of " + (i), "");
											}
										}
									}

									if (StringUtils.checkString(cellData).length() > 0) {
										try {
											int newLevel = -1;
											if (cellData.indexOf(".") != -1) {
												newLevel = Integer.parseInt(cellData.substring(0, cellData.indexOf(".")));
											} else {
												newLevel = Integer.parseInt(cellData.substring(0, 1));
											}

											if (oldLevel == newLevel) {

												order = order + 1;
											} else {
												level = level + 1;
												order = 0;
											}
											oldLevel = newLevel;
											item.setLevel(level);
											item.setOrder(order);
											LOG.info("s.no: " + level + "." + order);
											if (item.getOrder() == 0) {
												childList = new ArrayList<>();
											}
										} catch (Exception e) {
											LOG.error("Error while converting string to integer :" + e.getMessage());
											throw new ExcelParseException("SR No. should be numbers only at row number : " + (i + 1), null);
										}
									} else {
										throw new ExcelParseException("SOR SR No is missing row of " + (i + 1), "");
									}

									break;
								case 1:
									// ITEM_NAME
									if (StringUtils.checkString(cellData).length() > 0) {

										// Check if Top level Item Name exists
										if (item.getOrder() == 0) {
											for (String parentItem : parentList) {
												if (parentItem.equalsIgnoreCase(cellData))
													throw new ExcelParseException("Parent " + Global.BQ_EXCEL_COLUMNS_TYPE_2[1] + " \"" + cellData + "\" " + " is already exists at row number : " + (i + 1), null);
											}
											parentList.add(cellData);
										} else { // Check if Child Item Name at specified order already exists
											for (String childItem : childList) {
												if (childItem.equalsIgnoreCase(cellData))
													throw new ExcelParseException("Child " + Global.BQ_EXCEL_COLUMNS_TYPE_2[1] + " \"" + cellData + "\" " + " is already exists at row number : " + (i + 1), null);
											}
											childList.add(cellData);
										}
										item.setItemName(cellData);
									} else {
										throw new ExcelParseException("SOR Item Name is missing row of " + (i + 1), "");
									}
									break;
								case 2:
									// ITEM_DESCRIPTION
									item.setItemDescription(StringUtils.checkString(cellData));
									break;
								case 3:
									// UOM
									if (item.getOrder() != 0 && StringUtils.checkString(cellData).length() == 0) {
										throw new ExcelParseException("Please provide UOM for \"" + item.getItemName() + "\" at row number : " + (i + 1), null);
									}
									Uom uom = new Uom();
									uom.setUom(StringUtils.checkString(cellData));
									item.setUom(uom);
									break;
								case 4:
									// FIELD1
									item.setField1(StringUtils.checkString(cellData));
									break;
							}

						}
						list.add(item);
					}
				} else
					break;

			} // End For
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ExcelParseException(e.getMessage(), null);
		}

		return list;
	}

	private String getCellData(Cell cell) {
		String data = "";
		if (cell != null) {
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					data = cell.getRichStringCellValue().getString();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						data = cell.getDateCellValue().toString();
					} else {
						double value = cell.getNumericCellValue();
						if (value % 1 == 0) {
							data = String.valueOf((long) value);
						} else {
							data = String.valueOf(value);
						}
					}
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					data = String.valueOf(cell.getBooleanCellValue());
					break;
				case Cell.CELL_TYPE_BLANK:
					break;
				default:
			}
		}
		return data;
	}

}
