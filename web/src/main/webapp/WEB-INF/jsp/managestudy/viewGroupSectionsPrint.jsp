<%--
  The Print CRF JSP.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="com.akazaresearch.tags" prefix="aka_frm" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<html>
<head><title>Print CRF</title>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=8" />
  <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
  <script type="text/JavaScript" language="JavaScript" src=
    "includes/global_functions_javascript.js"></script>
  <script type="text/javascript"  language="JavaScript" src=
    "includes/repetition-model/repetition-model.js"></script>
  <link rel="stylesheet" href="includes/styles.css" type="text/css">
  <link rel="stylesheet" href="includes/print_crf.css" type="text/css">
    <link rel="icon" href="<c:url value='/images/favicon.ico'/>" />
    <link rel="shortcut icon" href="<c:url value='/images/favicon.ico'/>" />
</head>
<!-- Clinovo ticket #134 start -->
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js"></script>
<script type="text/JavaScript">
$(document).ready(function() {
$('.tableDiv').find('span').each(function() {
    var $this = $(this);
	var t = $this.text();
    $this.html(t.replace('&lt;','<').replace('&gt;', '>'));
});
$('.toplevel').find('.aka_headerBackground.aka_padding_large.aka_cellBorders.aka_font_general').each(function() {
    var $this = $(this);
	var t = $this.text();
    $this.html(t.replace('&lt;','<').replace('&gt;', '>'));
});
//Dynamic cell formating
$('.tableDiv').find('.aka_text_block_shared').css('height','100%').css('float','left').css('display','table');
$(".aka_form_table").attr('class', 'tableDiv');

//Block all inputs and textarea on print page
var elsarea = document.getElementsByTagName ('textarea');
for ( var i = 0; i < elsarea.length ; i ++ ) {
	elsarea[i].setAttribute('disabled', 'true'); 
}
var els = document.getElementsByTagName ('input');
for ( var i = 0; i < els.length ; i ++ ) {
 if ( els[i].type == 'text' || els[i].type == 'radio' || els[i].type == 'checkbox') els[i].setAttribute('disabled', 'true'); 
}
 });
</script>
<!-- end -->
<jsp:useBean scope="request" id="crfVersionBean" class="org.akaza.openclinica.bean.submit.CRFVersionBean" />
<jsp:useBean scope="request" id="crfBean" class="org.akaza.openclinica.bean.admin.CRFBean" />
<jsp:useBean scope="session" id="studyEvent" class="org.akaza.openclinica.bean.managestudy.StudyEventBean" />
<%-- dataInvolved is a request attribute set in PrintCRFServlet and PrintDataEntryServlet --%>
<c:set var="dataIsInvolved" value="${dataInvolved}" />
<body>
<div class="headerDiv">
<%-- This section was cut-and-pasted from the existing print JSP --%>
<h1><span class="title_manage">
  <c:out value="${crfBean.name}" /> <c:out value="${crfVersionBean.name}" /></span>
  <c:if test="${studySubject != null && studySubject.id>0}">
    <c:choose>
      <c:when test="${EventCRFBean.stage.initialDE}">
        <img src="images/icon_InitialDE.gif" alt="<fmt:message key="initial_data_entry" bundle="${resword}"/>" title="<fmt:message key="initial_data_entry" bundle="${resword}"/>">
      </c:when>
      <c:when test="${EventCRFBean.stage.initialDE_Complete}">
        <img src="images/icon_InitialDEcomplete.gif" alt="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>" title="<fmt:message key="initial_data_entry_complete" bundle="${resword}"/>">
      </c:when>
      <c:when test="${EventCRFBean.stage.doubleDE}">
        <img src="images/icon_DDE.gif" alt="<fmt:message key="double_data_entry" bundle="${resword}"/>" title="<fmt:message key="double_data_entry" bundle="${resword}"/>">
      </c:when>
      <c:when test="${EventCRFBean.stage.doubleDE_Complete}">
          <c:choose>
              <c:when test="${studyEvent.subjectEventStatus.signed}">
                  <img src="images/icon_Signed.gif" alt="<fmt:message key="subjectEventSigned" bundle="${resword}"/>" title="<fmt:message key="subjectEventSigned" bundle="${resword}"/>">
              </c:when>
              <c:when test="${EventCRFBean.sdvStatus}">
                  <img src="images/icon_DoubleCheck.gif" alt="<fmt:message key="sourceDataVerified" bundle="${resword}"/>" title="<fmt:message key="sourceDataVerified" bundle="${resword}"/>">
              </c:when>
              <c:otherwise>
                  <img src="images/icon_DEcomplete.gif" alt="<fmt:message key="data_entry_complete" bundle="${resword}"/>" title="<fmt:message key="data_entry_complete" bundle="${resword}"/>">
              </c:otherwise>
          </c:choose>
      </c:when>
      <c:when test="${EventCRFBean.stage.admin_Editing}">
        <img src="images/icon_AdminEdit.gif" alt="<fmt:message key="administrative_editing" bundle="${resword}"/>" title="<fmt:message key="administrative_editing" bundle="${resword}"/>">
      </c:when>
      <c:when test="${EventCRFBean.stage.locked}">
        <img src="images/icon_Locked.gif" alt="<fmt:message key="locked" bundle="${resword}"/>" title="<fmt:message key="locked" bundle="${resword}"/>">
      </c:when>
      <c:otherwise>
        <img src="images/icon_Invalid.gif" alt="<fmt:message key="invalid" bundle="${resword}"/>" title="<fmt:message key="invalid" bundle="${resword}"/>">
      </c:otherwise>
    </c:choose>
  </c:if>
  </h1>
  <jsp:include page="printSubmit.jsp"/>
  </div>
<%-- Begin new group CRF print section. the 'dataIsInvolved' variable
 is a request attribute, true or false, indicating whether the printed CRF is
 associated with an event and data --%>

  <aka_frm:print_tabletag><c:out value="${dataIsInvolved}"/></aka_frm:print_tabletag>

</body>
<jsp:include page="../include/changeTheme.jsp"/>
</html>