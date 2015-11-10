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
  User: bian, davey
  Date: 19-Apr-2010
  Updated: 06-Dec-2012
  Time: 14:22
--%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <nav class="navbar navbar-default" role="navigation">
       <div class="navbar-header">
          <span class="navbar-brand navbar-center">
            Pools
          </span>
       </div>
    </nav>

    <div id="tabs">
      <ul>
        <c:forEach items="${platformTypes}" var="pt" varStatus="c">
          <li><a href="#tab-${c.count}"><span>${pt} Pools</span></a></li>
        </c:forEach>
      </ul>

      <c:forEach items="${platformTypes}" var="pt" varStatus="c">
        <div id="tab-${c.count}">
          <nav id="navbar-${pt}" class="navbar navbar-default navbar-static" role="navigation">
            <div class="navbar-header">
              <span class="navbar-brand navbar-center" id="${pt}totalCount">${pt} Pools</span>
            </div>
            <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
              <ul class="nav navbar-nav navbar-right">
                <li id="${pt}-menu" class="dropdown">
                  <a id="${pt}drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
                  <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="${pt}drop1">
                    <li role="presentation"><a href='<c:url value="/miso/pool/new"/>'>Add Pool</a></li>
                    <li role="presentation"><a href='javascript:void(0);' onclick="Pool.barcode.selectPoolBarcodesToPrint('#${fn:toLowerCase(pt)}');">Print Barcodes</a></li>
                  </ul>
                </li>
              </ul>
            </div>
          </nav>
          <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered display" id="listing${pt}PoolsTable"></table>
          <script>
            jQuery("#listing${pt}PoolsTable").html("<img src='../styles/images/ajax-loader.gif'/>");
          </script>
        </div>
      </c:forEach>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          jQuery("#tabs").tabs();
          Pool.ui.createListingPoolsTables();
        });
      </script>
    </div>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>