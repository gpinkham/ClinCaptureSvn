<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="words"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="notes"/>

<jsp:include page="/includes/js/dialogs.js.jsp" />

<script>
	function showPropagateChangesDialog(childConfigIsSame, button) {
		var params = new DefaultDialogParams();
		params.bBoxTitle = "<fmt:message bundle="${words}" key="confirmation_message"/>";
		params.message = "<fmt:message bundle="${words}" key="propagate_change_message.1"/><br>" +
				"<fmt:message bundle="${words}" key="propagate_change_message.2"/><br><br>" +
				"<input name='answer' type='radio' value='3' checked><fmt:message bundle="${words}" key="propagate_change.3"/><br>" +
				"<input name='answer' type='radio' value='1'><fmt:message bundle="${words}" key="propagate_change.1"/><br>";
		if (childConfigIsSame != true) {
			params.message += "<input name='answer' type='radio' value='2'><fmt:message bundle="${words}" key="propagate_change.2"/><br>";
		}
		params.okButtonClass = "medium_continue";
		params.cancelButtonClass = "medium_cancel";
		params.okButtonValue = '<fmt:message bundle="${words}" key="continue"/>';
		params.height = 220;
		params.cancelButtonValue = '<fmt:message bundle="${words}" key="cancel"/>';
		params.buttons = {
			'<fmt:message bundle="${words}" key="continue"/>': function () {
				var answer = $('input[name=answer]:checked').val();
				$("#propagateChange").val(answer);
				$("#confirmDialog").remove();
				checkItemLevelSDVChanges("<fmt:message bundle='${words}' key='item_level_sdv_status_changed'/>", button);
			},
			'<fmt:message bundle="${words}" key="cancel"/>': function () {
				$("#confirmDialog").remove();
			}
		};
		createDialog(params);
		return false;
	}
</script>