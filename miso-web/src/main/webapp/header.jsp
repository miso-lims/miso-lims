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

  <title>MISO LIMS <c:if test="${not empty title}">- ${title}</c:if></title>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/prototype.js'/>"></script>
  <script type="text/javascript"
          src="<c:url value='/scripts/fluxion-ajax/fluxion-ajax.js?ts=${timestamp.time}'/>"></script>

  <!--Scriptaculous JS scripts below -->
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/scriptaculous.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/activityInput.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/effects.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/unittest.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/dragdrop.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/slider.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/builder.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/sound.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/controls.js'/>"></script>

  <!-- jQuery -->
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-1.8.3.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-ui-1.9.2.custom.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.simplyCountable.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.tinysort.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.typewatch.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.uitablefilter.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.validate.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/colorbox/jquery.colorbox-min-1.4.16.js'/>"></script>
  <link rel="stylesheet" href="<c:url value='/scripts/jquery/css/smoothness/jquery-ui-1.9.2.custom.min.css'/>"
        type="text/css">
  <link rel="stylesheet" href="<c:url value='/scripts/jquery/colorbox/colorbox-1.4.16.css'/>"
        type="text/css">
        <link rel="stylesheet" href="<c:url value='/styles/style.css?ts=${timestamp.time}'/>" type="text/css">
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.tablesorter.min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.metadata.js'/>"></script>

  <!-- Parsley -->
  <script type="text/javascript" src="<c:url value='/scripts/parsley/parsley.min.js'/>"></script>

  <!-- D3.js for Graphics -->
  <script type="text/javascript" src="<c:url value='/scripts/d3v2/d3.v2.min.js'/>"></script>

  <!-- unicode regexs for il8n validation -->
  <script type="text/javascript" src="<c:url value='/scripts/xregexp-all-min.js'/>"></script>

  <!-- MISO objects -->
  <script type="text/javascript" src="<c:url value='/scripts/lims.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/search.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/experiment_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/library_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/pool_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/project_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/reporting_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/sample_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/run_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/box_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript"
          src="<c:url value='/scripts/sequencer_partition_container_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript"
          src="<c:url value='/scripts/sequencer_reference_ajax.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/study_ajax.js?ts=${timestamp.time}'/>"></script>

  <!-- form validations -->
  <script type="text/javascript"
          src="<c:url value='/scripts/experiment_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/library_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/pool_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/project_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/sample_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/study_validation.js?ts=${timestamp.time}'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/box_validation.js?ts=${timestamp.time}'/>"></script>

  <!-- give $ back to prototype -->
  <script type="text/javascript">jQuery.noConflict();</script>

  <!--multi-select drag drop -->
  <script type="text/javascript"
          src="<c:url value='/scripts/multi_select_drag_drop.js?ts=${timestamp.time}'/>"></script>

  <!--drop down menu -->
  <script src="<c:url value='/scripts/menus.js?ts=${timestamp.time}'/>" type="text/javascript"></script>

  <!-- high charts-->
  <script type="text/javascript" src="<c:url value='/scripts/highcharts/highcharts.js'/>"></script>
  <script type="text/javascript" src="<c:url value='/scripts/highcharts/highcharts-more.js'/>"></script>

  <link rel="shortcut icon" href="<c:url value='/styles/images/favicon.ico'/>" type="image/x-icon"/>

    <!--IE check-->
    <script type="text/javascript">
        jQuery(document).ready(function () {
            if (jQuery.browser.msie) {
                alert("Internet Explorer is not supported by MISO. Please use Google Chrome, Safari or Mozilla Firefox");
            }
        });
    </script>
</head>

<body>
<table border="0" width="100%">
  <tr>
    <td class="headertable" align="left" onclick="window.location.href='<c:url value='/'/>'">
      <img src="<c:url value='/styles/images/miso_bowl1_logo-tm.png'/>" alt="MISO Logo" name="logo"
           border="0" id="misologo"/>
    </td>
    <td class="headertable" align="right" onclick="window.location.href='<c:url value='/'/>'">
      <img src="<c:url value='/styles/images/brand_logo.png'/>" alt="Brand Logo" name="logo"
           border="0" id="brandlogo"/>
    </td>
  </tr>
</table>

<div id="navtabs">
  <sec:authorize access="isAuthenticated()">
  <script type="text/javascript">
    <%-- checkUser('<sec:authentication property="principal.username"/>'); --%>
  </script>
  <ul>
    </sec:authorize>

    <sec:authorize access="isAuthenticated()">
      <li><a href="<c:url value="/miso/mainMenu"/>"><span>Home</span></a></li>
    </sec:authorize>

    <%--<sec:authorize access="hasRole('ROLE_ADMIN')">--%>
    <%--<li><a href="<c:url value="/miso/admin/menu"/>"><span>Admin</span></a></li>--%>
    <%--</sec:authorize>--%>

    <%--<sec:authorize access="hasRole('ROLE_TECH')">--%>
    <%--<li><a href="<c:url value="/miso/tech/menu"/>"><span>Tech</span></a></li>--%>
    <%--</sec:authorize>--%>

    <sec:authorize access="isAuthenticated()">
      <li>
        <a id="myAccountLink" href="<c:url value="/miso/myAccount"/>"><span id="myAccountSpan">My Account</span></a>
      </li>
    </sec:authorize>
    <sec:authorize access="isAuthenticated()">
      <li><a href="<c:url value="/miso/projects"/>"><span>My Projects</span></a></li>
    </sec:authorize>

    <sec:authorize access="hasRole('ROLE_INTERNAL')">
      <li><a href="<c:url value="/miso/analysis"/>"><span>Analysis</span></a></li>
    </sec:authorize>

    <sec:authorize access="hasRole('ROLE_INTERNAL')">
      <li><a href="<c:url value="/miso/flexreports"/>"><span>Reports</span></a></li>
    </sec:authorize>

    <sec:authorize access="isAuthenticated()">
      <li>
        <a href="<c:url value="https://documentation.tgac.ac.uk/display/MISO/"/>"><span>Help</span></a>
      </li>
    </sec:authorize>

    <sec:authorize access="isAuthenticated()">
  </ul>
  </sec:authorize>
</div>

<sec:authorize access="isAuthenticated()">
  <div id="loggedInBanner">Logged in as:
    <b id="currentUser"><sec:authentication property="principal.username"/></b> | <a
        href="<c:url value="/j_spring_security_logout"/>">Logout</a></div>
</sec:authorize>

<div id="content">
<c:if test="${not empty error}">
  <br/>

  <div class="flasherror">${error}</div>
  <br/>
</c:if>
<sec:authorize access="isAuthenticated()">
  <script type="text/javascript">
    setBaseUrl("<c:url value="/"/>");
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
  </script>
</sec:authorize>
