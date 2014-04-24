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

import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.junit.Test;
import org.mockito.Mockito;

public class ReportCRFServiceTest extends DefaultAppContextTest {
	@Test
	public void testCreatePDFReportReturnsNotNull() throws Exception {
		StudyBean currentStudy = (StudyBean) studyDAO.findByPK(1);
		int eventCRFId = 1;
		SessionManager sm = Mockito.mock(SessionManager.class);
		Mockito.when(sm.getDataSource()).thenReturn(dataSource);
		String urlPath = "";
		String sysPath = "";
		String dataPath = "";
		Locale testLocale = new Locale(locale);
		ResourceBundle resword = ResourceBundleProvider.getWordsBundle(testLocale);
		assertNotNull(reportCRFService.createPDFReport(eventCRFId, currentStudy, testLocale, resword, urlPath, sysPath,
				dataPath, sm));
	}

	@Test
	public void testCreatePDFReportReturnsCorrectFileName() throws Exception {
		SessionManager sm = Mockito.mock(SessionManager.class);
		Mockito.when(sm.getDataSource()).thenReturn(dataSource);

		Locale testLocale = new Locale(locale);
		ResourceBundle resword = ResourceBundleProvider.getWordsBundle(testLocale);
		assertEquals("Agent_Administration_v2.0_ssID1.pdf", reportCRFService.createPDFReport(1,
				(StudyBean) studyDAO.findByPK(1), testLocale, resword, "", "", "", sm));
	}
}
