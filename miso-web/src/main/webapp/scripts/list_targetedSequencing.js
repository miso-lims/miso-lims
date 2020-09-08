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
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'targeted-sequencing');
  },
  createUrl: function(config, kitDescriptorId) {
    return Urls.rest.targetedSequencings[config.add ? 'kitAvailableDatatable' : 'kitIncludedDatatable'](config.kitDescriptorId);
  },
  getQueryUrl: null,
  createBulkActions: function(config) {
    if (config.kitDescriptorId) {
      return !config.isAdmin ? [] : [{
        'name': config.add ? 'Associate' : 'Dissociate',
        'action': function(tarseqs) {
          var doAction = function() {
            var data = {};
            data[(config.add ? 'add' : 'remove')] = tarseqs.map(Utils.array.getId);
            data[(config.add ? 'remove' : 'add')] = [];
            Utils.ajaxWithDialog('Changing associated Targeted Sequencing panels', 'PUT', Urls.rest.kitDescriptors
                .updateTargetedSequencings(config.kitDescriptorId), data, Utils.page.pageReload);
          };
          doAction();
        }
      }];
    } else {
      var actions = HotTarget.targetedsequencing.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(ListUtils.createBulkDeleteAction('Targeted Sequencings', 'targetedsequencings', Utils.array.getAlias));
      }
      return actions;
    }
  },
  createStaticActions: function(config) {
    return config.isAdmin ? [ListUtils.createStaticAddAction('Targeted Sequencings', Urls.ui.targetedSequencings.bulkCreate, true)] : [];
  },
  createColumns: function(config) {
    return [{
      sTitle: 'Alias',
      mData: 'alias',
      include: true,
      iSortPriority: 1
    }, {
      sTitle: 'Description',
      mData: 'description',
      include: true,
      iSortPriority: 0
    }, {
      sTitle: 'Archived',
      mData: 'archived',
      mRender: function(data, type, full) {
        return !!data ? 'Yes' : 'No';
      },
      include: true,
      iSortPriority: 0
    }];
  }
}
