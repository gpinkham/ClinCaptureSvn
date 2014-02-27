/* Customize page */
function tollbarToogle(e) {
	if ($(e).children().text() == "Hide") {
		$(e).parent().animate({
			right : "-412px"
		}, 500);
		$(e).children().html("Show");
	} else {
		$(e).parent().animate({
			right : "0px"
		}, 500);
		$(e).children().html("Hide");
	}
}

if (!Array.prototype.forEach) {
	Array.prototype.forEach = function(fn, scope) {
		for ( var i = 0, len = this.length; i < len; ++i) {
			fn.call(scope, this[i], i, this);
		}
	}
}

function inicializeDnD() {
	$(".droptrue").sortable({
		connectWith : '.droptrue',
		opacity : 0.6,
		update : updatePostOrder
	});

	$("#toolbar, #layout").disableSelection();
	$(".widget .chart_wrapper a").attr("href", "#");
	$(".widget input[type=button]").remove();
	updatePostOrder();
}

function updatePostOrder() {
	var arr = [];
	$(".column1 div.widget").each(function() {
		arr.push($(this).attr('id'));
	});
	$('#postOrder1').val(arr.join(','));

	var arr2 = [];
	$(".column2 div.widget").each(function() {
		arr2.push($(this).attr('id'));
	});
	$('#postOrder2').val(arr2.join(','));

	var arr3 = [];
	$("#toolbar div.widget").each(function() {
		arr3.push($(this).attr('id'));
	});
	$('#unusedWidgets').val(arr3.join(','));
}

function removeWidget(elemet) {
	var itemToRemove = $(elemet).parent();
	$("#toolbar").prepend(itemToRemove);
	updatePostOrder();
}

function saveLayoutAndExit() {
	$.ajax({
		type : "POST",
		url : "saveHomePage",
		data : {
			orderInColumn1 : $("#postOrder1").val(),
			orderInColumn2 : $("#postOrder2").val(),
			unusedWidgets : $("#unusedWidgets").val(),
			userId : $("#userId").val(),
			studyId : $("#studyId").val()
		},
		success : function() {
			$("form[id=goToHomePage]").submit();
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}
/* /Customize page */

/* Initialization of widgets */
function initNdsAssignedToMeWidget() {
	var url = getCurentUrl();

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
				$(".widget table.signs td.optional").css("display",
						"table-cell");
			}

			var totalNds = newNds + updatedNds + closedNds
					+ resolutionProposedDns;
			captionLimit = countCaptionLimit(totalNds);
			var captionSelector = ".dns_assigned_to_me .captions td";
			setCaption(captionSelector, captionLimit);
			var valuesSecelctor = ".dns_assigned_to_me #stack";
			setStacksLenghts(valuesSecelctor, array, captionLimit);
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

function initEventsCompletionWidget(action) {
	var url = getCurentUrl();
	var lastElement = $(".events_completion input#last_element").val();

	if (lastElement == undefined) {
		lastElement = 0;
	}

	$(".events_completion #events_completion_container").hide(500);

	$.ajax({
		type : "POST",
		url : url + "initEventsCompletionWidget",
		data : {
			lastElement : lastElement,
			studyId : $("#studyId").val(),
			action : action
		},
		success : function(html) {
			$(".events_completion #events_completion_container").html(html);
			var hasNext = $("#events_completion_form #has_next").val();
			var hasPrevious = $("#events_completion_form #has_previous").val();

			if (hasNext == "true") {
				$(".events_completion input#next").css("display", "block");
			} else {
				$(".events_completion input#next").css("display", "none");
			}

			if (hasPrevious == "true") {
				$(".events_completion input#previous").css("display", "block");
			} else {
				$(".events_completion input#previous").css("display", "none");
			}
			$(".events_completion #events_completion_container").show(500);

			var stack = $("#events_completion_container #stacked_bar");
			stack.each(function(entry) {
				var values = new Array();
				var total = 0;
				var stack = $(this).find("li");
				stack.each(function(index) {
					var currentValue = $(this).find(".hidden").html();
					if (currentValue) {
						values[index] = parseInt(currentValue);
						total += parseInt(currentValue);
					}
				});
				if (parseInt(total) != 0) {
					var currentBarClass = $(this).attr("class");
					var selector = "#events_completion_container #stacked_bar."
							+ (currentBarClass) + " #stack";
					setStacksLenghts(selector, values, total);
				}
			});
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}
/* /Initialization of widgets */

/* Supporting functions */
function updateStucksNumbers() {
	$(".events_completion #stacked_bar").each(function(index) {
		$(this).attr("id", "stacked_bar" + index);
	});
}

function countCaptionLimit(total) {
	if (total < 4) {
		total = 4;
	} else if (total % 2 == 0) {
		(total / 2) % 2 == 0 ? total : total = total + 2;
	} else {
		(total + 1) % 4 == 0 ? total = total + 1 : total = total + 3;
	}
	return total;
}

function setCaption(selector, maxValue) {
	var counter = 0;
	$(selector).each(function() {
		if (counter == 0) {
			$(this).html(0);
		} else if (counter == 1) {
			$(this).html(maxValue / 4);
		} else if (counter == 2) {
			$(this).html(maxValue / 2);
		} else if (counter == 3) {
			$(this).html(maxValue / 4 * 3);
		} else if (counter == 4) {
			$(this).html(maxValue);
		}

		counter++;
	});
}

function setStacksLenghts(selector, values, captionLimit) {
	var barWidth = parseInt($(selector).parent().parent().width());
	var unitSize = barWidth / captionLimit;
	var counter = 0;
	$(selector).each(
			function(index) {
				var stackWidth = parseInt(values[counter], 10) * unitSize;
				$(this).animate({
					width : stackWidth
				}, 500);

				$(this).find("#pop-up").css("margin-left",
						(parseInt(values[counter]) * unitSize) / 2).html(
						parseInt(values[counter]));

				counter++;
			});
}

function getCurentUrl() {
	var urlTemp = new RegExp("^.*(pages)")
			.exec(window.location.href.toString());
	var url = "";

	if (urlTemp == null) {
		url = "pages/";
	} else {
		url = "";
	}
	return url;
}
/* /Supporting functions */