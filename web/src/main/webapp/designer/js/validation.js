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

$(function() {
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
	$("a[id='back']").attr("href", c + "/designer/rule.html");
	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment");
	var rule = JSON.parse(sessionStorage.getItem("rule"));
	if (rule.validation) {
		console.log(rule.xml);
		var validation = JSON.parse(rule.validation);
		sessionStorage.setItem("status", "load");
		// if a rule passed validation
		if (validation.ruleValidation === "rule_valid") {
			$(".failure").hide();
			$(".success").show();
			$("#failure").hide();
			$(".alert-info").hide();
			$(".alert-success").text("Rule is valid: " + rule.targets[0].expression);
			if (rule.targets) {
				for (var x = 0; x < rule.targets.length; x++) {
					var list = $("<li class='list-group-item'>");
					list.text(rule.targets[x].name);
					$("#items").append(list);
				}
				// Rule actions
				var list = $("<li class='list-group-item'>");
				if (rule.actions[0].type === "discrepancy") {
					list.text("Discrepancy Action");
				} else if (rule.actions[0].type === "email") {
					list.text("Email Action");
				} else if (rule.actions[0].type === "insert") {
					list.text("Insert Action");
				} else if (rule.actions[0].type === "showHide") {
					list.text("Show/Hide Action");
				}
				$("#action").append(list);
				// Initial data entry action
				if (rule.ide) {
					var list = $("<li class='list-group-item'>");
					list.text("Initial data entry");
					$("#executions").append(list);
				}
				// Double data entry action
				if (rule.dde) {
					var list = $("<li class='list-group-item'>");
					list.text("Double data entry");
					$("#executions").append(list);
				}
				// Administrative data entry action
				if (rule.ae) {
					var list = $("<li class='list-group-item'>");
					list.text("Administrative data entry");

					$("#executions").append(list);
				}
				// Data import action
				if (rule.di) {
					var list = $("<li class='list-group-item'>");
					list.text("Import data entry");
					$("#executions").append(list);
				}
			}
		// If a rule failed validation
		} else {
			$("#save").remove();
			$(".success").hide();
			$(".failure").show();
			$("#evaluates").text("Rule failure message");
			$(".alert-info").text(validation.ruleValidationFailMessage);
		}
	} else {
		// Redirect to design if no rule present
		window.open("rule.html", '_self');
	}
	$("#save").click(function() {
		saveRule(rule);
	});
	// Clean up on exit
	$("#exit").click(function() {
		cleanUp();
		$(this).click();
	});
});

/* =================================================================
 * Saves a given validated rule to CC
 *
 * Arguments [data]:
 * => data - the xml data to post to CC via the rule import servlet
 *
 * ============================================================= */
function saveRule(rule) {

	$("body").append(createLoader());
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
	var boundary = "---------------------------7da24f2e50046";
	var body = '--' + boundary + '\r\n'
	+ 'Content-Disposition: form-data; name="file";' + 'filename="temp.xml"\r\n'
	+ 'Content-type: text/xml\r\n\r\n'
	+ rule.xml + '\r\n'
    + '--'+boundary+ '--';

	$.ajax({
		type: "POST",
		contentType: "multipart/form-data; boundary="+boundary,
		data: body,
		url: c + "/ImportRule?action=confirm&rs=true&edit=" + rule.editing + "&id=" + rule.ruleSet + "&study=" + rule.study + "&copy=" + rule.copied,
		success: function(response) {
			try {
				// Context
				var ctx = Object.create(null);

				ctx.ae = rule.ae;
				ctx.di = rule.di;
				ctx.dde = rule.dde;
				ctx.ide = rule.ide;
				ctx.context = true;
				ctx.study = rule.study;
				ctx.targets = rule.targets;
				ctx.actions = rule.actions;
				ctx.evaluates = rule.evaluates;
				// Persist in sessin
				sessionStorage.setItem("context", JSON.stringify(ctx));
				cleanUp();
				window.open(rule.submission + "/designer/rule.html", '_self');
			} catch (e) {
				removeLoader();
				bootbox.alert({
					backdrop: false,
					message: "The rule was not saved! Check the server logs for details"
				});
			}
		},
		error: function(response) {
			sessionStorage.setItem("status", "load");
			handleErrorResponse({
				response: response
			});
		}
	})
}

function cleanUp() {
	sessionStorage.removeItem("rule");
	sessionStorage.removeItem("status");
}
