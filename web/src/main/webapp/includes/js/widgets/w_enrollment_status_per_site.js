$(window).load(function() {
	initEnrollStatusPerSiteWidget("init");
	getEnrollStatusPerSiteLegendValues();
});

/**
 * Initialize Enrollment Status per Site widget.
 * 1. Init - will be run when user opens home page 
 * 2. Next - will upload data for next sites (if exists) 
 * 3. Back - will upload data for previous sites (if exists)
 * Depending on returned data - "Next" or "Previous" buttons will be hidden
 * 
 * @param action - type if action that which will be executed 
 * @returns enrollStatusPerSiteChart.jsp 
 */
function initEnrollStatusPerSiteWidget(action) {
	var url = getCurrentUrl();
	var lastElement = $(".enrollment_status_per_site input#esps_last_element").val();
	$(".enrollment_status_per_site .pop-up-visible").css('display', 'none');
	$(".enrollment_status_per_site .pop-up").css('display', 'none');
	if (lastElement == undefined) 
		lastElement = 0;
	$(".enrollment_status_per_site #enrollment_status_per_site_container").hide(500);
	$.ajax({
		type : "POST",
		url : url + "initEnrollStatusPerSiteWidget",
		data : {
			epPerSiteDisplay : lastElement,
			studyId : $("#studyId").val(),
			action : action
		},
		success : function(html) {
			$("#enrollment_status_per_site_container").html(html);
			$(".pop-up").css('display', 'none');
			var hasNext = $("#esps_next_page_exists").val();
			var hasPrevious = $("#esps_previous_page_exists").val();
			if (hasNext == "true")
				$("#esps_next").css("display", "block");
			else
				$("#esps_next").css("display", "none");

			if (hasPrevious == "true") 
				$("#esps_previous").css("display", "block");
			else
				$("#esps_previous").css("display", "none");

			$("#enrollment_status_per_site_container").show(500, function () {
				$(".enrollment_status_per_site .pop-up").css('display', '');
			});
			var rows = $("#enrollment_status_per_site_container .stacked_bar");
			var limit = getMaxRowLengths(rows);
			var captionLimit = countCaptionLimit(limit);
			var captionSelector = ".enrollment_status_per_site .captions td";
			setCaption(captionSelector, captionLimit);
			
			rows.each(function(entry) {
				var values = new Array();
				var total = 0;
				var stacks = $(this).find("li");
				stacks.each(function(index) {
					var currentValue = $(this).find(".hidden").html();
					if (currentValue < 0)
						currentValue = 0;
					if (currentValue) {
						values[index] = parseInt(currentValue);
						total += parseInt(currentValue);
					}
				});
				if (parseInt(total) != 0) {
					var currentBarClass = $(this).attr("barnumber");
					var selector = "#enrollment_status_per_site_container .stacked_bar[barnumber="
							+ (currentBarClass) + "] .stack";
					var expectedEnrollmentSelector = "div.right_text[barnumber=" + currentBarClass +"]";
					var rowLength = getRowLength(selector, total, captionLimit);
					setStacksLengths(selector, values, captionLimit);
					moveElementsToTheRight(expectedEnrollmentSelector, rowLength);
				}
			});
			var element = document.getElementById('toolbar');
			if (element) {
				$(".enrollment_status_per_site .chart_wrapper a").attr("href", "#");
				$(".enrollment_status_per_site .bar_name a").attr("href", "#");
			} else {
				activateEnrollStatusPerSiteLegend();
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/**
 * Function for adding onClick action for "Enrollment Status per Site" widget's legend
 * and bars.
 */
function activateEnrollStatusPerSiteLegend() {
	var urlPrefix = "ListStudySubjects?module=admin&maxRows=15&showMoreLink=false&findSubjects_tr_"
			+ "=true&findSubjects_p_=1&findSubjects_mr_=15&findSubjects_s_0_studySubject.createdDate="
			+ "desc&findSubjects_f_studySubject.status=";
	var statuses = [ "available", "removed", "signed", "locked" ];
	$(".enrollment_status_per_site .pop-up-visible").css('display', '');
	$(".enrollment_status_per_site .signs td").each(
			function(index) {
				$(this).click(
						function() {
							window.location.href = urlPrefix + statuses[index];
						});
				$(this).css("cursor", "pointer");
			});
}

/**
 * Function for activation legend for "Enrollment Status per Site"
 */
function getEnrollStatusPerSiteLegendValues() {
	var element = document.getElementById('toolbar');
	var statusNames = [ 'Available', 'Removed', 'Signed', 'Locked'];
	if (!element) {
		var url = getCurrentUrl();
		$.ajax({
			type : "POST",
			url : url + "getEnrollStatusPerSiteLegendValues",
			data : {
				studyId : $("#studyId").val()
			},
			success : function(html) {
				var resArray =  html.replace(/\s+|[\[\]]+/g,'').split(',');
				setDataToLegend("both", ".enrollment_status_per_site", resArray, statusNames);
				$(".enrollment_status_per_site .signs td").mouseover(function() {
					$(this).find(".popup_legend_medium").css({display : "block","margin-top":"-39px"});
				});
				$(".enrollment_status_per_site .signs td").mouseout(function() {
					$(this).find(".popup_legend_medium").css({display : "none"});
				});
			},
			error : function(e) {
				console.log("Error:" + e);
			}
		});
	}
}

