<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<html>
<head>
	<title>Rules Studio</title>
	<meta charset="utf-8">
	<meta http-equiv='expires' content='0'>
	<meta http-equiv='pragma' content='no-cache'>
	<meta http-equiv='cache-control' content='no-cache'>
	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="shortcut icon" href="https://www.clinovo.com/favicon.ico">
	<!-- Bootstrap -->
	<link href="css/jquery-ui-1.10.3.min.css" rel="stylesheet" media="screen">
	<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link href="css/datepicker.css" rel="stylesheet" media="screen">
	<link href="css/designer.css" rel="stylesheet" media="screen">
	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	<script src="js/json2.js"></script>
	<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
	<script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
	<![endif]-->
	<script src="js/jquery.min.js"></script>
	<script src="js/jquery-ui-1.10.3.min.js"></script>
	<script src="js/date.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/bootbox.js"></script>
	<script src="js/bootstrap-datepicker.js"></script>
	<script src="js/parser.js"></script>
	<script src="js/designer.js"></script>
	<script src="js/interaction.js"></script>
	<script src="js/messages.js"></script>
</head>
<body>
<div class="container">
<div>
	<img src="images/rs-logo.png">
	<a href='https://www.youtube.com/watch?v=d3ZyHxf2FQ0' target=',_blank'><img class="img col-md-offset-12" src="images/brand2.png"></a>
</div>
<div class="row">
	<div class="col-md-12 pull-left">
		<div class="col-md-2 z drag-container parenthesis">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="panel-title center-text"><fmt:message bundle="${resword}" key="group"/></h3>
				</div>
				<div class="panel-body">
					<p class="btn group leftPAREN">&#40;</p>
					<p class="btn group rightPAREN">&#41;</p>
				</div>
			</div>
		</div>
		<div class="col-md-4 z drag-container compare">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="panel-title center-text"><fmt:message bundle="${resword}" key="compare"/></h3>
				</div>
				<div class="panel-body">
					<p class="btn comp compare lt">&lt;</p>
					<p class="btn comp compare gt">&gt;</p>
					<p class="btn comp compare lte" >&#8804;</p>
					<p class="btn comp compare gte" >&#8805;</p>
					<p class="btn comp compare eq">&#61;</p>
					<p class="btn comp compare neq">&#8800;</p>
					<p class="btn comp compare nct" >&#8713;</p>
					<p class="btn comp compare ct" >&isin;</p>
				</div>
			</div>
		</div>
		<div class="col-md-3 z compare">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="panel-title center-text"><fmt:message bundle="${resword}" key="calculate"/></h3>
				</div>
				<div class="panel-body">
					<p class="btn comp plus compare">&#43;</p>
					<p class="btn comp minus compare">&#45;</p>
					<p class="btn comp mult compare">&times;</p>
					<p class="btn comp divide compare">&divide;</p>
				</div>
			</div>
		</div>
		<div class="col-md-3 z condition">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="panel-title center-text"><fmt:message bundle="${resword}" key="condition"/></h3>
				</div>
				<div class="panel-body">
					<p class="btn eval and condition"><fmt:message bundle="${resword}" key="AND"/></p>
					<p class="btn eval or condition"><fmt:message bundle="${resword}" key="OR"/></p>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div id="designSurface" class="col-md-12 targettable">
		<div class="panel panel-primary">
			<div class="panel-body sortable ds-scrollable">
				<div class="pull-right trash">
					<div id="deleteButton" type="button" class="hidden delete">
						<span class="glyphicon glyphicon-trash pull-right"></span>
					</div>
				</div>
				<div class="dotted-border group init"><fmt:message bundle="${resword}" key="group_or_data"/></div>
			</div>
		</div>
	</div>
