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
    state.element.innerText = (state.selected.length
        ? (' ' + state.selected.length + ' selected') : '') + (hidden
        ? ' (' + hidden + ' on other pages)' : '');
  };
  var initTable = function(elementId, target, projectId, config, optionModifier) {
    var searchKey = target.name + '_search';
    var lastSearch = window.localStorage.getItem(searchKey);
    var staticActions = target.createStaticActions(config, projectId);
    var bulkActions = target.createBulkActions(config, projectId);
    var columns = target.createColumns(config, projectId).filter(function(x) {
      return x.include;
    });
    ListState[elementId] = {
      selected : [],
      data : [],
      lastId : -1,
      element : document.createElement('SPAN')
    };
    if (bulkActions.length > 0) {
      columns
          .unshift({
            "sTitle" : "",
            "mData" : "id",
            "include" : true,
            "bSortable" : false,
            "mRender" : function(data, type, full) {
              var checked = ListState[elementId].selected.some(function(obj) {
                return obj.id == data;
              }) ? " checked=\"checked\"" : "";
              
              return "<input type=\"checkbox\" id=\"" + elementId + "_toggle" + data + "\" onclick=\"ListUtils._checkEventHandler(this.checked, event, " + data + ", '" + elementId + "')\"" + checked + ">";
            }
          });
      if (staticActions.length > 0) {
        staticActions.push(null);
      }
      staticActions.push({
        "name" : "☑",
        "title" : "Select all",
        "handler" : function() {
          var state = ListState[elementId];
          state.lastId = -1;
          state.selected = Utils.array.deduplicateById(state.selected
              .concat(state.data));
          state.data.forEach(function(item) {
            var element = document
                .getElementById(elementId + "_toggle" + item.id);
            if (element) {
              element.checked = true;
            }
          });
          updateSelectedLabel(state);
        }
      });
      staticActions.push({
        "name" : "☐",
        "title" : "Deselect all",
        "handler" : function() {
          var state = ListState[elementId];
          state.lastId = -1;
          state.selected = [];
          state.data.forEach(function(item) {
            var element = document
                .getElementById(elementId + "_toggle" + item.id);
            if (element) {
              element.checked = false;
            }
          });
          updateSelectedLabel(state);
        }
      });
    }
    var errorMessage = document.createElement('DIV');
    var jqTable = jQuery('#' + elementId).html('');
    var options = Utils
        .setSortFromPriority({
          'aoColumns' : columns,
          'bJQueryUI' : true,
          'bAutoWidth' : false,
          'iDisplayLength' : 25,
          'iDisplayStart' : 0,
          'sDom' : '<"H"lf>r<"datatable-scroll"t><"F"ip>',
          'sPaginationType' : 'full_numbers',
          'bProcessing' : true,
          'oSearch' : {
            'sSearch' : lastSearch || ""
          },
          'fnDrawCallback' : function(oSettings) {
            jqTable.removeClass('disabled');
            jQuery('#' + elementId + '_paginate').find('.fg-button')
                .removeClass('fg-button');
            var filterbox = jQuery('#' + elementId + '_filter :input');
            filterbox.val(window.localStorage.getItem(searchKey));
            filterbox.on('change keyup paste', function() {
              window.localStorage.setItem(searchKey, filterbox.val());
            });
          }
        });
    optionModifier(options, jqTable, errorMessage, columns);
    jqTable.dataTable(options).fnSetFilteringDelay(1300);
    var tableNode = document.getElementById(elementId + '_wrapper');
    errorMessage.setAttribute('class', 'parsley-error');
    tableNode.parentNode.insertBefore(errorMessage, tableNode);
    if (bulkActions.length > 0 || staticActions.length > 0) {
      var toolbar = document.createElement('DIV');
      toolbar
          .setAttribute(
              'class',
              'fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix paging_full_numbers');
      tableNode.parentNode.insertBefore(toolbar, tableNode);
      if (staticActions.length > 0 && bulkActions.length > 0) {
        staticActions.push(null);
      }
      
      staticActions.concat(bulkActions.map(function(bulkAction) {
        return {
          name : bulkAction.name,
          handler : function() {
            if (ListState[elementId].selected.length == 0) {
              alert('Nothing selected.');
              return;
            }
            bulkAction.action(ListState[elementId].selected);
          }
        };
      })).forEach(function(buttonDescription) {
        var button;
        if (buttonDescription) {
          button = document.createElement('A');
          button.append(document.createTextNode(buttonDescription.name));
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
        toolbar.append(button);
      });
      if (bulkActions.length > 0) {
        toolbar.append(ListState[elementId].element);
      }
    }
  }
  return {
    createTable : function(elementId, target, projectId, config) {
      initTable(elementId, target, projectId, config, function(options,
          jqTable, errorMessage, columns) {
        options.bServerSide = true;
        options.sAjaxSource = target.createUrl(config, projectId);
        options.fnServerData = function(sSource, aoData, fnCallback) {
          jqTable.addClass('disabled');
          var filterbox = jQuery('#' + elementId + '_filter :input');
          filterbox.prop('disabled', true);
          jQuery.ajax({
            'dataType' : 'json',
            'type' : 'GET',
            'url' : sSource,
            'data' : aoData,
            'success' : function(data, textStatus, xhr) {
              errorMessage.innerText = data.sError;
              ListState[elementId].data = data.aaData;
              columns.forEach(function(column, index) {
                if (!column.visibilityFilter) {
                  return;
                }
                jqTable.fnSetColumnVis(index, column
                    .visibilityFilter(data.aaData.map(function(d) {
                      return d[column.mData];
                    })), false);
              });
              updateSelectedLabel(ListState[elementId]);
              fnCallback(data, textStatus, xhr);
              filterbox.prop('disabled', false);
            },
            'error' : function(xhr, statusText, errorThrown) {
              errorMessage.innerText = errorThrown;
              ListState[elementId].data = [];
              updateSelectedLabel(ListState[elementId]);
              fnCallback({
                iTotalRecords : 0,
                iTotalDisplayRecords : 0,
                sEcho : aoData.sEcho,
                aaData : []
              });
              filterbox.prop('disabled', false);
            }
          });
        };
      });
    },
    createStaticTable : function(elementId, target, config, data) {
      initTable(elementId, target, null, config, function(options, jqTable,
          errorMessage, columns) {
        options.aaData = data;
        ListState[elementId].data = data;
      });
    },
    _checkEventHandler : function(isChecked, ev, data, elementId) {
      var state = ListState[elementId];
      if (!ev.shiftKey) {
        if (isChecked) {
          state.lastId = data; // Record last click for range selection
          if (!state.selected.some(function(obj) {
            return obj.id == data;
          })) {
            Array.prototype.push.apply(state.selected, state.data
                .filter(function(obj) {
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
        var shiftIndex = state.lastId == -1 ? 0 : state.data
            .findIndex(function(obj) {
              return obj.id == state.lastId;
            });
        if (selectedIndex == -1 || shiftIndex == -1) {
          return;
        }
        var newlySelected = state.data.slice(Math
            .min(selectedIndex, shiftIndex), Math
            .max(selectedIndex, shiftIndex) + 1);
        newlySelected
            .forEach(function(obj) {
              var element = document
                  .getElementById(elementId + "_toggle" + obj.id);
              if (element) {
                element.checked = true;
              }
            });
        state.selected = Utils.array.deduplicateById(state.selected
            .concat(newlySelected));
      }
      updateSelectedLabel(state);
    },
    idHyperlinkColumn : function(headerName, urlFragment, id, getLabel,
        priority) {
      return {
        "sTitle" : headerName,
        "mData" : id,
        "include" : true,
        "iSortPriority" : priority,
        "bSortable" : priority >= 0,
        "mRender" : function(data, type, full) {
          return "<a href=\"/miso/" + urlFragment + "/" + data + "\">" + getLabel(full) + "</a>";
        }
      };
    },
    labelHyperlinkColumn : function(headerName, urlFragment, getId, label,
        priority) {
      return {
        "sTitle" : headerName,
        "mData" : label,
        "include" : true,
        "iSortPriority" : priority,
        "bSortable" : priority >= 0,
        "mRender" : function(data, type, full) {
          return "<a href=\"/miso/" + urlFragment + "/" + getId(full) + "\">" + data + "</a>";
        }
      };
    },
    render : {
      archived : function(data, type, full) {
        return data ? "🗄" : "";
      },
      booleanChecks : function(data, type, full) {
        if (typeof data == 'boolean') {
          return data ? "✔" : "✘";
        } else {
          return "?";
        }
      },
      platformType : function(data, type, full) {
        return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(
            Utils.array.namePredicate(data), Constants.platformTypes), 'key') || 'Unknown';
      },
      textFromId : function(list, property, unknown) {
        return function(data, type, full) {
          return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(
              Utils.array.idPredicate(data), list), property) || unknown || "Unknown";
        };
      },
    }
  };
})();
