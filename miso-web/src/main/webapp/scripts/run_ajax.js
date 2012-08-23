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

function editContainerIdBarcode(span, fc) {
  var s = jQuery(span);
  s.html("<input type='text' id='sequencerPartitionContainers[" + fc + "].identificationBarcode' name='sequencerPartitionContainers[" + fc + "].identificationBarcode' value='" + s.html() + "'/>" +
         "<button onclick='lookupContainer(this, " + fc + ");' type='button' class='fg-button ui-state-default ui-corner-all'>Lookup</button>");
}

function editContainerLocationBarcode(span, fc) {
  var s = jQuery(span);
  s.html("<input type='text' id='sequencerPartitionContainers[" + fc + "].locationBarcode' name='sequencerPartitionContainers[" + fc + "].locationBarcode' value='" + s.html() + "'/>");
}

function setupPoolList() {
  jQuery("#poolList").sortable({
                                 revert: true
                               });
  jQuery(".draggable").draggable({
                                   connectToSortable: '#poolList',
                                   revert: true,
                                   helper: 'clone',
                                   scroll: false
                                 });
  jQuery("ul, li").disableSelection();

  updateDroppables(".runPartitionDroppable");

  jQuery(".elementListDroppable").droppable({
    accept: '.draggable',
    activeClass: 'ui-state-hover',
    hoverClass: 'ui-state-active',
    tolerance: 'pointer',
    drop: function(event, ui) {
      jQuery(ui.draggable).find('input').attr("name", "");
      jQuery(ui.draggable).appendTo(jQuery(this));
    }
  });

  //sort by name
  jQuery("ul#poolList>li").tsort({attr:"pName"});
}

function setupExperimentList() {
  jQuery("#experimentList").sortable({
     revert: true
   });
  jQuery(".draggable").draggable({
     connectToSortable: '#experimentList',
     revert: true,
     helper: 'clone',
     scroll: false
   });
  jQuery("ul, li").disableSelection();
  updateDroppables(".runPartitionDroppable");

  jQuery(".elementListDroppable").droppable({
    accept: '.draggable',
    activeClass: 'ui-state-hover',
    hoverClass: 'ui-state-active',
    tolerance: 'pointer',
    drop: function(event, ui) {
      jQuery(ui.draggable).find('input').attr("name", "");
      jQuery(ui.draggable).appendTo(jQuery(this));
    }
  });

  //sort by alias
  jQuery("ul#experimentList>li").tsort({attr:"expAlias"});
}

function retrieveRunInformation(input) {
  if (jQuery("input[name=platformType]").is(":checked")) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'retrieveRunInformation',
      {'run':input, 'platform':jQuery("input[name='platformType']:checked").val(), 'url':ajaxurl},
      {'doOnSuccess':processRunInformation}
    );
  }
  else {
    alert("Platform type not selected. Please choose the run platform type.");
  }
}

var processRunInformation = function(json) {
  var health = json.health;
  var containers = json.containers;
  var chambers = json.chambers;
  var platform = json.platform;

  jQuery("input[name='status.health'][value='" + health + "']").attr("checked", "checked");
  jQuery("input[name='containerselect'][value='1']").attr("checked", "checked");

  resetPoolDraggables();
  Fluxion.doAjax(
    'runControllerHelperService',
    'changeContainer',
    {'platform':platform, 'numContainers':jQuery("input[name='containerselect']").val(), 'url':ajaxurl},
    {'doOnSuccess':function(j) {
      jQuery('#containerdiv').html(j.response);
      Fluxion.doAjax(
              'runControllerHelperService',
              'changeChamber',
              {'platform':platform, 'numChambers':jQuery("input[name='container" + (containers - 1) + "Select'][value=" + chambers + "]").val(), 'container':(containers - 1), 'url':ajaxurl},
              {'updateElement':'containerdiv' + (containers - 1)});
      jQuery("input[name='container" + (containers - 1) + "Select'][value=" + chambers + "]").attr("checked", "checked");
    }});
};

