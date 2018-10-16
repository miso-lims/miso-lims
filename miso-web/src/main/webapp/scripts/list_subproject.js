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

ListTarget.subproject = {
  name: "Subprojects",
  createUrl: function(config, projectId) {
    throw new Error("Must be provided statically");
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return HotTarget.subproject.getBulkActions(config);
  },
  createStaticActions: function(config, projectId) {
    return config.isInternal ? [{
      "name": "Add",
      "handler": function() {

        Utils.showDialog('Create Subprojects', 'Create', [{
          property: 'quantity',
          type: 'int',
          label: 'Quantity',
          value: 1
        }], function(result) {
          if (result.quantity < 1) {
            Utils.showOkDialog('Create Subprojects', ["That's a peculiar number of subprojects to create."]);
            return;
          }
          window.location = '/miso/subproject/bulk/new?' + jQuery.param({
            quantity: result.quantity,
          });
        });
      }
    }] : [];
  },
  createColumns: function(config, projectId) {
    return [
        {
          "sTitle": "Alias",
          "mData": "alias",
          "include": true,
          "iSortPriority": 1
        },
        {
          "sTitle": "Project",
          "mData": "parentProjectId",
          "include": true,
          "iSortPriority": 0,
          "mRender": function(data, type, full) {
            var projectAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(data), config.projects),
                'alias')
                || "Unknown";
            if (type === 'display') {
              return "<a href=\"/miso/project/" + data + "\">" + projectAlias + "</a>";
            } else {
              return projectAlias;
            }
          }
        }, {
          "sTitle": "Priority",
          "mData": "priority",
          "include": true,
          "iSortPriority": 0,
          "mRender": ListUtils.render.booleanChecks
        }, {
          "sTitle": "Reference Genome",
          "mData": "referenceGenomeId",
          "include": true,
          "iSortPriority": 0,
          "mRender": ListUtils.render.textFromId(Constants.referenceGenomes, 'alias')
        }, ];
  }
};

var Subproject = Subproject || {
  filterSamples: function(samplesArrowClickId, samplesTableId, subprojectAlias) {
    var expandableSection = jQuery('#' + samplesTableId).closest('.expandable_section');
    if (expandableSection.is(':hidden')) {
      // make it visible
      jQuery('#' + samplesArrowClickId).closest('.sectionDivider').click();
    }
    Utils.ui.filterTable(samplesTableId, 'subproject', subprojectAlias);
    expandableSection[0].scrollIntoView();
  }
};
