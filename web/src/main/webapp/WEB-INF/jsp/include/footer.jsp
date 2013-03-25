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

            <table id="footerInnerTable1"  border="0" cellpadding="0" cellspacing="0" width="">
            <tr>
                <td class="footer">
                <!--
                <a href="http://www.openclinica.org" target="new"><fmt:message key="openclinica_portal" bundle="${resword}"/></a>
                &nbsp;&nbsp;&nbsp;
                <a href="javascript:openDocWindow('help/index.html')"><fmt:message key="help" bundle="${resword}"/></a>
                &nbsp;&nbsp;&nbsp;
                -->
                <input id="ShowContactCAdministrator" class="button_medium" type="submit" onClick="window.location.href=('${pageContext.request.contextPath}/Contact');" value="<fmt:message key="nav_support" bundle="${resword}"/>" name="BTN_Support"/><br/><br/>
                <input id="ShowForum" class="button_medium" type="submit" onClick="window.location.href=('http://www.clinovo.com/clincapture/forum/');" value="<fmt:message key="forums" bundle="${resword}"/>" name="BTN_Support"/><br/><br/>		    
                <input id="ShowCommunity" class="button_medium" type="submit" onClick="window.location.href=('http://www.clinovo.com/clincapture/community');" value="<fmt:message key="community" bundle="${resword}"/>" name="BTN_Support"/>
                &nbsp;&nbsp;&nbsp;
                </td>
                <td class="footer"><fmt:message key="footer.license.2" bundle="${resword}"/> </td>
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
                <td class="footer"> <fmt:message key="footer.license.3" bundle="${resword}"/></td>
                <td>&nbsp;</td> <%-- <td align="right" class="footer"><a href="javascript:void(0)" onmouseover="Tip('<fmt:message key="footer.tooltip" bundle="${resword}"/>')" onmouseout="UnTip()"><center><fmt:message key="footer.edition.1" bundle="${resword}"/></center></a></td> --%>
            </tr>
            <tr>
                <td class="footer"/>
                <td class="footer"><%-- <fmt:message key="footer.license.3" bundle="${resword}"/> --%> </td>
                <td>&nbsp;</td> <%-- <td align="right" class="footer"><a href="javascript:void(0)" onmouseover="Tip('<fmt:message key="footer.tooltip" bundle="${resword}"/>')" onmouseout="UnTip()"><center><fmt:message key="footer.edition.2" bundle="${resword}"/></center></a></td> --%>
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
