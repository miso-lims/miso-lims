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
<%@ include file="../header.jsp" %>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>

<div id="maincontent">
  <div id="contentcolumn">
    <form:form commandName="workflowProcess" autocomplete="off">
    <sessionConversation:insertSessionConversationId attributeName="workflowProcess"/>
      <nav class="navbar navbar-default" role="navigation">
        <div class="navbar-header">
          <span class="navbar-brand navbar-center">
          <c:choose>
            <c:when test="${workflowProcess.id != 0}">View</c:when>
            <c:otherwise>Initiate</c:otherwise>
          </c:choose> Workflow Process
          </span>
        </div>
        <div class="navbar-right container-fluid">
          <c:choose>
            <c:when test="${workflowProcess.id != 0}">
              <button type="button" id="updateButton" class="btn btn-default navbar-btn" data-loading-text="Saving..." onclick="Workflow.process.updateWorkflowProcess()">Update</button>
            </c:when>
            <c:otherwise>
              <button type="button" id="startButton" class="btn btn-default navbar-btn" data-loading-text="Initiate..." onclick="Workflow.process.initiateWorkflowProcess()">Initiate</button>
            </c:otherwise>
          </c:choose>
        </div>
      </nav>
      <table class="in">
        <c:if test="${workflowProcess.id != 0}">
        <tr>
          <td class="h">Workflow Process ID:</td>
          <td>
            <input type='hidden' id='workflowProcessId' name='workflowProcessId' value='${workflowProcess.id}'/>${workflowProcess.id}
          </td>
        </tr>
        </c:if>
        <tr>
          <td class="h">Definition:</td>
          <c:choose>
            <c:when test="${workflowProcess.id != 0}">
            <td>
              <table class="list" id="workflowProcessDefinitionTable">
                <thead>
                <tr>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Created By</th>
                  <th>Creation Date</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td><a href="<c:url value="/miso/workflow/process/definition/${workflowProcess.definition.id}"/>">${workflowProcess.definition.name}</a></td>
                  <td>${workflowProcess.definition.description}</td>
                  <td>${workflowProcess.definition.creator.fullName}</td>
                  <td><fmt:formatDate pattern="dd/MM/yyyy" value="${workflowProcess.definition.creationDate}"/></td>
                </tr>
                </tbody>
              </table>
              <input type="hidden" id="workflowProcessDefinition" value="${workflowProcess.definition.id}"/>
            </td>
            </c:when>
            <c:otherwise>
              <form:select path="workflowProcessDefinition">
                <form:option value="0" label="Select..."/>
                <form:options items="${workflowProcessDefinitions}" itemLabel="name" itemValue="id"/>
              </form:select>
            </c:otherwise>
          </c:choose>
        </tr>
        <tr>
          <td>Assigned to:</td>
          <td>
            <form:select path="assignee">
              <form:option value="0" label="Select..."/>
              <form:options items="${users}" itemLabel="fullName" itemValue="userId"/>
            </form:select>
          </td>
        </tr>
        <c:if test="${workflowProcess.id != 0}">
        <tr>
          <td valign="top">Status:</td>
          <td>
            <div id="health-radio" class="btn-group" data-toggle="buttons">
            <form:radiobuttons id="status" path="status" items="${healthTypes}"
                               onchange="Workflow.ui.checkForCompletionDate();" element="label class='btn btn-default'"/>
            </div>
            <script>
              var c = jQuery('#health-radio :input:checked');
              c.parent('.btn').addClass('active');
              var inpv = c.val();
              if (inpv === "Completed") { c.parent('.btn').removeClass('btn-default').addClass("btn-success"); }
              if (inpv === "Failed") { c.parent('.btn').removeClass('btn-default').addClass("btn-danger"); }
            </script>

            <table class="list" id="workflowProcessStatusTable">
              <thead>
              <tr>
                <th>Start Date</th>
                <th>Completion Date</th>
                <th>Last Updated</th>
              </tr>
              </thead>
              <tbody>
              <tr>
                <td><fmt:formatDate pattern="dd/MM/yyyy" value="${workflowProcess.startDate}"/></td>
                <c:choose>
                  <c:when test="${(workflowProcess.status.key eq 'Completed' and empty workflowProcess.completionDate)
                          or workflowProcess.status.key eq 'Failed'
                          or workflowProcess.status.key eq 'Stopped'}">
                  <td><form:input path="completionDate" class="form-control"/></td>
                  <script type="text/javascript">
                    Utils.ui.addDatePicker("completionDate");
                  </script>
                  </c:when>
                  <c:otherwise>
                  <td id="completionDate">
                    <fmt:formatDate pattern="dd/MM/yyyy" value="${completionDate}"/>
                  </td>
                  </c:otherwise>
                </c:choose>
                <td>
                  <%-- get this from latest process --%>
                  <fmt:formatDate value="${lastUpdated}" dateStyle="long" pattern="dd/MM/yyyy HH:mm:ss"/>
                </td>
              </tr>
              </tbody>
            </table>
          </td>
        </tr>
        </c:if>
      </table>
    </form:form>
  </div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>