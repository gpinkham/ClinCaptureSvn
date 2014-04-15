function doSubmit() {
    $("input").each(function () {
        if ($(this).attr("disabled") != undefined) {
            $(this).removeAttr("disabled");
        }
    });
    $("#systemForm").submit();
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