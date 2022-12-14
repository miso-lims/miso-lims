BulkTarget = window.BulkTarget || {};
BulkTarget.containermodel = (function() {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.containerModels.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.containerModels.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'sequencing-container-models');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.containerModels.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [
        BulkUtils.columns.simpleAlias(255), {
          title: 'Barcode',
          type: 'text',
          data: 'identificationBarcode',
          maxLength: 255
        }, {
          title: 'Platform',
          type: 'dropdown',
          data: 'platformType',
          disabled: config.pageMode === 'edit',
          source: Constants.platformTypes,
          getItemLabel: Utils.array.get('key'),
          getItemValue: Utils.array.getName,
          sortSource: true,
          required: true
        }, {
          title: 'Partitions',
          type: 'int',
          data: 'partitionCount',
          required: true,
          min: 1
        },
        (function() {
          var column = BulkUtils.columns.makeBoolean('Fallback', 'fallback', true);
          column.description = 'Fallback containers do not represent actual products, and are only intended for use'
              + ' when the real container model cannot be determined.'
          return column;
        })(),
        BulkUtils.columns.archived()
      ];
    }
  };

})();
