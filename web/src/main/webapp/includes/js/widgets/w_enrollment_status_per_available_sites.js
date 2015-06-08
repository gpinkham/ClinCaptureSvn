$(window).load(function() {
	initESPASWidget("init");
});

/**
 * Initialize Enrollment Status per Site widget.
 * 1. Init - will be run when user opens home page
 * 2. Forward - will upload data for next sites (if exists)
 * 3. Back - will upload data for previous sites (if exists)
 * Depending on returned data - "Next" or "Previous" buttons will be hidden
 *
 * @param action - type if action that which will be executed
 * @returns enrollStatusPerSiteChart.jsp
 */
function initESPASWidget(action) {
	var url = getCurrentUrl();
	var $form = $("#espas_form");
	var firstRowNum = $form.find(".first_row").val();
	if (firstRowNum == undefined)
		firstRowNum = 0;
	$.ajax({
		type : "POST",
		url : url + "initESPASWidget",
		data : {
			firstRowNum : firstRowNum,
			action : action
		},
		success : function(html) {
			var $container = $("#espas_container");
			$container.html(html);
			$form = $("#espas_form");
			var hasNext = $form.find(".show_next").val();
			var hasPrevious = $form.find(".show_back").val();
			if (hasNext == "true")
				$(".espas .forward").css("display", "block");
			else
				$(".espas .forward").css("display", "none");

			if (hasPrevious == "true")
				$(".espas .back").css("display", "block");
			else
				$(".espas .back").css("display", "none");

			var rows = $container.find(".stacked_bar");
			var limit = getMaxRowLengths(rows);
			var captionLimit = countCaptionLimit(limit);
			var captionSelector = ".espas .captions td";
			setCaption(captionSelector, captionLimit);

			rows.each(function() {
				var values = [];
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
					var selector = "#espas_container .stacked_bar[barnumber="
						+ (currentBarClass) + "] .stack";
					var expectedEnrollmentSelector = "div.right_text[barnumber=" + currentBarClass +"]";
					var rowLength = getRowLength(selector, total, captionLimit);
					setStacksLengths(selector, values, captionLimit);
					moveElementsToTheRight(expectedEnrollmentSelector, rowLength);
				}
			});
			var element = document.getElementById('toolbar');
			if (!element) {
				activateESPASLegend();
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

/**
 * Function for adding onClick action for "Enrollment Status per Available Sites" widget's legend
 * and bars.
 */
function activateESPASLegend() {
	var urlPrefix = "ListStudySubjects?module=admin&maxRows=15&showMoreLink=false&findSubjects_tr_"
		+ "=true&findSubjects_p_=1&findSubjects_mr_=15&findSubjects_s_0_studySubject.createdDate="
		+ "desc&findSubjects_f_studySubject.status=";
	var statuses = [];
	$("#espas_form").find(".status").each(function(){
		statuses.push($(this).val());
	});
	$($(".espas .signs td").get().reverse()).each(
		function(index) {
			$(this).click(
				function() {
					window.location.href = urlPrefix + statuses[index].toLowerCase().replace(/\s/g,"+");
				});
			$(this).css("cursor", "pointer");
		});

	getESPASLegendValues();
}

/**
 * Function for activation legend for "Enrollment Status per Site"
 */
function getESPASLegendValues() {
	var element = document.getElementById('toolbar');
	var statusNames = [];
	$($("#espas_form").find(".status").get().reverse()).each(function(){
		statusNames.push($(this).val());
	});
	if (!element) {
		var url = getCurrentUrl();
		$.ajax({
			type : "POST",
			url : url + "getESPASLegendValues",
			success : function(html) {
				var resArray =  html.replace(/\s+|[\[\]]+/g,'').split(',');
				setDataToLegend("both", ".espas", resArray, statusNames);
				var $sign =$(".espas .signs td");
				$sign.mouseover(function() {
					$(this).find(".popup_legend_medium").css({display : "block","margin-top":"-39px"});
				});
				$sign.mouseout(function() {
					$(this).find(".popup_legend_medium").css({display : "none"});
				});
			},
			error : function(e) {
				console.log("Error:" + e);
			}
		});
	}
}

