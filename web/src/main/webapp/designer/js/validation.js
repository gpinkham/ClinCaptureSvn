$(function() {
	
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];

	$("a[id='back']").attr("href", c + "/designer/rule.html");
	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment?read=true");

	if (sessionStorage.getItem("validation")) {

		var validation = JSON.parse(sessionStorage.getItem("validation"));

		var rule = JSON.parse(sessionStorage.getItem("rule"));

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
					list.text("Intial data entry");

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

			sessionStorage.setItem("status", "remove");

			var obj = JSON.parse(response);
			bootbox.confirm(obj.message, function(result) {

				if (result) {

					window.open("rule.html", '_self');
				}
			});
			
		},

		error: function(response) {

			sessionStorage.setItem("status", "load");

			handleErrorResponse({

				response: response
			})
		}
	})
}
