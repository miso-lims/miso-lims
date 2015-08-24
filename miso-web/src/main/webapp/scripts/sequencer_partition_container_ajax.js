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

var Container = Container || {
  lookupContainer: function (t) {
    var self = this;
    var barcode = jQuery("input", jQuery(t).parent()).val();
    var seqRef = jQuery('#sequencerReference :checked').val();
    var container_cId = jQuery('input[name=container_cId]').val();
    if (!Utils.validation.isNullCheck(barcode)) {
      Fluxion.doAjax(
        'containerControllerHelperService',
        'lookupContainer',
        {'barcode': barcode, 'sequencerReferenceId':seqRef, 'container_cId':container_cId, 'url': ajaxurl},
        {'doOnSuccess': self.processLookup}
      );
    }
  },

  processLookup: function (json) {
    if (json.error) {
      jQuery('#partitionErrorDiv').html(json.error);
    }
    else {
      if (json.verify) {
        var dialogStr = "Container Properties\n\n";
        for (var key in json.verify) {
          dialogStr += "Partition " + key + ": " + json.verify[key] + "\n";
        }

        if (confirm("Found container '" + json.barcode + "'. Import this container?\n\n" + dialogStr)) {
          window.location.href = "/miso/container/"+json.containerId;
        }
      }
    }
  },

  deleteContainer: function (id) {
    if (confirm("Are you sure you want to delete this Container?")) {
      Fluxion.doAjax(
        'containerControllerHelperService',
        'checkContainer',
        {'containerId': id, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          if (json.response == 'yes') {
            if (confirm("This container is in a completed run, are you sure to delete it?")) {
              Container.confirmedDeleteContainer(id);
            }
          }
          else {
            Container.confirmedDeleteContainer(id);
          }
        }
        }
      );
    }
  },

  confirmedDeleteContainer: function (id) {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'deleteContainer',
      {'containerId': id, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        alert(json.response);
        window.location.href = "/miso/containers";
      }
      }
    );
  }
};

Container.ui = {
  editContainerIdBarcode: function (span) {
    var s = jQuery(span);
    s.html("<input type='text' id='identificationBarcode' name='identificationBarcode' value='" + s.html() + "'/>" +
           "<button onclick='Container.lookupContainer(this);' type='button' class='fg-button ui-state-default ui-corner-all'>Lookup</button>");
  },

  editContainerLocationBarcode: function (span, fc) {
    var s = jQuery(span);
    s.html("<input type='text' id='locationBarcode' name='locationBarcode' value='" + s.html() + "'/>");
  },

  populatePlatformTypes: function () {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'getPlatformTypes',
      {'url': ajaxurl},
      {
        'doOnSuccess': function (json) {
          jQuery('#platformTypesDiv').html(json.html);
        }
      });
  },

  changeContainerPlatformType: function (form) {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'changePlatformType',
      {'platformtype': form.value, 'container_cId': jQuery('input[name=container_cId]').val(), 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        jQuery('#sequencerReferenceSelect').html(json.sequencers);
        Container.pool.poolSearch("", form.value);
      }
      }
    );
  },

  populateContainerOptions: function (form) {
    if (form.value != 0) {
      Fluxion.doAjax(
        'containerControllerHelperService',
        'populateContainerOptions',
        {'sequencerReference': form.value, 'container_cId': jQuery('input[name=container_cId]').val(), 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          jQuery('#sequencerReferenceSelect').attr("platformId", json.platformId);
          if (json.partitions) {
            jQuery('#containerPartitions').html(json.partitions);
          }
          }
        }
      );
    }
  },

  changeContainer: function (numContainers, platform, seqrefId) {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'changeContainer',
      {'platform': platform, 'container_cId': jQuery('input[name=container_cId]').val(), 'numContainers': numContainers, 'sequencerReferenceId': seqrefId, 'url': ajaxurl},
      {'updateElement': 'containerdiv'});
  },

  changeContainerIlluminaLane: function (t, container) {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'changeIlluminaLane',
      {'platform': 'Illumina', 'container_cId': jQuery('input[name=container_cId]').val(), 'numLanes': jQuery(t).val(), 'container': container, 'url': ajaxurl},
      {'updateElement': 'containerdiv' + container});
  },

  changeContainerLS454Chamber: function (t, container) {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'changeChamber',
      {'platform': 'LS454', 'container_cId': jQuery('input[name=container_cId]').val(), 'numChambers': jQuery(t).val(), 'container': container, 'url': ajaxurl},
      {'updateElement': 'containerdiv' + container});
  },

  changeContainerSolidChamber: function (t, container) {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'changeChamber',
      {'platform': 'Solid', 'container_cId': jQuery('input[name=container_cId]').val(), 'numChambers': jQuery(t).val(), 'container': container, 'url': ajaxurl},
      {'updateElement': 'containerdiv' + container});
  },

  changeContainerPacBioChamber: function (t, container) {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'changeChamber',
      {'platform': 'PacBio', 'container_cId': jQuery('input[name=container_cId]').val(), 'numChambers': jQuery(t).val(), 'container': container, 'url': ajaxurl},
      {'updateElement': 'containerdiv' + container});
  },

  confirmPoolRemove: function (t) {
    if (confirm("Remove this pool?")) {
      jQuery(t).parent().remove();
    }
  },

  createListingContainersTable: function () {
    jQuery('#listingContainersTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'containerControllerHelperService',
      'listSequencePartitionContainersDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#listingContainersTable').html('');
        jQuery('#listingContainersTable').dataTable({
          "aaData": json.array,
          "aoColumns": [
            //{ "sTitle": "Name"},
            { "sTitle": "ID Barcode"},
            { "sTitle": "Platform"},
            { "sTitle": "Last Associated Run"},
            { "sTitle": "Last Sequencer Used"},
            { "sTitle": "Edit"}
          ],
          "bJQueryUI": true,
          "iDisplayLength": 25,
          "aaSorting": [
            [0, "desc"]
          ],
          "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>'
        });
        jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
        jQuery("#toolbar").append("<button style=\"margin-left:5px;\" onclick=\"window.location.href='/miso/container/new';\" class=\"fg-button ui-state-default ui-corner-all\">Create Partition Container</button>");
      }
      }
    );
  }
};

