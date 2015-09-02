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

var Run = Run || {
  deleteRun: function (runId, successfunc) {
    if (confirm("Are you sure you really want to delete RUN" + runId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'runControllerHelperService',
        'deleteRun',
        {'runId': runId, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          successfunc();
        }
        }
      );
    }
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
      {'doOnSuccess': self.insertRunQCRow}
    );
  },

  insertRunQCRow: function (json, includeId) {
    if (!jQuery('#runQcTable').attr("qcInProgress")) {
      jQuery('#runQcTable').attr("qcInProgress", "true");

      $('runQcTable').insertRow(1);

      if (includeId) {
        var column1 = $('runQcTable').rows[1].insertCell(-1);
        column1.innerHTML = "<input type='hidden' id='runId' name='runId' value='" + json.runId + "'/>";
      }

      var column2 = $('runQcTable').rows[1].insertCell(-1);
      column2.innerHTML = "<select id='runQcUser' name='runQcUser'>" + json.qcUserOptions + "</select>";
      var column3 = $('runQcTable').rows[1].insertCell(-1);
      column3.innerHTML = "<input id='runQcDate' name='runQcDate' type='text'/>";
      var column4 = $('runQcTable').rows[1].insertCell(-1);
      column4.innerHTML = "<select id='runQcType' name='runQcType'/>";
      var column5 = $('runQcTable').rows[1].insertCell(-1);
      column5.innerHTML = "<div id='runQcProcessSelection' name='runQcProcessSelection'/>";
      var column6 = $('runQcTable').rows[1].insertCell(-1);
      column6.innerHTML = "<input id='runQcInformation' name='runQcInformation' type='text'/>";
      var column7 = $('runQcTable').rows[1].insertCell(-1);
      column7.innerHTML = "<input id='runQcDoNotProcess' name='runQcDoNotProcess' type='checkbox'/>";
      var column8 = $('runQcTable').rows[1].insertCell(-1);
      //column8.innerHTML = "<a href='javascript:void(0);' onclick='Run.qc.addRunQC(\"runQcTable\");'/>Add</a>";
      column8.innerHTML = "<a href='javascript:void(0);' onclick='Run.qc.addRunQC(this);'/>Add</a>";

      Utils.ui.addMaxDatePicker("runQcDate", 0);

      Fluxion.doAjax(
        'runControllerHelperService',
        'getRunQCTypes',
        {'url': ajaxurl},
        {'doOnSuccess': function (json) {
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
        {'doOnSuccess': function (json) {
          jQuery('#runQcProcessSelection').html(json.processSelection);
        }
        }
      );
    }
    else {
      alert("Cannot add another QC when one is already in progress.")
    }
  },

  addRunQC: function (row) {
    var a = [];
    //jQuery(jQuery(row).parent().parent(), '.partitionOccupied', 'td', '.containerSummary').each(function() {
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
        'url': ajaxurl},
      {'updateElement': 'runQcTable',
        'doOnSuccess': function (json) {
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
    s.html("<input type='text' id='sequencerPartitionContainers[" + fc + "].identificationBarcode' name='sequencerPartitionContainers[" + fc + "].identificationBarcode' value='" + s.html() + "'/>" +
           "<button onclick='Run.container.lookupContainer(this, " + fc + ");' type='button' class='fg-button ui-state-default ui-corner-all'>Lookup</button>");
  },

  editContainerLocationBarcode: function (span, fc) {
    var s = jQuery(span);
    s.html("<input type='text' id='sequencerPartitionContainers[" + fc + "].locationBarcode' name='sequencerPartitionContainers[" + fc + "].locationBarcode' value='" + s.html() + "'/>");
  },

  changePlatformType: function (form, runId) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'changePlatformType',
      {'platformtype': form.value, 'run_cId': jQuery('input[name=run_cId]').val(), 'runId': runId, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery('#sequencerReferenceSelect').html(json.sequencers);
        Run.pool.poolSearch("", jQuery('input[name=platformType]:checked').val());
      }
      }
    );
  },

  populateRunOptions: function (form, runId) {
    if (form.value != 0) {
      Fluxion.doAjax(
        'runControllerHelperService',
        'populateRunOptions',
        {'sequencerReference': form.value, 'run_cId': jQuery('input[name=run_cId]').val(), 'runId': runId, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          jQuery('#runPartitions').html(json.partitions);
        }
        }
      );
    }
  },

  createListingRunsTable: function () {
    jQuery('#listingRunsTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    jQuery.fn.dataTableExt.oSort['no-run-asc'] = function (x, y) {
      var a = parseInt(x.replace(/^RUN/i, ""));
      var b = parseInt(y.replace(/^RUN/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-run-desc'] = function (x, y) {
      var a = parseInt(x.replace(/^RUN/i, ""));
      var b = parseInt(y.replace(/^RUN/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
    Fluxion.doAjax(
      'runControllerHelperService',
      'listRunsDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#listingRunsTable').html('');
        jQuery('#listingRunsTable').dataTable({
          "aaData": json.runsArray,
          "aoColumns": [
            { "sTitle": "Run Name", "sType": "no-run"},
            { "sTitle": "Alias"},
            { "sTitle": "Status"},
            { "sTitle": "Start Date"},
            { "sTitle": "End Date"},
            { "sTitle": "Type"},
            { "sTitle": "Edit"}
          ],
          "bJQueryUI": true,
          "iDisplayLength": 25,
          "aaSorting": [
            [0, "desc"]
          ]
        });
      }
    });
  },

  changeIlluminaLane: function (t, container) {
    Fluxion.doAjax(
            'runControllerHelperService',
            'changeIlluminaLane',
            {'platform': 'Illumina', 'run_cId': jQuery('input[name=run_cId]').val(), 'numLanes': jQuery(t).val(), 'container': container, 'url': ajaxurl},
            {'updateElement': 'containerdiv' + container});
  },

  changeLS454Chamber: function (t, container) {
    Fluxion.doAjax(
            'runControllerHelperService',
            'changeChamber',
            {'platform': 'LS454', 'run_cId': jQuery('input[name=run_cId]').val(), 'numChambers': jQuery(t).val(), 'container': container, 'url': ajaxurl},
            {'updateElement': 'containerdiv' + container});
  },

  changeSolidChamber: function (t, container) {
    Fluxion.doAjax(
            'runControllerHelperService',
            'changeChamber',
            {'platform': 'Solid', 'run_cId': jQuery('input[name=run_cId]').val(), 'numChambers': t.value, 'container': container, 'url': ajaxurl},
            {'updateElement': 'containerdiv' + container});
  },

  changePacBioChamber: function (t, container) {
    Fluxion.doAjax(
            'runControllerHelperService',
            'changeChamber',
            {'platform': 'PacBio', 'run_cId': jQuery('input[name=run_cId]').val(), 'numChambers': t.value, 'container': container, 'url': ajaxurl},
            {'updateElement': 'containerdiv' + container});
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

    jQuery(function () {
      jQuery('#addRunNoteDialog').dialog({
         autoOpen: false,
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
    });
    jQuery('#addRunNoteDialog').dialog('open');
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
        {'runId': runId, 'noteId': noteId, 'url': ajaxurl},
        {'doOnSuccess': Utils.page.pageReload}
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
        'doOnSuccess': function (json) {
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
        'doOnSuccess': function (json) {
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
      {'platform': platform, 'run_cId': jQuery('input[name=run_cId]').val(), 'numContainers': numContainers, 'sequencerReferenceId': seqrefId, 'url': ajaxurl},
      {'updateElement': 'containerdiv'});
  },

  lookupContainer: function (t, containerNum) {
    var self = this;
    var barcode = jQuery('#sequencerPartitionContainers\\[' + containerNum + '\\]\\.identificationBarcode').val();
    if (!Utils.validation.isNullCheck(barcode)) {
      Fluxion.doAjax(
        'runControllerHelperService',
        'lookupContainer',
        {'barcode': barcode, 'containerNum': containerNum, 'url': ajaxurl},
        {'doOnSuccess': self.processLookup}
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
          dialogStr += "Partition " + key + ": " + json.verify[key] + "\n";
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
      Run.pool.getPool(jQuery("#poolBarcode" + partitionNum), containerNum)
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
        {'poolId': newpool.find('input').val(), 'partition': jQuery(this).attr("partition"), 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          newpool.append(json.html);
          newpool.append("<span style='position: absolute; top: 0; right: 0;' onclick='Run.pool.confirmPoolRemove(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
        },
          'doOnError': function (json) {
            newpool.remove();
            alert("Error adding pool, no Study is present.");
          }
        }
      );
    });
  },

  checkPoolExperiment: function(t, poolId, partitionNum) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'checkPoolExperiment',
      {'poolId': poolId, 'partition': partitionNum, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
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
      {'poolId': poolId, 'studyId': studyId, 'runId': jQuery("input[name='runId']").val(), 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        var div = jQuery("#studySelectDiv" + partition + "_" + projectId).parent();
        jQuery("#studySelectDiv" + partition + "_" + projectId).remove();
        div.append(json.html);
      },
        'doOnError': function (json) {
          Utils.ui.reenableButton('studySelectButton-' + partition + '_' + poolId, "Select Study");
        }
      }
    );
  },

  generateCasava17DemultiplexCSV: function (runId, containerId) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'generateIlluminaDemultiplexCSV',
      {'runId': runId, 'containerId': containerId, 'casavaVersion': '1.7', 'url': ajaxurl},
      {'doOnSuccess': function (json) {
          jQuery.colorbox({width:"90%",height:"100%",html:"<pre>"+json.response+"</pre>"});
        }
      }
    );
  },

  generateCasava18DemultiplexCSV: function (runId, containerId) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'generateIlluminaDemultiplexCSV',
      {'runId': runId, 'containerId': containerId, 'casavaVersion': '1.8', 'url': ajaxurl},
      {'doOnSuccess': function (json) {
          jQuery.colorbox({width:"90%",height:"100%",html:"<pre>"+json.response+"</pre>"});
        }
      }
    );
  }
};

