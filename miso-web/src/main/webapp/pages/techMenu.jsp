<%@ include file="../header.jsp" %>


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

<div id="maincontent">
    <div id="contentcolumn">
        <h1>Technician Menu</h1>
        <div class="column">
          <div class="portlet">
              <div class="portlet-header">Sequencing Machines</div>
              <div class="portlet-content">
                  <a href="<c:url value="/miso/stats/ls454"/>">454</a><br/>
                  <a href="<c:url value="/miso/stats/illumina"/>">Illumina</a><br/>
                  <a href="<c:url value="/miso/stats/solid"/>">SOLiD</a><br/><br/>
              </div>
          </div>
        </div>
    </div>
</div>

<style type="text/css">
    .column { width: 50%; float: left; padding-bottom: 100px; margin-top: 4px; }
    .portlet { margin: 0 1em 1em 0; }
    .portlet-header { margin: 0.3em; padding-bottom: 4px; padding-left: 0.2em; }
    .portlet-header .ui-icon { float: right; }
    .portlet-content { padding: 0.4em; }
    .ui-sortable-placeholder { border: 1px dotted black; visibility: visible !important; height: 50px !important; }
    .ui-sortable-placeholder * { visibility: hidden; }
    .ui-widget-header { -moz-background-clip:border; -moz-background-inline-policy:continuous;
                        -moz-background-origin:padding; background:#CCDDFF none repeat scroll 0 0;
                        border:1px solid #AAAAAA; color:#666666; font-weight:bold; }
    .ui-widget { font-family:Verdana,Arial,sans-serif; font-size:1em; }
</style>

<script type="text/javascript">
jQuery(function() {
    jQuery(".column").sortable({
        connectWith: '.column',
        handle: '.portlet-header'
    });

    jQuery(".portlet").addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
        .find(".portlet-header")
            .addClass("ui-widget-header ui-corner-all")
            .prepend('<span class="ui-icon ui-icon-minusthick"></span>')
            .end()
        .find(".portlet-content");

    jQuery(".portlet-header .ui-icon").click(function() {
        jQuery(this).toggleClass("ui-icon-minusthick").toggleClass("ui-icon-plusthick");
        jQuery(this).parents(".portlet:first").find(".portlet-content").toggle();
    });

    jQuery(".column").disableSelection();
});  
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>