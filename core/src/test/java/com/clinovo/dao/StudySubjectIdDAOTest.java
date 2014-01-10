package com.clinovo.dao;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class StudySubjectIdDAOTest extends DefaultAppContextTest {

    @Test
    public void testThatFindByIdDoesNotReturnNull() {
        assertNotNull(studySubjectIdDAO.findById(1));
    }

    @Test
    public void testThatGetNextStudySubjectIdForExistingNameReturns2() {
        assertNotNull(studySubjectIdDAO.getNextStudySubjectId("TEST1"));
    }

    @Test
    public void testThatGetNextStudySubjectIdForNonExistingNameReturns1() {
        assertNotNull(studySubjectIdDAO.getNextStudySubjectId("NEWTEST1"));
    }
}
