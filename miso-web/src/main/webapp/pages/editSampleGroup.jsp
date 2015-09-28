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

<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 20-Jun-2014
--%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <form:form action="/miso/samplegroup" method="POST" commandName="entitygroup" autocomplete="off">
      <sessionConversation:insertSessionConversationId attributeName="entitygroup"/>
      <nav class="navbar navbar-default" role="navigation">
        <div class="navbar-header">
          <span class="navbar-brand navbar-center">
            <c:choose>
              <c:when test="${entitygroup.id != 0}">Edit</c:when>
              <c:otherwise>Create</c:otherwise>
            </c:choose> Sample Group
          </span>
        </div>
        <div class="navbar-right container-fluid">
          <%-- <button type="button" class="btn btn-default navbar-btn" onclick="return validate_entity_group(this.form);">Save</button> --%>
          <button type="button" id="saveButton" class="btn btn-default navbar-btn" data-loading-text="Saving..." onclick="EntityGroup.saveEntityGroup();">Save</button>
          <sec:authorize access="hasRole('ROLE_ADMIN')">
            <c:if test="${entitygroup.id != 0}">
              <button type="button" class="btn btn-default navbar-btn" onclick="EntityGroup.deleteEntityGroup(${entitygroup.id})" >Delete</button>
            </c:if>
          </sec:authorize>
        </div>
      </nav>

      <table class="in">
        <tr>
          <td class="h">Sample Group ID:</td>
          <td>
            <c:choose>
              <c:when test="${entitygroup.id != 0}"><input type='hidden' id='entityGroupId' name='id' value='${entitygroup.id}'/>${entitygroup.id}</c:when>
              <c:otherwise><i>Unsaved</i></c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td>Created by:</td>
          <td>
            <c:choose>
              <c:when test="${entitygroup.id != 0}">
                ${entitygroup.creator.fullName}
                <input type="hidden" value="${entitygroup.creator.id}" name="creator" id="creator"/>
              </c:when>
              <c:otherwise>
                ${SPRING_SECURITY_CONTEXT.authentication.principal.username}
                <input type="hidden" value="${currentUser.id}" name="creator" id="creator"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
        <tr>
          <td>Creation Date:</td>
          <td>
            ${entitygroup.creationDate}
            <input type="hidden" value="${entitygroup.creationDate}" name="creationDate" id="creationDate"/>
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
        <tr>
          <td>Workflow:</td>
          <td>
            <form:select path="parent">
              <form:option value="0" label="Select..."/>
              <form:options items="${workflows}" itemLabel="workflowDefinition.name" itemValue="id"/>
            </form:select>
          </td>
        </tr>
      </table>

      <div class="row-fluid">
        <div class="col-sm-12 col-md-6 small-pad"> <!-- left hand side -->
          <div class="panel panel-primary panel-dashboard">
            <div class="panel-heading">
              <h3 class="panel-title pull-left">Group Elements <span id='num-elements-badge' class='badge'></span></h3>
              <input type="text" size="40" id="filterGroupElements" name="filterGroupElements" class="form-control pull-right"/>
            </div>
            <div class="panel-body">
              <div id='groupElementList' class="list-group" style="height:320px">
                <c:if test="${not empty entitygroup.entities}">
                  <c:forEach items="${entitygroup.entities}" var="entity">
                    <a id="element-wrapper-${entity.id}" class="list-group-item dashboard">
                      <span id="entity${entity.id}" entityId="${entity.id}" entityName="${entity.name}">
                        <div><b>${entity.name} (${entity.alias}) : ${entity.sampleType}</b><br/>
                        Project: ${entity.project.alias}</div>
                        <input type="hidden" id="entities${entity.id}" value="${entity.name}" name="entities"/>
                      </span>
                      <span style='position: absolute; top: 0; right: 0;' onclick='EntityGroup.sample.confirmSampleRemove(this, ${entitygroup.id});' class='fa fa-fw fa-2x fa-times-circle-o pull-right'></span>
                    </a>
                  </c:forEach>
                </c:if>
              </div>
              <input type="hidden" value="on" name="_entities"/>
            </div>
          </div>
        </div>
        <div class="col-sm-12 col-md-6 col-lg-4 small-pad"> <!-- right hand side -->
          <div class="panel panel-primary panel-dashboard">
            <div class="panel-heading">
              <h3 class="panel-title pull-left">Find Samples</h3>
              <input type="text" size="40" id="searchSamples" name="searchSamples" class="form-control pull-right"/>
            </div>
            <div class="panel-body">
              <div id='sampleList' class="list-group" style="height:320px"></div>
            </div>
          </div>
        </div>
      </div>
    </form:form>
    <script type="text/javascript">
      jQuery('#num-elements-badge').html(jQuery('#groupElementList>.list-group-item').length.toString());

      jQuery('#filterGroupElements').keypress(function (e) {
        if (e.which == 13) return false;
      });
      jQuery('#searchSamples').keypress(function (e) {
        if (e.which == 13) return false;
      });

      Utils.timer.typewatchFunc(jQuery('#filterGroupElements'), function () {
        EntityGroup.ui.filterGroupElements(jQuery('#filterGroupElements').val());
      }, 300, 2);

      Utils.timer.typewatchFunc(jQuery('#searchSamples'), function () {
        <c:choose>
        <c:when test="${entitygroup.id == 0}">
          EntityGroup.sample.searchSamples(jQuery('#searchSamples').val());
        </c:when>
        <c:otherwise>
          EntityGroup.sample.searchSamples(jQuery('#searchSamples').val(), ${entitygroup.id});
        </c:otherwise>
        </c:choose>
      }, 300, 2);
    </script>
  </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>