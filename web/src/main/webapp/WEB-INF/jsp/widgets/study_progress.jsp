<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<script>
$(document).ready(function($) {
	setTimeout(function() { initStudyProgress(); }, 700);
});
</script>

<div class="study_progress" align="center">
	<h2><fmt:message key="study_progress_widget_header" bundle="${resword}"/></h2>
	<div id="study_progress_container">
	</div>	
</div>
