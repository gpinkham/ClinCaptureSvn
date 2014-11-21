/*
 * Initialize NDs Assigned To Me widget.
 */
$(window).load(function() {
	initNdsAssignedToMeWidget();
});

function initNdsAssignedToMeWidget() {
	var url = getCurrentUrl();
	$.ajax({
		type : "POST",
		url : url + "initNdsAssignedToMeWidget",
		data : {
			userId : $("#userId").val(),
			studyId : $("#studyId").val()
		},
		success : function(html) {
			var array = html.split(',');
			var newNds = parseInt(array[0]);
			var updatedNds = parseInt(array[1]);
			var resolutionProposedDns = parseInt(array[2]);
			var closedNds = parseInt(array[3]);
			if (resolutionProposedDns != 0) {
				$(".dns_assigned_to_me td.optional").css("display",
						"table-cell");
				$(".dns_assigned_to_me a.optional").css("display",
						"inline-block");
			}
			var totalNds = newNds + updatedNds + closedNds
					+ resolutionProposedDns;
			var captionLimit = countCaptionLimit(totalNds);
			var captionSelector = ".dns_assigned_to_me .captions td";
			setCaption(captionSelector, captionLimit);
			var valuesSelector = ".dns_assigned_to_me .stack";
			setStacksLengths(valuesSelector, array, captionLimit);
			var element = document.getElementById('toolbar');
			if (!element) {
				setDataToLegend("percents",".dns_assigned_to_me",array);
				activateNDsWidgetLegend();
			} else
				$(".dns_assigned_to_me .pop-up-visible").css('display', 'none');
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/*
 * Function for adding onClick action for "NDs assigned to me" widget's legend
 * and bars.
 */
function activateNDsWidgetLegend() {
	var urlPrefix = "ViewNotes?module=submit&listNotes_f_discrepancyNoteBean.user=";
	var currentUser = $("form#ndsWidgetForm input#cUser").val();
	var urlSufix = "&listNotes_f_discrepancyNoteBean.resolutionStatus=";
	var statuses = [];
	$(".dns_assigned_to_me .status").each(function(){
		statuses.push($(this).val());
	});
	$(".dns_assigned_to_me .pop-up-visible").css('display', 'table');
	$(".dns_assigned_to_me .stacked_bar a").each(
			function(index) {
				$(this).attr("href",
						urlPrefix + currentUser + urlSufix + statuses[index]);
			});
	$(".dns_assigned_to_me .signs td").each(
			function(index) {
				$(this).click(
						function() {
							window.location.href = urlPrefix + currentUser
									+ urlSufix + statuses[index];
						});
				$(this).css("cursor", "pointer");
			});
	$(".dns_assigned_to_me .signs td").mouseover(function() {
		$(this).find(".popup_legend_min").css({display : "block","margin-top":"-20px"});
	});
	$(".dns_assigned_to_me .signs td").mouseout(function() {
		$(this).find(".popup_legend_min").css({display : "none"});
	});
}
