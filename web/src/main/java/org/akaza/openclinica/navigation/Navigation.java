/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.navigation;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * Navigation class.
 */
@SuppressWarnings({ "unchecked" })
public final class Navigation {

	// "skip!"-set of pages, non pop-ups
	private static Set<String> exclusionURLs = new HashSet<String>(Arrays.asList("/PageToCreateNewStudyEvent",
			"/CRFListForStudyEvent", "/VerifyImportedCRFData", "/UpdateStudySubject", "/RemoveStudySubject",
			"/ResolveDiscrepancy", "/RestoreStudySubject", "/UpdateStudyEvent", "/RemoveStudyEvent",
			"/RestoreStudyEvent", "/DeleteEventCRF", "/RemoveEventCRF", "/RestoreEventCRF", "/InitUpdateCRF",
			"/InitUpdateSubStudy", "/UpdateSubStudy", "/DeleteStudyEvent", "/EditStudyUserRole",
			"/ViewSectionDataEntry", "/CreateSubjectGroupClass", "/SetStudyUserRole", "/UpdateProfile",
			"/SectionPreview", "/DefineStudyEvent", "/InitUpdateEventDefinition", "/UpdateEventDefinition",
			"/RemoveEventDefinition", "/RemoveSubject", "/RemoveStudy", "/ViewUserAccount", "/EditUserAccount",
			"/SetUserRole", "/ViewUserAccount", "/Configure", "/CreateUserAccount", "/UpdateJobImport",
			"/CreateJobExport", "/CreateJobImport", "/UpdateProfile", "/RemoveDataset", "/LockStudySubject",
			"/pages/extract", "/CreateNewStudyEvent", "/UpdateSubject", "/UpdateSubjectGroupClass",
			"/ViewSubjectGroupClass", "/RemoveSubjectGroupClass", "/RestoreSubjectGroupClass", "/UpdateJobExport",
			"/EditDataset", "/RemoveSite", "/RestoreSite", "/extract", "/ViewSelected", "/SelectItems",
			"/CreateDataset", "/EditSelected", "/EditDataset", "/pages/managestudy/chooseCRFVersion",
			"/pages/managestudy/confirmCRFVersionChange", "/pages/managestudy/changeCRFVersion", "/CreateCRFVersion",
			"/RemoveCRF", "/RemoveCRFVersion", "/RestoreCRF", "/RestoreCRFVersion", "/DeleteCRFVersion",
			"/LockCRFVersion", "/UnlockCRFVersion", "/CreateSubStudy", "/RemoveCRFFromDefinition",
			"/RestoreCRFFromDefinition", "/AddCRFToDefinition"));
	// ignored-set of pages, pop-ups or like pop-ups
	private static Set<String> exclusionPopUpURLs = new HashSet<String>(Arrays.asList("/ViewStudySubjectAuditLog",
			"/PrintAllEventCRF", "/PrintDataEntry", "/DiscrepancyNoteOutputServlet", "/PrintDataEntry",
			"/ViewItemDetail", "/PrintCRF", "/PrintEventCRF", "/ViewRulesAssignment", "/SelectItems",
			"/DownloadRuleSetXml", "/UpdateRuleSetRule", "/pages/handleSDVGet", "/DownloadVersionSpreadSheet",
			"/PrintAllSiteEventCRF", "/DeleteUser", "/UnLockUser", "/DeleteStudyUserRole", "/PauseJob",
			"/CreateDiscrepancyNote", "/confirmCRFVersionChange", "/ViewDiscrepancyNote", "/AccessFile",
			"/PrintSubjectCaseBook", "/ExportExcelStudySubjectAuditLog", "/ShowCalendarFunc", "/help",
			"/ViewCalendaredEventsForSubject", "/ResetPassword", "/pages/cancelScheduledJob", "/CRFListForStudyEvent",
			"/ChangeDefinitionCRFOrdinal", "/DoubleDataEntry", "/CreateOneDiscrepancyNote", "/MatchPassword",
			"/pages/handleSDVPost", "/pages/handleSDVRemove", "/CompleteCrfDelete", "/pages/sdvStudySubjects",
			"/InitialDataEntry", "/AdministrativeEditing"));
	// set of pages with special processing
	private static Set<String> specialURLs = new HashSet<String>(Arrays.asList("/ListEventsForSubjects"));
	private static String defaultShortURL = "/MainMenu";

