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

    // Library size validation
    jQuery('#dnaSize').attr('class', 'form-control');
    jQuery('#dnaSize').attr('data-parsley-maxlength', '10');
    jQuery('#dnaSize').attr('data-parsley-type', 'number');

    // Volume validation
    jQuery('#volume').attr('class', 'form-control');
    jQuery('#volume').attr('data-parsley-maxlength', '10');
    jQuery('#volume').attr('data-parsley-type', 'number');

    if (Constants.isDetailedSample) {
      var generatingAlias = Constants.automaticLibraryAlias == true && jQuery('#alias').val().length === 0;
      var selectedPlatform = jQuery('#platformTypes option:selected').text();

      // Group ID validation
      jQuery('#groupId').attr('class', 'form-control');
      jQuery('#groupId').attr('data-parsley-type', 'alphanum');
      jQuery('#groupId').attr('data-parsley-maxlength', '10');

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
      jQuery('#initialConcentration').attr('class', 'form-control');
      jQuery('#initialConcentration').attr('data-parsley-maxlength', '10');
      jQuery('#initialConcentration').attr('data-parsley-type', 'number');
      jQuery('#initialConcentration').attr('data-parsley-required', generatingAlias && selectedPlatform === 'PacBio');
    }

    jQuery('#library-form').parsley();
    jQuery('#library-form').parsley().validate();

    Validate.updateWarningOrSubmit('#library-form');
  },

};

