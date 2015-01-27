/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

var Workflow = Workflow || {
  initiateWorkflow : function() {
    var btn = jQuery("#startButton");
    btn.button('loading');

    var s = {};

    if (jQuery('#workflowId').length && !Utils.validation.isNullCheck(jQuery('#workflowId').val())) {
      //add an id if one exists (i.e. if we're editing an existing workflow)
      s["id"] = jQuery('#workflowId').val();
    }

    if (jQuery('#workflowDefinition').length && !Utils.validation.isNullCheck(jQuery('#workflowDefinition').val())) {
      //add an id if one exists (i.e. if we're editing an existing workflow)
      s["workflowDefinitionId"] = jQuery('#workflowDefinition').val();
    }
    else {
      btn.button('reset');
      alert("No valid workflow definition selected for this workflow.");
    }

    if (jQuery('#alias').length && !Utils.validation.isNullCheck(jQuery('#alias').val())) {
      s["alias"] = jQuery('#alias').val();
    }

    var userId = jQuery("select[name='assignee'] :selected").val();
    if (!Utils.validation.isNullCheck(userId) && userId != 0) {
      s["assignee"] = userId;
    }
    else {
      btn.button('reset');
      alert("Please select an assignee.");
    }

    var keys = [];
    jQuery("#workflowDefinitionStateFieldsList .state-key-input").each(function() {
      var p = jQuery(this);
      var keyText = p.attr("keyText");
      var keyValue = p.val();
      if (!Utils.validation.isNullCheck(keyText)) {
        var k = {"key":keyText,"value":keyValue};
        if (p.is("[keyId]")) k["keyId"] = p.attr("keyId");
        if (p.is("[valueId]")) k["valueId"] = p.attr("valueId");
        keys.push(k);
      }
    });
    s["keys"] = keys;

    Fluxion.doAjax(
      'workflowControllerHelperService',
      'initiateWorkflow',
      {'workflowDefinition': s, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        if (json.error) {
          btn.button('reset');
          alert("Unable to save workflow: " + json.error);
        }
        else {
          setTimeout(function () {
            btn.button('reset');
          }, 1000);
        }
      }
    });
  },

  updateWorkflow : function() {
    var btn = jQuery("#updateButton");
    btn.button('loading');

    var s = {};

    if (jQuery('#workflowId').length && !Utils.validation.isNullCheck(jQuery('#workflowId').val())) {
      //add an id if one exists (i.e. if we're editing an existing workflow)
      s["id"] = jQuery('#workflowId').val();
    }

    if (jQuery('#alias').length && !Utils.validation.isNullCheck(jQuery('#alias').val())) {
      s["alias"] = jQuery('#alias').val();
    }

    var status = jQuery('#health-radio :input:checked').val();
    s["status"] = status;

    var userId = jQuery("select[name='assignee'] :selected").val();
    if (!Utils.validation.isNullCheck(userId) && userId != 0) {
      s["assignee"] = userId;
    }
    else {
      btn.button('reset');
      alert("Please select an assignee.");
    }

    var keys = [];
    jQuery("#workflowDefinitionStateFieldsList .state-key-input").each(function() {
      var p = jQuery(this);
      var keyText = p.attr("keyText");
      var keyValue = p.val();
      if (!Utils.validation.isNullCheck(keyText)) {
        var k = {"key":keyText,"value":keyValue};
        if (p.is("[keyId]")) k["keyId"] = p.attr("keyId");
        if (p.is("[valueId]")) k["valueId"] = p.attr("valueId");
        keys.push(k);
      }
    });
    s["keys"] = keys;

    Fluxion.doAjax(
      'workflowControllerHelperService',
      'updateWorkflow',
      {'workflow': s, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        if (json.error) {
          btn.button('reset');
          alert("Unable to save workflow: " + json.error);
        }
        else {
          setTimeout(function () {
            btn.button('reset');
          }, 1000);
        }
      }
    });
  }
};

