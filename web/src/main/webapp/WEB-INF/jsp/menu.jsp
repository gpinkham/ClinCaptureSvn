<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean'/>

<jsp:include page="include/home-header.jsp"/>
<jsp:include page="include/sideAlert.jsp"/>


<link rel="stylesheet" href="includes/jmesa/jmesa.css" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>
<script type="text/javascript" language="JavaScript" src="includes/jmesa/jquery.blockUI.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>
<style type="text/css">

  .graph {
    position: relative; /* IE is dumb */
    width: 100px;
    border: 1px solid #3876C1;
    padding: 2px;
  }

  .graph .bar {
    display: block;
    position: relative;
    background: #E8D28C;
    text-align: center;
    color: #333;
    height: 1em;
    line-height: 1em;
  }

  .graph .bar span {
    position: absolute;
    left: 1em;
  }
</style>

<script type="text/JavaScript" language="JavaScript">
  //alignment of headers and icons

  jQuery(document).ready(function () {
    jQuery("div[id^='Event_']").parent().parent().parent().parent().parent().attr("align", "center");
    jQuery("tr.header").attr("align", "center");
  });
</script>

<!-- then instructions-->
<div id="box" class="dialog">
	<span id="mbm"><br>
		<fmt:message key="study_frozen_locked_note" bundle="${restext}"/>
	</span><br>
	<div style="text-align:center; width:100%;">
		<button onclick="hm('box');">OK</button>
	</div>
</div>
<tr id="sidebar_Instructions_open" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content">
			<fmt:message key="may_change_request_access" bundle="${restext}"/>
		</div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>

<jsp:include page="include/sideInfo.jsp"/>


<h1> 
    <span class="first_level_header" style="line-height:5px;">
        <fmt:message key="welcome_to" bundle="${restext}"/>
            <c:choose>
              <c:when test='${study.parentStudyId > 0}'>
                <c:out value='${study.parentStudyName}'/>
              </c:when>
              <c:otherwise>
                <c:out value='${study.name}'/>
              </c:otherwise>
            </c:choose>
    </span>
</h1>

<c:set var="roleName" value=""/>
<c:if test="${userRole != null && !userRole.invalid}">
  <c:set var="roleName" value="${userRole.role.name}"/>

  <c:set var="studyidentifier">
    <span class="alert"><c:out value="${study.identifier}"/></span>
  </c:set>

</c:if>
<span class="table_title_Admin" style="line-height:15px;">
	<a href="ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=<c:out value='${userBean.name}' />&listNotes_f_discrepancyNoteBean.resolutionStatus=<fmt:message key="Not_Closed" bundle="${resterm}"/>">
	  <fmt:message key="notes_assigned_to_me" bundle="${restext}"/><span>${assignedDiscrepancies}</span>&nbsp;
	</a><br/><br/>
</span>

<script type="text/JavaScript" language="JavaScript">
  //check which home page should be displayed
  $(document).ready(function () {
    chooseHomePageVersion();
  });
