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

<div id="maincontent">
  <div id="contentcolumn">
    <nav class="navbar navbar-default" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">
          Receive Samples
        </span>
      </div>
      <div class="navbar-right container-fluid">
        <button class="btn btn-default navbar-btn" href='javascript:void(0);' onclick="Sample.ui.setSampleReceiveDate('#sample_pan');">Save</button>
      </div>
    </nav>

    <form id="samples" commandName="sample" autocomplete="off" onsubmit="">
      <div class="col-sm-12 col-md-12 col-ld-12 small-pad">
        <div class="panel panel-primary panel-dashboard">
          <div class="panel-heading">
            <h3 class="panel-title pull-left">Enter Barcode</h3>
            <input type="text" size="40" id="searchSampleByBarcode" name="searchSampleByBarcode" class="form-control pull-right"/>
          </div>
          <div class="panel-body">
            <span id="msgspan"></span>
            <div id="sample_pan"></div>
            <div id="pager"></div>
          </div>
        </div>
      </div>
    </form>
  </div>
</div>
<script type="text/javascript">
  jQuery('#searchSampleByBarcode').keypress(function (e) {
    if (e.which == 13) return false;
  });

  Utils.timer.typewatchFunc(jQuery('#searchSampleByBarcode'), function () {
    Sample.ui.receiveSample(jQuery('#searchSampleByBarcode'));
  }, 400, 4);
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>