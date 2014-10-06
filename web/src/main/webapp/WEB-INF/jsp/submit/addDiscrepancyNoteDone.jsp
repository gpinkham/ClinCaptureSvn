<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<jsp:useBean scope='request' id='strResStatus' class='java.lang.String' />
<jsp:useBean scope='request' id='writeToDB' class='java.lang.String' />

<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>    
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 

<html>
<head>
    <title><fmt:message key="openclinica" bundle="${resword}"/>- <fmt:message key="add_discrepancy_note" bundle="${resword}"/></title>
    <link rel="stylesheet" href="includes/styles.css" type="text/css">
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js"></script>

    <script language="JavaScript" src="includes/CalendarPopup.js"></script>
    <style type="text/css">
        .popup_BG { background-image: url(images/main_BG.gif);
            background-repeat: repeat-x;
            background-position: top;
            background-color: #FFFFFF;
        }
    </style>

    <script type="text/javascript" language="javascript">
        setImageInParentWin('flag_<c:out value="${updatedDiscrepancyNote.field}"/>', '<%=request.getContextPath()%>/<c:out value="${updatedDiscrepancyNote.resStatus.iconFilePath}"/>', '${updatedDiscrepancyNote.resStatus.id}');
    </script>

    <ui:theme/>
</head>
<body class="popup_BG" style="margin: 25px;">
<!-- *JSP* submit/addDiscrepancyNoteDone.jsp -->
<h1>
	<span class="first_level_header">
		<fmt:message key="add_discrepancy_note" bundle="${resword}"/>
	</span>
</h1>

<br clear="all">
<div class="alert">    
	<c:forEach var="message" items="${pageMessages}">
		<c:out value="${message}" escapeXml="false"/> 
	</c:forEach>
</div>
<div class="alert" style="font-size: 12px; margin: 100px 0px">  
	<fmt:message key="attention_must_complete_and_submit" bundle="${restext}"/>
</div>
<table border="0" "> 
	<c:if test="${parent.id>0}">
		<tr valign="top">
			<td>
				<fmt:message key="discrepancy_thread_id" bundle="${resword}"/>
			</td>
			<td>
				<c:out value="${parent.id}"/>
			</td>
		</tr>       
	</c:if>
	<c:if test="${hasNotes == 'yes'}">        
		<tr valign="top">
			<td colspan="2">
				<a href="ViewDiscrepancyNote?id=<c:out value="${updatedDiscrepancyNote.entityId}"/>&name=<c:out value="${updatedDiscrepancyNote.entityType}"/>&field=<c:out value="${updatedDiscrepancyNote.field}"/>&column=<c:out value="${updatedDiscrepancyNote.column}"/>">
					<fmt:message key="view_parent_and_related_note" bundle="${resword}"/>
				</a> 
			</td>         
		</tr>
	</c:if> 
</table>
<table border="0" style="margin-top:60px"> 
	<tr>     
		<td>
			<input type="submit" name="B1" value="<fmt:message key="close" bundle="${resword}"/>" class="button_medium" onclick="javascript:window.close();">
		</td> 
	</tr>
</table> 

<jsp:include page="../include/changeTheme.jsp"/> 
</html>