</script>
<!--Old home page layout-->
<c:if test="${displayPageVersion=='old'}">
	<span class="old_home_page">
	<c:if test="${userRole.investigator || userRole.clinicalResearchCoordinator}">
	
	<div id="findSubjectsDiv">
		<script type="text/javascript">
		function onInvokeAction(id, action) {
	        if (id.indexOf('findSubjects') == -1) {
	          setExportToLimit(id, '');
	        }
	        createHiddenInputFieldsForLimitAndSubmit(id);
		}
		function onInvokeExportAction(id) {
			var parameterString = createParameterStringForLimit(id);
			location.href = '${pageContext.request.contextPath}/MainMenu?' + parameterString;
		}
		jQuery(document).ready(function () {
	        jQuery('#addSubject').click(function () {
	          jQuery.blockUI({ message: jQuery('#addSubjectForm'), css: {left: "300px", top: "10px" } });
			});
	
			jQuery('#cancel').click(function () {
	          jQuery.unblockUI();
	          return false;
			});
		});
	    </script>
	    <form action="${pageContext.request.contextPath}/ListStudySubjects">
	      <input type="hidden" name="module" value="admin">
	        ${findSubjectsHtml}
	    </form>
	  </div>
	  <div id="addSubjectForm" style="display:none;">
	    <c:import url="addSubjectMonitor.jsp"/>
	  </div>
	
	
	</c:if>
	
	<c:if test="${userRole.sysAdmin || userRole.studyAdministrator || userRole.studyDirector}">
	
	
	  <script type="text/javascript">
	    function onInvokeAction(id, action) {
	      if (id.indexOf('studySiteStatistics') == -1) {
	        setExportToLimit(id, '');
	      }
	      if (id.indexOf('subjectEventStatusStatistics') == -1) {
	        setExportToLimit(id, '');
	      }
	      if (id.indexOf('studySubjectStatusStatistics') == -1) {
	        setExportToLimit(id, '');
	      }
	      createHiddenInputFieldsForLimitAndSubmit(id);
	    }
	
	  </script>
	
	  <table>
	    <tr>
	      <td valign="top">
	        <form action="${pageContext.request.contextPath}/MainMenu">
	            ${studySiteStatistics}
	        </form>
	      </td>
	      <td valign="top">
	        <form action="${pageContext.request.contextPath}/MainMenu">
	            ${studyStatistics}
	        </form>
	      </td>
	    </tr>
	  </table>
	
	
	  <table>
	    <tr>
	      <td valign="top">
	        <form action="${pageContext.request.contextPath}/MainMenu">
	            ${subjectEventStatusStatistics}
	        </form>
	      </td>
	
	      <td valign="top">
	        <form action="${pageContext.request.contextPath}/MainMenu">
	            ${studySubjectStatusStatistics}
	        </form>
	      </td>
	    </tr>
	  </table>
	
	</c:if>
	
	<c:if test="${userRole.role.id eq 6}">
	
	
	  <script type="text/javascript">
	    function onInvokeAction(id, action) {
	      setExportToLimit(id, '');
	      createHiddenInputFieldsForLimitAndSubmit(id);
	    }
	    function onInvokeExportAction(id) {
	      var parameterString = createParameterStringForLimit(id);
	    }
	    function prompt(formObj, crfId) {
	      var bool = confirm(
	          "<fmt:message key="uncheck_sdv" bundle="${resmessages}"/>");
	      if (bool) {
	        formObj.action = '${pageContext.request.contextPath}/pages/handleSDVRemove';
	        formObj.crfId.value = crfId;
	        formObj.submit();
	      }
	    }
	  </script>
	
	  <div id="searchFilterSDV">
	    <table border="0" cellpadding="0" cellspacing="0">
	      <tr>
	        <td valign="bottom" id="Tab1'">
	          <div id="Tab1NotSelected">
	            <div class="tab_BG">
	              <div class="tab_L">
	                <div class="tab_R">
	                  <a class="tabtext" title="<fmt:message key="view_by_event_CRF" bundle="${resword}"/>"
	                     href='pages/viewAllSubjectSDVtmp?studyId=${studyId}'
	                     onclick="javascript:HighlightTab(1);"><fmt:message key="view_by_event_CRF"
	                                                                        bundle="${resword}"/></a></div>
	              </div>
	            </div>
	          </div>
	          <div id="Tab1Selected" style="display:none">
	            <div class="tab_BG_h">
	              <div class="tab_L_h">
	                <div class="tab_R_h"><span class="tabtext"><fmt:message key="view_by_event_CRF"
	                                                                        bundle="${resword}"/></span></div>
	              </div>
	            </div>
	          </div>
	        </td>
	
	        <td valign="bottom" id="Tab2'">
	          <div id="Tab2Selected">
	            <div class="tab_BG">
	              <div class="tab_L">
	                <div class="tab_R">
	                  <a class="tabtext" title="<fmt:message key="view_by_studysubjectID" bundle="${resword}"/>"
	                     href='pages/viewSubjectAggregate?studyId=${studyId}'
	                     onclick="javascript:HighlightTab(2);"><fmt:message key="view_by_studysubjectID"
	                                                                        bundle="${resword}"/></a></div>
	              </div>
	            </div>
	          </div>
	          <div id="Tab2NotSelected" style="display:none">
	            <div class="tab_BG_h">
	              <div class="tab_L_h">
	                <div class="tab_R_h"><span class="tabtext"><fmt:message key="view_by_studysubjectID"
	                                                                        bundle="${resword}"/></span></div>
	              </div>
	            </div>
	          </div>
	        </td>
	
	      </tr>
	    </table>
	    <script language="JavaScript">
	      HighlightTab(1);
	    </script>
	  </div>
	  <div id="subjectSDV">
	    <form name='sdvForm' action="${pageContext.request.contextPath}/pages/viewAllSubjectSDVtmp">
	      <input type="hidden" name="studyId" value="${study.id}">
	      <input type="hidden" name=imagePathPrefix value="">
	        <%--This value will be set by an onclick handler associated with an SDV button --%>
	      <input type="hidden" name="crfId" value="0">
	        <%-- the destination JSP page after removal or adding SDV for an eventCRF --%>
	      <input type="hidden" name="redirection" value="viewAllSubjectSDVtmp">
	        <%--<input type="hidden" name="decorator" value="mydecorator">--%>
	        ${sdvMatrix}
	      <br/>
	      <input type="button" name="BTN_Back_Smart" id="GoToPreviousPage"
	             value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium"
	             onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/>
	      <input type="submit" name="sdvAllFormSubmit" class="button_medium"
	             value="<fmt:message key="sdv_all_checked" bundle="${resword}"/>"
	             onclick="this.form.method='POST';this.form.action='${pageContext.request.contextPath}/pages/handleSDVPost';this.form.submit();"/>
	    </form>
	
	  </div>
	</c:if>
	<c:if test="${userRole.studyCoder}">
	
	  <script type="text/javascript">
	
	    $.ajax({
	      type: "POST",
	      url: "pages/codedItems"
	    })
	
	  </script>
	</c:if>
	</span>
	<!--//Old home page layout-->