function changePlatformType(form, runId) {
  Fluxion.doAjax(
    'runControllerHelperService',
    'changePlatformType',
    {'platformtype':form.value, 'run_cId':jQuery('input[name=run_cId]').val(), 'runId':runId, 'url':ajaxurl},
    {'doOnSuccess':
      function(json) {
        jQuery('#sequencerReferenceSelect').html(json.sequencers);
        poolSearch("", jQuery('input[name=platformType]:checked').val());
      }
    }
  );
}

function populateRunOptions(form, runId) {
  if (form.value != 0) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'populateRunOptions',
      {'sequencerReference':form.value, 'run_cId':jQuery('input[name=run_cId]').val(), 'runId':runId, 'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          jQuery('#runPartitions').html(json.partitions);
        }
      }
    );
  }
}

function previewRunImport(radio, runPath, expId) {
  Fluxion.doAjax(
    'runControllerHelperService',
    'previewRunImport',
    {'platformType':$RF(radio), 'runPath':runPath, 'experimentId':expId, 'url':ajaxurl},
    {'updateElement':'runPartitions'}
  );
}

function validateIlluminaPoolBarcode(t) {
  var func = function() {
    Fluxion.doAjax(
      'runControllerHelperService',
      'validateIlluminaPoolBarcode',
      {'barcode':t.value, 'url':ajaxurl, 'id':t.id},
      {'doOnSuccess': function(json) {
        if (json.valid) {
          t.addClass("valid");
        }
        else {
          t.addClass("invalid");
        }
      }});
  };
  timedFunc(func, 200);
}

function validateLS454PoolBarcode(t) {
  var func = function() {
    Fluxion.doAjax(
      'runControllerHelperService',
      'validateLS454Barcode',
      {'barcode':t.value, 'url':ajaxurl, 'id':t.id},
      {'doOnSuccess': function(json) {
        if (json.valid) {
          t.addClass("valid");
        }
        else {
          t.addClass("invalid");
        }
      }});
  };
  timedFunc(func, 200);
}

function validateSolidPoolBarcode(t) {
  var func = function() {
    Fluxion.doAjax(
      'runControllerHelperService',
      'validateSolidPoolBarcode',
      {'barcode':t.value, 'url':ajaxurl, 'id':t.id},
      {'doOnSuccess': function(json) {
        if (json.valid) {
          if (t.hasClass("invalid")) t.removeClass("invalid");
          t.addClass("valid");
        }
        else {
          if (t.hasClass("valid")) t.removeClass("valid");
          t.addClass("invalid");
        }
      }});
  };
  timedFunc(func, 200);
}

function $RF(el, radioGroup) {
  if ($(el).type && $(el).type.toLowerCase() == 'radio') {
    var radioGroup = $(el).name;
    var el = $(el).form;
  } else if ($(el).tagName.toLowerCase() != 'form') {
    return false;
  }

  var checked = $(el).getInputs('radio', radioGroup).find(
          function(re) {
            return re.checked;
          });
  return (checked) ? $F(checked) : null;
}

function changeLS454Container(t) {
  resetPoolDraggables();
  Fluxion.doAjax(
          'runControllerHelperService',
          'changeContainer',
          {'platform':'LS454', 'numContainers':jQuery(t).val(), 'url':ajaxurl},
          {'updateElement':'containerdiv'});
}

function changeLS454Chamber(t, container) {
  resetPoolDraggables();
  Fluxion.doAjax(
          'runControllerHelperService',
          'changeChamber',
          {'platform':'LS454', 'run_cId':jQuery('input[name=run_cId]').val(), 'numChambers':jQuery(t).val(), 'container':container, 'url':ajaxurl},
          {'updateElement':'containerdiv' + container});
}

function changeSolidContainer(t) {
  resetPoolDraggables();
  Fluxion.doAjax(
          'runControllerHelperService',
          'changeContainer',
          {'platform':'Solid', 'numContainers':t.value, 'url':ajaxurl},
          {'updateElement':'containerdiv'});
}

