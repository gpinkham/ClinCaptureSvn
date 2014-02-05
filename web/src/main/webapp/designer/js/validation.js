$(function() {
	
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment?read=true");
	$("a[id='back']").attr("href", c + "/designer/rule.html");

	if (sessionStorage.getItem("validation")) {

		var validation = JSON.parse(sessionStorage.getItem("validation"));

		var expression = sessionStorage.getItem("expression");
		var actions = JSON.parse(sessionStorage.getItem("actions"));
		var targets = JSON.parse(sessionStorage.getItem("targets"));
		var properties = JSON.parse(sessionStorage.getItem("properties"));

		sessionStorage.setItem("status", "loaded");

		// if a rule passed validation
		if (validation.ruleValidation === "rule_valid") {

			$(".failure").hide();
			$(".success").show();

			$(".alert-success").text("Rule is valid: " + expression);
			$("#evaluates").text("Rule evaluates to");
			$(".alert-info").text(validation.ruleEvaluatesTo);

			if (targets) {

				for (var x = 0; x < targets.length; x++) {

					var list = $("<li class='list-group-item'>");
					list.text(targets[x]);

					$("#items").append(list);
				}

				for (var x = 0; x < actions.length; x++) {

					var list = $("<li class='list-group-item'>");
					if (actions[x] === "discrepancy") {
						list.text("Discrepancy Action");
					} else if (actions[x] === "email") {
						list.text("Email Action");
					}

					$("#actions").append(list);
				}
				
				if (properties.initialDataEntry) {

					var list = $("<li class='list-group-item'>");
					list.text("Intial data entry");

					$("#executions").append(list);
				}

				if (properties.doubleDataEntry) {

					var list = $("<li class='list-group-item'>");
					list.text("Double data entry");

					$("#executions").append(list);
				}

				if (properties.administrativeDataEntry) {

					var list = $("<li class='list-group-item'>");
					list.text("Administrative data entry");

					$("#executions").append(list);
				}

				if (properties.importDataEntry) {

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

			sessionStorage.setItem("status", "failed");
		}
	} else {

		// Redirect to design if no rule present
		window.open("rule.html", '_self');
	}

	$("#save").click(function() {

		saveRule(sessionStorage.getItem("xml"));
	})
})

/* =================================================================
 * Saves a given validated rule to CC
 *
 * Arguments [data]:
 * => data - the xml data to post to CC via the rule import servlet
 *
 * ============================================================= */
function saveRule(data) {

	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	var boundary = "---------------------------7da24f2e50046";

	var body = '--' + boundary + '\r\n'
	+ 'Content-Disposition: form-data; name="file";' + 'filename="temp.xml"\r\n'
	+ 'Content-type: text/xml\r\n\r\n'
	+ data + '\r\n'
    + '--'+boundary+ '--';

	$.ajax({

		type: "POST",
		contentType: "multipart/form-data; boundary="+boundary,

		data: body,

		url: c + "/ImportRule?action=confirm&rs=true",

		success: function(response) {

			var obj = JSON.parse(response);
			bootbox.confirm(obj.message, function(result) {

				if (result) {

					window.open("rule.html", '_self');
				}
			});
			
		},

		error: function(response) {

			handleErrorResponse({

				response: response
			})
		}
	})
}