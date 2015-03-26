package com.clinovo.interceptor;

import com.clinovo.util.SessionUtil;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.BaseController;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.web.print.ODMClinicaDataResource;
import org.akaza.openclinica.web.print.RestODMFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

@RunWith(PowerMockRunner.class)
public class RestODMFilterTest {

    public static final String QUERY = "S_STUDY/SS_SSID1";

    @Mock
    private RestODMFilter restODMFilter;
    @Mock
    private ODMClinicaDataResource clinicaDataResource;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private StudyBean studyBean;
    private UserAccountBean userBean;

    @Before
    public void setUp() throws Exception {
        MockHttpSession session = new MockHttpSession();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        userBean = new UserAccountBean();
        userBean.setId(1);
        studyBean = new StudyBean();
        studyBean.setId(1);
        request.setRequestURI(QUERY);
        request.setSession(session);
        request.getSession().setAttribute(BaseController.USER_BEAN_NAME, userBean);
        request.getSession().setAttribute(SessionUtil.CURRENT_SESSION_LOCALE, Locale.ENGLISH);
    }

    @Test
    public void testThatFilterNotPassesUserWithInvalidRole() throws Exception {
        PowerMockito.doCallRealMethod().when(restODMFilter)
                .preHandle(request, response, clinicaDataResource);
        DataSource ds = Mockito.mock(DataSource.class);
        StudyDAO studyDAO = Mockito.mock(StudyDAO.class);
        StudySubjectDAO studySubjectDAO = Mockito.mock(StudySubjectDAO.class);
        StudySubjectBean studySubjectBean = new StudySubjectBean();
        Mockito.when(restODMFilter.getDataSource()).thenReturn(ds);
        Mockito.when(restODMFilter.getStudyDAO()).thenReturn(studyDAO);
        Mockito.when(restODMFilter.getStudySubjectDAO()).thenReturn(studySubjectDAO);
        Mockito.when(studySubjectDAO.findByOid(Mockito.anyString())).thenReturn(studySubjectBean);
        Mockito.when(studyDAO.findByStudySubjectId(Mockito.anyInt())).thenReturn(studyBean);

        assertEquals(false, restODMFilter.preHandle(request, response, clinicaDataResource));
    }
}
