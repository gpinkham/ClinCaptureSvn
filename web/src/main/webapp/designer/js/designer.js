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
		var th = $("<th>");
		th.text(params[x]);
		thRow.append(th);
	}
	thead.append(thRow);
	table.append(thead);
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
	});

	studyCrumb.append(studyLink);
	if (params.evt) {

		ol.find(".active").removeClass(".active");
		var eventCrumb = $("<li>");
		var eventLink = $("<a>");
		eventLink.text(params.evt);
		eventLink.click(function() {
			$("a[href='#events']").tab('show');
		});

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
		});
		crfCrumb.addClass("active");
		crfCrumb.append(crfLink);
		ol.append(crfCrumb);
	}	

	if (params.version) {

		ol.find(".active").removeClass(".active");

		var versionCrumb = $("<li>");
		var versionLink = $("<a>");
		versionLink.text(params.version);
		versionLink.click(function() {
			$("a[href='#versions']").tab('show');
		});
		versionCrumb.addClass("active");
		versionCrumb.append(versionLink);
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
	$("#deleteButton").hide();
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
				});

			} else if (currentPageIndex > 0) {
				currentPageIndex = currentPageIndex - 1;
				resetTable({
					arr: params.itemsArr,
					table: params.div.find("table"),
					pagination: params.div.find(".pagination")
				});
			}
		});

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
				});
			}
		});

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
		var tr = alphaArr[chunk];
		$(tr).find("td").toArray().map(function(x) {
			if (!$(x).is(".ui-draggable")) {
				createDraggable({
					element: $(x)
				});
			}
		});
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

	}).text(params.text);
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
	});
	createDroppable({
		element: div,
		accept: "#leftParentheses, #rightParentheses, div[id='items'] td, div[id='data'] p"
	});
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
	});
	createDroppable({
		element: div,
		accept: "div[id='compare'] p, div[id='calculate'] p"
	});
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
	});
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
	});
	createDroppable({
		element: div,
		accept: "div[id='evaluate'] p"
	});
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
	var btn = '<div id="edit-pop" type="button" class="pull-left space-right-m" onclick="addDroppable(this)"><span class="glyphicon glyphicon-pencil"></span></div><div id="del-pop" class="pull-left space-right-m" type="button" onclick="x(this)"><span class="glyphicon glyphicon-trash"></span></div>';
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
	}).on('shown.bs.popover', function (x) {
  		$(".tooltip").each(function() {
  			$(this).remove();
  		});
  		// Edit tool-tip
  		createToolTip({
  			element: $(this).find("#edit-pop"),
  			title: "Click to add a new drop surface"
  		});
		$(this).find("#edit-pop").on('show.bs.tooltip', function(x) {
			$(".tooltip").each(function() {
				$(this).remove();
			});
		});
  		// Delete tool-tip
  		createToolTip({
  			element: $(this).find("#del-pop"),
  			title: "Click to remove the current element from the expression"
  		});
  		$(this).find("#del-pop").on('show.bs.tooltip', function(x) {
			$(".tooltip").each(function() {
				$(this).remove();
			});
		});
  		// kill event
  		x.stopPropagation();
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

	var drop = $(popov).parents(".dotted-border")[0];
	var modalOuterDiv = createDiv({
		divClass: "modal fade tops"
	});

	var modalDialog = createDiv({
		divClass: "modal-dialog"
	});

	var modalContent = createDiv({
		divClass: "modal-content"
	});

	var div = createDiv(Object.create({
		divClass: "modal-body"
	}));

	var groupBtn = createButton({
		text: "Group / Data",
		btnClass: "btn btn-success space-right-m"
	}).click(function() {
		modalOuterDiv.remove();
		var d = createStartExpressionDroppable();
		if ($(drop.previousSibling).is(".eval") || !$(drop.previousSibling).is(".dotted-border")) {
			$(drop).before(d);
			createPopover(d);
		} else {
			$(drop).after(d);
			createPopover(d);
		} 
	});

	var compBtn = createButton({
		text: "Compare / Calculate",
		btnClass: "btn btn-primary space-right-m"
	}).click(function() {	
		modalOuterDiv.remove();
		var c = createSymbolDroppable();
		if ($(drop).next().size() > 0) {
			if (!$(drop).is(".eval") && !$(drop).is(".comp") && !$(drop).next().is(".comp") && !$(drop).next().is(".eval")) {
				$(drop).after(c);
				createPopover(c);
			}
		} else {
			if (!$(drop).is(".eval") && !$(drop).is(".comp")) {
				$(drop).after(c);
				createPopover(c);
			} else {
				removeInsert(drop, c);
			}
		}
	});

	var evalBtn = createButton({
		text: "Condition",
		btnClass: "btn btn-warning"
	}).click(function() {
		modalOuterDiv.remove();
		var eval = createConditionDroppable();
		if ($(drop).next().size() > 0) {
			if (!$(drop).is(".eval") && !$(drop).is(".comp") && !$(drop).next().is(".comp") && !$(drop).next().is(".eval")) {
				$(drop).after(eval);
				createPopover(eval);
			}
		} else {
			if (!$(drop).is(".eval") && !$(drop).is(".comp")) {
				$(drop).after(eval);
				createPopover(eval);
			} else {
				removeInsert(drop, eval);
			}
		}
	});

	div.append(groupBtn);
	div.append(compBtn);
	div.append(evalBtn);
	div.css("text-align", "center");
	modalContent.append(div);
	modalDialog.append(modalContent)
	modalOuterDiv.append(modalDialog)
	modalOuterDiv.modal({
		backdrop: false
	});
}

function removeInsert(drop, predicate) {
	$(drop).after(predicate);
	$(drop).remove();
}

/* =============================================================================
 * Invoked from the drop surface popover, deletes drop surface from which it was
 * invoked
 *
 * Argument Object [params] parameters:
 * - popov - the pop over which invoked this function
 * =========================================================================== */
function x(popov) {
	$(".tooltip").each(function() {
		$(this).remove();
	});
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
	}, 5000);
	return div;
}

