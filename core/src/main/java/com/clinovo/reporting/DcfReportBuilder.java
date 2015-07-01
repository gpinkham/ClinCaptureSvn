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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.reporting;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.margin;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.model.DiscrepancyCorrectionForm;
import com.clinovo.util.DRTemplates;
import com.clinovo.util.DateUtil;

/**
 * Builds DCF.
 * 
 * @author Frank
 * 
 */
public class DcfReportBuilder {

	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private static final int REPORT_MARGIN = 35;
	private static final int REPORT_TOP_MARGIN = 50;
	private static final float BORDER_WIDTH_ZERO_POINT_FIVE = 0.5f;
	private static final String CHECKBOX_UNICODE = "\u2610";
	private final String entityTypeStudyEvent = "studyEvent";
	private final String entityTypeSubject = "subject";
	private final String entityTypeStudySubject = "studySub";
	private ResourceBundle resword;
	private ResourceBundle resformat;
	private Locale locale;
	private String userTimeZoneId;

	public DcfReportBuilder() {
		this.locale = Locale.ENGLISH;
		this.userTimeZoneId = DateTimeZone.getDefault().getID();
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getUserTimeZoneId() {
		return userTimeZoneId;
	}

	public void setUserTimeZoneId(String userTimeZoneId) {
		this.userTimeZoneId = userTimeZoneId;
	}

	/**
	 * Builds DCF based on list of DiscrepancyCorrectionForm passed and saves it as pdf.
	 * 
	 * @param dcfs
	 *            List of DiscrepancyCorrectionForms
	 * @param fileName
	 *            name of pdf file
	 * @throws IOException
	 *             thrown in case of IO error during write
	 * @throws DRException
	 *             thrown in case of DynamicReports error during build
	 */
	public void buildPdf(List<DiscrepancyCorrectionForm> dcfs, String fileName) throws IOException, DRException {
		initResourceBundles();
		OutputStream outputStream = new FileOutputStream(fileName);
		JasperReportBuilder report = dcfs.size() < 2 ? buildSingleDcf(dcfs.get(0)) : buildMultipleDcfs(dcfs);
		try {
			report.toPdf(outputStream);
		} catch (DRException e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		}
	}

	private void initResourceBundles() {
		this.resword = ResourceBundleProvider.getWordsBundle(getLocale());
		this.resformat = ResourceBundleProvider.getFormatBundle(getLocale());
	}

	private JasperReportBuilder buildSingleDcf(DiscrepancyCorrectionForm dcf) {
		JasperReportBuilder report = DynamicReports.report();
		report.title(getDcfReportComponents(dcf));
		report.setPageFormat(PageType.A4).setPageMargin(
				margin().setLeft(REPORT_MARGIN).setRight(REPORT_MARGIN).setTop(REPORT_TOP_MARGIN)
						.setBottom(REPORT_MARGIN));
		return report;
	}

	@SuppressWarnings("rawtypes")
	private JasperReportBuilder buildMultipleDcfs(List<DiscrepancyCorrectionForm> dcfs) {
		JasperReportBuilder report = DynamicReports.report();
		List<ComponentBuilder> dcfReportComponents = new ArrayList<ComponentBuilder>();
		DiscrepancyCorrectionForm firstDcf = dcfs.remove(0);
		ComponentBuilder[] firstDcfComponents = getDcfReportComponents(firstDcf);
		dcfReportComponents.addAll(Arrays.asList(firstDcfComponents));
		ComponentBuilder[] additionalDcfComponents = getAdditionalDcfs(dcfs);
		dcfReportComponents.addAll(Arrays.asList(additionalDcfComponents));
		report.title(dcfReportComponents.toArray(new ComponentBuilder[0]));
		report.setPageFormat(PageType.A4).setPageMargin(
				margin().setLeft(REPORT_MARGIN).setRight(REPORT_MARGIN).setTop(REPORT_TOP_MARGIN)
						.setBottom(REPORT_MARGIN));
		return report;
	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder[] getAdditionalDcfs(List<DiscrepancyCorrectionForm> dcfs) {
		List<JasperReportBuilder> reports = generateReportList(dcfs);
		List<ComponentBuilder> subreports = new ArrayList<ComponentBuilder>();
		for (JasperReportBuilder report : reports) {
			subreports.add(cmp.pageBreak());
			subreports.add(cmp.subreport(report));
		}
		return subreports.toArray(new ComponentBuilder[0]);
	}

	private List<JasperReportBuilder> generateReportList(List<DiscrepancyCorrectionForm> dcfs) {
		List<JasperReportBuilder> reports = new ArrayList<JasperReportBuilder>();
		JasperReportBuilder report;
		for (DiscrepancyCorrectionForm dcf : dcfs) {
			report = DynamicReports.report();
			report.title(getDcfReportComponents(dcf));
			reports.add(report);
		}
		return reports;
	}

	@SuppressWarnings("rawtypes")
	private ComponentBuilder[] getDcfReportComponents(DiscrepancyCorrectionForm dcf) {
		List<ComponentBuilder> components = new ArrayList<ComponentBuilder>();
		components.add(DRTemplates.createTitleComponent(getReportTitle()));
		components.add(cmp.subreport(createStudyInfoTable(dcf)));
		components.add(cmp.subreport(createQueryInfoTable(dcf)));
		components.add(cmp.subreport(createCrfOrStudyEventOrSubjectInfoTable(dcf)));
		components.add(cmp.subreport(createQuestionTable(dcf)));
		components.add(cmp.subreport(createResponseTable()));
		components.add(cmp.subreport(createOfficialTable()));
		return components.toArray(new ComponentBuilder[0]);
	}

	private JasperReportBuilder createStudyInfoTable(DiscrepancyCorrectionForm dcf) {
		JasperReportBuilder report = report();
		report.columns(
				col.column("", "column_1", type.stringType()).setStyle(
						DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
				col.column("", "column_2", type.stringType()).setStyle(
						DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE)),
				col.column("", "column_3", type.stringType()).setStyle(
						DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
				col.column("", "column_4", type.stringType()).setStyle(
						DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE))).setDataSource(
				createFourColumnDataSource(createStudyInfoValues(dcf)));
		return report;
	}

	private JasperReportBuilder createQueryInfoTable(DiscrepancyCorrectionForm dcf) {
		JasperReportBuilder report = report();
		report.columns(
				col.column("", "column_1", type.stringType()).setStyle(
						DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
				col.column("", "column_2", type.stringType()).setStyle(
						DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE)),
				col.column("", "column_3", type.stringType()).setStyle(
						DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
				col.column("", "column_4", type.stringType()).setStyle(
						DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE))).setDataSource(
				createFourColumnDataSource(createNoteInfoValues(dcf)));
		return report;
	}

	private JasperReportBuilder createCrfOrStudyEventOrSubjectInfoTable(DiscrepancyCorrectionForm dcf) {
		if (dcf.getEntityType().equals(entityTypeStudyEvent)) {
			return createStudyEventDnInfoTable(dcf);
		} else if (dcf.getEntityType().equals(entityTypeSubject) || dcf.getEntityType().equals(entityTypeStudySubject)) {
			return createSubjectDnInfoTable(dcf);
		}
		return createCrfDnInfoTable(dcf);
	}

	private JasperReportBuilder createCrfDnInfoTable(DiscrepancyCorrectionForm dcf) {
		JasperReportBuilder report = report();
		report.addColumnHeader(
				cmp.horizontalList().add(
						cmp.text(resword.getString("dcf_visit").concat(":"))
								.setStyle(DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0))
								.setHorizontalAlignment(HorizontalAlignment.RIGHT),
						cmp.text(dcf.getEventName()).setStyle(
								DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE))))
				.columns(
						col.column("", "column_1", type.stringType()).setStyle(
								DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
						col.column("", "column_2", type.stringType()).setStyle(
								DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE)),
						col.column("", "column_3", type.stringType()).setStyle(
								DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
						col.column("", "column_4", type.stringType()).setStyle(
								DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE)))
				.setDataSource(createFourColumnDataSource(createCrfInfoValues(dcf)));
		return report;
	}

