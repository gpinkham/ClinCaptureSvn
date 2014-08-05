/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.bean.extract;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletOutputStream;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class converts or serializes DiscrepancyNoteBeans to Strings or iText-related classes so that they can be
 * compiled into a file and downloaded to the user. This is a convenience class with a number of different methods for
 * serializing beans to Strings.
 *
 * @author Bruce W. Perry
 */
public class DownloadDiscrepancyNote implements DownLoadBean {

	public static final String CSV = "text/plain";
	public static final String PDF = "application/pdf";

	private static final int TABLE_PADDING_PDF = 4;
	private static final int TABLE_SPACING_PDF = 4;

	private static final int STUDY_IDENTIFIER_FONT_SIZE_PDF = 18;
	private static final int PARENT_DN_SECTION_FONT_SIZE_PDF = 14;

	public static final Map<Integer, String> RESOLUTION_STATUS_MAP = new HashMap<Integer, String>();
	static {

		int index = 1;
		RESOLUTION_STATUS_MAP.put(index++, "New");
		RESOLUTION_STATUS_MAP.put(index++, "Updated");
		RESOLUTION_STATUS_MAP.put(index++, "Resolution Proposed");
		RESOLUTION_STATUS_MAP.put(index++, "Closed");
		RESOLUTION_STATUS_MAP.put(index, "Not Applicable");
	}

	public DownloadDiscrepancyNote() {
	}

