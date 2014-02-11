var selectedStudy = 0;
var parser = new Parser();

$(function() {

	// Get url component
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment?read=true");

	// ======================= Tool tip creation =======================

	$("#deleteButton").tooltip({

		container: "body",
		placement: "top",
		trigger: "hover focus",
		title: "Press to clear the current rule expression"
	})

	$("input.ui-droppable").tooltip({
		
		container: "body",
		placement: "bottom",
		trigger: "hover focus",
		title: "Apply the rule to the selected CRF item(s). Drag and drop CRF item(s) to target the rule.  Multiple targets can be selected"
	})

	$("#ruleName").tooltip({
		
		container: "body",
		placement: "bottom",
		trigger: "hover focus",
		title: "Enter a name for the new rule"
	})

	createToolTip({
		
		title: "Start grouping",
		element: $("#leftParentheses")
	})

	createToolTip({
		
		title: "End grouping",
		element: $("#rightParentheses")
	})

	createToolTip({
		
		title: "And...",
		element: $("#and")
	})	

	createToolTip({
		
		title: "Or...",
		element: $("#or")
	})

	createToolTip({
		
		title: "Not",
		element: $("#not")
	})

	createToolTip({
		
		title: "Equal",
		element: $("#equal")
	})

	createToolTip({
		
		title: "Not equal",
		element: $("#notEqual")
	})

	createToolTip({
		
		title: "Less than",
		element: $("#lessThan")
	})

	createToolTip({
		
		title: "Greater than",
		element: $("#greaterThan")
	})

	createToolTip({
		
		title: "Less than or equal to",
		element: $("#lessThanOrEqual")
	})

	createToolTip({
		
		title: "Greater than or equal to",
		element: $("#greaterThanOrEqual")
	})

	createToolTip({
		
		title: "Contain",
		element: $("#contain")
	})

	createToolTip({
		
		title: "Does not contain",
		element: $("#notContain")
	})

	createToolTip({
		
		title: "Plus",
		element: $("#plus")
	})

	createToolTip({
		
		title: "Minus",
		element: $("#minus")
	})

	createToolTip({
		
		title: "Divide by",
		element: $("#divide")
	})

	createToolTip({
		
		title: "Multiplied by",
		element: $("#multiply")
	})

	createToolTip({
		
		element: $("#number"),
		title: "Integer and float number field"
	})

	createToolTip({
		
		title: "Null field",
		element: $("#empty")
	})

	createToolTip({
		
		title: "Date field",
		element: $("#date")
	})

	createToolTip({
		
		title: "Text field",
		element: $("#text")
	})

	createToolTip({
		
		title: "Select a study",
		element: $("#studiesLink")
	})

	createToolTip({
		
		element: $("#eventsLink"),
		title: "Select a study event"
	})

	createToolTip({
		
		element: $("#crfsLink"),
		title: "Select an event CRF"
	})

	createToolTip({
		
		element: $("#versionsLink"),
		title: "Select a CRF Version"
	})

	createToolTip({
		
		element: $("#itemsLink"),
		title: "Select a CRF item"
	})

	createToolTip({
		
		element: $("#groupSurface"),
		title: "Drag and drop an item from the Group tool box, the data toolbox or a CRF item."
	})	

	// ======================= End of tool tip creation =======================

	// Hide action messages parameter divs
	$("div[id='actionMessages']").hide();
	$("div[id='actionMessages'] > *").hide();

	// ======================= Draggables =======================

	createDraggable({

		target: $("#groupSurface"),
		element: $("#leftParentheses")
	});

	createDraggable({

		target: $("#secondGroupSurface"),
		element: $("#rightParentheses")
	});

	$("div[id='evaluate']").find("p").each(function() {

		createDraggable({

			element: $(this),
			target: $(".eval"),
		});
	})

	$("div[id='compare']").find("p").each(function() {

		createDraggable({

			element: $(this),
			target: $(".comp"),
		});
	})

	$("div[id='calculate']").find("p").each(function() {

		createDraggable({

			element: $(this),
			target: $(".comp"),
		});
	})

	$("div[id='data']").find("p").each(function() {

		createDraggable({

			element: $(this),
			target: $(".group"),
		});
	})

	// ======================= End of creating draggables =======================

	// ======================= Droppables =======================
	createDroppable({
		element: $("#groupSurface"),
		accept: "#leftParentheses, div[id='data'] p, div[id='items'] td"
	})

	createDroppable({
		element: $(".target"),
		accept: "div[id='items'] td"
	})

	// ======================= End of creating droppables =======================

	// ======================= Event handlers =======================

	$("#ruleName").blur(function() {

		parser.setName($(this).val());
	})

	$("input[name='ruleInvoke']").change(function() {

		if ($("#evaluateTrue").is(":checked")) {
			parser.setEvaluatesTo(true);
		} else {
			parser.setEvaluatesTo(false);
		}
	})

	$("#ide").change(function() {

		parser.setInitialDataEntryExecute($(this).is(":checked"));
	})

	$("#ae").change(function() {
		parser.setAdministrativeEditingExecute($(this).is(":checked"));	
	})

	$("#dde").change(function() {
		parser.setDoubleDataEntryExecute($(this).is(":checked"));
	})

	$("#dataImport").change(function() {
		parser.setDataImportExecute($(this).is(":checked"));
	})

	$("#chkDiscrepancyText").change(function() {

		parser.setDiscrepancyAction({

			selected: $(this).is(":checked"),
			message: $("#discrepancyText").val()
		})

		$("#discrepancyText").find("textarea").focus();
	})

	$("#discrepancyText").find("textarea").blur(function() {

		parser.setDiscrepancyAction({

			message: $(this).val(),
			selected: $("#chkDiscrepancyText").is(":checked")
		})	
	})

	$("#chkEmail").change(function() {

		parser.setEmailAction({

			to: $("#toField").val(),
			message: $("#body").val(),
			selected: $(this).is(":checked")
		})

		$("#toField").focus();
	})

	$("#toField").blur(function() {

		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

		if (!re.test($(this).val().trim())) {

			$(this).parent().addClass("has-error");

		} else {

			parser.setEmailAction({

				to: $("#toField").val(),
				message: $("#body").val(),
				selected: $("#chkEmail").is(":checked")
			})

			$(this).parent().removeClass("has-error");
		}
	})

	$("#body").blur(function() {

		parser.setEmailAction({

			to: $("#toField").val(),
			message: $("#body").val(),
			selected: $("#chkEmail").is(":checked")
		})
	})

	$("#deleteButton").click(function() {

		if ($("#designSurface").find(".panel-body").children().size() > 2) {

			bootbox.confirm("Are you sure you want to clear the entire expression?", function(result) {

				if (result) {

					resetBuildControls($("#designSurface > .panel > .panel-body").filter(":first"));

					$(".modal-backdrop").remove();
				}
			});
		}
	})

	$('div[id="variables"] a').click(function(e) {

		currentPageIndex = 0;
		
		e.preventDefault();
		$(this).tab('show');
	})

	$("a[href=#items]").click(function() {

		var selected = $("table").find(".selected").length

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
					event: studyEvent.name,
					version: crf.versions[0].name

				})

			} else {

				createBreadCrumb({

					study: study.name

				})
			}
		}
	})

	$("#validate").click(function() {

		var rule = parser.getRule();

		if (rule) {

			validate(rule);
		}
	})

	createPopover($("#groupSurface"));
	fetchStudies();

	// If editing a rule
	if (getURLParameter("action") === "edit") {

		fetchRuleForEditing();
	}

	// If a rule already exists in a session, display it
	$(document).ready(function() {

		if (sessionStorage.getItem("status") && sessionStorage.getItem("status") === "load") {

			var rule = JSON.parse(sessionStorage.getItem("rule"));

			parser.render(rule);
		}

		sessionStorage.removeItem("status");

	})

	$('body').click(function(e) {

		$(".modal").remove();
		$(".popover").remove();
	});
})

Array.prototype.chunk = function(arr) {

    if (!this.length) {
        return [];
    }

    return [this.slice(0, arr)].concat(this.slice(arr).chunk(arr));
};
