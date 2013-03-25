package org.akaza.openclinica.navigation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import javax.servlet.http.HttpServletRequest;

@SuppressWarnings({"unchecked"})
public class Navigation {
	
	private static Set<String> exclusionURLs = new HashSet<String>(Arrays.asList("/PageToCreateNewStudyEvent",
			"/CRFListForStudyEvent", "/InitialDataEntry", "/ViewDiscrepancyNote", "/PrintSubjectCaseBook", 
			"/PrintDataEntry", "/AdministrativeEditing", "/UpdateStudySubject", "/RemoveStudySubject", 
			"/RestoreStudySubject", "/UpdateStudyEvent", "/RemoveStudyEvent", "/RestoreStudyEvent", 
			"/DeleteEventCRF", "/RemoveEventCRF", "/RestoreEventCRF"));
			
	
	/*
	 * Here send/receive logic of visitedURLs-stack is accumulated. 
	 * You can add transfer-logic here.
	 */
	public static void addToNavigationStack(HttpServletRequest request){
    	String urlPrefix = request.getContextPath();
		//for the case when back-button pressed after session died 
    	request.getSession().setAttribute("defaultURL", urlPrefix+"/MainMenu");
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
	private static void processRequestURL(Stack<String> visitedURLs,
			HttpServletRequest request) {
		//delete contextPath part of URL to do save links shorter
		String requestShortURI = request.getRequestURI().replaceAll(request.getContextPath(), "");
		String requestShortURL = requestShortURI;
		if (request.getQueryString()!=null){ 
			requestShortURL = requestShortURL+"?"+request.getQueryString();
    	}
		if (!visitedURLs.isEmpty()){
			if ((!visitedURLs.peek().contains(requestShortURI))&&(!exclusionURLs.contains(requestShortURI))
					&&(!"XMLHttpRequest".equals(request.getHeader("X-Requested-With")))){
				visitedURLs.push(requestShortURL);
			}	
		} else {
			visitedURLs.push(requestShortURL);
		}
		
	}
}
