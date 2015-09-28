<%@ include file="../header.jsp" %>

<%--
  ~ Copyright (c) 2014. The Genome Analysis Centre, Norwich, UK
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
    <ul>
      <c:if test="${fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
        <h1>
          <div id="totalCount">All Workflows</div>
        </h1>
        <table cellpadding="0" cellspacing="0" border="0" class="display" id="workflowsTable">
        </table>
        <script type="text/javascript">
          jQuery(document).ready(function () {
            Workflow.ui.createListingWorkflowsTable("workflowsTable");
          });
        </script>
      </c:if>

      <c:if test="${workflow.assignee.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username}">
        <h2>Workflows assigned to me</h2>
        <h1>
          <div id="totalCount">Workflows Assigned To Me</div>
        </h1>
        <table cellpadding="0" cellspacing="0" border="0" class="display" id="assignedWorkflowsTable">
        </table>
        <script type="text/javascript">
          jQuery(document).ready(function () {
            Workflow.ui.createListingAssignedWorkflowsTable("assignedWorkflowsTable");
          });
        </script>
      </c:if>
    </ul>
  </div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
