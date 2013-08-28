package com.clinovo.clincapture.control.managestudy;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.akaza.openclinica.bean.submit.crfdata.CRFDataPostImportContainer;
import org.akaza.openclinica.bean.submit.crfdata.FormDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ImportItemGroupDataBean;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.bean.submit.crfdata.StudyEventDataBean;
import org.akaza.openclinica.bean.submit.crfdata.SubjectDataBean;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

public class DataImportServletTest {
	
	protected InputStream stream;
	
	@Before
	public void setUp() throws Exception {
		stream = this.getClass().getClassLoader().getResourceAsStream("com/clinovo/DataImportServletXmlTest.xml");
	}
	
	@Test
	public void DataImportTest() throws Exception {
		ODMContainer odmContainer = new ODMContainer();
		JAXBContext jaxbContext = JAXBContext.newInstance(ODMContainer.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		if (stream != null) {
			InputSource inputSource = new InputSource(stream);
			SAXSource saxSource = new SAXSource(inputSource);
			try {
				odmContainer = (ODMContainer) jaxbUnmarshaller.unmarshal(saxSource);
				CRFDataPostImportContainer container = odmContainer.getCrfDataPostImportContainer();
				ArrayList<SubjectDataBean> sdb = container.getSubjectData();
				ArrayList<StudyEventDataBean> sedBean = sdb.get(0).getStudyEventData();
				ArrayList<FormDataBean> formDataBean = sedBean.get(0).getFormData();
				ArrayList<ImportItemGroupDataBean> itemGroupData = formDataBean.get(0).getItemGroupData();
				ArrayList<ImportItemDataBean> importItemDataBean = itemGroupData.get(0).getItemData();
				assertEquals(container.getStudyOID(), "S_001");
				assertEquals(sdb.get(0).getSubjectOID(), "SS_001000");
				assertEquals(sedBean.get(0).getStudyEventOID(), "SE_EVENT");
				assertEquals(formDataBean.get(0).getFormOID(), "F_FORM_0");
				assertEquals(itemGroupData.get(0).getItemGroupOID(), "IG_FORM_UNGROUPED");
				assertEquals(importItemDataBean.get(0).getItemOID(), "I_FORM_RCPT001_TXT_SUBINT");
				assertEquals(importItemDataBean.get(0).getValue(), "NPC");
				assertEquals(importItemDataBean.get(1).getItemOID(), "I_FORM_RCPT001_TXT_SAMPLEID");
				assertEquals(importItemDataBean.get(1).getValue(), "70792");
				assertEquals(importItemDataBean.get(2).getItemOID(), "I_FORM_RCPT001_RBD_SAMPREC");
				assertEquals(importItemDataBean.get(2).getValue(), "1");
			} catch (Exception e) {
				fail("Unmarshaller exception: " +e.getMessage());
			}
		} else {
				fail("Import xml not found");
		}

	}
}
