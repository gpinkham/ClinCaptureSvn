<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib prefix="view" uri="com.akazaresearch.viewtags" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<jsp:include page="../data-entry-include/mark_crf_completed_block.jsp"/>
		<jsp:include page="../data-entry-include/partial_save_block.jsp"/>
		<td>
			<c:if test="${dataEntryStage.isAdmin_Editing() and !(param.isUpper eq 'true')}">
				<input type="hidden" name="fromResolvingNotes" value="${fromResolvingNotes}"/>
			</c:if>
			<input type="button" id="srh" name="submittedResume" value="<fmt:message key="save" bundle="${resword}"/>" class=
				"button_medium medium_submit" onclick="submitCrfForm(this);"/></td>
		<c:if test="${!dataEntryStage.isAdmin_Editing() and section.lastSection and hideSaveAndNextButton eq null }">
			<td>
				<input type="button" id="snl" name="saveAndNext" value="${save_and_next_button_caption}"
					   class="${view:getHtmlButtonCssClass(save_and_next_button_caption, submitClassType)}" onclick="submitCrfForm(this);"/>
			</td>
		</c:if>
		<td><input type="submit" id="sel" name="submittedExit"
				   value="<fmt:message key="${! empty formMessages ? 'cancel': 'exit'}" bundle="${resword}"/>"
				   class="button_medium ${! empty formMessages ? 'medium_cancel' : 'medium_back'}"
				   onClick="return checkGoBackEntryStatus('submittedExit', '<fmt:message key="you_have_unsaved_data_exit" bundle="${resword}"/>', this);" /></td>
	</tr>
</table>