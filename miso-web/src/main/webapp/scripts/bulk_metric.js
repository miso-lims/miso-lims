BulkTarget = window.BulkTarget || {};
BulkTarget.metric = (function() {

  /*
   * Expected config: {
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.metrics.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.metrics.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'metrics');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.metrics.bulkEdit)];
    },
    getColumns: function(config, api) {
      return [BulkUtils.columns.simpleAlias(100), {
        title: 'Category',
        type: 'dropdown',
        data: 'category',
        source: Constants.metricCategories,
        getItemLabel: Utils.array.get('label'),
        getItemValue: Utils.array.get('value'),
        required: true
      }, {
        title: 'Threshold Type',
        type: 'dropdown',
        data: 'thresholdType',
        source: Constants.thresholdTypes,
        getItemLabel: function(item) {
          return Utils.decodeHtmlString(item.sign);
        },
        getItemValue: Utils.array.get('value'),
        required: true
      }, {
        title: 'Units',
        type: 'text',
        data: 'units'
      }];
    }
  };

})();