<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<div class="pie_chart_wrapper" align="left" style="overflow:hidden">
	<div id="subject_status_count_chart"></div>
</div>

<form class="hidden" id="subjects_status_count">
	<input type="text" id="ssc_available" value="${countOfAvailableSubjects}" />
	<input type="text" id="ssc_signed" value="${countOfSignedSubjects}" />
	<input type="text" id="ssc_removed" value="${countOfRemovedSubjects}" />
	<input type="text" id="ssc_locked" value="${countOfLockedSubjects}" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='available'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='Signed'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='removed'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='locked'/>" />
</form>

<script>applyThemeForChart();</script>