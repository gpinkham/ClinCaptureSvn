/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import com.clinovo.reporting.DcfReportBuilder;

public class DcfServiceTest extends DefaultAppContextTest {

	private Locale locale;
	private StudyBean study;
	private UserAccountBean user;
	private DcfService mockedDcfService;

	@Before
	public void setUp() throws Exception {
		study = (StudyBean) studyDAO.findByPK(1);
		user = new UserAccountBean();
		user.setName("root");
		user.setUserTimeZoneId(DateTimeZone.getDefault().getID());
		locale = Locale.ENGLISH;
		mockedDcfService = Mockito.spy(dcfService);
		DcfReportBuilder mockedDcfReportBuilder = Mockito.mock(DcfReportBuilder.class);
		Whitebox.setInternalState(mockedDcfService, "dcfReportBuilder", mockedDcfReportBuilder);
	}

	@Test
	public void testThatDcfIsGeneratedWithCorrectFileName() throws Exception {
		Map<Integer, Integer> noteIds = new HashMap<Integer, Integer>();
		noteIds.put(1, 1);
		String fileName = mockedDcfService.generateDcf(study, noteIds.keySet(), user, locale);
		String expectedFileName = "print" + File.separator + "dcf" + File.separator + "S_DEFAULTS1" + File.separator
				+ "root" + File.separator + "default-study_S_DEFAULTS1_ssID1_Failed_Validation_Check_1.pdf";
		assertTrue(fileName.endsWith(expectedFileName));
	}

	@Test
	public void testThatDcfIsGeneratedAndSaved() throws Exception {
		Map<Integer, Integer> noteIds = new HashMap<Integer, Integer>();
		noteIds.put(1, 1);
		String fileName = mockedDcfService.generateDcf(study, noteIds.keySet(), user, locale);
		String expectedFileName = "print" + File.separator + "dcf" + File.separator + "S_DEFAULTS1" + File.separator
				+ "root" + File.separator + "default-study_S_DEFAULTS1_ssID1_Failed_Validation_Check_1.pdf";
		assertTrue(fileName.endsWith(expectedFileName));
	}

	@After
	public void cleanUp() throws Exception {
		File dcfUserDir = new File("dcf" + File.separator + "root");
		if (dcfUserDir.exists()) {
			if (dcfUserDir.isDirectory()) {
				for (String file : dcfUserDir.list()) {
					File fileToDelete = new File(dcfUserDir, file);
					fileToDelete.delete();
				}
				dcfUserDir.delete();
			}
		}
		new File("dcf").delete();
	}
}
