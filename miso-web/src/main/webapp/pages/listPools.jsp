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

<%--
  Created by IntelliJ IDEA.
  User: bian, davey
  Date: 19-Apr-2010
  Updated: 06-Dec-2012
  Time: 14:22
--%>
<%@ include file="../header.jsp" %>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <h1>Pools</h1>

    <div id="tabs">
      <ul>
        <c:forEach items="${platformTypes}" var="pt" varStatus="c">
          <li><a href="#tab-${c.count}"><span>${pt} Pools</span></a></li>
        </c:forEach>
      </ul>

      <c:forEach items="${platformTypes}" var="pt" varStatus="c">
        <div id="tab-${c.count}">
          <h1>
            <span id="${pt}totalCount">${pt} Pools</span>
          </h1>
          <ul class="sddm">
            <li>
              <a onmouseover="mopen('ipomenu${c.count}')" onmouseout="mclosetime()">Options
                <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
              </a>

              <div id="ipomenu${c.count}"
                   onmouseover="mcancelclosetime()"
                   onmouseout="mclosetime()">
                <a href='<c:url value="/miso/pool/new"/>'>Add Pool</a>
                <a href='javascript:void(0);'
                   onclick="Pool.barcode.selectPoolBarcodesToPrint('#${fn:toLowerCase(pt)}'); return false;">Print
                  Barcodes
                  ...</a>
              </div>
            </li>
          </ul>
          <table class="display no-border" id="listing${pt}PoolsTable"></table>
          <script type="text/javascript">
            jQuery(document).ready(function () {
              Pool.ui.createListingPoolsTablePlatform('${pt}', '${poolConcentrationUnits}');
            });
          </script>
        </div>
      </c:forEach>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          jQuery("#tabs").tabs();
        });
      </script>
    </div>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
