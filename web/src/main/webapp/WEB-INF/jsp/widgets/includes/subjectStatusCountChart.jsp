<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="pie_chart_wrapper" align="left" style="overflow:hidden">
	<div id="chart_div"></div>
</div>

<form class="hidden" id="subjects_status_count">
	<input type="text" id="ssc_available" value="${countOfAvailableSubjects}" />
	<input type="text" id="ssc_removed" value="${countOfRemovedSubjects}" />
	<input type="text" id="ssc_locked" value="${countOfLockedSubjects}" />
	<input type="text" id="ssc_signed" value="${countOfSignedSubjects}" />
</form>