	private JasperReportBuilder createStudyEventDnInfoTable(DiscrepancyCorrectionForm dcf) {
		JasperReportBuilder report = report();
		report.addColumnHeader(
				cmp.horizontalList().add(
						cmp.text(resword.getString("dcf_visit").concat(":"))
								.setStyle(DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0))
								.setHorizontalAlignment(HorizontalAlignment.RIGHT),
						cmp.text(dcf.getEventName()).setStyle(
								DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE))))
				.columns(
						col.column("", "column_1", type.stringType()).setStyle(
								DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
						col.column("", "column_2", type.stringType()).setStyle(
								DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE)),
						col.column("", "column_3", type.stringType()).setStyle(
								DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
						col.column("", "column_4", type.stringType()).setStyle(
								DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE)))
				.setDataSource(createFourColumnDataSource(createStudyEventInfoValues(dcf)));
		return report;
	}

	private JasperReportBuilder createSubjectDnInfoTable(DiscrepancyCorrectionForm dcf) {
		JasperReportBuilder report = report();
		report.columns(
				col.column("", "column_1", type.stringType()).setStyle(
						DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
				col.column("", "column_2", type.stringType()).setStyle(
						DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE)),
				col.column("", "column_3", type.stringType()).setStyle(
						DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0)),
				col.column("", "column_4", type.stringType()).setStyle(
						DRTemplates.getNormalColumnStyle(0, BORDER_WIDTH_ZERO_POINT_FIVE))).setDataSource(
				createFourColumnDataSource(createSubjectDnInfoValues(dcf)));
		return report;
	}

	private JasperReportBuilder createQuestionTable(DiscrepancyCorrectionForm dcf) {
		JasperReportBuilder report = report();
		report.addColumnHeader(
				cmp.text(resword.getString("question_to_site").concat(":")).setStyle(
						DRTemplates.getQuestionToSiteHeaderStyle()))
				.columns(
						col.column("", "column_1", type.stringType()).setStyle(
								DRTemplates.getQuestionToSiteNormalStyle()))
				.setDataSource(createSingleColumnDataSource(createQuestionValues(dcf)));
		return report;
	}

	private JasperReportBuilder createResponseTable() {
		JasperReportBuilder report = report();
		final int numberOfLinesInResponseBox = 14;
		final int widthOfFirstColumn = 150;
		final int widthOfForthColumn = 132;
		report.addColumnHeader(
				cmp.text(
						resword.getString("response_from_site").concat(":")
								.concat(getNewLines(numberOfLinesInResponseBox))).setStyle(
						DRTemplates.getHeaderColumnStyle()))
				.columns(
						col.column("", "column_1", type.stringType())
								.setStyle(DRTemplates.getHeaderColumnStyle(BORDER_WIDTH_ZERO_POINT_FIVE, 0))
								.setWidth(widthOfFirstColumn),
						col.column("", "column_2", type.stringType()).setStyle(DRTemplates.getNormalColumnStyle(0, 0)),
						col.column("", "column_3", type.stringType()).setStyle(DRTemplates.getNormalColumnStyle(0, 0)),
						col.column("", "column_4", type.stringType()).setStyle(DRTemplates.getHeaderColumnStyle())
								.setWidth(widthOfForthColumn))
				.setDataSource(createFourColumnDataSource(createResponseTableValues()));
		return report;
	}

	private JasperReportBuilder createOfficialTable() {
		JasperReportBuilder report = report();
		final int fixedColumnWidth = 142;
		final int secondColumnWidth = 240;
		final int thirdColumnWidth = 10;
		final int lastColumnWidth = 146;
		report.addColumnHeader(cmp.text(resword.getString("dcf_dm_use_only")))
				.columns(
						col.column("", "column_1", type.stringType()).setStyle(DRTemplates.getHeaderColumnStyle())
								.setWidth(fixedColumnWidth),
						col.column("", "column_2", type.stringType()).setStyle(DRTemplates.getHeaderColumnStyle(0, 0))
								.setWidth(secondColumnWidth),
						col.column("", "column_3", type.stringType()).setStyle(DRTemplates.getNormalColumnStyle(0, 0))
								.setWidth(thirdColumnWidth),
						col.column("", "column_4", type.stringType()).setStyle(DRTemplates.getHeaderColumnStyle())
								.setWidth(lastColumnWidth))
				.setDataSource(createFourColumnDataSource(createOfficialTableValues()));
		return report;
	}

	private Map<String, String> createStudyInfoValues(DiscrepancyCorrectionForm dcf) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("study_label", resword.getString("study").concat(":"));
		values.put("study_value", dcf.getStudyName());
		values.put("subject_id_label", resword.getString("subject_ID").concat(":"));
		values.put("subject_id_value", dcf.getSubjectId());
		values.put("site_label", resword.getString("site").concat(":"));
		values.put("site_value", dcf.getSiteName());
		values.put("investigator_label", resword.getString("investigator").concat(":"));
		values.put("investigator_value", dcf.getInvestigatorName());
		return values;
	}

	private Map<String, String> createNoteInfoValues(DiscrepancyCorrectionForm dcf) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("note_label", resword.getString("note_id").concat(":"));
		values.put("note_value", dcf.getNoteId().toString());
		values.put("note_type_label", resword.getString("type").concat(":"));
		values.put("note_type_value", dcf.getNoteType());
		values.put("note_date_label", resword.getString("date").concat(":"));
		values.put("note_date_value", DateUtil.printDate(dcf.getNoteDate(), getUserTimeZoneId(),
				DateUtil.DatePattern.DATE, getLocale()));
		values.put("note_status_label", resword.getString("status"));
		values.put("note_status_value", dcf.getResolutionStatus());
		return values;
	}

	private Map<String, String> createCrfInfoValues(DiscrepancyCorrectionForm dcf) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("crf_name_label", resword.getString("CRF_name").concat(":"));
		values.put("crf_name_value", dcf.getCrfName());
		values.put("page_label", resword.getString("page").concat(":"));
		values.put("page_value", dcf.getPage() != null ? dcf.getPage().toString() : "");
		values.put("crf_item_name_label", resword.getString("crf_item_name").concat(":"));
		values.put("crf_item_name_value", dcf.getCrfItemName());
		values.put("crf_item_value_label", resword.getString("crf_value").concat(":"));
		values.put("crf_item_value", dcf.getCrfItemValue());
		return values;
	}

	private Map<String, String> createStudyEventInfoValues(DiscrepancyCorrectionForm dcf) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("event_item_name_label", resword.getString("dcf_visit_item_name").concat(":"));
		values.put("event_item_name_value", dcf.getEventItemName());
		values.put("event_item_value_lable", resword.getString("dcf_visit_item_value").concat(":"));
		values.put("event_item_value_value", dcf.getEventItemValue());
		return values;
	}

	private Map<String, String> createSubjectDnInfoValues(DiscrepancyCorrectionForm dcf) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("subject_item_name_label", resword.getString("dcf_subject_item_name").concat(":"));
		values.put("subject_item_name_value", dcf.getSubjectItemName());
		values.put("subject_item_value_lable", resword.getString("dcf_subject_item_value").concat(":"));
		values.put("subject_item_value", dcf.getSubjectItemValue());
		return values;
	}

	private Map<String, String> createQuestionValues(DiscrepancyCorrectionForm dcf) {
		final int numberOfExtraLinesInQuestionBox = 8;
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("question", dcf.getQuestionToSite().concat(getNewLines(numberOfExtraLinesInQuestionBox)));
		return values;
	}

	private Map<String, String> createResponseTableValues() {
		Map<String, String> values = new LinkedHashMap<String, String>();
		values.put("column_1", resword.getString("dcf_investigators_signature").concat(":").concat(getNewLines(2)));
		values.put("column_2", "");
		values.put("column_3", "");
		values.put("column_4",
				resword.getString("date").concat(": (").concat(resformat.getString("date_format").concat(")")));
		return values;
	}

	private Map<String, String> createOfficialTableValues() {
		Map<String, String> values = new LinkedHashMap<String, String>();
		String dbChangeCheckList = CHECKBOX_UNICODE.concat(" ")
				.concat(resword.getString("dcf_no_db_change").concat(getNewLines(2))).concat(CHECKBOX_UNICODE)
				.concat(" ").concat(resword.getString("dcf_db_change"));
		values.put("column_1", dbChangeCheckList);
		values.put("column_2", resword.getString("dcf_dm_signature"));
		values.put("column_3", "");
		values.put("column_4",
				resword.getString("date").concat(": (").concat(resformat.getString("date_format").concat(")")));
		return values;
	}

	private JRDataSource createFourColumnDataSource(Map<String, String> values) {
		final int maximumColumnNumber = 4;
		DRDataSource dataSource = new DRDataSource("column_1", "column_2", "column_3", "column_4");
		Iterator<String> it = values.keySet().iterator();
		List<String> rowFieldValues = new ArrayList<String>();
		while (it.hasNext()) {
			String rowFieldValue = values.get(it.next());
			rowFieldValues.add(rowFieldValue);
			if (rowFieldValues.size() == maximumColumnNumber) {
				dataSource.add(rowFieldValues.toArray());
				rowFieldValues.clear();
			}
		}
		return dataSource;
	}

	private JRDataSource createSingleColumnDataSource(Map<String, String> values) {
		DRDataSource dataSource = new DRDataSource("column_1");
		for (String key : values.keySet()) {
			dataSource.add(values.get(key));
		}
		return dataSource;
	}

	private String getReportTitle() {
		return resword.getString("discrepancy_correction_form").concat(" (").concat(resword.getString("dcf"))
				.concat(")");
	}

	private String getNewLines(int numberOfNewLines) {
		String newLines = "";
		int i = 0;
		while (i++ < numberOfNewLines) {
			newLines += "\n";
		}
		return newLines;
	}
}
