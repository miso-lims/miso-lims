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
ListUtils = {
  createTable : function(elementId, target, projectId, config) {
    var searchKey = target.name + '_search';
    var lastSearch = window.localStorage.getItem(searchKey);
    var staticActions = target.createStaticActions(config, projectId);
    var bulkActions = target.createBulkActions(config, projectId);
    var columns = target.createColumns(config, projectId).filter(function(x) {
      return x.include;
    });
    if (bulkActions.length > 0) {
      ListState[elementId] = [];
      columns.unshift(Utils.createToggleColumn('ListState.' + elementId));
    }
    var jqTable = jQuery('#' + elementId).html('');
    jqTable
        .dataTable(
            Utils
                .setSortFromPriority({
                  'aoColumns' : columns,
                  'bJQueryUI' : true,
                  'bAutoWidth' : false,
                  'iDisplayLength' : 25,
                  'iDisplayStart' : 0,
                  'sDom' : '<lf>rt<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
                  'sPaginationType' : 'full_numbers',
                  'bProcessing' : true,
                  'bServerSide' : true,
                  'sAjaxSource' : target.createUrl(config, projectId),
                  'oSearch' : {
                    'sSearch' : lastSearch || ""
                  },
                  'fnServerData' : function(sSource, aoData, fnCallback) {
                    jqTable.addClass('disabled');
                    jQuery.ajax({
                      'dataType' : 'json',
                      'type' : 'GET',
                      'url' : sSource,
                      'data' : aoData,
                      'success' : fnCallback
                    });
                  },
                  'fnDrawCallback' : function(oSettings) {
                    jqTable.removeClass('disabled');
                    jQuery('#' + elementId + '_paginate').find('.fg-button')
                        .removeClass('fg-button');
                    var filterbox = jQuery('#' + elementId + '_filter :input');
                    filterbox.val(window.localStorage.getItem(searchKey));
                    filterbox.on('change', function() {
                      window.localStorage.setItem(searchKey, filterbox.val());
                    });
                  }
                })).fnSetFilteringDelay(600);
    if (bulkActions.length > 0 || staticActions.length > 0) {
      var tableNode = document.getElementById(elementId + '_wrapper');
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
            var list = ListState[elementId];
            if (list.length == 0) {
              alert('Nothing selected.');
              return;
            }
            bulkAction.action(list);
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
          button.onclick = buttonDescription.handler;
        } else {
          button = document.createElement('SPAN');
        }
        toolbar.append(button);
      });
    }
  }
};
