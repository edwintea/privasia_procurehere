/**
 * 
 */
package com.privasia.procurehere.core.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.privasia.procurehere.core.exceptions.ExcelParseException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

/**
 * @author Nitin
 * @author Ravi
 * @param <E> the instance type to be returned after parsing.
 */
public abstract class FileParser<E> {

	private static final Logger LOG = LogManager.getLogger(FileParser.class);

	protected Workbook workbook = null;
	protected FileInputStream fs = null;

	/**
	 * @param file the file
	 * @return the list of entities after parsing the file.
	 * @throws ExcelParseException if any error during parsing.
	 */
	public abstract List<E> parse(File file) throws ExcelParseException;

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
			throw new ExcelParseException("Error opening file for reading. " + e.getMessage(), file.getName());
		}
		return sheet;
	}
	
 
}
