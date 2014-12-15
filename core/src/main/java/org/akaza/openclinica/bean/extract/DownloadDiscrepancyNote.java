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

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.akaza.openclinica.service.DiscrepancyNoteUtil;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class converts or serializes DiscrepancyNoteBeans to Strings or iText-related classes so that they can be
 * compiled into a file and downloaded to the user. This is a convenience class with a number of different methods for
 * serializing beans to Strings.
 */
public class DownloadDiscrepancyNote implements DownLoadBean {

	public static final String CSV = "text/plain";
	public static final String PDF = "application/pdf";
	public static final String FONT_NAME = "DejaVuSansMono";

	private static final int TABLE_PADDING_PDF = 4;
	private static final int TABLE_SPACING_PDF = 4;

	private static final int STUDY_IDENTIFIER_FONT_SIZE_PDF = 18;
	private static final int PARENT_DN_SECTION_FONT_SIZE_PDF = 12;

	public static final Map<Integer, String> RESOLUTION_STATUS_MAP = new HashMap<Integer, String>();
	private ResourceBundle resword = ResourceBundleProvider.getWordsBundle();
	private ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();

	static {
		int index = 1;
		RESOLUTION_STATUS_MAP.put(index++, "New");
		RESOLUTION_STATUS_MAP.put(index++, "Updated");
		RESOLUTION_STATUS_MAP.put(index++, "Resolution Proposed");
		RESOLUTION_STATUS_MAP.put(index++, "Closed");
		RESOLUTION_STATUS_MAP.put(index, "Not Applicable");
	}

	/**
	 * DownloadDiscrepancyNote constructor.
	 *
	 * @param locale the local object for i18n
	 */
	public DownloadDiscrepancyNote(Locale locale) {
		ResourceBundleProvider.updateLocale(locale);
	}

