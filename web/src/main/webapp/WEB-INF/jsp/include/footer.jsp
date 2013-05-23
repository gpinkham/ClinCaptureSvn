<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.buildNumber" var="resbuildnumber"/>
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

            <table id="footerInnerTable1"  border="0" cellpadding="0" cellspacing="0" width="" style="white-space: nowrap;">
            <tr>
                <td class="footer">
                <input id="ShowAbout" class="button_medium" type="submit" onClick="openDefWindow('help/about.jsp'); return false;" value="<fmt:message key="about" bundle="${resword}"/>" name="BTN_Support"/><br/><br/>
                <input id="ShowContactCAdministrator" class="button_medium" type="submit" onClick="window.location.href=('${pageContext.request.contextPath}/Contact');" value="<fmt:message key="nav_support" bundle="${resword}"/>" name="BTN_Support"/><br/><br/>
                <input id="ShowForum" class="button_medium" type="submit" onClick="window.location.href=('http://www.clinovo.com/clincapture/forum/');" value="<fmt:message key="forums" bundle="${resword}"/>" name="BTN_Support"/><br/><br/>		    
                <input id="ShowCommunity" class="button_medium" type="submit" onClick="window.location.href=('http://www.clinovo.com/clincapture/community');" value="<fmt:message key="community" bundle="${resword}"/>" name="BTN_Support"/>
                &nbsp;&nbsp;&nbsp;
                </td>
                <td class="footer" style="width: 450px"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td class="footer" align="right">
                    <div style="min-width: 70px;">
                        <fmt:message key="Version_release" bundle="${resword}"/>&nbsp;&nbsp;<fmt:message key="buildNumber" bundle="${resbuildnumber}"/>
                    </div>
                </td>
				<td width="80" align="right" valign="bottom" nowrap> 
					&nbsp;&nbsp;
					<a href="http://www.clinovo.com/"><img src="${pageContext.request.contextPath}/images/CLIlogo.jpg" width="150" height="50" border="0" ></a> 
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

<script language="JavaScript">
    if (document.body != null) {
        document.getElementById("footerInnerTable1").setAttribute("width", (document.body.clientWidth || document.body.innerWidth));
    }
</script>

</body>
</html>
