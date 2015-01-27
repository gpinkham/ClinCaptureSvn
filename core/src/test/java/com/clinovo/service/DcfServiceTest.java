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
package com.clinovo.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.After;
import org.junit.Test;

public class DcfServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatDcfIsGeneratedWithCorrectFileName() throws Exception {
		List<Integer> noteIds = new ArrayList<Integer>();
		noteIds.add(1);
		String fileName = dcfService.generateDcf(noteIds);
		String expectedFileName = "dcf" + File.separator + "default-study_S_DEFAULTS1_Failed_Validation_Check_1.pdf";
		assertEquals(expectedFileName, fileName);
	}
	
	@Test
	public void testThatDcfIsGeneratedAndSaved() throws Exception {
		List<Integer> noteIds = new ArrayList<Integer>();
		noteIds.add(1);
		dcfService.generateDcf(noteIds);
		File dcfFile = new File("dcf" + File.separator + "default-study_S_DEFAULTS1_Failed_Validation_Check_1.pdf");
		assertTrue(dcfFile.exists());
	}

	@After
	public void cleanUp() throws Exception {
		File dcfDir = new File("dcf");
		if (dcfDir.exists()) {
			if (dcfDir.isDirectory()) {
				for (String file : dcfDir.list()) {
					File fileToDelete = new File(dcfDir, file);
					fileToDelete.delete();
				}
				dcfDir.delete();
			}
		}
	}
}
