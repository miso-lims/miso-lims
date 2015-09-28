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
    <nav id="navbar-state-key" class="navbar navbar-default navbar-static" role="navigation">
      <div class="navbar-header">
        <span class="navbar-brand navbar-center">Workflow Management</span>
      </div>
    </nav>

    <div class="row-fluid">
      <div class="col-sm-12 col-md-9 col-lg-9 small-pad"> <!-- left hand side -->
        <div id="wfd-panel" class="panel panel-default padded-panel container-fluid">
          <nav class="navbar navbar-default" role="navigation">
            <div class="navbar-header">
              <span class="navbar-brand navbar-center">Workflow Definitions</span>
            </div>
          </nav>
          <div class="row-fluid">
            <div class="col-sm-12 col-md-6 col-lg-6 small-pad"> <!-- left hand side -->
              <div class="panel panel-primary panel-dashboard">
                <div class="panel-heading">
                  <h3 class="panel-title pull-left">Existing Workflow Definitions</h3>
                  <input type="text" size="40" id="filterWorkflowDefinitions" name="filterWorkflowDefinitions" class="form-control pull-right"/>
                </div>
                <div class="panel-body">
                  <div id='wfd-list' class="list-group" style="height:320px">
                    <c:if test="${not empty workflowDefinitions}">
                      <c:forEach items="${workflowDefinitions}" var="wfd">
                        <a id="wfd-pwrapper-${wfd.id}" class="list-group-item dashboard" href='<c:url value="/miso/workflow/definition/${wfd.id}"/>'>
                          <span id="workflowDefinition${wfd.id}">
                            Name: <b>${wfd.name}</b><br/>Description: ${wfd.description}<br/>Creator: ${wfd.creator.fullName}<br/>Creation Date: ${wfd.creationDate}
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
                  <h3 class="panel-title pull-left">Create new Workflow Definition</h3>
                  <button type="button" class="btn btn-default pull-right" onclick="Workflow.definition.saveWorkflowDefinition();">Save</button>
                </div>
                <div class="panel-body" style="overflow:auto">
                  <table class="in">
                    <tr>
                      <td class="h">Name:</td>
                      <td><input type="text" id="wfdName" name="wfdName" class="form-control"/></td>
                    </tr>
                    <tr>
                      <td class="h">Description:</td>
                      <td><input type="text" id="wfdDesc" name="wfdDesc" class="form-control"/></td>
                    </tr>
                  </table>
                  <h5 class="page-header header-margin-h5">State Fields
                    <i class="fa fa-file-text-o fa-lg fa-fw header-icon pull-left"></i>
                    <div class='state-key input-group pull-right'>
                      <input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='form-control' id='wfd-search-state-key' tabindex='0' placeholder='Type to start searching'/>
                      <span class='input-group-addon fa fa-plus-square-o fa-fw fa-2x miso-fa-2x-addon' onclick='Workflow.ui.addStateKeyToWorkflowDefinition("#wfd-search-state-key");'></span>
                    </div>
                  </h5>
                  <div id="workflowDefinitionStateFieldsDiv" style="max-height:160px; overflow:auto">
                    <div id="workflowDefinitionStateFieldsList" class="list-group"></div>
                  </div>
                  <h5 class="page-header header-margin-h5">Processes
                    <i class="fa fa-cubes fa-lg fa-fw header-icon pull-left"></i>
                    <div class='state-key input-group pull-right'>
                      <input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='form-control' id='wfd-search-process-definition' tabindex='0' placeholder='Type to start searching'/>
                      <span class='input-group-addon fa fa-plus-square-o fa-fw fa-2x miso-fa-2x-addon' onclick='Workflow.ui.addWorkflowProcessDefinitionToWorkflowDefinition("#wfd-search-process-definition");'></span>
                    </div>
                  </h5>
                  <div id="workflowProcessDefinitionSelectionDiv" style="max-height:160px; overflow:auto">
                    <ol id="workflowProcessDefinitionSelectionList" class="list-group"></ol>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div id="wfpddiv" class="panel panel-default padded-panel container-fluid">
          <nav class="navbar navbar-default" role="navigation">
            <div class="navbar-header">
              <span class="navbar-brand navbar-center">Workflow Process Definitions</span>
            </div>
          </nav>
          <div class="row-fluid">
            <div class="col-sm-12 col-md-6 col-lg-6 small-pad"> <!-- left hand side -->
              <div class="panel panel-primary panel-dashboard">
                <div class="panel-heading">
                  <h3 class="panel-title pull-left">Existing Workflow Process Definitions</h3>
                  <input type="text" size="40" id="filterWorkflowProcessDefinitions" name="filterWorkflowProcessDefinitions" class="form-control pull-right"/>
                </div>
                <div class="panel-body">
                  <div id='wfpdList' class="list-group" style="height:320px">
                    <c:if test="${not empty workflowProcessDefinitions}">
                      <c:forEach items="${workflowProcessDefinitions}" var="wfpd">
                        <a id="wfpd-pwrapper-${wfpd.id}" class="list-group-item dashboard" href='<c:url value="/miso/workflow/process/definition/${wfpd.id}"/>'>
                          <span id="workflowProcessDefinition${wfpd.id}">
                            Name: <b>${wfpd.name}</b><br/>Description: ${wfpd.description}<br/>Creator: ${wfpd.creator.fullName}<br/>Creation Date: ${wfpd.creationDate}
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
                  <h3 class="panel-title pull-left">Create new Workflow Process Definition</h3>
                  <button type="button" class="btn btn-default pull-right" onclick="Workflow.definition.saveWorkflowProcessDefinition();">Save</button>
                </div>
                <div class="panel-body">
                  <table class="in">
                    <tr>
                      <td class="h">Name:</td>
                      <td><input type="text" id="wfpdName" name="wfpdName" class="form-control"/></td>
                    </tr>
                    <tr>
                      <td class="h">Description:</td>
                      <td><input type="text" id="wfpdDesc" name="wfpdDesc" class="form-control"/></td>
                    </tr>
                  </table>
                  <h5 class="page-header header-margin-h5">State Fields
                    <i class="fa fa-file-text-o fa-lg fa-fw header-icon pull-left"></i>
                    <div class='state-key input-group pull-right'>
                      <input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='form-control' id='wfpd-search-state-key' tabindex='0' placeholder='Type to start searching'/>
                      <span class='input-group-addon fa fa-plus-square-o fa-fw fa-2x miso-fa-2x-addon' onclick='Workflow.ui.addStateKeyToWorkflowProcessDefinition("#wfpd-search-state-key");'>
                      </span>
                    </div>
                  </h5>
                  <div id="workflowProcessDefinitionStateFieldsDiv" style="max-height:160px">
                    <div id="workflowProcessDefinitionStateFieldsList" class="list-group"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-sm-12 col-md-3 col-lg-3 small-pad"> <!-- right hand side -->
        <div id="statekeysdiv" class="panel panel-primary panel-dashboard padded-panel">
          <nav class="navbar navbar-default" role="navigation">
            <div class="navbar-header">
              <span class="navbar-brand navbar-center">State Keys</span>
            </div>
            <div class="collapse navbar-collapse bs-example-js-navbar-collapse">
              <ul class="nav navbar-nav navbar-right">
                <li id="state-key-menu" class="dropdown">
                  <a id="skdrop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Options <b class="caret"></b></a>
                  <ul class="dropdown-menu dropdown-menu-right" role="menu" aria-labelledby="skdrop1">
                    <li role="presentation"><a href="javascript:void(0);" onclick="Workflow.ui.createNewFieldKey('newStateFieldsDiv');">Create new state key</a></li>
                  </ul>
                </li>
              </ul>
            </div>
          </nav>
          <div class="panel-body">
            <div id="newStateFieldsDiv"></div>
            <div id='stateFieldsList' class="list-group" style="height:320px">
              <c:if test="${not empty stateKeys}">
                <c:forEach items="${stateKeys}" var="stateKey" varStatus="s">
                <a id="state-wrapper-${s.index}" class="list-group-item dashboard">
                  <span id="state-key-${s.index}" keyText="${stateKey}">${stateKey}</span>
                </a>
                </c:forEach>
              </c:if>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</div>

<script>
  jQuery(document).ready(function() {
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

    var panelList = jQuery('#workflowProcessDefinitionSelectionList');
      panelList.sortable({
        handle: 'span.fa-arrows'
      });
    });
</script>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
