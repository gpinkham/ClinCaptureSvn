/* ============================================
 * Creates an <div> element.
 *
 * Argument Object [params] parameters:
 *  - id - the id of the <div>
 *  - divClass - the css class for the <div>
 *
 * Returns the created div
 *
 * ============================================= */
function createDiv(params) {

	var div = $("<div/>");
	div.attr("id", params.id);
	div.addClass(params.divClass);

	return div;
}

/* =============================================
 * Creates a single <button>.
 *
 * Argument Object [params] parameters:
 *  - id - the id of the <button>
 *  - btnClass - the css class for the <button>
 *  - text - the button display <button>
 *
 * Returns the created button
 * ============================================== */
function createButton(params) {

	var btn = $("<button/>");

	btn.attr("id", params.id);
	btn.addClass(params.btnClass);

	btn.text(params.text);

	return btn;
}

/* =========================================================================
 * Creates an <input> type control that can be bound to the <body> element.
 *
 * Argument Object [params] parameters:
 *  - id - the id of the <input>
 *  - type - the type of the input item
 *
 * Returns the created control
 * ========================================================================= */
function createInput(params) {

	if (params.type === undefined) throw formDesignException();

	// Question type text box
	var input = $("<input/>");

	input.attr("id", params.id);

	input.attr("type", params.type);

	return input;
}

/* =========================================================================
 * Creates a stubbed table without table data - the caller is expected to
 * insert those after this function returns. This function seeks abstractness
 * in how the table headers can be created.
 *
 * Argument Object [params] parameters:
 * - table headers to bind to table
 *
 * Returns the created table
 * ========================================================================= */
function createTable(params) {

	var table = $("<table>");

	table.addClass("table table-condensed table-bordered table-responsive table-hover");

	var thead = $("<thead>");
	
	var thRow = $("<tr>");

	for (var x = 0; x < params.length; x++) {

		var th = $("<th>")
		th.text(params[x])

		thRow.append(th)
	}

	thead.append(thRow)
	table.append(thead)
	table.append($("<tbody>"))

	return table
}

/* =========================================================================
 * Creates a bread crumb for the study/event/crf/item navigation tables
 *
 * Argument Object [params] parameters:
 * - study - this should always be provided as it is the first crumb
 * - event - the selected event
 * - crf - the selected crf in the event
 *
 * Returns the created bread crumb
 * ========================================================================= */
function createBreadCrumb(params) {

	$("#data").find(".panel-body > .breadcrumb").remove();

	var ol = $("<ol>");
	ol.addClass("breadcrumb");

	var studyCrumb = $("<li>");

	var studyLink = $("<a>");
	studyLink.text(params.study);
	studyLink.click(function() {

		$("a[href='#studies']").tab('show');
	})

	studyCrumb.append(studyLink);

	if (params.event) {

		ol.find(".active").removeClass(".active");

		var eventCrumb = $("<li>");

		var eventLink = $("<a>");
		eventLink.text(params.event);
		eventLink.click(function() {

			$("a[href='#events']").tab('show');
		})

		eventCrumb.addClass("active");

		eventCrumb.append(eventLink);
		ol.append(eventCrumb);

	} else {

		studyCrumb.addClass("active");
	}

	if (params.crf) {

		ol.find(".active").removeClass(".active");

		var crfCrumb = $("<li>");

		var crfLink = $("<a>");
		crfLink.text(params.crf);
		crfLink.click(function() {

			$("a[href='#crfs']").tab('show');
		})

		crfCrumb.addClass("active");

		crfCrumb.append(crfLink)
		ol.append(crfCrumb);
	}	

	if (params.version) {

		ol.find(".active").removeClass(".active");

		var versionCrumb = $("<li>");

		var versionLink = $("<a>");
		versionLink.text(params.version);
		versionLink.click(function() {

			$("a[href='#versions']").tab('show');
		})

		versionCrumb.addClass("active");

		versionCrumb.append(versionLink)
		ol.append(versionCrumb);
	}

	ol.prepend(studyCrumb);
	
	$("#data").find(".panel-body").append(ol);

	return ol;
}

/* =========================================================================
 * Resets the expression build surface by deleting all the children
 *
 * Argument Object [parentDiv] parameters:
 * - parentDiv - the expression surface being cleared
 *
 * ========================================================================= */
function resetBuildControls(parentDiv) {

	parentDiv.children("div").not(".pull-right").remove();
	parentDiv.append(createStartExpressionDroppable());
}

/* =========================================================================
 * Creates the pagination element and manages its subsequent interactions for
 * either studies, events, crfs or items table[s]
 *
 * Argument Object [params] parameters:
 * - div - the div to append the pagination to
 * - itemsArr - the array containing the items to page
 *
 * Returns the created pagination
 * ========================================================================= */
