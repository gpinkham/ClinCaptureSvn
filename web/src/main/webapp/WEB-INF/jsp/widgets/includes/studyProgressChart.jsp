<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<div class="pie_chart_wrapper" align="left" style="overflow:hidden">
	<div id="study_progress_chart"></div>
</div>

<form class="hidden" id="study_progress">
	<c:forEach items="${studyProgressMap}" var="eventStatus" varStatus="status">
		<input type="text" id="sp_${fn:replace(eventStatus.key, ' ', '_')}_count" value="${eventStatus.value}"/>
	</c:forEach>
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='scheduled'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='data_entry_started'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='SDV_complete'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='Signed'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='completed'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='skipped'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='stopped'/>" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resword}' key='locked'/>" />
</form>

<script>applyThemeForChart();</script>