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
  name: 'Library Aliquots',
  createUrl: function(config, projectId) {
    return config.add ? Urls.rest.libraryAliquots.availableForPoolDatatable(config.poolId) : Urls.rest.libraryAliquots
        .includedInPoolDatatable(config.poolId);
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    var actions = [{
      'name': config.add ? 'Add' : 'Remove',
      'action': function(elements) {
        var doAction = function() {
          var data = {};
          data[(config.add ? 'add' : 'remove')] = elements.map(Utils.array.getId);
          data[(config.add ? 'remove' : 'add')] = [];
          Utils.ajaxWithDialog('Changing pool', 'PUT', '/miso/rest/pools/' + config.poolId + '/contents', data, Utils.page.pageReload);
        };
        if (config.add) {
          HotUtils.warnIfConsentRevoked(elements, doAction, HotTarget.libraryaliquot.getLabel);
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
              label: element.name + ' (' + element.alias + ')',
              value: element.proportion,
              property: element.name,
              required: true
            });
          });
          Utils.showDialog('Edit Proportions', 'OK', fields, function(output) {
            Utils.ajaxWithDialog('Setting Proportions', 'PUT', '/miso/rest/pools/' + config.poolId + '/proportions', output,
                Utils.page.pageReload);
          });
        }
      });
    }
    return actions;
  },
  createStaticActions: function(config, projectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [
        {
          sTitle: 'Library Aliquot Name',
          mData: 'id', // for sorting purposes
          include: true,
          iSortPriority: 1,
          mRender: function(data, type, full) {
            if (type === 'display') {
              return '<a href="' + Urls.ui.libraryAliquots.edit(full.id) + '">' + full.name + '</a>';
            }
            return data;
          }
        },
        {
          sTitle: 'Library Aliquot Alias',
          mData: 'alias',
          include: true,
          iSortPriority: 0
        },
        {
          sTitle: "Warnings",
          mData: null,
          mRender: Warning.tableWarningRenderer(WarningTarget.poolelement.makeTarget(config.duplicateIndicesSequences,
              config.nearDuplicateIndicesSequences)),
          include: true,
          iSortPriority: 0,
          bVisible: true,
          bSortable: false
        }, {
          sTitle: 'Proportion',
          sType: 'numeric',
          mData: 'proportion',
          include: !config.add,
          iSortPriority: 0
        }, ListUtils.idHyperlinkColumn("Library Name", Urls.ui.libraries.edit, "libraryId", function(aliquot) {
          return aliquot.libraryName;
        }, 0, true, "noPrint"), ListUtils.labelHyperlinkColumn("Library Alias", Urls.ui.libraries.edit, function(aliquot) {
          return aliquot.libraryId;
        }, "libraryAlias", 0, true), ListUtils.idHyperlinkColumn("Sample Name", Urls.ui.samples.edit, "sampleId", function(aliquot) {
          return aliquot.sampleName;
        }, 0, true, "noPrint"), ListUtils.labelHyperlinkColumn("Sample Alias", Urls.ui.samples.edit, function(aliquot) {
          return aliquot.sampleId;
        }, "sampleAlias", 0, true, "noPrint"), {
          sTitle: 'Conc.',
          sType: 'numeric',
          mData: 'concentration',
          include: true,
          iSortPriority: 0,
          mRender: function(data, type, full) {
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
          sTitle: "Targeted Sequencing",
          mData: "targetedSequencingId",
          include: Constants.isDetailedSample,
          mRender: ListUtils.render.textFromId(Constants.targetedSequencings, 'alias', '(None)'),
          iSortPriority: 0,
          bSortable: false
        }, {
          sTitle: 'Indices',
          mData: 'indexIds',
          include: true,
          bSortable: false,
          iSortPriority: 0,
          mRender: function(data, type, full) {
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
          sTitle: 'Last Modified',
          mData: 'lastModified',
          include: true,
          iSortPriority: 0,
          sClass: 'noPrint'
        }, {
          sTitle: "QC Passed",
          mData: "libraryQcPassed",
          include: true,
          iSortPriority: 0,
          mRender: ListUtils.render.booleanChecks,
          bSortable: false
        }];
  }
};
