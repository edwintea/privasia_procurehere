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
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * @author Nitin
 * @param <E> the instance type to be returned after parsing.
 */
// TODO should be removed no use because of removed from pr screen
@Component
public class PrFileParser<E extends PrItem> {

	// TODO Check Sl no.(item) Sequence

	private static final Logger LOG = LogManager.getLogger(PrFileParser.class);

	protected Workbook workbook = null;
	protected FileInputStream fs = null;

	@Resource
	MessageSource messageSource;

	protected Class<E> clazz;

	public PrFileParser(Class<E> clazz) {
		this.clazz = clazz;
	}

	// public PrFileParser() {
	// ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
	// this.clazz = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
	// }

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
			LOG.info("PR Item List Records : " + list.size());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ExcelParseException(messageSource.getMessage("file.parse.error", new Object[] { e.getMessage() }, Global.LOCALE), file.getName());
		} finally {
			if (workbook != null) {
				workbook.close();
			}
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

			fs = new FileInputStream(file);

			WorkbookSettings ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			workbook = Workbook.getWorkbook(fs, ws);

			/** Getting Default Sheet i.e. 0 **/
			sheet = workbook.getSheet(0);

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ExcelParseException(messageSource.getMessage("file.parse.open.error", new Object[] { e.getMessage() }, Global.LOCALE), file.getName());
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

		Cell[] rowData = null;
		int rowCount;
		int columnCount;

		try {
			/** Total No Of Rows in Sheet, will return you no of rows that are occupied with some data **/
			rowCount = sheet.getRows();
			LOG.info("Row count is>>>>>>>>>>>>>>>" + rowCount);

			int startingRow = 0;
			boolean headingFound = false;
			// Skip to the line containing the column headings....
			if (rowCount > 0) {
				for (startingRow = 0; startingRow < rowCount; startingRow++) {
					rowData = sheet.getRow(startingRow);
					if (rowData.length > 0 && rowData[0].getContents().trim().length() != 0) {
						if (rowData[0].getContents().trim().equals("Sl No")) {
							headingFound = true;
							break;
						}
					}
				}
			} else {
				throw new ExcelParseException(messageSource.getMessage("file.parse.empty.error", new Object[] {}, Global.LOCALE), "");
			}

			if (!headingFound) {
				throw new ExcelParseException(messageSource.getMessage("file.parse.invalid.file.format", new Object[] {}, Global.LOCALE), "");
			}

			/*
			 * Validate the column titles (1st row) and sequence in the excel file.
			 */
			rowData = sheet.getRow(startingRow);
			columnCount = rowData.length;
			if (rowData.length > 0 && rowData[0].getContents().trim().length() != 0) {
				for (int j = 0; j < columnCount; j++) {
					String colTitle = rowData[j].getContents().trim();

					if (j < 7 && !colTitle.equalsIgnoreCase(Global.PR_EXCEL_COLUMNS[j])) {
						throw new ExcelParseException(messageSource.getMessage("file.parse.required.column", new Object[] { Global.BQ_EXCEL_COLUMNS_TYPE_1[j], (j + 1) }, Global.LOCALE), "");
					}
				}
			}

			// Skip to the data row (startingRow will be the heading column)
			startingRow++;

			// List for checking duplicate ItemName
			List<String> parentList = new ArrayList<>();
			List<String> childList = new ArrayList<>();
			/** Start reading data from 2nd row, as per standard **/
			for (int i = startingRow; i < rowCount; i++) {

				/** Get Individual Row **/
				rowData = sheet.getRow(i);

				// Break at the first occurrence of empty data at column 0;
				if (rowData[0].getContents() == null || (rowData[0].getContents() != null && rowData[0].getContents().trim().equals("")))
					break;

				/** Total Total No Of Columns in Sheet **/
				columnCount = rowData.length;

				E item = clazz.newInstance();
				for (int j = 0; j < columnCount; j++) {
					String cellData = StringUtils.checkString(rowData[j].getContents());

					if (j >= rowData.length) {
						break;
					}
					switch (j) {

					case 0:
						// SR NO
						try {

							int index = cellData.indexOf(".");
							if (index != -1) {
								item.setLevel(Integer.parseInt(cellData.substring(0, index)));
								item.setOrder(Integer.parseInt(cellData.substring(index + 1)));
							} else {
								item.setLevel(Integer.parseInt(cellData));
								item.setOrder(0);
							}
							if (item.getOrder() == 0) {
								childList = new ArrayList<>();
							}
						} catch (Exception e) {
							LOG.error("Error while converting string to integer :" + e.getMessage());
							throw new ExcelParseException(messageSource.getMessage("file.parse.slno.error", new Object[] { (i + 1) }, Global.LOCALE), null);
						}
						break;
					case 1:
						LOG.info(" SECTION_NAME item.getOrder() : " + item.getOrder());
						// SECTION_NAME
						if (StringUtils.checkString(cellData).length() > 0 && item.getOrder() == 0) {
							// Check if Top level Item Name exists
							// if (item.getOrder() == 0) {
							for (String parentItem : parentList) {
								if (parentItem.equalsIgnoreCase(cellData))
									throw new ExcelParseException(messageSource.getMessage("file.parse.duplicate.item.error", new Object[] { Global.BQ_EXCEL_COLUMNS_TYPE_1[1], cellData, (i + 1) }, Global.LOCALE), null);
							}
							parentList.add(cellData);
							// }
							// else { // Check if Child Item Name at specified order already exists
							// for (String childItem : childList) {
							// if (childItem.equalsIgnoreCase(cellData))
							// throw new
							// ExcelParseException(messageSource.getMessage("file.parse.duplicate.subitem.error", new
							// Object[] { Global.BQ_EXCEL_COLUMNS[1], cellData, (i + 1) }, Global.LOCALE), "");
							// }
							// childList.add(cellData);
							// }
							item.setItemName(StringUtils.checkString(cellData));
						} else {
							throw new ExcelParseException(messageSource.getMessage("file.parse.sectionname.not.found", new Object[] { (i + 1) }, Global.LOCALE), "");
						}
						break;
					case 2:
						LOG.info(" ITEM_NAME item.getOrder() : " + item.getOrder());
						// ITEM_NAME
						if (StringUtils.checkString(cellData).length() > 0 && item.getOrder() != 0) {
							// Check if Top level Item Name exists
							// if (item.getOrder() == 0) {
							// for (String parentItem : parentList) {
							// if (parentItem.equalsIgnoreCase(cellData))
							// throw new ExcelParseException(messageSource.getMessage("file.parse.duplicate.item.error",
							// new Object[] { Global.BQ_EXCEL_COLUMNS[1], cellData, (i + 1) }, Global.LOCALE), null);
							// }
							// parentList.add(cellData);
							// } else { // Check if Child Item Name at specified order already exists
							for (String childItem : childList) {
								if (childItem.equalsIgnoreCase(cellData))
									throw new ExcelParseException(messageSource.getMessage("file.parse.duplicate.subitem.error", new Object[] { Global.BQ_EXCEL_COLUMNS_TYPE_1[1], cellData, (i + 1) }, Global.LOCALE), "");
							}
							childList.add(cellData);
							// }
							item.setItemName(StringUtils.checkString(cellData));
						} else {
							throw new ExcelParseException(messageSource.getMessage("file.parse.itemname.not.found", new Object[] { (i + 1) }, Global.LOCALE), "");
						}
						break;
					case 3:
						// ITEM_DESCRIPTION
						item.setItemDescription(StringUtils.checkString(cellData));
						break;
					case 4:
						// QUANTITY
						if (StringUtils.checkString(cellData).length() > 0)
							item.setQuantity(new BigDecimal(cellData));
						break;
					case 5:
						// PRICE
						if (StringUtils.checkString(cellData).length() > 0) {
							BigDecimal bd = new BigDecimal(cellData);
							item.setUnitPrice(bd);
						}
						break;
						case 6:
							// PRICE PER UNIT
							if (StringUtils.checkString(cellData).length() > 0) {
								BigDecimal bd = new BigDecimal(cellData);
								item.setPricePerUnit(bd);
							}
							break;
					case 7:
						// TAX
						item.setItemTax(StringUtils.checkString(cellData));
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
					}

				}
				list.add(item);

			} // End For
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ExcelParseException(e.getMessage(), null);
		}

		return list;
	}

}
