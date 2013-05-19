/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2007 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.exception.CRFReadingException;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * SpreadSheetTable, an abstract superclass of spreadsheet classes used in OpenClinica. by Tom Hickerson, May 25 2007
 * 
 * @author thickerson
 * 
 */
public interface SpreadSheetTable {
	public NewCRFBean toNewCRF(javax.sql.DataSource ds, ResourceBundle bundle) throws IOException, CRFReadingException;

	public void setCrfId(int id);
	// was abstract, tbh now is more like a 'cover class'
	/*
	 * private POIFSFileSystem fs = null;
	 * 
	 * private UserAccountBean ub = null;
	 * 
	 * private String versionName = null;
	 * 
	 * private SpreadSheetTableClassic sstc = null;
	 * 
	 * private SpreadSheetTableRepeating sstr = null;
	 * 
	 * private int crfId = 0;
	 * 
	 * private String crfName = "";
	 * 
	 * private String versionIdString = "";
	 * 
	 * private boolean isRepeating = false;
	 * 
	 * protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	 * 
	 * public SpreadSheetTable(FileInputStream parseStream, UserAccountBean ub, String versionName) throws IOException {
	 * logger.info("entering sst..."); this.fs = new POIFSFileSystem(parseStream); this.ub = ub; this.versionName =
	 * versionName; //set crfId here? tbh logger.info("did we make it thus far?"); //determine which version of SST we
	 * are going to use. //currently we have Classic and Repeating. //tbh, 05/2007
	 * 
	 * //currently, look up the worksheets in a table, //if 'group' is one of the worksheets, //send to Repeating
	 * //otherwise, to Classic //tbh, 05/2007 HSSFWorkbook wb = new HSSFWorkbook(fs); int numSheets =
	 * wb.getNumberOfSheets(); //boolean isRepeating = false; for (int j = 0; j < numSheets; j++) { //HSSFSheet sheet =
	 * wb.getSheetAt(j);//sheetIndex); String sheetName = wb.getSheetName(j); if (sheetName.equalsIgnoreCase("groups"))
	 * { logger.info("found groups, repeating set to true"); isRepeating = true; } } if (isRepeating) { sstr = new
	 * SpreadSheetTableRepeating( parseStream, ub, versionName); logger.info("accessing sstr..."); } else { sstc = new
	 * SpreadSheetTableClassic( parseStream, ub, versionName); logger.info("accessing older version, sstc..."); } //now,
	 * how to get the nib back to the servlet? tbh, 5/25/2007 }
	 * 
	 * public NewCRFBean toNewCRF(javax.sql.DataSource ds)throws IOException { NewCRFBean nib = new
	 * NewCRFBean(ds,crfId); if (isRepeating) {
	 * 
	 * nib = sstr.toNewCRF(ds); } else { nib = sstc.toNewCRF(ds); } return nib; }
	 * 
	 * public void setCrfId(int id) { this.crfId = id; }
	 */
}
