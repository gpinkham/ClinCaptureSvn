<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="words"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="notes"/>

<script>
	function showSessionExpireDialog() {
		var params = new DefaultDialogParams();
		params.okButtonValue = '<fmt:message bundle="${words}" key="resume_data_entry"/>';
		params.cancelButtonValue = '<fmt:message bundle="${words}" key="delete_data_and_exit"/>';
		params.height = 130;
		params.buttons = {
			'<fmt:message bundle="${words}" key="resume_data_entry"/>': function () {
				closeDialog(params.selector);
				showLoginPopUp();
			},
			'<fmt:message bundle="${words}" key="delete_data_and_exit"/>': function () {
				location.href("${pageContext.request.contextPath}/MainMenu");
			}
		};
		params.message = "<fmt:message bundle="${words}" key="session_timed_out"/>";
		createDialog(params);
	}

	/**********************************************************
	 * Abstract functions.
	 **********************************************************/

	/**
	 * Function that will create modal dialog with parameters that
	 * were set by user.
	 * @param selector string
	 * @param params DefaultDialogParams object
	 */
	function createDialog(params, selector) {
		if (!isVarEmpty(selector)) {
			params.selector = selector;
		}
		params = !isVarEmpty(params) ? params : new DefaultDialogParams();
		if(params.showButtons) {
			params.buttons = !isVarEmpty(params.buttons) ? params.buttons : params.getButtons();
		}
		var content = params.createBoundingBox ? getMessageWithBoundingBox(params) : params.message;
		$(content).appendTo("body");

		$(params.selector).dialog(params);
	}

	/**
	 * Function that will add bounding box for a message dialog.
	 * @param params DefaultDialogParams object
	 * @returns {string} message with bounding box.
	 */
	function getMessageWithBoundingBox(params) {
		return "<div id='" + params.bBoxId + "' title='"
				+ params.bBoxTitle + "'>"
				+ "<div style='clear: both; margin-top: 2%; text-align: justify'>"
				+ params.message + "</div></div>";
	}

	/**
	 * Close modal dialog.
	 * @param selector String - jQuery selector of the dialog.
	 */
	function closeDialog(selector) {
		$(selector).remove();
	}

	/**
	 * Function that check if som value was assigned to var or it's empty
	 * @param param variable that needs to be checked
	 * @returns {boolean}
	 */
	function isVarEmpty(param) {
		return param == null || param == undefined || param == "";
	}

	/**
	 * Object that will contain all properties of message dialog,
	 * and some additional methods required to set all content.
	 * @constructor
	 */
	var DefaultDialogParams = function () {
		this.autoOpen = true;
		this.closeOnEscape = false;
		this.modal = true;
		this.showButtons = true;
		this.height = 150;
		this.width = 600;
		this.createBoundingBox = true;
		this.bBoxTitle = '<fmt:message bundle="${words}" key="confirm_action" />';
		this.selector = "#confirmDialog";
		this.bBoxId = "confirmDialog";
		this.highlightedRow = undefined;
		this.redirectLink = undefined;
		this.aLink = undefined;
		this.checkbox = undefined;
		this.cancelButtonValue = '<fmt:message bundle="${words}" key="no" />';
		this.okButtonValue = '<fmt:message bundle="${words}" key="yes" />';
		var instance = this;
		this.getButtons = function () {
			return {
				'<fmt:message bundle="${words}" key="yes" />': function () {
					$(instance.selector).remove();
					if (instance.aLink) {
						window.location.href = $(instance.aLink).attr('href');
					} else if (instance.redirectLink) {
						window.location.href = instance.redirectLink;
					} else if (instance.checkbox) {
						var isChecked = $(instance.checkbox).is(':checked');
						$(instance.checkbox).attr("checked", !isChecked);
					}
				},
				'<fmt:message bundle="${words}" key="no" />': function () {
					$(instance.selector).remove();
				}
			}
		};
		this.open = function (event, ui) {
			openDialog({
				dialogDiv: instance.selector,
				cancelButtonValue: instance.cancelButtonValue,
				okButtonValue: instance.okButtonValue,
				imagesFolderPath: determineImagesPath()
			});

			if (instance.aLink && instance.highlightedRow) {
				setAccessedObjected(instance.aLink);
			}
		};
	};

	function getLoginFailureMessage(key) {
		if (key === "locked") {
			return '<fmt:message key="account_locked" bundle="${notes}"/>';
		} else {
			return '<fmt:message key="password_failed" bundle="${notes}"/>';
		}
	}
</script>