function createPagination(params) {

	params.div.find(".pagination").remove();
	if (params.itemsArr.length > 1) {

		var ul = $("<ul>");
		ul.addClass("pagination");

		var el = $("<li>");
		var aEL = $("<a>");

		var tt = unescape(JSON.parse('"\u00AB\u00AB"'));
		aEL.text(tt);
		aEL.click(function() {

			if (currentPageIndex === 0) {

				resetTable({

					arr: params.itemsArr,
					table: params.div.find("table"),
					pagination: params.div.find(".pagination")
					
				})

			} else if (currentPageIndex > 0) {
		
				currentPageIndex = currentPageIndex - 1;

				resetTable({

					arr: params.itemsArr,
					table: params.div.find("table"),
					pagination: params.div.find(".pagination")
					
				})
			}

		})

		el.append(aEL);
		ul.prepend(el);


		var pager = $("<li>");
		var pagerLink = $("<a>");

		pagerLink.text(currentPageIndex + " of " + params.itemsArr.length);

		pager.append(pagerLink);
		ul.append(pager);

		var rEl = $("<li>");
		var rAEL = $("<a>");

		var tt = unescape(JSON.parse('"\u00BB\u00BB"'));
		rAEL.text(tt);
		rAEL.click(function() {

			if (currentPageIndex < params.itemsArr.length - 1) {

				currentPageIndex = currentPageIndex + 1;
				resetTable({

					arr: params.itemsArr,
					table: params.div.find("table"),
					pagination: params.div.find(".pagination")
					
				})
			}

		})

		rEl.append(rAEL);
		ul.append(rEl);

		return ul
	}
}

/* ============================================================
 * Resets a given table by remove all the existing table rows
 *
 * Argument Object [params] parameters:
 * - table - the table to reset
 * - arr - arr containing the updated items to add to the table
 *
 * ============================================================ */
function resetTable(params) {

	params.table.find("tbody > tr").remove();

	var alphaArr = params.arr[currentPageIndex]
	for (var chunk in alphaArr) {

		var tr = alphaArr[chunk]
		params.table.find("tbody").append(tr);
	}

	if (params.pagination) {

		params.pagination.find("a:eq(1)").text(currentPageIndex + 1 + " of " + params.arr.length);
	}	

	$(params.table.parent()).html(params.itemsTable);
}

/* =================================================
 * Creates a drop surface with the dotted borders
 *
 * Argument Object [params] parameters:
 * - id - the id of the drop surface
 * - class - the class[es] for the drop surface
 *
 * Returns the created drop surface
 * ================================================= */
function createDropSurface(params) {

	var div = createDiv({

		id: params.id,
		divClass: "dotted-border init " + params.class

	}).text(params.text)

	createPopover(div);

	return div;	
}

/* ====================================================================================
 * Creates the start expression drop surface with the dotted borders. This
 * drop surface is a group capable of accepting left parenthesis and right parenthesis.
 *
 * Returns the created drop surface
 * =================================================================================== */
function createStartExpressionDroppable() {

	var div = createDropSurface({

		class: "group",
		text: "Group or Data"
	})

	createDroppable({

		element: div,
		accept: "#leftParentheses, #rightParentheses, div[id='items'] td, div[id='data'] p"
	})

	return div;
}

/* =========================================================================
 * Creates the symbol expression drop surface with the dotted borders. This
 * drop surface is a group capable of accepting math and comparison symbols.
 *
 * Returns the created drop surface
 * ========================================================================= */
function createSymbolDroppable() {

	var div = createDropSurface({

		class: "comp",
		text: "Compare or Calculate"
	})

	createDroppable({
		element: div,
		accept: "div[id='compare'] p, div[id='calculate'] p"
	})

	return div;
}

/* =============================================================================
 * Creates the right parenthesis drop surface with the dotted borders. The drop
 * surface already has a right parenthesis added and does not accept drops.
 *
 * Returns the created drop surface
 * =========================================================================== */
function createRPARENDiv() {

	return createDropSurface({

		text: ")",
		class: "group bordered"
	})
}

/* =============================================================================
 * Creates the condition expression drop surface with the dotted borders. This
 * drop surface is a group capable of accepting math and comparison symbols.
 *
 * Returns the created drop surface
 * =========================================================================== */
function createConditionDroppable() {

	var div = createDropSurface({

		class: "eval",
		text: "Condition"
	})

	createDroppable({
		element: div,
		accept: "div[id='evaluate'] p"
	})

	return div;
}

/* =============================================================================
 * Creates the drop surface popover to allow editing and deleting the surface
 *
 * Argument Object [droppable] parameters:
 * - droppable - the droppable on which the popover will be bound
 *
 * =========================================================================== */
