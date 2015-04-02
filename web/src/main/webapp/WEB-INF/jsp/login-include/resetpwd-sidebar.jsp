<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="org.akaza.openclinica.web.SQLInitServlet"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>

<script language="JavaScript">
function reportBug(versionNumber) {
 var bugtrack = "http://dev.openclinica.org/ClinCapture/bug.php?version=<fmt:message key="version_number" bundle="${resword}"/>&url=";
 bugtrack = bugtrack + window.location.href;
 openDocWindow(bugtrack);

}
</script>

<!-- End Header Table -->
<table border="0" cellpadding=0" cellspacing="0">
    <tr>
        <td class="sidebar" valign="top">

<!-- Sidebar Contents -->

    <br><br>
    &nbsp;&nbsp; <a href="MainMenu">&nbsp;<fmt:message key="home" bundle="${resworkflow}"/></a>
<%--
	<br><br>
	<a href="RequestAccount"><fmt:message key="request_an_account" bundle="${resword}"/></a>
	<br><br>
	<a href="RequestPassword"><fmt:message key="forgot_password" bundle="${resword}"/></a>
--%>
    


<!-- End Sidebar Contents -->

                <br><img src="<c:url value='/images/spacer.gif'/>" width="120" height="1">

                </td>
                <td class="content" valign="top">