/**
 * 
 */
package com.privasia.procurehere.core.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.privasia.procurehere.core.entity.CqItem;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin
 * @param <E> the instance type to be returned after parsing.
 */
public class CqFileParser<E extends CqItem> {

	private static final Logger LOG = LogManager.getLogger(CqFileParser.class);

	protected Workbook workbook = null;
	protected FileInputStream fs = null;

	protected Class<E> clazz;

	public CqFileParser(Class<E> clazz) {
		this.clazz = clazz;
	}

	// TODO Check SR no.(item) Sequence

	/**
	 * @param file the file
	 * @return the list of entities after parsing the file.
	 * @throws ExcelParseException if any error during parsing.
	 */
	public Map<Integer, Map<Integer, E>> parse(File file) throws ExcelParseException {
		Map<Integer, Map<Integer, E>> list = null;
		try {
			Sheet sheet = processExcel(file);
			list = readContents(sheet);
			LOG.debug("CQ Item List Records : " + list.size());
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
	private Map<Integer, Map<Integer, E>> readContents(Sheet sheet) throws ExcelParseException, IOException {

		Map<Integer, Map<Integer, E>> dataMap = new HashMap<Integer, Map<Integer, E>>();

		// Cell[] rowData = null;
		Row rowData = null;
		int rowCount;
		int columnCount;
		int level = 0;
		int order = 0;
		int oldLevel = 0;
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
				throw new ExcelParseException("Invalid excel file format. Could not find 'SR No' as column heading in any row of the file at 1st column to begin data processing.", "");
			}

			/*
			 * Validate the column titles (1st row) and sequence in the excel file.
			 */
			rowData = sheet.getRow(startingRow);
			// columnCount = rowData.length;
			columnCount = rowData.getLastCellNum();
			if (columnCount > 0 && rowData.getCell(0).getStringCellValue().trim().length() > 0) {
				for (int j = 0; j < columnCount; j++) {
					String colTitle = rowData.getCell(j).getStringCellValue().trim();

					// if heading is greater then CQ_EXCEL_COLUMNS
					if (j >= Global.CQ_EXCEL_COLUMNS.length) {
						throw new ExcelParseException("Invalid Excel format. There should not be more then 30 option Columns in excel.", "");
					}

					if (!colTitle.equalsIgnoreCase(Global.CQ_EXCEL_COLUMNS[j])) {
						throw new ExcelParseException("CQ Item List excel file must contain '" + Global.CQ_EXCEL_COLUMNS[j] + "' at column " + (j + 1), "");
					}

				}
			}

			// Skip to the data row (startingRow will be the heading column)
			startingRow++;

			LOG.info("Starting data parsing at : " + startingRow);

			// List for checking duplicate ItemName
			List<String> parentList = new ArrayList<>();
			List<String> childList = new ArrayList<>();

			/** Start reading data from 2nd row, as per standard **/
			for (int i = startingRow; i < rowCount; i++) {

				/** Get Individual Row **/
				rowData = sheet.getRow(i);

				if (rowData == null) {
					LOG.info("Hard breaking at row as rowData is null >>>>>>>>>>>>>>>" + i);
					break;
				}

				/** Total Total No Of Columns in Sheet **/
				// columnCount = rowData.length;
				columnCount = rowData.getLastCellNum();

				if (columnCount > 0 && (rowData.getCell(0) != null || rowData.getCell(1) != null)) {
					Cell cell = rowData.getCell(0);
					String data = getCellData(cell);
					Cell cell2 = rowData.getCell(1);
					String data2 = getCellData(cell2);

					// Break at the first occurrence of empty data at column 0 and column 1;
					if ((StringUtils.checkString(data).length() == 0) && ((StringUtils.checkString(data2).length() == 0))) {
						break;
					}

					E item = clazz.newInstance();
					for (int j = 0; j < columnCount; j++) {

						if (j >= rowData.getLastCellNum()) {
							break;
						}

						String cellData = getCellData(rowData.getCell(j));
						switch (j) {

						case 0:
							if (StringUtils.checkString(getCellData(rowData.getCell(0))).length() == 0) {
								throw new ExcelParseException("SR number at row number " + (i + 1) + " is required ", null);
							}
							// SR NO
							try {

								/*
								 * int index = cellData.indexOf("."); if (index != -1) {
								 * item.setLevel(Integer.parseInt(cellData.substring(0, index)));
								 * item.setOrder(Integer.parseInt(cellData.substring(index + 1))); } else {
								 * item.setLevel(Integer.parseInt(cellData)); item.setOrder(0); }
								 */
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

								if (item.getOrder() == 0) {
									childList = new ArrayList<>();
								}

							} catch (Exception e) {
								LOG.error("Error while converting string to integer :" + e.getMessage());
								throw new ExcelParseException("SR No. should be numbers only at row number : " + (i + 1), null);
							}
							// Check if Top level Item exists
							Map<Integer, E> childDataMap = dataMap.get(item.getLevel());
							if (childDataMap == null) {
								childDataMap = new HashMap<Integer, E>();
							}
							// Check if Child Item at specified order already exists in map.
							E mapItem = childDataMap.get(item.getOrder());
							if (mapItem != null) {
								throw new ExcelParseException("Duplicate item with " + Global.CQ_EXCEL_COLUMNS[0] + " : " + item.getLevel() + "." + item.getOrder(), null);
							}

							childDataMap.put(item.getOrder(), item);
							dataMap.put(item.getLevel(), childDataMap);
							break;
						case 1:
							// ITEM_NAME
							if (cellData.length() == 0) {
								throw new ExcelParseException(Global.CQ_EXCEL_COLUMNS[1] + " is required at row number : " + (i + 1), null);
							}
							if (cellData.length() > 250) {
								throw new ExcelParseException("The input value is longer than 250 characters for " + Global.CQ_EXCEL_COLUMNS[1] + " at row number:" + (i + 1), null);
							}
							// Check if Top level Item Name exists
							if (item.getOrder() == 0) {
								for (String parentItem : parentList) {
									if (parentItem.equalsIgnoreCase(cellData))
										throw new ExcelParseException("Parent " + Global.CQ_EXCEL_COLUMNS[1] + " \"" + cellData + "\" " + " is already exists at row number : " + (i + 1), null);
								}
								parentList.add(cellData);
							} else { // Check if Child Item Name at specified order already exists
								for (String childItem : childList) {
									if (childItem.equalsIgnoreCase(cellData))
										throw new ExcelParseException("Child " + Global.CQ_EXCEL_COLUMNS[1] + " \"" + cellData + "\" " + " is already exists at row number : " + (i + 1), null);
								}
								childList.add(cellData);
							}

							item.setItemName(StringUtils.checkString(cellData));
							break;
						case 2:
							// ITEM_DESCRIPTION
							if (cellData.length() > 250) {
								throw new ExcelParseException("The input value is longer than 250 characters for " + Global.CQ_EXCEL_COLUMNS[2] + " at row number:" + (i + 1), null);
							}
							item.setItemDescription(StringUtils.checkString(cellData));
							break;
						case 3:
							// CQ Type
							if (cellData.length() == 0 && item.getOrder() != 0) {
								throw new ExcelParseException(Global.CQ_EXCEL_COLUMNS[3] + " is required at row number : " + (i + 1), null);
							}
							if (item.getOrder() != 0) {
								try {
									// throw exception in not matched
									CqType.fromString(cellData).toString();
									item.setCqType(CqType.fromString(cellData));
								} catch (Exception e) {
									throw new ExcelParseException("Invalid QUESTION TYPE \"" + cellData + "\" specified in the excel file at row number : " + (i + 1), null);
								}
							}
							break;
						case 4:
							if (cellData.length() == 0 && item.getOrder() != 0) {
								item.setAttachment(Boolean.FALSE);
								// throw new ExcelParseException(Global.CQ_EXCEL_COLUMNS[4] + " is required at row
								// number : " + (i + 1), null);
							}
							if (item.getOrder() != 0 && !cellData.equalsIgnoreCase("YES") && !cellData.equalsIgnoreCase("NO") && cellData.length()>0) {
								throw new ExcelParseException("Invalid ATTACHMENT \"" + cellData + "\" specified in the excel file at row number : " + (i + 1), null);
							}
							item.setAttachment(cellData.equalsIgnoreCase("YES") ? true : false);
							break;
						case 5:
							if (cellData.length() == 0 && item.getOrder() != 0) {

								item.setOptional(Boolean.FALSE);

								// throw new ExcelParseException(Global.CQ_EXCEL_COLUMNS[5] + " is required at row
								// number : " + (i + 1), null);
							}
							if (item.getOrder() != 0 && !cellData.equalsIgnoreCase("YES") && !cellData.equalsIgnoreCase("NO") && cellData.length()>0) {
								throw new ExcelParseException("Invalid OPTIONAL \"" + cellData + "\" specified in the excel file at row number : " + (i + 1), null);
							}
							item.setOptional(cellData.equalsIgnoreCase("YES") ? true : false);
							break;
						case 6:
							if (item.getAttachment() && cellData.length() == 0 && item.getOrder() != 0) {
								if (!item.getAttachment()) {
									item.setIsSupplierAttachRequired(Boolean.FALSE);
								}
								// throw new ExcelParseException(Global.CQ_EXCEL_COLUMNS[6] + " is required at row
								// number : " + (i + 1), null);
							}
							if (item.getAttachment() && item.getOrder() != 0 && !cellData.equalsIgnoreCase("YES") && !cellData.equalsIgnoreCase("NO") && cellData.length()>0) {
								throw new ExcelParseException("Invalid ATTACHMENT REQUIRED \"" + cellData + "\" specified in the excel file at row number : " + (i + 1), null);
							}
							item.setIsSupplierAttachRequired(cellData.equalsIgnoreCase("YES") ? true : false);
							if (Boolean.FALSE == item.getAttachment() && Boolean.TRUE == item.getIsSupplierAttachRequired()) {
								throw new ExcelParseException("ATTACHMENT REQUIRED cannot be set to YES when ATACHMENT is NO in the excel file at row number : " + (i + 1), null);
							}
							break;

						default:
							// Ignore options
							if (item.getCqType() == CqType.TEXT || item.getCqType() == CqType.DATE || item.getCqType() == CqType.PARAGRAPH  || item.getCqType() == CqType.NUMBER) {
								continue;
							}
							if (cellData.length() > 128) {
								throw new ExcelParseException("The input value is longer than 128 characters for " + "OPTION" + " at row number:" + (i + 1), null);
							}
							if (cellData.length() > 0) {
								if (item.getCqType() == CqType.CHOICE_WITH_SCORE && !(cellData.indexOf('/') == 1 || cellData.indexOf('/') == 2)) {
									throw new ExcelParseException("Question Type is Choice with score option '" + cellData + "' should be start with a score. e.g. '1/" + cellData + "'at row number : " + (i + 1), null);
								}
								// validate duplicate options
								for (String option : item.getOptions()) {
									if (option.equalsIgnoreCase(cellData))
										throw new ExcelParseException("Duplicate option value specified for Options '" + cellData + "' at row number : " + (i + 1), null);
								}

								item.getOptions().add(cellData);
							}

							break;
						}
					}

					// validate options
					int size = 0;
					if (item.getCqType() != null)
						switch (item.getCqType()) {
						case CHECKBOX:
							size = 1;
							break;
						case CHOICE:
						case CHOICE_WITH_SCORE:
						case LIST:
							size = 2;
							break;
						default:
							break;
						}

					if (item.getOptions().size() < size) {
						throw new ExcelParseException("Required options is " + size + " for Question Type : " + item.getCqType().getValue(), null);
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

		return dataMap;
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
