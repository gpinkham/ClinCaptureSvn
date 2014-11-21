<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<div class="chart_wrapper" align="left">
	<c:forEach items="${eventCompletionRows}" var="row" varStatus="status">
		<div class="bar_name">${row.rowName}</div>
		<ul barnumber="bar${status.count}" class="stacked_bar">
			<c:forEach items="${row.rowValues}" var="statuses" varStatus="num">
			<c:if test="${statuses.value!=0}">
				<a href="#a" def-id="${row.id}" status-id="${num.count - 1}">
					<li class="${statuses.key} stack">
						<div class="pop-up"></div>
						<div class="hidden" id="value">${statuses.value}</div>
					</li>
				</a>
			</c:if>
			</c:forEach>
		</ul>
	</c:forEach>
</div>

<form id="events_completion_form" class="hidden">
	<input type="text" id="ec_has_next" value="${eventCompletionHasNext}" /> 
	<input type="text" id="ec_has_previous" value="${eventCompletionHasPrevious}" />
	<input type="text" id="ec_last_element" value="${eventCompletionLastElement}" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='scheduled' />" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='data_entry_started' />" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='source_data_verified' />" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='signed' />" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='completed' />" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='skipped' />" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='stopped' />" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='locked' />" />
	<input type="hidden" class="status" value="<fmt:message bundle='${resterm}' key='not_scheduled' />" />
</form>

<script>applyThemeForChart();</script>