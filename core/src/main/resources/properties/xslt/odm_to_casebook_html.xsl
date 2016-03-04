<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
				xmlns:java="java" xmlns:xls="http://www.w3.org/1999/XSL/Transform"
				extension-element-prefixes="exsl"
				xmlns:exsl="http://exslt.org/common"
				xmlns:odm="http://www.cdisc.org/ns/odm/v1.3"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:OpenClinica="http://www.openclinica.org/ns/odm_ext_v130/v3.1">
	<xsl:param name="locale"/>
	<xsl:param name="language"/>
	<xsl:param name="dateFormat"/>
	<xsl:param name="includeDNs"/>
	<xsl:param name="includeAudits"/>
	<xsl:param name="casebookDate"/>
	<xsl:param name="formVersionOID"/>
	<xsl:param name="userTimeZoneId"/>
	<xsl:param name="dateTimeFormat"/>
	<xsl:param name="fileDownloadUrl"/>
	<xsl:param name="studyParameters"/>
	<xsl:param name="converterHelper"/>
	<xsl:param name="resourceBundleWords"/>
	<xsl:param name="resourceBundleTerms"/>
	<xsl:variable name="study" select="/odm:ODM/odm:Study[$studyOID = @OID]"/>
	<xsl:variable name="studyOID" select="/odm:ODM/odm:ClinicalData/@StudyOID"/>
	<xsl:variable name="studyName" select="$study/odm:GlobalVariables/odm:StudyName"/>
	<xsl:variable name="studySubject" select="/odm:ODM/odm:ClinicalData/odm:SubjectData"/>
	<xsl:variable name="protocolNameStudy" select="$study/odm:GlobalVariables/odm:ProtocolName"/>
	<xsl:variable name="studySubjectOID" select="/odm:ODM/odm:ClinicalData/odm:SubjectData/@OpenClinica:StudySubjectID"/>
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
				<title>ClinCapture - Printable Forms</title>
				<style type="text/css">
					/*! normalize.css v2.0.1 | MIT License | git.io/normalize */

					/* ==========================================================================
					HTML5 display definitions
					========================================================================== */

					/*
					* Corrects `block` display not defined in IE 8/9.
					*/

					article,
					aside,
					details,
					figcaption,
					figure,
					footer,
					header,
					hgroup,
					nav,
					section,
					summary {
					display: block;
					}

					/*
					* Corrects `inline-block` display not defined in IE 8/9.
					*/

					audio,
					canvas,
					video {
					display: inline-block;
					}

					/*
					* Prevents modern browsers from displaying `audio` without controls.
					* Remove excess height in iOS 5 devices.
					*/

					audio:not([controls]) {
					display: none;
					height: 0;
					}

					/*
					* Addresses styling for `hidden` attribute not present in IE 8/9.
					*/

					[hidden] {
					display: none;
					}

					/* ==========================================================================
					Base
					========================================================================== */

					/*
					* 1. Sets default font family to sans-serif.
					* 2. Prevents iOS text size adjust after orientation change, without disabling
					*    user zoom.
					*/

					html {
					font-family: sans-serif; /* 1 */
					-webkit-text-size-adjust: 100%; /* 2 */
					-ms-text-size-adjust: 100%; /* 2 */
					}

					/*
					* Removes default margin.
					*/

					body {
					margin: 0;
					}

					/* ==========================================================================
					Links
					========================================================================== */

					/*
					* Addresses `outline` inconsistency between Chrome and other browsers.
					*/

					a:focus {
					outline: thin dotted;
					}

					/*
					* Improves readability when focused and also mouse hovered in all browsers.
					*/

					a:active,
					a:hover {
					outline: 0;
					}

					/* ==========================================================================
					Typography
					========================================================================== */

					/*
					* Addresses `h1` font sizes within `section` and `article` in Firefox 4+,
					* Safari 5, and Chrome.
					*/

					h1 {
					font-size: 2em;
					}

					/*
					* Addresses styling not present in IE 8/9, Safari 5, and Chrome.
					*/

					abbr[title] {
					border-bottom: 1px dotted;
					}

					/*
					* Addresses style set to `bolder` in Firefox 4+, Safari 5, and Chrome.
					*/

					b,
					strong {
					font-weight: bold;
					}

					/*
					* Addresses styling not present in Safari 5 and Chrome.
					*/

					dfn {
					font-style: italic;
					}

					/*
					* Addresses styling not present in IE 8/9.
					*/

					mark {
					background: #ff0;
					color: #000;
					}

					/*
					* Corrects font family set oddly in Safari 5 and Chrome.
					*/

					code,
					kbd,
					pre,
					samp {
					font-family: monospace, serif;
					font-size: 1em;
					}

					/*
					* Improves readability of pre-formatted text in all browsers.
					*/

					pre {
					white-space: pre;
					white-space: pre-wrap;
					word-wrap: break-word;
					}

					/*
					* Sets consistent quote types.
					*/

					q {
					quotes: "\201C" "\201D" "\2018" "\2019";
					}

					/*
					* Addresses inconsistent and variable font size in all browsers.
					*/

					small {
					font-size: 80%;
					}

					/*
					* Prevents `sub` and `sup` affecting `line-height` in all browsers.
					*/

					sub,
					sup {
					font-size: 75%;
					line-height: 0;
					position: relative;
					vertical-align: baseline;
					}

					sup {
					top: -0.5em;
					}

					sub {
					bottom: -0.25em;
					}

					/* ==========================================================================
					Embedded content
					========================================================================== */

					/*
					* Removes border when inside `a` element in IE 8/9.
					*/

					img {
					border: 0;
					}

					/*
					* Corrects overflow displayed oddly in IE 9.
					*/

					svg:not(:root) {
					overflow: hidden;
					}

					/* ==========================================================================
					Figures
					========================================================================== */

					/*
					* Addresses margin not present in IE 8/9 and Safari 5.
					*/

					figure {
					margin: 0;
					}

					/* ==========================================================================
					Forms
					========================================================================== */

					/*
					* Define consistent border, margin, and padding.
					*/

					fieldset {
					border: 1px solid #c0c0c0;
					margin: 0 2px;
					padding: 0.35em 0.625em 0.75em;
					}

					/*
					* 1. Corrects color not being inherited in IE 8/9.
					* 2. Remove padding so people aren't caught out if they zero out fieldsets.
					*/

					legend {
					border: 0; /* 1 */
					padding: 0; /* 2 */
					}

					/*
					* 1. Corrects font family not being inherited in all browsers.
					* 2. Corrects font size not being inherited in all browsers.
					* 3. Addresses margins set differently in Firefox 4+, Safari 5, and Chrome
					*/

					button,
					input,
					select,
					textarea {
					font-family: inherit; /* 1 */
					font-size: 100%; /* 2 */
					margin: 0; /* 3 */
					}

					/*
					* Addresses Firefox 4+ setting `line-height` on `input` using `!important` in
					* the UA stylesheet.
					*/

					button,
					input {
					line-height: normal;
					}

					/*
					* 1. Avoid the WebKit bug in Android 4.0.* where (2) destroys native `audio`
					*    and `video` controls.
					* 2. Corrects inability to style clickable `input` types in iOS.
					* 3. Improves usability and consistency of cursor style between image-type
					*    `input` and others.
					*/

					button,
					html input[type="button"], /* 1 */
					input[type="reset"],
					input[type="submit"] {
					-webkit-appearance: button; /* 2 */
					cursor: pointer; /* 3 */
					}

					/*
					* Re-set default cursor for disabled elements.
					*/

					button[disabled],
					input[disabled] {
					cursor: default;
					}

					/*
					* 1. Addresses box sizing set to `content-box` in IE 8/9.
					* 2. Removes excess padding in IE 8/9.
					*/

					input[type="checkbox"],
					input[type="radio"] {
					box-sizing: border-box; /* 1 */
					padding: 0; /* 2 */
					}

					/*
					* 1. Addresses `appearance` set to `searchfield` in Safari 5 and Chrome.
					* 2. Addresses `box-sizing` set to `border-box` in Safari 5 and Chrome
					*    (include `-moz` to future-proof).
					*/

					input[type="search"] {
					-webkit-appearance: textfield; /* 1 */
					-moz-box-sizing: content-box;
					-webkit-box-sizing: content-box; /* 2 */
					box-sizing: content-box;
					}

					/*
					* Removes inner padding and search cancel button in Safari 5 and Chrome
					* on OS X.
					*/

					input[type="search"]::-webkit-search-cancel-button,
					input[type="search"]::-webkit-search-decoration {
					-webkit-appearance: none;
					}

					/*
					* Removes inner padding and border in Firefox 4+.
					*/

					button::-moz-focus-inner,
					input::-moz-focus-inner {
					border: 0;
					padding: 0;
					}

					/*
					* 1. Removes default vertical scrollbar in IE 8/9.
					* 2. Improves readability and alignment in all browsers.
					*/

					textarea {
					overflow: auto; /* 1 */
					vertical-align: top; /* 2 */
					}

					/* ==========================================================================
					Tables
					========================================================================== */

					/*
					* Remove most spacing between table cells.
					*/

					table {
					border-collapse: collapse;
					border-spacing: 0;
					}
				</style>
				<style type="text/css">
					@page {
					margin-right: 1cm;
					}

					@media print {
					table {
					page-break-after: auto
					}
					tr {
					page-break-inside: avoid;
					page-break-after: auto
					}
					td {
					page-break-inside: auto;
					page-break-after: auto
					}
					thead {
					display: table-header-group  }
					tfoot {
					display: table-footer-group
					}
					}

					body {
					font-family: Arial Unicode MS, Tahoma, Arial, Helvetica, Sans-Serif;
					margin-left:10px;
					}

					table.item-row {
					padding-left: 15px;
					padding-right: 15px;
					}

					h1 {
					font-weight:normal
					font-size: 12px;
					}
					h2 {
					font-weight:normal
					font-size: 11px;
					}
					h3 {
					font-weight:normal
					font-size: 10px;
					}
					h4 {
					font-weight:normal
					font-size: 9px;
					}

					a:link {
					color: red;
					}
					a:visited {
					color: red;
					}
					a:hover {
					color: blue;
					}
					a:active {
					color: red;
					}

					.repeating-group-table {
					table-layout:fixed !important;
					width: 100%  !important;
					}

					.repeating-group-table td {
					padding: 2px;
					}

					.repeating_item_group_element {
					vertical-align:top;
					}

					#page-header {
					table-layout:fixed;  width: 100%;
					padding:20px;
					margin-bottom: 1px;
					font-size: 14px;
					}

					#page-header td.left-header, #page-header td.right-header {
					vertical-align: top;
					padding-top: 10px;
					}

					#page-header td.left-header {
					width: 66%;
					}

					#page-header td#eventName, #page-header td#eventStartDate, #page-header td#eventLocation, #page-header td#eventEndDate {
					width: 33%;
					}

					.eventHeaderLabel {
					width: 30%;
					float: left;
					}

					#page-header td.printCrf_box6 {
					font-weight:bold;
					}

					#DNBackgroundColorForHeaders{
					background-color : #C4CFD6;
					text-decoration:underline;
					font-size: 90%;
					}

					.AL_DNBackgroundColorForHeaders{
					background-color : #999;
					text-decoration:underline;
					border-bottom-width: thick;
					border-bottom-style: solid;
					border-bottom-color: black;
					width: 100%;
					font-size: 90%;
					}

					.DNParentRowColor {
					background-color : #CCC;
					font-size: 80%;
					}

					#MetadataBackgroundColorForHeaders {
					background-color : #B6C4CF;
					text-decoration:underline;

					}

					#ALBackgroundColorForHeaders {
					background-color : #DADFE5;
					text-decoration:underline;
					font-size: 90%;
					}

					.spinner {
					display: none;
					position: fixed;
					top: 20%;
					left: 50%;
					height: 16px;
					width: 16px;
					}

					.with_border {
					border:1px solid black;
					}

					.centered {
					display: block;
					margin-left: auto;
					margin-right: auto;
					}

					.text_centered {
					text-align: center;
					}

					.text-right-aligned {
					text-align: right;
					}

					.text-left-aligned {
					text-align: left;
					}

					.text-left-aligned-bold {
					text-align: left;
					font-weight:bold;

					}
					.text-right-aligned-bold {
					text-align: right;
					font-weight:bold;
					}
					.repeating_item_wrapper{
					display:inline-block;
					margin-top:0px;
					margin-bottom:0px;
					margin-left:5px;
					}


					.item_def_wrapper{
					min-width:400px;
					padding-top:0px;
					padding-bottom:0px;
					margin-top:0px;
					margin-bottom:0px;
					margin-right:10px;
					padding-left: 5px;
					}

					.item_def_name{
					vertical-align: top;
					display: inline-block;
					font-weight: normal;
					}

					.bold_item_def_name{
					vertical-align: top;
					display: inline-block;
					font-weight: bold;
					}

					.no_border_top{
					border-top: none !important;
					}

					.item_data_div{
					background-color: white;
					width: 100%;
					min-height: 45px;
					border-top: 1px solid;
					}

					.item_def_number{
					vertical-align: top;
					display: inline-block;
					font-weight: normal;
					}

					.item_def_title{
					vertical-align: top;
					display: inline-block;
					width:200px;
					font-weight: normal;
					}

					div.item_def_control{
					vertical-align: bottom;
					display: inline-block;
					padding-top:0;
					padding-bottom:0;
					margin-top:0;
					margin-bottom:0;
					min-height:20px
					}

					span.item_def_control{
					vertical-align: bottom;
					display: inline-block;
					font-size: 0.7em;
					padding-top:0;
					padding-bottom:0;
					margin-top:0;
					margin-bottom:0;
					}

					.item_def_format_label{
					vertical-align: top;
					color:  gray;
					}

					div.inline {
					display: inline;
					}

					.checkbox_control{
					}

					.item_def_repeating_unit {
					display: inline-block;
					font-size: 0.6em;
					}

					.header-title {
					background-color:#DDDDDD;
					padding-left:10px;
					padding-right:10px;
					margin-top:0px;
					margin-bottom:0px;
					}

					.section-title {
					background-color: #B5B5B5;
					border: thin solid #999999;
					font-size: 1em;
					font-weight:bold;
					padding-top: 3px;
					padding-left: 6px;
					padding-right: 6px;
					padding-bottom: 3px;
					vertical-align: top;
					}


					.section-info {
					background-color: #B5B5B5;
					border: thin solid #999999;
					font-size: 1em;
					padding-top: 3px;
					padding-left: 6px;
					padding-right: 6px;
					padding-bottom: 3px;
					vertical-align: top;
					}

					.title_manage {
					color: #D4A718;
					}

					.select-option {
					display: block;
					padding-top:1px;
					padding-right:2px;
					padding-bottom:2px;
					min-height:20px;
					}

					.header-text {
					color: #333333;
					font-size: 13px;
					padding-bottom: 7px;

					padding-left: 2px;

					}

					.header-text-plus-not-bold {
					color: #333333;
					font-size: 14px;
					padding-bottom: 7px;
					}


					.header-text-plus {
					color: #333333;
					font-size: 14px;
					font-weight:bold;
					padding-bottom: 7px;
					}
					.investigator-text {
					color: #333333;
					padding-top: 100px;
					}

					.repeating_item_header {
					border:1px solid;
					font-size: 13px;
					font-weight: bold;
					background-color: #DDDDDD;
					height: 55px;
					}

					.repeating_item_group {
					vertical-align:top;
					}
					.repeating_item_option_names {
					font-weight: bold;
					padding-right: 10px;
					text-align: left;
					}

					tr.min-height {
					min-height: 18px;
					font-size : 0.8em
					}

					tr.min-height-20 {
					min-height: 20px;
					font-size : 0.9em
					}


					td.item-def-cell {
					vertical-align: top;
					}

					.repeating_item_group_header{
					font-size: 0.9em;
					padding-left: 5px;
					line-height: 15px;
					color: #4D4D4D;
					}

					.page-break-screen {
					display: block;
					margin-top: 300px;
					margin-bottom:50px;
					}

					.vertical-spacer-30px {
					margin-top: 30px;
					}

					.vertical-spacer-20px {
					margin-top: 20px;
					}

					table.repeating-horiz-items td, th {
					border:none;
					}

					input[type="text"], textarea {
					display: inline-block;
					background-color : #EEEEEE;
					border: none;
					width: 70%;
					margin-top:3px;
					margin-bottom:3px;
					text-align: center;
					}


					@media all {
					.page-break { display: none; }
					}

					@media print {
					.page-break { display: block; page-break-before: always; }

					body {
					background-color: white;
					}

					.no_print {
					display: none;
					}

					.page-break-screen {
					display: none;
					}
					}


					td {
					vertical-align: top;
					}




					.provenance td, .DNParentRowColor td {
					border-right: 1px solid black;
					}

					.provenance td {
					font-size: 80%;
					}

					.sigbox {
					margin-top: 50px;
					}

					.esig {
					text-indent:20px;
					font-style: italic;
					}

					.dn_notes {
					margin: 0 0 0 20px;
					}

					.dn_notes {
					margin: 0 0 0 20px;
					}

					#tocHeader {
					text-align: center;
					padding-top: 50px;
					padding-bottom: 20px;
					}

					.tocLabel {
					background-color: #153A5A;
					color: white;
					}

					.tocEvent {
					background-color: #B2BEC8;
					}

					.eventTable, .itemTable, .metadataTable {
					padding-left: 5px;
					margin-top: 20px;
					width: 100%;
					border-collapse: collapse;
					border-left-style: solid;
					border-left-width: 1px;
					border-left-color: #000000;
					border-right-style: solid;
					border-right-width: 1px;
					border-right-color: #000000;
					}

					.itemTable {
					margin-left: 1%;
					width: 99%;
					}

					.eventTable td, .itemTable td, .metadataTable td {
					border-top: 1px solid black;
					border-bottom: 1px solid black;
					border-right: 1px solid black;
					padding-left: 5px;
					}

					#crfList {	margin: 50px 0 50px 0;
					}

					#crfList p {
					margin: 0;
					padding-left: 50px;
					}

					#crfList ul {
					margin: 0;
					padding-left: 100px;
					list-style-type: none;
					}

					#loading_msg {
					margin-top: 5%;
					color: #999999;
					width: 100%;
					text-align: center;
					font-size: 18px;
					font-family: Verdana, Geneva, sans-serif;
					}

					div#item {
					width: auto !important;
					}
				</style>
				<style type="text/css">
					/*.lleo_errorSelection *::-moz-selection,
					.lleo_errorSelection *::selection,
					.lleo_errorSelection *::-webkit-selection {
					background-color: red !important;
					color: #fff !important;;
					}*/

					#lleo_dialog,
					#lleo_dialog * {
					color: #000 !important;
					font: normal 13px Arial, Helvetica !important;
					line-height: 15px !important;
					margin: 0 !important;
					padding: 0 !important;
					background: none !important;
					border: none 0 !important;
					position: static !important;
					vertical-align: baseline !important;
					overflow: visible !important;
					width: auto !important;
					height: auto !important;
					max-width: none !important;
					max-height: none !important;
					float: none !important;
					visibility: visible !important;
					text-align: left !important;
					text-transform: none !important;
					border-collapse: separate !important;
					border-spacing: 2px !important;
					box-sizing: content-box !important;
					box-shadow: none !important;
					opacity: 1 !important;
					text-shadow: none !important;
					letter-spacing: normal !important;
					-webkit-filter: none !important;
					-moz-filter: none !important;
					filter: none !important;
					}
					#lleo_dialog *:before,
					#lleo_dialog *:after {
					content: '';
					}

					#lleo_dialog iframe {
					height: 0 !important;
					width: 0 !important;
					}

					#lleo_dialog {
					position: absolute !important;
					background: #fff !important;
					border: solid 1px #ccc !important;
					padding: 7px 0 0 !important;
					left: -999px;
					top: -999px;
					width: 440px !important;
					overflow: hidden;
					display: block !important;
					z-index: 999999999 !important;
					box-shadow: 8px 16px 30px rgba(0, 0, 0, 0.16) !important;
					border-radius: 3px !important;
					opacity: 0 !important;
					-webkit-transform: translateY(15px);
					-moz-transform: translateY(15px);
					-ms-transform: translateY(15px);
					-o-transform: translateY(15px);
					transform: translateY(15px);
					}
					#lleo_dialog.lleo_show_small {
					width: 150px !important;
					}
					#lleo_dialog.lleo_show {
					opacity: 1 !important;
					-webkit-transform: translateY(0);
					-moz-transform: translateY(0);
					-ms-transform: translateY(0);
					-o-transform: translateY(0);
					transform: translateY(0);
					-webkit-transition: -webkit-transform 0.3s, opacity 0.3s !important;
					-moz-transition: -moz-transform 0.3s, opacity 0.3s !important;
					-ms-transition: -ms-transform 0.3s, opacity 0.3s !important;
					-o-transition: -o-transform 0.3s, opacity 0.3s !important;
					transition: transform 0.3s, opacity 0.3s !important;
					}
					#lleo_dialog.lleo_collapse {
					opacity: 0 !important;
					-webkit-transform: scale(0.25, 0.1) translate(-550px, 100px);
					-moz-transform: scale(0.25, 0.1) translate(-550px, 100px);
					-ms-transform: scale(0.25, 0.1) translate(-550px, 100px);
					-o-transform: scale(0.25, 0.1) translate(-550px, 100px);
					transform: scale(0.25, 0.1) translate(-550px, 100px);
					-webkit-transition: -webkit-transform 0.4s, opacity 0.4s !important;
					-moz-transition: -moz-transform 0.4s, opacity 0.4s !important;
					-ms-transition: -ms-transform 0.4s, opacity 0.4s !important;
					-o-transition: -o-transform 0.4s, opacity 0.4s !important;
					transition: transform 0.4s, opacity 0.4s !important;
					}
					#lleo_dialog input::-webkit-input-placeholder {
					color: #aaa !important;
					}
					#lleo_dialog .lleo_has_pic #lleo_word {
					margin-right: 80px !important;
					}
					#lleo_dialog #lleo_translationsContainer1 {
					position: relative !important;
					}
					#lleo_dialog #lleo_translationsContainer2 {
					padding: 7px 0 0 !important;
					vertical-align: middle !important;
					}
					#lleo_dialog #lleo_word {
					color: #000 !important;
					margin: 0 5px 2px 0 !important;
					/*float: left !important;*/
					}
					#lleo_dialog .lleo_has_sound #lleo_word {
					margin-left: 30px !important;
					}
					#lleo_dialog #lleo_text {
					font-weight: bold !important;
					color: #d56e00 !important;
					text-decoration: none !important;
					cursor: default !important;
					}
					/*
					#lleo_dialog #lleo_text.lleo_known {
					cursor: pointer !important;
					text-decoration: underline !important;
					}
					*/
					/*#lleo_dialog #lleo_closeBtn {
					position: absolute !important;
					right: 6px !important;
					top: 5px !important;
					line-height: 1px !important;
					text-decoration: none !important;
					font-weight: bold !important;
					font-size: 0 !important;
					color: #aaa !important;
					display: block !important;
					z-index: 9999999999 !important;
					width: 7px !important;
					height: 7px !important;
					padding: 0 !important;
					margin: 0 !important;
					}*/

					#lleo_dialog #lleo_optionsBtn {
					position: absolute !important;
					right: 3px !important;
					top: 5px !important;
					line-height: 1px !important;
					text-decoration: none !important;
					font-weight: bold !important;
					font-size: 13px !important;
					color: #aaa !important;
					padding: 2px !important;
					display: none;
					}
					#lleo_dialog.lleo_optionsShown #lleo_optionsBtn {
					display: block !important;
					}
					#lleo_dialog #lleo_optionsBtn img {
					width: 12px !important;
					height: 12px !important;
					}
					#lleo_dialog #lleo_sound {
					float: left !important;
					width: 16px !important;
					height: 16px !important;
					margin-left: 9px !important;
					margin-right: 3px !important;
					background: 0 0 no-repeat !important;
					cursor: pointer !important;
					display: none !important;
					background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAfNJREFUeNq0U01IVFEU/u57Oo5WhBRIBBptykWLYBa2soWiEKQQEbrSFsGbVRQKQc2iFqGitXqvjQxCoCJCqyI0aBUtZILaJNUuYWYWo8HovJ/707nP94bnz0rxwvfOuefd8517fi5TSuE4i50YwZ3l37ZhNlpgzFkaaM/G9sHF1YskNrT+7l4PjMOcb78t2JL71uxgB+2UlfxHTtq5N94fIOh/88kWgWfl73ZCSQkpeGg3H091JY6dI2S00qA/N3KO3dDUYhFgEmZGurG+w9FqApIHsVM7kaTF9Nhn0r8Q7hPWQgIRuNaH3AMUA4W/Lkdh04cpFS43G0TgxQTvCdMETVAk3KynIHwXZU/ge8XDt7KH9bKLjU0P2zVO5LsEpSejVRJ9UR18EtfqKegovs9R3Q6w9c/H1o4Aa2Jwm1lIvn9RJ4w9RdRRzqcYrpwycCll4Cy1lnkS3Bc6vfBg28v8aRIfI78zhB/1GygROH3jLyyzMQ0zlUZuZBSlKkeLoegGtTjYLcJ8pF+NakHOFC2J6w+f25mxSfWrWFF/ShXVPTGvtN14NNkVnxlYWJkgZEL7/vwKr55lKSVnaGYWkuYgrgG172uUv47+U7fw0EHaJXmalUQy/HqO6lBzEsVjJC4Q8kd6TETQpjuaGOvjv8b/AgwA/ij1XMx58NIAAAAASUVORK5CYII=) !important;
					}
					#lleo_dialog .lleo_has_sound #lleo_sound {
					display: block !important;
					}

					#lleo_dialog #lleo_soundWave {
					border: solid 5px #4495CC !important;
					border-radius: 5px !important;
					position: absolute !important;
					left: -5px !important;
					top: -5px !important;
					right: -5px !important;
					bottom: -5px !important;
					z-index: 0 !important;
					opacity: 0.9 !important;
					display: none !important;
					}
					#lleo_dialog #lleo_soundWave.lleo_beforePlaying {
					display: block !important;
					}
					#lleo_dialog #lleo_soundWave.lleo_playing {
					opacity: 0 !important;
					border-width: 20px !important;
					border-radius: 30px !important;

					-webkit-transform: scale(1.07,1.1) !important;
					-moz-transform: scale(1.07,1.1) !important;
					-ms-transform: scale(1.07,1.1) !important;
					transform: scale(1.07,1.1) !important;

					-webkit-transition: all 0.6s !important;
					-moz-transition: all 0.6s !important;
					-ms-transition: all 0.6s !important;
					transition: all 0.6s !important;
					}


					#lleo_dialog #lleo_picOuter {
					position: absolute !important;
					float: right !important;
					top: 4px;
					right: 5px;
					z-index: 9 !important;
					display: none !important;
					width: 100px !important;
					}
					#lleo_dialog.lleo_optionsShown #lleo_picOuter {
					right: 25px;
					}
					#lleo_dialog .lleo_has_pic #lleo_picOuter {
					display: block !important;
					}
					#lleo_dialog #lleo_picOuter:hover {
					width: auto !important;
					z-index: 11 !important;
					}
					#lleo_dialog #lleo_pic,
					#lleo_dialog #lleo_picBig {
					position: absolute !important;
					top: 0 !important;
					right: 0 !important;
					border: solid 2px #fff !important;
					-webkit-border-radius: 2px !important;
					-moz-border-radius: 2px !important;
					border-radius: 2px !important;
					z-index: 1 !important;
					}
					#lleo_dialog #lleo_pic {
					position: relative !important;
					border: none !important;
					width: 30px !important;
					}
					#lleo_dialog #lleo_picBig {
					box-shadow: -1px 2px 4px rgba(0,0,0,0.3);
					z-index: 2 !important;
					opacity: 0 !important;
					visibility: hidden !important;
					}
					#lleo_dialog #lleo_picOuter:hover #lleo_picBig {
					visibility: visible !important;
					opacity: 1 !important;
					-webkit-transition: opacity 0.3s !important;
					-webkit-transition-delay: 0.3s !important;
					}
					#lleo_dialog #lleo_transcription {
					margin: 0 80px 4px 31px !important;
					color: #aaaaaa !important;
					}
					#lleo_dialog .lleo_no_trans {
					color: #aaa !important;
					}

					#lleo_dialog .ll-translation-counter {
					float: right !important;
					font-size: 11px !important;
					color: #aaa !important;
					padding: 2px 2px 1px 10px !important;
					}

					#lleo_dialog .ll-translation-text {
					float: left !important;
					/*width: 80% !important;*/
					}

					#lleo_dialog #lleo_trans a {
					color: #3F669F !important;
					text-decoration: none !important;
					text-overflow: ellipsis !important;
					padding: 1px 4px !important;
					overflow: hidden !important;
					float: left !important;
					width: 320px !important;
					}

					#lleo_dialog .ll-translation-item {
					color: #3F669F !important;
					border: solid 1px #fff !important;
					padding: 3px !important;
					width: 100% !important;
					float: left !important;
					-moz-border-radius: 2px !important;
					-webkit-border-radius: 2px !important;
					border-radius: 2px !important;
					}

					#lleo_dialog .ll-translation-item:hover {
					border: solid 1px #9FC2C9 !important;
					background: #EDF4F6 !important;
					cursor: pointer !important;
					}
					#lleo_dialog .ll-translation-item:hover .ll-translation-counter {
					color: #83a0a6 !important;
					}

					#lleo_dialog .ll-translation-marker {
					background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAAECAYAAACp8Z5+AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAAWSURBVBhXY7RPm/+fAQkwIXNAbMICAJQ8AkvqWg/SAAAAAElFTkSuQmCC) !important;
					display: inline-block !important;
					width: 4px !important;
					height: 4px !important;
					margin: 7px 5px 2px 2px !important;
					float: left !important;
					}

					#lleo_dialog #lleo_icons {
					color: #aaa !important;
					font-size: 11px !important;
					background: #f8f8f8 !important;
					padding: 10px 10px 10px 16px !important;
					}
					#lleo_icons a {
					display: inline-block !important;
					width: 16px !important;
					height: 16px !important;
					margin: 0 10px -4px 3px !important;
					text-decoration: none !important;
					opacity: 0.5 !important;
					background: url(data:image/png;base64,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) !important;
					}
					#lleo_icons a:hover {
					opacity: 1 !important;
					}
					#lleo_icons a.lleo_google     {background-position:-34px 0 !important;}
					#lleo_icons a.lleo_multitran  {background-position:-64px 0 !important;}
					#lleo_icons a.lleo_lingvo     {background-position:-51px 0 !important; width: 12px !important;}
					#lleo_icons a.lleo_dict       {background-position:-17px 0 !important;}
					#lleo_icons a.lleo_linguee    {background-position:-81px 0 !important;}
					#lleo_icons a.lleo_michaelis  {background-position:-98px 0 !important;}

					#lleo_dialog #lleo_contextContainer {
					margin: 0 !important;
					padding: 3px 15px 8px 10px !important;
					background: #eee !important;
					background: -webkit-gradient(linear, left top, left bottom, from(#fff), to(#eee)) !important;
					background: -moz-linear-gradient(-90deg, #fff, #eee) !important;
					border-bottom: solid 1px #ddd !important;
					border-top-left-radius: 3px !important;
					border-top-right-radius: 3px !important;
					display: none !important;
					overflow: hidden !important;
					}
					#lleo_dialog .lleo_has_context #lleo_contextContainer {
					display: block !important;
					}
					#lleo_dialog #lleo_context {
					color: #444 !important;
					text-shadow: 1px 1px 0 #f4f4f4 !important;
					line-height: 12px !important;
					font-size: 11px !important;
					margin-left: 2px !important;
					}
					#lleo_dialog #lleo_context b {
					line-height: 12px !important;
					color: #000 !important;
					font-weight: bold !important;
					font-size: 11px !important;
					}
					/*#lleo_dialog #lleo_gBrand {
					color: #aaa !important;
					font-size: 10px !important;
					*//*padding-right: 52px !important;*//*
					padding-bottom: 14px !important;
					margin: -3px 4px 0 4px !important;
					background: left bottom url(data:image/png;base64,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) no-repeat !important;
					display: inline-block !important;
					float: right !important;
					}
					#lleo_dialog #lleo_gBrand.hidden {
					display: none !important;
					}*/
					#lleo_dialog #lleo_translateContextLink {
					color: #444 !important;
					text-shadow: 1px 1px 0 #f4f4f4 !important;
					background: -webkit-gradient(linear, left top, left bottom, from(#f4f4f4), to(#ddd)) !important;
					background: -moz-linear-gradient(-90deg, #f4f4f4, #ddd) !important;
					border: solid 1px !important;
					box-shadow: 1px 1px 0 #f6f6f6 !important;
					border-color: #999 #aaa #aaa #999 !important;
					-moz-border-radius: 2px !important;
					-webkit-border-radius: 2px !important;
					border-radius: 2px !important;
					padding: 0 3px !important;
					font-size: 11px !important;
					text-decoration: none !important;
					margin: 1px 5px 0 !important;
					display: inline-block !important;
					white-space: nowrap !important;
					}
					#lleo_dialog #lleo_translateContextLink:hover {
					background: #f8f8f8 !important;
					}
					#lleo_dialog #lleo_translateContextLink.hidden {
					visibility: hidden !important;
					}

					#lleo_dialog #lleo_setTransForm {
					display: block !important;
					margin-top: 3px !important;
					padding-top: 5px !important;
					/* Set position and background because the form might be overlapped by an image when no translations */
					position: relative !important;
					background: #fff !important;
					z-index: 10 !important;
					padding-bottom: 10px !important;
					padding-left: 16px !important;
					}
					#lleo_dialog .lleo-custom-translation {
					padding: 4px 5px !important;
					border: solid 1px #ddd !important;
					border-radius: 2px !important;
					width: 90% !important;
					min-width: 270px !important;
					background: -webkit-gradient(linear, 0 0, 0 20, from(#f1f1f1), to(#fff)) !important;
					background: -moz-linear-gradient(-90deg, #f1f1f1, #fff) !important;
					font: normal 13px Arial, Helvetica !important;
					line-height: 15px !important;
					}
					#lleo_dialog .lleo-custom-translation:hover {
					border: solid 1px #aaa !important;
					}
					#lleo_dialog .lleo-custom-translation:focus {
					background: #FFFEC9 !important;
					}

					#lleo_dialog *.hidden {
					display: none !important;
					}

					#lleo_dialog .infinitive{
					color: #D56E00 !important;
					text-decoration: none;
					border-bottom: 1px dotted #D56E00 !important;
					}
					#lleo_dialog .infinitive:hover{
					border: none !important;
					}

					#lleo_dialog .lleo_separator {
					height: 1px !important;
					background: #eee;
					margin-top: 10px !important;
					background: -webkit-linear-gradient(left, rgba(255,255,255,1) 0%,#eee 8%,rgba(255,255,255,1) 80%) !important;
					background: -moz-linear-gradient(left, rgba(255,255,255,1) 0%, #eee 8%, rgba(255,255,255,1) 80%) !important;
					background: -ms-linear-gradient(left, rgba(255,255,255,1) 0%,#eee 8%,rgba(255,255,255,1) 80%) !important;
					background: linear-gradient(to right, rgba(255,255,255,1) 0%,#eee 8%,rgba(255,255,255,1) 80%) !important;
					}
					#lleo_dialog #lleo_trans {
					/*border-top: 1px solid #eeeeee !important;*/
					padding: 5px 30px 0 14px !important;
					zoom: 1;
					}

					#lleo_dialog .lleo_clearfix {
					display: block !important;
					clear: both !important;
					visibility: hidden !important;
					height: 0 !important;
					font-size: 0 !important;
					}


					#lleo_dialog #lleo_picOuter table {
					width: 44px !important;
					position: absolute !important;
					right: 0 !important;
					top: 0 !important;
					vertical-align: middle !important;
					}

					#lleo_dialog #lleo_picOuter td {
					width: 38px !important;
					height: 38px !important;
					/*border: 1px solid #eeeeee !important;*/
					vertical-align: middle !important;
					text-align: center !important;
					}

					#lleo_dialog #lleo_picOuter td div {
					height: 38px !important;
					overflow: hidden !important;
					}

					#lleo_dialog .lleo_empty {
					margin: 0 5px 7px !important;
					}

					#lleo_youtubeExportBtn {
					margin-left: 10px;
					height: 24px;
					}
					#lleo_youtubeExportBtn i {
					display: inline-block;
					width: 16px;
					height: 16px;
					background: 0 0 url(https://d144fqpiyasmrr.cloudfront.net/plugins/all/images/i16.png) !important;
					}
					#lleo_youtubeExportBtn .yt-uix-button-content {
					font-size: 12px;
					line-height: 2px;
					}


					/*** Parsed Lyrics Content *****************************/

					.lleo_lyrics tran {
					background: transparent !important;
					border-radius: 2px !important;
					text-shadow: none !important;
					cursor: pointer !important;
					}
					.lleo_lyrics tran:hover {
					color: #fff !important;
					background: #C77213 !important;
					-webkit-transition: all 0.1s !important;
					-moz-transition: all 0.1s !important;
					-ms-transition: all 0.1s !important;
					-o-transition: all 0.1s !important;
					transition: all 0.1s !important;
					}

					.lleo_songName {
					border: solid 1px #ffd47c;
					background: #fff1c2;
					border-radius: 2px;
					}

					.lleo_hidden_iframe {
					visibility: hidden;
					}
				</style>
			</head>
			<body>
				<xls:if test="$formVersionOID != '*'">
					<xsl:for-each select="$studySubject/odm:StudyEventData">
						<xsl:variable name="studyEventOID" select="@StudyEventOID"/>
						<xsl:variable name="studyEventRepeatKey" select="@StudyEventRepeatKey"/>
						<xsl:for-each select="odm:FormData">
							<xsl:call-template name="studyEventCRFFormDetailsTemplate"/>
							<xls:call-template name="studyEventCRFFormDataTemplate">
								<xls:with-param name="studyEventOID" select="$studyEventOID"/>
								<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
							</xls:call-template>
						</xsl:for-each>
					</xsl:for-each>
				</xls:if>
				<xls:if test="$formVersionOID = '*'">
					<xsl:call-template name="headerTemplate">
						<xsl:with-param name="formStatus"/>
						<xsl:with-param name="studyEvent"/>
						<xsl:with-param name="interviewDate"/>
						<xsl:with-param name="interviewerName"/>
						<xsl:with-param name="pageType" select="'study_cover_page_type'"/>
						<xsl:with-param name="renderMode" select="'STUDY_SUBJECT_CASE_BOOK'"/>
					</xsl:call-template>
					<h3 id="tocHeader"><xsl:value-of select="java:getString($resourceBundleWords, 'study_subject')"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$studySubjectOID"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:getString($resourceBundleWords, 'table_of_contents')"/></h3>
					<xsl:call-template name="tableOfContentsTemplate"/>
					<xsl:call-template name="headerTemplate">
						<xsl:with-param name="formStatus"/>
						<xsl:with-param name="studyEvent"/>
						<xsl:with-param name="interviewDate"/>
						<xsl:with-param name="interviewerName"/>
						<xsl:with-param name="pageType" select="'study_cover_page_type'"/>
						<xsl:with-param name="renderMode" select="'STUDY_SUBJECT_CASE_BOOK'"/>
					</xsl:call-template>
					<h3 class="centered text_centered" id="subject_title"><xsl:value-of select="java:getString($resourceBundleWords, 'study_subject')"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$studySubjectOID"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:getString($resourceBundleWords, 'details')"/></h3>
					<xsl:call-template name="studySubjectDetailsTemplate"/>
					<xsl:call-template name="eventAndStudySubjectAuditTemplate">
						<xsl:with-param name="object" select="$studySubject"/>
						<xsl:with-param name="objectName" select="'study_subject'"/>
					</xsl:call-template>
					<xsl:for-each select="$studySubject/odm:StudyEventData">
						<xsl:call-template name="studyEventDetailsTemplate"/>
					</xsl:for-each>
				</xls:if>
			</body>
		</html>
	</xsl:template>
	<xsl:template name="headerTemplate">
		<xsl:param name="pageType"/>
		<xsl:param name="formStatus"/>
		<xsl:param name="studyEvent"/>
		<xsl:param name="renderMode"/>
		<xsl:param name="interviewDate"/>
		<xsl:param name="interviewerName"/>
		<xsl:variable name="interviewDateRequired" select="java:getInterviewDateRequired($studyParameters)"/>
		<xsl:variable name="interviewerNameRequired" select="java:getInterviewerNameRequired($studyParameters)"/>
		<table id="page-header">
			<tr>
				<td class='left-header'>
					<div class='header-text-plus text-left-aligned'>
						<b><xsl:value-of select="$studyName"/> (<xsl:value-of select="$protocolNameStudy"/>)</b>
					</div>
					<xsl:if test="$renderMode != 'STUDY_SUBJECT_CASE_BOOK'">
						<xsl:if test="$pageType != 'study_cover_page_type'">
							<div class='header-text text-left-aligned'>
								<xsl:if test="$studyEvent">
									<xsl:variable name="studyEventOID" select="$studyEvent/@StudyEventOID"/>
									<xsl:variable name="studyEventDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef[$studyEventOID = @OID]"/>
									<xsl:variable name="studyEventEndDate" select="java:formatSystemDate($converterHelper, $studyEvent/@OpenClinica:EndDate, $dateFormat, $locale)"/>
									<xsl:value-of select="$studyEventDef/@Name"/>
									<xsl:if test="$studyEventDef/@Repeating = 'Yes'"> (<xsl:value-of select="$studyEvent/@StudyEventRepeatKey"/>)</xsl:if>:
									<xsl:value-of select="java:formatSystemDate($converterHelper, $studyEvent/@OpenClinica:StartDate, $dateFormat, $locale)"/>
									<xsl:if test="$studyEventEndDate != ''">
										<xsl:value-of select="concat(' ', '-', ' ', $studyEventEndDate)"/>
									</xsl:if>
								</xsl:if>
							</div>
						</xsl:if>
						<div class='header-text text-left-aligned'><xsl:value-of select="java:getString($resourceBundleWords, 'event_status')"/>: <xsl:value-of select="../@OpenClinica:Status"/></div>
					</xsl:if>
				</td>
				<td class='right-header'>
					<div class='header-text-plus-not-bold text-left-aligned'>
						<xsl:value-of select="java:getString($resourceBundleWords, 'study_subject')"/>: <b><xsl:value-of select="$studySubjectOID"/></b>
					</div>
					<xsl:if test="$renderMode != 'STUDY_SUBJECT_CASE_BOOK'">
						<div class='header-text'><xsl:value-of select="java:getString($resourceBundleWords, 'CRF_status')"/>: <xsl:value-of select="java:asString($converterHelper, $formStatus, '___________________')"/></div>
						<xsl:if test="$interviewerNameRequired != 'not_used'">
							<div class='header-text'><xsl:value-of select="java:getString($resourceBundleWords, 'interviewer_name')"/>: <xsl:value-of select="java:asString($converterHelper, $interviewerName, '_____________________________')"/></div>
						</xsl:if>
						<xsl:if test="$interviewDateRequired != 'not_used'">
							<div class='header-text'><xsl:value-of select="java:getString($resourceBundleWords, 'interview_date')"/>: <xsl:value-of select="java:asString($converterHelper, $interviewDate, '_______________________')"/></div>
						</xsl:if>
					</xsl:if>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="tableOfContentsTemplate">
		<table border="1" width="100%">
			<tr class="tocLabel">
				<td id="toc_eventLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'events')"/></td>
				<td id="toc_crfLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'event_CRF')"/></td>
				<td id="toc_statusLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'status')"/></td>
			</tr>
			<xsl:for-each select="$studySubject/odm:StudyEventData">
				<xsl:variable name="studyEventForms" select="odm:FormData"/>
				<xsl:variable name="studyEventOID" select="@StudyEventOID"/>
				<xsl:variable name="studyEventStatus" select="@OpenClinica:Status"/>
				<xsl:variable name="studyEventRepeatKey" select="@StudyEventRepeatKey"/>
				<xsl:variable name="studyEventDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef[$studyEventOID = @OID]"/>
				<tr class="tocEvent">
					<td id="toc_eventName">
						<xsl:value-of select="$studyEventDef/@Name"/>
						<xsl:if test="$studyEventDef/@Repeating = 'Yes'">
							(<xsl:value-of select="$studyEventRepeatKey"/>)
						</xsl:if>
					</td>
					<td></td>
					<td id="toc_status"><xsl:value-of select="$studyEventStatus"/></td>
				</tr>
				<xsl:for-each select="$studyEventForms">
					<xsl:variable name="formOID" select="@FormOID"/>
					<xsl:variable name="formStatus" select="@OpenClinica:Status"/>
					<xsl:variable name="formDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:FormDef[$formOID = @OID]"/>
					<tr>
						<td></td>
						<td id="toc_formName"><xsl:value-of select="$formDef/@Name"/></td>
						<td id="toc_formStatus"><xsl:value-of select="$formStatus"/></td>
					</tr>
				</xsl:for-each>
			</xsl:for-each>
		</table>
		<div class="page-break-screen"><hr/></div>
		<div class="page-break"></div>
	</xsl:template>
	<xsl:template name="studySubjectDetailsTemplate">
		<xsl:variable name="collectDob" select="java:getCollectDob($studyParameters)"/>
		<xsl:variable name="personIdShownOnCRF" select="java:getPersonIdShownOnCRF($studyParameters)"/>
		<xsl:variable name="secondaryLabelViewable" select="java:getSecondaryLabelViewable($studyParameters)"/>
		<xsl:variable name="subjectPersonIdRequired" select="java:getSubjectPersonIdRequired($studyParameters)"/>
		<table id="page-header">
			<tr>
				<td class="left-header">
					<div id="subject_dob">
						<xsl:if test="$collectDob = 1">
							<xsl:value-of select="java:getString($resourceBundleWords, 'date_of_birth')"/>:<b/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$studySubject/@OpenClinica:DateOfBirth"/>
						</xsl:if>
						<xsl:if test="$collectDob = 2">
							<xsl:value-of select="java:getString($resourceBundleWords, 'date_of_birth')"/>:<b/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:getYear($converterHelper, string($studySubject/@OpenClinica:DateOfBirth))"/>
						</xsl:if>
					</div>
					<div id="subject_sex"><xsl:value-of select="java:getString($resourceBundleWords, 'gender')"/>:<b><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:asString($converterHelper, $studySubject/@OpenClinica:Sex)"/></b></div>
					<xsl:if test="$studySubject/@OpenClinica:EnrollmentDate">
						<div id="subject_enroll"><xsl:value-of select="java:getString($resourceBundleWords, 'enrollMMDDYYYY')"/>:<b><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$studySubject/@OpenClinica:EnrollmentDate"/></b></div>
					</xsl:if>
					<xsl:if test="$studySubject/@OpenClinica:Status">
						<div id="subject_status"><xsl:value-of select="java:getString($resourceBundleWords, 'subject_status')"/>:<b><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$studySubject/@OpenClinica:Status"/></b></div>
					</xsl:if>
				</td>
				<td class="right-header">
					<xsl:if test="$subjectPersonIdRequired != 'not used'">
						<xsl:if test="$personIdShownOnCRF != 'false'">
							<div id="subject_uniqueID">
								<xsl:value-of select="java:getString($resourceBundleWords, 'subject_unique_ID')"/>:<b><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$studySubject/@OpenClinica:UniqueIdentifier"/></b>
							</div>
						</xsl:if>
					</xsl:if>

					<xsl:if test="$secondaryLabelViewable = 'true'">
						<div id="subject_2ndID">
							<xsl:value-of select="java:getString($resourceBundleWords, 'secondary_ID')"/>:<b><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$studySubject/@OpenClinica:SecondaryID"/></b>
						</div>
					</xsl:if>
					<xsl:for-each select="$studySubject/OpenClinica:SubjectGroupData">
						<div id="subject_group"><xsl:value-of select="@OpenClinica:StudyGroupClassName"/>:<b><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="@OpenClinica:StudyGroupName"/></b></div>
					</xsl:for-each>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="eventAndStudySubjectAuditTemplate">
		<xsl:param name="object"/>
		<xsl:param name="objectName"/>
		<table class="eventTable">
			<tr class="AL_DNBackgroundColorForHeaders">
				<td colspan="6"><xsl:value-of select="java:getString($resourceBundleWords, $objectName)"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:getString($resourceBundleWords, 'Audit_History')"/></td>
			</tr>
			<tr class="provenance">
				<td id="eventEventLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'audit_event')"/>:</strong></td>
				<td id="eventTimeLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'date_time_of_server')"/>:</strong></td>
				<td id="eventUsrLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'user')"/>:</strong></td>
				<td id="eventValueLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'value_type')"/>:</strong></td>
				<td id="eventOldLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'old')"/>:</strong></td>
				<td id="eventNewLabel" class="right"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'new')"/>:</strong></td>
			</tr>
			<xsl:for-each select="$object/OpenClinica:AuditLogs/OpenClinica:AuditLog">
				<tr class="provenance">
					<td id="eventAuditType"><xsl:value-of select="@AuditType"/></td>
					<td id="eventTime"><xsl:value-of select="java:formatSystemDateTime($converterHelper, string(@DateTimeStamp), $dateTimeFormat, $locale)"/></td>
					<td id="eventUsr"><xsl:value-of select="@Name"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/>(<xsl:value-of select="@UserName"/>)</td>
					<td id="eventValue"><xsl:value-of select="@ValueType"/></td>
					<td id="eventOld"><xsl:value-of select="@OldValue"/></td>
					<td id="eventNew" class="right"><xsl:value-of select="@NewValue"/></td>
				</tr>
			</xsl:for-each>
		</table>
		<div class="page-break-screen"><hr/></div>
		<div class="page-break"></div>
	</xsl:template>
	<xsl:template name="studyEventDetailsTemplate">
		<xsl:variable name="studyEventOID" select="@StudyEventOID"/>
		<xsl:variable name="studyEventRepeatKey" select="@StudyEventRepeatKey"/>
		<xsl:variable name="formSigned" select="java:getString($resourceBundleTerms, 'signed')"/>
		<xsl:variable name="eventLocationRequired" select="java:getEventLocationRequired($studyParameters)"/>
		<xsl:variable name="studyEventDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:StudyEventDef[$studyEventOID = @OID]"/>
		<xsl:variable name="meaningOfSignature" select="concat(java:getString($resourceBundleWords, 'sure_to_sign_subject1'), java:getString($resourceBundleWords, 'sure_to_sign_subject2'))"/>
		<xsl:variable name="vars" select="java:set($converterHelper, 'studyEventName', $studyEventDef/@Name)"/>
		<xsl:if test="$studyEventDef/@Repeating = 'Yes'">
			<xsl:variable name="vars" select="java:set($converterHelper, 'studyEventName', concat($studyEventDef/@Name, ' (', $studyEventRepeatKey, ')'))"/>
		</xsl:if>
		<xsl:variable name="studyEventName" select="java:get($converterHelper, 'studyEventName')"/>
		<xsl:call-template name="headerTemplate">
			<xsl:with-param name="formStatus"/>
			<xsl:with-param name="studyEvent"/>
			<xsl:with-param name="interviewDate"/>
			<xsl:with-param name="interviewerName"/>
			<xsl:with-param name="pageType" select="'study_cover_page_type'"/>
			<xsl:with-param name="renderMode" select="'STUDY_SUBJECT_CASE_BOOK'"/>
		</xsl:call-template>
		<h3><center><xsl:value-of select="$studyEventName"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:getString($resourceBundleWords, 'details')"/></center></h3>
		<table id="page-header">
			<tr>
				<td id="eventName">
					<div class="eventHeaderLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'event_name')"/>:</div>
					<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><strong><xsl:value-of select="$studyEventDef/@Name"/></strong>
				</td>
				<td id="eventStartDate">
					<div class="eventHeaderLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'start_date')"/>:</div>
					<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><strong><xsl:value-of select="java:formatSystemDate($converterHelper, @OpenClinica:StartDate, $dateFormat, $locale)"/></strong></td>
				<td id="eventStatus">
					<div class="eventHeaderLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'event_status')"/>:</div>
					<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><strong><xsl:value-of select="@OpenClinica:Status"/></strong></td>
			</tr>
			<tr>
				<td id="eventLocation">
					<xsl:if test="$eventLocationRequired = 'required'">
						<div class="eventHeaderLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'event_location')"/>:</div>
						<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><strong><xsl:value-of select="@OpenClinica:StudyEventLocation"/></strong>
					</xsl:if>
					<xsl:if test="$eventLocationRequired = 'optional'">
						<div class="eventHeaderLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'event_location')"/>:</div>
						<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><strong><xsl:value-of select="@OpenClinica:StudyEventLocation"/></strong>
					</xsl:if>
				</td>
				<td id="eventEndDate">
					<div class="eventHeaderLabel"><xsl:value-of select="java:getString($resourceBundleWords, 'end_date')"/>:</div>
					<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><strong><xsl:value-of select="java:formatSystemDate($converterHelper, @OpenClinica:EndDate, $dateFormat, $locale)"/></strong>
				</td>
				<td><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/></td>
			</tr>
		</table>
		<xsl:if test="@OpenClinica:Status = $formSigned">
			<xsl:variable name="appOn" select="java:getString($resourceBundleWords, 'on')"/>
			<xsl:variable name="auditLog" select="OpenClinica:AuditLogs/OpenClinica:AuditLog[@NewValue = $formSigned][1]"/>
			<xsl:if test="$auditLog">
				<xsl:value-of select="$auditLog/@DateTimeStamp"/>
				<xsl:variable name="dateTimeStamp" select="java:formatSystemDateTime($converterHelper, string($auditLog/@DateTimeStamp), $dateTimeFormat, $locale)"/>
				<xsl:variable name="electronicSignature" select="concat($auditLog/@Name, ' (', $auditLog/@UserName, ') ', $appOn, ' ', $dateTimeStamp)"/>
				<div class="sigbox">
					<p class="esig"><xsl:value-of select="java:getString($resourceBundleWords, 'the_eCRF_that_are_part_of_this_event_were_signed_by')"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$electronicSignature"/>
						(<xsl:value-of select="java:getString($resourceBundleWords, 'server_time')"/>)<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:getString($resourceBundleWords, 'under_the_following_attestation')"/>:</p>
					<p class="esig"><xsl:value-of select="$meaningOfSignature"/></p>
				</div>
			</xsl:if>
		</xsl:if>
		<xsl:call-template name="crfListTemplate">
			<xsl:with-param name="studyEventOID" select="$studyEventOID"/>
			<xsl:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
		</xsl:call-template>
		<xsl:call-template name="eventAndStudySubjectAuditTemplate">
			<xsl:with-param name="object" select="."/>
			<xsl:with-param name="objectName" select="'SE'"/>
		</xsl:call-template>
		<xsl:for-each select="odm:FormData">
			<xsl:call-template name="studyEventCRFFormDetailsTemplate"/>
			<xls:call-template name="studyEventCRFFormDataTemplate">
				<xls:with-param name="studyEventOID" select="$studyEventOID"/>
				<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
			</xls:call-template>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="crfListTemplate">
		<xsl:param name="studyEventOID"/>
		<xsl:param name="studyEventRepeatKey"/>
		<div id="crfList">
			<p><xsl:value-of select="java:getString($resourceBundleWords, 'case_report_form')"/>:</p>
			<ul>
				<xsl:for-each select="odm:FormData">
					<xsl:variable name="formOID" select="@FormOID"/>
					<xsl:variable name="formDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:FormDef[$formOID = @OID]"/>
					<xsl:variable name="crfFormLink" select="concat('#', $studyEventOID, '/', $studyEventRepeatKey, '/', $formOID)"/>
					<li><a href="{$crfFormLink}"><xsl:value-of select="$formDef/@Name"/></a></li>
				</xsl:for-each>
			</ul>
		</div>
	</xsl:template>
	<xsl:template name="studyEventCRFFormDetailsTemplate">
		<xsl:variable name="formOID" select="@FormOID"/>
		<xsl:variable name="studyEventOID" select="../@StudyEventOID"/>
		<xsl:variable name="studyEventRepeatKey" select="../@StudyEventRepeatKey"/>
		<xsl:variable name="crfFormId" select="concat($studyEventOID, '/', $studyEventRepeatKey, '/', $formOID)"/>
		<xsl:variable name="formDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:FormDef[$formOID = @OID]"/>
		<xsl:variable name="formattedInterviewDate" select="java:formatSystemDate($converterHelper, @OpenClinica:InterviewDate, $dateFormat, $locale)"/>
		<xsl:call-template name="headerTemplate">
			<xsl:with-param name="pageType" select="''"/>
			<xsl:with-param name="studyEvent" select=".."/>
			<xsl:with-param name="renderMode" select="''"/>
			<xsl:with-param name="formStatus" select="@OpenClinica:Status"/>
			<xsl:with-param name="interviewDate" select="$formattedInterviewDate"/>
			<xsl:with-param name="interviewerName" select="@OpenClinica:InterviewerName"/>
		</xsl:call-template>
		<h3 class="centered text_centered" style="margin-bottom:30px"><a id="{$crfFormId}"><xsl:value-of select="$formDef/@Name"/></a></h3>
	</xsl:template>
	<xsl:template name="studyEventCRFFormDataTemplate">
		<xsl:param name="studyEventOID"/>
		<xsl:param name="studyEventRepeatKey"/>
		<xsl:variable name="formOID" select="@FormOID"/>
		<xsl:variable name="formDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:FormDef[$formOID = @OID]"/>
		<xsl:for-each select="$formDef/OpenClinica:FormDetails/OpenClinica:SectionDetails/OpenClinica:Section">
			<div class="vertical-spacer-30px"></div>
			<xsl:variable name="sectionLabel" select="java:asString($converterHelper, @SectionLabel)"/>
			<xsl:variable name="sectionSubtitle" select="java:asString($converterHelper, @SectionSubtitle)"/>
			<xsl:variable name="sectionPageNumber" select="java:asString($converterHelper, @SectionPageNumber)"/>
			<xsl:variable name="sectionInstructions" select="java:asString($converterHelper, @SectionInstructions)"/>
			<div class="section-title"><xsl:value-of select="java:getString($resourceBundleWords, 'section_title')"/>:<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="@SectionTitle" disable-output-escaping="yes"/></div>
			<xsl:if test="$sectionSubtitle != ''">
				<div class="section-info"><xsl:value-of select="java:getString($resourceBundleWords, 'subtitle')"/>:<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xls:value-of select="$sectionSubtitle" disable-output-escaping="yes"/></div>
			</xsl:if>
			<xsl:if test="$sectionInstructions != ''">
				<div class="section-info"><xsl:value-of select="java:getString($resourceBundleWords, 'instructions')"/>:<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xls:value-of select="$sectionInstructions" disable-output-escaping="yes"/></div>
			</xsl:if>
			<xsl:if test="$sectionPageNumber != ''">
				<div class="section-info"><xsl:value-of select="java:getString($resourceBundleWords, 'page')"/>:<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xls:value-of select="$sectionPageNumber" disable-output-escaping="yes"/></div>
			</xsl:if>
			<div class="vertical-spacer-20px"></div>
			<xsl:call-template name="crfFormItemDataTemplate">
				<xls:with-param name="formOID" select="$formOID"/>
				<xls:with-param name="sectionLabel" select="$sectionLabel"/>
				<xls:with-param name="studyEventOID" select="$studyEventOID"/>
				<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
			</xsl:call-template>
		</xsl:for-each>
		<xls:if test="$formVersionOID = '*'">
			<div class="page-break-screen"><hr/></div>
			<div class="page-break"></div>
			<xsl:call-template name="crfFormStatisticsTemplate">
				<xls:with-param name="formOID" select="$formOID"/>
				<xls:with-param name="studyEventOID" select="$studyEventOID"/>
				<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
			</xsl:call-template>
			<div class="page-break-screen"><hr/></div>
			<div class="page-break"></div>
		</xls:if>
	</xsl:template>
	<xsl:template name="crfFormItemDataTemplate">
		<xsl:param name="formOID"/>
		<xsl:param name="sectionLabel"/>
		<xsl:param name="studyEventOID"/>
		<xsl:param name="studyEventRepeatKey"/>
		<xsl:variable name="vars" select="java:initItemOIDList($converterHelper)"/>
		<xsl:variable name="vars" select="java:set($converterHelper, 'currentItemGroupOID', '')"/>
		<xsl:variable name="vars" select="java:set($converterHelper, 'startItemGroupTable', 'true')"/>
		<xsl:variable name="vars" select="java:set($converterHelper, 'closeItemGroupTable', 'false')"/>
		<xsl:variable name="vars" select="java:set($converterHelper, 'emptyItemGroupTable', 'false')"/>
		<xsl:variable name="vars" select="java:set($converterHelper, 'repeatingGroupTDWithoutTopBorder', '')"/>
		<xsl:variable name="studyEventData" select="/odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData[$studyEventOID = @StudyEventOID and $studyEventRepeatKey = @StudyEventRepeatKey]"/>
		<xsl:variable name="formData" select="/odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData[$studyEventOID = @StudyEventOID and $studyEventRepeatKey = @StudyEventRepeatKey]/odm:FormData[$formOID = @FormOID]"/>
		<xsl:for-each select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:ItemDef">
			<xls:variable name="itemDef" select="."/>
			<xls:variable name="itemOID" select="@OID"/>
			<xsl:variable name="itemPresentInForm" select="OpenClinica:ItemDetails/OpenClinica:ItemPresentInForm"/>
			<xsl:if test="$itemPresentInForm/OpenClinica:SectionLabel = $sectionLabel and $itemPresentInForm[@FormOID = $formOID]">
				<xsl:for-each select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:ItemGroupDef">
					<xsl:variable name="itemRef" select="odm:ItemRef"/>
					<xsl:variable name="itemGroupOID" select="@OID"/>
					<xsl:variable name="repeatingGroup" select="@Repeating"/>
					<xsl:if test="$itemRef[$itemOID = @ItemOID]">
						<xsl:variable name="itemName" select="$itemDef/@Name"/>
						<xsl:variable name="itemMandatory" select="$itemRef/@Mandatory"/>
						<xsl:variable name="codeListOID" select="$itemDef/odm:CodeListRef/@CodeListOID"/>
						<xsl:variable name="vars" select="java:set($converterHelper, 'additionalLayout', '')"/>
						<xsl:variable name="itemNumber" select="$itemDef/odm:Question/@OpenClinica:QuestionNumber"/>
						<xls:variable name="measurementUnitOID" select="$itemDef/odm:MeasurementUnitRef/@MeasurementUnitOID"/>
						<xsl:variable name="responseType" select="$itemPresentInForm/OpenClinica:ItemResponse/@ResponseType"/>
						<xsl:variable name="responseLayout" select="$itemPresentInForm/OpenClinica:ItemResponse/@ResponseLayout"/>
						<xsl:variable name="multiSelectListID" select="$itemDef/OpenClinica:MultiSelectListRef/@MultiSelectListID"/>
						<xsl:variable name="itemHeader" select="java:asString($converterHelper, $itemPresentInForm/OpenClinica:ItemHeader)"/>
						<xsl:variable name="itemSubHeader" select="java:asString($converterHelper, $itemPresentInForm/OpenClinica:ItemSubHeader)"/>
						<xsl:variable name="codeList" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:CodeList[$codeListOID = @OID]/odm:CodeListItem"/>
						<xsl:variable name="measurementUnit" select="/odm:ODM/odm:Study[1]/odm:BasicDefinitions/odm:MeasurementUnit[$measurementUnitOID = @OID]"/>
						<xsl:variable name="multiSelectList" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/OpenClinica:MultiSelectList[$multiSelectListID = @ID]/OpenClinica:MultiSelectListItem"/>
						<xsl:if test="$responseLayout = 'Horizontal'">
							<xsl:variable name="vars" select="java:set($converterHelper, 'additionalLayout', 'inline')"/>
						</xsl:if>
						<xsl:if test="$repeatingGroup != 'Yes'">
							<xsl:variable name="itemData" select="$formData/odm:ItemGroupData[$itemGroupOID = @ItemGroupOID]/odm:ItemData[$itemOID = @ItemOID]"/>
							<xls:call-template name="itemGroupTemplate">
								<xls:with-param name="itemOID" select="$itemOID"/>
								<xls:with-param name="formOID" select="$formOID"/>
								<xls:with-param name="itemData" select="$itemData"/>
								<xls:with-param name="codeList" select="$codeList"/>
								<xls:with-param name="itemName" select="$itemName"/>
								<xls:with-param name="itemNumber" select="$itemNumber"/>
								<xls:with-param name="itemHeader" select="$itemHeader"/>
								<xls:with-param name="responseType" select="$responseType"/>
								<xls:with-param name="itemSubHeader" select="$itemSubHeader"/>
								<xls:with-param name="itemMandatory" select="$itemMandatory"/>
								<xls:with-param name="studyEventOID" select="$studyEventOID"/>
								<xls:with-param name="studyEventData" select="$studyEventData"/>
								<xls:with-param name="multiSelectList" select="$multiSelectList"/>
								<xls:with-param name="measurementUnit" select="$measurementUnit"/>
								<xls:with-param name="itemPresentInForm" select="$itemPresentInForm"/>
							</xls:call-template>
						</xsl:if>
						<xsl:if test="$repeatingGroup = 'Yes'">
							<xsl:variable name="vars" select="java:incGroupCounter($converterHelper, $itemGroupOID)"/>
							<xsl:variable name="startItemGroupTable" select="java:get($converterHelper, 'startItemGroupTable')"/>
							<xsl:variable name="repeatingGroupTDWithoutTopBorder" select="java:get($converterHelper, 'repeatingGroupTDWithoutTopBorder')"/>
							<xls:if test="$startItemGroupTable = 'true'">
								<xsl:variable name="vars" select="java:initItemOIDList($converterHelper)"/>
								<xsl:variable name="vars" select="java:set($converterHelper, 'closeItemGroupTable', 'true')"/>
								<xsl:variable name="vars" select="java:set($converterHelper, 'startItemGroupTable', 'false')"/>
								<xsl:variable name="vars" select="java:set($converterHelper, 'emptyItemGroupTable', 'true')"/>
								<xsl:variable name="vars" select="java:set($converterHelper, 'currentItemGroupOID', $itemGroupOID)"/>
								<xls:value-of select="java:startRepeatingGroupTable($converterHelper, $repeatingGroupTDWithoutTopBorder)" disable-output-escaping="yes"/>
							</xls:if>
							<td class="repeating_item_header {$repeatingGroupTDWithoutTopBorder}" valign="bottom">
								<xsl:if test="$itemName != ''">
									<a class="bold_item_def_name" href="#{$studyEventData/../@SubjectKey}/{$studyEventOID}[{$studyEventData/@StudyEventRepeatKey}]/{$formOID}/{$itemOID}"><xsl:value-of select="$itemName" disable-output-escaping="yes"/></a><br/>
								</xsl:if>
								<xsl:if test="$itemHeader != ''">
									<xsl:value-of select="$itemHeader" disable-output-escaping="yes"/>
								</xsl:if>
							</td>
							<xsl:variable name="vars" select="java:addItemOID($converterHelper, $itemOID)"/>
							<xls:if test="java:breakGroupItems($converterHelper, $itemGroupOID)">
								<xsl:variable name="vars" select="java:set($converterHelper, 'startItemGroupTable', 'true')"/>
								<xls:value-of select="java:closeRepeatingGroupTable($converterHelper)" disable-output-escaping="yes"/>
								<xls:call-template name="repeatingItemGroupTemplate">
									<xls:with-param name="formOID" select="$formOID"/>
									<xls:with-param name="studyEventOID" select="$studyEventOID"/>
									<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
								</xls:call-template>
								<xsl:variable name="vars" select="java:set($converterHelper, 'repeatingGroupTDWithoutTopBorder', 'no_border_top')"/>
							</xls:if>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:if>
		</xsl:for-each>
		<xsl:variable name="closeItemGroupTable" select="java:get($converterHelper, 'closeItemGroupTable')"/>
		<xls:if test="$closeItemGroupTable = 'true'">
			<xls:value-of select="java:closeRepeatingGroupTable($converterHelper)" disable-output-escaping="yes"/>
			<xls:call-template name="repeatingItemGroupTemplate">
				<xls:with-param name="formOID" select="$formOID"/>
				<xls:with-param name="studyEventOID" select="$studyEventOID"/>
				<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
			</xls:call-template>
		</xls:if>
	</xsl:template>
	<xsl:template name="itemGroupTemplate">
		<xls:param name="itemOID"/>
		<xls:param name="formOID"/>
		<xls:param name="itemData"/>
		<xls:param name="codeList"/>
		<xls:param name="itemName"/>
		<xls:param name="itemNumber"/>
		<xls:param name="itemHeader"/>
		<xls:param name="responseType"/>
		<xls:param name="itemSubHeader"/>
		<xls:param name="itemMandatory"/>
		<xls:param name="studyEventOID"/>
		<xls:param name="studyEventData"/>
		<xls:param name="multiSelectList"/>
		<xls:param name="measurementUnit"/>
		<xls:param name="itemPresentInForm"/>
		<xsl:if test="$itemHeader != ''">
			<div class="header-title"><xsl:value-of select="$itemHeader" disable-output-escaping="yes"/></div>
		</xsl:if>
		<xsl:if test="$itemSubHeader != ''">
			<div class="header-title"><xsl:value-of select="$itemSubHeader" disable-output-escaping="yes"/></div>
		</xsl:if>
		<table class="item-row">
			<tr>
				<td class="item-def-cell">
					<div class="item_def_wrapper">
						<table border="0">
							<tr class="min-height">
								<td>
									<xsl:if test="$itemName != ''">
										<a class="item_def_name" href="#{$studyEventData/../@SubjectKey}/{$studyEventOID}[{$studyEventData/@StudyEventRepeatKey}]/{$formOID}/{$itemOID}"><xsl:value-of select="$itemName" disable-output-escaping="yes"/></a><br/>
									</xsl:if>
									<xsl:if test="$itemNumber != ''">
										<span class="item_def_number"><xsl:value-of select="$itemNumber" disable-output-escaping="yes"/></span>
									</xsl:if>
									<span class="item_def_title">
										<xsl:value-of select="$itemPresentInForm/OpenClinica:LeftItemText"/>
										<xsl:if test="$itemMandatory = 'Yes'">
											<xsl:value-of select="' * '"/>
										</xsl:if>
									</span>
								</td>
								<xsl:if test="$responseType = 'file'">
									<td>
										<br/>
										<xls:variable name="fileName" select="java:getFileName($converterHelper, $itemData/@Value)"/>
										<div><a href="{$fileDownloadUrl}{$itemData/@Value}" title="{$fileName}"><xsl:value-of select="$fileName"/></a></div>
									</td>
								</xsl:if>
								<xsl:if test="$responseType = 'textarea' or $responseType = 'text' or $responseType = 'calculation' or $responseType = 'instant-calculation' or $responseType = 'group-calculation'">
									<td>
										<br/>
										<div id="{$formOID}-{$itemOID}"><xsl:value-of select="$itemData/@Value" disable-output-escaping="yes"/></div>
									</td>
								</xsl:if>
								<xsl:if test="$responseType = 'radio' or $responseType = 'single-select'">
									<td>
										<br/>
										<xls:call-template name="radioTemplate">
											<xls:with-param name="itemOID" select="$itemOID"/>
											<xls:with-param name="formOID" select="$formOID"/>
											<xls:with-param name="itemData" select="$itemData"/>
											<xls:with-param name="codeList" select="$codeList"/>
										</xls:call-template>
									</td>
								</xsl:if>
								<xsl:if test="$responseType = 'multi-select' or $responseType = 'checkbox'">
									<td>
										<br/>
										<xls:call-template name="checkboxTemplate">
											<xls:with-param name="itemOID" select="$itemOID"/>
											<xls:with-param name="formOID" select="$formOID"/>
											<xls:with-param name="itemData" select="$itemData"/>
											<xls:with-param name="multiSelectList" select="$multiSelectList"/>
										</xls:call-template>
									</td>
								</xsl:if>
								<td align="left">
									<br/>
									<span class="item_def_format_label">
										<xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/>
										<xsl:if test="$measurementUnit">
											(<xsl:value-of select="$measurementUnit/@Name" disable-output-escaping="yes"/>)
										</xsl:if>
									</span>
								</td>
								<td>
									<br/>
									<span class="item_def_format_label"><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="$itemPresentInForm/OpenClinica:RightItemText" disable-output-escaping="yes"/></span>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
		<div class="vertical-spacer-20px"></div>
	</xsl:template>
	<xsl:template name="radioTemplate">
		<xsl:param name="itemOID"/>
		<xsl:param name="formOID"/>
		<xsl:param name="itemData"/>
		<xsl:param name="codeList"/>
		<xls:variable name="additionalLayout" select="java:get($converterHelper, 'additionalLayout')"/>
		<div class='item_def_control {$additionalLayout}'>
			<xsl:for-each select="$codeList">
				<div class='select-option {$additionalLayout}'>
					<xsl:if test="@CodedValue = $itemData/@Value">
						<input id="{$formOID}-{$itemOID}" type="radio" checked="true"/><span style="text-decoration: underline;font-weight: bold;"><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="odm:Decode/odm:TranslatedText"/></span>
					</xsl:if>
					<xsl:if test="not($itemData/@Value) or @CodedValue != $itemData/@Value">
						<input id="{$formOID}-{$itemOID}" type="radio"/><span><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="odm:Decode/odm:TranslatedText"/></span>
					</xsl:if>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template name="checkboxTemplate">
		<xsl:param name="itemOID"/>
		<xsl:param name="formOID"/>
		<xsl:param name="itemData"/>
		<xsl:param name="multiSelectList"/>
		<xls:variable name="additionalLayout" select="java:get($converterHelper, 'additionalLayout')"/>
		<div class='item_def_control {$additionalLayout}'>
			<xsl:for-each select="$multiSelectList">
				<xsl:variable name="shouldBeChecked" select="java:shouldBeChecked($converterHelper, $itemData/@Value, @CodedOptionValue)"/>
				<div class='select-option {$additionalLayout}'>
					<xsl:if test="$shouldBeChecked">
						<input id="{$formOID}-{$itemOID}" type="checkbox" checked="true"/><span style="text-decoration: underline;font-weight: bold;"><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="odm:Decode/odm:TranslatedText"/></span>
					</xsl:if>
					<xsl:if test="not($itemData/@Value) or not($shouldBeChecked)">
						<input id="{$formOID}-{$itemOID}" type="checkbox"/><span><xsl:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/><xsl:value-of select="odm:Decode/odm:TranslatedText"/></span>
					</xsl:if>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>
	<xsl:template name="repeatingItemGroupTemplate">
		<xls:param name="formOID"/>
		<xls:param name="studyEventOID"/>
		<xls:param name="studyEventRepeatKey"/>
		<xsl:variable name="currentItemGroupOID" select="java:get($converterHelper, 'currentItemGroupOID')"/>
		<xls:if test="$currentItemGroupOID != ''">
			<xsl:variable name="formData" select="/odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData[$studyEventOID = @StudyEventOID and $studyEventRepeatKey = @StudyEventRepeatKey]/odm:FormData[$formOID = @FormOID]"/>
			<table border="1" class="repeating-group-table no_border_top " cellspacing="0" cellpadding="0">
				<xls:for-each select="$formData/odm:ItemGroupData[$currentItemGroupOID = @ItemGroupOID]">
					<xsl:sort select="@ItemGroupRepeatKey" order="ascending"/>
					<xls:variable name="itemGroupRepeatKey" select="@ItemGroupRepeatKey"/>
					<xsl:variable name="vars" select="java:set($converterHelper, 'emptyItemGroupTable', 'false')"/>
					<tr class="min-height">
						<xls:call-template name="repeatingItemGroupDataTemplate">
							<xls:with-param name="index" select="0"/>
							<xls:with-param name="formOID" select="$formOID"/>
							<xls:with-param name="studyEventOID" select="$studyEventOID"/>
							<xls:with-param name="itemGroupOID" select="$currentItemGroupOID"/>
							<xls:with-param name="itemGroupRepeatKey" select="$itemGroupRepeatKey"/>
							<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
						</xls:call-template>
					</tr>
				</xls:for-each>
			</table>
			<xls:if test="java:get($converterHelper, 'emptyItemGroupTable') = 'true'">
				<xls:value-of select="java:rowForEmptyRepeatingGroupTable($converterHelper, $currentItemGroupOID)" disable-output-escaping="yes"/>
			</xls:if>
		</xls:if>
	</xsl:template>
	<xsl:template name="repeatingItemGroupDataTemplate">
		<xls:param name="index"/>
		<xls:param name="formOID"/>
		<xls:param name="itemGroupOID"/>
		<xls:param name="studyEventOID"/>
		<xls:param name="itemGroupRepeatKey"/>
		<xls:param name="studyEventRepeatKey"/>
		<xsl:variable name="vars" select="java:set($converterHelper, 'additionalLayout', '')"/>
		<xls:variable name="itemOID" select="java:getItemOID($converterHelper, string($index))"/>
		<xsl:variable name="itemDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:ItemDef[$itemOID = @OID]"/>
		<xsl:variable name="itemPresentInForm" select="$itemDef/OpenClinica:ItemDetails/OpenClinica:ItemPresentInForm[$formOID = @FormOID]"/>
		<xsl:variable name="codeListOID" select="$itemDef/odm:CodeListRef/@CodeListOID"/>
		<xsl:variable name="responseType" select="$itemPresentInForm/OpenClinica:ItemResponse/@ResponseType"/>
		<xsl:variable name="responseLayout" select="$itemPresentInForm/OpenClinica:ItemResponse/@ResponseLayout"/>
		<xsl:variable name="multiSelectListID" select="$itemDef/OpenClinica:MultiSelectListRef/@MultiSelectListID"/>
		<xsl:variable name="codeList" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:CodeList[$codeListOID = @OID]/odm:CodeListItem"/>
		<xsl:variable name="multiSelectList" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/OpenClinica:MultiSelectList[$multiSelectListID = @ID]/OpenClinica:MultiSelectListItem"/>
		<xsl:variable name="formData" select="/odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData[$studyEventOID = @StudyEventOID and $studyEventRepeatKey = @StudyEventRepeatKey]/odm:FormData[$formOID = @FormOID]"/>
		<xsl:if test="$responseLayout = 'Horizontal'">
			<xsl:variable name="vars" select="java:set($converterHelper, 'additionalLayout', 'inline')"/>
		</xsl:if>
		<xls:if test="$itemOID != ''">
			<td>
				<xls:for-each select="$formData/odm:ItemGroupData[$itemGroupOID = @ItemGroupOID and $itemGroupRepeatKey = @ItemGroupRepeatKey]/odm:ItemData[$itemOID = @ItemOID]">
					<xsl:variable name="itemData" select="."/>
					<xsl:variable name="vars" select="java:set($converterHelper, 'repeatingGroupItemValue', @Value)"/>
					<xls:if test="$itemDef/@DataType = 'date' or $itemDef/@DataType = 'partialDate'">
						<xsl:variable name="vars" select="java:set($converterHelper, 'repeatingGroupItemValue', java:formatSystemDate($converterHelper, @Value, $dateFormat, $locale))"/>
					</xls:if>
					<xsl:variable name="repeatingGroupItemValue" select="java:get($converterHelper, 'repeatingGroupItemValue')"/>

					<div class="item_data_div no_border_top">

						<xsl:if test="$responseType = 'file'">
							<xls:variable name="fileName" select="java:getFileName($converterHelper, $repeatingGroupItemValue)"/>
							<a href="{$fileDownloadUrl}{$repeatingGroupItemValue}" title="{$fileName}"><xsl:value-of select="$fileName"/></a>
						</xsl:if>

						<xsl:if test="$responseType = 'textarea' or $responseType = 'text' or $responseType = 'calculation' or $responseType = 'instant-calculation' or $responseType = 'group-calculation'">
							<xls:value-of select="$repeatingGroupItemValue"/>
						</xsl:if>

						<xsl:if test="$responseType = 'radio' or $responseType = 'single-select'">
							<xls:call-template name="radioTemplate">
								<xls:with-param name="itemOID" select="$itemOID"/>
								<xls:with-param name="formOID" select="$formOID"/>
								<xls:with-param name="itemData" select="$itemData"/>
								<xls:with-param name="codeList" select="$codeList"/>
							</xls:call-template>
						</xsl:if>

						<xsl:if test="$responseType = 'multi-select' or $responseType = 'checkbox'">
							<xls:call-template name="checkboxTemplate">
								<xls:with-param name="itemOID" select="$itemOID"/>
								<xls:with-param name="formOID" select="$formOID"/>
								<xls:with-param name="itemData" select="$itemData"/>
								<xls:with-param name="multiSelectList" select="$multiSelectList"/>
							</xls:call-template>
						</xsl:if>

					</div>

				</xls:for-each>
			</td>
		</xls:if>
		<xls:if test="$index != 3">
			<xls:call-template name="repeatingItemGroupDataTemplate">
				<xls:with-param name="index" select="$index + 1"/>
				<xls:with-param name="formOID" select="$formOID"/>
				<xls:with-param name="itemGroupOID" select="$itemGroupOID"/>
				<xls:with-param name="studyEventOID" select="$studyEventOID"/>
				<xls:with-param name="itemGroupRepeatKey" select="$itemGroupRepeatKey"/>
				<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
			</xls:call-template>
		</xls:if>
	</xsl:template>
	<xsl:template name="crfFormStatisticsTemplate">
		<xls:param name="formOID"/>
		<xls:param name="studyEventOID"/>
		<xls:param name="studyEventRepeatKey"/>
		<xsl:variable name="crfFormId" select="concat($studyEventOID, '/', $studyEventRepeatKey, '/', $formOID)"/>
		<xsl:variable name="formDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:FormDef[$formOID = @OID]"/>
		<xsl:variable name="formattedInterviewDate" select="java:formatSystemDate($converterHelper, @OpenClinica:InterviewDate, $dateFormat, $locale)"/>
		<xsl:variable name="formData" select="/odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData[$studyEventOID = @StudyEventOID and $studyEventRepeatKey = @StudyEventRepeatKey]/odm:FormData[$formOID = @FormOID]"/>
		<xsl:call-template name="headerTemplate">
			<xsl:with-param name="pageType" select="''"/>
			<xsl:with-param name="studyEvent" select=".."/>
			<xsl:with-param name="renderMode" select="''"/>
			<xsl:with-param name="formStatus" select="@OpenClinica:Status"/>
			<xsl:with-param name="interviewDate" select="$formattedInterviewDate"/>
			<xsl:with-param name="interviewerName" select="@OpenClinica:InterviewerName"/>
		</xsl:call-template>
		<h3 class="centered text_centered" style="margin-bottom:30px"><a id="{$crfFormId}"><xsl:value-of select="$formDef/@Name"/></a></h3>
		<xls:call-template name="dnsTemplate">
			<xls:with-param name="headerTDId" select="''"/>
			<xls:with-param name="itemGroupRepeatKey" select="''"/>
			<xls:with-param name="tableClass" select="'eventTable'"/>
			<xls:with-param name="headerTRclass" select="'AL_DNBackgroundColorForHeaders'"/>
			<xls:with-param name="headerFirstPart" select="java:getString($resourceBundleWords, 'interviewer_name')"/>
			<xls:with-param name="discrepancyNotes" select="$formData/OpenClinica:DiscrepancyNotes[$formData/@FormOID = @EntityID]/OpenClinica:DiscrepancyNote['interviewer_name' = @EntityName]"/>
		</xls:call-template>
		<xls:call-template name="dnsTemplate">
			<xls:with-param name="headerTDId" select="''"/>
			<xls:with-param name="itemGroupRepeatKey" select="''"/>
			<xls:with-param name="tableClass" select="'eventTable'"/>
			<xls:with-param name="headerTRclass" select="'AL_DNBackgroundColorForHeaders'"/>
			<xls:with-param name="headerFirstPart" select="java:getString($resourceBundleWords, 'interview_date')"/>
			<xls:with-param name="discrepancyNotes" select="$formData/OpenClinica:DiscrepancyNotes[$formData/@FormOID = @EntityID]/OpenClinica:DiscrepancyNote['date_interviewed' = @EntityName]"/>
		</xls:call-template>
		<xls:call-template name="auditHistoryTemplate">
			<xls:with-param name="headerTDId" select="''"/>
			<xls:with-param name="itemGroupRepeatKey" select="''"/>
			<xls:with-param name="tableClass" select="'eventTable'"/>
			<xls:with-param name="headerTRclass" select="'AL_DNBackgroundColorForHeaders'"/>
			<xls:with-param name="headerFirstPart" select="java:getString($resourceBundleWords, 'event_crf')"/>
			<xls:with-param name="auditLogs" select="$formData/OpenClinica:AuditLogs[$formData/@FormOID = @EntityID]/OpenClinica:AuditLog"/>
		</xls:call-template>
		<xls:call-template name="itemMetadataTemplate">
			<xls:with-param name="formOID" select="$formOID"/>
			<xls:with-param name="studyEventOID" select="$studyEventOID"/>
			<xls:with-param name="studyEventRepeatKey" select="$studyEventRepeatKey"/>
		</xls:call-template>
	</xsl:template>
	<xls:template name="dnsTemplate">
		<xls:param name="headerTDId"/>
		<xls:param name="tableClass"/>
		<xls:param name="headerTRclass"/>
		<xls:param name="headerFirstPart"/>
		<xls:param name="discrepancyNotes"/>
		<xls:param name="itemGroupRepeatKey"/>
		<xls:if test="$includeDNs and count($discrepancyNotes) != 0">
			<table class="{$tableClass}">
				<tr class="{$headerTRclass}">
					<td colspan="8" id="{$headerTDId}">
						<xsl:value-of select="$headerFirstPart"/>
						<xls:if test="$itemGroupRepeatKey != ''">
							<xls:value-of select="$itemGroupRepeatKey"/>
						</xls:if>: <xsl:value-of select="java:getString($resourceBundleWords, 'notes_and_discrepancies')"/></td>
				</tr>
				<tr class="provenance">
					<td id="eventNoteLabel">
						<strong><xsl:value-of select="java:getString($resourceBundleWords, 'note')"/>:</strong>
					</td>
					<td id="eventAssignLabel">
						<strong><xsl:value-of select="java:getString($resourceBundleWords, 'assigned_to')"/>:</strong>
					</td>
					<td id="eventIdLabel">
						<strong><xsl:value-of select="java:getString($resourceBundleWords, 'id')"/>:</strong>
					</td>
					<td id="eventTypeLabel">
						<strong><xsl:value-of select="java:getString($resourceBundleWords, 'type')"/>:</strong>
					</td>
					<td id="eventStatusLabel">
						<strong><xsl:value-of select="java:getString($resourceBundleWords, 'current_status')"/>:</strong>
					</td>
					<td id="eventNumLabel">
						<strong><xsl:value-of select="java:getString($resourceBundleWords, 'n_of_notes')"/>:</strong>
					</td>
					<td id="eventDateLabel">
						<strong><xsl:value-of select="java:getString($resourceBundleWords, 'created_date')"/>:</strong>
					</td>
					<td id="eventByLabel" class="right">
						<strong><xsl:value-of select="java:getString($resourceBundleWords, 'created_by')"/>:</strong>
					</td>
				</tr>
				<xls:for-each select="$discrepancyNotes">
					<tr class="DNParentRowColor">
						<td id="eventDescription">
							<strong><xls:value-of select="OpenClinica:ChildNote[1]/OpenClinica:Description"/></strong>
							<p class="dn_notes"></p>
						</td>
						<td id="eventUser"></td>
						<td id="eventID"><xls:value-of select="java:replace($converterHelper, @ID, 'DN_', '')"/></td>
						<td id="eventType"><xls:value-of select="@NoteType"/></td>
						<td id="eventStatus"><xls:value-of select="@Status"/></td>
						<td id="eventNum"><xls:value-of select="@NumberOfChildNotes"/></td>
						<td id="eventDate"><xsl:value-of select="java:formatSystemDate($converterHelper, @DateUpdated, $dateFormat, $locale)"/></td>
						<td id="eventBy" class="right"></td>
					</tr>
					<xls:for-each select="OpenClinica:ChildNote">
						<tr class="provenance">
							<td id="eventDescription">
								<strong><xls:value-of select="OpenClinica:Description"/></strong>
								<p class="dn_notes"><xls:value-of select="OpenClinica:DetailedNote"/></p>
							</td>
							<td id="eventUser">
								<xls:value-of select="odm:UserRef/@OpenClinica:FullName"/>
								<xls:if test="odm:UserRef/@OpenClinica:UserName">
									<xls:value-of select="concat(' (', odm:UserRef/@OpenClinica:UserName, ') ')"/>
								</xls:if>
							</td>
							<td id="eventID"><xls:value-of select="java:replace($converterHelper, @ID, 'CDN_', '')"/></td>
							<td id="eventType"></td>
							<td id="eventStatus"><xls:value-of select="@Status"/></td>
							<td id="eventNum"></td>
							<td id="eventDate"><xsl:value-of select="java:formatSystemDate($converterHelper, @DateCreated, $dateFormat, $locale)"/></td>
							<td id="eventBy" class="right">
								<xls:value-of select="@Name"/>
								<xls:if test="@UserName">
									<xls:value-of select="concat(' (', @UserName, ') ')"/>
								</xls:if>
							</td>
						</tr>
					</xls:for-each>
				</xls:for-each>
			</table>
		</xls:if>
	</xls:template>
	<xls:template name="auditHistoryTemplate">
		<xls:param name="auditLogs"/>
		<xls:param name="headerTDId"/>
		<xls:param name="tableClass"/>
		<xls:param name="headerTRclass"/>
		<xls:param name="headerFirstPart"/>
		<xls:param name="itemGroupRepeatKey"/>
		<xls:if test="$includeAudits and count($auditLogs) != 0">
			<table class="{$tableClass}">
				<tr class="{$headerTRclass}">
					<td colspan="6" id="{$headerTDId}">
						<xsl:value-of select="$headerFirstPart"/>
						<xls:if test="$itemGroupRepeatKey != ''">
							<xls:value-of select="$itemGroupRepeatKey"/>
						</xls:if>: <xls:value-of select="java:getString($resourceBundleWords, 'Audit_History')"/></td>
				</tr>
				<tr class="provenance">
					<td id="eventEventLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'audit_event')"/>:</strong></td>
					<td id="eventTimeLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'date_time_of_server')"/>:</strong></td>
					<td id="eventUsrLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'user')"/>:</strong></td>
					<td id="eventValueLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'value_type')"/>:</strong></td>
					<td id="eventOldLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'old')"/>:</strong></td>
					<td id="eventNewLabel" class="right"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'new')"/>:</strong></td>
				</tr>
				<xls:for-each select="$auditLogs">
					<tr class="provenance">
						<td id="eventAuditType"> <xls:value-of select="@AuditType"/></td>
						<td id="eventTime"><xsl:value-of select="java:formatSystemDateTime($converterHelper, string(@DateTimeStamp), $dateTimeFormat, $locale)"/></td>
						<td id="eventUsr">
							<xls:value-of select="@Name"/>
							<xls:if test="@UserName">
								<xls:value-of select="concat(' (', @UserName, ') ')"/>
							</xls:if>
						</td>
						<td id="eventValue"><xls:value-of select="@ValueType"/></td>
						<td id="eventOld"><xls:value-of select="@OldValue"/></td>
						<td id="eventNew" class="right"><xls:value-of select="@NewValue"/></td>
					</tr>
				</xls:for-each>
			</table>
		</xls:if>
	</xls:template>
	<xls:template name="itemMetadataTemplate">
		<xls:param name="formOID"/>
		<xls:param name="studyEventOID"/>
		<xls:param name="studyEventRepeatKey"/>
		<xsl:variable name="formDef" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:FormDef[$formOID = @OID]"/>
		<xsl:for-each select="$formDef/OpenClinica:FormDetails/OpenClinica:SectionDetails/OpenClinica:Section">
			<xsl:variable name="sectionLabel" select="@SectionLabel"/>
			<xsl:for-each select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:ItemDef">
				<xls:variable name="itemDef" select="."/>
				<xls:variable name="itemOID" select="@OID"/>
				<xsl:variable name="itemPresentInForm" select="OpenClinica:ItemDetails/OpenClinica:ItemPresentInForm"/>
				<xsl:if test="$itemPresentInForm/OpenClinica:SectionLabel = $sectionLabel and $itemPresentInForm[@FormOID = $formOID]">
					<xsl:for-each select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:ItemGroupDef">
						<xsl:variable name="itemRef" select="odm:ItemRef"/>
						<xsl:variable name="itemGroupOID" select="@OID"/>
						<xsl:variable name="repeatingGroup" select="@Repeating"/>
						<xsl:if test="$itemRef[$itemOID = @ItemOID]">
							<xsl:variable name="codeListOID" select="$itemDef/odm:CodeListRef/@CodeListOID"/>
							<xls:variable name="measurementUnitOID" select="$itemDef/odm:MeasurementUnitRef/@MeasurementUnitOID"/>
							<xsl:variable name="multiSelectListID" select="$itemDef/OpenClinica:MultiSelectListRef/@MultiSelectListID"/>
							<xsl:variable name="codeList" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/odm:CodeList[$codeListOID = @OID]/odm:CodeListItem"/>
							<xsl:variable name="measurementUnit" select="/odm:ODM/odm:Study[1]/odm:BasicDefinitions/odm:MeasurementUnit[$measurementUnitOID = @OID]"/>
							<xsl:variable name="multiSelectList" select="/odm:ODM/odm:Study[1]/odm:MetaDataVersion/OpenClinica:MultiSelectList[$multiSelectListID = @ID]/OpenClinica:MultiSelectListItem"/>
							<xsl:variable name="formData" select="/odm:ODM/odm:ClinicalData/odm:SubjectData/odm:StudyEventData[$studyEventOID = @StudyEventOID and $studyEventRepeatKey = @StudyEventRepeatKey]/odm:FormData[$formOID = @FormOID]"/>

							<table class="metadataTable">
								<tr>
									<td colspan="7" id="MetadataBackgroundColorForHeaders"><a id="{$studySubject/@SubjectKey}/{$studyEventOID}[{$studyEventRepeatKey}]/{$formOID}/{$itemOID}"></a><xls:value-of select="$itemDef/@Name"/>: <xsl:value-of select="java:getString($resourceBundleWords, 'metadata')"/></td>
								</tr>
								<tr class="provenance">
									<td id="left_itemLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'left_item_text')"/>:</strong></td>
									<td id="unitLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'units')"/>:</strong></td>
									<td id="typeLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'data_type')"/>:</strong></td>
									<td id="optTextLabel"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'response_options_text')"/> (<xsl:value-of select="java:getString($resourceBundleWords, 'response_values')"/>):</strong></td>
									<td id="groupLabel" class="right"><strong><xsl:value-of select="java:getString($resourceBundleWords, 'rule_group_label')"/>:</strong></td>
								</tr>
								<tr class="provenance">
									<td id="left_item"> <xls:value-of select="$itemPresentInForm/OpenClinica:LeftItemText" disable-output-escaping="yes"/> </td>
									<td id="units"><xls:value-of select="$measurementUnit" disable-output-escaping="yes"/></td>
									<td id="type"><xls:value-of select="$itemDef/@DataType"/></td>
									<td id="option">
										<xls:if test="count($codeList) != 0">
											<xls:for-each select="$codeList">
												<xls:value-of select="odm:Decode/odm:TranslatedText"/><xls:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/>(<xls:value-of select="@CodedValue"/>)<br/>
											</xls:for-each>
										</xls:if>
										<xls:if test="count($multiSelectList) != 0">
											<xls:for-each select="$multiSelectList">
												<xls:value-of select="odm:Decode/odm:TranslatedText"/><xls:value-of select="java:nbsp($converterHelper)" disable-output-escaping="yes"/>(<xls:value-of select="@CodedOptionValue"/>)<br/>
											</xls:for-each>
										</xls:if>
									</td>
									<td id="group" class="right"><xls:value-of select="$itemGroupOID"/></td>
								</tr>
							</table>

							<xls:for-each select="$formData/odm:ItemGroupData">
								<xls:if test="$itemGroupOID = @ItemGroupOID">
									<xsl:variable name="itemGroupRepeatKey" select="@ItemGroupRepeatKey"/>
									<xsl:variable name="itemData" select="odm:ItemData[$itemOID = @ItemOID]"/>
									<xsl:variable name="vars" select="java:set($converterHelper, 'itemGroupRepeatKey', '')"/>
									<xsl:if test="$repeatingGroup = 'Yes'">
										<xsl:variable name="vars" select="java:set($converterHelper, 'itemGroupRepeatKey', concat('(', $itemGroupRepeatKey, ')'))"/>
									</xsl:if>
									<xsl:variable name="itemGroupRepeatKey" select="java:get($converterHelper, 'itemGroupRepeatKey')"/>
									<xls:call-template name="dnsTemplate">
										<xls:with-param name="headerTRclass" select="''"/>
										<xls:with-param name="tableClass" select="'itemTable'"/>
										<xls:with-param name="headerFirstPart" select="$itemDef/@Name"/>
										<xls:with-param name="itemGroupRepeatKey" select="$itemGroupRepeatKey"/>
										<xls:with-param name="headerTDId" select="'DNBackgroundColorForHeaders'"/>
										<xls:with-param name="discrepancyNotes" select="$itemData/OpenClinica:DiscrepancyNotes[$itemOID = @EntityID]/OpenClinica:DiscrepancyNote"/>
									</xls:call-template>
								</xls:if>
							</xls:for-each>

							<xls:for-each select="$formData/odm:ItemGroupData">
								<xls:if test="$itemGroupOID = @ItemGroupOID">
									<xsl:variable name="itemGroupRepeatKey" select="@ItemGroupRepeatKey"/>
									<xsl:variable name="itemData" select="odm:ItemData[$itemOID = @ItemOID]"/>
									<xsl:variable name="vars" select="java:set($converterHelper, 'itemGroupRepeatKey', '')"/>
									<xsl:if test="$repeatingGroup = 'Yes'">
										<xsl:variable name="vars" select="java:set($converterHelper, 'itemGroupRepeatKey', concat('(', $itemGroupRepeatKey, ')'))"/>
									</xsl:if>
									<xsl:variable name="itemGroupRepeatKey" select="java:get($converterHelper, 'itemGroupRepeatKey')"/>
									<xls:call-template name="auditHistoryTemplate">
										<xls:with-param name="headerTRclass" select="''"/>
										<xls:with-param name="tableClass" select="'itemTable'"/>
										<xls:with-param name="headerFirstPart" select="$itemDef/@Name"/>
										<xls:with-param name="itemGroupRepeatKey" select="$itemGroupRepeatKey"/>
										<xls:with-param name="headerTDId" select="'ALBackgroundColorForHeaders'"/>
										<xls:with-param name="auditLogs" select="$itemData/OpenClinica:AuditLogs[$itemOID = @EntityID]/OpenClinica:AuditLog"/>
									</xls:call-template>
								</xls:if>
							</xls:for-each>

						</xsl:if>
					</xsl:for-each>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>
	</xls:template>
</xsl:stylesheet>