/* ===========================================================================
 * Handles erratic responses from a server.
 *
 * Arguments [params]:
 * => response - The response from the server (must be a valid http response)
 * ========================================================================== */
function handleErrorResponse(params) {
	$(".spinner").remove();
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
	});
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

			var existingValue = params.element.val();
 			params.element.text("");
 			params.element.removeClass("init");
 			params.element.addClass("bordered");
			if (parser.isText(ui.draggable)) {
				handleTextDrop(params.element);
			} else if (parser.isDate(ui.draggable)) {
				handleDateDrop(params.element);
			} else if (parser.isNumber(ui.draggable)) {
				handleNumberDrop(params.element);
			} else if (parser.isEmpty(ui.draggable)) {
				params.element.append('""');
			} else if (parser.isCurrentDate(ui.draggable)) {
				params.element.append(" _CURRENT_DATE");
			} else {
				if (ui.draggable.text() == "<") {
					params.element.append("&lt;");
				} else if (ui.draggable.text() == ">") {
					params.element.append("&gt;");
				} else if (ui.draggable.is("td.group")) {
					params.element.append(ui.draggable.attr("itemName"));
					// Persist attrinutes
					params.element.attr("item-oid", ui.draggable.attr("oid"));
					params.element.attr("crf-oid", ui.draggable.attr("crf-oid"));
					params.element.attr("event-oid", ui.draggable.attr("event-oid"));
					params.element.attr("group-oid", ui.draggable.attr("group-oid"));
					params.element.attr("version-oid", ui.draggable.attr("version-oid"));
				} else {
					params.element.append(ui.draggable.text());
				}
			}

			params.element.tooltip("hide");
			// Create the next droppable
			parser.createNextDroppable({
				ui: ui,
				element: params.element,
				existingValue: existingValue
			});
			params.element.css('font-weight', 'bold');
			$("#deleteButton").removeClass("hidden");
		}
	})

	function isDate(val) {
		var d = new Date(val);
		return !isNaN(d.valueOf()) && d.__proto__ != "Invalid Date";
	}

	params.element.dblclick(function() {

		params.element.tooltip("hide");
 		params.element.addClass("bordered");
		var input = $("<input>");
		if (isDate($(this).text())) {

			input.attr("type", "date");
			input.val($(this).text());
			var msie = window.navigator.userAgent.indexOf('MSIE ');
			var trident = window.navigator.userAgent.indexOf('Trident/');
			if (typeof InstallTrigger !== 'undefined' || msie > 0 || trident > 0) {
				input.data({date: new Date(input.val())}).datepicker('update').children("input").val(new Date(input.val()));
				input.datepicker().on("hide", function() {
					if ($(this).val()) {
						params.element.text($(this).val());
					} 
				});
			} else {
				input.blur(function() {
					if ($(this).val()) {
						params.element.text($(this).val());
					} else {
						params.element.text("Data");
					}
					$(this).remove();
				});
			}
		} else {
			input.val($(this).text());
			input.blur(function() {
				if ($(this).val()) {
					params.element.text($(this).val());
				} else {
					params.element.text("Data");
				}
				$(this).remove();
			});
		}

		input.css({
			"display": "inline",
			"text-align": "center"
		});

		$(this).text("");
		$(this).append(input);

		// focus/select
		input.focus(); 
		input.select();

		params.element.css('font-weight', 'bold');

	});
	
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
	});

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
	});

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

    var msie = window.navigator.userAgent.indexOf('MSIE ');
    var trident = window.navigator.userAgent.indexOf('Trident/');
	if (typeof InstallTrigger !== 'undefined' || msie > 0 || trident > 0) {
		newInput.datepicker().on("hide", function() {
			if ($(this).val()) {
				element.text($(this).val());
			} else {
				element.text("Select Date");
			}
		});
	} else {
		newInput.blur(function() {
			if ($(this).val()) {
				element.text($(this).val());
			} else {
				element.text("Data");
			}
		});
	}
	element.append(newInput);
	newInput.focus();
}