	/**
	 * Method updates servlet output stream with data for print.
	 *
	 * @param bean   the bean for print.
	 * @param format the print format.
	 * @param stream the servlet output stream.
	 * @throws DocumentException the exception for stream changes.
	 */
	public void downLoad(EntityBean bean, String format, OutputStream stream) throws DocumentException {

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

	/**
	 * Method updates servlet output stream with data for print.
	 *
	 * @param listOfBeans the list of beans for print.
	 * @param format      the print format.
	 * @param stream      the servlet output stream.
	 * @throws IOException the exception for prf object.
	 */
	public void downLoad(List<EntityBean> listOfBeans, String format, OutputStream stream) throws IOException {

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

	/**
	 * Method returns the size of print data.
	 *
	 * @param bean   the bean.
	 * @param format the print format
	 * @return the size of current bean.
	 * @throws IOException the exception for stream update.
	 */
	public int getContentLength(EntityBean bean, String format) throws IOException {
		return serializeToString(bean, false, 0).getBytes().length;
	}

	/**
	 * Method returns the length of bean list.
	 *
	 * @param threadBeans the list if beans.
	 * @return the length of the list bean.
	 * @throws IOException the exception for output stream update.
	 */
	public int getThreadListContentLength(List<DiscrepancyNoteThread> threadBeans) throws IOException {

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

	/**
	 * Method returns CSV in the string format.
	 *
	 * @param bean             the bean for print.
	 * @param includeHeaderRow the boolean parameter for enable/disable header row.
	 * @param threadNumber     the number of page.
	 * @return the csv in the string format.
	 * @throws IOException the exception for output stream update.
	 */
	public String serializeToString(EntityBean bean, boolean includeHeaderRow, int threadNumber) throws IOException {

		DiscrepancyNoteBean discNoteBean = (DiscrepancyNoteBean) bean;
		StringBuilder writer = new StringBuilder("");
		// If includeHeaderRow = true, the first row of the output consists of header names, only for CSV format
		if (includeHeaderRow) {

			writer.append(resword.getString("study_subject_ID")).append(",");
			writer.append(resword.getString("subject_status")).append(",");
			writer.append(resword.getString("study_site") + " " + resword.getString("view_executed_rules_oid")).append(",");
			// we're adding a thread number row
			writer.append(resword.getString("thread_id")).append(",");

			writer.append(resword.getString("note_id")).append(",");

			writer.append(resword.getString("parent_note_id")).append(",");

			writer.append(resword.getString("date_created")).append(",");
			writer.append(resword.getString("date_updated")).append(",");
			writer.append(resword.getString("days_open")).append(",");
			writer.append(resword.getString("days_since_updated")).append(",");

			if (discNoteBean.getDisType() != null) {
				writer.append(resword.getString("discrepancy_type")).append(",");
			}
			writer.append(resword.getString("resolution_status")).append(",");
			writer.append(resword.getString("event_name")).append(",");
			writer.append(resword.getString("crfEvaluationTable.crfName")).append(",");
			writer.append(resword.getString("CRF_status")).append(",");
			writer.append(resword.getString("entity_name")).append(",");
			writer.append(resword.getString("entity_value")).append(",");
			writer.append(resword.getString("description")).append(",");
			writer.append(resword.getString("detailed_notes")).append(",");
			writer.append(resword.getString("assigned_user")).append(",");
			writer.append(resword.getString("study_ID")).append("\n");
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
			String daysSinceUpdated = (discNoteBean.getDays() + "");
			writer.append(escapeQuotesInCSV(daysSinceUpdated.equals("0") ? "" : daysSinceUpdated)).append(",");
		} else {
			writer.append(",").append(",");
		}
		if (discNoteBean.getDisType() != null) {
			writer.append(escapeQuotesInCSV(discNoteBean.getDisType().getName())).append(",");
		}

		writer.append(escapeQuotesInCSV(resword.getString(RESOLUTION_STATUS_MAP.get(discNoteBean.getResolutionStatusId()).replaceAll(" ", "_").toLowerCase() + ""))).append(",");
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

	private void serializeToPDF(EntityBean bean, OutputStream stream) throws IOException, DocumentException {

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

	private void serializeListToPDF(String content, OutputStream stream) {

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

	private void serializeThreadsToPDF(List<DiscrepancyNoteThread> listOfThreads, OutputStream stream,
			String studyIdentifier) throws IOException {

		ServletOutputStream servletStream = (ServletOutputStream) stream;

		Document pdfDoc = new Document();

		try {
			PdfWriter.getInstance(pdfDoc, servletStream);
			pdfDoc.open();
			// Create header for the study identifier or name
			if (studyIdentifier != null) {
				HeaderFooter header = new HeaderFooter(new Phrase(resword.getString("study_identifier")
						+ ": " + studyIdentifier + " " + resword.getString("pg")
						+ " ", getFont(STUDY_IDENTIFIER_FONT_SIZE_PDF)), true);
				header.setAlignment(Element.ALIGN_CENTER);
				Paragraph para = new Paragraph(resword.getString("study_identifier") + ": " + studyIdentifier, getFont(STUDY_IDENTIFIER_FONT_SIZE_PDF));
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

	/**
	 * Method updates output stream with required print data.
	 *
	 * @param listOfThreadedBeans the list of beans for print.
	 * @param format              the print format.
	 * @param stream              the output stream.
	 * @param studyIdentifier     the current study identifier.
	 * @throws IOException the exception for output stream update.
	 */
	public void downLoadThreadedDiscBeans(List<DiscrepancyNoteThread> listOfThreadedBeans, String format,
			OutputStream stream, String studyIdentifier) throws IOException {

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
		PrintWriter out = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
		try {
			if (CSV.equalsIgnoreCase(format)) {
				out.write(allContent.toString());
			} else {
				this.serializeThreadsToPDF(listOfThreadedBeans, servletStream, studyIdentifier);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null && stream != null) {
				try {
					out.close();
					stream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private Table createTableThreadHeader(DiscrepancyNoteThread discNoteThread) throws DocumentException, IOException {

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
				content.append(resword.getString("item_field_name_value") + ": ");
				content.append(dnBean.getEntityName());
				if (!"".equalsIgnoreCase(dnBean.getEntityValue())) {
					content.append(" = ");
					content.append(dnBean.getEntityValue());
				}

			}

			Paragraph para = new Paragraph(content.toString(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF));
			Cell cell = new Cell(para);
			cell.setHeader(true);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setColspan(2);
			table.addCell(cell);
			table.endHeaders();

			// Add at least three more rows of data -- dnBean.getSubjectName()
			// row 1
			cell = createCell(resword.getString("study_subject"), dnBean.getSubjectName());
			table.addCell(cell);

			cell = createCell(resword.getString("study_event"), dnBean.getEventName());
			table.addCell(cell);

			// row 2
			cell = createCell(resword.getString("study_event_date"),
					dnBean.getEventStart() != null ? String.valueOf(dnBean.getEventStart()) : "");
			table.addCell(cell);

			content.append(dnBean.getCrfName());

			cell = new Cell(new Paragraph(resword.getString("CRF") + ": " + dnBean.getCrfName() + "\n" + resword.getString("status") + ": " + dnBean.getCrfStatus(),
					getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));

			table.addCell(cell);

			// row 3
			cell = createCell(resword.getString("type"), resterm.getString(discUtil.getResolutionStatusTypeName(dnBean.getDiscrepancyNoteTypeId()).replaceAll(" ", "_").toLowerCase()));

			table.addCell(cell);

			cell = createCell(resword.getString("resolution_status"), resword.getString(latestResolutionStatus.replaceAll(" ", "_").toLowerCase()));

			table.addCell(cell);

			cell = createCell(resword.getString("number_of_notes"), discNoteThread.getLinkedNoteList().size() + "");
			table.addCell(cell);

			cell = createCell(resword.getString("discrepancy_note_id"), dnBean.getId() + "");
			table.addCell(cell);

			cell = createCell(resword.getString("days_open"), dnBean.getAge() + "");
			table.addCell(cell);

			String daysSinceUpdated = escapeQuotesInCSV(dnBean.getDays() + "");
			cell = createCell(resword.getString("days_since_updated"), daysSinceUpdated.equals("0") ? "" : daysSinceUpdated + "");
			table.addCell(cell);

		}

		return table;
	}

	private Cell createCell(String propertyName, String propertyValue) throws DocumentException, IOException {
		Phrase para = new Phrase(propertyName + ": " + propertyValue, getFont(PARENT_DN_SECTION_FONT_SIZE_PDF));
		return new Cell(para);
	}

	private Table createTableFromBean(DiscrepancyNoteBean discBean) throws DocumentException, IOException {

		Table table = new Table(2);
		table.setTableFitsPage(true);
		table.setCellsFitPage(true);
		table.setBorderWidth(1);
		table.setBorderColor(new java.awt.Color(0, 0, 0));
		table.setPadding(TABLE_PADDING_PDF);
		table.setSpacing(TABLE_SPACING_PDF);
		Cell cell = new Cell(new Phrase(resword.getString("discrepancy_note_id") + ": " + discBean.getId(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		cell.setHeader(true);
		cell.setColspan(2);
		table.addCell(cell);
		table.endHeaders();

		cell = new Cell(new Phrase(resword.getString("subject") + " " + resword.getString("name") + ": " + discBean.getSubjectName(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("CRF_name") + ": " + discBean.getCrfName(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("description") + ": " + discBean.getDescription(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		if (discBean.getDisType() != null) {
			cell = new Cell(new Phrase(resword.getString("discrepancy_type") + ": " + discBean.getDisType().getName(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
			table.addCell(cell);
		}
		cell = new Cell(new Phrase(resword.getString("event_name") + ": " + discBean.getEventName(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("parent_note_id") + ": " + (discBean.getParentDnId() > 0 ? discBean.getParentDnId() : ""), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("resolution_status") + ": "
				+ new DiscrepancyNoteUtil().getResolutionStatusName(discBean.getResolutionStatusId()), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("detailed_notes") + ": " + discBean.getDetailedNotes(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("entity_name") + ": " + discBean.getEntityName(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("entity_value") + ": " + discBean.getEntityValue(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("date_updated") + ": " + discBean.getUpdatedDateString(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);
		cell = new Cell(new Phrase(resword.getString("study_ID") + ": " + discBean.getStudyId(), getFont(PARENT_DN_SECTION_FONT_SIZE_PDF)));
		table.addCell(cell);

		return table;

	}

	private Font getFont(int fontSize) throws IOException, DocumentException {

		Resource resource = new DefaultResourceLoader().getResource("..".concat(File.separator).concat("..")
				.concat(File.separator).concat("includes").concat(File.separator).concat(FONT_NAME).concat(".ttf"));
		BaseFont baseFont = BaseFont.createFont(resource.getFile().getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font font = new Font(baseFont, fontSize);

		return font;
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
		if (csvValue.contains(",")) {
			return "\"" + csvValue + "\"";
		} else {
			return csvValue;
		}
	}
}
