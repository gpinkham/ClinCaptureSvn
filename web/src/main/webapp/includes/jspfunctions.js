function doSubmit() {
	if (!filePathIsValid()) {
		return;
	}
    $("input").each(function () {
        if ($(this).attr("disabled") != undefined) {
            $(this).removeAttr("disabled");
        }
    });
    $("#systemForm").submit();
}

function filePathIsValid() {
	var $filePathInput = $("[property=filePath]").find("input.formfieldXL");
	if ($filePathInput.attr("disabled")) {
		return true;
	} else {
		var value = $filePathInput.val();
		if (value.slice(-1) != "/" && value.slice(-1) != "\\") {
			alertDialog({
				message: $("#fileNotFoundMessage").val(),
				height: 150,
				width: 500
			});
		} else {
			return true;
		}
	}
}

function cancel() {
    confirmDialog({
        message: '<fmt:message key="sure_to_cancel" bundle="${resword}"/>',
        height: 150,
        width: 500,
        redirectLink: "ListEventDefinition"
    });
}

function changeGroupState(themeColor, id) {
    if ($("#img_group_id_" + id).attr("src").indexOf("bt_Expand") >= 0) {
        $("#img_group_id_" + id).attr("src", "../images/" + themeColor + "bt_Collapse.gif");
        $("#div_group_id_" + id).removeClass("hidden");
        $("#div_sub_group_id_" + id).removeClass("hidden");
        $("#state_group_id_" + id).val("true");
    } else {
        $("#img_group_id_" + id).attr("src", "../images/" + themeColor + "bt_Expand.gif");
        $("#div_group_id_" + id + " div[id^=div_sub_group_id_] img[id^=img_group_id_]").attr("src", "../images/" + themeColor + "bt_Expand.gif");
        $("#div_group_id_" + id).addClass("hidden");
        $("#div_sub_group_id_" + id).addClass("hidden");
        $("#div_group_id_" + id + " div[id^=div_sub_group_id_] div[id^=div_group_id_]").addClass("hidden");
        $("#state_group_id_" + id).val("false");
        $("#div_group_id_" + id + " div[id^=div_sub_group_id_] input[id^=state_group_id_]").val("false");
    }
}

$(window).load(function() {
	if ($("#isRootUser").val() != "true") {
		$(".root_only").remove();
	}
});