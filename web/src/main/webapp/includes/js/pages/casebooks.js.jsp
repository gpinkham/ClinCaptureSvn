<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<script type="text/javascript">
	var $loader = "<img src='../images/ajax-loader-blue.gif' alt='Generating'/>";

	$(window).load(function(){
		var oidsList = "${generatingOIDs}";

		if (oidsList.length == 0) {
			return;
		}
		console.log("list: " + oidsList);
		var splitedList = oidsList.replace("[","").replace("]","").split(",");
		splitedList.forEach(function(item) {
			var $input = $("input[name=oids][ssoid=" + item + "]");
			$input.css("display", "none");
			$input.parent().append($loader);
		});
	});

	function selectAll(isSelect) {
		$("input[name='oids']").each(function () {
			this.checked = isSelect;
		});
	}

	function onInvokeAction(id) {
		createHiddenInputFieldsForLimitAndSubmit(id);
	}

	function sendSubjectOids() {
		var $sidebar = $("#sidebar_Alerts_open");
		var $sidebarContent = $sidebar.find(".sidebar_tab_content");
		var $subjectsList = $("input[name=oids]:checked");
		var list = $subjectsList.map(function () { return $(this).attr("ssoid");}).get().join(",");
		$subjectsList.each(function(){
			$(this).css("display", "none");
			$(this).parent().append($loader);
		});
		var url = new RegExp("^.*(pages)").exec(window.location.href.toString())[0];
		if ($sidebar.css("display") == 'none') {
			leftnavExpand('sidebar_Alerts_open');
			leftnavExpand('sidebar_Alerts_closed');
		}
		$sidebarContent.html("<div class='alert'><fmt:message key="selected_casebooks_in_progress" bundle="${resword}"/></div>");
		$.ajax({
			type: "POST",
			url: url + "/generateCasebooks",
			data: {
				oids: list
			},
			success: function (data) {
				var message = '<fmt:message key="casebooks_generated" bundle="${resword}"/>'.replace("{0}/", "");
				$sidebarContent.html('<div class="alert">' + message + '</div>');
				$subjectsList.each(function(){
					$(this).css("display", "");
					$(this).parent().find("img").remove();
				});
			},
			error: function (e) {
				console.log("Error:" + e);
			}
		});
	}
</script>