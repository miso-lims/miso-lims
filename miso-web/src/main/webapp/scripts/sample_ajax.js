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

// Custom Parsley validator to validate Sample alias server-side
window.Parsley.addValidator('sampleAlias', {
  validateString: function(value) {
    var deferred = new jQuery.Deferred();
    Fluxion.doAjax('sampleControllerHelperService', 'validateSampleAlias', {
      'alias': value,
      'url': ajaxurl
    }, {
      'doOnSuccess': function(json) {
        deferred.resolve();
      },
      'doOnError': function(json) {
        deferred.reject(json.error);
      }
    });
    return deferred.promise();
  },
  messages: {
    en: 'Alias must conform to the naming scheme.'
  }
});

var Sample = Sample
    || {

      removeSampleFromOverview: function(sampleId, overviewId, successfunc) {
        Utils.showConfirmDialog("Confirm Remove", "OK", ["Are you sure you really want to remove SAM" + sampleId + " from overview?"],
            function() {
              Fluxion.doAjax('sampleControllerHelperService', 'removeSampleFromOverview', {
                'sampleId': sampleId,
                'overviewId': overviewId,
                'url': ajaxurl
              }, {
                'doOnSuccess': function(json) {
                  successfunc();
                }
              });
            });
      },

      validateSample: function(isDetailedSample, skipAliasValidation, isNewSample) {
        Validate.cleanFields('#sample-form');
        jQuery('#sample-form').parsley().destroy();

        // Alias input field validation
        // 'data-parsley-required' attribute is set in JSP based on whether alias generation is enabled
        jQuery('#alias').attr('class', 'form-control');
        jQuery('#alias').attr('data-parsley-maxlength', '100');
        jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
        if (skipAliasValidation) {
          jQuery('#alias').attr('data-parsley-required', 'true');
        } else {
          jQuery('#alias').attr('data-parsley-sample-alias', '');
          jQuery('#alias').attr('data-parsley-debounce', '500');
        }

        // Description input field validation
        jQuery('#description').attr('class', 'form-control');
        jQuery('#description').attr('data-parsley-maxlength', '255');
        jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

        // Project validation
        jQuery('#project').attr('class', 'form-control');
        jQuery('#project').attr('data-parsley-required', 'true');
        jQuery('#project').attr('data-parsley-error-message', 'You must select a project.');

        // Date of Receipt validation: ensure date is of correct form
        jQuery('#receiveddatepicker').attr('class', 'form-control');
        jQuery('#receiveddatepicker').attr('data-date-format', 'YYYY-MM-DD');
        jQuery('#receiveddatepicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
        jQuery('#receiveddatepicker').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

        // Sample Type validation
        jQuery('#sampleTypes').attr('class', 'form-control');
        jQuery('#sampleTypes').attr('required', 'true');
        jQuery('#sampleTypes').attr('data-parsley-error-message', 'You must select a Sample Type');
        jQuery('#sampleTypes').attr('data-parsley-errors-container', '#sampleTypesError');

        // Scientific Name validation
        jQuery('#scientificName').attr('class', 'form-control');
        jQuery('#scientificName').attr('data-parsley-required', 'true');
        jQuery('#scientificName').attr('data-parsley-maxlength', '100');
        jQuery('#scientificName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

        // Volume validation
        jQuery('#volume').attr('class', 'form-control');
        jQuery('#volume').attr('data-parsley-maxlength', '10');
        jQuery('#volume').attr('data-parsley-type', 'number');

        // Concentration validation
        jQuery('#concentration').attr('class', 'form-control');
        jQuery('#concentration').attr('data-parsley-maxlength', '10');
        jQuery('#concentration').attr('data-parsley-type', 'number');

        // Distribution validation
        if (jQuery('#distributed').prop('checked')) {
          // need date and destination distribution info as well
          jQuery('#distributionDatePicker').attr('data-parsley-required', 'true');
          jQuery('#distributionDatePicker').attr('data-date-format', 'YYYY-MM-DD');
          jQuery('#distributionDatePicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
          jQuery('#distributionDatePicker').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');
          jQuery('#distributionRecipient').attr('data-parsley-required', 'true');
          jQuery('#distributionRecipient').attr('data-parsley-maxlength', '255');
          jQuery('#distributionRecipient').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
        } else {
          // should have no date or destination distribution info for undistributed samples
          jQuery('#distributionDatePicker').attr('data-parsley-max', '0');
          jQuery('#distributionRecipient').attr('data-parsley-max', '0');
        }

        if (isDetailedSample) {

          if (isNewSample) {
            // External Name validation
            jQuery('#externalName').attr('class', 'form-control');
            jQuery('#externalName').attr('data-parsley-required', 'true');
            jQuery('#externalName').attr('data-parsley-maxlength', '255');
            jQuery('#externalName').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
          }

          // SampleClass validation
          jQuery('#sampleClass').attr('class', 'form-control');
          jQuery('#sampleClass').attr('data-parsley-required', 'true');

          // Group ID validation
          jQuery('#groupId').attr('class', 'form-control');
          jQuery('#groupId').attr('data-parsley-pattern', Utils.validation.alphanumRegex);
          jQuery('#groupId').attr('data-parsley-maxlength', '100');

          // Group Description validation
          jQuery('#groupDescription').attr('class', 'form-control');
          jQuery('#groupDescription').attr('data-parsley-maxlength', '255');
          jQuery('#groupDescription').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

          // Tissue Class validation
          jQuery('#tissueClass').attr('class', 'form-control');
          jQuery('#tissueClass').attr('data-parsley-required', 'true');

          // TissueOrigin validation
          jQuery('#tissueOrigin').attr('class', 'form-control');
          jQuery('#tissueOrigin').attr('data-parsley-required', 'true');

          // TissueType validation
          jQuery('#tissueType').attr('class', 'form-control');
          jQuery('#tissueType').attr('data-parsley-required', 'true');

          // Secondary Identifier validation
          jQuery('#secondaryIdentifier').attr('class', 'form-control');
          jQuery('#secondaryIdentifier').attr('data-parsley-maxlength', '255');
          jQuery('#secondaryIdentifier').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

          // Region validation
          jQuery('#region').attr('class', 'form-control');
          jQuery('#region').attr('data-parsley-maxlength', '255');
          jQuery('#region').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

          // PassageNumber validation
          jQuery('#passageNumber').attr('class', 'form-control');
          jQuery('#passageNumber').attr('data-parsley-type', 'integer');

          // TimesReceived validation
          jQuery('#timesReceived').attr('class', 'form-control');
          jQuery('#timesReceived').attr('data-parsley-required', 'true');
          jQuery('#timesReceived').attr('data-parsley-type', 'integer');

          // TubeNumber validation
          jQuery('#tubeNumber').attr('class', 'form-control');
          jQuery('#tubeNumber').attr('data-parsley-required', 'true');
          jQuery('#tubeNumber').attr('data-parsley-type', 'integer');

          if (jQuery('#detailedQcStatusNote').is(':visible')) {
            jQuery('#detailedQcStatusNote').attr('class', 'form-control');
            jQuery('#detailedQcStatusNote').attr('data-parsley-required', 'true');
            jQuery('#detailedQcStatusNote').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
          }

          var selectedId = jQuery('#sampleClass').is('select') ? jQuery('#sampleClass option:selected').val() : jQuery('#sampleClass')
              .val();
          var sampleCategory = Sample.options.getSampleCategoryByClassId(selectedId);
          // assign sample class alias based on whether text or dropdown menu are present
          var sampleClassAlias = '';
          if (jQuery('#sampleClass').is('select')) {
            sampleClassAlias = jQuery('#sampleClass option:selected').text();
          } else {
            sampleClassAlias = jQuery('#sampleClassAlias').text();
          }
          switch (sampleCategory) {
          case 'Tissue Processing':
            switch (sampleClassAlias) {
            case 'Slide':
              // Cuts validation
              jQuery('#cuts').attr('class', 'form-control');
              jQuery('#cuts').attr('data-parsley-type', 'digits');
              jQuery('#cuts').attr('data-parsley-required', 'true');

              // Discards validation
              jQuery('#discards').attr('class', 'form-control');
              jQuery('#discards').attr('data-parsley-type', 'digits');
              jQuery('#discards').attr('data-parsley-required', 'true');

              // Thickness validation
              jQuery('#thickness').attr('class', 'form-control');
              jQuery('#thickness').attr('data-parsley-type', 'number');
              break;
            case 'LCM Tube':
              jQuery('#slidesConsumed').attr('class', 'form-control');
              jQuery('#slidesConsumed').attr('data-parsley-type', 'digits');
              jQuery('#slidesConsumed').attr('data-parsley-required', 'true');
              break;
            case 'Single Cell':
              Validate.makeDecimalField('#initialCellConcentration', 14, 10, false, false);

              jQuery('#digestion').attr('class', 'form-control');
              jQuery('#digestion').attr('data-parsley-maxlength', '255');
              jQuery('#digestion').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
              jQuery('#digestion').attr('data-parsley-required', 'true');
              break;
            }
            break;
          case 'Stock':
            Validate.makeDecimalField('#targetCellRecovery', 14, 10, false, false);
            Validate.makeDecimalField('#cellViability', 14, 10, false, false);
            Validate.makeDecimalField('#loadingCellConcentration', 14, 10, false, false);
            // fall-though to aliquot case (include same restrictions)
          case 'Aliquot':
            // TissueClass validation
            jQuery('#tissueClass').attr('class', 'form-control');
            jQuery('#tissueClass').attr('data-parsley-required', 'true');

            Validate.makeDecimalField('#inputIntoLibrary', 14, 10, false, false);
            break;
          }
        }

        jQuery('#sample-form').parsley();
        jQuery('#sample-form').parsley().validate();

        Validate.updateWarningOrSubmit('#sample-form');
      }
    };

Sample.library = {
  processBulkLibraryQcTable: function(tableName, json) {
    Utils.ui.reenableButton('bulkLibraryQcButton', "Save QCs");

    var a = json.saved;
    for (var i = 0; i < a.length; i++) {
      jQuery(tableName).find("tr:gt(0)").each(function() {
        if (jQuery(this).attr("libraryId") === a[i].libraryId) {
          jQuery(this).removeClass('row_selected');
          jQuery(this).addClass('row_saved');
          jQuery(this).find("td").each(function() {
            jQuery(this).css('background', '#CCFF99');
            if (jQuery(this).hasClass('rowSelect')) {
              jQuery(this).removeClass('rowSelect');
              jQuery(this).removeAttr('name');
            }
          });
        }
      });
    }

    if (json.errors) {
      var errors = json.errors;
      var errorStr = "";
      for (var j = 0; j < errors.length; j++) {
        errorStr += errors[j].error + "\n";
        jQuery(tableName).find("tr:gt(0)").each(function() {
          if (jQuery(this).attr("libraryId") === errors[j].libraryId) {
            jQuery(this).find("td").each(function() {
              jQuery(this).css('background', '#EE9966');
            });
          }
        });
      }
      alert("There were errors in your bulk input. The green rows have been saved, please fix the red rows:\n\n" + errorStr);
    } else {
      location.reload(true);
    }
  },

  validateLibraryQcs: function(json) {
    var ok = true;
    for (var i = 0; i < json.length; i++) {
      if (!json[i].results.match(/[0-9]+(\.[0-9]+)?/) || Utils.validation.isNullCheck(json[i].qcDate))
        ok = false;
    }
    return ok;
  },
};

Sample.options = {

  getSampleGroupsBySubProjectId: function(subProjectId) {
    return Constants.sampleGroups.filter(function(sampleGroup) {
      return sampleGroup.subprojectId == subProjectId;
    });
  },

  getSampleGroupsByProjectId: function(projectId) {
    return Constants.sampleGroups.filter(function(sampleGroup) {
      return sampleGroup.projectId == projectId && !sampleGroup.subprojectId;
    });
  },

  getSubProjectsByProjectId: function(projectId) {
    return Constants.subprojects.filter(function(subProject) {
      return subProject.parentProjectId == projectId;
    });
  },

  getSampleCategoryByClassId: function(sampleClassId) {
    var results = Constants.sampleClasses.filter(function(sampleClass) {
      return sampleClass.id == sampleClassId;
    });
    return results.length > 0 ? results[0].sampleCategory : null;
  }

};

Sample.ui = {
  filterSampleGroupOptions: function() {
    var validSampleGroups = [];
    var subProjectId = Sample.ui.getSelectedSubprojectId();
    if (subProjectId) {
      validSampleGroups = Sample.options.getSampleGroupsBySubProjectId(subProjectId);
    } else {
      var projectId = Sample.ui.getSelectedProjectId();
      if (projectId) {
        validSampleGroups = Sample.options.getSampleGroupsByProjectId(projectId);
      }
    }

    jQuery('#sampleGroup').empty().append('<option value = "">None</option>');
    for (var i = 0, l = validSampleGroups.length; i < l; i++) {
      jQuery('#sampleGroup').append('<option value = "' + validSampleGroups[i].id + '">' + validSampleGroups[i].groupId + '</option>');
    }
  },

  projectChanged: function() {
    Sample.ui.filterSubProjectOptions();
    Sample.ui.filterSampleGroupOptions();
  },

  subProjectChanged: function() {
    Sample.ui.filterSampleGroupOptions();
  },

  filterSubProjectOptions: function(setId) {
    var selected = setId ? setId : (jQuery('#subProject').val() ? jQuery('#subProject').val() : "");
    var projectId = Sample.ui.getSelectedProjectId();
    var subProjects = Sample.options.getSubProjectsByProjectId(projectId);
    jQuery('#subProject').empty().append('<option value = "">None</option>');
    for (var i = 0, l = subProjects.length; i < l; i++) {
      jQuery('#subProject').append('<option value = "' + subProjects[i].id + '">' + subProjects[i].alias + '</option>');
    }
    jQuery('#subProject').val(selected);
  },

  getSelectedProjectId: function() {
    return jQuery('#project option:selected').val() || jQuery('#project').val();
  },

  getSelectedSubprojectId: function() {
    return jQuery('#subProject option:selected').val() || jQuery('#subProject').val();
  },

  /**
   * Update display when user selects different sample classes during new sample receipt
   */
  sampleClassChanged: function() {
    var sampleClassId = parseInt(jQuery('#sampleClass option:selected').val());
    var sampleClass = Constants.sampleClasses.filter(function(sampleClass) {
      return sampleClass.id == sampleClassId;
    })[0];
    if (!sampleClass) {
      Sample.ui.setUpForTissue();
      return;
    }
    jQuery('#sampleCategory').val(sampleClass.sampleCategory);
    switch (sampleClass.sampleCategory) {
    case 'Aliquot':
      Sample.ui.setUpForAliquot();
      break;
    case 'Tissue Processing':
      Sample.ui.setUpForProcessing();
      if (sampleClass.alias == 'LCM Tube') {
        jQuery('#lcmTubeTable').show();
      } else {
        jQuery('#lcmTubeTable').hide();
      }
      if (sampleClass.alias == 'Slide') {
        jQuery('#slideTable').show();
      } else {
        jQuery('#slideTable').hide();
      }
      break;
    case 'Stock':
      Sample.ui.setUpForStock();
      break;
    default:
      Sample.ui.setUpForTissue();
      break;
    }
  },

  /**
   * Enable or disable distribution date & recipient, depending on if distributed is checked
   */
  distributionChanged: function() {
    var isDistributed = document.getElementById('distributed').checked;
    if (isDistributed) {
      document.getElementById('distributionDatePicker').removeAttribute('disabled');
      document.getElementById('distributionRecipient').removeAttribute('disabled');
    } else {
      document.getElementById('distributionDatePicker').setAttribute('value', '');
      document.getElementById('distributionDatePicker').setAttribute('disabled', 'disabled');
      document.getElementById('distributionRecipient').setAttribute('value', '');
      document.getElementById('distributionRecipient').setAttribute('disabled', 'disabled');
    }
  },

  /**
   * Update display with note required (or not) for QC Status
   */
  detailedQcStatusChanged: function() {
    // delete everything from the note
    jQuery('#detailedQcStatusNote').val('');

    // find the selected detailedQcStatus
    var dqcsId = jQuery('#detailedQcStatus option:selected').val();
    var selectedDQCS = Utils.array.findFirstOrNull(Utils.array.idPredicate(dqcsId), Constants.detailedQcStatuses);
    if (selectedDQCS !== null && selectedDQCS.noteRequired) {
      jQuery('#qcStatusNote').show();
    } else {
      jQuery('#qcStatusNote').hide();
    }
  },

  setUpForTissue: function() {
    jQuery('#detailedSampleAliquot').find(':input').each(function() {
      jQuery(this).val('');
    });
    jQuery('#detailedSampleStock').find(':input').each(function() {
      jQuery(this).val('');
    });
    jQuery('#detailedSampleAliquot').hide();
    jQuery('#detailedSampleStock').hide();
    jQuery('#slideTable').hide();
    jQuery('#lcmTubeTable').hide();
    jQuery('#tissueClassRow').hide();
    jQuery('#stockClassRow').hide();
    jQuery('#tissueClass').val('');
    jQuery('#detailedSampleTissue').show();
  },

  setUpForProcessing: function() {
    jQuery('#tissueClassRow').show();
    jQuery('#stockClassRow').hide();
    jQuery('#detailedSampleStock').hide();
    jQuery('#detailedSampleAliquot').hide();
  },

  setUpForAliquot: function() {
    jQuery('#detailedSampleStock').find(':input').each(function() {
      jQuery(this).val('');
    });
    jQuery('#tissueClassRow').show();
    jQuery('#stockClassRow').show();
    jQuery('#detailedSampleStock').show();
    jQuery('#detailedSampleAliquot').show();
  },

  setUpForStock: function() {
    jQuery('#detailedSampleAliquot').find(':input').each(function() {
      jQuery(this).val('');
    });
    jQuery('#tissueClassRow').show();
    jQuery('#stockClassRow').hide();
    jQuery('#detailedSampleStock').show();
    jQuery('#detailedSampleAliquot').hide();
    jQuery('#slideTable').hide();
    jQuery('#lcmTubeTable').hide();
  },

  editSampleLocationBarcode: function(span) {
    var v = span.find('a').text();
    span.html("<input type='text' value='" + v + "' name='locationBarcode' id='locationBarcode'>");
  },

  showSampleLocationChangeDialog: function(sampleId) {
    var self = this;
    jQuery('#changeSampleLocationDialog').html(
        "<form>" + "<fieldset class='dialog'>" + "<label for='notetext'>New Location:</label>"
            + "<input type='text' name='locationBarcodeInput' id='locationBarcodeInput' class='text ui-widget-content ui-corner-all'/>"
            + "</fieldset></form>");

    jQuery('#changeSampleLocationDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function() {
          self.changeSampleLocation(sampleId, jQuery('#locationBarcodeInput').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  changeSampleLocation: function(sampleId, barcode) {
    Fluxion.doAjax('sampleControllerHelperService', 'changeSampleLocation', {
      'sampleId': sampleId,
      'locationBarcode': barcode,
      'url': ajaxurl
    }, {
      'doOnSuccess': Utils.page.pageReload
    });
  },

  showSampleNoteDialog: function(sampleId) {
    var self = this;
    jQuery('#addSampleNoteDialog')
        .html(
            "<form>"
                + "<fieldset class='dialog'>"
                + "<label for='internalOnly'>Internal Only?</label>"
                + "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />"
                + "<br/>" + "<label for='notetext'>Text</label>"
                + "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' autofocus />"
                + "</fieldset></form>");

    jQuery('#addSampleNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function() {
          if (jQuery('#notetext').val().length > 0) {
            Utils.notes.addNote('sample', sampleId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          } else {
            jQuery('#notetext').focus();
          }
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  deleteSampleNote: function(sampleId, noteId) {
    if (confirm("Are you sure you want to delete this note?")) {
      Utils.notes.deleteNote('sample', sampleId, noteId);
    }
  },

  showExternalNameChangeDialog: function() {
    jQuery('#externalNameDialog').html(
        "<form>" + "<fieldset class='dialog'>" + "<strong><label for='externalNameInput'>External Name(s):</label></strong>"
            + "<input type='text' id='externalNameInput' class='text ui-widget-content ui-corner-all' required/>"
            + "<div id='parentSelectDiv'></div>  <span id='selectButton'></span>" + "</fieldset></form>");
    var oldExternalName = jQuery('#externalName').val();

    jQuery('#externalNameDialog')
        .dialog(
            {
              width: 400,
              modal: true,
              resizable: true,
              buttons: {
                "Validate External Name(s)": function() {
                  jQuery('#externalNameVal').val(jQuery('#externalNameInput').val());
                  jQuery('#externalName').val(jQuery('#externalNameInput').val());
                  jQuery('#externalNameInput').after('<img id="ajaxLoader" src="/../styles/images/ajax-loader.gif" class="fg-button"/>');
                  jQuery
                      .ajax(
                          {
                            url: "/miso/rest/sample/identitiesLookup?exactMatch=true",
                            data: "{\"identitiesSearches\":" + JSON.stringify([jQuery('#externalNameInput').val()]) + ", \"project\": "
                                + jQuery('#project').val() + ", \"requestCounter\":1}",
                            contentType: 'application/json; charset=utf8',
                            dataType: 'json',
                            type: 'POST'
                          })
                      .complete(function(data) {
                        console.log(data);
                      })
                      .success(
                          function(data) {
                            var identitiesResults = data.matchingIdentities;
                            var parentSelect = Sample.ui.createParentSelect(identitiesResults);
                            jQuery('#ajaxLoader').remove();
                            jQuery('#parentSelectDiv').html(parentSelect);
                            var selectParent = function() {
                              jQuery('#parentAlias').html(jQuery('#parentSelect option:selected').text());
                              jQuery('#identityId').val(jQuery('#parentSelect option:selected').val());
                              var externalName;
                              if (jQuery('#parentSelect').val() === '') {
                                externalName = jQuery('#externalNameInput').val();
                              } else {
                                externalName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(jQuery(
                                    '#parentSelect').val()), identitiesResults), 'externalName');
                              }
                              jQuery('#externalNameVal').html(externalName);
                              jQuery('#externalName').html(externalName);
                            };
                            jQuery('#selectButton')
                                .html(
                                    '<button type="button" id="chooseParent" class="ui-state-default" style="padding:5px;margin:8px 0px;">Select</button>')
                            jQuery('#chooseParent').click(function() {
                              selectParent();
                              jQuery('#externalNameDialog').dialog('close');
                            });
                          }).fail(function(data) {
                        jQuery('#externalNameVal').val(oldExternalName);
                        jQuery('#externalName').val(oldExternalName);
                        jQuery('#externalNameDialog').dialog('close');
                      });
                },
                "Cancel": function() {
                  jQuery(this).dialog('close');
                }
              }
            });
  },

  createParentSelect: function(identities) {
    var selectedProjectId = jQuery('#project').val();
    var sortedIdentities;
    if (identities.length) {
      sortedIdentities = identities.sort(function(a, b) {
        var aSortId = a.projectId == selectedProjectId ? 0 : a.projectId;
        var bSortId = b.projectId == selectedProjectId ? 0 : b.projectId;
        return aSortId - bSortId;
      });
    } else {
      sortedIdentities = identities;
    }
    var parentSelect = ['<select id="parentSelect" style="padding:3px;">'];
    var projShortName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(selectedProjectId),
        HotUtils.projects), 'shortname');
    var hasIdentityInProject = (sortedIdentities.length > 0 && sortedIdentities[0].projectId == selectedProjectId);
    if (!hasIdentityInProject)
      parentSelect.push('<option value="">First Receipt' + (projShortName ? ' (' + projShortName + ')' : '') + '</option>');
    for (var i = 0; i < sortedIdentities.length; i++) {
      parentSelect.push('<option value="' + sortedIdentities[i].id + '">' + sortedIdentities[i].alias + " --"
          + sortedIdentityes[i].externalName + '</option>');
    }
    parentSelect.push('</select>');
    return parentSelect.join('');
  },

  receiveSample: function(input) {
    var barcode = jQuery(input).val();
    if (!Utils.validation.isNullCheck(barcode)) {

      Fluxion
          .doAjax(
              'sampleControllerHelperService',
              'getSampleByBarcode',
              {
                'barcode': barcode,
                'url': ajaxurl
              },
              {
                'doOnSuccess': function(json) {
                  var sample_desc = "<div id='"
                      + json.id
                      + "' class='dashboard'><table width=100%><tr><td>Sample Name: "
                      + json.name
                      + "<br> Sample ID: "
                      + json.id
                      + "<br>Desc: "
                      + json.desc
                      + "<br>Sample Type:"
                      + json.type
                      + "</td><td style='position: absolute;' align='right'><span class='float-right ui-icon ui-icon-circle-close' onclick='Sample.ui.removeSample("
                      + json.id + ");' style='position: absolute; top: 0; right: 0;'></span></td></tr></table> </div>";
                  if (jQuery("#" + json.id).length === 0) {
                    jQuery("#sample_pan").append(sample_desc);
                    jQuery('#msgspan').html("");
                  } else {
                    jQuery('#msgspan').html("<i>This sample has already been scanned</i>");
                  }

                  // unbind to stop change error happening every time

                  // clear and focus
                  jQuery(input).val("");
                  jQuery(input).focus();
                },
                'doOnError': function(json) {
                  jQuery('#msgspan').html("<i>" + json.error + "</i>");
                }
              });
    } else {
      jQuery('#msgspan').html("");
    }
  },

  removeSample: function(sample) {
    jQuery("#" + sample).remove();
  },

  setSampleReceiveDate: function(sampleList) {
    var samples = [];
    jQuery(sampleList).children('div').each(function() {
      var sdiv = jQuery(this);
      samples.push({
        'sampleId': sdiv.attr("id")
      });
    });

    if (samples.length > 0) {
      Fluxion.doAjax('sampleControllerHelperService', 'setSampleReceivedDateByBarcode', {
        'samples': samples,
        'url': ajaxurl
      }, {
        'doOnSuccess': function(json) {
          alert(json.result);
        }
      });
    } else {
      alert("No samples scanned");
    }
  },
};

/**
 * Catches and logs errors.
 */
window.addEventListener('error', function(e) {
  var error = e.error;
  console.log(error);
});
