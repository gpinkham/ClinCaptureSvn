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
	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment?read=true");

	var rule = JSON.parse(sessionStorage.getItem("rule"));

	if (sessionStorage.getItem("validation")) {

		var validation = JSON.parse(sessionStorage.getItem("validation"));

		sessionStorage.setItem("status", "load");

		// if a rule passed validation
		if (validation.ruleValidation === "rule_valid") {

			$(".failure").hide();
			$(".success").show();

			$(".alert-success").text("Rule is valid: " + rule.expression);
			$("#evaluates").text("Rule evaluates to");
			$(".alert-info").text(validation.ruleEvaluatesTo);

			if (rule.targets) {

				for (var x = 0; x < rule.targets.length; x++) {

					var list = $("<li class='list-group-item'>");
					list.text(rule.targets[x]);

					$("#items").append(list);
				}

				for (var x = 0; x < rule.actions.length; x++) {

					var list = $("<li class='list-group-item'>");
					if (rule.actions[x].type === "discrepancy") {
						list.text("Discrepancy Action");
					} else if (rule.actions[x].type === "email") {
						list.text("Email Action");
					}

					$("#actions").append(list);
				}
				
				if (rule.ide) {

					var list = $("<li class='list-group-item'>");
					list.text("Initial data entry");

					$("#executions").append(list);
				}

				if (rule.dde) {

					var list = $("<li class='list-group-item'>");
					list.text("Double data entry");

					$("#executions").append(list);
				}

				if (rule.ae) {

					var list = $("<li class='list-group-item'>");
					list.text("Administrative data entry");

					$("#executions").append(list);
				}

				if (rule.di) {

					var list = $("<li class='list-group-item'>");
					list.text("Import data entry");

					$("#executions").append(list);
				}
			}

			// If a rule failed validation
		} else {

			$("#save").hide();
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
	})
})

/* =================================================================
 * Saves a given validated rule to CC
 *
 * Arguments [data]:
 * => data - the xml data to post to CC via the rule import servlet
 *
 * ============================================================= */
function saveRule(rule) {

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

		url: c + "/ImportRule?action=confirm&rs=true&edit=" + sessionStorage.getItem("edit") + "&id=" + sessionStorage.getItem("id"),

		success: function(response) {

			sessionStorage.setItem("status", "remove");

			var obj = JSON.parse(response);
			bootbox.confirm(obj.message, function(result) {

				if (result) {

					window.open(rule.submission + "/ViewRuleAssignment?read=true", '_self');
				}
			});

			sessionStorage.removeItem("edit");
		},

		error: function(response) {

			sessionStorage.setItem("status", "load");

			handleErrorResponse({

				response: response
			})
		}
	})
}
