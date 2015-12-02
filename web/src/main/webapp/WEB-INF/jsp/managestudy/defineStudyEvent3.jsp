<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="resnotes"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterms"/>


<jsp:include page="../include/managestudy-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

        <div class="sidebar_tab_content">

        </div>

    </td>

</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
    <td class="sidebar_tab">

        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

        <b><fmt:message key="instructions" bundle="${resword}"/></b>

    </td>
</tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='definition' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>
<h1>
	<span class="first_level_header">
		<fmt:message key="define_study_event"  bundle="${resword}"/> - <fmt:message key="selected_CRFs"  bundle="${resword}"/> - <fmt:message key="selected_default_version"  bundle="${resword}"/>
	</span>
</h1>

<form action="DefineStudyEvent" method="post" id="defineStudyEventForm">
    <input type="hidden" name="formWithStateFlag" id="formWithStateFlag" value="${formWithStateFlag != null ? formWithStateFlag : ''}" />
    <input type="hidden" name="actionName" value="confirm">
    
    <div style="width: 600px">
        <!-- These DIVs define shaded box borders -->
        <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

            <div class="tablebox_center">
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <c:set var="count" value="0"/>
                    <c:forEach var ="crf" items="${definition.crfs}">
                        <c:set value="${crfNameToEdcMap[crf.name]}" var="edc"/>
                        <input type="hidden" name="crfId<c:out value="${count}"/>" value="<c:out value="${crf.id}"/>">
                        <input type="hidden" name="crfName<c:out value="${count}"/>" value="<c:out value="${crf.name}"/>">

                        <tr valign="top" bgcolor="#F5F5F5">
                            <td class="table_header_column" colspan="4"><c:out value="${crf.name}"/></td>
                        </tr>

                        <tr valign="top">

                            <td class="table_cell">
                            	<fmt:message key="required" bundle="${resword}"/>:
                            	<input type="checkbox" onchange="javascript:changeIcon();" ${edc.requiredCRF ? 'checked' : ''} name="requiredCRF<c:out value="${count}"/>" value="yes">
                            </td>

                            <td class="table_cell">&nbsp;</td>

                            <td class="table_cell">
                            	<fmt:message key="password_required" bundle="${resword}"/>:
                            	<input type="checkbox" onchange="javascript:changeIcon();" ${edc.electronicSignature ? 'checked' : ''} name="electronicSignature<c:out value="${count}"/>" value="yes">
                            </td>

                            <td class="table_cell"><fmt:message key="default_version" bundle="${resword}"/>:
                                <select name="defaultVersionId<c:out value="${count}"/>" onchange="javascript:changeIcon();">
                                    <c:forEach var="version" items="${crf.versions}">
                                        <option ${edc.defaultVersionName == version.name ? 'selected' : ''} value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/>
                                    </c:forEach>
                                </select>
                            </td></tr>
                        <tr valign="top">
                            <td class="table_cell" colspan="2">
                            	<fmt:message key="hidden_crf" bundle="${resword}"/>:<input type="checkbox" ${edc.hideCrf ? 'checked' : ''} name="hiddenCrf<c:out value="${count}"/>" onchange="javascript:changeIcon();" value="yes">
                            </td>
                    		
                            <td class="table_cell" colspan="2"><fmt:message key="sdv_option" bundle="${resword}"/>:
							    <select name="sdvOption<c:out value="${count}"/>" onchange="javascript:changeIcon();">
                                    <c:forEach var="sdv" items="${crf.sdvOptions}">
                                        <option ${edc.sourceDataVerification.code == sdv.code ? 'selected' : ''} value="${sdv.code}"><fmt:message key="${sdv.description}" bundle="${resterms}"/></option>
                                    </c:forEach>
					        	</select>
							    </td>
                        </tr>

                        <tr valign="top">
                            <td class="table_cell" colspan="4">
                                <fmt:message key="acceptNewCrfVersions" bundle="${resword}"/> :
                                <input ${edc.acceptNewCrfVersions ? 'checked' : ''} type="checkbox" name="acceptNewCrfVersions<c:out value="${count}"/>" value="yes">
                            </td>
                        </tr>

                        <tr valign="top">
                            <td class="table_cell" colspan="4">
                                <fmt:message key="data_entry_quality" bundle="${resword}"/>:
                                <c:set var="deQualityDE" value=""/>
                                <c:set var="deQualityEvaluatedCRF" value=""/>
                                <c:choose>
                                    <c:when test="${edc.doubleEntry == true}">
                                        <c:set var="deQualityDE" value="checked"/>
                                    </c:when>
                                    <c:when test="${edc.evaluatedCRF == true}">
                                        <c:set var="deQualityEvaluatedCRF" value="checked"/>
                                    </c:when>
                                </c:choose>

								<c:choose>
									<c:when test="${study.studyParameterConfig.studyEvaluator == 'yes'}">
										<input type="radio" name="deQuality${count}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="dde" class="email_field_trigger uncheckable_radio" ${deQualityDE}/>
										<fmt:message key="double_data_entry" bundle="${resword}"/>

										<input type="radio" name="deQuality${count}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="evaluation" class="email_field_trigger uncheckable_radio" ${deQualityEvaluatedCRF}/>
										<fmt:message key="crf_data_evaluation" bundle="${resword}"/>
									</c:when>
									<c:otherwise>
										<input type="checkbox" name="deQuality${count}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="dde" class="email_field_trigger uncheckable_radio" ${deQualityDE}/>
										<fmt:message key="double_data_entry" bundle="${resword}"/>
									</c:otherwise>
								</c:choose>
                            </td>
                        </tr>

						<tr valign="top">
							<td class="table_cell" colspan="2" style="padding-bottom:9px;">
								<fmt:message key="send_email_when" bundle="${resword}"/>:
								<input type="radio" ${edc.emailStep == 'complete' ? 'checked' : ''} name="emailOnStep<c:out value="${count}"/>" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="complete" class="email_field_trigger uncheckable_radio">
								<fmt:message key="complete" bundle="${resterms}"/>
								<input type="radio" ${edc.emailStep == 'sign' ? 'checked' : ''} name="emailOnStep<c:out value="${count}"/>" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="sign" class="email_field_trigger uncheckable_radio">
								<fmt:message key="sign" bundle="${resterms}"/>
							</td>
							<td class="table_cell" colspan="2">
								<span class="email_wrapper" style="${edc.emailStep == 'complete' || edc.emailStep == 'sign' ? '' : 'display:none'}">
									<fmt:message key="email_crf_to" bundle="${resword}"/>:
									<input type="text" name="mailTo${count}" onchange="javascript:changeIcon();" style="width:115px;margin-left:79px" class="email_to_check_field" value="${edc.emailTo}"/>
								</span>
								<span class="alert" style="display:none"><fmt:message key="enter_valid_email" bundle="${resnotes}"/></span>
							</td>
						</tr>

                        <tr>
                            <td class="table_cell" colspan="4">
                                <fmt:message key="crfTabbingMode" bundle="${resword}"/>:
                                <c:set var="leftToRightTabbingMode" value="checked"/>
                                <c:set var="topToBottomTabbingMode" value=""/>
                                <c:if test='${edc.tabbingMode == "topToBottom"}'>
                                    <c:set var="leftToRightTabbingMode" value=""/>
                                    <c:set var="topToBottomTabbingMode" value="checked"/>
                                </c:if>
                                <input type="radio" name="tabbingMode${count}" onchange="javascript:changeIcon();" value="leftToRight" ${leftToRightTabbingMode}/> <fmt:message key="leftToRight" bundle="${resword}"/>
                                <input type="radio" name="tabbingMode${count}" onchange="javascript:changeIcon();" value="topToBottom" ${topToBottomTabbingMode}/> <fmt:message key="topToBottom" bundle="${resword}"/>
                            </td>
                        </tr>

                 
                        <c:set var="count" value="${count+1}"/>
                        <tr><td class="table_divider" colspan="4">&nbsp;</td></tr>
                    </c:forEach>

                </table>
            </div>
        </div></div></div></div></div></div></div></div>
    </div>

	<table border="0">
		<tr>
			<td>
				<input type="button" name="BTN_Back" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" onClick="$('input[name=actionName]').val('back1');$('#defineStudyEventForm').submit();"/>
			</td>
			<td>
				<input type="button" name="Submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium medium_continue"
					   onClick="javascript:validateCustomFields({expectedValues: ['email'], selectors: ['.email_to_check_field'], formToSubmit: '#defineStudyEventForm'});">
			</td>
			<td>
				<input type="button" name="Cancel" id="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium medium_cancel" onClick="formWithStateGoBackSmart('<fmt:message key="sure_to_cancel" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
			</td>
			<td>
				<img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom"/>
			</td>
		</tr>
	</table>
