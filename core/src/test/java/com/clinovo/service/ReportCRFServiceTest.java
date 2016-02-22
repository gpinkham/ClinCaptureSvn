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

package com.clinovo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Locale;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.ColumnBuilders;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilders;
import net.sf.dynamicreports.report.builder.datatype.StringType;
import net.sf.dynamicreports.report.builder.group.GroupBuilders;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.CoreResources;
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
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.clinovo.service.impl.ReportCRFServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReportCRFServiceImpl.class, DynamicReports.class, ColumnBuilders.class, CoreResources.class,
		SectionDAO.class, EventCRFDAO.class, CRFDAO.class, CRFVersionDAO.class, StudySubjectDAO.class,
		StudyEventDAO.class, StudyEventDefinitionDAO.class, StudyDAO.class, SubjectDAO.class})
@SuppressWarnings("unchecked")
public class ReportCRFServiceTest {

	private int eventCRFId;
	private String fileName;
	private Locale testLocale;
	private ReportCRFServiceImpl reportCRFService;
	private DynamicsMetadataService dynamicsMetadataService;

	@Before
	public void setUp() throws Exception {
		TextColumnBuilder<String> textColumnBuilder = Mockito.mock(TextColumnBuilder.class);
		ColumnBuilders columnBuilders = Mockito.mock(ColumnBuilders.class);
		PowerMockito
				.when(columnBuilders.column(Mockito.anyString(), Mockito.anyString(), Mockito.any(StringType.class)))
				.thenReturn(textColumnBuilder);
		PowerMockito.whenNew(ColumnBuilders.class).withAnyArguments().thenReturn(columnBuilders);
		GroupBuilders groupBuilders = Mockito.mock(GroupBuilders.class);
		PowerMockito.whenNew(GroupBuilders.class).withAnyArguments().thenReturn(groupBuilders);
		ComponentBuilders componentBuilders = Mockito.mock(ComponentBuilders.class);
		PowerMockito.whenNew(ComponentBuilders.class).withAnyArguments().thenReturn(componentBuilders);
		eventCRFId = 1;
		testLocale = Locale.ENGLISH;
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(CoreResources.getDBType()).thenReturn("postgres");
		JasperReportBuilder jasperReportBuilder = Mockito.mock(JasperReportBuilder.class);
		PowerMockito.mockStatic(DynamicReports.class);
		PowerMockito.when(DynamicReports.report()).thenReturn(jasperReportBuilder);
		SectionDAO sectionDAO = Mockito.mock(SectionDAO.class);
		EventCRFDAO eventCRFDAO = Mockito.mock(EventCRFDAO.class);
		CRFDAO crfDAO = Mockito.mock(CRFDAO.class);
		CRFVersionDAO crfVersionDAO = Mockito.mock(CRFVersionDAO.class);
		StudySubjectDAO studySubjectDAO = Mockito.mock(StudySubjectDAO.class);
		StudyEventDAO studyEventDAO = Mockito.mock(StudyEventDAO.class);
		StudyEventDefinitionDAO studyEventDefinitionDAO = Mockito.mock(StudyEventDefinitionDAO.class);
		StudyDAO studyDAO = Mockito.mock(StudyDAO.class);
		SubjectDAO subjectDAO = Mockito.mock(SubjectDAO.class);
		PowerMockito.whenNew(SectionDAO.class).withAnyArguments().thenReturn(sectionDAO);
		PowerMockito.whenNew(EventCRFDAO.class).withAnyArguments().thenReturn(eventCRFDAO);
		PowerMockito.whenNew(CRFDAO.class).withAnyArguments().thenReturn(crfDAO);
		PowerMockito.whenNew(CRFVersionDAO.class).withAnyArguments().thenReturn(crfVersionDAO);
		PowerMockito.whenNew(StudySubjectDAO.class).withAnyArguments().thenReturn(studySubjectDAO);
		PowerMockito.whenNew(StudyEventDAO.class).withAnyArguments().thenReturn(studyEventDAO);
		PowerMockito.whenNew(StudyEventDefinitionDAO.class).withAnyArguments().thenReturn(studyEventDefinitionDAO);
		PowerMockito.whenNew(StudyDAO.class).withAnyArguments().thenReturn(studyDAO);
		PowerMockito.whenNew(SubjectDAO.class).withAnyArguments().thenReturn(subjectDAO);
		PowerMockito.whenNew(JasperReportBuilder.class).withAnyArguments()
				.thenReturn(Mockito.mock(JasperReportBuilder.class));
		reportCRFService = Mockito.mock(ReportCRFServiceImpl.class);
		Whitebox.setInternalState(reportCRFService, "urlPath", "");
		Whitebox.setInternalState(reportCRFService, "sysPath", "");
		Whitebox.setInternalState(reportCRFService, "dataPath", "");
		Whitebox.setInternalState(reportCRFService, "resword", ResourceBundleProvider.getWordsBundle(testLocale));
		StudyConfigService studyConfigService = Mockito.mock(StudyConfigService.class);
		Whitebox.setInternalState(reportCRFService, "studyConfigService", studyConfigService);
		DataEntryService dataEntryService = Mockito.mock(DataEntryService.class);
		Whitebox.setInternalState(reportCRFService, "dataEntryService", dataEntryService);
		dynamicsMetadataService = Mockito.mock(DynamicsMetadataService.class);
		EventCRFBean eventCRFBean = new EventCRFBean();
		eventCRFBean.setCRFVersionId(1);
		eventCRFBean.setStudySubjectId(1);
		eventCRFBean.setStudyEventId(1);
		Mockito.when(eventCRFDAO.findByPK(Mockito.anyInt())).thenReturn(eventCRFBean);
		CRFVersionBean crfVersionBean = new CRFVersionBean();
		crfVersionBean.setCrfId(1);
		crfVersionBean.setName("v2.0");
		Mockito.when(crfVersionDAO.findByPK(Mockito.anyInt())).thenReturn(crfVersionBean);
		CRFBean crfBean = new CRFBean();
		crfBean.setName("Agent Administration");
		Mockito.when(crfDAO.findByPK(Mockito.anyInt())).thenReturn(crfBean);
		StudySubjectBean studySubjectBean = new StudySubjectBean();
		studySubjectBean.setLabel("ssID1");
		studySubjectBean.setStudyId(1);
		StudyBean studyBean = new StudyBean();
		Mockito.when(studyDAO.findByPK(Mockito.anyInt())).thenReturn(studyBean);
		Mockito.when(studySubjectDAO.findByPK(Mockito.anyInt())).thenReturn(studySubjectBean);
		StudyEventBean studyEventBean = new StudyEventBean();
		studyEventBean.setDateStarted(new Date());
		studyEventBean.setStudyEventDefinitionId(1);
		Mockito.when(studyEventDAO.findByPK(Mockito.anyInt())).thenReturn(studyEventBean);
		StudyEventDefinitionBean studyEventDefinitionBean = new StudyEventDefinitionBean();
		studyEventDefinitionBean.setName("");
		Mockito.when(studyEventDefinitionDAO.findByPK(Mockito.anyInt())).thenReturn(studyEventDefinitionBean);
		studyEventBean.setStudyEventDefinition(studyEventDefinitionBean);
		String titleText = crfBean.getName() + " " + crfVersionBean.getName();
		String reportFilePath = "" + (titleText + " " + studySubjectBean.getLabel()).replaceAll("( |/|\\\\)", "_");
		fileName = reportFilePath + titleText;
		Mockito.when(reportCRFService.generateReportFile(Mockito.anyList(), Mockito.anyMap(), Mockito.anyMap(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(fileName);
		Mockito.when(reportCRFService.createPDFReport(eventCRFId, testLocale, dynamicsMetadataService))
				.thenCallRealMethod();
	}

	@Test
	public void testCreatePDFReportReturnsNotNull() throws Exception {
		assertNotNull(reportCRFService.createPDFReport(eventCRFId, testLocale, dynamicsMetadataService));
	}

	@Test
	public void testCreatePDFReportReturnsCorrectFileName() throws Exception {
		assertEquals(fileName, reportCRFService.createPDFReport(1, testLocale, dynamicsMetadataService));
	}
}