function changeSolidChamber(t, container) {
  resetPoolDraggables();
  Fluxion.doAjax(
          'runControllerHelperService',
          'changeChamber',
          {'platform':'Solid', 'run_cId':jQuery('input[name=run_cId]').val(), 'numChambers':t.value, 'container':container, 'url':ajaxurl},
          {'updateElement':'containerdiv' + container});
}

function changePacBioChamber(t, container) {
  resetPoolDraggables();
  Fluxion.doAjax(
          'runControllerHelperService',
          'changeChamber',
          {'platform':'PacBio', 'run_cId':jQuery('input[name=run_cId]').val(), 'numChambers':t.value, 'container':container, 'url':ajaxurl},
          {'updateElement':'containerdiv' + container});
}

function changeIlluminaContainer(t) {
  resetPoolDraggables();
  Fluxion.doAjax(
          'runControllerHelperService',
          'changeContainer',
          {'platform':'Illumina', 'numContainers':t.value, 'url':ajaxurl},
          {'updateElement':'containerdiv'});
}

function changeContainer(numContainers, platform, seqrefId) {
  Fluxion.doAjax(
          'runControllerHelperService',
          'changeContainer',
          {'platform':platform, 'run_cId':jQuery('input[name=run_cId]').val(), 'numContainers':numContainers, 'sequencerReferenceId':seqrefId, 'url':ajaxurl},
          {'updateElement':'containerdiv'});
}

var resetPoolDraggables = function(j) {
  jQuery('.draggable').each(function(item) {
    jQuery(this).appendTo(jQuery('.poolList'))
  });
};

var updateDroppables = function(element) {
  jQuery(element).droppable({
    accept: '.draggable',
    activeClass: 'ui-state-hover',
    hoverClass: 'ui-state-active',
    tolerance: 'pointer',
    drop: function(event, ui) {
      if (jQuery(this).children().length == 0) {
        jQuery(ui.draggable).find('input').attr("name", jQuery(this).attr("bind"));
        jQuery(ui.draggable).appendTo(jQuery(this));
      }
    }
  });
};

function generateCasava17DemultiplexCSV(runId, containerId) {
  Fluxion.doAjax(
    'runControllerHelperService',
    'generateIlluminaDemultiplexCSV',
    {'runId':runId, 'containerId':containerId, 'casavaVersion':'1.7', 'url':ajaxurl},
    {'doOnSuccess':
      function(json) {
        alert(json.response);
      }
    }
  );
}

function generateCasava18DemultiplexCSV(runId, containerId) {
  Fluxion.doAjax(
    'runControllerHelperService',
    'generateIlluminaDemultiplexCSV',
    {'runId':runId, 'containerId':containerId, 'casavaVersion':'1.8', 'url':ajaxurl},
    {'doOnSuccess':
      function(json) {
        alert(json.response);
      }
    }
  );
}

function insertPoolNextAvailable(poolLi) {
  var pool = jQuery(poolLi);
  jQuery('.runPartitionDroppable:empty:first').each(function() {
    var newpool = pool.clone().appendTo(jQuery(this));
    newpool.removeAttr("ondblclick");
    newpool.find('input').attr("name", jQuery(this).attr("bind"));

    Fluxion.doAjax(
      'poolControllerHelperService',
      'checkPoolExperiment',
      {'poolId':newpool.find('input').val(), 'partition':jQuery(this).attr("partition"),'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          newpool.append(json.html);
          newpool.append("<span style='position: absolute; top: 0; right: 0;' onclick='confirmPoolRemove(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
        },
        'doOnError':
        function(json) {
          newpool.remove();
        }
      }
    );
  });
}

