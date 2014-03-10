<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="chart_wrapper" align="left">
	<c:forEach items="${eventCompletionRows}" var="row" varStatus="status">
		<div class="bar_name">${row.rowName}</div>
		<ul barnumber="bar${status.count}" class="stacked_bar">
			<c:forEach items="${row.rowValues}" var="statuses">
			<c:if test="${statuses.value!=0}">
				<a href="ListEventsForSubjects?defId=${row.id}&listEventsForSubject_p_=1&listEventsForSubject_mr_=15&listEventsForSubject_f_event.status=${fn:replace(statuses.key, '_', '+')}">
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
</form>