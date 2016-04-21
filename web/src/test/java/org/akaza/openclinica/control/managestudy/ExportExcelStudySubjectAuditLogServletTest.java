/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package org.akaza.openclinica.control.managestudy;

import java.util.Locale;

import com.clinovo.builder.AuditLogWorkbookBuilder;
import com.clinovo.service.AuditLogService;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.view.Page;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockRequestDispatcher;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.clinovo.i18n.LocaleResolver;

@SuppressWarnings({})
@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreResources.class, RequestContextHolder.class, AuditLogWorkbookBuilder.class})
public class ExportExcelStudySubjectAuditLogServletTest {

	@Mock
	private ServletRequestAttributes servletRequestAttributes;
	@Mock
	private MockHttpServletResponse response;
	@Mock
	private MockHttpServletRequest request;
	@Mock
	private MockServletContext servletContext;
	@Mock
	private MockRequestDispatcher requestDispatcher;

	private ExportExcelStudySubjectAuditLogServlet exportExcelStudySubjectAuditLogServlet;

	@Before
	public void setUp() throws Exception {
		exportExcelStudySubjectAuditLogServlet = PowerMockito.spy(new ExportExcelStudySubjectAuditLogServlet());
		MockHttpSession session = new MockHttpSession();
		AuditLogService mockedService = PowerMockito.mock(AuditLogService.class);
		Mockito.when(request.getSession()).thenReturn(session);

		Locale locale = Locale.ENGLISH;
		PowerMockito.mockStatic(CoreResources.class);
		PowerMockito.when(CoreResources.getSystemLocale()).thenReturn(locale);

		PowerMockito.mockStatic(RequestContextHolder.class);
		Whitebox.setInternalState(servletRequestAttributes, "request", request);
		PowerMockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(servletRequestAttributes);

		LocaleResolver.resolveLocale();

		Mockito.when(servletContext.getRequestDispatcher(Mockito.any(String.class))).thenReturn(requestDispatcher);
		Mockito.doReturn(servletContext).when(exportExcelStudySubjectAuditLogServlet).getServletContext();
		Mockito.doReturn(mockedService).when(exportExcelStudySubjectAuditLogServlet).getAuditLogService(servletContext);
	}

	@Test
	public void testThatServletForwardsToListStudySubjectServletIfThereIsNoAStudySubIdInTheRequest() throws Exception {
		exportExcelStudySubjectAuditLogServlet.processRequest(request, response);
		Mockito.verify(servletContext).getRequestDispatcher(Page.LIST_STUDY_SUBJECTS.getFileName());
		Mockito.verify(requestDispatcher).forward(request, response);
	}
}
