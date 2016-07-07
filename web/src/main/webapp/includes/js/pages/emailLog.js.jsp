<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="words"/>

<jsp:include page="/includes/js/dialogs.js.jsp" />

<script>
	function resendEmail(id, sentFromAdmin) {
		if (sentFromAdmin == true) {
			resend(id, sentFromAdmin);
			return;
		}
		var params = new DefaultDialogParams();
		params.message = "<fmt:message bundle="${words}" key="resend_email_admin_message"/><br><br>" +
		"<input name='use_original_sender' type='radio' value='true' checked><fmt:message bundle="${words}" key="resend_original_sender"/><br>" +
		"<input name='use_original_sender' type='radio' value='false'><fmt:message bundle="${words}" key="resend_admin_email"/><br>";

		params.okButtonClass = "medium_submit";
		params.cancelButtonClass = "medium_cancel";
		params.okButtonValue = '<fmt:message bundle="${words}" key="resend"/>';
		params.height = 220;
		params.cancelButtonValue = '<fmt:message bundle="${words}" key="cancel"/>';
		params.buttons = {
			'<fmt:message bundle="${words}" key="resend"/>': function () {
				var answer = $('input[name=use_original_sender]:checked').val();
				$("#confirmDialog").remove();
				resend(id, answer);
			},
			'<fmt:message bundle="${words}" key="cancel"/>': function () {
				$("#confirmDialog").remove();
			}
		};
		createDialog(params);
		return false;
	}

	function resend(id, useOriginalSender) {
		$.ajax({
			url: "ResendEmail",
			data: {
				id: id,
				useOriginalSender: useOriginalSender
			},
			success: function(responce) {
				if (responce == "success") {
					resendSuccess();
				} else {
					console.log("Error, incorrect result returned by resend function: " + responce);
				}
			},
			error: function(responce) {
				console.log("Error: " + responce);
			}
		})
	}

	function resendSuccess() {
		var params = new DefaultDialogParams();
		params.message = '<fmt:message bundle="${words}" key="resend_attempt_started"/><br>';
		params.okButtonValue = '<fmt:message bundle="${words}" key="ok"/>';
		params.height = 150;
		params.buttons = {
			'<fmt:message bundle="${words}" key="ok"/>': function () {
				$("#confirmDialog").remove();
			}
		};
		createDialog(params);
	}
</script>