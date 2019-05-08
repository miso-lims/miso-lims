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

ListTarget.targetedsequencing = {
  name: "Targeted Sequencing",
  createUrl: function(config, kitDescriptorId) {
    return '/miso/rest/targetedsequencings/dt/kit/' + config.kitDescriptorId + '/' + (config.add ? 'available' : 'included');
  },
  queryUrl: null,
  createBulkActions: function(config) {
    var actions = [{
      'name': config.add ? 'Add' : 'Remove',
      'action': function(tarseqs) {
        var doAction = function() {
          var data = {};
          data[(config.add ? 'add' : 'remove')] = tarseqs.map(Utils.array.getId);
          data[(config.add ? 'remove' : 'add')] = [];
          Utils.ajaxWithDialog('Changing associated Targeted Sequencing panels', 'PUT', '/miso/rest/kitdescriptors/'
              + config.kitDescriptorId + '/targetedsequencing', data, Utils.page.pageReload);
        };
        doAction();
      }
    }];
    return actions;
  },
  createStaticActions: function(config) {
    return [];
  },
  createColumns: function(config) {
    return [{
      'sTitle': 'Alias',
      'mData': 'alias',
      'include': true,
      'iSortPriority': 1
    }, {
      'sTitle': 'Archived',
      'mData': 'archived',
      'mRender': function(data, type, full) {
        return !!data ? 'Yes' : 'No';
      },
      'include': true,
      'iSortPriority': 0
    }];
  }
}