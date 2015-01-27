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

var Tasks = Tasks || {};

Tasks.ui = {
  populateRunningTasks : function() {
    var self = this;
    jQuery('#runningDiv').prepend('<img id="runningThrobber" src="/styles/images/ajax-loader.gif"/>');
    Fluxion.doAjax(
            'taskControllerHelperService',
            'populateRunningTasks',
    {'url':ajaxurl},
    {'doOnSuccess':self.processRunningTasks}
    );
  },

  processRunningTasks : function(json) {
    jQuery('#runningThrobber').remove();

    // clear table
    jQuery('#runningTasks tbody').html("");

    if (json.runningTasks.length > 0) {
      for (var i = 0; i < json.runningTasks.length; i++) {
        var task = json.runningTasks[i];
        var processRuns = task.conanProcessRuns;
        var rowtext = "<td>"+task.id+"</td><td>"+task.name+"</td><td>"+task.pipeline.name+"</td><td>"+task.statusMessage+"</td><td>"+new Date(task.startDate)+"</td>";
        rowtext += "<td><table width='100%' border='0'><tr>";
        for (var j = 0; j < task.pipeline.processes.length; j++) {
          rowtext += "<td id='"+task.id+"-"+task.pipeline.processes[j].name+"'>"+task.pipeline.processes[j].name+"</td>";
        }

        rowtext += "</tr></table></td>";
        jQuery('#runningTasks > tbody:last')
          .append(jQuery('<tr>')
            .append(rowtext)
        );

        for (var j = 0; j < task.pipeline.processes.length; j++) {
          var process = task.pipeline.processes[j];
          for (var k = 0; k < processRuns.length; k++) {
            if (processRuns[k].processName === process.name) {
              if (processRuns[k].exitValue == 0) {
                jQuery('#'+task.id+"-"+task.pipeline.processes[j].name).addClass("ok");
                jQuery('#'+task.id+"-"+task.pipeline.processes[j].name).attr("title", "Finished: " + new Date(processRuns[k].endDate));
              }
              else if (processRuns[k].exitValue == -1) {
                jQuery('#'+task.id+"-"+task.pipeline.processes[j].name).addClass("running");
                jQuery('#'+task.id+"-"+task.pipeline.processes[j].name).attr("title", "Started: " + new Date(processRuns[k].startDate));
              }
              else if (processRuns[k].exitValue == 1 || processRuns[k].exitValue == 255) {
                jQuery('#'+task.id+"-"+task.pipeline.processes[j].name).addClass("error");
              }
            }
          }
        }
      }

      jQuery('#runningTasks').dataTable();
    }
  },

  populatePendingTasks : function() {
    var self = this;
    jQuery('#pendingDiv').prepend('<img id="pendingThrobber" src="/styles/images/ajax-loader.gif"/>');
    Fluxion.doAjax(
            'taskControllerHelperService',
            'populatePendingTasks',
    {'url':ajaxurl},
    {'doOnSuccess':self.processPendingTasks}
    );
  },

  processPendingTasks : function(json) {
    jQuery('#pendingThrobber').remove();
    // clear table
    jQuery('#pendingTasks tbody').html("");

    if (json.pendingTasks.length > 0) {
      for (var i = 0; i < json.pendingTasks.length; i++) {
        var task = json.pendingTasks[i];
        var rowtext = "<td>"+task.id+"</td><td>"+task.name+"</td><td>"+task.pipeline.name+"</td><td>"+task.statusMessage+"</td><td>"+new Date(task.startDate)+"</td>";
        jQuery('#pendingTasks > tbody:last')
          .append(jQuery('<tr>')
            .append(rowtext)
        );
      }

      jQuery('#pendingTasks').dataTable();
    }
  },

  populateFailedTasks : function() {
    var self = this;
    jQuery('#failedDiv').prepend('<img id="failedThrobber" src="/styles/images/ajax-loader.gif"/>');
    Fluxion.doAjax(
            'taskControllerHelperService',
            'populateFailedTasks',
    {'url':ajaxurl},
    {'doOnSuccess':self.processFailedTasks}
    );
  },

  processFailedTasks : function(json) {
    jQuery('#failedThrobber').remove();
    // clear table
    jQuery('#failedTasks tbody').html("");

    if (json.failedTasks.length > 0) {
      for (var i = 0; i < json.failedTasks.length; i++) {
        var task = json.failedTasks[i];
        var rowtext = "<td>"+task.id+"</td><td>"+task.name+"</td><td>"+task.pipeline.name+"</td><td>"+task.statusMessage+"</td><td>"+new Date(task.startDate)+"</td>";
        jQuery('#failedTasks > tbody:last')
          .append(jQuery('<tr>')
            .append(rowtext)
        );
      }

      jQuery('#failedTasks').dataTable();
    }
  },

  populateCompletedTasks : function() {
    var self = this;
    jQuery('#completedDiv').prepend('<img id="completedThrobber" src="/styles/images/ajax-loader.gif"/>');
    Fluxion.doAjax(
            'taskControllerHelperService',
            'populateCompletedTasks',
    {'url':ajaxurl},
    {'doOnSuccess':self.processCompletedTasks}
    );
  },

  processCompletedTasks : function(json) {
    jQuery('#completedThrobber').remove();
    // clear table
    jQuery('#completedTasks tbody').html("");

    if (json.completedTasks.length > 0) {
      for (var i = 0; i < json.completedTasks.length; i++) {
        var task = json.completedTasks[i];
        var rowtext = "<td>"+task.id+"</td><td>"+task.name+"</td><td>"+task.pipeline.name+"</td><td>"+task.statusMessage+"</td><td>"+new Date(task.startDate)+"</td><td>"+new Date(task.completionDate)+"</td><td>"+task.currentState+"</td>";
        jQuery('#completedTasks > tbody:last')
          .append(jQuery('<tr>')
            .append(rowtext)
        );
      }

      jQuery('#completedTasks').dataTable();
    }
  },

  populatePipelines : function() {
    var self = this;
    Fluxion.doAjax(
            'taskControllerHelperService',
            'populatePipelines',
    {'url':ajaxurl},
    {'doOnSuccess':self.processPipelines}
    );
  },

  processPipelines : function(json) {
    for (var i = 0; i < json.pipelines.length; i++) {
      var pipeline = json.pipelines[i];

      jQuery('#pipelines > tbody:last').append(jQuery('<tr>').append('<td>'+pipeline.name+'</td><td id="'+pipeline.name+'Details"></td>'));

      var rowtext = jQuery('#'+pipeline.name+'Details');

      rowtext.append("<h2>"+pipeline.name+"</h2>");
      rowtext.append("<i>Required parameters are marked (<span style='color: red'>*</span>).</i>");

      for (var j = 0; j < pipeline.processes.length; j++) {
        var process = pipeline.processes[j];

        rowtext.append("<h4>"+process.name+"</h4>");

        rowtext.append("<table class='list' id='"+process.name+"-reqParams'><thead><tr><th>Name</th><th>Properties</th></tr></thead><tbody></tbody></table>");

        for (var k = 0; k < process.parameters.length; k++) {
          var parameter = process.parameters[k];

          jQuery('#'+process.name+'-reqParams > tbody:last').append(jQuery('<tr>').append("<td>"+parameter.name+"</td><td>"+
                       "Required: " + !parameter.optional + "<br/>" +
                       "Boolean: " + parameter.boolean + "<br/>" +
                       "Transient: " + parameter.transient + "</td>"
          ));
        }
      }
    }
  },

  selectPipeline : function(select, runId) {
    var self = this;
    var s = jQuery(select);
    if (!Utils.validation.isNullCheck(s.val())) {
      Fluxion.doAjax(
              'taskControllerHelperService',
              'getPipeline',
      {'url':ajaxurl, 'pipeline':s.val(), 'runId':runId},
      {'doOnSuccess':self.processPipeline}
      );
    }
  },

  processPipeline : function(json) {
    if (json.pipeline) {
      var pipeline = json.pipeline;
      jQuery('#pipelineDetails').html("");
      jQuery('#pipelineDetails').append("<h2>"+pipeline.name+"</h2>");

      if (jQuery('#base-file-path').length > 0) {
        jQuery('#pipelineDetails').append("Base Path:"+jQuery('#base-file-path').val()+"<br/>");
      }

      jQuery('#pipelineDetails').append("<i>Required parameters (<span style='color: red'>*</span>) must be filled in. Optional parameters can be left blank if not used.</i>");

      if (pipeline.allRequiredParameters.length > 0) {
        jQuery('#pipelineDetails').append("<table class='list' id='"+pipeline.name+"-reqParams'><thead><tr><th>Name</th><th>Value</th></tr></thead><tbody></tbody></table>");

        for (var i = 0; i < pipeline.allRequiredParameters.length; i++) {
          var parameter = pipeline.allRequiredParameters[i];
          if (!parameter.transient) {
            if (parameter.boolean) {
              if (parameter.optional) {
                jQuery('#'+pipeline.name+'-reqParams > tbody:last')
                .append(jQuery('<tr>')
                  .append("<td>"+parameter.name+"</td><td><input optional='true' type='checkbox' name='"+parameter.name+"'/>")
                );
              }
              else {
                jQuery('#'+pipeline.name+'-reqParams > tbody:last')
                .append(jQuery('<tr>')
                  .append("<td>"+parameter.name+"</td><td><input required='true' type='checkbox' name='"+parameter.name+"'/>")
                );
              }
            }
            else {
              if (parameter.optional) {
                jQuery('#'+pipeline.name+'-reqParams > tbody:last')
                  .append(jQuery('<tr>')
                  .append("<td>"+parameter.name+"</td><td><input optional='true' style='width:98%' type='text' id='"+parameter.name+"' name='"+parameter.name+"' value='' class='form-control'/>")
                );
              }
              else {
                jQuery('#'+pipeline.name+'-reqParams > tbody:last')
                  .append(jQuery('<tr>')
                  .append("<td>"+parameter.name+" <span style='color: red'>*</span></td><td><input style='width:98%' required='true' type='text' id='"+parameter.name+"' name='"+parameter.name+"' value='' class='form-control'/>")
                );
              }
            }
          }
        }
      }

      for (var j = 0; j < pipeline.processes.length; j++) {
        var process = pipeline.processes[j];
        for (var k = 0; k < process.parameters.length; k++) {
          if (jQuery('input[name="'+process.parameters[k].name+'"]').length == 0) {
            jQuery('#'+pipeline.name+'-reqParams > tbody:last')
              .append(jQuery('<tr>')
              .append("<td>"+process.parameters[k].name+"</td><td><input style='width:98%' optional='true' type='text' id='"+process.parameters[k].name+"' name='"+process.parameters[k].name+"' value='' class='form-control'/>")
            );
          }
        }
      }
    }

    jQuery('input[type=hidden]').each(function(e) {
      var n = jQuery(this).attr("name");
      var v = jQuery(this).attr("value");
      var act = n.replace('default-','');
      var inp = jQuery('input[name="'+act+'"]');
      if (inp.attr("type") == "checkbox" && v == "on") {
        inp.attr("checked", "checked");
      }
      inp.val(v);
    });
  }
};

