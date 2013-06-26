package org.akaza.openclinica.navigation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import javax.servlet.http.HttpServletRequest;

@SuppressWarnings({"unchecked"})
public class Navigation {
	
	//"skip!"-set of pages, non pop-ups
	private static Set<String> exclusionURLs = new HashSet<String>(Arrays.asList("/PageToCreateNewStudyEvent",
			"/CRFListForStudyEvent", "/InitialDataEntry", "/VerifyImportedCRFData",
			"/AdministrativeEditing", "/UpdateStudySubject", "/RemoveStudySubject", "/ResolveDiscrepancy", 
			"/RestoreStudySubject", "/UpdateStudyEvent", "/RemoveStudyEvent", "/RestoreStudyEvent", 
			"/DeleteEventCRF", "/RemoveEventCRF", "/RestoreEventCRF", "/CreateOneDiscrepancyNote",
			"/InitUpdateSubStudy", "/RemoveStudyUserRole", "/ViewSectionDataEntry", "/CreateSubjectGroupClass",
			"/SetStudyUserRole", "/UpdateProfile", "/SectionPreview", "/DefineStudyEvent", 
			"/InitUpdateEventDefinition", "/UpdateEventDefinition", "/RemoveEventDefinition", "/RemoveSubject", 
			"/RemoveStudy", "/ViewUserAccount", "/EditUserAccount", "/SetUserRole", "/ViewUserAccount", 
			"/Configure", "/CreateUserAccount", "/UpdateJobImport", "/CreateJobExport", "/CreateJobImport",
			"/UpdateProfile", "/RemoveDataset", "/LockStudySubject", "/pages/extract",
			"/CreateNewStudyEvent", "/UpdateSubject", "/UpdateSubjectGroupClass", "/ViewSubjectGroupClass",
			"/RemoveSubjectGroupClass", "/RestoreSubjectGroupClass", "/UpdateJobExport", "/EditDataset",
            "/RemoveSite", "/RestoreSite"));
	//ignored-set of pages, pop-ups or like pop-ups
	private static Set<String> exclusionPopUpURLs = new HashSet<String>(Arrays.asList("/ViewStudySubjectAuditLog",
			"/PrintAllEventCRF", "/PrintDataEntry", "/DiscrepancyNoteOutputServlet", "/PrintDataEntry",
			"/ViewItemDetail", "/PrintCRF", "/ChangeDefinitionOrdinal", "/PrintEventCRF",
			"/ViewRulesAssignment", "/DownloadRuleSetXml", "/UpdateRuleSetRule", "/pages/handleSDVGet",
			"/DownloadVersionSpreadSheet", "/PrintAllSiteEventCRF", "/DeleteUser", "/UnLockUser",
			"/DeleteStudyUserRole", "/PauseJob", "/SelectItems", "/CreateDiscrepancyNote", 
			"/confirmCRFVersionChange", "/ViewDiscrepancyNote", "/AccessFile", "/help",
			"/PrintSubjectCaseBook", "/ExportExcelStudySubjectAuditLog", "/ShowCalendarFunc", "/ViewCalendaredEventsForSubject",
			"/ResetPassword"));
	private static String defaultShortURL = "/MainMenu";		
	
	/*
	 * Here send/receive logic of visitedURLs-stack is accumulated. 
	 * You can add transfer-logic here.
	 */
	public static void addToNavigationStack(HttpServletRequest request){
    	String urlPrefix = request.getContextPath();
		//for the case when back-button pressed after session died 
    	request.getSession().setAttribute("defaultURL", urlPrefix+defaultShortURL);
    	request.getSession().setAttribute("navigationURL", urlPrefix+"/HelpNavigation");
    	
    	if (!"true".equals((String)request.getSession().getAttribute("skipURL"))){
    		Stack<String> visitedURLs = new Stack<String>();
        	if (request.getSession().getAttribute("visitedURLs")!=null) {
        		visitedURLs = (Stack<String>)request.getSession().getAttribute("visitedURLs");
        	}  
        	processRequestURL(visitedURLs, request);
        	request.getSession().setAttribute("visitedURLs", visitedURLs);

    	} else {
    		request.getSession().setAttribute("skipURL", "false");
    	}	
    }
	
	/*
	 * Here incoming request-URLs are (or aren't) added to visitedURLs-stack. 
	 * You can add business-logic here.
	 */
	private static void processRequestURL(Stack<String> visitedURLs, HttpServletRequest request) {
		//delete contextPath part of URL to do saved links shorter
		String requestShortURI = request.getRequestURI().replaceAll(request.getContextPath(), "");
		String requestShortURL = requestShortURI;
		if (request.getQueryString()!=null){ 
			requestShortURL = requestShortURL+"?"+request.getQueryString();
    	}
		if (!visitedURLs.isEmpty()){
			if ((!"XMLHttpRequest".equals(request.getHeader("X-Requested-With")))&&(!exclusionPopUpURLs.contains(requestShortURI))){
				if (visitedURLs.peek().equals("skip!")){
					visitedURLs.pop();
				}
				if (!exclusionURLs.contains(requestShortURI)) {
					if (!visitedURLs.peek().contains(requestShortURI)){
						visitedURLs.push(requestShortURL);
					}
				} else {
					visitedURLs.push("skip!");
				}
			}	
		} else {
			if ((!exclusionURLs.contains(requestShortURI))&&(!exclusionPopUpURLs.contains(requestShortURI))
					&&(!"XMLHttpRequest".equals(request.getHeader("X-Requested-With")))){
				visitedURLs.push(requestShortURL);
			} else {
				visitedURLs.push(defaultShortURL);
			}
			}	
		}
	}
