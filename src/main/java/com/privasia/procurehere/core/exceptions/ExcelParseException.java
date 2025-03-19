/**
 * 
 */
package com.privasia.procurehere.core.exceptions;

/**
 * @author Ravi
 */
public class ExcelParseException extends Exception {

	private static final long serialVersionUID = -7472808548876968062L;

	String fileName;

	public ExcelParseException(String message, String fileName) {
		super(message);
		this.fileName = fileName;
	}

	public ExcelParseException(String message, String fileName, Throwable throwable) {
		super(message, throwable);
		this.fileName = fileName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}



}
