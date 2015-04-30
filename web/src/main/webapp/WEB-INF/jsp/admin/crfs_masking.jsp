<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="resmessages"/>

<jsp:include page="../include/managestudy_top_pages.jsp" />

<jsp:include page="../include/sideAlert.jsp"/>
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
<jsp:include page="../include/sideInfo.jsp"/>

<h1>
    <span class="first_level_header" style="line-height:5px;">
        <fmt:message key="crfs_masking_for" bundle="${resword}"/>: ${user.name}
    </span>
</h1>

<br>
<form action="CRFsMasking" method="POST">
	<!-- Inputs to save values that were entered on the previous page -->
	<input type="hidden" name="userId" value="${userId}"/>
	<input type="hidden" name="firstName" value="${firstName}"/>
	<input type="hidden" name="lastName" value="${lastName}"/>
	<input type="hidden" name="email" value="${email}"/>
	<input type="hidden" name="phone" value="${phone}"/>
	<input type="hidden" name="institutionalAffiliation" value="${institutionalAffiliation}"/>
	<input type="hidden" name="userType" value="${userType}"/>

	<c:forEach var="sites" items="${sitesByStudies}" varStatus="studyNum">
		<div class="table_title_Manage">
			<a href="javascript:leftnavExpand('study${studyNum.count}');">
				<img id="excl_siteProperties" src="../images/bt_Collapse.gif" border="0" /> <fmt:message bundle="${resword}" key="study" />: ${sites.key}
				<c:if test="${not empty rolesByStudy[sites.key]}">
					; <fmt:message bundle="${resword}" key="role"/>: ${rolesByStudy[sites.key].role.name}
				</c:if>
			</a>
		</div>
		<div id="study${studyNum.count}">
			<c:forEach var="site" items="${sites.value}" varStatus="siteNum">
				<div style="padding-left:20px;">
					<div class="table_title_Manage">
						<a href="javascript:leftnavExpand('site${site.id}');">
							<img id="excl_site${site.id}" src="../images/bt_Collapse.gif" border="0"/>
							<fmt:message bundle="${resword}" key="site" />: <b>${site.name}</b>
							<c:if test="${not empty rolesBySite[site.id]}">
								; <fmt:message bundle="${resword}" key="role"/>: ${rolesBySite[site.id].role.name}
							</c:if>
						</a>
					</div>
					<div id="site${site.id}">
						<!-- Events block start -->
						<c:choose>
							<c:when test="${not empty rolesBySite[site.id] && rolesBySite[site.id].role.code == 'investigator'}">
								<div style="padding:20px">
									<fmt:message bundle="${resword}" key="masking_disabled_for_investigator"/>
								</div>
							</c:when>
							<c:otherwise>
								<c:forEach var="event" items="${eventsByStudies[site.parentStudyId]}" varStatus="eventNum">
									<div style="padding-left:20px">
										<a href="javascript:leftnavExpand('e${eventNum.count}s${site.id}');">
											<img id="excl_e${eventNum.count}s${site.id}" src="../images/bt_Expand.gif" border="0"/>
											<b>${event.name}</b>
										</a>
										<div id="e${eventNum.count}s${site.id}">
											<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
												<div class="tablebox_center">
													<table width="100%" class="table_horizontal">
														<tr style="background-color:#F5F5F5">
															<td style="min-width:500px"><fmt:message bundle="${resword}" key="CRF_name"/></td>
															<td style="width: 100px"><fmt:message bundle="${resword}" key="mask_this_crf"/></td>
														</tr>
														<c:set var="crfsId" value="S${site.id}_E${event.id}"/>
														<c:forEach var="crf" items="${crfsByEvents[crfsId]}" varStatus="crfNum">
															<tr>
																<td>${crf.name}</td>
																<td>
																	<c:set value="" var="masked" />
																	<c:set value="checked" var="notMasked" />
																	<c:set value="C${crf.id}" var="maskKey"/>
																	<c:if test="${not empty maskedCRFs[maskKey]}">
																		<c:set value="" var="notMasked" />
																		<c:set value="checked" var="masked" />
																	</c:if>
																	<input type="radio" ${masked} value="masked" name="crf_mask_c${crf.id}_e${event.id}_s${site.id}"> <fmt:message bundle="${resword}" key="yes"/>
																	<input type="radio" ${notMasked} value="notMasked" name="crf_mask_c${crf.id}_e${event.id}_s${site.id}"> <fmt:message bundle="${resword}" key="no"/>
																</td>
															</tr>
														</c:forEach>
													</table>
												</div>
											</div></div></div></div></div></div></div></div>
										</div>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
						<!-- Events block end -->
					</div>
				</div>
			</c:forEach>
		</div>
	</c:forEach>
	<table style="margin-top:40px">
		<tr>
			<td>
				<input type="button" name="back" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium" onclick="history.go(-1);"/>
			</td>
			<td>
				<input type="submit" name="submit_and_restore" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium"/>
			</td>
		</tr>
	</table>
</form>
<br>

<jsp:include page="../include/footer.jsp"/>
