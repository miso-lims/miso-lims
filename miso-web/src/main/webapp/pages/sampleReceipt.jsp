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
  User: thankia
  Date: 01-Mar-2012
  Time: 15:09:06
--%>
<%@ include file="../header.jsp" %>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>

<script src="<c:url value='/scripts/datatables_utils.js?ts=${timestamp.time}'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/datatable.css'/>" type="text/css">

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      Receive Samples
      <button type="submit" class="fg-button ui-state-default ui-corner-all"
              onclick="Sample.ui.setSampleReceiveDate('#sample_pan');">
        Save
      </button>
    </h1>

    <form id="samples" commandName="sample" autocomplete="off" onsubmit="">
      <div>
        Barcode:<input type="text"
                       size="40"
                       id="searchSampleByBarcode"
                       name="searchSampleByBarcode">

        <span id="msgspan"></span>

        <h2>Sample Information</h2>
        <div id="sample_pan"></div>
        <br/>
        <br/>
      </div>
      <div id="pager"></div>
    </form>
  </div>
</div>
<script type="text/javascript">
  jQuery('#searchSampleByBarcode').keypress(function(e) {
    if (e.which == 13) return false;
  });

  Utils.timer.typewatchFunc(jQuery('#searchSampleByBarcode'), function() {
    Sample.ui.receiveSample(jQuery('#searchSampleByBarcode'));
  }, 600, 4);
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>