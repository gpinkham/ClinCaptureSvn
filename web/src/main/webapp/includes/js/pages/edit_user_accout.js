function myCancel() {
	cancelButton = document.getElementById('cancel');
	if (cancelButton != null) {
		if (confirm($("sure_to_cancel").val())) {
			window.location.href = "ListUserAccounts";
			return true;
		} else {
			return false;
		}
	}
	return true;
}

function a() {
	if (document.getElementById('resetPassword').checked) {
		document.getElementById('displayPwd0').disabled = false
		document.getElementById('displayPwd1').disabled = false
	} else {
		document.getElementById('displayPwd0').disabled = true
		document.getElementById('displayPwd1').disabled = true
	}
}

var editUserFormState = {};

function saveEditUserFormState(stateHolder) {
	stateHolder.firstName = $("input[name=firstName]").val();
	stateHolder.lastName = $("input[name=lastName]").val();
	stateHolder.email = $("input[name=email]").val();
	stateHolder.phone = $("input[name=phone]").val();
	stateHolder.institutionalAffiliation = $("input[name=institutionalAffiliation]").val();
	stateHolder.userType = $("select[name=userType]").val();
	stateHolder.runWebServices = $("input[name=runWebServices]").val();
	stateHolder.resetPassword = $("input[name=resetPassword]").val();
}

function back_checkEditUserFormState() {
	var newState = {};
	saveEditUserFormState(newState);
	if (editUserFormState.firstName != newState.firstName || editUserFormState.lastName != newState.lastName || editUserFormState.phone != newState.phone || editUserFormState.email != newState.email ||
		editUserFormState.institutionalAffiliation != newState.institutionalAffiliation || editUserFormState.userType != newState.userType || editUserFormState.runWebServices != newState.runWebServices ||
		editUserFormState.resetPassword != newState.resetPassword) {
		confirmBackSmart($("#you_have_unsaved_data2").val(), $("#navigationURL").val(), $("#defaultURL").val());
	}
	else {
		goBackSmart($("#navigationURL").val(), $("#defaultURL").val());
	}
}

$(window).load(function(){
	saveEditUserFormState(editUserFormState);
	resetFormAction();
	a();
});

function redirectRequestToMaskingPage() {
	$("form#edit_user").attr("action", "CRFsMasking").submit();
}

function resetFormAction() {
	$("form#edit_user").attr("action", "EditUserAccount");
}