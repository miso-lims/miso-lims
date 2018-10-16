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

ListTarget.servicerecord = {
  name: "Service Records",
  createUrl: function(config, projectId) {
    throw new Error("Service records must be specified statically.");
  },
  createBulkActions: function(config, projectId) {
    return config.userIsAdmin ? [{
      name: 'Delete',
      action: function(items) {
        var lines = ['Are you sure you wish to delete the following service records? This cannot be undone.'];
        var ids = [];
        jQuery.each(items, function(index, record) {
          lines.push('* ' + record.title + (record.referenceNumber ? ' (' + record.referenceNumber + ')' : ''));
          ids.push(record.id);
        });
        Utils.showConfirmDialog('Delete Service Records', 'Delete', lines, function() {
          Utils.ajaxWithDialog('Deleting Service Records', 'POST', '/miso/rest/servicerecords/bulk-delete', ids, function() {
            Utils.page.pageReload();
          });
        });
      }
    }] : [];
  },
  createStaticActions: function(config, projectId) {
    return config.retiredInstrument ? [] : [{
      name: 'Add',
      handler: function() {
        window.location = '/miso/instrument/servicerecord/new/' + config.instrumentId;
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [{
      sTitle: 'Service Date',
      mData: 'serviceDate',
      include: true,
      iSortPriority: 1
    }, ListUtils.labelHyperlinkColumn('Title', 'instrument/servicerecord', Utils.array.getId, function(record) {
      return record.title;
    }, 0, true), {
      sTitle: 'Reference Number',
      mData: 'referenceNumber',
      include: true,
      iSortPriority: 0,
      mRender: function(data, type, full) {
        return data || '';
      }
    }, {
      sTitle: 'Files',
      mData: null,
      include: true,
      iSortPriority: 0,
      mRender: function(data, type, full) {
        if (type === 'display' && full.attachments && full.attachments.length) {
          var list = '<ul class="unformatted-list">';
          full.attachments.forEach(function(file) {
            list += '<li><a href="/miso/attachments/servicerecord/' + full.id + '/' + file.id + '">' + file.filename + '</a></li>';
          })
          list += '</ul>';
          return list;
        }
        return data;
      }
    }, {
      sTitle: 'Details',
      mData: 'details',
      include: true,
      bVisible: false,
      iSortPriority: 0
    }];
  }
};