function createPopover(droppable) {
	

	var last = droppable.last().attr("class").split(" ")[2]	
	var btn = '<div type="button" class="pull-left space-right-m" onclick="addDroppable(this)" last="' + last + '"><span class="glyphicon glyphicon-pencil"></span></div><div class="pull-left space-right-m" type="button" onclick="x(this)"><span class="glyphicon glyphicon-trash"></span></div>'

	droppable.popover({

		html: true,
		content: btn,
		placement: "top",
		trigger: "manual",
		container: droppable

	}).click(function(evt) {

		// existing
		$(".popover").remove();

		evt.stopPropagation();
		$(this).popover('show');
	});
}

/* =============================================================================
 * Invoked from the drop surface popover, this function creates a valid droppable
 * depending on the current droppable type, the past droppable type and the 
 * preceding droppable type (if any) -
 *
 * Argument Object [popov] parameters:
 * - popov - the pop over which invoked this function
 * =========================================================================== */
function addDroppable(popov) {

	var drop = $(popov).parents(".dotted-border");

	drop.attr("last", $(popov).attr("last"))

	var modalOuterDiv = createDiv({
		divClass: "modal fade tops"
	})

	var modalDialog = createDiv({
		divClass: "modal-dialog"
	});

	var modalContent = createDiv({
		divClass: "modal-content"
	});

	var div = createDiv(Object.create({
		divClass: "modal-body"
	}))

	var groupBtn = createButton({

		text: "Group / Data",
		btnClass: "btn btn-success space-right-m"

	}).click(function() {

		modalOuterDiv.remove();

		var d = createStartExpressionDroppable();
		if (drop.attr("last") === "group" && !drop.next().is(".dotted-border")) {

			drop.after(d);
			createPopover(d);

		} else  {

			drop.before(d);
			createPopover(d);
		}
	})

	var compBtn = createButton({

		text: "Compare / Calculate",
		btnClass: "btn btn-primary space-right-m"

	}).click(function() {	

		modalOuterDiv.remove();
		var c = createSymbolDroppable();
		if (drop.next().size() > 0) {

			if (!drop.is(".eval") && !drop.is(".comp") && !drop.next().is(".comp") && !drop.next().is(".eval")) {
				drop.after(c);
				createPopover(c);
			}
		} else {

			if (!drop.is(".eval") && !drop.is(".comp")) {
				drop.after(c);
				createPopover(c);
			}
		}
	})

	var evalBtn = createButton({

		text: "Condition",
		btnClass: "btn btn-warning"

	}).click(function() {

		modalOuterDiv.remove();
		var eval = createConditionDroppable();
		if (drop.next().size() > 0) {

			if (!drop.is(".eval") && !drop.is(".comp") && !drop.next().is(".comp") && !drop.next().is(".eval")) {
				drop.after(eval);
				createPopover(eval);
			}
		} else {

			if (!drop.is(".eval") && !drop.is(".comp")) {
				drop.after(eval);
				createPopover(eval);
			}
		}
	})

	div.append(groupBtn);
	div.append(compBtn);
	div.append(evalBtn);

	div.css("text-align", "center");

	modalContent.append(div);

	modalDialog.append(modalContent)
	modalOuterDiv.append(modalDialog)

	modalOuterDiv.modal({
		backdrop: false
	})
}

/* =============================================================================
 * Invoked from the drop surface popover, deletes drop surface from which it was
 * invoked
 *
 * Argument Object [popov] parameters:
 * - popov - the pop over which invoked this function
 * =========================================================================== */
function x(popov) {

	if ($("#designSurface").find(".dotted-border").size() > 1) {

		$(popov).parents(".dotted-border").remove();
	}
}

/* ===============================================================
 * Convienience method that creates a tooltip for a given element
 *
 * Argument Object [params] parameters:
 * - element - the element to create the tooltip for
 * - title - the tool tip title
 * =============================================================== */
function createToolTip(params) {

	params.element.tooltip({
		
		container: "body",
		title: params.title,
		placement: "bottom",
		trigger: "hover focus"

	}).on('shown.bs.tooltip', function() {

		setTimeout(function() {
			$(".tooltip").remove();
		}, 5000);
	});
}

/* ================================================================
 * Convienience method that creates an alert. Note that by default,
 * times out after 4 second

 * Argument Object [text] parameters:
 * - text - text alert text
 *
 * * Returns the created alert
 * =============================================================== */
function createAlert(text) {

	var div = createDiv({
		divClass: "alert alert-danger"
	}).text(text);

	var a = $("<a>");
	a.addClass("close");
	a.attr("data-dismiss", "alert");
	a.text("x");

	div.prepend(a);

	setTimeout(function() {
		$(".alert").alert('close');
	}, 4000)

	return div;
}

/* ===========================================================================
 * Handles erratic responses from a server.
 *
 * Arguments [params]:
 * => response - The response from the server (must be a valid http response)
 * ========================================================================== */