Workflow.ui = {
  createListingWorkflowsTable : function(tableId) {
    jQuery('#' + tableId).html("<img src='../styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'workflowControllerHelperService',
      'listWorkflowsDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#' + tableId).html('');
        jQuery('#' + tableId).dataTable({
          "aaData": json.workflowsArray,
          "aoColumns": [
            { "sTitle": "Name"},
            { "sTitle": "Description"},
            { "sTitle": "Alias"},
            { "sTitle": "Assigned To"},
            { "sTitle": "Status"},
            { "sTitle": "Start Date"},
            { "sTitle": "Completion Date"},
            { "sTitle": "Progress"},
            { "sTitle": "View"}
          ],
          "bJQueryUI": false,
          "iDisplayLength": 25,
          "aaSorting": [
            [0, "desc"]
          ]
        });
      }
    });
  },

  createListingAssignedWorkflowsTable : function(tableId) {
    jQuery('#' + tableId).html("<img src='../styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'workflowControllerHelperService',
      'listAssignedWorkflowsDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#' + tableId).html('');
        jQuery('#' + tableId).dataTable({
          "aaData": json.assignedWorkflowsArray,
          "aoColumns": [
            { "sTitle": "Name"},
            { "sTitle": "Description"},
            { "sTitle": "Alias"},
            { "sTitle": "Status"},
            { "sTitle": "Start Date"},
            { "sTitle": "Completion Date"},
            { "sTitle": "Progress"},
            { "sTitle": "View"}
          ],
          "bJQueryUI": false,
          "iDisplayLength": 25,
          "aaSorting": [
            [0, "desc"]
          ]
        });
      }
    });
  },

  createListingIncompleteWorkflowsTable : function(tableId) {
    jQuery('#' + tableId).html("<img src='../styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'workflowControllerHelperService',
      'listIncompleteWorkflowsDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#' + tableId).html('');
        jQuery('#' + tableId).dataTable({
          "aaData": json.incompleteWorkflowsArray,
          "aoColumns": [
            { "sTitle": "Name"},
            { "sTitle": "Description"},
            { "sTitle": "Alias"},
            { "sTitle": "Assignee"},
            { "sTitle": "Status"},
            { "sTitle": "Start Date"},
            { "sTitle": "Progress"},
            { "sTitle": "View"}
          ],
          "bJQueryUI": false,
          "iDisplayLength": 25,
          "aaSorting": [
            [0, "desc"]
          ]
        });
      }
    });
  },

  createNewFieldKey : function(div) {
    var self = this;
    if (!jQuery('#'+div).attr("display")) {
      var numinps = jQuery('#'+div).find("input").length;
      var fid = "field"+numinps;
      var options = "<div class='state-key input-group'>"+
                    "<input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='form-control' id='state-key-"+fid+"' tabindex='0' placeholder='Type to start searching'/>"+
                    "<span class='input-group-addon fa fa-plus-square-o fa-fw fa-2x miso-fa-2x-addon' onclick='Workflow.ui.processNewFieldKey(\"#state-key-"+fid+"\");'>"+
                    /*"<a href='javascript:void(0);' onclick='Workflow.ui.processNewFieldKey(\"#state-key-"+fid+"\");'></a>"+*/
                    "</span>"+
                    "</div>"+
                    "<div id='state-key-"+fid+"Result'></div><div class='error' id='state-key-"+fid+"Error'></div>";
      jQuery('#'+div).append(options);
      jQuery('#'+div).attr("display", "true");

      jQuery('#state-key-'+fid).select2({
        combobox: true,
        minimumInputLength: 2,
        query: function (query) {
          Fluxion.doAjax(
            'workflowControllerHelperService',
            'searchStateFieldKeys',
            {'key': query.term, 'disable': true, 'url': ajaxurl},
            {'doOnSuccess': function (json) {
              if (json.error) {
                jQuery('#state-key-'+fid+'Error').html(json.error);
              }
              else {
                var data = {results: json.response};
                query.callback(data);
              }
            }
          });
        }
      });
    }
  },

  processNewFieldKey : function(inp) {
    var input = jQuery(inp);
    var inpid = input.attr('id');

    Fluxion.doAjax(
      'workflowControllerHelperService',
      'addStateKey',
      {'key': input.val(), 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        if (json.error) {
          jQuery('#'+inpid+'Error').html("Unable to add key: " + json.error);
        }
        else {
          jQuery('#'+inpid+'Result').html("Key '"+input.val()+"' added successfully");
          jQuery('#stateFieldsList').prepend("<a id='state-wrapper-"+jQuery('#stateFieldsList .list-group-item').length+"' class='list-group-item dashboard'><span id='state-key-"+jQuery('#stateFieldsList .list-group-item').length+"' keyText='"+input.val()+"'>"+input.val()+"</span></a>");
          setTimeout(function () {
            jQuery('#'+inpid+'Result').html("");
          }, 1000);
        }
      }
    });
  },

  addStateKeyToWorkflowDefinition : function(inp) {
    var id = jQuery(inp).select2("val");
    console.log(id);
    if (!Utils.validation.isNullCheck(id) && id.length) {
      var text = jQuery(inp).select2("container").attr("key");

      if (jQuery('#wfd-state-wrapper-'+id).length == 0) {
        jQuery('#workflowDefinitionStateFieldsList').prepend("<a id='wfd-state-wrapper-"+id+"' class='list-group-item dashboard'><span id='wfd-state-key-"+id+"' keyId='"+id+"' keyText='"+text+"'>"+text+"</span></a>");
      }
    }
  },

  addWorkflowProcessDefinitionToWorkflowDefinition : function(inp) {
    var id = jQuery(inp).select2("val");
    if (!Utils.validation.isNullCheck(id) && id.length) {
      var text = jQuery(inp).select2("container").attr("key");

      //if (jQuery('#wfpd-wrapper-'+id).length == 0) {
      jQuery('#workflowProcessDefinitionSelectionList').append("<a id='wfpd-wrapper-"+jQuery('#workflowProcessDefinitionSelectionList .list-group-item').length+"' class='list-group-item dashboard' processId='"+id+"'><span class='fa fa-arrows fa-fw fa-2x'></span><span id='wfpd-process-"+jQuery('#workflowProcessDefinitionSelectionList .list-group-item').length+"'>"+text+"</span></a>");
      //}
    }
  },

  addStateKeyToWorkflowProcessDefinition : function(inp) {
    var id = jQuery(inp).select2("val");
    if (!Utils.validation.isNullCheck(id) && id.length) {
      var text = jQuery(inp).select2("container").attr("key");

      if (jQuery('#wfpd-state-wrapper-'+id).length == 0) {
        jQuery('#workflowProcessDefinitionStateFieldsList').prepend("<a id='wfpd-state-wrapper-"+id+"' class='list-group-item dashboard'><span id='wfpd-state-key-"+id+"' keyId='"+id+"' keyText='"+text+"'>"+text+"</span></a>");
      }
    }
  },

  newWorkflow : function() {

  },

  checkForCompletionDate : function() {
    var statusVal = jQuery('input[name=status]:checked').val();
    if (!Utils.validation.isNullCheck(statusVal)) {
      if (statusVal === "Failed" || statusVal === "Stopped") {
        alert("You are manually setting a workflow to Stopped or Failed. Please remember to enter a Completion Date!");
        if (jQuery("#completionDate input").length == 0) {
          jQuery("#completionDate").html("<input type='text' name='completionDate' id='completionDate' value='" + jQuery('#completionDate').html() + "' class='form-control'>");
          Utils.ui.addDatePicker("completionDate");
        }
      }
      else {
        if (jQuery("#completionDate").length > 0) {
          jQuery("#completionDate").html(jQuery("#completionDate").val());
        }
      }
    }
  }
};

