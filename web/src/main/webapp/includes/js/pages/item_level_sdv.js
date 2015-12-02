var submitSuccessMessage;

function setSuccessMessage(message) {
	submitSuccessMessage = message;
}

function getItemsTableForCRFVersion(crfVersionId) {
	var url = getCurrentUrl();
	var edcId = $("input[name=edc_id]").val();
	$.ajax({
		type: "POST",
		url: url + "configureItemLevelSDV",
		data: {
			edcId: edcId,
			crfVersionId: crfVersionId
		},
		success: function (html) {
			setImageWithTitle('DataStatus_bottom','../images/icon_UnchangedData.gif', 'You have not changed any data in this page.');
			$(".sdv_tables_content").html(html);
		},
		error: function (e) {
			console.log("Error:" + e);
		}
	});
}

function submitItemLevelSDV(showDialog) {
	var jsonData = getItemsValuesInJson();
	var url = getCurrentUrl();
	var edcId = $("input[name=edc_id]").val();
	var versionId = $("#currentVersion").val();

	$.ajax({
		type: "POST",
		url: url + "configureItemLevelSDV",
		data: {
			submit: true,
			jsonData: JSON.stringify(jsonData),
			edcId: edcId,
			versionId: versionId
		},
		success: function (html) {
			setImageWithTitle('DataStatus_bottom','../images/icon_UnchangedData.gif', 'You have not changed any data in this page.');
			if (html != "success") {
				console.log(html)
			} else if (showDialog) {
				params = {
					message: submitSuccessMessage,
					width: 500,
					height: 150
				};
				alertDialog(params);
			} else {
				if (edcId) {
					window.location = "../UpdateEventDefinition?id=" + edcId;
				}
			}
		},
		error: function (e) {
			console.log("Error:" + e);
		}
	});
}

function submitItemLevelSDVAndExit() {
	submitItemLevelSDV(false);
}

function getItemsValuesInJson() {
	var data = [];
	var $inputs = $(".sdv_tables_content input[name^=sdv_requirement_s]:checked");
	$inputs.each(function () {
		var inputName = $(this).attr("name");
		var inputValue = $(this).val();
		var entry = {
			inputName: inputName,
			inputValue: inputValue
		};
		data.push(entry);
	});
	return {inputsData: data};
}