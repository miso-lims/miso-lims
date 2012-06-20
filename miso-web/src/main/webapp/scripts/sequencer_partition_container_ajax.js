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

function populatePlatformTypes() {
  Fluxion.doAjax(
    'containerControllerHelperService',
    'getPlatformTypes',
  {'url':ajaxurl},
  {
    'doOnSuccess': function(json) {
      jQuery('#platformTypesDiv').html(json.html);
    }
  });
}

function changeContainerPlatformType(form) {
  Fluxion.doAjax(
          'containerControllerHelperService',
          'changePlatformType',
          {'platformtype':form.value, 'url':ajaxurl},
          {'doOnSuccess':
                  function(json) {
                    jQuery('#sequencerReferenceSelect').html(json.sequencers);
                  }
          }
          );
}

function populateContainerOptions(form) {
  if (form.value != 0) {
    Fluxion.doAjax(
            'containerControllerHelperService',
            'populateContainerOptions',
            {'sequencerReference':form.value, 'url':ajaxurl},
            {'doOnSuccess':
                    function(json) {
                      jQuery('#containerPartitions').html(json.partitions);
                    }
            }
            );
  }
}

function changeContainer(numContainers, platform, seqrefId) {
  Fluxion.doAjax(
          'containerControllerHelperService',
          'changeContainer',
          {'platform':platform, 'numContainers':numContainers, 'sequencerReferenceId':seqrefId, 'url':ajaxurl},
          {'updateElement':'containerdiv'});
}

function changeContainerLS454Chamber(t, container) {
  Fluxion.doAjax(
          'containerControllerHelperService',
          'changeChamber',
          {'platform':'LS454', 'numChambers':jQuery(t).val(), 'container':container, 'url':ajaxurl},
          {'updateElement':'containerdiv'+container});
}

function changeContainerSolidChamber(t, container) {
  Fluxion.doAjax(
          'containerControllerHelperService',
          'changeChamber',
          {'platform':'Solid', 'numChambers':jQuery(t).val(), 'container':container, 'url':ajaxurl},
          {'updateElement':'containerdiv'+container});
}

function changeContainerPacBioChamber(t, container) {
  Fluxion.doAjax(
          'containerControllerHelperService',
          'changeChamber',
          {'platform':'PacBio', 'numChambers':jQuery(t).val(), 'container':container, 'url':ajaxurl},
          {'updateElement':'containerdiv'+container});
}

function populatePartition(t) {
  var a = jQuery(t);
  var partitionNum = a.attr("partition");
  if (partitionNum > 0) {
    var ul = jQuery("ul[partition='"+(partitionNum-1)+"']");
    if (ul.length > 0) {
      if (!isNullCheck(ul.html()) && ul.find("div").length > 0) {
        a.html("<input type='text' id='poolBarcode"+partitionNum+"' name='poolBarcode"+partitionNum+"' partition='"+partitionNum+"' onkeyup='timedFunc(getPool(this), 300);'/><br/><span id='msg"+partitionNum+"'/>");
      }
      else {
        alert("Please enter a pool for partition " + (partitionNum));
      }
    }
  }
  else {
    a.html("<input type='text' id='poolBarcode"+partitionNum+"' name='poolBarcode"+partitionNum+"' partition='"+partitionNum+"' onkeyup='timedFunc(getPool(this), 300);'/><br/><span id='msg"+partitionNum+"'/>");
  }
}

function getPool(t) {
  var a = jQuery(t);
  var platform = jQuery("input[name='platformTypes']:checked").val();
  var pNum = a.attr("partition");
  Fluxion.doAjax(
    'containerControllerHelperService',
    'getPoolByBarcode',
    {'platform':platform, 'partition':pNum, 'barcode':a.val(),'url':ajaxurl},
    {'doOnSuccess':function(json) {
      if (json.err) {
        jQuery("#msg"+pNum).html(json.err);
      }
      else {
        a.parent().html(json.html);
      }
    }});
}

function selectContainerStudy(partition, poolId) {
  disableButton('studySelectButton-'+partition+'_'+poolId);
  //jQuery('#studySelectButton-'+partition+'_'+poolId).attr('disabled', 'disabled');
  //jQuery('#studySelectButton-'+partition+'_'+poolId).val("Processing...");

  var studyId = jQuery("select[name='poolStudies"+partition+"'] :selected").val();

  Fluxion.doAjax(
    'containerControllerHelperService',
    'selectStudyForPool',
    {'poolId':poolId, 'studyId':studyId, 'sequencerReferenceId':jQuery('#sequencerReference').val(), 'url':ajaxurl},
    {'doOnSuccess':
      function(json) {
        var div = jQuery("#studySelectDiv"+partition).parent();
        jQuery("#studySelectDiv"+partition).remove();
        div.append(json.html);
      },
      'doOnError':
      function(json) {
        reenableButton('studySelectButton-'+partition+'_'+poolId, "Select Study");
        //jQuery('#studySelectButton-'+partition+'_'+poolId).removeAttr('disabled');
        //jQuery('#studySelectButton-'+partition+'_'+poolId).val("Select Study");
      }
    }
  );
}

function confirmPoolRemove(t) {
  if (confirm("Remove this pool?")) {
    jQuery(t).parent().remove();
  }
}

function lookupContainer(t) {
  var barcode = jQuery("input", jQuery(t).parent()).val();
  if (!isNullCheck(barcode)) {
    Fluxion.doAjax(
      'containerControllerHelperService',
      'lookupContainer',
      {'barcode':barcode,'url':ajaxurl},
      {'doOnSuccess':processLookup}
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
        dialogStr += "Partition "+ key + ": " + json.verify[key] + "\n";
      }

      if (confirm("Import this container?\n\n"+dialogStr)) {
        jQuery('#partitionErrorDiv').html("");
        jQuery('#partitionDiv').html(json.html);
      }
    }
  }
};