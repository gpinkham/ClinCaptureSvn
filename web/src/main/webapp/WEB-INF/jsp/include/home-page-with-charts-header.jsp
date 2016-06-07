<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='isAdminServlet' class='java.lang.String' />

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	
	<title><fmt:message key="openclinica" bundle="${resword}"/></title>

	<link rel="stylesheet" href="../includes/styles.css?r=${revisionNumber}" type="text/css"/>
	<link rel="stylesheet" href="../includes/css/charts.css?r=${revisionNumber}" type="text/css"/>
	<link rel="stylesheet" href="../includes/jquery-ui.css?r=${revisionNumber}" type="text/css"/>

	<script type="text/javascript" src="../includes/jmesa/jsapi"></script>
	<script type="text/javascript">
		google.load('visualization', '1.0', {
			'packages' : [ 'corechart' ]
		});
	</script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-1.11.0.min.js"></script>	
	<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-ui-1.10.4.custom.min.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-migrate-1.2.1.min.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/global_functions_javascript.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/Tabs.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/js/widgets/Charts.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/CalendarPopup.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/repetition-model/repetition-model.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/prototype.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/scriptaculous.js?load=effects&r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="../includes/effects.js?r=${revisionNumber}"></script>
	
    <!-- Added for the new Calender -->
    <ui:calendar/>
	<!-- End -->

    <link rel="icon" href="<c:url value='../images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='../images/favicon.ico'/>" />

    <ui:theme/>
</head>
<body class="main_BG">
	<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="background">
    	<tr>
        	<td valign="top">
			<!-- Header Table -->
			<script language="JavaScript">var StatusBoxValue=1;</script>
            <table id="headerInnerTable2" border="0" cellpadding="0" cellspacing="0" class="header">
            <tr>
                <td valign="top">
					<!-- *JSP* ${pageContext.page['class'].simpleName} -->
					<div class="logo"><img src="${'..'}${systemLogoUrl}"></div>
					<!-- Main Navigation -->
   					<jsp:include page="../include/navBar.jsp">
     				   <jsp:param name="isSpringController" value="true" />
   					 </jsp:include>
					<!-- End Main Navigation -->
