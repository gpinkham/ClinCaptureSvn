<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.buildNumber" var="resbuildnumber"/>

<script type="text/javascript" src="<c:url value='/includes/wz_tooltip/wz_tooltip.js'/>"></script>
<!-- END MAIN CONTENT AREA -->
</td>
			</tr>
		</table>
		</td>
	</tr>
	<br>
	<tr>
		<td valign="bottom">

<!-- Footer -->

		<table border="0" cellpadding=0" cellspacing="0" width="100%">
			 <tr>
			 	
				<td class="footer">
				<a href="${pageContext.request.contextPath}/help/about.jsp" target="_blank"><fmt:message
							key="about" bundle="${resword}" /></a><br />
				<br />
				<br />
				<br />
				
				<a href="${pageContext.request.contextPath}/Contact"><fmt:message
							key="nav_support" bundle="${resword}" /></a><br />
				<br />
				<br />
				<br /><a href="http://www.clinovo.com/clincapture/forum/" target="_blank"><fmt:message
							key="forums" bundle="${resword}" /></a><br/>
				<br />
				<br />
				<br /><a href="http://www.clinovo.com/clincapture/community/" target="_blank"><fmt:message
							key="community" bundle="${resword}" /></a> 
						&nbsp;&nbsp;&nbsp;</td>
				<td class="footer"></td>
                <td class="footer" align="right">
                    <div style="min-width: 70px;">
                        <fmt:message key="Version_release" bundle="${resword}"/>&nbsp;&nbsp;<fmt:message key="buildNumber" bundle="${resbuildnumber}"/>
                    </div>
                </td>
                <td width="80" align="right" valign="bottom" nowrap> 
					&nbsp;&nbsp;
					<a href="http://www.clinovo.com/" target="_blank"><img src="<c:url value='${logoUrl}'/>" border="0" ></a>
					&nbsp;&nbsp;
				</td>
            </tr>
            <tr>
                <td class="footer"/>
                <td class="footer"> </td>
                <td>&nbsp;</td> <%-- <td align="right" class="footer"><a href="javascript:void(0)" onmouseover="Tip('<fmt:message key="footer.tooltip" bundle="${resword}"/>')" onmouseout="UnTip()"><center><fmt:message key="footer.edition.1" bundle="${resword}"/></center></a></td> --%>
            </tr>
            <tr>
                <td class="footer"/>
                <td class="footer"> <%-- <fmt:message key="footer.license.3" bundle="${resword}"/> --%> </td>
                <td>&nbsp;</td> <%-- <td align="right" class="footer"><a href="javascript:void(0)" onmouseover="Tip('<fmt:message key="footer.tooltip" bundle="${resword}"/>')" onmouseout="UnTip()"><center><fmt:message key="footer.edition.2" bundle="${resword}"/></center></a></td> --%>
            </tr>
		</table>

<!-- End Footer -->

		</td>
	</tr>
</table>

<script type="text/javascript">
        jQuery(document).ready(function() {
            jQuery('#cancel').click(function() {
                jQuery.unblockUI();
                return false;
            });

            jQuery('#Contact').click(function() {
                jQuery.blockUI({ message: jQuery('#contactForm'), css:{left: "200px", top:"180px" } });
            });
        });

    </script>


        <div id="contactForm" style="display:none;">
              <%-- <c:import url="contactPop.jsp">
              </c:import> --%>
        </div>
</body>

</html>
