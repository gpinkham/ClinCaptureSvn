<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:choose>
	<c:when test="${!dataEntryStage.isAdmin_Editing() && section.lastSection}">
		<c:choose>
			<c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
				<td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
				<c:if test="${markComplete=='Yes'}"> checked </c:if> onchange="changeImage('markComplete');" onclick="return crfCompleteAuthorize({ message: '<fmt:message key="crf_data_entry_password_required" bundle="${restext}"/>', height: 190, width: 730, checkbox: this });">
				</td>
				<td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
			</c:when>
			<c:otherwise>
				<td valign="bottom">  <input type="checkbox" id="markCompleteId" name="markComplete" value="Yes"
				<c:if test="${markComplete=='Yes'}"> checked </c:if> onclick="${markCRFMethodName}" onchange="changeImage('markComplete');">
				</td>
				<td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="mark_CRF_complete" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<td colspan="2">&nbsp;</td>
	</c:otherwise>
</c:choose>