/**
 * Builds an absolute casebook URL based on the user's selection
 *
 * @returns {string}
 */
function buildCasebookUrl() {
    var casebookType = $("[name='casebookType']:checked").val();
    var casebookPdf = $("#casebookTypePdf").is(':checked');
    var baseUrl = window.location.href;
    if(casebookPdf) {
        baseUrl = baseUrl.substr(0, baseUrl.lastIndexOf("/")) + "/pages/generateCasebook?studyOid=" + parentStudyOid()
        + "&studySubjectOid=" + subjectOid();
    } else {
        baseUrl = baseUrl.substr(0, baseUrl.lastIndexOf("/")) + "/print/clinicaldata/"
        + casebookType + "/" + parentStudyOid() + "/" + subjectOid();
        baseUrl += "/" + "*" + "/" + "*";
    }
    var casebookParams = [];
    $("[name='casebookParam']:checked").each(function () {
        casebookParams.push($(this).val());
    });
    if (casebookParams.length > 0) {
        baseUrl = baseUrl.indexOf("&") > 0 ? baseUrl += "&" : baseUrl += "?";
        baseUrl += casebookParams.join("&");
    }

    return baseUrl;
}
/**
 * Updates casebook link text field, reflecting the user's selection
 */
function updateCasebookLinkBox() {
    $("#casebookLinkText").val(buildCasebookUrl());
}

/**
 * Triggers casebook link update when user changes any option
 */
$("[name='casebookType'],[name='casebookParam']").change(function () {
    updateCasebookLinkBox();
});

/**
 * Show/hides casebook link text field
 */
$("#casebookLinkBtn").click(function () {
    updateCasebookLinkBox();
    $("#casebookLinkDisplay").toggle();
});

/**
 * Open casebook on a new window
 */
$("#casebookOpenBtn").click(function () {
    openPrintCRFWindow(buildCasebookUrl());
});