/* ===================================================
 * Adds the studies to the studies table for display.
 *
 *
 * Argument Object [studies] parameters:
 *  - studies - the return studies from CC
 * =================================================== */
function loadStudies(studies) {

	var itemArr = [];
	$("div[id='studies']").find("table").remove();
	if (studies) {
		// Table headers
		var table = createTable(['Name', 'Identifier']);
		for (var x = 0; x < studies.length; x++) {
			var study = studies[x];
			var tr = $("<tr>");
			tr.attr("id", x);
			tr.click(function() {
				var row = this;
				var data = JSON.parse(sessionStorage.getItem("studies"));
				// Extract selected study
				var currentStudy = data[$(row).attr("id")];
				if (parser.getStudy() !== data[$(row).attr("id")].id && 
					($("div[id='studies']").find("table > tbody > tr").size() > 1 && $(".dotted-border").size() > 2) && !editing) {
					createPrompt({
						row: row,
						study: currentStudy
					});

				} else {
					resetStudy({
						row: row,
						study: currentStudy
					});
				}
				editing = false;
			});

			var tdName = $("<td>");
			tdName.text(study.name);
			tdName.attr("oid", study.oid);

			var tdIdentifier = $("<td>");
			tdIdentifier.text(study.identifier);

			tr.append(tdName);
			tr.append(tdIdentifier);
			itemArr.push(tr);
		}

		$("div[id='studies']").append(table);
		currentPageIndex = 0;

		// Global
		var chunkedItemsArr = itemArr.chunk(10);
		var pagination = createPagination({
			itemsArr: chunkedItemsArr,
			div: $("div[id='studies']")
		});

		table.after(pagination);
		resetTable({
			table: table,
			arr: chunkedItemsArr,
			pagination: pagination
		});

		// probably editing
		if (parser.getStudy()) {
			var st = studies.filter(function(x) {
				return x.id === parser.getStudy();
			});
			$('tr[id="'+ studies.indexOf(st[0]) +'"]').click();
		} else {
			$(".table-hover").find("tbody > tr").filter(":first").click();
		}
		// Initial load should show studies
		$("a[href='#studies']").tab('show');
	}
}

/* =================================================================
 * Adds the a given study's events to the events table for display.
 *
 * Argument Object [study] parameters:
 *  - study - the study for whom events should be loaded
 * ============================================================== */
function loadStudyEvents(study) {
	var itemArr = [];
	$("div[id='events']").find("table").remove();
	if (study.events) {
		var eventTable = createTable(['Name', 'Description']);
		for (var x = 0; x < study.events.length; x++) {

			var studyEvent = study.events[x];
			var tr = $("<tr>");
			tr.attr("id", x);
			tr.click(function() {
				$("a[href='#crfs']").tab('show');
				// Make bold
				$(this).siblings(".selected").removeClass("selected");
				$(this).addClass("selected");

				var currentEvent = study.events[$(this).attr("id")]
				loadEventCRFs({
					study: study,
					studyEvent: currentEvent
				});

				createBreadCrumb({
					study: study.name,
					evt: currentEvent.name
				});
			});

			var tdName = $("<td>");
			tdName.text(studyEvent.name);
			tdName.attr("oid", studyEvent.oid);

			var tdDescription = $("<td>");
			if (studyEvent.description) {
				if (studyEvent.description.length > 25) {
					tdDescription.text(studyEvent.description.slice(0, 20) + "...");
					tdDescription.tooltip({
						placement: "top",
						container: "body",
						title: studyEvent.description
					});

				} else {
					tdDescription.text(studyEvent.description);
				}
			}
			tr.append(tdName);
			tr.append(tdDescription);
			itemArr.push(tr);
		}

		$("div[id='events']").append(eventTable);
		currentPageIndex = 0;
		// Global
		var chunkedItemsArr = itemArr.chunk(10);
		var pagination = createPagination({
			div: $("div[id='events']"),
			itemsArr: chunkedItemsArr
		});

		eventTable.after(pagination);
		resetTable({
			table: eventTable,
			arr: chunkedItemsArr,
			pagination: pagination
		});		
	}
}

