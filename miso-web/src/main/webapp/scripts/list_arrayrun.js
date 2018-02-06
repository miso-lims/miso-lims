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

ListTarget.arrayrun = {
  name: 'Array Runs',
  createUrl: function(config, projectId) {
    return '/miso/rest/arrayruns/dt' + (projectId ? '/project/' + projectId : '');
  },
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return [{
      name: 'Add',
      handler: function() {
        window.location = '/miso/arrayrun/new';
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn('ID', 'arrayrun', 'id', Utils.array.getId, 0, true),
        ListUtils.idHyperlinkColumn('Alias', 'arrayrun', 'id', Utils.array.getAlias, 0, true), {
          sTitle: 'Status',
          mData: 'status',
          mRender: function(data, type, full) {
            return data || '';
          },
          include: true,
          iSortPriority: 0
        }, {
          sTitle: 'Start Date',
          mData: 'startDate',
          mRender: function(data, type, full) {
            return data || '';
          },
          include: true,
          iSortPriority: 2
        }, {
          sTitle: 'End Date',
          mData: 'completionDate',
          mRender: function(data, type, full) {
            return data || '';
          },
          include: true,
          iSortPriority: 0
        }, {
          sTitle: 'Last Modified',
          mData: 'lastModified',
          include: Constants.isDetailedSample,
          iSortPriority: 0
        }];
  }
};
