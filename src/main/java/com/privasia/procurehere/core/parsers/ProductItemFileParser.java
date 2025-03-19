/**
 * 
 */
package com.privasia.procurehere.core.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.ProductItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author teja
 * @param <E> the instance type to be returned after parsing.
 */
public class ProductItemFileParser<E extends ProductItem> {

	private static final Logger LOG = LogManager.getLogger(ProductItemFileParser.class);

	protected Workbook workbook = null;
	protected FileInputStream fs = null;

	protected Class<E> clazz;

	public ProductItemFileParser(Class<E> clazz) {
		this.clazz = clazz;
	}

	// TODO Check Sl no.(item) Sequence

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
			LOG.info("Cost Center List Records : " + list.size());
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
			/**
			 * Total No Of Rows in Sheet, will return you no of rows that are occupied with some data
			 **/
			rowCount = sheet.getLastRowNum() + 1;
			LOG.info("Row count is>>>>>>>>>>>>>>>" + rowCount);

			int startingRow = 0;
			boolean headingFound = false;
			// Skip to the line containing the column headings....
			if (rowCount > 0) {
				for (startingRow = 0; startingRow < rowCount; startingRow++) {
					rowData = sheet.getRow(startingRow);
					if (rowData.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
						if (rowData != null && rowData.getCell(0) != null && rowData.getCell(0).getStringCellValue().length() > 0) {
							if (rowData.getCell(0).getStringCellValue().trim().equals("INTERFACE CODE")) {
								headingFound = true;
								break;
							}
						}
					}
				}
			} else {
				throw new ExcelParseException("Empty excel file provided.", "");
			}

