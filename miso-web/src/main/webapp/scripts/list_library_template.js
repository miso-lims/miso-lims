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

ListTarget.library_template = {
  name: "Library Templates",
  getUserManualUrl: function() {
    return Urls.external.userManual('libraries', 'library-templates');
  },
  createUrl: function(config, projectId) {
    var namespace = Urls.rest.libraryTemplates;
    return projectId ? namespace.projectDatatable(projectId) : namespace.datatable;
  },
  getQueryUrl: function() {
    return Urls.rest.libraryTemplates.query;
  },
  createBulkActions: function(config, projectId) {
    var actions = HotTarget.libraryTemplate.getBulkActions(config);
    actions.push({
      name: "Delete",
      action: function(items) {
        var lines = ['Are you sure you wish to delete the following library templates? This cannot be undone.'];
        var ids = [];
        jQuery.each(items, function(index, librarytemplate) {
          lines.push('* ' + librarytemplate.alias);
          ids.push(librarytemplate.id);
        });
        Utils.showConfirmDialog('Delete Library Templates', 'Delete', lines, function() {
          Utils.ajaxWithDialog('Deleting Library Templates', 'POST', Urls.rest.libraryTemplates.bulkDelete, ids, Utils.page.pageReload);
        });
      }
    });

    if (!projectId) {
      actions.push({
        name: "Add Project",
        action: function(items) {
          Utils.showDialog('Search for Project to Add', 'Search', [{
            type: "text",
            label: "Search",
            property: "query",
            value: ""
          }, ], function(results) {
            Utils.ajaxWithDialog('Getting Projects', 'GET', Urls.rest.projects.search + '?' + jQuery.param({
              q: results.query
            }), null, function(response) {
              var projectActions = [];
              response.forEach(function(project) {
                projectActions.push({
                  name: project.name + ': ' + project.alias + (project.shortName ? ' (' + project.shortName + ')' : ''),
                  handler: function() {
                    var templateIds = items.map(function(template) {
                      return template.id;
                    });
                    Utils.ajaxWithDialog("Adding Library Template" + (items.length > 1 ? "s" : "") + " to Project", "POST",
                        Urls.rest.libraryTemplates.addProject + "?" + jQuery.param({
                          projectId: project.id,
                        }), templateIds, function() {
                          Utils.showOkDialog("Add Project", ["Successfully added Library Template" + (items.length > 1 ? "s" : "")
                              + " to Project " + project.alias], Utils.page.pageReload);
                        });
                  }
                });
              });
              Utils.showWizardDialog("Add Project", projectActions);
            });
          });
        }
      });

      actions.push({
        name: "Remove Project",
        action: function(items) {
          var projectActions = [];
          var projectIds = {};
          items.forEach(function(template) {
            template.projectIds.forEach(function(id) {
              if (!projectIds.hasOwnProperty(id)) {
                projectIds[id] = true;
                jQuery.ajax({
                  url: Urls.rest.projects.get(id),
                  type: 'GET',
                  contentType: 'application/json; charset=utf8',
                }).success(
                    function(project) {
                      projectActions.push({
                        name: project.alias,
                        handler: function() {
                          var templateIds = items.map(function(item) {
                            return item.id;
                          });
                          Utils.ajaxWithDialog("Removing Library Template " + (items.length > 1 ? "s" : "") + " from Project", "POST",
                              Urls.rest.libraryTemplates.removeProject + "?" + jQuery.param({
                                projectId: id
                              }), templateIds, function() {
                                Utils.showOkDialog("Add Project", ["Successfully removed Library Template" + (items.length > 1 ? "s" : "")
                                    + " from Project " + project.alias], Utils.page.pageReload);
                              });
                        }
                      });
                      Utils.showWizardDialog("Remove Project", projectActions);
                    });
              }
            });
          });
        }
      });
    }

    return actions;
  },
  createStaticActions: function(config, projectId) {
    return [{
      name: "Add",
      handler: function() {
        window.location = Urls.ui.libraryTemplates.create + (projectId ? '?' + jQuery.param({
          projectId: projectId
        }) : '');
      }
    }];
  },
  createColumns: function(config, projectId) {

    var stringIdPredicate = function(id) {
      return function(item) {
        return !Utils.validation.isEmpty(id) && item.id == id;
      };
    }

    return [
        ListUtils.labelHyperlinkColumn('Alias', Urls.ui.libraryTemplates.edit, Utils.array.getId, 'alias', 1, true),
        {
          "sTitle": "Library Design",
          "mData": "designId",
          "include": Constants.isDetailedSample,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.libraryDesigns), 'name')
                || '';
          },
          "bSortable": false
        },
        {
          "sTitle": "Design Code",
          "mData": "designCodeId",
          "include": Constants.isDetailedSample,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.libraryDesignCodes), 'code')
                || '';
          },
          "bSortable": false
        },
        {
          "sTitle": "Library Type",
          "mData": "libraryTypeId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            return Utils.array
                .maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.libraryTypes), 'description')
                || '';
          },
          "bSortable": false
        },
        {
          "sTitle": "Selection",
          "mData": "selectionId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.librarySelections), 'name')
                || '';
          },
          "bSortable": false
        },
        {
          "sTitle": "Strategy",
          "mData": "strategyId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.libraryStrategies), 'name')
                || '';
          },
          "bSortable": false
        },
        {
          "sTitle": "Kit Name",
          "mData": "kitDescriptorId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.kitDescriptors), 'name')
                || '';
          },
          "bSortable": false
        },
        {
          "sTitle": "Index Family",
          "mData": "indexFamilyId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(stringIdPredicate(data), Constants.indexFamilies), 'name')
                || '';
          },
          "bSortable": false
        },
        {
          "sTitle": "Platform",
          "mData": "platformType",
          "include": true,
          "iSortPriority": 0,
          "bSortable": false,
          "mRender": function(data, type, full) {
            return data || '';
          }
        },
        {
          "sTitle": "Default Volume",
          "mData": "defaultVolume",
          "include": true,
          "iSortPriority": 0,
          "bSortable": false,
          "mRender": function(data, type, full) {
            return (data || '')
                + ' '
                + (full.volumeUnits
                    ? Utils.array.findUniqueOrThrow(Utils.array.namePredicate(full.volumeUnits), Constants.volumeUnits).units : '');
          }
        }];
  }
};
