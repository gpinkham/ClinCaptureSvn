<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='isAdminServlet' class='java.lang.String' />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" style="position: relative; min-height: 100%;">

<head>
    <c:set var="contextPath" value="${pageContext.request.contextPath}" />
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />

    <title><fmt:message key="openclinica" bundle="${resword}"/></title>

    <link rel="stylesheet" href="<c:out value="${contextPath}" />/includes/styles.css?r=${revisionNumber}" type="text/css">
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js?r=${revisionNumber}"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js?r=${revisionNumber}"></script>
    <!-- Added for the new Calender -->

    <ui:calendar/>
    <!-- End -->

    <script type="text/JavaScript" language="JavaScript" src="includes/prototype.js?r=${revisionNumber}"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $.ajax({
                url:'HelpThemeServlet',
                type: 'GET',
                dataType:'text',
                success:function(response2){
                    var themeColor = response2;

                    if (themeColor == 'violet') {
                        $('a').css('color','#AA62C6');
                        $('H1').css('color', '#AA62C6');
                        $("input").not(".medium_cancel, .medium_back, .medium_submit").each(function() {
                            var newSrc = $(this).css('background-image');
                            if (newSrc.indexOf('/violet/') >= 0) return;
                            newSrc = newSrc.replace('images/','images/violet/');
                            $(this).css('background-image', newSrc);
                        });
                    }
                    if (themeColor == 'green') {
                        $('a').css('color','#75b894');
                        $('H1').css('color', '#75b894');
                        $("input").each(function() {
                            var newSrc = $(this).css('background-image');
                            if (newSrc.indexOf('/green/') >= 0) return;
                            newSrc = newSrc.replace('images/','images/green/');
                            $(this).css('background-image', newSrc);
                        });
                    }}
            });
        })
    </script>

</head>
<body style="width:1024px; margin-bottom: 170px;" class="main_BG"

<c:choose>

<c:when test="${tabId!= null && tabId>0}">
onload="TabsForwardByNum(<c:out value="${tabId}"/>);<jsp:include page="../include/showPopUp2.jsp"/>"
</c:when>

<c:otherwise>

<jsp:include page="../include/showPopUp.jsp"/>

</c:otherwise>
</c:choose>
>
<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="background">
    <tr>
        <td valign="top">
<!-- Header Table -->

<!-- NEW 06-22 -->
    <script language="JavaScript">
    var StatusBoxValue=1;
    </script>

            <table border="0" cellpadding="0" cellspacing="0" class="header">
                <tr>
                    <td valign="top">
                        <div class="disabled_header"><img src="<c:url value='/images/spacer.gif'/>"></img></div>
                        <!-- *JSP* ${pageContext.page['class'].simpleName} -->
                        <div class="logo"><img src="<c:url value='/images/Logo_upper.gif'/>"></div>
                        <!-- Main Navigation -->
                        <%-- <jsp:include page="../include/navBar.jsp"/> --%>
                        
<!-- End Main Navigation -->