Container.partition = {
  populatePartition: function (t) {
    var a = jQuery(t);
    var partitionNum = a.attr("partition");
    if (partitionNum > 0) {
      var ul = jQuery("ul[partition='" + (partitionNum - 1) + "']");
      if (ul.length > 0) {
        if (!Utils.validation.isNullCheck(ul.html()) && ul.find("div").length > 0) {
          a.html("<input type='text' style='width: 90%' id='poolBarcode" + partitionNum + "' name='poolBarcode" + partitionNum + "' partition='" + partitionNum + "'/><button onclick='Container.partition.clearPartition("+partitionNum+")' type='button' class='fg-button ui-state-default ui-corner-all'>Cancel</button><br/><span id='msg" + partitionNum + "'/>");

          Utils.ui.escape(jQuery("#poolBarcode" + partitionNum), function () {
            a.html("");
          });

          Utils.timer.typewatchFunc(jQuery("#poolBarcode" + partitionNum), function () {
            Container.pool.getPool(jQuery("#poolBarcode" + partitionNum));
          }, 400, 4);
        }
        else {
          alert("Please enter a pool for partition " + (partitionNum));
        }
      }
    }
    else {
      a.html("<input type='text' style='width: 90%' id='poolBarcode" + partitionNum + "' name='poolBarcode" + partitionNum + "' partition='" + partitionNum + "'/><button onclick='Container.partition.clearPartition("+partitionNum+")' type='button' class='fg-button ui-state-default ui-corner-all'>Cancel</button><br/><span id='msg" + partitionNum + "'/>");

      Utils.ui.escape(jQuery("#poolBarcode" + partitionNum), function () {
        a.html("");
      });

      Utils.timer.typewatchFunc(jQuery("#poolBarcode" + partitionNum), function () {
        Container.pool.getPool(jQuery("#poolBarcode" + partitionNum));
      }, 400, 4);
    }
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
        'containerControllerHelperService',
        'checkPoolExperiment',
        {'poolId': newpool.find('input').val(), 'partition': jQuery(this).attr("partition"), 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          newpool.append(json.html);
          newpool.append("<span style='position: absolute; top: 0; right: 0;' onclick='Container.pool.confirmPoolRemove(this);' class='float-right ui-icon ui-icon-circle-close'></span>");
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
      'containerControllerHelperService',
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

  selectContainerStudy: function (partition, poolId, projectId) {
    Utils.ui.disableButton('studySelectButton-' + partition + '_' + poolId);
    var studyId = jQuery("select[name='poolStudies" + partition + "_" + projectId + "'] :selected").val();
    var platformId = jQuery("#sequencerReferenceSelect").attr("platformId");

    Fluxion.doAjax(
      'containerControllerHelperService',
      'selectStudyForPool',
      {'poolId': poolId, 'studyId': studyId, 'platformId': platformId, 'url': ajaxurl},
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
  }
};

Container.pool = {
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
          jQuery('#poolList .dashboard').each(function () {
            var inp = jQuery(this);
            inp.dblclick(function () {
              Container.partition.insertPoolNextAvailable(inp);
            });
          });
        }
      });
  },

  getPool: function (t) {
    var a = jQuery(t);
    var platform = jQuery("input[name='platformTypes']:checked").val();
    var pNum = a.attr("partition");
    Fluxion.doAjax(
      'containerControllerHelperService',
      'getPoolByBarcode',
      {'platform': platform, 'container_cId': jQuery('input[name=container_cId]').val(), 'partition': pNum, 'barcode': a.val(), 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        if (json.err) {
          jQuery("#msg" + pNum).html(json.err);
        }
        else {
          a.parent().html(json.html);
        }
      }
      }
    );
  },

  confirmPoolRemove: function (t, partitionNum) {
    if (confirm("Remove this pool?")) {
      if (partitionNum === undefined) {
        //previously unsaved container, just remove the div
        jQuery(t).parent().remove();
      }
      else {
        //previously saved container, actually remove the pool from the partition
        Fluxion.doAjax(
          'containerControllerHelperService',
          'removePoolFromPartition',
          {'container_cId': jQuery('input[name=container_cId]').val(), 'partitionNum': partitionNum, 'url': ajaxurl},
          {'doOnSuccess': jQuery(t).parent().remove()}
        );
      }
    }
  }
};