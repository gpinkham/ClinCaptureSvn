<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<script>
	<c:import url="../../includes/js/widgets/w_subject_status_count.js" />
</script>

<div class="subject_status_count" align="center">
	<h2><fmt:message key="subject_status_count_widget_header" bundle="${resword}"/></h2>
	<div id="subject_status_count_container">
	</div>	
</div>