function selectStudy(partition, poolId, projectId) {
  disableButton('studySelectButton-' + partition + '_' + poolId);
  var studyId = jQuery("select[name='poolStudies" + partition + "_" + projectId + "'] :selected").val();

  Fluxion.doAjax(
    'poolControllerHelperService',
    'selectStudyForPool',
    {'poolId':poolId, 'studyId':studyId, 'runId':jQuery("input[name='runId']").val(), 'url':ajaxurl},
    {'doOnSuccess':
      function(json) {
        var div = jQuery("#studySelectDiv" + partition + "_" + projectId).parent();
        jQuery("#studySelectDiv" + partition + "_" + projectId).remove();
        div.append(json.html);
      },
      'doOnError':
      function(json) {
        reenableButton('studySelectButton-' + partition + '_' + poolId, "Select Study");
      }
    }
  );
}

function toggleReadyToRunCheck(checkbox, platform) {
  poolSearch(jQuery('#searchPools').val(), platform);
}

function poolSearch(input, platform) {
  jQuery('#poolList').html("<img src='/styles/images/ajax-loader.gif'/>");
  Fluxion.doAjax(
    'poolSearchService',
    'poolSearch',
    {'str':input, 'readyOnly':jQuery('#showOnlyReady').attr('checked'), 'platformType':platform, 'url':ajaxurl},
    {
      "doOnSuccess":
        function(json) {
            jQuery('#poolList').html(json.html);
        }
    });
}

function generateRunQCRow(runId) {
  Fluxion.doAjax(
    'runControllerHelperService',
    'getRunQCUsers',
    {
      'runId':runId,
      'url':ajaxurl
    },
    {'doOnSuccess':insertRunQCRow}
  );
}

var insertRunQCRow = function(json, includeId) {
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
    column8.innerHTML = "<a href='javascript:void(0);' onclick='addRunQC(\"runQcTable\");'/>Add</a>";

    addMaxDatePicker("runQcDate", 0);

    Fluxion.doAjax(
      'runControllerHelperService',
      'getRunQCTypes',
      {'url':ajaxurl},
      {'doOnSuccess':function(json) {
        jQuery('#runQcType').html(json.types);
      }
      }
    );

    Fluxion.doAjax(
      'runControllerHelperService',
      'getRunQCProcessSelection',
      {
        'runId':json.runId,
        'url':ajaxurl
      },
      {'doOnSuccess':function(json) {
        jQuery('#runQcProcessSelection').html(json.processSelection);
      }
      }
    );
  }
  else {
    alert("Cannot add another QC when one is already in progress.")
  }
};

function addRunQC(table) {
  var a = [];
  jQuery('.partitionOccupied', 'td', '.containerSummary').each(function() {
    a.push({id:this.id});
  });
  var runid = jQuery("input[name='runId']").val();
  var qctype = jQuery("select[name='runQcType'] :selected").val();
  var donotprocess = jQuery("input[name='runQcDoNotProcess']").is(":checked");

  Fluxion.doAjax(
    'runControllerHelperService',
    'addRunQC',
    {
      'runId':runid,
      'qcCreator':jQuery("select[name='runQcUser'] :selected").val(),
      'qcDate':jQuery("input[name='runQcDate']").val(),
      'qcType':qctype,
      'processSelection':a,
      'information':jQuery("input[name='runQcInformation']").val(),
      'doNotProcess':donotprocess,
      'url':ajaxurl},
    {'updateElement':'runQcTable',
      'doOnSuccess':function(json) {
        jQuery('#runQcTable').removeAttr("qcInProgress");
        if ("SeqOps QC" === qctype && !donotprocess) {
          jQuery('#qcmenu').append("<a href='/miso/analysis/new/run/" + runid + "' class='add'>Initiate Analysis</a>");
        }
      }
    }
  );
}

function toggleProcessPartition(partition) {
  if (jQuery(partition).hasClass("partitionOccupied")) {
    jQuery(partition).removeClass("partitionOccupied");
  }
  else {
    jQuery(partition).addClass("partitionOccupied");
  }
}

