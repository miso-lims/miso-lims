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

ListTarget.project = {
  name: "Projects",
  getUserManualUrl: function() {
    return Urls.external.userManual('projects');
  },
  createUrl: function(config, projectId) {
    throw new Error("Static display only");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    if (config.forLibraryTemplate) {
      return [{
        name: 'Remove',
        action: function(items) {
          LibraryTemplate.removeProjects(items);
        }
      }];
    } else {
      return [ListUtils.createBulkDeleteAction("Projects", "projects", function(project) {
        if (!project.alias) {
          return project.shortName;
        } else if (!project.shortName) {
          return project.alias;
        } else {
          return project.alias + ' (' + project.shortName + ")";
        }
      })];
    }
  },
  createStaticActions: function(config, projectId) {
    if (config.forLibraryTemplate) {
      return [{
        name: 'Add',
        handler: function() {
          Utils.showDialog('Search for Project to Add', 'Search', [{
            type: "text",
            label: "Search",
            property: "query",
            value: ""
          }], function(results) {
            Utils.ajaxWithDialog('Getting Projects', 'GET', '/miso/rest/projects/search?' + jQuery.param({
              q: results.query
            }), null, function(response) {
              Utils.showWizardDialog("Add Project", response.map(function(project) {
                return {
                  name: project.name + ': ' + project.alias + (project.shortName ? ' (' + project.shortName + ')' : ''),
                  handler: function() {
                    LibraryTemplate.addProject(project);
                  }
                };
              }));
            });
          });
        }
      }];
    } else {
      return [{
        name: "Add",
        handler: function() {
          window.location.href = Urls.ui.projects.create;
        }
      }];
    }
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'ID',
      mData: 'id',
      bVisible: false
    }, {
      sTitle: 'Name',
      mData: 'name',
      iSortPriority: 0,
      iDatSort: 0, // Use ID for sorting,
      mRender: Warning.tableWarningRenderer(WarningTarget.project, function(project) {
        return Urls.ui.projects.edit(project.id);
      }),
      sClass: 'nowrap'
    }, ListUtils.labelHyperlinkColumn('Alias', Urls.ui.projects.edit, Utils.array.getId, 'alias', 1, true),
        ListUtils.labelHyperlinkColumn('Short Name', Urls.ui.projects.edit, Utils.array.getId, 'shortName', 0, true), {
          sTitle: 'Description',
          mData: 'description',
          include: true,
          iSortPriority: 0
        }, {
          sTitle: 'Status',
          mData: 'status',
          include: true,
          iSortPriority: 0
        }];
  }
};
