/**
 * 
 */
package com.privasia.procurehere.core.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Sana
 * @param <E> the instance type to be returned after parsing.
 */
public class RFSBqFileParser<E extends SourcingFormRequestBqItem> {

	private static final Logger LOG = LogManager.getLogger(RFSBqFileParser.class);

	protected Workbook workbook = null;
	protected FileInputStream fs = null;

	protected Class<E> clazz;

	public RFSBqFileParser(Class<E> clazz) {
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
			LOG.info("RFS BQ Item List Records : " + list.size());
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

			// fs = new FileInputStream(file);
			//
			// WorkbookSettings ws = new WorkbookSettings();
			// ws.setLocale(new Locale("en", "EN"));
			// workbook = Workbook.getWorkbook(fs, ws);

			/** Getting Default Sheet i.e. 0 **/
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
			/** Total No Of Rows in Sheet, will return you no of rows that are occupied with some data **/
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
				throw new ExcelParseException("Invalid excel file format.", "");
			}

			/*
			 * Validate the column titles (1st row) and sequence in the excel file.
			 */
			List<String> columnTitle = new ArrayList<String>();
			rowData = sheet.getRow(startingRow);
			// columnCount = rowData.length;
			columnCount = rowData.getLastCellNum();
			if (columnCount > 0 && rowData.getCell(0).getStringCellValue().trim().length() > 0) {
				for (int j = 0; j < columnCount; j++) {
					String colTitle = rowData.getCell(j).getStringCellValue().trim();

					if (j < 8 && !colTitle.equalsIgnoreCase(Global.BQ_EXCEL_COLUMNS_TYPE_2[j])) {
						throw new ExcelParseException("RFS BQ Item List excel file must contain '" + Global.BQ_EXCEL_COLUMNS_TYPE_2[j] + "' at column " + (j + 1), "");
					}
					if (j >= 8) {
						if (StringUtils.checkString(colTitle).length() > 0) {
							LOG.info("colTitle : " + colTitle);
							columnTitle.add(colTitle);
						}

					}
				}
			}

			// Skip to the data row (startingRow will be the heading column)
			startingRow++;

			// List for checking duplicate ItemName
			List<String> parentList = new ArrayList<>();
			List<String> childList = new ArrayList<>();
			int level = 0;
			int order = 0;
			int oldLevel = 0;

			/** Start reading data from 2nd row, as per standard **/
			for (int i = startingRow; i < rowCount; i++) {

				/** Get Individual Row **/
				rowData = sheet.getRow(i);

				/** Total Total No Of Columns in Sheet **/
				// columnCount = rowData.length;
				if (rowData != null) {
					// columnCount = rowData.getLastCellNum();

					// Break at the first occurrence of empty data at column 0;
					// if (rowData.getCell(0) == null || (rowData.getCell(0) != null &&
					// rowData.getCell(0).getStringCellValue() == null) || (rowData.getCell(0) != null &&
					// rowData.getCell(0).getStringCellValue() != null &&
					// rowData.getCell(0).getStringCellValue().trim().equals("")))
					// break;
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
									throw new ExcelParseException("BQ SR No is missing row of " + (i + 1), "");
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
									throw new ExcelParseException("BQ Item Name is missing row of " + (i + 1), "");
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
								// bqItems.setUom(uomService.getUombyCode(StringUtils.checkString(cellData)));
								break;
							case 4:
								// QUANTITY
								if (StringUtils.checkString(cellData).length() > 0 && new BigDecimal(cellData).doubleValue() == Double.valueOf(0)) {
									throw new ExcelParseException("Quantity for \"" + item.getItemName() + "\" should not be zero (0) at row number : " + (i + 1), null);
								} else if (StringUtils.checkString(cellData).length() > 0) {
									item.setQuantity(new BigDecimal(cellData));
								} else if (item.getOrder() != 0) {
									throw new ExcelParseException("Please provide Quantity for \"" + item.getItemName() + "\" at row number : " + (i + 1), null);
								}
								break;
							case 5:
								// PRICE
								if (StringUtils.checkString(cellData).length() > 0) {
									BigDecimal bd = new BigDecimal(cellData);
									item.setUnitPrice(bd);
								}
								break;
							case 6:
								// PRICE TYPE
								if (item.getOrder() != 0 && StringUtils.checkString(cellData).length() == 0) {
									throw new ExcelParseException("Please provide PRICE TYPE for \"" + item.getItemName() + "\" at row number : " + (i + 1), null);
								}
								try {
									item.setPriceType(PricingTypes.convertFromString(StringUtils.checkString(cellData)));
									if (item.getOrder() != 0 && PricingTypes.BUYER_FIXED_PRICE == item.getPriceType() && item.getUnitPrice() == null) {
										throw new ExcelParseException("Please provide Unit Price for \"" + item.getItemName() + "\" at row number : " + (i + 1), null);
									} else if (PricingTypes.BUYER_FIXED_PRICE != item.getPriceType() && item.getUnitPrice() != null) {
										throw new ExcelParseException("Unit Price should not be specified for \"" + item.getItemName() + "\" at row number : " + (i + 1), null);
									}
								} catch (ExcelParseException e) {
									throw new ExcelParseException(e.getMessage(), null);
								} catch (Exception e) {
									LOG.error("Error parsing file : " + e.getMessage(), e);
									throw new ExcelParseException("Invalid PRICE TYPE '" + cellData + "' specified in the excel file", null);
								}
								break;
							case 7:
								if (StringUtils.checkString(cellData).length() > 0) {
									item.setUnitBudgetPrice(new BigDecimal(cellData));
								}
								break;

							case 8:
								// FIELD1
								item.setField1(StringUtils.checkString(cellData));
								break;
							case 9:
								// FIELD2
								item.setField2(StringUtils.checkString(cellData));
								break;
							case 10:
								// FIELD3
								item.setField3(StringUtils.checkString(cellData));
								break;
							case 11:
								// FIELD4
								item.setField4(StringUtils.checkString(cellData));
								break;
							case 12:
								// FIELD5
								item.setField5(StringUtils.checkString(cellData));
								break;
							case 13:
								// FIELD6
								item.setField6(StringUtils.checkString(cellData));
								break;
							case 14:
								// FIELD7
								item.setField7(StringUtils.checkString(cellData));
								break;
							case 15:
								// FIELD8
								item.setField8(StringUtils.checkString(cellData));
								break;
							case 16:
								// FIELD9
								item.setField9(StringUtils.checkString(cellData));
								break;
							case 17:
								// FIELD10
								item.setField10(StringUtils.checkString(cellData));
								break;
							}

						}
						list.add(item);
					}
				} else
					// Break the loop at first empty row after row number 12 to assume end of records.
					// in case if the user mistakenly introduced empty row, we will not process the rows after such
					// empty row.
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