</div>
<div class="inner-scrollbar-wrapper">
	<div class="inner-scrollbar">
		<div class="row">
			<div class="col-md-3 space-top-sm">
				<span><fmt:message bundle="${resword}" key="rule_name"/>:</span>
			</div>
			<div class="col-md-7 space-top-sm">
				<input type="text" class="form-control" id="ruleName">
			</div>
		</div>
		<div class="row">
			<div class="col-md-3 space-top-sm">
				<span><fmt:message bundle="${resword}" key="target_this_rule_to"/>:</span>
			</div>
			<div class="col-md-7 space-top-sm">
				<div class="input-group parent-target">
					<input type="text" class="target form-control">
					<input type="text" class="linefy form-control input-group hidden">
					<span class="input-group-addon hidden">
						<input class="eventify" type="checkbox">
					</span>
					<span class="input-group-addon hidden">
						<input class="versionify" type="checkbox">
					</span>
					<span class="glyphicon glyphicon-remove input-group-addon"></span>
				</div>
				<span class="glyphicon glyphicon-wrench opt"></span>
			</div>
		</div>
		<div class="row">
			<div class="col-md-3 space-top-sm">
				<span><fmt:message bundle="${resword}" key="if_the_rule_evaluates_to"/>:</span>
			</div>
			<div class="col-md-2 space-top-sm">
				<input id="evaluateTrue" type="radio" name="ruleInvoke"> <fmt:message bundle="${resword}" key="true"/>
			</div>
			<div class="col-md-7 space-top-sm">
				<input id="evaluateFalse" type="radio" name="ruleInvoke"> <fmt:message bundle="${resword}" key="false"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-3 space-top-sm">
				<span><fmt:message bundle="${resword}" key="execute_te_rule_upon"/>:</span>
			</div>
			<div class="col-md-2 space-top-sm">
				<input id='ide' type="checkbox"> <fmt:message bundle="${resword}" key="initial_data_entry"/>
			</div>
			<div class="col-md-2 space-top-sm">
				<input id='ae' type="checkbox"> <fmt:message bundle="${resword}" key="administrative_editing"/>
			</div>
			<div class="col-md-2 space-top-sm">
				<input id='dde' type="checkbox"> <fmt:message bundle="${resword}" key="double_data_entry"/>
			</div>
			<div class="col-md-3 space-top-sm">
				<input id='dataimport' type="checkbox"> <fmt:message bundle="${resword}" key="data_import"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-3 space-top-sm">
				<span><fmt:message bundle="${resword}" key="take_the_following_action_s"/>:</span>
			</div>
			<div class="col-md-2 space-top-sm">
				<input type="radio" action="discrepancy" name="action"> <fmt:message bundle="${resword}" key="create_discrepancy"/>
			</div>
			<div class="col-md-2 space-top-sm">
				<input type="radio" action="email" name="action"> <fmt:message bundle="${resword}" key="send_email"/>
			</div>
			<div class="col-md-2 space-top-sm">
				<input type="radio" action="insert" name="action"> <fmt:message bundle="${resword}" key="insert_data"/>
			</div>
			<div class="col-md-3 space-top-sm">
				<input type="radio" action="show" name="action"> <fmt:message bundle="${resword}" key="show_crf_item"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-3 space-top-sm">
				&nbsp;
			</div>
			<div class="col-md-2 space-top-sm">
				<input type="radio" action="hide" name="action"> <fmt:message bundle="${resword}" key="hide_crf_item"/>
			</div>
			<div class="col-md-7 space-top-sm">
				&nbsp;
			</div>
		</div>
		<div class="row">
			<div class="col-md-12 pull-left dotted-border-lg">
				<div class="discrepancy-properties form-group">
					<label class="col-md-2 control-label"><fmt:message bundle="${resword}" key="discrepancy_text"/>:</label>
					<textarea class="form-control input-sm" rows="2"></textarea>
				</div>
				<div class="email-properties form-group">
					<label class="col-md-2 control-label"><fmt:message bundle="${resword}" key="email_to"/>:</label>
					<input type="text" class="to form-control input-sm">
				</div>
				<div class="form-group email-properties">
					<label for="email" class="col-md-2 control-label"><fmt:message bundle="${resword}" key="email_message"/>:</label>
					<textarea class="body form-control" rows="2"></textarea>
				</div>
				<span class="space-left-m"><fmt:message bundle="${resword}" key="with_the_following_information"/>:</span>
				<div class="insert-properties form-group">
					<div id="1" class="row">
						<div class="col-md-2">
							&nbsp;
						</div>
						<div class="col-md-3">
							<label class="lbl"><fmt:message bundle="${resword}" key="item"/>:</label>
							<input type="text" class="form-control input-sm col-md-3 item" style="text-align:center">
						</div>
						<div class="col-md-4 input-group">
							<label class="lbl"><fmt:message bundle="${resword}" key="value"/>:</label>
							<div class="input-group">
								<input type="text" class="form-control input-sm value" style="text-align:center">
								<span class="glyphicon glyphicon-remove input-group-addon"></span>
							</div>
						</div>
					</div>
				</div>
				<div class="show-hide-properties form-group">
					<div class="form-group">
						<label class="col-md-2 control-label"><fmt:message bundle="${resword}" key="message"/>:</label>
						<textarea class="message form-control input-sm" rows="2"></textarea>
					</div>
					<label class="col-md-2 control-label"><fmt:message bundle="${resword}" key="destination"/>:</label>
					<div class="col-md-6 space-bottom space-left-neg">
						<div class="input-group">
							<input type="text" class="form-control input-sm dest">
							<span class="glyphicon glyphicon-remove input-group-addon"></span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row footer-container">
	<div class="col-md-12">
		<nav class="navbar navbar-default nav-cont navbar-fixed-bottom" role="navigation">
			<ul class="nav nav-justified">
				<li><a id='validate' class="btn btn-primary navbar-btn"><fmt:message bundle="${resword}" key="validate_rule"/></a></li>
				<li><a id="exit" href="" class="btn btn-warning navbar-btn"><fmt:message bundle="${resword}" key="exit"/></a></li>
			</ul>
		</nav>
	</div>
