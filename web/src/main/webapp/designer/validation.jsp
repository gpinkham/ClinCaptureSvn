<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<!DOCTYPE html>
<html>
  <head>
  
    <title>Rule Designer</title>
  
    <meta charset="utf-8">
    <meta http-equiv='expires' content='0'>
    <meta http-equiv='pragma' content='no-cache'>
    <meta http-equiv='cache-control' content='no-cache'>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="shortcut icon" href="https://www.clinovo.com/favicon.ico"> 

    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/designer.css" rel="stylesheet" media="screen">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="deps/json2/json2.js"></script>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <script src="js/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/bootbox.js"></script>
    
    <script src="js/designer.js"></script>
    <script src="js/validation.js"></script>    
  </head>
  <body>
    <div class="container">
      <div class="row">
        <div class="col-md-offset-3">
          <legend><fmt:message bundle="${resword}" key="rs.validation.result"/></legend>
          <div class="alert alert-success success"><fmt:message bundle="${resword}" key="rs.validation.valid"/></div>
          <div class="alert alert-danger failure"><fmt:message bundle="${resword}" key="rs.validation.invalid"/></div>
          <legend id="failure"><fmt:message bundle="${resword}" key="rs.validation.failure"/></legend>
          <div class="alert alert-info"></div>
          <legend class="success"><fmt:message bundle="${resword}" key="rs.validation.items"/></legend>
          <ul id="items" class="list-group success"></ul>
          <legend class="success"><fmt:message bundle="${resword}" key="rs.validation.execution"/></legend>
          <ul id="executions" class="list-group success"></ul>
          <legend class="success"><fmt:message bundle="${resword}" key="rs.validation.action"/></legend>
          <ul id="action" class="list-group success"></ul>
        </div>
      </div>
      <div class="row">
        <div class="col-md-12">
          <nav class="navbar navbar-default nav-cont navbar-fixed-bottom" role="navigation">
            <ul class="nav nav-justified">
              <li>
	              <a id="back" href="" class="btn btn-primary navbar-btn">
		              <fmt:message bundle="${resword}" key="back"/>
	              </a>
              </li>
              <li>
	              <a id="save" class="btn btn-primary navbar-btn">
		              <fmt:message bundle="${resword}" key="save"/>
	              </a>
              </li>
              <li>
	              <a id="exit" href="" class="btn btn-warning navbar-btn">
		              <fmt:message bundle="${resword}" key="exit"/>
	              </a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </body>
</html>
