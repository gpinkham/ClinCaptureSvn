<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css">
<html>
<body onload="javascript:window.location.href='MainMenu'">
<center><h1><fmt:message key="logging_out" bundle="${resword}"/></h1></center>
</body>
</html>
