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
    return [{
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
  },
  createStaticActions: function(config, prodjectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [{
      'sTitle': 'Dilution Name',
      'mData': 'id',
      'include': true,
      'iSortPriority': 1,
      'mRender': function(data, type, full) {
        return full.name;
      }
    }, {
      'sTitle': 'Conc.',
      'sType': 'natural',
      'mData': 'concentration',
      'include': true,
      'iSortPriority': 0
    }, {
      'sTitle': 'Conc. Units',
      'mData': 'concentrationUnits',
      'include': true,
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

        if (config.duplicateIndicesSequences && config.duplicateIndicesSequences.indexOf(combined) != -1) {
          html += " <span class='parsley-custom-error-message'><strong>(DUPLICATE INDEX)</strong></span>";
        } else if (config.nearDuplicateIndicesSequences && config.nearDuplicateIndicesSequences.indexOf(combined) != -1) {
          html += " <span class='parsley-custom-error-message'><strong>(NEAR-DUPLICATE INDEX)</strong></span>";
        }
        if(indices.length == 0){
          html += " <span class='parsley-custom-error-message'><strong>(NO INDEX)</strong></span>";
        }

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
    }];
  }
};
