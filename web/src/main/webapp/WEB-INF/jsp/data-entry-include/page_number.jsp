<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<table border="0" cellpadding="0" cellspacing="0" style="margin-bottom: 6px;">
	<tr>
		<td valign="bottom" nowrap="nowrap" style="padding-right: 50px">
			<fmt:message key="page" bundle="${resword}"/>: <c:out value="${displayItemWithGroup.pageNumberLabel}" escapeXml="false"/>
		</td>
	</tr>
</table>