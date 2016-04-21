<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.buildNumber" var="resbuildnumber"/>

<script type="text/javascript" src="<c:url value='/includes/wz_tooltip/wz_tooltip.js'/>"></script>
<!-- END MAIN CONTENT AREA -->

<!-- Footer -->
<div style="width: 100%; position: absolute; bottom: 0px;">
		<table style="border: 0px; width: 100%;">
			<tr>
				<td style="width: 90%; vertical-align: bottom">
					<table style="border: 0px; width: 100%; height: 30px;">
						<tr>
							<td class="footer" style="width: 20%;">
								<a href="${pageContext.request.contextPath}/help/about.jsp" target="_blank">
									<fmt:message key="about" bundle="${resword}"/></a>
							</td>
							<td class="footer" style="width: 20%;">
								<a href="${pageContext.request.contextPath}/Contact">
									<fmt:message key="nav_support" bundle="${resword}"/></a>
							</td>
							<td class="footer" style="width: 20%;">
								<a href="http://www.clincapture.com/clincapture/forum/" target="_blank">
									<fmt:message key="forums" bundle="${resword}"/></a>
							</td>
							<td class="footer" style="width: 20%;">
								<a href="http://www.clincapture.com/clincapture/community/" target="_blank">
									<fmt:message key="community" bundle="${resword}"/></a>
							</td>
							<td class="footer" style="text-align: right; white-space: nowrap;">
								<fmt:message key="Version_release" bundle="${resword}"/>&nbsp;&nbsp;
								<fmt:message key="buildNumber" bundle="${resbuildnumber}"/>
							</td>
						</tr>
					</table>
				</td>
				<td style="padding-left: 10px; padding-right: 10px; vertical-align: bottom; text-align: right">
					<a href="http://www.clincapture.com/" target="_blank">
						<img src="<c:url value='${logoUrl}'/>" border="0" style="max-height: 150px;">
					</a>
				</td>
			</tr>
		</table>
</div>
<!-- End Footer -->

<script type="text/javascript">
	jQuery(document).ready(function () {
		jQuery('#cancel').click(function () {
			jQuery.unblockUI();
			return false;
		});

		jQuery('#Contact').click(function () {
			jQuery.blockUI({message: jQuery('#contactForm'), css: {left: "200px", top: "180px"}});
		});
	});
</script>

<div id="contactForm" style="display:none;">
</div>
