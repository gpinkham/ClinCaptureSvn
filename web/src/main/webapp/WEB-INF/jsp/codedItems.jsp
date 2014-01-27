<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>

<jsp:include page="include/managestudy_top_pages.jsp"/>

<jsp:include page="include/sideAlert.jsp"/>

<c:set var="color" scope="session" value="${newThemeColor}"/>
<c:if test="${(color == 'violet') || (color == 'green')}">
    <script>
        document.write( '<style class="hideStuff" ' + 'type="text/css">body {display:none;}<\/style>');
    </script>
</c:if>

<script type="text/javascript">
    codedItemAutoUpdate();
</script>

<script type="text/javascript">
    $('html').click(function (e) {
        if($(e.target).closest('#pageNavPosition').length === 0) {
            if($(e.target).closest('#tablepaging_result').filter(function () {return $(this).siblings("#response").size() === 1; }).length === 0) {
                $('#tablepaging_result').filter(function () {
                    return $(this).siblings("#response").size() === 1; }).parent("div").html('');
            }
        }
    });
</script>

<script type="text/javascript">
    function redirectUrl(buttonType) {

        var url = $("#codedItemUrl").val();

        if (url.indexOf("codedItems_f_status") > 0) {

            url = url.replace(/(codedItems_f_status=)[^\&]+/, '$1' + buttonType);
        } else {

            url = url + "&codedItems_f_status=" + buttonType;
        }

        window.location.replace(${pageContext.request.contextPath}"/pages/codedItems?" + url);
    }
</script>
<input type="hidden" id="codedItemUrl" value='<c:out value="${codedItemUrl}"/>'/>

<tr id="sidebar_Instructions_open">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
        <b>
        	<fmt:message key="instructions" bundle="${restext}"/>
        </b>
        <div class="sidebar_tab_content">
            <fmt:message key="design_implement_coding" bundle="${restext}"/>
        </div>
    </td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: none">
    <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
        	<img src="../images/sidebar_expand.gif" border="0" align="right" hspace="10">
        </a>
        <b>
        	<fmt:message key="instructions" bundle="${restext}"/>
        </b>
    </td>
</tr>
<jsp:include page="include/sideInfo.jsp"/>
<link rel="stylesheet" href="../includes/jmesa/jmesa.css" type="text/css">
<link rel="stylesheet" href="../includes/jquery-ui.css"  type="text/css"/>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="../includes/jmesa/jquery-ui.min.js"></script>

<script type="text/javascript">

    function onInvokeAction(id) {
        createHiddenInputFieldsForLimitAndSubmit(id);
    }

    var currentURL = "${requestScope['javax.servlet.forward.query_string']}"

    // Conceal auto code button on completed page
    $(document).ready(function() {

    	if (/Completed$/.test(currentURL)) {

    		$("input[name='autoCode']").hide();
    	}
    })

</script>

<h1>
	<span class="first_level_header">
		<fmt:message key="medical_coding" bundle="${resword}"/>
    	<a href="javascript:openDocWindow('../help/3_1_SDV_Help.html')">
        	<img src="../images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>">
    	</a>
	</span>
</h1>

<div>
	<a id="sumBoxParent" href="javascript:void(0)"
        onclick="showSummaryBox('sumBox',document.getElementById('sumBoxParent'),
        '<fmt:message key="show_summary_statistics" bundle="${resword}"/>',
        '<fmt:message key="hide_summary_statistics" bundle="${resword}"/>')">
    	<img name="ExpandGroup1" src="../images/bt_Collapse.gif" border="0">
    	<fmt:message key="hide_summary_statistics" bundle="${resword}"/>
	</a>
</div>