Tasks.job = {
  submitAnalysisTask : function() {
    var self = this;
    var okString = "Please fix the following issues:\n\n";
    var ok = true;

    Utils.ui.disableButton('submitTaskButton');

    jQuery('#pipelineDetails').find('input').each(function(e) {
      var inp = jQuery(this);
      if (inp.attr("type") == "checkbox") {
        if (inp.attr("optional") && !inp.is(":checked")) {
          inp.attr("disabled", "disabled");
        }
      }
      else {
        if (inp.attr("required")) {
          if (Utils.validation.isNullCheck(inp.val())) {
            ok = false;
            okString += inp.attr("name") + " is a required parameter\n";
          }
        }
        else if (inp.attr("optional")) {
          if (Utils.validation.isNullCheck(inp.val())) {
            inp.attr("disabled", "disabled");
          }
        }
      }
    });

    if (ok) {
      Fluxion.doAjax(
              'taskControllerHelperService',
              'submitJob',
      {'url':ajaxurl, 'submit':jQuery("#taskForm").serializeArray()},
      {'doOnSuccess':self.processTaskSubmission,
       'doOnError':function(json) {
         jQuery('#pipelineDetails').find('input').each(function(e) {
           if (jQuery(this).attr("disabled")) {
             jQuery(this).removeAttr("disabled");
           }
         });
         alert(json.error);
       }
      });
    }
    else {
      Utils.ui.reenableButton('submitTaskButton', "Submit Task");

      jQuery('#pipelineDetails').find('input').each(function(e) {
        if (jQuery(this).attr("disabled")) {
          jQuery(this).removeAttr("disabled");
        }
      });
      alert(okString);
    }
  },

  processTaskSubmission : function(json) {
    Utils.ui.reenableButton('submitTaskButton', "Submit Task");

    if (json.response) {
      if (confirm(json.response+". Return to Analysis page?")) {
        Utils.page.pageRedirect('/miso/analysis');
      }
    }
    else {
      Utils.page.pageRedirect('/miso/analysis');
    }
  }
};