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

<%--
  Created by IntelliJ IDEA.
  User: bianx
  
--%>
<%@ include file="../header.jsp" %>

<script type="text/javascript" src="<c:url value='/scripts/printer_ajax.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">
  <div id="contentcolumn">
    <h1>Custom Barcode Printing</h1>

    <div class="portlet">
      <div class="portlet-header">Custom 2D Barcode</div>
      <div class="portlet-content">

        <div id="form" style="margin-top:20px;margin-let:20px;">
          <table border="0">
            <tr>
              <td>Line 1:</td>
              <td><input type="text" id="customPrintLine1" size="30" maxlength="17"/></td>
              <td><span id="line1counter"
                        class="counter"></span></td>
              <td>Generate barcode for this line?<input id="barcodeit" type="checkbox"/></td>
            </tr>
            <tr>
              <td>Line 2:</td>
              <td><input type="text" id="customPrintLine2" size="30" maxlength="15"/></td>
              <td> <span id="line2counter"
                         class="counter"></span></td>
            </tr>
            <tr>
              <td>Line 3:</td>
              <td><input type="text" id="customPrintLine3" size="30" maxlength="17"/></td>
              <td><span id="line3counter"
                        class="counter"></span></td>
            </tr>
          </table>
          <button onclick="Print.service.printCustomBarcodes();">Print</button>
        </div>
      </div>
    </div>

    <div class="portlet">
      <div class="portlet-header">Custom 1D Barcode</div>
      <div class="portlet-content">

        <div id="form2" style="margin-top:20px;margin-let:20px;">
          <table border="0">
            <tr>
              <td>Text:</td>
              <td><input type="text" id="custom1DPrintLine1" size="30" maxlength="10"/></td>
              <td><span id="counter1D"
                        class="counter"></span></td>
            </tr>
            <tr>
              <td>How Many?:</td>
              <td><input type="text" id="custom1DPrintLine2" size="5" maxlength="2" value="1"/></td>
            </tr>
          </table>
          <button onclick="Print.service.printCustom1DBarcodes();">Print</button>
        </div>
      </div>
    </div>
    <div id="printServiceSelectDialog" title="Select a Printer"></div>
  </div>
</div>

<style type="text/css">
  .column {
    width: 50%;
    float: left;
    padding-bottom: 100px;
    margin-top: 4px;
  }

  .portlet {
    margin: 0 1em 1em 0;
  }

  .portlet-header {
    margin: 0.3em;
    padding-bottom: 4px;
    padding-left: 0.2em;
  }

  .portlet-header .ui-icon {
    float: right;
  }

  .portlet-content {
    padding: 0.4em;
  }

  .ui-sortable-placeholder {
    border: 1px dotted black;
    visibility: visible !important;
    height: 50px !important;
  }

  .ui-sortable-placeholder * {
    visibility: hidden;
  }

  .ui-widget-header {
    -moz-background-clip: border;
    -moz-background-inline-policy: continuous;
    -moz-background-origin: padding;
    background: #CCDDFF none repeat scroll 0 0;
    border: 1px solid #AAAAAA;
    color: #666666;
    font-weight: bold;
  }

  .ui-widget {
    font-family: Verdana, Arial, sans-serif;
    font-size: 1em;
  }
</style>

<script type="text/javascript">

  jQuery(document).ready(function () {
    jQuery('#customPrintLine1').simplyCountable({
      counter: '#line1counter',
      countType: 'characters',
      maxCount: 17,
      countDirection: 'down'
    });
    jQuery('#customPrintLine2').simplyCountable({
      counter: '#line2counter',
      countType: 'characters',
      maxCount: 15,
      countDirection: 'down'
    });
    jQuery('#customPrintLine3').simplyCountable({
      counter: '#line3counter',
      countType: 'characters',
      maxCount: 17,
      countDirection: 'down'
    });
    jQuery('#custom1DPrintLine1').simplyCountable({
      counter: '#counter1D',
      countType: 'characters',
      maxCount: 10,
      countDirection: 'down'
    });

    jQuery(".portlet").addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
        .find(".portlet-header")
        .addClass("ui-widget-header ui-corner-all")
        .prepend('<span class="ui-icon ui-icon-minusthick"></span>')
        .end()
        .find(".portlet-content");

    jQuery(".portlet-header .ui-icon").click(function () {
      jQuery(this).toggleClass("ui-icon-minusthick").toggleClass("ui-icon-plusthick");
      jQuery(this).parents(".portlet:first").find(".portlet-content").toggle();
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>