</form>
<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
    <tr>
        <td id="sidebar_Workflow_closed" style="display: none">
            <a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif" border="0"></a>
        </td>
        <td id="sidebar_Workflow_open" style="display: all">
            <table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
                <tr>
                    <td class="workflowBox_T" valign="top">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr>
                                <td class="workflow_tab">
                                    <a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
                                    <b><fmt:message key="workflow" bundle="${resword}"/></b>
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
                </tr>
                <tr>
                    <td colspan="2" class="workflowbox_B">
                        <div class="box_R"><div class="box_B"><div class="box_BR">
                            <div class="workflowBox_center">
                                
                                <!-- Workflow items -->
                                <table border="0" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td>
                                            <!-- These DIVs define shaded box borders -->
                                            <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
                                                <div class="textbox_center" align="center">
													<span class="title_manage">
														<fmt:message key="enter_definition_name_and_description" bundle="${resword}"/><br><br>
													</span>
                                                </div>
                                   			</div></div></div></div></div></div></div></div>
		                                </td>
		                                <td><img src="images/arrow.gif"></td>
		                                <td>
	                                    	<!-- These DIVs define shaded box borders -->
	                                        <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
                                                <div class="textbox_center" align="center">
													<span class="title_manage">
										            	<fmt:message key="add_CRFs_to_definition" bundle="${resword}"/><br><br>
													</span>
                                                </div>
                                            </div></div></div></div></div></div></div></div>
                                        </td>
                                        <td><img src="images/arrow.gif"></td>
                                        <td>
                                            <!-- These DIVs define shaded box borders -->
                                            <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
                                                <div class="textbox_center" align="center">
													<span class="title_manage">
										            	<b><fmt:message key="edit_properties_for_each_CRF" bundle="${resword}"/><br><br></b>
													</span>
                                                </div>
                                            </div></div></div></div></div></div></div></div>
                                        </td>
                                        <td><img src="images/arrow.gif"></td>
                                        <td>
                                            <!-- These DIVs define shaded box borders -->
                                            <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
                                                <div class="textbox_center" align="center">
													<span class="title_manage">
										            	<fmt:message key="confirm_and_submit_definition" bundle="${resword}"/><br><br>
													</span>
                                                </div>
                                            </div></div></div></div></div></div></div></div>
                                        </td>
                                    </tr>
                                </table>
                                <!-- end Workflow items -->

                            </div>
                        </div></div></div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
