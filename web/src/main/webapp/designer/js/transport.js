/* ==================================================================
 * Fetch studies from CC. The studies come with events/crf and items
 * added.
 * =============================================================== */
function fetchStudies() {
	
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$.ajax({

		type: "POST",

		url: c + "/studies?action=fetch",

		success: function(studies) {

			// FF can return a string
			if (typeof(studies) === "string") {

				studies = JSON.parse(studies)
			}

			sessionStorage.setItem("studies", JSON.stringify(studies));
			loadStudies(studies);

		},

		error: function(response) {

			handleErrorResponse({

				response: response
			})
		}
	})
}

function fetchRuleForEditing() {
	
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$.ajax({

		type: "POST",

		url: c + "/studies?action=edit&id=" + gup("id"),

		success: function(response) {

			var rule = null;

			// FF can return a string
			if (typeof(response) === "string") {

				rule = JSON.parse(response)
			}

			parser.render(rule);

		},

		error: function(response) {

			handleErrorResponse({

				response: response
			})
		}
	})
}

/* ===================================================
 * Adds the studies to the studies table for display.
 *
 *
 * Argument Object [studies] parameters:
 *  - studies - the return studies from CC
 * =================================================== */
function loadStudies(studies) {

	var itemArr = []
	$("div[id='studies']").find("table").remove();

	if (studies) {

		// Table headers
		var table = createTable(['Name', 'OID', 'Identifier']);

		for (var x = 0; x < studies.length; x++) {

			var study = studies[x]

			var tr = $("<tr>");

			tr.attr("id", x);
			tr.click(function() {

				if ($("div[id='studies']").find("table > tbody > tr").size() > 1 && $("#designSurface").find(".panel-body").find(".dotted-borders").size() > 2) {

					bootbox.confirm("The current rule will be lost. Are you sure you want to select another study?", 
						function(result) {

						if (result) {

							$("a[href='#events']").tab('show');

							// Make bold
							tr.siblings(".selected").removeClass("selected");

							tr.addClass("selected");

							var data = JSON.parse(sessionStorage.getItem("studies"));

							var currentStudy = data[tr.attr("id")]

							selectedStudy = currentStudy.id;

							loadStudyEvents(currentStudy)

							// Cascade load
							var topEvent = currentStudy.events[Object.keys(currentStudy.events)[0]]

							loadEventCRFs({

								study: currentStudy,
								studyEvent: topEvent
							});

							loadCRFVersionItems(topEvent.crfs[Object.keys(topEvent.crfs)[0]])

							createBreadCrumb({

								study: currentStudy.name,
							})
							
							resetBuildControls($("#designSurface > .panel > .panel-body").filter(":first"));

							$(".modal-backdrop").remove();
						} else {
							$(".modal-backdrop").remove();
						}
					});
				} else {

					// Make bold
					$(this).siblings(".selected").removeClass("selected");

					$(this).addClass("selected");

					var data = JSON.parse(sessionStorage.getItem("studies"));

					var currentStudy = data[$(this).attr("id")]

					selectedStudy = currentStudy.id;

					if (currentStudy.events) {

						loadStudyEvents(currentStudy)

						// Cascade load
						var topEvent = currentStudy.events[Object.keys(currentStudy.events)[0]]

						loadEventCRFs({

							study: currentStudy,
							studyEvent: topEvent
						});

						var version = topEvent.crfs[0].versions[0]

						loadCRFVersions({
							study: study,
							event: topEvent,
							crf: topEvent.crfs[0]
						});

						loadCRFVersionItems(version);
					}

					createBreadCrumb({

						study: currentStudy.name,
					})

					$("a[href='#events']").tab('show');
				}
			})

			var tdName = $("<td>");
			tdName.text(study.name);

			var tdOID = $("<td>");
			tdOID.text(study.oid);

			var tdIdentifier = $("<td>");
			tdIdentifier.text(study.identifier);

			tr.append(tdName);
			tr.append(tdOID);
			tr.append(tdIdentifier);

			itemArr.push(tr);
		}

		$("div[id='studies']").append(table)

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

		$(".table-hover").find("tbody > tr").filter(":first").click();

		// Initial load should show studies
		$("a[href='#studies']").tab('show');
	}
}

/* =================================================================
 * Adds the a given study's events to the events table for display.
 *
 *
 * Argument Object [study] parameters:
 *  - study - the study for whom events should be loaded
 * ============================================================== */
