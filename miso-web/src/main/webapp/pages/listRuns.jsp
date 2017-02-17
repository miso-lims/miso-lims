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
  User: davey
  Date: 16-Feb-2010
  Time: 08:51:03
--%>
<%@ include file="../header.jsp" %>
<script type="text/javascript" src="<c:url value='/scripts/runCalendar.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.popup.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
  <div id="contentcolumn">
    <h1>Runs</h1>
    <ul class="sddm">
      <li>
        <a onmouseover="mopen('addrunmenu')" onmouseout="mclosetime()">Add Run
          <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
        </a>
        <div id="addrunmenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()" style="visibility: hidden;">
          <c:forEach items="${platformTypes}" var="pt">
            <a href="/miso/run/new/${pt.name()}">Add ${pt.key}</a>
          </c:forEach>
        </div>
       </li>
    </ul>
    <table cellpadding="0" cellspacing="0" border="0" class="display" id="listingRunsTable">
    </table>
    <script type="text/javascript">
      jQuery(document).ready(function () {
        Run.ui.createListingRunsTable();
      });
    </script>
  </div>
</div>
</script>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