</div>
<div class="data">
	<div class='row'>
		<div class="col-md-6">
			<div class="panel panel-primary panel-primary-custom">
				<div class="panel-heading">
					<h3 class="panel-title center-text"><fmt:message bundle="${resword}" key="data"/></h3>
				</div>
				<div class="panel-body">
					<p class="btn group text"><fmt:message bundle="${resword}" key="text"/></p>
					<p class="btn group number"><fmt:message bundle="${resword}" key="number"/></p>
					<p class="btn group date"><fmt:message bundle="${resword}" key="date"/></p>
				</div>
			</div>
		</div>
		<div class="col-md-6">
			<div class="panel panel-primary panel-primary-custom">
				<div class="panel-heading">
					<h3 class="panel-title center-text"><fmt:message bundle="${resword}" key="macros"/></h3>
				</div>
				<div class="panel-body">
					<p class="btn group current-date"><fmt:message bundle="${resword}" key="current_date"/></p>
					<p class="btn group empty"><fmt:message bundle="${resword}" key="empty"/></p>
				</div>
			</div>
		</div>
	</div><br/>
	<div class="panel panel-primary border-less-panel">
		<div class="panel-body">
			<div class="space-top variables">
				<ul class="nav nav-tabs">
					<li id="studiesLink" class="active"><a class="pont green" href="#studies"><fmt:message bundle="${resword}" key="studies"/></a></li>
					<li id="eventsLink"><a class="pont green" href="#events"><fmt:message bundle="${resword}" key="events"/></a></li>
					<li id="crfsLink"><a class="pont green" href="#crfs"><fmt:message bundle="${resword}" key="crfs"/></a></li>
					<li id="versionsLink"><a class="pont green" href="#versions"><fmt:message bundle="${resword}" key="crf_versions"/></a></li>
					<li id="itemsLink"><a class="pont green" href="#items"><fmt:message bundle="${resword}" key="items"/></a></li>
				</ul>
				<div class="data-scrollbar">
					<div class="tab-content">
						<div class="tab-pane active" id="studies"></div>
						<div class="tab-pane" id="events"></div>
						<div class="tab-pane" id="crfs"></div>
						<div class="tab-pane" id="versions"></div>
						<div class="tab-pane" id="items"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</div>
<jsp:include page="messages.jsp"/>
</body>
</html>
