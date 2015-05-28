/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.bean;

import java.io.File;
import java.math.BigDecimal;

import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SystemStatusBean.
 */
public class SystemStatusBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemStatusBean.class);

	public static final String OK = "OK";
	public static final String ID = "id";
	public static final String MB = " Mb";
	public static final String KB = " Kb";
	public static final String OID = "oid";
	public static final String NAME = "name";
	public static final String STUDY = "study";
	public static final String BYTES = " Bytes";
	public static final String STUDY_ID = "studyId";
	public static final String DATA_SETS = "datasets";
	public static final String FILE_PATH = "filePath";
	public static final String STUDY_OID = "study_oid";
	public static final String EVENT_CRF_DATA = "Event_CRF_Data";
	public static final String DATA_SETS_DATA_SETS = "datasetsdatasets";
	public static final String SCHEDULED_DATA_IMPORT = "scheduled_data_import";
	public static final String PRINT = "print";
	public static final String DCF = "dcf";
	public static final String REPORTS = "crfReport";
	public static final String CASEBOOKS = "Casebooks";

	public static final int INT_0 = 0;
	public static final int INT_1024 = 1024;

	private String parameterId;
	private int studyId;
	private String studyOid;
	private String studyName;
	private int assignedUsers;
	private int crfSections;
	private long dataImportSize;
	private long dataExportSize;
	private long fileAttachmentsSize;
	private long dcfSize;
	private long crfReportsSize;
	private long casebooksSize;
	private long printSize;
	private String dataImportSizeValue;
	private String dataExportSizeValue;
	private String fileAttachmentsSizeValue;
	private String dcfSizeValue;
	private String crfReportsSizeValue;
	private String casebooksSizeValue;
	private String printSizeValue;

	/**
	 * System Status.
	 *
	 * @param parameterId String
	 * @param studyDao StudyDAO
	 * @param userAccountDao UserAccountDAO
	 * @param itemFormMetadataDao ItemFormMetadataDAO
	 */
	public SystemStatusBean(String parameterId, StudyDAO studyDao, UserAccountDAO userAccountDao,
			ItemFormMetadataDAO itemFormMetadataDao) {
		try {
			if (parameterId != null) {
				this.parameterId = parameterId;
				StudyBean studyBean = (StudyBean) studyDao.findByPK(Integer.parseInt(parameterId));
				if (studyBean != null && studyBean.getId() > 0 && studyBean.getParentStudyId() == 0) {
					studyId = studyBean.getId();
					studyOid = studyBean.getOid();
					studyName = studyBean.getName();
					assignedUsers = userAccountDao.getUsersAssignedMetric(studyId);
					crfSections = itemFormMetadataDao.getCrfSectionsMetric(studyId);

					String baseDir = CoreResources.getField(FILE_PATH);

					dataImportSize = getDirSize(baseDir.concat(File.separator).concat(SCHEDULED_DATA_IMPORT)
							.concat(File.separator).concat(studyBean.getOid()));
					dataImportSize += getDirSize(baseDir.concat(File.separator).concat(EVENT_CRF_DATA)
							.concat(File.separator).concat(studyBean.getOid()));
					dataImportSizeValue = convertSize(dataImportSize);

					dataExportSize = getDirSize(baseDir.concat(File.separator).concat(DATA_SETS_DATA_SETS)
							.concat(File.separator).concat(studyBean.getOid()));
					dataExportSize += getDirSize(baseDir.concat(File.separator).concat(DATA_SETS)
							.concat(File.separator).concat(studyBean.getOid()));
					dataExportSizeValue = convertSize(dataExportSize);

					fileAttachmentsSize = getDirSize(Utils.getAttachedFilePath(studyBean));
					fileAttachmentsSizeValue = convertSize(fileAttachmentsSize);

					dcfSize = getDirSize(baseDir.concat(File.separator).concat(PRINT).concat(File.separator).concat(DCF)
							.concat(File.separator).concat(studyBean.getOid()));
					dcfSizeValue = convertSize(dcfSize);

					crfReportsSize = getDirSize(baseDir.concat(File.separator).concat(PRINT).concat(File.separator).concat(REPORTS)
							.concat(File.separator).concat(studyBean.getOid()));
					crfReportsSizeValue = convertSize(crfReportsSize);

					casebooksSize = getDirSize(baseDir.concat(File.separator).concat(PRINT).concat(File.separator).concat(CASEBOOKS)
							.concat(File.separator).concat(studyBean.getOid()));
					casebooksSizeValue = convertSize(casebooksSize);

					printSize = casebooksSize + crfReportsSize + dcfSize;
					printSizeValue = convertSize(printSize);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
	}
	public long getFileAttachmentsSize() {
		return fileAttachmentsSize;
	}

	public String getParameterId() {
		return parameterId;
	}

	public int getStudyId() {
		return studyId;
	}

	public String getStudyOid() {
		return studyOid;
	}

	public String getStudyName() {
		return studyName;
	}

	public int getAssignedUsers() {
		return assignedUsers;
	}

	public int getCrfSections() {
		return crfSections;
	}

	public long getDataImportSize() {
		return dataImportSize;
	}

	public long getDataExportSize() {
		return dataExportSize;
	}

	public String getDataImportSizeValue() {
		return dataImportSizeValue;
	}

	public String getDataExportSizeValue() {
		return dataExportSizeValue;
	}

	public String getFileAttachmentsSizeValue() {
		return fileAttachmentsSizeValue;
	}

	public String getCasebooksSizeValue() {
		return casebooksSizeValue;
	}

	public long getDcfSize() {
		return dcfSize;
	}

	public long getCrfReportsSize() {
		return crfReportsSize;
	}

	public long getCasebooksSize() {
		return casebooksSize;
	}

	public String getDcfSizeValue() {
		return dcfSizeValue;
	}

	public String getCrfReportsSizeValue() {
		return crfReportsSizeValue;
	}

	public String getPrintSizeValue() {
		return printSizeValue;
	}

	public long getPrintSize() {
		return printSize;
	}

	private long getDirSize(String dirPath) {
		File dir = new File(dirPath);
		return dir.exists() ? FileUtils.sizeOfDirectory(dir) : 0;
	}

	private String convertSize(long size) {
		float value;
		String measure;
		if (size > INT_1024 * INT_1024) {
			measure = MB;
			value = size / (INT_1024 * INT_1024);
		} else if (size > INT_1024) {
			measure = KB;
			value = size / INT_1024;
		} else {
			value = size;
			measure = BYTES;
		}
		return new BigDecimal(value).setScale(INT_0, BigDecimal.ROUND_HALF_EVEN).toString().concat(measure);
	}
}
