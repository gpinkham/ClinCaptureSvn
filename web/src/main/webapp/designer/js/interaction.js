/* ===================================================================================================================================================================================================================================================================================================================================================================================================================================================================
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO‚ÄôS ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 * =================================================================================================================================================================================================================================================================================================================================================================================================================================================================== */

var editing = true;
var parser = new Parser();

$(function() {
	// Get url component
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment?read=true&restore=true");
	// ======================= Tool tip creation =======================
	$("#deleteButton").tooltip({
		container: "body",
		placement: "top",
		trigger: "hover focus",
		title: "Press to clear the current rule expression"
	});
	$(".target").tooltip({
		container: "body",
		placement: "auto",
		trigger: "hover focus",
		title: "Apply the rule to the selected CRF item(s). Drag and drop CRF item(s) to target the rule.  Multiple targets can be selected. To replace a target, drop another target on it."
	});
	$("#ruleName").tooltip({
		container: "body",
		placement: "bottom",
		trigger: "hover focus",
		title: "Enter a description for the new rule"
	});
	createToolTip({
		title: "Start grouping",
		element: $(".leftPAREN")
	});
	createToolTip({
		title: "End grouping",
		element: $(".rightPAREN")
	});
	createToolTip({
		title: "And...",
		element: $(".and")
	});
	createToolTip({
		title: "Or...",
		element: $(".or")
	});
	createToolTip({
		title: "Equal",
		element: $(".eq")
	});
	createToolTip({
		title: "Not equal",
		element: $(".neq")
	});
	createToolTip({
		title: "Less than",
		element: $(".lt")
	});
	createToolTip({
		title: "Greater than",
		element: $(".gt")
	});
	createToolTip({
		title: "Less than or equal to",
		element: $(".lte")
	});
	createToolTip({
		title: "Greater than or equal to",
		element: $(".gte")
	});
	createToolTip({
		title: "Contains",
		element: $(".ct")
	});
	createToolTip({
		element: $(".nct"),
		title: "Does not contain"
	});
	createToolTip({
		title: "Plus",
		element: $(".plus")
	});
	createToolTip({
		title: "Minus",
		element: $(".minus")
	});
	createToolTip({
		title: "Divide by",
		element: $(".divide")
	});
	createToolTip({
		title: "Multiplied by",
		element: $(".mult")
	});
	createToolTip({
		element: $(".number"),
		title: "Integer and float number field"
	});
	createToolTip({
		title: "Null field",
		element: $(".empty")
	});
	createToolTip({
		title: "Date field",
		element: $(".date")
	});
	createToolTip({
		title: "Text field",
		element: $(".text")
	});
	createToolTip({
		title: "Current system date",
		element: $(".current-date")
	});
	createToolTip({
		title: "Select a study",
		element: $("#studiesLink")
	});
	createToolTip({
		element: $("#eventsLink"),
		title: "Select a study event"
	});
	createToolTip({
		element: $("#crfsLink"),
		title: "Select an event CRF"
	});
	createToolTip({
		element: $("#versionsLink"),
		title: "Select a CRF Version"
	});
	createToolTip({
		element: $("#itemsLink"),
		title: "Select a CRF item"
	});
	createToolTip({
		element: $(".dotted-border"),
		title: "Drag and drop an item from the Group tool box, the data toolbox or a CRF item."
	});
	createToolTip({
		element: $(".item"),
		title: "Drag and drop a CRF item."
	});
	createToolTip({
		element: $(".eventify"),
		title: "Click to bind the target to the event."
	});
	createToolTip({
		element: $(".versionify"),
		title: "Click to bind the target to the crf version."
	});
	createToolTip({
		element: $(".linefy"),
		title: "Specify the line number in the repeating group to which the rule will apply"
	});
	createToolTip({
		element: $(".opt"),
		title: "Specify target options"
	});

	// ======================= End of tool tip creation =======================
	// Hide action messages parameter divs
	$(".dotted-border-lg").hide();
	$(".dotted-border-lg").children().hide();

	// ======================= Draggables =======================
	createDraggable({
		element: $(".group"),
		target: $(".dotted-border")
	});

	$(".condition").find("p").each(function() {
		createDraggable({
			element: $(this),
			target: $(".eval")
		});
	});

	$(".compare").find("p").each(function() {
		createDraggable({
			element: $(this),
			target: $(".comp")
		});
	});

	$(".calculate").find("p").each(function() {
		createDraggable({
			element: $(this),
			target: $(".comp")
		});
	});

	$(".data").find("p").each(function() {
		createDraggable({
			element: $(this),
			target: ".group, .value"
		});
	});

	// ======================= End of creating draggables =======================

	// ======================= Droppables =======================
	createDroppable({
		element: $(".dotted-border"),
		accept: ".leftPAREN, .data p, div[id='items'] td"
	});

	createDroppable({
		element: $(".target"),
		accept: "div[id='items'] td"
	});

	createDroppable({
		element: $(".item"),
		accept: "div[id='items'] td"
	});

	createDroppable({
		element: $(".dest"),
		accept: "div[id='items'] td"
	});

	createDroppable({
		element: $(".value"),
		accept: ".data p, div[id='items'] td"
	});

	// ======================= End of creating droppables =======================

	// ============================= Event handlers =============================
	$(document).on('blur', '#ruleName', function() {
		parser.setName($(this).val());
	})
	// Handles the setting of what the rule evaluates to
	$(document).on('change', 'input[name="ruleInvoke"]', function() {
		if ($("#evaluateTrue").is(":checked")) {
			parser.setEvaluates(true);
		} else {
			parser.setEvaluates(false);
		}
	});
	// Handles the setting of initial data entry action
	$(document).on('change', '#ide', function() {
		parser.setInitialDataEntryExecute($(this).is(":checked"));
	});
	// Handles the setting of administrative editing action
	$(document).on('change', '#ae', function() {
		parser.setAdministrativeEditingExecute($(this).is(":checked"));	
	});
	// Handles the setting of double data entry action
	$(document).on('change', '#dde', function() {
		parser.setDoubleDataEntryExecute($(this).is(":checked"));
	});
	// Handles the setting of data import action
	$(document).on('change', '#dataimport', function() {
		parser.setDataImportExecute($(this).is(":checked"));
	});
	// handles the setting of target delete action
	$(document).on('click', '.glyphicon-remove', function() {
		parser.deleteTarget(this);
	});
	// === Enable addition event OID to targets ===
	$(document).on('change', '.eventify', function() {
		parser.eventify(this);
	});
	// handles the setting of a specific version to a target
	$(document).on('change', '.versionify', function() {
		parser.versionify(this);
	});
	// Handles the setting of a specific line in a repeating item
	$(document).on('blur', '.linefy', function() {
		parser.linefy(this);
	});
	// Handles clicks on item draggable
	$(document).on('click', '.target, .dest, .value, .item', function() {
		showCRFItem(this);
	});
	$(document).on('click', '.opt', function() {
		var itemName = $(this).prev().find('.target').val();
		if (itemName) {
			// Check event duplication
			var eventDuplex = parser.isDuplicated({
				name: itemName,
				type: "eventOid"
			});
			if (!eventDuplex) {
				$(this).prev().find(".eventify").parent().removeClass("hidden");
			}
			// Check version duplication
			var versionDuplex = parser.isDuplicated({
				name: itemName,
				type: "crfVersionOid"
			});
			if (!versionDuplex) {
				$(this).prev().find(".versionify").parent().removeClass("hidden");
			}
			// Check if target is repeat item
			if (parser.isRepeatItem(itemName)) {
				var liner = $(this).prev().find(".linefy");
				liner.removeClass("hidden");
				liner.focus();
				liner.siblings(".target").css("width", "89%");
			}
		}
	});
	// === Discrepancy action ====
	$('input[action=discrepancy]').click(function() {
		parser.resetActions(this);
		var checked = $(this).attr("previous-state");
		if (checked == 'checked') {
			parser.setDiscrepancyAction({
				selected: false
			});
			$(this).attr("previous-state", false);
		} else {
			parser.setDiscrepancyAction({
				selected: true,
				message: $('.discrepancy-properties').find('textarea').val()
			});
			$(this).attr("previous-state", 'checked');
			$('.discrepancy-properties').find('textarea').focus();
		}
	});

	$('.discrepancy-properties').find('textarea').blur(function() {
		parser.setDiscrepancyAction({
			message: $(this).val(),
			selected: $('input[action=discrepancy]').is(":checked")
		});
	});

	// === Email action ====
	$("input[action=email]").click(function() {
		parser.resetActions(this);
		var checked = $(this).attr("previous-state");
		if (checked == 'checked') {
			parser.setEmailAction({
				selected: false
			});
			$(this).attr("previous-state", false);
		} else {
			parser.setEmailAction({
				to: $(".to").val(),
				message: $(".body").val(),
				selected: $(this).is(":checked")
			});
			$(".to").focus();
			$(this).attr("previous-state", 'checked');
		}
	});

	$(".to").blur(function() {
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		if (!re.test($(this).val().trim())) {
			$(this).parent().addClass("has-error");
		} else {
			parser.setEmailAction({
				to: $(".to").val(),
				message: $(".body").val(),
				selected: $("input[action=email]").is(":checked")
			});
			$(this).parent().removeClass("has-error");
		}
	});

	$(".body").blur(function() {
		parser.setEmailAction({
			to: $(".to").val(),
			message: $(".body").val(),
			selected: $("input[action=email]").is(":checked")
		});
	});

	// === Insert action ====
	$("input[action=insert]").click(function() {
		parser.resetActions(this);
		var checked = $(this).attr("previous-state");
		if (checked == 'checked') {
			parser.setInsertAction({
				selected: false
			});
			$(this).attr("previous-state", false);
		} else {
			parser.setInsertAction({
				selected: $(this).is(":checked")
			});
			$(".insert").find("textarea").focus();
			$(this).attr("previous-state", 'checked');
		}
	});

	$(".value").blur(function() {
		parser.setDestinationValue({
			value: $(this).val(),
			id: $(this).parents(".row").attr("id")
		});
	});

	// === Show/Hide action ====
	$("input[action=show]").click(function() {
		parser.resetActions(this);
		var checked = $(this).attr("previous-state");
		if (checked == 'checked') {
			parser.setShowHideAction({
				show: false,
				hide: false
			});
			$(this).attr("previous-state", false);
		} else {
			parser.setShowHideAction({
				show: true,
				hide: false
			});
			$(this).attr("previous-state", 'checked');
		}
	});

	$("input[action=hide]").click(function() {
		parser.resetActions(this);
		var checked = $(this).attr("previous-state");
		if (checked == 'checked') {
			parser.setShowHideAction({
				hide: false,
				show: false
			});
			$(this).attr("previous-state", false);
		} else {
			parser.setShowHideAction({
				hide: true,
				show: false
			});
			$(this).attr("previous-state", 'checked');
		}
	});

	$(".message").blur(function() {
		parser.setShowHideActionMessage($(this).val());
	});

	$("#deleteButton").click(function() {
		if ($("#designSurface").find(".panel-body").children().size() > 2) {
			bootbox.confirm("Are you sure you want to clear the entire expression?", function(result) {
				if (result) {
					resetBuildControls($("#designSurface > .panel > .panel-body").filter(":first"));
				}
				$(".modal-backdrop").remove();
			});
		}
	});

	$('.variables a').click(function(e) {
		currentPageIndex = 0;
		e.preventDefault();
		$(this).tab('show');
	});

	$("a[href=#items]").click(function() {
		var selected = $("table").find(".selected").length;
		var data = JSON.parse(sessionStorage.getItem("studies"));
		// Only study
		if (selected === 1) {
			var study = data[$("table").find(".selected").attr("id")]
			var studyEvent = study.events[Object.keys(study.events)[0]]
			if (studyEvent) {
				var crf = studyEvent.crfs[Object.keys(studyEvent.crfs)[0]]
				createBreadCrumb({
					crf: crf.name,
					study: study.name,
					evt: studyEvent.name,
					version: crf.versions[0].name
				});

			} else {
				createBreadCrumb({
					study: study.name
				});
			}
		}
	})

	$("#validate").click(function() {
		parser.validate();
	});
	$('p.ui-draggable').click(function() {
		handleClickDrop(this);
	});
	createPopover($(".dotted-border"));
	$(document).ready(function() {
		parser.fetchStudies();
	});

	$('body').on('click', function(e, data){
		if (!data) {
			$(".tops").remove();
			$(".popover").remove();
		}
	});
});

Array.prototype.chunk = function(arr) {
    if (!this.length) {
        return [];
    }
    return [this.slice(0, arr)].concat(this.slice(arr).chunk(arr));
};

var resizeBody = function() {
    var height = parseInt($(window).height());
    $("body").css("height", height + "px");
    var wrapperHeight = parseInt($(".navbar").offset().top) - parseInt($(".inner-scrollbar-wrapper").offset().top) - 20;
    var percent = parseInt((wrapperHeight / height) * 100);
    $(".inner-scrollbar-wrapper").css("height", percent + "%");
    $(".data-scrollbar").css("height", wrapperHeight + 20 + "px");
}

$( window ).load(function() {
    resizeBody();
});

$( window ).resize(function(){
    resizeBody();
});
