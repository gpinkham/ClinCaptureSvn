<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:choose> 
	<c:when test="${!dataEntryStage.isAdmin_Editing()}">
		<td valign="bottom">  
			<input type="checkbox" id="partialSaveId" name="markPartialSaved" value="Yes"
				<c:if test="${markPartialSaved=='Yes'}"> checked </c:if> onchange="changeImage('markPartialSaved');" onclick="checkPartialSaveCheckboxes(this.name, this.checked, 'markComplete');">
		</td>
		<td valign="bottom" nowrap="nowrap">&nbsp; <fmt:message key="partial_data" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
	</c:when>
	<c:otherwise>
		<td colspan="2">&nbsp;</td>
	</c:otherwise>
</c:choose>