BulkTarget = window.BulkTarget || {};
BulkTarget.librarydesign = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.libraryDesigns.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.libraryDesigns.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'library-designs');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.libraryDesigns.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [{
        title: 'Name',
        type: 'text',
        data: 'name',
        required: true,
        maxLength: 255
      }, {
        title: 'Sample Class',
        type: 'dropdown',
        data: 'sampleClassId',
        source: function(data, api) {
          return Constants.sampleClasses.filter(function(x) {
            return x.sampleCategory === 'Aliquot' && (!x.archived || data.sampleClassId === x.id);
          });
        },
        getItemLabel: Utils.array.getAlias,
        getItemValue: Utils.array.getId,
        sortSource: true,
        required: true
      }, {
        title: 'Selection',
        type: 'dropdown',
        data: 'selectionId',
        source: Constants.librarySelections,
        getItemLabel: Utils.array.getName,
        getItemValue: Utils.array.getId,
        sortSource: true,
        required: true
      }, {
        title: 'Strategy',
        type: 'dropdown',
        data: 'strategyId',
        source: Constants.libraryStrategies,
        getItemLabel: Utils.array.getName,
        getItemValue: Utils.array.getId,
        sortSource: true,
        required: true
      }, {
        title: 'DesignCode',
        type: 'dropdown',
        data: 'designCodeId',
        source: Constants.libraryDesignCodes,
        getItemLabel: Utils.array.get('code'),
        getItemValue: Utils.array.getId,
        sortSource: true,
        required: true
      }];
    }
  };

})();