	private Navigation() {
	}

	/**
	 * Removes a url from the visitedURLs.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param excludeUrl
	 *            String
	 * @throws Exception
	 *             an Exception
	 */
	public static void removeUrl(HttpServletRequest request, String excludeUrl) throws Exception {
		Stack<String> visitedURLs = (Stack<String>) request.getSession().getAttribute("visitedURLs");
		if (visitedURLs != null) {
			Iterator<String> iterator = visitedURLs.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().toLowerCase().startsWith(excludeUrl.toLowerCase())) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * Here send/receive logic of visitedURLs-stack is accumulated. You can add transfer-logic here.
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public static void addToNavigationStack(HttpServletRequest request) {
		String urlPrefix = request.getContextPath();
		// for the case when back-button pressed after session died
		request.getSession().setAttribute("defaultURL", urlPrefix + defaultShortURL);
		request.getSession().setAttribute("navigationURL", urlPrefix + "/HelpNavigation");

		if (!"true".equals(request.getSession().getAttribute("skipURL"))) {
			Stack<String> visitedURLs = new Stack<String>();
			if (request.getSession().getAttribute("visitedURLs") != null) {
				visitedURLs = (Stack<String>) request.getSession().getAttribute("visitedURLs");
			} else {
				if (request.getRequestURL().toString().endsWith("/AccessFile")) {
					request.getSession().setAttribute("redirectAfterLogin",
							request.getRequestURL().toString().concat("?").concat(request.getQueryString()));
				}
			}
			processRequestURL(visitedURLs, request);
			request.getSession().setAttribute("visitedURLs", visitedURLs);

		} else {
			request.getSession().setAttribute("skipURL", "false");
		}
	}

	/*
	 * Here incoming request-URLs are (or aren't) added to visitedURLs-stack. You can add business-logic here.
	 */
	private static void processRequestURL(Stack<String> visitedURLs, HttpServletRequest request) {
		// delete contextPath part of URL to make saved links shorter
		String requestShortURI = request.getRequestURI().replaceAll(request.getContextPath(), "");
		String requestShortURL = requestShortURI;

		if (request.getQueryString() != null) {
			requestShortURL = requestShortURL + "?" + request.getQueryString();
		}
		if (!visitedURLs.isEmpty()) {
			if ((!"XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
					&& (!exclusionPopUpURLs.contains(requestShortURI))) {
				if (visitedURLs.peek().equals("skip!")) {
					visitedURLs.pop();
				}
				if (!exclusionURLs.contains(requestShortURI)) {
					if (!specialURLs.contains(requestShortURI)) {
						if (!visitedURLs.peek().split("\\?")[0].equals(requestShortURI)) {
							visitedURLs.push(requestShortURL);
						}
					} else {
						specialProcessingForURL(visitedURLs, requestShortURL);
					}
				} else {
					visitedURLs.push("skip!");
				}
			}
		} else {
			if ((!exclusionURLs.contains(requestShortURI)) && (!exclusionPopUpURLs.contains(requestShortURI))
					&& (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With")))) {
				visitedURLs.push(requestShortURL);
			} else {
				visitedURLs.push(defaultShortURL);
			}
		}
	}

	private static void specialProcessingForURL(Stack<String> visitedURLs, String requestShortURL) {
		String requestShortURI = requestShortURL.split("\\?")[0];
		if ("/ListEventsForSubjects".equals(requestShortURI)) {
			if (visitedURLs.peek().split("\\?")[0].equals("/ListEventsForSubjects")
					|| visitedURLs.peek().split("\\?")[0].equals("/ListStudySubjects")) {
				visitedURLs.pop();
				visitedURLs.push(requestShortURL);
			} else {
				visitedURLs.push(requestShortURL);
			}
		} else {
			visitedURLs.push(requestShortURL);
		}
	}
}
