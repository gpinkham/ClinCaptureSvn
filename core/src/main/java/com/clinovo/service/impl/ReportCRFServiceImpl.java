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

package com.clinovo.service.impl;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;
import static net.sf.dynamicreports.report.builder.DynamicReports.grp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.group.ColumnGroupBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ResponseType;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.DataEntryService;
import com.clinovo.service.ReportCRFService;
import com.clinovo.util.DRTemplates;
import com.clinovo.util.DRUtil;

@Service("reportCRFService")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReportCRFServiceImpl implements ReportCRFService {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DataEntryService dataEntryService;
	@Autowired
	private StudyConfigService studyConfigService;
	
	private ResourceBundle resword;
	
	public String createPDFReport(int eventCRFId, StudyBean currentStudy, Locale locale, ResourceBundle resword, 
			String urlPath, String sysPath, String dataPath, SessionManager sm) throws Exception {
		return createReport(eventCRFId, currentStudy, locale, resword, urlPath, sysPath, dataPath, ".pdf", sm);
	}
	
	public String createReport(int eventCRFId, StudyBean currentStudy, Locale locale, ResourceBundle resword, 
			String urlPath, String sysPath, String dataPath, String fileExt, SessionManager sm) throws Exception {
		if (eventCRFId == 0) {
			return "";
		}
		this.resword = resword;
		
		SectionDAO sdao = new SectionDAO(dataSource);
		EventCRFDAO ecdao = new EventCRFDAO(dataSource);
		CRFDAO crfdao = new CRFDAO(dataSource);
		CRFVersionDAO crfVersionDAO = new CRFVersionDAO(dataSource);
		StudySubjectDAO ssdao = new StudySubjectDAO(dataSource);
		StudyEventDAO sedao = new StudyEventDAO(dataSource);
		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(dataSource);
		StudyDAO studydao = new StudyDAO(dataSource);
		SubjectDAO subjdao = new SubjectDAO(dataSource);
		ArrayList<SectionBean> allSectionBeans = new ArrayList<SectionBean>();
		ArrayList<DisplaySectionBean> sectionBeans = new ArrayList();
		SectionBean sb = new SectionBean();

		EventCRFBean ecb = (EventCRFBean) ecdao.findByPK(eventCRFId);
		
		// Get all the SectionBeans attached to this ECB
		ArrayList<SectionBean> sects = sdao.findAllByCRFVersionId(ecb.getCRFVersionId());
		for (int i = 0; i < sects.size(); i++) {
			sb = (SectionBean) sects.get(i);
			int sectId = sb.getId();
			if (sectId > 0) {
				allSectionBeans.add((SectionBean) sdao.findByPK(sectId));
			}
		}

		StudySubjectBean ssubj = (StudySubjectBean) ssdao.findByPK(ecb.getStudySubjectId());
		SubjectBean subj = (SubjectBean) subjdao.findByPK(ssubj.getSubjectId());
		StudyEventBean se = (StudyEventBean) sedao.findByPK(ecb.getStudyEventId());
		CRFVersionBean crfVerBean = (CRFVersionBean) crfVersionDAO.findByPK(ecb.getCRFVersionId());
		StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(se.getStudyEventDefinitionId());
		se.setStudyEventDefinition(sed);
		CRFBean cb = (CRFBean) crfdao.findByPK(crfVerBean.getCrfId());
		StudyBean study = (StudyBean) studydao.findByPK(ssubj.getStudyId());
		if (study.getParentStudyId() <= 0) {// top study
			studyConfigService.setParametersForStudy(study);
		} else {
			studyConfigService.setParametersForSite(study);
		}

		// Get the section beans from dataEntryService
		sectionBeans = dataEntryService.getAllDisplayBeans(allSectionBeans, ecb, study,
				Page.VIEW_SECTION_DATA_ENTRY_SERVLET);
		String titleText = cb.getName() + " " + crfVerBean.getName();
		String reportFilePath = dataPath + (titleText + " " + ssubj.getLabel()).replaceAll("( |/|\\\\)", "_");

		generateReportFile(sectionBeans, createHeaderValues(study, subj, ssubj, se, ecb, studydao, locale), titleText,
				reportFilePath, fileExt, urlPath, sysPath);
		return reportFilePath + fileExt;
	}

	public void generateReportFile(List<DisplaySectionBean> displaySectionBeans, Map<String, String> values, 
			String titleText, String reportFilePath, String fileExt, String urlPath, String sysPath) throws IOException, DRException {
		OutputStream out = new FileOutputStream(reportFilePath + fileExt);

		JasperReportBuilder report = DynamicReports.report();
		TextColumnBuilder<String> groupColumn = col.column("group", "group_column", type.stringType());
		ColumnGroupBuilder itemGroup = grp.group(groupColumn).setStyle(DRTemplates.groupColumnConditionalStyle)
				.addHeaderComponent(cmp.horizontalList().newRow().add(cmp.verticalGap(5)))
				.addFooterComponent(cmp.horizontalList().newRow().add(cmp.verticalGap(5)));
		
		report.title(DRTemplates.getTitleComponent(titleText, DRTemplates.getDynamicReportsComponent(urlPath, sysPath)), cmp.subreport(createCRFHeaderTable(values)),
				DRTemplates.getGapComponent()).pageFooter(DRTemplates.footerComponent);
		report
			.setColumnTitleStyle(DRTemplates.columnTitleStyle)
			.highlightDetailEvenRows()
			.columns(groupColumn,
				col.column("Question", "left_item_text", type.stringType()),
				col.column("Answer", "item_value", type.stringType()).setHorizontalAlignment(HorizontalAlignment.CENTER),
				col.column("", "right_item_text", type.stringType()))
			.setDataSource(createDataSource(displaySectionBeans))
			.groupBy(itemGroup);
		//report.show();
		report.toPdf(out);

		out.close();
	}

	private JasperReportBuilder createCRFHeaderTable(Map<String, String> values) {
		JasperReportBuilder report = report();
		report
		.setColumnStyle(DRTemplates.getHeaderColumnStyle())
		.columns(
				col.column("", "column_1", type.stringType()),
				col.column("", "column_2", type.stringType()).setHorizontalAlignment(HorizontalAlignment.CENTER),
				col.column("", "column_3", type.stringType()),
				col.column("", "column_4", type.stringType()).setHorizontalAlignment(HorizontalAlignment.CENTER))
		.setDataSource(createDataSourceForHeader(values));

		return report;
	}

	private JRDataSource createDataSourceForHeader(Map<String, String> values) {
		DRDataSource dataSource = new DRDataSource("column_1", "column_2", "column_3", "column_4");
		int size = values.size();
		
		int rem = size % 4;
		int NumberOfSteps = size / 4 + (rem == 0? 0 : rem == 3? 2 : 1);
		Iterator it = values.keySet().iterator();
		
		List<String> leftColumn = new ArrayList<String>();
		List<String> rightColumn = new ArrayList<String>();
		for (int i = 0; it.hasNext(); i++) {
			if (i < NumberOfSteps) {
				leftColumn.add(values.get((String) it.next()));
				leftColumn.add(it.hasNext()? values.get((String) it.next()) : "");
			} else {
				rightColumn.add(values.get((String) it.next()));
				rightColumn.add(it.hasNext()? values.get((String) it.next()) : "");
			}
		}
		
		while (leftColumn.size() > rightColumn.size()) {
			rightColumn.add("");
		}
		
		for (int i = 0; i < leftColumn.size(); i = i+2) {
			dataSource.add(leftColumn.get(i), leftColumn.get(i+1), rightColumn.get(i), rightColumn.get(i+1));
		}

		return dataSource;
	}

	private Map<String, String> createHeaderValues(StudyBean study, SubjectBean subj, StudySubjectBean ssubj,
			StudyEventBean studyEvent, EventCRFBean ecb, StudyDAO studydao, Locale locale) {
		SimpleDateFormat sdf = new SimpleDateFormat(ResourceBundleProvider.getFormatBundle(locale).getString(
				"date_format_string"));
		Map<String, String> values = new LinkedHashMap<String, String>();

		String studySubjectIDLabel = study == null
				|| StringUtil.isBlank(study.getStudyParameterConfig().getStudySubjectIdLabel()) ? resword
				.getString("study_subject_ID") + ":" : study.getStudyParameterConfig().getStudySubjectIdLabel()
				+ (study.getStudyParameterConfig().getStudySubjectIdLabel().endsWith(":") ? "" : ":");

		values.put("study_subject_ID_label", studySubjectIDLabel);
		values.put("study_subject_ID", ssubj.getLabel());
		
		String studyTitle = study.getName();
		if (study.getParentStudyId() > 0) {
			// this is a site, find parent
			StudyBean parentStudy = (StudyBean) studydao.findByPK(study.getParentStudyId());
			studyTitle = parentStudy.getName() + " - " + study.getName();
		}
		values.put("study_site_label", resword.getString("study_site") + ":");
		values.put("study_site", studyTitle);
		
		if (studyEvent != null) {
			String event =  studyEvent.getStudyEventDefinition().getName() + " (" + sdf.format(studyEvent.getDateStarted()) + ")";
			values.put("event_label", resword.getString("event") + ":");
			values.put("event", event);
		}
		
		if (ecb != null && ecb.getDateInterviewed() != null) {
			String interviewer = ecb.getInterviewerName() + " (" + sdf.format(ecb.getDateInterviewed()) + ")";
			values.put("interviewer_label", resword.getString("interviewer") + ":");
			values.put("interviewer", interviewer);
		}
		
		if (study != null && "true".equals(study.getStudyParameterConfig().getPersonIdShownOnCRF())) {
			String person_ID = subj.getUniqueIdentifier();
			values.put("person_ID_label", resword.getString("person_ID") + ":");
			values.put("person_ID", person_ID);
		}
		
		if (study != null && "1".equals(study.getStudyParameterConfig().getCollectDob())) {
			String age = Utils.getInstance().processAge(ssubj.getEnrollmentDate(), subj.getDateOfBirth());
			if (!"N/A".equals(age)) {
				values.put("age_label", resword.getString("age") + ":");
				values.put("age", age);
			}
		}

		if (subj != null && subj.getDateOfBirth() != null) {
			String date_of_birth = sdf.format(subj.getDateOfBirth());
			values.put("date_of_birth_label", resword.getString("date_of_birth") + ":");
			values.put("date_of_birth", date_of_birth);
		}
		
		if (study != null && "true".equals(study.getStudyParameterConfig().getGenderRequired()) 
				&& (subj.getGender() == 102 || subj.getGender() == 109)) {
			values.put("gender_label", study.getStudyParameterConfig().getGenderLabel() + ":");
			values.put("gender", subj.getGender() == 102? resword.getString("F") : resword.getString("M"));
		}
		
		return values;
	}

	private JRDataSource createDataSource(List<DisplaySectionBean> displaySectionBeans) {
		DRDataSource dataSource = new DRDataSource("group_column", "left_item_text", "item_value", "right_item_text");
		for (DisplaySectionBean dsb : displaySectionBeans) {
			String groupHeader = "";
			for (DisplayItemBean dib : dsb.getItems()) {
				String value = "";
				if (!"".equals(dib.getMetadata().getHeader().trim())) {
					groupHeader = DRUtil.getTextFromHeader(dib.getMetadata().getHeader());
				}
				if (dib.getMetadata().getResponseSet().getResponseType() == ResponseType.TEXT
						|| dib.getMetadata().getResponseSet().getResponseType() == ResponseType.TEXTAREA) {
					value = dib.getMetadata().getResponseSet().getValue();
				} else if (dib.getMetadata().getResponseSet().getResponseType() == ResponseType.RADIO) {
					value = DRUtil.getValueFromRadio(dib);
				} else if (dib.getMetadata().getResponseSet().getResponseType() == ResponseType.SELECT 
						|| dib.getMetadata().getResponseSet().getResponseType() == ResponseType.SELECTMULTI) {
					value = DRUtil.getValueFromSelect(dib);
				} else {
					continue;
				}
				
				dataSource.add(groupHeader, DRUtil.getTextFromHTML(dib.getMetadata().getLeftItemText()).replaceAll("&nbsp", " "), 
						value, DRUtil.getTextFromHTML(dib.getMetadata().getRightItemText()).replaceAll("&nbsp", " "));
			}
		}
		return dataSource;
	}
}
