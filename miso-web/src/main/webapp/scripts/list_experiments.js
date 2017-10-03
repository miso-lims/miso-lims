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

ListTarget.experiment = {
  name: "Experiments",
  createUrl: function(config, projectId) {
    throw "Experiments must be provided statically";
  },
  createBulkActions: function(config, projectId) {
    if (config.inSubmission) {
      return [];
    }
    return [{
      name: "Create Submission",
      action: function(experiments) {
        window.location = window.location.origin + '/miso/submission/new?' + jQuery.param({
          experimentIds: experiments.map(Utils.array.getId).join(',')
        });
      }
    }];
  },
  createStaticActions: function(config, projectId) {
    var actions = [];
    if (config.studiesForExperiment && config.studiesForExperiment.length) {
      actions.push({
        name: "Create New",
        handler: function() {
          var showCreate;
          var creationActions = config.studiesForExperiment.map(function(request) {
            return {
              name: request.experiment.library.name + " (" + request.experiment.library.alias + ")",
              handler: function() {
                Utils.showDialog("Create Experiment", "Create", [{
                  type: 'select',
                  required: true,
                  label: "Study",
                  values: request.studies,
                  getLabel: function(study) {
                    return study.name + " (" + study.alias + ")";
                  },
                  property: "study"
                }, {
                  type: 'text',
                  required: 'true',
                  label: 'Title',
                  property: 'title'
                }, {
                  type: 'text',
                  required: 'true',
                  label: 'Alias',
                  property: 'alias'
                }],
                    function(result) {
                      request.experiment.alias = result.alias;
                      request.experiment.study = result.study;
                      request.experiment.title = result.title;

                      Utils.ajaxWithDialog("Creating to Experiment", "POST", "/miso/rest/experiment", request.experiment,
                          Utils.page.pageReload);

                    }, showCreate);
              }
            };
          });
          showCreate = function() {
            Utils.showWizardDialog("Create Experiment", creationActions);

          };
          showCreate();

        }
      });

    }

    if (config.addToExperiment && config.addToExperiment.length) {
      actions.push({
        name: "Add to Existing",
        handler: function() {

          Utils.showWizardDialog("Add to Experiment", config.addToExperiment.map(function(request) {
            return {
              name: request.partition.containerName + " " + request.partition.partitionNumber + " (" + request.partition.pool.name
                  + ") to " + request.experiment.name + " (" + request.experiment.alias + ")",
              handler: function() {
                Utils.ajaxWithDialog("Adding to Experiment", "POST", "/miso/rest/experiment/" + request.experiment.id + "/add?"
                    + jQuery.param({
                      runId: config.runId,
                      partitionId: request.partition.id
                    }), null, Utils.page.pageReload);
              }
            };
          }));
        }
      });
    }
    return actions;
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn("Name", "experiment", "id", Utils.array.getName, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", "experiment", Utils.array.getId, "alias", 0, true), {
          "sTitle": "Platform",
          "mData": "platform.instrumentModel",
          "include": true,
          "iSortPriority": 0,
        }, ListUtils.labelHyperlinkColumn("Library Name", "library", function(experiment) {
          return experiment.library.id;
        }, "library.name", 0, !config.libraryId), ListUtils.labelHyperlinkColumn("Library Alias", "library", function(experiment) {
          return experiment.library.id;
        }, "library.alias", 0, !config.libraryId), ListUtils.labelHyperlinkColumn("Study Name", "study", function(experiment) {
          return experiment.study.id;
        }, "study.name", 0, !config.studyId), ListUtils.labelHyperlinkColumn("Study Alias", "study", function(experiment) {
          return experiment.study.id;
        }, "study.alias", 0, !config.studyId), ];
  }
};
