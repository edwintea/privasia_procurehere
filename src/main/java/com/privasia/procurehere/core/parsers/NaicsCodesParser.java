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

import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

import jxl.Cell;
import jxl.Sheet;

/**
 * This parser parses the NAICS excel file containing the master data
 * 
 * @author Nitin Otageri
 */
public class NaicsCodesParser extends FileParser<NaicsCodes> {

	private static final Logger LOG = LogManager.getLogger(NaicsCodesParser.class);

	@Override
	public List<NaicsCodes> parse(File file) throws ExcelParseException {

		List<NaicsCodes> list = null;
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
	private List<NaicsCodes> readContents(Sheet sheet) throws ExcelParseException, IOException {

		List<NaicsCodes> list = new ArrayList<NaicsCodes>();
		Cell[] rowData = null;
		int rowCount;
		int columnCount;

		try {

			/** Total No Of Rows in Sheet, will return you no of rows that are occupied with some data **/
			rowCount = sheet.getRows();
			LOG.info("Row count is>>>>>>>>>>>>>>>" + rowCount);

			int startingRow = 0;

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

					NaicsCodes level1 = new NaicsCodes();
					NaicsCodes level2 = new NaicsCodes();
					NaicsCodes level3 = new NaicsCodes();
					NaicsCodes level4 = new NaicsCodes();
					NaicsCodes level5 = new NaicsCodes();
					for (int j = 0; j < columnCount; j++) {
						String cellData = StringUtils.checkString(rowData[j].getContents());
						// If the Name is ending with T, remove it. It just means that this category is applicable in US and Canada
						if(cellData.endsWith("T")) {
							cellData = cellData.substring(0, cellData.length() - 1);
						}
						
						// Skip further columns processing if any one cell is empty.
						if(cellData.length() == 0) {
							break;
						}
						switch (j) {
						case 0:
							// LEVEL 1 CODE
							level1.setCategoryCode(Integer.parseInt(cellData));
							break;
						case 1:
							// LEVEL 1 NAME
							level1.setCategoryName(cellData);
							if(!list.contains(level1)) {
								level1.setLevel(1);
								level1.setOrder(list.size() + 1);
								level1.setChildren(new ArrayList<NaicsCodes>());
								list.add(level1);
							} else {
								// Keep list reference of level1 instead of the new instance.
								level1 = list.get(list.indexOf(level1));
								if(CollectionUtil.isEmpty(level1.getChildren())) {
									level1.setChildren(new ArrayList<NaicsCodes>());
								}
							}
							break;
						case 2:
							// LEVEL 2 CODE
							level2.setCategoryCode(Integer.parseInt("" + level1.getCategoryCode() + cellData));
							break;
						case 3:
							// LEVEL 2 NAME
							level2.setCategoryName(cellData);
							if(!level1.getChildren().contains(level2)) {
								level2.setLevel(2);
								level2.setOrder(level1.getChildren().size() + 1);
								level2.setChildren(new ArrayList<NaicsCodes>());
								level1.getChildren().add(level2);
							} else {
								// Keep list reference of level2 instead of the new instance.
								level2 = level1.getChildren().get(level1.getChildren().indexOf(level2));
								if(CollectionUtil.isEmpty(level2.getChildren())) {
									level2.setChildren(new ArrayList<NaicsCodes>());
								}
							}
							break;
						case 4:
							// LEVEL 3 CODE
							level3.setCategoryCode(Integer.parseInt("" + level2.getCategoryCode() + cellData));
							break;
						case 5:
							// LEVEL 3 NAME
							level3.setCategoryName(cellData);
							if(!level2.getChildren().contains(level3)) {
								level3.setLevel(3);
								level3.setOrder(level2.getChildren().size() + 1);
								level3.setChildren(new ArrayList<NaicsCodes>());
								level2.getChildren().add(level3);
							} else {
								// Keep list reference of level3 instead of the new instance.
								level3 = level2.getChildren().get(level2.getChildren().indexOf(level3));
								if(CollectionUtil.isEmpty(level3.getChildren())) {
									level3.setChildren(new ArrayList<NaicsCodes>());
								}
							}
							break;
						case 6:
							// LEVEL 4 CODE
							level4.setCategoryCode(Integer.parseInt("" + level3.getCategoryCode() + cellData));
							break;
						case 7:
							// LEVEL 4 NAME
							level4.setCategoryName(cellData);
							if(!level3.getChildren().contains(level4)) {
								level4.setLevel(4);
								level4.setOrder(level3.getChildren().size() + 1);
								level4.setChildren(new ArrayList<NaicsCodes>());
								level3.getChildren().add(level4);
							} else {
								// Keep list reference of level4 instead of the new instance.
								level4 = level3.getChildren().get(level3.getChildren().indexOf(level4));
								if(CollectionUtil.isEmpty(level4.getChildren())) {
									level4.setChildren(new ArrayList<NaicsCodes>());
								}
							}
							break;
						case 8:
							// LEVEL 5 CODE
							level5.setCategoryCode(Integer.parseInt("" + level4.getCategoryCode() + cellData));
							break;
						case 9:
							// LEVEL 5 NAME
							level5.setCategoryName(cellData);
							if(!level4.getChildren().contains(level5)) {
								if(level4.getChildren() == null) {
									level4.setChildren(new ArrayList<NaicsCodes>());
								}
								level5.setLevel(5);
								level5.setOrder(level4.getChildren().size() + 1);
								level5.setChildren(new ArrayList<NaicsCodes>());
								level4.getChildren().add(level5);
							} else {
								// Keep list reference of level5 instead of the new instance.
								level5 = level4.getChildren().get(level4.getChildren().indexOf(level5));
								if(CollectionUtil.isEmpty(level5.getChildren())) {
									level5.setChildren(new ArrayList<NaicsCodes>());
								}
							}
							break;
						default:
							break;
						}
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

}
