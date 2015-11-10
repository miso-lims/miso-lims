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
    <form:form commandName="workflow" autocomplete="off">
    <sessionConversation:insertSessionConversationId attributeName="workflow"/>
    <nav class="navbar navbar-default" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">
        <c:choose>
          <c:when test="${workflow.id != 0}">View</c:when>
          <c:otherwise>Initiate</c:otherwise>
        </c:choose> Workflow
        </span>
      </div>
      <div class="navbar-right container-fluid">
        <c:choose>
          <c:when test="${workflow.id != 0}">
            <button type="button" id="updateButton" class="btn btn-default navbar-btn" data-loading-text="Saving..." onclick="Workflow.updateWorkflow()">Update</button>
          </c:when>
          <c:otherwise>
            <button type="button" id="startButton" class="btn btn-default navbar-btn" data-loading-text="Initiate..." onclick="Workflow.initiateWorkflow()">Initiate</button>
          </c:otherwise>
        </c:choose>
      </div>
    </nav>
    <table class="in">
      <c:if test="${workflow.id != 0}">
      <tr>
        <td class="h">Workflow ID:</td>
        <td>
          <input type='hidden' id='workflowId' name='workflowId' value='${workflow.id}'/>${workflow.id}
        </td>
      </tr>
      </c:if>
      <tr>
        <td class="h">Definition:</td>
        <c:choose>
          <c:when test="${workflow.id != 0 or start}">
          <td>
            <table class="list" id="workflowDefinitionTable">
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
                <td><a href="<c:url value="/miso/workflow/definition/${workflow.workflowDefinition.id}"/>">${workflow.workflowDefinition.name}</a></td>
                <td>${workflow.workflowDefinition.description}</td>
                <td>${workflow.workflowDefinition.creator.fullName}</td>
                <td><fmt:formatDate pattern="dd/MM/yyyy" value="${workflow.workflowDefinition.creationDate}"/></td>
              </tr>
              </tbody>
            </table>
            <input type="hidden" id="workflowDefinition" value="${workflow.workflowDefinition.id}"/>
          </td>
          </c:when>
          <c:otherwise>
            <form:select path="workflowDefinition">
              <form:option value="0" label="Select..."/>
              <form:options items="${workflowDefinitions}" itemLabel="name" itemValue="id"/>
            </form:select>
          </c:otherwise>
        </c:choose>
      </tr>
      <tr>
        <td class="h">Alias:</td>
        <td>
          <div class="input-group">
            <form:input path="alias" class="validateable form-control"/><span id="aliascounter" class="input-group-addon"></span>
          </div>
        </td>
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
      <c:if test="${workflow.id != 0}">
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

          <table class="list" id="workflowStatusTable">
            <thead>
            <tr>
              <th>Start Date</th>
              <th>Completion Date</th>
              <th>Last Updated</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td><fmt:formatDate pattern="dd/MM/yyyy" value="${workflow.startDate}"/></td>
              <c:choose>
                <c:when test="${(workflow.status.key eq 'Completed' and empty workflow.completionDate)
                        or workflow.status.key eq 'Failed'
                        or workflow.status.key eq 'Stopped'}">
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
    <%-- processes --%>
    <div id="key-process-panel" class="panel panel-default padded-panel container-fluid">
      <div class="row-fluid">
        <div class="col-sm-12 col-md-6 col-lg-6 small-pad"> <!-- left hand side -->
          <div class="panel panel-primary panel-dashboard">
            <div class="panel-heading">
              <h3 class="panel-title pull-left">State Fields</h3>
            </div>
            <div class="panel-body" style="overflow:auto">
              <div id='workflowDefinitionStateFieldsList' class="list-group" style="height:320px">
                <c:if test="${not empty stateKeyMap and start}">
                  <c:forEach items="${stateKeyMap}" var="stateKey" varStatus="len">
                  <a id="wfd-state-wrapper-${len.index}" class="list-group-item dashboard">
                    <span id="wfd-state-key-${len.index}">
                      <%-- TODO allow only assignee, creator and admin edit rights --%>
                      <p style="float:left; margin-top:5px">${stateKey.key}:</p>
                      <input type="text" id="key-${len.index}" style="width:80%" class="form-control pull-right state-key-input" keyText="${stateKey.key}" value="${stateKey.value}"/>
                    </span>
                  </a>
                  </c:forEach>
                </c:if>

                <c:if test="${not empty pairMap}">
                  <c:forEach items="${pairMap}" var="pair" varStatus="len">
                  <a id="wfd-state-wrapper-${len.index}" class="list-group-item dashboard">
                    <span id="wfd-state-key-${len.index}">
                      <%-- TODO allow only assignee, creator and admin edit rights --%>
                      <p style="float:left; margin-top:5px">${keyMap[pair.key]}:</p>
                      <input type="text" id="key-${len.index}" style="width:80%" class="form-control pull-right state-key-input" keyId="${pair.key}" keyText="${keyMap[pair.key]}" valueId="${pair.value}" value="${valueMap[pair.value]}"/>
                    </span>
                  </a>
                  </c:forEach>
                </c:if>
              </div>
            </div>
          </div>
        </div>
        <div class="col-sm-12 col-md-6 col-lg-6 small-pad"> <!-- right hand side -->
          <div class="panel panel-primary panel-dashboard">
            <div class="panel-heading">
              <h3 class="panel-title pull-left">Workflow Processes</h3>
            </div>
            <div class="panel-body">
              <div id='workflowProcessDefinitionSelectionList' class="list-group" style="height:320px">
                <c:choose>
                  <c:when test="${not empty workflow.workflowProcesses}">
                    <c:forEach items="${workflow.workflowProcesses}" var="entry" varStatus="len">
                    <a id="wfpd-pwrapper-${len.index}" processId="${entry.id}" class="list-group-item dashboard" href='<c:url value="/miso/workflow/process/definition/${entry.definition.id}"/>'>
                      <c:choose>
                        <c:when test="${entry.started}">
                        <span class='fa fa-gears fa-fw fa-2x'></span>
                        </c:when>
                        <c:when test="${entry.completed}">
                        <span class='fa fa-check fa-fw fa-2x'></span>
                        </c:when>
                        <c:when test="${entry.failed}">
                        <span class='fa fa-times fa-fw fa-2x'></span>
                        </c:when>
                        <c:when test="${entry.paused}">
                        <span class='fa fa-clock-o fa-fw fa-2x'></span>
                        </c:when>
                        <c:otherwise>
                        <span class='fa fa-spinner fa-fw fa-2x'></span>
                        </c:otherwise>
                      </c:choose>
                      <span id="wfpd-process-${len.index}">
                        <p style="margin-left: 30px; margin-top: -23px;"><b>${entry.definition.name}</b><br/>Description: ${entry.definition.description}
                        <c:if test="${entry.started}">
                        <br/>
                        Started: ${entry.startDate}
                        </c:if>
                        <c:if test="${entry.completed}">
                        <br/>
                        Completed: ${entry.completionDate}
                        </c:if>
                        </p>
                      </span>
                    </a>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                  <c:forEach items="${workflow.workflowDefinition.workflowProcessDefinitions}" var="entry" varStatus="len">
                  <a id="wfpd-pwrapper-${len.index}" order="${entry.key}" processId="${entry.value.id}" class="list-group-item dashboard" href='<c:url value="/miso/workflow/process/definition/${entry.value.id}"/>'>
                    <span style='font-weight: bold;font-size: 30pt;color: #B3B3B3; float:left'>${len.index + 1}</span>
                    <span id="wfpd-process-${len.index}">
                      <p style="margin-left: 30px;"><b>${entry.value.name}</b><br/>Description: ${entry.value.description}<br/>Creator: ${entry.value.creator.fullName}<br/>Creation Date: ${entry.value.creationDate}</p>
                    </span>
                  </a>
                  </c:forEach>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>