function handleErrorResponse(params) {

	if (params.response.status === 404) {

		bootbox.alert({
			backdrop: false,
			message: "The server you are attempting to connect to appears to be unavailable at the moment. Please try again later!"
		});

	} else {

		bootbox.alert({

			backdrop: false,
			message: params.response.responseText
		});

	}
}

/* =========================================================================
 * Creates an element that can be dragged on a specified droppable
 *
 * Argument Object [params] parameters:
 *  - element - the element to convert to a draggable
 *  - target - the intend target[s] for this element
 * ========================================================================= */
function createDraggable(params) {

	$(params.element).draggable({

		scroll: true,
		cursor: "move",
		helper: "clone",
		revert: "invalid",
		snapMode: "outer",
		snap: params.target,
		scrollSensitivity: 100
	})
}

/* ====================================================================
 * Creates an element that can be dropped on with specified draggables
 *
 * Argument Object [params] parameters:
 *  - element - the element to convert to a droppable
 *  - accept - the intend target[s] for this element
 * ==================================================================== */
function createDroppable(params) {

	params.element.droppable({

		accept: params.accept,
		hoverClass: "ui-state-active",

		drop: function(event, ui) {			

 			params.element.text("");
 			params.element.removeClass("init");
 			params.element.addClass("bordered");
			
			if (parser.isText(ui.draggable)) {

				handleTextDrop(params.element);
				
			} else if (parser.isDate(ui.draggable)) {

				handleDateDrop(params.element);

			} else if (parser.isNumber(ui.draggable)) {

				handleNumberDrop(params.element);

			} else {

				params.element.append(ui.draggable.text());
			}

			params.element.tooltip("hide");

			// Create the next droppable
			parser.createNextDroppable({

				ui: ui,
				element: params.element,
			});

			params.element.css('font-weight', 'bold');
		}
	})

	params.element.dblclick(function() {

		params.element.tooltip("hide");
		params.element.removeClass("init");
 		params.element.addClass("bordered");

		var currentValue = $(this).text().slice(0, -5);

		var input = $("<input>");
		input.val(currentValue);

		input.blur(function() {

			if ($(this).val()) {

				params.element.text($(this).val());

			} else {

				params.element.text("Data");
			}

			$(this).remove();
		})

		input.css({

			"text-align": "center",
			"display": "table-cell"
		})

		$(this).text("");
		$(this).append(input);

		input.focus(); 
	})

	return params.element;
}

/* ======================================================================
 * Handles the dropping of text based UI items. Note that this function
 * enables the dropped item to switch between input and surface on
 * double click
 *
 * Note that this function should only be called on drop event.
 *
 * Argument Object [element] parameters:
 *  - element - the element that has been dropped
 * ==================================================================== */
function handleTextDrop(element) {

	var newInput = $("<input>");
	newInput.attr("type", "text");
	newInput.addClass("input-sm");

	newInput.blur(function() {

		if ($(this).val()) {

			element.text('"' + $(this).val() + '"');

		} else {

			element.text('"Data"');
		}
	})

	element.append(newInput);
	newInput.focus();
}

/* =============================================================================
 * Handles the dropping of number based UI items. Note that this function
 * enables the dropped item to switch between input (that accepts numbers only) 
 * and surface on double click
 *
 * Note that this function should only be called on drop event.
 *
 * Argument Object [element] parameters:
 *  - element - the element that has been dropped
 * ============================================================================ */
function handleNumberDrop(element) {

	var newInput = $("<input>");

	newInput.attr("type", "number");

	newInput.addClass("input-sm");

	newInput.blur(function() {

		if ($(this).val() && /[0-9]|\./.test($(this).val())) {

			element.text($(this).val());

		} else {

			element.text("Number");
			$("#designSurface").find(".panel-body").prepend(createAlert("Please enter a number"));
		}
	})

	element.append(newInput);
	newInput.focus();
}

/* =============================================================================
 * Handles the dropping of date based UI items. Note that this function
 * enables the dropped item to switch between pop a date picker and surface to 
 * hold the selected date.
 *
 * Note that this function should only be called on drop event.
 *
 * Argument Object [element] parameters:
 *  - element - the element that has been dropped
 * ============================================================================ */
function handleDateDrop(element) {

	var newInput = $("<input>");
	newInput.attr("type", "date");
	newInput.addClass("input-sm");

	// FF
	if (typeof InstallTrigger !== 'undefined') {

		newInput.datepicker().on("hide", function() {

			if ($(this).val()) {

				element.text($(this).val());

			} else {

				element.text("Data");
			}
		})
	} else {

		// Webkit browsers
		newInput.blur(function() {

			if ($(this).val()) {

				element.text($(this).val());

			} else {

				element.text("Data");
			}
		})
	}

	element.append(newInput);
	newInput.focus();
}
