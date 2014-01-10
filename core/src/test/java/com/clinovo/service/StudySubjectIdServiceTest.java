package com.clinovo.service;

import org.akaza.openclinica.DefaultAppContextTest;
import org.junit.Test;

public class StudySubjectIdServiceTest extends DefaultAppContextTest {

    @Test
    public void testThatGetNextStudySubjectIdForExistingNameReturns2() {
        assertNotNull(studySubjectIdService.getNextStudySubjectId("TEST1"));
    }

    @Test
    public void testThatGetNextStudySubjectIdForNonExistingNameReturns1() {
        assertNotNull(studySubjectIdService.getNextStudySubjectId("NEWTEST1"));
    }
}
