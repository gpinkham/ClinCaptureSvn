<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:choose>
	<c:when test="${cdisplay>0}">
		<c:set var="scdShowStatus" value="${displayItemWithGroup.singleItem.scdData.scdDisplayInfo.scdShowStatus}"/>
		<c:set var="cdId" value="${displayItemWithGroup.singleItem.item.id}"/>
		<c:choose>
			<c:when test="${scdShowStatus == 1}">

				<tr class="aka_stripes" id="<c:out value="hd${cdId}"/>">
			</c:when>
			<c:when test="${scdShowStatus == 2}">
				<tr class="aka_stripes" id="<c:out value="hd${cdId}"/>" style="display:none">
			</c:when>
			<c:otherwise>
				<tr class="aka_stripes">
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<tr class="aka_stripes">
	</c:otherwise>
</c:choose>
<td class="table_cell_left aka_stripes">
	<b><c:out value="${displayItemWithGroup.singleItem.metadata.header}" escapeXml="false"/></b>
</td>
</tr>