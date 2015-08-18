var loginForm;
var currentUserName;

function calcOffset() {
	var serverTime = getCookie('serverTime');
	serverTime = serverTime == null ? null : Math.abs(serverTime);
	var clientTimeOffset = (new Date()).getTime() - serverTime;
	setCookie('clientTimeOffset', clientTimeOffset);
}

$(window).load(function () {
	getLoginForm();
	getUserName();
	prepareSession();
});

function prepareSession() {
	calcOffset();
	checkSession();
}

function checkSession() {
	var sessionExpiry = Math.abs(getCookie('sessionExpiry'));
	var timeOffset = Math.abs(getCookie('clientTimeOffset'));
	var localTime = (new Date()).getTime();
	if (localTime - timeOffset > (sessionExpiry - 3000)) {
		showSessionExpireDialog();
	} else {
		setTimeout('checkSession()', 3000);
	}
}

function getLoginForm() {
	$.ajax({
		type: "POST",
		url: "pages/includes/getPageContent",
		data: {
			page: "login-include/login-box"
		},
		success: function (html) {
			loginForm = html;
		},
		error: function (e) {
			console.log("Error:" + e);
		}
	});
}

function showLoginPopUp() {
	params = new DefaultDialogParams();
	params.message = loginForm;
	params.showButtons = false;
	params.height = 'auto';
	params.width = 225;
	params.bBoxTitle = "";
	createDialog(params);

	$("#username").val(currentUserName).attr("disabled", true);
	$(".ui-dialog-titlebar").remove();
	$("#requestPassword").parent().remove();
	$("#exit").css("display", "inline").css("color", theme.mainColor);
	$("#exit a, #login h1").css("color", theme.mainColor);
	var imageOptions = {
		imagesFolderPath: determineImagesPath()
	};
	if (theme.name != 'blue') {
		$(".loginbutton").css('background-image', 'url(' + determineImagesPath() + theme.name + '/loginbutton_BG.gif)');
	}

	$(".loginbutton[type=submit]").click(function (event) {
		event.preventDefault();
		loginViaAjax();
	});
}

function loginViaAjax() {
	$(".login_alertbox_center").remove();
	$.ajax({
		type: "POST",
		url: "j_spring_security_check",
		data: {
			j_username: currentUserName,
			j_password: $("#j_password").val(),
			domain_name: $("[name=domain_name]").val(),
			shouldSessionParametersBeRestored: "true"
		},
		beforeSend: function (xhr) {
			xhr.setRequestHeader("X-Ajax-call", "true");
		},
		success: function (html) {
			if (html === "success") {
				closeDialog("#confirmDialog");
				setTimeout('prepareSession()', 10000);
			} else {
				$("#login").append("<div class='login_alertbox_center'>" + getLoginFailureMessage(html) + "</div>")
			}
		},
		error: function (e) {
			console.log("Error:" + e);
		}
	});
}

function getUserName() {
	currentUserName = $("#currentUser").val();
}