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

ListTarget.array = {
  name: "Arrays",
  createUrl: function(config, projectId) {
    return '/miso/rest/arrays/dt';
  },
  createBulkActions: function(config, projectId) {
    return [];
  },
  createStaticActions: function(config, projectId) {
    return [{
      name: 'Add',
      handler: function() {
        window.location = '/miso/array/new';
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.idHyperlinkColumn('ID', 'array', 'id', Utils.array.getId, 0, true),
        ListUtils.idHyperlinkColumn('Alias', 'array', 'id', Utils.array.getAlias, 0, true),
        ListUtils.idHyperlinkColumn('Serial Number', 'array', 'id', function(item) {
          return item.serialNumber;
        }, 0, true), {
          sTitle: 'Last Modified',
          mData: 'lastModified',
          include: true,
          iSortPriority: 2,
          bVisible: (Sample.detailedSample ? true : false)
        }];
  }
};
