<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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

            <table id="footerInnerTable1"  border="0" cellpadding="0" cellspacing="0" style="white-space: nowrap;" class="footerInnerTable">
            <tr>
                <td class="footer" style="width: 450px"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="footer" align="right">
                    <div style="min-width: 70px;">
                        <fmt:message key="Version_release" bundle="${resword}"/>&nbsp;&nbsp;<fmt:message key="buildNumber" bundle="${resbuildnumber}"/>
                    </div>
                </td>
				<td width="80" align="right" valign="bottom" nowrap> 
					&nbsp;&nbsp;
					<a href="http://www.clinovo.com/" target="_blank"><img src="<c:url value='${logoUrl}'/>" width="150" border="0" ></a>
					&nbsp;&nbsp;
				</td>
            </tr>
            <tr>
                <td class="footer"/>
                <td class="footer" style="width: 450px"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td>&nbsp;</td> 
            </tr>
            <tr>
                <td class="footer"/>
                <td class="footer"></td>
                <td>&nbsp;</td> 
            </tr>
        </table>

<!-- End Footer -->

        </td>
    </tr>
</table>
<jsp:include page="../include/changeTheme.jsp"/>


</body>
</html>
