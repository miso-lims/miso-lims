<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey @ TGAC
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

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta http-equiv='cache-control' content='no-cache'>
    <meta http-equiv='expires' content='0'>
    <meta http-equiv='pragma' content='no-cache'>

    <%-- timestamp to force browser to reload javascript --%>
    <jsp:useBean id="timestamp" class="java.util.Date" scope="request"/>

    <link rel="stylesheet" href="<c:url value='/styles/style.css'/>" type="text/css">
    <title>MISO LIMS External<c:if test="${not empty title}">- ${title}</c:if></title>
    <script type="text/javascript" src="<c:url value='/scripts/scriptaculous/prototype.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/fluxion-ajax/fluxion-ajax-compiled.js'/>"></script>

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
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-1.4.2.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery-ui-1.8.custom.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.simplyCountable.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.tinysort.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.tablesorter.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.uitablefilter.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.validate.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/jquery/colorbox/jquery.colorbox-min.js'/>"></script>
    <link rel="stylesheet" href="<c:url value='/scripts/jquery/css/smoothness/jquery-ui-1.8.custom.css'/>"
          type="text/css">
    <link rel="stylesheet" href="<c:url value='/scripts/jquery/colorbox/colorbox.css'/>"
          type="text/css">


    <!-- LIMS -->
    <script type="text/javascript" src="<c:url value='/scripts/lims.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/external.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/search.js'/>"></script>

    <!--CryptoJS-->
    <script type="text/javascript" src="<c:url value='/scripts/sha1.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/scripts/enc-base64-min.js'/>"></script>


    <!-- give $ back to prototype -->
    <script type="text/javascript">jQuery.noConflict();</script>

    <script type="text/javascript" src="<c:url value='/scripts/multi_select_drag_drop.js'/>"></script>

    <link rel="shortcut icon" href="<c:url value='/styles/images/favicon.ico'/>" type="image/x-icon"/>

    <!--drop down menu -->

    <script src="<c:url value='/scripts/menus.js'/>" type="text/javascript"></script>

    <!-- refresh page every 30mins and 1 sec -->
    <!--<meta http-equiv="refresh" content="1801"/>-->

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
                 border="0" id="brandlogo" />

        </td>
    </tr>
</table>

<script type="text/javascript">
    setBaseUrl("<c:url value="/"/>");
    jQuery(function() {
        //all hover and click logic for buttons
        jQuery(".fg-button:not(.ui-state-disabled)")
                .hover(
                function() {
                    jQuery(this).addClass("ui-state-hover");
                },
                function() {
                    jQuery(this).removeClass("ui-state-hover");
                })
    });
</script>

<div id="navtabs">
    <%--<ul>--%>
            <%--<li><a href="<c:url value="/"/>"><span>Home</span></a></li>--%>

    <%--</ul>--%>

</div>

<div id="content">
<c:if test="${not empty error}">
    <br/>

    <div class="flasherror">${error}</div>
    <br/>
</c:if>