/* =================================================================
 * Adds the a given event's crfs to the crf table for display.
 *
 * Argument Object [params] parameters:
 * - studyEvent - the event for whom crfs should be loaded
 * - study - the study to which the event belongs to
 * ============================================================== */
function loadEventCRFs(params) {

	var itemArr = [];
	$("div[id='crfs']").find("table").remove();
	if (params.studyEvent && params.studyEvent.crfs) {
		var crfTable = createTable(['Name', 'Description']);
		for (var cf = 0; cf < params.studyEvent.crfs.length; cf++) {
			var crf = params.studyEvent.crfs[cf];
			var tr = $("<tr>");
			tr.attr("id", cf);
			tr.click(function() {
				$("a[href='#versions']").tab('show');
				// Make bold
				$(this).siblings(".selected").removeClass("selected");
				$(this).addClass("selected");
				var currentCRF = params.studyEvent.crfs[$(this).attr("id")];
				loadCRFVersions({
					crf: currentCRF,
					study: params.study,
					evt: params.studyEvent
				});
				createBreadCrumb({
					crf: currentCRF.name,
					study: params.study.name,
					evt: params.studyEvent.name
				});
			});

			var tdName = $("<td>");
			tdName.text(crf.name);
			tdName.attr("oid", crf.oid);
			var tdDescription = $("<td>");
			if (crf.description) {
				if (crf.description.length > 25) {
					tdDescription.text(crf.description.slice(0, 20) + "...");
					tdDescription.tooltip({
						placement: "top",
						container: "body",
						title: crf.description
					});
				} else {
					tdDescription.text(crf.description);
				}
			}
			tr.append(tdName); 
			tr.append(tdDescription);
			itemArr.push(tr);
		}

		$("div[id='crfs']").append(crfTable)
		currentPageIndex = 0;

		// Global
		var chunkedItemsArr = itemArr.chunk(10);
		var pagination = createPagination({
			div: $("div[id='crfs']"),
			itemsArr: chunkedItemsArr
		});

		crfTable.after(pagination);
		resetTable({
			table: crfTable,
			arr: chunkedItemsArr,
			pagination: pagination
		});
	}
}

function loadCRFVersions(params) {
	var itemArr = [];
	$("div[id='versions']").find("table").remove();
	if (params.crf.versions) {
		var versionTable = createTable(['Name']);
		for (var ver = 0; ver < params.crf.versions.length; ver++) {
			var version = params.crf.versions[ver];
			var tr = $("<tr>");
			tr.attr("id", ver);
			tr.click(function() {
				$("a[href='#items']").tab('show');
				// Make bold
				$(this).siblings(".selected").removeClass("selected");
				$(this).addClass("selected");
				var currentVersion = params.crf.versions[$(this).attr("id")];
				loadCRFVersionItems({
					crf: params.crf.oid,
					evt: params.evt.oid,
					version: currentVersion
				});
				createBreadCrumb({
					crf: params.crf.name,
					evt: params.evt.name,
					study: params.study.name,
					version: currentVersion.name
				});
			});

			var tdName = $("<td>");
			tdName.text(version.name);
			tdName.attr("oid", version.oid);

			tr.append(tdName);
			itemArr.push(tr);
		}

		$("div[id='versions']").append(versionTable);
		currentPageIndex = 0;
		var chunkedItemsArr = itemArr.chunk(10);
		var pagination = createPagination({
			div: $("div[id='versions']"),
			itemsArr: chunkedItemsArr
		});

		versionTable.after(pagination);
		resetTable({
			table: versionTable,
			arr: chunkedItemsArr,
			pagination: pagination
		});
	}
}

/* =================================================================
 * Adds the a given crf's items to the items table for display.
 *
 *
 * Argument Object [params] parameters:
 * - crf - the crf for whom items should be loaded
 * ============================================================== */
