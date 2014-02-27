<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<script>
$(document).ready(function($) {
	setTimeout(function() { initEventsCompletionWidget("init"); }, 700);
});
</script>
<div class="events_completion" align="center">
	<h2><fmt:message key="events_completion_widget_header" bundle="${resword}"/></h2>
	<div id="events_completion_container">
	</div>
	<table class="captions_percents">
		<tr>
			<td>0%</td>
			<td>10</td>
			<td>20</td>
			<td>30</td>
			<td>40</td>
			<td>50</td>
			<td>60</td>
			<td>70</td>
			<td>80</td>
			<td>90</td>
			<td>100%</td>
		</tr>
	</table>
	<table class="signs">
		<tr>
			<td><div class="scheduled sign"></div> - Scheduled</td>
			<td><div class="data_entry_started sign"></div> - DES*</td>
			<td><div class="completed sign"></div> - Completed</td>
		</tr>
		<tr>
			<td><div class="signed sign"></div> - Signed</td>		
			<td><div class="locked sign"></div> - Locked</td>
			<td><div class="skipped sign"></div> - Skipped</td>
		</tr>
		<tr>
			<td><div class="stopped sign"></div> - Stopped</td>
			<td><div class="not_scheduled sign"></div> - Not Scheduled</td>
			<td><div class="sdved sign"></div> - SDV**</td>
		</tr>
	</table>	
	<table class="notes">
	<tr><td>* - Data Entry Started</td>
		<td>** - Source Data Verified</td></tr>
	</table>
	<table>
		<tr>
			<td align="left">
				<input type="button" name="BTN_Back" id="previous" value="Previous" class="button_medium" onClick="javascript: initEventsCompletionWidget('goBack');"/>
			</td>
			<td align="right">
				<input type="button" name="BTN_Forvard" id="next" value="Next" class="button_medium" onClick="javascript: initEventsCompletionWidget('goForward');"/>
			</td>
		</tr>
	</table>	
</div>