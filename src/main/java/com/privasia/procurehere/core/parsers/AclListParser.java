/**
 * 
 */
package com.privasia.procurehere.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.privasia.procurehere.core.entity.AccessRights;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.StringUtils;

import jxl.Cell;
import jxl.Sheet;

/**
 * This parser parses the ACL List excel file containing the master data
 * 
 * @author Nitin Otageri
 */
public class AclListParser extends FileParser<AccessRights> {

	private static final Logger LOG = LogManager.getLogger(AclListParser.class);

	@Override
	public List<AccessRights> parse(File file) throws ExcelParseException {

		List<AccessRights> list = null;
		try {
			Sheet sheet = processExcel(file);
			list = readContents(sheet);
			LOG.debug("Acl List Records : " + list.size());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ExcelParseException("Error parsing file : " + e.getMessage(), file.getName());
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
	 * Reading Excel File contents.
	 * 
	 * @param sheet the Sheet to process
	 * @return list containing the records in the excel file
	 * @throws ExcelParseException if error while parsing the excel file
	 * @throws IOException if any IO exception
	 */
	private List<AccessRights> readContents(Sheet sheet) throws ExcelParseException, IOException {

		List<AccessRights> list = new ArrayList<AccessRights>();
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
						if (rowData[0].getContents().trim().equals("ACL_VALUE")) {
							headingFound = true;
							break;
						}
					}
				}
			} else {
				throw new ExcelParseException("Empty excel file provided.", "");
			}

			if (!headingFound) {
				throw new ExcelParseException("Invalid excel file format. Could not find 'Date' as column heading in any row of the file at 1st column to begin data processing.", "");
			}

			/*
			 * Validate the column titles (1st row) and sequence in the excel file.
			 */
			rowData = sheet.getRow(startingRow);
			columnCount = rowData.length;
			if (rowData.length > 0 && rowData[0].getContents().trim().length() != 0) {
				for (int j = 0; j < columnCount; j++) {
					String colTitle = rowData[j].getContents().trim();
					switch (j) {
					case 0:
						if (!colTitle.equalsIgnoreCase("ACL_VALUE"))
							throw new ExcelParseException("Acl List excel file must contain 'ACL_VALUE' at column " + (j + 1), "");
						break;
					case 1:
						if (!colTitle.equalsIgnoreCase("ACL_NAME"))
							throw new ExcelParseException("Acl List excel file must contain 'ACL_NAME' at column " + (j + 1), "");
						break;
					case 2:
						if (!colTitle.equalsIgnoreCase("ACL_PARENT"))
							throw new ExcelParseException("Acl List excel file must contain 'ACL_PARENT' at column " + (j + 1), "");
						break;
					case 3:
						if (!colTitle.equalsIgnoreCase("FOR_OWNER"))
							throw new ExcelParseException("Acl List excel file must contain 'FOR_OWNER' at column " + (j + 1), "");
						break;
					case 4:
						if (!colTitle.equalsIgnoreCase("FOR_SUPPLIER"))
							throw new ExcelParseException("Acl List excel file must contain 'FOR_SUPPLIER' at column " + (j + 1), "");
						break;
					case 5:
						if (!colTitle.equalsIgnoreCase("FOR_BUYER"))
							throw new ExcelParseException("Acl List excel file must contain 'FOR_BUYER' at column " + (j + 1), "");
						break;
					case 6:
						if (!colTitle.equalsIgnoreCase("FOR_FINANCE"))
							throw new ExcelParseException("Acl List excel file must contain 'FOR_FINANCE' at column " + (j + 1), "");
						break;
					default:
						break;
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
				columnCount = rowData.length;

				if (rowData.length > 0 && rowData[0].getContents().trim().length() != 0) {

					// Break at the first occurrence of empty data at column 0;
					if (rowData[0].getContents() == null || (rowData[0].getContents().trim().equals("")))
						break;

					AccessRights acl = new AccessRights();
					for (int j = 0; j < columnCount; j++) {
						String cellData = StringUtils.checkString(rowData[j].getContents());
						switch (j) {
						case 0:
							// ACL VALUE
							acl.setAclValue(StringUtils.checkString(cellData));
							break;
						case 1:
							// ACL Name
							acl.setAclName(cellData);
							break;
						case 2:
							// ACL Parent
							if (StringUtils.checkString(cellData).length() > 0) {
								AccessRights parent = new AccessRights();
								parent.setAclValue(StringUtils.checkString(cellData));
								acl.setParent(parent);
							}
							break;
						case 3:
							// FOR OWNER
							if (StringUtils.checkString(cellData).length() > 0) {
								acl.setOwner(StringUtils.checkString(cellData).equals("1") ? true : false);
							}
							break;
						case 4:
							// FOR SUPPLIER
							if (StringUtils.checkString(cellData).length() > 0) {
								acl.setSupplier(StringUtils.checkString(cellData).equals("1") ? true : false);
							}
							break;
						case 5:
							// FOR BUYER
							if (StringUtils.checkString(cellData).length() > 0) {
								acl.setBuyer(StringUtils.checkString(cellData).equals("1") ? true : false);
							}
							break;
							
						case 6:
							// FOR Finance
							if (StringUtils.checkString(cellData).length() > 0) {
								//LOG.info("data....... in parser....."+StringUtils.checkString(cellData));
								
								acl.setFinanceCompany(StringUtils.checkString(cellData).equals("1") ? true : false);
								//LOG.info("data....... in in pbject....."+acl.getFinanceCompany());
								
							}
							break;
							
						case 7:
							// ORDER - May be first level or second or ....
							if (cellData.length() > 0) {
								acl.setAclOrder(Integer.parseInt(cellData));
							}
							break;
						case 8:
							// ORDER - May be first level or second or ....
							if (cellData.length() > 0) {
								acl.setAclOrder(Integer.parseInt(cellData));
							}
							break;
						case 9:
							// ORDER - May be first level or second or ....
							if (cellData.length() > 0) {
								acl.setAclOrder(Integer.parseInt(cellData));
							}
							break;
						case 10:
							// ORDER - May be first level or second or ....
							if (cellData.length() > 0) {
								acl.setAclOrder(Integer.parseInt(cellData));
							}
							break;

						default:
							break;
						}
					}

					list.add(acl);
				} else
					// Break the loop at first empty row after row number 12 to assume end of records.
					// in case if the user mistakenly introduced empty row, we will not process the rows after such
					// empty row.
					break;
			} // End For

		} catch (ExcelParseException e) {
			LOG.error("Error : " + e.getMessage(), e);
			throw new ExcelParseException(e.getMessage(), null);
		}
		return list;
	}

}
