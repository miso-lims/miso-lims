/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

var Run = Run || {
  deleteRun: function (runId, successfunc) {
    if (confirm("Are you sure you really want to delete RUN" + runId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'runControllerHelperService',
        'deleteRun',
        {
          'runId': runId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            successfunc();
          }
        }
      );
    }
  },
  
  // Validate methods can be found in parsley_form_validations.js
  validateRun: function () {
    Validate.cleanFields('#run-form');  
    jQuery('#run-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '255');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    jQuery('#wellName').attr('class', 'form-control');
    jQuery('#wellName').attr('data-parsley-maxlength', '255');
    jQuery('#wellName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    jQuery('#movieDuration').attr('class', 'form-control');
    jQuery('#movieDuration').attr('data-parsley-maxlength', '10');
    jQuery('#movieDuration').attr('data-parsley-type', 'number');

    jQuery('#numCycles').attr('class', 'form-control');
    jQuery('#numCycles').attr('data-parsley-maxlength', '10');
    jQuery('#numCycles').attr('data-parsley-type', 'number');

    jQuery('#callCycle').attr('class', 'form-control');
    jQuery('#callCycle').attr('data-parsley-maxlength', '10');
    jQuery('#callCycle').attr('data-parsley-type', 'number');

    jQuery('#imgCycle').attr('class', 'form-control');
    jQuery('#imgCycle').attr('data-parsley-maxlength', '10');
    jQuery('#imgCycle').attr('data-parsley-type', 'number');

    jQuery('#scoreCycles').attr('class', 'form-control');
    jQuery('#scoreCycles').attr('data-parsley-maxlength', '10');
    jQuery('#scoreCycles').attr('data-parsley-type', 'number');

    jQuery('#cycles').attr('class', 'form-control');
    jQuery('#cycles').attr('data-parsley-maxlength', '10');
    jQuery('#cycles').attr('data-parsley-type', 'number');

    // Radio button validation: ensure a platform is selected
    jQuery('#platformType').attr('class', 'form-control');
    jQuery('#platformTypes1').attr('required', 'true');
    jQuery('#platformType').attr('data-parsley-error-message', 'You must select a Platform.');
    jQuery('#platformTypes1').attr('data-parsley-errors-container', '#platformError');
    jQuery('#platformType').attr('data-parsley-class-handler', '#platformButtons');
    
    // Sequencer select field validation
    jQuery('#sequencerReference').attr('class', 'form-control');
    jQuery('#sequencerReference').attr('required', 'true');
    jQuery('#sequencerReference').attr('data-parsley-min', '1');
    jQuery('#sequencerReference').attr('data-parsley-error-message', 'You must select a Sequencer.');
    jQuery('#sequencerReference').attr('data-parsley-errors-container', '#sequencerReferenceError');

    // Run path input field validation
    jQuery('#filePath').attr('class', 'form-control');
    jQuery('#filePath').attr('data-parsley-required', 'true');
    jQuery('#filePath').attr('data-parsley-maxlength', '100');

    jQuery('#run-form').parsley();
    jQuery('#run-form').parsley().validate();

    if(jQuery("#sequencerPartitionContainers\\[0\\]\\.identificationBarcode").length == 0) {
    	// Serial number is not being modified.
        Validate.updateWarningOrSubmit('#run-form', Run.checkStudiesSelected);
    } else {
    	// Ensure provided serial number is unique.
    	var serialNumber = jQuery("#sequencerPartitionContainers\\[0\\]\\.identificationBarcode").val();
    	var containerId = null;
		if(jQuery("#sequencerPartitionContainers0").length > 0) {
		  containerId = jQuery("#sequencerPartitionContainers0").val();
		}

	    Fluxion.doAjax(
        'containerControllerHelperService',
        'isSerialNumberUnique',
        {
          'serialNumber': serialNumber,
          'containerId': containerId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function(json) {
        	  if(json.isSerialNumberUnique === "true") {
                  Validate.updateWarningOrSubmit('#run-form', Run.checkStudiesSelected);
        	  } else {
        		  // Serial number is not unique.
                  var serialNumberField = jQuery('#sequencerPartitionContainers\\[0\\]\\.identificationBarcode').parsley();
            	  window.ParsleyUI.addError(serialNumberField, "serialNumberError", 'This serial number is already in use. Please choose another.');
            	  return false;
        	  }
          },
          'doOnError': function(json) {
          	// Unable to perform lookup.
          	alert(json.error);
          	window.ParsleyUI.addError(serialNumberField, "serialNumberError", 'Unable to determine if serial number is unique.');
          	return false;
           }
        }
      );
    }
  },
  
  checkStudiesSelected: function () {
    var ok = true;
    var error = "Please correct the following error(s):\n\n";

    if (jQuery('div[id^="studySelectDiv"]').length > 0) {
      if (!confirm("You haven't selected a study for one or more pools. Are you sure you still want to save?")) {
        ok = false;
        error += "Please select studies for all the pools added.\n";
      }
    }

    if (!ok) {
      alert(error);
      return ok;
    }

    jQuery('#run-form').submit();
    return ok;
  },
  
  checkForCompletionDate: function () {
    var statusVal = jQuery('input[name=status\\.health]:checked').val();
    if (!Utils.validation.isNullCheck(statusVal)) {
      if (statusVal === "Failed" || statusVal === "Stopped") {
        alert("You are manually setting a run to Stopped or Failed. Please remember to enter a Completion Date!");
        if (jQuery("#completionDate input").length === 0) {
          jQuery("#completionDate").html("<input type='text' name='status.completionDate' id='status.completionDate' value='" + jQuery('#completionDate').html() + "'>");
          Utils.ui.addDatePicker("status\\.completionDate");
        }
      }
      else {
        if (jQuery("#status\\.completionDate").length > 0) {
          jQuery("#completionDate").html(jQuery("#status\\.completionDate").val());
        }
      }
    }
  },

  makePacBioUrl: function (pbDashboardUrl, runName, startString, instrumentName) {
    function zeroPad (number, size) {
      number = number.toString();
      while (number.length < size) number = "0" + number;
      return number;
    }
    // runName format [givenName]_[numberOfTimesMachineHasBeenRun]. The PBDashboard stores the run name without the numberOfTimesMachineHasBeenRun
    var truncatedRunName = runName.split("_").slice(0, -1).join("_");
    // startString format YYYY-MM-DD; need YYYYMMDD
    var sd = startString.split("-");
    var startDate = new Date(sd[0], (parseInt(sd[1]) - 1), sd[2]);
    // day before start in format YYYYMMDD-000000
    var before = new Date(startDate.getTime() - 86400000);
    var from = "" + before.getFullYear() + zeroPad(before.getMonth() + 1, 2) + zeroPad(before.getDate(), 2) + "-" + zeroPad(0, 6);
    var after = new Date(startDate.getTime() + 86400000);
    var to = "" + after.getFullYear() + zeroPad(after.getMonth() + 1, 2) + zeroPad(after.getDate(), 2) + "-" + zeroPad(0, 6);
    var url = pbDashboardUrl
              + "?instrument=" + (instrumentName ? instrumentName : "")
              + "&run=" + truncatedRunName
              + "&from=" + from
              + "&to=" + to;
    var pbDashTd = jQuery('#pbDashLink');
    pbDashTd.html('<a href="' + url + '" target="_blank">Run Report (opens in new tab)</a>');
  }
};

Run.qc = {
  generateRunQCRow: function (runId) {
    var self = this;
    Fluxion.doAjax(
      'runControllerHelperService',
      'getRunQCUsers',
      {
        'runId': runId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': self.insertRunQCRow
      }
    );
  },

  insertRunQCRow: function (json, includeId) {
    if (!jQuery('#runQcTable').attr("qcInProgress")) {
      jQuery('#runQcTable').attr("qcInProgress", "true");

      jQuery('#runQcTable')[0].insertRow(1);

      if (includeId) {
        var column1 = jQuery('#runQcTable')[0].rows[1].insertCell(-1);
        column1.innerHTML = "<input type='hidden' id='runId' name='runId' value='" + json.runId + "'/>";
      }

      var column2 = jQuery('#runQcTable')[0].rows[1].insertCell(-1);
      column2.innerHTML = "<select id='runQcUser' name='runQcUser'>" + json.qcUserOptions + "</select>";
      var column3 = jQuery('#runQcTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='runQcDate' name='runQcDate' type='text'/>";
      var column4 = jQuery('#runQcTable')[0].rows[1].insertCell(-1);
      column4.innerHTML = "<select id='runQcType' name='runQcType'/>";
      var column5 = jQuery('#runQcTable')[0].rows[1].insertCell(-1);
      column5.innerHTML = "<div id='runQcProcessSelection' name='runQcProcessSelection'/>";
      var column6 = jQuery('#runQcTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<input id='runQcInformation' name='runQcInformation' type='text'/>";
      var column7 = jQuery('#runQcTable')[0].rows[1].insertCell(-1);
      column7.innerHTML = "<input id='runQcDoNotProcess' name='runQcDoNotProcess' type='checkbox'/>";
      var column8 = jQuery('#runQcTable')[0].rows[1].insertCell(-1);
      column8.innerHTML = "<a href='javascript:void(0);' onclick='Run.qc.addRunQC(this);'/>Add</a>";

      Utils.ui.addMaxDatePicker("runQcDate", 0);

      Fluxion.doAjax(
        'runControllerHelperService',
        'getRunQCTypes',
        {
          'url': ajaxurl
        },
        { 
          'doOnSuccess': function (json) {
            jQuery('#runQcType').html(json.types);
          }
        }
      );

      Fluxion.doAjax(
        'runControllerHelperService',
        'getRunQCProcessSelection',
        {
          'runId': json.runId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            jQuery('#runQcProcessSelection').html(json.processSelection);
          }
        }
      );
    } else {
      alert("Cannot add another QC when one is already in progress.");
    }
  },

  addRunQC: function (row) {
    var a = [];
    jQuery('.partitionOccupied', jQuery(row).parent().parent()).each(function () {
      a.push({id: this.id});
    });

    var runid = jQuery("input[name='runId']").val();
    var qctype = jQuery("select[name='runQcType'] :selected").val();
    var donotprocess = jQuery("input[name='runQcDoNotProcess']").is(":checked");

    Fluxion.doAjax(
      'runControllerHelperService',
      'addRunQC',
      {
        'runId': runid,
        'qcCreator': jQuery("select[name='runQcUser'] :selected").val(),
        'qcDate': jQuery("input[name='runQcDate']").val(),
        'qcType': qctype,
        'processSelection': a,
        'information': jQuery("input[name='runQcInformation']").val(),
        'doNotProcess': donotprocess,
        'url': ajaxurl
      },
      {
        'updateElement': 'runQcTable',
        'doOnSuccess': function () {
          jQuery('#runQcTable').removeAttr("qcInProgress");
          if ("SeqOps QC" === qctype && !donotprocess) {
            jQuery('#qcmenu').append("<a href='/miso/analysis/new/run/" + runid + "' class='add'>Initiate Analysis</a>");
          }
        }
      }
    );
  },

  toggleProcessPartition: function (partition) {
    if (jQuery(partition).hasClass("partitionOccupied")) {
      jQuery(partition).removeClass("partitionOccupied");
    }
    else {
      jQuery(partition).addClass("partitionOccupied");
    }
  }
};

Run.ui = {
  editContainerIdBarcode: function (span, fc) {
    var s = jQuery(span);
    var barcodeText = jQuery('a[title="idBarcode"]').text();
    s.html("<input type='text' id='sequencerPartitionContainers[" + fc + "].identificationBarcode' name='sequencerPartitionContainers[" + fc + "].identificationBarcode' value='" + barcodeText + "'/>" +
           "<button onclick='Run.container.lookupContainer(this, " + fc + ");' type='button' class='fg-button ui-state-default ui-corner-all'>Lookup</button>");
    if (jQuery('#pencil')) jQuery('#pencil').hide();
  },

  editContainerLocationBarcode: function (span, fc) {
    var s = jQuery(span);
    s.html("<input type='text' id='sequencerPartitionContainers[" + fc + "].locationBarcode' name='sequencerPartitionContainers[" + fc + "].locationBarcode' value='" + s.html() + "'/>");
  },

  populateRunOptions: function (form, runId) {
    if (form.value !== 0) {
      Fluxion.doAjax(
        'runControllerHelperService',
        'populateRunOptions',
        {
          'sequencerReference': form.value,
          'run_cId': jQuery('input[name=run_cId]').val(),
          'runId': runId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            jQuery('#runPartitions').html(json.partitions);
            jQuery('#container1').click();
          }
        }
      );
    }
  },

  createListingRunsTable: function () {
    jQuery('#listingRunsTable').html("");    
    jQuery('#listingRunsTable').dataTable(Utils.setSortFromPriority({
      "aoColumns": [
        {
          "sTitle": "Run Name",
          "mData": "id",
          "mRender": function (data, type, full) {
            return "<a href=\"/miso/run/" + data + "\">" + full.name + "</a>";
          },
          "iSortPriority": 1
        },
        {
          "sTitle": "Alias",
          "mData": "alias",
          "mRender": function (data, type, full) {
            return "<a href=\"/miso/run/" + full.id + "\">" + data + "</a>";
          },
          "iSortPriority": 0
        },
        {
          "sTitle": "Status",
          "mData": "status",
          "mRender": function (data, type, full) {
            return (data ? data : "");
          },
          "iSortPriority": 0,
          "bSortable": false // status, start date and end date are pulled via status dao, not via run dao
        },
        {
          "sTitle": "Start Date",
          "mData": "startDate",
          "mRender": function (data, type, full) {
            return (data ? data : "");
          },
          "iSortPriority": 0,
          "bSortable": false
        },
        {
          "sTitle": "End Date",
          "mData": "endDate",
          "mRender": function (data, type, full) {
            return (data ? data : "");
          },
          "iSortPriority": 0,
          "bSortable": false
        },
        {
          "sTitle": "Type",
          "mData": "platformType",
          "iSortPriority": 0
        },
        {
          "sTitle": "Last Modified",
          "mData": "lastModified",
          "iSortPriority": 2,
          "bVisible": (Sample.detailedSample ? "true" : "false")
        }
      ],
      "bJQueryUI": true,
      "bAutoWidth": false,
      "iDisplayLength": 25,
      "iDisplayStart": 0,
      "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
      "sPaginationType": "full_numbers",
      "bProcessing": true,
      "bServerSide": true,
      "sAjaxSource": "/miso/rest/run/dt",
      "fnServerData": function (sSource, aoData, fnCallback) {
        jQuery('#listingRunsTable').addClass('disabled');
        jQuery.ajax({
          "dataType": "json",
          "type": "GET",
          "url": sSource,
          "data": aoData,
          "success": fnCallback // Do not alter this DataTables property
        });
      },
      "fnDrawCallback": function (oSettings) {
        jQuery('#listingRunsTable').removeClass('disabled');
        jQuery('#listingRunsTable_paginate').find('.fg-button').removeClass('fg-button');
      }
    })).fnSetFilteringDelay();
    jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
  },

  changeIlluminaLane: function (t, container) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'changeIlluminaLane',
      {
        'platform': 'Illumina',
        'run_cId': jQuery('input[name=run_cId]').val(),
        'numLanes': jQuery(t).val(),
        'container': container,
        'url': ajaxurl
      },
      {
        'updateElement': 'containerdiv' + container
      }
    );
  },

  changeLS454Chamber: function (t, container) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'changeChamber',
      {
        'platform': 'LS454',
        'run_cId': jQuery('input[name=run_cId]').val(),
        'numChambers': jQuery(t).val(),
        'container': container,
        'url': ajaxurl
      },
      {
        'updateElement': 'containerdiv' + container
      }
    );
  },

  changeSolidChamber: function (t, container) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'changeChamber',
      {
        'platform': 'Solid',
        'run_cId': jQuery('input[name=run_cId]').val(),
        'numChambers': t.value,
        'container': container,
        'url': ajaxurl
      },
      {
        'updateElement': 'containerdiv' + container
      }
    );
  },

  changePacBioChamber: function (t, container) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'changeChamber',
      {
        'platform': 'PacBio',
        'run_cId': jQuery('input[name=run_cId]').val(),
        'numChambers': t.value,
        'container': container,
        'url': ajaxurl
      },
      {
        'updateElement': 'containerdiv' + container
      }
    );
  },

  showRunNoteDialog: function (runId) {
    var self = this;
    jQuery('#addRunNoteDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<label for='internalOnly'>Internal Only?</label>" +
            "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
            "<br/>" +
            "<label for='notetext'>Text</label>" +
            "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery('#addRunNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function () {
          self.addRunNote(runId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  addRunNote: function (runId, internalOnly, text) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'addRunNote',
      {
        'runId': runId,
        'internalOnly': internalOnly,
        'text': text,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  deleteRunNote: function (runId, noteId) {
    if (confirm("Are you sure you want to delete this note?")) {
      Fluxion.doAjax(
        'runControllerHelperService',
        'deleteRunNote',
        {
          'runId': runId,
          'noteId': noteId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload
        }
      );
    }
  }
};

Run.alert = {
  watchRun: function (runId) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'watchRun',
      {
        'runId': runId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function () {
          Utils.page.pageReload();
        }
      }
    );
  },

  unwatchRun: function (runId) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'unwatchRun',
      {
        'runId': runId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function () {
          Utils.page.pageReload();
        }
      }
    );
  }
};