function loadCRFVersionItems(params) {
	var itemArr = [];
	$("div[id='items']").find("table").remove();
	if (params.version && params.version.items) {
		var itemsTable = createTable(['Name', 'Description', 'Data Type']);
		for (var it = 0; it < params.version.items.length; it++) {
			var item = params.version.items[it];
			var tr = $("<tr>");
			var tdName = $("<td>");
			if (item.name.length > 25) {
 				tdName.text(item.description.slice(0, 20) + "...");
 				tdName.tooltip({
 					placement: "top",
 					container: "body",
 					title: item.name
 				});
 			} else {
 				tdName.text(item.name);
 			}
			tdName.text(item.name);
			tdName.addClass("group");
			tdName.attr("itemName", item.name);
			// Attributes
			tdName.attr("oid", item.oid);
			tdName.attr("crf-oid", params.crf);
			tdName.attr("event-oid", params.evt);
			tdName.attr("group-oid", item.group);
			tdName.attr("version-oid", params.version.oid);
			createDraggable({
				element: tdName,
				target: ($("#dataSurface"), $("#secondDataSurface"))
			});

			var tdDescription = $("<td>");
			if (item.description) {
				if (item.description.length > 25) {
					tdDescription.text(item.description.slice(0, 20) + "...");
					tdDescription.tooltip({
						placement: "top",
						container: "body",
						title: item.description
					});

				} else {
					tdDescription.text(item.description);
				}
			}

			var tdDataType = $("<td>");
			tdDataType.text(item.type);
			tr.append(tdName);
			tr.append(tdDescription);
			tr.append(tdDataType);
			itemArr.push(tr);
		}

		$("div[id='items']").append(itemsTable);
		currentPageIndex = 0;

		// Global
		var chunkedItemsArr = itemArr.chunk(10);
		var pagination = createPagination({
			div: $("div[id='items']"),
			itemsArr: chunkedItemsArr
		});

		itemsTable.after(pagination);
		resetTable({
			table: itemsTable,
			arr: chunkedItemsArr,
			pagination: pagination
		});
	}
}

function createLoader() {
	var modalOuterDiv = createDiv({
		divClass: "spinner"
	});
	modalOuterDiv.append($('<img src="images/loader.gif" alt="Loading...">'));
	return modalOuterDiv;
}

function createPrompt(params) {
	bootbox.dialog({
		message: "The current expresion will be lost. Are you sure you want to select another study?",
		title: "Changing the study",
		buttons: {
			keep: {
				label: "Keep",
				className: "btn-success",
				callback: function() {
					params.reset = false;
					resetStudy(params);
					parser.setCopy(true);
					// Add targets, insert items and show/hide items
					var crfItems = parser.getRuleCRFItems();
					if (crfItems) {
						for (var x = 0; x < crfItems.length; x++) {
							var item = parser.getItem(crfItems[x].itemName);
							if (!item) {
								crfItems[x].holder.addClass("invalid");
							} else {
								crfItems[x].holder.removeClass("invalid");
							}
						}
					}
				}
			},
			clear: {
				label: "Clear",
				className: "btn-danger",
				callback: function() {
					params.reset = true;
					resetStudy(params);
				}
			},
			main: {
				label: "Cancel",
				className: "btn-primary",
				callback: function() {
					bootbox.hideAll();
				}
			}
		}
	});
}

function resetStudy(params) {
	$("a[href='#events']").tab('show');
	// Make bold
	$(params.row).siblings(".selected").removeClass("selected");
	$(params.row).addClass("selected");
	parser.setStudy(params.study.id);
	loadStudyEvents(params.study);
	// Cascade load
	var topEvent = params.study.events[Object.keys(params.study.events)[0]]
	loadEventCRFs({
		study: params.study,
		studyEvent: topEvent
	});

	if (topEvent) {
		// bold event
		parser.recursiveSelect({
			type: "event",
			candidate: topEvent.oid
		});
		loadCRFVersions({
			evt: topEvent,
			study: params.study,
			crf: topEvent.crfs[Object.keys(topEvent.crfs)[0]]
		});
		// bold crf
		parser.recursiveSelect({
			type: "crf",
			candidate: topEvent.crfs[Object.keys(topEvent.crfs)[0]].oid
		});
		loadCRFVersionItems({
			evt: topEvent.oid,
			crf: topEvent.crfs[Object.keys(topEvent.crfs)[0]].oid,
			version: topEvent.crfs[Object.keys(topEvent.crfs)[0]].versions[0]
		});
		// bold crf version
		parser.recursiveSelect({
			type: "version",
			candidate: topEvent.crfs[Object.keys(topEvent.crfs)[0]].versions[0].oid
		});
	}
	// boot-strap crumb
	createBreadCrumb({
		study: params.study.name
	});

	if (params.reset) {
		resetBuildControls($("#designSurface > .panel > .panel-body").filter(":first"));
	}
}
