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

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.StringUtils;

import jxl.Cell;
import jxl.Sheet;

/**
 * This parser parses the Country List excel file containing the master data
 * 
 * @author Nitin Otageri
 */
public class CountryParser extends FileParser<Country> {

	private static final Logger LOG = LogManager.getLogger(CountryParser.class);

	@Override
	public List<Country> parse(File file) throws ExcelParseException {

		List<Country> list = null;
		try {
			Sheet sheet = processExcel(file);
			list = readContents(sheet);
			LOG.debug("Country List Records : " + list.size());
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
	private List<Country> readContents(Sheet sheet) throws ExcelParseException, IOException {

		List<Country> list = new ArrayList<Country>();
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
						if (rowData[0].getContents().trim().equals("Name")) {
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
						if (!colTitle.equalsIgnoreCase("Name"))
							throw new ExcelParseException("Country List excel file must contain 'Name' at column " + (j + 1), "");
						break;
					case 1:
						if (!colTitle.equalsIgnoreCase("Code"))
							throw new ExcelParseException("Country List excel file must contain 'Code' at column " + (j + 1), "");
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

					Country obj = new Country();
					for (int j = 0; j < columnCount; j++) {
						String cellData = StringUtils.checkString(rowData[j].getContents()).replaceAll("[^\\x00-\\x7f]", "").replaceAll("\\p{Cc}", "");
						switch (j) {
						case 0:
							// Country Name
							obj.setCountryName(cellData);
							break;
						case 1:
							// Country Code
							obj.setCountryCode(cellData);
							break;
						default:
							break;
						}
					}

					list.add(obj);
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
