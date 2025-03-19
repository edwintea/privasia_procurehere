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

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin
 * @param <E> the instance type to be returned after parsing.
 */
public class SupplierParser<E extends Supplier> {

	private static final Logger LOG = LogManager.getLogger(SupplierParser.class);

	protected Workbook workbook = null;
	protected FileInputStream fs = null;

	protected Class<E> clazz;

	public SupplierParser(Class<E> clazz) {
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
			LOG.info("Supplier List Records : " + list.size());
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

				// Row row = sheet.getRow(startingRow);
				// Cell cell = row.getCell(0);
				// if (cell.getStringCellValue().trim().equals("COMPANY NAME")) {
				// headingFound = true;
				// break;
				// }

				for (startingRow = 0; startingRow < rowCount; startingRow++) {
					rowData = sheet.getRow(startingRow);
					if (rowData != null && rowData.getCell(0) != null && rowData.getCell(0).getStringCellValue().length() > 0) {
						if (rowData.getCell(0).getStringCellValue().trim().equals("COMPANY NAME")) {
							headingFound = true;
							break;
						}
					}
				}
			} else {
				throw new ExcelParseException("Empty excel file provided.", "");
			}

			if (!headingFound) {
				throw new ExcelParseException("Invalid excel file format. Could not find 'Company Name' as column heading in any row of the file at 1st column to begin data processing.", "");
			}

			/*
			 * Validate the column titles (1st row) and sequence in the excel file.
			 */
			List<String> columnTitle = new ArrayList<String>();
			rowData = sheet.getRow(startingRow);
			columnCount = rowData.getLastCellNum();
			if (columnCount > 0 && rowData.getCell(0).getStringCellValue().trim().length() > 0) {
				for (int j = 0; j < columnCount; j++) {
					String colTitle = rowData.getCell(j).getStringCellValue().trim();

					if (j < 10 && !colTitle.equalsIgnoreCase(Global.SUPPLIER_EXCEL_COLUMNS[j])) {
						throw new ExcelParseException("Supplier excel file must contain '" + Global.SUPPLIER_EXCEL_COLUMNS[j] + "' at column " + (j + 1), "");
					}
					if (j >= 11) {
						columnTitle.add(colTitle);
					}
				}
			}

			// Skip to the data row (startingRow will be the heading column)
			startingRow++;

			/** Start reading data from 2nd row, as per standard **/
			for (int i = startingRow; i < rowCount; i++) {

				/** Get Individual Row **/
				rowData = sheet.getRow(i);

				if (rowData == null)
					break; // no more data to read...

				/** Total No Of Columns in Sheet **/
				columnCount = rowData.getLastCellNum();

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
					for (int j = 0; j < columnCount; j++) {
						LOG.info("columnCount :" + columnCount);

						if (j >= rowData.getLastCellNum()) {
							break;
						}

						String cellData = getCellData(rowData.getCell(j));
						switch (j) {

						case 0:
							// COMPANY NAME
							LOG.info("cellData :" + cellData);
							if (StringUtils.checkString(cellData).length() > 0) {
								item.setCompanyName(cellData);
							}
							break;
						case 1:
							// COUNTRY CODE
							Country country = new Country();
							country.setCountryCode(StringUtils.checkString(cellData));
							item.setRegistrationOfCountry(country);
							break;
						case 2:
							// REGISTRATION NUMBER
							if (StringUtils.checkString(cellData).length() > 0)
								item.setCompanyRegistrationNumber(cellData);
							break;
						case 3:
							// Contact Full Name
							LOG.info("cellData :" + cellData);
							if (StringUtils.checkString(cellData).length() > 0) {
								item.setFullName(cellData);
							}
							break;
						case 4:
							// Contact Designation
							if (StringUtils.checkString(cellData).length() > 0) {
								item.setDesignation(cellData);
							}
							break;

						case 5:
							// Contact mobile number
							if (StringUtils.checkString(cellData).length() > 0) {
								item.setMobileNumber(cellData);
							}
							break;
						case 6:
							// CONTACT NUMBER
							item.setCompanyContactNumber(StringUtils.checkString(cellData));
							break;

						case 7:
							// COMMUNICATION EMAIL
							item.setLoginEmail(StringUtils.checkString(cellData));
							break;

						case 8:
							// Company Fax Number
							item.setFaxNumber(StringUtils.checkString(cellData));
							break;

						case 9:
							// Industry Category
							IndustryCategory ic = new IndustryCategory();
							ic.setCode(StringUtils.checkString(cellData));
							item.setIndustryCategory(ic);
							break;

						case 10:
							// Product Category
							ProductCategory productcategory = new ProductCategory();
							productcategory.setProductCode(StringUtils.checkString(cellData));
							item.setProductCategory(productcategory);
							break;

						case 11:
							// Vendor Code
							item.setVendorCode(StringUtils.checkString(cellData));
							break;

						case 12:
							// Status
							item.setFavSupplierStatus(StringUtils.checkString(cellData));
							break;
						case 13:
							// communication mail
							item.setCommunicationEmail((StringUtils.checkString(cellData)));
							break;
						case 14:
							// Ratings
							if (StringUtils.checkString(cellData).length() > 0) {
								item.setFavSupplierRatings(new BigDecimal(cellData));
							}
							break;
						case 15:
							// Supplier Tags
							SupplierTags supplierTags = new SupplierTags();
							supplierTags.setSupplierTags(StringUtils.checkString(cellData));
							item.setSupplierTags(supplierTags);
							break;
						}

					}
					list.add(item);

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
