function goBackAndSaveParams() {
	localStorage.setItem("studyId", $("[name=studyId]").val());
	history.go(-1);
}

function restoreParamsFromBack() {
	var studyId = localStorage.getItem("studyId");
	if (studyId != null){
		$("input[name=studyId][value=" + studyId + "]").attr('checked', true);
		localStorage.removeItem("studyId");
	}
}