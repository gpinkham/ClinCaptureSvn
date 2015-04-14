package com.clinovo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.junit.Test;

/**
 * EventDefinitionCrfServiceTest.
 */
public class EventDefinitionCrfServiceTest extends DefaultAppContextTest {

	@Test
	public void testThatUpdateChildEventDefinitionCrfsForNewCrfVersionSetsCorrectValues() throws Exception {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(3);
		UserAccountBean updater = (UserAccountBean) userAccountDAO.findByPK(1);

		EventDefinitionCRFBean eventDefinitionCrfBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		eventDefinitionCrfBean.setCrfId(2);
		eventDefinitionCrfBean.setUpdater(updater);
		eventDefinitionCrfBean.setDefaultVersionId(3);
		eventDefinitionCrfBean.setSelectedVersionIds("");
		eventDefinitionCRFDAO.update(eventDefinitionCrfBean);

		EventDefinitionCRFBean childEventDefinitionCrfBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(8);
		childEventDefinitionCrfBean.setCrfId(2);
		childEventDefinitionCrfBean.setUpdater(updater);
		childEventDefinitionCrfBean.setDefaultVersionId(2);
		childEventDefinitionCrfBean.setSelectedVersionIds("2");
		childEventDefinitionCrfBean.setAcceptNewCrfVersions(true);
		childEventDefinitionCrfBean.setParentId(eventDefinitionCrfBean.getId());
		eventDefinitionCRFDAO.update(childEventDefinitionCrfBean);

		eventDefinitionCrfService.updateChildEventDefinitionCrfsForNewCrfVersion(crfVersionBean, updater);

		childEventDefinitionCrfBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(8);
		assertTrue(childEventDefinitionCrfBean.getDefaultVersionId() == 2);
		assertTrue(childEventDefinitionCrfBean.getSelectedVersionIds().equals("2,3"));
	}

	@Test
	public void testThatUpdateChildEventDefinitionCRFsSetsCorrectValues() throws Exception {
		UserAccountBean updater = (UserAccountBean) userAccountDAO.findByPK(1);

		List<EventDefinitionCRFBean> childEventDefCRFs = new ArrayList<EventDefinitionCRFBean>();
		Map<Integer, EventDefinitionCRFBean> parentsMap = new HashMap<Integer, EventDefinitionCRFBean>();

		EventDefinitionCRFBean eventDefinitionCrfBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(1);
		eventDefinitionCrfBean.setUpdater(updater);
		eventDefinitionCrfBean.setDefaultVersionId(1);
		eventDefinitionCrfBean.setSelectedVersionIds("");
		eventDefinitionCRFDAO.update(eventDefinitionCrfBean);
		parentsMap.put(eventDefinitionCrfBean.getId(), eventDefinitionCrfBean);

		EventDefinitionCRFBean childEventDefinitionCrfBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(8);
		childEventDefinitionCrfBean.setUpdater(updater);
		childEventDefinitionCrfBean.setDefaultVersionId(2);
		childEventDefinitionCrfBean.setSelectedVersionIds("2");
		childEventDefinitionCrfBean.setParentId(eventDefinitionCrfBean.getId());
		eventDefinitionCRFDAO.update(childEventDefinitionCrfBean);
		childEventDefCRFs.add(childEventDefinitionCrfBean);

		eventDefinitionCrfService.updateChildEventDefinitionCRFs(childEventDefCRFs, parentsMap, updater);

		childEventDefinitionCrfBean = (EventDefinitionCRFBean) eventDefinitionCRFDAO.findByPK(8);
		assertTrue(childEventDefinitionCrfBean.getDefaultVersionId() == 1);
		assertTrue(childEventDefinitionCrfBean.getSelectedVersionIds().equals("2,1"));
	}

}