	public void downLoad(EntityBean bean, String format, OutputStream stream) {

		if (bean == null || stream == null
				|| !(bean instanceof org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean)) {
			throw new IllegalStateException(
					"An invalid parameter was passed to the DownloadDiscrepancyNote.downLoad method.");
		}
		DiscrepancyNoteBean discNBean = (DiscrepancyNoteBean) bean;
		// This must be a ServletOutputStream for our purposes
		ServletOutputStream servletStream = (ServletOutputStream) stream;

		try {
			if (CSV.equalsIgnoreCase(format)) {
				servletStream.print(serializeToString(discNBean, false, 0));
			} else {

				// Create PDF version
				serializeToPDF(discNBean, servletStream);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				servletStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void downLoad(List<EntityBean> listOfBeans, String format, OutputStream stream) {

		// The List must be of DiscrepancyNoteBeans
		if (listOfBeans == null) {
			return;
		}
		StringBuilder allContent = new StringBuilder();
		String singleBeanContent;

		for (EntityBean discNoteBean : listOfBeans) {
			if (!(discNoteBean instanceof DiscrepancyNoteBean)) {
				return;
			}

			DiscrepancyNoteBean discNBean = (DiscrepancyNoteBean) discNoteBean;
			singleBeanContent = serializeToString(discNBean, false, 0);
			allContent.append(singleBeanContent);
			allContent.append("\n");

		}

		// This must be a ServletOutputStream for our purposes
		ServletOutputStream servletStream = (ServletOutputStream) stream;

		try {
			if (CSV.equalsIgnoreCase(format)) {
				servletStream.print(allContent.toString());
			} else {

				// Create PDF version
				serializeListToPDF(allContent.toString(), servletStream);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (servletStream != null) {
				try {
					servletStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public int getContentLength(EntityBean bean, String format) {

		return serializeToString(bean, false, 0).getBytes().length;
	}

	public int getThreadListContentLength(List<DiscrepancyNoteThread> threadBeans) {

		int totalLength = 0;
		int count = 0;
		int threadCount = 1;

		for (DiscrepancyNoteThread discrepancyNoteThread : threadBeans) {

			for (DiscrepancyNoteBean discNoteBean : discrepancyNoteThread.getLinkedNoteList()) {

				++count;
				// Only count the byte length of a CSV header row for the first DNote; we're only
				// using response.setContentlength for CSV format, because apparently it is not
				// necessary for PDF
				totalLength += serializeToString(discNoteBean, (count == 1), threadCount).getBytes().length;
			}

			threadCount++;
		}
		return totalLength;
	}

	public String serializeToString(EntityBean bean, boolean includeHeaderRow, int threadNumber) {

		DiscrepancyNoteBean discNoteBean = (DiscrepancyNoteBean) bean;
		StringBuilder writer = new StringBuilder("");
		// If includeHeaderRow = true, the first row of the output consists of header names, only
		// for CSV format
		if (includeHeaderRow) {

			writer.append("Study Subject ID").append(",");
			writer.append("Subject Status").append(",");
			writer.append("Study/Site OID").append(",");
			// we're adding a thread number row
			writer.append("Thread ID").append(",");

			writer.append("Note ID").append(",");

			writer.append("Parent Note ID").append(",");

			writer.append("Date Created").append(",");
			writer.append("Date Update").append(",");
			writer.append("Days Open").append(",");
			writer.append("Days Since Updated").append(",");

			if (discNoteBean.getDisType() != null) {
				writer.append("Discrepancy Type").append(",");
			}
			writer.append("Resolution Status").append(",");
			writer.append("Event Name").append(",");
			writer.append("CRF Name").append(",");
			writer.append("CRF Status").append(",");
			writer.append("Entity name").append(",");
			writer.append("Entity value").append(",");
			writer.append("Description").append(",");
			writer.append("Detailed Notes").append(",");
			writer.append("Assigned User").append(",");
			writer.append("Study Id").append("\n");
		}

		// Fields with embedded commas must be
		// delimited with double-quote characters.
		writer.append(escapeQuotesInCSV(discNoteBean.getStudySub().getLabel())).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getStudySub().getStatus().getName())).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getStudy().getOid())).append(",");

		writer.append(escapeQuotesInCSV(threadNumber + "")).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getId() + "")).append(",");

		writer.append(discNoteBean.getParentDnId() > 0 ? discNoteBean.getParentDnId() : "").append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getCreatedDateString() + "")).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getUpdatedDateString() + "")).append(",");

		if (discNoteBean.getParentDnId() == 0) {
			writer.append(escapeQuotesInCSV(discNoteBean.getAge() + "")).append(",");

			String daysSinceUpdated = escapeQuotesInCSV(discNoteBean.getDays() + "");
			writer.append(daysSinceUpdated.equals("0") ? "" : daysSinceUpdated).append(",");
		} else {
			writer.append(",").append(",");
		}

		if (discNoteBean.getDisType() != null) {
			writer.append(escapeQuotesInCSV(discNoteBean.getDisType().getName())).append(",");
		}

		writer.append(escapeQuotesInCSV(RESOLUTION_STATUS_MAP.get(discNoteBean.getResolutionStatusId()) + "")).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getEventName())).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getCrfName())).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getCrfStatus())).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getEntityName())).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getEntityValue())).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getDescription() + "")).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getDetailedNotes() + "")).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getAssignedUser().getName())).append(",");

		writer.append(escapeQuotesInCSV(discNoteBean.getStudyId() + "")).append("\n");

		return writer.toString();

	}

	private void serializeToPDF(EntityBean bean, OutputStream stream) {

		ServletOutputStream servletStream = (ServletOutputStream) stream;
		DiscrepancyNoteBean discNBean = (DiscrepancyNoteBean) bean;
		StringBuilder writer = new StringBuilder();
		writer.append(serializeToString(discNBean, false, 0));

		Document pdfDoc = new Document();

		try {
			PdfWriter.getInstance(pdfDoc, servletStream);
			pdfDoc.open();
			pdfDoc.add(new Paragraph(writer.toString()));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		pdfDoc.close();

	}

	public void serializeListToPDF(String content, OutputStream stream) {

		ServletOutputStream servletStream = (ServletOutputStream) stream;

		Document pdfDoc = new Document();

		try {
			PdfWriter.getInstance(pdfDoc, servletStream);
			pdfDoc.open();
			pdfDoc.add(new Paragraph(content));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		pdfDoc.close();

	}

	public void serializeThreadsToPDF(List<DiscrepancyNoteThread> listOfThreads, OutputStream stream,
			String studyIdentifier) {

		ServletOutputStream servletStream = (ServletOutputStream) stream;

		Document pdfDoc = new Document();

		try {
			PdfWriter.getInstance(pdfDoc, servletStream);
			pdfDoc.open();
			// Create header for the study identifier or name
			if (studyIdentifier != null) {
				HeaderFooter header = new HeaderFooter(new Phrase("Study Identifier: " + studyIdentifier + " pg."),
						true);
				header.setAlignment(Element.ALIGN_CENTER);
				Paragraph para = new Paragraph("Study Identifier: " + studyIdentifier, new Font(Font.HELVETICA,
						STUDY_IDENTIFIER_FONT_SIZE_PDF, Font.BOLD, new Color(0, 0, 0)));
				para.setAlignment(Element.ALIGN_CENTER);
				pdfDoc.setHeader(header);
				pdfDoc.add(para);
			}
			boolean newPage = false;
			for (DiscrepancyNoteThread discNoteThread : listOfThreads) {
				if (discNoteThread != null && !discNoteThread.getLinkedNoteList().isEmpty()) {
					if (newPage) {
						pdfDoc.newPage();
					}
					pdfDoc.add(this.createTableThreadHeader(discNoteThread));

					for (DiscrepancyNoteBean discNoteBean : discNoteThread.getLinkedNoteList()) {
						if (discNoteBean.getParentDnId() > 0) {
							pdfDoc.add(this.createTableFromBean(discNoteBean));
							pdfDoc.add(new Paragraph("\n"));
						}
					}
					newPage = true;
				}
			}

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		pdfDoc.close();

	}

	public void downLoadThreadedDiscBeans(List<DiscrepancyNoteThread> listOfThreadedBeans, String format,
			OutputStream stream, String studyIdentifier) {

		if (listOfThreadedBeans == null) {
			return;
		}
		StringBuilder allContent = new StringBuilder();
		String singleBeanContent;
		int counter = 0;
		int threadCounter = 0;

		if (CSV.equalsIgnoreCase(format)) {
			for (DiscrepancyNoteThread dnThread : listOfThreadedBeans) {
				threadCounter++;
				for (DiscrepancyNoteBean discNoteBean : dnThread.getLinkedNoteList()) {

					++counter;

					singleBeanContent = counter == 1 ? serializeToString(discNoteBean, true, threadCounter)
							: serializeToString(discNoteBean, false, threadCounter);
					allContent.append(singleBeanContent);
				}
			}
		}

		// This must be a ServletOutputStream for our purposes
		ServletOutputStream servletStream = (ServletOutputStream) stream;

		try {
			if (CSV.equalsIgnoreCase(format)) {
				servletStream.print(allContent.toString());
			} else {

				this.serializeThreadsToPDF(listOfThreadedBeans, servletStream, studyIdentifier);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (servletStream != null) {
				try {
					servletStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private String escapeQuotesInCSV(String csvValue) {

		if (csvValue == null) {
			return "";
		}

		if (csvValue.contains("\u2018")) {
			csvValue = csvValue.replaceAll("\u2018", "'");
		}

		if (csvValue.contains("\u201C")) {
			csvValue = csvValue.replaceAll("\u201C", "\"");
		}

		if (csvValue.contains("\r\n")) {
			csvValue = csvValue.replaceAll("\r\n", "");
		}

		if (csvValue.contains("\n")) {
			csvValue = csvValue.replaceAll("\n", "");
		}

		// Escaping special characters in the String.
		csvValue = StringEscapeUtils.escapeJava(csvValue);

		if (csvValue.contains(",")) {
			return "\"" + csvValue + "\"";
		} else {
			return csvValue;
		}

	}

	private Table createTableThreadHeader(DiscrepancyNoteThread discNoteThread) throws BadElementException {

		Table table = new Table(2);
		table.setTableFitsPage(true);
		table.setCellsFitPage(true);
		table.setBorderWidth(1);
		table.setBorderColor(new java.awt.Color(0, 0, 0));
		table.setPadding(TABLE_PADDING_PDF);
		table.setSpacing(TABLE_SPACING_PDF);

		// Get information for the header; the resolution status, however, has to be the latest
		// resolution status for the DN thread
		DiscrepancyNoteBean dnBean = discNoteThread.getLinkedNoteList().getFirst();
		DiscrepancyNoteUtil discUtil = new DiscrepancyNoteUtil();
		String latestResolutionStatus = discUtil.getResolutionStatusName(discNoteThread.getLinkedNoteList().getFirst()
				.getResolutionStatusId());

		StringBuilder content = new StringBuilder("");
		if (dnBean != null) {
			if (!"".equalsIgnoreCase(dnBean.getEntityName())) {
				content.append("Item field name/value: ");
				content.append(dnBean.getEntityName());
				if (!"".equalsIgnoreCase(dnBean.getEntityValue())) {
					content.append(" = ");
					content.append(dnBean.getEntityValue());
				}

			}
			Paragraph para = new Paragraph(content.toString(), new Font(Font.HELVETICA, PARENT_DN_SECTION_FONT_SIZE_PDF,
					Font.BOLD, new Color(0, 0, 0)));
			Cell cell = new Cell(para);
			cell.setHeader(true);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setColspan(2);
			table.addCell(cell);
			table.endHeaders();

			// Add at least three more rows of data -- dnBean.getSubjectName()
			// row 1
			cell = createCell("Study Subject", dnBean.getSubjectName());
			table.addCell(cell);

			cell = createCell("Study Event", dnBean.getEventName());
			table.addCell(cell);

			// row 2
			cell = createCell("Study Event Date", dnBean.getEventStart() + "");
			table.addCell(cell);

			content.append(dnBean.getCrfName());

			cell = new Cell(new Paragraph("CRF: " + dnBean.getCrfName() + "\n" + "Status: " + dnBean.getCrfStatus(),
					new Font(Font.HELVETICA, PARENT_DN_SECTION_FONT_SIZE_PDF, Font.BOLD, new Color(0, 0, 0))));

			table.addCell(cell);

			// row 3
			cell = createCell("Type", discUtil.getResolutionStatusTypeName(dnBean.getDiscrepancyNoteTypeId()));

			table.addCell(cell);

			cell = createCell("Resolution Status", latestResolutionStatus);

			table.addCell(cell);

			cell = createCell("Number of notes", discNoteThread.getLinkedNoteList().size() + "");
			table.addCell(cell);

			cell = createCell("Discrepancy Note ID", dnBean.getId() + "");
			table.addCell(cell);

			cell = createCell("Days Open", dnBean.getAge() + "");
			table.addCell(cell);

			String daysSinceUpdated = escapeQuotesInCSV(dnBean.getDays() + "");
			cell = createCell("Days Since Updated", daysSinceUpdated.equals("0") ? "" : daysSinceUpdated + "");
			table.addCell(cell);

		}

		return table;
	}

	private Cell createCell(String propertyName, String propertyValue) throws BadElementException {

		Paragraph para = new Paragraph(propertyName + ": " + propertyValue, new Font(Font.HELVETICA,
				PARENT_DN_SECTION_FONT_SIZE_PDF, Font.BOLD, new Color(0, 0, 0)));
		return new Cell(para);
	}

	private Table createTableFromBean(DiscrepancyNoteBean discBean) throws BadElementException {

		Table table = new Table(2);
		table.setTableFitsPage(true);
		table.setCellsFitPage(true);
		table.setBorderWidth(1);
		table.setBorderColor(new java.awt.Color(0, 0, 0));
		table.setPadding(TABLE_PADDING_PDF);
		table.setSpacing(TABLE_SPACING_PDF);
		Cell cell = new Cell("Discrepancy note id: " + discBean.getId());
		cell.setHeader(true);
		cell.setColspan(2);
		table.addCell(cell);
		table.endHeaders();

		cell = new Cell("Subject name: " + discBean.getSubjectName());
		table.addCell(cell);
		cell = new Cell("CRF name: " + discBean.getCrfName());
		table.addCell(cell);
		cell = new Cell("Description: " + discBean.getDescription());
		table.addCell(cell);
		if (discBean.getDisType() != null) {
			cell = new Cell("Discrepancy note type: " + discBean.getDisType().getName());
			table.addCell(cell);
		}
		cell = new Cell("Event name: " + discBean.getEventName());
		table.addCell(cell);
		cell = new Cell("Parent note ID: " + (discBean.getParentDnId() > 0 ? discBean.getParentDnId() : ""));
		table.addCell(cell);
		cell = new Cell("Resolution status: "
				+ new DiscrepancyNoteUtil().getResolutionStatusName(discBean.getResolutionStatusId()));
		table.addCell(cell);
		cell = new Cell("Detailed notes: " + discBean.getDetailedNotes());
		table.addCell(cell);
		cell = new Cell("Entity name: " + discBean.getEntityName());
		table.addCell(cell);
		cell = new Cell("Entity value: " + discBean.getEntityValue());
		table.addCell(cell);
		cell = new Cell("Date updated: " + discBean.getUpdatedDateString());
		table.addCell(cell);
		cell = new Cell("Study ID: " + discBean.getStudyId());
		table.addCell(cell);

		return table;

	}
}
