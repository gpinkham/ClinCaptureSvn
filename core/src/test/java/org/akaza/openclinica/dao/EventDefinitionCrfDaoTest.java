package org.akaza.openclinica.dao;

import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class EventDefinitionCrfDaoTest extends DefaultAppContextTest {

    @Test
    public void testGetRequiredEventCRFDefIdsThatShouldBeSDVd() throws OpenClinicaException {
        StudyBean studyBean = (StudyBean)studyDAO.findByPK(1);
        List<Integer> result = eventDefinitionCRFDAO.getRequiredEventCRFDefIdsThatShouldBeSDVd(studyBean);
        assertEquals(result.size(), 0);
    }
}