Run.pool = {
  toggleReadyToRunCheck: function (checkbox, platform) {
    var self = this;
    self.poolSearch(jQuery('#searchPools').val(), platform);
  },

  poolSearch: function (input, platform) {
    var readyBool = false;
    if (jQuery('#showOnlyReady').attr('checked')) {
      readyBool = true;
    }
    jQuery('#poolList').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
    'poolSearchService',
    'poolSearch',
    {'str': input, 'readyOnly': readyBool, 'platformType': platform, 'url': ajaxurl},
    {
      "doOnSuccess": function (json) {
        jQuery('#poolList').html(json.html);
        jQuery('#poolList .dashboard').each(function() {
          var inp = jQuery(this);
          inp.dblclick(function() {
            Run.container.insertPoolNextAvailable(inp);
          });
        });
      }
    });
  },

  getPool: function (t, containerNum) {
    var a = jQuery(t);
    var platform = jQuery("input[name='platformTypes']:checked").val();
    var pNum = a.attr("partition");
    Fluxion.doAjax(
      'runControllerHelperService',
      'getPoolByBarcode',
      {'platform': platform, 'run_cId': jQuery('input[name=run_cId]').val(), 'container': containerNum, 'partition': pNum, 'barcode': a.val(), 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        if (json.err) {
          jQuery("#msg" + pNum).html(json.err);
        }
        else {
          a.parent().html(json.html);
        }
      }
    });
  },

  confirmPoolRemove: function (t) {
    if (confirm("Remove this pool?")) {
      jQuery(t).parent().remove();
    }
  }
};

