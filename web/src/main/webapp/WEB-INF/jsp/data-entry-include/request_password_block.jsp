<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<div id="box" class="dialog" style="display:none;">
	<span id="mbm">
	<c:choose>
		<c:when test="${section.eventDefinitionCRF.electronicSignature == true}">
			<fmt:message key="crf_data_entry_password_required" bundle="${restext}"/>
		</c:when>
		<c:otherwise>
			<fmt:message key="marking_CRF_complete_finalize_DE" bundle="${restext}"/>
		</c:otherwise>
	</c:choose>
	</span><br>
	<div style="text-align:center; width:100%;">
		<input align="center" type="password" name="password" id="passwordId"/>
		<input type="button"
			   onclick="requestSignatureFromCheckbox(document.getElementById('passwordId').value, checkboxObject);"
			   value="OK"/>
	</div>
</div>