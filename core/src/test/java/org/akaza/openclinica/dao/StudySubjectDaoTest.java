package org.akaza.openclinica.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

public class StudySubjectDaoTest extends DefaultAppContextTest {

    @Test
    public void testAllowSDVSubject() throws OpenClinicaException {
        boolean result = studySubjectDAO.allowSDVSubject(1, 1, 1);
        assertFalse(result);
    }
}
