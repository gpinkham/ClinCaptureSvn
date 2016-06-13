<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="com.akazaresearch.viewtags" prefix="view" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <meta name="gwt:property" content="locale=${pageContext.request.locale}">
    <title><decorator:title default="ClinCapture" /></title>
    <script type="text/javascript" language="javascript" src="../gwt/GwtMenu/org.akaza.openclinica.gwt.GwtMenu.nocache.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" language="javascript" src="../includes/prototype.js?r=${revisionNumber}"></script>
    <script type="text/javascript" language="javascript" src="../includes/global_functions_javascript.js?r=${revisionNumber}"></script>
    <script type="text/javascript" language="javascript" src="../includes/Tabs.js?r=${revisionNumber}"></script>
    <!-- Added for the new Calender -->

    <ui:calendar/>
    <!-- End -->
    <link rel="stylesheet" href="../includes/styles_updated.css?r=${revisionNumber}" type="text/css">
    <link rel="stylesheet" href="../includes/proto_styles.css?r=${revisionNumber}" type="text/css">
    <link rel="stylesheet" href="../gwt/GwtMenu/GwtMenu.css" type="text/css">
    <link rel="icon" href="<c:url value='${faviconUrl}'/>" />
    <link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>" />
    <decorator:head />
</head>
<body>
<div id="headerDiv">
    <ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
    <div id="logoDiv"><img src="<c:url value='${systemLogoUrl}'/>" alt="Clinovo logo"/></div>
    <!-- the sub-menu, or alternative menu, displays if JavaScript is disabled-->
    <div id="menuContainer">
        <noscript>
            <span class="noscript">
                <a href="MainMenu"><fmt:message key="nav_home" bundle="${resword}"/></a> | <a href="ListStudySubjectsSubmit"><fmt:message key="nav_submit_data" bundle="${resword}"/></a> | <a href="ExtractDatasetsMain"><fmt:message key="nav_extract_data" bundle="${resword}"/></a> | <a href="ManageStudy"><fmt:message key="manage_study" bundle="${resword}"/></a> | <a href="AdminSystem"><fmt:message key="bussines_admin" bundle="${resword}"/></a>
            </span>
        </noscript>

    </div>

    <div id="reportIssueDiv">
        <%--
        <a href="javascript:reportBug()"> <span class="originalFont"><fmt:message key="openclinica_report_issue" bundle="${resword}"/></span> </a> | <a href="javascript:openDocWindow('http://www.openclinica.org/ClinCapture/2.2/support/')"><span class="originalFont"><fmt:message key="openclinica_feedback" bundle="${resword}"/></span></a>
        --%>
    </div>
    <div id="userBoxDiv" class="userbox">
        <view:userbox />
    </div>
</div>
<%-- this element must be designed to optionally include/exclude its internal DIVs --%>
<view:sidebar />

<div id="bodyDiv">
    <decorator:body />
    <div id="workflowDiv">
        <view:workflow />
    </div>
</div>

<div id="footerDiv">
    <view:footer />
</div>
</body>
</html>