Library.ui = {
  updateConcentrationUnits: function() {
    var platformType = Library.ui.getSelectedPlatformType();
    // default to 'nM' before platform is chosen
    var units = platformType == null ? 'nM' : platformType.libraryConcentrationUnits;
    jQuery('#concentrationUnits').text(units);
  },

  changePlatformType: function(originalLibraryTypeId, callback) {
    var platformType = Library.ui.getSelectedPlatformType();

    var indexFamilySelect = jQuery('#indexFamily').empty()[0];
    Constants.indexFamilies.filter(function(family) {
      return !family.platformType || family.platformType == platformType.name;
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
    Library.ui.updateConcentrationUnits();
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
    Library.lastIndexPosition = 0;
    Library.ui.createIndexNextBox();
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

  createIndexNextBox: function() {
    var family = Library.ui.getCurrentIndexFamily();
    var max = Library.ui.maxIndexPositionInFamily(family);
    if (Library.lastIndexPosition < max) {
      Library.ui.createIndexSelect(max, 0);
    } else if (Library.lastIndexPosition == 0) {
      var container = jQuery('#indicesDiv');
      if (container.children().length == 0) {
        container.text("No indices available.");
      }
    }
    var container = document.getElementById('indicesDiv');
    // If this index family requires fewer indices than previously selected, we need to null them out in the form input or Spring will
    // create an array with a mix of new and old indices.
    var biggestMax = Library.ui.maxOfArray(Constants.indexFamilies.map(Library.ui.maxIndexPositionInFamily));
    for (var j = Library.lastIndexPosition; j < biggestMax; j++) {
      var nullInput = document.createElement("input");
      nullInput.type = "hidden";
      nullInput.value = "";
      nullInput.name = "indices[" + j + "]";
      container.appendChild(nullInput);
    }
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

  createIndexSelect: function(newPosition, selectedId) {
    var container = document.getElementById('indicesDiv');
    for (var position = Library.lastIndexPosition + 1; position <= newPosition; position++) {
      var widget = document.createElement("select");
      widget.id = "index" + position;
      widget.name = "indices[" + (position - 1) + "]";
      if (position > 1) {
        var nullOption = document.createElement("option");
        nullOption.value = "";
        nullOption.text = "(None)";
        widget.appendChild(nullOption);
      }
      var indices = Library.ui.getCurrentIndexFamily().indices.filter(function(index) {
        return index.position == position;
      });
      for (var i = 0; i < indices.length; i++) {
        var option = document.createElement("option");
        option.value = indices[i].id;
        option.text = indices[i].name + " (" + indices[i].sequence + ")";
        widget.appendChild(option);
      }
      if (position == newPosition) {
        widget.value = selectedId;
      }
      container.appendChild(widget);
    }
    Library.lastIndexPosition = newPosition;
  },

  fillDownIndexFamilySelects: function(tableselector, th) {
    DatatableUtils.collapseInputs(tableselector);
    var tableObj = jQuery(tableselector);
    var table = tableObj.dataTable();
    var header = jQuery(th);
    var headerName = header.attr("header");
    var firstSelectedRow = tableObj.find(".row_selected").first();
    if (firstSelectedRow.length > 0) {
      var td = firstSelectedRow.find("td[name=" + headerName + "]");
      var tdtext = td.html();

      var col = firstSelectedRow.children().index(td);

      var frId = 0;
      var aTrs = table.fnGetNodes();
      for (var i = 0; i < aTrs.length; i++) {
        if (jQuery(aTrs[i]).hasClass('row_selected')) {
          frId = i;
          break;
        }
      }

      tableObj.find("tr:gt(" + frId + ")").each(function() {
        table.fnUpdate(tdtext, table.fnGetPosition(this), col);
      });

      jQuery.get('../../library/indexPositionsJson', {
        indexFamily: tdtext
      }, {
        success: function(json) {
          tableObj.find("tr:gt(" + frId + ")").each(
              function() {
                var c = this.cells[col + 1];
                jQuery(c).html("");
                for (var i = 0; i < json.numApplicableIndices; i++) {
                  jQuery(c).append(
                      "<span class='indexSelectDiv' position='" + (i + 1) + "' id='indices" + (i + 1) + "'>- <i>Select...</i></span>");
                  if (json.numApplicableIndices > 1 && i === 0) {
                    jQuery(c).append("|");
                  }
                }

                // bind editable to selects
                jQuery("#cinput .indexSelectDiv").editable(function(value, settings) {
                  return value;
                }, {
                  loadurl: '../../library/indicesForPosition',
                  loaddata: function(value, settings) {
                    var ret = {};
                    ret["position"] = jQuery(this).attr("position");
                    if (!Utils.validation.isNullCheck(tdtext)) {
                      ret['indexFamily'] = tdtext;
                    } else {
                      ret['indexFamily'] = '';
                    }
                    return ret;
                  },
                  type: 'select',
                  onblur: 'submit',
                  placeholder: '',
                  style: 'inherit',
                  submitdata: function(tvalue, tsettings) {
                    return {
                      "row_id": this.parentNode.getAttribute('id'),
                      "column": table.fnGetPosition(this)[2]
                    };
                  }
                });
              });
        }
      });
    } else {
      alert("Please select a row to use as the Fill Down template by clicking in the Select column for that row.");
    }
  },

  fillDownIndexSelects: function(tableselector, th) {
    DatatableUtils.collapseInputs(tableselector);
    var tableObj = jQuery(tableselector);
    var table = tableObj.dataTable();
    var header = jQuery(th);
    var headerName = header.attr("header");
    var firstSelectedRow = tableObj.find(".row_selected").first();
    if (firstSelectedRow.length > 0) {
      var td = firstSelectedRow.find("td[name=" + headerName + "]");
      var tdtext = td.html();

      var col = firstSelectedRow.children().index(td);

      var frId = 0;
      var aTrs = table.fnGetNodes();
      for (var i = 0; i < aTrs.length; i++) {
        if (jQuery(aTrs[i]).hasClass('row_selected')) {
          frId = i;
          break;
        }
      }

      var firstSelText = jQuery(aTrs[frId].cells[col - 1]).text();

      tableObj.find("tr:gt(" + frId + ")").each(
          function() {
            var ifam = this.cells[col - 1];
            var ifamText = jQuery(ifam).text();
            var cell = jQuery(this.cells[col]);

            if (ifamText.trim()) {
              // no select means empty or already filled
              if (firstSelText.indexOf("Select") === 0) {
                // same family, just copy the cell
                if (firstSelText === ifamText) {
                  cell.html(tdtext);
                } else {
                  jQuery.get('../../library/indexPositionsJson', {
                    indexFamily: ifamText
                  }, {
                    success: function(json) {
                      cell.html("");
                      for (var i = 0; i < json.numApplicableIndices; i++) {
                        cell.append("<span class='indexSelectDiv' position='" + (i + 1) + "' id='indices" + (i + 1)
                            + "'>- <i>Select...</i></span>");
                        if (json.numApplicableIndices > 1 && i === 0) {
                          cell.append("|");
                        }
                      }
                    }
                  });
                }
              } else {
                // just copy select
                if (firstSelText === ifamText) {
                  cell.html(tdtext);
                }
              }
            }

            // bind editable to selects
            jQuery("#cinput .indexSelectDiv").editable(function(value, settings) {
              return value;
            }, {
              loadurl: '../../library/indicesForPosition',
              loaddata: function(value, settings) {
                var ret = {};
                ret["position"] = jQuery(this).attr("position");
                if (!Utils.validation.isNullCheck(ifamText)) {
                  ret['indexFamily'] = ifamText;
                } else {
                  ret['indexFamily'] = '';
                }

                return ret;
              },
              type: 'select',
              onblur: 'submit',
              placeholder: '',
              style: 'inherit',
              submitdata: function(tvalue, tsettings) {
                return {
                  "row_id": this.parentNode.getAttribute('id'),
                  "column": table.fnGetPosition(this)[2]
                };
              }
            });
          });
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
            self.addLibraryNote(libraryId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
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

  addLibraryNote: function(libraryId, internalOnly, text) {
    Fluxion.doAjax('libraryControllerHelperService', 'addLibraryNote', {
      'libraryId': libraryId,
      'internalOnly': internalOnly,
      'text': text,
      'url': ajaxurl
    }, {
      'doOnSuccess': Utils.page.pageReload
    });
  },

  deleteLibraryNote: function(libraryId, noteId) {
    var deleteIt = function() {
      Fluxion.doAjax('libraryControllerHelperService', 'deleteLibraryNote', {
        'libraryId': libraryId,
        'noteId': noteId,
        'url': ajaxurl
      }, {
        'doOnSuccess': Utils.page.pageReload
      });
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