Workflow.definition = {
  saveWorkflowDefinition : function() {
    var btn = jQuery("#saveButton");
    btn.button('loading');

    // Then whatever you actually want to do i.e. submit form
    // After that has finished, reset the button state using
    var defName = jQuery('#wfdName').val();
    var defDesc = jQuery('#wfdDesc').val();
    if (!Utils.validation.isNullCheck(defName) && !Utils.validation.isNullCheck(defDesc)) {
      if (jQuery('#workflowProcessDefinitionSelectionList .list-group-item').length == 0) {
        alert("You have not selected any workflow processes for this workflow definition");
        btn.button('reset');
      }
      else {
        var s = {};

        if (jQuery('#wfdId').length && !Utils.validation.isNullCheck(jQuery('#wfdId').val())) {
          //add an id if one exists (i.e. if we're editing a workflow def, not on the manageWorkflows.jsp)
          s["id"] = jQuery('#wfdId').val();
        }

        s["name"] = defName;
        s["description"] = defDesc;

        var keys = [];
        jQuery('#workflowDefinitionStateFieldsList .list-group-item span').each(function() {
          var p = jQuery(this);
          var keyId = p.attr("keyId");
          var keyText = p.attr("keyText");
          if (!Utils.validation.isNullCheck(keyId) && !Utils.validation.isNullCheck(keyText)) {
            keys.push({"keyId":keyId, "keyText":keyText});
          }
        });
        s["keys"] = keys;

        var pros = [];
        jQuery('#workflowProcessDefinitionSelectionList .list-group-item').each(function() {
          var p = jQuery(this);
          var order = p.index();
          var processId = p.attr("processId");
          pros.push({"order":order, "processId":processId});
        });
        s["processes"] = pros;

        Fluxion.doAjax(
          'workflowControllerHelperService',
          'addWorkflowDefinition',
          {'definition': s, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            if (json.error) {
              btn.button('reset');
              alert("Unable to add key: " + json.error);
            }
            else {
              if (jQuery('#wfdList').length) {
                //add list-group-item if the definition list exists (i.e. the manageWorkflows.jsp, not the editWorkflowDefinition.jsp)
                jQuery('#wfdList').prepend("<a id='wfd-wrapper-"+json.definition.id+"' class='list-group-item dashboard'><span id='workflowDefinition"+json.definition.id+"'>Name: <b>"+json.definition.name+"</b><br/>Description: "+json.definition.description+"<br/>Creation Date: "+json.definition.creationDate+"</span></a>");
                jQuery('#wfdName').val("");
                jQuery('#wfdDesc').val("");
                jQuery('#workflowDefinitionStateFieldsList').html("");
                jQuery('#workflowProcessDefinitionSelectionList').html("");
              }
              setTimeout(function () {
                btn.button('reset');
              }, 1000);
            }
          }
        });
      }
    }
    else {
      alert("You have not entered a process name and/or description");
    }
  },

  saveWorkflowProcessDefinition : function() {
    var btn = jQuery("#saveButton");
    btn.button('loading');

    var processName = jQuery('#wfpdName').val();
    var processDesc = jQuery('#wfpdDesc').val();

    if (!Utils.validation.isNullCheck(processName) && !Utils.validation.isNullCheck(processDesc)) {
      var s = {};

      if (jQuery('#wfpdId').length && !Utils.validation.isNullCheck(jQuery('#wfpdId').val())) {
        //add an id if one exists (i.e. if we're editing a workflow process def, not on the manageWorkflows.jsp)
        s["id"] = jQuery('#wfpdId').val();
      }

      s["name"] = processName;
      s["description"] = processDesc;

      var inputType = jQuery('#inputType').val();
      if (!Utils.validation.isNullCheck(inputType)) {
        s["inputType"] = inputType;
      }

      var outputType = jQuery('#outputType').val();
      if (!Utils.validation.isNullCheck(outputType)) {
        s["outputType"] = outputType;
      }

      var keys = [];
      jQuery('#workflowProcessDefinitionStateFieldsList .list-group-item span').each(function() {
        var p = jQuery(this);
        var keyId = p.attr("keyId");
        var keyText = p.attr("keyText");
        if (!Utils.validation.isNullCheck(keyId) && !Utils.validation.isNullCheck(keyText)) {
          keys.push({"keyId":keyId, "keyText":keyText});
        }
      });
      s["keys"] = keys;

      Fluxion.doAjax(
        'workflowControllerHelperService',
        'addWorkflowProcessDefinition',
        {'processDefinition': s, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          if (json.error) {
            btn.button('reset');
            alert("Unable to add workflow process definition: " + json.error);
          }
          else {
            if (jQuery('#wfpdList').length) {
              //add list-group-item
              jQuery('#wfpdList').prepend("<a id='wfpd-wrapper-"+json.definition.id+"' class='list-group-item dashboard'><span id='workflowProcessDefinition"+json.definition.id+"'>Name: <b>"+json.definition.name+"</b><br/>Description: "+json.definition.description+"<br/>Creation Date: "+json.definition.creationDate+"</span></a>");

              //reset
              jQuery('#wfpdName').val("");
              jQuery('#wfpdDesc').val("");
              jQuery('#workflowProcessDefinitionStateFieldsList').html("");
            }
            setTimeout(function () {
              btn.button('reset');
            }, 1000);
          }
        }
      });
    }
    else {
      alert("You have not entered a process name and/or description");
    }
  }
};
