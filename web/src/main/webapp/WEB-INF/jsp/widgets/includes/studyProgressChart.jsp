<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="pie_chart_wrapper" align="left" style="overflow:hidden">
	<div id="study_progress_chart"></div>
</div>

<form class="hidden" id="study_progress">
	<c:forEach items="${studyProgressMap}" var="eventStatus" varStatus="status">
		<input id="sp_${fn:replace(eventStatus.key, ' ', '_')}_count" value="${eventStatus.value}"/>
	</c:forEach>
</form>

<script>applyThemeForChart();</script>