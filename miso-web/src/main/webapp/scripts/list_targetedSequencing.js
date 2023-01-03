ListTarget.targetedsequencing = {
  name: "Targeted Sequencing",
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'targeted-sequencing');
  },
  createUrl: function(config, kitDescriptorId) {
    return Urls.rest.targetedSequencings[config.add ? 'kitAvailableDatatable' : 'kitIncludedDatatable'](config.kitDescriptorId);
  },
  getQueryUrl: null,
  showNewOptionSop: true,
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
      var actions = BulkTarget.targetedsequencing.getBulkActions(config);
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
