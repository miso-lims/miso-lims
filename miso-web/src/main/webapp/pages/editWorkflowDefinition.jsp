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
    <form:form commandName="workflowDefinition" autocomplete="off">
    <sessionConversation:insertSessionConversationId attributeName="workflowDefinition"/>
    <nav class="navbar navbar-default" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">
        <c:choose>
          <c:when test="${workflowDefinition.id != 0}">Edit</c:when>
          <c:otherwise>Create</c:otherwise>
        </c:choose> Workflow Definition
        </span>
      </div>
      <div class="navbar-right container-fluid">
        <button type="button" id="saveButton" class="btn btn-default navbar-btn" data-loading-text="Saving..." onclick="Workflow.definition.saveWorkflowDefinition()">Save</button>
        <c:if test="${workflowDefinition.id != 0}">
          <button type="button" id="startButton" class="btn btn-default navbar-btn" onclick="Utils.page.pageRedirect('/miso/workflow/start/${workflowDefinition.id}');">Start New</button>
        </c:if>
      </div>
    </nav>

    <table class="in">
      <tr>
        <td class="h">Workflow Definition ID:</td>
        <td>
          <c:choose>
            <c:when test="${workflowDefinition.id != 0}"><input type="hidden" value="${workflowDefinition.id}" name="wfdId" id="wfdId"/>
              ${workflowDefinition.id}
            </c:when>
            <c:otherwise><i>Unsaved</i></c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td>Name:</td>
        <td>
          <div class="input-group">
            <form:input path="name" id="wfdName" class="validateable form-control"/>
            <span id="namecounter" class="input-group-addon"></span>
          </div>
        </td>
      </tr>
      <tr>
        <td>Description:</td>
        <td>
          <div class="input-group">
            <form:input path="description" id="wfdDesc" class="validateable form-control"/>
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
              ${workflowDefinition.creator.fullName}
              <input type="hidden" value="${workflowDefinition.creator.id}" name="creator" id="creator"/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
      <tr>
        <td>Creation date:</td>
        <td><fmt:formatDate value="${workflowDefinition.creationDate}"/><input type="hidden" value="${workflowDefinition.creationDate}" name="creationDate" id="creationDate"/></td>
      </tr>
    </table>

    <div id="key-process-panel" class="panel panel-default padded-panel container-fluid">
      <div class="row-fluid">
        <div class="col-sm-12 col-md-6 col-lg-6 small-pad"> <!-- left hand side -->
          <div class="panel panel-primary panel-dashboard">
            <div class="panel-heading">
              <h3 class="panel-title pull-left">State Fields</h3>
              <div class='state-key input-group pull-right'>
                <input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='form-control' id='wfd-search-state-key' tabindex='0' placeholder='Type to start searching'/>
                <span class='input-group-addon fa fa-plus-square-o fa-fw fa-2x miso-fa-2x-addon' onclick='Workflow.ui.addStateKeyToWorkflowDefinition("#wfd-search-state-key");'>
                </span>
              </div>
            </div>
            <div class="panel-body" style="overflow:auto">
              <div id='workflowDefinitionStateFieldsList' class="list-group" style="height:320px">
                <c:if test="${not empty stateKeyMap}">
                  <c:forEach items="${stateKeyMap}" var="stateKey">
                    <a id="wfd-state-wrapper-${stateKey.key}" class="list-group-item dashboard">
                      <span id="wfd-state-key-${stateKey.key}" keyId="${stateKey.key}" keyText="${stateKey.value}">${stateKey.value}</span>
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
              <h3 class="panel-title pull-left">Workflow Process Definitions</h3>
              <div class='state-key input-group pull-right'>
                <input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='form-control' id='wfd-search-process-definition' tabindex='0' placeholder='Type to start searching'/>
                <span class='input-group-addon fa fa-plus-square-o fa-fw fa-2x miso-fa-2x-addon' onclick='Workflow.ui.addWorkflowProcessDefinitionToWorkflowDefinition("#wfd-search-process-definition");'></span>
              </div>
            </div>
            <div class="panel-body">
              <div id='workflowProcessDefinitionSelectionList' class="list-group" style="height:320px">
                <c:if test="${not empty workflowDefinition.workflowProcessDefinitions}">
                  <c:forEach items="${workflowDefinition.workflowProcessDefinitions}" var="entry" varStatus="len">
                    <a id="wfpd-pwrapper-${len.index}" order="${entry.key}" processId="${entry.value.id}" class="list-group-item dashboard" href='<c:url value="/miso/workflow/process/definition/${entry.value.id}"/>'>
                      <span class='fa fa-arrows fa-fw fa-2x'></span>
                      <span id="wfpd-process-${len.index}">
                        <p style="margin-left: 30px; margin-top: -23px;"><b>${entry.value.name}</b><br/>Description: ${entry.value.description}<br/>Creator: ${entry.value.creator.fullName}<br/>Creation Date: ${entry.value.creationDate}</p>
                      </span>
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
    var panelList = jQuery('#workflowProcessDefinitionSelectionList');
    panelList.sortable({
      handle: 'span.fa-arrows'
    });

    jQuery('#wfd-search-state-key').select2({
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

    jQuery('#wfd-search-process-definition').select2({
      combobox: true,
      minimumInputLength: 2,
      query: function (query) {
        Fluxion.doAjax(
          'workflowControllerHelperService',
          'searchWorkflowProcessDefinitions',
          {'query': query.term, 'url': ajaxurl},
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