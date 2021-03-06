<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='isAdminServlet' class='java.lang.String' />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" />
	
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />

	<title><fmt:message key="openclinica" bundle="${resword}"/></title>

	<link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css"/>
	<link rel="stylesheet" href="<c:out value="${contextPath}" />/includes/css/charts.css?r=${revisionNumber}" type="text/css"/>
	<link rel="stylesheet" href="includes/jquery-ui.css?r=${revisionNumber}" type="text/css"/>
	<link rel="icon" href="<c:url value='${faviconUrl}'/>" />
	<link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>" />

	<script type="text/javascript" src="includes/jmesa/jsapi"></script>
	<script type="text/javascript">
		google.load('visualization', '1.0', {
			'packages' : [ 'corechart' ]
		});
	</script>
	<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-ui.min.js"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/Tabs.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/js/widgets/Charts.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/repetition-model/repetition-model.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/prototype.js?r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/scriptaculous.js?load=effects&r=${revisionNumber}"></script>
	<script type="text/JavaScript" language="JavaScript" src="includes/effects.js?r=${revisionNumber}"></script>
    <!-- Added for the new Calender -->

    <ui:calendar/>
	<!-- End -->
    <ui:theme/>

    <script language="JavaScript">
        function reportBug() {
            var bugtrack = "https://www.openclinica.com/ClinCapture/bug.php?version=<fmt:message key="version_number" bundle="${resword}"/>&user=";
            var user= "<c:out value="${userBean.name}"/>";
            bugtrack = bugtrack + user+ "&url=" + window.location.href;
            openDocWindow(bugtrack);
        }
        function pageWidth() {return window.innerWidth != null? window.innerWidth: document.documentElement && document.documentElement.clientWidth ? document.documentElement.clientWidth:document.body != null? document.body.clientWidth:null;}
        function pageHeight() {return window.innerHeight != null? window.innerHeight: document.documentElement && document.documentElement.clientHeight ? document.documentElement.clientHeight:document.body != null? document.body.clientHeight:null;}
        function posLeft() {return typeof window.pageXOffset != 'undefined' ? window.pageXOffset:document.documentElement && document.documentElement.scrollLeft? document.documentElement.scrollLeft:document.body.scrollLeft? document.body.scrollLeft:0;}
        function posTop() {return typeof window.pageYOffset != 'undefined' ? window.pageYOffset:document.documentElement && document.documentElement.scrollTop? document.documentElement.scrollTop: document.body.scrollTop?document.body.scrollTop:0;}
        function scrollFix(){var obol=document.getElementById('ol');obol.style.top=posTop()+'px';obol.style.left=posLeft()+'px'}
        function sizeFix(){var obol=document.getElementById('ol');obol.style.height=pageHeight()+'px';obol.style.width=pageWidth()+'px';}
        function kp(e){ky=e?e.which:event.keyCode;if(ky==88||ky==120)hm();return false}
        function inf(h){tag=document.getElementsByTagName('select');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;tag=document.getElementsByTagName('iframe');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;tag=document.getElementsByTagName('object');for(i=tag.length-1;i>=0;i--)tag[i].style.visibility=h;}
        function sm(obl, wd, ht){var h='hidden';var b='block';var p='px';var obol=document.getElementById('ol'); var obbxd = document.getElementById('mbd');obbxd.innerHTML = $("#" + obl)[0].innerHTML;obol.style.height=pageHeight()+p;obol.style.width=pageWidth()+p;obol.style.top=posTop()+p;obol.style.left=posLeft()+p;obol.style.display=b;var tp=posTop()+((pageHeight()-ht)/2)-12;var lt=posLeft()+((pageWidth()-wd)/2)-12;var obbx=document.getElementById('mbox');obbx.style.top=(tp<0?0:tp)+p;obbx.style.left=(lt<0?0:lt)+p;obbx.style.width=wd+p;obbx.style.height=ht+p;inf(h);obbx.style.display=b;return false;}
        function hm(){var v='visible';var n='none';document.getElementById('ol').style.display=n;document.getElementById('mbox').style.display=n;inf(v);document.onkeypress=''}

        function initmb(){var ab='absolute';var n='none';var obody=document.getElementsByTagName('body')[0];var frag=document.createDocumentFragment();var obol=document.createElement('div');obol.setAttribute('id','ol');obol.style.display=n;obol.style.position=ab;obol.style.top=0;obol.style.left=0;obol.style.zIndex=998;obol.style.width='100%';frag.appendChild(obol);var obbx=document.createElement('div');obbx.setAttribute('id','mbox');obbx.style.display=n;obbx.style.position=ab;obbx.style.zIndex=999;var obl=document.createElement('span');obbx.appendChild(obl);var obbxd=document.createElement('div');obbxd.setAttribute('id','mbd');obl.appendChild(obbxd);frag.insertBefore(obbx,obol.nextSibling);obody.insertBefore(frag,obody.firstChild);
            window.onscroll = scrollFix; window.onresize = sizeFix;
        }
    </script>
</head>

<body class="main_BG" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0"
    <c:if test="${(study.status.locked || study.status.frozen)}">
        <c:if test="${userBean.numVisitsToMainMenu<=1 || studyJustChanged=='yes'}">
            onload="/*initmb();sm('box', 730,100);*/"
         </c:if>
      </c:if>
    <jsp:include page="../include/showPopUp.jsp"/>
>

<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="background">
    <tr>
        <td valign="top">
<!-- Header Table -->
<script language="JavaScript">
	var StatusBoxValue=1;
</script>

            <table id="headerInnerTable2" border="0" cellpadding="0" cellspacing="0" class="header">
            <tr>
                <td valign="top">

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

    <div class="logo"><img src="<c:url value='${systemLogoUrl}'/>"></div>

<!-- Main Navigation -->

    <jsp:include page="../include/navBar.jsp"/>
<!-- End Main Navigation -->

