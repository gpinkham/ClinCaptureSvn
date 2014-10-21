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
package org.akaza.openclinica.control.form;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.junit.Before;
import org.junit.Test;

public class FormDiscrepancyNotesTest {

	private FormDiscrepancyNotes fdn;

	@Before
	public void setUp() {
		fdn = new FormDiscrepancyNotes();
		DiscrepancyNoteBean dn = new DiscrepancyNoteBean();
		dn.setField("field1");
		dn.setDiscrepancyNoteTypeId(DiscrepancyNoteType.QUERY.getId());
		fdn.addNote("field1", dn);
		dn = new DiscrepancyNoteBean();
		dn.setField("field2");
		dn.setDiscrepancyNoteTypeId(DiscrepancyNoteType.ANNOTATION.getId());
		fdn.addNote("field2", dn);
		dn = new DiscrepancyNoteBean();
		dn.setField("field3");
		dn.setDiscrepancyNoteTypeId(DiscrepancyNoteType.REASON_FOR_CHANGE.getId());
		fdn.addNote("field3", dn);
	}

	@Test
	public void testThatAddRFCsAddsNonExistingNotes() {
		List<DiscrepancyNoteBean> annotations = new ArrayList<DiscrepancyNoteBean>();
		DiscrepancyNoteBean dn = new DiscrepancyNoteBean();
		dn.setField("field4");
		dn.setDiscrepancyNoteTypeId(DiscrepancyNoteType.REASON_FOR_CHANGE.getId());
		annotations.add(dn);
		dn = new DiscrepancyNoteBean();
		dn.setField("field5");
		dn.setDiscrepancyNoteTypeId(DiscrepancyNoteType.REASON_FOR_CHANGE.getId());
		annotations.add(dn);
		fdn.addAutoRFCs(annotations);
		assertEquals(5, fdn.getFieldNotes().size());
	}

	@Test
	public void testThatAddRFCsOmitsExistingNotes() {
		List<DiscrepancyNoteBean> annotations = new ArrayList<DiscrepancyNoteBean>();
		DiscrepancyNoteBean dn = new DiscrepancyNoteBean();
		dn.setField("field3");
		dn.setDiscrepancyNoteTypeId(DiscrepancyNoteType.REASON_FOR_CHANGE.getId());
		annotations.add(dn);
		dn = new DiscrepancyNoteBean();
		dn.setField("field4");
		dn.setDiscrepancyNoteTypeId(DiscrepancyNoteType.ANNOTATION.getId());
		annotations.add(dn);
		fdn.addAutoRFCs(annotations);
		assertEquals(4, fdn.getFieldNotes().size());
	}

	@Test
	public void testThatAddRFCsAddsForFieldsWithExistingQuery() {
		List<DiscrepancyNoteBean> annotations = new ArrayList<DiscrepancyNoteBean>();
		DiscrepancyNoteBean dn = new DiscrepancyNoteBean();
		dn.setField("field1");
		dn.setDiscrepancyNoteTypeId(DiscrepancyNoteType.REASON_FOR_CHANGE.getId());
		annotations.add(dn);
		fdn.addAutoRFCs(annotations);
		assertEquals(2, fdn.getNotes("field1").size());
	}
}
