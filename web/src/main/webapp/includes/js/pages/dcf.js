var pageContext;
function initDcf(context) {
	pageContext = context;
	$("#dcfRenderType").hide();
	toggleButtonEnable("dcf", "btn_generate_dcf");
	toggleButtonEnable("dcfRenderType", "btn_submit_dcf");
	setGenerateDcfClickEvent();
	setEmailControlFocusEvent();
	checkIfShouldPrintOrSave();
	setCheckAllOrNoneLinkClickEvent();
	setDcfIconClickEvent();
}

function setCheckAllOrNoneLinkClickEvent() {
	$("a.allcheckbox").each(function() {
		var a = $(this);
		var check = a.hasClass("check");
		a.click(function() {
			checkOrUncheckAllByClass("dcf", check);
			toggleButtonEnable("dcf", "btn_generate_dcf");
			return false;
		});
	});
}

function setGenerateDcfClickEvent() {
	$("#btn_generate_dcf").click(function() {
		$("#generateDcf").val("yes");
		showDcfRenderDialog();
		$(".dcfRenderType").click(function() {
			toggleButtonEnable("dcfRenderType", "btn_submit_dcf");
		});
		setDefaultEmail();
	});
}

function setEmailControlFocusEvent() {
	$("#email").focus(function() {
		checkEmailCheckbox();
	});
}

function setDcfIconClickEvent() {
	$("a.dcfIcon").each(function() {
		var a = $(this);
		var noteAndEntityId = a.attr("data-noteid");
		var siteEmail = a.attr("data-site-email");
		a.click(function() {
			appendNoteIdForClickedIconToForm(noteAndEntityId);
			$("#btn_generate_dcf").trigger("click");
			$("#email").val(siteEmail);
			return false;
		});
	});
}

function showDcfRenderDialog() {
	$("#dcfRenderType").show();
	$("#dcfRenderType").dialog({
		autoOpen : true,
		closeOnEscape : false,
		modal : true,
		width : 400,
		open : function() {
			openDialog({
				dialogDiv : this,
				closable : true,
				imagesFolderPath : determineImagesPath()
			});
		}
	});
	$("#btn_submit_dcf").click(function() {
		if (validateDcfRequest()) {
			appendPopupDivToDnForm();
			$("#dnform").submit();
			$("#generateDcf").val("");
			removeDcfIconValueFromForm();
		} else {
			$("#invalid_email").show();
			setTimeout(function() {
				$("#invalid_email").fadeOut(2000);
			}, 2000);
		}
	});
	$("#btn_cancel_dcf").click(function() {
		$("#generateDcf").val("");
		appendPopupDivToDnForm();
		removeDcfIconValueFromForm();
	});
}

function appendPopupDivToDnForm() {
	var div = $("#dcfRenderType");
	div.remove();
	$("#dnform").append(div);
}

function removeDcfIconValueFromForm() {
	if ($("#dcfIconId").length) {
		$("#dcfIconId").remove();
	}
}

function setDefaultEmail() {
	$("#email").val("");
	var checkboxes = "input[type=checkbox][class=dcf]";
	$(checkboxes).each(function() {
		if (this.checked) {
			$("#email").val($(this).attr("data-site-email"));
			return false;
		}
	});
}

function appendNoteIdForClickedIconToForm(noteAndEntityId) {
	if ($("#dcfIconId").length) {
		$("#dcfIconId").val(noteAndEntityId);
	} else {
		var noteIdElement = "<input id='dcfIconId' name='dcfIconId' type='hidden' value='"
				+ noteAndEntityId + "' />";
		$("#dnform").append(noteIdElement);
	}
}

function validateDcfRequest() {
	var valid = true;
	$("input[type=checkbox][class=dcfRenderType]")
			.each(
					function() {
						if (this.checked && $(this).val() == "email") {
							var email = $.trim($("#email").val());
							$("#email").val(email);
							var emailReg = new RegExp(
									/^[\w\-\.\+]+\@[a-zA-Z0-9\.\-]+\.[a-zA-z0-9]{2,4}$/);
							valid = emailReg.test(email);
							if (!valid) {
								return valid;
							}
						}
					});
	return valid;
}

function checkEmailCheckbox() {
	$("input[type=checkbox][class=dcfRenderType]").each(function() {
		if ($(this).val() == "email" && !this.checked) {
			this.checked = true;
			$(this).trigger("click");
			this.checked = true;
		}
	});
}

function toggleButtonEnable(checkBoxClass, buttonId) {
	var checkboxes = "input[type=checkbox][class=" + checkBoxClass + "]";
	var button = $("#" + buttonId);
	var atleastOneChecked = false;
	$(checkboxes).each(function() {
		if (this.checked) {
			atleastOneChecked = true;
			return false;
		}
	});
	if (atleastOneChecked) {
		button.show();
	} else {
		button.hide();
	}
}

function checkIfShouldPrintOrSave() {
	var shouldPrint = $("#printDcf").val() == "yes";
	var shouldSave = $("#saveDcf").val() == "yes";
	if (shouldPrint && shouldSave) {
		printDcf();
		setTimeout(function() {
			saveDcf()
		}, 2000);
	} else if (shouldPrint) {
		printDcf();
	} else if (shouldSave) {
		saveDcf();
	}
}

function printDcf() {
	openDocWindow(pageContext + "/ViewNotes?module=submit&printDcf=yes");
}

function saveDcf() {
	var url = pageContext + "/ViewNotes?module=submit&saveDcf=yes";
    window.open(url, "_blank");
}