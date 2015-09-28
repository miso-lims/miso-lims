<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@taglib prefix="sessionConversation" uri="/WEB-INF/tld/sessionConversation.tld" %>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
  <meta http-equiv="Pragma" content="no-cache">
  <meta http-equiv="Expires" content="0">

  <%-- timestamp to force browser to reload javascript --%>
  <jsp:useBean id="timestamp" class="java.util.Date" scope="request"/>

  <!-- jQuery -->
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-1.11.1.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-migrate-1.2.1-min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-ui-1.9.2.custom.min.js?ts=${timestamp.time}'/>"></script>

  <!-- bootstrap -->
  <script src="<c:url value='/scripts/bootstrap/bootstrap.min.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <link href="<c:url value='/scripts/bootstrap/css/bootstrap.min.css'/>" rel="stylesheet" type="text/css">

  <!-- fonts awesome icons -->
  <link href="<c:url value='/styles/font-awesome/css/font-awesome.css'/>" rel="stylesheet">
  <link href="<c:url value='/styles/css/plugins/timeline/timeline.css'/>" rel="stylesheet">
  <link href="<c:url value='/styles/css/sb-admin.css'/>" rel="stylesheet">

  <!-- datatables -->
  <script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>">
  <script src="<c:url value='/scripts/jquery/datatables/js/datatables.bootstrap.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/datatables.bootstrap.css'/>">
  <script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <script src="<c:url value='/scripts/natural_sort.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.radio.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

  <!-- jquery UI TOTDO remove all this eventually for bootstrap -->
  <link rel="stylesheet" href="<c:url value='/scripts/jquery/css/smoothness/jquery-ui-1.9.2.custom.min.css'/>" type="text/css">
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.simplyCountable.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.tinysort.min.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.typewatch.js?ts=${timestamp.time}'/>"></script>
  <%-- <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.uitablefilter.js?ts=${timestamp.time}'/>"></script> --%>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.validate.min.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.tablesorter.min.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.metadata.js?ts=${timestamp.time}'/>"></script>

  <!-- colourbox overlay support -->
  <script type="text/javascript" src="<c:url value='/scripts/jquery/colorbox/jquery.colorbox-min-1.4.16.js'/>"></script>
  <link rel="stylesheet" href="<c:url value='/scripts/jquery/colorbox/colorbox-1.4.16.css'/>" type="text/css">

  <!-- select2 -->
  <script src="<c:url value='/scripts/select2/select2.min.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
  <link href="<c:url value='/scripts/select2/select2.css'/>" rel="stylesheet" type="text/css">
  <link href="<c:url value='/scripts/select2/select2-bootstrap.css'/>" rel="stylesheet" type="text/css">

  <!-- fluxion AJAX library for Spring bean calls -->
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/prototype.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/fluxion-ajax/fluxion-ajax.js?ts=${timestamp.time}'/>"></script>

  <!-- D3.js for Graphics -->
  <script type="text/javascript" src="<c:url value='/scripts/d3v2/d3.v2.min.js?ts=${timestamp.time}'/>"></script>

  <!-- unicode regexs for il8n validation -->
  <script type="text/javascript" src="<c:url value='/scripts/xregexp-all-min.js?ts=${timestamp.time}'/>"></script>

  <!-- MISO objects -->
  <script type="text/javascript" src="<c:url value='/scripts/lims.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/search.js?ts=${timestamp.time}'/>"></script>

  <script type="text/javascript" src="<c:url value='/scripts/entity_group_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/entity_group_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/experiment_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/library_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/plate_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/pool_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/printer_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/project_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/reporting_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/run_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/sample_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/sequencer_partition_container_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/sequencer_reference_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/study_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/task_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/workflow_ajax.js?ts=${timestamp.time}'/>"></script>

  <!-- form validations -->
  <script type="text/javascript" src="<c:url value='/scripts/experiment_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/library_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/pool_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/project_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/plate_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/run_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/sample_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/sequencer_partition_container_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/study_validation.js?ts=${timestamp.time}'/>"></script>

  <!--multi-select drag drop -->
  <script type="text/javascript" src="<c:url value='/scripts/multi_select_drag_drop.js?ts=${timestamp.time}'/>"></script>

  <!--drop down menu -->
  <script src="<c:url value='/scripts/menus.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

  <!-- high charts-->
  <script type="text/javascript" src="<c:url value='/scripts/highcharts/highcharts.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/highcharts/highcharts-more.js?ts=${timestamp.time}'/>"></script>

  <link rel="shortcut icon" href="<c:url value='/styles/images/favicon.ico'/>" type="image/x-icon"/>

  <!-- miso stylez -->
  <link href="<c:url value='/styles/style.css?ts=${timestamp.time}'/>" rel="stylesheet" type="text/css">
  <link href="<c:url value='/styles/miso-bootstrap-compat.css'/>" rel="stylesheet" type="text/css">

  <!--IE check-->
  <script type="text/javascript">
    jQuery(document).ready(function () {
      if (jQuery.browser.msie) {
        alert("Internet Explorer is not supported by MISO. Please use Google Chrome, Safari or Mozilla Firefox");
      }
    });
  </script>

  <title>MISO LIMS <c:if test="${not empty title}">- ${title}</c:if></title>
