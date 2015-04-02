<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<c:set var="generate_dcf_button_class" scope="request" value="${system_lang eq 'ru' ? 'button_long': 'button_medium'}" />

<div id="dcfRenderType" title="<fmt:message key="dcf_generate" bundle="${resword}"/>" >
	<div style="clear: both; margin-top: 2%;">
		<p><fmt:message key="dcf_specify_render_type" bundle="${resword}"/>:</p>
		<input type="checkbox" name="dcfRenderType" class="dcfRenderType" value="save" /> <fmt:message key="dcf_render_save" bundle="${resword}"/><br/>
		<input type="checkbox" name="dcfRenderType" class="dcfRenderType" value="print" /> <fmt:message key="dcf_render_print" bundle="${resword}"/><br/>
		<input type="checkbox" name="dcfRenderType" class="dcfRenderType" value="email" /> <fmt:message key="dcf_render_email" bundle="${resword}"/>: 
		<input id="email" name="email" type="text" value="" style="width: 200px;" /> <br/>
		<div id="invalid_email" style="display: none; text-align: center;" class="alert">
			<fmt:message key="dcf_invalid_email_message" bundle="${resword}"/>
		</div>
		<input type="button" id="btn_cancel_dcf" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium" style="margin-top:20px" />
		<input type="hidden" id="generateDcf" name="generateDcf" value="" />
		&nbsp;&nbsp;
		<input type="button" id="btn_submit_dcf" value="<fmt:message key="dcf_generate" bundle="${resword}"/>" class="${generate_dcf_button_class}" style="margin-top:20px" />
	</div>
</div>

<c:if test="${printDcf ne null}">
	<input type="hidden" id="printDcf" value="${printDcf}" onclick="javascript:openPopup();" />
</c:if>
<c:if test="${saveDcf ne null}">
	<input type="hidden" id="saveDcf" name="saveDcf" value="${saveDcf}" />
</c:if>
<script type="text/javascript" src="includes/js/pages/dcf.js"></script>
<script>
	$(document).ready(function() {
		initDcf("<%=request.getContextPath()%>");
	});
</script>