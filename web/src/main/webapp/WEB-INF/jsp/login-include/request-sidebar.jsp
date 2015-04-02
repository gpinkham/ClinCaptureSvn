<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="org.akaza.openclinica.bean.core.Status"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<script language="JavaScript">
function reportBug(versionNumber) {
 var bugtrack = "http://dev.openclinica.org/ClinCapture/bug.php?version=<fmt:message key="version_number" bundle="${resword}"/>&url=";
 bugtrack = bugtrack + window.location.href;
 openDocWindow(bugtrack);

}
</script>
<!-- Breadcrumbs -->
<%--
	<div class="breadcrumbs">
	
	<a class="breadcrumb_completed"
			href="MainMenu">
			<fmt:message key="home" bundle="${resworkflow}"/></a>	
	&nbsp;
--%>
	</div>

<!-- End Breadcrumbs -->

				</td>

<!-- Help and ClinCapture Feedback Buttons -->

                <%-- 
				<td valign="top">
                    <table border="0" cellpadding="0" cellspacing="0">
                        <tr>&nbsp;</tr>
                        <tr>
                <td style="white-space:nowrap">
                    <a href="javascript:reportBug()">
                        <span class="aka_font_general" style="font-size: 0.9em">
                            <fmt:message key="openclinica_report_issue" bundle="${resword}"/></span>
                    </a>
                    |
                    <a href="javascript:openDocWindow('<c:out value="${sessionScope.supportURL}" />')">
                    <span class="aka_font_general" style="font-size: 0.9em"><fmt:message key="openclinica_feedback" bundle="${resword}"/></span></a>        </td>
                        </tr>
                    </table>
				</td>
				 --%>

<!-- end Help and ClinCapture Feedback Buttons -->

	<td valign="top" align="right">


<!-- User Box -->


<!-- End User Box -->

				</td>
			</tr>
		</table>
<!-- End Header Table -->
<table border="0" cellpadding=0" cellspacing="0">
	<tr>
		<td valign="top">

<!-- Sidebar Contents -->

<%--
	<br><br>
	&nbsp;&nbsp; <a href="MainMenu"><fmt:message key="login" bundle="${resword}"/> </a>	
	<br><br>
	<a href="RequestAccount"><fmt:message key="request_an_account" bundle="${resword}"/></a>
	<br><br>
	<a href="RequestPassword"><fmt:message key="forgot_password" bundle="${resword}"/></a>
--%>
	


<!-- End Sidebar Contents -->

				<br><img src="<c:url value='/images/spacer.gif'/>" width="160" height="1">

				</td>
				<td class="content" valign="top">