</head>

<body>
<nav class="navbar navbar-default navbar-fixed-top navbar-miso-header" role="navigation">
<div class="navbar-header">
  <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".sidebar-collapse">
    <span class="sr-only">Toggle navigation</span>
    <span class="icon-bar"></span>
    <span class="icon-bar"></span>
    <span class="icon-bar"></span>
  </button>
  <a class="navbar-brand" href="<c:url value="/"/>">
    <img src="<c:url value='/styles/images/miso_bowl1_logo-tm.png'/>" alt="MISO Logo" name="logo" border="0" id="misologo"/>
  </a>
</div>
<!-- /.navbar-header -->
<sec:authorize access="isAuthenticated()">
   <ul class="nav navbar-top-links navbar-right">
     <li class="dropdown">
      <a class="dropdown-toggle" data-toggle="dropdown" href="#">
        <i class="fa fa-tasks fa-fw"></i> <i class="fa fa-caret-down"></i>
      </a>
      <ul class="dropdown-menu dropdown-tasks">
        <li role="presentation" class="dropdown-header">Management</li>
        <c:if test="${sessionScope.analysisServerEnabled}">
          <li role="presentation"><a href="<c:url value="/miso/analysis"/>"><i class="fa fa-gears fa-fw"></i> Analysis</a></li>
        </c:if>
        <li role="presentation"><a href="<c:url value="/miso/flexreports"/>"><i class="fa fa-sliders fa-fw"></i> Reports</a></li>
      </ul>
    </li>
    <li class="dropdown">
      <a class="dropdown-toggle" data-toggle="dropdown" href="#">
        <i class="fa fa-bell fa-fw"></i> <i class="fa fa-caret-down"></i>
        <span id="alertMenuDropdown"></span>
      </a>
      <ul class="dropdown-menu dropdown-alerts" id="alertList"></ul>
    </li>

    <li class="dropdown">
      <a class="dropdown-toggle" data-toggle="dropdown" href="#">
        <i class="fa fa-user fa-fw"></i>  <i class="fa fa-caret-down"></i>
      </a>
      <ul class="dropdown-menu dropdown-user">
        <sec:authorize access="isAuthenticated()">
          <li id="currentUser" role="presentation" class="dropdown-header"><sec:authentication property="principal.username"/></li>
          <li><a href="<c:url value="/miso/myAccount"/>"><i class="fa fa-gear fa-fw"></i> My Account</a></li>
          <li><a target="_blank" href="https://documentation.tgac.ac.uk/display/MISO/MISO+0.2.0+User+Manual"><i class="fa fa-question fa-fw"></i> Help</a></li>
          <li class="divider"></li>
          <li><a href="<c:url value="/j_spring_security_logout"/>"><i class="fa fa-sign-out fa-fw"></i> Logout</a></li>
        </sec:authorize>
      </ul>
    </li>
  </ul>
</sec:authorize>
</nav>

<div id="content">
<c:if test="${not empty error}">
  <br/>

  <div class="flasherror">${error}</div>
  <br/>
</c:if>
<sec:authorize access="isAuthenticated()">
  <script type="text/javascript">
    jQuery(function () {
      //all hover and click logic for buttons
      jQuery(".fg-button:not(.ui-state-disabled)")
        .hover(
        function () {
          jQuery(this).addClass("ui-state-hover");
        },
        function () {
          jQuery(this).removeClass("ui-state-hover");
        }
      )
    });

    Utils.alert.checkAlerts();

    jQuery(document).ready(function () {
      jQuery('.misoicon').hover(
        function () {
          jQuery(this).addClass('misoicon-hover');
        },
        function () {
          jQuery(this).removeClass('misoicon-hover');
        }
      );
    });

    //bootstrap prototype fix
    var isBootstrapEvent = false;
    if (window.jQuery) {
      var all = jQuery('*');
      jQuery.each(['hide.bs.dropdown',
        'hide.bs.collapse',
        'hide.bs.modal',
        'hide.bs.tooltip',
        'hide.bs.popover'], function(index, eventName) {
        all.on(eventName, function( event ) {
          isBootstrapEvent = true;
        });
      });
    }
    var originalHide = Element.hide;
    Element.addMethods({
      hide: function(element) {
        if(isBootstrapEvent) {
          isBootstrapEvent = false;
          return element;
        }
        return originalHide(element);
      }
    });
  </script>
</sec:authorize>