			if (!headingFound) {
				throw new ExcelParseException("Invalid excel file format. Could not find 'INTERFACE CODE' as column heading in any row of the file at 1st column to begin data processing.", "");
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
					if (rowData.getCell(j) != null) {
						String colTitle = rowData.getCell(j).getStringCellValue().trim();
						if (j < 17 && !colTitle.equalsIgnoreCase(Global.PRODUCT_ITEM_EXCEL_COLUMNS[j])) {
							throw new ExcelParseException("Product item List excel file must contain '" + Global.PRODUCT_ITEM_EXCEL_COLUMNS[j] + "' at column " + (j + 1), "");
						}
						if (j >= 17) {
							columnTitle.add(colTitle);
						}
					}
				}
			}

			// Skip to the data row (startingRow will be the heading column)
			startingRow++;

			/** Start reading data from 2nd row, as per standard **/
			for (int i = startingRow; i < rowCount; i++) {

				/** Get Individual Row **/
				rowData = sheet.getRow(i);

				/** Total Total No Of Columns in Sheet **/
				// columnCount = rowData.length;
				if (rowData != null) {
					columnCount = rowData.getLastCellNum();
				}
				// Break at the first occurrence of empty data at column 0;
				// if (rowData.getCell(0) == null || (rowData.getCell(0) != null
				// &&
				// rowData.getCell(0).getStringCellValue() == null) ||
				// (rowData.getCell(0) != null &&
				// rowData.getCell(0).getStringCellValue() != null &&
				// rowData.getCell(0).getStringCellValue().trim().equals("")))
				// break;
				if (columnCount > 0 && rowData != null && rowData.getCell(0) != null) {
					Cell cell = rowData.getCell(0);
					String data = getCellData(cell);

					// Break at the first occurrence of empty data at column 0;
					if (StringUtils.checkString(data).length() == 0) {
						throw new ExcelParseException("Interface code not valid at row number " + (i + 1), "");
					}

					E item = clazz.newInstance();
					// item.setColumnTitles(columnTitle);
					for (int j = 0; j < columnCount; j++) {
						// LOG.info("columnCount :" + columnCount);

						if (j >= rowData.getLastCellNum()) {
							break;
						}

						String cellData = getCellData(rowData.getCell(j));
						switch (j) {
						/*
						 * case 0: // ID item.setId(StringUtils.checkString(cellData)); break;
						 */
						case 0:
							// PRODUCT CODE
							LOG.info("cellData :" + cellData);
							if (StringUtils.checkString(cellData).length() > 0) {
								item.setInterfaceCode(cellData);
							} else {
								throw new ExcelParseException("Interface code is missing at row number " + (i + 1), "");
							}
							break;
						case 1:
							// PRODUCT NAME
							if (StringUtils.checkString(cellData).length() > 0) {
								item.setProductName(cellData);
							} else {
								throw new ExcelParseException("Product name is missing at row number " + (i + 1), "");
							}
							break;

						case 2:
							// UOM
							if (StringUtils.checkString(cellData).length() > 0) {
								Uom uom = new Uom();
								uom.setUom(StringUtils.checkString(cellData));
								item.setUom(uom);
							} else {
								throw new ExcelParseException("Uom is missing at row number " + (i + 1), "");
							}
							break;

						case 3:
							// product category
							if (StringUtils.checkString(cellData).length() > 0) {
								ProductCategory productCategory = new ProductCategory();
								productCategory.setProductCode(StringUtils.checkString(cellData));
								item.setProductCategory(productCategory);
							} else {
								throw new ExcelParseException("Product category is missing at row number " + (i + 1), "");
							}
							break;
						case 4:
							// fav supplier
							FavouriteSupplier favoriteSupplier = null;
							if (StringUtils.checkString(cellData).length() > 0 && cellData.equalsIgnoreCase("None")) {
								item.setFavoriteSupplier(favoriteSupplier);
							} else if (StringUtils.checkString(cellData).length() > 0) {
								favoriteSupplier = new FavouriteSupplier();
								Supplier supp = new Supplier();
								supp.setCompanyName(cellData);
								LOG.info("fav supp in file parse :" + cellData);
								favoriteSupplier.setSupplier(supp);
							}
							item.setFavoriteSupplier(favoriteSupplier);
							break;
						case 5:
							// Unit Price
							if (StringUtils.checkString(cellData).length() > 0) {
								LOG.info("cellData :" + cellData);
								try {
									BigDecimal bd = new BigDecimal(cellData);
									item.setUnitPrice(bd);
								} catch (Exception e) {
									LOG.error("Error while parsing unit price into big decimal :" + e.getMessage(), e);
									throw new ExcelParseException("Invalid \'" + cellData + "\' Unit price. It must be a  number at row number " + (i + 1), null);
								}
							}
							break;

						case 6:
							// Tax
							if (StringUtils.checkString(cellData).length() > 0) {
								LOG.info("cellData :" + cellData);
								try {
									BigDecimal bd = new BigDecimal(cellData);
									item.setTax(bd);
								} catch (Exception e) {
									LOG.error("Error while parsing unit price into big decimal :" + e.getMessage(), e);
									throw new ExcelParseException("Invalid \'" + cellData + "\'Tax.It must be  a  number at row number " + (i + 1), null);
								}
							} else {
								throw new ExcelParseException("Tax is missing at row number " + (i + 1), "");
							}
							break;
						case 7:
							// ITEM TYPE
							item.setProductItemType(StringUtils.checkString(cellData).length() > 0 ? ProductItemType.fromString(cellData) : null);
							break;
						case 8:
							// GL CODE
							item.setGlCode(StringUtils.checkString(cellData));
							break;

						case 9:
							// UNSPSC CODE
							item.setUnspscCode(StringUtils.checkString(cellData));
							break;
						case 10:
							// STATUS
							if (Status.ACTIVE.toString().equals(cellData)) {
								item.setStatus(Status.ACTIVE);
							} else if (Status.INACTIVE.toString().equals(cellData)) {
								item.setStatus(Status.INACTIVE);
							} else {
								throw new ExcelParseException("Invalid STATUS '" + cellData + "' specified in the excel file", null);
							}
							break;

						case 11:
							// REMARKS
							item.setRemarks(StringUtils.checkString(cellData));
							break;
						case 12:
							// Historical Pricing RefNo
							item.setHistoricPricingRefNo(StringUtils.checkString(cellData));
							break;
						case 13:
							// Purchase Group Code
							item.setPurchaseGroupCode(StringUtils.checkString(cellData));
							break;

						case 14:
							// Brand
							item.setBrand(StringUtils.checkString(cellData));
							break;

						case 15:
							// Start Date
							if (StringUtils.checkString(cellData).length() > 0) {
								try {
									// if default template upload date format will be
									Date date1 = new SimpleDateFormat("dd/MM/yyyy"/* "E MMM dd HH:mm:ss Z yyyy" */).parse(cellData);
									item.setStartDate(date1);
								} catch (Exception e) {
									try {
										// if manual template upload date format will be
										Date date1 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(cellData);
										item.setStartDate(date1);
									} catch (Exception ee) {
										throw new ExcelParseException("Invalid Start Date '" + cellData + "' specified in the excel file. Date format will be dd/MM/yyyy", null);
									}
								}
							}
							break;

						case 16:
							// End Date
							if (StringUtils.checkString(cellData).length() > 0) {
								try {
									// if default template upload date format will be
									Date date2 = new SimpleDateFormat("dd/MM/yyyy"/* "E MMM dd HH:mm:ss Z yyyy" */).parse(cellData);
									item.setValidityDate(date2);
								} catch (Exception e) {
									try {
										// if manual template upload date format will be
										Date date2 = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(cellData);
										item.setValidityDate(date2);
									} catch (Exception eee) {
										throw new ExcelParseException("Invalid Validity Date '" + cellData + "' specified in the excel file. Date format will be dd/MM/yyyy", null);
									}

								}
							}

							// Date date2=new SimpleDateFormat("dd/MM/yyyy"/*"E MMM dd HH:mm:ss Z
							// yyyy"*/).parse(cellData);
							// item.setValidityDate(date2);
							break;

						case 17:
							// Brand
							item.setContractReferenceNumber(StringUtils.checkString(cellData));
							break;
						}

					}
					list.add(item);
				} else
					// Break the loop at first empty row after row number 12 to
					// assume end of records.
					// in case if the user mistakenly introduced empty row, we
					// will not process the rows after such
					// empty row.
					break;

			} // End For
		} catch (

		Exception e) {
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
