<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
 
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.buildNumber" var="resbuildnumber"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<!-- END MAIN CONTENT AREA -->
</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td valign="bottom">

<!-- Footer -->

            <table id="footerInnerTable2" border="0" cellpadding="0" cellspacing="0" width="">
			<tr>
				<td class="footer">
				<!--<a href="#">About ClinCapture</a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="#">Terms of Use</a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="#">Privacy Policy</a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				-->
                <!--
				<a href="#"><fmt:message key="openclinica_portal" bundle="${resword}"/></a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="javascript:openDocWindow('help/index.html')"><fmt:message key="help" bundle="${resword}"/></a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="javascript:reportBug()"><fmt:message key="openclinica_feedback" bundle="${resword}"/></a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			    -->
				<a href="#"><fmt:message key="contact" bundle="${resword}"/></a>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a href="#"><fmt:message key="openclinica_enterprise" bundle="${resword}"/></a>
				</td>
				<td class="footer" align="right">
                    <div style="min-width: 70px;">
                        <fmt:message key="Version_release" bundle="${resword}"/>&nbsp;&nbsp;<fmt:message key="buildNumber" bundle="${resbuildnumber}"/>
                    </div>
                </td>
				<td width="80" align="right" valign="bottom"><a href="http://www.clinovo.com/"><img src="<c:url value='${logoUrl}'/>" width="150" height="50" border="0" ></a></td>
			</tr>
		</table>

<!-- End Footer -->

		</td>
	</tr>
</table>

<script language="JavaScript">
    if (document.body != null) {
        document.getElementById("footerInnerTable2").setAttribute("width", (document.body.clientWidth || document.body.innerWidth));
    }
</script>

</body>

</html>
