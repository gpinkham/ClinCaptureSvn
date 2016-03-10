package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.web.bean.ListCRFRow;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * ListCRFService Implementation Test.
 */
public class ListCRFServiceImplTest extends DefaultAppContextTest {

	@Autowired
	ListCRFService listCRFService;

	@Test
	public void testThatGenerateRowsFromBeansReturnsBeansWithStudies() {
		ArrayList<CRFBean> crfBeans = new ArrayList<CRFBean>();
		CRFBean crf = new CRFBean();
		crf.setId(1);
		crfBeans.add(crf);
		ArrayList<ListCRFRow> rows = listCRFService.generateRowsFromBeans(crfBeans);
		CRFBean resultBean = (CRFBean) rows.get(0).getBean();
		assertEquals(1, resultBean.getStudiesWhereUsed().size());
	}
}