function loadStudyEvents(study) {

	var itemArr = []
	$("div[id='events']").find("table").remove();

	if (study.events) {

		var eventTable = createTable(['Name', 'Description', 'Identifier']);

		for (var x = 0; x < study.events.length; x++) {

			var studyEvent = study.events[x]

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

				// Cascade load
				loadCRFVersions({
					study: study,
					event: currentEvent,
					crf: currentEvent.crfs[Object.keys(currentEvent.crfs)[0]]
				});

				createBreadCrumb({

					study: study.name,
					event: currentEvent.name,
				})

			})

			var tdName = $("<td>");
			tdName.text(studyEvent.name);

			var tdDescription = $("<td>");

			if (studyEvent.description) {

				if (studyEvent.description.length > 25) {

					tdDescription.text(studyEvent.description.slice(0, 20) + "...");

					tdDescription.tooltip({

						placement: "top",
						container: "body",
						title: studyEvent.description
					})

				} else {

					tdDescription.text(studyEvent.description);
				}
			}

			var tdIdentifier = $("<td>");
			tdIdentifier.text(studyEvent.identifier);

			tr.append(tdName);
			tr.append(tdDescription);
			tr.append(tdIdentifier);

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
 *
 * Argument Object [params] parameters:
 * - studyEvent - the event for whom crfs should be loaded
 * - study - the study to which the event belongs to
 * ============================================================== */
function loadEventCRFs(params) {

	var itemArr = []
	$("div[id='crfs']").find("table").remove();

	if (params.studyEvent.crfs && params.studyEvent.crfs) {

		var crfTable = createTable(['Name', 'Identifier', 'Description']);

		for (var cf = 0; cf < params.studyEvent.crfs.length; cf++) {

			var crf = params.studyEvent.crfs[cf]

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
					event: params.studyEvent.name
				});

				createBreadCrumb({

					crf: currentCRF.name,
					study: params.study.name,
					event: params.studyEvent.name,
					
				})
			})

			var tdName = $("<td>");
			tdName.text(crf.name);

			var tdDescription = $("<td>");

			if (crf.description) {

				if (crf.description.length > 25) {

					tdDescription.text(crf.description.slice(0, 20) + "...");

					tdDescription.tooltip({

						placement: "top",
						container: "body",
						title: crf.description,
					})

				} else {

					tdDescription.text(crf.description);
				}
			}

			var tdOID = $("<td>");
			if (crf.oid) {

				if (crf.oid.length > 7) {

					tdOID.text(crf.oid.slice(0, 7) + "...");

					tdOID.tooltip({

						placement: "top",
						container: "body",
						title: crf.oid,
					})

				} else {

					tdOID.text(crf.oid);
				}
			}

			var tdVersion = $("<td>");
			tdVersion.text(crf.version);

			tr.append(tdName);
			tr.append(tdOID);
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

	var itemArr = []
	$("div[id='versions']").find("table").remove();

	if (params.crf.versions) {

		var versionTable = createTable(['Name', 'Identifier']);

		for (var ver = 0; ver < params.crf.versions.length; ver++) {

			var version = params.crf.versions[ver]

			var tr = $("<tr>");
			tr.attr("id", ver);
			tr.click(function() {

				$("a[href='#items']").tab('show');

				// Make bold
				$(this).siblings(".selected").removeClass("selected");

				$(this).addClass("selected");

				var currentVersion = params.crf.versions[$(this).attr("id")];

				loadCRFVersionItems(currentVersion);

				createBreadCrumb({

					event: params.event.name,
					study: params.study.name,
					version: currentVersion.name

				})
			})

			var tdName = $("<td>");
			tdName.text(version.name);


			var tdOID = $("<td>");

			if (version.oid) {

				if (version.oid.length > 7) {

					tdOID.text(version.oid.slice(0, 7) + "...");

					tdOID.tooltip({

						placement: "top",
						container: "body",
						title: version.oid,
					})

				} else {

					tdOID.text(version.oid);
				}
			}

			tr.append(tdName);
			tr.append(tdOID);

			itemArr.push(tr);
		}

		$("div[id='versions']").append(versionTable)

		currentPageIndex = 0;

		// Global
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
function loadCRFVersionItems(crf) {

	var itemArr = []
	$("div[id='items']").find("table").remove();

	if (crf.items) {

		var itemsTable = createTable(['Name', 'Description', 'Data Type']);

		for (var it = 0; it < crf.items.length; it++) {

			var item = crf.items[it]

			var tr = $("<tr>");

			var tdName = $("<td>");
			tdName.text(item.name);
			tdName.addClass("group")
			tdName.attr("oid", item.oid);
			tdName.attr("goid", item.group);

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
						title: item.description,
					})

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

/* =================================================================
 * Validates the designed rule using the CC Test Rule servlet
 *
 * Arguments [params]:
 * => expression - the expression in text format
 * => targets - the rule targets
 * => evaluateTo - What the rule should evaluate to
 * ============================================================= */
function validate(rule) {

	$.ajax({

		type: "POST",

		data: {

			rs: true,
			rule: rule.expression,
			target: rule.targets[0],
			testRuleActions: rule.evaluateTo
		},

		url: rule.submission + "/TestRule?action=validate&study=" + selectedStudy,

		success: function(response) {

			sessionStorage.setItem("validation", response);
			displayValidationResults(rule)
		},

		error: function(response) {

			handleErrorResponse({

				response: response
			})
		}
	})
}

/* =================================================================
 * Displays the validation results from testing a rule in CC.
 *
 * Note that this function loads the validation page to display the 
 * results.
 *
 * Arguments [params]:
 * => expression - the expression in text format
 * => targets - the rule targets
 * => evaluateTo - What the rule should evaluate to
 * ============================================================= */
function displayValidationResults(rule) {

	$.ajax({

		type: "POST",

		data: {

			action: "save",
			rule: JSON.stringify(rule)
			
		},

		url: rule.submission + "/studies?action=validate",

		success: function(response) {

			if (response) {

				// To be used in validation
				sessionStorage.setItem("xml", response);
				sessionStorage.setItem("rule", JSON.stringify(rule));

				// launch validation window
				window.open("validation.html", '_self');

			}
		},

		error: function(response) {

			handleErrorResponse({

				response: response
			})
		}
	})
}

function getURLParameter(name) {
  return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null
}

function gup(name) {

	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");

	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.href);
	if (results == null) {
		return "";
	} else {
		return results[1];	
	} 
}