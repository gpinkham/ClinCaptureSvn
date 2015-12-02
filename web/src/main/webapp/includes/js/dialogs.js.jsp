<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="words"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="notes"/>

<script>
	function showLoadingDialog(){
		if ($("#alertBox").length == 0) {
			var params = new DefaultDialogParams();
			params.height = 150;
			params.bBoxTitle = "";
			params.bBoxId = "alertBox";
			params.selector = "#alertBox";
			params.message = "<div style='text-align: center;'><fmt:message bundle="${words}" key="your_file_being_uploaded"/><br/><br/>" +
			"<img style='display:block;margin:auto;' src='images/ajax-loader-blue.gif'/></div>";
			params.width = 450;
			params.showButtons = false;
			params.open = function() {
				openDialog({
					dialogDiv: this,
					imagesFolderPath: determineImagesPath()
				});
			};
			createDialog(params);
		}
	}

	function showMedicalCodingAlertBox(ajaxResponse){
		if ($("#alertBox").length == 0) {
			var params = new DefaultDialogParams();
			params.height = 150;
			params.bBoxTitle = "<fmt:message bundle="${words}" key="medical_coding_process_message"/>";
			params.bBoxId = "alertBox";
			params.selector = "#alertBox";
			params.message = "<div style='text-align: center;'><fmt:message bundle="${words}" key="retrieving_medical_codes"/><br/>" +
					"<img style='display:block;margin:auto;' src='../images/ajax-loader-blue.gif'/></div>";
			params.width = 450;
			params.buttons = { '<fmt:message bundle="${words}" key="cancel"/>': function() { hideMedicalCodingAlertBox(ajaxResponse); }};
			params.open = function(event, ui) {
				openDialog({
					dialogDiv: this,
					cancelButtonValue: "<fmt:message bundle="${words}" key="cancel"/>",
					cancelButtonClass: "medium_cancel",
					imagesFolderPath: determineImagesPath()
				});
			};
			createDialog(params);
		}
	}

	function showDeleteCodingTermDialog(item) {
		var params = new DefaultDialogParams();
		params.bBoxId = 'alertBox';
		params.selector = '#alertBox';
		params.bBoxTitle = "<fmt:message bundle="${words}" key="warning_message"/>";
		params.message = "<fmt:message bundle="${words}" key="check_item_you_want_to_delete"/><br><br>" +
						"<input name='answer' type='radio' value='Code'><fmt:message bundle="${words}" key="code"/>" +
						"<input name='answer' type='radio' value='Alias'><fmt:message bundle="${words}" key="alias"/>" +
						"<input name='answer' type='radio' value='Both'><fmt:message bundle="${words}" key="both"/>";
		params.width = 500;
		params.height = 160;
		params.okButtonClass = "medium_submit";
		params.cancelButtonClass = "medium_cancel";
		params.okButtonValue = '<fmt:message bundle="${words}" key="submit"/>';
		params.cancelButtonValue = '<fmt:message bundle="${words}" key="cancel"/>';
		params.buttons = {
			'<fmt:message bundle="${words}" key="submit"/>': function () {
				var answer = $('input[name=answer]:checked').val();
				if (answer == 'Code') {
					uncodeCodeItem(item);
				} else if (answer == 'Alias') {
					deleteTerm(item);
				} else if (answer == 'Both') {
					uncodeCodeItem(item);
					deleteTerm(item);
				}
				$("#alertBox").remove();
			},
			'<fmt:message bundle="${words}" key="cancel"/>': function () {
				$("#alertBox").remove();
			}
		};
		createDialog(params);
	}

	function showConfirmDeleteCodingTermDialog(item) {
		var params = new DefaultDialogParams();
		params.bBoxId = 'alertBox';
		params.selector = '#alertBox';
		params.bBoxTitle = "<fmt:message bundle="${words}" key="warning_message"/>";
		params.message = "<fmt:message bundle="${words}" key="are_you_sure_you_want_to_delete_this_code"/>";
		params.width = 400;
		params.okButtonClass = "medium_submit";
		params.cancelButtonClass = "medium_cancel";
		params.okButtonValue = '<fmt:message bundle="${words}" key="submit"/>';
		params.cancelButtonValue = '<fmt:message bundle="${words}" key="cancel"/>';
		params.buttons = {
			'<fmt:message bundle="${words}" key="submit"/>': function() {
				uncodeCodeItem(item); $("#alertBox").remove();
			},
			'<fmt:message bundle="${words}" key="cancel"/>': function() {
				$("#alertBox").remove();
			}
		};
		createDialog(params);
	}

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
				location.href = "${pageContext.request.contextPath}/MainMenu";
			}
		};
		params.message = "<fmt:message bundle="${words}" key="session_timed_out"/>";
		createDialog(params);
	}

	function showChangedSDVConfigMessage(version) {
		var params = new DefaultDialogParams();
		params.height = 150;
		params.buttons = {
			'<fmt:message bundle="${words}" key="yes"/>': function () {
				closeDialog(params.selector);
				submitItemLevelSDV(true);
				setTimeout(function() {
					getItemsTableForCRFVersion(version);
				}, 2000)
			},
			'<fmt:message bundle="${words}" key="no"/>': function () {
				closeDialog(params.selector);
				getItemsTableForCRFVersion(version);
			}
		};
		params.message = "<fmt:message bundle="${words}" key="item_sdv_configuration_changed_save_it"/>";
		createDialog(params);
	}

	function showRandomizationDisabledDialog() {
		alertDialog({ message: "<fmt:message bundle="${words}" key="randomization_disabled_update_plan"/>", height: 150, width: 500 });
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
		this.okButtonClass;
		this.cancelButtonClass;
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
				okButtonClass: instance.okButtonClass,
				cancelButtonClass: instance.cancelButtonClass,
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
