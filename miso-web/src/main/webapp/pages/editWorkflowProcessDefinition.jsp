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
    <form:form commandName="workflowProcessDefinition" autocomplete="off">
    <sessionConversation:insertSessionConversationId attributeName="workflowProcessDefinition"/>
    <nav class="navbar navbar-default" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">
        <c:choose>
          <c:when test="${workflowProcessDefinition.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose> Workflow Process Definition
        </span>
      </div>
      <div class="navbar-right container-fluid">
        <button type="button" id="saveButton" class="btn btn-default navbar-btn" data-loading-text="Saving..." onclick="Workflow.definition.saveWorkflowProcessDefinition()">Save</button>
      </div>
    </nav>

    <table class="in">
      <tr>
        <td class="h">Workflow Process Definition ID:</td>
        <td>
          <c:choose>
            <c:when test="${workflowProcessDefinition.id != 0}"><input type="hidden" value="${workflowProcessDefinition.id}" name="wfpdId" id="wfpdId"/>
              ${workflowProcessDefinition.id}
            </c:when>
            <c:otherwise><i>Unsaved</i></c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td>Name:</td>
        <td>
          <div class="input-group">
            <form:input path="name" id="wfpdName" class="validateable form-control"/>
            <span id="namecounter" class="input-group-addon"></span>
          </div>
        </td>
      </tr>
      <tr>
        <td>Description:</td>
        <td>
          <div class="input-group">
            <form:input path="description" id="wfpdDesc" class="validateable form-control"/>
            <span id="descriptioncounter" class="input-group-addon"></span>
          </div>
        </td>
      </tr>
      <tr>
        <td>Creator:</td>
        <td>
          <c:choose>
            <c:when test="${(formObj.creator.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)}">
              You
            </c:when>
            <c:otherwise>
              ${workflowProcessDefinition.creator.fullName}
              <input type="hidden" value="${workflowProcessDefinition.creator.id}" name="creator" id="creator"/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td>Creation date:</td>
        <td><fmt:formatDate value="${workflowProcessDefinition.creationDate}"/><input type="hidden" value="${workflowProcessDefinition.creationDate}" name="creationDate" id="creationDate"/></td>
      </tr>

      <tr>
        <td>Input Type:</td>
        <td>
        <c:choose>
        <c:when test="${workflowProcessDefinition.id != 0 and not empty workflowProcessDefinition.inputType and not formObj.creator.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username}">
          ${workflowProcessDefinition.inputType.simpleName}<input type="hidden" value="${workflowProcessDefinition.inputType.name}" name="inputType" id="inputType"/></td>
        </c:when>
        <c:otherwise>
          <form:select path="inputType">
            <form:option value="0" label="None"/>
            <form:options items="${definitionTypes}" itemLabel="simpleName" itemValue="name"/>
          </form:select>
        </c:otherwise>
        </c:choose>
        </td>
      </tr>
      <tr>
        <td>Output Type:</td>
        <td>
        <c:choose>
        <c:when test="${workflowProcessDefinition.id != 0 and not empty workflowProcessDefinition.outputType and not formObj.creator.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username}">
          ${workflowProcessDefinition.outputType.simpleName}<input type="hidden" value="${workflowProcessDefinition.outputType.name}" name="outputType" id="outputType"/></td>
        </c:when>
        <c:otherwise>
          <form:select path="outputType">
            <form:option value="0" label="None"/>
            <form:options items="${definitionTypes}" itemLabel="simpleName" itemValue="name"/>
          </form:select>
        </c:otherwise>
        </c:choose>
        </td>
      </tr>
      <tr>
        <td>Type processor:</td>
        <td>
        <c:choose>
        <c:when test="${workflowProcessDefinition.id != 0 and not empty workflowProcessDefinition.inputType and not empty workflowProcessDefinition.outputType and not formObj.creator.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username}">
          ${workflowProcessDefinition.typeProcessor}<input type="hidden" value="${workflowProcessDefinition.typeProcessor.name}" name="typeProcessor" id="typeProcessor"/></td>
        </c:when>
        <c:otherwise>
          <form:select path="outputType">
            <form:option value="0" label="N/A"/>
          </form:select>
        </c:otherwise>
        </c:choose>
        </td>
      </tr>
    </table>

    <div id="key-process-panel" class="panel panel-default padded-panel container-fluid">
      <div class="row-fluid">
        <div class="col-sm-12 col-md-6 col-lg-6 small-pad"> <!-- left hand side -->
          <div class="panel panel-primary panel-dashboard">
            <div class="panel-heading">
              <h3 class="panel-title pull-left">State Fields</h3>
              <div class='state-key input-group pull-right'>
                <input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='form-control' id='wfpd-search-state-key' tabindex='0' placeholder='Type to start searching'/>
                <span class='input-group-addon fa fa-plus-square-o fa-fw fa-2x miso-fa-2x-addon' onclick='Workflow.ui.addStateKeyToWorkflowProcessDefinition("#wfpd-search-state-key");'>
                </span>
              </div>
            </div>
            <div class="panel-body" style="overflow:auto">
              <div id='workflowProcessDefinitionStateFieldsList' class="list-group" style="height:320px">
                <c:if test="${not empty stateKeyMap}">
                  <c:forEach items="${stateKeyMap}" var="stateKey">
                    <a id="wfpd-state-wrapper-${stateKey.key}" class="list-group-item dashboard">
                      <span id="wfpd-state-key-${stateKey.key}" keyId="${stateKey.key}" keyText="${stateKey.value}">${stateKey.value}</span>
                    </a>
                  </c:forEach>
                </c:if>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    </form:form>
  </div>
</div>
<script>
  jQuery(document).ready(function() {
    jQuery('#wfpd-search-state-key').select2({
      combobox: true,
      minimumInputLength: 2,
      query: function (query) {
        Fluxion.doAjax(
          'workflowControllerHelperService',
          'searchStateFieldKeys',
          {'key': query.term, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            if (json.error) {
              jQuery('#'+fid+'Error').html(json.error);
            }
            else {
              var data = {results: json.response};
              query.callback(data);
            }
          }
        });
      }
    });
  });
</script>
<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>