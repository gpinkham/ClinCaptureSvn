<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<html>
<head>
    <link rel="icon" href="<c:url value='${faviconUrl}'/>"/>
    <link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>"/>
    <link rel="stylesheet" href="../includes/styles.css?r=${revisionNumber}" type="text/css">
    <script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript"            src="../includes/global_functions_javascript.js?r=${revisionNumber}"></script>
    <ui:theme/>
    <title>
        <fmt:message key="generated_casebooks_for_study" bundle="${resword}"/> : <c:out value="${studyName}"/>
    </title>
</head>
<body>
<h1>
	<span class="first_level_header">
        <fmt:message key="generated_casebooks_for_study" bundle="${resword}"/> : <c:out value="${studyName}"/>
	</span>
</h1>

<c:import url="../include/showTable.jsp">
    <c:param name="rowURL" value="downloadCasebooksRow.jsp"/>
</c:import>

&nbsp<input id="close" class="button_medium" type="submit"
            onclick="javascript:window.close()" value="<fmt:message key="close_window" bundle="${resword}"/>"
            name="BTN_Close_Window"/>
<jsp:include page="../include/changeTheme.jsp"/>
<script>
    $("img[src*='images']").each(function () {
        $(this).attr('src', $(this).attr('src').replace('images/', '../images/'));
    });
    $("a").each(function () {
        $(this).attr('href', $(this).attr('href').replace('/pages/', '${pageContext.request.contextPath}/pages/'));
    });
    $("form").each(function () {
        $(this).attr('action', $(this).attr('action').replace('/pages/downloadCasebooks', '${pageContext.request.contextPath}/pages/downloadCasebooks'));
    });
    $("input").each(function () {
        $(this).css('background-image', $(this).css('background-image').replace('pages/images/', 'images/'));
    });
</script>
</body>
</html>