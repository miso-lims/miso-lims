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

ListTarget.poolelement = {
  name: 'Dilutions',
  createUrl: function(config, projectId) {
    return '/miso/rest/librarydilution/dt/pool/' + config.poolId + '/' + (config.add ? 'available' : 'included');
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = [{
      'name': config.add ? 'Add' : 'Remove',
      'action': function(elements) {
        var doAction = function() {
          var data = {};
          data[(config.add ? 'add' : 'remove')] = elements.map(Utils.array.getId);
          data[(config.add ? 'remove' : 'add')] = [];
          Utils.ajaxWithDialog('Changing pool', 'PUT', '/miso/rest/pool/' + config.poolId + '/contents', data, Utils.page.pageReload);
        };
        if (config.add) {
          HotUtils.warnIfConsentRevoked(elements, doAction, HotTarget.dilution.getLabel);
        } else {
          doAction();
        }
      }
    }];
    if (!config.add) {
      actions.push({
        name: 'Edit Proportions',
        action: function(elements) {
          var fields = [];
          elements.forEach(function(element) {
            fields.push({
              type: 'int',
              label: element.name + ' (' + element.library.alias + ')',
              value: element.proportion,
              property: element.name,
              required: true
            });
          });
          Utils.showDialog('Edit Proportions', 'OK', fields, function(output) {
            Utils.ajaxWithDialog('Setting Proportions', 'PUT', '/miso/rest/pool/' + config.poolId + '/proportions', output,
                Utils.page.pageReload);
          });
        }
      });
    }
    return actions;
  },
  createStaticActions: function(config, prodjectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [{
      'sTitle': 'Dilution Name',
      'mData': 'name',
      'include': true,
      'iSortPriority': 1
    }, {
      "sTitle": "Warnings",
      "mData": null,
      "mRender": WarningTarget.poolelement.tableWarnings(config.duplicateIndicesSequences, config.nearDuplicateIndicesSequences),
      "include": true,
      "iSortPriority": 0,
      "bVisible": true,
      "bSortable": false
    }, {
      'sTitle': 'Proportion',
      'sType': 'numeric',
      'mData': 'proportion',
      'include': !config.add,
      'iSortPriority': 0
    }, ListUtils.idHyperlinkColumn("Library Name", "library", "library.id", function(dilution) {
      return dilution.library.name;
    }, 0, true, "noPrint"), ListUtils.labelHyperlinkColumn("Library Alias", "library", function(dilution) {
      return dilution.library.id;
    }, "library.alias", 0, true), ListUtils.idHyperlinkColumn("Sample Name", "sample", "library.parentSampleId", function(dilution) {
      return "SAM" + dilution.library.parentSampleId;
    }, 0, true, "noPrint"), ListUtils.labelHyperlinkColumn("Sample Alias", "sample", function(dilution) {
      return dilution.library.parentSampleId;
    }, "library.parentSampleAlias", 0, true, "noPrint"), {
      'sTitle': 'Conc.',
      'sType': 'numeric',
      'mData': 'concentration',
      'include': true,
      'iSortPriority': 0,
      'mRender': function(data, type, full) {
        if (type === 'display' && !!data) {
          var units = Constants.concentrationUnits.find(function(unit) {
            return unit.name == full.concentrationUnits;
          });
          if (!!units) {
            return data + ' ' + units.units;
          }
        }
        return data;
      }
    }, {
      "sTitle": "Targeted Sequencing",
      "mData": "targetedSequencingId",
      "include": Constants.isDetailedSample,
      "mRender": ListUtils.render.textFromId(Constants.targetedSequencings, 'alias', '(None)'),
      "iSortPriority": 0,
      "bSortable": false
    }, {
      'sTitle': 'Indices',
      'mData': 'indexIds',
      'include': true,
      'bSortable': false,
      'iSortPriority': 0,
      'mRender': function(data, type, full) {
        var indices = Constants.indexFamilies.reduce(function(acc, family) {
          return acc.concat(family.indices.filter(function(index) {
            return data.indexOf(index.id) != -1;
          }));
        }, []).sort(function(a, b) {
          return a.position - b.position;
        });

        var combined = indices.map(function(index) {
          return index.sequence;
        }).join('');

        var html = indices.map(function(index) {
          return index.label;
        }).join(', ');

        return html;
      }
    }, {
      'sTitle': 'Last Modified',
      'mData': 'lastModified',
      'include': true,
      'iSortPriority': 0,
      'sClass': 'noPrint'
    }, {
      'sTitle': 'Low Quality',
      'bSortable': false,
      'mData': 'library.lowQuality',
      'mRender': function(data, type, full) {
        return data ? "⚠" : "";
      },
      'include': true,
      'iSortPriority': 0
    }, {
      "sTitle": "QC Passed",
      "mData": "library.qcPassed",
      "include": true,
      "iSortPriority": 0,
      "mRender": ListUtils.render.booleanChecks,
      "bSortable": false
    }];
  }
};