Run.container = {
  changeContainer: function (numContainers, platform, seqrefId) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'changeContainer',
      {
        'platform': platform,
        'run_cId': jQuery('input[name=run_cId]').val(),
        'numContainers': numContainers,
        'sequencerReferenceId': seqrefId,
        'url': ajaxurl
      },
      {
        'updateElement': 'containerdiv'
      }
    );
  },

  lookupContainer: function (t, containerNum) {
    var self = this;
    var barcode = jQuery('#sequencerPartitionContainers\\[' + containerNum + '\\]\\.identificationBarcode').val();
    if (!Utils.validation.isNullCheck(barcode)) {
      Fluxion.doAjax(
        'runControllerHelperService',
        'lookupContainer',
        {
          'barcode': barcode,
          'containerNum': containerNum,
          'url': ajaxurl
        },
        {
          'doOnSuccess': self.processLookup
        }
      );
    }
  },

  processLookup: function (json) {
    if (json.err) {
      jQuery('#partitionErrorDiv').html(json.err);
    }
    else {
      if (json.verify) {
        var dialogStr = "Container Properties\n\n";
        for (var key in json.verify) {
          if (json.verify.hasOwnProperty(key)) {
            dialogStr += "Partition " + key + ": " + json.verify[key] + "\n";
          }
        }

        if (confirm("Found container '" + json.barcode + "'. Import this container?\n\n" + dialogStr)) {
          jQuery('#partitionErrorDiv').html("");
          jQuery('#partitionDiv').html(json.html);
        }
      }
    }
  },

  populatePartition: function (t, containerNum, partitionNum) {
    var a = jQuery(t);
    a.html("<input type='text' style='width:90%' id='poolBarcode" + partitionNum + "' name='poolBarcode" + partitionNum + "' partition='" + partitionNum + "'/><button onclick='Run.container.clearPartition("+partitionNum+")' type='button' class='fg-button ui-state-default ui-corner-all'>Cancel</button><br/><span id='msg" + partitionNum + "'/>");

    Utils.ui.escape(jQuery("#poolBarcode" + partitionNum), function () {
      a.html("");
    });

    Utils.timer.typewatchFunc(jQuery("#poolBarcode" + partitionNum), function () {
      Run.pool.getPool(jQuery("#poolBarcode" + partitionNum), containerNum);
    }, 300, 2);
  },

  clearPartition: function (partitionNum) {
    jQuery("#poolBarcode" + partitionNum).parent().html("");
  },

  insertPoolNextAvailable: function (poolLi) {
    var pool = jQuery(poolLi);
    jQuery('.runPartitionDroppable:empty:first').each(function () {
      var newpool = pool.clone().appendTo(jQuery(this));
      newpool.removeAttr("ondblclick");
      newpool.find('input').attr("name", jQuery(this).attr("bind"));

      Fluxion.doAjax(
        'runControllerHelperService',
        'checkPoolExperiment',
        {
          'poolId': newpool.find('input').val(),
          'partition': jQuery(this).attr("partition"),
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            newpool.append(json.html);
            newpool.append("<span style='position: absolute; top: 0; right: 0;' onclick='Run.pool.confirmPoolRemove(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
          },
            'doOnError': function () {
              newpool.remove();
              alert("Error adding pool, no Study is present.");
            }
          }
        );
      }
    );
  },

  checkPoolExperiment: function(t, poolId, partitionNum) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'checkPoolExperiment',
      {
        'poolId': poolId,
        'partition': partitionNum,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery(t).append(json.html);
        },
        'doOnError': function (json) {
          alert("Error populating partition: " + json.error);
        }
      }
    );
  },

  selectStudy: function (partition, poolId, projectId) {
    Utils.ui.disableButton('studySelectButton-' + partition + '_' + poolId);
    var studyId = jQuery("select[name='poolStudies" + partition + "_" + projectId + "'] :selected").val();

    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectStudyForPool',
      {
        'poolId': poolId,
        'studyId': studyId,
        'runId': jQuery("input[name='runId']").val(),
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          var div = jQuery("#studySelectDiv" + partition + "_" + projectId).parent();
          jQuery("#studySelectDiv" + partition + "_" + projectId).remove();
          div.append(json.html);
        },
        'doOnError': function () {
          Utils.ui.reenableButton('studySelectButton-' + partition + '_' + poolId, "Select Study");
        }
      }
    );
  },

  generateCasava17DemultiplexCSV: function (runId, containerId) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'generateIlluminaDemultiplexCSV',
      {
        'runId': runId,
        'containerId': containerId,
        'casavaVersion': '1.7',
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery.colorbox({width:"90%",height:"100%",html:"<pre>"+json.response+"</pre>"});
        }
      }
    );
  },

  generateCasava18DemultiplexCSV: function (runId, containerId) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'generateIlluminaDemultiplexCSV',
      {
        'runId': runId,
        'containerId': containerId,
        'casavaVersion': '1.8',
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery.colorbox({width:"90%",height:"100%",html:"<pre>"+json.response+"</pre>"});
        }
      }
    );
  }
};
