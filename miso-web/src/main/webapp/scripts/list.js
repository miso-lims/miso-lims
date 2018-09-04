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

ListState = {};
ListTarget = {};
ListUtils = (function() {
  var updateSelectedLabel = function(state) {
    var hidden = state.selected.reduce(function(acc, item) {
      return acc + (state.data.every(function(d) {
        return d.id != item.id;
      }) ? 1 : 0);
    }, 0);
    state.element.innerText = (state.selected.length ? (' ' + state.selected.length + ' selected') : '')
        + (hidden ? ' (' + hidden + ' on other pages)' : '');
  };

  var searchTerms = {
    "fulfilled": {
      term: "is:fulfilled",
      help: "Check if there are no outstanding orders (remaining = 0)."
    },
    "active": {
      term: "is:active",
      help: "Check if there are outstanding orders (remaining > 0)."
    },
    "runstatus": {
      term: "is:RUNSTATUS",
      help: 'Match based on a run\'s "health".  ' +
      'RUNSTATUS may be one of: ' +
      'fulfilled, active, unknown, started, running, stopped, incomplete, failed, or completed.  ' +
      'For order completions, this means that the order includes at least one run with this status.  ' +
      '"incomplete" matches when a run\'s health ' +
      '(or an order completion with a run of this health) is any of running, started, or stopped'
    },
    "created": {
      term: "created:DATE",
      help: "Checks when this item was entered into MISO.  For rules about dates, see below."
    },
    "received": {
      term: "received:DATE",
      help: "Checks whether this item has a received date that matches the provided date.  For rules about dates, see below."
    },
    "changed": {
      term: "changed:DATE",
      help: "Checks when any person last edited this item.  For rules about dates, see below."
    },
    "creator": {
      term: "creator:USER",
      help: "Check for items entered into MISO by a particular user.  For the rules about users, see below."
    },
    "changedby": {
      term: "changedby:USER",
      help: "Checks for the last person to edit this item in MISO.  For the rules about users, see below."
    },
    "platform": {
      term: "platform:PLATFORM",
      help: "Check if this item is meant for a particular platform type: ILLUMINA, LS454, SOLID, IONTORRENT, PACBIO, OXFORDNANOPORE."
    },
    "index_name": {
      term: "index:NAME",
      help: "Checks if this item has the index provided.  The name can be a partial match."
    },
    "index_seq": {
      term: "index:SEQ",
      help: "Checks if this item has the index provided.  The sequence must be an exact match."
    },
    "class": {
      term: "class:NAME",
      help: "Check if the item belong to the sample class provided.  This is taken as a partial match."
    },
    "institute": {
      term: "institute:NAME",
      help: "Check if the item came from the institute mentioned.  This is a partial match."
    },
    "external": {
      term: "external:NAME",
      help: "Checks if an item came from the external identifier or external name."
    },
    "box": {
      term: "box:NAME",
      help: "Checks if an item is located in a particular box.  The name can either be the partial name or partial alias of the box."
    },
    "kitname": {
      term: "kitname:NAME",
      help: "Checks if an item uses a library, clustering, or multiplexing kit of the specified name. This is a partial match."
    },
    "subproject": {
      term: "subproject:NAME",
      help: "Checks if an item is tagged with the given subproject"
    },
    "parameters": {
      term: "parameters:NAME",
      help: "Checks if an item has the specified sequencing parameters."
    }
  };

  var makeTooltipHelp = function(target) {
    return "Search syntax: <br/><br/>"
      + target.searchTermSelector(searchTerms).map(function(term) {
        return term["term"];
      }).join('<br/>');
  };

  var makeSearchTooltip = function(searchDivId, target) {
    var searchTooltipId = "searchHelpTooltip_" + target.name.replace(/\s/g, '');

    jQuery("#" + searchDivId).append(
      '<div id="' + searchTooltipId + '" class="tooltip"> ' +
      '  <span>' +
      '   <img id="searchHelpQuestionMark" src="/styles/images/question_mark.png"' +
      '  </span>' +
      '  <span class="tooltiptext">' +
      makeTooltipHelp(target) +
      '  </span>' +
      '</div>');

    return searchTooltipId;
  };

  var makePopupTableBody = function(target) {
    var result = "";
    var targetTerms = target.searchTermSelector(searchTerms);

    for (var i = 0; i < targetTerms.length; ++i) {
      result += "<tr><td>" + targetTerms[i]["term"] + "</td><td>" + targetTerms[i]["help"] + "</td></tr>";
    }

    return result;
  };

  var makePopupTable = function(target) {
    return '<table class="searchHelpTable">' +
      '  <caption><h2>Search Terms</h2></caption>' +
      '  <thead>' +
      '    <tr><th>Syntax</th><th>Meaning</th></tr>' +
      '  </thead>' +
      makePopupTableBody(target) +
      '</table>';
  };

  var dateGrammar = '<table class="searchHelpTable">' +
    '  <caption><h2>DATE Format</h2></caption>' +
    '  <thead>' +
    '    <tr><th>Format</th><th>Behaviour</th></tr>' +
    '  </thead>' +
    '  <tr><td>lasthour</td><td>Filter from 1 hour ago to the current time.</td></tr>' +
    '  <tr><td>today</td><td>Anything that happened on the current calendar day.</td></tr>' +
    '  <tr><td>yesterday</td><td>Filter for anything on the last calendar day.</td></tr>' +
    '  <tr><td>thisweek</td><td>Filter from Monday 00:00:00 of the current week to the present time.</td></tr>' +
    '  <tr><td>lastweek</td><td>Filter from Monday 00:00:00 of the previous week to Sunday 23:59:59 of the previous week.</td></tr>' +
    '  <tr><td><i>N</i>hours</td><td>Filter for anything from the current time to <i>N</i> hours ago.</td></tr>' +
    '  <tr><td><i>N</i>days</td><td>Filter for anything from the current time to <i>N</i>*24 hours ago.</td></tr>' +
    '  <tr><td>YYYY-MM-DD</td><td>Search from YYYY-MM-DD 00:00:00 to YYYY-MM-DD 23:59:59</td></tr>' +
    '</table>';

  var userGrammar = '<table class="searchHelpTable">' +
    '  <caption><h2>USER Format</h2></caption>' +
    '  <thead>' +
    '    <tr><th>Format</th><th>Behaviour</th></tr>' +
    '  </thead>' +
    '  <tr><td>me</td><td>Searches for the current user.</td></tr>' +
    '  <tr><td>Anything else</td><td>Assumed to be the user\'s login name (not their human name).' +
    '    This starts searching from the beginning, so “jrh” will match “jrhacker”, but “hacker” will not match “jrhacker”.</td></tr>' +
    '</table>';

  var makePopupHelp = function(target) {
    return '<h1>Search Syntax</h1>' +
      '<p>' +
      '  This search box supports case-sensitive search syntax.' +
      '  Multiple searches can be separated by spaces (not AND).' +
      '  If a filter does not apply, it is ignored.' +
      '  Any other search term is taken as a regular query and matched against the current fields for each item.' +
      '  To search for a term with spaces, surround the entire term in quotation marks.' +
      '</p>' +
      '<br/>' +
      makePopupTable(target) +
      "<br/>" +
      dateGrammar +
      "<br/>" +
      userGrammar;
  };

  var makePopupElement = function(parentId, popupId, popupCloseId, target) {
    jQuery("#" + parentId).append(
      '<div id="' + popupId + '" class="popup">' +
      '  <div class="popup-inner">' +
      makePopupHelp(target) +
      '    <a id="' + popupCloseId + '" class="popup-close" href="#">x</a>' +
      '  </div>' +
      '</div>');
  };

  var registerPopupOpen = function(triggerId, popupId) {
    jQuery("#" + triggerId).click(function() {
      jQuery("#" + popupId).fadeIn(350);
    });
  };

  var registerPopupClose = function(popupCloseId, popupId) {
    var closePopup = function(e) {
      jQuery("#" + popupId).fadeOut(350);
      e.preventDefault();
    };

    // Close popup when popupCloseId is clicked
    jQuery("#" + popupCloseId).click(function(e) {
      closePopup(e);
    });

    // Close popup when esc is pressed
    jQuery(document).keyup(function(e) {
      if (e.keyCode == 27) {
        closePopup(e);
      }
    });
  };

  var makeSearchPopup = function(parentId, triggerId, target) {
    var popupId = "searchHelpPopup_" + target.name.replace(/\s/g, '');
    var popupCloseId = "searchHelpPopupClose_" + target.name.replace(/\s/g, '');

    makePopupElement(parentId, popupId, popupCloseId, target);
    registerPopupOpen(triggerId, popupId);
    registerPopupClose(popupCloseId, popupId);
  };

  var initTable = function(elementId, target, projectId, config, optionModifier) {
    var staticActions = target.createStaticActions(config, projectId);
    var bulkActions = target.createBulkActions(config, projectId);
    var columns = target.createColumns(config, projectId).filter(function(x) {
      return x.include;
    });
    ListState[elementId] = {
      selected: [],
      data: [],
      lastId: -1,
      element: document.createElement('SPAN')
    };
    if (bulkActions.length > 0) {
      columns.unshift({
        "sTitle": "",
        "mData": "id",
        "include": true,
        "bSortable": false,
        "sClass": "noPrint",
        "mRender": function(data, type, full) {
          var checked = ListState[elementId].selected.some(function(obj) {
            return obj.id == data;
          }) ? " checked=\"checked\"" : "";

          return "<input type=\"checkbox\" id=\"" + elementId + "_toggle" + data
              + "\" onclick='ListUtils._checkEventHandler(this.checked, event, " + JSON.stringify(data) + ", \"" + elementId + "\")'" + checked + ">";
        }
      });
      if (staticActions.length > 0) {
        staticActions.push(null);
      }
      staticActions.push({
        "name": "☑",
        "title": "Select all",
        "handler": function() {
          var state = ListState[elementId];
          state.lastId = -1;
          state.selected = Utils.array.deduplicateById(state.selected.concat(state.data));
          state.data.forEach(function(item) {
            var element = document.getElementById(elementId + "_toggle" + item.id);
            if (element) {
              element.checked = true;
            }
          });
          updateSelectedLabel(state);
        }
      });
      staticActions.push({
        "name": "☐",
        "title": "Deselect all",
        "handler": function() {
          var state = ListState[elementId];
          state.lastId = -1;
          state.selected = [];
          state.data.forEach(function(item) {
            var element = document.getElementById(elementId + "_toggle" + item.id);
            if (element) {
              element.checked = false;
            }
          });
          updateSelectedLabel(state);
        }
      });
      if (!projectId && target.queryUrl) {
        staticActions.push({
          "name": "📋",
          "title": "Select by names",
          "handler": function() {

            var showSelect = function(defaultText) {

              Utils.showDialog("Select by Names", "Select", [{
                "label": "Names, Aliases, or Barcodes",
                "type": "textarea",
                "property": "names",
                "rows": 15,
                "cols": 40,
                "value": defaultText,
                "required": true
              }], function(result) {
                var names = result.names.split(/[ \t\r\n]+/).filter(function(name) {
                  return name.length > 0;
                });
                if (names.length == 0) {
                  return;
                }
                Utils.ajaxWithDialog('Searching', 'POST', target.queryUrl, names, function(items) {
                  var title = items.length + ' ' + target.name;
                  var selectedActions = bulkActions.filter(function(bulkAction) {
                    return !!bulkAction;
                  }).map(function(bulkAction) {
                    return {
                      "name": bulkAction.name,
                      "handler": function() {
                        bulkAction.action(items);
                      }
                    };
                  });
                  var showActionDialog = function() {
                    Utils.showWizardDialog(title, selectedActions);
                  };

                  selectedActions.unshift({
                    "name": "View Selected",
                    "handler": function() {
                      Utils.showOkDialog(title, items.map(function(item) {
                        return item.name + ' (' + item.alias + ')';
                      }), showActionDialog);
                    }
                  });

                  selectedActions.push({
                    "name": "Select in list",
                    "handler": function() {

                      var state = ListState[elementId];
                      state.lastId = -1;
                      state.selected = items;
                      var ids = items.map(function(item) {
                        return item.id;
                      });
                      state.data.forEach(function(item) {
                        var element = document.getElementById(elementId + "_toggle" + item.id);
                        if (element) {
                          element.checked = ids.indexOf(item.id) != -1;
                        }
                      });
                      updateSelectedLabel(state);
                    }
                  });
                  showActionDialog();
                }, function() {
                  showSelect(names.join('\n'));
                });
              }, null);
            };
            showSelect('');
          }
        });
      }

    }
    var errorMessage = document.createElement('DIV');
    var jqTable = jQuery('#' + elementId).html('');
    var options = Utils.setSortFromPriority({
      'aoColumns': columns,
      'aLengthMenu': [10, 25, 50, 100, 200, 400, 1000],
      'bJQueryUI': true,
      'bAutoWidth': false,
      'iDisplayLength': 25,
      'iDisplayStart': 0,
      'sDom': '<"H"lf>r<"datatable-scroll"t><"F"ip>',
      'sPaginationType': 'full_numbers',
      'bStateSave': true,
      'bProcessing': true,
      'fnDrawCallback': function(oSettings) {
        jqTable.removeClass('disabled');
        jQuery('#' + elementId + '_paginate').find('.fg-button').removeClass('fg-button');
      },
      'fnPreDrawCallback': function(oSettings) {
        ListState[elementId].data = [];
      },
      'fnRowCallback': function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        ListState[elementId].data.push(aData);
      }
    });
    optionModifier(options, jqTable, errorMessage, columns);
    jqTable.dataTable(options);
    if (target.hasOwnProperty("searchTermSelector")) {
      var searchDivId = elementId + '_filter';

      var tooltipId = makeSearchTooltip(searchDivId, target);
      makeSearchPopup(searchDivId, tooltipId, target);
    }
    var filterbox = jQuery('#' + elementId + '_filter :input');
    filterbox.unbind();
    filterbox.bind('keyup', function(e) {
      if (e.keyCode == 13) {
        jqTable.fnFilter(this.value);
      }
    });
    var tableNode = document.getElementById(elementId + '_wrapper');
    errorMessage.setAttribute('class', 'parsley-error');
    tableNode.parentNode.insertBefore(errorMessage, tableNode);
    if (bulkActions.length > 0 || staticActions.length > 0) {
      var toolbar = document.createElement('DIV');
      toolbar.setAttribute('class', 'fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix paging_full_numbers');
      tableNode.parentNode.insertBefore(toolbar, tableNode);
      if (staticActions.length > 0 && bulkActions.length > 0) {
        staticActions.push(null);
      }

      staticActions.concat(bulkActions.map(function(bulkAction) {
        return bulkAction ? {
          name: bulkAction.name,
          handler: function() {
            if (ListState[elementId].selected.length == 0) {
              Utils.showOkDialog(bulkAction.name, ['Nothing selected.']);
              return;
            }
            bulkAction.action(ListState[elementId].selected);
          }
        } : null;
      })).forEach(function(buttonDescription) {
        var button;
        if (buttonDescription) {
          button = document.createElement('A');
          button.appendChild(document.createTextNode(buttonDescription.name));
          button.href = '#';
          button.setAttribute('class', 'ui-button ui-state-default');
          button.setAttribute('title', buttonDescription.title || '');
          button.onclick = function() {
            buttonDescription.handler();
            return false;
          }
        } else {
          button = document.createElement('SPAN');
          button.setAttribute('class', 'ui-state-default');
        }
        toolbar.appendChild(button);
      });
      if (bulkActions.length > 0) {
        toolbar.appendChild(ListState[elementId].element);
      }
    }
  }
  return {
    createTable: function(elementId, target, projectId, config) {
      initTable(elementId, target, projectId, config, function(options, jqTable, errorMessage, columns) {
        options.bServerSide = true;
        options.sAjaxSource = target.createUrl(config, projectId);
        options.fnServerData = function(sSource, aoData, fnCallback) {
          jqTable.addClass('disabled');
          var filterbox = jQuery('#' + elementId + '_filter :input');
          filterbox.prop('disabled', true);
          jQuery.ajax({
            'dataType': 'json',
            'type': 'GET',
            'url': sSource,
            'data': aoData,
            'success': function(data, textStatus, xhr) {
              errorMessage.innerText = data.sError;
              errorMessage.style.visibility = data.sError ? "visible" : "hidden";
              columns.forEach(function(column, index) {
                if (!column.visibilityFilter) {
                  return;
                }
                jqTable.fnSetColumnVis(index, column.visibilityFilter(data.aaData.map(function(d) {
                  return d[column.mData];
                })), false);
              });
              updateSelectedLabel(ListState[elementId]);
              fnCallback(data, textStatus, xhr);
              filterbox.prop('disabled', false);
            },
            'error': function(xhr, statusText, errorThrown) {
              errorMessage.style.visibility = "visible";
              errorMessage.innerText = errorThrown;
              updateSelectedLabel(ListState[elementId]);
              fnCallback({
                iTotalRecords: 0,
                iTotalDisplayRecords: 0,
                sEcho: aoData.sEcho,
                aaData: []
              });
              filterbox.prop('disabled', false);
            }
          });
        };
      });
    },
    createStaticTable: function(elementId, target, config, data) {
      initTable(elementId, target, null, config, function(options, jqTable, errorMessage, columns) {
        options.aaData = data;
        errorMessage.style.visibility = "hidden";
      });
    },
    _checkEventHandler: function(isChecked, ev, data, elementId) {
      var state = ListState[elementId];
      if (!ev.shiftKey) {
        if (isChecked) {
          state.lastId = data; // Record last click for range selection
          if (!state.selected.some(function(obj) {
            return obj.id == data;
          })) {
            Array.prototype.push.apply(state.selected, state.data.filter(function(obj) {
              return obj.id == data;
            }));
          }
        } else {
          var index = state.selected.findIndex(function(obj) {
            return obj.id == data;
          });
          if (index > -1) {
            state.selected.splice(index, 1);
          }
        }
      } else {
        var selectedIndex = state.data.findIndex(function(obj) {
          return obj.id == data;
        });
        var shiftIndex = state.lastId == -1 ? 0 : state.data.findIndex(function(obj) {
          return obj.id == state.lastId;
        });
        if (selectedIndex == -1 || shiftIndex == -1) {
          return;
        }
        var newlySelected = state.data.slice(Math.min(selectedIndex, shiftIndex), Math.max(selectedIndex, shiftIndex) + 1);
        newlySelected.forEach(function(obj) {
          var element = document.getElementById(elementId + "_toggle" + obj.id);
          if (element) {
            element.checked = true;
          }
        });
        state.selected = Utils.array.deduplicateById(state.selected.concat(newlySelected));
      }
      updateSelectedLabel(state);
    },
    idHyperlinkColumn: function(headerName, urlFragment, id, getLabel, priority, include, addClass) {
      return {
        "sTitle": headerName,
        "mData": id,
        "include": include,
        "iSortPriority": priority,
        "bSortable": priority >= 0,
        "sClass": addClass,
        "mRender": function(data, type, full) {
          if (type === 'display') {
            return data ? "<a href=\"/miso/" + urlFragment + "/" + data + "\">" + getLabel(full) + "</a>" : "";
          } else if (type === 'filter') {
            return getLabel(full);
          }
          return data;
        }
      };
    },
    labelHyperlinkColumn: function(headerName, urlFragment, getId, label, priority, include, addClass) {
      return {
        "sTitle": headerName,
        "mData": label,
        "include": include,
        "iSortPriority": priority,
        "bSortDirection": true,
        "bSortable": priority >= 0,
        "sClass": addClass,
        "mRender": function(data, type, full) {
          if (type === 'display') {
            return data ? "<a href=\"/miso/" + urlFragment + "/" + getId(full) + "\">" + data + "</a>" : "";
          }
          return data;
        }
      };
    },
    render: {
      archived: function(data, type, full) {
        return data ? "🗄" : "";
      },
      booleanChecks: function(data, type, full) {
        if (typeof data == 'boolean') {
          return data ? "✔" : "✘";
        } else {
          return "?";
        }
      },
      platformType: function(data, type, full) {
        return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.namePredicate(data), Constants.platformTypes), 'key')
            || 'Unknown';
      },
      textFromId: function(list, property, unknown) {
        return function(data, type, full) {
          return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(data), list), property) || unknown
              || "Unknown";
        };
      },
    },
    createBoxField: {
      property: 'createBox',
      type: 'checkbox',
      label: 'Create New Box',
      value: false
    }
  };
})();
