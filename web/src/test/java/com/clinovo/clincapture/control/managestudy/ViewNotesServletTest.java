package com.clinovo.clincapture.control.managestudy;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.managestudy.ViewNotesServlet;
import org.akaza.openclinica.control.submit.ListNotesTableFactory;
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

import com.clinovo.service.DcfService;
import com.clinovo.service.impl.DcfServiceImpl;
import com.clinovo.util.DcfRenderType;
import com.clinovo.util.SessionUtil;
import com.google.common.collect.Iterables;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ViewNotesServletTest extends DefaultAppContextTest {

	public static final int FIVE = 5;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private ViewNotesServlet viewNotesServlet;
	private UserAccountBean currentUser;
	private StudyUserRoleBean currentRole;
	private DcfService dcfService;
	private StudyBean currentStudy;

	private ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle();
	private ResourceBundle resexception = ResourceBundleProvider.getExceptionsBundle();
	private ResourceBundle resterm = ResourceBundleProvider.getTermsBundle();
	private ResourceBundle resword = ResourceBundleProvider.getWordsBundle();

	@Before
	public void setUp() throws Exception {

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		viewNotesServlet = Mockito.mock(ViewNotesServlet.class);
		dcfService = Mockito.mock(DcfServiceImpl.class);
		currentStudy = new StudyBean();
		currentStudy.setId(1);
		currentStudy.setName("Demo Study");
		currentStudy.setStatus(Status.AVAILABLE);
		request.setParameter("print", "no");
		request.setParameter("module", "admin");
		request.setParameter("showMoreLink", "true");
		request.setParameter("oneSubjectId", "1");
		SessionUtil.updateLocale(request.getSession(), Locale.ENGLISH);
		ResourceBundleProvider.updateLocale(SessionUtil.getLocale(request));
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("root", "root"));
		request.getSession().setAttribute("study", currentStudy);
		currentUser = new UserAccountBean();
		currentUser.addUserType(UserType.USER);
		currentRole = new StudyUserRoleBean();
		currentRole.setRole(Role.STUDY_MONITOR);
		Mockito.doCallRealMethod().when(viewNotesServlet).mayProceed(request, response);
		Mockito.doCallRealMethod().when(viewNotesServlet).processRequest(request, response);
		Mockito.doReturn(currentStudy).when(viewNotesServlet).getCurrentStudy(request);
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
		Mockito.doReturn(dcfService).when(viewNotesServlet).getDcfService();
		Whitebox.setInternalState(viewNotesServlet, "respage", respage);
		Whitebox.setInternalState(viewNotesServlet, "resterm", resterm);
		Whitebox.setInternalState(viewNotesServlet, "resexception", resexception);
		Whitebox.setInternalState(viewNotesServlet, "resword", resword);
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
	
	@Test
	public void testThatPrintDcfOptionStoresVariableInSession() throws Exception {
		request.setParameter(ViewNotesServlet.GENERATE_DCF, "yes");
		request.setParameter(ListNotesTableFactory.DCF_CHECKBOX_NAME, "1___1___value");
		request.setParameter(ViewNotesServlet.DCF_RENDER_CHECKBOX_NAME, "print");
		Set<Integer> noteIds = new HashSet<Integer>();
		noteIds.add(1);
		Mockito.doReturn("myfile.pdf").when(dcfService).generateDcf(currentStudy, noteIds, currentUser.getName());
		Mockito.doCallRealMethod().when(dcfService).addDcfRenderType(Mockito.any(DcfRenderType.class));
		Mockito.doCallRealMethod().when(dcfService).clearRenderTypes();
		Mockito.doCallRealMethod().when(dcfService).renderDcf();
		viewNotesServlet.processRequest(request, response);
		assertNotNull(request.getAttribute(ViewNotesServlet.PRINT_DCF));
	}
	
	@Test
	public void testThatSavDcfOptionStoresVariableInSession() throws Exception {
		request.setParameter(ViewNotesServlet.GENERATE_DCF, "yes");
		request.setParameter(ListNotesTableFactory.DCF_CHECKBOX_NAME, "1___1___value");
		request.setParameter(ViewNotesServlet.DCF_RENDER_CHECKBOX_NAME, "save");
		Set<Integer> noteIds = new HashSet<Integer>();
		noteIds.add(1);
		Mockito.doReturn("myfile.pdf").when(dcfService).generateDcf(currentStudy, noteIds, currentUser.getName());
		Mockito.doCallRealMethod().when(dcfService).addDcfRenderType(Mockito.any(DcfRenderType.class));
		Mockito.doCallRealMethod().when(dcfService).clearRenderTypes();
		Mockito.doCallRealMethod().when(dcfService).renderDcf();
		viewNotesServlet.processRequest(request, response);
		assertNotNull(request.getAttribute(ViewNotesServlet.SAVE_DCF));
	}
}
