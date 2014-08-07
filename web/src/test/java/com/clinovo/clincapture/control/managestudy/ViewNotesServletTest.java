package com.clinovo.clincapture.control.managestudy;

import com.google.common.collect.Iterables;
import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.managestudy.ViewNotesServlet;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ViewNotesServletTest extends DefaultAppContextTest {

	public static final int FIVE = 5;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ViewNotesServlet viewNotesServlet;
	private UserAccountBean currentUser;
	private StudyUserRoleBean currentRole;

	private ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle();
	private ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
	private ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();

	@Before
	public void setUp() throws Exception {

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		viewNotesServlet = Mockito.mock(ViewNotesServlet.class);
		StudyBean studyBean = new StudyBean();
		studyBean.setId(1);
		studyBean.setName("Demo Study");
		studyBean.setStatus(Status.AVAILABLE);
		ResourceBundleProvider.updateLocale(request.getLocale());
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("root", "root"));
		request.setParameter("print", "no");
		request.setParameter("module", "admin");
		request.setParameter("showMoreLink", "true");
		request.setParameter("oneSubjectId", "1");
		request.setPreferredLocales(Arrays.asList(new Locale("en")));
		request.getSession().setAttribute("study", studyBean);
		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.STUDY_MONITOR);
		Mockito.doCallRealMethod().when(viewNotesServlet).mayProceed(request, response);
		Mockito.doCallRealMethod().when(viewNotesServlet).processRequest(request, response);
		Mockito.doReturn(studyBean).when(viewNotesServlet).getCurrentStudy(request);
		Mockito.doReturn(currentUser).when(viewNotesServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(viewNotesServlet).getCurrentRole(request);
		Mockito.doReturn(dataSource).when(viewNotesServlet).getDataSource();
		Mockito.doReturn(studyEventDao).when(viewNotesServlet).getStudyEventDAO();
		Mockito.doReturn(studySubjectDAO).when(viewNotesServlet).getStudySubjectDAO();
		Mockito.doReturn(studyEventDefinitionDAO).when(viewNotesServlet).getStudyEventDefinitionDAO();
		Mockito.doReturn(studyDAO).when(viewNotesServlet).getStudyDAO();
		Mockito.doReturn(eventDefinitionCRFDAO).when(viewNotesServlet).getEventDefinitionCRFDAO();
		Mockito.doReturn(eventCRFDAO).when(viewNotesServlet).getEventCRFDAO();
		Mockito.doReturn(itemDataDAO).when(viewNotesServlet).getItemDataDAO();
		Mockito.doReturn(crfdao).when(viewNotesServlet).getCRFDAO();
		Mockito.doReturn(crfVersionDao).when(viewNotesServlet).getCRFVersionDAO();
		Mockito.doReturn(userAccountDAO).when(viewNotesServlet).getUserAccountDAO();
		Mockito.doReturn(idao).when(viewNotesServlet).getItemDAO();
		Mockito.doReturn(itemGroupMetadataDAO).when(viewNotesServlet).getItemGroupMetadataDAO();
		Whitebox.setInternalState(viewNotesServlet, "respage", respage);
		Whitebox.setInternalState(viewNotesServlet, "resterm", resterm);
		Whitebox.setInternalState(viewNotesServlet, "resexception", resexception);
		Whitebox.setInternalState(viewNotesServlet, "logger", LoggerFactory.getLogger("ViewNotesServlet"));

	}

	@Test(expected = InsufficientPermissionException.class)
	public void testThatViewNotesServletDoesNotGrantAccessToInvalidUserRole() throws Exception {
		currentRole.setRole(Role.INVALID);
		Mockito.doReturn(currentUser).when(viewNotesServlet).getUserAccountBean(request);
		Mockito.doReturn(currentRole).when(viewNotesServlet).getCurrentRole(request);
		viewNotesServlet.mayProceed(request, response);
	}

	@Test
	public void testThatViewNotesServletReturnsNotNullRequestFields() throws Exception {
		viewNotesServlet.processRequest(request, response);
		assertNotNull(request.getAttribute("grandTotal"));
		assertNotNull(request.getAttribute("typeKeys"));
		assertNotNull(request.getAttribute("summaryMap"));
		assertNotNull(request.getAttribute("mapKeys"));
		assertNotNull(request.getAttribute("viewNotesHtml"));
	}

	@Test
	public void testThatViewNotesServletReturnsCorrectTotalNumberOfItems() throws Exception {
		viewNotesServlet.processRequest(request, response);
		int grandTotal = Integer.parseInt(request.getAttribute("grandTotal").toString());
		assertEquals(FIVE, grandTotal);
	}

	@Test
	public void testThatViewNotesServletReturnsCorrectStatusesInSummaryMap() throws Exception {
		viewNotesServlet.processRequest(request, response);
		Map<String, Map<String, String>> customStat = (Map<String, Map<String, String>>) request
				.getAttribute("summaryMap");
		Map.Entry entry0 = Iterables.get(customStat.entrySet(), 0);
		Map.Entry entry1 = Iterables.get(customStat.entrySet(), 1);
		Map.Entry entry2 = Iterables.get(customStat.entrySet(), 2);
		Map.Entry entry3 = Iterables.get(customStat.entrySet(), THREE);
		Map.Entry entry4 = Iterables.get(customStat.entrySet(), FOUR);
		assertEquals("Updated", entry0.getKey());
		assertEquals("Not Applicable", entry1.getKey());
		assertEquals("Closed", entry2.getKey());
		assertEquals("New", entry3.getKey());
		assertEquals("Resolution Proposed", entry4.getKey());
	}

	@Test
	public void testThatViewNotesServletReturnsCorrectNumberOfUpdatedItems() throws Exception {
		viewNotesServlet.processRequest(request, response);
		Map<String, Map<String, String>> customStat = (Map<String, Map<String, String>>) request
				.getAttribute("summaryMap");
		Map.Entry updatedItems = Iterables.get(customStat.entrySet(), 0);
		Map<String, String> statusItems = (Map<String, String>) updatedItems.getValue();
		assertEquals("Query", Iterables.get(statusItems.entrySet(), 0).getKey());
		assertEquals("1", Iterables.get(statusItems.entrySet(), 0).getValue());
		assertEquals("Total", Iterables.get(statusItems.entrySet(), 1).getKey());
		assertEquals("1", Iterables.get(statusItems.entrySet(), 1).getValue());
	}

	@Test
	public void testThatViewNotesServletReturnsCorrectNumberOfTotalItems() throws Exception {
		viewNotesServlet.processRequest(request, response);
		Map<String, Map<String, String>> customStat = (Map<String, Map<String, String>>) request
				.getAttribute("summaryMap");
		Map.Entry updatedItems = Iterables.get(customStat.entrySet(), 1);
		Map<String, String> statusItems = (Map<String, String>) updatedItems.getValue();
		assertEquals("Annotation", Iterables.get(statusItems.entrySet(), 0).getKey());
		assertEquals("1", Iterables.get(statusItems.entrySet(), 0).getValue());
		assertEquals("Total", Iterables.get(statusItems.entrySet(), 1).getKey());
		assertEquals("1", Iterables.get(statusItems.entrySet(), 1).getValue());
	}

	@Test
	public void testThatViewNotesServletReturnsCorrectNumberOfClosedItems() throws Exception {
		viewNotesServlet.processRequest(request, response);
		Map<String, Map<String, String>> customStat = (Map<String, Map<String, String>>) request
				.getAttribute("summaryMap");
		Map.Entry updatedItems = Iterables.get(customStat.entrySet(), 2);
		Map<String, String> statusItems = (Map<String, String>) updatedItems.getValue();
		assertEquals("Total", Iterables.get(statusItems.entrySet(), 0).getKey());
		assertEquals("1", Iterables.get(statusItems.entrySet(), 0).getValue());
		assertEquals("Failed Validation Check", Iterables.get(statusItems.entrySet(), 1).getKey());
		assertEquals("1", Iterables.get(statusItems.entrySet(), 1).getValue());
	}

	@Test
	public void testThatViewNotesServletReturnsCorrectNumberOfNewItems() throws Exception {
		viewNotesServlet.processRequest(request, response);
		Map<String, Map<String, String>> customStat = (Map<String, Map<String, String>>) request
				.getAttribute("summaryMap");
		Map.Entry updatedItems = Iterables.get(customStat.entrySet(), THREE);
		Map<String, String> statusItems = (Map<String, String>) updatedItems.getValue();
		assertEquals("Query", Iterables.get(statusItems.entrySet(), 0).getKey());
		assertEquals("1", Iterables.get(statusItems.entrySet(), 0).getValue());
		assertEquals("Total", Iterables.get(statusItems.entrySet(), 1).getKey());
		assertEquals("2", Iterables.get(statusItems.entrySet(), 1).getValue());
		assertEquals("Failed Validation Check", Iterables.get(statusItems.entrySet(), 2).getKey());
		assertEquals("1", Iterables.get(statusItems.entrySet(), 2).getValue());
	}

	@Test
	public void testThatViewNotesServletReturnsCorrectNumberOfResolutionProposedItems() throws Exception {
		viewNotesServlet.processRequest(request, response);
		Map<String, Map<String, String>> customStat = (Map<String, Map<String, String>>) request
				.getAttribute("summaryMap");
		Map.Entry updatedItems = Iterables.get(customStat.entrySet(), FOUR);
		Map<String, String> statusItems = (Map<String, String>) updatedItems.getValue();
		assertEquals("Total", Iterables.get(statusItems.entrySet(), 0).getKey());
		assertEquals("0", Iterables.get(statusItems.entrySet(), 0).getValue());
	}
}
