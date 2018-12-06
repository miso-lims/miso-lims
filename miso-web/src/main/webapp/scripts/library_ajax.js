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

//Custom Parsley validator to validate Library alias server-side
window.Parsley.addValidator('libraryAlias', {
  validateString: function(value) {
    var deferred = new jQuery.Deferred();
    Fluxion.doAjax('libraryControllerHelperService', 'validateLibraryAlias', {
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

var Library = Library || {
  validateLibrary: function(skipAliasValidation) {
    Validate.cleanFields('#library-form');
    jQuery('#library-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-maxlength', '100');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    if (skipAliasValidation) {
      jQuery('#alias').attr('data-parsley-required', 'true');
    } else {
      jQuery('#alias').attr('data-parsley-library-alias', '');
      jQuery('#alias').attr('data-parsley-debounce', '500');
    }

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Date of Receipt validation: ensure date is of correct form
    jQuery('#receiveddatepicker').attr('class', 'form-control');
    jQuery('#receiveddatepicker').attr('data-date-format', 'YYYY-MM-DD');
    jQuery('#receiveddatepicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#receiveddatepicker').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

    // Date of Creation validation: ensure date is of correct form
    jQuery('#creationdatepicker').attr('class', 'form-control');
    jQuery('#creationdatepicker').attr('data-date-format', 'YYYY-MM-DD');
    jQuery('#creationdatepicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#creationdatepicker').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

    // Library size validation
    jQuery('#dnaSize').attr('class', 'form-control');
    jQuery('#dnaSize').attr('data-parsley-maxlength', '10');
    jQuery('#dnaSize').attr('data-parsley-type', 'number');

    // Volume validation
    jQuery('#volume').attr('class', 'form-control');
    jQuery('#volume').attr('data-parsley-maxlength', '10');
    jQuery('#volume').attr('data-parsley-type', 'number');

    // Indices validation
    jQuery('#index1').attr('class', 'form-control');
    if (jQuery('#indexFamily').val() > 0) {
      jQuery('#index1').attr('data-parsley-required', 'true');
      jQuery('#index1').attr('data-parsley-min', 1);
    } else {
      jQuery('#index1').attr('data-parsley-required', 'false');
      jQuery('#index1').removeAttr('data-parsley-min');
    }

    // Spike-in validation
    if (jQuery('#spikeIn').val()) {
      jQuery('#spikeInDilutionFactor').attr('class', 'form-control');
      jQuery('#spikeInDilutionFactor').attr('data-parsley-required', 'true');
      Validate.makeDecimalField('#spikeInVolume', 14, 10, true, false);
    } else {
      Validate.removeValidation('#spikeInDilutionFactor');
      Validate.removeValidation('#spikeInVolume');
    }

    if (Constants.isDetailedSample) {
      var generatingAlias = Constants.automaticLibraryAlias == true && jQuery('#alias').val().length === 0;
      var selectedPlatform = jQuery('#platformTypes option:selected').text();

      // Group ID validation
      jQuery('#groupId').attr('class', 'form-control');
      jQuery('#groupId').attr('data-parsley-pattern', Utils.validation.alphanumRegex);
      jQuery('#groupId').attr('data-parsley-maxlength', '100');

      // Group Description validation
      jQuery('#groupDescription').attr('class', 'form-control');
      jQuery('#groupDescription').attr('data-parsley-maxlength', '255');
      jQuery('#groupDescription').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

      jQuery('#dnaSize').attr('data-parsley-required', generatingAlias && selectedPlatform === 'Illumina');

      // Prep Kit validation
      jQuery('#libraryKit').attr('class', 'form-control');
      jQuery('#libraryKit').attr('data-parsley-required', 'true');

      jQuery('#libraryDesignCodes').attr('class', 'form-control');
      jQuery('#libraryDesignCodes').attr('data-parsley-required', 'true');
      jQuery('#libraryDesignCodes').attr('data-parsley-min', 1);

      // Concentration validation
      jQuery('#concentration').attr('class', 'form-control');
      jQuery('#concentration').attr('data-parsley-maxlength', '10');
      jQuery('#concentration').attr('data-parsley-type', 'number');
      jQuery('#concentration').attr('data-parsley-required', generatingAlias && selectedPlatform === 'PacBio');

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
        jQuery('#distributionDatePicker').attr('data-parsley-maxlength', '0');
        jQuery('#distributionRecipient').attr('data-parsley-maxlength', '0');
      }

    }

    jQuery('#library-form').parsley();
    jQuery('#library-form').parsley().validate();

    Validate.updateWarningOrSubmit('#library-form');
  },

};

Library.ui = {
  changeSpikeIn: function() {
    var spikeIn = jQuery('#spikeIn').val();
    Utils.ui.setDisabled('#spikeInDilutionFactor', !spikeIn);
    Utils.ui.setDisabled('#spikeInVolume', !spikeIn);
    if (!spikeIn) {
      jQuery('#spikeInDilutionFactor').val(null);
      jQuery('#spikeInVolume').val(null);
    }
  },

  changePlatformType: function(originalLibraryTypeId, callback) {
    var platformType = Library.ui.getSelectedPlatformType();

    var indexFamilySelect = jQuery('#indexFamily').empty()[0];
    Constants.indexFamilies.filter(
        function(family) {
          return (!family.platformType || family.platformType == platformType.name)
              && (!family.archived || family.id == Library.originalIndexFamilyId);
        }).sort(function(a, b) {
      if (a.id == b.id)
        return 0;
      if (a.id == 0)
        return -1;
      if (b.id == 0)
        return 1;
      return a.name.localeCompare(b.name);
    }).map(function(family) {
      var option = document.createElement("option");
      option.value = family.id;
      option.text = family.name;
      return option;
    }).forEach(function(o) {
      indexFamilySelect.appendChild(o);
    });

    var libraryTypesSelect = jQuery('#libraryTypes').empty()[0];
    Constants.libraryTypes.filter(function(type) {
      return type.platform == platformType.name && (!type.archived || type.id == originalLibraryTypeId);
    }).sort(function(a, b) {
      return a.alias.localeCompare(b.alias);
    }).map(function(type) {
      var option = document.createElement("option");
      option.value = type.id;
      option.text = type.alias;
      return option;
    }).forEach(function(o) {
      libraryTypesSelect.appendChild(o);
    });
    Library.ui.updateIndices();
    if (callback) {
      callback();
    }
  },

  getSelectedPlatformType: function() {
    var platformTypeKey = jQuery('#platformTypes').val();
    return Constants.platformTypes.filter(function(pt) {
      return pt.key == platformTypeKey;
    })[0];
  },

  updateIndices: function() {
    jQuery('#indicesDiv').empty();
    var family = Library.ui.getCurrentIndexFamily();
    var max = Library.ui.maxIndexPositionInFamily(family);
    for (var pos = 1; pos <= max; pos++) {
      Library.ui.createIndexSelect(pos);
    }
    // If this index family requires fewer indices than previously selected, we need to null them out in the form input or Spring will
    // create an array with a mix of new and old indices.
    var biggestMax = Library.ui.maxOfArray(Constants.indexFamilies.map(Library.ui.maxIndexPositionInFamily));
    var container = document.getElementById('indicesDiv');
    for (var j = max; j < biggestMax; j++) {
      var nullInput = document.createElement("input");
      nullInput.type = "hidden";
      nullInput.value = "";
      nullInput.name = "indices[" + j + "]";
      container.appendChild(nullInput);
    }
  },

  createIndexBox: function(id) {
    if (typeof id == 'undefined')
      return;
    var selectedIndex = Library.ui.getCurrentIndexFamily().indices.filter(function(index) {
      return index.id == id;
    })[0];
    Library.ui.createIndexSelect(selectedIndex.position, id);
  },

  maxOfArray: function(array) {
    return Math.max(0, Math.max.apply(Math, array));
  },

  maxIndexPositionInFamily: function(family) {
    return Library.ui.maxOfArray(family.indices.map(function(index) {
      return index.position;
    }));
  },

  getCurrentIndexFamily: function() {
    var familyId = jQuery('#indexFamily').val();
    var families = Constants.indexFamilies.filter(function(family) {
      return family.id == familyId;
    });
    if (families.length == 0) {
      return {
        id: 0,
        indices: []
      };
    } else {
      return families[0];
    }
  },

  createIndexSelect: function(position, selectedId) {
    var container = document.getElementById('indicesDiv');
    var widget = document.createElement("select");
    widget.id = "index" + position;
    widget.name = "indices[" + (position - 1) + "]";
    if (position > 1) {
      var nullOption = document.createElement("option");
      nullOption.value = "";
      nullOption.text = "(None)";
      widget.appendChild(nullOption);
    }
    var family = Library.ui.getCurrentIndexFamily();
    var indices = family.indices.filter(function(index) {
      return index.position == position;
    });
    for (var i = 0; i < indices.length; i++) {
      var option = document.createElement("option");
      option.value = indices[i].id;
      option.text = indices[i].name;
      if (!family.fakeSequence)
        option.text += " (" + indices[i].sequence + ")";
      widget.appendChild(option);
    }
    widget.value = selectedId ? selectedId : (position > 1 ? "" : 0);
    if (family.uniqueDualIndex) {
      widget.onchange = function() {
        var currentIndexValue = document.getElementById("index" + position).value;
        var currentIndex = family.indices.find(function(index) {
          return index.id == currentIndexValue;
        });
        if (currentIndex) {
          var nextIndexSelector = document.getElementById("index" + (position + 1));
          var nextIndex = family.indices.find(function(index) {
            return index.position == (position + 1) && index.name == currentIndex.name;
          });
          if (nextIndex) {
            nextIndexSelector.value = nextIndex.id;
          }
        }
      };
    }
    container.appendChild(widget);
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

  showLibraryNoteDialog: function(libraryId) {
    var self = this;
    jQuery('#addNoteDialog')
        .html(
            "<form>"
                + "<fieldset class='dialog'>"
                + "<label for='internalOnly'>Internal Only?</label>"
                + "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />"
                + "<br/>" + "<label for='notetext'>Text</label>"
                + "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' autofocus />"
                + "</fieldset></form>");

    jQuery('#addNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function() {
          if (jQuery('#notetext').val().length > 0) {
            Utils.notes.addNote('library', libraryId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
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

  deleteLibraryNote: function(libraryId, noteId) {
    var deleteIt = function() {
      Utils.notes.deleteNote('library', libraryId, noteId);
    }
    Utils.showConfirmDialog('Delete Note', 'Delete', ["Are you sure you want to delete this note?"], deleteIt);
  },

  changeDesign: function(callback) {
    var designSelect = document.getElementById('libraryDesignTypes');
    var selection = document.getElementById('librarySelectionTypes');
    var strategy = document.getElementById('libraryStrategyTypes');
    var code = document.getElementById('libraryDesignCodes');
    if (designSelect == null || designSelect.value == -1) {
      selection.disabled = false;
      strategy.disabled = false;
      if (code) {
        code.disabled = false;
      }
      if (typeof callback == 'function')
        callback();
    } else {
      var matchedDesigns = Constants.libraryDesigns.filter(function(rule) {
        return rule.id == designSelect.value;
      });
      if (matchedDesigns.length == 1) {
        selection.value = matchedDesigns[0].selectionId;
        selection.disabled = true;
        strategy.value = matchedDesigns[0].strategyId;
        strategy.disabled = true;
        if (code) {
          code.value = matchedDesigns[0].designCodeId;
          code.disabled = true;
        }
      }
    }
  }
};