<div id="sumBox" style="display:block; width:600px;">
	<table cellspacing="0" class="summaryTable" style="width:600px;">
        <tr>
        	<td>&nbsp;</td>
        	<td align="center"><fmt:message key="notCoded" bundle="${resword}"/></td>
        	<c:if test="${mcApprovalNeeded}">
        		<td align="center"><fmt:message key="notApproved" bundle="${resword}"/></td>
        	</c:if>
        	<c:choose>
        		<c:when test="${codeNotFoundItems > 0}">
        			<td id="tdCodeNotFound" align="center"><fmt:message key="codeNotFound" bundle="${resword}"/></td>
        		</c:when>
        		<c:otherwise>
        			<td id="tdCodeNotFound" align="center" style="display:none"><fmt:message key="codeNotFound" bundle="${resword}"/></td>
        		</c:otherwise>
        	</c:choose>
        	<td align="center"><fmt:message key="completed" bundle="${resword}"/></td>
        </tr>
        <tr>
        	<td align="center">Medical Terms</td>
        	<td align="center" name="tdNotCoded"><a href='javascript:redirectUrl("Not Coded");'>${unCodedItems}</a></td>
        	<c:if test="${mcApprovalNeeded}">
            	<td align="center">0</td>
            </c:if>
            <c:choose>
        		<c:when test="${codeNotFoundItems gt 0}">
        			<td align="center" name="tdCodeNotFound"><a href='javascript:redirectUrl("Code not Found");'>${codeNotFoundItems}</a></td>
        		</c:when>
        		<c:otherwise>
        			<td align="center" name="tdCodeNotFound" style="display:none"><a href='javascript:redirectUrl("Code not Found");'>${codeNotFoundItems}</a></td>
        		</c:otherwise>
        	</c:choose>
        	<td align="center" name="tdCoded"><a href='javascript:redirectUrl("Coded"); showUncodedItems();'>${codedItems}</a></td>
        </tr>
    </table> 
</div>

<form action="${pageContext.request.contextPath}/pages/codedItems" style="clear:left; float:left;">
    ${codedItemsTable}
    <input type="hidden" name="study" value="${studyId}">
</form>

<div style="clear:left; float:left">
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/> 
</div>

<div style="clear:right; float:left">
	<input type="button" name="autoCode" value="<fmt:message key="autoCode" bundle="${resword}" />" class="button_medium" onClick="autoCode()"/> 
</div>

<div style="clear:left">
	<br><br>
	<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
		<tr>
			<td id="sidebar_Workflow_closed" style="display: none">
				<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');">
					<img src="../images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif" border="0">
				</a>
			</td>
			<td id="sidebar_Workflow_open">
				<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
					<tr>
						<td class="workflowBox_T" valign="top">
							<table border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td class="workflow_tab">
										<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');">
											<img src="../images/sidebar_collapse.gif" border="0" align="right" hspace="10">
										</a>
										<b>
											<fmt:message key="workflow" bundle="${resword}"/>
										</b>
									</td>
								</tr>
							</table>
						</td>
						<td class="workflowBox_T" align="right" valign="top">
							<img src="../images/workflowBox_TR.gif">
						</td>
					</tr>
					<tr>
						<td colspan="2" class="workflowbox_B">
							<div class="box_R">
								<div class="box_B">
									<div class="box_BR">
										<div class="workflowBox_center">
											<table border="0" cellpadding="0" cellspacing="0">
												<tr>
													<td>
														<div class="box_T">
															<div class="box_L">
																<div class="box_R">
																	<div class="box_B">
																		<div class="box_TL">
																			<div class="box_TR">
																				<div class="box_BL">
																					<div class="box_BR">
																						<div class="textbox_center" align="center">
																							<span class="title_manage">
										                            							<fmt:message key="manage_study" bundle="${resword}"/>
																							</span>
																						</div>
																					</div>
																				</div>
																			</div>
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</td>
													<td>
														<img src="../images/arrow.gif">
													</td>
													<td>
														<div class="box_T">
															<div class="box_L">
																<div class="box_R">
																	<div class="box_B">
																		<div class="box_TL">
																			<div class="box_TR">
																				<div class="box_BL">
																					<div class="box_BR">
																						<div class="textbox_center" align="center">
												                             				<span class="title_manage">
																								<b>
																									<fmt:message key="medical_coding" bundle="${resword}"/>
																								</b>
																							</span>
																						</div>
																					</div>
																				</div>
																			</div>
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</td>
												</tr>
											</table>
										</div>
									</div>
								</div>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>

<jsp:include page="include/footer.jsp"/>