</c:if>

<c:if test="${displayPageVersion=='new'}">
	<!--New home page layout-->
	<span class="new_home_page">
	<input type="hidden" id="userId" name="userId" value="${userBean.id}"/>
	<input type="hidden" id="studyId" name="studyId" value="${study.id}"/>
	
	<c:if test="${!empty dispayWidgetsLayout}">
		<table class="widgets_container">
			<tr>
				<td>
					<c:forEach var="widget" items="${dispayWidgetsLayout}">
						<c:if test="${widget.ordinal ne 0 && widget.ordinal%2 ne 0}">
							<div class="widget">
								<jsp:include page="widgets/${widget.widgetName}"/>
							</div>
						</c:if>
					</c:forEach>
				</td>
				<td>
					<c:forEach var="widget" items="${dispayWidgetsLayout}">
						<c:if test="${widget.ordinal ne 0 && widget.ordinal%2 eq 0}">
							<div class="widget">
								<jsp:include page="widgets/${widget.widgetName}"/>
							</div>
						</c:if>
					</c:forEach>
				</td>
			</tr>
		</table>
	</c:if>
	</span>
	<!--//New home page layout-->
</c:if>
<c:if test="${userRole.role.id ne 6}">
 <br>
 	<table>
 		<tr>
	 		<td>
	 			<input type="button" name="BTN_Back_Smart" id="GoToPrevisusPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/> 
	 		</td>
	 		<c:if test="${displayPageVersion=='new'}">
				<td class="new_home_page">
					<input id="ConfigueHomePage" class="button_long" type="button" name="BTN_Config" value="<fmt:message key="customize_home_page" bundle="${resword}"/>" onClick="window.location.href=('pages/configureHomePage');"/>
				</td>
			</c:if>
		</tr>
 	</table> 
<br>
</c:if>


<jsp:include page="include/footer.jsp"/>
