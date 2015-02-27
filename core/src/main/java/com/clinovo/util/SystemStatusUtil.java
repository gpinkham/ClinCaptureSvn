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

package com.clinovo.util;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clinovo.bean.SystemStatusBean;

/**
 * SystemStatusUtil.
 */
public final class SystemStatusUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemStatusUtil.class);

	public static final String OK = "OK";
	public static final String ID = "id";
	public static final String OID = "oid";
	public static final String NAME = "name";
	public static final String STUDY = "study";
	public static final String STUDIES = "studies";
	public static final String STORAGE = "storage";
	public static final String STUDY_ID = "studyId";
	public static final String REAL_SIZE = "realSize";
	public static final String STUDY_OID = "study_oid";
	public static final String DATA_IMPORT = "dataImport";
	public static final String DATA_EXPORT = "dataExport";
	public static final String CRF_SECTIONS = "crfSections";
	public static final String ASSIGNED_USERS = "assignedUsers";
	public static final String FILE_ATTACHMENTS = "fileAttachments";

	private SystemStatusUtil() {
	}

	public static String getStatisticsForStudy(SystemStatusBean systemStatusBean) {
		StringBuilder sb = new StringBuilder("\n        ");
		try {
			if (systemStatusBean.getParameterId() == null) {
				sb.append("Please, specify the study id to display study statistics.");
			} else {
				if (systemStatusBean.getStudyId() > 0) {
					sb.append("\n      - Study: ").append(systemStatusBean.getStudyName());
					sb.append("\n      - Users Assigned: ").append(systemStatusBean.getAssignedUsers());
					sb.append("\n      - CRF Sections: ").append(systemStatusBean.getCrfSections());

					sb.append("\n      - Consumed storage: ");
					sb.append("\n            - data import: ").append(systemStatusBean.getDataImportSizeValue());
					sb.append("\n            - data export: ").append(systemStatusBean.getDataExportSizeValue());
					sb.append("\n            - file attachments inside the CRFs: ").append(
							systemStatusBean.getFileAttachmentsSizeValue());
					sb.append("\n");
				} else {
					sb.append("Parent study is not found.");
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return sb.toString();
	}

	public static String getXmlStatisticsForStudy(SystemStatusBean systemStatusBean) {
		String result = "";
		try {
			if (systemStatusBean.getStudyId() > 0) {
				DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

				Document document = documentBuilder.newDocument();
				Element studies = document.createElement(STUDIES);
				document.appendChild(studies);

				Element study = document.createElement(STUDY);
				studies.appendChild(study);

				Element id = document.createElement(ID);
				study.appendChild(id);
				id.setTextContent(Integer.toString(systemStatusBean.getStudyId()));

				Element oid = document.createElement(OID);
				study.appendChild(oid);
				oid.setTextContent(systemStatusBean.getStudyOid());

				Element name = document.createElement(NAME);
				study.appendChild(name);
				name.setTextContent(systemStatusBean.getStudyName());

				Element assignedUsers = document.createElement(ASSIGNED_USERS);
				study.appendChild(assignedUsers);
				assignedUsers.setTextContent(Integer.toString(systemStatusBean.getAssignedUsers()));

				Element crfSections = document.createElement(CRF_SECTIONS);
				study.appendChild(crfSections);
				crfSections.setTextContent(Integer.toString(systemStatusBean.getCrfSections()));

				Element storage = document.createElement(STORAGE);
				study.appendChild(storage);

				Element dataImport = document.createElement(DATA_IMPORT);
				storage.appendChild(dataImport);
				dataImport.setTextContent(systemStatusBean.getDataImportSizeValue());
				dataImport.setAttribute(REAL_SIZE, Long.toString(systemStatusBean.getDataImportSize()));

				Element dataExport = document.createElement(DATA_EXPORT);
				storage.appendChild(dataExport);
				dataExport.setTextContent(systemStatusBean.getDataExportSizeValue());
				dataExport.setAttribute(REAL_SIZE, Long.toString(systemStatusBean.getDataExportSize()));

				Element fileAttachments = document.createElement(FILE_ATTACHMENTS);
				storage.appendChild(fileAttachments);
				fileAttachments.setTextContent(systemStatusBean.getFileAttachmentsSizeValue());
				fileAttachments.setAttribute(REAL_SIZE, Long.toString(systemStatusBean.getFileAttachmentsSize()));

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult streamResult = new StreamResult(new StringWriter());
				transformer.transform(source, streamResult);

				result = streamResult.getWriter().toString();
			}
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
		return result;
	}
}
