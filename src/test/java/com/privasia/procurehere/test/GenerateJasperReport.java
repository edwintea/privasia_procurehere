/**
 * 
 */
package com.privasia.procurehere.test;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

/**
 * @author ravi
 *
 */
public class GenerateJasperReport {
	/**
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		// Write inside main method-- to generate jrxml to jasper class file.
		try {
			JasperCompileManager.compileReportToFile("/home/ravi/applications/eclipse/workspace/procurehere/src/main/resources/reports/PrSummary.jrxml");
			System.out.println("Done");
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