function showRunNoteDialog(runId) {
  jQuery('#addRunNoteDialog')
          .html("<form>" +
                "<fieldset class='dialog'>" +
                "<label for='internalOnly'>Internal Only?</label>" +
                "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
                "<br/>" +
                "<label for='notetext'>Text</label>" +
                "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' />" +
                "</fieldset></form>");

  jQuery(function() {
    jQuery('#addRunNoteDialog').dialog({
       autoOpen: false,
       width: 400,
       modal: true,
       resizable: false,
       buttons: {
         "Add Note": function() {
           addRunNote(runId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
           jQuery(this).dialog('close');
         },
         "Cancel": function() {
           jQuery(this).dialog('close');
         }
       }
     });
  });
  jQuery('#addRunNoteDialog').dialog('open');
}

var addRunNote = function(runId, internalOnly, text) {
  Fluxion.doAjax(
    'runControllerHelperService',
    'addRunNote',
    {
      'runId':runId,
      'internalOnly':internalOnly,
      'text':text,
      'url':ajaxurl
    },
    {
      'doOnSuccess':pageReload
    }
  );
};

function watchRun(runId) {
  Fluxion.doAjax(
    'runControllerHelperService',
    'watchRun',
    {
      'runId':runId,
      'url':ajaxurl
    },
    {
      'doOnSuccess':function (json) {
        pageReload();
      }
    });
}

function unwatchRun(runId) {
  Fluxion.doAjax(
    'runControllerHelperService',
    'unwatchRun',
    {
      'runId':runId,
      'url':ajaxurl
    },
    {
      'doOnSuccess':function (json) {
        pageReload();
      }
    });
}

function populatePartition(t, containerNum, partitionNum) {
  var a = jQuery(t);
  a.html("<input type='text' id='poolBarcode" + partitionNum + "' name='poolBarcode" + partitionNum + "' partition='" + partitionNum + "' onkeyup='timedFunc(getPool(this, " + containerNum + "), 300);'/><br/><span id='msg" + partitionNum + "'/>");
}

function getPool(t, containerNum) {
  var a = jQuery(t);
  var platform = jQuery("input[name='platformTypes']:checked").val();
  var pNum = a.attr("partition");
  Fluxion.doAjax(
    'runControllerHelperService',
    'getPoolByBarcode',
    {'platform':platform, 'run_cId':jQuery('input[name=run_cId]').val(), 'container':containerNum, 'partition':pNum, 'barcode':a.val(),'url':ajaxurl},
    {'doOnSuccess':function(json) {
      if (json.err) {
        jQuery("#msg" + pNum).html(json.err);
      }
      else {
        a.parent().html(json.html);
      }
    }});
}

function confirmPoolRemove(t) {
  if (confirm("Remove this pool?")) {
    jQuery(t).parent().remove();
  }
}

function lookupContainer(t, containerNum) {
  var barcode = jQuery('#sequencerPartitionContainers\\[' + containerNum + '\\]\\.identificationBarcode').val();
  if (!isNullCheck(barcode)) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'lookupContainer',
      {'barcode':barcode,'containerNum':containerNum,'url':ajaxurl},
      {'doOnSuccess': processLookup}
    );
  }
}

var processLookup = function(json) {
  if (json.err) {
    jQuery('#partitionErrorDiv').html(json.err);
  }
  else {
    if (json.verify) {
      var dialogStr = "Container Properties\n\n";
      for (var key in json.verify) {
        dialogStr += "Partition " + key + ": " + json.verify[key] + "\n";
      }

      if (confirm("Import this container?\n\n" + dialogStr)) {
        jQuery('#partitionErrorDiv').html("");
        jQuery('#partitionDiv').html(json.html);
      }
    }
  }
};

function deleteRun(runId, successfunc) {
  if (confirm("Are you sure you really want to delete RUN" + runId + "? This operation is permanent!")) {
    Fluxion.doAjax(
      'runControllerHelperService',
      'deleteRun',
      {'runId':runId, 'url':ajaxurl},
      {'doOnSuccess':function(json) {
        successfunc();
      }
      });
  }
}